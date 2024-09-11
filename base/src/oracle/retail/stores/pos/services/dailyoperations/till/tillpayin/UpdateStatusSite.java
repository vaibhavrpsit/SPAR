/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpayin/UpdateStatusSite.java /main/18 2013/12/10 16:48:49 abananan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abanan 12/10/13 - TenderType(CASH) addded for Ejournal.
 *    cgreen 03/16/12 - split transaction-methods out of utilitymanager
 *    cgreen 03/09/12 - add support for journalling queues by current register
 *    abhayg 08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    acadar 04/08/10 - merge to tip
 *    acadar 04/06/10 - use default locale for currency, date and time display
 *    acadar 04/01/10 - use default locale for currency display
 *    abonda 01/03/10 - update header date
 *    deghos 12/02/08 - EJ i18n changes
 *    ohorne 10/31/08 - Localization of Till-related Reason Codes
 *
 * ===========================================================================

     $Log:
      4    360Commerce 1.3         5/18/2007 9:18:13 AM   Anda D. Cadar   EJ
           and currency UI changes
      3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
     $
     Revision 1.4.4.1  2004/10/15 18:50:29  kmcbride
     Merging in trunk changes that occurred during branching activity

     Revision 1.5  2004/10/07 19:41:17  bwf
     @scr 7320 Added reason code number printing to journal.

     Revision 1.4  2004/03/03 23:15:15  bwf
     @scr 0 Fixed CommonLetterIfc deprecations.

     Revision 1.3  2004/02/12 16:50:03  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:48:04  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Jan 21 2004 17:09:38   DCobb
 * Set journalEndOfTransaction=false in call to utility.saveTransaction() method.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 *
 *    Rev 1.0   Aug 29 2003 15:58:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:27:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:30:16   msg
 * Initial revision.
 *
 *    Rev 1.3   08 Feb 2002 14:48:38   epd
 * fixed ejournal messages
 * Resolution for POS SCR-688: Till Pay-in - ejournal entry incorrect - time
 * Resolution for POS SCR-691: Till pay in - ejournal entry is incorrect, cashier is repeated
 * Resolution for POS SCR-692:  Till pay in - ejournal entry is incorrect - broken line below 'TILL PAY-IN' is incorrect
 * Resolution for POS SCR-693: Till pay-in - ejournal entry is incorrect, 'Amount' should be deleted after 'Pay-In' and 'In' should not be capitalized
 *
 *    Rev 1.2   29 Jan 2002 09:54:22   epd
 * Deprecated all methods using accumulate parameter and added new methods without this parameter.  Also removed all reference to the parameter wherever used.
 * (The behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.1   21 Nov 2001 14:27:36   epd
 * 1)  Creating txn at start of flow
 * 2)  Added new security access
 * 3)  Added cancel transaction site
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.0   Sep 21 2001 11:19:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:50   msg
 * header update
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package oracle.retail.stores.pos.services.dailyoperations.till.tillpayin;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
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
 * @version $Revision: /main/18 $
 */
public class UpdateStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -3320160533119329385L;

    public static final String SITENAME = "UpdateStatusSite";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPayInCargo cargo = (TillPayInCargo)bus.getCargo();

        boolean saveTransSuccess = true;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        Letter letter = new Letter(CommonLetterIfc.SUCCESS);

        // Set updated information in transaction
        TillAdjustmentTransactionIfc transaction = cargo.getTransaction();
        transaction.setTimestampEnd();

        transaction.setAdjustmentAmount(cargo.getAmount());
        // each trip through the service only adjusts the financial totals data once
        transaction.setAdjustmentCount(1);
        transaction.setReason(cargo.getSelectedLocalizedReasonCode());
        transaction.setTransactionType(TillAdjustmentTransactionIfc.TYPE_PAYIN_TILL);

        FinancialTotalsIfc totals = transaction.getFinancialTotals();
        RegisterIfc register = cargo.getRegister();
        TillIfc till = register.getCurrentTill();

        till.addTotals(totals);
        register.addTotals(totals);

        // attempt to save the transaction
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        try
        {
            // Pass the transaction, a null totals object, the till,
            // register, and false indicating that the transaction has
            // not completed its journaling yet
            utility.saveTransaction(transaction, null, till, register, false);
            // set flag to reprint ID
            cargo.setLastReprintableTransactionID(transaction.getTransactionID());
        }
        catch (DataException e)
        {
            logger.error("Till payin error: \n" + e + "");
            if (e.getErrorCode() == DataException.QUEUE_FULL_ERROR
                    || e.getErrorCode() == DataException.STORAGE_SPACE_ERROR
                    || e.getErrorCode() == DataException.QUEUE_OP_FAILED)
            {
                saveTransSuccess = false;
                UtilityManagerIfc util = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                DialogBeanModel dialogModel = util.createErrorDialogBeanModel(e, false);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            else
            {
                // set error code
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                letter = new Letter(CommonLetterIfc.UPDATE_ERROR);
            }

        }
        if (saveTransSuccess)
        {
            // is this needed anywhere else in the service?
            cargo.setTransaction(transaction);

            if (letter.getName().equals(CommonLetterIfc.SUCCESS))
            {
                // get the Journal manager
                JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

                // journal the till status
                if (jmi != null)
                {
                    StringBuffer sb = new StringBuffer();
                    Object[] dataArgs = new Object[2];

                    // Journal the CashierID.
                    sb.append(Util.EOL);
                    sb.append(Util.EOL);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TILL_PAY_IN_LABEL, null));
                    sb.append(Util.EOL);
                    sb.append(Util.EOL);

                    dataArgs[0] = cargo.getRegister().getCurrentTillID();
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_ID_LABEL,
                            dataArgs));
                    sb.append(Util.EOL);

                    dataArgs[0] = cargo.getAmount().toFormattedString();
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PAY_IN_LABEL,
                            dataArgs));
                    sb.append(Util.EOL);

                    dataArgs[0] = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CASH);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TENDER_TYPE_LABEL, dataArgs));
                    sb.append(Util.EOL);

                    LocalizedCodeIfc selectedReasonCode = cargo.getSelectedLocalizedReasonCode();
                    Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

                    dataArgs[0] = selectedReasonCode.getText(lcl);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REASON_LABEL,
                            dataArgs));
                    sb.append(Util.EOL);

                    dataArgs[0] = selectedReasonCode.getCode();
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.REASON_NUMBER_LABEL, dataArgs));

                    jmi.journal(cargo.getOperator().getEmployeeID(), transaction.getTransactionID(), sb.toString());
                    utility.completeTransactionJournaling(transaction);
                }
                else
                {
                    logger.warn("No journal manager found.");
                }
            }

            bus.mail(letter, BusIfc.CURRENT);
        }
    }
}
