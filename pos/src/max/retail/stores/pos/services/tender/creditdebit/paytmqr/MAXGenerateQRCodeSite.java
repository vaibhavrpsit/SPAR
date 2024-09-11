/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved. 
 * 
 * Rev 1.0  March 26, 2022    Kamlesh Pant 		Paytm QR Code Integration
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package	max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import java.util.Iterator; 
import java.util.Vector;

import Paytm.QrDisplay;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
//import max.retail.stores.domain.paytm.MAXPaytmResponse;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;
import max.retail.stores.domain.transaction.MAXLayawayPaymentTransaction;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXGenerateQRCodeSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = 1380316288509276435L; 
	
	public void arrive(BusIfc bus)
	{
		
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		
		try {
			
			//String taxInfo;
			//if(cargo.getTransaction().getTransactionType()==TransactionIfc.TYPE_LAYAWAY_PAYMENT)
			//if(cargo.getTransaction().getTransactionType()==TransactionIfc.TYPE_LAYAWAY_PAYMENT)
			//{
				//taxInfo = "CGST:0.0|SGST:0.0|IGST:0.0|CESS:0.0";	
				
			//}else {
				//taxInfo = readTaxInfo(cargo.getRetailTransaction());
				
		//	}
			String url = Gateway.getProperty("application", "createPaytmQRCodeURL", "");
			
			String amount = (String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT);
			
			MAXPaytmQRCodeResponse response = MAXPaytmQRCodeHelperUtiltiy.generateQRCode(url, cargo.getCurrentTransactionADO().getTransactionID(), amount, cargo.getTillID(), cargo.getStoreStatus().getStore().getStoreID());
			//System.out.println("response :"+response.toString());
			
			MAXPaytmResponse PaytmResponse = null;
			
			if(response != null && response.isDataException())
			{	
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("ServerOffline");
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Ok");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
				return;
			}
			if(response != null && response.getResultStatus() != null && 
					response.getResultStatus().equalsIgnoreCase(MAXPaytmTenderConstants.SUCCESS))
			{	
				response.setAmountPaid(amount);
				//response.setPhoneNumber(cargo.getPhoneNumber());
				setResponseData(cargo, response);
				response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
				String paytmResp = response.getPaytmResponse();
				response.setPaytmResponse(response.getOrderId() + " : " + paytmResp);
				response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
			//	cargo.setPaytmQRCodeResp(response);
				MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
				PaytmResponse = convertPaytmResponseData(response);
				PaytmResponse.setUrl(url);
				paytmTrans.saveRequest(PaytmResponse);
				QrDisplay qrDisplay = new QrDisplay(); 
				String mid = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.MID);
				String port = MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEVICEPORT);
				int baudRate = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.BAUDRATE));
				int parity = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.PARITY));
				int dataBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DATABITS));
				int stopBits = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.STOPBITS));
				int debugMode = Integer.parseInt(MAXPaytmQRCodeConfig.get(MAXPaytmQRCodeTenderConstants.DEBUGMODE));
				
				DialogBeanModel dialogModel = new DialogBeanModel();
				/*
				 * dialogModel.setResourceID("PAYTMQRCONFIRMATION");
				 * dialogModel.setType(DialogScreensIfc.CONFIRMATION);
				 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,"YES");
				 * dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,"NO");
				 * uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
				 */
				
				Boolean displayqr=qrDisplay.displayTxnQr(mid, port, baudRate, parity, dataBits, stopBits, response.getOrderId(), amount, response.getQrData(), "INR", debugMode,response.getRegisterId());
				//System.out.println("displayqr 113:"+displayqr);
				
				if(displayqr) {
					cargo.setPaytmQRCodeResp(response);
					TransactionIfc transaction = cargo.getTransaction();
					//RetailTransactionADOIfc transaction =cargo.getCurrentTransactionADO();
					//System.out.println("getCurrentTransactionADO :"+cargo.getCurrentTransactionADO().toString());
					if(transaction instanceof MAXSaleReturnTransaction) {
						((MAXSaleReturnTransaction) transaction).setPaytmQROrderId(response.getOrderId());
					}
					if(transaction instanceof MAXLayawayTransaction) {
						((MAXLayawayTransaction) transaction).setPaytmQROrderId(response.getOrderId());
					}
					if(transaction instanceof MAXLayawayPaymentTransaction) {
						((MAXLayawayPaymentTransaction) transaction).setPaytmQROrderId(response.getOrderId());
					}
					//bus.mail("Success");
					//DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[3];
					messgArray[0] = "QR Code generated successfully and shown on the device.";
					messgArray[1] = "Ask customer to pay using payment App.";
					messgArray[2] = "Press enter to check the payment status";
					//dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEERROR);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Success");
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,
							dialogModel);	
					
					return;
				}
				else
				{
					//DialogBeanModel dialogModel = new DialogBeanModel();
					String[] messgArray = new String[1];
					messgArray[0] = "Error in display the QR code on Device.";
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
					dialogModel.setType(DialogScreensIfc.ERROR);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Ok");
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}
				
			}
			else if(response != null && ((response.getResultStatus() != null && response.getResultStatus().equalsIgnoreCase(MAXPaytmTenderConstants.FAILURE) || 
					response.getResultCode() != null)))
			{
				setResponseData(cargo, response);
				response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
				if(response.getResponseCode() == 408)
				{
					response.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
				}
				else
				{
					response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
				}
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
				response.setPaytmResponse(" : " + response.getPaytmResponse());
			//	cargo.setPaytmResp(response);
				MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
				PaytmResponse = convertPaytmResponseData(response);
				PaytmResponse.setUrl(url);
				paytmTrans.saveRequest(PaytmResponse);
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] messgArray = new String[1];
				if(response.getResultMessage() != null)
					messgArray[0] = response.getResultMessage();
				else
					messgArray[0] = "Error in getting response from Paytm";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Ok");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
				return;
			}
			else if(response != null && response.getResultMessage() != null && 
					(response.getResultMessage().equalsIgnoreCase(MAXPaytmTenderConstants.NETWORKERROR) || 
							response.getResultMessage().equalsIgnoreCase(MAXPaytmTenderConstants.PAYTMTIMEOUTERROR)))
			{
				setResponseData(cargo, response);
				response.setRequestTypeA(MAXPaytmTenderConstants.TIMEOUT);
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
				response.setReqRespStatus(MAXPaytmTenderConstants.TIMEOUT);
				MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
				PaytmResponse = convertPaytmResponseData(response);
				PaytmResponse.setUrl(url);
				paytmTrans.saveRequest(PaytmResponse);
				
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID(response.getResultMessage());
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Ok");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
			}
			else
			{
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] messgArray = new String[1];
				messgArray[0] = "Error in sending request to Paytm";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
				dialogModel.setType(DialogScreensIfc.ERROR);
				dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"Ok");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error in setting paytm webservice calling " + e.getMessage());
			bus.mail("failure");
		}
	}
	
	public void setResponseData(MAXTenderCargo cargo, MAXPaytmQRCodeResponse response)
	{
	
		response.setStoreId(cargo.getStoreStatus().getStore().getStoreID());
		response.setRegisterId(cargo.getRegister().getWorkstation().getWorkstationID());
		response.setTillId(cargo.getTillID());
	//	response.setPhoneNumber(cargo.getPhoneNumber());
		response.setBussinessdate(cargo.getStoreStatus().getBusinessDate());
		//response.setTotalTransactionAmt(cargo.getCurrentTransactionADO().getBalanceDue().toString());
		response.setTotalTransactionAmt(cargo.getTransaction().getTransactionTotals().getSubtotal().toString());
		response.setRequestTypeB("G");
		response.setTimeOut(MAXPaytmQRCodeConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT));
		response.setTransactionId(cargo.getCurrentTransactionADO().getTransactionID());
		
	}
	
	
	public MAXPaytmResponse convertPaytmResponseData(MAXPaytmQRCodeResponse response)
	{
		
		MAXPaytmResponse paytmResponse = new MAXPaytmResponse();
		paytmResponse.setStoreId(response.getStoreId());
		paytmResponse.setRegisterId(response.getRegisterId());
		paytmResponse.setTillId(response.getTillId());
		paytmResponse.setPhoneNumber(null);
		paytmResponse.setBussinessdate(response.getBussinessdate());
		paytmResponse.setTotalTransactionAmt(response.getTotalTransactionAmt());
		paytmResponse.setRequestTypeB("G");
		paytmResponse.setTimeOut(MAXPaytmQRCodeConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT));
		paytmResponse.setTransactionId(response.getTransactionId());		
		paytmResponse.setOrderId(response.getOrderId());
		paytmResponse.setRequestTypeA(response.getRequestTypeA());
		paytmResponse.setReqRespStatus(response.getReqRespStatus());
		paytmResponse.setRequestTypeB(response.getRequestTypeB());		
		paytmResponse.setTimeOut(response.getTimeOut());
		paytmResponse.setPaytmResponse(response.getPaytmResponse());
		paytmResponse.setAmountPaid(response.getAmountPaid());
		return paytmResponse;
		
	}
	
	
	public String readTaxInfo(RetailTransactionIfc transaction) {
		
		String taxInfo = "";
		
		CurrencyIfc igstAmt = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc cgstAmt = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc sgstAmt = DomainGateway.getBaseCurrencyInstance();
		CurrencyIfc cessAmt = DomainGateway.getBaseCurrencyInstance();
		
	//	if(transaction instanceof MAXSaleReturnTransaction) {
			//totalSaving = getTotalSavings(posTransaction);
			Vector lineItems = ((SaleReturnTransaction)transaction).getItemContainerProxy().getLineItemsVector(); 
			for (Iterator itemsIter = lineItems.iterator(); itemsIter.hasNext();) {
				
				MAXSaleReturnLineItemIfc srli = (MAXSaleReturnLineItemIfc) itemsIter.next(); 

MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails = ((MAXItemTaxIfc) (srli.getItemPrice().getItemTax())).getLineItemTaxBreakUpDetail();
				
				
				for(int i = 0; i<lineItemBreakUpDetails.length; i++) {
					MAXLineItemTaxBreakUpDetailIfc breakupDetails = lineItemBreakUpDetails[i];
					CurrencyIfc taxableAmt = breakupDetails.getTaxableAmount().abs();
					MAXTaxAssignmentIfc taxAssignment = breakupDetails.getTaxAssignment();
					if(taxAssignment.getTaxCodeDescription() != null && taxAssignment.getTaxCodeDescription().toUpperCase().contains("CGST")){
						
						cgstAmt = cgstAmt.add(breakupDetails.getTaxAmount().abs());
					}else if(taxAssignment.getTaxCodeDescription() != null && (taxAssignment.getTaxCodeDescription().toUpperCase().contains("SGST")
							|| taxAssignment.getTaxCodeDescription().toUpperCase().contains("UTGST"))){
						
						sgstAmt = sgstAmt.add(breakupDetails.getTaxAmount().abs());
					}else if(taxAssignment.getTaxCodeDescription() != null && taxAssignment.getTaxCodeDescription().toUpperCase().contains("IGST")){
						
						igstAmt = igstAmt.add(breakupDetails.getTaxAmount().abs());
						
					}else if(taxAssignment.getTaxCodeDescription() != null && taxAssignment.getTaxCodeDescription().toUpperCase().contains("CESS")){
						
							cessAmt = cessAmt.add(breakupDetails.getTaxAmount().abs());
						
					}
				}
}
//}
		if(cgstAmt.getDoubleValue() > 0) {
			taxInfo = taxInfo.concat("CGST:".concat(cgstAmt.getStringValue()));
		}
		if(sgstAmt.getDoubleValue() > 0) {
			taxInfo = taxInfo.concat("|SGST:"+sgstAmt.getStringValue());
		}
		if(igstAmt.getDoubleValue() > 0) {
			taxInfo = taxInfo.concat("|IGST:"+igstAmt.getStringValue());
		}
	
	if(cessAmt.getDoubleValue() > 0) {
		taxInfo.concat("|CESS:"+cessAmt.getStringValue());
	}
	
	if(taxInfo == "") {
		taxInfo = "CGST:0.0|SGST:0.0|IGST:0.0|CESS:0.0";
	}
	
		return taxInfo;
	}


}
