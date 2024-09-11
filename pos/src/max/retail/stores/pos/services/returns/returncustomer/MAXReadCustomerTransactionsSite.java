/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * Rev 1.0  11 Nov, 2016              Nadia              MAX-StoreCredi_Return requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.returns.returncustomer;

import java.io.Serializable;

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
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.NoTransactionsErrorSite;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;

/**
 * This site reads all the transactions associated with a customer.
 * 
 * @version $Revision: /main/17 $
 */
public class MAXReadCustomerTransactionsSite extends NoTransactionsErrorSite
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
                if(!Util.isEmpty(transactions[0].getInternalOrderID())
                		&& ! (transactions[0].getCustomerID() == null || transactions[0].getInternalOrderID().equalsIgnoreCase(transactions[0].getCustomerID())))
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
