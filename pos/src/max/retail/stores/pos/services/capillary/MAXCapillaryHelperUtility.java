/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.
  
  Rev 1.3 	Jan 06, 2017		Ashish Yadav		Hardcoded password for showing offers on loyalty points screen
  Rev 1.2 	Jan 06, 2017		Ashish Yadav		intial draft Changes for Online redemption loyalty OTP FES
  Rev 1.1  04/01/2017     		Nitesh Kumar 		For till Reconcillation
  Rev 1.0  08/07/2015     		Mohd Arif for 		Capillary Milestone3
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.capillary;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;
import com.capillary.isg.pos.api.Coupon;
import com.capillary.isg.pos.api.Customer;
import com.capillary.isg.pos.api.Loyalty;

public class MAXCapillaryHelperUtility {

	private static Logger logger = Logger.getLogger(MAXCapillaryHelperUtility.class);
	private static String tillId = MAXCapillaryConfig.get("till_id");
	//Changes start for rev 1.1
	private static String pwd = MAXCapillaryConfig.get("ou_code");
	//Changes ends for rev 1.1
	/*Method  used for Customer enrolment*/
	public HashMap enroll(HashMap requestMap, HashMap responseMap) {

		Customer cust= new Customer();
		requestMap.put(MAXCapillaryConstants.ENROLL_TILL_ID,tillId);
		cust.enroll(requestMap, responseMap);
		logger.info(">> capillaryDataMapForCustomer ::"+responseMap);
		return responseMap;

	}


	/*Method used for Customer search*/
	public HashMap lookup(HashMap request, HashMap responseMap)
	{   
		ArrayList customers=new ArrayList();
		MAXCapillaryCustomer capCustomer=new MAXCapillaryCustomer();
		request.put(MAXCapillaryConstants.LOOKUP_TILL_ID,tillId);
		// Changes starts for rev 1.3 (Ashish : Loyalty OTP)
		request.put("password", "spar123");
		// Changes ends for rev 1.3 (Ashish : Loyalty OTP)
		Customer cust= new Customer();
		//Code merging changes start : Patch : 16_CapillaryLoggers

	    logger.info(">> Request to capillary ::" + request);
		//cust.lookup(request, responseMap);
		logger.info(">> Response from capillary ::" + responseMap);
		//Code merging changes Ends : 16_CapillaryLoggers

		/*logger.info(">> capillaryDataMapForCustomer ::"+request);*/
		logger.info(">> capillaryDataMapForCustomer ::"+responseMap);
		// Changes starts for Rev 1.2 (Ashish : Loyalty OTP)
		/*if(responseMap.get(MAXCapillaryConstants.LOOKUP_TOTAL_POINTS)!=null){
			capCustomer.setPointsAvailable(responseMap.get(MAXCapillaryConstants.LOOKUP_TOTAL_POINTS).toString());
		}

		if(responseMap.get(MAXCapillaryConstants.LOOKUP_TIER)!=null){
			capCustomer.setTier(responseMap.get(MAXCapillaryConstants.LOOKUP_TIER).toString());
		}

		if(responseMap.get(MAXCapillaryConstants.LOOKUP_CO_BRANDED)!=null){
			capCustomer.setCoBranded(responseMap.get(MAXCapillaryConstants.LOOKUP_CO_BRANDED).toString());  
		}

		if(responseMap.get(MAXCapillaryConstants.LOOKUP_CUSTOMER_NAME)!="" && responseMap.get(MAXCapillaryConstants.LOOKUP_CUSTOMER_NAME)!=null){
			String str=responseMap.get(MAXCapillaryConstants.LOOKUP_CUSTOMER_NAME).toString();
			String vals = str.trim();
			capCustomer.setCustomerName(vals);
		}

		if(responseMap.get(MAXCapillaryConstants.LOOKUP_CUSTOMER_NAME)!=null){
			capCustomer.setFirstName(responseMap.get(MAXCapillaryConstants.LOOKUP_CUSTOMER_NAME).toString());
			}
			else{
				capCustomer.setCustomerName("AAAAAAAAAAAAA");
			}
		//capCustomer.setCustomerName(capCustomer.getCustomerName());	
		if(responseMap.get("Customer Card Number")!=null)	{
			capCustomer.setCardNumber(responseMap.get("Customer Card Number").toString());
		}
		if(responseMap.get("Customer Mobile")!=null){
			capCustomer.setMobile(responseMap.get("Customer Mobile").toString());
		}
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_CUSTOMER_LASTVISIT)!=null){
			String dateSt = new String(responseMap.get(MAXCapillaryConstants.LOOKUP_CUSTOMER_LASTVISIT).toString());
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try{
			Date ldate=formatter.parse(dateSt);
			EYSDate dateEYS=new EYSDate(ldate);
			//EYSDate dateEYS = EYSDate.getEYSDate(EYSDate.FORMAT_MMDDYYYY, dateSt);
			capCustomer.setLastVisit(dateEYS);
			}
			catch (ParseException e) {
					// logger.error(e)
			}
		}
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_POINTS_EXP_IN_12MONTH)!=null){
			capCustomer.setLastVisit12months(responseMap.get(MAXCapillaryConstants.LOOKUP_POINTS_EXP_IN_12MONTH).toString());
		}
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_POINTS_EXP_IN_3MONTH)!=null){
			capCustomer.setLastVisit3months(responseMap.get(MAXCapillaryConstants.LOOKUP_POINTS_EXP_IN_3MONTH).toString()); 
		}

		if(responseMap.get(MAXCapillaryConstants.LOOKUP_CARD_TYPE)!=null){
			capCustomer.setCardType(responseMap.get(MAXCapillaryConstants.LOOKUP_CARD_TYPE).toString()); 
		}
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_DOB)!=null){
			String dob = new String(responseMap.get(MAXCapillaryConstants.LOOKUP_DOB).toString())"15/06/2016";
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try{
			Date ldate=formatter.parse(dob);
			EYSDate dateEYS=new EYSDate(ldate);
		//	EYSDate dateEYS = EYSDate.getEYSDate(EYSDate.FORMAT_MMDDYYYY, dateSt);
			capCustomer.setBirthdate(dateEYS);
			}
			catch (ParseException e) {
				// logger.error(e)
			}
		} 
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_POINTS_EXP_IN_1MONTH)!=null){
			capCustomer.setPointsExpriringOn(responseMap.get(MAXCapillaryConstants.LOOKUP_POINTS_EXP_IN_1MONTH).toString()); 

		}
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_GENDER)!=null){
			capCustomer.setGender(responseMap.get(MAXCapillaryConstants.LOOKUP_GENDER).toString()); 

		}
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_PIN_CODE)!=null){
			capCustomer.setPinCode(responseMap.get(MAXCapillaryConstants.LOOKUP_PIN_CODE).toString()); 

		}
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_EMAIL_ID)!=null){
			capCustomer.setEmail(responseMap.get(MAXCapillaryConstants.LOOKUP_EMAIL_ID).toString()); 

		}*/
		if(responseMap.get(MAXCapillaryConstants.Offers)!=null){
			ArrayList offer=new ArrayList();
			offer =(ArrayList) responseMap.get(MAXCapillaryConstants.Offers);

			capCustomer.setOffers(offer);
			}
		// Changes ends for Rev 1.2 (Ashish : Loyalty OTP)
		customers.add(capCustomer);

		responseMap.put("Customers", customers);
		if(responseMap.get(MAXCapillaryConstants.LOOKUP_response_Code)==null)
		{	
			responseMap.put(MAXCapillaryConstants.LOOKUP_response_Code, "200");
		}
		return responseMap;
	}
	/*Method is used for Loyalty points validation */
	public HashMap isRedeem( HashMap requestMap , HashMap responseMap )
	{      
		Loyalty loyalty=new Loyalty();
		requestMap.put(MAXCapillaryConstants.ISREDEEM_TILL_ID,tillId);
		loyalty.isRedeem(requestMap, responseMap);
		return responseMap;

	}
	/*Method is used for Loyalty points Redemption */
	public HashMap redeem(HashMap requestMap , HashMap responseMap )
	{      

		Loyalty loyalty=new Loyalty();
		requestMap.put(MAXCapillaryConstants.REDEEM_TILL_ID,tillId);
		loyalty.redeem(requestMap, responseMap);
		return responseMap; 
	}
	/*Method is used for Coupons validation*/ 

	public boolean isRedeemCoupon(HashMap request,HashMap responseMap){
		/* HashMap requestMap = new HashMap(); 
		 HashMap responseMap = new HashMap();
		 responseMap.put("CUST_ID", externalId); 
		 responseMap.put("MOBILE_NO", mobileNumber);
		 responseMap.put("COUPON_NO", couponNumber);*/
		Coupon coupon=new Coupon();
		request.put("Till ID",tillId);
		// Changes for capillary coupon redemption start
		//Changes starts for rev 1.1
		request.put("password",pwd+"123");
		//Changes ends for rev 1.1
		// Changes for capillary coupon redemption End
		coupon.isRedeem(request, responseMap);
		
		logger.info("Request:"+request);
		logger.info("Response:"+responseMap);
		return true; 	 
	}
	public boolean redeemCoupon(HashMap request,HashMap responseMap){
		/* HashMap requestMap = new HashMap(); 
		 HashMap responseMap = new HashMap();
		 requestMap.put("CUST_ID", external_id);
		 requestMap.put("MOBILE_NO", mobileNumber);   
		 requestMap.put("BILL_AMNT", transactionAmt);   
		 requestMap.put("BILL_NUMBER", BillNumber); 		
		 requestMap.put("COUPON_NO", couponNumber);*/
		Coupon coupon=new Coupon();
		request.put("Till ID",tillId);
		coupon.redeem(request, responseMap);
		
		logger.info("Request:"+request);
		logger.info("Response:"+responseMap);
	
		return true;

	}
}