/********************************************************************************
 *   
 *	Copyright (c) 2019  MAX Hypermarkets Pvt Ltd    All Rights Reserved.
 *  Rev 1.0     January 09, 2018        Vidhya.Kommareddi   TO Auto-Scheduling CR
 *
 ********************************************************************************/
package max.retail.stores.pos.services.tender.sbi;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import max.retail.stores.domain.arts.MAXConfigParameterTransaction;
import max.retail.stores.domain.arts.MAXDataTransactionKeys;
import max.retail.stores.domain.customer.MAXCustomerIfc;
import max.retail.stores.domain.customer.MAXTICCustomer;
import max.retail.stores.domain.transaction.MAXLayawayTransaction;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import max.retail.stores.pos.services.tender.MAXTenderCargo;
import max.retail.stores.pos.ui.MAXDialogScreensIfc;
import max.retail.stores.pos.ui.MAXEdgeDialogScreensIfc;
import max.retail.stores.pos.ui.MAXPOSUIManagerIfc;
import max.retail.stores.pos.ui.beans.MAXDialogBeanModel;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;


//--------------------------------------------------------------------------
/**
    Sets the currency selected.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class MAXSBIPointRedemptionAisle extends PosLaneActionAdapter
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
       revision number of this class
	 **/
	public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
	private static Logger log = Logger.getLogger(MAXSBIPointRedemptionAisle.class);

	//----------------------------------------------------------------------
	/**
       Sets the currency selected.
       <p>
       @param  bus     Service Bus
	 **/
	//----------------------------------------------------------------------
	public void traverse(BusIfc bus)
	{

		MAXTenderCargo cargo = (MAXTenderCargo)bus.getCargo();
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		String cardNumber = ui.getInput();
		String amount = cargo.getRedeemPointAmount();
		//System.out.println("Amount"+amount);
		MAXConfigParametersIfc configParam = getAllConfigparameter();
		//System.out.println("configParam "+configParam);
		int conversionRate = configParam.getSbiPointConversionRate();
		//System.out.println("conversionRate :"+conversionRate);
		POSBaseBeanModel  model=new POSBaseBeanModel();
		ui.showScreen(MAXPOSUIManagerIfc.PROCESS_CRM_REQUEST,model);

		MAXCustomerIfc customer = null;
		int totalPoints = 0;
		DecimalFormat decimalFormat = new DecimalFormat("##");
		if(cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
			customer = (MAXCustomerIfc) ((MAXSaleReturnTransaction) cargo.getTransaction()).getCustomer();
			//((MAXSaleReturnTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
		}else if(cargo.getTransaction() instanceof MAXLayawayTransaction) {
			customer = (MAXCustomerIfc) ((MAXLayawayTransaction) cargo.getTransaction()).getCustomer();
			//((MAXLayawayTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
		}
		//totalPoints =200;  // need to comment before delivery
		if(customer != null && customer.getMAXTICCustomer() != null && ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal() != null) {
			String sbiPoints = ((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal();
			//System.out.println("((MAXTICCustomer) customer.getMAXTICCustomer()).getSbiPointBal(); "+sbiPoints);
			totalPoints = Integer.parseInt(decimalFormat.format((int)Double.parseDouble(sbiPoints)));
		}
		int pointsconvert = Integer.parseInt(decimalFormat.format(((int)((Double.parseDouble(amount)*conversionRate)/12))));
		//System.out.println("\npointsconvert :"+pointsconvert);
		int pointstoconvert = pointsconvert*12;
		//System.out.println("\npointstoconvert :"+pointstoconvert);
		if(pointstoconvert>totalPoints) {
			pointstoconvert = totalPoints;
		}
		// Get value from application.properties
		//String messageID=(customer.getMessageId()!=null)?customer.getMessageId():"";
		// HashMap requestAttributes= new HashMap();
		JSONObject requestAttributes = new JSONObject();
		String storeId = Gateway.getProperty("application", "StoreID", "");
		int storeCode = Integer.parseInt(storeId.trim());
		requestAttributes.put("messageId", cargo.getTransaction().getTransactionID());
		requestAttributes.put("storeId", storeCode+"");
		requestAttributes.put("tillId", cargo.getTillID());
		//String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
		String timeStamp = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
		requestAttributes.put("invoiceDate", timeStamp);
		requestAttributes.put("invoiceNumber", cargo.getTransaction().getTransactionID());
		requestAttributes.put("sbiPointsToRedeem", pointstoconvert);
		requestAttributes.put("sbiCardLast4Digit", cardNumber);
		requestAttributes.put("mobileNumber", customer.getPrimaryPhone().getPhoneNumber());
		String requestBody = requestAttributes.toJSONString();
		System.out.println("\nMAXSBiPointRedemoption requestBody  :"+requestBody);
		log.info("SBI point conversion request body" + requestBody);
		String URL = Gateway.getProperty("application", "RedeemSBIRewardPoint", null);
		System.out.println("\nMAXSBiPointRedemoption URL : "+URL);
		log.info("SBI point conversion url" + URL);
		String response = executeValidationPost(URL, requestBody);
		System.out.println("\nMAXSBiPointRedemoption Response :"+response);
		log.info("SBI point conversion response" + response);

		String lmrPointBalBefore = "0";
		String convertedSbiPointBal = "0";
		String lmrPointBalAfter = "0";

		if(response !=null && !response.equals("")) {
			String trimString = response.toString().trim();
			JSONParser parser = new JSONParser();
			try {
				//	trimString = "{ \"messageId\": \"65675658887585\",   \"response\": \"S\",   \"message\": \"Successful Transaction\",   \"mobileNumber\": \"9986820708\",   \"lmrPointBalBefore\": 15.0,    \"lmrEquivalentSbiPoints\": 110.0,  \"lmrPointBalAfter\": 125.0 }\r\n";
				JSONObject jsonObject = (JSONObject) parser.parse(trimString);
				
				if(trimString.contains("response") &&  jsonObject.get("response") != null && (((String)jsonObject.get("response")).equalsIgnoreCase("S") || ((String)jsonObject.get("response")).equalsIgnoreCase("SUCCESS"))) {
					if (trimString.contains("amountBefore") &&  jsonObject.get("amountBefore") != null) {
						lmrPointBalBefore = ((Double)jsonObject.get("amountBefore")).toString();
						//System.out.println("lmrPointBalBefore :" + lmrPointBalBefore);
					}
					if (trimString.contains("equivalentAmount") &&  jsonObject.get("equivalentAmount") != null) {
						convertedSbiPointBal = ((Double)jsonObject.get("equivalentAmount")).toString();
						//System.out.println("convertedSbiPointBal :" + convertedSbiPointBal);
					}
					if (trimString.contains("amountAfter") &&  jsonObject.get("amountAfter") != null) {
						lmrPointBalAfter = ((Double)jsonObject.get("amountAfter")).toString();
						//System.out.println("lmrPointBalAfter :" + lmrPointBalAfter);
					}
					/*
					 CurrencyIfc convertedAmt = DomainGateway.getBaseCurrencyInstance("100");
					CurrencyIfc ltyValue = DomainGateway.getBaseCurrencyInstance("25");
					CurrencyIfc totalPoint = DomainGateway.getBaseCurrencyInstance("125");
					 */
					CurrencyIfc convertedAmt = DomainGateway.getBaseCurrencyInstance(convertedSbiPointBal);
					//System.out.println("\nconvertedAmt :"+convertedAmt);
					CurrencyIfc ltyValue = DomainGateway.getBaseCurrencyInstance(lmrPointBalBefore);
					//System.out.println("equivalentBefore :"+ltyValue);
					CurrencyIfc totalPoint = DomainGateway.getBaseCurrencyInstance(lmrPointBalAfter);
					//System.out.println("equivalentBalAfter :"+totalPoint);
					POSUIManagerIfc uiManager=(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
					if(cargo.getTransaction() instanceof MAXSaleReturnTransaction) {
						((MAXSaleReturnTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
					}else if(cargo.getTransaction() instanceof MAXLayawayTransaction) {
						((MAXLayawayTransaction) cargo.getTransaction()).setSbiRewardredeemFlag(true);
					}
					MAXDialogBeanModel dialogModel = new MAXDialogBeanModel();
					//System.out.println(dialogModel);
					String[] msg = new String[2];
					msg[0] = convertedAmt.getStringValue();		
					msg[1] = totalPoint.getStringValue();
					cargo.getSbiPointResp().setConvertedAmt(convertedAmt);
					cargo.getSbiPointResp().setTotalPoint(totalPoint);
					dialogModel.setArgs(msg);
					dialogModel.setResourceID("SBIPointDetailsNotice");
					dialogModel.setType(MAXEdgeDialogScreensIfc.SBI_POINT_INFORMATION);					
					dialogModel.setButtonLetter(MAXEdgeDialogScreensIfc.BUTTON_PARTIAL, "Partial");
					dialogModel.setButtonLetter(MAXEdgeDialogScreensIfc.BUTTON_CONVERTED, "Converted");
					dialogModel.setButtonLetter(MAXEdgeDialogScreensIfc.BUTTON_TOTAL, "Total");
			        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
		            // Display the dialog.
					
				}else {
					if (trimString.contains("message") &&  jsonObject.get("message") != null) {
						displayError(ui,(String) jsonObject.get("message"));
					}else {
						displayError(ui, "Unable to get response from CRM");
					}
				}
			}catch (Exception e) {
				System.out.println("Exception :"+ e);
				if (response.equalsIgnoreCase("Timeout")) {
					displayConnectionErrorDialogMessage(ui, "CRM_CONNECTIVITY_ERROR");
				}else {
					log.error(e);
					displayError(ui, "Unable to get response from CRM");
				}
			}

		}else {
			displayError(ui, "Unable to get response from CRM");
		}

		// redemption call required

	}

	private MAXConfigParametersIfc getAllConfigparameter() {

		MAXConfigParameterTransaction configTransaction = new MAXConfigParameterTransaction();
		MAXConfigParametersIfc configParameters = null;
		configTransaction = (MAXConfigParameterTransaction) DataTransactionFactory
				.create(MAXDataTransactionKeys.CONFIG_PARAMETER_TRANSACTION);

		try {
			configParameters = configTransaction.selectConfigParameters();
		} catch (DataException e1) {
			//e1.printStackTrace();
			log.error(e1.getMessage());
		}
		return configParameters;
	}

	public String executeValidationPost(String targetURL, String urlParameters) {
		URL url;
		HttpURLConnection connection = null;
		try {
			// Create connection
			url = new URL(targetURL);

			String timeOut = Gateway.getProperty("application",
					"RedeemSBIRewartimeOutInMilliSeconds", "5000");
			System.getProperties().setProperty(
					"sun.net.client.defaultConnectTimeout", timeOut);
			System.getProperties().setProperty(
					"sun.net.client.defaultReadTimeout", timeOut);
			connection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod("POST");

			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			//connection.setRequestProperty("authkey", "959F1B0A537EDDCDD4998DEF360CED3AB6D323AEC795B915");

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// Get Response
			log.error("responseCode:::" + connection.getResponseCode());
			InputStream is = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String line;
			StringBuffer response = new StringBuffer();
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
				//System.out.println("Line 273 :"+response);
			}
			rd.close();

			return response.toString();
		} catch (Exception e) {
			log.error("Error in sending CRM sbi Point conversion Request"
					+ e.getMessage() + "");

			return "Timeout";
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	protected void displayError(POSUIManagerIfc ui,  String message) {
		DialogBeanModel beanModel = new DialogBeanModel();
		beanModel.setResourceID("CRMCustomersearchError");
		String[] msg = new String[1];
		msg[0] = message;
		beanModel.setArgs(msg);
		beanModel.setType(DialogScreensIfc.ERROR);
		//beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Done");
		//beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.OK);
		//beanModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, CommonLetterIfc.RETRY);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
	}

	protected void displayConnectionErrorDialogMessage(POSUIManagerIfc ui, String resourceId) {
		DialogBeanModel beanModel = new DialogBeanModel();
		beanModel.setResourceID(resourceId);
		beanModel.setType(DialogScreensIfc.ERROR);
		beanModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.OK);
		ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, beanModel);
	}

	//----------------------------------------------------------------------
	/**
       Returns a string representation of the object.
       <P>
       @return String representation of object
	 **/
	//----------------------------------------------------------------------
	public String toString()
	{
		String strResult = new String("Class:  " + getClass().getName() + "(Revision " +
				getRevisionNumber() + ")" + hashCode());
		return(strResult);
	}

	//----------------------------------------------------------------------
	/**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
	 **/
	//----------------------------------------------------------------------
	public String getRevisionNumber()
	{
		return(revisionNumber);
	}
}
