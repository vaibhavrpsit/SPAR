/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import max.retail.stores.domain.order.MAXOrderIfc;
import max.retail.stores.pos.services.order.common.MAXOrderCargoIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Road to traverse after a line item has been added to the transaction.
    <ul>
    <li>Journals the item
    <li>Displays the item on the pole display
    </ul>
    @version $Revision: 5$
**/
//--------------------------------------------------------------------------
public class MAXAlterOrderItemAddedRoad extends PosLaneActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -5755679738841179135L;
	/**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 5$";

    //----------------------------------------------------------------------
    /**
       Journals the added line item information and makes the call to
       display the item info on the pole display device.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
    	MAXOrderCargoIfc                cargo       = (MAXOrderCargoIfc)bus.getCargo();
        OrderLineItemIfc       item        = cargo.getLineItem();
        item.getOrderItemStatus().getStatus().setStatus(0);
        cargo.getOrder().addLineItem(item);
        if(cargo.getOrder() instanceof MAXOrderIfc)
        	((MAXOrderIfc)cargo.getOrder()).setAlterOrder(true);
        //Show item on Line Display device
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        try
        {
            pda.lineDisplayItem(item);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage() + "");
        }

    }//end traverse
}
