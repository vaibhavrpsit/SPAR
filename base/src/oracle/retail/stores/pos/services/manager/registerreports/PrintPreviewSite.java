/*===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/PrintPreviewSite.java /rgbustores_13.4x_generic_branch/2 2011/03/29 14:20:35 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     03/29/11 - Checking in unchanged file
* vtemker     03/22/11 - Code reviewed and checked in 
* vtemker     03/22/11 - Create
* vtemker     03/22/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.manager.registerreports;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.PrintPreviewBeanModel;

public class PrintPreviewSite extends PosSiteActionAdapter
{

    /**
	 * 
	 */
    public void arrive(BusIfc bus)
    {
        RegisterReportsCargoIfc cargo = (RegisterReportsCargoIfc)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        RegisterReport report = cargo.getReport();
        report.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());

        try
        {
            PrintableDocumentManagerIfc printMgr = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                    PrintableDocumentManagerIfc.TYPE);
            String previewText = printMgr.getPreview((SessionBusIfc)bus, report);

            PrintPreviewBeanModel previewModel = new PrintPreviewBeanModel();
            previewModel.setPrintPreviewText(previewText);
            ui.showScreen(POSUIManagerIfc.PRINT_PREVIEW, previewModel);
        }
        catch (PrintableDocumentException pde)
        {
            logger.error("Error Previewing register report", pde.getNestedException());

            DialogBeanModel model = new DialogBeanModel();

            String msg[] = new String[1];
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                    BundleConstantsIfc.PRINTER_OFFLINE);

            model.setResourceID("RetryCancel");
            model.setType(DialogScreensIfc.RETRY_CANCEL);
            model.setArgs(msg);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

    }

}