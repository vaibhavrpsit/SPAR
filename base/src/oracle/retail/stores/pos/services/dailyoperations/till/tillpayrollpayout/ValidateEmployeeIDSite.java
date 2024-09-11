/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayrollpayout/ValidateEmployeeIDSite.java /main/10 2011/02/16 09:13:26 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:28 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.3  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.2  2004/04/12 18:49:35  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.1  2004/03/12 18:19:23  khassen
 *   @scr 0 Till Pay In/Out use case
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:05:10   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Nov 04 2003 19:00:14   cdb
 * Initial revision.
 * Resolution for 3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpayrollpayout;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Validates the employee ID.
 *
 * @author khassen
 */
public class ValidateEmployeeIDSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -2002132406123530043L;

    public static final String revisionNumber = "$Revision: /main/10 $";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Get the employee ID.
        TillPayrollPayOutCargo cargo = (TillPayrollPayOutCargo)bus.getCargo();
        String employeeID = cargo.getEmployeeID();
        EmployeeIfc employee = null;
        LetterIfc letter = null;

        // Look up employee in db.
        try
        {
            EmployeeTransaction empTransaction = null;
            empTransaction = (EmployeeTransaction) DataTransactionFactory.create(DataTransactionKeys.EMPLOYEE_TRANSACTION);
            
            employee = empTransaction.getEmployee(employeeID);
            letter = new Letter(CommonLetterIfc.SUCCESS);
        }
        catch (DataException de)
        {
            // log the error; set the error code in the cargo for future use.
            logger.error(
                         "EmployeeID '" + employeeID + "' error: " + de.getMessage() + "\n\t Error code = " +
                         Integer.toString(de.getErrorCode()) + "");
            cargo.setDataExceptionErrorCode(de.getErrorCode());
            letter = new Letter(CommonLetterIfc.FAILURE);
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}
