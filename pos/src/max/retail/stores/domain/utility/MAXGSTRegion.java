/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.utility;

import java.io.Serializable;

public class MAXGSTRegion implements Serializable {
	
	private static final long serialVersionUID = 1L;
	String  regionCode;
	String regionDesc;
	/**
	 * @return the regionCode
	 */
	public String getRegionCode() {
		return regionCode;
	}
	/**
	 * @param regionCode the regionCode to set
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
	/**
	 * @return the regionDesc
	 */
	public String getRegionDesc() {
		return regionDesc;
	}
	/**
	 * @param regionDesc the regionDesc to set
	 */
	public void setRegionDesc(String regionDesc) {
		this.regionDesc = regionDesc;
	}
	

}
