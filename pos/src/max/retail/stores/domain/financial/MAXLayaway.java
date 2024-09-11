/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 * 
 *	Rev 1.0   12/08/2014   Shruti Singh 	Initial Draft	Centralized Employee Discount 
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.financial;

import max.retail.stores.domain.employee.MAXEmployee;
import oracle.retail.stores.domain.financial.Layaway;

public class MAXLayaway extends Layaway {

	protected MAXEmployee maxEmployee = null;

	public MAXEmployee getMaxEmployee() {
		return maxEmployee;
	}

	public void setMaxEmployee(MAXEmployee maxEmployee) {
		this.maxEmployee = maxEmployee;
	}

	public Object clone() {
		MAXLayaway c = new MAXLayaway();

		setCloneAttributes(c);

		return c;
	}

	protected void setCloneAttributes(MAXLayaway newClass) {
		super.setCloneAttributes(newClass);
		newClass.setMaxEmployee(maxEmployee);
	}
}
