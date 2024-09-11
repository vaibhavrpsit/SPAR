/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/FloatCountSucceededRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:12 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:07 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

// foundation imports
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;

//------------------------------------------------------------------------------
/**

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class FloatCountSucceededRoad extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 933547093371438939L;


    public static final String LANENAME = "FloatCountSucceededRoad";

    //--------------------------------------------------------------------------
    /**


       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        // Count was a success, add financial count to till

        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        RegisterIfc r = cargo.getRegister();
        FinancialTotalsIfc ft = cargo.getFloatTotals();

        r.getTillByID(cargo.getTillID()).addTotals(ft);
        r.addTotals(ft);

    }

    //--------------------------------------------------------------------------
    /**


       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void backup(BusIfc bus)
    {

    }
}
