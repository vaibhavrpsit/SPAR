/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *
 * Copyright (c) 2008, 2009, Oracle and/or its affiliates.All rights reserved. 
 *  
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.tax;

import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;

/**
 * Interface to Calculate the Multiple Taxes for an Item based on the TaxCategory.
 * 
 * <P>
 * This MultipleTaxCalculator uses the various Tax Factors,Taxable Factors based
 * on the TaxAssignments and Tax Inclusive Selling Retail to arrive at the
 * Multiple Taxes for an Item. It calculates the Multiple Taxes for Sale and
 * Return of items.
 * 
 * <P>
 * 
 * 
 * @author Oracle IDC
 * @since 12.0.1 India Localization Changes related to Tax
 */
public interface MAXMultipleTaxCalculatorIfc {
	
	/**
	 * Returns the Item Taxable Amount by Subtracting the Total Item tax from
	 * the Selling Retail Inclusive of Tax.
	 * 
	 * @param item
	 * @return CurrencyIfc
	 */
	public CurrencyIfc getItemTaxableAmount(TaxLineItemInformationIfc item);
	
	/**
	 * Returns the TotalItemTax Amount by Summing the Tax Break up Amounts from
	 * the Tax information of each LineItem.
	 * 
	 * 
	 * @param item
	 * @return CurrencyIfc
	 */
	public CurrencyIfc calculateTotalItemTax(TaxLineItemInformationIfc item);
	
	/**
	 * Returns the TotalItemTax Amount by Summing the taxable amount from the lineItemTaxBreakUpDetail
	 * 
	 * 
	 * @param item
	 * @return CurrencyIfc
	 */
	public CurrencyIfc calculateTotalItemTax(MAXLineItemTaxBreakUpDetailIfc[] lineItemTaxBreakUpDetail,CurrencyIfc taxInclusiveSellingRetail,SaleReturnLineItemIfc srli);
	
	/**
	 * Create a TaxInformationIfc object.  This data is saved to the database
	 * with the transaction
	 *  
	 *  @param taxableAmount Taxable amount 
	 *  @param taxAmount Tax charged
	 *  @param mode Tax mode (standard, override, etc)
	 *  @return TaxInformationIfc object
	 */
	public TaxInformationIfc createTaxInformation(CurrencyIfc taxableAmount,
			CurrencyIfc taxAmount,
			MAXLineItemTaxBreakUpDetailIfc[] lineItemBreakUpDetails); 
	/**
	 * Get the TaxInculsive Selling Retail after discounts are applied, if Item is discountable. 
	 *  
	 *  @param item
	 *  @return taxable amount
	 */
	public CurrencyIfc getTaxInclusiveSellingRetail(
			TaxLineItemInformationIfc item) ;
}