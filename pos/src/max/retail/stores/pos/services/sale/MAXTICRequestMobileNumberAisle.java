package max.retail.stores.pos.services.sale;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import max.retail.stores.domain.customer.MAXCustomer;
import max.retail.stores.domain.customer.MAXCustomerConstantsIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.loyalty.MAXTICCRMConstants;
import max.retail.stores.domain.transaction.MAXSaleReturnTransactionIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.services.tender.loyaltypoints.MAXWebRequestResponseInfoAisle;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.Gender;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class MAXTICRequestMobileNumberAisle extends PosLaneActionAdapter{
	
	
	private static final long serialVersionUID = 1L;
	HashMap requestAttributes;
	private static int responseCode;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.pos.services.sale.MAXTICRequestMobileNumberAisle.class);

	
	public void traverse(BusIfc bus) {
		
		
		if (bus.getCargo()!=null && bus.getCargo() instanceof MAXTenderCargo )
		{
		MAXTenderCargo cargo=(MAXTenderCargo)bus.getCargo();
		if(cargo.getTransaction() instanceof SaleReturnTransactionIfc){
		String PhoneNumber="";
		MAXSaleReturnTransactionIfc transaction = (MAXSaleReturnTransactionIfc)cargo.getTransaction();
		
		MAXCustomer customer=null;
		MAXTICCustomer ticCustomer=null;
		boolean trainingMode=false;
		boolean reentryMode=false;
		if(cargo.getRegister()!=null && cargo.getRegister().getWorkstation()!=null){
			trainingMode=cargo.getRegister().getWorkstation().isTrainingMode();
			reentryMode=cargo.getRegister().getWorkstation().isTransReentryMode();
			
		}
		
		
		if(cargo!=null && cargo.getCustomerInfo()!=null && cargo.getCustomerInfo().getPhoneNumber()!=null ){
			PhoneIfc phone=cargo.getCustomerInfo().getPhoneNumber();
			// Changes starts for code merging(commenting below line)
			// PhoneNumber=phone.getAreaCode()+phone.getPhoneNumber();
			 PhoneNumber=phone+phone.getPhoneNumber();
			 // Changes ends for code merging
		}
        
		if(transaction!=null && transaction.getCustomer()!=null && transaction.getCustomer() instanceof MAXCustomer ){
			customer=(MAXCustomer)transaction.getCustomer();
		}
		
		if(transaction!=null && transaction.getMAXTICCustomer()!=null && transaction.getMAXTICCustomer() instanceof MAXTICCustomer ){
			ticCustomer=(MAXTICCustomer)transaction.getMAXTICCustomer();
		}
		//Added By Chiranjib
		else if(transaction!=null && transaction.getMAXTICCustomer()==null 
				&& transaction.getCustomer()!=null && transaction.getCustomer() instanceof MAXCustomer 
				&& ((MAXCustomer)transaction.getCustomer()).getTiccustomer()!=null ){
			ticCustomer = (MAXTICCustomer) ((MAXCustomer)transaction.getCustomer()).getTiccustomer();
			transaction.setMAXTICCustomer(ticCustomer);
		}
		
		
		
		
		
		String amount ="0";
	       ParameterManagerIfc parameterManager = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
	       try {
	    	   amount = parameterManager.getStringValue(MAXCheckAmtEligibleAisle.MINIMUM_LOYALTY_AMT);
		} catch (ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CurrencyIfc eligiblePrice = DomainGateway.getBaseCurrencyInstance(amount);
		CurrencyIfc totalPrice = transaction.getTransactionTotals().getGrandTotal();
	
		String TICCustomerButton=Gateway.getProperty("application", "TICCustomerButton", "NO");
		if ((totalPrice.compareTo(eligiblePrice) == CurrencyIfc.EQUALS
				|| totalPrice.compareTo(eligiblePrice) == CurrencyIfc.GREATER_THAN) && !(customer!=null && customer.getCustomerType()!=null && customer.getCustomerType().equalsIgnoreCase(MAXCustomerConstantsIfc.CRM)) && !(ticCustomer!=null &&  ticCustomer.getTICCustomerID()!=null && !ticCustomer.getTICCustomerID().trim().equalsIgnoreCase("")) && (TICCustomerButton!=null && TICCustomerButton.equalsIgnoreCase("YES")) && !reentryMode && !trainingMode)
		{
	
       if(PhoneNumber!=null && !PhoneNumber.trim().equalsIgnoreCase("") && PhoneNumber.trim().length()==10){
        
		//MAXTICCustomerIfc ticCustomer = cargo.getTICCustomer(); 
		requestAttributes = new HashMap();
	    requestAttributes = populateHashMap(cargo, requestAttributes,PhoneNumber);

		String urlParameters = createURL(requestAttributes, bus);
		String URL = Gateway.getProperty("application",
				"LoyaltyTicWebServiceURL", null);
		String response = executePost(URL, urlParameters);
		//System.out.println("response:::"+response);
		String trimString = response.toString().trim();

	      JSONParser parser=new JSONParser();
	      try{
	    	  String responseStatus;
	         JSONObject jsonObject=(JSONObject)parser.parse(trimString);
	        if(jsonObject.get("responseStatus")!=null)
		    responseStatus = (String)jsonObject.get("responseStatus");
	      }catch(Exception e){
	    	 
	      }
		
		
         }
		}
		}
		}
       
	
	
        	
        //}
		//priya changes for crm request bug start
		//bus.mail(new Letter("Tender2"),BusIfc.CURRENT);
      //priya changes for crm request bug end
        
	}
	
	
	private HashMap populateHashMap(MAXTenderCargo cargo, HashMap requestAttributes,String PhoneNumber) {
		EYSDate date = DomainGateway.getFactory().getEYSDateInstance();
		SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMM-yyyy");
		
		requestAttributes.put(MAXTICCRMConstants.TIC_STORE_ID, cargo
				.getRegister().getWorkstation().getStore().getStoreID());
		requestAttributes.put(MAXTICCRMConstants.TIC_TILL_ID, cargo
				.getRegister().getCurrentTillID());
		

		// Get value from application.properties
		String URL = Gateway.getProperty("application", "LoyaltyTicWebServiceURL",
				null);
		
		String timeOut = Gateway.getProperty("application",
				"LoyaltytimeOutInMilliSeconds", null);

		
		requestAttributes.put(MAXTICCRMConstants.MOBILE_NUMBER, PhoneNumber.trim());

         requestAttributes.put(MAXTICCRMConstants.CASHIER_ID, cargo.getOperator().getEmployeeID());
		//requestAttributes.put(MAXTICCRMConstants.CASHIER_ID, cargo.getEmployee().getEmployeeID());
		
		
		StringBuffer requestID = new StringBuffer();
		requestID.append(cargo.getTransaction().getTransactionID());
		requestID.append(date
		    .toFormattedString(MAXTICCRMConstants.NEW_DATE_FORMAT));
		 
		requestAttributes.put(MAXTICCRMConstants.REQUEST_ID, requestID);
		requestAttributes.put(MAXTICCRMConstants.REQUEST_SOURCE, "POS");
		requestAttributes.put(MAXTICCRMConstants.CUSTOMER_TYPE, "NOTREGTIC");
		
		
		requestAttributes.put(MAXTICCRMConstants.TIC_INVOICE_NUM,cargo.getTransaction().getTransactionID());
		
		SimpleDateFormat dateBuss=new SimpleDateFormat("dd/MM/yyyy");
		try{
			requestAttributes.put(MAXTICCRMConstants.TIC_INVOICE_DATE, dateFormat.format(dateBuss.parse(cargo.getRegister().getBusinessDate().toFormattedString())));
		
		}catch(Exception e){
			
		}
		
		requestAttributes.put(MAXTICCRMConstants.INVOICE_AMOUNT,"0");
		

		// return HasMap with required values
		return requestAttributes;
	}

	
	protected String createURL(HashMap requestInfo, BusIfc bus) {
		String urlParameters;
		try {
			

			 
		      JSONObject obj = new JSONObject();

		      obj.put("mobileNum", new Long(requestInfo.get(MAXTICCRMConstants.MOBILE_NUMBER).toString()));
		      if(requestInfo.get(MAXTICCRMConstants.FIRST_NAME)!=null && !requestInfo.get(MAXTICCRMConstants.FIRST_NAME).toString().equalsIgnoreCase("")){
			   obj.put("firstName", requestInfo.get(MAXTICCRMConstants.FIRST_NAME).toString());
		      }
		      if(requestInfo.get(MAXTICCRMConstants.LAST_NAME)!=null && !requestInfo.get(MAXTICCRMConstants.LAST_NAME).toString().equalsIgnoreCase("")){
				obj.put("lastName", requestInfo.get(MAXTICCRMConstants.LAST_NAME).toString());
		     }
		      
		      if(requestInfo.get(MAXTICCRMConstants.EMAIL_ID)!=null && !requestInfo.get(MAXTICCRMConstants.EMAIL_ID).toString().equalsIgnoreCase("")){
				obj.put("emailId", requestInfo.get(MAXTICCRMConstants.EMAIL_ID).toString());
		      }
		      
		      if(requestInfo.get(MAXTICCRMConstants.PIN_CODE)!=null && !requestInfo.get(MAXTICCRMConstants.PIN_CODE).toString().equalsIgnoreCase("")){
		  		obj.put("pinCode", requestInfo.get(MAXTICCRMConstants.PIN_CODE).toString());
		      }
		      
		      if(requestInfo.get(MAXTICCRMConstants.DOB)!=null && !requestInfo.get(MAXTICCRMConstants.DOB).toString().equalsIgnoreCase("")){
				obj.put("dateOfBirth", requestInfo.get(MAXTICCRMConstants.DOB).toString());
		      }
		      
		  	  if(requestInfo.get(MAXTICCRMConstants.GENDER) !=null  && !requestInfo.get(MAXTICCRMConstants.GENDER).toString().equalsIgnoreCase("")  && (!requestInfo.get(MAXTICCRMConstants.GENDER).toString().equalsIgnoreCase(new Integer(Gender.GENDER_UNSPECIFIED).toString()) && !requestInfo.get(MAXTICCRMConstants.GENDER).toString().equalsIgnoreCase("Unspecified"))){
				obj.put("gender", requestInfo.get(MAXTICCRMConstants.GENDER).toString());
		  	  }
		      obj.put("storeCode", requestInfo.get(MAXTICCRMConstants.TIC_STORE_ID).toString());
		      obj.put("invoiceNumber", requestInfo.get(MAXTICCRMConstants.TIC_INVOICE_NUM).toString());
		      obj.put("invoiceDate", requestInfo.get(MAXTICCRMConstants.TIC_INVOICE_DATE).toString());
		      obj.put("invoiceAmount",new Double( requestInfo.get(MAXTICCRMConstants.INVOICE_AMOUNT).toString()));
		      obj.put("tillId", requestInfo.get(MAXTICCRMConstants.TIC_TILL_ID).toString());
		      obj.put("cashierId", requestInfo.get(MAXTICCRMConstants.CASHIER_ID).toString());
		      obj.put("requestSource", requestInfo.get(MAXTICCRMConstants.REQUEST_SOURCE).toString());
		      obj.put("requestId", requestInfo.get(MAXTICCRMConstants.REQUEST_ID).toString());
		      obj.put("customerType", requestInfo.get(MAXTICCRMConstants.CUSTOMER_TYPE).toString());
		   


			return obj.toJSONString();
		} catch (Exception e) {
			Logger.getLogger(MAXWebRequestResponseInfoAisle.class.getName()).log(null, e);
			return null;
		}
	}
	

	public static String executePost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
		
			// Create connection
			url = new URL(targetURL);

	
			
		   // System.out.println("urlParam:::"+urlParameters);
			String timeOut = Gateway.getProperty("application", "LoyaltytimeOutInMilliSeconds", "5000");
			System.getProperties().setProperty("sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty("sun.net.client.defaultReadTimeout", timeOut);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
		 
			connection.setRequestProperty("Content-Type", "application/json");
			
			connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			responseCode = connection.getResponseCode();
			
			//System.out.println("responseCode:::"+responseCode);

			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			return response.toString();
		} catch (Exception e) {
			logger.error("Error in sending Request" + e.getMessage() + "");
			try {
				responseCode = connection.getResponseCode();
			} catch (IOException e1) {
				logger.error("IO Exception Caught::" + e1.getMessage() + "");
				return "Timeout";
			}
			return "";

		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	
	
}
