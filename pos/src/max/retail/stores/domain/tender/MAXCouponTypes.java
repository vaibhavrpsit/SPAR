/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tender;

import java.io.Serializable;
import java.util.List;

public class MAXCouponTypes implements Serializable {

	protected String couponName;
	protected List denominationCount;

	public MAXCouponTypes() {
	}

	public MAXCouponTypes(String couponName, List denominationCount) {
		this.couponName = couponName;
		this.denominationCount = denominationCount;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public List getDenominationCount() {
		return denominationCount;
	}

	public void setDenominationCount(List denominationCount) {
		this.denominationCount = denominationCount;
	}
}
