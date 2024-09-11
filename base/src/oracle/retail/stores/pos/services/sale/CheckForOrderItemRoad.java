/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckForOrderItemRoad.java /main/3 2013/04/17 16:44:39 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/17/13 - Create addPluItem for taking selectForItemSplit as
 *                         an argument.
 *    yiqzhao   04/16/13 - Fix the issue when enter quantity with item id in
 *                         sell item screen.
 *    jswan     04/05/13 - Modified to prevent adding order items when not
 *                         valid.
 * ===========================================================================
 */ 
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CheckOrderItemValidLaneAdapter;

public class CheckForOrderItemRoad extends CheckOrderItemValidLaneAdapter 
{
    
    private static final long serialVersionUID = 1L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/3 $";

    /**
     * Check for the line item entered from sale is web store item or not.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        SaleReturnLineItemIfc srli = cargo.getLineItem();
        boolean isOrderItem = false;
        
        if (isAddOrderItemValid(cargo.getRegister(), cargo.getRetailTransaction()))
        {
            if (srli.getPLUItem().getItemClassification().isWillCallFlag())
            {
                isOrderItem = true;
            }
            else if (srli.isPluDataFromCrossChannelSource())
            {
                isOrderItem = true;
            }
        }
        
        if (isOrderItem)
        {
            if (cargo.getSplittedLineItems() != null && cargo.getSplittedLineItems().length > 1)
            {
                //handle the situation like entering 5*1234 (split)
                for (int i=0; i<cargo.getSplittedLineItems().length; i++)
                {
                    srli = (SaleReturnLineItemIfc)cargo.getSplittedLineItems()[i];
                    srli.setSelectedForItemModification(true);
                    cargo.getOrderLineItems().add(srli);
                }
                cargo.setSplittedLineItems(null);
            }
            else
            {
                srli.setSelectedForItemModification(true);
                cargo.getOrderLineItems().add(srli);
            }
        }
    }
}
