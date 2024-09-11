/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/postvoid/CreateVoidTransactionSite.java /main/17 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    ohorne 10/11/11 - fixed abnormal (pre-mature) completion of ej in
 *                      re-entrymode
 *    cgreen 05/26/10 - convert to oracle packaging
 *    sbeesn 01/28/10 - Remove the flow that prompts the user to swipe debit
 *                      card & pin as they are not required in void
 *                      transactions.
 *    sbeesn 01/22/10 - Reverting the changes made to remove flow which prompts
 *                      the user to swipe debit card & pin as they resulted in
 *                      JUnit failure.
 *    sbeesn 01/20/10 - Removed flow which prompts the user to swipe debit card
 *                      & pin as they are not required in void transaction.
 *    abonda 01/03/10 - update header date
 *    vikini 03/11/09 - Checking for Re-Entry and Updating the transaction
 *                      Reentry status
 *    acadar 10/24/08 - localization of post void reason codes
 * ===========================================================================
     $Log:
      6    360Commerce 1.5         7/18/2007 8:43:35 AM   Alan N. Sinton  CR
           27651 - Made Post Void EJournal entries VAT compliant.
      5    360Commerce 1.4         1/25/2006 4:10:54 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      4    360Commerce 1.3         12/13/2005 4:42:34 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:20:26 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse
     $: CreateVoidTransactionSite.java,v $
      6    .v710     1.2.2.0     9/21/2005 13:39:21     Brendan W. Farrell
           Initial Check in merge 67.
      5    .v700     1.2.3.1     11/30/2005 17:22:52    Deepanshu       CR
           6261: Added Postvoid transaction reason code
      4    .v700     1.2.3.0     10/31/2005 18:02:13    Deepanshu       CR
           6102: Updated code to save the tax information in EJournal when a
           Returned transaction is post void transaction
      3    360Commerce1.2         3/31/2005 15:27:32     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:20:26     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:10:14     Robert Pearse
     $
     Revision 1.7.2.1  2004/11/12 14:28:53  kll
     @scr 7337: JournalFactory extensibility initiative

     Revision 1.7  2004/09/30 18:21:49  lzhao
     @scr add orignal transaction type and id in journal message of void transaction

     Revision 1.6  2004/07/23 22:17:26  epd
     @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode

     Revision 1.5  2004/04/29 17:30:00  bwf
     @scr 3377 Debit Reversal Work

     Revision 1.4  2004/03/08 23:35:43  blj
     @scr 0 - no changes

     Revision 1.3  2004/02/12 16:48:15  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:28:20  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Nov 19 2003 13:57:26   bwf
 * Moved some logic to RequiredOpenDrawerSite.
 *
 *    Rev 1.0   Nov 04 2003 11:16:00   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 23 2003 17:28:30   epd
 * Initial revision.
 *
 *    Rev 1.0   Oct 17 2003 13:03:18   epd
 * Initial revision.

* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.postvoid;

// java imports
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.journal.JournalableIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.journal.JournalActionEnum;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalFamilyEnum;
import oracle.retail.stores.pos.ado.journal.RegisterJournalIfc;
import oracle.retail.stores.pos.ado.store.RegisterADO;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.pos.ado.utility.AuthorizationException;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

//--------------------------------------------------------------------------
/**
    This site creates a new void transaction.
    @version $Revision: /main/17 $
**/
//--------------------------------------------------------------------------
public class CreateVoidTransactionSite extends PosSiteActionAdapter
{

    /**
     * Creates a new void transaction.
     * @param  bus     Service Bus
    */
    public void arrive(BusIfc bus)
    {
        VoidCargo cargo = (VoidCargo)bus.getCargo();
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        // create and set data on void ADO transaction
        VoidTransactionADO voidTxn = createVoidTranasction(cargo, pm);
        voidTxn.setOriginalTransaction(cargo.getOriginalTransactionADO());
        // set on cargo
        cargo.setCurrentTransactionADO(voidTxn);

        if(cargo.getRegister().getWorkstation().isTransReentryMode())
        {
          JournalManagerIfc journal =(JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
          String trnReEntry= I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_REENTRY_LABEL, null);
          journal.setEntryType(JournalableIfc.ENTRY_TYPE_TRANS);  //indicate entry is part of ongoing journal transaction
          journal.journal(trnReEntry);
        }

        // journal reason code
        JournalFactoryIfc jrnlFact = null;
        try
        {
            jrnlFact = JournalFactory.getInstance();
        }
        catch (ADOException e)
        {
            logger.error(JournalFactoryIfc.INSTANTIATION_ERROR, e);
            throw new RuntimeException(JournalFactoryIfc.INSTANTIATION_ERROR, e);
        }

        RegisterJournalIfc registerJournal = jrnlFact.getRegisterJournal();

        registerJournal.journal(voidTxn,
                                JournalFamilyEnum.TRANSACTION,
                                JournalActionEnum.VOID_REASON_CODE);
        registerJournal.journal(voidTxn,
                                JournalFamilyEnum.TRANSACTION,
                                JournalActionEnum.ORIG_TRANS);
        registerJournal.journal(voidTxn,
                				JournalFamilyEnum.LINEITEM,
                				JournalActionEnum.VOID);
        registerJournal.journal(voidTxn,
                                JournalFamilyEnum.TRANSACTION,
                                JournalActionEnum.ORIG_TOTAL);

        // process the voided transaction (reverse tenders, etc)
        try
        {
            voidTxn.process();
        }
        catch (AuthorizationException e)
        {
            // TODO: Change flow for call center screen if authorization failed
        }


        if(cargo.getRegister().getWorkstation().isTransReentryMode())
        {
          VoidTransactionIfc txn = (VoidTransactionIfc)voidTxn.toLegacy();
          if(txn != null)
          {
            txn.setReentryMode(true);
          }
        }

        String letterName = "Success";

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    /**
     * Creates the void ADO transaction
     * @param cargo
     * @param pm
     * @return
     */
    protected VoidTransactionADO createVoidTranasction(VoidCargo cargo, ParameterManagerIfc pm)
    {
        // create new void transaction

        RegisterADO registerADO = ContextFactory.getInstance()
                                                .getContext()
                                                .getRegisterADO();
        registerADO.setParameterManager(pm);
        VoidTransactionADO voidTxn = (VoidTransactionADO)registerADO
                .createTransaction(TransactionPrototypeEnum.VOID,
                                   cargo.getCustomerInfo(),
                                   cargo.getOperator());
        voidTxn.setLocalizedReasonCode(cargo.getLocalizedReasonCode());
        return voidTxn;
    }
}
