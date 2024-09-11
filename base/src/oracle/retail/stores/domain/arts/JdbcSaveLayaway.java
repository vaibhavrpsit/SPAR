/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveLayaway.java /main/16 2012/05/21 15:50:19 cgreene Exp $
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
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    mchellap  11/27/08 - Modified insertLayawayStatus to accept layaway as an
 *                         argument
 *    mchellap  11/13/08 - Modified SIM actions
 *    mchellap  11/13/08 - Inventory Reservation Module
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/27/2006 7:26:58 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    4    360Commerce 1.3         1/25/2006 4:11:22 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:27:52    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
 *
 *   Revision 1.7  2004/06/29 21:58:58  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.6  2004/04/09 16:55:47  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:38  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:49  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:26  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   May 08 2003 13:52:20   adc
 * Update the training mode flag
 * Resolution for 2336: Training mode, voided layaway deletion transaction is blocked at Queue
 *
 *    Rev 1.1   Jun 10 2002 11:14:54   epd
 * Merged in changes for Oracle
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.4   Jun 07 2002 17:47:42   epd
 * Merging in fixes made for McDonald's Oracle demo
 * Resolution for Domain SCR-83: Merging database fixes into base code
 *
 *    Rev 1.3   May 12 2002 23:39:36   mhr
 * db2 fixes for quoting.  chars must be quoted and ints must not be quoted.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.2   Mar 30 2002 09:37:56   mpm
 * Imported changes for PostgreSQL compatibility from 5.0.
 * Resolution for Backoffice SCR-795: Employee Assignment report abends under Postgresql
 *
 *    Rev 1.1   Mar 18 2002 22:48:24   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:16   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 01 2002 21:42:44   dfh
 * added method to parse long layaway legal string into pieces of 255, 255, 255, 135, includes newline chars
 * Resolution for POS SCR-1414: Layaway does not support multi-line legal statement
 *
 *
 *    Rev 1.0   Sep 20 2001 15:57:08   msg
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 12:34:04   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This operation inserts and updates the layaway table from the LayawayIfc
 * object.
 * 
 * @version $Revision: /main/16 $
 * @see oracle.retail.stores.domain.financial.LayawayIfc
 */
public abstract class JdbcSaveLayaway extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 3367128025842973255L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveLayaway.class);

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/16 $";

    /*** length of the layaway legal statement - matches database **/
    protected static final int LEGAL_STATEMENT_LENGTH = 900;

    /**
     * Perform layaway update.
     * 
     * @param dataConnection JdbcDataConnection
     * @param layaway LayawayIfc reference
     * @exception DataException thrown if error occurs
     */
    public void updateLayaway(JdbcDataConnection dataConnection,
                              LayawayIfc layaway)
                              throws DataException
    {
        // build sql statement
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_LAYAWAY);
        addUpdateColumns(layaway, sql);
        addUpdatedPaymentsCollected(layaway, sql);
        addUpdateQualifiers(layaway, sql);

        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error( e.toString());
            throw new DataException(DataException.UNKNOWN, "Layaway update", e);
        }
    }
    // end updateLayaway()

    /**
        Perform layaway update status. <P>
        @param dataConnection JdbcDataConnection
        @param layaway LayawayIfc reference
        @exception DataException thrown if error occurs
     */
    public void insertLayawayStatus(JdbcDataConnection dataConnection,
                              LayawayIfc layaway)
                              throws DataException
    {
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();

        // add table, columns, qualifiers
        sql.setTable(TABLE_RETAIL_TRANSACTION_LAYAWAY_STATUS);

        sql.addColumn(FIELD_RETAIL_STORE_ID, makeSafeString(layaway.getStoreID()));
        sql.addColumn(FIELD_WORKSTATION_ID,  makeSafeString(layaway.getWorkStationID()));
        // Update the status with current transaction
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, layaway.getCurrentTransactionSequenceNo());
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(layaway.getCurrentTransactionBusinessDate()));
        sql.addColumn(FIELD_LAYAWAY_ID,  makeSafeString(layaway.getLayawayID()));
        sql.addColumn(FIELD_LAYAWAY_STATUS, layaway.getStatus());
        sql.addColumn(FIELD_LAYAWAY_PREVIOUS_STATUS, layaway.getPreviousStatus());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error( e.toString());
            throw new DataException(DataException.UNKNOWN, "Layaway update", e);
        }
    }
    /**
        Perform layaway update. <P>
        @param dataConnection JdbcDataConnection
        @param layaway LayawayIfc reference
        @exception DataException thrown if error occurs
     */
    public void updateLayawayForPayment(JdbcDataConnection dataConnection,
                              LayawayIfc layaway)
                              throws DataException
    {
        // build sql statement
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_LAYAWAY);
        addUpdateLayawayForPaymentColumns(layaway,
                                          sql);
        addUpdatedPaymentsCollected(layaway,
                                    sql);
        addUpdateQualifiers(layaway,
                            sql);
        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(
                         de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(
                         e.toString());
            throw new DataException(DataException.UNKNOWN,
                                    "Layaway update",
                                    e);
        }

    }

    /**
        Perform layaway insert. <P>
        @param dataConnection JdbcDataConnection
        @param layaway LayawayIfc reference
        @exception DataException thrown if error occurs
     */
    public void insertLayaway(JdbcDataConnection dataConnection,
                              LayawayIfc layaway)
                              throws DataException
    {
        boolean updateRequired = false;
        // build sql statement
        SQLInsertStatement sql = new SQLInsertStatement();
        // add table, columns, qualifiers
        sql.setTable(TABLE_LAYAWAY);
        addInsertColumns(layaway, sql);
        insertPaymentCollected(layaway, sql);

        // execute statement
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            // if integrity error occurred, request update
            if (de.getErrorCode() == DataException.REFERENTIAL_INTEGRITY_ERROR)
            {
                updateRequired = true;
            }
            else
            {
                logger.error( de.toString());
                throw de;
            }
        }
        catch (Exception e)
        {
            logger.error( e.toString());
            throw new DataException(DataException.UNKNOWN, "Layaway insert", e);
        }

        // update layaway, if necessary
        if (updateRequired)
        {
            updateLayaway(dataConnection, layaway);
        }

    }

    /**
        Add update columns. <P>
        @param LayawayIfc layaway object
        @param sql SQLUpdateStatement
     */
    public void addUpdateColumns(LayawayIfc layaway, SQLUpdatableStatementIfc sql)
    {
        TransactionIDIfc transactionID = layaway.getInitialTransactionID();

        sql.addColumn(FIELD_RETAIL_STORE_ID,
                      makeSafeString(layaway.getStoreID()));
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_WORKSTATION_ID,
                      makeSafeString(transactionID.getWorkstationID()));
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_BUSINESS_DATE,
                      dateToSQLDateString(layaway.getInitialTransactionBusinessDate()));
        sql.addColumn(FIELD_LAYAWAY_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER,
                      Long.toString(transactionID.getSequenceNumber()));
        sql.addColumn(FIELD_LAYAWAY_ID,
                      makeSafeString(layaway.getLayawayID()));
        if (layaway.getCustomer() != null)
        {
            sql.addColumn(FIELD_CUSTOMER_ID,
                          makeSafeString(layaway.getCustomer().getCustomerID()));
        }
        sql.addColumn(FIELD_LAYAWAY_EXPIRATION_DATE,
                      dateToSQLDateFunction(layaway.getExpirationDate()));
        sql.addColumn(FIELD_LAYAWAY_GRACE_PERIOD,
                      dateToSQLDateFunction(layaway.getGracePeriodDate()));
        sql.addColumn(FIELD_LAYAWAY_MINIMUM_DOWN_PAYMENT,
                      layaway.getMinimumDownPayment().toString());
        sql.addColumn(FIELD_LAYAWAY_STORAGE_LOCATION,
                      makeSafeString(layaway.getLocationCode().getCode()));
        sql.addColumn(FIELD_LAYAWAY_TOTAL_AMOUNT,
                      layaway.getTotal().toString());
        sql.addColumn(FIELD_LAYAWAY_CREATION_FEE,
                      layaway.getCreationFee().toString());
        sql.addColumn(FIELD_LAYAWAY_DELETION_FEE,
                      layaway.getDeletionFee().toString());
        sql.addColumn(FIELD_TRAINING_MODE, getTrainingFlag(layaway));
        addUpdateLayawayForPaymentColumns(layaway, sql);



    }
    /**
        Add update columns. <P>
        @param LayawayIfc layaway object
        @param sql SQLUpdateStatement
     */
    public void addUpdateLayawayForPaymentColumns(LayawayIfc layaway,
                                 SQLUpdatableStatementIfc sql)
    {
        TransactionIDIfc transactionID =
          layaway.getInitialTransactionID();

        sql.addColumn(FIELD_LAYAWAY_STATUS,
                      Integer.toString(layaway.getStatus()));
        sql.addColumn(FIELD_LAYAWAY_PREVIOUS_STATUS,
                      Integer.toString(layaway.getPreviousStatus()));
        sql.addColumn(FIELD_LAYAWAY_TIMESTAMP_LAST_STATUS_CHANGE,
                      dateToSQLTimestampFunction(layaway.getLastStatusChange()));
        sql.addColumn(FIELD_LAYAWAY_BALANCE_DUE,
                      layaway.getBalanceDue().toString());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
    }

    /**
        Add insert columns. <P>
        @param LayawayIfc layaway object
        @param sql SQLInsertStatement
     */
    public void addInsertColumns(LayawayIfc layaway,
                                 SQLUpdatableStatementIfc sql)
    {
        sql.addColumn(FIELD_LAYAWAY_ID,
                      makeSafeString(layaway.getLayawayID()));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                      getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_TRAINING_MODE, getTrainingFlag(layaway));
        addLayawayLegalStrings(layaway, sql);
        addUpdateColumns(layaway, sql);
    }

    /**
        Adds update qualifier columns to SQL statement. <P>
        @param LayawayIfc layaway object
        @param sql SQLUpdateStatement
     */
    public void addUpdateQualifiers(LayawayIfc layaway,
                                    SQLUpdateStatement sql)
    {
        sql.addQualifier(FIELD_LAYAWAY_ID,
                         makeSafeString(layaway.getLayawayID()));
    }

    /**
        Inserts the total payment amount to SQL statement. <P>
        @param LayawayIfc layaway object
        @param sql SQLUpdateStatement
     */
    public void insertPaymentCollected(LayawayIfc layaway,
                                    SQLInsertStatement sql)
    {
        sql.addColumn(FIELD_LAYAWAY_TOTAL_PAYMENTS_COLLECTED,
                        layaway.getTotalAmountPaid().toString());
        sql.addColumn(FIELD_LAYAWAY_COUNT_PAYMENTS_COLLECTED,
                      layaway.getPaymentCount());
    }

    /**
        Updates the new total payment amount to SQL statement. <P>
        @param LayawayIfc layaway object
        @param sql SQLUpdateStatement
     */
    public void addUpdatedPaymentsCollected(LayawayIfc layaway,
                                    SQLUpdateStatement sql)
    {
        sql.addColumn(FIELD_LAYAWAY_TOTAL_PAYMENTS_COLLECTED,
                      FIELD_LAYAWAY_TOTAL_PAYMENTS_COLLECTED + " + " +
                        safeSQLCast(layaway.getTotalAmountPaid().toString()));
        sql.addColumn(FIELD_LAYAWAY_COUNT_PAYMENTS_COLLECTED,
                      FIELD_LAYAWAY_COUNT_PAYMENTS_COLLECTED + " + " +
                        safeSQLCast(getCountString(layaway)));
    }

    /**
        Updates the layaway legal statement to four SQL statements. Depending
        upon the length of the layaway legal statement, empty strings may be
        saved to the database. Current database length for the layaway legal
        statement is 900. <P>
        @param LayawayIfc layaway object
        @param sql SQLUpdateStatement
    **/
    
    public void addLayawayLegalStrings(LayawayIfc layaway,SQLUpdatableStatementIfc sql)
    {
        int length = layaway.getLegalStatement().length();
        StringBuffer lgl = new StringBuffer(layaway.getLegalStatement());
        pad(lgl,LEGAL_STATEMENT_LENGTH - length);
        String legal = new String(lgl.toString());

        if (length > 254)
        {
            sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT,
                          makeSafeString(legal.substring(0,255)));
        }
        else
        {
            sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT,
                          makeSafeString(legal.substring(0,255).trim()));
        }
        if (length > 509)
        {
            sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT1,
                          makeSafeString(legal.substring(255,510)));
        }
        else
        {
            sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT1,
                          makeSafeString(legal.substring(255,510).trim()));
        }
        if (length > 764)
        {
            sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT2,
                          makeSafeString(legal.substring(510,765)));
        }
        else
        {
            sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT2,
                          makeSafeString(legal.substring(510,765).trim()));
        }
        sql.addColumn(FIELD_LAYAWAY_LEGAL_STATEMENT3,
                      makeSafeString(legal.substring(765,LEGAL_STATEMENT_LENGTH).trim()));
    }

    /**
       This method is used to pad the string buffer with a given number of
       spaces

       @param buffer StringBuffer
       @param spaces int
     */
    protected void pad(StringBuffer buffer, int spaces)
    {
        for (int i = 0; i < spaces; i++)
        {
            buffer.append(" ");
        }
    }

    /**
       Returns the layaway training flag
       <P>
       @param  layaway    current layaway to insert
       @return  The layaway training flag
     */
    public String getTrainingFlag(LayawayIfc layaway)
    {
        String rc = "'0'";

        if (layaway.isTrainingMode())
        {
            rc = "'1'";
        }

        return(rc);
    }

    /**
       Returns the layaway payment count
       <P>
       @param  layaway    current layaway update
       @return  The layaway payment count
     */
    public String getCountString(LayawayIfc layaway)
    {
        Integer count = Integer.valueOf(layaway.getPaymentCount());
        String rc = count.toString();
        return(rc);
    }

    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(revisionNumber);
    }

    /**
       Returns the string representation of this object.
       @return String representation of object
     */
    @Override
    public String toString()
    {
        return(Util.classToStringHeader("JdbcSaveLayaway",
                                        getRevisionNumber(),
                                        hashCode()).toString());
    }
}

