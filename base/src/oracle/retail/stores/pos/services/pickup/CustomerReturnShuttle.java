/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pickup/CustomerReturnShuttle.java /main/1 2012/09/12 11:57:17 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    blarsen   02/06/12 - Moving GENERATE_SEQUENCE_NUMBER into
 *                         UtilityManagerIfc.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    asinton   12/11/09 - Implemented EJournaling of Price Promotion for items
 *                         including changes to price by linking a customer.
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/18/08 - Customer Return Shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pickup;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

import org.apache.log4j.Logger;

public class CustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7908464367443784886L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(CustomerReturnShuttle.class);

    /**
     * the customer main cargo
     */
    protected CustomerMainCargo customerMainCargo = null;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        // save the cargo from the CustomerIfc service in an instance variable
        customerMainCargo = (CustomerMainCargo) bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        CustomerIfc customer = customerMainCargo.getCustomer();
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        //pickupDeliveryOrderCargo.setRefreshNeeded(true);
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)pickupDeliveryOrderCargo.getTransaction();
        pickupDeliveryOrderCargo.setCustomer(customerMainCargo.getCustomer());
        // if the user wanted to link the customer with the transaction
        if (customerMainCargo.isLink() && (customer != null))
        {
            // set the CustomerIfc reference within the transaction
            if (transaction == null)
            {
                // Create transaction; the initializeTransaction() method is called
                // on UtilityManager
                transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
                transaction.setCashier(pickupDeliveryOrderCargo.getOperator());
                transaction.setSalesAssociate(pickupDeliveryOrderCargo.getSalesAssociate());
                CustomerUtilities.journalCustomerExit(bus, customerMainCargo.getEmployeeID(), customerMainCargo
                        .getTransactionID());
                utility.initializeTransaction(transaction, TransactionUtilityManagerIfc.GENERATE_SEQUENCE_NUMBER, customer.getCustomerID());
                pickupDeliveryOrderCargo.setTransaction(transaction);

            }
            else
            {
                CustomerUtilities.journalCustomerLink(bus, transaction.getCashier().getEmployeeID(), customer
                        .getCustomerID(), transaction.getTransactionID());
            }
            transaction.linkCustomer(customer);
            // journal the customer pricing after customer has been linked
            // to the transaction in order to pickup the correct pricing.
            CustomerUtilities.journalCustomerPricing(bus, transaction, customer.getPricingGroupID());

            // set the customer's name in the status area
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            StatusBeanModel statusModel = new StatusBeanModel();
            String[] vars = { customer.getFirstName(), customer.getLastName() };
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String pattern = util.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    TagConstantsIfc.CUSTOMER_NAME_TAG, TagConstantsIfc.CUSTOMER_NAME_PATTERN_TAG);
            String customerName = LocaleUtilities.formatComplexMessage(pattern, vars);
            statusModel.setCustomerName(customerName);
            LineItemsModel baseModel = new LineItemsModel();
            baseModel.setStatusBeanModel(statusModel);
            ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);

        }
        CustomerUtilities.journalCustomerExit(bus, customerMainCargo.getEmployeeID(), customerMainCargo.getTransactionID());
    }

}
