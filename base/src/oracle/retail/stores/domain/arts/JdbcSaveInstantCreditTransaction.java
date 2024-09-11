/* ===========================================================================
* Copyright (c) 2003, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveInstantCreditTransaction.java /main/13 2012/05/21 15:50:19 cgreene Exp $
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
 *    sgu       08/23/11 - change to use inQuotes
 *    sgu       08/23/11 - check nullpointer for approval status
 *    sgu       05/16/11 - move instant credit approval status to its own class
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
 *    7    360Commerce 1.6         6/20/2006 4:56:59 PM   Brendan W. Farrell
 *         Fixed compile error.
 *    6    360Commerce 1.5         6/20/2006 3:36:48 PM   Brendan W. Farrell
 *         Fixed unit tests for UDM.
 *    5    360Commerce 1.4         6/8/2006 6:11:44 PM    Brett J. Larsen CR
 *         18490 - UDM - InstantCredit AuthorizationResponseCode changed to a
 *         String
 *    4    360Commerce 1.3         1/25/2006 4:11:21 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:48 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:02 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:49    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:48     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:02     Robert Pearse
 *
 *   Revision 1.6  2004/04/09 16:55:46  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:37  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:47  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:24  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 31 2003 10:38:16   nrao
 * Modified data operation to write to the new Instant Credit table.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts into the transaction table.
 *
 * @version $Revision: /main/13 $
 */
public class JdbcSaveInstantCreditTransaction extends JdbcSaveTransaction implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = -5274927379745108423L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveInstantCreditTransaction.class);

    /**
     * Class constructor.
     */
    public JdbcSaveInstantCreditTransaction()
    {
        super();
        setName("JdbcSaveInstantCreditTransaction");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveInstantCreditTransaction.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        InstantCreditTransactionIfc trans = (InstantCreditTransactionIfc)action.getDataObject();
        insertInstantCreditTransaction(connection, trans);

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveInstantCreditTransaction.execute()");
    }

    /**
       Adds an instant credit enrollment transaction.
       <P>
       @param  dataConnection  connection to the db
       @param  transaction     an instantcredit transaction
       @exception DataException upon error
     */
    public void insertInstantCreditTransaction(JdbcDataConnection dataConnection,
                                          InstantCreditTransactionIfc transaction)
        throws DataException
    {
        // write to tr_trn if transaction is of type Instant Credit Enrollment
        if (transaction.getTransactionType() == TransactionConstantsIfc.TYPE_INSTANT_CREDIT_ENROLLMENT)
        {
            insertTransaction(dataConnection, transaction);
        }

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_INSTANT_CREDIT);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_OPERATOR_ID, getOperatorID(transaction));
        sql.addColumn(FIELD_EMPLOYEE_ID, getSalesAssociateID(transaction));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_AUTHORIZATION_RESPONSE, getAuthorizationResponse(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de);
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e);
            throw new DataException(DataException.UNKNOWN, "insertTransaction", e);
        }
    }

    /**
       Returns the transaction sequence number
       <P>
       @param  transaction     instant credit transaction
       @return  The transaction sequence number
     */
    public String getTransactionSequenceNumber(InstantCreditTransactionIfc transaction)
    {
        return(String.valueOf(transaction.getTransactionSequenceNumber()));
    }

    /**
       Returns the Store ID
       <P>
       @param  transaction     instant credit transaction
       @return  The Store ID
     */
    public String getStoreID(InstantCreditTransactionIfc transaction)
    {
        return inQuotes(transaction.getWorkstation().getStoreID());
    }

    /**
       Returns the Workstation ID
       <P>
       @param  transaction     instant credit transaction
       @return  The Workstation ID
     */
    public String getWorkstationID(InstantCreditTransactionIfc transaction)
    {
        return inQuotes(transaction.getWorkstation().getWorkstationID());
    }

    /**
       Returns the Operator ID
       <P>
       @param  transaction     instant credit transaction
       @return  The Operator ID
     */
    public String getOperatorID(InstantCreditTransactionIfc transaction)
    {
        return inQuotes(transaction.getCashier().getEmployeeID());
    }

    /**
        Returns the string value to be used in the database for the
        sales associate ID
        <p>
        @param transaction      instant credit transaction
        @return The sales associate ID
     */
    public String getSalesAssociateID(InstantCreditTransactionIfc transaction)
    {
        String value = null;
        String empID = null;
        // if sales associate ID is available, get it
        if (transaction.getSalesAssociate() != null)
        {
            empID = transaction.getSalesAssociate().getEmployeeID();
        }

        if (empID == null)
        {
            value = "null";
        }
        else
        {
            value = inQuotes(empID);
        }
        return(value);
    }

    /**
       Returns the string value for the business day
       <P>
       @param  transaction     instant credit transaction
       @return  The business day
     */
    public String getBusinessDayString(InstantCreditTransactionIfc transaction)
    {
        return(dateToSQLDateString(transaction.getBusinessDay()));
    }

    /**
       Returns the authorization response
       <P>
       @param  transaction     instant credit transaction
       @return  The authorization response
     */
    public String getAuthorizationResponse(InstantCreditTransactionIfc transaction)
    {
        // if available, get the response code
        if((transaction.getInstantCredit() != null) && (transaction.getInstantCredit().getApprovalStatus() != null))
        {
            return inQuotes(transaction.getInstantCredit().getApprovalStatus().getCode());
        }

        // otherwise return 0
        return inQuotes(0);
    }
}
