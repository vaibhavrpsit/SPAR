/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/ValidateSerialNumberReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Return shuttle class for serial validation tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.serialnumber.SerializedItemCargo;

/**
 * Return shuttle class for serial validation tour
 * @author nkgautam
 */
public class ValidateSerialNumberReturnShuttle implements ShuttleIfc
{

    /**
     * Called tour cargo class
     */
    protected SerialValidationCargo validationCargo ;

    /**
     * Copies information from the cargo used in the POS service.
     */
    public void load(BusIfc bus)
    {
        validationCargo = (SerialValidationCargo) bus.getCargo();

    }

    /**
     * Copies information from the cargo used in the POS service.
     */
    public void unload(BusIfc bus)
    {
        SerializedItemCargo serializedCargo = (SerializedItemCargo) bus.getCargo();
        serializedCargo.setItem(validationCargo.getLineItem());
    }

}
