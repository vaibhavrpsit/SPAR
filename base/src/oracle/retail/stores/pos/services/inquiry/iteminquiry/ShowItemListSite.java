/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ShowItemListSite.java /main/23 2014/07/16 08:58:27 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY) 
 *    abhinavs  11/25/14 - Fixing Bug 20089419. In addition to this,
 *                         also fixing a defect found in post screen naming
 *                         code changes.
 *    abhinavs  09/22/14 - Adding style and item type as filtering criteria.
 *    abhinavs  09/04/14 - Minor tweaks to implement correct UNDO action 
 *                         on item filtering results set
 *    tksharma  08/18/14 - Added code to change screen name for filtered
 *                         item results
 *    abhinavs  07/14/14 - CAE item search results filtering cleanup
 *    abhinavs  06/11/14 - Filtering item search results cleanup
 *    abhinavs  05/20/14 - Filtering item search results enhancement phaseII
 *    abhinavs  05/09/14 - Filtering item search results enhancement
 *    abhinavs  12/20/13 - Fix to enable webstore search button only when
 *                         xchannel is enabled
 *    abhinavs  04/24/13 - Fix to enable search webstore button on item list
 *                         screen
 *    mkutiana  01/03/13 - Remove return request button from nav button panel
 *    blarsen   09/11/12 - Merge project Echo (MPOS) into Trunk.
 *    cgreene   03/16/12 - check for null when accessing register for mobilepos
 *    hyin      08/31/12 - meta tag search POS UI work.
 *    rsnayak   10/07/11 - Enable disable Request ticket button
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         2/26/2008 6:33:59 AM   Naveen Ganesh
 *         unnecessary SOPs have been removed.
 *    4    360Commerce 1.3         11/22/2007 10:59:06 PM Naveen Ganesh   PSI
 *         Code checkin
 *    3    360Commerce 1.2         3/31/2005 4:30:00 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:18 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/18 22:47:42  aschenk
 *   @scr 4079 and 4080 - Items were cleared after a help or cancelled cancel for an item inquiry.
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
 *    Rev 1.1   Jan 26 2004 12:05:02   kll
 * attach department description to the item in question
 * Resolution for 3120: Item Inquiry is looking at the incorrect Column in the Tables for the Department
 *
 *    Rev 1.0   Aug 29 2003 16:00:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:22:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:00   msg
 * Initial revision.
 *
 *    Rev 1.1   28 Jan 2002 22:44:14   baa
 * ui fixes
 * Resolution for POS SCR-230: Cross Store Inventory
 * Resolution for POS SCR-824: Application crashes on Customer Add screen after selecting Enter
 *
 *    Rev 1.0   Sep 21 2001 11:29:48   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.gui.UIConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.SearchItemListBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.utility.DynamicDropdownsUtility;

/**
 * This site displays the ITEM_LIST screen.
 */
@SuppressWarnings("serial")
public class ShowItemListSite extends PosSiteActionAdapter
{

    protected static final String PSI_ENABLED_PROPERTY = "PSIEnabled";
    
    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    
    /**
     * Constant for cross channel enabled.
     */
    public static final String XCHANNEL_ENABLED = "XChannelEnabled";
    
    /** Filtered Item List Screen name prop*/
    protected static final String FL_ITM_LST_SC_NAME_TAG = "FilteredItemListScreenName";
    
    /**  Filtered Item List Screen name value*/
    protected static final String FL_ITM_LST_SC_NAME_DFLT = "Filtered Item List";
    
    /** Webstore Item list screen name prop */
    protected static final String WEBSTORE_ITM_LST_SC_NAME_TAG = "WebstoreItemListScreenName";
    
    /** Webstore Item list screen name value */
    protected static final String WEBSTORE_ITM_LST_SC_NAME_DFLT = "Webstore Item List";
    

    /**
     * Displays the ITEMS_LIST screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String all = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel",
                "<All>", uiLocale);
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        // Using utility class to dynamically populating dropdowns for filtering
        // the search result
        DynamicDropdownsUtility dropDowns = DynamicDropdownsUtility.getInstance();
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        boolean isXChannelEnabled = Gateway
                .getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
        // Enabling search webstore button by default
        boolean enableWebStore = isXChannelEnabled && !cargo.getAdvancedSearchResult().isUsingDerby();
        navModel.setButtonEnabled(CommonActionsIfc.WEB_STORE, enableWebStore);
        if (cargo.isFilterSearchResults() || cargo.isItemFromWebStore())
        {
            // Disabling search webstore button when user opted for filtering
            // the result
            // This is done in order to perform clean filtering
            navModel.setButtonEnabled(CommonActionsIfc.WEB_STORE, false);
        }
        if (cargo.getItemSearchResults().size() >= 1)
        {
            if (!cargo.getItemSearchResults().getLast().equals(cargo.getAdvancedSearchResult()))
            {
                cargo.getItemSearchResults().add(cargo.getAdvancedSearchResult());
            }
            if (cargo.getItemSearchResults().size() == 1 && !cargo.getAdvancedSearchResult().isWebstoreItems())
            {
                navModel.setButtonEnabled(CommonActionsIfc.WEB_STORE, enableWebStore);
            }
        }
        else
        {
            cargo.getItemSearchResults().add(cargo.getAdvancedSearchResult());
            cargo.setItemsSearchOrigCriteria((SearchCriteriaIfc)cargo.getInquiry().clone());
        }
        AdvItemSearchResults aisr = cargo.getAdvancedSearchResult();
        // update bean model with matching items list
        SearchItemListBeanModel model = new SearchItemListBeanModel();
        model.setItemList(aisr.getReturnItems());
        // enable/disable Inventory Inquiry button
        navModel.setButtonEnabled(CommonActionsIfc.INVENTORY, isInventoryInquirySupported(bus));
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        ArrayList<String> filterFields = new ArrayList<String>();

        List<String> availableFields = dropDowns.getAvailableItemSearchFields(pm, true,
                filterFields);

        boolean searchForItemByType = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_TYPE);
        boolean searchForItemByUOM = availableFields.contains(CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        boolean searchForItemByStyle = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_STYLE);
        boolean searchForItemByColor = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_TYPE);
        boolean searchForItemBySize = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_SIZE)
                && dropDowns.getSizeRequired(pm);
          

        try
        {
            // Filter results button is disabled if no filtering criterion is
            // applied
            if ((dropDowns.deptList(cargo, bus, utility, uiLocale, all).length > UIConstantsIfc.MIN_DROPDOWN_SIZE
                    || dropDowns.yesAndNo(cargo, all, utility, uiLocale, DynamicDropdownsUtility.CLEARANCE).length > UIConstantsIfc.MIN_DROPDOWN_SIZE
                    || dropDowns.yesAndNo(cargo, all, utility, uiLocale, DynamicDropdownsUtility.DISCOUNT).length > UIConstantsIfc.MIN_DROPDOWN_SIZE 
                    || dropDowns.yesAndNo(cargo, all, utility, uiLocale, DynamicDropdownsUtility.TAX).length > UIConstantsIfc.MIN_DROPDOWN_SIZE)
                    || (dropDowns.sizeList(cargo, bus, utility, uiLocale, all).length > UIConstantsIfc.MIN_DROPDOWN_SIZE && searchForItemBySize)
                    || (dropDowns.colorList(cargo, bus, utility, uiLocale, all).length > UIConstantsIfc.MIN_DROPDOWN_SIZE && searchForItemByColor)
                    || (dropDowns.uomList(cargo, bus, utility, uiLocale, all).length > UIConstantsIfc.MIN_DROPDOWN_SIZE && searchForItemByUOM)
                    || (dropDowns.styleList(cargo, bus, utility, uiLocale, all).length > UIConstantsIfc.MIN_DROPDOWN_SIZE && searchForItemByStyle)
                    || (dropDowns.itemTypeList(cargo, bus, utility, uiLocale, all).length > UIConstantsIfc.MIN_DROPDOWN_SIZE && searchForItemByType))
            {
                navModel.setButtonEnabled(CommonActionsIfc.FILTER_RESULTS, true);
            }
            else
            {
                navModel.setButtonEnabled(CommonActionsIfc.FILTER_RESULTS, false);
            }
        }
        catch (DataException ex)
        {
            String msg[] = new String[1];
            msg[0] = utility.getErrorCodeString(ex.getErrorCode());
            showErrorDialog(bus, "DatabaseError", msg, CommonLetterIfc.UNDO);
        }
        model.setLocalButtonBeanModel(navModel);
        if (cargo.isFilterSearchResults())
        {
            StatusBeanModel sbm = new StatusBeanModel();
            String screenName = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                    BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, FL_ITM_LST_SC_NAME_TAG, FL_ITM_LST_SC_NAME_DFLT);
            sbm.setScreenName(screenName);
            model.setStatusBeanModel(sbm);
        }
        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        if((cargo.isItemFromWebStore() && !cargo.isFilterSearchResults() && cargo.getItemSearchResults().size() > 1) || cargo.getAdvancedSearchResult().isWebstoreItems())
        {
            StatusBeanModel sbm = new StatusBeanModel();
            String screenName = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC, BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                    WEBSTORE_ITM_LST_SC_NAME_TAG, WEBSTORE_ITM_LST_SC_NAME_DFLT);
            sbm.setScreenName(screenName);
            model.setStatusBeanModel(sbm);
        }
        ui.showScreen(POSUIManagerIfc.ITEMS_LIST, model);
    }

    /**
     * This method checks whether the Inventory Inquiry is supported or not.
     */
    public boolean isInventoryInquirySupported(BusIfc bus)
    {
        boolean supported = false;

        try
        {
            ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

            // 1. Check whether user has access to Inventory Inquiry Function
            SecurityManagerIfc securityManager = (SecurityManagerIfc)Gateway.getDispatcher().getManager(
                    SecurityManagerIfc.TYPE);
            boolean access = securityManager.checkAccess(cargo.getAppID(), RoleFunctionIfc.INVENTORY_INQUIRY);

            // 2. Check wthether Inventory Inquiry is Enabled or not
            Boolean installedFlag = new Boolean(Gateway.getProperty("application", PSI_ENABLED_PROPERTY, "false"));

            // 3. Check whether the Reentry option is on or off
            boolean isReentryMode = isTransReentryMode(cargo.getRegister());

            if (access && installedFlag.booleanValue() && !isReentryMode)
            {
                supported = true;
            }
        }
        catch (Exception e)
        {
            logger.warn("Error while getting Inventory Inquiry Supported Flags");
            supported = false;
        }

        return supported;
    }

    public boolean isLatEnabled(BusIfc bus)
    {
        boolean supported = false;

        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        // 1. Check wthether LAT is installed or not
        Boolean installedFlag = new Boolean(Gateway.getProperty("application", "LatWebServiceEnabled", "false"));
        // 2. Check whether the Training mode is on or off
        boolean isTrainingMode = isTrainingMode(cargo.getRegister());
        // 3. Check whether the Reentry option is on or off
        boolean isReentryMode = isTransReentryMode(cargo.getRegister());
        if (installedFlag.booleanValue() && !isTrainingMode && !isReentryMode)
        {
            supported = true;
        }
        else if (isTrainingMode)
        {
            supported = false;
        }
        else if (isReentryMode)
        {
            supported = false;
        }
        return supported;
    }

    protected boolean isTrainingMode(RegisterIfc register)
    {
        if (register != null)
        {
            return register.getWorkstation().isTrainingMode();
        }
        return false;
    }


    protected boolean isTransReentryMode(RegisterIfc register)
    {
        if (register != null)
        {
            return register.getWorkstation().isTransReentryMode();
        }
        return false;
    }
    
    /**
     * Displays error Dialog
     * 
     * @param bus
     * @since 14.1
     */
    private void showErrorDialog(BusIfc bus, String id, String[] args, String letter)
    {
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

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
    
    /**
     * Setting original item search criteria when "Search webstore" is selected
     * for Xchannel item search
     *  
     * @param bus Service Bus
     */
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        if (cargo != null && cargo.getInquiry() != null)
        {
            String letterName = null;
            if (letter instanceof ButtonPressedLetter) // Is ButtonPressedLetter
            {
                // Get the String representation of the letter name
                // from the LetterIfc object
                letterName = letter.getName();
                if (CommonActionsIfc.WEB_STORE.equals(letterName))
                {
                    cargo.setItemSearchOrigCriteria(true);
                    cargo.getItemsSearchOrigCriteria().setRetrieveFromStore(false);
                }
                
                if(CommonActionsIfc.ADD.equals(letterName)  && cargo.getAdvancedSearchResult() != null)
                {
                    if (cargo.getAdvancedSearchResult().isWebstoreItems())
                     {
                         cargo.setItemFromWebStore(true);
                     }
                }
            }
        }

    }

}


