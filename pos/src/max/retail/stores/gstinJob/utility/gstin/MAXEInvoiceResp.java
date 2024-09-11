package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;

public class MAXEInvoiceResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ackNumber;
	private String ackDate;
	private String irn;
	private String signedInvoice;
	private String signedQRCode;
	private String qrCode;
	private String qrCodeData;
	private String errors;

	public String getAckNumber() {
		return ackNumber;
	}
	public void setAckNumber(String ackNumber) {
		this.ackNumber = ackNumber;
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
	public String getSignedInvoice() {
		return signedInvoice;
	}
	public void setSignedInvoice(String signedInvoice) {
		this.signedInvoice = signedInvoice;
	}
	public String getSignedQRCode() {
		return signedQRCode;
	}
	public void setSignedQRCode(String signedQRCode) {
		this.signedQRCode = signedQRCode;
	}
	public String getQrCode() {
		return qrCode;
	}
	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}
	public String getQrCodeData() {
		return qrCodeData;
	}
	public void setQrCodeData(String qrCodeData) {
		this.qrCodeData = qrCodeData;
	}
	public String getErrors() {
		return errors;
	}
	public void setErrors(String errors) {
		this.errors = errors;
	}



}
