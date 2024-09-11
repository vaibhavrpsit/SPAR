/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/RemoveTenderRoad.java /rgbustores_13.4x_generic_branch/3 2011/09/27 14:13:49 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/26/11 - set cashOnlyOption false and firstTimeChangeDue true
 *                         so that Change Due Options screen doesn't appear
 *                         when inappropriate.
 *    cgreene   08/09/11 - added support to remove last tender when change
 *                         override is declined
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:40 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/06/04 21:27:18  bwf
 *   @scr 5205 Fixed change due options and store credit flow for undo 
 *   and cancel during change and refund.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This class removes the tender from the cargo from the transaction.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
@SuppressWarnings("serial")
public class RemoveTenderRoad extends PosLaneActionAdapter
{

    /**
     * This arrive method removes the store credit tender.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        TenderADOIfc tender = cargo.getTenderADO();
        if (tender != null)
        {
            cargo.getCurrentTransactionADO().removeTender(tender);
            cargo.setCashOnlyOption(false);
            cargo.setFirstTimeChangeDue(true);
        }
        else
        {
            // in the cases where a change tender is being built, the payment
            // tendered which caused the change amount may be referenced in the
            // cargo via the lineDisplayTender.
            logger.info("TenderADO is null in cargo. Removing last displayed tender.");
            tender = cargo.getLineDisplayTender();
            if (tender != null)
            {
                cargo.getCurrentTransactionADO().removeTender(tender);
                cargo.setCashOnlyOption(false);
                cargo.setFirstTimeChangeDue(true);
            }
            else
            {
                logger.warn("No tender was found in cargo to remove from transaction.");
            }
        }
    }

}
