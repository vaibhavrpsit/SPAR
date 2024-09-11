/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transform/entity/Item/PriceChangeTransformer.java /main/18 2013/12/10 09:20:42 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     12/09/13 - Added JAVADOC.
 *    cgreene   11/15/13 - Corrected logic error in price cahnge transformer
 *                         that set override price as sale price. Also
 *                         corrected potential recursive loop between plutitem
 *                         and pricechange where one calls the other to
 *                         determine the price.
 *    tksharma  10/24/13 - changed transformPriceChange(..) method. Removed the
 *                         populating of TemporaryPrice's sale amount with
 *                         TemporaryItemPrice's override amount
 *    tksharma  05/07/13 - set the ClearanceID to the PriceChange object in
 *                         transformPriceChange method
 *    abondala  01/27/13 - extending JPA
 *    abondala  01/10/13 - support extending jpa
 *    tksharma  12/18/12 - return price fix
 *    vtemker   11/09/12 - Added else block to fix bug
 *    tksharma  11/05/12 - added ClearancePriceChanges transformation
 *    tksharma  10/15/12 - reverted changes for Clearance Pricing done as part
 *                         of sthallam_bug-14125259
 *    ohorne    10/09/12 - added comment
 *    tksharma  10/08/12 - Bug # 14703574 - Pick priceOveride prior to
 *                         SaleUnitAmount/Percent always
 *    tksharma  09/28/12 - modified transform(..) method to cast itemPrice to
 *                         PermanentItemPrice if it is permanent and
 *                         TemporaryItemPrice if it is not
 *    jswan     09/17/12 - Made changes due to code review.
 *    jswan     07/20/12 - Added to transform JPA Item Entitities into a
 *                         PLUItemIfc.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transform.entity.Item;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.event.EventConstantsIfc;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceChangeConstantsIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transform.entity.TransformerUtilities;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.storeservices.entities.price.ClearanceItemPrice;
import oracle.retail.stores.storeservices.entities.price.ClearancePrice;
import oracle.retail.stores.storeservices.entities.price.ItemPriceIfc;
import oracle.retail.stores.storeservices.entities.price.PermanentItemPrice;
import oracle.retail.stores.storeservices.entities.price.TemporaryItemPrice;
import oracle.retail.stores.storeservices.entities.price.TemporaryPrice;

import org.apache.log4j.Logger;

/**
 * This class transforms a list Item Price Entity objects into an array of Domain
 * PriceChangeIfc objects.
 * @since 14.0
 */
public class PriceChangeTransformer implements PriceChangeTransformerIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PriceChangeTransformer.class);

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transform.entity.Item.PriceChangeTransformerIfc#transform(java.util.List, oracle.retail.stores.domain.stock.PLUItemIfc)
     */
    @Override
    public PriceChangeIfc[] transform(List<ItemPriceIfc> itemPrices, PLUItemIfc pluItem)
    {
        List<PriceChangeIfc> priceChanges = new ArrayList<PriceChangeIfc>(itemPrices.size());

        for (ItemPriceIfc itemPrice : itemPrices)
        {
            // Get the pricing event
            int eventID = -1;
            if (!itemPrice.isPermanent())
            {
                if (!itemPrice.isClearance())
                {
                    eventID = ((TemporaryItemPrice)itemPrice).getTempItemPriceID().getEventID();
                }
                else
                {
                    eventID = ((ClearanceItemPrice)itemPrice).getItemPriceID().getEventID();
                }
            }
            else
            {
                eventID = ((PermanentItemPrice)itemPrice).getItemPriceID().getEventID();
            }

            // transform into price event
            ItemPriceMaintenanceEventIfc event = transformItemPriceMaintenanceEvent(itemPrice, eventID, pluItem.getStoreID());

            // create a price change object and transform into
            PriceChangeIfc priceChange = DomainGateway.getFactory().getPriceChangeInstance();
            transformPriceChange(priceChange, itemPrice);
            priceChange.setItem(pluItem);
            event.addItem(priceChange);
            priceChanges.add(priceChange);
        }

        return priceChanges.toArray(new PriceChangeIfc[priceChanges.size()]);
    }

    /**
     * This method updates the priceChange parameter with information from itemPrice, {@link ItemPriceIfc}, entity 
     * parameter.  The ItemPriceIfc entity can be a {@link ClearanceItemPrice}, a {@link TemporaryItemPrice} or a
     * {@link PermanentItemPrice}.
     * @param priceChange a oracle.retail.stores.domain.event.PriceChangeIfc object 
     * @param itemPrice a ItemPriceIfc entity.
     * @return the modified priceChange object.
     */
    protected PriceChangeIfc transformPriceChange(PriceChangeIfc priceChange, ItemPriceIfc itemPrice)
    {
        // use override price amount instead of sale unit amount if available
        if (itemPrice.getOverrideAmount() != null)
        {
            priceChange.setOverridePriceAmount(
                    DomainGateway.getBaseCurrencyInstance(itemPrice.getOverrideAmount()));
        }

        if (!itemPrice.getPrice().isPermanent())
        {
            if (itemPrice.isClearance())
            {
                ClearanceItemPrice clrItemPrice = (ClearanceItemPrice)itemPrice;
                ClearancePrice clrPrice = (ClearancePrice)clrItemPrice.getPrice();
                priceChange.setPromotionId(clrPrice.getClearanceID());
                priceChange.setPromotionName(clrPrice.getClearanceName());
                if (clrPrice.getCustomerGroupID() != null)
                {
                    priceChange.setPricingGroupID(clrPrice.getCustomerGroupID());
                }
            }
            else
            {
                TemporaryItemPrice tempItemPrice = (TemporaryItemPrice)itemPrice;
                TemporaryPrice tempPrice = (TemporaryPrice)itemPrice.getPrice();
                priceChange.setPromotionId(tempItemPrice.getPromotionID());
                priceChange.setPromotionComponentId(tempItemPrice.getPromotionComponentID());
                priceChange.setPromotionComponentDetailId(tempItemPrice.getPromotionComponentDetailID());
                priceChange.setPromotionName(tempPrice.getPromotionName());
                if (tempPrice.getCustomerGroupID() != null)
                {
                    priceChange.setPricingGroupID(tempPrice.getCustomerGroupID());
                }
            }
        }

        return priceChange;
    }

    /**
     * This method transforms the itemPrice entity ({@link ItemPriceIfc}) entity into an 
     * oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc domain object.  
     * The ItemPriceIfc entity can be a {@link ClearanceItemPrice}, a {@link TemporaryItemPrice} or a
     * {@link PermanentItemPrice}.
     * <p>
     * An ItemPriceMaintenanceEventIfc provides start and end (where appropriate) dates on a price.
     * @param itemPrice an ItemPriceIfc entity.
     * @param eventID the ID that identifies the event.
     * @param storeID the ID that identifies the store.
     * @return the ItemPriceMaintenanceEventIfc domain object.
     */
    public ItemPriceMaintenanceEventIfc transformItemPriceMaintenanceEvent(ItemPriceIfc itemPrice, int eventID,
            String storeID)
    {
        ItemPriceMaintenanceEventIfc event = DomainGateway.getFactory().getItemPriceMaintenanceEventInstance();
        event.setEventID(eventID);

        // store id
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);
        event.setStore(store);

        // type code
        String typeCode = itemPrice.getPrice().getTypeCode();
        event.setTypeCode(EventConstantsIfc.EVENT_TYPE_UNDEFINED);
        for (int i = 0; i < EventConstantsIfc.EVENT_TYPE_CODE.length; i++)
        {
            if (EventConstantsIfc.EVENT_TYPE_CODE[i].equals(typeCode))
            {
                event.setTypeCode(i);
                break;
            }
        }

        // effective date
        EYSDate date = TransformerUtilities.getEYSDate(
                itemPrice.getPrice().getEffectiveDate(), new EYSDate());
        event.setEffectiveDateTimestamp(date);
        
        if (!itemPrice.isPermanent())
        {
            if (!itemPrice.isClearance())
            {
                Calendar calendar = Calendar.getInstance();
                TemporaryItemPrice tempItemPrice = (TemporaryItemPrice)itemPrice;
                Date expDate = tempItemPrice.getExpirationDateTimestamp();
                calendar.setTime(expDate);
                date = TransformerUtilities.getEYSDate(new Timestamp(calendar.getTimeInMillis()), null);
                calendar.clear();
                calendar.setTime(tempItemPrice.getEffectiveDateTimestamp());
                event.setEffectiveDateTimestamp(TransformerUtilities.getEYSDate(new Timestamp(calendar.getTimeInMillis()),new EYSDate()));
            }
            else
            {
                ClearancePrice clrPrice = (ClearancePrice)itemPrice.getPrice();
                date = TransformerUtilities.getEYSDate(clrPrice.getExpirationDate(), null);
            }

            event.setExpirationDateTimestamp(date);
        }

        // sale price
        event.setSaleUnitAmount(itemPrice.getPrice().getSaleUnitAmount());
        typeCode = itemPrice.getPrice().getSaleUnitType();
        event.setApplicationCode(PriceChangeConstantsIfc.APPLICATION_CODE_UNDEFINED);
        for (int i = 0; i < PriceChangeConstantsIfc.APPLICATION_CODE.length; i++)
        {
            if (PriceChangeConstantsIfc.APPLICATION_CODE[i].equals(typeCode))
            {
                event.setApplicationCode(i);
                break;
            }
        }

        // priority
        event.setPriority(itemPrice.getPrice().getPriority());
        // last digit
        String lastDigit = itemPrice.getPrice().getLastDigit();
        if (lastDigit == null)
        {
            event.setLastPriceDigit(-1);
        }
        else
        {
            event.setLastPriceDigit(Integer.parseInt(lastDigit));
        }
        return event;
    }
}
