package max.retail.stores.gstinCentralJob.gstin;

import java.io.Serializable;

public interface InvoiceIfc extends Serializable {
	
	public String getStoreID();
	public void setStoreID(String storeID);
	public String getRegID();
	public void setRegID(String regID);
	public String getBusinessDate();
	public void setBusinessDate(String businessDate);
	public String getTxnID();
	public void setTxnID(String txnID);
	public String getTxnType();
	public void setTxnType(String txnType);
	public String getTxnStatus();
	public void setTxnStatus(String txnStatus);
	public String getCustGSTIN();
	public void setCustGSTIN(String custGSTIN);
	public String getStoreGSTIN();
	public void setStoreGSTIN(String storeGSTIN);
	public String getInvoiceRequest();
	public void setInvoiceRequest(String invoiceRequest);
	public String getInvRefID();
	public void setInvRefID(String invRefID);
	public String getDocumentNo();
	public void setDocumentNo(String documentNo);
	public String getAckNo();
	public void setAckNo(String ackNo);
	public String getAckDate();
	public void setAckDate(String ackDate);
	public String getIrn();
	public void setIrn(String irn);
	public String getSigned();
	public void setSigned(String signed);
	public String getSignedQRCode();
	public void setSignedQRCode(String signedQRCode);
	public String getqRCode();
	public void setqRCode(String qRCode);
	public String getqRCodeData();
	public void setqRCodeData(String qRCodeData);
	public String getCreatedRecord();
	public void setCreatedRecord(String createdRecord);
	public String getModifiedRecord();
	public void setModifiedRecord(String modifiedRecord);
}
