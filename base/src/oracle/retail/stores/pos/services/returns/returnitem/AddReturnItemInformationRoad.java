/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/AddReturnItemInformationRoad.java /main/1 2014/07/08 16:11:11 arabalas Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * 
 *    arabalas  07/08/14 - Add Return Item Information before closing the Tour
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This road gets the store details of the Sale Return Items and set it to Return Items.
**/
//--------------------------------------------------------------------------
public class AddReturnItemInformationRoad extends LaneActionAdapter
{

    /** serialVersionUID */
    private static final long serialVersionUID = 188580486127201303L;

    //----------------------------------------------------------------------
    /**
       This road gets the store details of the Sale Return Items and set it to Return Items.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // retrieve cargo
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        
        //ReturnItemIfc[] returnItems = cargo.getReturnItems();
        SaleReturnLineItemIfc[] saleReturnLineItems = cargo.getReturnSaleLineItems();
        
        String geoCode = cargo.getStoreStatus().getStore().getGeoCode();
        cargo.setGeoCode(geoCode);
        
        for(int i = 0; i < saleReturnLineItems.length; i++ )
        {
            ReturnItemIfc returnItem = saleReturnLineItems[i].getReturnItem();

            StoreIfc store = DomainGateway.getFactory().getStoreInstance();
            store.setStoreID(saleReturnLineItems[i].getPLUItem().getStoreID());
            returnItem.setStore(store);
            returnItem.setQuantityPurchased(saleReturnLineItems[i].getQuantityReturnable());
            returnItem.setQuantityReturnable(saleReturnLineItems[i].getQuantityReturnable());
        }
    }

}
