/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/GetVatRulesSite.java /main/3 2012/10/22 15:36:22 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/19/12 - Refactor to use DestinationTaxRule station to get
 *                         line item tax from send destination postal code.
 *    asinton   06/25/10 - Adding VAT classes back into source tree.
 *    asinton   06/25/10 - Adding VAT classes back into source tree.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         6/4/2007 1:44:23 PM    Sandy Gu        rework
 *       based on review comments
 *  1    360Commerce 1.0         5/17/2007 9:11:38 AM   Sandy Gu        Added
 *       this class to overlay GetTaxRulesSite to use local tax rules for a
 *       send transaction
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.displaysendmethod;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.send.address.SendCargo;

/**
 * Class for VAT get tax rules site
 */
public class GetVatRulesSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8331954753980102180L;

    /**
     * done letter
     **/
    public static final String DONE = "Done";

    /**
     * Arrival at the site. For VAT, we will not set send tax rules. It should
     * use the local tax rule for a send transaction as well.
     * 
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        SendCargo cargo = (SendCargo) bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);
        if (cargo.isTransactionLevelSendInProgress())
        {
            letter = new Letter(DONE);
        }

        logger.info("donot set send tax rules for VAT. Use local tax rules.");
        bus.mail(letter, BusIfc.CURRENT);
    }

}
