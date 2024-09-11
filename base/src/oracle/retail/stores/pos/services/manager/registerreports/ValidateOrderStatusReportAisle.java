/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/registerreports/ValidateOrderStatusReportAisle.java /main/3 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    yiqzhao   01/20/13 - Replace EYSDate by java.util.Date in order to use in
 *                         CO.
 *    sgu       01/15/13 - add back order status report
 *    sgu       01/15/13 - add back order status report
 *    vtemker   03/03/11 - Print Preview for Reports
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
 *    Rev 1.5   Jul 17 2003 14:32:28   sfl
 * Based on BA decision, not showing report type argument value for report is printing message.
 * Resolution for POS SCR-3181: Printing Register Reports - Message Prompt is incorrect.
 *
 *    Rev 1.4   Jul 01 2003 14:42:54   sfl
 * Get the endDate value from the bean model.
 * Resolution for POS SCR-2614: If current business date > system date, for order status report, get invalid date range
 *
 *    Rev 1.3   Mar 05 2003 10:09:22   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 25 2002 13:26:20   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 10 2002 14:15:14   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:19:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:36:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:24:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager.registerreports;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.OrderReadDataTransaction;
import oracle.retail.stores.domain.manager.order.OrderManager;
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
import oracle.retail.stores.pos.reports.OrderStatusReport;
import oracle.retail.stores.pos.reports.RegisterReport;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.OrderStatusReportBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//------------------------------------------------------------------------------
/**
   Verifies that the querry is valid before going on to print.
   <P>
   @version $Revision: /main/3 $
**/
//------------------------------------------------------------------------------
public class ValidateOrderStatusReportAisle extends PosLaneActionAdapter
{

    /**
        Aisle class name
    **/
    public static final String LANENAME = "ValidateOrderStatusReportAisle";
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
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        OrderManager orderManager = (OrderManager)bus.getManager(OrderManagerIfc.TYPE);
        OrderStatusReportBeanModel beanModel = (OrderStatusReportBeanModel) ui.getModel(POSUIManagerIfc.ORDER_REPORT);
        EYSDate startDate = beanModel.getStartBusinessDate();
        EYSDate endDate = beanModel.getEndBusinessDate();

        Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        String startDateString = startDate.toFormattedString(DateFormat.SHORT,locale);
        String endDateString = endDate.toFormattedString(DateFormat.SHORT,locale);
        String status = beanModel.getSelectedOrderStatus();
        boolean mailLetter = true;
        String[] errorString = new String[2];

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
            OrderSearchCriteriaIfc criteria = DomainGateway.getFactory().getOrderSearchCriteriaInstance();
           
            int statusArray[] = new int[] {OrderConstantsIfc.ORDER_STATUS_UNDEFINED};

            // Get the code that corresponds to the status string we got from the UI
            for (int i = 0; i < OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS.length; i++)
            {
                if (status.equals(OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS[i]))
                {
                    statusArray[0] = i;
                    break;
                }
            }

            // Retrieve the requested records by status
            try
            {
                switch (statusArray[0])
                {
                    case OrderConstantsIfc.ORDER_STATUS_NEW:
                    case OrderConstantsIfc.ORDER_STATUS_PRINTED:
                    case OrderConstantsIfc.ORDER_STATUS_PARTIAL:
                    case OrderConstantsIfc.ORDER_STATUS_FILLED:

                    criteria.configure(statusArray, startDate.toDate(), endDate.toDate(), storeID, trainingMode);
                    break;
                    // reset the start date to begin at 00:00:00
                    case OrderConstantsIfc.ORDER_STATUS_CANCELED:
                    case OrderConstantsIfc.ORDER_STATUS_COMPLETED:
                    startDate.initialize(startDate.getYear(),
                               startDate.getMonth(),
                               startDate.getDay(),
                               00,
                               00,
                               00);

                    criteria.configure(statusArray, startDate.toDate(), endDate.toDate(), storeID, trainingMode);
                    break;
                    default:
                    logger.error( "Unknown order status");
                }

                OrderSummaryEntryIfc[] data = orderManager.getOrderSummaries(criteria).toArray(new OrderSummaryEntryIfc[0]);
                OrderStatusReport osr = new OrderStatusReport(data, startDate, endDate, status);
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

                PromptAndResponseModel parModel = new PromptAndResponseModel();
                POSBaseBeanModel    baseModel    = new POSBaseBeanModel();

                parModel.setArguments("");
                baseModel.setPromptAndResponseModel(parModel);

                cargo.setReport((RegisterReport) osr);
    	        // Only mail a letter if we didn't put up a dialog screen
    	        /*if("Preview".equals(bus.getCurrentLetter().getName()))
    	        {
    	        	bus.mail(new Letter("PrintPreview"), BusIfc.CURRENT);
    		     }
    	        else
    	        {
    	           //ui.showScreen(POSUIManagerIfc.PRINT_REPORT, beanModel);
    	           bus.mail(new Letter("ValidOrderStatus"), BusIfc.CURRENT);
    	        }*/
                //ui.showScreen(POSUIManagerIfc.PRINT_REPORT, baseModel);
            }
            catch (DataException exception)
            {
                logger.error( "" + exception + "");
                mailLetter = false;
                String errorType = null;

                if(exception.getErrorCode() != DataException.NO_DATA)
                {
                    // A real error
                    UtilityManagerIfc utility =
                      (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
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
        }

        if (mailLetter)
        {
	        // Only mail a letter if we didn't put up a dialog screen
	        if("Preview".equals(bus.getCurrentLetter().getName()))
	        {
	        	bus.mail(new Letter("PrintPreview"), BusIfc.CURRENT);
		     }
	        else
	        {
	           ui.showScreen(POSUIManagerIfc.PRINT_REPORT, beanModel);
	           bus.mail(new Letter("ValidOrderStatus"), BusIfc.CURRENT);
	        }
        }

    }

    //--------------------------------------------------------------------------
    /**
       Set the args in the ui model and display the error dialog.

       @param ui POSUIManagerIfc
       @param args String array for the text to display on the dialog
       @param id String identifier for the configuration of the dialog
       @param buttonToLeterMapping mapping to letters to mail on the error dialog for the given button
    **/
    //--------------------------------------------------------------------------
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
