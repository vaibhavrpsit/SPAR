/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  Copyright (c) 1998-2002 360Commerce, Inc.    All Rights Reserved.

     $Log$
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.sale.multiplemrp;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;

//------------------------------------------------------------------------------
/**
    
    @version $Revision: /rgbustores_12.0.9in_branch/1 $
**/
//------------------------------------------------------------------------------

public class MAXUpdateActiveMRPAisle extends LaneActionAdapter implements LaneActionIfc
{

    //--------------------------------------------------------------------------
    /**
             

            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
    }

    //--------------------------------------------------------------------------
    /**
             

            @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void backup(BusIfc bus, SnapshotIfc snapshot)
    {
    }

}
