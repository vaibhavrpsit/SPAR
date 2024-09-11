/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/security/common/SecurityOverrideNoUnlockLaunchShuttle.java /main/1 2013/11/19 09:42:41 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/18/13 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin.security.common;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.admin.security.override.SecurityOverrideCargo;

/**
 * Loads and unloads from a {@link UserAccessCargoIfc} but instructs UI to
 * not unlock the primary screen during the tour.
 *
 * @author cgreene
 * @since 14.0
 */
public class SecurityOverrideNoUnlockLaunchShuttle extends SecurityOverrideLaunchShuttle
{
    private static final long serialVersionUID = -7546876803183225003L;


    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        SecurityOverrideCargo launchingCargo = (SecurityOverrideCargo) bus.getCargo();
        launchingCargo.setUnlockScreenAfterDialog(false);
    }
}
