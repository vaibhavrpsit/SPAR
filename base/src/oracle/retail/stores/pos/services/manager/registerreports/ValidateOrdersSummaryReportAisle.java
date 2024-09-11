/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/ValidateOrdersSummaryReportAisle.java /main/23 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    sgu       01/22/13 - calling getOrderHistory api for order summary report
 *    yiqzhao   01/20/13 - Replace EYSDate by java.util.Date in order to use in
 *                         CO.
 *    sgu       07/17/12 - add order summary search by card token or masked
 *                         number
 *    sgu       07/16/12 - remove result type from order search criteria
 *    sgu       07/13/12 - clean up order manager api
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    sgu       05/22/12 - remove order filled status
 *    sgu       05/21/12 - remove order printed status
 *    ohorne    05/16/12 - now using OrderManager
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    vtemker   03/07/11 - Print Preview for Reports - fixed review comments
 *    vtemker   03/03/11 - Changes for Print Preview Reports Quickwin
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    jswan     04/24/09 - Modified to ensure that orders created in training
 *                         mode can only retrieve in training mode, and
 *                         non-training mode orders can only be retrieved in
 *                         non-training mode.
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         8/21/2007 2:07:55 PM   Ranjan X Ojha   Fix
 *         for No Orders Found Dialog
 *    4    360Commerce 1.3         8/14/2007 2:32:33 PM   Ranjan X Ojha   Fixed
 *          Data for OrderSummaryReports
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:42 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:29 PM  Robert Pearse
 *
 *   Revision 1.8  2004/07/23 17:57:15  jdeleau
 *   @scr 5183 Correct Flow for register reports on database error.
 *
 *   Revision 1.7  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
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
 *    Rev 1.8   Jul 17 2003 14:30:14   sfl
 * Based on BA decision, not showing report type argument value for report is printing message.
 * Resolution for POS SCR-3181: Printing Register Reports - Message Prompt is incorrect.
 *
 *    Rev 1.7   Jul 15 2003 18:33:56   DCobb
 * Added ORDER_STATUS_VOIDED to the status array and set the time of the end date to 23:59:59.
 * Resolution for POS SCR-2476: Voided special order not show on Order Summary report
 *
 *    Rev 1.6   Jul 03 2003 18:11:42   sfl
 * Retrieve correct end date when the system date is BEFORE the business date during Orders Summary reporting.
 * Resolution for POS SCR-3022: Current business date > system date, selecting to print reports returns Invalid Date Range
 *
 *    Rev 1.5   Jun 04 2003 16:00:58   RSachdeva
 * showDialogScreen for database error
 * Resolution for POS SCR-2485: Offline-Reports -Order Locks App
 *
 *    Rev 1.4   May 28 2003 16:31:12   bwf
 * Handle if a start date is not entered.
 * Resolution for 2616: At Order Sum Rpt screen, clear start day field and select Print, POS hangs up
 *
 *    Rev 1.3   Mar 05 2003 10:01:28   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 25 2002 13:23:20   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 10 2002 14:13:34   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:19:14   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:36:44   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:24:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.order.OrderManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.reports.OrdersSummaryReport;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateRangeReportBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
   Verifies that the querry is valid before going on to print.

    @version $Revision: /main/23 $
**/
//------------------------------------------------------------------------------
public class ValidateOrdersSummaryReportAisle extends PosLaneActionAdapter
{
    /**
     * This id is used to tell the compiler not to generate a new
     * serialVersionUID.
     */
    private static final long serialVersionUID = -8784865890589499612L;

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/23 $";
    /**
       class name constant
    **/
    public static final String LANENAME = "ValidateOrdersSummaryReportAisle";
    /**
        not yet available tag
    **/
    public static final String NOT_YET_AVAILABLE_TAG = "NotYetAvailable";
    /**
        not yet available default text
    **/
    public static final String NOT_YET_AVAILABLE_TEXT = "NOT YET AVAILABLE";

    //--------------------------------------------------------------------------
    /**
       Validate the date and the querry, then mail the letter to print.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DateRangeReportBeanModel beanModel = (DateRangeReportBeanModel) ui.getModel(POSUIManagerIfc.ORDER_SUM_RPT);
        List<OrderSummaryEntryIfc> summaries = new ArrayList<OrderSummaryEntryIfc>(0);

        EYSDate startDate = beanModel.getStartBusinessDate();
        EYSDate endDate = beanModel.getEndBusinessDate();
        Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        String startDateString = null;
        if(startDate != null)
        {
            startDateString = startDate.toFormattedString(DateFormat.SHORT,locale);
        }
        String endDateString = endDate.toFormattedString(DateFormat.SHORT,locale);
        boolean mailLetter = true;
        String[] errorString = new String[2];

        RegisterReportsCargo cargo = (RegisterReportsCargo) bus.getCargo();
        boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();
        String storeID = cargo.getStoreStatus().getStore().getStoreID();

        // Check first to see if the dates are in ascending order and valid
        if(startDate != null &&
           endDate.before(startDate))
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, RegisterReportsCargo.INVALID_DATE_RANGE, null);
        }
        else if (startDate == null || (!startDate.isValid()) || (!endDate.isValid())) // valid date
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, "InvalidBusinessDate", null);
        }
        else
        {
            OrderManagerIfc orderManager = (OrderManagerIfc)bus.getManager(OrderManagerIfc.TYPE);
            String errorType = null;

            // Get order summaries New through Filled
            try
            {
                startDate.initialize(startDate.getYear(),
                  startDate.getMonth(),
                  startDate.getDay(),
                  00,
                  00,
                  00);
                endDate.initialize(endDate.getYear(),
                  endDate.getMonth(),
                  endDate.getDay(),
                  23,
                  59,
                  59);

                summaries.addAll(orderManager.getOrderHistory(startDate, endDate, storeID, trainingMode));

                if (summaries.isEmpty())
                {
                    // No orders
                    mailLetter = false;
                    errorType = "INFO_NOT_FOUND_ERROR";
                    showDialogScreen(ui, errorString, errorType, null);
                }
            }
            catch (DataException de)
            {
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                errorString[0] = utility.getErrorCodeString(de.getErrorCode());
                errorString[1] = "";
                errorType = RegisterReportsCargo.DATABASE_ERROR;
                mailLetter = false;
                HashMap<Integer, String> map = new HashMap<Integer, String>(1);
                map.put(new Integer(DialogScreensIfc.BUTTON_OK), CommonLetterIfc.CANCEL);
                showDialogScreen(ui, errorString, errorType, map);
            }
        } // else

        // Only mail a letter if we didn't put up a dialog screen
        if (mailLetter)
        {
            OrderSummaryEntryIfc[] data = (OrderSummaryEntryIfc[])summaries.toArray(new OrderSummaryEntryIfc[summaries.size()]);
            OrdersSummaryReport osr = new OrdersSummaryReport(data, startDate, endDate);
            osr.setStoreID(storeID);

            if (cargo.getOperator() == null || cargo.getOperator().getEmployeeID() == null)
            {
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                String notYetAvailable = utility.retrieveText("Common",
                                                              BundleConstantsIfc.MANAGER_BUNDLE_NAME,
                                                              NOT_YET_AVAILABLE_TAG,
                                                              NOT_YET_AVAILABLE_TEXT);
                osr.setCashierID(notYetAvailable);
            }
            else
            {
                osr.setCashierID(cargo.getOperator().getEmployeeID());
            }

            cargo.setReport((RegisterReport) osr);

            PromptAndResponseModel parModel = new PromptAndResponseModel();
            POSBaseBeanModel    baseModel    = new POSBaseBeanModel();

            parModel.setArguments("");
            baseModel.setPromptAndResponseModel(parModel);


            if ("Preview".equals(bus.getCurrentLetter().getName()))
            {
                bus.mail(new Letter("PrintPreview"), BusIfc.CURRENT);
            }
            else
            {
                bus.mail(new Letter("ValidOrdersSummary"), BusIfc.CURRENT);
            }
        }

    }

    //--------------------------------------------------------------------------
    /**
       Set the args in the ui model and display the error dialog.

       @param args String array for the text to display on the dialog
       @param id String identifier for the configuration of the dialog
       @param buttonToLetterMapping map buttons to letters they should take
    **/
    //--------------------------------------------------------------------------
    protected void showDialogScreen(POSUIManagerIfc ui, String[] args, String id, HashMap<Integer, String> buttonToLetterMapping)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);

        if(buttonToLetterMapping != null)
        {
            Iterator<Integer> iter = buttonToLetterMapping.keySet().iterator();
            while(iter.hasNext())
            {
                Integer buttonName = (Integer) iter.next();
                String letter = (String) buttonToLetterMapping.get(buttonName);
                model.setButtonLetter(buttonName.intValue(), letter);
            }
        }

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
