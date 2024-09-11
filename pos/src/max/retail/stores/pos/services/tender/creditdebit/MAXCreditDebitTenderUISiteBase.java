/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 * Rev  	1.0  	21 Dec, 2016              Ashish Yadav             Intial Credit Card FES
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.tender.creditdebit;

import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.cidscreens.CIDAction;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class MAXCreditDebitTenderUISiteBase extends PosSiteActionAdapter {
	public void arrive(BusIfc bus) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		MSRModel msr = cargo.getPreTenderMSRModel();
		boolean creditOnlineFlowFlag = ((MAXTenderCargo) cargo).isCreditOnlineFlow();
		if (msr != null) {
			cargo.getTenderAttributes().put("MSR_MODEL", msr);

			cargo.getTenderAttributes().remove("NUMBER");
			bus.mail(new Letter("Next"), BusIfc.CURRENT);
		} else if (cargo.getNextTender() != null) {
			cargo.setTenderAttributes(cargo.getNextTender().getTenderAttributes());
			bus.mail(new Letter("Done"), BusIfc.CURRENT);
		} else {
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager("UtilityManager");

			String message = utility.retrieveCommonText("swipeNowMessage", "You may swipe your card now.");

			// POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager("UIManager");
			// ui.showScreen("CREDIT_DEBIT_CARD", new POSBaseBeanModel());
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			String letter = bus.getCurrentLetter().getName();
			if ((letter != null)) {
					if (creditOnlineFlowFlag) {
					int i = 1;
					ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
					POSBaseBeanModel model = new POSBaseBeanModel();
					NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
					model.setLocalButtonBeanModel(localModel);
					ui.showScreen(MAXPOSUIManagerIfc.CREDIT_LOYALTY_SCREEN, model);
					//ui.showScreen(MAXPOSUIManagerIfc.CREDIT_OPTIONS, model);
				} else {
					ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD, new POSBaseBeanModel());
				}
			} else {
				ui.showScreen(POSUIManagerIfc.CREDIT_DEBIT_CARD, new POSBaseBeanModel());
			}

			POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
			try {
				CIDAction clear = new CIDAction("MessageScreen", 1);
				pda.cidScreenPerformAction(clear);
				CIDAction action = new CIDAction("MessageScreen", 12);
				action.setStringValue(message);
				pda.cidScreenPerformAction(action);
				CIDAction show = new CIDAction("MessageScreen", 2);
				pda.cidScreenPerformAction(show);
			} catch (DeviceException e) {
				logger.warn("Error while using customer interface device: " + e.getMessage() + "");
			}
		}
	}

	public void depart(BusIfc bus) {
		/*if (!(bus.getCurrentLetter().getName().equals("Next"))) {
			return;
		}*/
		if (bus.getCurrentLetter().getName().equals("Next")) {
		TenderCargo cargo = (TenderCargo) bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager("UIManager");
		POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel();
		PromptAndResponseModel parModel = model.getPromptAndResponseModel();
		EncipheredCardDataIfc cardData = null;
		if (parModel.isSwiped()) {
			cardData = parModel.getMSRModel().getEncipheredCardData();
			cargo.getTenderAttributes().put(TenderConstants.NUMBER, parModel.getMSRModel().getEncipheredCardData().getTruncatedAcctNumber());
            cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
            cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Swipe);
			cargo.getTenderAttributes().put("MSR_MODEL", parModel.getMSRModel());

			cargo.getTenderAttributes().remove("NUMBER");
		} else if (cargo.getPreTenderMSRModel() == null) {
			//cargo.getTenderAttributes().put("NUMBER", ui.getInput());

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
                    cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
                    cargo.getTenderAttributes().put(TenderConstants.ENTRY_METHOD, EntryMethod.Manual);
                    cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
		}
                catch(EncryptionServiceException e)
                {
                    String message = "unable to decrypt the text";
                    throw new RuntimeException(message, e);
                }
			cargo.getTenderAttributes().put(TenderConstants.NUMBER, cardNum);
			cargo.getTenderAttributes().remove(TenderConstants.MSR_MODEL);
		}
		  else
            {  
                MSRModel msr = (MSRModel)cargo.getTenderAttributes().get(TenderConstants.MSR_MODEL);
                cardData = msr.getEncipheredCardData();
                cargo.getTenderAttributes().put(TenderConstants.NUMBER, msr.getEncipheredCardData().getTruncatedAcctNumber());
                cargo.getTenderAttributes().put(TenderConstants.ENCIPHERED_CARD_DATA, cardData);
            }

		cargo.setPreTenderMSRModel(null);
	}
}}