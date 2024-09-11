/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/IsRequestedServiceTillReconcileSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:34 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillCloseCargo;

//--------------------------------------------------------------------------
/**
    This signal determines if the requesting service is Till Reconcile.
    <P>
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsRequestedServiceTillReconcileSignal implements TrafficLightIfc

{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6570301980708255702L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Determines whether teh requesting service is Till Reconcile.
        <p>
        @param bus the bus trying to proceed
        @return true if till reconcile ; false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        TillCloseCargo cargo = (TillCloseCargo)bus.getCargo();
        boolean flag = false;
        if (cargo.getRequestedService() == RoleFunctionIfc.RECONCILE_TILL)
        {
          flag = true;
        }

        return flag;
    }

}
