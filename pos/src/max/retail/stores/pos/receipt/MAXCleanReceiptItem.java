/* ===========================================================================
 * Copyright (c) 2016 MAX Hyper Market Inc. All Rights Reserved.
 *
 * Rev 1.0  hitesh dua   18 Apr,2017 initial revision
 * change equal method for item clubbing as per max v12
 ===========================================================================
 */
package max.retail.stores.pos.receipt;

import java.io.Serializable;
import java.math.BigDecimal;

import max.retail.stores.domain.lineitem.MAXReturnItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItem;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXTransactionTotals;
import max.retail.stores.domain.transaction.MAXTransactionTotalsIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransaction;

/**
 * This class wraps a line item in order to identify which line items are "like"
 * each other based upon item id and price. This is useful for printing a
 * "clean" receipt where line items that have the same id and price are rolled
 * back up into the same line item with an increased quantity.
 * <p>
 * The primary implementation of this class is its {@link #equals(Object)}
 * method where the id and price are compared. 
 * 
 * @author cgreene
 * @since 13.1
 */
public class MAXCleanReceiptItem implements Serializable
{
    private static final long serialVersionUID = -8401036981592104439L;
    
    /**
     * The line item that this "item" wraps.
     */
    protected SaleReturnLineItemIfc saleLineItem;
    protected TransactionIfc transaction;

    /**
     * Constructor
     *
     * @param itemId
     * @param price
     */
    public MAXCleanReceiptItem(SaleReturnLineItemIfc saleLineItem, TransactionIfc transaction) {
    	 this.saleLineItem = saleLineItem;
    	 this.transaction = transaction;
	}

	/**
     * Compare the input CleanReceiptItem object with the current object.
     */
    @Override
    public boolean equals(Object obj)
    {
    	boolean isEqual = false;
    	if (obj instanceof MAXCleanReceiptItem)
        {
            MAXCleanReceiptItem c = (MAXCleanReceiptItem)obj;
            if(transaction instanceof TenderableTransactionIfc && c.getSaleReturnLineItem() instanceof MAXSaleReturnLineItem && getSaleReturnLineItem() instanceof MAXSaleReturnLineItem)
            isEqual=check((TenderableTransactionIfc)transaction,(MAXSaleReturnLineItem)c.getSaleReturnLineItem(),(MAXSaleReturnLineItem)getSaleReturnLineItem());
            //commented v14 code
            /*            ItemPriceIfc price = getPrice();

            if (getItemId().equals(c.getItemId())
                    && Util.isObjectEqual(c.getPrice().getSellingPrice(), price.getSellingPrice())
                    && Util.isObjectEqual(c.getPrice().getPermanentSellingPrice(), price.getPermanentSellingPrice())
                    && CodeConstantsIfc.CODE_UNDEFINED.equals(c.getPrice().getItemPriceOverrideReason().getCode())
                    && CodeConstantsIfc.CODE_UNDEFINED.equals(price.getItemPriceOverrideReason().getCode())
                    && (c.getPrice().getDiscountEligible() == price.getDiscountEligible())
                    && (c.getPrice().getEmployeeDiscountEligible() == price.getEmployeeDiscountEligible())
                    && (c.getPrice().getDamageDiscountEligible() == price.getDamageDiscountEligible())
                    // check that the discounts applied are the same
                    && (Util.isObjectEqual(c.getPrice().getItemDiscounts(), price.getItemDiscounts())
                    // they may not be due to the calculated amount but if they are of the same rule, then its ok
                    || (getAdvancedPricingRuleID() != null && getAdvancedPricingRuleID().equals(c.getAdvancedPricingRuleID())))
                    && Util.isObjectEqual(c.getPrice().getRestockingFee(), price.getRestockingFee())
                    && c.getPrice().getItemTax().getDefaultRate() == price.getItemTax().getDefaultRate()
                    && c.getPrice().getItemTax().getTaxable() == price.getItemTax().getTaxable()
                    && c.getPrice().getItemTax().getExternalTaxEnabled() == price.getItemTax().getExternalTaxEnabled()
                    && Util.isObjectEqual(c.getPrice().getItemTax().getTaxByTaxJurisdiction(), price.getItemTax().getTaxByTaxJurisdiction())
                    && isOrderItemStatusEqual(c.getSaleReturnLineItem().getOrderItemStatus(), getSaleReturnLineItem().getOrderItemStatus())
                    && c.getSaleReturnLineItem().getSendLabelCount() == getSaleReturnLineItem().getSendLabelCount()
                    && c.getSaleReturnLineItem().getItemQuantityDecimal().signum() == getSaleReturnLineItem().getItemQuantityDecimal().signum())
            {
                isEqual = true;
            }
*/        }
        return isEqual;
    }

	private boolean check(TenderableTransactionIfc transaction,
			MAXSaleReturnLineItemIfc ob1, MAXSaleReturnLineItemIfc ob2) {
		boolean isEqual1 = true;
		CurrencyIfc ssp = DomainGateway.getBaseCurrencyInstance();
		// if (ob1.getPromoDiscountForReceipt()!=null){
		// ssp=ob1.getExtendedSellingPrice().divide(ob1.getItemQuantityDecimal()).subtract(ob1.getPromoDiscountForReceipt());
		// }else{
		// System.out.println("Hello");
		ssp = ob1.getExtendedDiscountedSellingPrice().divide(
				ob1.getItemQuantityDecimal());
		if (!ob1.getItemPrice().getTotalTransactionDiscountsByAmount()
				.toString().equalsIgnoreCase("0.00")) {
			ssp = ssp.add(ob1.getItemPrice()
					.getTotalTransactionDiscountsByAmount()
					.divide(ob1.getItemQuantityDecimal()));
		}
		if (!ob1.getItemPrice().getTotalTransactionDiscountsByPercentage()
				.toString().equalsIgnoreCase("0.00")) {
			ssp = ssp.add(ob1.getItemPrice()
					.getTotalTransactionDiscountsByPercentage()
					.divide(ob1.getItemQuantityDecimal()));
		}
		
		// }
		// ssp =
		// ob1.getExtendedDiscountedSellingPrice().divide(ob1.getItemQuantityDecimal());

		// System.out.println(ob2.getItemDiscountTotal());
		// System.out.println(ob1.getItemDiscountTotal());
		CurrencyIfc ssp1 = ob2.getExtendedDiscountedSellingPrice().divide(
				ob2.getItemQuantityDecimal());
		if (ob2.getItemPrice().getItemDiscounts() != null
				&& ob2.getItemDiscountTotal().getDoubleValue() != ob1
						.getItemDiscountTotal().divide(ob1.getItemQuantityDecimal()).getDoubleValue()
				&& ob2.getPromoDiscountForReceipt() != null) {
			if (!((ob2.getItemDiscountTotal().subtract(ob1
					.getItemDiscountTotal().divide(ob1.getItemQuantityDecimal()))).toString().equalsIgnoreCase(
					"0.00")
					|| (ob2.getItemDiscountTotal().subtract(ob1
							.getItemDiscountTotal().divide(ob1.getItemQuantityDecimal()))).abs().toString().equalsIgnoreCase(
							"0.01")
					&& ob2.getItemID()
					.equalsIgnoreCase(ob1.getItemID()))) {
				double totalDiscountOnItem = 0;
				double promoDiscountOnItem = 0;
				// double discountByPercentage = 0;
				// double discountByAmount = 0;
				// double discountsExceptPromo = 0;
				if (ob2.getItemDiscountTotal() != null)
					totalDiscountOnItem = ob2.getItemDiscountTotal()
							.getDoubleValue();

				if (ob2.getPromoDiscountForReceipt() != null)
					promoDiscountOnItem = ob2.getPromoDiscountForReceipt()
							.getDoubleValue();

				double discountByPercentage = ob2.getItemPrice()
						.getTotalTransactionDiscountsByPercentage()
						.divide(ob2.getItemQuantityDecimal()).getDoubleValue();

				double discountByAmount = ob2.getItemPrice()
						.getTotalTransactionDiscountsByAmount()
						.divide(ob2.getItemQuantityDecimal()).getDoubleValue();

				double discountsExceptPromo = totalDiscountOnItem
						- promoDiscountOnItem;
				discountsExceptPromo = (double) Math
						.round(discountsExceptPromo * 100) / 100;
				double sumOfDiscByAmtAndPer = discountByPercentage
						+ discountByAmount;
				sumOfDiscByAmtAndPer = (double) Math
						.round(sumOfDiscByAmtAndPer * 100) / 100;
				// Changes by Gaurav for discount issue i.e. added if condition
				// : Start
				if (discountsExceptPromo != discountByPercentage
						&& discountsExceptPromo != discountByAmount
						&& discountsExceptPromo != (sumOfDiscByAmtAndPer))
					ssp1 = ssp1.add(ob2.getItemDiscountTotal().subtract(
							ob2.getPromoDiscountForReceipt()));
				// Changes by Gaurav for discount issue i.e. added if condition
				// : End
			}

		}
		// Changes for wrong discount bug by Gaurav and Manpreet: Added third OR
		// condition and abs() condition for employee discount bug
		/*Rev 1.10 start*/
		
		
		
		
		if (ob2.getItemDiscountTotal().getDoubleValue() == ob1
				.getItemDiscountTotal().getDoubleValue()
				|| (ob2.getItemDiscountTotal().divide(ob2.getItemQuantityDecimal()).subtract(ob1
						.getItemDiscountTotal().divide(ob1.getItemQuantityDecimal()))).abs().toString()
						.equalsIgnoreCase("0.00")
						|| (ob2.getItemDiscountTotal().divide(ob2.getItemQuantityDecimal()).subtract(ob1
						.getItemDiscountTotal().divide(ob1.getItemQuantityDecimal()))).abs().toString()
						.equalsIgnoreCase("0.01")
				|| (transaction instanceof VoidTransaction && ob2
						.getItemDiscountTotal()
						.divide(ob2.getItemQuantityDecimal()).getDoubleValue() == ob1
						.getItemDiscountTotal()
						.divide(ob1.getItemQuantityDecimal()).getDoubleValue())) {
			/*Rev 1.10 end*/
			if (!ob2.getItemPrice().getTotalTransactionDiscountsByAmount()
					.toString().equalsIgnoreCase("0.00")) {
				/*
				 * if(transaction instanceof VoidTransaction) {
				 */
				// Changes for wrong discount bug by Gaurav and Manpreet:
				// Divided transaction discount by qty
				ssp1 = ssp1.add(ob2.getItemPrice()
						.getTotalTransactionDiscountsByAmount()
						.divide(ob2.getItemQuantityDecimal()));
				;
				/*
				 * } else { ssp1=ssp1.add(ob2.getItemPrice().
				 * getTotalTransactionDiscountsByAmount()); }
				 */
			}
			if (!ob2.getItemPrice().getTotalTransactionDiscountsByPercentage()
					.toString().equalsIgnoreCase("0.00")) {
				/*
				 * if(transaction instanceof VoidTransaction) {
				 */
				// Changes for wrong discount bug by Gaurav and Manpreet:
				// Divided transaction discount by qty
				ssp1 = ssp1.add(ob2.getItemPrice()
						.getTotalTransactionDiscountsByPercentage()
						.divide(ob2.getItemQuantityDecimal()));
				/*
				 * } else { ssp1=ssp1.add(ob2.getItemPrice().
				 * getTotalTransactionDiscountsByPercentage()); }
				 */
			}
			if (ob2.getAdvancedPricingDiscount() != null && ob1.getAdvancedPricingDiscount() != null
					&& !Util.isObjectEqual(ob1.getAdvancedPricingDiscount().getDiscountAmount().divide(ob1.getItemQuantityDecimal()),
							ob2.getAdvancedPricingDiscount().getDiscountAmount()))
				if (ob2.getItemPrice().getItemTransactionDiscountAmount() != null
						&& !ob2.getItemPrice()
								.getItemTransactionDiscountAmount().toString()
								.equalsIgnoreCase("0.00")) {
					if (!ob2.getItemPrice()
							.getTotalTransactionDiscountsByAmount().toString()
							.equalsIgnoreCase("0.00")) {
						if (ob2.getPromoDiscountForReceipt() != null)// Changes
																		// for
																		// wrong
																		// discount
																		// bug
																		// by
																		// Gaurav
																		// and
																		// Manpreet:
																		// Added
																		// null
																		// check
																		// condition
						{
							if (!(ob2.getItemPrice()
									.getTotalTransactionDiscountsByAmount()
									.getDoubleValue() != 0 && ob2
									.getItemPrice()
									.getTotalTransactionDiscountsByPercentage()
									.getDoubleValue() != 0))
								ssp1 = ssp1
										.add(ob2.getItemDiscountTotal()
												.subtract(
														ob2.getPromoDiscountForReceipt())
												.subtract(
														ob2.getItemPrice()
																.getTotalTransactionDiscountsByAmount()));
						}
					}
					if (!ob2.getItemPrice()
							.getTotalTransactionDiscountsByPercentage()
							.toString().equalsIgnoreCase("0.00")) {
						if (ob2.getPromoDiscountForReceipt() != null)// Changes
																		// for
																		// wrong
																		// discount
																		// bug
																		// by
																		// Gaurav
																		// and
																		// Manpreet:
																		// Added
																		// null
																		// check
																		// condition
						{
							if (!(ob2.getItemPrice()
									.getTotalTransactionDiscountsByAmount()
									.getDoubleValue() != 0 && ob2
									.getItemPrice()
									.getTotalTransactionDiscountsByPercentage()
									.getDoubleValue() != 0))
								ssp1 = ssp1
										.add(ob2.getItemDiscountTotal()
												.subtract(
														ob2.getPromoDiscountForReceipt())
												.subtract(
														ob2.getItemPrice()
																.getTotalTransactionDiscountsByPercentage()));
						}

					}
				} else {
					// Changes For Issue reported from prod after patch
					// deployment for TIC Customer by Manpreet :Start
					if (ob2.getItemDiscountTotal() != null
							&& ob2.getPromoDiscountForReceipt() != null) {
						ssp1 = ssp1.add(ob2.getItemDiscountTotal().subtract(
								ob2.getPromoDiscountForReceipt()));
					}
					// Changes For Issue reported from prod after patch
					// deployment for TIC Customer by Manpreet :End
				}
		} else {

		}
		// ADDED BY VAIBHAV

		BigDecimal sp = new BigDecimal(ssp.getDoubleValue()).setScale(1,
				BigDecimal.ROUND_HALF_UP);
		;
		BigDecimal sp1 = new BigDecimal(ssp1.getDoubleValue()).setScale(1,
				BigDecimal.ROUND_HALF_UP);
		;

		try {
			if (!Util.isObjectEqual(ob1.getPLUItem(), ob2.getPLUItem())) {
				if (!Util.isObjectEqual(ob1.getItemID(), ob2.getItemID()))

					return false;
			}
			if (!Util.isObjectEqual(ob1.getRegistry(), ob2.getRegistry()))
				return false;
			if (((MAXSaleReturnLineItem) ob1).isSourceAvailable() != ((MAXSaleReturnLineItem) ob2)
					.isSourceAvailable())
				return false;
			/** MAX Rev 1.7 Change : Start **/
			if (ob1.getItemSerial() != null && ob2.getItemSerial() != null)
				if (ob1.getItemSerial().equalsIgnoreCase(ob2.getItemSerial()))
					return false;
			/** MAX Rev 1.7 Change : End **/
			if (ob1.isFromTransaction() != ob2.isFromTransaction())
				return false;
			if (ob1.isGiftReceiptItem() != ob2.isGiftReceiptItem())
				return false;
			/*if (!Util.isObjectEqual(ob1.getOrderItemStatus(),
					ob2.getOrderItemStatus()))
				return false;*/
			if (ob1.getSalesAssociateModifiedFlag() != ob2
					.getSalesAssociateModifiedFlag())
				return false;
			if (!Util.isObjectEqual(ob1.getLineReference(),
					ob2.getLineReference()))
				return false;
			if (!(ob1.getOrderLineReference() == ob2.getOrderLineReference()))
				return false;
			if (!(ob1.getKitHeaderReference() == ob2.getKitHeaderReference()))
				return false;
			if (!(ob1.getEntryMethod() == ob2.getEntryMethod()))
				return false;
			if (ob1.isRelatedItemReturnable() != ob2.isRelatedItemReturnable())
				return false;
			if (ob1.getRelatedItemSequenceNumber() != ob2
					.getRelatedItemSequenceNumber())
				return false;
			if (ob1.isRelatedItemDeleteable() != ob2.isRelatedItemDeleteable())
				return false;
			if (ob1.getItemPrice().getItemPriceOverrideReasonCode() != ob2
					.getItemPrice().getItemPriceOverrideReasonCode())
				return false;
			if (ob1.getItemPrice().getItemPriceOverrideReasonCode() == ob2
					.getItemPrice().getItemPriceOverrideReasonCode())
				if (!Util.isObjectEqual(ob1.getSellingPrice(),
						ob2.getSellingPrice()))
					return false;
			if (ob1.getReturnItem() != null && ob2.getReturnItem() != null)
				if (!((MAXReturnItem) ob1.getReturnItem())
						.compare((MAXReturnItem) ob2.getReturnItem()))
					return false;
			if (ob1.getSalesAssociateModifiedFlag() == ob2
					.getSalesAssociateModifiedFlag())
				if (!Util.isObjectEqual(ob1.getSalesAssociate(),
						ob2.getSalesAssociate()))
					return false;
			/*
			 * if(ob2.getPromoDiscountForReceipt()!=null)
			 * ob2.getAdvancedPricingDiscount
			 * ().setDiscountAmount(ob2.getPromoDiscountForReceipt());
			 * if(ob1.getPromoDiscountForReceipt()!=null)
			 * ob1.getAdvancedPricingDiscount
			 * ().setDiscountAmount(ob2.getPromoDiscountForReceipt());
			 */

			if (Util.isObjectEqual(ob1.getAdvancedPricingDiscount(),
					ob2.getAdvancedPricingDiscount()))
				if (!isEqual1)
					return false;
			if (!Util.isObjectEqual(sp, sp1))
				// ADDED BY VAIBHAV
				if (sp.subtract(sp1).abs().doubleValue() > 0.1)
					return false;
				else if (!verifyDiscount(ob1, ob2))
					return false;
			if (Util.isObjectEqual(sp, sp1))
				if (!verifyDiscount(ob1, ob2))
					return false;
			if (!verifySendDetails(transaction, ob1, ob2))
				return false;
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public boolean verifyDiscount(MAXSaleReturnLineItemIfc itm1,
			MAXSaleReturnLineItemIfc itm2) {

		ItemDiscountStrategyIfc[] discounts = itm1.getItemPrice()
				.getItemDiscounts();
		// BigDecimal itm1qty = itm1.getItemQuantityDecimal();
		ItemDiscountStrategyIfc[] discounts1 = itm2.getItemPrice()
				.getItemDiscounts();
		// BigDecimal itm2qty = itm2.getItemQuantityDecimal();
		// if(itm1qty!=itm2qty){
		// itm1.getItemDiscountTotal().divide(itm1qty);
		// }
		int count = 0;
		// CurrencyIfc discamt1=DomainGateway.getBaseCurrencyInstance();
		// CurrencyIfc discamt2=DomainGateway.getBaseCurrencyInstance();
		if (discounts.length != discounts1.length)
			return false;
		if (discounts.length == discounts1.length && discounts.length > 0) {
			for (int i = 0; i < discounts.length; i++) {
				DiscountRuleIfc d = discounts[i];
				DiscountRuleIfc d1 = discounts1[i];
				if (d.getAccountingMethod() == d1.getAccountingMethod()
						&& d.getDiscountScope() == d1.getDiscountScope()
						&& d.getReasonCode() == d1.getReasonCode()) {
					/** MAX Rev 1.5 Change : Start **/
					// Added by vaibhav
					CurrencyIfc aamt = DomainGateway.getBaseCurrencyInstance();
					CurrencyIfc aamt1 = DomainGateway.getBaseCurrencyInstance();
					// Changes for wrong discount bug by Gaurav and Manpreet:
					// Second && condition if promo discount for receipt not
					// equals 0.0
					if (itm1.getPromoDiscountForReceipt() != null
							&& !(itm1.getPromoDiscountForReceipt()
									.getDoubleValue() == 0.0)) {
						// discamt1=d.getDiscountAmount().divide(itm1.getItemQuantityDecimal());
						// discamt2=d1.getDiscountAmount().divide(itm2.getItemQuantityDecimal());
						// if(i==discounts.length-1){
						// aamt=itm1.getPromoDiscountForReceipt().divide(itm1.getItemQuantityDecimal()).subtract(discamt1);
						// }else
						aamt = itm1.getPromoDiscountForReceipt().divide(
								itm1.getItemQuantityDecimal());
						;
					} else {
						aamt = d.getDiscountAmount().divide(
								itm1.getItemQuantityDecimal());
					}
					if (itm2.getPromoDiscountForReceipt() != null) {
						// if(i==discounts.length-1 &&
						// d1.getDiscountAmount()!=null){
						// aamt1=itm2.getPromoDiscountForReceipt().add(discamt2);
						// }else{
						aamt1 = itm2.getPromoDiscountForReceipt();
						// aamt1=itm2.getPromoDiscountForReceipt().divide(itm2.getItemQuantityDecimal());
						// }

					} else {
						aamt1 = d1.getDiscountAmount().divide(
								itm2.getItemQuantityDecimal());
					}
					BigDecimal amt = new BigDecimal(Math.round(aamt
							.getDoubleValue()));
					BigDecimal amt1 = new BigDecimal(Math.round(aamt1
							.getDoubleValue()));
					/** MAX REv 1.5 Change : End **/
					if (Util.isObjectEqual(amt, amt1))
						count++;
				}
			}
		}

		if (discounts.length == count)
			return true;
		return false;

	}

	private boolean verifySendDetails(TenderableTransactionIfc transaction,
			SaleReturnLineItemIfc ob1, SaleReturnLineItemIfc ob2) {
		boolean flag = false;
		SendPackageLineItemIfc[] sendPackages=null;
		if(transaction.getTransactionTotals() instanceof MAXTransactionTotalsIfc && ((MAXTransactionTotalsIfc)transaction
				.getTransactionTotals()).getSendPackages()!=null){
		sendPackages =((MAXTransactionTotalsIfc)transaction
				.getTransactionTotals()).getSendPackages();
		SendPackageLineItemIfc[] sendPackage1 = new SendPackageLineItemIfc[sendPackages.length];
		int sendLabel1 = ob1.getSendLabelCount();
		int sendLabel2 = ob2.getSendLabelCount();
		if (sendLabel1 == sendLabel2)
			return true;

		if (sendLabel1 > 0 && sendLabel1 > 0) {
			SendPackageLineItemIfc sl1 = sendPackages[sendLabel1 - 1];
			SendPackageLineItemIfc sl2 = sendPackages[sendLabel2 - 1];
			if (sl1 == sl2)
				return true;
			else {
				if (sl1.equals(sl2)) {
					sendLabel1 = sendLabel1 > sendLabel2 ? sendLabel2
							: sendLabel1;
					ob1.setSendLabelCount(sendLabel1);
					ob2.setSendLabelCount(sendLabel1);
					((MAXTransactionTotals) transaction.getTransactionTotals())
							.modifySendPackage(sendLabel2, null);
					// sendPackages[sendLabel2] = null;
					// transaction.getTransactionTotals().setSendPackages(sendPackages);
					// sendModified=true;
					return true;
				}
				return false;
			}
		}
	}else return true;
		return false;
	}

    /**
     * Determine if the OrderItemStatusIfc objects are equal.  If the deposit
     * amounts are within one cent, they will be considered matching.
     *
     * @param obj1
     * @param obj2
     * @return true if OrderItemStatus objects match
     * @since 14.1
     */
    public boolean isOrderItemStatusEqual (OrderItemStatusIfc obj1, OrderItemStatusIfc obj2)
    {
        boolean isEqual;

        if (Util.isObjectEqual(obj1.getStatus(), obj2.getStatus())
                && Util.isObjectEqual(obj1.getQuantityPickedUp(), obj2.getQuantityPickedUp())
                && Util.isObjectEqual(obj1.getQuantityPicked(), obj2.getQuantityPicked())
                && Util.isObjectEqual(obj1.getQuantityShipped(), obj2.getQuantityShipped())
                && (Util.isObjectEqual(obj1.getDepositAmount(), obj2.getDepositAmount())
                    || isDepositAmountEqual(obj1.getDepositAmount(), obj2.getDepositAmount()))
                && Util.isObjectEqual(obj1.getReference(), obj2.getReference())
                && Util.isObjectEqual(obj1.getItemDispositionCode(), obj2.getItemDispositionCode())
                && Util.isObjectEqual(obj1.getPickupDate(), obj2.getPickupDate())
                && Util.isObjectEqual(obj1.getDeliveryDetails(), obj2.getDeliveryDetails()))
        {
            isEqual = true;
        }
        else
        {
            isEqual = false;
        }
        return isEqual;
    }    
    
    /**
     * Returns true if the two objects are within 
     * one cent apart.  
     *
     * @param obj1
     * @param obj2
     * @return boolean true if the amount differ by a hundredth
     * @since 14.1
     */
    public boolean isDepositAmountEqual(CurrencyIfc deposit1, CurrencyIfc deposit2)
    {
        if (Util.isObjectEqual(deposit1, deposit2))
            return true;
        double difference = deposit1.subtract(deposit2).abs().getDoubleValue();
        return (difference <= 0.01);
    }    
    
    /**
     * returns the item id
     *
     * @return item id
     */
    public String getItemId()
    {
        return getSaleReturnLineItem().getItemID();
    }

    /**
     * Return the rule id associated with this line item.
     * 
     * @return
     */
    public String getAdvancedPricingRuleID()
    {
        String ruleId = null;
        if (saleLineItem instanceof SaleReturnLineItem)
        {
            ruleId = ((SaleReturnLineItem)saleLineItem).getAdvancedPricingRuleID();
        }
        return ruleId;
    }

    /**
     * returns the item price
     *
     * @return price
     */
    public ItemPriceIfc getPrice()
    {
        return getSaleReturnLineItem().getItemPrice();
    }

    /**
     * returns the SaleReturnLineItemIfc
     *
     * @return price
     */
    public SaleReturnLineItemIfc getSaleReturnLineItem()
    {
        return saleLineItem;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return getItemId().hashCode();
    }
}
