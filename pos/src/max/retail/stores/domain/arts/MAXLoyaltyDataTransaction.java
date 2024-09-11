/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0	 Prateek		20/05/2013		Initial Draft : Changes for TIC Customer Integration.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXLoyaltyDataTransaction extends DataTransaction implements DataTransactionIfc {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 919330276967237025L;

	/**
	 * The default name that links this transaction to a command within
	 * DataScript.
	 **/
	public static String dataCommandName = "LoyaltyDataTransaction";

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXLoyaltyDataTransaction.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXLoyaltyDataTransaction() {
		super(dataCommandName);
	}

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 * 
	 * @param name
	 *            transaction name
	 **/
	// ---------------------------------------------------------------------
	public MAXLoyaltyDataTransaction(String name) {
		super(name);
	}

	// ---------------------------------------------------------------------

	/*
	 * Encrypting the value for DB Function
	 */
	public HashMap encryptValue(HashMap encryptAtribute) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.encryptValue");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("EncryptAttributeValue");
		dataAction.setDataObject(encryptAtribute);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		HashMap encryptedAttributesValue = (HashMap) getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.encryptValue");

		return (encryptedAttributesValue);
	}

	/*
	 * Saving the Values in the DB for Store Web Request
	 */
	public void saveRequest(HashMap requestAtribute) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.saveRequest");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SaveWebRequestValue");
		dataAction.setDataObject(requestAtribute);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.saveRequest");

	}

	/*
	 * Update the Values in the DB for Store Web Request
	 */
	public void updateRequest(HashMap responseAtribute) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.updateRequest");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("UpdateWebRequestValue");
		dataAction.setDataObject(responseAtribute);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.updateRequest");

	}

	/*
	 * Selecting the Values from the DB for Store Web Request MAX - 167 Rev
	 * 1.1
	 */
	public List selectRequest() throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.selectRequest");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SelectWebRequestValue");
		// dataAction.setDataObject(requestAtribute);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		List requestAttributes = (List) getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXLoyaltyDataTransaction.selectRequest");
		return requestAttributes;
	}
}
