/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved. 
 *
 *  Rev 1.0    Sep 12, 2022		Kamlesh Pant	CapLimit Enforcement for Liquor
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import max.retail.stores.domain.MaxLiquorDetails;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcLiquorUMAndCategoryLookup extends JdbcDataOperation implements ARTSDatabaseIfc  {

	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action) throws DataException {
		
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcLiquorUMAndCategoryLookup.execute");
		
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		MaxLiquorDetails output =null;
		// send back the selected data (or lack thereof)
		String itemID = null;
		if ((action.getDataObject()) instanceof String) {
			String itemId = (String) action.getDataObject();
			output=readItemIdFromHotKey(connection, itemId);
			

		}

		dataTransaction.setResult(output);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcLiquorUMAndCategoryLookup.execute");
	}
	
	public MaxLiquorDetails readItemIdFromHotKey(JdbcDataConnection dataConnection, String itemId) throws DataException {
		SQLSelectStatement sql = new SQLSelectStatement();
		
		MaxLiquorDetails liqrdetails=new MaxLiquorDetails();
		sql.addTable(TABLE_ITEM, ALIAS_ITEM);

		// add columns
		sql.addColumn(ALIAS_ITEM + "." + FIELD_MERCHANDISE_CLASSIFICATION_CODE + "7");
		sql.addColumn(ALIAS_ITEM + "." + FIELD_MERCHANDISE_CLASSIFICATION_CODE + "8");
		sql.addColumn(ALIAS_ITEM + "." + FIELD_POS_DEPARTMENT_ID);
		sql.addColumn(ALIAS_ITEM + "." + FIELD_ITEM_ID);

		// add qualifiers
		sql.addQualifier(ALIAS_ITEM + "." + FIELD_ITEM_ID +"= " + makeSafeString(itemId));

		//System.out.println("Query 57"+sql.getSQLString());
		
		try {
			dataConnection.execute(sql.getSQLString());

			ResultSet rs = (ResultSet) dataConnection.getResult();

			while (rs.next()) {

				liqrdetails.setLiqUMinLtr(rs.getNString(1)); 
		
				liqrdetails.setLiquorCategory(rs.getNString(2));
				liqrdetails.setDepartment(rs.getNString(3));
				liqrdetails.setItem(rs.getNString(4));
				

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

		return liqrdetails;
	}
	
	
}
