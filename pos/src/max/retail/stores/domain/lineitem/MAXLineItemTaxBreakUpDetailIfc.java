/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates.All rights reserved. 
 *  
 *  ===========================================================================
 * $Log:Added as part of India Localization Changes related to Tax
 * ===========================================================================
 *  
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.lineitem;

import java.math.BigDecimal;

import max.retail.stores.domain.tax.MAXTaxAssignmentIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;

/**
 * Interface for LineItemTaxBreakUpDetail Object.
 * 
 * Each Item will have a TaxCategory associated with it and in turn each ItemTax
 * object will have an array LineItemTaxBreakUpDetails based on the
 * TaxAssignments for that item.
 * 
 * <P>
 * Each Item will have ItemTax object, which wil be enhanced to have array of
 * LineItemTaxBreakUpDetail which will have TaxAssignment,TaxableAmount and
 * TaxAmount, which effectively represents the TaxAssignemnts for the Item. When
 * TaxAmount of all the LineItemTaxBreakUpDetail is summed it gives the total
 * item tax.
 * <P>
 * 
 * 
 * @author Oracle IDC
 * @since 12.0.1 India Localization Changes related to Tax
 */
public interface MAXLineItemTaxBreakUpDetailIfc extends EYSDomainIfc {
	/**
	 * 
	 * @return
	 */
	public CurrencyIfc getTaxableAmount();

	/**
	 * 
	 * @param taxableAmount
	 */
	public void setTaxableAmount(CurrencyIfc taxableAmount);

	/**
	 * 
	 * @return
	 */
	public CurrencyIfc getTaxAmount();

	/**
	 * 
	 * @param taxAmount
	 */
	public void setTaxAmount(CurrencyIfc taxAmount);

	/**
	 * 
	 * @return
	 */
	public MAXTaxAssignmentIfc getTaxAssignment();

	/**
	 * 
	 * @param taxAssignment
	 */
	public void setTaxAssignment(MAXTaxAssignmentIfc taxAssignment);

	/**
	 * 
	 * @return
	 */
	public int getSaleReturnTaxFlag();

	/**
	 * 
	 * @param saleReturnTaxFlag
	 */
	public void setSaleReturnTaxFlag(int saleReturnTaxFlag);

	/**
	 * 
	 * @return taxCode
	 */
	public String getTaxCode();

	/**
	 * 
	 * @param taxCode
	 */
	public void setTaxCode(String taxCode);

	/**
	 * 
	 * @return taxCode
	 */
	public String getTaxCodeDescription();

	/**
	 * 
	 * @param taxCodeDescription
	 */
	public void setTaxCodeDescription(String taxCodeDesc);

	/**
	 * 
	 * @return taxRate
	 */
	public String getTaxRate();

	/**
	 * 
	 * @param taxBreakUpRate
	 */
	public void setTaxRate(BigDecimal taxBreakUpRate);

}
