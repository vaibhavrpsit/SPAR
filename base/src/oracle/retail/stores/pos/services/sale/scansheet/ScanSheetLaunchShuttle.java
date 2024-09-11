/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/scansheet/ScanSheetLaunchShuttle.java /main/3 2013/03/06 14:16:09 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 03/06/13 - Removed unused new visit attribute
 *    asinto 02/28/12 - XbranchMerge asinton_bug-13732985 from
 *                      rgbustores_13.4x_generic_branch
 *    asinto 02/27/12 - refactored the flow so that items added from scan sheet
 *                      doesn't allow for a hang or mismatched letter.
 *    jkoppo 03/02/11 - New scan sheet tour launch shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Sends data to the Scan Sheet service.
 * @author asinton
 * 
 */
public class ScanSheetLaunchShuttle implements ShuttleIfc
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5910228927871836750L;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        ScanSheetCargo scanSheetCargo = (ScanSheetCargo) bus.getCargo();
    }

}
