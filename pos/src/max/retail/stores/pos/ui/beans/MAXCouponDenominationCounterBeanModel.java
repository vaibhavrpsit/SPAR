/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

import java.util.ArrayList;

import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

public class MAXCouponDenominationCounterBeanModel extends POSBaseBeanModel{
	
	protected ArrayList denominationCount = null;
	protected String couponName = null;
	public MAXCouponDenominationCounterBeanModel() {
	}
	public ArrayList getDenominationCount() {
		return denominationCount;
	}
	public void setDenominationCount(ArrayList denominationCount) {
		this.denominationCount = denominationCount;
	}
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}
}
