/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.1  02/July/2013              Prateek				Changes done for BUG 6821
 *  Rev 1.0  23/May/2013               Prateek				Changes for Grouping like items together on receipt
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.lineitem;

import oracle.retail.stores.domain.lineitem.ReturnItem;
import oracle.retail.stores.foundation.utility.Util;

public class MAXReturnItem extends ReturnItem {

	public boolean isFromGiftReceipt() {
		return this.fromGiftReceipt;
	}

	public boolean isHaveReceipt() {
		return this.haveReceipt;
	}

	public boolean ifFromRetrievedTransaction() {
		return this.fromRetrievedTransaction;
	}

	public boolean compare(Object obj) { // begin equals()
		boolean isEqual = false;
		try {
			ReturnItem c = (ReturnItem) obj; // downcast the input object

			// compare all the attributes of ReturnItem
			if ((Util.isObjectEqual(pluItem, c.getPLUItem())) && (Util.isObjectEqual(price, c.getPrice()))
					&& (taxRate == c.getTaxRate()) && (Util.isObjectEqual(store, c.getStore()))
					&& (Util.isObjectEqual(salesAssociate, c.getSalesAssociate()))
					&& (Util.isObjectEqual(originalTransactionID, c.getOriginalTransactionID()))
					&& (Util.isObjectEqual(originalTransactionBusinessDate, c.getOriginalTransactionBusinessDate()))
					&& (getReasonCode() == c.getReasonCode())
					&& (Util.isObjectEqual(restockingFee, c.getRestockingFee()))
					&& (Util.isObjectEqual(serialNumber, c.getSerialNumber())) && (entryMethod == c.getEntryMethod())
					&& (fromGiftReceipt == ((MAXReturnItem) c).isFromGiftReceipt())
					&& (haveReceipt == ((MAXReturnItem) c).isHaveReceipt())
					&& (fromRetrievedTransaction == c.isFromRetrievedTransaction())
					&& (subLineNumber == c.getSubLineNumber())
					&& (Util.isObjectEqual(newLowerPrice, c.getNewLowerPrice()))) {
				isEqual = true;
			} else {
				isEqual = false;
			}
		} catch (Exception e) {
			isEqual = false;
		}
		return (isEqual);
	} // end equals()

	/** MAX Rev 1.1 Change : Start **/
	public Object clone() {
		MAXReturnItem newItem = new MAXReturnItem();
		setCloneAttributes(newItem);
		return newItem;
	}
	/** MAX Rev 1.1 Change : End **/
}
