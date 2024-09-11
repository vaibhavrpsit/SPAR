 
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
  	Rev 1.0  15/Apr/2013	Jyoti Rawal, Initial Draft: Changes for Gift Card Functionality 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.giftcard;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.pos.services.giftcard.GiftCardCargo;

//--------------------------------------------------------------------------
/**
 * This class provides the cargo for the gift card service.
 * <P>
 * 
 * @version $Revision: 1.2 $
 **/
// --------------------------------------------------------------------------
public class MAXGiftCardCargo extends GiftCardCargo {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3110001167450707636L;
	/**
	 * revision number supplied by source-code control system
	 **/
	public static final String revisionNumber = "$Revision: 1.2 $";
	/**
	 * gift card object reference
	 **/
	protected GiftCardIfc giftCard = null;
	/**
	 * The index of the line item which is activating
	 **/
	protected int lineItemCount = -1;

	/**
	 * The request type for gift card
	 **/
	protected int requestType = -1;

	protected String gcType = null;

	public String getGcType() {
		return gcType;
	}

	public void setGcType(String gcType) {
		this.gcType = gcType;
	}

	/**
	 * The current gift card amount
	 */
	protected CurrencyIfc amount = null;

	/**
	 * This boolean is used for navigational purposes
	 */
	protected boolean displayedGetAmountScreen = true;

	// --------------------------------------------------------------------------
	/**
	 * Gets the gift card reference
	 * 
	 * @return GiftCardIfc
	 **/
	// --------------------------------------------------------------------------
	public GiftCardIfc getGiftCard() {
		return giftCard;
	}

	// --------------------------------------------------------------------------
	/**
	 * Gets the Gift Card amount
	 * 
	 * @return CurrencyIfc
	 **/
	// --------------------------------------------------------------------------
	public CurrencyIfc getGiftCardAmount() {
		return amount;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the Gift Card amount
	 * 
	 * @param value
	 *            CurrencyIfc
	 **/
	// --------------------------------------------------------------------------
	public void setGiftCardAmount(CurrencyIfc value) {
		amount = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the gift card reference
	 * 
	 * @param value
	 *            GiftCardIfc
	 **/
	// --------------------------------------------------------------------------
	public void setGiftCard(GiftCardIfc value) {
		giftCard = value;
	}

	// --------------------------------------------------------------------------
	/**
	 * Gets the line item counter.
	 * 
	 * @return index of in the tender line which is doing activation
	 **/
	// --------------------------------------------------------------------------
	public int getLineItemCounter() {
		return lineItemCount;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the line item counter
	 * 
	 * @param inde
	 *            index of in the tender line which is doing activation
	 **/
	// --------------------------------------------------------------------------
	public void setLineItemCounter(int index) {
		lineItemCount = index;
	}

	// --------------------------------------------------------------------------
	/**
	 * Gets the request type.
	 * 
	 * @return request type
	 **/
	// --------------------------------------------------------------------------
	public int getRequestType() {
		return requestType;
	}

	// --------------------------------------------------------------------------
	/**
	 * Sets the request type
	 * 
	 * @param request
	 *            type
	 **/
	// --------------------------------------------------------------------------
	public void setRequestType(int type) {
		requestType = type;
	}
}
