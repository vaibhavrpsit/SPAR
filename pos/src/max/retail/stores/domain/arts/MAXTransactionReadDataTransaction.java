/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012 - 2013 MAX, Inc.    All Rights Reserved.
  Rev 1.1	27/May/2013	  	Prateek, Block EOD if till not approved
  Rev 1.0	5/04/2013	Prateek	 		Initial Draft: Changes for Suspended Bills FES.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

public class MAXTransactionReadDataTransaction extends TransactionReadDataTransaction {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXTransactionReadDataTransaction.class);

	public MAXTransactionReadDataTransaction() {
		super();
	}

	public MAXTransactionReadDataTransaction(String name) {
		super(name);
	}

	public int getSuspendedTransactionCount(TransactionIfc transaction) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadDataTransaction.getSuspendedTransactionCount");

		int returnValue = 0;

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("ReadSuspendTransactionByStatus");
		da.setDataObject(transaction);
		dataActions[0] = da;
		setDataActions(dataActions);

		Integer counts = (Integer) getDataManager().execute(this);
		returnValue = counts.intValue();

		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadDataTransaction.getSuspendedTransactionCount");

		return (returnValue);
	}
	
	public BigDecimal getCashTransactionForCustomer(String mobile,String date) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadDataTransaction.getCashTransactionForCustomer");

		BigDecimal returnValue;

		//changes by Shyvanshu Mehra
		
		
		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("ReadTransactionForCustomer");
		da.setDataObject(mobile);
		da.setDataObject(date);
		dataActions[0] = da;
		setDataActions(dataActions);

		BigDecimal sumTotal = (BigDecimal) getDataManager().execute(this);
		returnValue = sumTotal;

		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadDataTransaction.getCashTransactionForCustomer");

		return (returnValue);
	}
	

	/** MAX Rev 1.1 Change: Start **/
	public int getUnapproveTillReconcileCount() throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadDataTransaction.getUnapproveTillReconcileCount");

		int returnValue = 0;

		// set data actions and execute
		DataActionIfc[] dataActions = new DataActionIfc[1];
		DataAction da = new DataAction();
		da.setDataOperationName("UnapproveTillReconcileCount");
		da.setDataObject(new Integer(0));
		dataActions[0] = da;
		setDataActions(dataActions);

		Integer counts = (Integer) getDataManager().execute(this);
		returnValue = counts.intValue();

		if (logger.isDebugEnabled())
			logger.debug("MAXTransactionReadDataTransaction.getUnapproveTillReconcileCount");

		return (returnValue);
	}
	/** MAX Rev 1.1. Change : End **/
}
