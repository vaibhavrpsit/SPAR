/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncustomer/ReadCustomerTransactionsSite.java /main/17 2012/10/29 12:55:23 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    jswan  10/25/12 - Modified to support returns by order.
 *    cgreen 02/15/11 - move constants into interfaces and refactor
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    ohorne 09/18/09 - XbranchMerge ohorne_bug-8913676 from
 *                      rgbustores_13.1x_branch
 *    ohorne 09/17/09 - now displays 'Max # of Possible Matches Notice' instead
 *                      of db error when number of customer transactions exceed
 *                      limit
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
 *                      Some early assumptions in this feature were not valid.
 *                      The assumption that the credentials sent to CO is
 *                      associated with a store hierarchy is not valid for WAS.
 *                      In this case no credentials are sent. CO was modified to
 *                      *not* filter on logged in user. In some cases a store
 *                      criteria is required when a users hierarchy is not
 *                      available. Requirements at this time are to only lookup
 *                      current stores transactions.
 *    mchell 04/09/09 - Backed out the last change, moved the filtering
 *                      functionality to jdbc class
 *    mchell 03/31/09 - Filtering canceled,void and suspended transactions
 *                      from customer history
 *
 * ===========================================================================

     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse
     $
     Revision 1.8  2004/06/03 14:47:45  epd
     @scr 5368 Update to use of DataTransactionFactory

     Revision 1.7  2004/04/20 13:17:06  tmorris
     @scr 4332 -Sorted imports

     Revision 1.6  2004/04/14 15:17:10  pkillick
     @scr 4332 -Replaced direct instantiation(new) with Factory call.

     Revision 1.5  2004/03/10 14:16:46  baa
     @scr 0 fix javadoc warnings

     Revision 1.4  2004/03/03 23:15:16  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:51:47  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:52:29  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:05:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Feb 24 2003 13:28:42   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.1   Aug 14 2002 14:14:52   jriggins
 * Switched call from displayNoTransactions() to displayNoTransactionsForCustomer()
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:06:16   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 10 2002 18:01:16   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.0   Sep 21 2001 11:24:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:34   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.returns.returncustomer;

import java.io.Serializable;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.returns.returncommon.NoTransactionsErrorSite;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;

/**
 * This site reads all the transactions associated with a customer.
 * 
 * @version $Revision: /main/17 $
 */
public class ReadCustomerTransactionsSite extends NoTransactionsErrorSite
{

    private static final long serialVersionUID = -8977207255449202424L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
     * Letter name constant
     */
    public static final String MULTIPLE_TRANS = "MultipleTrans";

    /**
     * Letter name constant
     */
    public static final String ONE_TRANS = "OneTrans";

    /**
     * Error text constant
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

        boolean noData = false;
        Letter letter  = null;
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        String id = cargo.getCustomer().getCustomerID();
        String trainingMode = getTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());

        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();
        if (searchCriteria == null)
        {
            // Create a new object search criteria and set store number and date range
            searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();
            searchCriteria.setStoreNumber(null);
            searchCriteria.setDateRange(null);
        }
        searchCriteria.setTrainingMode(trainingMode);
        searchCriteria.setCustomer(cargo.getCustomer());

        //set requested locales
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        searchCriteria.setLocaleRequestor(utility.getRequestLocales());

        try
        {
            TransactionHistoryDataTransaction trans =
                (TransactionHistoryDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_HISTORY_DATA_TRANSACTION);

            TransactionSummaryIfc[] summary = trans.readTransactionHistory(searchCriteria);

            letter = storeTransactionsInCargo(cargo, summary, bus);
        }
        catch(DataException de)
        {
            logger.error("Can't find Transaction for CustomerIfcID = " + id + "");
            logger.error( "" + de + "");
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                noData = true;
            }
            else if (de.getErrorCode() == DataException.RESULT_SET_SIZE)
            {
                letter = new Letter(CommonLetterIfc.TOO_MANY);
            }
            else
            {
                cargo.setDataExceptionErrorCode(de.getErrorCode());
                letter = new Letter(CommonLetterIfc.DB_ERROR);
            }
        }

        if (noData)
        {
            displayNoTransactionsForCustomer(bus);
        }
        else
        {
            bus.mail(letter, BusIfc.CURRENT);
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
    protected Letter storeTransactionsInCargo(ReturnCustomerCargo cargo,
                                              TransactionSummaryIfc[] transactions,
                                              BusIfc bus)
    {
        // Initialize the letter
        Letter letter = new Letter(ONE_TRANS);

        // Check to see if there are too many
        int maxTrans = getMaximumNumberOfTransactions(bus);
        if (transactions.length > maxTrans)
        {
            letter = new Letter(CommonLetterIfc.TOO_MANY);
        }
        else
        {
            cargo.setTransactionSummary(transactions);
            if (transactions.length > 1)
            {
                letter = new Letter(MULTIPLE_TRANS);
            }
            else
            {
                // The default letter is one transaction; by default
                // the selected index should be the first element.
                cargo.setSelectedIndex(0);
                if(!Util.isEmpty(transactions[0].getInternalOrderID()))
                {
                    letter = new Letter(ReturnUtilities.TRANSACTION_HAS_ORDER);
                    cargo.setSelectedTransactionOrderID(transactions[0].getInternalOrderID());
                }
            }
            cargo.displayCustomer(bus);
        }

        return(letter);

    }

    /**
     * Convert training mode boolean flag to sql format.
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
     * @param bus the current bus
     */
    static public int getMaximumNumberOfTransactions(BusIfc bus)
    {
        // look up Till Count Float parameter
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        Serializable[]  values = null;
        int                max = ParameterConstantsIfc.MAXIMUM_MATCHES_DEFAULT;

        try
        {
            values  = pm.getParameterValues(ParameterConstantsIfc.RETURN_ReturnMaximumMatches);
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

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}
