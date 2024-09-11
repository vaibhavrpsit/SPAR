package max.retail.stores.domain.arts;

import max.retail.stores.domain.liquidationreport.MAXLiquidationReport;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

public class MAXJdbcWriteLiquidationItem extends JdbcDataOperation implements
MAXARTSDatabaseIfc, MAXCodeConstantsIfc{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MAXJdbcWriteLiquidationItem() {
		super();
		setName("MAXJdbcWriteLiquidationItem");
		//System.out.println("Going inside MAXJdbcWriteLiquidationItem");
	}
		
		public void execute(DataTransactionIfc dt, DataConnectionIfc dc,
				DataActionIfc da) throws DataException {
			if (logger.isDebugEnabled())
				logger.debug("MAXJdbcWriteLiquidationItem.execute");
			//System.out.println("Again Going inside MAXJdbcWriteLiquidationItem");
			
			/*
			 * getUpdateCount() is about the only thing outside of
			 * DataConnectionIfc that we need.
			 */
			JdbcDataConnection connection = (JdbcDataConnection) dc;
			MAXLiquidationReport liquidationReport = (MAXLiquidationReport) da
					.getDataObject();

			saveLiquidationItem(connection, liquidationReport);
			if (logger.isDebugEnabled())
				logger.debug("MAXJdbcWriteLiquidationItem.execute");
		}

		protected void saveLiquidationItem(JdbcDataConnection dc,MAXLiquidationReport liquidationReport) throws DataException {
			try 
			{
				insertLiquidationItem(dc, liquidationReport);
			} catch (DataException de) {
				throw de;
			} catch (Exception e) {
				throw new DataException(DataException.UNKNOWN);
			}
		}
		protected void insertLiquidationItem(JdbcDataConnection dc,MAXLiquidationReport liquidationReport) throws DataException {
			SQLInsertStatement sql = new SQLInsertStatement();
			
			sql.setTable(TABLE_LIQUIDATION_REPORT);

			sql.addColumn(FIELD_RETAIL_STOREID, getStoreID(liquidationReport));
			sql.addColumn(FIELD_BUSINESS_DATE, getBusinessDay(liquidationReport));
			sql.addColumn(FIELD_TRANSACTION_ID, getTransactionID(liquidationReport));
			//String bizdate=getBusinessDay(liquidationReport);
			//String registerId=getWorkstationID(liquidationReport);
			sql.addColumn(FIELD_ID_WS, getWorkstationID(liquidationReport));
			sql.addColumn(FIELD_LIQ_BARCODE, getLiquidationbarcode(liquidationReport));
			sql.addColumn(FIELD_ITEM_PRICE,getItemprice(liquidationReport));
			sql.addColumn( FIELD_ITEM_ID,getItemId(liquidationReport));
			
			
//			
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

	
	protected String getStoreID(MAXLiquidationReport liquidationReport) {
		return ("'" + liquidationReport.getStoreID() + "'");
	}
	protected String getItemId(MAXLiquidationReport liquidationReport) {
		return ("'" + liquidationReport.getItemId() + "'");
	}
	protected String getLiquidationbarcode(MAXLiquidationReport liquidationReport) {
		return ("'" + liquidationReport.getLiquidationbarcode() + "'");
	}
	protected String getItemprice(MAXLiquidationReport liquidationReport) {
		return ("'" + liquidationReport.getItemprice() + "'");
	}

	protected String getWorkstationID(MAXLiquidationReport liquidationReport) {
		return ("'" + liquidationReport.getWsID() + "'");
	}

	protected String getBusinessDay(MAXLiquidationReport liquidationReport) {
		return dateToSQLDateString(liquidationReport.getBusinessDay());
	}

	protected String getTransactionID(MAXLiquidationReport liquidationReport) {
		return ("'" + liquidationReport.getTransactionID() + "'");
	}
	
	
		
	
	

}
