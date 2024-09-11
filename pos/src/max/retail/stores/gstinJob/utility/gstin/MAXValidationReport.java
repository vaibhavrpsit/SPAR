package max.retail.stores.gstinJob.utility.gstin;

import java.io.Serializable;
import java.util.ArrayList;


public class MAXValidationReport implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	

	private String locationGstin;
	private String locationName;
	private String documentNumber;
	private String documentDate;
	private String supplyType;
	private String billFromGstin;
	private String custom1;
	private String custom2;
	private String custom3;
	private String custom4;
	private String custom5;
	private String custom6;
	private String custom7;
	private String custom8;
	private String custom9;
	private String custom10;	
	private ArrayList<MAXPropertyErrorsResp> propertyErrors = new ArrayList<MAXPropertyErrorsResp>();
	
	private String referenceId;
	public String getLocationGstin() {
		return locationGstin;
	}
	public void setLocationGstin(String locationGstin) {
		this.locationGstin = locationGstin;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getDocumentDate() {
		return documentDate;
	}
	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}
	public String getSupplyType() {
		return supplyType;
	}
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}
	public String getBillFromGstin() {
		return billFromGstin;
	}
	public void setBillFromGstin(String billFromGstin) {
		this.billFromGstin = billFromGstin;
	}
	public String getCustom1() {
		return custom1;
	}
	public void setCustom1(String custom1) {
		this.custom1 = custom1;
	}
	public String getCustom2() {
		return custom2;
	}
	public void setCustom2(String custom2) {
		this.custom2 = custom2;
	}
	public String getCustom3() {
		return custom3;
	}
	public void setCustom3(String custom3) {
		this.custom3 = custom3;
	}
	public String getCustom4() {
		return custom4;
	}
	public void setCustom4(String custom4) {
		this.custom4 = custom4;
	}
	public String getCustom5() {
		return custom5;
	}
	public void setCustom5(String custom5) {
		this.custom5 = custom5;
	}
	public String getCustom6() {
		return custom6;
	}
	public void setCustom6(String custom6) {
		this.custom6 = custom6;
	}
	public String getCustom7() {
		return custom7;
	}
	public void setCustom7(String custom7) {
		this.custom7 = custom7;
	}
	public String getCustom8() {
		return custom8;
	}
	public void setCustom8(String custom8) {
		this.custom8 = custom8;
	}
	public String getCustom9() {
		return custom9;
	}
	public void setCustom9(String custom9) {
		this.custom9 = custom9;
	}
	public String getCustom10() {
		return custom10;
	}
	public void setCustom10(String custom10) {
		this.custom10 = custom10;
	}
	public ArrayList<MAXPropertyErrorsResp> getPropertyErrors() {
		return propertyErrors;
	}
	public void setPropertyErrors(ArrayList<MAXPropertyErrorsResp> propertyErrors) {
		this.propertyErrors = propertyErrors;
	}
	public String getReferenceId() {
		return referenceId;
	}
	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	

}
