package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;

public class MAXErrorResp implements Serializable{

	private static final long serialVersionUID = -6311048833805203290L;

	private String code;
	private String message;
	private String type;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}



}
