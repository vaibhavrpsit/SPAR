/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.tender;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;

public class MAXDenominationCount implements Serializable {

	protected CurrencyIfc currency = null;
	protected int quantity;
	protected String subType = null;

	public MAXDenominationCount() {
		currency = DomainGateway.getBaseCurrencyInstance();
		quantity = 0;
	}

	public MAXDenominationCount(CurrencyIfc currency, int quantity) {
		this.currency = currency;
		this.quantity = quantity;
	}

	public CurrencyIfc getCurrency() {
		return currency;
	}

	public void setCurrency(CurrencyIfc currency) {
		this.currency = currency;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getSubType() {
		return subType;
	}

	public void setSubType(String subType) {
		this.subType = subType;
	}
}
