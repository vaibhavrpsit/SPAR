/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/alterations/CustomerFoundRoad.java /main/13 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    asinton   03/15/12 - remove redundant call to transaction.setCustomer
 *                         when transaction.linkCustomer calls setCustomer.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   12/11/09 - Implemented EJournaling of Price Promotion for items
 *                         including changes to price by linking a customer.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:36 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:37 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:21 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:04  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:53:56   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 05 2003 18:18:12   DCobb
 * Generalized names of alterations attributes.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.1   Aug 21 2002 11:21:22   DCobb
 * Added Alterations service.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.alterations;

// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * This road is traversed when a customer has be associated with the
 * transaction.
 * It updates the UI to display the linked Customer.
 */
@SuppressWarnings("serial")
public class CustomerFoundRoad extends PosLaneActionAdapter
{
    /**
     * Updates the UI to display the linked Customer.
     * @param the service bus
     */
    public void traverse(BusIfc bus)
    {
        // Get the cargo
        AlterationsCargo cargo = (AlterationsCargo)bus.getCargo();

        // Get access to the UI Manager from the bus
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Local variables
        SaleReturnTransactionIfc transaction;
        CustomerIfc customer;

        if (cargo.isCustomerLinkRefreshUI()) // Link the customer.
        {
            if (cargo.getTransaction() != null && cargo.getCustomer() != null)
            {
                // Get the transaction from the cargo
                transaction = (SaleReturnTransactionIfc)cargo.getTransaction();

                // Get the customer from the cargo
                customer = cargo.getCustomer();

                // Link the customer
                transaction.linkCustomer(customer);

                // Set the transaction back in the cargo
                cargo.setTransaction(transaction);

                // Refresh the UI to reflect the linked Customer
                refreshUI(customer, ui);

                CustomerUtilities.journalCustomerLink(bus, cargo.getEmployeeID(),
                                                customer.getCustomerID(),
                                                cargo.getTransaction().getTransactionID());
                // journal the customer pricing after customer has been linked
                // to the transaction in order to pickup the correct pricing. 
                CustomerUtilities.journalCustomerPricing(bus, transaction, customer.getPricingGroupID());
                CustomerUtilities.journalCustomerExit(bus, cargo.getEmployeeID(),
                                                cargo.getTransaction().getTransactionID());
            }
        }

    }

    /**
     * Refresh the UI so that the linked customer is displayed in the
     * status panel.
     * @param the customer
     * @param the user interface manager
     */
    public void refreshUI(CustomerIfc customer, POSUIManagerIfc ui)
    {
        // Construct the models
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        StatusBeanModel sbModel = new StatusBeanModel();

        // Set the customer's name in the status area
        sbModel.setCustomerName(customer.getFirstLastName());

        // set the statusModel in the beanModel
        beanModel.setStatusBeanModel(sbModel);

        // Set and show the model
        ui.showScreen(POSUIManagerIfc.ALTERATION_TYPE, beanModel);
    }
}

