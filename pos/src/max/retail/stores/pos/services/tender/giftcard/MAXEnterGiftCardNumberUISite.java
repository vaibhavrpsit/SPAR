/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.giftcard;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * @author blj
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class MAXEnterGiftCardNumberUISite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -1605845995622006276L;

	//----------------------------------------------------------------------
    /**
       Displays the GIFT_CARD screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Check first for eventual card already swiped
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        MSRModel msr = cargo.getPreTenderMSRModel();
        POSBaseBeanModel model  = new POSBaseBeanModel();
        if (msr != null)
        {
            cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, msr);
            // remove manually entered number
            cargo.getTenderAttributes().remove(TenderConstants.NUMBER);
            bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
        }
        else
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            PromptAndResponseModel pModel = new PromptAndResponseModel();
		boolean enableResponse = true;
    	ParameterManagerIfc pm = (ParameterManagerIfc) bus
				.getManager(ParameterManagerIfc.TYPE);
    	try {
			enableResponse = pm.getBooleanValue("ManualEntryEnable").booleanValue();
		} catch (ParameterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	pModel.setResponseEnabled(enableResponse);
		model.setPromptAndResponseModel(pModel);  
			  
            ui.showScreen(POSUIManagerIfc.GIFT_CARD, model);
            
            UtilityManagerIfc utility =
                (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String message = utility.retrieveCommonText("swipeNowMessage",
                                                        "You may swipe your card now.");
            
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
        }
    }
    /* Get the card number from the ui and put into the tenderAttributes
     * @param BusIfc bus
     * @see com.extendyourstore.foundation.tour.application.SiteActionAdapter#depart(com.extendyourstore.foundation.tour.ifc.BusIfc)
     */
   /* public void depart(BusIfc bus)
    {
        // Get information from UI
        
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel();
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();
        
        if (parModel != null) 
        {
        
            if (parModel.isSwiped())
            {
                // Put the model in tenderAttributes
                cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, parModel.getMSRModel());
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_MAGSWIPE);
            }
            else if (parModel.isScanned())
            {
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_AUTO);
            }
            else if (cargo.getPreTenderMSRModel() == null)
            {
                // if manually entered, we only have the card number.
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, ui.getInput());
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, TenderLineItemIfc.ENTRY_METHOD_MANUAL);
                // remove MSR (may have been previously swiped)
                cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
            }
            else
            {  
                MSRModel msr = (MSRModel)cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, msr.getAccountNumber());
            }
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setCardNumber(cargo.getTenderAttributes().get(TenderConstants.NUMBER).toString());
            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_INQUIRY);
            cargo.setGiftCard(giftCard);

            // This was added to force processing past the off-line condition when performing
            // an inquiry during tendering.  The current tender amount is being set on the available
            giftCard.setInquireAmountForTender(true);
            String tenderAmount = cargo.getTenderAttributes().get(TenderConstants.AMOUNT).toString();
            giftCard.setBalanceForInquiryFailure(DomainGateway.getBaseCurrencyInstance(tenderAmount));
        }
        
        // set pre tender msr model to null
        // we dont want to come back into credit/debit automatically if we have problems.
        cargo.setPreTenderMSRModel(null);            

     }
*/
}
