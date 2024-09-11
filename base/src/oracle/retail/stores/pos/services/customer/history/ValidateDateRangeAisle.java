/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/history/ValidateDateRangeAisle.java /main/16 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    npoola    02/27/09 - reverted back the changes from mahipal for date
 *                         validation. Mathews changes already fixed the bug
 *    mahising  02/25/09 - Fixed issue for start and end date lable
 *    mkochumm  02/23/09 - use default locale date format in error dialog
 * 
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.history;

import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;

/**
 * Verifies that the begin date and end date are valid before going on to
 * perform the email search. $Revision: /main/16 $
 */
public class ValidateDateRangeAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = -3800126468249810224L;

    public static final String LANENAME = "ValidateDateRangeAisle";

    /**
     * Validates the dates are valid, sends the appropriate letter.
     * 
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {

        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();

        Letter letter = new Letter("Lookup"); // default customer search
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DateSearchBeanModel model = (DateSearchBeanModel)ui.getModel(POSUIManagerIfc.NARROW_SEARCH);

        EYSDate[] dateRange = new EYSDate[2];
        dateRange[0] = model.getStartDate();
        dateRange[1] = model.getEndDate();

        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        String startDateString = dateRange[0].toFormattedString(defaultLocale);
        String endDateString = dateRange[1].toFormattedString(defaultLocale);
        String[] errorString = new String[2];

        // Check first to see if the dates are in ascending order
        if (dateRange[0].after(dateRange[1]))
        {
            dateRange[0].setMonth(-1);
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, "INVALID_DATE_RANGE");
        }
        else if ((!dateRange[0].isValid()) || (!dateRange[1].isValid())) // valid
                                                                            // date
        {
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, "InvalidBusinessDate");
        }
        else
        // Only mail a letter if we didn't put up a dialog screen
        {
            // Save search criteria to cargo
            SearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();
            searchCriteria.setDateRange(dateRange);
            cargo.setSearchCriteria(searchCriteria);
            bus.mail(letter, BusIfc.CURRENT);
        }

    }

    /**
     * Set the args in the ui model and display the error dialog.
     * 
     * @param args String array for the text to display on the dialog
     * @param id String identifier for the configuration of the dialog
     */
    protected void showDialogScreen(POSUIManagerIfc ui, String[] args, String id)
    {
        UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, id, args, CommonLetterIfc.RETRY);
    }

}
