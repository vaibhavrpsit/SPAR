/*===========================================================================
* Copyright (c) 2011, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/SetRequestTypeHouseAccountAuthRefundRoad.java /main/2 2013/11/06 16:12:15 asinton Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* asinton     11/06/13 - fixed setting of request type to AuthorizeCardRefund
* sgu         10/17/11 - prompt for card swipe or manual entry once card tender
*                        buttons are clicked
* sgu         09/08/11 - add house account as a refund tender
* sgu         09/01/11 - Creation
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
public class SetRequestTypeHouseAccountAuthRefundRoad extends LaneActionAdapter
{

    /**
     * Set the Request Type to CardAuthorization
     */
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        HashMap<String,Object> tenderAttributes = cargo.getTenderAttributes();

        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.HOUSE_ACCOUNT);
        String token = (String) tenderAttributes.get(TenderConstants.ACCOUNT_NUMBER_TOKEN);
        if (cargo.refundToOriginalCard() && !Util.isEmpty(token))
        {
            tenderAttributes.put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCardRefundWithToken);
        }
        else
        {
            tenderAttributes.put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCardRefund);
            tenderAttributes.put(TenderConstants.ENCIPHERED_CARD_DATA, null);
            tenderAttributes.put(TenderConstants.ACCOUNT_NUMBER_TOKEN, null);
        }
    }

}

