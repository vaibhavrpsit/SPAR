/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/ServiceItemSelectedAisle.java /main/15 2013/01/07 11:08:04 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    jswan     09/25/12 - Modified to support retrieval of the list of Service
 *                         (non-merchandise) items.
 *    jkoppolu  04/19/11 - Set PosItemId in the search criteria
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       12/08/09 - rework PLURequestor to use EnumSet and rename
 *                         set/unsetRequestType to add/removeRequestType
 *    sgu       12/04/09 - write error to the log if the selected service item
 *                         is not found
 *    mahising  02/22/09 - Fixed issue for Gift Card wrapper service
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:56 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:25:12 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:14:10 PM  Robert Pearse
 * $
 * Revision 1.8  2004/05/27 19:31:33  jdeleau
 * @scr 2775 Remove unused imports as a result of tax engine rework
 *
 * Revision 1.7  2004/05/27 17:12:48  mkp1
 * @scr 2775 Checking in first revision of new tax engine.
 *
 * Revision 1.6  2004/04/20 13:17:05  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.5  2004/04/14 15:17:09  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.4  2004/03/03 23:15:06  bwf
 * @scr 0 Fixed CommonLetterIfc deprecations.
 *
 * Revision 1.3  2004/02/12 16:51:03  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:39:28  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:18
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.0 Aug 29 2003 16:01:50 CSchellenger Initial revision.
 *
 * Rev 1.3 Dec 13 2002 14:11:48 sfl Store address state/province data is
 * included to support Canadian tax rule lookup. Resolution for POS SCR-1749:
 * POS 6.0 Tax Package
 *
 * Rev 1.2 11 Sep 2002 10:22:24 sfl Added tax rule/rate database query when
 * service item is brought in through modifyitem -> services path instead of
 * the standard item id type-in approach. Resolution for POS SCR-1749: POS 5.5
 * Tax Package
 *
 * Rev 1.1 Aug 21 2002 11:21:26 DCobb Added Alterations service. Resolution for
 * POS SCR-1753: POS 5.5 Alterations Package
 *
 * Rev 1.0 Apr 29 2002 15:17:18 msg Initial revision.
 *
 * Rev 1.0 Mar 18 2002 11:37:40 msg Initial revision.
 *
 * Rev 1.3 Mar 08 2002 15:57:56 dfh added serviceitemflag to itemcargo, set
 * this flag to true when services item added, journals this item when
 * returning to pos after successfully adding the services item Resolution for
 * POS SCR-1123: Non Merchandise items selected from list not appearing on EJ
 *
 * Rev 1.2 Jan 04 2002 12:09:32 dfh updates to test if transaction is null, may
 * not have trans in cargo yet..... Resolution for POS SCR-260: Special Order
 * feature for release 5.0
 *
 * Rev 1.1 Dec 11 2001 20:52:48 dfh checks for special order able, displays
 * special order item error screen if not special order able Resolution for POS
 * SCR-260: Special Order feature for release 5.0
 *
 * Rev 1.0 Sep 21 2001 11:29:04 msg Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

// foundation imports
import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.arts.PLUTransaction;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteria;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ServiceItemListBeanModel;

//--------------------------------------------------------------------------
/**
 * This aisle is traversed when a service item has been selected.
 * <p>
 *
 * @version $Revision: /main/15 $
 */
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ServiceItemSelectedAisle extends PosLaneActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * special order item error screen name
     */
    public static final String SPECIAL_ORDER_ITEM_RESOURCE_ID = "SpecialOrderItemError";
    public static final String ITEM_NOT_FOUND_RESOURCE_ID = "ItemNotFoundError";

    //----------------------------------------------------------------------
    /**
     * Tests if item available for special order, if not displays the special
     * orderi item error screen, otherwise sets the price of the item.
     * <P>
     *
     * @param bus
     *            Service Bus
     */
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        /*
         * Grab the item from the ui
         */
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ServiceItemListBeanModel beanModel;
        beanModel =
            (ServiceItemListBeanModel) ui.getModel(
                POSUIManagerIfc.NON_MERCHANDISE);
    	ItemCargo cargo = (ItemCargo)bus.getCargo();
    	ItemSearchResult item = beanModel.getSelectedServiceItem();
    	String pluItemID = item.getItemID();

        try
        {
            // retrieve a fully populated PLU for the selected service item.
        	// The plu items retrieved for the service item drop down are
            // only populated with the necessary info to render the dropdown UI.
            String storeID = cargo.getStoreStatus().getStore().getStoreID();
            String geoCode = cargo.getStoreStatus().getStore().getGeoCode();

            SearchCriteriaIfc inquiry = new SearchCriteria();
            inquiry.setItemID(item.getItemID());
            inquiry.setStoreNumber(storeID);
            inquiry.setGeoCode(geoCode);
            PLURequestor pluRequestor = new PLURequestor();
            pluRequestor.removeRequestType(PLURequestor.RequestType.RelatedItems);
            inquiry.setPLURequestor(pluRequestor);
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            inquiry.setRetrieveFromStore(true);
            
            ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
            PLUItemIfc pluItem = mgr.getPluItem(inquiry);

            // if special order in progress and item not special orderable show
            // error dialog
            if (cargo.getTransaction() != null
                    && (cargo.getTransaction().getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE && ((OrderTransactionIfc)cargo
                            .getTransaction()).getOrderType() == OrderConstantsIfc.ORDER_TYPE_SPECIAL)
                && pluItem.isSpecialOrderEligible() == false)
            {
                // Using "generic dialog bean". display the error dialog
                DialogBeanModel model = new DialogBeanModel();

                // Set model to same name as dialog
                // Set button and arguments
                model.setResourceID(SPECIAL_ORDER_ITEM_RESOURCE_ID);
                model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                model.setButtonLetter(
                    DialogScreensIfc.BUTTON_OK,
                    CommonLetterIfc.CANCEL);

                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
            else
            {
                String letterName = CommonLetterIfc.CONTINUE;

                // Check for alteration service item
                if (pluItem instanceof AlterationPLUItemIfc)
                {
                    letterName = "Alterations";
                    cargo.setAlterationItemFlag(true);
                }

                cargo.setPLUItem(pluItem);
                cargo.setServiceItemFlag(true); // valid service item selected...
                bus.mail(new Letter(letterName), BusIfc.CURRENT);
            }

        }
        catch (DataException de)
        {
            logger.error( "The selected service items is not found: " + de.getMessage() + "");

        	 // Using "generic dialog bean". display the error dialog
            DialogBeanModel model = new DialogBeanModel();

            // Set model to same name as dialog
            // Set button and arguments
            model.setResourceID(ITEM_NOT_FOUND_RESOURCE_ID);
            String[] args = new String[1];
            args[0] = pluItemID;
            model.setArgs(args);
            model.setType(DialogScreensIfc.ERROR);
            model.setButtonLetter(
                DialogScreensIfc.BUTTON_OK,
                CommonLetterIfc.CANCEL);

            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
        }
    }

}
