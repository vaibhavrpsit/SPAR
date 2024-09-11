/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * MODIFIED    (MM/DD/YY)
 *    jswan     05/14/14 - Fix issue removing canceled transaction from display.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Removes selected transaction summary from the summary list.
 * @since 14.1
 */
public class RemoveCancelTransactionFromListAisle extends PosLaneActionAdapter
{ 
    private static final long serialVersionUID = -8107756821339860666L;

    /**
     * Removes selected transaction summary from the summary list.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get the transaction and initialize the letter value
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo) bus.getCargo();
        cargo.removeSelectedTransactionFromSummaryList();
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
