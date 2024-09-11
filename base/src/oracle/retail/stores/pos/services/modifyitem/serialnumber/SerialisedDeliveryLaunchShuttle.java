/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/13/12 - Modified to support the change in location of the
 *                         pickup and delivery tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - New launch shuttle class for serialised item
 *                         delivery
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import java.util.ArrayList;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class SerialisedDeliveryLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7195241072798759822L;
    
    /**
     * Calling Item cargo
     */
    protected SerializedItemCargo itemCargo = null;


    /**
     * Loads the parent cargo
     */
    public void load(BusIfc bus)
    {
        // load the financial cargo
        super.load(bus);
        itemCargo = (SerializedItemCargo)bus.getCargo();
    }

    /**
     * Transfers the item cargo to the pickup delivery order cargo for the
     * modify item service.
     * @param bus Service Bus to copy cargo to.
     */
    public void unload(BusIfc bus)
    {
        ArrayList itemList = itemCargo.getLineItems();
        SaleReturnLineItemIfc[] items = (SaleReturnLineItemIfc[])itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        pickupDeliveryOrderCargo.setRegister(itemCargo.getRegister());
        pickupDeliveryOrderCargo.setTransactionType(itemCargo.getTransactionType());
        pickupDeliveryOrderCargo.setTransaction(itemCargo.getTransaction());
        pickupDeliveryOrderCargo.setCustomer(itemCargo.getCustomer());
        pickupDeliveryOrderCargo.setLineItems(items);
        pickupDeliveryOrderCargo.setItem(itemCargo.getItem());
        pickupDeliveryOrderCargo.setStoreStatus(itemCargo.getStoreStatus());
        pickupDeliveryOrderCargo.setOperator(itemCargo.getOperator());
        pickupDeliveryOrderCargo.setTenderLimits(itemCargo.getTenderLimits());
    }

}
