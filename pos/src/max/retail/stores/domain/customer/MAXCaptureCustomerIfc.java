/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  08 Nov, 2016              Nadia              MAX-StoreCredi_Return requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.customer;

import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.utility.EYSDate;

/**
 * @author khassen
 *
 *         This is the interface to the CaptureCustomer class. It is used for
 *         managing a Capture Customer object throughout the course of a
 *         transaction.
 */
// <!-- MAX Rev 1.0 Change : Start -->
public interface MAXCaptureCustomerIfc extends MAXCustomerIfc, CaptureCustomerIfc {
	// <!-- MAX Rev 1.0 Change : end -->
	public String getFirstName();

	public void setFirstName(String first);

	public String getLastName();

	public void setLastName(String last);

	public String getAddressLine(int i);

	/** @deprecated in release 12.1 use setAddressLine **/
	public void addAddressLine(String line);

	public void setAddressLine(int index, String line);

	public String getCity();

	public void setCity(String city);

	public String getCountry();

	public void setCountry(String country);

	public String getState();

	public void setState(String state);

	public String getPostalCode();

	public void setPostalCode(String postalCode);

	public String getPostalCodeExt();

	public void setPostalCodeExt(String postalCodeExt);

	/*public String getAreaCode();

	public void setAreaCode(String areaCode);*/

	public String getPhoneNumber();

	public void setPhoneNumber(String phoneNumber);

	public int getPhoneType();

	public void setPhoneType(int type);

	public String getIDType();

	public void setIDType(String idType);

	public String getStoreID();

	public void setStoreID(String id);

	public String getWsID();

	public void setWsID(String id);

	public EYSDate getBusinessDay();

	public void setBusinessDay(EYSDate date);

	public String getTransactionID();

	public void setTransactionID(String id);

	// public Object clone();
	// protected void setCloneAttributes(CaptureCustomer c);
}
