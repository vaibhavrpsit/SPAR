/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/email/find/ValidateDateRangeAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:29 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.email.find;

import oracle.retail.stores.common.utility._360Date;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.email.EmailCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DateSearchBeanModel;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Verifies that the begin date and end date are valid before going on to
 * perform the email search.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class ValidateDateRangeAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = 4178389072646680520L;

    public static final String LANENAME = "ValidateDateRangeAisle";

    /**
     * Validates the dates are valid, sends the appropriate letter.
     * 
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {

        EmailCargo cargo = (EmailCargo)bus.getCargo();

        Letter letter = new Letter("CustomerSearch"); // default customer
                                                        // search
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DateSearchBeanModel model = null;
        EYSDate startDate = null;
        EYSDate endDate = null;

        model = (DateSearchBeanModel)ui.getModel(POSUIManagerIfc.NARROW_SEARCH);
        startDate = model.getStartDate();
        endDate = model.getEndDate();

        String startDateString = startDate.toFormattedString(_360Date.FORMAT_MMDDYYYY);
        String endDateString = endDate.toFormattedString(_360Date.FORMAT_MMDDYYYY);
        boolean mailLetter = true;
        String[] errorString = new String[2];

        // Check first to see if the dates are in ascending order
        if (startDate.after(endDate))
        {
            startDate.setMonth(-1);
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, "INVALID_DATE_RANGE");
        }
        else if ((!startDate.isValid()) || (!endDate.isValid())) // valid
                                                                    // date
        {
            mailLetter = false;
            errorString[0] = startDateString;
            errorString[1] = endDateString;
            showDialogScreen(ui, errorString, "InvalidBusinessDate");
        }
        // Only mail a letter if we didn't put up a dialog screen
        // mail OrderSearch or CustomerSearch to continue lookup for emails
        if (mailLetter)
        {
            if (cargo.getSearchMethod() == EmailCargo.SEARCH_BY_ORDER_ID)
            {
                letter = new Letter("OrderSearch");
            }

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
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);

        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }
}
