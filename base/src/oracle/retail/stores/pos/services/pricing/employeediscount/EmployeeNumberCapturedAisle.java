/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/EmployeeNumberCapturedAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/22/10 - lowered EMPLOYEE_ID_MIN_LENGTH to 1
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/2/2008 4:34:42 PM    Maisa De Camargo CR
 *         31000 - Added min/max length validation to the EmployeeNumber
 *         field. This change had to be done in this file because the max
 *         length validation defined in the uicfg.xml files doesn't apply for
 *         scanned data.
 *         Merged changes from 12x. Code Reviewed by Deepti Sharma.
 *    3    360Commerce 1.2         3/31/2005 4:27:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:50 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.6  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.5  2004/02/20 17:34:57  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This aisle reads in the employee number.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EmployeeNumberCapturedAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = -9126842000409057365L;

    /**
     * revision number
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * minimum accepted employee id length. See EMPLOYEE_NUMBER screen in pricinguicfg.xml
     */
    public static final int EMPLOYEE_ID_MIN_LENGTH = 1;

    /**
     * maximum accepted employee id length. See EMPLOYEE_NUMBER screen in pricinguicfg.xml
     */
    public static final int EMPLOYEE_ID_MAX_LENGTH = 10;

    /**
     * employee number label tag
     */
    public static final String EMPLOYEE_NUMBER_TAG = "EmployeeNumber";

    /**
     * employee number label
     */
    public static final String EMPLOYEE_NUMBER = "Employee Number";

    /**
     * empty label
     */
    public static final String EMPTY_LABEL = "";

    /**
     * constant to identify the number of fields on the ui
     */
    public static final int FIELD_COUNT = 4;

    /**
     * Reads in the employee number from the UI.
     * 
     * @param bus Service Bus
     */
    public void traverse(BusIfc bus)
    {
        PricingCargo cargo = (PricingCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.EMPLOYEE_NUMBER);
        PromptAndResponseModel pAndRModel = model.getPromptAndResponseModel();
        String employeeID = null;

        // all employee enter screen now can be scanned and swiped. this checks
        // if the employee was swiped and if it is, it goes to the utility manager
        // to retrieve the employee id.
        if (pAndRModel.isSwiped())
        {
            employeeID = util.getEmployeeFromModel(pAndRModel);
        }
        else
        {
            employeeID = ui.getInput();
        }

        // get input from ui
        if (isEmpNumberLengthValid(employeeID))
        {
            cargo.setEmployeeDiscountID(employeeID);

            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            logger.warn("The length of the employee ID entered was not valid: " + employeeID);
            String msg[] = new String[FIELD_COUNT];
            // initialize model bean
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("INVALID_DATA");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

            msg[0] = util.retrieveDialogText(EMPLOYEE_NUMBER_TAG, EMPLOYEE_NUMBER);

            for (int i = 1; i < FIELD_COUNT; i++)
            {
                msg[i] = EMPTY_LABEL;
            }

            dialogModel.setArgs(msg);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    /**
     * Check whether the length of the employee login id entered is valid
     * compared to the length configuration specified for EMPLOYEE_NUMBER screen
     * in pricinguicfg.xml. This is to ensure that employee login id swiped in
     * the application is validated for length in order to avoid any exception
     * while saving the transaction.
     * 
     * @param bus
     * @param employeeId
     * @return true if the length of the employeeId is valid
     */
    protected boolean isEmpNumberLengthValid(String employeeId)
    {
    	if (employeeId != null && 
    			(employeeId.length() < EMPLOYEE_ID_MIN_LENGTH || employeeId.length() > EMPLOYEE_ID_MAX_LENGTH))
    		return false;

    	return true;
    }
}
