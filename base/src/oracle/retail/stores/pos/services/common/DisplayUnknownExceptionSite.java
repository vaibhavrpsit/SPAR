/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/DisplayUnknownExceptionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/26/10 - set letter to UNKNOWN_EXCEPTION
 *    cgreene   03/24/10 - initial version
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Displays the "An unknown exception occurred." dialog error screen.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class DisplayUnknownExceptionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6796178501472631033L;
    /**
     * class name constant
     */
    public static final String SITENAME = "DisplayUnknownExceptionSite";
    /**
     * class name constant
     */
    public static final String DIALOGTEXT_KEY = "UnexpectedExceptionOccurred";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(DIALOGTEXT_KEY);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNKNOWN_EXCEPTION);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }
}
