/********************************************************************************
 *   
 *	Copyright (c) 2019 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Jul 01, 2019		Purushotham Reddy 	Changes for POS_Amazon Pay Integration 
 *
 ********************************************************************************/

package max.retail.stores.pos.services.printing;

/**
 @author Purushotham Reddy Sirison
 **/

import java.io.Serializable;
import java.util.Date;

import oracle.retail.stores.domain.utility.EYSDate;

public class MAXEReceiptResponse implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private String otp;
	private String message;
	private String error;
	private String orderId;
	private String status;
	private String statusCode;
	private String statusMessage;
	private String walletTxnId;
	private String amountPaid;
	private String phoneNumber;
	private String requestTypeA;
	private String reqRespStatus;
	private String storeId;
	private String registerId;
	private String tillId;
	private EYSDate bussinessdate;
	private String transactionId;
	private String totalTransactionAmt;
	private String requestTypeB;
	private Date respReceivedDate;
	private String timeOut;
	private int responseCode;
	private String sendResponse;
	private String url;
	private boolean dataException;
	private String reasonCode;
	private String reasonDescription;
	private String amount;
	private String amazonTransactionId;
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public String getOTP() {
		return otp;
	}

	public void setOTP(String otp) {
		this.otp = otp;
	}

	public String getAmazonTransactionId() {
		return amazonTransactionId;
	}

	public void setAmazonTransactionId(String amazonTransactionId) {
		this.amazonTransactionId = amazonTransactionId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}

	public String getReasonDescription() {
		return reasonDescription;
	}

	public void setReasonDescription(String reasonDescription) {
		this.reasonDescription = reasonDescription;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the orderId
	 * 
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId
	 *            the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the statusMessage
	 */
	public String getStatusMessage() {
		return statusMessage;
	}

	/**
	 * @param statusMessage
	 *            the statusMessage to set
	 */
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	/**
	 * @return the walletTxnId
	 */
	public String getWalletTxnId() {
		return walletTxnId;
	}

	/**
	 * @param walletTxnId
	 *            the walletTxnId to set
	 */
	public void setWalletTxnId(String walletTxnId) {
		this.walletTxnId = walletTxnId;
	}

	/**
	 * @return the amountPaid
	 */
	public String getAmountPaid() {
		return amountPaid;
	}

	/**
	 * @param amountPaid
	 *            the amountPaid to set
	 */
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}

	/**
	 * @return the phoneNumber
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * @param phoneNumber
	 *            the phoneNumber to set
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * @return the requestTypeA
	 */
	public String getRequestTypeA() {
		return requestTypeA;
	}

	/**
	 * @param requestTypeA
	 *            the requestTypeA to set
	 */
	public void setRequestTypeA(String requestTypeA) {
		this.requestTypeA = requestTypeA;
	}

	/**
	 * @return the reqRespStatus
	 */
	public String getReqRespStatus() {
		return reqRespStatus;
	}

	/**
	 * @param reqRespStatus
	 *            the reqRespStatus to set
	 */
	public void setReqRespStatus(String reqRespStatus) {
		this.reqRespStatus = reqRespStatus;
	}

	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return storeId;
	}

	/**
	 * @param storeId
	 *            the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	/**
	 * @return the registerId
	 */
	public String getRegisterId() {
		return registerId;
	}

	/**
	 * @param registerId
	 *            the registerId to set
	 */
	public void setRegisterId(String registerId) {
		this.registerId = registerId;
	}

	/**
	 * @return the tillId
	 */
	public String getTillId() {
		return tillId;
	}

	/**
	 * @param tillId
	 *            the tillId to set
	 */
	public void setTillId(String tillId) {
		this.tillId = tillId;
	}

	/**
	 * @return the bussinessdate
	 */
	public EYSDate getBussinessdate() {
		return bussinessdate;
	}

	/**
	 * @param bussinessdate
	 *            the bussinessdate to set
	 */
	public void setBussinessdate(EYSDate bussinessdate) {
		this.bussinessdate = bussinessdate;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId
	 *            the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the totalTransactionAmt
	 */
	public String getTotalTransactionAmt() {
		return totalTransactionAmt;
	}

	/**
	 * @param totalTransactionAmt
	 *            the totalTransactionAmt to set
	 */
	public void setTotalTransactionAmt(String totalTransactionAmt) {
		this.totalTransactionAmt = totalTransactionAmt;
	}

	/**
	 * @return the requestTypeB
	 */
	public String getRequestTypeB() {
		return requestTypeB;
	}

	/**
	 * @param requestTypeB
	 *            the requestTypeB to set
	 */
	public void setRequestTypeB(String requestTypeB) {
		this.requestTypeB = requestTypeB;
	}

	/**
	 * @return the respReceivedDate
	 */
	public Date getRespReceivedDate() {
		return respReceivedDate;
	}

	/**
	 * @param respReceivedDate
	 *            the respReceivedDate to set
	 */
	public void setRespReceivedDate(Date respReceivedDate) {
		this.respReceivedDate = respReceivedDate;
	}

	/**
	 * @return the timeOut
	 */
	public String getTimeOut() {
		return timeOut;
	}

	/**
	 * @param timeOut
	 *            the timeOut to set
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
	 * @param responseCode
	 *            the responseCode to set
	 */
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	/**
	 * @return the paytmResponse
	 */
	public String getSendResponse() {
		return sendResponse;
	}

	/**
	 * @param paytmResponse
	 *            the paytmResponse to set
	 */
	public void setSendResponse(String sendResponse) {
		this.sendResponse = sendResponse;
	}

	/**
	 * @return the dataException
	 */
	public boolean isDataException() {
		return dataException;
	}

	/**
	 * @param dataException
	 *            the dataException to set
	 */
	public void setDataException(boolean dataException) {
		this.dataException = dataException;
	}
}
