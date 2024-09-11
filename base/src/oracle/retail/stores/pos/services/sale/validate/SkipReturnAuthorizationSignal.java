/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/SkipReturnAuthorizationSignal.java /main/1 2014/07/01 11:51:55 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;

/**
 * Road should only be clear if the cargo's skipReturnAuthorization flag is set
 * to true
 */
@SuppressWarnings("serial")
public class SkipReturnAuthorizationSignal implements TrafficLightIfc
{
    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc#roadClear(oracle
     * .retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public boolean roadClear(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo) bus.getCargo();
        return cargo.isSkipReturnAuthorization();
    }

}
