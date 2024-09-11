/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.0     Dec 28, 2016		Ashish yadav			Changes for Online points redemption FES
*
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.manager.customer;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;

public interface MAXCustomerManagerIfc extends CustomerManagerIfc{

	public static final String TYPE = "CustomerManager";

	public abstract CustomerIfc getCustomer(CustomerSearchCriteriaIfc paramCustomerSearchCriteriaIfc)
			throws DataException;
}
