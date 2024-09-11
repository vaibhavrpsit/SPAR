/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.util.ArrayList;

import org.apache.log4j.Logger;



import max.retail.stores.domain.MAXUtils.MAXIGSTTax;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXReadTaxOnPLUItem extends DataTransaction implements DataTransactionIfc {
	
	private static final long serialVersionUID = 1L;

	protected static String dataCommandName="MAXReadTaxOnPLUItem";

	private static Logger logger = Logger.getLogger(MAXGSTRegionMappingTransactions.class);
	
	public MAXReadTaxOnPLUItem()
	{
		super(dataCommandName);
	}
	
	public ArrayList<MAXTaxAssignment> readTax(MAXIGSTTax srli) throws DataException
	{
		logger.info("MAXReadTaxOnPLUItem.readTax()");
		DataAction dataAction = new DataAction();
		dataAction.setDataObject(srli);
		dataAction.setDataOperationName("ReadTaxOnPLUItem");

		DataActionIfc[] dataActions = new DataActionIfc[1];
		dataActions[0] = dataAction;
		setDataActions(dataActions);
		ArrayList<MAXTaxAssignment> pluItems = (ArrayList<MAXTaxAssignment>) getDataManager().execute(this);
				
		return pluItems;
	}

}
