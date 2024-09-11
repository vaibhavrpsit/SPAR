
package max.retail.stores.pos.services.customer.common;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Prompt the user for confirmation of their intent
    to include this employee in customer record.
    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXConfirmEmployeeSite extends PosSiteActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
        Prompts for confirmation of Employee.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        EmployeeIfc employee = cargo.getEmployee();

        String args[] = new String[2];
        args[0] = employee.getPersonName().getFirstName();
        args[1] = employee.getPersonName().getLastName();

                // setup the model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("CustomerEmployeeConfirmation");
        model.setType(DialogScreensIfc.CONFIRMATION);
        model.setArgs(args);

                // display the screen
                POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
