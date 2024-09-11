/* ===========================================================================
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadIDDIData.java /main/14 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  03/07/13 - SQLSelectStatement is used instead of TableQueryInfo
 *                         so that multiple tables can be used.
 *    cgreene   08/02/11 - tweaks to code to reduce amount of memory usage
 *                         while extracting IDDI
 *    hyin      03/09/11 - remove deprecated method
 *    hyin      03/08/11 - minor changes
 *    hyin      03/01/11 - add handle for BLOB data
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  02/18/09 - Fixed max batch size issue
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         6/4/2007 9:52:23 PM    Tony Zgarba     Fixed a
 *        threading issue that occurred during concurring writes.
 *  4    360Commerce 1.3         6/4/2007 12:23:48 AM   Naveen Ganesh   Added
 *       code documentation
 *  3    360Commerce 1.2         5/30/2007 7:52:51 AM   Manikandan Chellapan
 *       Added batch size limitation
 *  2    360Commerce 1.1         5/29/2007 4:32:36 PM   Michael P. Barnett Read
 *        IDDI batch size from properties file.
 *  1    360Commerce 1.0         5/28/2007 5:27:50 AM   Naveen Ganesh
 * $
 *  2    360Commerce1.1         5/15/2007 2:55:21 AM   Michael P. Barnett Used
 *       logger instead of System.out.
 *  1    360Commerce1.0         5/10/2007 3:37:13 AM   Naveen Ganesh
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.foundation.iddi.TableQueryInfo;
import oracle.retail.stores.foundation.iddi.ifc.IDDIWriterIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation reads the table data based on the TableQueryInfo object
 */
public abstract class JdbcReadIDDIData
    extends JdbcDataOperation
    implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 3095979504628148723L;
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(JdbcReadIDDIData.class);
    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/14 $";

    protected static final String[] dateColumnTypes = {"DATE"};
    protected static final String[] timeColumnTypes = {"TIME"};
    protected static final String[] timestampColumnTypes = {"TIMESTAMP","DATETIME","SMALLDATETIME"};
    protected static final String[] blobColumnTypes = {"BLOB"};

    /**
     * Maximum supported Batch size is 10,000.
     */
    public static final int MAX_BATCH_SIZE = 10000;

    /**
     * Default Batch Size if not specified is 1,000.
     */
    public static final int DEFAULT_BATCH_SIZE = 1000;

    protected static final int ZERO = 0;
    protected int fileWriteBatchSize = 0;

    /**
     * Class constructor.
     */
    public JdbcReadIDDIData()
    {

        String batchSize;

        // This setting resides in domain.properties
        batchSize =
            Gateway.getProperty("domain", "IDDIBatchSize", "1");

        // Try to convert property to integer value
        try
        {
            fileWriteBatchSize = Integer.parseInt(batchSize);
        }
        // If no valid value in the properties file
        catch (NumberFormatException nfe)
        {
            fileWriteBatchSize = DEFAULT_BATCH_SIZE;
        }

        if(fileWriteBatchSize < 0 || fileWriteBatchSize > MAX_BATCH_SIZE)
        {
            fileWriteBatchSize = MAX_BATCH_SIZE;
        }
    }


    /**
     * Selects table data based on the columns, qualifier data passed through
     * tableQueryInfo
     * 
     * @deprecated As of release 14.0, replace by
     *             {@link #selectIDDIData(JdbcDataConnection, SQLSelectStatement, IDDIWriterIfc)}
     * @param dataConnection
     * @return
     * @throws DataException
     */
    @Deprecated
    protected Vector<Object> selectIDDIData(JdbcDataConnection dataConnection, TableQueryInfo tableQueryInfo,
            IDDIWriterIfc writer) throws DataException
    {
        Vector<Object> dataVector = new Vector<Object>(0);

        try
        {
            int totalColumnCount = ZERO;
            long totalRowCount = ZERO;

            SQLSelectStatement sql = new SQLSelectStatement();

            sql.setTable(tableQueryInfo.getTableName());

            String[] fields = tableQueryInfo.getTableFields();

            // Set the Table fields. If there is no field information, set the
            // table field as *
            if (fields != null)
            {
                for (int fieldCounter = 0; fieldCounter < fields.length;)
                {
                    sql.addColumn(fields[fieldCounter++]);
                }
            }
            else
            {
                sql.addColumn("*");
            }

            // Add qualifiers to the sql object
            if (tableQueryInfo.getQueryFilter() != null)
            {

                for (int queryInfo = ZERO; queryInfo < (tableQueryInfo.getQueryFilter().length); queryInfo++)
                {
                    // Add qualifier
                    sql.addQualifier(tableQueryInfo.getQueryFilter()[queryInfo]);

                }

            }

            // Add non qualifiers to the sql object
            if (tableQueryInfo.getQueryNonFilter() != null)
            {

                for (int queryInfo = ZERO; queryInfo < (tableQueryInfo.getQueryNonFilter().length); queryInfo++)
                {
                    // Add non qualifier
                    sql.addQualifier(tableQueryInfo.getQueryNonFilter()[queryInfo]);
                }

            }

            // Add Order By clause
            if (tableQueryInfo.getOrderBy() != null)
            {
                sql.addOrdering(tableQueryInfo.getOrderBy());
            }

            logger.debug(sql.getSQLString());

            // Execute the query
            dataConnection.execute(sql.getSQLString());

            // Get Result set from after execution
            ResultSet rs = (ResultSet)dataConnection.getResult();

            // Open File for writing data
            writer.open();

            if (rs != null)
            {
                totalColumnCount = rs.getMetaData().getColumnCount();

                // columnSet contains the column names of the table
                List<String> columnSet = new ArrayList<String>(totalColumnCount);

                // columnType contains the column datatype of the table
                List<String> columnType = new ArrayList<String>(totalColumnCount);

                // Add ColumnNames and ColumnTypes into columnSet and columnType
                // respectively
                for (int columnCounter = 1; columnCounter <= totalColumnCount; columnCounter++)
                {

                    columnSet.add(rs.getMetaData().getColumnName(columnCounter));
                    columnType.add(rs.getMetaData().getColumnTypeName(columnCounter));
                }

                dataVector.add(columnSet);
                dataVector.add(columnType);

                // Iterate over the ResultSet and add the resultset data in the
                // form of arraylist to vector to be returned
                List<List<String>> dataRows = new ArrayList<List<String>>(fileWriteBatchSize);
                while (rs.next())
                {
                    totalRowCount++;
                    List<String> dataRow = new ArrayList<String>();
                    for (int columnIndex = 1; columnIndex <= totalColumnCount; columnIndex++)
                    {
                        boolean isDateColumn = false;
                        boolean isTimeColumn = false;
                        boolean isTimeStampColumn = false;
                        boolean isBlobColumn = false;

                        // Check if the columntype is one of the Date types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < dateColumnTypes.length; columnTypeCounter++)
                        {

                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(dateColumnTypes[columnTypeCounter]))
                            {
                                isDateColumn = true;
                                break;
                            }
                        }

                        // Check if the columntype is one of the Timestamp types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < timestampColumnTypes.length; columnTypeCounter++)

                        {
                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(timestampColumnTypes[columnTypeCounter]))
                            {
                                isTimeStampColumn = true;

                                break;
                            }
                        }
                        // Check if the columntype is one of the Time types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < timeColumnTypes.length; columnTypeCounter++)
                        {
                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(timeColumnTypes[columnTypeCounter]))
                            {
                                isTimeColumn = true;

                                break;
                            }
                        }

                        // Check if the columntype is one of the BLOB types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < blobColumnTypes.length; columnTypeCounter++)
                        {
                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(blobColumnTypes[columnTypeCounter]))
                            {
                                isBlobColumn = true;

                                break;
                            }
                        }

                        String dataValue = null;
                        // If the columntype is Date Type
                        if (isDateColumn)
                        {
                            Date dt = rs.getDate(columnIndex);

                            if (dt != null)
                            {
                                dataValue = dt.toString();
                            }
                        }
                        // If the columntype is Time Type
                        else if (isTimeColumn)
                        {
                            Time tm = rs.getTime(columnIndex);
                            if (tm != null)
                            {
                                dataValue = tm.toString();
                            }
                        }
                        // If the columntype is Timestamp Type
                        else if (isTimeStampColumn)
                        {
                            Timestamp ts = rs.getTimestamp(columnIndex);
                            if (ts != null)
                            {
                                dataValue = ts.toString();
                            }
                            // if the columntype is Blob Type
                        }
                        else if (isBlobColumn)
                        {
                            Blob blob = rs.getBlob(columnIndex);
                            if (blob != null)
                            {
                                dataValue = convertBlobToString(blob.getBytes(1L, (int)blob.length()));
                            }
                        }
                        else
                        {
                            dataValue = rs.getString(columnIndex);
                        }
                        // add the data
                        dataRow.add(dataValue);
                    }

                    dataRows.add(dataRow);

                    if (totalRowCount % fileWriteBatchSize == ZERO)
                    {
                        writer.write(columnType, dataRows);
                        dataRows.clear();
                    }
                }

                if (dataRows.size() > ZERO)
                {
                    writer.write(columnType, dataRows);
                    dataRows.clear();
                }

                rs.close();
                writer.close();
                dataRows = null;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception occured in JdbcReadIDDIData selectIDDIData()", se);
            throw new DataException(DataException.SQL_ERROR, "SelectIDDIData", se);
        }
        catch (Exception e)
        {
            logger.error("Unknown exception occured in JdbcReadIDDIData selectIDDIData()", e);
            throw new DataException(DataException.UNKNOWN, "SelectIDDIData", e);
        }

        return dataVector;
    }

    /**
     * Selects table data based on the columns, qualifier data passed through tableQueryInfo
     *
     * @param dataConnection
     * @return
     * @throws DataException
     */
    protected Vector<Object> selectIDDIData(
        JdbcDataConnection dataConnection,
        SQLSelectStatement selectStatement,
        IDDIWriterIfc writer)
        throws DataException
    {
        Vector<Object> dataVector = new Vector<Object>(4);

        try
        {
            int totalColumnCount = ZERO;
            long totalRowCount = ZERO;

            logger.debug(selectStatement.getSQLString());

            // Execute the query
            dataConnection.execute(selectStatement.getSQLString());

            // Get Result set from after execution
            ResultSet rs = (ResultSet) dataConnection.getResult();

            // Open File for writing data
            writer.open();

            if (rs!=null)
            {
                totalColumnCount = rs.getMetaData().getColumnCount();

                // columnSet contains the column names of the table
                List<String> columnSet = new ArrayList<String>(totalColumnCount);

                // columnType contains the column datatype of the table
                List<String> columnType = new ArrayList<String>(totalColumnCount);

                // Add ColumnNames and ColumnTypes into columnSet and columnType respectively
                for (int columnCounter = 1; columnCounter <= totalColumnCount; columnCounter++)
                {

                    columnSet.add(rs.getMetaData().getColumnName(columnCounter));
                    columnType.add(rs.getMetaData().getColumnTypeName(columnCounter));
                }

                dataVector.add(columnSet);
                dataVector.add(columnType);

                // Iterate over the ResultSet and add the resultset data in the
                // form of arraylist to vector to be returned
                List<List<String>> dataRows = new ArrayList<List<String>>(fileWriteBatchSize);
                while (rs.next())
                {
                    totalRowCount++;
                    List<String> dataRow = new ArrayList<String>();
                    for (int columnIndex = 1; columnIndex <= totalColumnCount; columnIndex++)
                    {
                        boolean isDateColumn = false;
                        boolean isTimeColumn = false;
                        boolean isTimeStampColumn = false;
                        boolean isBlobColumn = false;
                        
                        // Check if the columntype is one of the Date types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < dateColumnTypes.length; columnTypeCounter++)
                        {

                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(dateColumnTypes[columnTypeCounter]))
                            {
                                isDateColumn = true;
                                break;
                            }
                        }

                        // Check if the columntype is one of the Timestamp types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < timestampColumnTypes.length; columnTypeCounter++)

                        {
                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(timestampColumnTypes[columnTypeCounter]))
                            {
                                isTimeStampColumn = true;

                                break;
                            }
                        }
                        // Check if the columntype is one of the Time types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < timeColumnTypes.length; columnTypeCounter++)
                        {
                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(timeColumnTypes[columnTypeCounter]))
                            {
                                isTimeColumn = true;

                                break;
                            }
                        }

                        // Check if the columntype is one of the BLOB types
                        for (int columnTypeCounter = ZERO; columnTypeCounter < blobColumnTypes.length; columnTypeCounter++)
                        {
                            if (rs.getMetaData().getColumnTypeName(columnIndex)
                                    .equalsIgnoreCase(blobColumnTypes[columnTypeCounter]))
                            {
                                isBlobColumn = true;

                                break;
                            }
                        }

                        String dataValue = null;
                        // If the columntype is Date Type
                        if (isDateColumn)
                        {
                            Date dt = rs.getDate(columnIndex);

                            if (dt != null)
                            {
                                dataValue = dt.toString();
                            }
                        }
                        // If the columntype is Time Type
                        else if (isTimeColumn)
                        {
                            Time tm = rs.getTime(columnIndex);
                            if (tm != null)
                            {
                                dataValue = tm.toString();
                            }
                        }
                        // If the columntype is Timestamp Type
                        else if (isTimeStampColumn)
                        {
                            Timestamp ts = rs.getTimestamp(columnIndex);
                            if (ts != null)
                            {
                                dataValue = ts.toString();
                            }
                            // if the columntype is Blob Type
                        }
                        else if (isBlobColumn)
                        {
                            Blob blob = rs.getBlob(columnIndex);
                            if (blob != null)
                            {
                                dataValue = convertBlobToString(blob.getBytes(1L, (int)blob.length()));
                            }
                        }
                        else
                        {
                            dataValue = rs.getString(columnIndex);
                        }
                        // add the data
                        dataRow.add(dataValue);
                    }

                    dataRows.add(dataRow);

                    if (totalRowCount % fileWriteBatchSize == ZERO)
                    {
                        writer.write(columnType, dataRows);
                        dataRows.clear();
                    }
                }

                if (dataRows.size() > ZERO)
                {
                    writer.write(columnType, dataRows);
                    dataRows.clear();
                }

                rs.close();
                writer.close();
                dataRows = null;
            }
        }
        catch (SQLException se)
        {
            logger.error("SQL Exception occured in JdbcReadIDDIData selectIDDIData()", se);
            throw new DataException(
                DataException.SQL_ERROR,
                "SelectIDDIData",
                se);
        }
        catch(Exception e)
        {
            logger.error("Unknown exception occured in JdbcReadIDDIData selectIDDIData()", e);
            throw new DataException(
                DataException.UNKNOWN,
                "SelectIDDIData",
                e);
        }

        return dataVector;
    }

    /**
     * Derby import utility expects hexadecimal format for blob datatype. This
     * essentially convert byte array to a hexadecimal string. The base64 encode
     * will NOT work with derby import utility.
     * 
     * @param bytes
     * @return hexadecimal string of byte array
     */
    private String convertBlobToString(byte[] bytes)
    {
        if (bytes == null)
            return "";

        char[] hexchars = new char[bytes.length * 2];
        int value = 0;
        int highIndex = 0;
        int lowIndex = 0;
        for (int i = 0; i < bytes.length; i++)
        {
            value = (bytes[i] + 256) % 256;
            highIndex = value >> 4;
            lowIndex = value & 0x0f;
            hexchars[i * 2 + 0] = HEXTABLE[highIndex];
            hexchars[i * 2 + 1] = HEXTABLE[lowIndex];
        }
        return new String(hexchars);
    }

}
