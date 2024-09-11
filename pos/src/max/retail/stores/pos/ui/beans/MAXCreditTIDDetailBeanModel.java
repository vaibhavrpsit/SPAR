/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCreditTIDDetailBeanModel extends POSBaseBeanModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected String[] tid;
	protected String[] batchid;
	protected CurrencyIfc[] amount;
	protected CurrencyIfc total;
	
	public MAXCreditTIDDetailBeanModel() {
		super();
	}
	public MAXCreditTIDDetailBeanModel(String[] tid, String[] batchid,
			CurrencyIfc[] amount, CurrencyIfc total) {
		super();
		this.tid = tid;
		this.batchid = batchid;
		this.amount = amount;
		this.total = total;
	}
	
	public String[] getTid() {
		return tid;
	}
	public void setTid(String[] tid) {
		this.tid = tid;
	}
	public String[] getBatchid() {
		return batchid;
	}
	public void setBatchid(String[] batchid) {
		this.batchid = batchid;
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
	public void updateTotals()
	{
		total = DomainGateway.getBaseCurrencyInstance();
		for(int i=0;i<amount.length;i++)
		{
			total = total.add(amount[i]);
		}
	}
	
	
		
}
