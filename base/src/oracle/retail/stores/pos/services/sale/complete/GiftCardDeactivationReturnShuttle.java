/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/complete/GiftCardDeactivationReturnShuttle.java /rgbustores_13.4x_generic_branch/2 2011/09/07 08:33:36 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    kelesika  12/06/10 - Multiple reversal of gift cards
 *    kelesika  12/03/10 - Multiple gift card reversals
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.complete;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

@SuppressWarnings("serial")
public class GiftCardDeactivationReturnShuttle 
extends GiftCardActivationReturnShuttle implements ShuttleIfc
{
    public void load(BusIfc bus)
    {
        super.load(bus);
    }

    public void unload(BusIfc bus)
    {
        if(responseList != null && !responseList.isEmpty())
        {
            SaleCargoIfc saleCargo = (SaleCargoIfc)bus.getCargo();
            SaleReturnTransactionIfc transaction = saleCargo.getTransaction();
            SaleReturnLineItemIfc[] giftCardLineItems = transaction.getProductGroupLineItems(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD);
            int responseIndex = 0;
            // retrieve the response from the list
            for(int i = 0; i < giftCardLineItems.length; i++)
            {
                PLUItemIfc pluItem = giftCardLineItems[i].getPLUItem();
                if(pluItem instanceof GiftCardPLUItemIfc)
                {
                    GiftCardIfc giftCard = ((GiftCardPLUItemIfc)pluItem).getGiftCard();
                    if (giftCard.getStatus().equals(StatusCode.Active))
                    {
                        AuthorizeTransferResponseIfc response = responseList.get(responseIndex);
                        updateGiftCard(giftCard, response);
                        // Reset the Request Type to RELOAD in case the user tries to add funds to this
                        // card again.  
                        giftCard.setRequestType(GiftCardIfc.GIFT_CARD_RELOAD);
                        responseIndex++;
                    }
                }
            }
        }
    }
}
