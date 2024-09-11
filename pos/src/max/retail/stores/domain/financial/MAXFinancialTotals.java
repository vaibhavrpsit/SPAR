/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	Prateek		5/Sep/2013		Changes done for Modification in report.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.financial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import oracle.retail.stores.domain.financial.FinancialTotals;

public class MAXFinancialTotals extends FinancialTotals {

	protected HashMap couponDenominationCount = null;
	protected HashMap acquirerBankDetails = null;
	protected List giftCertificateDenomination = null;
	protected List cashDenomination = null;

	/** MAX Rev 1.1 Change : Start **/
	protected List enteredTotals = new ArrayList();

	/** MAX Rev 1.1 Change : End **/

	public MAXFinancialTotals() {
		super();
	}

	public void resetTotals() {
		super.resetTotals();
		couponDenominationCount = null;
	}

	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	public Object clone() {
		MAXFinancialTotals c = new MAXFinancialTotals();
		setCloneAttributes(c);
		return c;
	}

	public void setCloneAttributes(MAXFinancialTotals newClass) {
		super.setCloneAttributes(newClass);
		if (this.couponDenominationCount != null) {
			newClass.setCouponDenominationCount(this.couponDenominationCount);
			newClass.setAcquirerBankDetails(this.acquirerBankDetails);
			newClass.setGiftCertificateDenomination(this.giftCertificateDenomination);
			newClass.setGiftCertificateDenomination(this.cashDenomination);
		}
	}

	public HashMap getCouponDenominationCount() {
		return couponDenominationCount;
	}

	public void setCouponDenominationCount(HashMap couponDenominationCount) {
		this.couponDenominationCount = couponDenominationCount;
	}

	public HashMap getAcquirerBankDetails() {
		return acquirerBankDetails;
	}

	public void setAcquirerBankDetails(HashMap acquirerBankDetails) {
		this.acquirerBankDetails = acquirerBankDetails;
	}

	public List getGiftCertificateDenomination() {
		return giftCertificateDenomination;
	}

	public void setGiftCertificateDenomination(List giftCertificateDenomination) {
		this.giftCertificateDenomination = giftCertificateDenomination;
	}

	public List getCashDenomination() {
		return cashDenomination;
	}

	public void setCashDenomination(List cashDenomination) {
		this.cashDenomination = cashDenomination;
	}

	/** MAX Rev 1.1 Change : Start **/
	public List getEnteredTotals() {
		return enteredTotals;
	}

	public void setEnteredTotals(List enteredTotals) {
		this.enteredTotals = enteredTotals;
	}

	/** MAX Rev 1.1 Change : End **/
}
