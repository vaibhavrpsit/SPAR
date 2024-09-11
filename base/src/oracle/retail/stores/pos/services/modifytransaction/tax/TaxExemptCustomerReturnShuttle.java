/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/TaxExemptCustomerReturnShuttle.java /main/18 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    blarsen   02/06/12 - Moving GENERATE_SEQUENCE_NUMBER into
 *                         UtilityManagerIfc.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    asinton   12/11/09 - Implemented EJournaling of Price Promotion for items
 *                         including changes to price by linking a customer.
 *    deghosh   10/29/08 - EJI18n_changes_ExtendyourStore
 *    acadar    10/14/08 - unit test fixes
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:30:19 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:25:46 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:41 PM  Robert Pearse
     $
     Revision 1.4  2004/02/24 16:21:31  cdb
     @scr 0 Remove Deprecation warnings. Cleaned code.

     Revision 1.3  2004/02/12 16:51:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:37  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:02:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 30 2003 08:58:06   baa
 * display tax cert # if available
 *
 *    Rev 1.1   Aug 12 2002 14:47:12   jriggins
 * Getting customer's name from the resource bundle
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:15:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:39:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

/**
 * Return shuttle class for ModifyTransactionTax service.
 */
public class TaxExemptCustomerReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 265464464243L;

    protected static final Logger logger = Logger.getLogger(TaxExemptCustomerReturnShuttle.class);

    protected CustomerIfc customer = null;

    protected SaleReturnTransactionIfc transaction = null;

    protected boolean transactionCreated = false;

    /**
     * customer cargo
     */
    protected CustomerMainCargo customerMainCargo = null;

    /**
     * customer name bundle tag.
     */
    public static final String CUSTOMER_NAME_TAG = "CustomerName";

    /**
     * customer name default text.
     */
    public static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    /**
     * Loads from child (Customer) cargo class.
     *
     * @param b bus interface
     */
    public void load(BusIfc bus)
    {
        // retrieve cargo
        customerMainCargo = (CustomerMainCargo)bus.getCargo();
    }

    /**
     * Unloads to parent (ModifyTransaction) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of ModifyTransaction class
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>Cargo unloaded
     * </UL>
     *
     * @param b bus interface
     */
    @Override
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerMainCargo.getCustomer();
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo)bus.getCargo();
        SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)cargo.getTransaction();

        // if customer was link pass along the info
        if (customerMainCargo.isLink() && (customer != null))
        {
            // retrieve cargo
            if (transaction == null)
            {
                TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
                // Create transaction; the initializeTransaction() method is
                // called
                // on utility manager
                transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
                transaction.setCashier(cargo.getOperator());
                transaction.setSalesAssociate(cargo.getSalesAssociate());
                utility.initializeTransaction(transaction, TransactionUtilityManagerIfc.GENERATE_SEQUENCE_NUMBER, customer.getCustomerID());
                cargo.setTransactionCreated(true);
            }
            else
            {

                CustomerUtilities.journalCustomerLink(bus, transaction.getCashier().getEmployeeID(), customer
                        .getCustomerID(), transaction.getTransactionID());
            }

            // set the CustomerIfc reference within the transaction
            cargo.setCustomerLinked(true);
            cargo.setDirtyFlag(true);
            transaction.linkCustomer(customer);
            // journal the customer pricing after customer has been linked
            // to the transaction in order to pickup the correct pricing.
            CustomerUtilities.journalCustomerPricing(bus, transaction, customer.getPricingGroupID());
            cargo.setCustomer(customer);
            cargo.setTransaction(transaction);
            // journal discounts as needed
            CustomerUtilities.journalCustomerExit(bus, transaction.getCashier().getEmployeeID(), transaction
                    .getTransactionID());
            CustomerGroupIfc[] customerGroups = customer.getCustomerGroups();
            DiscountRuleIfc[] discounts = null;
            if (customerGroups != null && customerGroups.length > 0)
            {
                customer.getCustomerGroups()[0].getDiscountRules();
                discounts = customerGroups[0].getDiscountRules();
            }
            if (discounts != null && discounts.length > 0)
            {
                JournalManagerIfc mgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                DiscountRuleIfc rule = discounts[0];
//                String message = "\nTRANS: Discount" + "\n  Discount % " + 100.0 * rule.getDiscountRate().doubleValue()
//                        + "\n  Disc. Rsn. " + rule.getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                //
                String message = "\n"+I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PRICING_TRANS_DISCOUNT_LABEL, null);
                message += "\n"+I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SPECIAL_ORDER_DISCOUNT_PERCENT, null);
                message += 100.0 * rule.getDiscountRate().doubleValue();
                message += "\n";
                message += I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SPECIAL_ORDER_DISCOUNT_REASON, null);
                message += rule.getName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));


                mgr.journal(transaction.getCashier().getEmployeeID(), transaction.getTransactionID(), message);
            }

            // set the customer's name in the status area
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            StatusBeanModel statusModel = new StatusBeanModel();
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                    CUSTOMER_NAME_TAG, CUSTOMER_NAME_TEXT);
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            String customerName = LocaleUtilities.formatComplexMessage(pattern, parms);
            statusModel.setCustomerName(customerName);

            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            baseModel.setStatusBeanModel(statusModel);
            ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);

        }

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.services.common.FinancialCargoShuttle#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  TaxExemptCustomerReturnShuttle (Revision " + getRevisionNumber() + ")"
                + hashCode());

        return (strResult);
    }

    /**
     * Returns the revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
