/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcRetrieveTransactionIDsByTimePeriod.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
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
 *  1    360Commerce 1.0         7/23/2007 1:12:12 PM   Maisa De Camargo
 *       Retrieve transactions by Time Period.
 *       
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
    This operation reads a list of transaction IDs by Time Period
**/
public class JdbcRetrieveTransactionIDsByTimePeriod
extends JdbcRetrieveTransactionIDsByBatchID
{
    /** Generated Serial UID */
    private static final long serialVersionUID = 1L;
    
    /**
        Class constructor.
     */
    public JdbcRetrieveTransactionIDsByTimePeriod()
    {
        super();
        setName("JdbcRetrieveTransactionIDsByTimePeriod");
    }

    /**
       Adds qualifiers for SQL statement to be used for selecting transaction
       IDs.
       @param sql SQLSelectStatement under construction
       @param tLogEntry TLog entry object to be used as key
     */
    protected void addSelectTransactionIDsQualifiers(SQLSelectStatement sql,
                                                     POSLogTransactionEntryIfc tLogEntry)
    {
        // add store ID qualifier, if necessary
        if (!Util.isEmpty(tLogEntry.getStoreID()))
        {
            sql.addQualifier(ARTSDatabaseIfc.FIELD_RETAIL_STORE_ID,
                             inQuotes(tLogEntry.getStoreID()));
        }

        // If the start time is not null, then the add a qualifier that searhes
        // for transactions which have a transaction end time which equal to or
        // or greater than the start time.
        if (tLogEntry.getStartTime() != null)
        {
            sql.addQualifier(ARTSDatabaseIfc.FIELD_TRANSACTION_END_DATE_TIMESTAMP + " >= "   
                    + dateToSQLTimestampString(tLogEntry.getStartTime().dateValue()));
        }

        // If the end time is not null, then the add a qualifier that searhes
        // for transactions which have a transaction end time which equal to or
        // or less than the end time.
        if (tLogEntry.getEndTime() != null)
        {
            sql.addQualifier(ARTSDatabaseIfc.FIELD_TRANSACTION_END_DATE_TIMESTAMP + " <= " 
                    + dateToSQLTimestampString(tLogEntry.getEndTime().dateValue()));
        }
    }
}
