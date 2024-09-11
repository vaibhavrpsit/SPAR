/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/IncrementItemIndexRoad.java /main/1 2012/05/02 14:07:49 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

//foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    This class increments the item index that determines which line item is current.
    <p>
    @version $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class IncrementItemIndexRoad extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    //----------------------------------------------------------------------
    /**
       This method increments the item index that determines which line item is current.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        cargo.incrementLineItemIndex();
    }
}
