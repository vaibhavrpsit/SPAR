
package max.retail.stores.pos.services.customer.common;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

// foundation imports


//--------------------------------------------------------------------------
/**
        Check the employee ID to make certain it exists.
        If it does not, send a NoMatch letter to display an
        error message. If it is, mail a Continue letter to
        go on to the LookupCustomer site.
    $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXCheckEmployeeIDSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
       Check the employee ID to make certain it exists.
       If it does not, send a Failure letter to display an
       error message. If it is, mail a Success letter to
       go on to the next site. <p>
       @param bus the bus arriving at this site
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String letterName = CommonLetterIfc.CONTINUE;

        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        String newEmployeeID = cargo.getCustomer().getEmployeeID();

        if ((newEmployeeID != null) && (newEmployeeID.length() > 0))
        {
            String originalEmployeeID = cargo.getEmployeeID();
            if (originalEmployeeID == null || !originalEmployeeID.equals(newEmployeeID) ||
                cargo.getDataExceptionErrorCode() != DataException.UNKNOWN)
            {
                try
                {
                    EmployeeTransaction et = null;
                    
                    et = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
                    
                    EmployeeIfc employee = et.getEmployeeID(newEmployeeID);
                    cargo.setEmployee(employee);

                    // if found, everything is ok, mail a SUCCESS letter to initiate a
                    // confirm dialog
                    letterName = CommonLetterIfc.SUCCESS;
                    // reset cargo
                    cargo.setDataExceptionErrorCode(DataException.UNKNOWN);
                }
                catch (DataException de)        // handle data base exceptions
                {
                    POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                    // log the error; set the error code in the cargo for future use.
                    logger.warn( "" + de + "");
                    cargo.setDataExceptionErrorCode(de.getErrorCode());
                    switch(de.getErrorCode())
                    {
                       case DataException.NO_DATA:
                       {
                           UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "EMPLOYEE_NO_MATCH_ID_ERROR", null,CommonLetterIfc.RETRY);
                           break;
                       }
                       default:
                       {
                           String args[] = new String[1];
                           UtilityManagerIfc utility = 
                             (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                           args[0] = 
                             utility.getErrorCodeString(cargo.getDataExceptionErrorCode());
                           UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError", args,CommonLetterIfc.FAILURE);
                       }
                    }
                    letterName = null;
                }
            }
        }

        // Mail the appropriate result to continue
        if ( letterName !=null)
        {
          bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }
    //--------------------------------------------------------------------------
    /**
       Set the args in the ui model and display the error dialog.

       @param args String array for the text to display on the dialog
       @param id String identifier for the configuration of the dialog
    **/
    //--------------------------------------------------------------------------
    protected void showDialogScreen(BusIfc bus, String[] args, String id, String letter)
    {
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(id);
        model.setType(DialogScreensIfc.ERROR);
        model.setArgs(args);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,letter);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
