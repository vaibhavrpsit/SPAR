/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/GetItemRoad.java /main/4 2014/06/11 13:22:16 jswan Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     11/20/14 - Set searchItemByItemNumber to false if 
*                        searchItemByItemID to true
* asinton     09/11/14 - Set the saved selectedTabIndex from the
*                        ItemInfoBeanModel into the cargo.
* jswan       06/10/14 - Modified to support retrieving extended data for
*                        instore items.
* yiqzhao     01/04/13 - Refactoring ItemManager
* sgu         12/21/12 - use locale requestor to pass locale information in
*                        item service
* sgu         12/20/12 - use locale requestor
* yiqzhao     11/07/12 - Read plu item when it is necessary.
* yiqzhao     10/25/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.SearchItemListBeanModel;

//--------------------------------------------------------------------------
/**
    This ailse is traveled when the user selects an item.
    It stores the item in the cargo.
    @version $Revision: /main/4 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class GetItemRoad extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/4 $";

    //----------------------------------------------------------------------
    /**
        Stores the item info and dept list  in the cargo.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // Initialize bean model values
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utilMgr = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo.getPLUItem()== null)
        {
            //has not been read
            PLUItemIfc item = null;
            
            SearchCriteriaIfc inquiry = (SearchCriteriaIfc) cargo.getInquiry();
            SearchCriteriaIfc searchInquiry = (SearchCriteriaIfc)inquiry.clone();
            searchInquiry.setStoreNumber(cargo.getStoreStatus().getStore().getStoreID());
            if (ui.getModel() instanceof SearchItemListBeanModel) //coming from item list screen
            {
                SearchItemListBeanModel model = (SearchItemListBeanModel) ui.getModel();
                ItemSearchResult isr = model.getSelectedItem();
                searchInquiry.setItemID(isr.getItemID());
                if (searchInquiry.isSearchItemByItemID())
                {
                    searchInquiry.setSearchItemByItemNumber(false);
                }
            }
            else if (ui.getModel() instanceof ItemInfoBeanModel) //coming from item detail screen
            {
                ItemInfoBeanModel model = (ItemInfoBeanModel) ui.getModel();
                searchInquiry.setItemID(model.getItemNumber());
            }
        
            try 
            {
                // retrieve PLU with all the supported locales
                searchInquiry.setLocaleRequestor(utilMgr.getRequestLocales());
                searchInquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
                if (searchInquiry.getPLURequestor() == null)
                {
                    searchInquiry.setPLURequestor(new PLURequestor());
                }
                
                if (cargo.isItemFromWebStore()) // web store search
                {
                    searchInquiry.setRetrieveFromStore(false);
                }
                else // local search
                {
                    searchInquiry.setRetrieveFromStore(true);
                }
                boolean retrieveExtendedDataOnLocalPLULookup = Gateway.getBooleanProperty(
                        Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedDataOnLocalPLULookup", false);
                int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
                inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);
                searchInquiry.setRetrieveExtendedDataOnLocalPLULookup(retrieveExtendedDataOnLocalPLULookup);
                
                ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
                item = mgr.getPluItem(searchInquiry);
                cargo.setPLUItem(item);
                cargo.setModifiedFlag(true);
                
            } 
            catch (DataException de) 
            {
                cargo.setErrorCode(de.getErrorCode());
            }
        }

        if (ui.getModel() instanceof ItemInfoBeanModel)
        {
            cargo.setSelectedTabIndex(((ItemInfoBeanModel) ui.getModel()).getSelectedTabIndex());
        }
    }
}
