/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/CloseDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:02 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

//------------------------------------------------------------------------------
/**
    Attempts to close the cash drawer after counting the float by calling
    TillCashDrawer.tillCloseCashDrawer
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see TillCashDrawer
**/
//------------------------------------------------------------------------------
public class CloseDrawerSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       CloseDrawerSite
    **/
    //--------------------------------------------------------------------------
    public static final String SITENAME = "CloseDrawerSite";

    //--------------------------------------------------------------------------
    /**
       Calls TillCashDrawer.tillCloseCashDrawer to close the cash drawer.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // calls TillCashDrawer.tillCloseCashDrawer
        TillCashDrawer.tillCloseCashDrawer(bus);

    }
}
