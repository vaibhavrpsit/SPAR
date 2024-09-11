/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *
 *	Rev 1.1     Aug 08, 2017		Hitesh Dua			updated shipping Address is not printing on receipt  
 *	
 *	Rev 1.2    Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
 *   Rev 1.3      Aug 25, 2021		Atul Shukla        	EWallet FES Implementation
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.customer;

// java imports
// java imports
// java imports
import java.math.BigDecimal;
import java.util.ArrayList;

import oracle.retail.stores.domain.customer.Customer;
import oracle.retail.stores.domain.utility.AddressBookEntryIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EmailAddress;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.foundation.utility.Util;

public class MAXCustomer extends Customer implements MAXCustomerIfc {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -5492762869155265837L;

	/**
	 * revision number supplied by Team Connection.
	 **/
	public static String revisionNumber = "$Revision: 4$";

	/*
	 * // MAX Rev 1.0 Change : Start protected String customerType = null;
	 * 
	 * public String getCustomerType() { return customerType; } public void
	 * setCustomerType(String value) { customerType = value; } // MAX Rev 1.0
	 * Change : end
	 */
	// ----------------------------------------------------------------------
	/**
	 * Balance Point
	 */
	// ----------------------------------------------------------------------
	protected BigDecimal balancePoint = null;

	// ----------------------------------------------------------------------
	/**
	 * Nalance Point Last Updation Date
	 */
	// ----------------------------------------------------------------------
	protected EYSDate balancePointLastUpdationDate = null;

	// ----------------------------------------------------------------------
	/**
	 * Customer Type
	 */
	// ----------------------------------------------------------------------
	protected String customerType = MAXCustomerConstantsIfc.LOCAL;
	// ----------------------------------------------------------------------
	/**
	 * Customer Tier
	 */
	// ----------------------------------------------------------------------
	protected String customerTier = null;

	protected boolean tagCustomer = false;
	// ----------------------------------------------------------------------
	/**
	 * Points Expiring Next Month
	 */
	// ----------------------------------------------------------------------
	protected BigDecimal pointsExpiringNextMonth = null;

	/* Rev 1.2 Changes starts */
	/**
	 * Variable to check whether Customer has both loyalty and local customer
	 * information or not
	 */
	protected boolean bothLocalAndLoyaltyCustomerAttached = false;

	/**
	 * Variable to hold loyalty Card Number of Customer. This will be same as
	 * customer Id if only one customer of loyalty type is attached to
	 * transaction. If two customers are attached to a transaction and one is
	 * ORPOS Customer and Other is Loyalty this field will hold the customer Id
	 * of loyalty customer
	 */

	protected String loyaltyCardNumber = null;

	// Changes starts for Rev 1.0 (Ashish :Loyalty OTP)
	protected int retrytimeout;
	protected String origCustomerID = null;
	protected int timeoutvalue = 1;
	protected boolean Otpvalidation = false;
	protected String messageid = "";
	private int otp;
	protected EYSDate lastVisit = null;

	protected String lastVisit3months = null;
	protected String lastVisit12months = null;
	private ArrayList custoffers = new ArrayList();
	protected boolean CapillaryCustomerSuccessResponse = false;
	// Changes ends for Rev 1.0 (Ashish :Loyalty OTP)

	/**
	 * Variable to hold First name of loyalty Customer. This will be same as
	 * customer name if only one customer of loyalty type is attached to
	 * transaction. If two customers are attached to a transaction and one is
	 * ORPOS Customer and Other is Loyalty this field will hold the customer
	 * name of loyalty customer
	 */
	protected String loyaltyCustomerFirstName = null;

	/* Variable to hold one phone number of loyalty Customer */
	protected Phone loyaltyCustomerPhone = null;

	/* Variable to hold one phone number of loyalty Customer */
	protected EmailAddress loyaltyCustomerEMail = null;

	/* Rev 1.2 Changes ends */

	// / Changes for Rev 1.2 : Starts

	protected MAXCustomerIfc ticcustomer;

	protected boolean ticCustomerVisibleFlag = false;

	protected String houseAccountNumber = "";
	// / Changes for Rev 1.2 : ENDs

	protected int mobikwikTimeoutValue = 1;
	private int mobikwikTotp;
	protected String mobikwikCustomerPhone;
	// Changes ends for
	// Changes starts for Rev 1.3
	protected String eWalletTraceId = null;
	private boolean isLMREWalletCustomerFlag;
	
	private String sbiPoints=null;

	// Changes End for Rev 1.3
	public MAXCustomer() { // Begin Customer()
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
	protected void setCloneAttributes(MAXCustomer newClass) { // begin
																// setCloneAttributes()
		super.setCloneAttributes(newClass);
		newClass.setBalancePoint(balancePoint);
		newClass.setBalancePointLastUpdationDate(balancePointLastUpdationDate);
		newClass.setCustomerType(customerType);
		newClass.setCustomerTier(customerTier);
		newClass.setPointsExpiringNextMonth(pointsExpiringNextMonth);
		// for the tic customer addition start
		newClass.setMAXTICCustomer(ticcustomer);
		newClass.setTicCustomerVisibleFlag(ticCustomerVisibleFlag);
		// for the tic customer addition END
		newClass.setLoyaltyCardNumber(loyaltyCardNumber);
		// Changes starts for Rev 1.0 (Ashish :Loyalty OTP)
		newClass.setLoyaltyTimeout(timeoutvalue);
		newClass.setLoyaltyRetryTimeout(retrytimeout);
		newClass.setLoyaltyotp(otp);
		// newClass.setCustomerID(origCustomerID);
		newClass.setMessageId(messageid);
		newClass.setLoyaltyRetryTimeout(retrytimeout);
		newClass.setLoyaltyCustomerFirstName(loyaltyCustomerFirstName);
		newClass.setLoyaltyCustomerPhone(loyaltyCustomerPhone);
		newClass.setLoyaltyCustomerEMail(loyaltyCustomerEMail);
		// Changes ends for Rev 1.0 (Ashish :Loyalty OTP)
		newClass.setMobikwikTotp(mobikwikTotp);
		newClass.setMobikwikTimeout(mobikwikTimeoutValue);
		newClass.setMobikwikCustomerPhoneNumber(mobikwikCustomerPhone);
		newClass.setLastVisit(lastVisit);
		newClass.setLastVisit3months(lastVisit3months);
		newClass.setLastVisit12months(lastVisit12months);
		newClass.setCustoffers(custoffers);
		newClass.setCapillaryCustomerSuccessResponse(CapillaryCustomerSuccessResponse);
	} // end setCloneAttributes()

	// ---------------------------------------------------------------------
	/**
	 * Determine if two MAXCustomer objects are identical.
	 * <P>
	 * 
	 * @param obj
	 *            object to compare with
	 * @return boolean true if the objects are identical
	 **/
	// ---------------------------------------------------------------------
	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof MAXCustomer) { // begin compare MAXcustomer
											// objects
											// downcast the input object
			MAXCustomer cust = (MAXCustomer) obj;

			if (super.equals(cust)) {
				if (balancePoint == cust.getBalancePoint()
						&& Util.isObjectEqual(balancePointLastUpdationDate,
								cust.getBalancePointLastUpdationDate())
						&& Util.isObjectEqual(customerType,
								cust.getCustomerType())
						&& Util.isObjectEqual(customerTier,
								cust.getCustomerTier())
						&& pointsExpiringNextMonth == cust
								.getPointsExpiringNextMonth()) {
					isEqual = true;
				} else {
					isEqual = false;
				}
			} else {
				isEqual = false;
			}
		}

		return isEqual;

	}

	// ---------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String toString() { // Begin toString()
								// result string
		StringBuilder strResult = Util.classToStringHeader("MAXCustomer",
				getRevisionNumber(), hashCode());
		strResult.append("BalancePoint:                       [")
				.append(balancePoint).append("]\n");
		strResult
				.append("BalancePointLastUpdationDate:                       "
						+ "[").append(balancePointLastUpdationDate)
				.append("]\n");
		strResult.append("CustomerType:                         [")
				.append(customerType).append("]\n");
		strResult.append("CustomerTier:                         [")
				.append(customerTier).append("]\n");
		strResult.append("PointsExpiringNextMonth:                         [")
				.append(pointsExpiringNextMonth).append("]\n");
		strResult.append(super.toString());
		// pass back result
		return (strResult.toString());
	} // End toString()

	// ----------------------------------------------------------------------
	/**
	 * Returns balancePoint.
	 * 
	 * @return balancePoint BigDecimal
	 */
	// ----------------------------------------------------------------------
	public BigDecimal getBalancePoint() {
		return balancePoint;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets balancePoint.
	 * 
	 * @param balancePoint
	 *            int
	 */
	// ----------------------------------------------------------------------
	public void setBalancePoint(BigDecimal balancePoint) {
		this.balancePoint = balancePoint;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns balancePointLastUpdationDate.
	 * 
	 * @return balancePointLastUpdationDate EYSDate
	 */
	// ----------------------------------------------------------------------
	public EYSDate getBalancePointLastUpdationDate() {
		return balancePointLastUpdationDate;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets balancePointLastUpdationDate.
	 * 
	 * @param balancePointLastUpdationDate
	 *            EYSDate
	 */
	// ----------------------------------------------------------------------
	public void setBalancePointLastUpdationDate(
			EYSDate balancePointLastUpdationDate) {
		this.balancePointLastUpdationDate = balancePointLastUpdationDate;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns customerType.
	 * 
	 * @return customerType String
	 */
	// ----------------------------------------------------------------------
	public String getCustomerType() {
		return customerType;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets customerType.
	 * 
	 * @param customerType
	 *            String
	 */
	// ----------------------------------------------------------------------
	public void setCustomerType(String customerType) {
		this.customerType = customerType;
		if (this.customerType.equalsIgnoreCase(MAXCustomerConstantsIfc.CRM))
			this.loyaltyCardNumber = this.customerID;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns value.
	 * 
	 * @return value boolean
	 */
	// ----------------------------------------------------------------------
	public boolean isCustomerTag() {
		return tagCustomer;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets value.
	 * 
	 * @param value
	 *            boolean
	 */
	// ----------------------------------------------------------------------
	public void setCustomerTag(boolean value) {
		this.tagCustomer = value;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns customerTier.
	 * 
	 * @return customerTier String
	 */
	// ----------------------------------------------------------------------
	public String getCustomerTier() {
		return customerTier;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets customerTier.
	 * 
	 * @param customerTier
	 *            String
	 */
	// ----------------------------------------------------------------------
	public void setCustomerTier(String customerTier) {
		this.customerTier = customerTier;
	}

	// ----------------------------------------------------------------------
	/**
	 * Returns pointsExpiringNextMonth.
	 * 
	 * @return pointsExpiringNextMonth BigDecimal
	 */
	// ----------------------------------------------------------------------
	public BigDecimal getPointsExpiringNextMonth() {
		return pointsExpiringNextMonth;
	}

	// ----------------------------------------------------------------------
	/**
	 * Sets pointsExpiringNextMonth.
	 * 
	 * @param pointsExpiringNextMonth
	 *            int
	 */
	// ----------------------------------------------------------------------
	public void setPointsExpiringNextMonth(BigDecimal pointsExpiringNextMonth) {
		this.pointsExpiringNextMonth = pointsExpiringNextMonth;
	}

	/* Rev 1.2 Changes starts */
	/**
	 * @return the loyaltyCardNumber
	 */
	public String getLoyaltyCardNumber() {
		return loyaltyCardNumber;
	}

	/**
	 * @param loyaltyCardNumber
	 *            the loyaltyCardNumber to set
	 */
	public void setLoyaltyCardNumber(String loyaltyCardNumber) {
		this.loyaltyCardNumber = loyaltyCardNumber;
	}

	/**
	 * @return the loyaltyCustomerFirstName
	 */
	public String getLoyaltyCustomerFirstName() {
		return loyaltyCustomerFirstName;
	}

	/**
	 * @param loyaltyCustomerFirstName
	 *            the loyaltyCustomerFirstName to set
	 */
	public void setLoyaltyCustomerFirstName(String loyaltyCustomerFirstName) {
		this.loyaltyCustomerFirstName = loyaltyCustomerFirstName;
	}

	/**
	 * @return the bothLocalAndLoyaltyCustomerAttached
	 */
	public boolean isBothLocalAndLoyaltyCustomerAttached() {
		return bothLocalAndLoyaltyCustomerAttached;
	}

	/**
	 * @param bothLocalAndLoyaltyCustomerAttached
	 *            the bothLocalAndLoyaltyCustomerAttached to set
	 */
	public void setBothLocalAndLoyaltyCustomerAttached(
			boolean bothLocalAndLoyaltyCustomerAttached) {
		this.bothLocalAndLoyaltyCustomerAttached = bothLocalAndLoyaltyCustomerAttached;
	}

	/**
	 * @return
	 * @return the loyaltyCustomerPhone
	 */
	public Phone getLoyaltyCustomerPhone() {
		return loyaltyCustomerPhone;
	}

	/**
	 * @param loyaltyCustomerPhone
	 *            the loyaltyCustomerPhone to set
	 */
	public void setLoyaltyCustomerPhone(Phone loyaltyCustomerPhone) {
		this.loyaltyCustomerPhone = loyaltyCustomerPhone;
	}

	/**
	 * @return the loyaltyCustomerEMail
	 */
	public EmailAddress getLoyaltyCustomerEMail() {
		return loyaltyCustomerEMail;
	}

	/**
	 * @param loyaltyCustomerEMail
	 *            the loyaltyCustomerEMail to set
	 */
	public void setLoyaltyCustomerEMail(EmailAddress loyaltyCustomerEMail) {
		this.loyaltyCustomerEMail = loyaltyCustomerEMail;
	}

	public MAXCustomerIfc getTiccustomer() {
		return ticcustomer;
	}

	public void setTiccustomer(MAXCustomerIfc ticcustomer) {
		this.ticcustomer = ticcustomer;
	}

	public boolean isTicCustomerVisibleFlag() {
		return ticCustomerVisibleFlag;
	}

	public void setTicCustomerVisibleFlag(boolean ticCustomerVisibleFlag) {
		this.ticCustomerVisibleFlag = ticCustomerVisibleFlag;
	}

	// / for the tic customer addition start

	public void setMAXTICCustomer(MAXCustomerIfc ticcustomer) {
		// TODO Auto-generated method stub
		this.ticcustomer = ticcustomer;
	}

	public MAXCustomerIfc getMAXTICCustomer() {
		// TODO Auto-generated method stub
		return ticcustomer;
	}

	public void setHouseAccountNumber(String value) {
		houseAccountNumber = value;
	}

	// / for the tic customer addition End

	// Changes starts for Rev 1.1 (Ahsish : Loyalty OTP)
	public void setLoyaltyotp(int otpcode) {
		otp = otpcode;

	}

	public int getLoyaltyotp() {

		return otp;
	}

	public void setLoyaltyTimeout(int timeout) {
		timeoutvalue = timeout;

	}

	public int getLoyaltyTimeout() {

		return timeoutvalue;
	}

	public void setOtpValidation(boolean value) {
		Otpvalidation = value;

	}

	public boolean IsOtpValidation() {

		return Otpvalidation;
	}

	public void setLoyaltyRetryTimeout(int retrytime) {
		retrytimeout = retrytime;

	}

	public int getLoyaltyRetryTimeout() {

		return retrytimeout;
	}

	public void setMessageId(String msgid) {
		messageid = msgid;

	}

	public String getMessageId() {

		return messageid;
	}

	// Changes starts for Rev 1.1 (Ahsish : Loyalty OTP)
	// changes for rev 1.1 start
	public AddressIfc getAddressByType(int value) {
		AddressIfc address = null;
		for (AddressBookEntryIfc a : this.addressBookEntries) {
			if (a.getAddressType() == value) {
				address = a.getAddress();
				if (!("null".equalsIgnoreCase(a.getAddress().getLine1()))
						&& !("".equalsIgnoreCase(a.getAddress().getCity())))
					break;
			}

		}

		return address;
	}

	// changes for rev 1.1 end

	// changes for rev 1.2 Start
	@Override
	public void setMobikwikTotp(int mobikwikTotp1) {
		this.mobikwikTotp = mobikwikTotp1;
	}

	@Override
	public int getMobikwikTotp() {
		// TODO Auto-generated method stub
		return mobikwikTotp;
	}

	@Override
	public int getMobikwikTimeout() {
		// TODO Auto-generated method stub
		return mobikwikTimeoutValue;
	}

	@Override
	public void setMobikwikTimeout(int mobikwikTimeoutValue1) {
		this.mobikwikTimeoutValue = mobikwikTimeoutValue1;

	}

	// changes for rev 1.2 End

	@Override
	public void setMobikwikCustomerPhoneNumber(String mobikwikCustomerPhone) {
		this.mobikwikCustomerPhone = mobikwikCustomerPhone;

	}

	@Override
	public String getMobikwikCustomerPhoneNumber() {
		// TODO Auto-generated method stub
		return mobikwikCustomerPhone;
	}

	@Override
	public void setLastVisit(EYSDate lastVisit) {
		this.lastVisit = lastVisit;

	}

	@Override
	public EYSDate getLastVisit() {

		return lastVisit;
	}

	@Override
	public String getLastVisit3months() {
		return lastVisit3months;
	}

	@Override
	public void setLastVisit3months(String lastVisit3months) {
		this.lastVisit3months = lastVisit3months;
	}

	@Override
	public String getLastVisit12months() {
		return lastVisit12months;
	}

	@Override
	public void setLastVisit12months(String lastVisit12months) {
		this.lastVisit12months = lastVisit12months;
	}

	@Override
	public void setCustoffers(ArrayList custoffers) {
		this.custoffers = custoffers;
	}

	@Override
	public ArrayList getCustoffers() {
		return custoffers;
	}

	@Override
	public boolean isCapillaryCustomerSuccessResponse() {
		return CapillaryCustomerSuccessResponse;
	}

	@Override
	public void setCapillaryCustomerSuccessResponse(
			boolean CapillaryCustomerSuccessResponse) {
		this.CapillaryCustomerSuccessResponse = CapillaryCustomerSuccessResponse;
	}

	@Override
	public String getOrigCustomerID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrigCustomerID(String origCustomerID) {
		// TODO Auto-generated method stub

	}

	// Changes starts for Rev 1.3
	@Override
	public String getLMREWalletTraceId() {
		// TODO Auto-generated method stub
		return eWalletTraceId;
	}

	@Override
	public void setLMREWalletTraceId(String traceId) {
		// TODO Auto-generated method stub
		this.eWalletTraceId = traceId;

	}

	public boolean isLMREWalletCustomerFlag() {
		return isLMREWalletCustomerFlag;
	}

	public void setLMREWalletCustomerFlag(boolean isLMREWalletCustomerFlag) {
		this.isLMREWalletCustomerFlag = isLMREWalletCustomerFlag;
	}
	// Changes End for Rev 1.3
	public String getSbiPointBal() {
		return sbiPoints;
	}
	public void setSbiPointBal(String sbiPointBal) {
		sbiPoints=sbiPointBal;
	}

	public char[] getPointBal() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
