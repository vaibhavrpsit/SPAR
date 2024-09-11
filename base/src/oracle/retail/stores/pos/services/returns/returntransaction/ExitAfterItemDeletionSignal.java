/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ExitAfterItemDeletionSignal.java /main/1 2014/06/10 15:26:18 arabalas Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* arabalas    05/26/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This signal returns true if the return is for mobile returns
 *
 * @since 14.1
 */
public class ExitAfterItemDeletionSignal implements TrafficLightIfc
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;


    public boolean roadClear(BusIfc bus)
    {
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        return cargo.isExitAfterItemDeletion();
    }

}
