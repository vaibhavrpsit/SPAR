/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXReadHomeStateTransactions extends DataTransaction implements DataTransactionIfc{
	
	private static final long serialVersionUID = 1L;

	protected static String dataCommandName="MAXReadHomeStateTransactions";

	private static Logger logger = Logger.getLogger(MAXGSTRegionMappingTransactions.class);
	
	public MAXReadHomeStateTransactions()
	{
		super(dataCommandName);
	}
	public String readHomeState(String storeID) throws DataException
	{
		logger.debug("MAXReadHomeStateTransactions.readHomeState");
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("GSTHomeState");
		dataAction.setDataObject(storeID);
		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;
		setDataActions(dataActions);	
		return (String)(getDataManager().execute(this));
	}

}
