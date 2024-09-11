package max.retail.stores.pos.appmanager.send;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.pos.appmanager.send.SendManagerIfc;

public interface MAXSendManagerIfc extends SendManagerIfc{
	// Changes starts for code merging(adding below method as it is not present in base 14)
	public String getShippingCalculationType(ParameterManagerIfc pm);
	public CurrencyIfc getShippingCharge(String shippingCalculation,
            AbstractTransactionLineItemIfc[] lineItems);
	// Changes ends for code merging

}
