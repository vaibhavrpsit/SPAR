/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/history/ReadCustomerTransactionsAisle.java /main/13 2011/02/16 09:13:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    blarsen   07/09/09 - XbranchMerge
 *                         blarsen_bug8629786-transaction-lookup-by-card-number-fails
 *                         from rgbustores_13.1x_branch
 *    blarsen   07/07/09 - Removing previous change that includes store number
 *                         in search criteria. This limits the scope in cases
 *                         where it should not. CO now uses default hierarchy
 *                         when no store criteria us specified.
 *    blarsen   07/01/09 - XbranchMerge
 *                         blarsen_bug8629786-customer-transaction-history-retrieval-fails-on-was
 *                         from main
 *    blarsen   06/29/09 - Adding the current store id to the search criteria.
 *                         Some early assumtions in this feature were not
 *                         valid. The assumption that the creditionals sent to
 *                         CO is associated with a store hierarchy is not valid
 *                         for WAS. In this case no creditials are sent. CO was
 *                         modified to *not* filter on logged in user. In some
 *                         cases a store criteria is required when a users
 *                         hierarchy is not available. Requirements at this
 *                         time are to only lookup current stores transactions.
 *    sgu       04/08/09 - localize store location name when retrieving
 *                         transaction header centrally.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:33 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:31 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:32 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/03 14:47:43  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:11:00  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.4  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.3  2004/02/12 16:49:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:44:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:55:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 03 2003 16:40:46   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Sep 25 2002 10:18:56   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:32:46   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Mar 2002 15:04:52   baa
 * use TooManyMatches instead of MaxMatches dialog
 * Resolution for POS SCR-26: Cancel Confirm screen looks different if cust linked (Linux)
 *
 *    Rev 1.1   Mar 18 2002 23:12:38   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:25:20   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 10 2002 10:12:06   mpm
 * Removed unnecessary reference to StatusBeanModel.
 * Resolution for POS SCR-351: Internationalization
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
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.history;

import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.TransactionHistoryDataTransaction;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site reads all the transactions associated with a customer.
 * 
 * @version $Revision: /main/13 $
 */
public class ReadCustomerTransactionsAisle extends LaneActionAdapter
{
    private static final long serialVersionUID = 7861857656260625730L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(ReadCustomerTransactionsAisle.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     * Error text constants
     */
    public static final String NO_DATA_TEXT = "for this customer.";

    /**
     * This site reads all the transactions associated with a customer.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        String id = cargo.getCustomer().getCustomerID();

        String trainingMode = getTrainingMode(cargo.getRegister().getWorkstation().isTrainingMode());
        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        try
        {
            TransactionHistoryDataTransaction trans = null;

            trans = (TransactionHistoryDataTransaction) DataTransactionFactory.create(DataTransactionKeys.TRANSACTION_HISTORY_DATA_TRANSACTION);

            TransactionSummaryIfc[] summary = null;
            if (searchCriteria != null)
            {
                searchCriteria.setCustomer(cargo.getCustomer());
            }
            else
            {
                searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();
                searchCriteria.setCustomerID(id);
            }

            searchCriteria.setExclusionMode(false);
            searchCriteria.setStoreNumber(null);
            searchCriteria.setTrainingMode(trainingMode);
            searchCriteria.setLocaleRequestor(utility.getRequestLocales());

            summary  = trans.readTransactionHistory(searchCriteria);
            storeTransactionsInCargo(cargo, summary, bus, searchCriteria);

        }
        catch(DataException de)
        {
            logger.error(
                         "Can't find Transaction for CustomerIfcID = " + id + "");
            logger.error( "" + de + "");
            if (de.getErrorCode() == DataException.NO_DATA)
            {
                displayError(bus,"HISTORY_NOT_FOUND_ERROR", null,"Done");
            }
            else
            {
                cargo.setDataExceptionErrorCode(de.getErrorCode());
                displayError
                  (bus, "DATABASE_ERROR", utility.getErrorCodeString(de.getErrorCode()), "Done");
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
             displayError(bus,"TooManyMatches", null,"Retry");
        }
        else
        {
            cargo.setTransactionSummary(transactions);
            cargo.displayCustomer(bus);
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }

    /**
     * Convert training mode boolean flag to SQL format.
     * 
     * @Param boolean mode
     * @return String result '1' if true '0' otherwise
     **/
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
        int                max = ParameterConstantsIfc.MAXIMUM_MATCHES_DEFAULT;

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

    public void displayError(BusIfc bus, String ResourceID, String argText, String letter)
    {
                // Get the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(ResourceID);
        dialogModel.setType(DialogScreensIfc.ERROR);

        // Set the correct argument
        if (argText != null)
        {
           String args[] = new String[1];
           args[0] = argText;
           dialogModel.setArgs(args);
        }
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
        // display the screen
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

}