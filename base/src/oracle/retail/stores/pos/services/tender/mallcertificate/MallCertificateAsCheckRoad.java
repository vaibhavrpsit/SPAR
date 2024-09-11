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
 * This road sets the tender attribute to Mall check.
 * 
 * @author icole
 *
 */
@SuppressWarnings("serial")
public class MallCertificateAsCheckRoad extends PosLaneActionAdapter
{

    @Override
    public void traverse(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        cargo.getTenderAttributes().put(TenderConstants.CERTIFICATE_TYPE,
                                        TenderMallCertificateADO.MALL_GC_AS_CHECK);
    }
}
