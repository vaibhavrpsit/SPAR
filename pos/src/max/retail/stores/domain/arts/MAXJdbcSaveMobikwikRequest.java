package max.retail.stores.domain.arts;

import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.loyalty.MAXLoyaltyConstants;
import max.retail.stores.domain.mobikwik.MaxMobikwikConstant;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcSaveMobikwikRequest  extends JdbcDataOperation implements MAXARTSDatabaseIfc 
{
	
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcSaveMobikwikRequest.class);


	@Override
	public void execute(DataTransactionIfc paramDataTransactionIfc,
			DataConnectionIfc dataConnection,
			DataActionIfc action) throws DataException
			{
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveMobikwikRequest.execute()");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

		HashMap requestAttributes = (HashMap) action.getDataObject();

		saveMobikwikRequestAttribute(connection, requestAttributes);

		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveMobikwikRequest.execute");

		
	}
	
	protected void saveMobikwikRequestAttribute(JdbcDataConnection connection, HashMap requestAttributes) throws DataException 
	{
		try {
			insertMobikwikDetails(connection, requestAttributes);
		} catch (Exception e) 
		{
			e.printStackTrace();
			//throw new DataException(DataException.UNKNOWN);
		}

	}

	private void insertMobikwikDetails(JdbcDataConnection connection,
			HashMap requestAttributes) throws DataException
	{
		SQLInsertStatement sql = new SQLInsertStatement();
	try
	{
		sql.setTable(TABLE_MOBIKWIK_WEB_REQUEST_LOG);
		sql.addColumn(FIELD_MOBIKWIK_MESSAGE_ID, String.valueOf(requestAttributes.get(MaxMobikwikConstant.MESSAGE_ID)));
		sql.addColumn(FIELD_MOBIKWIK_REQUEST_TYPE_A,
				makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.REQUEST_TYPE_A)));
		sql.addColumn(FIELD_MOBIKWIK_REQUEST_STATUS,
				makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.REQUEST_STATUS)));

		// Checking condition for Time-out case
	
		if (requestAttributes.get(MaxMobikwikConstant.TIME_OUT_REQUEST_MESSAGE_ID) != null) {
			sql.addColumn(FIELD_MOBIKWIK_ID_MESSAGE_TYPMEOUT,
					String.valueOf(requestAttributes.get(MaxMobikwikConstant.TIME_OUT_REQUEST_MESSAGE_ID)));
		}
		sql.addColumn(FIELD_MOBIKWIK_STORE_ID, makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.STORE_ID)));
		sql.addColumn(FIELD_MOBIKWIK_TILL_ID, makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.TILL_ID)));
		sql.addColumn(FIELD_REGISTER_ID,
				makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.REGISTER_ID)));
		sql.addColumn(FIELD_MOBIKWIK_INVOICE_BUSINESS_DATE,
				getBusinessDayString((EYSDate) requestAttributes.get(MaxMobikwikConstant.INVOICE_BUSINESS_DATE)));
		sql.addColumn(FIELD_MOBIKWIK_INVOICE_NUMBER, String.valueOf(requestAttributes.get(MaxMobikwikConstant.INVOICE_NUMBER)));
		sql.addColumn(FIELD_MOBIKWIK_PHONE_NUMBER,
				makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.MOBIKWIK_PHONE_NUMBER)));
		sql.addColumn(FIELD_MOBIKWIK_TRANS_TOTAL_AMT,
				(String) requestAttributes.get(MaxMobikwikConstant.TRAN_TOTAL_AMOUNT).toString());
		sql.addColumn(FIELD_MOBIKWIK_SETTLE_TOTAL_AMT,
				(String) requestAttributes.get(MaxMobikwikConstant.SETTLE_TOTAL_AMOUNT).toString());
		if (requestAttributes.get(MaxMobikwikConstant.REQUEST_TYPE_B) != null) {
			sql.addColumn(FIELD_MOBIKWIK_REQUEST_TYPE_B,
					makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.REQUEST_TYPE_B)));
		}
		sql.addColumn(FIELD_MOBIKWIK_REQUEST_DATE, getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_MOBIKWIK_REQUEST_TIME_OUT,
				(String) requestAttributes.get(MaxMobikwikConstant.REQUEST_TIME_OUT).toString());
		sql.addColumn(FIELD_MOBIKWIK_REQUEST_URL,
				makeSafeString((String) requestAttributes.get(MaxMobikwikConstant.REQUEST_URL)));
	}catch(Exception e)
	{
		System.out.println(e.getMessage());
		e.printStackTrace();
	}
				

		try
		{
			System.out.println(sql.getSQLString().toString());
			connection.execute(sql.getSQLString());
			System.out.println(sql.getSQLString().toString());
		} catch (DataException de)
		{
			logger.error("" + de + "");
			throw de;
		} catch (Exception e) 
		{
			logger.error("" + "");
			throw new DataException(DataException.UNKNOWN);
		}
	}

	public String getBusinessDayString(EYSDate date)
	{
		return (dateToSQLDateString(date));
	}
}
		
	



