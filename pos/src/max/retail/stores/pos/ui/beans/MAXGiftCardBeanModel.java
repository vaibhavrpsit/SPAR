/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.pos.ui.beans.GiftCardBeanModel;

public class MAXGiftCardBeanModel extends GiftCardBeanModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1092990885399919041L;

	// File revision number
	public static String revisionNumber = "$Revision: 1.3 $";

	// Swiping indicator
	protected boolean swiped = false;

	// Scanning indicator
	protected boolean scanned = false;

	// Gift Card Number
	protected String giftCardNumber = null;

	// Gift Card initial balance
	protected BigDecimal giftCardInitialBalance = null;

	// Gift Card amount label
	protected String giftCardAmountLabel;

	// Gift Card amount
	protected BigDecimal giftCardAmount = null;

	// Gift Card expiration date
	/*
	 * @deprecated in version 7.0
	 */
	protected EYSDate giftCardExpirationDate = DomainGateway.getFactory().getEYSDateInstance();

	// Gift Card status
	protected String giftCardStatus = null;

	// Gift Card status
	protected boolean openAmount = false;

	// Found the card in gift card inquiry
	protected boolean validInquriy = true;

	protected String trackData = null;

	public String getTrackData() {
		return trackData;
	}

	public void setTrackData(String trackData) {
		this.trackData = trackData;
	}

	// ---------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// ---------------------------------------------------------------------
	public MAXGiftCardBeanModel() {
	}

	// ---------------------------------------------------------------------------
	/**
	 * Get swiping flag
	 * 
	 * @return boolean flag
	 **/
	// ---------------------------------------------------------------------------
	public boolean isSwiped() {
		return swiped;
	}

	// ---------------------------------------------------------------------------
	/**
	 * Sets the swiping flag
	 * 
	 * @param value
	 *            boolean
	 **/
	// ---------------------------------------------------------------------------
	public void setSwiped(boolean value) {
		swiped = value;
	}

	// ---------------------------------------------------------------------------
	/**
	 * Get scanning flag
	 * 
	 * @return boolean flag
	 **/
	// ---------------------------------------------------------------------------
	public boolean isScanned() {
		return scanned;
	}

	// ---------------------------------------------------------------------------
	/**
	 * Sets the scanning flag
	 * 
	 * @param value
	 *            boolean
	 **/
	// ---------------------------------------------------------------------------
	public void setScanned(boolean value) {
		scanned = value;
	}

	// ------------------------------------------------------------------------
	/**
	 * Gets the gift card number.
	 * 
	 * @return String gift card number
	 */
	// ------------------------------------------------------------------------
	public String getGiftCardNumber() {
		return giftCardNumber;
	}

	// ------------------------------------------------------------------------
	/**
	 * Sets the gift card number.
	 * 
	 * @param number
	 *            the gift card number
	 */
	// ------------------------------------------------------------------------
	public void setGiftCardNumber(String number) {
		giftCardNumber = number;
	}

	// ------------------------------------------------------------------------
	/**
	 * Gets the gift card amount label.
	 * 
	 * @return String gift card amount label
	 */
	// ------------------------------------------------------------------------
	public String getGiftCardAmountLabel() {
		return giftCardAmountLabel;
	}

	// ------------------------------------------------------------------------
	/**
	 * Sets the gift card amount label.
	 * 
	 * @param label
	 *            the gift card amount label.
	 */
	// ------------------------------------------------------------------------
	public void setGiftCardAmountLabel(String label) {
		giftCardAmountLabel = label;
	}

	// ------------------------------------------------------------------------
	/**
	 * Gets the gift card initial balance.
	 * 
	 * @return BigDecimal gift card initial balance
	 */
	// ------------------------------------------------------------------------
	public BigDecimal getGiftCardInitialBalance() {
		return giftCardInitialBalance;
	}

	// ------------------------------------------------------------------------
	/**
	 * Sets the gift card initial balance.
	 * 
	 * @param amount
	 *            the gift card initial balance
	 */
	// ------------------------------------------------------------------------
	public void setGiftCardInitialBalance(BigDecimal amount) {
		giftCardInitialBalance = amount;
	}

	// ------------------------------------------------------------------------
	/**
	 * Gets the gift card amount.
	 * 
	 * @return BigDecimal gift card amount
	 */
	// ------------------------------------------------------------------------
	public BigDecimal getGiftCardAmount() {
		return giftCardAmount;
	}

	// ------------------------------------------------------------------------
	/**
	 * Sets the gift card amount.
	 * 
	 * @param amount
	 *            the gift card amount
	 */
	// ------------------------------------------------------------------------
	public void setGiftCardAmount(BigDecimal amount) {
		giftCardAmount = amount;
	}

	// ------------------------------------------------------------------------
	/**
	 * Gets the gift card expiration date description.
	 * 
	 * @return EYSDate gift card expiration date
	 * @deprecated in version 7.0
	 */
	// ------------------------------------------------------------------------
	public EYSDate getGiftCardExpirationDate() {
		return giftCardExpirationDate;
	}

	// ------------------------------------------------------------------------
	/**
	 * Sets the gift card expiration date.
	 * 
	 * @param date
	 *            the gift card expiration date
	 * @deprecated in version 7.0
	 */
	// ------------------------------------------------------------------------
	public void setGiftCardExpirationDate(EYSDate date) {
		giftCardExpirationDate = date;
	}

	// ------------------------------------------------------------------------
	/**
	 * Gets the gift card status.
	 * 
	 * @return String gift card status
	 */
	// ------------------------------------------------------------------------
	/*public String getGiftCardStatus() {
		return giftCardStatus;
	}*/

	// ------------------------------------------------------------------------
	/**
	 * Sets the gift card status.
	 * 
	 * @param status
	 *            the gift card status
	 */
	// ------------------------------------------------------------------------
	public void setGiftCardStatus(String status) {
		giftCardStatus = status;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Gets the open amount flag.
	 * <P>
	 * 
	 * @return openAmount as boolean
	 **/
	// ----------------------------------------------------------------------------
	public boolean getOpenAmount() {
		return (openAmount);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets the open amount flag.
	 * <P>
	 * 
	 * @param value
	 *            as boolean
	 **/
	// ----------------------------------------------------------------------------
	public void setOpenAmount(boolean value) {
		openAmount = value;
	}

	// ----------------------------------------------------------------------------
	/**
	 * Gets the card is valid or not.
	 * <P>
	 * 
	 * @return isCardValid as boolean
	 **/
	// ----------------------------------------------------------------------------
	public boolean isValidInquriy() {
		return (validInquriy);
	}

	// ----------------------------------------------------------------------------
	/**
	 * Sets the card is found in inquiry or not.
	 * <P>
	 * 
	 * @param value
	 *            as boolean
	 **/
	// ----------------------------------------------------------------------------
	public void setValidInquiry(boolean value) {
		validInquriy = value;
	}

	// ---------------------------------------------------------------------------
	/**
	 * Converts to a string representing the data in this Object
	 * 
	 * @returns string representing the data in this Object
	 **/
	// ---------------------------------------------------------------------------
	public String toString() {
		StringBuffer buff = new StringBuffer();

		buff.append("Class: GiftCardBeanModel Revision: " + revisionNumber + "\n");
		buff.append("Swiped         [" + swiped + "]\n");
		buff.append("Scanned        [" + scanned + "]\n");
		buff.append("CardNumber     [" + giftCardNumber + "]\n");
		buff.append("CardAmount     [" + giftCardAmount + "]\n");
		buff.append("CardStatus     [" + giftCardStatus + "]\n");

		return buff.toString();
	}

}
