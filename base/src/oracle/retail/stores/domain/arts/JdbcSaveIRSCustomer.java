/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveIRSCustomer.java /rgbustores_13.4x_generic_branch/3 2011/09/02 13:05:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/01/11 - Encryption CR
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    abondala  11/04/08 - updated file related to customer id types reason
 *                         code.
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *
 * ===========================================================================

     $Log:
      2    360Commerce 1.1         9/29/2006 7:29:00 PM   Brendan W. Farrell
           Fix oracle unit tests.
      1    360Commerce 1.0         12/13/2005 4:47:56 PM  Barry A. Pape
     $
 */
package oracle.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;

/**
 * This operation performs inserts into IRS Customer and Address tables.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class JdbcSaveIRSCustomer extends JdbcSaveCustomer
{
    /**
     * 
     */
    private static final long serialVersionUID = -6560620504132943133L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveIRSCustomer.class);

    /**
     * Class constructor.
     */
    public JdbcSaveIRSCustomer()
    {
        super();
        setName("JdbcSaveIRSCustomer");
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
        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveIRSCustomer.execute()");

        // getUpdateCount() is about the only thing outside of
        // DataConnectionIfc that we need.

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTransaction artsTransaction = (ARTSTransaction) action.getDataObject();
        TenderableTransactionIfc tenderableTransaction = (TenderableTransactionIfc) artsTransaction.getPosTransaction();

        //insert the irs customer first as irs customer id is
        //needed in retail transaction
        if(tenderableTransaction.getIRSCustomer() != null)
        {
            insertIRSCustomer(connection, tenderableTransaction);
            dataTransaction.setResult(tenderableTransaction.getIRSCustomer());
        }

        if (logger.isDebugEnabled()) logger.debug( "JdbcSaveIRSCustomer.execute()");
    }

    /**
     * Inserts IRS Customer
     * 
     * @param dataConnection data connection
     * @param tenderableTrans tenderable transaction
     * @exception DataException data exception
     */
    public void insertIRSCustomer(JdbcDataConnection dataConnection,
                                  TenderableTransactionIfc tenderableTrans) throws DataException
    {
        IRSCustomerIfc customer = tenderableTrans.getIRSCustomer();
        ARTSCustomer artsCustomer = new ARTSCustomer(customer);
        try
        {
            int partyID = generatePartyID(dataConnection);
            artsCustomer.setPartyId(partyID);
            customer.setCustomerIDPrefix(tenderableTrans.getTransactionIdentifier().getStoreID());
            customer.setCustomerID(generateCustomerID(artsCustomer));

            insertParty(dataConnection, artsCustomer);
            insertIRSCustomer(dataConnection, artsCustomer);
            insertAddress(dataConnection, artsCustomer);

        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, e.toString());
        }
    }

    /**
     * Inserts into the IRS customer table.
     * 
     * @param dataConnection The connection to the data source
     * @param artsCustomer The customer information to save
     * @exception DataException upon error
     */
    protected void insertIRSCustomer(JdbcDataConnection dataConnection,
                                  ARTSCustomer artsCustomer)
        throws DataException
    {
        IRSCustomerIfc irsCustomer = (IRSCustomerIfc)artsCustomer.getPosCustomer();

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_IRS_CUSTOMER);

        // Fields
        sql.addColumn(FIELD_IRS_CUSTOMER_ID, getCustomerID(artsCustomer));
        sql.addColumn(FIELD_PARTY_ID, getPartyID(artsCustomer));
        sql.addColumn(FIELD_IRS_CUSTOMER_FIRST_NAME, getFirstName(artsCustomer));
        sql.addColumn(FIELD_IRS_CUSTOMER_LAST_NAME, getLastName(artsCustomer));
        sql.addColumn(FIELD_IRS_CUSTOMER_MIDDLE_INITIAL, getMiddleName(artsCustomer));
        sql.addColumn(FIELD_IRS_CUSTOMER_DATE_OF_BIRTH, dateToDateFormatString(irsCustomer.getBirthdate().dateValue()));
        sql.addColumn(FIELD_IRS_CUSTOMER_ENCRYPTED_TAXPAYER_ID_NUMBER, makeSafeString(irsCustomer.getEncipheredTaxID().getEncryptedNumber()));
        sql.addColumn(FIELD_IRS_CUSTOMER_MASKED_TAXPAYER_ID_NUMBER, makeSafeString(irsCustomer.getEncipheredTaxID().getMaskedNumber()));
        sql.addColumn(FIELD_IRS_CUSTOMER_OCCUPATION, makeSafeString(irsCustomer.getOccupation()));
        sql.addColumn(FIELD_IRS_CUSTOMER_VERIFYING_ID_TYPE, makeSafeString(irsCustomer.getLocalizedPersonalIDCode().getCode()));
        sql.addColumn(FIELD_IRS_CUSTOMER_ENCRYPTED_VERIFYING_ID_NUMBER, makeSafeString(irsCustomer.getVerifyingID().getEncryptedNumber()));
        sql.addColumn(FIELD_IRS_CUSTOMER_MASKED_VERIFYING_ID_NUMBER, makeSafeString(irsCustomer.getVerifyingID().getMaskedNumber()));
        sql.addColumn(FIELD_IRS_CUSTOMER_VERIFYING_ID_ISSUING_STATE, makeSafeString(irsCustomer.getVerifyingIdIssuingState()));
        sql.addColumn(FIELD_IRS_CUSTOMER_VERIFYING_ID_ISSUING_COUNTRY, makeSafeString(irsCustomer.getVerifyingIdIssuingCountry()));
        sql.addColumn(FIELD_IRS_CUSTOMER_DATE_CASH_RECEIVED, dateToDateFormatString(irsCustomer.getDateCashReceived().dateValue()));

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
            throw new DataException(DataException.UNKNOWN, "insertIRSCustomer", e);
        }
    }
}
