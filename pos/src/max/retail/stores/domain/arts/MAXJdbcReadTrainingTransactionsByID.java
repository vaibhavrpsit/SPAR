/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *	Rev 1.0		Feb 20,2017			Nadia Arora		Changes for price of item not coming on return
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;


import org.apache.log4j.Logger;

import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcReadTrainingTransactionsByID extends MAXJdbcReadTransaction
{

	   private static Logger logger = Logger.getLogger(MAXJdbcReadTransactionsByID.class);
	 
	   public MAXJdbcReadTrainingTransactionsByID()
	   {
	     setName("MAXJdbcReadTrainingTransactionsByID");
	   }
	 
	   public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
	     throws DataException
	   {
	     if (logger.isDebugEnabled()) logger.debug("JdbcReadTrainingTransactionsByID.execute");
	 
	 
	     JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
	 
	 
	     TransactionIfc transaction = (TransactionIfc)action.getDataObject();
	     LocaleRequestor localeRequestor = getLocaleRequestor(transaction);
	 
	     TransactionIfc[] transactions = readTrainingTransactionsByID(connection, transaction, localeRequestor);
	 
	 
	     dataTransaction.setResult(transactions);
	 
	     if (!(logger.isDebugEnabled())) return; logger.debug("JdbcReadTrainingTransactionsByID.execute");
	   }
	 }