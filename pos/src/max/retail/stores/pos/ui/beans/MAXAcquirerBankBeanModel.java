/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXAcquirerBankBeanModel extends POSBaseBeanModel {

	protected String[] bankName;
	protected CurrencyIfc[] amount;
	protected CurrencyIfc total;
	public MAXAcquirerBankBeanModel() {
		super();
	}
	public MAXAcquirerBankBeanModel(String[] bankName, CurrencyIfc[] currency,
			CurrencyIfc total) {
		super();
		this.bankName = bankName;
		this.amount = currency;
		this.total = total;
	}
	
	public String[] getBankName() {
		return bankName;
	}
	public void setBankName(String[] bankName) {
		this.bankName = bankName;
	}
	public CurrencyIfc[] getAmount() {
		return amount;
	}
	public void setAmount(CurrencyIfc[] amount) {
		this.amount = amount;
	}
	public CurrencyIfc getTotal() {
		return total;
	}
	public void setTotal(CurrencyIfc total) {
		this.total = total;
	}
	public void updateTotal()
	{
		total = DomainGateway.getBaseCurrencyInstance("0.00");
		for(int i=0;i<amount.length;i++)
		{
			total = total.add(amount[i]);
		}
	}
}
