/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSelectCustomerGroups.java /main/17 2012/08/23 23:03:25 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  08/23/12 - fix offline creating a customer. Customer groups and
 *                         priging groups are displayed in the drop downs on UI
 *                         which are retrieved through jdbc operations. May
 *                         need to introduce jpa for these operations.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   08/22/11 - removed deprecated methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/29/09 - XbranchMerge cgreene_bug-8931245 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/29/09 - remove time char column from promo eligibility table
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    11/02/08 - updated as per code review
 *    acadar    11/02/08 - changes to read the localized reason codes for
 *                         customer groups and store coupons
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 * ===========================================================================
      $Log:
      7    360Commerce 1.6         7/25/2006 4:25:03 PM   Brendan W. Farrell
           UDM fix
      6    360Commerce 1.5         6/8/2006 3:54:25 PM    Brett J. Larsen CR
           18490 - UDM - columns CD_MTH_PRDV, CD_SCP_PRDV and CD_BAS_PRDV's
           type was changed to INTEGER
      5    360Commerce 1.4         6/6/2006 6:03:44 PM    Brett J. Larsen CR
           18490 - UDM - TimeDatePriceDerivationRuleEligibility
           (CO_EL_TM_PRDV) - Effective/Expiration Dates changed to type:
           TIMESTAMP
      4    360Commerce 1.3         1/25/2006 4:11:25 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:05 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/16/2005 16:28:30    Jason L. DeLeau 4215:
           Get rid of redundant ArtsDatabaseifc class
      3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:51     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:05     Robert Pearse
     $
     Revision 1.6  2004/04/09 16:55:46  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.5  2004/02/17 17:57:37  bwf
     @scr 0 Organize imports.

     Revision 1.4  2004/02/17 16:18:46  rhafernik
     @scr 0 log4j conversion

     Revision 1.3  2004/02/12 17:13:19  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:24  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 15:33:12   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Apr 02 2003 13:47:50   baa
 * I18n Database conversion for customer group
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.0   Jun 03 2002 16:40:38   msg
 * Initial revision.
 *
 *    Rev 1.2   May 12 2002 23:40:10   mhr
 * db2 quote fixes.  chars/varchars must be quoted and ints/decimals must not be quoted.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:49:12   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:56:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

/**
 * This class provides the methods needed to read the available customer
 * groups and the associated, currently effective discount rules. <P>
 * @see oracle.retail.stores.domain.customer.CustomerGroupIfc
 * @see oracle.retail.stores.domain.discount.DiscountRuleIfc

*/
public class JdbcSelectCustomerGroups extends JdbcDataOperation implements ARTSDatabaseIfc, DiscountRuleConstantsIfc
{
    private static final long serialVersionUID = -5031864517974049502L;
    /** The logger to which log messages will be sent. */
    private static Logger logger = Logger.getLogger(JdbcSelectCustomerGroups.class);

    /**
     * Executes the SQL statements against the database.
     * 
     * @param  dataTransaction     The data transaction
     * @param  dataConnection      The connection to the data source
     * @param  action              The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcSelectCustomerGroups.execute()");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        
        ArrayList <CustomerGroupIfc> retrievedGroups = null;
        
        CustomerSearchCriteriaIfc criteria = (CustomerSearchCriteriaIfc)action.getDataObject();

        try
        {
            retrievedGroups = readCustomerGroups(connection, criteria.getLocaleRequestor());
        }
        catch (DataException de)
        {
            throw de;
        }

        dataTransaction.setResult(retrievedGroups);

        if (logger.isDebugEnabled()) logger.debug( "JdbcSelectCustomerGroups.execute()");
    }


    /**
     * Returns an array of customer groups and the associated discount
     * rules currently in effect. 
     *
     * @param  dataConnection  connection to the db
     * @param  sqlLocale  locale to be use for displaying the retrieved data
     * @return array of customer groups
     * @exception DataException upon error
     */
    public ArrayList<CustomerGroupIfc> readCustomerGroups(JdbcDataConnection dataConnection, LocaleRequestor sqlLocale) throws DataException
    {

       ArrayList<CustomerGroupIfc> retrievedGroups = null;
       try
       {
           // build SQL statement, execute and parse result set
           SQLSelectStatement sql = buildCustomerGroupsSQLStatement(sqlLocale);
           dataConnection.execute(sql.getSQLString());
           retrievedGroups = parseCustomerGroupsResultSet(dataConnection);
           // if groups exist (and they do, or a NO_DATA exception would
           // be thrown
           for (CustomerGroupIfc customerGroup : retrievedGroups) 
           {
               readDiscountRules(dataConnection, customerGroup, sqlLocale);
           }
       }
       catch (SQLException se)
       {
           throw new DataException(DataException.SQL_ERROR, "readCustomerGroups", se);
       }
       catch (DataException de)
       {
           throw de;
       }
       catch (Exception e)
       {
           throw new DataException(DataException.UNKNOWN, "readCustomerGroups", e);
       }
       finally
       {
           if (dataConnection.getResult() != null)
           {
               try
               {
                   ((ResultSet)dataConnection.getResult()).close();
               }
               catch (SQLException se)
               {
                   dataConnection.logSQLException(se, "JdbcSelectCustomerGroups -- Could not close result handle");
               }
           }
       }

       return(retrievedGroups);
    }


    /**
     * Reads discount rules for associated group and sets group's
     * rules attribute accordingly. 
     *
     * @param  dataConnection  connection to the db
     * @param group retrieved customer group
     * @exception DataException upon error
     */
    public void readDiscountRules(JdbcDataConnection dataConnection,CustomerGroupIfc group, LocaleRequestor locale) throws DataException
    {
        DiscountRuleIfc[] retrievedRules = null;
        DiscountRuleIfc rule = null;
        try
        {
            SQLSelectStatement sql = buildDiscountRulesSQLStatement(group.getGroupID());
            dataConnection.execute(sql.getSQLString());
            retrievedRules = parseDiscountRulesResultSet(dataConnection);
            LocalizedCodeIfc reasonCode = null;
            if (retrievedRules != null)
            {
                for (int i = 0; i < retrievedRules.length; i++)
                {
                    rule = retrievedRules[i];
                    sql = buildLocalizedRuleSQL(rule.getRuleID(), locale);
                    dataConnection.execute(sql.getSQLString());
                    rule = readLocalizedRule(dataConnection, rule);
                    reasonCode = DomainGateway.getFactory().getLocalizedCode();
                    reasonCode.setCode(group.getGroupID());
                    reasonCode.setText(group.getLocalizedNames());
                    rule.setReason(reasonCode);
                    group.addDiscountRule(rule);
                }
            }
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "readDiscountRules", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readDiscountRules", e);
        }

    }

    /**
     * Builds the SQL for reading the localization information for the rule
     * 
     * @param ruleId
     * @param locale
     * @return
     */
    protected SQLSelectStatement buildLocalizedRuleSQL(String ruleId, LocaleRequestor locale)
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_PRICE_DERIVATION_RULE, ALIAS_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_PRICE_DERIVATION_RULE_I8, ALIAS_PRICE_DERIVATION_RULE_I8);

        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE_I8, FIELD_LOCALE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        sql.addQualifier(ALIAS_PRICE_DERIVATION_RULE + "." + FIELD_PRICE_DERIVATION_RULE_ID + " = " + ruleId);

        //add locale qualifier
        Set<Locale> bestMatches = LocaleMap.getBestMatch("", locale.getLocales());
        sql.addQualifier(ALIAS_PRICE_DERIVATION_RULE_I8 + "." + FIELD_LOCALE + " " + JdbcDataOperation.buildINClauseString(bestMatches));

        sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE,FIELD_PRICE_DERIVATION_RULE_ID,ALIAS_PRICE_DERIVATION_RULE_I8,FIELD_PRICE_DERIVATION_RULE_ID);

        return sql;

    }

    /**
     * Reads the result set and populates the localized name for the discount rule
     * 
     * @param dc
     * @param rule
     * @return
     * @throws DataException
     * @throws SQLException
     */
    protected DiscountRuleIfc readLocalizedRule(JdbcDataConnection dc,DiscountRuleIfc rule) throws DataException, SQLException
    {
        ResultSet rs = (ResultSet)dc.getResult();
        while (rs.next())
        {
          int index = 0;
          String localeString = JdbcDataOperation.getSafeString(rs, ++index);
          String localizedName = JdbcDataOperation.getSafeString(rs, ++index);
          Locale lcl = LocaleUtilities.getLocaleFromString(localeString);
          rule.setName(lcl, localizedName);
        }
        rule.getReason().setText(rule.getLocalizedNames());
        return rule;
    }


    /**
     * Builds a select statement for reading the customer groups as per a specific locale
     * 
     * @param LocaleRequestor locale
     * @return
     * @throws SQLException
     */
    protected SQLSelectStatement buildCustomerGroupsSQLStatement(LocaleRequestor locale) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_CUSTOMER_GROUP,ALIAS_CUSTOMER_GROUP);
        sql.addTable(TABLE_CUSTOMER_GROUP_I8,ALIAS_CUSTOMER_GROUP_I8 );
        // add columns
        sql.addColumn(ALIAS_CUSTOMER_GROUP, FIELD_CUSTOMER_GROUP_ID);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8, FIELD_LOCALE);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8, FIELD_CUSTOMER_GROUP_NAME);
        sql.addColumn(ALIAS_CUSTOMER_GROUP_I8, FIELD_CUSTOMER_GROUP_DESCRIPTION);

       // add additional qualifiers
        addCustomerGroupsAdditionalQualifiers(sql, locale);
        // add ordering
        addCustomerGroupsOrdering(sql);
        return(sql);
    }


    /**
     * Add additional qualifiers to sql statement. 
     *
     * @param sql SQLSelectStatement to modify
     * @param LocaleRequestor locale
     */
    protected void addCustomerGroupsAdditionalQualifiers(SQLSelectStatement sql, LocaleRequestor locale)
    {
        sql.addJoinQualifier(ALIAS_CUSTOMER_GROUP_I8,FIELD_CUSTOMER_GROUP_ID,ALIAS_CUSTOMER_GROUP,FIELD_CUSTOMER_GROUP_ID);
        Set<Locale> bestMatches = LocaleMap.getBestMatch("", locale.getLocales());
        sql.addQualifier(ALIAS_CUSTOMER_GROUP_I8 + "." +  FIELD_LOCALE + " " +  JdbcDataOperation.buildINClauseString(bestMatches));
    }

    /**
     * Adds ordering clause to SQL statement. 
     *
     * @param sql SQLSelectStatement object which should have ordering added
     */
    protected void addCustomerGroupsOrdering(SQLSelectStatement sql)
    {
        sql.addOrdering(ALIAS_CUSTOMER_GROUP_I8,FIELD_CUSTOMER_GROUP_NAME);
    }

    /**
     * Parses result set and creates an array of CustomerGroupIfc objects. 
     *
     * @param dataConnection data connection
     * @return CustomerGroupIfc[] object
     * @exception SQLException thrown if result set cannot be parsed
     * @exception DataException thrown if no records in result set
     */
    protected ArrayList <CustomerGroupIfc> parseCustomerGroupsResultSet(JdbcDataConnection dataConnection) throws SQLException, DataException
    {
        ArrayList <CustomerGroupIfc> groups = new ArrayList<CustomerGroupIfc>();
        ResultSet rs = (ResultSet) dataConnection.getResult();
        CustomerGroupIfc group = null;
        String groupId = null;

        if (rs != null)
        {
            while (rs.next())
            {
                int index = 0;
                groupId = getSafeString(rs, ++index);
                group = ReadARTSCustomerSQL.getGroup(groupId, groups);
                if(group == null )
                {
                    group = instantiateCustomerGroupIfc();
                    group.setGroupID(groupId);
                    groups.add(group);
                }
                String localeString = getSafeString(rs, ++index);
                Locale lcl = LocaleUtilities.getLocaleFromString(localeString);
                group.setName(lcl, getSafeString(rs, ++index));
                group.setDescription(lcl,getSafeString(rs, ++index));
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
        

        return(groups);
    }

    /**
     * Builds SQL statement for selection of discount rules. 
     *
     * @param groupID group identifier string
     * @param LocaleRequestor
     * @return sql string
     * @exception SQLException thrown if error occurs
     */
    protected SQLSelectStatement buildDiscountRulesSQLStatement(String groupID)  throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_PRICE_DERIVATION_RULE, ALIAS_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_ITEM_PRICE_DERIVATION, ALIAS_ITEM_PRICE_DERIVATION);
        sql.addTable(TABLE_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY);
        sql.addTable(TABLE_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY, ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY);
        // add columns
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_TRANSACTION_CONTROL_BREAK_CODE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_STATUS_CODE);
        sql.addColumn(ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE);
        sql.addColumn(ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY, FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_DESCRIPTION);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_INCLUDED_IN_BEST_DEAL_FLAG);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE);
        sql.addColumn(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE);
        sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_MONETARY_AMOUNT);
        sql.addColumn(ALIAS_ITEM_PRICE_DERIVATION, FIELD_ITEM_PRICE_DERIVATION_SALE_UNIT_PERCENT);
        // add qualifiers
        sql.addQualifier(ALIAS_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY
                         + "." + FIELD_CUSTOMER_GROUP_ID +
                         " = " + groupID);

        sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE,FIELD_PRICE_DERIVATION_RULE_ID,ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY,FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE,FIELD_PRICE_DERIVATION_RULE_ID,ALIAS_CUSTOMER_AFFILIATION_PRICE_DERIVATION_RULE_ELIGIBILITY,FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addJoinQualifier(ALIAS_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID, ALIAS_ITEM_PRICE_DERIVATION, FIELD_PRICE_DERIVATION_RULE_ID);


        // add additional qualifiers
        addDiscountRulesAdditionalQualifiers(sql);
        // add ordering
        addDiscountRulesOrdering(sql);
        return(sql);
    }

    /**
     * Add additional qualifiers to sql statement.
     * 
     * @param sql SQLSelectStatement to modify
     */
    protected void addDiscountRulesAdditionalQualifiers(SQLSelectStatement sql)
    {
        // add timestamp rules
        sql.addQualifier(currentTimestampRangeCheckingString(
                         ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY + "." +
                         FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE,
                         ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY + "." +
                         FIELD_PRICE_DERIVATION_RULE_EXPIRATION_DATE));
        // limit to customer triggered
        sql.addQualifier(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE +
                         " = " + ASSIGNMENT_CUSTOMER);

    }

    /**
     * Adds ordering clause to SQL statement.
     * 
     * @param sql SQLSelectStatement object which should have ordering added
     */
    protected void addDiscountRulesOrdering(SQLSelectStatement sql)
    {
        sql.addOrdering
            (ALIAS_TIME_DATE_PRICE_DERIVATION_RULE_ELIGIBILITY + "." +
             FIELD_PRICE_DERIVATION_RULE_EFFECTIVE_DATE);
    }

     /**
      * Parses result set and creates an array of DiscountRuleIfc objects. <P>
      * @param dataConnection data connection
      * @return DiscountRuleIfc[] object
      * @exception SQLException thrown if result set cannot be parsed
      * @exception DataException thrown if no records in result set
      */
    protected DiscountRuleIfc[]parseDiscountRulesResultSet(JdbcDataConnection dataConnection)
        throws SQLException, DataException
    {
        DiscountRuleIfc retrievedRule = null;
        Vector<DiscountRuleIfc> rules = new Vector<DiscountRuleIfc>();
        ResultSet rs = (ResultSet) dataConnection.getResult();
        int recordsFound = 0;

        if (rs != null)
        {
            while (rs.next())
            {
                recordsFound++;
                // build rule object
                retrievedRule = instantiateDiscountRuleIfc();
                convertDiscountRulesResultSetEntry(retrievedRule, rs);
                rules.addElement(retrievedRule);
            }

            // close result set
            rs.close();
        }

        DiscountRuleIfc[] retrievedRules = null;
        // ignore not found
        if (recordsFound > 0)
        {
            retrievedRules = new DiscountRuleIfc[recordsFound];
            rules.copyInto(retrievedRules);
        }

        return(retrievedRules);
    }

    /**
     * Converts result set entry into a DiscountRuleIfc object. 
     *
     * @param rule DiscountRuleIfc object
     * @param rs ResultSet set at entry to be converted
     * @return index of result set entries
     * @exception SQLException thrown if error occurs
     */
    protected int convertDiscountRulesResultSetEntry(DiscountRuleIfc rule, ResultSet rs)
        throws SQLException
    {
        int index = 0;
        String appliedWhen = getSafeString(rs, ++index);
        String status = getSafeString(rs, ++index);
        rule.setEffectiveDate(timestampToEYSDate(rs, ++index));
        rule.setEffectiveTime(new EYSTime(rule.getEffectiveDate()));
        rule.setExpirationDate(timestampToEYSDate(rs, ++index));
        rule.setExpirationTime(new EYSTime(rule.getExpirationDate()));
        rule.setDescription(getSafeString(rs, ++index));
        rule.setRuleID(getSafeString(rs, ++index));
        int reasonCode = rs.getInt(++index);
        String reasonCodeString = CodeConstantsIfc.CODE_UNDEFINED;
        try
        {
            reasonCodeString = Integer.toString(reasonCode);
        }
        catch (Exception e)
        {
            // do nothing, use CODE_UNDEFINED
        }
        rule.getReason().setCode(reasonCodeString);
        String includedInBestDealFlag = getSafeString(rs, ++index);
        rule.setDiscountScope(rs.getInt(++index));
        rule.setDiscountMethod(rs.getInt(++index));
        rule.setDiscountAmount
            (getCurrencyFromDecimal(rs, ++index));
        rule.setDiscountRate(getPercentage(rs, ++index));
        // right now, only customer discount rules are supported
        rule.setDiscountScope(DISCOUNT_SCOPE_TRANSACTION);
        rule.setAssignmentBasis(ASSIGNMENT_CUSTOMER);

        setDiscountRuleValues(rule,
                              appliedWhen,
                              status,
                              includedInBestDealFlag);
        return(index);
    }

    /**
     * Sets values in discount rule object.
     *
     * @param rule DiscountRuleIfc object already created
     * @param appliedWhen string value of applied when attribute
     * @param status string value of status
     * @param includedInBestDeal string value of includedInBestDeal flag
     */
    public void setDiscountRuleValues(DiscountRuleIfc rule,
                                      String appliedWhen,
                                      String status,
                                      String includedInBestDealFlag)
    {
        // set applied when value
        if (appliedWhen.equals("DT"))
        {
            rule.setAppliedWhen(APPLIED_DETAIL);
        }
        else if (appliedWhen.equals("MT"))
        {
            rule.setAppliedWhen(APPLIED_MERCHANDISE_SUBTOTAL);
        }
        else
        {
            rule.setAppliedWhen(APPLIED_UNDEFINED);
        }

        // set status
        rule.setStatus(STATUS_PENDING);
        for (int i = 0; i < STATUS_DESCRIPTORS.length; i++)
        {
            if (status.equals(STATUS_DESCRIPTORS[i]))
            {
                rule.setStatus(i);
                i = STATUS_DESCRIPTORS.length;
            }
        }


        if (includedInBestDealFlag.equals("1"))
        {
            rule.setIncludedInBestDeal(true);
        }
        else
        {
            rule.setIncludedInBestDeal(false);
        }

    }

    /**
     * Instantiates DiscountRuleIfc object. 
     *
     * @return DiscountRuleIfc object
     */
    public DiscountRuleIfc instantiateDiscountRuleIfc()
    {
        return(DomainGateway.getFactory().getDiscountRuleInstance());
    }

    /**
     * Instantiates CustomerGroupIfc object. 
     *
     * @return CustomerGroupIfc object
     */
    public CustomerGroupIfc instantiateCustomerGroupIfc()
    {
        return(DomainGateway.getFactory().getCustomerGroupInstance());
    }


}
