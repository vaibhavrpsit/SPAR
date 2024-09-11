/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/history/ReadCustomerHistorySite.java /main/15 2014/01/13 12:27:52 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    subrde 01/13/14 - Modified the parameter constant in
 *                      getMaximumNumberOfTransactions to
 *                      CUSTOMER_MaximumTransactionHistoryNumber
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    blarse 07/09/09 - XbranchMerge
 *                      blarsen_bug8629786-transaction-lookup-by-card-number-fails
 *                      from rgbustores_13.1x_branch
 *    blarse 07/07/09 - Removing previous change that includes store number in
 *                      search criteria. This limits the scope in cases where
 *                      it should not. CO now uses default hierarchy when no
 *                      store criteria us specified.
 *    blarse 07/01/09 - XbranchMerge
 *                      blarsen_bug8629786-customer-transaction-history-retrieval-fails-on-was
 *                      from main
 *    blarse 06/29/09 - Adding the current store id to the search criteria.
 *                      Some early assumtions in this feature are not valid.
 *                      The assumption that the creditionals sent to CO is
 *                      associated with a store hierarchy is not valid for WAS.
 *                      In this case no creditials are sent. CO was modified to
 *                      *not* filter on logged in user. In some cases a tore
 *                      criteria is required when a users hierarchy is not
 *                      available. Requirements at this time are to only lookup
 *                      current stores transactions.
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         11/9/2006 6:52:52 PM   Jack G. Swan
           Initial XMl Replication check-in.
      3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:24:30 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse   
     $
     Revision 1.7.2.1  2005/01/14 19:26:20  bwf
     @scr 7869 Moved fix for 6837 into ReadCustomerHistorySite so that we can check whether to history list at all.

     Revision 1.7  2004/06/03 14:47:43  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.6  2004/04/20 13:11:00  tmorris
     @scr 4332 -Sorted imports

     Revision 1.5  2004/04/14 15:17:10  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.4  2004/03/03 23:15:11  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:49:31  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:44:51  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 * 
 *    Rev 1.0   Aug 29 2003 15:55:48   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.7   Mar 21 2003 10:58:30   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.6   Mar 20 2003 18:18:50   baa
 * customer screens refactoring
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 * 
 *    Rev 1.5   Mar 13 2003 13:29:14   bwf
 * Database Internationalization
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.4   Mar 03 2003 16:36:52   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.3   Jan 31 2003 16:27:38   bwf
 * Code cleanup.
 * Resolution for 1938: If no trans. occurred during date range, wrong error screen opens
 * 
 *    Rev 1.2   Jan 31 2003 16:19:12   bwf
 * Instead of checking if initialSearch is true, removed that and checked if start date of date range is after current date.  If it is then display info not found, otherwise history not found.
 * Resolution for 1938: If no trans. occurred during date range, wrong error screen opens
 * 
 *    Rev 1.1   Sep 25 2002 10:09:54   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:32:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:36   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:20   msg
 * Initial revision.
 * 
 *    Rev 1.4   19 Nov 2001 16:16:34   baa
 * customer & inquiry options cleanup
 * Resolution for POS SCR-300: Using a future date on Narrow Search returns History Not Found
 *
 *    Rev 1.3   16 Nov 2001 10:33:10   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   08 Nov 2001 10:38:04   baa
 * fix resource id for  historynotfound error
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.1   05 Nov 2001 17:36:58   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.3   30 Oct 2001 16:10:46   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.2   24 Oct 2001 17:06:12   baa
 * customer history feature. Allow for layaway transactions to display.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.1   23 Oct 2001 16:53:36   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.customer.history;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This site reads all the transactions associated with a customer.
 * 
 * @version $Revision: /main/15 $
 */
public class ReadCustomerHistorySite extends SiteActionAdapter
{
    private static final long serialVersionUID = -1919602269919988047L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReadCustomerHistorySite.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Constant for maximum number of matches default.
     */
    public static final int MAXIMUM_MATCHES_DEFAULT = 20;

    /**
     * Error text constasnt
     */
    public static final String NO_DATA_TEXT = "for this customer.";

    /**
     * This site reads all the transactions associated with a customer.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        String id = cargo.getCustomer().getCustomerID();

        String trainingMode = getTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();
        TransactionSummaryIfc[] summary = null;
        try
        {
            TransactionHistoryDataTransaction trans = null;
            
            trans = (TransactionHistoryDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_HISTORY_DATA_TRANSACTION);
            
            if (searchCriteria != null)
            {
                searchCriteria.setCustomer(cargo.getCustomer());
            }
            else
            {
                searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();
                searchCriteria.setCustomerID(id);
            }
            
            //set requested locales 
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);   
            searchCriteria.setLocaleRequestor(utility.getRequestLocales());         
            
            searchCriteria.setExclusionMode(false);
            searchCriteria.setStoreNumber(null);
            searchCriteria.setTrainingMode(trainingMode);
            searchCriteria.setMaximumMatches(getMaximumNumberOfTransactions(bus));

            summary  = trans.readTransactionHistory(searchCriteria);
            ArrayList<TransactionSummaryIfc> summaries = new ArrayList<TransactionSummaryIfc>(Arrays.asList(summary));
            Iterator<TransactionSummaryIfc> iter = summaries.iterator();
            // according to scr 6837 these transaction types should not be counted in
            // history and therefore we must check if they are there with reguards to
            // whether to display history screen or not.
            while(iter.hasNext())
            {
                TransactionSummaryIfc transSummary = iter.next();
                if(transSummary.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED ||
                   transSummary.getTransactionStatus() == TransactionIfc.STATUS_CANCELED ||
                   transSummary.getTransactionType() == TransactionIfc.TYPE_VOID)
                iter.remove();
            }
            summary = summaries.toArray(new TransactionSummaryIfc[summaries.size()]);

            if(summary == null ||
               summary.length == 0)
            {
                String resourceID = "HistoryNotFound";
                EYSDate[] range = searchCriteria.getDateRange();
                if(range != null && range[0].after(DomainGateway.getFactory().getEYSDateInstance()))
                {
                    resourceID = "INFO_NOT_FOUND_ERROR";                  
                }
             displayError(bus,resourceID, null,CommonLetterIfc.DONE);
            }
            else
            {
                storeTransactionsInCargo(cargo, summary, bus, searchCriteria);
            }

        }
        catch(DataException de)
        {
            logger.error(
                         "Can't find Transaction for CustomerIfcID = " + id + "");
            logger.error( "" + de + "");
            switch(de.getErrorCode())
            {
               case DataException.NO_DATA:
               {
                       String resourceID = "HistoryNotFound";
                       EYSDate[] range = searchCriteria.getDateRange();
                       if(range != null && range[0].after(DomainGateway.getFactory().getEYSDateInstance()))
                       {
                           resourceID = "INFO_NOT_FOUND_ERROR";                  
                       }
                    displayError(bus,resourceID, null,CommonLetterIfc.DONE);
                    break;
               }
               case DataException.RESULT_SET_SIZE:
               {
                   displayError(bus,"TooManyMatches", null,CommonLetterIfc.RETRY);
                    // eat the exception catch it later
                    //storeTransactionsInCargo(cargo, summary, bus, searchCriteria);
                    break;
               }
               default:
               {
                   cargo.setDataExceptionErrorCode(de.getErrorCode());
                   UtilityManagerIfc utility = 
                     (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                   displayError
                     (bus,"DATABASE_ERROR", utility.getErrorCodeString(de.getErrorCode()),CommonLetterIfc.DONE);
               }
            }
        }
    }

    /**
     * Puts the transactions into the cargo and determine the next step, i.e.
     * which letter to mail.
     * 
     * @return Letter the next action to take
     * @Param ReturnFindTransCargo location to put retrieved transactions
     * @Param TransactionIfc[] the array of transactions
     */
    protected void storeTransactionsInCargo(ReturnCustomerCargo cargo,
                                            TransactionSummaryIfc[] transactions,
                                            BusIfc bus, SearchCriteriaIfc criteria)
    {
        // Check to see if there are too many
        int maxTrans = getMaximumNumberOfTransactions(bus);
        boolean notSameDay = true;
        if (criteria.getDateRange() != null)
        {
          EYSDate[] range = criteria.getDateRange();
          if (range[0].equals(range[1]))
          {
             notSameDay = false;
          }
        }

        if ((transactions.length > maxTrans) && notSameDay)
        {
             displayError(bus,"TooManyMatches", null,CommonLetterIfc.RETRY);
        }
        else
        {
            cargo.setTransactionSummary(transactions);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }

    }

    /**
     * Convert training mode boolean flag to SQL format.
     * 
     * @Param boolean mode
     * @return String result '1' if true '0' otherwise
     */
    protected String getTrainingMode(boolean mode)
    {
       String result = "0";
       if (mode)
       {
          result = "1";
       }
       return (result);
    }

    /**
     * Gets the maximum number of transactions from the parameter manager.
     * 
     * @return int the maximum number of transactions
     * @Param BusIfc the current bus
     */
    static public int getMaximumNumberOfTransactions(BusIfc bus)
    {
        // look up Till Count Float parameter
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        Serializable[]  values = null;
        int                max = MAXIMUM_MATCHES_DEFAULT;

        try
        {
            values  = pm.getParameterValues(ParameterConstantsIfc.CUSTOMER_MaximumTransactionHistoryNumber);
            Long mL = new Long((String)values[0]);
            max     = mL.intValue();
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

    public void displayError(BusIfc bus, String resourceID, String argText, String letter)
    {
        // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // Set the correct argument
        String args[] = null;
        if (argText != null)
        {
           args = new String[1];
           args[0] = argText;
         }
        UIUtilities.setDialogModel(ui,DialogScreensIfc.ERROR,resourceID, args,letter);
    }
}