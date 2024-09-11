/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.manager.tenderauth;

import oracle.retail.stores.domain.utility.GiftCardIfc;

//-------------------------------------------------------------------------
/**
 * GiftCardAuthResponse object encapsultes the data after authorization is done
 * 
 * 
 * @version $Revision: 3$
 **/
// -------------------------------------------------------------------------
public class MAXGiftCardAuthResponse extends MAXTenderAuthResponse {
	// This id is used to tell
	// the compiler not to generate a
	// new serialVersionUID.
	//
	static final long serialVersionUID = -1484635281080257263L;

	protected GiftCardIfc giftCard = null;

	// ---------------------------------------------------------------------
	/**
	 * Constructor sets the request type using constant value from
	 * TenderAuthConstantsIfc.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXGiftCardAuthResponse() {

	}

	// -------------------------------------------------------------------------
	/**
	 * Sets the gift card.
	 * 
	 * @param card
	 *            GiftCard
	 **/
	// -------------------------------------------------------------------------
	public void setGiftCard(GiftCardIfc card) {
		giftCard = card;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns the gift card.
	 * 
	 * @return GiftCard
	 **/
	// ---------------------------------------------------------------------
	public GiftCardIfc getGiftCard() {
		return giftCard;
	}

	// ---------------------------------------------------------------------
	/**
	 * Returns attribute descriptor String
	 * <P>
	 * 
	 * @return String formatted attribute descriptor
	 **/
	// ---------------------------------------------------------------------
	public String toString() {
		StringBuffer values = new StringBuffer(super.toString());

		values.append("\nGift Card:");

		if (giftCard == null) {
			values.append("                  [null]\n");
		} else {
			values.append(giftCard.toString());
		}

		return values.toString();
	}
}
