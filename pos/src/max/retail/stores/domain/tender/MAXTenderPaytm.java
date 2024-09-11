/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013	MAX HyperMarkets.    All Rights Reserved.
	Rev 1.0 	20/05/2013		Bhanu Priya 		Changes done for Paytm CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import java.util.Locale;

import max.retail.stores.domain.paytm.MAXPaytmResponse;
//import max.retail.stores.domain.MAXPaytmResponse;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AbstractTenderLineItem;
import oracle.retail.stores.foundation.utility.Util;

public class MAXTenderPaytm extends AbstractTenderLineItem implements MAXTenderPaytmIfc {

	private String mobileno = null;
	private String  orderID= null;
	private String  walletTransID= null;
	/**
	 * face value
	 **/
	protected CurrencyIfc faceValue = null;
	/**
	 * taxable status
	 **/
	
	protected MAXPaytmResponse resp;
	protected String paytmamt = null;

	public MAXTenderPaytm() {
		typeCode = MAXTenderLineItemIfc.TENDER_TYPE_PAYTM;
		amountTender = DomainGateway.getBaseCurrencyInstance();
		paytmamt = new String();
		setHasDenominations(false);
	}

	public Object clone() { // begin clone()
		// instantiate new object
		MAXTenderPaytm po = new MAXTenderPaytm();

		// set values
		setCloneAttributes(po);

		// pass back Object
		return po;
	}

	public void setCloneAttributes(MAXTenderPaytm newClass) {
		super.setCloneAttributes(newClass);
		if (mobileno != null) {
			newClass.setPaytmMobileNumber(getPaytmMobileNumber());
		}
		if (faceValue != null) {
			newClass.setFaceValue((CurrencyIfc) faceValue.clone());
		}
		((MAXTenderPaytm) newClass).setTypeCode(typeCode);
		if (paytmamt != null) {
			newClass.setPaytmAmount(paytmamt);
		}
		newClass.setPaytmWalletTransactionID(getPaytmWalletTransactionID());
		newClass.setPaytmOrderID(getOrderID());
	}



	public String toString() { // begin toString()
		// build result string
		StringBuffer strResult = new StringBuffer();
		strResult.append("Class:  MAXTenderPaytm (Revision ").append(getRevisionNumber()).append(") @")
				.append(hashCode()).append(Util.EOL);
		// add attributes to string
		if (getPaytmMobileNumber() == null) {
			strResult.append("Mobile Number:         [null]");
			strResult.append("Mobile Number:         [null]").append(Util.EOL);
			;

		} else {
			strResult.append("Mobile Number:                     ").append("[").append(getPaytmMobileNumber())
					.append("]").append("[").append(getPaytmAmount()).append("]").append(Util.EOL);
		}
		// pass back result
		return (strResult.toString());
	} // end toString()

	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = true;
		// confirm object instanceof this object
		if (obj instanceof MAXTenderPaytm) { // begin compare objects
			MAXTenderPaytm c = (MAXTenderPaytm) obj; // downcast
																		// the
																		// input
																		// object
			// compare all the attributes of TenderPurchaseOrder
			if (super.equals(obj) && Util.isObjectEqual(getPaytmMobileNumber(), c.getPaytmMobileNumber())
					&& Util.isObjectEqual(getFaceValue(), c.getFaceValue())
					&& Util.isObjectEqual(getPaytmAmount(), c.getPaytmAmount())) {
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
		strResult += abstractTenderLineItemAttributesToJournalString(paramLocale) + "\n  L.P. # " + mobileno
				+ "\n  L.A. # " + paytmamt;
		return (strResult);
	}

	@Override
	public void setPaytmMobileNumber(String mobileno) {
		// TODO Auto-generated method stub
		this.mobileno=mobileno;
	}

	@Override
	public String getPaytmMobileNumber() {
		// TODO Auto-generated method stub
		return mobileno;
	}

	@Override
	public String getPaytmAmount() {
		// TODO Auto-generated method stub
		return paytmamt;
	}

	@Override
	public void setPaytmAmount(String paytmamt) {
		// TODO Auto-generated method stub
		this.paytmamt=paytmamt;
	}

	@Override
	public void setFaceValue(CurrencyIfc faceValue) {
		// TODO Auto-generated method stub
		
	}

	
	

	

	@Override
	public CurrencyIfc getFaceValue() {
		// TODO Auto-generated method stub
		return faceValue;
	}

	@Override
	public void setPaytmWalletTransactionID(String walletTransID) {
		// TODO Auto-generated method stub
		this.walletTransID=walletTransID;
	}

	@Override
	public String getPaytmWalletTransactionID() {
		// TODO Auto-generated method stub
		return walletTransID;
	}

	@Override
	public void setPaytmOrderID(String orderID) {
		// TODO Auto-generated method stub
		this.orderID=orderID;
	}

	@Override
	public String getOrderID() {
		// TODO Auto-generated method stub
		return orderID;
	}
}
