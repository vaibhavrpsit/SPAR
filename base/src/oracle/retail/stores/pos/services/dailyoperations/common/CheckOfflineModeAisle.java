/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/CheckOfflineModeAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/12/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.common;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.DataManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

/**
 * This aisle will check to see if the data manager is offline. If it is, then
 * an {@link CommonLetterIfc#OFFLINE} letter is mailed. Otherwise, a
 * {@link CommonLetterIfc#CONTINUE} letter is mailed.
 * 
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CheckOfflineModeAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -6688675637575799275L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        Letter letter = new Letter(CommonLetterIfc.CONTINUE);

        DataManagerIfc dataManager = (DataManagerIfc)bus.getManager(DataManagerIfc.TYPE);
        if (dataManager != null)
        {
            if (!dataManager.getOnlineState())
            {
                letter = new Letter(CommonLetterIfc.OFFLINE);
            }
        }

        bus.mail(letter, BusIfc.CURRENT);
    }
}
