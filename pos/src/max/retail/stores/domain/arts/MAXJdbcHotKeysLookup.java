/* ===========================================================================
R* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/max/retail/stores/domain/arts/MAXJdbcHotKeysLookup.java /main/32 2014/06/17 15:26:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * Rev 1.0	Aug 26,2016		Nitesh Kumar	changes for code merging 
 * ===========================================================================
 */
package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcHotKeysLookup extends JdbcDataOperation implements ARTSDatabaseIfc {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7425209937691177210L;

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcHotKeyLookup.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// send back the selected data (or lack thereof)
		String itemID = null;
		if ((action.getDataObject()) instanceof String) {
			String hotKey = (String) action.getDataObject();
			itemID = readItemIdFromHotKey(connection, hotKey);

		}

		dataTransaction.setResult(itemID);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcHotKeyLookup.execute");

	}

	public String readItemIdFromHotKey(JdbcDataConnection dataConnection, String hotKey) throws DataException {
		SQLSelectStatement sql = new SQLSelectStatement();

		// add tables
		sql.addTable(TABLE_ITEM, ALIAS_ITEM);

		// add columns
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_ID);

		// add qualifiers
		sql.addQualifier(ALIAS_ITEM + "." + FIELD_MERCHANDISE_CLASSIFICATION_CODE + "0 = " + makeSafeString(hotKey));

		String itemId = "";
		try {
			dataConnection.execute(sql.getSQLString());

			ResultSet rs = (ResultSet) dataConnection.getResult();

			while (rs.next()) {

				itemId = itemId + getSafeString(rs, 1) + ";";

			}

			rs.close();
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			dataConnection.logSQLException(se, "PLUItem lookup");
			throw new DataException(DataException.SQL_ERROR, "PLUItem lookup", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "PLUItem lookup", e);
		}

		return itemId;
	}

}
