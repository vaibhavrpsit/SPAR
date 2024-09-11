/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/ModifyTransactionResumeReturnShuttle.java /main/14 2014/05/14 14:41:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    rgour     06/17/13 - setting captured customer value for shipping method
 *                         screen if no customer is linked to the transaction
 *    mchellap  08/12/11 - BUG#11854626 Customer Information not send to RM for
 *                         resumed transactions
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         9/25/2007 9:34:12 AM   Bret Courtney
 *         setting sales associate retrieving transaction as the sales
 *         associate in the transaction itself
 *    3    360Commerce 1.2         3/31/2005 4:29:05 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:35 AM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:02:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Aug 08 2002 08:36:40   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:14:18   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:38:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:30:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
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
 * Return shuttle class for ModifyTransactionResume service.
 * 
 * @version $Revision: /main/14 $
 */
public class ModifyTransactionResumeReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 3264199762385984733L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ModifyTransactionResumeReturnShuttle.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

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

        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();
        transaction = cargo.getTransaction();

        originalReturnTransactions = cargo.getOriginalReturnTransactions();
    }

    /**
     * Unloads to parent (ModifyTransaction) cargo class.
     * 
     * @param b bus interface
     */
    @Override
    public void unload(BusIfc bus)
    {
        // unload financial cargo
        super.unload(bus);

        ModifyTransactionCargo cargo = (ModifyTransactionCargo)bus.getCargo();
        if (transaction != null)
        {
            // current sales associate always overrides any existing sales
            // associate in the transaction
            transaction.setSalesAssociate(cargo.getSalesAssociate());

            cargo.setTransaction(transaction);
            cargo.setUpdateParentCargoFlag(true);
            // set status according to customer setting
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            CustomerIfc customer = transaction.getCustomer();
            cargo.setCustomerInfo(cargo.getCustomerInfo());
            if(cargo.getTransaction().getCustomer()!=null)
            {
            	cargo.getCustomerInfo().getPhoneNumber().setPhoneNumber(cargo.getTransaction().getCustomer().getPrimaryPhone().getPhoneNumber());
            }
            //cargo.getCustomerInfo();
            // if customer is null, no change required
            // (status field should already be blank since no
            // transaction has been initiated)
            String customerName = "";
            if (customer != null)
            {
                Object[] parms = { customer.getFirstName(), customer.getLastName() };
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                String pattern = utility.retrieveText("CustomerAddressSpec", BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                        CUSTOMER_NAME_TAG, CUSTOMER_NAME_TEXT);
                customerName = LocaleUtilities.formatComplexMessage(pattern, parms);

                //cargo.setCustomerInfo(transaction.getCustomerInfo());

                // set the customer's name in the status area
                ui.customerNameChanged(customerName);
            }
            else
            {
                if (transaction.getCaptureCustomer() != null)
                {
                    customerName = transaction.getCaptureCustomer().getFirstLastName();
                    ui.customerNameChanged(customerName);
                }
            }
            // Refresh the sales associate name on the sell item screen.
            EmployeeIfc salesAssociate = transaction.getSalesAssociate();
            if (salesAssociate != null)
            {
                ui.salesAssociateNameChanged(salesAssociate.getPersonName().getFirstLastName());
            }
        }

        // add all original return transactions to the parent cargo list
        if (originalReturnTransactions != null)
        {
            for (int i = 0; i < originalReturnTransactions.length; i++)
            {
                cargo.addOrignalReturnTransaction(originalReturnTransactions[i]);
            }
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
        strResult.append("Class:  ModifyTransactionResumeReturnShuttle").append(" (Revision ")
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