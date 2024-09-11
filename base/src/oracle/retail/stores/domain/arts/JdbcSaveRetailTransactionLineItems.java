/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcSaveRetailTransactionLineItems.java /main/107 2014/07/24 15:23:29 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/19/14 - Missing transactionStatus for orderTransaction when
 *                         saving return line item.
 *    yiqzhao   11/12/14 - Add a column price entry required flag(FL_ENTR_PRC_RQ)
 *    amishash  09/29/14 - Only applied system discounts should be saved
 *    sgu       07/23/14 - add tax authority name
 *    yiqzhao   07/15/14 - Save original line number for order line item.
 *    yiqzhao   07/01/14 - Add two columns for tr_ltm_sls_rtn table.
 *    sgu       06/22/14 - insert discount and tax status for take with items
 *                         added during pickup
 *    yiqzhao   06/20/14 - Persist order line item deposit amount and balance
 *                         due.
 *    yiqzhao   06/11/14 - Handle new columns for original transaction id for
 *                         order.
 *    ohorne    03/07/14 - Suspended Orders are not saved to OR_* tables
 *    sgu       03/06/14 - add logic to retrieve transaction discount and tax
 *                         for CSC order
 *    mchellap  02/26/14 - Persist external serialnumber creation flag
 *    yiqzhao   10/24/13 - Persist external validation serialized item flag in
 *                         sale return line item table.
 *    jswan     10/04/13 - Fixed an RTLog issue caused by a conflict between a
 *                         promotional price and price override.
 *    tksharma  09/23/13 - modified insertSaleReturnPriceModifier and
 *                         updateSale...() methods to push promoId,
 *                         promoCompid, promoCompDetailId to
 *                         TR_MDFR_SLS_RTN_PRC table
 *    abondala  09/05/13 - deprecate some of the API related to tax that is not
 *                         referred as that is causing outOfMemory issues.
 *    vtemker   08/22/13 - EIT Defect 240 - xchannel orders with price override
 *                         details were not getting saved to POS db
 *    yiqzhao   07/11/13 - Populate the return total amount.
 *    tksharma  05/03/13 - fixed method getDiscountRuleID(..) to return
 *                         priceDerivationRuleID for multithreshold rules too
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    mkutiana  04/04/13 - Fix NPE for items deleted after a dscount is added
 *    sgu       03/08/13 - set sales type and item status for order based
 *                         return item
 *    icole     02/28/13 - Forward Port Print trace number on receipt for gift
 *                         cards, required by ACI.
 *    sgu       01/14/13 - process pickup or cancel for store order items
 *    mkutiana  01/10/13 - Persisting the Sale restrictive age field for the
 *                         SaleReturn Line items in the transacation
 *    sgu       01/07/13 - add quantity pending
 *    sgu       12/27/12 - add serialized item flag to sale return line item
 *                         table
 *    rgour     12/20/12 - add business day column for return tender data
 *    sgu       12/14/12 - add logic to get order id at line item level
 *    sgu       12/14/12 - add order id column and rename order item reference
 *                         column
 *    jswan     12/13/12 - Modified to prorate discount and tax for returns of
 *                         order line items.
 *    sgu       12/12/12 - prorate tax for order pickup, cancel, and return
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    jswan     12/06/12 - Modified to support JDBC opertions for order tax and
 *                         discount status.
 *    yiqzhao   11/27/12 - Reduce to three kinds of status.
 *    yiqzhao   11/26/12 - Ade auto merge.
 *    yiqzhao   11/26/12 - set order line item status in database.
 *    sgu       11/26/12 - set desposition code for special order item
 *    sgu       11/26/12 - set pickup store id for store order items
 *    yiqzhao   11/19/12 - set oder line item status for RTLog.
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    yiqzhao   11/15/12 - crosschannel for RTLog
 *    sgu       11/09/12 - add check for xc
 *    sgu       11/07/12 - added captured order line item
 *    jswan     10/25/12 - Modified to support returns by order.
 *    sgu       10/16/12 - rename FIELD_ITEM_QUANTITY_PICKED to
 *                         FIELD_ITEM_QUANTITY_PICKED_UP
 *    sgu       10/16/12 - clean up order item quantities
 *    sgu       10/15/12 - added ordered amount at order ine item level
 *    arabalas  10/03/12 - Forward Port : Fix is to update the POS log
 *                         correctly when transaction overrides of tax are
 *                         followed by item overrides of tax
 *    sgu       09/19/12 - add completed and cancelled amount at order line
 *                         item level
 *    sgu       08/27/12 - persist a TransactionDiscountAuditIfc discount rule
 *    sgu       08/27/12 - read transaction discount audit from db
 *    sgu       08/21/12 - rename column names in retail price modifer
 *    sgu       08/20/12 - fix defects in xc order discount rule reading
 *    sgu       08/17/12 - refactor discount audit
 *    sgu       08/16/12 - add ItemDiscountAudit discount rule
 *    sthallam  05/30/12 - Enhanced RPM Integration - Clearance Pricing
 *    cgreene   05/16/12 - arrange order of businessDay column to end of
 *                         primary key to improve performance since most
 *                         receipt lookups are done without the businessDay
 *    sgu       05/16/12 - rename column LN_ITM_REF to AI_ORD_LN_ITM based on
 *                         Luis' review comments
 *    sgu       05/15/12 - added order line sequence number
 *    sgu       05/15/12 - remove column LN_ITM_REF from order line item tables
 *    yiqzhao   05/03/12 - save and read shipping charge flag for
 *                         SaleReturnLineItem
 *    sgu       04/25/12 - fixed indentation
 *    sgu       04/24/12 - add order line reference
 *    sgu       04/19/12 - save order status change date
 *    sgu       04/18/12 - enhance order item tables to support xc
 *    sgu       04/04/12 - merge with latest build
 *    sgu       03/29/12 - move order recipient record be transactional data.
 *    rabhawsa  03/29/12 - item condition is not a mandaotory field.
 *    mkutiana  03/28/12 - XbranchMerge mukothan_bug-13090267 from
 *                         rgbustores_13.3x_generic_branch
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    rabhawsa  03/21/12 - persisting item condition
 *    cgreene   12/20/11 - XbranchMerge mukothan_bug-12924680 from
 *                         rgbustores_13.3x_generic_branch
 *    mchellap  11/08/11 - Don't save temp price for price entry required items
 *    jswan     10/10/11 - Increased the sequence number for deleted items if
 *                         there is a tender change record being written to the
 *                         database.
 *    yiqzhao   10/06/11 - remove extra @parm for saveSaleReturnTaxModifier
 *                         method
 *    yiqzhao   10/06/11 - remove the loop to avoid calculate tax amount
 *                         multiple time.
 *    yiqzhao   10/06/11 - save manual tax group id (0) rather than the item
 *                         tax group id in SaleReturnTaxModifier table.
 *    asinton   09/09/11 - null checks on giftcard.getCurrentBalance() and
 *                         giftCard.getInitialBalance() to allow canceled
 *                         transaction to persist
 *    mukothan  09/08/11 - Updated serialVersionUID
 *    cgreene   09/07/11 - fixed makeSafeString for promotion name
 *    mukothan  09/06/11 - Adding one more column AI_RTN_TND to TR_RTN_TND and
 *                         making it one of PK so that in case of multiple
 *                         tenders for same transaction, each tender can be
 *                         saved without loss of data.
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    vtemker   08/16/11 - Fix for BO flash report on partial pickup of special
 *                         order (Bug ID 12594470)
 *    cgreene   07/19/11 - store layaway and order ids in separate column from
 *                         house account number.
 *    cgreene   07/18/11 - remove hashed number column from gift card tables
 *    blarsen   07/15/11 - Fix misspelled word: retrival
 *    blarsen   07/14/11 - Persisting new authorization journal key
 *    cgreene   07/07/11 - convert entryMethod to an enum
 *    ohorne    07/01/11 - corrected PosItemID and ItemID in OR_LTM
 *    asinton   06/29/11 - Refactored to use EntryMethod and
 *                         AuthorizationMethod enums.
 *    sgu       02/17/11 - use best match on receipt locale before saving
 *    sgu       02/16/11 - save manufacturer item upc
 *    sgu       02/15/11 - check in all
 *    blarsen   01/12/11 - XbranchMerge
 *                         blarsen_bug11069064-null-on-receipt-for-returned-gift-card
 *                         from rgbustores_13.3x_generic_branch
 *    blarsen   01/12/11 - Preventing the actual string null from being written
 *                         to the database when getpromotionName() returns a
 *                         null pointer. The string null was being printed on
 *                         receipts for returned gift cards.
 *    acadar    01/06/11 - merged from 132.3
 *    acadar    01/06/11 - XbranchMerge acadar_bug-10387218 from
 *                         rgbustores_13.3x_generic_branch
 *    acadar    12/10/10 - use quotes
 *    cgreene   12/01/10 - implement saving applied promotion names into
 *                         tr_ltm_prm table
 *    acadar    10/25/10 - merged to tip
 *    acadar    10/21/10 - changes for transaction discounts
 *    nkgautam  10/20/10 - fixed discount percent rounding up issue
 *    jswan     10/07/10 - Fixed issue with voiding a Price Adjustment in which
 *                         the original transaction had a Transaction Discount.
 *    acadar    08/31/10 - comments cleanup
 *    acadar    08/31/10 - changes for external orders to not filter by action
 *                         codes
 *    acadar    08/30/10 - do not filter external order items based on action
 *                         code
 *    jswan     08/25/10 - Fixed issues returning a transaction with a
 *                         transaction discount and non discountable items.
 *                         Also refactored the creation of PLUItems to remove
 *                         extraneous data element from the SaleReturnLineItem
 *                         table.
 *    jswan     08/18/10 - Added taxable flag to the Sale Return Line Item
 *                         table.
 *    jswan     08/13/10 - Checkin for label server change.
 *    sgu       06/11/10 - use -1 as the external order item id to be filled
 *    sgu       06/11/10 - add external order line item for all items of a
 *                         transaction with an external order
 *    jswan     06/01/10 - Modified to support transaction retrieval
 *                         performance and data requirements improvements.
 *    jswan     05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       05/25/10 - clean up jdbc read or save external order info
 *    sgu       05/25/10 - add jdbc read or save for external order info
 *    cgreene   05/05/10 - remove deprecated log amanger and technician
 *    cgreene   04/28/10 - updating deprecated names
 *    jswan     04/27/10 - Merges from refreshing.
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    jswan     04/23/10 - Refactored CTR to include more data in the
 *                         SaleReturnLineItem class and table to reduce the
 *                         data required in and retvieved from the CO database.
 *                         Modified this class to save new SaleReturnLineItem
 *                         data members to the new columns in the database.
 *    cgreene   04/22/10 - updating deprecated names
 *    jswan     04/12/10 - Checkin prelimary modifications in order to refresh
 *                         the view.
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *    blarsen   10/01/09 - XbranchMerge
 *                         blarsen_bug8841387-validate-trans-discount-on-return-fix
 *                         from rgbustores_13.1x_branch
 *    blarsen   09/03/09 - Adding transactionDiscounts != null check. When
 *                         returning a transaction with trans-level discounts,
 *                         this can occur.
 *    jswan     04/22/09 - Fixed issue with saving the correct order sequence
 *                         number associated with the this row.
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    mahising  02/26/09 - Rework for PDO functionality
 *    jswan     01/20/09 - Modified to save status to
 *                         TABLE_ORDER_LINE_ITEM_STATUS for all types of
 *                         orders.
 *    mahising  01/13/09 - fix QA issue
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    aphulamb  12/09/08 - Deposite Amount and Cancel Trasaction Fixes.
 *    npoola    12/04/08 - Amrish Checkins
 *    npoola    12/04/08 - PDO Amrish checkins
 *    mchellap  12/02/08 - Merging with previous version
 *    mchellap  12/02/08 - Modified updateOrderLineItem to save line Item line
 *                         number in status table
 *    npoola    11/30/08 - CSP POS and BO changes
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/15/08 - Pickup Delivery Order functionality
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    cgreene   11/18/08 - removed call to deprecated salereturntrans method
 *                         that did nothing
 *    mdecama   11/17/08 - If a giftCertificate was added, deleted and
 *                         re-added, it wasn't being saved in the
 *                         GiftCertificate Table. Moved the Logic to insert a
 *                         GiftCertificate from the saveUnknownItem method to
 *                         the insertSaleReturnLineItem method. Deprecated the
 *                         method isGiftCertificateDeleted.
 *    ranojha   11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    10/31/08 - fixes
 *    acadar    10/31/08 - minor fixes for manual discounts localization
 *    ranojha   10/31/08 - Refreshed View and Merged changes with Reason Codes
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 *    ranojha   10/30/08 - Fixed Return/UOM and Department Reason Codes
 *    ranojha   10/29/08 - Fixed ReturnItem
 *    ranojha   10/29/08 - Changes for Return, UOM and Department Reason Codes
 *    acadar    10/29/08 - merged to tip
 *    ddbaker   10/28/08 - Update for merge
 *    acadar    10/25/08 - localization of price override reason codes
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
 $Log:
     17   360Commerce 1.16        5/7/2007 2:21:04 PM    Sandy Gu
     enhance shipping method retrieval and internal tax engine to handle
     tax rules
     16   360Commerce 1.15        5/3/2007 11:57:43 PM   Sandy Gu
     Enhance transaction persistence layer to store inclusive tax
     15   360Commerce 1.14        4/25/2007 10:01:10 AM  Anda D. Cadar   I18N
     merge
     14   360Commerce 1.13        4/2/2007 5:57:02 PM    Snowber Khan    Merge
     from v8x 1.11.1.2 - CR 23450 - tax override on/off not being save
     to tax item modifier table - backing out change - having unexpected
     side-effects, CR 25856 - Updating to preserve "tax exempt amount"
     for record keeping - without treating it as a charged tax.

     13   360Commerce 1.12        1/12/2007 4:47:06 PM   Brett J. Larsen Merge
     from JdbcSaveRetailTransactionLineItems.java, Revision 1.11.1.0
     12   360Commerce 1.11        11/16/2006 1:12:09 PM  Brendan W. Farrell
     Oracle treats "" as null, so only add unique id if it contains
     something.   Otherwise use the default.
     11   360Commerce 1.10        6/8/2006 3:54:24 PM    Brett J. Larsen CR
     18490 - UDM - columns CD_MTH_PRDV, CD_SCP_PRDV and CD_BAS_PRDV's
     type was changed to INTEGER
     10   360Commerce 1.9         5/12/2006 5:26:29 PM   Charles D. Baker
     Merging with v1_0_0_53 of Returns Managament
     9    360Commerce 1.8         4/27/2006 7:26:59 PM   Brett J. Larsen CR
     17307 - remove inventory functionality - stage 2
     8    360Commerce 1.7         4/24/2006 5:51:32 PM   Charles D. Baker
     Merge of NEP62
     7    360Commerce 1.6         3/17/2006 4:10:41 AM   Akhilashwar K. Gupta
     CR-16013: Updated method getTaxExemptAmount(RetailTransactionIfc
     transaction) for the correct tax exempt amount
     6    360Commerce 1.5         1/25/2006 4:11:23 PM   Brett J. Larsen merge
     7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
     5    360Commerce 1.4         1/22/2006 11:41:23 AM  Ron W. Haight
     Removed references to com.ibm.math.BigDecimal
     4    360Commerce 1.3         12/13/2005 4:43:45 PM  Barry A. Pape
     Base-lining of 7.1_LA
     3    360Commerce 1.2         3/31/2005 4:28:44 PM   Robert Pearse
     2    360Commerce 1.1         3/10/2005 10:22:49 AM  Robert Pearse
     1    360Commerce 1.0         2/11/2005 12:12:03 PM  Robert Pearse
     $:
     9    .v710     1.2.1.1.3.0 9/21/2005 13:39:48     Brendan W. Farrell
     Initial Check in merge 67.
     8    .v700     1.2.1.4     1/6/2006 12:37:25      Deepanshu       CR
     6017: Calculate and save tax exempt
     7    .v700     1.2.1.3     11/14/2005 17:13:40    Deepanshu       CR
     6144: Persist the value of fromRetrievedTransaction for a retrieved
     transaction.
     6    .v700     1.2.1.2     11/7/2005 10:19:59     Deepanshu       CR
     6088: Add Authorization Response column only if authorization code
     is not blank
     5    .v700     1.2.1.1     5/19/2005 16:07:09     Jason L. DeLeau Correct
     class cast exception
     4    .v700     1.2.1.0     4/29/2005 15:52:42     Charles Suehs   The
     discount amount must be populated even for discounts based upon
     percentage.
     3    360Commerce1.2         3/31/2005 15:28:44     Robert Pearse
     2    360Commerce1.1         3/10/2005 10:22:49     Robert Pearse
     1    360Commerce1.0         2/11/2005 12:12:03     Robert Pearse
 $
 Revision 1.37.2.1  2004/10/20 13:11:04  kll
 @scr 7393: gift certificate db2 syntax adjustments

 Revision 1.37  2004/09/16 20:42:07  jdeleau
 @scr 7196 Fix saving BigDecimal object to the database - toString() introduces
 precision errors, get the float value and convert that to a String instead.

 Revision 1.36  2004/08/12 20:38:45  cdb
 @scr 6644 Data Operation was using Time Stamp instead of SQL Date String.

 Revision 1.35  2004/08/09 18:51:33  kll
 @scr 6645: modified getPriceModifierPercent

 Revision 1.34  2004/08/03 18:14:19  cdb
 @scr 6646 insertGiftCardMehod: Corrected duplicate "addColumn" statement. Put request type in quotes.

 Revision 1.33  2004/08/01 22:33:28  cdb
 @scr 6646 Error writing to gift card now causes data transaction to fail and induce a rollback and transaction queueing.

 Revision 1.32  2004/07/30 14:21:53  kll
 @scr 6622: use JdbcUtilities' inQuotes method

 Revision 1.31  2004/07/19 21:53:44  jdeleau
 @scr 6329 Fix the way post-void taxes were being retrieved.
 Fix for tax overrides, fix for post void receipt printing, add new
 tax rules for reverse transaction types.

 Revision 1.30  2004/07/15 23:21:17  crain
 @scr 5280 Gift Certificates issued in Training Mode can be Tendered outside of Training Mode

 Revision 1.29  2004/07/02 19:11:27  jdeleau
 @scr 5982 Support Tax Holiday

 Revision 1.28  2004/06/29 21:58:58  aachinfiev
 Merge the changes for inventory & POS integration

 Revision 1.27  2004/06/28 14:13:22  jdeleau
 @scr 5818 Suspended transactions now have all types of
 overrides preserved when suspended.

 Revision 1.26  2004/06/15 16:05:33  jdeleau
 @scr 2775 Add database entry for uniqueID so returns w/
 receipt will work, make some fixes to FinancialTotals storage of tax.

 Revision 1.25  2004/06/11 17:31:35  cdb
 @scr 5553 Updated handling of gift cards in financial totals for sake of register summary reports.

 Revision 1.24  2004/06/02 16:55:11  jriggins
 @scr 4971 Removed price override indicators and $0 discounts from price adjustment component objects which was causing the RetailPriceModifier table to be incorrectly updated leading to DataExceptions

 Revision 1.23  2004/06/01 18:30:43  jdeleau
 @scr 2775 Bug fixes for tax

 Revision 1.22  2004/05/28 22:21:16  jdeleau
 @scr 2775 Fix ArrayOutOfBounds Exception

 Revision 1.21  2004/05/28 19:10:20  jdeleau
 @scr 2775 Change tax structure to use TaxInformationIfc


 Revision 1.19  2004/05/26 19:26:03  lzhao
 @scr 4670: add send label count for send.

 Revision 1.18  2004/05/19 23:09:27  cdb
 @scr 5103 Updating to more correctly handle register reports.

 Revision 1.17  2004/05/04 15:34:52  blj
 @scr 4603 - added new column to gift card table.

 Revision 1.16  2004/04/21 13:38:17  jriggins
 @scr 3979 Added price adjustment logic for updating the RetailPriceModifier table and removed restriction on saving both sale and return price adjusted components

 Revision 1.15  2004/04/20 12:49:25  jriggins
 @scr 3979 Added UpdatePriceAdjustedItemsDataTransaction and associated operations

 Revision 1.14  2004/04/15 15:25:28  jriggins
 @scr 3979 Added price adjustment line item columns

 Revision 1.13  2004/04/14 20:07:38  lzhao
 @scr 3872 Redeem, change gift card request type from String to in.

 Revision 1.12  2004/04/09 16:55:44  cdb
 @scr 4302 Removed double semicolon warnings.

 Revision 1.11  2004/03/16 18:27:08  cdb
 @scr 0 Removed tabs from all java source code.

 Revision 1.10  2004/03/02 22:44:28  cdb
 @scr 3588 Added ability to save Employee ID associated
 with a discount at the transaction level.

 Revision 1.9  2004/02/26 20:55:56  epd
 @scr 3561 Fixed a couple returns related db issues

 Revision 1.8  2004/02/19 21:05:30  epd
 @scr 3561 Fixed bug in saving item size

 Revision 1.7  2004/02/18 21:09:08  epd
 @scr 3561
 Makes use of new Item Size code attribute

 Revision 1.6  2004/02/17 20:37:12  baa
 @scr 3561 returns

 Revision 1.4  2004/02/17 16:18:44  rhafernik
 @scr 0 log4j conversion

 Revision 1.3  2004/02/12 17:13:18  mcs
 Forcing head revision

 Revision 1.2  2004/02/11 23:25:21  bwf
 @scr 0 Organize imports.

 Revision 1.1.1.1  2004/02/11 01:04:28  cschellenger
 updating to pvcs 360store-current


 *
 *    Rev 1.4   Feb 09 2004 17:11:24   crain
 * Added gift certificate item
 * Resolution for 3814: Issue Gift Certificate
 *
 *    Rev 1.3   Feb 05 2004 13:00:00   lzhao
 * add fields current balance and requestType to db gift card table.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Jan 26 2004 17:21:50   cdb
 * Added support for Employee and Damage item discounts.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.1   Sep 03 2003 16:21:40   mrm
 * DB2 support
 * Resolution for POS SCR-3357: Add support needed by RSS
 *
 *    Rev 1.0   Aug 29 2003 15:32:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.17   Jul 15 2003 14:58:36   sfl
 * Since IBMBigDecimal could generate a long value, need to make sure the percentage value has proper format.
 * Resolution for POS SCR-3170: Insert cleanup in pe_dsc
 *
 *    Rev 1.16   09 Jul 2003 19:15:40   mpm
 * Added support for price override authorization persistence.
 *
 *    Rev 1.15   Jun 17 2003 11:47:00   sfl
 * Improved the code.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.14   Jun 12 2003 13:32:32   sfl
 * Save extra data into the SaleReturnTaxLineItem table
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.13   Apr 24 2003 17:26:32   sfl
 * Minor enhancement
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.12   Apr 24 2003 17:12:36   sfl
 * Implemented recording each tax jurisdiction's tax amount to support tax auditing.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.11   Mar 20 2003 09:29:00   jgs
 * Changes due to code review.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.10   Mar 12 2003 10:02:30   DCobb
 * Code review cleanup.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.9   Mar 05 2003 18:04:50   DCobb
 * Save and restore alteration information.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.8   Feb 15 2003 17:25:58   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.7   Jan 22 2003 15:07:40   mpb
 * SCR #1626
 * In updateRetailPriceModifier() and insertRetailPriceModifier(), save the accounting method for reductions.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.6   Jan 20 2003 11:49:04   jgs
 * Added code to read/write columns for allow repeating sources, deal distribution, and percent off lowest priced Item.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.5   Dec 19 2002 10:43:00   sfl
 * Using improved way to dynamically increase deleted items' sequence number when having transaction discounts.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.4   Dec 18 2002 10:18:36   sfl
 * Added checking to see if 1) transaction has multiple discounts, 2) transaction has deleted items. If yes, need to increase the deleted line item sequence number to be greater than the last sequence number of the recorded multiple transaction discount sequence numbers.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.3   03 Oct 2002 17:24:54   sfl
 * Implemented recording calculated tax data for each line items
 * and save into a new table defined by ARTS to keep various types of  item level tax data.
 *
 *    Rev 1.2   16 Jun 2002 08:15:40   vpn-mpm
 * Insulated against empty-department-ID being written to database.
 *
 *    Rev 1.1   05 Jun 2002 17:11:50   jbp
 * changes for pricing updates
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:40:04   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.sql.SQLDeleteStatement;
import oracle.retail.stores.common.sql.SQLInsertStatement;
import oracle.retail.stores.common.sql.SQLUpdateStatement;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ItemDiscountAuditStrategyIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.SecurityOverrideIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.common.utility.Util;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This class is the data operation for saving retail transaction line items to
 * the database.
 *
 * @version $Revision: /main/107 $
 */
public class JdbcSaveRetailTransactionLineItems extends JdbcSaveRetailTransaction
    implements DiscountRuleConstantsIfc
{
    private static final long serialVersionUID = 3853213564571908740L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcSaveRetailTransactionLineItems.class);

    protected static final String TYPE_SALE_RETURN = "SR";

    protected static final String TYPE_DISCOUNT = "DS";

    protected static final String TYPE_TAX = "TX";

    protected static final String TYPE_HOUSE_PAYMENT = "HP";

    protected static final String TYPE_ORDER = "OR";

    protected static final String EXTERNAL_ORDER_ITEM_ID_TO_BE_FILLED_IN = "-1";

    /**
     * Serialization Capture time : StoreReceiving
     */
    protected static final String STORE_RECEIVING_SERIALIZED_CAPTURE_TIME = "StoreReceiving";

    /**
     * Class constructor.
     */
    public JdbcSaveRetailTransactionLineItems()
    {
        super();
        setName("JdbcSaveRetailTransactionLineItems");
    }

    /**
     * Execute the SQL statements against the database.
     *
     * @param dataTransaction The data transaction
     * @param dataConnection The connection to the data source
     * @param action The information passed by the valet
     * @exception DataException upon error
     */
    public void execute(DataTransactionIfc dataTransaction, DataConnectionIfc dataConnection, DataActionIfc action)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveRetailTransactionLineItems.execute()");

        /*
         * getUpdateCount() is about the only thing outside of DataConnectionIfc
         * that we need.
         */
        JdbcDataConnection connection = (JdbcDataConnection) dataConnection;

        // Navigate the input object to obtain values that will be inserted
        // into the database.
        ARTSTransaction artsTransaction = (ARTSTransaction) action.getDataObject();
        TenderableTransactionIfc transaction = (TenderableTransactionIfc) artsTransaction.getPosTransaction();
        saveRetailTransactionLineItems(connection, transaction);

        if (logger.isDebugEnabled())
            logger.debug("JdbcSaveRetailTransactionLineItems.execute()");
    }

    /**
     * Updates the Retail Transaction Line Item and Sale Return Line Item
     * tables. Also, may have to update Discount Line Item and/or Tax Line Item
     * tables
     *
     * @param dataConnection connection to the data source
     * @param transaction retail transaction to save the line items
     * @exception DataException upon error
     */
    public void saveRetailTransactionLineItems(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
            throws DataException
    {
        if (transaction instanceof PaymentTransactionIfc &&
        // layaway transactions insert their own payments
                !(transaction instanceof LayawayTransactionIfc))
        {
            try
            {
                // save payment line item
                savePaymentLineItem(dataConnection, transaction);
            }
            catch (DataException de)
            {
                throw de;
            }
        }
        else
        {
            if (transaction instanceof RetailTransactionIfc)
            {
                RetailTransactionIfc rt = (RetailTransactionIfc) transaction;
                int lineItemSequenceNumber = rt.getLineItems().length;
                saveSaleReturnLineItems(dataConnection, rt);
                saveTaxLineItem(dataConnection, rt, lineItemSequenceNumber);
                saveDiscountLineItems(dataConnection, rt, ++lineItemSequenceNumber);
                if (transaction instanceof SaleReturnTransactionIfc)
                {
                    saveReturnTendersData(dataConnection, (SaleReturnTransactionIfc) transaction);
                }
            }

            // if order, insert order line item data
            if (transaction instanceof OrderTransactionIfc)
            {
                saveOrderLineItems(dataConnection, (OrderTransactionIfc) transaction);
            }
            else
            {
                // This code must be in this else statement because
                // an OrderTransactionIfc object is also a SaleReturnTransactionIfc.
                // This code should be executed for non order transactions only.
                if (transaction instanceof SaleReturnTransactionIfc &&
                        transaction.getTransactionStatus() != TransactionConstantsIfc.STATUS_CANCELED)
                {
                    // This method iterates through each sale return line item to determine
                    // if the return status sould be written to the order line item table.
                    saveReturnOrderLineItemStatus(dataConnection, (SaleReturnTransactionIfc) transaction);
                }
            }
        }
    }

    /**
     * Save house payment specific information to database. Domain does not have
     * a housepaymentlineitem class. For house payment info, this method first
     * tries to insert it. If that fails, it will attempt to insert it.
     * Modifies the PaymentOnAccountLineItem tables.
     *
     * @param dataConnection Data Source
     * @param transaction The Retail Transaction to save
     * @exception DataException upon error
     */
    public void savePaymentLineItem(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction)
            throws DataException
    {
        // If the insert fails, then try to update the line item
        try
        {
            insertPaymentLineItem(dataConnection, (PaymentTransactionIfc) transaction);
        }
        catch (DataException e)
        {
            updatePaymentLineItem(dataConnection, (PaymentTransactionIfc) transaction);
        }
    }

    /**
     * Saves the sale return line items. For each line item, this method first
     * tries to update it. If that fails, it will attempt to insert it.
     * Modifies the RetailTransactionLineItem and SaleReturnLineItem tables.
     *
     * @param dataConnection Data Source
     * @param transaction The Retail Transaction to save
     * @exception DataException upon error
     */
    public void saveSaleReturnLineItems(JdbcDataConnection dataConnection, RetailTransactionIfc transaction)
            throws DataException
    {
        AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();

        Vector v = ((SaleReturnTransactionIfc) transaction).getItemContainerProxy().getLineItemsVector();
        v.copyInto(lineItems);

        int numItems = 0;
        int maxItemSequenceNumber = 0;
        SaleReturnLineItemIfc srli = null;
        if (lineItems != null)
        {
            numItems = lineItems.length;
        }

        SaleReturnLineItemIfc lineItem;

        /*
         * Loop through each line item. Continue through them all even if one
         * has failed.
         */
        for (int i = 0; i < numItems; i++)
        {
            lineItem = (SaleReturnLineItemIfc) lineItems[i];

            // if item is a giftcard, then save to gift card table
            if (lineItem.getPLUItem() instanceof GiftCardPLUItemIfc)
            {
                insertGiftCard(dataConnection, transaction, lineItem);
            }
            else if (lineItem.isAlterationItem())
            {
                saveAlteration(dataConnection, transaction, lineItem);
            }

            /*
             * If the insert fails, then try to update the line item
             */
            try
            {
                insertSaleReturnLineItem(dataConnection, transaction, lineItem);
            }
            catch (DataException e)
            {
                updateSaleReturnLineItem(dataConnection, transaction, lineItem);
            }
        }

        // Loop through each deleted line items and insert them into
        // RetailTransactionTable
        Vector deletedLineItems = ((SaleReturnTransactionIfc) transaction).getDeletedLineItems();
        if (deletedLineItems != null)
        {
            if (deletedLineItems.size() > 0)
            {
                // When the transaction is not suspended
                if (transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED)
                {
                    maxItemSequenceNumber = numItems + 1;
                }

                // When the transaction is suspended
                if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED)
                {
                    maxItemSequenceNumber = numItems + 1;
                }
                if (transaction.containsOrderLineItems())
                {
                    maxItemSequenceNumber += 1;
                }

                // When the transaction has customer discount
                if (transaction.getTransactionDiscounts() != null)
                {
                    maxItemSequenceNumber += 1;
                }

                // Increment by number of tender types used
                maxItemSequenceNumber += transaction.getTenderLineItemsSize();

                // When change is due
                if (transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED
                        && transaction.getTenderTransactionTotals().getBalanceDue().signum() != CurrencyIfc.ZERO)
                {
                    maxItemSequenceNumber += 1;
                }

                // Increase sequence number by one when having mutiple
                // transaction level discounts and deleted items.
                if (transaction.getTransactionDiscounts() != null)
                {
                    if ((deletedLineItems.size() > 0) && (transaction.getTransactionDiscounts().length > 1))
                    {
                        maxItemSequenceNumber += (transaction.getTransactionDiscounts().length - 1);
                    }
                }

                // Increase the sequence number by one for the tender change record
                if (transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED
                        && transaction.getTenderTransactionTotals().getChangeDue().signum() != CurrencyIfc.ZERO)
                {
                    maxItemSequenceNumber += 1;
                }

                for (int i = 0; i < deletedLineItems.size(); i++)
                {
                    // Insert the deleted items into the
                    // RetailTransacionLineItem table
                    insertRetailTransactionLineItem(dataConnection, transaction, maxItemSequenceNumber,
                            TYPE_SALE_RETURN, "1");
                    // Insert the deleted items into the SaleReturnLineItem
                    // table and other tables
                    srli = (SaleReturnLineItemIfc) deletedLineItems.elementAt(i);
                    srli.setLineNumber(maxItemSequenceNumber);
                    insertDeletedSaleReturnLineItem(dataConnection, transaction, srli);
                    // Move to next deleted line item
                    maxItemSequenceNumber = maxItemSequenceNumber + 1;
                }
            }
        }
    }

    /**
     * Saves the return tenders data. For each line item, this method first
     * tries to update it. If that fails, it will attempt to insert it.
     * Modifies the ReturnTender table.
     *
     * @param dataConnection Data Source
     * @param transaction The Retail Transaction to save
     * @exception DataException upon error
     */
    public void saveReturnTendersData(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction)
            throws DataException
    {
        ReturnTenderDataElementIfc[] returnTenders = transaction.getReturnTenderElements();
        int numItems = 0;

        if (returnTenders != null)
        {
            numItems = returnTenders.length;
        }

        /*
         * Loop through each return tender. Continue through them all even if
         * one has failed.
         */
        for (int i = 0; i < numItems; i++)
        {
            try
            {
                insertReturnTendersData(dataConnection, transaction, returnTenders[i], i);
            }
            catch (DataException e)
            {
                updateReturnTendersData(dataConnection, transaction, returnTenders[i], i);
            }

        }

    }

    /**
     * Saves an unknown item
     *
     * @param dataConnection Data Source
     * @param transaction The Retail Transaction to save
     * @param lineItem The sales/return line item
     * @exception DataException upon error
     */
    public void saveUnknownItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        try
        {
            insertUnknownItem(dataConnection, transaction, lineItem);
        }
        catch (DataException e)
        { // Is this really the right thing to do?
            updateUnknownItem(dataConnection, transaction, lineItem);
        }
    }

    /**
     * Saves the commission modifier
     *
     * @param dataConnection Data Source
     * @param transaction The Retail Transaction to save
     * @param lineItem The sales/return line item
     * @exception DataException
     */
    public void saveCommissionModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        try
        {
            insertCommissionModifier(dataConnection, transaction, lineItem);
        }
        catch (DataException e)
        {
            updateCommissionModifier(dataConnection, transaction, lineItem);
        }
    }

    /**
     * Saves the sale the retail modifiers
     *
     * @param dataConnection Data Source
     * @param transaction The Retail Transaction to save
     * @param lineItem The sales/return line item
     * @exception DataException
     */
    public void saveRetailPriceModifiers(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        int discountSequenceNumber = 0;

        /*
         * See if there is a price override
         */
        if (lineItem.getItemPrice().isPriceOverride())
        {
            try
            {
                insertRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber, null);
            }
            catch (DataException e)
            {
                updateRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber, null);
            }

            ++discountSequenceNumber;
        }

        ItemDiscountStrategyIfc[] modifiers = lineItem.getItemPrice().getItemDiscounts();
        ItemDiscountStrategyIfc discountLineItem;

        // get number of discounts for loop
        int numDiscounts = 0;
        if (modifiers != null)
        {
            numDiscounts = modifiers.length;
        }

        /*
         * Loop through each line item.
         */
        for (int i = 0; i < numDiscounts; i++)
        {
            discountLineItem = modifiers[i];

            /*
             * In the case of a sale line item, a transaction discount will be written to the Sale
             * Return Price Modifier Table; however, since returns have been converted to Item
             * level discounts they must go into the Retail Price modifier table.
             */
            boolean saveRetailPriceModifier = false;
            if (discountLineItem.getDiscountScope() == DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION)
            {
                if (lineItem.isReturnLineItem())
                {
                    saveRetailPriceModifier = true;
                }
            }
            else
            {
                saveRetailPriceModifier = true;
            }

            if (saveRetailPriceModifier)
            {
                /*
                 * If the insert fails, then try to update the line item
                 */
                try
                {
                    insertRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
                            discountLineItem);
                }
                catch (DataException e)
                {
                    updateRetailPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
                            discountLineItem);
                }
            }
            else
            {
                /*
                 * If the insert fails, then try to update the line item
                 */
                try
                {
                    insertSaleReturnPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
                            discountLineItem);
                }
                catch (DataException e)
                {
                    updateSaleReturnPriceModifier(dataConnection, transaction, lineItem, discountSequenceNumber,
                            discountLineItem);
                }
            }

            if (transaction instanceof OrderTransactionIfc)
            {
                // DiscountStatus will be null for items that have been deleted from the transaction after a discount is added
                // For items deleted AFTER a discount is added to the transaction, we donot need to save the information in the order tables
                // The deleted items will be save in the transaction tables
                if (lineItem.getOrderItemStatus().getDiscountStatus(discountLineItem.getOrderItemDiscountLineReference()) != null)
                {
                    saveOrderDiscountStatus(dataConnection, (OrderTransactionIfc)transaction, lineItem, discountSequenceNumber,
                        discountLineItem);
                }
            }

            ++discountSequenceNumber;
        }

        // Save the Promotions
        PromotionLineItemIfc[] promotionLineItems = lineItem.getItemPrice().getPromotionLineItems();
        PromotionLineItemIfc promotionLineItem;

        if (promotionLineItems != null && promotionLineItems.length > 0
                // unknown items have a temp price but are not on promotion
                && !(lineItem.getPLUItem() instanceof UnknownItemIfc)
                // price entry required items have an override price but are not on promotion
                && !(lineItem.getPLUItem().getItemClassification().isPriceEntryRequired())
                // price overrides are not on promotion
                && !(lineItem.getItemPrice().isPriceOverride()))
        {
            for (int sequenceNumber = 0; sequenceNumber < promotionLineItems.length; sequenceNumber++)
            {
                promotionLineItem = promotionLineItems[sequenceNumber];
                try
                {
                    insertPromotionLineItem(dataConnection, transaction, lineItem, promotionLineItem, sequenceNumber);
                }
                catch (DataException e)
                {
                    updatePromotionLineItem(dataConnection, transaction, lineItem, promotionLineItem);
                }

            }
        }
    }

    /**
     * Saves the sale tax modifiers
     *
     * @param dataConnection Data Source
     * @param transaction The Retail Transaction to save
     * @param lineItem The sales/return line item
     * @exception DataException upon error
     */
    public void saveSaleReturnTaxModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        ItemTaxIfc tax = lineItem.getItemPrice().getItemTax();

        // Only save overrides if they happened.
        if(tax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT ||
                    tax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE ||
                    tax.getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_ON ||
              tax.getTaxMode() == TaxConstantsIfc.TAX_MODE_TOGGLE_OFF )
        {
            /*
             * If the insert fails, then try to update the line item
             */
            try
            {
                insertSaleReturnTaxModifier(dataConnection, transaction, lineItem, 0, tax);
            }
            catch (DataException e)
            {
                updateSaleReturnTaxModifier(dataConnection, transaction, lineItem, 0, tax);
            }
        }
    }

    /**
     * Saves the transaction tax information
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The current sequence number of the line
     *            items
     * @exception DataException upon error
     */
    public void saveTaxLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber) throws DataException
    {
        try
        {
            insertTaxLineItem(dataConnection, transaction, lineItemSequenceNumber);
        }
        catch (DataException e)
        {
            updateTaxLineItem(dataConnection, transaction, lineItemSequenceNumber);
        }

        if (transaction.getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
        {
            /*
             * If it's tax exempt, add a Tax Exemption Modififier
             */
            try
            {
                insertTaxExemptionModifier(dataConnection, transaction, lineItemSequenceNumber);
            }
            catch (DataException e)
            {
                updateTaxExemptionModifier(dataConnection, transaction, lineItemSequenceNumber);
            }
        }
    }

    /**
     * Saves the discounts from the line items
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the sale/return line
     *            item
     * @exception DataException upon error
     */
    public void saveDiscountLineItems(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber) throws DataException
    {
        // Allow transaction level discount if it is a web managed transaction or there is discountable
        // line item in a ORPOS managed transaction.
        if (transaction instanceof SaleReturnTransactionIfc
                && ((SaleReturnTransactionIfc) transaction).hasDiscountableItems() || ((SaleReturnTransactionIfc) transaction).isWebManagedOrder())
        {
            TransactionDiscountStrategyIfc[] discountLineItems = transaction.getTransactionDiscounts();
            int numDiscounts = 0;
            if (discountLineItems != null)
            {
                numDiscounts = discountLineItems.length;
            }

            /*
             * Loop through each line item.
             */
            for (int i = 0; i < numDiscounts; i++)
            {
                TransactionDiscountStrategyIfc lineItem = discountLineItems[i];
                if (lineItem.getAssignmentBasis() == TransactionDiscountStrategyIfc.ASSIGNMENT_ITEM
                        && !lineItem.isSystemDiscountApplied())
                {
                    continue;
                }
                /*
                 * If the insert fails, then try to update the line item
                 */
                try
                {
                    insertDiscountLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
                }
                catch (DataException e)
                {
                    updateDiscountLineItem(dataConnection, transaction, lineItemSequenceNumber, lineItem);
                }

                ++lineItemSequenceNumber;
            }
        }
    }

    /**
     * Saves order line items.
     *
     * @param dataConnection connection to database
     * @param orderTransaction order transaction
     * @exception DataException thrown if error occurs
     */
    public void saveOrderLineItems(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction)
            throws DataException
    {
        // insert new line items into order line item table
        AbstractTransactionLineItemIfc[] lineItems = orderTransaction.getLineItems();
        int numItems = 0;
        if (lineItems != null)
        {
            numItems = lineItems.length;
        }

        // loop through line items
        for (int i = 0; i < numItems; i++)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc) lineItems[i];
            if (orderTransaction.getTransactionType() != TransactionIfc.TYPE_ORDER_INITIATE)
            {
               if (lineItem.getOrderItemStatus().getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
               {
                   insertOrderLineItem(dataConnection, orderTransaction, lineItem, i);
               }
               else
               {
                   updateOrderLineItem(dataConnection, orderTransaction, lineItem, i);
               }
            }
            else
            {
                insertOrderLineItem(dataConnection, orderTransaction, lineItem, i);
            }
        }
    }

    /**
     * Update the Retail Transaction Line Item table.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the sales/return
     *            line item
     * @param lineItemType type of line item
     * @exception DataException upon error
     */
    public void updateRetailTransactionLineItem(JdbcDataConnection dataConnection,
            TenderableTransactionIfc transaction, int lineItemSequenceNumber, String lineItemType) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TYPE_CODE, getLineItemType(lineItemType));

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getSequenceNumber(lineItemSequenceNumber));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateRetailTransactionLineItem", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update RetailTransactionLineItem");
        }
    }

    /**
     * Inserts a House Payment Transaction info into a payment line item table.
     * Uses <code>transaction</code> to obtain the primary keys.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @exception DataException upon error
     */
    public void updatePaymentLineItem(JdbcDataConnection dataConnection, PaymentTransactionIfc transaction)
            throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();
        // Table
        sql.setTable(TABLE_PAYMENTONACCOUNT_LINE_ITEM);

        // Field
        PaymentIfc payment = transaction.getPayment();
        if (payment != null)
        {
            if (payment.getEncipheredCardData() != null)
            {
                sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_ENCRYPTED, inQuotes(payment.getEncipheredCardData().getEncryptedAcctNumber()));
                sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_MASKED, inQuotes(payment.getEncipheredCardData().getMaskedAcctNumber()));
            }
            else
            {
                sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CUSTOMER_ACCOUNTID, inQuotes(payment.getReferenceNumber()));
            }
        }
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_ACCOUNT_CODE, inQuotes(transaction.getPayment().getPaymentAccountType()));
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_AMOUNT, transaction.getPaymentAmount().getStringValue());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateHousePaymentLineItem", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update HousePaymentLineItem");
        }
    }

    /**
     * Updates the Sale Return Line Item table.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sales/return line item
     * @exception DataException upon error
     */
    public void updateSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        updateSaleReturnLineItem(dataConnection, transaction, lineItem, TYPE_SALE_RETURN);
    }

    /**
     * Updates the Sale Return Line Item table.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sales/return line item
     * @param lineItemTypeCode line item type code
     * @exception DataException upon error
     */
    public void updateSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, String lineItemTypeCode) throws DataException
    {
        /*
         * Update the Retail Transaction Line Item table first This also has the
         * effect of setting the pos transaction
         */
        updateRetailTransactionLineItem(dataConnection, transaction, lineItem.getLineNumber(), lineItemTypeCode);
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
        sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
        sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
        sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
        sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
        sql
                .addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount()
                        .getStringValue());
        sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
        sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));
        sql.addColumn(FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE, getReturnItemConditionCode(lineItem));
        
        sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
        sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
        sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
        sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));

        sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
        sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
        sql.addColumn(FIELD_SHIPPING_CHARGE_FLAG, getShippingChargeFlag(lineItem));
        sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
        sql.addColumn(FIELD_ORDER_ID, inQuotes(getOrderID(transaction, lineItem)));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().getIxRetailCode()));
        sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
        sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
        sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG, getSalesAssociateModifiedFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION, getReceiptDescription(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE, getReceiptDescriptionLocal(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG, getRestockingFeeFlag(lineItem));
        sql.addColumn(FIELD_SERIALIZED_ITEM_VALIDATION_FLAG, getSerializedItemFlag(lineItem));
        sql.addColumn(FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG, getExternalValidationSerializedItemFlag(lineItem));
        sql.addColumn(FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN, isPOSAllowedToCreateUIN(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE, getProductGroupID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG, getSizeRequiredFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE, getLineItemUOMCode(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID, getPosDepartmentID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID, getItemTypeID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG, getReturnProhibited(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG, getEmployeeDiscountAllowed(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG, getTaxable(lineItem));
        sql.addColumn(FIELD_ITEM_DISCOUNT_FLAG, getDiscountable(lineItem));
        sql.addColumn(FIELD_ITEM_DAMAGE_DISCOUNT_FLAG, getDamageDiscountable(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID, getMerchandiseHierarchyGroupID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC, getManufacturerItemUPC(lineItem));
        sql.addColumn(FIELD_CLEARANCE_INDICATOR, getClearanceIndicator(lineItem));
        sql.addColumn(NON_RETRIEVED_ORIGINAL_RECEIPT_ID, getUserSuppliedReceiptID(lineItem));
        sql.addColumn(FIELD_SALE_AGE_RESTRICTION_ID, getRestrictiveAge(lineItem));
        sql.addColumn(FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG, getPriceEntryRequired(lineItem));

        String extendedRestockingFee = getItemExtendedRestockingFee(lineItem);
        if (extendedRestockingFee != null)
        {
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT, extendedRestockingFee);
        }

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getLineItemSequenceNumber(lineItem));

        if (lineItem.isPriceAdjustmentLineItem())
        {
            sql.addColumn(FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG, makeCharFromBoolean(true));
            sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
        }
        else if (lineItem.isPartOfPriceAdjustment())
        {
            sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
        }

        if (transaction instanceof SaleReturnTransactionIfc)
        {
            if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED ||
                    transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED)
            {
                ReturnItemIfc theReturnItem = lineItem.getReturnItem();
                if (theReturnItem != null)
                {
                    boolean wasRetrieved = theReturnItem.isFromRetrievedTransaction();
                    if (wasRetrieved)
                    {
                        sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'1'");
                    }
                    else
                    {
                        sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'0'");
                    }
                }
            }
        }
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateSaleReturnLineItem", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update SaleReturnLineItem");
        }

        saveRetailPriceModifiers(dataConnection, transaction, lineItem);
        saveSaleReturnTaxModifier(dataConnection, transaction, lineItem);
        saveExternalOrderLineItem(dataConnection, transaction, lineItem);

        /*
         * Track commission properly
         */
        String employee = transaction.getSalesAssociate().getEmployeeID();
        if (lineItem.getSalesAssociate() != null && !employee.equals(lineItem.getSalesAssociate().getEmployeeID()))
        {
            saveCommissionModifier(dataConnection, transaction, lineItem);
        }
    }

    /**
     * Returns the string value of the damage discount eligigle flag
     * @param lineItem
     * @return string with SLQ string boolean value
     */
    private String getDamageDiscountable(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.isDamageDiscountEligible())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the string value of the discount eligigle flag
     * @param lineItem
     * @return string with SLQ string boolean value
     */
    private String getDiscountable(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.isDiscountEligible())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Updates an unknown item
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sale/return line item
     * @exception DataException upon error
     */
    public void updateUnknownItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_UNKNOWN_ITEM);

        // Fields
        sql.addColumn(FIELD_POS_ITEM_ID, getItemID(lineItem));
        sql.addColumn(FIELD_UNKNOWNITEM_CURRENT_SALE_UNIT_POS_RETAIL_PRICE_AMOUNT, getItemPrice(lineItem));
        sql.addColumn(FIELD_ITEM_DESCRIPTION, getItemDescription(lineItem));
        sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
        sql.addColumn(FIELD_ITEM_TAX_EXEMPT_CODE, inQuotes(getItemTaxable(lineItem)));
        if (!Util.isEmpty(lineItem.getPLUItem().getDepartmentID()))
        {
            sql.addColumn(FIELD_POS_DEPARTMENT_ID, inQuotes(lineItem.getPLUItem().getDepartmentID()));
        }
        sql.addColumn(FIELD_UNIT_OF_MEASURE_CODE, getUOMCode(lineItem));

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateUnknownItem", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update UnknownItem");
        }
    }

    /**
     * Updates a commission modifier
     * Currently, this supports only one commission modifier per line item
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sale/return line item
     * @exception DataException upon error
     */
    public void updateCommissionModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_COMMISSION_MODIFIER);

        // Fields
        sql.addColumn(FIELD_EMPLOYEE_ID, getEmployeeID(lineItem));
        sql.addColumn(FIELD_COMMISSION_AMOUNT_PERCENT_FLAG, "'P'");
        sql.addColumn(FIELD_COMMISSION_MODIFIER_PERCENT, "100");

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_COMMISSION_MODIFIER_SEQUENCE_NUMBER + " = " + getSequenceNumber(0));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateCommissionModifier", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update CommissionModifier");
        }
    }

    /**
     * Updates a single price modifier.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sales/return line item
     * @param sequenceNumber The sequence number of the modifier
     * @param discountLineItem discount
     * @exception DataException upon error
     */
    public void updateRetailPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
            throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_RETAIL_PRICE_MODIFIER);

        // Fields
        if (discountLineItem != null)
        { // Item discount
            //This is the item extended discount amount for this discount strategy. It can be negative for return items
            sql.addColumn(FIELD_RETAIL_PRICE_EXTENDED_DISCOUNT_AMOUNT,
                    discountLineItem.getItemDiscountAmount().getStringValue());
            sql.addColumn(FIELD_RETAIL_PRICE_USE_EXTENDED_DISCOUNT_FLAG,
                    getPriceModifierUseExtendedDiscountFlag(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE, getPriceModifierScopeCode(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
                    getPriceModifierAssignmentBasis(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID, makeSafeString(discountLineItem
                    .getDiscountEmployeeID()));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
                    getPriceModifierDamageDiscountFlag(discountLineItem));
            sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
            sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
                    getPriceModifierReferenceID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
                    getPriceModifierReferenceIDTypeCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
                    inQuotes(discountLineItem.getAccountingMethod()));
            sql.addColumn(FIELD_PROMOTION_ID, discountLineItem.getPromotionId());
            sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, discountLineItem.getPromotionComponentId());
            sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, discountLineItem.getPromotionComponentDetailId());
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, discountLineItem.getPricingGroupID());

        }
        else
        { // Price Override
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
            // if security override data exists, use it
            SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
            if (priceOverrideAuthorization != null)
            {
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
                        makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
                        .getEntryMethod().getLegacyCode());
            }
        }

        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER + " = " + getSequenceNumber(sequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateRetailPriceModifier", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update RetailPriceModifier");
        }
    }

    /**
     * Updates a single tax modifier.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sales/return line item
     * @param sequenceNumber The sequence number of the tax line item
     * @param taxLineItem tax
     * @exception DataException upon error
     */
    public void updateSaleReturnTaxModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemTaxIfc taxLineItem) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_TAX_MODIFIER);

        // Fields
        sql.addColumn(FIELD_SALE_RETURN_TAX_AMOUNT, getItemTaxAmount(lineItem));
        sql.addColumn(FIELD_TAX_TYPE_CODE, getItemTaxMode(taxLineItem));
        sql.addColumn(FIELD_TAX_PERCENT, getItemTaxPercent(taxLineItem));
        sql.addColumn(FIELD_TAX_OVERRIDE_PERCENT, getItemTaxOverridePercent(taxLineItem));
        sql.addColumn(FIELD_TAX_OVERRIDE_AMOUNT, getItemTaxOverrideAmount(taxLineItem));
        sql.addColumn(FIELD_SALE_RETURN_TAX_EXEMPTION_REASON_CODE, getItemTaxReasonCode(taxLineItem));
        sql.addColumn(FIELD_TAX_SCOPE_ID, getTaxScope(taxLineItem));
        sql.addColumn(FIELD_TAX_METHOD_ID, getItemTaxMethod(taxLineItem));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_TAX_MODIFIER_SEQUENCE_NUMBER + " = " + getSequenceNumber(sequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateSaleReturnTaxModifier", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update SaleReturnTaxModifier");
        }
    }

    /**
     * Updates a tax line item
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the sale line item
     * @exception DataException upon error
     */
    public void updateTaxLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber) throws DataException
    {

        /*
         * Update the Retail Transaction Line Item table first
         */
        updateRetailTransactionLineItem(dataConnection, transaction, lineItemSequenceNumber, TYPE_TAX);

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_TAX_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_TAX_AMOUNT, getTaxAmount(transaction));
        sql.addColumn(FIELD_TAX_INC_AMOUNT, getInclusiveTaxAmount(transaction));
        sql.addColumn(FIELD_TAX_TYPE_CODE, getTaxMode(transaction));
        sql.addColumn(FIELD_TAX_PERCENT, getTaxPercent(transaction));
        sql.addColumn(FIELD_TAX_OVERRIDE_PERCENT, getTaxOverridePercent(transaction));
        sql.addColumn(FIELD_TAX_OVERRIDE_AMOUNT, getTaxOverrideAmount(transaction));
        sql.addColumn(FIELD_TAX_REASON_CODE, getTaxReasonCode(transaction));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getSequenceNumber(lineItemSequenceNumber));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateTaxLineItem", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update TaxLineItem");
        }
    }

    /**
     * Updates a tax exemption modifier. Used for transaction level tax exempt
     * information.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the tax line item
     * @exception DataException upon error
     */
    public void updateTaxExemptionModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_TAX_EXEMPTION_MODIFIER);

        // Fields
        sql.addColumn(FIELD_ENCRYPTED_TAX_MODIFIER_TAX_EXEMPTION_CERTIFICATE_NUMBER, getTaxExemptCertificateID(transaction));
        sql.addColumn(FIELD_MASKED_TAX_MODIFIER_TAX_EXEMPTION_CERTIFICATE_NUMBER, getMaskedTaxExemptCertificateID(transaction));
        sql.addColumn(FIELD_TAX_MODIFIER_TAX_EXEMPTION_REASON_CODE, getTaxReasonCode(transaction));

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getSequenceNumber(lineItemSequenceNumber));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateTaxExemptionModifier", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update TaxExemptionModifier");
        }
    }

    /**
     * Updates the discount line item table.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the sale line item
     * @param lineItem discount
     * @exception DataException upon error
     */
    public void updateDiscountLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber, TransactionDiscountStrategyIfc lineItem) throws DataException
    {
        /*
         * Update the Retail Transaction Line Item table first
         */
        updateRetailTransactionLineItem(dataConnection, transaction, lineItemSequenceNumber, TYPE_DISCOUNT);

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_DISCOUNT_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_DISCOUNT_REASON_CODE, getDiscountReasonCode(lineItem));
        sql.addColumn(FIELD_DISCOUNT_TYPE_CODE, getDiscountType(lineItem));
        sql.addColumn(FIELD_DISCOUNT_AMOUNT, getDiscountAmount(lineItem));
        sql.addColumn(FIELD_DISCOUNT_PERCENT, getDiscountPercent(lineItem));
        sql.addColumn(FIELD_DISCOUNT_ASSIGNMENT_BASIS, getDiscountAssignmentBasis(lineItem));
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID,
                makeSafeString(lineItem.getDiscountEmployeeID()));
        sql.addColumn(FIELD_DISCOUNT_ENABLED, getDiscountEnabled(lineItem));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_INCLUDED_IN_BEST_DEAL_FLAG, getIncludedInBestDealFlag(lineItem));
        sql.addColumn(FIELD_DISCOUNT_RULE_ID, getDiscountRuleID(lineItem));

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getSequenceNumber(lineItemSequenceNumber));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID, getDiscountReferenceID(lineItem));
        sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID_TYPE_CODE, getDiscountReferenceIDTypeCode(lineItem));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateDiscountLineItem", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update DiscountLineItem");
        }
    }

    /**
     * Inserts a Retail Transaction Line Item.
     * Uses <code>transaction</code> to obtain the primary keys.
     * A detail line item of Retail Transaction that records the business
     * conducted between the retail store and another party involving the
     * exchange in ownership and/or accountability for merchandise and/or tender
     * or involving the exchange of tender for services.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the line item within
     *            a Transaction
     * @param lineItemType A code to denote the type of retail transaction line
     *            item
     * @exception DataException upon error
     */
    public void insertRetailTransactionLineItem(JdbcDataConnection dataConnection,
            TenderableTransactionIfc transaction, int lineItemSequenceNumber, String lineItemType) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TYPE_CODE, getLineItemType(lineItemType));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertRetailTransactionLineItem", e);
        }
    }

    /**
     * Inserts a Retail Transaction Line Item.
     * Uses <code>transaction</code> to obtain the primary keys.
     * A detail line item of Retail Transaction that records the business
     * conducted between the retail store and another party involving the
     * exchange in ownership and/or accountability for merchandise and/or tender
     * or involving the exchange of tender for services.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the line item within
     *            a Transaction
     * @param lineItemType A code to denote the type of retail transaction line
     *            item
     * @param voidFlag A flag to indicate if this item has been deleted
     * @exception DataException upon error
     */
    public void insertRetailTransactionLineItem(JdbcDataConnection dataConnection,
            TenderableTransactionIfc transaction, int lineItemSequenceNumber, String lineItemType, String voidFlag)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TYPE_CODE, getLineItemType(lineItemType));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG, makeSafeString(voidFlag));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertRetailTransactionLineItem", e);
        }
    }

    /**
     * Inserts a House Payment Transaction info into a payment line item table.
     * Uses <code>transaction</code> to obtain the primary keys.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param tableName name of payment table in which to insert
     * @exception DataException upon error
     */
    public void insertPaymentLineItem(JdbcDataConnection dataConnection, PaymentTransactionIfc transaction,
            String tableName) throws DataException
    {
        /*
         * insertRetailTransactionLineItem(dataConnection, transaction,
         * lineItem.getLineNumber(), TYPE_HOUSE_PAYMENT);
         */
        // Get all necessary data
        SQLInsertStatement sql = new SQLInsertStatement();
        // Table
        sql.setTable(tableName);
        // Fields, all fields are not in place in database
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        PaymentIfc payment = transaction.getPayment();
        if(payment != null)
        {
            if (payment.getEncipheredCardData() != null)
            {
                sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_ENCRYPTED, inQuotes(payment.getEncipheredCardData().getEncryptedAcctNumber()));
                sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CARD_NUMBER_MASKED, inQuotes(payment.getEncipheredCardData().getMaskedAcctNumber()));
            }
            else
            {
                sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_CUSTOMER_ACCOUNTID, inQuotes(payment.getReferenceNumber()));
            }
        }
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_ACCOUNT_CODE, inQuotes(transaction.getPayment()
                .getPaymentAccountType()));
        sql.addColumn(FIELD_PAYMENT_AGAINST_RECEIVABLE_AMOUNT, transaction.getPaymentAmount().getStringValue());
        sql.addColumn(FIELD_ACCOUNT_BALANCE_DUE, transaction.getPayment().getBalanceDue().getStringValue());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertPaymentLineItem", e);
        }
    }

    /**
     * Inserts payment info into a payment line item table.
     * Uses <code>transaction</code> to obtain the primary keys.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @exception DataException upon error
     */
    public void insertPaymentLineItem(JdbcDataConnection dataConnection, PaymentTransactionIfc transaction)
            throws DataException
    {
        insertPaymentLineItem(dataConnection, transaction, TABLE_PAYMENTONACCOUNT_LINE_ITEM);
    }

    /**
     * Inserts a Sale Return Line Item.
     * A line item component of a Retail transaction that records the exchange
     * in ownership of a merchandise item (i.e. a sale or return) or the sale or
     * refund related to a service.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @exception DataException upon error
     */
    public void insertSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        insertSaleReturnLineItem(dataConnection, transaction, lineItem, TYPE_SALE_RETURN);
    }

    /**
     * Inserts a Sale Return Line Item.
     * A line item component of a Retail transaction that records the exchange
     * in ownership of a merchandise item (i.e. a sale or return) or the sale or
     * refund related to a service.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @param lineItemTypeCode line item type code
     * @exception DataException upon error
     */
    public void insertSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, String lineItemTypeCode) throws DataException
    {
        insertRetailTransactionLineItem(dataConnection, transaction, lineItem.getLineNumber(), lineItemTypeCode);

        // Don't save the sale components of price adjustments or price
        // adjustment instances.
        // We'll update the sale components in
        // UpdatePriceAdjustedLineItemsTransaction
        if (lineItem.isPriceAdjustmentLineItem())
        {
            return;
        }

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
        sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
        sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
        sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
        sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
        sql
                .addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_DISCOUNTED_AMOUNT,
                        getItemExtendedDiscountedAmount(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
        sql
                .addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount()
                        .getStringValue());
        sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
        sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
        sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));
        sql.addColumn(FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE, getReturnItemConditionCode(lineItem));

        sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
        sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
        sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
        sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));
        
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
        sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
        sql.addColumn(FIELD_SHIPPING_CHARGE_FLAG, getShippingChargeFlag(lineItem));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
        sql.addColumn(FIELD_ORDER_ID, inQuotes(getOrderID(transaction, lineItem)));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().getIxRetailCode()));
        sql.addColumn(FIELD_SIZE_CODE, getItemSizeCode(lineItem));

        sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
        sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
        sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG, getSalesAssociateModifiedFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION, getReceiptDescription(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE, getReceiptDescriptionLocal(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG, getRestockingFeeFlag(lineItem));
        sql.addColumn(FIELD_SERIALIZED_ITEM_VALIDATION_FLAG, getSerializedItemFlag(lineItem));
        sql.addColumn(FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG, getExternalValidationSerializedItemFlag(lineItem));
        sql.addColumn(FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN, isPOSAllowedToCreateUIN(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE, getProductGroupID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG, getSizeRequiredFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE, getLineItemUOMCode(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID, getPosDepartmentID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID, getItemTypeID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG, getReturnProhibited(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG, getEmployeeDiscountAllowed(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG, getTaxable(lineItem));
        sql.addColumn(FIELD_ITEM_DISCOUNT_FLAG, getDiscountable(lineItem));
        sql.addColumn(FIELD_ITEM_DAMAGE_DISCOUNT_FLAG, getDamageDiscountable(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID, getMerchandiseHierarchyGroupID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC, getManufacturerItemUPC(lineItem));
        sql.addColumn(FIELD_CLEARANCE_INDICATOR, getClearanceIndicator(lineItem));
        sql.addColumn(NON_RETRIEVED_ORIGINAL_RECEIPT_ID, getUserSuppliedReceiptID(lineItem));
        sql.addColumn(FIELD_SALE_AGE_RESTRICTION_ID, getRestrictiveAge(lineItem));
        sql.addColumn(FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG, getPriceEntryRequired(lineItem));
        
        String extendedRestockingFee = getItemExtendedRestockingFee(lineItem);
        if (extendedRestockingFee != null)
        {
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT, extendedRestockingFee);
        }
        
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT, lineItem.getDepositAmount().getStringValue());
        
        if (lineItem instanceof OrderLineItemIfc)
        {
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE, ((OrderLineItemIfc)lineItem).getItemBalanceDue().getStringValue());
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE, makeCharFromBoolean(((OrderLineItemIfc)lineItem).isPriceCancelledDuringPickup()));
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE, makeCharFromBoolean(((OrderLineItemIfc)lineItem).isInStorePriceDuringPickup()));
        }
        
        if (lineItem.isKitHeader())
        {
            sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER));
            sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemID(lineItem));
            sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
        }
        else if (lineItem.isKitComponent())
        {
            sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT));
            sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemKitID((KitComponentLineItemIfc) lineItem));
            sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
        }

        if (lineItem.isPriceAdjustmentLineItem())
        {
            sql.addColumn(FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG, makeCharFromBoolean(true));
            sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
        }
        else if (lineItem.isPartOfPriceAdjustment())
        {
            sql.addColumn(FIELD_ITEM_PRICEADJ_REFERENCE_ID, lineItem.getPriceAdjustmentReference());
        }

        if (transaction instanceof SaleReturnTransactionIfc)
        {
            if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED ||
                    transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED)
            {
                ReturnItemIfc theReturnItem = lineItem.getReturnItem();
                if (theReturnItem != null)
                {
                    boolean wasRetrieved = theReturnItem.isFromRetrievedTransaction();
                    if (wasRetrieved)
                    {
                        sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'1'");
                    }
                    else
                    {
                        sql.addColumn(FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG, "'0'");
                    }
                }
            }
        }
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertSaleReturnLineItem", e);
        }

        saveRetailPriceModifiers(dataConnection, transaction, lineItem);
        saveSaleReturnTaxModifier(dataConnection, transaction, lineItem);

        saveSaleReturnLineItemTaxInformation(dataConnection, transaction, lineItem);
        saveExternalOrderLineItem(dataConnection, transaction, lineItem);

        /*
         * Track commission properly
         */
        String employee = transaction.getSalesAssociate().getEmployeeID();
        if (lineItem.getSalesAssociate() != null && !employee.equals(lineItem.getSalesAssociate().getEmployeeID()))
        {
            saveCommissionModifier(dataConnection, transaction, lineItem);
        }

        /*
         * See if it's an unknown item that we need to save
         */
        if (lineItem.getPLUItem() instanceof UnknownItemIfc)
        {
            saveUnknownItem(dataConnection, transaction, lineItem);
        }
    }

    /**
     * Returns the string value to be used in the database for
     * clearance flag
     *
     * @param the lineItem
     * @return string
     */
    public String getClearanceIndicator(SaleReturnLineItemIfc lineItem)
    {
        if (lineItem.isOnClearance())
            return ("'1'");
        return ("'0'");
    }
    
    /**
     * Returns the string value to be used in the database for
     * price entry required flag
     *
     * @param the lineItem
     * @return string
     */
    protected String getPriceEntryRequired(SaleReturnLineItemIfc lineItem)
    {
        if (lineItem.getPLUItem().getItemClassification().isPriceEntryRequired())
            return  ("'1'");
        else
            return ("'0'");
    }    

    /**
     * Returns the string value to be used in the database for
     * the user supplied receipt ID.
     *
     * @param the lineItem
     * @return string
     */
    public String getUserSuppliedReceiptID(SaleReturnLineItemIfc lineItem)
    {
        String value = "null";

        // If there is no return item, return the default value.
        if (lineItem.getReturnItem() != null)
        {
            // If there is no userSuppliedReceiptID, return the default value.
            if (lineItem.getReturnItem().getNonRetrievedOriginalReceiptId() != null)
            {
                value = inQuotes(lineItem.getReturnItem().getNonRetrievedOriginalReceiptId());
            }
        }

        return (value);
    }

	/**
     * Inserts a deleted Sale Return Line Item.
     * A line item component of a Retail transaction that records the exchange
     * in ownership of a merchandise item (i.e. a sale or return) or the sale or
     * refund related to a service.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @exception DataException upon error
     */
    public void insertDeletedSaleReturnLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_GIFT_REGISTRY_ID, getGiftRegistryString(lineItem));
        sql.addColumn(FIELD_ITEM_ID, getItemID(lineItem));
        sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
        sql.addColumn(FIELD_SERIAL_NUMBER, getItemSerial(lineItem));
        sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
        sql
                .addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_DISCOUNTED_AMOUNT,
                        getItemExtendedDiscountedAmount(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
        sql
                .addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount()
                        .getStringValue());
        sql.addColumn(FIELD_MERCHANDISE_RETURN_FLAG, getReturnFlag(lineItem));
        sql.addColumn(FIELD_MERCHANDISE_RETURN_REASON_CODE, getReturnReasonCode(lineItem));

        sql.addColumn(FIELD_POS_ORIGINAL_TRANSACTION_ID, getOriginalTransactionId(lineItem));
        sql.addColumn(FIELD_ORIGINAL_BUSINESS_DAY_DATE, getOriginalDate(lineItem));
        sql.addColumn(FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getOriginalLineNumber(lineItem));
        sql.addColumn(FIELD_ORIGINAL_RETAIL_STORE_ID, getOriginalStoreID(lineItem));

        sql.addColumn(FIELD_POS_DEPARTMENT_ID, getDepartmentID(lineItem));
        sql.addColumn(FIELD_SEND_FLAG, getSendFlag(lineItem));
        sql.addColumn(FIELD_SEND_LABEL_COUNT, getSendLabelCount(lineItem));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_GIFT_RECEIPT_FLAG, getGiftReceiptFlag(lineItem));
        sql.addColumn(FIELD_ORDER_ID, inQuotes(getOrderID(transaction, lineItem)));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ITEM_ID_ENTRY_METHOD_CODE, inQuotes(lineItem.getEntryMethod().getIxRetailCode()));
        sql.addColumn(FIELD_SIZE_CODE, getItemSizeCode(lineItem));

        sql.addColumn(FIELD_RETURN_RELATED_ITEM_FLAG, getReturnRelatedItemFlag(lineItem));
        sql.addColumn(FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER, getRelatedSeqNum(lineItem));
        sql.addColumn(FIELD_REMOVE_RELATED_ITEM_FLAG, getRemoveRelatedItemFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE, getPermanentSellingPrice(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION, getReceiptDescription(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE, getReceiptDescriptionLocal(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG, getRestockingFeeFlag(lineItem));
        sql.addColumn(FIELD_SERIALIZED_ITEM_VALIDATION_FLAG, getSerializedItemFlag(lineItem));
        sql.addColumn(FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG, getExternalValidationSerializedItemFlag(lineItem));
        sql.addColumn(FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN, isPOSAllowedToCreateUIN(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE, getProductGroupID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG, getSizeRequiredFlag(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE, getLineItemUOMCode(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID, getPosDepartmentID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID, getItemTypeID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG, getReturnProhibited(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG, getEmployeeDiscountAllowed(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG, getTaxable(lineItem));
        sql.addColumn(FIELD_ITEM_DISCOUNT_FLAG, getDiscountable(lineItem));
        sql.addColumn(FIELD_ITEM_DAMAGE_DISCOUNT_FLAG, getDamageDiscountable(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VOID_FLAG, makeSafeString("1"));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID, getMerchandiseHierarchyGroupID(lineItem));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC, getManufacturerItemUPC(lineItem));
        sql.addColumn(FIELD_CLEARANCE_INDICATOR, getClearanceIndicator(lineItem));
        sql.addColumn(NON_RETRIEVED_ORIGINAL_RECEIPT_ID, getUserSuppliedReceiptID(lineItem));
        sql.addColumn(FIELD_SALE_AGE_RESTRICTION_ID, getRestrictiveAge(lineItem));
        sql.addColumn(FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG, getPriceEntryRequired(lineItem));

        String extendedRestockingFee = getItemExtendedRestockingFee(lineItem);

        if (extendedRestockingFee != null)
        {
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT, extendedRestockingFee);
        }
        
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT, lineItem.getDepositAmount().getStringValue());
        
        if (lineItem instanceof OrderLineItemIfc)
        {
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE, ((OrderLineItemIfc)lineItem).getItemBalanceDue().getStringValue());
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE, makeCharFromBoolean(((OrderLineItemIfc)lineItem).isPriceCancelledDuringPickup()));
            sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE, makeCharFromBoolean(((OrderLineItemIfc)lineItem).isInStorePriceDuringPickup()));            
        }
        
        if (lineItem.isKitHeader())
        {
            sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER));
            sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemID(lineItem));
            sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
        }
        else if (lineItem.isKitComponent())
        {
            sql.addColumn(FIELD_ITEM_KIT_SET_CODE, inQuotes(ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT));
            sql.addColumn(FIELD_ITEM_COLLECTION_ID, getItemKitID((KitComponentLineItemIfc) lineItem));
            sql.addColumn(FIELD_ITEM_KIT_HEADER_REFERENCE_ID, lineItem.getKitHeaderReference());
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertDeletedSaleReturnLineItem", e);
        }

        saveRetailPriceModifiers(dataConnection, transaction, lineItem);
        saveSaleReturnTaxModifier(dataConnection, transaction, lineItem);
        saveExternalOrderLineItem(dataConnection, transaction, lineItem);

        /*
         * Track commission properly
         */
        String employee = transaction.getSalesAssociate().getEmployeeID();
        if (lineItem.getSalesAssociate() != null && !employee.equals(lineItem.getSalesAssociate().getEmployeeID()))
        {
            saveCommissionModifier(dataConnection, transaction, lineItem);
        }

        /*
         * See if it's an unknown item that we need to save
         */
        if (lineItem.getPLUItem() instanceof UnknownItemIfc)
        {
            saveUnknownItem(dataConnection, transaction, lineItem);
        }

        if (lineItem.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            insertGiftCard(dataConnection, transaction, lineItem);
        }
    }

    /**
     * Inserts an unknown item.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @exception DataException upon error
     */
    public void insertUnknownItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_UNKNOWN_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_POS_ITEM_ID, getItemID(lineItem));
        sql.addColumn(FIELD_UNKNOWNITEM_CURRENT_SALE_UNIT_POS_RETAIL_PRICE_AMOUNT, getItemPrice(lineItem));
        sql.addColumn(FIELD_ITEM_DESCRIPTION, getItemDescription(lineItem));
        sql.addColumn(FIELD_TAX_GROUP_ID, getTaxGroupID(lineItem));
        sql.addColumn(FIELD_ITEM_TAX_EXEMPT_CODE, inQuotes(getItemTaxable(lineItem)));
        String deptID = lineItem.getPLUItem().getDepartmentID();
        if (!Util.isEmpty(deptID))
        {
            sql.addColumn(FIELD_POS_DEPARTMENT_ID, inQuotes(deptID));
        }
        sql.addColumn(FIELD_UNIT_OF_MEASURE_CODE, getUOMCode(lineItem));

        // mark the type of unknown item as gift certificate
        if (lineItem.getPLUItem() instanceof GiftCertificateItemIfc)
        {
            sql.addColumn(FIELD_UNKNOWN_ITEM_TYPE, makeSafeString(GIFT_CERTIFICATE));
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertUnknownItem", e);
        }
    }

    /**
     * Inserts a gift certificate.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @exception DataException upon error
     * @deprecated in 14.0; use oracle.retail.stores.domain.arts.JdbcSaveRetailTransactionLineItems.saveGiftCertificateDocument()
     */
    public void insertGiftCertificate(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        JdbcSaveCertificate saveCertificateOperation = new JdbcSaveCertificate();
        saveCertificateOperation.saveSaleReturnLineItem(dataConnection, transaction, lineItem);
    }

    /**
     * Inserts a commission modifier.
     * A modifier that captures earned commissions by an employee involved in
     * serving the customer purchasing the merchandise or service item
     * identified in the Sale/Return Line Item.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @exception DataException upon error
     */
    public void insertCommissionModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_COMMISSION_MODIFIER);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_EMPLOYEE_ID, getEmployeeID(lineItem));
        sql.addColumn(FIELD_COMMISSION_MODIFIER_SEQUENCE_NUMBER, getSequenceNumber(0));
        sql.addColumn(FIELD_COMMISSION_AMOUNT_PERCENT_FLAG, "'P'");
        sql.addColumn(FIELD_COMMISSION_MODIFIER_PERCENT, "100");

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertCommissionModifier", e);
        }
    }

    /**
     * Inserts a price modifier. Used for item discount information.
     * A line item modifier that reflects a modification of the retail selling
     * price. It is provided by PLU through the application of a predefined
     * price change rule that depends on parameters provided during the sale
     * transaction. Examples of the kinds of parameters provided in this
     * scenario include: number of items purchased, customer (or shopper)
     * affiliation, etc.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @param sequenceNumber The sequence number of the modifier
     * @param discountLineItem optional discount information
     * @exception DataException upon error
     */
    public void insertRetailPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_RETAIL_PRICE_MODIFIER);

        // Get the line and transaction number.
        String lineNumber = getLineItemSequenceNumber(lineItem);
        String tranNumber = getTransactionSequenceNumber(transaction);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, tranNumber);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, lineNumber);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, getSequenceNumber(sequenceNumber));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        if (discountLineItem != null)
        { // Item discount
            // This is the item extended discount amount for this discount strategy. It can be negative for return items
            sql.addColumn(FIELD_RETAIL_PRICE_EXTENDED_DISCOUNT_AMOUNT,
                    discountLineItem.getItemDiscountAmount().getStringValue());
            sql.addColumn(FIELD_RETAIL_PRICE_USE_EXTENDED_DISCOUNT_FLAG,
                    getPriceModifierUseExtendedDiscountFlag(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE, getPriceModifierScopeCode(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
                    getPriceModifierAssignmentBasis(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID, makeSafeString(discountLineItem
                    .getDiscountEmployeeID()));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
                    getPriceModifierDamageDiscountFlag(discountLineItem));
            sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
            sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
                    getPriceModifierReferenceID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
                    getPriceModifierReferenceIDTypeCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
                    inQuotes(discountLineItem.getAccountingMethod()));
            sql.addColumn(FIELD_PROMOTION_ID, discountLineItem.getPromotionId());
            sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, discountLineItem.getPromotionComponentId());
            sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, discountLineItem.getPromotionComponentDetailId());
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, discountLineItem.getPricingGroupID());
            sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_REFERENCE, discountLineItem.getOrderItemDiscountLineReference());
        }
        else
        { // Price Override
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
            // if security override data exists, use it
            SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
            if (priceOverrideAuthorization != null)
            {
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
                        makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
                        .getEntryMethod().getLegacyCode());
            }
            sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_REFERENCE, "-1");
        }
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertRetailPriceModifier", e);
        }
    }

    /**
     * Inserts a tax modifier. Used for item tax information.
     * A modifier to record tax or tax exemption by line item.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @param taxSequenceNumber The sequence number of the tax line item
     * @param taxLineItem tax information
     * @exception DataException upon error
     */
    public void insertSaleReturnTaxModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, int taxSequenceNumber, ItemTaxIfc taxLineItem) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_TAX_MODIFIER);

        // this table only is only inserted when there is a manual tax update. The number
        // of lines is always equal to one.
        int taxGroupId = taxLineItem.getTaxGroupId();
        if ( taxLineItem.getTaxInformationContainer() != null )
        {
        	if ( taxLineItem.getTaxInformationContainer().getTaxInformation() != null )
        	{
        		if ( taxLineItem.getTaxInformationContainer().getTaxInformation().length > 0 )
        		{
        			taxGroupId = taxLineItem.getTaxInformationContainer().getTaxInformation()[0].getTaxGroupID();
        		}
        	}
        }

        // set line item tax reason code to external and override rate to the
        // external rate used, if externally calculated and no line item
        // overrides.....
        // external tax mgr
        if (taxLineItem.getExternalTaxEnabled() && taxLineItem.getOverrideRate() == 0.0
                && taxLineItem.getOverrideAmount().signum() == CurrencyIfc.ZERO)
        {
            taxLineItem.getReason().setCode(Integer.toString(TaxIfc.TAX_MODE_EXTERNAL_RATE));
            taxLineItem.setExternalOverrideRate(taxLineItem.getDefaultRate());
            taxLineItem.setDefaultRate(0.0);
        }

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_TAX_MODIFIER_SEQUENCE_NUMBER, getSequenceNumber(taxSequenceNumber));
        sql.addColumn(FIELD_SALE_RETURN_TAX_AMOUNT, getItemTaxAmount(lineItem));
        sql.addColumn(FIELD_TAX_GROUP_ID, taxGroupId);
        sql.addColumn(FIELD_TAX_TYPE_CODE, getItemTaxMode(taxLineItem));
        sql.addColumn(FIELD_TAX_PERCENT, getItemTaxPercent(taxLineItem));
        sql.addColumn(FIELD_TAX_OVERRIDE_PERCENT, getItemTaxOverridePercent(taxLineItem));
        sql.addColumn(FIELD_TAX_OVERRIDE_AMOUNT, getItemTaxOverrideAmount(taxLineItem));
        sql.addColumn(FIELD_SALE_RETURN_TAX_EXEMPTION_REASON_CODE, getItemTaxReasonCode(taxLineItem));
        sql.addColumn(FIELD_TAX_SCOPE_ID, getTaxScope(taxLineItem));
        // sql.addColumn(FIELD_TAX_METHOD_ID, getItemTaxMethod(taxLineItem)); -
        // external tax mgr
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertSaleReturnTaxModifier", e);
        }
    }

    /**
     * Record tax information for each line item in the sale return transaction.
     * An entry in the table is stored for each item in the line items
     * taxInformationContainer. In general the container will only have one item
     * in it, but for multiple jurisdiction taxes more than one tax rule may
     * exist for a particular line item.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem sale/return line item
     * @throws DataException if there is an error saving to the database
     */
    public void saveSaleReturnLineItemTaxInformation(JdbcDataConnection dataConnection,
            RetailTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {

        TaxInformationIfc[] taxInfo = lineItem.getTaxInformationContainer().getTaxInformation();
        if (taxInfo != null)
        {
            for (int i = 0; i < taxInfo.length; i++)
            {
                insertSaleReturnTaxLineItem(dataConnection, transaction, lineItem, taxInfo[i]);
            }
        }

        if (transaction instanceof OrderTransactionIfc)
        {
            saveOrderTaxInformation(dataConnection, (OrderTransactionIfc)transaction, lineItem);
        }
    }

    /**
     * Insert a SaleReturnLineItem's tax information into the database. This
     * save the tax rule that the taxInfo is applying.
     *
     * @param dataConnection Connection to the database
     * @param transaction Transaction this line item is part of
     * @param lineItem The lien item being saved
     * @param taxInfo Information about tax charged on that line item
     * @throws DataException If database save fails.
     */
    public void insertSaleReturnTaxLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, TaxInformationIfc taxInfo) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_TAX_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_TAX_AUTHORITY_ID, taxInfo.getTaxAuthorityID());
        sql.addColumn(FIELD_TAX_GROUP_ID, taxInfo.getTaxGroupID());
        sql.addColumn(FIELD_TAX_TYPE, taxInfo.getTaxTypeCode());
        sql.addColumn(FIELD_TAX_HOLIDAY, makeStringFromBoolean(taxInfo.getTaxHoliday()));
        sql.addColumn(FIELD_TAXABLE_SALE_RETURN_AMOUNT, taxInfo.getTaxableAmount().toString());
        sql.addColumn(FIELD_SALE_RETURN_TAX_AMOUNT, taxInfo.getTaxAmount().toString());
        sql.addColumn(FIELD_ITEM_TAX_AMOUNT_TOTAL, getItemTaxAmount(lineItem));
        sql.addColumn(FIELD_ITEM_TAX_INC_AMOUNT_TOTAL, getItemInclusiveTaxAmount(lineItem));
        sql.addColumn(FIELD_TAX_AUTHORITY_NAME, makeSafeString(taxInfo.getTaxAuthorityName()));
        sql.addColumn(FIELD_TAX_RULE_NAME, makeSafeString(taxInfo.getTaxRuleName()));
        sql.addColumn(FIELD_TAX_PERCENTAGE, String.valueOf(taxInfo.getTaxPercentage().floatValue()));
        sql.addColumn(FIELD_TAX_MODE, taxInfo.getTaxMode());
        sql.addColumn(FIELD_FLG_TAX_INCLUSIVE, makeStringFromBoolean(taxInfo.getInclusiveTaxFlag()));
        if (taxInfo.getUniqueID() != null && !taxInfo.getUniqueID().equals(""))
        {
            sql.addColumn(FIELD_UNIQUE_ID, makeSafeString(taxInfo.getUniqueID()));
        }
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertSaleReturnTaxLineItem", e);
        }
    }


    /**
     * There two tables that associated with tax on orders that can be saved.  If the
     * the order is an in-store order, the running totals of amount cancelled, amount
     * completed and amount return must be saved to the Order Line Item Tax table.
     *
     * In all cases a snap shot of these totals at transaction time will be saved to the
     * Sale Return Order Line Item Tax table.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */
    protected void saveOrderTaxInformation(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        //OrderLineItemRetailPriceModifier table should not contain Cross Channel items or suspended items
        if ( !(lineItem.getOrderItemStatus().isCrossChannelItem() || orderTransaction.isSuspended()) )  
        {
            if (orderTransaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                insertOrderLineItemTax(dataConnection, orderTransaction, lineItem);
            }
            else if (lineItem.isReturnLineItem())
            {
                updateOrderLineItemTax(dataConnection, orderTransaction, lineItem);
            }
            else if (lineItem.getOrderItemStatus().getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
            {
                // insert tax status for take with items added during order pickup
                insertOrderLineItemTax(dataConnection, orderTransaction, lineItem);
            }
            else
            {
                updateOrderLineItemTax(dataConnection, orderTransaction, lineItem);
            }
        }

        try
        {
            insertSaleReturnOrderLineItemTax(dataConnection, orderTransaction, lineItem);
        }
        catch (DataException e)
        {
            updateSaleReturnOrderLineItemTax(dataConnection, orderTransaction, lineItem);
        }
    }

    /**
     * Insert the tax identifiers and the totals into the Order Line Item Tax table.
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */
    protected void insertOrderLineItemTax(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        List<OrderItemTaxStatusIfc> taxStatusList = itemStatus.getTaxStatusList();

        for(OrderItemTaxStatusIfc oits: taxStatusList)
        {
            // set up SQL
            SQLInsertStatement sql = new SQLInsertStatement();
            sql.setTable(TABLE_ORDER_LINE_ITEM_TAX);

            sql.addColumn(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
            sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
            sql.addColumn(FIELD_TAX_AUTHORITY_ID, oits.getAuthorityID());
            sql.addColumn(FIELD_TAX_GROUP_ID,oits.getTaxGroupID());
            sql.addColumn(FIELD_TAX_TYPE, oits.getTypeCode());
            sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, inQuotes(itemStatus.getOriginalTransactionId().getStoreID()));
            sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, inQuotes(itemStatus.getOriginalTransactionId().getWorkstationID()));
            sql.addColumn(FIELD_TOTAL_TAX_AMOUNT, oits.getTotalAmount().toString());
            sql.addColumn(FIELD_COMPLETED_TAX_AMOUNT, oits.getCompletedAmount().toString());
            sql.addColumn(FIELD_CANCELLED_TAX_AMOUNT, oits.getCancelledAmount().toString());
            sql.addColumn(FIELD_RETURNED_TAX_AMOUNT, oits.getReturnedAmount().toString());
            
            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(de.toString());
                throw de;
            }
            catch (Exception e)
            {
                logger.error("" + Util.throwableToString(e) + "");

                throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
            }
        }
    }

    /**
     * Update the totals in the Order Line Item Tax table.
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */
    protected void updateOrderLineItemTax(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        List<OrderItemTaxStatusIfc> taxStatusList = itemStatus.getTaxStatusList();

        for(OrderItemTaxStatusIfc oits: taxStatusList)
        {
            // set up SQL
            SQLUpdateStatement sql = new SQLUpdateStatement();
            sql.setTable(TABLE_ORDER_LINE_ITEM_TAX);

            sql.addColumn(FIELD_TOTAL_TAX_AMOUNT, oits.getTotalAmount().toString());
            sql.addColumn(FIELD_COMPLETED_TAX_AMOUNT, oits.getCompletedAmount().toString());
            sql.addColumn(FIELD_CANCELLED_TAX_AMOUNT, oits.getCancelledAmount().toString());
            sql.addColumn(FIELD_RETURNED_TAX_AMOUNT, oits.getReturnedAmount().toString());

            sql.addQualifier(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
            sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
            sql.addQualifier(FIELD_TAX_AUTHORITY_ID, oits.getAuthorityID());
            sql.addQualifier(FIELD_TAX_GROUP_ID, oits.getTaxGroupID());
            sql.addQualifier(FIELD_TAX_TYPE, oits.getTypeCode());
            sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()) );
            sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()));

            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(de.toString());
                throw de;
            }
            catch (Exception e)
            {
                logger.error("" + Util.throwableToString(e) + "");

                throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
            }
        }
    }

    /**
     * Insert the transaction/order identifiers and the totals into the Sale Return
     * Order Line Item Tax table.
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */
    protected void insertSaleReturnOrderLineItemTax(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        List<OrderItemTaxStatusIfc> taxStatusList = itemStatus.getTaxStatusList();

        for(OrderItemTaxStatusIfc oits: taxStatusList)
        {
            // set up SQL
            SQLInsertStatement sql = new SQLInsertStatement();
            sql.setTable(TABLE_SALE_RETURN_ORDER_TAX_LINE_ITEM);

            sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
            sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
            sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
            sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
            sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
            sql.addColumn(FIELD_TAX_AUTHORITY_ID, oits.getAuthorityID());
            sql.addColumn(FIELD_TAX_GROUP_ID,oits.getTaxGroupID());
            sql.addColumn(FIELD_TAX_TYPE, oits.getTypeCode());
            sql.addColumn(FIELD_TOTAL_TAX_AMOUNT, oits.getTotalAmount().toString());
            sql.addColumn(FIELD_COMPLETED_TAX_AMOUNT, oits.getCompletedAmount().toString());
            sql.addColumn(FIELD_CANCELLED_TAX_AMOUNT, oits.getCancelledAmount().toString());
            sql.addColumn(FIELD_RETURNED_TAX_AMOUNT, oits.getReturnedAmount().toString());
            sql.addColumn(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
            sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
            
            TransactionIDIfc originalTxnId = itemStatus.getOriginalTransactionId();
            if (originalTxnId!=null)
            {
                sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, inQuotes(originalTxnId.getStoreID()));
                sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, inQuotes(originalTxnId.getWorkstationID()));
            }     
           
            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(de.toString());
                throw de;
            }
            catch (Exception e)
            {
                logger.error("" + Util.throwableToString(e) + "");

                throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
            }
        }
    }

    /**
     * Update the totals to the Sale Return Order Line Item Tax table.
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */

    protected void updateSaleReturnOrderLineItemTax(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        List<OrderItemTaxStatusIfc> taxStatusList = lineItem.getOrderItemStatus().getTaxStatusList();

        for(OrderItemTaxStatusIfc oits: taxStatusList)
        {
            // set up SQL
            SQLUpdateStatement sql = new SQLUpdateStatement();
            sql.setTable(TABLE_SALE_RETURN_ORDER_TAX_LINE_ITEM);

            sql.addColumn(FIELD_TOTAL_TAX_AMOUNT, oits.getTotalAmount().toString());
            sql.addColumn(FIELD_COMPLETED_TAX_AMOUNT, oits.getCompletedAmount().toString());
            sql.addColumn(FIELD_CANCELLED_TAX_AMOUNT, oits.getCancelledAmount().toString());
            sql.addColumn(FIELD_RETURNED_TAX_AMOUNT, oits.getReturnedAmount().toString());

            sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
            sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
            sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
            sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
            sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
            sql.addQualifier(FIELD_TAX_AUTHORITY_ID, oits.getAuthorityID());
            sql.addQualifier(FIELD_TAX_GROUP_ID,oits.getTaxGroupID());
            sql.addQualifier(FIELD_TAX_TYPE, oits.getTypeCode());

            try
            {
                dataConnection.execute(sql.getSQLString());
            }
            catch (DataException de)
            {
                logger.error(de.toString());
                throw de;
            }
            catch (Exception e)
            {
                logger.error("" + Util.throwableToString(e) + "");

                throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
            }
        }
    }

    /**
     * Inserts a tax line. Used for transaction level tax information.
     * A line item component of a Retail Transaction that records the charging
     * and offsetting liability credit for sales tax on merchandise items and
     * services sold by the store or debit for merchandise returned to the
     * store.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The Transaction that contains the line item
     * @param lineItemSequenceNumber The sequence number of the sales line item
     * @exception DataException upon error
     */
    public void insertTaxLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber) throws DataException
    {
        insertRetailTransactionLineItem(dataConnection, transaction, lineItemSequenceNumber, TYPE_TAX);

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_TAX_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
        sql.addColumn(FIELD_TAX_AMOUNT, getTaxAmount(transaction));
        sql.addColumn(FIELD_TAX_INC_AMOUNT, getInclusiveTaxAmount(transaction));
        sql.addColumn(FIELD_TAX_TYPE_CODE, getTaxMode(transaction));
        sql.addColumn(FIELD_TAX_PERCENT, getTaxPercent(transaction));
        sql.addColumn(FIELD_TAX_OVERRIDE_PERCENT, getTaxOverridePercent(transaction));
        sql.addColumn(FIELD_TAX_OVERRIDE_AMOUNT, getTaxOverrideAmount(transaction));
        sql.addColumn(FIELD_TAX_REASON_CODE, getTaxReasonCode(transaction));
        // sql.addColumn(FIELD_GEO_CODE, getTaxGeoCode(transaction));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertTaxLineItem", e);
        }
    }

    /**
     * Inserts a tax exemption modifier. Used for transaction level tax exempt
     * information.
     * A line item modifier to the Tax Line Item component of a Retail
     * Transaction that provides supplementary data regarding tax exemptions and
     * exceptions for a specific sales tax line item.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the tax line item
     * @exception DataException upon error
     */
    public void insertTaxExemptionModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_TAX_EXEMPTION_MODIFIER);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
        sql.addColumn(FIELD_ENCRYPTED_TAX_MODIFIER_TAX_EXEMPTION_CERTIFICATE_NUMBER, getTaxExemptCertificateID(transaction));
        sql.addColumn(FIELD_MASKED_TAX_MODIFIER_TAX_EXEMPTION_CERTIFICATE_NUMBER, getMaskedTaxExemptCertificateID(transaction));
        sql.addColumn(FIELD_TAX_MODIFIER_TAX_EXEMPTION_REASON_CODE, getTaxReasonCode(transaction));
        sql.addColumn(FIELD_TAX_MODIFIER_TAX_EXEMPTION_AMOUNT, getTaxExemptAmount(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertTaxExemptionModifier", e);
        }
    }

    /**
     * Inserts a discount line.
     * A line item component of a Retail Transaction that records the granting
     * of a reduction of price on items and/or services purchased by a customer
     * and treats that price reduction as an expense item for accounting
     * purposes.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItemSequenceNumber The sequence number of the line item
     * @param lineItem discount information
     * @exception DataException upon error
     */
    public void insertDiscountLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            int lineItemSequenceNumber, TransactionDiscountStrategyIfc lineItem) throws DataException
    {
        /*
         * Update the Retail Transaction Line Item table first
         */
        insertRetailTransactionLineItem(dataConnection, transaction, lineItemSequenceNumber, TYPE_DISCOUNT);

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_DISCOUNT_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItemSequenceNumber));
        sql.addColumn(FIELD_DISCOUNT_REASON_CODE, getDiscountReasonCode(lineItem));
        sql.addColumn(FIELD_DISCOUNT_TYPE_CODE, getDiscountType(lineItem));
        sql.addColumn(FIELD_DISCOUNT_AMOUNT, getDiscountAmount(lineItem));
        sql.addColumn(FIELD_DISCOUNT_PERCENT, getDiscountPercent(lineItem));
        sql.addColumn(FIELD_DISCOUNT_ASSIGNMENT_BASIS, getDiscountAssignmentBasis(lineItem));
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID,
                makeSafeString(lineItem.getDiscountEmployeeID()));
        sql.addColumn(FIELD_DISCOUNT_ENABLED, getDiscountEnabled(lineItem));
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_INCLUDED_IN_BEST_DEAL_FLAG, getIncludedInBestDealFlag(lineItem));
        sql.addColumn(FIELD_DISCOUNT_RULE_ID, getDiscountRuleID(lineItem));
        sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID, getDiscountReferenceID(lineItem));
        sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID_TYPE_CODE, getDiscountReferenceIDTypeCode(lineItem));
        sql.addColumn(FIELD_PROMOTION_ID, getPromotionID(lineItem));
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID,getPricingGroupID(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertDiscountLineItem", e);
        }
    }

    /**
     * Removes entries from order-line-item table.
     *
     * @param dataConnection connection to database
     * @param orderTransaction order transaction
     * @exception DataException thrown if error occurs
     */
    public void removeOrderLineItems(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction)
            throws DataException
    {
        SQLDeleteStatement sql = new SQLDeleteStatement();

        sql.setTable(TABLE_ORDER_ITEM);

        sql.addQualifier(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                logger.error(de.toString());
                throw de;
            }
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "removeOrderLineItems", e);
        }

    }

    /**
     * Inserts an order line item.
     *
     * @param dataConnection connection to database
     * @param orderTransaction order transaction
     * @param lineItem line item
     * @param lineItemSequenceNumber index into line item array
     * @exception DataException thrown if error occurs
     */
    public void insertOrderLineItem(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            SaleReturnLineItemIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        // save Pickup/Delivery Order Line Item Status data into table
        insertPickupDeliveryOrderLineItemStatus(dataConnection, orderTransaction, lineItem, lineItemSequenceNumber);

        // Only create an order item system record if it is not a cross channel or a suspended item.
        // ORPOS is not the system of records for cross channel order items.
        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        if (itemStatus.isCrossChannelItem() || orderTransaction.isSuspended())
            return;
        
        // pull out order status
        OrderStatusIfc orderStatus = orderTransaction.getOrderStatus();
        TransactionIDIfc originalTxnId = itemStatus.getOriginalTransactionId();

        // set up SQL
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_ORDER_ITEM);

        sql.addColumn(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, inQuotes(originalTxnId.getStoreID()));
        sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, inQuotes(originalTxnId.getWorkstationID()));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderStatus.getTimestampBegin().dateValue()));
        sql.addColumn(FIELD_ITEM_STATUS, Integer.toString(itemStatus.getStatus().getStatus()));
        sql.addColumn(FIELD_ITEM_STATUS_PREVIOUS, Integer.toString(itemStatus.getStatus().getPreviousStatus()));
        sql.addColumn(FIELD_PARTY_ID, "0");
        sql.addColumn(FIELD_ITEM_ID, inQuotes(lineItem.getItemID()));
        sql.addColumn(FIELD_POS_ITEM_ID, inQuotes(lineItem.getPosItemID()));
        sql.addColumn(FIELD_POS_DEPARTMENT_ID, inQuotes(lineItem.getPLUItem().getDepartmentID()));
        sql.addColumn(FIELD_TAX_GROUP_ID, Integer.toString(lineItem.getTaxGroupID()));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount()
                        .getStringValue());
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(lineItemSequenceNumber));
        sql.addColumn(FIELD_ITEM_DESCRIPTION, getItemDescription(lineItem));
        if (itemStatus.getStatus().getLastStatusChange() != null)
        {
            sql.addColumn(FIELD_ORDER_STATUS_CHANGE, dateToSQLDateString(itemStatus.getStatus().getLastStatusChange()));
        }

        sql.addColumn(FIELD_ITEM_QUANTITY_ORDERED, itemStatus.getQuantityOrdered().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_PICKED_UP, itemStatus.getQuantityPickedUp().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_SHIPPED, itemStatus.getQuantityShipped().toString());
    	sql.addColumn(FIELD_ITEM_QUANTITY_NEW, itemStatus.getQuantityNew().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_PENDING, itemStatus.getQuantityPending().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_AVAILABLE, itemStatus.getQuantityPicked().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_CANCELLED, itemStatus.getQuantityCancelled().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_RETURNED, itemStatus.getQuantityReturned().toString());
        sql.addColumn(FIELD_ORDER_TOTAL_AMOUNT, itemStatus.getOrderedAmount().toString());
        sql.addColumn(FIELD_ORDER_COMPLETED_AMOUNT, itemStatus.getCompletedAmount().toString());
        sql.addColumn(FIELD_ORDER_CANCELLED_AMOUNT, itemStatus.getCancelledAmount().toString());
        sql.addColumn(FIELD_ORDER_RETURNED_AMOUNT, itemStatus.getReturnedAmount().toString());
        sql.addColumn(FIELD_ORDER_DEPOSIT_AMOUNT, itemStatus.getDepositAmount().toString());
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));

        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));

        // save item despositioncode, pickupdate, delivery order id
        sql.addColumn(FIELD_ORDER_ITEM_DISPOSITION, inQuotes(String.valueOf(itemStatus.getItemDispositionCode())));
        sql.addColumn(FIELD_PICKUP_ORDER_STORE, makeSafeString(itemStatus.getPickupStoreID())); // a special order or delivery item also needs to be picked up from a store
        if (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
        {
            // save delivery order id
            if (itemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY)
            {
                int deliveryId=lineItem.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID();
                sql.addColumn(FIELD_DELIVERY_ORDER_ID, Integer.toString(deliveryId));
            }
            // save pickup date
            if (itemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP)
            {
                sql.addColumn(FIELD_PICKUP_ORDER_DATE, getPickupDate(itemStatus));
                sql.addColumn(FIELD_PICKUP_ORDER_FNAME, makeSafeString(itemStatus.getPickupFirstName()));
                sql.addColumn(FIELD_PICKUP_ORDER_LNAME, makeSafeString(itemStatus.getPickupLastName()));
                sql.addColumn(FIELD_PICKUP_ORDER_PHONE, makeSafeString(itemStatus.getPickupContact().getPhoneNumber()));
            }
        }

        sql.addColumn(FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER,Long.toString(originalTxnId.getSequenceNumber()));
        sql.addColumn(FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE, dateToSQLDateString(itemStatus.getOriginalBusinessDate()));
        sql.addColumn(FIELD_ORIGINAL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(itemStatus.getOriginalLineNumber()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * Inserts an order line item status.
     * <P>
     *
     * @param dataConnection connection to database
     * @param orderTransaction order transaction
     * @param lineItem line item
     * @param lineItemSequenceNumber index into line item array
     * @exception DataException thrown if error occurs
     */
    public void insertPickupDeliveryOrderLineItemStatus(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem, int lineItemSequenceNumber)
            throws DataException
    {
        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        SQLInsertStatement sql = new SQLInsertStatement();
        // Table
        sql.setTable(TABLE_ORDER_LINE_ITEM_STATUS);

        // Field
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(orderTransaction.getBusinessDay()));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(lineItemSequenceNumber));
        sql.addColumn(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ORDER_LINE_ITEM_CAPTURED_SEQUENCE_NUMBER, lineItem.getCapturedOrderLineReference());
        sql.addColumn(FIELD_ITEM_STATUS, getItemStatus(itemStatus));
        sql.addColumn(FIELD_ITEM_STATUS_PREVIOUS, getItemPreviousStatus(itemStatus));
        if (itemStatus.getStatus().getLastStatusChange() != null)
        {
            sql.addColumn(FIELD_ORDER_STATUS_CHANGE, dateToSQLDateString(itemStatus.getStatus().getLastStatusChange()));
        }
        
        // A pickup or cancel order line item may involve partial pickup or cancel, use its line item status;
        // otherwise, use its order item status
        if ((lineItem instanceof OrderLineItemIfc) && ((OrderLineItemIfc) lineItem).isPickupCancelLineItem())
        {
            sql.addColumn(FIELD_LINE_ITEM_STATUS, ((OrderLineItemIfc)lineItem).getItemStatus());
        }
        else
        {
            sql.addColumn(FIELD_LINE_ITEM_STATUS, getItemStatus(itemStatus));
        }
        sql.addColumn(FIELD_XC_ORDER_ITEM_FLAG, getCrossChannelItemFlag(itemStatus));
        sql.addColumn(FIELD_ITEM_QUANTITY_ORDERED, itemStatus.getQuantityOrdered().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_PICKED_UP, itemStatus.getQuantityPickedUp().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_SHIPPED, itemStatus.getQuantityShipped().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_NEW, itemStatus.getQuantityNew().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_PENDING, itemStatus.getQuantityPending().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_AVAILABLE, itemStatus.getQuantityPicked().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_CANCELLED, itemStatus.getQuantityCancelled().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_RETURNED, itemStatus.getQuantityReturned().toString());
        sql.addColumn(FIELD_ORDER_TOTAL_AMOUNT, itemStatus.getOrderedAmount().toString());
        sql.addColumn(FIELD_ORDER_COMPLETED_AMOUNT, itemStatus.getCompletedAmount().toString());
        sql.addColumn(FIELD_ORDER_CANCELLED_AMOUNT, itemStatus.getCancelledAmount().toString());
        sql.addColumn(FIELD_ORDER_RETURNED_AMOUNT, itemStatus.getReturnedAmount().toString());
        sql.addColumn(FIELD_ORDER_DEPOSIT_AMOUNT, itemStatus.getDepositAmount().toString());
        sql.addColumn(FIELD_ORDER_LINE_ITEM_DISPOSITION_CODE, itemStatus.getItemDispositionCode());
        sql.addColumn(FIELD_DELIVERY_ORDER_ID, itemStatus.getDeliveryDetails().getDeliveryDetailID());
        sql.addColumn(FIELD_PICKUP_ORDER_STORE, makeSafeString(itemStatus.getPickupStoreID()));
        sql.addColumn(FIELD_PICKUP_ORDER_DATE, getPickupDate(itemStatus));
        sql.addColumn(FIELD_PICKUP_ORDER_FNAME, makeSafeString(itemStatus.getPickupFirstName()));
        sql.addColumn(FIELD_PICKUP_ORDER_LNAME, makeSafeString(itemStatus.getPickupLastName()));
        sql.addColumn(FIELD_PICKUP_ORDER_PHONE, makeSafeString(itemStatus.getPickupContact().getPhoneNumber()));
        sql.addColumn(FIELD_PICKUP_ORDER_SHIP_TO_STORE_FLAG, getShipToStoreForPickupFlag(itemStatus));
        sql.addColumn(FIELD_FULFILLMENT_ORDER_ID, makeSafeString(itemStatus.getFulfillmentOrderID()));
        
        TransactionIDIfc originalTxnId = itemStatus.getOriginalTransactionId();
        if (originalTxnId!=null)
        {
            sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, inQuotes(originalTxnId.getStoreID()));
            sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, inQuotes(originalTxnId.getWorkstationID()));
            sql.addColumn(FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER,Long.toString(originalTxnId.getSequenceNumber()));
            sql.addColumn(FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE, dateToSQLDateString(itemStatus.getOriginalBusinessDate()));  
        }   

        if (itemStatus.getOriginalLineNumber()!=-1)
        {
            sql.addColumn(FIELD_ORIGINAL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(itemStatus.getOriginalLineNumber()));
        }
        
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertPickupOrderLineItem", e);
        }

    }

    /**
     * Updates an order line item.
     *
     * @param dataConnection connection to database
     * @param orderTransaction order transaction
     * @param lineItem line item
     * @param lineItemSequenceNumber index into line item array
     * @exception DataException thrown if error occurs
     */
    public void updateOrderLineItem(JdbcDataConnection dataConnection, OrderTransactionIfc orderTransaction,
            SaleReturnLineItemIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        // save Pickup/Delivery Order Line Item Status data into table
        insertPickupDeliveryOrderLineItemStatus(dataConnection, orderTransaction, lineItem, lineItem.getLineNumber());

        // Only update the order item system record if it is not a cross channel or suspended item.
        // ORPOS is not the system of records for cross channel order items.
        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        if (itemStatus.isCrossChannelItem() || orderTransaction.isSuspended())
            return;
        
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_ORDER_ITEM);

        sql.addColumn(FIELD_ITEM_STATUS, Integer.toString(itemStatus.getStatus().getStatus()));
        sql.addColumn(FIELD_ITEM_STATUS_PREVIOUS, Integer.toString(itemStatus.getStatus().getPreviousStatus()));
        sql.addColumn(FIELD_TAX_GROUP_ID, Integer.toString(lineItem.getTaxGroupID()));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_QUANTITY, getItemQuantity(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT, getItemExtendedAmount(lineItem));
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT, lineItem.getItemTaxAmount().getStringValue());
        sql.addColumn(FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT, lineItem.getItemInclusiveTaxAmount()
                .getStringValue());
        sql.addColumn(FIELD_ITEM_DESCRIPTION, getItemDescription(lineItem));
        if (itemStatus.getStatus().getLastStatusChange() != null)
        {
            sql.addColumn(FIELD_ORDER_STATUS_CHANGE, dateToSQLDateString(itemStatus.getStatus().getLastStatusChange()));
        }

        sql.addColumn(FIELD_ITEM_QUANTITY_PICKED_UP, itemStatus.getQuantityPickedUp().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_SHIPPED, itemStatus.getQuantityShipped().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_NEW, itemStatus.getQuantityNew().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_PENDING, itemStatus.getQuantityPending().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_AVAILABLE, itemStatus.getQuantityPicked().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_CANCELLED, itemStatus.getQuantityCancelled().toString());
        sql.addColumn(FIELD_ITEM_QUANTITY_RETURNED, itemStatus.getQuantityReturned().toString());
        sql.addColumn(FIELD_ORDER_COMPLETED_AMOUNT, itemStatus.getCompletedAmount().toString());
        sql.addColumn(FIELD_ORDER_CANCELLED_AMOUNT, itemStatus.getCancelledAmount().toString());
        sql.addColumn(FIELD_ORDER_RETURNED_AMOUNT, itemStatus.getReturnedAmount().toString());
        sql.addColumn(FIELD_ORDER_DEPOSIT_AMOUNT, itemStatus.getDepositAmount().toString());

        // add qualifiers for the update
        sql.addQualifier(FIELD_ORDER_ID + " = '" + orderTransaction.getOrderID() + "'");
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItem.getOrderLineReference());
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID, makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()));
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * This method determines if the returned quantity, amount, discount
     * amounts and tax amounts should be saved in the various order and
     * order snap shot transaction tables.
     * @param dataConnection
     * @param transaction
     * @throws DataException
     */
    protected void saveReturnOrderLineItemStatus(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction) throws DataException
    {
        // loop through line items
        AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();
        for (AbstractTransactionLineItemIfc abstractLineItem: lineItems)
        {
            if (abstractLineItem instanceof OrderLineItemIfc)
            {
                OrderLineItemIfc lineItem = (OrderLineItemIfc) abstractLineItem;

                // If this is an Order Line Item and a return, save the order status to the various
                // order and order snap shot transaction tables.
                if (lineItem.isReturnLineItem())
                {
                    // The methods called to do these updates take an OrderTransaction as a parameter,
                    // which they use use it to get the transaction and order ID values.  This code
                    // instanciates an OrderTransaction object and transfers the necessary values
                    // from the current transaction to the (dummy) order transaction.
                    //
                    // Since the OrderTransactionIfc interface does not contain the get/setTransactionIdentifier()
                    // methods and this is not real order transaction, this code just calls new on the
                    // concrete class.
                    OrderTransaction dummyOrderTransaction = new OrderTransaction();
                    dummyOrderTransaction.setTransactionIdentifier(transaction.getTransactionIdentifier());
                    dummyOrderTransaction.setWorkstation(transaction.getWorkstation());
                    dummyOrderTransaction.setBusinessDay(transaction.getBusinessDay());
                    dummyOrderTransaction.setOrderID(lineItem.getOrderID());

                    // Setting this value to TYPE_ORDER_PARTIAL forces updates rather than inserts
                    // on the order tables.
                    dummyOrderTransaction.setTransactionType(TransactionIfc.TYPE_ORDER_PARTIAL);
                    dummyOrderTransaction.setTransactionStatus(transaction.getTransactionStatus()); 
                    saveReturnOrderLineItemStatus(dataConnection, dummyOrderTransaction, lineItem);
                }
            }
        }
    }

    /**
     * This method saves the returned quantity, amount, discount amount and
     * tax amount for a line item in the various order and order
     * transaction tables.
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @throws DataException
     */
    protected void saveReturnOrderLineItemStatus(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, OrderLineItemIfc lineItem) throws DataException
    {
        // The updateOrderLineItem() method calls the insertPickupDeliveryOrderLineItemStatus()
        // method, so insertPickupDeliveryOrderLineItemStatus() has to be called seperately for
        // cross channel items.
        if (lineItem.getOrderItemStatus().isCrossChannelItem())
        {
            insertPickupDeliveryOrderLineItemStatus(dataConnection, orderTransaction, lineItem, lineItem.getLineNumber());
        }
        else
        {
            updateOrderLineItem(dataConnection, orderTransaction, lineItem, lineItem.getLineNumber());
        }

        // Save the order discount status information.
        ItemDiscountStrategyIfc[] modifiers = lineItem.getItemPrice().getItemDiscounts();
        int discountSequenceNumber = 0;
        // Skip the price override, if there is one.
        if (lineItem.getItemPrice().isPriceOverride())
        {
            ++discountSequenceNumber;
        }
        for(ItemDiscountStrategyIfc discountLineItem: modifiers)
        {
            saveOrderDiscountStatus(dataConnection, orderTransaction, lineItem, discountSequenceNumber, discountLineItem);
            ++discountSequenceNumber;
        }

        // Save the tax status
        saveOrderTaxInformation(dataConnection, orderTransaction, lineItem);
    }

    /**
     * Inserts a Gift Card
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @exception DataException upon error
     */
    public void insertGiftCard(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        // Get gift card
        GiftCardIfc giftCard = ((GiftCardPLUItemIfc) (lineItem.getPLUItem())).getGiftCard();

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_GIFT_CARD);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItem.getLineNumber()));
        EncipheredCardDataIfc cardData = giftCard.getEncipheredCardData();
        if(cardData != null)
        {
            sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER, inQuotes(cardData.getEncryptedAcctNumber()));
            sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER, inQuotes(cardData.getMaskedAcctNumber()));
        }
        sql.addColumn(FIELD_GIFT_CARD_ACTIVATION_ADJUDICATION_CODE, makeSafeString(giftCard.getApprovalCode()));
        sql.addColumn(FIELD_GIFT_CARD_ENTRY_METHOD, getEntryMethod(giftCard.getEntryMethod()));
        sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE, inQuotes(String.valueOf(giftCard.getRequestType())));
        if(giftCard.getCurrentBalance() != null)
        {
            sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE, giftCard.getCurrentBalance().getStringValue());
        }
        if(giftCard.getInitialBalance() != null)
        {
            sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE, giftCard.getInitialBalance().getStringValue());
        }
        //+I18N
        sql.addColumn(FIELD_CURRENCY_ID, lineItem.getExtendedSellingPrice().getType().getCurrencyId());
        //-I18N
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA, makeSafeString(giftCard.getSettlementData()));
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER,makeSafeString(giftCard.getTraceNumber()));
        String authDateTime = getAuthorizationDateTime(giftCard.getAuthorizedDateTime());
        if(
           (authDateTime == null)    ||
           (authDateTime.equals("")) ||
           (authDateTime.equals("null"))
          )
        {
            sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME, getSQLCurrentTimestampFunction());
        }
        else
        {
            sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME, authDateTime);
        }

        sql.addColumn(FIELD_TENDER_AUTHORIZATION_JOURNAL_KEY, makeSafeString(giftCard.getJournalKey()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertGiftCard", e);
        }
    }

    /**
     * Returns database safe string for the given EntryMethod.
     * @param entryMethod
     * @return database safe string for the given EntryMethod.
     */
    protected String getEntryMethod(EntryMethod entryMethod)
    {
        String returnValue = inQuotes("");
        if(entryMethod != null)
        {
            returnValue = inQuotes(entryMethod.toString());
        }
        return returnValue;
    }

    /**
     * Saves an Alteration
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @exception DataException upon error
     */
    public void saveAlteration(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        try
        {
            insertAlteration(dataConnection, transaction, lineItem);
        }
        catch (DataException de)
        {
            updateAlteration(dataConnection, transaction, lineItem);
        }
    }

    /**
     * Inserts an Alteration
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @exception DataException upon error
     */
    public void insertAlteration(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        // Get alteration
        AlterationIfc alteration = ((AlterationPLUItemIfc) (lineItem.getPLUItem())).getAlteration();

        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_ALTERATION_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItem.getLineNumber()));
        sql.addColumn(FIELD_ALTERATION_TYPE, inQuotes(alteration.getAlterationType()));
        sql.addColumn(FIELD_ITEM_DESCRIPTION, makeSafeString(alteration.getItemDescription()));
        sql.addColumn(FIELD_ITEM_ID, makeSafeString(alteration.getItemNumber()));
        sql.addColumn(FIELD_VALUE1, makeSafeString(alteration.getValue1()));
        sql.addColumn(FIELD_VALUE2, makeSafeString(alteration.getValue2()));
        sql.addColumn(FIELD_VALUE3, makeSafeString(alteration.getValue3()));
        sql.addColumn(FIELD_VALUE4, makeSafeString(alteration.getValue4()));
        sql.addColumn(FIELD_VALUE5, makeSafeString(alteration.getValue5()));
        sql.addColumn(FIELD_VALUE6, makeSafeString(alteration.getValue6()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertAlteration", e);
        }
    }

    /**
     * Updates an Alteration
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @exception DataException upon error
     */
    public void updateAlteration(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem) throws DataException
    {
        // Get alteration
        AlterationIfc alteration = ((AlterationPLUItemIfc) (lineItem.getPLUItem())).getAlteration();

        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_ALTERATION_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItem.getLineNumber()));
        sql.addColumn(FIELD_ALTERATION_TYPE, inQuotes(alteration.getAlterationType()));
        sql.addColumn(FIELD_ITEM_DESCRIPTION, makeSafeString(alteration.getItemDescription()));
        sql.addColumn(FIELD_ITEM_ID, makeSafeString(alteration.getItemNumber()));
        sql.addColumn(FIELD_VALUE1, makeSafeString(alteration.getValue1()));
        sql.addColumn(FIELD_VALUE2, makeSafeString(alteration.getValue2()));
        sql.addColumn(FIELD_VALUE3, makeSafeString(alteration.getValue3()));
        sql.addColumn(FIELD_VALUE4, makeSafeString(alteration.getValue4()));
        sql.addColumn(FIELD_VALUE5, makeSafeString(alteration.getValue5()));
        sql.addColumn(FIELD_VALUE6, makeSafeString(alteration.getValue6()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateAlteration", e);
        }
    }

    /**
     * Get the DiscountReferenceID from the lineitem.
     *
     * @param lineItem discount line item
     * @return discount ReferenceID
     */
    public String getDiscountReferenceID(DiscountRuleIfc lineItem)
    {
        String returnString = null;
        if (lineItem.getReferenceID() != null)
        {
            returnString = "'" + lineItem.getReferenceID() + "'";
        }
        return returnString;
    }

    /**
     * Get the DiscountReferenceIDTypeCode from athe lineitem.
     *
     * @param lineItem discount line item
     * @return discount ReferenceIDTypeCode
     */
    public String getDiscountReferenceIDTypeCode(DiscountRuleIfc lineItem)
    {
        return "'" + DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[lineItem.getReferenceIDCode()] + "'";
    }

    /**
     * Get the PriceModifierReferenceID from athe lineitem.
     *
     * @param lineItem discount line item
     * @return ReferenceID
     */
    public String getPriceModifierReferenceID(DiscountRuleIfc lineItem)
    {
        String returnString = null;
        if (lineItem.getReferenceID() != null)
        {
            returnString = "'" + lineItem.getReferenceID() + "'";
        }
        return returnString;
    }

    /**
     * Get the PriceModifierReferenceIDTypeCode from athe lineitem.
     *
     * @param lineItem discount line item
     * @return ReferenceIDTypeCode
     */
    public String getPriceModifierReferenceIDTypeCode(DiscountRuleIfc lineItem)
    {
        return "'" + DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[lineItem.getReferenceIDCode()] + "'";
    }

    /**
     * Calculate discount percent for a discount line item.
     *
     * @param lineItem discount line item
     * @return discount percent
     */
    public String getPriceModifierPercent(ItemDiscountStrategyIfc lineItem)
    {
        BigDecimal percent = BigDecimal.ZERO;

        int discountMethod = lineItem.getDiscountMethod();
        // For a discount audit, uses its original discount method;
        // Its current discount method is always DISCOUNT_METHOD_AMOUNT.
        if (lineItem instanceof ItemDiscountAuditStrategyIfc)
        {
            discountMethod = ((ItemDiscountAuditStrategyIfc)lineItem).getOriginalDiscountMethod();
        }

        switch (discountMethod)
        {
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
                percent = lineItem.getDiscountRate().movePointRight(2);
                break;

            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
            default:
                break;
        }

        String result = percent.toString();
        int pos = result.indexOf(".");
        if (pos != -1)
        {
            int fracPlaces = result.length() - (pos + 1);
            if (fracPlaces >= 2)
            {
                result = result.substring(0, pos + 3); // pickup two digits
                                                        // after decimal point
            }
        }

        return (result);
    }

    /**
     * Returns the price override amount.
     *
     * @param lineItem line item
     * @return the price override amount
     */
    public String getPriceModifierAmount(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getItemPrice().getSellingPrice().getStringValue());
    }

    /**
     * Calculate discount amount for a discount line item.
     *
     * @param lineItem discount line item
     * @return discount amount
     */
    public String getPriceModifierAmount(ItemDiscountStrategyIfc lineItem)
    {
        String amount = null;

        switch (lineItem.getDiscountMethod())
        {
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE:
                if (lineItem instanceof ItemDiscountAuditIfc)
                {
                    ItemDiscountAuditIfc discount = (ItemDiscountAuditIfc) lineItem;
                    // For an item discount audit, save its unit discount amount.
                    // The amount should always be positive.
                    if (discount.getUnitDiscountAmount() != null)
                    {
                        amount = discount.getUnitDiscountAmount().abs().getStringValue();
                    }
                    else
                    {
                        amount = discount.getDiscountAmount().abs().getStringValue();
                    }
                }
                else if (lineItem instanceof ItemDiscountByAmountIfc)
                {
                    ItemDiscountByAmountIfc discount = (ItemDiscountByAmountIfc) lineItem;
                    // For whatever reason, we store a positive discount amount
                    // to indicate item discounts. A negative amount
                    // indicates a return with a transaction discount that
                    // has been converted to an item discount
                    // (ReturnItemTransactionDiscountAudit).
                    // The correct discount amount is generated by the discount
                    // strategies - that is negative for returns and voids
                    // and positive for sales and void returns.
                    amount = discount.getDiscountAmount().abs().getStringValue();
                }
                else if (lineItem instanceof ReturnItemTransactionDiscountAuditIfc)
                {
                    ReturnItemTransactionDiscountAuditIfc discount = (ReturnItemTransactionDiscountAuditIfc) lineItem;
                    amount = discount.getDiscountAmount().getStringValue();
                }
                else if (lineItem instanceof ItemTransactionDiscountAuditIfc)
                {
                    ItemTransactionDiscountAuditIfc discount = (ItemTransactionDiscountAuditIfc) lineItem;
                    amount = discount.getDiscountAmount().getStringValue();
                }
                break;
            // new BO requirements this must be populated event for PERCENTAGE
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
                amount = lineItem.getDiscountAmount().abs().getStringValue();
                break;
            default:
                amount = "0";
                break;
        }

        return (amount);
    }

    /**
     * Build discount type code
     *
     * @param discount type
     * @return discount type
     */
    public String getDiscountType(TransactionDiscountStrategyIfc discount)
    {
        int discountMethod = discount.getDiscountMethod();
        if (discount instanceof TransactionDiscountAuditIfc)
        {
            discountMethod = ((TransactionDiscountAuditIfc)discount).getOriginalDiscountMethod();
        }
        String typeCode = DiscountRuleConstantsIfc.DISCOUNT_METHOD_CODE[discountMethod];
        return ("'" + typeCode + "'");
    }

    /**
     * Returns the discount reason code
     *
     * @param discount discount
     * @return discount reason code
     */
    public String getDiscountReasonCode(TransactionDiscountStrategyIfc discount)
    {
        return makeSafeString(discount.getReason().getCode());
    }

    /**
     * Returns the discount amount for a discount line item
     *
     * @param lineItem discount line item
     * @return discount amount
     */
    public String getDiscountAmount(TransactionDiscountStrategyIfc lineItem)
    {
        BigDecimal amount = BigDecimal.ZERO;
        switch (lineItem.getDiscountMethod())
        {
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
                // new BO requires this be populated regardless of method.
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE:
                amount = new BigDecimal(lineItem.getDiscountAmount().getStringValue());
                break;

            default:
                break;
        }

        return (amount.toString());
    }

    /**
     * Returns the scope code for a discount line item
     *
     * @param lineItem discount line item
     * @return scope code
     */
    public int getPriceModifierScopeCode(ItemDiscountStrategyIfc lineItem)
    {
        return lineItem.getDiscountScope();
    }

    /**
     * Returns the method code for a discount line item
     *
     * @param lineItem discount line item
     * @return method code
     */
    public int getPriceModifierMethodCode(ItemDiscountStrategyIfc lineItem)
    {
        int discountMethod = lineItem.getDiscountMethod();
        // For a discount audit, save the original discount method by the time the discount is applied;
        // The audit's own discount method is alway DISCOUNT_METHOD_AMOUNT.
        if (lineItem instanceof ItemDiscountAuditStrategyIfc)
        {
            discountMethod = ((ItemDiscountAuditStrategyIfc)lineItem).getOriginalDiscountMethod();
        }
        return discountMethod;
    }

    /**
     * Returns the assignment basis for a discount line item
     *
     * @param lineItem discount line item
     * @return int assignment basis
     */
    public int getPriceModifierAssignmentBasis(ItemDiscountStrategyIfc lineItem)
    {
        return lineItem.getAssignmentBasis();
    }

    /**
     * Returns the rule id for a discount line item ('1' for manual discount
     * ruleID for advancedPricing discounts.
     *
     * @param lineItem discount line item
     * @return method code
     */
    public String getDiscountRuleID(DiscountRuleIfc lineItem)
    {
        String ruleID = "1";
        if (!(lineItem.getRuleID().equals("")))
        {
            ruleID = lineItem.getRuleID();
        }
        ruleID = ruleID.split(":")[0]; // for multithreshold rules rule id is
                                       // [discountruleID : threshold ID]. We do
                                       // not want to print the threshold id in
                                       // modifier table.
        return ruleID;

    }

    /**
     * Returns the discount percent for a discount line item
     *
     * @param lineItem discount line item
     * @return discount amount
     */
    public String getDiscountPercent(TransactionDiscountStrategyIfc lineItem)
    {
        BigDecimal percent = BigDecimal.ZERO;
        int discountMethod = lineItem.getDiscountMethod();
        if (lineItem instanceof TransactionDiscountAuditIfc)
        {
            discountMethod = ((TransactionDiscountAuditIfc)lineItem).getOriginalDiscountMethod();
        }

        switch (discountMethod)
        {
            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE:
                /*
                 * Store as a percentage, not a rate (i.e. 8.25, not .0825)
                 */
                percent = lineItem.getDiscountRate();
                if (percent.toString().length() >= 5)
                {
                    BigDecimal scaleOne = new BigDecimal(1);
                    //changing the scale to 4 to handle discount percentage like 8.25, which comes as .0825
                    //and gets rounded to 0.09 for a scale of 2.
                    percent = percent.divide(scaleOne, 4, BigDecimal.ROUND_HALF_UP);
                }
                percent = percent.movePointRight(2);
                break;

            case DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT:
            default:
                break;
        }

        return (percent.toString());
    }

    /**
     * Returns the discount assignment basis code for a discount line item
     *
     * @param lineItem discount line item
     * @return discount assignment basis code
     */
    public String getDiscountAssignmentBasis(TransactionDiscountStrategyIfc lineItem)
    {
        StringBuilder strResult = new StringBuilder("'");
        try
        {
            strResult.append(ASSIGNMENT_BASIS_CODES[lineItem.getAssignmentBasis()]);
        }
        // use manual if all else fails
        catch (ArrayIndexOutOfBoundsException e)
        {
            strResult.append(ASSIGNMENT_BASIS_CODES[ASSIGNMENT_MANUAL]);
        }
        strResult.append("'");

        return (strResult.toString());
    }

    /**
     * Returns the discount-enabled flag for a discount line item
     *
     * @param lineItem discount line item
     * @return discount-enabled flag string
     */
    public String getDiscountEnabled(TransactionDiscountStrategyIfc lineItem)
    {
        return (makeStringFromBoolean(lineItem.getEnabled()));
    }

    /**
     * Returns the discount-included in best deal flag for a discount line item
     *
     * @param lineItem discount line item
     * @return discount-included in best deal flag string
     */
    public String getIncludedInBestDealFlag(DiscountRuleIfc lineItem)
    {
        boolean flag = false;
        if (lineItem instanceof CustomerDiscountByPercentageIfc)
        {
            flag = ((CustomerDiscountByPercentageIfc) lineItem).isIncludedInBestDeal();
        }
        return (makeStringFromBoolean(flag));
    }

    /**
     * Returns the discount-included in best deal flag for a discount line item
     *
     * @param lineItem discount line item
     * @return discount-included in best deal flag string
     */
    public String getPriceModifierDamageDiscountFlag(ItemDiscountStrategyIfc lineItem)
    {
        boolean flag = lineItem.isDamageDiscount();

        return (makeStringFromBoolean(flag));
    }

    /**
     * Returns the employee ID for the line item
     *
     * @param lineItem The line item
     * @return the employee ID
     */
    protected String getEmployeeID(SaleReturnLineItemIfc lineItem)
    {
        return ("'" + lineItem.getSalesAssociate().getEmployeeID() + "'");
    }

    /**
     * Returns the original sale date for return items.
     *
     * @param lineItem The line item
     * @return String
     */
    protected String getOriginalDate(SaleReturnLineItemIfc lineItem)
    {
        String ret = "null";

        if (lineItem.getReturnItem() != null && lineItem.getReturnItem().getOriginalTransactionBusinessDate() != null)
        {
            ret = dateToSQLDateString(lineItem.getReturnItem().getOriginalTransactionBusinessDate().dateValue());
        }

        return ret;
    }

    /**
     * Returns the original tran id for return items.
     *
     * @param lineItem The line item
     * @return String
     */
    protected String getOriginalTransactionId(SaleReturnLineItemIfc lineItem)
    {
        String ret = "null";

        if (lineItem.getReturnItem() != null && lineItem.getReturnItem().getOriginalTransactionID() != null)
        {
            ret = "'" + lineItem.getReturnItem().getOriginalTransactionID().getTransactionIDString() + "'";
        }

        return ret;
    }

    /**
     * Returns the original store id for return items.
     *
     * @param lineItem The line item
     * @return String
     */
    protected String getOriginalStoreID(SaleReturnLineItemIfc lineItem)
    {
        String ret = "null";

        if (lineItem.getReturnItem() != null && lineItem.getReturnItem().getStore() != null)
        {
            ret = "'" + lineItem.getReturnItem().getStore().getStoreID() + "'";
        }

        return ret;
    }

    /**
     * Returns the original tran id for return items.
     *
     * @param lineItem The line item
     * @return String
     */
    protected String getOriginalLineNumber(SaleReturnLineItemIfc lineItem)
    {
        String ret = "-1";

        if (lineItem.getReturnItem() != null)
        {
            ret = String.valueOf(lineItem.getReturnItem().getOriginalLineNumber());
        }
        else if (lineItem instanceof OrderLineItemIfc)
        {
            ret = String.valueOf(lineItem.getOriginalLineNumber());
        }
        return ret;
    }

    /**
     * Returns the type or retail transaction line item.
     *
     * @param lineItemType The type line item
     * @return the type or retail transaction line item.
     */
    protected String getLineItemType(String lineItemType)
    {
        return ("'" + lineItemType + "'");
    }

    /**
     * Returns the string value to be used in the database for the gift registry
     *
     * @param lineItem The sale/return line item
     * @return The gift registry value
     */
    public String getGiftRegistryString(SaleReturnLineItemIfc lineItem)
    {
        String value = "null";

        // If there is not gift registry associated with this object, we
        // need to set the registry value to null in the database.
        if (lineItem.getRegistry() != null)
        {
            value = "'" + lineItem.getRegistry().getID() + "'";
        }

        return (value);
    }

    /**
     * Returns the sequence number of the retail transaction line item.
     *
     * @param lineItem The retail transaction line item
     * @return the sequence number of the retail transaction line item.
     */
    protected String getLineItemSequenceNumber(AbstractTransactionLineItemIfc lineItem)
    {
        return (String.valueOf(lineItem.getLineNumber()));
    }

    /**
     * Returns the item ID for the SaleReturnLineItem.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the item ID for the SaleReturnLineItem.
     */
    protected String getItemID(SaleReturnLineItemIfc lineItem)
    {
        return ("'" + lineItem.getPLUItem().getItemID() + "'");
    }

    /**
     * Returns the item Kit ID for a KitComponentLineItem.
     *
     * @param lineItem The KitComponentLineItem
     * @return the item ID for the components parent KitHeaderLineItem.
     */
    protected String getItemKitID(KitComponentLineItemIfc lineItem)
    {
        return ("'" + lineItem.getItemKitID() + "'");
    }

    /**
     * Returns the item serial number for the SaleReturnLineItem.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the item serial number for the SaleReturnLineItem.
     */
    protected String getItemSerial(SaleReturnLineItemIfc lineItem)
    {
        String ret = "null";
        if (lineItem.getItemSerial() != null)
        {
            ret = "'" + lineItem.getItemSerial() + "'";
        }
        return (ret);
    }

    /**
     * Returns the tax group ID for the SaleReturnLineItem.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the tax group ID for the SaleReturnLineItem.
     */
    protected String getTaxGroupID(SaleReturnLineItemIfc lineItem)
    {
        return (Integer.toString(lineItem.getPLUItem().getTaxGroupID()));
    }

    /**
     * Returns the tax amount of the transaction.
     *
     * @param transaction The transaction
     * @return the tax amount of the transaction.
     */
    protected String getTaxAmount(RetailTransactionIfc transaction)
    {
        return transaction.getTransactionTotals().getTaxInformationContainer().getTaxAmount().toString();
    }

    /**
     * Returns the inclusive tax amount of the transaction.
     *
     * @param transaction The transaction
     * @return the inclusive tax amount of the transaction.
     */
    protected String getInclusiveTaxAmount(RetailTransactionIfc transaction)
    {
        return transaction.getTransactionTotals().getTaxInformationContainer().getInclusiveTaxAmount().toString();
    }

    /**
     * Returns the tax mode.
     *
     * @param transaction the retail transaction
     * @return tax mode
     */
    protected String getTaxMode(RetailTransactionIfc transaction)
    {
        return String.valueOf(transaction.getTransactionTax().getTaxMode());
    }

    /**
     * Returns the default tax rate of the transaction.
     *
     * @param transaction the retail transaction
     * @return the default tax rate of the transaction.
     */
    protected String getTaxPercent(RetailTransactionIfc transaction)
    {
        return String.valueOf(transaction.getTransactionTax().getDefaultRate() * 100.0);
    }

    /**
     * Returns the override tax rate of the transaction.
     *
     * @param transaction the retail transaction
     * @return the override tax rate of the transaction.
     */
    protected String getTaxOverridePercent(RetailTransactionIfc transaction)
    {
        return (String.valueOf(transaction.getTransactionTax().getOverrideRate() * 100.0));
    }

    /**
     * Returns the override tax amount of the transaction.
     *
     * @param transaction the retail transaction
     * @return the override tax amount of the transaction.
     */
    protected String getTaxOverrideAmount(RetailTransactionIfc transaction)
    {
        return (transaction.getTransactionTax().getOverrideAmount().getStringValue());
    }

    /**
     * Returns the transaction tax reason code
     *
     * @param transaction The retail transaction
     * @return the tax reason code
     */
    protected String getTaxReasonCode(RetailTransactionIfc transaction)
    {
        return makeSafeString(transaction.getTransactionTax().getReason().getCode());
    }

    /**
     * Returns the transaction exempt certificate ID
     *
     * @param transaction the retail transaction
     * @return the tax exempt certificate ID
     */
    protected String getTaxExemptCertificateID(RetailTransactionIfc transaction)
    {
        return (makeSafeString(transaction.getTransactionTax().getTaxExemptCertificateID()));
    }

    /**
     * Returns the masked transaction exempt certificate ID
     *
     * @param transaction the retail transaction
     * @return the masked tax exempt certificate ID
     */
    protected String getMaskedTaxExemptCertificateID(RetailTransactionIfc transaction)
    {
        EncipheredDataIfc taxCertificate = FoundationObjectFactory.getFactory()
                .createEncipheredDataInstance(transaction.getTransactionTax().getTaxExemptCertificateID());
        return (makeSafeString(taxCertificate.getMaskedNumber()));
    }

    /**
     * Returns the quantity of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the quantity of the item.
     */
    protected String getItemQuantity(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getItemQuantityDecimal().toString());
    }

    /**
     * Returns the unit of measure code of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the unit of measure code of the item.
     */
    protected String getUOMCode(SaleReturnLineItemIfc lineItem)
    {
        String value = "null";
        PLUItemIfc pluItem = lineItem.getPLUItem();

        if (pluItem.getUnitOfMeasure() != null)
        {
            value = "'" + lineItem.getPLUItem().getUnitOfMeasure().getUnitID() + "'";
        }
        else if (pluItem instanceof UnknownItemIfc)
        {
            UnknownItemIfc item = (UnknownItemIfc) pluItem;
            value = "'" + item.getUOMCode() + "'";
        }
        return (value);
    }

    /**
     * Returns the description of the item. This is transactional data.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the description of the item.
     */
    protected String getItemDescription(SaleReturnLineItemIfc lineItem)
    {
        return (makeSafeString(lineItem.getPLUItem().getDescription(LocaleMap.getLocale(LocaleMap.DEFAULT))));
    }

    /**
     * Returns the price of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the price of the item.
     */
    protected String getItemPrice(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getSellingPrice().getStringValue());
    }

    /**
     * Returns the extended amount of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the extended amount of the item.
     */
    protected String getItemExtendedAmount(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getExtendedSellingPrice().getStringValue());
    }

    /**
     * Returns the extended amount of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the extended amount of the item.
     */
    protected String getPermanentSellingPrice(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getItemPrice().getPermanentSellingPrice().getStringValue());
    }

    /**
     * Returns the receipt description.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the receipt description.
     */
    protected String getReceiptDescription(SaleReturnLineItemIfc lineItem)
    {
        return (makeSafeString(lineItem.getReceiptDescription()));
    }

    /**
     * Returns the receipt description locale.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the receipt description locale.
     */
    protected String getReceiptDescriptionLocal(SaleReturnLineItemIfc lineItem)
    {
    	Locale bestLocale = LocaleMap.getBestMatch(lineItem.getReceiptDescriptionLocale());
        return (makeSafeString(bestLocale.toString()));
    }

    /**
     * Returns the restocking fee flag.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the restocking fee flag.
     */
    protected String getRestockingFeeFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.getPLUItem().getItemClassification().getRestockingFeeFlag())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the product group ID.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the product group ID.
     */
    protected String getProductGroupID(SaleReturnLineItemIfc lineItem)
    {
        return (makeSafeString(lineItem.getPLUItem().getProductGroupID()));
    }

    /**
     * Returns the size required flag.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the size required flag.
     */
    protected String getSizeRequiredFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.getPLUItem().isItemSizeRequired())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the size required flag.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the size required flag.
     */
    protected String getLineItemUOMCode(SaleReturnLineItemIfc lineItem)
    {
        return (makeSafeString(lineItem.getPLUItem().getUnitOfMeasure().getUnitID()));
    }

    /**
     * Returns the Pos Department ID.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the Pos Department ID.
     */
    protected String getPosDepartmentID(SaleReturnLineItemIfc lineItem)
    {
        return (makeSafeString(lineItem.getPLUItem().getDepartmentID()));
    }

    /**
     * Returns the Item Type ID.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the Item Type ID.
     */
    protected String getItemTypeID(SaleReturnLineItemIfc lineItem)
    {
        return String.valueOf(lineItem.getPLUItem().getItemClassification().getItemType());
    }

    /**
     * Returns the Return Prohibited flag value.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the Return Prohibited flag.
     */
    protected String getReturnProhibited(SaleReturnLineItemIfc lineItem)
    {
        // The representation of this attribute is opposite in the database
        // and the object model.  In the object model it expressed as ReturnEligible
        // and in the data model it is expressed as ReturnProhibited.  Therefore,
        // when writing this out, ReturnEligible = true becomes ReturnProhibited = false.
        String value = "'0'";
        if (!lineItem.getPLUItem().getItemClassification().isReturnEligible())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the Employee Discount Allowed flag value.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the Employee Discount Allowed flag.
     */
    private String getEmployeeDiscountAllowed(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.isEmployeeDiscountEligible())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the Employee Discount Allowed flag value.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the Employee Discount Allowed flag.
     */
    private String getTaxable(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.getPLUItem().getTaxable())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the extended discounted amount of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the extended amount of the item.
     */
    protected String getItemExtendedDiscountedAmount(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getExtendedDiscountedSellingPrice().getStringValue());
    }

    /**
     * Returns the extended restocking amount of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the extended restocking amount of the item.
     */
    protected String getItemExtendedRestockingFee(SaleReturnLineItemIfc lineItem)
    {
        String restockingFeeString = null;

        ItemPriceIfc price = lineItem.getItemPrice();
        if (price != null)
        {
            CurrencyIfc restockingFee = price.getExtendedRestockingFee();

            if (restockingFee != null)
            {
                restockingFeeString = restockingFee.getStringValue();
            }
        }

        return (restockingFeeString);
    }

    /**
     * Returns the tax amount of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the tax amount of the item.
     */
    protected String getItemTaxAmount(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getItemPrice().getItemTaxAmount().getStringValue());
    }

    /**
     * Returns the inclusive tax amount of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the inclusive tax amount of the item.
     */
    protected String getItemInclusiveTaxAmount(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getItemPrice().getItemInclusiveTaxAmount().getStringValue());
    }

    /**
     * Returns the default tax rate of the item.
     *
     * @param itemTax The tax information for the line item
     * @return the default tax rate of the item.
     */
    protected String getItemTaxPercent(ItemTaxIfc itemTax)
    {
        return (String.valueOf(itemTax.getDefaultRate() * 100.0));
    }

    /**
     * Returns the override tax rate of the item.
     *
     * @param itemTax The tax information for the line item
     * @return the override tax rate of the item.
     */
    protected String getItemTaxOverridePercent(ItemTaxIfc itemTax)
    {
        return (String.valueOf(itemTax.getOverrideRate() * 100.0));
    }

    /**
     * Returns the override tax amount of the item.
     *
     * @param itemTax The tax information for the line item
     * @return the override tax amount of the item.
     */
    protected String getItemTaxOverrideAmount(ItemTaxIfc itemTax)
    {
        return (itemTax.getOverrideAmount().getStringValue());
    }

    /**
     * Returns the tax mode of the item.
     *
     * @param itemTax The tax information for the line item
     * @return the tax mode of the item.
     */
    protected String getItemTaxMode(ItemTaxIfc itemTax)
    {
        return (String.valueOf(itemTax.getTaxMode()));
    }

    /**
     * Returns the tax mode of the item.
     *
     * @param lineItem The line item to get the taxability of
     * @return the tax mode of the item.
     */
    protected String getItemTaxable(SaleReturnLineItemIfc lineItem)
    {
        String value = "0";
        if (lineItem.getItemPrice().getItemTax().getTaxable())
        {
            value = "1";
        }
        return (value);
    }

    /**
     * Returns the tax scope of the item.
     *
     * @param itemTax The tax information for the line item
     * @return the tax scope of the item.
     */
    protected String getTaxScope(ItemTaxIfc itemTax)
    {
        return (String.valueOf(itemTax.getTaxScope()));
    }

    /**
     * Returns the tax reason code
     *
     * @param itemTax The tax information for the line item
     * @return the tax reason code
     */
    protected String getItemTaxReasonCode(ItemTaxIfc itemTax)
    {
        return makeSafeString(itemTax.getReason().getCode());
    }

    /**
     * Returns whether the line item is a return.
     *
     * @param lineItem The SaleReturnLineItem
     * @return whether the line item is a return.
     */
    protected String getReturnFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.getItemQuantityDecimal().signum() < 0)
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns whether the line item is an advanced pricing rule
     *
     * @param lineItem The SaleReturnLineItem
     * @return whether the line item is an advanced pricing rule.
     */
    protected String getAdvancedPricingRuleFlag(DiscountRuleIfc lineItem)
    {
        boolean flag = lineItem.isAdvancedPricingRule();

        return (makeStringFromBoolean(flag));
    }

    /**
     * Returns the return reason code of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the return reason code of the item.
     */
    protected String getReturnReasonCode(SaleReturnLineItemIfc lineItem)
    {
        String value = "null";
        if (lineItem.getReturnItem() != null)
        {
            value = makeSafeString(lineItem.getReturnItem().getReason().getCode());
        }
        return (value);
    }

    /**
     * Returns the return item condition of the item.
     * @param lineItem the SaleReturnLineItem
     * @return the item condition code of the item.
     */
    protected String getReturnItemConditionCode(SaleReturnLineItemIfc lineItem)
    {
        String value = "null";
        if(lineItem.getReturnItem() !=null && lineItem.getReturnItem().hasItemCondition())
        {
            value = makeSafeString(lineItem.getReturnItem().getItemCondition().getCode());
        }
        return (value);

    }

    /**
     * Returns the department ID of the item.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the department ID of the item.
     */
    protected String getDepartmentID(SaleReturnLineItemIfc lineItem)
    {
        return ("'" + lineItem.getPLUItem().getDepartmentID() + "'");
    }

    /**
     * Returns the string value of the sequence number.
     *
     * @param sequenceNumber The sequence number
     * @return the sequence number.
     */
    protected String getSequenceNumber(int sequenceNumber)
    {
        return (String.valueOf(sequenceNumber));
    }

    /**
     * Returns the price modifier reason code.
     *
     * @param discount The discount
     * @return the price modifier reason code for the item.
     */
    protected String getPriceModifierReasonCode(ItemDiscountStrategyIfc discount)
    {
        return makeSafeString(discount.getReason().getCode());
    }

    /**
     * Returns the price modifier reason code.
     *
     * @param lineItem The discount
     * @return the price modifier reason code for the item.
     */
    protected String getPriceModifierReasonCode(SaleReturnLineItemIfc lineItem)
    {
        return makeSafeString(lineItem.getItemPrice().getItemPriceOverrideReason().getCode());
    }

    /**
     * Returns the item's tax method.
     *
     * @param itemTax Object containing tax data
     * @return 0 for default method, 1 for external tax method.
     */
    protected String getItemTaxMethod(ItemTaxIfc itemTax)
    {
        String value = "0";
        if (itemTax.getExternalTaxEnabled())
        {
            value = "1";
        }
        return (value);
    }

    /**
     * Returns the item's Send flag.
     *
     * @param lineItem SaleReturnLineItemIfc
     * @return 0 for not send item, 1 for send item.
     */
    protected String getSendFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.getItemSendFlag())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the item's Send flag.
     *
     * @param lineItem SaleReturnLineItemIfc
     * @return 0 for not send item, 1 for send item.
     */
    protected String getShippingChargeFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.isShippingCharge())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the item's Send flag.
     *
     * @param SaleReturnLineItemIfc lineItem
     * @return 0 for not send item, 1 for send item.
     */
    protected String getSendLabelCount(SaleReturnLineItemIfc lineItem)
    {
        return String.valueOf(lineItem.getSendLabelCount());
    }

    /**
     * Returns the item's Gift Receipt flag.
     *
     * @param lineItem SaleReturnLineItemIfc
     * @return 0 for not gift receipt item, 1 for gift Receipt item.
     */
    protected String getGiftReceiptFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.isGiftReceiptItem())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Return the SQL value that tells whether or not a particular related item
     * is returnable
     *
     * @param lineItem
     * @return '0' or '1' (false or true)
     * @since NEP67
     */
    protected String getReturnRelatedItemFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.isRelatedItemReturnable())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Return the sequence number this related item is linked to
     *
     * @param lineItem
     * @return sequence number
     * @since NEP67
     */
    protected String getRelatedSeqNum(SaleReturnLineItemIfc lineItem)
    {
        return String.valueOf(lineItem.getRelatedItemSequenceNumber());
    }

    /**
     * Return value to store in the SQL for the relatedRemoveItemFlag.
     *
     * @param lineItem
     * @return '0' or '1' (false or true)
     * @since NEP67
     */
    protected String getRemoveRelatedItemFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.isRelatedItemDeleteable())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the item's size code.
     *
     * @param lineItem SaleReturnLineItemIfc
     * @return item size code
     */
    protected String getItemSizeCode(SaleReturnLineItemIfc lineItem)
    {
        String value = "";
        if (lineItem.getItemSizeCode() != null)
        {
            value = lineItem.getItemSizeCode();
        }
        value = makeSafeString(value);
        return (value);
    }

    /**
     * Returns the promotion id for a discount line item.
     *
     * @param lineItem discount line item
     * @return promotion id
     */
    public int getPromotionID(DiscountRuleIfc lineItem)
    {
        return lineItem.getPromotionId();
    }

    /**
     * Inserts the return tender data.
     *
     * @param dataConnection connection to the db
     * @param transaction retail transaction
     * @param returnTender return tender to insert
     * @exception DataException error saving data to the db
     */
    public void insertReturnTendersData(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
            ReturnTenderDataElementIfc returnTender, int returnTenderSequenceNumber) throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_RETURN_TENDER_DATA);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_RETURN_TENDER_SEQUENCE_NUMBER, returnTenderSequenceNumber);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_TENDER_TYPE_CODE, inQuotes(returnTender.getTenderType()));
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TENDER_MEDIA_ISSUER_ID, inQuotes(returnTender.getCardType()));
        sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT, returnTender.getTenderAmount().getStringValue());
        if ((returnTender.getApprovalCode() == null) || !(returnTender.getApprovalCode().equals("")))
        {
            sql.addColumn(FIELD_AUTHORIZATION_RESPONSE, returnTender.getApprovalCode());
        }
        if (returnTender.getExpirationDate() != null)
        {
            sql.addColumn(FIELD_EXPIRATION_DATE, dateToSQLDateString(returnTender.getExpirationDate().dateValue()));
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.debug(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertTenderLineItem", e);
        }
    }

    /**
     * Inserts the return tender data.
     *
     * @param dataConnection connection to the db
     * @param transaction retail transaction
     * @param returnTender tender to update
     * @exception DataException error saving data to the db
     */
    public void updateReturnTendersData(JdbcDataConnection dataConnection, TenderableTransactionIfc transaction,
            ReturnTenderDataElementIfc returnTender, int returnTenderSequenceNumber) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_RETURN_TENDER_DATA);

        // Fields
        sql.addColumn(FIELD_TENDER_TYPE_CODE, inQuotes(returnTender.getTenderType()));
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TENDER_MEDIA_ISSUER_ID, inQuotes(returnTender.getCardType()));
        sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT, returnTender.getTenderAmount().getStringValue());
        sql.addColumn(FIELD_AUTHORIZATION_RESPONSE, returnTender.getApprovalCode());
        if (returnTender.getExpirationDate() != null)
        {
            sql.addColumn(FIELD_EXPIRATION_DATE, dateToSQLDateString(returnTender.getExpirationDate().dateValue()));
        }

        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETURN_TENDER_SEQUENCE_NUMBER, returnTenderSequenceNumber);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.debug(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateTenderLineItem", e);
        }
    }

    /**
     * Returns the transaction exempt tax amount
     *
     * @param transaction the retail transaction
     * @return the tax exempt Amount
     */
    protected String getTaxExemptAmount(RetailTransactionIfc transaction)
    {
        return transaction.getTransactionTotals().getTaxInformationContainer().getTaxExemptAmount().toString();
    }


    /**
     * Inserts a Transaction Price Modifier. For now, this table is used for
     * ReSA. In POS, the transaction discounts go to the TR_LTM_DSC table but it
     * is not split up by line item. In order to figure out the discount amount
     * for each line item, a complex calculation must be implemented. It is
     * already implemented in POS, but it is not visible by ReSA (ReSA -export
     * module- doesn't depend on the domain module). Since we already have all
     * the correct amounts in the lineItem object, we can avoid replicating the
     * complex calculation for ReSA by writing the data to this new table.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sales/return line item
     * @param sequenceNumber The sequence number of the modifier
     * @param discountLineItem discount
     * @exception DataException upon error
     */
    public void insertSaleReturnPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_PRICE_MODIFIER);

        // Get the line and transaction number.
        String lineNumber = getLineItemSequenceNumber(lineItem);
        String tranNumber = getTransactionSequenceNumber(transaction);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, tranNumber);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, lineNumber);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, getSequenceNumber(sequenceNumber));
        sql.addColumn(FIELD_RECORD_CREATION_TIMESTAMP, getSQLCurrentTimestampFunction());
        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        if (discountLineItem != null)
        {
            // Item discount
            int promotionId = 0;
            int promotionComponentId = 0;
            int promotionComponentDetailId = 0;
            if (transaction.getTransactionDiscounts() != null && transaction.getTransactionDiscounts().length > 0)
            {
                promotionId = transaction.getTransactionDiscounts()[0].getPromotionId();
                promotionComponentId = transaction.getTransactionDiscounts()[0].getPromotionComponentId();
                promotionComponentDetailId = transaction.getTransactionDiscounts()[0].getPromotionComponentDetailId();
            }
            else
            {
                promotionId = discountLineItem.getPromotionId();
                promotionComponentId = discountLineItem.getPromotionComponentId();
                promotionComponentDetailId = discountLineItem.getPromotionComponentDetailId();
            }
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
                    getPriceModifierAssignmentBasis(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID, makeSafeString(discountLineItem
                    .getDiscountEmployeeID()));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
                    getPriceModifierDamageDiscountFlag(discountLineItem));
            sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
            sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
                    getPriceModifierReferenceID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
                    getPriceModifierReferenceIDTypeCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
                    inQuotes(discountLineItem.getAccountingMethod()));
            sql.addColumn(FIELD_PROMOTION_ID, promotionId);
            sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, promotionComponentId);
            sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, promotionComponentDetailId);
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, discountLineItem.getPricingGroupID());
            sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_REFERENCE, discountLineItem.getOrderItemDiscountLineReference());
        }
        else
        {
            // Price Override
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
            // if security override data exists, use it
            SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
            if (priceOverrideAuthorization != null)
            {
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
                        makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
                        .getEntryMethod().getIxRetailCode());
            }
            sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_REFERENCE, "-1");
        }
        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Insert SaleReturnPriceModifier", e);
        }
    }

    /**
     * Updates a Transaction Price Modifier. For now, this table is used for
     * ReSA. In POS, the transaction discounts go to the TR_LTM_DSC table but it
     * is not split up by line item. In order to figure out the discount amount
     * for each line item, a complex calculation must be implemented. It is
     * already implemented in POS, but it is not visible by ReSA (ReSA -export
     * module- doesn't depend on the domain module). Since we already have all
     * the correct amounts in the lineItem object, we can avoid replicating the
     * complex calculation for ReSA by writing the data to this new table.
     *
     * @param dataConnection Data Source
     * @param transaction The retail transaction
     * @param lineItem the sales/return line item
     * @param sequenceNumber The sequence number of the modifier
     * @param discountLineItem discount
     * @exception DataException upon error
     */
    public void updateSaleReturnPriceModifier(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, int sequenceNumber, ItemDiscountStrategyIfc discountLineItem)
            throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_SALE_RETURN_PRICE_MODIFIER);

        // Fields
        if (discountLineItem != null)
        {
            // Item discount
            int promotionId = 0;
            int promotionComponentId = 0;
            int promotionComponentDetailId = 0;
            if (transaction.getTransactionDiscounts() != null && transaction.getTransactionDiscounts().length > 0)
            {
                promotionId = transaction.getTransactionDiscounts()[0].getPromotionId();
                promotionComponentId = transaction.getTransactionDiscounts()[0].getPromotionComponentId();
                promotionComponentDetailId = transaction.getTransactionDiscounts()[0].getPromotionComponentDetailId();
            }
            else
            {
                promotionId = discountLineItem.getPromotionId();
                promotionComponentId = discountLineItem.getPromotionComponentId();
                promotionComponentDetailId = discountLineItem.getPromotionComponentDetailId();
            }
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, getDiscountRuleID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT, getPriceModifierPercent(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE, getPriceModifierMethodCode(discountLineItem));
            sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE,
                    getPriceModifierAssignmentBasis(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID, makeSafeString(discountLineItem
                    .getDiscountEmployeeID()));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT,
                    getPriceModifierDamageDiscountFlag(discountLineItem));
            sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL, getIncludedInBestDealFlag(discountLineItem));
            sql.addColumn(FIELD_ADVANCED_PRICING_RULE, getAdvancedPricingRuleFlag(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID,
                    getPriceModifierReferenceID(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE,
                    getPriceModifierReferenceIDTypeCode(discountLineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE, discountLineItem.getTypeCode());
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE,
                    inQuotes(discountLineItem.getAccountingMethod()));
            sql.addColumn(FIELD_PROMOTION_ID, promotionId);
            sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, promotionComponentId);
            sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, promotionComponentDetailId);
            sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, discountLineItem.getPricingGroupID());
        }
        else
        { // Price Override
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID, "0");
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE, getPriceModifierReasonCode(lineItem));
            sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, getPriceModifierAmount(lineItem));
            // if security override data exists, use it
            SecurityOverrideIfc priceOverrideAuthorization = lineItem.getItemPrice().getPriceOverrideAuthorization();
            if (priceOverrideAuthorization != null)
            {
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID,
                        makeSafeString(priceOverrideAuthorization.getAuthorizingEmployeeID()));
                sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE, priceOverrideAuthorization
                        .getEntryMethod().getIxRetailCode());
            }
        }

        sql.addColumn(FIELD_RECORD_LAST_MODIFIED_TIMESTAMP, getSQLCurrentTimestampFunction());

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER + " = " + getSequenceNumber(sequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "UpdateSaleReturnPriceModifier", e);
        }

        if (0 >= dataConnection.getUpdateCount())
        {
            throw new DataException(DataException.NO_DATA, "Update SaleReturnPriceModifier");
        }
    }


    /**
     * There three tables that associated with discounts on orders that can be saved.  If the
     * the order is an in-store order, the running totals of amount cancelled, amount
     * completed and amount returned must be saved to the Order Line Item Retail Price Modifier
     * table.
     *
     * Discounts on items are saved in different tables depending on scope of the discount.
     * A snap shot of the discount totals are always save to the either the Sale Return Order
     * Retail Price Modifier table (transaction scope) or the Order Retail Price Modifier (item
     * scope) table.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */
    protected void saveOrderDiscountStatus(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem,
            int discountSequenceNumber, ItemDiscountStrategyIfc discountLineItem)
                    throws DataException
    {
        //OrderLineItemRetailPriceModifier table should not contain Cross Channel items or suspended items
        if ( !(lineItem.getOrderItemStatus().isCrossChannelItem() || orderTransaction.isSuspended()) )   
        {
            if (orderTransaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                insertOrderLineItemDiscount(dataConnection, orderTransaction, lineItem, discountLineItem);
            }
            else if (lineItem.isReturnLineItem())
            {
                updateOrderLineItemDiscount(dataConnection, orderTransaction, lineItem, discountLineItem);   
            }
            else if (lineItem.getOrderItemStatus().getItemDispositionCode()==OrderConstantsIfc.ORDER_ITEM_DISPOSITION_SALE)
            {
                // insert order discount status for take with items added during pickup
                insertOrderLineItemDiscount(dataConnection, orderTransaction, lineItem, discountLineItem);
            }
            else
            {
                updateOrderLineItemDiscount(dataConnection, orderTransaction, lineItem, discountLineItem);
            }
        }


        if (discountLineItem.getDiscountScope() == DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION)
        {
            try
            {
                insertOrderItemTransactionDiscountStatus(dataConnection, orderTransaction, lineItem,
                        discountSequenceNumber, discountLineItem);
            }
            catch (DataException e)
            {
                updateOrderItemTransactionDiscountStatus(dataConnection, orderTransaction, lineItem,
                        discountSequenceNumber, discountLineItem);
            }
        }
        else
        {
            try
            {
                insertOrderItemDiscountStatus(dataConnection, orderTransaction, lineItem,
                        discountSequenceNumber, discountLineItem);
            }
            catch (DataException e)
            {
                updateOrderItemDiscountStatus(dataConnection, orderTransaction, lineItem,
                        discountSequenceNumber, discountLineItem);
            }
        }
    }

    /**
     * This method inserts the discount related data into the Order Line Item
     * Retail Price Modifier table.  This table is a part of the Order group of
     * tables and maintains current state of the discount amounts - total, completed,
     * cancelled, and returned.
     * As with all the other Order Tables, it contians in-store order information only.
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @param discountLineItem
     * @throws DataException
     */
    protected void insertOrderLineItemDiscount(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem,
            ItemDiscountStrategyIfc discountLineItem) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        OrderItemDiscountStatusIfc oids = itemStatus.getDiscountStatus(discountLineItem.getOrderItemDiscountLineReference());

        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);

        sql.addColumn(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, oids.getLineNumber());
        sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, inQuotes(itemStatus.getOriginalTransactionId().getStoreID()));
        sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, inQuotes(itemStatus.getOriginalTransactionId().getWorkstationID()));
        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT, oids.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT, oids.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT, oids.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, oids.getReturnedAmount().toString());

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItemDiscount", e);
        }
    }

    /**
     * This method updates the discount related data in the Order Line Item
     * Retail Price Modifier table.  This table is a part of the Order group of
     * tables and maintains current state of the discount amounts - total, completed,
     * cancelled, and returned.
     *
     * As with all the other Order Tables, it contians in-store order information only.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @param discountLineItem
     * @throws DataException
     */
    protected void updateOrderLineItemDiscount(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem,
            ItemDiscountStrategyIfc discountLineItem) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        OrderItemDiscountStatusIfc oids = itemStatus.getDiscountStatus(discountLineItem.getOrderItemDiscountLineReference());

        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);

        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT, oids.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT, oids.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT, oids.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, oids.getReturnedAmount().toString());

        sql.addQualifier(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, oids.getLineNumber());
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + makeSafeString(itemStatus.getOriginalTransactionId().getStoreID()) );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, makeSafeString(itemStatus.getOriginalTransactionId().getWorkstationID()));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItemDiscount", e);
        }
    }

    /**
     * This method insterts the discount related data into the Sale Return Order
     * Retail Price Modifier table.  This table is a part of the Transaction group of
     * tables and contains a snapshot of the discount amounts - total, completed,
     * cancelled, and returned - at the time of the transaction.
     *
     * This table contains data from both in-store and cross channel orders.
     *
     * This table contains the item's portion of a discount with transaction scope.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @param discountSequenceNumber
     * @param discountLineItem
     * @throws DataException
     */
    protected void insertOrderItemTransactionDiscountStatus(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem,
            int discountSequenceNumber, ItemDiscountStrategyIfc discountLineItem) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        OrderItemDiscountStatusIfc oids = itemStatus.getDiscountStatus(discountLineItem.getOrderItemDiscountLineReference());

        // set up SQL
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_SALE_RETURN_ORDER_PRICE_MODIFIER);

        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, discountSequenceNumber);
        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT, oids.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT, oids.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT, oids.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, oids.getReturnedAmount().toString());
        sql.addColumn(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, oids.getLineNumber());
        
        TransactionIDIfc originalTxnId = itemStatus.getOriginalTransactionId();
        if (originalTxnId!=null)
        {
            sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, inQuotes(originalTxnId.getStoreID()));
            sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, inQuotes(originalTxnId.getWorkstationID()));
        }     

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * This method updates the discount related data in the Sale Return Order
     * Retail Price Modifier table.  This table is a part of the Transaction group of
     * tables and contains a snapshot of the discount amounts - total, completed,
     * cancelled, and returned - at the time of the transaction.
     *
     * This table contains data from both in-store and cross channel orders.
     *
     * This table contains the item's portion of a discount with transaction scope.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @param discountSequenceNumber
     * @param discountLineItem
     * @throws DataException
     */
    protected void updateOrderItemTransactionDiscountStatus(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem,
            int discountSequenceNumber, ItemDiscountStrategyIfc discountLineItem) throws DataException
    {
        OrderItemDiscountStatusIfc oids = lineItem.getOrderItemStatus().getDiscountStatus(discountLineItem.getOrderItemDiscountLineReference());

        // set up SQL
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_SALE_RETURN_ORDER_PRICE_MODIFIER);

        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT, oids.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT, oids.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT, oids.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, oids.getReturnedAmount().toString());

        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, discountSequenceNumber);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * This method insterts the discount related data into the Order
     * Retail Price Modifier table.  This table is a part of the Transaction group of
     * tables and contains a snapshot of the discount amounts - total, completed,
     * cancelled, and returned - at the time of the transaction.
     *
     * This table contains data from both in-store and cross channel orders.
     *
     * This table contains the information of discounts with item scope.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @param discountSequenceNumber
     * @param discountLineItem
     * @throws DataException
     */
    protected void insertOrderItemDiscountStatus(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem,
            int discountSequenceNumber, ItemDiscountStrategyIfc discountLineItem) throws DataException
    {
    	OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        OrderItemDiscountStatusIfc oids = itemStatus.getDiscountStatus(discountLineItem.getOrderItemDiscountLineReference());

        // set up SQL
        SQLInsertStatement sql = new SQLInsertStatement();
        sql.setTable(TABLE_RETAIL_ORDER_PRICE_MODIFIER);

        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, discountSequenceNumber);
        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT, oids.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT, oids.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT, oids.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, oids.getReturnedAmount().toString());
        sql.addColumn(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        sql.addColumn(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, oids.getLineNumber());
        
        TransactionIDIfc originalTxnId = itemStatus.getOriginalTransactionId();
        if (originalTxnId!=null)
        {
            sql.addColumn(FIELD_ORDER_ORIGINAL_STORE_ID, inQuotes(originalTxnId.getStoreID()));
            sql.addColumn(FIELD_ORDER_ORIGINAL_WORKSTATION_ID, inQuotes(originalTxnId.getWorkstationID()));
        }     

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * This method updates the discount related data in the Order
     * Retail Price Modifier table.  This table is a part of the Transaction group of
     * tables and contains a snapshot of the discount amounts - total, completed,
     * cancelled, and returned - at the time of the transaction.
     *
     * This table contains data from both in-store and cross channel orders.
     *
     * This table contains the information of discounts with item scope.
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @param discountSequenceNumber
     * @param discountLineItem
     * @throws DataException
     */
    protected void updateOrderItemDiscountStatus(JdbcDataConnection dataConnection,
            OrderTransactionIfc orderTransaction, SaleReturnLineItemIfc lineItem,
            int discountSequenceNumber, ItemDiscountStrategyIfc discountLineItem) throws DataException
    {
        OrderItemDiscountStatusIfc oids = lineItem.getOrderItemStatus().getDiscountStatus(discountLineItem.getOrderItemDiscountLineReference());

        // set up SQL
        SQLUpdateStatement sql = new SQLUpdateStatement();
        sql.setTable(TABLE_RETAIL_ORDER_PRICE_MODIFIER);

        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT, oids.getTotalAmount().toString());
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT, oids.getCompletedAmount().toString());
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT, oids.getCancelledAmount().toString());
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT, oids.getReturnedAmount().toString());
       
        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER, discountSequenceNumber);

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");

            throw new DataException(DataException.UNKNOWN, "insertOrderLineItem", e);
        }
    }

    /**
     * Inserts a Promotion Line Item.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @param lineItemTypeCode line item type code
     * @exception DataException upon error
     */
    public void insertPromotionLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, PromotionLineItemIfc promotionLineItem, int promotionLineItemSequenceNumber)
            throws DataException
    {
        SQLInsertStatement sql = new SQLInsertStatement();

        // Table
        sql.setTable(TABLE_PROMOTION_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
        sql.addColumn(FIELD_PROMOTION_LINE_ITEM_SEQUENCE_NUMBER, promotionLineItemSequenceNumber);
        sql.addColumn(FIELD_PROMOTION_ID, promotionLineItem.getPromotionId());
        sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, promotionLineItem.getPromotionComponentId());
        sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, promotionLineItem.getPromotionComponentDetailId());
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, promotionLineItem.getDiscountAmount().toString());
        sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, promotionLineItem.getPricingGroupID());
        sql.addColumn(FIELD_PROMOTION_RECEIPT_LOCALE, inQuotes(String.valueOf(promotionLineItem.getReceiptLocale())));
        if (promotionLineItem.getPromotionName() != null)
        {
            sql.addColumn(FIELD_PROMOTION_RECEIPT_NAME, makeSafeString(promotionLineItem.getPromotionName()));
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "insertPromotionLineItem", e);
        }
    }

    /**
     * Updates a Promotion Line Item.
     *
     * @param dataConnection Data source connection to use
     * @param transaction The retail transaction
     * @param lineItem The sale/return line item
     * @param lineItemTypeCode line item type code
     * @exception DataException upon error
     */
    public void updatePromotionLineItem(JdbcDataConnection dataConnection, RetailTransactionIfc transaction,
            SaleReturnLineItemIfc lineItem, PromotionLineItemIfc promotionLineItem) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_PROMOTION_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_PROMOTION_LINE_ITEM_SEQUENCE_NUMBER, promotionLineItem.getPromotionLineItemSequenceNumber());
        sql.addColumn(FIELD_PROMOTION_ID, promotionLineItem.getPromotionId());
        sql.addColumn(FIELD_PROMOTION_COMPONENT_ID, promotionLineItem.getPromotionComponentId());
        sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID, promotionLineItem.getPromotionComponentDetailId());
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT, promotionLineItem.getDiscountAmount().getStringValue());
        sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID, promotionLineItem.getPricingGroupID());
        sql.addColumn(FIELD_PROMOTION_RECEIPT_LOCALE, inQuotes(String.valueOf(promotionLineItem.getReceiptLocale())));
        if (promotionLineItem.getPromotionName() != null)
        {
            sql.addColumn(FIELD_PROMOTION_RECEIPT_NAME, makeSafeString(promotionLineItem.getPromotionName()));
        }
        else
        {
            sql.addColumn(FIELD_PROMOTION_RECEIPT_NAME, null);
        }

        // qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                    + getLineItemSequenceNumber(lineItem));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updatePromotionLineItem", e);
        }
    }

    /**
     * Returns the Item Permanent Price Amount
     *
     * @param lineItem The SaleReturnLineItem
     * @return the item permanent price
     */
    protected String getItemPermanentPrice(SaleReturnLineItemIfc lineItem)
    {
        return (lineItem.getPLUItem().getItem().getPermanentPrice().getStringValue());
    }

    /**
     * Get a safe string for Authorization Date
     * @param lineItem
     * @return
     */
    public String getAuthorizationDateTime(EYSDate dateTime)
    {
        String date = "null";
        if (dateTime != null)
        {
            date = dateToSQLTimestampString(dateTime.dateValue());
        }

        return date;
    }

    /**
     * Returns the string value to be used in the database for the sales
     * associate modified flag
     *
     * @param the lineItem
     * @return string
     */
    public String getSalesAssociateModifiedFlag(SaleReturnLineItemIfc lineItem)
    {
        if (lineItem.getSalesAssociateModifiedFlag())
            return ("'1'");
        return ("'0'");
    }

    /**
       Returns the business date
       <P>
       @param  orderTransaction     of OrderTransactionIfc
       @return  business date
     */
    public String getBusinessDate(OrderTransactionIfc orderTransaction)
    {
        String businessDate = dateToSQLDateString(orderTransaction.getTimestampBegin().dateValue());

        return (businessDate);
    }
    /**
       Returns the order business date
       <P>
       @param  orderTransaction     of OrderTransactionIfc
       @return  order business date
     */
    public String getOrderBusinessDate(OrderTransactionIfc orderTransaction)
    {
        String orderBusinessDate = dateToSQLDateString(orderTransaction.getOrderStatus().getTimestampBegin().dateValue());

        return (orderBusinessDate);
    }

    /**
       Returns the order item status
       <P>
       @param  iStatus     of OrderItemStatusIfc
       @return  item staus
     */
    public int getItemStatus(OrderItemStatusIfc iStatus)
    {
        int itemStatus = iStatus.getStatus().getStatus();
        return itemStatus;
    }

    /**
       Returns the order item previous status
       <P>
       @param  iStatus     of OrderItemStatusIfc
       @return  item previous status
     */
    public int getItemPreviousStatus(OrderItemStatusIfc iStatus)
    {
        int itemPreviousStatus = iStatus.getStatus().getPreviousStatus();
        return itemPreviousStatus;
    }

    /**
       Returns the pickup date
       <P>
       @param  itemStatus     of OrderItemStatusIfc
       @return  pickup date
     */
    public String getPickupDate(OrderItemStatusIfc itemStatus)
    {
        EYSDate pickupDate = itemStatus.getPickupDate();
        if (pickupDate != null)
        {
            return dateToDateFormatString(pickupDate.dateValue());
        }
        return null;
    }

    /**
     * Returns the int value of the pricing group id.
     *
     * @param transaction
     * @return the pricingGroupID.
     */
    @SuppressWarnings("unchecked")
    private int getPricingGroupID(RetailTransactionIfc transaction)
    {
        SaleReturnLineItemIfc lineItem;
        Vector v = ((SaleReturnTransactionIfc)transaction).getItemContainerProxy().getLineItemsVector();
        for (int i = v.size() - 1; i >= 0; i--)
        {
            lineItem = (SaleReturnLineItemIfc)v.get(i);
            ItemDiscountStrategyIfc[] modifiers = lineItem.getItemPrice().getItemDiscounts();
            if (modifiers != null)
            {
                for (int j = modifiers.length - 1; j >= 0; j--)
                {
                    if (modifiers[j].getPricingGroupID() != -1)
                    {
                        return modifiers[j].getPricingGroupID();
                    }
                }
            }
        }

        // did not find a pricing group id
        return -1;
    }

    /**
     * Insert the external order line item into the database
     * @param dataConnection the data connection
     * @param transaction the retail transaction
     * @param lineItem the sale return line item
     * @throws DataException
     */
    public void insertExternalOrderLineItem(JdbcDataConnection dataConnection,
            RetailTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
         SQLInsertStatement sql = new SQLInsertStatement();

         // Table
         sql.setTable(TABLE_EXTERNAL_ORDER_LINE_ITEM);

         // Fields
         sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
         sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
         sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
         sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
         sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getLineItemSequenceNumber(lineItem));
         sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_ID, getExternalOrderItemID(lineItem));
         sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_PARENT_ID, getExternalOrderParentItemID(lineItem));
         sql.addColumn(FIELD_EXTERNAL_PRICING_FLAG, getExternalPricingFlag(lineItem));
         sql.addColumn(FIELD_EXTERNAL_TAX_FLAG, getExternalTaxFlag(lineItem));
         sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_UPDATE_SOURCE, getExternalOrderItemUpdateFlag(lineItem));
         try
         {
             dataConnection.execute(sql.getSQLString());
         }
         catch (DataException de)
         {
             logger.error(de.toString());
             throw de;
         }
         catch (Exception e)
         {
             throw new DataException(DataException.UNKNOWN, "insertExternalOrderLineItem", e);
         }
    }


    /**
     * Update the external order line item in the database
     * @param dataConnection the data connection
     * @param transaction the retail transaction
     * @param lineItem the sale return line item
     * @throws DataException
     */
    public void updateExternalOrderLineItem(JdbcDataConnection dataConnection,
            RetailTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        SQLUpdateStatement sql = new SQLUpdateStatement();

        // Table
        sql.setTable(TABLE_EXTERNAL_ORDER_LINE_ITEM);

        // Fields
        sql.addColumn(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addColumn(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addColumn(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getSequenceNumber(lineItem.getLineNumber()));
        sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_ID, getExternalOrderItemID(lineItem));
        sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_PARENT_ID, getExternalOrderParentItemID(lineItem));
        sql.addColumn(FIELD_EXTERNAL_PRICING_FLAG, getExternalPricingFlag(lineItem));
        sql.addColumn(FIELD_EXTERNAL_TAX_FLAG, getExternalTaxFlag(lineItem));
        sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_UPDATE_SOURCE, getExternalOrderItemUpdateFlag(lineItem));

        try
        {
            dataConnection.execute(sql.getSQLString());
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "updateExternalOrderLineItem", e);
        }
    }

    /**
     * It saves a external order line item into the database by inserting it first;
     * If failed, it will try to update the line item
     * @param dataConnection the data connection
     * @param transaction the retail transaction
     * @param lineItem the sale return line item
     * @throws DataException
     */
    public void saveExternalOrderLineItem(JdbcDataConnection dataConnection,
            RetailTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (transaction instanceof SaleReturnTransactionIfc)
        {
            SaleReturnTransactionIfc srTxn = (SaleReturnTransactionIfc)transaction;
            if (srTxn.hasExternalOrder())
            {
                try
                {
                    insertExternalOrderLineItem(dataConnection, transaction, lineItem);
                }
                catch (DataException e)
                {
                    updateExternalOrderLineItem(dataConnection, transaction, lineItem);
                }
            }
        }
    }

    /**
     * Get external order item ID
     * @param lineItem the sale return line item
     * @return the external order item ID
     */
    protected String getExternalOrderItemID(SaleReturnLineItemIfc lineItem)
    {
        String ret = makeSafeString(EXTERNAL_ORDER_ITEM_ID_TO_BE_FILLED_IN);
        if (lineItem.isFromExternalOrder())
        {
            ret = makeSafeString(lineItem.getExternalOrderItemID());
        }

        return ret;
    }

    /**
     * Get external order parent item ID
     * @param lineItem the sale return line item
     * @return the external order parent item ID
     */
    protected String getExternalOrderParentItemID(SaleReturnLineItemIfc lineItem)
    {
        String ret = "null";
        if (!StringUtils.isBlank(lineItem.getExternalOrderParentItemID()))
        {
            ret = makeSafeString(lineItem.getExternalOrderParentItemID());
        }
        return (ret);
    }

    /**
     * Get external pricing flag
     * @param lineItem the sale return line item
     * @return the external pricing flag
     */
    protected String getExternalPricingFlag(SaleReturnLineItemIfc lineItem)
    {
        return makeStringFromBoolean(lineItem.hasExternalPricing());
    }
    
    /**
     * Get external tax flag
     * @param lineItem the sale return line item
     * @return the external tax flag
     */
    protected String getExternalTaxFlag(SaleReturnLineItemIfc lineItem)
    {
        return makeStringFromBoolean(lineItem.hasExternalTax());
    }

    /**
     * Get the external order source update flag for an external order line item
     * @param lineItem the sale return line item
     * @return the external order line item action code
     */
    protected String getExternalOrderItemUpdateFlag(SaleReturnLineItemIfc lineItem)
    {
        return makeStringFromBoolean(lineItem.isExternalOrderItemUpdateSourceFlag());
    }

    /**
     * Get the merchandise hierarchy group id for a sale return line item
     * @param lineItem the sale return line item
     * @return the merchandise hierarchy group id
     */
    protected String getMerchandiseHierarchyGroupID(SaleReturnLineItemIfc lineItem)
    {
    	return (makeSafeString(lineItem.getPLUItem().getItemClassification().getMerchandiseHierarchyGroup()));
    }

    /**
     * Get the merchandise hierarchy group id for a sale return line item
     * @param lineItem the sale return line item
     * @return the merchandise hierarchy group id
     */
    protected String getManufacturerItemUPC(SaleReturnLineItemIfc lineItem)
    {
    	return (makeSafeString(lineItem.getPLUItem().getManufacturerItemUPC()));
    }

    /**
     * Returns the string value of the cross channel order item flag
     * @param the order item status
     * @return string with SLQ string boolean value
     */
    protected String getCrossChannelItemFlag(OrderItemStatusIfc itemStatus)
    {
        String value = "'0'";
        if (itemStatus.isCrossChannelItem())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the string value of the ship to store for pickup flag
     * @param the order item status
     * @return string with SLQ string boolean value
     */
    protected String getShipToStoreForPickupFlag(OrderItemStatusIfc itemStatus)
    {
        String value = "'0'";
        if (itemStatus.isShipToStoreForPickup())
        {
            value = "'1'";
        }
        return (value);
    }

    /**
     * Returns the use extended discount flag for a discount line item
     *
     * @param lineItem discount line item
     * @return use extended discount flag string
     */
    public String getPriceModifierUseExtendedDiscountFlag(ItemDiscountStrategyIfc lineItem)
    {
        // This flag is true if the discount is an audit
        boolean flag = lineItem instanceof ItemDiscountAuditStrategyIfc;
        return (makeStringFromBoolean(flag));
    }

    /**
     * Returns the order id at the transaction line item level
     * @param transaction the transaction
     * @param srli the sale return line item
     * @return the order id
     */
    protected String getOrderID(RetailTransactionIfc transaction, SaleReturnLineItemIfc srli)
    {
        // For a return order line item, the order id is specified at the line item level.
        String orderID = srli.getOrderID();

        // For an order initiate, complete, partial, or cancel transaction, the order id
        // is specified at the order transaction level.
        if (StringUtils.isBlank(orderID) && (transaction instanceof OrderTransactionIfc))
        {
            orderID = ((OrderTransactionIfc) transaction).getOrderID();
        }
        return orderID;
    }

    /**
     * Returns the serialized item flag.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the serialized item flag.
     */
    protected String getSerializedItemFlag(SaleReturnLineItemIfc lineItem)
    {
        String value = "'0'";
        if (lineItem.getPLUItem().getItemClassification().isSerializedItem())
        {
            value = "'1'";
        }
        return (value);
    }
    
    /**
     * Returns the serialized item flag.
     *
     * @param lineItem The SaleReturnLineItem
     * @return the serialized item flag.
     */
    protected String getExternalValidationSerializedItemFlag(SaleReturnLineItemIfc lineItem)
    {
        String validate = "0";
        String serialEntryItem = lineItem.getPLUItem().getItemClassification().getSerialEntryTime();
        if (serialEntryItem!=null && serialEntryItem.equals(STORE_RECEIVING_SERIALIZED_CAPTURE_TIME))
        {
            validate = "1";
        }
        return (validate);
    }
    
    /**
     * Returns true if the inventory mangement allows POS to create UIN
     *
     * @param lineItem The SaleReturnLineItem
     * @return the serialized item flag.
     */
    protected String isPOSAllowedToCreateUIN(SaleReturnLineItemIfc lineItem)
    {
        String validate = "0";
        boolean uinCreateFlag = lineItem.getPLUItem().getItemClassification().isExternalSystemCreateUIN();
        if (uinCreateFlag)
        {
            validate = "1";
        }
        return (validate);
    }
    
    /**
     * Returns the restrictive age of the item.
     * @param lineItem The SaleReturnLineItem
     * @return the restrictive age of the item
     */
    protected int getRestrictiveAge(SaleReturnLineItemIfc lineItem)
    {
        return lineItem.getPLUItem().getRestrictiveAge();
       
    }

}
