/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/SerialisedPickUpLaunchShuttle.java /main/8 2013/01/02 11:55:36 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       12/27/12 - no need to set pre split index
 *    sgu       12/27/12 - set presplit index and item selected flags
 *    jswan     04/13/12 - Modified to support the change in location of the
 *                         pickup and delivery tours.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - new launch shuttle for serialised item pickup
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;

public class SerialisedPickUpLaunchShuttle extends FinancialCargoShuttle implements ShuttleIfc
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4239029815122459415L;
    
    /**
     * Calling Item cargo
     */
    protected SerializedItemCargo itemCargo = null;

    /**
     * Loads the item cargo.
     * @param bus Service Bus to copy cargo from.
     */
    public void load(BusIfc bus)
    {
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
        SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])itemCargo.getLineItems().toArray(new SaleReturnLineItemIfc[0]);
       
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        pickupDeliveryOrderCargo.setRegister(itemCargo.getRegister());
        pickupDeliveryOrderCargo.setTransactionType(itemCargo.getTransactionType());
        pickupDeliveryOrderCargo.setTransaction(itemCargo.getTransaction());
        pickupDeliveryOrderCargo.setCustomer(itemCargo.getCustomer());
        pickupDeliveryOrderCargo.setLineItems(lineItems);
        pickupDeliveryOrderCargo.setItem(itemCargo.getItem());
        pickupDeliveryOrderCargo.setStoreStatus(itemCargo.getStoreStatus());
        pickupDeliveryOrderCargo.setOperator(itemCargo.getOperator());
        pickupDeliveryOrderCargo.setTenderLimits(itemCargo.getTenderLimits());
    }

}
