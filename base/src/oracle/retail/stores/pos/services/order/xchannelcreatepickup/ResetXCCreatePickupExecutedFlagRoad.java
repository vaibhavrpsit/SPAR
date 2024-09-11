/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/ResetXCCreatePickupExecutedFlagRoad.java /main/1 2013/08/09 17:26:43 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   08/09/13 - Set the xc flag to true once undo from store search.

 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

public class ResetXCCreatePickupExecutedFlagRoad extends PosLaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    //----------------------------------------------------------------------
    /**
         This method gets the customer data from the model, creates a customer capture
         object to temporarily hold the data and adds the map in the cargo.
        <P>
        @param  bus     Service Bus
     **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
     
        cargo.setXchannelCreatePickupExecuted(true);
    }
}
