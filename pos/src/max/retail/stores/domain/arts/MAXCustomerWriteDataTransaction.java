/********************************************************************************
*   
*	Copyright (c) 2015  Lifestyle India pvt Ltd    All Rights Reserved.
*	
*	Rev	1.0 	22-Oct-2018		Jyoti Yadav		LS Edge Phase 2	
*
********************************************************************************/
package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import max.retail.stores.domain.customer.MAXCustomer;
import oracle.retail.stores.domain.arts.CustomerWriteDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

public class MAXCustomerWriteDataTransaction extends CustomerWriteDataTransaction {
	private static final long serialVersionUID = 4284048869904130352L;
	private static Logger logger = Logger
			.getLogger(MAXCustomerWriteDataTransaction.class);
	public static String dataUpdateName = "LSIPLCustomerWriteDataTransaction";

	public MAXCustomerWriteDataTransaction() {
		super(dataUpdateName);
	}

	public MAXCustomerWriteDataTransaction(String name) {
		super(name);
	}

	public void updateCustomerTier(MAXCustomer customer) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("LSIPLCustomerWriteDataTransaction.updateCustomerTier");
		// ARTSCustomer artsCustomer = new ARTSCustomer(customer);
		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];

		DataAction da = new DataAction();
		da.setDataOperationName("UpdateCustomerTier");
		da.setDataObject(customer);
		dataActions[0] = da;
		setDataActions(dataActions);

		CustomerIfc result = (CustomerIfc) getDataManager().execute(this);
		if (result != null) {
			customer.setCustomerID(result.getCustomerID());
		}
		if (logger.isDebugEnabled())
			logger.debug("MAXCustomerWriteDataTransaction.updateCustomerTier");
	}

}
