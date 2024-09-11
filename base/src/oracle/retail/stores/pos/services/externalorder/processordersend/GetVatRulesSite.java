/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processordersend/GetVatRulesSite.java /main/1 2012/10/22 15:36:22 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     10/19/12 - Refactor by using DestinationTaxRule station to get
*                        new tax rules from shipping destination postal code.
* yiqzhao     10/19/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.externalorder.processordersend;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


/**
 * Class for VAT get tax rules site
 */
public class GetVatRulesSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Arrival at the site. For VAT, we will not set send tax rules. It should
     * use the local tax rule for a send transaction as well.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.DONE);

        logger.info("donot set shipping tax rules for VAT. Use local tax rules.");
        bus.mail(letter, BusIfc.CURRENT);
    }
}
