/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcLookupRedeemedTender.java /main/15 2012/04/02 10:35:21 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    miparek   02/13/09 - fixed d#1532 related to invalid store credit
 *                         displayed on tendering with redeemed store credit
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:38 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse
 *
 *   Revision 1.6.2.1  2004/11/17 18:01:36  lzhao
 *   @scr 7738: match the querry with the insert.
 *
 *   Revision 1.6  2004/05/04 03:36:57  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.5  2004/04/26 22:17:25  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.4  2004/04/21 22:08:46  epd
 *   @scr 4513 Fixing database code for certificate validation
 *
 *   Revision 1.3  2004/04/21 15:01:22  blj
 *   @scr 3872 - fixed a problem with redeem certificate validation.
 *
 *   Revision 1.2  2004/04/16 14:58:26  blj
 *   @scr 3872 - fixed a few flow and screen text issues.
 *
 *   Revision 1.1  2004/04/15 20:49:22  blj
 *   @scr 3871 - fixed problems with postvoid.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 *
 */
public class JdbcLookupRedeemedTender extends JdbcDataOperation implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 7536141810668022962L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcLookupCertificate.class);

    /**
     * Redeemed string
     */
    private static final String REDEEMED = "Redeemed";

    /**
     * Class constructor.
     */
    public JdbcLookupRedeemedTender()
    {
        setName("JdbcLookupRedeemedTender");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupRedeemedTender.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        TenderCertificateIfc redeemTender = (TenderCertificateIfc) action.getDataObject();

        boolean tendered = true;

        // Check redeem table for redeemTender.
        try
        {
            lookupRedeemTender(connection, redeemTender);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                tendered = false;
            }
            else
            {
                throw de;
            }
        }

        redeemTender.setTendered(tendered);
        dataTransaction.setResult(redeemTender);

        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupRedeemedTender.execute()");
    }

    /**
     Selects from the redeem transaction table.
     <P>
     @param  dataConnection  a connection to the database
     @param  certificate TenderCertificateIfc
     @exception  DataException thrown when an error occurs executing the
     SQL against the DataConnection, or when
     processing the ResultSet
     */
    protected void lookupRedeemTender(JdbcDataConnection dataConnection,
            TenderCertificateIfc certificate)
    throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug("Begin JdbcReadTransaction.selectRedeemTransaction()");

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_REDEEM_TRANSACTION);

        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_REDEEM_STATE);

    /**
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_REDEEM_ID);
        sql.addColumn(FIELD_REDEEM_AMOUNT);
        */

    /**
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " +
                getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " +
                getWorkstationID(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " +
                getBusinessDayString(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " +
                getTransactionSequenceNumber(transaction)); **/
        sql.addQualifier(FIELD_REDEEM_ID + " = " +
                makeSafeString(new String(certificate.getNumber())));
        sql.addQualifier(FIELD_REDEEM_STATE + " = " + getRedeemState());

        sql.addNotQualifier(FIELD_REDEEM_FACE_VALUE_AMOUNT + " != " +
                certificate.getAmountTender());

        if (((TenderAlternateCurrencyIfc)certificate).getAlternateCurrencyTendered() != null)
        {
            sql.addNotQualifier(FIELD_REDEEM_FOREIGN_AMOUNT + " != " +
                    ((TenderAlternateCurrencyIfc)certificate).getAlternateCurrencyTendered());
        }
        else
        {
            sql.addNotQualifier(FIELD_REDEEM_AMOUNT + " != " +
                    certificate.getAmountTender());
        }

        executeQuery(dataConnection, certificate, sql);
    }
    /**
     * @param dataConnection
     * @param certificate
     * @param sql
     * @throws DataException
     */
    protected void executeQuery(JdbcDataConnection dataConnection, TenderCertificateIfc certificate, SQLSelectStatement sql) throws DataException
    {
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                throw new DataException(DataException.NO_DATA, "lookupRedeemedTender");
            }

            int index = 0;
            String storeID = getSafeString(rs, ++index);
            String workstationID = getSafeString(rs, ++index);
            int sequenceNumber = rs.getInt(++index);
            EYSDate redeemDate = getEYSDateFromString(rs, ++index);

            TransactionIDIfc transactionIdentifier = DomainGateway.getFactory().getTransactionIDInstance();

            // Use workstation ID, store ID and sequence number to form
            // transaction ID.  If any of these are not numeric, they will
            // be employed as strings.  Sequence number is four digits;
            // store number is five digits; workstation ID is three digits.
            transactionIdentifier.setTransactionID(storeID, workstationID, sequenceNumber);

            String transactionID = transactionIdentifier.getTransactionIDString();

            // sets the redeem transactionID and redeem date in certificate
            certificate.setRedeemTransactionID(transactionID);
            certificate.setRedeemDate(redeemDate);

            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "lookupRedeemTender");
            throw new DataException(DataException.SQL_ERROR, "lookupRedeemedTender", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "lookupRedeemedTender", e);
        }
    }

    /**
     Returns the redeem state
     <P>
     @return  redeem state string
     */
    public String getRedeemState()
    {
        return ("'" + REDEEMED + "'");
    }

}
