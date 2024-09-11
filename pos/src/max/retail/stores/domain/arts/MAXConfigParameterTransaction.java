/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Copyright (c) 2015 Lifestyle India       All Rights Reserved.
 * Upgraded to ORPOS 14.0.1 from Lifestyle ORPOS 12.0.9IN: AAKASH GUPTA(EYLLP):Aug-17-2015
 *
 * Rev 1.0  23/May/2013	Geetika Chugh     VAT Extra requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import max.retail.stores.domain.utility.MAXConfigParameters;
import max.retail.stores.domain.utility.MAXConfigParametersIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;


/**
 * @author  Geetika Chugh
 *
 */
public class MAXConfigParameterTransaction extends DataTransaction implements DataTransactionIfc {


	/**
	 *
	 */
	private static final long serialVersionUID = 8977946928867932001L;

	protected static String dataCommandName="MAXConfigParameterTransaction";

	private static Logger logger = Logger.getLogger(MAXConfigParameterTransaction.class);

	public MAXConfigParameterTransaction()
	{
		super(dataCommandName);
	}

	/**
	 *
	 * @param inventory
	 * @throws DataException
	 */
	//makes call to jdbc to retreive parameter values from database
	//rev 1.0 change starts
	public MAXConfigParametersIfc selectConfigParameters() throws DataException
	{
		MAXConfigParametersIfc confParameters = new MAXConfigParameters();
		logger.debug("MAXConfigParameterTransaction.selectConfigParameters");
		DataAction dataAction = new DataAction();
		dataAction.setDataOperationName("ConfigParameter");
		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;
		setDataActions(dataActions);
		confParameters = (MAXConfigParameters)getDataManager().execute(this);
		return confParameters;
	}
	//rev 1.0 change ends
}