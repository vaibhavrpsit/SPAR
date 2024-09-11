/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2010 Lifestyle India Pvt Ltd.    All Rights Reserved.
 *
 * Rev 1.4  Jul 10,2019	  Nitika Arora    E-Wallet Integration 
 * Rev 1.3	Jul 03, 2018  Jyoti Yadav	  Changes for RTS Phase2 and My Credit Wallet
 * Rev 1.2  Dec 08, 2014  Shavinki Goyal  Resolution for MAX-FES:-Multiple Tender using Innoviti  
 * Rev 1.1 Nov 12, 2011 Ashwini Kumar
 * MAX-269, picking loyalty car number from a new property loyalty card number
 *
 * Rev 1.0  Feb 08, 2011 1:31:08 PM tarun.gupta
 * Initial revision.
 * Resolution for FES_LMG_India_Customer_Loyalty_v1.1
 * On Deletion of Loyalty Tender Reversal Request to be send 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender;

import java.math.BigDecimal;
import java.util.HashMap;

import max.retail.stores.domain.tender.MAXTenderChargeIfc;
import max.retail.stores.pos.services.edc.CallingOnlineDebitCardTender;
import max.retail.stores.pos.services.edc.pinelab.CallingOnlinePineLabDebitCardTender;
import max.retail.stores.pos.services.tender.creditdebit.MAXPineLabTransactionConstantsIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.factory.TenderFactoryIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.TenderBeanModel;

/**
 * Deletes tender from a transaction
 */
public class MAXPreDeleteTenderActionAisle extends PosLaneActionAdapter {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -7203438153389583317L;

	private static int responseCode;

	public void traverse(BusIfc bus) {
		HashMap reversalAttributes = new HashMap();

		CallingOnlineDebitCardTender edcClassObj = new CallingOnlineDebitCardTender();
		CallingOnlinePineLabDebitCardTender edcPineLabClassObj = new CallingOnlinePineLabDebitCardTender();
		// Get the tender from the model and construct an ADO tender from it
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		TenderBeanModel model = (TenderBeanModel) ui.getModel();

		TenderLineItemIfc tenderToRemove = model.getTenderToDelete();
		// Create ADO tender
		TenderFactoryIfc factory = null;

		// get Current txn from cargo
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		// RetailTransactionADOIfc txnADO = cargo.getCurrentTransactionADO();

		// Added by Gaurav for the unipay functionality..starts
		// code added by Gaurav using unipay..starts
		// boolean tederRemovalEligible = false;
		//Changes starts for rev 1.1 (Vaibhav : PineLab)
		if (tenderToRemove instanceof MAXTenderChargeIfc && ((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("HostResponseCode") != null
				&& (("00").equals(((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("HostResponseCode").toString())|| 
						("APPROVED").equals(((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("HostResponseMessage").toString()))) {
			// System.out.println("Inside Test");
			//Changes ends for rev 1.1 (Vaibhav : PineLab)
			// CREDIT_DEBIT_ONLINE_OFFLINE_SWIPE
			ui.showScreen(MAXPOSUIManagerIfc.EDC_VOID_SCREEN, model);
			// Needs to provide request xml
			HashMap responseMap = new HashMap();
           //updated to 14.1
			String amountString = new BigDecimal(tenderToRemove.getAmountMaximumChange().multiply(new BigDecimal("100.00")).toString()).intValue() + "";

			String transactionTime = ((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("StateTransactionTime").toString();
			String transactionReferenceNumber = ((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("StateInvoiceNumber").toString();
			String transactionBatchNo = ((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("StateBatchNumber").toString();
			
			try 
			{
				// MAX Change for Rev 1.2: Start
				MAXTenderChargeIfc tenderCharge = (MAXTenderChargeIfc)tenderToRemove;
				
				String cardType =  tenderCharge.getCardType();
				// changed by atul shukla, Replace LTYPT with "L-"
				//Changes starts for Rev 1.1 (Vaibhav ; PineLab)
				if(((MAXTenderChargeIfc) tenderToRemove).getResponseDate() != null &&
						((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("EDCType") != null &&
								!((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("EDCType").toString().equalsIgnoreCase("") &&
								((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("EDCType").toString().equalsIgnoreCase("PLUTUS")){
					amountString = new BigDecimal(tenderToRemove.getAmountTender().multiply(new BigDecimal("100.00")).toString()).intValue() + "";
					String bankCode  = ((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("AcquiringBankCode").toString();
					if (cardType.startsWith("L-")) // Points Redemption Void
					{
						
						//responseMap = edcPineLabClassObj.makePostVoidEDC(null, amountString, transactionReferenceNumber, transactionTime, "1", "5");
						responseMap = edcPineLabClassObj.doSaleTransaction(cargo.getCurrentTransactionADO().getTransactionID(),
								amountString, transactionReferenceNumber, transactionTime, "0", "0",MAXPineLabTransactionConstantsIfc.PLUTUS_POINTS_REDEMPTION_VOID,transactionBatchNo);
					}
					else if (cardType.contains("PHONE PE")) // Phone Pe void
					{
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID("VoidPineLabTransaction");
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCPostVoidedFail");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
					else if (cardType.contains("UPI")) // EMI Void
					{
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID("VoidPineLabTransaction");
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCPostVoidedFail");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
					else if (tenderCharge.isEmiTransaction()) // EMI Void
					{
						//responseMap = edcPineLabClassObj.makePostVoidEDC(null, amountString, transactionReferenceNumber, transactionTime, "1", "10");
						
						responseMap = edcPineLabClassObj.doSaleTransaction(cargo.getCurrentTransactionADO().getTransactionID(),
								amountString, transactionReferenceNumber, transactionTime, "0", "0",MAXPineLabTransactionConstantsIfc.PLUTUS_VOID_TRANSACTION_TYPE,bankCode);
					}
					else // Normal Sale Void
					{
						//responseMap = edcPineLabClassObj.makePostVoidEDC(null, amountString, transactionReferenceNumber, transactionTime, "1", "0");
						
						responseMap = edcPineLabClassObj.doSaleTransaction(cargo.getCurrentTransactionADO().getTransactionID(),
								amountString, transactionReferenceNumber, transactionTime, "0", "0",MAXPineLabTransactionConstantsIfc.PLUTUS_VOID_TRANSACTION_TYPE,bankCode);
					}
				}else{
					if (cardType.startsWith("L-")) // Points Redemption Void
					{
						
						responseMap = edcClassObj.makePostVoidEDC(null, amountString, transactionReferenceNumber, transactionTime, "1", "5");

					}
					else if (tenderCharge.isEmiTransaction()) // EMI Void
					{
						responseMap = edcClassObj.makePostVoidEDC(null, amountString, transactionReferenceNumber, transactionTime, "1", "10");
					}
					else // Normal Sale Void
					{
						responseMap = edcClassObj.makePostVoidEDC(null, amountString, transactionReferenceNumber, transactionTime, "1", "0");
					}
				}
				//Changes ends for Rev 1.1 (Vaibhav ; PineLab)

				try 
				{ 
					if(!((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("EDCType").toString().equalsIgnoreCase("PLUTUS")){
					edcClassObj.printChargeSlipData(bus, responseMap);
					// MAX Change for Rev 1.2: End
				} else {
					edcPineLabClassObj.printChargeSlipData(bus, responseMap);
				}
				}
				catch (Exception e)
				{

				}
				cargo.setResponseMap(responseMap);
				/*if (((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("HostResponseCode") != null
						&& ("00").equals(((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("HostResponseCode").toString())) {*/
				//Changes starts for rev 1.1 (Vaibhav : PineLab)
				if (responseMap != null && ((responseMap.get("HostResponseCode") != null
						 && responseMap.get("HostResponseCode").equals("00")) || (responseMap.get("HostResponse") != null
								 && responseMap.get("HostResponse").equals("APPROVED")) || (responseMap.get("HostResponseCode") != null
										 && responseMap.get("HostResponseCode").equals("APPROVED"))))
					//Changes ends for rev 1.1 (Vaibhav : PineLab)
				{
					bus.mail(new Letter("EDCPostVoided"), BusIfc.CURRENT);
				} 
				else 
				{
					if(((MAXTenderChargeIfc) tenderToRemove).getResponseDate() != null &&
							((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("EDCType") != null &&
							!((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("EDCType").toString().equalsIgnoreCase("") &&
							((MAXTenderChargeIfc) tenderToRemove).getResponseDate().get("EDCType").toString().equalsIgnoreCase("PLUTUS")){
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID("VoidPineLabTransaction");
						String msg[] = new String[6];
						msg[0] = (responseMap.get("HostResponseCode")).toString();
						
						dialogModel.setArgs(msg);
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCPostVoidedFail");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
					else{
						DialogBeanModel dialogModel = new DialogBeanModel();
						dialogModel.setResourceID("VoidTransaction");
						dialogModel.setType(DialogScreensIfc.ERROR);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "EDCPostVoidedFail");
						ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
					}
				//bus.mail(new Letter("EDCPostVoidedFail"), BusIfc.CURRENT);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} 
		/*Change for Rev 1.4: Start*/
		/*else if (tenderToRemove instanceof MAXTenderGiftCard){
			bus.mail(new Letter("DeleteGiftCard"), BusIfc.CURRENT);
		}
		Change for Rev 1.4: End
		Change for Rev 1.3: Start
		else if (tenderToRemove instanceof MAXTenderCreditNoteIfc && ((MAXTenderCreditNoteIfc) tenderToRemove).getResponseCode() != null
				&& ("0").equals(((MAXTenderCreditNoteIfc) tenderToRemove).getResponseCode().toString())){
			bus.mail(new Letter("DeleteCreditNote"), BusIfc.CURRENT);
		}
		Change for Rev 1.3: End
		//Change for Rev 2.5 Start
		else if (tenderToRemove instanceof MAXTenderEWallet){
			bus.mail(new Letter("DeleteEWallet"), BusIfc.CURRENT);
		}*/
		//Change for Rev 2.5 Ends
		else {
			bus.mail(new Letter("EDCPostVoided"), BusIfc.CURRENT);
		}

		// modification by Gaurav..ends

	}

}
