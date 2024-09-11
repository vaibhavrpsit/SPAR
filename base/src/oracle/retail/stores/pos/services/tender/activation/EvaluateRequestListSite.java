/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/activation/EvaluateRequestListSite.java /main/2 2014/02/10 15:44:37 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   02/06/14 - reworked flow for gift card activation error
 *                         scenarios
 *    asinton   05/04/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *    asinton   05/03/11 - New activation service for APF.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.activation;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

/**
 * This site evaluates the request list to see if more activations
 * are needed.
 * @since 13.4
 */
@SuppressWarnings("serial")
public class EvaluateRequestListSite extends PosSiteActionAdapter
{
    /** constant string for Activate letter. */ 
    public static final String ACTIVATE_LETTER = "Activate";

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ActivationCargo cargo = (ActivationCargo)bus.getCargo();
        cargo.incrementCurrentIndex();
        String letter = CommonLetterIfc.SUCCESS;
        if(cargo.getCurrentRequest() != null)
        {
            letter = ACTIVATE_LETTER;
        }
        else if(!cargo.getFailedLineNumbersList().isEmpty())
        {
            letter = CommonLetterIfc.FAILURE;
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

}
