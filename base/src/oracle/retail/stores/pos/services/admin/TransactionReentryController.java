/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/TransactionReentryController.java /main/8 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    deghosh   12/23/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         11/22/2007 10:57:22 PM Naveen Ganesh
 * $
 * Revision 1.4.2.1  2004/10/15 18:50:27  kmcbride
 * Merging in trunk changes that occurred during branching activity
 *
 * Revision 1.5  2004/10/07 16:24:11  bwf
 * @scr 7321, 7323 Moved the journal to above the save transaction call which ends the journal entry.
 *
 * Revision 1.4  2004/09/22 19:11:17  kll
 * @scr 7236: save transaction reentry as a control transaction
 *
 * Revision 1.3  2004/04/01 16:04:10  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.2  2004/03/26 21:18:20  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.1  2004/03/24 20:09:55  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.1  2004/03/21 19:24:47  bjosserand
 * @scr 4093 Transaction Reentry
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

/**
 * @author bjosserand
 *
 * This class handles miscellaneous tasks associated with turning on and off Transaction Reentry mode
 */
public class TransactionReentryController implements TransactionReentryControllerIfc
{
    public static final String JOURNAL_TEXT = "Transaction Re-entry Mode";
    protected WorkstationIfc workstation;
    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.main.TransactionReentryController.class);


    /**
     * Create a journal entry for turning transaction reentry on or off.
     *
     * @param bus
     * @param reentryMode
     * @param operator
     */
    public void journalTransaction(BusIfc bus, boolean reentryMode, EmployeeIfc operator)
    {
        String journalString = null;

        /////////////////////////////////////////////////////////////////////////////////////
        // create a transaction
        TransactionIfc transaction = DomainGateway.getFactory().getTransactionInstance();

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        utility.initializeTransaction(transaction);

        // set up the transaction
        transaction.setTimestampEnd();

        if (reentryMode)
        {
            transaction.setTransactionType(TransactionConstantsIfc.TYPE_ENTER_TRANSACTION_REENTRY);
            journalString=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.TRANSACTION_REENTRY_ENTERING, null);
        }
        else
        {
            transaction.setTransactionType(TransactionConstantsIfc.TYPE_EXIT_TRANSACTION_REENTRY);
            journalString=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.TRANSACTION_REENTRY_EXITING, null);
        }

        transaction.setCashier(operator);

        // journal the transaction
        // get the journal manager
        JournalManagerIfc journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);

        // construct a string buffer for the journal entry
        StringBuffer sb =
            new StringBuffer().append(Util.EOL).append(journalString).append(Util.EOL);

        // write the entry to the journal
        journal.journal(transaction.getCashier().getLoginID(), transaction.getTransactionID(), sb.toString());

        try
        {
            utility.saveTransaction(transaction);
        }
        catch(DataException de)
        {
            de.printStackTrace();
            logger.error("Error saving Transaction Reentry: " + de);
        }

    }
}
