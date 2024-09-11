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

public class MAXJdbcReadSrcandTgtDiscountPerc extends JdbcDataOperation implements MAXARTSDatabaseIfc {
	/**
	 * The logger to which log messages will be sent.
	 **/
	private static Logger logger = Logger
			.getLogger(max.retail.stores.domain.arts.MAXJdbcReadSrcandTgtDiscountPerc.class);

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadSrcandTgtDiscountPerc.execute");

		// set data connection
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		// action.getDataObject().toString();
		// ArrayList al = new ArrayList();
		String s = null;
		s = readSrcandTgtDiscountPerc(connection, action.getDataObject().toString());

		// return array
		dataTransaction.setResult(s);
	}

	public String readSrcandTgtDiscountPerc(JdbcDataConnection dataConnection, String promoID)

			throws DataException {
		{
			if (logger.isDebugEnabled())
				logger.debug("MAXJdbcReadSrcandTgtDiscountPerc.readSrcandTgtDiscountPerc");

			ResultSet rs = null;
			SQLSelectStatement sql = new SQLSelectStatement();
			String readSrcandTgtDiscountPercList = null;
			int index = 1;
			// build sql query
			sql.addTable("ru_prdv");
			sql.addColumn("max_vendor_src_prcnt");
			sql.addColumn("max_vendor_trgt_prcnt");
			sql.addColumn("MAX_VENDOR_ID");
			sql.addColumn("MAX_VENDOR_CONT_PRCNT");
			sql.addColumn("MAX_VENDOR_CONT_AMT");
			sql.addColumn("MAX_VENDOR_FUNDED");
			// removed single qoutes as it was crashing in offline mode
			sql.addQualifier("ID_RU_PRDV=" + "" + promoID + "");

			try {
				dataConnection.execute(sql.getSQLString());
				rs = (ResultSet) dataConnection.getResult();
				// readSrcandTgtDiscountPercList = new ArrayList();
				while (rs.next()) {

					readSrcandTgtDiscountPercList = rs.getString(1) + "_" + rs.getString(2) + "_" + promoID + "_"
							+ rs.getString(3) + "_" + rs.getString(4) + "_" + rs.getString(5) + "_" + rs.getString(6);
					// printedItemFreeDiscountRuleList.set(index,
					// rs.getString(2));
				}

			} catch (DataException de) {
				logger.warn("" + de + "");
				throw de;
			} catch (SQLException se) {
				dataConnection.logSQLException(se, "MAXJdbcReadSrcandTgtDiscountPerc");
				throw new DataException(DataException.SQL_ERROR, "MAXJdbcReadSrcandTgtDiscountPerc", se);
			} catch (Exception e) {
				throw new DataException(DataException.UNKNOWN, "MAXJdbcReadSrcandTgtDiscountPerc", e);
			}
			rs = (ResultSet) dataConnection.getResult();

			// read locale from database result set

			if (logger.isDebugEnabled())
				logger.debug("MAXJdbcReadSrcandTgtDiscountPerc");
			return readSrcandTgtDiscountPercList;
		}

		// end initialize()
	}
}