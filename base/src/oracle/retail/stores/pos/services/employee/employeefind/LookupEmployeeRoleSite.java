/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/LookupEmployeeRoleSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:20 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/15 20:18:44  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Jan 26 2004 15:57:48   jriggins
 * Initial revision.
 * Resolution for 3597: Employee 7.0 Updates
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

// foundation imports
import java.util.Vector;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeFindForUpdateTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EmployeeLookupRoleBeanModel;
//------------------------------------------------------------------------------
/**

               The LookupEmployee site looks up the employee based
               on the information returned by the UI from the FindEmployeeRole
               site. The database lookup done on the employee Role.
               If there was no match for the Role, an appropriate error
               message is displayed, and the user is returned to
               the FindEmployeeInformation site to modify the
               search information. If there was a database error,
               that message is displayed.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class LookupEmployeeRoleSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "LookupEmployeeRoleSite";

    //--------------------------------------------------------------------------
    /**
       The LookupEmployee site looks up the employee based
       on the information returned by the UI from the FindEmployeeRole
       site. The database lookup done on the employee Role.
       If there was no match for the Role, an appropriate error
       message is displayed, and the user is returned to
       the FindEmployeeInformation site to modify the
       search information. If there was a database error,
       that message is displayed.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        // default the letter to continue
        Letter result = new Letter(CommonLetterIfc.CONTINUE);
        Vector employees = null;
        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();
        
        EmployeeFindForUpdateTransaction empTransaction = null;
        
        empTransaction = (EmployeeFindForUpdateTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_FIND_FOR_UPDATE_TRANSACTION);
        
        try
        {                              
            // search for employees by role
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            EmployeeLookupRoleBeanModel beanModel = (EmployeeLookupRoleBeanModel)ui.getModel();
            RoleIfc selectedRole = beanModel.getSelectedRole();            
            employees = empTransaction.selectEmployeesByRole(selectedRole);

            // process the results for viewing
            if (employees != null)
            {
                // display the list of possible matches if more than one match is
                // found
                if (employees.size() > 1)
                {    
                    cargo.setEmployeeList(employees);
                    result = new Letter(EmployeeCargo.POSSIBLE_MATCHES);
                }
                // go directly to the employee edit screen if only one match 
                // is found                
                else
                {    
                    cargo.setEmployee((EmployeeIfc)employees.get(0));
                    cargo.setOriginalEmployee((EmployeeIfc)cargo.getEmployee().clone());
                    result = new Letter(EmployeeCargo.SINGLE_MATCH);
                }
            }
        }
        catch (DataException de)        
        {                               
            // log the error; set the error code in the cargo for future use.
            cargo.setDataExceptionErrorCode(de.getErrorCode());

            // if no matches were found, on add that's not an error.  Go on.
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                result = new Letter(EmployeeCargo.NO_MATCH);
                cargo.setFatalError(false);
            }
            else
            {    // take care of database errors

                cargo.setDataExceptionErrorCode(de.getErrorCode());
                cargo.setFatalError(true);
                result = new Letter(CommonLetterIfc.DB_ERROR);

                logger.error( "Employee error: " + de.getMessage() + "");
            }
        }                               

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);
    }
}  // end class LookupEmployeeRoleSite
