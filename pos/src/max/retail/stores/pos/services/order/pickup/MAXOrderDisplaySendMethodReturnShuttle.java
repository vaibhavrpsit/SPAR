/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	13/Aug/2013	  	Prateek, Changes done for Special Order CR - Suggested Tender
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.order.pickup;

import max.retail.stores.domain.order.MAXOrder;
import max.retail.stores.domain.transaction.MAXOrderTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.pickup.PickupOrderCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;

//------------------------------------------------------------------------------
/**
 * Return shuttle class for DisplaySendMethod service.
 * <P>
 * 
 * @version $Revision: 3$
 */
//------------------------------------------------------------------------------
public class MAXOrderDisplaySendMethodReturnShuttle implements ShuttleIfc
{ // begin class DisplaySendMethodReturnShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4783754192579837249L;

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: 3$";
    /**
     * send cargo
     */
    protected SendCargo sendCargo = null;
    
    public void load(BusIfc bus)
    {
        // begin load()
        
        // retrieve cargo
        sendCargo = (SendCargo) bus.getCargo();

    } // end load()

    public void unload(BusIfc bus)
    {
        // begin unload()

        // retrieve cargo
        PickupOrderCargo cargo = (PickupOrderCargo) bus.getCargo();
        cargo.getOrder().setTotals(sendCargo.getTransaction().getTransactionTotals());
        ((MAXOrder)cargo.getOrder()).setHasShippingCharge(true);
		/**MAX Rev 1.1 Change : Start**/
        ((MAXOrder)cargo.getOrder()).setSuggestedTender(((MAXOrderTransactionIfc)sendCargo.getTransaction()).getSuggestedTender());
		/**MAX Rev 1.1 Change : Start**/
//        sendCargo.getTransaction().setTransactionTotals(cargo.getOrder().getTotals());
        
    } // end unload()


} // end class DisplaySendMethodReturnShuttle
