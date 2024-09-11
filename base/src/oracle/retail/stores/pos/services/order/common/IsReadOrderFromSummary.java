/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/IsReadOrderFromSummary.java /main/2 2013/03/19 11:55:19 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       03/07/13 - handle multiple return orders
 *    jswan     10/25/12 - Added to support returns by order.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal checks to see if there is only 1 order summary in the cargo.
    <p>
    @version $Revision: /main/2 $
**/
//--------------------------------------------------------------------------
public class IsReadOrderFromSummary implements TrafficLightIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3979877899205091284L;

    //----------------------------------------------------------------------
    /**
       Checks to see if there is only 1 order summary in the cargo.
       <P>
       @return true if only 1 order summary, false otherwise.
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean result = false;
        OrderCargoIfc cargo = (OrderCargoIfc)bus.getCargo();
        OrderSummaryEntryIfc[] orderSummaries = ((OrderCargo)cargo).getOrderSummaries();
        
        if (cargo.isReadOrderFromSummary() && (orderSummaries.length == 1))
        {
            ((OrderCargo)cargo).setSelectedSummary(orderSummaries[0]);
            result = true;
        }
        
        return result;
    }
}
