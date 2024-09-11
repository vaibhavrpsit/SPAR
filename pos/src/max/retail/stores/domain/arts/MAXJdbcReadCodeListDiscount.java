/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadCodeListDiscount.java /main/17 2014/03/19 11:37:18 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  03/14/14 - Throwing checked exceptions for fallback on remoteDT
 *                         to avoid NPE
 *    mjwallac  12/19/13 - fix POS null dereferences (part 1)
 *    abondala  05/01/13 - convert primitive types to objects before setting
 *                         the bind variables.
 *    abondala  05/01/13 - replace dynamic sql with bind variables.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ohorne    12/23/08 - now uses new Statement object to generate ResultSet
 *                         b/c method accessed while still reading ResultSet
 *                         obtained from JdbcDataConnection's statement
 *    mdecama   11/04/08 - Added retrieval and saving of the deprecated db
 *                         column for text. It requires a non-null value for
 *                         backwards compatibility
 *    ohorne    11/04/08 - removed reliance on scrollable ResultSet
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    11/02/08 - changes to read the localized reason codes for
 *                         customer groups and store coupons
 *    acadar    10/31/08 - fixes for retrieving the reason codes for advanced
 *                         pricing rules
 *    acadar    10/31/08 - added check for null LocalizedCodeIfc
 *    acadar    10/31/08 - fixes to distinguish between manual item discounts
 *                         and markdowns
 *    acadar    10/31/08 - fixes
 *    mdecama   10/17/08 - Fixed problem populating the CodeList
 *    mdecama   10/15/08 - Retrieves a CodeList for Discounts
 * =========================================================================== */
package max.retail.stores.domain.arts;

import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.persistence.utility.MAXARTSDatabaseIfc;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.JdbcReadCodeListDiscount;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

import org.apache.log4j.Logger;

/**
 * This Data Operation retrieves a Code List for Manual Discounts
 */
public class MAXJdbcReadCodeListDiscount extends JdbcReadCodeListDiscount implements MAXARTSDatabaseIfc
{
    /**
     * Generated serialVersionUID
     */
    private static final long serialVersionUID = -8427546114961221352L;

    /**
     * The logger to which log messages will be sent.
     */
    private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXJdbcReadCodeListDiscount.class);

 
   /* *//**
     * Builds the discount qualifier
     *
     * @param sql
     * @param criteria
     *//*
*/   

    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCodeListDiscount.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        if (action.getDataObject() instanceof CodeSearchCriteriaIfc)
        {
            CodeSearchCriteriaIfc criteria = (CodeSearchCriteriaIfc)action.getDataObject();
            LocalizedCodeIfc code = readCode(connection, criteria);
            dataTransaction.setResult(code);
        }
        else if (action.getDataObject() instanceof CodeListSearchCriteriaIfc)
        {
            CodeListSearchCriteriaIfc criteria = (CodeListSearchCriteriaIfc)action.getDataObject();
            if (criteria.getSearchType() == CodeSearchCriteriaIfc.SEARCH_CODE_LIST)
            {
                CodeListIfc codeList = readCodeList(connection, criteria);
                dataTransaction.setResult(codeList);
            }

            if (criteria.getSearchType() == CodeSearchCriteriaIfc.SEARCH_CODE_LIST_ID)
            {
                // This can be a chain call. Get the Results
                List<String> chainedCodeListIDs = null;
                if ((dataTransaction.getResult() != null) && (dataTransaction.getResult() instanceof Vector))
                {
                    chainedCodeListIDs = (Vector<String>)dataTransaction.getResult();
                }

                List<String> codeListIDs = readCodeListIDs(connection, criteria);

                // Concatenate Results
                if (chainedCodeListIDs != null)
                {
                    if (codeListIDs != null)
                    {
                        codeListIDs.addAll(chainedCodeListIDs);
                        dataTransaction.setResult((Vector)codeListIDs);
                    }
                    else
                    {
                        dataTransaction.setResult((Vector)chainedCodeListIDs);
                    }
                }
                else
                {
                    dataTransaction.setResult((Vector)codeListIDs);
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadCodeListDiscount.execute()");
    }
    
    public CodeListIfc readCodeList(JdbcDataConnection dataConnection, CodeListSearchCriteriaIfc criteria)
            throws DataException
    {
        CodeListIfc codeList = null;

        try
        {
            SQLSelectStatement sql = null;
            if (!criteria.getStoreID().equals(STORE_ID_CORPORATE))
            {
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
                codeList = parseCodeListResultSet(dataConnection, criteria.getStoreID(), criteria.getListID());
            }

            // If there is no CodeList for the Store, read from CORP
            if (codeList == null)
            {
                criteria.setStoreID(STORE_ID_CORPORATE);
                sql = buildCodeListSQLStatement(criteria);
                dataConnection.execute(sql.getSQLString(), sql.getParameterValues());
                codeList = parseCodeListResultSet(dataConnection, criteria.getStoreID(), criteria.getListID());
            }
            
            if(null == codeList)
            {
                throw new DataException(DataException.NO_DATA, "JdbcReadCodeListDiscount.parseCodeListResultSet");
            }
            
        }
        catch (SQLException se)
        {
            logger.error("JdbcReadCodeListDiscount.readCodeList", se);
            throw new DataException(DataException.SQL_ERROR, "JdbcReadCodeListDiscount.readCodeList", se);
        }
        catch (DataException de)
        {
            logger.error("JdbcReadCodeListDiscount.readCodeList", de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error("JdbcReadCodeListDiscount.readCodeList", e);
            throw new DataException(DataException.UNKNOWN, "JdbcReadCodeListShippingMethod.readCodeList", e);
        }

        return codeList;
    }

    /**
     * Builds a SQL Statement for retrieval of a CodeList
     *
     * @param criteria
     * @return
     * @throws SQLException
     */
    protected SQLSelectStatement buildCodeListSQLStatement(CodeListSearchCriteriaIfc criteria) throws SQLException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_PRICE_DERIVATION_RULE, TABLE_PRICE_DERIVATION_RULE);
        sql.addTable(TABLE_PRICE_DERIVATION_RULE_I8, TABLE_PRICE_DERIVATION_RULE_I8);

        sql.setDistinctFlag(true);

        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_ID);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_NAME); // For backwards compatibility
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_REASON_CODE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_STATUS_CODE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_CODE_LIST_ENTRY_DEFAULT_FLAG);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE, FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_LOCALE);
        sql.addColumn(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        buildDiscountQualifier(sql, criteria);

        sql.addOrdering(TABLE_PRICE_DERIVATION_RULE, FIELD_CODE_LIST_ENTRY_SORT_INDEX);
        sql.addOrdering(TABLE_PRICE_DERIVATION_RULE_I8, FIELD_PRICE_DERIVATION_RULE_NAME);

        sql.addTable(TABLE_PRICE_DERIVATION_RULE);

        return (sql);
    }
    
 protected void buildDiscountQualifier(SQLSelectStatement sql,
			CodeListSearchCriteriaIfc criteria) {
		String listId = criteria.getListID();

		if (listId.equals("PreferredCustomerDiscount")) {
			sql.addQualifier(new SQLParameterValue("RU_PRDV", "CD_BAS_PRDV",
					new Integer(1)));
		} else {
			sql.addQualifier(new SQLParameterValue("RU_PRDV", "CD_BAS_PRDV",
					new Integer(0)));

			if (listId.equals("TransactionDiscountByPercentage")) {
				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_SCP_PRDV", new Integer(0)));

				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_MTH_PRDV", new Integer(1)));
			} else if (listId.equals("TransactionDiscountByAmount")) {
				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_SCP_PRDV", new Integer(0)));

				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_MTH_PRDV", new Integer(2)));
			} else if (listId.equals("ItemDiscountByPercentage")) {
				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_SCP_PRDV", new Integer(1)));

				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_MTH_PRDV", new Integer(1)));
			} else if (listId.equals("ItemDiscountByAmount")) {
				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_SCP_PRDV", new Integer(1)));

				sql.addQualifier(new SQLParameterValue("RU_PRDV",
						"CD_MTH_PRDV", new Integer(2)));
			} else if(listId.equals(MAXCodeConstantsIfc.CODE_LIST_DISCOUNT_CARD_DISCOUNT)){
              	 sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
                           new Integer(MAXCodeConstantsIfc.DISCOUNT_SCOPE_DISCOUNT_CARD)));
//                   sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
//                           new Integer(DISCOUNT_METHOD_AMOUNT)));
              }else if(listId.equals(MAXCodeConstantsIfc.CODE_LIST_CAPILLARY_COUPON_DISCOUNT)){
             	 sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
                       new Integer(MAXCodeConstantsIfc.DISCOUNT_SCOPE_CAPILLARY_COUPON)));
//               sql.addQualifier(new SQLParameterValue(TABLE_PRICE_DERIVATION_RULE, FIELD_PRICE_DERIVATION_RULE_METHOD_CODE,
//                       new Integer(DISCOUNT_METHOD_AMOUNT)));
              }

		}

		if (!(Util.isEmpty(criteria.getStoreID()))) {
			sql.addQualifier(new SQLParameterValue("RU_PRDV", "ID_STR_RT",
					criteria.getStoreID()));
		}
		sql.addJoinQualifier("RU_PRDV", "ID_RU_PRDV", "RU_PRDV_I8",
				"ID_RU_PRDV");

		sql.addQualifier("LCL "
				+ buildINClauseString(LocaleMap.getBestMatch("", criteria
						.getLocaleRequestor().getLocales())));
	}
    
    
    }
