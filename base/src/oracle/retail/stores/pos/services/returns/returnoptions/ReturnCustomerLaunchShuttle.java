/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ReturnCustomerLaunchShuttle.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:52 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/16 13:36:40  baa
 *   @scr  3561 returns enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:42:40   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:06:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Mar 2002 16:13:14   baa
 * add Entering/Exiting Custmer msg to journal
 * Resolution for POS SCR-648: Customer Find not journaling Entering and Exiting Customer during MBC
 *
 *    Rev 1.0   Mar 18 2002 11:46:24   msg
 * Initial revision.
 *
 *    Rev 1.2   08 Feb 2002 14:08:52   baa
 * test
 * Resolution for POS SCR-1202: Return by item requiring Customer hangs on Customer Contact
 *
 *    Rev 1.1   08 Feb 2002 14:08:06   baa
 * testing
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 * Resolution for POS SCR-1202: Return by item requiring Customer hangs on Customer Contact
 *
 *    Rev 1.0   Sep 21 2001 11:25:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnLaunchShuttle;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;

/**
 * This shuttle updates the child cargo (ReturnCustomerCargo) with
 * information from the parent cargo (ReturnOptionsCargo).
 */
public class ReturnCustomerLaunchShuttle extends AbstractReturnLaunchShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2379749125633961175L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(ReturnCustomerLaunchShuttle.class);

    /**
     * Return options cargo
     */
    ReturnOptionsCargo roCargo = null;

    /**
     * Copies information needed from parent service.
     * 
     * @param bus Parent Service Bus.
     */
    @Override
    public void load(BusIfc bus)
    {

        // Perform FinancialCargoShuttle load
        super.load(bus);

        // retrieve cargo from the parent service
        roCargo = (ReturnOptionsCargo)bus.getCargo();

    }

    /**
     * Stores information needed by child service.
     * 
     * @param bus Child Service Bus.
     */
    @Override
    public void unload(BusIfc bus)
    {
        // Perform FinancialCargoShuttle unload
        super.unload(bus);

        // retrieve cargo from the child
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        // set the return transaction id in the options cargo
        String transactionID = null;
        if (roCargo.getTransaction() != null)
        {
            cargo.setCustomer(roCargo.getTransaction().getCustomer());
            transactionID = roCargo.getTransaction().getTransactionID();
        }
        else
        {
            cargo.setCustomer(null);
        }

        cargo.setOriginalReturnTransactions(roCargo.getOriginalReturnTransactions());
        cargo.setOriginalExternalOrderReturnTransactions(
                roCargo.getOriginalExternalOrderReturnTransactions());
        cargo.setReturnData(roCargo.getReturnData());

        // set the serach criteria
        SearchCriteriaIfc searchCriteria = roCargo.getSearchCriteria();
        if (searchCriteria != null)
        {
            cargo.setSearchCriteria(searchCriteria);
            cargo.setCustomer(searchCriteria.getCustomer());
        }
        CustomerUtilities.journalCustomerEnter(bus, roCargo.getOperator().getEmployeeID(), transactionID);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  ReturnCustomerLaunchShuttle (Revision " + ")" + hashCode());

        // pass back result
        return (strResult);
    }
}
