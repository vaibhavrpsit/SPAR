/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 * Copyright (c) 2015 Lifestyle.    All Rights Reserved.  
 * 
 * Rev 2.0 -03/07/2023 for capillary coupon max discount validation
 * Rev 1.0 -12/06/2015 for capillary coupon discount changes to add class and subclass level in hierarchy by Mohd Arif*/
package max.retail.stores.pos.services.sale.validate;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author mohd.arif
 *
 */
public class MAXCreateMessagesCapillaryCRM extends DefaultHandler {

	public MAXCreateMessagesCapillaryCRM() {
	}

	private static Logger logger = Logger
	.getLogger(MAXCreateMessagesCapillaryCRM.class);
	protected static HashMap responseMap = new HashMap();

	/************************************
	 * COUPON
	 ************************************/

	/*
	 * private static String getResponseText(InputStream inStream) { return new
	 * Scanner(inStream).useDelimiter("\\A").next(); }
	 */

	/**
	 * Coupon - IsRedeemable()
	 * 
	 * @param capillaryCRMObj
	 * @return
	 */
	protected static MAXCapillaryCRM processCouponIsRedeemableResponseMessage(
			MAXCapillaryCRM capillaryCRMObj) {

		try {
			String jsonMessage = capillaryCRMObj.getConnResponseMessage();
			//			System.out.println(jsonMessage);
			logger.info(">> Response Recieved ::" + jsonMessage);
			List brandName = new ArrayList();
			List companyList = new ArrayList();
			List divisionList = new ArrayList();
			List groupList = new ArrayList();
			List departmentList = new ArrayList();
			/* start Rev 1.0 for capillary coupon discount  by Arif.....
			 * 
			 * 12/06/2015
			 */

			List classlist = new ArrayList();
			List subclasslist = new ArrayList();
			List skulist=new ArrayList();
			/* End Rev 1.0 for capillary coupon discount  by Arif.....
			 * 
			 * 12/06/2015
			 */
			String seriesCode = "";
			String discountOn = "";
			String discountType = "";
			Double discountValue = new Double(0);
			//Added by Kumar Vaibhav for validate max disc of coupon-Rev 2.0-start
			Double discountupto = new Double(0);
			//Added by Kumar Vaibhav for validate max disc of coupon-Rev 2.0-end
			Double minBillAmnt = new Double(0);
			Boolean itemStatusSuccess = new Boolean(false);
			Boolean is_redeemable = new Boolean(false);
			Boolean validWithDiscounted = new Boolean(false);
			String couponNo = "";
			String itemStatusCode = "";
			String itemStatusMessage = "";
			// Initializing JSON
			JSONParser jsonParser = new JSONParser();
			// Setting values in objects..
			Object object = jsonParser.parse(jsonMessage.toString());
			/*String JSON_FILE="D:\\MPOS_NOV_14\\maxpos\\skufile1.json";
			JSONParser parser = new JSONParser();
			Object object = parser.parse(new FileReader(JSON_FILE));*/

			//change by arif for poc..
			JSONObject jsonObject = (JSONObject) object;
			JSONObject response = (JSONObject) jsonObject.get("response");
			JSONObject status = (JSONObject) response.get("status");
			String responseCode = ((Long) status.get("code")).toString();
			String responseMsg = (String) status.get("message");
			Boolean responseSuccess = (Boolean) status.get("success");
			/*	System.out.println("The first code is: " + responseCode);
			System.out.println("The first message is: " + responseMsg);
			System.out.println("The first success is: " + responseSuccess);*/
			if (responseCode.equalsIgnoreCase("200")
					|| responseCode.equalsIgnoreCase("500")) {
				// take the elements of the json array
				JSONObject coupons = (JSONObject) response.get("coupons");
				JSONObject redeemable = (JSONObject) coupons.get("redeemable");
				couponNo = (String) redeemable.get("code");
				is_redeemable = ((Boolean) redeemable.get("is_redeemable"));

				JSONObject itemStatus = (JSONObject) redeemable
				.get("item_status");
				if (itemStatus.get("status") != null)
					itemStatusSuccess = ((Boolean) itemStatus.get("status"));
				else
					itemStatusSuccess = ((Boolean) itemStatus.get("success"));
				itemStatusCode = ((Long) itemStatus.get("code")).toString();
				itemStatusMessage = ((String) itemStatus.get("message"));
				if (responseCode.equalsIgnoreCase("200")
						&& itemStatusCode.equalsIgnoreCase("700")) {
					JSONObject seriesInfo = (JSONObject) redeemable
					.get("series_info");
					seriesCode = ((String) seriesInfo.get("series_code"));
					discountOn = ((String) seriesInfo.get("discount_on"));
					discountType = ((String) seriesInfo.get("discount_type"));
					discountValue = ((Double) seriesInfo.get("discount_value"));
					//Added by Kumar Vaibhav for validate max disc of coupon-Rev 2.0-start
					discountupto = ((Double) seriesInfo.get("discount_upto"));
					//Added by Kumar Vaibhav for validate max disc of coupon-Rev 2.0-end
					minBillAmnt = ((Double) seriesInfo.get("min_bill_amount"));
					validWithDiscounted = ((Boolean) seriesInfo.get("valid_with_discounted_item"));
					JSONObject categories = (JSONObject) seriesInfo
					.get("categories");
					if (categories != null) {
						JSONArray category = (JSONArray) categories
						.get("category");
						Iterator j = category.iterator();
						// take each value from the json array separately
						while (j.hasNext()) {
							JSONObject innerObj;
							Object value = j.next();
							if (value instanceof JSONObject) {
								innerObj = (JSONObject) value;
								String heirarchyLevel = (String) innerObj
								.get("level");
								String heirarchyCode = (String) innerObj
								.get("label");
								if (heirarchyLevel.equalsIgnoreCase("0"))
									companyList.add(heirarchyCode);
								else if (heirarchyLevel.equalsIgnoreCase("1"))
									divisionList.add(heirarchyCode);
								else if (heirarchyLevel.equalsIgnoreCase("2"))
									groupList.add(heirarchyCode);
								else if (heirarchyLevel.equalsIgnoreCase("3"))
									departmentList.add(heirarchyCode);
								/* start Rev 1.0 changes */
								else if (heirarchyLevel.equalsIgnoreCase("4"))
									classlist.add(heirarchyCode);
								else if (heirarchyLevel.equalsIgnoreCase("5"))
									subclasslist.add(heirarchyCode);
								/* End Rev 1.0 changes */
							}
						}
					}
					JSONObject brands = (JSONObject) seriesInfo.get("brands");
					if (brands != null) {
						JSONArray brand = (JSONArray) brands.get("brand");
						Iterator k = brand.iterator();

						while (k.hasNext()) {
							JSONObject innerBrandObj;
							Object brandValue = k.next();
							if (brandValue instanceof JSONObject) {
								innerBrandObj = (JSONObject) brandValue;
								brandName.add(((String) innerBrandObj
										.get("label")).toUpperCase());
							}
						}
					}
					//change by arif for POC..
					JSONObject skus = (JSONObject) seriesInfo.get("product_sku");
					if (skus != null) {
						skulist = (List) skus.get("sku");
					}

				}
				capillaryCRMObj.setItemStatus(itemStatusSuccess.toString());
				capillaryCRMObj.setItemStatusCode(itemStatusCode);
				capillaryCRMObj.setItemStatusMsg(itemStatusMessage);
				/*System.out.println("The Item code is: " + itemStatusCode);
				System.out.println("The Item message is: " + itemStatusMessage);
				System.out.println("The Item success is: " + itemStatusSuccess);*/
			}
			capillaryCRMObj.setResponseSuccess(responseSuccess.booleanValue());
			capillaryCRMObj.setResponseCode(responseCode);
			capillaryCRMObj.setResponseMessage(responseMsg);

			if (responseCode.equalsIgnoreCase("200")) {
				HashMap responseMap = new HashMap();
				responseMap.put("COUPON_SERIES_CODE", seriesCode);
				responseMap.put("COUPON_NO", couponNo);
				responseMap.put("IS_REDEEMABLE", is_redeemable);
				responseMap.put("VALID_WITH_DISCOUNTED", validWithDiscounted);
				responseMap.put("COUPON_DISC_ON", discountOn);
				responseMap.put("COUPON_DISC_TYPE", discountType);
				responseMap.put("COUPON_DISC_VALUE", discountValue);
				//Added by Kumar Vaibhav for validate max disc of coupon-Rev 2.0-start
				responseMap.put("COUPON_UPTO_DISC_VALUE", discountupto);
				
				//Added by Kumar Vaibhav for validate max disc of coupon-Rev 2.0-end
				responseMap.put("COUPON_MIN_BILL_AMNT", minBillAmnt);
				responseMap.put("COUPON_ITEM_HRCHY_CMPNY", companyList);
				responseMap.put("COUPON_ITEM_HRCHY_DIV", divisionList);
				responseMap.put("COUPON_ITEM_HRCHY_GRP", groupList);
				responseMap.put("COUPON_ITEM_HRCHY_DEP", departmentList);
				/* start Rev 1.0 changes */
				responseMap.put("COUPON_ITEM_HRCHY_CLS", classlist);
				responseMap.put("COUPON_ITEM_HRCHY_SBCLS", subclasslist);
				/* End Rev 1.0 changes */
				responseMap.put("COUPON_BRND_NAME", brandName);
				responseMap.put("COUPON_DISC_SKULIST", skulist);
				capillaryCRMObj.setTransData(responseMap);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}
		return capillaryCRMObj;
	}

	protected static String createCouponRedeemRequestMessage(
			MAXCapillaryCRM capillaryCRMObj) {
		// Initializing JSON
		StringWriter reqJsonMessage = new StringWriter();
		try {

			JSONObject objCustomerValues = new JSONObject();
			JSONObject objCustomer = new JSONObject();
			objCustomerValues.put("mobile", (String) capillaryCRMObj.getTransData()
					.get("MOBILE_NO"));
			objCustomerValues.put("email", "");
			objCustomerValues.put("external_id", (String) capillaryCRMObj
					.getTransData().get("CUST_ID"));
			objCustomer.put("customer", objCustomerValues);
			JSONObject objtransactionValues = new JSONObject();
			JSONObject objtransaction = new JSONObject();
			objtransactionValues.put("amount", (String) capillaryCRMObj
					.getTransData().get("BILL_AMNT"));
			objtransactionValues.put("number", (String) capillaryCRMObj
					.getTransData().get("BILL_NUMBER"));
			objtransaction.put("transaction", objtransactionValues);
			JSONObject code = new JSONObject();
			code.put("customer", objCustomerValues);
			code.put("code", (String) capillaryCRMObj.getTransData().get("COUPON_NO"));
			code.put("validation_code", "");
			code.put("transaction", objtransactionValues);
			JSONArray arrayCoupon = new JSONArray();
			arrayCoupon.add(code);

			JSONObject objRoot = new JSONObject();
			objRoot.put("coupon", arrayCoupon);
			JSONObject obj = new JSONObject();
			obj.put("root", objRoot);

			obj.writeJSONString(reqJsonMessage);
			//			System.out.println(reqJsonMessage.toString());
			logger.info(">> Request Sent ::" + reqJsonMessage.toString());
		} catch (IOException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return reqJsonMessage.toString();
	}

	protected static MAXCapillaryCRM processCouponRedeemResponseMessage(
			MAXCapillaryCRM capillaryCRMObj) {
		try {
			String jsonMessage = capillaryCRMObj.getConnResponseMessage();
			logger.info(">> Response Recieved ::" + jsonMessage);

			// Initializing JSON

			JSONParser jsonParser = new JSONParser();
			// Setting values in objects..
			Object object = jsonParser.parse(jsonMessage);
			//			System.out.println(jsonMessage);

			JSONObject jsonObject = (JSONObject) object;
			JSONObject response = (JSONObject) jsonObject.get("response");
			JSONObject status = (JSONObject) response.get("status");
			String responseCode = ((Long) status.get("code")).toString();
			String responseMsg = (String) status.get("message");
			Boolean responseSuccess = (Boolean) status.get("success");
			/*System.out.println("The Response code is: " + responseCode);
			System.out.println("The Response message is: " + responseMsg);
			System.out.println("The Response success is: " + responseSuccess);*/
			if (responseCode.equalsIgnoreCase("200") || responseCode.equalsIgnoreCase("500")) {
				JSONObject coupons = (JSONObject) response.get("coupons");
				if(coupons != null){
					JSONObject coupon = (JSONObject) coupons.get("coupon");
					if(coupon != null){
						JSONObject itemStatus = (JSONObject) coupon.get("item_status");
						Boolean itemStatusSuccess = (Boolean) itemStatus.get("success");
						String itemStatusCode = ((Long) itemStatus.get("code"))
						.toString();
						String itemStatusMsg = (String) itemStatus.get("message");

						/*System.out.println("The Item code is: " + itemStatusCode);
						System.out.println("The Item message is: " + itemStatusMsg);
						System.out.println("The Item success is: " + itemStatusSuccess);*/

						capillaryCRMObj.setItemStatus(itemStatusSuccess.toString());
						capillaryCRMObj.setItemStatusCode(itemStatusCode);
						capillaryCRMObj.setItemStatusMsg(itemStatusMsg);
					}
				}
			}
			capillaryCRMObj.setResponseSuccess(responseSuccess.booleanValue());
			capillaryCRMObj.setResponseCode(responseCode);
			capillaryCRMObj.setResponseMessage(responseMsg);

			logger.info(">> capillaryCRMObj ::" + capillaryCRMObj);
		} catch (ParseException e) {
			logger.error(e);
			e.printStackTrace();
		} 
		return capillaryCRMObj;
	}
}
