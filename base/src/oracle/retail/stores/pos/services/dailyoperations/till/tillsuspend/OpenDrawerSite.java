/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillsuspend/OpenDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
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
 *   Revision 1.3  2004/02/12 16:50:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:03  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:40   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 04 2003 17:25:20   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 27 2002 14:48:54   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:25:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillsuspend;
// Bedrock imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

//------------------------------------------------------------------------------
/**
    Attempts to open the cash drawer to allow suspending of the Till by calling
    TillCashDrawer.tillOpenCashDrawer
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see TillCashDrawer
**/
//------------------------------------------------------------------------------

public class OpenDrawerSite extends PosSiteActionAdapter
{

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
      Cash Drawer Retry Continue Cancel suspend tag
    **/
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_SUSPEND_TAG = 
      "CashDrawerRetryContinueCancel.suspend";
    /**
      Cash Drawer Retry Continue Cancel suspend default text
    **/
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_SUSPEND_TEXT = 
      "the till suspend";

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
          tillOpenCashDrawer(bus,utility.retrieveDialogText(CASH_DRAWER_RETRY_CONT_CANCEL_SUSPEND_TAG,
                                                            CASH_DRAWER_RETRY_CONT_CANCEL_SUSPEND_TEXT));
    }
    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------

    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  OpenDrawerSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------

    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
