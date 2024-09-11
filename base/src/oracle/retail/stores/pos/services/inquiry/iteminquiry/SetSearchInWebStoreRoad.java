/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/SetSearchInWebStoreRoad.java /main/2 2013/04/24 14:41:16 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  04/24/13 - Fix to enable search webstore button on item list
 *                         screen
 *    jswan     01/10/13 - Added to support item manager rework.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This road sets the SearchCriteria object so that Item Manager will call the
 * web service to to perform the item lookup/search operation.
 * 
 * @version $Revision: /main/2 $
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
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        cargo.getInquiry().setRetrieveFromStore(false);
        cargo.setWebstoreSearchButton(false);
    }
}
