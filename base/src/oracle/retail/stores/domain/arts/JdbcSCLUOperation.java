/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSCLUOperation.java /rgbustores_13.4x_generic_branch/2 2011/09/15 13:34:45 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/15/11 - removed deprecated methods and changed static
 *                         methods to non-static
 *    ohorne    02/22/11 - ItemNumber can be ItemID or PosItemID
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   09/25/09 - XbranchMerge cgreene_bug-8931126 from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/24/09 - refactor SQL statements up support
 *                         preparedStatements for updates and inserts to
 *                         improve dept hist perf
 *    cgreene   03/01/09 - upgrade to using prepared statements for PLU
 *    cgreene   02/11/09 - convert to using qualifier for prepared statements
 *    cgreene   01/22/09 - Use SearchCriteria to add store to where clause
 * 
 * ===========================================================================
 *    $Log:
 *    4    360Commerce 1.3 11/15/2007 11:39:37 AM Christian Greene
 *         CR29596 - Comment out removing of coupon id from source list. Since
 *          JdbcPLUOperation is not filtering StoreCoupons, call the configure
 *          method in JdbcSCLUOperation with any found coupons.
 *    3    360Commerce 1.2 3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1 3/10/2005 10:22:51 AM  Robert Pearse   
 *    1    360Commerce 1.0 2/11/2005 12:12:04 PM  Robert Pearse   
 *   $
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 * 
 *    Rev 1.0   Aug 29 2003 15:33:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:40:34   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:06   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:50   msg
 * Initial revision.
 *
 *    Rev 1.3   27 Dec 2001 16:39:54   pjf
 * Store coupon revisions.
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.2   08 Dec 2001 10:34:00   mia
 * remove store coupon APR rules specific changes -
 * moved to POS site LookupStoreCouponItemSite
 * Resolution for Backoffice SCR-61: EYSPOS5.0.0 - Store Coupon Enhancements
 *
 *    Rev 1.1   08 Oct 2001 11:57:06   pjf
 * Modified to use updated DiscountListIfc.
 * Resolution for POS SCR-10: Advanced Pricing
 *
 *    Rev 1.0   Sep 20 2001 15:56:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLParameterIfc;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * Retrieves a store coupon PLUItem and its associated pricing rules. Configures
 * the store coupon rules as necessary.
 */
public class JdbcSCLUOperation extends JdbcPLUOperation
{
    private static final long serialVersionUID = -4649945053031599011L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSCLUOperation.class);

    /**
     * revision number of this class
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /**
     * Executes the SQL statements against the database.
     * 
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    @Override
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSCLUOperation.execute start");

        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;
        PLUItemIfc[] pluItems = null;
        SearchCriteriaIfc criteria = (SearchCriteriaIfc)action.getDataObject();

        // Get the PLUItems from the database
        pluItems = readSCLUItem(connection, criteria);

        // Set the list of PluItems to the result.
        dataTransaction.setResult(pluItems);

        if (logger.isDebugEnabled())
            logger.debug("JdbcSCLUOperation.execute ends");
    }

    /**
     * Reads store coupon PLUItems from the POS Identity and Item tables.
     *
     * @param dataConnection  a connection to the database
     * @param inquiry the item lookup key configured for search by ItenNumber, ItemID, or PosItemID
     * @return An array of PLUItems
     * @exception  DataException thrown when an error occurs executing the
     *             SQL against the DataConnection, or when processing the ResultSet
     */
    protected PLUItemIfc[] readSCLUItem(JdbcDataConnection connection, SearchCriteriaIfc inquiry) throws DataException
    {
        if (inquiry.isSearchItemByItemNumber())
        {
            return readSCLUItemByItemNumber(connection, inquiry);
        }
        else if (inquiry.isSearchItemByItemID())
            {
                return readSCLUItemByItemID(connection, inquiry);
        }
        else if (inquiry.isSearchItemByPosItemID())
        {
            return readSCLUItemByPosItemID(connection, inquiry);
        }
        else
        {
            //search method not explicitly stated so search by PosItemID
            return readSCLUItemByPosItemID(connection, inquiry);
        }
    }
    
    /**
     * Reads store coupon PLUItems from the POS Identity and Item tables by ItemID OR PosItemID
     * 
     * @param dataConnection a connection to the database
     * @param criteria the item lookup key configured for search by ItemID OR PosItemID
     * @return An array of PLUItems
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public PLUItemIfc[] readSCLUItemByItemNumber(JdbcDataConnection dataConnection, SearchCriteriaIfc criteria) throws DataException
    {
        // create a qualifier to filter on ITEM_TYPE_STORE_COUPON
        List<SQLParameterIfc> andQualifiers = new ArrayList<SQLParameterIfc>(2);
        andQualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_RETAIL_STORE_ID, criteria.getStoreNumber()));
        andQualifiers.add(new SQLParameterValue(ALIAS_ITEM, FIELD_ITEM_TYPE_CODE, ARTSDatabaseIfc.ITEM_TYPE_STORE_COUPON));

        List<SQLParameterIfc> orQualifiers = new ArrayList<SQLParameterIfc>(2);
        orQualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, criteria.getItemID()));
        orQualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_RETAIL_STORE_ID, criteria.getStoreNumber()));
        orQualifiers.add(new SQLParameterValue(ALIAS_ITEM, FIELD_ITEM_TYPE_CODE, ARTSDatabaseIfc.ITEM_TYPE_STORE_COUPON));
        
        PLUItemIfc[] selected = selectPLUItem(dataConnection, andQualifiers, orQualifiers, criteria.getLocaleRequestor());
        if (selected.length > 0)
        {
            configureStoreCouponRules(selected[0]);
        }
        return selected;
    }
    
    /**
     * Reads store coupon PLUItems from the POS Identity and Item tables by PosItemID.
     * 
     * @param dataConnection a connection to the database
     * @param criteria the item lookup key configured for search by PosItemID
     * @return An array of PLUItems
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public PLUItemIfc[] readSCLUItemByPosItemID(JdbcDataConnection dataConnection, SearchCriteriaIfc criteria) throws DataException
    {
        // create a qualifier to filter on ITEM_TYPE_STORE_COUPON
        List<SQLParameterIfc> qualifiers = new ArrayList<SQLParameterIfc>(3);
        qualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_POS_ITEM_ID, criteria.getPosItemID()));
        qualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_RETAIL_STORE_ID, criteria.getStoreNumber()));
        qualifiers.add(new SQLParameterValue(ALIAS_ITEM, FIELD_ITEM_TYPE_CODE, ARTSDatabaseIfc.ITEM_TYPE_STORE_COUPON));

        PLUItemIfc[] selected = selectPLUItem(dataConnection, qualifiers, criteria.getLocaleRequestor());
        if (selected.length > 0)
        {
            configureStoreCouponRules(selected[0]);
        }
        return selected;
    }

    /**
     * Reads store coupon PLUItems from the POS Identity and Item tables by ItemID.
     * 
     * @param dataConnection a connection to the database
     * @param criteria the item lookup key configured for search by ItemID
     * @return An array of PLUItems
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public PLUItemIfc[] readSCLUItemByItemID(JdbcDataConnection dataConnection, SearchCriteriaIfc criteria) throws DataException
    {
        // create a qualifier to filter on ITEM_TYPE_STORE_COUPON
        List<SQLParameterIfc> qualifiers = new ArrayList<SQLParameterIfc>(3);
        qualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_ITEM_ID, criteria.getItemID()));
        qualifiers.add(new SQLParameterValue(ALIAS_POS_IDENTITY, FIELD_RETAIL_STORE_ID, criteria.getStoreNumber()));
        qualifiers.add(new SQLParameterValue(ALIAS_ITEM, FIELD_ITEM_TYPE_CODE, ARTSDatabaseIfc.ITEM_TYPE_STORE_COUPON));

        PLUItemIfc[] selected = selectPLUItem(dataConnection, qualifiers, criteria.getLocaleRequestor());
        if (selected.length > 0)
        {
            configureStoreCouponRules(selected[0]);
        }
        return selected;
    }

    
    /**
     * Removes the store coupon item id from the source criteria.
     */
    public static void configureStoreCouponRules(PLUItemIfc coupon)
    {
        String couponID = coupon.getItemID();
        AdvancedPricingRuleIfc temp = null;

        // for each rule associated with the store coupon
        for (Iterator<AdvancedPricingRuleIfc> i = coupon.advancedPricingRules(); i.hasNext();)
        {
            temp = i.next();

            // remove the couponID from list of source criteria
            /* Removing this id, breaks
             * ItemContainerProxy#areAllStoreCouponsApplied() since the coupon
             * id is no longer a source. Why is this done? It is commented out
             * in JdbcReadTransaction#selectAdvancedPricingRules too
             */
            // temp.getSourceList().removeEntry(couponID);
            // if the StoreCoupon is transaction scope
            // set the checkSources to false and SourcesAreTargets to true
            if (temp.isScopeTransaction())
            {
                temp.activateTransactionDiscount();
            }
            // Set the ReferenceID and Code for this rule
            temp.setReferenceID(couponID);
            temp.setReferenceIDCode(DiscountRuleConstantsIfc.REFERENCE_ID_CODE_STORE_COUPON);
        }
    }

}
