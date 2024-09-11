/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/transaction/SaleReturnTransaction.java /main/66 2014/07/09 16:20:03 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       07/08/14 - add new function to getTotablableLineItems
 *    vineesin  07/03/14 - Do not include cancelled price line item
 *    jswan     06/20/14 - Modified to get the recommended items for all sale
 *                         return line items in the transaction.
 *    yiqzhao   05/14/14 - Add clone externalOrderType
 *    yiqzhao   05/09/14 - Add method isOrderPickupOrCancel.
 *    yiqzhao   05/07/14 - add isXChannelPickupOrCancel method.
 *    rabhawsa  02/27/14 - calling method updateNumberOfItemsSold after adding
 *                         line items
 *    cgreene   01/28/14 - Add support for saving type of external order
 *    cgreene   01/13/14 - added isWebmanagedOrder
 *    cgreene   01/03/14 - external order id javadoc
 *    tksharma  12/18/13 - called updateTransactionTotals() in
 *                         setCloneAttributes
 *    abondala  09/05/13 - deprecate some of the API related to tax that is not
 *                         referred as that is causing outOfMemory issues.
 *    abondala  09/04/13 - initialize collections
 *    jswan     05/07/13 - Modified to support sending voided order returns to
 *                         the cross channel order repository.
 *    abondala  05/07/13 - for price adjustments, prorated tax calculator
 *                         should not be called.
 *    rgour     04/01/13 - CBR cleanup
 *    sgu       02/19/13 - save order status for a suspended order transaction
 *    jswan     02/20/13 - Modified for Currency Rounding.
 *    jswan     02/13/13 - Fixed warning created by the move and deprecation of
 *                         the Util class.
 *    sgu       12/12/12 - move xchannel functions to sale return transaction
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    tzgarba   08/27/12 - Merge project Echo (MPOS) into trunk.
 *    blarsen   06/29/12 - Added unappliedStoreCoupons(). Used by MPOS to
 *                         provide more info to operator.
 *    yiqzhao   04/30/12 - move getShippingChargeLineItem to
 *                         SaleReturnLineItemIfc
 *    yiqzhao   04/26/12 - handle shipping charge as sale return line item
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    sgu       08/23/11 - check nullpointer for approval status
 *    sgu       05/16/11 - move instant credit approval status to its own class
 *    cgreene   03/18/11 - XbranchMerge cgreene_124_receipt_quick_wins from
 *                         main
 *    cgreene   03/16/11 - implement You Saved feature on reciept and
 *                         AllowMultipleQuantity parameter
 *    ohorne    08/12/10 - transactions containing an External Order always
 *                         have customer physicallyPresent
 *    aariyer   07/30/10 - For the OCC Screen display
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    acadar    06/08/10 - changes for signature capture, disable txn send, and
 *                         discounts
 *    acadar    06/07/10 - changes for signature capture
 *    acadar    06/03/10 - refresh to tip
 *    acadar    06/02/10 - signature capture changes
 *    sgu       06/01/10 - check in after merge
 *    sgu       06/01/10 - check in order sell item flow
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       05/20/10 - fix the equal method
 *    sgu       05/19/10 - minor fixes for external order APIs
 *    sgu       05/19/10 - add APIs to store external order info in
 *                         SaleReturnTransaction class
 *    sgu       05/18/10 - set external send flag on shipping packages
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/27/10 - updating deprecated names
 *    jswan     03/24/10 - Fix an issue with instant credit enrollment discount
 *                         with special orders.
 *    abondala  01/03/10 - update header date
 *    npoola    12/17/09 - formatted the java doc for the hasServiceItems()
 *                         emthod
 *    npoola    12/17/09 - added the new method hasServiceItems() to check the
 *                         transaction has any service items
 *    cgreene   04/09/09 - removed rebate item references
 *    cgreene   03/20/09 - keep kit components off receipts by implementing new
 *                         method getLineItemsExceptExclusions
 *    vikini    03/09/09 - Return Item with Trans Disc shows up as Item Disc in
 *                         POS report
 *    cgreene   01/08/09 - removed reference to getSendPackageVector()
 *    deghosh   12/24/08 - EJ I18n changes
 *    deghosh   12/23/08 - EJ i18n changes
 *    vikini    12/18/08 - Printing Return Ticket ID on Receipt
 *    vikini    12/17/08 - Clone method edited to also set the Rebate Item
 *                         Object
 *    vchengeg  12/16/08 - ej defect fixes
 *    deghosh   12/08/08 - EJ i18n changes
 *    arathore  11/20/08 - updated for ereceipt.
 *    cgreene   11/18/08 - added method isReturn
 *    ranojha   11/04/08 - Code refreshed to tip
 *    ranojha   11/04/08 - Changes for Tax Exempt reason codes
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    akandru   10/31/08 - EJ Changes_I18n
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *
 * ===========================================================================
 * $Log:
 *    18   360Commerce 1.17        5/21/2007 9:17:04 AM   Anda D. Cadar   Ej
 *         changes and cleanup
 *    17   360Commerce 1.16        5/16/2007 7:56:04 PM   Brett J. Larsen
 *         CR 26903 - 8.0.1 merge to trunk
 *
 *         BackOffice <ARG> Summary Report overhaul (many CRs fixed)
 *
 *    16   360Commerce 1.15        5/14/2007 6:08:34 PM   Sandy Gu
 *         update inclusive information in financial totals and history tables
 *    15   360Commerce 1.14        5/14/2007 4:46:47 PM   Brett J. Larsen CR
 *         26477 - overloaded addSendPackageInfo to include tax info
 *         (required for reading shp_rds_sls_rtn)
 *    14   360Commerce 1.13        5/1/2007 12:16:12 PM   Brett J. Larsen CR
 *         26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *         feature)
 *    13   360Commerce 1.12        4/25/2007 10:00:19 AM  Anda D. Cadar   I18N
 *         merge
 *    12   360Commerce 1.11        11/9/2006 5:58:58 PM   Brett J. Larsen
 *         22978 - CTR - found while baselining CTR - some equality checks
 *         were incorrect
 *
 *         changed "==" comparisons to Util.isObjectEqual calls where
 *         appropriate
 *    11   360Commerce 1.10        10/20/2006 12:54:16 PM Charles D. Baker
 *         Revamped EOL behavior of transaction header for automated testing
 *         success.
 *    10   360Commerce 1.9         8/10/2006 11:17:44 AM  Brendan W. Farrell
 *         16500 - Remove Sales: xxx journaling because it has been moved up
 *         to base
 *         transaction class jouraling.
 *    9    360Commerce 1.8         6/8/2006 6:11:44 PM    Brett J. Larsen CR
 *         18490 - UDM - InstantCredit AuthorizationResponseCode changed to a
 *         String
 *    8    360Commerce 1.7         5/12/2006 5:26:37 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    7    360Commerce 1.6         4/27/2006 7:29:50 PM   Brett J. Larsen CR
 *         17307 - remove inventory functionality - stage 2
 *    6    360Commerce 1.5         1/25/2006 4:11:44 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    5    360Commerce 1.4         1/22/2006 11:41:58 AM  Ron W. Haight
 *         Removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:43:52 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:01 PM  Robert Pearse
 *:
 *    5    .v700     1.2.3.1     11/1/2005 11:06:34     Jason L. DeLeau 4181:
 *         Remove deprecated reference to getGiftCardRegistry
 *    4    .v700     1.2.3.0     10/24/2005 12:40:14    Deepanshu       CR
 *         6150: The fixed is merged from Gap as this is a proper fix to check
 *         the null condition.
 *    3    360Commerce1.2         3/31/2005 15:29:48     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:59     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:01     Robert Pearse
 *
 *    4    360Commerce1.3         12/13/2005 4:43:52 PM  Barry A. Pape
 *: SaleReturnTransaction.java,v $
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 3:29:48 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:59 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:01 PM  Robert Pearse
 *:
 *    4    .v710     1.2.2.0     9/21/2005 13:40:18     Brendan W. Farrell
 *         Initial Check in merge 67.
 *    3    360Commerce1.2         3/31/2005 15:29:48     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:24:59     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:01     Robert Pearse
 *
 *   Revision 1.53  2004/10/04 18:11:55  jdeleau
 *   @scr 7301 Make sure non-taxable stuff goes in the right
 *   financial totals bucket.
 *
 *   Revision 1.52  2004/09/23 00:30:51  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.51  2004/09/17 15:49:59  jdeleau
 *   @scr 7146 Define a taxable transaction, for reporting purposes.
 *
 *   Revision 1.50  2004/09/13 21:58:29  jdeleau
 *   @scr 6791 Transaction Level Send in regard to taxes
 *
 *   Revision 1.49  2004/09/07 21:44:57  mweis
 *   @scr 7012 Don't update kit header's inventory on POS sale (or return).
 *
 *   Revision 1.48  2004/09/01 20:34:20  mweis
 *   @scr 7012 Inventory updates for POS in the Returns arena.
 *
 *   Revision 1.47  2004/08/26 22:12:36  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.46  2004/08/26 20:24:43  lzhao
 *   @scr 6828: update clone and equals for attributes
 *
 *   Revision 1.45  2004/08/23 16:15:46  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.44  2004/08/10 07:20:46  mwright
 *   Merge (3) with top of tree
 *   Added send package count attribute to class, with accessor functions
 *
 *
 *   Revision 1.43  2004/07/16 02:53:09  cdb
 *   @scr 4559 Re-enabled error dialog for invalid discount.
 *
 *   Revision 1.42  2004/07/09 20:56:16  cdb
 *   @scr 6115    Removed unusual check that discount be less than discounted price. Updated to validate
 *   employee transaction discounts as well.
 *
 *
 *   Revision 1.41.2.1  2004/07/29 01:50:49  mwright
 *   Added send package count to class, with accessor functions
 *
 *
 *   Revision 1.41  2004/06/29 21:59:00  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.40  2004/06/28 21:39:15  jriggins
 *   @scr 5777 Added logic for copying item-level taxes over to the sale component of a price adjustment. Removed unecessary calls in SaleReturnTransaction.addPriceAdjustmentLineItem()
 *
 *   Revision 1.39  2004/06/28 16:53:47  aschenk
 *   @scr 4864 - Added Gift receipt option to Transaction menu
 *
 *   Revision 1.38  2004/06/26 21:55:11  mweis
 *   @scr 5533 Return with tax exemption transaction causes app to crash
 *
 *   Revision 1.37  2004/06/22 17:24:33  lzhao
 *   @scr 4670: code review
 *
 *   Revision 1.36  2004/06/21 13:01:46  lzhao
 *   @scr 4670: add flag to indicate the customer type: capture/link.
 *
 *   Revision 1.35  2004/06/19 14:02:17  lzhao
 *   @scr 4670: add attribute for flag of send customer:linking or capture
 *
 *   Revision 1.34  2004/06/16 13:35:15  lzhao
 *   @scr 4670: add attribute checkCustomerPresent for send.
 *
 *   Revision 1.33  2004/06/15 16:05:33  jdeleau
 *   @scr 2775 Add database entry for uniqueID so returns w/
 *   receipt will work, make some fixes to FinancialTotals storage of tax.
 *
 *   Revision 1.32  2004/06/15 00:44:31  jdeleau
 *   @scr 2775 Support register reports and financial totals with the new
 *   tax engine.
 *
 *   Revision 1.31  2004/06/10 23:06:34  jriggins
 *   @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service
 *
 *   Revision 1.30  2004/05/27 19:31:38  jdeleau
 *   @scr 2775 Remove unused imports as a result of tax engine rework
 *
 *   Revision 1.29  2004/05/27 16:59:22  mkp1
 *   @scr 2775 Checking in first revision of new tax engine.
 *
 *   Revision 1.28  2004/05/19 18:33:31  cdb
 *   @scr 5103 Updating to more correctly handle register reports.
 *
 *   Revision 1.27  2004/05/19 18:28:09  rsachdeva
 *   @scr 4670 Send: Multiple Sends
 *
 *   Revision 1.26  2004/05/11 23:03:01  jdeleau
 *   @scr 4218 Backout recent changes to remove TransactionDiscounts,
 *   going to go a different route and remove the newly added
 *   voids and grosses instead.
 *
 *   Revision 1.24  2004/04/28 19:51:45  jriggins
 *   @scr 3979 Code review cleanup
 *
 *   Revision 1.23  2004/04/27 20:01:17  jdeleau
 *   @scr 4218 Add in the concrete calls for register reports data, refactor
 *   the houseCardEnrollment methods to be in line with other FinancialTotals
 *   methods.
 *
 *   Revision 1.22  2004/04/22 07:26:29  jriggins
 *   @scr 3979 Excluding price adjustment composites from totals
 *
 *   Revision 1.21  2004/04/21 13:31:03  jriggins
 *   @scr 3979 Removed TaxCalculationEngine reference in addPriceAdjustemtnLineItem()
 *
 *   Revision 1.20  2004/04/20 12:52:16  jriggins
 *   @scr 3979 Uncommeted reference to TaxCalculationEngine
 *
 *   Revision 1.19  2004/04/19 03:26:16  jriggins
 *   @scr 3979 Changed addPriceAdjustmentLineItem() to use a SaleReturnItemIfc instance as return item
 *
 *   Revision 1.18  2004/04/16 22:31:29  jriggins
 *   @scr 3979 Changed addPLUItem() to addLineItem() in addPriceAdjustmentLineItem()
 *
 *   Revision 1.17  2004/04/15 15:39:56  jriggins
 *   @scr 3979 Added addPriceAdjustmentLineItem() method
 *
 *   Revision 1.16  2004/04/08 22:04:15  bjosserand
 *   @scr 4093 Transaction Reentry
 *
 *   Revision 1.15  2004/04/05 23:03:01  jdeleau
 *   @scr 4218 JavaDoc fixes associated with RegisterReports changes
 *
 *   Revision 1.14  2004/04/03 00:51:45  jriggins
 *   @scr 3979 merged 1.12 and 1.13
 *
 *   Revision 1.13  2004/04/03 00:21:15  jriggins
 *   @scr 3979 Price Adjustment feature dev
 *
 *   Revision 1.12  2004/04/02 23:07:34  jdeleau
 *   @scr 4218 Register Reports - House Account and initial changes to
 *   the way SummaryReports are built.
 *
 *   Revision 1.11  2004/03/16 18:27:06  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.10  2004/02/25 00:46:56  bwf
 *   @scr 3883 Credit Rework.
 *
 *   Revision 1.9  2004/02/20 15:30:10  khassen
 *   @scr 3631 Fix for journalHeader() method.
 *
 *   Revision 1.8  2004/02/17 20:37:12  baa
 *   @scr 3561 returns
 *
 *   Revision 1.6  2004/02/12 22:56:56  tfritz
 *   @scr 3718 Added nonTaxable functionality
 *
 *   Revision 1.5  2004/02/12 19:58:05  baa
 *   @scr 0 fix javadoc
 *
 *   Revision 1.4  2004/02/12 17:14:42  mcs
 *   Forcing head revision
 *
 *   Revision 1.3  2004/02/12 15:59:27  epd
 *   @scr N/A
 *   removed useless code
 *
 *   Revision 1.2  2004/02/11 23:28:51  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:34  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.9   Dec 03 2003 11:07:52   bwf
 * Updated cloneattributes.
 *
 *    Rev 1.8   Dec 01 2003 13:46:14   bwf
 * Updated for echeck declines.
 *
 *    Rev 1.7   Dec 01 2003 13:14:30   rrn
 * In journal strings, changed 'Removed' to 'Deleted'.
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.6   Nov 18 2003 11:05:44   bwf
 * Fixed clone.
 *
 *    Rev 1.5   Nov 16 2003 15:12:14   bwf
 * Added declined e check vector to hold tenderline items for franking.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.4   Oct 31 2003 13:18:40   nrao
 * Added code for Instant Credit Enrollment.
 *
 *    Rev 1.3   Oct 31 2003 11:33:30   nrao
 * Added methods for use with Instant Credit Enrollment.
 *
 *    Rev 1.2   Oct 17 2003 10:28:24   bwf
 * Added employeeDiscountID and removed unused imports.
 * Resolution for 3412: Feature Enhancement: Employee Discount
 *
 *    Rev 1.1   Oct 14 2003 11:53:10   baa
 * allow discounts to be applied to sale item in the case of an even exchange.
 * Resolution for 3408: Unable to tender transaction where item returned then sold with discount
 *
 *    Rev 1.0.2.1   Oct 10 2003 16:56:48   baa
 * allow even exchange with discount
 * Resolution for 3408: Unable to tender transaction where item returned then sold with discount
 *
 *    Rev 1.0.2.0   Sep 26 2003 07:51:52   blj
 * forcing a branch
 *
 *    Rev 1.0   Aug 29 2003 15:41:02   CSchellenger
 * Initial revision.
 *
 *    Rev 1.24   24 Jun 2003 20:06:22   mpm
 * Added code to set financials, bypass inventory updates, when in-process-void transactions are saved.
 *
 *    Rev 1.23   May 21 2003 20:04:04   mpm
 * Added facility to count transactions with returned items.
 * Resolution for Backoffice SCR-1957: Integrate Kintore code
 *
 *    Rev 1.22   May 18 2003 09:06:38   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.21   May 17 2003 13:20:32   mpm
 * Added hasReturnItems() method.
 * Resolution for POS SCR-2557: Implement daemon for transaction post-processor
 *
 *    Rev 1.20   May 06 2003 18:09:20   sfl
 * Put back the call for tax calculation. Do need it after each sale or return item is added.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.19   May 06 2003 17:48:18   sfl
 * Comment out an extra call on taxCalculationEnige
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.18   Mar 04 2003 18:07:28   sfl
 * Moved detailed tax calculation data structure and logic to ItemContainerProxy and TaxCalculationEngine
 * to keep SaleReturnTransaction clean.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.17   Feb 28 2003 15:01:30   sfl
 * Relocate the threshold amount base tax rule handling data attribute and its supporting methods' detailed definitions to ItemContainerProxy.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.16   Feb 15 2003 14:52:28   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.15   Jan 09 2003 13:04:26   sfl
 * Excluded return items from the tax override checking logic
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.14   Dec 13 2002 15:58:40   sfl
 * Make sure retrieved tax amount for return item is negative.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.13   Nov 22 2002 15:15:52   sfl
 * Set item level tax amount to zero when the item is
 * set to tax toggle off.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.12   11 Nov 2002 15:58:14   sfl
 * Removed unnecessary tax scope checking
 * during tax calculation for non tax override items.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.11   08 Nov 2002 16:11:48   sfl
 * Added extra item send flag value check so that shipping destination tax rule data will not be override by the local tax rules.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.10   04 Nov 2002 16:09:36   sfl
 * Use Constants to replace the integer values.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.9   04 Nov 2002 11:57:34   sfl
 * Make the manual tax override to work with the new tax calculation engine.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.8   08 Oct 2002 13:36:18   sfl
 * Cleaned out some unused code.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.7   04 Oct 2002 18:09:06   sfl
 * Minor enhancement on setting ItemTaxAmount attribute value.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.6   03 Oct 2002 17:01:40   sfl
 * Added new method to let each line item to have its own tax data.
 *
 *    Rev 1.5   10 Sep 2002 16:58:14   sfl
 * Fixed lineitem indexing during item deletion from tax grouped
 * hashtable.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.4   Sep 06 2002 15:49:08   HDyer
 * Added method hasGiftRegistryItems() to iterate through item list and return true if any are linked to a gift registry.
 * Resolution for 1804: Auto-Print Gift Receipt for Gift Registry Items Feature
 *
 *    Rev 1.3   04 Sep 2002 13:37:46   sfl
 * Update the methods to add items and remove items
 * by tax group from the hashtable in the transaction.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.2   28 Aug 2002 16:23:34   sfl
 * Added a hashtable to hold tax group id (as key) and the vector of items of that tax group. Also added methods to support that
 * hashtable.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.1   Aug 21 2002 12:46:24   DCobb
 * Added hasAlterationItems().
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.0   Jun 03 2002 17:06:10   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderConstantsIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItem;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataContainerIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.SendTaxUtil;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditApprovalStatus;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.commons.lang3.StringUtils;

/**
 * Sale or return transaction object.
 *
 * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc
 */
public class SaleReturnTransaction extends AbstractTenderableTransaction
    implements SaleReturnTransactionIfc, InstantCreditTransactionIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 1836685048643285228L;

    /**
     * item handler reference
     */
    protected ItemContainerProxyIfc itemProxy = null;

    /**
     * order identifier
     */
    protected String orderID = "";

    /**
     * Vector to hold deleted line items, may come from several item clear
     * actions.
     */
    protected Vector<SaleReturnLineItemIfc> deletedLineItems = new Vector<SaleReturnLineItemIfc>(1);

    /**
     * Employee Discount ID
     */
    protected String employeeDiscountID = null;

    /**
     * Instant Credit Enrollment
     */
    protected InstantCreditIfc instantCredit = null;

    // Return Tenders
    protected ReturnTenderDataContainerIfc returnTendersContainer;

    /**
     * Flag indicating that the transaction is re-entry mode.
     */
    boolean reentryMode = false;

    /**
     * Flag for checked customer physically present. This is actually used to
     * know if this check has been done or not.
     */
    boolean checkedCustomerPresent = false;

    /**
     * This is used to indicate whether customer is actually physially present
     * or not
     */
    boolean customerPhysicallyPresent = false;

    /**
     * Flag to indicate what kind of customer used in send transaction It can be
     * linked customer or captured customer
     */
    boolean sendCustomerLinked = true;

    /**
     * Flag indicating if there is a transaction wide gift receipt for this sale
     * transaction
     */
    boolean transactionGiftReceiptAssigned = false;

    /**
     * This value is stored in the retail transaction table, to indicate how
     * many send items are present
     */
    int sendPackageCount = 0;
    
    protected TaxRulesVO taxRulesVO = null;

    // DOB entered by the associate for items that have an age restriction.
    protected EYSDate ageRestrictedDOB = null;

    protected boolean salesAssociateModifiedFlag = false;

    protected String returnTicketID = null;

    /**
     * The unique identifier for an order that was created externally. This ID
     * is more specific than {@link #getExternalOrderNumber()}.
     */
    protected String externalOrderID = null;

    /**
     * The shopper-friendly number of order that was created externally. This
     * number can be displayed and used as a reference to
     * {@link #externalOrderID}.
     */
    protected String externalOrderNumber = null;

    /**
     * Indicator of what system is managing the external order. e.g. ATG or
     * Siebel. Defaults to ExternalOrderConstantsIfc#TYPE_UNKNOWN
     */
    protected int externalOrderType = ExternalOrderConstantsIfc.TYPE_UNKNOWN;

    /**
     * Flag indicating if the transaction requires a service contract
     */
    protected boolean requireServiceContractFlag = false;

    /**
     * Placeholder for legal documents: like phone contracts or any other legal documents
     * associated with a transaction
     */
    protected List<LegalDocumentIfc> legalDocuments = new ArrayList<LegalDocumentIfc>();
    
    /**
     * Checks send level assigned.
     */
    protected boolean transactionLevelSendAssigned = false;  
    
    /**
     * Send package line item for each send
     */
    protected Vector<SendPackageLineItemIfc> sendPackages;  
    
   
    /**
     * Currency type for Transaction
     */
    protected CurrencyTypeIfc currencyType; 
   
    /**
     * Country Code for the transaction
     */
    protected String transactionCountryCode;
    
    /**
     * Constructs SaleReturnTransaction object. 
     */
    public SaleReturnTransaction()
    {
        initialize();
    }

    /**
     * Constructs SaleReturnTransaction object.
     *
     * @param station The workstation(register) to create a transaction for
     */
    public SaleReturnTransaction(WorkstationIfc station)
    {
        // initialize Transaction object
        initialize(station);
        // initialize this object
        initialize();
    }

    /**
     * Constructs SaleReturnTransaction object.
     *
     * @param transactionID id for the transaction
     */
    public SaleReturnTransaction(String transactionID)
    {
        // initialize Transaction object
        initialize(transactionID);
        // initialize this object
        initialize();
    }

    /**
     * Initialize values.
     */
    protected void initialize()
    {
        itemProxy = DomainGateway.getFactory().getItemContainerProxyInstance();
        transactionType = TransactionIfc.TYPE_SALE;
        sendPackages = new Vector<SendPackageLineItemIfc>(1); 
        CurrencyTypeIfc baseCurrency = DomainGateway.getBaseCurrencyType();
        currencyType=baseCurrency;
        super.initialize();
    }

    /**
     * Clones SaleReturnTransaction object
     *
     * @return instance of SaleReturnTransaction object
     */
    public Object clone()
    {
        SaleReturnTransaction srt = new SaleReturnTransaction();

        setCloneAttributes(srt);

        return srt;
    }

    /**
     * Sets clone attributes. This method is provided to facilitate
     * extensibility.
     *
     * @param newClass new instance of SaleReturnTransaction
     */
    protected void setCloneAttributes(SaleReturnTransaction newClass)
    {

        // set attributes
        super.setCloneAttributes(newClass);
        if (itemProxy != null)
        {
            newClass.setItemContainerProxy((ItemContainerProxyIfc)itemProxy.clone());
        }
        newClass.updateTransactionTotals();
        newClass.setTransactionTax((TransactionTaxIfc)getTransactionTax().clone());

        if (this.getDeletedLineItems() != null)
        {
            if (this.getDeletedLineItems().size() > 0)
            {
                for (int i = 0; i < this.getDeletedLineItems().size(); i++)
                {
                    SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)getDeletedLineItems().elementAt(i);
                    newClass.addDeletedLineItems((SaleReturnLineItemIfc)lineItem.clone());
                }
            }
        }

        newClass.setSendCustomerLinked(isSendCustomerLinked());
        newClass.setTransactionGiftReceiptAssigned(isTransactionGiftReceiptAssigned());
        newClass.setCheckedCustomerPresent(checkedCustomerPresent);
        newClass.setCustomerPhysicallyPresent(customerPhysicallyPresent);

        if (returnTendersContainer != null)
        {
            ReturnTenderDataElementIfc[] tenderElements = getReturnTenderElements();
            if (tenderElements != null)
            {
                ReturnTenderDataElementIfc[] newTenderElements = tenderElements.clone();
                newClass.getReturnTenderDataContainer().addTenderElements(newTenderElements);
            }
        }

        newClass.setOrderID(orderID);
        newClass.setEmployeeDiscountID(employeeDiscountID);
        newClass.setReentryMode(reentryMode);
        newClass.setSendPackageCount(sendPackageCount);
        if (instantCredit != null)
        {
            newClass.setInstantCredit((InstantCreditIfc)instantCredit.clone());
        }
        newClass.setAgeRestrictedDOB(ageRestrictedDOB);
        newClass.setSalesAssociateModifiedFlag(salesAssociateModifiedFlag);
        newClass.setExternalOrderID(externalOrderID);
        newClass.setExternalOrderNumber(externalOrderNumber);
        newClass.setExternalOrderType(externalOrderType);
        newClass.setRequireServiceContractFlag(requireServiceContractFlag);
        newClass.setTransactionLevelSendAssigned(this.transactionLevelSendAssigned);        
        newClass.setCurrencyType(currencyType);
        newClass.setTransactionCountryCode(transactionCountryCode);
        
        
        if(legalDocuments != null && !legalDocuments.isEmpty())
        {
            Iterator<LegalDocumentIfc> it = legalDocuments.iterator();
            while (it.hasNext())
            {
                LegalDocumentIfc doc = (LegalDocumentIfc)it.next();
                newClass.addLegalDocument((LegalDocumentIfc)doc.clone());
            }

        }
        // set send package info
        for (int s = 0; s < this.getItemSendPackagesCount(); s++)
        {
            SendPackageLineItemIfc sendPackage = sendPackages.get(s);
            newClass.addSendPackage((SendPackageLineItemIfc)sendPackage.clone());
        } 
    }

    /**
     * Calculates FinancialTotals based on current transaction.
     *
     * @return FinancialTotalsIfc object
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();
        if (getTransactionStatus() == TransactionIfc.STATUS_CANCELED)
        {
            financialTotals.setNumberCancelledTransactions(1);
            financialTotals.setAmountCancelledTransactions(totals.getSubtotal().subtract(totals.getDiscountTotal())
                    .abs());
        }
        else
        {
            // get transaction financial totals
            financialTotals.add(getSaleReturnFinancialTotals());

            financialTotals.add(getLineItemsFinancialTotals());

            // get totals from tender line items
            financialTotals.add(getTenderFinancialTotals(getTenderLineItems(), getTransactionTotals()));

            // Get the totals for user deleted lines.
            financialTotals.setUnitsLineVoids(getUnitsLineVoids());
            financialTotals.setAmountLineVoids(getAmountLineVoids());
        }

        setHouseCardEnrollmentCounts(financialTotals);
        
        return (financialTotals);
    }

    /**
     * Determines if an attempt to perform a House Card Enrollment has occurred
     * and sets the counts accordingly.
     * @param financialTotals
     */
    protected void setHouseCardEnrollmentCounts(FinancialTotalsIfc financialTotals)
    {
        if (getInstantCredit() != null)
        {
            if (InstantCreditApprovalStatus.APPROVED.equals(getInstantCredit().getApprovalStatus()))
            {
                financialTotals.addHouseCardEnrollmentsApproved(1);
            }
            else if (InstantCreditApprovalStatus.DECLINED.equals(getInstantCredit().getApprovalStatus()))
            {
                financialTotals.addHouseCardEnrollmentsDeclined(1);
            }
        }
    }
    
    /**
     * Derives the additive financial totals for a sale for transaction values,
     * not including line items and tenders .
     *
     * @return additive financial totals
     */
    public FinancialTotalsIfc getSaleReturnFinancialTotals()
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

        // gross total is transaction subtotal with discount applied
        CurrencyIfc gross = totals.getSubtotal().subtract(totals.getDiscountTotal());
        CurrencyIfc tax = totals.getTaxTotal();
        CurrencyIfc inclusiveTax = totals.getInclusiveTaxTotal();

        // Back out shipping tax since shipping tax are tracked separately in
        // financial totals.
        tax = tax.subtract(financialTotals.getAmountTaxShippingCharges());
        inclusiveTax = inclusiveTax.subtract(financialTotals.getAmountInclusiveTaxShippingCharges());

        Vector<AbstractTransactionLineItemIfc> lineItemsVector = itemProxy.getLineItemsVector();
        Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator();
        SaleReturnLineItemIfc temp = null;
        while (i.hasNext())
        {
            // check for sale items with corresponding kit type
            temp = (SaleReturnLineItemIfc)i.next();
            if (temp.isPriceAdjustmentLineItem())
            {
                financialTotals.addUnitsPriceAdjustments(new BigDecimal(1));
            }
        }

        // handle transaction values
        if (transactionType == TransactionIfc.TYPE_SALE || transactionType == TransactionIfc.TYPE_EXCHANGE)
        {
            // set tax exempt, taxable sales
            // Note: at this time, tax exempt is only managed at the transaction
            // level
            if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                financialTotals.addAmountGrossTaxExemptTransactionSales(gross);
                financialTotals.addCountGrossTaxExemptTransactionSales(1);
            }

            // isTaxableTransaction will return false for tax exempt
            if (isTaxableTransaction())
            {
                financialTotals.addAmountGrossTaxableTransactionSales(gross);
                financialTotals.addCountGrossTaxableTransactionSales(1);
                financialTotals.addAmountTaxTransactionSales(tax);
                financialTotals.addAmountInclusiveTaxTransactionSales(inclusiveTax);
            }
            else
            {
                financialTotals.addAmountGrossNonTaxableTransactionSales(gross);
                financialTotals.addCountGrossNonTaxableTransactionSales(1);
            }
        }
        else
        {
            // set tax exempt, taxable sales
            // Note: at this time, tax exempt is only managed at the transaction
            // level
            if (getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                financialTotals.addAmountGrossTaxExemptTransactionReturns(gross.abs());
                financialTotals.addCountGrossTaxExemptTransactionReturns(1);
            }

            // isTaxableTransaction will return false for tax exempt
            if (isTaxableTransaction())
            {
                financialTotals.addAmountGrossTaxableTransactionReturns(gross.abs());
                financialTotals.addCountGrossTaxableTransactionReturns(1);
                financialTotals.addAmountTaxTransactionReturns(tax.abs());
                financialTotals.addAmountInclusiveTaxTransactionReturns(inclusiveTax.abs());
            }
            else
            {
                financialTotals.addAmountGrossNonTaxableTransactionReturns(gross.abs());
                financialTotals.addCountGrossNonTaxableTransactionReturns(1);
            }
        }

        // handle discount quantities
        // Transaction Amounts are updated in getLineItemsFinancialTotals() by
        // ItemPrice
        TransactionDiscountStrategyIfc[] transDiscounts = getTransactionDiscounts();
        if (transDiscounts != null)
        {
            for (int x = 0; x < transDiscounts.length; x++)
            {
                if (transDiscounts[x].getAssignmentBasis() == DiscountRuleIfc.ASSIGNMENT_EMPLOYEE)
                {
                    financialTotals.addUnitsGrossTransactionEmployeeDiscount(new BigDecimal(1));
                }
                else
                {
                    financialTotals.addNumberTransactionDiscounts(1);
                }
            }
        }

        // Add the round (cash) change amount to the financial totals object.
        if (getTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.NEGATIVE)
        {
            financialTotals.addAmountChangeRoundedOut(getTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }
        if (getTransactionTotals().getCashChangeRoundingAdjustment().signum() == CurrencyIfc.POSITIVE)
        {
            financialTotals.addAmountChangeRoundedIn(getTransactionTotals().getCashChangeRoundingAdjustment().abs());
        }

        // set transactions-with-returned-items count
        if (hasReturnItems())
        {
            financialTotals.setTransactionsWithReturnedItemsCount(1);
        }

        return (financialTotals);
    }

    /**
     * Derives the additive financial totals for line items.
     *
     * @return additive financial totals for line items
     */
    public FinancialTotalsIfc getLineItemsFinancialTotals()
    {
        FinancialTotalsIfc financialTotals = DomainGateway.getFactory().getFinancialTotalsInstance();

        SaleReturnLineItemIfc[] items = getLineItemsExcluding(ItemConstantsIfc.ITEM_KIT_CODE_HEADER
                | ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM);

        // Kit Headers and price adjustment composite objects are not included
        // in financial totals
        for (int i = 0; i < items.length; i++)
        {
            financialTotals.add(items[i].getFinancialTotals());
        }

        return financialTotals;
    }

    /**
     * Adds a line item to the transaction line item vector.
     *
     * @param lineItem SaleReturnLineItemIfc reference
     */
    public void addLineItem(SaleReturnLineItemIfc lineItem)
    {
        addLineItem((AbstractTransactionLineItemIfc)lineItem);
    }

    /**
     * Adds a line item to the transaction line item vector.
     *
     * @param lineItem AbstractTransactionLineItemIfc
     */
    public void addLineItem(AbstractTransactionLineItemIfc lineItem)
    {
        itemProxy.addLineItem(lineItem);
        // doTaxCalculationForAllLineItems();
        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
        
        totals.updateNumberOfItemsSold(getItemContainerProxy().getLineItems());
        
    }

    /**
     * Adds a single PLU item to the transaction.
     *
     * @param pItem PLU item
     * @return transaction line item
     */
    public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem)
    {
        return addPLUItem(pItem, BigDecimalConstants.ONE_AMOUNT);
    }

    /**
     * Adds a PLU item to the transaction.
     *
     * @param pItem PLU item
     * @param qty quantity
     * @return transaction line item
     */
    public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty)
    {
        SaleReturnLineItemIfc srli = itemProxy.addPLUItem(pItem, qty);
        completeAddPLUItem(srli);

        return (srli);
    }

    //---------------------------------------------------------------------
    /**
     * Adds an external order PLU item to the transaction
     * @param pItem PLU item
     * @param pExternalOrderItem external order item
     * @return transaction line item
     */
    //---------------------------------------------------------------------
    public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem,
                                            ExternalOrderItemIfc pExternalOrderItem)
    {
        SaleReturnLineItemIfc srli = itemProxy.addPLUItem(pItem, pExternalOrderItem);
        completeAddPLUItem(srli);

        return (srli);
    }

    //---------------------------------------------------------------------
    /**
     * Complete adding the sale line item
     * @param srli the sale line item
     */
    //---------------------------------------------------------------------
    protected void completeAddPLUItem(SaleReturnLineItemIfc srli)
    {
         if (this.getSendTaxRules() != null && this.isTransactionLevelSendAssigned())
         {
             SendTaxUtil util = new SendTaxUtil();
             TaxRulesVO taxRulesVO = this.getSendTaxRules();
             util.setTaxRulesForLineItem(taxRulesVO, srli);
         }
         updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                 .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
         
         totals.updateNumberOfItemsSold(getItemContainerProxy().getLineItems());
    }

    /**
     * Adds a return item to the transaction.
     *
     * @param pItem PLUItem object
     * @param rItem ReturnItem object
     * @param qty quantity
     * @return transaction line item
     */
    public SaleReturnLineItemIfc addReturnItem(PLUItemIfc pItem, ReturnItemIfc rItem, BigDecimal qty)
    {
        SaleReturnLineItemIfc srli = itemProxy.addReturnItem(pItem, rItem, qty);

        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
        return (srli);
    }

    //---------------------------------------------------------------------
    /**
     * Adds an external order return item to the transaction. <P>
     * @param pItem PLU item
     * @param rItem retrun item
     * @param pExternalOrderItem external order item
     * @return transaction line item
     */
    //---------------------------------------------------------------------
    public SaleReturnLineItemIfc addReturnItem(PLUItemIfc pItem,
                                               ReturnItemIfc rItem,
                                               ExternalOrderItemIfc pExternalOrderItem)
    {
        SaleReturnLineItemIfc srli = itemProxy.addReturnItem(pItem, rItem, pExternalOrderItem);

        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
        return (srli);
    }

    /**
     * Adds a price adjustment line item (including its corresponding return and
     * sale components) to the transaction.
     *
     * @param saleLineItem price adjustment sale line item. Reference is
     *            modified.
     * @param returnItem price adjustment return line item. Reference is
     *            modified.
     * @return PriceAdjustmentLineItemIfc instance representing the price
     *         adjustment
     */
    public PriceAdjustmentLineItemIfc addPriceAdjustmentLineItem(SaleReturnLineItemIfc saleLineItem,
            SaleReturnLineItemIfc returnLineItem)
    {
        PriceAdjustmentLineItemIfc priceAdjLineItem = null;
        priceAdjLineItem = DomainGateway.getFactory().getPriceAdjustmentLineItemInstance();
        priceAdjLineItem.initialize(saleLineItem, returnLineItem);

        /*
         * Add the return and sale line items to the transaction.
         */
        // Add return line item.
        addLineItem(returnLineItem);

        // Add sale line item.
        addLineItem(saleLineItem);

        // Now that we have the return and sale elements, create and add the
        // price adjustment line item.
        // The PriceAdjustmentLineItemIfc instance is added for use by the UI
        // and other facilities
        addLineItem(priceAdjLineItem);

        return priceAdjLineItem;
    }

    /**
     * Remove a line from the transaction.
     *
     * @param index Line Item to remove
     */
    public void removeLineItem(int index)
    {
        itemProxy.removeLineItem(index);
        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
    }

    /**
     * Add the amount of line void to the transaction.
     *
     * @param lineItem an abstract line item
     */
    public void incrementLineVoid(AbstractTransactionLineItemIfc lineItem)
    {
        itemProxy.incrementLineVoid(lineItem);
    }

    /**
     * Remove multiple lines from the transaction.
     *
     * @param indices Indeces to remove
     */
    public void removeLineItems(int[] indices)
    {
        itemProxy.removeLineItems(indices);
        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
    }

    /**
     * Retrieve clone of a line item from a transaction.
     *
     * @param index index into line item vector
     * @return line item object
     */
    public AbstractTransactionLineItemIfc retrieveItemByIndex(int index)
    {
        return (itemProxy.retrieveItemByIndex(index));
    }

    /**
     * Retrieve of a line item from a transaction.
     *
     * @param lineItemNumber item number
     * @return line item object
     */
    public AbstractTransactionLineItemIfc retrieveLineItemByID(int lineItemNumber)
    {
        return (itemProxy.retrieveLineItemByID(lineItemNumber));
    }

    /**
     * Retreives the PriceAdjustmentLineItemIfc instance that matches the
     * provided reference, if available
     *
     * @param priceAdjReference The reference by which to search
     * @return A memory reference of the PriceAdjustmentLineItemIfc instance
     *         that matches the provided reference, if available
     */
    public PriceAdjustmentLineItemIfc retrievePriceAdjustmentByReference(int priceAdjReference)
    {
        return (itemProxy.retrievePriceAdjustmentByReference(priceAdjReference));
    }

    /**
     * Replace a line item in a transaction and in all best deal groups that
     * contain it.
     *
     * @param lineItem lineItem to replace the old one with
     * @param index Index to do the replacement at
     */
    public void replaceLineItem(AbstractTransactionLineItemIfc lineItem, int index)
    {
        itemProxy.replaceLineItem(lineItem, index);
        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
    }

    /**
     * Calculates and returns the sum of any advanced pricing discounts applied
     * for this transaction. This total includes any store coupon discounts
     * calculated using the best deal calculation.
     *
     * @return CurrencyIfc total
     */
    public CurrencyIfc getAdvancedPricingDiscountTotal()
    {
        return (itemProxy.getAdvancedPricingDiscountTotal());
    }

    /**
     * Returns the best deal winners.
     *
     * @return list of best deal winners
     */
    public ArrayList<BestDealGroupIfc> getBestDealWinners()
    {
        return itemProxy.getBestDealWinners();
    }

    /**
     * Calculates and applies the best deal discounts for advanced pricing
     * rules.
     */
    public void calculateBestDeal()
    {
        itemProxy.calculateBestDeal();
        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
    }

    /**
     * Returns a boolean indicating whether the line item is a source for any
     * advanced pricing rule held by this transaction.
     *
     * @param source The source to check for discounts
     * @return boolean true if source can be used, false otherwise
     */
    public boolean isPotentialSource(DiscountSourceIfc source)
    {
        return (itemProxy.isPotentialSource(source));
    }

    /**
     * Returns a boolean indicating whether the line item is a target for any
     * advanced pricing rule held by this transaction.
     *
     * @param target Item to check for advanced pricing rule
     * @return boolean true if target can be used, false otherwise
     */
    public boolean isPotentialTarget(DiscountTargetIfc target)
    {
        return (itemProxy.isPotentialTarget(target));
    }

    /**
     * Retrieves line items array in vector form.
     *
     * @return line item vector for this transaction
     */
    public Vector<AbstractTransactionLineItemIfc> getLineItemsVector()
    {
        return itemProxy.getLineItemsVector();
    }

    /**
     * Retrieves iterator for line items array.
     *
     * @return iterator for line items array
     */
    public Iterator<AbstractTransactionLineItemIfc> getLineItemsIterator()
    {
        return itemProxy.getLineItemsIterator();
    }

    /**
     * Retrieves line items array.
     *
     * @return line items for this transaction
     */
    public AbstractTransactionLineItemIfc[] getLineItems()
    {
        return itemProxy.getLineItems();
    }
    
    /**
     * Retrieves lineItems that are included in transaction totals
     * 
     * @return array of totalable line items.
     */
    public AbstractTransactionLineItemIfc[] getTotalableLineItems()
    {
        List<AbstractTransactionLineItemIfc> itemList = new ArrayList<AbstractTransactionLineItemIfc>();
        for (AbstractTransactionLineItemIfc abstractLineItem : getLineItemsVector())
        {
            if (abstractLineItem.isTotalable())
            {
                itemList.add(abstractLineItem);
            }
        }
        return itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.RetailTransactionIfc#getLineItemsExceptExclusions()
     */
    public AbstractTransactionLineItemIfc[] getLineItemsExceptExclusions()
    {
        List<AbstractTransactionLineItemIfc> itemList = new ArrayList<AbstractTransactionLineItemIfc>(getLineItemsSize());
        for (AbstractTransactionLineItemIfc abstractLineItem : getLineItemsVector())
        {
            if (abstractLineItem instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)abstractLineItem;
                if (lineItem.isPartOfPriceAdjustment() // don't include any price adjustment components
                       || (lineItem.isSaleLineItem() && lineItem.isKitComponent()) // don't include any sale kit comps
                       || (lineItem.isReturnLineItem() && lineItem.isKitHeader()) // don't include any return kit headers
                       || (lineItem.isPriceAdjustmentLineItem() && lineItem.isKitHeader()) // don't include any adj kit headers
                       || (lineItem.isPriceCancelledDuringPickup())) // don't include any price-cancelled line
                {
                    continue;
                }
                itemList.add(lineItem);
            }
        }
        return itemList.toArray(new AbstractTransactionLineItemIfc[itemList.size()]);
    }

    /**
     * Retrieves a Collection containing any KitHeaderLineItemIfcs that are
     * associated with this transaction.
     *
     * @return Collection of KitHeaderLineItemIfcs
     */
    public Collection<SaleReturnLineItemIfc> getKitHeaderLineItems()
    {
        return itemProxy.getKitHeaderLineItems();
    }

    /**
     * Checks to see if any line items in this transaction have been flagged to
     * print a gift receipt. If so, a flag is set for the transaction to signify
     * that this transaction has items that need a gift receipt. @ return
     * boolean gift receipt flag
     */
    public boolean hasGiftReceiptItems()
    {
        boolean giftReceipt = false;
        Iterator<AbstractTransactionLineItemIfc> i = itemProxy.getLineItemsIterator();

        while (i.hasNext())
        {
            if (((SaleReturnLineItemIfc)i.next()).isGiftReceiptItem())
            {
                giftReceipt = true;
                break;
            }
        }
        return giftReceipt;
    }

    /**
     * Checks to see if any line items in this transaction are linked to a gift
     * registry. If so, a flag is set for the transaction to signify that this
     * transaction has items that are linked to a gift registry. @ return
     * boolean gift registry flag
     */
    public boolean hasGiftRegistryItems()
    {
        boolean giftRegistry = false;
        Iterator<AbstractTransactionLineItemIfc> i = itemProxy.getLineItemsIterator();

        while (i.hasNext())
        {
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)i.next();
            if (item.getRegistry() != null)
            {
                giftRegistry = true;
                break;
            }
        }
        return giftRegistry;
    }

    /**
     * Checks to see if any line items in this transaction are alteration items.
     * If so, a flag is set for the transaction to signify that this transaction
     * has items that need alteration receipts.
     * @ return boolean alteration flag
     */
    public boolean hasAlterationItems()
    {
        boolean alteration = false;
        Iterator<AbstractTransactionLineItemIfc> i = itemProxy.getLineItemsIterator();
        SaleReturnLineItemIfc item = null;

        while (i.hasNext())
        {
            item = (SaleReturnLineItemIfc)i.next();
            if (item.isSaleLineItem() && item.isAlterationItem())
            {
                alteration = true;
                break;
            }
        }
        return alteration;
    }

    /**
     * Checks to see if any line items in this transaction are service items.
     * If so, a flag is set for the transaction to signify that this transaction
     * has items.
     * @return boolean serviceItem flag
     */
    public boolean hasServiceItems()
    {
        boolean serviceItem = false;
        Iterator<AbstractTransactionLineItemIfc> i = itemProxy.getLineItemsIterator();
        SaleReturnLineItemIfc item = null;

        while (i.hasNext())
        {
            item = (SaleReturnLineItemIfc)i.next();
            if (item.getPLUItem().getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE )
            {
                serviceItem = true;
                break;
            }
        }
        return serviceItem;
    }
    /**
     * Checks to see if any line items in this transaction have been flagged as
     * send items. If so, a flag is set for the transaction to signify that this
     * transaction has items that need to be send. @ return boolean send flag
     */
    public boolean hasSendItems()
    {
        boolean send = false;
        Iterator<AbstractTransactionLineItemIfc> i = itemProxy.getLineItemsIterator();
        SaleReturnLineItemIfc item = null;

        while (i.hasNext())
        {
            item = (SaleReturnLineItemIfc)i.next();
            if (item.isSaleLineItem() && item.getItemSendFlag())
            {
                send = true;
                break;
            }
        }
        return send;
    }

    /**
     * This method checks to see if a customer is present during a send
     * transaction
     * @deprecated see {@link SaleReturnTransactionIfc.isCustomerPhysicallyPresent()}
     * @return
     */
    public boolean isCustomerPresentDuringSend()
    {
        boolean present = false;

        /*
         * This method should be completed after the send service is reworked.
         * The send service will ask if the customer is present or not. This
         * method should check that whereever it is implemented.
         */

        return present;
    }

    /**
     * Retrieves a subset of line items excluding those of the type passed in as
     * an argument. This method is used for kit item display and manipulation.
     *
     * @param type an int indicating the type of line item to exclude. Valid
     *            types are declared in ItemKitConstantsIfc
     * @see oracle.retail.stores.domain.stock.ItemKitConstantsIfc
     * @return an array containing all line items from this transaction
     *         excluding those of type
     */
    public SaleReturnLineItemIfc[] getLineItemsExcluding(int type)
    {
        return (itemProxy.getLineItemsExcluding(type));
    }

    /**
     * Retrieves a subset of line items excluding those of the type passed in as
     * an argument. This method is used for kit item display and manipulation.
     *
     * @param type an int indicating the type of line item to exclude. Valid
     *            types are declared in ItemKitConstantsIfc
     * @see oracle.retail.stores.domain.stock.ItemKitConstantsIfc
     * @return an array containing all line items from this transaction
     *         excluding those of type
     */
    public SaleReturnLineItemIfc[] getSaleLineItemsExcluding(int type)
    {
        return (itemProxy.getSaleLineItemsExcluding(type));
    }

    /**
     * Retrieves a subset of line items excluding those of the type passed in as
     * an argument. This method is used for kit item display and manipulation.
     *
     * @param type an int indicating the type of line item to exclude. Valid
     *            types are declared in ItemKitConstantsIfc
     * @see oracle.retail.stores.domain.stock.ItemKitConstantsIfc
     * @return an array containing all line items from this transaction
     *         excluding those of type
     */
    public SaleReturnLineItemIfc[] getReturnLineItemsExcluding(int type)
    {
        return (itemProxy.getReturnLineItemsExcluding(type));
    }

    /**
     * Retrieves a subset of price adjustment line items excluding those of the
     * type passed in as an argument. This method is used for kit item display
     * and manipulation.
     *
     * @param type an int indicating the type of line item to exclude. Valid
     *            types are declared in ItemKitConstantsIfc
     * @see oracle.retail.stores.domain.stock.ItemKitConstantsIfc
     * @return an array containing all line items from this transaction
     *         excluding those of type
     */
    public SaleReturnLineItemIfc[] getPriceAdjustmentLineItemsExcluding(int type)
    {
        return (itemProxy.getPriceAdjustmentLineItemsExcluding(type));
    }


    /**
     * Retrieves the line items that are part of the group passed as the
     * argument.
     *
     * @param prodGroupID product group identifier
     * @return array of SaleReturnLineItemIfc[]
     */
    public SaleReturnLineItemIfc[] getProductGroupLineItems(String prodGroupID)
    {
        return (itemProxy.getProductGroupLineItems(prodGroupID));
    }

    /**
     * Sets the line items array
     *
     * @param items array of AbstractTransactionLineItemIfc references
     */
    public void setLineItems(AbstractTransactionLineItemIfc[] items)
    {
        itemProxy.setLineItems(items);
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Check line quantities and reset transaction type, if needed.
     */
    protected void resetTransactionType()
    {
        // set default type
        int type = TransactionIfc.TYPE_SALE;
        // Only used in case of trans with grandtotal of zero and contains all
        // return items,
        // otherwise, goes ignored
        boolean allReturnItems = true;
        // get items
        AbstractTransactionLineItemIfc[] items = getLineItems();
        int numberItems = 0;
        if (items != null)
        {
            numberItems = items.length;
        }
        for (int i = 0; i < numberItems; i++)
        {

            // If item is SaleReturnLineItem
            if (items[i] instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)items[i];
                if (srli.isReturnLineItem())
                {
                    // if quantity negative and total negative,
                    // set for return and exit
                    if (totals.getGrandTotal().signum() == CurrencyIfc.NEGATIVE)
                    {
                        type = TransactionIfc.TYPE_RETURN;
                        break;
                    }
                    // if total is zero and trans contains all return items,
                    // then set
                    // type to RETURN, otherwise, we conclude it's an exchange
                    // trans
                    else if (totals.getGrandTotal().signum() == CurrencyIfc.ZERO)
                    {
                        if (i + 1 == numberItems && allReturnItems)
                        {
                            type = TransactionIfc.TYPE_RETURN;
                        }
                    }
                }
                else
                {
                    allReturnItems = false;
                }
            }
        }

        // set transaction type
        setTransactionType(type);
    }

    /**
     * Adds a transaction discount and updates the transaction totals.
     *
     * @param disc TransactionDiscountStrategyIfc
     */
    public void addTransactionDiscount(TransactionDiscountStrategyIfc disc)
    {
        addTransactionDiscount(disc, true);
    }

    /**
     * Adds a transaction discount and optionally updates the transaction
     * totals.
     *
     * @param disc TransactionDiscountStrategyIfc
     * @param doUpdate flag indicating transaction totals should be updated
     */
    public void addTransactionDiscount(TransactionDiscountStrategyIfc disc, boolean doUpdate)
    {
        itemProxy.addTransactionDiscount(disc);
        if (doUpdate)
        {
            // update totals
            updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());
        }
    }

    /**
     * Adds a transaction discount and updates the transaction totals during
     * the tender process.
     *
     * @param disc TransactionDiscountStrategyIfc
     */
    public void addTransactionDiscountDuringTender(TransactionDiscountStrategyIfc disc)
    {
        // For this class and most class that extend it, this the same as adding a discount
        // at any other time.
        addTransactionDiscount(disc);
    }

    /**
     * Clears transaction discounts by percentage.
     *
     * @param doTotals flag indicating totals should be updated
     */
    public void clearTransactionDiscountsByPercentage(boolean doTotals)
    {
        itemProxy.clearTransactionDiscountsByPercentage();
        // check to update totals
        if (doTotals)
        {
            // update totals
            updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());
        }
    }

    /**
     * Removes Preferred Customer discounts that were added to the list of
     * AdvancedPricingRules.
     */
    public void clearCustomerBestDealDiscounts()
    {
        itemProxy.clearCustomerBestDealDiscounts();
    }

    /**
     * Clears transaction discounts by percentage.
     *
     * @param doTotals flag indicating totals should be updated
     */
    public void clearCustomerDiscountsByPercentage(boolean doTotals)
    {
        itemProxy.clearCustomerDiscountsByPercentage();
        // check to update totals
        if (doTotals)
        {
            updateTransactionTotals(getLineItems(), getTransactionDiscounts(), getTransactionTax());
        }
    }

    /**
     * Sets enabled flag on transaction discounts by percentage to specified
     * value. CustomerDiscountByPercentage discounts are not affected.
     *
     * @param enableFlag desired setting of enable flag
     * @param doTotals flag indicating totals should be updated
     */
    public void enableTransactionDiscountsByPercentage(boolean enableFlag, boolean doTotals)
    {
        itemProxy.enableTransactionDiscountsByPercentage(enableFlag);
        // check to update totals
        if (doTotals)
        {
            // update totals
            updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());
        }
    }

    /**
     * Sets enabled flag on customer transaction discounts by percentage to
     * specified value.
     *
     * @param enableFlag desired setting of enable flag
     * @param doTotals flag indicating totals should be updated
     */
    public void enableCustomerDiscountsByPercentage(boolean enableFlag, boolean doTotals)
    {
        itemProxy.enableCustomerDiscountsByPercentage(enableFlag);
        // check to update totals
        if (doTotals)
        {
            // update totals
            updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());
        }
    }

    /**
     * Clears transaction discounts by percentage, but does not recalculate
     * toatls.
     */
    public void clearTransactionDiscountsByPercentage()
    {
        itemProxy.clearTransactionDiscountsByPercentage();
    }

    /**
     * Retrieves array of transaction discounts by percentage.
     *
     * @return array of disc transaction discount objects, null if not found
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscountsByPercentage()
    {
        return (itemProxy.getTransactionDiscountsByPercentage());
    }

    /**
     * Sets array of transaction discounts by percentage and re-calculates the
     * totals.
     *
     * @param value array of disc transaction discount objects, null if not
     *            found
     */
    public void setTransactionDiscountsByPercentage(TransactionDiscountStrategyIfc[] value)
    {
        itemProxy.setTransactionDiscountsByPercentage(value);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Clears transaction discounts by amount.
     *
     * @param doTotals flag indicating totals should be updated
     */
    public void clearTransactionDiscountsByAmount(boolean doTotals)
    {
        itemProxy.clearTransactionDiscountsByAmount();
        if (doTotals)
        {
            // update totals
            updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());
        }
    }

    /**
     * Clears transaction discounts by amount, but does not re-calculate totals.
     */
    public void clearTransactionDiscountsByAmount()
    {
        itemProxy.clearTransactionDiscountsByAmount();
    }

    /**
     * Clears specified transaction discounts from the transaction discount
     * collection and updates the transaction totals.
     *
     * @param discountMethod int discountMethod from DiscountRuleConstantsIfc
     * @param assignmentBasis int assignmentBasis from DiscountRuleConstantsIfc
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     */
    public void clearTransactionDiscounts(int discountMethod, int assignmentBasis)
    {
        itemProxy.clearTransactionDiscounts(discountMethod, assignmentBasis);

        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Retrieves array containing specified transaction discounts.
     *
     * @param discountMethod from DiscountRuleConstantsIfc
     * @param assignmentBasis from DiscountRuleConstantsIfc
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     * @return array of disc transaction discount objects
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscounts(int discountMethod, int assignmentBasis)
    {
        return itemProxy.getTransactionDiscounts(discountMethod, assignmentBasis);
    }

    /**
     * Sets array of transaction discounts by amount and re-calculates the
     * totals.
     *
     * @param value of disc transaction discount objects, null if not found
     */
    public void setTransactionDiscountsByAmount(TransactionDiscountStrategyIfc[] value)
    {
        itemProxy.setTransactionDiscountsByAmount(value);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());

    }

    /**
     * Retrieves array of transaction discounts by amount.
     *
     * @return array of disc transaction discount objects, null if not found
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscountsByAmount()
    {
        return (itemProxy.getTransactionDiscountsByAmount());
    }

    /**
     * Retrieves array of transaction discounts.
     *
     * @return array of disc transaction discount objects, null if not found
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscounts()
    {
        return (itemProxy.getTransactionDiscounts());
    }

    /**
     * Sets array of transaction discounts and re-calculates the totals.
     *
     * @param value array of disc transaction discount objects, null if not
     *            found
     */
    public void setTransactionDiscounts(TransactionDiscountStrategyIfc[] value)
    {
        itemProxy.setTransactionDiscounts(value);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());

    }

    /**
     * Adds an array of transaction discounts and re-calculates the totals.
     *
     * @param value of disc transaction discount objects, null if not found
     */
    public void addTransactionDiscounts(TransactionDiscountStrategyIfc[] value)
    {
        itemProxy.addTransactionDiscounts(value);

        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());

    }

    /**
     * Clears transaction discounts.
     *
     * @param doTotals flag indicating totals should be updated
     */
    public void clearTransactionDiscounts(boolean doTotals)
    {
        itemProxy.clearTransactionDiscounts();
        // check to update totals
        if (doTotals)
        {
            // update totals
            updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                    .getTransactionTax());
        }
    }

    /**
     * Adds an advanced pricing rule to the transaction if it is not already
     * stored in the transaction's advanced pricing rule collection. The key is
     * the discount rule id.
     *
     * @param rule pricing rule to add
     */
    public void addAdvancedPricingRule(AdvancedPricingRuleIfc rule)
    {
        itemProxy.addAdvancedPricingRule(rule);
    }

    /**
     * Adds AdvancedPricing rules to the transaction.
     *
     * @param rules containing advancedPricingRuleIfcs
     */
    public void addAdvancedPricingRules(AdvancedPricingRuleIfc[] rules)
    {
        itemProxy.addAdvancedPricingRules(rules);
    }

    /**
     * Returns an array of the advanced pricing rules for this transaction.
     *
     * @return AdvancedPricingRuleIfc[]
     */
    public AdvancedPricingRuleIfc[] getAdvancedPricingRules()
    {
        return (itemProxy.getAdvancedPricingRules());
    }

    /**
     * Returns an iterator over the advanced pricing rules for this transaction.
     *
     * @return Iterator
     */
    public Iterator<DiscountRuleIfc> advancedPricingRules()
    {
        return itemProxy.advancedPricingRules();
    }

    /**
     * Clears the advanced pricing rules from the transaction.
     */
    public void clearAdvancedPricingRules()
    {
        itemProxy.clearAdvancedPricingRules();
    }

    /**
     * Returns a boolean indicating whether this transaction has advanced
     * pricing rules.
     *
     * @return boolean
     */
    public boolean hasAdvancedPricingRules()
    {
        return !(itemProxy.hasAdvancedPricingRules());
    }

    /**
     * Adds a best deal group to the transaction.
     *
     * @param group BestDealGroup to add
     */
    public void addBestDealGroup(BestDealGroupIfc group)
    {
        itemProxy.addBestDealGroup(group);
    }

    /**
     * Adds a best deal group to the transaction.
     *
     * @param groups list of BestDealGroups to add
     */
    public void addBestDealGroups(List<BestDealGroupIfc> groups)
    {
        itemProxy.addBestDealGroups(groups);
    }

    /**
     * Removes any advanced pricing discounts currently applied to the targets
     * within the transaction. Calling this method does not have any effect on
     * the transaction totals.
     */
    public void clearBestDealDiscounts()
    {
        itemProxy.clearBestDealDiscounts();
    }

    /**
     * Returns an array list of DiscountSourceIfcs (objects that are eligible
     * sources) for an advanced pricing rule. The sources are retrieved from the
     * line items vector.
     *
     * @return ArrayList containing sources eligible for a rule
     * @see oracle.retail.stores.domain.discount.DiscountSourceIfc
     */
    public ArrayList<DiscountSourceIfc> getDiscountSources()
    {
        return (itemProxy.getDiscountSources());
    }

    /**
     * Returns an array list of available DiscountTargetIfcs (objects that are
     * eligible targets) for an advanced pricing rule. The targets are retrieved
     * from the transaction's line items vector.
     *
     * @return ArrayList containing targets eligible for a rule
     * @see oracle.retail.stores.domain.discount.DiscountTargetIfc
     */
    public ArrayList<DiscountTargetIfc> getDiscountTargets()
    {
        return (itemProxy.getDiscountTargets());
    }

    /**
     * Retrieves size of lineItemsVector vector.
     *
     * @return line items vector size
     */
    public int getLineItemsSize()
    {
        return itemProxy.getLineItemsSize();
    }

    /**
     * Retrieves sales associate.
     *
     * @return sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (itemProxy.getSalesAssociate());
    }

    /**
     * Retrieves sales associate identifier.
     *
     * @return sales associate identifier
     */
    public String getSalesAssociateID()
    {
        return (itemProxy.getSalesAssociateID());
    }

    /**
     * Sets sales associate modified flag.
     *
     * @param value modified flag
     */
    public void setSalesAssociateModifiedFlag(boolean value)
    {
        salesAssociateModifiedFlag = value;
    }

    /**
     * Retrieves sales associate-modified flag.
     *
     * @return sales associate-modified flag
     */
    public boolean getSalesAssociateModifiedFlag()
    {
        return (salesAssociateModifiedFlag);
    }

    /**
     * Sets salesAssociate attribute.
     *
     * @param emp salesAssociate
     */
    public void setSalesAssociate(EmployeeIfc emp)
    {
        itemProxy.setSalesAssociate(emp);

    }

    /**
     * Retrieves default registry identifier.
     *
     * @return default registry identifier
     */
    public RegistryIDIfc getDefaultRegistry()
    {
        return (itemProxy.getDefaultRegistry());
    }

    /**
     * Sets defaultRegistry attribute.
     *
     * @param value default registry
     */
    public void setDefaultRegistry(RegistryIDIfc value)
    {
        itemProxy.setDefaultRegistry(value);
    }

    /**
     * Sets customer attribute and performs other operations associated with
     * assigning a customer to a transaction, such as setting discount rules.
     *
     * @param value customer
     */
    public void linkCustomer(CustomerIfc value)
    {
        // set customer reference
        setCustomer(value);
        itemProxy.linkCustomer(value);
        updateTransactionTotals(getItemContainerProxy().getLineItems(), getItemContainerProxy()
                .getTransactionDiscounts(), getItemContainerProxy().getTransactionTax());
    }

    /**
     * Returns the discount strategy used to generate the preferred customer
     * discount.
     *
     * @param includeDealDiscounts indicating whether to include customer
     *            discounts that are part of best deal
     * @return DiscountRuleIfc[] containing discounts assigned by customer
     */
    public DiscountRuleIfc[] getPreferredCustomerDiscounts(boolean includeDealDiscounts)
    {
        return itemProxy.getPreferredCustomerDiscounts(includeDealDiscounts);
    }

    /**
     * Retrieves tax object.
     *
     * @return tax object
     */
    public TransactionTaxIfc getTransactionTax()
    {
        return (itemProxy.getTransactionTax());
    }

    /**
     * Sets tax attribute.
     *
     * @param value tax
     */
    public void setTransactionTax(TransactionTaxIfc value)
    {
        itemProxy.setTransactionTax(value);
    }

    /**
     * Set tax exempt.
     *
     * @param cert tax exempt certificate identifier
     * @param reason reason code
     * @deprecated as of release 13.1 use {@link #setTaxExempt(String, LocalizedCodeIfc)} instead
     */
    public void setTaxExempt(String cert, int reason)
    {
        itemProxy.setTaxExempt(cert, reason);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Set tax exempt.
     *
     * @param cert tax exempt certificate identifier
     * @param reasonCode The LocalizedCode
     */
    public void setTaxExempt(String cert, LocalizedCodeIfc reasonCode)
    {
        itemProxy.setTaxExempt(cert, reasonCode);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Clear tax exempt.
     */
    public void clearTaxExempt()
    {
        itemProxy.clearTaxExempt();
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Set transaction to nontaxable.
     */
    @SuppressWarnings("deprecation")
    public void setNonTaxable()
    {
        // Set the transaction tax to Non Taxable
        TransactionTaxIfc tt = getTransactionTax();
        tt.setTaxExempt(null, -1);
        tt.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);

        // Set all the item taxes to Non Taxable
        AbstractTransactionLineItemIfc[] lineItems = itemProxy.getLineItems();
        for (int i = 0; i < lineItems.length; i += 1)
        {
            if (lineItems[i] instanceof SaleReturnLineItemIfc)
            {
                ((SaleReturnLineItemIfc)lineItems[i]).getItemTax().setTaxable(false);
            }
        }

        // update totals
        updateTransactionTotals(lineItems, itemProxy.getTransactionDiscounts(), tt);
    }

    /**
     * Override tax rate.
     *
     * @param newRate new tax rate
     * @param updateAllItemsFlag flag indicating all items should be updated
     * @param reason reasonCode
     * @deprecated as of 13.1 use {@link #overrideTaxRate(double, boolean, LocalizedCodeIfc)} instead
     */
    public void overrideTaxRate(double newRate, boolean updateAllItemsFlag, int reason)
    {
        itemProxy.overrideTaxRate(newRate, updateAllItemsFlag, reason);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Override tax rate.
     *
     * @param newRate new tax rate
     * @param updateAllItemsFlag flag indicating all items should be updated
     * @param reason reasonCode
     */
    public void overrideTaxRate(double newRate, boolean updateAllItemsFlag, LocalizedCodeIfc reason)
    {
        itemProxy.overrideTaxRate(newRate, updateAllItemsFlag, reason);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Override tax amount.
     *
     * @param newAmount new tax amount
     * @param updateAllItemsFlag flag indicating all items should be updated
     * @param reason reasonCode
     * @deprecated as of 13.1 use {@link #overrideTaxAmount(CurrencyIfc, boolean, LocalizedCodeIfc)} instead
     */
    public void overrideTaxAmount(CurrencyIfc newAmount, boolean updateAllItemsFlag, int reason)
    {
        itemProxy.overrideTaxAmount(newAmount, updateAllItemsFlag, reason);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Override tax amount.
     *
     * @param newAmount new tax amount
     * @param updateAllItemsFlag flag indicating all items should be updated
     * @param reason reasonCode
     */
    public void overrideTaxAmount(CurrencyIfc newAmount, boolean updateAllItemsFlag, LocalizedCodeIfc reason)
    {
        itemProxy.overrideTaxAmount(newAmount, updateAllItemsFlag, reason);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Clear tax override.
     *
     * @param updateAllItemsFlag flag indicating all items should be updated
     */
    public void clearTaxOverride(boolean updateAllItemsFlag)
    {
        itemProxy.clearTaxOverride(updateAllItemsFlag);
        // update totals
        updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Set item tax objects based on transaction tax object's value.
     *
     * @param updateAllItemsFlag flag indicating all items should be updated
     */
    public void setTransactionTaxOnItems(boolean updateAllItemsFlag)
    {
        itemProxy.setTransactionTaxOnItems(updateAllItemsFlag);
    }

    /**
     * Retrieves tender display transaction totals. In this case, these are the
     * same as the standard transaction totals.
     *
     * @return tender display transaction totals
     */
    public TransactionTotalsIfc getTenderTransactionTotals()
    {
        return (getTransactionTotals());
    }

    /**
     * Updates transaction totals and resets transaction type.
     *
     * @param lineItems array of line items
     * @param discounts array of transaction discounts
     * @param tax transaction tax object
     */
    protected void updateTransactionTotals(AbstractTransactionLineItemIfc[] lineItems,
            TransactionDiscountStrategyIfc[] discounts, TransactionTaxIfc tax)
    {
        totals.updateTransactionTotals(lineItems, discounts, tax);
        totals.updateNumberOfItemsSold(lineItems);
        resetTransactionType();
    }

    
    /**
     * Retrieves size of tenderLineItemsVector vector.
     *
     * @return tender line items vector size
     */
    public int getTenderLineItemsSize()
    {
        return tenderLineItemsVector.size();
    }

    /**
     * Retrieves OrderID descriptor string.
     *
     * @return orderID
     */
    public String getOrderID()
    {
        return orderID;
    }

    /**
     * Sets OrderID descriptor string.
     *
     * @param id Id to set
     */
    public void setOrderID(String id)
    {
        orderID = id;
    }

    /**
     * Retrieves amount of line voids (deleted lines).
     *
     * @return amount of line voids (deleted lines)
     */
    public CurrencyIfc getAmountLineVoids()
    {
        return (itemProxy.getAmountLineVoids());
    }

    /**
     * Sets amount of line voids (deleted lines).
     *
     * @param value amount of line voids (deleted lines)
     */
    public void setAmountLineVoids(CurrencyIfc value)
    {
        itemProxy.setAmountLineVoids(value);
    }

    /**
     * Adds to amount of line voids (deleted lines).
     *
     * @param value increment amount of line voids (deleted lines)
     */
    public void addAmountLineVoids(CurrencyIfc value)
    {
        itemProxy.addAmountLineVoids(value);
    }

    /**
     * Retrieves units on line voids (deleted lines).
     *
     * @return units on line voids (deleted lines)
     */
    public BigDecimal getUnitsLineVoids()
    {
        return (itemProxy.getUnitsLineVoids());
    }

    /**
     * Sets units on line voids (deleted lines).
     *
     * @param value units on line voids (deleted lines)
     */
    public void setUnitsLineVoids(BigDecimal value)
    {
        itemProxy.setUnitsLineVoids(value);
    }

    /**
     * Adds to units on line voids (deleted lines).
     *
     * @param value increment units on line voids (deleted lines)
     */
    public void addUnitsLineVoids(BigDecimal value)
    {
        itemProxy.addUnitsLineVoids(value);
    }

    /**
     * Gets the deleted line items.
     *
     * @return Vector contains the a list of deleted line items.
     */
    public Vector<SaleReturnLineItemIfc> getDeletedLineItems()
    {
        return deletedLineItems;
    }

    /**
     * Add a deleted line item into the deletedLineItems vector.
     *
     * @param lineItems contains the list of deleted line item information.
     */
    public void addDeletedLineItems(SaleReturnLineItemIfc lineItems)
    {
        deletedLineItems.addElement(lineItems);
    }

    /**
     * Get the items by a tax group.
     *
     * @return hashtable
     * @deprecated since 14.0
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public Hashtable getItemsByTaxGroup()
    {
        return itemProxy.getItemsByTaxGroup();
    }

    /**
     * Set the items by a tax group hashtable.
     *
     * @param ht hashtable
     * @deprecated since 14.0
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public void setItemsByTaxGroup(Hashtable ht)
    {
        itemProxy.setItemsByTaxGroup(ht);
    }

    /**
     * add items to the hashtable by tax group.
     * @deprecated since 14.0
     */
    @Deprecated
    public void addItemByTaxGroup()
    {
        itemProxy.addItemByTaxGroup();
    }

    /**
     * Remove an item from the tax group - item paired hashtable.
     *
     * @param item SaleReturnLineItemIfc
     * @deprecated since 14.0
     */
    @Deprecated
    public void removeItemByTaxGroup(SaleReturnLineItemIfc item)
    {
        itemProxy.removeItemByTaxGroup(item);
    }

    /**
     * Update an item in the tax group - item paired hashtable. This method will
     * be used during tax override.
     *
     * @param item SaleReturnLineItemIfc
     *
     * @deprecated since 14.0
     */
    @Deprecated
    public void updateItemByTaxGroup(SaleReturnLineItemIfc item)
    {
        itemProxy.updateItemByTaxGroup(item);
    }

    /**
     * Offer a public access of updating transaction totals.
     */
    public void updateTransactionTotals()
    {
        this.updateTransactionTotals(itemProxy.getLineItems(), itemProxy.getTransactionDiscounts(), itemProxy
                .getTransactionTax());
    }

    /**
     * Indicates whether this transaction contains one or more order line items.
     *
     * @return true if line items vector contains order line item(s).
     */
    public boolean containsOrderLineItems()
    {
        return (itemProxy.containsOrderLineItems());
    }

    /**
     * Indicates whether this transaction contains one or more return items.
     *
     * @return true if line items vector contains return line item(s).
     */
    public boolean containsReturnLineItems()
    {
        return (itemProxy.containsReturnLineItems());
    }

    /**
     * Sets item handler.
     *
     * @param value item handler
     */
    public void setItemContainerProxy(ItemContainerProxyIfc value)
    {
        itemProxy = value;
    }

    /**
     * Retrieves item handler.
     *
     * @return item handler
     */
    public ItemContainerProxyIfc getItemContainerProxy()
    {
        return (itemProxy);
    }

    /**
     * Validates to see if all store coupons in transaction can be applied
     * successfully to the current items
     * <p>
     *
     * @return true if all store coupons are applicable, false otherwise
     */
    public boolean areAllStoreCouponsApplied()
    {
        return (itemProxy.areAllStoreCouponsApplied());
    }

    /**
     * Returns list of coupons item IDs which were not applied.
     * <p>
     *
     * @return list of coupons item IDs which were not applied.
     */
    public List<String> unappliedStoreCoupons()
    {
        return (itemProxy.unappliedStoreCoupons());
    }

    /**
     * Calculates and returns the sum of any store coupon discount applied to
     * this transaction.
     *
     * @param discountScope indicating whether to calculate using item or
     *            transaction scope store coupons. See
     *            oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     * @return CurrencyIfc total
     */
    public CurrencyIfc getStoreCouponDiscountTotal(int discountScope)
    {
        return (itemProxy.getStoreCouponDiscountTotal(discountScope));
    }

    /**
     * Tests whether this transaction has discountable items.
     *
     * @return boolean
     */
    public boolean hasDiscountableItems()
    {
        return (itemProxy.hasDiscountableItems());
    }

    /**
     * Counts discountable items.
     *
     * @return number of discountable items
     */
    public int countDiscountableItems()
    {
        return (itemProxy.countDiscountableItems());
    }

    /**
     * Tests whether this transaction has one or more regular transaction
     * discounts. CustomerDiscountByPercentageIfc discounts are not included in
     * this test.
     *
     * @return boolean if one or more transaction discounts are associated with
     *         this transaction
     */
    public boolean hasTransactionDiscounts()
    {
        return (itemProxy.hasTransactionDiscounts());
    }

    /**
     * Tests whether any of the items in this transaction has a discount amount
     * greater than its selling price.
     *
     * @return boolean
     */
    public boolean itemsDiscountExceedsSellingPrice()
    {
        return (itemProxy.itemsDiscountExceedsSellingPrice());
    }

    /**
     * Tests whether any of the items in this transaction has a tax amount
     * greater than its selling price.
     *
     * @return boolean
     */
    public boolean itemsTaxExceedsSellingPrice()
    {
        return (itemProxy.itemsTaxExceedsSellingPrice());
    }

    /**
     * Calculates and returns a percentage of this transactions subtotal as a
     * currency amount.
     *
     * @param percentage to calculate
     * @return percentage of subtotal amount as CurrencyIfc
     */
    public CurrencyIfc calculateSubtotalPercentage(BigDecimal percentage)
    {
        CurrencyIfc value = null;
        if (percentage != null)
        {
            value = totals.getSubtotal().multiply(percentage);
        }
        return value;
    }

    /**
     * Tests whether the transaction discounts exceed the amount passed in as a
     * parameter.
     *
     * @param amount to test
     * @return boolean
     */
    public boolean transactionDiscountsExceed(CurrencyIfc amount)
    {
        CurrencyIfc subtotal = totals.getSaleSubtotal();
        CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
        boolean discountExceed = false;

        // check for percent discounts
        TransactionDiscountStrategyIfc[] percentDiscounts = getTransactionDiscounts(
                DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE, DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        if (percentDiscounts.length > 0)
        {
            // calculate % discount amount after subtracting $ discount amount
            // from subtototal
            CurrencyIfc percentDiscountAmount = subtotal.multiply(percentDiscounts[0].getDiscountRate());

            // add the % discount amount to the $ discount amount
            discountAmount = percentDiscountAmount;

        }

        // get transaction discounts
        TransactionDiscountStrategyIfc[] amountDiscounts = getTransactionDiscounts(
                DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT, DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL);
        if (amountDiscounts.length > 0)
        {
            // get transaction discount amount
            discountAmount = discountAmount.add(amountDiscounts[0].getDiscountAmount());
        }

        // if total discount amount is greater than amount, return true
        if (discountAmount.compareTo(amount.abs()) == CurrencyIfc.GREATER_THAN)
        {
            discountExceed = true;
        }
        return (discountExceed);
    }

    /**
     * Tests whether the transaction discounts exceed the amount passed in as a
     * parameter.
     *
     * @param amount to test
     * @return boolean
     */
    public boolean transactionEmployeeDiscountsExceed(CurrencyIfc amount)
    {
        CurrencyIfc subtotal = totals.getSaleSubtotal();
        CurrencyIfc discountAmount = DomainGateway.getBaseCurrencyInstance();
        boolean discountExceed = false;

        // check for percent discounts
        TransactionDiscountStrategyIfc[] percentDiscounts = getTransactionDiscounts(
                DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE, DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
        if (percentDiscounts.length > 0)
        {
            // calculate % discount amount after subtracting $ discount amount
            // from subtototal
            CurrencyIfc percentDiscountAmount = subtotal.multiply(percentDiscounts[0].getDiscountRate());

            // add the % discount amount to the $ discount amount
            discountAmount = percentDiscountAmount;
        }

        // get transaction discounts
        TransactionDiscountStrategyIfc[] amountDiscounts = getTransactionDiscounts(
                DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT, DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE);
        if (amountDiscounts.length > 0)
        {
            // get transaction discount amount
            discountAmount = discountAmount.add(amountDiscounts[0].getDiscountAmount());

        }

        // if total discount amount is greater than amount, return true
        if (discountAmount.compareTo(amount.abs()) == CurrencyIfc.GREATER_THAN)
        {
            discountExceed = true;
        }
        return (discountExceed);
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        StringBuilder strResult = Util.classToStringHeader("SaleReturnTransaction", null, hashCode());
        strResult.append(super.toString()).append(
            Util.formatToStringEntry("Item handler", getItemContainerProxy())).append(
            Util.formatToStringEntry("Order ID", orderID));
        strResult.append(Util.formatToStringEntry("SendCustomerLinked", sendCustomerLinked));
        strResult.append(Util.formatToStringEntry("CustomerPresentChecked", checkedCustomerPresent));
        strResult.append(Util.formatToStringEntry("CustomerPhysicallyPresent", customerPhysicallyPresent));
        strResult.append(Util.formatToStringEntry("Total number of sends", getItemSendPackagesCount()));
        // pass back result
        return (strResult.toString());
    }

    /**
     * Returns default journal string.
     *
     * @return default journal string
     * @deprecated as of 13.1 use {@link #toJournalString(Locale)} instead
     */
    public String toJournalString()
    {
        return (toJournalString(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
    }

    /**
     * Returns default journal string.
     *
     * @param journalLocale locale received from the client.
     * @return default journal string
     */
    public String toJournalString(Locale journalLocale)
    {
        StringBuffer strResult = new StringBuffer();
        // start with Transaction journal data
        strResult.append(journalHeader(journalLocale));

        // journal transaction modifiers
        strResult.append(itemProxy.journalTransactionModifiers(getCustomer(), journalLocale));

        // journal line items
        strResult.append(itemProxy.journalLineItems(journalLocale));

        // tender line items not journaled here (yet)

        // pass back result
        return (strResult.toString());
    }

    /**
     * Write journal header to specified string buffer.
     *
     * @param journalLocale locale received from the client
     * @return journal fragment string
     */
    public String journalHeader(Locale journalLocale)
    {
        StringBuffer strResult = new StringBuffer();
        strResult.append(super.toJournalString(journalLocale));

        // The below logic assumes that an EOL was the
        // last string append to, in the above toJournalString()
        // method.
        int index = strResult.lastIndexOf(Util.EOL);
        if (index > 0 && index < strResult.length())
            strResult.delete(index, strResult.length());

        // pass back result
        return (strResult.toString());
    }

    /**
     * Write line items to journal string.
     *
     * @param journalLocale locale received from the client
     * @return journal fragment string
     */
    public String journalLineItems(Locale journalLocale)
    {
        return (itemProxy.journalLineItems(journalLocale));
    }

    /**
     * Write transaction modifiers to journal string.
     *
     * @param journalLocale Client's journal locale.
     * @return journal fragment string
     */
    public String journalTransactionModifiers(Locale journalLocale)
    {
        return (itemProxy.journalTransactionModifiers((CustomerIfc)null, journalLocale));
    }

    /**
     * Write transaction modifiers to journal string.
     *
     * @param customer CustomerIfc reference
     * @return journal fragment string
     */
    public String journalTransactionModifiers(CustomerIfc customer, Locale journalLocale)
    {
        return (itemProxy.journalTransactionModifiers(customer, journalLocale));
    }

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    public boolean equals(Object obj)
    {
        boolean equal = false;

        // If it's a SaleReturnTransaction, compare its attributes
        if (obj instanceof SaleReturnTransaction)
        {
            // downcast the input object
            SaleReturnTransaction c = (SaleReturnTransaction)obj;
            if (!super.equals(obj))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getItemContainerProxy(), c.getItemContainerProxy()))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getInstantCredit(), c.getInstantCredit()))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getReturnTenderDataContainer(), c.getReturnTenderDataContainer()))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getTransactionTax(), c.getTransactionTax()))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getDeletedLineItems(), c.getDeletedLineItems()))
            {
                equal = false;
            }
            else if (isTransactionGiftReceiptAssigned() != c.isTransactionGiftReceiptAssigned())
            {
                equal = false;
            }
            else if (checkedCustomerPresent() != c.checkedCustomerPresent())
            {
                equal = false;
            }
            else if (isSendCustomerLinked() != c.isSendCustomerLinked())
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getEmployeeDiscountID(), c.getEmployeeDiscountID()))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getOrderID(), c.getOrderID()))
            {
                equal = false;
            }
            else if (isReentryMode() != c.isReentryMode())
            {
                equal = false;
            }
            else if (getSendPackageCount() != c.getSendPackageCount())
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getAgeRestrictedDOB(), c.getAgeRestrictedDOB()))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getExternalOrderID(), c.getExternalOrderID()))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getExternalOrderNumber(), c.getExternalOrderNumber()))
            {
                equal = false;
            }
            else if (requireServiceContract() != c.requireServiceContract())
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(getLegalDocuments(), c.getLegalDocuments()))
            {
                equal = false;
            }
            else if (transactionLevelSendAssigned != c.transactionLevelSendAssigned)
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(sendPackages, c.sendPackages))
            {
                equal = false;
            }    
              
            else if (!Util.isObjectEqual(currencyType, c.currencyType))
            {
                equal = false;
            }  
            else if (!Util.isObjectEqual(transactionCountryCode, c.transactionCountryCode))
            {
                equal = false;
            }  
            
            else
            {
                equal = true;
            }
        }

        return (equal);
    }

    /**
     * Returns true if the transaction is an exchage; false otherwise. The
     * transaction type of exchange is no longer used. This is used in cases
     * where we need to know an exchange took place, although the transaction is
     * typed as a sale or a return.
     *
     * @return true if the transaction is an exchange; false otherwise.
     */
    public boolean isExchange()
    {
        boolean exchangeFlag = false;
        if (transactionType == TransactionIfc.TYPE_SALE || transactionType == TransactionIfc.TYPE_RETURN)
        {
            AbstractTransactionLineItemIfc[] items = getLineItems();
            for (int i = 0; i < items.length; i++)
            {
                if (items[i] instanceof SaleReturnLineItemIfc)
                {
                    boolean returnFlag = ((SaleReturnLineItemIfc)items[i]).isReturnLineItem();
                    // if this is a sale with a return item, it's an exchange
                    if ((transactionType == TransactionIfc.TYPE_SALE && returnFlag)
                            || (transactionType == TransactionIfc.TYPE_RETURN && !returnFlag))
                    {
                        // set flag and exit
                        exchangeFlag = true;
                        i = items.length;
                    }
                }
            }
        }
        return (exchangeFlag);

    }

    /**
     * Returns true if the transaction has return items.
     *
     * @return true if the transaction has return items, false otherwise.
     */
    public boolean hasReturnItems()
    {
        boolean hasReturnItems = false;
        if (transactionType == TransactionIfc.TYPE_RETURN || isExchange())
        {
            hasReturnItems = true;
        }
        return (hasReturnItems);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#isReturn()
     */
    public boolean isReturn()
    {
        return (TransactionConstantsIfc.TYPE_RETURN == getTransactionType());
    }

    /**
     * Method to determine if any of the items in the transaction contains a
     * specific serial number.
     *
     * @param serialNumber to check
     * @return boolean - true if serial number found
     */
    public boolean containsSerialNumber(String serialNumber)
    {
        return (itemProxy.containsSerialNumber(serialNumber));
    }

    /**
     * Get the employee Discount id String
     *
     * @return employeeDiscountID String
     */
    public String getEmployeeDiscountID()
    {
        return employeeDiscountID;
    }

    /**
     * Setst the empployee discount id string
     *
     * @param employeeDiscountID String
     */
    public void setEmployeeDiscountID(String employeeDiscountID)
    {
        this.employeeDiscountID = employeeDiscountID;
    }

    /**
     * Return an instant credit object associated with this transaction. This
     * value is populated if a customer has applied for a houseCard.
     *
     * @return instantCredit credit card object
     * @see oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc#getInstantCredit()
     */
    public InstantCreditIfc getInstantCredit()
    {
        return this.instantCredit;
    }

    /**
     * Set the instant credit object. This is set when a customer has applied
     * for a house card.
     *
     * @param instantCredit credit card
     * @see oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc#setInstantCredit(oracle.retail.stores.domain.utility.InstantCreditIfc)
     */
    public void setInstantCredit(InstantCreditIfc instantCredit)
    {
        this.instantCredit = instantCredit;
    }

    /**
     * Sets the return tender data elements. These will be set into the
     * ReturnTenderDataContainer.
     *
     * @param dataElements return tender data elements to set
     */
    public void setReturnTenderElements(ReturnTenderDataElementIfc[] dataElements)
    {
        getReturnTenderDataContainer().setTenderElements(dataElements);
    }

    /**
     * Gets the return tender data elements. This is a convienience method and
     * actually retrieves the elements from ReturnTenderDataContainer.
     *
     * @return tenderElements in the returnTenderDataContainer
     */
    public ReturnTenderDataElementIfc[] getReturnTenderElements()
    {
        return getReturnTenderDataContainer().getTenderElements();
    }

    /**
     * Appends the return tender data elements. These will be added into the
     * ReturnTenderDataContainer.
     *
     * @param dataElements to append to the list of existing tender elements
     */
    public void appendReturnTenderElements(ReturnTenderDataElementIfc[] dataElements)
    {
        getReturnTenderDataContainer().addTenderElements(dataElements);
    }

    /**
     * Create the return tender data container (if not already created)
     *
     * @return the return tender data container
     */
    public ReturnTenderDataContainerIfc getReturnTenderDataContainer()
    {
        if (returnTendersContainer == null)
            returnTendersContainer = DomainGateway.getFactory().getReturnTenderDataContainerInstance();
        return returnTendersContainer;
    }

    /**
     * Buffers journal string for a transaction level discount.
     *
     * @param discount - transcation discount strategy
     * @param message - journal StringBuffer
     * @deprecated as of 13.1 use {@link #journalTranLevelDiscount(TransactionDiscountStrategyIfc, StringBuffer, Locale)} instead
     */
    public static void journalTranLevelDiscount(TransactionDiscountStrategyIfc discount, StringBuffer message)
    {
        int AVAIL_DISCOUNT_LENGTH = 23;

        if (discount != null && message != null)
        {
            if (discount instanceof TransactionDiscountByAmountIfc)
            {
                CurrencyIfc amt = ((TransactionDiscountByAmountIfc)discount).getDiscountAmount();
                String amtStr = amt.toFormattedString().trim();
                message
                        .append(Util.EOL)
                        .append("TRANS: Discount")
                        .append(Util.SPACES.substring(amtStr.length(), AVAIL_DISCOUNT_LENGTH))
                        .append(amtStr)
                        .append(Util.EOL)
                        .append("  Discount: Amt. Deleted")
                        .append(Util.EOL)
                        .append("  Disc. Rsn.: ")
                        .append(
                                (discount.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)) == null) ? discount
                                        .getReason().getCode()
                                        : discount.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
            }
            else if (discount instanceof TransactionDiscountByPercentageIfc)
            {
                BigDecimal rate = discount.getDiscountRate();
                rate = rate.movePointRight(2);
                rate = rate.setScale(0, BigDecimal.ROUND_HALF_UP);
                String rateStr = rate.toString();

                message
                        .append(Util.EOL)
                        .append("TRANS: Discount")
                        .append(Util.EOL)
                        .append("  Discount: ")
                        .append(rateStr)
                        .append("% Deleted")
                        .append(Util.EOL)
                        .append("  Disc. Rsn.: ")
                        .append(
                                (discount.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)) == null) ? discount
                                        .getReason().getCode()
                                        : discount.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
            }
        }
    }

    /**
     * Buffers journal string for a transaction level discount.
     *
     * @param discount - transcation discount strategy
     * @param message - journal StringBuffer
     * @param journalLocale locale received from the client
     */
    public static void journalTranLevelDiscount(TransactionDiscountStrategyIfc discount, StringBuffer message,
            Locale journalLocale)
    {

        if (discount != null && message != null)
        {
            if (discount instanceof TransactionDiscountByAmountIfc)
            {
                CurrencyIfc amt = ((TransactionDiscountByAmountIfc)discount).getDiscountAmount();
                String amtStr = amt.toFormattedString().trim();
                Object[] dataArgs = new Object[] { amtStr };
                message
                .append(Util.EOL)
                .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.TRANS_DISCOUNT_TAG_LABEL, null,
                        journalLocale))
                        .append(Util.EOL)
                        .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.DISCOUNT_AMOUNT_DELETED_LABEL, dataArgs,
                                journalLocale))
                        .append(Util.EOL);

                        String reasonCodeText = (discount.getReason().getText(journalLocale) == null)
                        ? discount.getReason().getCode(): discount.getReason().getText(journalLocale);
                        dataArgs[0] = reasonCodeText;

                        message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, dataArgs,
                                journalLocale));
            }
            else if (discount instanceof TransactionDiscountByPercentageIfc)
            {
                BigDecimal rate = discount.getDiscountRate();
                rate = rate.movePointRight(2);
                rate = rate.setScale(0, BigDecimal.ROUND_HALF_UP);
                String rateStr = rate.toString();
                Object[] dataArgs = new Object[]{rateStr};
                message
                    .append(Util.EOL)
                    .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.TRANS_DISCOUNT_TAG_LABEL, null,
                            journalLocale))
                    .append(Util.EOL)
                    .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DISCOUNT_LABEL, dataArgs,
                            journalLocale))
                    .append(JournalConstantsIfc.PERCENTILE_SYMBOL)
                    .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DELETED_LABEL, null,
                            journalLocale))
                    .append(Util.EOL);
                dataArgs[0] =  (discount.getReason().getText(journalLocale) == null)
                                    ? discount.getReason().getCode(): discount.getReason().getText();
                    message.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, dataArgs,
                            journalLocale));

            }
        }
    }

    /**
     * @return Returns the reentryMode.
     */
    public boolean isReentryMode()
    {
        return reentryMode;
    }

    /**
     * @param reentryMode The reentryMode to set.
     */
    public void setReentryMode(boolean reentryMode)
    {
        this.reentryMode = reentryMode;
    }

    /**
     * @return Returns the checkedCustomerPresent.
     */
    public boolean checkedCustomerPresent()
    {
        if (hasExternalOrder())
        {
            //transactions containing an External Order
            //will always have customer physicallyPresent
            //so there is no reason to check
            return true;
        }
        else
        {
            return checkedCustomerPresent;
        }
    }

    /**
     * @param checkedCustomerPresent The checkedCustomerPresent to set.
     */
    public void setCheckedCustomerPresent(boolean checkedCustomerPresent)
    {
        this.checkedCustomerPresent = checkedCustomerPresent;
    }

    /**
     * Get send customer is linked customer or captured customer. It is used for
     * journal and print receipt
     *
     * @param boolean sendCustomerLinked
     */
    public boolean isSendCustomerLinked()
    {
        return sendCustomerLinked;
    }

    /**
     * Set send customer. It can be linked customer or captured customer. It is
     * used for journal and print receipt
     *
     * @param boolean sendCustomerLinked
     */
    public void setSendCustomerLinked(boolean value)
    {
        sendCustomerLinked = value;
    }

    /**
     * get all the send items based on send index from the transaction
     *
     * @param int sendIndex
     * @return SaleReturnLineItemsIfc send line items
     */
    public SaleReturnLineItemIfc[] getSendItemBasedOnIndex(int sendIndex)
    {
        // some items from this send have been deleted.
        // get the number of item from the send.
        SaleReturnLineItemIfc[] sendItems = null;
        int count = 0; // item count in the send
        SaleReturnLineItemIfc items[] = (SaleReturnLineItemIfc[])getLineItems();
        for (int i = 0; i < items.length; i++)
        {
            if (items[i].getSendLabelCount() == sendIndex)
            {
                count++;
            }
        }
        // get the items in the send
        if (count > 0)
        {
            sendItems = new SaleReturnLineItemIfc[count];
            int index = -1;
            for (int i = 0; i < items.length; i++)
            {
                if (items[i].getSendLabelCount() == sendIndex)
                {
                    sendItems[++index] = items[i];
                }
            }
        }
        return sendItems;
    }

    /**
     * get send index based on selected line item index
     *
     * @param int[] all selected line item index
     * @return int send index
     */
    public int getSendIndexFromSelectedItems(int[] allSelected)
    {
        int sendIndex = 0;
        for (int i = 0; i < allSelected.length; i++)
        {
            int index = allSelected[i];
            // get send index
            sendIndex = ((SaleReturnLineItemIfc)retrieveItemByIndex(index)).getSendLabelCount();
            if (sendIndex > 0)
            {
                // there are send items in the delete
                break;
            }
        }
        return sendIndex;
    }

    /**
     * check if the all the send items are deleted
     *
     * @param int[] selected line item index for delete
     * @param int send index
     * @return boolean flag to indicate all of them get deleted or not
     */
    public boolean isAllItemInSendDeleted(int[] allSelected, int sendIndex)
    {
        boolean isAllItemInSendDeleted = true;
        int countInTheSend = 0;
        // check there is other item in the same send
        SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])getLineItems();
        for (int i = 0; i < lineItems.length; i++)
        {
            if (lineItems[i].getSendLabelCount() == sendIndex && !lineItems[i].isShippingCharge())
            {
                countInTheSend++;
            }
        }
        int sendCountInDelete = 0;
        for (int i = 0; i < allSelected.length; i++)
        {
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)retrieveItemByIndex(allSelected[i]);
            if (item.getSendLabelCount() == sendIndex && !item.isShippingCharge())
            {
                sendCountInDelete++;
            }
        }
        if (countInTheSend > sendCountInDelete)
        {
            isAllItemInSendDeleted = false;
        }
        return isAllItemInSendDeleted;
    }

    /**
     * Checks transactionGiftReceiptAssigned to see if a transaction wide gift
     * receipt has been assigned
     *
     * @return boolean transactionGiftReceiptAssigned
     */
    public boolean isTransactionGiftReceiptAssigned()
    {
        return transactionGiftReceiptAssigned;
    }

    /**
     * Set transactionGiftReceiptAssigned. It is set to true when a gift receipt
     * was added to the entire transaction
     *
     * @param boolean value
     */
    public void setTransactionGiftReceiptAssigned(boolean value)
    {
        transactionGiftReceiptAssigned = value;
    }

    /**
     * Set sendPackageCount.
     *
     * @param int count
     */
    public void setSendPackageCount(int count)
    {
        sendPackageCount = count;
    }

    /**
     * Retrieve sendPackageCount.
     *
     * @return the send package count
     */
    public int getSendPackageCount()
    {
        return sendPackageCount;
    }

    /**
     * Indicates customer is physically present
     *
     * @return boolean true if customer is physically present
     */
    public boolean isCustomerPhysicallyPresent()
    {
        if (hasExternalOrder())
        {
            //transactions containing an External Order
            //will always have customer physicallyPresent
            return true;
        }
        else
        {
            return customerPhysicallyPresent;
        }
    }

    /**
     * Checks if customer is physically present
     *
     * @param customerPhysicallyPresent The customerPhysicallyPresent to set.
     */
    public void setCustomerPhysicallyPresent(boolean customerPhysicallyPresent)
    {
        this.customerPhysicallyPresent = customerPhysicallyPresent;
    }

    /**
     * Set the tax rules for transaction level send
     *
     * @param taxRulesVO
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#setSendTaxRules(oracle.retail.stores.domain.tax.TaxRulesVO)
     */
    public void setSendTaxRules(TaxRulesVO taxRulesVO)
    {
        this.taxRulesVO = taxRulesVO;
    }

    /**
     * Get the tax rules for transaction level send
     *
     * @return taxRulesVO
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#getSendTaxRules()
     */
    public TaxRulesVO getSendTaxRules()
    {
        return this.taxRulesVO;
    }

    /**
     * Check to see if the transaction is taxable. A transaction is taxable is
     * assumed taxable, unless every item in the transaction is a non-taxable
     * item.
     *
     * @return true or false
     * @since 7.0
     */
    public boolean isTaxableTransaction()
    {
        boolean taxableItemFound = false;
        boolean nonTaxableItemFound = false;

        Vector<AbstractTransactionLineItemIfc> lineItems = this.getLineItemsVector();
        for (int i = 0; i < lineItems.size(); i++)
        {
            if (lineItems.elementAt(i) instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItems.elementAt(i);
                if (srli.getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE
                        || srli.getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT
                        || srli.getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF)
                {
                    nonTaxableItemFound = true;
                }
                else
                {
                    taxableItemFound = true;
                }
            }
        }

        boolean taxable = true; // default
        if (!taxableItemFound && nonTaxableItemFound)
        {
            taxable = false;
        }
        return taxable;
    }

    /**
     * Get the age restriction dob entered by the associate.
     *
     * @return Returns the ageRestrictedDOB.
     */
    public EYSDate getAgeRestrictedDOB()
    {
        return ageRestrictedDOB;
    }

    /**
     * Sets the age restriction dob entered by the associate.
     *
     * @param ageRestrictedDOB The ageRestrictedDOB to set.
     */
    public void setAgeRestrictedDOB(EYSDate ageRestrictedDOB)
    {
        this.ageRestrictedDOB = ageRestrictedDOB;
    }

    /**
     * For each new send adds shipping-to customer and shipping method used
     *
     * @param shippingMethodUsed shipping method used
     * @param shippingToCustomer shipping to customer
     */
    public SendPackageLineItemIfc addSendPackageInfo(ShippingMethodIfc shippingMethodUsed, CustomerIfc shippingToCustomer)
    {
        SendPackageLineItem sendPackage = createSendPackageLineItemInstance(shippingMethodUsed, shippingToCustomer);
        addSendPackage(sendPackage);

        return sendPackage;
    }

    /**
     * Update an existing send shipping-to customer and shipping method used
     *
     * @param shippingMethodUsed shipping method used
     * @param shippingToCustomer shipping to customer
     */
    public void updateSendPackageInfo(int index, ShippingMethodIfc shippingMethodUsed, CustomerIfc shippingToCustomer)
    {
        SendPackageLineItem sendPackage = createSendPackageLineItemInstance(shippingMethodUsed, shippingToCustomer);
        sendPackages.set(index, sendPackage);
    }

    /**
     * Creates an instance of a SendPackageLineItem based on the shipping method
     * and shipping to customer parameters.
     *
     * @param shippingMethodUsed shipping method used
     * @param shippingToCustomer shipping to customer
     * @return SendPackageLineItem
     */
    protected SendPackageLineItem createSendPackageLineItemInstance(ShippingMethodIfc shippingMethodUsed,
            CustomerIfc shippingToCustomer)
    {
        SendPackageLineItem sendPackageLineItem = new SendPackageLineItem();

        sendPackageLineItem.initialize(shippingMethodUsed, shippingToCustomer);        

        return sendPackageLineItem;
    }

    /**
     * Remove the send package identified by its 1-based index.
     * 
     * @param packageNumber the number of the package to remove
     */
    public void removeSendPackage(int packageNumber)
    {
        sendPackages.remove(packageNumber - 1);
    }

    /**
     * @return an array of all the send package line items
     */
    public SendPackageLineItemIfc[] getSendPackages()
    {
        if (sendPackages != null && sendPackages.size() > 0)
            return sendPackages.toArray(new SendPackageLineItem[sendPackages.size()]);
        else
            return null;
    }

    public String getReturnTicketID()
    {
        if (returnTicketID != null)
        {
            return returnTicketID;
        }

        return "";
    }

    public void setReturnTicketID(String returnTktID)
    {
        this.returnTicketID = returnTktID;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#getExternalOrderID()
     */
    @Override
    public String getExternalOrderID()
    {
        return externalOrderID;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#setExternalOrderID(java.lang.String)
     */
    @Override
    public void setExternalOrderID(String id)
    {
        this.externalOrderID = id;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#getExternalOrderNumber()
     */
    @Override
    public String getExternalOrderNumber()
    {
        return externalOrderNumber;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#setExternalOrderNumber(java.lang.String)
     */
    @Override
    public void setExternalOrderNumber(String number)
    {
        this.externalOrderNumber = number;
    }

    /**
     * @return a boolean flag indicating if the transaction contains an external
     *         order
     */
    public boolean hasExternalOrder()
    {
        return !StringUtils.isBlank(getExternalOrderID());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#isWebManagedOrder()
     */
    @Override
    public boolean isWebManagedOrder()
    {
        return ExternalOrderConstantsIfc.TYPE_ATG == getExternalOrderType();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#getExternalOrderType()
     */
    @Override
    public int getExternalOrderType()
    {
        return externalOrderType;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc#setExternalOrderType(int)
     */
    @Override
    public void setExternalOrderType(int externalOrderType)
    {
        this.externalOrderType = externalOrderType;
    }

    /**
     * returns a boolean to indicate if contract signature was captured
     */
    public boolean isContractSignatureCaptured()
    {

        boolean signaturedCaptured = false;

        Iterator<LegalDocumentIfc> it = legalDocuments.iterator();
        while (it.hasNext())
        {
            LegalDocumentIfc legalDocument = (LegalDocumentIfc)it.next();
            if ((legalDocument != null) && legalDocument.getSignature() != null)
            {
                signaturedCaptured = true;
                break;
            }

        }
        return signaturedCaptured;
    }

    /**
     * @return a boolean flag indicating if the transation
     * requires the signing of a service contract
     */
    public boolean requireServiceContract()
    {
        return requireServiceContractFlag;
    }

    /**
     * Set the flag indicating if the transaction requires the
     * signing of a service contract
     * @param flag the service contract flag
     */
    public void setRequireServiceContractFlag(boolean flag)
    {
        this.requireServiceContractFlag = flag;
    }

    /**
     * @return the legalDocument
     */
    public List<LegalDocumentIfc> getLegalDocuments()
    {
        return legalDocuments;
    }

    /**
     * @param legalDocument the legalDocument to set
     */
    public void addLegalDocument(LegalDocumentIfc legalDocument)
    {
        legalDocuments.add(legalDocument);
    }

    /**
     * @param legalDocuments the legalDocuments to set
     */
    public void setLegalDocuments(List<LegalDocumentIfc> legalDocuments)
    {
        this.legalDocuments = legalDocuments;
    }

    /**
     * LegalDocument
     */
    public LegalDocumentIfc getLegalDocument(int index)
    {
        return legalDocuments.get(index);
    }

    /**
     * Flag to indicate if the transaction has external send package
     */
    public boolean hasExternalSendPackage()
    {
        boolean externalSendPackage = false;
        SendPackageLineItemIfc[] sendPackage = this.getSendPackages();
        if(sendPackage != null && sendPackage.length > 0)
        {
            for (int i = 0; i < sendPackage.length; i++)
            {
                if(sendPackage[i].isExternalSend())
                {
                    externalSendPackage = true;
                    break;
                }
            }
        }

        return externalSendPackage;
    }

    /**
     * Get external order sale return line items
     * @param externalOrderItemIDs the external order item IDs
     * @return the list of line items
     */
    public List<SaleReturnLineItemIfc> getExternalOrderLineItems(List<String> externalOrderItemIDs)
    {
        return itemProxy.getExternalOrderLineItems(externalOrderItemIDs);
    }
    

    
    /**
     * Sets Transaction Level Send. This implies that the Send Level being used
     * is Transaction, not Item.
     *
     * @param transactionLevelSendAssigned true means transaction level send
     */
    public void setTransactionLevelSendAssigned(boolean transactionLevelSendAssigned)
    {
        this.transactionLevelSendAssigned = transactionLevelSendAssigned;
    }

    /**
     * Checks for Transaction or Item Level Send. Transaction Level send gets
     * shipping method info after clicking tender. Multiple sends are not
     * allowed.
     *
     * @return boolean true means transaction level send
     */
    public boolean isTransactionLevelSendAssigned()
    {
        boolean assigned = false;
        if (getItemSendPackagesCount() == 1 && this.transactionLevelSendAssigned)
        {
            assigned = true;
        }
        return assigned;
    }    
    
    /**
     * Gets the total send packages count. If value > 0 is returned, it implies
     * that sends (one or more) have taken place. The Value returned is as per
     * the total number of sends. Item send packages count implies send packages
     * that has/should have items in it. This should not be confused with send
     * level, as it is used for both item level and transaction level send
     *
     * @return int item send packages count
     */
    public int getItemSendPackagesCount()
    {
        return sendPackages.size();
    }
    


    /**
     * For each new send adds a send package line item containing shipping-to
     * customer and shipping method used
     *
     * @param sendPackage send package line item
     */
    public void addSendPackage(SendPackageLineItemIfc sendPackageUsed)
    {
        sendPackages.add(sendPackageUsed);
        sendPackageUsed.setPackageNumber(sendPackages.size());
    }

    /**
     * Gets send packages for all the sends. Beware this returns the actual
     * vector. Refrain from manipulating it directly.
     *
     * @return Vector send packages for all the sends
     */
    public Vector<SendPackageLineItemIfc> getSendPackageVector()
    {
        return sendPackages;
    }
    


    /**
     * Get send package at the requested index. This is zero-based, which would
     * be the package's number minus one.
     *
     * @return package at the requested index.
     */
    public SendPackageLineItemIfc getSendPackage(int index)
    {
        SendPackageLineItemIfc sendPackage = sendPackages.get(index);
        sendPackage.setPackageNumber(index + 1);
        return sendPackage;
    }    
    


    /**
     * Remove the shipping charge line item from the transaction since all the send items for this shipping charge 
     * are removed.
     * @param transaction
     * @param sendIndex
     * @return
     */
    public SaleReturnLineItemIfc getShippingChargeLineItem(int sendIndex)
    {
        SaleReturnLineItemIfc shippingChargeLineItem = null;
        AbstractTransactionLineItemIfc lineItems[] = getLineItems();
        for (int i = 0; i < lineItems.length; i++)
        {
            if ( lineItems[i] instanceof SaleReturnLineItemIfc )
            {
                SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[i];
                if ( lineItem.isShippingCharge() && lineItem.getSendLabelCount() == sendIndex )
                {
                    return shippingChargeLineItem = lineItem;
                }
            }
        }
        return shippingChargeLineItem;
    }    
    
    /**
     * @return a boolean flag indicating if the transaction contains cross
     * channel order line item.
     */
    @Override
    public boolean containsXChannelOrderLineItem()
    {
        boolean result = false;

        for (AbstractTransactionLineItemIfc lineItem : getLineItems())
        {
            OrderItemStatusIfc orderItemStatus = ((SaleReturnLineItemIfc)lineItem).getOrderItemStatus();
            if ((orderItemStatus != null) && orderItemStatus.isCrossChannelItem())
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * @return a boolean flag indicating if the transaction contains cross
     * channel order line item only.
     */
    public boolean containsXChannelOrderLineItemOnly()
    {
        boolean result = getLineItems().length > 0;  
        for (AbstractTransactionLineItemIfc lineItem : getLineItems())
        {
            OrderItemStatusIfc orderItemStatus = ((SaleReturnLineItemIfc)lineItem).getOrderItemStatus();
            if ((orderItemStatus == null) || !orderItemStatus.isCrossChannelItem())
            {
                result = false;
                break;
            }
        }
        return result;
    }
    
    /*
     * Return the cross channel order grand total
     */
    public CurrencyIfc getXChannelGrandTotal()
    {
        CurrencyIfc grandTotal = DomainGateway.getBaseCurrencyInstance();
        for (AbstractTransactionLineItemIfc li : getLineItems())
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)li;
            OrderItemStatusIfc orderItemStatus = srli.getOrderItemStatus();
            if ((orderItemStatus != null) && orderItemStatus.isCrossChannelItem() && !srli.isKitHeader())
            {
                grandTotal = grandTotal.add(srli.getItemPrice().getItemTotal());
            }
        }

        return grandTotal; 
    }   

    /** Returns the transaction country code.
    *
    *  @return String
    */
    public String getTransactionCountryCode()
    {
        return transactionCountryCode;
    }

    /** Sets the ISO country code for this transaction.
    *
    *  @param String transactionCountryCode
    */
    public void setTransactionCountryCode(String transactionCountryCode)
    {
        this.transactionCountryCode = transactionCountryCode;
    }   
    
    /** Returns the transaction currency type.
    *
    *  @return CurrencyTypeIfc
    */
    public CurrencyTypeIfc getCurrencyType()
    {
        return currencyType;
    }

    /** Sets the currency type for this transaction.
    *
    *  @param CurrencyTypeIfc currencyType
    */
    public void setCurrencyType(CurrencyTypeIfc currencyType)
    {
        this.currencyType = currencyType;
    }

    /**
     * return true if the transaction is cross channel order pickup or order cancel. Otherwise, return false
     * @return
     */
    public boolean isOrderPickupOrCancel()
    {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.transaction.RetailTransactionIfc#getRecommendedItems(int)
     */
    @Override
    public List<ExtendedItemData> getRecommendedItems(int maxRecommendedItemsListSize)
    {
        List<ExtendedItemData> recommendedItems = new ArrayList<ExtendedItemData>();
        AbstractTransactionLineItemIfc[] lineItems = getLineItemsExceptExclusions();

        for(int i = lineItems.length - 1; i >= 0; i--)
        {
            AbstractTransactionLineItemIfc item = lineItems[i];
            if (item instanceof SaleReturnLineItemIfc &&
                            (((SaleReturnLineItemIfc)item).getPLUItem().getExtendedItemDataContainer() != null &&
                            ((SaleReturnLineItemIfc)item).getPLUItem().getExtendedItemDataContainer().getRecommendedItems() != null))
            {
                // Iterate through the list of recommended items add the unique ones to the recommended list.
                for(ExtendedItemData recommendedItem: ((SaleReturnLineItemIfc)item).getPLUItem().getExtendedItemDataContainer().getRecommendedItems())
                {
                    if (!recommendedItems.contains(recommendedItem))
                    {
                        recommendedItems.add(recommendedItem);
                    }
                    if(recommendedItems.size() >= maxRecommendedItemsListSize)
                    {
                        break;
                    }
                }
            }
            if(recommendedItems.size() >= maxRecommendedItemsListSize)
            {
                break;
            }
        }

        return recommendedItems;
    }

}
