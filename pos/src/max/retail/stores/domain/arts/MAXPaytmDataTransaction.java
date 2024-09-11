package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import max.retail.stores.domain.MAXAmazonPayResponse;
import max.retail.stores.domain.MAXMobikwikResponse;
import max.retail.stores.domain.MAXPaytmResponse;
//import max.retail.stores.domain.MAXPaytmResponse;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;



public class MAXPaytmDataTransaction  extends DataTransaction implements DataTransactionIfc {

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
	public MAXPaytmDataTransaction() {
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
	public MAXPaytmDataTransaction(String name) {
		super(name);
	}

	/*
	 * Saving the Values in the DB for Store Web Request
	 */
	public void saveRequest(MAXPaytmResponse response) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXPaytmDataTransaction.saveRequest");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SavePaytmRequestValue");
		dataAction.setDataObject(response);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXPaytmDataTransaction.saveRequest");

	}
	
	public void saveMobikwikRequest(MAXMobikwikResponse response) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXPaytmDataTransaction.saveRequest");

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SaveMobikwikRequestValue");
		dataAction.setDataObject(response);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

		if (logger.isDebugEnabled())
			logger.debug("MAXPaytmDataTransaction.saveRequest");

	}

	
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
			logger.error("Database Status Offline during paytm db check");
			return Boolean.FALSE;
		}

		return Boolean.TRUE;

		
}

	public void saveAmazonPayChargeRequest(MAXAmazonPayResponse response) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXPaytmDataTransaction.saveAmazonPayChargeRequest");

		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SaveAmazonPayChargeResponseDetails");
		dataAction.setDataObject(response);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

	}
	
	public void saveAmazonPayRefundRequest(MAXAmazonPayResponse response) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXPaytmDataTransaction.saveAmazonPayRefundRequest");

		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SaveAmazonPayRefundResponseDetails");
		dataAction.setDataObject(response);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

	}
	
	public void saveAmazonPayVerifyRequest(MAXAmazonPayResponse response) throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXPaytmDataTransaction.saveAmazonPayVerifyRequest");

		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("SaveAmazonPayVerifyResponseDetails");
		dataAction.setDataObject(response);
		dataActions[0] = dataAction;
		setDataActions(dataActions);

		getDataManager().execute(this);

	}
	
	
}
