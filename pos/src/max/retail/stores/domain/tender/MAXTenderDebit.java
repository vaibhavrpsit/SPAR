/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	Rev 1.0			27 Oct 2017			Jyoti Yadav				Changes for Innoviti Integration CR
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.domain.tender;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.tender.TenderDebit;

public class MAXTenderDebit extends TenderDebit implements MAXTenderDebitIfc {

	protected String authCode;

	protected String bankCode;

	protected boolean emiTransaction = false;

	public boolean isEmiTransaction() {
		return emiTransaction;
	}

	public void setEmiTransaction(boolean emiTransaction) {
		this.emiTransaction = emiTransaction;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String toString() {
		// result string
		String strResult = new String("Class:  LSIPLTenderDebit (Revision " + getRevisionNumber() + ") @" + hashCode());
		strResult += "authCode:                             [" + authCode + "]\n" + "bankCode:               ["
				+ bankCode + "]\n" + "emiTransaction:               [" + emiTransaction + "]";
		strResult += (super.toString());

		// pass back result
		return (strResult);
	}

	public Object clone() {
		MAXTenderDebitIfc c = new MAXTenderDebit();
		// set attributes in clone
		setCloneAttributes(c);

		return c;
	}

	protected void setCloneAttributes(MAXTenderDebitIfc newClass) {

		super.setCloneAttributes(newClass);
		if (authCode != null) {
			newClass.setAuthCode(new String(authCode));
		}
		if (bankCode != null) {
			newClass.setAuthCode(new String(bankCode));
		}
		newClass.setEmiTransaction(emiTransaction);
	}

	public boolean equals(Object obj) {
		boolean isEqual = false;
		if (obj instanceof MAXTenderDebit) {
			MAXTenderDebit c = (MAXTenderDebit) obj; // downcast the input
														// object
			// compare all the attributes of TenderDebit
			if (super.equals(obj) && Util.isObjectEqual(authCode, c.authCode)
					&& Util.isObjectEqual(bankCode, c.bankCode) && emiTransaction == c.emiTransaction) {
				isEqual = true; // set the return code to true
			}
		}
		return (isEqual);
	}
}
