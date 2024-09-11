/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/IsRefundTenderSignal.java /main/1 2014/02/20 14:40:22 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     02/20/14 - Fixing null pointer exception when cancelling failed
 *                         gift card refund tender.
 *                         
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

// Foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    Indicates if the user has expended all log on attempts.

**/
//--------------------------------------------------------------------------
public class IsRefundTenderSignal implements TrafficLightIfc
{

    /**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 835713145752514790L;

	//--------------------------------------------------------------------------
    /**
       roadClear determines whether it is safe for the bus to proceed

       @param bus the bus trying to proceed
       @return true if attempts have been exhausted; false otherwise
    **/
    //--------------------------------------------------------------------------

    public boolean roadClear(BusIfc bus)
    {
        boolean ret = false;
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        if (cargo.getCurrentLineNumber() == null)
        {
            ret = true;
        }
        return ret;
    }
}
