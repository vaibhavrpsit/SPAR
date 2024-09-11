/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *
 *	Rev 1.0		Dec 27, 2016		Mansi Goel		Changes for Advanced Search
 *
 ********************************************************************************/

package max.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;

public class MAXItemInfoBeanModel extends ItemInfoBeanModel {

	private static final long serialVersionUID = 1131722161899958974L;

	protected boolean multipleMaximumRetailPriceFlag;

	protected boolean retailLessThanMRPFlag = true;

	protected BigDecimal maximumRetailPrice = Util.I_BIG_DECIMAL_ZERO;

	protected boolean maximumRetailPriceEnabled = true;

	public BigDecimal getMaximumRetailPrice() {
		return maximumRetailPrice;
	}

	public void setMaximumRetailPrice(BigDecimal maximumRetailPrice) {
		this.maximumRetailPrice = maximumRetailPrice;
	}

	public boolean isMultipleMaximumRetailPriceFlag() {
		return multipleMaximumRetailPriceFlag;
	}

	public void setMultipleMaximumRetailPriceFlag(boolean multipleMaximumRetailPriceFlag) {
		this.multipleMaximumRetailPriceFlag = multipleMaximumRetailPriceFlag;
	}

	public boolean isRetailLessThanMRPFlag() {
		return retailLessThanMRPFlag;
	}

	public void setRetailLessThanMRPFlag(boolean retailLessThanMRPFlag) {
		this.retailLessThanMRPFlag = retailLessThanMRPFlag;
	}

	public boolean isMaximumRetailPriceEnabled() {
		return maximumRetailPriceEnabled;
	}

	public void setMaximumRetailPriceEnabled(boolean maximumRetailPriceEnabled) {
		this.maximumRetailPriceEnabled = maximumRetailPriceEnabled;
	}

}
