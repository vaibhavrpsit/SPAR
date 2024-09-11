/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2016	MAX HyperMarkets.    All Rights Reserved.
 
	Rev 1.0 	12/07/2016		Abhishek Goyal		Initial Draft: Changes for CR
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.tender;

import java.util.Locale;

import oracle.retail.stores.domain.tender.AbstractTenderLineItem;
import oracle.retail.stores.foundation.utility.Util;

public class MAXTenderEComPrepaid extends AbstractTenderLineItem implements MAXTenderEComPrepaidIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MAXTenderEComPrepaid() {
		typeCode = MAXTenderLineItemIfc.TENDER_TYPE_ECOM_PREPAID;
		setHasDenominations(false);
	}

	public Object clone() {
		// TODO Auto-generated method stub
		MAXTenderEComPrepaid tenderEComPrepaid = new MAXTenderEComPrepaid();

		// set values
		setCloneAttributes(tenderEComPrepaid);

		// pass back Object
		return tenderEComPrepaid;
	}

	public void setCloneAttributes(MAXTenderEComPrepaid newClass) {
		super.setCloneAttributes(newClass);
	}

	public String toString() { // begin toString()
		// build result string
		StringBuffer strResult = new StringBuffer();
		strResult.append("Class:  MAXTenderEComPrepaid (Revision ").append(getRevisionNumber()).append(") @")
				.append(hashCode()).append(Util.EOL);
		strResult.append(abstractTenderLineItemAttributesToString());
		// add attributes to string

		// pass back result
		return (strResult.toString());
	}

	public boolean equals(Object obj) { // begin equals()
		boolean isEqual = true;
		// confirm object instanceof this object
		if (obj instanceof MAXTenderEComPrepaid) { // begin compare objects
			MAXTenderEComPrepaid c = (MAXTenderEComPrepaid) obj; // downcast the
																	// input
																	// object
			// compare all the attributes of TenderPurchaseOrder
			if (super.equals(obj)) {
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

	@Override
	public String toJournalString(Locale paramLocale) {
		// TODO Auto-generated method stub
		String journalString = abstractTenderLineItemAttributesToJournalString(paramLocale);
		return (journalString);
	}

}
