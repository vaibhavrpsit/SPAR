
/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.0     11/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

import java.util.List;

import org.apache.log4j.Logger;

import max.retail.stores.domain.customer.MAXCustomer;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXTICCustomerDataTransaction extends DataTransaction implements DataTransactionIfc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8099907687865271851L;
	/**
	 * The default name that links this transaction to a command within
	 * DataScript.
	 **/
	public static String dataCommandName = "MAXTICCustomerDataTransaction";

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXTICCustomerDataTransaction.class);

	/**
	 * revision number of this class
	 **/
	public static final String revisionNumber = "$Revision: 4$";

	public MAXTICCustomerDataTransaction() {
		super(dataCommandName);
	}

	/**
	 * 
	 * @param inventory
	 * @throws DataException
	 */

	// makes call to jdbc to retreive TIC Customer parameter values from
	// database Akhilesh

	public List getTICCustomerConfig() throws DataException {

		List listTic = null;
		// MAXVatExtraParametersIfc VatExtraParameters = new
		// MAXVatExtraParameters();
		logger.debug("MAXTICCustomerDataTransaction.getTICCustomerConfig");

		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("TICCustomerConfig");
		dataAction.setDataObject("");
		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;
		setDataActions(dataActions);
		listTic = (List) getDataManager().execute(this);
		return listTic;
	}

	public void updateCustomerTier(MAXCustomer customer) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXTICCustomerDataTransaction.updateCustomerTier");
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
			logger.debug("LSIPLTICCustomerDataTransaction.updateCustomerTier");
	}
}
