/********************************************************************************
*   
*	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
*	
*
*	Rev 1.1		Apr 18, 2017		Mansi Goel		Changes to resolve Emp discount is not applied on items which are part
*													of source and quantity is more than required in rule 
*	Rev	1.0 	Mar 20, 2017		Mansi Goel		Changes to resolve Emp Discount issue for promotional discounted items	
*
********************************************************************************/

package max.retail.stores.domain.discount;

import java.util.Iterator;
import java.util.Vector;

import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import oracle.retail.stores.domain.discount.StandardDiscountCalculation;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;


public class MAXStandardDiscountCalculation extends StandardDiscountCalculation {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7634307101474308801L;

	/**
	 * Removes non-employee-discount-eligible items from list.
	 *
	 * @param lineItems
	 *            existing Vector of line items
	 * @return discountableLineItems items eligible for employee discount
	 */
	public Vector removeNonEmployeeDiscountableItems(Vector<AbstractTransactionLineItemIfc> lineItems) {
		Vector discountableItems = null;
		if (lineItems != null) {
			discountableItems = (Vector) lineItems.clone();
			Iterator i = discountableItems.iterator();
			SaleReturnLineItemIfc li = null;

			while (i.hasNext()) {
				li = (SaleReturnLineItemIfc) i.next();
				if (li instanceof MAXSaleReturnLineItemIfc) {
					//Changes for Rev 1.1 : Starts
					if (!li.getPLUItem().getItemClassification().getEmployeeDiscountAllowedFlag()
							|| li.isReturnLineItem() || (((MAXSaleReturnLineItemIfc) li).getBestDealWinnerName() != null
							&& ((MAXSaleReturnLineItemIfc) li).getAdvancedPricingDiscount() != null)) {
					//Changes for Rev 1.1 : Ends
						i.remove();
					}
				} else {
					if (!li.getPLUItem().getItemClassification().getEmployeeDiscountAllowedFlag()
							|| (li.isReturnLineItem()
									&& (li.isFromTransaction() || li.getReturnItem().isNonRetrievedReceiptedItem()))
							|| li.hasExternalPricing()
							|| (li.isPickupCancelLineItem() && !li.isInStorePriceDuringPickup())) {
						i.remove();
					}
				}
			}
		}

		return discountableItems;
	}
}
