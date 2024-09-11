/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/giftcard/GiftCardActivationReturnShuttle.java /rgbustores_13.4x_generic_branch/4 2011/09/16 10:48:10 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     09/15/11 - Fixed issues with completely creating of a refund
 *                         gift card tender from a issue/reload response
 *                         object.
 *    jswan     08/29/11 - Fixed Available Balance value on receipt for return
 *                         amount credited (reloaded) to an existing giftcard.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         5/29/2008 11:18:51 AM  Alan N. Sinton  CR
 *       31655: Code to allow refund of monies to multiple gift cards.  Code
 *       changes reviewed by Dan Baker.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.giftcard;

import java.util.List;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ado.tender.AuthorizedTenderADOBuilderIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.activation.ActivationCargo;

/**
 * GiftCardActivationReturnShuttle copies information from the cargo used
 * in the gift card activation service to the calling service.
 */
public class GiftCardActivationReturnShuttle implements ShuttleIfc
{
    /** serialVersionUID     */
    private static final long serialVersionUID = 65229120176224987L;
    
    /** Activation response object */
    protected AuthorizeTransferResponseIfc response = null;
    
    /**
     * Load method.
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void load(BusIfc bus)
    {
        // There is only one response; get the first one in the list.
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        List<AuthorizeTransferResponseIfc> list = cargo.getResponseList();
        response = list.get(0);
    }

    /**
     * Unload method
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void unload(BusIfc bus)
    {
        if(ResponseCode.Approved.equals(response.getResponseCode()))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            AuthorizedTenderADOBuilderIfc builder = (AuthorizedTenderADOBuilderIfc)BeanLocator.getBean(BeanLocator.APPLICATION_CONTEXT_KEY, AuthorizedTenderADOBuilderIfc.BEAN_KEY);
            TenderADOIfc tender = builder.buildTenderADO(response);
            TenderGiftCardIfc tcg = (TenderGiftCardIfc)tender.toLegacy();
            // Make absolutely sure the tender amount is negative.
            tcg.setAmountTender(tcg.getAmountTender().abs().negate());
            cargo.setTenderADO(tender);
        }
    }
}
