/* *****************************************************************************************
 * Copyright (c) 2015   Lifestyle India Pvt. Ltd.All Rights Reserved.
 *
 * Initial Draft 	October 17th,2015:	 Aakash Gupta(EYLLP)
 * *******************************************************************************************/
package max.retail.stores.ws.employee;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * POJO class for employee details which is marshaled into an XML for POS
 * application.
 *
 * @author Aakash Gupta
 */
@XmlRootElement(name = "EmployeeDetails")
@XmlType(propOrder = { "employeeID", "partyID", "loginID", "alternateID", "designation", "firstName", "lastName",
		"location", "statusCode", "workGroupID", "locale", "type", "storeID", "newPaswwordRequired",
		"passwordCreateDate", "failedPasswordCount", "role", "specialDiscountFlag", "discountAvailableAmount",
		"discountEligibleAmount", "validInvalidCheck" })
public class MAXEmployeeDetails {

	private String employeeID;
	private int partyID;
	private String loginID;
	private String alternateID;
	private String designation;
	private String firstName;
	private String lastName;
	private String location;
	private String statusCode;
	private int workGroupID;
	private String locale;
	private int type;
	private String storeID;
	private boolean newPaswwordRequired;
	private String passwordCreateDate;
	private int failedPasswordCount;
	private String role;
	private String specialDiscountFlag;
	private int discountAvailableAmount;
	private int discountEligibleAmount;
	private String validInvalidCheck;
	
	@XmlElement(name = "Employee_ID")
	public String getEmployeeID() {
		return employeeID;
	}

	public void setEmployeeID(String employeeID) {
		this.employeeID = employeeID;
	}

	@XmlElement(name = "Party_ID")
	public int getPartyID() {
		return partyID;
	}

	public void setPartyID(int i) {
		this.partyID = i;
	}

	@XmlElement(name = "Login_ID")
	public String getLoginID() {
		return loginID;
	}

	public void setLoginID(String loginID) {
		this.loginID = loginID;
	}

	@XmlElement(name = "Alternate_ID")
	public String getAlternateID() {
		return alternateID;
	}

	public void setAlternateID(String alternateID) {
		this.alternateID = alternateID;
	}

	@XmlElement(name = "Designation")
	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	@XmlElement(name = "First_Name")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name = "Last_Name")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlElement(name = "Location")
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@XmlElement(name = "Status_code")
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@XmlElement(name = "Workgroup_ID")
	public int getWorkGroupID() {
		return workGroupID;
	}

	public void setWorkGroupID(int i) {
		this.workGroupID = i;
	}

	@XmlElement(name = "Locale")
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	@XmlElement(name = "Employee_Type")
	public int getType() {
		return type;
	}

	public void setType(int i) {
		this.type = i;
	}

	@XmlElement(name = "Store_ID")
	public String getStoreID() {
		return storeID;
	}

	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	@XmlElement(name = "NewPassword_Flag")
	public boolean isNewPaswwordRequired() {
		return newPaswwordRequired;
	}

	public void setNewPaswwordRequired(boolean newPaswwordRequired) {
		this.newPaswwordRequired = newPaswwordRequired;
	}

	@XmlElement(name = "Password_Create_Date")
	public String getPasswordCreateDate() {
		return passwordCreateDate;
	}

	public void setPasswordCreateDate(String passwordCreateDate) {
		this.passwordCreateDate = passwordCreateDate;
	}

	@XmlElement(name = "Failed_Password_Count")
	public int getFailedPasswordCount() {
		return failedPasswordCount;
	}

	public void setFailedPasswordCount(int failedPasswordCount) {
		this.failedPasswordCount = failedPasswordCount;
	}

	@XmlElement(name = "Role")
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@XmlElement(name = "Special_Discount_Flag")
	public String isSpecialDiscountFlag() {
		return specialDiscountFlag;
	}

	public void setSpecialDiscountFlag(String string) {
		this.specialDiscountFlag = string;
	}

	@XmlElement(name = "Available_Discount_Amount")
	public int getDiscountAvailableAmount() {
		return discountAvailableAmount;
	}

	public void setDiscountAvailableAmount(int i) {
		this.discountAvailableAmount = i;
	}

	@XmlElement(name = "Eligible_Discount_Amount")
	public int getDiscountEligibleAmount() {
		return discountEligibleAmount;
	}

	public void setDiscountEligibleAmount(int i) {
		this.discountEligibleAmount = i;
	}

	@XmlElement(name = "Valid_Invalid_Check")
	public String getValidInvalidCheck() {
		return this.validInvalidCheck;
	}

	public void setValidInvalidCheck(String validInvalidCheck) {
		this.validInvalidCheck = validInvalidCheck;
	}
	
	
	
}