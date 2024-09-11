/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/GetSelectedStoreRoad.java /main/4 2014/05/01 10:28:08 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/30/14 - Make pickup item from the same store as a cross
 *                         channel item.
 *    abhinavs  08/24/13 - Xchannel Inventory lookup enhancement phase I
 *    sgu       01/11/13 - set xchannel order item flag based on if item is
 *                         avilable in the store inventory
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

//foundation imports
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.AvailableToPromiseInventoryLineItemModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
    This road determines the selected store and sets it on the cargo.
    <p>
    @version $Revision: /main/4 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class GetSelectedStoreRoad extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/4 $";

    //----------------------------------------------------------------------
    /**
       This method determines the selected store and sets it on the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        ListBeanModel model = (ListBeanModel)ui.getModel(POSUIManagerIfc.XC_PICKUP_STORE_SELECT);
        AvailableToPromiseInventoryLineItemModel silm = 
                (AvailableToPromiseInventoryLineItemModel)model.getSelectedValue();
        StoreIfc store = cargo.getStoreByID(silm.getStoreID());
        for(int i=0;i<cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().size();i++)
        {
            cargo.getStoreForPickupByLineNum().put(cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().get(i).getLineNumber(),store);
        }
    }
}
