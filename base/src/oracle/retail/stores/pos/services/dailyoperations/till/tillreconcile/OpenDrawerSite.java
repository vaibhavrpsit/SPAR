/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/OpenDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:20 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:11 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:49 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

// foudation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

//------------------------------------------------------------------------------
/**
    Attempts to open the cash drawer to count the float by calling
    TillCashDrawer.tillOpenCashDrawer
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class OpenDrawerSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
      Cash Drawer Retry Continue Cancel close tag
    **/
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_CLOSE_TAG = 
      "CashDrawerRetryContinueCancel.close";
    /**
      Cash Drawer Retry Continue Cancel close default text
    **/
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_CLOSE_TEXT = 
      "the till close";
    //--------------------------------------------------------------------------
    /**
       openDrawerSite
    **/
    //--------------------------------------------------------------------------
    public static final String SITENAME = "OpenDrawerSite";

    //--------------------------------------------------------------------------
    /**
       Calls TillCashDrawer.tillOpenCashDrawer to open the cash drawer.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        // calls TillCashDrawer.tillOpenCashDrawer
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        TillCashDrawer.
          tillOpenCashDrawer(bus, utility.retrieveDialogText(CASH_DRAWER_RETRY_CONT_CANCEL_CLOSE_TAG,
                                                             CASH_DRAWER_RETRY_CONT_CANCEL_CLOSE_TEXT));
    }
}
