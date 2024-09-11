/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/CheckOrderItemValidLaneAdapter.java /main/1 2013/04/05 14:28:29 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/05/13 - Added to support checking the validity of creating
 *                         on order line item on the fly.
 * ===========================================================================
 */ 
package oracle.retail.stores.pos.services.common;

import java.util.Iterator;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This class provides extending class with the method isAddOrderItemValid
 */
@SuppressWarnings("serial")
abstract public class CheckOrderItemValidLaneAdapter extends PosLaneActionAdapter 
{
    
    /**
     * Check the register and transaction to verify that items can become
     * order items. 
     * @param cargo
     * @return
     */
    protected boolean isAddOrderItemValid(RegisterIfc register, RetailTransactionIfc transaction)
    {
        boolean addOrderItems = true;
        
        // Check for training and re-entry mode
        if (register.getWorkstation().isTransReentryMode() || register.getWorkstation().isTrainingMode())
        {
            addOrderItems = false;
        }

        // Check for return and external items already in the the transaction.
        if (transaction != null)
        {
            for (Iterator<AbstractTransactionLineItemIfc> iter = transaction.getLineItemsIterator(); iter.hasNext();)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)iter.next();
                if (lineItem.isReturnLineItem() || lineItem.isFromExternalOrder())
                {
                    addOrderItems = false;
                }
            }
        }

        // Check for transaction level send
        if (transaction instanceof SaleReturnTransactionIfc && ((SaleReturnTransactionIfc)transaction).isTransactionLevelSendAssigned())
        {
            addOrderItems = false;
        }

        // Check for layaway transaction
        if (transaction instanceof LayawayTransactionIfc)
        {
            addOrderItems = false;
        }
        
        // Check for special order transaction
        if (transaction instanceof OrderTransaction && ((OrderTransaction)transaction).getOrderType() == OrderConstantsIfc.ORDER_TYPE_SPECIAL)
        {
            addOrderItems = false;
        }
        
        return addOrderItems;
    }
}
