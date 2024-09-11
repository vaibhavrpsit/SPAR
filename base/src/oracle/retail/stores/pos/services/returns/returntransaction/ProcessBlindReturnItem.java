/* ===========================================================================
* Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ProcessBlindReturnItem.java /main/3 2014/07/08 16:11:09 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    arabalas  07/08/14 - added quantityPurchased to returnItemData
 *    mchellap  07/03/14 - Fixed returned quantity not getting updated in db.
 *    mchellap  05/09/14 - Road to add blind return item to cargo
 *    mchellap  05/08/14 - Initial version
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This road fixes up the original transaction to contain the total returned
 * quantity, and sets the external order data on the returned sale return line
 * items
 **/
@SuppressWarnings("serial")
public class ProcessBlindReturnItem extends LaneActionAdapter
{

    /**
     * @param bus Service Bus
     **/
    public void traverse(BusIfc bus)
    {
        // Every thing is good to go.
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();

        // No more items will be returned, process the current transaction.
        // Update quantities from the return screen
        List<SaleReturnLineItemIfc> returnItemsList = cargo.getLineItemsToDisplayList();

        if (returnItemsList != null && returnItemsList.size() > 0)
        {
            SaleReturnLineItemIfc[] rsLineItems = new SaleReturnLineItemIfc[returnItemsList.size()];
            ReturnItemIfc[] returnItems = new ReturnItemIfc[returnItemsList.size()];
            SaleReturnLineItemIfc item;
            int index = 0;
            for (Iterator<SaleReturnLineItemIfc> i = returnItemsList.iterator(); i.hasNext();)
            {
                item = i.next();
                rsLineItems[index] = (SaleReturnLineItemIfc) item.clone();
                returnItems[index] = DomainGateway.getFactory().getReturnItemInstance();
                returnItems[index].setItemQuantity(item.getQuantityReturnable());
                returnItems[index].setFromRetrievedTransaction(true);
                returnItems[index].setQuantityPurchased(item.getQuantityReturnable());
                index++;
            }
            cargo.setReturnSaleLineItems(rsLineItems);
            cargo.setReturnItems(returnItems);
        }

    }
}
