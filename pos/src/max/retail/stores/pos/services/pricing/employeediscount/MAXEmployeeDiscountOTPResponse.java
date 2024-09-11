package max.retail.stores.pos.services.pricing.employeediscount;

import java.io.Serializable;
import java.util.Date;

import oracle.retail.stores.domain.utility.EYSDate;

public class MAXEmployeeDiscountOTPResponse  implements Serializable{
	/**
	 * @author kajal nautiyal Employee Discount validation through OTP
	 */
	private String StoreId;
	private String OTP;
	
	
	private String resultStatus;
	private String resultCode;
	private String resultMessage;
	private String requestTypeA;
	private String reqRespStatus;
	private String requestTypeB;
	private Date respReceivedDate;
	private String timeOut;
	private int responseCode;
	private boolean dataException;
	public String getStoreId() {
		return StoreId;
	}
	public void setStoreId(String storeId) {
		StoreId = storeId;
	}
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	public String getResultCode() {
		return resultCode;
	}
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	public String otp;
	public String getResultOtp() {
		return otp;
	}
	public void setResultOtp(String otp) {
		this.otp = otp;
	}
	public String getRequestTypeA() {
		return requestTypeA;
	}
	public void setRequestTypeA(String requestTypeA) {
		this.requestTypeA = requestTypeA;
	}
	public String getReqRespStatus() {
		return reqRespStatus;
	}
	public void setReqRespStatus(String reqRespStatus) {
		this.reqRespStatus = reqRespStatus;
	}
	public String getRequestTypeB() {
		return requestTypeB;
	}
	public void setRequestTypeB(String requestTypeB) {
		this.requestTypeB = requestTypeB;
	}
	public Date getRespReceivedDate() {
		return respReceivedDate;
	}
	public void setRespReceivedDate(Date respReceivedDate) {
		this.respReceivedDate = respReceivedDate;
	}
	public String getTimeOut() {
		return timeOut;
	}
	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public boolean isDataException() {
		return dataException;
	}
	public void setDataException(boolean dataException) {
		this.dataException = dataException;
	}
	/*
	 * public String getOTP() { return OTP; } public void setOTP(String oTP) {
	 * this.OTP = OTP; }
	 */
	public String getOTP() {
		return OTP;
	}
	
	public void setOTP(String OTP) {
		this.OTP = OTP;
	}
	

}
