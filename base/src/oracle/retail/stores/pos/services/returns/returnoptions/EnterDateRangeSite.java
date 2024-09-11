/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/EnterDateRangeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/06/09 19:24:04  aschenk
 *   @scr 5437 - Changed the End Date field to not have a default.
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 18 2003 15:12:46   RSachdeva
 * Setting default Values for date fields 
 * Resolution for POS SCR-2132: Invalidate purchase date range are accepted
 * Resolution for POS SCR-2133: Purchase Date screen, default "Purchase Date To" is not current business date
 * Resolution for POS SCR-2144: At Purchase Date screen, default Purchase Date From need be none
 * 
 *    Rev 1.0   Apr 29 2002 15:04:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:16   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;
//------------------------------------------------------------------------------
/**
        Displays screen for entering the date range.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EnterDateRangeSite extends PosSiteActionAdapter
{                                                                               // begin class EnterDateRangeSite

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       site name constant
    **/
    public static final String SITENAME = "EnterDateRangeSite";

    //--------------------------------------------------------------------------
    /**
       Displays screen for entering the date range.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                                                   // begin arrive()
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DateSearchBeanModel model = new DateSearchBeanModel();
        
        model.setclearUIFields(false);
        
        //Setting Business Date for End Date(default value)
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        //return REQ, v8.doc states on page 64 that no return date should be displayed
        //model.setEndDate(cargo.getStoreStatus().getBusinessDate());
        
        // show the screen
        ui.showScreen(POSUIManagerIfc.PURCHASE_DATE, model);

    }                                                                   // end arrive()
}                                                                               // end class EnterDateRangeSite
