/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 * 	Rev	1.3 	Mar 07, 2019		Purushotham Reddy	Changes for New Discount Rule Promo-CR
 *
 *	Rev 1.2		Apr 14, 2017		Mansi Goel			Changes to resolve amount based tiered
 *														rule is not applied properly
 *	Rev 1.1		Apr	10, 2017		Mansi Goel			Changes to resolve amount based tiered 
 *														rules does not apply after required amount is satisfied
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel			Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import max.retail.stores.domain.MAXUtils.MAXUtils;
import max.retail.stores.domain.comparators.MAXComparators;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.domain.discount.DiscountItemIfc;
import oracle.retail.stores.domain.discount.DiscountListEntry;
import oracle.retail.stores.domain.discount.DiscountListEntryIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.SourceCriteria;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

public class MAXSourceCriteria extends SourceCriteria implements MAXDiscountRuleConstantsIfc, MAXDiscountListIfc {

	private static final long serialVersionUID = -5032904285017035602L;

	public boolean evaluate(ArrayList candidates, ArrayList selected, boolean sourcesAreTargets) {
		boolean value = false;

		switch (thresholdType) {
		case THRESHOLD_QUANTITY: {
			// see if all quantity thresholds have been achieved
			if (ruleReasonCode == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNofXatZPctoffTiered)// gaurav
				value = evaluateQuantitiesByTieredRule(candidates, selected);
			else
				value = evaluateQuantities(candidates, selected);
			break;
		}
		case THRESHOLD_AMOUNT: {
			// see if all amount thresholds have been achieved
			value = evaluateAmounts(candidates, selected);
			break;
		}
		default: {
			throw new IllegalStateException("Invalid thresholdTypeCode loaded for pricing rule ");
		}
		}
		return value;
	}

	public Object clone() {
		MAXSourceCriteria newList = new MAXSourceCriteria();
		setCloneAttributes(newList);
		return newList;
	}

	/**
	 * Sorts sources based on the reason code of the rule.
	 * <p>
	 * BuyNofXgetHighestPricedXatZ%off, Buy$NorMoreofXGetYatZ$off,
	 * Buy$NorMoreofXGetYatZ%off, Buy$NorMoreofXGetYatZ$, BuyNofXforZ$ and
	 * BuyNofXforZ%off should have the sources sorted by price descending.
	 * <p>
	 * BuyNofXgetLowestPricedXatZ%off should be sort price ascending.
	 * 
	 * @param entries
	 *            the {@link DiscountListEntry}s to sort.
	 */
	protected void sortAnyEntries(List entries) {
		switch (getRuleReasonCode()) {
		// Can't predict best grouping for something of this nature
		// Selecting lowest priced items might cause some prices to go negative.
		// Selecting highest priced items might prevent an item from being used
		// for another better rule.
		case DISCOUNT_REASON_BuyNofXforZ$off:
			break;
		// Percent Off of a source that's a target - we definitely want the
		// highest priced
		// items included.
		case DISCOUNT_REASON_BuyNofXforZPctoff:
		case DISCOUNT_REASON_BuyNofXforZ$:
		case DISCOUNT_REASON_BuyNofXgetHighestPricedXatZPctoff:
			Collections.sort(entries, MAXComparators.discountListEntryDescending);
			break;
		// We have to have the lowest priced item in the group to satisfy this
		// rule
		case DISCOUNT_REASON_BuyNofXgetLowestPricedXatZPctoff:
			Collections.sort(entries, MAXComparators.discountListEntryAscending);
			break;
		// Other scenarios depend on whether sources are targets
		case DISCOUNT_REASON_BuyNofXgetYatZPctoff:
		case DISCOUNT_REASON_BuyNofXgetYatZ$:
		case DISCOUNT_REASON_Buy$NorMoreOfXgetYatZPctoff:
		case DISCOUNT_REASON_Buy$NorMoreOfXgetYatZ$:
		case DISCOUNT_REASON_Buy$NofXatZPctoffTiered:
		case DISCOUNT_REASON_Buy$NofXatZ$offTiered:
			if (sourcesAreTargets) {
				Collections.sort(entries, MAXComparators.discountListEntryDescending);
			} else {
				Collections.sort(entries, MAXComparators.discountListEntryAscending);
			}
			break;
		default:
			if (!sourcesAreTargets) {
				Collections.sort(entries, MAXComparators.discountListEntryAscending);
			}
		}
	}

	protected boolean evaluateQuantities(ArrayList candidates, ArrayList selected, boolean allEligibleSources,
			boolean reEvaluateSources) {
		// reset previously counted quantities
		resetQuantities();
		selected.clear();

		String criterion = null;
		DiscountSourceIfc candidate = null;
		boolean criteriaSatisfied = false;

		if (getRuleReasonCode() == 1 && getComparisonBasis() == MAXDiscountRuleConstantsIfc.COMPARISON_BASIS_ITEM_GROUP) {
			Collections.sort(candidates, MAXComparators.lineItemPriceAscending);
		}

		// check to see if a source matches the criterion
		for (Iterator<DiscountItemIfc> candidateIter = candidates.iterator(); candidateIter.hasNext();) {
			candidate = (DiscountSourceIfc) candidateIter.next();
			// for each String in the criteria list
			for (Iterator<String> p = criteria(); p.hasNext();)

			{
				criterion = p.next();
				// Changes for Rev 1.0 : Starts
				if (attributesEqual(criterion, (MAXSaleReturnLineItem) candidate)
						&& itemIdIsAcceptable(criterion, selected, candidate)) {
					// Changes for Rev 1.0 : Ends
					// if match is found, increment the quantity counted,
					// add item to the selected bucket and remove from
					// candidates
					if (quantitySatisfied(criterion)) {
						if (getDescription().equals(
								DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZPctoff)
								|| getDescription().equals(
										DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each)) {
							// if all is required, add now. We determine "any"
							// selections later
							if (isAllRequired()) {
								selected.add(candidate);
								// selected candidates are no longer candidates
								candidateIter.remove();
							}
							incrementQuantity(criterion, candidate);
							List<DiscountListEntry> entries = new ArrayList<DiscountListEntry>();
							// add qualified dles to the list
							for (Iterator i = map.values().iterator(); i.hasNext();) {
								DiscountListEntry dle = (DiscountListEntry) i.next();
								if (dle.quantitySatisfied()) {
									entries.add(dle);
								}
							}
							sortAnyEntries(entries);
						} else if (allEligibleSources) {
							if (isAllRequired()) {
								selected.add(candidate);
								candidateIter.remove(); // selected candidates
														// are no longer
														// candidates
							}
							incrementQuantity(criterion, candidate);
						}
						// If evaluating for the Any Qty against ANY source
						// criteria.
						else if (reEvaluateSources && quantitySatisfied(criterion) && !altQuantitySatisfied(criterion)) {
							incrementAltQuantity(criterion, candidate);
						} else {
							break;
						}
					} else if (!quantitySatisfied(criterion) || allEligibleSources) {
						// if all is required, add now. We determine "any"
						// selections later
						if (isAllRequired()) {
							selected.add(candidate);
							candidateIter.remove(); // selected candidates are
													// no longer candidates

							incrementQuantity(criterion, candidate);
						}

						// increment quantity only if ANY criteria is not
						// satisfied
						else if (!anySatisfied() || isAnyComboAcceptable()) {
							incrementQuantity(criterion, candidate);
						}
					} else {
						if (MAXUtils.isTieredDiscount(getRuleReasonCode()) && selected.size() > 0) {
							selected.add(candidate);
							candidateIter.remove();
							incrementQuantity(criterion, candidate);
						}
					}
				}// end if
			}// end for
		}// end for

		if (isAnyComboAcceptable() && isMultiThreshold()) {
			criteriaSatisfied = evaluateAnyComboQuantitySatisfied(candidates, selected);
		} else {
			criteriaSatisfied = (!isAllRequired()) ? (MAXUtils.isTieredDiscount(getRuleReasonCode())) ? evaluateAnyTieredSatisfied(
					candidates, selected) : evaluateAnySatisfied(candidates, selected)
					: allSatisfied();
		}
		if (criteriaSatisfied) {
			checkSourcesForManualDiscounts(selected, candidates);
		}

		return criteriaSatisfied;
	}

	protected boolean evaluateQuantitiesByTieredRule(ArrayList<DiscountItemIfc> candidates,
			ArrayList<DiscountItemIfc> selected) {

		resetQuantities();
		selected.clear();

		String criterion = null;
		DiscountSourceIfc candidate = null;

		for (Iterator<String> p = criteria(); p.hasNext();) {
			criterion = p.next();

			for (Iterator<DiscountItemIfc> candidateIter = candidates.iterator(); candidateIter.hasNext();) {
				candidate = (DiscountSourceIfc) candidateIter.next();

				if (comparisonBasis == 6) {
					if (attributesEqual(this.map, (MAXSaleReturnLineItem) candidate)
							&& itemIdIsAcceptable(criterion, selected, candidate)) {
						if (MAXUtils.isTieredDiscount(getRuleReasonCode())) {
							selected.add(candidate);
							candidateIter.remove();
							incrementQuantity(criterion, candidate);
						} else {
							break;
						}

					}
				} else {
					if (attributesEqual(criterion, (MAXSaleReturnLineItem) candidate)
							&& itemIdIsAcceptable(criterion, selected, candidate)) {

						if (MAXUtils.isTieredDiscount(getRuleReasonCode())) {
							selected.add(candidate);
							candidateIter.remove();
							incrementQuantity(criterion, candidate);

						} else {
							break;
						}

					}

				}// end if
			}// end for
		}// end for

		return (anyQuantity > 0) ? (MAXUtils.isTieredDiscount(getRuleReasonCode())) ? evaluateAnyTieredSatisfied(
				candidates, selected) : evaluateAnySatisfied(candidates, selected) : allSatisfied();
	}

	public boolean attributesEqual(Map map, MAXSaleReturnLineItem item) {
		boolean flag = false;
		String criterion = null;
		for (Iterator<String> p = criteria(); p.hasNext();) {
			criterion = (String) p.next();
			HashMap<String, String> map1 = new HashMap<String, String>();
			map1.put(COMPARISION_BASIS, String.valueOf(comparisonBasis));
			map1.put(CRITERION, criterion);

			flag = criterion.equals(item.getComparator(map1));
			if (flag)
				break;
		}
		return flag;
	}

	protected boolean evaluateAnyTieredSatisfied(List<DiscountItemIfc> candidates, List<DiscountItemIfc> selected) {
		boolean anySatisfied = anySatisfied();
		if (anySatisfied) {
			List<DiscountItemIfc> tempSelected = pickAnyTieredEntries();
			candidates.removeAll(tempSelected);
			if (!(this.ruleReasonCode == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNofXatZPctoffTiered))
				selected.addAll(tempSelected);
		}
		return anySatisfied;
	}

	protected List<DiscountItemIfc> pickAnyTieredEntries() {
		List<DiscountListEntry> entries = new ArrayList<DiscountListEntry>();
		for (Iterator i = map.values().iterator(); i.hasNext();) {
			MAXDiscountListEntry dle = (MAXDiscountListEntry) i.next();
			if (dle.quantitySatisfied()) {
				entries.add(dle);
			} else {
				entries.add(dle); // Even when quantity is not Satisfied
			}
		}
		// if too many, sort by most expensive
		if (entries.size() > anyQuantity) {
			sortAnyEntries(entries);
		}
		// add any qty to selected
		List<DiscountItemIfc> anySelected = new ArrayList<DiscountItemIfc>();
		for (int i = 0; i < entries.size(); i++) {
			MAXDiscountListEntry dle = (MAXDiscountListEntry) entries.get(i);
			anySelected.addAll(dle.discountItems);
		}
		return anySelected;
	}

	public boolean uses(MAXSaleReturnLineItem item, boolean flag) {
		boolean uses = false;

		if (isPriceValid(item)) {
			for (Iterator i = criteria(); i.hasNext();) {
				if (attributesEqual((String) i.next(), item)) {
					uses = true;
					break;
				}
			}
		}

		return uses;
	}

	@Override
	public MAXDiscountListEntry getMAXEntry(String id) {
		return (MAXDiscountListEntry) map.get(id);
	}

	// Changes for Rev 1.0 : Starts
	@Override
	public boolean attributesEqual(String criterion, DiscountItemIfc item) {

		// changed the return String
		HashMap map = new HashMap();
		map.put(COMPARISION_BASIS, String.valueOf(comparisonBasis));
		map.put(CRITERION, criterion);

		boolean result = comparisonBasis == -1 ? item.isClassifiedAs(criterion) : criterion
				.equals(((MAXSaleReturnLineItem) item).getComparator(map));
		if (result) {
			// commented for bug 7143 and 7118
			// uncommented for bug 7370
			if (((SaleReturnLineItemIfc) item).getPLUItem() instanceof MAXPLUItemIfc) {
				if ((((MAXPLUItemIfc) ((SaleReturnLineItemIfc) item).getPLUItem()).getItemExclusionGroupList() != null)
						&& (((MAXPLUItemIfc) ((SaleReturnLineItemIfc) item).getPLUItem()).getItemExclusionGroupList()
								.size() != 0))
					result = false;
			}
		}
		return result;

	}

	@Override
	public boolean quantitySatisfied(String id) {
		boolean satisfied = false;

		if (containsEntry(id)) {
			MAXDiscountListEntry dle = (MAXDiscountListEntry) map.get(id);
			if (getRuleReasonCode() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNOrMoreOfXGetatUnitPriceTiered)
				satisfied = dle.quantitySatisfiedForKGItem();
			else
				satisfied = dle.quantitySatisfied();
		}
		return satisfied;
	}

	protected List<DiscountItemIfc> pickAnyEntries() {
		List<DiscountListEntryIfc> entries = new ArrayList<DiscountListEntryIfc>(map.size());
		// add qualified dles to the list
		for (DiscountListEntryIfc dle : map.values()) {
			switch (thresholdType) {
			case THRESHOLD_QUANTITY: {
				if (dle.quantitySatisfied()) {
					entries.add(dle);
				}
				break;
			}
			case THRESHOLD_AMOUNT: {
				if (dle.amountSatisfied()) {
					entries.add(dle);
				}
				break;
			}
			}
		}
		// if too many, sort by most expensive
		if (entries.size() > anyQuantity) {
			sortAnyEntries(entries);
		}
		int anySelectedQty = anyQuantity;
		if (entries.size() < anyQuantity) {
			// When the all source items belong to the same creiterion and match
			// the ANY quantity
			anySelectedQty = entries.size();
		}
		// add any qty to selected
		List<DiscountItemIfc> anySelected = new ArrayList<DiscountItemIfc>(entries.size());
		for (int i = 0; i < anySelectedQty; i++) {
			// don't stop if "NorMore" type discount
			if (i >= anyQuantity
					&& !(DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZPctoff.equals(description) || DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each
							.equals(description))) {
				break;
			}
			MAXDiscountListEntry dle = (MAXDiscountListEntry) entries.get(i);
			anySelected.addAll(dle.discountItems);
		}
		return anySelected;
	}
	// Changes for Rev 1.0 : Ends

	//Changes for Rev 1.1 : Starts
	protected boolean evaluateAmounts(ArrayList candidates, ArrayList selected) {
		// reset previously counted amounts
		resetAmounts();
		resetQuantities();
		selected.clear();

		String criterion = null;
		DiscountSourceIfc candidate = null;
		boolean criteriaSatisfied = false;

		// for each String in the criteria list
		for (Iterator<String> p = criteria(); p.hasNext();) {
			criterion = p.next();

			// check to see if an item has a matching attribute
			for (Iterator candidateIter = candidates.iterator(); candidateIter.hasNext();) {
				candidate = (DiscountSourceIfc) candidateIter.next();
				if (attributesEqual(criterion, candidate) && itemIdIsAcceptable(criterion, selected, candidate)) {
					// if match is found, increment the amount counted,
					// add item to the selected bucket and remove from
					// candidates
					if (amountSatisfied(criterion)
							&& (!(getDescription().equals(DISCOUNT_DESCRIPTION_Buy$NofXatZ$offTiered) || getDescription()
									.equals(DISCOUNT_DESCRIPTION_Buy$NofXatZPctoffTiered)))) {
						break;
					} else {
						if (isAllRequired()) {
							selected.add(candidate);
							candidateIter.remove();
						}
						//Changes for Rev 1.2 : Starts
						addToAmount(criterion, candidate.getSellingPrice());
						//Changes for Rev 1.2 : Ends
						//Puru
						if(((MAXSaleReturnLineItemIfc)candidate).isUnitOfMeasureItem())
						{
							double qty = ((MAXSaleReturnLineItemIfc)candidate).getItemPrice().getItemQuantityDecimal().doubleValue();
							int intQty = ((MAXSaleReturnLineItemIfc)candidate).getItemPrice().getItemQuantityDecimal().intValue();
							for(int i=0 ; i< intQty; i++)
							{
								if(i!=0){
									addToAmount(criterion, candidate.getSellingPrice());
								}
							}
							if((qty - (((MAXSaleReturnLineItemIfc)candidate).getItemPrice().getItemQuantityDecimal().intValue())) > 0.0)
							{
								BigDecimal quantity = (((MAXSaleReturnLineItemIfc)candidate).getItemPrice().getItemQuantityDecimal())
										.subtract(new BigDecimal(((MAXSaleReturnLineItemIfc)candidate).getItemPrice().getItemQuantityDecimal().intValue()));
								addToAmount(criterion, candidate.getSellingPrice().multiply(quantity));
							}
						}
						
						addDiscountItem(criterion, candidate);
					}
				}// end if
			}// end for
		}// end for

		if (isAnyComboAcceptable() && isMultiThreshold()) {
			criteriaSatisfied = evaluateAnyComboAmountSatisfied(candidates, selected);
		} else {
			criteriaSatisfied = (!isAllRequired()) ? evaluateAnyAmountSatisfied(candidates, selected)
					: allAmountsSatisfied();
		}
		return criteriaSatisfied;
	}
	//Changes for Rev 1.1 : Ends
}