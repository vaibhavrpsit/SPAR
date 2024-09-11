/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/CheckDuplicateIDSite.java /main/13 2013/04/05 11:47:28 abhineek Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhineek  04/05/13 - fix to avoid creation of more than one employee with
 *                         same login id
 *    rsnayak   08/03/10 - Edit Employee Fix
 *   
 *    rsnayak   07/19/10 - Employee Edit Fix
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.7  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.6  2004/05/07 15:22:01  tfritz
 *   @scr 4219 Do not check for duplicate ID in training mode
 *
 *   Revision 1.5  2004/04/15 16:54:31  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:49:24  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;
// java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeFindForUpdateTransaction;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
//------------------------------------------------------------------------------
/**
    The CheckDuplicateID site checks to see if the Employee Login Id entered
    duplicates one already in the system. If so, the user will be informed and
    returned to EmployeeMaster. Otherwise the flow continues to
    EmployeeAddLookup.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------

public class CheckDuplicateIDSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "CheckDuplicateIDSite";

    //--------------------------------------------------------------------------
    /**
       The CheckDuplicateID site checks to see if the Employee Login Id
       entered duplicates one already in the system.  If so, the user will
       be informed and returned to EmployeeMaster.   Otherwise the flow
       continues to EmployeeAddLookup.

       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {

        Letter result = new Letter(EmployeeCargo.SAVE);

        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();

        RegisterIfc register = cargo.getRegister();
        boolean trainingModeOn = false;

        if (register != null)
        {
            trainingModeOn = register.getWorkstation().isTrainingMode();
        }

        if (!trainingModeOn)
        {

            EmployeeFindForUpdateTransaction empTransaction = null;

            empTransaction = (EmployeeFindForUpdateTransaction)DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_FIND_FOR_UPDATE_TRANSACTION);

            try
            {
                // Added to check for duplicate Employee Login ID in DB when editing an Employee
                boolean isEmployeeIDEdited = true;
                if (cargo.getOriginalEmployee() != null)
                {

                    if ((cargo.getEmployee().getLoginID().equals(cargo.getOriginalEmployee().getLoginID())))
                    {
                        isEmployeeIDEdited = false;

                    }
                }

                if (isEmployeeIDEdited)
                {
                    empTransaction.getEmployee(cargo.getEmployee().getLoginID());

                    // if the ID matches, in the case of an add, it's an error.
                    // Send a duplicate ID letter
                    result = new Letter(EmployeeCargo.DUPLICATE_ID);

                    // Duplicate ID is not fatal to this service. Set fatalError to false.
                    // This is used to handle the OK
                    cargo.setFatalError(false);
                }

            } // end employee read try block
            catch (DataException de)
            {
                // log the error; set the error code in the cargo for future use.
                logger.error("Employee error: " + de.getMessage() + "");
                cargo.setDataExceptionErrorCode(de.getErrorCode());

                // if no matches were found, on add that's not an error. Go on.
                if (de.getErrorCode() == DataException.NO_DATA)
                {
                    result = new Letter(EmployeeCargo.SAVE);
                }
                else
                { // take care of database errors

                    cargo.setDataExceptionErrorCode(de.getErrorCode());
                    cargo.setFatalError(true);
                    result = new Letter(CommonLetterIfc.DB_ERROR);

                }
            }

        }

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);

    }

} // end class CheckDuplicateIDSite
