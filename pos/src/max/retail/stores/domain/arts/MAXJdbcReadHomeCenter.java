/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcReadHomeCenter extends JdbcDataOperation implements MAXARTSDatabaseIfc {

	private static final long serialVersionUID = 1L;
	/* 
	 * @Override by Kritica.Agarwal
	 * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
	 */
	private static Logger logger =
			Logger.getLogger(
					max.retail.stores.domain.arts.MAXJdbcReadHomeCenter.class);
	@Override
	public void execute(DataTransactionIfc transaction,
			DataConnectionIfc connection,
			DataActionIfc action) throws DataException {
		JdbcDataConnection dataConnection = (JdbcDataConnection)connection;
		String storeId = (String) action.getDataObject();
		
		transaction.setResult(readHomeStore(dataConnection,storeId));
		
		
	}
	protected String readHomeStore(DataConnectionIfc connection, String storeId)throws DataException{
		String homeState = null;
	
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable( TABLE_STORE_REGIONS, ALIAS_STORE_SAFE);
		/*
		 * Add columns and their values
		 */
		sql.addColumn(ALIAS_STORE_SAFE + "." + FIELD_STORE_REGION_NAME);
		// add joins
		sql.addOuterJoinQualifier(" JOIN " + TABLE_RETAIL_STORE + " "
				+ ALIAS_RETAIL_STORE_ITEM + " ON " + ALIAS_RETAIL_STORE_ITEM
				+ "." + FIELD_STORE_REGION_ID + " = " + ALIAS_STORE_SAFE + "."
				+ FIELD_STORE_REGION_ID);
		// add Qualifier
		sql.addQualifier(ALIAS_RETAIL_STORE_ITEM + "." + FIELD_RETAIL_STORE_ID
				+ " = '" + storeId + "'");
		
		try
		{
			connection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) connection.getResult();
			//int count=0;
			while (rs.next()){
				homeState=getSafeString(rs,1);
				homeState=homeState.replaceAll("\\s+","");
			}
			rs.close();		

		}catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			logger.warn("" + se + "");
			throw new DataException(DataException.SQL_ERROR, "transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table", e);
		}
		return homeState;
	}
}
