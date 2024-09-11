/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcLookupStoreCredit.java /main/19 2013/01/10 15:05:55 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   01/10/13 - Add business name for store credit and store credit
 *                         tender line tables.
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    sgu       02/03/11 - check in all
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
 *    7    360Commerce 1.6         4/5/2006 6:00:08 AM    Akhilashwar K. Gupta
 *         CR-3861: As per BA decision, reverted back the changes done earlier
 *          to fix the CR i.e. addition of following 4 fields in Store Credit
 *         and related code:
 *         - RetailStoreID
 *         - WorkstationID
 *         - TransactionSequenceNumber
 *         - BusinessDayDate
 *    6    360Commerce 1.5         3/15/2006 11:47:24 PM  Akhilashwar K. Gupta
 *         CR-3861: Modified lookupStoreCredit() method
 *    5    360Commerce 1.4         1/25/2006 4:11:09 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:41:15 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:38 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:39 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:55 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     11/16/2005 16:26:07    Jason L. DeLeau 4215:
 *         Get rid of redundant ArtsDatabaseifc class
 *    3    360Commerce1.2         3/31/2005 15:28:38     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:39     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:11:55     Robert Pearse
 *
 *   Revision 1.15.2.1  2004/11/16 18:35:11  lzhao
 *   @scr 7678: check rows for redeem action in store credit tender line table
 *
 *   Revision 1.15  2004/07/28 01:05:27  blj
 *   @scr 6495 updated status so that they all match.
 *
 *   Revision 1.14  2004/06/19 18:51:47  blj
 *   @scr 5694 - added a check for tender amount to storecredit lookup
 *
 *   Revision 1.13  2004/05/14 21:32:56  blj
 *   @scr 4476 - fix  post void for store credit issue/redeem/tender
 *
 *   Revision 1.12  2004/05/11 16:08:46  blj
 *   @scr 4476 - more rework for store credit tender.
 *
 *   Revision 1.11  2004/05/02 01:54:21  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.10  2004/04/21 22:08:46  epd
 *   @scr 4513 Fixing database code for certificate validation
 *
 *   Revision 1.9  2004/04/21 15:01:49  blj
 *   @scr 4476 - updated for store credit rework for foreign currency.
 *
 *   Revision 1.8  2004/04/15 20:49:22  blj
 *   @scr 3871 - fixed problems with postvoid.
 *
 *   Revision 1.7  2004/04/09 16:55:43  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.6  2004/04/02 15:36:01  blj
 *   @scr 3871 - commented out some code until later
 *
 *   Revision 1.5  2004/02/17 17:57:35  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:44  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:21  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 18 2003 19:40:02   blj
 * updated for store credit validation
 *
 *    Rev 1.0   Dec 05 2003 16:00:00   sfl
 * Initial revision.
 * Resolution for 3421: Tender redesign
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
/**
    This operation searches for a store credit that
    has been used for tendering some previous transaction.
    <P>
    @version $Revision: /main/19 $

**/
public class JdbcLookupStoreCredit extends JdbcDataOperation
                                implements ARTSDatabaseIfc
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4217427536676333920L;
    
    /**
        The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcLookupStoreCredit.class);

    /**
       Class constructor.
     */
    public JdbcLookupStoreCredit()
    {
        setName("JdbcLookupStoreCredit");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupStoreCredit.execute()");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        TenderStoreCreditIfc tenderStoreCredit = (TenderStoreCreditIfc) action.getDataObject();

        lookupStoreCredit(connection, tenderStoreCredit);

        dataTransaction.setResult(tenderStoreCredit.getStoreCredit());

        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupStoreCredit.execute()");
    }

    /**
       Searches for a store credit in TABLE_STORE_CREDIT_TENDER_LINE_ITEM.
       <P>
       @param  dataConnection  connection to the db
       @param  storeCredit store credit
       @exception DataException upon error
       @deprecated in 14.0; all status has been moved to the Store Credit table
     */
    public void lookupTenderedStoreCredit(JdbcDataConnection dataConnection,
                                           TenderStoreCreditIfc storeCredit)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupStoreCredit.lookupTenderedStoreCredit()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // Table
        sql.setTable(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

        // add columns
        sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);
        if (((TenderAlternateCurrencyIfc)storeCredit).getAlternateCurrencyTendered() != null)
        {
            sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT);
        }
        else
        {
            sql.addColumn(TABLE_STORE_CREDIT_TENDER_LINE_ITEM + "." + FIELD_STORE_CREDIT_BALANCE);
        }

        // add Qualifiers
        sql.addQualifier(FIELD_STORE_CREDIT_ID
                         + " = " + makeSafeString(new String(storeCredit.getNumber())));

        if (((TenderAlternateCurrencyIfc)storeCredit).getAlternateCurrencyTendered() != null)
        {
            sql.addQualifier(addAbsFunction(FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT)
                    + " = " + addAbsFunction(((TenderAlternateCurrencyIfc)storeCredit).getAlternateCurrencyTendered().toString()));
        }
        else
        {
            sql.addQualifier(addAbsFunction(FIELD_STORE_CREDIT_BALANCE)
                    + " = " + addAbsFunction(storeCredit.getAmountTender().toString()) );
        }

        sql.addQualifier(FIELD_STORE_CREDIT_TENDER_STATE
                        + " = " + makeSafeString(TenderCertificateIfc.REDEEMED));

        //add Join Qualifiers
        sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM,
                             FIELD_RETAIL_STORE_ID,
                             ALIAS_TRANSACTION,
                             FIELD_RETAIL_STORE_ID);

        sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM,
                             FIELD_WORKSTATION_ID,
                             ALIAS_TRANSACTION,
                             FIELD_WORKSTATION_ID);

        sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM,
                             FIELD_TRANSACTION_SEQUENCE_NUMBER,
                             ALIAS_TRANSACTION,
                             FIELD_TRANSACTION_SEQUENCE_NUMBER);

        sql.addJoinQualifier(TABLE_STORE_CREDIT_TENDER_LINE_ITEM,
                             FIELD_BUSINESS_DAY_DATE,
                             ALIAS_TRANSACTION,
                             FIELD_BUSINESS_DAY_DATE);

        // add Order by desc
        sql.addOrdering(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_END_DATE_TIMESTAMP
                        + " desc");

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            int     index = 0;
            String  storeID = null;
            String  workstationID = null;
            int     sequenceNumber = 0;
            EYSDate redeemDate = null;

            //when doing void, negative amount will be added as the balance into the table.
            //add all the balances together, if it is zero, the store credit is not redeemed.
            BigDecimal totalBalance = new BigDecimal(0.0);
            while ( rs.next() )
            {
                // get amount first to figure out the transaction is post void or not
                BigDecimal amount = getBigDecimal(rs, 5);
                // Get the last non post void transcation.
                // The result is sorted desc by transaction ending date
                if ( amount.signum()>=0 && redeemDate==null )
                {
                    index = 0;
                    storeID = getSafeString(rs, ++index);
                    workstationID = getSafeString(rs, ++index);
                    sequenceNumber = rs.getInt(++index);
                    redeemDate = getEYSDateFromString(rs, ++index);
                }

                //add total balance in order to determine the card can be redeem or not
                totalBalance = totalBalance.add(amount);

            }
            rs.close();

            if ( totalBalance.intValue()==0 )
            {
                throw new DataException(DataException.NO_DATA, "lookupTenderedStoreCredit");
            }
            else
            {
                TransactionIDIfc transactionIdentifier = DomainGateway.getFactory().getTransactionIDInstance();

                // Use workstation ID, store ID and sequence number to form
                // transaction ID.  If any of these are not numeric, they will
                // be employed as strings.  Sequence number is four digits;
                // store number is five digits; workstation ID is three digits.
                transactionIdentifier.setTransactionID(storeID, workstationID, sequenceNumber);

                String transactionID = transactionIdentifier.getTransactionIDString();

                // sets the redeem transactionID and redeem date in certificate
                storeCredit.setRedeemTransactionID(transactionID);
                storeCredit.setRedeemDate(redeemDate);
            }

        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "lookupTenderedCStoreCredit");
            throw new DataException(DataException.SQL_ERROR, "lookupTenderedStoreCredit", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "lookupTenderedStoreCredit",
                                    e);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupStoreCredit.lookupTenderedStoreCredit()");
    }

    /**
       Searches for a store credit in TABLE_STORE_CREDIT.
       <P>
       @param  dataConnection  connection to the db
       @param  String store credit id
       @exception DataException upon error
     */
    public void lookupStoreCredit(JdbcDataConnection dataConnection,
                                       TenderStoreCreditIfc tenderStoreCredit)
        throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupStoreCredit.lookupStoreCredit()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_STORE_CREDIT);

        // Add columns
        sql.addColumn(FIELD_STORE_CREDIT_ID);
        sql.addColumn(FIELD_STORE_CREDIT_BALANCE);
        sql.addColumn(FIELD_STORE_CREDIT_EXPIRATION_DATE);
        sql.addColumn(FIELD_STORE_CREDIT_STATUS);
        sql.addColumn(FIELD_STORE_CREDIT_PREVIOUS_STATUS);
        sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_BUSINESS_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_TRAINING_FLAG);
        sql.addColumn(FIELD_CURRENCY_ISO_CODE);
        sql.addColumn(FIELD_STORE_CREDIT_ISSUE_DATE);
        sql.addColumn(FIELD_STORE_CREDIT_REDEEM_DATE);
        sql.addColumn(FIELD_STORE_CREDIT_VOID_DATE);

        // Qualifiers
        sql.addQualifier(FIELD_STORE_CREDIT_ID
                         + " = " + makeSafeString(new String(tenderStoreCredit.getNumber())));

        sql.addQualifier(FIELD_STORE_CREDIT_TRAINING_FLAG
                + " = " + makeStringFromBoolean(tenderStoreCredit.isTrainingMode()));
        
        try
        {
            StoreCreditIfc storeCredit = null;
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();


            while ( rs.next() )
            {
                int index = 0;
                String documentID  = getSafeString(rs, ++index);
                String sAmount     = getSafeString(rs, ++index);
                EYSDate expDate    = dateToEYSDate(rs, ++index);
                String status      = getSafeString(rs, ++index);
                String prevStatus  = getSafeString(rs, ++index);
                String fName       = getSafeString(rs, ++index);
                String lName       = getSafeString(rs, ++index);
                String businessName = getSafeString(rs, ++index);
                boolean isTrainingMode = getBooleanFromString(rs, ++index);
                String isoCode     = getSafeString(rs, ++index);
                EYSDate issueDate  = timestampToEYSDate(rs, ++index);
                EYSDate redeemDate = timestampToEYSDate(rs, ++index);
                EYSDate voidDate   = timestampToEYSDate(rs, ++index);
                
                CurrencyIfc amount = DomainGateway.getCurrencyInstance(isoCode, sAmount);
                storeCredit = DomainGateway.getFactory().getStoreCreditInstance();
                storeCredit.setDocumentID(documentID);
                storeCredit.setAmount(amount);
                storeCredit.setExpirationDate(expDate);
                storeCredit.setStatus(status);
                storeCredit.setPreviousStatus(prevStatus);
                storeCredit.setLastName(lName);
                storeCredit.setFirstName(fName);
                storeCredit.setBusinessName(businessName);
                storeCredit.setTrainingMode(isTrainingMode);
                storeCredit.setIssueDate(issueDate);
                storeCredit.setRedeemDate(redeemDate);
                storeCredit.setVoidDate(voidDate);
            }
            rs.close();
            
            if (storeCredit == null)
            {
                throw new DataException(DataException.NO_DATA, "No Store credit available for " + tenderStoreCredit.getNumber());
            }
            
            tenderStoreCredit.setStoreCredit(storeCredit);
        }
        catch (DataException de)
        {
            logger.warn(de);
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN,
                                    "lookupStoreCredit",
                                    e);
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcLookupStoreCredit.lookupStoreCredit()");
    }

    /**
        Adds a abs function to the column name or value.
        <p>
        @param  str   the column name
        @return abs function with the string
     */
    protected String addAbsFunction(String str)
    {
        return "abs(" + str + ")";
    }
}
