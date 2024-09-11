/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		24/04/2013		Initial Draft: Changes for Gift Card
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.pricing;

import java.math.BigDecimal;

import max.retail.stores.domain.employee.MAXRoleFunctionIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.pricing.AbstractPercentEnteredAisle;
public class MAXGCPercentEnteredAisle extends AbstractPercentEnteredAisle{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -6452913266317430399L;

	public void traverse(BusIfc bus)
	    {
		MAXPricingCargo cargo = (MAXPricingCargo) bus.getCargo();
		//POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		
		//String dialog = null;
		String letter = CommonLetterIfc.OK;
		//String pmValue = null;
		int pmPercent = 0;
		BigDecimal response = new BigDecimal("0.00");
		/**
		 * Rev 1.0 changes start here
		 */
		ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
		try {
			pmPercent = pm.getIntegerValue("GCDiscountAmountPercent").intValue();
			response = new BigDecimal(pmPercent).setScale(2);
			cargo.setAccessFunctionID(MAXRoleFunctionIfc.PRICE_DISCOUNT);
		} catch (ParameterException e) {
			if (logger.isInfoEnabled())
				logger.info("MAXGCPercentEnteredSite.arrive(), cannot find GCDiscountAmountPercent paremeter.");
		}
//		cargo.setGCDiscountPercent(pmPercent);
		response = response.divide(new BigDecimal(100.0), BigDecimal.ROUND_HALF_UP);
//		SaleReturnLineItemIfc[] lineItems = cargo.getItems();
//		if( (lineItems != null) && (lineItems.length > 0) &&
//                (lineItems[0].getPLUItem() instanceof GiftCardPLUItemIfc )){
//			for (int i = 0; i < lineItems.length; i++) {
//				SaleReturnLineItemIfc srli = lineItems[i];
//		// Chop off the potential long values caused by BigDecimal.
		if (response.toString().length() > 5) {
			BigDecimal scaleOne = new BigDecimal(1);
			response = response.divide(scaleOne, 2, BigDecimal.ROUND_HALF_UP);
		}
//		ItemDiscountByPercentageIfc sgy = null;
//		sgy = createDiscountStrategy(cargo, response, null);
//		// check to see if adding this discount will make the item's
//		// price go
//		// negative (or positive if it is a return item)
//		SaleReturnLineItemIfc clone = (SaleReturnLineItemIfc) srli.clone();
////		clearDiscountsByPercentage(clone);
//		clone.addItemDiscount(sgy);
//		clone.calculateLineItemPrice();
//		}
//		}
//		cargo.setDoDiscount(true);
		
		TransactionDiscountByPercentageIfc percentDiscount = DomainGateway.getFactory().getTransactionDiscountByPercentageInstance();
		percentDiscount.setDiscountRate(response);
		percentDiscount.setEnabled(true);
		if(cargo.getTransaction()!=null){
		((SaleReturnTransaction)cargo.getTransaction()).addTransactionDiscount(percentDiscount);
		cargo.setDiscount(percentDiscount);
		}
		bus.mail(letter, BusIfc.CURRENT);
	    }

	protected boolean isEligibleForDiscount(SaleReturnLineItemIfc srli) {
		// TODO Auto-generated method stub
		return true;
	}

	protected String getMaxPercentParameterName() {
		// TODO Auto-generated method stub
		return null;
	}
			
}
