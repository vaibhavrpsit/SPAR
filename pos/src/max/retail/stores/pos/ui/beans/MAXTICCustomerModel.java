/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.1     Nov 08, 2016		Ashish Yadav		Cashless Loyalty FES
 *	Rev 1.0     Oct 18, 2016		Ashish Yadav		Code Merging
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ui.beans;

import java.util.List;

import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXTICCustomerModel extends  POSBaseBeanModel{
	
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -8540802640460212474L;
	
	/**   Array of gender types  **/
    protected   String[] genderTypes = null;
    protected   String firstName= null; 
    protected   String lastName= null; 
    protected   String mobile= null; 
    // Changes start for Rev 1.1 (Cashless Loyalty)
    protected   String birthDate= null;
	protected   String birthYear= null;
    //protected   String dob= null; 
	 // Changes ends for Rev 1.1 (Cashless Loyalty)
    protected   String pinCode= null; 
    protected   String gender= null; 
    protected   String email= null; 
    protected   List configList = null;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	// Changes start for Rev1.1 (Cashless Loyalty)
	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	public String getBirthYear() {
		return birthYear;
	}

	public void setBirthYear(String birthYear) {
		this.birthYear = birthYear;
	}

	/*public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}*/
	// Changes start for Rev1.1 (Cashless Loyalty)

	public String getPinCode() {
		return pinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List getConfigList() {
		return configList;
	}

	public void setConfigList(List configList) {
		this.configList = configList;
	}

	public String[] getGenderTypes() {
		return genderTypes;
	}

	public void setGenderTypes(String[] genderTypes) {
		this.genderTypes = genderTypes;
	}  
    
    
    

	

	
}
