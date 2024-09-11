/* ===========================================================================
 * Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/SelectServiceItemSite.java /main/11 2012/09/26 17:43:42 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/25/12 - Modified to support retrieval of the list of Service
 *                         (non-merchandise) items.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:10 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:03  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:17:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:29:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ServiceItemListBeanModel;

/**
 * This site displays the NON_MERCHANDISE screen.
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class SelectServiceItemSite extends PosSiteActionAdapter
{
    /**
     * revision number of this class
     **/
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Displays the NON_MERCHANDISE screen.
     * 
     * @param bus Service Bus
     */
    public void arrive(BusIfc bus)
    {
        // grab the transaction (if it exists)
        ItemCargo cargo = (ItemCargo)bus.getCargo();

        /*
         * Setup bean model information for the UI to display
         */
        ServiceItemListBeanModel beanModel = new ServiceItemListBeanModel();
        beanModel.setServiceItems(cargo.getServiceItems().getReturnItems());

        /*
         * Display the screen
         */
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.NON_MERCHANDISE, beanModel);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  SelectServiceItemSite (Revision " + getRevisionNumber() + ")"
                + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    @Override
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
