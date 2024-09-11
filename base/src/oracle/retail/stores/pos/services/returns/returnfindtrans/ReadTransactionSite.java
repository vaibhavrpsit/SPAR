/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/ReadTransactionSite.java /main/27 2014/06/03 17:06:12 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    vinees 11/19/14 - If there are multiple transaction with same ID Passing 
 *                      business date also to get transaction from database.
 *    yiqzha 11/13/14 - Avoid set hasRecept to false when a customer has a receipt
 *                      but the transaction data cannot be found in database.
 *                      The item entered should not re-price.
 *    jswan  11/10/14 - limit the number of Customer Gift Lists retrieved by ICE.
 *    yiqzha 11/10/14 - Set hasReceipt to false if the transaction can't be found.
 *    crain  10/01/14 - Fix to prevent user to return items (non retrieved
 *                         receipted return) from training mode in the normal
 *                         mode and vice versa.
 *    asinto 06/03/14 - added extended customer data locale to the
 *                      CustomerSearchCriteriaIfc.
 *    asinto 03/19/14 - changed override value for ReturnMaximumMatches to 10
 *                      if configured value is 0
 *    asinto 03/18/14 - override ReturnMaximumMatches value with 1 if parameter
 *                      is set to 0
 *    tkshar 12/10/12 - commons-lang update 3.1
 *    jswan  10/25/12 - Modified to support returns by order.
 *    acadar 08/03/12 - moved customer search criteria
 *    acadar 06/26/12 - Cross Channel changes
 *    acadar 05/29/12 - changes for cross channel
 *    acadar 05/23/12 - CustomerManager refactoring
 *    asinto 03/19/12 - Retrieve customer data for retrieved transaction
 *    cgreen 09/19/11 - formatting of javadoc
 *    ohorne 08/10/11 - fix for transaction filtering by item number
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    jswan  05/11/10 - Pre code reveiw clean up.
 *    jswan  05/11/10 - Returns flow refactor: modifiec to mail a more
 *                      appropriate letter when the transaction is not found.
 *    abhayg 03/02/10 - For fixing the issue when user enter the reciept no .
 *                      in which any of last 4 charcters are alphabet
 *    
 *    abhayg 03/02/10 - For Fixing the null pointer issue
 *    abonda 01/03/10 - update header date
 *    mchell 12/10/09 - Serialisation return without receipt changes
 *
 * ===========================================================================

     $Log:
      8    360Commerce 1.7         3/25/2008 5:14:12 AM   Sujay Beesnalli
           Forward ported from v12x. Modified to allow for the retrieval of
           and selection from multple transactions.
      7    360Commerce 1.6         2/22/2008 2:11:38 PM   Christian Greene
           CR30318 Rollback 2 versions to CTR fix with small updates to use
           CompressionUtility.
      6    360Commerce 1.5         2/20/2008 3:22:09 PM   Alan N. Sinton  CR
           30318: Reverting changes to unbreak CTR.
      5    360Commerce 1.4         2/13/2008 1:50:19 PM   Christian Greene
           Update returns-lookup to eliminate the extra isVoided call to
           CentralOffice by setting voided transaction's status upon
           post-void. Also compress XML data retrieved from CO during
           returns-lookup.
      4    360Commerce 1.3         11/9/2006 6:55:26 PM   Jack G. Swan
           Initial XMl Replication check-in.
      3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:33 PM  Robert Pearse
     $
     Revision 1.18  2004/07/28 21:00:58  epd
     @scr 6579 fixed logic when more than 1 txn is found

     Revision 1.17  2004/07/13 15:55:26  jriggins
     @scr 6183 Added a check for post voided transactions

     Revision 1.16  2004/06/03 14:47:42  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.15  2004/04/20 13:17:05  tmorris
     @scr 4332 -Sorted imports

     Revision 1.14  2004/04/14 20:50:01  tfritz
     @scr 4367 - Renamed moveTransactionToOrigninal() method to moveTransactionToOriginal() method and added a call to setOriginalTransactionId() in this method.

     Revision 1.13  2004/04/14 15:17:09  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.12  2004/03/22 06:17:50  baa
     @scr 3561 Changes for handling deleting return items

     Revision 1.10  2004/03/10 19:23:15  epd
     @scr 3561 fixed letter being mailed by dialog

     Revision 1.9  2004/03/04 20:50:28  baa
     @scr 3561 returns add support for units sold

     Revision 1.8  2004/02/27 22:43:50  baa
     @scr 3561 returns add trans not found flow

     Revision 1.7  2004/02/26 16:47:09  rzurga
     @scr 0 Add optional and customizable date to the transaction id and its receipt barcode

     Revision 1.6  2004/02/24 15:15:34  baa
     @scr 3561 returns enter item

     Revision 1.5  2004/02/17 20:40:28  baa
     @scr 3561 returns

     Revision 1.4  2004/02/13 22:46:22  baa
     @scr 3561 Returns - capture tender options on original trans.

     Revision 1.3  2004/02/12 16:51:48  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:28  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.6   Jan 23 2004 16:07:52   baa
 * continue returns development
 *
 *    Rev 1.5   Jan 13 2004 14:41:54   baa
 * continue return developemnt
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.4   Dec 30 2003 16:58:20   baa
 * cleanup for return feature
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.3   Dec 29 2003 15:36:10   baa
 * return enhancements
 *
 *    Rev 1.2   Dec 19 2003 13:22:32   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.1   Dec 17 2003 11:20:34   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Aug 29 2003 16:06:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 14 2003 09:26:04   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   Aug 14 2002 14:16:08   jriggins
 * Switched call from displayInvalidTransaction() to displayInvalidTransactionNoSellItems().
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:05:56   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:42   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 10 2002 18:01:16   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Jan 22 2002 17:40:08   dfh
 * initial support for order partial
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   Dec 10 2001 10:06:14   dfh
 * added check to include order complete transactions
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *

 *    Rev 1.0   Sep 21 2001 11:24:58   msg

 * Initial revision.

 *
 *    Rev 1.1   Sep 17 2001 13:12:40   msg
 * header update
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction;
import oracle.retail.stores.domain.arts.TransactionReadDataTransaction;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.data.AbstractDBUtils;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteria;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CustomerSearchCriteriaIfc.SearchType;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.NoTransactionsErrorSite;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.commons.lang3.StringUtils;

/**
 * Read the transaction.
 */
public class ReadTransactionSite extends NoTransactionsErrorSite
{
    // for serialization compatibility
    private static final long serialVersionUID = 1985728140980582L;

    /**
     * Constant for maximum number of matches default.
     */
    public static final int RETURN_MAXIMUM_MATCHES_DEFAULT = 20;

    /**
     * Read the transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo) bus.getCargo();

        //Display Retrieving transaction message
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.RETRIEVE_TRANSACTION, new POSBaseBeanModel());

        //get utility manager
    	UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        /*
         * Lookup the transaction from the ID stored in the cargo.
         */
        TransactionIDIfc transactionID = cargo.getOriginalTransactionId();
        boolean noData = false;
        boolean invalidTrans = false;
        Letter letter = null;

        try
        {
            boolean trainingMode = cargo.getRegister().getWorkstation().isTrainingMode();

            LocaleRequestor localeRequestor = utility.getRequestLocales();

            TransactionIfc[] transactions = null;

            if (transactionID != null && !Util.isEmpty(transactionID.getTransactionIDString()))
            {
                cargo.setHaveReceipt(true);
                String transIDString = transactionID.getTransactionIDString();
                EYSDate businessDate = cargo.getOriginalBusinessDate();
                EYSDate transactionBusinessDate = null;

                TransactionReadDataTransaction dt = null;

                dt = (TransactionReadDataTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_TRANSACTIONS_FOR_RETURN);

                if(businessDate != null)
                {
                    transactionBusinessDate = businessDate;
                }
                else
                {
                    transactionBusinessDate = transactionID.getBusinessDate();
                }
                
                transactions = dt.readTrainingTransactionsByID(transIDString, transactionBusinessDate, trainingMode, localeRequestor);

                // We only want those transactions which have not been post voided.
                TransactionIfc nonVoidedTransactions[] = new TransactionIfc[0];
                if (transactions != null)
                {
                    ArrayList<TransactionIfc> nonVoidedTransactionList = new ArrayList<TransactionIfc>(transactions.length);
                    CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                    TenderableTransactionIfc tenderableTransaction = null;
                    CustomerIfc customer = null;
                    for (int i = 0; i < transactions.length; i++)
                    {
                        if(transactions[i] instanceof TenderableTransactionIfc &&
                                StringUtils.isNotBlank(((TenderableTransactionIfc)transactions[i]).getCustomerId()))
                        {
                            tenderableTransaction = (TenderableTransactionIfc)transactions[i];
                            try
                            {
                                CustomerSearchCriteriaIfc criteria = new CustomerSearchCriteria(SearchType.SEARCH_BY_CUSTOMER_ID,tenderableTransaction.getCustomerId(), utility.getRequestLocales());
                                Locale extendedDataRequestLocale = cargo.getOperator().getPreferredLocale();
                                if(extendedDataRequestLocale == null)
                                {
                                    extendedDataRequestLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
                                }
                                criteria.setExtendedDataRequestLocale(extendedDataRequestLocale);
                                int maxCustomerItemsPerListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxCustomerItemsPerListSize", "10"));
                                criteria.setMaxCustomerItemsPerListSize(maxCustomerItemsPerListSize);
                                int maxTotalCustomerItemsSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxTotalCustomerItemsSize", "40"));
                                criteria.setMaxTotalCustomerItemsSize(maxTotalCustomerItemsSize);
                                int maxNumberCustomerGiftLists = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxNumberCustomerGiftLists", "4"));
                                criteria.setMaxNumberCustomerGiftLists(maxNumberCustomerGiftLists);
                                customer = customerManager.getCustomer(criteria);
                                tenderableTransaction.setCustomer(customer);
                            }
                            catch (DataException de)
                            {
                                logger.warn("Could not retrieve customer: " + tenderableTransaction.getCustomerId(), de);
                            }
                        }
                        if(!trainingMode)
                        {
                            // no longer ask remote server if voided, transaction should know. 08FEB08 CMG
                            if (!transactions[i].isTrainingMode() && !transactions[i].isVoided())//dt.isTransactionVoided(transactions[i]))
                            {
                                nonVoidedTransactionList.add(transactions[i]);
                            }
                        }
                        else
                        {
                            if(transactions[i].isTrainingMode() && !transactions[i].isVoided())
                            {
                                nonVoidedTransactionList.add(transactions[i]);
                            }
                        }
                    }

                    nonVoidedTransactions = nonVoidedTransactionList.toArray(nonVoidedTransactions);
                }

                letter = storeTransactionsInCargo(cargo, nonVoidedTransactions, bus);
            }
            else
            {

                SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();

                // Removed this line for Cross Store Returns; the store number should
                // on be set by the user in the UI.
                // searchCriteria.setStoreNumber(cargo.getRegister().getWorkstation().getStoreID());
              
                
                searchCriteria.setTrainingMode(getTrainingMode(trainingMode));
                int maxTrans = getMaximumNumberOfTransactions(bus);
                searchCriteria.setMaximumMatches(maxTrans);
                searchCriteria.setLocaleRequestor(localeRequestor);
                
                TransactionHistoryDataTransaction trans = null;

                trans = (TransactionHistoryDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_HISTORY_DATA_TRANSACTION);

                TransactionSummaryIfc[] summary = trans.readTransactionHistory(searchCriteria);
                
                letter = storeTransactionsInCargo(cargo, summary, bus);
            }
        }
        catch (DataException de)
        {
            logger.error( "Can't find Transaction for ID=" + transactionID + "");
            logger.error( "" + de + "");
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                noData = true;
            }
            else
            {
                cargo.setDataExceptionErrorCode(de.getErrorCode());
                letter = new Letter(CommonLetterIfc.DB_ERROR);
            }
        }
        catch (IllegalArgumentException iae)
        {
            noData = true;
            invalidTrans = true;
        }

        if (noData)
        {
            if (invalidTrans)
            {
                displayInvalidTransactionNoSellItems(bus);
            }
            else
            {
                displayNoTransactionsForNumber(bus);
                cargo.setTransactionFound(false);
            }
        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Puts the transactions into the cargo and determine the next step, i.e.
     * which letterName to mail.
     * 
     * @return name of the next action to take
     * @Param ReturnFindTransCargo location to put retrieved transactions
     * @Param TransactionIfc[] the array of transactions
     */
    protected Letter storeTransactionsInCargo(ReturnFindTransCargo cargo,
            TransactionSummaryIfc[] transactions,
            BusIfc bus)
    {
        // Initialize the letterName
        String letterName = CommonLetterIfc.SUCCESS;

        // Check to see if there are too many
        int maxTrans = getMaximumNumberOfTransactions(bus);
        if (transactions.length > maxTrans)
        {
            letterName = CommonLetterIfc.TOO_MANY;
        }
        else
        {
            String itemSerialNumber = cargo.getSearchCriteria().getItemSerialNumber();
            String itemNumber = cargo.getSearchCriteria().getItemNumber();
            TransactionSummaryIfc[] filteredSummary = null;

            if (!Util.isEmpty(itemSerialNumber))
            {
                // If item id is not entered for serial number search, don't
                // filter transactions using item id.
                if (Util.isEmpty(itemNumber))
                {
                    filteredSummary = transactions;
                }
                else
                {
                    filteredSummary = findSummariesByItem(transactions, itemNumber);
                }
            }
            else
            {
                filteredSummary = findSummariesByItem(transactions, itemNumber);
            }

            if (filteredSummary == null || filteredSummary.length == 0)
            {
                letterName = CommonLetterIfc.INVALID;
            }
            else
            {
                cargo.setTransactionSummaries(filteredSummary, true);

                if (filteredSummary.length == 1)
                {
                    cargo.setSelectedIndex(0);
                    cargo.setSelectedSummaryIndex(0);
                }
                else
                {
                    letterName = CommonLetterIfc.MULTIPLE_MATCHES;
                }
            }
        }

        return (new Letter(letterName));

    }

    /**
     * Puts the transactions into the cargo and determine the next step, i.e.
     * which letter to mail.
     * 
     * @return Letter the next action to take
     * @Param ReturnFindTransCargo location to put retrieved transactions
     * @Param TransactionIfc[] the array of transactions
     * @Param BusIfc the current bus
     */
    protected Letter storeTransactionsInCargo(ReturnFindTransCargo cargo,
            TransactionIfc[] transactions, BusIfc bus)
        throws IllegalArgumentException
    {
        // Remove transactions from the cargo, if any already existed.
        cargo.resetTransactions();

        // Initialize the letter
        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        // Get all valid transactions.
        int transactionCount = 0;
        OrderTransactionIfc orderTransaction=null;
        int orderType=0;

        for (int i = 0; i < transactions.length; i++)
        {
            if(transactions[i] instanceof OrderTransaction)
            {
            	orderTransaction=(OrderTransaction)transactions[i];
            	orderType=orderTransaction.getOrderType();
            }
            if (transactions[i].getTransactionType() == TransactionIfc.TYPE_SALE
                || transactions[i].getTransactionType() == TransactionIfc.TYPE_EXCHANGE
                || transactions[i].getTransactionType() == TransactionIfc.TYPE_RETURN
                || transactions[i].getTransactionType() == TransactionIfc.TYPE_LAYAWAY_COMPLETE
                || transactions[i].getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
                || transactions[i].getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
                || orderType == OrderConstantsIfc.ORDER_TYPE_ON_HAND  &&(transactions[i].getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE) )
            {
                cargo.addToTransactions((SaleReturnTransactionIfc) transactions[i]);
                transactionCount++;
            }
        }

        // Check to see if there are too many
        int maxTrans = getMaximumNumberOfTransactions(bus);
        if (transactionCount > maxTrans)
        {
            letter = new Letter(CommonLetterIfc.TOO_MANY);
        }
        else if (transactionCount == 0)
        {
            logger.warn( "There are no valid transactions for the current transactionID.");
            throw new IllegalArgumentException();
        }
        else if (transactionCount == 1)
        {
            // If this transaction has value in the the Order ID data member...
            String orderID = cargo.getTransactionFromCollection(0).getOrderID();
            if (!Util.isEmpty(orderID))
            {
                letter = new Letter(ReturnUtilities.TRANSACTION_HAS_ORDER);
                cargo.setSelectedTransactionOrderID(orderID);
            }
            else
            {
                cargo.moveTransactionToOriginal(cargo.getTransactionFromCollection(0));
            }
        }
        else
        {
            // If there is more than one transaction, mail this letter.
            letter = new Letter(CommonLetterIfc.MULTIPLE_MATCHES);
        }

        return letter;
    }

    /**
     * Gets the maximum number of transactions from the parameter manager.
     * 
     * @return int the maximum number of transactions
     * @param bus the current bus
     */
    static public int getMaximumNumberOfTransactions(BusIfc bus)
    {
        // look up Till Count Float parameter
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        Serializable[] values = null;
        int max = RETURN_MAXIMUM_MATCHES_DEFAULT;

        try
        {
            values = pm.getParameterValues(ParameterConstantsIfc.RETURN_ReturnMaximumMatches);
            Long mL = new Long((String) values[0]);
            max = mL.intValue();
            if(max == 0)
            {
                logger.warn("Invalid setting of '0' found for parameter: " + ParameterConstantsIfc.RETURN_ReturnMaximumMatches + ", using value of '10' instead");
                max = 10;
            }
        }
        catch (ParameterException e)
        {
            // Use default
        }
        catch (NumberFormatException ne)
        {
            // Use default
        }

        return max;
    }

    /**
     * Provides an array of transaction summaries containing the specified item
     * number. If no matches are found and empty array is returned.
     * 
     * @param itemID provides the item number utilized to filter the currently
     *            activive transaction summary list.
     * @return an array of transaction summaries filtered based upon the
     *         provided parameters.
     */
    protected TransactionSummaryIfc[] findSummariesByItem(TransactionSummaryIfc[] currentSummaries, String itemNumber)
    {
        TransactionSummaryIfc summary = null;
        ArrayList<TransactionSummaryIfc> filteredList = new ArrayList<TransactionSummaryIfc>();

        for (int idx = 0; idx < currentSummaries.length; idx++)
        {
            summary = currentSummaries[idx];
            if (summary.isItemInSummary(itemNumber))
            {
                filteredList.add(summary);
            }
        }

        TransactionSummaryIfc[] result = new TransactionSummaryIfc[filteredList.size()];
        filteredList.toArray(result);

        return result;
    }

    /**
     * Convert training mode boolean flag to sql format.
     * 
     * @Param boolean trainingMode
     * @return String result '1' if true '0' otherwise
     */
    protected String getTrainingMode(boolean trainingMode)
    {
        return trainingMode? AbstractDBUtils.TRUE : AbstractDBUtils.FALSE;
    }

    /**
     * Show the NO TRANSACTIONS FOR NUMBER ERROR SCREEN
     * 
     * @param bus Service Bus
     */
    public void displayNoTransactionsForNumber(BusIfc bus)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("RetrieveTransactionNotFound");
        dialogModel.setType(DialogScreensIfc.CONFIRMATION);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES,"Retry");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO,"TransactionNotFound");
        // display the screen
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}
