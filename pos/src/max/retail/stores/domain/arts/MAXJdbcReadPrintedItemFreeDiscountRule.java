/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2013 MAXHyperMarkets, Inc.    All Rights Reserved.
   Rev 1.0	Izhar		29/05/2013		Discount Rule
 
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;

// java imports
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
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

public class MAXJdbcReadPrintedItemFreeDiscountRule extends JdbcDataOperation implements MAXARTSDatabaseIfc {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcReadPrintedItemFreeDiscountRule.class);

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadPrintedItemFreeDiscountRule.execute");

		// set data connection
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		ArrayList al = new ArrayList();
		al = JdbcReadPrintedItemFreeDiscountRule(connection, al);

		// return array
		dataTransaction.setResult(al);
	}

	public ArrayList JdbcReadPrintedItemFreeDiscountRule(JdbcDataConnection dataConnection, ArrayList list)

			throws DataException {
		{
			if (logger.isDebugEnabled())
				logger.debug("MAXJdbcReadPrintedItemFreeDiscountRule.readPrintedItemFreeDiscountRule");

			ResultSet rs = null;
			SQLSelectStatement sql = new SQLSelectStatement();
			ArrayList printedItemFreeDiscountRuleList = null;
			int index = 1;
			// build sql query
			sql.addTable("RU_PRDV");
			sql.addColumn("MO_TH_SRC");
			sql.addColumn("MAX_FREE_ITEM");
			sql.addQualifier("SC_RU_PRDV != 'Expired'");
			sql.addQualifier("cd_bas_prdv = 2");
			sql.addQualifier("MAX_FREE_ITEM IS NOT NULL");
			sql.addQualifier("MAX_FREE_ITEM != 'null'"); // Added by karni
			sql.addQualifier("MO_TH_SRC <> 0");
			sql.addQualifier(currentTimestampRangeCheckingString(FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE,
					FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE));
			sql.addOrdering("MO_TH_SRC");
			try {
				dataConnection.execute(sql.getSQLString());
				rs = (ResultSet) dataConnection.getResult();
				printedItemFreeDiscountRuleList = new ArrayList();
				while (rs.next()) {

					printedItemFreeDiscountRuleList.add(rs.getString(1) + "_" + rs.getString(2));
					// printedItemFreeDiscountRuleList.set(index,
					// rs.getString(2));
					++index;
				}

			} catch (DataException de) {
				logger.warn("" + de + "");
				throw de;
			} catch (SQLException se) {
				dataConnection.logSQLException(se, "JdbcReadPrintedItemFreeDiscountRule");
				throw new DataException(DataException.SQL_ERROR, "JdbcReadPrintedItemFreeDiscountRule", se);
			} catch (Exception e) {
				throw new DataException(DataException.UNKNOWN, "JdbcReadPrintedItemFreeDiscountRule", e);
			}
			rs = (ResultSet) dataConnection.getResult();

			// read locale from database result set

			if (logger.isDebugEnabled())
				logger.debug("JdbcReadPrintedItemFreeDiscountRule");
			return printedItemFreeDiscountRuleList;
		}

		// end initialize()
	}
}