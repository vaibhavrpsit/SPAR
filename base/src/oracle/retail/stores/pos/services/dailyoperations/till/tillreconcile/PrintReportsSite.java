/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/PrintReportsSite.java /rgbustores_13.4x_generic_branch/1 2011/11/08 14:51:54 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  11/08/11 - Set VAT enabled flag to totals
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    blarsen   03/26/09 - Removing dead code. (noticed in a code review of a
 *                         sister PrintReportsSite)
 *    atirkey   01/07/09 - Till reconcile
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.data.DataException;
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
import oracle.retail.stores.pos.reports.SummaryReport;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Print the Close Till Summary Report.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class PrintReportsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 680551604557747839L;

    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * message text tag
     */
    public static final String CLOSE_SUCCESS_MESSAGE_TAG = "CloseSuccessMsg";

    /**
     * default message pattern
     */
    public static final String CLOSE_SUCCESS_MESSAGE = "Till {0} has successfully closed.";

    /**
     * Build the report and print it.
     * 
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        StoreIfc store = cargo.getStoreStatus().getStore();

        EYSDate businessDate = cargo.getStoreStatus().getBusinessDate();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        boolean mailLetter = true;

        RegisterIfc register = cargo.getRegister();
        String tillID = cargo.getTillID();

        POSBaseBeanModel pbbModel = new POSBaseBeanModel();
        PromptAndResponseModel pnrModel = new PromptAndResponseModel();
        StatusBeanModel sbModel = new StatusBeanModel();

        String[] vars = new String[1];
        vars[0] = tillID;
        String pattern = utility.retrieveText("Common", BundleConstantsIfc.TILL_BUNDLE_NAME, CLOSE_SUCCESS_MESSAGE_TAG,
                CLOSE_SUCCESS_MESSAGE);
        String message = LocaleUtilities.formatComplexMessage(pattern, vars);

        pnrModel.setArguments(message);
        sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        pbbModel.setPromptAndResponseModel(pnrModel);
        pbbModel.setStatusBeanModel(sbModel);
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, pbbModel);

        PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus
                .getManager(PrintableDocumentManagerIfc.TYPE);
        SummaryReport report = (SummaryReport)pdm.getParameterBeanInstance(ReportTypeConstantsIfc.TILL_SUMMARY_REPORT);
        FinancialTotalsDataTransaction ftdt = null;

        ftdt = (FinancialTotalsDataTransaction)DataTransactionFactory
                .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

        try
        {
            // See if till exists
            ftdt.readTillStatus(store, tillID);

            // Since saving the till reconcile transaction is asynchronous it may not have
            // finished by this time. By trying again until the till status is "Reconciled"
            // we may be able to retrieve the fresh data. Giving it 3 tries to accomplish.
            TillIfc[] tills = null;
            for (int tries = 3; tries > 0; tries--)
            {
                tills = ftdt.readTillTotals(store.getStoreID(), tillID, businessDate);
                if (tills.length > 1)
                {
                    for (int cnt = 1; cnt < tills.length; cnt++)
                    {
                        tills[0].addTotals(tills[cnt].getTotals());
                    }
                }
                if (tills[0].getStatus() == AbstractFinancialEntityIfc.STATUS_RECONCILED)
                {
                    // break the loop
                    tries = 0;
                }
                else
                {
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException ie)
                    {
                        // eat the exception!
                    }
                }
            }
            report.setFinancialEntity(tills[0]);
        }
        catch (DataException exception) // revert to hard totals info
        {
            logger.error("" + exception + "");
            report.setFinancialEntity(register.getTillByID(tillID));
        }

        // Update header info
        report.setStartDate(businessDate);
        report.setStoreID(store.getStoreID());
        report.setRegisterID(register.getWorkstation().getWorkstationID());
        report.setCashierID(cargo.getOperator().getEmployeeID());
        report.getFinancialEntity().getTotals().setVatEnabled(isVATEnabled());  

        try
        {
            if (cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_NONE)
            {
                report.setCountTillAtClose(false);
            }
            pdm.printReceipt((SessionBusIfc)bus, report);
            report.setCountTillAtClose(true); // set right back to make sure
                                                // everything works ok
            // pda.cutPaper(97); // cut paper 97% of its width
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        }
        catch (PrintableDocumentException e)
        {
            // Update printer status
            logger.error("Unable to print summary report", e.getNestedException());
            sbModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            mailLetter = false;

            String msg[] = new String[1];

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
    
    /**
     * Returns VAT enabled flag
     * 
     * @return boolean
     */
    private boolean isVATEnabled()
    {
        return Gateway.getBooleanProperty("application", "InclusiveTaxEnabled", false);
    }
}