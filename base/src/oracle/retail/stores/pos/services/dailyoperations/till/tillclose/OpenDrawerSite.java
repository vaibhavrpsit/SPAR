/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillclose/OpenDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
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
 *   Revision 1.3  2004/02/12 16:49:57  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 04 2003 14:16:44   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 23 2002 08:42:08   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:28:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:28:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillclose;

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
