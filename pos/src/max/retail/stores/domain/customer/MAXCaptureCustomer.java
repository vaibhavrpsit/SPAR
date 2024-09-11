/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.1     Nov 09, 2016		Ashish Yadav    	Changes for Home Delivery Send FES
 * Rev 1.0  08 Nov, 2016              Nadia              MAX-StoreCredi_Return requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.customer;

import java.util.Vector;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;

/**
 * @author khassen
 *
 *         This is the CaptureCustomer class to be used for maintaining captured
 *         customer information throughout the course of a transaction.
 */

// <!-- MAX Rev 1.0 Change : Start -->
public class MAXCaptureCustomer extends MAXCustomer implements MAXCaptureCustomerIfc {
	// <!-- MAX Rev 1.0 Change : end -->
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -2042169845130404255L;

	protected static final int NUM_ADDRESS_LINES = 2;

	protected String idType = "";

	// Additional transaction-related information.
	// Used for database updates.
	protected String transactionID = "";
	protected String storeID = "";
	protected String wsID = "";
	protected EYSDate businessDay = null;
	protected LocalizedCodeIfc personalIDType = DomainGateway.getFactory().getLocalizedCode();

	public MAXCaptureCustomer() {
		super();
	}

	protected AddressIfc getAddressObject() {
		Vector addressList = super.getAddresses();
		if (addressList == null) {
			return null;
		}
		if (addressList.size() < 1) {
			return null;
		}
		return (AddressIfc) addressList.elementAt(0);
	}

	protected PhoneIfc getPhoneObject() {
		Vector phones = super.getPhones();
		if (phones == null) {
			return null;
		}
		if (phones.size() < 1) {
			return null;
		}
		return (PhoneIfc) phones.elementAt(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#getFirstName()
	 */
	public String getFirstName() {
		return super.getFirstName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setFirstName(java.
	 * lang.String)
	 */
	public void setFirstName(String firstName) {
		super.setFirstName(firstName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#getLastName()
	 */
	public String getLastName() {
		return super.getLastName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setLastName(java.
	 * lang.String)
	 */
	public void setLastName(String lastName) {
		super.setLastName(lastName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#getAddressLine(
	 * int)
	 */
	public String getAddressLine(int i) {
		AddressIfc address = getAddressObject();

		if (address == null) {
			return "";
		}
		Vector lines = address.getLines();
		if ((lines == null) || (lines.size() < (i + 1))) {
			return "";
		}
		return (String) lines.elementAt(i);
	}

	/**
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#setAddressLine(int,
	 *      java.lang.String)
	 * @deprecated in release 12.1 use setAddressLine
	 **/
	public void addAddressLine(String line) {
		AddressIfc address = getAddressObject();

		if (address == null) {
			address = DomainGateway.getFactory().getAddressInstance();
			addAddress(address);
		}
		address.addAddressLine(line);
	}

	/**
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#setAddressLine(int,
	 *      java.lang.String)
	 **/
	public void setAddressLine(int lineNumber, String line) {
		AddressIfc address = getAddressObject();

		if (address == null) {
			address = DomainGateway.getFactory().getAddressInstance();
			addAddress(address);
		}
		Vector lines = address.getLines();
		// Just in case someone adds lines "out of order"
		while (lines.size() < lineNumber) {
			lines.addElement("");
		}
		lines.setElementAt(line, lineNumber - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#getCity()
	 */
	public String getCity() {
		AddressIfc address = getAddressObject();
		if (address == null) {
			return "";
		}
		return address.getCity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setCity(java.lang.
	 * String)
	 */
	public void setCity(String city) {
		AddressIfc address = getAddressObject();
		if (address == null) {
			address = DomainGateway.getFactory().getAddressInstance();
			addAddress(address);
		}
		address.setCity(city);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#getCountry()
	 */
	public String getCountry() {
		AddressIfc address = getAddressObject();
		if (address == null) {
			return "";
		}
		return address.getCountry();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setCountry(java.
	 * lang.String)
	 */
	public void setCountry(String country) {
		AddressIfc address = getAddressObject();
		if (address == null) {
			address = DomainGateway.getFactory().getAddressInstance();
			addAddress(address);
		}
		address.setCountry(country);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#getState()
	 */
	public String getState() {
		AddressIfc address = getAddressObject();
		if (address == null) {
			return "";
		}
		return address.getState();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setState(java.lang
	 * .String)
	 */
	public void setState(String state) {
		AddressIfc address = getAddressObject();
		if (address == null) {
			address = DomainGateway.getFactory().getAddressInstance();
			addAddress(address);
		}
		address.setState(state);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#getPostalCode()
	 */
	public String getPostalCode() {
		AddressIfc address = getAddressObject();
		if (address == null) {
			return "";
		}
		return address.getPostalCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setPostalCode(java
	 * .lang.String)
	 */
	public void setPostalCode(String postalCode) {
		AddressIfc address = getAddressObject();
		if (address == null) {
			address = DomainGateway.getFactory().getAddressInstance();
			addAddress(address);
		}
		address.setPostalCode(postalCode);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#getPostalCodeExt()
	 */
	public String getPostalCodeExt() {
		AddressIfc address = getAddressObject();
		if (address == null) {
			return "";
		}
		return address.getPostalCodeExtension();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setPostalCodeExt(
	 * java.lang.String)
	 */
	public void setPostalCodeExt(String postalCodeExt) {
		AddressIfc address = getAddressObject();
		if (address == null) {
			address = DomainGateway.getFactory().getAddressInstance();
			addAddress(address);
		}
		address.setPostalCodeExtension(postalCodeExt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#getAreaCode()
	 */
	/*public String getAreaCode() {
		PhoneIfc phone = getPhoneObject();
		CustomerIfc cust = getcustomer
		if (phone == null) {
			return "";
		}
		return phone.getAreaCode();
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setAreaCode(java.
	 * lang.String)
	 */
	/*public void setAreaCode(String areaCode) {
		PhoneIfc phone = getPhoneObject();
		if (phone == null) {
			phone = DomainGateway.getFactory().getPhoneInstance();
			addPhone(phone);
		}
		((MAXCaptureCustomer) phone).setAreaCode(areaCode);
	}*/

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#getPhoneNumber()
	 */
	public String getPhoneNumber() {
		PhoneIfc phone = getPhoneObject();
		if (phone == null) {
			return "";
		}
		return phone.getPhoneNumber();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setPhoneNumber(
	 * java.lang.String)
	 */
	public void setPhoneNumber(String phoneNumber) {
		PhoneIfc phone = getPhoneObject();
		if (phone == null) {
			phone = DomainGateway.getFactory().getPhoneInstance();
			addPhone(phone);
		}
		phone.setPhoneNumber(phoneNumber);
	}

	public void setPhoneType(int type) {
		PhoneIfc phone = getPhoneObject();
		if (phone == null) {
			phone = DomainGateway.getFactory().getPhoneInstance();
			addPhone(phone);
		}
		phone.setPhoneType(type);
	}

	public int getPhoneType() {
		PhoneIfc phone = getPhoneObject();
		if (phone == null) {
			return PhoneConstantsIfc.PHONE_TYPE_UNSPECIFIED;
		}
		return phone.getPhoneType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.extendyourstore.domain.customer.CaptureCustomerIfc#getIDType()
	 */
	public String getIDType() {
		return idType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.extendyourstore.domain.customer.CaptureCustomerIfc#setIDType(java.
	 * lang.String)
	 */
	public void setIDType(String idType) {
		this.idType = idType;
	}

	/**
	 * @return Returns the businessDay.
	 */
	public EYSDate getBusinessDay() {
		return businessDay;
	}

	/**
	 * @param businessDay
	 *            The businessDay to set.
	 */
	public void setBusinessDay(EYSDate businessDay) {
		this.businessDay = businessDay;
	}

	/**
	 * @return Returns the store ID.
	 */
	public String getStoreID() {
		return storeID;
	}

	/**
	 * @param storeID
	 *            The store ID to set.
	 */
	public void setStoreID(String storeID) {
		this.storeID = storeID;
	}

	/**
	 * @return Returns the workstation ID.
	 */
	public String getWsID() {
		return wsID;
	}

	/**
	 * @param wsID
	 *            The workstation ID to set.
	 */
	public void setWsID(String wsID) {
		this.wsID = wsID;
	}

	public String getTransactionID() {
		return transactionID;
	}

	/**
	 * @param transactionID
	 *            The transactionID to set.
	 */
	public void setTransactionID(String transactionID) {
		this.transactionID = transactionID;
	}

	public Object clone() {
		MAXCaptureCustomer c = new MAXCaptureCustomer();

		setCloneAttributes(c);

		return (Object) c;
	}

	protected void setCloneAttributes(MAXCaptureCustomer c) {
		super.setCloneAttributes(c);

		c.setFirstName(getFirstName());
		c.setLastName(getLastName());

		AddressIfc address = getAddressObject();
		if (address != null) {
			Vector lines = address.getLines();
			if (lines != null) {
				for (int i = 0; i < lines.size(); i++) {
					int lineNumber = i + 1;
					c.setAddressLine(lineNumber, getAddressLine(i));
				}
			}
		}

		c.setCity(getCity());
		c.setCountry(getCountry());
		c.setState(getState());
		c.setPostalCode(getPostalCode());
		c.setPostalCodeExt(getPostalCodeExt());
		// Changes start for rev 1.1 (Send : Commenting below line as AreaCode() is not present in base 14)
		//c.setAreaCode(getAreaCode());
		// Changes end for rev 1.1 (Send)
		c.setPhoneNumber(getPhoneNumber());
		c.setIDType(getIDType());
		c.setStoreID(getStoreID());
		c.setWsID(getWsID());
		c.setBusinessDay(getBusinessDay());
		c.setTransactionID(getTransactionID());
		if (this.personalIDType != null) {
		      c.setPersonalIDType((LocalizedCodeIfc)getPersonalIDType().clone());
		    }
	}

	@Override
	public LocalizedCodeIfc getPersonalIDType() {
		// TODO Auto-generated method stub
		return this.personalIDType;
	}

	@Override
	public void setPersonalIDType(LocalizedCodeIfc customerIDType) {
		// TODO Auto-generated method stub
		this.personalIDType = customerIDType;
	}

}
