/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/reprintreceipt/NotReprintableErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:10 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:44 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:47 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:51:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:07:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:46   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.reprintreceipt;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Displays the not-reprintable error message, waits for user acknowlegement.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class NotReprintableErrorAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = -9124494619726143809L;

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:8; $EKW:";

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

        // Set the correct argument, getting it from the cargo
        String args[] = new String[1];
        ReprintReceiptCargo cargo = (ReprintReceiptCargo)bus.getCargo();
        args[0] = cargo.nonReprintableErrorCodeToString();

        // show the screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("TransactionNotReprintableError");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(args);
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
        String strResult = Util.classToStringHeader("NotReprintableErrorAisle", revisionNumber, hashCode()).toString();
        // pass back result
        return (strResult);
    }
}