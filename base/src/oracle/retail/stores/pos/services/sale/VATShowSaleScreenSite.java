/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/VATShowSaleScreenSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   06/25/10 - Adding VAT classes back into source tree.
 *    asinton   06/25/10 - Adding VAT classes back into source tree.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         4/30/2007 3:46:05 PM   Alan N. Sinton  Merge
 *       from v12.0_temp.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;

/**
 * Subclass of ShowSaleScreenSite to support VAT screen changes.
 */
public class VATShowSaleScreenSite extends ShowSaleScreenSite
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6316753665929408809L;

    /**
     * Overriding the super's setLineDisplay() method to support changes for VAT.
     * @param pda
     * @param transaction
     * @param utility
     * @see oracle.retail.stores.pos.services.sale.ShowSaleScreenSite#setLineDisplay(com.extendyourstore.pos.device.POSDeviceActions, com.extendyourstore.domain.transaction.SaleReturnTransactionIfc, com.extendyourstore.pos.manager.ifc.UtilityManagerIfc)
     */
    protected void setLineDisplay(POSDeviceActions pda, SaleReturnTransactionIfc transaction, UtilityManagerIfc utility)
    {
        try
        {
            if (transaction != null)
            {
                //update running total on line display since modifications
                // could have been made to the transaction
                String totalTag =
                utility.retrieveLineDisplayText(
                        "TotalAbbreviatedText",
                        TagConstantsIfc.SHORT_TOTAL_TAG);
                StringBuffer displayLine2 =  new StringBuffer()
                  .append(totalTag)
                  .append(
                        Util.formatTextData(
                                makeShorter(
                                transaction
                                .getTransactionTotals()
                                .getBalanceDue()),
                                9,
                                true));
                pda.displayTextAt(1, 0, displayLine2.toString());
            }
            else
            {
                pda.clearText();
            }
        }
        catch(DeviceException de)
        {
            logger.warn("Unable to use Line Display: " + de.getMessage() + "");
        }
    }

}
