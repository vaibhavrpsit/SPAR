/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/TenderFormatter.java /main/40 2013/11/22 17:47:22 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   11/22/13 - Not including card sub-type in debit e-journal. Not
 *                         applicable. (it's logged as null or Unknown)
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
 *    jswan     10/02/13 - Modified to fix I18N for Entry Method.
 *    blarsen   04/05/13 - Fixing null ptr exception in AJB debit edge case.
 *                         Using !isEmpty() insted of != null.
 *    icole     03/06/13 - Print Trace Number on Debit receipt if exists, else
 *                         print System Audit Tranc Number if exists per ACI's
 *                         requirements.
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   10/21/11 - added missing debit type and auth amount to journal
 *    sgu       10/11/11 - remove expiration date journal entry for debit
 *                         tender
 *    sgu       10/11/11 - remove the journaling of expiration date for HA
 *                         tender
 *    asinton   09/28/11 - don't print gift card balance when in transaction
 *                         reentry.
 *    cgreene   09/14/11 - change to use Stringbuilder
 *    cgreene   09/12/11 - revert aba number encryption, which is not sensitive
 *    icole     09/08/11 - Correct check ejournal entry for swipped drivers
 *                         license.
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   08/25/11 - defect# 510.'POS - Check - data missing from EJ'
 *    ohorne    08/24/11 - Personal Id and MICR entry method fix
 *    ohorne    08/18/11 - APF: check cleanup
 *    mjwallac  08/02/11 - Fix ClassCastException in commonCertificateInfo()
 *    ohorne    07/14/11 - fix for ClassCastException in formatCheckInfo()
 *    blarsen   07/12/11 - Added support for REVERSAL journal entries. (Today
 *                         they are the same as VOIDs. This might change in the
 *                         future.)
 *    cgreene   07/12/11 - update generics
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    blarsen   05/24/11 - Removed dependency on expiry. Expiry is no longer
 *                         returned by payment service.
 *    sgu       02/03/11 - check in all
 *    npoola    11/29/10 - added the new constant AUTHORIZATION_DECLINE for
 *                         offline authorization scenario
 *    asinton   09/22/10 - Adding Credit Card Accountability Responsibility and
 *                         Disclosure Act of 2009 changes.
 *    dwfung    09/14/10 - fixed sign of voiding till txn when journaling
 *    asinton   09/08/10 - modified the journaling of voided credit tender
 *                         authorization since authorization is now done
 *                         asynchronously
 *    acadar    06/10/10 - use default locale for currency display
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   05/06/10 - Added Prepaid Remaining Balance to receipt and
 *                         ejournal
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    yiqzhao   03/17/10 - negative in void tender journal
 *    yiqzhao   03/17/10 - fix negative format problem
 *    crain     03/05/10 - Replace CHANGE_DUE
 *    sbeesnal  01/28/10 - Added NULL check to pass the JUnit tests.
 *    abondala  01/03/10 - update header date
 *    nganesh   04/10/09 - Masking the experi date in EJ if it is Housecard
 *                         tender type
 *    nganesh   03/31/09 - Modified to mask account number in EJournal
 *    nganesh   03/14/09 - store credit EJ to have Localized customer id
 *                         information
 *    vchengeg  01/07/09 - EJ defect fixes
 *    vchengeg  12/27/08 - ej defect fixes
 *    vchengeg  12/16/08 - ej defect fixes
 *    vchengeg  12/10/08 - EJ defect fixes
 *    tzgarba   11/05/08 - Merged with tip
 *    sswamygo  11/05/08 - Checkin after merges
 *
 * ===========================================================================
 * $Log:
 28   360Commerce 1.27        2/27/2008 3:19:23 PM   Alan N. Sinton  CR 29989:
 Changed masked to truncated for UI renders of PAN.
 27   360Commerce 1.26        2/4/2008 2:29:51 PM    Alan N. Sinton  CR 30125:
 Make journaling of credit and debit account information dependant on
 EncipheredCardDataIfc and not MSRModelIfc.  Reason: MSRModelIfc is not
 available when card number is manually entered.  Code changes were
 reviewed by Michael Barnett.
 26   360Commerce 1.25        12/5/2007 4:33:13 PM   Michael P. Barnett In
 formatDebitInfo(), check for null reference.
 25   360Commerce 1.24        11/21/2007 1:55:42 AM  Deepti Sharma   CR 29598:
 changes for credit/debit PABP
 24   360Commerce 1.23        8/24/2007 3:12:11 PM   Ranjan X Ojha   Removed
 GC Available Balance from Ejournal while GC Tendered.
 23   360Commerce 1.22        8/23/2007 4:49:19 PM   Maisa De Camargo CR 28344
 - Updated Exchange Rate precision to 6 decimals.
 22   360Commerce 1.21        8/9/2007 5:40:49 PM    Ranjan X Ojha   Removed
 Original Balance from EJournal of GiftCard and Changed Remaining
 Balance to GC Available Balance.
 21   360Commerce 1.20        8/6/2007 3:50:00 PM    Mathews Kochummen fix
 journalling of response when offline
 20   360Commerce 1.19        7/18/2007 8:43:35 AM   Alan N. Sinton  CR 27651
 - Made Post Void EJournal entries VAT compliant.
 19   360Commerce 1.18        7/5/2007 5:44:50 PM    Anda D. Cadar   call
 DomainGateway,getBaseCurrencyInstance to get a currency instance, then
 call toGroupFormattedString on it so it is properly aligned
 18   360Commerce 1.17        7/3/2007 12:31:55 PM   Anda D. Cadar   Created a
 getter for the journalLocale
 17   360Commerce 1.16        7/2/2007 3:50:40 PM    Anda D. Cadar   format
 the amounts in EJ
 16   360Commerce 1.15        7/2/2007 10:10:46 AM   Ashok.Mondal    CR 27448
 :Display the formatted authorized amount on eJournal.
 15   360Commerce 1.14        6/28/2007 5:18:21 PM   Charles D. Baker CR 27166
 - Updated to make use of alignment provided by
 CurrencyIfc.toGroupFormattedString()
 14   360Commerce 1.13        6/27/2007 6:22:11 AM   Manikandan Chellapan
 Price String hada space, trimmed.
 13   360Commerce 1.12        5/21/2007 9:16:18 AM   Anda D. Cadar   EJ
 changes
 12   360Commerce 1.11        4/25/2007 8:52:56 AM   Anda D. Cadar   I18N
 merge
 *
 11   360Commerce 1.10        4/17/2007 10:45:13 AM  Ashok.Mondal    CR 5810
 :V7.2.2 merge to trunk.
 10   360Commerce 1.9         2/6/2007 2:13:58 PM    Edward B. Thorne Merge
 from TenderFormatter.java, Revision 1.7.1.0
 9    360Commerce 1.8         12/5/2006 11:31:09 AM  Brendan W. Farrell Fix
 merged code that caused a crash.
 8    360Commerce 1.7         10/5/2006 10:33:01 AM  Keith L. Lesikar Merge
 fix from BBY. Do not display null values for gift card amounts within
 EJ.
 7    360Commerce 1.6         7/24/2006 7:55:54 PM   Rohit Sachdeva  16032:
 Code Merged from v7x Star Team view
 6    360Commerce 1.5         3/6/2006 4:09:53 AM    Akhilashwar K. Gupta
 CR-8235: Modified formatAuthorizationInfo() method to not save
 following information in E-Journal-
  Auth. Status: Approved
  Auth. Response: Approved
  Auth. Amt: 5.00
 5    360Commerce 1.4         2/17/2006 2:52:56 AM   Akhilashwar K. Gupta
 Modified for CR 6084
 4    360Commerce 1.3         12/13/2005 4:42:31 PM  Barry A. Pape
 Base-lining of 7.1_LA
 3    360Commerce 1.2         3/31/2005 4:30:24 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:25:57 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:14:52 PM  Robert Pearse
 *
 Revision 1.24.2.2  2004/11/12 23:18:36  cdb
 @scr 5084 Updated Journaling and Redeedm Gift Cert Receipt.
 Revision 1.24.2.1  2004/10/15 18:50:26  kmcbride
 Merging in trunk changes that occurred during branching activity
 Revision 1.25  2004/10/07 18:56:00  bwf
 @scr 7314, 7315 Cash is not change, but a refund in return and redeem.
 Revision 1.24  2004/08/12 21:00:41  crain
 @scr 5084 Tender Redeem_Missing Data Elements for Gift Cert Redeem Data Input/Output
 Revision 1.23  2004/07/30 20:10:45  lzhao
 @scr 6629: add initial/current balance and entry type in journal.
 Revision 1.22  2004/07/17 18:43:17  bwf
 @scr 5944 Only journal last 4 digits of debit card.
 Revision 1.21  2004/07/16 01:11:55  jdeleau
 @scr 5446 Correct the way phone numbers are sent to e-journal for
 mail bank checks, remove the use of deprecated constants.
 Revision 1.20  2004/06/29 22:32:58  crain
 @scr 5847 Foreign Currency: Currency type not printing to EJ
 Revision 1.19  2004/06/24 19:33:12  bwf
 @scr 5743 Fixed journaling of void transactions.
 Revision 1.18  2004/06/24 15:31:38  blj
 @scr 5185 - Had to update gift card credit to get Amount from the tenderAttributes
 Revision 1.17  2004/06/02 04:05:19  blj
 @scr 4529 - resolution to customer id printing issues
 Revision 1.16  2004/05/26 23:09:03  crain
 @scr 5062 Purchase Order- Taxable status missing from journal when agency is other/business
 Revision 1.15  2004/05/21 18:52:36  kll
 @scr 5036: Pending status if Authorization has yet to occur
 Revision 1.14  2004/05/20 22:54:58  cdb
 @scr 4204 Removed tabs from code base again.
 Revision 1.13  2004/05/20 18:59:24  jeffp
 @scr 4178 - added check to see if agency name is null
 Revision 1.12  2004/05/17 22:02:13  blj
 @scr 5035 - resolutioni-printed last 4 in ejournal
 Revision 1.11  2004/05/14 19:04:33  lzhao
 @scr 4553: Redeem Journal
 Revision 1.10  2004/04/27 20:50:13  epd
 @scr 4513 Fixes for printing and journalling when forced cash change is present
 Revision 1.9  2004/04/22 20:52:19  epd
 @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 Revision 1.8  2004/04/08 20:33:02  cdb
 @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * Rev 1.12 Jan 06 2004 16:25:30 DCobb Corrected oveeride journal string.
 * Resolution for 3614: E-journal is reversing the Manager Override to/for
 * information when Override Tenders is executed.
 *
 * Rev 1.11 Dec 18 2003 21:11:24 crain Added PO info Resolution for 3421:
 * Tender redesign
 *
 * Rev 1.10 Dec 18 2003 19:46:50 blj fixed flow issues and removed debug
 * statements
 *
 * Rev 1.9 Dec 11 2003 13:06:28 bwf Update for mall gift certificate.
 * Resolution for 3538: Mall Certificate Tender
 *
 * Rev 1.8 Dec 08 2003 16:29:08 bwf Updated for code review.
 *
 * Rev 1.7 Dec 04 2003 17:46:28 epd Updates for debit auth
 *
 * Rev 1.6 Nov 25 2003 18:15:38 blj giftcard functional testing resolutions
 *
 * Rev 1.5 Nov 25 2003 10:41:56 cdb Modified to use TenderLineItemIfc for entry
 * type - Auto implies Scanned. Resolution for 3421: Tender redesign
 *
 * Rev 1.4 Nov 24 2003 17:43:22 cdb Updated to track entry method for gift
 * certificate tendering. Resolution for 3421: Tender redesign
 *
 * Rev 1.3 Nov 21 2003 15:22:56 crain Check for null Resolution for 3421:
 * Tender redesign
 *
 * Rev 1.2 Nov 20 2003 16:21:20 crain Added gift certificate Resolution for
 * 3421: Tender redesign
 *
 * Rev 1.1 Nov 13 2003 16:17:28 bwf Added check journaling. Resolution for
 * 3429: Check/ECheck Tender
 *
 * Rev 1.0 Nov 04 2003 11:11:16 epd Initial revision.
 *
 * Rev 1.7 Nov 01 2003 15:08:44 epd dev updates
 *
 * Rev 1.6 Oct 30 2003 20:33:14 epd added method to make sure authorization of
 * tenders is journalled
 *
 * Rev 1.5 Oct 30 2003 12:50:28 crain Added coupon Resolution for 3421: Tender
 * redesign
 *
 * Rev 1.4 Oct 27 2003 18:23:32 epd Added code for Credit tender
 *
 * Rev 1.3 Oct 24 2003 15:03:14 bwf Return removed import. Resolution for 3418:
 * Purchase Order Tender Refactor
 *
 * Rev 1.2 Oct 24 2003 14:48:32 bwf Add journaling for PO. Resolution for 3418:
 * Purchase Order Tender Refactor
 *
 * Rev 1.1 Oct 21 2003 17:43:08 epd added journalling of deleted tenders
 *
 * Rev 1.0 Oct 17 2003 12:31:24 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCode;
import oracle.retail.stores.common.utility.StringUtils;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.Role;
import oracle.retail.stores.domain.tender.AuthorizableTenderIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditConstantsIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.pos.ado.tender.AuthorizableADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderCashADO;
import oracle.retail.stores.pos.ado.tender.TenderCheckADO;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderDebitADO;
import oracle.retail.stores.pos.ado.tender.TenderGiftCardADO;
import oracle.retail.stores.pos.ado.tender.TenderMallCertificateADO;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.transaction.TransactionPrototypeEnum;
import oracle.retail.stores.pos.ado.transaction.VoidTransactionADO;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * Journal formatter for tender.
 */
public class TenderFormatter implements RegisterJournalFormatterIfc
{
    protected static final String TENDERED = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TENDERED_LABEL, null,getJournalLocale());
    protected static final String ISSUED = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ISSUED_LABEL, null,getJournalLocale());
    protected static final String REDEEMED = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REDEEMED_LABEL, null,getJournalLocale());
    protected static final String REVERSED = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.REVERSED_LABEL, null,getJournalLocale());
    protected static final String AUTHORIZED = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZED_LABEL, null,getJournalLocale());
    protected static final String DELETED = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DELETED_LABEL, null,getJournalLocale());
    protected static final String DECLINED = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DECLINED_LABEL, null,getJournalLocale());

    /**
     * Currency Service
     */
    protected static CurrencyServiceIfc currencyService = CurrencyServiceLocator.getCurrencyService();

    /**
     * Locale
     */
    protected static Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);

    /**
     * Handle to the ParameterManagerIfc.
     */
    protected ParameterManagerIfc parameterManager;

    /** Tender action String */
    protected String tenderAction;

    /**
     * Gets the locale used for Journaling
     * @return
     */
    public static Locale getJournalLocale()
    {
        // attempt to get instance
        if (journalLocale == null)
        {
            journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
        }


        return journalLocale;
    }

    /* non-Javadoc)
     * @see oracle.retail.stores.ado.journal.RegisterJournalFormatterIfc#format(oracle.retail.stores.ado.journal.JournalTemplateIfc,
     *      oracle.retail.stores.ado.journal.JournalableADOIfc,
     *      oracle.retail.stores.ado.journal.JournalActionEnum)
     */
    public String format(JournalTemplateIfc template, JournalableADOIfc journalable, JournalActionEnum action)
    {
        StringBuilder sb = new StringBuilder(256);

        if (action == JournalActionEnum.ADD)
        {
            // For adding a tender, the tender action is "Tendered"
            tenderAction = TENDERED;
            if (((TenderADOIfc)journalable).getAmount().signum() == CurrencyIfc.NEGATIVE)
            {
                tenderAction = ISSUED;
            }
            formatTender(template, journalable, sb);
        }
        else if (action == JournalActionEnum.REDEEM)
        {
            tenderAction = REDEEMED;
            formatTender(template, journalable, sb);
        }
        else if (action == JournalActionEnum.VOID || action == JournalActionEnum.REVERSAL)
        {
            // For adding a voided tender, the tender action is "Reversed"
            tenderAction = REVERSED;
            formatTender(template, journalable, sb);
        }
        else if (action == JournalActionEnum.DELETE)
        {
            // For adding a voided tender, the tender action is "Reversed"
            tenderAction = DELETED;
            formatTender(template, journalable, sb);
        }
        else if (action == JournalActionEnum.VOID_TOTAL)
        {
            formatVoidTenderTotal(template, journalable, sb);
        }
        else if (action == JournalActionEnum.OVERRIDE)
        {
            formatOverride(template, journalable, sb);
        }
        else if (action == JournalActionEnum.AUTHORIZATION)
        {
            tenderAction = AUTHORIZED;
            formatTender(template, journalable, sb);
        }
        else if (action == JournalActionEnum.AUTHORIZATION_DECLINED)
        {
            tenderAction = DECLINED;
            formatTender(template, journalable, sb);
        }

        return sb.toString();
    }

    /**
     * Journals a tender that's been added to a transaction
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatTender(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        // In this case, we just need to journal the tender itself
        assert (journalable instanceof TenderADOIfc);

        TenderADOIfc tender = (TenderADOIfc) journalable;

        formatGeneralTenderInfo(template, tender, sb);

        // format journal string based on tender type
        if (tender.getTenderType() == TenderTypeEnum.CASH)
        {} // Nothing extra do to for cash.
        else if (tender.getTenderType() == TenderTypeEnum.CREDIT)
        {
            formatCreditInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.TRAVELERS_CHECK)
        {
            formatTravelersCheckInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.PURCHASE_ORDER)
        {
            formatPurchaseOrderInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.COUPON)
        {
            formatCouponInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.GIFT_CARD)
        {
            formatGiftCardInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.GIFT_CERT)
        {
            formatGiftCertificateInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.MALL_CERT)
        {
            formatMallCertificateInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.DEBIT)
        {
            formatDebitInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.CHECK)
        {
            formatCheckInfo(template, journalable, sb);
        }
        else if (tender.getTenderType() == TenderTypeEnum.STORE_CREDIT)
        {
            TenderStoreCreditADO tscADO = (TenderStoreCreditADO) tender;
            TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) tscADO.toLegacy();

            // if Issue Store Credit, then do nothing
            if (tsc != null && tsc.getState() == TenderStoreCreditConstantsIfc.ISSUE)
            {
                // do nothing -- Issue Store Credit is being journalled
                // elsewhere
            }
            else
            {
                formatStoreCreditInfo(template, journalable, sb);
            }
        }
        else if (tender.getTenderType() == TenderTypeEnum.MAIL_CHECK)
        {
            formatMailCheckInfo(template, journalable, sb);
        }

        // Journal authorization information if instance of AuthorizableADOIfc
        // and is not a voided credit, voided credits are authorized asynchronously
        if (journalable instanceof AuthorizableADOIfc && !isVoidedCredit(journalable))
        {
            formatAuthorizationInfo(template, journalable, sb);
        }
    }

    /**
     * Returns true iff journalable is an instanceo of TenderCreditADO and isVoided is true.
     * @param journalable
     * @return True iff journalable is an instanceo of TenderCreditADO and isVoided is true, false otherwise.
     */
    protected boolean isVoidedCredit(JournalableADOIfc journalable)
    {
        boolean isVoidedCredit = false;
        if(journalable instanceof TenderCreditADO)
        {
            isVoidedCredit = ((TenderCreditADO)journalable).isVoided();
        }
        return isVoidedCredit;
    }
    /**
     * All tenders, when journalled, journal this information
     *
     * @param template
     * @param cashTender
     * @param sb
     */
    protected void formatGeneralTenderInfo(JournalTemplateIfc template, TenderADOIfc tender, StringBuilder sb)
    {
        Map<String,Object> journalMemento = tender.getJournalMemento();

        TenderStoreCreditADO tscADO = null;
        TenderStoreCreditIfc tsc = null;

        // check type of transaction
        if (tender.getTenderType() == TenderTypeEnum.STORE_CREDIT)
        {
            tscADO = (TenderStoreCreditADO) tender;
            tsc = (TenderStoreCreditIfc) tscADO.toLegacy();
        }

        // if Issue Store Credit, then do nothing
        if (tsc != null && tsc.getState() == TenderStoreCreditConstantsIfc.ISSUE)
        {
            // do nothing -- Issue Store Credit is being journalled elsewhere
        }
        else if (tender.getTenderType() == TenderTypeEnum.CASH &&
                tenderAction.equals(ISSUED)  &&
                !((TenderCashADO)tender).isRefundCash())
        {

            // do nothing -- Issued Cash will be counted as part of Change due
        }
        else
        {
            sb.append(template.getEndOfLine());

            String descriptor =JournalConstantsIfc.JOURNAL_ENTRY_PREFIX +(String) journalMemento.get(JournalConstants.DESCRIPTOR);
            String descriptorI18n = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, descriptor,null,journalLocale);
            String amountString = tender.getAmount().toGroupFormattedString();
            Object[] dataArgs = new Object[]{descriptorI18n,this.tenderAction,amountString};
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TENDER_DESC_ACTION_AMOUNT_LABEL,dataArgs,journalLocale));

            if (tender.toLegacy() instanceof TenderAlternateCurrencyIfc)
            {
                CurrencyIfc foreignAmount = ((TenderAlternateCurrencyIfc)tender.toLegacy()).getAlternateCurrencyTendered();
                if (foreignAmount != null)
                {
                    sb.append(template.getEndOfLine());
                    String currencyDesc = I18NHelper.getString(I18NConstantsIfc.COMMON_TYPE, "Common."+foreignAmount.getCurrencyCode(),null);

                    dataArgs = new Object[]{currencyDesc,foreignAmount.toGroupFormattedString()};
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AMT_RECD_LABEL,dataArgs,journalLocale));

                    sb.append(template.getEndOfLine());

                    dataArgs = new Object[]{new DecimalFormat("0.000000").format(foreignAmount.getBaseConversionRate().doubleValue())};
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXCHANGE_RATE_LABEL,dataArgs,journalLocale));

                }
            }
        }
    }

    /**
     * Formats Credit specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatCreditInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Object[] dataArgs;
        Map<String,Object> memento = journalable.getJournalMemento();
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)memento.get(TenderConstants.ENCIPHERED_CARD_DATA);
        if(cardData != null)
        {
            dataArgs = new Object[]{(String) memento.get(JournalConstants.CARD_TYPE)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TYPE_LABEL,dataArgs,journalLocale));

            String truncatedCardNumber = cardData.getTruncatedAcctNumber();
            if (StringUtils.isNotEmpty(truncatedCardNumber))
            {
                int len = truncatedCardNumber.length();
                String lastFourCreditCardNum = truncatedCardNumber.substring(len-4, len);
                dataArgs = new Object[]{lastFourCreditCardNum};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ACCOUNT_NUMBER_LABEL,dataArgs,journalLocale));
            }
        }

        if (memento.get(JournalConstants.ENTRY_METHOD) != null)
        {
            sb.append(Util.EOL);
            EntryMethod method = (EntryMethod)memento.get(JournalConstants.ENTRY_METHOD);
            dataArgs = getI18NJournalTenderEntryMethod(method);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_LABEL,dataArgs,journalLocale));
        }

        // test to see if authorization response has been received
        if(!((AuthorizableADOIfc) journalable).isAuthorized() && (tenderAction != DELETED))
        {
            dataArgs = new Object[]{I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PENDING_LABEL,null,journalLocale)};
            sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_STATUS_LABEL,dataArgs,journalLocale));
        }
        else if(isVoidedCredit(journalable))
        {
            dataArgs = new Object[]{I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZED_LABEL,null,journalLocale)};
            sb.append(Util.EOL).append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_STATUS_LABEL,dataArgs,journalLocale));
        }

    }

    /**
     * Formats Debit specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatDebitInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        EncipheredCardDataIfc cardData = (EncipheredCardDataIfc)memento.get(TenderConstants.ENCIPHERED_CARD_DATA);
        if (cardData != null)
        {
            String truncatedCardNumber = cardData.getTruncatedAcctNumber();

            String lastFourCreditCardNum = "";
            if (!Util.isEmpty(truncatedCardNumber))
            {
                int len = truncatedCardNumber.length();
                lastFourCreditCardNum = truncatedCardNumber.substring(len-4, len);
            }

            Object[] dataArgs = new Object[]{lastFourCreditCardNum};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ACCOUNT_NUMBER_LABEL,dataArgs,journalLocale));

            if (memento.get(JournalConstants.ENTRY_METHOD) != null)
            {
                sb.append(Util.EOL);
                EntryMethod method = (EntryMethod)memento.get(JournalConstants.ENTRY_METHOD);
                dataArgs = getI18NJournalTenderEntryMethod(method);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_LABEL,dataArgs,journalLocale));
            }

            if (memento.get(TenderConstants.TRACE_NUMBER) != null)
            {
                String traceNumber = (String)memento.get(TenderConstants.TRACE_NUMBER);
                dataArgs = new Object[] { traceNumber };
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRACE_NUMBER_LABEL, dataArgs, journalLocale));
            }
        }
    }

    /**
     * Formats Traveler Check specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatTravelersCheckInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Object [] dataArgs;
        dataArgs = new Object[]{((Short) journalable.getJournalMemento().get(TenderConstants.COUNT)).toString()};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.QUANTITY_OF_CHECKS_LABEL,dataArgs,journalLocale));
        sb.append(Util.EOL);
    }

    /**
     * Formats Purchase Order specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatPurchaseOrderInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Object [] dataArgs;
        Map<String,Object> memento = journalable.getJournalMemento();
        String agencyName = (String) memento.get(TenderConstants.AGENCY_NAME);

        if (agencyName == null)
        {
            agencyName = "";
        }

        sb.append(Util.EOL).append(agencyName);

        String faceValueAmount = (String) memento.get(TenderConstants.FACE_VALUE_AMOUNT);
        CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance();
        amt.setStringValue(faceValueAmount);
        String formattedFaceValueAmount = amt.toGroupFormattedString();

        dataArgs = new Object[]{formattedFaceValueAmount};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PO_TENDER_AMOUNT_LABEL,dataArgs,journalLocale));


        sb.append(Util.EOL).append("# ").append(((String) memento.get(TenderConstants.NUMBER)));

        dataArgs = new Object[]{(String) memento.get(TenderConstants.TAXABLE_STATUS)};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_STATUS_LABEL,dataArgs,journalLocale));

        sb.append(Util.EOL);
    }

    /**
     * Formats coupon specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatCouponInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object[] dataArgs;
        dataArgs = new Object[]{((String) memento.get(TenderConstants.COUPON_NUMBER))};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NUMBER_LABEL,dataArgs,journalLocale));
        sb.append(Util.EOL);
    }

    /**
     * Formats check specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatCheckInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object[] dataArgs;
        String maskedNumber = "";

        EncipheredDataIfc accountNumber = (EncipheredDataIfc)memento.get(TenderConstants.ENCIPHERED_DATA_ACCOUNT_NUMBER);
        if (accountNumber != null)
        {
            maskedNumber = accountNumber.getMaskedNumber();
        }
        if (journalable instanceof TenderCheckADO)
        {
            TenderCheckADO tenderCheck = (TenderCheckADO)journalable;
            TenderCheckIfc tenderCheckRDO = (TenderCheckIfc)tenderCheck.toLegacy();
            maskedNumber = tenderCheckRDO.getAccountNumberEncipheredData().getMaskedNumber();
        }

        if (!Util.isEmpty(maskedNumber))
        {
            dataArgs = new Object[]{ maskedNumber };
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ACCOUNT_NUMBER_LABEL,dataArgs,journalLocale));
        }
        String ABANumber = (String) memento.get(TenderConstants.ABA_NUMBER);
        if ((ABANumber != null) && !(Util.isEmpty(ABANumber)))
        {
            dataArgs = new Object[]{ ABANumber };
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ABANUMBER_LABEL,dataArgs,journalLocale));
        }
        String checkNumber = (String) memento.get(TenderConstants.CHECK_NUMBER);
        if (!(Util.isEmpty(checkNumber)))
        {
            dataArgs = new Object[]{checkNumber};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CHECKNUMBER_LABEL,dataArgs,journalLocale));
        }

        //Personal ID (i.e. Drivers License) Entry Method
        //Requirements have changed to not show the id number
        EntryMethod idEntryMethod = (EntryMethod) memento.get(TenderConstants.ID_ENTRY_METHOD);
        if (EntryMethod.Automatic.equals(idEntryMethod) || EntryMethod.Swipe.equals(idEntryMethod))
        {
            String IDType = (String) memento.get(TenderConstants.ID_TYPE);
            if (!(Util.isEmpty(IDType)))
            {
                dataArgs = new Object[]{IDType};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ID_TYPE_LABEL,dataArgs,journalLocale));
            }
            String IDIssuer = (String) memento.get(TenderConstants.ID_STATE);
            if (!(Util.isEmpty(IDIssuer)))
            {
                dataArgs = new Object[]{IDIssuer};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ISSUER_LABEL,dataArgs,journalLocale));
            }
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ID_ENTRY_LABEL_SWIPE, null,journalLocale));
        }
        else
        {
            String IDType = (String) memento.get(TenderConstants.ID_TYPE);
            if (!(Util.isEmpty(IDType)))
            {
                dataArgs = new Object[]{IDType};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ID_TYPE_LABEL,dataArgs,journalLocale));
            }
            String IDIssuer = (String) memento.get(TenderConstants.ID_STATE);
            if (!(Util.isEmpty(IDIssuer)))
            {
                dataArgs = new Object[]{IDIssuer};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ISSUER_LABEL,dataArgs,journalLocale));
            }
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ID_ENTRY_LABEL_MANUAL, null,journalLocale));
        }

        //MICR Entry Method
        EntryMethod entryMethod = (EntryMethod) memento.get(TenderConstants.ENTRY_METHOD);
        if (entryMethod != null)
        {
            sb.append(Util.EOL);
            EntryMethod method = (EntryMethod)memento.get(TenderConstants.ENTRY_METHOD);
            dataArgs = getI18NJournalTenderEntryMethod(method);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_METHOD_LABEL,dataArgs,journalLocale));
        }

    }

    /**
     * Formats mail check specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatMailCheckInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object[] dataArgs;

        String firstName = (String) memento.get(TenderConstants.FIRST_NAME);
        if (!(Util.isEmpty(firstName)))
        {
            dataArgs = new Object[]{firstName};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.FIRST_NAME_LABEL,dataArgs,journalLocale));
        }

        String lastName = (String) memento.get(TenderConstants.LAST_NAME);
        if (!(Util.isEmpty(lastName)))
        {
            dataArgs = new Object[]{lastName};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAST_NAME_LABEL,dataArgs,journalLocale));
        }

        String addressLine1 = (String) memento.get(TenderConstants.ADDRESS_1);
        if (!(Util.isEmpty(addressLine1)))
        {
            dataArgs = new Object[]{addressLine1};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ADDRESS1_LABEL,dataArgs,journalLocale));
        }

        String addressLine2 = (String) memento.get(TenderConstants.ADDRESS_2);
        if (!(Util.isEmpty(addressLine2)))
        {
            dataArgs = new Object[]{addressLine2};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ADDRESS2_LABEL,dataArgs,journalLocale));
        }

        String addressLine3 = (String) memento.get(TenderConstants.ADDRESS_3);
        if (!(Util.isEmpty(addressLine3)))
        {
            dataArgs = new Object[]{addressLine3};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ADDRESS3_LABEL,dataArgs,journalLocale));
        }

        String city = (String) memento.get(TenderConstants.CITY);
        if (city != null && !city.equals(""))
        {
            dataArgs = new Object[]{city};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CITY_LABEL,dataArgs,journalLocale));
        }

        String state = (String) memento.get(TenderConstants.STATE);
        if (state != null && !state.equals(""))
        {
            dataArgs = new Object[]{state};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.STATE_LABEL,dataArgs,journalLocale));
        }

        String country = (String) memento.get(TenderConstants.COUNTRY);
        if (country != null && !country.equals(""))
        {
            dataArgs = new Object[]{country};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.COUNTRY_LABEL,dataArgs,journalLocale));
        }

        String postalCode = (String) memento.get(TenderConstants.POSTAL_CODE_1);
        if (postalCode != null && !postalCode.equals(""))
        {
            dataArgs = new Object[]{postalCode};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ZIP_LABEL,dataArgs,journalLocale));

            String postalCodeExtension = (String) memento.get(TenderConstants.POSTAL_CODE_2);
            if (postalCodeExtension != null && !postalCodeExtension.equals(""))
            {
                sb.append(Util.EOL).append("-").append(postalCodeExtension);
            }
        }

        @SuppressWarnings("unchecked")
        Collection<PhoneIfc> phones = (Collection<PhoneIfc>)memento.get(TenderConstants.PHONES);
        for (PhoneIfc phone : phones)
        {
            sb.append(phone.toJournalString(getJournalLocale()));
        }


        //String IDType = (String) memento.get(TenderConstants.ID_TYPE);
        //if (!(Util.isEmpty(IDType)))
        //{
        //    sb.append(Util.EOL).append(" ID type: ").append(IDType);
        //}
    }

    /**
     * Converts an auth status to a String description
     *
     * @param authorizationStatus
     * @return
     */
    protected String authorizationStatusToString(int authorizationStatus)
    {
        String strResult;
        Object[] dataArgs;
        try
        {
            strResult = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.JOURNAL_ENTRY_PREFIX+
                    AuthorizableTenderIfc.AUTHORIZATION_STATUS_DESCRIPTORS[authorizationStatus],null,journalLocale);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            dataArgs = new Object[]{authorizationStatus};
            strResult = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.INVALID_VALUE_LABEL,dataArgs,journalLocale);
        }
        return (strResult);
    }

    /**
     * Formats authorization information common to all authorizable tenders
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatAuthorizationInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object[] dataArgs;

        // don't journal this info if we haven't authorized yet
        if (((AuthorizableADOIfc) journalable).isAuthorized())
        {
            if (!(journalable instanceof TenderGiftCardADO))
            {
                if(tenderAction.equalsIgnoreCase(DECLINED))
                {
                    dataArgs = new Object[]{authorizationStatusToString(AuthorizableTenderIfc.AUTHORIZATION_STATUS_DECLINED)};
                }
                else
                {
                    dataArgs = new Object[]{authorizationStatusToString(((Integer) memento.get(TenderConstants.AUTH_STATUS)).intValue())};
                }
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_STATUS_LABEL,dataArgs,journalLocale));
            }
            if (journalable instanceof TenderGiftCardADO)
            {
                CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance();
                String remBalance = (String) memento.get(TenderConstants.REMAINING_BALANCE);
                // don't print the current balance if in transaction reentry mode
                if (remBalance != null && !((TenderGiftCardADO)journalable).isTransactionReentryMode())
                {
                    amt.setStringValue(remBalance);
                    dataArgs = new Object[]{amt.toGroupFormattedString()};
                    sb.append(Util.EOL);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GC_AVAILABLE_BALANCE_LABEL,dataArgs,journalLocale));
                }
                else
                {
                    dataArgs = new Object[]{I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NOT_AVAILABLE_LABEL, null)};
                    sb.append(Util.EOL);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GC_AVAILABLE_BALANCE_LABEL,dataArgs,journalLocale));
                }
                if (memento.get(TenderConstants.TRACE_NUMBER) != null)
                {
                    String traceNumber = (String)memento.get(TenderConstants.TRACE_NUMBER);
                    dataArgs = new Object[] { traceNumber };
                    sb.append(Util.EOL);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TRACE_NUMBER_LABEL, dataArgs, journalLocale));
                }
            }
            if (memento.get(TenderConstants.AUTH_CODE) != null)
            {
                dataArgs = new Object[]{(String) memento.get(TenderConstants.AUTH_CODE)};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_CODE_LABEL,dataArgs,journalLocale));
            }
            if (!(journalable instanceof TenderGiftCardADO))
            {
                String resp = (String)(memento.get(TenderConstants.AUTH_RESPONSE));
                if (resp != null && resp.length() > 0)
                {
                    dataArgs = new Object[]{resp};
                    sb.append(Util.EOL);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_RESPONSE_LABEL,dataArgs,journalLocale));
                }
                else
                {
                    resp = (String)(memento.get(TenderConstants.AUTH_RESPONSE_CODE));
                    dataArgs = new Object[]{resp};
                    sb.append(Util.EOL);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_RESPONSE_LABEL,dataArgs,journalLocale));
                }
            }
        }

        if (journalable instanceof TenderCreditADO || journalable instanceof TenderDebitADO ||
                journalable instanceof TenderCheckADO || journalable instanceof TenderGiftCardADO)
        {
            if (memento.get(TenderConstants.AUTH_METHOD) != null)
            {
                dataArgs = new Object[]{(String) memento.get(TenderConstants.AUTH_METHOD)};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_METHOD_LABEL,dataArgs,journalLocale));
            }
            if (!(journalable instanceof TenderGiftCardADO))
            {
                if (memento.get(TenderConstants.AUTH_AMOUNT) != null)
                {
                    //get formatted  authorized amount
                    String authAmount = (String)memento.get(TenderConstants.AUTH_AMOUNT);
                    CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance();
                    amt.setStringValue(authAmount);
                    String formattedAuthAmount = amt.toGroupFormattedString();

                    dataArgs = new Object[]{formattedAuthAmount};
                    sb.append(Util.EOL);
                    sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_AMOUNT_LABEL,dataArgs,journalLocale));
                }
            }
        }
        if (memento.get(TenderConstants.FINANCIAL_NETWORK_STATUS) != null)
        {
            dataArgs = new Object[]{(String) memento.get(TenderConstants.FINANCIAL_NETWORK_STATUS)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NETWORK_LABEL,dataArgs,journalLocale));
        }
        if (journalable instanceof TenderCreditADO || journalable instanceof TenderDebitADO)
        {
            CurrencyIfc remBalance = (CurrencyIfc) memento.get(TenderConstants.REMAINING_BALANCE);
            if (remBalance != null)
            {
                dataArgs = new Object[]{remBalance.toGroupFormattedString()};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AVAILABLE_BALANCE_LABEL,dataArgs,journalLocale));
            }

            // Credit Card Accountability Responsibility and Disclosure Act of 2009
            String promotionDescription = (String)memento.get(TenderConstants.PROMOTION_DESCRIPTION);
            if(Util.isEmpty(promotionDescription) == false)
            {
                dataArgs = new Object[]{promotionDescription};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PROMOTION_DESCRIPTION_LABEL,dataArgs,journalLocale));
            }
            String promotionDuration = (String)memento.get(TenderConstants.PROMOTION_DURATION);
            if(Util.isEmpty(promotionDuration) == false)
            {
                dataArgs = new Object[]{promotionDuration};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PROMOTION_DURATION_LABEL,dataArgs,journalLocale));
            }
            String promotionAPR = (String)memento.get(TenderConstants.PROMOTION_APR);
            String promotionAPRType = (String)memento.get(TenderConstants.PROMOTION_APR_TYPE);
            if(Util.isEmpty(promotionAPR) == false && Util.isEmpty(promotionAPRType) == false)
            {
                dataArgs = new Object[]{promotionAPR, promotionAPRType};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.PROMOTION_APR_LABEL,dataArgs,journalLocale));
            }
            String accountAPR = (String)memento.get(TenderConstants.ACCOUNT_APR);
            String accountAPRType = (String)memento.get(TenderConstants.ACCOUNT_APR_TYPE);
            if(Util.isEmpty(accountAPR) == false && Util.isEmpty(accountAPRType) == false)
            {
                dataArgs = new Object[]{accountAPR, accountAPRType};
                sb.append(Util.EOL);
                sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ACCOUNT_APR_LABEL,dataArgs,journalLocale));
            }
        }
    }

    /**
     * Journals the totals information for Void transactions
     *
     * @param template
     * @param txn
     * @param sb
     */
    protected void formatVoidTenderTotal(JournalTemplateIfc template, JournalableADOIfc txn, StringBuilder sb)
    {
        assert (txn instanceof RetailTransactionADOIfc);

        Map<String,Object> memento = txn.getJournalMemento();
        CurrencyIfc tenderTotal = (CurrencyIfc) memento.get(JournalConstants.TOTAL_TENDER);
        if (txn instanceof VoidTransactionADO)
        {
            VoidTransactionADO trans = (VoidTransactionADO)txn;
            if (trans.getOriginalTranactionType() == TransactionPrototypeEnum.TILL_ADJUSTMENT)
            {
                tenderTotal = tenderTotal.negate();
            }
        }
        String tenderTotalStr = tenderTotal.toGroupFormattedString();
        Object[] dataArgs = new Object[] { tenderTotalStr };
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTALTENDER_REVERSED_AMOUNT,dataArgs,journalLocale));

        CurrencyIfc balanceDue = (CurrencyIfc) memento.get(JournalConstants.BALANCE_DUE);
        String balanceDueStr = balanceDue.toGroupFormattedString();

        dataArgs = new Object[]{balanceDueStr};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BALANCE_DUE_REVERSED_LABEL,dataArgs,journalLocale));
    }

    /**
     * Journals a tender override
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatOverride(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object[] dataArgs;

        Boolean result = Boolean.valueOf((String) memento.get(JournalConstants.BOOLEAN));
        String resultStr = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GRANTED_LABEL,null,journalLocale);
        if (result == Boolean.FALSE)
        {
            resultStr = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DENIED_LABEL,null,journalLocale);
        }
        dataArgs = new Object[]{Role.getFunctionTitle(getJournalLocale(), Integer.parseInt((String) memento.get(JournalConstants.FUNCTION))),resultStr};

        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SECURITY_OVERRIDE_LABEL,dataArgs,journalLocale));

        dataArgs = new Object[]{(String) memento.get(JournalConstants.OPERATOR)};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TO_CASHIER_LABEL,dataArgs,journalLocale));

        dataArgs = new Object[]{(String) memento.get(JournalConstants.OVERRIDE_OPERATOR)};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BY_EMPLOYEE_LABEL,dataArgs,journalLocale));
    }

    /**
     * Creates the common certificate info.
     *
     * @param memento
     * @param sb
     */
    protected void commonCertificateInfo(Map<String,Object> memento, StringBuilder sb)
    {
        EntryMethod entryMethod = (EntryMethod)memento.get(TenderConstants.ENTRY_METHOD);
        if (entryMethod != null)
        {
            sb.append(Util.EOL);
            EntryMethod method = (EntryMethod)memento.get(TenderConstants.ENTRY_METHOD);
            Object[] dataArgs = getI18NJournalTenderEntryMethod(method);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_LABEL,dataArgs,journalLocale));
        }

    }

    /**
     * Formats gift certificate specific information
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatGiftCertificateInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        boolean tenderedRedeemed = REDEEMED.equals(tenderAction) || TENDERED.equals(tenderAction);
        Object [] dataArgs;
        Map<String,Object> memento = journalable.getJournalMemento();

        String faceValue = (String) memento.get(TenderConstants.FACE_VALUE_AMOUNT);
        CurrencyIfc amt = DomainGateway.getBaseCurrencyInstance();
        amt.setStringValue(faceValue);
        String formattedFaceValue = amt.toGroupFormattedString();
        if (!Util.isEmpty(faceValue))
        {
            dataArgs = new Object[]{formattedFaceValue};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CERT_VALUE_LABEL,dataArgs,journalLocale));
        }

        dataArgs = new Object[]{(String) memento.get(TenderConstants.NUMBER)};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NUMBER_LABEL,dataArgs,journalLocale));

        if (tenderedRedeemed)
        {
            dataArgs = new Object[]{((String) memento.get(TenderConstants.STORE_NUMBER))};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ISSUING_STORE_NUMBER_LABEL,dataArgs,journalLocale));
        }

        commonCertificateInfo(memento, sb);

        String type = "";
        if (memento.get(TenderConstants.CERTIFICATE_TYPE) != null)
        {
            type = memento.get(TenderConstants.CERTIFICATE_TYPE).toString();
        }

        if (!tenderedRedeemed)
        {
            dataArgs = new Object[]{type};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TYPE_LABEL,dataArgs,journalLocale));

            dataArgs = new Object[]{I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.APPROVED_LABEL,null,journalLocale)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.AUTHORIZATION_STATUS_LABEL,dataArgs,journalLocale));
        }
        sb.append(Util.EOL);
    }

    /**
     * Format gift card information.
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatGiftCardInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object [] dataArgs;
        // journal store credit ID
        dataArgs = new Object[]{memento.get(TenderConstants.NUMBER)};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NUMBER_LABEL,dataArgs,journalLocale));

        if (memento.get(TenderConstants.ENTRY_METHOD) != null)
        {
            sb.append(Util.EOL);
            EntryMethod method = (EntryMethod)memento.get(TenderConstants.ENTRY_METHOD);
            dataArgs = getI18NJournalTenderEntryMethod(method);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_METHOD_LABEL,dataArgs,journalLocale));
        }
    }

    protected void formatStoreCreditInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object dataArgs[];
        // journal store credit ID
        dataArgs = new Object[]{memento.get(TenderConstants.NUMBER)};
        sb.append(Util.EOL);
        sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NUMBER_LABEL,dataArgs,journalLocale));

        // journal entry method
        if (memento.get(TenderConstants.ENTRY_METHOD) != null)
        {
            sb.append(Util.EOL);
            EntryMethod method = (EntryMethod)memento.get(TenderConstants.ENTRY_METHOD);
            dataArgs = getI18NJournalTenderEntryMethod(method);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ENTRY_LABEL,dataArgs,journalLocale));
        }

        Locale ejLocale=getJournalLocale();
        // journal expiration date
        if (memento.get(TenderConstants.EXPIRATION_DATE) != null)
        {
            dataArgs = new Object[]{memento.get(TenderConstants.EXPIRATION_DATE)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.EXPIRATION_DATE_LABEL,dataArgs,journalLocale));
        }

        // Journal Customer first, last name and id type.
        if (memento.get(TenderConstants.FIRST_NAME) != null)
        {
            dataArgs = new Object[]{memento.get(TenderConstants.FIRST_NAME)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.FIRST_NAME_LABEL,dataArgs,journalLocale));
        }
        if (memento.get(TenderConstants.LAST_NAME) != null)
        {
            dataArgs = new Object[]{memento.get(TenderConstants.LAST_NAME)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.LAST_NAME_LABEL,dataArgs,journalLocale));
        }

        if (!Util.isEmpty(((LocalizedCode)memento.get(TenderConstants.LOCALIZED_ID_TYPE)).getText(ejLocale)))
        {
            dataArgs = new Object[]{((LocalizedCode)memento.get(TenderConstants.LOCALIZED_ID_TYPE)).getText(ejLocale)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ID_TYPE_LABEL,dataArgs,journalLocale));
        }
    }

    /**
     * This method formats the mall certificate info.
     *
     * @param template
     * @param journalable
     * @param sb
     */
    protected void formatMallCertificateInfo(JournalTemplateIfc template, JournalableADOIfc journalable, StringBuilder sb)
    {
        Map<String,Object> memento = journalable.getJournalMemento();
        Object [] dataArgs;
        if ((String) memento.get(TenderConstants.NUMBER) != null)
        {
            dataArgs = new Object[]{(String) memento.get(TenderConstants.NUMBER)};
            sb.append(Util.EOL);
            sb.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NUMBER_LABEL,dataArgs,journalLocale));
        }

        commonCertificateInfo(memento, sb);
        String type = "Mall";
        if ((String) memento.get(TenderConstants.CERTIFICATE_TYPE) != null
                && !((String) memento.get(TenderConstants.CERTIFICATE_TYPE)).equals(""))
        {
            if (memento.get(TenderConstants.CERTIFICATE_TYPE).toString().equals(
                    TenderMallCertificateADO.MALL_GC_AS_CHECK))
            {
                type = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MALL_CERT_AS_CHECK_LABEL,null,journalLocale);
            }
            else if (memento.get(TenderConstants.CERTIFICATE_TYPE).toString().equals(
                    TenderMallCertificateADO.MALL_GC_AS_PO))
            {
                type = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MALL_CERT_AS_PO_LABEL,null,journalLocale);
            }
        }
        dataArgs = new Object[]{type};
        sb.append(Util.EOL).append( I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TYPE_LABEL,dataArgs,journalLocale));
    }

    /*
     * Get the localized journal text for the tender entry method.
     */
    protected Object[] getI18NJournalTenderEntryMethod(EntryMethod method)
    {
        String methodText = I18NHelper.getString(I18NConstantsIfc.COMMON_TYPE, CommonActionsIfc.COMMON + "." +
                method.toString());
        Object[] object = new Object[1];
        object[0] = methodText;
        return object;
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
