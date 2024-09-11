/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/SerialValidationReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:13 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Return shuttle class to invoke serial validation
 *                         from layaway find tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import oracle.retail.stores.pos.services.common.SerialValidationCargo;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

public class SerialValidationReturnShuttle implements ShuttleIfc
{
    protected SerialValidationCargo validationCargo;

    /**
     Copies information from the cargo used in the POS service.
     @param  bus     Service Bus
     */
    public void load(BusIfc bus)
    {
        validationCargo = (SerialValidationCargo)bus.getCargo();

    }

    /**
     Copies information from the cargo used in the POS service.
     @param  bus     Service Bus
     */
    public void unload(BusIfc bus)
    {
        LayawayCargo layawaycargo = (LayawayCargo)bus.getCargo();

    }

}
