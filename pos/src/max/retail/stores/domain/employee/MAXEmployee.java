/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     Nov 22, 2016	        Ashish Yadav		Changes for Employee Discount FES
 *
 ********************************************************************************/
package max.retail.stores.domain.employee;

import oracle.retail.stores.domain.employee.Employee;

public class MAXEmployee extends Employee implements MAXEmployeeIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int availAmount = 0;

	public static int elligibleAmount = 0;

	// public static boolean specialDiscountFlag = false;

	protected String socialSecurityNumber = "";

	public static String employeeIDreturn = "";

	public static String amountToUpdated = "";

	public static boolean isUpdatedAmount = false;

	protected String employeeLocation = "";

	protected String eligibleAmount = "";

	protected String availableAmount = "";

	protected String specialEmployeeDisount = "";
	protected String companyName = "";

	public static MAXEmployee maxEmployee = null;
	
	// Changes start for Rev 1.0 (Ashish : Discount Employee)
	protected String statusCode = "";
	// Changes ends for Rev 1.0

	public MAXEmployee() {

	}
	
	//Added by Vaibhav for emp disc otp
		public String locale ="";
		public String ValidInvalidCheck ="";
		public String StatusCode ="";
		// ends

	public String getEmpoloyeeLocation() {
		// TODO Auto-generated method stub
		return employeeLocation;
	}

	public String getEligibleAmount() {
		// TODO Auto-generated method stub
		return eligibleAmount;
	}

	public String getAvailableAmount() {
		// TODO Auto-generated method stub
		return availableAmount;
	}

	public void setEmpoloyeeLocation(String value) {
		// TODO Auto-generated method stub
		employeeLocation = value;
	}

	public void setEligibleAmount(String value) {
		// TODO Auto-generated method stub
		eligibleAmount = value;
	}

	public void setAvailableAmount(String value) {
		// TODO Auto-generated method stub
		availableAmount = value;
	}

	public String getSpecialEmployeeDiscountValue() {
		// TODO Auto-generated method stub
		return specialEmployeeDisount;
	}

	public void setSpecialEmployeeDiscountValue(String value) {
		// TODO Auto-generated method stub
		specialEmployeeDisount = value;
	}

	public MAXEmployee getMaxEmployee() {
		if (maxEmployee != null)
			return maxEmployee;
		else
			return new MAXEmployee();
	}

	public void setMaxEmployee(MAXEmployee maxEmployee) {
		MAXEmployee.maxEmployee = maxEmployee;
	}

	@Override
	public void setSocialSecurityNumber(String safeString) {
		// TODO Auto-generated method stub
		socialSecurityNumber = safeString;
	}
// Changes starts for Rev 1.0 (Ashish : Employee Discount)
	@Override
	public String getStatusCode() {
		// TODO Auto-generated method stub
		return statusCode;
	}

	@Override
	public void setStatusCode(String value) {
		statusCode = value;
		
	}
// Changes starts for Rev 1.0 (Ashish : Employee Discount)

	// below code added by atul shukla 
	@Override
	public String getCompanyName() {
		// TODO Auto-generated method stub
		return companyName;
	}

	@Override
	public void setCompanyName(String companyName) {
		// TODO Auto-generated method stub
		this.companyName=companyName;
		
	}
	public String getLocale() {
		// TODO Auto-generated method stub
		
		return this.locale;
	}
	public void setLocale(String value) {
		// TODO Auto-generated method stub
				this.locale = value;
	}
	public String getValidInvalidCheck() {
		// TODO Auto-generated method stub
		
		return this.ValidInvalidCheck;
	}


	public void setValidInvalidCheck(String value) {
		// TODO Auto-generated method stub
		
		this.ValidInvalidCheck = value;
	}
}
