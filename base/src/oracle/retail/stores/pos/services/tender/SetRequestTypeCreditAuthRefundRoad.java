/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/SetRequestTypeCreditAuthRefundRoad.java /rgbustores_13.4x_generic_branch/3 2011/10/17 14:55:57 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       10/17/11 - prompt for card swipe or manual entry once card
 *                         tender buttons are clicked
 *    blarsen   09/27/11 - Changed request type to AuthorizeCardRefund. This
 *                         will prevent debit/credit/giftcard prompt from being
 *                         displayed to customer.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;


@SuppressWarnings("serial")
public class SetRequestTypeCreditAuthRefundRoad extends LaneActionAdapter
{

    /**
     * Set the to AuthorizeCard/Credit
     */
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);

        // If the token is available, use it to perform the refund against the original card.
        HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
        String token = (String) tenderAttributes.get(TenderConstants.ACCOUNT_NUMBER_TOKEN);
        if (cargo.refundToOriginalCard() && !Util.isEmpty(token))
        {
            cargo.getTenderAttributes().put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCardRefundWithToken);
        }
        else
        {
            cargo.getTenderAttributes().put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCardRefund);
            tenderAttributes.put(TenderConstants.ACCOUNT_NUMBER_TOKEN, null);
        }
    }
}
