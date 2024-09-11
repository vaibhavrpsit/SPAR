/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/IsICCDetailsAvailable.java /rgbustores_13.4x_generic_branch/2 2011/06/29 16:08:36 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/28/11 - rename hashed credit card field to token
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *    cgreene   06/01/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * Road should only be clear if the cargo contains an authorization of a credit
 * card that was an ICC "chip and pin" card.
 *
 * @author cgreene
 * @since 13.4
 */
public class IsICCDetailsAvailable implements TrafficLightIfc
{
    private static final long serialVersionUID = -8476255349053186513L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        TenderableTransactionIfc trans = cargo.getTransaction();
        if (trans.getTransactionType() != TransactionIfc.TYPE_VOID)
        {
            TenderLineItemIfc[] tenders = trans.getTenderLineItems();
            for (int i = 0; i < tenders.length; i++)
            {
                if (tenders[i] instanceof TenderChargeIfc && 
                   ((TenderChargeIfc)tenders[i]).getICCDetails() != null)
                {
                    return true;
                }
            }
        }
        return false;
    }

}
