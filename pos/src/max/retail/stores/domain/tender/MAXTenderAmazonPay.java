/************************************************************************************************
 *   
 *	Copyright (c) 2019 MAX SPAR Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Jul 01, 2019		Purushotham Reddy 		Changes for POS_Amazon Pay Integration 
 *
 ************************************************************************************************/

package max.retail.stores.domain.tender;

/**
@author Purushotham Reddy Sirison
**/

import java.util.Locale;

import max.retail.stores.domain.paytm.MAXPaytmResponse;
//import max.retail.stores.domain.MAXPaytmResponse;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.AbstractTenderLineItem;
import oracle.retail.stores.foundation.utility.Util;

public class MAXTenderAmazonPay extends AbstractTenderLineItem implements MAXTenderAmazonPayIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String mobileNumber = null;
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
	protected String paymentAmount = null;

	public MAXTenderAmazonPay() {
		typeCode = MAXTenderLineItemIfc.TENDER_TYPE_AMAZON_PAY;
		amountTender = DomainGateway.getBaseCurrencyInstance();
		paymentAmount = new String();
		setHasDenominations(false);
	}

	public Object clone() { // begin clone()
		// instantiate new object
		MAXTenderAmazonPay po = new MAXTenderAmazonPay();

		// set values
		setCloneAttributes(po);

		// pass back Object
		return po;
	}

	public void setCloneAttributes(MAXTenderAmazonPay newClass) {
		super.setCloneAttributes(newClass);
		if (mobileNumber != null) {
			newClass.setAmazonPayMobileNumber(getAmazonPayMobileNumber());
		}
		if (faceValue != null) {
			newClass.setFaceValue((CurrencyIfc) faceValue.clone());
		}
		((MAXTenderAmazonPay) newClass).setTypeCode(typeCode);
		if (paymentAmount != null) {
			newClass.setAmazonPayAmount(paymentAmount);
		}
		newClass.setAmazonPayWalletTransactionID(getAmazonPayWalletTransactionID());
		newClass.setAmazonPayOrderID(getAmazonPayOrderID());
	}


	public String toString() { // begin toString()
		// build result string
		StringBuffer strResult = new StringBuffer();
		strResult.append("Class:  MAXTenderAmazonPay (Revision ").append(getRevisionNumber()).append(") @")
				.append(hashCode()).append(Util.EOL);
		// add attributes to string
		if (getAmazonPayMobileNumber() == null) {
			strResult.append("Mobile Number:         [null]");
			strResult.append("Mobile Number:         [null]").append(Util.EOL);
			;

		} else {
			strResult.append("Mobile Number:                     ").append("[").append(getAmazonPayMobileNumber())
					.append("]").append("[").append(getAmazonPayAmount()).append("]").append(Util.EOL);
		}
		// pass back result
		return (strResult.toString());
	} // end toString()

	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = true;
		// confirm object instanceof this object
		if (obj instanceof MAXTenderAmazonPay) { // begin compare objects
			MAXTenderAmazonPay c = (MAXTenderAmazonPay) obj; // downcast
																		// the
																		// input
																		// object
			// compare all the attributes of TenderPurchaseOrder
			if (super.equals(obj) && Util.isObjectEqual(getAmazonPayMobileNumber(), c.getAmazonPayMobileNumber())
					&& Util.isObjectEqual(getFaceValue(), c.getFaceValue())
					&& Util.isObjectEqual(getAmazonPayAmount(), c.getAmazonPayAmount())) {
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
		String strResult = new String();
		strResult += abstractTenderLineItemAttributesToJournalString(paramLocale) + "\n  L.P. # " + mobileNumber
				+ "\n  L.A. # " + paymentAmount;
		return (strResult);
	}

	@Override
	public void setAmazonPayMobileNumber(String mobileNumber) {
		this.mobileNumber=mobileNumber;
	}

	@Override
	public String getAmazonPayMobileNumber() {
		return mobileNumber;
	}

	@Override
	public String getAmazonPayAmount() {
		return paymentAmount;
	}

	@Override
	public void setAmazonPayAmount(String paymentAmount) {
		this.paymentAmount=paymentAmount;
	}

	@Override
	public void setFaceValue(CurrencyIfc faceValue) {
		
	}


	@Override
	public CurrencyIfc getFaceValue() {
		return faceValue;
	}

	@Override
	public void setAmazonPayWalletTransactionID(String walletTransID) {
		this.walletTransID=walletTransID;
	}

	@Override
	public String getAmazonPayWalletTransactionID() {
		return walletTransID;
	}

	@Override
	public void setAmazonPayOrderID(String orderID) {
		this.orderID=orderID;
	}

	@Override
	public String getAmazonPayOrderID() {
		return orderID;
	}
}
