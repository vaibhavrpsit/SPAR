/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/EnterItemInfoSite.java /main/23 2011/12/05 12:16:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    npoola 12/20/10 - action button texts are moved to CommonActionsIfc
 *    sgu    06/08/10 - enhance ItemNotFoundPriceCodeBean to display external
 *                      order quantity and description
 *    sgu    06/08/10 - fix item interactive screen prompts to include item #
 *                      and description
 *    sgu    06/08/10 - add item # & desc to the screen prompt. fix unknow item
 *                      screen to disable price and quantity for external item
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    ranojh 11/24/08 - Fixed for POS crash due to NPE.
 *    ohorne 11/04/08 - localization of Department-related resaon codes
 *
 * ===========================================================================

     $Log:
      7    360Commerce 1.6         5/21/2007 7:46:20 PM   Anda D. Cadar   EJ
           changes
      6    360Commerce 1.5         3/30/2007 4:00:36 AM   Michael Boyd    CR
           26172 - v8x merge to trunk

           6    .v8x      1.4.1.0     3/12/2007 6:16:25 PM   Brett J. Larsen
           CR 4530
           - default value not being used by unit of measure codes
      5    360Commerce 1.4         7/29/2006 4:06:34 AM   Brett J. Larsen CR
           4530: default reason code fix
           v7x->360Commerce merge
      4    360Commerce 1.3         1/22/2006 11:15:01 PM  Ron W. Haight
           removed references to com.ibm.math.BigDecimal
      3    360Commerce 1.2         4/1/2005 2:58:02 AM    Robert Pearse
      2    360Commerce 1.1         3/10/2005 9:51:25 PM   Robert Pearse
      1    360Commerce 1.0         2/11/2005 11:40:54 PM  Robert Pearse
     $

      5    .v7x      1.3.1.0     6/23/2006 5:10:30 AM   Dinesh Gautam   CR
           4530: Fix for reason code

     Revision 1.8.2.1  2005/01/17 15:59:24  rsachdeva
     @scr 7912 Defaulting to the previous Item Not Found in same transaction

     Revision 1.8  2004/07/29 19:20:56  rsachdeva
     @scr 6274 Item Not Found Cancel for Returns

     Revision 1.7  2004/06/23 20:03:51  mweis
     @scr 5385 Return of UnknownItem with serial and size blows up app

     Revision 1.6  2004/06/04 16:41:58  mweis
     @scr 4895 Return for UnknownItem of quantity > 1 and marked as a serial item does not retain info

     Revision 1.5  2004/03/12 22:56:36  rsachdeva
     @scr 3906 Sale Item Size

     Revision 1.4  2004/03/04 20:53:33  rsachdeva
     @scr 3906 Quantity and Unit of Measure

     Revision 1.3  2004/02/12 16:48:02  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:19:59  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   14 Nov 2003 00:02:32   baa
 * Initial revision.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.common;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.StringTokenizer;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.stock.UnknownItem;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemNotFoundBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site displays the ITEM_NOT_FOUND screen to allow the user to enter the
 * required information for the item.
 * 
 * @version $Revision: /main/23 $
 */
public class EnterItemInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -2597507112263282126L;

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/23 $";

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

    public static final int ITEM_NUMBER_MAX_LENGTH = 14;

    /**
     * Displays the ITEM_NOT_FOUND form to allow the user to enter the required
     * information for the item.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        if (isValidItemNumberLength(bus))
        {
            displayScreen(bus, getModelWithAllSettings(bus));
        }
        else
        {
            String msg[] = new String[1];

            // get arguments for dialog
            msg[0] = ((PLUCargoIfc) bus.getCargo()).getPLUItemID();

            // initialize model bean
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("ITEM_NOT_FOUND");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonActionsIfc.INVALID);
            dialogModel.setArgs(msg);

            // display dialog
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    /**
     * Returns a boolean, validates the length of item number
     * 
     * @param the string data
     * @return boolean
     */
    public boolean isValidItemNumberLength(BusIfc bus)
    {
        boolean isValid = true;
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();
        int maxPLUItermLength = ITEM_NUMBER_MAX_LENGTH;
        if (cargo instanceof SaleCargoIfc)
        {
            maxPLUItermLength = ((SaleCargoIfc) cargo).getMaxPLUItemIDLength();
        }
        else if (cargo instanceof ReturnItemCargoIfc)
        {
            maxPLUItermLength = ((ReturnItemCargoIfc) cargo).getMaxPLUItemIDLength();
        }
        String data = ((PLUCargoIfc) bus.getCargo()).getPLUItemID();

        if (data != null && data.length() > maxPLUItermLength)
        {
            isValid = false;
        }
        return isValid;
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
     * Displays the ITEM_NOT_FOUND screen.
     * 
     * @param bus Service Bus
     * @param model ItemNotFoundBeanModel
     */
    protected void displayScreen(BusIfc bus, ItemNotFoundBeanModel model)
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
        ui.showScreen(POSUIManagerIfc.ITEM_NOT_FOUND, model);
    }

    /**
     * Initializes the bean model
     * 
     * @param bus Service Bus
     * @return ItemNotFoundBeanModel item not found bean model with all settings
     *         done. Should display with default values.
     */
    public ItemNotFoundBeanModel getModelWithAllSettings(BusIfc bus)
    {
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();
        ItemNotFoundBeanModel model = getModel(cargo);
        model.setDepartmentStrings(getDepartmentStrings(bus));
        model.setDepartmentIDs(getDepartmentIDs(bus));
        model.setUnitOfMeasureStrings(getUnitOfMeasureStrings(bus));
        model.setDefaultUOM(getDefaultUnitOfMeasure(bus));
        model.setDefaultValue(model.getDefaultUOM());
        model.setUnitOfMeasure((String) null);

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
    // SCR 4895
    private static void setModelWithUnknownItemSettings(ItemNotFoundBeanModel model, PLUCargoIfc cargo)
    {
        UnknownItem uItem = (UnknownItem) cargo.getPLUItem();
        ItemIfc item = uItem.getItem();
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        String description = item.getDescription(locale);
        if (!Util.isEmpty(description))
        {
            model.setItemDescription(description);
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
     * @return ItemNotFoundBeanModel item not found bean model
     */
    public ItemNotFoundBeanModel getModel(PLUCargoIfc cargo)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        //
        // Setup the bean model
        //
        ItemNotFoundBeanModel model = new ItemNotFoundBeanModel();
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
            CurrencyIfc defaultPrice = cargo.getItemPrice();
            if (defaultPrice != null)
            {
                model.setPriceModifiable(false);
                model.setDefaultPrice(defaultPrice.getDecimalValue());
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
        // these default values will be displayed for unitofmeasure, quantity,
        // and price
        model.setUnitOfMeasure(unit);
        model.setQuantity(model.getDefaultQuantity());
        model.setPrice(model.getDefaultPrice());
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
        String[] unitOfMeasure = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();

        CodeListIfc list = utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        if (list != null)
            unitOfMeasure = list.getTextStrings(uiLocale);

        return unitOfMeasure;

    }

    /**
     * Gets the default Unit of Measure array of strings
     * 
     * @param bus Service Bus
     */
    public String getDefaultUnitOfMeasure(BusIfc bus)
    {
        String defaultUnitOfMeasure = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale uiLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();

        CodeListIfc list = utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        if (list != null)
            defaultUnitOfMeasure = list.getDefaultOrEmptyString(uiLocale);

        return defaultUnitOfMeasure;
    }

    public String makeKey(String descr)
    {
        String key = "";
        StringTokenizer st = new java.util.StringTokenizer(descr, " ");
        while (st.hasMoreElements())
        {
            key = key + st.nextToken();
        }
        return key;
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
     * Gets the Department array of ids
     * 
     * @param bus Service Bus
     */
    public String[] getDepartmentIDs(BusIfc bus)
    {
        String[] departmentIDs = null;
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        PLUCargoIfc cargo = (PLUCargoIfc) bus.getCargo();
        CodeListIfc list = utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_DEPARTMENT);
        if (list != null)
            departmentIDs = list.getKeyStrings();

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
