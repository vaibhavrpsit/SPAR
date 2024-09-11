/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/SendToActivationAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   04/22/10 - Modified redeem tour to fix timeout issue.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/4/2008 4:21:07 AM    Manikandan Chellapan
 *         CR#30670 Reactivating the giftcard after redeem timeout.
 *    4    360Commerce 1.3         3/19/2008 4:46:01 AM   Manikandan Chellapan
 *         CR#30651 Fixed cancel gift card redeem transaction. Code reviewed
 *         by Naveen Ganesh.
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:09 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/06/23 15:27:32  bwf
 *   @scr 5312 DeActivate gift card when hitting undo.
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
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * @deprecated As of 13.3 This class is deprecated.  Use {@see oracle.retail.stores.pos.services.redeem.CheckIfGiftCardActivationRequiredSite} instead.
 */
public class SendToActivationAisle extends PosLaneActionAdapter
{
    public void traverse(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        String letterName = "Continue";
        TenderADOIfc tender = ((RedeemTransactionADO)cargo.getCurrentTransactionADO()).getRedeemTender();        
        if ( tender instanceof TenderGiftCardADO)
        {
            GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();
            giftCard.setEncipheredCardData(cargo.getGiftCard().getEncipheredCardData());
            giftCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM_VOID);
            giftCard.setCurrentBalance(tender.getAmount());
            cargo.setGiftCard(giftCard);
            letterName = "Activation";
            // Want all other sites to be able to realize they have a timed out transaction
            if(bus.getCurrentLetter().getName().equals(CommonLetterIfc.TIMEOUT))
            {
                // Reactivate the giftcard after giftcard redeem timeout
                letterName = "Reactivate"; 
            }
        }
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
