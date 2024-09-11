/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/SetSearchInWebStoreRoad.java /main/1 2013/03/08 15:44:39 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan        11/03/14 - added UIN (SIM) lookup to the non-receipted returns flow.
 *    mkutiana     03/08/13 - Set flag on the SearchCriteria to NOT use the
 *                            Store to retrieve
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;

/**
 * This road sets the SearchCriteria object so that Item Manager will call the
 * web service to to perform the item lookup/search operation.
 * 
 * @version $Revision: /main/1 $
 */
@SuppressWarnings("serial")
public class SetSearchInWebStoreRoad extends LaneActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        cargo.setItemLookupLocaction(ReturnItemCargoIfc.ItemLookupType.WEB);   
    }
}
