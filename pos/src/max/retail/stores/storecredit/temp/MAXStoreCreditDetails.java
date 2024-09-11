/* *****************************************************************************************
 * Copyright (c) 2015   Lifestyle India Pvt. Ltd.All Rights Reserved.
 *
 * Rev 1.1          January  06th,2018   Shilpa Rawal(EYLLP) GC_eGV_CN Redemption Cross OU
 *
 * Initial Draft 	December 4th,2015:	 Aakash Gupta(EYLLP)
 * *******************************************************************************************/

package max.retail.stores.storecredit.temp;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * POJO class for store credit details which is marshaled into an XML for POS
 * application.
 *
 * @author Aakash Gupta
 */
@XmlRootElement(name = "StoreCredit")

public class MAXStoreCreditDetails {

	private String storeCreditID;
	private Double amount;
	private String status;
	private String expiratonDate;
	private String redemptionTransactionID;
	private String redemptionDate;
	private Boolean valid;
	private boolean rollOutComplete;
	private String firstName;
	private String lastName;
	private Integer currencyID;
	private String idType;
	private boolean storeCreditLock;
	private String mobileNumber;

	/* Changes for Rev 1.1 Starts */
	private String issuingOraganizationUnit;

	/* Changes for Rev 1.1 Ends */

	@XmlElement(name = "ID")
	public String getStoreCreditID() {
		return storeCreditID;
	}

	public void setStoreCreditID(String storeCreditID) {
		this.storeCreditID = storeCreditID;
	}

	@XmlElement(name = "BALANCE_AMOUNT")
	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@XmlElement(name = "STATUS")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElement(name = "EXPIRATION_DATE")
	public String getExpiratonDate() {
		return expiratonDate;
	}

	public void setExpiratonDate(String expiratonDate) {
		this.expiratonDate = expiratonDate;
	}

	@XmlElement(name = "REDEMPTION_TRANSACTION")
	public String getRedemptionTransactionID() {
		return redemptionTransactionID;
	}

	public void setRedemptionTransactionID(String redemptionTransactionID) {
		this.redemptionTransactionID = redemptionTransactionID;
	}

	@XmlElement(name = "REDEMPTION_DATE")
	public String getRedemptionDate() {
		return redemptionDate;
	}

	public void setRedemptionDate(String redemptionDate) {
		this.redemptionDate = redemptionDate;
	}

	@XmlElement(name = "IS_VALID")
	public Boolean isValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	@XmlElement(name = "IS_ROLLOUT_COMPLETE")
	public boolean isRollOutComplete() {
		return rollOutComplete;
	}

	public void setRollOutComplete(boolean rollOutComplete) {
		this.rollOutComplete = rollOutComplete;
	}

	@XmlElement(name = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement(name = "CURRENCY_ID")
	public Integer getCurrencyID() {
		return currencyID;
	}

	public void setCurrencyID(Integer currencyID) {
		this.currencyID = currencyID;
	}

	@XmlElement(name = "CUST_ID_TYPE")
	public String getIdType() {
		return idType;
	}

	public void setIdType(String idType) {
		this.idType = idType;
	}

	/* Changes for Rev 1.1 Starts */

	@XmlElement(name = "Issuing_OU_ID")
	public String getIssuingOraganizationUnit() {
		return issuingOraganizationUnit;
	}

	public void setIssuingOraganizationUnit(String issuingOraganizationUnit) {
		this.issuingOraganizationUnit = issuingOraganizationUnit;
	}

	@XmlElement(name = "Store_Credit_Lock")
	public boolean isStoreCreditLock() {
		return storeCreditLock;
	}

	public void setStoreCreditLock(boolean storeCreditLock) {
		this.storeCreditLock = storeCreditLock;
	}

	/* Changes for Rev 1.1 Ends */
	
	@XmlElement(name = "mobile")
	public String getSCmobileNumber() {
		return mobileNumber;
	}
	public void setSCmobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
}
