/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates.All rights reserved. 
 *  
 *  ===========================================================================
 * $Log:Added as part of India Localization Changes related to Tax
 * ===========================================================================
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.tax;

import java.math.BigDecimal;

import oracle.retail.stores.domain.utility.EYSDomainIfc;

public interface MAXTaxAssignmentIfc extends EYSDomainIfc {
	public int getApplicationOrder();

	public void setApplicationOrder(int applicationOrder);

	public String getAppliedOn();

	public void setAppliedOn(String appliedOn);

	public BigDecimal getTaxableAmountFactor();

	public void setTaxableAmountFactor(BigDecimal taxableFactor);

	public BigDecimal getTaxAmountFactor();

	public void setTaxAmountFactor(BigDecimal taxableFactor);

	public int getTaxCategory();

	public void setTaxCategory(int taxCategory);

	public String getTaxCode();

	public void setTaxCode(String taxCode);

	public String getTaxCodeDescription();

	public void setTaxCodeDescription(String taxCodeDescription);

	public BigDecimal getTaxRate();

	public void setTaxRate(BigDecimal taxRate);
	//Change for Rev 1.0 : Starts
	public String getTaxType();

	public void setTaxType(String taxType);
	//Change for Rev 1.0 : Ends
}
