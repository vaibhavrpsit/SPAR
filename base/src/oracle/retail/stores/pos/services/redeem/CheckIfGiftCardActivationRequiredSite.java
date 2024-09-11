/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/CheckIfGiftCardActivationRequiredSite.java /rgbustores_13.4x_generic_branch/2 2011/09/07 08:33:37 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   04/22/10 - Modified redeem tour to fix timeout issue.
 *    asinton   04/22/10 - Adding site to replace SendToActivateAisle
 *                         functionality.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.transaction.RedeemTransactionADO;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * This site checks to see if there are gift cards requiring activation,
 * or reactivation when timeout or user "undo" from the tender station.
 */
public class CheckIfGiftCardActivationRequiredSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 802466442522836433L;

    /** constant for the Activation letter */
    public static final String ACTIVATION = "Activation";

    /** constant for the Reactivate letter */
    public static final String REACTIVATE = "Reactivate";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        String letterName = CommonLetterIfc.CONTINUE;
        if(cargo.isInactiveTimeout())
        {
            letterName = CommonLetterIfc.TIMEOUT;
        }
        TenderADOIfc tender = ((RedeemTransactionADO)cargo.getCurrentTransactionADO()).getRedeemTender();        
        if ( tender instanceof TenderGiftCardADO)
        {
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setEncipheredCardData(cargo.getGiftCard().getEncipheredCardData());
            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM_VOID);
            giftCard.setReqestedAmount(tender.getAmount());
            cargo.setGiftCard(giftCard);
            letterName = CheckIfGiftCardActivationRequiredSite.ACTIVATION;
            // Want all other sites to be able to realize they have a timed out transaction
            if(cargo.isInactiveTimeout())
            {
                // Reactivate the giftcard after giftcard redeem timeout
                letterName = CheckIfGiftCardActivationRequiredSite.REACTIVATE; 
            }
        }
        // reset the inactiveTimeout flag in the cargo.
        cargo.setInactiveTimeout(false);
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

}
