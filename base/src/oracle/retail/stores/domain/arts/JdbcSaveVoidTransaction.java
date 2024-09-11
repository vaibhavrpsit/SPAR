/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveVoidTransaction.java /main/18 2012/05/21 15:50:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    yiqzhao   03/03/10 - Update StoreSafeTenderCurrentAmount for postvoid
 *                         TillPickUP and TillLoan.
 *    abondala  01/03/10 - update header date
 *    mahising  03/17/09 - Fixed issue to update bank deposit amount for till
 *                         loan void transaction
 *    mahising  02/25/09 - Fixed for updating till loan post void transaction
 *
 * ===========================================================================
 * $Log:
 *    7    360Commerce 1.6         2/22/2008 2:15:22 PM   Christian Greene
 *         CR30318 Roll back to two version to CTR slowness fix also include
 *         CompressionUtility into fixes.
 *    6    360Commerce 1.5         2/20/2008 3:22:09 PM   Alan N. Sinton  CR
 *         30318: Reverting changes to unbreak CTR.
 *    5    360Commerce 1.4         2/13/2008 1:50:19 PM   Christian Greene
 *         Update returns-lookup to eliminate the extra isVoided call to
 *         CentralOffice by setting voided transaction's status upon
 *         post-void. Also compress XML data retrieved from CO during
 *         returns-lookup.
 *    4    360Commerce 1.3         1/25/2006 4:11:25 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/30/2005 17:24:38    Deepanshu       CR
 *         6261: Added Postvoid transaction reason code
 *    3    360Commerce1.2         3/31/2005 15:28:45     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:51     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:04     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:44  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
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
 *    Rev 1.1   Nov 24 2003 14:46:50   baa
 * throw data exception
 * Resolution for 3494: JdbcSaveVoidTransaction incorrectly updates record if insert fails
 *
 *    Rev 1.0.1.0   Nov 24 2003 14:41:46   baa
 * throw exception if void trans. cannot be inserted
 * Resolution for 3494: JdbcSaveVoidTransaction incorrectly updates record if insert fails
 *
 *    Rev 1.0   Aug 29 2003 15:33:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Jul 04 2003 09:42:28   jgs
 * Modified to include read/write of id_ws_vd column.
 * Resolution for 2574: transactions not uniquely identifiable enough for voiding
 *
 *    Rev 1.0   Jun 03 2002 16:40:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 22:49:04   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 20 2001 15:57:36   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:33:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.tender.TenderTypeMap;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts into the transaction and control transaction
 * tables.
 * <P>
 *
 * @version $Revision: /main/18 $
 */
public class JdbcSaveVoidTransaction extends JdbcSaveControlTransaction
{

    // Constant for serialization compatibility
    private static final long serialVersionUID = 1984757683673712L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveVoidTransaction.class);

    /**
     * Class constructor.
     * <P>
     */
    public JdbcSaveVoidTransaction()
    {
        super();
        setName("JdbcSaveVoidTransaction");
    }

    /**
     * Executes the SQL statements against the database.
     * <P>
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveVoidTransaction.execute()");

        /*
         * getUpdateCount() is about the only thing outside of DataConnectionIfc
         * that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTransaction trans = (ARTSTransaction)action.getDataObject();
        saveVoidTransaction(connection, (VoidTransactionIfc)trans.getPosTransaction());

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveVoidTransaction.execute()");
    }

    /**
     * Saves a void transaction and updates the voided transaction's status.
     * <P>
     *
     * @param dataConnection connection to the db
     * @param transaction a void transaction
     * @exception DataException upon error
     */
    public void saveVoidTransaction(JdbcDataConnection dataConnection, VoidTransactionIfc transaction)
            throws DataException
    {
        try
        {
            insertPostVoidTransaction(dataConnection, transaction);
            updateVoidedTransactionStatus(dataConnection, transaction);

            if ( transaction.getOriginalTransaction().getTransactionType() == TransactionConstantsIfc.TYPE_LOAN_TILL ||
            	 transaction.getOriginalTransaction().getTransactionType() == TransactionConstantsIfc.TYPE_PICKUP_TILL )
            {
                updateStoreSafeCurrentAmount(dataConnection, transaction);
            }

        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;

        }
    }

    /**
     * Updates the post void transaction table.
     * <P>
     *
     * @param dataConnection connection to the db
     * @param transaction a pos transaction
     * @exception DataException thrown when an error occurs.
     */
    public void updatePostVoidTransaction(JdbcDataConnection dataConnection, VoidTransactionIfc transaction)
            throws DataException
    {
        /*
         * Update the Control Transaction table first.
         */
        updateControlTransaction(dataConnection, transaction);

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_POST_VOID_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_VOIDED, getTransactionSequenceNumber(transaction.getOriginalTransaction()));
        sql.addColumn(FIELD_VOIDED_WORKSTATION_ID, getWorkstationID(transaction.getOriginalTransaction()));
        sql.addColumn(FIELD_VOIDED_REASON_CODE, inQuotes(transaction.getReason().getCode()));

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "updatePostVoidTransaction", e);
        }
    }

    /**
     * Updates voided transaction by setting the status to voided.
     * <P>
     *
     * @param dataConnection connection to the db
     * @param transaction a pos transaction
     * @exception DataException thrown when an error occurs.
     */
    public void updateVoidedTransactionStatus(JdbcDataConnection dataConnection, VoidTransactionIfc transaction)
            throws DataException
    {
        /*
         * Update the Control Transaction table first.
         */
        updateControlTransaction(dataConnection, transaction);

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_TRANSACTION_STATUS_CODE, TransactionConstantsIfc.STATUS_VOIDED);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction.getOriginalTransaction()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction.getOriginalTransaction()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "updateVoidedTransactionStatus", e);
        }
    }

    /**
     * Inserts into the post void transaction table.
     * <P>
     *
     * @param dataConnection connection to the data source
     * @param transaction a post void transaction
     * @exception DataException thrown when an error occurs.
     */
    public void insertPostVoidTransaction(JdbcDataConnection dataConnection, VoidTransactionIfc transaction)
            throws DataException
    {
        /*
         * Update the Control Transaction table first.
         */
        insertControlTransaction(dataConnection, transaction);

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_POST_VOID_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_VOIDED, getTransactionSequenceNumber(transaction.getOriginalTransaction()));
        sql.addColumn(FIELD_VOIDED_WORKSTATION_ID, getWorkstationID(transaction.getOriginalTransaction()));
        if (transaction.getReason() != null)
        {
            sql.addColumn(FIELD_VOIDED_REASON_CODE, inQuotes(transaction.getReason().getCode()));
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "insertTransaction", e);
        }
    }

    /**
     * updates store safe tender history table.
     * <P>
     *
     * @param dataConnection connection to the data source
     * @param transaction Void Transaction
     * @exception DataException thrown when an error occurs.
     */
    public void updateStoreSafeCurrentAmount(JdbcDataConnection dataConnection, VoidTransactionIfc transaction)
            throws DataException
    {
        String tendertype = null;
        String countryCode = null;
        TillAdjustmentTransactionIfc tillAdjustment = (TillAdjustmentTransactionIfc)transaction
                .getOriginalTransaction();
        FinancialCountTenderItemIfc[] tenderItems = tillAdjustment.getTenderCount().getTenderItems();
        if (tenderItems != null)
        {
            tendertype = TenderTypeMap.getTenderTypeMap().getCode(tenderItems[tenderItems.length - 1].getTenderDescriptor().getTenderType());
            	//tenderItems[tenderItems.length - 1].getDescription();
            countryCode = tenderItems[tenderItems.length - 1].getCurrencyCode();
        }
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // Table
        sql.setTable(TABLE_STORE_SAFE_TENDER_HISTORY);
        // Fields
        sql.addColumn(FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT, FIELD_STORE_SAFE_TENDER_CURRENT_AMOUNT + "+"
        		+ safeSQLCast(tillAdjustment.getAdjustmentAmount().toString()));

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_TENDER_SUBTYPE
                + " = "
                + JdbcDataOperation.makeSafeString(emptyStringToSpaceString(tillAdjustment.getTender()
                        .getTenderSubType())));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE + " = " + JdbcDataOperation.makeSafeString(tendertype));
        sql.addQualifier(FIELD_CURRENCY_ISSUING_COUNTRY_CODE + " = " + JdbcDataOperation.makeSafeString(countryCode));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "UpdateTillLoanTransaction", e);
        }

    }
}
