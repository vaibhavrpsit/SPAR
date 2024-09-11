/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/03/14 - Fixing corner case of 'UNDO' action
 *                         when search criteria takes directly to
 *                         the item detail screen.
 *    asinton   09/24/14 - managing the cargo.getItemSearchResults list to
 *                         prevent application hang.
 *    asinton   09/03/14 - initial checkin.  Executes logic to dicide
 *                         where to go when Undo is selected.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * Handles the Undo letter from the {@link DisplayItemSite}.
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class UndoDisplayItemActionSite extends SiteActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        String letter = CommonLetterIfc.SEARCH;
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.isDisplayRecommendedItem())
        {
            letter = CommonLetterIfc.UNDO;
        }
        else if (cargo.getItemSearchResults() != null && cargo.getItemSearchResults().size() > 0)
        {
            // If the result list has only one AdvItemSearchResults and that has only one item, then mail 
            //letter "AdvanceSearch" only if the flag is true.
            if(cargo.getItemSearchResults().size() == 1 &&
                    ((AdvItemSearchResults)cargo.getItemSearchResults().get(0)).getReturnItems() != null &&
                    ((AdvItemSearchResults)cargo.getItemSearchResults().get(0)).getReturnItems().size() == 1)
            {
                cargo.setInquiry(null);
                cargo.getItemSearchResults().pop();
                if(cargo.isAdvanceSearch())
                {
                    letter = "AdvanceSearch";
                }
            }
            else
            {
                letter = "ItemList";
            }
        }
        
        else if(cargo.isAdvanceSearch())
        {
            letter = "AdvanceSearch";
        }
        // if we're off to search again then clear the inquiry instance
        if("AdvanceSearch".equals(letter) || CommonLetterIfc.SEARCH.equals(letter))
        {
            cargo.setInquiry(null);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

}
