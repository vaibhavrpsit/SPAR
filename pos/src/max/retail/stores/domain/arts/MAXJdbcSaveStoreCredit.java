/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   Copyright (c) 2012 - 2013 MAXHyperMarket, Inc.    All Rights Reserved.
   Rev 1.0  25/05/2013	Tanmaya		Initial Draft: Changes for Store Credit
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.domain.utility.MAXStoreCreditIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.ARTSTransaction;
import oracle.retail.stores.domain.arts.JdbcSaveStoreCredit;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.AbstractTenderDocumentIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcSaveStoreCredit extends JdbcSaveStoreCredit {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8166635022984706833L;
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveStoreCredit.class);

	@Override
	protected void insertStoreCreditDocument(JdbcDataConnection connection, StoreCreditIfc sc) throws DataException {
		// Put away the store credit record
		// Define the table
		
		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(TABLE_STORE_CREDIT);

		// Add columns and their values and qualifiers
		sql.addColumn(FIELD_STORE_CREDIT_ID, makeSafeString(sc.getStoreCreditID()));
		sql.addColumn(FIELD_STORE_CREDIT_BALANCE, sc.getAmount().getDecimalValue().toString());// toDecimalFormattedString());

		if (sc.getExpirationDate() == null) {
			sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE, null);
		} else {
			sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE, dateToSQLDateFunction(sc.getExpirationDate()));
		}
		sql.addColumn(FIELD_STORE_CREDIT_STATUS, makeSafeString(sc.getStatus()));
		if (sc.getFirstName() == null || sc.getFirstName().equals(""))
			sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME, makeSafeString("FirstName"));
		else
			sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME, makeSafeString(sc.getFirstName()));
		if (sc.getLastName() == null || sc.getLastName().equals(""))
			sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME, makeSafeString("LastName"));
		else
			sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME, makeSafeString(sc.getLastName()));
		sql.addColumn(FIELD_STORE_CREDIT_ID_TYPE, makeSafeString("PAN Card"));
		sql.addColumn(FIELD_STORE_CREDIT_TRAINING_FLAG, ((sc.isTrainingMode()) ? "'1'" : "'0'"));
		// +I18N
		sql.addColumn(FIELD_CURRENCY_ID, sc.getAmount().getType().getCurrencyId());
		// -I18N
		
		//Change for Rev 1.1: Start	
		if(sc instanceof MAXStoreCreditIfc)
		{   if(((MAXStoreCreditIfc) sc).getSCmobileNumber()!=null)
			sql.addColumn("CUST_MOBILE_NUM", makeSafeString(((MAXStoreCreditIfc) sc).getSCmobileNumber()));
		}
		//Change for Rev 1.1:End
		

		connection.execute(sql.getSQLString());

		if (connection.getUpdateCount() < 1) {
			logger.error("Store Credit Insert count was not greater than 0");
			throw new DataException(DataException.UNKNOWN, "Store Credit Insert count was not greater than 0");
		}

		//return (new Integer(connection.getUpdateCount()));
	}

	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled()) {
			logger.debug("JdbcSaveStoreCredit.execute()");
		}

		ARTSTransaction trans = (ARTSTransaction) action.getDataObject();
		TenderableTransactionIfc transaction = (TenderableTransactionIfc) trans.getPosTransaction();
		logger.info(" (execute) Before EWallet Tender");
		logger.info(" (execute) Condition Value:- "+!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag());
		if(!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag())
		{
			logger.info(" (execute) Inside EWallet Tender");
		TenderLineItemIfc[] tenderLineItems = transaction.getTenderLineItems();
		TenderLineItemIfc[] arr$ = tenderLineItems;
		int len$ = tenderLineItems.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			TenderLineItemIfc lineItem = arr$[i$];
			if (lineItem instanceof MAXTenderStoreCreditIfc) {
				this.saveStoreCredit((JdbcDataConnection) dataConnection, transaction, (TenderStoreCreditIfc) lineItem);
			}
		}

		if (transaction instanceof RedeemTransactionIfc) {
			RedeemTransactionIfc rTrans = (RedeemTransactionIfc) transaction;
			if (rTrans.getRedeemTender() instanceof TenderStoreCreditIfc) {
				this.saveStoreCredit((JdbcDataConnection) dataConnection, transaction,
						(TenderStoreCreditIfc) rTrans.getRedeemTender());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("JdbcSaveStoreCredit.execute()");
		}

	}
	}
	
	@Override
	protected void saveStoreCredit(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
            TenderStoreCreditIfc lineItem) throws DataException
    {
		logger.info(" (saveStoreCredit) Before EWallet Tender");
		logger.info(" (saveStoreCredit) Condition Value:- "+!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag());
		if(!((MAXSaleReturnTransaction)transaction).isEWalletTenderFlag())
		{
			logger.info(" (saveStoreCredit) Inside EWallet Tender");
		
        StoreCreditIfc document = ((TenderStoreCreditIfc)lineItem).getStoreCredit();
        if (transaction instanceof VoidTransactionIfc)
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.VOIDED);
            document.setVoidDate(new EYSDate());
        }
        else
        if (lineItem.getAmountTender().signum() < 0)
        {
            document.setStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setIssueDate(new EYSDate());
        }
        else
        {
            document.setPreviousStatus(AbstractTenderDocumentIfc.ISSUED);
            document.setStatus(AbstractTenderDocumentIfc.REDEEMED);
            document.setRedeemDate(new EYSDate());
        }
        
        if (updateStoreCredit(dataConnection, document) < 1)
        {
            insertStoreCreditDocument(dataConnection, document);
        }
		}
    }
}
