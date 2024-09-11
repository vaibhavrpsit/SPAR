/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/NotFoundErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - switch UI call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:46 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 20:13:10  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:36   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:34:50   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:09:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:23:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:13:10   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:12   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Displays the info not found error message, waits for user acknowlegement.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class NotFoundErrorAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = -3944658013897379865L;

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Displays the info not found error message, waits for user acknowlegement.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // show the screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
        dialogModel.setType(DialogScreensIfc.ERROR);
        uiManager.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = Util.classToStringHeader("NotFoundErrorAisle", 
                revisionNumber, hashCode()).toString();
        // pass back result
        return (strResult);
    }
}