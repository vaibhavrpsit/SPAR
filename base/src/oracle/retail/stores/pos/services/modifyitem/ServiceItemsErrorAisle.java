/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ServiceItemsErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:25 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:10 PM  Robert Pearse   
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
 *    Rev 1.0   Apr 29 2002 15:17:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:37:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:28:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This aisle is traversed if no service items are found.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ServiceItemsErrorAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7274483154188932062L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * resource id constant
     */
    public static final String RESOURCE_ID = "ItemsNotFound";

    /**
     * Displays an error message.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog in config\posUI.properties
        // Set button and arguments
        model.setResourceID(RESOURCE_ID);
        model.setType(DialogScreensIfc.ERROR);

        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class:  PriceEnteredAisle (Revision " + 
                getRevisionNumber() + ")" + hashCode());
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}