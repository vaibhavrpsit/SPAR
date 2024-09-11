/*===========================================================================
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *   cgreene     10/17/14 - remove unneeded calls to clear discount rules
 *   yiqzhao     10/09/14 - mapping unit of measure between store and xc.
 */
package oracle.retail.stores.pos.services.order.common;

import org.apache.commons.lang3.StringUtils;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.PLURequestor;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.manager.item.ItemManagerIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

public class RefreshPickupLineItemSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Refreshes each line item with current information from the item
     * repository.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // Initialize method variables
        OrderCargo cargo = (OrderCargo)bus.getCargo();
        RetailTransactionIfc transaction = cargo.getOrderTransaction();

        // If this transaction can have line items
        if (transaction instanceof OrderTransactionIfc)
        {
            // Get the item container proxy and clone the line items.
            OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
            ItemContainerProxyIfc proxy = orderTransaction.getItemContainerProxy();
            AbstractTransactionLineItemIfc[] lineItems = proxy.getLineItems();

            boolean foundInStorePriceDuringPickup = false;
            // Iterate through the list of line items
            for (AbstractTransactionLineItemIfc lineItem : lineItems)
            {
                // If the current line item is a sale or return
                if (lineItem instanceof OrderLineItemIfc && ((OrderLineItemIfc)lineItem).isInStorePriceDuringPickup())
                {
                    foundInStorePriceDuringPickup = true;
                    OrderLineItemIfc oli = (OrderLineItemIfc)lineItem;

                    // Retrieve the item and update line item
                    if (Util.isEmpty(oli.getPLUItem().getStoreID()))
                    {
                        oli.getPLUItem().setStoreID(transaction.getTransactionIdentifier().getStoreID());
                    }

                    PLUItemIfc retrievedPluItem = getItem(bus, oli);
                    if ((retrievedPluItem != null) && !retrievedPluItem.getItemClassification().isPriceEntryRequired())
                    {
                        clearLineItemForReprice(oli);
                        mergePluWithLineItem(retrievedPluItem, oli, proxy);
                    }
                }
            }

            if (foundInStorePriceDuringPickup)
            {
                // Recalculate pricing
                ((SaleReturnTransactionIfc)transaction).calculateBestDeal();

                // Force recalcuation of the transaction totals.
                orderTransaction.updateTransactionTotals();
            }
        }

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
    }

    /**
     * Gets the PLU Item from one of the item data repositories
     * 
     * @param bus
     * @param pluItem
     * @return the retrieved PLUItemIfc object; null if not found or error
     */
    protected PLUItemIfc getItem(BusIfc bus, OrderLineItemIfc oli)
    {
        PLUItemIfc pluItem = oli.getPLUItem();
        // letter to be sent
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        SearchCriteriaIfc inquiry = DomainGateway.getFactory().getSearchCriteriaInstance();
        OrderCargo cargo = (OrderCargo)bus.getCargo();
        PLUItemIfc retrievedPluItem = null;

        try
        {
            ItemManagerIfc mgr = (ItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);

            // get list of items matching search criteria from database
            inquiry.setLocaleRequestor(utility.getRequestLocales());
            // set storeID
            inquiry.setStoreNumber(pluItem.getStoreID());
            // Set POS item ID
            if (!StringUtils.isBlank(pluItem.getPosItemID()))
            {
                inquiry.setPosItemID(pluItem.getPosItemID());
                inquiry.setSearchItemByPosItemID(true);
            }
            else
            {
                inquiry.setItemID(pluItem.getItemID());
                inquiry.setSearchItemByItemID(true);
            }

            inquiry.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
            inquiry.setRetrieveFromStore(true);
            inquiry.setPLURequestor(new PLURequestor());
            if (pluItem.getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_STORE_COUPON)
            {
                inquiry.setLookupStoreCoupon(true);
            }
            boolean retrieveExtendedDataOnLocalPLULookup = Gateway.getBooleanProperty(
                    Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedDataOnLocalPLULookup", false);
            inquiry.setRetrieveExtendedDataOnLocalPLULookup(retrieveExtendedDataOnLocalPLULookup);
            int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(
                    Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
            inquiry.setMaxRecommendedItemsListSize(maxRecommendedItemsListSize);

            retrievedPluItem = mgr.getPluItem(inquiry);
        }
        catch (DataException de)
        {
            try
            {
                // look for item information from web store.
                ItemManagerIfc mgr = (ItemManagerIfc)bus.getManager(ItemManagerIfc.TYPE);
                inquiry.setRetrieveFromStore(false);
                retrievedPluItem = mgr.getPluItem(inquiry);
            }
            catch (DataException dez)
            {
                logger.info("Error reading Item: " + pluItem.getItemID() + ".", de);
            }
        }

        return retrievedPluItem;
    }

    /**
     * Clear the line item to get ready for in store reprice
     * 
     * @param oli the order line item
     */
    protected void clearLineItemForReprice(OrderLineItemIfc oli)
    {
        // set this flag to false to force price and tax calculation in store.
        oli.setFromTransaction(false);

        // clear any advanced pricing rule
        oli.removeAdvancedPricingDiscount();
        
        // clear all item price override, promotions, and discounts since price
        // will be recalculated
        ItemPriceIfc itemPrice = oli.getItemPrice();
        itemPrice.setPriceOverrideAuthorization(null);
        itemPrice.getItemPriceOverrideReason().setCode(CodeConstantsIfc.CODE_UNDEFINED);
        itemPrice.setPromotionLineItems(null);
        itemPrice.clearItemDiscounts();

        // clear all tax override/exempt flags since this item tax is to be
        // recalculated in store.
        ItemTaxIfc itemTax = oli.getItemTax();
        itemTax.setTaxMode(TaxConstantsIfc.TAX_MODE_STANDARD);
        itemTax.setOverrideRate(0);
        itemTax.setOverrideAmount(DomainGateway.getBaseCurrencyInstance());
    }

    /**
     * Merge the data from the retrieved PLUItem into the existing line item.
     * 
     * @param cargo
     * @param retrievedPluItem
     * @param oli
     * @param proxy
     */
    protected void mergePluWithLineItem(PLUItemIfc retrievedPluItem, OrderLineItemIfc oli, ItemContainerProxyIfc proxy)
    {

        // If the pricing should be replaced, add the perm and temp
        // prices to the pluItem, set the current price on the Item
        // Price object and recalculate the price.
        oli.getPLUItem().setPermanentPriceChanges(retrievedPluItem.getPermanentPriceChanges());
        oli.getPLUItem().setTemporaryPriceChangesAndTemporaryPriceChangesForReturns(
                retrievedPluItem.getTemporaryPriceChanges());
        oli.getPLUItem().setClearancePriceChangesAndClearancePriceChangesForReturns(
                retrievedPluItem.getClearancePriceChanges());
        oli.getPLUItem().addAdvancedPricingRules(retrievedPluItem.getAdvancedPricingRules());
        proxy.addAdvancedPricingRules(retrievedPluItem.getAdvancedPricingRules());
        if (oli.getPLUItem().getAdvancedPricingRules() != null)
        {
            proxy.addItemAdvancedPricingRules(oli.getPLUItem());
        }

        proxy.revaluateLineItemPrice(oli, retrievedPluItem);
        oli.calculateLineItemPrice();

        // If there are localized descriptions available, set them on the
        // embedded PLU Item.
        if (retrievedPluItem.getItem().getLocalizedDescriptions().size() > 0)
        {
            oli.getPLUItem().getItem().setLocalizedDescriptions(retrievedPluItem.getItem().getLocalizedDescriptions());
        }

        // If there are localized descriptions available, set them on the
        // embedded PLU Item.
        if (retrievedPluItem.getItem().getShortLocalizedDescriptions().size() > 0)
        {
            oli.getPLUItem().getItem()
                    .setShortLocalizedDescriptions(retrievedPluItem.getItem().getShortLocalizedDescriptions());
        }

        // Add in the item messages, classification, tax rules and department.
        oli.getPLUItem().setAllItemLevelMessages(retrievedPluItem.getAllItemLevelMessages());
        oli.getPLUItem().setItemClassification(retrievedPluItem.getItemClassification());
        oli.getPLUItem().setDepartment(retrievedPluItem.getDepartment());
        oli.getPLUItem().setTaxRules(retrievedPluItem.getTaxRules());
        oli.getPLUItem().setRelatedItemContainer(retrievedPluItem.getRelatedItemContainer());

        // Add the item image
        oli.getPLUItem().setItemImage(retrievedPluItem.getItemImage());
    }
}
