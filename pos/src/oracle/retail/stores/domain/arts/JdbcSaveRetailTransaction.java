/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveRetailTransaction.java /main/67 2014/03/27 15:39:34 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/11/14 - add support for returning ASA ordered items
 *    cgreene   01/28/14 - Add support for saving type of external order
 *    cgreene   01/24/14 - add column to indicate transaction is created from
 *                         an ATG order
 *    abhinavs  11/07/13 - Fix to handle payment txn while saving crncy and
 *                         ctry code in TR_RTL table
 *    abondala  09/04/13 - initialize collections
 *    rhaight   08/23/13 - Field Support Updates
 *    ckancher  08/16/13 - Fix for bill pay transaction
 *    rabhawsa  06/26/13 - ruleID fix for threshold discount rule
 *    rgour     04/01/13 - CBR cleanup
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    sgu       07/17/12 - add item serach criteria
 *    sgu       06/26/12 - change order delivery id column name
 *    sgu       06/15/12 - add payment support to xc order creation
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    cgreene   04/03/12 - removed deprecated methods
 *    sgu       05/14/12 - check for null customer
 *    sgu       05/14/12 - check order customer nullpointer
 *    sgu       05/07/12 - rename crossChannel to xChannel
 *    sgu       05/07/12 - rename crossChannel to XChannel
 *    sgu       04/26/12 - check in merge changes
 *    sgu       04/26/12 - save cross channel order delivery info at
 *                         transaction level
 *    sgu       04/24/12 - read/save order delivery details
 *    icole     04/20/12 - Forward port fix for apostrophe in first or last
 *                         name not being saved.
 *    yiqzhao   04/16/12 - refactor store send in transaction totals
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    mkutiana  04/13/12 - XbranchMerge mukothan_bug-13474020 from
 *                         rgbustores_13.1x_branch
 *    sgu       04/09/12 - check in after merge
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    sgu       03/29/12 - move order recipient record be transactional data.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    yiqzhao   03/07/12 - add OrderShippingDetail domain object and modify the
 *                         related code
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/19/11 - encryption CR
 *    cgreene   01/10/11 - refactor blob helpers into one
 *    abondala  06/07/10 - rename externalOrderDesc to externalOrderNumber
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       05/25/10 - add jdbc read or save for external order info
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    nkgautam  02/15/10 - added gift receipt transaction flag update
 *    abondala  01/03/10 - update header date
 *    npoola    04/21/09 - personalID to safe String

 *    sbeesnal  04/21/09 - Modified the updateOrderDeliveryDetail method to
 *                         remove business day from SQL where condition. This
 *                         was not updating the orders created with business
 *                         day other than current business day.
 *    mchellap  04/21/09 - fixed db2 issue
 *    mahising  04/01/09 - Fixed order status and business date issue for PDO
 *                         transaction
 *    cgreene   03/30/09 - print special instructions line as two lines and
 *                         spell out whole label. hide label when there are not
 *                         special instructions
 *    npoola    03/27/09 - fixed the Signature Capture information insert
 *    mahising  02/27/09 - clean up code after code review by jack for PDO
 *    mahising  02/26/09 - Rework for PDO functionality
 *    cgreene   02/22/09 - check for null sales associate when getting id
 *    mahising  02/20/09 - Change status Pickup to Order in service alert
 *    mahising  01/13/09 - fix QA issue
 *    aphulamb  12/10/08 - returns functionality changes for greying out
 *                         buttons
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    aphulamb  11/25/08 - Checking files after code review by Amrish
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/15/08 - Pickup Delivery Order functionality
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    mchellap  11/19/08 - Added quotes for db2 compatibility
 *    rkar      11/04/08 - Added code for POS-RM integration
 *
 * ===========================================================================
 * $Log:
 *    13   360Commerce 1.12        4/25/2007 10:01:10 AM  Anda D. Cadar   I18N
 *         merge
 *    12   360Commerce 1.11        2/6/2007 11:04:18 AM   Anil Bondalapati
 *         Merge from JdbcSaveRetailTransaction.java, Revision 1.8.1.0
 *    11   360Commerce 1.10        12/11/2006 3:15:23 PM  Charles D. Baker CR
 *         21238 - Updated to remove quotes around integer value for DB2
 *         behavior. Continues to function with Oracle.
 *    10   360Commerce 1.9         12/9/2006 11:33:21 AM  Charles D. Baker CR
 *         22796 - Corrected Oracle date format problem. Age restriction
 *         column is DATE type in retail transaction table.
 *    9    360Commerce 1.8         11/9/2006 7:28:30 PM   Jack G. Swan
 *         Modifided for XML Data Replication and CTR.
 *    8    360Commerce 1.7         8/9/2006 11:27:07 AM   Robert Zurga    As
 *         per Dan Baker: I had started looking at this CR before you started
 *         looking at it. I'd suggest using the getStringValue method of the
 *         CurrencyIfc class instead of the toString method. I think
 *         getStringValue is less ambiguous than the more generic toString
 *         method that is available in all objects. For instance, if a
 *         customer overrides our currency objects, they may not implement
 *         toString to behave as getStringValue does.
 *
 *         This actually the suggestion in the CR, but whoever did the change
 *         in .v7x didn't follow that advice. We use getStringValue
 *         extensively in JdbcSaveRetailTransactionLineItem.
 *
 *    7    360Commerce 1.6         8/7/2006 6:02:16 PM    Robert Zurga    Merge
 *          18133 from v7x
 *    6    360Commerce 1.5         8/7/2006 3:27:07 PM    Robert Zurga    Merge
 *          from JdbcSaveRetailTransaction.java, Revision 1.4.1.1
 *    5    360Commerce 1.4         1/25/2006 4:11:23 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:43:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse
 *:
 *    5    .v710     1.2.2.0     9/21/2005 13:39:48     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    4    .v700     1.2.3.0     1/4/2006 16:12:11      Rohit Sachdeva
 *         4123:Customer Physically Present
 *    3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
 *
 *   Revision 1.14.2.1  2004/12/07 18:31:56  jdeleau
 *   @scr 7783 Save Layaway IDs
 *
 *   Revision 1.14  2004/08/17 19:43:28  rsachdeva
 *   @scr 6791 Fix for Phone Type and Phone Number  for Suspended/Retrieved and Update Send flow
 *   Phone number entered at Shipping address was not retrieved for Work type.
 *
 *   Revision 1.13  2004/08/10 14:23:44  rsachdeva
 *   @scr 6791 Transaction Level Send Flag
 *
 *   Revision 1.12  2004/07/19 14:39:06  kll
 *   @scr 6335: wrapping value in single quotes
 *
 *   Revision 1.11  2004/07/14 13:37:40  kll
 *   @scr 6139: catch SQL Exceptions
 *
 *   Revision 1.10  2004/06/22 17:24:33  lzhao
 *   @scr 4670: code review
 *
 *   Revision 1.9  2004/06/19 14:01:02  lzhao
 *   @scr 4670: add column for the flag for send customer: linking or capture
 *
 *   Revision 1.8  2004/06/07 23:00:30  lzhao
 *   @scr 4670: add more column for shippingRecords table.
 *
 *   Revision 1.7  2004/06/02 19:01:53  lzhao
 *   @scr 4670: add shippingRecords table.
 *
 *   Revision 1.6  2004/04/09 16:55:45  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:32:54   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Apr 01 2003 15:40:34   pjf
 * Save transaction subtotals to the database for CO reporting.
 * Resolution for 1585: Maintain ARTS SQL for Oracle
 *
 *    Rev 1.2   Mar 25 2003 16:45:24   HDyer
 * Update tr_rtl table to add state and country columns per code review.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.1   Dec 23 2002 12:37:46   HDyer
 * Added personal ID fields.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.0   Jun 03 2002 16:40:04   msg
 * Initial revision.
 *
 *    Rev 1.2   May 12 2002 23:40:06   mhr
 * db2 quote fixes.  chars/varchars must be quoted and ints/decimals must not be quoted.
 * Resolution for Domain SCR-50: db2 port fixes
 *
 *    Rev 1.1   Mar 18 2002 22:48:38   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 12:08:26   msg
 * Initial revision.
 *
 *    Rev 1.6   27 Dec 2001 15:51:12   sfl
 * Save shipping address information into
 * new table SaleReturnLineItemAddress
 * instead of saving into RetailTransaction.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.5   Dec 26 2001 17:25:18   mpm
 * Modified to properly handle order transaction retrieval.
 * Resolution for Domain SCR-14: Special Order modifications
 *
 *    Rev 1.4   21 Dec 2001 08:54:58   sfl
 * Included country data column during saving information
 * into RetailTransaction table.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.3   20 Dec 2001 14:43:50   sfl
 * Enhenced the condition check to decide if
 * the shipping information needs to be stored
 * when the transaction is suspended.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.2   19 Dec 2001 14:03:10   sfl
 * Added new columns in the save operation for RetailTransaction table to support retrieving shipping address information for
 * the reprint receipt when the one time use address
 * is used without updating the linked customer address.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.1   11 Dec 2001 18:18:40   sfl
 * Save shipping information when the transaction
 * has send items be shipped.
 * Resolution for Domain SCR-19: Domain SCR for Shipping Method use case in Send Package
 *
 *    Rev 1.0   Sep 20 2001 15:57:04   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 12:34:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;


import java.awt.Point;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderConstantsIfc;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.sql.SQLUpdatableStatementIfc;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.ImageUtils;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.ARTSTransaction;
import oracle.retail.stores.domain.arts.JdbcSaveTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.order.OrderRecipientIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.transaction.BillPayTransaction;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransaction;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperFactory;
import oracle.retail.stores.persistence.utility.DatabaseBlobHelperIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This operation performs inserts into the transaction and retail transaction
 * tables.
 * <P>
 *
 */
public class JdbcSaveRetailTransaction extends JdbcSaveTransaction
{
    
    
    private static final long serialVersionUID = -6798607456581912835L;
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveRetailTransaction.class);

    /**
     * Class constructor.
     */
    public JdbcSaveRetailTransaction()
    {
        super();
        setName("JdbcSaveRetailTransaction");
    }

    /**
     * Executes the SQL statements against the database.
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
            logger.debug("JdbcSaveRetailTransaction.execute()");

        /*
         * getUpdateCount() is about the only thing outside of DataConnectionIfc
         * that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTransaction artsTransaction = (ARTSTransaction)action.getDataObject();
        saveRetailTransaction(connection, (TenderableTransactionIfc)artsTransaction.getPosTransaction());

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveRetailTransaction.execute()");
    }

    /**
     * Saves the retail_transaction. This method first tries to update the
     * transaction. If that fails, it will attempt to insert the transaction.
     * <p>
     * Modifies both the Transaction and RetailTransaction tables.
     *
     * @param dataConnection the connection to the data source
     * @param transaction The Retail Transaction to update
     * @exception DataException
     */
    public void saveRetailTransaction(JdbcDataConnection dataConnection, TenderableTransactionIfc posTransaction)
            throws DataException
    {
        // 17312379 Logging addition for field support
        if (logger.isInfoEnabled())
        {
            String busDate = posTransaction.getBusinessDay().toFormattedString();
            String transID = posTransaction.getTransactionID();
            int tranStatus = posTransaction.getTransactionStatus();
            int tranType = posTransaction.getTransactionType();
            StringBuffer sb = new StringBuffer();
            sb.append("JdbcSaveRetailTransaction.saveRetailTransaction() - Transaction ID: ");
            sb.append(transID);
            sb.append(", Business Date: ");
            sb.append(busDate);
            sb.append(", Transaction Type: ");
            sb.append(tranType);
            sb.append(", Transaction Status: ");
            sb.append(tranStatus);
            logger.info(sb.toString());
        }
    
        // End 17312379 mods
        
        /*
         * If the insert fails, then try to update the transaction
         */
        try
        {
            insertRetailTransaction(dataConnection, posTransaction);
        }
        catch (DataException de)
        {
            // updateRetailTransaction(dataConnection, posTransaction);
            /*
             * Shouldn't be updating this type of transaction. Pass back
             * exception instead.
             */
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("Couldn't save retail transaction.");
            logger.error("" + e + "");
            throw new DataException(DataException.UNKNOWN, "Couldn't save retail transaction.", e);
        }
    }

    /**
     * Inserts into the retail_transaction table.
     *
     * @param dataConnection the connection to the data source
     * @param transaction The Retail Transaction to update
     * @exception DataException
     */
    public void insertRetailTransaction(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
            throws DataException
    {
        /*
         * Insert the transaction in the Transaction table first.
         */
        insertTransaction(dataConnection, transaction);

        // if a suspended transaction save all applicable advanced pricing rules
        if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED
                && transaction instanceof SaleReturnTransactionIfc)
        {

            ((SaleReturnTransactionIfc)transaction).clearBestDealDiscounts();
            insertAdvancedPricingRules(dataConnection, transaction);
        }

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_RETAIL_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_CUSTOMER_ID, getCustomerID(transaction));
        sql.addColumn(FIELD_SUSPENDED_TRANSACTION_REASON_CODE, getSuspendedTransactionReasonCode(transaction));

        addTransactionTotalsColumns((SQLUpdatableStatementIfc)sql, transaction);

        sql.addColumn(FIELD_ENCRYPTED_PERSONAL_ID_NUMBER, getEncryptedPersonalIDNumber(transaction));
        sql.addColumn(FIELD_MASKED_PERSONAL_ID_NUMBER, getMaskedPersonalIDNumber(transaction));
        sql.addColumn(FIELD_PERSONAL_ID_REQUIRED_TYPE, getPersonalIDType(transaction));
        sql.addColumn(FIELD_PERSONAL_ID_STATE, getPersonalIDState(transaction));
        sql.addColumn(FIELD_PERSONAL_ID_COUNTRY, getPersonalIDCountry(transaction));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_SEND_PACKAGE_COUNT, getSendPackageCount(transaction));        
        
        sql.addColumn(FIELD_TRANSACTION_CURRENCY, getCurrencyType(transaction));
        sql.addColumn(FIELD_TRANSACTION_COUNTRY, getTransactionCountryCode(transaction));
        
        if (transaction instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc srTransaction = (SaleReturnTransactionIfc)transaction;
            if (srTransaction.getAgeRestrictedDOB() != null)
            {
                sql.addColumn(FIELD_AGE_RESTRICTED_DOB, getAgeRestrictedDOB(srTransaction));
            }

            if (srTransaction.isSendCustomerLinked())
            {
                sql.addColumn(FIELD_SEND_CUSTOMER_TYPE, "'0'");
            }
            else
            {
                sql.addColumn(FIELD_SEND_CUSTOMER_TYPE, "'1'");
            }
            if (srTransaction.isCustomerPhysicallyPresent())
            {
                sql.addColumn(FIELD_SEND_CUSTOMER_PHYSICALLY_PRESENT, "'1'");
            }
            else
            {
                sql.addColumn(FIELD_SEND_CUSTOMER_PHYSICALLY_PRESENT, "'0'");
            }
            if ( (transaction instanceof SaleReturnTransaction) &&
                 ((SaleReturnTransaction)transaction).isTransactionLevelSendAssigned())
            {
                sql.addColumn(FIELD_TRANSACTION_LEVEL_SEND, "'1'");
            }
            else
            {
                sql.addColumn(FIELD_TRANSACTION_LEVEL_SEND, "'0'");
            }
            if(srTransaction.isTransactionGiftReceiptAssigned())
            {
                sql.addColumn(FIELD_TRANSACTION_LEVEL_GIFT_RECEIPT_FLAG, "'1'");
            }
            else
            {
                sql.addColumn(FIELD_TRANSACTION_LEVEL_GIFT_RECEIPT_FLAG, "'0'");
            }

            // insert external order info
            sql.addColumn(FIELD_EXTERNAL_ORDER_ID, getExternalOrderID(srTransaction));
            sql.addColumn(FIELD_EXTERNAL_ORDER_NUMBER, getExternalOrderNumber(srTransaction));
            sql.addColumn(FIELD_EXTERNAL_ORDER_TYPE, getExternalOrderType(srTransaction));
            sql.addColumn(FIELD_CONTRACT_SIGNATURE_REQUIRED_FLAG, makeStringFromBoolean(srTransaction.requireServiceContract()));
        }

        // if it's a retail transaction, add these columns
        if (transaction instanceof RetailTransactionIfc)
        {
            RetailTransactionIfc rt = (RetailTransactionIfc)transaction;
            sql.addColumn(FIELD_EMPLOYEE_ID, getSalesAssociateID(rt));
            sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(rt));
            sql.addColumn(FIELD_ORDER_ID, makeSafeString(rt.getOrderID()));
        }

        if (transaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_PARTIAL
                || transaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_COMPLETE)
        {
            CurrencyIfc appliedDeposit = getAppliedOrderDeposit(transaction);
            sql.addColumn(FIELD_DEPOSIT_AMOUNT_APPLIED, appliedDeposit.getStringValue());
        }

        if (transaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_INITIATE
                && ((OrderTransactionIfc)transaction).getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
        {
            CurrencyIfc appliedDeposit = getAppliedOrderDeposit(transaction);
            sql.addColumn(FIELD_DEPOSIT_AMOUNT_APPLIED, appliedDeposit.getStringValue());
        }
        if (transaction instanceof LayawayPaymentTransactionIfc)
        {
            LayawayPaymentTransactionIfc lt = (LayawayPaymentTransactionIfc)transaction;
            if (lt.getLayaway() != null)
            {
                sql.addColumn(FIELD_LAYAWAY_ID, makeSafeString(lt.getLayaway().getLayawayID()));
            }
        }

        if (transaction.getIRSCustomer() != null)
        {
            sql.addColumn(FIELD_IRS_CUSTOMER_ID, getIRSCustomerID(transaction));
        }

        if (transaction.getReturnTicket() != null)
        {
            sql.addColumn(FIELD_TRANSACTION_RETURN_TICKET, makeSafeString(transaction.getReturnTicket()));
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "insertRetailTransaction", e);
        }

        if ( transaction instanceof SaleReturnTransaction )
        {
            if (((SaleReturnTransaction)transaction).getItemSendPackagesCount() > 0)
            {
                saveTransactionShippings(dataConnection, transaction);
            }
        }

        if (transaction instanceof OrderTransactionIfc)
        {
            OrderTransactionIfc orderTransaction = (OrderTransactionIfc) transaction;
            if (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
            {
                // save delivery detail into database
                boolean isCrossChannelOrder = orderTransaction.containsXChannelOrderLineItem();
                Collection<OrderDeliveryDetailIfc> orderDeliveryDetailCollection = orderTransaction.getDeliveryDetails();
                if (orderDeliveryDetailCollection != null)
                {
                    for (OrderDeliveryDetailIfc orderDeliveryDetail : orderDeliveryDetailCollection)
                    {
                        insertOrderDeliveryDetailStatus(dataConnection, orderTransaction, orderDeliveryDetail);
                        if (!orderTransaction.isSuspended() && !isCrossChannelOrder &&
                                (orderTransaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_INITIATE))
                        {
                            insertOrderDeliveryDetail(dataConnection, orderTransaction, orderDeliveryDetail);
                        }
                    }
                }

                // save order recepient detail into database
                if (orderTransaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_PARTIAL ||
                    orderTransaction.getTransactionType() == TransactionConstantsIfc.TYPE_ORDER_COMPLETE)
                {
                    OrderRecipientIfc orderRecipient = orderTransaction.getOrderRecipient();
                    if (orderRecipient != null)
                    {
                        insertOrderRecipientDetail(dataConnection, orderTransaction, orderRecipient);
                    }
                }
            }
        }
    }

    /**
     * This method gets the total deposit applied to this transaction.
     *
     * @param transaction
     * @return
     */
    private CurrencyIfc getAppliedOrderDeposit(TenderableTransactionIfc transaction)
    {
        CurrencyIfc depositPaid = DomainGateway.getBaseCurrencyInstance();
        AbstractTransactionLineItemIfc[] lineItems = ((OrderTransactionIfc)transaction).getLineItems();
        depositPaid.setZero();
        for (int i = 0; i < lineItems.length; i++)
        {
            depositPaid = depositPaid
                    .add(((SaleReturnLineItemIfc)lineItems[i]).getOrderItemStatus().getDepositAmount());
        }
        return depositPaid;
    }

    /**
     * Adds TransactionTotals values from a TenderableTransactionIfc to a
     * SQLUpdateableStatementIfc.
     *
     * @param SQLUpdateableStatementIfc sql - the statement
     * @param TenderableTransactionIfc transaction - a tenderable transaction
     */
    protected void addTransactionTotalsColumns(SQLUpdatableStatementIfc sql, TenderableTransactionIfc transaction)
    {
        TransactionTotalsIfc totals = transaction.getTransactionTotals();

        sql.addColumn(FIELD_TRANSACTION_SALES_TOTAL, totals.getSubtotal().getStringValue());
        sql.addColumn(FIELD_TRANSACTION_DISCOUNT_TOTAL, totals.getDiscountTotal().getStringValue());
        sql.addColumn(FIELD_TRANSACTION_TAX_TOTAL, totals.getTaxTotal().getStringValue());
        sql.addColumn(FIELD_TRANSACTION_INC_TAX_TOTAL, totals.getInclusiveTaxTotal().getStringValue());
        sql.addColumn(FIELD_TRANSACTION_NET_TOTAL, totals.getGrandTotal().getStringValue());
        sql.addColumn(FIELD_TRANSACTION_TENDER_TOTAL, totals.getAmountTender().getStringValue());
    }

    /**
     * Inserts into the transaction price derivation rule table all the
     * applicable advanced pricing rules in order to be able to recreate them
     * during a transaction retrieve.
     *
     * @param dataConnection connection to the db
     * @param transaction a pos transaction
     * @exception DataException upon error
     */
    public void insertAdvancedPricingRules(JdbcDataConnection dataConnection, TransactionIfc transaction)
            throws DataException
    {

        List<String> ruleIDs = new ArrayList<String>();
        for (Iterator<DiscountRuleIfc> i = ((SaleReturnTransactionIfc)transaction).advancedPricingRules(); i.hasNext();)
        {
            AdvancedPricingRuleIfc rule = (AdvancedPricingRuleIfc)i.next();
            SQLInsertStatement sql = new SQLInsertStatement();

            // Table
            sql.setTable(TABLE_TRANSACTION_PRICE_DERIVATION_RULES);
            
            // for mutltithreshold rules, remove the colon and threshhold id.
            // Only one record should be inserted for a multithreshold rule.
            String ruleID = rule.getRuleID();
            ruleID = ruleID.split(Util.COLON)[0];

            if (ruleIDs.contains(ruleID))
            {
                continue;
            }
            ruleIDs.add(ruleID);
            
            // Fields
            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ID, ruleID);
            sql.addColumn(FIELD_COMPARISON_BASIS_CODE, new String("'" + rule.getSourceComparisonBasis() + "'"));
            sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID, makeSafeString(rule.getReferenceID()));
            sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID_TYPE_CODE, new String("'"
                    + DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[rule.getReferenceIDCode()] + "'"));

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
                throw new DataException(DataException.UNKNOWN, "insertAdvancedPricingRules", e);
            }
        }
    }

    /**
     * Updates the retail transaction table.
     *
     * @param dataConnection
     * @param transaction The Retail Transaction to update
     * @exception DataException
     */
    public void updateRetailTransaction(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
            throws DataException
    {
        /*
         * Update the Transaction table first.
         */
        updateTransaction(dataConnection, transaction);

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_RETAIL_TRANSACTION);

        // Fields
        sql.addColumn(FIELD_CUSTOMER_ID, getCustomerID(transaction));

        addTransactionTotalsColumns(sql, transaction);

        sql.addColumn(FIELD_ENCRYPTED_PERSONAL_ID_NUMBER, getPersonalIDNumber(transaction));
        sql.addColumn(FIELD_MASKED_PERSONAL_ID_NUMBER, getPersonalIDNumber(transaction));
        sql.addColumn(FIELD_PERSONAL_ID_REQUIRED_TYPE, getPersonalIDType(transaction));
        sql.addColumn(FIELD_SUSPENDED_TRANSACTION_REASON_CODE, getSuspendedTransactionReasonCode(transaction));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // if it's a retail transaction, add these columns
        if (transaction instanceof RetailTransactionIfc)
        {
            RetailTransactionIfc rt = (RetailTransactionIfc)transaction;
            sql.addColumn(FIELD_EMPLOYEE_ID, getSalesAssociateID(rt));
            sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(rt));
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
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "updateRetailTransaction", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update RetailTransaction");
        }
    }

    /**
     * Returns the string value to be used in the database for the gift registry
     *
     * @param SaleReturnTransaction object containing the gift registry
     * @return The gift registry value
     * @deprecated
     */
    public String getGiftRegistryString(SaleReturnTransactionIfc transaction)
    {
        return (getGiftRegistryString((RetailTransactionIfc)transaction));
    }

    /**
     * Returns the string value to be used in the database for the gift registry
     *
     * @param the transaction object containing the gift
     * @return The gift registry value
     */
    public String getGiftRegistryString(RetailTransactionIfc transaction)
    {
        String value = "null";

        // If there is not gift registry associated with this object, we
        // need to set the registry value to null in the database.
        if (transaction instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc trans = (SaleReturnTransactionIfc)transaction;
            if (trans.getDefaultRegistry() != null)
            {
                value = "'" + trans.getDefaultRegistry().getID() + "'";
            }
        }

        return (value);
    }

    /**
     * Returns the string value to be used in the database for the customer ID
     *
     * @param the transaction object containing the customer
     * @return The gift registry value
     */
    public String getCustomerID(TenderableTransactionIfc transaction)
    {
        CustomerIfc customer = transaction.getCustomer();
        String customerID = "null";
        if (customer != null)
        {
            customerID = "'" + customer.getCustomerID() + "'";
        }

        return (customerID);
    }

    /**
     * Returns the string value to be used in the database for the irs customer
     * ID
     *
     * @param transaction object containing the irs customer
     * @return String irs customer id
     */
    public String getIRSCustomerID(TenderableTransactionIfc transaction)
    {
        IRSCustomerIfc irsCustomer = transaction.getIRSCustomer();
        String irsCustomerID = "null";
        if (irsCustomer != null)
        {
            irsCustomerID = "'" + irsCustomer.getCustomerID() + "'";
        }
        return (irsCustomerID);
    }

    /**
     * Returns the string value to be used in the database for the sales
     * associate ID
     *
     * @param the transaction the retail transaction
     * @return The gift registry value
     */
    public String getSalesAssociateID(RetailTransactionIfc transaction)
    {
        if (transaction.getSalesAssociate() != null)
        {
            return inQuotes(transaction.getSalesAssociate().getEmployeeID());
        }
        return null;
    }

    /**
     * Returns the reason code for a suspended transaction.
     *
     * @param the transaction object containing the customer
     * @return SQL-ready version of the reason code
     */
    public String getSuspendedTransactionReasonCode(TenderableTransactionIfc transaction)
    {
        String reason;
        if (transaction.getSuspendReason() != null)
            reason = transaction.getSuspendReason().getCode();
        else
            reason = CodeConstantsIfc.CODE_UNDEFINED;
        return ("'" + reason + "'");
    }

    /**
     * Inserts shipping address information
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @exception DataException upon error
     */
    public void saveTransactionShippings(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
            throws DataException
    {

        String firstName = "";
        String lastName = "";
        String businessName = "";
        boolean isBusinessCustomer = false;
        String addrLine1 = "";
        String addrLine2 = "";
        String city = "";
        String state = "";
        String zip = "";
        String zipExt = "";
        String country = "";
        String phoneNumber = "";
        String phoneType = "";
        String phoneAreaCode = "";
        String phoneExtension = "";
        String shippingCarrier = "";
        String shippingType = "";
        CurrencyIfc baseShippingCharge = null;
        CurrencyIfc weightBasedShippingCharge = null;
        CurrencyIfc flatRate = null;
        String shippingInstruction = "";
        CurrencyIfc calculatedShippingCharge = null;

        boolean isExternalSend = false;

        int count = 0;
        if (transaction.getTransactionTotals() != null)
        {
            if ((transaction instanceof RetailTransactionIfc) )
            {
                count = ((RetailTransactionIfc)transaction).getItemSendPackagesCount();
            }
        }
        if (count > 0)
        {
            SendPackageLineItemIfc[] sendPackages = ((RetailTransactionIfc)transaction).getSendPackages();
            if (sendPackages.length == count)
            {
                for (int i = 0; i < count; i++)
                {
                    SQLInsertStatement sql = new SQLInsertStatement();

                    // Table
                    sql.setTable(TABLE_SHIPPING_RECORDS);

                    ShippingMethodIfc shippingMethod = sendPackages[i].getShippingMethod();
                    CustomerIfc shippingCustomer = sendPackages[i].getCustomer();

                    // Fields
                    sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
                    sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
                    sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
                    sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
                    sql.addColumn(FIELD_SEND_LABEL_COUNT, String.valueOf(i + 1));

                    firstName = shippingCustomer.getFirstName();
                    if (firstName != null)
                    {
                        sql.addColumn(FIELD_SHIPPING_RECORDS_FIRST_NAME, makeSafeString(firstName));
                    }
                    lastName = shippingCustomer.getLastName();
                    if (lastName != null)
                    {
                        sql.addColumn(FIELD_SHIPPING_RECORDS_LAST_NAME, makeSafeString(lastName));
                    }
                    businessName = shippingCustomer.getCustomerName();
                    if (businessName != null)
                    {
                        sql.addColumn(FIELD_SHIPPING_RECORDS_BUSINESS_NAME, makeSafeString(businessName));
                    }

                    isBusinessCustomer = shippingCustomer.isBusinessCustomer();
                    sql.addColumn(FIELD_SHIPPING_RECORDS_BUSINESS_CUSTOMER, makeStringFromBoolean(isBusinessCustomer));

                    List<AddressIfc >addressList = shippingCustomer.getAddressList();
                    if (!addressList.isEmpty())
                    {
                        AddressIfc addr = addressList.get(0);

                        Vector<String> lines = addr.getLines();
                        if (lines.size() != 0)
                        {
                            addrLine1 = (String)lines.elementAt(0);
                            if (lines.size() >= 1 && !lines.elementAt(1).equals(""))
                            {
                                addrLine2 = (String)lines.elementAt(1);
                            }
                        }
                        city = addr.getCity();
                        state = addr.getState();
                        zip = addr.getPostalCode();
                        country = addr.getCountry();
                        List<PhoneIfc>phoneList = shippingCustomer.getPhoneList();
                        if (phoneList.size() > 0)
                        {
                            for (int p = 0; p < phoneList.size(); p++)
                            {
                                PhoneIfc phone = (PhoneIfc)phoneList.get(p);
                                if (phone != null)
                                {
                                    phoneType = String.valueOf(phone.getPhoneType());
                                    phoneNumber = phone.getPhoneNumber();
                                    phoneExtension = phone.getExtension();
                                    if (!Util.isEmpty(phoneNumber))
                                    {
                                        // only one telephone number is there
                                        // for shipping customer
                                        break;
                                    }
                                }
                            }
                        }

                        if (addr.getPostalCodeExtension() != null && !addr.getPostalCodeExtension().equals(""))
                        {
                            zipExt = addr.getPostalCodeExtension();
                        }

                        sql.addColumn(FIELD_SHIPPING_RECORDS_LINE1, makeSafeString(addrLine1));
                        sql.addColumn(FIELD_SHIPPING_RECORDS_LINE2, makeSafeString(addrLine2));
                        sql.addColumn(FIELD_SHIPPING_RECORDS_CITY, makeSafeString(city));
                        sql.addColumn(FIELD_SHIPPING_RECORDS_STATE, makeSafeString(state));
                        sql.addColumn(FIELD_SHIPPING_RECORDS_POSTAL_CODE, makeSafeString(zip));
                        sql.addColumn(FIELD_SHIPPING_RECORDS_ZIP_EXT, makeSafeString(zipExt));
                        sql.addColumn(FIELD_SHIPPING_RECORDS_COUNTRY, makeSafeString(country));
                        sql.addColumn(FIELD_PHONE_TYPE, makeSafeString(phoneType));
                        sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER, makeSafeString(phoneNumber));
                        sql.addColumn(FIELD_CONTACT_AREA_TELEPHONE_CODE, makeSafeString(phoneAreaCode));
                        sql.addColumn(FIELD_CONTACT_EXTENSION, makeSafeString(phoneExtension));
                        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
                        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
                    }

                    shippingCarrier = shippingMethod.getShippingCarrier(LocaleMap
                            .getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
                    shippingType = shippingMethod.getShippingType(LocaleMap
                            .getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));
                    baseShippingCharge = shippingMethod.getBaseShippingCharge();
                    weightBasedShippingCharge = shippingMethod.getShippingChargeRateByWeight();
                    flatRate = shippingMethod.getFlatRate();
                    shippingInstruction = shippingMethod.getShippingInstructions();
                    calculatedShippingCharge = shippingMethod.getCalculatedShippingCharge();

                    isExternalSend = sendPackages[i].isExternalSend();

                    // Type of shipping method id is Integer
                    sql.addColumn(FIELD_SHIPPING_METHOD_ID, getShippingMethodID(shippingMethod));
                    sql.addColumn(FIELD_SHIPPING_CARRIER, makeSafeString(shippingCarrier));
                    sql.addColumn(FIELD_SHIPPING_TYPE, makeSafeString(shippingType));
                    sql.addColumn(FIELD_SHIPPING_BASE_CHARGE, baseShippingCharge.getStringValue());
                    sql.addColumn(FIELD_SHIPPING_CHARGE_RATE_BY_WEIGHT, weightBasedShippingCharge.getStringValue());
                    sql.addColumn(FIELD_FLAT_RATE, flatRate.getStringValue());
                    sql.addColumn(FIELD_SHIPPING_CHARGE, calculatedShippingCharge.getStringValue());
                    sql.addColumn(FIELD_SPECIAL_INSTRUCTION, makeSafeString(shippingInstruction));

                    sql.addColumn(FIELD_EXTERNAL_SHIPPING_FLAG, makeStringFromBoolean(isExternalSend));

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
                        throw new DataException(DataException.UNKNOWN, "saveTransactionShippings", e);
                    }
                }
            }
        }
    }



    /**
     * Returns the sequence number of the retail transaction line item.
     *
     * @param lineItem The retail transaction line item
     * @return the sequence number of the retail transaction line item.
     */
    protected String getLineItemSequenceNumber(AbstractTransactionLineItemIfc lineItem)
    {
        return (String.valueOf(lineItem.getLineNumber()));
    }

    /**
     * Returns the string value to be used in the database for the customer's
     * personal ID
     *
     * @param the transaction object containing the customer info
     * @return The personal ID value
     */
    @SuppressWarnings("deprecation")
    public String getPersonalIDNumber(TenderableTransactionIfc transaction)
    {
        CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
        String personalID = "null";
        if (customerInfo != null && customerInfo.getPersonalIDNumber() != null
                && customerInfo.getPersonalIDNumber().length() > 0)
        {
            personalID = "'" + customerInfo.getPersonalIDNumber()+ "'";
        }

        return (personalID);
    }

    /**
     * Returns the encrypted string value to be used in the database for the
     * customer's personal ID
     *
     * @param the transaction object containing the customer info
     * @return The encrypted personal ID value
     */
    public String getEncryptedPersonalIDNumber(TenderableTransactionIfc transaction)
    {
        CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
        String encryptedPersonalID = "null";
        if (customerInfo != null && customerInfo.getPersonalID().getEncryptedNumber() != null
                && customerInfo.getPersonalID().getEncryptedNumber().length() > 0)
        {
            encryptedPersonalID = "'" + customerInfo.getPersonalID().getEncryptedNumber() + "'";
        }

        return (encryptedPersonalID);
    }

    /**
     * Returns the masked string value to be used in the database for the
     * customer's personal ID
     *
     * @param the transaction object containing the customer info
     * @return The masked personal ID value
     */
    public String getMaskedPersonalIDNumber(TenderableTransactionIfc transaction)
    {
        CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
        String maskedPersonalID = "null";
        if (customerInfo != null && customerInfo.getPersonalID().getMaskedNumber() != null
                && customerInfo.getPersonalID().getMaskedNumber().length() > 0)
        {
            maskedPersonalID = "'" + customerInfo.getPersonalID().getMaskedNumber() + "'";
        }

        return (maskedPersonalID);
    }

    /**
     * Returns the string value to be used in the database for the customer's
     * personal ID type
     *
     * @param the transaction object containing the customer info
     * @return The personal ID type value
     */
    public String getPersonalIDType(TenderableTransactionIfc transaction)
    {
        CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
        String personalIDType = "null";

        if (customerInfo != null && customerInfo.getLocalizedPersonalIDType() != null
                && !Util.isEmpty(customerInfo.getLocalizedPersonalIDType().getCode()))
        {
            personalIDType = makeSafeString(customerInfo.getLocalizedPersonalIDType().getCode());
        }

        return (personalIDType);
    }

    /**
     * Returns the string value to be used in the database for the customer's
     * personal ID state
     *
     * @param the transaction object containing the customer info
     * @return The personal ID state value
     */
    public String getPersonalIDState(TenderableTransactionIfc transaction)
    {
        CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
        String personalIDState = "null";
        if (customerInfo != null && customerInfo.getPersonalIDState() != null
                && customerInfo.getPersonalIDState().length() > 0)
        {
            personalIDState = "'" + customerInfo.getPersonalIDState() + "'";
        }

        return (personalIDState);
    }

    /**
     * Returns the string value to be used in the database for the customer's
     * personal ID country
     *
     * @param the transaction object containing the customer info
     * @return The personal ID Country value
     */
    public String getPersonalIDCountry(TenderableTransactionIfc transaction)
    {
        CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
        String personalIDCountry = "null";
        if (customerInfo != null && customerInfo.getPersonalIDCountry() != null
                && customerInfo.getPersonalIDCountry().length() > 0)
        {
            personalIDCountry = "'" + customerInfo.getPersonalIDCountry() + "'";
        }

        return (personalIDCountry);
    }

    /**
     * Get the sql safe string for age restriction date of birth.
     *
     * @param transaction
     * @return
     */
    public String getAgeRestrictedDOB(SaleReturnTransactionIfc transaction)
    {
        return (dateToDateFormatString(transaction.getAgeRestrictedDOB().dateValue()));
    }

    /**
     * @param transaction
     * @return
     */
    public String getSendPackageCount(TenderableTransactionIfc transaction)
    {
        String value = "0";

        if ( transaction instanceof SaleReturnTransaction )
        {
            value = String.valueOf(((SaleReturnTransaction)transaction).getItemSendPackagesCount());
        }
        return value;

    }
    
    /**
     * Returns String value to used in database for Transaction Country Code .
     *
     * @param transaction
     * @return String 
     */
    public String getTransactionCountryCode(TenderableTransactionIfc transaction)
    {
        String value = null;

        if (transaction instanceof SaleReturnTransaction)
        {
            value = ((SaleReturnTransaction)transaction).getTransactionCountryCode();
        }
        else if (transaction instanceof BillPayTransaction)
        {
            value = ((BillPayTransaction)transaction).getTransactionCountryCode();
        }
        else if (transaction instanceof PaymentTransaction
                && (((PaymentTransaction)transaction).getPaymentAmount() != null))
        {
            value = ((PaymentTransaction)transaction).getPaymentAmount().getCountryCode();
        }

        return (value == null)? null : inQuotes(value);
    }

    /**
     * Returns String value to used in database for current Transaction Currency
     *
     * @param transaction
     * @return String 
     */
    public String getCurrencyType(TenderableTransactionIfc transaction)
    {
        String value = null;

        if (transaction instanceof SaleReturnTransaction && ((SaleReturnTransaction)transaction).getCurrencyType() != null)
        {
            value = ((SaleReturnTransaction)transaction).getCurrencyType().getCurrencyCode();
        }
        else if (transaction instanceof BillPayTransaction && ((BillPayTransaction)transaction).getCurrencyType() != null)
        {
            value = ((BillPayTransaction)transaction).getCurrencyType().getCurrencyCode();
        }
        else if (transaction instanceof PaymentTransaction && ((PaymentTransaction)transaction).getPaymentAmount() != null)
        {
            value = ((PaymentTransaction)transaction).getPaymentAmount().getCurrencyCode();
        }

        return (value == null)? null : inQuotes(value);
    }
    

    /**
     * Returns the shipping method
     *
     * @param shippingMethod a pos shipping method
     * @return The shipping method id
     */
    public String getShippingMethodID(ShippingMethodIfc shippingMethod)
    {
        return (String.valueOf(Integer.valueOf(shippingMethod.getShippingMethodID())));
    }

    /**
     * Returns the first name
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return first name
     */
    public String getFirstName(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String firstName = orderDeliveryDetail.getFirstName();
        return (makeSafeString(firstName));
    }

    /**
     * Returns the last name
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return last name
     */
    public String getLastName(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String lastName = orderDeliveryDetail.getLastName();
        return (makeSafeString(lastName));
    }

    /**
     * Returns the business name
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return business name
     */
    public String getBusinessName(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String businessName = orderDeliveryDetail.getBusinessName();
        return (makeSafeString(businessName));
    }

    /**
     * Returns the delivery address
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return delivery address
     */
    public String getDeliveryAddress1(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        AddressIfc address = orderDeliveryDetail.getDeliveryAddress();
        Vector<String> addressLines = address.getLines();
        String deliveryAddress1 = null;
        if (addressLines != null && addressLines.size() > 0)
        {
            deliveryAddress1 = (String)addressLines.elementAt(0);
        }
        return (makeSafeString(deliveryAddress1));
    }

    /**
     * Returns the delivery address
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return delivery address
     */
    public String getDeliveryAddress2(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        AddressIfc address = orderDeliveryDetail.getDeliveryAddress();
        Vector<String> addressLines = address.getLines();
        String deliveryAddress2 = null;
        if (addressLines != null && addressLines.size() > 0)
        {
            deliveryAddress2 = (String)addressLines.elementAt(1);
        }
        return (makeSafeString(deliveryAddress2));
    }

    /**
     * Returns the city
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return city
     */
    public String getCity(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String city = orderDeliveryDetail.getDeliveryAddress().getCity();
        return (makeSafeString(city));
    }

    /**
     * Returns the country
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return country
     */
    public String getCountry(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String country = orderDeliveryDetail.getDeliveryAddress().getCountry();
        return (makeSafeString(country));

    }

    /**
     * Returns the state
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return state
     */
    public String getState(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String state = orderDeliveryDetail.getDeliveryAddress().getState();
        return (makeSafeString(state));

    }

    /**
     * Returns the postal code
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return postal code
     */
    public String getPostalCode(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String getPostalCode = orderDeliveryDetail.getDeliveryAddress().getPostalCode();
        return (makeSafeString(getPostalCode));

    }

    /**
     * Returns the phone type
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return phone type
     */
    public String getPhoneType(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String phoneType = orderDeliveryDetail.getContactPhone().getPhoneType() + "";
        return (makeSafeString(phoneType));

    }

    /**
     * Returns the phone number
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return phone number
     */
    public String getPhoneNumber(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String phoneNumber = orderDeliveryDetail.getContactPhone().getPhoneNumber();
        return (makeSafeString(phoneNumber));

    }

    /**
     * Returns the special instruction
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return special instruction
     */
    public String getSpecialInstructions(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String specialInstruction = orderDeliveryDetail.getSpecialInstructions();
        return (makeSafeString(specialInstruction));

    }

    /**
     * Returns the delivery date
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return delivery date
     */
    public EYSDate getDeliveryDate(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        EYSDate deliveryDate = orderDeliveryDetail.getDeliveryDate();
        return (deliveryDate);
    }

    /**
     * Return the delivery shipping carrier
     *
     * @param orderDeliveryDetail
     * @return
     */
    public String getDeliveryShippingCarrier(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String shippingCarrier = orderDeliveryDetail.getShippingCarrier();
        return (makeSafeString(shippingCarrier));
    }

    /**
     * Return the delivery shipping type
     * @param orderDeliveryDetail
     * @return
     */
    public String getDeliveryShippingType(OrderDeliveryDetailIfc orderDeliveryDetail)
    {
        String shippingType = orderDeliveryDetail.getShippingType();
        return (makeSafeString(shippingType));
    }

    /**
     * Returns the business date
     *
     * @param orderDeliveryDetail of OrderDeliveryDetailIfc
     * @return business date
     */
    public String getBusinessDate(OrderTransactionIfc orderTransaction)
    {
        String businessDate = dateToSQLDateString(orderTransaction.getBusinessDay().dateValue());

        return (businessDate);
    }

    /**
     * Insert a Delivery Detail information into the database.
     *
     * @param dataConnection Connection to the database
     * @param orderTransaction OrderTransaction
     * @param orderDeliveryDetail OrderDeliveryDetail
     * @throws DataException If database save fails.
     */
    public void insertOrderDeliveryDetail(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            OrderDeliveryDetailIfc orderDeliveryDetail) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        String tableName = TABLE_DELIVERY_ORDER_RECORD;
        sql.setTable(tableName);

        // Fields
        sql.addColumn(FIELD_ORDER_ID, getOrderID(orderTransaction)); // order
        sql.addColumn(FIELD_DELIVERY_ORDER_ID, Integer.toString(orderDeliveryDetail.getDeliveryDetailID())); // delivery
        sql.addColumn(FIELD_ORDER_STATUS, getOrderStatus(orderTransaction)); // order
        sql.addColumn(FIELD_CUSTOMER_ID, getCustomerID(orderTransaction)); // customerID
        sql.addColumn(FIELD_DELIVERY_ORDER_FIRST_NAME, getFirstName(orderDeliveryDetail)); // first
        sql.addColumn(FIELD_DELIVERY_ORDER_LASTNAME, getLastName(orderDeliveryDetail)); // last
        sql.addColumn(FIELD_DELIVERY_ORDER_BUSINESS_NAME, getBusinessName(orderDeliveryDetail)); // business
        sql.addColumn(FIELD_DELIVERY_ORDER_ADDRESS_LINE1, getDeliveryAddress1(orderDeliveryDetail)); // delivery
        sql.addColumn(FIELD_DELIVERY_ORDER_ADDRESS_LINE2, getDeliveryAddress2(orderDeliveryDetail)); // delivery
        sql.addColumn(FIELD_DELIVERY_ORDER_CITY, getCity(orderDeliveryDetail)); // city
        sql.addColumn(FIELD_DELIVERY_ORDER_COUNTRY, getCountry(orderDeliveryDetail)); // country
        sql.addColumn(FIELD_DELIVERY_ORDER_STATE, getState(orderDeliveryDetail)); // state
        sql.addColumn(FIELD_DELIVERY_ORDER_POSTAL_CODE, getPostalCode(orderDeliveryDetail)); // postal
        sql.addColumn(FIELD_DELIVERY_ORDER_PHONE_TYPE, getPhoneType(orderDeliveryDetail)); // phone
        sql.addColumn(FIELD_DELIVERY_ORDER_CONTACT_PHONE_NUMBER, getPhoneNumber(orderDeliveryDetail)); // phone
        sql.addColumn(FIELD_DELIVERY_ORDER_SPECIAL_INSTRUCTIONS, getSpecialInstructions(orderDeliveryDetail)); // special
        sql.addColumn(FIELD_DELIVERY_ORDER_DELIVERY_DATE, dateToDateFormatString(getDeliveryDate(orderDeliveryDetail)
                .dateValue())); // delivery date

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
            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * Insert a Delivery Detail information into the database.
     *
     * @param dataConnection Connection to the database
     * @param orderTransaction OrderTransaction
     * @param orderDeliveryDetail OrderDeliveryDetail
     * @throws DataException If database save fails.
     */
    public void insertOrderDeliveryDetailStatus(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            OrderDeliveryDetailIfc orderDeliveryDetail) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        String tableName = TABLE_TRANSACTION_ORDER_DELIVERY_DETAIL;
        sql.setTable(tableName);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
        sql.addColumn(FIELD_TRANSACTION_DELIVERY_ORDER_ID, Integer.toString(orderDeliveryDetail.getDeliveryDetailID())); // delivery
        sql.addColumn(FIELD_ORDER_ID, getOrderID(orderTransaction)); // order
        sql.addColumn(FIELD_ORDER_STATUS, getOrderStatus(orderTransaction)); // order
        sql.addColumn(FIELD_CUSTOMER_ID, getCustomerID(orderTransaction)); // customerID
        sql.addColumn(FIELD_DELIVERY_ORDER_FIRST_NAME, getFirstName(orderDeliveryDetail)); // first
        sql.addColumn(FIELD_DELIVERY_ORDER_LASTNAME, getLastName(orderDeliveryDetail)); // last
        sql.addColumn(FIELD_DELIVERY_ORDER_BUSINESS_NAME, getBusinessName(orderDeliveryDetail)); // business
        sql.addColumn(FIELD_DELIVERY_ORDER_ADDRESS_LINE1, getDeliveryAddress1(orderDeliveryDetail)); // delivery
        sql.addColumn(FIELD_DELIVERY_ORDER_ADDRESS_LINE2, getDeliveryAddress2(orderDeliveryDetail)); // delivery
        sql.addColumn(FIELD_DELIVERY_ORDER_CITY, getCity(orderDeliveryDetail)); // city
        sql.addColumn(FIELD_DELIVERY_ORDER_COUNTRY, getCountry(orderDeliveryDetail)); // country
        sql.addColumn(FIELD_DELIVERY_ORDER_STATE, getState(orderDeliveryDetail)); // state
        sql.addColumn(FIELD_DELIVERY_ORDER_POSTAL_CODE, getPostalCode(orderDeliveryDetail)); // postal
        sql.addColumn(FIELD_DELIVERY_ORDER_PHONE_TYPE, getPhoneType(orderDeliveryDetail)); // phone
        sql.addColumn(FIELD_DELIVERY_ORDER_CONTACT_PHONE_NUMBER, getPhoneNumber(orderDeliveryDetail)); // phone
        sql.addColumn(FIELD_DELIVERY_ORDER_SPECIAL_INSTRUCTIONS, getSpecialInstructions(orderDeliveryDetail)); // special
        sql.addColumn(FIELD_DELIVERY_ORDER_DELIVERY_DATE, dateToDateFormatString(getDeliveryDate(orderDeliveryDetail)
                .dateValue())); // delivery date
        sql.addColumn(FIELD_DELIVERY_ORDER_SHIPPING_CARRIER, getDeliveryShippingCarrier(orderDeliveryDetail)); // shipping carrier
        sql.addColumn(FIELD_DELIVERY_ORDER_SHIPPING_TYPE, getDeliveryShippingType(orderDeliveryDetail)); // shipping carrier

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
            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * Insert a Order Recipient information into the database.
     *
     * @param dataConnection Connection to the database
     * @param orderTransaction OrderTransaction
     * @param orderRecipientDetail OrderRecipient
     * @throws DataException If database save fails.
     */
    public void insertOrderRecipientDetail(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            OrderRecipientIfc orderRecipientDetail) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_ORDER_RECIPIENT_DETAIL);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
        sql.addColumn(FIELD_ORDER_RECIPIENT_ORDER_ID, getOrderID(orderTransaction));
        sql.addColumn(FIELD_ORDER_RECIPIENT_ORDER_STATUS, getOrderStatus(orderTransaction));
        CustomerIfc customer = orderTransaction.getCustomer();
        if (customer != null)
        {
            sql.addColumn(FIELD_CUSTOMER_ID, inQuotes(customer.getCustomerID()));
        }
        EYSDate actualPickupDate = DomainGateway.getFactory().getEYSDateInstance();
        orderRecipientDetail.setActualPickupDate(actualPickupDate);
        sql.addColumn(FIELD_ORDER_RECIPIENT_ACTUAL_PICKUP_DATE, getActualPickupDate(orderRecipientDetail));

        try
        {
            dataConnection.execute(sql.getSQLString());
            HashMap<String, Object> map = new HashMap<String, Object>(4);
            map.put(FIELD_RETAIL_STORE_ID, orderTransaction.getWorkstation().getStoreID());
            map.put(FIELD_WORKSTATION_ID, orderTransaction.getWorkstation().getWorkstationID());
            map.put(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(orderTransaction.getTransactionSequenceNumber()));
            SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
            String dateString = format.format(orderTransaction.getBusinessDay().dateValue());
            map.put(FIELD_BUSINESS_DAY_DATE, dateString);
            DatabaseBlobHelperIfc helper = DatabaseBlobHelperFactory.getInstance().getDatabaseBlobHelper(dataConnection
                    .getConnection());
            Point[] signaturePoints = (Point[])orderRecipientDetail.getCustomerSignature();
            String signatureData = ImageUtils.getInstance().convertPointArrayToXYString(signaturePoints);
            if (helper != null)
            {
                helper.updateBlob(dataConnection.getConnection(),
                        TABLE_ORDER_RECIPIENT_DETAIL, FIELD_ORDER_RECIPIENT_CUSTOMER_SIGNATURE,
                        signatureData.getBytes(), map);
            }
            else
            {
                logger.warn("" + helper + "");
            }

        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }

        // read order delivery detail id
        ResultSet rs = readOrderDeliveryDetail(dataConnection, orderTransaction);
        try
        {
            if (rs.next())
            {
                // update order status in order delivery detail
                updateOrderDeliveryDetail(dataConnection, orderTransaction);
            }
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "read OrderDeliveryDetail");
            throw new DataException(DataException.SQL_ERROR, "read OrderDeliveryDetail", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "read OrderDeliveryDetail", e);
        }

    }

    /**
     * Updates the order delivery detail table.
     *
     * @param dataConnection
     * @param orderTransaction The Order Transaction to update
     * @exception DataException
     */
    public void updateOrderDeliveryDetail(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction)
            throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_DELIVERY_ORDER_RECORD);

        // Fields
        sql.addColumn(FIELD_ORDER_STATUS, getOrderStatus(orderTransaction)); // order

        // Qualifiers
        sql.addQualifier(FIELD_ORDER_ID + " = " + getOrderID(orderTransaction));
        sql.addQualifier(FIELD_CUSTOMER_ID + " = " + makeSafeString(orderTransaction.getCustomer().getCustomerID()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(e.toString());
            throw new DataException(DataException.UNKNOWN, "updateOrderDeliveryDetail", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update OrderDeliveryDetail");
        }

    }

    /**
     * Returns the result set.
     *
     * @param dataConnection JdbcDataConnection
     * @param orderTransaction OrderTransactionIfc
     * @return the result set
     */
    public ResultSet readOrderDeliveryDetail(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction)
            throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        String orderID = orderTransaction.getOrderID();

        // table
        sql.setTable(TABLE_DELIVERY_ORDER_RECORD);

        // column
        sql.addColumn(FIELD_DELIVERY_ORDER_ID);

        // qualifier
        sql.addQualifier(FIELD_ORDER_ID + " = '" + orderID + "'");

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            return rs;
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "read OrderDeliveryDetail", e);
        }
    }

    /**
     * Get a safe string for Actual Pickup Date
     *
     * @param OrderRecipientIfc
     * @return
     */
    public String getActualPickupDate(OrderRecipientIfc orderRecipient)
    {
        String date = "null";
        if (orderRecipient.getActualPickupDate() != null)
        {
            date = dateToSQLTimestampString(orderRecipient.getActualPickupDate().dateValue());
        }

        return date;
    }

    /**
     * Get a Order id
     *
     * @param OrderTransactionIfc
     * @return order id
     */
    public String getOrderID(OrderTransactionIfc orderTransaction)
    {
        String orderID = orderTransaction.getOrderID();
        return inQuotes(orderID);
    }

    /**
     * Get a order status
     *
     * @param OrderTransactionIfc
     * @return order status
     */
    public int getOrderStatus(OrderTransactionIfc orderTransaction)
    {
        int orderStatus = orderTransaction.getOrderStatus().getStatus().getStatus();
        return orderStatus;
    }

    public String getExternalOrderID(SaleReturnTransactionIfc transaction)
    {
        if (!StringUtils.isBlank(transaction.getExternalOrderID()))
        {
            return makeSafeString(transaction.getExternalOrderID());
        }
        return "null";
    }

    public String getExternalOrderNumber(SaleReturnTransactionIfc transaction)
    {
        if (!StringUtils.isBlank(transaction.getExternalOrderNumber()))
        {
            return makeSafeString(transaction.getExternalOrderNumber());
        }
        return "null";
    }

    /**
     * Return the string representation of
     * {@link SaleReturnTransactionIfc#getExternalOrderType()}.
     *
     * @param transaction
     * @return
     */
    public String getExternalOrderType(SaleReturnTransactionIfc transaction)
    {
        int type = transaction.getExternalOrderType();
        if (type > -1 && type < ExternalOrderConstantsIfc.ORDER_TYPE_DESCRIPTORS.length)
        {
            return inQuotes(ExternalOrderConstantsIfc.ORDER_TYPE_DESCRIPTORS[type]);
        }

        logger.warn("The specified external order type did not match a value in the descriptors list. Type = " + type);
        return "null";
    }
}
