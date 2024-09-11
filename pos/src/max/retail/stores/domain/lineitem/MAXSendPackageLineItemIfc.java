package max.retail.stores.domain.lineitem;

import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;

public interface MAXSendPackageLineItemIfc extends SendPackageLineItemIfc{
	// Chnages starts for code merging(below methods is not present in base 14)
	public FinancialTotalsIfc getFinancialTotals(boolean isNotVoid);
	// Changes ends for code merging

}
