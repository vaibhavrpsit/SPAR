/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/ItemInfoEnteredAisle.java /main/28 2013/01/29 16:18:43 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/29/13 - Remove item size check to allow alpha numeric
 *                         string.
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    icole     04/24/12 - Removed display of item on CPOI and LineDisplay as
 *                         this is done at the ShowSaleScreen site and resulted
 *                         in the item being shown twice.
 *    icole     03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                         have more generic code, rather than heavily Pincomm.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    blarsen   06/14/11 - Adding storeID to scrolling receipt request.
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    blarsen   09/07/10 - Setting item product group to UNKNOWN ITEM. This
 *                         flag is used when the item is returned. It ensures
 *                         that the item is recreated as an UnknownItemIfc.
 *    asinton   08/26/10 - Set permanent price on unknown item so that it will
 *                         show as the PreviousPrice in the POSLog.
 *    sgu       06/08/10 - add item # & desc to the screen prompt. fix unknow
 *                         item screen to disable price and quantity for
 *                         external item
 *    sgu       06/02/10 - refactor AddItemSite to move business logic to
 *                         TransactionUtility
 *    sgu       06/01/10 - check in merge changes
 *    sgu       06/01/10 - check in after merge
 *    sgu       06/01/10 - check in order sell item flow
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   02/01/10 - set unknown item's short desc
 *    abondala  01/03/10 - update header date
 *    ranojha   12/02/08 - Fixed POS crashes due to returned null CodeList and
 *                         Department Strings
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         3/25/2008 6:21:36 AM   Vikram Gopinath CR
 *         #30683, porting changes from v12x. Save the correct pos department
 *         id for an unknown item.
 *    5    360Commerce 1.4         1/22/2006 11:45:01 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:31 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:37 PM  Robert Pearse
 *
 *   Revision 1.12  2004/06/15 20:29:35  mkp1
 *   @scr 2775 Tax Rules On unknown item
 *
 *   Revision 1.11  2004/04/05 15:47:54  jdeleau
 *   @scr 4090 Code review comments incorporated into the codebase
 *
 *   Revision 1.10  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 *   Revision 1.9  2004/03/22 22:20:57  epd
 *   @scr 0 removed new unnecessary new on BigDecimal since BigDecimal is immutable
 *
 *   Revision 1.8  2004/03/18 19:20:21  rsachdeva
 *   @scr 3906 Item Size Format
 *
 *   Revision 1.7  2004/03/17 20:03:19  rsachdeva
 *   @scr  3906 Sale Item Size
 *
 *   Revision 1.6  2004/03/15 20:12:38  rsachdeva
 *   @scr  3906 Sale Item Size
 *
 *   Revision 1.5  2004/03/12 23:35:59  rsachdeva
 *   @scr 3906 Sale Item Size
 *
 *   Revision 1.4  2004/03/12 23:06:37  rsachdeva
 *   @scr  3906 Sale Item Size
 *
 *   Revision 1.3  2004/02/12 16:48:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:19:59  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 09 2004 10:01:38   baa
 * returns - item not found fixes
 *
 *    Rev 1.0   14 Nov 2003 00:02:48   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.math.BigDecimal;
import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadNewTaxRuleTransaction;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ItemNotFoundBeanModel;
import oracle.retail.stores.pos.utility.PLUItemUtility;

import org.apache.log4j.Logger;

/**
 * This aisle is traversed after the user enters additional item information. It
 * adds the item to the item list in the cargo.
 *
 */
public class ItemInfoEnteredAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = -4883109862716905811L;
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ItemInfoEnteredAisle.class);


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
        PLUCargoIfc cargo = (PLUCargoIfc)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ItemNotFoundBeanModel model = (ItemNotFoundBeanModel)ui.getModel(POSUIManagerIfc.ITEM_NOT_FOUND);

        UnknownItemIfc item = DomainGateway.getFactory().getUnknownItemInstance();
        item.setItemID(model.getItemNumber());
        item.setDepartmentID(model.getDepartmentID());
        //item size code being set
        if (!Util.isEmpty(model.getItemSize()) && cargo instanceof ItemSizeCargoIfc)
        {
            ((ItemSizeCargoIfc)cargo).setItemSizeCode(model.getItemSize());
        }
        String desc = model.getItemDescription();
        item.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), desc);
        item.getShortLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), desc);
        if(model.isSearchItemByManufacturer() && model.getManufacturer()!=null)
        {
             item.setManufacturer(LocaleMap.getLocale(LocaleMap.DEFAULT), model.getManufacturer());
        }
        BigDecimal amt = model.getPrice();
        CurrencyIfc currencyAmount = DomainGateway.getBaseCurrencyInstance(amt.toString());
        item.setPrice(currencyAmount);
        item.setPermanentPrice(currencyAmount);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        if (model.getDepartmentName()!=null)
        {
            item.setDepartmentID
              (((utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_DEPARTMENT)).
                findListEntry(model.getDepartmentName(), false, locale)).getCode());
        }
        item.setTaxable(model.getTaxable());
        // set tax group to 1 to  match parts for demo purposes.  This will be
        // replaced in Release 2.5 by the standard default tax group.
        item.setTaxGroupID(1);

        // set unit of measure
        setUnitOfMeasure(model, item, cargo);

        lookupTaxes(item, cargo);

        // Update the cargo with the entered information
        cargo.setPLUItem(item);
        cargo.setItemQuantity(model.getQuantity());
        cargo.setDepartmentName(model.getDepartmentName());
        cargo.setDepartmentID(model.getDepartmentID());
        cargo.setItemSerial(model.getItemSerial());
        
        // Set the unknown item's product group to "unknown".
        // If the item is later returned, this flag is used when recreating
        // the plu item type. 
        item.getItem().getItemClassification().getGroup().setGroupID(ProductGroupConstantsIfc.PRODUCT_GROUP_UNKNOWN_ITEM);

        if (Util.isEmpty(model.getItemSerial()) ||
            Util.isObjectEqual(model.getQuantity(), BigDecimalConstants.ONE_AMOUNT) )
        {
            if (cargo instanceof SaleCargoIfc && ((SaleCargoIfc)cargo).getTransaction() == null)
            {
                ((SaleCargoIfc)cargo).initializeTransaction(bus);
            }
            cargo.completeItemNotFound(bus);
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
    public void setUnitOfMeasure(ItemNotFoundBeanModel model, UnknownItemIfc item, PLUCargoIfc cargo)
    {
        // begin setUnitOfMeasure()
        UtilityManagerIfc utility =
          (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        // get string and look it up in code map
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String uomString = model.getUnitOfMeasure();
        CodeListIfc list = utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_UNIT_OF_MEASURE);
        CodeEntryIfc uomCodeEntry = list.findListEntry(uomString, false, locale);
        String uomCode = uomCodeEntry.getCode();


        item.setUOMCode(uomCode);
        item.getLocalizedUOMNames().initialize(LocaleMap.getSupportedLocales(), uomString);
        item.setUOMName(locale, uomString);
    }

    /**
     *
     * @param item
     * @param cargo
     */
    public void lookupTaxes(UnknownItemIfc item, PLUCargoIfc cargo)
    {
        //Only look up tax rules if taxable
        if(item.getTaxable())
        {
            if(cargo.getGeoCode() == null)
            {
                logger.error("GeoCode is null can not look up taxes on unknown item");
            }
            else
            {
                try
                {
                    //retrieve tax rules from database
                    ReadNewTaxRuleTransaction taxRuleTransaction = (ReadNewTaxRuleTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_NEW_TAX_RULE_TRANSACTION);
                    TaxRulesVO taxRulesVO = taxRuleTransaction.getDepartmentDefaultTaxRules(cargo.getGeoCode(), item.getDepartmentID());

                    if(taxRulesVO.hasTaxRules())
                    {
                        //we can do the following line because we
                        //should only have the tax rules for one
                        //tax group
                        item.setTaxRules(taxRulesVO.getAllTaxRules());
                    }

                }
                catch(DataException de)
                {
                    logger.error("Received exception looking up tax rules for unknown item with geoCode = "
                            + cargo.getGeoCode() + "and department = " + item.getDepartmentID(), de);
                }
            }
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
