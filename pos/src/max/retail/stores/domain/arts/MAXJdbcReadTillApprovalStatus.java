/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.1	19/July/2013	Prateek, Changes for Bug 7129
  Rev 1.0	27/May/2013	  	Prateek, Block EOD if till not approved
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.domain.arts;

import java.sql.ResultSet;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcReadTillApprovalStatus extends JdbcDataOperation {

	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadTillApprovalStatus.class);

	public MAXJdbcReadTillApprovalStatus() {
		super();
		setName("MAXJdbcReadTillApprovalStatus");
	}

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		Integer count = fetchTillCount(connection);
		dataTransaction.setResult(count);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadTillApprovalStatus.execute");

	}

	private Integer fetchTillCount(JdbcDataConnection dataConnection) throws DataException {
		/** MAX Rev 1.1 Change : Start **/
		// String sql = "SELECT COUNT(*) FROM AS_TL WHERE MAX_ALTER_TL_RECO=1";
		String sql = "SELECT COUNT(*) FROM AS_TL WHERE MAX_ALTER_TL_RECO=0";
		/** MAX Rev 1.1 Change : End **/
		int count = 0;
		try {
			dataConnection.execute(sql);
			ResultSet rs = (ResultSet) dataConnection.getResult();
			if (rs != null) {
				if (rs.next())
					count = rs.getInt(1);
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return new Integer(count);
	}
}
