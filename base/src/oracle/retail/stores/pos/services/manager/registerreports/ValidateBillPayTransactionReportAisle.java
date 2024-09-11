/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/ValidateBillPayTransactionReportAisle.java /main/5 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    vtemker   03/03/11 - Changes for Print Preview Reports Quickwin
 *    nkgautam  07/06/10 - bill pay report changes
 *    nkgautam  06/30/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.manager.utility.UtilityManager;
import oracle.retail.stores.pos.reports.BillPayReport;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateRangeReportBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class ValidateBillPayTransactionReportAisle extends PosLaneActionAdapter
{

    private static final long serialVersionUID = 706317260110984034L;

    /**
    not yet available tag
     **/
    public static final String NOT_YET_AVAILABLE_TAG = "NotYetAvailable";
    /**
    not yet available default text
     **/
    public static final String NOT_YET_AVAILABLE_TEXT = "NOT YET AVAILABLE";

    /**
     * Validate the date and the query, then mail the letter to print.
     */
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManager.TYPE);
        DateRangeReportBeanModel beanModel = (DateRangeReportBeanModel) ui.getModel(POSUIManagerIfc.DATE_RANGE_REPORT);
        EYSDate startDate = beanModel.getStartBusinessDate();
        EYSDate endDate = beanModel.getEndBusinessDate();

        Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        String startDateString = startDate.toFormattedString(DateFormat.SHORT,locale);
        String endDateString = endDate.toFormattedString(DateFormat.SHORT,locale);
        boolean mailLetter = true;
        String[] errorString = new String[2];

        SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
        inquiry.setLocaleRequestor(utility.getRequestLocales());

        // Check first to see if the date is valid
        if (endDate.before(startDate))
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, RegisterReportsCargo.INVALID_DATE_RANGE, null);
        }
        else if ((!startDate.isValid()) || (!endDate.isValid())) // valid date
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, "InvalidBusinessDate", null);
        }
        else
        {
            RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
            String storeID = cargo.getStoreStatus().getStore().getStoreID();
            boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
            inquiry.setStoreNumber(storeID);
            inquiry.setTrainingMode(String.valueOf(trainingMode));

            try
            {
                EYSDate[] dateRange = new EYSDate[2];

                dateRange[0] = startDate;
                dateRange[1] = endDate;

                inquiry.setDateRange(dateRange);
                TransactionSummaryIfc transSummary = null;

                TransactionReadDataTransaction dataTransaction = null;
                dataTransaction = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_READ_DATA_TRANSACTION);


                transSummary = dataTransaction.retrieveBillPayments(inquiry);


                BillPayReport billsReport = new BillPayReport(transSummary,startDate, endDate);

                PromptAndResponseModel parModel = new PromptAndResponseModel();
                POSBaseBeanModel    baseModel    = new POSBaseBeanModel();

                parModel.setArguments("");
                baseModel.setPromptAndResponseModel(parModel);

                if (cargo.getOperator() == null || cargo.getOperator().getEmployeeID() == null)
                {
                    String notYetAvailable = utility.retrieveText("Common",
                            BundleConstantsIfc.MANAGER_BUNDLE_NAME,
                            NOT_YET_AVAILABLE_TAG,
                            NOT_YET_AVAILABLE_TEXT);
                    billsReport.setCashierID(notYetAvailable);
                }
                else
                {
                    billsReport.setCashierID(cargo.getOperator().getEmployeeID());
                }

                billsReport.setStoreID(storeID);
                cargo.setReport((RegisterReport) billsReport);
                ui.showScreen(POSUIManagerIfc.PRINT_REPORT, baseModel);
            }
            catch (DataException exception)
            {
                logger.error( "" + exception + "");
                mailLetter = false;
                String errorType = null;

                if(exception.getErrorCode() != DataException.NO_DATA)
                {
                    // A real error
                    errorString[0] = utility.getErrorCodeString(exception.getErrorCode());
                    errorString[1] = "";
                    errorType = RegisterReportsCargo.DATABASE_ERROR;
                    HashMap map = new HashMap(1);
                    map.put(new Integer(DialogScreensIfc.BUTTON_OK), CommonLetterIfc.CANCEL);
                    showDialogScreen(ui, errorString, errorType, map);
                }
                else
                {
                    errorType = "INFO_NOT_FOUND_ERROR";
                    showDialogScreen(ui, errorString, errorType, null);
                }
            }

            if (mailLetter)
            {
                PromptAndResponseModel parModel = new PromptAndResponseModel();
                POSBaseBeanModel baseModel = new POSBaseBeanModel();

                parModel.setArguments("");
                baseModel.setPromptAndResponseModel(parModel);
                if ("Preview".equals(bus.getCurrentLetter().getName()))
                {
                    bus.mail(new Letter("PrintPreview"), BusIfc.CURRENT);
                }
                else
                {
                    // ui.showScreen(POSUIManagerIfc.PRINT_REPORT, beanModel);
                    bus.mail(new Letter("ValidBillPayReport"), BusIfc.CURRENT);
                }

            }

        }
    }

    protected void showDialogScreen(POSUIManagerIfc ui, String[] args, String id, HashMap buttonToLetterMapping)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);

        if(buttonToLetterMapping != null)
        {
            Iterator iter = buttonToLetterMapping.keySet().iterator();
            while(iter.hasNext())
            {
                Integer buttonName = (Integer) iter.next();
                String letter = (String) buttonToLetterMapping.get(buttonName);
                model.setButtonLetter(buttonName.intValue(), letter);
            }
        }
        // there will be no args for info not found.
        if (args[0] !=null )
        {
            model.setArgs(args);
        }

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
