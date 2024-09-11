/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/06/14 - Changing condition on size due to recent 
 *                         'UNDO' action refactoring.
 *    abhinavs  09/04/14 - Minor tweaks to implement correct UNDO action 
 *                         on item filtering results set
 *    asinton   09/03/14 - initial checking.  Executes logic to decide
 *                         where to go when Undo is selected.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Handles the Undo letter from the {@link ShowItemListSite} in the ItemInquiry tour.
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class UndoItemListActionSite extends SiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter =  CommonLetterIfc.SEARCH;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.getItemSearchResults().size() >= 1)
        {
            letter = "ItemList";
        }
        else if(cargo.isAdvanceSearch())
        {
            letter = "AdvanceSearch";
            cargo.setInquiry(null);
        }
        if(cargo.getItemSearchResults().size() <= 1)
        {
            cargo.setFilterSearchResults(false);
            if(cargo.getAdvancedSearchResult().isWebstoreItems())
            {
                cargo.setItemFromWebStore(true);
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

}
