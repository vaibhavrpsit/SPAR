package max.retail.stores.gstinCentralJob.gstin;

public class Invoice implements InvoiceIfc {

	/**
	 *  mohan yadav
	 */
	private static final long serialVersionUID = 1L;

	String storeID=null;
	String regID=null;
	String businessDate=null;
	String txnID=null;	
	String txnType=null;	
	String txnStatus=null;	
	String custGSTIN=null;
	String storeGSTIN=null;
	String invoiceRequest=null;
	String invRefID=null;
	String documentNo=null;
	String ackNo=null;
	String ackDate=null;	
	String irn=null;
	String signed=null;
	String signedQRCode=null;
	String qRCode=null;
	String qRCodeData = null;
	String createdRecord=null;
	String modifiedRecord=null;
	
	public String getStoreID() {
		return storeID;
	}
	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}
	public String getRegID() {
		return regID;
	}
	public void setRegID(String regID) {
		this.regID = regID;
	}
	public String getBusinessDate() {
		return businessDate;
	}
	public void setBusinessDate(String businessDate) {
		this.businessDate = businessDate;
	}
	public String getTxnID() {
		return txnID;
	}
	public void setTxnID(String txnID) {
		this.txnID = txnID;
	}
	public String getTxnType() {
		return txnType;
	}
	public void setTxnType(String txnType) {
		this.txnType = txnType;
	}
	public String getTxnStatus() {
		return txnStatus;
	}
	public void setTxnStatus(String txnStatus) {
		this.txnStatus = txnStatus;
	}
	public String getCustGSTIN() {
		return custGSTIN;
	}
	public void setCustGSTIN(String custGSTIN) {
		this.custGSTIN = custGSTIN;
	}
	public String getStoreGSTIN() {
		return storeGSTIN;
	}
	public void setStoreGSTIN(String storeGSTIN) {
		this.storeGSTIN = storeGSTIN;
	}
	public String getInvoiceRequest() {
		return invoiceRequest;
	}
	public void setInvoiceRequest(String invoiceRequest) {
		this.invoiceRequest = invoiceRequest;
	}
	public String getInvRefID() {
		return invRefID;
	}
	public void setInvRefID(String invRefID) {
		this.invRefID = invRefID;
	}
	public String getDocumentNo() {
		return documentNo;
	}
	public void setDocumentNo(String documentNo) {
		this.documentNo = documentNo;
	}
	public String getAckNo() {
		return ackNo;
	}
	public void setAckNo(String ackNo) {
		this.ackNo = ackNo;
	}
	public String getAckDate() {
		return ackDate;
	}
	public void setAckDate(String ackDate) {
		this.ackDate = ackDate;
	}
	public String getIrn() {
		return irn;
	}
	public void setIrn(String irn) {
		this.irn = irn;
	}
	public String getSigned() {
		return signed;
	}
	public void setSigned(String signed) {
		this.signed = signed;
	}
	public String getSignedQRCode() {
		return signedQRCode;
	}
	public void setSignedQRCode(String signedQRCode) {
		this.signedQRCode = signedQRCode;
	}
	public String getqRCode() {
		return qRCode;
	}
	public void setqRCode(String qRCode) {
		this.qRCode = qRCode;
	}
	public String getqRCodeData() {
		return qRCodeData;
	}
	public void setqRCodeData(String qRCodeData) {
		this.qRCodeData = qRCodeData;
	}
	public String getCreatedRecord() {
		return createdRecord;
	}
	public void setCreatedRecord(String createdRecord) {
		this.createdRecord = createdRecord;
	}
	public String getModifiedRecord() {
		return modifiedRecord;
	}
	public void setModifiedRecord(String modifiedRecord) {
		this.modifiedRecord = modifiedRecord;
	}

	

}
