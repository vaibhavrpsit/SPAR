/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.  
 *
 *  Rev 1.0    Sep 12, 2022		Kamlesh Pant	CapLimit Enforcement for Liquor
 *
 ********************************************************************************/

package max.retail.stores.domain;

import java.io.Serializable;

public class MaxLiquorDetails implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8750399122751719019L;
	/**
	 * 
	 */
	
	
	String item;
	String LiqUMinLtr;
	String LiquorCategory;
	String Department;
	double beertot;
	public double getBeertot() {
		return beertot;
	}
	public void setBeertot(double beertot) {
		this.beertot = beertot;
	}
	public String getDepartment() {
		return Department;
	}
	public void setDepartment(String department) {
		Department = department;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getLiqUMinLtr() {
		return LiqUMinLtr;
	}
	public void setLiqUMinLtr(String liqUMinLtr) {
		LiqUMinLtr = liqUMinLtr;
	}
	public String getLiquorCategory() {
		return LiquorCategory;
	}
	public void setLiquorCategory(String liquorCategory) {
		LiquorCategory = liquorCategory;
	}
	@Override
	public String toString() {
		return "MaxLiquorDetails [item=" + item + ", LiqUMinLtr=" + LiqUMinLtr + ", LiquorCategory=" + LiquorCategory
				+ ", Department="+Department+"]";
	}
	

}
