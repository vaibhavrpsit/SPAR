/* ===========================================================================
 Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ShowItemSite.java /main/58 2014/07/01 14:00:58 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzhao 09/22/14 - Add style and item type, check the value for department.
 *    asinto 09/03/14 - setting the advance search flag in the cargo to
 *                      indicate that we're doing an advance search.
 *    jswan  06/27/14 - Added extended data to the model for the item details
 *                      pop-up.
 *    abhina 04/29/14 - Filter item search results enhancement
 *    tkshar 01/15/14 - modifed to showItemSite to display the advance search
 *                      or empty editable item info screen and to take the
 *                      response text and create an inquiry object
 *    abhina 04/23/13 - Fix to set metatagsearch flag false to enable
 *                      configurable search
 *    abhina 04/19/13 - Fix to implement correct undo action on item inquiry
 *                      screen
 *    abhina 04/18/13 - Unloading iteminquiry cargo on undo action
 *    arabal 03/06/13 - Updated the Set Image process to set Scaled Image..
 *                      Given it a static size of 100..
 *    hyin   02/01/13 - re-arrange taxable and discountable drop down menu.
 *    hyin   01/25/13 - add all option to dept, taxable and discountable
 *                      fields.
 *    mkutia 01/03/13 - Remove return request button from nav button panel
 *    hyin   11/13/12 - enable adv search from result screen.
 *    hyin   11/12/12 - re-work on item search result screen to make it
 *                      editable. So a new search can be performed.
 *    yiqzha 11/08/12 - Avoid NullPointerException.
 *    yiqzha 11/07/12 - Add related item type buttons when the item contains
 *                      related items.
 *    hyin   10/05/12 - itemSearchResult: discountable, onClearance, planogram,
 *                      manufacture
 *    hyin   10/04/12 - ItemsearchResult: message and taxable work.
 *    jswan  09/24/12 - Modified to support request of Advanced Item Search
 *                      through JPA.
 *    tzgarb 03/16/12 - Added null check for register object in the cargo
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    sthall 05/30/12 - Enhanced RPM Integration - Clearance Pricing
 *    mjwall 05/02/12 - Fortify: fix redundant null checks, part 4
 *    cgreen 12/05/11 - used getStockItem since a PLUItem can't be a StockItem
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    rsnaya 04/08/11 - request ticket button fix
 *    rsnaya 03/09/11 - pos lat integtation for label batch
 *    ohorne 02/22/11 - ItemNumber can be ItemID or PosItemID
 *    npoola 12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreen 11/03/10 - rename ItemLevelMessageConstants
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    cgreen 10/12/09 - set manufacturers names if getting PLU a second time
 *    cgreen 09/03/09 - set image icon onto model
 *    cgreen 04/10/09 - fix possible npe when checking for blob by adding
 *                      hasImageBlob to ItemImageIfc
 *    cgreen 03/30/09 - removed item name column from item image table
 *    cgreen 03/19/09 - refactoring changes
 *    deghos 01/07/09 - EJ I18n defect fixes
 *    vcheng 01/07/09 - EJ defect fixes
 *    deghos 01/06/09 - EJ i18n defect fixes
 *    mchell 12/23/08 - Changes for searchForItemByManufacturer parameter
 *    vcheng 12/16/08 - ej defect fixes
 *    nkgaut 12/02/08 - Changes to include ILRM on the item info screen
 *    mchell 12/02/08 - Changes for Item search field parameter update
 *    akandr 10/30/08 - EJ changes
 *    deghos 10/29/08 - EJI18n_changes_ExtendyourStore
 *    ddbake 10/23/08 - Updates due to merge
 *    ddbake 10/22/08 - Updating to use localized item descriptions
 *    ranojh 10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    ranojh 10/17/08 - Changes for code review
 *    mipare 10/17/08 - Deptartment list changes for localized text
 *    mipare 10/16/08 - dept list changes
 *    abonda 10/14/08 - I18Ning manufacturer name
 *    ddbake 10/09/08 - Refactor of reference implementation of POS I18N
 *                      Persistence
 *    atirke 10/01/08 - modified for item images
 *    atirke 09/30/08 -
 *    atirke 09/29/08 - Changes for item images, added blob image to bean model
 *
 *
 * ===========================================================================
 $Log:
 11   360Commerce 1.10        2/26/2008 6:33:59 AM   Naveen Ganesh
 unnecessary SOPs have been removed.
 10   360Commerce 1.9         1/10/2008 4:38:09 AM   Naveen Ganesh
 Handled the item getting added to the transaction problem
 9    360Commerce 1.8         11/22/2007 10:59:07 PM Naveen Ganesh   PSI
 Code checkin
 8    360Commerce 1.7         8/16/2007 2:57:17 PM   Anda D. Cadar   CR
 28345: Display size description for stock items
 7    360Commerce 1.6         7/19/2007 10:29:35 AM  Anda D. Cadar   Call
 CurrencyServiceIfc to format the item price; removed ISO currency
 code
 6    360Commerce 1.5         5/4/2006 5:11:50 PM    Brendan W. Farrell
 Remove inventory.
 5    360Commerce 1.4         1/22/2006 11:45:11 AM  Ron W. Haight
 removed references to com.ibm.math.BigDecimal
 4    360Commerce 1.3         12/13/2005 4:42:41 PM  Barry A. Pape
 Base-lining of 7.1_LA
 3    360Commerce 1.2         3/31/2005 4:30:00 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:25:18 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse
 $
 Revision 1.14  2004/05/20 22:54:58  cdb
 @scr 4204 Removed tabs from code base again.

 Revision 1.13  2004/05/18 21:49:02  lzhao
 @scr 4292: item inquiry ejournal

 Revision 1.12  2004/05/14 17:28:58  lzhao
 @scr 4292: price inquiry journal info update.

 Revision 1.11  2004/05/03 18:30:29  lzhao
 @scr 4544, 4556: keep user entered info when back to the page.

 Revision 1.10  2004/04/30 18:43:01  lzhao
 @scr 4556: set the value user previously entered.

 Revision 1.9  2004/04/28 22:51:29  lzhao
 @scr 4081,4084: roll item info to inventory screen.

 Revision 1.8  2004/04/22 17:35:52  lzhao
 @scr 4291, 4384 show department and size info.

 Revision 1.7  2004/04/09 17:57:00  lzhao
 @scr 4294: populate item id if it is entered before.

 Revision 1.6  2004/03/16 18:30:46  cdb
 @scr 0 Removed tabs from all java source code.

 Revision 1.5  2004/02/26 22:10:11  lzhao
 @scr 3841 Inquiry Options Enhancement.
 code review follow up.

 Revision 1.4  2004/02/23 21:43:24  lzhao
 @scr 3841 Inquiry Options Enhancement.
 Add showJounal()

 Revision 1.3  2004/02/12 16:50:31  mcs
 Forcing head revision

 Revision 1.2  2004/02/11 21:51:10  rhafernik
 @scr 0 Log4J conversion and code cleanup

 Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:00:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:22:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:02   msg
 * Initial revision.
 *
 *    Rev 1.4   Feb 05 2002 16:42:32   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.3   06 Dec 2001 11:15:20   pjf
 * Updates for kits and item inquiry.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.2   Nov 07 2001 15:50:04   vxs
 * Modified LineDisplayItem() in POSDeviceActionGroup, so accommodating changes for other files as well.
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.1   Oct 12 2001 15:40:28   vxs
 * Putting line display mechanism in service code.
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.0   Sep 21 2001 11:29:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:08   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;

import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.constants.ItemLevelMessageConstants;
import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.item.RelatedItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.AdvancedInquiryDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.utility.DynamicDropdownsUtility;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This site displays the ITEM_INFO screen.
 */
public class ShowItemSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 5957045449801925726L;

    /**
     * item inquiry information for logging
     */
    protected static final String ITEM_DESCRIPTION = "Item Description: ";
    protected static final String ITEM_DEPARTMENT = "Item Department: ";
    protected static final String ITEM_PRICE = "Item Price: ";
    protected static final String ITEM_UNIT = "Item Unit of Measure: ";
    protected static final String ITEM_TAXABLE = "Item Taxable: ";
    protected static final String ITEM_DISCOUNTABLE = "Item Discountable: ";
    protected static final String PRICE_INQUIRY = "Price Inquiry ";
    protected static final String ITEM_NUMBER = "Item Number: ";
    protected static final String ITEM_SIZE = "Item Size: ";

    protected static final String YES = "YES";
    protected static final String NO = "NO";
    protected static final String TRUE = "true";
    protected static final String FALSE = "false";

    // to display manufacturer and planogram
    protected static final String ITEM_MANUFACTURER = "Manufacturer:";
    protected static final String ITEM_PLANOGRAM_ID = "Planogram ID:";

    protected static final String PSI_ENABLED_PROPERTY = "PSIEnabled";

    public static final String SEARCH_ITEM_BY_MANUFACTURER = "SearchForItemByManufacturer";
    public static final String PLANOGRAM_DISPLAY = "PlanogramDisplay";

    protected boolean latEnabled = false;
    public static final int IMAGE_MAX_HEIGHT = 100;
    public static final int IMAGE_MAX_WIDTH = 100;
    
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
     * Displays the Editable ITEM_INFO or the advanced search screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // retrieve item information from cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        
        ItemSearchResult item = cargo.getItem();

        // Initialize bean model values
        ItemInfoBeanModel model = new ItemInfoBeanModel();
       
        if(item != null)
        {
            model.setItemDescription(item.getItemShortDescription());

            model.setItemNumber(item.getItemID());
            model.setItemDept(item.getDepartmentDescription());
            model.setColorDesc(item.getColorDescription());
            model.setPrice(item.getPrice());
            model.setItemType(item.getItemTypeCode());
            model.setUnitOfMeasure(item.getUnitOfMeasureDescription());
            model.setOnClearance(item.isOnClearance());
            if (StringUtils.isBlank(item.getStyleDescription()))
            {
                model.setItemStyle(item.getStyleDescription());
            }
            model.setTaxableFlag(item.isTaxable());
            model.setDiscountableFlag(item.isDiscountable());

            if (item.isItemSizeRequired())
            {
                model.setItemSize(item.getSizeDescription());
            }

            model.setItemLevelMessage(item.getItemScreenMessage());
        }
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        boolean itemSizeRequired = DynamicDropdownsUtility.getInstance().getSizeRequired(pm);
        model.setItemSizeRequired(itemSizeRequired);
        if (item != null)
        {
            item.setItemSizeRequired(itemSizeRequired);
        }
        boolean searchForItemByManufacturer = false;
        boolean usePlanogramID = false;
        try
        {
            searchForItemByManufacturer  = pm.getBooleanValue(SEARCH_ITEM_BY_MANUFACTURER);
            usePlanogramID = pm.getBooleanValue(PLANOGRAM_DISPLAY);
        }
        catch (ParameterException pe)
        {
            logger.error("Cannot retrive parameter value", pe);
        }
        model.setSearchItemByManufacturer(searchForItemByManufacturer);
        if (item!=null)
        {
            model.setUsePlanogramID(usePlanogramID);
        }
        else
        {
            model.setUsePlanogramID(false);
        }

        if (model.isSearchItemByManufacturer() && item != null)
        {
            model.setItemManufacturer(item.getManufacture());
        }

        if (model.isUsePlanogramID() && item != null)
        {
            model.setPlanogramID(item.getPlanogramID());
        }

        showPriceInquiryJournal(bus, model, item, cargo.isMetaTagSearch());

        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();

        // enable/disable Inventory Inquiry button
        navModel.setButtonEnabled(CommonActionsIfc.INVENTORY,isIventoryInquirySupported(bus));
        
        navModel.setButtonEnabled(RelatedItemGroupIfc.AUTOMATIC, false);
        navModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, false);
        navModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, false);
        navModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, false);
        
        List<RelatedItemSearchResult> relatedItemSearchResults = null;
        if (item != null)
        {
            relatedItemSearchResults = item.getRelatedItemSearchResult();
        }
        if ( relatedItemSearchResults!=null && relatedItemSearchResults.size()>0 )
        {
	    	for (RelatedItemSearchResult relatedItemSearchResult: relatedItemSearchResults)
	    	{
	    		if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.AUTOMATIC))
	    			navModel.setButtonEnabled(RelatedItemGroupIfc.AUTOMATIC, true);
	    		else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.CROSS_SELL))
	    			navModel.setButtonEnabled(RelatedItemGroupIfc.CROSS_SELL, true);
	    		else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.UPSELL))
	    			navModel.setButtonEnabled(RelatedItemGroupIfc.UPSELL, true);   	
	    		else if (relatedItemSearchResult.getRelatedItemTypeCode().equals(RelatedItemGroupIfc.SUBSTITUTE))
	    			navModel.setButtonEnabled(RelatedItemGroupIfc.SUBSTITUTE, true);    	
	    	}
        }
    	
        model.setLocalButtonBeanModel(navModel);
        populateLists(model, cargo, bus);    
      
        String imgUrl = null;
        if (item != null)
        {
            imgUrl = item.getImageLocation();
        }
        if (!Util.isEmpty(imgUrl))
        {
            model.setImageLocation(imgUrl);
            model.setBlobImage(false);
        }

        // blob logic
        byte[] imgData = null;
        if (item != null)
        {
            imgData = item.getImageBlob();
        }
        if ((imgData != null) && (imgData.length >0))
        {
            model.setImage(new ImageIcon(Toolkit.getDefaultToolkit().createImage(imgData).getScaledInstance(IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT, Image.SCALE_FAST)));
            model.setBlobImage(true);
            model.setImageBlob(imgData);
        }

        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        cargo.setAdvanceSearch(true);
        ui.showScreen(POSUIManagerIfc.ITEM_INFO, model);

        // Show item on Line Display device
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        try
        {
            PLUItemIfc pluItem = DomainGateway.getFactory().getPLUItemInstance();
            if(item != null)
            {
                pluItem.setShortDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE),
                        item.getItemShortDescription());
                pluItem.setPrice(DomainGateway.getBaseCurrencyInstance(item.getPrice()));
            }
            pda.lineDisplayItem(pluItem);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage());
        }
    }

    private void populateLists(ItemInfoBeanModel model, ItemInquiryCargo cargo, BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        List<String> availabeFields = DynamicDropdownsUtility.getInstance().getAvailableItemSearchFields(pm, true, new ArrayList<String>());
        
        String all = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale);
        try
        {
            if (availabeFields.contains(DEPARTMENT_FIELD))
            {
                model.setDeptList(cargo.getDeptList());              
                
                //dept
                if (model.getDeptList() == null)
                {
                    CodeListIfc deptMap = utility.getReasonCodes(cargo.getStoreStatus().getStore().getStoreID(), CodeConstantsIfc.CODE_LIST_DEPARTMENT);

                    // retrieve department entries
                    CodeEntryIfc[] deptEntries = deptMap.getEntries();
                    DepartmentIfc[] deptList = new DepartmentIfc[deptEntries.length+1];
                    DepartmentIfc dept = null;
                    
                    dept = DomainGateway.getFactory().getDepartmentInstance();
                    dept.setDepartmentID("-1");
                    dept.setDescription(uiLocale, all);
                    deptList[0] = dept;
                    
                    dept = null;
                    for (int i = 1; i < deptEntries.length + 1; i++)
                    {
                        dept = DomainGateway.getFactory().getDepartmentInstance();
                        dept.setLocalizedDescriptions(deptEntries[i-1].getLocalizedText());
                        dept.setDepartmentID(deptEntries[i-1].getCode());
                        deptList[i] = dept;
                    }
                    
                    model.setDeptList(deptList);
                }
            }
            //item type
            if (availabeFields.contains(ITEM_TYPE_FIELD))
            {            
                if (model.getTypeList() == null)
                {
                    AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                            .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
                    ItemTypeIfc[] typeList = transaction.getItemTypeList(utility.getRequestLocales());
                    
                    if (typeList.length > 0)
                    {
                        ItemTypeIfc[] rtnList = new ItemTypeIfc[typeList.length + 1];
                        rtnList[0] = DomainGateway.getFactory().getItemTypeInstance();
                        rtnList[0].setItemTypeID("-1");
                        rtnList[0].setItemTypeName(all);
                        for (int i=0; i<typeList.length; i++)
                        {
                            rtnList[i+1] = DomainGateway.getFactory().getItemTypeInstance();
                            rtnList[i+1].setItemTypeID(typeList[i].getItemTypeID());
                            rtnList[i+1].setItemTypeName(typeList[i].getItemTypeName());
                        }
                        model.setTypeList(rtnList);
                    }     
                }
            }
            //style
            if (availabeFields.contains(STYLE_FIELD))
            {            
                if (model.getStyleList() == null)
                {
                    AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                            .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
                    ItemStyleIfc[] styleList = transaction.getStyleList(utility.getRequestLocales());
                    
                    if (styleList.length > 0)
                    {
                        ItemStyleIfc[] rtnList = new ItemStyleIfc[styleList.length + 1];
                        rtnList[0] = DomainGateway.getFactory().getItemStyleInstance();
                        rtnList[0].setIdentifier("-1");
                        rtnList[0].setDescription(uiLocale, all);
                        for (int i=0; i<styleList.length; i++)
                        {
                            rtnList[i+1] = DomainGateway.getFactory().getItemStyleInstance();
                            rtnList[i+1].setIdentifier(styleList[i].getIdentifier());
                            rtnList[i+1].setDescription(uiLocale, styleList[i].getDescription(uiLocale));
                        }
                        model.setStyleList(rtnList);
                    }     
                }
            }
        
            if (availabeFields.contains(SIZE_FIELD))
            {
                //size
                if (model.isItemSizeRequired())
                {
                    if (model.getSizeList() == null)
                    {
                        AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                                .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
                        ItemSizeIfc[] sizeList = transaction.getSizeList(utility.getRequestLocales());
                        
                        if (sizeList.length > 0)
                        {
                            ItemSizeIfc[] rtnList = new ItemSizeIfc[sizeList.length+1];
                            rtnList[0] = DomainGateway.getFactory().getItemSizeInstance();
                            rtnList[0].setSizeCode("-1");
                            rtnList[0].setDescription(uiLocale,all);
                            
                            for (int i=0; i<sizeList.length; i++)
                            {
                                rtnList[i+1] = sizeList[i];
                            }
                            model.setSizeList(rtnList);
                        }
                    }
                }
            }

            if (availabeFields.contains(COLOR_FIELD))
            {   //color
                if (model.getColorList() == null)
                {
                    AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                            .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
                    ItemColorIfc[] colorList = transaction.getColorList(utility.getRequestLocales());
                    
                    if (colorList.length > 0)
                    {
                        ItemColorIfc[] rtnList = new ItemColorIfc[colorList.length + 1];
                        rtnList[0] = DomainGateway.getFactory().getItemColorInstance();
                        rtnList[0].setIdentifier("-1");
                        rtnList[0].setDescription(uiLocale,all);
                        for (int i=0; i<colorList.length; i++)
                        {
                            rtnList[i+1] = colorList[i];
                        }
                        model.setColorList(rtnList);
                    }     
                }
            }
            
            if (availabeFields.contains(UOM_FIELD))
            {
                
                //UOM list
                if (model.getUomList() == null)
                {
                    CodeListIfc uomMap = utility.getReasonCodes(cargo.getStoreStatus().getStore().getStoreID(), CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);

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
                    
                    if (uomList.length > 0)
                    {
                        UnitOfMeasureIfc[] rtnList = new UnitOfMeasureIfc[uomList.length + 1];
                        rtnList[0] = DomainGateway.getFactory().getUnitOfMeasureInstance();
                        rtnList[0].setUnitID("-1");
                        rtnList[0].setDescription(uiLocale,all);
                        rtnList[0].setName(uiLocale, all);
                        for (int i=0; i<uomList.length; i++)
                        {
                            rtnList[i+1] = uomList[i];
                        }
                        model.setUomList(rtnList);
                    }
                }
            }
            
            if (model.getYesAndNo() == null)
            {
                String[] yesAndNo = new String[3];
                yesAndNo[0] = all;
                yesAndNo[1] = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, "Yes", "Yes", uiLocale);
                yesAndNo[2] = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, "No", "No", uiLocale);
                
                model.setYesAndNo(yesAndNo);
            }
        } catch (DataException de)
        {
            String msg[] = new String[1];
            msg[0] = utility.getErrorCodeString(de.getErrorCode());
            showErrorDialog(bus, "DatabaseError", msg, CommonLetterIfc.UNDO);
        }
    }

    /**
     * Journal the price inquiry results.
     *
     * @param ItemInfoBeanModel model
     * @param PLUItemIfc item
     */
    protected void showPriceInquiryJournal(BusIfc bus, ItemInfoBeanModel model, ItemSearchResult searchItem, boolean isMetaTagSearch)
    {
        JournalManagerIfc jmi = (JournalManagerIfc)Gateway.getDispatcher().getManager(JournalManagerIfc.TYPE);
        if (jmi != null)
        {


            StringBuffer entry = new StringBuffer();
            Object[] dataArgs = new Object[2];
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PRICE_INQUIRY_LABEL,
                    null));
            entry.append(Util.EOL);

            dataArgs[0] = model.getItemNumber();
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_NUMBER_LABEL,
                    dataArgs));
            entry.append(Util.EOL);

            dataArgs[0] = model.getItemDescription();
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.ITEM_DESCRIPTION_LABEL, dataArgs));
            entry.append(Util.EOL);

            dataArgs = new Object[] { model.getItemManufacturer() == null ? "" : model.getItemManufacturer() };
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MANUFACTURER_LABEL,
                    dataArgs));
            entry.append(Util.EOL);

            if (searchItem != null && searchItem.isItemSizeRequired())
            {
                dataArgs[0] = model.getItemSize();
                entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_SIZE_LABEL,
                        dataArgs));
                entry.append(Util.EOL);
            }

            dataArgs[0] = model.getItemDept();
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.ITEM_DEPARTMENT_LABEL, dataArgs));
            entry.append(Util.EOL);

            Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
            String formattedPrice = CurrencyServiceLocator.getCurrencyService()
                    .formatCurrency(model.getPrice(), defaultLocale);
            dataArgs[0] = formattedPrice;
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_PRICE_LABEL,
                    dataArgs));
            entry.append(Util.EOL);

            dataArgs[0] = model.getUnitOfMeasure();
            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.ITEM_UNIT_OF_MEASURE_LABEL, dataArgs));
            entry.append(Util.EOL);

            if (model.isTaxable() == true)
            {
                dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.YES_LABEL, null);

            }
            else
            {
                dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NO_LABEL, null);
            }

            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_TAXABLE_LABEL,
                    dataArgs));
            entry.append(Util.EOL);

            // change true to YES, false to NO
            if (model.isDiscountable() == true)
            {
                dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.YES_LABEL, null);
                // content.append(ITEM_TAXABLE).append(YES).append("\n");
            }
            else
            {
                dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NO_LABEL, null);
            }

            entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.ITEM_DISCOUNTABLE_LABEL, dataArgs));
            entry.append(Util.EOL);

            if (model.isUsePlanogramID())
            {
                entry.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.PLANOGRAM_ID_LABEL, null));
                if (model.getPlanogramID() != null)
                {
                    int planogram = model.getPlanogramID().length;
                    for (int i = 0; i < planogram; i++)
                    {
                        String planogramID = model.getPlanogramID()[i];
                        entry.append(planogramID).append(Util.EOL);
                        // content.append("\t");
                    }
                }
            }
            jmi.journal(entry.toString());
        }
    }

    /**
     * Calls <code>arrive</code>
     *
     * @param bus Service Bus
     */
    public void reset(BusIfc bus)
    {
        arrive(bus);
    }

    /**
     * This method checks whether the Inventory Inquiry is supported or not.
     */
    public boolean isIventoryInquirySupported(BusIfc bus)
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
            boolean isReentryMode = cargo.getRegister().getWorkstation().isTransReentryMode();

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

    /**
     * Displays error Dialog
     * 
     * @param bus
     * @since 14.0
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

    /**
     * Create and return an item bean model for use in displaying this screen.
     *
     * @return
     * @since 14.0
     */
    public static ItemInfoBeanModel buildItemInfoModel(BusIfc bus, SaleReturnLineItemIfc lineItem)
    {
        // Check if properties are already cached.
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        Locale uiLocale = LocaleMap.getBestMatch(locale);
        ItemInfoBeanModel model = new ItemInfoBeanModel();
        PLUItemIfc item = lineItem.getPLUItem();
        model.setItemDescription(item.getItem().getShortDescription(uiLocale));

        model.setItemNumber(item.getItemID());
        model.setItemDept(item.getItem().getDepartment().getDescription(uiLocale));
        model.setPrice(item.getPrice().getDecimalValue());
        model.setUnitOfMeasure(item.getUnitOfMeasure().getName(uiLocale));
        model.setOnClearance(item.isOnClearance());
        model.setTaxableFlag(item.getTaxable());
        model.setDiscountableFlag(item.isDiscountEligible());

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        boolean itemSizeRequired = DynamicDropdownsUtility.getInstance().getSizeRequired(pm);
        model.setItemSizeRequired(itemSizeRequired);
        if (item.isItemSizeRequired())
        {
            model.setItemSize(item.getItem().getDescription(uiLocale));
        }

        model.setItemLevelMessage(item.getItemLevelMessage(ItemLevelMessageConstants.SALE, 
        		ItemLevelMessageConstants.SCREEN, LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
        boolean searchForItemByManufacturer = false;
        boolean usePlanogramID = false;
        try
        {
            searchForItemByManufacturer  = pm.getBooleanValue(SEARCH_ITEM_BY_MANUFACTURER);
            usePlanogramID = pm.getBooleanValue(PLANOGRAM_DISPLAY);
        }
        catch (ParameterException pe)
        {
            logger.error("Cannot retrive parameter value", pe);
        }
        model.setSearchItemByManufacturer(searchForItemByManufacturer);
        model.setUsePlanogramID(usePlanogramID);
        model.setSearchItemByManufacturer(searchForItemByManufacturer);
        model.setUsePlanogramID(usePlanogramID);

        if (model.isSearchItemByManufacturer())
        {
            model.setItemManufacturer(item.getManufacturer(uiLocale));
        }

        if (model.isUsePlanogramID())
        {
            model.setPlanogramID(item.getPlanogramID());
        }

        String imgUrl = item.getItemImage().getImageLocation();
        if (!Util.isEmpty(imgUrl))
        {
            model.setImageLocation(imgUrl);
            model.setBlobImage(false);
        }

        // blob logic
        byte[] imgData = item.getItemImage().getImageBlob();
        if ((imgData != null) && (imgData.length > 0))
        {
            model.setImage(new ImageIcon(Toolkit.getDefaultToolkit().createImage(imgData).getScaledInstance(IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT, Image.SCALE_FAST)));
            model.setBlobImage(true);
            model.setImageBlob(imgData);
        }
        
        if (item.getExtendedItemDataContainer() != null)
        {
            model.setDetail(item.getExtendedItemDataContainer().getDetail());
            model.setRecommendedItems(item.getExtendedItemDataContainer().getRecommendedItems());
        }

        return model;
    }
    
    /**
     * Find what button click from ITEM_INFO screen.
     * 
     * @param bus Service Bus
     */
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        ItemInquiryCargo cargo = (ItemInquiryCargo) bus.getCargo();
        if (cargo != null && cargo.getInquiry() != null)
        {
            cargo.getInquiry().setMetaTagSearch(false);
        }        
        String letterName = null;
        if (letter instanceof ButtonPressedLetter) // Is ButtonPressedLetter
        {
            // Get the String representation of the letter name
            // from the LetterIfc object
            letterName = letter.getName();
            if (letterName != null && letterName.equals(CommonLetterIfc.UNDO) && cargo != null)
            {
                cargo.setInquiry(null);
                cargo.setItem(null);
            }
        }
        
    }
}
