/* ===========================================================================
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/SetRequestTypeGiftCardAuthRoad.java /main/2 2013/05/03 16:53:08 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   05/02/13 - set the ACTION_CODE on the tender attributes hashmap
 *                         before calling Authorization service in order to
 *                         invoke the sale in the gift card formatter.
 *    blarsen   06/10/11 - Initial version. Set request type to
 *                         AuthorizeCard/GiftCard.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.HashMap;

import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc.RequestSubType;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;


@SuppressWarnings("serial")
public class SetRequestTypeGiftCardAuthRoad extends LaneActionAdapter
{

    /**
     * Set the to AuthorizeCard/GiftCard
     */
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCard);
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.GIFT_CARD);
        tenderAttributes.put(TenderConstants.ACTION_CODE, RequestSubType.AuthorizeSale);
    }

}
