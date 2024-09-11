/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/UpdateStatusSite.java /main/20 2014/06/25 15:23:30 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/24/14 - Forward port fix to prevent 2 or more online
 *                         registers from opening the same till when done
 *                         simultaneously. In addition rearranged the code to
 *                         shorten the arrive method.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nganesh   03/26/09 - Modified code to handle denomination
 *                         internationalization for Auditlog and EJournal
 *    nganesh   03/20/09 - Internationalized EJ for Denomination
 *                         internationalization refactoring
 *    deghosh   02/02/09 - EJ i18n defect fixes
 *    deghosh   11/25/08 - EJ I18n
 *
 * ===========================================================================
 * $Log:
 *    10   360Commerce 1.9         2/29/2008 5:28:13 AM   Chengegowda Venkatesh
 *          fix for CR : 30345
 *    9    360Commerce 1.8         1/10/2008 7:54:21 AM   Manas Sahu      Event
 *          Originator Changes
 *    8    360Commerce 1.7         1/7/2008 8:32:24 AM    Chengegowda Venkatesh
 *          PABP FR40 : Changes for AuditLog incorporation
 *    7    360Commerce 1.6         8/13/2007 3:01:32 PM   Charles D. Baker CR
 *         27803 - Remove unused domain property.
 *    6    360Commerce 1.5         7/10/2007 4:51:51 PM   Charles D. Baker CR
 *         27506 - Updated to remove old fix for truncating extra decimal
 *         places that are used for accuracy. Truncating is no longer
 *         required.
 *    5    360Commerce 1.4         5/18/2007 9:18:13 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    4    360Commerce 1.3         4/25/2007 8:52:29 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/07 18:27:11  dcobb
 *   @scr 1734 Wrong error message when attempt to open another till in reg acct.
 *   Fixed in CheckTillStatusSite. Moved deprecated TillOpenCargo to the deprecation tree and imported new TillCargo from _360commerce tree..
 *
 *   Revision 1.4  2004/03/03 23:15:09  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:00  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:58:04   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jan 10 2003 13:36:34   sfl
 * Shorted the printed float amount to be two digits after
 * decimal point.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.3   Dec 20 2002 11:32:06   DCobb
 * Add floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 *
 *    Rev 1.2   Sep 03 2002 16:03:44   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   May 13 2002 19:36:06   mpm
 * Added support for till open/close and register open/close transactions.
 * Resolution for POS SCR-1630: Make changes to support TLog facility.
 *
 *    Rev 1.0   Apr 29 2002 15:27:54   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillopen;

import java.util.Hashtable;
import java.util.Locale;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.TillOpenEvent;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;

/**
 * Updates the existing till status and totals or inserts the till status and
 * totals.
 * 
 * @version $Revision: /main/20 $
 */

/**
 * @author icole
 *
 */
public class UpdateStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 4999363114232915964L;
    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$Revision: /main/20 $";
    
    /**
     * Originating class and method.
     */
    protected static final String originator = "LookupStoreStatusSite.arrive";
    /**
     * Attempts to update the existing till status and till totals. If this
     * fails, then inserts the till status and till totals.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillOpenCargo cargo = (TillOpenCargo) bus.getCargo();
        boolean saveTransSuccess = true;

        // Local copy of register. Update if everything ok
        RegisterIfc register = (RegisterIfc) cargo.getRegister().clone();
        TillIfc till = cargo.getTill();
        String letterName = CommonLetterIfc.SUCCESS;

        try
        {
            FinancialTotalsDataTransaction dt = null;
            dt = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
            TillIfc dbTill = dt.readTillStatus(register.getWorkstation().getStore(), till.getTillID());
            if (dbTill.getBusinessDate().dateValue().equals(register.getBusinessDate().dateValue()))
            {
                letterName = CommonLetterIfc.TILL_OPEN_ERROR;
            }
        }
        catch (DataException e)
        {
            if (e.getErrorCode() != DataException.NO_DATA)
            {
                logger.warn("Error reading till information");
            }
            else
            {
                logger.warn("No till found");
            }
        }
        if (letterName != CommonLetterIfc.TILL_OPEN_ERROR)
        {
            // Set till status to OPEN
            till.setStatus(AbstractFinancialEntityIfc.STATUS_OPEN);
            till.setDrawerID("");
            till.setBusinessDate(register.getBusinessDate());
            till.setOpenTime();
            till.setSignOnOperator(cargo.getOperator());
            till.setRegisterAccountability(register.getAccountability());
            // set drawer status as occupied,  set register till and asume one drawer status.
            register.setCurrentTillID(cargo.getTillID());
            register.getDrawer(DrawerIfc.DRAWER_PRIMARY).setDrawerStatus(
                    AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED, cargo.getTillID());
            // Add opened till to register
            register.addTill(till);
            // Set register in cargo
            cargo.setRegister(register);
            // create till open transaction
            TillOpenCloseTransactionIfc transaction = createTillOpenTransaction(bus, cargo, till, register);
            journalTillOpen(bus, cargo, transaction, register);
            // Returned letter name of false indicates that the save failed and that a dialog has been shown and no letter is to be mailed.
            letterName = saveTillOpenTransaction(bus, cargo, transaction);
            if (letterName == CommonLetterIfc.FAILURE)
            {
                saveTransSuccess = false;
            }
        }
        if (saveTransSuccess)
        {
        	bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }  // end arrive

    /**
     * Audit log the opening of the till.
     * 
     * @param transaction
     * @param cargo
     * @param success
     * 
     * @since 14.1
     */
    protected void auditLogTillOpen(TransactionIfc transaction, TillOpenCargoIfc cargo, Boolean success)
    {
        TillOpenEvent ev = (TillOpenEvent)AuditLoggingUtils.createLogEvent(TillOpenEvent.class, AuditLogEventEnum.TILL_OPEN);
        AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();
        Hashtable<String, String> denominationQuantities = new Hashtable<String, String>();
        Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        String logTotalFloatAmount = AuditLoggerConstants.FLOAT_AMOUNT;
        
        if (cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL
                || cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY)
        {
            FinancialTotalsIfc fti = cargo.getFloatTotals();
            ReconcilableCountIfc rci = fti.getStartingFloatCount();
            FinancialCountIfc fci = rci.getEntered();
            FinancialCountTenderItemIfc[] fcti = fci.getTenderItems();
            CurrencyIfc ci = fci.getAmount();
            if (cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
            {
                for (int i = 0; i < fcti.length; i++)
                {
                    if (fcti[i].isSummary() == false)
                    {
                        String i18nAuditDesc = fcti[i].getTenderDescriptor().getDenomination()
                                .getDenominationDisplayName(defaultLocale);
                        int num = fcti[i].getNumberItemsIn();
                        denominationQuantities.put(i18nAuditDesc, String.valueOf(num));
                    }
                }
            }
            String totalFloatString = ci.toFormattedString();
            logTotalFloatAmount = totalFloatString;
        }
        ev.setUserId(transaction.getCashier().getLoginID());
        ev.setStoreId(transaction.getFormattedStoreID());
        ev.setRegisterID(transaction.getWorkstation().getWorkstationID());
        ev.setBusinessDate(transaction.getBusinessDay().dateValue());
        ev.setTillID(transaction.getTillID());
        ev.setOperatorID(transaction.getCashier().getEmployeeID());
        ev.setTransactionNumber(transaction.getTransactionID());
        ev.addDenomination(denominationQuantities, null);
        ev.setFloatAmount(logTotalFloatAmount);
        ev.setEventOriginator(originator);
        if (success)
        {
            auditService.logStatusSuccess(ev);
        }
        else
        {
            auditService.logStatusFailure(ev);
        }
    }
    
    /**
     * Create the till open transaction.
     * 
     * @param bus
     * @param cargo
     * @param till
     * @param register
     * @return transaction
     * 
     * @since 14.1
     */
    protected TillOpenCloseTransactionIfc createTillOpenTransaction(BusIfc bus, TillOpenCargoIfc cargo, TillIfc till, RegisterIfc register)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        TillOpenCloseTransactionIfc transaction = DomainGateway.getFactory().getTillOpenCloseTransactionInstance();
        transaction.setStartingFloatCount(till.getTotals().getStartingFloatCount().getEntered());
        transaction.setTenderDescriptorArrayList(cargo.getStoreStatus().getSafeTenderTypeDescList());

        // successfully added/updated the till
        transaction.setTransactionType(TransactionIfc.TYPE_OPEN_TILL);
        utility.initializeTransaction(transaction, -1);
        transaction.setTimestampEnd();
        transaction.setTransactionStatus(TransactionIfc.STATUS_COMPLETED);
        transaction.setTill(till);
        transaction.setRegister(register);
        return transaction;
    }
    
    /**
     * Write the transaction to the database.  Returns Success letter if no problem, Failure letter indicating that a dialog has been 
     * displayed and no letter is to be mailed, or UpdateError letter for failures not requiring a dialog.
     * 
     * @param bus
     * @param cargo
     * @param transaction
     * @return letter
     * 
     * @since 14.1
     */
    protected String saveTillOpenTransaction(BusIfc bus, TillOpenCargoIfc cargo, TillOpenCloseTransactionIfc transaction)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        String letterName = CommonLetterIfc.SUCCESS;
        // Write a transaction to the DB
        try
        {
            utility.saveTransaction(transaction);
            if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditLogTillOpen(transaction, cargo, true);
            }
        }
        catch (DataException se)
        {
            if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                auditLogTillOpen(transaction, cargo, false);
            }
            logger.error("" + se + "");
            if (se.getErrorCode() == DataException.QUEUE_FULL_ERROR
                    || se.getErrorCode() == DataException.STORAGE_SPACE_ERROR
                    || se.getErrorCode() == DataException.QUEUE_OP_FAILED)
            {

                POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                DialogBeanModel dialogModel = util.createErrorDialogBeanModel(se, false);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
                // letter indicating dialog has been displayed and no letter is to be mailed. 
                letterName = CommonLetterIfc.FAILURE;
            }
            else
            {
                // set error code
                cargo.setDataExceptionErrorCode(se.getErrorCode());
                letterName = CommonLetterIfc.UPDATE_ERROR;
            }
        }
        return letterName;
    }
    
    /**
     * Journal the till open.
     * 
     * @param bus
     * @param cargo
     * @param transaction
     * @param registerd
     * 
     * @since 14.1
     */
    protected void journalTillOpen(BusIfc bus, TillOpenCargoIfc cargo, TransactionIfc transaction, RegisterIfc register)
    {
        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        // journal the till status
        StringBuffer sb = new StringBuffer();
        // default journal Title, TillID for Parameter 'No', 'Summary',
        // 'Detail'.
        Object[] dataArgs = new Object[2];
        dataArgs[0] = cargo.getTillID();
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.OPEN_TILL_LABEL, null))
                .append(Util.EOL)
                .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.HEADER_LINE_LABEL,
                        null))
                .append(Util.EOL)
                .append(Util.EOL)
                .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_ID_LABEL,
                        dataArgs));
        // additional journal output for both Parameter 'Summary' and 'Detail.
        if (cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL
                || cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY)
        {
            FinancialTotalsIfc fti = cargo.getFloatTotals();
            ReconcilableCountIfc rci = fti.getStartingFloatCount();
            FinancialCountIfc fci = rci.getEntered();
            FinancialCountTenderItemIfc[] fcti = fci.getTenderItems();
            CurrencyIfc ci = fci.getAmount();
            // journal output for 'Unexpected Float Amount Accepted' condition
            String floatAmount = register.getTillFloatAmount().toFormattedString();
            if (!ci.getStringValue().equals(floatAmount))
            {
                dataArgs[0] = floatAmount;
                sb.append(Util.EOL)
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.EXPECTED_FLOAT_AMOUNT_LABEL, dataArgs));
            }
            // check to see if TillCountTillOpen parameter is set to 'Detail'
            // journal output for Parameter 'Detail'.
            if (cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
            {
                sb.append(Util.EOL);

                for (int i = 0; i < fcti.length; i++)
                {
                    if (fcti[i].isSummary() == false)
                    {
                        String i18nDesc = fcti[i].getTenderDescriptor().getDenomination()
                                .getDenominationDisplayName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));

                        int num = fcti[i].getNumberItemsIn();
                        dataArgs = new Object[] { i18nDesc, num };
                        String trCurrDesc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.TILL_CURR_DETAIL, dataArgs);
                        sb.append(Util.EOL).append(trCurrDesc);
                    }
                }
            }
            // journal Total Float for Parameter 'Detail', 'Summary'
            String totalFloatString = ci.toFormattedString();
            dataArgs[0] = totalFloatString;
            sb.append(Util.EOL)
                    .append(Util.EOL)
                    .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_FLOAT_LABEL,
                            dataArgs));
            sb.append(Util.EOL).append(Util.EOL).append(Util.EOL);
        }
        jmi.journal(cargo.getOperator().getEmployeeID(), transaction.getTransactionID(), sb.toString());
    }
}
