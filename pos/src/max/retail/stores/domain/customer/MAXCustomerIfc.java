/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	*  Rev 1.1     Oct  10, 2016		Atul Shukla		    Mobikwik FES
 *	Rev 1.0     Jan 06, 2016		Ashish Yadav		Online Points Redemption FES
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.customer;

// java imports
import java.math.BigDecimal;
import java.util.ArrayList;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EmailAddress;
import oracle.retail.stores.domain.utility.Phone;

//------------------------------------------------------------------------------
/**
 * Interface for Customer class.
 * <P>
 * 
 * @see com.extendyourstore.domain.customer.Customer
 * @see com.extendyourstore.domain.utility.EYSDomainIfc
 * @see com.extendyourstore.domain.utility.AbstractRoutableIfc
 * @version $Revision: 4$
 **/
// ------------------------------------------------------------------------------
public interface MAXCustomerIfc extends CustomerIfc {

	/**
	 * revision number supplied by Team Connection
	 **/
	public static String revisionNumber = "$Revision: 4$";

	/*
	 * // MAX Rev 1.0 Change : Start public String getCustomerType(); public
	 * void setCustomerType(String customerType); // MAX Rev 1.0 Change : End
	 */

	/**
	 * Returns balancePoint.
	 * 
	 * @return balancePoint BigDecimal
	 */
	// ----------------------------------------------------------------------
	public BigDecimal getBalancePoint();

	// ----------------------------------------------------------------------
	/**
	 * Sets balancePoint.
	 * 
	 * @param balancePoint
	 *            int
	 */
	// ----------------------------------------------------------------------
	public void setBalancePoint(BigDecimal balancePoint);

	// ----------------------------------------------------------------------
	/**
	 * Returns balancePointLastUpdationDate.
	 * 
	 * @return balancePointLastUpdationDate EYSDate
	 */
	// ----------------------------------------------------------------------
	public EYSDate getBalancePointLastUpdationDate();

	// ----------------------------------------------------------------------
	/**
	 * Sets balancePointLastUpdationDate.
	 * 
	 * @param balancePointLastUpdationDate
	 *            EYSDate
	 */
	// ----------------------------------------------------------------------
	public void setBalancePointLastUpdationDate(EYSDate balancePointLastUpdationDate);

	// ----------------------------------------------------------------------
	/**
	 * Returns customerType.
	 * 
	 * @return customerType String
	 */
	// ----------------------------------------------------------------------
	public String getCustomerType();

	// ----------------------------------------------------------------------
	/**
	 * Sets customerType.
	 * 
	 * @param customerType
	 *            String
	 */
	// ----------------------------------------------------------------------
	public void setCustomerType(String customerType);

	// ----------------------------------------------------------------------
	/**
	 * Returns value.
	 * 
	 * @return customerType boolean
	 */
	// ----------------------------------------------------------------------
	public boolean isCustomerTag();

	// ----------------------------------------------------------------------
	/**
	 * Sets value.
	 * 
	 * @param customerType
	 *            boolean
	 */
	// ----------------------------------------------------------------------
	public void setCustomerTag(boolean value);

	// ----------------------------------------------------------------------
	/**
	 * Returns customerTier.
	 * 
	 * @return customerTier String
	 */
	// ----------------------------------------------------------------------
	public String getCustomerTier();

	// ----------------------------------------------------------------------
	/**
	 * Sets customerTier
	 * 
	 * @param customerTier
	 *            String
	 */
	// ----------------------------------------------------------------------
	public void setCustomerTier(String customerTier);

	// ----------------------------------------------------------------------
	/**
	 * Returns pointsExpiringNextMonth.
	 * 
	 * @return pointsExpiringNextMonth BigDecimal
	 */
	// ----------------------------------------------------------------------
	public BigDecimal getPointsExpiringNextMonth();

	// ----------------------------------------------------------------------
	/**
	 * Sets pointsExpiringNextMonth
	 * 
	 * @param pointsExpiringNextMonth
	 *            BigDecimal
	 */
	// ----------------------------------------------------------------------
	public void setPointsExpiringNextMonth(BigDecimal pointsExpiringNextMonth);

	/**
	 * @return the loyaltyCardNumber
	 */
	public String getLoyaltyCardNumber();

	/**
	 * @param loyaltyCardNumber
	 *            the loyaltyCardNumber to set
	 */
	public void setLoyaltyCardNumber(String loyaltyCardNumber);

	/**
	 * @return the loyaltyCustomerFirstName
	 */
	public String getLoyaltyCustomerFirstName();

	/**
	 * @param loyaltyCustomerFirstName
	 *            the loyaltyCustomerFirstName to set
	 */
	public void setLoyaltyCustomerFirstName(String loyaltyCustomerFirstName);

	/**
	 * @return the bothLocalAndLoyaltyCustomerAttached
	 */
	public boolean isBothLocalAndLoyaltyCustomerAttached();

	/**
	 * @param bothLocalAndLoyaltyCustomerAttached
	 *            the bothLocalAndLoyaltyCustomerAttached to set
	 */
	public void setBothLocalAndLoyaltyCustomerAttached(boolean bothLocalAndLoyaltyCustomerAttached);

	/**
	 * @return the loyaltyCustomerPhone
	 */
	public Phone getLoyaltyCustomerPhone();

	/**
	 * @param loyaltyCustomerPhone
	 *            the loyaltyCustomerPhone to set
	 */
	public void setLoyaltyCustomerPhone(Phone loyaltyCustomerPhone);

	/**
	 * @return the loyaltyCustomerEMail
	 */
	public EmailAddress getLoyaltyCustomerEMail();

	/**
	 * @param loyaltyCustomerEMail
	 *            the loyaltyCustomerEMail to set
	 */
	public void setLoyaltyCustomerEMail(EmailAddress loyaltyCustomerEMail);

	/// for the tic customer addition Akhilesh

	public void setMAXTICCustomer(MAXCustomerIfc ticcustomer);

	public MAXCustomerIfc getMAXTICCustomer();

	public boolean isTicCustomerVisibleFlag();

	public void setTicCustomerVisibleFlag(boolean ticCustomerVisibleFlag);

	public void setHouseAccountNumber(String value);
	/// for the tic customer addition end
	
	// Changes starts for Rev 1.0 (Ashish :Loyalty OTP)
	public boolean IsOtpValidation();
	public void  setLoyaltyotp(int otp);
	public int getLoyaltyotp();
	public void  setOtpValidation(boolean value);
	public int getLoyaltyTimeout();
	public String getMessageId();
	public void  setMessageId(String string) ;
	public void setLoyaltyTimeout(int i);
	public void setLoyaltyRetryTimeout(int i);
	public int getLoyaltyRetryTimeout();
	// Changes ends for Rev 1.0 (Ashish : Loyalty OTP)
	// Changes starts for Rev 1.1 (Atul :Mobikwik OTP)
	//public boolean IsOtpValidation();
	public void  setMobikwikTotp(int totp);
	public int getMobikwikTotp();
	public void  setMobikwikCustomerPhoneNumber(String mobikwikCustomerPhone);
	public String getMobikwikCustomerPhoneNumber();
	public int getMobikwikTimeout();
	//public String getMessageId();
	//public void  setMessageId(String string) ;
	public void setMobikwikTimeout(int i);
	//public void setLoyaltyRetryTimeout(int i);
	//public int getLoyaltyRetryTimeout();
	// Changes ends for Rev 1.0 (Ashish : Loyalty OTP)
	public void setLastVisit(EYSDate birthdate);
	public void setLastVisit12months(String Visit12months);
	public void setLastVisit3months(String Visit12months);
	public void setCustoffers(ArrayList setcustoffers) ;

	public EYSDate getLastVisit();
	public String getLastVisit3months();
	public String getLastVisit12months();
	public ArrayList getCustoffers();
	public boolean isCapillaryCustomerSuccessResponse() ;
	public void setCapillaryCustomerSuccessResponse(boolean CapillaryCustomerSuccessResponse);
    public String getOrigCustomerID();
	
	public void setOrigCustomerID(String origCustomerID);
	// Changes starts for Rev 1.3
	  public String getLMREWalletTraceId();
		
		public void setLMREWalletTraceId(String traceId);
		public boolean isLMREWalletCustomerFlag();
		public void setLMREWalletCustomerFlag(boolean isLMREWalletCustomerFlag);
		// Changes End for Rev 1.3

		public void setSbiPointBal(String sbiPointBal);
		public String getSbiPointBal();
		
}
