/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/ItemPriceCodeInfoEnteredAisle.java /main/20 2013/01/29 16:18:43 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/29/13 - Remove item size check to allow alpha numeric
 *                         string.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nkgautam  02/10/09 - fix for null department name for item not present in
 *                         db
 *    ranojha   10/31/08 - Changes for CodeList and Department Reason Codes
 *    ranojha   10/29/08 - Fixed ReturnItem
 *    ranojha   10/29/08 - Changes for Return, UOM and Department Reason Codes
 *    ddbaker   10/23/08 - Updates due to merge
 *    ranojha   10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    ddbaker   10/17/08 - Domain portion of I18N ItemIfc description updates.
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.PriceCodeConverter;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.ItemSizeCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemNotFoundPriceCodeBeanModel;
import oracle.retail.stores.pos.utility.PLUItemUtility;

import org.apache.log4j.Logger;

/**
 * This aisle is traversed after the user enters additional item information. It
 * adds the item to the item list in the cargo.
 *
 * @version $Revision: /main/20 $
 */
@SuppressWarnings("serial")
public class ItemPriceCodeInfoEnteredAisle extends LaneActionAdapter
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ItemInfoEnteredAisle.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/20 $";

    /**
     * Constant for invalid serial quantity error screen
     */
    public static final String QUANTITY_NOTICE = "QuantityNotice";

    /**
     * unit of measure tag
     */
    public static final String UNIT_OF_MEASURE_TAG = "unitOfMeasure";

    /**
     * unit of measure default text
     */
    public static final String UNIT_OF_MEASURE_TEXT = "units";

    /**
     * This aisle is traversed when the user enters the item information in the
     * ITEM_NOT_FOUND screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // Grab the bean model from the UI
        ReturnItemCargo cargo = (ReturnItemCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemNotFoundPriceCodeBeanModel model;
        model = (ItemNotFoundPriceCodeBeanModel)ui.getModel(POSUIManagerIfc.ITEM_NOT_FOUND_PRICE_CODE);

        UnknownItemIfc item = DomainGateway.getFactory().getUnknownItemInstance();
        item.setItemID(model.getItemNumber());
        // item size code being set
        if (!Util.isEmpty(model.getItemSize()))
        {
            ((ItemSizeCargoIfc)cargo).setItemSizeCode(model.getItemSize());
        }
        String desc = model.getItemDescription();
        item.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), desc);
        BigDecimal amt = model.getPrice();

        String priceCode = model.getPriceCode();
        BigDecimal price = PriceCodeConverter.getInstance().convertPriceCodeToPrice(priceCode);
        model.setPrice(price);
        amt = model.getPrice();
        item.setPrice(DomainGateway.getBaseCurrencyInstance(amt.toString()));
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        if(model.getDepartmentName() != null)
        {
            Locale lcl = LocaleMap.getLocale(LocaleMap.DEFAULT);
            item.setDepartmentID(((utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_DEPARTMENT))
                    .findListEntry(model.getDepartmentName(), true, lcl)).getCode());
        }
        item.setTaxable(model.getTaxable());
        // set tax group to 1 to match parts for demo purposes. This will be
        // replaced in Release 2.5
        // by the standard default tax group.
        item.setTaxGroupID(1);

        // set unit of measure
        setUnitOfMeasure(model, item, cargo);

        /*
         * Update the cargo with the entered information
         */
        cargo.setPLUItem(item);
        cargo.setDepartmentName(model.getDepartmentName());
        cargo.setItemSerial(model.getItemSerial());
        cargo.setUnknownItemQuantity(model.getQuantity());

        if (Util.isEmpty(model.getItemSerial()) || Util.isObjectEqual(model.getQuantity(), BigDecimalConstants.ONE_AMOUNT))
        {
            cargo.completeItemNotFound();

            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);

            // Show item on Line Display device
            try
            {
                pda.lineDisplayItem(item);
            }
            catch (DeviceException e)
            {
                logger.warn("Unable to use Line Display: " + e.getMessage() + "");
            }

            /*
             * Continue to next site
             */
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            // Using "generic dialog bean".
            DialogBeanModel dialogBeanmodel = new DialogBeanModel();
            dialogBeanmodel.setResourceID(QUANTITY_NOTICE);
            dialogBeanmodel.setType(DialogScreensIfc.ERROR);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBeanmodel);
        }
    }

    /**
     * Set unit of measure on item, as needed.
     *
     * @param model ItemNotFoundBeanModel object
     * @param item UnknownItemIfc object
     * @param cargo PLUCargoIfc object
     */
    public void setUnitOfMeasure(ItemNotFoundPriceCodeBeanModel model,
    							 UnknownItemIfc item,
    							 ReturnItemCargo cargo)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        // get string and look it up in code map
        String uomString = model.getUnitOfMeasure();

        CodeListIfc list  = utility.getReasonCodes(cargo.getStoreID(),CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        CodeEntryIfc uomCodeEntry = list.findListEntry(uomString, true, locale);
        String uomCode = uomCodeEntry.getCode();

        String unit = utility.retrieveText(POSUIManagerIfc.ITEM_NOT_FOUND_SPEC, BundleConstantsIfc.COMMON_BUNDLE_NAME,
                UNIT_OF_MEASURE_TAG, UNIT_OF_MEASURE_TEXT, locale);
        if (uomCode != null && !uomCode.equals(unit))
        {
            item.setUOMCode(uomCode);
            item.getLocalizedUOMNames().initialize(LocaleMap.getSupportedLocales(), uomString);
            //item.setUOMName(locale, uomString);
        }
    }

    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
