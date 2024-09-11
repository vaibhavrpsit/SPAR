/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 * 	Rev	1.2 	Mar 07, 2019		Purushotham Reddy	Changes for New Discount Rule Promo-CR
 *
 *	Rev	1.0 	Nov 07, 2016		Mansi Goel			Changes for Discount Rule FES	
 *
 ********************************************************************************/

package max.retail.stores.domain.discount;

//java imports
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import max.retail.stores.domain.comparators.MAXComparators;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.BestDealGroup;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxy;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;

import org.apache.log4j.Logger;

public class MAXBestDealGroup extends BestDealGroup {
	
	private static final long serialVersionUID = -1146843532113740147L;
	private static Logger logger = Logger.getLogger(MAXBestDealGroup.class);

	public void applyAdvancedPricingDiscounts() {
		if (logger.isDebugEnabled())
			logger.debug("applying best deal: " + this);
		CurrencyIfc runningTotal = (CurrencyIfc) getTotalSellingPrice().clone();
		CurrencyIfc discountAmount = null;
		CurrencyIfc fixedPrice = null;
		int discountMethod = discountRule.getDiscountMethod();
		int pricingGroupID = discountRule.getPricingGroupID();

		if (discountRule.getDescription().equalsIgnoreCase("BuyNOrMoreOfXGetatUnitPriceTiered")) {
			discountMethod = 4;
			// discountRule.setDiscountMethod(discountMethod);
		}

		Iterator<DiscountSourceIfc> sourcesIterator;
		DiscountTargetIfc source;

		boolean lowestPricedDiscountApplied = false;
		boolean highestPricedDiscountApplied = false;

		initializeAppliedTargets();
		if (discountRule.getDealDistribution()
				&& !discountRule.getDescription().equalsIgnoreCase("BuyNOrMoreOfXGetatUnitPriceTiered")) {

			discountMethod = DISCOUNT_METHOD_AMOUNT;
			discountAmount = getTotalDiscount();

			// If the sources are targets flag is true, the sources are
			// already in the discountAppliedTargets list: don't add them
			// again.
			if (discountRule.getSourcesAreTargets() == false) {
				// Add all sources that are eligibile targets to the target
				// collection
				sourcesIterator = sources.iterator();
				while (sourcesIterator.hasNext() == true) {
					// Cast to DiscountTargetIfc to check if it's an eligible
					// target.
					source = (DiscountTargetIfc) sourcesIterator.next();
					if (source.isTargetEnabled() == true) {
						discountAppliedTargets.add(source);
					}
				}
			}

			runningTotal = ItemContainerProxy.calculateTotalSellingPrice(discountAppliedTargets);
		}

		if (discountMethod == DISCOUNT_METHOD_FIXED_PRICE
				|| discountMethod == MAXDiscountRuleConstantsIfc.DISCOUNT_METHOD_WEIGHTED_ITEM) {

			// get the fixed price for all target items
			if (discountRule.getDescription().equals(
					DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each)) {
				// For this discount rule both source and targets are same. Get
				// the quantity from
				// the source list and calculate the total fixed price.
				int quantity = 1;
				Object[] criterias = discountRule.getSourceList().criteriaArray();
				if (criterias.length > 0) {
					quantity = discountRule.getSourceList().getQuantity((String) criterias[0]);
				}
				fixedPrice = discountRule.getFixedPrice().multiply(new BigDecimal(quantity));
			} else {
				fixedPrice = (CurrencyIfc) discountRule.getFixedPrice().clone();
			}
		}

		if (!discountAppliedTargets.isEmpty()) {
			Iterator<DiscountTargetIfc> targetsIterator = discountAppliedTargets.iterator();

			List<DiscountTargetIfc> mItemsForDiscountList = new ArrayList<DiscountTargetIfc>();
			mItemsForDiscountList.addAll(discountAppliedTargets);
			boolean flgForMTypeRule = false;

			// create an ItemDiscountStrategy object and apply it to the targets
			DiscountTargetIfc target = null;

			while (targetsIterator.hasNext()) {
				ItemDiscountStrategyIfc tempStrategy = null;
				target = targetsIterator.next();

				switch (discountMethod) {
				case DISCOUNT_METHOD_PERCENTAGE: {
					if (discountRule.getDescription().equals(
							MAXDiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNofXgetMofXwithLowestPriceatZPctoff)) {
						flgForMTypeRule = true;

						if (isLowestPricedTarget(target) && !lowestPricedDiscountApplied) {
							lowestPricedDiscountApplied = true;
							tempStrategy = DomainGateway.getFactory().getItemDiscountByPercentageInstance();
							tempStrategy.setDiscountRate(discountRule.getDiscountRate());
							discountAppliedTargets = new ArrayList<DiscountTargetIfc>();
							//Changes to sort item list for BuyNofXgetMofXwithLowestPriceatZ%off rule : Starts 
							Collections.sort(mItemsForDiscountList, MAXComparators.lineItemPriceAscending);
							//Changes to sort item list for BuyNofXgetMofXwithLowestPriceatZ%off rule : Ends
							if (discountRule instanceof MAXAdvancedPricingRuleIfc) {
								for (int i = 0; i < ((MAXAdvancedPricingRuleIfc) discountRule).getmValue(); i++)
									discountAppliedTargets.add(mItemsForDiscountList.get(i));
							}
						} else {
							((DiscountSourceIfc) target).setSourceAvailable(false);
						}
					} else if (discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_LOWEST) {
						if (isLowestPricedTarget(target) && !lowestPricedDiscountApplied) {
							lowestPricedDiscountApplied = true;
							tempStrategy = DomainGateway.getFactory().getItemDiscountByPercentageInstance();
							tempStrategy.setDiscountRate(discountRule.getDiscountRate());
							discountAppliedTargets = new ArrayList<DiscountTargetIfc>();
							discountAppliedTargets.add(target);
							tempStrategy.setPricingGroupID(pricingGroupID);
						} else {
							((DiscountSourceIfc) target).setSourceAvailable(false);
						}
					} else if (discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_HIGHEST) {
						if (isHighestPricedTarget(target) && !highestPricedDiscountApplied) {
							highestPricedDiscountApplied = true;
							tempStrategy = DomainGateway.getFactory().getItemDiscountByPercentageInstance();
							tempStrategy.setDiscountRate(discountRule.getDiscountRate());
							discountAppliedTargets = new ArrayList<DiscountTargetIfc>();
							discountAppliedTargets.add(target);
							tempStrategy.setPricingGroupID(pricingGroupID);
						} else {
							((DiscountSourceIfc) target).setSourceAvailable(false);
						}
						
					} else if (discountRule.getCalcDiscOnItemType() == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZPctoffTiered_BillBuster) {
						
						tempStrategy = DomainGateway.getFactory().getItemDiscountByPercentageInstance();
						tempStrategy.setDiscountRate(discountRule.getDiscountRate());
						discountAppliedTargets = new ArrayList<DiscountTargetIfc>();
						discountAppliedTargets.add(target);
						tempStrategy.setPricingGroupID(pricingGroupID);
					
					}
					else {
						tempStrategy = DomainGateway.getFactory().getItemDiscountByPercentageInstance();

						tempStrategy.setDiscountRate(discountRule.getDiscountRate());
						tempStrategy.setPricingGroupID(pricingGroupID);
					}

					break;
				}
				case DISCOUNT_METHOD_AMOUNT: {
					tempStrategy = DomainGateway.getFactory().getItemDiscountByAmountInstance();
					if (discountAmount == null) {
						discountAmount = getDiscountRule().getDiscountAmount();
					}
					if (discountRule.getDescription().equals(
							DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each)) {
						if (target.getExtendedSellingPrice().abs().compareTo(getDiscountRule().getFixedPrice()) == CurrencyIfc.GREATER_THAN) {
							CurrencyIfc da = target.getExtendedSellingPrice().abs()
									.subtract(getDiscountRule().getFixedPrice());
							tempStrategy.setDiscountAmount(da);
						} else {
							CurrencyIfc da = DomainGateway.getBaseCurrencyInstance();
							tempStrategy.setDiscountAmount(da);
						}
					} else if (Integer.parseInt(getDiscountRule().getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NofXatZ$offTiered
							|| Integer.parseInt(getDiscountRule().getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNofXatZ$offTiered
							|| Integer.parseInt(getDiscountRule().getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNofXforZ$off
							|| Integer.parseInt(getDiscountRule().getReason().getCode()) == MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_Buy$NorMoreGetYatZ$offTiered_BillBuster) {

						PLUItemIfc plu = ((SaleReturnLineItemIfc) target).getPLUItem();
						if ((plu != null)
								&& ((plu.getItem().getUnitOfMeasure().getUnitID().equals("KG")) || (plu.getItem()
										.getUnitOfMeasure().getUnitID().equals("UT")))) {
							tempStrategy.setDiscountAmount(discountAmount);
							runningTotal = runningTotal.subtract(target.getExtendedSellingPrice());
						} else {
							tempStrategy.setDiscountAmount(calculateProratedDiscount(target, runningTotal,
									discountAmount));
							runningTotal = runningTotal.subtract(target.getExtendedSellingPrice());
							discountAmount = discountAmount.subtract(tempStrategy.getDiscountAmount());
						}
						// END FIX of bug 2018

						break;
					} else {
						tempStrategy.setDiscountAmount(calculateProratedDiscount(target, runningTotal, discountAmount));
					}
					runningTotal = runningTotal.subtract(target.getExtendedSellingPrice());
					discountAmount = discountAmount.subtract(tempStrategy.getDiscountAmount());
					tempStrategy.setPricingGroupID(pricingGroupID);
					break;
				}
				case DISCOUNT_METHOD_FIXED_PRICE: {
					tempStrategy = DomainGateway.getFactory().getItemDiscountByFixedPriceStrategyInstance();

					// get a reference to the price for this target item
					CurrencyIfc targetPrice = target.getExtendedSellingPrice();

					// calculate and set the discount amount for this target's
					// strategy
					//discountAmount = calculateFixedPriceDiscount(targetPrice, runningTotal, fixedPrice);
					//Puru
					if(!(((SaleReturnLineItemIfc) target).isUnitOfMeasureItem()))
					{
						discountAmount = calculateFixedPriceDiscount(targetPrice, runningTotal, fixedPrice);
					}
					else if((((SaleReturnLineItemIfc) target).isUnitOfMeasureItem()))
					{
						BigDecimal qty = ((SaleReturnLineItemIfc) target).getItemQuantityDecimal();
						targetPrice = targetPrice.divide(qty);
						runningTotal = runningTotal.divide(qty);
						discountAmount = calculateFixedPriceDiscount(targetPrice, runningTotal, fixedPrice);
					}
					tempStrategy.setDiscountAmount(discountAmount);

					// reduce the fixed price of the
					fixedPrice = fixedPrice.subtract(targetPrice.abs().subtract(discountAmount));
					runningTotal = runningTotal.subtract(targetPrice);

					break;
				}
				case MAXDiscountRuleConstantsIfc.DISCOUNT_METHOD_WEIGHTED_ITEM: {
					tempStrategy = DomainGateway.getFactory().getItemDiscountByFixedPriceStrategyInstance();
					CurrencyIfc targetPrice = target.getExtendedSellingPrice();
					BigDecimal decimalQuantity = ((SaleReturnLineItem) target).getItemQuantityDecimal();
					PLUItemIfc plu = ((SaleReturnLineItemIfc) target).getPLUItem();
					AdvancedPricingRuleIfc[] aPR = plu.getAdvancedPricingRules();
					BigDecimal checkQty = new BigDecimal("0.00");
					if (aPR != null) {
						for (int i = 0; i < aPR.length; i++) {
							// Bug fix for Weighted Item Promo @Puru
						if( !( aPR[i].getDescription().equalsIgnoreCase("Buy$NorMoreOfX(WeightedOrUnit)getYatZ%off") || 
								aPR[i].getDescription().equalsIgnoreCase("Buy$NorMoreOfX(WeightedOrUnit)getYatZ$") ||
										aPR[i].getDescription().equalsIgnoreCase("Buy$NorMoreOfX(WeightedOrUnit)getYatZ$off") )){
							BigDecimal thresQty = aPR[i].getSourceThreshold().getDecimalValue();
							int v = decimalQuantity.compareTo(thresQty);
							if (v == 1 || v == 0) {
								int t = thresQty.compareTo(checkQty);
								if (t == 1 || t == 0) {
									fixedPrice = aPR[i].getFixedPrice();
									checkQty = thresQty;
								}
							}
						}
					  }
					}
					CurrencyIfc csp = fixedPrice.multiply(decimalQuantity);
					discountAmount = targetPrice.subtract(csp);
					tempStrategy.setDiscountAmount(discountAmount);
					fixedPrice = csp.subtract(targetPrice.subtract(discountAmount));
					runningTotal = runningTotal.subtract(targetPrice);
					break;
				}

				default: {
					throw new IllegalStateException(
							"Unknown discount rule type in BestDealGroup.applyAdvancedPricingDiscounts(): "
									+ discountRule.toString());
				}
				}// end switch

				if (tempStrategy != null) {
					strategy = tempStrategy;
					strategy.setReason(discountRule.getReason());

					strategy.setRuleID(getRuleID());
					strategy.setLocalizedNames(discountRule.getLocalizedNames());
					// set the assignment basis for the strategy to that of the
					// parent rule
					strategy.setAssignmentBasis(discountRule.getAssignmentBasis());
					// set the advancedPricingFlag
					strategy.setAdvancedPricingRule(true);
					strategy.setIncludedInBestDeal(discountRule.isIncludedInBestDeal());
					// add referenceID referenceIDTypeCode
					strategy.setReferenceID(discountRule.getReferenceID());
					strategy.setReferenceIDCode(discountRule.getReferenceIDCode());
					strategy.setAccountingMethod(discountRule.getAccountingMethod());

					// Add Temporary Change Promotion Ids
					strategy.setPromotionId(discountRule.getPromotionId());
					strategy.setPromotionComponentId(discountRule.getPromotionComponentId());
					strategy.setPromotionComponentDetailId(discountRule.getPromotionComponentDetailId());
					strategy.setDescription(discountRule.getDescription());

					// clear the previously applied strategy
					target.removeAdvancedPricingDiscount();
					if (flgForMTypeRule) {
						for (int i = 0; i < discountAppliedTargets.size(); i++) {
							ItemDiscountStrategyIfc tStrategy = (ItemDiscountStrategyIfc) strategy.clone();
							DiscountTargetIfc dsc = discountAppliedTargets.get(i);
							dsc.applyAdvancedPricingDiscount(tStrategy);
						}
						flgForMTypeRule = false;
					} else
						target.applyAdvancedPricingDiscount(strategy);
				}

			}// end while targets.hasNext()

			// Set all sources used by this rule to be unavailable.
			setSourceAvailability(false);

			bestDeal = true;
		} else {
			throw new IllegalStateException("No targets in BestDealGroup.applyAdvancedPricingDiscounts(): "
					+ discountRule.toString());
		}
	}

	protected CurrencyIfc[] totalDiscountArray = null;

	public CurrencyIfc[] getTotalDiscountArray() {
		return totalDiscountArray;
	}

	public void setTotalDiscountArray(CurrencyIfc[] totalDiscountArray) {
		this.totalDiscountArray = totalDiscountArray;
	}

	public CurrencyIfc calculateTotalDiscount() {
		clearTotals();

		totalSellingPrice = ItemContainerProxy.calculateTotalSellingPrice(discountRule.getTargets());

		switch (discountRule.getDiscountMethod()) {
		case DISCOUNT_METHOD_PERCENTAGE: {
			if (discountRule.getDescription().equals(
					MAXDiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNofXgetMofXwithLowestPriceatZPctoff)) {
				if (discountRule instanceof MAXAdvancedPricingRuleIfc) {
					totalDiscount = ((MAXAdvancedPricingRuleIfc) discountRule).getDiscountAmountOnMLowestPricedItem();
				}
			} else if (discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_LOWEST) {
				totalDiscount = discountRule.getDiscountAmountOnLowestPricedItem();
			} else if (discountRule.getCalcDiscOnItemType() == DiscountRuleConstantsIfc.DISCOUNT_TYPE_ON_ITEM_HIGHEST) {
				totalDiscount = discountRule.getDiscountAmountOnHighestPricedItem();
			} else {
				totalDiscount = totalSellingPrice.multiply(discountRule.getDiscountRate());
			}
			break;
		}
		case DISCOUNT_METHOD_AMOUNT: {
			totalDiscount = discountRule.getDiscountAmount();
			break;
		}
		case DISCOUNT_METHOD_FIXED_PRICE: {
			CurrencyIfc totalFixedPrice = discountRule.getFixedPrice();
			if (discountRule.getDescription().equals(
					DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNorMoreOfXforZ$Each)) {
				List<DiscountTargetIfc> targets = discountRule.getTargets();
				Iterator<DiscountTargetIfc> targetsIterator = targets.iterator();
				totalSellingPrice = DomainGateway.getBaseCurrencyInstance();
				totalFixedPrice = DomainGateway.getBaseCurrencyInstance();
				CurrencyIfc fixedPrice = discountRule.getFixedPrice();
				while (targetsIterator.hasNext()) {
					DiscountTargetIfc target = targetsIterator.next();
					if (target.getExtendedSellingPrice().compareTo(fixedPrice) == CurrencyIfc.GREATER_THAN) {
						totalSellingPrice = totalSellingPrice.add(target.getExtendedSellingPrice());
						totalFixedPrice = totalFixedPrice.add(fixedPrice);
					}
				}
				totalDiscount = totalSellingPrice.abs().subtract(totalFixedPrice);

			} else if (discountRule.getDescription().equalsIgnoreCase("BuyNOrMoreOfXGetatUnitPriceTiered")) {
				for (Iterator<DiscountTargetIfc> itr = discountRule.getTargets().iterator(); itr.hasNext();) {
					Object obj = itr.next();
					if (obj instanceof MAXSaleReturnLineItemIfc) {
						MAXSaleReturnLineItemIfc item = (MAXSaleReturnLineItemIfc) obj;
						BigDecimal decimalQuantity = item.getItemQuantityDecimal();
						CurrencyIfc csp = discountRule.getFixedPrice().multiply(decimalQuantity);
						totalDiscount = totalSellingPrice.subtract(csp);
					}
				}
			} else {
				if (totalSellingPrice.abs().compareTo(totalFixedPrice) == CurrencyIfc.GREATER_THAN) {
					totalDiscount = totalSellingPrice.abs().subtract(totalFixedPrice);
				}
			}
			break;
		}
		case MAXDiscountRuleConstantsIfc.DISCOUNT_METHOD_WEIGHTED_ITEM: {
			totalDiscount = totalSellingPrice.subtract(discountRule.getFixedPrice());
			break;
		}
		default: {
			throw new IllegalStateException("Unknown discount rule type in BestDealGroup.calculateTotalDiscount(): "
					+ discountRule.toString());
		}
		} // end switch

		totalDiscount = totalDiscount.abs();
		return totalDiscount;
	}
}
