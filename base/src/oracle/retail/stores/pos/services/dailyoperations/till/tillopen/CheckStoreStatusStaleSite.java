/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/CheckStoreStatusStaleSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/24/10 - convert to a site
 *    cgreene   02/24/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

/**
 * This site will check to see if the store's status is stale. If it is, then
 * an {@link CommonLetterIfc#CONTINUE} letter is mailed. Otherwise, the status
 * is uncertain and a {@link CommonLetterIfc#STATUS} letter is mailed.
 * 
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CheckStoreStatusStaleSite extends SiteActionAdapter
{
    private static final long serialVersionUID = -3544878137267900827L;

    /**
     * revision number of this class
     */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /*(non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);

        StoreStatusIfc status = cargo.getStoreStatus();
        if (status != null)
        {
            if (status.isStale())
            {
                letter = new Letter(CommonLetterIfc.STATUS);
            }
        }

        bus.mail(letter, BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
