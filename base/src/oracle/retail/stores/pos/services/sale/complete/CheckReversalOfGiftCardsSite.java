/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/CheckReversalOfGiftCardsSite.java /rgbustores_13.4x_generic_branch/2 2011/07/27 13:57:19 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   07/27/11 - build break fix
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 * Checks if gift cards need to be reversed.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class CheckReversalOfGiftCardsSite extends PosSiteActionAdapter
{
    /** constant for no reversal required letter */
    public static final String NO_GIFT_CARD_REVERSAL_REQUIRED_LETTER = "NoGiftCardReversalRequired";

    /** constant for reversal required letter */
    public static final String GIFT_CARD_REVERSAL_REQUIRED_LETTER = "GiftCardReversalRequired";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        String letter = NO_GIFT_CARD_REVERSAL_REQUIRED_LETTER;
        SaleReturnLineItemIfc[] items = null;
        int reversalCount = 0;
        if ( transaction != null )
        {
            items = transaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
        }
        if ((items != null) && (items.length > 0))
        {
            for ( int i = 0; i < items.length; i++ )
            {
                SaleReturnLineItemIfc item = items[i];
                if ( item.getPLUItem() instanceof GiftCardPLUItemIfc )
                {
                    GiftCardPLUItemIfc giftCardItem = (GiftCardPLUItemIfc)item.getPLUItem();
                    GiftCardIfc giftCard = giftCardItem.getGiftCard();
                    switch (giftCard.getRequestType())
                    {
                        case GiftCardIfc.GIFT_CARD_ISSUE:
                            if (!StatusCode.Unknown.equals(giftCard.getStatus()))
                            {
                                giftCard.setRequestType(GiftCardIfc.GIFT_CARD_ISSUE_REVERSE);
                            }
                            reversalCount++;
                            break;
                        case GiftCardIfc.GIFT_CARD_RELOAD:
                            if (!StatusCode.Unknown.equals(giftCard.getStatus()))
                            {
                                giftCard.setRequestType(GiftCardIfc.GIFT_CARD_RELOAD_REVERSE);
                            }
                            reversalCount++;
                            break;
                        default:
                            break;
                    }
                }
            }
            if (reversalCount > 0)
            {
                letter = GIFT_CARD_REVERSAL_REQUIRED_LETTER;
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
