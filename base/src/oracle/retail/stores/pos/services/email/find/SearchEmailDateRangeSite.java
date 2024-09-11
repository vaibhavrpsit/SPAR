/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/SearchEmailDateRangeSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:06 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:48:23  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 11:17:30   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;

//------------------------------------------------------------------------------
/**
    This site is used to select a date range to search for emails.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SearchEmailDateRangeSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "SearchEmailDateRangeSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //--------------------------------------------------------------------------
    /**
       Display a date range to search for emails.
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.
            getManager(UIManagerIfc.TYPE);

        ui.showScreen(POSUIManagerIfc.NARROW_SEARCH, new DateSearchBeanModel());
    }

    //--------------------------------------------------------------------------
    /**
       Saves the valid date range values for searching for orders.

       @param bus the bus departing from this site
    **/
    //--------------------------------------------------------------------------

    public void depart(BusIfc bus)
    {
        EmailCargo      cargo   = (EmailCargo)bus.getCargo();

        //retrieve the date range from the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DateSearchBeanModel model = (DateSearchBeanModel)ui.getModel(POSUIManagerIfc.NARROW_SEARCH);

        cargo.setStartDate(model.getUpdatedStartDate());  // at 00:00:00
        cargo.setEndDate(model.getUpdatedEndDate());      // at 23:59:59
        cargo.setDateRange(true);

    }
}
