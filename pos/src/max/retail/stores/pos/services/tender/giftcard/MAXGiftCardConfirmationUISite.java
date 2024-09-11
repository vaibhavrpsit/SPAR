/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev	1.0 	Dec 20, 2016		Mansi Goel		Changes for Gift Card FES	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.giftcard;

import max.retail.stores.pos.device.MAXPOSDeviceActions;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
     This method displays the gift card charge confirmation.
     $Revision: 4$
 **/
//--------------------------------------------------------------------------
public class MAXGiftCardConfirmationUISite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8137886420056241724L;

	//----------------------------------------------------------------------
    /**
        This method displays the gift card confirmation screen.
        @param bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {            
        MAXPOSDeviceActions pda = new MAXPOSDeviceActions((SessionBusIfc)bus);
        try
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            TenderADOIfc tender = null;
            String message = utility.retrieveCommonText("giftCardConfirmation",
                                                        "Authorize Charge Amount:\n {0}\n Gift Card # {1}");
            TenderCargo cargo = (TenderCargo)bus.getCargo();   
            String cardNum = ((String)cargo.getTenderAttributes().get(TenderConstants.NUMBER));
            String cardNumber = "";
            if(cardNum.length()>16){
            	cardNumber = cardNum.substring(0, 16);
            }
            // if there is no card number, get it from the msr model
            if (cardNumber == null)
            {
                MSRModel msrModel = (MSRModel)cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
                cardNumber = msrModel.getAccountNumber();
            }
            try
            {
                TenderFactoryIfc factory = (TenderFactoryIfc)ADOFactoryComplex.getFactory("factory.tender");
                tender = factory.createTender(cargo.getTenderAttributes());
                ((TenderGiftCard)tender.toLegacy()).setGiftCard(cargo.getGiftCard());
            }
            catch (ADOException adoe)
            {
                adoe.printStackTrace();
            }
            String amountString = null;
            if (tender == null)
            {
                amountString = ((String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT));
            }
            else
            {
                amountString = tender.getAmount().toFormattedString();
            }

            String[] parms = {amountString, 
                              cardNumber.substring(cardNumber.length()-4)};
            message = LocaleUtilities.formatComplexMessage(message,parms);
            String cancelButtonLabel = utility.retrieveCommonText("Cancel", "Cancel");
            String okButtonLabel = utility.retrieveCommonText("OK", "Ok");
            
            // make sure the device is there and working
            if (pda.isFormOnline().equals(Boolean.TRUE))
            {
                CIDAction resetAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.RESET);
                pda.cidScreenPerformAction(resetAction);           
                
                CIDAction messageAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SET_MESSAGE);
                messageAction.setStringValue(message);
                pda.cidScreenPerformAction(messageAction);
                
                CIDAction b1LabelAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SET_BUTTON1_LABEL);
                b1LabelAction.setStringValue(okButtonLabel);
                pda.cidScreenPerformAction(b1LabelAction); 
                
                CIDAction b1ActionAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SET_BUTTON1_ACTION);
                b1ActionAction.setStringValue("Success");
                pda.cidScreenPerformAction(b1ActionAction);   
                
                CIDAction b2LabelAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SET_BUTTON2_LABEL);
                b2LabelAction.setStringValue(cancelButtonLabel);
                pda.cidScreenPerformAction(b2LabelAction);
                
                CIDAction b2ActionAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SET_BUTTON2_ACTION);
                b2ActionAction.setStringValue("Cancel");
                pda.cidScreenPerformAction(b2ActionAction);
                
                CIDAction showAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.SHOW);
                pda.cidScreenPerformAction(showAction);
                
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.REQUEST_CUSTOMER_VERIFY, new POSBaseBeanModel());
            }
            else
            {
                //otherwise just mail letter.
                bus.mail(new Letter("Success"), BusIfc.CURRENT);
            }
        }
        catch(Exception e)
        {
            logger.warn( e);
            // convert to gift card anyway
            bus.mail(new Letter("Success"), BusIfc.CURRENT);
        }        
    }
    
    //----------------------------------------------------------------------
    /**
        Clear the message.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.SiteActionIfc#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);            
            CIDAction clearAction = new CIDAction(CIDAction.CPOI_TWO_BUTTON_SCREEN_NAME, CIDAction.CLEAR);
            pda.cidScreenPerformAction(clearAction);
        }
        catch(Exception e)
        {
            logger.warn(e);
        }
        
        if(bus.getCurrentLetter().getName().equalsIgnoreCase("Cancel"))
        {
            // nullify swipe anytime card when canceling
            ((TenderCargo)bus.getCargo()).setPreTenderMSRModel(null);
        }
    }
}
