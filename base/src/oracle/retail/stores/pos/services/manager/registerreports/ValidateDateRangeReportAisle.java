/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/ValidateDateRangeReportAisle.java /main/15 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    10/08/14 - set the selected date range when the department
 *                         sales report doesnt fetch any records
 *    abondala  09/04/13 - initialize collections
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    vtemker   03/07/11 - Print Preview for Reports - fixed review comments
 *    vtemker   03/03/11 - Changes for Print Preview Reports Quickwin
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ohorne    03/13/09 - added support for localized department names
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.10  2004/07/23 17:57:15  jdeleau
 *   @scr 5183 Correct Flow for register reports on database error.
 *
 *   Revision 1.9  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.8  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.7  2004/05/05 20:06:44  jdeleau
 *   @scr 3022 Fix error message appearing for reports when they have no data.
 *
 *   Revision 1.6  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/13 12:57:46  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/29 17:25:05  jdeleau
 *   @scr 3190 If the Dept Sales Report has no data, still print a report
 *   with the totals being set to 0
 *
 *   Revision 1.3  2004/02/12 16:50:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 17 2003 14:29:40   sfl
 * Based on BA decision, not showing report type argument value for report is printing message.
 * Resolution for POS SCR-3181: Printing Register Reports - Message Prompt is incorrect.
 *
 *    Rev 1.3   Mar 05 2003 09:53:34   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 25 2002 13:19:28   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:33:58   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:19:14   msg
 * Initial revision.
 *
 *    Rev 1.1   09 Apr 2002 17:09:14   jbp
 * modified design of associate productivity report
 * Resolution for POS SCR-15: Sales associate activity report performs inadequately, crashes
 *
 *    Rev 1.0   Mar 18 2002 11:36:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:24:10   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

// Java imports
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.AssociateProductivityIfc;
import oracle.retail.stores.domain.financial.DepartmentActivity;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.reports.AssociateProductivityReport;
import oracle.retail.stores.pos.reports.DepartmentSalesReport;
import oracle.retail.stores.pos.reports.HourlyProductivityReport;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateRangeReportBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
    Get the date range, then get the data.

    @version $Revision: /main/15 $
**/
//------------------------------------------------------------------------------
public class ValidateDateRangeReportAisle extends PosLaneActionAdapter
{
	private static final long serialVersionUID = 3092212237580098299L;
	
	/**
     * Name of this lane
     */
    public static final String LANENAME = "ValidateDateRangeReportAisle";

    //--------------------------------------------------------------------------
    /**
       Get the dates from the UI.
       See if there's any data in that date range.
       Show the progress screen.
       Mail a letter.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DateRangeReportBeanModel beanModel =
          (DateRangeReportBeanModel) ui.getModel(POSUIManagerIfc.DATE_RANGE_REPORT);
        EYSDate startDate = beanModel.getStartBusinessDate();
        EYSDate endDate = beanModel.getEndBusinessDate();
        //Retrieve locale for ui subsystem
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        String startDateString = startDate.toFormattedString(locale);;
        String endDateString = endDate.toFormattedString(locale);
        boolean mailLetter = true;
        String[] errorString = new String[2];

        // Check first to see if the date is valid
        if (endDate.before(startDate) || !startDate.isValid() || !endDate.isValid())
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, RegisterReportsCargo.INVALID_DATE_RANGE, null);
        }
        else
        {
            RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
            String storeID = cargo.getStoreStatus().getStore().getStoreID();
            
            FinancialTotalsDataTransaction ftdt = null;
            
            ftdt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            
            RegisterReport report = null;

            try
            {
                switch (cargo.getReportType())
                {
                    case RegisterReportsCargo.REPORT_DEPTSALES:
                    {
                        report = getDepartmentSalesReport(storeID, startDate, endDate);
                        break;
                    }
                    case RegisterReportsCargo.REPORT_HOURSALES:
                    {
                        HourlyProductivityReport hpr =
                            new HourlyProductivityReport(ftdt.readTimeIntervalTotals(storeID, startDate, endDate));
                        hpr.setStartDate(startDate);
                        hpr.setEndDate(endDate);
                        report = hpr;
                        break;
                    }
                    case RegisterReportsCargo.REPORT_ASSOCPROD:
                    {
                        AssociateProductivityIfc[] apArray =
                            ftdt.readAssociateProductivity(storeID, startDate, endDate);
                        AssociateProductivityReport apr =
                            new AssociateProductivityReport(apArray);

                        apr.setStartDate(startDate);
                        apr.setEndDate(endDate);
                        report = apr;
                        break;
                    }
                    default:
                    {
                        logger.error(
                                     "" + Integer.toString(cargo.getReportType()) + " is not a valid type");
                    }
                }
                printReport(report, cargo, ui);
            }
            catch (DataException exception)
            {
                if(exception.getErrorCode() != DataException.NO_DATA)
                {
                    exception.printStackTrace();
                    logger.error(exception.toString());
                    mailLetter = false;
                    String errorType = null;
                    // A real error
                    UtilityManagerIfc utility =
                      (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                    errorString[0] = utility.getErrorCodeString(exception.getErrorCode());
                    errorString[1] = "";
                    errorType = RegisterReportsCargo.DATABASE_ERROR;
                    HashMap<Integer, String> map = new HashMap<Integer, String>(1);
                    map.put(new Integer(DialogScreensIfc.BUTTON_OK), CommonLetterIfc.CANCEL);
                    showDialogScreen(ui, errorString, errorType, map);
                }
                else if(cargo.getReportType() == RegisterReportsCargo.REPORT_HOURSALES)
                {
                    HourlyProductivityReport hpr = new HourlyProductivityReport();
                    hpr.setStartDate(startDate);
                    hpr.setEndDate(endDate);
                    printReport(hpr, cargo, ui);
                }
                else if(cargo.getReportType() == RegisterReportsCargo.REPORT_ASSOCPROD)
                {
                    AssociateProductivityIfc[] apArray = new AssociateProductivityIfc[0];
                    AssociateProductivityReport apr = new AssociateProductivityReport(apArray);
                    apr.setStartDate(startDate);
                    apr.setEndDate(endDate);
                    printReport(apr, cargo, ui);
                }

            }
        }

        // Only mail a letter if we didn't put up a dialog screen
        if (mailLetter)
        {
            if ("Preview".equals(bus.getCurrentLetter().getName()))
            {
                bus.mail(new Letter("PrintPreview"), BusIfc.CURRENT);
            }
            else
            {
                PromptAndResponseModel parModel = new PromptAndResponseModel();
                POSBaseBeanModel baseModel = new POSBaseBeanModel();
                parModel.setArguments("");
                baseModel.setPromptAndResponseModel(parModel);
                bus.mail(new Letter("ValidDateRange"), BusIfc.CURRENT);
            }
        }
    }

    /**
     * This is a helper class to print hourly sales, associate productivity,
     * and departmental sales reports
     *  
     *  @param report report to print
     *  @param cargo containing storeID needed for report
     *  @param ui Interface used to draw the next screen
     */
    protected void printReport(RegisterReport report, RegisterReportsCargo cargo, POSUIManagerIfc ui)
    {
        String storeID = cargo.getStoreStatus().getStore().getStoreID();
        report.setStoreID(storeID);
        report.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
        report.setCashierID(cargo.getOperator().getEmployeeID());
        cargo.setReport(report);
    }

    /**
     *  Produce a DepartmentSales Report, if there is no data then produce
     *  an empty report (SCR 3190).
     * 
     *  @param storeId Store ID to get the report for
     *  @param startDate start date of report
     *  @param endDate end date of report
     *  @return The department report
     *  @throws DataException If there is an error getting the report
     */
    public DepartmentSalesReport getDepartmentSalesReport(String storeId, EYSDate startDate, EYSDate endDate) 
       throws DataException
    {
        DepartmentSalesReport dsr = null;
        try
        {
            FinancialTotalsDataTransaction ftdt = null;
            
            ftdt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            
            LocaleRequestor locales = new LocaleRequestor(LocaleMap.getLocale(LocaleConstantsIfc.REPORTS));
            dsr = new DepartmentSalesReport(ftdt.readDepartmentTotals(storeId, startDate, endDate, locales));
            dsr.setStartDate(startDate);
            dsr.setEndDate(endDate);
            return dsr;
        }
        catch(DataException de)
        {
            if(de.getErrorCode() == DataException.NO_DATA)
            {
                dsr = new DepartmentSalesReport(new DepartmentActivity[0]);
                dsr.setStartDate(startDate);
                dsr.setEndDate(endDate);
                return dsr;
            }
            else
            {
                throw de;
            }
        }
    }
    //--------------------------------------------------------------------------
    /**
       Set the args in the ui model and display the error dialog.

       @param ui POSUIManagerIfc
       @param args String array for the text to display on the dialog
       @param id String identifier for the configuration of the dialog
       @param buttonToLetterMapping mapping of buttons to letters
    **/
    //--------------------------------------------------------------------------
    private void showDialogScreen(POSUIManagerIfc ui, String[] args, String id, HashMap<Integer, String> buttonToLetterMapping)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);

        if(buttonToLetterMapping != null)
        {
            Iterator <Integer> iter = buttonToLetterMapping.keySet().iterator();
            while(iter.hasNext())
            {
                Integer buttonName = (Integer) iter.next();
                String letter = (String) buttonToLetterMapping.get(buttonName);
                model.setButtonLetter(buttonName.intValue(), letter);
            }
        }
        // there will be no args for info not found.
        if (args[0] != null)
        {
            model.setArgs(args);
        }

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
