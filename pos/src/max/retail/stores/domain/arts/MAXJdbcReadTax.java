/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.0		May 04, 2017		Kritica Agarwal 	GST Changes
 *
 ********************************************************************************/
package max.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import  max.retail.stores.domain.LSIPLUtils.MAXIGSTTax;
import max.retail.stores.domain.tax.MAXTaxAssignment;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcReadTax extends JdbcDataOperation implements MAXARTSDatabaseIfc {
	
	private static final long serialVersionUID = 1L;
	private static Logger logger =
			Logger.getLogger(
					max.retail.stores.domain.arts.MAXJdbcReadTax.class);
	
	@Override
	public void execute(DataTransactionIfc transaction,
			DataConnectionIfc connection,
			DataActionIfc action) throws DataException {
		JdbcDataConnection dataConnection = (JdbcDataConnection)connection;
		MAXIGSTTax itemDetail = (MAXIGSTTax) action.getDataObject();
		ArrayList<MAXTaxAssignment> gstRegion = assignTaxAssignments(dataConnection,itemDetail);
		transaction.setResult(gstRegion);	
		
	}
	
	protected ArrayList<MAXTaxAssignment> assignTaxAssignments(JdbcDataConnection dataConnection,MAXIGSTTax itemDetail) throws DataException {
		// Get the Tax Assignments for each PLUItem	
		ArrayList<MAXTaxAssignment> taxAssignment =	getInterStateReCalculateTaxAssignments(dataConnection, itemDetail);
	return taxAssignment;
}

	public ArrayList<MAXTaxAssignment> getInterStateReCalculateTaxAssignments(
			JdbcDataConnection dataConnection, MAXIGSTTax itemDetail)
			throws DataException {

	ResultSet resultSet = null;
	ArrayList<MAXTaxAssignment> taxAssignmentList = new ArrayList<MAXTaxAssignment>();
	
	try {
		SQLSelectStatement sql = new SQLSelectStatement();
		sql.addTable(TABLE_TAX_ASSIGNMENT, ALIAS_TAX_ASSIGNMENT);
		sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_ID_CTGY_TX);
		sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD);
		sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_CD_DSCR);
		sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_RT);
		sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TX_FCT);
		sql.addColumn(ALIAS_TAX_ASSIGNMENT, FIELD_TXBL_FCT);
		sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + FIELD_ID_CTGY_TX + " = "
					+ itemDetail.getTaxCategory());
		/*sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." + MAXARTSDatabaseIfc.FIELD_RETAIL_STORE_ID + " = " + "'"
				+ itemDetail.getStoreId() + "'");*/
			/*
			 * if(itemDetail.isRrpTaxEnabled()){ sql.addQualifier(ALIAS_TAX_ASSIGNMENT +
			 * "."+ MAXARTSDatabaseIfc.FIELD_TAX_DIFF + " !='I' ");
			 * sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." +
			 * MAXARTSDatabaseIfc.FIELD_TAX_TO_REGION + " = " + ALIAS_TAX_ASSIGNMENT + "." +
			 * MAXARTSDatabaseIfc.FIELD_TAX_FROM_REGION); } else{
			 * sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "."+
			 * MAXARTSDatabaseIfc.FIELD_TAX_DIFF + " ='I' ");
			 * sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." +
			 * MAXARTSDatabaseIfc.FIELD_TAX_TO_REGION + " in " +
			 * "(select gst_reg_code from max_gst_reg_map where " +
			 * "upper(gst_reg_desc)=upper('"+itemDetail.getToRegion()+"'))" );
			 * 
			 * sql.addQualifier(ALIAS_TAX_ASSIGNMENT + "." +
			 * MAXARTSDatabaseIfc.FIELD_TAX_FROM_REGION + " in " +
			 * "(select gst_reg_code from max_gst_reg_map where " +
			 * "upper(gst_reg_desc)=upper('"+itemDetail.getFromRegion()+"'))" ); }
			 */
		
		//System.out.println("Sql Query : "+ sql.getSQLString());
		dataConnection.execute(sql.getSQLString());
		resultSet = (ResultSet) dataConnection.getResult();

		if (resultSet != null) {
			MAXTaxAssignment taxAssignment = null;
			while (resultSet.next()) {
				taxAssignment = new MAXTaxAssignment();

				int taxCtgy = resultSet.getInt(FIELD_ID_CTGY_TX);
				String taxCode = resultSet.getString(FIELD_TX_CD);

				String taxCodeDesc = resultSet.getString(FIELD_TX_CD_DSCR);
				BigDecimal taxRate = resultSet.getBigDecimal(FIELD_TX_RT);
				BigDecimal taxAmtFactor = resultSet.getBigDecimal(FIELD_TX_FCT);
				BigDecimal taxableAmtFactor = resultSet.getBigDecimal(FIELD_TXBL_FCT);

				taxAssignment.setTaxCode(taxCode);
				taxAssignment.setTaxCodeDescription(taxCodeDesc);
				taxAssignment.setTaxRate(taxRate);
				taxAssignment.setTaxableAmountFactor(taxableAmtFactor);
				taxAssignment.setTaxAmountFactor(taxAmtFactor);
				taxAssignment.setTaxCategory(taxCtgy);
				taxAssignmentList.add(taxAssignment);
			}
		}
	}

	catch (SQLException sqlException) {
		dataConnection.logSQLException(sqlException, " Tax Assignment lookup -- Error Retrieving Tax Assignments");
		throw new DataException(DataException.SQL_ERROR, "Tax Assignment lookup", sqlException);
	} finally {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException se) {
				dataConnection.logSQLException(se, "Tax Assignment lookup -- Could not close result handle");
			}
		}
	}
	return taxAssignmentList;

}

}
