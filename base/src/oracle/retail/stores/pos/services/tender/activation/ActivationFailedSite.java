/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/ActivationFailedSite.java /rgbustores_13.4x_generic_branch/1 2011/07/26 16:59:02 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   07/26/11 - Evaluates if gift card activation should be retried
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site evaluates the activation error and decides if a retry is appropriate
 * or to just exit with Failure letter
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class ActivationFailedSite extends PosSiteActionAdapter
{
    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ActivationCargo activationCargo = (ActivationCargo)bus.getCargo();
        AuthorizeTransferRequestIfc request = activationCargo.getCurrentRequest();
        AuthorizeTransferResponseIfc response = activationCargo.getCurrentResponse();
        // assume failure
        String letter = CommonLetterIfc.FAILURE;
        RequestSubType requestSubType = request.getRequestSubType();
        if(!RequestSubType.Inquiry.equals(requestSubType) &&
                !RequestSubType.Deactivate.equals(requestSubType) &&
                !RequestSubType.RedeemVoid.equals(requestSubType) &&
                !RequestSubType.VoidGiftCard.equals(requestSubType) &&
                !RequestSubType.ReloadVoid.equals(requestSubType))
        {
            if(ResponseCode.Declined.equals(response.getResponseCode()))
            {
                letter = CommonLetterIfc.CONTINUE;
            }
        }
        bus.mail(letter, BusIfc.CURRENT);
    }

}
