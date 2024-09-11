/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/lookup/NarrowSearchSite.java /main/11 2012/07/13 12:43:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:07 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:44 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:24  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:12:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 13:01:12   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.lookup;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the narrow search screen if not performing a status based search
    for orders.
    <P>
    <P>
    @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------

public class NarrowSearchSite extends PosSiteActionAdapter
{

    /**
       class name constant
    **/
    public static final String SITENAME = "NarrowSearchSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    //--------------------------------------------------------------------------
    /**
       Determines whether to show the Narrow Search screen based upon the search
       method.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.NARROW_SEARCH, new DateSearchBeanModel());
    }

    //--------------------------------------------------------------------------
    /**
       Saves the valid date range values for searching for orders.
       <P>
       @param bus the bus departing from this site
    **/
    //--------------------------------------------------------------------------

    public void depart(BusIfc bus)
    {

        OrderSearchCargoIfc      cargo   = (OrderSearchCargoIfc)bus.getCargo();

        //retrieve the date range from the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DateSearchBeanModel model = (DateSearchBeanModel)ui.getModel(POSUIManagerIfc.NARROW_SEARCH);

        // set the order cargo data
        cargo.setStartDate(model.getUpdatedStartDate()); // at 00:00:00
        cargo.setEndDate(model.getUpdatedEndDate());     // at 23:59:59
        cargo.setDateRange(true);

    }
}
