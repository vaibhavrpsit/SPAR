/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveIDDIRegisterVersion.java /main/13 2012/05/21 15:50:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    nganesh   04/08/09 - Added record added and updated dates
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         5/24/2007 7:53:20 AM   Naveen Ganesh
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.foundation.iddi.ifc.DataSetInfoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts and updates in the IDDI version table
 * 
 * @version $Revision: /main/13 $
 */
public class JdbcSaveIDDIRegisterVersion extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1310683645341662477L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveIDDIRegisterVersion.class);

    /**
     * Class constructor.
     */
    public JdbcSaveIDDIRegisterVersion()
    {
        super();
        setName("JdbcSaveIDDIRegisterVersion");
    }

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
                        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveIDDIRegisterVersion.execute()");


        // Get the connection object
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Call saveIDDIVersion method to save the dataset version information


        saveIDDIRegisterVersion(connection,(DataSetInfoIfc) action.getDataObject());

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveIDDIRegisterVersion.execute()");
    }

    /**
        Saves the Dataset version.  This method first tries to update
        the dataset version.  If that fails, it will attempt to insert the
        dataset version.
        @param  dataConnection  The connection to the data source
        @param  datasetinformation
        @exception DataException
     */
    public void saveIDDIRegisterVersion(JdbcDataConnection dataConnection,
    								DataSetInfoIfc dataSetInfo)
                                      throws DataException
    {
        /*
         * If the update fails, then try to insert the version
         */
        try
        {
        	updateIDDIRegVersion(dataConnection,dataSetInfo);

        }
        catch (DataException de)
        {
        	insertIDDIRegVersion(dataConnection, dataSetInfo);
        }
        catch (Exception e)
        {
            logger.error( "Couldn't save IDDI Register Version.");
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,
                                    "Couldn't save IDDI Register Version.",
                                    e);
        }
    }

    /**
        Inserts into the ma_vn_iddi table.
        <P>
        @param  dataConnection  the connection to the data source
        @param  datasetinformation
        @exception DataException
     */
    public void insertIDDIRegVersion(JdbcDataConnection dataConnection,
    									DataSetInfoIfc dataSetInfo)
                                        throws DataException
    {


    	SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_IDDI_REG_VERSION);

        // Set the field data to be inserted
        sql.addColumn(FIELD_DATASET_ID, dataSetInfo.getDataSetID());
        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(dataSetInfo.getStoreID()));
        sql.addColumn(FIELD_WORKSTATION_ID,makeSafeString(dataSetInfo.getRegisterID()));
        sql.addColumn(FIELD_BATCH_ID, makeSafeString(dataSetInfo.getBatchID()));

        String dateTime = getSQLCurrentTimestampFunction();
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, dateTime);
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, dateTime);

        try
        {
        	// Insert data
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertIDDIRegVersion", e);
        }


    }


    /**
        Updates the ma_vn_iddi table.
        <P>
        @param  dataConnection
        @param  datasetinfo
        @exception DataException
     */
    public void updateIDDIRegVersion(JdbcDataConnection dataConnection,
    									DataSetInfoIfc dataSetInfo)
                                        throws DataException
    {

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_IDDI_REG_VERSION);


        // Data to be updated
        sql.addColumn(FIELD_BATCH_ID, JdbcUtilities.makeSafeString(dataSetInfo.getBatchID()));

        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // Field Qualifiers
        sql.addQualifier(FIELD_DATASET_ID + " = " + dataSetInfo.getDataSetID());
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + JdbcUtilities.makeSafeString(dataSetInfo.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + JdbcUtilities.makeSafeString(dataSetInfo.getRegisterID()));

        try
        {
        	// update dataset version
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "updateIDDIRegVersion", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "updateIDDIRegVersion");
        }
    }



}

