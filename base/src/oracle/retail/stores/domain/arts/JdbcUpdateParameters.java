/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateParameters.java /main/13 2013/09/05 10:36:17 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
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
 *    4    360Commerce 1.3         1/25/2006 4:11:27 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:53 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:06 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:55    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:53     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:25  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   May 20 2003 13:06:30   cdb
 * Initial revision.
 * Resolution for 1930: RE-FACTORING AND FEATURE ENHANCEMENTS TO PARAMETER SUBSYSTEM
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.Enumeration;
import java.util.Hashtable;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.utility.UpdateMessage;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.manager.parameter.Parameter;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.parameter.Settings;
import oracle.retail.stores.foundation.manager.parameter.Source;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation performs data operations pertaining to parameter updates.
 * 
 * @version $Revision: /main/13 $
 */
public class JdbcUpdateParameters extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -6026485940567858324L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcUpdateParameters.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    // Table names
    public static final String REGISTER_TABLE                    = TABLE_WORKSTATION;

    // Column names
    public static final String ACCOUNTABILITY_COLUMN             = FIELD_WORKSTATION_ACCOUNTABILITY;

    public static final String COUNT_TILL_AT_RECONCILE_COLUMN    = FIELD_WORKSTATION_COUNT_TILL_AT_RECONCILE;
    public static final String COUNT_FLOAT_AT_OPEN_COLUMN        = FIELD_WORKSTATION_COUNT_FLOAT_AT_OPEN;
    public static final String COUNT_FLOAT_AT_RECONCILE_COLUMN   = FIELD_WORKSTATION_COUNT_FLOAT_AT_RECONCILE;
    public static final String COUNT_CASH_LOAN_COLUMN            = FIELD_WORKSTATION_COUNT_CASH_LOAN;
    public static final String COUNT_CASH_PICKUP_COLUMN          = FIELD_WORKSTATION_COUNT_CASH_PICKUP;
    public static final String COUNT_CHECK_PICKUP_COLUMN         = FIELD_WORKSTATION_COUNT_CHECK_PICKUP;

    public static final String TILL_RECONCILE_COLUMN             = FIELD_WORKSTATION_TILL_RECONCILE;
    public static final String FLOAT_AMOUNT_COLUMN               = FIELD_WORKSTATION_TILL_FLOAT_AMOUNT;
    // End column names

    // Parameter names and values
    // Accountability: 0 - register, 1 - cashier
    public static final String REGISTER                   = "Register";
    public static final String CASHIER                    = "Cashier";
    public static final String ACCOUNTABILITY             = "Accountability";

    // Counts: 0 - none, 1 - summary, 2 - detail
    public static final String NONE                       = "No";
    public static final String SUMMARY                    = "Summary";
    public static final String DETAIL                     = "Detail";
    public static final String COUNT_TILL_AT_RECONCILE    = "CountTillAtReconcile";
    public static final String COUNT_FLOAT_AT_OPEN        = "CountFloatAtOpen";
    public static final String COUNT_FLOAT_AT_RECONCILE   = "CountFloatAtReconcile";
    public static final String COUNT_CASH_LOAN            = "CountCashLoan";
    public static final String COUNT_CASH_PICKUP          = "CountCashPickup";
    public static final String COUNT_CHECK_PICKUP         = "CountCheckPickup";

    public static final String TILL_RECONCILE             = "TillReconcile"; //char 1
    public static final String FLOAT_AMOUNT               = "FloatAmount"; //DECIMAL(13,2)
    // End Parameter names and values

    // Table and Column mappings
    public static Hashtable<String,String> tableMap = null;
    public static Hashtable<String,String> columnMap = null;

    // Initialize table and column mappings
    static
    {
        columnMap = new Hashtable<String,String>(9);
        columnMap.put(ACCOUNTABILITY, ACCOUNTABILITY_COLUMN);
        columnMap.put(COUNT_TILL_AT_RECONCILE, COUNT_TILL_AT_RECONCILE_COLUMN);
        columnMap.put(COUNT_FLOAT_AT_OPEN, COUNT_FLOAT_AT_OPEN_COLUMN);
        columnMap.put(COUNT_FLOAT_AT_RECONCILE, COUNT_FLOAT_AT_RECONCILE_COLUMN);
        columnMap.put(COUNT_CASH_LOAN, COUNT_CASH_LOAN_COLUMN);
        columnMap.put(COUNT_CASH_PICKUP, COUNT_CASH_PICKUP_COLUMN);
        columnMap.put(COUNT_CHECK_PICKUP, COUNT_CHECK_PICKUP_COLUMN);
        columnMap.put(TILL_RECONCILE, TILL_RECONCILE_COLUMN);
        columnMap.put(FLOAT_AMOUNT, FLOAT_AMOUNT_COLUMN);

        tableMap = new Hashtable<String,String>(9);
        tableMap.put(COUNT_TILL_AT_RECONCILE, REGISTER_TABLE);
        tableMap.put(ACCOUNTABILITY, REGISTER_TABLE);
        tableMap.put(COUNT_FLOAT_AT_OPEN, REGISTER_TABLE);
        tableMap.put(COUNT_FLOAT_AT_RECONCILE, REGISTER_TABLE);
        tableMap.put(COUNT_CASH_LOAN, REGISTER_TABLE);
        tableMap.put(COUNT_CASH_PICKUP, REGISTER_TABLE);
        tableMap.put(COUNT_CHECK_PICKUP, REGISTER_TABLE);
        tableMap.put(TILL_RECONCILE, REGISTER_TABLE);
        tableMap.put(FLOAT_AMOUNT, REGISTER_TABLE);

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
        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateParameters.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        UpdateMessage updateMessage = (UpdateMessage) action.getDataObject();
        updateParameters(connection, updateMessage);

        if (logger.isDebugEnabled()) logger.debug( "JdbcUpdateParameters.execute()");
    }

    /**
     * Updates parameters. The previous parameter values are lost.
     * 
     * @param dataConnection connection to the db
     * @param updateMessage object containing parameters and additional database
     *            keys
     * @exception DataException upon error
     */
    public void updateParameters(JdbcDataConnection dataConnection,
                                 UpdateMessage updateMessage)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcUpdateParameters.updateParameters()");
        // update records
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Parameter values are contained in the Source object
        Source source = (Source)updateMessage.getDataObject();

        // Determines if the table name has been set in the udpate sql
        boolean tableSet = false;

        // Get the group keys from the source
        Enumeration groups = null;
        try
        {
            groups = source.getSettingsKeys();
        }
        catch (ParameterException pe)
        {
            throw new DataException(DataException.SQL_ERROR,
                                    "JdbcUpdateParameters",
                                    pe);
        }

        // Should be only one group per update
        if (groups.hasMoreElements())
        {
            String groupName = (String)groups.nextElement();
            Settings settings = null;
            // Get the group (settings) object
            try
            {
                settings = (Settings)source.getSettings(groupName);
            }
            catch (ParameterException pe)
            {
                throw new DataException(DataException.SQL_ERROR,
                                        "JdbcUpdateParameters",
                                        pe);
            }
            // Add the parameter values to the sql
            for (Enumeration parameters = settings.getParameterKeys(); parameters.hasMoreElements();)
            {
                String parameterName = (String)parameters.nextElement();
                Parameter parameter = null;
                // Get the parameter to update
                try
                {
                    parameter = (Parameter)settings.getParameter(parameterName);
                }
                catch (ParameterException pe)
                {
                    throw new DataException(DataException.SQL_ERROR,
                                            "JdbcUpdateParameters",
                                            pe);
                }

                // Underlying assumption is that all the parameters are going
                // to reside in a single table. Can expand this section of code
                // if this assumption changes.
                if (!tableSet)
                {
                    // Table
                    sql.setTable(getTableName(parameter.getName()));
                }

                // The format of the sql statement depends on the parameter
                // name
                if (ACCOUNTABILITY.equals(parameter.getName()))
                {
                    addAccountabilityColumn(sql,parameter);
                }
                else if (FLOAT_AMOUNT.equals(parameter.getName()))
                {
                    sql.addColumn(getColumnName(parameter.getName()),
                                  makeSafeString((String)parameter.getValues()[0]));
                }
                else if (TILL_RECONCILE.equals(parameter.getName()))
                {
                    sql.addColumn(getColumnName(parameter.getName()),
                                  getFlag((String)parameter.getValues()[0]));
                }
                else
                {
                    addCountColumn(sql, parameter);
                }
            }
        }

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + makeSafeString(updateMessage.getProperty(ParameterTransaction.STORE_ID)));
        sql.addQualifier(FIELD_WORKSTATION_CLASSIFICATION + " = " + makeSafeString(updateMessage.getProperty(ParameterTransaction.WORKSTATION_CLASSIFICATION)));

        try
        {
            dataConnection.execute(sql.getSQLString());

            // insert if no updates
            if (0 == dataConnection.getUpdateCount())
            {
                logger.error( "" + "No register settings updated." + "");
            }
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN,
                                    "JdbcUpdateParameters",
                                    e);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcUpdateParameters.updateParameters()");
    }

    /**
     * Return the table name corresponding to a given parameter
     * 
     * @param parameterName Name of parameter whose table is sought
     * @return the table name
     */
    public static String getTableName(String parameterName)
    {
        return tableMap.get(parameterName);
    }

    /**
     * Return the column name corresponding to a given parameter
     * 
     * @param parameterName Name of parameter whose column is sought
     * @return the column name
     */
    public static String getColumnName(String parameterName)
    {
        return columnMap.get(parameterName);
    }

    /**
     * Returns the value of the flag
     * 
     * @param boolean boolean flag to convert: true(Y) is 1, false(N) is 0.
     * @return the string representation of the flag
     * @exception DataException if the flag value is not valid
     */
    protected String getFlag(String flag)
        throws DataException
    {
        String value = null;

        if ("Y".equals(flag))
        {
            value = "'1'";
        }
        else if ("N".equals(flag))
        {
            value = "'0'";
        }
        else
        {
            throw new DataException(DataException.UNKNOWN,
                                    "JdbcUpdateParameters: Invalid boolean value in parameter ["
                                    + flag + "]");
        }
        return(value);
    }

    /**
     * Converts parameter value to appropriate accountability code
     * 
     * @param sql sql to append column value
     * @param parameter containing value to update
     * @exception DataException if the code is not valid
     */
    protected void addAccountabilityColumn(SQLUpdateStatement sql, Parameter parameter)
    throws DataException
    {
        String value = null;

        if (CASHIER.equals(parameter.getValues()[0]))
        {
            value = "'1'";
        }
        else if (REGISTER.equals(parameter.getValues()[0]))
        {
            value = "'0'";
        }
        else
        {
            throw new DataException(DataException.UNKNOWN,
                                    "JdbcUpdateParameters: Invalid accountability value in parameter ["
                                    + parameter.getValues()[0] + "]");
        }
        sql.addColumn(getColumnName(parameter.getName()),
                      value);
    }

    /**
     * Converts parameter value to appropriate count code
     * 
     * @param sql sql to append column value
     * @param parameter containing value to update
     * @exception DataException if the code is not valid
     */
    protected void addCountColumn(SQLUpdateStatement sql, Parameter parameter)
    throws DataException
    {
        String value = null;

        if (NONE.equals(parameter.getValues()[0]))
        {
            value = "'0'";
        }
        else if (SUMMARY.equals(parameter.getValues()[0]))
        {
            value = "'1'";
        }
        else if (DETAIL.equals(parameter.getValues()[0]))
        {
            value = "'2'";
        }
        else
        {
            throw new DataException(DataException.UNKNOWN,
                                    "JdbcUpdateParameters: Invalid count value in parameter ["
                                    + parameter.getValues()[0] + "]");
        }

        sql.addColumn(getColumnName(parameter.getName()),
                      value);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

    /**
     * Returns the string representation of this object.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcUpdateParameters",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
    public static void main(String args[])
    {
        new JdbcUpdateParameters();
    }

}
