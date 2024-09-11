/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillloan/UpdateStatusSite.java /main/20 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    nkgautam  12/01/10 - forward port : training mode check added before
 *                         updating Store safe totals
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    nganesh   03/20/09 - Modified EJournal for denomination
 *                         internationalization refactoring
 *    deghosh   02/02/09 - EJ i18n defect fixes
 *    deghosh   12/15/08 - EJ i18n journal text format changes
 *    deghosh   12/02/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *          issues with tills and CurrencyID.
 *    4    360Commerce 1.3         5/18/2007 9:18:12 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *
 *   Revision 1.8  2004/07/30 21:18:12  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *
 *   Revision 1.7  2004/06/03 14:47:46  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:10  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:11  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:16  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:59  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:54  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 28 2004 17:34:16   DCobb
 * Added Pickup and Loan parameters.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.1   Jan 21 2004 16:54:36   DCobb
 * Set journalEndOfTransaction=false in call to utility.saveTransaction() method.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 *
 *    Rev 1.0   Aug 29 2003 15:57:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:28:16   msg
 * Initial revision.
 *
 *    Rev 1.1   25 Mar 2002 12:33:44   epd
 * Jose asked me to check these in.  Updates to use TenderDescriptor
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.5   02 Mar 2002 12:47:52   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.4   29 Jan 2002 09:54:20   epd
 * Deprecated all methods using accumulate parameter and added new methods without this parameter.  Also removed all reference to the parameter wherever used.
 * (The behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.3   04 Dec 2001 12:42:00   epd
 * Added code to update store safe
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   21 Nov 2001 14:27:32   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   29 Oct 2001 16:15:00   epd
 * Updated files to remove reference to Till related parameters.  This information, formerly contained in parameters, now resides as register settings obtained from the RegisterIfc class.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:18:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillloan;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreSafeWriteDataTransaction;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Updates till status.
 * 
 * @version $Revision: /main/20 $
 */
@SuppressWarnings("serial")
public class UpdateStatusSite extends PosSiteActionAdapter
{
    public static final String SITENAME = "UpdateStatusSite";

    /**
     * Updates till status.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        boolean saveTransSuccess = true;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        TillLoanCargo cargo = (TillLoanCargo)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.SUCCESS);
        // Local copy of register. Update if everything ok
        RegisterIfc register = (RegisterIfc)cargo.getRegister().clone();
        TillIfc till = register.getTillByID(cargo.getTillID());
        // Temporary till in cargo has the updated financial totals
        // Need to copy them to the actual register's till.
        TillIfc t = cargo.getTill();
        // add loan to total
        t.getTotals().addCountTillLoans(1);
        // setTotals
        till.addTotals(t.getTotals());
        // Set register in cargo
        cargo.setRegister(register);
        // Create the Till Loan transaction
        TillAdjustmentTransactionIfc transaction = cargo.getTransaction();
        transaction.setTimestampEnd();
        String tender = DomainGateway.getFactory().getTenderTypeMapInstance()
                .getDescriptor(TenderLineItemIfc.TENDER_TYPE_CASH);
        transaction.setTenderType(tender);

        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setTenderType(TenderLineItemIfc.TENDER_TYPE_CASH);
        td.setCountryCode(DomainGateway.getBaseCurrencyInstance().getCountryCode());
        td.setCurrencyID(DomainGateway.getBaseCurrencyType().getCurrencyId());
        transaction.setAdjustmentAmount(t.getAmountTotal(td));
        transaction.setAdjustmentCount(1); // always 1
        transaction.setCountType(cargo.getLoanCountType());
        StoreSafeIfc safe = DomainGateway.getFactory().getStoreSafeInstance();
        safe.setBusinessDay(cargo.getStoreStatus().getBusinessDate());
        safe.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
        safe.setValidTenderDescList(cargo.getStoreStatus().getSafeTenderTypeDescList());

        if (cargo.getLoanCountType() != FinancialCountIfc.COUNT_TYPE_NONE)
        {
            FinancialTotalsIfc loanTotals = cargo.getLoanTotals();
            ReconcilableCountIfc[] tillLoans = loanTotals.getTillLoans();
            FinancialCountIfc fc = tillLoans[tillLoans.length - 1].getEntered();
            transaction.setTenderCount(fc);
            // update safe
            safe.addLoanCount(fc);
        }

        cargo.setTransaction(transaction);

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        // save the transaction
        try
        {
            // Save this transaction seperately since it doesn't determine
            // the success or failure of closing the till
            //
            // Pass the transaction, a null totals object, the till,
            // register, and false indicating that the transaction has
            // not completed its journaling yet.
            utility.saveTransaction(transaction, null, till, register, false);
            // set flag to reprint ID
            cargo.setLastReprintableTransactionID(transaction.getTransactionID());
            // update safe totals in database

            StoreSafeWriteDataTransaction safeTransaction = null;

            safeTransaction = (StoreSafeWriteDataTransaction)DataTransactionFactory
                    .create(DataTransactionKeys.STORE_SAFE_WRITE_DATA_TRANSACTION);

            if (!transaction.isTrainingMode())
            {
                safeTransaction.updateStoreSafeTotals(safe);
            }
        }
        catch (DataException e)
        {
            logger.error("" + e + "");
            if (e.getErrorCode() == DataException.QUEUE_FULL_ERROR
                    || e.getErrorCode() == DataException.STORAGE_SPACE_ERROR
                    || e.getErrorCode() == DataException.QUEUE_OP_FAILED)
            {
                saveTransSuccess = false;
                UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                DialogBeanModel dialogModel = util.createErrorDialogBeanModel(e, false);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else
            {
                letter = new Letter(CommonLetterIfc.UPDATE_ERROR);
            }
        }

        // journal
        if (saveTransSuccess && letter.getName().equals(CommonLetterIfc.SUCCESS))
        {
            String countTillLoan = FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[register.getTillCountCashLoan()];

            if (countTillLoan == null || countTillLoan.length() == 0)
            {
                // set to default as defined in requirements
                countTillLoan = FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[FinancialCountIfc.COUNT_TYPE_SUMMARY];
            }

            // get the Journal manager
            JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
            StringBuffer sb = new StringBuffer();

            if (jmi != null)
            {
                // journal loan detail here.
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LOAN_COUNT_LABEL,
                        null));
                sb.append(Util.EOL);
                sb.append(Util.EOL);
                sb.append(Util.EOL);
                Object[] dataArgs = new Object[2];
                dataArgs[0] = cargo.getTillID();
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_ID_LABEL,
                        dataArgs));
                sb.append(Util.EOL);
                sb.append(Util.EOL);
            }
            else
            {
                logger.warn("No journal manager found.");
            }

            if (!countTillLoan.equals(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[FinancialCountIfc.COUNT_TYPE_NONE]))
            {
                FinancialTotalsIfc loanTotals = cargo.getLoanTotals();
                ReconcilableCountIfc[] tillLoans = loanTotals.getTillLoans();
                FinancialCountIfc fc = tillLoans[tillLoans.length - 1].getEntered();
                FinancialCountTenderItemIfc[] fcti = fc.getTenderItems();

                // journal the till status
                if (jmi != null)
                {
                    if (countTillLoan
                            .equals(FinancialCountIfc.COUNT_TYPE_DESCRIPTORS[FinancialCountIfc.COUNT_TYPE_DETAIL]))
                    {
                        for (int i = 0; i < fcti.length; i++)
                        {
                            if (fcti[i].isSummary() == false)
                            {
                                Object[] dataArgs = new Object[2];
                                String desc = fcti[i].getDescription();
                                int num = fcti[i].getNumberItemsIn();
                                String i18nDesc = fcti[i].getTenderDescriptor().getDenomination()
                                        .getDenominationDisplayName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                                dataArgs = new Object[] { i18nDesc, num };
                                String trCurrDesc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                        JournalConstantsIfc.TILL_CURR_DETAIL, dataArgs);
                                sb.append(trCurrDesc);
                                sb.append(Util.EOL);
                            }
                        }
                        sb.append(Util.EOL);
                    }
                    // use default locale for currency display
                    Object[] dataArgs = { fc.getAmount().toFormattedString() };
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TOTAL_LOAN_LABEL, dataArgs));
                }
            }

            String fromRegister = transaction.getFromRegister();
            if (!Util.isEmpty(fromRegister))
            {
                sb.append(Util.EOL);
                Object[] dataArgs = new Object[2];
                dataArgs[0] = fromRegister;
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.FROM_REGISTER_LABEL,
                        dataArgs));
            }

            jmi.journal(cargo.getOperator().getEmployeeID(), transaction.getTransactionID(), sb.toString());
            utility.completeTransactionJournaling( transaction);
        }
        if (saveTransSuccess)
        {
            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
