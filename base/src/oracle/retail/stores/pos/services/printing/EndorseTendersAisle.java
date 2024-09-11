/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/printing/EndorseTendersAisle.java /main/57 2014/05/15 14:30:33 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     08/22/14 - fixed check franking problem, bug 18638712, only printing 
 *                         the heading.
 *    cgreene   05/15/14 - XbranchMerge cgreene_bug-18686513 from
 *                         rgbustores_14.0x_generic_branch
 *    cgreene   05/15/14 - prevent endorsing ATG orders
 *    yiqzhao   03/13/14 - Franking the current tender information.
 *    tksharma  12/12/13 - Added code to check if StoreCoupon is part of
 *                         franking list before prompting
 *    abondala  09/04/13 - initialize collections
 *    mkutiana  08/30/13 - Moving Echeck Authorization message text to
 *                         blueprint file
 *    rsnayak   04/12/12 - Franking Blueprint receipts
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    mchellap  09/29/10 - BUG#10153387 Fixed giftcertificate franking after
 *                         printer timeout
 *    ohorne    08/18/10 - external order support for training mode
 *    acadar    07/30/10 - use external order number
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    ohorne    06/08/10 - added External Order Franking
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    jswan     03/04/10 - Modifed to prevent coupons associated with a return
 *                         from triggering coupon franking.
 *    abondala  01/03/10 - update header date
 *    blarsen   08/31/09 - XbranchMerge
 *                         blarsen_bug8822427-issue-amt-wrong-on-franked-gift-cert
 *                         from rgbustores_13.1x_branch
 *    blarsen   08/24/09 - 'Amount: 5.00' franked for 5,000.00 gift cert. Gift
 *                         Cert header formatter was formatting the amount
 *                         string which was preformatted by the caller. The
 *                         separator (comma for USD) was not handled by the
 *                         parser. Parser only considered value to left of
 *                         comma. Removed offending code which is not required
 *                         since value is preformatted by caller.
 *    blarsen   04/16/09 - Added a comment to clairfy mail bank check franking.
 *    nkgautam  04/16/09 - fox for displaying name in mail bank check franking
 *                         slip for any customer
 *    acadar    04/10/09 - take into account nr. of spaces that we used to pad
 *                         to the left
 *    acadar    04/10/09 - center align Void label
 *    sswamygo  04/02/09 - Updated to Match Requirements for Post void store
 *                         credit tender.
 *    sswamygo  04/02/09 - Updated the static var REDEEMED_DEFAULT = Tendered
 *    sswamygo  04/01/09 - Updated the Franking text for Tender of Gift Cert.
 *                         to be Left Indent.
 *    acadar    03/27/09 - removed unused comments
 *    acadar    03/27/09 - franking of void redeem gift certificate
 *    blarsen   03/26/09 - Printer goes offline when franking runs out of
 *                         paper. Added check for SLIP_EMPTY exception code.
 *    acadar    03/24/09 - do not show the id number when endorsing a check
 *    blarsen   03/19/09 - Replaced hard-coded advance-slip-before-printing
 *                         values with configurable values specified in the
 *                         posdevices.xml file. (new properties)
 *    abondala  03/13/09 - getting text from wrong bundle for the Mall gift
 *                         Certificate.
 *    blarsen   03/10/09 - made franking use the new slipPrintSize printer
 *                         device property - enhanced formatting code to
 *                         consider that some characters, say chinese, are
 *                         double wide -
 *    blarsen   03/06/09 - added escape codes to make slip printer use
 *                         single-wide font. added footer to set printer back
 *                         to normal as a precaution.
 *    masahu    03/11/09 - Refranking of Money Order, Trav Chq and Mall Cert
 *    ranojha   02/27/09 - Fix giftcertificate issued slip
 *    arathore  02/20/09 - Updated as per review comments.
 *    arathore  02/19/09 - Updated to add store credit deatails for sale post
 *                         void transaction.
 *    arathore  02/18/09 - Corrected franking format for Gift Cert.
 *    arathore  02/16/09 - corrected format for Store Credit Redeem franking.
 *    arathore  02/16/09 - Removed padding from Customer Name.
 *    arathore  02/16/09 - Corrected the format for Mail Bank check phone
 *                         number.
 *    arathore  02/14/09 - Updated to pass Personal Id information to printing
 *                         tour.
 *    arathore  02/14/09 - Corrected the format for Mail Bank check franking.
 *    sgu       02/11/09 - fix alternate currency franking amount
 *    sgu       01/15/09 - convert string to decimal format before calling
 *                         CurrencyIfc.setStringValue
 *    miparek   01/09/09 - fixed d#1342,modified key of
 *                         store_credit_no_space_prop to StoreCreditText
 *    cgreene   11/11/08 - use phone list instead of vector
 *    abondala  11/06/08 - updated files related to reason codes
 *
 * ===========================================================================
 * $Log:
 *    11   360Commerce 1.10        6/10/2008 5:16:07 PM   Deepti Sharma
 *         Removed Total Tender and Change Due from printer slip for mail bank
 *          check. Code reviewed by Alan Sinton
 *    10   360Commerce 1.9         3/21/2008 8:48:29 AM   Mathews Kochummen
 *         forward port from v12x to trunk. reviewed by alan
 *    9    360Commerce 1.8         3/13/2008 11:19:16 AM  Mathews Kochummen
 *         forward port from v12x to trunk. reviewed by michael
 *    8    360Commerce 1.7         11/15/2007 11:14:31 AM Christian Greene
 *         Belize merge - check for deleted layaways
 *    7    360Commerce 1.6         8/14/2007 6:19:57 AM   VIVEKANAND KINI Fixed
 *          CR 4362
 *    6    360Commerce 1.5         7/9/2007 12:54:48 PM   Mathews Kochummen use
 *          locale format date,time
 *    5    360Commerce 1.4         4/25/2007 8:52:16 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/25/2006 4:11:00 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:23 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *:
 *    6    .v700     1.2.1.2     10/31/2005 08:39:27    Rohit Sachdeva
 *         Franking should use Default Locale in Product
 *    5    .v700     1.2.1.1     10/26/2005 13:18:03    Rohit Sachdeva  CR#
 *         6096 Internationalizing "Amount Issued" for  Store Credit Issued to
 *         use Default Locale
 *    4    .v700     1.2.1.0     10/24/2005 12:49:11    Jason L. DeLeau 6135:
 *         Make sure you can decline authorization on the call referral page,
 *         during a post void transaction when the GC authorizer is offline.
 *    3    360Commerce1.2         3/31/2005 15:28:00     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:23     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:53     Robert Pearse
 *
 *   Revision 1.63.2.3  2004/11/01 19:47:10  rsachdeva
 *   @scr 7565 Purchase Order Number
 *
 *   Revision 1.63.2.2  2004/10/29 18:18:12  bwf
 *   @scr 7445 Fix getNumber by checking null incase id was swiped.
 *
 *   Revision 1.63.2.1  2004/10/25 21:01:18  kll
 *   @scr 7499: improper computation of change with respect to MailBankCheck tenders
 *
 *   Revision 1.63  2004/09/27 20:11:52  kll
 *   @scr 6648: spacing fix
 *
 *   Revision 1.62  2004/09/23 19:11:57  bwf
 *   @scr 7165 Now put tender type and amount on insert tender screen.
 *
 *   Revision 1.61  2004/09/22 21:06:06  kll
 *   @scr 6648: alternative address retrieval for TenderMailBankCheck
 *
 *   Revision 1.60  2004/09/01 16:25:43  kll
 *   @scr 3660: case where line size < header
 *
 *   Revision 1.59  2004/08/27 21:33:05  crain
 *   @scr 6024 Printing: Post Void franking on Gift Certificate Issue using the same footprint
 *
 *   Revision 1.58  2004/08/27 16:56:12  kll
 *   @scr 5757: certificate mod
 *
 *   Revision 1.57  2004/08/10 01:31:59  crain
 *   @scr 6533 Franking text for Tender of Gift Certificates does not match requirements
 *
 *   Revision 1.56  2004/08/09 18:52:36  crain
 *   @scr 6528 Franking text for Issue of Gift Certificate does not match requirements
 *
 *   Revision 1.55  2004/08/06 22:59:03  crain
 *   @scr 6528 Franking text for Issue of Gift Certificate does not match requirements
 *
 *   Revision 1.54  2004/08/02 22:49:41  blj
 *   @scr 3242 - used fix provided in scr
 *
 *   Revision 1.53  2004/07/29 22:15:46  bvanschyndel
 *   @scr 6528 Localized changes of the Void transaction slip.
 *   Changes for 6280 affected the sale slips.
 *
 *   Revision 1.52  2004/07/29 20:02:05  blj
 *   @scr 4530 - updated per scr
 *
 *   Revision 1.51  2004/07/26 21:49:10  blj
 *   @scr 4530 defect resolution
 *
 *   Revision 1.50  2004/07/23 13:44:25  kll
 *   @scr 6363: left alignment of Mail Bank Check information
 *
 *   Revision 1.49  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.48  2004/07/20 18:59:32  kll
 *   @scr 6363: left justification of Mail Bank Check information
 *
 *   Revision 1.47  2004/07/17 13:58:08  bvanschyndel
 *   @scr 6279, 6280 Fixed endorsement slip formatting for Check and Gift Certificate Post Void
 *
 *   Revision 1.46  2004/07/16 23:03:15  crain
 *   @scr 6301 Franking insert screen for Mall GC incorrect
 *
 *   Revision 1.45  2004/07/16 22:52:38  crain
 *   @scr 6301 Franking insert screen for Mall GC incorrect
 *
 *   Revision 1.44  2004/07/16 15:58:16  aschenk
 *   @scr 6142 - Fixed format of Store credit issue slip printing.
 *
 *   Revision 1.43  2004/07/15 21:12:52  cdb
 *   @scr 5641 Updated franking for Mail Bank Check.
 *
 *   Revision 1.42  2004/07/15 17:29:58  bwf
 *   @scr 6064 Always from tender amount.
 *
 *   Revision 1.41  2004/07/13 16:01:50  jdeleau
 *   @scr 5841 Make sure already franked gift certificates dont
 *   get refranked on a printer error.
 *
 *   Revision 1.40  2004/07/09 18:46:58  bwf
 *   @scr 6064 Get correct amount to frank gift cert with.
 *
 *   Revision 1.39  2004/07/07 20:10:20  bwf
 *   @scr 4482 Remove ID # field from check franking.
 *
 *   Revision 1.38  2004/07/07 19:51:59  blj
 *   @scr 6013 - added ability to get the amount tender from the TenderMoneyOrder object.
 *
 *   Revision 1.37  2004/07/02 02:53:35  crain
 *   @scr 5953 Franking of issued gift cert with discount in training mode
 *
 *   Revision 1.36  2004/06/29 23:42:37  blj
 *   @scr 5906 - updated per requirements
 *
 *   Revision 1.35  2004/06/22 19:38:39  crain
 *   @scr 5192 Franking of Issued Gift Certificate with discount pauses for at least 10 minutes during the franking process (before the "Discount Applied" line)
 *
 *   Revision 1.34  2004/05/28 14:06:24  blj
 *   @scr 5115, 5117 - defect resolution
 *
 *   Revision 1.33  2004/05/25 15:12:39  blj
 *   @scr 5117 - fixed printing issues for redeem store credit
 *
 *   Revision 1.32  2004/05/20 22:54:58  cdb
 *   @scr 4204 Removed tabs from code base again.
 *
 *   Revision 1.31  2004/05/20 18:59:24  jeffp
 *   @scr 4178 - added check to see if agency name is null
 *
 *   Revision 1.30  2004/05/18 16:24:53  aschenk
 *   @scr 4589 Auth no longer prints on the check deposit slip when the system is in reentry mode.
 *
 *   Revision 1.29  2004/05/17 20:54:12  blj
 *   @scr 4449- resolution
 *
 *   Revision 1.28  2004/05/16 22:11:42  blj
 *   @scr 4504 Resolution for printing inconsistency issue.
 *
 *   Revision 1.27  2004/05/13 21:52:50  crain
 *   @scr 4150 Discount Applied
 *
 *   Revision 1.26  2004/05/13 15:38:07  kll
 *   @scr 4525: Endorse Mail Bank Checks
 *
 *   Revision 1.25  2004/05/12 18:26:25  bwf
 *   @scr 4617 Put dialog message to tell user to take check or return e-check.
 *
 *   Revision 1.24  2004/05/06 14:59:32  kll
 *   @scr 3826: use properties file to obtain specific ID type
 *
 *   Revision 1.23  2004/04/27 18:45:40  lzhao
 *   @scr 4553: Gift Certificate Franking
 *
 *   Revision 1.22  2004/04/22 22:35:55  blj
 *   @scr 3872 - more cleanup
 *
 *   Revision 1.21  2004/04/22 21:41:12  cdb
 *   @scr 4503 Made Tender Store Bank Account Name active
 *
 *   Revision 1.20  2004/04/22 20:52:18  epd
 *   @scr 4513 FIxes to tender, especially gift card, gift cert, and store credit
 *
 *   Revision 1.19  2004/04/22 17:56:30  cdb
 *   @scr 4452 Removing Franking Offline Behavior parameter.
 *
 *   Revision 1.18  2004/04/22 17:17:44  cdb
 *   @scr 4452 Removing Franking Offline Behavior parameter.
 *
 *   Revision 1.17  2004/04/15 20:56:18  blj
 *   @scr 3871 - updated to fix problems with void and offline.
 *
 *   Revision 1.16  2004/03/24 20:15:55  tfritz
 *   @scr 4151 - Added Training Mode Annotation to gift certificate franking when in training mode.
 *
 *   Revision 1.15  2004/03/14 21:24:26  tfritz
 *   @scr 3884 - New Training Mode Functionality
 *
 *   Revision 1.14  2004/03/05 00:51:38  crain
 *   @scr 3421 Tender redesign
 *
 *   Revision 1.13  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.12  2004/03/02 23:41:38  crain
 *   @scr 3421 Tender redesign
 *
 *   Revision 1.11  2004/02/29 22:20:54  nrao
 *   Fixed Post Void Slip Printing for Issue Store Credit.
 *
 *   Revision 1.10  2004/02/27 18:13:44  nrao
 *   Added capture customer information to franking for issue store credit.
 *
 *   Revision 1.9  2004/02/24 22:56:54  nrao
 *   Added method for franking Issue Store Credit during post void.
 *
 *   Revision 1.8  2004/02/21 02:05:59  crain
 *   @scr 3814 Issue Gift Certificate
 *
 *   Revision 1.7  2004/02/19 18:07:55  nrao
 *   Added fix for Issue Store Credit Franking.
 *
 *   Revision 1.6  2004/02/17 19:50:38  nrao
 *   Externalized strings
 *
 *   Revision 1.5  2004/02/17 17:56:07  nrao
 *   Modified printing for Issue Store Credit
 *
 *   Revision 1.4  2004/02/13 16:48:13  kll
 *   @scr 3825: additional information added to franking Money Order slips
 *
 *   Revision 1.3  2004/02/12 16:51:40  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.16   Feb 10 2004 14:41:24   bwf
 * Refactor Echeck.
 *
 *    Rev 1.15   Jan 21 2004 15:24:28   bwf
 * Updated franking of bank information by switching order.
 *
 *    Rev 1.14   16 Jan 2004 14:36:54   awilliamson
 * Added code to print Echeck franking when in training mode
 *
 *    Rev 1.13   Jan 07 2004 18:11:14   nrao
 * Fix for SCR 3652. Checked for null pointer.
 *
 *    Rev 1.12   Dec 17 2003 15:34:30   nrao
 * Added franking for PO Tender.
 *
 *    Rev 1.11   Dec 11 2003 13:20:36   bwf
 * Updated for mall gc.
 * Resolution for 3538: Mall Certificate Tender
 *
 *    Rev 1.10   Dec 03 2003 11:25:26   Tim Fritz
 * Fixed franking of split tender of multiple Traveler's Checks.  Resolution for 1684.
 *
 *    Rev 1.9   Nov 25 2003 13:14:08   bwf
 * Update post void checks.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.8   Nov 25 2003 10:48:50   bwf
 * Fix void header for check.
 *
 *    Rev 1.7   Nov 24 2003 18:50:10   crain
 * Added post void for gift certificate
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.6   Nov 24 2003 14:43:24   cdb
 * Updated Gift Cert. franking to match requirements.
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.5   Nov 20 2003 18:22:04   bwf
 * Check franking
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.4   Oct 29 2003 15:38:30   blj
 * fixed franking for money order.
 *
 *    Rev 1.3   Oct 28 2003 15:55:02   blj
 * fixed a problem with franking money order tenders.
 *
 *    Rev 1.2   Oct 27 2003 15:42:34   blj
 * updated for money order tender franking
 *
 *    Rev 1.1   Oct 03 2003 13:46:26   rsachdeva
 * getFormattedSalesAssociateID - if required use TenderableTransactionIfc getCashier
 * Resolution for POS SCR-2654: Layaway payment with store credit tender, the sales id is null on franking
 *
 *    Rev 1.0   Aug 29 2003 16:05:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.24   10 Jul 2003 23:41:50   baa
 * add check type to message
 *
 *    Rev 1.23   Jul 08 2003 13:34:16   sfl
 * Added coupon checking before printing coupon redeem.
 * Resolution for POS SCR-3060: Franking of Store Credit -Franks Store Credit and Store Coupon on the slip.
 *
 *    Rev 1.22   Jul 07 2003 17:02:06   vxs
 * setting redeemAmountTxt correctly inside buildHeader()
 *
 *    Rev 1.21   Jul 03 2003 11:57:30   sfl
 * Added condition checking so that the actual used store credit amount will be printed during store credit redeem printing.
 * Resolution for POS SCR-2247: The amount of Store credit redeem is not correct on franking
 *
 *    Rev 1.20   May 27 2003 14:08:30   bwf
 * AGAIN, trying to have associated scrs appear in comments.
 * Resolution for 2623: POS crashes at Layaway payment with check tender
 * Resolution for 2624: POS crashes at layaway payment with gift cert. tender
 * Resolution for 2625: POS crashes at layaway payment with Trav. check / Canadian Trav. Check tender
 * Resolution for 2626: POS crashes at layaway payment with Mall Cert. tender
 *
 *    Rev 1.19   May 27 2003 14:03:56   bwf
 * Check in with scr resolution number.
 *
 *    Rev 1.18   May 27 2003 13:52:50   bwf
 * Check to make sure instanceof RetailTransactionIfc in checkStoreCoupons.
 *
 *    Rev 1.17   May 14 2003 15:42:08   adc
 * Added extra check for PaymentTransaction
 * Resolution for 2462: Paying a House Account with a Traveler's check crashes the system
 *
 *    Rev 1.16   May 09 2003 18:26:00   HDyer
 * Fixed bug in processChecks where it was getting an array index out of bounds when trying to use substr on the authMethod string.
 * Resolution for POS SCR-2284: Training Mode - System crashes when authorizing check tender
 *
 *    Rev 1.15   May 02 2003 14:49:14   sfl
 * Added conditional checking to avoid the cast exception that happened during using store credit for the payment of PARTITAL layaway payment amount.
 * Resolution for POS SCR-2248: POS Client is crashed at Store credit screen for layaway payment
 *
 *    Rev 1.14   Apr 17 2003 17:48:04   KLL
 * code review mods
 * Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 *    Rev 1.13   Apr 09 2003 13:08:10   KLL
 * removed debug statement
 * Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 *    Rev 1.12   Apr 04 2003 09:44:52   KLL
 * frank store coupons
 * Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 *    Rev 1.11   Mar 12 2003 11:22:34   KLL
 * mall certificates, gift certificates and store credit franking
 * Resolution for POS SCR-2084: Franking Functional Enhancements
 *
 *    Rev 1.10   Nov 27 2002 15:55:56   DCobb
 * Add Canadian Check tender.
 * Resolution for POS SCR-1842: POS 6.0 Canadian Check Tender
 *
 *    Rev 1.9   Nov 04 2002 11:41:58   DCobb
 * Add Mall Gift Certificate.
 * Resolution for POS SCR-1821: POS 6.0 Mall Gift Certificates
 *
 *    Rev 1.8   Sep 19 2002 09:59:06   jriggins
 * Now accomodating the message format style string in AuthLabel
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.7   Sep 13 2002 12:50:02   jriggins
 * Added blockFormatDate() and make use of it in buildHeader().  Added more bundle retrievals in processChecks().
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.6   Aug 15 2002 17:52:50   baa
 * updates for !18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.5   Aug 14 2002 18:16:32   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.4   21 May 2002 16:17:30   baa
 * fix padding for franking
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.3   17 May 2002 11:42:00   baa
 * fix padding
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.2   17 May 2002 11:24:38   baa
 * fix padding on string for franking
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   07 May 2002 22:49:40   baa
 * ils
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   01 May 2002 15:06:38   baa
 * extract text  for  internationalization
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.0   Mar 18 2002 11:44:24   msg
 * Initial revision.
 *
 *    Rev 1.4   11 Mar 2002 18:38:16   baa
 * allow 40 chars on multiline fields, acct no set to 34
 * Resolution for POS SCR-1541: TenderStoreBankAccount number parameter max should be 34
 *
 *    Rev 1.3   05 Mar 2002 09:53:38   baa
 * remove system out
 * Resolution for POS SCR-1379: Retry on Print Error for check franking did not retry.  It printed the receipt
 *
 *    Rev 1.2   01 Mar 2002 19:05:52   baa
 * fix franking multiple checks, halt /proceed options
 * Resolution for POS SCR-1362: Selecting No on Slip Printer Timeout retries franking, should not
 *
 *    Rev 1.1   06 Feb 2002 20:47:20   baa
 * defect fix
 * Resolution for POS SCR-641: Any franking problem with multi Check tenders do not frank the 2nd check
 *
 *    Rev 1.0   Sep 21 2001 11:22:44   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:11:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.printing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinterConst;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderConstantsIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCheck;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCoupon;
import oracle.retail.stores.domain.tender.TenderGiftCertificate;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheck;
import oracle.retail.stores.domain.tender.TenderMoneyOrder;
import oracle.retail.stores.domain.tender.TenderPurchaseOrder;
import oracle.retail.stores.domain.tender.TenderStoreCredit;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheck;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.device.POSPrinterActionGroupIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.receipt.FrankingReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.receipt.ReceiptTypeConstantsIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

/**
 * Print endorsement on tender documents.
 * 
 * @version $Revision: /main/57 $
 */
public class EndorseTendersAisle extends PosLaneActionAdapter
{
    /** serialVersionUID */
    private static final long serialVersionUID = -3921858959310461127L;

    /**
     * Name of this lane.
     */
    public static final String LANENAME = "EndorseTendersAisle";

    /** Bank Account Number **/
    protected static final String BANK_ACCOUNT_NUMBER = "TenderStoreBankAccountNumber";

    /** Bank Account Number **/
    protected static final String BANK_ACCOUNT_NAME = "TenderStoreBankName";

    /** Printer Timeout **/
    protected static final String PRINTER_TIMEOUT = "PrinterTimeout";

    /** Printer Error **/
    protected static final String PRINT_ERROR = "PrintError";

    /** Slip Printer Offline Retry Continue Dialog **/
    protected static final String OFFLINE_DEVICE_RETRY_CONTINUE = "RetryContinue";

    /** Return E-Check to Customer **/
    protected static final String RETURN_E_CHECK_TO_CUSTOMER = "ReturnECheckToCustomer";

    /** Place Deposited Check **/
    protected static final String PLACE_DEPOSITED_CHECK = "PlaceDepositedCheck";

    /**
     * Printer offline tag
     */
    public static final String RETRY_CONTINUE_PRINTER_OFFLINE = "RetryContinue.PrinterOffline";

    /**
     * Default Printer offline text
     */
    public static final String DEFAULT_PRINTER_OFFLINE_TEXT = "Printer Offline";

    /** Proceed **/
    protected static final String PROCEED = "Proceed";

    /**
     * Size of the string that will be passed to the slip printer
     */
    protected static int ENDORSEMENT_SIZE = 328;

    /**
     * single check count
     */
    protected static short SINGLE_CHECK = 1;

    /**
     * printingSpec
     */
    public static final String PRINTING_SPEC = "printingSpec";

    /**
     * external order printing label property name
     */
    public static final String EXTERNAL_ORDER_PROMPT_PROP = "ExternalOrder";

    /**
     * external order printing label default value
     */
    public static final String EXTERNAL_ORDER_PROMPT_DEFAULT = "External Order";

    /**
     * mall certificate label property name
     */
    public static final String MALL_CERT_CHK_LABEL_PROP = "MallCertChkLabel";

    /**
     * mall certificate label default value
     */
    public static final String MALL_CERT_CHK_LABEL_DEFAULT = "Mall Gift Cert as Chk Redeemed";

    /**
     * mall certificate label property name
     */
    public static final String MALL_CERT_PO_LABEL_PROP = "MallCertPOLabel";

    /**
     * mall certificate label default value
     */
    public static final String MALL_CERT_PO_LABEL_DEFAULT = "Mall Gift Cert as P.O. Redeemed";

    public static final String NON_TRANS_LABEL_PROP = "NonTransferableLabel";

    /**
     * training mode banner property tag
     */
    protected static final String TRAINING_MODE_BANNER_PROP = "TrainingModeBanner";

    /**
     * banner to print for training mode receipts
     */
    protected static final String TRAINING_MODE_BANNER_DEFAULT = "*** Training Mode ***";

    /**
     * Non-transferable default
     */
    public static final String NON_TRANS_LABEL_DEFAULT = "Non-transferable";

    /**
     * Franking Bpt
     */
    private static final String FRANKING_DOCTYPE = "Franking";

    /**
     * Money Order Document Type
     */
    private static final String MONEYORDER_DOCTYPE = "MoneyOrder";

    /**
     * Purchase Order Document Type
     */
    private static final String PURCHASEORDER_DOCTYPE = "PurchaseOrder";

    /**
     * Default Locale
     */
    public static final Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);

    private static final String EXTERNALORDER_DOCTYPE = "ExternalOrder";
    
    protected static final String FRANKING_TENDER_LIST = "FrankingTenderList";
    
    protected static final String FRANKING_VOIDED_TENDER_LIST = "TendersToFrankOnPostVoid";

    /**
     * If the transaction has tenders to endorse, do it.
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {

        String tillID = new String("");
        boolean sendMail = true;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        Vector tenders = new Vector();
        Vector processedTenders = new Vector();
        Map<String, CurrencyIfc> nontenders = new HashMap<String, CurrencyIfc>(0);
        DialogBeanModel dialogModel = new DialogBeanModel();
        Locale printLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        Locale usrLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String frankTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME, "FrankingMsg",
                "{0} for {1}", usrLocale);

        PrintingCargo cargo = (PrintingCargo) bus.getCargo();

        // Get the tender items to endorse
        tenders = cargo.getTendersToFrank();
        processedTenders = cargo.getProcessedTenders();

        TenderableTransactionIfc trans = cargo.getTenderableTransaction();

        // Load the FrankingReceiptParameterBean using Spring
        FrankingReceiptParameterBeanIfc bean = (FrankingReceiptParameterBeanIfc) BeanLocator
                .getApplicationBean(FrankingReceiptParameterBeanIfc.BEAN_KEY);
        bean.setDocumentType(FRANKING_DOCTYPE);
        bean.setTransaction(trans);

        // A boolean flag to indicate if there is a layaway PARTIAL payment
        // using store credit
        boolean noPartialLywPmtInStoreCredit = true;
        if (tenders != null && tenders.size() > 0)
        {
            Enumeration e = tenders.elements();
            while (e.hasMoreElements())
            {
                Object o = e.nextElement();

                if ((trans.getTransactionType() == TransactionIfc.TYPE_HOUSE_PAYMENT)
                        || (o instanceof TenderStoreCredit && trans.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_COMPLETE))
                {
                    noPartialLywPmtInStoreCredit = false;
                    break;
                }
            }
        }

        // Only call the checkStoreCoupons when not using store credit be used
        // for partial layaway payment.
        if (noPartialLywPmtInStoreCredit)
        {
            String[] frankConfig = null;
            try
            {
                ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

                // get the appropriate franking list
                if (cargo.getTransType() == TransactionIfc.TYPE_VOID)
                {
                    frankConfig = pm.getStringValues(FRANKING_VOIDED_TENDER_LIST);
                }
                else
                {
                    frankConfig = pm.getStringValues(FRANKING_TENDER_LIST);
                }
            }
            catch (ParameterException pe)
            {
                logger.error("" + Util.throwableToString(pe) + "");
            }
            ArrayList<String> frankingSettings = new ArrayList<String>(Arrays.asList(frankConfig));
            if (frankingSettings.contains("StoreCoupon"))
            {
                nontenders = checkStoreCoupons(trans);
            }

        }

        tillID = cargo.getTillID();

        // Make sure the Vector is not empty
        if (tenders != null && tenders.size() > 0)
        {
            Enumeration e = tenders.elements();

            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            String bankAccntNo = cargo.getParameterValue(pm, BANK_ACCOUNT_NUMBER);
            String bankAccntName = cargo.getParameterValue(pm, BANK_ACCOUNT_NAME);
            // String offlineBehavior =
            // (String)cargo.getParameterValue(pm,OFFLINE_BEHAVIOR);

            // Go through the list of tenders and endorse them
            int tenderCount = 0;

            while (e.hasMoreElements())
            {

                Object o = e.nextElement();
                if (processedTenders != null)
                {
                    if (processedTenders.contains(o))
                    {
                        continue;
                    }
                }

                // Build the Header
                buildHeader(trans, tillID, bankAccntNo, bankAccntName, utility, o, null, bean);

                short chkCount = SINGLE_CHECK;
                String chkType = new String("");
                String argText = new String("");
                String chkTypeTag;
                CurrencyIfc chkAmount = null;

                //Process checks and Travel Checks
                if (o instanceof TenderTravelersCheck)
                {
                    TenderTravelersCheck check = (TenderTravelersCheck) o;
                    short pendingChkCount = (short) (cargo.getPendingCheckCount());
                    if (pendingChkCount == 0)
                    {
                        chkCount = check.getNumberChecks();
                    }
                    else
                    {
                        chkCount = (short) (cargo.getPendingCheckCount());
                    }
                    chkTypeTag = check.getTypeDescriptorString();
                    chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                            chkTypeTag, usrLocale);

                    // determine if check amount is in base or alternate
                    // currency
                    chkAmount = ((TenderAlternateCurrencyIfc) check).getAlternateCurrencyTendered();
                    if (chkAmount == null)
                    {
                        chkAmount = check.getAmountTender();
                    }
                }
                else if (o instanceof TenderMoneyOrder)
                {
                    TenderMoneyOrder check = (TenderMoneyOrder) o;
                    chkTypeTag = check.getTypeDescriptorString();
                    chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                            chkTypeTag, usrLocale);
                    chkAmount = check.getAmountTender();
                }
                else if (o instanceof TenderGiftCertificate)
                {
                    TenderGiftCertificate check = (TenderGiftCertificate) o;
                    chkTypeTag = check.getTypeDescriptorString();
                    if (((TenderGiftCertificate) o).getCertificateType().equals(
                            TenderGiftCertificateIfc.MALL_GC_AS_CHECK))
                    {
                        chkType = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                                MALL_CERT_CHK_LABEL_PROP, MALL_CERT_CHK_LABEL_DEFAULT, usrLocale);

                    }
                    else if (((TenderGiftCertificate) o).getCertificateType().equals(
                            TenderGiftCertificateIfc.MALL_GC_AS_PO))
                    {
                        chkType = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                                MALL_CERT_PO_LABEL_PROP, MALL_CERT_PO_LABEL_DEFAULT, usrLocale);
                    }
                    else
                    {
                        chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                                chkTypeTag, usrLocale);
                    }

                    chkAmount = check.getAmountTender();
                }
                // account for Store Credit
                else if (o instanceof TenderStoreCredit)
                {
                    TenderStoreCredit check = (TenderStoreCredit) o;
                    chkTypeTag = check.getTypeDescriptorString();
                    chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                            chkTypeTag, usrLocale);
                    chkAmount = check.getAmountTender();
                }
                else if (o instanceof TenderPurchaseOrder)
                {
                    TenderPurchaseOrder check = (TenderPurchaseOrder) o;
                    chkTypeTag = check.getTypeDescriptorString();
                    chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                            chkTypeTag, usrLocale);
                    chkAmount = check.getAmountTender();
                }
                else if (o instanceof TenderMailBankCheck)
                {
                    TenderMailBankCheck check = (TenderMailBankCheck) o;
                    chkTypeTag = check.getTypeDescriptorString();
                    chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                            chkTypeTag, usrLocale);
                    chkAmount = check.getAmountTender();
                }
                else
                {
                    TenderCheck check = (TenderCheck) o;
                    chkTypeTag = check.getTypeDescriptorString();

                    // determine if check amount is in base or alternate
                    // currency
                    chkAmount = ((TenderAlternateCurrencyIfc) check).getAlternateCurrencyTendered();
                    if (chkAmount == null)
                    {
                        chkAmount = check.getAmountTender();
                    }

                    // retrieve locale to format chk amount to display
                    chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                            chkTypeTag, usrLocale);

                    if (trans.getTransactionType() == TransactionIfc.TYPE_VOID)
                    {
                        bean.setTenderCheck(false);
                        bean.setVoidTenderCheck(true);
                        buildIDTypeAndState(check, printLocale, utility, bean);
                    }
                    else if (check.getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_CHECK)
                    {
                        // retrieve locale to format chk amount to frank
                        String chkAmntPrinted = checkNull(chkAmount.toFormattedString());
                        bean.setTenderCheck(true);
                        // gather additional endorsement information
                        processChecks(check, printLocale, chkAmntPrinted, utility, cargo.getTransaction()
                                .isReentryMode(), bean);

                    }
                    else
                    {
                        // E-check
                        bean.setECheck(true);
                        bean.setDocumentType(ReceiptTypeConstantsIfc.ECHECK_DOCTYPE);
                    }
                }

                if (trans.isTrainingMode())
                {
                    bean.setTrainingMode(true);
                }

                String chkAmntDisplayed = checkNull(chkAmount.toFormattedString());
                Object[] parms = { chkType, chkAmntDisplayed };
                argText = LocaleUtilities.formatComplexMessage(frankTxt, parms, usrLocale);

                // print endorsement
                cargo.setCurrentTender(tenderCount);
                sendMail = endorseDocument(bus, argText,o, chkCount, ui, dialogModel, cargo,
                        bean);

                // An error was detected exit loop
                if (!sendMail)
                {
                    break;
                }
                tenderCount++;
            } // while
        } // if
          // account for non-tender items
        if (nontenders != null && nontenders.size() > 0)
        {
            ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
            String bankAccntNo = cargo.getParameterValue(pm, BANK_ACCOUNT_NUMBER);
            String bankAccntName = cargo.getParameterValue(pm, BANK_ACCOUNT_NAME);

            Set<String> nontendersHashSet = new HashSet<String>(nontenders.keySet());
            Iterator<String> iter = nontendersHashSet.iterator();
            int tenderCount = 0;
            while (iter.hasNext())
            {
                String o = iter.next();

                if (processedTenders != null)
                {
                    if (processedTenders.contains(o))
                    {
                        continue;
                    }
                }

                CurrencyIfc redeemAmount = nontenders.get(o);
                
                buildHeader(trans, tillID, bankAccntNo, bankAccntName, utility, o, redeemAmount, bean);

                short chkCount = SINGLE_CHECK;
                String chkType = new String("");
                String argText = new String("");
                String chkTypeTag = new String("StoreCoupon");
                chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag, chkTypeTag,
                        usrLocale);
                argText = chkType;

                if (trans.isTrainingMode())
                {
                    bean.setTrainingMode(true);
                }

                // cargo.setCurrentTender(tenderCount);
                if (!(trans.getTransactionType() == TransactionConstantsIfc.TYPE_RETURN && trans
                        .getTenderLineItemsVector().get(tenderCount++) instanceof TenderStoreCredit))
                {
                    sendMail = endorseNonTenderDocument(nontenders, bus, argText, o, chkCount, ui, dialogModel, cargo,
                            bean);
                }

            }
        } // if

        // If no errors franking so far..
        if (sendMail)
        {
            sendMail = frankIssuedGiftCertificates(bus, dialogModel, bean);
        }

        // external order
        if (trans instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc saleTrans = (SaleReturnTransactionIfc)trans;
            if (saleTrans.hasExternalOrder() && saleTrans.getExternalOrderType() != ExternalOrderConstantsIfc.TYPE_ATG)
            {
                String orderNumber = checkNull(((SaleReturnTransactionIfc)trans).getExternalOrderNumber());
                bean.setOrderNumber(orderNumber);
                bean.setDocumentType(EXTERNALORDER_DOCTYPE);
                
                String argText = utility.retrieveText(POSUIManagerIfc.PROMPT_AND_RESPONSE_SPEC,
                        BundleConstantsIfc.PRINTING_BUNDLE_NAME, EXTERNAL_ORDER_PROMPT_PROP, EXTERNAL_ORDER_PROMPT_DEFAULT,
                        usrLocale);

                if (trans.isTrainingMode())
                {
                    bean.setTrainingMode(true);
                }

                sendMail = endorseNonTenderDocument(new HashMap(0), bus, argText, "", EndorseTendersAisle.SINGLE_CHECK, ui,
                        dialogModel, cargo, bean);
            }
        }

        if (sendMail)
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        else
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
    }

    /**
     * Frank the issued gift certificates.
     * 
     * @param bus Bus containing cargo information
     * @param dialogModel DialogModel to set with error messages if there is a
     *            problem franking.
     * @return whether or not gift certs were successfully franked. If there were
     *         no gift certificates to frank, it will return true.
     */
    public boolean frankIssuedGiftCertificates(BusIfc bus, DialogBeanModel dialogModel,
            FrankingReceiptParameterBeanIfc bean)
    {
        PrintingCargo cargo = (PrintingCargo) bus.getCargo();
        boolean sendMail = true;
        Locale printLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        Locale usrLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String frankTxt = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME, "FrankingMsg",
                "{0} for {1}", usrLocale);
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        String bankAccntName = cargo.getParameterValue(pm, BANK_ACCOUNT_NAME);

        ArrayList certs = cargo.getGiftCertificatesForFranking();
        SaleReturnLineItemIfc sli = null;
        // Get the index of the next index to print.
        for (int i = 0; i < certs.size(); i++)
        {
            sli = (SaleReturnLineItemIfc) certs.get(i);
            GiftCertificateItemIfc giftCertificate = (GiftCertificateItemIfc) sli.getPLUItem();
            String chkTypeTag = giftCertificate.getDescription(usrLocale);
            String argText = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                    chkTypeTag, usrLocale);

            // retrieve locale to format chk amount to display
            String chkAmntDisplayed = checkNull(giftCertificate.getPrice().toFormattedString());
            String chkType = utility.retrieveText("Common", BundleConstantsIfc.COMMON_BUNDLE_NAME, chkTypeTag,
                    chkTypeTag, usrLocale);
            Object[] parms = { chkType, chkAmntDisplayed };
            argText = LocaleUtilities.formatComplexMessage(frankTxt, parms, usrLocale);

            buildHeader(cargo.getTenderableTransaction(), cargo.getTillID(), "", bankAccntName, utility,
                    giftCertificate, null, bean);

            // check for discount applied
            if (sli.getItemPrice().getItemDiscounts() != null && sli.getItemPrice().getItemDiscounts().length > 0)
            {
                /*
                 * endorsement.append("  "); String discountAppliedText =
                 * utility.retrieveCommonText("DiscountApplied",
                 * "****Discount Applied****", printLocale);
                 * endorsement.append(discountAppliedText).append("\n");
                 */
            }

            if (cargo.getTenderableTransaction().isTrainingMode())
            {
                bean.setTrainingMode(true);
            }

            cargo.setCurrentNonTenderDocument(i);

            sendMail = endorseNonTenderDocument(new HashMap(0), bus, argText, giftCertificate,
                    EndorseTendersAisle.SINGLE_CHECK, ui, dialogModel, cargo, bean);

            if (sendMail)
            {
                cargo.setGiftCertificateFranked(sli);
            }
            else
            {
                break;
            }
        }// for loop
        return sendMail;
    }

    /**
     * Starts automatic endorsement process.
     * 
     * @param bus BusIfc
     * @param argText String
     * @param text String
     * @param item Object
     * @param chkCount short
     * @param ui
     * @param dialogModel
     * @param cargo
     * @return completedTask boolean
     */
    protected boolean endorseDocument(BusIfc bus, String argText, Object item, short chkCount,
            POSUIManagerIfc ui, DialogBeanModel dialogModel, PrintingCargo cargo, FrankingReceiptParameterBeanIfc beanWithAllTenders)
    {
        boolean completedTask = false;
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        StatusBeanModel statusModel = new StatusBeanModel();

        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
                .getManager(PrintableDocumentManagerIfc.TYPE);
        
        FrankingReceiptParameterBeanIfc bean = beanWithThisTender(cargo.getTenderableTransaction(), item, beanWithAllTenders);

        try
        {

            parModel.setArguments(argText);
            baseModel.setPromptAndResponseModel(parModel);

            for (int i = chkCount; i > 0; i--)
            {
                ui.showScreen(POSUIManagerIfc.INSERT_TENDER, baseModel);
                pda.beginSlipInsertion();
                pda.endSlipInsertion();
                String printText = printManager.getPreview((SessionBusIfc) bus, bean);
                pda.printNormal(POSPrinterConst.PTR_S_SLIP, printText);
                ui.showScreen(POSUIManagerIfc.REMOVE_TENDER, baseModel);
                pda.beginSlipRemoval();
                pda.endSlipRemoval();
                cargo.setPendingCheckCount(chkCount - 1);
                // Update printer status
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
                baseModel.setStatusBeanModel(statusModel);
            }
            cargo.setPendingCheckCount(0);
            // remove item after succesfully franked
            cargo.addProcessedTender((TenderLineItemIfc) item);
            completedTask = true;

            // if a check and not a void transaction
            if (item instanceof TenderCheck
                    && ((PrintingCargo) bus.getCargo()).getTenderableTransaction().getTransactionType() != TransactionIfc.TYPE_VOID)
            {
                handleCheckAfterFrank((TenderCheck) item, dialogModel);
                completedTask = false;
            }
        }
        catch (DeviceException e)
        {
            handlePrintingErrors(e, argText, pda, bus, dialogModel);
        }
        catch (PrintableDocumentException e)
        {
            handlePrintingErrors(argText,pda, bus, dialogModel);
        }

        return (completedTask);
    }

    
    /**
     * Displays printing errors dialogs
     * 
     * @param exception PrintableDocumentException the exception being raised
     * @param argText String
     * @param pda POSDeviceActions
     * @param bus BusIfc reference
     * @param dialogModel
     */
    protected void handlePrintingErrors(String argText, POSDeviceActions pda, BusIfc bus,
            DialogBeanModel dialogModel)
    {
        dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
        String arg[] = new String[1];
        dialogModel.setResourceID(PRINT_ERROR);
        arg[0] = argText;
        dialogModel.setArgs(arg);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Print");
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Discard");
    }

    void handleCheckAfterFrank(TenderCheck check, DialogBeanModel dialogModel)

    {
        dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        if (check.getTypeCode() != TenderLineItemConstantsIfc.TENDER_TYPE_CHECK)
        {
            dialogModel.setResourceID(RETURN_E_CHECK_TO_CUSTOMER);
        }
        else
        {
            dialogModel.setResourceID(PLACE_DEPOSITED_CHECK);
        }
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CONTINUE);
    }

    /**
     * Displays printing errors dialogs
     * 
     * @param exception DeviceException the exception being raised
     * @param argText String
     * @param pda POSDeviceActions
     * @param bus BusIfc reference
     * @param dialogModel
     */
    protected void handlePrintingErrors(DeviceException exception, String argText, POSDeviceActions pda, BusIfc bus,
            DialogBeanModel dialogModel)
    {
        Throwable oe = exception.getCause();

        JposException jpe = null;

        StatusBeanModel statusModel = new StatusBeanModel();

        if (oe != null && oe instanceof JposException)
        {
            jpe = (JposException) oe;
        }

        if (jpe != null)
        {
            logger.warn("Exception occurred during franking:\n" + "message = " + jpe.getMessage() + "\n"
                    + "error code = " + jpe.getErrorCode() + "\n" + "extended error code = "
                    + jpe.getErrorCodeExtended());
        }

        // Assuming timeouts are only on insertion, not removal
        if ((jpe != null) && (jpe.getErrorCode() == JposConst.JPOS_E_TIMEOUT))
        {

            // display dialog
            dialogModel.setType(DialogScreensIfc.CONFIRMATION);
            String arg[] = new String[1];
            dialogModel.setResourceID(PRINTER_TIMEOUT);
            arg[0] = argText;
            dialogModel.setArgs(arg);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_YES, "Print");
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_NO, "Discard");
        }
        // while franking, the slip printer ran out of paper
        // not much can be done in this situation
        // display generic error message (FA recommendation)
        else if (jpe != null && jpe.getErrorCode() == JposConst.JPOS_E_EXTENDED
                && jpe.getErrorCodeExtended() != POSPrinterConst.JPOS_EPTR_SLP_EMPTY)
        {
            dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
            String arg[] = new String[1];
            dialogModel.setResourceID(PRINT_ERROR);
            arg[0] = argText;
            dialogModel.setArgs(arg);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Print");
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Discard");
        }
        else
        {
            // Update printer status
            statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);
            dialogModel.setStatusBeanModel(statusModel);
            if (oe != null)
            {
                logger.warn("DeviceException.NestedException:\n " + Util.throwableToString(oe) + "");
            }

            // Are we Offline?
            if (jpe != null
                    && ((jpe.getErrorCode() == JposConst.JPOS_E_EXTENDED) || (jpe.getErrorCode() == JposConst.JPOS_E_OFFLINE)))
            {

                // Determine what the next step should be
                dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);

                String arg[] = new String[1];
                dialogModel.setResourceID(OFFLINE_DEVICE_RETRY_CONTINUE);

                UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                arg[0] = utility.retrieveDialogText(RETRY_CONTINUE_PRINTER_OFFLINE, DEFAULT_PRINTER_OFFLINE_TEXT);

                dialogModel.setArgs(arg);
                // The Continue flow
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Discard");
                // The Retry flow
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Print");

            }
            else
            // no timeout, not offline
            {
                // Try to reset the printer
                try
                {
                    pda.beginSlipRemoval();
                    pda.endSlipRemoval();
                }
                catch (DeviceException tmp)
                {
                    // Do nothing... we're already in recovery mode.
                }

                dialogModel.setType(DialogScreensIfc.RETRY_CONTINUE);
                String arg[] = new String[1];
                dialogModel.setResourceID(PRINT_ERROR);
                arg[0] = argText;
                dialogModel.setArgs(arg);
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_RETRY, "Print");
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_CONTINUE, "Discard");
            }
        } // else
          // Make sure we don't try to frank the same thing again
    }

    /**
     * Builds the additional header for check franking
     * 
     * @param check TenderCheck the current check
     * @param locale
     * @param chkAmnt String the check amount
     * @param utility
     * @param FrankingReceiptParameterBeanIfc
     */
    protected void processChecks(TenderCheck check, Locale locale, String chkAmnt, UtilityManagerIfc utility,
            boolean reentryMode, FrankingReceiptParameterBeanIfc bean)
    {

        String authCode = checkNull(check.getAuthorizationCode());
        String authMethod = checkNull(check.getAuthorizationMethod());
        bean.setCheckAuthCode(authCode);
        bean.setCheckAuthMethod(authMethod);
        buildIDTypeAndState(check, locale, utility, bean);

    }

    /**
     * Creates the state and id type lines.
     * 
     * @param check
     * @param locale
     * @param utility
     * @param FrankingReceiptParameterBeanIfc
     */
    protected void buildIDTypeAndState(TenderCheck check, Locale locale, UtilityManagerIfc utility,
            FrankingReceiptParameterBeanIfc bean)
    {

        String idType = checkNull(check.getPersonalIDType().getText(locale));
        // If id type is null dont print idtype info
        if (idType.length() > 0)
        {
            bean.setIdType(idType);

        }

        String idState = checkNull(check.getIDIssuer());
        if (idState.length() > 0)
        {
            bean.setIdState(idState);
        }

    }

    /**
     * Builds the standard header for franking
     * 
     * @param trans TenderableTransactionIfc the current transaction
     * @param tillID String
     * @param account String
     * @param bank String
     * @param utility
     * @param object
     * @param curr
     * @return StringBuilder header
     */
    protected void buildHeader(TenderableTransactionIfc trans, String tillID, String account, String bank,
            UtilityManagerIfc utility, Object object, CurrencyIfc curr, FrankingReceiptParameterBeanIfc bean)
    {
        String redeemAmountTxt = null;

        if (trans.getTransactionTotals().getAmountTender() != null)
        {
            redeemAmountTxt = trans.getTransactionTotals().getAmountTender().toFormattedString();
        }
        if (curr != null)
        {
            redeemAmountTxt = curr.toFormattedString();
        }

        // print blank lines for Issue Store Credit
        if (object instanceof TenderStoreCredit)
        {
            TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) object;
            bean.setTenderStoreCredit(tsc);
            bean.setTenderStoreCredit(true);
        }

        if (object instanceof TenderCheck)
        {
            // only display if not a void transaction
            if (trans.getTransactionType() != TransactionIfc.TYPE_VOID
                    && ((TenderCheck) object).getTypeCode() == TenderLineItemConstantsIfc.TENDER_TYPE_CHECK)
            {
                bean.setTenderCheck((TenderCheckIfc) object);
                bean.setBankAccntName(bank);
                bean.setBankAccntNum(account);

            }
        }
        else if (object instanceof TenderMoneyOrder)
        {

            bean.setDocumentType(MONEYORDER_DOCTYPE);
            bean.setTenderMoneyOrder((TenderMoneyOrder) object);
            bean.setBankAccntName(bank);
            bean.setBankAccntNum(account);

            // MISSING FROM FSD
            if (trans.getTransactionType() == TransactionIfc.TYPE_VOID)
            {

            }

        }
        else if (object instanceof TenderTravelersCheck)
        {
            bean.setTenderTravelersCheck((TenderTravelersCheck) object);
            bean.setBankAccntName(bank);
            bean.setBankAccntNum(account);
            bean.setTenderTravelersCheck(true);
        }
        else if (object instanceof TenderPurchaseOrder)
        {
            bean.setDocumentType(PURCHASEORDER_DOCTYPE);
            bean.setTenderPurchaseOrder((TenderPurchaseOrder) object);
        }
        else if (object instanceof TenderGiftCertificate || object instanceof GiftCertificateItemIfc)
        {
            if (object instanceof GiftCertificateItemIfc)
            {
                redeemAmountTxt = ((GiftCertificateItemIfc) object).getPrice().toFormattedString();
            }
            else
            // removed all checks here. we need to get the amount from the
            // tender, otherwise we use the total tender amount regardless of
            // over or under tender
            {
                redeemAmountTxt = ((TenderLineItemIfc) object).getAmountTender().abs().toFormattedString();
            }

            if (trans.getTransactionType() == TransactionIfc.TYPE_VOID)
            {
                // buildVoidHeader(header, utility);
            }

            if (object instanceof GiftCertificateItemIfc)
            {
                bean.setGiftCertificateItemFlag(true);
                bean.setGiftCertificateItem((GiftCertificateItemIfc) object);
            }
            else if (object instanceof TenderGiftCertificate)
            {
                bean.setTenderGiftCertificate(true);
                bean.setTenderGiftCertificate((TenderGiftCertificateIfc) object);
            }

            if (object instanceof TenderGiftCertificate
                    && (((TenderGiftCertificate) object).getCertificateType()
                            .equals(TenderGiftCertificateIfc.MALL_GC_AS_CHECK)))
            {

                bean.setBankAccntName(bank);
                bean.setBankAccntNum(account);
                bean.setMallCertCheck(true);
                bean.setTenderGiftCertificate(false);
            }

            if (object instanceof TenderGiftCertificate
                    && ((TenderGiftCertificate) object).getCertificateType().equals(
                            TenderGiftCertificateIfc.MALL_GC_AS_PO))
            {
                bean.setMallCertPo(true);
                bean.setTenderGiftCertificate(false);
            }

        }

        else if (object instanceof TenderStoreCredit)
        {
            if (((TenderLineItemIfc) object).getAmountTender().abs()
                    .compareTo(trans.getTransactionTotals().getAmountTender().abs()) < 0)
            {
                redeemAmountTxt = ((TenderLineItemIfc) object).getAmountTender().toFormattedString();
            }

            TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) object;
            StoreCreditIfc sc = tsc.getStoreCredit();
            Locale printLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            String idType = tsc.getPersonalIDType(printLocale);
            bean.setIdType(idType);

            if (trans.getTransactionType() == TransactionConstantsIfc.TYPE_VOID)
            {

                // CODED THE RECIPET UNABLE TO TEST IT COZ OF STORE CREDIT IS
                // NOT PERSISTING
                // buildVoidHeader(header, utility);
                // buildIssueStoreCreditPostVoidHeader(header, object, utility);
            }
            else
            {
                if (StoreCreditIfc.ISSUED.equalsIgnoreCase(sc.getStatus()))
                {
                    // buildIssueStoreCreditHeader(header, object, utility);
                }
                else
                {
                    int transType = trans.getTransactionType();
                    // buildRedeemStoreCreditHeader(header, object, utility,
                    // transType);
                }
            }
        }
        /*
         * Mail Bank Checks (MBC) MBCs are not franked. The MBC franking was
         * added for GAP to frank a form the user fills out when an MBC is
         * issued for refunds. This capability is customer specific. However,
         * since it is controlled by a parameter, it was decided to keep it in
         * base product. (Printing->Franking Tender List->Mail Bank Check) Note
         * that the "Printing->Tenders To Frank on Post Void" parameter list
         * does not include MBCs.
         */
        else if (object instanceof TenderMailBankCheck)
        {
            bean.setTenderMailBankCheck((TenderMailBankCheck) object);
            bean.setTenderMailBankCheck(true);
        }

        else if (object instanceof TenderCoupon)
        { // handle StoreCoupon instances
            bean.setTenderCoupon(true);
            bean.setTenderCoupon((TenderCoupon) object);
        }
       
    }

    /**
     * This method returns the same String it was passed if that string is non-
     * null or an empty string.
     * 
     * @param s String to check
     * @return String safe non-null string
     */
    protected String checkNull(String s)
    {
        if (s == null)
        {
            s = new String("");
        }

        return s;
    }

    /**
     * Builds the header for Issue Store Credit
     * 
     * @param header StringBuilder
     * @param object Object
     * @param utility UtilityManagerIfc
     * @return StringBuilder header
     */
    protected StringBuilder buildIssueStoreCreditHeader(StringBuilder header, Object object, UtilityManagerIfc utility)
    {
        header.append("\n");
        Locale printLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
        TenderStoreCreditIfc tsc = null;
        if (object instanceof TenderStoreCreditIfc)
        {
            tsc = (TenderStoreCreditIfc) object;
        }

        // buildStoreCreditCustName(header, utility, tsc);

        // buildStoreCreditIDType(header, utility, tsc);

        // store credit number
        /**
         * String merchCert = utility.retrieveText(PRINTING_SPEC,
         * BundleConstantsIfc.PRINTING_BUNDLE_NAME, MERCH_CERT_LABEL_PROP,
         * MERCH_CERT_LABEL_DEFAULT); header.append(merchCert);
         **/

        StoreCreditIfc sc = null;// buildStoreCreditID(header, utility, tsc);

        // buildStoreCreditAmountIssuedInfo(header, utility, sc);

        // non transferable
        String nonTrans = utility.retrieveText(PRINTING_SPEC, BundleConstantsIfc.PRINTING_BUNDLE_NAME,
                NON_TRANS_LABEL_PROP, NON_TRANS_LABEL_DEFAULT, printLocale);
        header.append(nonTrans);
        header.append("\n");
        return header;
    }

    /**
     * Testing for existence of store coupons.
     * 
     * @param transaction
     * @return Map map containing store coupons applied to the transaction.
     */
    protected Map<String, CurrencyIfc> checkStoreCoupons(TenderableTransactionIfc transaction)
    {
        // TenderableTransactionIfc transaction =
        // cargo.getTenderableTransaction();
        // set up the enumeration
        DiscountRuleIfc d = null;
        int trigger = -1;
        int x = 0; // count of store coupon discount applications
        // collection for store coupon applications
        Map<String, CurrencyIfc> map = new HashMap<String, CurrencyIfc>(0);
        boolean testKey = false;

        if (transaction instanceof RetailTransactionIfc
                && (!(transaction instanceof LayawayTransactionIfc) || !(((LayawayTransactionIfc) transaction)
                        .getLayaway().getStatus() == LayawayConstantsIfc.STATUS_DELETED)))
        {
            if (((RetailTransactionIfc) transaction).getLineItemsVector() != null)
            {
                for (Object element : ((RetailTransactionIfc) transaction).getLineItemsVector())
                {
                    SaleReturnLineItemIfc hold = (SaleReturnLineItemIfc) element;

                    ItemPriceIfc ip = hold.getItemPrice();

                    ItemDiscountStrategyIfc[] discounts = ip.getItemDiscounts();

                    for (int i = 0; i < discounts.length; i++)
                    {
                        d = discounts[i];

                        trigger = d.getAssignmentBasis();

                        switch (trigger) {
                        case DiscountRuleConstantsIfc.ASSIGNMENT_STORE_COUPON:
                            x++;
                            // construct a cotainer with elements which satisfy
                            // this condition
                            testKey = map.containsKey(d.getReferenceID());
                            if (testKey)
                            {
                                // Negative currency amounts come from a return
                                // and returned coupons
                                // are not franked; only increment the sale
                                // coupon amounts and update the map.
                                CurrencyIfc curr1 = (CurrencyIfc) map.get(d.getReferenceID());
                                if (curr1.signum() > 0)
                                {
                                    CurrencyIfc curr2 = curr1.add(d.getDiscountAmount());
                                    map.put(d.getReferenceID(), curr2);
                                }
                            }
                            else
                            {
                                // Negative currency amounts come from a return
                                // and returned coupons
                                // are not franked; only add the sale coupon
                                // amounts to the map.
                                if (d.getDiscountAmount().signum() > 0)
                                {
                                    map.put(d.getReferenceID(), d.getDiscountAmount());
                                }
                            }
                            break;

                        default:
                            break;
                        }
                    }
                }
            }
        }
        map.size();
        map.keySet();

        return map;
    }

    /**
     * Starts automatic endorsement process.
     * 
     * @param nontenders Map
     * @param bus BusIfc
     * @param argText String
     * @param text String
     * @param item Object
     * @param chkCount short
     * @param ui POSUIManagerIfc
     * @param dialogModel DialogBeanModel
     * @param cargo PrintingCargo
     * @return completedTask boolean
     */
    protected boolean endorseNonTenderDocument(Map nontenders, BusIfc bus, String argText, Object item, short chkCount,
            POSUIManagerIfc ui, DialogBeanModel dialogModel, PrintingCargo cargo, FrankingReceiptParameterBeanIfc beanWithAllTenders)
    {
        boolean completedTask = false;
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        StatusBeanModel statusModel = new StatusBeanModel();

        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        PrintableDocumentManagerIfc printManager = (PrintableDocumentManagerIfc) bus
                .getManager(PrintableDocumentManagerIfc.TYPE);
        
        FrankingReceiptParameterBeanIfc bean = beanWithThisTender(cargo.getTenderableTransaction(), item, beanWithAllTenders);

        try
        {

            parModel.setArguments(argText);
            baseModel.setPromptAndResponseModel(parModel);

            int pendingDocs = 1;

            for (int i = 0; i < pendingDocs; i++)
            {
                // Add this item to the list so we can remove it from the
                // original
                // list if there's a failure.

                ui.showScreen(POSUIManagerIfc.INSERT_TENDER, baseModel);
                pda.beginSlipInsertion();

                pda.endSlipInsertion();

                String printText = printManager.getPreview((SessionBusIfc) bus, bean);
                pda.printNormal(POSPrinterConst.PTR_S_SLIP, printText);
                ui.showScreen(POSUIManagerIfc.REMOVE_TENDER, baseModel);
                pda.beginSlipRemoval();
                pda.endSlipRemoval();

                // Update printer status
                statusModel.setStatus(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
                baseModel.setStatusBeanModel(statusModel);

            }
            completedTask = true;

        }
        catch (DeviceException e)
        {
            handlePrintingErrors(e, argText, pda, bus, dialogModel);
        }
        catch (PrintableDocumentException e)
        {
            handlePrintingErrors(argText, pda, bus, dialogModel);
        }
        return (completedTask);
    }

    /**
     * Get FrankingReceiptParameterBean which only contains the current tender.
     * @param transaction
     * @param tenderItem current tender
     * @param beanWithAllTenders
     * @return FrankingReceiptParameterBean object which excludes the other tender information
     */
    protected FrankingReceiptParameterBeanIfc beanWithThisTender(TenderableTransactionIfc transaction, Object tenderItem, FrankingReceiptParameterBeanIfc beanWithAllTenders)
    {
        // Load the FrankingReceiptParameterBean 
        FrankingReceiptParameterBeanIfc bean = (FrankingReceiptParameterBeanIfc) BeanLocator
                .getApplicationBean(FrankingReceiptParameterBeanIfc.BEAN_KEY);
        bean.setDocumentType(FRANKING_DOCTYPE);
        bean.setTransaction(transaction);
        
        // Set tender information for this particular tenderItem, exlucde the other tender in case of split tenders
        if (tenderItem instanceof TenderStoreCredit)
        {
            bean.setTenderStoreCredit(beanWithAllTenders.getTenderStoreCredit());
            bean.setTenderStoreCredit(true);
        }
        else if (tenderItem instanceof TenderCheck)
        {
                bean.setTenderCheck(beanWithAllTenders.getTenderCheck());
                bean.setBankAccntName(beanWithAllTenders.getBankAccntName());
                bean.setBankAccntNum(beanWithAllTenders.getBankAccntNum());
                bean.setDocumentType(beanWithAllTenders.getDocumentType());
                bean.setECheck(beanWithAllTenders.isECheck());
                bean.setTenderCheck(beanWithAllTenders.isTenderCheck());
        }
        else if (tenderItem instanceof TenderMoneyOrder)
        {
            bean.setDocumentType(MONEYORDER_DOCTYPE);
            bean.setTenderMoneyOrder(beanWithAllTenders.getTenderMoneyOrder());
            bean.setBankAccntName(beanWithAllTenders.getBankAccntName());
            bean.setBankAccntNum(beanWithAllTenders.getBankAccntNum());
        }
        else if (tenderItem instanceof TenderTravelersCheck)
        {
            bean.setTenderTravelersCheck(beanWithAllTenders.getTenderTravelersCheck());
            bean.setBankAccntName(beanWithAllTenders.getBankAccntName());
            bean.setBankAccntNum(beanWithAllTenders.getBankAccntNum());
            bean.setTenderTravelersCheck(true);
        }
        else if (tenderItem instanceof TenderPurchaseOrder)
        {
            bean.setDocumentType(PURCHASEORDER_DOCTYPE);
            bean.setTenderPurchaseOrder(beanWithAllTenders.getTenderPurchaseOrder());
        }
        else if (tenderItem instanceof GiftCertificateItemIfc)
        {
            bean.setGiftCertificateItemFlag(true);
            bean.setGiftCertificateItem(beanWithAllTenders.getGiftCertificateItem());
        }
        else if (tenderItem instanceof TenderStoreCredit)
        {
            TenderStoreCreditIfc tsc = (TenderStoreCreditIfc) tenderItem;
            StoreCreditIfc sc = tsc.getStoreCredit();
            Locale printLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            String idType = tsc.getPersonalIDType(printLocale);
            bean.setIdType(idType);
        }
        else if (tenderItem instanceof TenderMailBankCheck)
        {
            bean.setTenderMailBankCheck(beanWithAllTenders.getTenderMailBankCheck());
            bean.setTenderMailBankCheck(true);
        }
        else if (tenderItem instanceof TenderCoupon)
        { 
            bean.setTenderCoupon(true);
            bean.setTenderCoupon(beanWithAllTenders.getTenderCoupon());
        }
        else if (tenderItem instanceof TenderGiftCertificate)
        {
            bean.setTenderGiftCertificate(true);
            bean.setTenderGiftCertificate(beanWithAllTenders.getTenderGiftCertificate());
            String certificateType = ((TenderGiftCertificate) tenderItem).getCertificateType();
            if (certificateType.equals(TenderGiftCertificateIfc.MALL_GC_AS_CHECK))
            {
                bean.setBankAccntName(beanWithAllTenders.getBankAccntName());
                bean.setBankAccntNum(beanWithAllTenders.getBankAccntNum());
                bean.setMallCertCheck(true);
                bean.setTenderGiftCertificate(false);
            }
            else if (certificateType.equals(TenderGiftCertificateIfc.MALL_GC_AS_PO))
            {
                bean.setMallCertPo(true);
                bean.setTenderGiftCertificate(false);
            }
        }               
        return bean;
    }
    /**
     * Gets the device's character widths map from the device action group.
     * 
     * @see oracle.retail.stores.pos.device.POSDeviceActionGroupIfc#getCharWidths
     * @return slip printer line size
     */
    public static Map<String, Integer> getCharWidths()
    {
        Map<String, Integer> charWidths = new HashMap<String, Integer>(0);

        try
        {
            POSPrinterActionGroupIfc dag;
            DeviceTechnicianIfc deviceTechnician = (DeviceTechnicianIfc) Gateway.getDispatcher().getLocalTechnician(
                    DeviceTechnicianIfc.TYPE);
            dag = (POSPrinterActionGroupIfc) deviceTechnician.getDeviceActionGroup(POSPrinterActionGroupIfc.TYPE);
            charWidths = dag.getCharWidths();
        }
        catch (TechnicianNotFoundException tnfe)
        {
            logger.error(tnfe);
        }
        catch (DeviceException de)
        {
            logger.error(de);
        }
        return charWidths;
    }
}
