/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Oct 12, 2016		Atul Shukla		Mobikwik Tender Payment
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.tender.wallet.mobikwik;

public class MAXMobikwikObj 
{
	private String mobikwikURL = "";
	private String requestMethod = "";
	private String requestMessage = "";
    private String responseCode = "";
    private String connResponseCode = "";
  	private String connResponseMessage = "";
  	boolean result =false;
	private boolean  isOTPEnabled ;
	private String  resultCode ="";
	private String message="";
	private int totp ;
	
	//private boolean isOTPenabledstore =false;
	//private double loyaltypoint;
	private double mobikwikAmt;
	private String response;
	
	//private boolean isredeemcall2;
  	
	
	private String responseMessage = "";

	public void setMobikwik_URL(String mobikwikURL) {
		mobikwikURL = mobikwikURL;
	}

	public String getMobikwik_URL() {
		return mobikwikURL;
	}

	public void setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public void setRequestMessage(String requestMessage) {
		this.requestMessage = requestMessage;
	}

	public String getRequestMessage() {
		return requestMessage;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setConnResponseCode(String connResponseCode) {
		this.connResponseCode = connResponseCode;
	}

	public String getConnResponseCode() {
		return connResponseCode;
	}

	public void setConnResponseMessage(String connResponseMessage) {
		this.connResponseMessage = connResponseMessage;
	}

	public String getConnResponseMessage() {
		return connResponseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setOTPEnabled(boolean isOTPEnabled) {
		this.isOTPEnabled = isOTPEnabled;
	}

	public boolean isOTPEnabled() {
		return isOTPEnabled;
	}

	

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setOtp(int totp) {
		this.totp = totp;
	}

	public int getOtp() {
		return totp;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultCode() {
		return resultCode;
	}

	/*
	public void setOTPenabledstore(boolean isOTPenabledstore) {
		this.isOTPenabledstore = isOTPenabledstore;
	}

	public boolean isOTPenabledstore() {
		return isOTPenabledstore;
	}

	public void setIsredeemcall2(boolean isredeemcall2) {
		this.isredeemcall2 = isredeemcall2;
	}

	public boolean isIsredeemcall2() {
		return isredeemcall2;
	}
*/
	public void setMobikwikAmt(double points) {
		this.mobikwikAmt = points;
	}

	public double getMobikwikAmt() {
		return mobikwikAmt;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public String getResponse() {
		return response;
	}

}
