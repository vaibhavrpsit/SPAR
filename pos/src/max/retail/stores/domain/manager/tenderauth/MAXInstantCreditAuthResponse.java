/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

 Copyright (c) 2003 360Commerce, Inc.    All Rights Reserved.
 
 $Log:
  3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
  2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse   
  1    360Commerce 1.0         2/11/2005 12:11:25 PM  Robert Pearse   
 $
 Revision 1.5  2004/09/23 00:30:52  kmcbride
 @scr 7211: Inserting serialVersionUIDs in these Serializable classes

 Revision 1.4  2004/06/03 21:53:06  nrao
 @scr 3916
 Added new error code for Call Error.

 Revision 1.3  2004/02/17 16:18:52  rhafernik
 @scr 0 log4j conversion

 Revision 1.2  2004/02/12 17:14:16  mcs
 Forcing head revision

 Revision 1.1.1.1  2004/02/11 01:04:33  cschellenger
 updating to pvcs 360store-current


 * 
 *    Rev 1.6   Jan 27 2004 10:37:46   nrao
 * Changed the account number to be MOD 10 compliant.
 * 
 *    Rev 1.5   Jan 21 2004 15:59:28   khassen
 * Modified dummyInstantCreditResponse, changed approval status.
 * 
 *    Rev 1.4   Nov 21 2003 11:13:56   nrao
 * Changed account number from 13 to 16 digits. Made changes to copyright and revision number.

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.manager.tenderauth;

import java.io.Serializable;

//----------------------------------------------------------------------------------------
/**
 * This class summarizes all the information and methods required to request
 * enrollment, reference number inquiry, instant credit inquiry and 1 day
 * temporary shopping pass. This is the response from the Authorizer.
 * 
 * @version $Revision: 3$
 * @see InstantCreditRequest
 */
// ----------------------------------------------------------------------------------------
public class MAXInstantCreditAuthResponse extends MAXTenderAuthResponse implements Serializable {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = 1180009447086394250L;

	/**
	 * revision number supplied by SCM
	 */
	public static String revisionNumber = "$Revision: 3$";

	// different possible responses from authorizer
	public final static int ENROLL_BY_PHONE = 1;
	public final static int REFERENCE_NOT_FOUND = 2;
	public final static int INSTANT_CREDIT_PROCESSOR_OFFLINE = 3;
	public final static int TIMEOUT = 4;
	public final static int DECLINED = 5;
	public final static int APPROVED = 6;
	public final static int CALL_CENTER = 7;
	public final static int ERROR_RETRY = 8;
	public final static int OFFLINE = 9;
	public final static int CALL_ERROR = 0;

	protected String approvalCode = null;
	protected String responseDisplay = null;
	protected String accountNumber = null;
	protected String firstName = null;
	protected String lastName = null;
	protected String address = null;
	protected String city = null;
	protected String state = null;
	protected String zip = null;
	protected String extZip = null;
	protected String creditLimit = null;
	protected String shoppingPassExpirationDate = null;
	protected String currentBalance = null;
	protected String openToBuy = null;
	protected String lastPaymentDate = null;
	protected String lastPaymentAmount = null;
	protected String annualPercentageRate = null;
	protected String monthlyPeriodicRate = null;
	protected int approvalStatus;

	/**
	 * A Null Constructor.
	 */
	public MAXInstantCreditAuthResponse() {

	}

	// returns appropriate response verbiage based on approval status
	public String getApprovalStatusString(int status) {
		String result = null;

		switch (status) {
		case DECLINED:
			result = "Cannot Approve";
			break;
		case APPROVED:
			result = "Approved";
			break;
		case CALL_CENTER:
			result = "Call Reference Number";
			break;
		case ENROLL_BY_PHONE:
			result = "Call Error Number XXXXX";
			break;
		case REFERENCE_NOT_FOUND:
			result = "Reference Not Found";
			break;
		case INSTANT_CREDIT_PROCESSOR_OFFLINE:
			result = "Instant Credit Processor Offline";
			break;
		case TIMEOUT:
			result = "Timeout";
			break;
		case ERROR_RETRY:
			result = "Data Error Retry";
			break;
		case OFFLINE:
			result = "System Offline";
			break;
		case CALL_ERROR:
			result = "Call Reference Number";
			break;
		default:
		}
		return result;
	}

	/**
	 * @param approvalCode
	 *            String code when approved
	 */
	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}

	/**
	 * @return String code when approved
	 */
	public String getApprovalCode() {
		return this.approvalCode;
	}

	/**
	 * @param responseDisplay
	 *            String displayed when franking
	 */
	public void setResponseDisplay(String responseDisplay) {
		this.responseDisplay = responseDisplay;
	}

	/**
	 * @return String displayed when franking
	 */
	public String getResponseDisplay() {
		return this.responseDisplay;
	}

	/**
	 * @param accountNumber
	 *            Account Number String
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return Account Number String
	 */
	public String getAccountNumber() {
		return this.accountNumber;
	}

	/**
	 * @param firstName
	 *            First Name String
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return First Name String
	 */
	public String getFirstName() {
		return this.firstName;
	}

	/**
	 * @param lastName
	 *            Last Name String
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return Last Name String
	 */
	public String getLastName() {
		return this.lastName;
	}

	/**
	 * @param address
	 *            String
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return Address String
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * @param city
	 *            String
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return City String
	 */
	public String getCity() {
		return this.city;
	}

	/**
	 * @param state
	 *            String
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return State String
	 */
	public String getState() {
		return this.state;
	}

	/**
	 * @param zip
	 *            String
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}

	/**
	 * @return Zip String
	 */
	public String getZip() {
		return this.zip;
	}

	/**
	 * @param Credit
	 *            Limit String
	 */
	public void setCreditLimit(String creditLimit) {
		this.creditLimit = creditLimit;
	}

	/**
	 * @return Credit Limit String
	 */
	public String getCreditLimit() {
		return this.creditLimit;
	}

	/**
	 * @param Expiration
	 *            Date String
	 */
	public void setShoppingPassExpirationDate(String shoppingPassExpirationDate) {
		this.shoppingPassExpirationDate = shoppingPassExpirationDate;
	}

	/**
	 * @return Expiration Date String
	 */
	public String getShoppingPassExpirationDate() {
		return this.shoppingPassExpirationDate;
	}

	/**
	 * @param Current
	 *            Balance on Card String
	 */
	public void setCurrentBalance(String currentBalance) {
		this.currentBalance = currentBalance;
	}

	/**
	 * @return Current Balance on Card String
	 */
	public String getCurrentBalance() {
		return this.currentBalance;
	}

	/**
	 * @param Open
	 *            to Buy String
	 */
	public void setOpenToBuy(String openToBuy) {
		this.openToBuy = openToBuy;
	}

	/**
	 * @return Open to Buy String
	 */
	public String getOpenToBuy() {
		return this.openToBuy;
	}

	/**
	 * @param Last
	 *            Payment Date String
	 */
	public void setLastPaymentDate(String lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}

	/**
	 * @return Last Payment Date String
	 */
	public String getLastPaymentDate() {
		return this.lastPaymentDate;
	}

	/**
	 * @param Last
	 *            Payment Amount String
	 */
	public void setLastPaymentAmount(String lastPaymentAmount) {
		this.lastPaymentAmount = lastPaymentAmount;
	}

	/**
	 * @return Last Payment Amount String
	 */
	public String getLastPaymentAmount() {
		return this.lastPaymentAmount;
	}

	/**
	 * @param APR
	 *            String
	 */
	public void setAnnualPercentageRate(String annualPercentageRate) {
		this.annualPercentageRate = annualPercentageRate;
	}

	/**
	 * @return APR String
	 */
	public String getAnnualPercentageRate() {
		return this.annualPercentageRate;
	}

	/**
	 * @param Monthly
	 *            Periodic Rate String
	 */
	public void setMonthlyPeriodicRate(String monthlyPeriodicRate) {
		this.monthlyPeriodicRate = monthlyPeriodicRate;
	}

	/**
	 * @return Monthly Periodic Rate String
	 */
	public String getMonthlyPeriodicRate() {
		return this.monthlyPeriodicRate;
	}

	/**
	 * @return Approval Status corresponding to above static fields
	 */
	public int getApprovalStatus() {
		return this.approvalStatus;
	}

	/**
	 * @param approvalStatus
	 *            int corresponding to above static fields
	 */
	public void setApprovalStatus(int approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	// Dummy response used in training mode
	public static MAXInstantCreditAuthResponse createDummyInstantCreditResponse() {
		MAXInstantCreditAuthResponse response = new MAXInstantCreditAuthResponse();

		response.setAccountNumber("6511111135000002");
		response.setApprovalCode("CJA123");
		response.setResponseDisplay("APPROVED");
		response.setFirstName("Customer");
		response.setLastName("Name");
		response.setAddress("Customer Address");
		response.setCity("City");
		response.setState("ST");
		response.setZip("12340");
		response.setCreditLimit("120000");
		response.setShoppingPassExpirationDate("20000601");
		response.setAnnualPercentageRate("0000");
		response.setMonthlyPeriodicRate("000000");
		response.setApprovalStatus(MAXInstantCreditAuthResponse.APPROVED);
		response.setCurrentBalance("1000.00");
		response.setOpenToBuy("500.00");
		response.setLastPaymentDate("20030101");
		response.setLastPaymentAmount("100.00");

		return response;
	}

	/**
	 * @return extension zip code
	 */
	public String getExtZip() {
		return extZip;
	}

	/**
	 * @param string
	 *            Extension Zip Code
	 */
	public void setExtZip(String string) {
		extZip = string;
	}

	// ----------------------------------------------------------------------
	/**
	 * Retrieves the Team Connection revision number.
	 * <p>
	 * 
	 * @return String representation of revision number
	 **/
	// ----------------------------------------------------------------------
	public String getRevisionNumber() {
		return revisionNumber;
	}

	// ----------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * <p>
	 * 
	 * @return String representation of object
	 **/
	// ----------------------------------------------------------------------
	public String toString() {
		return "Class: " + getClass().getName() + " (Revision " + getRevisionNumber() + ") @" + hashCode();
	}
}
