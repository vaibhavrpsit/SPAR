/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Log:$
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mallcertificate;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderMallCertificateADO;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.tender.TenderCargo;

/**
 * This road sets the tender attributes to PO.
 * 
 * @author icole
 *
 */
@SuppressWarnings("serial")
public class MallCertificateAsPORoad extends PosLaneActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().put(TenderConstants.CERTIFICATE_TYPE, 
                                        TenderMallCertificateADO.MALL_GC_AS_PO);
    }

}
