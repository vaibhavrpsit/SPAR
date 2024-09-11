/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/EnterItemPriceCodeInfoSite.java /main/19 2011/12/05 12:16:21 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    sgu       06/08/10 - enhance ItemNotFoundPriceCodeBean to display
 *                         external order quantity and description
 *    sgu       06/08/10 - fix item interactive screen prompts to include item
 *                         # and description
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    01/04/10 - correctly retrieve departments
 *    nkgautam  02/10/09 - Fix for not getting department list for unknown item
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.stock.UnknownItem;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.CodeListManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.ItemSizeCargoIfc;
import oracle.retail.stores.pos.services.common.PLUCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ItemNotFoundPriceCodeBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site displays the ITEM_NOT_FOUND_PRICE_CODE screen to allow the user to
 * enter the required information for the item.
 * 
 * @version $Revision: /main/19 $
 */
public class EnterItemPriceCodeInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 9181083784499794011L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /**
     * unit of measure tag
     */
    public static final String UNIT_OF_MEASURE_TAG = "unitOfMeasure";

    /**
     * unit of measure default text
     */
    public static final String UNIT_OF_MEASURE_TEXT = "units";

    /**
     * Item Not Found Screen Name tag
     */
    public static final String ITEM_NOT_FOUND_SCREEN_NAME_TAG = "ItemNotFoundScreenName";

    /**
     * Item Not Found Screen Name default text
     */
    public static final String ITEM_NOT_FOUND_SCREEN_NAME_TEXT = "Item not found.";

    /**
     * Displays the ITEM_NOT_FOUND_PRICE_CODE form to allow the user to enter
     * the required information for the item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        displayScreen(bus, getModelWithAllSettings(bus));
    }

    /**
     * Initializes the bean model from the item in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void reset(BusIfc bus)
    {
        displayScreen(bus, getModelWithAllSettings(bus));
    }

    /**
     * Displays the ITEM_NOT_FOUND_PRICE_CODE screen.
     * 
     * @param bus Service Bus
     * @param model ItemNotFoundPriceCodeBeanModel
     */
    protected void displayScreen(BusIfc bus, ItemNotFoundPriceCodeBeanModel model)
    {
        // set up prompt text
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        String[] args = new String[2];
        args[0] = model.getItemNumber();
        args[1] = model.getItemDescription();
        responseModel.setArguments(args);
        model.setPromptAndResponseModel(responseModel);

        //
        // Ask the UI Manager to display the form
        //
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ITEM_NOT_FOUND_PRICE_CODE, model);
    }

    /**
     * Initializes the bean model
     * 
     * @param bus Service Bus
     * @return ItemNotFoundPriceCodeBeanModel item not found bean model with all
     *         settings done. Should display with default values.
     */
    public ItemNotFoundPriceCodeBeanModel getModelWithAllSettings(BusIfc bus)
    {
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();
        ItemNotFoundPriceCodeBeanModel model = getModel(cargo);
        model.setDepartmentStrings(getDepartmentStrings(bus));

        model.setUnitOfMeasureStrings(getUnitOfMeasureStrings(bus));

        // For UnknownItems, set additional information on the model
        // This is used to retain data when coming back from error dialog
        if (cargo.getPLUItem() instanceof UnknownItem && bus.getCurrentLetter() != null
                && bus.getCurrentLetter().getName().equalsIgnoreCase("Ok"))
        {
            setModelWithUnknownItemSettings(model, cargo);
        }

        boolean isScanned = cargo.isItemScanned();
        if (isScanned)
        {
            if (bus.getCargo() instanceof ItemSizeCargoIfc)
            {
                ItemSizeCargoIfc sizeCargo = (ItemSizeCargoIfc) bus.getCargo();
                if (!Util.isEmpty(sizeCargo.getItemSizeCode()))
                {
                    model.setItemSize(sizeCargo.getItemSizeCode());
                }
            }
        }
        if (cargo.isEnableCancelItemNotFoundFromReturns())
        {
            NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
            globalModel.setButtonEnabled(CommonActionsIfc.CANCEL, true);
            model.setGlobalButtonBeanModel(globalModel);
        }
        return model;
    }

    /**
     * Assuming the PLU item is an instance of an UnknownItem, will set the
     * model with as many settings as possible from the cargo.
     */

    private static void setModelWithUnknownItemSettings(ItemNotFoundPriceCodeBeanModel model, PLUCargoIfc cargo)
    {
        UnknownItem uItem = (UnknownItem) cargo.getPLUItem();
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        ItemIfc item = uItem.getItem();

        String desc = item.getDescription(locale);
        if (!Util.isEmpty(desc))
        {
            model.setItemDescription(desc);
        }

        if (item.getSellingPrice() != null && item.getSellingPrice().getDecimalValue() != null)
        {
            model.setPrice(item.getSellingPrice().getDecimalValue());
        }

        if (!Util.isEmpty(cargo.getDepartmentName()))
        {
            model.setDepartmentName(cargo.getDepartmentName());
        }

        if (!Util.isEmpty(uItem.getUOMName(locale)))
        {
            model.setUnitOfMeasure(uItem.getUOMName(locale));
        }

        // This seems to have no effect. And on Returns of an UnknowItem, it
        // blows up.
        // model.setQuantity(cargo.getItemQuantity());

        model.setTaxable(item.getTaxable());

        if (cargo instanceof ItemSizeCargoIfc)
        {
            ItemSizeCargoIfc isCargo = (ItemSizeCargoIfc) cargo;
            if (!Util.isEmpty(isCargo.getItemSizeCode()))
            {
                model.setItemSize(isCargo.getItemSizeCode());
            }
        }
    }

    /**
     * Initializes the bean model
     * 
     * @param cargo Service Cargo
     * @return ItemNotFoundPriceCodeBeanModel item not found bean model
     */
    public ItemNotFoundPriceCodeBeanModel getModel(PLUCargoIfc cargo)
    {

        UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        //
        // Setup the bean model
        //
        ItemNotFoundPriceCodeBeanModel model = new ItemNotFoundPriceCodeBeanModel();
        model.setItemNumber(cargo.getPLUItemID());
        model.setItemSerial(cargo.getItemSerial());
        if (cargo.isExternalOrder())
        {
            BigDecimal defaultQuantity = cargo.getItemQuantity();
            if (defaultQuantity != null)
            {
                model.setQuantityModifiable(false);
                model.setDefaultQuantity(defaultQuantity);
            }
            String defaultItemDesc = cargo.getItemDescription();
            if (defaultItemDesc != null)
            {
                model.setDefaultItemDescription(defaultItemDesc);
            }
        }
        model.setReason(getReason(cargo));
        model.setSelectedItem(model.getDefaultValue());

        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String unit = utility.retrieveText(POSUIManagerIfc.ITEM_NOT_FOUND_SPEC, BundleConstantsIfc.COMMON_BUNDLE_NAME,
                UNIT_OF_MEASURE_TAG, UNIT_OF_MEASURE_TEXT, locale);
        model.setDefaultUOM(unit);
        // these default values will be displayed for unitofmeasure and quantity
        model.setUnitOfMeasure(unit);
        model.setQuantity(model.getDefaultQuantity());
        model.setItemDescription(model.getDefaultItemDescription());
        return (model);
    }

    /**
     * Gets the Unit of Measure array of strings
     * 
     * @param bus Service Bus
     */
    public String[] getUnitOfMeasureStrings(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();
        CodeListManagerIfc codeListManager = (CodeListManagerIfc) bus.getManager(CodeListManagerIfc.TYPE);
        CodeListSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeListSearchCriteriaInstance();
        criteria.setStoreID(cargo.getStoreID());
        criteria.setListID(CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        criteria.setLocaleRequestor(utility.getRequestLocales());
        CodeListIfc list = codeListManager.getCodeList(criteria);

        return (list.getTextStrings(uiLocale));
    }

    /**
     * Gets the Department array of strings
     * 
     * @param bus Service Bus
     */
    public String[] getDepartmentStrings(BusIfc bus)
    {
        String[] departmentIDs = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();
        CodeListIfc list = utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_DEPARTMENT);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        if (list != null)
            departmentIDs = list.getTextStrings(uiLocale);

        return departmentIDs;
    }

    /**
     * Gets the ItemQuantity long value
     * 
     * @param cargo PLUCargoIfc
     */
    protected BigDecimal getItemQuantity(PLUCargoIfc cargo)
    {
        BigDecimal value = cargo.getItemQuantity();
        // if zero or negative, set to 1
        if (value.signum() <= 0)
        {
            value = BigDecimalConstants.ONE_AMOUNT;
        }
        return (value);
    }

    /**
     * Gets the Reason string
     * 
     * @param cargo PLUCargoIfc
     */
    protected String getReason(PLUCargoIfc cargo)
    {
        String reason = null;
        int code = cargo.getDataExceptionErrorCode();
        UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        if (code == DataException.NO_DATA)
        {
            reason = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC, BundleConstantsIfc.COMMON_BUNDLE_NAME,
                    ITEM_NOT_FOUND_SCREEN_NAME_TAG, ITEM_NOT_FOUND_SCREEN_NAME_TEXT);
        }
        else
        {
            reason = utility.getErrorCodeString(code);
        }

        return (reason);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }
}
