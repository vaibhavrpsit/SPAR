package max.retail.stores.pos.appmanager.send;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.pos.appmanager.send.SendManager;

public class MAXSendManager extends SendManager implements MAXSendManagerIfc{

	@Override
	public String getShippingCalculationType(ParameterManagerIfc pm) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CurrencyIfc getShippingCharge(String shippingCalculation, AbstractTransactionLineItemIfc[] lineItems) {
		// TODO Auto-generated method stub
		return null;
	}

}
