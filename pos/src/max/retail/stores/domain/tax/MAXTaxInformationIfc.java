/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev 1.0 20/12/2016    hitesh dua   set tax breakup details
*
* *******************************************************************************/
package max.retail.stores.domain.tax;

import max.retail.stores.domain.lineitem.MAXLineItemTaxBreakUpDetailIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;

/**
 * @author Mohd Asif
 * 
 */
public interface MAXTaxInformationIfc extends TaxInformationIfc {

	/* India Localization- Tax related Changes starts here */
	/**
	 * Gets the LineItemTaxBreakUpDetailIfc[]
	 * 
	 * @return LineItemTaxBreakUpDetailIfc[]
	 */
	public MAXLineItemTaxBreakUpDetailIfc[] getLineItemTaxBreakUpDetails();

	/**
	 * Sets the LineItemTaxBreakUpDetailIfc[]
	 * 
	 * @param LineItemTaxBreakUpDetailIfc
	 *            []
	 */
	public void setLineItemTaxBreakUpDetails(
			MAXLineItemTaxBreakUpDetailIfc[] taxBreakUpDetails);
	/* India Localization- Tax related Changes ends here */
	
	
	//changes for rev 1.0 start
		/**
		 * @return the taxableAmt
		 */
		public CurrencyIfc getTaxableAmt();

		/**
		 * @param taxableAmt the taxableAmt to set
		 */
		public void setTaxableAmt(CurrencyIfc taxableAmt);

		/**
		 * @return the taxAmt
		 */
		public CurrencyIfc getTaxAmt(); 

		/**
		 * @param taxAmt the taxAmt to set
		 */
		public void setTaxAmt(CurrencyIfc taxAmt);
		
		public CurrencyIfc addTaxableAmt(CurrencyIfc taxableAmt);
			
		
		public CurrencyIfc addTaxAmt(CurrencyIfc taxAmt);
		//changes for rev 1.0 end


}

