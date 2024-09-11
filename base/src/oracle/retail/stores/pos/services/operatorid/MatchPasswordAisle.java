/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/MatchPasswordAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         9/26/2006 3:37:35 PM   Christian Greene update
 *        renamed UpdateEmployeePasswordAdaptor
 *  4    360Commerce 1.3         9/26/2006 12:14:18 PM  Christian Greene
 *       Refactor persisting employee password change into a parent-level
 *       aisle
 *  3    360Commerce 1.2         3/31/2005 4:29:01 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:28 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:34 PM  Robert Pearse   
 * $
 * Revision 1.9.2.2  2004/11/22 22:25:57  kll
 * @scr 7683: update logging
 *
 * Revision 1.9.2.1  2004/11/22 21:12:25  kll
 * @scr 7683: catch null exception, but shouldn't terminate flow
 *
 * Revision 1.9  2004/07/28 22:53:23  epd
 * @scr 6593 fixed training mode issue
 *
 * Revision 1.8  2004/07/23 22:17:26  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.7  2004/06/03 14:47:46  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.6  2004/05/06 11:21:32  tmorris
 * @scr 4221 -Employees must re-enter new pwd until out of training mode.
 *
 * Revision 1.5  2004/04/20 13:17:06  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.4  2004/04/13 02:26:34  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.3  2004/02/26 22:57:54  jriggins
 * @scr 3872 code review changes
 *
 * Revision 1.2  2004/02/19 23:36:45  jriggins
 * @scr 3782 this commit mainly deals with the database modifications needed for Enter New Password feature in Operator ID
 * Revision 1.1 2004/02/13 16:35:20 jriggins
 * @scr 3782 Initial revision
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.common.UpdateEmployeePasswordAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * New password has been entered. Now we need to confirm this password by
 * prompting again.
 */
public class MatchPasswordAisle extends UpdateEmployeePasswordAdapter
{
    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.operatorid.MatchPasswordAisle.class);

    public void traverse(BusIfc bus)
    {
        LetterIfc letter = null;

        // Pull UI entry from screen
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String newPassword2 = ui.getInput();

        // Compare the two password entries to make sure they match
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        String newPassword1 = cargo.getEmployeeNewPassword();

        // If the entries match, save them in the current object and the
        // database
        // also change the password required flag to false.
        // Set the appropriate letter depending on results.
        if (newPassword1.equals(newPassword2))
        {
            EmployeeIfc employee = cargo.getSelectedEmployee();
            letter = handlePasswordMatch(employee, newPassword2, cargo);
            bus.mail(letter, BusIfc.CURRENT);
        }
        // Display an error dialog if the entries don't match
        else
        {
            showPasswordEntryError(ui);
        }
    }

    /**
     * Provides the business logic for updating the new password and setting
     * the new password required flag to <code>false</code> in the current
     * EmployeeIfc in cargo as well as in the database
     * 
     * @param employee
     * @param newPassword
     * @return LetterIfc the letter based on whether or not the employee update
     *         was successful
     */
    protected LetterIfc handlePasswordMatch(
        EmployeeIfc employee,
        String newPassword,
        OperatorIdCargo cargo)
    {
        LetterIfc letter = null;

        boolean trainingModeIndicator = false;
        try
        {
            trainingModeIndicator = cargo.getRegister().getWorkstation().isTrainingMode();
        }
        catch( Exception de)
        {
            logger.warn(
                    "trainingModeIndicator is a null value" ,
                    de);
        }
        
        //If in Training Mode keep asking a new employee to re-enter password
        if(trainingModeIndicator)
        {
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        else
        {
            boolean updateSuccess = updateEmployeePassword(employee, newPassword);
            if (updateSuccess)
            {
                letter = new Letter(CommonLetterIfc.SUCCESS);
            }
            else
            {
                letter = new Letter(CommonLetterIfc.DB_ERROR);
            }
        }
        
        return letter;
    }

    /**
     * Displays the NewPasswordEntryError dialog screen.
     * 
     * @param ui
     */
    protected void showPasswordEntryError(POSUIManagerIfc ui)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("NewPasswordEntryError");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);        
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "RetryFailedMatchPassword");        
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "CancelFailedMatchPassword");
        
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
