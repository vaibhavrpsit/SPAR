/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/EnterReturnItemInformationSite.java /main/30 2013/12/19 11:31:43 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/13/14 - Set receiptNumber when transaction cannot be
 *                         found from the database.
 *    rabhawsa  12/19/13 - Code has been added to populate the saleAssociate
 *                         section with the login id or the employee id of the
 *                         employee
 *    mkutiana  04/18/13 - displaying ordernumber if the transaction is an
 *                         OrderTransaction
 *    abhinavs  12/07/12 - Fixing HP Fortify redundant null check issues
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    rgour     10/16/12 - CBR fix if item is not available in current store
 *    rabhawsa  03/08/12 - RM i18n changes populated Item Condition codes
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    jswan     08/17/11 - Modified to prevent the return of Gift Cards as
 *                         items and part of a transaction. Also cleaned up
 *                         references to gift cards objects in the return
 *                         tours.
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jkoppolu  08/30/10 - Fixed gift card item's price issue,POS-Return gift
 *                         card item by non-retrieving return.
 *    abhayg    08/26/10 - Serial Number needs to be displayed on the Return
 *                         Item Info screen For Serialized Item
 *    sgu       08/05/10 - take out commented out line
 *    sgu       08/03/10 - reject a partially used gift card
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/27/10 - First pass changes to return item for external order
 *                         project.
 *    jswan     06/01/10 - Modified to support transaction retrieval
 *                         performance and data requirements improvements.
 *    jswan     05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    jswan     12/14/09 - Modifications for 'Min return price for X days'
 *                         feature.
 *    abondala  03/05/09 - updated related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         4/3/2008 11:57:20 PM   Manikandan Chellapan
 *         CR#31161 Showing truncated giftcard number instead of encrypted
 *         account number.
 *    8    360Commerce 1.7         3/29/2007 7:18:23 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         8    .v8x      1.6.1.0     3/10/2007 4:22:40 PM   Maisa De Camargo
 *         Updated Default Settings for Reason Code.
 *    7    360Commerce 1.6         5/12/2006 5:25:32 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    6    360Commerce 1.5         5/4/2006 5:11:51 PM    Brendan W. Farrell
 *         Remove inventory.
 *    5    360Commerce 1.4         4/27/2006 7:07:08 PM   Brett J. Larsen CR
 *         17307 - inventory functionality removal - stage 2
 *    4    360Commerce 1.3         2/16/2006 7:32:22 AM   Dinesh Gautam
 *         Modified default reason codes.
 *    3    360Commerce 1.2         3/31/2005 4:28:04 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.26.2.1  2004/11/03 22:16:21  mweis
 *   @scr 7604 Allow inventory to behave correctly when database is offline.
 *
 *   Revision 1.26  2004/10/05 20:48:36  cdb
 *   @scr 7246 Removed references to location id and item id in inventory state. Modified returns
 *   so that selection is first in list when default location or state is invalid.
 *
 *   Revision 1.25  2004/09/24 17:30:01  mweis
 *   @scr Consolodate "Sale" and "Return" parameters for Inventory into the helper utility.
 *
 *   Revision 1.24  2004/09/03 16:01:27  mweis
 *   @scr 7012 Use Inventory constants in POS.
 *
 *   Revision 1.23  2004/09/01 20:34:19  mweis
 *   @scr 7012 Inventory updates for POS in the Returns arena.
 *
 *   Revision 1.22  2004/08/30 20:34:51  mweis
 *   @scr 7021 POS Inventory database work for locations and statuses.
 *
 *   Revision 1.21  2004/08/30 17:17:15  mweis
 *   @scr 7012 Refactor POS inventory database methods calls to be more descriptive: blahInventoryLocation()
 *
 *   Revision 1.20  2004/08/27 22:07:34  mweis
 *   @scr 7012 For Returns enable/disable display of Inventory info on per item basis
 *
 *   Revision 1.19  2004/08/27 20:27:29  mweis
 *   @scr 7012 First iteration on Inventory w.r.t. Returns.
 *
 *   Revision 1.18  2004/07/28 17:10:00  bvanschyndel
 *   @scr 6568 Moved inventory state DB query from the Bean to the Site for returns
 *
 *   Revision 1.17  2004/07/28 16:59:15  bvanschyndel
 *   @scr 0 Moved inventory state DB query from the Bean to the Site for returns
 *
 *   Revision 1.16  2004/07/22 23:08:57  blj
 *   @scr 6258 - changed the flow so that if UNDO is pressed, we dont lookup the item again we use the information previously entered.
 *
 *   Revision 1.15  2004/07/15 19:28:17  lzhao
 *   @scr 6284: return gift card without receipt.
 *
 *   Revision 1.14  2004/07/14 00:04:37  mweis
 *   @scr 6174 When required, an item's size matters.
 *
 *   Revision 1.13  2004/07/07 18:17:16  blj
 *   @scr 5966 - resolution
 *
 *   Revision 1.12  2004/06/29 22:03:31  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.11  2004/06/22 21:42:06  mweis
 *   @scr 5566 Return of a kit component should use extended price
 *
 *   Revision 1.10  2004/06/19 19:31:31  mweis
 *   @scr 5567 Cannot UNDO from returning a kit when on the Return Item Info screen
 *
 *   Revision 1.9  2004/06/02 21:56:00  mweis
 *   @scr 3098 Returns of a non-UOM item allows a fractional (decimal) quantity.
 *
 *   Revision 1.8  2004/05/13 19:38:41  jdeleau
 *   @scr 4862 Support timeout for all screens in the return item flow.
 *
 *   Revision 1.7  2004/03/23 18:42:20  baa
 *   @scr 3561 fix gifcard return bugs
 *
 *   Revision 1.6  2004/03/10 20:50:14  epd
 *   @scr 3561 Item size now just displays as label if item from retrieved transaction
 *
 *   Revision 1.5  2004/03/09 15:34:02  epd
 *   @scr 3561 refactor of bug fix to repair entering detailed item info
 *
 *   Revision 1.4  2004/02/24 22:08:14  baa
 *   @scr 3561 continue returns dev
 *
 *   Revision 1.3  2004/02/12 16:51:45  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   05 Feb 2004 23:16:26   baa
 * returs - multi items
 *
 *    Rev 1.1   Jan 23 2004 16:10:06   baa
 * continue returns developement
 *
 *    Rev 1.0   Aug 29 2003 16:05:48   CSchellenger
 * Initial revision.
 *
 *    Rev 1.10   Jul 15 2003 14:46:20   baa
 * allow alphanumeric values on sale associate field
 * Resolution for 3121: sales associate field not editable
 *
 *    Rev 1.9   Apr 24 2003 11:45:48   RSachdeva
 * Setting Default Store # for Manual Return
 * Resolution for POS SCR-2131: For manual return, at Return Item Info screen, no default store # and salse associate drop down list
 *
 *    Rev 1.8   Mar 05 2003 15:29:34   HDyer
 * Removed localization of reason codes as that is done in the bean. Removed use of sort index as it is not guaranteed to be the same as the list index, and therefore not reliable to set the reason code by. Since reason code is not localized, now the string is reliable to use when setting the selected reason code.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.7   Feb 21 2003 13:29:42   crain
 * Remove deprecated calls
 * Resolution for 1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.6   Feb 11 2003 12:22:18   bwf
 * Before sending G.C. Inquiry letter, checked to see if ItemReturn service.  If this is the case, send failure instead.
 * Resolution for 1861: Application hangs at the G. C. Invalid Window
 *
 *    Rev 1.5   Feb 07 2003 12:46:28   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.4   Jan 13 2003 14:57:26   RSachdeva
 * Replaced AbstractFinancialCargo.getCodeListMap()   by UtilityManagerIfc.getCodeListMap()
 * Resolution for POS SCR-1907: Remove deprecated calls to AbstractFinancialCargo.getCodeListMap()
 *
 *    Rev 1.3   Aug 13 2002 15:29:50   jriggins
 * Removed hardcoded strings for setting the model description and restocking fee labels.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   13 Jun 2002 23:27:42   vxs
 * Re-checkin with associated SCR, didn't work last time
 * Resolution for POS SCR-1637: Gift Card Return - selecting a gift card does not return it.
 *
 *    Rev 1.1   10 Jun 2002 12:25:42   vxs
 * Added another constraint to the if statement if ("".equals(giftCard.getCardNumber())
 *
 *    Rev 1.0   Apr 29 2002 15:06:38   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 11:48:02   cir
 * Check for validation failed flag
 * Resolution for POS SCR-110: Return Item Info data cleared after Qty Notice for entering Qty > 1 and serial number
 *
 *    Rev 1.0   Mar 18 2002 11:45:08   msg
 * Initial revision.
 *
 *    Rev 1.7   10 Mar 2002 11:48:18   pjf
 * Maintain kit inventory at header level.
 * Resolution for POS SCR-1444: Selling then returning a kit does not upadate the inventory count
 * Resolution for POS SCR-1503: When all kit items are returned and attempt to retrieve trans no error displays
 *
 *    Rev 1.6   22 Feb 2002 18:01:50   cir
 * Set the gift card in item
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 *
 *    Rev 1.5   20 Feb 2002 14:19:14   cir
 * Send an Invalid letter for non returnable gift card
 * Resolution for POS SCR-671: Gift card - multiple item return with expended gift card error
 *
 *    Rev 1.4   Feb 05 2002 16:43:14   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.3   20 Nov 2001 09:17:08   pjf
 * Changes to support manual return of kit components when kit header item number entered.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.2   Nov 07 2001 15:50:08   vxs
 * Modified LineDisplayItem() in POSDeviceActionGroup, so accommodating changes for other files as well.
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.1   Oct 12 2001 15:40:30   vxs
 * Putting line display mechanism in service code.
 * Resolution for POS SCR-208: Line Display
 *
 *    Rev 1.0   Sep 21 2001 11:24:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import java.util.Locale;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.ReturnItemInfoBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;


/**
 * Shows the screen to enter the Return item information.
 */
public class EnterReturnItemInformationSite extends PosSiteActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = 5142363230817006605L;
    /**
        no restock fee applicable bundle tag
    **/
    public static final String RESTOCK_FEE_NO_TAG = "RestockFeeNo";
    /**
        no restock fee applicable default text
    **/
    public static final String RESTOCK_FEE_NO_TEXT = "N";
    /**
        restock fee applicable bundle tag
    **/
    public static final String RESTOCK_FEE_YES_TAG = "RestockFeeYes";
    /**
        restock fee applicable default text
    **/
    public static final String RESTOCK_FEE_YES_TEXT = "Y";
    /**
     * Show the UI screen to enter the return information
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        // Get the cargo
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc) bus.getCargo();

        // get the ui reference
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Utility manager
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        // Create the model for the Return Item Info bean
        ReturnItemInfoBeanModel model = new ReturnItemInfoBeanModel();
        // if there were input errors on the screen, get the old bean model
        if (cargo.getValidationFailed())
        {
            model = (ReturnItemInfoBeanModel) ui.getModel(POSUIManagerIfc.RETURN_ITEM_INFO);
        }

        model.setTimerModel(new DefaultTimerModel(bus, true));

        // a list of all the reasons
        CodeListIfc reasons = utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES);
        cargo.setLocalizedReasonCodes(reasons);
        
        //a list of all the item conditions
        CodeListIfc itemConditions = utility.getReasonCodes(cargo.getStoreID(), CodeConstantsIfc.CODE_LIST_RETURN_ITEM_CONDITION_CODES);
        cargo.setLocalizedItemConditionCodes(itemConditions);
        
        // set PLU Item item info in model
        SaleReturnLineItemIfc srli = cargo.getSaleLineItem();
        PLUItemIfc item = null;

        LetterIfc letter = null;

        // Check to see if the cashier pressed Undo.  SCR 5567.
        if (bus.getCurrentLetter().getName().equalsIgnoreCase(CommonLetterIfc.UNDO))
        {
            letter = bus.getCurrentLetter();
        }
        
        if (letter == null && srli != null)
        {
 
        	item = srli.getPLUItem();
        	// set the model
            model.setItemNumber(item.getItemID());
            String description = item.getDescription(locale);
            if (Util.isEmpty(description))
            {
                description = srli.getReceiptDescription();
            }
            model.setItemDescription(description);
            if (cargo.getOriginalTransaction() != null)
            {
                model.setPrice(srli.getExtendedDiscountedSellingPrice());
                model.setItemSize(srli.getItemSizeCode());
                model.setItemSizeEnabled(false);
            }
            else
            {
                if (srli.isKitComponent())
                {
                    // Use the ext. price (normal minus discounts)
                    model.setPrice(srli.getExtendedDiscountedSellingPrice());
                }
                else
                {
                    int pricingGroupID = -1;
                    if (cargo instanceof ReturnItemCargo)
                    {
                        ReturnItemCargo returnItemCargo = (ReturnItemCargo)cargo;
                        if (returnItemCargo.getTransaction() != null &&
                            returnItemCargo.getTransaction().getCustomer() != null &&
                            returnItemCargo.getTransaction().getCustomer().getPricingGroupID() != null)
                        {
                            pricingGroupID = returnItemCargo.getTransaction().getCustomer().getPricingGroupID();
                        }
                    }
                    model.setPrice(item.getReturnPrice(pricingGroupID));
                }
                model.setItemSize(cargo.getItemSizeCode());
            }

            model.setUnitOfMeasure(ReturnUtilities.getUOMName(item, locale, utility, cargo.getStoreID()));
            model.setUOM(srli.isUnitOfMeasureItem());

            //  if the Transaction is available use these values for the model
            model.setEnableOnlyQuantityReason(false);
            if (cargo.getOriginalTransaction() != null)
            {
                // set the return fields
                String receiptNumber = "";
                if(cargo.getOriginalTransaction() instanceof OrderTransactionIfc)
                {
                    receiptNumber = cargo.getOriginalTransaction().getOrderID();
                }
                else
                {
                    receiptNumber = cargo.getOriginalTransaction().getTransactionID();
                }
                model.setReceiptNumber(receiptNumber);
                if (cargo.getOriginalTransaction().getWorkstation() != null
                    && cargo.getOriginalTransaction().getWorkstation().getStore() != null)
                {
                    model.setStoreNumber(cargo.getOriginalTransaction().getWorkstation().getStore().getStoreID());
                    model.setEnableOnlyQuantityReason(true);
                }
            }
            else if (((ReturnItemCargo)cargo).getOriginalTransactionId()!=null && 
                      !Util.isBlank(((ReturnItemCargo)cargo).getOriginalTransactionId().getTransactionIDString()))
            {
                // get receiptNumber when the customer has the receipt but the transaction cannot be found in the database
                String receiptNumber = ((ReturnItemCargo)cargo).getOriginalTransactionId().getTransactionIDString();

                model.setReceiptNumber(receiptNumber);
            }
            if (!model.getEnableOnlyQuantityReason())
            {
                //This implies this is Manual Return for which we need to set the
                //the store number default value as Current Store Number
                //This gets displayed in Editable Text Field for Manual Return
                String storeID = Gateway.getProperty("application", "StoreID", "");
                model.setStoreNumber(storeID);
            }

            // This model boolean is not set individually; it derives its value
            // when this class calls setEnableOnlyQuantityReason() method on
            // the model (ReturnItemInfoBeanModel) object.
            if (model.isReceiptNumberEnabled())
            {
                setTenderTypesOnModel(bus, model);
            }
            
            // The return is from an external order, get the quantity from the
            // corresponding external order item.
            if (cargo.isExternalOrder())
            {
                ExternalOrderItemIfc eoi = cargo.getPLUItem().getReturnExternalOrderItem();
                model.setQuantity(eoi.getQuantity());
                model.setQuantityEnabled(false);
            }
            else
            {
                model.setQuantity(cargo.getItemQuantity());
            }

            if (cargo.getSaleLineItemSalesAssociate() != null)
            {
                if (cargo.getSaleLineItemSalesAssociate().getLoginID() != null
                        && cargo.getSaleLineItemSalesAssociate().getLoginID().length() > 0)

                {
                    model.setSalesAssociate(cargo.getSaleLineItemSalesAssociate().getLoginID());

                }
                else
                {
                    model.setSalesAssociate(cargo.getSaleLineItemSalesAssociate().getEmployeeID());
                }
            }
            // set the reason codes in the model
            model.setSelectedReasonCode(reasons.getDefaultOrEmptyString(locale));
            model.inject(reasons, reasons.getDefaultOrEmptyString(locale), LocaleMap.getBestMatch(locale));
            model.setSelected(true);
            
            //set the item condition codes in the model
            model.getItemConditionModel().setSelectedItemConditionCode(itemConditions.getDefaultOrEmptyString(locale));
            model.getItemConditionModel().inject(itemConditions, itemConditions.getDefaultOrEmptyString(locale), LocaleMap.getBestMatch(locale));
            model.getItemConditionModel().setSelected(true);
            
            //set the serialized item detail
            String itemSerial = cargo.getItemSerial();
            boolean isItemSerialised = srli.getPLUItem().isSerializedItem();
            if(itemSerial!=null&&isItemSerialised)
            {
                model.setSerialNumberRequired(true);
                model.setSerialNumber(itemSerial);
            }

            // set the restocking fee to N
            String restockFeeNoStr =
                utility.retrieveText(
                    "ReturnItemInfoSpec",
                    BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                    RESTOCK_FEE_NO_TAG,
                    RESTOCK_FEE_NO_TEXT,
                    locale);
            model.setRestockingFee(restockFeeNoStr);

            // set the serial number required flag
            ItemClassificationIfc itemClassification = item.getItemClassification();
            if (itemClassification != null)
            {
                // check if the restocking fee flag is true
                if (itemClassification.getRestockingFeeFlag())
                {
                    String restockFeeYesStr =
                        utility.retrieveText(
                            "ReturnItemInfoSpec",
                            BundleConstantsIfc.RETURNS_BUNDLE_NAME,
                            RESTOCK_FEE_YES_TAG,
                            RESTOCK_FEE_YES_TEXT,
                            locale);
                    model.setRestockingFee(restockFeeYesStr);
                }
            }
            
            if( srli!=null && srli.getPLUItem().isAvailableInCurrentStore()==false)
            {
            	model.setPrice(srli.getSellingPrice());
            	model.setPriceEnabled(true);
            	String responseText= utility.retrieveText("ReturnItemInfoSpec", BundleConstantsIfc.RETURN_BUNDLE_NAME, "ItemNotInCurrentStore", "Item Information Not Found for :{0} .\nEnter Item Information and press Next.", locale) ;
            	PromptAndResponseModel responseModel = new PromptAndResponseModel();
            	String[] args = new String[1];               
                args[0]=srli.getItemDescription(locale);                             
                responseModel.setArguments(args);
                responseModel.setPromptText(responseText);
                model.setPromptAndResponseModel(responseModel);            
            }

            // Set optional integration information.  Example: Inventory.
            setIntegrationInformation(model, item, bus);


            if (item.isKitHeader())
            {
                model.setQuantity(BigDecimalConstants.ONE_AMOUNT);
                cargo.setReturnItemInfo(model);
                bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
            }
            else
            {
                // show the screen
                ui.showScreen(POSUIManagerIfc.RETURN_ITEM_INFO, model);

                //Show item on Line Display device
                POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
                try
                {
                    pda.lineDisplayItem(item);
                }
                catch (DeviceException e)
                {
                    logger.warn("Unable to use Line Display:.", e);
                }
             }
          
        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /*
     * This method gets the list of tender descriptors from the Parameter
     * Manager, gets the display value for each descriptor from the Return
     * Text bundle, and sets the array of display text values on the model.
     */
    private void setTenderTypesOnModel(BusIfc bus, ReturnItemInfoBeanModel model)
    {
        try
        {
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            String[] tendersDescriptors = pm.getStringValues(ReturnUtilities.SALE_TENDERS_FOR_REFUND);
            String tenderText = null;
            for (int i = 0; i < tendersDescriptors.length; i++)
            {
                tenderText = utility.retrieveText(BundleConstantsIfc.ORIGINAL_TENDER_TAG, 
                        BundleConstantsIfc.RETURN_BUNDLE_NAME, 
                        tendersDescriptors[i], tendersDescriptors[i], locale) ;
                tendersDescriptors[i] = tenderText;
            }
            model.setTenderDescriptors(tendersDescriptors);
        }
        catch (ParameterException pe)
        {
            logger.error("EnterReturnItemInformationSite could not be retrieved from the ParameterManager.", pe);
        }
    }

    protected void setIntegrationInformation(ReturnItemInfoBeanModel model, PLUItemIfc item, BusIfc bus)
    {
        // For any of the optional modules, set any necessary information.
    }
}
