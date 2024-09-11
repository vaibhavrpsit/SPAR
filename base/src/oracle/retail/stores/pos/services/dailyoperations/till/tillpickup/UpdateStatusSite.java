/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillpickup/UpdateStatusSite.java /main/24 2012/09/12 11:57:09 blarsen Exp $
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
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    sgu       04/14/09 - refresh
 *    sgu       04/13/09 - Fix till pickup journal crash
 *    nganesh   03/26/09 - Updated for auditlog
 *    djenning  03/25/09 - use correct currency, even for NONE count type of
 *                         till pickup.
 *    nganesh   03/20/09 - Modified EJournal for denomination
 *                         internationalization refactoring
 *    ranojha   02/18/09 - Fixed import and export logic for TenderDescriptor
 *                         in till Pickup POSLog
 *    deghosh   02/02/09 - EJ i18n defect fixes
 *    deghosh   12/09/08 - EJ i18n formatting changes
 *    deghosh   12/02/08 - EJ i18n changes
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        8/14/2007 9:38:11 AM   Anda D. Cadar
 *         externaliza EJ for Pickup
 *    10   360Commerce 1.9         6/11/2007 11:51:27 AM  Anda D. Cadar   SCR
 *         27206: replace getNationality with getCountryCode; Nationality
 *         column in co_cny was poulated previosly with the value for the
 *         country code. I18N change was to populate nationality with
 *         nationality value
 *    9    360Commerce 1.8         5/23/2007 7:10:48 PM   Jack G. Swan    Fixed
 *          issues with tills and CurrencyID.
 *    8    360Commerce 1.7         5/18/2007 2:33:36 PM   Anda D. Cadar   EJ
 *         changes
 *    7    360Commerce 1.6         5/18/2007 9:18:14 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    6    360Commerce 1.5         4/25/2007 8:52:28 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    5    360Commerce 1.4         2/21/2006 3:51:19 AM   Akhilashwar K. Gupta
 *         CR-6701 - Updated comment in arrive() method as per the code review
 *          done by Rohit Sachdeva
 *    4    360Commerce 1.3         2/15/2006 5:30:12 AM   Akhilashwar K. Gupta
 *         Fixed in V710 view. Merging the changes in this view.
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:36 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse
 *
 *   Revision 1.14  2004/07/30 21:20:18  dcobb
 *   @scr 6462 Financial Totals are not correct for the detail count during Till Open/Reconcile
 *   Replaced all instances of FinancialCountIfc.getTenderItem(int, String) with getSummaryTenderItemByDescriptor(TenderDescriptorIfc).
 *
 *   Revision 1.13  2004/07/09 23:27:01  dcobb
 *   @scr 5190 Crash on Pickup Canadian Checks
 *   @scr 6101  Pickup of local cash gives "Invalid Pickup" of checks error
 *   Backed out awilliam 5109 changes and fixed crash on pickup of Canadian checks.
 *
 *   Revision 1.12  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.11  2004/05/27 17:09:35  awilliam
 *   @scr 5190 crash when slecting pickup canadian checks
 *
 *   Revision 1.10  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.9  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.8  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.7  2004/02/12 16:50:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.6  2004/02/11 23:25:23  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Journal "Canadian" instead of "CA".
 *
 *   Revision 1.5  2004/02/11 22:06:52  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Corrected test for 'Check' to work with alternate currency.
 *
 *   Revision 1.4  2004/02/11 21:47:43  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.3  2004/02/11 21:13:56  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Extracted journaling to the method journal().
 *
 *   Revision 1.2  2004/02/11 20:47:32  dcobb
 *   @scr 3381 Feature Enhancement:  Till Pickup and Loan
 *   Journal the nationality of alternate tender
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 28 2004 17:34:14   DCobb
 * Added Pickup and Loan parameters.
 * Resolution for 3381: Feature Enhancement:  Till Pickup and Loan
 *
 *    Rev 1.1   Jan 21 2004 16:19:28   DCobb
 * Set journalEndOfTransaction=false in call to utility.saveTransaction() methos.
 * Resolution for 3701: Timing problem can occur in CancelTransactionSite (multiple).
 *
 *    Rev 1.0   Aug 29 2003 15:58:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Sep 03 2002 16:03:50   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:26:32   msg
 * Initial revision.
 *
 *    Rev 1.2   25 Mar 2002 12:33:46   epd
 * Jose asked me to check these in.  Updates to use TenderDescriptor
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.9   02 Mar 2002 12:48:06   pdd
 * Converted to use TenderTypeMapIfc.
 * Resolution for POS SCR-627: Make the Tender type list extendible.
 *
 *    Rev 1.8   16 Feb 2002 14:31:22   epd
 * Updated to send countryCode to getAmount method
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.7   08 Feb 2002 16:00:54   epd
 * removed dashed line from ejournal
 * Resolution for POS SCR-732: Till pickup - ejournal entry has a broken line under 'Pickup Count' - should not be there
 *
 *    Rev 1.6   29 Jan 2002 09:54:22   epd
 * Deprecated all methods using accumulate parameter and added new methods without this parameter.  Also removed all reference to the parameter wherever used.
 * (The behavior is to accumulate totals)
 * Resolution for POS SCR-770: Remove the accumulate parameter and all references to it.
 *
 *    Rev 1.5   05 Dec 2001 16:26:28   epd
 * fixed expected count
 * Resolution for POS SCR-95: Till pickup - receipt for check detail is incorrect
 *
 *    Rev 1.4   05 Dec 2001 16:01:22   epd
 * fixed bug in output of pickup amount
 * Resolution for POS SCR-92: Ejournal - Till Pickup entry incorrect
 *
 *    Rev 1.3   04 Dec 2001 12:42:00   epd
 * Added code to update store safe
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.2   20 Nov 2001 16:19:02   epd
 * creating transaction up front
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *
 *    Rev 1.1   12 Nov 2001 17:19:12   epd
 * Added line to save Till ID in transaction
 * Resolution for POS SCR-280: Till status save incomplete
 *
 *    Rev 1.0   Sep 21 2001 11:19:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:14:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillpickup;

import java.util.Locale;

import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
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
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Updates till status.
 */
public class UpdateStatusSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 3327261317895538459L;

    public static final String SITENAME = "UpdateStatusSite";

    /**
     * Updates till status.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TillPickupCargo cargo = (TillPickupCargo)bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.SUCCESS);
        boolean saveTransSuccess = true;
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        // Local copy of register. Update if everything ok
        RegisterIfc register = (RegisterIfc)cargo.getRegister().clone();
        TillIfc till = register.getTillByID(cargo.getTillID());

        // tender descriptor for current pickup
        TenderDescriptorIfc td = DomainGateway.getFactory().getTenderDescriptorInstance();
        td.setCountryCode(cargo.getTenderNationality());
        td.setTenderType(tenderTypeMap.getTypeFromDescriptor(cargo.getTenderName()));
        CurrencyIfc altCurrency = DomainGateway.getAlternateCurrencyInstance(cargo.getTenderNationality());
        td.setCurrencyID(altCurrency.getType().getCurrencyId());

        FinancialCountTenderItemIfc originalCount = till.getTotals().
                                                         getCombinedCount().
                                                         getExpected().
                                                         getSummaryTenderItemByDescriptor(td);
        // Temporary till in cargo has the updated financial totals
        // Need to copy them to the actual register's till.
        TillIfc t = cargo.getTill();
        // setTotals
        t.getTotals().addCountTillPickups(1);
        till.addTotals(t.getTotals());
        // Set register in cargo
        cargo.setRegister(register);
        // Set updated information in transaction
        TillAdjustmentTransactionIfc transaction = cargo.getTransaction();
        transaction.setTimestampEnd();
        transaction.setTender(td);
        //transaction.setTenderType(cargo.getTenderName());
        transaction.setTillID(cargo.getTillID());
        StoreSafeIfc safe = DomainGateway.getFactory().getStoreSafeInstance();
        safe.setBusinessDay(cargo.getStoreStatus().getBusinessDate());
        safe.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
        safe.setValidTenderDescList(cargo.getStoreStatus().getSafeTenderTypeDescList());

        if (cargo.getPickupCountType() != FinancialCountIfc.COUNT_TYPE_NONE ||
        cargo.getTenderName().equals(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)))
        {


            FinancialTotalsIfc pickupTotals = cargo.getPickupTotals();
            ReconcilableCountIfc [] tillPickups = pickupTotals.getTillPickups();
            FinancialCountIfc fc = tillPickups[tillPickups.length-1].getEntered();

            transaction.setTenderCount(fc);

            transaction.setCountType(cargo.getPickupCountType());
            // we need to pull country code from one of the tender items
            FinancialCountTenderItemIfc[] fcti = fc.getTenderItems();
            String countryCode = fcti[0].getTenderDescriptor().getCountryCode();
            transaction.setAdjustmentAmount(fc.getAmount(countryCode));
            // update safe with count
            safe.addPickupCount(fc);


            if (originalCount != null)
            {
                transaction.setExpectedAmount(originalCount.getAmountTotal().negate());
                transaction.setExpectedCount(originalCount.getNumberItemsTotal() * -1);
            }
            else
            {
                transaction.setExpectedAmount(DomainGateway.getBaseCurrencyInstance());
                transaction.setExpectedCount(0);
            }
        }
        else
        {
        	CurrencyIfc zeroAdjustment = DomainGateway.getAlternateCurrencyInstance(cargo.getTenderNationality());
            transaction.setAdjustmentAmount(zeroAdjustment);
        }
        transaction.setAdjustmentCount(1); // always 1
        cargo.setTransaction(transaction);

        TransactionUtilityManagerIfc tutility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        try
        {
            //
            // Save this transaction seperately since it doesn't determine
            // the success or failure of closing the till
            //
            // Pass the transaction, the totals object, the till,
            // register, and false indicating that the transaction has
            // not completed its journaling yet.
            tutility.saveTransaction(transaction, t.getTotals(), till, register, false);
            // set flag to reprint ID
            cargo.setLastReprintableTransactionID(transaction.getTransactionID());
            // update safe totals in database
            StoreSafeWriteDataTransaction safeTransaction = null;

            safeTransaction = (StoreSafeWriteDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_SAFE_WRITE_DATA_TRANSACTION);

            if(!transaction.isTrainingMode())
            {
                safeTransaction.updateStoreSafeTotals(safe);
            }
        }
        catch (DataException e)
        {
            logger.error( e.toString());
            if (e.getErrorCode() == DataException.QUEUE_FULL_ERROR ||
        			e.getErrorCode() == DataException.STORAGE_SPACE_ERROR ||
        			e.getErrorCode() == DataException.QUEUE_OP_FAILED)
        	{
        		saveTransSuccess = false;
        		DialogBeanModel dialogModel = utility.createErrorDialogBeanModel(e, false);
                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
        	}
        	else
        	{
                // set error code
                letter = new Letter (CommonLetterIfc.UPDATE_ERROR);
        	}
        }

        // journal the transaction
        if (saveTransSuccess && letter.getName().equals(CommonLetterIfc.SUCCESS))
        {
            journal(bus, cargo, utility, transaction);
        }
        if (saveTransSuccess)
        {
        	bus.mail(letter, BusIfc.CURRENT);
        }
    }

    /**
     * Journals the pickup transaction.
     * 
     * @param jmi The journal manager
     * @param cargo The till pickup cargo
     * @param utility The utility manager
     * @param transaction The till pickup transaction
     */
    private void journal(BusIfc bus,
                         TillPickupCargo cargo,
                         UtilityManagerIfc utility,
                         TillAdjustmentTransactionIfc transaction)
    {
        // get the Journal manager
        JournalManagerIfc jmi = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
        // journal the till status
        TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        StringBuilder sb = new StringBuilder();
        Object[] dataArgs = new Object[2];
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

        CurrencyIfc baseCurrency = DomainGateway.getBaseCurrencyInstance();


        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PICKUP_COUNT_LABEL, null)).append(Util.EOL).append(Util.EOL);
        if ( (cargo.getPickupCountType() == FinancialCountIfc.COUNT_TYPE_NONE ||
                cargo.getPickupCountType() == FinancialCountIfc.COUNT_TYPE_SUMMARY) &&
                !cargo.getTenderName().equals(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)))
        {
            // check for alternate tender
            String nationality = cargo.getTenderNationality();
            if (!baseCurrency.getCountryCode().equals(nationality))
            {
                sb.append(utility.retrieveCommonText(nationality+"_Nationality",nationality, locale));
                sb.append(" ");
            }

            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+cargo.getTenderName(),null));
            sb.append(Util.EOL).append(Util.EOL);
        }
        dataArgs[0] = cargo.getTillID();
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
				JournalConstantsIfc.TILL_ID_LABEL, dataArgs));
        sb.append(Util.EOL);

        // Do the following if pickup count type is not set to none
        // or the tender type is 'Check'
        if (cargo.getPickupCountType() != FinancialCountIfc.COUNT_TYPE_NONE ||
                cargo.getTenderName().equals(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)))
        {
            // check to see if TillCountTillPickup parameter is set to Detail
            // journal detail pickup info if true.
            FinancialTotalsIfc pickupTotals = cargo.getPickupTotals();
            ReconcilableCountIfc [] tillPickups = pickupTotals.getTillPickups();
            FinancialCountIfc fc = tillPickups[tillPickups.length-1].getEntered();
            FinancialCountTenderItemIfc[] fcti =  fc.getTenderItems();
            String countryCode = fcti[0].getTenderDescriptor().getCountryCode();

            if (cargo.getPickupCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL ||
                    cargo.getTenderName().equals(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)))
            {

            	sb.append(Util.EOL);

                for (int i = 0; i < fcti.length; i++)
                {
                    if (fcti[i].isSummary() == false)
                    {
                        String desc = fcti[i].getDescription();
                        int num = fcti[i].getNumberItemsOut();
                        CurrencyIfc amount = fcti[i].getAmountOut();
                        if (desc.indexOf(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)) != -1)
                        {
                            String i18nDesc = I18NHelper.getString(I18NConstantsIfc.COMMON_TYPE, "Common."+desc,null);
                            if (!countryCode.equals(baseCurrency.getCountryCode()))
                            {
                                dataArgs = new Object []{i18nDesc,amount.negate().toISOFormattedString()};
                                String trCurrDesc =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_CURR_DETAIL,dataArgs);
                                sb.append(trCurrDesc);
                            }
                            else
                            {
                                dataArgs = new Object []{i18nDesc,amount.negate().toFormattedString()};
                                String trCurrDesc =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_CURR_DETAIL,dataArgs);
                                sb.append(trCurrDesc);
                            }
                            sb.append(Util.EOL);
                        }
                        else
                        {
                        	String i18nDesc = fcti[i].getTenderDescriptor().getDenomination().getDenominationDisplayName(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
                        	dataArgs = new Object []{i18nDesc,num};
                        	String trCurrDesc =I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TILL_CURR_DETAIL,dataArgs);
                        	sb.append(trCurrDesc);
                            sb.append(Util.EOL);
                        }
                    }
                }
            }

            CurrencyIfc tillPickupAmt = fc.getAmount(countryCode);
            String formattedTillPickupAmt = tillPickupAmt.toFormattedString();
            String formattedExpectedAmt = transaction.getExpectedAmount().toFormattedString();
            if (!countryCode.equals(baseCurrency.getCountryCode()))
            {
                // alternate currency, display ISO code
                formattedTillPickupAmt =  tillPickupAmt.toISOFormattedString();
                formattedExpectedAmt = transaction.getExpectedAmount().toISOFormattedString();
            }
            if (cargo.getTenderName().equals(tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)) &&
                    cargo.getPickupCountType() == FinancialCountIfc.COUNT_TYPE_DETAIL)
            {
            	sb.append(Util.EOL);
            	dataArgs[0] = formattedTillPickupAmt;
                sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_PICKUP_ENTERED_LABEL, dataArgs));
                sb.append(Util.EOL);
                dataArgs[0] = formattedExpectedAmt;
                sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_PICKUP_EXPECTED_LABEL, dataArgs));
            }
            else
            {
            	dataArgs[0] = formattedTillPickupAmt;
            	sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_PICKUP_LABEL, dataArgs));
            }
        }

        String toRegister = transaction.getToRegister();
        if (!Util.isEmpty(toRegister))
        {
        	dataArgs[0] = toRegister;
        	sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TO_REGISTER_LABEL, dataArgs));
        }

        jmi.journal(sb.toString());
        TransactionUtilityManagerIfc tutility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);
        tutility.completeTransactionJournaling(transaction);
    }
}