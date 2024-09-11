package max.retail.stores.pos.services.sale.multiplemrp;

import max.retail.stores.pos.services.common.MAXAbstractFinancialCargo;
import oracle.retail.stores.domain.stock.PLUItemIfc;

// Changes starts for code merging(commenting below line)
public class MAXMultipleMRPCargo extends MAXAbstractFinancialCargo{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3438792870627289198L;
	// Changes ends for code merging
	protected boolean isApplyBestDeal = false;
	protected PLUItemIfc item;
	protected String initialOriginLetter = null;// Sakshi for best deal button

	public boolean isApplyBestDeal() {
		return isApplyBestDeal;
	}

	public void setApplyBestDeal(boolean isApplyBestDeal) {
		this.isApplyBestDeal = isApplyBestDeal;
	}
	
	public String getInitialOriginLetter() {
		return initialOriginLetter;
	}

	public void setInitialOriginLetter(String initialOriginLetter) {
		this.initialOriginLetter = initialOriginLetter;
	}
	// Changes starts for code merging(adding below methods as it is not present in base 14)
	
	public void setPLUItem(PLUItemIfc value)
    {
        item = value;
    }
	public PLUItemIfc getPLUItem()
    {
        return(item);
    }
	// Changs ends for code merging
}
