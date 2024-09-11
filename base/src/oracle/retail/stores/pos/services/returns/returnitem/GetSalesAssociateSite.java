/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/GetSalesAssociateSite.java /main/11 2011/02/16 09:13:29 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:17:06  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/13 01:35:19  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/02/23 14:58:52  baa
 *   @scr 0 cleanup javadocs
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 15 2003 14:46:28   baa
 * allow alphanumeric values on sale associate field
 * Resolution for 3121: sales associate field not editable
 * 
 *    Rev 1.0   Apr 29 2002 15:05:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:52   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:08   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.EmployeeTransaction;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site gets the sales associate associated with the return.
 * 
 * @version $Revision: /main/11 $
 */
public class GetSalesAssociateSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 978519529134572370L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/11 $";

    /**
     * This site attempts to get the sales associate entered in the return info
     * screen. It also sets the transferCargo flag to true; if the process gets
     * this point, we want the application to add this item to the temporary
     * transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        // Set the flag that forces the transfer of cargo to the calling
        // service.
        cargo.setTransferCargo(true);

        /*
         * Lookup the employee ID in the database
         */
        String salesAssociateID = cargo.getSalesAssociateID();

        if (salesAssociateID.length() > 0)
        {
            EmployeeTransaction empTransaction = null;

            empTransaction = (EmployeeTransaction) DataTransactionFactory
                    .create(DataTransactionKeys.EMPLOYEE_TRANSACTION);

            try
            {
                EmployeeIfc salesAssociate = empTransaction.getEmployee(salesAssociateID);
                cargo.getReturnItem().setSalesAssociate(salesAssociate);
            }
            catch (DataException de)
            {
                // log the error, but just continue if there is one
                logger.error("Error with SalesAssociateID \"" + salesAssociateID + "\".", de);
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
