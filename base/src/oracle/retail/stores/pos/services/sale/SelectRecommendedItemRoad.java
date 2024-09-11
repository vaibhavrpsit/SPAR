/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/SelectRecommendedItemRoad.java /main/1 2014/06/22 09:20:30 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/16/14 - Added to support display of extended data
 *                         recommended items from the Sale Item
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

/**
 * This class gets the item ID of the selected recommended item and sets
 * it on the cargo.
 * @since 14.1
 */
@SuppressWarnings("serial")
public class SelectRecommendedItemRoad extends PosLaneActionAdapter
{

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Get the cargo
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        
        // Get the id of the selected recommended item and set it on the cargo.
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        LineItemsModel beanModel = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
        cargo.setSelectedRecommendedItemId(beanModel.getSelectedRecommendedItemID());
    }

}
