/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  15/July/2013               Prateek				Initial Draft: Changes done for BUG: 7103
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 **/
package max.retail.stores.pos.journal;

import max.retail.stores.pos.receipt.MAXVATHelper;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.receipt.VATHelper;

/**
 * Journal formatter for SaleReturnLineItems in VAT enabled environment.
 * $Revision: 1$
 */
public class MAXVATSaleReturnLineItemJournalFormatter extends MAXSaleReturnLineItemJournalFormatter
{
    /** Handle to VAT helper. */
    protected MAXVATHelper  helper = new MAXVATHelper ();

    /**
     *
     * @param taxMode
     * @return
     * @see com._360commerce.pos.journal.SaleReturnLineItemJournalFormatter#getTaxFlag(int)
     */
    protected String getTaxFlag(int taxMode)
    {
        PLUItemIfc pluItem = saleReturnLineItem.getPLUItem();
        return helper.getVATTaxFlag(saleReturnLineItem, pluItem, taxMode);
    }

    /**
     * Journals the tax information.
     * @param buffer
     * @param ip
     * @param taxMode
     * @param taxScope
     */
    protected void journalTax(StringBuffer buffer, ItemPriceIfc itemPrice, int taxMode, int taxScope)
    {
        helper.journalVATLineItemTax(buffer, saleReturnLineItem, itemPrice, taxMode, taxScope);
    }    
}
