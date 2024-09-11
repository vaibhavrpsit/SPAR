/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/PrintReceiptSite.java /main/14 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    djenning  07/06/09 - do not reuse the report object as the report object
 *                         is caching some data.
 *    acadar    03/12/09 - display till float report
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

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
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Prints receipt upon closing the till.
 *
 * @version $Revision: /main/14 $
 */
public class PrintReceiptSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 4800665455007847468L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    public static final String SITENAME = "PrintReceiptSite";

    /**
     * Collects the Till count or Float count then calls printReport to print
     * the count(s). Displays a screen that printing is occurring. Catches
     * device exceptions and displays a dialog to allow the user to Retry or
     * Cancel. If parameters TillCountFloatAtClose AND TillCountTillAtClose are
     * set to No, then do not count the till nor float, otherwise count till or
     * float based upon each parameter's value (Summary/Detail).
     *
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        boolean mailLetter = true;
        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        int floatCountType = cargo.getFloatCountType();
        int tillCountType = cargo.getTillCountType();

        POSBaseBeanModel pbbModel = new POSBaseBeanModel();
        PromptAndResponseModel pnrModel = new PromptAndResponseModel();
        StatusBeanModel sbModel = new StatusBeanModel();

        if ((floatCountType != FinancialCountIfc.COUNT_TYPE_NONE)
                || (tillCountType != FinancialCountIfc.COUNT_TYPE_NONE))
        {
            pnrModel.setArguments("");
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
            pbbModel.setPromptAndResponseModel(pnrModel);
            pbbModel.setStatusBeanModel(sbModel);
            ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, pbbModel);
        }

        // Get financial count
        FinancialTotalsIfc fc = cargo.getRegister().getTillByID(cargo.getTillID()).getTotals();

        try
        {
            PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)Gateway.getDispatcher().getManager(
                    PrintableDocumentManagerIfc.TYPE);

            // some info is being cached in the report, so create a seperate report for each invocation
            if (floatCountType != FinancialCountIfc.COUNT_TYPE_NONE)
            {
                TillCountReport report = (TillCountReport)pdm.getParameterBeanInstance(ReportTypeConstantsIfc.TILLCOUNT_REPORT);
                report.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
                report.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
                report.setTillID(cargo.getTillID());
                report.setCashierID(cargo.getOperator().getEmployeeID());

            	FinancialCountIfc endFloat = fc.getEndingFloatCount().getEntered();
                report.setTillCount(endFloat);
                report.setTillCountType(TillCountReport.END_FLOAT);
                pdm.printReceipt((SessionBusIfc)bus, report);
            }

            if (tillCountType != FinancialCountIfc.COUNT_TYPE_NONE)
            {
                TillCountReport report = (TillCountReport)pdm.getParameterBeanInstance(ReportTypeConstantsIfc.TILLCOUNT_REPORT);
                report.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
                report.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
                report.setTillID(cargo.getTillID());
                report.setCashierID(cargo.getOperator().getEmployeeID());

                FinancialCountIfc enter = fc.getCombinedCount().getEntered();
                report.setTillCount(enter);
                report.setTillCountType(TillCountReport.CLOSE);
                pdm.printReceipt((SessionBusIfc)bus, report);
            }


        }
        catch (PrintableDocumentException e)
        {

            // Update printer status
            logger.error("Unable to print TillCountReport: " + e.getNestedException());
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            mailLetter = false;

            String msg[] = new String[1];

            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

            msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                    BundleConstantsIfc.PRINTER_OFFLINE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("RetryContinue");
            model.setType(DialogScreensIfc.RETRY_CONTINUE);
            model.setArgs(msg);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }

        if (mailLetter)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }
}