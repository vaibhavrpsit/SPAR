package max.retail.stores.pos.services.common;

import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;

public class MAXAbstractFinancialCargo extends AbstractFinancialCargo{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6080895723821784220L;

	// changes starts for code merging(added below method as it not present in base 14)
	public boolean getSystemPos()
    {
        return true;
    }
	// Changes ends for code emrging

}
