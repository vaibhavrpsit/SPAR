/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/ReverseTendersActionSite.java /rgbustores_13.4x_generic_branch/1 2011/07/26 16:59:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   07/26/11 - Evaluates tenders for reversal when tendering is
 *                         aborted
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.SaleReturnTransactionADO;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 *  Sets up all reversible tenders for a reversal
 */
@SuppressWarnings("serial")
public class ReverseTendersActionSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc saleReturnTransaction = cargo.getTransaction();
        RetailTransactionADOIfc retailTransactionADO = new SaleReturnTransactionADO();
        retailTransactionADO.fromLegacy(saleReturnTransaction);
        cargo.setCurrentTransactionADO(retailTransactionADO);
        
        TenderADOIfc[] tenders = cargo.getCurrentTransactionADO()
                   .getTenderLineItems(TenderLineItemCategoryEnum.REVERSAL_PENDING);
        
        // if we marked any tenders for reversal, go to authorization
        // otherwise go on to delete tenders
        String letter = "Authorize";
        if (tenders.length == 0)
        {
            letter = "Continue";
        }        
        
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
