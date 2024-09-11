/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/TransReentryRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:28 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:19 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/07/01 17:01:56  blj
 *   @scr 5932 - updated per requirements.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * 
 * Since transaction reentry transactions bypass the lookup of
 * return transactions by receipt or gift receipt, In this Road, we set the 
 * appropriate flags in the cargo to simulate a not found scenario 
 * w/o displaying the not found dialog screen.
 */
public class TransReentryRoad extends LaneActionAdapter
{
//----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();
        cargo.setHaveReceipt(true);
        cargo.setTransactionFound(false);
    }
}
