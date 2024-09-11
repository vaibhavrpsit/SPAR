/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeeadd/EmployeeAddLookupSite.java /main/12 2011/12/05 12:16:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:56 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:16 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/15 16:55:29  tmorris
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
 *    Rev 1.0   Aug 29 2003 15:59:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Feb 26 2003 12:06:30   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.1   Dec 18 2002 17:40:20   baa
 * add employee preferred locale support
 * Resolution for POS SCR-1843: Multilanguage support
 * 
 *    Rev 1.0   Apr 29 2002 15:24:04   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:23:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:07:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeeadd;

import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeFindForUpdateTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeUtilities;

/**
 * The Employee Lookup site accesses the employee data to see if the entered
 * name matches an employee name that is already in the database. If the
 * employee Login ID already exists in the system, the system displays an error
 * and returns to the EmployeeMaster screen. If the name is a duplicate, but the
 * ID is unique, the system will proceed to the EmployeeMatchsFound site. So
 * there.
 * 
 * @version $Revision: /main/12 $
 */
@SuppressWarnings("serial")
public class EmployeeAddLookupSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "EmployeeAddLookupSite";

    /**
     * The EmployeeAddLookupSite does a lookup to see if an employee with the
     * same name (first and last) is already in the database.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Letter result = new Letter(EmployeeCargo.SAVE);
        Vector<EmployeeIfc> employeeVector = null;

        EmployeeIfc[] employeeArray;

        int maximumMatches; // maximum matches returned for the employee
        int index; // index used to copy array into vector

        EmployeeCargo cargo = (EmployeeCargo)bus.getCargo();
        EmployeeFindForUpdateTransaction empTransaction = null;

        empTransaction = (EmployeeFindForUpdateTransaction)DataTransactionFactory
                .create(DataTransactionKeys.EMPLOYEE_FIND_FOR_UPDATE_TRANSACTION);

        try
        {
            // check for name depending on what's entered.
            SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
            // set locale
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            LocaleRequestor requestor = new LocaleRequestor(locale);
            inquiry.setLocaleRequestor(requestor);
            inquiry.setEmployee(cargo.getEmployee());

            // log the error; set the error code in the cargo for future use;
            // create letter.
            employeeArray = empTransaction.selectEmployees(inquiry);
            // check for maximum matches here the database will return a vector
            // of employees, check the number of entries in the vector against
            // the maximum integer number. if appropriate, set:
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

            maximumMatches = EmployeeUtilities.getMaximumMatches(pm, SITENAME, cargo.getOperatorID());
            int length = employeeArray.length;
            if (length > maximumMatches)
            {
                // if too many matches were found
                result = new Letter(EmployeeCargo.TOO_MANY);
            }
            else
            {
                // if multiple matches were found, but not too many to list
                employeeVector = new Vector<EmployeeIfc>(length);

                // copy the result array into the Vector
                for (index = 0; index < length; index++)
                { // Begin copy the array elements into the vector
                    employeeVector.add(employeeArray[index]);

                } // End copy the array elements into the vector

                cargo.setEmployeeList(employeeVector);
                result = new Letter(EmployeeCargo.MATCH);
            }
        }
        catch (DataException de)
        {
            // log the error; set the error code in the cargo for future use.
            cargo.setDataExceptionErrorCode(de.getErrorCode());

            // if no matches were found, on add that's not an error. Go on.
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                result = new Letter(EmployeeCargo.SAVE);
                cargo.setFatalError(false);
            }
            else
            {
                // take care of database errors
                cargo.setDataExceptionErrorCode(de.getErrorCode());
                cargo.setFatalError(true);
                result = new Letter(CommonLetterIfc.DB_ERROR);

                logger.error("Employee error.", de);
            }
        }

        // Mail the appropriate result to continue
        bus.mail(result, BusIfc.CURRENT);
    }
}
