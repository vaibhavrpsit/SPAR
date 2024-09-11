/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 

 *  Rev 1.0     11/03/2015      Akhilesh kumar          		Loyalty Customer
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.domain.arts;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import max.retail.stores.domain.ticcustomer.MAXTICCustomerConfig;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcTICCustomerList extends JdbcDataOperation implements ARTSDatabaseIfc, MAXARTSDatabaseIfc {

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction)
			throws DataException {

		List list = getCustomerConfigList(dataConnection);
		dataTransaction.setResult((Serializable) list);

		// TODO Auto-generated method stub

	}

	public List getCustomerConfigList(DataConnectionIfc connection) throws DataException {

		List ticList = new ArrayList();
		MAXTICCustomerConfig customer = null;
		SQLSelectStatement sql = new SQLSelectStatement();
		// Table
		sql.addTable(TABLE_TIC_CUSTOMER_CONFIG, ALIAS_TIC_CUSTOMER_CONFIG);

		/*
		 * Add columns and their values
		 */
		sql.addColumn(FIELD_VIEW_FLD);
		sql.addColumn(FIELD_MAND_FLD);

		// Extract data from the result set.
		try {
			connection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) connection.getResult();

			if (rs.next()) {
				customer = new MAXTICCustomerConfig();
				if (rs.getString(FIELD_VIEW_FLD) != null) {
					customer.setDisplayField(rs.getString(FIELD_VIEW_FLD));
				}

				if (rs.getString(FIELD_MAND_FLD) != null) {
					customer.setMandatoryField(rs.getString(FIELD_MAND_FLD));
				}
				ticList.add(customer);

			}
		} catch (SQLException e) {
			((JdbcDataConnection) connection).logSQLException(e, "Processing result set.");
			throw new DataException(DataException.SQL_ERROR,
					"An SQL Error occurred proccessing the result set from selecting an employee in JdbcEmployeeLookupOperation.",
					e);
		}

		return ticList;
	}

}
