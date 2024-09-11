/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.0     10/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.customer;

public interface MAXTICCustomerIfc extends MAXCustomerIfc {

	public String getTICCustomerID();

	public void setTICCustomerID(String ticCustomerID);

	public void setTICFirstName(String firstName);

	public String getTICFirstName();

	public void setTICLastName(String lastName);

	public String getTICLastName();

	public void setTICMobileNumber(String mobileNumber);

	public String getTICMobileNumber();

	public void setTICbirthdate(String dateofBirth);

	public String getTICbirthdate();

	public void setTICGender(String gender);

	public String getTICGender();

	public void setTICEmail(String email);

	public String getTICEmail();

	public void setTICPinNumber(String pinNumber);

	public String getTICPinNumber();

	public Boolean getExistingCustomer();

	public void setExistingCustomer(Boolean existingCustomer);
	
	
	/*changes for Rev 1.0 start*/
	public String getTICCustomerTier();

	public void setTICCustomerTier(String ticCusotmerTier);

	public String getTICCustomerPoints();
	public void setTICCustomerPoints(String ticCustomerPoints);

	/*changes for Rev 1.0  end*/
	
	public String getSbiPointBal();
	public void setSbiPointBal(String sbiPointBal);
	
	
}
