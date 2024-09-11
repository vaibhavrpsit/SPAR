/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTaxHistory.java /rgbustores_13.4x_generic_branch/2 2011/09/21 13:59:07 tksharma Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    6    360Commerce 1.5         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    5    360Commerce 1.4         2/6/2007 11:05:13 AM   Anil Bondalapati
 *         Merge from JdbcReadTaxHistory.java, Revision 1.2.1.0 
 *    4    360Commerce 1.3         12/8/2006 5:01:14 PM   Brendan W. Farrell
 *         Read the tax history when creating pos log for openclosetill
 *         transactions.  Rewrite of some code was needed.
 *    3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/07/08 20:13:59  jdeleau
 *   @scr 6054 - For the tax history write opreration, TaxMode was being
 *   saved instead of tax type.  For the read operation,  a join condition on
 *   taxType was missing.
 *
 *   Revision 1.6  2004/07/02 19:11:27  jdeleau
 *   @scr 5982 Support Tax Holiday
 *
 *   Revision 1.5  2004/06/28 23:17:40  jdeleau
 *   @scr 5877 Partial Fix
 *
 *   Revision 1.4  2004/06/24 21:33:56  dcobb
 *   @scr 5263 - Can't resume suspended till.
 *   Store, workstation, and till IDs can be alphanumeric.
 *
 *   Revision 1.3  2004/06/24 19:03:50  dcobb
 *   @scr 5263 - Can't resume suspended till.
 *   Backed out khassen changes.
 *
 *   Revision 1.2  2004/06/23 15:36:20  khassen
 *   @scr 5263 - Added quotations around till ID query entry.
 *
 *   Revision 1.1  2004/06/15 00:44:31  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.financial.TaxTotalsIfc;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteria;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Read in the tax history (totals)
 * 
 * $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class JdbcReadTaxHistory extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     * Retrieve the tax history
     * 
     * @param dataTransaction
     * @param dataConnection
     * @param dataAction
     * 
     * @throws DataException
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction)
      throws DataException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug( "JdbcReadTaxHistory.execute()");
        }

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        TaxHistorySelectionCriteria criteria = (TaxHistorySelectionCriteria) dataAction.getDataObject();
        TaxTotalsContainerIfc container = readTaxData(connection, criteria);

        // Send back the result
        dataTransaction.setResult(container);

        if (logger.isDebugEnabled()) 
        {
            logger.debug( "JdbcReadTaxHistory.execute()");
        }
    }
    
    /**
     *  Retrieve all the taxes charged based on the given search criteria
     *  
     *  @param connection databaseConnection
     *  @param searchCriteria searchCriteria to use
     *  @return Container with tax data for each rule
     *  @throws DataException on DB error
     */
    public TaxTotalsContainerIfc readTaxData(JdbcDataConnection connection, TaxHistorySelectionCriteria searchCriteria)
      throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // Add the desired tables (and aliases)
        sql.addTable(TABLE_TAX_HISTORY, ALIAS_TAX_HISTORY);
      
        // Add desired columns
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_AUTHORITY_ID);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_TYPE);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_COUNT);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_AMOUNT);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_HOLIDAY);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_FLG_TAX_INCLUSIVE);
        sql.addColumn(ALIAS_TAX_AUTHORITY, FIELD_TAX_AUTHORITY_NAME);
        sql.addColumn(ALIAS_TAX_GROUP_RULE, FIELD_TAX_RULE_NAME);
        
        sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_TAX_AUTHORITY+" "+ALIAS_TAX_AUTHORITY +" ON",
                                  ALIAS_TAX_AUTHORITY+"."+FIELD_TAX_AUTHORITY_ID, ALIAS_TAX_HISTORY+"."+FIELD_TAX_AUTHORITY_ID);
        sql.addOuterJoinQualifier("LEFT OUTER JOIN "+TABLE_TAX_GROUP_RULE+" "+ALIAS_TAX_GROUP_RULE +" ON",
                                  "("+ALIAS_TAX_GROUP_RULE+"."+FIELD_TAX_GROUP_ID, ALIAS_TAX_HISTORY+"."+FIELD_TAX_GROUP_ID);
        sql.addOuterJoinQualifier("AND",
                                  ALIAS_TAX_GROUP_RULE+"."+FIELD_TAX_AUTHORITY_ID, ALIAS_TAX_HISTORY+"."+FIELD_TAX_AUTHORITY_ID);
        sql.addOuterJoinQualifier("AND",
                                  ALIAS_TAX_GROUP_RULE+"."+FIELD_TAX_TYPE, ALIAS_TAX_HISTORY+"."+FIELD_TAX_TYPE);
        sql.addOuterJoinQualifier("AND",
                                  ALIAS_TAX_GROUP_RULE+"."+FIELD_TAX_HOLIDAY, ALIAS_TAX_HISTORY+"."+FIELD_TAX_HOLIDAY+")");
        
        // For the specified till only
        sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_RETAIL_STORE_ID, "'" + String.valueOf(searchCriteria.getStoreId()) + "'");
        if(searchCriteria.getCriteriaType() == TaxHistorySelectionCriteria.SEARCH_BY_WORKSTATION)
        {
            sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_WORKSTATION_ID, "'" + String.valueOf(searchCriteria.getWorkstationId()) + "'");
        }
        if(searchCriteria.getCriteriaType() == TaxHistorySelectionCriteria.SEARCH_BY_TILL)
        {
            sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_WORKSTATION_CURRENT_TILL_ID, "'" + String.valueOf(searchCriteria.getTillId()) + "'");
        }

        // For the specified business day only
        sql.addQualifier(ALIAS_TAX_HISTORY + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + (dateToSQLDateString(searchCriteria.getBusinessDate().dateValue())));
        
       

        TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
        try
        {
            connection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)connection.getResult();

            while (rs.next())
            {
                int index = 0;
                TaxTotalsIfc taxTotals = DomainGateway.getFactory().getTaxTotalsInstance();
                taxTotals.setTaxAuthorityId(rs.getInt(++index));
                taxTotals.setTaxGroupId(rs.getInt(++index));
                taxTotals.setTaxType(rs.getInt(++index));
                taxTotals.setTaxCount(rs.getInt(++index));
                taxTotals.setTaxAmount(getCurrencyFromDecimal(rs, ++index));
                taxTotals.setTaxHoliday(getBooleanFromString(rs, ++index));
                taxTotals.setInclusiveTaxFlag(getBooleanFromString(rs, ++index));
                taxTotals.setTaxAuthorityName(getSafeString(rs, ++index));
                taxTotals.setTaxRuleName(getSafeString(rs, ++index));
                
                StringBuffer uniqueId = new StringBuffer(String.valueOf(taxTotals.getTaxAuthorityId()));
                uniqueId.append("-");
                uniqueId.append(taxTotals.getTaxGroupId());
                uniqueId.append("-");
                uniqueId.append(taxTotals.getTaxType());
                uniqueId.append("-");
                uniqueId.append(taxTotals.isTaxHoliday());
                taxTotals.setUniqueId(uniqueId.toString());
                container.addTaxTotals(taxTotals);
            }
            rs.close();
            return container;
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectTaxHistory", se);
        }
    }
}
