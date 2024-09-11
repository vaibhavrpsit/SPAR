package max.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcReadParking extends JdbcDataOperation implements ARTSDatabaseIfc {
	/**
	 *  
	 */
	private static final long serialVersionUID = 4605746138074257057L;

	public MAXJdbcReadParking() {
		super();
		setName("MAXJdbcReadParking");
		//System.out.println("MAXJdbcReadMallCrtfTransactionRead 37");
	}

	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
			throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadParking.execute");
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		int output = 0;
		TenderableTransactionIfc trans = null;
//String date=null;
		trans = (TenderableTransactionIfc) action.getDataObject();
// date = (String) action.getDataObject();
		output = readcustomernewinformation(connection, trans);
		dataTransaction.setResult(output);
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadMallCrtfTransactionRead.execute");
	}

	public int readcustomernewinformation(JdbcDataConnection dataConnection, TenderableTransactionIfc trans)
			throws DataException {
		//System.out.println("MAXJdbcReadMallCrtfTransactionRead 63");
		SQLSelectStatement sql = new SQLSelectStatement();
		int cstmrdetails1 = 0;
		//System.out.println("hgffgfghghg");
		SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
		String businessDate = format.format(trans.getBusinessDay().dateValue());
		String Mob_number = trans.getCustomerInfo().getPhoneNumber().getPhoneNumber();
//DATE=trans.getBusinessDay().toString();
		String sqls = "select count(*)from (select t.inf_ct Mob_number,t.id_str_rt store_code,"
				+ "t.dc_dy_bsn biz_date,t.id_str_rt||t.id_Ws||t.ai_trn TRANSACTION_NO, s.uc_cpn_sc tender_type,"
				+ "s.AI_LN_ITM from tr_trn t,tr_rtl r,tr_itm_cpn_tnd s where t.dc_dy_bsn=r.dc_dy_bsn and"
				+ " r.dc_dy_bsn=s.dc_dy_bsn and t.ai_Trn=r.ai_Trn and r.ai_trn=s.ai_trn and t.fl_trg_trn='0'"
				+ " and t.dc_dy_bsn='" + businessDate + "' and t.inf_ct='" + Mob_number + "'  and s.uc_cpn_sc ='Parking' and "
				+ "t.sc_Trn='2' and t.ty_trn in (1,2))";
		try {
			dataConnection.execute(sqls);
			ResultSet rs = (ResultSet) dataConnection.getResult();
			while (rs.next()) {
				cstmrdetails1 = rs.getInt(1);
				System.out.println("=============" + cstmrdetails1);
			}
			rs.close();
		} catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			logger.warn("" + se + "");
			throw new DataException(DataException.SQL_ERROR, "transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table", e);
		}
		return cstmrdetails1;
	}
}
