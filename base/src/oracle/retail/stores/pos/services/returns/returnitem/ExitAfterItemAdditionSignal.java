/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ExitAfterItemAdditionSignal.java /main/1 2014/06/10 15:26:16 arabalas Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* arabalas    05/15/14 - added a Signal when to Exit the Tour
* arabalas    05/13/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This signal returns true if the return is for mobile returns
 *
 * @since 14.1
 */
public class ExitAfterItemAdditionSignal implements TrafficLightIfc
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public boolean roadClear(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        return cargo.isExitAfterItemAddition();
    }

}
