/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *
 * Rev 1.1  Apr 13, 2017 10:06:04 AM Nadia Arora
 * Paytm Integration
 *		
 * Rev 1.0  Aug 12, 2015 2:06:04 PM Priyanka Singh
 * Initial revision.
 * Resolution for port in 14 version
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.pos.receipt.blueprint.MAXParameterConstantsIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Puts up UI prompting the user to enter a card number
 */
public class MAXPineLabCreditDebitTenderUISite extends PosSiteActionAdapter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Arrive at site
	 * 
	 * @param bus
	 *            Bus that is arriving
	 * */
	public void arrive(BusIfc bus) 
	{
		// Check first for eventual card already swiped
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MSRModel msr = cargo.getPreTenderMSRModel();
		boolean creditOnlineFlowFlag = ((MAXTenderCargo)cargo).isCreditOnlineFlow();
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
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			String message = utility.retrieveCommonText("swipeNowMessage", "You may swipe your card now.");

			// put up UI asking for Card number
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			// MAX Change for Rev 1.9: Start
			String letter = bus.getCurrentLetter().getName();			
			if ((letter != null))
			{
				if (letter.equals("CreditDebit"))
				{
					ui.showScreen(MAXPOSUIManagerIfc.CREDIT_DEBIT_ONLINE_OFFLINE_SWIPE, new POSBaseBeanModel());
				}
				else if (creditOnlineFlowFlag)
				{					int i=1;
					ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
					POSBaseBeanModel model = new POSBaseBeanModel();
					NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
					model.setLocalButtonBeanModel(localModel);
//					boolean isPaytmEnabled = Boolean.FALSE;
//					try {
//						isPaytmEnabled = pm.getBooleanValue(MAXParameterConstantsIfc.PAYTMENABLED);
//					} catch (ParameterException e1) {
//						e1.printStackTrace();
//					}
//					if(isPaytmEnabled == false)
//					{
//						model.getLocalButtonBeanModel().setButtonEnabled(MAXPaytmTenderConstants.PAYTM, false);
//					}
//					else
//					{
//						model.getLocalButtonBeanModel().setButtonEnabled(MAXPaytmTenderConstants.PAYTM, true);
//					}
					ui.showScreen(MAXPOSUIManagerIfc.PINELAB_CREDIT_LOYALTY_SCREEN, model);
				}
				else
				{
					ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD, new POSBaseBeanModel());
				}
			}
			else
			{
				ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD, new POSBaseBeanModel());
			}
			// MAX Change for Rev 1.9: End

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
				logger.warn("Error while using customer interface device: " + e.getMessage() + "");
			}
		}

	}

	/**
	 * Leave the site
	 * 
	 * @param bus
	 *            bus leaving the site
	 *  */
	public void depart(BusIfc bus) {
		if (bus.getCurrentLetter().getName().equals("Next")) {
			// Get information from UI
			TenderCargo cargo = (TenderCargo) bus.getCargo();
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel();
			PromptAndResponseModel parModel = model.getPromptAndResponseModel();
	        EncipheredCardDataIfc cardData = null;

			if (parModel.isSwiped()) {
				//added by geetika
                cardData = parModel.getMSRModel().getEncipheredCardData();
                cargo.getTenderAttributes().put(TenderConstants.MSR_MODEL, parModel.getMSRModel());
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, parModel.getMSRModel().getEncipheredCardData().getTruncatedAcctNumber());
                cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Swipe);
			cargo.getTenderAttributes().remove(TenderConstants.NUMBER);
			}
			
			
			
			else if (cargo.getPreTenderMSRModel() == null) {
				// if manually entered, we only have the card number.
				//code change start by priyanka for issue Credit card number printing wrong last 4 digit in prints and in DB
				String cardNumber=ui.getInput();					
				String cardNum =null;
				if (cardNumber!=null && !(cardNumber.equalsIgnoreCase(""))){
				if(cardNumber.indexOf('^')>0){
				cardNum = cardNumber.substring(2, cardNumber.indexOf('^')); 
				}
				else{
					 StringBuffer sepBuffer = new StringBuffer();int cardIndex = 0;
					 for(int i = 0; i < cardNumber.length(); i++){						
						 char c = cardNumber.charAt(i);
						 if(c > 47 && c < 58)
						 sepBuffer.append(c);
					    }
					 cardNum = sepBuffer.toString();
					}
				}
				  try
	                {
	                    cardData = FoundationObjectFactory.getFactory().
	                            createEncipheredCardDataInstance(cardNum.getBytes());
	                   // cargo.getTenderAttributes().put(TenderConstants.NUMBER, cardData.getTruncatedAcctNumber());
	                    cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
	                    cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
	                    // remove MSR (may have been previously swiped)
	                    cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
	                }
	                catch(EncryptionServiceException e)
	                {
	                    String message = "unable to decrypt the text";
	                    throw new RuntimeException(message, e);
	                }
			
				cargo.getTenderAttributes().put(TenderConstants.NUMBER, cardNum);
				// remove MSR (may have been previously swiped)
				cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
			}
			//added else condition ---geetika
			  else
	            {  
	                MSRModel msr = (MSRModel)cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
	                cardData = msr.getEncipheredCardData();
	                cargo.getTenderAttributes().put(TenderConstants.NUMBER, msr.getEncipheredCardData().getTruncatedAcctNumber());
	                cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
	                
	            }
			// set pre tender msr model to null
			// we dont want to come back into credit/debit automatically if we
			// have problems.
			cargo.setPreTenderMSRModel(null);
		}
	}
}
