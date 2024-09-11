/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
  Rev 1.0	 Prateek		20/05/2013		Initial Draft: Changes for TIC Customer Integration.
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJDBCEncryptLoyaltyNumber extends JdbcDataOperation {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -52907062396413702L;

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJDBCEncryptLoyaltyNumber.class);

	// ---------------------------------------------------------------------
	/**
	 * Class constructor.
	 */
	// ---------------------------------------------------------------------
	public MAXJDBCEncryptLoyaltyNumber() {
		setName("MAXJDBCEncryptLoyaltyNumber");
	}

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJDBCEncryptLoyaltyNumber.execute()");

		/*
		 * getInsertCount() is about the only thing outside of DataConnectionIfc
		 * that we need.
		 */
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		HashMap attributeTobeEncrypted = (HashMap) action.getDataObject();

		HashMap encryptedAttributes = new HashMap();
		try {
			String encryptedvalue = encryptRequestInfo(connection,
					(attributeTobeEncrypted.get(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER)).toString());
			encryptedAttributes.put(MAXLoyaltyConstants.LOYALTY_CARD_NUMBER, encryptedvalue);

		} catch (SQLException e) {
			e.printStackTrace();
		}

		dataTransaction.setResult(encryptedAttributes);

		if (logger.isDebugEnabled())
			logger.debug("MAXJDBCEncryptLoyaltyNumber.execute()");

	}

	protected String encryptRequestInfo(JdbcDataConnection dataConnection, String loyaltyNumber) throws SQLException {
		CallableStatement cs;
		Connection conn = dataConnection.getConnection();
		cs = conn.prepareCall("{? = call POS_ENC(?)}");

		// Register the type of the return value
		cs.registerOutParameter(1, Types.VARCHAR);

		// Set the value for the IN parameter
		cs.setString(2, loyaltyNumber); // provide the value

		// Execute and retrieve the returned value
		cs.execute();
		String retValue = cs.getString(1);
		return retValue;
	}

}
