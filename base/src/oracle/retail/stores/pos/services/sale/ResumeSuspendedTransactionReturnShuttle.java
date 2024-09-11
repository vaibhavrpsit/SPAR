/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ResumeSuspendedTransactionReturnShuttle.java /main/2 2014/05/14 14:41:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    rgour     11/09/12 - Enhancements in Suspended Transactions
 *
 * =========================================================================== 
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifytransaction.resume.ModifyTransactionResumeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Return shuttle class for Resume Suspended Transaction service.
 * 
 * @version $Revision: /main/2 $
 */
public class ResumeSuspendedTransactionReturnShuttle extends FinancialCargoShuttle
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6370559010352598385L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ResumeSuspendedTransactionReturnShuttle.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/2 $";

    /**
     * Customer name bundle tag
     */
    public static final String CUSTOMER_NAME_TAG = "CustomerName";

    /**
     * Customer name default text
     */
    public static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    /**
     * transaction to be resumed
     */
    protected RetailTransactionIfc transaction;

    /**
     * This array contains a list of SaleReturnTransacions on which returns have
     * been completed. It will be used if a transaction with returned lineitems
     * is resumed.
     */
    protected SaleReturnTransactionIfc[] originalReturnTransactions;

    protected ModifyTransactionResumeCargo modifyResumecargo = null;

    /**
     * Loads from child (ModifyTransactionResume) cargo class.
     * 
     * @param b bus interface
     */
    @Override
    public void load(BusIfc bus)
    {
        // load financial cargo
        super.load(bus);
        modifyResumecargo = (ModifyTransactionResumeCargo)bus.getCargo();
        transaction = modifyResumecargo.getTransaction();
        originalReturnTransactions = modifyResumecargo.getOriginalReturnTransactions();

    }

    /**
     * Unloads to parent (Sale) cargo class.
     * 
     * @param b bus interface
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);
        SaleCargo cargo = (SaleCargo)bus.getCargo();
        if (transaction != null)
        {
            // current sales associate always overrides any existing sales
            // associate in the transaction
            transaction.setSalesAssociate(cargo.getSalesAssociate());

            cargo.setTransaction((SaleReturnTransactionIfc)transaction);

            // cargo.setUpdateParentCargoFlag(true);
            // set status according to customer setting
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            CustomerIfc customer = transaction.getCustomer();
            // if customer is null, no change required
            // (status field should already be blank since no
            // transaction has been initiated)
            if (customer != null)
            {
                Object[] parms = { customer.getFirstName(), customer.getLastName() };
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                        CUSTOMER_NAME_TAG, CUSTOMER_NAME_TEXT);
                String customerName = LocaleUtilities.formatComplexMessage(pattern, parms);

                cargo.setCustomerInfo(transaction.getCustomerInfo());

                // set the customer's name in the status area
                ui.customerNameChanged(customerName);
            }

            // Refresh the sales associate name on the sell item screen.
            EmployeeIfc salesAssociate = transaction.getSalesAssociate();
            if (salesAssociate != null)
            {
                ui.salesAssociateNameChanged(salesAssociate.getPersonName().getFirstLastName());
            }
        }
        if (modifyResumecargo.getTransaction() instanceof OrderTransaction)
        {
            cargo.setRetailTransactionIfc(modifyResumecargo.getTransaction());

            // if there is a transaction, set the tender limits
            if (cargo.getTransaction() != null)
            {
                cargo.getTransaction().setTenderLimits(cargo.getTenderLimits());
            }
        }

        if (originalReturnTransactions != null)
        {
            for (int i = 0; i < originalReturnTransactions.length; i++)
            {
                cargo.addOriginalReturnTransaction(originalReturnTransactions[i]);
            }
        }

        EmployeeIfc salesAssociate = null;
        salesAssociate = modifyResumecargo.getSalesAssociate();
        if (salesAssociate != null)
        {
            if (logger.isInfoEnabled())
                logger.info("ModifyTransactionReturnShuttle: setting sales associate");
            cargo.setEmployee(salesAssociate);
        }
    }

    /**
     * Returns the string representation of the object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuffer strResult = new StringBuffer();
        strResult.append("Class:  ResumeSuspendedTransactionReturnShuttle").append(" (Revision ")
                .append(getRevisionNumber()).append(")").append(hashCode());
        return (strResult.toString());
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