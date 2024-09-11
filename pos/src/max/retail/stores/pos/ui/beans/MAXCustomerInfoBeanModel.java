/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Oct 19, 2016		Mansi Goel			Changes for Customer FES
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ui.beans;

import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;

public class MAXCustomerInfoBeanModel extends CustomerInfoBeanModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3867055792913181084L;

	// Changes for Rev 1.0 : Start
	protected String typeTICOrPOS = "";

	/**
	 * MAXCustomerInfoBeanModel constructor.
	 */
	public MAXCustomerInfoBeanModel() {
		super();
	}

	public String getTypeTICOrPOS() {
		return typeTICOrPOS;
	}

	public void setTypeTICOrPOS(String typeTICOrPOS) {
		this.typeTICOrPOS = typeTICOrPOS;
	}
	
	public String getExtPostalCode() {
		return fieldExtPostalCode;
	}

	public void setExtPostalCode(String extPostalCode) {
		fieldExtPostalCode = extPostalCode;
	}
	// Changes for Rev 1.0 : End

}
