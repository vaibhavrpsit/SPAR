/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		27/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.financial;

import oracle.retail.stores.domain.financial.FinancialCountTenderItem;

public class MAXFinancialCountTenderItem extends FinancialCountTenderItem {

	protected boolean isTenderEntered = false;

	public boolean isTenderEntered() {
		return isTenderEntered;
	}

	public void setTenderEntered(boolean isTenderEntered) {
		this.isTenderEntered = isTenderEntered;
	}

	public Object clone() { // begin clone()
							// instantiate new object
		MAXFinancialCountTenderItem c = new MAXFinancialCountTenderItem();
		setCloneAttributes(c);
		return ((Object) c);
	} // end clone()

	protected void setCloneAttributes(MAXFinancialCountTenderItem newClass) {
		super.setCloneAttributes(newClass);
		newClass.setTenderEntered(isTenderEntered);
	}
}
