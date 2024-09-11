/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  11/May/2013	Jyoti Rawal, Initial Draft: Changes for Hire Purchase Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.purchaseorder;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 *  Validate Hire Purchase OverTendering  
 */
public class MAXHPLimitActionSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -153524946491994800L;
	

	/* (non-Javadoc)
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
	public void arrive(BusIfc bus) {
		// get tender attributes from cargo and add tender type
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		 boolean overtenderableHP = false;
		POSUIManagerIfc ui = (POSUIManagerIfc) bus
				.getManager(UIManagerIfc.TYPE);
		ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
		String[] invalidOvertenderTenders = null;
		try {
			invalidOvertenderTenders = pm
					.getStringValues("TendersNotAllowedForOvertender");
		} catch (ParameterException e) {
			 if (logger.isInfoEnabled()) logger.info(
             "MAXHPLimitActionSite.arrive(), cannot find TendersNotAllowedForOvertender parameter.");
		}
		for (int i = 0; i < invalidOvertenderTenders.length; i++) {
			if (invalidOvertenderTenders[i].equals("HirePurchase")) {
				CurrencyIfc balanceDue = cargo.getCurrentTransactionADO()
						.getBalanceDue();
				try {
					CurrencyIfc tenderAmount = parseAmount((String) cargo
							.getTenderAttributes().get(TenderConstants.AMOUNT));
					if (tenderAmount.compareTo(balanceDue) == CurrencyIfc.GREATER_THAN) {
						
						overtenderableHP = true;
						
					} else {
						overtenderableHP = false;
						//bus.mail(new Letter("Success"), BusIfc.CURRENT);
					}
				} catch (TenderException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		if(overtenderableHP == true){
			DialogBeanModel dialogModel = new DialogBeanModel();
			dialogModel.setResourceID("OvertenderNotAllowed");
			dialogModel.setType(DialogScreensIfc.ERROR);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,
					"Undo");
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
					dialogModel);
			return;
		}else{
			bus.mail(new Letter("Success"), BusIfc.CURRENT);
		}

	}
    
   

	/**
	 * @param amountString
	 * @return
	 * @throws TenderException
	 */
	protected CurrencyIfc parseAmount(String amountString)
			throws TenderException {
		CurrencyIfc amount = null;
		try {
			amount = DomainGateway.getBaseCurrencyInstance(amountString);
		} catch (Exception e) {
			throw new TenderException("Attempted to parse amount string",
					TenderErrorCodeEnum.INVALID_AMOUNT, e);
		}
		return amount;
	}

    
}
