/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.sale.validate;

import java.util.Iterator;

import max.retail.stores.domain.discount.MAXDiscountRuleConstantsIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountStrategy;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageStrategy;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
/**
 * This site removes the capillary coupon discounts if the user returns to sell item screen
 * while applying the said discount.
 * 
 * @author aakash.gupta
 * @since April-15th,2015
 *
 */
public class MAXRemoveCapillaryDiscountsSite extends PosSiteActionAdapter{

	/**
	 * serialVersionUID long
	 */
	private static final long serialVersionUID = -8715450071508919456L;

	public void arrive(BusIfc bus){
		SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();
		
		if (cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			MAXSaleReturnTransactionIfc trans = (MAXSaleReturnTransactionIfc) cargo.getTransaction();
			if(!trans.getCapillaryCouponsApplied().isEmpty()){
				trans.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
												 MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);
				trans.clearTransactionDiscounts(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
												MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON);

				MAXSaleReturnLineItemIfc lineItem = null;
				Iterator i = trans.getItemContainerProxy().getLineItemsIterator();
				while (i.hasNext()) {
					lineItem = (MAXSaleReturnLineItemIfc) i.next();
					ItemDiscountByAmountStrategy[] igy = new ItemDiscountByAmountStrategy[
					                                                                      (lineItem.getItemPrice())
					                                                                      .getItemDiscountsByAmount().length
					                                                                      ];
					if (igy.length > 0) {
						for (int k = 0; k < igy.length; k++) {
							lineItem.clearItemDiscountsByAmount(DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT,
																MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON,
																false);
						}
					}
					ItemDiscountByPercentageStrategy[] pgy = new ItemDiscountByPercentageStrategy[
					                                                                              ((lineItem.getItemPrice())
					                                                                        	  .getItemDiscountsByPercentage().length)
					                                                                               ];
					if (pgy.length > 0) {
						for (int k = 0; k < pgy.length; k++) {
							lineItem.clearItemDiscountsByPercentage(DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE,
																	MAXDiscountRuleConstantsIfc.ASSIGNMENT_CAPILLARYCOUPON,
																	false);
						}
					}
				}
				trans.removeCapillaryCouponsApplied();
				trans.getTransactionTotals().updateTransactionTotals(
															trans.getItemContainerProxy().getLineItems(),
															trans.getItemContainerProxy().getTransactionDiscounts(),
															trans.getItemContainerProxy().getTransactionTax()
															);
			}
		}
		bus.mail(CommonLetterIfc.UNDO);
	}
}