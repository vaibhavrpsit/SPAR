/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *    Copyright (c) 2022-2023 MAXHyperMarket, Inc.    All Rights Reserved.
 * 
 * Rev 1.0  April 13, 2022   Kamlesh Pant 		Paytm QR Code Integration
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.tender.creditdebit.paytmqr;

import java.util.Iterator; 
import java.util.Vector;

import Paytm.QrDisplay;
import max.retail.stores.domain.arts.MAXPaytmDataTransaction;
import max.retail.stores.domain.lineitem.MAXItemTaxIfc;
import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
//import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import max.retail.stores.domain.paytm.MAXPaytmQRCodeResponse;
import max.retail.stores.domain.MAXPaytmResponse;
import max.retail.stores.domain.tender.paytm.MAXPaytmTenderConstants;
import max.retail.stores.domain.tender.paytmqr.MAXPaytmQRCodeTenderConstants;
import max.retail.stores.domain.transaction.MAXLayawayPaymentTransaction;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

public class MAXPaytmQRTenderStatusCheckSite extends PosSiteActionAdapter{

	private static final long serialVersionUID = 1380316288505676435L; 
	
	public void arrive(BusIfc bus)
	{
		POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
		MAXTenderCargo cargo = (MAXTenderCargo) bus.getCargo();
		String orderId = null;
		try {
			
			TransactionIfc transaction = cargo.getTransaction();
			
			if(transaction instanceof MAXSaleReturnTransaction) {
				orderId = ((MAXSaleReturnTransaction) transaction).getPaytmQROrderId();
			}
			if(transaction instanceof MAXLayawayTransaction) {
				orderId =  ((MAXLayawayTransaction) transaction).getPaytmQROrderId();
			}
			if(transaction instanceof MAXLayawayPaymentTransaction) {
				orderId =  ((MAXLayawayPaymentTransaction) transaction).getPaytmQROrderId();
			}
			//Create connection
			if(orderId != null && !orderId.equalsIgnoreCase("")) {
				
			
			String uri = Gateway.getProperty("application", "transactionStatusPaytmQRCodeURL", "");
			
			MAXPaytmQRCodeResponse response = MAXPaytmQRCodeHelperUtiltiy.checkTransactionStatus(uri, orderId, cargo.getTillID(), cargo.getStoreStatus().getStore().getStoreID());
			
			MAXPaytmResponse PaytmResponse = null;
			
			if(response != null && response.isDataException())
			{	
				DialogBeanModel dialogModel = new DialogBeanModel();
				dialogModel.setResourceID("ServerOffline");
				dialogModel.setType(DialogScreensIfc.ERROR);
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				
				return;
			}
			if(response != null && response.getResultStatus() != null && 
					response.getResultStatus().equalsIgnoreCase("TXN_SUCCESS"))
			{	
				setResponseData(cargo, response);
				response.setReqRespStatus(MAXPaytmTenderConstants.RESPONSERECEIVED);
				
				String paytmResp = response.getPaytmResponse();
				response.setPaytmResponse(response.getOrderId() + " : " + paytmResp);
				response.setRequestTypeA(MAXPaytmTenderConstants.RESPONSENOTRECEIVEDORREQUESTSENTORREVERSE);
				cargo.setPaytmQRCodeResp(response);
				MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
				PaytmResponse = convertPaytmResponseData(response);
				PaytmResponse.setUrl(uri);
				paytmTrans.saveRequest(PaytmResponse);
				
				bus.mail("Success");
				return;
				
			}else if(response != null && ((response.getResultStatus() != null && response.getResultStatus().equalsIgnoreCase("PENDING"))))
			{
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
			
				setResponseData(cargo, response);
					response.setPaytmResponse(" : " + response.getPaytmResponse());
				//	cargo.setPaytmResp(response);
					MAXPaytmDataTransaction paytmTrans = new MAXPaytmDataTransaction();
					PaytmResponse = convertPaytmResponseData(response);
					PaytmResponse.setUrl(uri);
					paytmTrans.saveRequest(PaytmResponse);
				
						DialogBeanModel dialogModel = new DialogBeanModel();
						String[] messgArray = new String[1];
						
						messgArray[0] = "Payment Status not confirmed yet.";
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDINGERROR);
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
						dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"failure");
						uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
						return;
			}
			else if(response != null && ((response.getResultStatus() != null && response.getResultStatus().equalsIgnoreCase("TXN_FAILURE") || 
					response.getResultCode() != null)))
			{
				if(response.getAmountPaid() == null || 
						response.getAmountPaid().equals("null") || 
						response.getAmountPaid().equals(null))
				{
					response.setAmountPaid("0.00");
				}
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
				PaytmResponse.setUrl(uri);
				paytmTrans.saveRequest(PaytmResponse);
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] messgArray = new String[1];
				if(response.getResultMessage() != null)
					messgArray[0] = response.getResultMessage();
				else
					messgArray[0] = "Failure response received from paytm server.";
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
				dialogModel.setType(DialogScreensIfc.ERROR);
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
				PaytmResponse.setUrl(uri);
				paytmTrans.saveRequest(PaytmResponse);
				
			DialogBeanModel dialogModel = new DialogBeanModel();
				
				String[] messgArray = new String[1];
				messgArray[0] = response.getResultMessage();
				
					dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDINGERROR);
					dialogModel.setArgs(messgArray);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"failure");
					uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
			else
			{
				
				DialogBeanModel dialogModel = new DialogBeanModel();
				String[] messgArray = new String[1];
				messgArray[0] = "Error in sending request to Paytm";
				
				
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID(MAXPaytmTenderConstants.PAYTMERROR);
					dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
					dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"failure");
				uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;
			}
		}else {
			DialogBeanModel dialogModel = new DialogBeanModel();
			String[] messgArray = new String[1];
			
			messgArray[0] = "Paytm QR order Id not available in transaction.";
			dialogModel.setArgs(messgArray);
			dialogModel.setResourceID(MAXPaytmQRCodeTenderConstants.PAYTMQRCODEPENDINGERROR);
			dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK,"failure");
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
		response.setTotalTransactionAmt(cargo.getCurrentTransactionADO().getBalanceDue().toString());
		response.setRequestTypeB("S");
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
		paytmResponse.setRequestTypeB("S");
		paytmResponse.setTimeOut(MAXPaytmQRCodeConfig.get(MAXPaytmTenderConstants.CONNECTIONTIMEOUT));
		paytmResponse.setTransactionId(response.getTransactionId());		
		paytmResponse.setOrderId(response.getOrderId());
		paytmResponse.setRequestTypeA("R");
		paytmResponse.setReqRespStatus(response.getReqRespStatus());
		paytmResponse.setRequestTypeB(response.getRequestTypeB());		
		paytmResponse.setTimeOut(response.getTimeOut());
		paytmResponse.setPaytmResponse(response.getPaytmResponse());
		paytmResponse.setAmountPaid(response.getAmountPaid());
		return paytmResponse;
		
	}
}
