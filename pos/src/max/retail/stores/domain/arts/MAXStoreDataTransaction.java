/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *

  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  29/April/2013               Himanshu              MAX-StoreCreditTender-FES_v1 2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

// java imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

//-------------------------------------------------------------------------
/**
 * @author Himanshu
 * 
 **/
// -------------------------------------------------------------------------
public class MAXStoreDataTransaction extends StoreDataTransaction {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 6425518018875259115L;
	/**
	 * The logger to which log messages will be sent.
	 */
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXStoreDataTransaction.class);

	/**
	 * The default name that links this transaction to a command within
	 * DataScript.
	 **/
	public static String dataCommandName = "MAXStoreDataTransaction";

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 */
	// ---------------------------------------------------------------------
	public MAXStoreDataTransaction() {
		super(dataCommandName);
	}

	/**
	 * MAX Customizations Gets store transfer entity id
	 * 
	 * Added by Himanshu
	 */
	public String getStoreTransferEntity(String storeId) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXStoreDataTransaction.getStoreTransferEntity");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("ReadStoreDetails");
		da.setDataObject(storeId);
		dataActions[0] = da;
		setDataActions(dataActions);

		String transferEntityId = (String) getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXStoreDataTransaction.getStoreTransferEntity");

		return transferEntityId;
	}

}
