/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadPricingGroup.java /main/10 2012/08/23 21:53:59 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  08/23/12 - return the list to be consistent with the jpa
 *                         operation
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    mahising  01/14/09 - fixed QA issue
 *    mahising  12/23/08 - fix base issue
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.domain.customer.PricingGroup;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

public class JdbcReadPricingGroup extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -1504348563212736570L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/10 $";

    /**
     * Constructor.
     */
    public JdbcReadPricingGroup()
    {
        setName("JdbcReadPricingGroup");
    }

    /**
     * Execute the SQL statements against the database.
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param action
     * @exception DataException
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        ArrayList<PricingGroupIfc> pricingGroups = null;
        LocaleRequestor   sqlLocale = null;
        if (action.getDataObject() instanceof LocaleRequestor)
        {
            sqlLocale = (LocaleRequestor)action.getDataObject();
        }
        try
        {
            pricingGroups = readPricingGroups(connection, sqlLocale);
        }
        catch (DataException de)
        {
            throw de;
        }
        dataTransaction.setResult(pricingGroups);
    }

    /**
     * Select all Pricing Group from the pricing group table.
     * <P>
     * 
     * @param the data connection on which to execute.
     * @param SQL locale
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the ResultSet
     */
    public ArrayList<PricingGroupIfc> readPricingGroups(JdbcDataConnection dataConnection, LocaleRequestor sqlLocale)
            throws DataException
    {
        ArrayList<PricingGroupIfc> retrievedGroups = null;
		try {
            // build SQL statement, execute and parse result set
			SQLSelectStatement sql = buildPricingGroupsSQLStatement(sqlLocale);

            dataConnection.execute(sql.getSQLString());

            retrievedGroups = parsePricingGroupsResultSet(dataConnection);

		} catch (SQLException se) {
			throw new DataException(DataException.SQL_ERROR,
					"readPricingGroups", se);
		} catch (DataException de) {
            throw de;
		} catch (Exception e) {
			throw new DataException(DataException.UNKNOWN, "readPricingGroups",
					e);
        }
        return (retrievedGroups);
    }

    /**
     * Build the query to select pricing group
     */
    private SQLSelectStatement buildPricingGroupsSQLStatement(LocaleRequestor locale)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_PRICING_GROUP,ALIAS_CUSTOMER_GROUP);
        sql.addTable(TABLE_PRICING_GROUP_I8,ALIAS_CUSTOMER_GROUP_I8 );

        // add columns
        sql.addColumn(ALIAS_CUSTOMER_GROUP,FIELD_CUSTOMER_PRICING_GROUP_ID);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8,FIELD_LOCALE);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8,FIELD_CUSTOMER_PRICING_GROUP_NAME);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8,FIELD_CUSTOMER_PRICING_GROUP_DESCRIPTION);

        // add additional qualifiers
        addCustomerGroupsAdditionalQualifiers(sql, locale);
        // add ordering
        addPricingGroupsOrdering(sql);

        return (sql);
    }

    /**
     * Add additional qualifiers to sql statement.
     *
     * @param sql SQLSelectStatement to modify
     * @param LocaleRequestor locale
     */
    protected void addCustomerGroupsAdditionalQualifiers(SQLSelectStatement sql, LocaleRequestor locale)
    {
        sql.addJoinQualifier(ALIAS_CUSTOMER_GROUP_I8,FIELD_CUSTOMER_PRICING_GROUP_ID,ALIAS_CUSTOMER_GROUP,FIELD_CUSTOMER_PRICING_GROUP_ID);
        Set<Locale> bestMatches = LocaleMap.getBestMatch("", locale.getLocales());
        sql.addQualifier(ALIAS_CUSTOMER_GROUP_I8 + "." +  FIELD_LOCALE + " " +  JdbcDataOperation.buildINClauseString(bestMatches));
    }

    /**
     * Add Pricing Groups in order of the pricing group names.
     * 
     * @param SQLSelectStatement
     */
    protected void addPricingGroupsOrdering(SQLSelectStatement sql)
    {
        sql.addOrdering(FIELD_CUSTOMER_PRICING_GROUP_NAME);
    }

    protected ArrayList<PricingGroupIfc> parsePricingGroupsResultSet(JdbcDataConnection dataConnection) throws SQLException,
            DataException
    {
        ArrayList<PricingGroupIfc> groups = new ArrayList<PricingGroupIfc>();
        ResultSet rs = (ResultSet)dataConnection.getResult();
        PricingGroupIfc group = null;
        int groupId = 0;

        if (rs != null)
        {
            while (rs.next())
            {
                int index = 0;
                groupId = rs.getInt(++index);
                group = ReadARTSCustomerSQL.getPricingGroup(groups, groupId);
                if (group == null)
                {
                    group = new PricingGroup();
                    group.setPricingGroupID(groupId);
                    groups.add(group);
                }
                String localeString = getSafeString(rs, ++index);
                Locale lcl = LocaleUtilities.getLocaleFromString(localeString);
                group.setPricingGroupName(lcl, getSafeString(rs, ++index));
                group.setPricingGroupDescription(lcl, getSafeString(rs, ++index));
            }
            // close result set
            rs.close();
        }

        // handle not found
        if (groups.size() == 0)
        {
            String msg = "JdbcSelectCustomerGroups: groups not found.";
            throw new DataException(DataException.NO_DATA, msg);
        }
        

        return (groups);
    }

    /**
     * Convert resultset into pricing group object
     * 
     * @param PricingGroupIfc.
     * @param ResultSet
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection
     * @exception SQLException thrown when an error occurs with the
     */
    protected int convertPricingGroupResultSetEntry(PricingGroupIfc group, ResultSet rs) throws SQLException
    {
        int index = 0;
        group.setPricingGroupID(rs.getInt(++index));
        group.setPricingGroupName(getSafeString(rs, ++index));
        group.setPricingGroupDescription(getSafeString(rs, ++index));

        return (index);
    }
}
