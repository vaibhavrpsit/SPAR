/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev	1.0 	Sep 10, 2018		Purushotham Reddy         Added the class for Code Merge CR	
 *
 ********************************************************************************/

package max.retail.stores.pos.services.sale.scansheet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.sale.scansheet.ScanSheetCargo;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;

public class MAXScanSheetCargo extends ScanSheetCargo implements CargoIfc,
		Serializable {
	private static final long serialVersionUID = -4047305408354225643L;

	private ImageGridBeanModel imageGridBeanModel;

	private boolean isNewVisitToScanSheet;

	/**
	 * This map holds the return page numbers for the categories Required during
	 * return from one category to other
	 */
	private Map<String, Integer> returnPageNumberMap = new HashMap<String, Integer>(
			0);

	/**
	 * selected scan sheet item ID.
	 */
	private String selectedScanSheetItemID;

	public Map<String, Integer> getReturnPageNumberMap() {
		return returnPageNumberMap;
	}

	public void setReturnPageNumber(Map<String, Integer> returnPageNumberMap) {
		this.returnPageNumberMap = returnPageNumberMap;
	}

	public ImageGridBeanModel getImageGridBeanModel() {
		return imageGridBeanModel;
	}

	public void setImageGridBeanModel(ImageGridBeanModel imageGridBeanModel) {
		this.imageGridBeanModel = imageGridBeanModel;
	}

	/**
	 * Sets the selected scan sheet item.
	 * 
	 * @param itemID
	 */
	public void setSelectedScanSheetItemID(String itemID) {
		this.selectedScanSheetItemID = itemID;
	}

	/**
	 * Gets the selected scan sheet item.
	 * 
	 * @return the selected scan sheet item.
	 */
	public String getSelectedScanSheetItemID() {
		return this.selectedScanSheetItemID;
	}

	private String categoryID;
	private String categoryDescription;

	public void setScansheetCategoryID(String categoryID) {
		this.categoryID = categoryID;
	}

	public String getScansheetCategoryID() {
		return this.categoryID;
	}

	public void setScansheetCategoryDescription(String categoryDescription) {
		this.categoryDescription = categoryDescription;
	}

	public String getScansheetCategoryDescription() {
		return this.categoryDescription;
	}
}
