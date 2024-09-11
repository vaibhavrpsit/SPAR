/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.5 	Mar 07, 2019		Purushotham Reddy	Changes for New Discount Rule Promo-CR
 *  Rev 1.4     Mar 16, 2017        Nitika Arora    	Changes for post void fixes(setting itemdiscountamount in the particular strategy object)
 *  Rev 1.3     Mar 01, 2017        Nitika Arora   	 	Changes for printing the csp mrp difference on discount column of receipt.
 *  Rev 1.2     Feb 09, 2017        Nitika Arora    	Changes for Id 233
 *	Rev	1.1 	Nov 07, 2016		Mansi Goel			Changes for Discount Rule FES
 *	Rev	1.0 	Aug 16, 2016		Nitesh Kumar		Changes for Code Merging	
 *
 ********************************************************************************/

package max.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

import max.retail.stores.domain.discount.MAXAdvancedPricingRuleIfc;
import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.discount.MAXItemDiscountStrategyIfc;
import max.retail.stores.domain.discount.MAXTransactionDiscountStrategyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPrice;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.utility.SecurityOverrideIfc;
import oracle.retail.stores.foundation.utility.Util;

import org.apache.log4j.Logger;

//------------------------------------------------------------------------------
/**
 * Item pricing object.
 * <P>
 * 
 * @version $Revision: /rgbustores_12.0.9in_branch/3 $
 **/
// ------------------------------------------------------------------------------
public class MAXItemPrice extends ItemPrice implements MAXItemPriceIfc, MAXDiscountRuleConstantsIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 2853148947303375535L;

	/**
	 * revision number supplied by Team Connection
	 **/
	public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/3 $";

	/** The logger to which log messages will be sent. **/
	protected static Logger logger = Logger.getLogger(max.retail.stores.domain.lineitem.MAXItemPrice.class);
	
	protected CurrencyIfc itemDiscountCardAmount;
	protected CurrencyIfc returnItemDiscountCardAmount;
	protected CurrencyIfc discountAmount;

	protected CurrencyIfc soldMRP;

	public CurrencyIfc getSoldMRP() {
		return soldMRP;
	}

	public void setSoldMRP(CurrencyIfc soldMRP) {
		this.soldMRP = soldMRP;
	}

	// protected boolean discountedPriceModified = false;
	// ---------------------------------------------------------------------
	/**
	 * Constructs MAXItemPrice object.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXItemPrice() {
		itemQuantity = BigDecimal.ZERO;
		sellingPrice = DomainGateway.getBaseCurrencyInstance();
		permanentSellingPrice = DomainGateway.getBaseCurrencyInstance();
		extendedSellingPrice = DomainGateway.getBaseCurrencyInstance();
		extendedDiscountedSellingPrice = DomainGateway.getBaseCurrencyInstance();
		itemTotal = DomainGateway.getBaseCurrencyInstance();
		itemDiscountAmount = DomainGateway.getBaseCurrencyInstance();
		itemTransactionDiscountAmount = DomainGateway.getBaseCurrencyInstance();
		itemDiscountCardAmount = DomainGateway.getBaseCurrencyInstance();
		itemDiscountTotal = DomainGateway.getBaseCurrencyInstance();
		itemTax = DomainGateway.getFactory().getItemTaxInstance();
		itemTax.setItemPrice(this);
	} // end ItemPrice()

	// ---------------------------------------------------------------------
	/**
	 * Constructs copy of ItemPrice.
	 * <P>
	 * 
	 * @return generic object copy of item price object
	 **/
	// ---------------------------------------------------------------------
	public Object clone() {
		MAXItemPriceIfc ip = (MAXItemPriceIfc) DomainGateway.getFactory().getItemPriceInstance();
		setCloneAttributes((MAXItemPrice) ip);
		return ip;
	}

	// ---------------------------------------------------------------------
	/**
	 * Sets attributes for clone.
	 * <P>
	 * 
	 * @param newClass
	 *            instance of new object
	 **/
	// ---------------------------------------------------------------------
	protected void setCloneAttributes(MAXItemPrice newClass) { // begin
																// setCloneAttributes()
		newClass.setItemQuantity(new BigDecimal(itemQuantity.toString()));
		newClass.setSellingPrice((CurrencyIfc) sellingPrice.clone());
		newClass.setPermanentSellingPrice((CurrencyIfc) permanentSellingPrice.clone());
		newClass.setItemTax((ItemTaxIfc) itemTax.clone());
		ItemDiscountStrategyIfc d = null;
		Enumeration e = itemDiscountsVector.elements();
		while (e.hasMoreElements()) {
			d = (ItemDiscountStrategyIfc) e.nextElement();
			newClass.addItemDiscount((ItemDiscountStrategyIfc) d.clone());
		}
		// clone other entries
		newClass.extendedSellingPrice = (CurrencyIfc) extendedSellingPrice.clone();
		newClass.extendedDiscountedSellingPrice = (CurrencyIfc) extendedDiscountedSellingPrice.clone();
		newClass.itemTotal = (CurrencyIfc) itemTotal.clone();
		newClass.itemDiscountAmount = (CurrencyIfc) itemDiscountAmount.clone();
		newClass.itemDiscountTotal = (CurrencyIfc) itemDiscountTotal.clone();
		newClass.itemTransactionDiscountAmount = (CurrencyIfc) itemTransactionDiscountAmount.clone();
		// added by dipak goit for Capillary Coupon Discount
		newClass.itemDiscountCardAmount = (CurrencyIfc) itemDiscountCardAmount.clone();
		// end for Capillary Coupon Discount
		newClass.setItemPriceOverrideReasonCode(getItemPriceOverrideReasonCode());
		newClass.discountEligible = discountEligible;
		newClass.employeeDiscountEligible = employeeDiscountEligible;
		newClass.damageDiscountEligible = damageDiscountEligible;
		newClass.soldMRP = getSoldMRP();

		if (restockingFee != null) {
			newClass.restockingFee = (CurrencyIfc) restockingFee.clone();
		}

		if (extendedRestockingFee != null) {
			newClass.extendedRestockingFee = (CurrencyIfc) extendedRestockingFee.clone();
		}
		if (priceOverrideAuthorization != null) {
			newClass.priceOverrideAuthorization = (SecurityOverrideIfc) priceOverrideAuthorization.clone();
		}

		if (promotionLineItems != null) {
			Enumeration pliEnum = promotionLineItems.elements();
			PromotionLineItemIfc newPromotionLineItem = null;
			while (pliEnum.hasMoreElements()) {
				newPromotionLineItem = (PromotionLineItemIfc) pliEnum.nextElement();
				newClass.addPromotionLineItem((PromotionLineItemIfc) newPromotionLineItem.clone());
			}
		}
		if(discountAmount != null)
		newClass.discountAmount =  (CurrencyIfc) discountAmount.clone();;
	} // end setCloneAttributes()

	// ---------------------------------------------------------------------
	/**
	 * Determine if two objects have equal attribute values and that the
	 * associated objects are equivalent.
	 * 
	 * @param obj
	 *            object to compare with
	 * @return boolean true if the objects pass the test
	 **/
	// ---------------------------------------------------------------------
	public boolean equals(Object obj) {
		boolean isEqual = false;

		if (obj instanceof MAXItemPrice) {
			MAXItemPrice price = (MAXItemPrice) obj;
			try {
				if (objectEquals("getSellingPrice", getSellingPrice(), price.getSellingPrice())
						&& objectEquals("getSellingPriceBeforeOverride", getPermanentSellingPrice(),
								price.getPermanentSellingPrice())
						&& objectEquals("getExtendedSellingPrice", getExtendedSellingPrice(),
								price.getExtendedSellingPrice())
						&& objectEquals("getExtendedDiscountedSellingPrice", getExtendedDiscountedSellingPrice(),
								price.getExtendedDiscountedSellingPrice())
						&& objectEquals("getItemTotal", getItemTotal(), price.getItemTotal())
						&& objectEquals("getItemTaxAmount", getItemTaxAmount(), price.getItemTaxAmount())
						&& objectEquals("getItemInclusiveTaxAmount", getItemInclusiveTaxAmount(),
								price.getItemInclusiveTaxAmount())
						&& objectEquals("getItemQuantity", getItemQuantityDecimal(), price.getItemQuantityDecimal())
						&& (getItemPriceOverrideReasonCode() == price.getItemPriceOverrideReasonCode())
						&& (getDiscountEligible() == price.getDiscountEligible())
						&& (getEmployeeDiscountEligible() == price.getEmployeeDiscountEligible())
						&& (getDamageDiscountEligible() == price.getDamageDiscountEligible())
						&& objectEquals("getItemDiscountAmount", getItemDiscountAmount(), price.getItemDiscountAmount())
						&& objectEquals("getItemDiscountTotal", getItemDiscountTotal(), price.getItemDiscountTotal())
						&& objectEquals("getItemTransactionDiscountAmount", getItemTransactionDiscountAmount(),
								price.getItemTransactionDiscountAmount())
						&& objectEquals("getItemDiscount", getItemDiscounts(), price.getItemDiscounts())
						&& objectEquals("getItemTax", getItemTax(), price.getItemTax())
						&& objectEquals("getRestockingFee", getRestockingFee(), price.getRestockingFee())
						&& objectEquals("getExtendedRestockingFee", getExtendedRestockingFee(),
								price.getExtendedRestockingFee())
						&& objectEquals("getPriceOverrideAuthorization", getPriceOverrideAuthorization(),
								price.getPriceOverrideAuthorization())
						&& objectEquals("getRevisionNumber", getRevisionNumber(), price.getRevisionNumber())
						&& objectEquals("getDiscountAmount", getDiscountAmount(), price.getDiscountAmount()	)) {
					isEqual = true;
				} else {
					isEqual = false;
				}
			} catch (Exception e) {
				logger.error("ItemPrice.equals, Exception: " + e + Util.throwableToString(e));
				isEqual = false;
			}
		}
		return isEqual;
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculate percentage discounts.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	/* Edited by dipak goit for Capilary Coupon Discount */
	protected void calculatePercentageDiscounts() {
		// calculate discounts by percentage
		ItemDiscountStrategyIfc[] d = getItemDiscountsByPercentage();
		CurrencyIfc discountCardAmount = null;
		if (d != null) {
			for (int i = 0; i < d.length; i++) {
				/* Condition: 1 */
				/**
				 * If discount and item% discount in one transaction then
				 * discount value is differ from sell transaction to return
				 * transaction add condition
				 */
				if (returnItemDiscountCardAmount != null && itemDiscountCardAmount != null) {
					if (itemDiscountCardAmount.getStringValue().equalsIgnoreCase("0.00")) {
						discountCardAmount = itemDiscountCardAmount;
						itemDiscountCardAmount = returnItemDiscountCardAmount.negate();
					}
				}
				/* End of Condition: 1 */
				/**
				 * The next line is commented because we do not need to work for
				 * best deal. But it is implemented in Other Project (MAX)
				 */
				// ItemDiscountStrategyIfc bd = getBestDealDiscount();
				if ((d[i].getAssignmentBasis() == ASSIGNMENT_EMPLOYEE && isEmployeeDiscountEligible())
						|| (d[i].getAssignmentBasis() == ASSIGNMENT_MANUAL && d[i].isDamageDiscount()
								&& isDamageDiscountEligible())
						|| isDiscountEligible()) {
					CurrencyIfc tempPrice = extendedSellingPrice.subtract(itemDiscountAmount)
							.subtract(itemDiscountCardAmount);
					CurrencyIfc localDisc = DomainGateway.getBaseCurrencyInstance();
					//localDisc = d[i].calculateItemDiscount(tempPrice);
					localDisc = d[i].calculateItemDiscount(tempPrice, itemQuantity);
					/**
					 * The next line is commented because we do not need to work
					 * for best deal. But it is implemented in Other Project
					 * (MAX)
					 */
					// if(bd!=null &&
					// d[i].getDiscountEmployee()!=null&&d[i].getAssignmentBasis()==5)
					// {
					// clearItemDiscounts();
					// localDisc.setDecimalValue(DomainGateway.getBaseCurrencyInstance().getDecimalValue());
					// }
					d[i].setItemDiscountAmount(itemDiscountAmount);
					itemDiscountAmount = itemDiscountAmount.add(localDisc);					
					if (discountCardAmount != null)
						itemDiscountCardAmount = discountCardAmount;
				}
			}
		}
	}

	// ---------------------------------------------------------------------
	/**
	 * Calculate dollar amount discounts.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	protected void calculateAmountDiscounts() {
		// calculate manual discount by amount
		// ItemDiscountStrategyIfc[] d = getItemDiscountsByAmount(); --
		// above line is commented by Dipak Goit

		ItemDiscountStrategyIfc[] itemdiscount = getItemDiscountsByAmountIncludingCapillaryAmount();

		/**
		 * The next line is commented because we do not need to work for best
		 * deal. But it is implemented in Other Project (MAX)
		 */
		// ItemDiscountStrategyIfc bd = getBestDealDiscount();

		if (itemdiscount != null) {
			CurrencyIfc calItmDiscount = null;
			for (int i = 0; i < itemdiscount.length; i++) {
				if ((itemdiscount[i].getAssignmentBasis() == ASSIGNMENT_EMPLOYEE && isEmployeeDiscountEligible())
						|| (itemdiscount[i].getAssignmentBasis() == ASSIGNMENT_MANUAL
								&& itemdiscount[i].isDamageDiscount() && isDamageDiscountEligible())
						|| isDiscountEligible()) {
					calItmDiscount = itemdiscount[i].calculateItemDiscount(extendedSellingPrice);
					
					/* Rev 1.3 start */
					if (itemdiscount[i] instanceof ItemDiscountByAmountStrategy && itemdiscount[i]
							.getAssignmentBasis() != MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON) {
						/* Rev 1.3 end */
						calItmDiscount = calItmDiscount.multiply(getItemQuantityDecimal().abs());
					}

					if (((itemdiscount[i]
							.getAssignmentBasis() == MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON)
							|| (itemdiscount[i].getReasonCode() == 5170))) {
						// changes for Capillary Coupon
						if (!((MAXItemDiscountStrategyIfc) itemdiscount[i]).getCapillaryCoupon().isEmpty()
								|| ((MAXItemDiscountStrategyIfc) itemdiscount[i]).getCapillaryCoupon().isEmpty()) {
							BigDecimal extndprice = new BigDecimal(calItmDiscount.getStringValue());
							try {
								BigDecimal val = new BigDecimal(0.00);
								int roundingMode = BigDecimal.ROUND_HALF_UP;
								val = extndprice.divide(getItemQuantityDecimal(), 5, roundingMode);
								calItmDiscount = calItmDiscount.divide(calItmDiscount);
								BigDecimal b = new BigDecimal(1.00);
								b = val.multiply(getItemQuantityDecimal()).divide(b, 2, roundingMode);
								calItmDiscount = calItmDiscount.multiply(b);

							} catch (Exception e) {

							}
						} else {
							calItmDiscount = calItmDiscount.multiply(getItemQuantityDecimal().abs());
						}

					}
					// Rev 1.25 End
					// Change for bug 15932 by Aakash:Endss
					else {
						// Below line Commented by Danish
						// calItmDiscount =
						// calItmDiscount.multiply(getItemQuantityDecimal().abs());
					}

					// End for Capillary Coupon
					/*
					 * } c = c.multiply(getItemQuantityDecimal().abs()); Rev 1.1
					 * start double bc = c.getDoubleValue(); BigDecimal bd=new
					 * BigDecimal(bc); bd = bd.setScale(2,
					 * BigDecimal.ROUND_HALF_UP); BigDecimal bd1=new
					 * BigDecimal(1); bd = bd.divide(bd1, bd.ROUND_UP);
					 * CurrencyIfc
					 * newc=DomainGateway.getBaseCurrencyInstance(bd);
					 */
					/**
					 * The next line is commented because we do not need to work
					 * for best deal. But it is implemented in Other Project
					 * (MAX)
					 */
					// if(bd!=null &&
					// d[i].getDiscountEmployee()!=null&&d[i].getAssignmentBasis()==5)
					// {
					// clearItemDiscounts();
					// c.setDecimalValue(DomainGateway.getBaseCurrencyInstance().getDecimalValue());
					// }
					//Changes for Id 233 starts
					itemdiscount[i].setItemDiscountAmount(calItmDiscount);
					//Changes for Id 233 ends
					itemDiscountAmount = itemDiscountAmount.add(calItmDiscount);
					/* Rev 1.1 End */
				}
			}
		}
	}

	/**
	 * Actually, We need to override getItemDiscountsByAmount()method because we
	 * have customized ItemDiscountStrategyIfc by adding Capillary related
	 * changes to MAXItemDiscountStrategyIfc. But java does not allow any method
	 * to override by changing its signature. If we change signature then java
	 * Compiler treats it as a new method of the current class.
	 *
	 */
	// ---------------------------------------------------------------------
	/**
	 * Retrieves array of item discounts by amount that must include capillary
	 * coupon amount.
	 * <P>
	 * 
	 * @return array of discount items of MAXItemDiscountStrategyIfc type.
	 **/
	// ---------------------------------------------------------------------
	public ItemDiscountStrategyIfc[] getItemDiscountsByAmountIncludingCapillaryAmount() {
		ArrayList discounts = new ArrayList();
		ItemDiscountStrategyIfc d = null;

		for (Iterator i = itemDiscountsVector.iterator(); i.hasNext();) {
			d = (ItemDiscountStrategyIfc) i.next();

			if (d instanceof ItemDiscountByAmountStrategy && !d.isAdvancedPricingRule()) {
				discounts.add(d);
			}
		}

		ItemDiscountStrategyIfc[] array = new ItemDiscountStrategyIfc[discounts.size()];
		discounts.toArray(array);

		return array;
	}

	public void recalculateItemTotal() { // begin recalculateItemTotal()

		itemTotal = extendedSellingPrice.subtract(itemDiscountAmount);
		// changes by dipak goit for capillary coupon
		itemTotal = itemTotal.subtract(itemTransactionDiscountAmount).subtract(itemDiscountCardAmount);
		// end for capillary coupon
		itemDiscountTotal = extendedSellingPrice.subtract(itemTotal);
		extendedDiscountedSellingPrice.setStringValue(itemTotal.getStringValue());
		itemTax.setItemTaxableAmount(itemTotal);

		// If its a return, setItemTaxableAmount will have negated the taxable
		// amount.
		// This also has to negate the tax if appropriate.
		if (itemTax.getItemTaxableAmount().signum() == CurrencyIfc.NEGATIVE) {
			if (getItemTaxAmount().signum() == CurrencyIfc.POSITIVE) {
				setItemTaxAmount(getItemTaxAmount().negate());
			}
			if (getItemInclusiveTaxAmount().signum() == CurrencyIfc.POSITIVE) {
				setItemInclusiveTaxAmount(getItemInclusiveTaxAmount().negate());
			}
		}
		// add the restocking fee (only for return items)
		if (restockingFee != null) {
			extendedRestockingFee = restockingFee.multiply(itemQuantity);
			itemTotal = itemTotal.subtract(extendedRestockingFee);
		}

	} // end recalculateItemTotal()
		// ---------------------------------------------------------------------

	/**
	 * Adds transaction discount audit record.
	 * 
	 * @param value
	 *            the amount of the transaction discount
	 * @param td
	 *            strategy to collect attributes from
	 **/
	// ---------------------------------------------------------------------
	public void addTransactionDiscount(CurrencyIfc value, MAXTransactionDiscountStrategyIfc tds) {
		// instantiate new discount
		ItemTransactionDiscountAuditIfc td = (ItemTransactionDiscountAuditIfc) tds;
		ItemTransactionDiscountAuditIfc itda = (ItemTransactionDiscountAuditIfc) DomainGateway.getFactory()
				.getItemTransactionDiscountAuditInstance();
		//Commented For upgradation
		//itda.initialize(value, td.getReasonCode(), td.getAssignmentBasis());
		itda.setDiscountRate(td.getDiscountRate());
		itda.setReferenceID(td.getReferenceID());
		itda.setReferenceIDCode(td.getReferenceIDCode());
		itda.setDiscountEmployee(td.getDiscountEmployeeID());
		// added ruleId to allow coupon rule to survive suspend so POS won't
		// reject it later. CR30190 14FEB08 CMG
		// See {@link ItemContainerProxyIfc#areAllStoreCouponsApplied()}.
		itda.setRuleID(td.getRuleID());
		if (td.getAssignmentBasis() == MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON) {
			//Changes for Rev 1.1 : Starts
			((MAXAdvancedPricingRuleIfc) itda).setCapillaryCoupon(((MAXAdvancedPricingRuleIfc) td).getCapillaryCoupon());
			//Changes for Rev 1.1 : Ends
		}
		addItemDiscount(itda);
		itemTransactionDiscountAmount = itemTransactionDiscountAmount.add(value);
		itemDiscountTotal = itemDiscountTotal.add(value);
		recalculateItemTotal();
	}

	// public boolean isDiscountedPriceModified() {
	// return discountedPriceModified;
	// }
	//
	// public void setDiscountedPriceModified(boolean discountedPriceModified) {
	// this.discountedPriceModified = discountedPriceModified;
	// }

	public CurrencyIfc getReturnItemDiscountCardAmount() {
		return returnItemDiscountCardAmount;
	}

	public void setReturnItemDiscountCardAmount(CurrencyIfc returnItemDiscountCardAmount) {
		this.returnItemDiscountCardAmount = returnItemDiscountCardAmount;
	}

	public CurrencyIfc getItemDiscountCardAmount() {
		return itemDiscountCardAmount;
	}

	public void setItemDiscountCardAmount(CurrencyIfc itemDiscountCardAmount) {
		this.itemDiscountCardAmount = itemDiscountCardAmount;
		recalculateItemTotal();
	}

	// override for BuyNOrMoreOfXGetatUnitPriceTiered wrong discount amount
	protected void calculateBestDealDiscount() {
		// Calculate the discount that corresponds to the best deal
		ItemDiscountStrategyIfc bd = getBestDealDiscount();
		if (bd != null && isDiscountEligible()) {
			CurrencyIfc localDisc = bd.calculateItemDiscount(extendedSellingPrice);

			if ((bd.getDiscountMethod() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
					|| (bd.getDiscountMethod() == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE)) {
				/* Rev 1.2 start BuyNOrMoreOfXGetatUnitPriceTiered */
				//Puru
				if (Integer.parseInt(bd.getReason().getCode()) != MAXDiscountRuleConstantsIfc.DISCOUNT_REASON_BuyNOrMoreOfXGetatUnitPriceTiered
						&& !(bd.getDescription().equalsIgnoreCase("BuyNofXforZ$")) && 
						!(bd.getDescription().equalsIgnoreCase("Buy$NorMoreOfX(WeightedOrUnit)getYatZ$"))
						&& !(bd.getDescription().equalsIgnoreCase("Buy$NorMoreOfX(WeightedOrUnit)getYatZ$off"))
						&& !(bd.getDescription().equalsIgnoreCase("Buy$NorMoreOfX(WeightedOrUnit)getYatZ%off"))
					/* Rev 1.2 End BuyNOrMoreOfXGetatUnitPriceTiered */)
					localDisc = localDisc.multiply(getItemQuantityDecimal().abs());
			}
            bd.setItemDiscountAmount(localDisc);
			itemDiscountAmount = itemDiscountAmount.add(localDisc);
		}
	}

	
	
	public String getRevisionNumber() { // begin getRevisionNumber()
		// return string
		return (Util.parseRevisionNumber(revisionNumber));
	}
	
	/**
	 * set the calculated value for discount.
	 * 
	 * @param discountAmount
	 * 
	 */
	public void setDiscountAmount(CurrencyIfc discountAmount) {
		this.discountAmount = discountAmount;
	}
	
	/**
	 * @return  
	 * get the calculated discount amount to be printed on receipt
	 */
	public CurrencyIfc getDiscountAmount()
	{
		return discountAmount;
	}
}
