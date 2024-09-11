/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/GiftCardDeactivationLaunchShuttle.java /rgbustores_13.4x_generic_branch/10 2011/08/18 16:04:15 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/18/11 - added journaling of activation, deactivation,
 *                         inquiring to tender.activation service.
 *    asinton   08/15/11 - fixed reversal (void) of gift card redeem.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   06/28/11 - refactor out tenderId in favor of token and
 *                         accountNumber
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    cgreene   05/27/11 - move auth response objects into domain
 *    blarsen   05/20/11 - Changed TenderType from int constants to a real
 *                         enum.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:13 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/08/31 19:12:36  blj
 *   @scr 6855 - cleanup gift card credit code and fix defects found by PBY
 *
 *   Revision 1.5  2004/05/11 16:05:29  blj
 *   @scr 4603 - fixed for post void of giftcard issue/reload/redeem/credit
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:15  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:28:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:16:02   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 23 2003 17:28:32   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 13:03:22   epd
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:07:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:44:02   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:22:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:34   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.postvoid;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.lineitem.LineItemTypeEnum;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the cargo used
 * in the POS service to the cargo used in the Gift Card Activation service.
 */
public class GiftCardDeactivationLaunchShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6767313232640310617L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(GiftCardDeactivationLaunchShuttle.class);

    /**
     * List of AuthorizeTransferRequestIfc objects
     */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /**
     * Transaction
     */
    protected VoidTransactionIfc voidTransaction;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        VoidCargo voidCargo = (VoidCargo)bus.getCargo();
        VoidTransactionADO voidTransactionADO = (VoidTransactionADO)voidCargo.getCurrentTransactionADO();
        voidTransaction = (VoidTransactionIfc)voidTransactionADO.toLegacy();
        SaleReturnLineItemIfc[] giftCardLineItems = voidTransaction.getProductGroupLineItems(LineItemTypeEnum.TYPE_GIFT_CARD.toString());
        requestList = new ArrayList<AuthorizeTransferRequestIfc>();
        if(giftCardLineItems != null)
        {
            for(int i = 0; i < giftCardLineItems.length; i++)
            {
                SaleReturnLineItemIfc item = giftCardLineItems[i];
                PLUItemIfc pluItem = item.getPLUItem();
                if(pluItem instanceof GiftCardPLUItemIfc)
                {
                    GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)pluItem;
                    // create instance of the request object
                    AuthorizeTransferRequestIfc request = buildActivateRequest(
                            voidCargo.getRegister().getWorkstation(),
                            pluItem.getPrice(),
                            giftCardPLUItem.getGiftCard());
                    // add it to the list
                    requestList.add(request);
                }
            }
        }
        if(voidTransaction.getOriginalTransaction() instanceof RedeemTransactionIfc)
        {
            RedeemTransactionIfc redeemTransaction = (RedeemTransactionIfc)voidTransaction.getOriginalTransaction();
            if(redeemTransaction.getRedeemTender() instanceof TenderGiftCardIfc)
            {
                TenderGiftCardIfc tenderGiftCard = (TenderGiftCardIfc)redeemTransaction.getRedeemTender();
                GiftCardIfc giftCard = tenderGiftCard.getGiftCard();
                AuthorizeTransferRequestIfc request = buildActivateRequest(
                        voidCargo.getRegister().getWorkstation(),
                        tenderGiftCard.getAmountTender(),
                        giftCard);
                // add it to the list
                requestList.add(request);
            }
        }
    }

    /**
     * Builds the activate request.
     * @param workstation
     * @param amount
     * @param giftCard
     * @return the activate request.
     */
    protected AuthorizeTransferRequestIfc buildActivateRequest(WorkstationIfc workstation, CurrencyIfc amount, GiftCardIfc giftCard)
    {
        AuthorizeTransferRequestIfc request = DomainGateway.getFactory().getAuthorizeTransferRequestInstance();
        // populate the request with the necessary information
        request.setWorkstation(workstation);
        request.setAccountNumber(giftCard.getCardNumber());
        request.setEntryMethod(giftCard.getEntryMethod());
        switch(giftCard.getRequestType())
        {
            case GiftCardIfc.GIFT_CARD_ISSUE :
                request.setRequestSubType(RequestSubType.Deactivate);
                break;
            case GiftCardIfc.GIFT_CARD_RELOAD :
                request.setRequestSubType(RequestSubType.ReloadVoid);
                break;
            case GiftCardIfc.GIFT_CARD_REDEEM :
                request.setRequestSubType(RequestSubType.RedeemVoid);
                break;
            default :
                logger.error("Unknown request type: " + giftCard.getRequestType());
                break;
        }
        request.setRequestTenderType(TenderType.GIFT_CARD);
        request.setRequestType(AuthorizeRequestIfc.RequestType.GiftCard);
        request.setBaseAmount(amount.abs());
        return request;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        activationCargo.setRequestList(requestList);
        activationCargo.setTransaction(voidTransaction);
    }

}
