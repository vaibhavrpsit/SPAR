/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcard/GiftCardActivationReturnShuttle.java /main/3 2013/02/28 15:30:05 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    asinton   02/06/13 - transfer the redeem authorization code into the
 *                         redeem tender for persistance
 *    ohorne    08/09/11 - APF:foreign currency support
 *    asinton   05/31/11 - Refactored Gift Card Redeem and Tender for APF
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.redeem.giftcard;

import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

/**
 * Retrieves the information from the giftcard redemption.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GiftCardActivationReturnShuttle implements ShuttleIfc
{
    /**
     * List of AuthorizeTransferResponseIfc from the Authorization Service.
     */
    protected List<AuthorizeTransferResponseIfc> responseList;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        responseList = activationCargo.getResponseList();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        RedeemCargo redeemCargo = (RedeemCargo)bus.getCargo();
        GiftCardIfc giftCard = redeemCargo.getGiftCard();
        if(responseList != null && !responseList.isEmpty())
        {
            AuthorizeTransferResponseIfc response = responseList.get(0);
            CurrencyIfc amount = response.getBaseAmount();
            giftCard.setCurrentBalance(amount);
            giftCard.setApprovalCode(response.getAuthorizationCode());
            giftCard.setTraceNumber(response.getTraceNumber());
        }
    }

}
