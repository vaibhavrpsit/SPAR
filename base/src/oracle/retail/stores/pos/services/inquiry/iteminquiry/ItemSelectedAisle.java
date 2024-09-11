/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ItemSelectedAisle.java /main/8 2014/06/11 13:22:16 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/10/14 - Modified to support retrieving extended data for
 *                         instore items.
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    sgu       12/21/12 - use locale requestor to pass locale information in
 *                         item service
 *    sgu       12/20/12 - use locale requestor
 *    cgreene   10/26/12 - formatting
 *    hyin      10/12/12 - use single ItemSearchCriteria for metaTag service.
 *    hyin      10/02/12 - fix jaxb infinitely loop read.
 *    jswan     09/24/12 - Added to support request of Advanced Item Search
 *                         through JPA.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.SearchItemListBeanModel;

/**
 * This aisle is traveled when the user selects an item. It stores the item in
 * the cargo.
 * 
 * @version $Revision: /main/8 $
 */
@SuppressWarnings("serial")
public class ItemSelectedAisle extends LaneActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/8 $";

    /**
     * Stores the item info and dept list in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Initialize bean model values
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utilMgr = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        PLUItemIfc item = null;

        SearchCriteriaIfc inquiry = cargo.getInquiry();
        SearchCriteriaIfc searchInquiry = (SearchCriteriaIfc)inquiry.clone();
        searchInquiry.setStoreNumber(cargo.getStoreStatus().getStore().getStoreID());
        if (ui.getModel() instanceof SearchItemListBeanModel) //coming from item list screen
        {
            SearchItemListBeanModel model = (SearchItemListBeanModel)ui.getModel();
            ItemSearchResult isr = model.getSelectedItem();
            searchInquiry.setItemID(isr.getItemID());
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
            ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
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
            inquiry.setRetrieveExtendedDataOnLocalPLULookup(retrieveExtendedDataOnLocalPLULookup);
            int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
            inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);
            item = mgr.getPluItem(searchInquiry);
            cargo.setPLUItem(item);
            cargo.setModifiedFlag(true);
            bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
        catch (DataException de)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            String msg[] = new String[1];
            msg[0] = utility.getErrorCodeString(de.getErrorCode());
            showErrorDialog(bus, "DatabaseError", msg, CommonLetterIfc.CANCEL);
        }
    }

    /**
     * Displays error Dialog
     * 
     * @param bus
     */
    private void showErrorDialog(BusIfc bus, String id, String[] args, String letter)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID(id);
        if (args != null)
        {
            model.setArgs(args);
        }
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
