/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/AdvanceSearchSite.java /main/33 2014/05/16 16:28:24 abhinavs Exp $
=======
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/AdvanceSearchSite.java /main/33 2014/05/16 16:28:24 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *   abhinavs  10/03/14 - Setting selected clearance index from the dropdown
 *                        to include it in verifying the condition if atleast one
 *                        search criteria is given.
 *    yiqzhao   09/22/14 - Get the parameter value from the utility, cleanup.
 *    abhinavs  04/30/14 - Filter item search results enhancement
 *    yiqzhao   03/21/14 - Enable item id wild card search in advance search
 *                         screen.
 *    abhinavs  12/20/13 - Fix to enable webstore search button only when
 *                         xchannel is enabled
 *    abhinavs  04/24/13 - Fix to enable search webstore button on item list
 *                         screen
 *    abhinavs  04/19/13 - Fix to implement correct undo action on item inquiry
 *                         screen
 *    hyin      01/29/13 - allowing user to only enter description on item
 *                         detail search.
 *    hyin      11/14/12 - reset inquiry fields.
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *    cgreene   09/10/12 - Popup menu implementation
 *    hyin      08/15/12 - new meta tag adv. search feature
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mchellap  01/12/09 - Department search field changes
 *    mchellap  01/05/09 - Department search field changes
 *    mchellap  12/23/08 - Changes for searchForItemByManufacturer parameter
 *    mchellap  12/02/08 - Changes for Item search field parameter update
 *    ranojha   10/29/08 - Changes for Return, UOM and Department Reason Codes
 *    sgu       10/24/08 - externaliz item type
 *    ranojha   10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    ranojha   10/17/08 - Changes for code review
 *    ranojha   10/17/08 - Changes for UnitOfMeasure and Item Size/Color and
 *                         Style
 *    miparek   10/17/08 - dept list changes for locale requestor
 *    miparek   10/17/08 - dept list changes with locale requestor
 *    mchellap  10/16/08 - formatting
 *    mchellap  10/16/08 - Advance Item Inquiry Changes
 *    mchellap  10/16/08 - Advance Item Inquiry
 *    ranojha   10/14/08 - Fixed getSupportedLocales method in UtilityManager
 *    ranojha   10/13/08 - Working on Item Style, Size and Color changes
 *    ddbaker   10/09/08 - Refactor of reference implementation of POS I18N
 *                         Persistence
 *    ddbaker   10/06/08 - Preliminary I18N Persistence Updates for Size, Style
 *                         and Color.
 *    mchellap  09/30/08 - Updated copy right header
 *
 *
 *
 *     $Log:
 *      4    360Commerce 1.3         12/13/2005 4:42:41 PM  Barry A. Pape
 *           Base-lining of 7.1_LA
 *      3    360Commerce 1.2         3/31/2005 4:27:12 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:19:35 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:09:26 PM  Robert Pearse
 *     $
 *     Revision 1.7  2004/06/18 17:45:39  dfierling
 *     @scr 5651 - Advanced Price Inquiry fields retain values fix
 *
 *     Revision 1.6  2004/05/03 18:30:29  lzhao
 *     @scr 4544, 4556: keep user entered info when back to the page.
 *
 *     Revision 1.5  2004/04/09 17:57:00  lzhao
 *     @scr 4294: populate item id if it is entered before.
 *
 *     Revision 1.4  2004/03/16 18:30:46  cdb
 *     @scr 0 Removed tabs from all java source code.
 *
 *     Revision 1.3  2004/02/26 22:10:11  lzhao
 *     @scr 3841 Inquiry Options Enhancement.
 *     code review follow up.
 *
 *     Revision 1.2  2004/02/19 18:06:24  lzhao
 *     @scr 3841 Inquiry Options Enhancement.
 *     Modify comments.
 *
 *     Revision 1.1  2004/02/16 22:42:17  lzhao
 *     @scr 3841:Inquiry Option Enhancement
 *     add price inquiry and advance search screens.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;


import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.AdvancedInquiryDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemInquiryBeanModel;
import oracle.retail.stores.pos.utility.DynamicDropdownsUtility;


/**
 * This site displays the ITEM_INFO_QUERY screen.
 * 
 * @version $Revision: /main/33 $
 */
@SuppressWarnings("serial")
public class AdvanceSearchSite extends PosSiteActionAdapter
{
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/33 $";

    /**
     * UI wildcard tag
     */
    public static final String UI_WILDCARD_TAG = "UIWildcard";

    /**
     * UI wildcard
     */
    public static final String UI_WILDCARD = "*";

    /**
     * DB wildcard tag
     */
    public static final String DB_WILDCARD_TAG = "DBWildcard";

    /**
     * DB wildcard
     */
    public static final String DB_WILDCARD = "%";

    /**
     * default string for department list
     */
    public static final String DEFAULT_ALL = "<All>";

    /**
     * default string for search item by manufacturer
     */
    public static final String SEARCH_ITEM_BY_MANUFACTURER = "SearchForItemByManufacturer";

    /**
     * default selected values for combo
     */
    public static final String DEFAULT_SELECTED_VALUE = "-1";

    /**
     * Item Search fields parameter
     */
    public static final String ITEM_SEARCH_FIELDS = "ItemSearchFields";

    /**
     * Item Search fields parameter
     */
    public static final String DEPARTMENT_FIELD = "Department";

    /**
     * default string for item type field
     */
    public static final String ITEM_TYPE_FIELD = "ItemType";

    /**
     * default string for Unit Of Measure field
     */
    public static final String UOM_FIELD = "UnitOfMeasure";

    /**
     * default string for Style field
     */
    public static final String STYLE_FIELD = "Style";

    /**
     * default string for Color field
     */
    public static final String COLOR_FIELD = "Color";
    
    /**
     * default string for Size field
     */
    public static final String SIZE_FIELD = "Size";

    /**
     * item type label
     */
    public static final String ITEM_TYPE_LABEL = "ItemTypeLabel";

    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

    /**
     * Constant for Meta Tag Search.
     */
    public static final String METATAG_SEARCH_ENABLED = "MetaTagSearchEnabled";
    
    /**
     * Displays the ADV_SEARCH screen.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        cargo.setSimpleSearchTypeFlow(false);
        //Setting advancedsearch results to null to begin new search
        cargo.setAdvancedSearchResult(null);
        // update model with department list
        ItemInquiryBeanModel model = new ItemInquiryBeanModel();
        
        boolean isMetaTagSearch = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, METATAG_SEARCH_ENABLED,
                false);
        if (isMetaTagSearch)
        {
            model.setMetaTagAdvSearch(true);
        }
        else
        {
            // old flow
            setupConfigurableSearch(model, bus, ui);
        }
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        model.setItemSizeRequired( DynamicDropdownsUtility.getInstance().getSizeRequired(pm));
        
        model.setSelected(true);
        ui.showScreen(POSUIManagerIfc.ITEM_SEARCH, model);
    }

    private void setupConfigurableSearch(ItemInquiryBeanModel model, BusIfc bus, POSUIManagerIfc ui)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        String storeID = cargo.getOperator().getStoreID();

        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        boolean searchForItemByManufacturer = false;
        try
        {
            searchForItemByManufacturer = pm.getBooleanValue(SEARCH_ITEM_BY_MANUFACTURER);
        }
        catch (ParameterException pe)
        {
            logger.error("Cannot retrive search by manufacture field parameter value");
        }
        // Adding the value of the parameter to the cargo
        cargo.setSearchItemByManufacturer(searchForItemByManufacturer);

        // Configurable Search
        model.setSearchItemByManufacturer(searchForItemByManufacturer);
      
        boolean comingFromResultPage = (cargo.getInquiry() != null) && (cargo.getInquiry().isSearchFromItemDetail());
        ArrayList<String> comingFromResultPageRelatedFields = new ArrayList<String>();
        comingFromResultPageRelatedFields.add(ITEM_TYPE_FIELD);
        comingFromResultPageRelatedFields.add(STYLE_FIELD);
        
        List<String> availabeFields = DynamicDropdownsUtility.getInstance().getAvailableItemSearchFields(pm, comingFromResultPage, comingFromResultPageRelatedFields);
        
        boolean searchForItemByDepartment = availabeFields.contains(DEPARTMENT_FIELD);
        boolean searchForItemByType  = availabeFields.contains(ITEM_TYPE_FIELD);
        boolean searchForItemByUOM   = availabeFields.contains(UOM_FIELD);
        boolean searchForItemByStyle = availabeFields.contains(STYLE_FIELD);
        boolean searchForItemByColor = availabeFields.contains(COLOR_FIELD);
        boolean searchForItemBySize  = availabeFields.contains(SIZE_FIELD) && model.isItemSizeRequired();

        try
        {
            if (searchForItemByDepartment)
            {
                cargo.setDeptList(getDepartmentList(utility, storeID));
                model.setDeptList(cargo.getDeptList());
            }

            if (searchForItemByType)
            {
                cargo.setTypeList(getItemTypeList(utility));
                model.setTypeList(cargo.getTypeList());
            }

            if (searchForItemByUOM)
            {
                cargo.setUomList(getUOMList(utility, storeID));
                model.setUomList(cargo.getUomList());
            }

            if (searchForItemByStyle)
            {
                cargo.setStyleList(getStyleList(utility));
                model.setStyleList(cargo.getStyleList());
            }

            if (searchForItemByColor)
            {
                cargo.setColorList(getColorList(utility));
                model.setColorList(cargo.getColorList());
            }

            if (searchForItemBySize)
            {
                cargo.setSizeList(getSizeList(utility));
                model.setSizeList(cargo.getSizeList());
            }

            cargo.setSearchItemByDepartment(searchForItemByDepartment);
            cargo.setSearchItemByType(searchForItemByType);
            cargo.setSearchItemByUOM(searchForItemByUOM);
            cargo.setSearchItemByStyle(searchForItemByStyle);
            cargo.setSearchItemByColor(searchForItemByColor);
            cargo.setSearchItemBySize(searchForItemBySize);

            model.setSearchItemByDepartment(searchForItemByDepartment);
            model.setSearchItemByType(searchForItemByType);
            model.setSearchItemByUOM(searchForItemByUOM);
            model.setSearchItemByStyle(searchForItemByStyle);
            model.setSearchItemByColor(searchForItemByColor);
            model.setSearchItemBySize(searchForItemBySize);

            // if the system not found the info, reset user info previously
            // entered
            SearchCriteriaIfc inquiry = cargo.getInquiry();
            if (inquiry != null)
            {
                String itemID = inquiry.getItemID();
                if (itemID != null)
                {
                    model.setItemNumber(replaceStar(itemID));
                }
                
                inquiry.setDiscountable(-1);
                inquiry.setTaxable(-1);
                inquiry.setClearance(-1);

                model.setItemDesc(inquiry.getDescription());
                model.setManufacturer(inquiry.getManufacturer());

                if (searchForItemByDepartment)
                    model.setSelectedDeptByDescription(cargo.getDeptName(LocaleMap
                            .getLocale(LocaleConstantsIfc.USER_INTERFACE)));
                if (searchForItemByType && cargo.getItemType()!=null )
                    model.setSelectedItemTypeByID(cargo.getItemType().getItemTypeID());
                if (searchForItemByUOM && cargo.getUom()!=null )
                    model.setSelectedItemUOMByID(cargo.getUom().getUnitID());
                if (searchForItemByStyle && cargo.getStyle()!=null )
                    model.setSelectedItemStyleByID(cargo.getStyle().getIdentifier());
                if (searchForItemByColor && cargo.getColor()!=null )
                    model.setSelectedItemColorByID(cargo.getColor().getIdentifier());
                if (searchForItemBySize && cargo.getSize()!=null)
                    model.setSelectedItemSizeByID(cargo.getSize().getSizeCode());
            }
            else
            {
                // set user's input from price inquiry screen
                model.setItemNumber(ui.getInput());
                model.setSelectedDept(DEFAULT_SELECTED_VALUE);
                model.setSelectedType(DEFAULT_SELECTED_VALUE);
                model.setSelectedUOM(DEFAULT_SELECTED_VALUE);
                model.setSelectedStyle(DEFAULT_SELECTED_VALUE);
                model.setSelectedColor(DEFAULT_SELECTED_VALUE);
                model.setSelectedSize(DEFAULT_SELECTED_VALUE);
            }
        }
        catch (DataException de)
        {
            String msg[] = new String[1];
            msg[0] = utility.getErrorCodeString(de.getErrorCode());
            showErrorDialog(bus, "DatabaseError", msg, CommonLetterIfc.UNDO);
        }

    }

    /**
     * Find what button click from ADV_SEARCH screen.
     * 
     * @param bus Service Bus
     */
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();

        String letterName = null;
        
        if (letter instanceof ButtonPressedLetter) // Is ButtonPressedLetter
        {
            // Get the String representation of the letter name
            // from the LetterIfc object
            letterName = letter.getName();
            if (letterName != null && letterName.equals(CommonLetterIfc.UNDO))
            {   
            	ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
                cargo.setInquiry(null);

            }
        }
    }

    /**
     * Get department list.
     * 
     * @param utility UtilityManagerIfc
     * @param storeID
     * @return DepartmentIfc[]
     */
    protected DepartmentIfc[] getDepartmentList(UtilityManagerIfc utility, String storeID)
    {
        // retrieve department code list from reason codes.
        CodeListIfc deptMap = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_DEPARTMENT);

        // retrieve department entries
        CodeEntryIfc[] deptEntries = deptMap.getEntries();
        DepartmentIfc[] deptList = new DepartmentIfc[deptEntries.length];
        DepartmentIfc dept = null;
        for (int i = 0; i < deptEntries.length; i++)
        {
            dept = DomainGateway.getFactory().getDepartmentInstance();
            dept.setLocalizedDescriptions(deptEntries[i].getLocalizedText());
            dept.setDepartmentID(deptEntries[i].getCode());
            deptList[i] = dept;
        }
        return deptList;
    }

    /**
     * Get types list.
     * 
     * @param utility UtilityManagerIfc
     * @return DepartmentIfc[]
     * @throws DataException
     */
    protected ItemTypeIfc[] getItemTypeList(UtilityManagerIfc utility) throws DataException
    {
        AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
        ItemTypeIfc[] itemTypeList = null;
        itemTypeList = transaction.getItemTypeList(utility.getRequestLocales());

        // externalize item type names
        for (ItemTypeIfc itemType : itemTypeList)
        {
            String propName = ITEM_TYPE_LABEL + "." + itemType.getItemTypeName();
            String itemTypeName = utility.retrieveText("ItemInquirySpec",
                    BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, propName, itemType.getItemTypeName());
            itemType.setItemTypeName(itemTypeName);
        }

        return itemTypeList;
    }

    /**
     * Get UOM list.
     * 
     * @param utility UtilityManagerIfc
     * @param storeID
     * @return UnitOfMeasureIfc[]
     */
    protected UnitOfMeasureIfc[] getUOMList(UtilityManagerIfc utility, String storeID)
    {
        // retrieve UOM code list from reason codes.

        CodeListIfc uomMap = null;

        uomMap = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);

        // retrieve UOM entries
        CodeEntryIfc[] uomEntries = uomMap.getEntries();
        UnitOfMeasureIfc[] uomList = new UnitOfMeasureIfc[uomEntries.length];

        UnitOfMeasureIfc uom = null;
        for (int i = 0; i < uomEntries.length; i++)
        {
            uom = DomainGateway.getFactory().getUnitOfMeasureInstance();
            uom.setUnitID(uomEntries[i].getCode());
            uom.setLocalizedNames(uomEntries[i].getLocalizedText());
            uomList[i] = uom;
        }
        return uomList;
    }

    /**
     * Get styles list.
     * 
     * @param utility UtilityManagerIfc
     * @return ItemStyleIfc[]
     * @throws DataException
     */
    protected ItemStyleIfc[] getStyleList(UtilityManagerIfc utility) throws DataException
    {
        ItemStyleIfc[] styleList = null;
        AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
        styleList = transaction.getStyleList(utility.getRequestLocales());

        return styleList;
    }

    /**
     * Get colors list.
     * 
     * @param utility UtilityManagerIfc
     * @return ItemColorIfc[]
     * @throws DataException
     */
    protected ItemColorIfc[] getColorList(UtilityManagerIfc utility) throws DataException
    {
        ItemColorIfc[] colorList = null;
        AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
        colorList = transaction.getColorList(utility.getRequestLocales());

        return colorList;
    }

    /**
     * Get sizes list.
     * 
     * @param utility UtilityManagerIfc
     * @return DepartmentIfc[]
     * @throws DataException
     */
    protected ItemSizeIfc[] getSizeList(UtilityManagerIfc utility) throws DataException
    {
        ItemSizeIfc[] sizeList = null;
        AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
        sizeList = transaction.getSizeList(utility.getRequestLocales());

        return sizeList;
    }

    /**
     * Replaces '%' to '*' from storing data to cargo
     * 
     * @param string oldtext
     */
    public String replaceStar(String oldtext)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String uiWildcard = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                UI_WILDCARD_TAG, UI_WILDCARD);
        String dbWildcard = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                DB_WILDCARD_TAG, DB_WILDCARD);

        return Util.replaceStar(oldtext, dbWildcard.charAt(0), uiWildcard.charAt(0));
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
