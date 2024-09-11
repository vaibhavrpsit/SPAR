/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ItemSelectedRoad.java /main/17 2013/01/07 11:08:04 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    hyin      10/15/12 - clean up webService methods.
 *    jswan     09/24/12 - Modified to support request of Advanced Item Search
 *                         through JPA.
 *    hyin      09/25/12 - additional CO PLUItem service work.
 *    hyin      09/19/12 - CO pluitem webservice work.
 *    hyin      09/05/12 - meta tag item search: add to transaction
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    11/16/09 - remove signal and properly set the modified flag in
 *                         the cargo
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:41 PM  Robert Pearse
 *
 *   Revision 1.6  2004/03/18 22:47:42  aschenk
 *   @scr 4079 and 4080 - Items were cleared after a help or cancelled cancel for an item inquiry.
 *
 *   Revision 1.5  2004/03/10 00:09:03  lzhao
 *   @scr 3840 InquiryOptions: Inventory Inquiry
 *
 *   Revision 1.4  2004/02/27 17:07:09  lzhao
 *   @scr 3841 Inquiry Options Enhancement
 *   Item will not be added unless Add button clicked.
 *
 *   Revision 1.3  2004/02/12 16:50:31  mcs
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
 *    Rev 1.0   Aug 29 2003 16:00:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jun 12 2003 11:19:24   RSachdeva
 * setModifiedFlag true
 * Resolution for POS SCR-2658: Inquiry Options on item inventory is automatically adding item to sale
 *
 *    Rev 1.0   Apr 29 2002 15:22:30   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:33:58   msg
 * Initial revision.
 *
 *    Rev 1.1   28 Jan 2002 22:44:12   baa
 * ui fixes
 * Resolution for POS SCR-230: Cross Store Inventory
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.0   Sep 21 2001 11:29:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemListBeanModel;
import oracle.retail.stores.pos.ui.beans.SearchItemListBeanModel;

import org.apache.commons.lang3.StringUtils;

//--------------------------------------------------------------------------
/**
    This road is traveled when the user selects an item
    It stores the item  in the cargo.
    @deprecated in 14.0 Need to display a Screen at this point.  Use ItemSelectedAilse.
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class ItemSelectedRoad extends LaneActionAdapter
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/17 $";

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
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        PLUItemIfc item = null;
        
        if (ui.getModel() instanceof ItemListBeanModel)
        {
            ItemListBeanModel model = (ItemListBeanModel) ui.getModel();
            item = (PLUItemIfc)model.getSelectedItem();
        }
        else 
        {
            SearchCriteriaIfc inquiry = (SearchCriteriaIfc) cargo.getInquiry();
            SearchCriteriaIfc searchInquiry = (SearchCriteriaIfc)inquiry.clone();
            searchInquiry.setStoreNumber(cargo.getStoreStatus().getStore().getStoreID());
            if (ui.getModel() instanceof SearchItemListBeanModel) //coming from item list screen
            {
                SearchItemListBeanModel model = (SearchItemListBeanModel) ui.getModel();
                ItemSearchResult isr = model.getSelectedItem();
                searchInquiry.setItemNumber(isr.getItemID());
            }
            else if (ui.getModel() instanceof ItemInfoBeanModel) //coming from item detail screen
            {
                ItemInfoBeanModel model = (ItemInfoBeanModel) ui.getModel();
                searchInquiry.setItemNumber(model.getItemNumber());
            }

            try {
                if (cargo.isItemFromWebStore()) // web store search
                {
//                    Locale[] locales = { LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE),
//                            LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
//                            LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)};           
//                    LocaleRequestor requestor = new LocaleRequestor(locales);
//                    searchInquiry.setLocaleRequestor(requestor);
//                    
//                    ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
//                    item = mgr.getPluItem(searchInquiry);
                    String pluID = searchInquiry.getItemNumber();
                    String storeID = ""; //storeID will be loaded through formatter
                    List<Locale> locales = new ArrayList<Locale>();
                    Locale defaultLoc = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
                    Locale userLoc = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
                    Locale journalLoc = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
                    locales.add(defaultLoc);
                    locales.add(userLoc);
                    locales.add(journalLoc);
                    Calendar date = Calendar.getInstance(defaultLoc); 
                    String geoCode = "";
                    String localesStr = getLocalesStr(locales);
                    
                    //Use ItemSelectedAisle
                    //ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
                    //item = mgr.lookupPluItem(pluID, storeID, date, geoCode, localesStr);
                } else // local search
                {
                    ItemManagerIfc mgr = (ItemManagerIfc) bus.getManager(ItemManagerIfc.TYPE);
                    searchInquiry.setRetrieveFromStore(true);
                    item = mgr.getPluItem(searchInquiry);
                }
            } catch (DataException de) {
                UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                String msg[] = new String[1];
                msg[0] = utility.getErrorCodeString(de.getErrorCode());
                showErrorDialog(bus, "DatabaseError", msg,  CommonLetterIfc.RETRY);
            }
            
        }

        cargo.setPLUItem(item);
        cargo.setModifiedFlag(true);
    }
    
    protected String getLocalesStr(List<Locale> locales)
    {
        StringBuffer rtnStr = new StringBuffer();
        for (int i=0; i<locales.size(); i++)
        {
            Locale loc = locales.get(i);
            if (loc != null)
            {
                if (rtnStr.length() > 0)
                {
                    rtnStr.append(",");
                }
                String lang = loc.getLanguage();
                String ctry = loc.getCountry();
                if (StringUtils.isEmpty(ctry))
                {
                    rtnStr.append(lang);
                }
                else
                {
                    rtnStr.append(lang + "_" + ctry);
                }
            }
        }
        
        return rtnStr.toString();
    }
    
    //----------------------------------------------------------------------
    /**
     *   Displays error Dialog
     *   @param bus
     */
    //----------------------------------------------------------------------
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
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,letter);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
}
