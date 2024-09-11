/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/DisplayTemporaryPasswordSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * $ 7    360Commerce 1.6         10/12/2006 8:17:49 AM  Christian Greene
 * $      Adding new functionality for PasswordPolicy.  Employee password will
 * $       now be persisted as a byte[] in hexadecimal.  Updates include UI
 * $      changes, persistence changes, and AppServer configuration changes.
 * $      A database rebuild with the new SQL scripts will be required.
 * $ 6    360Commerce 1.5         9/27/2006 4:58:07 PM   Christian Greene Add
 * $      functionality to display temp password after adding a standard
 * $      employee
 * $ 5    360Commerce 1.4         9/27/2006 4:56:16 PM   Christian Greene 
 * $ 4    360Commerce 1.3         9/26/2006 3:37:35 PM   Christian Greene
 * $      update renamed UpdateEmployeePasswordAdaptor
 * $ 3    360Commerce 1.2         9/26/2006 3:32:06 PM   Christian Greene 
 * $ 2    360Commerce 1.1         9/26/2006 3:31:14 PM   Christian Greene 
 * $ 1    360Commerce 1.0         9/26/2006 12:14:18 PM  Christian Greene 
 * $$ 6    360Commerce1.5         9/27/2006 4:58:07 PM   Christian Greene Add
 * $      functionality to display temp password after adding a standard
 * $      employee
 * $ 5    360Commerce1.4         9/27/2006 4:56:16 PM   Christian Greene 
 * $ 4    360Commerce1.3         9/26/2006 3:37:35 PM   Christian Greene update
 * $      renamed UpdateEmployeePasswordAdaptor
 * $ 3    360Commerce1.2         9/26/2006 3:32:06 PM   Christian Greene 
 * $ 2    360Commerce1.1         9/26/2006 3:31:14 PM   Christian Greene 
 * $ 1    360Commerce1.0         9/26/2006 12:14:18 PM  Christian Greene 
 * $$$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import oracle.retail.stores.pos.services.common.EmployeeCargoIfc;
import oracle.retail.stores.pos.services.common.PasswordCargoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This message is displayed if an employee has been succesfully added to the
 * system and now his temporary password needs to be displayed for recording.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class DisplayTemporaryPasswordSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 536597804183000478L;

    public static final String SITENAME = "DisplayTemporaryPasswordSite";

    /**
     * Arrive at the site. Display the temp password.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Retrieve the data from cargo
        EmployeeCargoIfc employeeCargo = (EmployeeCargoIfc) bus.getCargo();
        EmployeeIfc employee = employeeCargo.getEmployee();
        String name = employee.getPersonName().getFirstLastName();

        PasswordCargoIfc passwordCargo = (PasswordCargoIfc) bus.getCargo();
        String newPassword = passwordCargo.getPlainTextPassword();

        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("DisplayTemporaryPassword");
        model.setArgs(new String[] { name, newPassword, name });
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
