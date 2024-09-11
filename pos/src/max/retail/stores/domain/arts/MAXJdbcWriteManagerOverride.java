
package max.retail.stores.domain.arts;

//Changes to capture ManagerOverride for Reporting purpose



import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import max.retail.stores.domain.manageroverride.MAXManagerOverride;


public class MAXJdbcWriteManagerOverride extends JdbcDataOperation implements
		MAXARTSDatabaseIfc, MAXCodeConstantsIfc {

	/**
	 * 
	 * 
	 */
	private static final long serialVersionUID = -4806979289386966392L;

	public MAXJdbcWriteManagerOverride() {
		super();
		setName("MAXJdbcWriteManagerOverride");
		//System.out.println("Going inside MAXJdbcWriteManagerOverride");
		
		
	}

	/* (non-Javadoc)
	 * @see com.extendyourstore.foundation.manager.ifc.data.DataOperationIfc#execute(com.extendyourstore.foundation.manager.ifc.data.DataTransactionIfc, com.extendyourstore.foundation.manager.ifc.data.DataConnectionIfc, com.extendyourstore.foundation.manager.ifc.data.DataActionIfc)
	 */
	public void execute(DataTransactionIfc dt, DataConnectionIfc dc,
			DataActionIfc da) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcWriteManagerOverride.execute");
		//System.out.println("Again Going inside MAXJdbcWriteManagerOverride");
		
		/*
		 * getUpdateCount() is about the only thing outside of
		 * DataConnectionIfc that we need.
		 */
		JdbcDataConnection connection = (JdbcDataConnection) dc;
		MAXManagerOverride managerOverride = (MAXManagerOverride) da
				.getDataObject();

		saveManagerOverride(connection, managerOverride);
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcWriteManagerOverride.execute");
	}

	protected void saveManagerOverride(JdbcDataConnection dc,
			MAXManagerOverride managerOverride) throws DataException {
		try 
		{
			insertManagerOverride(dc, managerOverride);
		} catch (DataException de) {
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN);
		}
	}

	protected void insertManagerOverride(JdbcDataConnection dc,
			MAXManagerOverride managerOverride) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();
		//System.out.println("YES IT IS GOING INSIDE MAXJDBCWRITEMANAGEROVERRIDE");
		sql.setTable(TABLE_RETAIL_TRANSACTION_MANAGER_OVERRIDE);

		sql.addColumn(FIELD_RETAIL_STOREID, getStoreID(managerOverride));
		sql.addColumn(FIELD_MGR_WORKSTATION_ID,getWorkstationID(managerOverride));
		sql.addColumn(FIELD_BUSINESS_DATE, getBusinessDay(managerOverride));
		sql.addColumn(FIELD_TRANSACTION_ID, getTransactionID(managerOverride));
		String bizdate=getBusinessDay(managerOverride);
		String registerId=getWorkstationID(managerOverride);
		sql.addColumn(FIELD_MANAGER_OVERRIDE_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(managerOverride,dc,bizdate,registerId));
		sql.addColumn(FIELD_OVERRIDE_AUTHORIZING_EMPLOYEEID,getOverrideAuthorizingEmployeeID(managerOverride));
		sql.addColumn(FIELD_OVERRIDE_FEATURE_ID,getOverrideFeatureID(managerOverride));
		//sql.addColumn(FIELD_OVERRIDE_FEATURE_ID,managerOverride.getFeatureId());
		sql.addColumn(FIELD_MGR_STORE_CREDIT_ID,getStoreCreditID(managerOverride));
		sql.addColumn(FIELD_MGR_ITEM_ID, getPluItemID(managerOverride));
		sql.addColumn(FIELD_MGR_RECORD_CREATION_TIMESTAMP,getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_MGR_RECORD_LAST_MODIFIED_TIMESTAMP,getSQLCurrentTimestampFunction());
//		System.out.println("1st");
		sql.addColumn(FIELD_CASHIER_ID, getCashierID(managerOverride));
		sql.addColumn(FIELD_AMOUNT_MO , getAmountMO(managerOverride));

	//	System.out.println("2nd");
		sql.addColumn(FIELD_LMR_ID , getLMR_ID(managerOverride));
//		try {
//			System.out.println(sql.getSQLString());
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		try 
		{
			dc.execute(sql.getSQLString());
		} catch (DataException de) {
			logger.error("" + de + "");
			System.out.println(de+"exception print here..........."  );

			throw de;
		} catch (Exception e) {
			logger.error("" + "");
			System.out.println(e+"exception print here..........."  );

			throw new DataException(DataException.UNKNOWN);
		}
	}

		protected String getLMR_ID(MAXManagerOverride managerOverride) {
		// TODO Auto-generated method stub
		return ("'" + managerOverride.getLMR_ID() + "'");
	}


	protected String getAmountMO(MAXManagerOverride managerOverride) {
		// TODO Auto-generated method stub
		return ("'" + managerOverride.getAmountMO() + "'");
	}

	protected String getCashierID(MAXManagerOverride managerOverride) {
		// TODO Auto-generated method stub
		return ("'" + managerOverride.getCashierID() + "'");
	}

	protected String getStoreID(MAXManagerOverride managerOverride) {
		return ("'" + managerOverride.getStoreID() + "'");
	}

	protected String getWorkstationID(MAXManagerOverride managerOverride) {
		return ("'" + managerOverride.getWsID() + "'");
	}

	protected String getBusinessDay(MAXManagerOverride managerOverride) {
		return dateToSQLDateString(managerOverride.getBusinessDay());
	}

	protected String getTransactionID(MAXManagerOverride managerOverride) {
		if(managerOverride.getTransactionID()!=null && !managerOverride.getTransactionID().equals("")) {
		return ("'" + managerOverride.getTransactionID() + "'");
		}
		else {
			return ("'" + managerOverride.getTransactionNO() + "'");
		}
	}
	
	protected String getSequenceNumber(MAXManagerOverride managerOverride,JdbcDataConnection dc,String bizdate,String registerId) {
		
		if("-1".equalsIgnoreCase(managerOverride.getSequenceNumber()))
		{
			return "'" + getManagerOverrideSequenceNo(dc,bizdate,registerId)+ "'";
		}
		return ("'" + managerOverride.getSequenceNumber() + "'");
	}

	protected String getOverrideAuthorizingEmployeeID(
			MAXManagerOverride managerOverride) {
		return ("'" + managerOverride.getManagerId() + "'");
	}

	protected int getOverrideFeatureID(MAXManagerOverride managerOverride) 
	{
		int returnVal = 0;
		if(!Util.isEmpty(managerOverride.getFeatureId()))
			returnVal = Integer.valueOf(managerOverride.getFeatureId());
		return returnVal;
	}

	protected String getPluItemID(MAXManagerOverride managerOverride) {
		if(!Util.isEmpty(managerOverride.getItemId()))
			return("'" + managerOverride.getItemId()+ "'");
		else
			return "0";
	}

	protected String getStoreCreditID(MAXManagerOverride managerOverride) {
		return ("'" + managerOverride.getStoreCreditId() + "'");
	}
	protected int getManagerOverrideSequenceNo(JdbcDataConnection connection,String bizdate, String registerId)
	{
		int managerOverrideSequenceNo = 1;
		//Changed the AI_TRN for non transaction manager overrides from 0 to -1 to fix missing transaction issue
		String query="SELECT MAX(AI_LN_OVRD) FROM TR_LTM_EM_OVRD WHERE DC_DY_BSN="+bizdate+" AND ID_WS="+registerId+" and AI_TRN=-1";
		try {
			connection.execute(query);
			ResultSet rs = (ResultSet)connection.getResult();
			if (rs.next())
			{
				managerOverrideSequenceNo += rs.getInt(1);
			}
		} catch (DataException e) {	
			logger.error("Error while reading max managerOverrideSequenceNo ",e);
		} catch (SQLException e) {
			logger.error("Error while reading max managerOverrideSequenceNo ",e);
		}
			
		return managerOverrideSequenceNo; 
	}
}
