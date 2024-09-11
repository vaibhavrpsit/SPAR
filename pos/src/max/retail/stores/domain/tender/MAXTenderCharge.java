/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
*  Rev 1.1  22/MAy/2013	Jyoti Rawal, Changes for Credit Card FES
*  Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.tender.TenderCharge;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.utility.EYSDate;

/** MFL Change for Rev 1.1 : Start */
public class MAXTenderCharge extends TenderCharge implements MAXTenderChargeIfc
/** MFL Change for Rev 1.1 : End */
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7682608121378464275L;
	/**
	 * Credit card changes start
	 */
	private String acquiringBankCode = "";
	private String authRemarks = "";
	private String invoiceNumber = "";
	private String transactionAcquirer = "";
	private String batchNumber = "";
	private String retrievalRefNo = "";
	private String merchID = "";
	private String transactionType = "";
	
	private String tId = "";
	protected String authCode;
	protected String bankCode;
	protected String bankName;
	protected String aquirerStatus;
	protected boolean emiTransaction = false;
	protected HashMap responseDate = new HashMap();
	//Added by vaibhav for pine labs
    protected String rrnnumber="";
    
    //changes for paytmqr
    protected String merchantTransactionId;
    protected String orderNumber = null;
    protected String rrnNumber;
    protected String paytmUPIorWalletPaytment = null;

	/*Change for Rev 1.2: End*/
	/**
	 * Credit card changes end
	 */
	public MAXTenderCharge() {

	}

	/**
	 * Rev 1.0 changes start here
	 */
	public String getRrnnumber() {
		return rrnnumber;
	}

	public void setRrnnumber(String rrnnumber) {
		this.rrnnumber = rrnnumber;
	}
	protected String qcApprovalCode = null;

	public String getQcApprovalCode() {
		return qcApprovalCode;
	}

	public void setQcApprovalCode(String qcApprovalCode) {
		this.qcApprovalCode = qcApprovalCode;
	}

	public String getQcType() {
		return qcType;
	}

	public void setQcType(String qcType) {
		this.qcType = qcType;
	}

	protected String qcType = null;

	protected EYSDate qcExpiryDate = null;

	public EYSDate getQcExpiryDate() {
		return qcExpiryDate;
	}

	public void setQcExpiryDate(EYSDate qcExpiryDate) {
		this.qcExpiryDate = qcExpiryDate;
	}

	protected String qcTransId = null;

	public String getQcTransId() {
		return qcTransId;
	}

	public void setQcTransId(String qcTransId) {
		this.qcTransId = qcTransId;
	}

	/**
	 * Rev 1.0 changes end here
	 */
	/**
	 * Rev 1.1 Credit Card Changes start
	 */
	public String getAcquiringBankCode() {
		return acquiringBankCode;
	}

	public String getAuthRemarks() {
		return authRemarks;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public String getTransactionAcquirer() {
		return transactionAcquirer;
	}

	public void setAcquiringBankCode(String acqBankCode) {
		acquiringBankCode = acqBankCode;

	}

	public void setAuthRemarks(String authRem) {
		authRemarks = authRem;

	}

	public void setInvoiceNumber(String invNumber) {
		invoiceNumber = invNumber;
	}

	public void setTransactionAcquirer(String trnAcq) {
		transactionAcquirer = trnAcq;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNo) {
		batchNumber = batchNo;

	}

	public String getMerchID() {
		return merchID;
	}

	public String getRetrievalRefNumber() {
		return retrievalRefNo;
	}

	public void setMerchID(String merchId) {
		merchID = merchId;

	}

	public void setRetrievalRefNumber(String rrn) {
		retrievalRefNo = rrn;

	}

	// transaction type of credit tedner.
	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	/* get and set the credit TID no. */
	public String getTID() {
		return tId;
	}

	public void setTID(String tId) {
		this.tId = tId;

	}

	protected String lastFourDigits;

	public String getLastFourDigits() {
		return lastFourDigits;
	}

	public void setLastFourDigits(String lastFourDigits) {
		this.lastFourDigits = lastFourDigits;
	}
	public boolean isEmiTransaction()
	{
		return emiTransaction;
	}

	public void setEmiTransaction(boolean emiTransaction) 
	{
		this.emiTransaction = emiTransaction;
	}
	public String getAuthCode() {
		return authCode;
	}
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	public String getBankCode() {
		return bankCode;
	}
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	/**
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	 */
	public String getAquirerStatus() {
		return aquirerStatus;
	}
	public void setAquirerStatus(String aquirerStatus) {
		this.aquirerStatus = aquirerStatus;
	}
	
	public HashMap getResponseDate() {
		return responseDate;
	}
	public void setResponseDate(HashMap responseDate) {
		this.responseDate = responseDate;
	}
	
	//changes for paytmqr
	public String getMerchantTransactionId() {
		return merchantTransactionId;
	}

	public void setMerchantTransactionId(String merchantTransactionId) {
		this.merchantTransactionId = merchantTransactionId;
	}
	
	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getRrnNumber() {
		return rrnNumber;
	}

	public void setRrnNumber(String rrnNumber) {
		this.rrnNumber = rrnNumber;
	}
	
	public String getPaytmUPIorWalletPaytment() {
		return paytmUPIorWalletPaytment;
	}

	public void setPaytmUPIorWalletPaytment(String paytmUPIorWalletPaytment) {
		this.paytmUPIorWalletPaytment = paytmUPIorWalletPaytment;
	}
	public Object clone()
	{
	     MAXTenderChargeIfc tc = new MAXTenderCharge();
	     
	 
	  setCloneAttributes(tc);
	     
	   return tc;
	}
	
	protected void setCloneAttributes(MAXTenderChargeIfc newClass)
	{
	  super.setCloneAttributes(newClass);
	  
	  newClass.setAcquiringBankCode(this.acquiringBankCode);
	  newClass.setAuthRemarks(this.authRemarks);
	  newClass.setInvoiceNumber(this.invoiceNumber);
	  newClass.setTransactionAcquirer(this.transactionAcquirer);
	  newClass.setBatchNumber(this.batchNumber);
	  newClass.setRetrievalRefNumber(this.retrievalRefNo);
	  newClass.setMerchID(this.merchID);
	  newClass.setTransactionType(this.transactionType);
	  newClass.setTID(this.tId);
	 //newClass.setQcApprovalCode(this.qcApprovalCode);
	  newClass.setQcType(this.qcType);
	  newClass.setQcExpiryDate(this.qcExpiryDate);
	  newClass.setQcTransId(this.qcTransId);
	  newClass.setLastFourDigits(this.lastFourDigits);
		//Changes for Rev 1.1: Start
	  newClass.setEmiTransaction(this.emiTransaction);
		//Changes for Rev 1.1: End
	  
	  //changes for paytmqr
	 newClass.setMerchantTransactionId(this.merchantTransactionId);
	 newClass.setOrderNumber(this.orderNumber);
	 newClass.setRrnNumber(this.rrnNumber);
	 newClass.setPaytmUPIorWalletPaytment(this.paytmUPIorWalletPaytment);
															// object
			// compare all the attributes of TenderCash
			// Change for Rev 1.1 : Start
			// Change for Rev 1.1 : End
	
	  newClass.setAuthCode(this.authCode);
	/**
	 * @param phoneNumber the phoneNumber to set
	 */
	
	  newClass.setBankCode(this.bankCode);
	/**
	 * @param merchantTransactionId the merchantTransactionId to set
	 */



	  newClass.setResponseDate(this.responseDate);
 // end class TenderCharge
	  newClass.setBankName(this.bankName);
/**
 * @param bankCode
 *            the bankCode to set
 */
	  newClass.setAquirerStatus(this.aquirerStatus);
	// TODO Auto-generated method stub
}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}
	// TODO Auto-generated method stub

	
	
}


/* Changes for Rev 1.4 starts*/
/**
 * @return the phoneNumber
 */

/**
 * @param phoneNumber the phoneNumber to set
 */
//public void setPaytmPhoneNumber(String phoneNumber) {
	///this.phoneNumber = phoneNumber;
//}

/**
 * @return the otp
 */
//public String getPaytmTotp() {
	
////	return totp;
//}

/**
 * @param otp the otp to set
 */
//public void setPaytmTotp(String totp) {
	//this.totp = totp;
//}
/* Changes for Rev 1.4 ENDS*/
