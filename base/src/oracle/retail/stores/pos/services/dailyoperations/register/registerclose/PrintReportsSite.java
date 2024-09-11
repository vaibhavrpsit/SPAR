/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registerclose/PrintReportsSite.java /main/16 2013/11/08 11:24:16 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   11/08/13 - vat-enabled not being set. close register report
 *                         including sales-tax lines.
 *    rabhawsa  06/25/12 - wptg- merged keys for values 'Register' 'has
 *                         successfully closed'
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    blarsen   03/26/09 - Updated to use receipt builder framework.
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/16/2008 4:43:20 AM   Manas Sahu      The
 *         SummaryReport instance was directly created instead of using
 *         PrintableDocumentManager. So now creating the instance of
 *         SummaryReport from PrintableDocumentManager which will check if the
 *          system is VAT enabled and then create instance of
 *         VATSummaryReport. Code Reviewed by Vivekanand Kini
 *    3    360Commerce 1.2         3/31/2005 4:29:31 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:27 PM  Robert Pearse
 *
 *   Revision 1.10  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.9  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.8  2004/07/18 18:47:35  cdb
 *   @scr 1850 Removed dialog incorrectly added based on SCR inconsistent with requirements.
 *
 *   Revision 1.7  2004/07/12 20:47:07  jeffp
 *   @scr 5147 put current thread to sleep once once ui screen was shown
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:13:10  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:49:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:57:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Mar 04 2003 14:08:50   RSachdeva
 * string constant  for automatic report database error system offline tag and default text
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Feb 20 2003 15:09:16   RSachdeva
 * Clean Up for Code Conversion as per Coding Standards
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 30 2002 09:30:54   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 19 2002 14:15:14   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:29:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:14:58   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:27:38   msg
 * Initial revision.
 *
 *    Rev 1.3   12 Mar 2002 16:52:44   pdd
 * Modified to use the factory.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.2   Mar 10 2002 18:00:10   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.1   Mar 09 2002 16:42:30   mpm
 * Text externalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:17:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:00   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registerclose;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
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
 * @version $Revision: /main/16 $
 */
public class PrintReportsSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4961628757633499761L;

    public static final String SITENAME = "PrintReportsSite";

    /**
     * close argument text tag
     */
    protected static String REGISTER_CLOSE_TEXT_TAG = "RegisterClosePromptArgumentText";

    /**
     * close argument text
     */
    protected static String REGISTER_CLOSE_TEXT = "Register has successfully closed";

    /**
      automatic report database error system offline tag
    **/
    public static final String AUTO_REPORT_DBERR_SYSOFF_TAG = "AutomaticReportDatabaseError.SystemOffline";
    /**
      automatic report database error system offline default text
    **/
    public static final String AUTO_REPORT_DBERR_SYSOFF_TEXT =
      "The system is offline to the register data.";

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        RegisterCloseCargo cargo = (RegisterCloseCargo) bus.getCargo();
        StoreIfc           store = cargo.getStoreStatus().getStore();
        RegisterIfc     register = cargo.getRegister();
        EYSDate     businessDate = cargo.getStoreStatus().getBusinessDate();
        EmployeeIfc          emp = cargo.getOperator();
        POSUIManagerIfc       ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        PrintableDocumentManagerIfc printableDocumentManager = (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
        SummaryReport report = (SummaryReport)printableDocumentManager.getParameterBeanInstance(ReportTypeConstantsIfc.SUMMARY_REPORT);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        FinancialTotalsDataTransaction ftdt = null;

        ftdt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);

        boolean success = getData(ftdt, register, store, businessDate, report, emp, ui, bus);

        // if there is no data, no need to go on
        if (success)
        {
            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            PromptAndResponseModel pandrModel = new PromptAndResponseModel();
            String registerCloseText =
              utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                                   BundleConstantsIfc.DAILY_OPERATIONS_BUNDLE_NAME,
                                   REGISTER_CLOSE_TEXT_TAG,
                                   REGISTER_CLOSE_TEXT);

            pandrModel.setArguments(registerCloseText);
            baseModel.setPromptAndResponseModel(pandrModel);
            StatusBeanModel statusModel = new StatusBeanModel();
            baseModel.setStatusBeanModel(statusModel);
            ui.showScreen(POSUIManagerIfc.REPORT_PRINTING, baseModel);

            // Allows UI screen above to stay visible for longer time
            // This exists because the screen disappears to fast, but
            // if this becomes the source of a performance defect and
            // it is deemed necessary to remove, provide some other mechanism
            // to ensure the screen is displayed for a sufficient length of
            // time for viewing.
            try
            {
                Thread.sleep(4000);
            }
            catch (InterruptedException ie)
            {
                logger.error("Interrupted Exception,", ie);
            }

            try
            {
                // Print report
                printableDocumentManager.printReceipt((SessionBusIfc)bus, report);
                boolean isFiscalPrintingEnabled = Gateway.getBooleanProperty("application", "FiscalPrintingEnabled", false);

                // Print Z report to end the day for fiscal printer
                if (isFiscalPrintingEnabled)
                {
                    POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
                    pda.printFiscalZReport();
                }



                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
                bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
            }
            catch (PrintableDocumentException e)
            {
                logger.error("Unable to print summary report", e.getNestedException());

                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

                String msg[] = new String[1];
                msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                                                    BundleConstantsIfc.PRINTER_OFFLINE);

                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("RetryContinue");
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setArgs(msg);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            catch (DeviceException e)
            {
                logger.error("Unable to print fiscal printer Z report", e);
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

                String msg[] = new String[1];
                msg[0] = utility.retrieveDialogText(BundleConstantsIfc.PRINTER_OFFLINE_TAG,
                                                    BundleConstantsIfc.PRINTER_OFFLINE);

                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID("RetryContinue");
                model.setType(DialogScreensIfc.RETRY_CONTINUE);
                model.setArgs(msg);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
        }   // end if getData()
    }

    /**
     * Retrieves the summary report data based upon the user inputs.
     *
     * @param ftdt the transaction to hold the data
     * @param register the register
     * @param store the current store object
     * @param businessDate current business date
     * @param report the summary report.
     * @param emp the cashier.
     * @param ui the ui manager
     * @return true if retrieve succeeds.
     */
    protected boolean getData(FinancialTotalsDataTransaction ftdt, RegisterIfc register,
                              StoreIfc store, EYSDate businessDate, SummaryReport report,
                              EmployeeIfc emp, POSUIManagerIfc ui, BusIfc bus)
    {
        boolean flag = true;
        try
        {
            WorkstationIfc workStation = DomainGateway.getFactory().getWorkstationInstance();
            workStation.setWorkstationID(register.getWorkstation().getWorkstationID());
            workStation.setStore(store);
            ftdt.readRegisterStatus(workStation);
            getTotals(ftdt, register, store, businessDate, report, emp);
        }
        catch (DataException exception)
        {
            flag = false;
            logger.error( "" + exception + "");
            if(exception.getErrorCode() != DataException.NO_DATA)
            {
                String[] errorString = new String[1];
                UtilityManagerIfc utility =
                  (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                errorString[0] =
                  utility.retrieveDialogText(AUTO_REPORT_DBERR_SYSOFF_TAG,
                                             AUTO_REPORT_DBERR_SYSOFF_TEXT);
                showDialogScreen(ui, errorString, "AutomaticReportDatabaseError");
            }
        }
        return(flag);
    }

    /**
     * Retrieves the financial totals for the entered business day. Logs
     * database errors.
     *
     * @param ftdt the transaction to hold the data
     * @param register the register
     * @param store the current store object
     * @param businessDate current business date
     * @param report the summary report.
     * @param emp the cashier.
     */
    protected void getTotals(FinancialTotalsDataTransaction ftdt, RegisterIfc register,
                             StoreIfc store, EYSDate businessDate, SummaryReport report,
                             EmployeeIfc emp) throws DataException
    {
        RegisterIfc[] registers = ftdt.readRegister(store.getStoreID(),
                                                    register.getWorkstation().getWorkstationID(), businessDate);

        report.setFinancialEntity(registers[0]);

        //update header info
        report.setStartDate(businessDate);
        report.setStoreID(store.getStoreID());
        report.setRegisterID(register.getWorkstation().getWorkstationID());
        report.getFinancialEntity().getTotals().setVatEnabled(isVATEnabled());

        if (emp != null)
        {
            report.setCashierID(emp.getEmployeeID());
        }
        report.setTrainingMode(register.getWorkstation().isTrainingMode());
    }

    /**
     * Displays an error screen with the appropriate error message.
     */
    protected void showDialogScreen(POSUIManagerIfc ui, String[] args, String id)
    {

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return String representation of object
     */
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  PrintReportsSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()


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
