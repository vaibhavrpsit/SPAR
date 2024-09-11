/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/PrinterErrorOccurredAisle.java /rgbustores_13.4x_generic_branch/1 2011/06/03 09:46:42 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/02/11 - Tweaks to support Servebase chipnpin
 *    cgreene   06/01/11 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.LocalizedDeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Display a dialog that shows a printer error has occurred.
 *
 * @author cgreene
 * @since 13.4
 */
public class PrinterErrorOccurredAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 1146256229885027972L;

    public static final String LANENAME = "PrinterErrorOccurredAisle";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Update printer status
        StatusBeanModel statusModel = new StatusBeanModel();
        statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
        PrintingCargo cargo = (PrintingCargo)bus.getCargo();
        PrintableDocumentException e = cargo.getPrinterError();
        Throwable nested = e.getCause();

        if (nested != null)
        {
            logger.warn("NestedException:\n" + Util.throwableToString(nested));
        }

        String msg[] = new String[1];
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        if (nested instanceof LocalizedDeviceException)
        {
            msg[0] = nested.getLocalizedMessage();
        }
        else if (nested instanceof DeviceException && ((DeviceException)nested).getErrorCode() != DeviceException.UNKNOWN)
        {
            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                    BundleConstantsIfc.PRINTER_OFFLINE);
        }
        else
        {
            msg[0] = utility.retrieveDialogText("RetryContinue.UnknownPrintingError",
                    "An unknown error occurred while printing.");
        }

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("RetryContinue");
        model.setType(DialogScreensIfc.RETRY_CONTINUE);
        model.setArgs(msg);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
