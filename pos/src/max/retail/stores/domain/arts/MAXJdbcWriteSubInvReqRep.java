 
package max.retail.stores.domain.arts;

//Changes to capture SubInvReqRep for Reporting purpose



import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import max.retail.stores.domain.SubInvReqRep.MAXSubInvReqRep;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;


/**
 * @author kajal
 *
 */
public class MAXJdbcWriteSubInvReqRep extends JdbcDataOperation implements
		MAXARTSDatabaseIfc, MAXCodeConstantsIfc {

	/**
	 * 
	 * 
	 */
	//private static final long serialVersionUID = -4806979289386966392L;

	public MAXJdbcWriteSubInvReqRep() {
		super();
		setName("MAXJdbcWriteSubInvReqRep");
		//System.out.println("Going inside MAXJdbcWriteSubInvReqRep");
		
		
	}

	/* (non-Javadoc)
	 * @see com.extendyourstore.foundation.manager.ifc.data.DataOperationIfc#execute(com.extendyourstore.foundation.manager.ifc.data.DataTransactionIfc, com.extendyourstore.foundation.manager.ifc.data.DataConnectionIfc, com.extendyourstore.foundation.manager.ifc.data.DataActionIfc)
	 */
	public void execute(DataTransactionIfc dt, DataConnectionIfc dc,
			DataActionIfc da) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcWriteSubInvReqRep.execute");
		//System.out.println("Again Going inside MAXJdbcWriteSubInvReqRep");
		
		/*
		 * getUpdateCount() is about the only thing outside of
		 * DataConnectionIfc that we need.
		 */
		JdbcDataConnection connection = (JdbcDataConnection) dc;
		MAXSubInvReqRep SubInvReqRep = (MAXSubInvReqRep) da
				.getDataObject();

		saveSubInvReqRep(connection, SubInvReqRep);
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcWriteSubInvReqRep.execute");
	}

	protected void saveSubInvReqRep(JdbcDataConnection dc,
			MAXSubInvReqRep SubInvReqRep) throws DataException {
		try 
		{
			insertSubInvReqRep(dc, SubInvReqRep);
		} catch (DataException de) {
			throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN);
		}
	}

	protected void insertSubInvReqRep(JdbcDataConnection dc,
			MAXSubInvReqRep SubInvReqRep) throws DataException {
		SQLInsertStatement sql = new SQLInsertStatement();
		//System.out.println("YES IT IS GOING INSIDE MAXJdbcWriteSubInvReqRep");
		sql.setTable(TABLE_SUB_INV_REQ_REP);

		sql.addColumn(FIELD_RETAIL_STOREID, getStoreID(SubInvReqRep));
		sql.addColumn(FIELD_TRANSACTION_ID, getTransactionID(SubInvReqRep));
		
		sql.addColumn(FIELD_MGR_WORKSTATION_ID,(getTransactionID(SubInvReqRep).substring(6,9)));
		//String bizdate=getBusinessDay(SubInvReqRep);
		//String registerId=getWorkstationID(SubInvReqRep);
		sql.addColumn(FIELD_BUSINESS_DATE, getBusinessDay(SubInvReqRep));
		//sql.addColumn(FIELD_MANAGER_OVERRIDE_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(SubInvReqRep,dc,bizdate,registerId));
		sql.addColumn(FIELD_SUB_INV_REQ,getSUB_INV_REQ(SubInvReqRep).toString());
		sql.addColumn(FIELD_SUB_INV_REP,getSUB_INV_REP(SubInvReqRep).toString());

		
		sql.addColumn(FIELD_MGR_RECORD_CREATION_TIMESTAMP,getSQLCurrentTimestampFunction());
		sql.addColumn(FIELD_MGR_RECORD_LAST_MODIFIED_TIMESTAMP,getSQLCurrentTimestampFunction());
		
//		System.out.println("1st");
		
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



	private String getSequenceNumber(MAXSubInvReqRep subInvReqRep, JdbcDataConnection dc, String bizdate,
			String registerId) {
		// TODO Auto-generated method stub
		return null;
	}

	private String getWorkstationID(MAXSubInvReqRep subInvReqRep) {
		// TODO Auto-generated method stub
		return null;
	}

	protected String getStoreID(MAXSubInvReqRep SubInvReqRep) {
		return ("'" + SubInvReqRep.getStoreID() + "'");
	}



	protected String getBusinessDay(MAXSubInvReqRep SubInvReqRep) {
		return dateToSQLDateString(SubInvReqRep.getBusinessDay());
	}

	protected String getTransactionID(MAXSubInvReqRep SubInvReqRep) {
		return ("'" + SubInvReqRep.getTransactionID() + "'");
	}
	protected String getSUB_INV_REQ (MAXSubInvReqRep SubInvReqRep) {
		return ("'" + SubInvReqRep.getSUB_INV_REQ() + "'");
	}
	
	protected String getSUB_INV_REP (MAXSubInvReqRep SubInvReqRep) {
		return ("'" + SubInvReqRep.getSUB_INV_REP() + "'");
	}
	
	

	protected int getSubInvReqRepSequenceNo(JdbcDataConnection connection,String bizdate, String registerId)
	{
		int SubInvReqRepSequenceNo = 1;
		//Changed the AI_TRN for non transaction manager overrides from 0 to -1 to fix missing transaction issue
		//String query="SELECT MAX(AI_LN_OVRD) FROM TR_LTM_EM_OVRD WHERE DC_DY_BSN="+bizdate+" AND ID_WS="+registerId+" and AI_TRN=-1";
		String query="SELECT MAX(AI_LN_OVRD) FROM SUB_INV_REQ_REP WHERE DC_DY_BSN="+bizdate+" AND ID_WS="+registerId+" and AI_TRN=-1";
		
		try {
			connection.execute(query);
			ResultSet rs = (ResultSet)connection.getResult();
			if (rs.next())
			{
				SubInvReqRepSequenceNo += rs.getInt(1);
			}
		} catch (DataException e) {	
			logger.error("Error while reading max SubInvReqRepSequenceNo ",e);
		} catch (SQLException e) {
			logger.error("Error while reading max SubInvReqRepSequenceNo ",e);
		}
			
		return SubInvReqRepSequenceNo; 
	}
}
