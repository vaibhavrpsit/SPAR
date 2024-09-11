/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveTaxHistory.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
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
 *    4    360Commerce 1.3         5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/07/02 19:11:27  jdeleau
 *   @scr 5982 Support Tax Holiday
 *
 *   Revision 1.6  2004/06/16 00:44:33  jdeleau
 *   @scr 2775 Fix the way tax histories are saved
 *
 *   Revision 1.5  2004/06/15 21:46:37  jdeleau
 *   @scr 2775 Quotes around all string variables
 *
 *   Revision 1.4  2004/06/15 21:11:00  jdeleau
 *   @scr 2775 add more makeSafeString calls
 *
 *   Revision 1.3  2004/06/15 21:08:49  jdeleau
 *   @scr 2775 Fix safeString calls
 *
 *   Revision 1.2  2004/06/15 16:05:33  jdeleau
 *   @scr 2775 Add database entry for uniqueID so returns w/
 *   receipt will work, make some fixes to FinancialTotals storage of tax.
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

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.financial.TaxTotalsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Save tax totals to the db
 * $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcSaveTaxHistory extends JdbcSaveReportingPeriod implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 4662662285974643116L;

    /**
     * Logger for error messages
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveRegister.class);
    
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
     * Default constructor
     */
    public JdbcSaveTaxHistory()
    {
        super.setName("JdbcSaveTaxHistory");
    }
    
    /**
     * Save the tax totals to the DB 
     * @param dataTransaction
     * @param dataConnection
     * @param dataAction
     * @throws DataException
     * @see oracle.retail.stores.foundation.manager.ifc.data.DataOperationIfc#execute(oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc, oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc)
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc dataAction)
      throws DataException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug( "JdbcSaveTaxHistory.execute()");
        }

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        TaxTotalsContainerIfc taxTotalsContainer = (TaxTotalsContainerIfc)dataAction.getDataObject();

        TaxTotalsIfc[] taxTotals = taxTotalsContainer.getTaxTotals();
        for(int i=0; i<taxTotals.length; i++)
        {
            if(recordExists(connection, taxTotals[i], taxTotalsContainer))
            {
                updateTaxTotals(connection, taxTotals[i], taxTotalsContainer);
            }
            else
            {
                insertTaxTotals(connection, taxTotals[i], taxTotalsContainer);
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug( "JdbcSaveTaxHistory.execute()");
        }
    }

    /**
     *  Update the tax totals in the database
     *  
     *  @param connection database connection
     *  @param taxTotals tax info
     *  @param taxTotalsContainer till information
     *  @throws DataException on any DB error
     */
    public void updateTaxTotals(JdbcDataConnection connection, TaxTotalsIfc taxTotals, TaxTotalsContainerIfc taxTotalsContainer)
      throws DataException
    {
            // Prepare for an insert
            SQLUpdateStatement sql = new SQLUpdateStatement();
            
            // Add the desired tables (and aliases)
            sql.setTable(TABLE_TAX_HISTORY);
            
            // Then add the specific tax jurisdiction data
            sql.addColumn(FIELD_TAX_COUNT, FIELD_TAX_COUNT + " + " + String.valueOf(taxTotals.getTaxCount()));
            sql.addColumn(FIELD_TAX_AMOUNT, FIELD_TAX_AMOUNT + " + " + taxTotals.getTaxAmount().toString());
            
            // Add the qualifiers
            sql.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(taxTotalsContainer.getStoreId()));
            sql.addQualifier(FIELD_WORKSTATION_ID, makeSafeString(taxTotalsContainer.getWorkstationId()));
            sql.addQualifier(FIELD_WORKSTATION_CURRENT_TILL_ID, makeSafeString(taxTotalsContainer.getTillId()));
            sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(taxTotalsContainer.getBusinessDate()));
            sql.addQualifier(FIELD_TAX_AUTHORITY_ID, String.valueOf(taxTotals.getTaxAuthorityId()));
            sql.addQualifier(FIELD_TAX_HOLIDAY, makeStringFromBoolean(taxTotals.isTaxHoliday()));
            sql.addQualifier(FIELD_TAX_GROUP_ID, String.valueOf(taxTotals.getTaxGroupId()));
            sql.addQualifier(FIELD_TAX_TYPE, String.valueOf(taxTotals.getTaxType()));

            connection.execute(sql.getSQLString());
    
            if (0 >= connection.getUpdateCount())
            {
                logger.error("Tax History Update statement failed to update any columns");
                throw new DataException(DataException.NO_DATA, "Update Tax History");
            }
    }
    
    /**
     *  Insert the tax totals into the database
     *  
     *  @param connection database connection
     *  @param taxTotals info about the tax being saved
     *  @param taxTotalsContainer till information
     *  @throws DataException on any DB error
     */
    public void insertTaxTotals(JdbcDataConnection connection, TaxTotalsIfc taxTotals, TaxTotalsContainerIfc taxTotalsContainer)
      throws DataException
    {
            // Prepare for an insert
            SQLInsertStatement sql = new SQLInsertStatement();
            
            // Add the desired tables (and aliases)
            sql.setTable(TABLE_TAX_HISTORY);
            
            // Set the stuff based off the container
            sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(taxTotalsContainer.getStoreId()));
            sql.addColumn(FIELD_WORKSTATION_ID, makeSafeString(taxTotalsContainer.getWorkstationId()));
            sql.addColumn(FIELD_WORKSTATION_CURRENT_TILL_ID, makeSafeString(taxTotalsContainer.getTillId()));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(taxTotalsContainer.getBusinessDate()));
            // Then add the specific tax jurisdiction data
            sql.addColumn(FIELD_TAX_GROUP_ID, taxTotals.getTaxGroupId());
            sql.addColumn(FIELD_TAX_AUTHORITY_ID, taxTotals.getTaxAuthorityId());
            sql.addColumn(FIELD_TAX_TYPE, taxTotals.getTaxType());
            sql.addColumn(FIELD_TAX_HOLIDAY, makeStringFromBoolean(taxTotals.isTaxHoliday()));
            sql.addColumn(FIELD_TAX_COUNT, taxTotals.getTaxCount());
            sql.addColumn(FIELD_TAX_AMOUNT, taxTotals.getTaxAmount().toString());
            sql.addColumn(FIELD_FLG_TAX_INCLUSIVE, makeStringFromBoolean(taxTotals.getInclusiveTaxFlag()));
            
            connection.execute(sql.getSQLString());
    
            if (0 >= connection.getUpdateCount())
            {
                logger.error("Tax History Update statement failed to update any columns");
                throw new DataException(DataException.NO_DATA, "Update Tax History");
            }
    }

    /**
     *  Determine if the tax total record already exists.
     *  
     *  @param connection
     *  @param taxTotalsContainer container which contains till/workstation/store info
     *  @param taxTotals record of tax totals
     *  @return true if it exists, otherwise false.
     */
    public boolean recordExists(JdbcDataConnection connection, TaxTotalsIfc taxTotals, TaxTotalsContainerIfc taxTotalsContainer)
    {
        boolean exists = false;
        SQLSelectStatement sql = new SQLSelectStatement();

        // Add the desired tables (and aliases)
        sql.addTable(TABLE_TAX_HISTORY, ALIAS_TAX_HISTORY);

        // Add desired columns
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_AUTHORITY_ID);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_TAX_HISTORY, FIELD_TAX_TYPE);
        
        // For the specified till only
        sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_RETAIL_STORE_ID, makeSafeString(String.valueOf(taxTotalsContainer.getStoreId())));
        sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_WORKSTATION_ID, makeSafeString(String.valueOf(taxTotalsContainer.getWorkstationId())));
        sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_WORKSTATION_CURRENT_TILL_ID, makeSafeString(String.valueOf(taxTotalsContainer.getTillId())));
        sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_TAX_AUTHORITY_ID, String.valueOf(taxTotals.getTaxAuthorityId()));
        sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_TAX_GROUP_ID, String.valueOf(taxTotals.getTaxGroupId()));
        sql.addQualifier(ALIAS_TAX_HISTORY, FIELD_TAX_TYPE, String.valueOf(taxTotals.getTaxType()));
        sql.addQualifier(FIELD_TAX_HOLIDAY, makeStringFromBoolean(taxTotals.isTaxHoliday()));

        // For the specified business day only
        sql.addQualifier(ALIAS_TAX_HISTORY + "." + FIELD_BUSINESS_DAY_DATE
                         + " = " + (dateToSQLDateString(taxTotalsContainer.getBusinessDate().dateValue())));
        

        try
        {
            connection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)connection.getResult();
            
            while(rs.next())
            {
                rs.getInt(1);
                exists = true;
                break;
            }

            rs.close();
        }
        catch (SQLException se)
        {
            logger.error("Could not very record's existance"+se);
        }
        catch (DataException de)
        {
            logger.error("Could not very record's existance"+de);
        }
        return exists;
    }
}
