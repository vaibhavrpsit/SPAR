/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ItemDetailDisplayedRoad.java /main/13 2014/07/10 14:02:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  11/25/14 - Moved previous fix to set inquiry object to null
 *                         as it only gets reset when extended data no
 *                         local item search is enabled. This prevents
 *                         NPE on otherwise scenario.
 *    abhinavs  11/19/14 - Setting inquiry object to null so when
 *                         it fetches extended data it doesn't take the stale
 *                         inquiry object.
 *    jswan     07/09/14 - Modified to retrieve extended item data for webstore
 *                         items when select from the item list.
 *    jswan     06/20/14 - Modified to support display of a Recommended Item.
 *    hyin      09/05/12 - meta tag item search: add to transaction
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:36 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/03/18 22:47:42  aschenk
 *   @scr 4079 and 4080 - Items were cleared after a help or cancelled cancel for an item inquiry.
 *
 *   Revision 1.4  2004/02/27 17:07:09  lzhao
 *   @scr 3841 Inquiry Options Enhancement
 *   Item will not be added unless Add button clicked.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:14   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 12 2003 11:21:06   RSachdeva
 * setModifiedFlag true
 * Resolution for POS SCR-2658: Inquiry Options on item inventory is automatically adding item to sale
 * 
 *    Rev 1.0   Apr 29 2002 15:22:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:52   msg
 * Initial revision.
 * 
 *    Rev 1.1   28 Jan 2002 22:44:06   baa
 * ui fixes
 * Resolution for POS SCR-230: Cross Store Inventory
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.0   Sep 21 2001 11:29:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.SearchItemListBeanModel;

//--------------------------------------------------------------------------
/**
    This road is traveled when the user enters the item
    number. It stores the item number in the cargo.
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ItemDetailDisplayedRoad extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    //----------------------------------------------------------------------
    /**
        Stores the item number in the cargo.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Initialize bean model values
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SearchItemListBeanModel model = (SearchItemListBeanModel) ui.getModel();

         ItemSearchResult item = model.getSelectedItem();

        // update cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        cargo.setItem(fetchExtendedDataSearchResult(bus, item));
    }

    /**
     * Fetch the item search result to include extended data if 1) RetrieveExtendedDataOnLocalItemSearch property 
     * is true, and 2) the extended data is not already available.
     * <p>
     * Ordinarily, this sort of code would only be included in an aisle in order to account for
     * exception conditions.  However, in this case, the application already has the Item Result object and
     * extended data is not mandatory.  If the read fails, the method logs the error and continues on with 
     * the item data it already has. 
     * <p/>
     * @param bus
     * @param item
     * @return
     */
    protected ItemSearchResult fetchExtendedDataSearchResult(BusIfc bus, ItemSearchResult item)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

        ItemSearchResult extendedSearchResult = item;
        boolean retrieveExtendedDataOnLocalItemSearch = Gateway.getBooleanProperty(
                Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedDataOnLocalItemSearch", false);
        
        // Determine if the ItemSearchResult came from the local store or the web store by
        // comparing the store current store ID to the store ID from the result object.
        String currentStoreID = cargo.getStoreStatus().getStore().getStoreID();
        boolean retrieveFromStore = true;
        if(!item.getStoreID().equals(currentStoreID))
        {
            retrieveFromStore = false;
        }

        // If....
        // 1) The ItemSearchResult is not null AND 
        // 2) ItemSearchResult does not already contain extended item data AND
        // 3) Either retrieve extended data on local item search OR the item was retrieve from the Web Store
        // 4) THEN retrieve the item again to get the extended data.
        if(item != null && item.getExtendedItemDataContainer() == null && (
                retrieveExtendedDataOnLocalItemSearch || retrieveFromStore))
        {
            Locale searchLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            String itemNo = item.getItemID();
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
            // Setting inquiry object to null so when it fetches
            // extended data it doesn't take the stale inquiry object.
            cargo.setInquiry(null);
            cargo.setInquiry(searchLocale, itemNo, itemDesc, deptID, geoCode, manufacturer, itemTypeCode, uomID, itemStyleCode, itemColorCode, itemSizeCode, itemList);
            SearchCriteriaIfc inquiryItem = cargo.getInquiry();
            inquiryItem.setPricingDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            inquiryItem.setRetrieveExtendedDataOnLocalItemSearch(retrieveExtendedDataOnLocalItemSearch);
            inquiryItem.setRetrieveFromStore(retrieveFromStore);
            inquiryItem.setStoreNumber(item.getStoreID());
            inquiryItem.setSearchItemByItemID(true);
            int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
            inquiryItem.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);

            ItemManagerIfc mgr = (ItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);
            try
            {
                AdvItemSearchResults aisr = mgr.searchPluItems(inquiryItem);
                if (aisr != null && aisr.getReturnItems().size() > 0)
                {
                    extendedSearchResult = aisr.getReturnItems().get(0);
                }
            }
            catch (DataException e)
            {
                logger.warn("Could not retrieve extended data for item search results. DataException Error Code: " + e.getErrorCode());
            }
        }
        return extendedSearchResult;
    }
}