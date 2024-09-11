/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     Nov 22, 2016	        Ashish Yadav		Changes for Employee Discount FES
 *
 ********************************************************************************/
package max.retail.stores.domain.employee;

import oracle.retail.stores.domain.employee.EmployeeIfc;

public interface MAXEmployeeIfc extends EmployeeIfc {

	public String getEmpoloyeeLocation();

	public String getEligibleAmount();

	public String getAvailableAmount();

	public String getSpecialEmployeeDiscountValue();

	public void setEmpoloyeeLocation(String value);

	public void setEligibleAmount(String value);

	public void setAvailableAmount(String value);

	public void setSpecialEmployeeDiscountValue(String value);

	public MAXEmployee getMaxEmployee();

	public void setMaxEmployee(MAXEmployee maxEmployee);

	public void setSocialSecurityNumber(String safeString);
	
	// Changes start for Rev 1.0 (Ashish : Employee Discount)
	public String getStatusCode();
	public void setStatusCode(String value);
	// Changes end for Rev 1.0
	// below code added by atul shukla for employee discount FES
	public String getCompanyName();
	public void setCompanyName(String value);
	// Added by Vaibhav starts Changes by Kajal Nautiyal for Employee Discount through OTP
	public String getLocale();
	public void setLocale(String value);
	public String getValidInvalidCheck();
	public void setValidInvalidCheck(String value);
	
		// ends
	
	
}
