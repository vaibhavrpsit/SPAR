package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;
import java.util.ArrayList;


public class MAXDocumentStatusErrorResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private String requestType;
	private String status;
	private ArrayList<MAXValidationReport> validationReport = new ArrayList<MAXValidationReport>();
	private String referenceId;


	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public ArrayList<MAXValidationReport> getValidationReport() {
		return validationReport;
	}

	public void setValidationReport(ArrayList<MAXValidationReport> validationReport) {
		this.validationReport = validationReport;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

}
