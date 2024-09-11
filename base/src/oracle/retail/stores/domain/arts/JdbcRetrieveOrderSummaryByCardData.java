/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveOrderSummaryByCardData.java /main/4 2014/06/17 15:26:37 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/16/14 - CAE Order summary enhancement phase I
* yiqzhao     12/24/12 - Refactoring xc formatter, transformer and others.
* tksharma    12/10/12 - commons-lang update 3.1
* sgu         07/17/12 - add order summary search by card token or masked
*                        number
* sgu         07/17/12 - add new file
* sgu         07/17/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.utility.CardDataIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.commons.lang3.StringUtils;

/**
 * This class has been deprecated in the wake of an enhancement from JDBC 
 * operation to JPA operation. In lieu of this use {@link #JpaRetrieveOrderSummary}
 * @deprecated as of 14.1
 */
public class JdbcRetrieveOrderSummaryByCardData extends JdbcRetrieveOrderSummary implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 8865749402305071372L;

    /**
     * Class constructor.
     */
    public JdbcRetrieveOrderSummaryByCardData()
    {
        super();
        setName("JdbcRetrieveOrderSummaryByCardData");
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
                     "JdbcRetrieveOrderSummaryByCardData.execute");

        // set data connection
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // grab arguments and call RetrieveOrderSummaryByCustomer()
        OrderSearchCriteriaIfc orderSearchCriteria =
          (OrderSearchCriteriaIfc) action.getDataObject();
        OrderSummaryEntryIfc[] orderSumList =
          retrieveOrderSummaries(connection, orderSearchCriteria);

        dataTransaction.setResult(orderSumList);

        if (logger.isDebugEnabled()) logger.debug(
                    "JdbcRetrieveOrderSummaryByCardData.execute");
    }

    /**
     * Sets qualifiers to select SQL statement based on search key.
     *
     * @param sql SQLSelectStatement object
     * @param orderSearchKey OrderSearchKey object
     */
    protected void setSelectQualifiers(SQLSelectStatement sql, OrderSearchCriteriaIfc OrderSearchCriteriaIfc)
    {
        super.setSelectQualifiers(sql, OrderSearchCriteriaIfc);

        CardDataIfc cardData = OrderSearchCriteriaIfc.getCardData();

        sql.setDistinctFlag(true);

        // Join order tender table
        sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM+" "+ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM
                +" ON " +ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM+"."+FIELD_RETAIL_STORE_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_STORE_ID
                +" AND "+ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM+"."+FIELD_WORKSTATION_ID + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID
                +" AND "+ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM+"."+FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER
                +" AND "+ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM+"."+FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_ORDER+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);


        if (!StringUtils.isBlank(cardData.getCardToken()))
        {
            // test and add qualifiers for card token
            sql.addQualifier(ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM + "." +FIELD_TENDER_AUTHORIZATION_ACCOUNT_NUMBER_TOKEN + " = '" + cardData.getCardToken() + "'");
        }
        else
        {
            // test and add qualifiers for masked card number
            sql.addQualifier(ALIAS_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM + "." +FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_MASKED + " like '" +
                    cardData.getLeadingCardNumber() + "%" + cardData.getTrailingCardNumber() + "'");
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
