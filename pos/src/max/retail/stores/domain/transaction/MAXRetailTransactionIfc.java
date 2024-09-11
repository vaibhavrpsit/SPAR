package max.retail.stores.domain.transaction;

import java.util.Currency;

import oracle.retail.stores.domain.transaction.RetailTransactionIfc;

public interface MAXRetailTransactionIfc extends RetailTransactionIfc {
	
	 	Currency getItemPrice();

	   void setItemPrice(String var1);

}
