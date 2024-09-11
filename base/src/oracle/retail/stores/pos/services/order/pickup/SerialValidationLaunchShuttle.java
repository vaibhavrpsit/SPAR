/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/SerialValidationLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/17/09 - code review updates
 *    nkgautam  12/15/09 - Launch shuttle class to invoke serial validation
 *                         from pickup tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.pos.services.common.SerialValidationCargo;

import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;

public class SerialValidationLaunchShuttle implements ShuttleIfc
{

    /**
     * Calling Cargo class
     */
    protected PickupOrderCargo pickupCargo;

    /**
      Copies information from the cargo used in the POS service.
      @param  bus     Service Bus
     */
    public void load(BusIfc bus)
    {
        pickupCargo = (PickupOrderCargo)bus.getCargo();

    }

    /**
      Copies information from the cargo used in the POS service.
      @param  bus     Service Bus
     */
    public void unload(BusIfc bus)
    {
        String storeID = null;
        SerialValidationCargo validationCargo = (SerialValidationCargo)bus.getCargo();
        validationCargo.setProcessValidationResult(true);
        validationCargo.setRegister(pickupCargo.getRegister());
        validationCargo.setLineItem(pickupCargo.getLineItem());
        if(pickupCargo.getStoreStatus()!= null)
        {
            storeID = pickupCargo.getStoreStatus().getStore().getStoreID();
        }

        SearchCriteriaIfc criteria = new SearchCriteria();
        criteria.setStoreNumber(storeID);
        criteria.setItemSerialNumber(pickupCargo.getLineItem().getItemSerial());
        criteria.setItemID(pickupCargo.getLineItem().getItemID());
        validationCargo.setCriteria(criteria);
    }

}
