/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Prateek		4/June/2013		Initial Draft: Changes for Till Reconcilation FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.arts.AccumulatorTransactionIfc;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXSaveReconcileTenderDetails extends JdbcDataOperation implements MAXARTSDatabaseIfc {

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction)
			throws DataException {
		try {
			saveCashDetails(dataTransaction, dataConnection, dataAction);
			saveCouponTenderDetails(dataTransaction, dataConnection, dataAction);
			saveAcquirerBankDetails(dataTransaction, dataConnection, dataAction);
			saveGiftCertificateDetails(dataTransaction, dataConnection, dataAction);
		} catch (DataException de) {
			logger.error(de);
		}
	}

	protected void saveAcquirerBankDetails(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection,
			DataActionIfc dataAction) throws DataException {
		MAXJdbcSaveAcquirerBankDetails saveBank = new MAXJdbcSaveAcquirerBankDetails();

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		ARTSTill arts = (ARTSTill) dataAction.getDataObject();
		AccumulatorTransactionIfc trans = (AccumulatorTransactionIfc) dataTransaction;
		saveBank.insertBankDetails(connection, arts.getPosTill(), arts.getRegister());
	}

	protected void saveCouponTenderDetails(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection,
			DataActionIfc dataAction) throws DataException {
		MAXJdbcSaveCouponDenomination saveCoupon = new MAXJdbcSaveCouponDenomination();

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		ARTSTill artsTill = (ARTSTill) dataAction.getDataObject();
		AccumulatorTransactionIfc trans = (AccumulatorTransactionIfc) dataTransaction;
		saveCoupon.insertCouponDenomination(connection, artsTill.getPosTill(), artsTill.getRegister());
	}

	protected void saveGiftCertificateDetails(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection,
			DataActionIfc dataAction) throws DataException {
		MAXJdbcSaveGCDenomination saveGC = new MAXJdbcSaveGCDenomination();

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		ARTSTill artsTill = (ARTSTill) dataAction.getDataObject();
		AccumulatorTransactionIfc trans = (AccumulatorTransactionIfc) dataTransaction;
		saveGC.insertGCData(connection, artsTill.getPosTill(), artsTill.getRegister());
	}

	protected void saveCashDetails(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection,
			DataActionIfc dataAction) throws DataException {
		MAXJdbcSaveCashDenomination saveGC = new MAXJdbcSaveCashDenomination();

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		ARTSTill artsTill = (ARTSTill) dataAction.getDataObject();
		AccumulatorTransactionIfc trans = (AccumulatorTransactionIfc) dataTransaction;
		saveGC.insertCashData(connection, artsTill.getPosTill(), artsTill.getRegister());
	}
}
