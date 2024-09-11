/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/ticcustomer/MAXTICCustomerConfig.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.ticcustomer;

import java.io.Serializable;

public class MAXTICCustomerConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6141038000972171594L;

	protected String displayField;

	protected String MandatoryField;

	public String getDisplayField() {
		return displayField;
	}

	public void setDisplayField(String displayField) {
		this.displayField = displayField;
	}

	public String getMandatoryField() {
		return MandatoryField;
	}

	public void setMandatoryField(String mandatoryField) {
		MandatoryField = mandatoryField;
	}

}
