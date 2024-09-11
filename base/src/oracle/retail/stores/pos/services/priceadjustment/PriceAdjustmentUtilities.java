/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/priceadjustment/PriceAdjustmentUtilities.java /main/18 2013/12/17 15:32:48 mkutiana Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mkutia 12/16/13 - XbranchMerge lapattab_bug-17496194 from
 *                      rgbustores_13.3x_generic_branch - Changed the laneaction 
 *                      for TooManyReadTransaction Aisle to display the Price
 *                      Adj error dialog and added traversal for Ok letter
 *    abonda 09/04/13 - initialize collections
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    ranojh 11/04/08 - Changes for Tax Exempt reason codes
 *    ranojh 10/29/08 - Fixed ReturnItem
 *    acadar 10/25/08 - localization of price override reason codes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.priceadjustment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;

/**
 * Contains various constants and methods used by the priceadjustment service
 * objects. $Revision: /main/18 $
 */
public class PriceAdjustmentUtilities
{

    // Dialog IDs
    /**
     * No price adjustable items dialog
     */
    public static final String DIALOG_NO_PRICEADJ_ITEMS = "InvalidTransactionNoPriceAdjItems";

    /**
     * Time Limit Exceeded Dialog
     */
    public static final String DIALOG_TIME_EXPIRED = "InvalidTransactionPriceAdjTimeExpired";

    /**
     * Price Adjustments Offline dialog
     */
    public static final String DIALOG_PRICEADJ_OFFLINE = "PriceAdjOffline";

    // Letters
    /**
     * ItemsFound letter
     */
    public static final String LETTER_ITEMS_FOUND = "ItemsFound";

    /**
     * NoItems letter
     */
    public static final String LETTER_NO_ITEMS = "NoItems";

    /**
     * TimeLimitExpired letter
     */
    public static final String LETTER_TIME_EXPIRED = "TimeLimitExpired";
    
    /**
     * TooMany Transactions letter
     */
    public static final String LETTER_TOO_MANY = "TooMany";

    /**
     * Map of letters to dialog IDs
     */
    private static final Map<String, String> dialogMap = new HashMap<String, String>(1);

    // Populate the dialog map
    static
    {
        dialogMap.put(LETTER_NO_ITEMS, DIALOG_NO_PRICEADJ_ITEMS);
        dialogMap.put(LETTER_TIME_EXPIRED, DIALOG_TIME_EXPIRED);
        dialogMap.put(CommonLetterIfc.DB_ERROR, DIALOG_PRICEADJ_OFFLINE);
        dialogMap.put(LETTER_TOO_MANY, DIALOG_NO_PRICEADJ_ITEMS);
    }

    /**
     * Returns the dialog ID that maps to the provided key
     *
     * @param dialogKey
     *            the key which maps to the requested dialog ID.
     *
     * @return String Dialog ID which maps to the provided dialogKey or null if
     *         no such mapping exists
     */
    public static String getDialogID(String dialogKey)
    {
        return dialogMap.get(dialogKey);
    }

    /**
     * Converts the supplied SaleReturnLineItemIfc instance to a return item by
     * setting it's members appropriately to reflect that it is now a return
     * item.
     *
     * This method is used by the priceadjustment service in order to create a
     * return components for PriceAdjustmentLineItemIfc instances
     *
     * @param originalTransaction
     *            The original transaction
     * @param originalLineItem
     *            the line item to convert; it is assumed that this line item
     *            has been retrieved from the database based on a previous
     *            transaction.
     * @return reference to the converted line item (originalLineItem)
     */
    public static SaleReturnLineItemIfc createReturnLineItem(TransactionIfc originalTransaction,
            SaleReturnLineItemIfc originalLineItem)
    {
        SaleReturnLineItemIfc returnLineItem = (SaleReturnLineItemIfc)originalLineItem.clone();

        // Set the return info

        // Erase any price override history
        ItemPriceIfc itemPrice = returnLineItem.getItemPrice();
        itemPrice.getItemPriceOverrideReason().setCode(CodeConstantsIfc.CODE_UNDEFINED);

        // Negate item quantity and tax amount and copy where
        // necessary.
        BigDecimal returnItemQuantity = originalLineItem.getItemQuantityDecimal().negate();
        returnLineItem.setItemQuantity(returnItemQuantity);
        returnLineItem.getItemPrice().setItemQuantity(returnItemQuantity);
        returnLineItem.getItemTax().getTaxInformationContainer().negate();

        // Create a ReturnItemIfc instance that contains the return data
        ReturnItemIfc returnItem = DomainGateway.getFactory().getReturnItemInstance();
        returnItem.setItemQuantity(returnItemQuantity);
        returnItem.setQuantityPurchased(returnLineItem.getItemQuantityDecimal().abs());
        returnItem.setQuantityReturnable(returnLineItem.getQuantityReturnable().abs());
        returnItem.setItemTax((ItemTaxIfc)returnLineItem.getItemTax().clone());
        returnItem.setOriginalLineNumber(returnLineItem.getLineNumber());
        returnItem.setOriginalTransactionID(originalTransaction.getTransactionIdentifier());
        returnItem.setOriginalTransactionBusinessDate(originalTransaction.getBusinessDay());
        returnItem.setPLUItem(returnLineItem.getPLUItem());
        returnItem.setPrice(returnLineItem.getSellingPrice());
        returnItem.setSalesAssociate(returnLineItem.getSalesAssociate());
        // Price adjusted items require a receipt
        returnItem.setHaveReceipt(true);
        returnItem.setFromRetrievedTransaction(true);
        // Since no restocking actually occurs, the restocking fee
        // should always be zero
        CurrencyIfc zeroCurrency = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);
        returnItem.setRestockingFee(zeroCurrency);
        returnItem.setTaxRate(returnLineItem.getItemTax().getDefaultRate());

        // Set the price adjustment reason code, if it exists
        CodeEntryIfc priceAdjReasonCode = PriceAdjustmentUtilities.getPriceAdjustmentReasonCode();
        if (priceAdjReasonCode != null)
        {
            LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
            localizedCode.setCode(priceAdjReasonCode.getCode());
            localizedCode.setText(priceAdjReasonCode.getLocalizedText());
            returnItem.setReason(localizedCode);
        }

        // Add the returnItem object to the return line item
        returnLineItem.setReturnItem(returnItem);

        // Convert the transaction discounts
        ReturnUtilities.setTransactionDiscounts(returnLineItem, returnLineItem.getItemQuantityDecimal());

        return returnLineItem;
    }

    /**
     * Return the reason code associated with price adjustements, if a available
     *
     * @return The reason code associated with price adjustements, if a
     *         available, or null.
     */
    public static CodeEntryIfc getPriceAdjustmentReasonCode()
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String storeID = Gateway.getProperty("application", "StoreID", "");
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        CodeListIfc returnReasonCodes = utility.getReasonCodes(storeID, CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES);
        
        CodeEntryIfc priceAdjustmentReadonCode = returnReasonCodes.findListEntry(
                CodeConstantsIfc.CODE_ENTRY_PRICE_ADJUSTMENT, false, locale);

        return (priceAdjustmentReadonCode);
    }

    /**
     * Compares the prices of the original line item with the current line item and returns true only if
     * the current prices is a better deal
     *
     * @param originalLineItem The original line item
     * @param currentLineItem The current line item
     * @return true only if the current price is a better deal than the original price
     */
    public static boolean isBetterDeal(SaleReturnLineItemIfc originalLineItem, SaleReturnLineItemIfc currentLineItem)
    {
        boolean isBetterDeal = false;

        // Get the extended discounted prices for the original and current line items
        CurrencyIfc originalPrice = originalLineItem.getExtendedDiscountedSellingPrice();
        CurrencyIfc currentPrice = currentLineItem.getExtendedDiscountedSellingPrice();

        // Do the comparison and then return the result
        if (currentPrice.compareTo(originalPrice) < 0)
        {
            isBetterDeal = true;
        }

        return isBetterDeal;
    }

}
