/* ===========================================================================
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/ImportDataSite.java /rgbustores_13.4x_generic_branch/2 2011/09/29 09:38:00 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.DataSetManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site displays the application main menu.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
@SuppressWarnings("serial")
public class ImportDataSite extends PosSiteActionAdapter
{

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ImportDataSite.class);

    /**
     * Retrieve dataset updates from the store server if any are ready.
     * 
     * @param bus Service Bus
     * @return void
     */
    @Override
    public void arrive(BusIfc bus)
    {
        boolean updatesAvailable;

        // Get the ui manager from the bus.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // Get the dataset manager from the bus.
        DataSetManagerIfc dsman = (DataSetManagerIfc)bus.getManager(DataSetManagerIfc.TYPE);

        // Check if any datasets are available for import
        updatesAvailable = dsman.isAnyDataSetAvailable();

        // If at least one dataset is available for import into the local db
        if (updatesAvailable == true)
        {
            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("DatabaseUpdate");
            model.setType(DialogScreensIfc.NO_RESPONSE);
            ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);

            // Consume all available datasets
            dsman.consumeAllDataSets();
        }

        // After datasets are imported, return to main menu.
        bus.mail(CommonLetterIfc.UNDO);
    }
}
