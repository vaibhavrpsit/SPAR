/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
*	
*	Rev 1.1    May 11, 2017		Ashish Yadav		Changes for M-Coupon Issuance  FES
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.domain.arts;


import java.util.ArrayList;

import com.capillary.solutions.landmark.transaction.dto.Effect;

import max.retail.stores.domain.mcoupon.MAXMcouponIfc;
import max.retail.stores.domain.transaction.MAXSaleReturnTransaction;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcSaveMcouponDeatils extends JdbcDataOperation implements max.retail.stores.persistence.utility.MAXARTSDatabaseIfc{

	/* 
	 * @Override by Ashish Yadav
	 * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
	 */
	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection,
			DataActionIfc dataAction) throws DataException {
		if (logger.isDebugEnabled())
			logger.debug("MAXJdbcSaveMcouponDeatils.execute");

		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		ArrayList<Effect> mcoupons = (ArrayList<Effect>) dataAction.getDataObject();

		saveMcoupons(connection, null);
		
		
		dataTransaction.setResult(null);
		if (!(logger.isDebugEnabled()))
			return;
		logger.debug("JdbcSaveCustomer.execute");
		
	}
	
	// changes starts for rev 1.1

	/**
	 * Added by Ashish Yadav
	 * To save the copons returned from cappillary
	 * @throws DataException 
	 */
	public void saveMcoupons(JdbcDataConnection connection,
			MAXSaleReturnTransaction saleReturnTransaction) throws DataException  {
		// TODO Auto-generated method stub
		
		ArrayList<MAXMcouponIfc> mcouponList=saleReturnTransaction.getMcouponList();
		
		for(MAXMcouponIfc mcoupon:mcouponList){
		SQLInsertStatement sql = new SQLInsertStatement();
		sql.setTable(MAXARTSDatabaseIfc.TABLE_MAX_CAPILLARY_ISSUED_CPNS);

		sql.addColumn(MAXARTSDatabaseIfc.FIELD_ID_STR_RT,makeSafeString(saleReturnTransaction.getWorkstation().getStoreID()));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_MCOUPON_ISSUE_DATE,getTMcouponIssueTimeStamp(saleReturnTransaction));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_ID_WS,makeSafeString(saleReturnTransaction.getWorkstation().getWorkstationID()));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_AI_TRN,saleReturnTransaction.getTransactionID());
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_CPN_NO,getCouponString(mcoupon));
		sql.addColumn(MAXARTSDatabaseIfc.FIELD_REQ_STAT,makeSafeString("Success"));
		
		
	

		try {
			logger.info("Save Mcoupon SQL"+sql.getSQLString());
			connection.execute(sql.getSQLString(), false);
		} catch (DataException de) {
			logger.error(de);
			throw de;
		} catch (Exception e) {
			logger.error(e);
			throw new DataException(0, "saveMcoupons", e);
		}
		}
		
	}
	// changes ends for rev 1.1
	
	public String getCouponString(MAXMcouponIfc mcoupon) {
		return makeSafeString(mcoupon.getCouponNumber());
	}
	
	public String getTMcouponIssueTimeStamp(SaleReturnTransactionIfc transaction) {
		return dateToDateFormatString(transaction.getTimestampBegin()
				.dateValue());
	}
	
}
