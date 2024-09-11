/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/DisplayNoMatchSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:04 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 15:58:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:24:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 24 2001 11:17:32   MPM
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Displays the Info Not Found screen.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class DisplayNoMatchSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 55480822373262336L;

    /**
     * class name constant
     **/
    public static final String LANENAME = "DisplayNoMatchSite";

    /**
     * revision number for this class
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Display the Info Not Found message, wait for user acknowlegement.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        EmailCargo cargo = (EmailCargo)bus.getCargo();
        cargo.setDateRange(false); // do not use date range for the next search

        // Display an error message, wait for user acknowlegement.

        // get the POS UI manager
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // show the screen
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("INFO_NOT_FOUND_ERROR");
        dialogModel.setType(DialogScreensIfc.ERROR);
        uiManager.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);

        // display dialog
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);

    } // arrive

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  DisplayNoMatchSite (Revision " + 
                getRevisionNumber() + ")" + hashCode());
        return (strResult);

    }
}