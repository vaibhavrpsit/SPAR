/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionTaxLaunchShuttle.java /main/13 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:41 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/24 16:21:30  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:14:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:34   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:30:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import java.util.Vector;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifytransaction.tax.ModifyTransactionTaxCargo;

import org.apache.log4j.Logger;

/**
 * Launch shuttle class for ModifyTransactionTax service.
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class ModifyTransactionTaxLaunchShuttle extends FinancialCargoShuttle
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ModifyTransactionTaxLaunchShuttle.class);
    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
     * vector of items from retail transaction
     */
    protected Vector lineItems;
    /**
     * transaction tax object
     */
    protected TransactionTaxIfc transactionTax = null;
    /**
     * Flag to determine whether a transaction can be created by the child
     * service
     */
    protected boolean createTransaction = false;
    /**
     * Flag to determine whether a customer has been previouly linked
     */
    protected boolean customerPreviouslyLinked = false;
    /**
     * transaction
     */
    protected RetailTransactionIfc transaction = null;
    /**
     * modify transaction cargo
     */
    protected ModifyTransactionCargo modifyTransactionCargo = null;

    /**
     * Loads parent (ModifyTransaction) cargo class.
     * 
     * @param b bus interface
     */
    @Override
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);

        // retrieve cargo
        modifyTransactionCargo = (ModifyTransactionCargo) bus.getCargo();
        transaction = modifyTransactionCargo.getTransaction();
        if (transaction != null)
        {
            lineItems = transaction.getLineItemsVector();
            transactionTax = (TransactionTaxIfc)transaction.getTransactionTax().clone();
            CustomerIfc customer = modifyTransactionCargo.getTransaction().getCustomer();
            if (customer != null)
            {
               customerPreviouslyLinked = true;
            }
        }
        else
        {
            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            transactionTax = utility.getInitialTransactionTax();
            lineItems = new Vector<AbstractTransactionLineItemIfc>();
            createTransaction = true;
        }
    }

    /**
     * Unloads to child (ModifyTransactionTax) cargo class.
     * 
     * @param b bus interface
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        // pull out transaction tax object, line items, etc.

        // retrieve cargo
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo)bus.getCargo();

        // update child cargo
        cargo.initialize(lineItems, transactionTax);
        cargo.setSalesAssociate(modifyTransactionCargo.getSalesAssociate());
        cargo.setCreateTransaction(createTransaction);

        // if the transaction exist pass it along to the child service
        if (customerPreviouslyLinked)
        {
            cargo.setCustomer(transaction.getCustomer());
            cargo.setCustomerPreviouslyLinked(true);
        }
        if (!createTransaction)
        {
            cargo.setTransaction(transaction);
        }
    }
}