/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processordersend/GetDestinationTaxRuleSite.java /main/1 2012/10/22 15:36:22 yiqzhao Exp $
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
public class GetDestinationTaxRuleSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Only send Next letter. This site will be replaced by GetVatRulesSite when the system installed at VAT enabled.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {

        Letter letter = new Letter(CommonLetterIfc.NEXT);

        bus.mail(letter, BusIfc.CURRENT);
    }

}
