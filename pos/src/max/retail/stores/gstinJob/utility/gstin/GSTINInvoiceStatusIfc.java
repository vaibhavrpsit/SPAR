package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;

public interface GSTINInvoiceStatusIfc extends Serializable {
	
	public String getLocationGstin();
	public void setLocationGstin(String locationGstin);
	public String getLocationName();
	public void setLocationName(String locationName);
	public String getDocumentNumber();
	public void setDocumentNumber(String documentNumber);
	public String getDocumentDate();
	public void setDocumentDate(String documentDate);
	public String getSupplyType();
	public void setSupplyType(String supplyType);
	public String getBillFromGstin();
	public void setBillFromGstin(String billFromGstin);
	public String getPortCode();
	public void setPortCode(String portCode);
}
