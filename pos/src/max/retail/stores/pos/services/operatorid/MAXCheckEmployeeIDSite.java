/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * 
 * Copyright (c) 2016-2017 Max Hypermarket. All Rights Reserved. 
 * 
 * Rev 1.0  26 Oct, 2016              Nadia              MAX-POS-LOGIN-FESV1 0.doc requirement.
 * 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.operatorid;

import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.operatorid.OperatorIdCargo;

//--------------------------------------------------------------------------
/**
    Test the Employee ID to see if it is in the list and determine the next
    step to take.
    <p>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class MAXCheckEmployeeIDSite extends PosSiteActionAdapter
{                                       // begin class class CheckEmployeeIDSite
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
  
        String passwordParm = "ManualEntryRequiresPassword";
        String idParm = "ManualEntryID";

        try{
            cargo.setIDType(pm.getStringValue(idParm));
        }
        catch (ParameterException pe){
            logger.error(pe);
        }

        // Look for the employee in the list
        if (employees != null){
            for (int i = 0; i < employees.length; i++) {
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
            catch (ParameterException pe) {
				logger.warn("Could not determine whether password is required based on "
						+ passwordParm
						+ ". Defaulting to requiring a password.");
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
