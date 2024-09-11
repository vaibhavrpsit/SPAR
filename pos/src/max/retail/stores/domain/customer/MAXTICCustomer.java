/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.0     10/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.customer;

import java.io.Serializable;

public class MAXTICCustomer extends MAXCustomer implements MAXTICCustomerIfc, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1211128254566241107L;
	/**
	 * 
	 */
	private String ticCustomerID;
	private String firstName;
	private String LastName;
	private String mobileNumber;
	private String TICbirthdate;
	private String gender;
	private String email;
	private String pinNumber;
	private Boolean existingCustomer;
	
	private String ticCustomerTier;
	private String ticCustomerPoints;
	


	public MAXTICCustomer() { // Begin Customer()
	}

	// ---------------------------------------------------------------------
	/**
	 * Clones this object.
	 * <P>
	 * 
	 * @return cloned object
	 **/
	// ---------------------------------------------------------------------
	public Object clone() { // begin clone()
		MAXCustomer c = new MAXCustomer();

		// set attributes in clone
		setCloneAttributes(c);

		return ((Object) c);
	} // end clone()

	// ---------------------------------------------------------------------
	/**
	 * Sets attributes in clone.
	 * <P>
	 * 
	 * @param newClass
	 *            new instance of class
	 **/
	// ---------------------------------------------------------------------
	protected void setCloneAttributes(MAXTICCustomer newClass) { // begin
																	// setCloneAttributes()
		super.setCloneAttributes(newClass);
		newClass.setTICCustomerID(ticCustomerID);
		newClass.setTICFirstName(firstName);
		newClass.setTICLastName(LastName);
		newClass.setTICMobileNumber(mobileNumber);
		newClass.setTICbirthdate(TICbirthdate);
		newClass.setTICGender(gender);
		newClass.setTICEmail(email);
		newClass.setTICPinNumber(pinNumber);
		newClass.setExistingCustomer(existingCustomer);
		newClass.setTICCustomerTier(ticCustomerTier);
		newClass.setSbiPointBal(sbiPointBal);
	} // end setCloneAttributes()

	public void setTICFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getTICFirstName() {
		return firstName;
	}

	public void setTICLastName(String lastName) {
		LastName = lastName;
	}

	public String getTICLastName() {
		return LastName;
	}

	public void setTICMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getTICMobileNumber() {
		return mobileNumber;
	}

	public void setTICbirthdate(String TICbirthdate) {
		this.TICbirthdate = TICbirthdate;
	}

	public String getTICbirthdate() {
		return TICbirthdate;
	}

	public void setTICGender(String gender) {
		this.gender = gender;
	}

	public String getTICGender() {
		return gender;
	}

	public void setTICEmail(String email) {
		this.email = email;
	}

	public String getTICEmail() {
		return email;
	}

	public void setTICPinNumber(String pinNumber) {
		this.pinNumber = pinNumber;
	}

	public String getTICPinNumber() {
		return pinNumber;
	}

	public String getTICCustomerID() {
		return ticCustomerID;
	}

	public void setTICCustomerID(String ticCustomerID) {
		this.ticCustomerID = ticCustomerID;
	}

	public Boolean getExistingCustomer() {
		return existingCustomer;
	}

	public void setExistingCustomer(Boolean existingCustomer) {
		this.existingCustomer = existingCustomer;
	}

	@Override
	public String getTICCustomerTier() {
		return ticCustomerTier;
	}

	@Override
	public void setTICCustomerTier(String ticCusotmerTier) {
		this.ticCustomerTier = ticCusotmerTier;
	}

	public String getTICCustomerPoints() {
		return ticCustomerPoints;
	}

	/**
	 * @param customerPoints the customerPoints to set
	 */
	public void setTICCustomerPoints(String ticCustomerPoints) {
		this.ticCustomerPoints = ticCustomerPoints;
	}
	private String sbiPointBal ="0.0";
	public String getSbiPointBal() {
		return sbiPointBal;
	}


	public void setSbiPointBal(String sbiPointBal) {
		this.sbiPointBal = sbiPointBal;
	}

}
