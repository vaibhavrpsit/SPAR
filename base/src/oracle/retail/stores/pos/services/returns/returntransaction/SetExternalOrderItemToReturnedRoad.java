/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/SetExternalOrderItemToReturnedRoad.java /rgbustores_13.4x_generic_branch/2 2011/08/18 08:44:04 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    sgu       08/04/10 - redirect the return flow back to
 *                         DisplayExternalOrderDialogSite for any rejected
 *                         partially used gift card item
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     07/14/10 - Added for undo processing of external orders.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
 * If this is an external order return, this class marks the external order item
 * as returned.
 **/
// --------------------------------------------------------------------------
public class SetExternalOrderItemToReturnedRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2748447510518531064L;

    // ----------------------------------------------------------------------
    /**
       This method sets up the cargo to process the next item in the list. If this
       is an external order return, marks the external order as returned.
     * <P>
     *
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Increment the current item index.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        if (cargo.isExternalOrder() && cargo.getReturnItem() != null)
        {
            cargo.setAssociatedExternalOrderItemReturnedStatus(cargo.getPLUItem().getReturnExternalOrderItem(), true);
        }
    }
}
