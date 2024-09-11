/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcMarkInventoryReservationOrderList.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    mchellap  02/20/09 - Added quotes for db2 compatibility
 *    mchellap  11/13/08 - Inventory Reservation Module
 *    mchellap  11/07/08 - Jdbc class to mark order transactions after
 *                         inventory reservation export
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;

/**
 * This operation updates the Inventory Reservation batch ID column in the
 * transaction table.
 */
public class JdbcMarkInventoryReservationOrderList extends JdbcUpdateTransactionBatchIDs implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -4224473044067059322L;

    /**
     * Default constructor.
     */
    public JdbcMarkInventoryReservationOrderList()
    {
        super();
        setName("JdbcMarkInventoryReservationOrderList");
    }

    /**
     * Builds SQL statement for updating Inventory Reservation Batch ID for a
     * transaction.
     * 
     * @param transaction transaction entry
     * @return SQL statement for performing update
     */
    protected SQLUpdateStatement buildUpdateSQL
      (POSLogTransactionEntryIfc transaction)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // set table
        sql.setTable(TABLE_TRANSACTION);
        // set column
        sql.addColumn(FIELD_TRANSACTION_INVENTORY_RESERVATION_BATCH_IDENTIFIER,
                inQuotes(transaction.getBatchID()));
        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID,
                         inQuotes(transaction.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID,
                         inQuotes(transaction.getTransactionID().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                         transaction.getTransactionID().getSequenceNumber());
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE,
                         dateToSQLDateString(transaction.getBusinessDate()));

        return (sql);
    }

}
