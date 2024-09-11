/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/giftcertificate/CheckForeignGiftCertificateSite.java /main/12 2012/03/27 10:57:16 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/27/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/05/19 20:34:41  crain
 *   @scr 5080 Tender Redeem_Disc. Applied Alt Flow not Called from Foreign Gift Cert Alt Flow
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem.giftcertificate;

import oracle.retail.stores.pos.services.redeem.RedeemCargo;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
/**
     This site checks if the gift certificate is foreign
    @version $Revision: /main/12 $
    @deprecated in 14.0; no longer needed
 */
@SuppressWarnings("serial")
public class CheckForeignGiftCertificateSite extends SiteActionAdapter
{
    //--------------------------------------------------------------------------
    /**
     
     This site checks if the gift certificate is foreign
     @param bus the bus arriving at this site
     **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        RedeemCargo cargo = (RedeemCargo)bus.getCargo();
        Letter letter = null;
        if (cargo.isForeign())
        {
            letter = new Letter(CommonLetterIfc.FOREIGN);
        }
        else
        {
            letter = new Letter(CommonLetterIfc.CONTINUE);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
}
