/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel		Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import max.retail.stores.domain.MAXUtils.MAXUtils;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import oracle.retail.stores.domain.discount.DiscountItemIfc;
import oracle.retail.stores.domain.discount.DiscountListEntryIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.TargetCriteria;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;

public class MAXTargetCriteria extends TargetCriteria implements MAXDiscountRuleConstantsIfc, MAXDiscountListIfc {

	private static final long serialVersionUID = -8496296079553924831L;

	protected boolean allowRepeatingSources = true;

	/**
	 * Clones the criteria list.
	 * 
	 * @return new TargetCriteria Object
	 */
	public Object clone() {
		MAXTargetCriteria newList = new MAXTargetCriteria();
		setCloneAttributes(newList);
		return newList;
	}

	// gaurav

	protected void setCloneAttributes(MAXSourceCriteria newList) {
		super.setCloneAttributes(newList);
		newList.setAllowRepeatingSources(allowRepeatingSources);
	}

	public MAXDiscountListEntry getMAXEntry(String id) {
		return (MAXDiscountListEntry) map.get(id);
	}

	/**
	 * Tests to see if the target criteria for this rule have been met. Adds any
	 * objects which satisfy a target criterion to the selected list.
	 * 
	 * @param ArrayList
	 *            containing potential targets for this rule
	 * @return boolean indicating whether all the target criteria for this rule
	 *         have been met.
	 */
	protected boolean evaluateQuantities(ArrayList candidates, ArrayList selected, boolean allEligibleTargets) {
		// reset previously counted quantities
		resetQuantities();
		selected.clear();

		String criterion = null;
		DiscountTargetIfc candidate = null;

		// for each String in the criteria list
		for (Iterator<String> p = criteria(); p.hasNext();) {
			criterion = p.next();

			// check to see if a source matches the criterion
			for (Iterator candidateIter = candidates.iterator(); candidateIter.hasNext();) {
				candidate = (DiscountTargetIfc) candidateIter.next();
				// Changes for Rev 1.0 : Starts
				if (attributesEqual(criterion, (MAXSaleReturnLineItem) candidate)
						&& itemIdIsAcceptable(criterion, selected, candidate)) {
					// Changes for Rev 1.0 : Ends
					if (allEligibleTargets || !quantitySatisfied(criterion)) {
						if (anyQuantity == 0) {
							selected.add(candidate);
							candidateIter.remove();
						}
						incrementQuantity(criterion, candidate);
					} else {
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

		return (!isAllRequired()) ? evaluateAnySatisfied(candidates, selected, allEligibleTargets) : allSatisfied();
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

	public boolean getAllowRepeatingSources() {
		return allowRepeatingSources;
	}

	public void setAllowRepeatingSources(boolean value) {
		allowRepeatingSources = value;
	}

	protected boolean itemIdIsAcceptable(String criterion, ArrayList selected, DiscountTargetIfc item) {
		if (!allowRepeatingSources) {
			String itemID = item.getItemID();
			List otherSelections = null;
			if (criterion == null || anyQuantity == 0) {
				otherSelections = selected;
			} else {
				// check against items in DLE for this item.
				MAXDiscountListEntry dle = (MAXDiscountListEntry) map.get(criterion);
				otherSelections = dle.discountItems;
			}
			if (isItemAlreadySelected(itemID, otherSelections)) {
				return false;
			}
		}

		return true;
	}

	private boolean isItemAlreadySelected(String itemID, List otherSelections) {
		// check against rest in selected list
		for (Iterator j = otherSelections.iterator(); j.hasNext();) {
			DiscountSourceIfc temp = (DiscountSourceIfc) j.next();

			if (temp instanceof AbstractTransactionLineItemIfc) {
				String tempID = ((AbstractTransactionLineItemIfc) temp).getItemID();
				if (tempID.equals(itemID)) {
					return true;
				}
			}
		} // end for
		return false;
	}

	// Changes for Rev 1.0 : Starts
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

	@Override
	public boolean attributesEqual(String criterion, DiscountItemIfc item) {

		// changed the return String
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(COMPARISION_BASIS, String.valueOf(comparisonBasis));
		map.put(CRITERION, criterion);

		boolean result = comparisonBasis == -1 ? item.isClassifiedAs(criterion) : criterion
				.equals(((MAXSaleReturnLineItem) item).getComparator(map));
		if (result) {
			if (((SaleReturnLineItemIfc) item).getPLUItem() instanceof MAXPLUItemIfc) {
				if ((((MAXPLUItemIfc) ((SaleReturnLineItemIfc) item).getPLUItem()).getItemExclusionGroupList() != null)
						&& (((MAXPLUItemIfc) ((SaleReturnLineItemIfc) item).getPLUItem()).getItemExclusionGroupList()
								.size() != 0))
					result = false;
			}
		}
		return result;
	}
	// Changes for Rev 1.0 : Ends
}