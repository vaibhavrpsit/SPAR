/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXDetailCouponBeanModel extends POSBaseBeanModel{

	protected String[] couponName = null;
	protected CurrencyIfc[] couponValue = null;
	protected CurrencyIfc total = null;
	public String[] getCouponName() {
		return couponName;
	}
	public void setCouponName(String[] couponName) {
		this.couponName = couponName;
	}
	public CurrencyIfc[] getCouponValue() {
		return couponValue;
	}
	public void setCouponValue(CurrencyIfc[] currency) {
		this.couponValue = currency;
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
		for(int i=0;i<couponValue.length;i++)
		{
			total = total.add(couponValue[i]);
		}
	}
}
