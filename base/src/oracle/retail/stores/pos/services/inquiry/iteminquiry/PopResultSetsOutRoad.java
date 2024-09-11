/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/PopResultSetsOutRoad.java /main/1 2014/05/16 16:28:25 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* asinton     10/08/14 - Fix tour ItemInquriy tour flow.
* asinton     09/03/14 - added check for empty list
* abhinavs    05/09/14 - Filtering Item search results enhancement
* abhinavs    05/09/14 - Initial Version
* abhinavs    05/09/14 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This road is traveled when user clicks undo on item list screen. It set the
 * item search results on cargo from the linked list and subsequently popping out the last element from the same.
 * 
 * @Since 14.1
 */
public class PopResultSetsOutRoad extends LaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3015445794269438542L;

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse
     * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.getItemSearchResults().size() > 1)
        {
            cargo.getItemSearchResults().removeLast();
            cargo.setAdvancedSearchResult(cargo.getItemSearchResults().getLast());
        }
        else if (cargo.getItemSearchResults().size() > 0)
        {
            cargo.getItemSearchResults().removeLast();
            cargo.setInquiry(null);
        }
    }
}