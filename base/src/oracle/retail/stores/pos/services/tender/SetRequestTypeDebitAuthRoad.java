/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/SetRequestTypeDebitAuthRoad.java /rgbustores_13.4x_generic_branch/1 2011/06/10 15:11:50 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   06/10/11 - Initial version. Set the request type to
 *                         AuthorizeCard/Debit
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
public class SetRequestTypeDebitAuthRoad extends LaneActionAdapter
{

    /**
     * Set the to AuthorizeCard/Debit
     */
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().put(TenderConstants.REQUEST_TYPE, AuthorizeRequestIfc.RequestType.AuthorizeCard);
        cargo.getTenderAttributes().put(TenderConstants.TENDER_TYPE, TenderTypeEnum.DEBIT);
    }

}
