package max.retail.stores.domain.order;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;

public interface MAXOrderStatusIfc extends OrderStatusIfc{
	
	public void setTotal(CurrencyIfc value);
	public void setMinimumDepositAmount(CurrencyIfc value);
	public void setDepositAmount(CurrencyIfc value);

}
