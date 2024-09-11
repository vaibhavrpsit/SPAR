package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;
import java.util.ArrayList;


public class MAXDocumentStatusResp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	private String requestType;
	private String status;
	private ArrayList<MAXDataReport> dataReport = new ArrayList<MAXDataReport>();
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

	public ArrayList<MAXDataReport> getDataReport() {
		return dataReport;
	}

	public void setDataReport(ArrayList<MAXDataReport> dataReport) {
		this.dataReport = dataReport;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}



}
