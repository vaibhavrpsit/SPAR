/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateTransactionSIMTLogIDs.java /main/1 2013/01/16 11:47:21 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     11/30/12 - CR 204 - Update SIM batch id
* vtemker     11/28/12 - intial version
* vtemker     11/28/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.arts;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * This operation updates the t-log batch ID column in the transaction table.
 * 
 * @version $Revision: /main/1 $
 */
public class JdbcUpdateTransactionSIMTLogIDs extends JdbcUpdateTransactionBatchIDs implements ARTSDatabaseIfc
{

    private static final long serialVersionUID = 7793228958253538220L;
    
    /**
     * Class constructor.
     */
    public JdbcUpdateTransactionSIMTLogIDs()
    {
        super();
        setName("JdbcUpdateTransactionSIMTLogIDs");
    }

    /**
     * Builds SQL statement for updating TLog ID for a transaction.
     * 
     * @param transaction transaction entry
     * @return SQL statement for performing update
     */
    protected SQLUpdateStatement buildUpdateSQL(POSLogTransactionEntryIfc transaction)
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        
        // set table
        sql.setTable(TABLE_TRANSACTION);
        // set column
        sql.addColumn(FIELD_TRANSACTION_SIMTLOG_BATCH_IDENTIFIER,
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

        return(sql);
    }
    
}