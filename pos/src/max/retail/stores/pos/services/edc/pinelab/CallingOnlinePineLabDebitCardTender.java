/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.edc.pinelab;

import java.util.HashMap;

import com.pinelabs.billingapp.CSVMessage;
import com.pinelabs.billingapp.PlutusTransport;

import jpos.POSPrinterConst;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @author kumar.vaibhav
 *
 */
public class CallingOnlinePineLabDebitCardTender {
	
	HashMap resp = new  HashMap();

	public CallingOnlinePineLabDebitCardTender() {

	}
	

	// public void doAnyTriggerTransaction(){
	// Thread th = new Thread(){
	// public void run(){
	// PlutusTransport plutusTransport = new PlutusTransport();
	// String strCSV = "1234,10000,01,,,5678,TRUE,,,,";
	// System.out.println("request CSV: "+strCSV);
	// CSVMessage message = new CSVMessage();
	// message.setCSV(strCSV);
	// int iResponse = plutusTransport.PL_TriggerTransaction(4001, message);
	// System.out.println("response: "+iResponse);
	// System.out.println("response csvmessage: "+message.getCSV());
	// }
	// };
	// th.start();
	// }

	public HashMap doSaleTransaction(String transactionID, String amountString, String invoiceNumber,
			String transactionTime, String requestType, String requestMode,final int tranType, String transactionBatchNo) {
		
//		Thread th = new Thread(){
//			public void run(){			
				PlutusTransport plutusTransport = new PlutusTransport();
				int iResponse = 0;
				String strCSV = null;
				if(tranType == 5120 || tranType == 5102 || tranType == 4101){
					
					strCSV = transactionID+","+amountString+",,,,,,,";
				}
				else if(tranType == 4102){
					
					strCSV = transactionID+","+amountString+",,,,,,,,,"+transactionBatchNo+","+invoiceNumber;
				}
				else if(tranType == 4006){
					
					strCSV = transactionID+","+amountString+","+transactionBatchNo+",,,"+invoiceNumber;
				}else{
					strCSV = transactionID+","+amountString+",01,,,"+invoiceNumber+",TRUE,,,,";
				}
//				System.out.println("request CSV: "+strCSV);
//				System.out.println("request type: " +tranType);
//				logger.info("request CSV:: " + strCSV);
//				logger.info("request type: " + tranType);
				CSVMessage message = new CSVMessage();
				message.setCSV(strCSV);
				try {
				iResponse = plutusTransport.PL_TriggerTransaction(tranType, message);
//				System.out.println("response: "+iResponse);
//				System.out.println("response csvmessage: "+message.getCSV());
				resp = parseCSV(message.getCSV(),tranType);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
//			}
//		};
//		th.start();
		return resp;
	}
	
	public HashMap doGetStatusTransaction(String transactionID,
			String amountString, String invoiceNumber, String transactionTime,
			String requestType, String requestMode, final int tranType) {

		PlutusTransport plutusTransport = new PlutusTransport();
		int iResponse = 0; 
		String strCSV = transactionID + "," + amountString + ",,,,,,,,,";
		//System.out.println("request CSV: " + strCSV);
		CSVMessage message = new CSVMessage();
		message.setCSV(strCSV);
		try {
			iResponse = plutusTransport
					.PL_TriggerTransaction(tranType, message);
			//System.out.println("response: " + iResponse);
			//System.out.println("response csvmessage: " + message.getCSV());
			resp = parseCSV(message.getCSV(), tranType);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp;

	}

	public HashMap parseCSV(String csv,int requestMode){
		HashMap resp = new  HashMap<>();
		String str[] = (csv.replace("\"","")).replace(" ,", ",").split(",");
        /*for(int i=1;i<str.length;i++){
            String arr[] = str[i].split(":");
            resp.put(arr[0], arr[1]);
        }*/
		if(str.length > 0 && str.length >= 33){
//		resp.put("TrackingID", str[0]);
//		resp.put("ApprovalCode", str[1]);
//		resp.put("HostResponse", str[2]);
//		resp.put("CardNumber", str[3]);
//		resp.put("ExpirationDate", str[4]);
//		resp.put("CardholderName", str[5]);
//		resp.put("CardType", str[6]);
//		resp.put("InvoiceNumber", str[7]);
//		resp.put("BatchNumber", str[8]);
//		resp.put("TerminalID", str[9]);
//		resp.put("LoyaltyPointsAwarded", str[10]);
//		resp.put("Remark", str[11]);
//		resp.put("TransactionAcquirerName", str[12]);
//		resp.put("MerchantID", str[13]);
//		resp.put("RetrievalReferenceNumber", str[14]);
//		resp.put("CardEntryMode", str[15]);
//		resp.put("CardholderNameOnReceipt", str[16]);
//		resp.put("MerchantName", str[17]);
//		resp.put("MerchantAddress", str[18]);
//		resp.put("MerchantCity", str[19]);
//		resp.put("PlutusVersion", str[20]);
//		resp.put("AcquiringBankCode", str[21]);
//		resp.put("RewardRedeemedAmount", str[22]);
//		resp.put("RewardRedeemedPoints", str[23]);
//		resp.put("RewardBalanceAmount", str[24]);
//		resp.put("RewardBalancePoints", str[25]);
//		resp.put("ChargeSlip", str[26]);
//		resp.put("CouponCode", str[27]);
//		resp.put("AmountLoadedOncardsOrWallets", str[28]);
//		resp.put("RFU3", str[29]);
//		resp.put("SettlementSummary", str[30]);
//		resp.put("DateOfTransaction", str[31]);
//		resp.put("TimeOfTransaction", str[32]);
//		resp.put("PineLabsClientID", str[33]);
//		resp.put("PineLabsBatchID", str[34]);
//		resp.put("PineLabsROC", str[35]);
			
			
			
			resp.put("CardNumber", str[3]);
			resp.put("IsManualEntry", false);
			resp.put("SchemeType", str[6]);
			resp.put("MerchantID", str[13]);
			resp.put("StateTID", str[9]);
			resp.put("StateBatchNumber", str[8]);
			resp.put("HostResponseCode", "");
			resp.put("StateInvoiceNumber", str[7]);
			resp.put("HostResponseMessage", str[2]);
			resp.put("HostResponse", str[2]);
			resp.put("HostResponseApprovalCode", str[1]);
			resp.put("HostResponseRetrievelRefNumber", str[14]);
			resp.put("AcquiringBankCode", str[21]);
//			if(str[12].contains("BANK")){
//				resp.put("SelectedAquirerName", (str[12].replace("BANK", " BANK")));
//			}else{
//			resp.put("SelectedAquirerName", str[12]);
//			}
			resp.put("SelectedAquirerName", str[12].concat("-P"));
			String date = str[31];
			String time = str[32];
			String finalTime = "";
			if(date != null && !date.equalsIgnoreCase("")){
				String mm =	date.substring(0, 2);
				String dd = date.substring(2, 4);
				String yyyy = date.substring(4);
				if(time != null && time != ""){
					String hh = time.substring(0, 2);
					String mmm = time.substring(2, 4);
					String ss = time.substring(4);
					finalTime = yyyy.concat("-").concat(mm).concat("-").concat(dd).concat("T").concat(hh).concat(":").concat(mmm).concat(":").concat(ss).concat(".").concat("0Z");
					//System.out.println(finalTime);
				}
			}
			resp.put("StateTransactionTime", finalTime);
			resp.put("EDCType", "PLUTUS");
			
			if(requestMode == 5101 && str[27] != null && !str[27].equalsIgnoreCase("")){
				resp.put("EMITenure", (str[27].split(("|"))[1]));
			}
			
		}
		if(requestMode == 4006){
			resp.put("HostResponseCode", csv);
		}
//		if(requestMode == 5101 && str[27] != null && !str[27].equalsIgnoreCase("")){
//			resp.put("EMITenure", (str[27].split(("|"))[1]));
//		}
		if(str.length > 0 && str.length >= 12){
			if(str[2] != null && !str[2].equalsIgnoreCase("")){
				
			}
			else{
				if(str[11] != null && !str[11].equalsIgnoreCase("")){
				resp.put("HostResponseMessage", str[11]);
				resp.put("HostResponse", str[11]);
				}
			}
		}
		
        return resp;
	}
	
	
	
	
	public String getFormattedReportForCustomer(HashMap map) {
		StringBuffer buff = new StringBuffer();
		buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
		buff.append(System.getProperty("line.separator"));
		for (int i = 0; i < 20; i++) {

			// buff.append(RegisterReport.NEW_LINE);
			String data = map.get("PrintLine" + i).toString();
			// /Gaurav
			int LL = 40;
			int DL = 0;
			if (data != null && data.length() > 2)
				DL = data.length() / 2;

			int SL = 0;
			SL = (LL / 2) - DL;

			if (data != null && data.length() > 2) {
				for (int j = 0; j < SL; j++) {
					data = " " + data;
				}
			}

		}

		return (buff.toString());
	}
	
	public void printChargeSlipData(BusIfc bus, HashMap response) {
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
		ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
		String LINE_SEPARATOR = "line.separator";
		String sep = System.getProperty(LINE_SEPARATOR);
		StringBuffer sepBuffer = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			sepBuffer.append(sep);
		}
		String sixBlankLines = sepBuffer.toString();

		try {
			// akanksha

			ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
			boolean Value = false;
			try {
				String check = pm.getStringValue("PrintingOnlineCreditCardChargeSlip");
				if (check.equals("Y"))
					Value = true;
				else {
					Value = false;
				}
			} catch (ParameterException pe) {

			}

			// akanksha
			if (Value) {
				pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, getFormattedReportForCustomer(response) + sixBlankLines);
				// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
				// + sixBlankLines);
				pda.cutPaper(100);

				pda.printNormal(POSPrinterConst.PTR_S_RECEIPT, getFormattedReportForMerchant(response) + sixBlankLines);
				// pda.printNormal(POSPrinterConst.PTR_S_RECEIPT,getFormattedReport(response)
				// + sixBlankLines);
				pda.cutPaper(100);
			}
		} catch (DeviceException e) {
			ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
			DialogBeanModel model = new DialogBeanModel();
			String msg[] = new String[1];
			UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
			msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
					BundleConstantsIfc.PRINTER_OFFLINE);
			model.setResourceID("RetryCancel");
			model.setType(DialogScreensIfc.RETRY_CANCEL);
			model.setArgs(msg);
			ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
		}
	}
	
	public String getFormattedReportForMerchant(HashMap map) {
		StringBuffer buff = new StringBuffer();
		buff.append("***************************************");
		// buff.append(RegisterReport.NEW_LINE);
		buff.append(System.getProperty("line.separator"));
		for (int i = 20; i < 40; i++) {

			// buff.append(RegisterReport.NEW_LINE);
			String data = map.get("PrintLine" + i).toString();
			int LL = 40;
			int DL = 0;
			if (data != null && data.length() > 2)
				DL = data.length() / 2;

			int SL = 0;
			SL = (LL / 2) - DL;

			if (data != null && data.length() > 2) {
				for (int j = 0; j < SL; j++) {
					data = " " + data;
				}
			}
		}
		return (buff.toString());
	}
	
//	public static void main(String args[]){
//		CallingOnlinePineLabDebitCardTender k = new CallingOnlinePineLabDebitCardTender();
//		String csv = "1234,0,APPROVED,533114******2373,XXXX,,ASHISH YADAV             /MASTERCARD,5,4,89765438,0,PROCESSED,HDFC BANK,               ,20,2,1,LOVE COMMUNICATION,JANAKPURI,NEW DELHI    DEL       ,Plutus v1.49.3 MT HDFC BANK,1,,,,,,,,,,6152020,151321,2675530,10000,4001";
//		k.parseCSV(csv);
//	}
}
