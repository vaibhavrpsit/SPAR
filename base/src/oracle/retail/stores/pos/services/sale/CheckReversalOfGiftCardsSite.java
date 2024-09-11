/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/CheckReversalOfGiftCardsSite.java /rgbustores_13.4x_generic_branch/4 2011/07/26 16:57:39 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    kelesika  12/06/10 - Multiple reversal of gift cards
 *    kelesika  12/03/10 - Multiple gift card reversals
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

public class CheckReversalOfGiftCardsSite extends PosSiteActionAdapter
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = 3177573203789032807L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        Letter letter = new Letter("NoGiftCardReversalRequired");
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
                cargo.setReverseCount(reversalCount);
                cargo.setReverseGiftCard(true);
                letter = new Letter("ReverseGiftCards");
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
