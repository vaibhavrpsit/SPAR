/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/06/13 - deprecate negative version of trafficlight and just
 *                         use XML option instead
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/16/09 - Signal class created for Serialisation
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * This signal decides the tour flow on the basis of serialization
 * enabled/disabled boolean.
 * 
 * @author nkgautam
 */
@SuppressWarnings("serial")
public class IsSerialisationEnabledSignal implements TrafficLightIfc
{

    /**
     * Checks whether serialization is enabled or not.
     * 
     * @param bus Service Bus
     */
    public boolean roadClear(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        boolean serialisationEnabled = utility.getSerialisationProperty();
        return serialisationEnabled;
    }

}
