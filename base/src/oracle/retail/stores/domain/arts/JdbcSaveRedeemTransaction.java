/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveRedeemTransaction.java /main/21 2013/02/28 15:30:04 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     02/28/13 - Account info for gift card redeem was missing from
 *                         DB.
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    asinton   02/13/13 - change column name from LU_AJD_ACTV_GF to
 *                         LU_AJD_RDM_GF.
 *    rgour     01/30/13 - gift card redeem transaction's approval code is
 *                         stored in database
 *    cgreene   05/21/12 - XbranchMerge cgreene_bug-13951397 from
 *                         rgbustores_13.5x_generic
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   07/18/11 - remove hashed number column from gift card tables
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/05/10 - remove deprecated log amanger and technician
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    abondala  11/06/08 - updated files related to reason codes
 *    abondala  11/05/08 - updated files related to the reason codes
 *                         CheckIDTypes and MailBankCheckIDTypes
 *
 * ===========================================================================
 * $Log:
 *    9    360Commerce 1.8         4/19/2008 1:59:40 PM   Michael P. Barnett In
 *          insertRedeemTransaction(), insert the encrypted and hashed gift
 *         card account number.
 *    8    360Commerce 1.7         4/18/2008 1:45:03 AM   Manas Sahu      The
 *         updateRedeemTransaction was missing statements for GiftCard and
 *         GiftCertificate. Hence the error No Column(s) specified. Also in
 *         case of ConnectionError not invoking UpdateTransaction in
 *         saveRedeemTransaction. Code Reviewed By Naveen
 *    7    360Commerce 1.6         5/29/2007 9:13:47 AM   Ashok.Mondal
 *         Insert currencyID to redeem transaction table.
 *    6    360Commerce 1.5         4/26/2007 1:03:32 PM   Ashok.Mondal    CR
 *         16572 :V7.2.2 merge to trunk.
 *    5    360Commerce 1.4         4/25/2007 10:01:10 AM  Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         6/12/2006 3:32:11 PM   Brendan W. Farrell
 *         Fix for UDM.
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse
 *
 *   Revision 1.13  2004/05/04 03:36:57  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.12  2004/04/26 22:17:25  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.11  2004/04/22 22:34:55  blj
 *   @scr 3872-more cleanup
 *
 *   Revision 1.10  2004/04/16 14:58:26  blj
 *   @scr 3872 - fixed a few flow and screen text issues.
 *
 *   Revision 1.9  2004/04/15 20:49:22  blj
 *   @scr 3871 - fixed problems with postvoid.
 *
 *   Revision 1.8  2004/04/08 20:13:09  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts into the transaction and redeem transaction
 * tables.
 * 
 * @version $Revision: /main/21 $
 */
public class JdbcSaveRedeemTransaction extends JdbcSaveTransaction
{
    private static final long serialVersionUID = -1877140009009648034L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveRedeemTransaction.class);

    /**
     * Redeemed string
     */
    private static final String REDEEMED = "Redeemed";

    /**
     * Class constructor.
     */
    public JdbcSaveRedeemTransaction()
    {
        super();
        setName("SaveRedeemTransaction");
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
        logger.info("JdbcSaveRedeemTransaction.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        ARTSTransaction artsTransaction = (ARTSTransaction) action.getDataObject();
        saveRedeemTransaction(connection,
                              (RedeemTransactionIfc) artsTransaction.getPosTransaction());

        logger.info("JdbcSaveRedeemTransaction.execute()");
    }

    /**
        Saves the redeem_transaction.  This method first tries to update
        the transaction.  If that fails, it will attempt to insert the
        transaction.
        <p>
        Modifies both the Transaction and RedeemTransaction tables.
        <P>
        @param  dataConnection  the connection to the data source
        @param  posTransaction RedeemTransactionIfc The Redeem Transaction to update
        @exception DataException
     */
    public void saveRedeemTransaction(JdbcDataConnection dataConnection,
                                      RedeemTransactionIfc posTransaction)
                                      throws DataException
    {
        /*
         * If the insert fails, then try to update the transaction
         */
        try
        {
            insertRedeemTransaction(dataConnection, posTransaction);
        }
        catch (DataException de)
        {
        	if (de.getErrorCode() == DataException.CONNECTION_ERROR)
        	{
        		throw de;
        	}
            updateRedeemTransaction(dataConnection, posTransaction);
        }
        catch (Exception e)
        {
            logger.error("Couldn't save redeem transaction.", e);
            throw new DataException(DataException.UNKNOWN,
                                    "Couldn't save redeem transaction.",
                                    e);
        }
    }

    /**
        Inserts into the redeem_transaction table.
        <P>
        @param  dataConnection  the connection to the data source
        @param  transaction     The Redeem Transaction to update
        @exception DataException
     */
    public void insertRedeemTransaction(JdbcDataConnection dataConnection,
                                        RedeemTransactionIfc transaction)
                                        throws DataException
    {
        /*
         * Insert the transaction in the Transaction table first.
         */
        int currencyID;
        insertTransaction(dataConnection, transaction);

        SQLInsertStatement sql = new SQLInsertStatement();

        sql.setTable(TABLE_REDEEM_TRANSACTION);

        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                      getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_REDEEM_STATE, getRedeemState());
        // TODO: add in for transaction reentry
        //sql.addColumn(FIELD_REENTRY, makeStringFromBoolean(transaction.isReentryMode()));

        TenderLineItemIfc redeemTender = getRedeemTender(transaction);

        if (redeemTender instanceof TenderStoreCreditIfc)
        {
            TenderStoreCreditIfc storeCredit = getTenderStoreCredit(transaction);
            // Set Store Credit state to Redeemed.
            storeCredit.setState(TenderStoreCreditIfc.REDEEM);
            sql.addColumn(FIELD_TENDER_TYPE_CODE,
                    makeSafeString(Integer.toString(TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT)));
            sql.addColumn(FIELD_REDEEM_ID,
                    makeSafeString(storeCredit.getStoreCreditID()));
            sql.addColumn(FIELD_REDEEM_AMOUNT,
                    makeSafeStringFromCurrency(storeCredit.getAmount()));
            sql.addColumn(FIELD_REDEEM_FOREIGN_AMOUNT,
                    makeSafeStringFromCurrency(storeCredit.getAlternateCurrencyTendered()));
            sql.addColumn(FIELD_STORE_CREDIT_STATUS,
                    makeSafeString(storeCredit.getState()));
            sql.addColumn(FIELD_CUSTOMER_FIRST_NAME,
                    makeSafeString(storeCredit.getFirstName()));
            sql.addColumn(FIELD_CUSTOMER_LAST_NAME,
                    makeSafeString(storeCredit.getLastName()));
            sql.addColumn(FIELD_CUSTOMER_ID_TYPE,
                    makeSafeString(storeCredit.getPersonalIDType().getCode()));
            //+I18N
            if(storeCredit.getAlternateCurrencyTendered() != null)
            {
            	currencyID = storeCredit.getAlternateCurrencyTendered().getType().getCurrencyId();
            }
            else
            {
            	currencyID = storeCredit.getAmountTender().getType().getCurrencyId();
            }
            sql.addColumn(FIELD_CURRENCY_ID, currencyID);
            //-I18N
        }
        else if (redeemTender instanceof TenderGiftCardIfc)
        {
            TenderGiftCardIfc giftCard = (TenderGiftCardIfc) redeemTender;
            sql.addColumn(FIELD_TENDER_TYPE_CODE,
                makeSafeString(Integer.toString(TenderLineItemIfc.TENDER_TYPE_GIFT_CARD)));
            sql.addColumn(FIELD_REDEEM_ID, makeSafeString(giftCard.getGiftCard().getEncipheredCardData().getMaskedAcctNumber()));
            sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER, makeSafeString(giftCard.getGiftCard().getEncipheredCardData().getEncryptedAcctNumber()));
            sql.addColumn(FIELD_REDEEM_AMOUNT, makeSafeStringFromCurrency(giftCard.getAmountTender()));
            sql.addColumn(FIELD_GIFT_CARD_REDEEM_ADJUDICATION_CODE, makeSafeString(giftCard.getGiftCard().getApprovalCode()));
            sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER,makeSafeString(giftCard.getGiftCard().getTraceNumber()));
            
            //+I18N
            sql.addColumn(FIELD_CURRENCY_ID, giftCard.getAmountTender().getType().getCurrencyId());
            //-I18N
        }
        else if (redeemTender instanceof TenderGiftCertificateIfc)
        {
            TenderGiftCertificateIfc giftCert = (TenderGiftCertificateIfc) redeemTender;
            sql.addColumn(FIELD_TENDER_TYPE_CODE, makeSafeString(Integer.toString(TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE)));
            sql.addColumn(FIELD_REDEEM_ID, makeSafeString(giftCert.getGiftCertificateNumber()));
            sql.addColumn(FIELD_ISSUING_STORE_NUMBER, makeSafeString(giftCert.getStoreNumber()));
            sql.addColumn(FIELD_REDEEM_AMOUNT, makeSafeStringFromCurrency(giftCert.getAmountTender()));
            sql.addColumn(FIELD_REDEEM_FOREIGN_AMOUNT, makeSafeStringFromCurrency(giftCert.getAlternateCurrencyTendered()));
            sql.addColumn(FIELD_REDEEM_FACE_VALUE_AMOUNT, makeSafeStringFromCurrency(giftCert.getFaceValueAmount()));
            //+I18N
            if(giftCert.getAlternateCurrencyTendered() != null)
            {
            	currencyID = giftCert.getAlternateCurrencyTendered().getType().getCurrencyId();
            }
            else
            {
            	currencyID = giftCert.getAmountTender().getType().getCurrencyId();
            }
            sql.addColumn(FIELD_CURRENCY_ID, currencyID);
            //-I18N
        }

        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP,
                getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP,
                getSQLCurrentTimestampFunction());

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
            throw new DataException(DataException.UNKNOWN, "insertRedeemTransaction", e);
        }
    }

    /**
     Updates into the redeem_transaction table.
     <P>
     @param  dataConnection  the connection to the data source
     @param  transaction     The Redeem Transaction to update
     @exception DataException
     */
    public void updateRedeemTransaction(JdbcDataConnection dataConnection,
            RedeemTransactionIfc transaction)
    throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_REDEEM_TRANSACTION);

        TenderLineItemIfc redeemTender = getRedeemTender(transaction);

        sql.addColumn(FIELD_REDEEM_STATE, getRedeemState());
        if (redeemTender instanceof TenderStoreCreditIfc)
        {
            TenderStoreCreditIfc storeCredit = getTenderStoreCredit(transaction);
            sql.addColumn(FIELD_REDEEM_ID,
                    makeSafeString(storeCredit.getStoreCreditID()));
            sql.addColumn(FIELD_REDEEM_AMOUNT,
                    makeSafeStringFromCurrency(storeCredit.getAmount()));
        }
        else if (redeemTender instanceof TenderGiftCardIfc)
        {
            TenderGiftCardIfc giftCard = (TenderGiftCardIfc) redeemTender;
            sql.addColumn(FIELD_REDEEM_ID, makeSafeString(giftCard.getCardNumber()));
            sql.addColumn(FIELD_REDEEM_AMOUNT, makeSafeStringFromCurrency(giftCard.getAmountTender()));
            sql.addColumn(FIELD_GIFT_CARD_ACTIVATION_ADJUDICATION_CODE, makeSafeString(giftCard.getGiftCard().getApprovalCode()));
            sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER,makeSafeString(giftCard.getTraceNumber()));
        }
        else if (redeemTender instanceof TenderGiftCertificateIfc)
        {
            TenderGiftCertificateIfc giftCert = (TenderGiftCertificateIfc) redeemTender;
            sql.addColumn(FIELD_REDEEM_ID, makeSafeString(giftCert.getGiftCertificateNumber()));
            sql.addColumn(FIELD_ISSUING_STORE_NUMBER, makeSafeString(giftCert.getStoreNumber()));
            sql.addColumn(FIELD_REDEEM_AMOUNT, makeSafeStringFromCurrency(giftCert.getAmountTender()));
            sql.addColumn(FIELD_REDEEM_FOREIGN_AMOUNT, makeSafeStringFromCurrency(giftCert.getAlternateCurrencyTendered()));
            sql.addColumn(FIELD_REDEEM_FACE_VALUE_AMOUNT, makeSafeStringFromCurrency(giftCert.getFaceValueAmount()));
        }
//        // Qualifiers
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
            logger.error( de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateRedeemTransaction", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "updateRedeemTransaction");
        }

    }

    /**
     * @param transaction
     * @return
     */
    public TenderLineItemIfc getRedeemTender(RedeemTransactionIfc transaction)
    {
        TenderLineItemIfc redeemTender = transaction.getRedeemTender();
        return redeemTender;
    }

    /**
     * @param transaction
     * @return
     */
    public TenderStoreCreditIfc getTenderStoreCredit(RedeemTransactionIfc transaction)
    {
        TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc)transaction.getRedeemTender();
        return storeCredit;
    }



          /**
         Returns the sequence number of the retail transaction line item.
         <p>
         @param  lineItem    The retail transaction line item
         @return the sequence number of the retail transaction line item.
     */
          protected String getLineItemSequenceNumber(AbstractTransactionLineItemIfc lineItem)
     {
         return(String.valueOf(lineItem.getLineNumber()));
     }

    /**
       Returns safe currency string <P>
       @param  curr  CurrencyIfc
       @return String
     */
    public String makeSafeStringFromCurrency(CurrencyIfc curr)
    {
        String value = "0";
        if (curr != null)
        {
            value = curr.getStringValue();
        }
        return value;
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
