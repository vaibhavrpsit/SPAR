/* ===========================================================================
* Copyright (c) 2001, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/ItemContainerProxy.java /main/114 2014/07/17 13:24:08 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   11/14/14 - Use isNonRetrievedReceiptedItem for mpos and pos.
 *    yiqzhao   11/12/14 - For priceEntryRequired item, the price should be obtained 
 *                         from sale return line item, not pluItem.
 *    cgreene   10/17/14 - Move checkTransactionDiscount call inside of calculateBestDeal method.
 *    icole     10/03/14 - Fix for Bug 19693792, preserve discount for return item
 *                         from a receipted return item if it is deleted. 
 *    abhinavs  08/22/14 - Putting NPE check in place while resuming
 *                         suspended transaction from MPOS
 *    mchellap  08/11/14 - Add NPE to clearBestDealGroups for the transient
 *                         attribute bestDealGroups.
 *    cgreen    08/05/14 - Use existing PLUItems already in transaction or
 *                         consolidate them.
 *    icole     07/11/14 - Correct problem of non-taxable item being taxable if
 *                         partial tender with tax exempt PO is undone. The tax
 *                         mode should be set back to the original value.
 *    yiqzhao   06/30/14 - Add isInStorePriceDuringPickup check for re-price.
 *    sgu       06/20/14 - disable transactional discount and tax override for
 *                         pickup cancel order line item
 *    asinton   02/06/14 - added fix to prevent index out of bounds exception
 *    yiqzhao   02/03/14 - Save store coupon discount reference id and
 *                         reference id code.
 *    tksharma  12/18/13 - modified clearCustomerDiscountsByPercentage() to
 *                         clear customer transaction discounts from itemPrice
 *                         object
 *    cgreene   11/08/13 - added method addLineItemForDiscount with correct
 *                         logic for discerning non-receipt returns from
 *                         non-returns
 *    tksharma  10/24/13 - fixed the clearing of system scope transaction
 *                         discounts
 *    tksharma  10/15/13 - constructCSPAdvancedPricingRule method modfied to
 *                         provide a fix on multi threshold rules
 *    rgour     10/08/13 - preserving the line item number position while
 *                         spliting the item
 *    tksharma  10/07/13 - added abs() in replaceLineItem method for forward
 *                         port - 16437919
 *    tksharma  10/03/13 - added method clearSystemTransactionDiscounts for
 *                         transaction discount evaluation
 *    mkutiana  09/18/13 - fixed npe during retrieve transaction with no
 *                         customer
 *    yiqzhao   09/05/13 - Get correct kit item price when a quantity of one
 *                         kit component is greater than one.
 *    abondala  09/05/13 - deprecate some of the API related to tax that is not
 *                         referred as that is causing outOfMemory issues.
 *    yiqzhao   08/20/13 - Add javadoc.
 *    yiqzhao   08/20/13 - check external order items before adding transaction
 *                         level discount.
 *    rabhawsa  08/13/13 - adding item in source and target based on
 *                         nonreceiptedreturn flag.
 *    abondala  08/09/13 - remove the if condition in isRuleStillUsed that will
 *                         apply the coupon for the second target after
 *                         removing an item that has the coupon applied.
 *    abhinavs  08/08/13 - Fix to apply threshold discounts on suspended txns
 *    yiqzhao   08/08/13 - ADE auto merge.
 *    yiqzhao   08/08/13 - add revaluateLineItemPrice() with pluItem for
 *                         retrieving suspended txn.
 *    tksharma  08/07/13 - reset quantities for evaluateAmounts() method
 *    tksharma  07/30/13 - added rule description to TargetList in
 *                         factorAgainstRules method
 *    tksharma  07/25/13 - added code to add item to
 *                         DiscountSource/DiscountTarget list if the item is
 *                         not a return item
 *    jswan     07/08/13 - Fixed issue with retrieved transaction return line
 *                         items loosing their discounts.
 *    tksharma  06/26/13 - added null check to isPotentialSourceForTransaction
 *                         method
 *    tksharma  06/17/13 - fixed cloneLineItems method to clone the discounts
 *    tksharma  05/16/13 - fixed multithreshold to work for more than one
 *                         sources in addItemAdvancedPricingRules(..)
 *    tksharma  05/15/13 - added code to splitSourcesForTransaction Rules
 *    tksharma  05/10/13 - split sources for transaction level rules
 *    sgu       04/19/13 - added customer id null check
 *    yiqzhao   04/17/13 - Remove the new addPluItem interface which has
 *                         addSelectedForItemSplit flag.
 *    yiqzhao   04/17/13 - Create addPluItem for taking selectForItemSplit as
 *                         an argument.
 *    yiqzhao   04/16/13 - Fix the issue when enter quantity with item id in
 *                         sell item screen.
 *    tksharma  03/25/13 - removed
 *                         addPromotionsLineItems(SaleReturnLineItemIfc) and
 *                         addPromotionLineItem(srli, itemDiscStrategy), they
 *                         are no longer rewuired
 *    jswan     03/13/13 - Fixed an issue with ringing up store transaction
 *                         coupons caused by new fuctionality in
 *                         evaluateSourcesForTransactionRules().
 *    tksharma  03/06/13 - null pointer fix in checkTransactionDiscountVector
 *                         for description
 *    rabhawsa  02/28/13 - calling getExtendedDiscountedSellingPrice to fetch
 *                         the discounted price.
 *    tksharma  02/22/13 - modified method checkTransactionDiscountVector() to
 *                         add support for BuyNofXforZ$/%off
 *    tksharma  02/13/13 - addPromotionsLineItems(SaleReturnLineItemIfc) and
 *                         addPromotionLineItem(srli, itemDiscStrategy) for
 *                         adding discountRule name to TransactionLineItems
 *    tksharma  01/31/13 - added clearance Price change entry to retail price
 *                         modifier table
 *    tksharma  01/08/13 - moved the isRuleStillUsed code as a seperate method.
 *                         Called it for transactionDiscounts too
 *    tksharma  12/09/12 - added checkTransactionDiscountVector method.. to
 *                         check and evaluate if the discount should be applied
 *                         or removed.
 *    blarsen   06/29/12 - Refactored areAllStoreCouponsApplied() to support
 *                         new method unappliedStoreCoupons(). The later is
 *                         needed for MPOS to give operator more info about
 *                         which coupon needs attention.
 *    asinton   09/10/12 - prevent null references being added to the
 *                         transaction discount list
 *    tksharma  08/02/12 - multithreshold-merge with sthallam code
 *    tksharma  08/02/12 - multithreshold- discount rule
 *    icole     07/13/12 - Forward port tax override label on receipt is
 *                         printing incorrectly.
 *    sthallam  07/10/12 - Enhanced RPM Integration - Multithreshold discount
 *                         rules
 *    tksharma  06/06/12 - Enhanced RPM Integration - New Discount Rules
 *    icole     04/18/12 - Forward port fix for wrong item removed from sell
 *                         item screen for price adjustment.
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    tksharma  08/02/11 - setTransactionTaxOnItems(boolean) changed to set
 *                         price
 *    blarsen   12/22/10 - Changed to use an updated (and consistent) method in
 *                         DiscountUtility.
 *    cgreene   12/01/10 - implement saving applied promotion names into
 *                         tr_ltm_prm table
 *    jkoppolu  11/26/10 - Extended price should not be reverted back to the
 *                         previous price when a delivery is added to an item
 *                         that was included in a promotion and price override
 *                         on ORPOS.
 *    sgu       11/09/10 - change pricing group checking condition
 *    acadar    10/18/10 - fix the IllegalAccessException and store coupon
 *                         being considered for external order items
 *    npoola    09/02/10 - Defualt size code is not needed incase it is null
 *    ohorne    08/30/10 - fix for IllegalStateException in
 *                         areAllStoreCouponsApplied()
 *    rrkohli   07/29/10 - fix to show error message when transaction discount
 *                         or store coupon has been applied and no discount
 *                         eligible items exist on the transaction
 *    rsnayak   07/23/10 - Price override for Return without Recipt
 *    sgu       06/23/10 - added check in getExternalOrderLineItems api
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    sgu       06/10/10 - fix tabs
 *    sgu       06/10/10 - negate the quantity if the external order item is a
 *                         return
 *    sgu       06/01/10 - check in after merge
 *    sgu       06/01/10 - check in order sell item flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abhayg    05/21/10 - discount rule fix
 *    abhayg    05/21/10 - discount rule fix
 *    sgu       05/19/10 - minor fixes for external order APIs
 *    sgu       05/19/10 - enhance APIs to disallow discount rules for items
 *                         with external pricing
 *    sgu       05/18/10 - set external send flag on shipping packages
 *    sgu       05/18/10 - add external order plu and return line item
 *    sgu       05/18/10 - add APIs to add sale return line item with an
 *                         external order item
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/27/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    vapartha  02/08/10 - Changed code so that the pricingRule is added only
 *                         if the rule doesnt have a pricinggroup id.
 *    aariyer   01/22/10 - For the discounts
 *    jswan     01/11/10 - Fix 2 issues with return with lowest price in X
 *                         days: 1. Prevent customer specific price change if
 *                         transaction ID has already been entered; 2. Allow
 *                         reset of price when new customer is missing customer
 *                         specific pricing ID.
 *    abondala  01/03/10 - update header date
 *    jswan     12/14/09 - Modifications for 'Min return price for X days'
 *                         feature.
 *    cgreene   10/27/09 - fix line item splitting when lines are non-receipt
 *                         returns
 *    cgreene   10/26/09 - refactored calculateBestDeal to ensure sales and
 *                         returns are compared separately and not mixed.
 *    cgreene   04/14/09 - convert pricingGroupID to integer instead of string
 *    jswan     04/14/09 - Modified to fix conflict between multi quantity
 *                         items and items that have been marked for Pickup or
 *                         Delivery.
 *    cgreene   03/31/09 - fixed missing promotion name on receipt by
 *                         refactoring appliedPromotion to ItemPrice object and
 *                         setPromotionName from PriceChange.setCloneAttributes
 *    mahising  03/20/09 - Fixed issue for CSP when re-activated expired
 *                         discount rule for customer pricing
 *    kkhirema  03/20/09 - Updated the label from Link Customer to Customer as
 *                         per transaction header standard
 *    cgreene   03/19/09 - refactoring changes
 *    cgreene   03/12/09 - fix deal used past its applicationLimit error by
 *                         fixing logic in updateDiscountLimits to recursively
 *                         call itself
 *    sbeesnal  03/06/09 - In removeLineItem method, check whether store coupon
 *                         item which is to be removed is present in the
 *                         current sale transaction or not.
 *    mahising  03/04/09 - fixed customer specific pricing related issue for
 *                         deleting an item
 *    mahising  02/22/09 - Fixed issue for printing promotion name in receipt
 *    deghosh   02/12/09 - Cleaning the deprecated method toJournalString()
 *    vchengeg  01/27/09 - ej defect fixes
 *    npoola    12/18/08 - PDO cart image issue fix
 *    mahising  12/09/08 - rework of base issue
 *    npoola    12/08/08 - customer menu error fixed and also
 *                         ItemContainerProxy fixed
 *    mahising  12/04/08 - JUnit fix and SQL fix
 *    npoola    11/30/08 - CSP POS and BO changes
 *    lslepeti  11/06/08 - code refresh
 *    lslepeti  11/05/08 - add rules of type BuyNorMoreOfXforZ%off and
 *                         BuyNorMoreOfXforZ$each
 *    ranojha   11/04/08 - Code refreshed to tip
 *    ranojha   11/04/08 - Changes for Tax Exempt reason codes
 *    acadar    11/03/08 - localization of transaction tax reason codes
 *    acadar    11/03/08 - localization of reason codes for discounts and
 *                         merging to tip
 *    acadar    10/30/08 - use localized reason codes for item and transaction
 *                         discounts
 *    akandru   10/30/08 - EJ changes
 *    acadar    10/28/08 - localization for item tax reason codes
 *    akandru   10/23/08 - new helper class is used
 *    akandru   10/21/08 - new method added to take the client's journal
 *                         locale.
 *    akandru   10/21/08 - new method added to take the client's journal
 *                         locale.
 *    akandru   10/20/08 - new method tojournalstring which takes Locale is
 *                         added.
 *    akandru   10/20/08 -
 *    cgreene   10/02/08 - merged with tip
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      16   360Commerce 1.15        5/21/2007 9:17:02 AM   Anda D. Cadar   Ej
           changes and cleanup
      15   360Commerce 1.14        5/18/2007 12:15:45 PM  Maisa De Camargo
           Added PromotionLineItem
      14   360Commerce 1.13        5/16/2007 5:01:58 PM   Owen D. Horne
           CR#24874 - Merged fix from v8.0.1
           8    .v8x       1.6.1.0     4/10/2007 10:15:11 AM  Michael Wisbauer
           Modified how sequence numbers are being set mostly for the orginal
           transactions for refunds so items are updated correclty in the db.
      13   360Commerce 1.12        5/14/2007 4:36:07 PM   Brett J. Larsen 26477
            - persist vat shipping charge

           fixing header on new addSendPackageInfo

      12   360Commerce 1.11        5/14/2007 4:32:28 PM   Brett J. Larsen CR
           26477 - persist vat shipping charge

           overloading addSendPackageInfo to include tax amounts
           (required when reading SHP_RDS_SLS_RTN)

      11   360Commerce 1.10        5/1/2007 12:16:12 PM   Brett J. Larsen CR
           26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
           feature)
      10   360Commerce 1.9         4/25/2007 10:00:41 AM  Anda D. Cadar   I18N
           merge
      9    360Commerce 1.8         4/18/2007 1:37:20 PM   Ashok.Mondal    CR
           3881 : V7.2.2 merge to trunk.
      8    360Commerce 1.7         4/17/2007 12:32:14 PM  Ashok.Mondal    CR
           4034 :V7.2.2 merge to trunk.
      7    360Commerce 1.6         5/12/2006 5:26:31 PM   Charles D. Baker
           Merging with v1_0_0_53 of Returns Managament
      6    360Commerce 1.5         1/25/2006 4:11:05 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:41:39 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:49 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:28:30 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:24 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:11:36 PM  Robert Pearse
     $: ItemContainerProxy.java,v $
      9    .v710     1.2.2.0     9/21/2005 13:39:35     Brendan W. Farrell
           Initial Check in merge 67.
      8    .v700     1.2.3.4     12/27/2005 15:06:16    Deepanshu       CR
           8052: Check for Employee discount eligibility
      7    .v700     1.2.3.3     11/2/2005 16:54:49     Jason L. DeLeau 4199:
           Make methods public for extensibility.
      6    .v700     1.2.3.2     11/1/2005 11:06:34     Jason L. DeLeau 4181:
           Remove deprecated reference to getGiftCardRegistry
      5    .v700     1.2.3.1     10/28/2005 12:18:14    Jason L. DeLeau 176:
           Make sure that when a coupon is deleted, the discount is removed.
      4    .v700     1.2.3.0     10/27/2005 15:06:47    Deepanshu       CR
           6104: Changed the condition to check item discounts.
      3    360Commerce1.2         3/31/2005 15:28:30     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:24     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:11:36     Robert Pearse
     $
     Revision 1.20.2.4  2004/11/30 16:59:31  jdeleau
     @scr 7628 Remove transaction scope check for items tax exceeding the item price.

     Revision 1.20.2.3  2004/11/04 22:55:15  jdeleau
     @scr 7599 Make sure default tax rate is set.

     Revision 1.20.2.2  2004/11/01 17:04:36  jdeleau
     @scr 7520 Use factory for ItemTax object instead of direct instantiation.

     Revision 1.20.2.1  2004/10/19 20:59:36  jdeleau
     @scr 7391 Tax Exempt items must override the send tax rules, for
     tax exempt transactions even after send tax rules are already retrieved.

     Revision 1.20  2004/09/23 00:30:54  kmcbride
     @scr 7211: Inserting serialVersionUIDs in these Serializable classes

     Revision 1.19  2004/08/23 16:15:45  cdb
     @scr 4204 Removed tab characters

     Revision 1.18  2004/07/27 00:07:46  jdeleau
     @scr 6251 Make sure when tax is toggled off, that the original tax is charged when its toggled back on

     Revision 1.17  2004/07/21 15:45:06  jdeleau
     @scr 6382 Don't check if tax exceeds item amount unless the tax
     being charged is of item scope.

     Revision 1.16  2004/07/06 20:45:34  jeffp
     @scr 5427 Added check to see if item was priced at its original price in the replaceLineItem() method

     Revision 1.15  2004/06/23 23:37:42  cdb
     @scr 5450 Removed advanced pricing rules associated with a given item
     that may have an impact on best deal calculations of other items.

     Revision 1.14  2004/06/10 23:06:34  jriggins
     @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service

     Revision 1.13  2004/06/02 19:01:53  lzhao
     @scr 4670: add shippingRecords table.

     Revision 1.12  2004/05/06 20:50:57  epd
     @scr 4267 Changes to logic to make sure transaction was really retrieved when the code thought it was

     Revision 1.11  2004/04/28 19:51:45  jriggins
     @scr 3979 Code review cleanup

     Revision 1.10  2004/04/22 07:25:37  jriggins
     @scr 3979 Changed getLineItemsExcluding() to use checkItemType() instead of checkKitType()

     Revision 1.9  2004/04/15 15:38:14  jriggins
     @scr 3979 Added logic for adding and removing price adjustment line items. Also added checkItemType() method

     Revision 1.8  2004/04/03 00:19:54  jriggins
     @scr 3979 Added getPriceAdjustmentLineItemsExcluding() method

     Revision 1.7  2004/03/16 18:27:07  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.6  2004/03/10 21:40:52  cdb
     @scr 0 Repaired some unit tests and added null pointer
     safety to domain objects.

     Revision 1.5  2004/03/10 19:41:51  baa
     @scr work for parsing size from scanned item

     Revision 1.4  2004/03/02 23:16:55  aarvesen
     @scr 3561 use the item size from the ReturnItem to set the code on the item container

     Revision 1.3  2004/02/12 17:13:57  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:32  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.2   Oct 29 2003 13:32:00   baa
 * refactoring
 * Resolution for 3392: 610/700 Cleanup
 *
 *    Rev 1.19   Jul 24 2003 13:26:20   jgs
 * Prevent advanced discounts with negative values from being added to the best deal groups array list.
 * Resolution for 3236: Research - Deal Discounts and the item is less than the discount amount
 *
 *    Rev 1.18   Jul 24 2003 09:28:08   jgs
 * The clone removes advanced pricing discounts from the items in the container and rebuilds them from the advanced pricing rules.  I added a check to see if the advanced pricing rules were available.  This preserves the discounts for completed transactions that have been retrieved from the database.
 * Resolution for 3218: Layaway  Pickup\Return \Delete/Void totals are not correct.
 *
 *    Rev 1.17   Jul 19 2003 14:49:14   sfl
 * Don't reset line items' line number in the removeItemByTaxGroup method. It's been
 * done inside addItemByTaxGroup() method
 * beause the storage data is refreshed.
 * Resolution for POS SCR-3081: Deleting a Item from list of item on sell item screen changes the order of items and removes items form the sale item screen that were not deleted.
 *
 *    Rev 1.16   Jul 17 2003 06:45:06   jgs
 * Fixed problems caused by addition of Markdowns to the allowable number of ItemDiscountStrategy objects associated with an item.
 * Resolution for 3037: The ejournal for a transaction with multiple (3) % discounts applies and removes the first two discounts on the ejournal.
 *
 *    Rev 1.15   Jul 11 2003 09:39:50   jgs
 * Added code to clearBestDealDiscounts() to set source items back to available for best deal processing.
 * Resolution for 3146: A Discount Rule with specified target item/quantity is applying the discount to more than one target item.
 *
 *    Rev 1.14   Jun 04 2003 09:11:36   RSachdeva
 * Null check added
 * Resolution for POS SCR-2547: Modify business cust's discount and link the business customer to sale transaction, POS is crashed.
 *
 *    Rev 1.13   May 30 2003 16:41:00   sfl
 * Commented out not necessary tax rule retrieve.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.12   May 18 2003 09:06:36   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.11   May 08 2003 17:56:40   sfl
 * Apply abs() to item quantity in case return items' quanity is used to control how many times a loop shall go.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.10   May 07 2003 09:34:16   sfl
 * Added additional null checking to handle potential situation that not tax rule assigned to sale item during addItemByTaxGroup.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.9   May 06 2003 17:46:40   sfl
 * Added additional condition checking to make sure that the tax amount obtained from tax table related rules are correctly updated when item price are overriden.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.8   Apr 16 2003 14:18:50   sfl
 * Took away an extra ;.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.7   Mar 20 2003 09:30:54   jgs
 * Changes due to code review.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.6   Mar 19 2003 16:53:10   sfl
 * 1. Included the ItemsByTaxGroup in the setCloneAttributes
 *     method.
 * 2. Included the non-receipt return items in the
 *     ItemsByTaxGroup hashtable.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.5   Feb 28 2003 14:57:00   sfl
 * Added new data attribute and supporting methods for
 * threshold amount based tax rule processing.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.4   Feb 15 2003 14:52:14   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.3   Dec 13 2002 10:39:18   pjf
 * Don't clone best deal groups, recalculate best deal instead.
 * Resolution for 101: Merge KB discount fixes.
 *
 *    Rev 1.2   04 Oct 2002 18:03:16   sfl
 * Added the enhancement for the line items clone so that
 * the tax rules attached to the PLUItems will be included
 * during clone.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.1   05 Jun 2002 17:11:54   jbp
 * changes for pricing updates
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:57:58   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.comparators.Comparators;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.DiscountItemIfc;
import oracle.retail.stores.domain.discount.DiscountListIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.DiscountSourceIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.discount.PromotionLineItem;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.SuperGroupIfc;
import oracle.retail.stores.domain.discount.Threshold;
import oracle.retail.stores.domain.discount.ThresholdIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.ItemConstantsIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponent;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.log4j.Logger;

/**
 * This class provides the implementation for an entity which uses a list of
 * items. Entities which would use this class are SaleReturnTransaction and
 * Order.
 * <P>
 * The ItemContainerProxy class contains the array of items plus the other
 * modifiers which operate on the using entity, such as discounts and tax. These
 * are contained herein because these modifiers force a change in the items in
 * the container (such as when a transaction discount or transaction tax
 * modifier is pro-rated across the items).
 *
 * @see oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc
 * @see oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc
 * @see oracle.retail.stores.domain.order.OrderIfc
 */

public class ItemContainerProxy implements ItemContainerProxyIfc
{
    // This id is used to tell the compiler not to generate a new
    // serialVersionUID.
    static final long serialVersionUID = 1839263975946547262L;

    /** The logger to which log messages will be sent. */
    private static Logger logger = Logger.getLogger(ItemContainerProxy.class);

    /** A constant to remember the base currency type for this transaction to speed performance. */
    private final CurrencyIfc ZERO;

    /**
     * All of the line items in the tranaction.
     */
    protected Vector<AbstractTransactionLineItemIfc> lineItemsVector;

    /**
     * All of the transaction discounts applied to the transaction.
     */
    protected Vector<TransactionDiscountStrategyIfc> transactionDiscountsVector;

    /**
     * Advanced pricing rules that are possible to be applied in this transaction.
     * Once applied, a rule become part of {@link #bestDealWinners}. This map is
     * not a transient collection because any subsequent change to the transaction
     * may cause one of the non-winners to become a winner.
     */
    protected HashMap<String, DiscountRuleIfc> advancedPricingRules;

    /**
     * Customer-specific advanced pricing rules that are active in this
     * transaction.
     */
    protected HashMap<String, DiscountRuleIfc> cspAdvancedPricingRules;

    /**
     * The possible best deal groups that are calulated from the advanced
     * pricing rules. The groups that can be combined to give the best discount
     * become part of {@link #bestDealWinners}. This list is only used during
     * calculations.
     */
    protected transient ArrayList<BestDealGroupIfc> bestDealGroups;

    /**
     * The advanced pricing rules that qualified and have been calculated to
     * give the best deal to the customer.
     */
    protected ArrayList<BestDealGroupIfc> bestDealWinners;

    /**
     * list used to track the number of times an advanced pricing rule has been
     * applied. This list is only used during calculations.
     */
    protected transient DiscountListIfc discountLimits = null;

    /**
     * sales associate
     */
    protected EmployeeIfc salesAssociate;

    /**
     * default registry
     */
    protected RegistryIDIfc defaultRegistry = null;

    /**
     * transaction tax data
     */
    protected TransactionTaxIfc transactionTax = null;

    /**
     * amount of line voids (deleted lines)
     */
    protected CurrencyIfc amountLineVoids = null;

    /**
     * units on line voids (deleted lines)
     */
    protected BigDecimal unitsLineVoids = null;

    /**
     * Hashtable to hold tax group id (as key) and the vector of items of that
     * tax group
     * @deprecated since 14.0. No replacement.
     */
    @Deprecated
    protected Hashtable<String, Vector<SaleReturnLineItemIfc>> itemsByTaxGroup;

    /**
     * pricing Group ID.
     */
    protected int pricingGroupID;

    /**
     * CustomerIfc to hold custmer object from linkCustomer();
     */
    protected CustomerIfc customer = null;

    /**
     * Constructs ItemContainerProxy object.
     */
    public ItemContainerProxy()
    {
        ZERO = DomainGateway.getBaseCurrencyInstance();
        initialize();
    }

    /**
     * Initialize values.
     */
    protected void initialize()
    {
        lineItemsVector = new Vector<AbstractTransactionLineItemIfc>(2);
        transactionDiscountsVector = new Vector<TransactionDiscountStrategyIfc>(2);
        transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
        advancedPricingRules = new HashMap<String, DiscountRuleIfc>(0);
        bestDealGroups = new ArrayList<BestDealGroupIfc>(2);
        bestDealWinners = new ArrayList<BestDealGroupIfc>(2);
        amountLineVoids = DomainGateway.getBaseCurrencyInstance();
        unitsLineVoids = BigDecimal.ZERO;
        discountLimits = DomainGateway.getFactory().getDiscountListInstance();
        itemsByTaxGroup = new Hashtable<String, Vector<SaleReturnLineItemIfc>>(0);
        cspAdvancedPricingRules = new HashMap<String, DiscountRuleIfc>(2);
    }

    /**
     * Clones ItemContainerProxy object
     *
     * @return instance of ItemContainerProxy object
     */
    public Object clone()
    {
        ItemContainerProxy itemContainerProxy = new ItemContainerProxy();

        setCloneAttributes(itemContainerProxy);

        // regenerate best deal discounts on the cloned items
        itemContainerProxy.calculateBestDeal();
        return itemContainerProxy;
    }

    /**
     * Sets clone attributes. This method is provided to facilitate
     * extensibility.
     *
     * @param newClass new instance of ItemContainerProxy
     */
    protected void setCloneAttributes(ItemContainerProxy newClass)
    {
        if (salesAssociate != null)
        {
            newClass.setSalesAssociate((EmployeeIfc)(salesAssociate.clone()));
        }
        newClass.setTransactionTax((TransactionTaxIfc)transactionTax.clone());
        // clone discounts
        TransactionDiscountStrategyIfc[] tds = getTransactionDiscounts();
        if (tds != null)
        {
            TransactionDiscountStrategyIfc[] tclone = new TransactionDiscountStrategyIfc[tds.length];
            for (int i = 0; i < tds.length; i++)
            {
                tclone[i] = (TransactionDiscountStrategyIfc)tds[i].clone();
            }
            newClass.setTransactionDiscounts(tclone);
        }
        // clone line items
        newClass.setLineItems(cloneLineItems());
        // confirm gift registry exists before cloning
        if (defaultRegistry != null)
        {
            newClass.setDefaultRegistry((RegistryIDIfc)defaultRegistry.clone());
        }
        Iterator<DiscountRuleIfc> i = advancedPricingRules();
        AdvancedPricingRuleIfc rule = null;
        while (i.hasNext())
        {
            rule = (AdvancedPricingRuleIfc)i.next();
            newClass.addAdvancedPricingRule((AdvancedPricingRuleIfc)rule.clone());
        }
        Iterator<DiscountRuleIfc> k = cspAdvancedPricingRules.values().iterator();
        AdvancedPricingRuleIfc csprule = null;
        while (k.hasNext())
        {
            csprule = (AdvancedPricingRuleIfc)k.next();
            newClass.addCSPAdvancedPricingRule((AdvancedPricingRuleIfc)csprule.clone());
        }

        // confirm amountLineVoids exists before cloning
        if (amountLineVoids != null)
        {
            newClass.setAmountLineVoids((CurrencyIfc)amountLineVoids.clone());
        }
        // confirm unitsLineVoids exists before cloning
        if (unitsLineVoids != null)
        {
            newClass.setUnitsLineVoids(unitsLineVoids);
        }
        newClass.associateKitComponentsWithHeaders();
        if (customer != null)
        {
            newClass.linkCustomer((CustomerIfc)customer.clone());
        }
    }

    /**
     * Clones SaleReturnLineItem vector.
     *
     * @return clone of sale return line items vector
     */
    public AbstractTransactionLineItemIfc[] cloneLineItems()
    {
        AbstractTransactionLineItemIfc[] atli = getLineItems();
        AbstractTransactionLineItemIfc[] tclone = null;
        if (atli != null)
        {
            tclone = new AbstractTransactionLineItemIfc[atli.length];
            for (int i = 0; i < atli.length; i++)
            {
                tclone[i] = (AbstractTransactionLineItemIfc)atli[i].clone();
                // clear advanced pricing discounts and make cloned items
                // eligible
                // for best deal calculation, discounts will be regenerated
                // by the call to calculateBestDeal() in this.clone()
                /*
                 * This is comment on the comment above. This will only work if
                 * there are advanced pricing rules in the this container, i.e.
                 * while the transaction is being created. If a completed
                 * transaction has been retrieved from the database, then the
                 * pricing rules are not available to rebuild the discounts the
                 * discounts disapear from the transaction. Checking for the
                 * availability of the rules fixes this problem.
                 */
                if (tclone[i] instanceof SaleReturnLineItem && !advancedPricingRules.isEmpty()
                        && !((SaleReturnLineItem)tclone[i]).isFromTransaction())
                {
                    ((SaleReturnLineItem)tclone[i]).setSourceAvailable(true);
                    ((SaleReturnLineItem)tclone[i]).removeAdvancedPricingDiscount();
                }
            }
        }

        return (tclone);
    }

    /**
     * Converts a Collection of line items to a SaleReturnLineItemIfc[].
     *
     * @param items Collection to convert
     * @return array of SaleReturnLineItemIfc[]
     * @throws ArrayStoreException if elements in Collection are not proper type
     */
    public static SaleReturnLineItemIfc[] toItemArray(Collection<AbstractTransactionLineItemIfc> items)
    {
        SaleReturnLineItemIfc[] itemArray = new SaleReturnLineItemIfc[0];
        if (items != null)
        {
            // create an array containing line items
            itemArray = items.toArray(itemArray);
        }

        return itemArray;
    }

    /**
     * Associates the kit component line item in this container with their
     * respective header line items.
     */
    public void associateKitComponentsWithHeaders()
    {
        Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator();
        SaleReturnLineItemIfc item = null;

        while (i.hasNext())
        {
            item = (SaleReturnLineItemIfc)i.next();
            if (item.isKitHeader())
            {
                ((KitHeaderLineItemIfc)item).associateKitComponentLineItems(lineItemsVector.iterator());
            }
        }
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
        lineItem.setLineNumber(lineItemsVector.size());
        lineItemsVector.addElement(lineItem);
        resetLineItemNumbers();
    }

    /**
     * Adds a single PLU item to the transaction.
     *
     * @param pItem PLU item
     * @return transaction line item
     * @see #addPLUItem(PLUItemIfc, BigDecimal)
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
     * @param reference line reference (used in CrossReach)
     * @return transaction line item
     * @see #addPLUItem(PLUItemIfc, BigDecimal)
     */
    public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty, String reference)
    {
        SaleReturnLineItemIfc srli = addPLUItem(pItem, qty);
        srli.setLineReference(reference);
        return (srli);
    }

    /**
     * Adds a PLU item to the transaction. Calls
     * {@link #createSaleReturnLineItemInstance(PLUItemIfc)} then
     * {@link #completeAddPLUItem(SaleReturnLineItemIfc, PLUItemIfc, BigDecimal)}.
     *
     * @param pItem PLU item
     * @param qty quantity
     * @return transaction line item
     */
    public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, BigDecimal qty)
    {
        SaleReturnLineItemIfc srli = createSaleReturnLineItemInstance(pItem);
        completeAddPLUItem(srli, qty);
        // pass back line item
        return srli;
    }

    /**
     * Adds a PLU item to the transaction.s
     *
     * @param pItem PLU item
     * @param pExternalOrderItem external order item
     * @return transaction line item
     */
    public SaleReturnLineItemIfc addPLUItem(PLUItemIfc pItem, ExternalOrderItemIfc pExternalOrderItem)
    {
        SaleReturnLineItemIfc srli = createSaleReturnLineItemInstance(pItem, pExternalOrderItem);
        completeAddPLUItem(srli, pExternalOrderItem.getQuantity());
        // pass back line item
        return srli;
    }

    /**
     * Complete adding a sale line item
     *
     * @param srli the new sale line item
     * @param qty item quantity
     */
    protected void completeAddPLUItem(SaleReturnLineItemIfc srli, BigDecimal qty)
    {
        if (srli.isKitHeader())
        {
            addKitComponentItems((KitHeaderLineItemIfc)srli);
        }

        // get pricing group id
        if (customer != null && customer.getPricingGroupID() != null && !srli.hasExternalPricing())
        {
            revaluateLineItemPrice(srli);
        }
        // required for best deal calculations
        srli.calculateLineItemPrice();

        // if quantity is not 1, modify it
        if (qty.compareTo(BigDecimalConstants.ONE_AMOUNT) != 0)
        {
            srli.modifyItemQuantity(qty);
        }

        srli.setLineNumber(lineItemsVector.size());
        lineItemsVector.addElement(srli);
        resetLineItemNumbers();

        if (customer != null && customer.getPricingGroupID() != null && !srli.hasExternalPricing())
        {
            constructCSPAdvancedPricingRule(srli.getPLUItem());
        }

        if (!srli.hasExternalPricing())
        {
            addItemAdvancedPricingRules(srli.getPLUItem());
            calculateBestDeal();
        }
    }

    /**
     * Creates and adds KitComponentLineItemIfcs to the transaction for a
     * KitHeaderLineItem instance. Associates kit component line items with
     * header line item.
     *
     * @param header KitHeaderLineItemIfc
     */
    public void addKitComponentItems(KitHeaderLineItemIfc header)
    {
        // get an iterator over the header's ItemKitIfc's components
        // (KitComponentIfcs)
        ItemKitIfc headerPLUItem = (ItemKitIfc)header.getPLUItem();
        Iterator<?> componentPLUItems = headerPLUItem.getComponentItemIterator();
        KitComponentIfc component = null;
        ArrayList<SaleReturnLineItemIfc> componentLineItems = new ArrayList<SaleReturnLineItemIfc>();

        // for each KitComponentIfc, create a KitComponentLineItem and add to
        // the transaction
        while (componentPLUItems.hasNext())
        {
            component = (KitComponentIfc)componentPLUItems.next();
            componentLineItems.add(addPLUItem(component, component.getQuantity()));
        }

        for (Iterator<SaleReturnLineItemIfc> i = componentLineItems.iterator(); i.hasNext();)
        {
            header.addKitComponentLineItem((KitComponentLineItemIfc)i.next());
        }
    }

    /**
     * Adds a price adjustment line item and its sale and return components to
     * the line item list.
     *
     * @param priceAdjLineItem PriceAdjustmentLineItemIfc reference to add
     */
    public void addPriceAdjustmentLineItem(PriceAdjustmentLineItemIfc priceAdjLineItem)
    {
        addLineItem(priceAdjLineItem);
        addLineItem(priceAdjLineItem.getPriceAdjustReturnItem());
        addLineItem(priceAdjLineItem.getPriceAdjustSaleItem());
    }

    /**
     * Creates an instance of a SaleReturnLineItemIfc based on the type of the
     * PLUItemIfc parameter. Same as calling
     * {@link #createSaleReturnLineItemInstance(PLUItemIfc, ExternalOrderItemIfc)}
     * with null.
     *
     * @param pItem PLU item
     * @return SaleReturnLineItemIfc
     */
    public SaleReturnLineItemIfc createSaleReturnLineItemInstance(PLUItemIfc pItem)
    {
        return createSaleReturnLineItemInstance(pItem, null);
    }

    /**
     * Creates an instance of a SaleReturnLineItemIfc based on the type of the
     * PLUItemIfc parameter. If the pluItem already exists in the transaction,
     * the pre-existing one will be used instead. 
     *
     * @param pItem PLU item
     * @param pExternalOrderItem external order item
     * @return SaleReturnLineItemIfc
     */
    public SaleReturnLineItemIfc createSaleReturnLineItemInstance(PLUItemIfc pItem,
            ExternalOrderItemIfc pExternalOrderItem)
    {
        pItem = determinePLUItemToUse(pItem);

        // create and initialze the line item object
        SaleReturnLineItemIfc srli;
        if (pItem.isKitHeader())
        {
            srli = DomainGateway.getFactory().getKitHeaderLineItemInstance();
            srli.initialize(
                    pItem,
                    BigDecimal.ONE,
                    initializeItemTax(transactionTax.getDefaultRate(), transactionTax.getDefaultTaxRules(),
                            pItem.getTaxable()), getSalesAssociate(), getDefaultRegistry(), null, pExternalOrderItem);
        }
        else if (pItem.isKitComponent())
        {
            srli = DomainGateway.getFactory().getKitComponentLineItemInstance();
            srli.initialize(
                    pItem,
                    ((KitComponent)pItem).getQuantity(),
                    initializeItemTax(transactionTax.getDefaultRate(), transactionTax.getDefaultTaxRules(),
                            pItem.getTaxable()), getSalesAssociate(), getDefaultRegistry(), null, pExternalOrderItem);
        }
        else
        {
            srli = DomainGateway.getFactory().getSaleReturnLineItemInstance();
            // CR 27192
            srli.initialize(
                    pItem,
                    BigDecimal.ONE,
                    initializeItemTax(transactionTax.getDefaultRate(), transactionTax.getDefaultTaxRules(),
                            pItem.getTaxable()), getSalesAssociate(), getDefaultRegistry(), null, pExternalOrderItem);
        }
        return srli;
    }

    /**
     * Analyse the {@link PLUItemIfc}. It it already exists in the transaction,
     * then use the pre-existing one. If none are found, the PLUItems are
     * coalesced together to share the same {@link DiscountRuleIfc} objects.
     *
     * @param pItem
     * @return
     * @since 14.1
     */
    protected PLUItemIfc determinePLUItemToUse(PLUItemIfc pItem)
    {
        PLUItemIfc existingPLUItem = null;
        boolean swapMade = false;
        for (AbstractTransactionLineItemIfc existingLineItem : lineItemsVector)
        {
            if (existingLineItem instanceof SaleReturnLineItemIfc)
            {
                existingPLUItem = ((SaleReturnLineItemIfc)existingLineItem).getPLUItem();
                if (existingPLUItem.equals(pItem))
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Using already existing PLUItem from line item " + existingLineItem.getLineNumber());
                    }
                    pItem = existingPLUItem;
                    swapMade = true;
                    break;
                }
            }
        }

        // if we have a brand new PLUItem, try to merge its same contents
        if (!swapMade)
        {
            for (AbstractTransactionLineItemIfc existingLineItem : lineItemsVector)
            {
                if (existingLineItem instanceof SaleReturnLineItemIfc)
                {
                    existingPLUItem = ((SaleReturnLineItemIfc)existingLineItem).getPLUItem();
                    pItem.consolidate(existingPLUItem);
                }
            }            
        }
        return pItem;
    }

    /**
     * Splits any multi-quantity line items that are held by this transaction
     * into single unit line items if they could be used to satisfy an advanced
     * pricing rule's criteria .
     *
     * @deprecated since 14.0. No replacement.
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public void splitSourcesAndTargets()
    {
        SaleReturnLineItemIfc srli = null;
        // test each item for discount potential and split if quantity is > 1
        for (Iterator i = ((Vector)lineItemsVector.clone()).iterator(); i.hasNext();)
        {
            srli = (SaleReturnLineItemIfc)i.next();

            if ((isPotentialSource((DiscountSourceIfc)srli) || isPotentialTarget((DiscountTargetIfc)srli))
                    && ((srli.getItemQuantityDecimal().intValue() > 1 || srli.getItemQuantityDecimal().intValue() < -1) && (!srli
                            .isUnitOfMeasureItem())))
            {
                splitLineItem(srli);
            }
        }
    }

    /**
     * Splits any multi-quantity line items that are held by this transaction
     * into single unit line items if they could be used to satisfy an advanced
     * pricing rule's or transaction discount rule's criteria .
     *
     * @param forTransactionLevelRules
     */
    @SuppressWarnings("rawtypes")
    public void splitSourcesAndTargets(boolean forTransactionLevelRules)
    {
        SaleReturnLineItemIfc srli = null;
        // test each item for discount potential and split if quantity is > 1
        for (Iterator i = ((Vector)lineItemsVector.clone()).iterator(); i.hasNext();)
        {
            srli = (SaleReturnLineItemIfc)i.next();
            if (!forTransactionLevelRules)
            {
                if ((isPotentialSource((DiscountSourceIfc)srli) || isPotentialTarget((DiscountTargetIfc)srli))
                        && ((srli.getItemQuantityDecimal().intValue() > 1 || srli.getItemQuantityDecimal().intValue() < -1) && (!srli
                                .isUnitOfMeasureItem())))
                {
                    splitLineItem(srli);
                }
            }
            else
            {
                if (isPotentialSourceForTransactionRule((DiscountSourceIfc)srli)
                        && ((srli.getItemQuantityDecimal().intValue() > 1 || srli.getItemQuantityDecimal().intValue() < -1) && (!srli
                                .isUnitOfMeasureItem())))
                {
                    splitLineItem(srli);
                }
            }
        }
    }

    /**
     * Returns a boolean indicating whether the line item is a source for any
     * transaction discounts rule held by this transaction.
     *
     * @return boolean true if source can be used, false otherwise
     */
    public boolean isPotentialSourceForTransactionRule(DiscountSourceIfc source)
    {
        AdvancedPricingRuleIfc rule = null;
        boolean isSource = false;

        // for each advanced pricing rule, check if the line item is
        // a potential source
        if (source.isSourceAvailable())
        {
            for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
            {
                rule = i.next().getRule();
                if (rule != null)
                {
                    isSource = rule.isPotentialSource(source);
                }
                if (isSource)
                {
                    break;
                }
            }
        }
        return isSource;
    }

    /**
     * Splits a line item in multiple items for best deal calculations.
     *
     * @param srli SaleReturnLineItem
     */
    public void splitLineItem(SaleReturnLineItemIfc srli)
    {
        int position=0;
        // remove the item from the line items vector
        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            if (i.next() == srli)
            {
                i.remove();
                break;
            }
            position++;
        }

        BigDecimal qty = srli.getItemQuantityDecimal();
        BigDecimal newQty = BigDecimalConstants.ONE_AMOUNT;
        if (qty.signum() == -1)
        {
            newQty = newQty.negate();
        }
        // create clones of the line item, set their quantity to one
        // and add them to the vector
        for (int i = 0; i < qty.abs().intValue(); i++)
        {
            SaleReturnLineItemIfc newSrli = (SaleReturnLineItem)srli.clone();
            if (srli.getItemQuantityDecimal().intValue() > 0)
            {
                newSrli.modifyItemQuantity(BigDecimalConstants.ONE_AMOUNT);
            }

            else
            {
                newSrli.modifyItemQuantity(BigDecimalConstants.NEGATIVE_ONE);
            }
            newSrli.calculateLineItemPrice();

            ((DiscountSourceIfc)newSrli).setSourceAvailable(true);
            lineItemsVector.insertElementAt(newSrli, position++);

            newSrli.setSelectedForItemSplit(true);

        } // end for (int i = 0; i < (qty-1); i++)

        resetLineItemNumbers();

    }

    /**
     * Returns the best deal winners.
     */
    public ArrayList<BestDealGroupIfc> getBestDealWinners()
    {
        return bestDealWinners;
    }

    /**
     * Returns a boolean indicating whether a specific rule is included in the
     * collection of winning best deal groups.
     *
     * @param rule Specific rule to test
     * @return Specific rule is included
     */
    public boolean isBestDealWinner(AdvancedPricingRuleIfc rule)
    {
        boolean winner = false;

        // test the winners collection for a group with matching rule ID
        for (BestDealGroupIfc group : bestDealWinners)
        {
            if (rule.getRuleID().equals(group.getRuleID()))
            {
                winner = true;
                break;
            }
        } // end for (Iterator i = bestDealWinner.iterator()...)

        return winner;
    }

    /**
     * Calculates and applies the best deal discounts for advanced pricing
     * rules.
     */
    @SuppressWarnings("unchecked")
    public void calculateBestDeal()
    {
        // remove any previously applied advanced pricing discounts
        // this call enables all sources and clears the existing best deal
        // winners
        clearBestDealDiscounts();

        // recheck Transaction Discounts in the transaction Discount vector
        checkTransactionDiscountVector();

        if(discountLimits == null)
        {
            discountLimits =  DomainGateway.getFactory().getDiscountListInstance();
        }

        if (this.hasAdvancedPricingRules())
        {
            BestDealGroupIfc group = null;
            ArrayList<DiscountSourceIfc> availableSources = null;
            ArrayList<DiscountTargetIfc> availableTargets = null;
            boolean dealApplied = true;

            // reset the limits list using current pricing rules
            initializeDiscountLimits(discountLimits);

            // test for and split items with quantity > 1
            splitSourcesAndTargets(false);

            while (dealApplied)
            {
                // initialize the flag to false for each iteration, will be set
                // to true if a
                // best deal is found and applied
                dealApplied = false;

                // clear any groups left over from a previous iteration
                clearBestDealGroups();

                // get just the sale source and target items
                availableSources = getDiscountSources();
                availableTargets = getDiscountTargets();

                // compare the sale sources and targets against the available rules
                factorAgainstRules(availableSources, availableTargets);

                // get the remaining non-receipt return source and target items
                ArrayList<DiscountSourceIfc> returnSources = getDiscountSources(true);
                ArrayList<DiscountTargetIfc> returnTargets = getDiscountTargets(true);

                // compare the return sources and targets against the available rules
                factorAgainstRules(returnSources, returnTargets);

                // check to make sure a non-transaction level group isn't
                // beaten by a transaction wide group - this is necessary due
                // to the use of checkSources and sourcesAreTargets flags
                // in order to give transaction level deals all discount
                // eligible items
                // in a transaction
                filterTransactionBestDealGroups();

                // sort the bestDealGroups list in descending order by total
                // discount
                Collections.sort(bestDealGroups, Comparators.bestDealDiscountDescending);

                // add the returns back to the sales before combinging best deals
                availableSources.addAll(returnSources);
                availableTargets.addAll(returnTargets);

                // generate combinations and add them to list of bestDealGroups
                combineBestDealGroups(availableSources, availableTargets);

                // apply the bestDealGroup with greatest total discount
                if (!bestDealGroups.isEmpty())
                {
                    group = bestDealGroups.get(0);
                    group.applyAdvancedPricingDiscounts();
                    bestDealWinners.add(group);
                    updateDiscountLimits(group, discountLimits);
                    dealApplied = true;
                    if (logger.isDebugEnabled())
                    {
                        StringBuffer logString = new StringBuffer();
                        logString.append("Winning rule is ").append(group.getRuleID());
                        logString.append(" with sources ");
                        for (Iterator<DiscountSourceIfc> i = group.getSources().iterator(); i.hasNext();)
                        {
                            logString.append(((SaleReturnLineItemIfc)i.next()).getPLUItem().getPosItemID());
                            logString.append(":");
                        }
                        logString.append(" and targets ");
                        for (Iterator<DiscountTargetIfc> i = group.getTargets().iterator(); i.hasNext();)
                        {
                            logString.append(((SaleReturnLineItemIfc)i.next()).getPLUItem().getPosItemID());
                            logString.append(":");
                        }
                        logger.debug(logString.toString());
                    }
                }

            } // end while (dealApplied)
        } // end if (this.hasAdvancedPricingRules())
    }

    /**
     * The available sources and targets are checked against every rule
     * available to this proxy. All combinations of qualifications are
     * determined, i.e factored.
     * <p>
     * Sales and returns should be factored separately.
     *
     * @param availableSources
     * @param availableTargets
     */
    @SuppressWarnings("unchecked")
    protected void factorAgainstRules(ArrayList<DiscountSourceIfc> availableSources,
            ArrayList<DiscountTargetIfc> availableTargets)
    {
        AdvancedPricingRuleIfc rule = null;

        // for each advanced pricing rule, create BestDealGroups
        // using the sources and targets currently available to the
        // transaction
        for (Iterator<DiscountRuleIfc> i = advancedPricingRules(); i.hasNext();)
        {
            rule = (AdvancedPricingRuleIfc)i.next();

            if (rule.isIncludedInBestDeal())
            {
                String ruleID = rule.getRuleID();
                rule.getSourceList().setDescription(rule.getDescription());
                rule.getTargetList().setDescription(rule.getDescription());

                if (!discountLimits.containsEntry(ruleID))
                {
                    addBestDealGroups(rule.generateBestDealGroups((ArrayList<DiscountSourceIfc>)availableSources.clone(),
                            (ArrayList<DiscountTargetIfc>)availableTargets.clone()));
                }
                else
                {
                    int availableApplications = discountLimits.getQuantityRequired(ruleID)
                            - discountLimits.getQuantity(ruleID);

                    addBestDealGroups(rule.generateBestDealGroups((ArrayList<DiscountSourceIfc>)availableSources.clone(),
                            (ArrayList<DiscountTargetIfc>)availableTargets.clone(), availableApplications));
                }
            } // end if (rule.isIncludedInBestDeal())
        } // end for
    }

    /**
     * Clears and initializes a limits list with entries for rules that are
     * limited in the number of times they can be applied.
     *
     * @param limits DiscountListIfc - the list to initialize
     */
    public void initializeDiscountLimits(DiscountListIfc limits)
    {
        limits.clear();

        AdvancedPricingRuleIfc rule = null;

        // for each advanced pricing rule,
        // check to see if there is a limit on the
        // number of times it can be applied,
        // if there is, create an entry in the limits list
        for (Iterator<DiscountRuleIfc> i = advancedPricingRules(); i.hasNext();)
        {
            rule = (AdvancedPricingRuleIfc)i.next();

            if (rule.isApplicationLimited())
            {
                limits.addEntry(rule.getRuleID(), rule.getApplicationLimit());
            }
        }
    }

    /**
     * Updates the limits list when a best deal group is applied.
     *
     * @param group - BestDealGroupIfc to use for updating the limits
     * @param limits - DiscountListIfc containing limit data to update
     */
    public void updateDiscountLimits(BestDealGroupIfc group, DiscountListIfc limits)
    {
        if (group instanceof SuperGroupIfc)
        {
            for (Iterator<BestDealGroupIfc> i = ((SuperGroupIfc)group).subgroups(); i.hasNext();)
            {
                BestDealGroupIfc subgroup = i.next();
                if (subgroup instanceof SuperGroupIfc)
                {
                    updateDiscountLimits(subgroup, limits);
                }
                else
                {
                    limits.incrementQuantity(subgroup.getRuleID(), (DiscountItemIfc)lineItemsVector.get(0));
                }
            }
        }
        else
        {
            limits.incrementQuantity(group.getRuleID(), (DiscountItemIfc)lineItemsVector.get(0));
        }
    }

    /**
     * For each of the best deal groups generated in each iteration of
     * calculateBestDeal(), this method generates combinations of best deal
     * groups that might be used to calculate the greatest total discount on the
     * transaction. Groups that do not compete for sources/targets are
     * generated, aggregated and sorted in order to determine their potential
     * discount and discover the combination that should be applied first.
     *
     * @param sources - an ArrayList containing potential sources for a group
     * @param targets - an ArrayList containing potential targets for a group
     */
    @SuppressWarnings("unchecked")
    public void combineBestDealGroups(ArrayList<DiscountSourceIfc> sources, ArrayList<DiscountTargetIfc> targets)
    {
        // a list to hold the aggregations of non-competing groups
        ArrayList<Object> list = new ArrayList<Object>();
        // a list to hold the non-competing groups
        ArrayList<BestDealGroupIfc> nonCompeting = new ArrayList<BestDealGroupIfc>();

        // for each of the groups generated by an iteration of calculate best
        // deal
        for (Iterator<BestDealGroupIfc> s = bestDealGroups.iterator(); s.hasNext();)
        {
            // get the group and create a limits list for it
            BestDealGroupIfc gruppe0 = s.next();
            DiscountListIfc limitsList = DomainGateway.getFactory().getDiscountListInstance();

            // make a copy of the possible sources and targets
            ArrayList<DiscountSourceIfc> srcs = (ArrayList<DiscountSourceIfc>)sources.clone();
            ArrayList<DiscountTargetIfc> tgts = (ArrayList<DiscountTargetIfc>)targets.clone();

            // initialize the limits and update them with the data from the
            // group
            initializeDiscountLimits(limitsList);
            updateDiscountLimits(gruppe0, limitsList);

            // remove this groups sources and targets from the pool
            gruppe0.removeSourcesAndTargets(srcs, tgts);

            // if any sources remain
            if (!srcs.isEmpty())
            {
                // add the group to the list of non-competitors
                nonCompeting.add(gruppe0);

                // create a temporary group
                BestDealGroupIfc gruppe1 = null;

                // while additional groups can be generated
                // generate and save the next highest group
                do
                {
                    gruppe1 = getNextBestDealGroup(gruppe0, srcs, tgts, limitsList);
                    if (gruppe1 != null)
                    {
                        gruppe1.removeSourcesAndTargets(srcs, tgts);
                        updateDiscountLimits(gruppe1, limitsList);
                        nonCompeting.add(gruppe1);
                    }
                } while (gruppe1 != null);

                // if any additional groups were created,
                // add them to the list of aggregates
                if (nonCompeting.size() > 1)
                {
                    list.add(nonCompeting.clone());
                }

                // reset nonCompeting for next iteration
                nonCompeting.clear();

            } // end if (!srcs.isEmpty())

        } // end for (Iterator s = bestDealGroups.iterator(); s.hasNext();)

        // for all the aggregations in the list,
        // create SuperGroups and add them to the bucket of potential winners
        for (Iterator<Object> y = list.iterator(); y.hasNext();)
        {
            SuperGroupIfc g = DomainGateway.getFactory().getSuperGroupInstance();
            g.setSubgroups((ArrayList<BestDealGroupIfc>)y.next());
            addBestDealGroup(g);
        } // end for (Iterator y = list.iterator(); y.hasNext(); )

        // sort potential winners
        Collections.sort(bestDealGroups, Comparators.bestDealDiscountDescending);
    }

    /**
     * Checks to make sure a non-transaction level group isn't beaten by a
     * transaction wide group - this is necessary due to the use of checkSources
     * and sourcesAreTargets flags which give transaction-level BestDealGroups
     * all the eligible items in a transaction. This method compares each
     * non-transaction group's discount with the discount given by the
     * transaction rule for the source/target items in that group. If the
     * non-transactional discount is greater, the transaction level group is
     * removed and the transaction discount is regenerated from the pool of
     * items left after the higher discounts are applied.
     */
    @SuppressWarnings("unchecked")
    public void filterTransactionBestDealGroups()
    {
        ArrayList<BestDealGroupIfc> transactionGroups = new ArrayList<BestDealGroupIfc>();
        ArrayList<BestDealGroupIfc> nonTransactionGroups = new ArrayList<BestDealGroupIfc>();
        BestDealGroupIfc grupo = null;
        AdvancedPricingRuleIfc rule = null;

        // sort the groups by scope
        for (Iterator<BestDealGroupIfc> z = bestDealGroups.iterator(); z.hasNext();)
        {
            grupo = z.next();
            rule = grupo.getDiscountRule();

            if (rule != null)
            {
                if (rule.isScopeTransaction())
                {
                    transactionGroups.add(grupo);
                }
                else
                {
                    nonTransactionGroups.add(grupo);
                } // end if (rule.isScopeTransaction())

            } // end if (rule != null)

        } // end for (Iterator z = ...)

        // temporary storage for comparing and sorting groups
        AdvancedPricingRuleIfc tranRule = null;
        BestDealGroupIfc tranGroup = null;
        BestDealGroupIfc nonTranGroup = null;
        BestDealGroupIfc tempGroup = null;
        ArrayList<BestDealGroupIfc>       betterDeals = new ArrayList<BestDealGroupIfc>(10);
        ArrayList<AdvancedPricingRuleIfc> losers = new ArrayList<AdvancedPricingRuleIfc>(10);
        ArrayList<DiscountSourceIfc>      sources = null;
        ArrayList<DiscountTargetIfc>      targets = null;
        ArrayList<BestDealGroupIfc>       temp = null;

        // for each non-transaction level group,
        // check to see if it would provide a better discount than the
        // equivalent transaction level group
        for (Iterator<BestDealGroupIfc> a = nonTransactionGroups.iterator(); a.hasNext();)
        {
            nonTranGroup = a.next();

            for (Iterator<BestDealGroupIfc> b = transactionGroups.iterator(); b.hasNext();)
            {
                tranGroup = b.next();
                tranRule = tranGroup.getDiscountRule();

                sources = (ArrayList<DiscountSourceIfc>)nonTranGroup.getSources().clone();
                targets = (ArrayList<DiscountTargetIfc>)nonTranGroup.getTargets().clone();

                // clone the transaction level rule and use it to generate a
                // group for comparison
                // the group is generated using the sources/targets from the non
                // transaction level
                // group being compared
                temp = ((AdvancedPricingRuleIfc)tranRule.clone()).generateBestDealGroups(sources, targets);

                // if a group was generated (should always be the case since
                // sources are not
                // checked for transaction rules), get it and compare the total
                // discounts
                if (!temp.isEmpty())
                {
                    tempGroup = (BestDealGroupIfc)temp.get(0);
                }

                if (tempGroup != null)
                {
                    if (nonTranGroup.getTotalDiscount().compareTo(tempGroup.getTotalDiscount()) > 0)
                    {
                        // nonTransactionLevel group gave a higher discount than
                        // transaction level group
                        betterDeals.add(nonTranGroup);
                        losers.add(tranRule);

                        // remove the original transaction level group from the
                        // initial set
                        for (Iterator<BestDealGroupIfc> u = bestDealGroups.iterator(); u.hasNext();)
                        {
                            if (tranGroup == u.next())
                            {
                                u.remove();
                                break;
                            }
                        } // end for (Iterator u = bestDealGroups.iterator()

                    } // end if (nonTranGroup.getTotalDiscount().comapareTo()
                }
            } // end for (Iterator b = transactionGroups.iterator()

        } // end for (Iterator a = nonTransactionGroups.iterator()

        // get lists of all items on the transaction
        sources = getDiscountSources();
        targets = getDiscountTargets();

        // remove all winning group's sources/targets from the lists
        for (Iterator<BestDealGroupIfc> f = betterDeals.iterator(); f.hasNext();)
        {
            grupo = f.next();
            grupo.removeSourcesAndTargets(sources, targets);
        }

        // use the losing rules to generate discounts on remaining items
        AdvancedPricingRuleIfc aLoser = null;

        for (Iterator<AdvancedPricingRuleIfc> p = losers.iterator(); p.hasNext();)
        {
            aLoser = p.next();

            // generate a new group using leftover source/target items and
            // add it to the set of potential winners
            addBestDealGroups(aLoser.generateBestDealGroups((ArrayList<DiscountSourceIfc>)sources.clone(), (ArrayList<DiscountTargetIfc>)targets.clone()));
        }
    }

    /**
     * Determine the next highest best deal group given a pre-existing group.
     *
     * @param gruppe - Given pre-existing group to test
     * @param srcs - an ArrayList containing potential sources for a group
     * @param tgts - an ArrayList containing potential targets for a group
     * @param limits - Discount list
     * @return Next highest Best Deal Group
     */
    @SuppressWarnings("unchecked")
    public BestDealGroupIfc getNextBestDealGroup(BestDealGroupIfc gruppe, ArrayList<DiscountSourceIfc> srcs, ArrayList<DiscountTargetIfc> tgts,
            DiscountListIfc limits)
    {
        // create a list of the advanced pricing rules on the transaction
        ArrayList<DiscountRuleIfc> rules = new ArrayList<DiscountRuleIfc>(advancedPricingRules.values());

        // remove the rule that generated gruppe from the list
        for (Iterator<DiscountRuleIfc> i = rules.iterator(); i.hasNext();)
        {
            if (i.next() == gruppe.getDiscountRule())
            {
                i.remove();
                break;
            }
        } // end for (Iterator i = rules.iterator(); i.hasNext(); )

        // create temporary storage variables
        ArrayList<BestDealGroupIfc> sortGroups = new ArrayList<BestDealGroupIfc>(25);
        AdvancedPricingRuleIfc rule = null;
        ArrayList<DiscountSourceIfc> srcs2 = null;
        ArrayList<DiscountTargetIfc> tgts2 = null;
        BestDealGroupIfc nextBest = null;

        // for each remaining rule, generate additional bestDealGroups
        for (Iterator<DiscountRuleIfc> i = rules.iterator(); i.hasNext();)
        {
            rule = (AdvancedPricingRuleIfc)i.next();

            if (rule.isIncludedInBestDeal())
            {
                String ruleID = rule.getRuleID();
                srcs2 = (ArrayList<DiscountSourceIfc>)srcs.clone();
                tgts2 = (ArrayList<DiscountTargetIfc>)tgts.clone();

                if (!limits.containsEntry(ruleID))
                {
                    sortGroups.addAll(rule.generateBestDealGroups(srcs2, tgts2));
                }
                else
                {
                    int availableApplications = limits.getQuantityRequired(ruleID) - limits.getQuantity(ruleID);

                    sortGroups.addAll(rule.generateBestDealGroups(srcs2, tgts2, availableApplications));

                } // end if (!limits.containsEntry(ruleID))

            } // end if (rule.isIncludedInBestDeal())

        } // end for (Iterator i = rules.iterator();i.hasNext();)

        // sort all the groups that were generated w/o gruppe sources/targets
        if (!sortGroups.isEmpty())
        {
            Collections.sort(sortGroups, Comparators.bestDealDiscountDescending);

            nextBest = (BestDealGroupIfc)sortGroups.get(0);
            sortGroups.clear();
        }

        // return the group with the next highest discount
        return nextBest;
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
        CurrencyIfc total = ZERO;// DomainGateway.getBaseCurrencyInstance();
        CurrencyIfc amount = null;
        ItemDiscountStrategyIfc discount = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            discount = ((SaleReturnLineItemIfc)i.next()).getAdvancedPricingDiscount();

            if (discount != null)
            {
                amount = discount.getDiscountAmount();
                if (amount != null)
                {
                    total = total.add(amount);
                }
            }
        }

        return total;
    }

    /**
     * Calculates and returns the sum of any store coupon discount applied to
     * this transaction.
     *
     * @param discountScope value indicating whether to calculate using item or
     *            transaction scope store coupons. See
     *            oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     * @return CurrencyIfc total
     */
    public CurrencyIfc getStoreCouponDiscountTotal(int discountScope)
    {
        CurrencyIfc total = ZERO;// DomainGateway.getBaseCurrencyInstance();
        SaleReturnLineItemIfc item = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            item = (SaleReturnLineItemIfc)i.next();
            total = total.add(item.getDiscountAmount(discountScope, DiscountRuleConstantsIfc.ASSIGNMENT_STORE_COUPON));
        }

        return total;
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
        SaleReturnLineItemIfc srli = null;
        srli = DomainGateway.getFactory().getSaleReturnLineItemInstance();
        srli.initialize(pItem, BigDecimalConstants.ONE_AMOUNT,
                initializeReturnItemTax(rItem, transactionTax.getDefaultTaxRules(), pItem.getTaxable()),
                rItem.getSalesAssociate(), (RegistryIDIfc)null, rItem);
        completeAddReturnItem(srli, pItem, rItem, qty);

        return (srli);
    }

    /**
     * Adds a return item to the transaction.
     *
     * @param pItem PLUItem object
     * @param rItem ReturnItem object
     * @param pExternalOrderItem ExternalOrderItem object
     * @return transaction line item
     */
    public SaleReturnLineItemIfc addReturnItem(PLUItemIfc pItem, ReturnItemIfc rItem,
            ExternalOrderItemIfc pExternalOrderItem)
    {
        SaleReturnLineItemIfc srli = null;
        srli = DomainGateway.getFactory().getSaleReturnLineItemInstance();
        srli.initialize(pItem, initializeReturnItemTax(rItem, transactionTax.getDefaultTaxRules(), pItem.getTaxable()),
                rItem.getSalesAssociate(), (RegistryIDIfc)null, rItem, pExternalOrderItem);

        BigDecimal quantity = pExternalOrderItem.getQuantity();
        if (!pExternalOrderItem.isSellItem())
        {
            quantity = quantity.negate();
        }
        completeAddReturnItem(srli, pItem, rItem, quantity);

        return (srli);
    }

    /**
     * Complete adding the return line item
     *
     * @param srli the return line item
     * @param pItem the PLU item
     * @param rItem the return item
     * @param qty the return quantity
     */
    protected void completeAddReturnItem(SaleReturnLineItemIfc srli, PLUItemIfc pItem, ReturnItemIfc rItem,
            BigDecimal qty)
    {
        srli.calculateLineItemPrice();
        if (rItem.getItemSize() != null)
        {
            srli.setItemSizeCode(rItem.getItemSize().getSizeCode());
        }
        // if change in quantity
        if (qty.compareTo(BigDecimalConstants.ONE_AMOUNT) != 0)
        {
            srli.modifyItemQuantity(qty);
        }
        // add to vector
        addLineItem(srli);

        if (!srli.hasExternalPricing())
        {
            addItemAdvancedPricingRules(pItem);
            calculateBestDeal();
        }
    }

    /**
     * Resets the line item numbers
     */
    @SuppressWarnings("unchecked")
    public void resetLineItemNumbers()
    {
        // clear all information collected during reset
        Hashtable<Integer, Integer> lineNumberLookupTbl = new Hashtable<Integer, Integer>(2);
        Vector<Object> lineNumberResettedLineItems = new Vector<Object>(2);
        Vector<Object> sequenceNumberResettedLineItems = new Vector<Object>(2);

        // sort the items so kit components come last
        Collections.sort(lineItemsVector, Comparators.componentLineItemsLast);

        // reset all existing line item numbers
        for (int i = 0; i < lineItemsVector.size(); i++)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItemsVector.elementAt(i);

            // set up hash table to convert old line number to new line number
            Integer lineNumber = Integer.valueOf(lineItem.getLineNumber());
            if (!lineNumberLookupTbl.containsKey(lineNumber))
            {
                lineNumberLookupTbl.put(lineNumber, Integer.valueOf(i));
            }

            // set new line number
            lineItem.setLineNumber(i);
            if (lineItem.isPartOfPriceAdjustment())
            {
                int refID = lineItem.getPriceAdjustmentReference();
                PriceAdjustmentLineItemIfc priceAdjLineItem = retrievePriceAdjustmentByReference(refID);
                if (priceAdjLineItem != null)
                {
                    if (lineItem.getItemQuantityDecimal().compareTo(BigDecimal.ZERO) < 0)
                    {
                        priceAdjLineItem.getPriceAdjustReturnItem().setLineNumber(i);
                    }
                    else
                    {
                        priceAdjLineItem.getPriceAdjustSaleItem().setLineNumber(i);
                    }
                }
            }
            lineNumberResettedLineItems.add(lineItem);

            // update the related item references
            SaleReturnLineItemIfc[] relatedItems = lineItem.getRelatedItemLineItems();
            if (relatedItems != null)
            {
                for (int j = 0; j < relatedItems.length; j++)
                {
                    relatedItems[j].setRelatedItemSequenceNumber(i);
                    sequenceNumberResettedLineItems.add(relatedItems[j]);
                }
                lineItem.setRelatedItemLineItems(relatedItems);
            }
        }

        // reset all existing related line item numbers
        for (int i = 0; i < lineItemsVector.size(); i++)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItemsVector.elementAt(i);

            // If the related item sequence number is not yet resetted, do it
            // here
            if (!containsObjectRef(sequenceNumberResettedLineItems, lineItem))
            {
                int relatedItemSequenceNumber = lineItem.getRelatedItemSequenceNumber();
                if (relatedItemSequenceNumber >= 0)
                {
                    Integer newRelatedItemSequenceNumberInteger = lineNumberLookupTbl.get(Integer
                            .valueOf(relatedItemSequenceNumber));
                    int newRelatedItemSequenceNumber = newRelatedItemSequenceNumberInteger != null ? newRelatedItemSequenceNumberInteger
                            .intValue() : -1;
                    lineItem.setRelatedItemSequenceNumber(newRelatedItemSequenceNumber);

                }
            }

            // If the line number is not yet resetted, do it here
            SaleReturnLineItemIfc[] relatedItems = lineItem.getRelatedItemLineItems();
            Vector<SaleReturnLineItemIfc> newRelatedItemsVector = new Vector<SaleReturnLineItemIfc>(2);
            if (relatedItems != null)
            {
                newRelatedItemsVector.addAll(Arrays.asList(relatedItems));
                for (int j = relatedItems.length - 1; j >= 0; j--)
                {
                    if (!containsObjectRef(lineNumberResettedLineItems, relatedItems[j]))
                    {
                        int lineNumber = relatedItems[j].getLineNumber();
                        Integer newLineNumber = lineNumberLookupTbl.get(Integer.valueOf(lineNumber));
                        if (newLineNumber != null)
                        {
                            relatedItems[j].setLineNumber(newLineNumber.intValue());
                        }
                        else
                        {
                            newRelatedItemsVector.remove(j);
                        }
                    }
                }
                lineItem.setRelatedItemLineItems(newRelatedItemsVector.toArray(new SaleReturnLineItemIfc[0]));
            }
        }
    }

    /**
     * Remove a line from the transaction.
     *
     * @param index
     */
    public void removeLineItem(int index)
    {
        // first save count and amounts of line items deleted to be used by
        // reports
        incrementLineVoid(lineItemsVector.get(index));

        SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItemsVector.elementAt(index);

        // Find the PriceAdjustmentLineItemIfc instance for the component so
        // that
        // all related references can be removed
        if (item.isPartOfPriceAdjustment())
        {
            int refID = item.getPriceAdjustmentReference();
            PriceAdjustmentLineItemIfc priceAdjLineItem = retrievePriceAdjustmentByReference(refID);

            if (priceAdjLineItem != null)
            {
                removeLineItem(priceAdjLineItem.getLineNumber());
                return;
            }
        }

        // Remove price adjustment line items and their components
        if (item instanceof PriceAdjustmentLineItemIfc)
        {
            PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc)item;
            // remove components and reset the line numbers
            lineItemsVector.removeElementAt(priceAdjLineItem.getPriceAdjustReturnItem().getLineNumber());
            resetLineItemNumbers();
            lineItemsVector.removeElementAt(priceAdjLineItem.getPriceAdjustSaleItem().getLineNumber());
            resetLineItemNumbers();
            lineItemsVector.removeElementAt(priceAdjLineItem.getLineNumber());
            resetLineItemNumbers();
            // recalculate the best deal and update transaction totals
            calculateBestDeal();
            return;
        }

        // if item is a kit header, have it remove its components first
        if (item.isKitHeader())
        {
            ((KitHeaderLineItemIfc)item).removeKitComponentLineItems(lineItemsVector.iterator());
        }

        // if item is a related item, make sure to remove primary items
        // relationship
        if (item.getRelatedItemSequenceNumber() >= 0)
        {
            SaleReturnLineItemIfc primaryItem = (SaleReturnLineItemIfc)lineItemsVector.get(item
                    .getRelatedItemSequenceNumber());
            SaleReturnLineItemIfc[] relatedItems = primaryItem.getRelatedItemLineItems();
            ArrayList<AbstractTransactionLineItemIfc> relatedItemsList = new ArrayList<AbstractTransactionLineItemIfc>();
            if (relatedItems != null)
            {
                relatedItemsList.addAll(Arrays.asList(relatedItems));
                for (int i = 0; i < relatedItems.length; i++)
                {
                    if (item.getLineNumber() == relatedItems[i].getLineNumber())
                    {
                        relatedItemsList.remove(i);
                        break;
                    }
                }
            }
            primaryItem.setRelatedItemLineItems(toItemArray(relatedItemsList));
        }
        // remove element and reset the line numbers
        lineItemsVector.removeElementAt(index);

        boolean isDuplicateAdvPricingRule = false;
        if (item.getPLUItem().advancedPricingRules() != null && item.getPLUItem().advancedPricingRules().hasNext())
        {
            String itemID = item.getPLUItem().getItemID();
            for (Iterator<AbstractTransactionLineItemIfc> iter = lineItemsVector.iterator(); iter.hasNext();)
            {
                SaleReturnLineItemIfc element = (SaleReturnLineItemIfc)iter.next();
                String elementItemID = element.getPLUItem().getItemID();
                if (itemID.equals(elementItemID))
                {
                    isDuplicateAdvPricingRule = true;
                }
            }
        }
        // skip Removing Advance pricing rule if there are any Duplicate Advance
        // pricing rule
        // present in the transaction
        if (!isDuplicateAdvPricingRule)
        {
            // attempt to remove any of the rules associated with the PLUItem
            // that might impact another item
            for (Iterator<AdvancedPricingRuleIfc> i = item.getPLUItem().advancedPricingRules(); i.hasNext();)
            {
                // addAdvancedPricingRule() only removes the rule
                // if it is already associated with the transaction
                removeAdvancedPricingRule((AdvancedPricingRuleIfc)i.next());
            }
        }

        resetLineItemNumbers();
        // recalculate the best deal and update transaction totals
        calculateBestDeal();

        // look through the list on more time for price adjustment line items.
        // remove it if it no longer is a better deal.
        for (Iterator<AbstractTransactionLineItemIfc> iter = lineItemsVector.iterator(); iter.hasNext();)
        {
            SaleReturnLineItemIfc element = (SaleReturnLineItemIfc)iter.next();
            CurrencyIfc zeroCurrency = ZERO;// DomainGateway.
            // getBaseCurrencyInstance();
            if (element.isPriceAdjustmentLineItem() && element.getSellingPrice().compareTo(zeroCurrency) >= 0)
            {
                removeLineItem(element.getLineNumber());
                return;
            }
        }

    }

    /**
     * Add the amount of line void to the transaction.
     *
     * @param lineItem an abstract line item
     */
    public void incrementLineVoid(AbstractTransactionLineItemIfc lineItem)
    {
        if (lineItem instanceof SaleReturnLineItemIfc)
        {
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItem;
            BigDecimal itemQuantity = item.getItemQuantityDecimal();
            // If quantity is negative(return item), then negate the quantity
            // before adding to line voids count.
            if (itemQuantity.signum() > 0)
            {
                addUnitsLineVoids(itemQuantity);
            }
            else
            {
                addUnitsLineVoids(itemQuantity.negate());
            }
            ItemPriceIfc ip = item.getItemPrice();
            addAmountLineVoids(ip.getItemTotal().subtract(ip.getItemTaxAmount()));
        }
    }

    /**
     * Remove multiple lines from the transaction.
     *
     * @param indices
     */
    public void removeLineItems(int[] indices)
    {
        if (indices != null)
        {
            // sort the array if indices
            Arrays.sort(indices);
            // remove elements from largest index to smallest
            for (int i = indices.length - 1; i >= 0 ; i--)
            {
                Object whatsit = lineItemsVector.get(i);
                if (whatsit instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)whatsit;
                    // before deleting line item, save count and amounts of line
                    // items deleted to be used by reports
                    incrementLineVoid(item);
                    lineItemsVector.removeElementAt(indices[i]);

                    // attempt to remove any of the rules associated with the
                    // PLUItem
                    // that might impact another item
                    for (Iterator<AdvancedPricingRuleIfc> iter = item.getPLUItem().advancedPricingRules(); iter.hasNext();)
                    {
                        // addAdvancedPricingRule() only removes the rule
                        // if it is already associated with the transaction
                        removeAdvancedPricingRule((AdvancedPricingRuleIfc)iter.next());
                    }
                }
            }
            resetLineItemNumbers();

            setUnitsLineVoids(getUnitsLineVoids().add(new BigDecimal(indices.length)));
            // recalculate the best deal and update transaction totals
            calculateBestDeal();
        }
    }

    /**
     * Retrieve clone of a line item from a transaction.
     *
     * @param index index into line item vector
     * @return line item object
     */
    public AbstractTransactionLineItemIfc retrieveItemByIndex(int index)
    {
        AbstractTransactionLineItemIfc li = lineItemsVector.elementAt(index);
        // copy line item for usage
        AbstractTransactionLineItemIfc useLineItem = (AbstractTransactionLineItemIfc)li.clone();
        return (useLineItem);
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
        PriceAdjustmentLineItemIfc priceAdjItem = null;

        for (int x = 0; x < lineItemsVector.size(); x++)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItemsVector.get(x);
            if (lineItem.isPriceAdjustmentLineItem() && priceAdjReference == lineItem.getPriceAdjustmentReference())
            {
                priceAdjItem = (PriceAdjustmentLineItemIfc)lineItem;
                break;
            }
        }

        return priceAdjItem;
    }

    /**
     * Retrieve of a line item from a transaction.
     *
     * @param lineItemNumber line item number
     * @return line item object
     */
    public AbstractTransactionLineItemIfc retrieveLineItemByID(int lineItemNumber)
    {
        AbstractTransactionLineItemIfc useLineItem = null;
        for (int i = 0; i < lineItemsVector.size(); i++)
        {
            AbstractTransactionLineItemIfc lineItem = lineItemsVector.elementAt(i);
            if (lineItem.getLineNumber() == lineItemNumber)
            {
                useLineItem = lineItem;
                break;
            }
        }
        return useLineItem;
    }

    /**
     * Replace a line item in the vector of lineItems.
     *
     * @param lineItem line item object
     * @param index index into line item vector
     */
    public void replaceLineItem(AbstractTransactionLineItemIfc lineItem, int index)
    {
        // save a reference to the old line item
        SaleReturnLineItemIfc oldItem = (SaleReturnLineItemIfc)lineItemsVector.elementAt(index);
        SaleReturnLineItemIfc newItem = (SaleReturnLineItemIfc)lineItem;

        // newItem.removeAdvancedPricingDiscount();

        SaleReturnLineItem newItemSource = (SaleReturnLineItem)lineItem;

        CurrencyIfc oldPrice = oldItem.getPLUItem().getPrice();
        CurrencyIfc newPrice = newItem.getExtendedDiscountedSellingPrice();
        // oldPrice is zero for an item selected from a receipt for a return, thus existing discounts
        // need to be preserved
        if (oldPrice.compareTo(ZERO) != 0 && !oldPrice.getDecimalValue().abs().equals(newPrice.getDecimalValue().abs()))
        {
            newItem.removeAdvancedPricingDiscount();
        }
        else
        {
            newItemSource.setSourceAvailable(true);
        }

        // replace element
        newItem.setLineNumber(index);
        lineItemsVector.setElementAt(newItem, index);

        // clear best deals after replacing the lineitem, else newItem isn't
        // cleared properly
        clearBestDealDiscounts();

        // remove kit component line items if necessary
        if (oldItem.isKitHeader())
        {
            ((KitHeaderLineItemIfc)oldItem).removeKitComponentLineItems(lineItemsVector.iterator());
        }

        // if new item is kit header, add its components
        if (newItem.isKitHeader())
        {
            Iterator<?> i = ((KitHeaderLineItemIfc)newItem).getKitComponentLineItems();
            while (i.hasNext())
            {
                lineItemsVector.add((KitComponentLineItemIfc)i.next());
            }
        }

        // Remove all related components of the old price adjustment
        if (oldItem.isPriceAdjustmentLineItem())
        {
            PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc)oldItem;
            lineItemsVector.remove(priceAdjLineItem.getPriceAdjustReturnItem());
            lineItemsVector.remove(priceAdjLineItem.getPriceAdjustSaleItem());
        }

        // Add all related components of the new price adjustment
        if (newItem.isPriceAdjustmentLineItem())
        {
            PriceAdjustmentLineItemIfc priceAdjLineItem = (PriceAdjustmentLineItemIfc)newItem;

            SaleReturnLineItemIfc returnItem = priceAdjLineItem.getPriceAdjustReturnItem();
            lineItemsVector.add(returnItem);

            SaleReturnLineItemIfc saleItem = priceAdjLineItem.getPriceAdjustSaleItem();
            lineItemsVector.add(saleItem);
        }

        resetLineItemNumbers();
        for (Iterator<AdvancedPricingRuleIfc> i = newItem.getPLUItem().advancedPricingRules(); i.hasNext();)
        {
            AdvancedPricingRuleIfc rule = i.next();
            // if the rule has pricIngGroupId than it must be calculated only
            // when customer is linked since it is related to pricingGroupID
            if (rule.getPricingGroupID() == -1 || rule.getPricingGroupID() == 0)
            {
                if (rule.isScopeTransaction())
                {
                    checkAddTransactionDiscount(rule);
                }
            }
        }
        // recalculate the best deal
        calculateBestDeal();
    }

    /**
     * Returns a boolean indicating whether the line item is a source for any
     * advanced pricing rule held by this transaction.
     *
     * @return boolean true if source can be used, false otherwise
     */
    public boolean isPotentialSource(DiscountSourceIfc source)
    {
        AdvancedPricingRuleIfc rule = null;
        boolean isSource = false;

        // for each advanced pricing rule, check if the line item is
        // a potential source
        if (source.isSourceAvailable())
        {
            for (Iterator<DiscountRuleIfc> i = advancedPricingRules(); i.hasNext();)
            {
                rule = (AdvancedPricingRuleIfc)i.next();
                isSource = rule.isPotentialSource(source);
                if (isSource)
                {
                    break;
                }
            }
        }
        return isSource;
    }

    /**
     * Returns a boolean indicating whether the line item is a target for any
     * advanced pricing rule held by this transaction.
     *
     * @return boolean true if target can be used, false otherwise
     */
    public boolean isPotentialTarget(DiscountTargetIfc target)
    {
        AdvancedPricingRuleIfc rule = null;
        boolean isTarget = false;

        // for each advanced pricing rule, check if the line item is
        // a potential source
        if (target.isTargetEnabled())
        {
            for (Iterator<DiscountRuleIfc> i = advancedPricingRules(); i.hasNext();)
            {
                rule = (AdvancedPricingRuleIfc)i.next();
                isTarget = rule.isPotentialTarget(target);
                if (isTarget)
                {
                    break;
                }
            }
        }
        return isTarget;
    }

    /**
     * Retrieves line items array in vector form.
     *
     * @return line item vector for this transaction
     */
    public Vector<AbstractTransactionLineItemIfc> getLineItemsVector()
    {
        return lineItemsVector;
    }

    /**
     * Retrieves line items array.
     *
     * @return line items for this transaction
     */
    public AbstractTransactionLineItemIfc[] getLineItems()
    {
        return toItemArray(lineItemsVector);
    }

    /**
     * Returns iterator for line items.
     *
     * @return iterator for line items
     */
    public Iterator<AbstractTransactionLineItemIfc> getLineItemsIterator()
    {
        return lineItemsVector.iterator();
    }

    /**
     * Retrieves order line items array.
     *
     * @return order line items for this transaction
     */
    public OrderLineItemIfc[] getOrderLineItems()
    {
        OrderLineItemIfc[] oList = null;
        ArrayList<Object> oArray = new ArrayList<Object>();
        Object o = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            // test line item type
            o = i.next();
            if (o instanceof OrderLineItemIfc)
            {
                oArray.add(o);
            }
        }

        // create array from list
        oList = new OrderLineItemIfc[oArray.size()];
        oArray.toArray(oList);

        return oList;
    }

    /**
     * Retrieves a Collection containing KitHeaderLineItemIfcs that are
     * associated with this transaction.
     *
     * @return Collection of KitHeaderLineItemIfcs
     */
    public Collection<SaleReturnLineItemIfc> getKitHeaderLineItems()
    {
        ArrayList<SaleReturnLineItemIfc> headers = new ArrayList<SaleReturnLineItemIfc>();
        SaleReturnLineItemIfc item = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            item = (SaleReturnLineItemIfc)i.next();
            if (item.isKitHeader())
            {
                headers.add(item);
            }
        }
        return headers;
    }

    /**
     * Retrieves a subset of line items excluding those of the type passed in as
     * an argument. This method is used for situations where specific types of
     * items need to be displayed and/or manipulated. You can test more than one
     * type of item at a time by doing a bitwise OR on type. Example: to check
     * for price adjustment components and kit headers set type to
     * ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM |
     * ItemConstants.ITEM_KIT_CODE_HEADER
     *
     * @param type An int indicating the type of line item to exclude. Valid
     *            types are declared in ItemConstantsIfc
     * @return an array containing all line items from this transaction
     *         excluding those of the type defined by argument
     * @see oracle.retail.stores.domain.stock.ItemConstantsIfc
     */
    public SaleReturnLineItemIfc[] getLineItemsExcluding(int type)
    {
        ArrayList<AbstractTransactionLineItemIfc> items = new ArrayList<AbstractTransactionLineItemIfc>();
        Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator();
        SaleReturnLineItemIfc temp = null;

        while (i.hasNext())
        {
            temp = (SaleReturnLineItemIfc)i.next();
            checkItemType(type, temp, items);
        }
        return toItemArray(items);
    }

    /**
     * Retrieves a subset of sale line items excluding those of the type passed
     * in as an argument. This method is used for situations where specific
     * types of items need to be displayed and/or manipulated. You can test more
     * than one type of item at a time by doing a bitwise OR on type. Example:
     * to check for price adjustment components and kit headers set type to
     * ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM |
     * ItemConstants.ITEM_KIT_CODE_HEADER
     *
     * @param type An int indicating the type of line item to exclude. Valid
     *            types are declared in ItemConstantsIfc
     * @return an array containing all sale line items from this transaction
     *         excluding those of the type defined by argument
     * @see oracle.retail.stores.domain.stock.ItemConstantsIfc
     */
    public SaleReturnLineItemIfc[] getSaleLineItemsExcluding(int type)
    {
        ArrayList<AbstractTransactionLineItemIfc> items = new ArrayList<AbstractTransactionLineItemIfc>();
        Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator();
        SaleReturnLineItemIfc temp = null;

        while (i.hasNext())
        {
            // check for sale items with corresponding kit type
            temp = (SaleReturnLineItemIfc)i.next();
            if (temp.isSaleLineItem())
            {
                checkItemType(type, temp, items);
            }
        }

        return toItemArray(items);
    }

    /**
     * Retrieves a subset of return line items excluding those of the type
     * passed in as an argument. This method is used for situations where
     * specific types of items need to be displayed and/or manipulated. You can
     * test more than one type of item at a time by doing a bitwise OR on type.
     * Example: to check for price adjustment components and kit headers set
     * type to ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM |
     * ItemConstants.ITEM_KIT_CODE_HEADER
     *
     * @param type An int indicating the type of line item to exclude. Valid
     *            types are declared in ItemConstantsIfc
     * @return an array containing all return line items from this transaction
     *         excluding those of the type defined by argument
     * @see oracle.retail.stores.domain.stock.ItemConstantsIfc
     */
    public SaleReturnLineItemIfc[] getReturnLineItemsExcluding(int type)
    {
        ArrayList<AbstractTransactionLineItemIfc> items = new ArrayList<AbstractTransactionLineItemIfc>();
        Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator();
        SaleReturnLineItemIfc temp = null;

        while (i.hasNext())
        {
            temp = (SaleReturnLineItemIfc)i.next();
            if (temp.isReturnLineItem())
            {
                checkItemType(type, temp, items);
            }

        }
        return toItemArray(items);
    }

    /**
     * Retrieves a subset of price adjustment line items excluding those of the
     * type passed in as an argument. This method is used for kit item display
     * and manipulation.
     *
     * @param type An int indicating the type of line item to exclude. Valid
     *            types are declared in ItemKitConstantsIfc
     * @see oracle.retail.stores.domain.stock.ItemKitConstantsIfc
     * @return array containing all sale line items from this transaction
     *         excluding those of the type defined by argument
     */
    public SaleReturnLineItemIfc[] getPriceAdjustmentLineItemsExcluding(int type)
    {
        ArrayList<AbstractTransactionLineItemIfc> items = new ArrayList<AbstractTransactionLineItemIfc>();
        Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator();
        SaleReturnLineItemIfc temp = null;

        while (i.hasNext())
        {
            // check for sale items with corresponding kit type
            temp = (SaleReturnLineItemIfc)i.next();
            if (temp.isPriceAdjustmentLineItem())
            {
                checkItemType(type, temp, items);
            }
        }

        return toItemArray(items);
    }

    /**
     * Tests an item to see if its kit type matches the type argument. If the
     * item is not of type defined, it is added to the Collection argument.
     *
     * @param type - a kit code type to test for
     * @param item - the item to test
     * @param items - the collection to add to if not of defined type
     * @see oracle.retail.stores.domain.stock.ItemKitConstantsIfc
     */
    public void checkKitType(int type, SaleReturnLineItemIfc item, Collection<SaleReturnLineItemIfc> items)
    {
        switch (type)
        {
        case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
            if (!item.isKitComponent())
            {
                items.add(item);
            }
            break;
        case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
            if (!item.isKitHeader())
            {
                items.add(item);
            }
            break;
        case ItemKitConstantsIfc.ITEM_KIT_CODE_NO_KIT:
            if (!item.isKitComponent() && !item.isKitHeader())
            {
                items.add(item);
            }
            break;
        default:
            items.add(item);
            break;
        }
    }

    /**
     * Tests an item to see if its type matches the type argument. If the item
     * is not of type defined, it is added to the Collection argument. This
     * method is similar to checkKitType except that you can test more than one
     * type of item at a time by doing a bitwise OR. Example: to check for price
     * adjustment components and kit headers set type to
     * ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM |
     * ItemConstants.ITEM_KIT_CODE_HEADER
     *
     * @param type - an item code type to test for
     * @param item - the item to test
     * @param items - the collection to add to if not of defined type
     * @see oracle.retail.stores.domain.stock.ItemConstantsIfc
     */
    public void checkItemType(int type, SaleReturnLineItemIfc item, Collection<AbstractTransactionLineItemIfc> items)
    {
        boolean addItem = true;

        if ((ItemConstantsIfc.ITEM_KIT_CODE_COMPONENT & type) == ItemConstantsIfc.ITEM_KIT_CODE_COMPONENT)
        {
            if (item.isKitComponent())
            {
                addItem = false;
            }
        }
        if ((ItemConstantsIfc.ITEM_KIT_CODE_HEADER & type) == ItemConstantsIfc.ITEM_KIT_CODE_HEADER)
        {
            if (item.isKitHeader())
            {
                addItem = false;
            }
        }
        if ((ItemConstantsIfc.ITEM_KIT_CODE_NO_KIT | type) == ItemConstantsIfc.ITEM_KIT_CODE_NO_KIT)
        {
            if (item.isKitComponent() || item.isKitHeader())
            {
                addItem = false;
            }
        }
        if ((ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM & type) == ItemConstantsIfc.ITEM_PRICEADJ_LINEITEM)
        {
            if (item.isPriceAdjustmentLineItem())
            {
                addItem = false;
            }
        }
        if ((ItemConstantsIfc.ITEM_PRICEADJ_COMPONENT & type) == ItemConstantsIfc.ITEM_PRICEADJ_COMPONENT)
        {
            if (item.isPartOfPriceAdjustment())
            {
                addItem = false;
            }
        }
        if (addItem)
        {
            items.add(item);
        }
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
      // create a new vector (groupLineItems) that contains just the line
      // items
      // that are part of the product group
        Vector<SaleReturnLineItemIfc> groupLineItems = new Vector<SaleReturnLineItemIfc>(2);
        SaleReturnLineItemIfc[] items = null;
        if (lineItemsVector != null)
        {
            for (int i = 0; i < lineItemsVector.size(); i++)
            {
                if (lineItemsVector.elementAt(i) instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)lineItemsVector.elementAt(i);
                    if (item.getPLUItem().getItemClassification().getGroup().getGroupID().equals(prodGroupID))
                    {
                        groupLineItems.addElement(item);
                    }
                }
            }
        }
        // create a SaleReturnLineItemIfc[] array and copy the groupLineItems
        // elements into it
        if (groupLineItems.size() > 0)
        {
            items = new SaleReturnLineItemIfc[groupLineItems.size()];
            groupLineItems.copyInto(items);
        }

        return items;
    }

    /**
     * Sets the line items array
     *
     * @param items array of AbstractTransactionLineItemIfc references
     */
    public void setLineItems(AbstractTransactionLineItemIfc[] items)
    {
        // clear the Collection
        lineItemsVector.clear();
        if (items != null)
        {
            // add the items from the array and reset line numbers
            lineItemsVector.addAll(Arrays.asList(items));
            // resetLineItemNumbers();
        }
    }

    /**
     * Adds a transaction discount and optionally updates the transaction
     * totals. But discount will not be added for external order transactions.
     *
     * @param disc TransactionDiscountStrategyIfc
     */
    public void addTransactionDiscount(TransactionDiscountStrategyIfc disc)
    {
        // add only if not null.  This is possible because ill defined transaction discount rules
        // in the database will be excluded by AdvancePricingRule.createDiscountStrategy().
        boolean containExernalOrderItem = false;
        if(disc != null)
        {
            AbstractTransactionLineItemIfc[] lineItems = getLineItems();
            for (AbstractTransactionLineItemIfc lineItem : lineItems)
            {
                if (lineItem instanceof SaleReturnLineItemIfc)
                {
                    SaleReturnLineItemIfc saleReturnLineItem = (SaleReturnLineItemIfc)lineItem;
                    if (saleReturnLineItem.isFromExternalOrder())
                    {
                        //External order items cannot have any kind of discount.
                        containExernalOrderItem = true;
                        break;
                    }
                }
            }
            if ( !containExernalOrderItem )
            {
                transactionDiscountsVector.addElement(disc);
            }
        }
    }

    /**
     * Clears transaction discounts by percentage except for those generated by
     * a store coupon or via a preferred customer program.
     */
    public void clearTransactionDiscountsByPercentage()
    {
        TransactionDiscountStrategyIfc d = null;

        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // if transaction percentage discount found, remove it from vector
            if ((d instanceof TransactionDiscountByPercentageIfc) && !(d instanceof CustomerDiscountByPercentageIfc)
                    && !d.isStoreCoupon())
            {
                i.remove();
            }
        }
    }

    /**
     * Removes Preferred Customer discounts that were added to the list of
     * AdvancedPricingRules.
     */
    public void clearCustomerBestDealDiscounts()
    {
        clearBestDealDiscounts(DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER);
    }

    /**
     * Removes selected discounts that were added to the list of
     * AdvancedPricingRules. Discounts removed are determined by the
     * assignmentBasis parameter. Recalculates best deal after removal.
     *
     * @param assignmentBasis
     * @see DiscountRuleConstantsIfc
     */
    public void clearBestDealDiscounts(int assignmentBasis)
    {
        AdvancedPricingRuleIfc rule = null;

        for (Iterator<DiscountRuleIfc> e = advancedPricingRules(); e.hasNext();)
        {
            rule = (AdvancedPricingRuleIfc)e.next();

            // test the rule's assignment basis
            if (rule.getAssignmentBasis() == assignmentBasis)
            {
                // clear the deal discounts
                for (Iterator<BestDealGroupIfc> j = rule.getBestDealGroups().iterator(); j.hasNext();)
                {
                    ((BestDealGroupIfc)j.next()).removeAdvancedPricingDiscounts();
                }

                // clear regular item discounts generated by PCD
                // w/includedInBestDeal flag set to true
                SaleReturnLineItemIfc item = null;
                for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
                {
                    item = (SaleReturnLineItemIfc)i.next();
                    item.clearItemDiscounts(rule.getRuleID());
                }
                // remove the rule from the list
                e.remove();
                break;
            }
        }
        // recalculate discounts
        calculateBestDeal();
    }

    /**
     * Clears customer discounts by percentage.
     */
    public void clearCustomerDiscountsByPercentage()
    {
        TransactionDiscountStrategyIfc d = null;

        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // if customer discount found, remove it from vector
            if (d instanceof CustomerDiscountByPercentageIfc)
            {
                i.remove();
            }

        } // end for (Iterator i = transactionDiscountsVector()...
        
        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)i.next();
            ItemPriceIfc itemPrice = srli.getItemPrice();
            itemPrice.clearItemTransactionDiscounts(DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER);
            itemPrice.calculateItemTotal();
        }
    }

    /**
     * Sets enabled flag on transaction discounts by percentage to specified
     * value. CustomerDiscountByPercentage discounts are not affected.
     *
     * @param enableFlag desired setting of enable flag
     * @deprecated functionality supported by this method was removed from
     *             requirements (Preferred Discount exclusive flag)
     */
    public void enableTransactionDiscountsByPercentage(boolean enableFlag)
    {
        TransactionDiscountStrategyIfc d = null;

        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            // if percentage discount not found, add it to new vector
            if (d instanceof TransactionDiscountByPercentageIfc)
            {
                d.setEnabled(enableFlag);
            }
        }
    }

    /**
     * Sets enabled flag on customer transaction discounts by percentage to
     * specified value.
     *
     * @param enableFlag desired setting of enable flag
     * @deprecated - functionality supported by this method was removed from
     *             requirements(Preferred Discount exclusive flag)
     */
    public void enableCustomerDiscountsByPercentage(boolean enableFlag)
    {
        TransactionDiscountStrategyIfc d = null;
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();
            if (d instanceof CustomerDiscountByPercentageIfc)
            {
                d.setEnabled(enableFlag);
            }
        }
    }

    /**
     * Retrieves array of transaction discounts by percentage, return values do
     * not include CustomerDiscountByPercentageIfcs.
     *
     * @return array of disc transaction discount objects, null if not found
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscountsByPercentage()
    {
        ArrayList<TransactionDiscountStrategyIfc> temp = new ArrayList<TransactionDiscountStrategyIfc>();
        TransactionDiscountStrategyIfc strategy = null;

        // loop through discounts and retrieve percentage discounts
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            strategy = i.next();

            if (strategy instanceof TransactionDiscountByPercentageIfc
                    && !(strategy instanceof CustomerDiscountByPercentageIfc))
            {
                temp.add(strategy);
            }
        }
        TransactionDiscountStrategyIfc[] value = new TransactionDiscountStrategyIfc[temp.size()];
        temp.toArray(value);
        return value;
    }

    /**
     * Sets array of transaction discounts by percentage.
     *
     * @param value array of disc transaction discount objects
     */
    public void setTransactionDiscountsByPercentage(TransactionDiscountStrategyIfc[] value)
    {
        clearTransactionDiscountsByPercentage();

        // if new discounts exist, add them to vector
        if (value != null)
        {
            transactionDiscountsVector.addAll(Arrays.asList(value));
        }
    }

    /**
     * Clears transaction discounts by amount except for those generated by a
     * store coupon.
     */
    public void clearTransactionDiscountsByAmount()
    {
        TransactionDiscountStrategyIfc d = null;

        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            d = i.next();

            if (d instanceof TransactionDiscountByAmountIfc && !d.isStoreCoupon())
            {
                i.remove();
            }
        }
    }

    /**
     * Sets array of transaction discounts by amount.
     *
     * @param value array of disc transaction discount objects
     */
    public void setTransactionDiscountsByAmount(TransactionDiscountStrategyIfc[] value)
    {
        // clear discounts by amount
        clearTransactionDiscountsByAmount();

        // if new discounts exist, add them to vector
        if (value != null)
        {
            transactionDiscountsVector.addAll(Arrays.asList(value));
        }
    }

    /**
     * Retrieves array of transaction discounts by amount.
     *
     * @return array of disc transaction discount objects
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscountsByAmount()
    {
        ArrayList<TransactionDiscountStrategyIfc> temp = new ArrayList<TransactionDiscountStrategyIfc>();
        TransactionDiscountStrategyIfc strategy = null;

        // loop through discounts and retrieve percentage discounts
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            strategy = i.next();

            if (strategy instanceof TransactionDiscountByAmountIfc)
            {
                temp.add(strategy);
            }
        }
        TransactionDiscountStrategyIfc[] value = new TransactionDiscountStrategyIfc[temp.size()];
        temp.toArray(value);
        return value;

    }

    /**
     * Retrieves array of transaction discounts. Returns null if no discounts
     * are available.
     *
     * @return array of disc transaction discount objects
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscounts()
    {
        int numDiscounts = transactionDiscountsVector.size();
        TransactionDiscountStrategyIfc[] discounts = null;

        if (numDiscounts > 0)
        {
            discounts = new TransactionDiscountStrategyIfc[numDiscounts];
            transactionDiscountsVector.copyInto(discounts);
        }

        return discounts;
    }

    /**
     * Retrieves iterator for transaction discounts.
     *
     * @return interator of transaction discount objects
     */
    public Iterator<TransactionDiscountStrategyIfc> getTransactionDiscountsIterator()
    {
        return (transactionDiscountsVector.iterator());
    }

    /**
     * Sets contents of local discount vector to an array of transaction
     * discounts.
     *
     * @param value array of disc transaction discount objects
     */
    public void setTransactionDiscounts(TransactionDiscountStrategyIfc[] value)
    {
        clearTransactionDiscounts();

        if (value != null)
        {
            transactionDiscountsVector.addAll(Arrays.asList(value));
        }
    }

    /**
     * Adds an array of transaction discounts to local discount vector.
     *
     * @param value array of disc transaction discount objects
     */
    public void addTransactionDiscounts(TransactionDiscountStrategyIfc[] value)
    {
        // add discounts to vector
        if (value != null)
        {
            transactionDiscountsVector.addAll(Arrays.asList(value));
        }
    }

    /**
     * Clears transaction discounts.
     */
    public void clearTransactionDiscounts()
    {
        transactionDiscountsVector.clear();
    }

    /**
     * Retrieves array containing specified transaction discounts.
     *
     * @param discountMethod Discount method from DiscountRuleConstantsIfc
     * @param assignmentBasis Assignment basis from DiscountRuleConstantsIfc
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     * @return array of disc transaction discount objects
     */
    public TransactionDiscountStrategyIfc[] getTransactionDiscounts(int discountMethod, int assignmentBasis)
    {
        ArrayList<TransactionDiscountStrategyIfc> temp = new ArrayList<TransactionDiscountStrategyIfc>();
        TransactionDiscountStrategyIfc strategy = null;

        // loop through discounts and retrieve percentage discounts
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            strategy = i.next();

            if (strategy.getDiscountMethod() == discountMethod && strategy.getAssignmentBasis() == assignmentBasis)
            {
                temp.add(strategy);
            }
        }
        TransactionDiscountStrategyIfc[] value = new TransactionDiscountStrategyIfc[temp.size()];
        temp.toArray(value);
        return value;
    }

    /**
     * Clears specified transaction discounts from the transaction discount
     * collection.
     *
     * @param discountMethod Discount method from DiscountRuleConstantsIfc
     * @param assignmentBasis Assignment basis from DiscountRuleConstantsIfc
     * @see oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc
     */
    public void clearTransactionDiscounts(int discountMethod, int assignmentBasis)
    {
        TransactionDiscountStrategyIfc strategy = null;
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            strategy = i.next();

            if (strategy.getDiscountMethod() == discountMethod && strategy.getAssignmentBasis() == assignmentBasis)
            {
                i.remove();
            }
        }
    }

    /**
     * Removes a advanced pricing rule from the transaction's advanced pricing
     * rule collection if it is not associated with any other line items. The
     * key is the discount rule id.
     *
     * @param rule advanced pricing rule to remove
     */
    public void removeAdvancedPricingRule(AdvancedPricingRuleIfc rule)
    {
        String ruleId = rule.getRuleID();

        if (rule.isIncludedInBestDeal() && advancedPricingRules.containsKey(ruleId))
        {

            if (!isRuleStillUsed(rule))
            {
                advancedPricingRules.remove(ruleId);
            }
        }
        else
        {
            if (!isRuleStillUsed(rule))
            {
                // Transaction discounts not included in best deal may need to
                // be removed.
                Iterator<TransactionDiscountStrategyIfc> iter = transactionDiscountsVector.iterator();
                while (iter.hasNext())
                {
                    TransactionDiscountStrategyIfc txDiscRule = iter.next();
                    if (txDiscRule.getRuleID().equals(ruleId))
                    {
                        iter.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Adds an advanced pricing rule to the transaction if it is not already
     * stored in the transaction's advanced pricing rule collection. The key is
     * the discount rule id.
     *
     * @param rule Advanced pricing rule to add
     */
    public void addAdvancedPricingRule(AdvancedPricingRuleIfc rule)
    {
        if ( rule.isIncludedInBestDeal() )
        {
            if (!advancedPricingRules.containsKey(rule.getRuleID()))
            {
                advancedPricingRules.put(rule.getRuleID(), rule);
            }
            else // a copy of this rule already exists in the transaction's map.
            {
                if (rule.isStoreCoupon() && !rule.isScopeTransaction())
                {
                    /* All rules that are coupon-based and item-scoped have to
                     * have their ReferenceID set. This code ensures that the
                     * rule has that value set in the case it was retrieved
                     * without it.
                     */
                    DiscountRuleIfc previouslyCachedRule = advancedPricingRules.get(rule.getRuleID());
                    if (previouslyCachedRule.getReferenceID() == null && rule.getReferenceID() != null)
                    {
                        previouslyCachedRule.setReferenceID(rule.getReferenceID());
                        previouslyCachedRule.setReferenceIDCode(rule.getReferenceIDCode());
                    }
                }
            }
        }
        else if (rule.isScopeTransaction())
        {
            checkAddTransactionDiscount(rule);
        }
    }

    /**
     * Adds AdvancedPricing rules to the transaction.
     *
     * @param rules array containing advancedPricingRuleIfcs
     */
    public void addAdvancedPricingRules(AdvancedPricingRuleIfc[] rules)
    {
        if (rules != null)
        {
            for (int i = 0; i < rules.length; i++)
            {
                addAdvancedPricingRule(rules[i]);
            }
        }
    }

    /**
     * Add advanced pricing rules associated with the PLU item
     *
     * @param pItem the PLU item
     */
    public void addItemAdvancedPricingRules(PLUItemIfc pItem)
    {
        // pass back line item
        // attempt to add each of the rules associated with the PLUItem
        // to the transaction
        for (Iterator<AdvancedPricingRuleIfc> i = pItem.advancedPricingRules(); i.hasNext();)
        {
            AdvancedPricingRuleIfc rule = i.next();

            if (rule.isThresholdDiscountRule())
            {
                createThresholdAdvancedPricingRules(rule);
            }
            else
            {
                // if the rule has pricIngGroupId than it must be calculated only
                // when customer is linked since it is related to pricingGroupID
                if (rule.getPricingGroupID() == -1 || rule.getPricingGroupID() == 0)
                {
                    // addAdvancedPricingRule() only adds the rule
                    // if it is not already associated with the transaction
                    addAdvancedPricingRule(rule);
                }
            }
        } // end for (Iterator i = pItem.advancedPricingRules(); i.hasNext(); )
    }

    /**
     * For multi threshold rule, create a seperate rule for each threshold and
     * add to the advancedPricingRules vector
     *
     * @param rule
     */
    private void createThresholdAdvancedPricingRules(AdvancedPricingRuleIfc rule)
    {
        // create a separate rule for each threshold
        List<Threshold> thresholdList = rule.getThresholdList();
        for (Threshold threshold : thresholdList)
        {
            AdvancedPricingRuleIfc thresholdRule = (AdvancedPricingRuleIfc)rule.clone();
            thresholdRule.clearThresholdList();
            setThresholdValuesToRule(thresholdRule, threshold);
            // making ruleId as combination of ruleId and thresholdId
            thresholdRule.setRuleID(thresholdRule.getRuleID().concat(Util.COLON).concat(threshold.getThresholdID()));

            DiscountListIfc sourceList = thresholdRule.getSourceList();
            Iterator<String> it = sourceList.criteria();

            while (it.hasNext())
            {
                if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_QUANTITY)
                {
                    thresholdRule.getSourceList().getEntry(it.next()).setQuantityRequired(threshold.getThresholdVal());
                }
                else if (rule.getThresholdTypeCode() == DiscountRuleConstantsIfc.THRESHOLD_AMOUNT)
                {
                    CurrencyIfc thrAmount = DomainGateway.getCurrencyInstance(DomainGateway.getBaseCurrencyType()
                            .getCountryCode(), Integer.toString(threshold.getThresholdVal()));
                    thresholdRule.getSourceList().getEntry(it.next()).setAmountRequired(thrAmount);
                }
            }
            if (thresholdRule.getPricingGroupID() == -1 || rule.getPricingGroupID() == 0)
            {
                addAdvancedPricingRule(thresholdRule);
            }
        }

    }

    /**
     * Returns an array of the advanced pricing rules for this transaction.
     *
     * @return AdvancedPricingRuleIfc[]
     */
    public AdvancedPricingRuleIfc[] getAdvancedPricingRules()
    {
        Collection<DiscountRuleIfc> rules = advancedPricingRules.values();

        return rules.toArray(new AdvancedPricingRuleIfc[rules.size()]);
    }

    /**
     * Returns an iterator over the advanced pricing rules for this transaction.
     *
     * @return Iterator
     */
    public Iterator<DiscountRuleIfc> advancedPricingRules()
    {
        return advancedPricingRules.values().iterator();
    }

    /**
     * Clears the advanced pricing rules from the transaction.
     */
    public void clearAdvancedPricingRules()
    {
        advancedPricingRules.clear();
    }

    /**
     * Returns a boolean indicating whether this transaction has advanced
     * pricing rules.
     *
     * @return boolean
     */
    public boolean hasAdvancedPricingRules()
    {
        return !advancedPricingRules.isEmpty();
    }

    /**
     * Adds a best deal group to the transaction. Negative discounts are allowed
     * so as to allow for non-receipt return to receive the lowest pricing
     *
     * @param group BestDealGroup to add
     */
    public void addBestDealGroup(BestDealGroupIfc group)
    {
        bestDealGroups.add(group);
     }

    /**
     * Adds a best deal group to the transaction.
     *
     * @param groups BestDealGroup to add
     */
    public void addBestDealGroups(List<BestDealGroupIfc> groups)
    {
        for (Iterator<BestDealGroupIfc> iter = groups.iterator(); iter.hasNext();)
        {
            // Prevent negative discounts from being added to the groups.
            BestDealGroupIfc group = (BestDealGroupIfc)iter.next();
            addBestDealGroup(group);
        }
    }

    /**
     * Clears the best deal groups from the transaction. Removes any advanced
     * pricing discounts currently applied to the targets within the
     * transaction.
     */
    protected void clearBestDealGroups()
    {
        if (bestDealGroups != null)
        {
            bestDealGroups.clear();
        }
        else
        {
            bestDealGroups = new ArrayList<BestDealGroupIfc>(2);
        }
    }

    /**
     * Removes any advanced pricing discounts currently applied to the targets
     * within the transaction. Calling this method does not have any effect on
     * the transaction totals.
     */
    public void clearBestDealDiscounts()
    {
        for (Iterator<BestDealGroupIfc> i = bestDealWinners.iterator(); i.hasNext();)
        {
            BestDealGroupIfc winner = i.next();
            winner.removeAdvancedPricingDiscounts();

            // Clear the deal information in the targets
            ArrayList<DiscountTargetIfc> targets = winner.getTargets();
            if (targets != null)
            {
                Iterator<DiscountTargetIfc> it = targets.iterator();

                // make sure the original line items have been updated
                while (it.hasNext())
                {
                    int lineNo = ((AbstractTransactionLineItemIfc)it.next()).getLineNumber();
                    AbstractTransactionLineItemIfc originalLine = retrieveLineItemByID(lineNo);
                    // The discounts from retrieved transaction line items are not recalculated; do
                    // remove the discount from the line item or mark as an available source.
                    if (originalLine != null && !isRetrievedReturnLineItem(originalLine))
                    {
                        ((DiscountTargetIfc)originalLine).removeAdvancedPricingDiscount();
                        ((DiscountSourceIfc)originalLine).setSourceAvailable(true);
                    }

                } // end while(i.hasNext())
            } // if (targets != null)

            ArrayList<DiscountSourceIfc> sources = winner.getSources();
            if (sources != null)
            {
                Iterator<DiscountSourceIfc> it = sources.iterator();

                // make sure the original line items have been updated
                while (it.hasNext())
                {
                    int lineNo = ((AbstractTransactionLineItemIfc)it.next()).getLineNumber();
                    AbstractTransactionLineItemIfc originalLine = retrieveLineItemByID(lineNo);
                    // The discounts from retrieved transaction line items cannot serve as a
                    // discount source.
                    if (originalLine != null && !isRetrievedReturnLineItem(originalLine))
                    {
                        ((DiscountSourceIfc)originalLine).setSourceAvailable(true);
                    }

                } // end while(i.hasNext())
            } // if (sources != null)

        } // end for (Iterator i = bestDealWinners.iterator(); i.hasNext(); )
        bestDealWinners.clear();

    }

    /**
     * Calls {@link #getDiscountSources(boolean)} with <code>false</code>
     */
    public ArrayList<DiscountSourceIfc> getDiscountSources()
    {
        return getDiscountSources(false);
    }

    /**
     * Returns an array list of DiscountSourceIfcs (objects that are eligible
     * sources) for an advanced pricing rule. The sources are retrieved from the
     * line items vector. If <code>nonReceiptReturns</code> is true, consider
     * only lines items that are non-receipt returns. Otherwise if false,
     * consider only Sale line items.
     *
     * @param nonReceiptReturns if true, consider only return line items that
     *                  are not receipted.
     * @return ArrayList containing sources eligible for a rule
     * @see oracle.retail.stores.domain.discount.DiscountSourceIfc
     */
    @SuppressWarnings("unchecked")
    public ArrayList<DiscountSourceIfc> getDiscountSources(boolean nonReceiptReturns)
    {
        ArrayList<DiscountSourceIfc> sources = new ArrayList<DiscountSourceIfc>();
        AbstractTransactionLineItemIfc lineItem = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = getLineItemsVector().iterator(); i.hasNext();)
        {
            lineItem = i.next();
            if (((DiscountSourceIfc)lineItem).isSourceAvailable())
            {
                addLineItemForDiscount(sources, lineItem, nonReceiptReturns);
            }
        }

        // sort the line items in descending order so that higher priced
        // items of the same type are used first to satisfy rule criteria
        Collections.sort(sources, Comparators.lineItemPriceDescending);
        // this sort ensures that discounts will be applied first to kit items
        // that satisfy an APR and then to any items that are in a kit but are
        // scanned individually, necessary because the kit header is not part
        // of the source criteria for a kit pricing rule
        Collections.sort(sources, Comparators.kitComponentLineItemsFirst);

        return sources;
    }


    /**
     * @return ArrayList containing sources eligible for a rule
     */
    public ArrayList<DiscountSourceIfc> getTransactionDiscountSources()
    {
        return getTransactionDiscountSources(false);
    }

    /**
     * Returns an array list of DiscountSourceIfcs (objects that are eligible
     * sources) for a transaction level advanced pricing rule. The sources are
     * retrieved from the line items vector. If <code>nonReceiptReturns</code>
     * is true, consider only lines items that are non-receipt returns.
     * Otherwise if false, exclude them.
     *
     * @param nonReceiptReturns if true, consider only return line items that
     *                  are not receipted.
     * @return ArrayList containing sources eligible for a rule
     */
    @SuppressWarnings("unchecked")
    public ArrayList<DiscountSourceIfc> getTransactionDiscountSources(boolean nonReceiptReturns)
    {
        ArrayList<DiscountSourceIfc> sources = new ArrayList<DiscountSourceIfc>();
        AbstractTransactionLineItemIfc lineItem = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = getLineItemsVector().iterator(); i.hasNext();)
        {
            lineItem = i.next();
            if (((DiscountSourceIfc)lineItem).isSourceAvailableForTransactionDiscounts())
            {
                addLineItemForDiscount(sources, lineItem, nonReceiptReturns);
            }
        }

        // sort the line items in descending order so that higher priced
        // items of the same type are used first to satisfy rule criteria
        Collections.sort(sources, Comparators.lineItemPriceDescending);

        return sources;

    }


    /**
     * Calls {@link #getDiscountTargets(boolean)} with <code>false</code>
     */
    public ArrayList<DiscountTargetIfc> getDiscountTargets()
    {
        return getDiscountTargets(false);
    }

    /**
     * Returns an array list of available DiscountTargetIfcs (objects that are
     * eligible targets) for an advanced pricing rule. The targets are retrieved
     * from the transaction's line items vector. If
     * <code>nonReceiptReturns</code> is true, consider only lines items that
     * are non-receipt returns. Otherwise if false, consider only sale line
     * items.
     *
     * @param nonReceiptReturns if true, consider only return line items that
     *                  are not receipted.
     * @return ArrayList containing targets eligible for a rule
     * @see oracle.retail.stores.domain.discount.DiscountTargetIfc
     */
    @SuppressWarnings("unchecked")
    public ArrayList<DiscountTargetIfc> getDiscountTargets(boolean nonReceiptReturns)
    {

        ArrayList<DiscountTargetIfc> targets = new ArrayList<DiscountTargetIfc>();
        AbstractTransactionLineItemIfc lineItem = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = getLineItemsVector().iterator(); i.hasNext();)
        {
            lineItem = i.next();
            if (((DiscountSourceIfc)lineItem).isSourceAvailable() && ((DiscountTargetIfc)lineItem).isTargetEnabled())
            {
                addLineItemForDiscount(targets, lineItem, nonReceiptReturns);
            }
        }

        // sort the line items in descending order so that higher priced
        // items of the same type are used first to satisfy rule criteria
        Collections.sort(targets, Comparators.lineItemPriceDescending);
        // this sort ensures that discounts will be applied first to kit items
        // that satisfy an APR and then to any items that are in a kit but are
        // scanned individually, necessary because the kit header is not part
        // of the source criteria for a kit pricing rule
        Collections.sort(targets, Comparators.kitComponentLineItemsFirst);

        return targets;

    }

    public ArrayList<DiscountTargetIfc> getTransactionDiscountTargets()
    {
        return getTransactionDiscountTargets(false);
    }

    /**
     * Returns an array list of available DiscountTargetIfcs (objects that are
     * eligible targets) for a transaction level advanced pricing rule. The
     * targets are retrieved from the transaction's line items vector. If
     * <code>nonReceiptReturns</code> is true, consider only lines items that
     * are non-receipt returns. Otherwise if false, exclude them.
     *
     * @param nonReceiptReturns if true, consider only return line items that
     *                  are not receipted.
     * @return ArrayList containing targets eligible for a rule
     * @see oracle.retail.stores.domain.discount.DiscountTargetIfc
     */
    @SuppressWarnings("unchecked")
    public ArrayList<DiscountTargetIfc> getTransactionDiscountTargets(boolean nonReceiptReturns)
    {

        ArrayList<DiscountTargetIfc> targets = new ArrayList<DiscountTargetIfc>();
        AbstractTransactionLineItemIfc lineItem = null;

        for (Iterator<AbstractTransactionLineItemIfc> i = getLineItemsVector().iterator(); i.hasNext();)
        {
            lineItem = i.next();
            if (((DiscountSourceIfc)lineItem).isSourceAvailableForTransactionDiscounts() && ((DiscountTargetIfc)lineItem).isTargetEnabled())
            {
                addLineItemForDiscount(targets, lineItem, nonReceiptReturns);
            }
        }

        // sort the line items in descending order so that higher priced
        // items of the same type are used first to satisfy rule criteria
        Collections.sort(targets, Comparators.lineItemPriceDescending);

        return targets;

    }

    /**
     * Add the line item to the list if either
     * <ui>
     *   <li>
     *     Looking for non-receipt-return line items and the specified line
     *     item is a return and is not retrieved from a previous sale.
     *   </li>
     *   <li>
     *     Looking for non-return items.
     *   </li>
     * </ul>
     *
     * @param list a list of line items that can be sources or targets
     * @param lineItem the line item that may be added to the list.
     * @param nonReceiptReturns if true, consider only return line items that
     *                  are not receipted.
     * @see #isRetrievedReturnLineItem(AbstractTransactionLineItemIfc)
     * @since 14.0
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void addLineItemForDiscount(ArrayList list,
            AbstractTransactionLineItemIfc lineItem, boolean nonReceiptReturns)
    {
        if (lineItem instanceof SaleReturnLineItemIfc)
        {
            SaleReturnLineItemIfc slri = (SaleReturnLineItemIfc)lineItem;
            // if we want only non-receipted return line items, then add it
            if (nonReceiptReturns)
            {
            	// If this is a non reciepted return, that is 1) the sale return line item is a return, and it not (from
            	// a retrieved transaction or a line item for which an original reciept id has been entered). 
                if (slri.isReturnLineItem() && !(slri.isFromTransaction() || slri.getReturnItem().isNonRetrievedReceiptedItem()))
                {
                    list.add(lineItem);
                }
            }
            // else, if we don't, only add it if its not a return
            else if (!slri.isReturnLineItem())
            {
                list.add(lineItem);
            }   
        }
    }

    /**
     * Retrieves size of lineItemsVector vector.
     *
     * @return line items vector size
     */
    public int getLineItemsSize()
    {
        return lineItemsVector.size();
    }

    /**
     * Retrieves sales associate.
     *
     * @return sales associate
     */
    public EmployeeIfc getSalesAssociate()
    {
        return (salesAssociate);
    }

    /**
     * Retrieves sales associate identifier.
     *
     * @return sales associate identifier
     */
    public String getSalesAssociateID()
    {
        return (getSalesAssociate().getEmployeeID());
    }

    /**
     * Sets salesAssociate attribute.
     *
     * @param emp salesAssociate
     */
    public void setSalesAssociate(EmployeeIfc emp)
    {
        salesAssociate = emp;

    }

    /**
     * Retrieves default registry identifier.
     *
     * @return default registry identifier
     */
    public RegistryIDIfc getDefaultRegistry()
    {
        return (defaultRegistry);
    }

    /**
     * Sets defaultRegistry attribute.
     *
     * @param value default registry
     */
    public void setDefaultRegistry(RegistryIDIfc value)
    {
        defaultRegistry = value;
    }

    /**
     * Sets customer attribute and performs other operations associated with
     * assigning a customer to a transaction, such as setting discount rules.
     *
     * @param value customer
     */
    public void linkCustomer(CustomerIfc value)
    {
        if (value != null)
        {

            // assigning the value to globally declared customer object which we
            // use it at addPLUItem to get the pricingGroupID
            customer = value;
            // customer has changed. apply or remove customer-specific pricing
            revaluateLineItemPriceOnTransaction();

            // remove any customer generated best deal discounts
            clearCustomerBestDealDiscounts();

            // remove any regular transaction customer discounts
            // and update the transaction totals
            clearCustomerDiscountsByPercentage();
            // clear the customer specific advanced pricing rule
            clearCSPAdvancedPricingRule();
            if (customer.getPricingGroupID() != null)
            {
                constructCSPAdvancedPricingRuleForTransaction();
            }

            DiscountRuleIfc discount = null;
            DiscountRuleIfc[] discountList = value.getFirstGroupsDiscountRules();

            // check for discount group rule
            if (discountList != null && discountList.length > 0)
            {
                discount = discountList[0];
            }

            if (discount != null)
            {
                // boolean to indicate if the customer discount
                // is applied using best deal processing
                boolean applied = false;

                if (discount.isIncludedInBestDeal())
                {
                    // create a pricing rule using attributes from the customer
                    // discount
                    AdvancedPricingRuleIfc rule = DomainGateway.getFactory().getAdvancedPricingRuleInstance();

                    rule.setDiscountMethod(discount.getDiscountMethod());
                    rule.setRuleID(discount.getRuleID());
                    rule.setReason(discount.getReason());
                    rule.setAssignmentBasis(DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER);
                    rule.setDiscountScope(discount.getDiscountScope());
                    rule.setDiscountRate(discount.getDiscountRate());
                    rule.setIncludedInBestDeal(discount.isIncludedInBestDeal());
                    rule.activateTransactionDiscount();

                    // add it to the collection of rules
                    addAdvancedPricingRule(rule);

                    // force recalculation of deal discounts
                    calculateBestDeal();

                    // check to see if it was applied
                    applied = isBestDealWinner(rule);
                }
                else
                {
                    calculateBestDeal();
                }

                // if not a best deal winner, the customer discount needs to be
                // applied to any items that are not discounted by advanced
                // pricing rules
                if (!applied)
                {
                    // create a CustomerDiscount strategy
                    CustomerDiscountByPercentageIfc disc = DomainGateway.getFactory()
                            .getCustomerDiscountByPercentageInstance();
                    disc.initialize(discount.getDiscountRate(), discount.getReason(), discount.getRuleID(),
                            discount.isIncludedInBestDeal());

                    // add it to the transaction discounts
                    addTransactionDiscount(disc);
                }

            } // end if (discount != null)
            else
            {
                calculateBestDeal();
            }
        }
    }

    /**
     * It reset the saleReturnLineItem selling price based on the customer
     * specific pricing.
     */
    public void revaluateLineItemPrice(SaleReturnLineItemIfc srli)
    {
        PLUItemIfc pluItem = srli.getPLUItem();
        revaluateLineItemPrice(srli, pluItem);
    }

    /**
     * It reset the saleReturnLineItem selling price based on the customer
     * specific pricing and pluItem.
     */
    public void revaluateLineItemPrice(SaleReturnLineItemIfc srli, PLUItemIfc pluItem)
    {
        if (srli.getItemPrice().getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            EYSDate when = new EYSDate();
            int prcGrpId = -1;
            if (customer != null && customer.getPricingGroupID() != null)
            {
                prcGrpId = customer.getPricingGroupID();
            }

            CurrencyIfc customerPrice = pluItem.getPrice(when, prcGrpId);
            CurrencyIfc defaultSellingPrice = pluItem.getPrice();
            if (customerPrice.compareTo(defaultSellingPrice) == CurrencyIfc.LESS_THAN)
            {
                if (!pluItem.getItemClassification().isPriceEntryRequired())
                {
                    srli.getItemPrice().setSellingPrice(customerPrice);
                    PriceChangeIfc promotionalPriceChange = srli.getPLUItem().getEffectivePromotionalPrice(when, prcGrpId);
                    srli.getItemPrice().setAppliedPromotion(promotionalPriceChange);
                }
            }
            else
            {
                if (!pluItem.getItemClassification().isPriceEntryRequired())
                {
                    srli.getItemPrice().setSellingPrice(defaultSellingPrice);
                    PriceChangeIfc promotionalPriceChange = srli.getPLUItem().getEffectivePromotionalPrice(when);
                    srli.getItemPrice().setAppliedPromotion(promotionalPriceChange);
                }
            }
        }
    }

    /**
     * It reset the saleReturnLineItem selling price based on the customer
     * specific pricing.
     */
    protected void revaluateReturnLineItemPrice(SaleReturnLineItemIfc srli)
    {
        CurrencyIfc sellingPrice = srli.getReturnItem().getPrice();

        if (customer != null && customer.getPricingGroupID() != null && customer.getPricingGroupID().intValue() > 0)
        {
            CurrencyIfc customerPrice = srli.getPLUItem().getReturnPrice(customer.getPricingGroupID());
            if (customerPrice.compareTo(sellingPrice) < 0)
            {
                srli.getReturnItem().setPrice(customerPrice);
                srli.getItemPrice().setSellingPrice(customerPrice);
            }
        }
        else
        {
            CurrencyIfc customerPrice = srli.getReturnItem().getPrice();
            srli.getReturnItem().setPrice(customerPrice);
            srli.getItemPrice().setSellingPrice(customerPrice);
        }
    }

    /**
     * recalculate line items in the saleReturnLineItemIfc when customer is
     * linked after adding some items to transaction.
     *
     * @param pricingGroupID
     */
    public void revaluateLineItemPriceOnTransaction()
    {
        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)i.next();
            if (!srli.isReturnLineItem() && !srli.hasExternalPricing() && (srli.isInStorePriceDuringPickup() || !srli.isPickupCancelLineItem()))
            {
                revaluateLineItemPrice(srli);
                srli.calculateLineItemPrice();
            }
            else
            {
                if (srli.getReturnItem() != null &&
                    !srli.getReturnItem().isFromRetrievedTransaction() &&
                    !srli.getReturnItem().hasOriginalTransactionID() &&
                    !srli.hasExternalPricing())
                {
                    revaluateReturnLineItemPrice(srli);
                    srli.calculateLineItemPrice();
                }
            }
        }
    }

    /**
     * Returns the discount strategy used to generate the preferred customer
     * discount.
     *
     * @return DiscountRuleIfc[] containing discounts assigned by customer
     */
    public DiscountRuleIfc[] getPreferredCustomerDiscounts(boolean includeDealDiscounts)
    {
        ArrayList<DiscountRuleIfc> discounts = new ArrayList<DiscountRuleIfc>();
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            DiscountRuleIfc rule = i.next();
            if (rule.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER)
            {
                discounts.add(rule);
            }
        }

        if (includeDealDiscounts)
        {
            for (Iterator<DiscountRuleIfc> i = advancedPricingRules.values().iterator(); i.hasNext();)
            {
                DiscountRuleIfc rule = i.next();
                if (rule.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_CUSTOMER)
                {
                    discounts.add(rule);
                }
            }
        }

        DiscountRuleIfc[] values = new DiscountRuleIfc[discounts.size()];
        discounts.toArray(values);
        return values;
    }

    /**
     * Retrieves tax object.
     *
     * @return tax object
     */
    public TransactionTaxIfc getTransactionTax()
    {
        return transactionTax;
    }

    /**
     * Sets tax attribute.
     *
     * @param value tax
     */
    public void setTransactionTax(TransactionTaxIfc value)
    {
        transactionTax = value;
    }

    /**
     * Set tax exempt.
     *
     * @param cert tax exempt certificate identifier
     * @param reason reason code
     * @deprecated As of release 13.1 Use { @link
     *             ItemContainerProxy#setTaxExempt(String, LocalizedCodeIfc)}
     */
    public void setTaxExempt(String cert, int reason)
    {
        transactionTax.setTaxExempt(cert, reason);
        setTransactionTaxOnItems(true);
    }

    /**
     * Set tax exempt.
     *
     * @param cert tax exempt certificate identifier
     * @param reasonCode
     */
    public void setTaxExempt(String cert, LocalizedCodeIfc reasonCode)
    {
        transactionTax.setTaxExempt(cert, reasonCode);
        setTransactionTaxOnItems(true);
    }

    /**
     * Clear tax exempt.
     */
    public void clearTaxExempt()
    {
        transactionTax.clearTaxExempt();
        resetTransactionTaxOnItems();
    }

    /**
     * Override tax rate.
     *
     * @param newRate new tax rate
     * @param updateAllItemsFlag flag indicating all items should be updated
     * @param reason reasonCode
     * @deprecated as of 13.1. Use {@link overrideTaxRate(double newRate,
     *             boolean updateAllItemsFlag, LocalizedCodeIfc reason)}
     */
    public void overrideTaxRate(double newRate, boolean updateAllItemsFlag, int reason)
    {
        transactionTax.overrideTaxRate(newRate, reason);
        setTransactionTaxOnItems(updateAllItemsFlag);
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
        transactionTax.overrideTaxRate(newRate, reason);
        setTransactionTaxOnItems(updateAllItemsFlag);
    }

    /**
     * Override tax amount.
     *
     * @param newAmount new tax amount
     * @param updateAllItemsFlag flag indicating all items should be updated
     * @param reason reasonCode
     * @deprecated as of 13.1. Use {@link overrideTaxAmount(CurrencyIfc
     *             newAmount, boolean updateAllItemsFlag, LocalizedCodeIfc
     *             reason)}
     */
    public void overrideTaxAmount(CurrencyIfc newAmount, boolean updateAllItemsFlag, int reason)
    {
        transactionTax.overrideTaxAmount(newAmount, reason);
        setTransactionTaxOnItems(updateAllItemsFlag);
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
        transactionTax.overrideTaxAmount(newAmount, reason);
        setTransactionTaxOnItems(updateAllItemsFlag);
    }

    /**
     * Clear tax override.
     *
     * @param updateAllItemsFlag flag indicating all items should be updated
     */
    public void clearTaxOverride(boolean updateAllItemsFlag)
    {
        transactionTax.clearOverrideTax();
        setTransactionTaxOnItems(updateAllItemsFlag);
    }

    /**
     * Set item tax objects based on transaction tax object's value.
     *
     * @param updateAllItemsFlag flag indicating all items should be updated
     */
    public void setTransactionTaxOnItems(boolean updateAllItemsFlag)
    {
        // enumerate line items
        Enumeration<AbstractTransactionLineItemIfc> e = lineItemsVector.elements();
        AbstractTransactionLineItemIfc li = null;
        ItemTaxIfc it = null;
        CurrencyIfc zero = ZERO;// DomainGateway.getBaseCurrencyInstance();

        while (e.hasMoreElements())
        {
            li = e.nextElement();

            // We use SaleReturnLineItem, here, rather than an Ifc
            // because we're making a distinction between sales and returns
            // which currently applies only to SaleReturnLineItem
            if (li instanceof SaleReturnLineItem)
            {
                SaleReturnLineItem srli = (SaleReturnLineItem)li;
                int refId = srli.getPriceAdjustmentReference();
                PriceAdjustmentLineItemIfc pali = retrievePriceAdjustmentByReference(refId);
                it = srli.getItemTax();

                // tax exempt overwrites all
                if ((transactionTax.getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
                        && srli.isTaxExemptionEligible())
                {
                    it.setOverrideRate(0);
                    it.setOverrideAmount(zero);
                    it.getReason().setCode(CodeConstantsIfc.CODE_UNDEFINED);
                    it.setTaxScope(TaxIfc.TAX_SCOPE_TRANSACTION);
                    srli.clearTaxAmounts();
                    it.setOriginalTaxMode(it.getTaxMode());
                    // Tax exempt will override any Send Tax Rules
                    it.setSendTaxRules(null);
                    it.setTaxMode(TaxIfc.TAX_MODE_EXEMPT);
                }
                // check if update-all-items flag or
                // mode gives OK to overwrite
                else if ((updateAllItemsFlag || it.getTaxMode() == TaxIfc.TAX_MODE_STANDARD)
                        && (it.getTaxMode() != TaxIfc.TAX_MODE_NON_TAXABLE)
                        && (it.getTaxMode() != TaxIfc.TAX_MODE_TOGGLE_OFF)
                        && (srli.isTaxOverrideEligible()))
                {
                    it.setTaxToggle(true);
                    it.setOverrideRate(transactionTax.getOverrideRate());
                    it.setOverrideAmount(transactionTax.getOverrideAmount());
                    it.getReason().setCode(CodeConstantsIfc.CODE_UNDEFINED);
                    it.getReason().setText(DomainGateway.getFactory().getLocalizedText());
                    it.setTaxScope(TaxIfc.TAX_SCOPE_TRANSACTION);
                    it.setOriginalTaxMode(it.getTaxMode());
                    it.setTaxMode(transactionTax.getTaxMode());
                }
                
                if (pali != null)
                {
                    pali.setPriceAdjustSaleItem(srli);
                }
            }
        }
    }

    /**
     *  Reset the item tax mode to the original mode when removing a tax exempt tender.
     *  
     *  @since 14.1
     */
    protected void resetTransactionTaxOnItems()
    {
        Enumeration<AbstractTransactionLineItemIfc> e = lineItemsVector.elements();
        AbstractTransactionLineItemIfc li = null;
        ItemTaxIfc it = null;
        while (e.hasMoreElements())
        {
            li = e.nextElement();
            if (li instanceof SaleReturnLineItem)
            {
                SaleReturnLineItem srli = (SaleReturnLineItem)li;
                // transaction tax stuff applies only to sale items
                if (srli.isSaleLineItem() || srli.isReturnLineItem())
                {
                    it = srli.getItemTax();
                    // restore original tax mode which is saved for a tax exempt tender.
                    it.setTaxMode(it.getOriginalTaxMode());
                }    
            }
        }    
    }    
    
    /**
     * Constructs item tax object based on transaction values.
     *
     * @param rate default tax rate
     * @param taxable item taxable status
     * @return new ItemTax object
     */
    public ItemTaxIfc initializeItemTax(double rate, boolean taxable)
    {
        return initializeItemTax(rate, null, taxable);
    }

    /**
     * Constructs item tax object based on transaction values.
     *
     * @param rate default tax rate
     * @param taxRules Default tax rules
     * @param taxable item taxable status
     * @return new ItemTax object
     */
    public ItemTaxIfc initializeItemTax(double rate, TaxRuleIfc[] taxRules, boolean taxable)
    {
        // construct item tax object based on transaction values
        ItemTaxIfc it = DomainGateway.getFactory().getItemTaxInstance();
        it.setDefaultRate(rate);
        it.setDefaultTaxRules(taxRules);
        it.getReason().setCode(CodeConstantsIfc.CODE_UNDEFINED);
        it.setTaxScope(TaxIfc.TAX_SCOPE_TRANSACTION);
        it.setTaxToggle(taxable);
        it.setTaxMode(transactionTax.getTaxMode());
        it.setOverrideRate(transactionTax.getOverrideRate());
        it.setOverrideAmount(transactionTax.getOverrideAmount());
        it.setTaxable(taxable);
        it.setExternalTaxEnabled(transactionTax.getExternalTaxEnabled());
        return (it);
    }

    /**
     * Constructs item tax object based on return item, transaction values.
     *
     * @param rItem Rate default tax rate
     * @param taxable item taxable status
     * @return new ItemTax object
     */
    public ItemTaxIfc initializeReturnItemTax(ReturnItemIfc rItem, boolean taxable)
    {
        return initializeReturnItemTax(rItem, null, taxable);
    }

    /**
     * Constructs item tax object based on return item, transaction values.
     *
     * @param rItem Rate default tax rate
     * @param taxRules Default tax rules
     * @param taxable item taxable status
     * @return new ItemTax object
     */
    public ItemTaxIfc initializeReturnItemTax(ReturnItemIfc rItem, TaxRuleIfc[] taxRules, boolean taxable)
    {
        CurrencyIfc zero = ZERO;// DomainGateway.getBaseCurrencyInstance();
        // construct item tax object based on transaction values
        ItemTaxIfc it = DomainGateway.getFactory().getItemTaxInstance();
        it.setDefaultRate(rItem.getTaxRate());
        it.setDefaultTaxRules(taxRules);
        it.getReason().setCode(CodeConstantsIfc.CODE_UNDEFINED);
        it.setTaxScope(TaxIfc.TAX_SCOPE_ITEM);
        it.setTaxToggle(taxable);
        if (rItem.getItemTax() != null)
        {
            it.setTaxMode(rItem.getItemTax().getTaxMode());
        }
        else
        {
            it.setTaxMode(TaxIfc.TAX_MODE_RETURN_RATE);
        }
        it.setOverrideRate(0);
        it.setOverrideAmount(zero);
        it.setTaxable(taxable);
        it.setExternalTaxEnabled(transactionTax.getExternalTaxEnabled());
        return (it);
    }

    /**
     * Retrieves amount of line voids (deleted lines).
     *
     * @return amount of line voids (deleted lines)
     */
    public CurrencyIfc getAmountLineVoids()
    {
        return (amountLineVoids);
    }

    /**
     * Sets amount of line voids (deleted lines).
     *
     * @param value amount of line voids (deleted lines)
     */
    public void setAmountLineVoids(CurrencyIfc value)
    {
        amountLineVoids = value;
    }

    /**
     * Adds to amount of line voids (deleted lines).
     *
     * @param value increment amount of line voids (deleted lines)
     */
    public void addAmountLineVoids(CurrencyIfc value)
    {
        amountLineVoids = amountLineVoids.add(value);
    }

    /**
     * Retrieves units on line voids (deleted lines).
     *
     * @return units on line voids (deleted lines)
     */
    public BigDecimal getUnitsLineVoids()
    {
        return (unitsLineVoids);
    }

    /**
     * Sets units on line voids (deleted lines).
     *
     * @param value units on line voids (deleted lines)
     */
    public void setUnitsLineVoids(BigDecimal value)
    {
        unitsLineVoids = value;
    }

    /**
     * Adds to units on line voids (deleted lines).
     *
     * @param value increment units on line voids (deleted lines)
     */
    public void addUnitsLineVoids(BigDecimal value)
    {
        unitsLineVoids = unitsLineVoids.add(value);
    }

    /**
     * Indicates whether this collection contains the given object reference.
     * The comparision is reference based.
     *
     * @return true if collection contains the object reference.
     */
    public static boolean containsObjectRef(Collection<Object> collection, Object obj)
    {
        boolean returnValue = false;

        if (collection != null)
        {
            for (Iterator<Object> i = collection.iterator(); i.hasNext();)
            {
                // Check for reference. Do not use .equal in this case!!!
                if (i.next() == obj)
                {
                    returnValue = true;
                    break;
                }
            }
        }

        return returnValue;
    }

    /**
     * Indicates whether this transaction contains one or more order line items.
     *
     * @return true if line items vector contains order line item(s).
     */
    public boolean containsOrderLineItems()
    {
        boolean returnValue = false;

        if (lineItemsVector != null)
        {
            for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
            {
                if (i.next() instanceof OrderLineItemIfc)
                {
                    returnValue = true;
                    break;
                }
            }
        }

        return returnValue;
    }

    /**
     * Indicates whether this transaction contains one or more return items.
     *
     * @return true if line items vector contains return line item(s).
     */
    public boolean containsReturnLineItems()
    {
        boolean returnValue = false;

        if (lineItemsVector != null)
        {
            for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
            {
                Object o = i.next();

                if ((o instanceof SaleReturnLineItemIfc) && ((SaleReturnLineItemIfc)o).isReturnLineItem())
                {

                    returnValue = true;
                    break;
                }
            }
        }

        return returnValue;
    }

    /**
     * Validates to see if all store coupons in transaction can be applied
     * successfully to the current items
     *
     * @return true if all store coupons are applicable, false otherwise
     */
    public boolean areAllStoreCouponsApplied()
    {
        return (unappliedStoreCoupons().size() == 0);
    }

    /**
     * Validates to see if all store coupons in transaction can be applied
     * successfully to the current items
     *
     * @return a list of store coupon item IDs which were not applied successfully to the current items.
     */
    public List<String> unappliedStoreCoupons()
    {
        // list of unused coupons. Will be filled then emptied of used coupons.
        List<String> unusedCoupons = new ArrayList<String>();

        // add coupon ids that are present
        for (Iterator<AbstractTransactionLineItemIfc> itemsIter = lineItemsVector.iterator(); itemsIter.hasNext();)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)itemsIter.next();
            if (lineItem.getPLUItem().isStoreCoupon())
            {
                unusedCoupons.add(lineItem.getItemID());
            }
        }

        // check bestDealWinners. Remove coupons from unusedCoupons
        for (Iterator<BestDealGroupIfc> dealIter = getBestDealWinners().iterator(); dealIter.hasNext();)
        {
            BestDealGroupIfc group = dealIter.next();
            if (group instanceof SuperGroupIfc)
            {
                SuperGroupIfc superGroup = (SuperGroupIfc)group;
                for (Iterator<BestDealGroupIfc> groupIter = superGroup.subgroups(); groupIter.hasNext();)
                {
                    group = groupIter.next();
                    if (group.getDiscountRule().isStoreCoupon())
                    {
                        for (Iterator<DiscountSourceIfc> iterSource = group.getSources().iterator(); iterSource.hasNext();)
                        {
                            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)iterSource.next();
                            unusedCoupons.remove(lineItem.getItemID());
                        }
                    }
                }
            }
            else if (group.getDiscountRule().isStoreCoupon())
            {
                for (Iterator<DiscountSourceIfc> iterSource = group.getSources().iterator(); iterSource.hasNext();)
                {
                    SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)iterSource.next();
                    unusedCoupons.remove(lineItem.getItemID());
                }
            }
        }
        // look for coupons that trigger transactional discounts
        if (unusedCoupons.size() > 0 && transactionDiscountsVector.size() > 0)
        {
            List<String> storeCoupons = new ArrayList<String>();
            for (Iterator<TransactionDiscountStrategyIfc> discIter = transactionDiscountsVector.iterator(); discIter.hasNext();)
            {
                DiscountRuleIfc transDisc = discIter.next();
                String transRuleID = transDisc.getRuleID();
                // loop through lineitems of unusedCoupons and see if they
                // are a source for this ruleID

                for (Iterator<String> coupIter = unusedCoupons.iterator(); coupIter.hasNext();)
                {
                    String couponID = coupIter.next();
                    for (Iterator<AbstractTransactionLineItemIfc> lineIter = getLineItemsIterator(); lineIter.hasNext();)
                    {
                        SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineIter.next();
                        if (couponID.equals(lineItem.getItemID()))
                        {
                            AdvancedPricingRuleIfc[] rules = lineItem.getPLUItem().getAdvancedPricingRules();
                            for (int i = rules.length - 1; i >= 0; i--)
                            {
                                if (transRuleID.equals(rules[i].getRuleID()))
                                {
                                    SaleReturnLineItemIfc srli = null;
                                    for (Iterator<AbstractTransactionLineItemIfc> itemsIter = lineItemsVector.iterator(); itemsIter.hasNext();)
                                    {
                                        srli = (SaleReturnLineItemIfc)itemsIter.next();
                                        if (srli.isDiscountEligible())
                                        {
                                            storeCoupons.add(couponID);
                                        }

                                    }
                                }// end if (transRuleID.equals(rules[i].getRuleID()))

                            }// end for (int i = rules.length - 1; i >= 0; i--)
                        }// end if (couponID.equals(lineItem.getItemID()))
                    }// end for (Iterator lineIter = getLineItemsIterator(); lineIter.hasNext();)
                }// end for (Iterator coupIter = unusedCoupons.iterator(); coupIter.hasNext();)
            }// end for (Iterator discIter = transactionDiscountsVector.iterator(); discIter.hasNext();)
            unusedCoupons.removeAll(storeCoupons);
        }
        return unusedCoupons;
    }

    /**
     * Tests whether this collection has discount eligible items.
     *
     * @return boolean
     */
    public boolean hasDiscountableItems()
    {
        return countDiscountableItems() > 0;
    }

    /**
     * Counts discountable items.
     *
     * @return discountable items
     */
    public int countDiscountableItems()
    {
        Iterator<AbstractTransactionLineItemIfc> itemsIter = lineItemsVector.iterator();
        SaleReturnLineItemIfc srli = null;
        int discountableItems = 0;

        while (itemsIter.hasNext())
        {
            srli = (SaleReturnLineItemIfc)itemsIter.next();

            if (DiscountUtility.isAnyDiscountEligible(srli))
            {
                discountableItems++;
            }
        }
        return discountableItems;
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
        boolean value = false;
        Object o = null;

        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            o = i.next();

            if (!(o instanceof CustomerDiscountByPercentageIfc))
            {
                value = true;
                break;
            }
        }
        return value;
    }

    /**
     * Tests whether any of the items in this container has a discount amount
     * greater than its selling price.
     *
     * @return boolean
     */
    public boolean itemsDiscountExceedsSellingPrice()
    {
        boolean exceeds = false;
        SaleReturnLineItemIfc item = null;
        Iterator<AbstractTransactionLineItemIfc> itemIter = getLineItemsIterator();

        while (itemIter.hasNext())
        {
            item = (SaleReturnLineItemIfc)itemIter.next();
            if (item.discountExceedsSellingPrice())
            {
                exceeds = true;
                break;
            }
        }
        return exceeds;
    }

    /**
     * Tests whether any of the items in this container has a tax amount greater
     * than its selling price.
     *
     * @return boolean
     */
    public boolean itemsTaxExceedsSellingPrice()
    {
        boolean exceeds = false;
        SaleReturnLineItemIfc item = null;
        Iterator<AbstractTransactionLineItemIfc> itemIter = getLineItemsIterator();

        while (itemIter.hasNext())
        {
            item = (SaleReturnLineItemIfc)itemIter.next();
            if (item.taxExceedsSellingPrice())
            {
                exceeds = true;
                break;
            }
        }

        return exceeds;
    }

    /**
     * Method to determine if any of the items in the transaction contains a
     * specific serial number.
     *
     * @param serialNumber
     * @return boolean - true if serial number found
     */
    public boolean containsSerialNumber(String serialNumber)
    {
        boolean contains = false;
        SaleReturnLineItemIfc item = null;
        Iterator<AbstractTransactionLineItemIfc> itemIter = getLineItemsIterator();

        while (itemIter.hasNext())
        {
            item = (SaleReturnLineItemIfc)itemIter.next();
            if (item.getItemSerial() != null && item.getItemSerial().equals(serialNumber))
            {
                contains = true;
                break;
            }
        }

        return contains;
    }

    /**
     * Method to default display string function.
     *
     * @return String representation of object
     */
    public String toString()
    {
      // result string
        StringBuilder strResult = Util.classToStringHeader("ItemContainerProxy", getRevisionNumber(), hashCode());
        strResult.append(Util.formatToStringEntry("Transaction tax", transactionTax.toString()))
                .append(Util.formatToStringEntry("Transaction discounts", getTransactionDiscounts()))
                .append(Util.formatToStringEntry("Line items", getLineItems()))
                .append(Util.formatToStringEntry("Advanced pricing rules", getAdvancedPricingRules()))
                .append(Util.formatToStringEntry("Best deal groups", bestDealGroups))
                .append(Util.formatToStringEntry("Amount line voids", amountLineVoids.toString()))
                .append(Util.formatToStringEntry("Units line voids", unitsLineVoids.toString()));
        // pass back result
        return (strResult.toString());
    }

    /**
     * Write journal header to specified string buffer.
     *
     * @param cashier EmployeeIfc reference
     * @return journal fragment string
     * @deprecated as of 13.1. New method added to take the journal locale
     */
    public String journalHeader(EmployeeIfc cashier)
    {
        StringBuffer strResult = new StringBuffer();

        EmployeeIfc employee = getSalesAssociate();
        if (employee != null)
        {
            String emp = employee.getEmployeeID();
            int len = emp.length() + cashier.getEmployeeID().length();
            strResult.append("                        ".substring(len)).append("Sales: ").append(emp);
        }
        // pass back result
        return (strResult.toString());
    }

    /**
     * Write journal header to specified string buffer.
     *
     * @param cashier EmployeeIfc reference
     * @param journalLocale Locale received from the client
     * @return journal fragment string
     */
    public String journalHeader(EmployeeIfc cashier, Locale journalLocale)
    {
        StringBuffer strResult = new StringBuffer();

        EmployeeIfc employee = getSalesAssociate();
        if (employee != null)
        {
            String emp = employee.getEmployeeID();
            Object[] dataArgs = new Object[] { emp };
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SALES_LABEL, dataArgs,
                            journalLocale));
        }
        // pass back result
        return (strResult.toString());
    }

    /**
     * Write transaction modifiers to journal string.
     *
     * @param customer CustomerIfc reference
     * @return journal fragment string
     * @deprecated as of 13.1. new method added which takes journal locale
     */
    public String journalTransactionModifiers(CustomerIfc customer)
    {
        return (journalTransactionModifiers(customer, LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));
    }

    /**
     * Write transaction modifiers to journal string.
     *
     * @param customer CustomerIfc reference
     * @return journal fragment string
     */
    public String journalTransactionModifiers(CustomerIfc customer, Locale journalLocale)
    {
        StringBuffer strResult = new StringBuffer();
        if (customer != null && customer.getCustomerID() != null)
        {
            Object[] dataArgs = new Object[] { customer.getCustomerID().trim() };
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.CUSTOMER_TAG_LABEL,
                            dataArgs, journalLocale));
            strResult.append(Util.EOL);
        }

        // journal discounts as needed
        journalDiscounts(strResult, journalLocale);

        // journal transaction tax, if not standard
        if (getTransactionTax().getTaxMode() != TaxIfc.TAX_MODE_STANDARD)
        {
            strResult.append(getTransactionTax().toJournalString(journalLocale));

        }

        // journal gift registry
        if (getDefaultRegistry() != null)
        {
            strResult.append(Util.EOL).append(
                 I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,JournalConstantsIfc.TRANS_GIFT_REG_LABEL,
                        null, journalLocale)).append(Util.EOL);
            Object[] dataArgs = new Object[] { getDefaultRegistry().getID() };
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REG_LABEL,
                    dataArgs, journalLocale));
        }

        return (strResult.toString());
    }

    /**
     * Write line items to journal string.
     *
     * @result journal fragment string
     * @deprecated as of 13.1. new method added to take the client's journal
     *             locale.
     */
    public String journalLineItems()
    {

        return journalLineItems(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));
    }

    /**
     * Write line items to journal string.
     *
     * @param journalLocale locale received from the client
     * @result journal fragment string
     */
    public String journalLineItems(Locale journalLocale)
    {
        StringBuffer strResult = new StringBuffer(Util.EOL);
        SaleReturnLineItemIfc srli = null;

        // KitComponentLineItems are journaled by the call to their
        // parent KitHeaderLineItem so exclude them here
        SaleReturnLineItemIfc[] items = getLineItemsExcluding(ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT);

        String saTrans = (getSalesAssociate() == null) ? "" : getSalesAssociate().getEmployeeID();

        for (int i = 0; i < items.length; i++)
        {
            strResult.append(Util.EOL);
            srli = items[i];
            if (srli.getSalesAssociate() != null)
            {
                String saItem = srli.getSalesAssociate().getEmployeeID();
                strResult.append(srli.toJournalString(journalLocale));
                ItemPriceIfc ipifc = srli.getItemPrice();
                if (ipifc.getItemDiscounts() != null && ipifc.getItemDiscounts().length > 0)
                {
                    ItemDiscountStrategyIfc[] discounts = ipifc.getItemDiscounts();
                    for (int j = 0; j < discounts.length; ++j)
                    {
                        if (!(discounts[j] instanceof ItemTransactionDiscountAudit)
                                && !(discounts[j].isAdvancedPricingRule()))
                        {
                            strResult.append(Util.EOL).append(
                                    srli.toJournalManualDiscount(discounts[j], false, journalLocale));
                        }
                    }
                }

                if (!(saTrans.equals(saItem)))
                {
                    strResult.append(Util.EOL).append(Util.EOL);
                    Object[] dataArgs = new Object[] { srli.getItemID() };
                    strResult.append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LABEL,
                                    dataArgs, journalLocale)).append(Util.EOL);
                    dataArgs[0] = saItem;
                    strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.SALES_ASSOC_LABEL, dataArgs, journalLocale));
                }
            }
        }

        return strResult.toString();
    }

    /**
     * Journal discounts.
     *
     * @param journalString journal string buffer
     * @deprecated as of 13.1. New method which takes journal locale has been
     *             added.
     */
    public void journalDiscounts(StringBuffer journalString)
    {
        TransactionDiscountStrategyIfc[] discounts = getTransactionDiscounts();
        if (discounts != null && discounts.length > 0)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                journalString.append(discounts[i].toJournalString());
            }
        }
    }

    /**
     * Journal discounts.
     *
     * @param journalString journal string buffer
     * @param journalLocale Locale received from the client
     */
    public void journalDiscounts(StringBuffer journalString, Locale journalLocale)
    {
        TransactionDiscountStrategyIfc[] discounts = getTransactionDiscounts();
        if (discounts != null && discounts.length > 0)
        {
            for (int i = 0; i < discounts.length; i++)
            {
                journalString.append(discounts[i].toJournalString(journalLocale));
            }
        }
    }

    /**
     * add items to the hashtable by tax group.
     *
     * @deprecated  since 14.0 no replacement.
     */
    @Deprecated
    public void addItemByTaxGroup()
    {

        // Since the tax group - item paired hashtable will be
        // refreshed together with ShowSaleScreen, need a temp
        // hashtable to hold the previous tax rule data.

        // Refresh the hashtable.
        itemsByTaxGroup.clear();

        for (Iterator<AbstractTransactionLineItemIfc> it = this.getLineItemsIterator(); it.hasNext();)
        {
            SaleReturnLineItemIfc item = null;
            item = (SaleReturnLineItemIfc)it.next();

            // Exclude the return items with receipt because their tax rules
            // data have been retrieved.
            if (!item.isReturnLineItem()
                    || (item.isReturnLineItem() && !item.getReturnItem().isFromRetrievedTransaction()))
            {
                // For an existing tax group, retrieve its item vector from
                // the hashtable, add the new item into the vector and put back
                // to
                // the hashtable.

                if (itemsByTaxGroup.containsKey(String.valueOf(item.getTaxGroupID())))
                {
                    Vector<SaleReturnLineItemIfc> v = new Vector<SaleReturnLineItemIfc>(0);
                    if (itemsByTaxGroup.containsKey(String.valueOf(item.getTaxGroupID())))
                    {
                        v = itemsByTaxGroup.get(String.valueOf(item.getTaxGroupID()));
                    }

                    // Because some items are displayed as a single line item
                    // even it has quantity greater than 1,
                    // need to using quantity attribute to store the correct
                    // number of the items in the vector in
                    // the hashtable for tax calculation.

                    for (int j = 0; j < item.getItemQuantityDecimal().abs().intValue(); j++)
                    {
                        v.addElement(item);
                    }
                    // Update the Tax Group and item vector pair in the
                    // hashtable.
                    itemsByTaxGroup.remove(String.valueOf(item.getTaxGroupID()));
                    itemsByTaxGroup.put(String.valueOf(item.getTaxGroupID()), v);

                }
                // When a brand new tax group which is not in the hashtable yet,
                // add it into the hashtable.
                if (!itemsByTaxGroup.containsKey(String.valueOf(item.getTaxGroupID())))
                {
                    // Add a new tax group and its first item in the hashtable
                    Vector<SaleReturnLineItemIfc> v2 = new Vector<SaleReturnLineItemIfc>(0);
                    v2.addElement(item);
                    itemsByTaxGroup.put(String.valueOf(item.getTaxGroupID()), v2);

                }
            } // End of return item checking.
        }
    }

    /**
     * Remove an item from the tax group - item paired hashtable.
     *
     * @deprecated  since 14.0 no replacement.
     *
     * @param item SaleReturnLineItemIfc
     */
    @Deprecated
    public void removeItemByTaxGroup(SaleReturnLineItemIfc item)
    {
        if (itemsByTaxGroup.containsKey(String.valueOf(item.getTaxGroupID())))
        {
            // For an existing tax group, retrieve its item vector from
            // the hashtable, remove the item from the vector and put back to
            // the hashtable.
            Vector<SaleReturnLineItemIfc> v = itemsByTaxGroup.get(String.valueOf(item.getTaxGroupID()));

            // If there is only one line item for a specific tax group. erase it
            // from the hashtable.
            if ((v.size() == 1) && (v.size() != 0))
            {
                itemsByTaxGroup.remove(String.valueOf(item.getTaxGroupID()));
            }
            if (v.size() > 1)
            // Locate the position of the to be deleted line items, and erase
            // the correct number of them.
            {
                for (int i = 0; i < v.size(); i++)
                {
                    if (v.elementAt(i).getLineNumber() == item.getLineNumber())
                    {
                        // for (int j=0;
                        // j<item.getItemQuantityDecimal().intValue(); j++)
                        // {
                        v.remove(i);
                        // }
                        break;
                    }
                }

                itemsByTaxGroup.remove(String.valueOf(item.getTaxGroupID()));
                itemsByTaxGroup.put(String.valueOf(item.getTaxGroupID()), v);
            }
        }
        else
        {
            // Do nothing
        }
    }

    /**
     * Update an item in the tax group - item paired hashtable. This method will
     * be used during tax override.
     *
     * @deprecated since 14.0. No replacement.
     *
     * @param item SaleReturnLineItemIfc
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public void updateItemByTaxGroup(SaleReturnLineItemIfc item)
    {
        if (itemsByTaxGroup.containsKey(String.valueOf(item.getTaxGroupID())))
        {
            Vector<SaleReturnLineItemIfc> vNew = new Vector<SaleReturnLineItemIfc>(0);
            SaleReturnLineItemIfc srli = null;

            // For an existing tax group, retrieve its item vector from
            // the hashtable, modify the item in the vector and put back to
            // the hashtable.
            Vector v = itemsByTaxGroup.get(String.valueOf(item.getTaxGroupID()));

            // If there is only one line item for a specific tax group. erase it
            // from the hashtable.
            if (v.size() > 0)
            {
                for (int i = 0; i < v.size(); i++)
                {
                    if (((SaleReturnLineItemIfc)v.elementAt(i)) != null)
                    {
                        srli = (SaleReturnLineItemIfc)v.elementAt(i);
                        if ((LocaleUtilities.compareValues(srli.getItemID(), item.getItemID()) == 0)
                                && (srli.getLineNumber() == item.getLineNumber()))
                        {
                            srli = item;
                        }
                    }
                    vNew.addElement(srli);
                }
            }
            itemsByTaxGroup.remove(String.valueOf(item.getTaxGroupID()));
            itemsByTaxGroup.put(String.valueOf(item.getTaxGroupID()), vNew);

        }
        else
        {
            // Do nothing
        }
    }

    /**
     * Get the items by a tax group.
     *
     * @deprecated  since 14.0 no replacement.
     *
     * @return hashtable
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public Hashtable getItemsByTaxGroup()
    {
        return itemsByTaxGroup;
    }

    /**
     * Set the items by a tax group hashtable.
     *
     * @deprecated  since 14.0 no replacement.
     *
     * @param ht hashtable
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Deprecated
    public void setItemsByTaxGroup(Hashtable ht)
    {
        this.itemsByTaxGroup = ht;
    }

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean equal = false;

        // If it's a ItemContainerProxy, compare its attributes
        if (obj instanceof ItemContainerProxy)
        {
            // downcast the input object
            ItemContainerProxy c = (ItemContainerProxy)obj;
            if (!Util.isObjectEqual(lineItemsVector, c.lineItemsVector))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(transactionDiscountsVector, c.transactionDiscountsVector))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(salesAssociate, c.salesAssociate))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(defaultRegistry, c.defaultRegistry))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(transactionTax, c.transactionTax))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(amountLineVoids, c.amountLineVoids))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(unitsLineVoids, c.unitsLineVoids))
            {
                equal = false;
            }
            else if (!Util.isObjectEqual(customer, c.customer))
            {
                equal = false;
            }
        }

        return (equal);
    }

    /**
     * Calculate the total selling price of an arbitrary list of items.
     *
     * @param targets list of items
     * @return CurrencyIfc
     */
    public static CurrencyIfc calculateTotalSellingPrice(List<DiscountTargetIfc> targets)
    {
        Iterator<DiscountTargetIfc> targetsIterator = targets.iterator();
        CurrencyIfc totalPrice = DomainGateway.getBaseCurrencyInstance();

        while (targetsIterator.hasNext())
        {
            DiscountTargetIfc target = targetsIterator.next();
            totalPrice = totalPrice.add(target.getExtendedSellingPrice());
        }

        return totalPrice;
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Add Promotion Line Item if there is an effective promotion on this item
     * that is priced less that the permanent price of the item.
     *
     * @param srli
     * @deprecated as of 13.3. Temporary price changes are always recorded as
     *             promotion line items and not just ones from RMS.
     * @see #revaluateLineItemPrice(SaleReturnLineItemIfc)
     * @see ItemPriceIfc#setAppliedPromotion(PriceChangeIfc)
     */
    protected void addPromotionLineItemToTransaction()
    {
        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)i.next();
            if (!srli.hasExternalPricing())
            {
                addPromotionLineItem(srli);
            }
        }
    }

    /**
     * Add Promotion Line Item if there is an effective promotion on this item
     * that is priced less that the permanent price of the item.
     *
     * @param srli
     * @deprecated as of 13.3. Temporary price changes are always recorded as
     *             promotion line items and not just ones from RMS.
     * @see #revaluateLineItemPrice(SaleReturnLineItemIfc)
     * @see ItemPriceIfc#setAppliedPromotion(PriceChangeIfc)
     */
    protected void addPromotionLineItem(SaleReturnLineItemIfc srli)
    {
        EYSDate now = new EYSDate();
        PLUItemIfc pluItem = srli.getPLUItem();
        PriceChangeIfc priceChange = null;
        int prcGrpId = -1;
        if (customer != null)
        {
            if (customer.getPricingGroupID() != null)
            {
                prcGrpId = customer.getPricingGroupID();
            }
            priceChange = pluItem.getEffectiveTemporaryPriceChange(now, prcGrpId);
        }
        else
        {
            priceChange = pluItem.getEffectiveTemporaryPriceChange(now);
        }
        if (priceChange != null && priceChange.getPromotionId() != 0)
        {
            CurrencyIfc retailPrice = priceChange.getNewPrice();
            CurrencyIfc permanentPrice = pluItem.getPermanentPrice(now);

            if (permanentPrice.compareTo(retailPrice) == CurrencyIfc.GREATER_THAN)
            {
                PromotionLineItemIfc[] promotionLineItems = srli.getPromotionLineItems();
                if (promotionLineItems != null)
                {
                    for (int i = 0; i < promotionLineItems.length; i++)
                    {
                        if (promotionLineItems[i].isTypePriceChange())
                        {
                            srli.getItemPrice().removePromotionLineItem(promotionLineItems[i]);
                        }
                    }
                }
                CurrencyIfc discountAmount = permanentPrice.subtract(retailPrice);
                PromotionLineItemIfc promotionLineItem = DomainGateway.getFactory().getPromotionLineItemInstance();
                promotionLineItem.setPromotionId(priceChange.getPromotionId());
                promotionLineItem.setPromotionComponentId(priceChange.getPromotionComponentId());
                promotionLineItem.setPromotionComponentDetailId(priceChange.getPromotionComponentDetailId());
                promotionLineItem.setDiscountAmount(discountAmount);
                promotionLineItem.setPricingGroupID(priceChange.getPricingGroupID());
                promotionLineItem.setPromotionType(PromotionLineItem.PROMOTION_TYPE_PRICE_CHANGE);
                srli.getItemPrice().addPromotionLineItem(promotionLineItem);
            }
        }
    }

    /**
     * constructs the customer speicific advanced pricing rule in the
     * transaction.
     */
    public void constructCSPAdvancedPricingRuleForTransaction()
    {
        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)i.next();
            if (!srli.hasExternalPricing() && (!srli.isPickupCancelLineItem() || srli.isInStorePriceDuringPickup()))
            {
                constructCSPAdvancedPricingRule(srli.getPLUItem());
            }
        }
    }

    /**
     * constructs the customer speicific advanced pricing rule in the
     * transaction.
     *
     * @param pluItem contains the rule for customer specific pricing
     */
    public void constructCSPAdvancedPricingRule(PLUItemIfc pluItem)
    {
        AdvancedPricingRuleIfc[] rules = pluItem.getAdvancedPricingRules();
        int prcGrpId = -1;
        if (customer.getPricingGroupID() != null)
        {
            prcGrpId = customer.getPricingGroupID();
        }
        if (rules != null)
        {
            for (int i = 0; i < rules.length; i++)
            {
                AdvancedPricingRuleIfc rule = rules[i];
                if (rule.getPricingGroupID() == prcGrpId)
                {
                    if (!rule.isThresholdDiscountRule())
                    {
                        addAdvancedPricingRule(rule);
                    }
                    else
                    {
                        createThresholdAdvancedPricingRules(rule);
                    }
                    cspAdvancedPricingRules.put(rule.getRuleID(), rule);
                }
            }
        }
    }

    /**
     * clears all the advanced pricing rule.
     */
    public void clearCSPAdvancedPricingRule()
    {
        for (Iterator<DiscountRuleIfc> itr = cspAdvancedPricingRules.values().iterator(); itr.hasNext();)
        {
            AdvancedPricingRuleIfc rule = (AdvancedPricingRuleIfc)itr.next();
            String ruleId = rule.getRuleID();
            // remove customer specific discount rule for a particular item if
            // deleted from sale screen
            if (advancedPricingRules.containsKey(ruleId) && rule.getPricingGroupID() != -1)
            {
                advancedPricingRules.remove(ruleId);
            }
        }
        cspAdvancedPricingRules.clear();
    }

    /**
     * clears all the advanced pricing rule.
     */
    public void addCSPAdvancedPricingRule(AdvancedPricingRuleIfc pricingRule)
    {
        cspAdvancedPricingRules.put(pricingRule.getRuleID(), pricingRule);
    }

    /**
     * Get external order sale return line items
     *
     * @param externalOrderItemIDs the external order item IDs
     * @return the list of line items
     */
    public List<SaleReturnLineItemIfc> getExternalOrderLineItems(List<String> externalOrderItemIDs)
    {
        HashSet<String> externalOrderIDsSet = new HashSet<String>(externalOrderItemIDs);
        AbstractTransactionLineItemIfc[] lineItems = getLineItems();
        ArrayList<SaleReturnLineItemIfc> externalOrderLineItems = new ArrayList<SaleReturnLineItemIfc>();
        for (AbstractTransactionLineItemIfc lineItem : lineItems)
        {
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc saleReturnLineItem = (SaleReturnLineItemIfc)lineItem;
                if (saleReturnLineItem.isFromExternalOrder())
                {
                    String externalOrderItemID = saleReturnLineItem.getExternalOrderItemID();
                    if (externalOrderIDsSet.contains(externalOrderItemID))
                    {
                        externalOrderLineItems.add(saleReturnLineItem);
                    }
                }
            }
        }

        return externalOrderLineItems;
    }

    /**
     * Checks if the transaction Discount already exists in the transaction
     * discount vector. If not, it adds it.
     *
     * @param rule
     */
    protected void checkAddTransactionDiscount(AdvancedPricingRuleIfc rule)
    {
        // see if the rule is already in the transaction discounts vector
        boolean alreadyAdded = false;
        String ruleID = null;
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            ruleID = i.next().getRuleID();
            if (ruleID != null && ruleID.equals(rule.getRuleID()))
            {
                alreadyAdded = true;
                break;
            }
        }
        if (!alreadyAdded)
        {

            // have the rule generate a regular transaction discount
            // strategy
            // and add it to the transaction discounts bucket

               addTransactionDiscount((TransactionDiscountStrategyIfc)rule.createDiscountStrategy());

        }
    }

    /**
     * Sets threshold values from Multithreshold rule to corresponding Advanced
     * Pricing rules.
     *
     * @param rule
     * @param threshold
     */
    protected void setThresholdValuesToRule(AdvancedPricingRuleIfc rule, ThresholdIfc threshold)
    {
        if (threshold.getDiscountPercent() != null)
        {
            rule.setDiscountRate(threshold.getDiscountPercent().multiply(new BigDecimal(0.01)));
        }
        if (threshold.getDiscountAmount() != null)
        {
            CurrencyIfc discAmount = DomainGateway.getBaseCurrencyInstance();
            discAmount.setDecimalValue(threshold.getDiscountAmount());
            rule.setDiscountAmount(discAmount);
        }
        if (threshold.getNewPrice() != null)
        {
            CurrencyIfc fixPrice = DomainGateway.getBaseCurrencyInstance();
            fixPrice.setDecimalValue(threshold.getNewPrice());
            rule.setFixedPrice(fixPrice);
        }
        rule.getSourceList().setMultiThreshold(true);
    }

    /**
     * iterates through the transaction discounts vector and removes the
     * discounts which do not meet the criteria.
     */
    protected void checkTransactionDiscountVector()
    {
        clearSystemTransactionDiscounts();
        for (Iterator<TransactionDiscountStrategyIfc> i = transactionDiscountsVector.iterator(); i.hasNext();)
        {
            AdvancedPricingRuleIfc rule = i.next().getRule();
            String description = null;
            if (rule != null)
            {
                description = rule.getDescription();
            }
            if (description != null
                            && (description.equals(DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_Buy$NofXforZ$off)
                            || description.equals(DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_Buy$NofXforZPctoff)
                            || description.equals(DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNofXforZ$off)
                            || description.equals(DiscountRuleConstantsIfc.DISCOUNT_DESCRIPTION_BuyNofXforZPctoff)))
            {
                if (rule != null && !rule.isStoreCoupon())
                {
                    splitSourcesAndTargets(true);
                    if (!rule.evaluateSourcesForTransactionRules(getTransactionDiscountSources(),
                            getTransactionDiscountTargets()))
                    {
                        i.remove();
                    }
                }

            }
        }
    }

    /**
     * Clears the system applied transaction discounts from line items
     */
    private void clearSystemTransactionDiscounts()
    {
        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); i.hasNext();)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)i.next();
            ItemPriceIfc itemPrice = srli.getItemPrice();
            itemPrice.clearTransactionScopeSystemDiscounts();
            srli.calculateLineItemPrice();
        }

    }

    /**
     * Returns true if rule is associated with any other line items
     *
     * @param discountRule
     * @return
     */
    protected boolean isRuleStillUsed(DiscountRuleIfc discountRule)
    {
        boolean ruleStillUsed = false;
        for (Iterator<AbstractTransactionLineItemIfc> i = lineItemsVector.iterator(); !ruleStillUsed && i.hasNext();)
        {
            Object whatsit = i.next();
            if (whatsit instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)whatsit;
                for (Iterator<AdvancedPricingRuleIfc> iter = item.getPLUItem().advancedPricingRules(); !ruleStillUsed && iter.hasNext();)
                {
                    whatsit = iter.next();
                    if (whatsit instanceof DiscountRuleIfc)
                    {
                        DiscountRuleIfc otherRule = (DiscountRuleIfc)whatsit;
                        if (otherRule != null && otherRule.getRuleID().equals(discountRule.getRuleID()))
                        {
                            ruleStillUsed = true;
                        }
                    }
                }
            }
        }
        return ruleStillUsed;
    }

    /**
     * This method performs the casting and checks that are required to
     * determine if a line item is a return and originates from a retrieved
     * transaction.
     *
     * @param o Object
     * @return true if the object is a line item is a return and originates from
     *         a retrieved transaction.
     */
    protected boolean isRetrievedReturnLineItem(AbstractTransactionLineItemIfc o)
    {
        boolean isReceipted = false;

        if (o instanceof SaleReturnLineItemIfc)
        {
            isReceipted = isRetrievedReturnLineItem((SaleReturnLineItemIfc)o);
        }

        return isReceipted;
    }

    /**
     * This method determines if a line item is a return and originates from a
     * retrieved transaction.
     *
     * @param lineItem AbstractTransactionLineItemIfc
     * @return true if the object is a line item is a return and originates from
     *         a retrieved transaction.
     */
    protected boolean isRetrievedReturnLineItem(SaleReturnLineItemIfc lineItem)
    {
        if (lineItem.isReturnLineItem() && lineItem.isFromTransaction())
        {
            return true;
        }

        return false;
    }
}
