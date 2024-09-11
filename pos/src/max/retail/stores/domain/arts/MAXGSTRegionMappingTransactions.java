/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.utility.MAXGSTRegion;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXGSTRegionMappingTransactions extends DataTransaction implements DataTransactionIfc{
	
	private static final long serialVersionUID = 1L;

	protected static String dataCommandName="MAXGSTRegionMappingTransactions";

	private static Logger logger = Logger.getLogger(MAXGSTRegionMappingTransactions.class);
	
	public MAXGSTRegionMappingTransactions()
	{
		super(dataCommandName);
	}
	public HashMap<Integer,MAXGSTRegion> readRegionFromMaping() throws DataException
	{
		logger.debug("MAXGSTRegionMappingTransactions.readRegionFromMaping");
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("GSTRegionMapping");
		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;
		setDataActions(dataActions);
		HashMap<Integer,MAXGSTRegion> gstRegionCode = (HashMap<Integer,MAXGSTRegion>)getDataManager().execute(this);		
		return gstRegionCode;
	}

}
