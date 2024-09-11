/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/CustomerReturnShuttle.java /main/12 2012/09/12 11:57:21 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    blarsen   02/16/12 - XbranchMerge
 *                         blarsen_bug13689528-ej-issue-customer-link-uses-prev-trans-id
 *                         from rgbustores_13.4x_generic_branch
 *    blarsen   02/06/12 - Moving GENERATE_SEQUENCE_NUMBER into
 *                         UtilityManagerIfc.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         8/10/2006 11:17:00 AM  Brendan W. Farrell
 *         16500 -Merge fix from v7.x.  Maintain sales associate to be used in
 *          reporting.
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/27 22:32:03  bwf
 *   @scr 7244 Merged 2 versions of abstractfinancialcargo.
 *
 *   Revision 1.4  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:20:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:04   msg
 * Initial revision.
 *
 *    Rev 1.1   23 Oct 2001 16:54:40   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:21:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;
// foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
    Transfer necessary data from the Find Customer service back to the
    Find Layaway service.
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7044426180097411172L;

    /**
        revision number supplied by source-code-control system
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";

    /**
       the cargo being passed from the Find Customer service to the Find Layaway
       service
    **/
    protected CustomerMainCargo customerCargo = null;

    //----------------------------------------------------------------------
    /**
       This method saves the CustomerMainCargo of the bus.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        customerCargo = (CustomerMainCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       This method stores attributes from the CustomerMainCargo to the
       LayawayCargo of the bus.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerCargo.getCustomer();
        LayawayCargo cargo = (LayawayCargo)bus.getCargo();
        cargo.setDataExceptionErrorCode(customerCargo.getDataExceptionErrorCode());
        // if the user wanted to link the customer with the layaway
        if ((customerCargo.isLink()) && (customer != null))
        {

            AbstractFinancialCargo afCargo = (AbstractFinancialCargo)bus.getCargo();
            cargo.setCustomer(customer);

            TransactionIfc transaction = cargo.getSeedLayawayTransaction();
            if (transaction == null)
            {
                TransactionUtilityManagerIfc utility =
                        (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
                // Create transaction; the initializeTransaction() method is called
                // on UtilityManager
                transaction = DomainGateway.getFactory().getTransactionInstance();
                transaction.setCashier(afCargo.getOperator());
                if(afCargo.getSalesAssociate() != null)
                {
                    transaction.setSalesAssociate(afCargo.getSalesAssociate());
                }
                // this method does not exist in Transaction
                // ensure it is set in whatever uses the seed transaction
                // if necessary.
                utility.initializeTransaction(transaction, UtilityManagerIfc.GENERATE_SEQUENCE_NUMBER, customer.getCustomerID());
                cargo.setSeedLayawayTransaction(transaction);
            }

            CustomerUtilities.journalCustomerExit
              (bus, transaction.getCashier().getEmployeeID(),
               transaction.getTransactionID());

        }

    } // unload

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // result string
        String strResult = new String("Class:  " + getClass().getName() +
                                      " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
