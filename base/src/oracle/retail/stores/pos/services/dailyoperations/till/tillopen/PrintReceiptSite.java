/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/PrintReceiptSite.java /main/14 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vikini    02/24/09 - getting float count for till summary
 *    vikini    02/11/09 - Display Till counts when opening till
 *    cgreene   11/13/08 - configure print beans into Spring context
 * 
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargo;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.reports.ReportTypeConstantsIfc;
import oracle.retail.stores.pos.reports.TillCountReport;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Prints receipt upon opening the till.
 * <P>
 * 
 * @version $Revision: /main/14 $
 */
public class PrintReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7071243731367678972L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Collects the float count then calls printReport to print the count.
     * Displays a screen that printing is occurring. Catches device exceptions
     * and displays a dialog to allow the user to Retry or Cancel. If parameter
     * TillCountFloatAtOpen is set to No then do not count the float when
     * openning a till, otherwise count the till based upon this parameter's
     * value (Summary/Detail).
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {

        TillOpenCargo cargo = (TillOpenCargo)bus.getCargo();
        boolean mailLetter = true;
        String sep = System.getProperty("line.separator");
        StringBuffer sepBuffer = new StringBuffer();
        for (int i = 0; i < 6; i++)
        {
            sepBuffer.append(sep);
        }

        if (cargo.getFloatCountType() != FinancialCountIfc.COUNT_TYPE_NONE)
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            PromptAndResponseModel pandrModel = new PromptAndResponseModel();
            pandrModel.setArguments("");
            baseModel.setPromptAndResponseModel(pandrModel);
            StatusBeanModel statusModel = new StatusBeanModel();
            statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            baseModel.setStatusBeanModel(statusModel);
            ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, baseModel);

            try
            {
                // Get financial count
                FinancialTotalsIfc fc = cargo.getRegister().getTillByID(cargo.getTillID()).getTotals();
                PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)
                    Gateway.getDispatcher().getManager(PrintableDocumentManagerIfc.TYPE);

                TillCountReport report = (TillCountReport)pdm.getParameterBeanInstance(ReportTypeConstantsIfc.TILLCOUNT_REPORT);

                report.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
                report.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
                report.setTillCountType(TillCountReport.START_FLOAT);
                report.setTillID(cargo.getTillID());
                report.setCashierID(cargo.getOperator().getEmployeeID());
                report.setTillCount(fc.getCombinedCount().getExpected());

                pdm.printReceipt((SessionBusIfc)bus, report);
            }
            catch (PrintableDocumentException e)
            {
                logger.error("Unable to print TillCountReport", e);
                mailLetter = false;

                // Update printer status
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

                String msg[] = new String[1];
                msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                        BundleConstantsIfc.PRINTER_OFFLINE);

                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("RetryContinue");
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setArgs(msg);
                model.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Success");
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
        }

        if (mailLetter)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}
