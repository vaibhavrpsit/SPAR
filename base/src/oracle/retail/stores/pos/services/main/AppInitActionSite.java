/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/AppInitActionSite.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.main.tdo.MainTDOIfc;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Calls the application initialization via the MainTDO helper class
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class AppInitActionSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    protected static final String TDO_MAIN_INIT = "tdo.main.init";

    /**
     * Obtains a reference to the MainTDOIfc helper. Calls the initApplication
     * method on the helper
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // letter to mail to next destination
        Letter letter = null;

        try
        {
            MainTDOIfc tdo = (MainTDOIfc)TDOFactory.create(TDO_MAIN_INIT);
            tdo.initApplication(bus);

            letter = new Letter(CommonLetterIfc.NEXT);
            bus.mail(letter, BusIfc.CURRENT);
        }
        catch (TDOException e)
        {
            displayErrorDialog(e, bus);
            letter = new Letter(CommonLetterIfc.OK);
        }
    } // end arrive

    private void displayErrorDialog(TDOException tdoe, BusIfc bus)
    {
        if ((tdoe.getErrorTextResourceName() == null) || (tdoe.getErrorTextDefault() == null))
        {
            logger.error(tdoe);
        }
        else if ("InitializationFailure".equals(tdoe.getErrorTextResourceName()))
        {
            // get reference to ui and bean model
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            String args[] = { tdoe.getErrorTextDefault() };
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID(tdoe.getErrorTextResourceName());
            model.setArgs(args);
            model.setType(DialogScreensIfc.ERROR);
            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            // show dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
        }
        else
        {
            // issue log message with text from original exception
            logger.error(tdoe);

            // get reference to ui and bean model
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

            // derive error string from exception object
            String errorString[] = new String[2]; // was 1

            String errorKey = tdoe.getErrorTextResourceName() + ".description";
            // errorString[0] =
            // utility.retrieveDialogText(tdoe.getErrorTextKey(),
            // tdoe.getErrorTextDefault() );
            errorString[0] = utility.retrieveDialogText(errorKey, tdoe.getErrorTextDefault());

            // set bean model
            DialogBeanModel model = new DialogBeanModel();

            model.setResourceID(tdoe.getErrorTextResourceName());
            model.setArgs(errorString);
            model.setType(DialogScreensIfc.ERROR);
            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            // show dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
        }
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
}