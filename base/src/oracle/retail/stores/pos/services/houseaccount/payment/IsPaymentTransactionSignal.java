/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/houseaccount/payment/IsPaymentTransactionSignal.java /main/1 2013/07/02 13:09:09 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* jswan       07/01/13 - Fixed failure to cancel House Account Transaction when
*                        cancel button pressed or timout occurs.
* ===========================================================================
*/

package oracle.retail.stores.pos.services.houseaccount.payment;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

@SuppressWarnings("serial")
public class IsPaymentTransactionSignal implements TrafficLightIfc
{
    public boolean roadClear(BusIfc bus)
    {
        boolean isPaymentTransaction = true;
        PayHouseAccountCargo cargo = (PayHouseAccountCargo) bus.getCargo();
        if (cargo.getTransaction() == null)
        {
            isPaymentTransaction = false;
        }

        return isPaymentTransaction;
    }
}