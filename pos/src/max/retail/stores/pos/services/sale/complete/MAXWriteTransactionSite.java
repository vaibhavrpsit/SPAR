package max.retail.stores.pos.services.sale.complete;

// Rev 1.0 Kajal Nautiyal Changes to capture SubInvReqRep for Reporting purpose

import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import max.retail.stores.domain.SubInvReqRep.MAXSubInvReqRep;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.arts.MAXSaveManagerOverrideTransaction;
import max.retail.stores.domain.arts.MAXSaveSubInvReqRep;
import max.retail.stores.domain.lineitem.MAXSaleReturnLineItemIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;
import max.retail.stores.pos.services.sale.MAXSaleCargo;
import max.retail.stores.pos.services.sale.complete.submitinvoiceresponse.OxigenSubmitInvoiseResponse;
import max.retail.stores.pos.services.tender.oxigenwallet.MAXOxigenTenderConstants;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.services.sale.complete.WriteTransactionSite;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
public class MAXWriteTransactionSite extends WriteTransactionSite {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpsURLConnection connection=null;

	String mobileNumber=null;
	StringBuilder response;
	
	//StringBuilder response = new StringBuilder();
 
	@Override
    public void arrive(BusIfc bus)
    {  
		response=new StringBuilder();
		MAXSaleCargo cargo = (MAXSaleCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        
        // clear the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
        
        //mobileNumber = (cargo.getTransaction().getCustomer() != null)? (cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber() != null? cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber().toString(): null): null;
       if (cargo.getTransaction().getCustomer()!=null&&cargo.getTransaction().getCustomer().getPrimaryPhone()!=null&&cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber()!=null) {
    	   //if (cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber()!=null) {
    		  // submitInvoice(bus,ui);
    		  
		//}
	}
       MAXSaleCargo saleCargo = (MAXSaleCargo) bus.getCargo();
   	JSONObject jsonOxigenSubmitInvoiceRequest = getJsonRequestObject(saleCargo);
      

   	
       saveInvoice(bus,response,jsonOxigenSubmitInvoiceRequest);
       
     
       
       bus.mail(new Letter("Save"), BusIfc.CURRENT);
        
    }

	private void submitInvoice(BusIfc bus, POSUIManagerIfc ui) {
		MAXSaleCargo saleCargo = (MAXSaleCargo) bus.getCargo();
		
		DialogBeanModel dialogModel = new DialogBeanModel();
		
		JSONObject jsonOxigenSubmitInvoiceRequest = getJsonRequestObject(saleCargo);
		
		        String url = Gateway.getProperty("application", "OxigenSumbitInvoiceURL", "");
				//String url = "http://landmark-qa.thnxwallet.com/api/v1/invoice/submit";
				logger.info("URL of Oxigen Sumbit Invoice API is " + url);
				System.out.println("URL of Oxigen Sumbit Invoice API is " + url);
				logger.info("Request of Oxigen Sumbit Invoice API is " + jsonOxigenSubmitInvoiceRequest.toString());
				System.out.println("Request of Oxigen Sumbit Invoice API is " + jsonOxigenSubmitInvoiceRequest.toString());
				// Call CRM API
				
				String oxigenSubmitInvoiseResponse = null;
				String[] messgArray = new String[1];
				try {
					oxigenSubmitInvoiseResponse = executesubmitInvoiseRequest(url,jsonOxigenSubmitInvoiceRequest);
					
					OxigenSubmitInvoiseResponse submitInvoiseResponseResponse = handleOxigenSubmitInvoiseResponse(oxigenSubmitInvoiseResponse);
					
					if(submitInvoiseResponseResponse!=null&&submitInvoiseResponseResponse.getResponseHeader() != null&&submitInvoiseResponseResponse.getResponseHeader().getResponseCode() != null&&submitInvoiseResponseResponse.getResponseHeader().getResponseCode().equalsIgnoreCase(MAXOxigenTenderConstants.SUCCESS))
					{
						//System.out.println("In maxWriteTransactionsite");
						saleCargo.getTransaction().setSubmitinvresponse("Y");
						 bus.mail(new Letter("Save"), BusIfc.CURRENT);
						 
						
						return;
					}
					
					else 
					{
						  messgArray[0] = submitInvoiseResponseResponse.getResponseHeader().getResponseMsg();
						  dialogModel.setArgs(messgArray);
						dialogModel.setResourceID("SubmitInvoiceFailureMessage");
						dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
			            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Save");
				        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				        saleCargo.getTransaction().setSubmitinvresponse("N");
						return;
						 
					}
				}
				catch (SocketTimeoutException socketTimeOutException) {
					if(socketTimeOutException.getMessage()!=null)
					messgArray[0] = socketTimeOutException.getMessage();
					else
						messgArray[0] = "Connection Time Out";	
					dialogModel.setArgs(messgArray);
					dialogModel.setResourceID("SubmitInvoiceFailureMessage");
					 dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
					 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "RetrySubmitInvoice");
			        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Save");
			        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				} 
				
				catch (ConnectException  connectException) {
					if(connectException.getMessage()!=null)
						messgArray[0] = connectException.getMessage();
						else
							messgArray[0] = "Connection Exception";	
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID("SubmitInvoiceFailureMessage");
					dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
					 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "RetrySubmitInvoice");
			        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Save");
			        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}
				catch(NoRouteToHostException noRouteToHostException){
					if(noRouteToHostException.getMessage()!=null)
						messgArray[0] = noRouteToHostException.getMessage();
						else
							messgArray[0] = "Host Exception";	
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID("SubmitInvoiceFailureMessage");
					dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
					 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "RetrySubmitInvoice");
			        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Save");
			        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}
				catch (UnknownHostException unknownHostException) {
					if(unknownHostException.getMessage()!=null)
						messgArray[0] = unknownHostException.getMessage();
						else
							messgArray[0] = "Unknown Host Exception";	
						dialogModel.setArgs(messgArray);
						dialogModel.setResourceID("SubmitInvoiceFailureMessage");
					dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
					 dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "RetrySubmitInvoice");
			        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Save");
			        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
					return;
				}catch (JsonParseException jsonParseException) {
				messgArray[0] = "Error In Calling Oxigen Webservice Request";	
				dialogModel.setArgs(messgArray);
				dialogModel.setResourceID("SubmitInvoiceFailureMessage");
				dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
	            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Save");
		        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
				return;}
				
				
				
				catch (Exception e) {
					e.printStackTrace();
					logger.error("Error in setting oxigen webservice calling " + e.getMessage());
				}
				
				finally {
					if(connection != null) {
						connection.disconnect(); 
					}
				}
				
			} 

	
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private JSONObject getJsonRequestObject(MAXSaleCargo cargo) {
		JSONObject job = new JSONObject();
		JSONArray modeOfPayment = new JSONArray();
		modeOfPayment.add(MAXOxigenTenderConstants.MODE_OF_PAYMENT_WALLET);
		Date date= new Date();
		 SimpleDateFormat format = new SimpleDateFormat(
				    "yyyy-MM-dd'T'HH:mm:ss.SSS");
				String requestTimestamp=format.format(date);
				SimpleDateFormat invoiceDateformat = new SimpleDateFormat(
						"dd-MM-yyyy HH:mm:ss");
				
				
				String mobileNumber = (cargo.getTransaction().getCustomer() != null)
						? (cargo.getTransaction().getCustomer().getPrimaryPhone()!=null ? (cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber() != null
								? cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber().toString()
								: "9826012345"):"9826012345")
						: "9826012345";
		Map m1 = new LinkedHashMap(1);
		job.put(MAXOxigenTenderConstants.TRANSACTIONID, cargo.getTransaction().getTransactionID().toString());
		m1.put(MAXOxigenTenderConstants.REQUEST_TYPE, MAXOxigenTenderConstants.REQUEST_TYPE_SUBMIT_INVOICE);
		m1.put(MAXOxigenTenderConstants.REQUEST_ID, cargo.getTransaction().getTransactionID().toString());
		m1.put(MAXOxigenTenderConstants.REQUEST_TIME, requestTimestamp.toString());
		m1.put(MAXOxigenTenderConstants.MOBILENUMBER, mobileNumber);
		//m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, ((MAXSaleReturnTransaction) cargo.getTransaction()).geteWalletTraceId().toString());
		m1.put(MAXOxigenTenderConstants.ORIGINAL_DIALOGUE_TRACE_ID, null);
		m1.put(MAXOxigenTenderConstants.WALLET_OWNER, MAXOxigenTenderConstants.SPAR_CONSTANT);
		m1.put(MAXOxigenTenderConstants.CHANNEL,MAXOxigenTenderConstants.POS_CONSTANT);
		job.put(MAXOxigenTenderConstants.REQUEST_HEADER, m1);
		m1 = new LinkedHashMap(2);
		m1.put(MAXOxigenTenderConstants.STORE_CODE, cargo.getStoreStatus().getStore().getStoreID());
		m1.put(MAXOxigenTenderConstants.TERMINAL_ID, cargo.getRegister().getWorkstation().getWorkstationID());
		m1.put(MAXOxigenTenderConstants.OPTIONAL_INFO, null);
		job.put(MAXOxigenTenderConstants.STORE_DETAILS, m1);
		m1 = new LinkedHashMap(3);
		m1.put(MAXOxigenTenderConstants.INVOICENO, cargo.getTransaction().getTransactionID().toString());
		m1.put(MAXOxigenTenderConstants.INVOICEDATE, invoiceDateformat.format((cargo.getTransaction()).getBusinessDay().toDate()));
		m1.put(MAXOxigenTenderConstants.INVOICEGROSSAMOUNT, (cargo.getTransaction()).getTransactionTotals().getGrandTotal().abs().toString());
		m1.put(MAXOxigenTenderConstants.INVOICENETAMOUNT, (cargo.getTransaction()).getTransactionTotals().getGrandTotal().abs().subtract((cargo.getTransaction()).getTransactionTotals().getTaxTotal().abs()));
		//System.out.println("NETamount"+(cargo.getTransaction()).getTransactionTotals().getGrandTotal().abs().subtract((cargo.getTransaction()).getTransactionTotals().getTaxTotal().abs()));
		m1.put(MAXOxigenTenderConstants.MODEOFPAYMENT, modeOfPayment);
		m1.put(MAXOxigenTenderConstants.PROMOCODE, MAXOxigenTenderConstants.PROMO_CODE_BOGO);
		job.put(MAXOxigenTenderConstants.TRANSACTIONINFO, m1);
		job.put(MAXOxigenTenderConstants.ITEMS, getSaleLineItemsArray(cargo));
		return job;
	}
	
	private JSONArray getSaleLineItemsArray(MAXSaleCargo saleCargo) {
		

		 JSONObject job = new JSONObject();
		 JSONArray itemArray = new JSONArray();
		List<Map> listItemOfMaps = new ArrayList<>();
		 for (Enumeration e = (saleCargo.getTransaction()).getLineItemsVector().elements(); e.hasMoreElements();) {
				MAXSaleReturnLineItemIfc srli = (MAXSaleReturnLineItemIfc) e.nextElement();
				//count++;
				 Map saleItemAttributesMap = new LinkedHashMap(1);
				// saleItemAttributesMap.put(MAXOxigenTenderConstants.AMOUNT, srli.getExtendedDiscountedSellingPrice().toString());
				 saleItemAttributesMap.put(MAXOxigenTenderConstants.AMOUNT,(srli.getItemPrice().getSellingPrice().multiply(srli.getItemQuantityDecimal()).subtract(srli.getItemDiscountAmount())).toString());
				 saleItemAttributesMap.put(MAXOxigenTenderConstants.ITEMS_CODE, srli.getItemID().toString());
				 saleItemAttributesMap.put(MAXOxigenTenderConstants.INVOICE_ITEMS_QUANTITY, srli.getItemQuantityDecimal().toString());
				 saleItemAttributesMap.put(MAXOxigenTenderConstants.INVOICE_ITEMS_RATE, srli.getItemPrice().getSellingPrice().toString());
				 saleItemAttributesMap.put(MAXOxigenTenderConstants.INVOICE_ITEMS_VALUE, srli.getItemQuantityDecimal().toString());
				 saleItemAttributesMap.put(MAXOxigenTenderConstants.INVOICE_ITEMS_GROSS_AMOUNT, (srli.getItemPrice().getSellingPrice().multiply(srli.getItemQuantityDecimal())).toString());
				 saleItemAttributesMap.put(MAXOxigenTenderConstants.INVOICE_ITEMS_DISCOUNT_VALUE, srli.getItemDiscountAmount().toString());
				listItemOfMaps.add(saleItemAttributesMap);
				
		}
		 for (Map saleItemAttributesMap : listItemOfMaps) {
			 itemArray.add(saleItemAttributesMap);
		}
		
		 return itemArray;
		
	}

	public String executesubmitInvoiseRequest(String URL, JSONObject jsonContentObj) throws IOException, JsonParseException {

		URL targetURL = new URL(URL);

		connection = (HttpsURLConnection) targetURL.openConnection();
		
		SSLContext sc = null;
		try {
			sc = SSLContext.getInstance("TLSv1.2");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
			@Override
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(X509Certificate[] certs, String t) {
			}

			@Override
			public void checkServerTrusted(X509Certificate[] certs, String t) {
			}
		} };
		try {
			sc.init(null, certs, new java.security.SecureRandom());
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		connection.setSSLSocketFactory(sc.getSocketFactory());
		connection.setHostnameVerifier(new HostnameVerifier()
		{
			@Override
			public boolean verify(String hostname, SSLSession session)
			{
				return true;
			}
		});
		
		connection.setRequestMethod(MAXOxigenTenderConstants.REQUEST_METHOD_POST);
		String urlParameters = jsonContentObj.toString();


		connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTTYPE, MAXOxigenTenderConstants.JSON);
		connection.setRequestProperty(MAXOxigenTenderConstants.CONTENTLENGTH,
				Integer.toString(urlParameters.getBytes().length));
		connection.setUseCaches(false);
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);

		connection.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.close();
		int responseCode = connection.getResponseCode();
		InputStream is;
		if (responseCode == HttpURLConnection.HTTP_OK) {
			is = connection.getInputStream();
		} else {
			is = connection.getErrorStream();
		}

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		String line = "";
		while ((line = rd.readLine()) != null) {
			response.append(line);
			response.append('\r');
		}
		rd.close();
			logger.info("Submit Invoice Response Body:::" + response);
			System.out.println("\nSubmit Invoice Response Body:::" + response);
			return response.toString();
		}
	
	private OxigenSubmitInvoiseResponse handleOxigenSubmitInvoiseResponse(String oxigensubmitInvoiseResponse) throws JsonParseException, JsonMappingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		OxigenSubmitInvoiseResponse oxigenSubmitInvoiseResponse = mapper.readValue(oxigensubmitInvoiseResponse, OxigenSubmitInvoiseResponse.class);
		return oxigenSubmitInvoiseResponse;
	}
	// Rev 1.0 start
	
	public void saveInvoice(BusIfc bus,StringBuilder response,JSONObject jsonOxigenSubmitInvoiceRequest) {
	    SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
	   // cargo.getTransaction().setTransactionStatus(2);
	    
	   //System.out.println("Going inside MAXWriteTransactionSite");

	    
	    RetailTransactionIfc trans = cargo.getRetailTransaction();
		MAXSubInvReqRep mgo= new MAXSubInvReqRep();
		mgo.setBusinessDay(cargo.getTransaction().getBusinessDay());
		mgo.setTransactionID(cargo.getRetailTransaction().getTransactionID());
		mgo.setWsID(trans.getWorkstation().getWorkstationID().toString());
		mgo.setStoreID(trans.getWorkstation().getStoreID().toString());
		//System.out.println("3rd");
		mgo.setSUB_INV_REQ(jsonOxigenSubmitInvoiceRequest.toString());
		if(response!=null) {
		mgo.setSUB_INV_REP(response.toString());
		}
		else
		{
			mgo.setSUB_INV_REP("WebService could not reached");
			//System.out.print("connection not recieved");
		}
		MAXSaveSubInvReqRep dbTrans = null;
		dbTrans = (MAXSaveSubInvReqRep) DataTransactionFactory.create(MAXDataTransactionKeys.SAVE_SUBMIT_INV_REQ_REP);
		

		
		try {
			dbTrans.saveSubInvReqRep(mgo);
			if (response!=null && jsonOxigenSubmitInvoiceRequest!=null ) {
			response =null;
			jsonOxigenSubmitInvoiceRequest=null;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	   // bus.mail(new Letter("Override"), BusIfc.CURRENT);
		bus.mail(new Letter("Save"), BusIfc.CURRENT);
	  }
	// Rev 1.0 ends
}
