/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/FilterSearchResultsRoad.java /main/2 2014/07/16 08:58:27 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    07/14/14 - CAE item search filtering cleanup
* abhinavs    05/10/14 - Filtering item search results enhancement
* abhinavs    04/16/14 - Initial version
* abhinavs    04/16/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This road set the flag indicating filtering of item search results
 * 
 * @since 14.1
 * @version $Revision: /main/2 $
 */
@SuppressWarnings("serial")
public class FilterSearchResultsRoad extends LaneActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        cargo.setFilterSearchResults(true);
        if(cargo.isItemFromWebStore())
        {
            cargo.getInquiry().setRetrieveFromStore(false);
            cargo.setWebstoreSearchButton(false);
            cargo.getInquiry().setMetaTagSearchStr("");
        }
    }
}