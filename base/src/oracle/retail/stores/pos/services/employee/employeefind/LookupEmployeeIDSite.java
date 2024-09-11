/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/LookupEmployeeIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *   Revision 1.9  2004/06/24 15:10:17  awilliam
 *   @scr 5726 search for standard employee by id crashes system
 *
 *   Revision 1.8  2004/06/18 22:19:01  tmorris
 *   @scr 4278 -Changed variable name.
 *
 *   Revision 1.7  2004/06/04 12:43:13  tmorris
 *   @scr 4278 -Created a check to ensure employee exp. dates are verified against the current date.
 *
 *   Revision 1.6  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/15 16:57:43  tmorris
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
 *    Rev 1.0   Aug 29 2003 15:59:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:23:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:32:26   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:23:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeFindForUpdateTransaction;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
//------------------------------------------------------------------------------
/**

               The LookupEmployee site looks up the employee based
               on the information returned by the UI from the FindEmployeeID
               site. The database lookup done on the employee ID.
               If there was no match for the id, an appropriate error
               message is displayed, and the user is returned to
               the FindEmployeeInformation site to modify the
               search information. If there was a database error,
               that message is displayed.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class LookupEmployeeIDSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "LookupEmployeeIDSite";

    //--------------------------------------------------------------------------
    /**
       The LookupEmployee site looks up the employee based
       on the information returned by the UI from the FindEmployeeID
       site. The database lookup done on the employee ID.
       If there was no match for the id, an appropriate error
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
        EmployeeIfc employee = null;

        int length;                        // length of customer array
        int index;                        // index used to copy array into vector

        // Status flag
        boolean bOk = true;

        EmployeeCargo cargo = (EmployeeCargo) bus.getCargo();

        EmployeeFindForUpdateTransaction empTransaction = null;

        empTransaction = (EmployeeFindForUpdateTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_FIND_FOR_UPDATE_TRANSACTION);

        try
        {                               // begin employee read try block

            // check for name depending on what's entered.

            // log the error; set the error code in the cargo for futur use; create letter.
            employee = empTransaction.getEmployeeID(cargo.getEmployee().getEmployeeID());

            // Get employee expiration date
            EYSDate expDateCheck = employee.getExpirationDate();

            // Get the current system date
            EYSDate systemDate = DomainGateway.getFactory().getEYSDateInstance();

            // If the employee expiration date is before the current system date then send NO MATCH letter
            if (expDateCheck != null)
            {

                if(!expDateCheck.before(systemDate))
                {
                    cargo.setEmployee(employee);
                    cargo.setOriginalEmployee((EmployeeIfc)cargo.getEmployee().clone());
                }
                else
                {
                    result = new Letter(EmployeeCargo.NO_MATCH);
                }
            }
            else
            {
                cargo.setEmployee(employee);
                cargo.setOriginalEmployee((EmployeeIfc)cargo.getEmployee().clone());
            }
        }                               // end employee read try block

        catch (DataException de)        // handle data base exceptions
        {                               // begin  data base exception catch
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
        }                               // end database error catch

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);


    }
}  // end class LookupEmployeeIDSite
