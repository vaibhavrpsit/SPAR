/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	27-Aug-2015		Geetika4.Chugh		<Comments>	
*
********************************************************************************/
package max.retail.stores.pos.services.tender.creditdebit;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
     This class just mails a different letter than cancel.
     $Revision: 1.1 $
 **/
//--------------------------------------------------------------------------
public class MAXPineLabConvertCancelToOtherAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 9202472882941911198L;

    public static final String LANENAME = "ConvertCancelToOtherAisle";

    //----------------------------------------------------------------------
    /**
        Mail an Undo letter instead of cancel so that it can exit the service
        correctly.
        @param bus
        @see com.extendyourstore.foundation.tour.ifc.LaneActionIfc#traverse(com.extendyourstore.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        bus.mail(new Letter("Undo"), BusIfc.CURRENT);
    }    
}
