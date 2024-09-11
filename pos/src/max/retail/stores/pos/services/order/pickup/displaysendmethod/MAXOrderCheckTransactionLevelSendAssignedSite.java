/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order.pickup.displaysendmethod;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.send.address.SendCargo;


//------------------------------------------------------------------------------
/**
    Site to check if transaction level send has been assigned
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------
public class MAXOrderCheckTransactionLevelSendAssignedSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4035215087428359587L;
	/**
       revision number 
    **/
    public static final String revisionNumber = "$Revision: 3$";
 
    //--------------------------------------------------------------------------
    /**
       Checks if transaction level send has been assigned
       @param bus service bus reference
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        LetterIfc letter = new Letter(CommonLetterIfc.NEXT);
        SendCargo sendCargo = (SendCargo)bus.getCargo();
        if(sendCargo.isTransactionLevelSendInProgress())
        {
             letter = new Letter(CommonLetterIfc.CONTINUE);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}