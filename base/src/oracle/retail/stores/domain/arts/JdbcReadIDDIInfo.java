/* ===========================================================================
* Copyright (c) 2007, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadIDDIInfo.java /main/13 2013/09/05 10:36:19 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  03/07/13 - SQLSelectStatement is used instead of TableQueryInfo
 *                         so that multiple tables can be used.
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  6    360Commerce 1.5         6/4/2007 9:52:23 PM    Tony Zgarba     Fixed a
 *        threading issue that occurred during concurring writes.
 *  5    360Commerce 1.4         5/28/2007 5:34:01 AM   Naveen Ganesh   Removed
 *        Column Names and Column Type from dataVector
 *  4    360Commerce 1.3         5/24/2007 8:53:56 AM   Naveen Ganesh   Removed
 *        RowCount
 *  3    360Commerce 1.2         5/18/2007 9:48:24 AM   Naveen Ganesh   Handled
 *        Date, Time, TimeStamp data retrieval
 *  2    360Commerce 1.1         5/14/2007 4:25:21 PM   Michael P. Barnett Used
 *        logger instead of System.out.
 *  1    360Commerce 1.0         5/9/2007 5:07:13 PM    Naveen Ganesh   
 * $
 *  2    360Commerce1.1         5/15/2007 2:55:21 AM   Michael P. Barnett Used
 *       logger instead of System.out.
 *  1    360Commerce1.0         5/10/2007 3:37:13 AM   Naveen Ganesh
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.foundation.iddi.TableQueryInfo;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This operation reads the table data based on the TableQueryInfo object
 */
public abstract class JdbcReadIDDIInfo extends JdbcDataOperation implements ARTSDatabaseIfc
{

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadIDDIInfo.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    private final int ZERO = 0;

    private String[] dateColumnTypes = { "DATE" };

    private String[] timeColumnTypes = { "TIME" };

    private String[] timestampColumnTypes = { "TIMESTAMP", "DATETIME", "SMALLDATETIME" };

    /**
     * Class constructor.
     */
    public JdbcReadIDDIInfo()
    {
    }

    /**
     * Selects table data based on the coulumns, qualifier data passed through
     * tableQueryInfo
     * 
     * @deprecated As of release 14.0, replace by
     *             {@link #selectIDDIInfo(JdbcDataConnection, SQLSelectStatement)}
     * @param dataConnection connection to the db
     */
    @Deprecated
    protected Vector selectIDDIInfo(JdbcDataConnection dataConnection, TableQueryInfo tableQueryInfo)
            throws DataException
    {

        Vector dataVector = new Vector(0);

        try
        {
            int totalColumnCount = ZERO;

            // columnSet contains the column names of the table
            ArrayList columnSet = new ArrayList();

            // columnType contains the column datatype of the table
            ArrayList columnType = new ArrayList();

            SQLSelectStatement sql = new SQLSelectStatement();

            sql.setTable(tableQueryInfo.getTableName());

            String[] fields = tableQueryInfo.getTableFields();

            // Set the Table fields. If there is no field information, set the
            // table field as *
            if (fields != null)
            {
                for (int fieldCounter = ZERO; fieldCounter < fields.length;)
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

            if (rs != null)
            {

                totalColumnCount = rs.getMetaData().getColumnCount();
                // Add ColumnNames and ColumnTypes into columnSet and columnType
                // respectively
                for (int columnCounter = 1; columnCounter <= totalColumnCount; columnCounter++)
                {

                    columnSet.add(rs.getMetaData().getColumnName(columnCounter));
                    columnType.add(rs.getMetaData().getColumnTypeName(columnCounter));
                }

                // Iterate over the ResultSet and add the resultset data in the
                // form of arraylist
                // to vector to be returned
                while (rs.next())
                {

                    ArrayList dataRow = new ArrayList();
                    for (int columnIndex = 1; columnIndex <= totalColumnCount; columnIndex++)
                    {
                        boolean isDateColumn = false;
                        boolean isTimeColumn = false;
                        boolean isTimeStampColumn = false;
                        // Check if the columntype is one of the Date types
                        for (int columnTypeCounter = 0; columnTypeCounter < dateColumnTypes.length; columnTypeCounter++)
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
                        }
                        else
                        {
                            dataValue = rs.getString(columnIndex);
                        }
                        // add the data
                        dataRow.add(dataValue);
                    }

                    dataVector.add(dataRow);

                }

                rs.close();

            }
        }
        catch (SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(DataException.SQL_ERROR, "JdbcReadIDDIInfo", se);
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "JdbcReadIDDIInfo", e);
        }

        if (dataVector.isEmpty())
        {
            logger.warn("No data found");
            throw new DataException(DataException.NO_DATA, "No data found");

        }

        return dataVector;
    }

    /**
     * Selects table data based on the coulumns, qualifier data passed through
     * tableQueryInfo
     * 
     * @param dataConnection connection to the db
     */
    protected Vector selectIDDIInfo(
        JdbcDataConnection dataConnection,
        SQLSelectStatement selectStatement)
        throws DataException
    {

        Vector dataVector = new Vector(4);

        try
        {
        	int totalColumnCount = ZERO ;

        	// columnSet contains the column names of the table
            ArrayList columnSet = new ArrayList();

            // columnType contains the column datatype of the table
            ArrayList columnType = new ArrayList();

           	logger.debug(selectStatement.getSQLString());

           	//Execute the query
        	dataConnection.execute(selectStatement.getSQLString());

        	// Get Result set from after execution
            ResultSet rs = (ResultSet) dataConnection.getResult();

            if (rs!=null)
            {

            	totalColumnCount= rs.getMetaData().getColumnCount();
	            // Add ColumnNames and ColumnTypes into columnSet and columnType respectively
	            for (int columnCounter=1;columnCounter<=totalColumnCount;columnCounter++)
	            {

	            	columnSet.add(rs.getMetaData().getColumnName(columnCounter));
	            	columnType.add(rs.getMetaData().getColumnTypeName(columnCounter));
	            }


	            // Iterate over the ResultSet and add the resultset data in the form of arraylist
	            // to vector to be returned
	            while (rs.next())
	            {

	                ArrayList dataRow = new ArrayList();
	            	for (int columnIndex=1;columnIndex<=totalColumnCount;columnIndex++)
	            	{
			            boolean isDateColumn = false;
			            boolean isTimeColumn = false;
			            boolean isTimeStampColumn = false;
	            		// Check if the columntype is one of the Date types
	            		for (int columnTypeCounter=0;columnTypeCounter<dateColumnTypes.length; columnTypeCounter++)
	            		{

	            			if (rs.getMetaData().getColumnTypeName(columnIndex).equalsIgnoreCase(dateColumnTypes[columnTypeCounter]))
	            			{
	            				isDateColumn = true;
	            				break;
	            			}
	            		}

	            		// Check if the columntype is one of the Timestamp types
	            		for (int columnTypeCounter=ZERO;columnTypeCounter<timestampColumnTypes.length; columnTypeCounter++)

	            		{
	            			if (rs.getMetaData().getColumnTypeName(columnIndex).equalsIgnoreCase(timestampColumnTypes[columnTypeCounter]))
	            			{
	            				isTimeStampColumn = true;

	            				break;
	            			}
	            		}
	            		// Check if the columntype is one of the Time types
	            		for (int columnTypeCounter=ZERO;columnTypeCounter<timeColumnTypes.length; columnTypeCounter++)
	            		{
	            			if (rs.getMetaData().getColumnTypeName(columnIndex).equalsIgnoreCase(timeColumnTypes[columnTypeCounter]))
	            			{
	            				isTimeColumn = true;

	            				break;
	            			}
	            		}

            			String dataValue = null;
            			// If the columntype is Date Type
	            		if(isDateColumn)
	            		{

	            			Date dt = rs.getDate(columnIndex);

	            			if(dt!=null)
	            			{
	            				dataValue = dt.toString();
	            			}

	            		}
            			// If the columntype is Time Type
	            		else if(isTimeColumn)
	            		{
	            			Time tm = rs.getTime(columnIndex);
	            			if (tm!=null)
	            			{
	            				dataValue = tm.toString();
	            			}
	            		}
            			// If the columntype is Timestamp Type
	            		else if(isTimeStampColumn)
	            		{
	            			Timestamp ts = rs.getTimestamp(columnIndex);
	            			if (ts!=null)
	            			{
	            				dataValue = ts.toString();
	            			}
	            		}
	            		else
	            		{
	            			dataValue = rs.getString(columnIndex);
	            		}
	            		// add the data
	            		dataRow.add(dataValue);
	            	}

	            	dataVector.add(dataRow);

	            }

	            rs.close();

            }
        }
        catch (SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(
                DataException.SQL_ERROR,
                "JdbcReadIDDIInfo",
                se);
        }
        catch(Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(
                DataException.UNKNOWN,
                "JdbcReadIDDIInfo",
                e);
        }

        if (dataVector.isEmpty())
        {
            logger.warn("No data found");
            throw new DataException(
                DataException.NO_DATA,
                "No data found");

        }

        return dataVector;
    }


}
