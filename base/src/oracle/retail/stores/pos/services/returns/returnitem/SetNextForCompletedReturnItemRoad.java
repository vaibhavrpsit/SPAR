/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/SetNextForCompletedReturnItemRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - Modification for external order feature.
 *    jswan     05/27/10 - Added to return item for external order feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the user has completed the returns process
    for the current item.
**/
//--------------------------------------------------------------------------
public class SetNextForCompletedReturnItemRoad extends LaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -9084627070086828188L;

    //----------------------------------------------------------------------
    /**
        This method is executed when the user has completed the returns process
        for the current item.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Increment the current item index.
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();

        // Set the last item returned index.
        cargo.setLastLineItemReturnedIndex(cargo.getCurrentItem());
        // If the return is from an external order, set the return indicator for the
        // corresponding external order item to true; This tracks which external 
        // orders have been returned.  This helps fulfill the requirement that all
        // external order items must be returned.
        if (cargo.isExternalOrder())
        {
            cargo.setAssociatedExternalOrderItemReturnedStatus(cargo.getPLUItem().getReturnExternalOrderItem(),
                    true);
        }
        
        cargo.setCurrentItem(cargo.getCurrentItem() + 1);
        if (cargo.getReturnSaleLineItems() != null)
        {
            SaleReturnLineItemIfc item = cargo.getReturnSaleLineItems()[cargo.getCurrentItem()];
            cargo.setPLUItem(item.getPLUItem());
            cargo.setPrice(item.getSellingPrice());
            cargo.getReturnItem().setItemQuantity(item.getQuantityReturnable());
        }

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  SetNextReturnItemRoad (Revision " +
                                      ")" + hashCode());
        return(strResult);
    }                                   // end toString()
}
