/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Bhanu Priya 		Changes done for Paytm CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import java.util.Locale;

//import max.retail.stores.domain.MAXPaytmResponse;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AbstractTenderLineItem;
import oracle.retail.stores.foundation.utility.Util;

public class MAXTenderMobikwik extends AbstractTenderLineItem implements MAXTenderMobikwikIfc {

	private String mobikWikMob = null;
	private String  mobikWikOrderID= null;
	private String  mobikWikTransID= null;
	/**
	 * face value
	 **/
	protected CurrencyIfc faceValue = null;
	/**
	 * taxable status
	 **/
	
	//protected MAXPaytmResponse resp;
	protected String mobikwikAmt = null;

	public MAXTenderMobikwik() {
		typeCode = MAXTenderLineItemIfc.TENDER_TYPE_MOBIKWIK;
		amountTender = DomainGateway.getBaseCurrencyInstance();
		mobikwikAmt = new String();
		setHasDenominations(false);
	}

	public Object clone() { // begin clone()
		// instantiate new object
		MAXTenderMobikwik po = new MAXTenderMobikwik();

		// set values
		setCloneAttributes(po);

		// pass back Object
		return po;
	}

	public void setCloneAttributes(MAXTenderMobikwik newClass) {
		super.setCloneAttributes(newClass);
		if (mobikWikMob != null) {
			newClass.setMobikwikMobileNumber(getMobikwikMobileNumber());
		}
		if (faceValue != null) {
			newClass.setFaceValue((CurrencyIfc) faceValue.clone());
		}
		((MAXTenderMobikwik) newClass).setTypeCode(typeCode);
		if (mobikwikAmt != null) {
			newClass.setMobikwikAmount(mobikwikAmt);
		}
		newClass.setMobikwikWalletTransactionID(getMobikwikWalletTransactionID());
		newClass.setMobikwikOrderID(getMobikwikOrderID());
	}



	public String toString() { // begin toString()
		// build result string
		StringBuffer strResult = new StringBuffer();
		strResult.append("Class:  MAXTenderMobikwik (Revision ").append(getRevisionNumber()).append(") @")
				.append(hashCode()).append(Util.EOL);
		// add attributes to string
		if (getMobikwikMobileNumber() == null) {
			strResult.append("Mobile Number:         [null]");
			strResult.append("Mobile Number:         [null]").append(Util.EOL);
			

		} else {
			strResult.append("Mobile Number:                     ").append("[").append(getMobikwikMobileNumber())
					.append("]").append("[").append(getMobikwikAmount()).append("]").append(Util.EOL);
		}
		// pass back result
		return (strResult.toString());
	} // end toString()

	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = true;
		// confirm object instanceof this object
		if (obj instanceof MAXTenderMobikwik) { // begin compare objects
			MAXTenderMobikwik c = (MAXTenderMobikwik) obj; // downcast
																		// the
																		// input
																		// object
			// compare all the attributes of TenderPurchaseOrder
			if (super.equals(obj) && Util.isObjectEqual(getMobikwikMobileNumber(), c.getMobikwikMobileNumber())
					&& Util.isObjectEqual(getFaceValue(), c.getFaceValue())
					&& Util.isObjectEqual(getMobikwikAmount(), c.getMobikwikAmount())) {
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
		strResult += abstractTenderLineItemAttributesToJournalString(paramLocale) + "\n  L.P. # " + mobikWikMob
				+ "\n  L.A. # " + mobikwikAmt;
		return (strResult);
	}

	

	@Override
	public void setMobikwikMobileNumber(String mobikWikMob) {
		// TODO Auto-generated method stub
		this.mobikWikMob=mobikWikMob;
	}

	@Override
	public String getMobikwikMobileNumber() {
		// TODO Auto-generated method stub
		return mobikWikMob;
	}

	@Override
	public String getMobikwikAmount() {
		// TODO Auto-generated method stub
		return mobikwikAmt;
	}

	@Override
	public void setMobikwikAmount(String mobikwikAmt) {
		// TODO Auto-generated method stub
		this.mobikwikAmt=mobikwikAmt;
	}

	@Override
	public void setMobikwikWalletTransactionID(String mobikWikTransID) {
		// TODO Auto-generated method stub
		this.mobikWikTransID=mobikWikTransID;
	}

	@Override
	public String getMobikwikWalletTransactionID() {
		// TODO Auto-generated method stub
		return mobikWikTransID;
	}

	@Override
	public void setMobikwikOrderID(String mobikWikOrderID) {
		// TODO Auto-generated method stub
		this.mobikWikOrderID=mobikWikOrderID;
	}

	@Override
	public String getMobikwikOrderID() {
		// TODO Auto-generated method stub
		return mobikWikOrderID;
	}

	@Override
	public void setFaceValue(CurrencyIfc faceValue) {
		// TODO Auto-generated method stub
		this.faceValue=faceValue;
	}

	@Override
	public CurrencyIfc getFaceValue() {
		// TODO Auto-generated method stub
		return faceValue;
	}
}
