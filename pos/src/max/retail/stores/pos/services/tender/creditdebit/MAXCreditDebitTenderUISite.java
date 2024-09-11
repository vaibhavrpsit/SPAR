/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.0  22/May/2013	Jyoti Rawal, Initial Draft: Changes for Credit Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Puts up UI prompting the user to enter a card number  
 */
public class MAXCreditDebitTenderUISite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5294577122382435720L;

	/**
     * Arrive at site
     * @param bus Bus that is arriving
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#arrive(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        // Check first for eventual card already swiped
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().put("TENDER_TYPE", TenderTypeEnum.CREDIT);
        
        try {
			cargo.getCurrentTransactionADO().validateRefundLimits(
					cargo.getTenderAttributes(), true, true);
		} catch (TenderException e) {
			TenderErrorCodeEnum error = e.getErrorCode();
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
			if (error == TenderErrorCodeEnum.OVERTENDER_ILLEGAL) {
				UIUtilities.setDialogModel(ui, 1, "OvertenderNotAllowed", null,
						"Invalid");
				return;
			}
			if (error == TenderErrorCodeEnum.MAX_LIMIT_VIOLATED) {
				int buttons[] = { 1, 2 };
				String letters[] = { "Override", "Invalid" };
				String args[] = { TenderTypeEnum.CREDIT.toString() };
				UIUtilities.setDialogModel(ui, 0, "AmountExceedsMaximum", args,
						buttons, letters);
				return;
			}
			if (error == TenderErrorCodeEnum.MIN_LIMIT_VIOLATED) {
				int buttons[] = { 1, 2 };
				String letters[] = { "Override", "Invalid" };
				String args[] = { TenderTypeEnum.CREDIT.toString() };
				UIUtilities.setDialogModel(ui, 0, "AmountLessThanMinimum",
						args, buttons, letters);
				return;
			}
		}
        MSRModel msr = cargo.getPreTenderMSRModel();
        if (msr != null)
        {
            cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, msr);
            // remove manually entered number
            cargo.getTenderAttributes().remove(TenderConstants.NUMBER);
            bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
        }
        else if (cargo.getNextTender() != null)
        {
            cargo.setTenderAttributes(cargo.getNextTender().getTenderAttributes());
            bus.mail(new Letter("Done"), BusIfc.CURRENT);
        }
        else
        {    
            UtilityManagerIfc utility =
                (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String message = utility.retrieveCommonText("swipeNowMessage",
                                                        "You may swipe your card now.");

            // put up UI asking for Card number
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD, new POSBaseBeanModel());
            
         POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            try
            {
                CIDAction clear = new CIDAction(CIDAction.CPOI_MESSAGE_SCREEN_NAME, CIDAction.CLEAR);
                pda.cidScreenPerformAction(clear);
                CIDAction action = new CIDAction(CIDAction.CPOI_MESSAGE_SCREEN_NAME, CIDAction.SET_MESSAGE);
                action.setStringValue(message);
                pda.cidScreenPerformAction(action);
                CIDAction show = new CIDAction(CIDAction.CPOI_MESSAGE_SCREEN_NAME, CIDAction.SHOW);
                pda.cidScreenPerformAction(show);
            }
            catch (DeviceException e)
            {
                logger.warn(
                            "Error while using customer interface device: " + e.getMessage() + "");
            }
        } // end of if
        
    }
    
    /**
     * Leave the site
     * @param bus bus leaving the site
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
    public void depart(BusIfc bus)
    {
        if (bus.getCurrentLetter().getName().equals("Next"))
        {
            // Get information from UI
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            POSBaseBeanModel model = (POSBaseBeanModel)ui.getModel();
            PromptAndResponseModel parModel = model.getPromptAndResponseModel();
            if (parModel.isSwiped())
            {
                // is swiped save the whole MSR model.
                cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, parModel.getMSRModel());
                // remove manually entered number
                cargo.getTenderAttributes().remove(TenderConstants.NUMBER);               
            }            
            else if (cargo.getPreTenderMSRModel() == null)
            {
                // if manually entered, we only have the card number.
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
                // remove MSR (may have been previously swiped)
                cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
            }
            // set pre tender msr model to null
            // we dont want to come back into credit/debit automatically if we have problems.
            cargo.setPreTenderMSRModel(null);            
        }
    }
}
