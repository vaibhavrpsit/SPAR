/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.order.alter;

import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**

    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MaxAlterOrderRemoveItemRoad extends PosLaneActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7574967560642364334L;
	/**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: 3$";

    //----------------------------------------------------------------------
    /**
       Removes the selected item from the transaction.

       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

    	MAXOrderCargo cargo = (MAXOrderCargo)bus.getCargo();
        //cargo.getTransaction().removeLineItem(cargo.getLineItem().getLineNumber());

    }

}
