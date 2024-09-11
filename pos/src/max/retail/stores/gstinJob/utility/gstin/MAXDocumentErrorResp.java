package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;
import java.util.ArrayList;

public class MAXDocumentErrorResp implements Serializable {

	private static final long serialVersionUID = -6311048833805203290L;


	private String requestId;
	private ArrayList<MAXErrorResp> errors = new ArrayList<MAXErrorResp>();


	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public ArrayList<MAXErrorResp> getErrors() {
		return errors;
	}
	public void setErrors(ArrayList<MAXErrorResp> errors) {
		this.errors = errors;
	}

}