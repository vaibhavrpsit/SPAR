/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/cancel/CancelOrdersNextAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:34 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:58 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:46 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:21  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:13:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 13:00:12   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.cancel;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CancelOrdersNextAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 7156831685510865345L;

    /**
     * revision number supplied by source-code control system
     **/
    public static final String revisionNumber = "$KW=@(#); $Ver=rapp.vtg_2.5:5; $EKW";

    /**
     * lane name constant
     **/
    public static final String LANENAME = "CancelOrdersNextAisle";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog
        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("ConfirmCancel");
        model.setType(DialogScreensIfc.CONFIRMATION);

        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    } 

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#toString()
     */
    @Override
    public String toString()
    {
        // build result string
        StringBuffer strResult = new StringBuffer();

        strResult.append("Class:  CancelOrdersNextAisle (Revision ");
        strResult.append(getRevisionNumber());
        strResult.append(") @");
        strResult.append(hashCode());
        strResult.append("\n");

        // pass back result
        return (strResult.toString());
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

} // end class CancelOrdersNextAisle

