/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/CompleteReturnProcessingRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     07/14/10 - Added for undo processing of external orders.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
 * This road fixes up the original transaction to contain the total returned 
 * quantity, and sets the external order data on the returned sale return line items
 **/
// --------------------------------------------------------------------------
public class CompleteReturnProcessingRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3439758183098028821L;

    // ----------------------------------------------------------------------
    /**
     * This method fixes up the original transaction to contain the total returned 
     * quantity, and sets the external order data on the returned sale 
     * return line items
     * @param bus Service Bus
     **/
    // ----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Every thing is good to go.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        
        if (cargo.getTransferCargo())
        {
            cargo.completeReturnProcess();
        }
    }
}
