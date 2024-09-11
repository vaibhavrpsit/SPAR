/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.MAXUtils;

import java.io.Serializable;

public class MAXIGSTTax implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String taxCategory;
	private String storeId;
	private String toRegion;
	private String fromRegion;
	private boolean rrpTaxEnabled;
	public boolean isRrpTaxEnabled() {
		return rrpTaxEnabled;
	}
	public void setRrpTaxEnabled(boolean rrpTaxEnabled) {
		this.rrpTaxEnabled = rrpTaxEnabled;
	}
	/**
	 * @return the taxCategory
	 */
	public String getTaxCategory() {
		return taxCategory;
	}
	/**
	 * @param taxCategory the taxCategory to set
	 */
	public void setTaxCategory(String taxCategory) {
		this.taxCategory = taxCategory;
	}
	/**
	 * @return the storeId
	 */
	public String getStoreId() {
		return storeId;
	}
	/**
	 * @param storeId the storeId to set
	 */
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	/**
	 * @return the toRegion
	 */
	public String getToRegion() {
		return toRegion;
	}
	/**
	 * @param toRegion the toRegion to set
	 */
	public void setToRegion(String toRegion) {
		this.toRegion = toRegion;
	}
	/**
	 * @return the fromRegion
	 */
	public String getFromRegion() {
		return fromRegion;
	}
	/**
	 * @param fromRegion the fromRegion to set
	 */
	public void setFromRegion(String fromRegion) {
		this.fromRegion = fromRegion;
	}

}
