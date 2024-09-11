/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveOrderSummaryByCustomer.java /main/24 2014/06/17 15:26:37 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/16/14 - CAE Order summary enhancement phase I
 *    abondala  10/14/13 - fix the query
 *    abondala  06/03/13 - fixed the query that retrieve orders by customer
 *                         information
 *    abondala  05/30/13 - customer search should not be case sensitive
 *    abondala  05/28/13 - add business name field to the order search screen
 *    yiqzhao   01/16/13 - Use non domain object CustomerData as part of
 *                         orderSearchCriteria. It is also used at storeservice
 *                         in CO.
 *    yiqzhao   12/24/12 - Refactoring xc formatter, transformer and others.
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       07/24/12 - fix defect in order summary retrieval
 *    sgu       07/17/12 - enhance order summary search by item and customer
 *                         info
 *    sgu       07/17/12 - add item serach criteria
 *    sgu       07/16/12 - add order search by customer info
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/25/2006 4:11:20 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:47 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:01 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:28:09    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:47     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:01     Robert Pearse
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
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Jun 03 2002 16:39:12   msg
 * Initial revision.
 *
 *    Rev 1.2   05 Apr 2002 15:55:56   dfh
 * fix narrow search by customer
 * Resolution for POS SCR-178: CR/Order, incomplete date range search, dialog text erroneous
 *
 *    Rev 1.1   Mar 18 2002 22:46:14   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:06:22   msg
 * Initial revision.
 *
 *    Rev 1.4   Jan 23 2002 21:16:50   dfh
 * removed void exclusion qualifier
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   Jan 02 2002 15:30:20   dfh
 * updates to not retrieve voided new special orders
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Jan 02 2002 13:56:40   dfh
 * updates to not return the order summary if the order status
 *  is undefined or suspended-canceled
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   29 Nov 2001 07:05:32   mpm
 * Continuing order modifications.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 20 2001 15:57:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:10   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.CustomerDataIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.commons.lang3.StringUtils;

/**
 * This operation reads all of the matching a specified order ID.
 * This class has been deprecated in wake of an enhancement from JDBC 
 * operation to JPA operation. In lieu of this use {@link #JpaRetrieveOrderSummary}
 * @version $Revision: /main/24 $
 * @deprecated as of 14.1
 */
public class JdbcRetrieveOrderSummaryByCustomer extends JdbcRetrieveOrderSummary implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 698434487258245619L;

    /**
     * Class constructor.
     */
    public JdbcRetrieveOrderSummaryByCustomer()
    {
        super();
        setName("JdbcRetrieveOrderSummaryByCustomer");
    }

    /**
     * Executes the SQL statements against the database.
     *
     * @param dataTransaction
     * @param dataConnection
     * @param action
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "JdbcRetrieveOrderSummaryByCustomer.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call RetrieveOrderSummaryByCustomer()
        OrderSearchCriteriaIfc orderSearchCriteria =
          (OrderSearchCriteriaIfc) action.getDataObject();
        OrderSummaryEntryIfc[] orderSumList =
          retrieveOrderSummaries(connection, orderSearchCriteria);

        dataTransaction.setResult(orderSumList);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcRetrieveOrderSummaryByCustomer.execute");
    }

    /**
     * Sets qualifiers to select SQL statement based on search key.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchKey OrderSearchKey object
     */
    protected void setSelectQualifiers(SQLSelectStatement sql, OrderSearchCriteriaIfc orderSearchCriteria)
    {
        super.setSelectQualifiers(sql, orderSearchCriteria);

        CustomerDataIfc customer = orderSearchCriteria.getCustomer();

        if (!StringUtils.isBlank(customer.getCustomerID()))
        {
            // test and add qualifiers for customer ID
            sql.addQualifier(ALIAS_ORDER + "." +FIELD_CUSTOMER_ID + " = '" + orderSearchCriteria.getCustomer().getCustomerID() + "'");
        }
        else
        {
            sql.setDistinctFlag(true);

            // Join order item table
            sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_ORDER_LINE_ITEM_STATUS+" "+ALIAS_TRANSACTION_ORDER_LINE_ITEM
                    +" ON " +ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_RETAIL_STORE_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_STORE_ID
                    +" AND "+ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_WORKSTATION_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID
                    +" AND "+ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER
                    +" AND "+ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);

            // Join order delivery detail table
            sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_TRANSACTION_ORDER_DELIVERY_DETAIL+" "+ALIAS_TRANSACTION_ORDER_DELIVERY_DTL
                    +" ON " +ALIAS_TRANSACTION_ORDER_DELIVERY_DTL+"."+FIELD_RETAIL_STORE_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_STORE_ID
                    +" AND "+ALIAS_TRANSACTION_ORDER_DELIVERY_DTL+"."+FIELD_WORKSTATION_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID
                    +" AND "+ALIAS_TRANSACTION_ORDER_DELIVERY_DTL+"."+FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER
                    +" AND "+ALIAS_TRANSACTION_ORDER_DELIVERY_DTL+"."+FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);

            StringBuilder pickupQualifier = new StringBuilder();
            StringBuilder deliveryQualifier = new StringBuilder();
            StringBuilder orderQualifier = new StringBuilder();


            // add qualifiers for customer company name
            if (!StringUtils.isBlank(customer.getCompanyName()))
            {
                pickupQualifier.append(ALIAS_TRANSACTION_ORDER_LINE_ITEM + "." + FIELD_PICKUP_ORDER_LNAMEE_UPPER_CASE + " LIKE UPPER ('" + customer.getCompanyName() + "')");
                deliveryQualifier.append(ALIAS_TRANSACTION_ORDER_DELIVERY_DTL + "." + FIELD_DELIVERY_ORDER_LASTNAME_UPPER_CASE + " LIKE UPPER ('" + customer.getCompanyName() + "')");
                orderQualifier.append(ALIAS_ORDER + "." + FIELD_CONTACT_LAST_NAME_UPPER_CASE + " LIKE UPPER ('" + customer.getCompanyName() + "')");
            }
            else
            {
                // add qualifiers for customer first name
                if (!StringUtils.isBlank(customer.getFirstName()))
                {
                    pickupQualifier.append(ALIAS_TRANSACTION_ORDER_LINE_ITEM + "." + FIELD_PICKUP_ORDER_FNAMEE_UPPER_CASE + " LIKE UPPER ('" + customer.getFirstName() + "')");
                    deliveryQualifier.append(ALIAS_TRANSACTION_ORDER_DELIVERY_DTL + "." + FIELD_DELIVERY_ORDER_FIRST_NAME_UPPER_CASE + " LIKE UPPER ('" + customer.getFirstName() + "')");
                    orderQualifier.append(ALIAS_ORDER + "." + FIELD_CONTACT_FIRST_NAME_UPPER_CASE + " LIKE UPPER ('" + customer.getFirstName() + "')");
                }

                // add qualifiers for customer last name
                if (!StringUtils.isBlank(customer.getLastName()))
                {
                    if (pickupQualifier.length() > 0) pickupQualifier.append(" AND ");
                    pickupQualifier.append(ALIAS_TRANSACTION_ORDER_LINE_ITEM + "." + FIELD_PICKUP_ORDER_LNAMEE_UPPER_CASE + " LIKE UPPER ('" + customer.getLastName() + "')");
                    if (deliveryQualifier.length() > 0) deliveryQualifier.append(" AND ");
                    deliveryQualifier.append(ALIAS_TRANSACTION_ORDER_DELIVERY_DTL + "." + FIELD_DELIVERY_ORDER_LASTNAME_UPPER_CASE + " LIKE UPPER ('" + customer.getLastName() + "')");
                    if (orderQualifier.length() > 0) orderQualifier.append(" AND ");
                    orderQualifier.append(ALIAS_ORDER + "." + FIELD_CONTACT_LAST_NAME_UPPER_CASE + " LIKE UPPER ('" + customer.getLastName() + "')");
                    
                }
            }
            
            if (!StringUtils.isBlank(customer.getPhoneNumber()))
            {
                // add qualifiers for customer phone #
                if (pickupQualifier.length() > 0) pickupQualifier.append(" AND ");
                pickupQualifier.append(ALIAS_TRANSACTION_ORDER_LINE_ITEM + "." + FIELD_PICKUP_ORDER_PHONE + " = '" + customer.getPhoneNumber() + "'");
                if (deliveryQualifier.length() > 0) deliveryQualifier.append(" AND ");
                deliveryQualifier.append(ALIAS_TRANSACTION_ORDER_DELIVERY_DTL + "." + FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER + " = '" + customer.getPhoneNumber() + "'");
                
            }            

            StringBuilder qualifiers = new StringBuilder();
            qualifiers.append("( ");
            
            boolean orderQualifierSet = false;
            boolean pickupQualifierSet = false;
            if(orderQualifier.toString().trim().length() > 0)
            {
                qualifiers.append(" ("+orderQualifier.toString()+") " );
                orderQualifierSet = true;
            }
            
            if(orderQualifierSet)
            {
                if(pickupQualifier.toString().trim().length() > 0)
                {
                    qualifiers.append(" OR ("+pickupQualifier.toString()+") ");
                }
                pickupQualifierSet = true;
            }
            else
            {
                if(pickupQualifier.toString().trim().length() > 0)
                {
                    qualifiers.append("("+pickupQualifier.toString()+") ");
                }
                pickupQualifierSet = true;
            }
            
            if(orderQualifierSet || pickupQualifierSet)
            {
                if(deliveryQualifier.toString().trim().length() > 0)
                {
                    qualifiers.append(" OR ("+deliveryQualifier.toString()+")");
                } 
            }
            else
            {
                if(deliveryQualifier.toString().trim().length() > 0)
                {
                    qualifiers.append(" OR ("+deliveryQualifier.toString()+")");
                }
            }
            qualifiers.append(" )");
            
            sql.addQualifier(qualifiers.toString());            
        }

        sql.addQualifier(ALIAS_ORDER+"."+FIELD_ORDER_STATUS + " != " + OrderConstantsIfc.ORDER_STATUS_UNDEFINED);
        sql.addQualifier(ALIAS_ORDER+"."+FIELD_ORDER_STATUS + " != " + OrderConstantsIfc.ORDER_STATUS_SUSPENDED_CANCELED);

    }

    /**
     * Adds date qualifiers to select SQL statement based on search key and
     * specified column.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchKey OrderSearchKey object
     * @param columnName column name to check against date
     */
    protected void addDateQualifiers(SQLSelectStatement sql, OrderSearchCriteriaIfc orderSearchCriteria, String columnName)
    {
        super.addDateQualifiers(sql, orderSearchCriteria, FIELD_ORDER_STATUS_CHANGE);
    }
}
