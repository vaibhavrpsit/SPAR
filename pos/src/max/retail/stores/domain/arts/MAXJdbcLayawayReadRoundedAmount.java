/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   Rev 1.0	Izhar		29/05/2013		Discount Rule
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

// java imports
import java.sql.ResultSet;
import java.sql.SQLException;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

//-------------------------------------------------------------------------
/**
 * This operation takes a POS domain Customer and creates a new entry in the
 * database.
 * <P>
 * 
 * @version $Revision: 6$
 **/
// -------------------------------------------------------------------------

public class MAXJdbcLayawayReadRoundedAmount extends JdbcDataOperation implements MAXARTSDatabaseIfc {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcLayawayReadRoundedAmount.class);

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcLayawayReadRoundedAmount.execute");

		// set data connection
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		LayawayIfc inputLayaway = (LayawayIfc) action.getDataObject();
		String s;
		// ArrayList al = new ArrayList();
		s = maxLayawayReadRoundedAmountDataTransaction(connection, inputLayaway);

		// return array
		dataTransaction.setResult(s);
	}

	public String maxLayawayReadRoundedAmountDataTransaction(JdbcDataConnection dataConnection, LayawayIfc layaway)

			throws DataException {
		{
			if (logger.isDebugEnabled())
				logger.debug("MAXJdbcLayawayReadRoundedAmount.maxLayawayReadRoundedAmountDataTransaction");

			ResultSet rs = null;
			SQLSelectStatement sql = new SQLSelectStatement();
			String roundedAmount = null;
			// build sql query
			sql.addTable("TR_RTL");
			sql.addColumn("MO_OFF_TOT");
			sql.addQualifier("ID_LY=" + makeSafeString(layaway.getLayawayID()));
			sql.addQualifier("ID_WS=" + makeSafeString(layaway.getInitialTransactionID().getWorkstationID()));
			sql.addQualifier("DC_DY_BSN=" + dateToSQLDateString(layaway.getInitialTransactionBusinessDate()));
			sql.addQualifier("AI_TRN=" + Long.toString(layaway.getInitialTransactionID().getSequenceNumber()));
			try {
				dataConnection.execute(sql.getSQLString());
				rs = (ResultSet) dataConnection.getResult();
				while (rs.next()) {

					roundedAmount = rs.getString(1);
				}

			} catch (DataException de) {
				logger.warn("" + de + "");
				throw de;
			} catch (SQLException se) {
				dataConnection.logSQLException(se,
						"MAXJdbcLayawayReadRoundedAmount.maxLayawayReadRoundedAmountDataTransaction");
				throw new DataException(DataException.SQL_ERROR,
						"MAXJdbcLayawayReadRoundedAmount.maxLayawayReadRoundedAmountDataTransaction", se);
			} catch (Exception e) {
				throw new DataException(DataException.UNKNOWN,
						"MAXJdbcLayawayReadRoundedAmount.maxLayawayReadRoundedAmountDataTransaction", e);
			}
			rs = (ResultSet) dataConnection.getResult();

			// read locale from database result set

			if (logger.isDebugEnabled())
				logger.debug("MAXJdbcLayawayReadRoundedAmount.maxLayawayReadRoundedAmountDataTransaction");
			return roundedAmount;
		}

		// end initialize()
	}
}