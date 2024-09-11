/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcLookupCertificate.java /main/17 2012/04/02 10:35:15 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
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
 *  6    360Commerce 1.5         5/2/2008 4:55:23 PM    Christian Greene 31553
 *       Add join clause to only select gift cert tenders that have not been
 *       voided
 *  5    360Commerce 1.4         7/3/2007 9:49:13 AM    Ashok.Mondal    CR
 *       27478 : Save the currency ID value to gift certificate table.
 *  4    360Commerce 1.3         1/25/2006 4:11:09 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         3/31/2005 4:28:38 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:39 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse   
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
/**
    This operation searches for a certificate.
    <P>
    @version $Revision: /main/17 $

**/
public class JdbcLookupCertificate extends JdbcDataOperation implements ARTSDatabaseIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8569050777420759787L;

    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcLookupCertificate.class);

    /**
       revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
       Class constructor.
     */
    public JdbcLookupCertificate()
    {
        setName("JdbcLookupCertificate");
    }

    /**
       Executes the SQL statements against the database.
       <P>
       @param  dataTransaction     The data transaction
       @param  dataConnection      The connection to the data source
       @param  action              The information passed by the valet
       @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction,
                        DataConnectionIfc dataConnection,
                        DataActionIfc action)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupCertificate.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        TenderGiftCertificateIfc certificate = (TenderGiftCertificateIfc) action.getDataObject();
        lookupCertificate(connection, certificate);
        dataTransaction.setResult(certificate.getDocument());

        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupCertificate.execute()");
    }

    /**
       Searches for a certificate in TABLE_GIFT_CERTIFICATE_TENDER_LINE_ITEM.
       <P>
       @param  dataConnection  connection to the db
       @param  String certificate number
       @return boolean (true if found)
       @exception DataException upon error
       @deprecated in 14.0; all status information has be moved to the Gift Certificate Table.
     */
    public void lookupTenderedCertificate(JdbcDataConnection dataConnection,
                                              TenderCertificateIfc certificate)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupCertificate.lookupTenderedCertificate()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // add table
        sql.setTable(TABLE_GIFT_CERTIFICATE_TENDER_LINE_ITEM, ALIAS_TENDER_LINE_ITEM);

        // add columns
        sql.addColumn(ALIAS_TENDER_LINE_ITEM, FIELD_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_TENDER_LINE_ITEM, FIELD_WORKSTATION_ID);
        sql.addColumn(ALIAS_TENDER_LINE_ITEM, FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_TENDER_LINE_ITEM, FIELD_BUSINESS_DAY_DATE);

        // join to TR_TRN table to exclude voided tenders
        StringBuilder joinClause = new StringBuilder(250);
        joinClause.append(" INNER JOIN ").append(TABLE_TRANSACTION).append(" ON ");
        joinClause.append(TABLE_TRANSACTION).append(".").append(FIELD_RETAIL_STORE_ID)
            .append(" = ").append(ALIAS_TENDER_LINE_ITEM).append(".").append(FIELD_RETAIL_STORE_ID);
        joinClause.append(" AND ").append(TABLE_TRANSACTION).append(".").append(FIELD_WORKSTATION_ID)
            .append(" = ").append(ALIAS_TENDER_LINE_ITEM).append(".").append(FIELD_WORKSTATION_ID);
        joinClause.append(" AND ").append(TABLE_TRANSACTION).append(".").append(FIELD_BUSINESS_DAY_DATE)
            .append(" = ").append(ALIAS_TENDER_LINE_ITEM).append(".").append(FIELD_BUSINESS_DAY_DATE);
        joinClause.append(" AND ").append(TABLE_TRANSACTION).append(".").append(FIELD_TRANSACTION_SEQUENCE_NUMBER)
            .append(" = ").append(ALIAS_TENDER_LINE_ITEM).append(".").append(FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addOuterJoinQualifier(joinClause.toString());

        // add negative voided status condition
        sql.addQualifier(TABLE_TRANSACTION + "." + FIELD_TRANSACTION_STATUS_CODE + 
                " != " + TransactionConstantsIfc.STATUS_VOIDED);

        // add gift cert qualifiers
        sql.addQualifier(FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER, makeSafeString(new String(certificate.getNumber())));

        if (((TenderAlternateCurrencyIfc)certificate).getAlternateCurrencyTendered() != null)
        {
            sql.addQualifier(FIELD_GIFT_CERTIFICATE_FOREIGN_FACE_VALUE_AMOUNT,
                    ((TenderAlternateCurrencyIfc)certificate).getAlternateCurrencyTendered().toString());
        }
        else
        {
            sql.addQualifier(FIELD_GIFT_CERTIFICATE_FACE_VALUE_AMOUNT, certificate.getAmountTender().toString());
        }

        sql.addQualifier(FIELD_ISSUING_STORE_NUMBER, makeSafeString(certificate.getStoreNumber()));

        executeQuery(dataConnection, certificate, sql);

        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupCertificate.lookupTenderedCertificate()");
    }

    /**
     * @param dataConnection
     * @param certificate
     * @param sql
     * @throws DataException
       @deprecated in 14.0; all status information has be moved to the Gift Certificate Table.
     */
    protected void executeQuery(JdbcDataConnection dataConnection, TenderCertificateIfc certificate, SQLSelectStatement sql) throws DataException
    {
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                throw new DataException(DataException.NO_DATA, "lookupTenderedCertificate");
            }
            else
            {
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
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "lookupTenderedCertificate");
            throw new DataException(DataException.SQL_ERROR, "lookupTenderedCertificate", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "lookupTenderedCertificate",
                                    e);
        }
    }

    /**
    Searches for a certificate in TABLE_GIFT_CERTIFICATE_TENDER_LINE_ITEM.
    <P>
    @param  dataConnection  connection to the db
    @param  String certificate number
    @return boolean (true if found)
    @exception DataException upon error
    @deprecated in 14.0 
   */
   public void lookupCertificate(JdbcDataConnection dataConnection,
                                        TenderCertificateIfc certificate, boolean lookupByStore)
         throws DataException
   {
       lookupCertificate(dataConnection, certificate);
   }
   
   /**
       Searches for a certificate in TABLE_GIFT_CERTIFICATE_TENDER_LINE_ITEM.
       <P>
       @param  dataConnection  connection to the db
       @param  String certificate number
       @return boolean (true if found)
       @exception DataException upon error
    */
    public void lookupCertificate(JdbcDataConnection dataConnection, TenderCertificateIfc certificate)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupCertificate.lookupCertificate()");

        TenderGiftCertificateIfc giftCertificate = (TenderGiftCertificateIfc)certificate;
        
        SQLSelectStatement sql = new SQLSelectStatement();
        
        // Table
        sql.setTable(TABLE_GIFT_CERTIFICATE);

        getColumns(sql);
        
        getQualifiers(sql, giftCertificate);


        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            // If we find a record, put sequence number and workstation id values
            // in the certificate object
            if (rs.next())
            {
                int index = 0;
                String documentID = getSafeString(rs, ++index);
                long seqNumber    = rs.getInt(++index);
                String wrkStID    = getSafeString(rs, ++index);
                EYSDate bussDay   = getEYSDateFromString(rs, ++index);
                String storeID    = getSafeString(rs, ++index);
                int lnSeqNumber   = rs.getInt(++index);
                String sAmount    = getSafeString(rs, ++index);
                String isoCode    = getSafeString(rs, ++index);
                boolean isTrainingMode = getBooleanFromString(rs, ++index);
                String status     = getSafeString(rs, ++index);
                String prevStatus = getSafeString(rs, ++index);
                EYSDate issueDate = timestampToEYSDate(rs, ++index);
                EYSDate redeemDate= timestampToEYSDate(rs, ++index);
                EYSDate voidDate  = timestampToEYSDate(rs, ++index);

                CurrencyIfc amount = DomainGateway.getCurrencyInstance(isoCode, sAmount);
                GiftCertificateDocumentIfc document = DomainGateway.getFactory().
                    getGiftCertificateDocumentInstance();
                document.setDocumentID(documentID);
                document.setIssuingStoreID(storeID);
                document.setIssuingWorkstationID(wrkStID);
                document.setIssuingTransactionSeqNumber(seqNumber);
                document.setIssuingBusinessDate(bussDay);
                document.setIssuingLineItemNumber(lnSeqNumber);
                document.setAmount(amount);
                document.setStatus(status);
                document.setPreviousStatus(prevStatus);
                document.setTrainingMode(isTrainingMode);
                document.setIssueDate(issueDate);
                document.setRedeemDate(redeemDate);
                document.setVoidDate(voidDate);
                
                giftCertificate.setDocument(document);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "lookupCertificate");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "lookupCertificate",
                                    e);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupCertificate.lookupCertificate()");
    }

    /**
     * Add columns to the sql object
     * @param sql
     */
    protected void getColumns(SQLSelectStatement sql)
    {
        // add column
        sql.addColumn(FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER);
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_FACE_VALUE_AMOUNT);
        sql.addColumn(FIELD_CURRENCY_ISO_CODE);
        sql.addColumn(FIELD_TRAINING_MODE);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_STATUS);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_PREVIOUS_STATUS);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_ISSUE_DATE);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_REDEEM_DATE);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_VOID_DATE);
    }

    /**
     * Add the qualifiers to the sql object 
     * @param sql
     * @param giftCertificate
     */
    protected void getQualifiers(SQLSelectStatement sql, TenderGiftCertificateIfc giftCertificate)
    {
        // Qualifiers
        sql.addQualifier(FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER, makeSafeString(new String(giftCertificate.getNumber())));
        sql.addQualifier(FIELD_TRAINING_MODE, makeStringFromBoolean(giftCertificate.isTrainingMode()));
    }

}

