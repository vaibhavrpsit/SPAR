/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionSendLaunchShuttle.java /main/16 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse
 *
 *   Revision 1.2  2004/08/24 14:58:51  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.1  2004/08/09 19:17:52  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the transaction service cargo to the
 * transaction level send service cargo.
 *
 */
@SuppressWarnings("serial")
public class ModifyTransactionSendLaunchShuttle extends FinancialCargoShuttle
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ModifyTransactionSendLaunchShuttle.class);
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/16 $";
    /**
     * line items in the transaction
     */
    protected SaleReturnLineItemIfc[] items = null;
    /**
     * transaction
     */
    protected RetailTransactionIfc transaction;

    /**
     * Copies information from the cargo used in the ModifyTransaction service.
     *
     * @param bus Service Bus
     */
    @Override
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();

        if (cargo.getTransaction() == null)
        {
            // This is used if no line items were added and
            // transaction send was chosen. We still need to
            // get the customer present,billing and shipping address
            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

            // Create a new transaction
            SaleReturnTransactionIfc createTransaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();

            // Initialize fields specific to SaleReturnTransaction
            createTransaction.setCashier(cargo.getOperator());
            if (cargo.getSalesAssociate() != null)
            {
                createTransaction.setSalesAssociate(cargo.getSalesAssociate());
            }
            else
            {
                createTransaction.setSalesAssociate(cargo.getOperator());
            }
            // Initializes the fields common to all transactions.
            utility.initializeTransaction(createTransaction);
            if (cargo.getRegister() != null && cargo.getRegister().getWorkstation() != null)
            {
                boolean transReentry = cargo.getRegister().getWorkstation().isTransReentryMode();
                ((SaleReturnTransaction)createTransaction).setReentryMode(transReentry);
            }

            // Set up default locales for pole display and receipt
            Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            UIUtilities.setUILocaleForCustomer(defaultLocale);
            cargo.setTransaction(createTransaction);
        }
        transaction = cargo.getTransaction();
        if (transaction != null)
        {
            items = (SaleReturnLineItemIfc[])transaction.getLineItems();
        }
    }

    /**
     * Copies information to the cargo used in the Send service.
     *
     * @param bus Service Bus
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);
        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        cargo.setTransaction(transaction);
        cargo.setItems(items);
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
      // return string
        return (revisionNumber);
    }
}
