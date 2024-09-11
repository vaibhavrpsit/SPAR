/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/PrintReportsSite.java /main/12 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusAndTotalsIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.EYSDate;
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
 * @version $Revision: /main/12 $
 */
public class PrintReportsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3258791463038492803L;

    public static final String SITENAME = "PrintReportsSite";

    /**
     * end of day cargo
     */
    protected EndOfDayCargo cargo = null;

    /**
     * POS ui manager interface
     */
    protected POSUIManagerIfc ui = null;

    /**
     * bus interface
     */
    protected BusIfc bus = null;

    /**
     * store interface
     */
    protected StoreIfc store = null;

    /**
     * displayed error string
     */
    protected String errorString[] = new String[2];

    /**
     * dialog bean model
     */
    protected DialogBeanModel model = new DialogBeanModel();

    /**
     * financial totals
     */
    protected FinancialTotalsDataTransaction ftdt = null;

    protected SummaryReport report;

    /**
     * business date
     */
    protected EYSDate businessDate = null;

    /**
     * store data tracation
     */
    protected StoreDataTransaction sdt = null;

    /**
     * operator employee
     */
    protected EmployeeIfc emp = null;

    /**
     * register
     */
    protected RegisterIfc register;

    /**
     * prompt argument tag
     */
    protected static final String PROMPT_ARGUMENT_TAG = "EndOfDayReportPrintingPromptArgument";

    /**
     * prompt argument
     */
    protected static final String PROMPT_ARGUMENT = "Business day has successfully closed.";

    /**
     * Automatic Report Database Error System Offline tag
     */
    public static final String AUTOMATIC_REPORT_DBERR_SYSOFFLINE_TAG = "AutomaticReportDatabaseError.SystemOffline";

    /**
     * Automatic Report Database Error SystemOffline default text
     */
    public static final String AUTOMATIC_REPORT_DBERR_SYSOFFLINE_TEXT = "The system is offline to the register data.";

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        sdt = (StoreDataTransaction)DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);
        ftdt = (FinancialTotalsDataTransaction)DataTransactionFactory
                .create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

        cargo = (EndOfDayCargo)bus.getCargo();
        store = cargo.getStoreStatus().getStore();
        businessDate = cargo.getStoreStatus().getBusinessDate();
        register = cargo.getRegister();
        emp = cargo.getOperator();
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        PromptAndResponseModel pandrModel = new PromptAndResponseModel();
        pandrModel.setArguments(utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME, PROMPT_ARGUMENT_TAG, PROMPT_ARGUMENT));
        baseModel.setPromptAndResponseModel(pandrModel);
        StatusBeanModel statusModel = new StatusBeanModel();
        baseModel.setStatusBeanModel(statusModel);
        ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, baseModel);

        PrintableDocumentManagerIfc pdm = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
        report = (SummaryReport)pdm.getParameterBeanInstance(ReportTypeConstantsIfc.SUMMARY_REPORT);
        if (getTotals())
        {
            try
            {
                pdm.printReceipt((SessionBusIfc)bus, report);
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            }
            catch (PrintableDocumentException e)
            {
                // Update printer status
                logger.error("Unable to print report.", e);
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

                String msg[] = new String[1];
                msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                        BundleConstantsIfc.PRINTER_OFFLINE);

                model = new DialogBeanModel();
                model.setResourceID("RetryContinue");
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setArgs(msg);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }

        }
    }

    /**
     * Retrieves the financial totals for the entered business day. Logs
     * database errors.
     */
    protected boolean getTotals()
    {
        boolean flag = true;

        try
        {
            StoreStatusIfc storeStat = sdt.readStoreStatus(store.getStoreID());
            FinancialTotalsIfc storeTotals = sdt.readStoreTotals(store, businessDate);
            StoreStatusAndTotalsIfc sst = instantiateStoreStatusAndTotals();
            sst.copyStoreStatus(storeStat);
            sst.setTotals(storeTotals);
            report.setFinancialEntity(sst);
            report.setStartDate(businessDate);
            report.setStoreID(store.getStoreID());
            report.setRegisterID(register.getWorkstation().getWorkstationID());
            if (cargo.getOperator() != null)
            {
                report.setCashierID(cargo.getOperator().getEmployeeID());
            }
            report.setTrainingMode(register.getWorkstation().isTrainingMode());
        }
        catch (DataException exception)
        {
            logger.error("" + exception + "");
            flag = false;

            if (exception.getErrorCode() != DataException.NO_DATA)
            {
                UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(
                        UtilityManagerIfc.TYPE);
                errorString[0] = utility.retrieveDialogText(AUTOMATIC_REPORT_DBERR_SYSOFFLINE_TAG,
                        AUTOMATIC_REPORT_DBERR_SYSOFFLINE_TEXT);
                showDialogScreen(errorString, "AutomaticReportDatabaseError");
            }
        }
        return (flag);
    }

    /**
     * Displays an error screen with the appropriate error message.
     */
    protected void showDialogScreen(String[] args, String id)
    {
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);

        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }

    /**
     * Returns a new StoreStatusAndTotalsIfc objec.
     * 
     * @return StoreStatusAndTotalsIfc
     * @deprecated As of release 4.5.0, use #instantiateStoreStatusAndTotals()
     */
    protected StoreStatusAndTotalsIfc instanciateStoreStatusAndTotals()
    {
        return (instantiateStoreStatusAndTotals());
    }

    /**
     * Returns a new StoreStatusAndTotalsIfc objec.
     * 
     * @return StoreStatusAndTotalsIfc
     */
    protected StoreStatusAndTotalsIfc instantiateStoreStatusAndTotals()
    {
        return (DomainGateway.getFactory().getStoreStatusAndTotalsInstance());
    }

    /**
     * Returns a string representation of the object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = "Class:  PrintReportsSite (Revision " + getRevisionNumber() + ")" + hashCode();
        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
