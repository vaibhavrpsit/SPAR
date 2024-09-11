/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/relateditem/AddRelatedOrderItemAisle.java /main/2 2013/04/05 14:28:29 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/05/13 - Modified to prevent adding order items when not
 *                         valid.
 * ===========================================================================
 */ 
package oracle.retail.stores.pos.services.modifyitem.relateditem;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CheckOrderItemValidLaneAdapter;

/**
 * Update the modified item's Gift Registry Put it into cargo Mail a final
 * letter
 * 
 * @version $Revision: /main/2 $
 */
@SuppressWarnings("serial")
public class AddRelatedOrderItemAisle extends CheckOrderItemValidLaneAdapter
{
    /**
     * Get UI input Put it into cargo update the modified item. Mail a final
     * letter
     * 
     * @param BusIfc bus
     * @return void
     * @exception
     */
    @Override
    public void traverse(BusIfc bus)
    {
        RelatedItemCargo cargo = (RelatedItemCargo)bus.getCargo();
        SaleReturnLineItemIfc   lineItem = cargo.getLineItem();
        boolean isOrderItem = false;
        
        if (isAddOrderItemValid(cargo.getRegister(), cargo.getRetailTransaction()))
        {
            if (lineItem.getPLUItem().getItemClassification().isWillCallFlag())
            {
                isOrderItem = true;
            }
            else if (lineItem.isPluDataFromCrossChannelSource())
            {
                isOrderItem = true;
            }
        }
        
        if (!cargo.getRegister().getWorkstation().isTransReentryMode() && isOrderItem)
            cargo.addOrderLineItem(lineItem);

        //And, move along.
        bus.mail(new Letter("Next"), BusIfc.CURRENT);
    }

}