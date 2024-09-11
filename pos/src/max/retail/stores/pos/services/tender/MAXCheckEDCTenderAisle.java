/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.
 * 
 *  @author kumar Vaibhav
 * 
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import oracle.retail.stores.domain.tender.TenderGiftCard;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransaction;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @author kumar.v
 *
 */
public class MAXCheckEDCTenderAisle extends PosLaneActionAdapter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6611552796824565787L;

	public void traverse(BusIfc bus) {

		String letter = null;
		boolean edcFlag = false;
		boolean paytmFlag = false;
		boolean GiftCardFlag = false;
		//Change for Rev 1.2 Start
		boolean eWalletFlag = false;
		//Change for Rev 1.2 Ends
		TenderCargo cargo = (TenderCargo) bus.getCargo();

		try {
			// If transaction os layaway Transaction, need to handle classcast
			// Exception..
			
			if(cargo.getCurrentTransactionADO().toLegacy() instanceof LayawayPaymentTransaction)
			{
				LayawayPaymentTransaction trx = (LayawayPaymentTransaction) cargo.getCurrentTransactionADO().toLegacy();
				TenderLineItemIfc[] tlm = trx.getTenderLineItems();
				int size = tlm.length;
				for (int i = 0; i < size; i++) {
					//Changes starts for rev 1.1 (Ashish : PineLab)
					if (tlm[i] instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) tlm[i]).getResponseDate() != null)
							&& (("00").equals(((MAXTenderChargeIfc) tlm[i]).getResponseDate().get("HostResponseCode")) || ("APPROVED").equals(((MAXTenderChargeIfc) tlm[i]).getResponseDate().get("HostResponseCode"))
									|| ("APPROVED").equals(((MAXTenderChargeIfc) tlm[i]).getResponseDate().get("HostResponse")))) {
						edcFlag = true;
					}
					//Changes ends for rev 1.1 (Ashish : PineLab)
					//Rev 1.1 changes starts
					else if(tlm[i] instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) tlm[i]).getCardType() != null)
							&& ("PAYTM").equals(((MAXTenderChargeIfc) tlm[i]).getCardType()))
					{
						paytmFlag = true;
					}
					//Rev 1.1 changes ends
					//Change for Rev 1.2 Start
					/*else
					{
						if (tlm[i] instanceof MAXTenderEWallet) {
							eWalletFlag = true;
						}
					}*/
					//Change for Rev 1.2 Ends
				}			
			}
			else if(cargo.getCurrentTransactionADO().toLegacy() instanceof LayawayTransaction)
			{
			MAXLayawayTransaction trx = (MAXLayawayTransaction) cargo.getCurrentTransactionADO().toLegacy();
			TenderLineItemIfc[] tlm = trx.getTenderLineItems();
			int size = tlm.length;
			for (int i = 0; i < size; i++) {
				if (tlm[i] instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) tlm[i]).getResponseDate() != null)
						&& ("00").equals(((MAXTenderChargeIfc) tlm[i]).getResponseDate().get("HostResponseCode"))) {
					edcFlag = true;
				}
				//Rev 1.1 changes starts
				else if(tlm[i] instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) tlm[i]).getCardType() != null)
						&& ("PAYTM").equals(((MAXTenderChargeIfc) tlm[i]).getCardType()))
				{
					paytmFlag = true;
				}
				//Rev 1.1 changes ends
				//Change for Rev 1.2 Start
				else
				{
					/*if (tlm[i] instanceof MAXTenderEWallet) {
						eWalletFlag = true;
					}*/
				}
				//Change for Rev 1.2 Ends
			}			
			}
			else{
			MAXSaleReturnTransaction trx = (MAXSaleReturnTransaction) cargo.getCurrentTransactionADO().toLegacy();
			TenderLineItemIfc[] tlm = trx.getTenderLineItems();
			int size = tlm.length;
			for (int i = 0; i < size; i++) {
				//Changes starts for Rev 1.1 (Ashish : PineLab)
				if (tlm[i] instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) tlm[i]).getResponseDate() != null)
						&& (("00").equals(((MAXTenderChargeIfc) tlm[i]).getResponseDate().get("HostResponseCode")) || ("APPROVED").equals(((MAXTenderChargeIfc) tlm[i]).getResponseDate().get("HostResponseCode"))
								|| ("APPROVED").equals(((MAXTenderChargeIfc) tlm[i]).getResponseDate().get("HostResponse")))) {
					edcFlag = true;
				
				}
				//Changes starts for Rev 1.1 (Ashish : PineLab)
				//Rev 1.1 changes starts
				else if(tlm[i] instanceof MAXTenderChargeIfc && (((MAXTenderChargeIfc) tlm[i]).getCardType() != null)
						&& ("PAYTM").equals(((MAXTenderChargeIfc) tlm[i]).getCardType()))
				{
					paytmFlag = true;
				}
				//Rev 1.1 changes ends
			}
			for (int j = 0; j < size; j++) {
				if (tlm[j] instanceof TenderGiftCard) {
					GiftCardFlag = true;
				}
				//Change for Rev 1.2 Start
				/*if (tlm[j] instanceof MAXTenderEWallet) {
					eWalletFlag = true;
				}*/
				//Change for Rev 1.2 Ends
			}
			}
		} catch (ClassCastException e) {

		}
		if (edcFlag) {
			DialogBeanModel dialogModel = new DialogBeanModel();
			String msg[] = new String[4];
			dialogModel.setResourceID("EDC_TENDER_REVERSAL");
			msg[0] = "<<--||--:: Reversal Blocked ::--||-->>";
			msg[1] = "Here Tendering is done using Online Unipay System";
			msg[2] = "Please firstly Line Void the Online EDC tenders and Try Again";
			msg[3] = "::Thanks::";
			dialogModel.setArgs(msg);
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);

			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCUsed");
			// dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Undo");
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		}
		//priyanka change start for partial tender message of gift card
		else if(GiftCardFlag){
			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("GiftcardEscMessage");
			model.setType(DialogScreensIfc.ERROR);
			model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "GiftCardUndo");
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
			
		}//priyanka change end for partial tender message of gift card
		//Rev 1.1 changes starts
		else if(paytmFlag == true)
		{
			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("PAYTM_TENDER_REVERSAL");
			model.setType(DialogScreensIfc.ERROR);
			model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCUsed");
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		//Rev 1.1 changes ends
		//Change for Rev 1.2 Start
		else if(eWalletFlag == true)
		{
			DialogBeanModel model = new DialogBeanModel();
			model.setResourceID("DeleteTenderEWallet");
			model.setType(DialogScreensIfc.ERROR);
			model.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCUsed");
			POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
		//Change for Rev 1.2 Ends
		else {
			letter = "EDCNotUsed";
			bus.mail(new Letter(letter), BusIfc.CURRENT);
		}
		// bus.mail(new Letter(letter), BusIfc.CURRENT);

	}

}
