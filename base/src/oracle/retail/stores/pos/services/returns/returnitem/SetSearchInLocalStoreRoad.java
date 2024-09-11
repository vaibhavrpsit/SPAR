/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header$
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *  jswan       11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;

/**
 * This road sets the SearchCriteria object so that Item Manager will call the
 * Data Manager to to perform the item lookup/search operation.
 * 
 * @version $Revision: /main/1 $
 */
@SuppressWarnings("serial")
public class SetSearchInLocalStoreRoad extends LaneActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        cargo.setItemLookupLocaction(ReturnItemCargoIfc.ItemLookupType.STORE);
    }
}
