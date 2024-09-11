/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/IsDestinationNotAdvanceSearchScreenSignal.java /main/2 2014/06/22 09:20:30 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* asinton     09/03/14 - Deprecating this file as the logic has moved to new site.
* jswan       06/20/14 - Modified to support display of a Recommended Item.
* abhinavs    04/22/13 - Initial version
* abhinavs    04/22/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

/**
 * 
 * @deprecated As of 14.1, this class is deprecated.  It's functionality has moved to {@link UndoDisplayItemActionSite}.
 *
 */
public class IsDestinationNotAdvanceSearchScreenSignal implements TrafficLightIfc {
	// This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //

	private static final long serialVersionUID = 1621973331149131398L;
    
    

    //----------------------------------------------------------------------
    /**
        Checks to find out if destination is advance search screen or price inquiry screen
        @return boolean true if destination is price inquiry screen or false otherwise.
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean result = false;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.isSimpleSearchTypeFlow() && !cargo.isSkipPriceInquiryFlag() && !cargo.isDisplayRecommendedItem())
        {
           result = true;
        }
        return(result);
    }
}
