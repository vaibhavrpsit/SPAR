/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/patriot/CancelCaptureIRSCustomerSite.java /main/11 2011/02/16 09:13:30 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:05 PM  Barry A. Pape   
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.patriot;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 * Clears PAT Customer info from cargo.
 * 
 * @version $Revision: /main/11 $
 */
public class CancelCaptureIRSCustomerSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 5029194435569892547L;

    /**
     * Clear PAT Customer info from Cargo
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        cargo.getCurrentTransactionADO().setIRSCustomer(null);

        bus.mail(new Letter(CommonLetterIfc.CANCEL), BusIfc.CURRENT);
    }
}
