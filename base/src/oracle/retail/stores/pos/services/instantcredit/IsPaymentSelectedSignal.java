/*===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/instantcredit/IsPaymentSelectedSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/24 19:03:16 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         05/23/11 - move inquiry for payment into instantcredit service
* sgu         05/23/11 - add new class
* sgu         05/23/11 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.instantcredit;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

public class IsPaymentSelectedSignal implements TrafficLightIfc
{
    public boolean roadClear(BusIfc bus)
    {
        boolean isPaymentSelected = false;
        InstantCreditCargo cargo = (InstantCreditCargo) bus.getCargo();
        if (cargo.getProcess() == InstantCreditCargo.PROCESS_PAYMENT)
        {
            isPaymentSelected = true;
        }

        return isPaymentSelected;
    }
}