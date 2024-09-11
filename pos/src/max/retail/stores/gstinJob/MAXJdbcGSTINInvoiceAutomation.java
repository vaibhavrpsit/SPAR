package max.retail.stores.gstinJob;


import java.io.CharArrayReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import max.retail.stores.domain.gstin.GSTInvoice;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.gstinJob.utility.gstin.GSTINInvoice;
import max.retail.stores.gstinJob.utility.gstin.GSTINInvoiceIfc;
import max.retail.stores.gstinJob.utility.gstin.GSTINInvoiceStatus;
import max.retail.stores.gstinJob.utility.gstin.GSTINInvoiceStatusIfc;
import max.retail.stores.gstinJob.utility.gstin.MAXEInvoiceResp;
import max.retail.stores.gstinJob.utility.gstin.MAXGSTINConstantsIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
public class MAXJdbcGSTINInvoiceAutomation extends JdbcDataOperation implements MAXARTSDatabaseIfc {


	private static final long serialVersionUID = 8529112041558867485L;

	

	private static final String request = null;

	private static final String size = null;

	private static Logger logger = Logger.getLogger(MAXJdbcGSTINInvoiceAutomation.class.getName());;

	JdbcDataConnection connection = null;

	HashMap result = null;
	


	Connection jdbcConnection = null;
	HashMap<String,Double> map = null;

	int sequence = 0;


	public void execute(DataTransactionIfc data, DataConnectionIfc dataConnection, DataActionIfc dataaction) throws DataException {
		result = new HashMap();
		connection = (JdbcDataConnection) dataConnection;
		jdbcConnection = connection.getConnection();
		HashMap input = (HashMap) dataaction.getDataObject();
		int transferID = (Integer) input.get(MAXCodeConstantsIfc.GSTIN_AUTOMATION);
		if(transferID==1){
			HashMap gstinConf= new HashMap<>();
			gstinConf = selectConfigParam(dataConnection);	
			data.setResult(gstinConf);			
		}else if(transferID==2){
			int days=0;
			if(input.get(MAXCodeConstantsIfc.GSTIN_TTL_DAYS)!="" && input.get(MAXCodeConstantsIfc.GSTIN_TTL_DAYS)!=null) {
			 days =  Integer.parseInt(input.get(MAXCodeConstantsIfc.GSTIN_TTL_DAYS).toString());
			}
			int totalTxn =Integer.parseInt(input.get(MAXCodeConstantsIfc.GSTIN_TTL_TXN).toString());
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);
			String businessDate = businessDate(storeId, jdbcConnection);
			String minBusinessDate = minBusinessDate(days+1, jdbcConnection);
			ArrayList<GSTINInvoiceIfc> invoiceList = getGstinDetails(jdbcConnection,  businessDate,  minBusinessDate, totalTxn);
			data.setResult(invoiceList);
		}else if(transferID==3){
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);	
			GSTINInvoiceIfc invoice = (GSTINInvoiceIfc) input.get(MAXCodeConstantsIfc.GSTIN_INVOICE);	
			boolean updateStatus = updateGSTINSaveStatus(storeId, invoice, jdbcConnection);

			data.setResult(updateStatus);
		}else if(transferID==4){
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);	
			MAXEInvoiceResp eInv  = (MAXEInvoiceResp) input.get(MAXCodeConstantsIfc.GSTIN_EINV);	
			String documentId = (String) input.get(MAXCodeConstantsIfc.GSTIN_DOC_ID);
			GSTINInvoiceIfc invoice = (GSTINInvoiceIfc) input.get(MAXCodeConstantsIfc.GSTIN_INVOICE);	
			boolean updateStatus = updateGSTINSuccessStatus(storeId, eInv, documentId, invoice, jdbcConnection);
			data.setResult(updateStatus);
		}
		else if(transferID==5){
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);		
			String documentError = (String) input.get(MAXCodeConstantsIfc.GSTIN_DOC_ERROR);
			GSTINInvoiceIfc invoice = (GSTINInvoiceIfc) input.get(MAXCodeConstantsIfc.GSTIN_INVOICE);	
			boolean updateStatus = updateGSTINError(storeId, documentError, invoice, jdbcConnection);
			data.setResult(updateStatus);
		}
		else if(transferID==6){
			int days=0;
			if(input.get(MAXCodeConstantsIfc.GSTIN_TTL_DAYS)!="" && input.get(MAXCodeConstantsIfc.GSTIN_TTL_DAYS)!=null) {
			 days =  Integer.parseInt(input.get(MAXCodeConstantsIfc.GSTIN_TTL_DAYS).toString());
			}
			int totalTxn =Integer.parseInt(input.get(MAXCodeConstantsIfc.GSTIN_TTL_TXN).toString());
			String storeId = (String) input.get(MAXCodeConstantsIfc.STORE_ID);
			String businessDate = businessDate(storeId, jdbcConnection);
			String minBusinessDate = minBusinessDate(days+1, jdbcConnection);
			ArrayList<GSTINInvoiceIfc> invoiceList = getFailedGstinDetails(jdbcConnection,  businessDate,  minBusinessDate, totalTxn);
			data.setResult(invoiceList);
		}

	}

	protected HashMap<String, String> selectConfigParam(DataConnectionIfc connection) throws DataException{

		HashMap<String, String> map= new HashMap<String, String>();
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_GSTIN_DETAILS, ALIAS_GSTIN_CONFIG);
		sql.addColumn(ALIAS_GSTIN_CONFIG + "." + FIELD_PARAM_NM);
		sql.addColumn(ALIAS_GSTIN_CONFIG + "." + FIELD_PARAM_VALUE);

		try
		{
			connection.execute(sql.getSQLString());
			ResultSet rs = (ResultSet) connection.getResult();

			while (rs.next())
			{
				String paramName= rs.getString(FIELD_PARAM_NM);
				String paramVal= rs.getString(FIELD_PARAM_VALUE);
				map.put(paramName, paramVal);				
			}

			rs.close();

		}catch (DataException de) {
			logger.warn("" + de + "");
			throw de;
		} catch (SQLException se) {
			logger.warn("" + se + "");
			throw new DataException(DataException.SQL_ERROR, "unable to get gstin details", se);
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "unable to get gstin details", e);
		}
		return map;
	}

	public String businessDate(String storeID, Connection jdbcConn) {

		String businessDate="";
		String query = "select str.sc_hst_str, bsn.dc_dy_bsn  from le_hst_str str, ca_prd_rp rp, ca_dy_bsn bsn where str.id_str_rt = '"+storeID+"' "
				+ " and str.fy = rp.fy and str.ty_pr_prd = rp.ty_pr_prd and str.id_pr_prd = rp.id_pr_prd "
				+ " and rp.fy = bsn.fy and rp.fw_nmb = bsn.fw_nmb and rp.wd_dy_fsc = bsn.wd_dy_fsc order by bsn.dc_dy_bsn desc";

		Statement stmt = null;
		ResultSet rs  = null;
		try {
			stmt = jdbcConn.createStatement();
			logger.info(query);
			rs  = stmt.executeQuery(query);
			if(rs.next())
			{
				businessDate = rs.getString("dc_dy_bsn");
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		finally
		{
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}			
		}

		return businessDate;
	}

	public String minBusinessDate(int count, Connection jdbcConn) {

		String businessDate="";
		String query = "SELECT * FROM (SELECT  E.DC_DY_BSN,  row_number() over (order by E.DC_DY_BSN desc) rn FROM CA_DY_BSN E order by E.DC_DY_BSN desc) WHERE RN = '"+count+"'";

		Statement stmt = null;
		ResultSet rs  = null;
		try {
			stmt = jdbcConn.createStatement();
			logger.info(query);
			rs  = stmt.executeQuery(query);
			if(rs.next())
			{
				businessDate = rs.getString("dc_dy_bsn");
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}
		finally
		{
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}			
		}
        
		return businessDate;
	}

	public ArrayList<GSTINInvoiceIfc> getGstinDetails(Connection jdbcConn, String businessDate, String minBusinessDate, int rowLimit) {

		ArrayList<GSTINInvoiceIfc> invoiceList = new ArrayList<GSTINInvoiceIfc>();
		logger.error("Invoicelist");
		String query = "select * from "+TABLE_GSTIN_E_INVOICE_DETAILS+ " WHERE GET_INVOICE_STATUS = '0' AND dc_dy_bsn BETWEEN '"+minBusinessDate+"' AND '"+businessDate+"'  AND  ROWNUM <= "+rowLimit;

		Statement stmt = null;
		ResultSet rs  = null;
		try {
			stmt = jdbcConn.createStatement();
			logger.info(query);
			rs  = stmt.executeQuery(query);
			GSTINInvoiceIfc invoice = null;
			while (rs.next()) {
				invoice = new GSTINInvoice();

				invoice.setStoreID( rs.getString(FIELD_RETAIL_STORE_ID));
				invoice.setRegID( rs.getString(FIELD_WORKSTATION_ID));
				invoice.setBusinessDate( rs.getString(FIELD_BUSINESS_DAY_DATE));
				invoice.setTxnID( rs.getString(FIELD_TRANSACTION_SEQUENCE_NUMBER));
				invoice.setTxnType( rs.getString(FIELD_TRANSACTION_TYPE_CODE));
				invoice.setTxnStatus( rs.getString(FIELD_TRANSACTION_STATUS_CODE));
				invoice.setInvoiceStatus(rs.getString(FIELD_INVOICE_STATUS));
				invoice.setCustGSTIN(rs.getString(FIELD_INVOICE_TO_GSTIN));
				invoice.setStoreGSTIN(rs.getString(FIELD_INVOICE_FROM_GSTIN));
				invoice.setInvoiceRequest(rs.getString(FIELD_GST_REQUEST));
				invoice.setInvRefID(rs.getString(FIELD_INVOICE_REFERENCE_ID));
				invoice.setDocumentNo(rs.getString(FIELD_INVOICE_DOCUMENT_NO));
				invoice.setInvoiceError(rs.getString(FIELD_INVOICE_ERROR));
				invoice.setAckNo(rs.getString(FIELD_INVOICE_ACK_NO));
				invoice.setAckDate(rs.getString(FIELD_INVOICE_ACKDATE));
				invoice.setIrn(rs.getString(FIELD_INVOICE_IRN));
				invoice.setSigned(rs.getString(FIELD_INVOICE_SIGNED));
				invoice.setSignedQRCode(rs.getString(FIELD_INVOICE_SIGNED_QRCODE));
				invoice.setqRCode(rs.getString(FIELD_INVOICE_QRCODE));
				invoice.setqRCodeData(rs.getString(FIELD_INVOICE_QRCODE_DATA));
				invoiceList.add(invoice);
			}
			stmt.close();
			rs.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		finally
		{
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}			
		}

		return invoiceList;

	}



	public boolean updateGSTINSaveStatus(String storeId, GSTINInvoiceIfc invoice, Connection jdbcConnection) {


		SQLUpdateStatement sql = new SQLUpdateStatement();
		sql.setTable(TABLE_GSTIN_E_INVOICE_DETAILS);
		sql.addColumn(FIELD_INVOICE_STATUS, "1");
		sql.addColumn(FIELD_INVOICE_REFERENCE_ID, makeSafeString(invoice.getInvRefID()));
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(invoice.getStoreID()));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE, makeSafeString(invoice.getBusinessDate()));
		sql.addQualifier(FIELD_WORKSTATION_ID, makeSafeString(invoice.getRegID()));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, makeSafeString(invoice.getTxnID()));
		//
		try {
			logger.info(sql.getSQLString());
			connection.execute(sql.getSQLString());
			connection.getUpdateCount();
			return true;
		} catch (DataException e) {
			logger.error(e.getMessage());
			return false;
		}

	}


	private boolean updateGSTINSuccessStatus(String storeId, MAXEInvoiceResp eInv, String documentId, GSTINInvoiceIfc invoice, Connection jdbcCon) {
		boolean flag=false;
		
			String query = "UPDATE " + TABLE_GSTIN_E_INVOICE_DETAILS + " SET " 	+FIELD_INVOICE_DOCUMENT_NO + " = ? ," +FIELD_INVOICE_ACK_NO + " = ? ," 
				+ FIELD_INVOICE_ACKDATE + " = ? ," 	+ FIELD_INVOICE_IRN + " = ? ," 	+ FIELD_INVOICE_SIGNED + " = ? ," 	+ FIELD_INVOICE_SIGNED_QRCODE + " = ? ," 
				+ FIELD_INVOICE_QRCODE + " = ? ," + FIELD_INVOICE_QRCODE_DATA + " = ? ," + FIELD_GET_INVOICE_ERROR + " = ? ," + FIELD_INVOICE_STATUS + " = '2' ," 
				+ FIELD_RECORD_LAST_MODIFIED_TIMESTAMP + " = ? "
				+ " WHERE " +  FIELD_RETAIL_STORE_ID + " = ?"+" AND " +  FIELD_WORKSTATION_ID + " = ?  AND " +  FIELD_BUSINESS_DAY_DATE + " = ?  AND " +  FIELD_TRANSACTION_SEQUENCE_NUMBER + " = ?";
		
		PreparedStatement ps = null;
		try
		{


			ps = jdbcCon.prepareStatement(query);
			int n = 1;
			ps.setString(n++, documentId);
			ps.setString(n++,  eInv.getAckNumber());
			ps.setString(n++, eInv.getAckDate());
			ps.setString(n++,  eInv.getIrn());
			ps.setCharacterStream(n++, new CharArrayReader(eInv.getSignedInvoice().toCharArray()), eInv.getSignedInvoice().length());
			ps.setCharacterStream(n++, new CharArrayReader(eInv.getSignedQRCode().toCharArray()), eInv.getSignedQRCode().length());
			ps.setCharacterStream(n++, new CharArrayReader(eInv.getQrCode().toCharArray()), eInv.getQrCode().length());
			ps.setCharacterStream(n++, new CharArrayReader(eInv.getQrCodeData().toCharArray()), eInv.getQrCodeData().length());
			
			ps.setString(n++, eInv.getErrors());			
			ps.setTimestamp(n++, new Timestamp(new Date().getTime()));
			ps.setString(n++, invoice.getStoreID());
			ps.setString(n++, invoice.getRegID());
			ps.setString(n++, invoice.getBusinessDate());
			ps.setLong(n++, Long.parseLong(invoice.getTxnID()));
			int rr = ps.executeUpdate();
			flag=true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		finally
		{
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
		return flag;

		
		


	}

	private boolean updateGSTINError(String storeId,  String documentError, GSTINInvoiceIfc invoice, Connection jdbcConnection2) {
		SQLUpdateStatement sql = new SQLUpdateStatement();
		sql.setTable(TABLE_GSTIN_E_INVOICE_DETAILS);
		sql.addColumn(FIELD_GET_INVOICE_ERROR, makeSafeString(documentError));
		sql.addColumn(FIELD_INVOICE_STATUS, "3");
		sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
		sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(invoice.getStoreID()));
		sql.addQualifier(FIELD_BUSINESS_DAY_DATE, makeSafeString(invoice.getBusinessDate()));
		sql.addQualifier(FIELD_WORKSTATION_ID, makeSafeString(invoice.getRegID()));
		sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, makeSafeString(invoice.getTxnID()));
		//
		try {
			logger.info(sql.getSQLString());
			connection.execute(sql.getSQLString());
			connection.getUpdateCount();
			return true;
		} catch (DataException e) {
			logger.error(e.getMessage());
			return false;
		}


	}



	public ArrayList<GSTINInvoiceIfc> getFailedGstinDetails(Connection jdbcConn, String businessDate, String minBusinessDate, int rowLimit) {
logger.error("gstinvoice");
		ArrayList<GSTINInvoiceIfc> invoiceList = new ArrayList<GSTINInvoiceIfc>();
		String query = "select * from "+TABLE_GSTIN_E_INVOICE_DETAILS+ " WHERE (GET_INVOICE_STATUS = '1' OR GET_INVOICE_STATUS = '3') AND dc_dy_bsn BETWEEN '"+minBusinessDate+"' AND '"+businessDate+"'  AND  ROWNUM <= "+rowLimit +" ORDER BY TS_CRT_RCRD DESC";

		Statement stmt = null;
		ResultSet rs  = null;
		try {
			stmt = jdbcConn.createStatement();
			logger.info(query);
			rs  = stmt.executeQuery(query);
			GSTINInvoiceIfc invoice = null;
			while (rs.next()) {
				invoice = new GSTINInvoice();

				invoice.setStoreID( rs.getString(FIELD_RETAIL_STORE_ID));
				invoice.setRegID( rs.getString(FIELD_WORKSTATION_ID));
				invoice.setBusinessDate( rs.getString(FIELD_BUSINESS_DAY_DATE));
				invoice.setTxnID( rs.getString(FIELD_TRANSACTION_SEQUENCE_NUMBER));
				invoice.setTxnType( rs.getString(FIELD_TRANSACTION_TYPE_CODE));
				invoice.setTxnStatus( rs.getString(FIELD_TRANSACTION_STATUS_CODE));
				invoice.setInvoiceStatus(rs.getString(FIELD_INVOICE_STATUS));
				invoice.setCustGSTIN(rs.getString(FIELD_INVOICE_TO_GSTIN));
				invoice.setStoreGSTIN(rs.getString(FIELD_INVOICE_FROM_GSTIN));
				//invoice.setInvoiceRequest(rs.getString(FIELD_GST_REQUEST));
				String request = rs.getString(FIELD_GST_REQUEST);

				Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
				HashMap  data = gson.fromJson(request, HashMap.class);
				String statusReq = null;
				if(data.size()>0) {
					ArrayList<GSTINInvoiceStatusIfc> failedInvList = new ArrayList<GSTINInvoiceStatusIfc>();
					GSTINInvoiceStatusIfc failedInv = new GSTINInvoiceStatus();
					ArrayList req = gson.fromJson(gson.toJson(data.get("data")), ArrayList.class);  //GSTInvoice
					if(req.size()>0) {
						GSTInvoice inv = gson.fromJson(gson.toJson(req.get(0)) , GSTInvoice.class);
						failedInv.setLocationGstin(inv.getLocationGstin());
						failedInv.setLocationName(null);
						failedInv.setDocumentNumber(inv.getDocumentNumber());
						failedInv.setDocumentDate(inv.getDocumentDate());
						failedInv.setSupplyType(inv.getSupplyType());
						failedInv.setBillFromGstin(inv.getBillFromGstin());
						failedInv.setPortCode(null);
						failedInvList.add(failedInv);
					}

					HashMap<String, ArrayList<GSTINInvoiceStatusIfc>> mapInvoice = new HashMap<String, ArrayList<GSTINInvoiceStatusIfc>>();
					mapInvoice.put("criterias", failedInvList);
					statusReq = gson.toJson(mapInvoice);

				}
				invoice.setInvoiceRequest(statusReq);
				invoice.setInvRefID(rs.getString(FIELD_INVOICE_REFERENCE_ID));
				invoice.setDocumentNo(rs.getString(FIELD_INVOICE_DOCUMENT_NO));
				invoice.setInvoiceError(rs.getString(FIELD_INVOICE_ERROR));
				invoice.setAckNo(rs.getString(FIELD_INVOICE_ACK_NO));
				invoice.setAckDate(rs.getString(FIELD_INVOICE_ACKDATE));
				invoice.setIrn(rs.getString(FIELD_INVOICE_IRN));
				invoice.setSigned(rs.getString(FIELD_INVOICE_SIGNED));
				invoice.setSignedQRCode(rs.getString(FIELD_INVOICE_SIGNED_QRCODE));
				invoice.setqRCode(rs.getString(FIELD_INVOICE_QRCODE));
				invoice.setqRCodeData(rs.getString(FIELD_INVOICE_QRCODE_DATA));
				invoiceList.add(invoice);
			}
			stmt.close();
			rs.close();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		finally
		{
			try {
				stmt.close();
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}			
		}

		return invoiceList;

	}



}

