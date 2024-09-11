/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveVoidRedeemTransaction.java /rgbustores_13.4x_generic_branch/2 2011/07/18 16:21:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/19/2008 2:17:17 PM   Michael P. Barnett In
 *          updateRedeemTransaction(), update the encrypted and hashed gift
 *         card account number columns.
 *    5    360Commerce 1.4         5/29/2007 9:13:47 AM   Ashok.Mondal
 *         Insert currencyID to redeem transaction table.
 *    4    360Commerce 1.3         4/26/2007 1:03:32 PM   Ashok.Mondal    CR
 *         16572 :V7.2.2 merge to trunk.
 *    3    360Commerce 1.2         3/31/2005 4:28:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:04 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/06/04 18:57:49  crain
 *   @scr 5388 Voiding a gift card redeem does not increase the "out" in tender summary in register reports
 *
 *   Revision 1.5  2004/05/14 21:32:56  blj
 *   @scr 4476 - fix  post void for store credit issue/redeem/tender
 *
 *   Revision 1.4  2004/05/04 03:36:58  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.3  2004/04/26 22:17:25  crain
 *   @scr 4553 Redeem Gift Certificate
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

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 *
 */
public class JdbcSaveVoidRedeemTransaction extends JdbcSaveRedeemTransaction
{
    private static final long serialVersionUID = -7921653988932284061L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveVoidRedeemTransaction.class);

    /**
     * Voided string
     */
    private static final String VOIDED = "VOIDED";

    /**
     * Class constructor.
     */
    public JdbcSaveVoidRedeemTransaction()
    {
        super();
        setName("SaveVoidRedeemTransaction");
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
        logger.info("JdbcSaveVoidRedeemTransaction.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;



        //ARTSTransaction artsTransaction = (ARTSTransaction) action.getDataObject();
        RedeemTransactionIfc transaction = (RedeemTransactionIfc)action.getDataObject();
        updateRedeemTransaction(connection, transaction);

        logger.info("JdbcSaveVoidRedeemTransaction.execute()");
    }

    /**
     * Updates into the redeem_transaction table.
     * 
     * @param dataConnection the connection to the data source
     * @param transaction The Redeem Transaction to update
     * @exception DataException
     */
    public void updateRedeemTransaction(JdbcDataConnection dataConnection,
            RedeemTransactionIfc transaction)
    throws DataException
    {
    	int currencyID;
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_REDEEM_TRANSACTION);

        TenderLineItemIfc redeemTender = getRedeemTender(transaction);

        if (redeemTender instanceof TenderStoreCreditIfc)
        {
            TenderStoreCreditIfc storeCredit = getTenderStoreCredit(transaction);
            sql.addColumn(FIELD_REDEEM_ID,
                    makeSafeString(storeCredit.getStoreCreditID()));
            sql.addColumn(FIELD_REDEEM_AMOUNT,
                    makeSafeStringFromCurrency(storeCredit.getAmount()));
            sql.addColumn(FIELD_REDEEM_FOREIGN_AMOUNT,
                    makeSafeStringFromCurrency(storeCredit.getAlternateCurrencyTendered()));
            sql.addColumn(FIELD_REDEEM_STATE, getVoidRedeemState());
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
        else if (redeemTender instanceof TenderGiftCertificateIfc)
        {
            TenderGiftCertificateIfc giftCert = (TenderGiftCertificateIfc) redeemTender;
            sql.addColumn(FIELD_REDEEM_ID, makeSafeString(giftCert.getGiftCertificateNumber()));
            sql.addColumn(FIELD_ISSUING_STORE_NUMBER, makeSafeString(giftCert.getStoreNumber()));
            sql.addColumn(FIELD_REDEEM_AMOUNT, makeSafeStringFromCurrency(giftCert.getAmountTender()));
            sql.addColumn(FIELD_REDEEM_FOREIGN_AMOUNT, makeSafeStringFromCurrency(giftCert.getAlternateCurrencyTendered()));
            sql.addColumn(FIELD_REDEEM_FACE_VALUE_AMOUNT, makeSafeStringFromCurrency(giftCert.getFaceValueAmount()));
            sql.addColumn(FIELD_REDEEM_STATE, getVoidRedeemState());
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
        else if (redeemTender instanceof TenderGiftCardIfc)
        {
            TenderGiftCardIfc tenderGiftCard = (TenderGiftCardIfc) redeemTender;
            sql.addColumn(FIELD_REDEEM_ID, makeSafeString(tenderGiftCard.getEncipheredCardData().getMaskedAcctNumber()));
            sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER, makeSafeString(tenderGiftCard.getEncipheredCardData().getEncryptedAcctNumber()));
            sql.addColumn(FIELD_REDEEM_AMOUNT, makeSafeStringFromCurrency(tenderGiftCard.getAmountTender()));
            sql.addColumn(FIELD_REDEEM_STATE, getVoidRedeemState());
            //+I18N
            sql.addColumn(FIELD_CURRENCY_ID, tenderGiftCard.getAmountTender().getType().getCurrencyId());
            //-I18N
        }


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
     * Returns the redeem state
     * 
     * @return redeem state string
     */
    public String getVoidRedeemState()
    {
        return ("'" + VOIDED + "'");
    }
}
