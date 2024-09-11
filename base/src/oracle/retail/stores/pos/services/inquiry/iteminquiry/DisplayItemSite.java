/* ===========================================================================
 Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/DisplayItemSite.java /main/6 2014/06/11 13:22:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    asint  10/08/14 - Fix tour ItemInquriy tour flow.
 *    asinto 09/11/14 - Set the saved selectedTabIndex from the cargo into
 *                      the ItemInfoBeanModel. 
 *    abhina 09/04/14 - Minor tweaks to implement correct UNDO action 
 *                      on item filtering results set
 *    jswan  06/06/14 - Modified to support a tabbed UI for the item info bean
 *                      for the ICE project.
 *    abhina 04/30/14 - Filter item search results enhancement
 *    subrde 03/14/14 - Reseting item in the cargo at depart
 *    sgu    01/29/14 - initialize prompt and response model
 *    tkshar 01/15/14 - New site to display the Item Display screen for a
 *                      single item result after advance search
 *
 *
 * ===========================================================================*/
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.List;
import java.util.Locale;

import javax.swing.ImageIcon;

import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.item.RelatedItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.AdvancedInquiryDataTransaction;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.manager.ifc.SecurityManagerIfc;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
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
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/** This site displays the item display screen. */
public class DisplayItemSite extends PosSiteActionAdapter
{
    public static final int IMAGE_MAX_HEIGHT = 100;
    public static final int IMAGE_MAX_WIDTH = 100;
    protected static final String PSI_ENABLED_PROPERTY = "PSIEnabled";

    public static final String SEARCH_ITEM_BY_MANUFACTURER = "SearchForItemByManufacturer";
    public static final String PLANOGRAM_DISPLAY = "PlanogramDisplay";
    public static final String SIZE_INPUT_FIELD = "SizeInputField";

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Displays the ITEM_DISPLAY screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {

        // retrieve item information from cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        
        ItemSearchResult item = cargo.getItem();
        if(!cargo.getItemSearchResults().contains(cargo.getAdvancedSearchResult()))
        {
            cargo.getItemSearchResults().add(cargo.getAdvancedSearchResult());
        }

        // Initialize bean model values
        ItemInfoBeanModel model = new ItemInfoBeanModel();
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        model.setPromptAndResponseModel(responseModel);
        model.setSelectedTabIndex(cargo.getSelectedTabIndex());
        
        if(item != null)
        {
            model.setItemDescription(item.getItemShortDescription());

            model.setItemNumber(item.getItemID());
            model.setItemDept(item.getDepartmentDescription());
            model.setColorDesc(item.getColorDescription());
            model.setPrice(item.getPrice());
            model.setUnitOfMeasure(item.getUnitOfMeasureDescription());
            model.setOnClearance(item.isOnClearance());
            model.setTaxableFlag(item.isTaxable());
            model.setDiscountableFlag(item.isDiscountable());
            model.setItemType(item.getItemTypeCode());
            model.setItemStyle(item.getStyleDescription());
            model.setItemSize(item.getSizeDescription());
            model.setItemLevelMessage(item.getItemScreenMessage());
        }
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        model.setItemSizeRequired(getSizeRequired(pm));
        if (item != null)
        {
            item.setItemSizeRequired(getSizeRequired(pm));
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
        model.setUsePlanogramID(usePlanogramID);

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

        boolean retrieveExtendedData = Gateway.getBooleanProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedData", false);
        model.setShowExtendedDataUI(retrieveExtendedData);
        if (item.getExtendedItemDataContainer() != null)
        {
            model.setDetail(item.getExtendedItemDataContainer().getDetail());
            model.setRecommendedItems(item.getExtendedItemDataContainer().getRecommendedItems());
        }
        
        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ITEM_DISPLAY, model);

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
        
        model.setDeptList(cargo.getDeptList());
        try
        {
            String all = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel", "<All>", uiLocale);
            
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
            
            //size
            if (model.isItemSizeRequired())
            {
                if (model.getSizeList() == null)
                {
                    ItemSizeIfc[] sizeList = null;
                    AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                            .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
                    sizeList = transaction.getSizeList(utility.getRequestLocales());
                    
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

            //color
            if (model.getColorList() == null)
            {
                ItemColorIfc[] colorList = null;
                AdvancedInquiryDataTransaction transaction = (AdvancedInquiryDataTransaction) DataTransactionFactory
                        .create(DataTransactionKeys.ADVANCED_INQUIRY_DATA_TRANSACTION);
                colorList = transaction.getColorList(utility.getRequestLocales());
                
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
            
            //UOM list
            if (model.getUomList() == null)
            {
                CodeListIfc uomMap = null;

                uomMap = utility.getReasonCodes(cargo.getStoreStatus().getStore().getStoreID(), CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);

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
            
            if (model.getYesAndNo() == null)
            {
                String[] yesAndNo = new String[3];
                yesAndNo[0] = all;
                yesAndNo[1] = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, "Yes", "Yes", uiLocale);
                yesAndNo[2] = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, "No", "No", uiLocale);
                
                model.setYesAndNo(yesAndNo);
            }
            
        }catch (DataException de)
        {
            String msg[] = new String[1];
            msg[0] = utility.getErrorCodeString(de.getErrorCode());
            showErrorDialog(bus, "DatabaseError", msg, CommonLetterIfc.UNDO);
        }  
        
        
        
        
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
     * Gets the Size Required Paramemter
     * @param pm
     * @return true if size is requried
     */
    protected static boolean getSizeRequired(ParameterManagerIfc pm)
    {
        Boolean sizeInput = Boolean.FALSE;
        try
        {
            sizeInput = pm.getBooleanValue(SIZE_INPUT_FIELD);
        }
        catch (ParameterException e)
        {
            logger.error("Unable to get " + SIZE_INPUT_FIELD + " parameter value.", e);
        }

        return sizeInput.booleanValue();
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
     * Find what button click from ITEM_INFO screen.
     * 
     * @param bus Service Bus
     */
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemInfoBeanModel model = ((ItemInfoBeanModel)ui.getModel(POSUIManagerIfc.ITEM_DISPLAY));
        PromptAndResponseModel parModel = model.getPromptAndResponseModel();
        String input = parModel.getResponseText();
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
                cargo.setItem(null);
            }
            else
            {
                SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
                inquiry.setItemNumber(input);
                cargo.setInquiry(inquiry);
            }
        }

    }

}
