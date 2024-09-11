/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillreconcile/UpdateStatusSite.java /main/28 2013/10/28 09:04:41 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
 *    abhinavs  12/07/12 - Fixing HP Fortify redundant null check issues
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    nganesh   03/26/09 - Modified code to handle denomination
 *                         internationalization for Auditlog and EJournal
 *    nganesh   03/20/09 - Modified EJournal for denomination
 *                         internationalization refactoring
 *    mahising  02/22/09 - Fixed issue for Foreign Currency Journal entry
 *    deghosh   02/02/09 - EJ i18n defect fixes
 *    deghosh   12/15/08 - EJ i18n till reconcile texts added
 *    deghosh   12/15/08 - EJ i18n journal text format changes
 *    deghosh   11/26/08 - EJ i18n changes
 *    deghosh   11/05/08 - I18n changes auditlog
 *    deghosh   11/05/08 - Made I18n changes for audit logging POS till
 *                         reconcile
 *    deghosh   10/29/08 - EJI18n_changes_ExtendyourStore
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 *   $Log:
 *    24   360Commerce 1.23        6/11/2008 5:28:40 PM   Charles D. Baker CR
 *         31744 - Corrected formatting of total till amount. Code reviewed by
 *          Anda Cadar.
 *    23   360Commerce 1.22        6/7/2008 8:37:48 AM    Manas Sahu
 *         Removed spaces after Cash_Total
 *    22   360Commerce 1.21        6/6/2008 5:46:22 AM    Manikandan Chellapan
 *         CR#31956 Fixed audit log user id logging
 *    21   360Commerce 1.20        6/2/2008 10:36:26 AM   Neeraj Gautam   Made
 *         changes for displaying Total Till Amount on EJ when no transaction
 *         is made on the till and changed the label Total Count to Total Till
 *          Amount as per the requirement doc(Till Options)
 *    20   360Commerce 1.19        6/1/2008 6:05:59 AM    subramanyaprasad gv
 *         CR 31847: Made changes to bring in consistency in audit log for
 *         till reconcile. Code reviewed by Anil Kandru.
 *    19   360Commerce 1.18        5/2/2008 6:14:35 AM    Manas Sahu      When
 *         parameter is set to detail and the float is not enetered by the
 *         operator in that case the Audit Log has to report the summary.
 *    18   360Commerce 1.17        4/18/2008 5:11:52 AM   Deepankar Ghosh
 *         Modified a line for setting correct operator ID
 *    17   360Commerce 1.16        4/15/2008 4:24:35 AM   Manikandan Chellapan
 *         CR#30394 Removed unnecessary elements in till reconcile event.
 *    16   360Commerce 1.15        3/24/2008 4:30:51 AM   Sujay Beesnalli For
 *         defect 30839, forward ported from v12x
 *    15   360Commerce 1.14        3/6/2008 12:37:39 AM   Manikandan Chellapan
 *         CR#30394 Fixed Till Reconcile Event audit log entry
 *    14   360Commerce 1.13        2/29/2008 5:28:13 AM   Chengegowda Venkatesh
 *          fix for CR : 30345
 *    13   360Commerce 1.12        1/24/2008 12:04:33 AM  Chengegowda Venkatesh
 *          PABP 30 - Originating Point checkin for BO and CO audit Log
 *    12   360Commerce 1.11        1/18/2008 4:29:59 AM   Chengegowda Venkatesh
 *          PABP 30 - Originating Point checkin for BO and CO audit Log
 *    11   360Commerce 1.10        1/7/2008 10:18:22 PM   Chengegowda Venkatesh
 *          Changes for AuditLog incorporation
 *    10   360Commerce 1.9         7/13/2007 4:37:38 PM   Anda D. Cadar
 *         removed ISO code from base currency
 *    9    360Commerce 1.8         7/13/2007 3:59:36 PM   Anda D. Cadar   call
 *         toGroupFormattedString
 *    8    360Commerce 1.7         7/10/2007 4:51:51 PM   Charles D. Baker CR
 *         27506 - Updated to remove old fix for truncating extra decimal
 *         places that are used for accuracy. Truncating is no longer
 *         required.
 *    7    360Commerce 1.6         5/21/2007 1:02:07 PM   Anda D. Cadar   use
 *         ConstrainedTextField for currency display
 *    6    360Commerce 1.5         5/18/2007 2:33:36 PM   Anda D. Cadar   EJ
 *         changes
 *    5    360Commerce 1.4         5/18/2007 9:18:14 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    4    360Commerce 1.3         4/25/2007 8:52:28 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *   $
 *
 *   Revision 1.16  2004/07/23 16:19:51  dcobb
 *   @scr 6428 suspend/resume till - can't resume till after different user closes their till
 *   Update the currentTillID for the register before the save operations.
 *
 *   Revision 1.15  2004/07/09 23:27:01  dcobb
 *   @scr 5190 Crash on Pickup Canadian Checks
 *   @scr 6101  Pickup of local cash gives "Invalid Pickup" of checks error
 *   Backed out awilliam 5109 changes and fixed crash on pickup of Canadian checks.
 *
 *   Revision 1.14  2004/07/08 16:43:12  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   tillreconcile needs updated sequence number upon return from tillclose.
 *
 *   Revision 1.13  2004/07/06 21:24:33  dcobb
 *   @scr 2028 Till Closing Discrepancy Confirmation screen
 *   Determine expected closing float from the entered starting float.
 *
 *   Revision 1.12  2004/06/30 20:35:16  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   journaling
 *
 *   Revision 1.11  2004/06/30 18:17:59  dcobb
 *   @scr 5167 - Till Close and Till Reconcile will both be journaled.
 *
 *   Revision 1.10  2004/06/25 22:35:42  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Addjouranling for foreign currency count.
 *
 *   Revision 1.9  2004/06/25 00:23:52  dcobb
 *   @scr 5263 - Can't resume suspended till.
 *   the till has been removed from the drawer; set the till status to floating and update the current till for the register.
 *
 *   Revision 1.8  2004/06/24 17:26:02  dcobb
 *   @scr 5263 - Can't resume suspended till.
 *   Backed out khassen changes.
 *
 *   Revision 1.7  2004/06/23 14:24:08  khassen
 *   @scr 5263 - Updated register to reflect current status of till.
 *
 *   Revision 1.6  2004/06/10 22:06:21  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency count.
 *
 *   Revision 1.5  2004/05/26 21:36:15  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Testing with no alternate currencies configured.
 *
 *   Revision 1.4  2004/05/17 20:23:42  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Added RemoveTillRoad to tillreconcile. Drawer status is set in RemoveTillRoad.
 *
 *   Revision 1.3  2004/05/17 19:24:17  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Set drawer status to unoccupied only if the current till for the register is the reconciled till.
 *
 *   Revision 1.2  2004/05/14 17:00:25  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Check for online status when comping from tillclose.
 *
 *   Revision 1.1  2004/04/15 18:57:00  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Till reconcile service is now separate from till close.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import oracle.retail.stores.commerceservices.audit.AuditLoggerI18NHelper;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.DenominationLogElement;
import oracle.retail.stores.commerceservices.audit.event.TillReconcileEvent;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.AbstractFinancialEntityIfc;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
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
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
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

import org.apache.log4j.Logger;

/**
 * Updates the till status, totals, operator, and closing time. Updates the
 * database with this till information.
 *
 * @version $Revision: /main/28 $
 */
public class UpdateStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 7712857059123898983L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/28 $";

    /**
     * Updates the till status by setting the status to reconciled, setting the
     * closing time, setting the operator, and updating the database with this
     * tills totals.
     *
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        TillReconcileCargo cargo = (TillReconcileCargo)bus.getCargo();
        
        boolean saveTransSuccess = true;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Local references to register and till.
        RegisterIfc register = cargo.getRegister();
        TillIfc till = register.getTillByID(cargo.getTillID());

        // create close till transaction
        TillOpenCloseTransactionIfc transaction = DomainGateway.getFactory().getTillOpenCloseTransactionInstance();
        transaction.setTransactionType(TransactionIfc.TYPE_CLOSE_TILL);
        utility.initializeTransaction(transaction, -1);
        transaction.setTimestampEnd();

        // save current register accountability of the register
        till.setRegisterAccountability(register.getAccountability());

        // Update current till to indicate an empty drawer
        TillIfc currentTill = register.getCurrentTill();
        if (currentTill != null)
        {
            String currentTillID = currentTill.getTillID();
            if (currentTillID != null)
            {
                if (currentTillID.equalsIgnoreCase(till.getTillID()))
                {
                    register.setCurrentTillID("");
                }
            }
        }

        // Mark the status of the till
        till.setStatus(AbstractFinancialEntityIfc.STATUS_RECONCILED);
        transaction.setEndingFloatCount(cargo.getFloatTotals().getEndingFloatCount());
        transaction.setEndingCombinedEnteredCount(cargo.getTillTotals().getCombinedCount().getEntered());
        transaction.setTenderDescriptorArrayList(cargo.getStoreStatus().getSafeTenderTypeDescList());

        till.setCloseTime();
        till.setSignOffOperator(cargo.getOperator());

        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        journalTillReconcile(till, register, transaction, cargo, jmi);

        // Get audit logger
        AuditLoggerServiceIfc auditLogger = AuditLoggingUtils.getAuditLogger();
        // save all common data.
        // Note: These are saved after till totals so that the totals go first
        // into the transaction queue
        try
        {

            transaction.setRegister(register);
            transaction.setTill(till);
            transaction.setTillID(till.getTillID());
            // save the till reconcile transaction
            utility.saveTransaction(transaction);

            // log the event if the register is not in Training and Reentry mode
            if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                TillReconcileEvent event = logAuditEvent(bus, cargo);
                // Set the transaction number here
                event.setTransactionNumber(transaction.getTransactionID());
                event.setEventOriginator("LookupStoreStatusSite.arrive");
                event.setBusinessDate(transaction.getBusinessDay().dateValue());
                auditLogger.logStatusSuccess(event);
            }

        }
        catch (DataException e)
        {
            logger.error(e.toString());

            // log the failure if the register is not in Training and Reentry
            // mode
            if (!CheckTrainingReentryMode.isTrainingRetryOn(cargo.getRegister()))
            {
                TillReconcileEvent event = logAuditEvent(bus, cargo);
                // Set the transaction number here
                event.setTransactionNumber(transaction.getTransactionID());
                event.setEventOriginator("LookupStoreStatusSite.arrive");
                event.setBusinessDate(transaction.getBusinessDay().dateValue());
                auditLogger.logStatusFailure(event);
            }
            UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            DialogBeanModel dialogModel = util.createErrorDialogBeanModel(e);
            //display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
            saveTransSuccess = false;
        }
        if (saveTransSuccess)
        {
        	bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
        }
    }

    /**
     * Journals the till reconcile.
     *
     * @param till till object
     * @param register register object
     * @param transaction TillOpenCloseTransaction object
     * @param cargo cargo object
     * @param jmi JournalManagerIfc object
     */
    protected void journalTillReconcile(TillIfc till, RegisterIfc register, TillOpenCloseTransactionIfc transaction,
            TillReconcileCargo cargo, JournalManagerIfc jmi)
    { // begin journalTillReconcile()
        // journal the till status
        if (jmi != null)
        {
            StringBuffer sb = new StringBuffer();


            // get pattern for baseCurrencyType
            CurrencyIfc baseCurrency = DomainGateway.getBaseCurrencyInstance();

            String baseCountry = baseCurrency.getCountryCode();

            // Default journal Title, TillID for Parameter
            // TillCloseCountFloat = 'No', 'Summary' or 'Detail'.
            Object[] dataArgs = new Object[2];
			dataArgs[0] = cargo.getTillID();
            sb
					.append(
							I18NHelper.getString(
									I18NConstantsIfc.EJOURNAL_TYPE,
									JournalConstantsIfc.RECONCILE_TILL_LABEL,
									null))
					.append(Util.EOL)
					.append(Util.EOL)
					.append(
							I18NHelper
									.getString(I18NConstantsIfc.EJOURNAL_TYPE,
											JournalConstantsIfc.TILL_ID_LABEL,
											dataArgs))
					.append(Util.EOL)
					.append(
							I18NHelper.getString(
									I18NConstantsIfc.EJOURNAL_TYPE,
									JournalConstantsIfc.STATUS_LABEL, null))
					.append(
							I18NHelper
									.getString(
											I18NConstantsIfc.EJOURNAL_TYPE,
											"JournalEntry."
													+ AbstractStatusEntityIfc.STATUS_DESCRIPTORS[till
															.getStatus()]
													+ "Label", null)).append(
							Util.EOL);

            // Additional journal output for Parameter TillCloseCountFloat
            // set to 'Summary' or 'Detail.
            if (cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL
                    || cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY)
            {
                FinancialTotalsIfc floatFti = cargo.getFloatTotals();
                ReconcilableCountIfc floatRci = floatFti.getEndingFloatCount();
                FinancialCountIfc floatFci = floatRci.getEntered();
                FinancialCountTenderItemIfc[] floatFcti = floatFci.getTenderItems();
                CurrencyIfc floatCi = floatFci.getAmount().abs();

                // Journal output for
                // 'Unexpected Float Amount Accepted' condition.
                CurrencyIfc floatAmount = till.getTotals().getStartingFloatCount().getEntered().getAmount();
                if (floatCi.compareTo(floatAmount) != CurrencyIfc.EQUALS)
                {
                	dataArgs[0] = floatAmount.toFormattedString();
                	sb
							.append(Util.EOL)
							.append(
									I18NHelper
											.getString(
													I18NConstantsIfc.EJOURNAL_TYPE,
													JournalConstantsIfc.EXPECTED_FLOAT_AMOUNT_LABEL,
													dataArgs));
                }

                // journal output for Parameter
                // TillCloseFloatCount = 'Detail'.
                if (cargo.getFloatCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
                {

                    for (int i = 0; i < floatFcti.length; i++)
                    {
                        if (floatFcti[i].isSummary() == false)
                        {


                            int num = floatFcti[i].getNumberItemsOut();
                            String i18nDesc = floatFcti[i].getTenderDescriptor().getDenomination().getDenominationDisplayName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                            dataArgs = new Object []{i18nDesc,num};

                            String trCurrDesc =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_RECONCILE_CURR_DETAIL,dataArgs);

                            sb.append(Util.EOL).append(trCurrDesc);
                        }
                    }
                }

                // journal Total Float for Parameter 'Detail', 'Summary'
                String formattedFloat = floatCi.toFormattedString();
                dataArgs[0] = formattedFloat;
                sb.append(Util.EOL).append(Util.EOL).append(
						I18NHelper
								.getString(I18NConstantsIfc.EJOURNAL_TYPE,
										JournalConstantsIfc.TOTAL_FLOAT_LABEL,
										dataArgs));

            }

            // Additional journal output for Parameter TillCount
            // set to 'Summary' or 'Detail.
            if (cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL
                    || cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY)
            {
                FinancialTotalsIfc tillFti = cargo.getTillTotals();
                ReconcilableCountIfc tillRci = tillFti.getCombinedCount();
                FinancialCountIfc entered = tillRci.getEntered();
                FinancialCountTenderItemIfc[] enteredFcti = entered.getTenderItems();
                String noTransactionFlag = "yes";

                CurrencyTypeIfc[] alternateCurrencies = DomainGateway.getAlternateCurrencyTypes();
                int currLen = 1;
                if (alternateCurrencies != null)
                {
                    currLen += alternateCurrencies.length;
                }
                String[] currNat = new String[currLen];
                currNat[0] = DomainGateway.getBaseCurrencyType().getCountryCode();
                
                if(null != alternateCurrencies)
                {
                for (int i = 1; i < currLen; i++)
                {
                    if(null != alternateCurrencies[i-1])
                    {
                	currNat[i] = alternateCurrencies[i - 1].getCountryCode();
                    }
                }
                }
                // journal output for Parameter
                // TillCloseCountTill = 'Summary'.
                if (cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY)
                {
                    CurrencyIfc subTotal;

                    for (int cnt = 0; cnt < currNat.length; cnt++)
                    {
                        subTotal = DomainGateway.getBaseCurrencyInstance();
                        String isoCode = "";
                        String cDesc = "";
                        if (cnt > 0)
                        {
                            subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                            isoCode = subTotal.getCurrencyCode();
                            cDesc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+isoCode,null);
                        }
                        String currTotalCode = subTotal.getCurrencyCode();

                        sb.append(Util.EOL);
                        String totalsFlag = "no";


                        for (int i = 0; i < enteredFcti.length; i++)
                        {
                            String nat = enteredFcti[i].getAmountTotal().getCountryCode();

                            if (enteredFcti[i].isSummary() && nat.equals(currNat[cnt]))
                            {
                                String desc = enteredFcti[i].getDescription();
                                String amtTotal = enteredFcti[i].getAmountTotal().toGroupFormattedString();

                               // int spc = 28 - currDesc.length() - desc.length() - 3 - amtTotal.length();
                                String i18nDesc = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+desc,null);
                                if(cDesc!="")
                        		{
                                	dataArgs = new Object []{cDesc,i18nDesc,amtTotal};
                        		}
                                else
                                {
                                	dataArgs = new Object []{"",i18nDesc,amtTotal};
                                }


                                String trCurrDesc =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_RECONCILE_CURR,dataArgs);

                                sb.append(Util.EOL).append(trCurrDesc);

                                subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                                totalsFlag = "yes";
                            }
                        }

                        if (totalsFlag == "yes" || noTransactionFlag == "yes")
                        {
                            String subTotalString = "0.00";
                            noTransactionFlag = "no";
                            if (!baseCountry.equals(subTotal.getCountryCode()))
                            {
                                subTotalString = subTotal.toISOFormattedString();

                            }
                            else
                            {
                                subTotalString = subTotal.toFormattedString();

                            }

                            String cTD = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+currTotalCode,null);
                            dataArgs = new Object []{cTD,subTotalString};

                            String jTotalAmount =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_TILL_AMOUNT,dataArgs);

                            sb.append(Util.EOL).append(Util.EOL).append(jTotalAmount);
                        }
                    }
                }

                // journal output for Parameter
                // TillCloseCountTill = 'Detail'.
                else if (cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
                {
                    CurrencyIfc subTotal;

                    for (int cnt = 0; cnt < currNat.length; cnt++)
                    {
                        subTotal = DomainGateway.getBaseCurrencyInstance();
                        String isoCode = "";
                        if (cnt > 0)
                        {
                            subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                            isoCode = subTotal.getCurrencyCode() + " ";
                        }
                        String currTotalCode = subTotal.getCurrencyCode();

                        boolean addBlankLines = true;
                        String totalsFlag = "no";

                        boolean alternatesFound = false;
                        for (int i = 0; i < enteredFcti.length; i++)
                        {
                            String nat = enteredFcti[i].getAmountTotal().getCountryCode();

                            if (enteredFcti[i].isSummary() == false && nat.equals(currNat[cnt])
                                    && enteredFcti[i].getSummaryDescription().endsWith("Cash"))
                            {
                                if (addBlankLines)
                                {
                                    sb.append(Util.EOL).append(Util.EOL);
                                    addBlankLines = false;
                                }
                                // if we've found alternate currency entry,
                                // put heading
                                if (cnt > 0 && alternatesFound == false)
                                {
                                    sb
											.append(
													I18NHelper
															.getString(
																	I18NConstantsIfc.EJOURNAL_TYPE,
																	JournalConstantsIfc.FOREIGN_CURRENCY_LABEL,
																	null))
											.append(Util.EOL);
									alternatesFound = true;
                                }

                                int amt = enteredFcti[i].getNumberItemsTotal();
                                sb.append(Util.EOL);
                                /*
                                 * if (cnt > 0) { sb.append(currDesc); }
                                 */


                                String i18nDesc = enteredFcti[i].getTenderDescriptor().getDenomination().getDenominationDisplayName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                                dataArgs = new Object []{i18nDesc,amt};

                                String trCurrDesc =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_RECONCILE_CURR_DETAIL,dataArgs);

                                sb.append(trCurrDesc);

                                //sb.append(desc).append(":").append(amt);
                                subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                                totalsFlag = "yes";
                            }
                        }

                        if (totalsFlag == "yes")
                        {
                            String subTotalString = "0.00";
                            if (!baseCountry.equals(subTotal.getCountryCode()))
                            {
                                subTotalString = subTotal.toISOFormattedString();

                            }
                            else
                            {
                                subTotalString = subTotal.toFormattedString();

                            }

                            dataArgs = new Object []{subTotalString};
                            String str = I18NHelper.getString(
									I18NConstantsIfc.EJOURNAL_TYPE,
									JournalConstantsIfc.CASH_TAG_LABEL,
									dataArgs);

                            dataArgs = new Object []{isoCode,str};

                            sb
									.append(Util.EOL)
									.append(Util.EOL)
									.append(
											I18NHelper
													.getString(
															I18NConstantsIfc.EJOURNAL_TYPE,
															JournalConstantsIfc.TOTAL_TAG_LABEL,
															dataArgs));

                        }
                        CurrencyIfc cashTotal = (CurrencyIfc)subTotal.clone();

                        // journal the rest of the detail.
                        // String test = sb.toString();
                        addBlankLines = true;
                        totalsFlag = "no";
                        subTotal = DomainGateway.getBaseCurrencyInstance();
                        if (cnt > 0)
                        {
                            subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                        }

                        for (int i = 0; i < enteredFcti.length; i++)
                        {
                            String nat = enteredFcti[i].getAmountTotal().getCountryCode();

                            if (enteredFcti[i].isSummary() && nat.equals(currNat[cnt])
                                    && !enteredFcti[i].getDescription().endsWith("Cash"))
                            {
                                if (addBlankLines)
                                {
                                    sb.append(Util.EOL);
                                    addBlankLines = false;
                                }
                                String desc = enteredFcti[i].getDescription();
                                String i18nCode = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+desc,null);

                                // for foreign currency EJ entry
                                StringTokenizer st = new StringTokenizer(desc, " ");
                                int tokens = st.countTokens();
                                if (tokens > 1)
                                {
                                    String s = "";
                                    while (st.hasMoreTokens())
                                    {
                                        s = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                                JournalConstantsIfc.JOURNAL_ENTRY_PREFIX + st.nextToken(), null);
                                    }
                                    i18nCode = enteredFcti[i].getAmountTotal().getCurrencyCode() + " " + s;
                                }

                                CurrencyIfc amt = enteredFcti[i].getAmountTotal();

                                String totalDesc = i18nCode + ": " + enteredFcti[i].getNumberItemsTotal();
                                String amtString = "0.00";
                                if (!baseCountry.equals(amt.getCountryCode()))
                                {
                                    amtString = amt.toISOFormattedString();

                                }
                                else
                                {
                                    amtString = amt.toFormattedString();

                                }


                                dataArgs = new Object []{totalDesc,amtString};
                                sb.append(Util.EOL).append(I18NHelper
										.getString(
												I18NConstantsIfc.EJOURNAL_TYPE,
												JournalConstantsIfc.TOTAL_TAG_LABEL,
												dataArgs));

                                subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                                totalsFlag = "yes";
                            }
                        }
                        if (cashTotal != null)
                        {
                            subTotal = subTotal.add(cashTotal);
                        }
                        if (totalsFlag == "yes" || noTransactionFlag == "yes")
                        {
                            String subTotalString = "0.00";
                            noTransactionFlag = "no";
                            if (!baseCountry.equals(subTotal.getCountryCode()))
                            {
                                subTotalString = subTotal.toISOFormattedString();

                            }
                            else
                            {
                                subTotalString = subTotal.toFormattedString();

                            }

                            String cTD = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+currTotalCode,null);
                            dataArgs = new Object []{cTD,subTotalString};

                            String jTotalAmount =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_TILL_AMOUNT,dataArgs);

                            sb.append(Util.EOL).append(Util.EOL).append(jTotalAmount);

                        }
                    }
                }
            }

            jmi.journal(cargo.getOperator().getEmployeeID(), transaction.getTransactionID(), sb.toString());
        }
    } // end journalTillReconcile()

    /**
     * Returns TillReconcileEvent with till reconcile event details
     *
     * @param cargo The Till Reconcile Cargo
     * @return TillReconcileEvent The till reconcile event
     */
    private TillReconcileEvent logAuditEvent(BusIfc bus, TillReconcileCargo cargo)
    {


        FinancialTotalsIfc tillFti = cargo.getTillTotals();
        ReconcilableCountIfc tillRci = tillFti.getCombinedCount();
        FinancialCountIfc entered = tillRci.getEntered();
        FinancialCountTenderItemIfc[] enteredFcti = entered.getTenderItems();
        boolean cashModified = false;
        if (enteredFcti != null  && enteredFcti.length != 0)
        {
            for (int i = 0; i < enteredFcti.length; i++)
            {
                if (enteredFcti[i].getDescription().endsWith("Cash"))
                {
                    cashModified = true;
                    break;
                }
            }
        }

        Hashtable<String, String> denominationQuantities = new Hashtable<String, String>();

        HashSet<String> currencyCodes = new HashSet<String>();


        Collection<DenominationLogElement> denominations = new ArrayList<DenominationLogElement>();

        TillReconcileEvent reconcileEvent = (TillReconcileEvent)AuditLoggingUtils.createLogEvent(
                TillReconcileEvent.class, AuditLogEventEnum.TILL_RECONCILE);

        // Set store id
        reconcileEvent.setStoreId(cargo.getRegister().getWorkstation().getStoreID());

        // Set register id
        reconcileEvent.setRegisterID(cargo.getRegister().getWorkstation().getWorkstationID());

        // Set operator id
        reconcileEvent.setOperatorID(cargo.getOperator().getEmployeeID());

        // Set user id
        reconcileEvent.setUserId(cargo.getOperator().getLoginID());

        // Set till id
        reconcileEvent.setTillID(cargo.getTill().getTillID());

        // Get blind close parameter value
        boolean blindClose = getBlindCloseParameterValue(bus);

        // Set Blind close
        reconcileEvent.setBlindClose(blindClose);

        // Denomination sequence for audit logger
        ArrayList<String> order = new ArrayList<String>();

        CurrencyTypeIfc[] alternateCurrencies = DomainGateway.getAlternateCurrencyTypes();
        int currLen = 1;
        if (alternateCurrencies != null)
        {
            currLen += alternateCurrencies.length;
        }
        String[] currNat = new String[currLen];
        currNat[0] = DomainGateway.getBaseCurrencyType().getCountryCode();
        
       if(null != alternateCurrencies){
        for (int i = 1; i < currLen; i++)
        {
           if(null !=alternateCurrencies[i-1])
           {
        	currNat[i] = alternateCurrencies[i - 1].getCountryCode();
           }
        }
       }
        // log output for Parameter TillCloseCountTill = 'Summary' And
        // BlindClose is 'False'
        if (cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY && !blindClose)
        {
            CurrencyIfc subTotal;
            String i18nCurrCode = null;

            // total credit amount
            CurrencyIfc creditTotal = DomainGateway.getBaseCurrencyInstance();
            // total debit amount
            CurrencyIfc debitTotal = DomainGateway.getBaseCurrencyInstance();

            for (int cnt = 0; cnt < currNat.length; cnt++)
            {
                subTotal = DomainGateway.getBaseCurrencyInstance();

                // true if there is a credit tender
                boolean creditTotalFlag = false;

                // true if there is a debit tender
                boolean debitTotalFlag = false;

                if (cnt > 0)
                {
                    subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                }

                String currTotalDesc = subTotal.getType().getNationality();

                for (int i = 0; null != enteredFcti && i < enteredFcti.length; i++)
                {
                    String nat = enteredFcti[i].getAmountTotal().getCountryCode();
                    i18nCurrCode = enteredFcti[i].getAmountTotal().getType().getNationality();

                    if (enteredFcti[i].isSummary() && nat.equals(currNat[cnt]))
                    {

                        // calculate total credit amount
                        if (enteredFcti[i].getSummaryDescription().equals("Credit"))
                        {
                            creditTotal = creditTotal.add(enteredFcti[i].getAmountTotal());
                            creditTotalFlag = true;
                        }

                        else if (enteredFcti[i].getSummaryDescription().equals("Debit"))
                        {
                            debitTotal = debitTotal.add(enteredFcti[i].getAmountTotal());
                            debitTotalFlag = true;
                        }

                        else
                        {
                            String desc = enteredFcti[i].getDescription();
                            String amtTotal = enteredFcti[i].getAmountTotal().toFormattedString();
                            String descI18n = null;

                            StringBuffer tenderDesc = new StringBuffer();


                            tenderDesc.append(desc).append("_Total");



                            descI18n = AuditLoggerI18NHelper.getTransactionTotalString(0, currTotalDesc, null,desc, tenderDesc.toString());
                            order.add(descI18n);
                            denominationQuantities.put(descI18n, amtTotal);
                        }

                        subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                    }
                }

                if (creditTotalFlag)
                {

                    String desc = AuditLoggerI18NHelper.getTransactionTotalString(0, i18nCurrCode, null,"Credit", "Credit_Total");
                    order.add(desc);

                    denominationQuantities.put(desc, creditTotal.toFormattedString());
                }

                if (debitTotalFlag)
                {

                    String desc = AuditLoggerI18NHelper.getTransactionTotalString(0, i18nCurrCode, null,"Debit", "Debit_Total");
                    order.add(desc);

                    denominationQuantities.put(desc, debitTotal.toFormattedString());
                }
            }
        }

        else if (cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL && cashModified == false)
        {

            CurrencyIfc subTotal;
            String descI18n = null;
            String i18nCurrCode = null;

            // total credit amount
            CurrencyIfc creditTotal = DomainGateway.getBaseCurrencyInstance();
            // total debit amount
            CurrencyIfc debitTotal = DomainGateway.getBaseCurrencyInstance();
            // testing
            enteredFcti = cargo.getFinancialTotals().getCombinedCount().getExpected().getTenderItems();
            for (int cnt = 0; cnt < currNat.length; cnt++)
            {
                subTotal = DomainGateway.getBaseCurrencyInstance();

                // true if there is a credit tender
                boolean creditTotalFlag = false;

                // true if there is a debit tender
                boolean debitTotalFlag = false;

                if (cnt > 0)
                {
                    subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                }

                String currTotalDesc = subTotal.getType().getNationality();

                for (int i = 0; i < enteredFcti.length; i++)
                {
                    String nat = enteredFcti[i].getAmountTotal().getCountryCode();
                    i18nCurrCode = enteredFcti[i].getAmountTotal().getType().getNationality();

                    if (enteredFcti[i].isSummary() && nat.equals(currNat[cnt]))
                    {

                        // calculate total credit amount
                        if (enteredFcti[i].getSummaryDescription().equals("Credit"))
                        {
                            creditTotal = creditTotal.add(enteredFcti[i].getAmountTotal());
                            creditTotalFlag = true;
                        }

                        else if (enteredFcti[i].getSummaryDescription().equals("Debit"))
                        {
                            debitTotal = debitTotal.add(enteredFcti[i].getAmountTotal());
                            debitTotalFlag = true;
                        }

                        else
                        {
                            String desc = enteredFcti[i].getDescription();
                            String amtTotal = enteredFcti[i].getAmountTotal().toFormattedString();

                            StringBuffer tenderDesc = new StringBuffer();


                            tenderDesc.append(desc).append("_Total");

                            descI18n = AuditLoggerI18NHelper.getTransactionTotalString(0, currTotalDesc, null,desc, tenderDesc.toString());
                            order.add(descI18n);
                            denominationQuantities.put(descI18n, amtTotal);

                        }

                        subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                    }
                }

                if (creditTotalFlag)
                {

                    String desc = AuditLoggerI18NHelper.getTransactionTotalString(0, i18nCurrCode, null,"Credit", "Credit_Total");
                    order.add(desc);

                    denominationQuantities.put(desc, creditTotal.toFormattedString());
                }

                if (debitTotalFlag)
                {
                    String desc = AuditLoggerI18NHelper.getTransactionTotalString(0, i18nCurrCode, null,"Debit", "Debit_Total");
                    order.add(desc);
                    denominationQuantities.put(desc, debitTotal.toFormattedString());
                }
            }

        }


        else if (cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
        {
            CurrencyIfc subTotal;

            for (int cnt = 0; cnt < currNat.length; cnt++)
            {
                boolean creditTotalFlag = false;
                String i18nCurrCode = null;

                subTotal = DomainGateway.getBaseCurrencyInstance();
                String currDesc = "";
                if (cnt > 0)
                {
                    subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                    currDesc = subTotal.getType().getNationality() + " ";
                }

                String totalsFlag = "no";

                for (int i = 0; null != enteredFcti && i < enteredFcti.length; i++)
                {
                    String nat = enteredFcti[i].getAmountTotal().getCountryCode();
                    i18nCurrCode = enteredFcti[i].getAmountTotal().getType().getNationality();

                    if (enteredFcti[i].isSummary() == false && nat.equals(currNat[cnt])
                            && enteredFcti[i].getSummaryDescription().endsWith("Cash")
                            && enteredFcti[i].getNumberItemsTotal() != 0)
                    {
                        String desc = enteredFcti[i].getDescription();
                        int amt = enteredFcti[i].getNumberItemsTotal();

                        desc = AuditLoggerI18NHelper.getString(desc);


                        String descI18N = enteredFcti[i].getTenderDescriptor().getDenomination().getDenominationDisplayName(LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE));

                        order.add(descI18N+"_"+enteredFcti[i].getCurrencyCode());
                        denominationQuantities.put(descI18N+"_"+enteredFcti[i].getCurrencyCode(), String.valueOf(amt));

                        currencyCodes.add(enteredFcti[i].getCurrencyCode());
                        subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                        totalsFlag = "yes";
                    }
                }

                if (totalsFlag == "yes")
                {

                	Iterator<String> it = currencyCodes.iterator();

                	int count = 0;
                	while(it.hasNext())
                	{
                		denominationQuantities.put("CURRENCY_CODES"+count++, it.next());
                	}

                    String subTotalString = "0.00";

                    StringBuffer desc = new StringBuffer();

                    String descI18n = null;

                    desc.append(currDesc).append("Cash_").append("Total");

                    subTotalString = subTotal.toFormattedString();

                    descI18n = AuditLoggerI18NHelper.getTransactionTotalString(0,currDesc, null,"Cash", desc.toString());


                    order.add(descI18n);


                    denominationQuantities.put(descI18n, subTotalString);

                }

                CurrencyIfc cashTotal = (CurrencyIfc)subTotal.clone();

                // log the rest of the detail.
                totalsFlag = "no";
                subTotal = DomainGateway.getBaseCurrencyInstance();
                if (cnt > 0)
                {
                    subTotal = DomainGateway.getAlternateCurrencyInstance(currNat[cnt]);
                }

                CurrencyIfc creditTotal = DomainGateway.getBaseCurrencyInstance();

                // Number of credits
                int creditCount = 0;

                for (int i = 0; null != enteredFcti && i < enteredFcti.length; i++)
                {
                    String nat = enteredFcti[i].getAmountTotal().getCountryCode();

                    if (enteredFcti[i].isSummary() && nat.equals(currNat[cnt])
                            && !enteredFcti[i].getDescription().endsWith("Cash"))
                    {

                        if (enteredFcti[i].getSummaryDescription().equals("Credit"))
                        {
                            creditTotal = creditTotal.add(enteredFcti[i].getAmountTotal());
                            creditCount += 1;
                            creditTotalFlag = true;
                        }
                        else
                        {
                            String desc = enteredFcti[i].getDescription();
                            String currDescI18n = null;
                            String descI18n = null;
                            currDescI18n = enteredFcti[i].getAmountTotal().getType().getNationality();
                            desc = desc.replaceFirst(currDescI18n,"");
                            desc = desc.replaceFirst(" ","");
                            CurrencyIfc amt = enteredFcti[i].getAmountTotal();

                            descI18n = AuditLoggerI18NHelper.getTransactionTotalString(0,currDescI18n, null,desc, (desc + "_Total"));

                            order.add(descI18n);
                            denominationQuantities.put(descI18n, amt.toFormattedString());

                            // Number of items
                            String count = String.valueOf(enteredFcti[i].getNumberItemsTotal());
                            int iCount = Integer.parseInt(count);

                            // Add count
                            String countFlag = "yes";
                            String countDesc = AuditLoggerI18NHelper.getTransactionTotalString(iCount,currDescI18n, countFlag,desc,desc);
                            order.add(countDesc);

                            denominationQuantities.put(countDesc, count);

                            // Add the amount to total
                            subTotal = subTotal.add(enteredFcti[i].getAmountTotal());
                            totalsFlag = "yes";

                        }

                    }

                }

                if (creditTotalFlag)
                {
                    // Add the credit total
                	String desc = AuditLoggerI18NHelper.getTransactionTotalString(0, i18nCurrCode, null,"Credit", "Credit_Total");
                    order.add(desc);

                    denominationQuantities.put(desc, creditTotal.toFormattedString());

//                  Number of items
                    String count = String.valueOf(creditCount);
                    int iCount = Integer.parseInt(count);

                    // Add credit count
                    String countFlag = "yes";
                    String countDesc = AuditLoggerI18NHelper.getTransactionTotalString(iCount,i18nCurrCode, countFlag,"Credit","Credit");
                    order.add(countDesc);
                    denominationQuantities.put(countDesc, String.valueOf(creditCount));

                    // Add credit count

                }
                if (cashTotal != null)
                {
                    subTotal = subTotal.add(cashTotal);
                }
            }

        }

        // add denominations to the event only if count type is not "NO"
        if (!(cargo.getTillCountType() == FinancialCountIfc.COUNT_TYPE_NONE))
        {
            String[] denomOrder = new String[order.size()];
            order.toArray(denomOrder);

            // Set denominations
            // Create a denomination log element
            DenominationLogElement logElement = new DenominationLogElement(denominationQuantities, denomOrder);
            denominations.add(logElement);
            reconcileEvent.setDenominations(denominations);
        }

        return reconcileEvent;

    }

    /**
     * Gets the boolean value associated with the Blind Close parameter. If the
     * value can not be found, a warning is logged and true.
     *
     * @return boolean value for the parameter
     */
    private boolean getBlindCloseParameterValue(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        Logger logger = Logger
                .getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tillreconcile.UpdateStatusSite.class);
        boolean blindClose = true;
        try
        {
            Boolean blindCloseParameter = pm.getBooleanValue("BlindClose");
            blindClose = blindCloseParameter.booleanValue();
        }
        catch (ParameterException pe)
        {
            logger.warn(getClass().getName() + ": getBlindCloseParameter(): Could not read parameter [BlindClose]");
        }
        return blindClose;
    }

}
