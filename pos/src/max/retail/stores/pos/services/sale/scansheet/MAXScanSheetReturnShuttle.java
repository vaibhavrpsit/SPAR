/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Sep 10, 2018		Purushotham Reddy         Added the class for Code Merge CR	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale.scansheet;

import max.retail.stores.pos.services.sale.MAXSaleCargoIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * Retrieves data from the Scan Sheet service.
 * 
 * @author asinton
 * 
 */
public class MAXScanSheetReturnShuttle implements ShuttleIfc {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 3671796635466168184L;

	/**
	 * Handle to the scan sheet cargo
	 */
	private MAXScanSheetCargo scanSheetCargo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail
	 * .stores.foundation.tour.ifc.BusIfc)
	 */
	@Override
	public void load(BusIfc bus) {
		scanSheetCargo = (MAXScanSheetCargo) bus.getCargo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail
	 * .stores.foundation.tour.ifc.BusIfc)
	 */
	@Override
	public void unload(BusIfc bus) {
		// Bhanu Priya Changes starts
		MAXSaleCargoIfc saleCargo = (MAXSaleCargoIfc) bus.getCargo();
		saleCargo.setSelectedScanSheetItemID(scanSheetCargo
				.getSelectedScanSheetItemID());
		saleCargo.setCategoryIDScanSheet(scanSheetCargo
				.getScansheetCategoryID());
		saleCargo.setCategoryDescripionScanSheet(scanSheetCargo
				.getScansheetCategoryDescription());
		// Bhanu Priya Changes Ends

	}
}
