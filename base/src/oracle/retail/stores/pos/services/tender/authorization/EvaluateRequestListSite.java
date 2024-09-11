/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/authorization/EvaluateRequestListSite.java /rgbustores_13.4x_generic_branch/1 2011/03/22 17:35:43 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/21/11 - new tender authorization service
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.authorization;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site evaluates the request list to see if more authorizations
 * are needed.
 * @author asinton
 * @since 13.4
 */
@SuppressWarnings("serial")
public class EvaluateRequestListSite extends PosSiteActionAdapter
{
    /** constant string for Authorize letter. */ 
    public static final String AUTHORIZE_LETTER = "Authorize";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AuthorizationCargo cargo = (AuthorizationCargo)bus.getCargo();
        cargo.incrementCurrentIndex();
        String letter = CommonLetterIfc.SUCCESS;
        if(cargo.getCurrentRequest() != null)
        {
            letter = AUTHORIZE_LETTER;
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

}
