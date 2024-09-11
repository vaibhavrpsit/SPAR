/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 *
 *	Rev 1.0			27 Oct 2017			Jyoti Yadav				Changes for Innoviti Integration CR
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.ui.beans;

import java.util.Vector;

import oracle.retail.stores.pos.ui.beans.ReasonBeanModel;

public class MAXCreditDebitInfoBeanModel extends ReasonBeanModel {
	protected String authorizationCode = null;

	protected String selectedBankCode = null;

	protected Vector bankCodeStrings = new Vector();

	public String getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(String authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	public Vector getBankCodeStrings() {
		return bankCodeStrings;
	}

	public void setBankCodeStrings(Vector bankCodeStrings) {
		this.bankCodeStrings = bankCodeStrings;
	}

	public String getSelectedBankCode() {
		return selectedBankCode;
	}

	public void setSelectedBankCode(String selectedBankCode) {
		this.selectedBankCode = selectedBankCode;
	}
}
