/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/SetSelectedToMatchReturnedRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
    This road modifies the list of ExternalOrderItemReturnStatusElement so
    that the "selected" status for each element matches the "returned" status.
**/
//--------------------------------------------------------------------------
public class SetSelectedToMatchReturnedRoad extends LaneActionAdapter
{

    /** serialVersionUID */
    private static final long serialVersionUID = -8031104293068927927L;

    //----------------------------------------------------------------------
    /**
       This method modifies the list of ExternalOrderItemReturnStatusElement so
       that the "selected" status for each element matches the "returned" status.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();
        cargo.resetExternalOrderItemsSelectForReturn();
    }
}
