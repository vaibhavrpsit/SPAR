/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/OperatorDeclinedAisle.java /rgbustores_13.4x_generic_branch/8 2011/07/25 10:22:07 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   07/22/11 - Changed to not call payment manager to reverse auth.
 *                         This is now done by the reversal service prior to
 *                         reaching this aisle.
 *    cgreene   07/20/11 - tweak reversal support so requests must use
 *                         instanceof reversalrequest
 *    jswan     06/22/11 - Modified to support signature capture in APF.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    cgreene   05/20/11 - implemented enums for reponse code and giftcard
 *                         status code
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    asinton   03/21/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This aisle is traversed after the tender is reversed and sets the response to declined and
 * mails a "Declined" letter.
 *
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class OperatorDeclinedAisle extends LaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();

        AuthorizeTransferResponseIfc response = cargo.getCurrentResponse();

        if (response != null)
        {
            response.setResponseCode(ResponseCode.Declined);
        }

        bus.mail(new Letter(CommonLetterIfc.DECLINED), BusIfc.CURRENT);
    }
}