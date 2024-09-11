/* ===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/giftcardinquiry/GiftCardInquiryReturnShuttle.java /main/2 2013/02/28 15:30:05 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    asinton   05/02/11 - Gift Card refactor for APF
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.inquiry.giftcardinquiry;

import java.util.List;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

/**
 * Retrieves the list of AuthorizeTransferResponseIfc objects and populates
 * the information into gift card. 
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class GiftCardInquiryReturnShuttle implements ShuttleIfc
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
        if(responseList != null && !responseList.isEmpty())
        {
            // retrieve the response from the list
            AuthorizeTransferResponseIfc response = responseList.get(0);
            if(response != null)
            {
                InquiryCargo inquiryCargo = (InquiryCargo)bus.getCargo();
                GiftCardIfc giftCard = inquiryCargo.getGiftCard();
                giftCard.setCurrentBalance(response.getCurrentBalance());
                giftCard.setInitialBalance(response.getCurrentBalance());
                giftCard.setStatus(response.getStatus());
                giftCard.setTraceNumber(response.getTraceNumber());
            }        
        }
    }

}
