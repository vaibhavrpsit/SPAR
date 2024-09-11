/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/manager/utility/UtilityManager.java /main/57 2014/02/10 11:32:19 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  02/10/14 - Fortify Null Check
 *    jswan     02/07/14 - Fixed zero length empoyee id issue.
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    asinton   10/03/11 - moved CardTypeUtility to domain in order to support
 *                         card type determination on store server
 *    cgreene   08/16/11 - implement timeout capability for admin menu
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    asinton   01/21/11 - Fix for timezone issue on the EJournal.
 *    rsnayak   11/26/10 - forward port 10216221 fix for transaction number
 *    jswan     11/05/10 - Modified to prevent returns with employee discounts
 *                         from printing the Employee Discount Store Receipt.
 *    jswan     11/04/10 - Fixed hasEmployeeDiscounts to take into account
 *                         whether the line item is a sale.
 *    acadar    09/07/10 - externalize supported localesz
 *    abhayg    08/13/10 - STOPPING POS TRANSACTION IF REGISTER HDD IS FULL
 *    npoola    08/11/10 - Actual register is used for training mode instead of
 *                         OtherRegister.
 *    jkoppolu  07/12/10 - Modified as part of the fix for bug#9704082
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/16/09 - Added utility methods for serialisation
 *    cgreene   06/18/09 - add comments about no longer caching reason codes
 *    cgreene   05/15/09 - generic performance improvements
 *    asinton   05/04/09 - Removed unused getDefaultTaxRate method.
 *    jswan     04/16/09 - Modified to support I18N for new tax parameters.
 *    jswan     04/16/09 - Fix data issues with deptartment tax calculation and
 *                         default tax.
 *    ohorne    03/30/09 - added getEmailValidationRegexp()
 *    jswan     03/23/09 - Rework default tax to include a rule from the
 *                         database; this supports printing receipts and
 *                         reports.
 *    abondala  03/05/09 - updated
 *    sgu       03/02/09 - check in after refresh
 *    sgu       03/02/09 - use denomination descriptions from its I18n table
 *    jswan     02/27/09 - Code review changes
 *    arathore  02/17/09 - udpated to sort the state list based on state name.
 *    sgu       02/04/09 - get country and state names from localization bundle
 *    sgu       02/04/09 - retrieve country and state names from localization
 *                         bundle
 *    vchengeg  01/27/09 - ej defect fixes
 *    vchengeg  01/07/09 - ej defect fixes
 *    mkochumm  12/17/08 - format phone number
 *    mchellap  11/21/08 - Merge
 *    mchellap  11/21/08 - Renamed TransactionStatus to TransactionStatusBean
 *    mchellap  11/20/08 - Changes for code review comments
 *    mchellap  11/20/08 - Modified initialize and save transaction methods to
 *                         update transaction status
 *    ranojha   11/05/08 - Fixed Customer
 *    ranojha   11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *    mkochumm  11/04/08 - i18n changes for phone and postalcode fields
 *    akandru   10/30/08 - EJ changes
 *    deghosh   10/29/08 - EJI18n_changes_ExtendyourStore
 *    acadar    10/24/08 - localization of post void reason codes
 *    ranojha   10/21/08 - Code Review Comments for UnitOfMeasure changes
 *    ranojha   10/20/08 - Deprecated Method getSupportedLocales for
 *                         UtilityManager
 *    mdecama   10/20/08 - Deprecated codeListMap field and accessors
 *    ranojha   10/14/08 - Fixed getSupportedLocales method in UtilityManager

 *    mkochumm  10/10/08 - i18n pos locales changes
 *    ranojha   10/09/08 - Merged UtilityManager
 *    ranojha   10/09/08 - Changes for User Selection for Employee and Customer
 *    ddbaker   10/07/08 - Updated using code review feedback.
 *    ddbaker   10/06/08 - Updated to carry only best match locales in
 *                         LocaleRequestor object.
 *    ddbaker   10/04/08 - POS Persistence Infrastructure Work
 *    cgreene   10/02/08 - merged with tip
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 * $Log:
 *  25   360Commerce 1.24        4/18/2008 3:03:27 PM   Alan N. Sinton  CR
 *       31133: Undid fix for 30247 and fixed the code for employee card
 *       swipe.  Code was reviewed by Tony Zgarba.
 *  24   360Commerce 1.23        4/16/2008 3:13:41 AM   Anil Kandru     Account
 *        number getter method added.
 *  23   360Commerce 1.22        4/12/2008 6:36:44 PM   Christian Greene
 *       upgrade StringBuffer to StringBuilder for performance
 *  22   360Commerce 1.21        3/7/2008 8:45:23 AM    Manas Sahu      Set the
 *        Reentry mode in Transaction before setting the transaction in Cargo
 *  21   360Commerce 1.20        1/17/2008 5:24:06 PM   Alan N. Sinton  CR
 *       29954: Refactor of EncipheredCardData to implement interface and be
 *       instantiated using a factory.
 *  20   360Commerce 1.19        1/10/2008 1:05:19 PM   Alan N. Sinton  CR
 *       29761:  Code review changes per Tony Zgarba and Jack Swan.
 *  19   360Commerce 1.18        12/17/2007 6:21:32 PM  Alan N. Sinton  CR
 *       29598: Fixed some flow issues.
 *  18   360Commerce 1.17        12/14/2007 8:59:59 AM  Alan N. Sinton  CR
 *       29761: Removed non-PABP compliant methods and modified card RuleIfc
 *       to take an instance of EncipheredCardData.
 *  17   360Commerce 1.16        12/12/2007 6:47:38 PM  Alan N. Sinton  CR
 *       29761: FR 8: Prevent repeated decryption of PAN data.
 *  16   360Commerce 1.15        8/20/2007 1:47:10 PM   Jack G. Swan    Promote
 *        from .v12x
 *  15   360Commerce 1.14        7/26/2007 7:59:53 AM   Alan N. Sinton  CR
 *       27192 Make item lookup depend on department tax group ID if item's
 *       tax group ID is invalid.
 *  14   360Commerce 1.13        5/8/2007 5:22:00 PM    Alan N. Sinton  CR
 *       26486 - Refactor of some EJournal code.
 *  13   360Commerce 1.12        4/30/2007 7:01:38 PM   Alan N. Sinton  CR
 *       26485 - Merge from v12.0_temp.
 *  12   360Commerce 1.11        4/27/2007 8:21:29 AM   Mathews Kochummen make
 *       ej search work for all locales
 *  11   360Commerce 1.10        4/25/2007 8:52:40 AM   Anda D. Cadar   I18N
 *       merge
 *
 *  10   360Commerce 1.9         4/13/2007 3:15:55 PM   Ashok.Mondal    CR
 *       21514 : V7.2.2 merge to trunk.
 *  9    360Commerce 1.8         4/5/2007 2:12:23 PM    Ashok.Mondal    CR
 *       19560 - v7x merge to trunk :Fix to correct the incorrect EJournal
 *       spacing for linked customer.
 *  8    360Commerce 1.7         12/13/2006 4:27:37 PM  Charles D. Baker CR
 *       22741 - Added extra line before linked customer ID after transaction
 *       header.
 *  7    360Commerce 1.6         7/24/2006 6:43:16 PM   Rohit Sachdeva  16836:
 *       Merged 16836 code changes from v7x Star Team View
 *  6    360Commerce 1.5         1/25/2006 4:11:54 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  5    360Commerce 1.4         1/22/2006 11:45:03 AM  Ron W. Haight   removed
 *        references to com.ibm.math.BigDecimal
 *  4    360Commerce 1.3         12/13/2005 4:42:37 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:26:38 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:15:27 PM  Robert Pearse
 * $: UtilityManager.java,v $
 *  6    .v710     1.2.2.0     9/21/2005 13:40:28     Brendan W. Farrell
 *       Initial Check in merge 67.
 *  5    .v700     1.2.3.1     11/4/2005 10:44:36     Jason L. DeLeau 4201:
 *       Services Impact - Fix PriceCodeConverter extensibility issues.
 *  4    .v700     1.2.3.0     10/25/2005 10:55:34    Deepanshu       CR 6153:
 *       Skip check of 'Tax Exempt Receipt Print Control' if check for 'Cancel
 *       Transaction Receipt Print Control' is true so that 'Cancel Transaction
 *       Receipt Print Control' overrides 'Tax Exempt Receipt Print Control'
 *  3    360Commerce1.2         3/31/2005 15:30:41     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:26:38     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:15:27     Robert Pearse
 * $
 * Revision 1.60  2004/08/16 21:14:52  lzhao
 * @scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.
 *
 * Revision 1.59  2004/08/04 18:19:02  blj
 * @scr 5214 - refactored app so that default locale language displays first in drop down lists.
 *
 * Revision 1.58  2004/07/30 19:04:17  cdb
 * @scr 6373 Updated receipt format to meet changing requirements.
 *
 * Revision 1.57  2004/07/29 20:43:25  jdeleau
 * @scr 6594 backout changes until further requirements are known
 *
 * Revision 1.55  2004/07/28 18:28:41  jdeleau
 * @scr 6539 Gift Receipts set on a transaction level need to print
 * all line items on the same gift receipt.
 *
 * Revision 1.54  2004/07/23 22:17:26  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.53  2004/07/21 15:41:35  dcobb
 * @scr 6375 Exponential gift receipts get printed
 * Removed loop around call to printGiftReceipt.
 *
 * Revision 1.52  2004/07/15 22:31:03  cdb
 * @scr 1673 Removed all deprecation warnings from log and manager packages.
 * Moved LogArchiveStrategies to deprecation tree.
 *
 * Revision 1.51  2004/07/14 18:47:10  epd
 * @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 * Revision 1.50  2004/07/08 23:34:30  jdeleau
 * @scr 6086 payroll till pay out on post void was crashing the system.  In fact it
 * was not implemented at all.  Now its implemented just as normal till pay out.
 *
 * Revision 1.49  2004/06/29 17:05:38  cdb
 * @scr 4205 Removed merging of money orders into checks.
 * Added ability to count money orders at till reconcile.
 *
 * Revision 1.48  2004/06/25 20:26:53  bwf
 * @scr 4107 Fixed gift card issue automatic gift receipt.
 *
 * Revision 1.47  2004/06/25 17:02:57  cdb
 * @scr 4479 Added Alterations Print Control parameter.
 *
 * Revision 1.46  2004/06/22 00:13:24  cdb
 * @scr 4205 Updated to merge money orders into checks during till reconcile.
 *
 * Revision 1.45  2004/06/17 15:35:33  jdeleau
 * @scr 4616 Add PrintItemTax parameter, and make receipts print tax
 * on a line item basis as necessary.
 *
 * Revision 1.44  2004/06/14 17:44:56  mweis
 * @scr 5578 Customer Survey / Reward needs to have an interface
 *
 * Revision 1.43  2004/06/08 17:24:33  dfierling
 * @scr 5291 - Corrected for printing Alteration instructions
 *
 * Revision 1.42  2004/06/03 14:47:46  epd
 * @scr 5368 Update to use of DataTransactionFactory
 *
 * Revision 1.41  2004/05/24 22:19:06  crain
 * @scr 5060 Purchase Order- Extra copy of receipt printed when PO Tender issued by Other
 *
 * Revision 1.40  2004/05/19 15:25:01  lzhao
 * @scr 3693: make journal read, write none transaction message.
 *
 * Revision 1.39  2004/05/17 16:31:45  rsachdeva
 * @scr 4670 Send: Multiple Sends Only Comment Changed
 *
 * Revision 1.38  2004/05/10 13:42:26  kll
 * @scr 3599: journal loginID vs. employeeID based upon ManualEntryID parameter
 *
 * Revision 1.37  2004/04/30 17:02:25  cdb
 * @scr 4489 Updated to match BA desire for print control behavior. Also cleaned up some javadoc.
 *
 * Revision 1.36  2004/04/28 23:32:20  cdb
 * @scr 4456 Updated Post Void Signature Lines parameter to match requirements.
 *
 * Revision 1.35  2004/04/27 22:24:52  dcobb
 * @scr 4452 Feature Enhancement: Printing
 * Code review updates.
 *
 * Revision 1.34  2004/04/27 20:07:26  dcobb
 * @scr 4452 Feature Enhancement: Printing
 * Only print gift receipts for the items left to return..
 *
 * Revision 1.33  2004/04/22 18:40:23  cdb
 * @scr 4452 Added Gift Receipt Header and Footer as parameters
 * for gift receipt construction.
 *
 * Revision 1.32  2004/04/22 14:43:39  dcobb
 * @scr 4452 Feature Enhancement: Printing
 * Added Reprint Select.
 *
 * Revision 1.31  2004/04/21 21:15:24  cdb
 * @scr 4452 Enforced prohibiting gift receipts for damaged items.
 *
 * Revision 1.29  2004/04/21 18:29:11  dcobb
 * @scr 4452 Feature Enhancement: Printing
 * Added printGiftReceipt() method.
 *
 * Revision 1.28  2004/04/21 18:26:09  cdb
 * @scr 4452 Modified behavior of employee discount receipts.
 *
 * Revision 1.27  2004/04/21 16:58:36  cdb
 * @scr 4452 Modified behavior of gift card issue gift receipt.
 *
 * Revision 1.26  2004/04/20 22:41:08  cdb
 * @scr 4452 Added Redeem Transaction Footer
 *
 * Revision 1.25  2004/04/20 21:40:37  cdb
 * @scr 4452 Added Gift Receipt Header and Footer parameters.
 *
 * Revision 1.24  2004/04/20 21:11:43  cdb
 * @scr 4452 Added Gift Receipt Header and Footer parameters.
 *
 * Revision 1.23  2004/04/20 20:14:27  cdb
 * @scr 4452 Enabled auto print of gift receipt for gift card issue.
 *
 * Revision 1.22  2004/04/20 14:33:56  tmorris
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.21  2004/04/16 16:03:40  tmorris
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.20  2004/04/07 14:36:08  jdeleau
 * @scr 4090 Set up the LocaleMaps for DEVICES where necessary
 *
 * Revision 1.19  2004/03/31 16:22:58  awilliam
 * @scr 3985 fix for send gift receipts going to same address not printing on the same Gift receipt
 *
 * Revision 1.18  2004/03/30 22:32:12  rsachdeva
 * @scr 3906 Journal index
 *
 * Revision 1.17  2004/03/24 17:06:34  blj
 * @scr 3871-3872 - Added the ability to reprint redeem transaction receipts and added a void receipt.
 *
 * Revision 1.16  2004/03/22 17:26:43  blj
 * @scr 3872 - added redeem security, receipt printing and saving redeem transactions.
 *
 * Revision 1.15  2004/03/22 03:49:28  cdb
 * @scr 3588 Code Review Updates
 *
 * Revision 1.14  2004/03/17 20:29:32  mweis
 * @scr 4025 Customer Survey/Reward enablement
 *
 * Revision 1.12  2004/03/14 21:19:34  tfritz
 * @scr 3884 - New Training Mode Functionality
 *
 * Revision 1.11  2004/03/12 23:33:26  mweis
 * @scr 0 Javadoc cleanup
 *
 * Revision 1.10  2004/03/04 23:42:15  cdb
 * @scr 3588 Added store copy receipt of transaction
 * employee discounts. Also cleaned up Utility Manager.
 *
 * Revision 1.9  2004/02/27 16:33:51  cdb
 * @scr 3588 Corrected employee receipt problem - forgot
 * to check for employee discounts by percent.
 *
 * Revision 1.8  2004/02/24 19:19:33  bwf
 * @scr 0 Code review changes for operator id swipe.
 *
 * Revision 1.7  2004/02/19 18:37:41  cdb
 * @scr 3588 Added Store Copy of Employee Discount receipt.
 *
 * Revision 1.6  2004/02/17 00:32:04  bjosserand
 * @scr 0
 *
 * Revision 1.5  2004/02/14 00:26:14  bjosserand
 * @scr 0
 *
 * Revision 1.4  2004/02/12 16:48:40  mcs
 * Forcing head revision
 *
 * Revision 1.3  2004/02/12 00:47:15  bjosserand
 * @scr 0
 *
 * Revision 1.2  2004/02/11 21:34:24  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:13
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.10 Feb 05 2004 14:17:22 bwf Update for automatic gift receipt
 * printing. Resolution for 3765: Modify Item Feature
 *
 * Rev 1.9 Feb 03 2004 16:48:58 bwf Added getEmployeeFromModel.
 *
 * Rev 1.8 Jan 26 2004 16:04:58 epd Updates for void printing
 *
 * Rev 1.7 Jan 06 2004 10:59:50 epd added method to determine card type
 *
 * Rev 1.6 Dec 18 2003 19:16:16 blj changed cardtypeIfc to cardtype for
 * backwards compatibility
 *
 * Rev 1.5 Dec 18 2003 11:14:24 epd Updated method to return interface type
 *
 * Rev 1.4 Dec 17 2003 09:10:18 rrn Changes to initializeTransaction( ) and
 * indexTransactionInJournal( ) for journal enhancements. Resolution for 3611:
 * EJournal to database
 *
 * Rev 1.3 08 Nov 2003 01:29:48 baa cleanup -sale refactoring
 *
 * Rev 1.2 Oct 20 2003 13:57:34 kll SCR-3161: do not print alteration receipts
 * in the case of a canceled transaction
 *
 * Rev 1.1 Oct 13 2003 14:09:56 kll do not print gift receipts for canceled
 * transactions
 *
 * Rev 1.0 Aug 29 2003 15:51:42 CSchellenger Initial revision.
 *
 * Rev 1.48 Jul 28 2003 18:19:06 bwf Make sure tax info before printing tax
 * exempt receipts. Resolution for 3278: Reprint of Till Pay Void Crashes App
 * and Loan
 *
 * Rev 1.47 Jul 24 2003 14:44:00 baa add system property BUNDLE_TESTING to
 * allow brakets on property names when not found in bundles Resolution for
 * 2169: Modify base retrieve text methods to include <default text>
 *
 * Rev 1.46 Jul 18 2003 14:07:54 baa Rename print control parameters to match
 * requirements Resolution for 3089: Layaway Pickup Receipt Print Control
 * missing from POS
 *
 * Rev 1.45 Jul 15 2003 14:23:16 bwf Check trans instanceof before getting tax.
 *
 * Rev 1.44 Jul 14 2003 15:43:48 bwf Check NumberTaxExemptReceipts parameter
 * and print accordingly. Resolution for 2507: The "Number of Tax Exempt
 * Receipts" paramater is not working.
 *
 * Rev 1.43 Jun 27 2003 17:23:26 bwf Check if tender type p.o. then print 2
 * receipts. Resolution for 2260: Sale with PO Tender, needs printing 2 copies
 * of receipt
 *
 * Rev 1.42 May 22 2003 17:05:58 jgs Modified to support new Journal
 * Requirements. Resolution for 2543: Modify EJournal to put entries into a JMS
 * Queue on the store server.
 *
 * Rev 1.41 May 20 2003 08:47:26 jgs Modified to use the Domain Factory to
 * create instances of Hardtotals objects. Resolution for 2573: Modify
 * Hardtotals compress to remove dependency on code modifications.
 *
 * Rev 1.40 Apr 30 2003 11:30:10 bwf In IndexTransactionInJournal, changed way
 * that index is written. Removed writing of store number and register number
 * before cashier and sales assoc number in order to correspond to the way we
 * search for transactions for ejournal. Resolution for 2233: E. Journal- Find
 * Transaction entering Cashier ID or Sales Assoc ID=Trans Not Found Message
 *
 * Rev 1.39 Apr 24 2003 10:21:00 KLL no gift receipt for canceled transactions
 * Resolution for POS SCR-2191: Gift Receipt printing when a transaction is
 * Cancelled
 *
 * Rev 1.38 Apr 16 2003 12:22:58 baa defect fixes Resolution for POS SCR-2098:
 * Refactoring of Customer Service Screens
 *
 * Rev 1.37 Apr 09 2003 08:31:56 KLL instanceof check to prevent classcast
 * exception Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 * Rev 1.36 Apr 04 2003 17:30:30 sfl Added new data attribute and supporting
 * methods for storing tax rules for local store. Resolution for POS SCR-1749:
 * POS 6.0 Tax Package
 *
 * Rev 1.35 Apr 02 2003 13:51:52 baa I18n Database support for customer groups
 * Resolution for POS SCR-1866: I18n Database support
 *
 * Rev 1.34 Mar 11 2003 09:49:50 baa fix bug with retrieving supported locales
 * Resolution for POS SCR-1843: Multilanguage support
 *
 * Rev 1.33 Mar 11 2003 08:50:44 KLL integrating Code Review results Resolution
 * for POS SCR-1959: Printing: Cancel Transactions
 *
 * Rev 1.32 Mar 10 2003 15:36:12 KLL removed printGiftReceipt boolean from
 * non-applicable transaction types in printReceipt() switch statement
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 * Rev 1.31 Mar 07 2003 17:11:02 baa code review changes for I18n Resolution
 * for POS SCR-1740: Code base Conversions
 *
 * Rev 1.30 Mar 05 2003 20:44:32 KLL integration of code review results
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 * Rev 1.28 Feb 21 2003 11:26:14 KLL Extend receipt if credit card is manually
 * entered AND parameter declares manual imprint should be captured Resolution
 * for POS SCR-2061: Printing: Manual Capture of Credit Imprint
 *
 * Rev 1.27 Feb 21 2003 09:35:28 baa Changes for contries.properties
 * refactoring Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.26 Jan 31 2003 17:35:10 baa change pole display locale and receipt to
 * match link customer locale preferences Resolution for POS SCR-1843:
 * Multilanguage support
 *
 * Rev 1.25 Jan 30 2003 16:59:50 baa add employe locale preference for offline
 * flow Resolution for POS SCR-1843: Multilanguage support
 *
 * Rev 1.24 Jan 24 2003 11:31:42 KLL Printing: exchange transactions determined
 * within Utility Manager Resolution for POS SCR-1884: Printing Functional
 * Requirements
 *
 * Rev 1.23 Jan 23 2003 17:10:50 KLL call printReceipt() for canceled
 * transactions Resolution for POS SCR-1959: Printing: Cancel Transactions
 *
 * Rev 1.22 Jan 20 2003 10:41:06 KLL n Receipts: new SaleReceipt and
 * VoidSaleReceipt constructors Resolution for POS SCR-1884: Printing
 * Functional Requirements
 *
 * Rev 1.21 Jan 16 2003 17:34:22 crain Set the code list map in receipt from
 * UtilityManager Resolution for 1907: Remove deprecated calls to
 * AbstractFinancialCargo.getCodeListMap()
 *
 * Rev 1.20 Jan 11 2003 14:54:28 KLL n Receipts control in application.xml
 * Resolution for POS SCR-1884: Printing Functional Requirements
 *
 * Rev 1.19 Jan 02 2003 12:08:14 crain Added code list map Resolution for 1875:
 * Adding a business customer offline crashes the system
 *
 * Rev 1.18 Dec 18 2002 17:40:20 baa add employee preferred locale support
 * Resolution for POS SCR-1843: Multilanguage support
 *
 * Rev 1.17 Nov 27 2002 14:20:06 baa cleanup Resolution for POS SCR-1553:
 * cleanup dead code
 *
 * Rev 1.16 Oct 09 2002 14:58:42 jriggins Added getReasonCodeTextEntries()
 * methods Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.15 Sep 25 2002 15:37:38 kmorneau moved intrusive CardType functions to
 * their own utility Resolution for 1815: Credit Card Types Accepted
 *
 * Rev 1.14 Sep 23 2002 12:00:24 kmorneau change call to DomainObjectFactory
 * method for CardType instance to the alternate Resolution for 1815: Credit
 * Card Types Accepted
 *
 * Rev 1.13 Sep 19 2002 09:25:46 kmorneau added getConfiguredCardTypeInstance()
 * Resolution for 1815: Credit Card Types Accepted
 *
 * Rev 1.12 Sep 18 2002 17:15:18 baa country/state changes Resolution for POS
 * SCR-1740: Code base Conversions
 *
 * Rev 1.11 Sep 09 2002 16:19:38 HDyer Modify printReceipt() to look at the
 * Auto Print Gift Receipt parameter - if set to Yes and any item is linked to
 * a gift registry, then print a gift receipt for those items. Resolution for
 * 1804: Auto-Print Gift Receipt for Gift Registry Items Feature
 *
 * Rev 1.10 Sep 04 2002 15:21:06 jriggins Added getErrorCodeString() Resolution
 * for POS SCR-1740: Code base Conversions
 *
 * Rev 1.9 Sep 04 2002 10:01:24 baa replace calls to InternationalTextSupport
 * with calls to ResourceBundleUtil which allow us to change the locale
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.8 Sep 03 2002 16:03:12 baa externalize domain constants and parameter
 * values Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.7 Aug 23 2002 15:47:16 DCobb Defaulted number of alteration receipts
 * when the parameter NumberAlterationsReceipts is not defined. Resolution for
 * POS SCR-1753: POS 5.5 Alterations Package
 *
 * Rev 1.6 22 Aug 2002 10:49:28 sfl Put the external tax package branch code
 * into a new method so that the external tax package can be replaced with the
 * relational database provided tax data by deprecating this method. Resolution
 * for POS SCR-1749: POS 5.5 Tax Package
 *
 * Rev 1.5 Aug 21 2002 11:21:04 DCobb Added Alterations service. Resolution for
 * POS SCR-1753: POS 5.5 Alterations Package
 *
 * Rev 1.4 Aug 07 2002 19:33:56 baa remove hard coded date formats Resolution
 * for POS SCR-1740: Code base Conversions
 *
 * Rev 1.3 Jul 26 2002 10:48:52 pjf Add check digit support to POS. Resolution
 * for 1768: Check Digit support
 *
 * Rev 1.2 Jul 05 2002 17:58:30 baa code conversion and reduce number of color
 * settings Resolution for POS SCR-1740: Code base Conversions
 *
 * Rev 1.1 Jun 24 2002 09:18:22 baa modify retrieve text to add subsystem
 * notion Resolution for POS SCR-1624: Localization Support
 *
 * Rev 1.0 Apr 29 2002 15:44:08 msg Initial revision.
 *
 * Rev 1.1 02 Apr 2002 15:14:04 dfh updates to better journal discounts for
 * order pickup, order cancel, and return by customer (no receipt) Resolution
 * for POS SCR-1567: Picked up Orders/ return of picked up orders missing
 * discounts in EJ
 *
 * Rev 1.0 Mar 18 2002 11:14:52 msg Initial revision.
 *
 * Rev 1.23 Mar 12 2002 18:28:14 mpm Externalized line-display text. Resolution
 * for POS SCR-351: Internationalization
 *
 * Rev 1.22 Mar 12 2002 14:09:08 mpm Externalized text in receipts and
 * documents. Resolution for POS SCR-351: Internationalization
 *
 * Rev 1.21 Mar 10 2002 18:00:04 mpm Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 * Rev 1.20 Feb 27 2002 17:27:24 mpm Restructured end-of-transaction
 * processing. Resolution for POS SCR-1440: Enhance end-of-transaction
 * processing for performance reasons
 *
 * Rev 1.19 Feb 24 2002 13:44:52 mpm Externalized text for default, common and
 * giftcard config files. Resolution for POS SCR-351: Internationalization
 *
 * Rev 1.18 Feb 05 2002 16:42:02 mpm Modified to use IBM BigDecimal. Resolution
 * for POS SCR-1121: Employ IBM BigDecimal
 *
 * Rev 1.17 29 Jan 2002 09:54:16 epd Deprecated all methods using accumulate
 * parameter and added new methods without this parameter. Also removed all
 * reference to the parameter wherever used. (The behavior is to accumulate
 * totals) Resolution for POS SCR-770: Remove the accumulate parameter and all
 * references to it.
 *
 * Rev 1.16 Jan 21 2002 22:39:22 dfh updates for special order partial, tender,
 * printing receipt Resolution for POS SCR-260: Special Order feature for
 * release 5.0
 *
 * Rev 1.15 Jan 20 2002 21:48:44 dfh added special order cancel trans type,
 * initial code for voiding a canceled special order (all items canceled)
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 * Rev 1.14 02 Jan 2002 14:17:56 jbp Added VoidSpecialOrderReceipt Resolution
 * for POS SCR-260: Special Order feature for release 5.0
 *
 * Rev 1.13 Dec 19 2001 09:43:10 blj promoted to the wrong promotion group,
 * checkout and checked back in to put in development promotion group
 * Resolution for POS SCR-451: Suspending a trans with an Gift Receipt item
 * prints 2 receipts
 *
 * Rev 1.12 Dec 19 2001 09:29:50 blj Fixed defects: 455,456,451,453 Resolution
 * for POS SCR-237: Gift Receipt Feature Resolution for POS SCR-451: Suspending
 * a trans with an Gift Receipt item prints 2 receipts
 *
 * Rev 1.11 Dec 12 2001 11:02:28 blj Corrected a divide by zero problem.
 * Resolution for POS SCR-237: Gift Receipt Feature
 *
 * Rev 1.10 Dec 11 2001 14:39:12 blj added comments. Resolution for POS
 * SCR-237: Gift Receipt Feature
 *
 * Rev 1.9 Dec 11 2001 14:32:16 blj Corrected a problem with the extended price
 * printing for multiple gift receipts. Resolution for POS SCR-237: Gift
 * Receipt Feature
 *
 * Rev 1.8 Dec 10 2001 17:23:28 blj updated per codereview findings. Resolution
 * for POS SCR-237: Gift Receipt Feature
 *
 * Rev 1.7 Dec 04 2001 10:50:16 dfh updates to print special order receipts
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 * Rev 1.6 Dec 03 2001 16:46:58 blj Updated for code review. Resolution for POS
 * SCR-237: Gift Receipt Feature
 *
 * Rev 1.5 29 Nov 2001 14:22:06 jbp revised changes from previous checkin.
 * Resolution for POS SCR-221: Receipt Design Changes
 *
 * Rev 1.3 Nov 27 2001 18:09:52 blj Updated to print the extended discounted
 * selling price. Resolution for POS SCR-237: Gift Receipt Feature
 *
 * Rev 1.2 26 Nov 2001 16:06:20 jbp moved receipt logic form
 * printTransactionReceipt Aisle to Utility Manager Resolution for POS SCR-221:
 * Receipt Design Changes
 *
 * Rev 1.1 10 Oct 2001 15:22:26 jbp save customer info upon initializtion of
 * transaction Resolution for POS SCR-207: Prompt for Customer Info
 *
 * Rev 1.0 Sep 21 2001 11:09:58 msg Initial revision.
 *
 * Rev 1.1 Sep 17 2001 13:05:08 msg header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.manager.utility;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JFormattedTextField;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.StoreDataTransaction;
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.utility.CardType;
import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.domain.utility.CardTypeUtility;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.StateIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.parameter.ParameterIfc;
import oracle.retail.stores.foundation.manager.parameter.EnumeratedListValidator;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.manager.Manager;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.CreditTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.CodeListManagerIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.utility.CheckDigitUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * The UtilityManager implements utility methods used in the POS application.
 * Prior to release 4.5.0, these methods were implemented as static methods in
 * oracle.retail.stores.pos.services.common.DefaultSite.
 * <P>
 * These methods are generally used to initiate and save transaction. Those
 * activities are used in several places in the application, and the
 * UtilityManager provides a single, extendable implementation point for those
 * actions.
 * <P>
 * Typically, this manager won't be associated with a technician.
 */
public class UtilityManager extends Manager implements UtilityManagerIfc
{
    /** The logger to which log messages will be sent. */
    private static final Logger logger = Logger.getLogger(UtilityManager.class);

    /**
     * The list of supported countries
     */
    protected static CountryIfc[] countries = null;

    /**
     * The list of supported locales
     */
    protected static Locale[] supportedLocales = null;

    /**
     * current UI locale
     */
    protected static Locale currentUILocale = null;

    /**
     * The collection of credit card objects.
     */
    protected static CardType singletonCardType = null;

    /**
     * The flat file configuration for the credit card objects.
     */
    protected static String cardTypeRulesFile = "";

    /**
     * IMEI Enabled/Disabled Property name
     */
    protected static final String IMEIProperty = "IMEIEnabled";

    /**
     * IMEI Enabled/Disabled Property name
     */
    protected static final String SerializationProperty = "SerializationEnabled";

    /**
     * IMEI Enabled/Disabled Property name
     */
    protected static final String IMEIFieldLengthProperty = "IMEIFieldLength";

    /**
     * The default constructor; sets up a unique address.
     * 
     * @exception IllegalStateException is thrown if the manager cannot be
     *                created.
     */
    public UtilityManager()
    {
        getAddress();
    }

    /**
     * Updates the store status from the database.
     * 
     * @param status The last known store status
     * @return The store status
     * @exception DataException upon error
     */
    public StoreStatusIfc refreshStoreStatus(StoreStatusIfc status) throws DataException
    {
        try
        {
            // This would be handled better if we had asynchronous
            // notification of a change in store status.
            // Since we currently don't have this, we may need to update
            // the store status twice. Once if we think the store is
            // opened and a second time if that business day is closed.
            // This covers the possibility that someone has closed what
            // we think is the current business day and then opened a
            // new business day.
            StoreDataTransaction dt = null;

            dt = (StoreDataTransaction) DataTransactionFactory.create(DataTransactionKeys.STORE_DATA_TRANSACTION);

            if (status.getStatus() == AbstractStatusEntityIfc.STATUS_OPEN)
            {
                // confirm the store status
                dt.refreshStoreStatus(status);
            }

            if (status.getStatus() != AbstractStatusEntityIfc.STATUS_OPEN)
            {
                // Check to see if a new business day has been opened
                status = dt.readStoreStatus(status.getStore().getStoreID());
            }
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }

        return (status);
    }

    /**
     * Checks for a change in UI locale
     * 
     * @return boolean Returns true if the current UI has changed false
     *         otherwise
     */
    public boolean hasUILocaleChanged()
    {
        return !LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE).equals(currentUILocale);
    }

    /**
     * Retrieves the list of supported countries and its Administrative Regions
     * as specified on the application.xml
     * 
     * @param pm the Parameter Manager
     * @return array of countries
     */
    public CountryIfc[] getCountriesAndStates(ParameterManagerIfc pm)
    {
        if (countries == null || hasUILocaleChanged())
        {
            currentUILocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            // get list of countries from parameterManager
            Serializable[] stateCodes = null;
            Serializable[] countryCodes = null;

            // Retrieve the list of supported countries from the system
            // parameters
            try
            {

                ParameterIfc storeCountry = pm.getSource().getParameter("StoreCountry");

                if (storeCountry.getValidator() instanceof EnumeratedListValidator)
                {
                    countryCodes = ((EnumeratedListValidator) storeCountry.getValidator()).getAllowableValues();
                }

                // Retrieve the list of states for the supported countries from
                // the system parameters
                ParameterIfc storeStates = pm.getSource().getParameter("StoreStateProvince");
                if (storeStates.getValidator() instanceof EnumeratedListValidator)
                {
                    stateCodes = ((EnumeratedListValidator) storeStates.getValidator()).getAllowableValues();
                }
                
                if (countryCodes != null)
                {
                    countries = new CountryIfc[countryCodes.length];
    
                    for (int i = 0; i < countryCodes.length; i++)
                    {
                        CountryIfc countryInfo = getCountryProperties((String) countryCodes[i]);
                        String countryName = retrieveText("Common", BundleConstantsIfc.LOCALIZATION_BUNDLE_NAME,
                                (String) countryCodes[i], (String) countryCodes[i]);
                        countryInfo.setCountryName(countryName);
    
                        if (stateCodes != null)
                        {
                            StateIfc[] stateList = null;
                            ArrayList<StateIfc> alist = new ArrayList<StateIfc>();
                            for (int j = 0; j < stateCodes.length; j++)
                            {
                                if (((String) stateCodes[j]).startsWith((String) countryCodes[i]))
                                {
                                    StateIfc aState = DomainGateway.getFactory().getStateInstance();
                                    // Remove the appended country and store
                                    // only the ISO code for the state
                                    aState.setStateCode(((String) stateCodes[j]).substring(3, ((String) stateCodes[j])
                                            .length()));
                                    String stateName = retrieveText("Common", BundleConstantsIfc.LOCALIZATION_BUNDLE_NAME,
                                            (String) stateCodes[j], (String) stateCodes[j]);
                                    aState.setStateName(stateName);
                                    aState.setCountryCode((String) countryCodes[i]);
                                    alist.add(aState);
                                }
                            }
                            // sort the state list based on state name
                            Collections.sort(alist, new Comparator<StateIfc>()
                            {
                                public int compare(StateIfc o1, StateIfc o2)
                                {
                                    return LocaleUtilities.compareValues(o1.getStateName(), o2.getStateName());
                                }
                            });
                            stateList = new StateIfc[alist.size()];
                            alist.toArray(stateList);
                            countryInfo.setStates(stateList);
                            countries[i] = countryInfo;
                        }
                    }
                }
            }
            catch (ParameterException e)
            {
            }

        }
        return countries;
    }

    /**
     * Retrieves the country properties from the property file
     * 
     * @param country the country code
     * @return The country
     */
    protected CountryIfc getCountryProperties(String country)
    {
        // ask it to read the countries properties

        CountryIfc countryInfo = DomainGateway.getFactory().getCountryInstance();

        // use default values if properties are not defined for the specified
        // country
        countryInfo.setCountryCode(country);
        countryInfo.setPhoneFormat(DomainGateway.getProperty(country + LocaleConstantsIfc.PHONE_MASK, null));
        countryInfo.setPostalCodeFormat(DomainGateway.getProperty(country + LocaleConstantsIfc.POSTAL_MASK, null));
        countryInfo.setExtPostalCodeFormat(DomainGateway
                .getProperty(country + LocaleConstantsIfc.EXT_POSTAL_MASK, null));

        String postalRequired = DomainGateway.getProperty(country + LocaleConstantsIfc.POSTAL_CODE_REQUIRED, "false");
        countryInfo.setPostalCodeRequired(Boolean.valueOf(postalRequired));

        String extPostalRequired = DomainGateway.getProperty(country + LocaleConstantsIfc.EXT_POSTAL_CODE_ENABLED,
                "false");
        countryInfo.setExtPostalCodeRequired(Boolean.valueOf(extPostalRequired));
        countryInfo.setPostalCodeDelimiter(DomainGateway.getProperty(
                country + LocaleConstantsIfc.POSTAL_CODE_DELIMITER, ""));
        countryInfo.setAddressDelimiter(DomainGateway.getProperty(country + LocaleConstantsIfc.ADDRESS_DELIMITER, ""));

        return countryInfo;
    }

    /**
     * Retrieves the index in the country array given the country code
     * 
     * @param code the country code
     * @param pm parameter manager reference
     * @return index in the country array
     */
    public int getCountryIndex(String code, ParameterManagerIfc pm)
    {
        int index = 0;
        if (getCountriesAndStates(pm) != null)
        {
            for (int i = 0; i < countries.length; i++)
            {
                if (code.equals(countries[i].getCountryCode()))
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * Retrieves the index in the state array given the state code
     * 
     * @param countryIndex country index
     * @param code the state code
     * @param pm parameter manager reference
     * @return index in the state array
     */
    public int getStateIndex(int countryIndex, String code, ParameterManagerIfc pm)
    {
        int index = 0;
        if (getCountriesAndStates(pm) != null)
        {
            StateIfc[] states = countries[countryIndex].getStates();
            for (int i = 0; i < states.length; i++)
            {
                if (code.equals(states[i].getStateCode()))
                {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * Retrieves text through international text support facility for specified
     * spec name, bundle name and property. Implements default if property not
     * found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveText(String specName, String bundleName, String propName, String defaultValue)
    {
        return retrieveText(specName, bundleName, propName, defaultValue, LocaleConstantsIfc.USER_INTERFACE);
    }

    /**
     * Retrieves text through international text support facility for specified
     * spec name, bundle name and property. Implements default if property not
     * found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @param defaultValue default value
     * @param subsystem the subsystem which determines which locale to use
     * @return text from support facility
     */
    public String retrieveText(String specName, String bundleName, String propName, String defaultValue,
            String subsystem)
    {
        Locale locale = LocaleMap.getLocale(subsystem);
        return (retrieveText(specName, bundleName, propName, defaultValue, locale));
    }

    /**
     * Retrieves text through ResourceBundleUtil facility for specified spec
     * name, bundle name and property. Implements default if property not found.
     * 
     * @param specName bean specification name
     * @param bundleName bundle in which to search for answer
     * @param propName property key
     * @param defaultValue default value
     * @param locale the locale used to retrieve the bundle
     * @return text from support facility
     */
    public String retrieveText(String specName, String bundleName, String propName, String defaultValue, Locale locale)
    {
        Properties props = null;
        if (Util.isObjectEqual(bundleName, BundleConstantsIfc.COMMON_BUNDLE_NAME))
        {
            props = getBundleProperties(specName, BundleConstantsIfc.COMMON_BUNDLE_NAME, locale);
        }
        else
        {
            // use multiple bundles to include common
            String bundles[] = { BundleConstantsIfc.COMMON_BUNDLE_NAME, bundleName };
            props = getBundleProperties(specName, bundles, locale);
        }

        String returnValue = null;

        // Adding brakets to the property names help us
        // determine if text is comming from bundles or the
        // default values. To activate the BUNDLE_TESTING flag
        // the application has to be run with
        // -DBUNDLE_TESTING
        String testPropName = "<" + propName + ">";
        boolean testingBundles = (System.getProperty("BUNDLE_TESTING") != null);
        if (props == null)
        {
            if (testingBundles)
            {
                returnValue = testPropName;
            }
            else
            {
                returnValue = defaultValue;
            }

        }
        else
        {
            if (testingBundles)
            {
                returnValue = props.getProperty(propName, testPropName);
            }
            else
            {
                returnValue = props.getProperty(propName, defaultValue);
            }

        }
        return (returnValue);
    }

    /**
     * Retrieves a handle to the bundle properties
     * 
     * @param tag bean key name
     * @param bundle bundle in which to search for answer
     * @param locale the locale used to retrieve the bundle
     * @return Properties handle to the bundle
     */
    public Properties getBundleProperties(String tag, String bundle, Locale locale)
    {
        Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
        Properties props = ResourceBundleUtil.getGroupText(tag, bundle, bestMatchLocale);
        return props;
    }

    /**
     * Retrieves a handle to the bundle properties
     * 
     * @param tag bean key name
     * @param bundles bundles in which to search for answer
     * @param locale the locale used to retrieve the bundle
     * @return Properties handle to the bundle
     */
    public Properties getBundleProperties(String tag, String[] bundles, Locale locale)
    {
        Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
        Properties props = ResourceBundleUtil.getGroupText(tag, bundles, bestMatchLocale);
        return props;
    }

    /**
     * Retrieves journal text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveJournalText(String propName, String defaultValue)
    {
        return (retrieveText("JournalEntry", BundleConstantsIfc.EJOURNAL_BUNDLE_NAME, propName, defaultValue,
                LocaleConstantsIfc.JOURNAL));
    }

    /**
     * Retrieves report text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveReportText(String propName, String defaultValue)
    {
        return (retrieveText("ReportSpec", BundleConstantsIfc.REPORTS_BUNDLE_NAME, propName, defaultValue,
                LocaleConstantsIfc.REPORTS));
    }

    /**
     * Retrieves dialog text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveDialogText(String propName, String defaultValue)
    {
        return (retrieveText(POSUIManagerIfc.DIALOG_SPEC, BundleConstantsIfc.DIALOG_BUNDLE_NAME, propName,
                defaultValue, LocaleConstantsIfc.USER_INTERFACE));
    }

    /**
     * Retrieves dialog text through international text support facility.
     * 
     * @param propName property key
     * @return text from support facility
     */
    public String retrieveCommonText(String propName)
    {
        return retrieveCommonText(propName, propName);
    }

    /**
     * Retrieves dialog text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveCommonText(String propName, String defaultValue)
    {
        return (retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, propName, defaultValue,
                LocaleConstantsIfc.USER_INTERFACE));
    }

    /**
     * Retrieves common text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @param subsystem id that identifies the subsystem retrieving the message
     * @return text from support facility
     */
    public String retrieveCommonText(String propName, String defaultValue, String subsystem)
    {
        return (retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, propName, defaultValue, subsystem));
    }

    /**
     * Retrieves common text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @param locale the locale of the desired text
     * @return text from support facility
     */
    public String retrieveCommonText(String propName, String defaultValue, Locale locale)
    {
        return (retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, propName, defaultValue, locale));
    }

    /**
     * Retrieve translation for supported Locales
     * 
     * @param localeKey
     * @param defaultValue
     * @param locale
     * @return
     */
    public String getLocaleDisplayName(String localeKey, String defaultValue, Locale locale)
    {
        return (retrieveCommonText(localeKey, defaultValue, locale));
    }

    /**
     * Retrieves line-display text through international text support facility.
     * 
     * @param propName property key
     * @param defaultValue default value
     * @return text from support facility
     */
    public String retrieveLineDisplayText(String propName, String defaultValue)
    {
        return (retrieveText(BundleConstantsIfc.LINE_DISPLAY_SPEC, BundleConstantsIfc.LINE_DISPLAY_BUNDLE_NAME,
                propName, defaultValue, LocaleConstantsIfc.POLE_DISPLAY));
    }

    /**
     * Validates the check digit (last element in String number) according to
     * the algorithm mapped to posFunction. If String posFunction is not mapped
     * to a CheckDigitStrategy in CheckDigitUtility, this method returns true
     * and no validation is performed.
     * 
     * @param posFunction - the name of the pos function requesting validation
     * @param number - a numeric String, with last element containing the check
     *            digit
     * @return boolean true if the check digit is valid for the given function
     *         or if the function is not configured in the CheckDigitUtility
     * @see oracle.retail.stores.pos.utility.CheckDigitUtility
     */
    public boolean validateCheckDigit(String posFunction, String number)
    {
        boolean valid = true;
        CheckDigitUtility util = CheckDigitUtility.getInstance();

        if (util.isConfigured(posFunction))
        {
            valid = util.validateCheckDigit(posFunction, number.getBytes());
        }

        return valid;
    }

    /**
     * Validates the check digit (last element in String number) according to
     * the algorithm mapped to posFunction. If String posFunction is not mapped
     * to a CheckDigitStrategy in CheckDigitUtility, this method returns true
     * and no validation is performed.
     * 
     * @param posFunction - the name of the pos function requesting validation
     * @param cardData - Instance of the EncipheredCardData
     * @return boolean true if the check digit is valid for the given function
     *         or if the function is not configured in the CheckDigitUtility
     * @see oracle.retail.stores.pos.utility.CheckDigitUtility
     */
    public boolean validateCheckDigit(String posFunction, EncipheredCardDataIfc cardData)
    {
        boolean valid = false;
        if (cardData != null && (cardData.isCheckDigitEvaluated() == false))
        {
            KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc) Gateway.getDispatcher()
                    .getManager(KeyStoreEncryptionManagerIfc.TYPE);
            byte[] cardNumber = null;
            try
            {
                cardNumber = encryptionManager.decrypt(Base64
                        .decodeBase64(cardData.getEncryptedAcctNumber().getBytes()));
            }
            catch (EncryptionServiceException ese)
            {
                logger.error("Couldn't decrypt card number", ese);
            }
            CheckDigitUtility util = CheckDigitUtility.getInstance();

            if (util.isConfigured(posFunction))
            {
                valid = util.validateCheckDigit(posFunction, cardNumber);
            }
            else
            {
                // were not checking the check digit.
                valid = true;
            }
            // clear the cardNumber
            Util.flushByteArray(cardNumber);
            cardData.setCheckDigitValid(valid);
        }
        else if (cardData != null)
        {
            valid = cardData.isCheckDigitValid();
        }
        return valid;
    }

    /**
     * Retrieves the string corresponding to the error represented by errorCode
     * translated into the Locale specified for the user interface.
     * 
     * @param errorCode int representing the error that has occurred. The
     *            {@link oracle.retail.stores.foundation.manager.data.DataException}
     *            class has the corresponding error code constants.
     * @return String containing the corresponding error message for the given
     *         errorCode and translated per the Locale for the user interface.
     * @see oracle.retail.stores.foundation.manager.data.DataException
     */
    public String getErrorCodeString(int errorCode)
    {
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String errorCodeIntString = LocaleUtilities.formatNumber(errorCode, locale);
        String errorCodeString = this.retrieveText("Error", BundleConstantsIfc.COMMON_BUNDLE_NAME, errorCodeIntString,
                "An unknown exception occurred.");

        return errorCodeString;

    }

    /**
     * Set the filename of the card rules file.
     * 
     * @param filename the String filename
     */
    public void setCardTypeRulesFile(String filename)
    {
        cardTypeRulesFile = filename;
    }

    /**
     * Return the filename of the card rules file.
     * 
     * @return the filename of the flat file rules
     */
    public String getCardTypeRulesFile()
    {
        return cardTypeRulesFile;
    }

    /**
     * Return a configured CardType through the CardType utility.
     * 
     * @return a configured CardType object
     */
    public CardType getConfiguredCardTypeInstance()
    {
        CardTypeUtility util = CardTypeUtility.getInstance(getCardTypeRulesFile());
        return util.getCardTypeInstance();
    }

    /**
     * Given a card number, attempt to find out what type of credit (VISA, MC,
     * etc)
     * 
     * @param cardData The EncipheredCardData instance to test
     * @return The appropriate enumerated credit type.
     */
    public CreditTypeEnum determineCreditType(EncipheredCardDataIfc cardData)
    {
        CardTypeIfc cardTypeUtility = getConfiguredCardTypeInstance();

        // return the card type
        String cardType = cardTypeUtility.identifyCardType(cardData, TenderTypeEnum.CREDIT.toString());
        return CreditTypeEnum.makeEnumFromString(cardType);
    }

    /**
     * This method returns an employee id string after being given a prompt and
     * response model.
     * 
     * @param pAndRModel
     * @return employeeID string
     */
    public String getEmployeeFromModel(PromptAndResponseModel pAndRModel)
    {
        String employeeID = null;
        MSRModel msrModel = pAndRModel.getMSRModel();
        // we are using the credit card as an employee id card
        // see if the surname is not null, it is not equal to empty string
        // and the first chaacter is ' '
        // if it is set surname to " "
        if (msrModel.getSurname() == null || msrModel.getSurname().equals(""))
        {
            employeeID = " ";
        }
        else
        if (msrModel.getSurname().length() > 10)
        {
            employeeID = msrModel.getSurname().substring(0, 10);
        }
        else
        {
            employeeID = msrModel.getSurname();
        }
        return employeeID;
    }

    /**
     * If we are in training mode, this method will check the
     * SendTrainingModeTransactionToJournal parameter to see if it set to Y. If
     * it is set to Y, the training mode transaction should be written to the
     * e-journal
     * 
     * @param trainingModeOn - set to true for training mode
     * @return true if the transaction should be written to the e-journal
     */
    public boolean journalTransaction(boolean trainingModeOn)
    {
        boolean journalOn = true;
        // If in training mode then check to see if
        // SendTrainingModeTransactionsToJournal parameter
        // is set to Yes to journal training mode transactions
        if (trainingModeOn)
        {
            // get parameter manager
            UtilityIfc util = null;
            try
            {
                util = Utility.createInstance();
            }
            catch (ADOException e)
            {
                String message = "Configuration problem: could not instantiate UtilityIfc instance";
                logger.error(message, e);
                throw new RuntimeException(message, e);
            }
            journalOn = util.getParameterValue("SendTrainingModeTransactionsToJournal", "Y").equalsIgnoreCase("Y");
        }
        return (journalOn);
    }

    /**
     * Returns the a Locale Requestor with all potentially required locales
     * 
     * @return A locale requestor object with all required locales.
     */
    public LocaleRequestor getRequestLocales()
    {
        LocaleRequestor localeRequestor = LocaleMap.getSupportedLocaleRequestor();
        return localeRequestor;
    }

    /**
     * Returns reason code text given a String constant and int reason code.
     * 
     * @param String codeConstant value
     * @param int reasonCode value
     * @return reason code text
     * @see oracle.retail.stores.domain.utility.CodeConstantsIfc
     */
    public String getReasonCodeText(CodeListIfc list, int reasonCode)
    {
        String value = "";
        if (list != null)
        {
            CodeEntryIfc entry = list.findListEntryByCode(Integer.toString(reasonCode));
            if (entry != null)
            {
                String text = entry.getText(LocaleMap.getLocale(LocaleMap.DEFAULT));
                if (text != null)
                {
                    value = text;
                }
            }
        }

        return value;
    }

    /**
     * Returns phone format for a country defined in domain.properties
     * 
     * @param String countryCode
     * @return PhoneMask corresponding to countryCode
     */
    public String getPhoneFormat(String countryCode)
    {
        String phoneFormat = DomainGateway.getProperty(countryCode + LocaleConstantsIfc.PHONE_MASK, "");
        return phoneFormat;
    }

    /**
     * Returns regular expression validation string for a country defined in
     * domain.properties
     * 
     * @param String countryCode
     * @return PhoneValidationRegexp corresponding to countryCode
     */
    public String getPhoneValidationRegexp(String countryCode)
    {
        String validationRegexp = DomainGateway.getProperty(countryCode + ".PhoneValidationRegexp", "");
        return validationRegexp;
    }

    /**
     * Returns the regular expression used for email address format validation,
     * defined in domain.properties
     * 
     * @return the regex pattern
     */
    public String getEmailValidationRegexp()
    {
        String validationRegexp = DomainGateway.getProperty("EmailValidationRegexp", "");
        return validationRegexp;
    }

    /**
     * get a phone number formatted as per the phone pattern for that country
     * 
     * @return formatted phone number
     */
    public String getFormattedNumber(String phoneNumber, String countryCode)
    {
        String formatted = phoneNumber;
        try
        {
            String phoneMask = getPhoneFormat(countryCode);
            if (phoneMask != null && phoneMask.length() > 0)
            {
                MaskFormatter mf = new MaskFormatter(phoneMask);
                mf.setValueContainsLiteralCharacters(false);

                JFormattedTextField ftf = new JFormattedTextField();
                DefaultFormatterFactory factory = new DefaultFormatterFactory(mf);
                ftf.setFormatterFactory(factory);

                ftf.setValue(formatted);
                formatted = ftf.getText();
            }
        }
        catch (ParseException ex)
        {
            logger.debug("ParseException ex", ex);
        }

        return formatted;
    }

    /**
     * Returns postalcode format for a country defined in domain.properties
     * 
     * @param String countryCode
     * @return PostalCodeMask corresponding to countryCode
     **/
    public String getPostalCodeFormat(String countryCode)
    {
        String phoneFormat = DomainGateway.getProperty(countryCode + LocaleConstantsIfc.POSTAL_MASK, "");
        return phoneFormat;
    }

    /**
     * Returns regular expression validation string for a country defined in
     * domain.properties
     * 
     * @param String countryCode
     * @return PostalCodeValidationRegexp corresponding to countryCode
     **/
    public String getPostalCodeValidationRegexp(String countryCode)
    {
        String validationRegexp = DomainGateway.getProperty(countryCode + ".PostalCodeValidationRegexp", "");
        return validationRegexp;
    }

    /**
     * Gets the Post Void reason codes from the CodeListManager. As of version
     * 13.1 we are no longer caching reason codes. They come from the
     * {@link CodeListManagerIfc} which is usually configured to retrieve them
     * from the Derby database.
     * 
     * @param String storeId
     * @param String codeListType
     */
    public CodeListIfc getReasonCodes(String storeId, String codeListType)
    {
        CodeListIfc reasonCodes = null;

        CodeListManagerIfc codeListManager = (CodeListManagerIfc) Dispatcher.getDispatcher().getManager(
                CodeListManagerIfc.TYPE);
        CodeListSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeListSearchCriteriaInstance();
        criteria.setStoreID(storeId);
        criteria.setListID(codeListType);
        criteria.setLocaleRequestor(getRequestLocales());

        reasonCodes = codeListManager.getCodeList(criteria);

        return reasonCodes;
    }

    /**
     * Gets the IMEI Enabled/Disabled property
     * 
     * @return boolean
     */
    public boolean getIMEIProperty()
    {
        return (Gateway.getBooleanProperty("application", IMEIProperty, false));
    }

    /**
     * Gets the Serialisation Enabled/Disabled property
     * 
     * @return boolean
     */
    public boolean getSerialisationProperty()
    {
        return (Gateway.getBooleanProperty("application", SerializationProperty, false));
    }

    /**
     * Gets the IMEI Field Length Property
     * 
     * @return boolean
     */
    public String getIMEIFieldLengthProperty()
    {
        return (Gateway.getProperty("domain", IMEIFieldLengthProperty, "15"));
    }

    /**
     * This method is used to create dialog box specific for data/queue
     * exceptions
     * 
     * @param ex DataException
     * @return DialogBeanModel
     */
    public DialogBeanModel createErrorDialogBeanModel(DataException ex)
    {
        return createErrorDialogBeanModel(ex, true);
    }

    /**
     * This method is used to create dialog box specific for certain data/queue
     * exceptions
     * 
     * @param ex DataException
     * @param defaultModel boolean
     * @return DialogBeanModel
     */
    public DialogBeanModel createErrorDialogBeanModel(DataException ex, boolean defaultModel)
    {
        String errorString[];
        DialogBeanModel dialogModel = new DialogBeanModel();

        if (ex.getErrorCode() == DataException.QUEUE_FULL_ERROR)
        {
            errorString = new String[1];
            errorString[0] = getErrorCodeString(ex.getErrorCode());
            dialogModel.setResourceID("QueueFullError");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(errorString);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "QueueFull");
        }
        else if (ex.getErrorCode() == DataException.STORAGE_SPACE_ERROR)
        {
            errorString = new String[1];
            errorString[0] = getErrorCodeString(DataException.STORAGE_SPACE_ERROR);
            dialogModel.setResourceID("QueueError");
            dialogModel.setType(DialogScreensIfc.RETRY);
            dialogModel.setArgs(errorString);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
        }
        else if (ex.getErrorCode() == DataException.QUEUE_OP_FAILED)
        {
            errorString = new String[1];
            errorString[0] = getErrorCodeString(ex.getErrorCode());
            dialogModel.setResourceID("DATABASE_ERROR_RETRY");
            dialogModel.setType(DialogScreensIfc.RETRY);
            dialogModel.setArgs(errorString);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Retry");
        }
        else if (defaultModel)
        {
            errorString = new String[2];
            errorString[0] = getErrorCodeString(ex.getErrorCode());
            errorString[1] = "";
            dialogModel.setResourceID("TranDatabaseError");
            dialogModel.setType(DialogScreensIfc.ERROR);
            dialogModel.setArgs(errorString);
        }

        return dialogModel;
    }

}
