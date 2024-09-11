/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/scansheet/ScanSheetReturnShuttle.java /main/2 2012/02/28 13:27:06 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    asinto 02/28/12 - XbranchMerge asinton_bug-13732985 from
 *                      rgbustores_13.4x_generic_branch
 *    asinto 02/27/12 - refactored the flow so that items added from scan sheet
 *                      doesn't allow for a hang or mismatched letter.
 *    jkoppo 03/02/11 - New scan sheet tour return shuttle
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 * Retrieves data from the Scan Sheet service.
 * @author asinton
 *
 */
public class ScanSheetReturnShuttle implements ShuttleIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 3671796635466168184L;

    /**
     * Handle to the scan sheet cargo
     */
    private ScanSheetCargo scanSheetCargo;

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        scanSheetCargo = (ScanSheetCargo) bus.getCargo();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
    	SaleCargoIfc saleCargo = (SaleCargoIfc) bus.getCargo();
        saleCargo.setSelectedScanSheetItemID(scanSheetCargo.getSelectedScanSheetItemID());
        ((MAXSaleCargoIfc) saleCargo).setCategoryIDScanSheet(scanSheetCargo.getScansheetCategoryID());
        ((MAXSaleCargoIfc) saleCargo).setCategoryDescripionScanSheet(scanSheetCargo.getScansheetCategoryDescription());
    }

}
