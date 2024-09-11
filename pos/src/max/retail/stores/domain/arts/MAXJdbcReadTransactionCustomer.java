/*Changes by Shyvanshu Mehra*/

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
public class MAXJdbcReadTransactionCustomer extends JdbcDataOperation implements MAXARTSDatabaseIfc, MAXCodeConstantsIfc{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4605746138074257057L;
	
	public MAXJdbcReadTransactionCustomer() {
		super();
		setName("MAXJdbcReadTransactionCustomer");
		//System.out.println("MAXJdbcReadTransactionCustomer 27");
		
		
	}

	
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action) throws DataException {
		
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadTransactionCustomer.execute");
		
		//System.out.println("MAXJdbcReadTransactionCustomer 40");
		
		//logger.warn("dffjdffjkjnwkjv");
		
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		
		BigDecimal output =null;
		TenderableTransactionIfc trans = null;
		//String date=null;
		if ((action.getDataObject()) instanceof TenderableTransactionIfc) {
			trans = (TenderableTransactionIfc) action.getDataObject();
			// date = (String) action.getDataObject();
			output=readcustomerinformation(connection, trans);
			

		}

		dataTransaction.setResult(output);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcReadTransactionCustomer.execute");
	}
	
	public BigDecimal readcustomerinformation(JdbcDataConnection dataConnection, TenderableTransactionIfc trans) throws DataException {
		
		//System.out.println("MAXJdbcReadTransactionCustomer 63");
		SQLSelectStatement sql = new SQLSelectStatement();
		BigDecimal cstmrdetails1=new BigDecimal(0.0);
		
		//System.out.println("hgffgfghghg");
		//MAXCustomerDetails cstmrdetails=new MAXCustomerDetails();
		/*sql.addTable(TABLE_TRANSACTION, ALIAS_ITEM);

		// add columns
		sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID );
		sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID );
		sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
		sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);

		// add qualifiers
		sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_CUSTOMER_INFO +"= " + makeSafeString(mobile));
		sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE +"= " + makeSafeString(date));*/ 
		 SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
         String businessDate = format.format(trans.getBusinessDay().dateValue());
		String Mob_number=trans.getCustomerInfo().getPhoneNumber().getPhoneNumber();
		//DATE=trans.getBusinessDay().toString();
		String sqls="select sum(mo_itm_ln_tnd) from tr_trn  join  tr_ltm_tnd on tr_trn.ai_Trn=tr_ltm_tnd.ai_trn and " + 
				"tr_Trn.dc_Dy_bsn=tr_ltm_tnd.dc_Dy_bsn and tr_Trn.id_Ws=tr_ltm_tnd.id_Ws where tr_Trn.sc_trn=2 and tr_trn.ty_trn in (1,2) and tr_ltm_tnd.ty_Tnd='CASH' " + 
				"and tr_trn.fl_tre_trn !='1' and tr_trn.fl_trg_trn<>'1' and tr_trn.inf_ct='"+Mob_number+"' and tr_trn.dc_Dy_bsn='"+businessDate+"' group by tr_trn.inf_ct,tr_trn.dc_dy_bsn";
		//System.out.println("hgffgfghghg============ "+sqls);
		try
		{
			dataConnection.execute(sqls);
			ResultSet rs = (ResultSet) dataConnection.getResult();
			
		while (rs.next())
			{
			cstmrdetails1=rs.getBigDecimal(1);
			//System.out.println("============="+rs.getBigDecimal(1));
				
			}
			rs.close();
		}catch (DataException de) {
			//logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			//logger.warn("" + se + "");
			throw new DataException(DataException.SQL_ERROR, "transaction table", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "transaction table", e);
		}
		return cstmrdetails1;
	}
       	
	

}
