/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftcardinquiry/GiftCardInquiryLaunchShuttle.java /rgbustores_13.4x_generic_branch/9 2011/08/18 16:04:15 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/18/11 - added journaling of activation, deactivation,
 *                         inquiring to tender.activation service.
 *    ohorne    08/09/11 - APF:foreign currency support
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    cgreene   06/28/11 - refactor out tenderId in favor of token and
 *                         accountNumber
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    cgreene   05/27/11 - move auth response objects into domain
 *    blarsen   05/20/11 - Changed TenderType from int constants to a real
 *                         enum.
 *    sgu       05/05/11 - set request type for gift card authorize request
 *    asinton   05/02/11 - Gift Card refactor for APF
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.giftcardinquiry;

import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

/**
 * Prepare the list of AuthorizeTransferRequestIfc instances for the
 * Tender Authorization service.
 *
 * @author asinton
 * @since 13.4
 */
public class GiftCardInquiryLaunchShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3905487754564687886L;
    /**
     * List of AuthorizeTransferRequestIfc objects
     */
    protected List<AuthorizeTransferRequestIfc> requestList;

    /**
     * Flag to indicate that there is a transaction in progress
     */
    protected boolean transactionInProgress;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        InquiryCargo inquiryCargo = (InquiryCargo)bus.getCargo();
        requestList = new ArrayList<AuthorizeTransferRequestIfc>(1);
        GiftCardIfc giftCard = inquiryCargo.getGiftCard();
        transactionInProgress = inquiryCargo.isTransactionInProgress();
        if(giftCard != null)
        {
            // create instance of the request object
            AuthorizeTransferRequestIfc request = DomainGateway.getFactory().getAuthorizeTransferRequestInstance();
            // populate the request with the necessary information
            request.setWorkstation(inquiryCargo.getRegister().getWorkstation());
            request.setAccountNumber(giftCard.getCardNumber());
            request.setEntryMethod(giftCard.getEntryMethod());
            request.setRequestSubType(RequestSubType.Inquiry);
            request.setRequestTenderType(TenderType.GIFT_CARD);
            request.setBaseAmount(DomainGateway.getBaseCurrencyInstance());
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
        // set the requestList on the AuthorizationCargo
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        activationCargo.setRequestList(requestList);
        activationCargo.setTransactionInProgress(transactionInProgress);
    }

}
