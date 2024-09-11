/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *  
 *  
 *	Rev 1.1		Oct 26, 2016	Mansi Goel			Changes for resolve Classcast exception
 *	Rev 1.0     Oct 19, 2016	Mansi Goel			Code Merging
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.event;

import max.retail.stores.domain.lineitem.MAXMaximumRetailPriceChangeIfc;
import oracle.retail.stores.domain.event.PriceChange;

public class MAXPriceChange extends PriceChange implements MAXPriceChangeIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3008714678070271910L;
	// Changes for Rev 1.0 : Starts
	protected MAXMaximumRetailPriceChangeIfc maximumRetailPriceChange = null;
	protected String appliedOn;

	public MAXMaximumRetailPriceChangeIfc getMaximumRetailPriceChange() {
		return maximumRetailPriceChange;
	}

	public void setMaximumRetailPriceChange(
			MAXMaximumRetailPriceChangeIfc maximumRetailPriceChange) {
		this.maximumRetailPriceChange = maximumRetailPriceChange;
	}

	public String getAppliedOn() {
		return this.appliedOn;
	}

	public void setAppliedOn(String appliedOn) {
		this.appliedOn = appliedOn;
	}

	public void setCloneAttributes(MAXPriceChange newClass) {
		super.setCloneAttributes(newClass);
		if (maximumRetailPriceChange != null) {
			newClass.setMaximumRetailPriceChange((MAXMaximumRetailPriceChangeIfc) getMaximumRetailPriceChange()
					.clone());
		}
		if (appliedOn != null) {
			newClass.setAppliedOn(appliedOn);
		}
	}
	// Changes for Rev 1.0 : Ends
	
	// Changes for Rev 1.1 : Starts
	public Object clone() {
		MAXPriceChange c = new MAXPriceChange();
		setCloneAttributes(c);
		return c;
	}
	// Changes for Rev 1.1 : Ends
	protected String spclEmpDisc;
	@Override
	public String getSpclEmpDis() {
		// TODO Auto-generated method stub
		return spclEmpDisc;
	}

	@Override
	public void setSpclEmpDis(String spclEmpDisc) {
		// TODO Auto-generated method stub
		this.spclEmpDisc = spclEmpDisc;
	}
}
