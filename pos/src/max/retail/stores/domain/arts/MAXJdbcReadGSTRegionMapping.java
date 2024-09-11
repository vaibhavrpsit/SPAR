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
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.utility.MAXGSTRegion;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcReadGSTRegionMapping  extends JdbcDataOperation implements MAXARTSDatabaseIfc {

	private static final long serialVersionUID = 1L;
	/* 
	 * @Override by Kritica.Agarwal
	 * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
	 */
	private static Logger logger =
			Logger.getLogger(
					max.retail.stores.domain.arts.MAXJdbcReadGSTRegionMapping.class);
	@Override
	public void execute(DataTransactionIfc transaction,
			DataConnectionIfc connection,
			DataActionIfc action) throws DataException {
		JdbcDataConnection dataConnection = (JdbcDataConnection)connection;
		HashMap<Integer,MAXGSTRegion> gstRegion = readGSTRegionMapping(dataConnection);
		transaction.setResult(gstRegion);	
		
	}
	
	protected HashMap<Integer,MAXGSTRegion> readGSTRegionMapping(DataConnectionIfc connection) throws DataException{

		
		HashMap<Integer,MAXGSTRegion> regMapping = new HashMap<Integer,MAXGSTRegion>();
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable( TABLE_GST_REG_MAP, ALIAS_GST_REG_MAP);
		/*
		 * Add columns and their values
		 */
		sql.addColumn(ALIAS_GST_REG_MAP + "." + FIELD_REGION_CODE);
		sql.addColumn(ALIAS_GST_REG_MAP + "." + FIELD_REGION_DESC);
		
		try
		{
			connection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) connection.getResult();
			int count=0;
			while (rs.next())
			{
				count++;
				MAXGSTRegion gstRegion = new MAXGSTRegion();
				gstRegion.setRegionCode(getSafeString(rs,1));
				gstRegion.setRegionDesc(getSafeString(rs,2));
				regMapping.put(count, gstRegion);
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

		//gstRegion.setGstRegion(regMapping);
		return regMapping;
	}

}
