/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CustomerReturnShuttle.java /main/17 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    blarsen   02/16/12 - XbranchMerge
 *                         blarsen_bug13689528-ej-issue-customer-link-uses-prev-trans-id
 *                         from rgbustores_13.4x_generic_branch
 *    blarsen   02/06/12 - Moving GENERATE_SEQUENCE_NUMBER into
 *                         UtilityManagerIfc.
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    asinton   12/11/09 - Implemented EJournaling of Price Promotion for items
 *                         including changes to price by linking a customer.
 *    kulu      02/27/09 - POSPAL locale should be device locale if customer
 *                         preferred is not specified.
 *    kulu      02/27/09 - POSPAL locale should be device locale if customer
 *                         preferred is not specified.
 *    kulu      02/27/09 - POSPAL locale should be device locale if customer
 *                         preferred is not specified.
 *    asinton   11/25/08 - Intermittent check in.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:41 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Nov 07 2003 12:36:30   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 *
 *    Rev 1.0   Nov 05 2003 14:13:54   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

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

/**
 * Transfer necessary data from the Customer service back to the POS service.
 */
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

    /**
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        // save the cargo from the CustomerIfc service in an instance variable
        customerMainCargo = (CustomerMainCargo)bus.getCargo();
    }

    /**
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerMainCargo.getCustomer();
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        cargo.setRefreshNeeded(true);
        SaleReturnTransactionIfc transaction = cargo.getTransaction();

        // if the user wanted to link the customer with the transaction
        if (customerMainCargo.isLink() && (customer != null))
        {
            // set the CustomerIfc reference within the transaction
            if (transaction == null)
            {
                // Create transaction; the initializeTransaction() method is
                // called
                // on UtilityManager
                transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
                transaction.setCashier(cargo.getOperator());
                transaction.setSalesAssociate(cargo.getEmployee());
                CustomerUtilities.journalCustomerExit(bus, customerMainCargo.getEmployeeID(),
                        customerMainCargo.getTransactionID());
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
                utility.initializeTransaction(transaction, UtilityManagerIfc.GENERATE_SEQUENCE_NUMBER, customer.getCustomerID());
                cargo.setTransaction(transaction);
            }
            else
            {
                CustomerUtilities.journalCustomerLink(bus, transaction.getCashier().getEmployeeID(),
                        customer.getCustomerID(), transaction.getTransactionID());
            }
            transaction.linkCustomer(customer);
            // journal the customer pricing after customer has been linked
            // to the transaction in order to pickup the correct pricing.
            CustomerUtilities.journalCustomerPricing(bus, transaction, customer.getPricingGroupID());

            // set the customer's name in the status area
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            StatusBeanModel statusModel = new StatusBeanModel();
            String[] vars = { customer.getFirstName(), customer.getLastName() };
            UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
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
