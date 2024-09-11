/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXUserDefineDenominationBeanModel extends POSBaseBeanModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected CurrencyIfc currency=null;
	protected int quantity = 0;
	public MAXUserDefineDenominationBeanModel() {
		currency = DomainGateway.getBaseCurrencyInstance();
		quantity=0;
	}
	public MAXUserDefineDenominationBeanModel(CurrencyIfc currency, int quantity) {
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
}
