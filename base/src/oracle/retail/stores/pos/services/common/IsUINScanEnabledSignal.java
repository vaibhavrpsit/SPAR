/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/17/09 - Added extra condition of serialisation property
 *    nkgautam  12/16/09 - Signal class created for IMEI
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * This signal decides the tour flow on the basis of
 * IMEI enabled/disabled boolean.
 * @author nkgautam
 */
public class IsUINScanEnabledSignal implements TrafficLightIfc
{

    /**
     * Checks whether IMEI is enalbed or not
     */
    public boolean roadClear(BusIfc bus)
    {

        boolean result = false;
        boolean IMEIEnabled = false;
        boolean serialisationEnabled = false;
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        IMEIEnabled = utility.getIMEIProperty();
        serialisationEnabled = utility.getSerialisationProperty();
        if(IMEIEnabled && serialisationEnabled)
        {
            result=true;
        }
        else
        {
            result=false;
        }
        return (result);
    }

}
