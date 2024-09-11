/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/RecommendedItemSearchRoad.java /main/1 2014/06/22 09:20:30 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/16/14 - Added to support display of extended data
 *                         recommended items from the Sale Item
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * Creates the search criteria instance for the recommended item search.
 * @since 14.1
 *
 */
@SuppressWarnings("serial")
public class RecommendedItemSearchRoad extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        Locale searchLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String itemNo = cargo.getSelectedRecommendedItemId();
        String itemDesc = null;
        String deptID = null;
        String geoCode = null;
        String manufacturer = null;
        String itemTypeCode = null;
        String uomID = null;
        String itemStyleCode = null;
        String itemColorCode = null;
        String itemSizeCode = null;
        List<ItemSearchResult> itemList = null;
        
        cargo.setInquiry(searchLocale, itemNo, itemDesc, deptID, geoCode, manufacturer, itemTypeCode, uomID, itemStyleCode, itemColorCode, itemSizeCode, itemList);
        SearchCriteriaIfc inquiryItem = cargo.getInquiry();
        inquiryItem.setStoreNumber(cargo.getStoreStatus().getStore().getStoreID());
    }
}
