/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcUpdateTransactionTLogIDs.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    1    360Commerce 1.0         11/9/2006 6:42:35 PM   Jack G. Swan    
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:25:45    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:46     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:54     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:06     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:43  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:19  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:33:44   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jan 22 2003 15:06:48   mpm
 * Preliminary merge of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.0   Sep 05 2002 11:08:58   msg
 * Initial revision.
 *
 *    Rev 1.2   Jul 02 2002 13:26:40   vpn-mpm
 * Changed batch identifier to a string.
 *
 *    Rev 1.1   04 Jun 2002 14:33:46   vpn-mpm
 * Modified to update with batch ID on transaction entry object.
 *
 *    Rev 1.0   May 28 2002 08:29:54   mpm
 * Initial revision.
 * Resolution for Domain SCR-45: TLog facility
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;

/**
 * This operation updates the t-log batch ID column in the transaction table.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class JdbcUpdateTransactionTLogIDs extends JdbcUpdateTransactionBatchIDs implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 165113332630204011L;
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Class constructor.
     */
    public JdbcUpdateTransactionTLogIDs()
    {
        super();
        setName("JdbcUpdateTransactionTLogIDs");
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
        sql.addColumn(FIELD_TRANSACTION_TLOG_BATCH_IDENTIFIER, transaction.getBatchID());
        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(transaction.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getTransactionID().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, transaction.getTransactionID().getSequenceNumber());
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(transaction.getBusinessDate()));

        return (sql);
    }

    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
