/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/Item/PluItemTransformer.java /main/17 2014/01/24 16:58:49 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/24/14 - fix null dereferences
 *    jswan     12/13/13 - Upated JAVADOC.
 *    jswan     12/09/13 - Added JAVADOC.
 *    yiqzhao   09/05/13 - Get correct kit item price when a quantity of one
 *                         kit component is greater than one.
 *    abondala  09/04/13 - initialize collections *                         
 *    jswan     04/08/13 - Fixed issue where items (coupons) do not have a
 *                         department.
 *    asinton   02/21/13 - fixed the organization of mapping transaction type
 *                         to item message lists
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    yiqzhao   11/08/12 - Set type code for related item container.
 *    yiqzhao   11/07/12 - Add setPrice() for relatedItemSummary.
 *    yiqzhao   11/07/12 - Minor change for getting localized description.
 *    yiqzhao   10/25/12 - Read related items.
 *    ohorne    10/09/12 - Fixed potential NPE w/TaxGroupID
 *    jswan     09/17/12 - Fixed issues with kit components and made changes
 *                         due to code review.
 *    jswan     08/29/12 - Added Color/Size/Style.
 *                         PLUItemIfc.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.Item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.MessageDTO;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
import oracle.retail.stores.domain.stock.StockItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.transform.entity.TransformerUtilities;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.storeservices.entities.item.AssetMessageI18N;
import oracle.retail.stores.storeservices.entities.item.Item;
import oracle.retail.stores.storeservices.entities.item.ItemColorI18N;
import oracle.retail.stores.storeservices.entities.item.ItemI18N;
import oracle.retail.stores.storeservices.entities.item.ItemManufacturerI18N;
import oracle.retail.stores.storeservices.entities.item.ItemMessage;
import oracle.retail.stores.storeservices.entities.item.ItemPlanogram;
import oracle.retail.stores.storeservices.entities.item.ItemSizeI18N;
import oracle.retail.stores.storeservices.entities.item.ItemStyleI18N;
import oracle.retail.stores.storeservices.entities.item.KitComponent;
import oracle.retail.stores.storeservices.entities.item.LocationItem;
import oracle.retail.stores.storeservices.entities.item.MerchandiseClassification;
import oracle.retail.stores.storeservices.entities.item.PLUItemAggregationIfc;
import oracle.retail.stores.storeservices.entities.item.POSIdentity;
import oracle.retail.stores.storeservices.entities.item.RelatedItem;
import oracle.retail.stores.storeservices.entities.item.SerializedItemLabelI18N;
import oracle.retail.stores.storeservices.entities.item.StockItem;
import oracle.retail.stores.storeservices.entities.item.UnitOfMeasureI18N;
import oracle.retail.stores.storeservices.entities.store.DepartmentI18N;

import org.apache.log4j.Logger;

/**
 * This class transforms a list POSIdentity Entity object into a 
 * PLUItemIfc Domain object.
 * @since 14.0
 */
public class PluItemTransformer implements PluItemTransformerIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PluItemTransformer.class);

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.Item.PluItemTransformerIfc#transform(oracle.retail.stores.storeservices.entities.item.POSIdentity, boolean, Locale)
     */
    @Override
    public PLUItemIfc transform(POSIdentity posItem, PLUItemAggregationIfc kitHeader, Locale bestMatchingDefaultLocale)
    {
        PLUItemIfc pluItem = transformPOSIdentityComponents(posItem, kitHeader);
        
        if (pluItem.getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_STOCK)
        {
            transformStockItem(posItem, pluItem);
        }
        
        transformItemLocalizedItemDescriptions(posItem, pluItem, bestMatchingDefaultLocale);
        
        transformRelatedItems(posItem, pluItem, bestMatchingDefaultLocale);

        return pluItem;
    }

    /**
     * <b>This method transforms these item entities:</b>
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.Item}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.ItemI18N}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.ItemImage}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.ItemManufacturerI18N}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.ItemPlanogram}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.KitComponent}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.LocationItem}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.MerchandiseClassification}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.POSIdentity}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.store.Department}
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.store.DepartmentI18N}
     * <p>
     * <b>Into Domain objects of the following types:</b>
     * <p>
     * <ul>
     *      oracle.retail.stores.domain.stock:
     * <ul>
     * <p>
     *         AlterationPLUItemIfc
     * <p>
     *         GiftCardPLUItemIfc
     * <p>
     *         ItemClassificationConstantsIfc
     * <p>
     *         ItemClassificationIfc
     * <p>
     *         ItemImageIfc
     * <p>
     *         KitComponentIfc
     * <p>
     *         MerchandiseClassificationIfc
     * <p>
     *         PLUItemIfc
     * <p>
     *         ProductGroupIfc
     * <p>
     *         StockItemIfc
     * <p>
     *         UnitOfMeasureIfc
     * <p>
     * </ul>        
     *     oracle.retail.stores.domain.store:
     * <ul>
     * <p>
     *         DepartmentIfc
     * </ul>
     * <p>        
     *     oracle.retail.stores.domain.utility:</b>
     * <ul>
     *         AlterationIfc
     * <p>        
     *         GiftCardIfc
     * </ul>
     * </ul>        
     * <p>
     * @param posItem a POSIdentity entity which contains Item entity object graph. 
     * @param kitHeader if posItem is a kit component, this is the kit header; otherwise null.
     * @return the PLU Item domain object; this can be a PLUItemIfc or one of its subclasses - 
     * GiftCardIfc, AlterationIfc, ItemKitIfc (header), or KitComponentIfc.
     */
    protected PLUItemIfc transformPOSIdentityComponents(POSIdentity posItem, PLUItemAggregationIfc kitHeader)
    {
        LocationItem locationItem = posItem.getLocationItem();
        Item item = locationItem.getItem();
        
        // Instantiate the correct PLUItem object
        PLUItemIfc pluItem = null;
        switch (item.getProductType()) 
        {
            case Item:
            {
                pluItem = DomainGateway.getFactory().getPLUItemInstance();
                if (item instanceof StockItem)
                {
                    pluItem.setItem(DomainGateway.getFactory().getStockItemInstance());
                }
                break;
            }

            case GiftCard:
            {
                GiftCardIfc gc = DomainGateway.getFactory().getGiftCardInstance();
                gc.setOpenAmount(posItem.isPriceEntryRequired());
                GiftCardPLUItemIfc gcPLUItem = DomainGateway.getFactory().getGiftCardPLUItemInstance();
                gcPLUItem.setGiftCard(gc);
                pluItem = gcPLUItem;
                break;
            }
            
            case Alteration:
            {
                AlterationIfc alt = DomainGateway.getFactory().getAlterationInstance();
                AlterationPLUItemIfc alterPLUItem = DomainGateway.getFactory().getAlterationPLUItemInstance();
                alterPLUItem.setAlteration(alt);
                pluItem = alterPLUItem;
                break;
            }
            
            case KitHeader:
            {
                pluItem = DomainGateway.getFactory().getItemKitInstance();
                break;
            }
            
            case KitComponent:
            {
                KitComponentIfc kitComponent = DomainGateway.getFactory().getKitComponentInstance();
                if ( kitHeader!=null )
                {
                    kitComponent.setKitComponent(true);
                    if ( kitHeader.getItem() != null )
                    {
                        kitComponent.setItemKitID(kitHeader.getItem().getItemID());
                        
                        List<KitComponent> components = kitHeader.getItem().getKitComponents();
                        for ( KitComponent component : components)
                        {
                            if ( component.getMemberID().equals(item.getItemID()) )
                            {
                                kitComponent.setQuantity(new BigDecimal(component.getAssemblyCount()));
                                break;
                            }
                        }
                    }
                }
                pluItem = kitComponent;
                break;
            }
            default:
            {
                logger.warn("Unsupported item productType: " + item.getProductType());
                throw new IllegalArgumentException("Unsupported item productType in PLUItemTransformer.");
            }
            
        }
        
        // Set values on the PLUItem from the POSIdentity Entity
        pluItem.setPosItemID(posItem.getPOSItemID());
        pluItem.setManufacturerID(TransformerUtilities.getSafeInt(posItem.getManufacturerID(), 0));
        pluItem.setManufacturerItemUPC(posItem.getItemManufacturerUPC());

        // Set values on the pluItem from the Item Entity
        pluItem.setItemID(item.getItemID());
        pluItem.setTaxable(TransformerUtilities.getSafeInt(item.getTaxExemptCode(), 1) == 1);
        if (item.getTaxGroupID() != null)
        {
            pluItem.setTaxGroupID(item.getTaxGroupID());
        }
        
        DepartmentIfc dept = DomainGateway.getFactory().getDepartmentInstance();
        if (item.getPosDepartment() != null)
        {
            dept.setDepartmentID(item.getPosDepartment().getDepartmentID());
            for (DepartmentI18N deptI18n : item.getPosDepartment().getLocalizedDescriptions())
            {
                Locale locale = LocaleUtilities.getLocaleFromString(deptI18n.getLocale());
                dept.setDescription(locale, deptI18n.getName());
            }
        }
        pluItem.setDepartment(dept);

        pluItem.setItemSizeRequired(item.isSizeRequired());
        if (item.getItemImage() != null)
        {
            ItemImageIfc itemImage = DomainGateway.getFactory().getItemImageInstance();
            itemImage.setImageLocation(item.getItemImage().getLocation());
            itemImage.setImageBlob(item.getItemImage().getBlob());
            pluItem.setItemImage(itemImage);
        }
        if (item.getLocalizedManufactures() != null)
        {
            List<ItemManufacturerI18N> localizedManufactures = 
                item.getLocalizedManufactures();
            for(ItemManufacturerI18N localizedManufacture: localizedManufactures)
            {
                Locale locale = LocaleUtilities.getLocaleFromString(
                        localizedManufacture.getItemManufacturerI18NID().getLocale());
                String manufacturer = localizedManufacture.getManufactureName();
                pluItem.setManufacturer(locale, manufacturer);
            }
        }

        // Set values on the pluItem from the locationItem
        pluItem.setStoreID(locationItem.getLocationItemID().getStoreID());
        pluItem.setCompareAtPrice(TransformerUtilities.getCurrencyFromDecimal(locationItem.getCompareAtPrice()));
        pluItem.setRestrictiveAge(TransformerUtilities.getSafeInt(locationItem.getRestrictedAge(), 0));
        if (locationItem.getPlanograms() != null && locationItem.getPlanograms().size() > 0)
        {
            List<ItemPlanogram> itemPlanograms = locationItem.getPlanograms();
            ArrayList<String> planogramIDs = new ArrayList<String>();
            for(ItemPlanogram itemPlanogram: itemPlanograms)
            {
                planogramIDs.add(itemPlanogram.getExtPlanogramID());
            }
            pluItem.setPlanogramID(planogramIDs.toArray(new String[planogramIDs.size()]));
        }

        // Set values on the ItemClassification from the POSIdentity Entity
        ItemClassificationIfc ic = pluItem.getItemClassification();
        ic.setQuantityModifiable(!posItem.isQuantityProhibited());
        ic.setReturnEligible(!posItem.isReturnProhibited());
        ic.setPriceOverridable(posItem.isPriceModifiable());
        ic.setMinimumSaleQuantity(posItem.getMinimumSaleQuantity());
        ic.setMaximumSaleQuantity(posItem.getMaximumSaleQuantity());
        ic.setMultipleCouponEligible(posItem.isAllowMultipleCoupons());
        ic.setPriceEntryRequired(posItem.isPriceEntryRequired());
        ic.setElectronicCouponAvailable(posItem.isElectronicCoupon());
        ic.setCouponRestricted(posItem.isCouponRestricted());
        ic.setSpecialOrderEligible(posItem.isSpecialOrderEligible());
        ic.setEmployeeDiscountAllowedFlag(posItem.isEmployeeDiscountEligible());

        // Set values on the ItemClassification from the Item Entity
        ic.setDiscountEligible(item.isDiscountEligible());
        ic.setDamageDiscountEligible(item.isDamageDiscountEligible());
        ic.setRegistryEligible(item.isRegistryEligible());
        ic.setAuthorizedForSale(item.isSaleEligible());
        ic.setItemKitSetCode(item.getKitCode());
        ic.setSubstituteItemAvailable(item.isSubstituteIdentified());
        ic.setMerchandiseHierarchyGroup(item.getMerchandiseHierarchyGroupID());

        switch (item.getItemType())
        {
            case StockItem:
                ic.setItemType(ItemClassificationConstantsIfc.TYPE_STOCK);
                break;
                
            case CouponItem:
                ic.setItemType(ItemClassificationConstantsIfc.TYPE_STORE_COUPON);
                break;
                
            case ServiceItem:
                ic.setItemType(ItemClassificationConstantsIfc.TYPE_SERVICE);
                break;

            default:
                // Do nothing the method item.getItemType() ensures that StockItem is the default value. 
        }

        // create product group and set ID if necessary
        ProductGroupIfc pg = DomainGateway.getFactory().getProductGroupInstance();
        if (item.getHierarchyLevelCode() != null)
        {
            pg.setGroupID(item.getHierarchyLevelCode());
        }
        ic.setGroup(pg);
        
        if (item.getMerchandiseCodes() != null)
        {
            List<MerchandiseClassification> merchandiseCodes = item.getMerchandiseCodes();
            for(MerchandiseClassification merchandiseCode: merchandiseCodes)
            {
                MerchandiseClassificationIfc classification = 
                    DomainGateway.getFactory().getMerchandiseClassificationInstance();
                classification.setIdentifier(merchandiseCode.getClassificationCode());
                ic.addMerchandiseClassification(classification);
            }
        }
        
        pluItem.setItemClassification(ic);
        
        return pluItem;
    }

    /**
     * This method transforms these item and item related entities:
     * <p>
     * {@link oracle.retail.stores.storeservices.entities.item.AssetMessageI18N}
     * {@link oracle.retail.stores.storeservices.entities.item.ItemColorI18N}
     * {@link oracle.retail.stores.storeservices.entities.item.ItemMessage}
     * {@link oracle.retail.stores.storeservices.entities.item.ItemSizeI18N}
     * {@link oracle.retail.stores.storeservices.entities.item.ItemStyleI18N}
     * {@link oracle.retail.stores.storeservices.entities.item.SerializedItemLabelI18N}
     * {@link oracle.retail.stores.storeservices.entities.item.StockItem}
     * <p>
     * Into the following Domain objects:
     * <p>
     * <ul>
     *      oracle.retail.stores.domain.stock:
     * <ul>
     * <p>
     *         ItemColorIfc
     * <p>
     *         ItemSizeIfc
     * <p>
     *         ItemStyleIfc
     * <p>
     *         MessageDTO
     * <p>
     *         StockItemIfc
     * <p>
     *         UnitOfMeasureIfc
     * <p>
     * </ul>        
     * </ul>        
     * <p>
     * This methods puts the results of its transformation into the pluItem parameter
     * @param posItem a POSIdentity entity which contains Item entity object graph. 
     * @param pluItem the PLU Item domain object
     */
    protected void transformStockItem(POSIdentity posItem, PLUItemIfc pluItem)
    {
        StockItem stockItem = (StockItem)posItem.getLocationItem().getItem();
        pluItem.setItemWeight(stockItem.getPackItemWeight());
        pluItem.getItemClassification().setSerializedItem(stockItem.isSerializedItem());
        pluItem.getItemClassification().setRestockingFeeFlag(stockItem.isRestockingFeeItem());
        pluItem.getItemClassification().setWillCallFlag(stockItem.isWillCallItem());
        pluItem.getItemClassification().setExternalSystemCreateUIN(stockItem.isExternalCreateSerializedUINAllowed());
        pluItem.getItemClassification().setSerialEntryTime(stockItem.getCaptureTime());
        Locale locale = null;

        if (stockItem.getUnitOfMeasure() != null)
        {
            UnitOfMeasureIfc pluUOM = DomainGateway.getFactory().getUnitOfMeasureInstance();
            if (!stockItem.getUnitOfMeasure().isDefaultFlag())
            {
                pluUOM.setMetric(stockItem.getUnitOfMeasure().isEnglishMetricFlag());
                pluUOM.setUnitID(stockItem.getUnitOfMeasure().getUnitOfMeasureID());
            }
            // set the desc, so that dialog can show it
            if (stockItem.getUnitOfMeasure().getLocaleDescriptions() != null)
            {
                List<UnitOfMeasureI18N> localeDescriptions = 
                    stockItem.getUnitOfMeasure().getLocaleDescriptions();
                for(UnitOfMeasureI18N localeDescription: localeDescriptions)
                {
                    locale = LocaleUtilities.getLocaleFromString(localeDescription.getLocale());
                    pluUOM.setName(locale, localeDescription.getName());
                }
            }
            pluItem.setUnitOfMeasure(pluUOM);
        }
        
        if (stockItem.getItemMessages() != null)
        {
            MessageDTO mdto = null;
            Map<String, List<MessageDTO>> messagesMap = new HashMap<String, List<MessageDTO>>(1);

            List<ItemMessage> itemMessages = stockItem.getItemMessages();
            for(ItemMessage itemMessage: itemMessages)
            {
                mdto = new MessageDTO();
                mdto.setItemMessageCodeID(itemMessage.getMessageID().toString());
                mdto.setItemMessageTransactionType(itemMessage.getMessageTransactionType().toString());
                mdto.setItemMessageType(itemMessage.getMessageType().toString());
                
                if (itemMessage.getLocalizedAssetMessages() != null)
                {
                    List<AssetMessageI18N> localizedMessages = itemMessage.getLocalizedAssetMessages();
                    for(AssetMessageI18N localizedMessage: localizedMessages)
                    {
                        mdto.setDefaultItemMessage(localizedMessage.getDescription());
                        mdto.addLocalizedItemMessage(LocaleUtilities.
                            getLocaleFromString(localizedMessage.getAssetMessageI18NID().getLocale()),
                                localizedMessage.getDescription());
                    }
                }
                if(messagesMap.get(mdto.getItemMessageTransactionType()) == null)
                {
                    messagesMap.put(mdto.getItemMessageTransactionType(), new ArrayList<MessageDTO>());
                }
                messagesMap.get(mdto.getItemMessageTransactionType()).add(mdto);
            }
            pluItem.setAllItemLevelMessages(messagesMap);
        }

        if (stockItem.getLocalizedLableNames() != null)
        {
            List<SerializedItemLabelI18N> localizedLabels = stockItem.getLocalizedLableNames();
            for (SerializedItemLabelI18N localizedLabel: localizedLabels)
            {
                locale = LocaleUtilities.getLocaleFromString(localizedLabel.getSerializedItemLabelI18NID().getLocale());
                pluItem.setDescription(locale, localizedLabel.getLabelName());
            }
        }
        
        if (stockItem.getItemColor() != null &&
            stockItem.getItemColor().getLocalizedDescriptions() != null &&
            stockItem.getItemColor().getLocalizedDescriptions().size() > 0)
        {
            ItemColorIfc itemColor = DomainGateway.getFactory().getItemColorInstance();
            itemColor.setIdentifier(stockItem.getItemColor().getColorID());
            List<ItemColorI18N> localizedDescriptions = stockItem.getItemColor().getLocalizedDescriptions();
            for(ItemColorI18N localizedDescription: localizedDescriptions)
            {
                locale = LocaleUtilities.getLocaleFromString(localizedDescription.getLocale());
                itemColor.setDescription(locale, localizedDescription.getColorDescription());
                itemColor.setName(locale, localizedDescription.getColorName());
            }
            ((StockItemIfc)pluItem.getItem()).setItemColor(itemColor);
        }

        if (stockItem.getItemSize() != null && 
            stockItem.getItemSize().getLocalizedDescriptions() != null &&
            stockItem.getItemSize().getLocalizedDescriptions().size() > 0)
        {
            ItemSizeIfc itemSize = DomainGateway.getFactory().getItemSizeInstance();
            itemSize.setIdentifier(stockItem.getItemSize().getItemSizeID());
            List<ItemSizeI18N> localizedDescriptions = stockItem.getItemSize().getLocalizedDescriptions();
            for(ItemSizeI18N localizedDescription: localizedDescriptions)
            {
                locale = LocaleUtilities.getLocaleFromString(localizedDescription.getLocale());
                itemSize.setDescription(locale, localizedDescription.getTableDescription());
                itemSize.setName(locale, localizedDescription.getTableSize());
            }
            ((StockItemIfc)pluItem.getItem()).setItemSize(itemSize);
        }

        if (stockItem.getItemStyle() != null && 
            stockItem.getItemStyle().getLocalizedDescriptions() != null &&
            stockItem.getItemStyle().getLocalizedDescriptions().size() > 0)
        {
            ItemStyleIfc itemStyle = DomainGateway.getFactory().getItemStyleInstance();
            itemStyle.setIdentifier(stockItem.getItemStyle().getStyleID());
            List<ItemStyleI18N> localizedDescriptions = stockItem.getItemStyle().getLocalizedDescriptions();
            for(ItemStyleI18N localizedDescription: localizedDescriptions)
            {
                locale = LocaleUtilities.getLocaleFromString(localizedDescription.getLocale());
                itemStyle.setDescription(locale, localizedDescription.getStyleDesc());
                itemStyle.setName(locale, localizedDescription.getStyleName());
            }
            ((StockItemIfc)pluItem.getItem()).setItemStyle(itemStyle);
        }
    }

    /**
     * This method gets the list of localized long and short descriptions from the POSIdentity 
     * entity (contained in stored in {@link ItemI18N}) and sets them on the pluItem parameter.  
     * @param posItem a POSIdentity entity which contains the Item entity object graph. 
     * @param pluItem the PLU Item domain object
     * @param bestMatchingDefaultLocale the default locale
     */
    protected void transformItemLocalizedItemDescriptions(POSIdentity posItem, PLUItemIfc pluItem,
            Locale bestMatchingDefaultLocale)
    {
        Item item = posItem.getLocationItem().getItem();
        Locale locale = null;

        if (item.getLocalizedDescriptions() != null)
        {
            List<ItemI18N> localizedDescriptions = item.getLocalizedDescriptions();
            for (ItemI18N localizedDescription: localizedDescriptions)
            {
                locale = LocaleUtilities.getLocaleFromString(localizedDescription.getItemI18NID().getLocale());
                pluItem.setDescription(locale, localizedDescription.getDescription());
                pluItem.setShortDescription(locale, localizedDescription.getShortDescription());
                if (locale.equals(bestMatchingDefaultLocale))
                {
                    pluItem.getLocalizedDescriptions().setDefaultLocale(bestMatchingDefaultLocale);
                    pluItem.getShortLocalizedDescriptions().setDefaultLocale(bestMatchingDefaultLocale);
                }
            }
        }
    }
    
    /**
     * This method gets the list of of related items from the the POSIdentity entity and
     * calls the transformRelatedItem() method for each one.
     * @param posItem a POSIdentity entity which contains the Item entity object graph. 
     * @param pluItem the PLU Item domain object
     * @param bestMatchingDefaultLocale the default locale
     */
    protected void transformRelatedItems(POSIdentity posItem, PLUItemIfc pluItem, Locale bestMatchingDefaultLocale)
    {
    	Item item = posItem.getLocationItem().getItem();
        if (item.getRelatedItems()!=null && item.getRelatedItems().size()>0)
        {
        	for (RelatedItem relatedItem: item.getRelatedItems())
        	{
        		transformRelatedItem(relatedItem, pluItem, bestMatchingDefaultLocale);
        	}
        }
    }
    
    /**
     * This method transforms a {@link RelatedItem} entity Into the following Domain objects:
     * <p>
     * <ul>
     *      oracle.retail.stores.domain.stock:
     * <ul>
     * <p>
     *         RelatedItemGroupIfc
     * <p>
     *         RelatedItemIfc
     * <p>
     *         RelatedItemSummaryIfc
     * <p>
     *         ItemImageIfc
     * <p>
     * </ul>        
     * </ul>        
     * <p>
     * @param posRelatedItem a RelatedItem entity
     * @param pluItem the item interface used in POS application
     */
    protected void transformRelatedItem(RelatedItem posRelatedItem, PLUItemIfc pluItem, Locale bestMatchingDefaultLocale)
    {

    	RelatedItemIfc relatedItem = DomainGateway.getFactory().getRelatedItemInstance();
    	relatedItem.setDeleteable(posRelatedItem.isRemoveFlag());
    	relatedItem.setReturnable(posRelatedItem.isReturnFlag());
    	
    	RelatedItemSummaryIfc relatedItemSummary = DomainGateway.getFactory().getRelatedItemSummaryInstance();
        
    	relatedItemSummary.setItemID(posRelatedItem.getRelatedItemId());
    	relatedItemSummary.setPrice(TransformerUtilities.getCurrencyFromDecimal(posRelatedItem.getPrice()));
    	
        Locale locale = null;
        List<ItemI18N> localizedDescriptions = posRelatedItem.getLocalizedItemDescriptions();
        if (localizedDescriptions != null)
        {
            for (ItemI18N localizedDescription: localizedDescriptions)
            {
                locale = LocaleUtilities.getLocaleFromString(localizedDescription.getItemI18NID().getLocale());
                relatedItemSummary.setDescription(locale, localizedDescription.getDescription());
                if (locale.equals(bestMatchingDefaultLocale))
                {
                	relatedItemSummary.getLocalizedDescriptions().setDefaultLocale(bestMatchingDefaultLocale);
                }
            }
        }
        
        relatedItemSummary.setDepartmentID(posRelatedItem.getDepartmentID());
    	if (posRelatedItem.getItemImage() != null)
    	{
    		ItemImageIfc itemImage = DomainGateway.getFactory().getItemImageInstance();
    		itemImage.setImageLocation(posRelatedItem.getItemImage().getLocation());
    		itemImage.setImageBlob(posRelatedItem.getItemImage().getBlob());
    		relatedItemSummary.setItemImage(itemImage);
    	}
    		
    	relatedItem.setRelatedItemSummary(relatedItemSummary);

    	RelatedItemGroupIfc relatedItemGroup = pluItem.getRelatedItemContainer().get(posRelatedItem.getTypeCode());
    	if ( relatedItemGroup==null )
    	{
    		relatedItemGroup = DomainGateway.getFactory().getRelatedItemGroupInstance();
    		relatedItemGroup.setTypeCode(posRelatedItem.getTypeCode());
    		pluItem.getRelatedItemContainer().put(posRelatedItem.getTypeCode(), relatedItemGroup);
    	}
    	relatedItemGroup.addRelatedItem(relatedItem);
    }
}
