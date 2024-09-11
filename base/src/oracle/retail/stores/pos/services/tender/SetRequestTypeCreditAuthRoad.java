/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/SetRequestTypeCreditAuthRoad.java /rgbustores_13.4x_generic_branch/4 2011/06/10 15:11:50 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/10/11 - Renamed AuthorizeCard to AuthorizeCard/Credit.
 *    blarsen   06/09/11 - Updated the renamed RequestType AuthorizeCard.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    blarsen   05/12/11 - "Road/LaneAction to explicitly set the tender's APF
 *                         request type to CardAuthorization."
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.domain.manager.payment.AuthorizeRequestIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;


@SuppressWarnings("serial")
public class SetRequestTypeCreditAuthRoad extends LaneActionAdapter
{

    /**
     * Set the to AuthorizeCard/Credit
     */
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCard);
        cargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.CREDIT);
    }

}
