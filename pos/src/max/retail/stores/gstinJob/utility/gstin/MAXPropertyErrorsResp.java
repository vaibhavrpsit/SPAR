package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;
import java.util.ArrayList;

public class MAXPropertyErrorsResp implements Serializable {

	private static final long serialVersionUID = -6311048833805203290L;


	private String propertyName;
	private ArrayList<MAXErrorResp> errors = new ArrayList<MAXErrorResp>();
	


	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public ArrayList<MAXErrorResp> getErrors() {
		return errors;
	}
	public void setErrors(ArrayList<MAXErrorResp> errors) {
		this.errors = errors;
	}


	
}
