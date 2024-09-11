/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ui.beans;

import java.io.Serializable;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;

public class MAXCouponDenominationCountSummaryBeanModel implements Serializable{

	protected CurrencyIfc amount = null;
	protected int quantity = 0;
	protected String label = null;
	public MAXCouponDenominationCountSummaryBeanModel() {
	}
	public CurrencyIfc getAmount() {
		return amount;
	}
	public void setAmount(CurrencyIfc amount) {
		this.amount = amount;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}	
}
