/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *     Copyright (c) 2022-2023 MAX Hypermarket, Inc    All Rights Reserved. 
 *
 * Rev 1.0  March 22, 2022    Kamlesh Pant		Requirement Paytm
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain;

import java.io.Serializable;
import java.util.Date;

import oracle.retail.stores.domain.utility.EYSDate;

public class MAXPaytmQRCodeResponse implements Serializable{

	private String orderId;
	private String resultStatus;
	private String resultCode;
	private String resultMessage;
	private String txnId;	
	private String amountPaid;
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
	private String paytmResponse;
	private boolean dataException;
	
	private String qrCodeId;
	private String qrData;
	private String image;
	
	private String txtType;
	private String gatewayName;
	private String bankName;	
	private String paymentMode;
	private String mid;
	private String authRefId;
	private String bankTxnId;
	private String merchantUniqueReference;
	
	private String refId;
	private String refundId;
	
	
	
	/**
	 * @return the orderId
	 * 
	 */
	public String getOrderId() {
		return orderId;
	}
	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	/**
	 * @return the txnId
	 */
	public String getTxnId() {
		return txnId;
	}
	/**
	 * @param txnId the txnId to set
	 */
	public void setTxnId(String txnId) {
		this.txnId = txnId;
	}
	/**
	 * @return the amountPaid
	 */
	public String getAmountPaid() {
		return amountPaid;
	}
	/**
	 * @param amountPaid the amountPaid to set
	 */
	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	/**
	 * @return the requestTypeA
	 */
	public String getRequestTypeA() {
		return requestTypeA;
	}
	/**
	 * @param requestTypeA the requestTypeA to set
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
	 * @param reqRespStatus the reqRespStatus to set
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
	 * @param storeId the storeId to set
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
	 * @param registerId the registerId to set
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
	 * @param tillId the tillId to set
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
	 * @param bussinessdate the bussinessdate to set
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
	 * @param transactionId the transactionId to set
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
	 * @param totalTransactionAmt the totalTransactionAmt to set
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
	 * @param requestTypeB the requestTypeB to set
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
	 * @param respReceivedDate the respReceivedDate to set
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
	 * @param timeOut the timeOut to set
	 */
	public void setTimeOut(String timeOut) {
		this.timeOut = timeOut;
	}
	/**
	 * @return the paytmResponse
	 */
	public String getPaytmResponse() {
		return paytmResponse;
	}
	/**
	 * @param paytmResponse the paytmResponse to set
	 */
	public void setPaytmResponse(String paytmResponse) {
		this.paytmResponse = paytmResponse;
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
	public String getQrCodeId() {
		return qrCodeId;
	}
	public void setQrCodeId(String qrCodeId) {
		this.qrCodeId = qrCodeId;
	}
	public String getQrData() {
		return qrData;
	}
	public void setQrData(String qrData) {
		this.qrData = qrData;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getTxtType() {
		return txtType;
	}
	public void setTxtType(String txtType) {
		this.txtType = txtType;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public String getBankName() {
		return bankName;
	}
	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getAuthRefId() {
		return authRefId;
	}
	public void setAuthRefId(String authRefId) {
		this.authRefId = authRefId;
	}
	public String getBankTxnId() {
		return bankTxnId;
	}
	public void setBankTxnId(String bankTxnId) {
		this.bankTxnId = bankTxnId;
	}
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getMerchantUniqueReference() {
		return merchantUniqueReference;
	}
	public void setMerchantUniqueReference(String merchantUniqueReference) {
		this.merchantUniqueReference = merchantUniqueReference;
	}
	public String getRefId() {
		return refId;
	}
	public void setRefId(String refId) {
		this.refId = refId;
	}
	public String getRefundId() {
		return refundId;
	}
	public void setRefundId(String refundId) {
		this.refundId = refundId;
	}
	
}
