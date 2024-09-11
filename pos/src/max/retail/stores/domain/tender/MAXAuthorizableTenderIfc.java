/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDate;

public interface MAXAuthorizableTenderIfc extends MAXTenderLineItemIfc { // begin
																			// interface
																			// AuthorizableTenderIfc

	/**
	 * revision number supplied by source-code-control system
	 **/
	public static String revisionNumber = "$Revision: 5$";
	/**
	 * authorization pending
	 **/
	public static final int AUTHORIZATION_STATUS_PENDING = 0;
	/**
	 * authorization approved
	 */
	public static final int AUTHORIZATION_STATUS_APPROVED = 1;
	/**
	 * authorization declined
	 */
	public static final int AUTHORIZATION_STATUS_DECLINED = 2;
	/**
	 * authorization status descriptors
	 */
	public static final String AUTHORIZATION_STATUS_DESCRIPTORS[] = { "Pending", "Approved", "Declined" };
	/**
	 * automatic authorization ( via financial network )
	 **/
	public static final String AUTHORIZATION_METHOD_AUTO = "Automatic";
	/**
	 * systematic authorization ( via tender limits in quarry properties )
	 */
	public static final String AUTHORIZATION_METHOD_SYSTEM = "System";
	/**
	 * manual authorization ( via telephone referal or manager approval)
	 */
	public static final String AUTHORIZATION_METHOD_MANUAL = "Manual";
	/**
	 * financial network online
	 */
	public static final String AUTHORIZATION_NETWORK_ONLINE = "Online";
	/**
	 * financial network offline
	 */
	public static final String AUTHORIZATION_NETWORK_OFFLINE = "Offline";

	/**
	 * Used in journal entries for tender authorization information.
	 */
	public static final String JOURNAL_AUTHORIZATION = new String("Authorization");

	// ---------------------------------------------------------------------
	/**
	 * Determines based on limits if offline authorization is Ok.
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>none
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>none
	 * </UL>
	 * 
	 * @return true if online authorization is required; false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean offlineAuthorizationOk();

	// ---------------------------------------------------------------------
	/**
	 * Determines based on limits if online authorization is required.
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>needs specification
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>needs specification
	 * </UL>
	 * 
	 * @param onlineIndicator
	 *            indicates if connection to provider is available
	 * @return true if online authorization is required; false otherwise
	 * @deprecated Use onlineAuthorizationRequired() for online;
	 *             offlineAuthorizationOk() when offline.
	 **/
	// ---------------------------------------------------------------------
	public boolean onlineAuthorizationRequired(boolean onlineIndicator);

	// ---------------------------------------------------------------------
	/**
	 * Determines based on limits if online authorization is required. Assumes
	 * system is online.
	 * <P>
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>needs specification
	 * </UL>
	 * <B>Post-Condition(s)</B>
	 * <UL>
	 * <LI>needs specification
	 * </UL>
	 * 
	 * @return true if online authorization is required; false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean onlineAuthorizationRequired();

	// ---------------------------------------------------------------------
	/**
	 * Sets authorization amount.
	 * <P>
	 * 
	 * @param authorization
	 *            amount
	 **/
	// ---------------------------------------------------------------------
	public void setAuthorizationAmount(CurrencyIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves authorization amount.
	 * <P>
	 * 
	 * @return authorization amount
	 **/
	// ---------------------------------------------------------------------
	public CurrencyIfc getAuthorizationAmount();

	// ---------------------------------------------------------------------
	/**
	 * Sets authorization method.
	 * <P>
	 * 
	 * @param value
	 *            authorization method
	 **/
	// ---------------------------------------------------------------------
	public void setAuthorizationMethod(String value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves authorization method.
	 * <P>
	 * 
	 * @return authorization method
	 **/
	// ---------------------------------------------------------------------
	public String getAuthorizationMethod();

	// ---------------------------------------------------------------------
	/**
	 * Sets authorization code.
	 * <P>
	 * 
	 * @param value
	 *            authorization code
	 **/
	// ---------------------------------------------------------------------
	public void setAuthorizationCode(String value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves authorization code.
	 * <P>
	 * 
	 * @return authorization code
	 **/
	// ---------------------------------------------------------------------
	public String getAuthorizationCode();

	// ---------------------------------------------------------------------
	/**
	 * Sets authorization status.
	 * <P>
	 * 
	 * @param value
	 *            authorization status
	 **/
	// ---------------------------------------------------------------------
	public void setAuthorizationStatus(int value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves authorization status.
	 * <P>
	 * 
	 * @return authorization status
	 **/
	// ---------------------------------------------------------------------
	public int getAuthorizationStatus();

	// ---------------------------------------------------------------------
	/**
	 * Sets authorization response.
	 * <P>
	 * 
	 * @param value
	 *            authorization response
	 **/
	// ---------------------------------------------------------------------
	public void setAuthorizationResponse(String value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves authorization response.
	 * <P>
	 * 
	 * @return authorization response
	 **/
	// ---------------------------------------------------------------------
	public String getAuthorizationResponse();

	// ---------------------------------------------------------------------
	/**
	 * Sets status of financial network.
	 * <P>
	 * 
	 * @param value
	 *            status of financial network
	 **/
	// ---------------------------------------------------------------------
	public void setFinancialNetworkStatus(String value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves status of financial network.
	 * <P>
	 * 
	 * @return status of financial network
	 **/
	// ---------------------------------------------------------------------
	public String getFinancialNetworkStatus();

	// ---------------------------------------------------------------------
	/**
	 * Returns string representation of authorization status.
	 * <B>Pre-Condition(s)</B>
	 * <UL>
	 * <LI>none
	 * </UL>
	 * ;
	 * </UL>
	 * 
	 * @return String representation of authorization status
	 **/
	// ---------------------------------------------------------------------
	public String authorizationStatusToString();

	// ---------------------------------------------------------------------
	/**
	 * Returns journal string.
	 * <P>
	 * 
	 * @return journal string
	 **/
	// ---------------------------------------------------------------------
	public String abstractAttributesToJournalString();

	// ---------------------------------------------------------------------
	/**
	 * Method to default display string function.
	 * <P>
	 * 
	 * @return String representation of object
	 **/
	// ---------------------------------------------------------------------
	public String abstractAttributesToString();

	// ---------------------------------------------------------------------
	/**
	 * Gets Authorized Date
	 * 
	 * @return String
	 */
	// ---------------------------------------------------------------------
	public EYSDate getAuthorizedDateTime();

	// ---------------------------------------------------------------------
	/**
	 * Sets Authorized Date
	 * 
	 * @param authorizedDate
	 */
	// ---------------------------------------------------------------------
	public void setAuthorizedDateTime(EYSDate authorizedDateTime);

	// ---------------------------------------------------------------------
	/**
	 * Gets Settlement Data
	 * 
	 * @return String
	 */
	// ---------------------------------------------------------------------
	public String getSettlementData();

	// ---------------------------------------------------------------------
	/**
	 * Sets Settlement Data
	 * 
	 * @param settlementData
	 */
	// ---------------------------------------------------------------------
	public void setSettlementData(String settlementData);

}
