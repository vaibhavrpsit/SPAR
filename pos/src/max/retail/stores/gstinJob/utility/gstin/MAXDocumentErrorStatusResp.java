package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;
import java.util.ArrayList;


public class MAXDocumentErrorStatusResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private String requestType;
	private String status;
	private ArrayList<MAXDataErrorReport> dataReport = new ArrayList<MAXDataErrorReport>();
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



	public ArrayList<MAXDataErrorReport> getDataReport() {
		return dataReport;
	}

	public void setDataReport(ArrayList<MAXDataErrorReport> dataReport) {
		this.dataReport = dataReport;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}



}
