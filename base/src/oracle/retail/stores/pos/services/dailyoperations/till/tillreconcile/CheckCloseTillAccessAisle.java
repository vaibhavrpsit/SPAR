/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/CheckCloseTillAccessAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:53 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/06/30 00:21:24  dcobb
 *   @scr 5165 - Allowed to reconcile till when database is offline.
 *   @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;


//------------------------------------------------------------------------------
/**
    Set the access function for the Till Close station. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class CheckCloseTillAccessAisle extends PosLaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Set the access function for the Till Close station.
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        TillReconcileCargo cargo = (TillReconcileCargo) bus.getCargo();
        cargo.setRequestedService(RoleFunctionIfc.RECONCILE_TILL);
        cargo.setAccessFunctionID(RoleFunctionIfc.CLOSE_TILL);
    }

}
