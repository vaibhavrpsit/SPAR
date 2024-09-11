/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/RegisterJournal.java /main/13 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   03/16/12 - set register number before using journalmanager
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    asinton   01/21/11 - Fix for timezone issue on the EJournal.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  9    360Commerce 1.8         8/14/2007 2:42:32 PM   Owen D. Horne
 *       CR10731: initialization now sets Cashier and Sales Assoc. IDs
 *       appropriately for ManualEntryID parameter (User vs Employee)
 *  8    360Commerce 1.7         8/11/2007 7:47:49 AM   Dwight D. Jennings
 *       Revert last change
 *  7    360Commerce 1.6         8/10/2007 5:23:35 PM   Owen D. Horne
 *       initialization now sets Cashier and Sales Assoc. IDs appropriately
 *       for ManualEntryID parameter (User vs Employee)
 *  6    360Commerce 1.5         7/18/2007 8:43:35 AM   Alan N. Sinton  CR
 *       27651 - Made Post Void EJournal entries VAT compliant.
 *  5    360Commerce 1.4         6/4/2007 6:01:32 PM    Alan N. Sinton  CR
 *       26486 - Changes per review comments.
 *  4    360Commerce 1.3         5/8/2007 5:22:00 PM    Alan N. Sinton  CR
 *       26486 - Refactor of some EJournal code.
 *  3    360Commerce 1.2         3/31/2005 4:29:37 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:38 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:38 PM  Robert Pearse   
 *
 * Revision 1.7  2004/07/23 22:17:25  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.6  2004/07/16 01:11:55  jdeleau
 * @scr 5446 Correct the way phone numbers are sent to e-journal for
 * mail bank checks, remove the use of deprecated constants.
 *
 * Revision 1.5  2004/06/11 14:15:12  kll
 * @scr 5081: do not alter entryType IF value is ..._END
 *
 * Revision 1.4  2004/06/11 02:03:54  kll
 * @scr 5081: Correct journal entries entryType attribute from always assuming the value of starting a transaction
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:11:14 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:31:22 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import java.util.HashMap;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargoIfc;

/**
 *  RegisterJournal
 */
public class RegisterJournal implements RegisterJournalIfc
{
    /**
     * Map of formatters. Better to cache than constantly create new ones.
     */
    protected HashMap<JournalFamilyEnum, RegisterJournalFormatterIfc> formatterMap = new HashMap<JournalFamilyEnum, RegisterJournalFormatterIfc>(0);

    /**
     * Handle to the ParameterManager set by caller. 
     * @return
     */
    protected ParameterManagerIfc parameterManager;

    /**
     * Our configured Template
     */
    protected JournalTemplateIfc template;

    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.RegisterJournalIfc#journal(oracle.retail.stores.ado.journal.JournalableIfc,
     *      oracle.retail.stores.ado.journal.JournalFamilyEnum,
     *      oracle.retail.stores.ado.journal.JournalActionEnum)
     */
    @Override
    public void journal(
        JournalableADOIfc journalable,
        JournalFamilyEnum family,
        JournalActionEnum action)
    {
        // Get the appropriate formatter
        RegisterJournalFormatterIfc formatter = getFormatter(family);
        formatter.setParameterManager(parameterManager);
        // Use the formatter to format the journal entry
        String journalString =
            formatter.format(getTemplate(), journalable, action);

        // Check the border condition that we are creating a new transaction.
        // This border condition requires that the Journal Manager is
        // initialized
        // and that the journal entry is made in a certain fashion.
        if (family == JournalFamilyEnum.TRANSACTION
            && action == JournalActionEnum.CREATE)
        {
            // The journalable is a new transaction instance
            initializeJournalManager((RetailTransactionADOIfc) journalable);
        }

        JournalManagerIfc jm = getJournalManager();
        if (action != JournalActionEnum.CREATE 
            && jm.getEntryType() != JournalableIfc.ENTRY_TYPE_END)
        {
            jm.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);
        }
        jm.journal(journalString);
    }

    /**
     * Retrieves the formatter from the cache. If it doesn't exist yet, it is
     * created and cached.
     * 
     * @param family The key to determining which formatter to use
     * @return The formatter.
     */
    protected RegisterJournalFormatterIfc getFormatter(JournalFamilyEnum family)
    {
        // get the formatter from the map
        RegisterJournalFormatterIfc formatter = formatterMap.get(family);
        // if we don't have one yet, create it
        if (formatter == null)
        {
            if (family == JournalFamilyEnum.TRANSACTION)
            {
                formatter = new TransactionFormatter();
            }
            else if (family == JournalFamilyEnum.LINEITEM)
            {
                formatter = new LineItemFormatter();
            }
            else if (family == JournalFamilyEnum.TENDER)
            {
                formatter = new TenderFormatter();
            }
            // cache the formatter
            formatterMap.put(family, formatter);
        }
        return formatter;
    }

    protected void initializeJournalManager(RetailTransactionADOIfc txnADO)
    {
        JournalManagerIfc journalManager = getJournalManager();
        BusIfc bus = TourContext.getInstance().getTourBus();

        // index the previous transaction (if there is one)
        String sequenceNo = journalManager.getSequenceNumber();
        if (sequenceNo != null && sequenceNo != "")
        {
            TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
            utility.indexTransactionInJournal(sequenceNo);
        }

        // set storeID,registerID,cashierID,salesAssociateId for journal
        AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)bus.getCargo();
        journalManager.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
        journalManager.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
        TransactionIfc txnRDO = (TransactionIfc) ((ADO) txnADO).toLegacy();
        String userParm = null;
        try
        {
          UtilityIfc util = Utility.createInstance();
          userParm = util.getParameterValue(ParameterConstantsIfc.OPERATORID_ManualEntryID, ParameterConstantsIfc.OPERATORID_ManualEntryID_USER);       	
        }
        catch(Throwable e)  //catching throwable to handle Error thrown by unit tests using Mock Object framework
        {
          //In case of exception set userParm to USER_LOGIN by default
          userParm = ParameterConstantsIfc.OPERATORID_ManualEntryID_USER;
        }
       
        if (userParm.equalsIgnoreCase(ParameterConstantsIfc.OPERATORID_ManualEntryID_USER))
        {
          journalManager.setCashierID(txnRDO.getCashier().getLoginID());
          journalManager.setSalesAssociateID(txnRDO.getCashier().getLoginID());
        }
        else
        {
          journalManager.setCashierID(txnRDO.getCashier().getEmployeeID());
          journalManager.setSalesAssociateID(txnRDO.getCashier().getEmployeeID());
        }

        journalManager.setEntryType(JournalableIfc.ENTRY_TYPE_START);
        journalManager.setBusinessDate(cargo.getStoreStatus().getBusinessDate());
        journalManager.setSequenceNumber(txnRDO.getTransactionID());
    }

    /**
     * Get and initialize the JournalManager.
     *
     * @return
     */
    protected JournalManagerIfc getJournalManager()
    {
        BusIfc bus = TourContext.getInstance().getTourBus();
        JournalManagerIfc journalManager = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (bus.getCargo() instanceof AbstractFinancialCargoIfc)
        {
            AbstractFinancialCargoIfc cargo = (AbstractFinancialCargoIfc)bus.getCargo();
            journalManager.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());
        }
        return journalManager;
    }

    /**
     * Simple method to generate new and cache journal template.
     * 
     * @return a JournalTemplateIfc instance.
     */
    protected JournalTemplateIfc getTemplate()
    {
        if (template == null)
        {
            template = new JournalTemplate();
        }
        return template;
    }

    /**
     * Sets the ParmaeterManager instance.
     * @param pm
     */
    public void setParameterManager(ParameterManagerIfc pm)
    {
        parameterManager = pm;
    }
}
