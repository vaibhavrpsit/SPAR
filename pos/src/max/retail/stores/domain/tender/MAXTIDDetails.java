/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;

public class MAXTIDDetails implements Serializable {

	protected String tid;
	protected String batchid;
	protected CurrencyIfc amount;

	public MAXTIDDetails() {
		tid = new String();
		batchid = new String();
		amount = DomainGateway.getBaseCurrencyInstance();
	}

	public MAXTIDDetails(String tid, String batchid, CurrencyIfc amount) {
		this.tid = tid;
		this.batchid = batchid;
		this.amount = amount;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getBatchid() {
		return batchid;
	}

	public void setBatchid(String batchid) {
		this.batchid = batchid;
	}

	public CurrencyIfc getAmount() {
		return amount;
	}

	public void setAmount(CurrencyIfc amount) {
		this.amount = amount;
	}
}
