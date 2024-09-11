package max.retail.stores.domain.arts;

import max.retail.stores.domain.MAXMobikwikResponse;
//import max.retail.stores.domain.MAXPaytmResponse;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

public class MAXMobikwikDataTransaction extends DataTransaction implements DataTransactionIfc {

	private static final long serialVersionUID = -7122992617889348298L;

	public static String dataCommandName = "MAXPaytmDataTransaction";

	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXPaytmDataTransaction.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 * <P>
	 **/
	// ---------------------------------------------------------------------
	public MAXMobikwikDataTransaction() {
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
	public MAXMobikwikDataTransaction(String name) {
		super(name);
	}

	
	public void saveMobikwikRequest(MAXMobikwikResponse response) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXMobikwikDataTransaction.saveRequest");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SaveMobikwikRequestValue");
		dataAction.setDataObject(response);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXMobikwikDataTransaction.saveRequest");

	}

	/*
	public boolean verifyDatabaseStatus(ARTSTill till)
	{
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("CheckDatabaseStatus");
		dataAction.setDataObject(till);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		try {
			getDataManager().execute(this);
		} catch (DataException e) {
			// TODO Auto-generated catch block
			logger.error("Database Status Offline during paytm db check");
			return Boolean.FALSE;
		}

		return Boolean.TRUE;

		
}
*/
}
