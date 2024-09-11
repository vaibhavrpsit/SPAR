/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.tender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MAXCreditCardDetails implements Serializable {

	protected String bankName;
	protected List tidDetails;

	public MAXCreditCardDetails() {
		bankName = new String();
		tidDetails = new ArrayList();
	}

	public MAXCreditCardDetails(String bankName, List tidDetails) {
		super();
		this.bankName = bankName;
		this.tidDetails = tidDetails;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public List getTidDetails() {
		return tidDetails;
	}

	public void setTidDetails(List tidDetails) {
		this.tidDetails = tidDetails;
	}
}
