package max.retail.stores.gstinJob;

public class MAXGSTINConfigDetail implements MAXGSTINConfigDetailIfc {
	
	private static final long serialVersionUID = 1L;
	String paramName=null;
	String paramValue=null;
	String createdRecord=null;
	String modifiedRecord=null;
	/**
	 * @return the paramName
	 */
	public String getParamName() {
		return paramName;
	}
	/**
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	/**
	 * @return the paramValue
	 */
	public String getParamValue() {
		return paramValue;
	}
	/**
	 * @param paramValue the paramValue to set
	 */
	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}
	/**
	 * @return the createdRecord
	 */
	public String getCreatedRecord() {
		return createdRecord;
	}
	/**
	 * @param createdRecord the createdRecord to set
	 */
	public void setCreatedRecord(String createdRecord) {
		this.createdRecord = createdRecord;
	}
	/**
	 * @return the modifiedRecord
	 */
	public String getModifiedRecord() {
		return modifiedRecord;
	}
	/**
	 * @param modifiedRecord the modifiedRecord to set
	 */
	public void setModifiedRecord(String modifiedRecord) {
		this.modifiedRecord = modifiedRecord;
	}
	
	
}

