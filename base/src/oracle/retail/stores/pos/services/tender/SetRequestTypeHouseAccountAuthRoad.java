/* ===========================================================================
* Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/SetRequestTypeHouseAccountAuthRoad.java /main/2 2013/11/06 16:12:15 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   11/06/13 - check if REQUEST_TYPE is not already set before
 *                         setting to AuthorizeCard
 *    ohorne    06/10/11 - apf: HouseAccount
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.util.Map;

import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;


@SuppressWarnings("serial")
public class SetRequestTypeHouseAccountAuthRoad extends LaneActionAdapter
{

    /**
     * Set the Request Type to CardAuthorization
     */
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        Map<String, Object> tenderAttributes = cargo.getTenderAttributes();
        if(tenderAttributes.get(TenderConstants.REQUEST_TYPE) == null)
        {
            tenderAttributes.put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCard);
        }
        tenderAttributes.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.HOUSE_ACCOUNT);
    }

}
