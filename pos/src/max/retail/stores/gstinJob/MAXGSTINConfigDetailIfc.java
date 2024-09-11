package max.retail.stores.gstinJob;
import java.io.Serializable;

public interface MAXGSTINConfigDetailIfc extends Serializable {

	public String getParamName();
	public void setParamName(String paramName);
	public String getParamValue();
	public void setParamValue(String paramValue);
	public String getCreatedRecord();
	public void setCreatedRecord(String createdRecord);
	public String getModifiedRecord();
	public void setModifiedRecord(String modifiedRecord);
}
