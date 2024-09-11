/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/PrintReportSite.java /main/12 2011/03/08 17:21:30 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/07/11 - Print Preview for Reports - fixed review comments
 *    abhayg    08/02/10 - REPORTS PRINTED IN TRAINING MODE SHOULD DISPLAY
 *                         TRAINING MODE
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
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
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site prints the report that is in the cargo.
 * 
 * @version $Revision: /main/12 $
 */
public class PrintReportSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 8422553200408373717L;

    public static final String SITENAME = "PrintReportSite";

    /**
     * line separator
     */
    public static final String LINE_SEPARATOR = "line.separator";

    /**
     * Get the report and print it.
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        RegisterReportsCargoIfc cargo = (RegisterReportsCargoIfc)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);

        boolean mailLetter = true;
        RegisterReport report = cargo.getReport();
        report.setTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());

        //Show the "Report Printing" screen
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        POSBaseBeanModel baseModel = new POSBaseBeanModel();

        parModel.setArguments("");
        baseModel.setPromptAndResponseModel(parModel);
        ui.showScreen(POSUIManagerIfc.PRINT_REPORT, baseModel);

        try
        {
            PrintableDocumentManagerIfc printMgr = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                    PrintableDocumentManagerIfc.TYPE);
            printMgr.printReceipt((SessionBusIfc)bus, report);
        }
        catch (PrintableDocumentException e)
        {
            // Update printer status
            logger.error("Error printing register report", e.getNestedException());
            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            mailLetter = false;
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

        if (mailLetter)
        {
            bus.mail(new Letter("PrintComplete"), BusIfc.CURRENT);
        }
    }
}
