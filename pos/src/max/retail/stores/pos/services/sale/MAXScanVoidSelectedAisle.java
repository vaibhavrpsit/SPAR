/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAX, Inc.    All Rights Reserved.
  Rev. 1.0 		Tanmaya		05/04/2013		Initial Draft: Change for Scan and void
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;

public class MAXScanVoidSelectedAisle extends LaneActionAdapter implements LaneActionIfc{
	  /**
	 * 
	 */
	private static final long serialVersionUID = -1359758528949410400L;

	//--------------------------------------------------------------------------
    /**

            This aisle sets the role function ID in the cargo
            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        //   cargo.setAccessFunctionID(RoleFunctionIfc.SCANANDVOID);  //To Do 
        bus.mail(new Letter("Override"), BusIfc.CURRENT);
    }
    
}
