/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.tender;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;

public interface MAXTenderGiftCardIfc extends MAXTenderChargeIfc { // begin
																	// interface
																	// TenderGiftCardIfc
	/**
	 * revision number supplied by source-code-control system
	 **/
	public static String revisionNumber = "$Revision: 4$";

	// ----------------------------------------------------------------------------
	/**
	 * Retrieves gift card reference.
	 * <P>
	 * 
	 * @return gift card reference
	 **/
	// ----------------------------------------------------------------------------
	public GiftCardIfc getGiftCard();

	// ----------------------------------------------------------------------------
	/**
	 * Sets gift card reference.
	 * <P>
	 * 
	 * @param value
	 *            gift card reference
	 **/
	// ----------------------------------------------------------------------------
	public void setGiftCard(GiftCardIfc value);

	// ---------------------------------------------------------------------
	/**
	 * Retrieves card number.
	 * <P>
	 * 
	 * @return card number
	 **/
	// ---------------------------------------------------------------------
	public String getCardNumber();

	// ---------------------------------------------------------------------
	/**
	 * Sets card number.
	 * <P>
	 * 
	 * @param card
	 *            number
	 **/
	// ---------------------------------------------------------------------
	public void setCardNumber(String card);

	/**
	 * @return Returns the cashChangeAmount.
	 */
	public CurrencyIfc getCashChangeAmount();

	/**
	 * @param cashChangeAmount
	 *            The cashChangeAmount to set.
	 */
	public void setCashChangeAmount(CurrencyIfc cashChangeAmount);

	// ---------------------------------------------------------------------
	/**
	 * Returns the request type of the gift card
	 * 
	 * @return String
	 **/
	// ---------------------------------------------------------------------
	public String getState();

	// ---------------------------------------------------------------------
	/**
	 * Sets the request type of the gift card
	 * 
	 * @param val
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setState(String val);

	// ---------------------------------------------------------------------
	/**
	 * Sets the status of the gift card
	 * 
	 * @param status
	 *            String
	 **/
	// ---------------------------------------------------------------------
	public void setStatus(String status);

	// ---------------------------------------------------------------------
	/**
	 * Creates clone of this object.
	 * <P>
	 * 
	 * @return Object clone of this object
	 **/
	// ---------------------------------------------------------------------
	public Object clone();

	// ---------------------------------------------------------------------
	/**
	 * Determine if two objects are identical.
	 * <P>
	 * 
	 * @param obj
	 *            object to compare with
	 * @return true if the objects are identical, false otherwise
	 **/
	// ---------------------------------------------------------------------
	public boolean equals(Object obj);

	// ----------------------------------------------------------------------
	/**
	 * Return whether or not tender is a gift card credit.
	 * 
	 * @param value
	 **/
	// ----------------------------------------------------------------------
	public void setGiftCardCredit(boolean value);

	// ----------------------------------------------------------------------
	/**
	 * Return whether or not tender is a gift card credit.
	 * 
	 * @return
	 **/
	// ----------------------------------------------------------------------
	public boolean isGiftCardCredit();

	// ---------------------------------------------------------------------
	/**
	 * Returns request type.
	 * 
	 * @return request type
	 **/
	// ---------------------------------------------------------------------
	public int getRequestType();

	// ---------------------------------------------------------------------
	/**
	 * Sets request type.
	 * 
	 * @param value
	 *            request type
	 **/
	// ---------------------------------------------------------------------
	public void setRequestType(int type);

}
