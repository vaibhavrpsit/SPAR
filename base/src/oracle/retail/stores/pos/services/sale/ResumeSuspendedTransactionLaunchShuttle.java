/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ResumeSuspendedTransactionLaunchShuttle.java /main/3 2014/05/14 14:41:27 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/14/14 - rename retrieve to resume
 *    rgour     12/04/12 - Suspedned Transaction not found if entered id is
 *                         found in both Item Master and suspended transaction
 *                         list
 *    rgour     11/09/12 - Enhancements in Suspended Transactions
 *   
 * =========================================================================== 
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.modifytransaction.resume.ModifyTransactionResumeCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Launch shuttle class for Resume Suspended Transaction service.
 * 
 * @version $Revision: /main/3 $
 */
public class ResumeSuspendedTransactionLaunchShuttle extends FinancialCargoShuttle
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -250052797862643799L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ResumeSuspendedTransactionLaunchShuttle.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/3 $";

    /**
     * the transaction
     */
    protected RetailTransactionIfc transaction = null;

    /**
     * POS sale cargo
     */
    protected SaleCargoIfc saleCargo = null;

    /**
     * sales associate set using modify transaction sales associate
     */
    protected boolean salesAssociateAlreadySet = false;

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
        // resume cargo from the parent
        saleCargo = (SaleCargoIfc)bus.getCargo();
        salesAssociateAlreadySet = saleCargo.isAlreadySetTransactionSalesAssociate();
        transaction = saleCargo.getTransaction();
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
        RetailTransactionIfc transaction = null;
        // clone the transaction
        if (saleCargo.getTransaction() != null)
        {
            transaction = (RetailTransactionIfc)saleCargo.getTransaction().clone();
        }
        // resume cargo from the child(ModifyTransaction Cargo)
        ModifyTransactionResumeCargo cargo = (ModifyTransactionResumeCargo)bus.getCargo();

        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel posBase = (POSBaseBeanModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
        String transactionIdEntered = null;
        if (posBase != null)
        {
            PromptAndResponseModel parModel = posBase.getPromptAndResponseModel();
            if (parModel != null)
            {
                transactionIdEntered = parModel.getResponseText();
            }
        }
        if (transactionIdEntered == null)
        {
            transactionIdEntered = saleCargo.getPLUItemID();
        }
        cargo.setTransactionIDEntered(transactionIdEntered);
        // set the child reference to the cloned object
        cargo.setTransaction(transaction);
        cargo.setSalesAssociate(saleCargo.getEmployee());
        cargo.setOperator(saleCargo.getOperator());
        cargo.setRegister(saleCargo.getRegister());
        cargo.setStoreStatus(saleCargo.getStoreStatus());
        cargo.setTenderLimits(saleCargo.getTenderLimits());
    }

    /**
     * Launch's the string representation of the object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  ResumeSuspendedTransactionLaunchShuttle").append(" (Revision ")
                .append(getRevisionNumber()).append(")").append(hashCode());
        return (strResult.toString());

    }

    /**
     * Retruns the revision number.
     * 
     * @return String representation of revision number
     **/
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}