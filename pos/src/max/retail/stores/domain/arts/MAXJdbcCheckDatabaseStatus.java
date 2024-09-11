package max.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.arts.ARTSTill;
import oracle.retail.stores.domain.arts.JdbcDataOperation;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class MAXJdbcCheckDatabaseStatus extends JdbcDataOperation implements ARTSDatabaseIfc
{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TillIfc selectTill(JdbcDataConnection dataConnection,
			String storeID,
			String tillID)
					throws DataException
	{
		SQLSelectStatement sql = new SQLSelectStatement();

		/*
		 * Add the desired tables (and aliases)
		 */
		sql.addTable(TABLE_TILL, ALIAS_TILL);

		/*
		 * Add desired columns
		 */
		
		sql.addColumn(FIELD_TILL_STATUS_CODE);
		sql.addColumn(FIELD_WORKSTATION_ID);
		
		/*
		 * Add Qualifier(s)
		 */
		// Rev	1.1 Starts
		sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));
		sql.addQualifier(FIELD_TENDER_REPOSITORY_ID + " = " + getTillID(tillID));
		// Rev	1.1 Ends
		
		TillIfc till = null;
		try
		{
			dataConnection.execute(sql.getSQLString());

			String signOnOperatorID = null;
			String signOffOperatorID = null;
			ResultSet rs = (ResultSet) dataConnection.getResult();

			if (rs.next())
			{
			}

		}
		 catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                logger.warn(de.toString());
            }
            else
            {
                logger.error("Could not read till " + tillID, de);
            }
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectTill: Till table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectTill: Till table", e);
        }

        return(till);
	}


	/* 
	 * @Override by Nadia.Arora
	 * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
	 */
	@Override
	public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException {
		JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
		ARTSTill artsTill = (ARTSTill) action.getDataObject();
		selectTill(connection,  artsTill.getStoreID(), artsTill.getTillID());
		
	}
	
	// Rev	1.1 Starts
	protected String getStoreID(String storeID)
	{
		return("'" + storeID + "'");
	}
	
	protected String getTillID(String tillID)
	{
		return("'" + tillID + "'");
	}
}
