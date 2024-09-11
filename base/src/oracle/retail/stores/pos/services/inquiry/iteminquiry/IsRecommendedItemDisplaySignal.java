/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/IsDestinationNotAdvanceSearchScreenSignal.java /main/2 2014/06/22 09:20:30 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
*    jswan     11/17/14 - Fixed recommended item read failure.
* ===========================================================================
*/

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 *  roadClear() method returns true if coming from ICE recommended item 
 *  selection on Sell Item Screen.
 */
@SuppressWarnings("serial")
public class IsRecommendedItemDisplaySignal implements TrafficLightIfc 
{
    /**
    * Method returns true if coming from ICE recommended item selection on Sell Item Screen.        
    * @return boolean
    **/
    public boolean roadClear(BusIfc bus)
    {
        boolean result = false;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.isDisplayRecommendedItem())
        {
           result = true;
        }
        return(result);
    }
}
