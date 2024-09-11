package max.retail.stores.gstinCentralJob;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.gstin.MAXGSTINValidationResponseIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.gstinCentralJob.gstin.Invoice;
import max.retail.stores.gstinCentralJob.gstin.InvoiceIfc;
import max.retail.stores.gstinCentralJob.gstin.InvoiceResp;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import max.retail.stores.domain.gstin.MAXGSTINValidationResponse;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.util.DBUtils;

public class MAXJdbcGSTINDataTransfer extends JdbcDataOperation implements MAXARTSDatabaseIfc {


	private static final long serialVersionUID = 8529112041558867485L;

	
	
	private static final String getStoreID = null;
	
	//private static final String getstoreGSTINID = "09ABFCS6700C1ZK";

	private static final String getRegID = null;



	private static final String FIELD_ID_PRTY = null;

	

	

	private static Logger logger = Logger.getLogger(MAXJdbcGSTINDataTransfer.class.getName());;

	//Rev 1.1 start --end 
	//JdbcDataConnection connection = null;

	HashMap result = null;

	//Rev 1.1 start --end 
	//Connection jdbcConnection = null;
	HashMap<String,Double> map = null;

	int sequence = 0;


	public void execute(DataTransactionIfc data, DataConnectionIfc dataConnection, DataActionIfc dataaction) throws DataException {
		result = new HashMap();
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		Connection	jdbcConnection = connection.getConnection();
		HashMap input = (HashMap) dataaction.getDataObject();
		int transferID = (Integer) input.get(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_ID);
		if(transferID==1){
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);
			ArrayList<InvoiceIfc> list= selectGstinInvoiceDetails(jdbcConnection, storeId);
			data.setResult(list);
		}else if(transferID==2){
			ArrayList<InvoiceResp> invoiceRespList = (ArrayList<InvoiceResp>) input.get(MAXCodeConstantsIfc.GSTIN_DATA_TRANFER_DETAILS);
			for(int i=0; i<invoiceRespList.size(); i++){
				InvoiceResp invoiceResp=invoiceRespList.get(i);
				updateEReceiptSendAppFlag(connection , invoiceResp);
			}
			//System.out.println("test jdbc");
		}
		else if(transferID==3){
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);
			String txnId = (String) input.get(MAXCodeConstantsIfc.TXNID);
			String storeGSTINId =  getStoreGSTIN(connection, storeId);
			MAXGSTINValidationResponseIfc storeGstin = getStoreGSTINDetails(connection, storeGSTINId, txnId, storeId);
			data.setResult((Serializable) storeGstin);
		}
		else if(transferID==4){
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);
			String storeGSTIN = (String) input.get(MAXCodeConstantsIfc.GSTIN_STORE_NO);			
			MAXGSTINValidationResponseIfc response = (MAXGSTINValidationResponseIfc) input.get(MAXCodeConstantsIfc.GSTIN_STORE);
			boolean flag = insertStoreGSTINDetails(connection, storeGSTIN, storeId, response);
			HashMap status = new HashMap();
			status.put("Status", flag);
			data.setResult(status);
		}
		

	}



	private ArrayList<InvoiceIfc> selectGstinInvoiceDetails(Connection jdbcConn, String storeID) {
		InvoiceIfc invoiceDetails = null;
		ArrayList<InvoiceIfc> invoiceList = new ArrayList<InvoiceIfc>();
		String query = "select * from "+TABLE_GSTIN_E_INVOICE_DETAILS+" where "+FIELD_RETAIL_STORE_ID+" = '"+storeID +"' and "
				+ FIELD_TRANSFER_STATUS +" = '0' and "+FIELD_INVOICE_STATUS+" = '2'";
		Statement stmt = null;
		ResultSet rs  = null;
		try {
			stmt = jdbcConn.createStatement();
			logger.info(query);
			rs  = stmt.executeQuery(query);
			while (rs.next()) {
				invoiceDetails = new Invoice();
				invoiceDetails.setStoreID(storeID);
				invoiceDetails.setRegID(rs.getString(FIELD_WORKSTATION_ID));
				invoiceDetails.setBusinessDate(rs.getString(FIELD_BUSINESS_DAY_DATE));
				invoiceDetails.setTxnID(rs.getString(FIELD_TRANSACTION_SEQUENCE_NUMBER));
				invoiceDetails.setTxnType(rs.getString(FIELD_TRANSACTION_TYPE_CODE));
				invoiceDetails.setTxnStatus(rs.getString(FIELD_TRANSACTION_STATUS_CODE));
				invoiceDetails.setCustGSTIN(rs.getString(FIELD_INVOICE_TO_GSTIN));
				invoiceDetails.setStoreGSTIN(rs.getString(FIELD_INVOICE_FROM_GSTIN));
				invoiceDetails.setInvoiceRequest(rs.getString(FIELD_GST_REQUEST));
				invoiceDetails.setInvRefID(rs.getString(FIELD_INVOICE_REFERENCE_ID));
				invoiceDetails.setDocumentNo(rs.getString(FIELD_INVOICE_DOCUMENT_NO));
				invoiceDetails.setAckNo(rs.getString(FIELD_INVOICE_ACK_NO));
				invoiceDetails.setAckDate(rs.getString(FIELD_INVOICE_ACKDATE));
				invoiceDetails.setIrn(rs.getString(FIELD_INVOICE_IRN));
				invoiceDetails.setSigned(rs.getString(FIELD_INVOICE_SIGNED));
				invoiceDetails.setSignedQRCode(rs.getString(FIELD_INVOICE_SIGNED_QRCODE));
				invoiceDetails.setqRCode(rs.getString(FIELD_INVOICE_QRCODE));
				invoiceDetails.setqRCodeData(rs.getString(FIELD_INVOICE_QRCODE_DATA));
				invoiceDetails.setCreatedRecord(rs.getString(FIELD_RECORD_CREATION_TIMESTAMP));
				invoiceList.add(invoiceDetails);		
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		finally
		{
			DBUtils.getInstance().closeResultSet(rs);
			DBUtils.getInstance().closeStatement(stmt);			
		}

		return invoiceList;
	}

	public boolean updateEReceiptSendAppFlag(JdbcDataConnection jdbcConn, InvoiceResp invoiceResp) {

		int count = retryCount(jdbcConn.getConnection(), invoiceResp);
		count = count + 1;
		SQLUpdateStatement sql = new SQLUpdateStatement();
		sql.setTable(TABLE_GSTIN_E_INVOICE_DETAILS);
		sql.addColumn(FIELD_TRANSFER_STATUS, makeSafeString(invoiceResp.getStatus()));
		sql.addColumn(FIELD_TRANSFER_ERROR_LOG, makeSafeString(invoiceResp.getError()));
		sql.addColumn(FIELD_TRANSFER_RETRY_COUNT, count);
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(invoiceResp.getStoreID()));
		sql.addQualifier(FIELD_WORKSTATION_ID, makeSafeString(invoiceResp.getRegID()));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE, makeSafeString(invoiceResp.getBusinessDate()));
		sql.addQualifier(MAXARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER, makeSafeString(invoiceResp.getTxnID()));
		try {
			logger.info(sql.getSQLString());
			jdbcConn.execute(sql.getSQLString());
			jdbcConn.getUpdateCount();
			return true;
		} catch (DataException e) {
			logger.error(e.getMessage());
			return false;
		}

	}


	public int retryCount(Connection jdbcConn, InvoiceResp invoiceResp) {

		int status = 0;
		Statement stmt = null;
		ResultSet rs  = null;
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(MAXARTSDatabaseIfc.TABLE_GSTIN_E_INVOICE_DETAILS);
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_TRANSFER_RETRY_COUNT);
		sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(invoiceResp.getStoreID()));
		sql.addQualifier(FIELD_WORKSTATION_ID, makeSafeString(invoiceResp.getRegID()));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE, makeSafeString(invoiceResp.getBusinessDate()));
		sql.addQualifier(MAXARTSDatabaseIfc.FIELD_TRANSACTION_SEQUENCE_NUMBER, makeSafeString(invoiceResp.getTxnID()));
		try {
			stmt = jdbcConn.createStatement();
			logger.info(sql.getSQLString());
			rs  = stmt.executeQuery(sql.getSQLString());
			if (rs.next()) {
				status = rs.getInt(1);
			} else {
				status = 0;
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
		} catch (DataException e) {
			logger.error(e.getMessage());
		}
		finally
		{
			DBUtils.getInstance().closeResultSet(rs);
			DBUtils.getInstance().closeStatement(stmt);
		}

		return status;
	}
	
	
	private MAXGSTINValidationResponseIfc getStoreGSTINDetails(JdbcDataConnection connection,
			String storeGSTIN, String txnID, String storeID) {

		MAXGSTINValidationResponseIfc response = null;
		HashMap<String, String> map = new HashMap<String, String>();
		ResultSet rs = null;
		try {
			SQLSelectStatement sql = new SQLSelectStatement();
			sql.setTable(TABLE_GSTIN_STORE_DETAILS);
			sql.addColumn(FIELD_LEGAL_NAME);
			sql.addColumn(FIELD_STATE_JURISDICTION_CD);
			sql.addColumn(FIELD_TAXPAYER_TYPE);
			sql.addColumn(FIELD_DATE_OF_CANCEL);

			sql.addColumn(FIELD_BUILDING_NM);
			sql.addColumn(FIELD_STREET);
			sql.addColumn(FIELD_LOCALITY);
			sql.addColumn(FIELD_BUILDING_NO);
			sql.addColumn(FIELD_STATE);

			sql.addColumn(FIELD_CITY);
			sql.addColumn(FIELD_DISTRICT);
	
			sql.addColumn(FIELD_FLOOR_NO);
			sql.addColumn(FIELD_LATITUTE);
			sql.addColumn(FIELD_PIN_CODE);
			sql.addColumn(FIELD_LONGTITUTE);
			


			sql.addColumn(FIELD_LAST_UPDATED);
			sql.addColumn(FIELD_REGISTRATION_DATE);
			sql.addColumn(FIELD_BUSINESS_CONSTITUTION);
			sql.addColumn(FIELD_GSTN_STATUS);
			sql.addColumn(FIELD_CENTR_JURISDICTION_CD);
			sql.addColumn(FIELD_CENTR_JURISDICTION_NM);
			sql.addColumn(FIELD_REGISTR_TRADE_NAME);
			sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(storeID));
			sql.addQualifier(FIELD_INVOICE_FROM_GSTIN, makeSafeString(storeGSTIN));
			connection.execute(sql.getSQLString());
			rs = (ResultSet) connection.getResult();
			response = new MAXGSTINValidationResponse();
			if (rs.next()) {
				
				response.setGstin(storeGSTIN);
				response.setLgnm(rs.getString(FIELD_LEGAL_NAME));
				response.setStj(rs.getString(FIELD_STATE_JURISDICTION_CD));
				response.setDty(rs.getString(FIELD_TAXPAYER_TYPE));
				response.setCxdt(rs.getString(FIELD_DATE_OF_CANCEL));
				response.setBnm(rs.getString(FIELD_BUILDING_NM));
				response.setSt(rs.getString(FIELD_STREET));
				response.setLoc(rs.getString(FIELD_LOCALITY));
				response.setBno(rs.getString(FIELD_BUILDING_NO));
				response.setStcd(rs.getString(FIELD_STATE));
				response.setCity(rs.getString(FIELD_CITY));
				response.setDst(rs.getString(FIELD_DISTRICT));
				response.setFlno(rs.getString(FIELD_FLOOR_NO));
				response.setLt(rs.getString(FIELD_LATITUTE));
				response.setPncd(rs.getString(FIELD_PIN_CODE));
				response.setLg(rs.getString(FIELD_LONGTITUTE));
				response.setLstupdt(rs.getString(FIELD_LAST_UPDATED));
				response.setRgdt(rs.getString(FIELD_REGISTRATION_DATE));
				response.setCtb(rs.getString(FIELD_BUSINESS_CONSTITUTION));
				response.setSts(rs.getString(FIELD_GSTN_STATUS));
				response.setCtjCd(rs.getString(FIELD_CENTR_JURISDICTION_CD));
				response.setCtj(rs.getString(FIELD_CENTR_JURISDICTION_NM));
				response.setTradeNam(rs.getString(FIELD_REGISTR_TRADE_NAME));


			}else {
				response.setGstin(storeGSTIN);
			}
			
		} catch (Exception exception) {
			logger.error("Couldn't save retail transaction.");
			logger.error("" + exception + "");

		}finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response;
	}
	
	private String getStoreGSTIN(DataConnectionIfc dataConnection, String storeId) {

		String storeGSTIN = null;
		ResultSet resultSet = null;
		try {
			SQLSelectStatement sql = new SQLSelectStatement();
			sql.addTable(TABLE_RETAIL_STORE_GSTIN);
			sql.addColumn(FIELD_ID_IDTN_TX_NMB2);
			sql.addQualifier(FIELD_RETAIL_STORE_ID_1, makeSafeString(storeId));
			sql.addColumn(FIELD_ID_PRTY);
			sql.addQualifier(FIELD_ID_STR_RT,makeSafeString(storeId));
			
			logger.error("SQL"+sql.getSQLString());
			System.out.println("sql.getSQLString() "+sql.getSQLString());
			dataConnection.execute(sql.getSQLString());
			resultSet = (ResultSet) dataConnection.getResult();
			if (resultSet.next()) {
				storeGSTIN = getSafeString(resultSet, 1);
			}
			
		} catch (Exception exception) {
			logger.error("Couldn't save retail transaction.");
			logger.error("" + exception + "");
		}finally {
			try {
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return storeGSTIN;
	}
	
	
	protected boolean insertStoreGSTINDetails(JdbcDataConnection connection,
			String storeGSTIN,  String storeID, MAXGSTINValidationResponseIfc response) throws DataException {
			boolean status = false;
			SQLInsertStatement sql = new SQLInsertStatement();
			sql.setTable(TABLE_GSTIN_STORE_DETAILS);
			sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(storeID));
			sql.addColumn(FIELD_INVOICE_FROM_GSTIN, makeSafeString(response.getGstin()));
			sql.addColumn(FIELD_LEGAL_NAME, makeSafeString(response.getLgnm()));
			sql.addColumn(FIELD_STATE_JURISDICTION_CD, makeSafeString(response.getStj()));
			sql.addColumn(FIELD_TAXPAYER_TYPE, makeSafeString(response.getDty()));
			sql.addColumn(FIELD_DATE_OF_CANCEL, makeSafeString(response.getCxdt()));

			sql.addColumn(FIELD_BUILDING_NM, makeSafeString(response.getBnm()));
			sql.addColumn(FIELD_STREET, makeSafeString(response.getSt()));
			sql.addColumn(FIELD_LOCALITY, makeSafeString(response.getLoc()));
			sql.addColumn(FIELD_BUILDING_NO, makeSafeString(response.getBno()));
			sql.addColumn(FIELD_STATE, makeSafeString(response.getStcd()));

			sql.addColumn(FIELD_CITY, makeSafeString(response.getCity()));
			sql.addColumn(FIELD_DISTRICT, makeSafeString(response.getDst()));
			
			sql.addColumn(FIELD_FLOOR_NO, makeSafeString(response.getFlno()));
			sql.addColumn(FIELD_LATITUTE, makeSafeString(response.getLt()));
			sql.addColumn(FIELD_PIN_CODE, makeSafeString(response.getPncd()));
			sql.addColumn(FIELD_LONGTITUTE, makeSafeString(response.getLg()));


			sql.addColumn(FIELD_LAST_UPDATED, makeSafeString(response.getLstupdt()));
			sql.addColumn(FIELD_REGISTRATION_DATE, makeSafeString(response.getRgdt()));
			sql.addColumn(FIELD_BUSINESS_CONSTITUTION, makeSafeString(response.getCtb()));
			sql.addColumn(FIELD_GSTN_STATUS, makeSafeString(response.getSts()));
			sql.addColumn(FIELD_CENTR_JURISDICTION_CD, makeSafeString(response.getStcd()));
			sql.addColumn(FIELD_CENTR_JURISDICTION_NM, makeSafeString(response.getCtj()));
			sql.addColumn(FIELD_REGISTR_TRADE_NAME, makeSafeString(response.getTradeNam()));

			sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
					getSQLCurrentTimestampFunction());
			sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
					getSQLCurrentTimestampFunction());
			try {
				logger.info(sql.getSQLString());
				System.out.println("sql.getSQLString()  :  " +sql.getSQLString());
				connection.execute(sql.getSQLString());
				status = true;
			} catch (DataException de) {
				logger.error("" + de + "");
				throw de;
			} catch (Exception e) {
				logger.error("Exception during saving request response Youth Card"
						+ e);
				throw new DataException(DataException.UNKNOWN);
			}
		return status;
	}

}

