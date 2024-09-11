/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/SerialNumberSite.java
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 * MODIFIED    (MM/DD/YY)
 * icole        09/03/14 - initial - collect serial number when balance paid in full.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.layaway.payment;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/*
 * Passing to an existing Aisle to collect Serial Number if needed.
 * @since 14.1 
 */
@SuppressWarnings("serial")
public class SerialNumberSite extends PosSiteActionAdapter 
{
    public void arrive(BusIfc bus)
    {
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}

