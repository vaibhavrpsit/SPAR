/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/DisplayOrderSearchOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:34 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:12:50   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 09 2002 13:29:22   dfh
 * updates to clear the customer name from the status area
 * and general cleanup
 * Resolution for POS SCR-177: CR/Order, linked customer remains displayed after Esc/Cancel
 * 
 *    Rev 1.0   Sep 24 2001 13:01:02   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

//foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the ORDER_SEARCH_OPTIONS screen.  This site is used by
    the Pickup and Cancel Order services. Removes the customer name from 
    the screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class DisplayOrderSearchOptionsSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "DisplayOrderSearchOptionsSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Displays the Order Search Options screen and removes the customer name
       from the status area.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        OrderCargo      cargo   = (OrderCargo) bus.getCargo();
        POSUIManagerIfc ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model  = new POSBaseBeanModel();

        // clear the customer's name in the status area
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setCustomerName("");
        model.setStatusBeanModel(statusModel);

        ui.showScreen(POSUIManagerIfc.ORDER_SEARCH_OPTIONS, model);
    }
}




