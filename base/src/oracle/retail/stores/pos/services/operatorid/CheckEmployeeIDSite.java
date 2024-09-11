/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/operatorid/CheckEmployeeIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:02 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/20 14:05:40  kll
 *   @scr 4046: changed ManualEntryID and AutomaticEntryID parameters
 *
 *   Revision 1.3  2004/02/12 16:51:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jul 09 2003 13:35:30   bwf
 * Check entry type before checking cashier list.
 * Resolution for 3021: Operator ID- Manual Entry- Emp ID and Password =Yes, can login to POS using Login ID
 * 
 *    Rev 1.2   May 08 2003 11:30:58   bwf
 * Check employee id and check which id to use.
 * Resolution for 1933: Employee Login enhancements
 * 
 *    Rev 1.1   Apr 14 2003 19:02:40   pdd
 * Added login configuration checks.
 * Resolution for 1933: Employee Login enhancements
 * 
 *    Rev 1.0   Apr 29 2002 15:13:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:32:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:10:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.operatorid;

// foundation imports
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    Test the Employee ID to see if it is in the list and determine the next
    step to take.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckEmployeeIDSite extends PosSiteActionAdapter
{                                       // begin class class CheckEmployeeIDSite
    /**
       revision number
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** String Login **/
    public String loginStr = "User";
    
    /** String Employee **/
    public String employeeStr = "Employee";

    //----------------------------------------------------------------------
    /**
       Test the Employee ID to see if it is in the list and determine the next
       step to take.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Get the cargo
        OperatorIdCargo cargo = (OperatorIdCargo) bus.getCargo();
        EmployeeIfc employees[] = cargo.getEmployees();
        String id = cargo.getEmployeeID();
        String letter = "Password";
        boolean employeeInList = false;
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = (POSBaseBeanModel) ui.getModel(POSUIManagerIfc.OPERATOR_IDENTIFICATION);
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();   
        String passwordParm;
        String idParm;
        
        if (parModel.isScanned() || parModel.isSwiped())
        {
            passwordParm = "AutomaticEntryRequiresPassword";
            idParm = "AutomaticEntryID";
        }
        else
        {
            passwordParm = "ManualEntryRequiresPassword";
            idParm = "ManualEntryID";
        }        

        try
        {
            cargo.setIDType(pm.getStringValue(idParm));
        }
        catch (ParameterException pe)
        {
            System.out.println("*** error getting parameter" + idParm);
        }

        // Look for the employee in the list
        if (employees != null)
        {
            for (int i = 0; i < employees.length; i++)
            {
// NEED TO ADD COMPARISON OF EMPLOYEE ID
                if ((id.equals(employees[i].getLoginID()) &&
                     cargo.getIDType().equals(loginStr)) ||
                    (id.equals(employees[i].getEmployeeID()) &&
                     cargo.getIDType().equals(employeeStr)))
                {
                    cargo.setSelectedEmployee(employees[i]);
                    // Look for an active employee
                    if (employees[i].getLoginStatus() == EmployeeIfc.LOGIN_STATUS_ACTIVE)
                    {
                        employeeInList = true;
                    }
                    // fall out
                    i = employees.length;
                }
            }
        }

        if (employeeInList && !cargo.getPasswordRequiredWithList())
        {
            letter = "Success";
        }
        else
        {
        
            try
            {
                // If password is not required based on parameter
                // and it's not an override
                if (!"Y".equalsIgnoreCase(pm.getStringValue(passwordParm))
                    && !cargo.getSecurityOverrideFlag())
                {
                    letter = "Validate";
                    cargo.setPasswordRequired(false);
                }
            }
            catch (ParameterException pe)
            {
                logger.warn( 
                            "Could not determine whether password is required based on " + passwordParm + ". Defaulting to requiring a password.");
            }
        }

        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    //----------------------------------------------------------------------
    /**
       Calls <code>arrive</code>
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void reset(BusIfc bus)
    {
        arrive(bus);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  CheckEmployeeIDSite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class CheckEmployeeIDSite
