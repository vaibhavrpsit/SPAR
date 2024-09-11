/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcard/GiftCardActivationLaunchShuttle.java /main/14 2012/02/15 12:36:56 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abananan  09/26/14 - Fix for bug-19366855
 *    asinton   02/15/12 - XbranchMerge asinton_bug-13714945 from
 *                         rgbustores_13.4x_generic_branch
 *    asinton   02/15/12 - include transactionID in the gift card activation
 *                         requests.
 *    asinton   08/22/11 - pass transaction to the activation service
 *    ohorne    08/09/11 - APF:foreign currency support
 *    cgreene   07/12/11 - update generics
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
 |    7    360Commerce 1.6         5/29/2008 11:18:36 AM  Alan N. Sinton  CR
 |         31655: Code to allow refund of monies to multiple gift cards.  Code
 |          changes reviewed by Dan Baker.
 |    6    360Commerce 1.5         5/7/2008 5:13:42 PM    Tony Zgarba     CR
 |         31555.  Updated to check that only RetailTransaction ADOs are
 |         loaded into the shuttle.  All other types are ignored.  Reviewed by
 |          Jack Swan.
 |    5    360Commerce 1.4         5/7/2008 12:05:43 PM   Jack G. Swan
 |         Changed to support putting returned deposits from Layaway and Order
 |          delete/cancel transactions onto new and existing giftcards.  This
 |         code was reviewed by Brett Larson.
 |    4    360Commerce 1.3         4/27/2008 5:28:27 PM   Charles D. Baker CR
 |         31482 - Updated the journalResponse method of GetResponseSite to
 |         intelligently journal entries with the appropriate journal type
 |         (Trans or Not Trans). Code Review by Tony Zgarba.
 |    3    360Commerce 1.2         4/24/2008 5:30:04 PM   Charles D. Baker CR
 |         31452 - Attached current transaction to gift card inquiry journal
 |         entry when one is available. Code review by Anda Cadar.
 |    2    360Commerce 1.1         2/15/2006 5:23:27 AM   Akhilashwar K. Gupta 
 |    1    360Commerce 1.0         2/15/2006 5:13:05 AM   Akhilashwar K. Gupta 
 |   $
 |
 | $Revision: /main/14 $
 | Feb 14, 2006 21:39:22 Akhilashwar Initial revision
 |
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle copies information from the cargo used in the redeem gift card
 * service to the cargo used in the Gift Card Activation service.
 */
public class GiftCardActivationLaunchShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2065194675767018765L;

    /**
     * Logger
     */
    private static final Logger logger = Logger.getLogger(GiftCardActivationLaunchShuttle.class);

    /**
     * List of AuthorizeTransferRequestIfc objects
     */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /**
     * Handle to the transaction
     */
    protected TenderableTransactionIfc transaction;
    
    /**
     * Handle to the register
     */
    protected RegisterIfc register;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        TenderCargo tenderCargo = (TenderCargo) bus.getCargo();
        EYSDomainIfc object = tenderCargo.getCurrentTransactionADO().toLegacy();
        if(object instanceof TenderableTransactionIfc)
        {
            transaction = (TenderableTransactionIfc)object;
            register = tenderCargo.getRegister();
        }
        GiftCardIfc giftCard = tenderCargo.getGiftCard();
        requestList = new ArrayList<AuthorizeTransferRequestIfc>();
        if(giftCard != null)
        {
            // create instance of the request object
            AuthorizeTransferRequestIfc request = DomainGateway.getFactory().getAuthorizeTransferRequestInstance();
            // populate the request with the necessary information
            request.setWorkstation(tenderCargo.getRegister().getWorkstation());
            request.setAccountNumber(giftCard.getCardNumber());
            request.setEntryMethod(giftCard.getEntryMethod());
            request.setTransactionID(transaction.getTransactionIdentifier().getTransactionIDString());
            switch(tenderCargo.getAuthorizationTransactionType())
            {
                case GiftCardIfc.GIFT_CARD_ISSUE :
                    request.setRequestSubType(RequestSubType.Activate);
                    break;
                case GiftCardIfc.GIFT_CARD_RELOAD :
                    request.setRequestSubType(RequestSubType.ReloadGiftCard);
                    break;
                default :
                    logger.error("Unknown request type: " + giftCard.getRequestType());
                    break;
            }
            request.setRequestTenderType(AuthorizationConstantsIfc.TenderType.GIFT_CARD);
            Map<String,Object> tenderAttributes = tenderCargo.getTenderAttributes();
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance((String)tenderAttributes.get(TenderConstants.AMOUNT));
            request.setBaseAmount(amount.abs());
            request.setRequestType(AuthorizeRequestIfc.RequestType.GiftCard);
            // add it to the list
            requestList.add(request);
        }
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        activationCargo.setRequestList(requestList);
        activationCargo.setTransaction(transaction);
        activationCargo.setRegister(register);;
    }
}
