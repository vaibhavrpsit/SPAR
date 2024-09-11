/* ===========================================================================
* Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/ExitAfterItemAdditionSignal.java /main/1 2014/06/03 13:25:30 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  05/09/14 - Singal to exit returntransaction tour after adding a
 *                         blind return item
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * This signal returns true if the return is for mobile returns
 **/
@SuppressWarnings("serial")
public class ExitAfterItemAdditionSignal implements TrafficLightIfc
{

    public boolean roadClear(BusIfc bus)
    {
        ReturnTransactionCargo cargo = (ReturnTransactionCargo) bus.getCargo();
        return cargo.isExitAfterItemAddition();
    }

}
