/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcard/GiftCardActivationLaunchShuttle.java /rgbustores_13.4x_generic_branch/9 2011/09/07 08:33:37 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/06/11 - Fixed issues with gift card balance when
 *                         issuing/reloading multiple gift cards and one card
 *                         fails.
 *    asinton   08/22/11 - set transaction to prevent npe
 *    asinton   08/18/11 - added journaling of activation, deactivation,
 *                         inquiring to tender.activation service.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   06/28/11 - refactor out tenderId in favor of token and
 *                         accountNumber
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    asinton   05/31/11 - Refactored Gift Card Redeem and Tender for APF
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 |   5    360Commerce 1.4         5/5/2008 5:12:08 PM    Alan N. Sinton  CR
 |        30689: Added register ID and transaction ID to the request and log
 |        it.  Code reviewed by Mathews Kochummen.
 |   4    360Commerce 1.3         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |        31482 - Updated the journalResponse method of GetResponseSite to
 |        intelligently journal entries with the appropriate journal type
 |        (Trans or Not Trans). Code Review by Tony Zgarba.
 |   3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse   
 |   2    360Commerce 1.1         3/10/2005 10:21:52 AM  Robert Pearse   
 |   1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse   
 |  $
 |  Revision 1.5  2004/09/23 00:07:17  kmcbride
 |  @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 |
 |  Revision 1.4  2004/04/22 17:33:20  lzhao
 |  @scr 3872: code review. remove unused code.
 |
 |  Revision 1.3  2004/04/13 19:02:22  lzhao
 |  @scr 3872: gift card redeem.
 |
 |  Revision 1.2  2004/04/08 20:33:02  cdb
 |  @scr 4206 Cleaned up class headers for logs and revisions.
 |
 |  Revision 1.1  2004/03/31 16:17:23  lzhao
 |  @scr 3872: gift card redeem service update
 |
 |  Revision 1.2  2004/03/25 23:01:23  lzhao
 |  @scr #3872 Redeem Gift Card
 |
 |  $Revision: /rgbustores_13.4x_generic_branch/9 $
 |  Mar 23, 2004 lzhao
 | 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcard;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

/**
 * This shuttle copies information from the cargo used
 * in the redeem gift card service to the cargo used in the Gift Card Activation service.
 */
public class GiftCardActivationLaunchShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2401020692262733543L;
    /**
     * List of AuthorizeTransferRequestIfc objects
     */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /**
     * Handle to the transaction
     */
    protected TenderableTransactionIfc transaction;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        RedeemCargo redeemCargo = (RedeemCargo) bus.getCargo();
        GiftCardIfc giftCard = redeemCargo.getGiftCard();
        EYSDomainIfc object = redeemCargo.getCurrentTransactionADO().toLegacy();
        if(object instanceof TenderableTransactionIfc)
        {
            transaction = (TenderableTransactionIfc)object;
        }
        requestList = new ArrayList<AuthorizeTransferRequestIfc>();
        if(giftCard != null)
        {
            // create instance of the request object
            AuthorizeTransferRequestIfc request = DomainGateway.getFactory().getAuthorizeTransferRequestInstance();
            // populate the request with the necessary information
            request.setWorkstation(redeemCargo.getRegister().getWorkstation());
            request.setAccountNumber(giftCard.getCardNumber());
            request.setEntryMethod(giftCard.getEntryMethod());

            if(giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_REDEEM_VOID)
            {
                request.setRequestSubType(RequestSubType.RedeemVoid);
            }
            else
            {
                request.setRequestSubType(RequestSubType.Redeem);
            }
            request.setRequestTenderType(TenderType.GIFT_CARD);
            request.setBaseAmount(giftCard.getReqestedAmount().abs());
            request.setRequestType(AuthorizeRequestIfc.RequestType.GiftCard);
            // add it to the list
            requestList.add(request);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {                                   
        ActivationCargo activationCargo = (ActivationCargo) bus.getCargo();
        activationCargo.setRequestList(requestList);
        activationCargo.setTransaction(transaction);
    }                                   
}
