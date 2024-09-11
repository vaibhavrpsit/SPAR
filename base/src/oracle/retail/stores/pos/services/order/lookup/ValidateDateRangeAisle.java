/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/lookup/ValidateDateRangeAisle.java /main/14 2012/07/13 12:43:50 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/12/12 - remove retrieve order summary by status or by
 *                         emessage
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:24  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Aug 26 2002 10:15:20   jriggins
 * removed a debug statement
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:12:28   msg
 * Initial revision.
 *
 *    Rev 1.1   05 Apr 2002 16:01:04   dfh
 * updates to improve date validation, cleanup
 * Resolution for POS SCR-178: CR/Order, incomplete date range search, dialog text erroneous
 *
 *    Rev 1.0   Mar 18 2002 11:41:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 13:01:12   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.lookup;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
   Verifies that the begin date and end date are valid before going on to perform
   the order search.
   <P>
   @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class ValidateDateRangeAisle extends PosLaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "ValidateDateRangeAisle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
       screen name constant
    **/
    protected static final String INVALID_DATE_RANGE = "INVALID_DATE_RANGE";

    //--------------------------------------------------------------------------
    /**
       Validates the dates are valid and displays an error message or sends the
       appropriate letter.
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get dates from the model and order cargo to determine type of search
        OrderSearchCargoIfc cargo = (OrderSearchCargoIfc) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        DateSearchBeanModel model1 = (DateSearchBeanModel)ui.getModel(POSUIManagerIfc.NARROW_SEARCH);
        EYSDate startDate = model1.getStartDate();
        EYSDate endDate = model1.getEndDate();

        boolean mailLetter = true;
        String[] errorString = new String[2];
        String startDateString = "";
        String endDateString = "";
        if (startDate != null && endDate != null)
        {
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            startDateString = startDate.toFormattedString(locale);
            endDateString = endDate.toFormattedString(locale);

            // Check first to see if the dates are in ascending order
            if(startDate.after(endDate))
            {
                mailLetter = false;
                startDate.setMonth(-1);
                errorString[0] = startDateString;
                errorString[1] = endDateString;
                showDialogScreen(ui, errorString, INVALID_DATE_RANGE);
            }
        }
        else if (startDate != null)
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, INVALID_DATE_RANGE);
        }
        else if (endDate != null)
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, INVALID_DATE_RANGE);
        }
        // Only mail a letter if we didn't put up a dialog screen
        if (mailLetter)
        {
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }

    //--------------------------------------------------------------------------
    /**
       Set the args in the ui model and display the error dialog.

       @param ui  UI manager
       @param args String array for the text to display on the dialog
       @param id String identifier for the configuration of the dialog
    **/
    //--------------------------------------------------------------------------
    protected void showDialogScreen(POSUIManagerIfc ui, String[] args, String id)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);

        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }
}
