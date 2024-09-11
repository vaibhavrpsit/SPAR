/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Prateek		Initial Draft: Changes for TIC Customer Integration
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AbstractTenderLineItem;
import oracle.retail.stores.foundation.utility.Util;

public class MAXTenderLoyaltyPoints extends AbstractTenderLineItem implements MAXTenderLoyaltyPointsIfc {

	private String loyaltyCardNumber = null;

	/**
	 * face value
	 **/
	protected CurrencyIfc faceValue = null;
	/**
	 * taxable status
	 **/
	protected String loyaltyPointAmount = null;

	public MAXTenderLoyaltyPoints() {
		typeCode = MAXTenderLineItemIfc.TENDER_TYPE_LOYALTY_POINTS;
		amountTender = DomainGateway.getBaseCurrencyInstance();
		loyaltyPointAmount = new String();
		setHasDenominations(false);
	}

	public Object clone() { // begin clone()
		// instantiate new object
		MAXTenderLoyaltyPoints po = new MAXTenderLoyaltyPoints();

		// set values
		setCloneAttributes(po);

		// pass back Object
		return po;
	}

	public void setCloneAttributes(MAXTenderLoyaltyPoints newClass) {
		super.setCloneAttributes(newClass);
		if (loyaltyCardNumber != null) {
			newClass.setLoyaltyCardNumber(getLoyaltyCardNumber());
		}
		if (faceValue != null) {
			newClass.setFaceValue((CurrencyIfc) faceValue.clone());
		}
		((MAXTenderLoyaltyPoints) newClass).setTypeCode(typeCode);
		if (loyaltyPointAmount != null) {
			newClass.setLoyaltyPointAmount(loyaltyPointAmount);
		}
	}

	public String getLoyaltyCardNumber() {
		return loyaltyCardNumber;
	}

	public void setLoyaltyCardNumber(String loyaltyCardNumber) {
		this.loyaltyCardNumber = loyaltyCardNumber;
	}

	public CurrencyIfc getFaceValue() {
		return faceValue;
	}

	public void setFaceValue(CurrencyIfc faceValue) {
		this.faceValue = faceValue;
	}

	public String getLoyaltyPointAmount() {
		return loyaltyPointAmount;
	}

	public void setLoyaltyPointAmount(String loyaltyPointAmount) {
		this.loyaltyPointAmount = loyaltyPointAmount;
	}

	public String toString() { // begin toString()
		// build result string
		StringBuffer strResult = new StringBuffer();
		strResult.append("Class:  MAXTenderLoyaltyPoints (Revision ").append(getRevisionNumber()).append(") @")
				.append(hashCode()).append(Util.EOL);
		// add attributes to string
		if (getLoyaltyCardNumber() == null) {
			strResult.append("LoyaltyCardNumber:         [null]");
			strResult.append("LoyaltyCardAmount:         [null]").append(Util.EOL);
			;

		} else {
			strResult.append("LoyaltyCardNumber:                     ").append("[").append(getLoyaltyCardNumber())
					.append("]").append("[").append(getLoyaltyPointAmount()).append("]").append(Util.EOL);
		}
		// pass back result
		return (strResult.toString());
	} // end toString()

	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = true;
		// confirm object instanceof this object
		if (obj instanceof MAXTenderLoyaltyPoints) { // begin compare objects
			MAXTenderLoyaltyPoints c = (MAXTenderLoyaltyPoints) obj; // downcast
																		// the
																		// input
																		// object
			// compare all the attributes of TenderPurchaseOrder
			if (super.equals(obj) && Util.isObjectEqual(getLoyaltyCardNumber(), c.getLoyaltyCardNumber())
					&& Util.isObjectEqual(getFaceValue(), c.getFaceValue())
					&& Util.isObjectEqual(getLoyaltyPointAmount(), c.getLoyaltyPointAmount())) {
				isEqual = true; // set the return code to true
			} else {
				isEqual = false; // set the return code to false
			}
		} // end compare objects
		else {
			isEqual = false;
		}
		return (isEqual);
	}

	public void setTypeCode(int type) {
		typeCode = type;
	}

	@Override
	public String toJournalString(Locale paramLocale) {
		// TODO Auto-generated method stub
		String strResult = new String();
		strResult += abstractTenderLineItemAttributesToJournalString(paramLocale) + "\n  L.P. # " + loyaltyCardNumber
				+ "\n  L.A. # " + loyaltyPointAmount;
		return (strResult);
	}
}
