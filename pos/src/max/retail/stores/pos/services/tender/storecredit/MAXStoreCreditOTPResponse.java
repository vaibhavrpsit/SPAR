/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *
 *	Rev 1.0 	May 14, 2024			Kamlesh Pant		Store Credit OTP:
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.storecredit;

import java.io.Serializable;

public class MAXStoreCreditOTPResponse implements Serializable{ 
	private String result;
	private String resultCode;
	private String resultMessage;
	private String OTP;
	private String timeOut;
	private int responseCode;
	private boolean dataException;
	private static final long serialVersionUID = 1L;
	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}
	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}
	/**
	 * @return the resultMessage
	 */
	public String getResultMessage() {
		return resultMessage;
	}
	/**
	 * @param resultMessage the resultMessage to set
	 */
	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}
	/**
	 * @return the oTP
	 */
	public String getOTP() {
		return OTP;
	}
	/**
	 * @param oTP the oTP to set
	 */
	public void setOTP(String oTP) {
		OTP = oTP;
	}
	/**
	 * @return the timeOut
	 */
	public String getTimeOut() {
		return timeOut;
	}
	/**
	 * @param timeOut the timeOut to set
	 */
	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}
	/**
	 * @return the responseCode
	 */
	public int getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	/**
	 * @return the dataException
	 */
	public boolean isDataException() {
		return dataException;
	}
	/**
	 * @param dataException the dataException to set
	 */
	public void setDataException(boolean dataException) {
		this.dataException = dataException;
	}
	
}
