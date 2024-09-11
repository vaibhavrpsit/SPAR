package max.retail.stores.gstinJob.utility.gstin;

public class GSTINInvoiceStatus implements GSTINInvoiceStatusIfc {

	/**
	 *  mohan yadav
	 */
	private static final long serialVersionUID = 1L;

		
	String locationGstin=null;
	String locationName=null;
	String documentNumber=null;
	String documentDate=null;	
	String supplyType=null;	
	String billFromGstin=null;	
	String portCode=null;
	/**
	 * @return the locationGstin
	 */
	public String getLocationGstin() {
		return locationGstin;
	}
	/**
	 * @param locationGstin the locationGstin to set
	 */
	public void setLocationGstin(String locationGstin) {
		this.locationGstin = locationGstin;
	}
	/**
	 * @return the locationName
	 */
	public String getLocationName() {
		return locationName;
	}
	/**
	 * @param locationName the locationName to set
	 */
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	/**
	 * @return the documentNumber
	 */
	public String getDocumentNumber() {
		return documentNumber;
	}
	/**
	 * @param documentNumber the documentNumber to set
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	/**
	 * @return the documentDate
	 */
	public String getDocumentDate() {
		return documentDate;
	}
	/**
	 * @param documentDate the documentDate to set
	 */
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
	/**
	 * @return the supplyType
	 */
	public String getSupplyType() {
		return supplyType;
	}
	/**
	 * @param supplyType the supplyType to set
	 */
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	/**
	 * @return the billFromGstin
	 */
	public String getBillFromGstin() {
		return billFromGstin;
	}
	/**
	 * @param billFromGstin the billFromGstin to set
	 */
	public void setBillFromGstin(String billFromGstin) {
		this.billFromGstin = billFromGstin;
	}
	/**
	 * @return the portCode
	 */
	public String getPortCode() {
		return portCode;
	}
	/**
	 * @param portCode the portCode to set
	 */
	public void setPortCode(String portCode) {
		this.portCode = portCode;
	}
	
	
}
