/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadAdvancedPricingRuleKeys.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:04 mszekely Exp $
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
 *    4    360Commerce 1.3         1/25/2006 4:11:13 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:39 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:57 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:37    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:39     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:42     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:57     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:46  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:16  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:23  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:31:34   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   14 Jul 2003 02:03:42   mwright
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleKey;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleKeyIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * JdbcReadAdvancedPricingRuleKeys Reads a list of all PricingRuleKeys that
 * match the given criteria.
 */
public class JdbcReadAdvancedPricingRuleKeys extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -3211528537119105169L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadAdvancedPricingRuleKeys.class);

    /**
     * revision number of this class
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcReadAdvancedPricingRuleKeys()
    {
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadAdvancedPricingRuleKeys.execute");
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        AdvancedPricingRuleSearchCriteriaIfc criteria =
             (AdvancedPricingRuleSearchCriteriaIfc) action.getDataObject();

        AdvancedPricingRuleKeyIfc[] ruleKeys = null;
        ruleKeys = selectAdvancedPricingRules(connection, criteria);
        dataTransaction.setResult(ruleKeys);
        if (logger.isDebugEnabled()) logger.debug( "JdbcReadAdvancedPricingRuleKeys.execute");
    }

    /**
     * List all the advanced pricing rules based on a search criteria. The only
     * criteria we're currently interested in is the StoreID.
     * 
     * @param dataConnection connection to the db
     * @param criteria The specific search criteria
     * @exception DataException upon error
     */
    protected AdvancedPricingRuleKeyIfc[] selectAdvancedPricingRules(JdbcDataConnection connection,
            AdvancedPricingRuleSearchCriteriaIfc crt) throws DataException
    {
        ArrayList<AdvancedPricingRuleKeyIfc> list = new ArrayList<AdvancedPricingRuleKeyIfc>(30);
        AdvancedPricingRuleKeyIfc aKey;
        SQLSelectStatement stmt = new SQLSelectStatement();

        stmt.setTable(TABLE_PRICE_DERIVATION_RULE);
        stmt.addColumn(FIELD_RETAIL_STORE_ID);
        stmt.addColumn(FIELD_PRICE_DERIVATION_RULE_ID);

        // Maybe we only want some rule keys...
        String reqStoreID = crt.getStoreID();
        if ((reqStoreID != null) && !reqStoreID.equals("AllStores"))
            stmt.addQualifier(FIELD_RETAIL_STORE_ID, makeSafeString(crt.getStoreID()));

        try
        {
            connection.execute(stmt.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            // for each row in result, add the
            while (rs.next())
            {

                // Make the new key
                aKey = new AdvancedPricingRuleKey();
                // aKey =
                // DomainGateway.getFactory().getAdvancedPricingRuleKeyInstance();

                // Populate it
                aKey.initialize(rs.getString(FIELD_RETAIL_STORE_ID).trim(), rs.getInt(FIELD_PRICE_DERIVATION_RULE_ID));
                list.add(aKey);
            }

            // finished
            rs.close();
        }
        catch (SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(DataException.SQL_ERROR, "selectAdvancedPricingRules", se);
        }

        // Make & Populate a result
        AdvancedPricingRuleKeyIfc result[] = new AdvancedPricingRuleKeyIfc[list.size()];
        result = list.toArray(result);
        return result;
    }
}
