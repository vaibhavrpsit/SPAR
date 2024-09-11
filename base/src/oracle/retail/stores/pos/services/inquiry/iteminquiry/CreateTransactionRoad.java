/* ===========================================================================
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/CreateTransactionRoad.java /main/7 2013/02/13 10:30:49 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    02/13/13 - setAgeRestrictedDOB only when it is null
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    arabalas  08/28/12 - Setting the Sale associate ID to a transaction when
 *                         it is available
 *    nkgautam  12/14/10 - added restricted dob to transaction after
 *                         initialising
 *    nkgautam  12/01/10 - npe check added for dob skip transactions, where
 *                         transaction is still not initialized
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    jswan     10/29/10 - Moved transaction creation to an aisle.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;

/**
 * This road is traversed when the user has selected to add an item and it has
 * passed all PLUItem requirements.
 */
public class CreateTransactionRoad extends LaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4914958451723442119L;

    /**
     * This road is traversed when the user has selected to add an item and it
     * has passed all PLUItem requirements.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.getModifiedFlag() && 
            cargo.getPLUItem() != null &&
            cargo.getTransaction() == null)
        {
            SaleReturnTransactionIfc transaction = DomainGateway.getFactory().getSaleReturnTransactionInstance();
            transaction.setCashier(cargo.getOperator());
            if (cargo.getSalesAssociate() != null)
            {
                transaction.setSalesAssociate(cargo.getSalesAssociate());
            }
            else
            {
                transaction.setSalesAssociate(cargo.getOperator());
            }
            boolean transReentry = cargo.getRegister().getWorkstation().isTransReentryMode();
            ((SaleReturnTransaction)transaction).setReentryMode(transReentry);

            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            utility.initializeTransaction(transaction);
            if(transaction.getAgeRestrictedDOB() == null)
            {
                transaction.setAgeRestrictedDOB(cargo.getRestrictedDOB());
            }
            cargo.setTransaction(transaction);
        }
        else if (cargo.getTransaction() != null)
        {
            if(((SaleReturnTransactionIfc)cargo.getTransaction()).getAgeRestrictedDOB() == null)
            {
                ((SaleReturnTransactionIfc)cargo.getTransaction()).setAgeRestrictedDOB(cargo.getRestrictedDOB());
            }
        }
    }
}
