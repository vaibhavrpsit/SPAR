/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/OpenDrawerSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
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
 *    1    360Commerce 1.0         2/11/2005 12:12:50 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/29 20:06:21  dcobb
 *   @scr 4098 Open Drawer before detail count screens.
 *   Pickup changed to open drawer before detail count screens.
 *
 *   Revision 1.3  2004/02/12 16:50:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:43  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 10 2004 14:29:36   DCobb
 * Open the cash drawer after a successful count.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 * 
 *    Rev 1.0   Aug 29 2003 15:58:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Mar 04 2003 16:53:36   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 27 2002 14:58:24   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   05 Jun 2002 22:01:54   baa
 * support for  opendrawerfortrainingmode parameter
 * Resolution for POS SCR-1645: Training Mode Enhancements
 *
 *    Rev 1.0   Apr 29 2002 15:26:08   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:30   msg
 * Initial revision.
 *
 *    Rev 1.2   21 Nov 2001 14:27:46   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   20 Nov 2001 16:18:56   epd
 * Creating transaction up front
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:19:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:15:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillCashDrawer;

//------------------------------------------------------------------------------
/**
    Attempts to open the cash drawer by calling
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
       site name
    **/
    public static final String SITENAME = "OpenDrawerSite";
    /**
      Cash Drawer Retry Continue Cancel pickup tag
    **/
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_PICKUP_TAG = 
      "CashDrawerRetryContinueCancel.pickup";
    /**
      Cash Drawer Retry Continue Cancel pickup default text
    **/
    public static final String CASH_DRAWER_RETRY_CONT_CANCEL_PICKUP_TEXT = 
      "the till pickup";

    //--------------------------------------------------------------------------
    /**
       Calls TillCashDrawer.tillOpenCashDrawer to open the cash drawer.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        TillCashDrawer.
          tillOpenCashDrawer(bus,utility.retrieveDialogText(CASH_DRAWER_RETRY_CONT_CANCEL_PICKUP_TAG,
                                                            CASH_DRAWER_RETRY_CONT_CANCEL_PICKUP_TEXT));
    }

}
