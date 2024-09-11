/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/resume/LookupTransactionsNotFoundAisle.java /main/11 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:59 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:12  mcs
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
 *    Rev 1.0   Aug 29 2003 16:02:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   10 May 2002 10:44:14   vxs
 * Added model.setButtonLetter(0, CommonLetterIfc.FAILURE);
 *
 *    Rev 1.0   Apr 29 2002 15:15:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.resume;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Aisle to traverse if there are no suspended transactions.
 * 
 * @version $Revision: /main/11 $
 */
@SuppressWarnings("serial")
public class LookupTransactionsNotFoundAisle extends PosLaneActionAdapter
{
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * Display an error message, wait for user acknowledgment.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();

        // Set model to same name as dialog in config\posUI.properties
        model.setResourceID("SuspendTransactionNotFound");
        model.setButtonLetter(0, CommonLetterIfc.FAILURE);
        model.setType(DialogScreensIfc.ERROR);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}