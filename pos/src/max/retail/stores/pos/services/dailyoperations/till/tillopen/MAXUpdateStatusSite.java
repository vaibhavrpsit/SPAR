/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillopen/UpdateStatusSite.java /main/20 2014/06/25 15:23:30 icole Exp $
 * ===========================================================================
 * Rev 1.0 	Aug 26,2016		Ashish yadav 	Changes done for code merging
 * Initial revision.
 * ===========================================================================
 */
package max.retail.stores.pos.services.dailyoperations.till.tillopen;

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
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
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
import oracle.retail.stores.pos.services.common.CheckTrainingReentryMode;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargo;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.TillOpenCargoIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillopen.UpdateStatusSite;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

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
public class MAXUpdateStatusSite extends UpdateStatusSite
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
		// Changes start for Rev 1.0
		POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
		//Changes ends for rev 1.0

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
			/*// Changes start for Rev 1.0
			saveTransSuccess = false;
			// changes ends for Rev 1.0
*/
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
