/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/ShowFilterItemSite.java /main/2 2014/07/16 08:58:27 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    09/22/14 - CAE item filtering final cleanup.
*                        Removing unused codes.
* abhinavs    08/13/14 - CAE item filtering cleanup round II.
* abhinavs    07/14/14 - CAE item search results filtering cleanup
* abhinavs    07/11/14 - CAE item filtering xchannel enabled fix
* abhinavs    05/09/14 - Filtering Item search results enhancement
* abhinavs    05/09/14 - Initial Version
* abhinavs    05/09/14 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.utility.DynamicDropdownsUtility;

/**
 * This site displays the FILTER_CRITERIA screen.
 * @since 14.1
 */
@SuppressWarnings("serial")
public class ShowFilterItemSite extends PosSiteActionAdapter
{
    public static final String SIZE_INPUT_FIELD = "SizeInputField";

    
    /**
     * Displays the Editable filter criteria search screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // retrieve item information from cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        // Initialize bean model values
        ItemInfoBeanModel model = new ItemInfoBeanModel();
        model.setNonFilteringItemSearch(!cargo.isFilterSearchResults());
        
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        model.setItemSizeRequired(getSizeRequired(pm));
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();

        model.setLocalButtonBeanModel(navModel);
        
        /* Call populateFilteringList(model, cargo, bus) to dynamically
         * populating filtering criteria
        */
        populateFilteringList(model, cargo, bus);
        
        // Display the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.FILTER_ITEMS, model);
       
    }
    
    /**
     * Dynamically populating search criteria dropdowns based on the searched results 
     * @param model
     * @param cargo
     * @param bus
     * @since 14.1
     */
    protected void populateFilteringList(ItemInfoBeanModel model, ItemInquiryCargo cargo, BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String all = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME, "AllLabel",
                "<All>", uiLocale);
        DynamicDropdownsUtility dropDowns = DynamicDropdownsUtility.getInstance();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        ArrayList<String> filterFields = new ArrayList<String>();

        List<String> availableFields = dropDowns.getAvailableItemSearchFields(pm, true,
                filterFields);

        boolean searchForItemByType = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_TYPE);
        boolean searchForItemByUOM = availableFields.contains(CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        boolean searchForItemByStyle = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_STYLE);
        boolean searchForItemByColor = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_TYPE);
        boolean searchForItemBySize = availableFields.contains(CodeConstantsIfc.CODE_LIST_ITEM_SIZE)
                && model.isItemSizeRequired();

        try
        {
            // dept list is dynamically populated. This criteria don't depend on the parameter.
            //In other words always showed if size is not null.
            if (model.getDeptList() == null)
            {
                model.setDeptList(dropDowns.deptList(cargo, bus, utility, uiLocale, all));
            }
            
            // size list is dynamically populated
            if (model.isItemSizeRequired() && searchForItemBySize)
            {
                if (model.getSizeList() == null)
                {
                    model.setSizeList(dropDowns.sizeList(cargo, bus, utility, uiLocale, all));
                }
            }

            // color list is dynamically populated
            if (model.getColorList() == null && searchForItemByColor)
            {
                model.setColorList(dropDowns.colorList(cargo, bus, utility, uiLocale, all));

            }

            // UOM list is dynamically populated
            if (model.getUomList() == null && searchForItemByUOM)
            {
                model.setUomList(dropDowns.uomList(cargo, bus, utility, uiLocale, all));
            }

            // Style list is dynamically populated
            if (model.getStyleList() == null && searchForItemByStyle)
            {
                model.setStyleList(dropDowns.styleList(cargo, bus, utility, uiLocale, all));
            }

            // Item type list is dynamically populated
            if (model.getTypeList() == null && searchForItemByType)
            {
                model.setTypeList(dropDowns.itemTypeList(cargo, bus, utility, uiLocale, all));
            }
        }
        catch (DataException de)
        {
            String msg[] = new String[1];
            msg[0] = utility.getErrorCodeString(de.getErrorCode());
            showErrorDialog(bus, "DatabaseError", msg, CommonLetterIfc.UNDO);
        }

        // Yes and No list is dynamically populated
        if (model.getYesAndNo() == null)
        {
            String[] yesAndNo = new String[3];
            yesAndNo[0] = all;
            yesAndNo[1] = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, "Yes", "Yes", uiLocale);
            yesAndNo[2] = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, "No", "No", uiLocale);

            model.setYesAndNo(yesAndNo);

            // Yes And No list for Clearance
            String[] yesAndNoClearance = dropDowns.yesAndNo(cargo, all, utility, uiLocale, DynamicDropdownsUtility.CLEARANCE);
            model.setYesAndNoClearance(yesAndNoClearance);
            // Yes And No list for Discount
            String[] yesAndNoDiscount = dropDowns.yesAndNo(cargo, all, utility, uiLocale, DynamicDropdownsUtility.DISCOUNT);
            model.setYesAndNoDiscount(yesAndNoDiscount);
            // Yes And No list for Tax
            String[] yesAndNoTax = dropDowns.yesAndNo(cargo, all, utility, uiLocale, DynamicDropdownsUtility.TAX);
            model.setYesAndNoTax(yesAndNoTax);
            model.setYesAndNo(yesAndNo);
        }

    }

    /**
     * Displays error Dialog
     * 
     * @param bus
     * @since 14.0
     */
    protected void showErrorDialog(BusIfc bus, String id, String[] args, String letter)
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
     * Find what button click from this screen.
     * 
     * @param bus Service Bus
     */
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
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

