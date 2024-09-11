/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/SerialValidationLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/17/09 - Code review updates
 *    nkgautam  12/15/09 - Launch shuttle class to invoke serial validation
 *                         from layaway find tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import oracle.retail.stores.pos.services.common.SerialValidationCargo;

import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

public class SerialValidationLaunchShuttle implements ShuttleIfc
{

    /**
     * Calling Cargo class
     */
    protected LayawayCargo layawaycargo;

    /**
     Copies information from the cargo used in the POS service.
     @param  bus     Service Bus
     */
    public void load(BusIfc bus)
    {
        layawaycargo = (LayawayCargo)bus.getCargo();

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
        validationCargo.setRegister(layawaycargo.getRegister());
        validationCargo.setLineItem(layawaycargo.getLineItem());

        if(layawaycargo.getStoreStatus()!= null)
        {
            storeID = layawaycargo.getStoreStatus().getStore().getStoreID();
        }

        SearchCriteriaIfc criteria = new SearchCriteria();
        criteria.setStoreNumber(storeID);
        criteria.setItemSerialNumber(layawaycargo.getLineItem().getItemSerial());
        criteria.setItemID(layawaycargo.getLineItem().getItemID());
        validationCargo.setCriteria(criteria);
    }

}
