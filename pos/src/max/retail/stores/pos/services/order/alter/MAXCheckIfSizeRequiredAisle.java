/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.alter;

import max.retail.stores.pos.services.order.common.MAXOrderCargoIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This ailse validates the returned item information.
    <p>
    @version $Revision: 3$
**/
//--------------------------------------------------------------------------
public class MAXCheckIfSizeRequiredAisle extends LaneActionAdapter
{
    
	private static final long serialVersionUID = -7100916416483823380L;
	/**
       revision number
    **/
    public static final String revisionNumber = "$Revision: 3$";

 
    //----------------------------------------------------------------------
    /**
       This ailse checks if the item size is required
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        MAXOrderCargoIfc cargo = (MAXOrderCargoIfc)bus.getCargo();
        PLUItemIfc pluItem = cargo.getPLUItem();
        
        String letterName = "Continue2";

        if (pluItem != null && pluItem.isItemSizeRequired() )
        {
             letterName ="Size";
        }
        bus.mail(new Letter(letterName), BusIfc.CURRENT);

    }
}
