/* ===========================================================================
* Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadAllDataSets.java /main/6 2013/09/05 10:36:18 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  02/17/09 - Added Dataset name field to ArtsDatabaseIfc
 *    mchellap  02/17/09 - Jdbc to read dataset metadata
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.foundation.iddi.DataSetInfo;
import oracle.retail.stores.foundation.iddi.ifc.DataSetInfoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

//-------------------------------------------------------------------------
/**
 * This class reads dataset information from IDDI data set table
 */
//-------------------------------------------------------------------------
public class JdbcReadAllDataSets extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.JdbcReadAllDataSets.class);

    //---------------------------------------------------------------------
    /**
     * Class constructor.
     */
    //---------------------------------------------------------------------
    public JdbcReadAllDataSets()
    {
        setName("JdbcReadAllDataSets");
    }

    //---------------------------------------------------------------------
    /**
     * Executes the SQL statements against the database.
     * <P>
     *
     * @param dataTransaction
     *          The data transaction
     * @param dataConnection
     *          The connection to the data source
     * @param action
     *          The information passed by the valet
     * @exception DataException
     *              upon error
     */
    //---------------------------------------------------------------------
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadAllDataSets.execute()");

        Vector dataVector = new Vector(1);

        SQLSelectStatement sql = new SQLSelectStatement();

        // Table
        sql.setTable(TABLE_IDDI_DATASET);

        // Data to be read
        sql.addColumn(FIELD_DATASET_ID);
        sql.addColumn(FIELD_DATASET_NAME);
        sql.addColumn(FIELD_RETAIL_STORE_ID);


        try
        {
            // read all datasets
            dataConnection.execute(sql.getSQLString());

            // Get Result set from after execution
            ResultSet rs = (ResultSet) dataConnection.getResult();
            DataSetInfoIfc dataSetInfo = null;
            while (rs.next())
            {
                dataSetInfo = new DataSetInfo();
                dataSetInfo.setDataSetID(getBigDecimal(rs, 1).intValue());
                dataSetInfo.setDataSetName((getSafeString(rs, 2)));
                dataSetInfo.setStoreID(getSafeString(rs, 3));
                dataVector.add(dataSetInfo);
            }
            dataTransaction.setResult(dataVector);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "updateIDDIRegVersion", se);
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "ReadAllDataSets", e);
        }
    }

}
