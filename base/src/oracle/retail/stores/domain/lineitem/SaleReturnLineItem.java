/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/lineitem/SaleReturnLineItem.java /main/115 2014/07/24 15:23:29 sgu Exp $
 * ===========================================================================
 * NOTES <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *   sgu    11/19/14 - add negateItemQuantity function for post voiding case
 *   yiqzhao 11/14/14 - Use isNonRetrievedReceiptedItem for mpos and pos.
 *   crain   10/13/14 - Bug 19768618:  handle return tax when the return item
 *                      gets the default tax rules applied
 *   yiqzhao 10/09/14 - avoid undefined status in orignalOrderLineItemStatus.
 *   amishash08/10/14 - forward port 19693762. Added condition to check 
 *                      discount eligibility in case for all non coupon items.
 *   cgreen  08/05/14 - remove deprecated methods
 *   sgu     07/23/14 - add tax authority name
 *   sgu     07/11/14 - change tax status description to retrieve from
 *                      TaxInformationContainer
 *   sgu     07/11/14 - disallow tax exemption for receipted return and order
 *                      pickup/cancel item
 *   yiqzhao 06/30/14 - Add isPriceCancelledDuringPickup.
 *   yiqzhao 06/30/14 - Add isInStorePriceDuringPickup check for re-price.
 *   sgu     06/20/14 - disable transactional discount and tax override for
 *                      pickup cancel order line item
 *   yiqzhao 06/20/14 - Add depositAmount and delete getDepositApplied().
 *   sgu     06/15/14 - fix transaction total and discount calculation for
 *                      order pickup transaction
 *   yiqzhao 03/07/14 - Add isOrderPickedUpItem method for order transactions
 *                      which have take with items.
 *   sgu     03/06/14 - add logic to retrieve transaction discount and tax for
 *                      CSC order
 *   abhinav 01/27/14 - Fix to provide I18n support for item footer messages
 *   swbhask 01/23/14 - removed extra orderId in OrderPickup receipt modified
 *                      getPrintedTransactionID
 *   tksharm 11/22/13 - changed getPrintedTransactionId method for return item
 *                      to print transaction id only when non retrieved
 *                      transaction id is null
 *   tksharm 11/14/13 - removed the restriction of priceoverriden items
 *                      participating in discount rules
 *   abhinav 08/13/13 - Fix to display localized item messages on a receipt
 *   arabala 07/24/13 - Set the 'priceoverride' flag after item is price
 *                      overridden and these priceoverridden items are
 *                      restricted being added to discount rules
 *   mchella 04/24/13 - Added method getPrintedTransactionID
 *   yiqzhao 04/17/13 - Create attribute selectedForItemSplit flag for order
 *                      pickup and shipping.
 *   jswan   04/12/13 - Modified to prevent the update of item sales totals for
 *                      order line items.
 *   rgour   04/01/13 - CBR cleanup
 *   tksharm 03/25/13 - added method isSourceAvailableForTransactionDiscounts
 *   rabhaws 03/20/13 - gift certificates should not have discounted price in
 *                      retail store history table.
 *   sgu     02/12/13 - display prorated deposit amount for partial pickup or
 *                      cancel item
 *   tksharm 01/31/13 - added clearance Price change entry to retail price
 *                      modifier table
 *   sgu     12/18/12 - calculate prorated order item tax using original order
 *                      item status
 *   sgu     12/14/12 - remove get/set order id to sale return line item
 *   sgu     12/10/12 - prorate discount for order pickup and return
 *   tksharm 12/10/12 - commons-lang update 3.1
 *   sgu     11/07/12 - added captured order line item
 *   sgu     10/16/12 - only prorate item if needed
 *   sgu     10/09/12 - use new createOrderTransaction from UI flow
 *   sgu     09/06/12 - set transaction tax override reason code to item level
 *   sthalla 05/30/12 - Enhanced RPM Integration - Clearance Pricing
 *   jswan   05/14/12 - Modified to fix issue with split of multi-quantity line
 *                      items.
 *   jswan   05/07/12 - Modified to support cross channel order item
 *                      applicaiton flow changes.
 *   mjwalla 05/04/12 - Fortify: fix redundant null checks, part 5
 *   yiqzhao 05/03/12 - set shipping charge to totals
 *   yiqzhao 04/26/12 - handle shipping charge as sale return line item
 *   yiqzhao 04/03/12 - refactor store send for cross channel
 *   rsnayak 03/22/12 - cross border return changes
 *   jswan   01/05/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *   cgreene 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *   sgu     10/21/11 - negate return tax correctly
 *   mchella 10/25/11 - Set permanent price for items without permanent price
 *   mchella 10/14/11 - Gift card RTLog issue rework
 *   rsnayak 10/10/11 - Pos log Item id fix
 *   mchella 09/30/11 - Fixed Giftcard RTLog issue related to discounts
 *   sgu     09/22/11 - negate return tax in post void case
 *   jswan   08/22/11 - Fixes issues with gift card totals.
 *   jswan   08/17/11 - Modified to prevent the return of Gift Cards as items
 *                      and part of a transaction. Also cleaned up references
 *                      to gift cards objects in the return tours.
 *   blarsen 07/15/11 - Fix misspelled word: retrival
 *   cgreene 07/07/11 - convert entryMethod to an enum
 *   blarsen 01/07/11 - XbranchMerge
 *                      blarsen_bug10624300-discount-flag-change-side-effects2
 *                      from rgbustores_13.3x_generic_branch
 *   blarsen 01/07/11 - XbranchMerge
 *                      blarsen_bug10624300-discount-flag-change-side-effects
 *                      from rgbustores_13.3x_generic_branch
 *   blarsen 01/06/11 - Changed get discount eligible methods to simply return
 *                      the plu item discount flag. Methods requireing the more
 *                      restrictive eligiblity checks should use the methods in
 *                      DiscountUtility. The values returned from
 *                      DiscountUtility should not be saved to the database
 *                      (this change fixes this problem).
 *   blarsen 12/23/10 - XbranchMerge
 *                      blarsen_bug10396003-item-discount-flag-overrides-employee-discount
 *                      from main
 *   blarsen 12/22/10 - Moved discount eligible logic into DiscountUtility and
 *                      made logic consistent with change of requirements.
 *   cgreene 12/01/10 - implement saving applied promotion names into
 *                      tr_ltm_prm table
 *   jkoppol 11/30/10 - Transaction level discounts being applied to GC when
 *                      discount greater than card value
 *   cgreene 11/03/10 - rename ItemLevelMessageConstants
 *   mchella 09/14/10 - BUG#10104697 Disable price adjustment for transaction
 *                      level employee discounts.
 *   acadar  08/31/10 - changes for external orders to not filter by action
 *                      codes
 *   acadar  08/30/10 - do not filter external order items based on action code
 *   jswan   08/25/10 - Fixed issues returning a transaction with a transaction
 *                      discount and non discountable items. Also refactored
 *                      the creation of PLUItems to remove extraneous data
 *                      element from the SaleReturnLineItem table.
 *   jswan   08/18/10 - Merge changes from refresh to lable.
 *   jswan   08/18/10 - Added lineItemTaxable flag to the Sale Return Line Item
 *                      table.
 *   jswan   08/13/10 - Checkin for label server change.
 *   rsnayak 08/13/10 - POS Log Unknown Item fix
 *   acadar  08/05/10 - updated clone(), equals() and toString()
 *   acadar  08/05/10 - read the plu tax group id and use it for
 *                      suspend/retrieve
 *   acadar  06/10/10 - refreshed to tip
 *   acadar  06/10/10 - use default locale for currency display
 *   sgu     06/10/10 - fix tabs
 *   sgu     06/10/10 - negate the quantity if the external order item is a
 *                      return
 *   acadar  06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *   jswan   06/01/10 - Fixed merge issues
 *   jswan   06/01/10 - Modified to support transaction retrieval performance
 *                      and data requirements improvements.
 *   jswan   05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *   cgreene 05/26/10 - convert to oracle packaging
 *   sgu     05/20/10 - fix the identation
 *   sgu     05/20/10 - check in refactor of initialize method
 *   sgu     05/19/10 - minor fixes for external order APIs
 *   sgu     05/19/10 - enhance APIs to disallow discount rules for items with
 *                      external pricing
 *   sgu     05/18/10 - add external order plu and return line item
 *   sgu     05/18/10 - enhance SaleReturnLineItem class to store external
 *                      order info
 *   acadar  05/17/10 - temporarily rename the package
 *   acadar  05/17/10 - incorporated feedback from code review
 *   acadar  05/14/10 - initial version for external order processing
 *   cgreene 04/28/10 - updating deprecated names
 *   cgreene 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *   cgreene 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *   nkgauta 01/22/10 - Added Item IMEI Number Attribute
 *   aariyer 01/21/10 - For the giftcard
 *   abondal 01/03/10 - update header date
 *   cgreene 10/29/09 - XbranchMerge cgreene_nonreceiptreturns from
 *                      rgbustores_13.1x_branch
 *   cgreene 10/26/09 - back-dated isSourceAvailable to allow for
 *                      not-from-transaction return items
 *   cgreene 06/26/09 - implement QUANTITY formatting for printed number qhich
 *                      prints as an int or a double
 *   cgreene 06/22/09 - remove unnecassary creation of BigDecimal
 *   blarsen 04/20/09 - The <item #> <quantity> @ <price> line should not be
 *                      printed for gift card reloads. Making special method to
 *                      return a null selling price for this condition.
 *   cgreene 04/15/09 - removed uneeded ILRM constants
 *   jswan   04/14/09 - Modified to fix conflict between multi quantity items
 *                      and items that have been marked for Pickup or Delivery.
 *   cgreene 03/31/09 - fixed missing promotion name on receipt by refactoring
 *                      appliedPromotion to ItemPrice object and
 *                      setPromotionName from PriceChange.setCloneAttributes
 *   spurkay 03/25/09 - Discounts should not be applied to kit header items.
 *                      Since kit header items are split into kit component
 *                      items while adding to the transaction and calculating
 *                      best deal, we need to check the whether the item is kit
 *                      component or not while checking souce and target.
 *   acadar  03/23/09 - fix for the crash during return without receipt
 *   cgreene 03/20/09 - add appliedPromo to setCloneAttributes
 *   cgreene 03/19/09 - refactoring changes
 *   mahisin 03/04/09 - Fixed send button disable issue when line item mark as
 *                      pick-up or deliver vice versa
 *   mahisin 02/26/09 - Rework for PDO functionality
 *   deghosh 02/12/09 - Cleaning the deprecated method toJournalString()
 *   vchenge 02/05/09 - Made changes to format EJournal for Discount and
 *                      Markdowns
 *   vchenge 01/07/09 - ej defect fixes
 *   aphulam 01/02/09 - fix delivery issues
 *   nkgauta 12/30/08 - EJ Changes for extra space b/w price and Tax
 *   aphulam 12/23/08 - Mock padding fix and PDO flow related changes for
 *                      buttons enable/disable
 *   vikini  12/18/08 - Printing Ticket ID in the POS Receipt
 *   aphulam 12/10/08 - checked in after ade recover
 *   aphulam 12/10/08 - returns functionality changes for greying out buttons
 *   vchenge 12/05/08 - Formatted Ejournal entry for HPQC bug : 990
 *   abondal 12/02/08 - RM-POS integration
 *   deghosh 12/02/08 - EJ i18n changes
 *   cgreene 11/20/08 - remove unnecesary methods that were for printing
 *   cgreene 11/12/08 - fix misspell in getOriginalTransactionId
 *   mchella 11/10/08 - Merge Changes
 *   mchella 11/10/08 - Corrected merge error
 *   mchella 11/10/08 - Return Receipt - added methods to return original txn
 *                      ID and date
 *   vikini  11/10/08 - Displaying Message code ID in EJ
 *   vikini  11/08/08 - Externalized constants
 *   sswamyg 11/05/08 - Checkin after merges
 *   vchenge 11/05/08 - Internationalization of EJournal strings :
 *                      parameterized DB calls by passing the journal locale
 *   vchenge 11/04/08 - Changed deprecated-method calls to the new-method calls
 *                      by passing the Locale.
 *   sswamyg 11/04/08 - Modified to use toJournalString(Locale)
 *   acadar  11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *   akandru 10/31/08 - EJ Changes_I18n
 *   acadar  10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *   akandru 10/30/08 - EJ changes
 *   vikini  10/30/08 - Checking in after ADE merge
 *   vikini  10/30/08 - Changes from Code Review.Adding Code comments, Code
 *                      formatting.
 *   vikini  10/30/08 - Edited to provide implementation for Rebate and Footer
 *                      Message printing
 *   vikini  10/29/08 - Code changes to add screen level messages in Sale Item
 *                      Screen.
 *   acadar  10/29/08 - merged to tip
 *   ddbaker 10/28/08 - Update for merge
 *   acadar  10/28/08 - removed old deprecated methods
 *   acadar  10/27/08 - fix broken unittests
 *   acadar  10/25/08 - localization of price override reason codes
 *   akandru 10/23/08 - new helper class is used
 *   akandru 10/21/08 - new methods are added
 *   akandru 10/21/08 - new methods added to take the locale
 *   akandru 10/21/08 -
 *   cgreene 09/19/08 - updated with changes per FindBugs findings
 *   cgreene 09/11/08 - update header
 *
 * ===========================================================================
     $Log:
      24   360Commerce 1.23        5/21/2007 9:17:03 AM   Anda D. Cadar   Ej
           changes and cleanup
      23   360Commerce 1.22        5/18/2007 12:17:08 PM  Maisa De Camargo
           Added Methods for the PromotionLineItems.
      22   360Commerce 1.21        5/14/2007 6:08:34 PM   Sandy Gu
           update inclusive information in financial totals and history tables
      21   360Commerce 1.20        5/8/2007 11:30:58 AM   Anda D. Cadar
           currency changes for I18N
      20   360Commerce 1.19        4/30/2007 5:38:35 PM   Sandy Gu        added
            api to handle inclusive tax
      19   360Commerce 1.18        4/25/2007 10:00:39 AM  Anda D. Cadar   I18N
           merge
      18   360Commerce 1.17        4/12/2007 10:43:47 AM  Peter J. Fierro Merge
            for CR20660 from 7.2.2

           16   .v7x      1.7.1.7     8/22/2006 7:29:19 AM   Keith L. Lesikar
           Setting value for positemID for unknown line items sold within
           POS.
      17   360Commerce 1.16        12/8/2006 5:01:16 PM   Brendan W. Farrell
           Read the tax history when creating pos log for openclosetill
           transactions.  Rewrite of some code was needed.
      16   360Commerce 1.15        10/20/2006 12:54:16 PM Charles D. Baker
           Revamped EOL behavior of transaction header for automated testing
           success.
      15   360Commerce 1.14        8/7/2006 3:10:15 PM    Brendan W. Farrell
           Change fix from v7.x to meet coding standards.
      14   360Commerce 1.13        7/25/2006 7:48:21 PM   Robert Zurga
           Correct CR link to 18472
      13   360Commerce 1.12        7/25/2006 7:41:18 PM   Robert Zurga    Merge
            CR 19105 .v7x      1.7.1.4     6/22/2006 6:11:48 AM   Nageshwar
           Mishra CR 18472: Removed the extra line added in the code between
           header and the first Item.

      12   360Commerce 1.11        7/25/2006 4:03:37 PM   Nathan Syfrig
           CR18124:  Re-merged in the tax accumulator work and isGiftItem()
           from v7x, which was auto-merged out...
      11   360Commerce 1.10        7/25/2006 11:56:46 AM  Nathan Syfrig   Merge
            from SaleReturnLineItem.java, Revision 1.7.1.6
      10   360Commerce 1.9         7/20/2006 6:41:31 PM   Brendan W. Farrell
           Merge catch of null item id.
      9    360Commerce 1.8         4/27/2006 7:29:47 PM   Brett J. Larsen CR
           17307 - remove inventory functionality - stage 2
      8    360Commerce 1.7         3/30/2006 4:51:59 PM   Michael Wisbauer
           Changed to get original tax info from returnitem and then negate it
            before adding to rules
      7    360Commerce 1.6         3/29/2006 12:09:14 AM  Venkat Reddy    CR
           8312: Updated ???getFinancialTotals()??? method to set the Gift Card
           Sales Amount and Count .
      6    360Commerce 1.5         1/25/2006 4:11:44 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:41:41 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:49 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse
     $:
      19   360Commerce 1.18        4/25/2007 10:00:39 AM  Anda D. Cadar   I18N
           merge
      18   360Commerce 1.17        4/12/2007 10:43:47 AM  Peter J. Fierro Merge
            for CR20660 from 7.2.2

           16   .v7x      1.7.1.7     8/22/2006 7:29:19 AM   Keith L. Lesikar
           Setting value for positemID for unknown line items sold within
           POS.
      17   360Commerce 1.16        12/8/2006 5:01:16 PM   Brendan W. Farrell
           Read the tax history when creating pos log for openclosetill
           transactions.  Rewrite of some code was needed.
      16   360Commerce 1.15        10/20/2006 12:54:16 PM Charles D. Baker
           Revamped EOL behavior of transaction header for automated testing
           success.
      15   360Commerce 1.14        8/7/2006 3:10:15 PM    Brendan W. Farrell
           Change fix from v7.x to meet coding standards.
      14   360Commerce 1.13        7/25/2006 7:48:21 PM   Robert Zurga
           Correct CR link to 18472
      13   360Commerce 1.12        7/25/2006 7:41:18 PM   Robert Zurga    Merge
            CR 19105 .v7x      1.7.1.4     6/22/2006 6:11:48 AM   Nageshwar
           Mishra CR 18472: Removed the extra line added in the code between
           header and the first Item.

      12   360Commerce 1.11        7/25/2006 4:03:37 PM   Nathan Syfrig
           CR18124:  Re-merged in the tax accumulator work and isGiftItem()
           from v7x, which was auto-merged out...
      11   360Commerce 1.10        7/25/2006 11:56:46 AM  Nathan Syfrig   Merge
            from SaleReturnLineItem.java, Revision 1.7.1.6
      10   360Commerce 1.9         7/20/2006 6:41:31 PM   Brendan W. Farrell
           Merge catch of null item id.
      9    360Commerce 1.8         4/27/2006 7:29:47 PM   Brett J. Larsen CR
           17307 - remove inventory functionality - stage 2
      8    360Commerce 1.7         3/30/2006 4:51:59 PM   Michael Wisbauer
           Changed to get original tax info from returnitem and then negate it
            before adding to rules
      7    360Commerce 1.6         3/29/2006 12:09:14 AM  Venkat Reddy    CR
           8312: Updated ???getFinancialTotals()??? method to set the Gift Card
           Sales Amount and Count .
      6    360Commerce 1.5         1/25/2006 4:11:44 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:41:41 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:49 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse
     $:
      5    .v700     1.2.3.1     12/27/2005 15:06:20    Deepanshu       CR
           8052: Check for Employee discount eligibility
      4    .v700     1.2.3.0     12/21/2005 14:35:41    Deepanshu       CR
           7788: Set tax rule for return
      3    360Commerce1.2         3/31/2005 15:29:48     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:24:58     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:14:00     Robert Pearse
     $
      4    360Commerce1.3         12/13/2005 4:43:49 PM  Barry A. Pape
     $: SaleReturnLineItem.java,v $
           Base-lining of 7.1_LA
      3    360Commerce1.2         3/31/2005 3:29:48 PM   Robert Pearse
      2    360Commerce1.1         3/10/2005 10:24:58 AM  Robert Pearse
      1    360Commerce1.0         2/11/2005 12:14:00 PM  Robert Pearse
     $:
      4    .v710     1.2.2.0     9/21/2005 13:40:17     Brendan W. Farrell
           Initial Check in merge 67.
      3    360Commerce1.2         3/31/2005 15:29:48     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:24:58     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:14:00     Robert Pearse
     $
     Revision 1.86.2.2  2004/11/16 20:58:31  jdeleau
     @scr 4711 Allow item taxes to override transaction taxes.

     Revision 1.86.2.1  2004/11/03 22:19:42  mweis
     @scr 7012 Use proper Inventory constants for default values.

     Revision 1.86  2004/09/28 20:49:18  lzhao
     @scr 7147: exclusive gift card as a sale item.

     Revision 1.85  2004/09/24 15:46:54  jdeleau
     @scr 7238 For taxes on items being returned with more than
     one jurisdiction, the tax amount was not being correctly calculated.

     Revision 1.84  2004/09/14 16:58:41  mweis
     @scr 7012 Move the determination of whether a PLUItem is a unit-of-measure item to a utilities spot.

     Revision 1.83  2004/08/23 16:15:45  cdb
     @scr 4204 Removed tab characters

     Revision 1.82  2004/07/28 21:11:02  jdeleau
     @scr 6578 return rules had the same uniqueID for different items,
     causing tax to be calculated incorrectly.

     Revision 1.81  2004/07/26 14:39:55  jdeleau
     @scr 2775 Rename ReturnItemTaxRule to ReverseItemTaxRule add isReturn
     method, to the ReverseItemTaxRule interface

     Revision 1.80  2004/07/24 16:27:47  jdeleau
     @scr 6430 Make Tax Exempt items save their tax correctly in the database.

     Revision 1.79  2004/07/23 19:39:01  rsachdeva
     @scr 6412 'False' displays under Item header in EJournal display

     Revision 1.78  2004/07/23 18:15:22  jdeleau
     @scr 2775 Minor updates to the way tax works

     Revision 1.77  2004/07/23 00:49:31  jdeleau
     @scr 6332 Make ReturnTaxRule extend TaxRuleIfc, so that
     it can be properly read over the network, it is not a runtime tax rule.

     Revision 1.76  2004/07/22 20:28:27  jdeleau
     @scr 2775 Fix the way certain tax things were done, to comply
     with arch guidelines.

     Revision 1.75  2004/07/22 17:37:19  jdeleau
     @scr 6408 Make sure reprint receipt is exactly the same as the original.  To
     do this the right data needed to be pulled from the database, and
     the uniqueID on the taxRule needed to be properly set.

     Revision 1.74  2004/07/20 18:45:49  rsachdeva
     @scr 5647 (-xx.xx) in Journal fixed

     Revision 1.73  2004/07/19 21:53:44  jdeleau
     @scr 6329 Fix the way post-void taxes were being retrieved.
     Fix for tax overrides, fix for post void receipt printing, add new
     tax rules for reverse transaction types.

     Revision 1.72  2004/07/13 20:05:53  lzhao
     @scr 6060: Gift Receipt for reload gift card

     Revision 1.71  2004/07/13 00:23:12  jdeleau
     @scr 6186 Make sure tax scope is correctly saved in the ItemTaxIfc.
     Correct some code in TransactionOverride that was incorrectly using
     the scope variable to check for modifications to tax.

     Revision 1.70  2004/07/12 14:38:08  jdeleau
     @scr 6153 Fix return on gift card, removing print statement

     Revision 1.69  2004/07/12 14:16:04  jdeleau
     @scr 6153 Make sure gift card returns work correctly

     Revision 1.68  2004/07/09 18:39:18  aachinfiev
     @scr 6082 - Replacing "new" with DomainObjectFactory.

     Revision 1.67  2004/07/08 18:15:56  cdb
     @scr 6038 Updated so that inability to find PLUItem associated with a
     given sale return line item will throw data not found data exception. Cleaned
     up some associated errors.

     Revision 1.66  2004/07/07 22:10:34  jdeleau
     @scr 5785 Tax on returns needs to be pro-rated if the original tax was
     on a quantified number of items.  The pro-rating needs to have
     no rounding errors in the longer term, so that if a person is taxed
     68 cents on 5 items, he will only be refunded 68 cents even if he returns
     the 5 items one at a time.

     Revision 1.65  2004/06/30 21:26:55  jdeleau
     @scr 5921 Void transactions were doubling returned tax on
     kit Items.  This is because the header and individual line items were
     both calculating tax.  This is now corrected.

     Revision 1.64  2004/06/29 21:59:00  aachinfiev
     Merge the changes for inventory & POS integration

     Revision 1.63  2004/06/29 21:29:15  jdeleau
     @scr 5777 Improve on the way return taxes are calculated, to solve this defect.
     Returns and purchases were going into the same container, and return
     values were being cleared on subsequent calculations.

     Revision 1.62  2004/06/28 19:06:24  jdeleau
     @scr 5846 Make individual line items appear for kits too

     Revision 1.61  2004/06/28 16:09:25  jdeleau
     @scr 5866 Fix non-taxable items being taxed

     Revision 1.60  2004/06/25 21:42:25  jdeleau
     @scr 5776 Don't tax non-taxable items on a return.

     Revision 1.59  2004/06/24 21:31:41  jdeleau
     @scr 5817 On a return, if the tax was on a quantity of items, and the return
     is a limited number, pro-rate that tax based on the number of items
     being returned.

     Revision 1.58  2004/06/24 19:02:51  jdeleau
     @scr 5808 Dont allow transaction overrides to overwrite the tax on
     a tax-exempt or non-taxable item.

     Revision 1.57  2004/06/24 18:33:20  jdeleau
     @scr 5805 Tax Exempt transactions should not refund tax amounts on returns.

     Revision 1.56  2004/06/24 16:16:43  jdeleau
     @scr 5784 Fix the way T1-TN are generated, they are actually tax rule
     names.

     Revision 1.55  2004/06/21 22:29:16  jdeleau
     @scr 3767 Make sure the default tax rate is used if no rules can be found.

     Revision 1.54  2004/06/21 21:06:29  jriggins
     @scr 5686 Added a mechanism to allow manually setting the isPartOfPriceAdjustment status which is useful for displaying price adjustment components when they are normally filtered out.

     Revision 1.53  2004/06/21 13:51:32  rsachdeva
     @scr 4670 Send: Multiple Sends getItemSendFlag javadoc

     Revision 1.52  2004/06/17 17:36:34  mkp1
     @scr 2775 Defects for Tax

     Revision 1.51  2004/06/15 00:44:31  jdeleau
     @scr 2775 Support register reports and financial totals with the new
     tax engine.

     Revision 1.50  2004/06/14 13:50:33  mkp1
     @scr 2775 Changed returns that are retrieved not to recalculate tax

     Revision 1.49  2004/06/11 17:31:35  cdb
     @scr 5553 Updated handling of gift cards in financial totals for sake of register summary reports.

     Revision 1.48  2004/06/11 13:59:34  mkp1
     @scr 2775 More Tax - Returns

     Revision 1.47  2004/06/08 21:29:50  mweis
     @scr 5240 Returns:  Should not allow returning a gift certificate

     Revision 1.46  2004/06/04 19:10:17  rsachdeva
     @scr 4670 Send: Multiple Sends Return By Receipt/Void

     Revision 1.45  2004/06/02 13:33:47  mkp1
     @scr 2775 Implemented item tax overrides using new tax engine

     Revision 1.44  2004/05/27 16:59:23  mkp1
     @scr 2775 Checking in first revision of new tax engine.

     Revision 1.43  2004/05/26 14:07:18  rsachdeva
     @scr 4670 Send: Multiple Sends

     Revision 1.42  2004/05/20 22:54:56  cdb
     @scr 4204 Removed tabs from code base again.

     Revision 1.41  2004/05/19 18:33:31  cdb
     @scr 5103 Updating to more correctly handle register reports.

     Revision 1.40  2004/05/18 00:35:11  cdb
     @scr 5103    Corrected behavior of item and transaction discounts.

     Revision 1.39  2004/05/04 19:26:57  rsachdeva
     @scr 4670 Send: Multiple Sends

     Revision 1.38  2004/05/04 19:10:30  jriggins
     @scr 3979 Added check for empty EmployeeIfc instance in hasEmployeeDiscount()

     Revision 1.37  2004/05/03 19:59:02  dcobb
     @scr 4381 get "Tax Override Not Allowed" error when try to override tax for an item that was non-taxable but had tax turned on for that item

     Revision 1.36  2004/04/28 19:50:00  jriggins
     @scr 3979 Code review cleanup and removed the get/setLinkedItems methods introduced in price adjustment development

     Revision 1.35  2004/04/21 13:28:42  jriggins
     @scr 3979 Added get/setTransactionSequenceNumber()

     Revision 1.34  2004/04/20 12:50:59  jriggins
     @scr 3979 Removed setIsPartOfPriceAdjustment()

     Revision 1.33  2004/04/16 13:51:33  mweis
     @scr 4410 Price Override indicator -- initial submission

     Revision 1.32  2004/04/15 15:52:01  rsachdeva
     @scr 3906 Sale

     Revision 1.31  2004/04/15 15:39:16  jriggins
     @scr 3979 Added price adjustment members. Also added hasEmployeeDiscount() method

     Revision 1.30  2004/04/14 20:07:38  lzhao
     @scr 3872 Redeem, change gift card request type from String to in.

     Revision 1.29  2004/04/09 21:59:08  mweis
     @scr 4206 JavaDoc updates.

     Revision 1.28  2004/04/07 20:56:49  lzhao
     @scr 4218: add gift card info for summary report.

     Revision 1.27  2004/04/05 14:23:02  rsachdeva
     @scr  3906 Sale

     Revision 1.26  2004/04/03 00:21:15  jriggins
     @scr 3979 Price Adjustment feature dev

     Revision 1.25  2004/03/31 19:58:17  pkillick
     @scr 4167 -Used checks to ensure that gift certificates aren't added as items.(getFinancialTotals(boolean))

     Revision 1.24  2004/03/17 23:03:10  dcobb
     @scr 3911 Feature Enhancement: Markdown
     Code review modifications.

     Revision 1.23  2004/03/17 18:16:24  tfritz
     @scr 4003 - Fixed merging problems.

     Revision 1.22  2004/03/17 17:05:23  pkillick
     @scr  4003 - .substring() was removed because it was cutting off the last three chars from a value for the e-journal.(Line 2871)

     Revision 1.21  2004/03/16 21:35:24  dcobb
     @scr 3870 Feature Enhancement: Damage Discount
     Corrected revision history.

     Revision 1.20  2004/03/16 18:27:07  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.19  2004/03/15 20:43:36  cdb
     @scr 3588 Preserving Employee and Damage discountable flags.

     Revision 1.18  2004/03/15 20:28:34  cdb
     @scr 3588 Updated ItemPrice test. Removed ItemPrice deprecated (2 release) methods.

     Revision 1.17  2004/03/11 20:34:37  baa
     @scr 3561 add changes to handle transaction variable length id

     Revision 1.16  2004/03/10 21:40:52  cdb
     @scr 0 Repaired some unit tests and added null pointer
     safety to domain objects.

     Revision 1.15  2004/03/10 19:41:51  baa
     @scr work for parsing size from scanned item

     Revision 1.14  2004/03/05 19:11:44  bwf
     @scr 3765 Fix automatic gift receipt printing for gift
     card issue.

     Revision 1.13  2004/03/05 00:41:52  bjosserand
     @scr 3954 Tax Override

     Revision 1.12  2004/03/03 20:41:44  baa
     @scr 3561 Add is returnable method to saleline item

     Revision 1.11  2004/03/03 17:26:50  baa
     @scr 3561 add journaling of return items

     Revision 1.10  2004/03/02 18:49:54  baa
     @scr 3561 Returns add size info to journal and receipt

     Revision 1.9  2004/02/27 22:51:33  dcobb
     @scr 3870 Feature Enhancement: Damage Discounts
     Updated journaling for damage discounts.

     Revision 1.8  2004/02/24 15:15:33  baa
     @scr 3561 returns enter item

     Revision 1.7  2004/02/18 20:40:09  aarvesen
     @scr 3561 added item size code accessors

     Revision 1.6  2004/02/18 17:09:48  cdb
     @scr 3588 Updated journaling for Employee Discounts.

     Revision 1.5  2004/02/17 16:18:51  rhafernik
     @scr 0 log4j conversion

     Revision 1.4  2004/02/12 21:36:29  epd
     @scr 0
     These files comprise all new/modified files that make up the refactored send service

     Revision 1.3  2004/02/12 17:13:57  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:26:31  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:32  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.7   Feb 10 2004 10:07:58   bwf
 * Update per code review.
 *
 *    Rev 1.6   Feb 09 2004 17:15:40   crain
 * Added issued gift certificates
 * Resolution for 3814: Issue Gift Certificate
 *
 *    Rev 1.5   Feb 09 2004 14:56:06   cdb
 * Made methods for clearing item discounts more precise to handle damage and employee discount conditions.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.4   Feb 05 2004 14:14:04   bwf
 * Added is gift card issue and hasdamagediscount.
 * Resolution for 3765: Modify Item Feature
 *
 *    Rev 1.3   Feb 04 2004 08:33:36   Tim Fritz
 * The e-journal QTY is now X instead of X.XX for deleted items
 * Resolution for 3637: E. Journal format for QTY is incorrect when deleting items
 *
 *    Rev 1.2   Dec 10 2003 10:30:40   rrn
 * In journalString methods, changed "Removed" to "Deleted" and
 * quantity format to single digit unless item is Unit of Measure.
 * Resolution for 3506: Journal format changes
 *
 *    Rev 1.1   Oct 22 2003 09:59:44   baa
 * refactoring- get salereturnlineitem from factory
 *
 *    Rev 1.0.1.0   Oct 22 2003 09:54:30   baa
 * refactoring - get saleReturnlineitem instance from factory
 *
 *    Rev 1.0   Aug 29 2003 15:38:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.19   Aug 15 2003 17:28:54   baa
 * fix currency formatting issues
 * Resolution for 3328: Select Canadian Check crashes POS
 *
 *    Rev 1.18   Aug 08 2003 15:50:02   sfl
 * Took away the unnecessary longer precision process.
 * Resolution for POS SCR-3317: negative number currency formats vary
 *
 *    Rev 1.17   Jul 17 2003 06:45:04   jgs
 * Fixed problems caused by addition of Markdowns to the allowable number of ItemDiscountStrategy objects associated with an item.
 * Resolution for 3037: The ejournal for a transaction with multiple (3) % discounts applies and removes the first two discounts on the ejournal.
 *
 *    Rev 1.16   Jul 11 2003 09:42:14   jgs
 * Fixed printing of "Item:" when more than one manual item discount is available.
 * Resolution for 3034: Item Markdown % information is not displayed on the ejournal.
 *
 *    Rev 1.15   Jul 08 2003 07:23:58   jgs
 * Modified journal string to display either discount or markdown and report when none discount eligible items are included in a group item discount.
 * Resolution for 3034: Item Markdown % information is not displayed on the ejournal.
 * Resolution for 3036: Item Markdown % information is not displayed on the ejournal when a a line item is cleared and the transaction is cancelled.
 * Resolution for 3039: ejournal prints a non-discountable line item with a discount.
 *
 *    Rev 1.14   18 Jun 2003 06:42:18   mpm
 * Added code to set financial totals for price overrides.
 *
 *    Rev 1.13   Jun 06 2003 14:22:20   RSachdeva
 * Item Discounts count and amount not capturing for Void Transactions
 * Resolution for POS SCR-2554: Item Discounts not capturing and printing data correctly on Statistical Summary Report
 *
 *    Rev 1.12   Apr 22 2003 14:04:06   sfl
 * Excluded the kit header item from the taxExceedsSellingPrice
 * checking because kit header item's price is zero and that will
 * trigger unnecessary error message to be displayed.
 * Resolution for POS SCR-2190: Tax Error occurs when kit component is NOT the first item entered
 *
 *    Rev 1.11   Apr 09 2003 11:18:40   bwf
 * Deprecation Fixes
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.10   Mar 05 2003 18:05:24   DCobb
 * Change name alterationItemFlag & accessors.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.9   Feb 15 2003 14:52:18   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.8   Jan 22 2003 15:32:28   mpb
 * SCR #1626
 * Added methods to support getting and clearing markdowns as well as discounts.
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.7   Jan 13 2003 15:21:00   sfl
 * An improved checking for having correct item price display format in EJ.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.6   Jan 10 2003 11:26:26   sfl
 * Added the missing taxable tag,  tax amount EJ prints for return items. Also, adjusted the print format of currency length after decimal so that they are consistent for quantity @ price display part in EJ.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.5   Dec 13 2002 10:39:18   pjf
 * Don't clone best deal groups, recalculate best deal instead.
 * Resolution for 101: Merge KB discount fixes.
 *
 *    Rev 1.4   Dec 02 2002 18:06:56   sfl
 * Convert long precision currency format to shorter precision format for E-Journal Item price display.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.3   Aug 21 2002 12:47:00   DCobb
 * Added alteration item flag.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.2   11 Jun 2002 16:25:26   jbp
 * changes to report markdowns
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   05 Jun 2002 17:11:56   jbp
 * changes for pricing updates
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Jun 03 2002 16:58:50   msg
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.domain.lineitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;


import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderItemIfc;
import oracle.retail.stores.common.constants.ItemLevelMessageConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAudit;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.TaxTotals;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.financial.TaxTotalsIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.stock.ReturnMessageDTO;
import oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.ReverseItemTaxRuleIfc;
import oracle.retail.stores.domain.tax.ReverseTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.RunTimeTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.utility.xml.XMLConversionException;
import oracle.retail.stores.foundation.utility.xml.XMLConverterIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * Line item for sale or return.
 *
 * @version $Revision: /main/115 $
 */
public class SaleReturnLineItem extends AbstractTransactionLineItem implements SaleReturnLineItemIfc,
        DiscountRuleConstantsIfc
{
    private static final long serialVersionUID = 1082130267781642247L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(SaleReturnLineItem.class);

    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/115 $";

    /**
     * item quantity
     */
    protected BigDecimal itemQuantity = BigDecimal.ZERO;

    /**
     * quantity of items returned against this transaction
     */
    protected BigDecimal quantityReturned = BigDecimal.ZERO;

    /**
     * registry
     */
    protected RegistryIDIfc registry = null;

    /**
     * gift-registry-modified flag
     */
    protected boolean registryModifiedFlag = false;

    /**
     * item price
     */
    protected ItemPriceIfc itemPrice = DomainGateway.getFactory().getItemPriceInstance();

    /**
     * Price lookup item
     */
    protected PLUItemIfc pluItem = null;

    /**
     * return item data
     */
    protected ReturnItemIfc returnItem = null;

    /**
     * reply from request to return this item.
     */
    protected ReturnMessageDTO returnMessage;

    /**
     * discount rule for advanced pricing and best deal
     */
    protected ItemDiscountStrategyIfc advancedPricingDiscount = null;
    

    /**
     * The deposit amount for this item
     */
    protected CurrencyIfc depositAmount = null;

    /**
     * flag indicating availability as discount source
     */
    private boolean sourceAvailable = true;

    /**
     * item ID. Used when pluItem is not present (yet).
     */
    protected String pluItemID = null;

    /**
     * item serial number.
     */
    protected String itemSerial = null;

    /**
     * Item IMEI number
     */
    protected String itemIMEINumber =  null;
    /**
     * item tax rate method - set to default rate initially
     */
    protected int itemTaxMethod = ItemTaxIfc.ITEM_TAX_DEFAULT_RATE;

    /**
     * item send flag - set to false as default
     */
    protected boolean itemSendFlag = false;

    /**
     * gift receipt flag
     */
    protected boolean giftReceipt = false;   
    
    /**
     * alteration item flag
     */
    protected boolean alterationItemFlag = false;

    /**
     * order item status
     */
    protected OrderItemStatusIfc orderItemStatus = DomainGateway.getFactory().getOrderItemStatusInstance();
    
    /**
     * original order item status before any updates
     */
    protected OrderItemStatusIfc originalOrderItemStatus = DomainGateway.getFactory().getOrderItemStatusInstance();

    /**
     * line item reference
     */
    protected String lineReference = "";
    
    /**
     * order identifier
     **/
    protected String orderID = "";

    /**
     * order line item reference
     */
    protected int orderLineReference = 0;

    /**
     * captured order line item reference
     */
    protected int capturedOrderLineReference = 0;

    /**
     * kit header reference
     */
    protected int kitHeaderReference = 0;

    /**
     * entry method
     */
    protected EntryMethod entryMethod = EntryMethod.Manual;

    /**
     * this is the string code that represents an item size
     */
    protected String itemSizeCode;

    /**
     * this is the flag that indicates whether the tax has been changed in any
     * way
     */
    protected boolean taxChanged = false;

    /**
     * flag that indicates whether or not this object represents a price
     * adjustment defaults to false
     */
    protected boolean isPriceAdjustmentLineItem = false;

    /**
     * Reference to the "parent" PriceAdjustmentLineItemIfc object which
     * contains this line item
     */
    protected int priceAdjustmentReference = -1;

    /** The marker used to indicate if a price was overridden. */
    public static final String OVERRIDE_MARKER = DomainUtil.retrieveOverrideMarker("journal");

    /**
     * Original line number of this line item. This member was introduced in
     * order for price adjustment updates to locate the original sale line item
     * since its in-memory line number can change as it changes positions in the
     * current transaction.
     */
    protected int originalLineNumber = -1;

    /**
     * Original transaction number of this line item. This member was introduced
     * in order for price adjustment updates to update the retail price modifier
     * for the original sale item.
     */
    private long transactionSequenceNumber = -1;

    /**
     * send label count
     */
    protected int sendLabelCount = 0;

    /**
     * Flag to indicate whether or not an item is part of a price adjustment See
     * the isPartOfPriceAdjustment() method for more details as to how this
     * status is evaluated.
     */
    protected boolean isPartOfPriceAdjustment = false;

    /**
     * Flag to indicate whether or not an item has been manually set to be a
     * part of a price adjustment Otherwise isPartOfPriceAdjustment() method
     * uses other fields to determine whether or not an item is part of a price
     * adjustment. A call to setIsPartOfPriceAdjustment() will also set this
     * flag to true. This method would normally be used for display purposes
     * when a price adjustment component may need to appear in the UI where it
     * may normally have been filtered out (i.e. in the ShowSaleScreenSite after
     * a previously price adjusted line item has been returned.
     */
    protected boolean manuallySetPartOfPriceAdjustmentFlag = false;

    /**
     * Tax rules for a return
     */
    protected ReverseItemTaxRuleIfc[] reverseTaxRules = null;

    /**
     * Keep track of whether or not this line item is from a transaction. If it
     * is, the tax amounts will be preserved and not recalculated.
     */
    protected boolean fromTransaction = false;

    /**
     * Determine whether a related item is returnable
     *
     * @since NEP67
     */
    protected boolean relatedItemReturnable = true;

    /**
     * Which sequence number is the related item associated with.
     *
     * @since NEP67
     */
    protected int relatedItemSequenceNumber = -1;

    /**
     * Determine whether or not a related item is deletable
     *
     * @since NEP67
     */
    protected boolean relatedItemDeleteable = true;

    /**
     * line items array list
     */
    protected List<SaleReturnLineItemIfc> relatedItemLineItems;

    /**
     * Flag to indicate that this SaleReturnLineItem has had a price
     * modificaiton. i.e. PriceOverride. Initialized to false.
     */
    protected boolean hasPriceModification = false;

    /**
     * Flag to indicate that this SaleReturnLineItem has return lineitem.
     */
    protected boolean hasReturnItem = false;

    /**
     * Flag to indicate that this SaleReturnLineItem has send lineitem.
     */
    protected boolean hasSendItem = false;

    /**
     * Flag to indicate that this SaleReturnLineItem has been selected for Item
     * Modification.
     */
    protected boolean selectedForItemModification = false;
    
    /**
     * Flag to indicate that this SaleReturnLineItem has been splitted due to best deal calculation.
     */
    protected boolean selectedForItemSplit = false;

    /**
     * The description that was printed on the customer receipt; it orginates in
     * AS_ITM_I8.DE_ITM_SHRT, when the app writes the transaction to the DB, it saves
     * the value in TR_LTM_SLS_RTN.DE_ITM_SHRT_RCPT.  This element was added to
     * reduce the reliance of transacion retrieval on related item pricing and
     * item master tables.
     */
    protected String receiptDescription = null;

    /**
     * The locale of the customer description; it orginates in AS_ITM_I8.LCL and,
     * when the app writes the transaction to the DB, it saves the value in TR_LTM_SLS_RTN.
     * DE_ITM_LCL.  This element was added to reduce the reliance of transacion
     * retrieval on related item pricing and item master tables.
     */
    protected Locale receiptDescriptionLocale = LocaleMap.getLocale(LocaleConstantsIfc.RECEIPT);

    /**
     * Flag to indicate if this SaleReturnLineItem has an external order item id
     */
    protected String externalOrderItemID = null;

    /**
     * Flag to indicate if this SaleReturnLineItem has an external order parent item id
     */
    protected String externalOrderParentItemID = null;

    /**
     * Flag to indicate if this SaleReturnLineItem has pricing set by an external system
     */
    protected boolean externalPricingFlag = false;
    
    /**
     * Flag to indicate if this SaleReturnLineItem has tax set by an external system
     */
    protected boolean externalTaxFlag = false;

    /**
     * Flag to indicate if this sale return line item has an external item that needs to be updated
     * in the external order system
     */
    protected boolean externalOrderItemUpdateSourceFlag = true;

    /**
     * Flag indicating if the line item is a shipping charge
     */
    protected boolean isShippingCharge = false;

    /**
     * Store Base Currency for Cross Border Return of the Original Transaction
     *
     */
    protected String storeBaseCurrency = null;

    /**
     * Flag indicating if the line item is on clearance
     *
     */
    protected boolean onClearance;

    /**
     * This boolean indicates that the base item data was retrieved from a data
     * source that can be considered as cross channel.  The purpose of this flag
     * is assist with item processing with occurs imediately after item retrieval;
     */
    protected boolean pluDataFromCrossChannelSource = false;

    /**
     * Some processes such as xchannel pickup/shipping need to be able to identify
     * the group of items that were produced by spliting up line items in order to
     * accomadate advanced pricing.  This data member is not saved to the database,
     * and is not reliable over the entire course of the transaction.  A process
     * that needs use this data member must manage this member's contents.
     */
    protected int preSplitLineNumber = -1;
    
    /**
     * Constructs SaleReturnLineItem object.
     */
    public SaleReturnLineItem()
    {
    }

    /**
     * Initializes a SaleReturnLineItem object, setting item, tax rate, sales
     * associate, registry attributes and return item attributes.
     *
     * @param item PLU item
     * @param quantity item quantity
     * @param tax ItemTax object
     * @param pSalesAssociate default sales associate
     * @param pRegistry default registry
     * @param pReturnItem return item info
     */
    public void initialize(PLUItemIfc item, BigDecimal quantity, ItemTaxIfc tax, EmployeeIfc pSalesAssociate,
            RegistryIDIfc pRegistry, ReturnItemIfc pReturnItem)
    {
        // set attribute values
       initialize(item, quantity, tax, pSalesAssociate, pRegistry, pReturnItem, null);
    }

    /**
     * Initializes a SaleReturnLineItem object, setting item, tax rate, sales
     * associate, registry attributes, return item attributes and external
     * order item.
     *
     * @param item PLU item
     * @param tax ItemTax object
     * @param pSalesAssociate default sales associate
     * @param pRegistry default registry
     * @param pReturnItem return item info
     * @param pExternalOrderItem external order item
     */
    public void initialize(PLUItemIfc item, ItemTaxIfc tax,
            EmployeeIfc pSalesAssociate,
            RegistryIDIfc pRegistry,
            ReturnItemIfc pReturnItem,
            ExternalOrderItemIfc pExternalOrderItem)
    {
        BigDecimal quantity = pExternalOrderItem.getQuantity();
        if (!pExternalOrderItem.isSellItem())
        {
            quantity = quantity.negate();
        }
        initialize(item, quantity, tax, pSalesAssociate, pRegistry, pReturnItem, pExternalOrderItem);
    }

    /**
     * Initializes a SaleReturnLineItem object, setting item, tax rate, sales
     * associate, registry attributes and return item attributes.
     *
     * @param item PLU item
     * @param quantity item quantity
     * @param tax ItemTax object
     * @param pSalesAssociate default sales associate
     * @param pRegistry default registry
     * @param pReturnItem return item info
     */
    public void initialize(PLUItemIfc item, BigDecimal quantity, ItemTaxIfc tax, EmployeeIfc pSalesAssociate,
            RegistryIDIfc pRegistry, ReturnItemIfc pReturnItem, ExternalOrderItemIfc pExternalOrderItem)
    {
        // set attribute values
        itemQuantity = quantity;
        pluItem = item;
        salesAssociate = pSalesAssociate;
        returnItem = pReturnItem;
        returnMessage = new ReturnMessageDTO();
        depositAmount = DomainGateway.getBaseCurrencyInstance();

        // create itemPrice and set values to defaults or to those from pluItem
        itemPrice = DomainGateway.getFactory().getItemPriceInstance();

        itemPrice.setItemQuantity(itemQuantity);
        itemPrice.setItemTax(tax);

        // store external order item information
        if (pExternalOrderItem != null)
        {
            externalOrderItemID = pExternalOrderItem.getId();
            externalOrderParentItemID = pExternalOrderItem.getParentId();
            externalPricingFlag = pExternalOrderItem.getPrice() != null;
            externalOrderItemUpdateSourceFlag = pExternalOrderItem.isUpdateSourceFlag();
        }

        // set external pricing if available
        if (externalPricingFlag)
        {
            if (pExternalOrderItem != null)
            {
                itemPrice.setSellingPrice(pExternalOrderItem.getPrice());
                itemPrice.setPermanentSellingPrice(pExternalOrderItem.getPrice());
            }
            itemPrice.setDiscountEligible(false);
            itemPrice.setEmployeeDiscountEligible(false);
            itemPrice.setDamageDiscountEligible(false);
        }
        // set information from the plu if available
        if (pluItem != null)
        {
            if (!externalPricingFlag)
            {
                itemPrice.setSellingPrice(pluItem.getPrice());

                if (pluItem.getItemClassification().isPriceEntryRequired())
                {
                    itemPrice.setPermanentSellingPrice(pluItem.getPrice());
                }
                else
                {
                    itemPrice.setPermanentSellingPrice(pluItem.getPermanentPrice(new EYSDate()));
                }
                
                itemPrice.setAppliedPromotion(pluItem.getEffectivePromotionalPrice());
                itemPrice.setDiscountEligible(pluItem.isDiscountEligible());
                itemPrice.setEmployeeDiscountEligible(pluItem.getItemClassification().getEmployeeDiscountAllowedFlag());
                itemPrice.setDamageDiscountEligible(pluItem.getDamageDiscountEligible());
            }
            itemPrice.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());

            // Added to reduce dependance on item pricing and master tables during
            // transaction retrieval.
            receiptDescription = pluItem.getShortDescription(receiptDescriptionLocale);

            onClearance = pluItem.isOnClearance();
        }

        // set price from return item, if available
        if (returnItem != null)
        {
            if (!externalPricingFlag)
            {
                itemPrice.setSellingPrice(returnItem.getPrice());
                if (returnItem.getPLUItem() != null)
                {
                    itemPrice.setAppliedPromotion(returnItem.getPLUItem().getEffectivePromotionalPrice(
                            returnItem.getOriginalTransactionBusinessDate()));
                }
            }
            itemPrice.setRestockingFee(returnItem.getRestockingFee());
            itemSerial = returnItem.getSerialNumber();
        }

        if (pluItem == null || pluItem.getItemClassification().isRegistryEligible())
        {
            registry = pRegistry;
        }

    } // end SaleReturnLineItem()

    /**
     * Calculates and sets item price.
     */
    public void calculateLineItemPrice()
    {
        itemPrice.calculateItemTotal();
    }

    /**
     * Copies object.
     *
     * @return generic object copy of this SaleReturnLineItem object
     */
    public Object clone()
    {
        SaleReturnLineItem newSrli = new SaleReturnLineItem();

        setCloneAttributes(newSrli);

        return newSrli;
    }

    /**
     * Clones the attributes of this class attributes. This is to be called by
     * the clone of the children with an new instance of this class.
     *
     * @param newSrli new SaleReturnLineItem instance
     */
    protected void setCloneAttributes(SaleReturnLineItem newSrli)
    {
        // clone superclass attributes
        super.setCloneAttributes(newSrli);

        // Note, there is no need to clone the PLUItem. It is immutable.
        newSrli.pluItem = pluItem;

        // clone rest of attributes
        if (itemSerial != null)
        {
            newSrli.itemSerial = itemSerial;
        }
        // clone return item, if valid
        if (returnItem != null)
        {
            newSrli.returnItem = (ReturnItemIfc)returnItem.clone();
        }
        // clone registry, if valid
        if (registry != null)
        {
            newSrli.registry = (RegistryIDIfc)registry.clone();
        }

        // build new item price, if old one exists
        if (itemPrice != null)
        {
            newSrli.setItemPrice((ItemPriceIfc)itemPrice.clone());
        }

        // build new advanced pricing discount, if old one exists
        if (advancedPricingDiscount != null)
        {
            newSrli.advancedPricingDiscount = (ItemDiscountStrategyIfc)advancedPricingDiscount.clone();
        }
        // set other attributes
        newSrli.setItemQuantity(itemQuantity);
        newSrli.setQuantityReturned(quantityReturned);
        newSrli.setRegistryModifiedFlag(registryModifiedFlag);
        newSrli.setItemTaxMethod(itemTaxMethod);
        newSrli.setSourceAvailable(sourceAvailable);
        newSrli.setItemSendFlag(itemSendFlag);
        newSrli.setFromTransaction(fromTransaction);
        if (getItemSendFlag() || this.isShippingCharge)
        {
            newSrli.setSendLabelCount(sendLabelCount);
        }
        newSrli.setGiftReceiptItem(giftReceipt);
        newSrli.setAlterationItemFlag(alterationItemFlag);
        newSrli.setItemSizeCode(itemSizeCode);
        if (orderItemStatus != null)
        {
            newSrli.setOrderItemStatus((OrderItemStatusIfc)orderItemStatus.clone());
        }
        if (originalOrderItemStatus != null)
        {
            newSrli.setOriginalOrderItemStatus((OrderItemStatusIfc)originalOrderItemStatus.clone());
        }
        else
        {
            newSrli.setOriginalOrderItemStatus(null);
        }
        newSrli.setLineReference(getLineReference());
        newSrli.setOrderID(getOrderID());
        newSrli.setOrderLineReference(getOrderLineReference());
        newSrli.setCapturedOrderLineReference(getCapturedOrderLineReference());
        newSrli.setKitHeaderReference(kitHeaderReference);
        newSrli.setEntryMethod(getEntryMethod());
        newSrli.setTaxChanged(isTaxChanged());
        newSrli.setIsPriceAdjustmentLineItem(isPriceAdjustmentLineItem());
        newSrli.setPriceAdjustmentReference(getPriceAdjustmentReference());
        newSrli.manuallySetPartOfPriceAdjustmentFlag = this.manuallySetPartOfPriceAdjustmentFlag;
        if (newSrli.manuallySetPartOfPriceAdjustmentFlag)
        {
            newSrli.setIsPartOfPriceAdjustment(this.isPartOfPriceAdjustment);
        }
        newSrli.setRelatedItemSequenceNumber(this.relatedItemSequenceNumber);
        newSrli.setRelatedItemReturnable(this.relatedItemReturnable);
        newSrli.setRelatedItemDeleteable(this.relatedItemDeleteable);
        SaleReturnLineItemIfc[] relatedItems = getRelatedItemLineItems();
        if (relatedItems != null)
        {
            for (int i = 0; i < relatedItems.length; i++)
            {
                newSrli.addRelatedItemLineItem((SaleReturnLineItem)relatedItems[i].clone());
            }
        }
        newSrli.setHasPriceModification(this.hasPriceModification);
        newSrli.setHasReturnItem(this.hasReturnItem);
        newSrli.setHasSendItem(this.hasSendItem);
        newSrli.setSelectedForItemModification(this.selectedForItemModification);
        newSrli.setExternalOrderItemID(this.externalOrderItemID);
        newSrli.setExternalOrderParentItemID(this.externalOrderParentItemID);
        newSrli.setExternalPricingFlag(this.externalPricingFlag);
        newSrli.setExternalTaxFlag(this.externalTaxFlag);
        newSrli.setExternalOrderItemUpdateSourceFlag(this.externalOrderItemUpdateSourceFlag);
        newSrli.setReceiptDescription(this.receiptDescription);
        newSrli.setReceiptDescriptionLocale(this.receiptDescriptionLocale);
        newSrli.setOnClearance(this.onClearance);
        newSrli.setShippingCharge(this.isShippingCharge);        
        newSrli.setPluDataFromCrossChannelSource(this.pluDataFromCrossChannelSource);
        newSrli.setPreSplitLineNumber(this.preSplitLineNumber);
        newSrli.setSelectedForItemSplit(this.isSelectedForItemSplit());
        if (this.depositAmount != null)
        {
            newSrli.setDepositAmount((CurrencyIfc)this.depositAmount.clone());
        }
        newSrli.setOriginalLineNumber(this.originalLineNumber);
    }

    /**
     * Modifies item quantity, reset item total.
     *
     * @param newQty new quantity
     */
    public void modifyItemQuantity(BigDecimal newQty)
    {
        itemQuantity = newQty;
        itemPrice.setItemQuantity(itemQuantity);
        itemPrice.calculateItemTotal();
    }
    
    /**
     * Negate item quantity. This function is used to post void a transaction
     */
    public void negateItemQuantity()
    {
    	itemQuantity = itemQuantity.negate();
    	itemPrice.negateItemTotal();
    }

    /**
     * Returns the total price of this line item with all modifications applied.
     *
     * @return CurrencyIfc total
     */
    public CurrencyIfc getLineItemAmount()
    {
        return itemPrice.getItemTotal();
    }

    /**
     * Returns the localized text description for a given locale.
     *
     * @param locale The locale of the description to retrieve
     * @return The localized description
     */
    public String getItemDescription(Locale locale)
    {
        return pluItem == null ? null : pluItem.getDescription(locale);
    }

    /**
     * Returns the localized text descriptions.
     *
     * @return The localized text descriptions
     */
    public LocalizedTextIfc getLocalizedItemDescriptions()
    {
        return pluItem == null ? null : pluItem.getLocalizedDescriptions();
    }

    /**
     * Returns the ID for this item.
     *
     * @return String identifier
     */
    public String getItemID()
    {
        String id = "";

        if (pluItem != null)
        {
            id = pluItem.getItemID();
        }
        else if (pluItemID != null)
        {
            id = pluItemID;
        }

        return id;
    }

    /**
     * Returns the original price for a single item.
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getSellingPrice()
    {
        return itemPrice.getSellingPrice();
    }

    /**
     * Returns the original price for a single item to be printed on receipts.
     * This method contains additional logic specific to the
     * "<item #> <quantity> @ <price>" line. This line should not be printed in
     * some cases. For these cases, a null is returned. Review the logic in this
     * method before using it for any other purpose.
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getPrintedSellingPrice()
    {
        if (isGiftCardReload())
        {
            return null;
        }

        return itemPrice.getSellingPrice();
    }

    /**
     * Returns the extended price before discounts. (original price * item
     * quantity)
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getExtendedSellingPrice()
    {
        return itemPrice.getExtendedSellingPrice();
    }

    /**
     * Returns the extended price after discounts. ((original price * item
     * quantity) - item discount total)
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getExtendedDiscountedSellingPrice()
    {
        return itemPrice.getExtendedDiscountedSellingPrice();
    }

    /**
     * Returns the item discount
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getItemDiscountAmount()
    {
        return itemPrice.getItemDiscountAmount();
    }

    /**
     * Returns the total discount amount for discounts matching specified
     * parameters.
     *
     * @param discountScope discount scope
     * @param assignmentBasis assignment basis
     * @return The total discount amount for discounts matching specified
     *         parameters.
     */
    public CurrencyIfc getDiscountAmount(int discountScope, int assignmentBasis)
    {
        return itemPrice.getDiscountAmount(discountScope, assignmentBasis);
    }

    /**
     * Returns the item discount total
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getItemDiscountTotal()
    {
        return itemPrice.getItemDiscountTotal();
    }

    /**
     * Clears ItemDiscountStrategyIfcs with the corresponding discountRuleID
     * from itemPrice.itemDiscountsVector
     *
     * @param discountRuleID the discount rule ID
     */
    public void clearItemDiscounts(String discountRuleID)
    {
        // have the itemPrice clear discounts with corresponding ruleID
        itemPrice.clearItemDiscounts(discountRuleID);
        // clear the advancedPricingDiscount and make item available as source
        // if necessary
        if (advancedPricingDiscount != null && advancedPricingDiscount.getRuleID().equals(discountRuleID))
        {
            advancedPricingDiscount = null;
            setSourceAvailable(true);
        }
        // recalculate item $ values
        calculateLineItemPrice();
    }

    /**
     * Retrieves array of item discounts by percentage.
     *
     * @return array of disc item discount objects, null if not found
     */
    public ItemDiscountStrategyIfc[] getItemDiscountsByPercentage()
    {
        return itemPrice.getItemDiscountsByPercentage();
    }

    /**
     * Retrieves array of item discounts by amount.
     *
     * @return array of disc item discount objects, null if not found
     */
    public ItemDiscountStrategyIfc[] getItemDiscountsByAmount()
    {
        return itemPrice.getItemDiscountsByAmount();
    }

    /**
     * Retrieves array of return item discounts.
     *
     * @return array of disc item discount objects, null if not found
     */
    public ItemDiscountStrategyIfc[] getReturnItemDiscounts()
    {
        return itemPrice.getReturnItemDiscounts();
    }

    /**
     * Retrieves array of transaction discount audit objects.
     *
     * @return array of transasction discount audit objects, null if not found
     */
    public ItemDiscountStrategyIfc[] getTransactionDiscounts()
    {
        return itemPrice.getTransactionDiscounts();
    }

    /**
     * Clears the transaction discounts
     */
    public void clearTransactionDiscounts()
    {
        itemPrice.clearTransactionDiscounts();
        itemPrice.setItemTransactionDiscountAmount(DomainGateway.getBaseCurrencyInstance());
    }

    /**
     * Recalculates the item total
     */
    public void recalculateItemTotal()
    {
        itemPrice.recalculateItemTotal();
    }

    /**
     * Sets the discount total
     *
     * @param itemDiscount discount total
     */
    public void setItemDiscountTotal(CurrencyIfc itemDiscount)
    {
        itemPrice.setItemDiscountTotal(itemDiscount);
    }

    /**
     * Returns the transaction discount amount
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getItemTransactionDiscountAmount()
    {
        return itemPrice.getItemTransactionDiscountAmount();
    }

    /**
     * Adds a transaction discount.
     *
     * @param value discount amount
     * @param td strategy to collect discount attributes from
     */
    public void addTransactionDiscount(CurrencyIfc value, TransactionDiscountStrategyIfc td)
    {
        itemPrice.addTransactionDiscount(value, td);
    }

    /**
     * Gets the item tax amount.
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getItemTaxAmount()
    {
        return itemPrice.getItemTaxAmount();
    }

    /**
     * Sets the item tax amount
     *
     * @param value item tax amount
     */
    public void setItemTaxAmount(CurrencyIfc value)
    {
        itemPrice.setItemTaxAmount(value);
    }

    /**
     * Gets the item inclusive tax amount.
     *
     * @return CurrencyIfc
     */
    public CurrencyIfc getItemInclusiveTaxAmount()
    {
        return itemPrice.getItemInclusiveTaxAmount();
    }

    /**
     * Sets the item inclusive tax amount
     *
     * @param value item inclusive tax amount
     */
    public void setItemInclusiveTaxAmount(CurrencyIfc value)
    {
        itemPrice.setItemInclusiveTaxAmount(value);
    }

    /**
     * Set the scope of the tax
     *
     * @param scope
     * @see oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc#setTaxScope(int)
     */
    public void setTaxScope(int scope)
    {
        itemPrice.getItemTax().setTaxScope(scope);
    }

    /**
     * Get the tax scope
     *
     * @return scope
     * @see oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc#getTaxScope()
     */
    public int getTaxScope()
    {
        return itemPrice.getItemTax().getTaxScope();
    }

    //---------------------------------------------------------------------
    /**
     * Returns tax modification reason code
     *
     * @return LocalizedCodeIfc reason code
     */
    //---------------------------------------------------------------------
    public LocalizedCodeIfc getTaxModReasonCode()
    {
        return itemPrice.getItemTax().getReason();
    }

    //---------------------------------------------------------------------
    /**
     * Sets tax modification reason code
     *
     * @param code LocalizedCodeIfc reason code
     */
    //---------------------------------------------------------------------
    public void setTaxModReasonCode(LocalizedCodeIfc code)
    {
        itemPrice.getItemTax().setReason(code);
    }

    /**
     * Returns the item tax
     *
     * @return ItemTax
     */
    public ItemTaxIfc getItemTax()
    {
        return itemPrice.getItemTax();
    }

    /**
     * Returns the item tax mode
     *
     * @return int tax mode
     */
    public int getTaxMode()
    {
        return getItemTax().getTaxMode();
    }

    /**
     * Set the tax mode
     *
     * @param value Value to set
     */
    public void setTaxMode(int value)
    {
        getItemTax().setTaxMode(value);
    }

    /**
     * Determines the tax status descriptor flag.
     *
     * @return a string value to describe taxability status
     */
    public String getTaxStatusDescriptor()
    {
        int taxMode;
        TaxInformationContainerIfc container = getTaxInformationContainer();
        if ((container != null) && (container.getTaxInformation() != null) && (container.getTaxInformation().length > 0))
        {
            taxMode = container.getTaxInformation()[0].getTaxMode();
        }
        else
        {
            taxMode = TaxIfc.TAX_MODE_NON_TAXABLE;
        }
        return TaxIfc.TAX_MODE_CHAR[taxMode];
    }

    /**
     * Clears the taxes
     */
    public void clearTaxAmounts()
    {
        getItemTax().clearTaxAmounts();
    }

    /**
     * Returns the taxable flag
     *
     * @return boolean
     */
    public boolean getTaxable()
    {
        return pluItem.getTaxable();
    }

    /**
     * Sets the taxable flag
     *
     * @param value The boolean value for the taxable flag
     */
    public void setTaxable(boolean value)
    {
        pluItem.setTaxable(value);
    }

    /**
     * Returns the tax group ID
     *
     * @return int
     */
    public int getTaxGroupID()
    {
        return pluItem.getTaxGroupID();
    }

    /**
     * Tests the item's tax amount to see if it is greater than the extended
     * selling price of the item.
     *
     * @return boolean
     */
    public boolean taxExceedsSellingPrice()
    {
        boolean value = false;

        if (!this.isKitHeader())
        {
            if (this.isSaleLineItem()
                    && getExtendedSellingPrice().compareTo(getItemTaxAmount()) == CurrencyIfc.LESS_THAN)
            {
                value = true;
            }
            else if (this.isReturnLineItem()
                    && getItemTaxAmount().compareTo(getExtendedSellingPrice()) == CurrencyIfc.LESS_THAN)
            {
                value = true;
            }
        }

        return value;
    }

    /**
     * Tests the item's discount amount to see if it is greater than the
     * extended selling price of the item.
     *
     * @return boolean
     */
    public boolean discountExceedsSellingPrice()
    {
        boolean value = false;

        if (this.isSaleLineItem()
                && getExtendedSellingPrice().compareTo(getItemDiscountTotal()) == CurrencyIfc.LESS_THAN)
        {
            value = true;
        }
        else if (this.isReturnLineItem()
                && getItemDiscountTotal().compareTo(getExtendedSellingPrice()) == CurrencyIfc.LESS_THAN)
        {
            value = true;
        }

        return value;
    }

    /**
     * Returns the extended amount for this item with everything applied but tax
     *
     * @return CurrencyIfc amount
     */
    public CurrencyIfc getFinalPreTaxAmount()
    {
        return itemPrice.getExtendedDiscountedSellingPrice();
    }

    /**
     * Determines if this class is discountable by amount
     *
     * @return boolean
     */
    public boolean isDiscountableByAmount()
    {
        return true;
    }

    /**
     * Determines if this class is discountable by percentage
     *
     * @return boolean
     */
    public boolean isDiscountableByPercentage()
    {
        return true;
    }

    /**
     * Adds item discount object.
     *
     * @param discount ItemDiscountStrategyIfc object
     */
    public void addItemDiscount(ItemDiscountStrategyIfc discount)
    {
        itemPrice.addItemDiscount(discount);
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item discounts by percentage with a given basis and damage flag.
     *
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByPercentage(int basis, boolean damage)
    {
        itemPrice.clearItemDiscountsByPercentage(basis, damage);
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item discounts by percentage with a given type code, basis and
     * damage flag.
     *
     * @param typeCode int
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByPercentage(int typeCode, int basis, boolean damage)
    {
        itemPrice.clearItemDiscountsByPercentage(typeCode, basis, damage);
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item markdowns by percentage.
     */
    public void clearItemMarkdownsByPercentage()
    {
        itemPrice.clearItemMarkdownsByPercentage();
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item markdowns by percentage.
     *
     * @param typeCode type code
     */
    public void clearItemMarkdownsByPercentage(int typeCode)
    {
        itemPrice.clearItemMarkdownsByPercentage(typeCode);
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item discounts by amount with a given basis and damage flag.
     *
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByAmount(int basis, boolean damage)
    {
        itemPrice.clearItemDiscountsByAmount(basis, damage);
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item discounts by amount with a given type code, basis and damage
     * flag.
     *
     * @param typeCode int
     * @param basis The assignment basis number
     * @param damage The damage flag
     */
    public void clearItemDiscountsByAmount(int typeCode, int basis, boolean damage)
    {
        itemPrice.clearItemDiscountsByAmount(typeCode, basis, damage);
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item markdowns by amount.
     */
    public void clearItemMarkdownsByAmount()
    {
        itemPrice.clearItemMarkdownsByAmount();
        itemPrice.calculateItemTotal();
    }

    /**
     * Clears item markdowns by amount.
     *
     * @param typeCode type code
     */
    public void clearItemMarkdownsByAmount(int typeCode)
    {
        itemPrice.clearItemMarkdownsByAmount(typeCode);
        itemPrice.calculateItemTotal();
    }

    /**
     * Sets array of item discounts by amount.
     *
     * @param value array of disc item discount objects, null if not found
     */
    public void setItemDiscountsByAmount(ItemDiscountStrategyIfc[] value)
    {
        itemPrice.setItemDiscountsByAmount(value);
        itemPrice.calculateItemTotal();
    }

    /**
     * Sets array of item discounts by percentage.
     *
     * @param value array of disc item discount objects, null if not found
     */
    public void setItemDiscountsByPercentage(ItemDiscountStrategyIfc[] value)
    {
        itemPrice.setItemDiscountsByPercentage(value);
        itemPrice.calculateItemTotal();
    }

    /**
     * Modifies item tax.
     *
     * @param newRate new tax rate
     * @param reasonCode reason code
     */
    public void modifyItemTaxRate(double newRate, LocalizedCodeIfc reasonCode)
    {
        itemPrice.overrideTaxRate(newRate, reasonCode);
    }

    /**
     * Modifies the item selling price
     *
     * @param newPrice
     * @param reason
     */
    public void modifyItemPrice(CurrencyIfc newPrice, LocalizedCodeIfc reason)
    {
        itemPrice.overridePrice(newPrice, reason);
    }

    /**
     * Modifies item registry and sets modified flag to requested value.
     *
     * @param newGift new registry
     * @param modified modified flag
     */
    public void modifyItemRegistry(RegistryIDIfc newGift, boolean modified)
    {
        // set new registry, modified flag
        setRegistry(newGift);
        setRegistryModifiedFlag(modified);
    }

    /**
     * Force implementation of getFinancialTotals by subclasses
     *
     * @return totals FinancialTotalsIfc object for this line item
     */
    public FinancialTotalsIfc getFinancialTotals()
    {
        // Calling method assumes this transaction is either a Sale
        // or a Return transaction and not a Post Void Transaction.
        return getFinancialTotals(true);
    }

    /**
     * Returns financial totals for the line item.
     *
     * @param isSaleOrReturnTransaction boolean
     * @return totals FinancialTotalsIfc object for this line item
     */
    public FinancialTotalsIfc getFinancialTotals(boolean isSaleOrReturnTransaction)
    {
        // In order to clearify the processing of this function, I changed the
        // name of the
        // of parameter. This method needs to know if its dealing with a regular
        // SaleReturn
        // transaction or a voided SaleReturn transaction. If the parameter
        // isSaleOrReturnTransaction is false, then this transaction is a
        // PostVoid.

        // Get the values required to calcultate totals
        // ItemPriceIfc ip = getItemPrice();
        PLUItemIfc pluItem = getPLUItem();
        BigDecimal units = getItemQuantityDecimal();
        CurrencyIfc gross = getItemPrice().getExtendedDiscountedSellingPrice();
        CurrencyIfc extendedRestockingFee = getItemPrice().getExtendedRestockingFee();
        
        // Adjust the gross based on the restocking fee.
        if (extendedRestockingFee != null)
        {
            gross = gross.subtract(extendedRestockingFee);
        }

        FinancialTotalsIfc totals = DomainGateway.getFactory().getFinancialTotalsInstance();
        // Process sale and return items
        if (isSaleOrReturnTransaction)
        {
            if (isShippingCharge)
            {
                getShippingChargeItemSaleOrVoidFinancialTotals(totals, true);
            }
            else if (pluItem instanceof GiftCardPLUItemIfc)
            {
                // It is possible that the gift card will be discounted, but it is still issued/reloaded,
                // etc. at the full face value of the card.
                getGiftCardFinancialTotals(totals, getItemPrice().getExtendedSellingPrice(), units);
            }
            else if (pluItem instanceof GiftCertificateItemIfc)
            {
                getGiftCertificateFinancialTotals(totals, getItemPrice().getExtendedSellingPrice(), units);
            }
            else
            {
                getPLUItemFinancialTotals(totals, gross, units);
            }   
        }
        else
        // Process sale and return items in post void transactions
        {
            if (isShippingCharge)
            {
                getShippingChargeItemSaleOrVoidFinancialTotals(totals, false);
            }
            else if (pluItem instanceof GiftCardPLUItemIfc)
            {
                getVoidGiftCardFinancialTotals(totals, getItemPrice().getExtendedSellingPrice(), units);
            }
            else if (pluItem instanceof GiftCertificateItemIfc)
            {
                getVoidGiftCertificateFinancialTotals(totals, gross, units);
            }
            else
            // regular item
            {
                getVoidPLUItemFinancialTotals(totals, gross, units);
            }
        }

        // process item discounts
        FinancialTotalsIfc dsft = null;
        dsft = getItemPrice().getItemDiscountsFinancialTotals();
        totals.add(dsft);

        // Increment or decrement the price overrides totals. The reason this
        // works here
        // that units and gross are positive for sales and voids of returns and
        // negative for
        // returns and voids of sales.
        if (!getItemPrice().getItemPriceOverrideReason().getCode().equals(CodeConstantsIfc.CODE_UNDEFINED))
        {
            totals.addUnitsPriceOverrides(units);
            totals.addAmountPriceOverrides(gross);
        }

        // pass back totals
        return (totals);
    } // end getFinancialTotals()

    /**
     * This method calculates the totals for the sales of gift cards.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getGiftCardFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)getPLUItem();
        if (giftCardPLUItem != null)
        {
            GiftCardIfc giftCard = giftCardPLUItem.getGiftCard();
            if (giftCard != null)
            {
                if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_ISSUE)
                {
                    totals.addAmountGrossGiftCardItemIssued(gross);
                    totals.addUnitsGrossGiftCardItemIssued(units);
                    totals.addAmountGrossGiftCardItemSales(gross);
                    totals.addUnitsGrossGiftCardItemSales(units);
                }
                else if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD)
                {
                    totals.addAmountGrossGiftCardItemReloaded(gross);
                    totals.addUnitsGrossGiftCardItemReloaded(units);
                }
                else if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_CREDIT_ISSUE
                        || giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_CREDIT_RELOAD)
                {
                    totals.addAmountGrossGiftCardItemCredit(gross);
                    totals.addUnitsGrossGiftCardItemCredit(units);
                }
            }
        }
    }

    /**
     * This method calculates the totals for the sales of gift cards in post
     * void transactions.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getVoidGiftCardFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        GiftCardPLUItemIfc giftCardPLUItem = (GiftCardPLUItemIfc)getPLUItem();
        GiftCardIfc giftCard = giftCardPLUItem.getGiftCard();
        if (giftCard != null)
        {
            switch (giftCard.getRequestType())
            {
                case (GiftCardIfc.GIFT_CARD_ISSUE):
                {
                    totals.addAmountGrossGiftCardItemIssueVoided(gross.abs());
                    totals.addUnitsGrossGiftCardItemIssueVoided(units.abs());
                    break;
                }
                case (GiftCardIfc.GIFT_CARD_RELOAD):
                {
                    totals.addAmountGrossGiftCardItemReloadVoided(gross.abs());
                    totals.addUnitsGrossGiftCardItemReloadVoided(units.abs());
                    break;
                }
                case (GiftCardIfc.GIFT_CARD_CREDIT_ISSUE):
                case (GiftCardIfc.GIFT_CARD_CREDIT_RELOAD):
                {
                    totals.addAmountGrossGiftCardItemCreditVoided(gross.abs());
                    totals.addUnitsGrossGiftCardItemCreditVoided(units.abs());
                    break;
                }
            }
        }
    }

    /**
     * This method calculates the totals for the sales of gift certificates.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getGiftCertificateFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        totals.addAmountGrossGiftCertificateIssued(gross);
        totals.addUnitsGrossGiftCertificateIssued(units);
    }

    /**
     * This method calculates the totals for the sales of gift certificates in
     * post void transactions.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getVoidGiftCertificateFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        totals.addAmountGrossGiftCertificateIssuedVoided(gross.abs());
        totals.addUnitsGrossGiftCertificateIssuedVoided(units.abs());
    }

    /**
     * This method determines if the current line item was a sale or return and
     * calls the appropriate method to calcualte the totals.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getPLUItemFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        // Determine is this is as sale or return
        boolean isSale = true;
        if (units.signum() < 0)
        {
            isSale = false;
        }

        if (isSale)
        {
            getPLUItemSaleFinancialTotals(totals, gross, units);
        }
        else
        {
            getPLUItemReturnFinancialTotals(totals, gross.abs(), units.abs());
        }
    }

    /**
     * This method calcultes the totals for sale line items, taking into account
     * the taxability of the item.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getPLUItemSaleFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        ItemPriceIfc ip = getItemPrice();
        ItemTaxIfc it = ip.getItemTax();
        ItemClassificationIfc sc = getPLUItem().getItemClassification();
        int taxMode = it.getTaxMode();
        if (taxMode == TaxIfc.TAX_MODE_RETURN_RATE && returnItem.getTaxRate() != 0.00)
        {
            taxMode = TaxIfc.TAX_MODE_STANDARD;
        }

        switch (taxMode)
        {
            case TaxIfc.TAX_MODE_EXEMPT:
            {
                if (!isOrderItem() || isPickedUpOrderItem())
                {
                    totals.addAmountGrossTaxExemptItemSales(gross);
                    totals.addUnitsGrossTaxExemptItemSales(units);
    
                    // since tax-exempt sales are a subset of nontaxable,
                    // the totals are also included for non-taxable sales
                    totals.addAmountGrossNonTaxableItemSales(gross);
                    totals.addUnitsGrossNonTaxableItemSales(units);
    
                    if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                    {
                        totals.addAmountGrossNonTaxableNonMerchandiseSales(gross);
                        totals.addUnitsGrossNonTaxableNonMerchandiseSales(units);
                    }
                }
                break;
            }
            case TaxIfc.TAX_MODE_TOGGLE_OFF:
            case TaxIfc.TAX_MODE_NON_TAXABLE:
            case TaxIfc.TAX_MODE_RETURN_RATE:
            {
                if (!isOrderItem() || isPickedUpOrderItem())
                {
                    totals.addAmountGrossNonTaxableItemSales(gross);
                    totals.addUnitsGrossNonTaxableItemSales(units);
    
                    if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                    {
                        totals.addAmountGrossNonTaxableNonMerchandiseSales(gross);
                        totals.addUnitsGrossNonTaxableNonMerchandiseSales(units);
                    }
                }
                break;
            }
            default:
            {
                if (!isOrderItem() || isPickedUpOrderItem())
                {
                    totals.addAmountGrossTaxableItemSales(gross);
                    totals.addUnitsGrossTaxableItemSales(units);
                }
                totals.addAmountTaxItemSales(ip.getItemTaxAmount());
                totals.addAmountInclusiveTaxItemSales(ip.getItemInclusiveTaxAmount());

                // Save all the separate tax rules into totals
                TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
                TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
                for (int i = 0; i < taxInformation.length; i++)
                {
                    TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
                    container.addTaxTotals(taxTotalsItem);
                }
                totals.addTaxes(container);

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE && (!isOrderItem()||isPickedUpOrderItem()))
                {
                    totals.addAmountGrossTaxableNonMerchandiseSales(gross);
                    totals.addUnitsGrossTaxableNonMerchandiseSales(units);
                }
                break;
            } // end default
        } // end switch
    }

    /**
     * This method calcultes the totals for return line items, taking into
     * account the taxability of the item.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getPLUItemReturnFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        ItemPriceIfc ip = getItemPrice();
        ItemTaxIfc it = ip.getItemTax();
        ItemClassificationIfc sc = getPLUItem().getItemClassification();

        int taxMode = it.getTaxMode();
        if (taxMode == TaxIfc.TAX_MODE_RETURN_RATE && (returnItem.getTaxRate() != 0.00 || it.getTaxableAmount() != 0.00))
        {
            taxMode = TaxIfc.TAX_MODE_STANDARD;
        }

        switch (taxMode)
        { // begin evaluate tax mode

            case TaxIfc.TAX_MODE_EXEMPT:
            {
                totals.addAmountGrossTaxExemptItemReturns(gross.abs());
                totals.addUnitsGrossTaxExemptItemReturns(units.abs());

                // since tax-exempt sales are a subset of nontaxable,
                // the totals are also included for non-taxable sales
                totals.addAmountGrossNonTaxableItemReturns(gross.abs());
                totals.addUnitsGrossNonTaxableItemReturns(units.abs());

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                {
                    totals.addAmountGrossNonTaxableNonMerchandiseReturns(gross.abs());
                    totals.addUnitsGrossNonTaxableNonMerchandiseReturns(units.abs());
                }

                if (ip.getExtendedRestockingFee() != null && ip.getExtendedRestockingFee().signum() != 0)
                {
                    totals.addAmountRestockingFeesFromNonTaxableItems(ip.getExtendedRestockingFee().abs());
                    totals.addUnitsRestockingFeesFromNonTaxableItems(units.abs());
                }
                break;
            }
            case TaxIfc.TAX_MODE_TOGGLE_OFF:
            case TaxIfc.TAX_MODE_NON_TAXABLE:
            case TaxIfc.TAX_MODE_RETURN_RATE:
            {
                totals.addAmountGrossNonTaxableItemReturns(gross.abs());
                totals.addUnitsGrossNonTaxableItemReturns(units.abs());

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                {
                    totals.addAmountGrossNonTaxableNonMerchandiseReturns(gross.abs());
                    totals.addUnitsGrossNonTaxableNonMerchandiseReturns(units.abs());
                }

                if (ip.getExtendedRestockingFee() != null && ip.getExtendedRestockingFee().signum() != 0)
                {
                    totals.addAmountRestockingFeesFromNonTaxableItems(ip.getExtendedRestockingFee().abs());
                    totals.addUnitsRestockingFeesFromNonTaxableItems(units.abs());
                }
                break;
            }
            default:
            {
                totals.addAmountGrossTaxableItemReturns(gross.abs());
                totals.addUnitsGrossTaxableItemReturns(units.abs());
                totals.addAmountTaxItemReturns(ip.getItemTaxAmount().abs());
                totals.addAmountInclusiveTaxItemReturns(ip.getItemInclusiveTaxAmount().abs());

                // Save all the separate tax rules into totals
                TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
                TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
                for (int i = 0; i < taxInformation.length; i++)
                {
                    TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
                    container.addTaxTotals(taxTotalsItem);
                }
                totals.addTaxes(container);

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                {
                    totals.addAmountGrossTaxableNonMerchandiseReturns(gross.abs());
                    totals.addUnitsGrossTaxableNonMerchandiseReturns(units.abs());
                }

                if (ip.getExtendedRestockingFee() != null && ip.getExtendedRestockingFee().signum() != 0)
                {
                    totals.addAmountRestockingFees(ip.getExtendedRestockingFee().abs());
                    totals.addUnitsRestockingFees(units.abs());
                }

                break;
            } // end default
        } // end switch

    }

    /**
     * This method determines if the current line item was a sale or return in a
     * post void transaction and calls the appropriate method to calcualte the
     * totals.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getVoidPLUItemFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        // Determine is this is as sale or return
        boolean isSale = true;
        if (units.signum() > 0)
        {
            isSale = false;
        }

        if (isSale)
        {
            getVoidPLUItemSaleFinancialTotals(totals, gross, units);
        }
        else
        {
            getVoidPLUItemReturnFinancialTotals(totals, gross.abs(), units.abs());
        }
    }

    /**
     * This method calcultes the totals for sale line items in a post void
     * transaction , taking into account the taxability of the item.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getVoidPLUItemSaleFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        ItemPriceIfc ip = getItemPrice();
        ItemTaxIfc it = ip.getItemTax();
        ItemClassificationIfc sc = getPLUItem().getItemClassification();
        int taxMode = it.getTaxMode();
        if (taxMode == TaxIfc.TAX_MODE_RETURN_RATE && returnItem.getTaxRate() != 0.00)
        {
            taxMode = TaxIfc.TAX_MODE_STANDARD;
        }

        switch (taxMode)
        {
            case TaxIfc.TAX_MODE_EXEMPT:
            {
                if (!isOrderItem())
                {
                    totals.addAmountGrossTaxExemptItemSalesVoided(gross.abs());
                    totals.addUnitsGrossTaxExemptItemSalesVoided(units.abs());
    
                    // since tax-exempt sales are a subset of nontaxable,
                    // the totals are also included for non-taxable sales
                    totals.addAmountGrossNonTaxableItemSalesVoided(gross.abs());
                    totals.addUnitsGrossNonTaxableItemSalesVoided(units.abs());
    
                    if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                    {
                        totals.addAmountGrossNonTaxableNonMerchandiseSalesVoided(gross.abs());
                        totals.addUnitsGrossNonTaxableNonMerchandiseSalesVoided(units.abs());
                    }
                }
                break;
            }
            case TaxIfc.TAX_MODE_TOGGLE_OFF:
            case TaxIfc.TAX_MODE_NON_TAXABLE:
            case TaxIfc.TAX_MODE_RETURN_RATE:
            {
                if (!isOrderItem())
                {
                    totals.addAmountGrossNonTaxableItemSalesVoided(gross.abs());
                    totals.addUnitsGrossNonTaxableItemSalesVoided(units.abs());
    
                    if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                    {
                        totals.addAmountGrossNonTaxableNonMerchandiseSalesVoided(gross.abs());
                        totals.addUnitsGrossNonTaxableNonMerchandiseSalesVoided(units.abs());
                    }
                }
                break;
            }
            default:
                if (!isOrderItem())
                {
                    totals.addAmountGrossTaxableItemSalesVoided(gross.abs());
                    totals.addUnitsGrossTaxableItemSalesVoided(units.abs());
                }
                totals.addAmountTaxItemSales(ip.getItemTaxAmount().abs().negate());
                totals.addAmountInclusiveTaxItemSales(ip.getItemInclusiveTaxAmount().abs().negate());

                // Save all the separate tax rules into totals
                TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
                TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
                for (int i = 0; i < taxInformation.length; i++)
                {
                    TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
                    container.addTaxTotals(taxTotalsItem);
                }
                totals.addTaxes(container);

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE && !isOrderItem())
                {
                    totals.addAmountGrossTaxableNonMerchandiseSalesVoided(gross.abs());
                    totals.addUnitsGrossTaxableNonMerchandiseSalesVoided(units.abs());
                }
                break;
        } // end evaluate tax mode
    }

    /**
     * This method calcultes the totals for return line items in a post void
     * transaction , taking into account the taxability of the item.
     *
     * @param totals
     * @param gross
     * @param units
     */
    protected void getVoidPLUItemReturnFinancialTotals(FinancialTotalsIfc totals, CurrencyIfc gross, BigDecimal units)
    {
        ItemPriceIfc ip = getItemPrice();
        ItemTaxIfc it = ip.getItemTax();
        ItemClassificationIfc sc = getPLUItem().getItemClassification();
        int taxMode = it.getTaxMode();
        if (taxMode == TaxIfc.TAX_MODE_RETURN_RATE && returnItem.getTaxRate() != 0.00)
        {
            taxMode = TaxIfc.TAX_MODE_STANDARD;
        }

        switch (taxMode)
        {
            case TaxIfc.TAX_MODE_EXEMPT:

                totals.addAmountGrossTaxExemptItemReturnsVoided(gross.abs());
                totals.addUnitsGrossTaxExemptItemReturnsVoided(units.abs());

                // since tax-exempt sales are a subset of nontaxable,
                // the totals are also included for non-taxable sales
                totals.addAmountGrossNonTaxableItemReturnsVoided(gross.abs());
                totals.addUnitsGrossNonTaxableItemReturnsVoided(units.abs());

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                {
                    totals.addAmountGrossNonTaxableNonMerchandiseReturnsVoided(gross.abs());
                    totals.addUnitsGrossNonTaxableNonMerchandiseReturnsVoided(units.abs());
                }

                if (ip.getExtendedRestockingFee() != null && ip.getExtendedRestockingFee().signum() != 0)
                {
                    totals.addAmountRestockingFeesFromNonTaxableItems(ip.getExtendedRestockingFee().abs().negate());
                    totals.addUnitsRestockingFeesFromNonTaxableItems(units.abs().negate());
                }
                break;

            case TaxIfc.TAX_MODE_TOGGLE_OFF:
            case TaxIfc.TAX_MODE_NON_TAXABLE:
            case TaxIfc.TAX_MODE_RETURN_RATE:

                totals.addAmountGrossNonTaxableItemReturnsVoided(gross.abs());
                totals.addUnitsGrossNonTaxableItemReturnsVoided(units.abs());

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                {
                    totals.addAmountGrossNonTaxableNonMerchandiseReturnsVoided(gross.abs());
                    totals.addUnitsGrossNonTaxableNonMerchandiseReturnsVoided(units.abs());
                }

                if (ip.getExtendedRestockingFee() != null && ip.getExtendedRestockingFee().signum() > 0)
                {
                    totals.addAmountRestockingFeesFromNonTaxableItems(ip.getExtendedRestockingFee().abs().negate());
                    totals.addUnitsRestockingFeesFromNonTaxableItems(units.abs().negate());
                }
                break;

            default:
                totals.addAmountGrossTaxableItemReturnsVoided(gross.abs());
                totals.addUnitsGrossTaxableItemReturnsVoided(units.abs());
                totals.addAmountTaxItemReturns(ip.getItemTaxAmount().abs().negate());
                totals.addAmountInclusiveTaxItemReturns(ip.getItemInclusiveTaxAmount().abs().negate());

                // Save all the separate tax rules into totals
                TaxInformationIfc[] taxInformation = it.getTaxInformationContainer().getTaxInformation();
                TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
                for (int i = 0; i < taxInformation.length; i++)
                {
                    TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
                    container.addTaxTotals(taxTotalsItem);
                }
                totals.addTaxes(container);

                if (sc.getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
                {
                    totals.addAmountGrossTaxableNonMerchandiseReturnsVoided(gross.abs());
                    totals.addUnitsGrossTaxableNonMerchandiseReturnsVoided(units.abs());
                }

                if (ip.getExtendedRestockingFee() != null && ip.getExtendedRestockingFee().signum() != 0)
                {
                    totals.addAmountRestockingFees(ip.getExtendedRestockingFee().abs().negate());
                    totals.addUnitsRestockingFees(units.negate().abs().negate());
                }
                break;
        }
    }

    /**
     * Returns registry.
     *
     * @return registry
     */
    public RegistryIDIfc getRegistry()
    {
        return (registry);
    }

    /**
     * Sets registry.
     *
     * @param reg registry object
     */
    public void setRegistry(RegistryIDIfc reg)
    {
        registry = reg;
    }

    /**
     * Retrieves item quantity.
     *
     * @return item quantity
     */
    public BigDecimal getItemQuantityDecimal()
    {
        return (itemQuantity);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#getItemQuantity()
     */
    public Number getItemQuantity()
    {
        if (!isUnitOfMeasureItem() && getItemQuantityDecimal() != null)
        {
            return getItemQuantityDecimal().toBigInteger();
        }
        return getItemQuantityDecimal();
    }

    /**
     * Sets item quantity, but does not re-calculate totals.
     *
     * @param value new quantity
     * @see #modifyItemQuantity(BigDecimal)
     */
    public void setItemQuantity(BigDecimal value)
    {
        itemQuantity = value;
    }

    /**
     * Retrieves quantity returnable. This is equal to the item quantity less
     * the item quantity returned.
     *
     * @return quantity returnable
     */
    public BigDecimal getQuantityReturnable()
    {
        return itemQuantity.subtract(quantityReturned);
    }

    /**
     * Retrieves quantity returned.
     *
     * @return quantity returned
     */
    public BigDecimal getQuantityReturnedDecimal()
    {
        return (quantityReturned);
    }

    /**
     * Sets quantity returned.
     *
     * @param value new quantity
     */
    public void setQuantityReturned(BigDecimal value)
    {
        quantityReturned = value;
    }

    /**
     * Returns item price object.
     *
     * @return item price object
     */
    public ItemPriceIfc getItemPrice()
    {
        return (itemPrice);
    }

    /**
     * Sets item price attribute.
     *
     * @param value item price reference
     */
    public void setItemPrice(ItemPriceIfc value)
    {
        itemPrice = value;
    }

    /**
     * Returns a boolean indicating whether this is a sale line item. Tests for
     * itemQuantity greater than 0.
     *
     * @return true if itemQuantity > 0
     */
    public boolean isSaleLineItem()
    {
        return (itemQuantity.signum() > 0);
    }

    /**
     * Returns a boolean indicating whether this is a return line item. Tests
     * for itemQuantity less than 0.
     *
     * @return true if itemQuantity < 0, the instance has a ReturnItemIfc
     *         instance, and is not an instance of PriceAdjustmentLineItemIfc
     */
    public boolean isReturnLineItem()
    {
        return (!isPriceAdjustmentLineItem() && itemQuantity.signum() < 0 && returnItem != null);
    }

    /**
     * Returns a boolean indicating whether this is an order line item.
     *
     * @return true if item is an order line item, false otherwise
     */
    public boolean isOrderItem()
    { // begin isOrderItem()
        boolean isOrderFlag = true;
        if (getOrderItemStatus().getStatus().getStatus() == EYSStatusIfc.STATUS_UNDEFINED)
        {
            isOrderFlag = false;
        }
        return (isOrderFlag);
    } // end isOrderItem()
    
    /**
     * Returns a boolean indicating whether this is an order picked up line item.
     *
     * @return true if item is an order line item, false otherwise
     */
    public boolean isPickedUpOrderItem()
    { // begin isPickedUpOrderItem()
        boolean isPickedUpOrderFlag = false;
        if (getOrderItemStatus().getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)
        {
            isPickedUpOrderFlag = true;
        }
        return (isPickedUpOrderFlag);
    } // end isPickedUpOrderItem()

    /**
     * Returns a boolean indicating whether this item has a unit of measure
     * other than the default. Tests for UnitID not equal to "UN".
     *
     * @return true if (! UnitID.equals("UN"))
     */
    public boolean isUnitOfMeasureItem()
    {
        // Delegate
        return DomainUtil.isUnitOfMeasureItem(pluItem);
    }

    /**
     * Returns PLU item.
     *
     * @return PLU item
     */
    public PLUItemIfc getPLUItem()
    {
        return (pluItem);
    }

    /**
     * Returns PLU item identifier.
     *
     * @return PLU item identifier
     */
    public String getPLUItemID()
    {
        if (pluItem != null && pluItemID == null)
        {
            pluItemID = pluItem.getItemID();
        }
        return (pluItemID);
    }

    /**
     * Returns item serial number.
     *
     * @return item serial number
     */
    public String getItemSerial()
    {
        return (itemSerial);
    }

    /**
     * Sets PLU item.
     *
     * @param value PLU item
     */
    public void setPLUItem(PLUItemIfc value)
    {
        pluItem = value;
    }

    /**
     * Sets PLU item ID.
     *
     * @param value PLU item ID
     */
    public void setPLUItemID(String value)
    {
        pluItemID = value;
    }

    /**
     * Sets item serial number.
     *
     * @param value item serial number
     */
    public void setItemSerial(String value)
    {
        itemSerial = value;
    }

    /**
     * Returns return item.
     *
     * @return return item
     */
    public ReturnItemIfc getReturnItem()
    {
        return (returnItem);
    }

    /**
     * Sets return item.
     *
     * @param value return item
     */
    public void setReturnItem(ReturnItemIfc value)
    {
        returnItem = value;
    }

    /**
     * Sets RM Message for this Item
     *
     * @param RMReturnMessageDTO
     */
    public void setReturnMessage(ReturnMessageDTO returnMessage)
    {
        this.returnMessage = returnMessage;
    }

    /**
     * Returns RMMessage for this Item
     *
     * @return RMReturnMessageDTO
     */
    public ReturnMessageDTO getReturnMessage()
    {
        return returnMessage;
    }

    /**
     * Set whether or not this sale item came from an already tendered
     * transaction.
     *
     * @param val true or false
     */
    public void setFromTransaction(boolean val)
    {
        this.fromTransaction = val;
    }

    /**
     * Get whether or not this line item came from an already tendered
     * transaction.
     *
     * @return true or false
     */
    public boolean isFromTransaction()
    {
        return this.fromTransaction;
    }

    /**
     * Returns order item status.
     *
     * @return order item status
     */
    public OrderItemStatusIfc getOrderItemStatus()
    {
        return (orderItemStatus);
    }

    /**
     * Sets order item status.
     *
     * @param value order item status
     */
    public void setOrderItemStatus(OrderItemStatusIfc value)
    {
        orderItemStatus = value;
    }

    /**
     * Returns original order item status
     * 
     * @return original order item status
     */
    public OrderItemStatusIfc getOriginalOrderItemStatus() 
    {
        return originalOrderItemStatus;
    }

    /**
     * Sets original order item status
     * 
     * @param originalOrderItemStatus original order item status
     */
    public void setOriginalOrderItemStatus(
            OrderItemStatusIfc originalOrderItemStatus) 
    {
        this.originalOrderItemStatus = originalOrderItemStatus;
    }

    /**
     * Returns line item reference.
     *
     * @return line item reference
     */
    public String getLineReference()
    {
        return (lineReference);
    }

    /**
     * Sets order item status.
     *
     * @param value order item status
     */
    public void setLineReference(String value)
    {
        lineReference = value;
    }
    
    //----------------------------------------------------------------------------
    /**
        Retrieves order identifier
        @return order identifier
    **/
    //----------------------------------------------------------------------------
    public String getOrderID()
    {                                   // begin getOrderID()
        return(orderID);
    }                                   // end setOrderID()

    //----------------------------------------------------------------------------
    /**
        Sets order identifier
        @param value  order identifier
    **/
    //----------------------------------------------------------------------------
    public void setOrderID(String value)
    {                                   // begin setOrderID()
        orderID = value;
    }                                   // end setOrderID()

    /**
     * Returns order item reference.
     *
     * @return order item reference
     */
    public int getOrderLineReference()
    {
        return (orderLineReference);
    }

    /**
     * Sets order item reference.
     *
     * @param value order item reference
     */
    public void setOrderLineReference(int value)
    {
        orderLineReference = value;
    }

    /**
     * Returns captured order item reference.
     *
     * @return captured order item reference
     */
    public int getCapturedOrderLineReference()
    {
        return capturedOrderLineReference;
    }

    /**
     * Sets captured order item reference.
     *
     * @param value captured order item reference
     */
    public void setCapturedOrderLineReference(int value)
    {
        this.capturedOrderLineReference = value;
    }

    /**
     * Returns entry method.
     *
     * @return entry method
     */
    public EntryMethod getEntryMethod()
    {
        return entryMethod;
    }

    /**
     * Sets entry method.
     *
     * @param value entry method
     */
    public void setEntryMethod(EntryMethod value)
    {
        entryMethod = value;
    }

    /**
     * Retrieves indicator item is eligible for discounting.
     *
     * This simply returns the item's flag.  This does not consider other
     * factors such as external pricing, return item, etc.
     *
     * @return indicator item is eligible for discounting
     * @deprecated as of 13.3 - use preferred isDiscountEligible() -
     *     isDiscountEligible() follows standard naming conventions
     */
    public boolean getDiscountEligible()
    {
        return getPLUItem().getDiscountEligible();
    }

    /**
     * Retrieves indicator item is eligible for discounting.
     *
     * This simply returns the item's flag.  This does not consider other
     * factors such as external pricing, return item, etc.
     *
     * @return indicator item is eligible for discounting
     */
    public boolean isDiscountEligible()
    {
        return getPLUItem().getDiscountEligible();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.AbstractTransactionLineItem#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        boolean isEqual = false;

        try
        {
            SaleReturnLineItem c = (SaleReturnLineItem)obj;

            if (this == obj)
            {
                isEqual = true;
            }
            // compare all the attributes of SaleReturnLineItem
            else if (!super.equals(obj))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getItemQuantityDecimal(), c.getItemQuantityDecimal()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getQuantityReturnedDecimal(), c.getQuantityReturnedDecimal()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(registry, c.getRegistry()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(itemPrice, c.getItemPrice()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(pluItem, c.getPLUItem()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(returnItem, c.getReturnItem()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(advancedPricingDiscount, c.getAdvancedPricingDiscount()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(itemSerial, c.getItemSerial()))
            {
                isEqual = false;
            }
            else if (!sourceAvailable == c.sourceAvailable)
            {
                isEqual = false;
            }
            else if (!itemSendFlag == c.itemSendFlag)
            {
                isEqual = false;
            }
            else if (!fromTransaction == c.fromTransaction)
            {
                isEqual = false;
            }
            else if (!giftReceipt == c.giftReceipt)
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getOrderItemStatus(), c.getOrderItemStatus()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getOriginalOrderItemStatus(), c.getOriginalOrderItemStatus()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getLineReference(), c.getLineReference()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getOrderID(), c.getOrderID()))
            {
                isEqual = false;
            }
            else if (!(orderLineReference == c.getOrderLineReference()))
            {
                isEqual = false;
            }
            else if (!(capturedOrderLineReference == c.getCapturedOrderLineReference()))
            {
                isEqual = false;
            }
            else if (!(kitHeaderReference == c.getKitHeaderReference()))
            {
                isEqual = false;
            }
            else if (!(entryMethod == c.getEntryMethod()))
            {
                isEqual = false;
            }
            else if (this.relatedItemReturnable != c.isRelatedItemReturnable())
            {
                isEqual = false;
            }
            else if (this.relatedItemSequenceNumber != c.getRelatedItemSequenceNumber())
            {
                isEqual = false;
            }
            else if (this.relatedItemDeleteable != c.isRelatedItemDeleteable())
            {
                isEqual = false;
            }
            else if (this.hasReturnItem != c.hasReturnItem())
            {
                isEqual = false;
            }
            else if (this.hasSendItem != c.hasSendItem())
            {
                isEqual = false;
            }
            else if (this.selectedForItemModification != c.isSelectedForItemModification())
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getExternalOrderItemID(), c.getExternalOrderItemID()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(getExternalOrderParentItemID(), c.getExternalOrderParentItemID()))
            {
                isEqual = false;
            }
            else if (this.hasExternalPricing() != c.hasExternalPricing())
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(isExternalOrderItemUpdateSourceFlag(),c.isExternalOrderItemUpdateSourceFlag()))
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(this.receiptDescription, c.getReceiptDescription()))
            {
                isEqual = false;
            }
            else if (isShippingCharge != c.isShippingCharge())
            {
                isEqual = false;
            }
            else if (!Util.isObjectEqual(this.receiptDescriptionLocale, c.getReceiptDescriptionLocale()))
            {
                isEqual = false;
            }
            else if (this.pluDataFromCrossChannelSource != c.isPluDataFromCrossChannelSource())
            {
                isEqual = false;
            }
            else if (this.preSplitLineNumber != c.getPreSplitLineNumber())
            {
                isEqual = false;
            } 
            else if (!this.depositAmount.equals(c.getDepositAmount()))
            {
                isEqual = false;
            }  
            else
            {
                isEqual = true;
            }
        }
        catch (Exception e)
        // catching classcastexceptions is faster than instanceof
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Retrieves itemTaxMethod attribute.
     *
     * @return itemTaxMethod attribute
     */
    public int getItemTaxMethod()
    {
        return itemTaxMethod;
    }

    /**
     * Sets itemTaxMethod attribute.
     *
     * @param value attribute
     */
    public void setItemTaxMethod(int value)
    {
        itemTaxMethod = value;
    }

    /**
     * Sets the available flag for this source. This flag indicates whether the
     * source is currently being used in an advanced pricing strategy.
     *
     * @param value boolean indicating availability
     */
    public void setSourceAvailable(boolean value)
    {
        sourceAvailable = value;
    }

    /**
     * Returns the available flag for this source.
     *
     * @return boolean indicating source availability
     */
    public boolean isSourceAvailable()
    {
        return sourceAvailable // true if this is already not part of a pricing rule
                && (isSaleLineItem() || !isFromTransaction()) // sale items only, unless is a non-receipt return
                && !isUnitOfMeasureItem() // uom items are not allowed as part of rules
                && !isKitHeader() && !isKitComponent() // kit header items are not allowed as part of rules
                && !hasExternalPricing() // items with external pricing (such as items from a Siebel order) are not allowed as part of discount rules
                && (isInStorePriceDuringPickup() || !isPickupCancelLineItem()) // an order pickup or cancel line item which is not in store price cannot be source of advanced discount rules
        		&& !hasNonRetrievedOriginalReceiptId() // non-retireved receipted return
        		&& ((!pluItem.isStoreCoupon()  &&  isDiscountEligible() )|| (pluItem.isStoreCoupon()) );// check discount eligibility for items apart from storecoupon.

    }   
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.DiscountSourceIfc#isSourceAvailableForTransactionDiscounts()
     */
    public boolean isSourceAvailableForTransactionDiscounts()
    {
        return (isSaleLineItem() || !isFromTransaction()) // sale items only, unless is a non-receipt return
                && !isUnitOfMeasureItem() // uom items are not allowed as part of rules
                && !isKitHeader() && !isKitComponent() // kit header items are not allowed as part of rules
                && !hasExternalPricing()  // items with external pricing (such as items from a Siebel order) are not allowed as part of discount rules
                && (isInStorePriceDuringPickup() || !isPickupCancelLineItem()) // an order pickup or cancel line item cannot be source of advanced discount rules
                && ((!pluItem.isStoreCoupon()  &&  isDiscountEligible() )|| (pluItem.isStoreCoupon()) );// check discount eligibility for items apart from storecoupon.
    }

    /**
     * Tests if this object can be used as a target for an advanced pricing
     * rule.
     *
     * @return - boolean indicating whether the target functionality described
     *         by this interface is available.
     */
    public boolean isTargetEnabled()
    {
        return getDiscountEligible() && (advancedPricingDiscount == null) && (!isUnitOfMeasureItem()) &&  
                (!isKitHeader()) && (!isKitComponent()) && !hasExternalPricing() && 
                (isInStorePriceDuringPickup() || !isPickupCancelLineItem()) && 
                (!hasNonRetrievedOriginalReceiptId());
    }

    /**
     * Returns the quantity of items for this target.
     *
     * @return BigDecimal quantity
     */
    public BigDecimal getTargetQuantity()
    {
        return getItemQuantityDecimal();
    }

    /**
     * Returns a String used to test for source or target equality. The String
     * returned is based on the value of the comparisonBasis argument.
     *
     * @param comparisonBasis basis for comparison
     * @return String value to be used for comparison
     */
    public String getComparator(int comparisonBasis)
    {
        String value = null;

        switch (comparisonBasis)
        {
            case COMPARISON_BASIS_ITEM_ID:
                value = getPLUItem().getItemID();
                break;
            case COMPARISON_BASIS_DEPARTMENT_ID:
                value = getPLUItem().getDepartmentID();
                break;
            case COMPARISON_BASIS_MERCHANDISE_CLASS:
                value = getPLUItem().getMerchandiseCodesString();
                break;
            default:
                break;
        }
        return value;
    }

    /**
     * Returns true if specified classification ID is in list, false otherwise
     *
     * @param classificationID classification ID
     * @return true if specified classification ID is in list, false otherwise
     */
    public boolean isClassifiedAs(String classificationID)
    {
        return this.getPLUItem().getItemClassification().isClassifiedAs(classificationID);
    }

    /**
     * Returns the advanced pricing rule which was applied to this target.
     *
     * @return AdvancedPricingRuleIfc
     */
    public ItemDiscountStrategyIfc getAdvancedPricingDiscount()
    {
        return advancedPricingDiscount;
    }

    /**
     * Returns the discountRuleID for this target.
     *
     * @return String ruleID
     */
    public String getAdvancedPricingRuleID()
    {
        return (advancedPricingDiscount == null) ? null : advancedPricingDiscount.getRuleID();
    }

    /**
     * Modifies the price of a DiscountTarget. Applies a discount to the target
     * for an advanced pricing rule.
     *
     * @param discount the rule to apply to this target
     */
    public void applyAdvancedPricingDiscount(ItemDiscountStrategyIfc discount)
    {
        // Do not apply advanced pricing discounts to Gift Cards. This has been
        // discovered to be an issue with Preferred Customer discounts, which are
        // transaction level pricing rules.
            advancedPricingDiscount = discount;
            itemPrice.addItemDiscount(advancedPricingDiscount);
            calculateLineItemPrice();
            setSourceAvailable(false);

    }

    /**
     * Removes an advanced pricing rule that was previously applied to this
     * target.
     */
    public void removeAdvancedPricingDiscount()
    {
        if (advancedPricingDiscount != null)
        {
            List<DiscountRuleIfc> discounts = new ArrayList<DiscountRuleIfc>(Arrays
                    .asList(itemPrice.getItemDiscounts()));
            DiscountRuleIfc rule = null;

            for (Iterator<DiscountRuleIfc> i = discounts.iterator(); i.hasNext();)
            {
                rule = i.next();
                if (rule.getRuleID().equals(advancedPricingDiscount.getRuleID()))
                {
                    i.remove();
                }
            }

            ItemDiscountStrategyIfc[] da = new ItemDiscountStrategyIfc[discounts.size()];
            discounts.toArray(da);
            itemPrice.setItemDiscounts(da);

            advancedPricingDiscount = null;

            calculateLineItemPrice();

            setSourceAvailable(true);

        } // end if (advancedPricingDiscount != null)
    }

    /**
     * Returns true if this PLUItem is a kit header item, false otherwise.
     *
     * @return boolean
     */
    public boolean isKitHeader()
    {
        return false;
    }

    /**
     * Returns true if this PLUItem is a kit component item, false otherwise.
     *
     * @return boolean
     */
    public boolean isKitComponent()
    {
        return false;
    }

    /**
     * Retrieves the kit header reference value if item is part of a kit or -1
     * if it is not.
     *
     * @return int ID
     */
    public int getKitHeaderReference()
    {
        return kitHeaderReference;
    }

    /**
     * Sets the kit header reference value for this kit component. Value is
     * derived from the hashCode of the kit header item.
     *
     * @param id the kit header reference
     */
    public void setKitHeaderReference(int id)
    {
        kitHeaderReference = id;
    }

    /**
     * Retrieves indicator item requires collection of a serial number.
     *
     * @return indicator item requires collection of a serial number
     */
    public boolean isSerializedItem()
    {
        return pluItem.isSerializedItem();
    }

    /**
     * Returns true if this is a non-merchandise item.
     *
     * @return true if non-merchandise (service) item, false if not
     */
    public boolean isServiceItem()
    {
        boolean serviceItem = false;
        if (pluItem != null
                && (pluItem.getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE))
        {
            serviceItem = true;
        }
        return serviceItem;
    }

    /**
     * Retrieves indicator item is eligible for special order.
     *
     * @return indicator item is eligible for special order
     */
    public boolean isSpecialOrderEligible()
    {
        return pluItem.isSpecialOrderEligible();
    }

    /**
     * This method is used as a test method to know if this is a Send Item. It
     * checks if this line item is associated with a particular Send Count
     * greater than 0 and also that it is not a return line item.
     *
     * @return boolean true if this is a send item, false otherwise
     */
    public boolean getItemSendFlag()
    {
        return (itemSendFlag && (sendLabelCount > 0) && !isReturnLineItem());
    }

    /**
     * Sets item send flag attribute.
     *
     * @param value send flag
     */
    public void setItemSendFlag(boolean value)
    {
        itemSendFlag = value;
    }

    /**
     * Returns true if this item can be sent, false otherwise.
     *
     * @return boolean
     */
    public boolean isEligibleForSend()
    {
        if (getPLUItem().getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE)
        {
            return false;
        }
        return true;
    }

    /**
     * Sets the gift receipt flag for this item. This flag indicates whether the
     * item needs to have a gift receipt printed.
     *
     * @param value boolean indicating if a gift receipt should be printed.
     */
    public void setGiftReceiptItem(boolean value)
    {
        giftReceipt = value;
    }

    /**
     * Returns the gift receipt flag for this item.
     *
     * @return boolean indicating if a gift receipt should be printed.
     */
    public boolean isGiftReceiptItem()
    {
        return giftReceipt;

    }

    /**
     * Sets the alteration item flag for this item. This flag indicates whether
     * the item needs to have an alteration receipt printed.
     *
     * @param value boolean indicating if an alteration receipt should be
     *            printed.
     */
    public void setAlterationItemFlag(boolean value)
    {
        alterationItemFlag = value;
    }

    /**
     * Gets the alteration item flag for this item. This flag indicates whether
     * the item needs to have an alteration receipt printed.
     *
     * @return true if an alteration receipt should be printed.
     */
    public boolean getAlterationItemFlag()
    {
        return alterationItemFlag;
    }

    /**
     * Returns the alteration flag for this item.
     *
     * @return Whether the item is an alteration item.
     */
    public boolean isAlterationItem()
    {
        return alterationItemFlag;
    }

    /**
     * This method checks to see if the item is a gift card isssue. We assume it
     * is an issue if the status is active as apposed to reload.
     *
     * @return Whether the item is a gift card issue.
     */
    public boolean isGiftCardIssue()
    {
        boolean returnCode = false;
        PLUItemIfc item = getPLUItem();
        if (item instanceof GiftCardPLUItemIfc)
        {
            GiftCardIfc giftCard = ((GiftCardPLUItemIfc)item).getGiftCard();
            if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_ISSUE)
            {
                returnCode = true;
            }
        }
        return returnCode;
    }

    /**
     * This method checks to see if the item is a gift card reload.
     *
     * @return Whether the item is a gift card reload.
     */
    public boolean isGiftCardReload()
    {
        boolean returnCode = false;
        PLUItemIfc item = getPLUItem();
        if (item instanceof GiftCardPLUItemIfc)
        {
            GiftCardIfc giftCard = ((GiftCardPLUItemIfc)item).getGiftCard();
            if (giftCard.getRequestType() == GiftCardIfc.GIFT_CARD_RELOAD)
            {
                returnCode = true;
            }
        }
        return returnCode;
    }

    /**
     * Check through all discount to see if there is a damage discount related
     * to this item.
     *
     * @return isDamageDiscount boolean
     */
    public boolean hasDamageDiscount()
    {
        boolean returnCode = false;
        ItemDiscountStrategyIfc[] discArray = getItemDiscountsByAmount();
        // check item discounts by amount
        for (int i = 0; i < discArray.length; i++)
        {
            ItemDiscountStrategyIfc discount = discArray[i];
            if (discount.isDamageDiscount())
            {
                returnCode = true;
                break;
            }
        }
        // check item discount by percentage
        if (returnCode == false)
        {
            discArray = getItemDiscountsByPercentage();
            for (int i = 0; i < discArray.length; i++)
            {
                ItemDiscountStrategyIfc discount = discArray[i];
                if (discount.isDamageDiscount())
                {
                    returnCode = true;
                    break;
                }
            }
        }
        return returnCode;
    }

    /**
     * Checks through all discounts to see if there is an employee discount
     * related to this item.
     *
     * @return boolean true only if this item has an employee discount applied
     */
    public boolean hasEmployeeDiscount()
    {
        boolean returnCode = false;
        ItemDiscountStrategyIfc[] discArray = getItemDiscountsByAmount();
        // check item discounts by amount
        for (int i = 0; i < discArray.length; i++)
        {
            ItemDiscountStrategyIfc discount = discArray[i];
            if (discount.getDiscountEmployee() != null && !Util.isEmpty(discount.getDiscountEmployee().getEmployeeID()))
            {
                returnCode = true;
                break;
            }
        }
        // check item discount by percentage
        if (returnCode == false)
        {
            discArray = getItemDiscountsByPercentage();
            for (int i = 0; i < discArray.length; i++)
            {
                ItemDiscountStrategyIfc discount = discArray[i];
                if (discount.getDiscountEmployee() != null
                        && !Util.isEmpty(discount.getDiscountEmployee().getEmployeeID()))
                {
                    returnCode = true;
                    break;
                }
            }
        }
        // check transaction level discounts
        if (returnCode == false)
        {
            discArray = getTransactionDiscounts();
            for (int i = 0; i < discArray.length; i++)
            {
                ItemDiscountStrategyIfc discount = discArray[i];
                if (discount.getDiscountEmployee() != null && !Util.isEmpty(discount.getDiscountEmployee().getEmployeeID()) )
                {
                    returnCode = true;
                    break;
                }
            }
        }
        return returnCode;
    }

    /**
     * This method returns true when: 1) this is a return item, 2)the Sale Return Line Item does 
     * not come from a retrieved transaction, 3) the customer has a reciept, and 4) the operator 
     * keys the reciept ID into the Return Item Info Screen.
     * <p>
     * The purpose of this code is to prevent the application of pricing rules in this instance.
     * The automatic discounts prevent the operator from matching the pricing on the reciept.
     * <p>
     * @return boolean
     */
    protected boolean hasNonRetrievedOriginalReceiptId()
    {
    	boolean receipted = false;
    	
		if (isReturnLineItem() && getReturnItem().isNonRetrievedReceiptedItem())
		{
			receipted = true;
		}
    	
    	return receipted;
    }
    
    /**
     * Check if this item meets the returnable criteria
     *
     * @return is item returnable
     */
    public boolean isReturnable()
    {
        boolean isReturnableItem = false;
        if (getPLUItem() != null)
        {
            // To determine if an item is returnable, must fulfill all the
            // following conditions:
            // 1. is not a return item
            // 2. is return elegible
            // 3. has a return quatity available
            // 4. does not have a damage discount
            // 5. is not a gift certificate
            // 6. is not a gift card
            boolean returnElegible = getPLUItem().getItemClassification().getReturnEligible();
            if (!isReturnLineItem() && returnElegible && (getQuantityReturnable().signum() > 0) && !hasDamageDiscount()
                    && !(getPLUItem() instanceof GiftCertificateItemIfc)
                    && !(getPLUItem() instanceof GiftCardPLUItemIfc))
            {
                isReturnableItem = true;
            }
        }
        return isReturnableItem;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.AbstractTransactionLineItem#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String giftReceiptStr = "false";
        String strResult = new String("Class:  SaleReturnLineItem (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        strResult += "\n" + super.toString();

        strResult += "\n\tQuantity:          [" + itemQuantity + "]";
        strResult += "\n\tQuantity returned: [" + quantityReturned + "]";
        strResult += "\n\tEntry method:      [" + entryMethod + "]";
        strResult += "\n\tItemTaxMethod: [" + ItemTaxIfc.ITEM_TAX_METHOD_DESCRIPTORS[itemTaxMethod] + "]";
        if (giftReceipt)
        {
            giftReceiptStr = "true";
        }
        strResult += "\n\tGift Receipt Flag: [" + giftReceiptStr + "]";
        if (pluItem == null)
        {
            strResult += "\n\n\tPLU Item:\t(null)";
        }
        else
        {
            strResult += "\n\n\t" + pluItem.toString();
        }
        if (itemSerial != null)
        {
            strResult += "\n\tItem Serial Number:" + itemSerial;
        }
        if (returnItem == null)
        {
            strResult += "\n\n\tReturn Item:\t(null)";
        }
        else
        {
            strResult += "\n\n\t" + returnItem.toString();
        }
        if (itemPrice == null)
        {
            strResult += "\n\n\tItem Price:\t(null)";
        }
        else
        {
            strResult += "\n\n\t" + itemPrice.toString();
        }
        if (registry == null)
        {
            strResult += "\n\n\tGift Registry:\t(null)";
        }
        else
        {
            strResult += "\n\n\t" + registry.toString();
        }
        if (advancedPricingDiscount == null)
        {
            strResult += "\n\n\tAdvanced Pricing Discount:\t(null)";
        }
        else
        {
            strResult += "\n\n\t" + advancedPricingDiscount.toString();
        }
        if (orderItemStatus == null)
        {
            strResult += "\n\n\tOrder Item Status:\t(null)";
        }
        else
        {
            strResult += "\n\n\t" + orderItemStatus.toString();
        }
        if (originalOrderItemStatus == null)
        {
            strResult += "\n\n\tOriginal Order Item Status:\t(null)";
        }
        else
        {
            strResult += "\n\n\t" + originalOrderItemStatus.toString();
        }
        strResult += "\n\tLine reference:    [" + getLineReference() + "]";
        strResult += "\n\tOrder id:    [" + getOrderID() + "]";
        strResult += "\n\tOrder line reference:    [" + getOrderLineReference() + "]";
        strResult += "\n\tCaptured Order line reference:    [" + getCapturedOrderLineReference() + "]";
        strResult += "\n\tIs Price Adjustment Line Item:    [" + isPriceAdjustmentLineItem() + "]";
        strResult += "\n\tIs Price Adjustment Line Item Component:    [" + isPartOfPriceAdjustment() + "]";
        strResult += "\n\tRelated Item Sequence Number:    [" + this.getRelatedItemSequenceNumber() + "]";
        strResult += "\n\tRelated Item is Returnable:      [" + this.isRelatedItemReturnable() + "]";
        strResult += "\n\tRelated Item is OnClearance:      [" + this.isOnClearance() + "]";
        strResult += "\n\tLine Item contain Return Item:   [" + this.hasReturnItem() + "]";
        strResult += "\n\tLine Item contain Send Item:   [" + this.hasSendItem() + "]";
        strResult += "\n\tSeclected for Item Modification:   [" + this.isSelectedForItemModification() + "]";
        strResult += "\n\tSeclected for External Item ID:   [" + this.getExternalOrderItemID() + "]";
        strResult += "\n\tSeclected for External Parent Item ID:   [" + this.getExternalOrderParentItemID() + "]";
        strResult += "\n\tSeclected for External Pricing Flag:   [" + this.hasExternalPricing() + "]";
        strResult += "\n\tReceipt Description:   [" + this.getReceiptDescription() + "]";
        strResult += "\n\tReceipt Description Locale:   [" + this.getReceiptDescriptionLocale() + "]";
        strResult += "\n\tIs Shipping Charge Item: [" + this.isShippingCharge() + "]";
        strResult += "\n\tIs From Cross Channel Source: [" + this.pluDataFromCrossChannelSource + "]";
        strResult += "\n\tPre Split Line Number: [" + this.preSplitLineNumber + "]";
        strResult += "\n\tStore Base Currency: [" + this.storeBaseCurrency + "]";
        strResult += "\n\tDeposit Amount: [" + this.depositAmount + "]";

        // pass back result
        return (strResult);
    }

    /**
     * Returns default journal string.
     *
     * @param journalLocale locale received from the client
     * @return default journal string
     */
    public String toJournalString(Locale journalLocale)
    {
        StringBuilder strResult = new StringBuilder();
        ItemPriceIfc ip = getItemPrice();

        int taxMode = ip.getItemTax().getTaxMode();
        int taxScope = ip.getItemTax().getTaxScope();

        // Item number
        CurrencyIfc itemPrice = ip.getExtendedSellingPrice();
        String priceString = itemPrice.toGroupFormattedString().trim();
        String priceStringNegated = itemPrice.negate().toGroupFormattedString();
        int signum = itemPrice.getDecimalValue().signum();

        // Assume quantity is in decimals.
        BigDecimal quantity = getItemQuantityDecimal();
        quantity = quantity.setScale(2);
        if (!isUnitOfMeasureItem())
        {
            // However, if we aren't a UoM item, display quantity as an integer
            if (quantity.intValue() == quantity.doubleValue())
            {
                quantity = quantity.setScale(0);
            }
        }
        String quantityString = quantity.toString();

        // price -part 1
        if (quantityString.startsWith("-"))
        {
            quantityString = quantityString.replace('-', '(');
            quantityString = quantityString + ")";
            if (signum >= 0)
            {
                priceString = priceStringNegated;
            }
        }

        // Tax Mode
        String taxFlag = "T";
        if (taxMode == TaxIfc.TAX_MODE_STANDARD && pluItem.getTaxable() == false)
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
        }
        else
        {
            taxFlag = TaxIfc.TAX_MODE_CHAR[taxMode];
        }

        Object[] dataArgs = new Object[] { pluItem.getItemID(), priceString.trim(), taxFlag };
        strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_ITEM,
                dataArgs, journalLocale));

        // Item description
        strResult.append(Util.EOL).append(pluItem.getDescription(journalLocale));

        // Item size
        if (!Util.isEmpty(itemSizeCode))
        {
            dataArgs[0] = itemSizeCode;
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SIZE_LABEL, dataArgs,
                            journalLocale));
        }

        // Item Quantity and Unit Price
        String sellingPrice = ip.getSellingPrice().toGroupFormattedString();
        Object[] dataArgs2 = new Object[] { quantityString, sellingPrice };
        strResult.append(Util.EOL).append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.QUANTITY_LABEL, dataArgs2,
                        journalLocale));

        String overrideMarker = "";
        if (ip.isPriceOverride())
        {
            overrideMarker = OVERRIDE_MARKER;
        }
        strResult.append(overrideMarker);

        // Item serial number
        if (!Util.isEmpty(itemSerial))
        {
            dataArgs[0] = itemSerial;
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER_LABEL,
                            dataArgs, journalLocale)).append(Util.EOL);
        }

        // if the PLUItem is a GiftCardPLUItem journal gift card information
        if (pluItem instanceof GiftCardPLUItemIfc)
        {
            strResult.append(((GiftCardPLUItemIfc)pluItem).getGiftCard().toJournalString(journalLocale));
        }

        // journal non-standard tax
        if (taxMode != TaxIfc.TAX_MODE_STANDARD)
        {
            if (taxScope == TaxIfc.TAX_SCOPE_ITEM) // tax overirde is at the
                                                   // item level
            {
                strResult.append(ip.getItemTax().toJournalString(journalLocale));
            }
        }

        // Journal return items specific info
        if (isReturnLineItem())
        {
            // journal original Trans.
            ReturnItemIfc returnItem = this.getReturnItem();
            if (returnItem.getOriginalTransactionID() != null)
            {
                dataArgs[0] = returnItem.getOriginalTransactionID().getTransactionIDString();
                strResult.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ORIGINAL_TRANS_LABEL,
                                dataArgs, journalLocale)).append(" ").append(
                        returnItem.getOriginalTransactionID().getBusinessDateString()).append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.RETRIEVED_LABEL, null,
                                journalLocale));
                if (returnItem.isFromRetrievedTransaction())
                {
                    strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                            JournalConstantsIfc.YES_LABEL, null, journalLocale));
                }
                else
                {
                    strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.NO_LABEL,
                            null, journalLocale));
                }

                if (returnItem.isFromGiftReceipt())
                {
                    strResult.append(Util.EOL).append(
                            I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                                    JournalConstantsIfc.GIFT_RECEIPT_LABEL, null, journalLocale));
                }

            }
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_RETURNED_LABEL, null,
                            journalLocale));
        }

        if (getRegistry() != null)
        {
            dataArgs[0] = getRegistry().getID();
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.GIFT_REG_LABEL, dataArgs,
                            journalLocale));
        }

        // if sales associate modified, write it
        if (getSalesAssociateModifiedFlag() && getSalesAssociate() != null)
        {
            dataArgs[0] = getSalesAssociate().getEmployeeID();
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SALES_ASSOC_LABEL,
                            dataArgs, journalLocale));
        }
        else
        {
            ReturnItemIfc ri = getReturnItem();
            // if return, get sales associate
            if (getItemQuantityDecimal().signum() < 0 && ri != null && ri.getSalesAssociate() != null)
            {
                dataArgs[0] = ri.getSalesAssociate().getEmployeeID();
                strResult.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SALES_ASSOC_LABEL,
                                dataArgs, journalLocale));
            }
        }
        // pass back result

        return (strResult.toString());
    }

    /**
     * This method journals the date information.
     *
     * @param date
     * @param journalLocale locale received from the client
     * @return the journal string
     */
    public String toJournalString(EYSDate date, Locale journalLocale)
    {
        String journalString = toJournalString(journalLocale);
        StringBuilder strResult = new StringBuilder(journalString);
        strResult.append(Util.EOL);
        // if the item has a restrictive age
        // journal the date or skipped
        Object[] dataArgs = new Object[] { pluItem.getRestrictiveAge() };
        if (pluItem.getRestrictiveAge() > 0)
        {
            strResult.append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MINIMUM_AGE_LABEL,
                            dataArgs, journalLocale)).append(Util.EOL);

            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.DATE_OF_BIRTH_LABEL, new Object[] { "" }, journalLocale));

            // if year less than 1000 then it was skipped
            if (date.getYear() < 1000)
            {
                strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                        JournalConstantsIfc.SKIPPED_LABEL, null, journalLocale));
            }
            else
            {
                Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
                strResult.append(date.toFormattedString(locale));
            }
        }

        return (strResult.toString());
    }

    /**
     * Journal related item information.
     *
     * @param date
     * @param itemId
     * @param journalLocale locale received from the client
     * @return The journal string
     */
    public String toJournalString(EYSDate date, String itemId, Locale journalLocale)
    {
        String journalString;
        if (date == null)
        {
            journalString = toJournalString(journalLocale);
        }
        else
        {
            journalString = toJournalString(date, journalLocale);
        }
        StringBuilder strResult = new StringBuilder();
        strResult.append(journalString).append(Util.EOL);
        if (itemId != null && relatedItemSequenceNumber > -1)
        {
            Object[] dataArgs = new Object[] { itemId };
            strResult.append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.RELATED_ITEM_FOR_ITEM_LABEL, dataArgs, journalLocale));
        }
        return (strResult.toString());
    }

    /**
     * Returns journal string when removing an item.
     *
     * @param journalLocale locale received from the client
     * @return journal string when removing an item
     */
    public String toJournalDeleteString(Locale journalLocale)
    {

        ItemPriceIfc ip = getItemPrice();
        CurrencyIfc itemPrice = ip.getExtendedSellingPrice();

        String priceString = itemPrice.toGroupFormattedString();
        int signum = itemPrice.getDecimalValue().signum();
        StringBuilder strResult = new StringBuilder();

        // Item number
        strResult.append(toItemJournal(journalLocale));

        BigDecimal quantity = getItemQuantityDecimal();

        Integer qtyInt = Integer.valueOf(quantity.intValue());
        String quantityString = qtyInt.toString();
        if (quantityString.startsWith("-"))
        {
            quantityString = quantityString.substring(1);
            if (signum == CurrencyIfc.NEGATIVE)
            {
                priceString = itemPrice.negate().toGroupFormattedString();
            }
        }
        else
        {
            quantityString = "(" + quantityString + ")";
            if (signum > CurrencyIfc.NEGATIVE)
            {
                priceString = itemPrice.negate().toGroupFormattedString();
            }
        }

        // price
        strResult.append(priceString);

        // Tax flag
        if (this.isReturnLineItem() && this.getTaxable())
        {
            strResult.append(" T");
        }

        // Item description
        strResult.append(Util.EOL).append(pluItem.getDescription(journalLocale));

        // Item Quantity and Unit Price
        Object[] dataArgs2 = new Object[] { quantityString, ip.getSellingPrice().toGroupFormattedString() };
        strResult.append(Util.EOL).append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.QUANTITY_LABEL, dataArgs2,
                        journalLocale));

        // If we have a price override, use the override marker.
        String overrideMarker = "";
        if (ip.isPriceOverride())
        {
            overrideMarker = OVERRIDE_MARKER;
        }
        strResult.append(overrideMarker);

        // Item tax
        Object[] dataArgs = new Object[] { this.getItemTaxAmount().negate() };
        if ((getItemTax() != null) && this.isReturnLineItem() && this.getTaxable())
        {
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_LABEL, dataArgs,
                            journalLocale));
        }

        // Item serial number
        if (itemSerial != null)
        {
            dataArgs[0] = itemSerial;
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER_LABEL,
                            dataArgs, journalLocale)).append(Util.EOL);
        }

        return (strResult.toString());
    }

    /**
     * Journals discounts, if they exist.
     *
     * @param discount the discount to be journaled
     * @param discountRemoved true if discout is being removed, false if added.
     * @param journalLocale locale received from the client
     * @return Journal string.
     */
    public String toJournalManualDiscount(ItemDiscountStrategyIfc discount, boolean discountRemoved,
            Locale journalLocale)
    {
        StringBuilder strJournal = new StringBuilder();
        Object[] discountData = null;
        Object[] discountReasonData = null;

        boolean damageDiscount = false;
        if ((discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_MANUAL)
                && discount.isDamageDiscount())
        {
            damageDiscount = true;
        }

        int method = discount.getDiscountMethod();
        String reason = discount.getReason().getCode();

        String discountDescription = "";
        if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT
                || method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE)
        {
            discountDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.AMOUNT_LABEL, null, journalLocale);
        }
        else if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
        {
            BigDecimal discountRate = discount.getDiscountRate();
            discountRate = discountRate.movePointRight(2);
            discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
            discountDescription = discountRate.toString() + JournalConstantsIfc.PERCENTILE_SYMBOL;
        }
        else
        {
            discountDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.UNKNOWN_LABEL, null, journalLocale);
        }


        CurrencyIfc discountAmount = (CurrencyIfc)discount.getDiscountAmount().clone();
        discountAmount = discountAmount.multiply(getItemQuantityDecimal());
        String price;
        if (discountRemoved)
        {
            price = discountAmount.toGroupFormattedString();
            discountDescription = discountDescription
                    + I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.DELETED_LABEL, null,
                            journalLocale);
        }
        else
        {
            price = discountAmount.negate().toGroupFormattedString();
        }

        String reasonDescription = "";
        if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
        {
            reasonDescription = discount.getDiscountEmployeeID();
        }
        else if (!damageDiscount)
        {
            reasonDescription = reason;
            String reasonCodeText = discount.getReason().getText(journalLocale);
            if (reasonCodeText != null)
            {
                reasonDescription = reasonDescription + "-" + reasonCodeText;
            }
        }

        discountData = new Object[] { discountDescription };
        discountReasonData = new Object[] { reasonDescription };

        String accntDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.DISCOUNT_LABEL, discountData, journalLocale);
        String rsnDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                JournalConstantsIfc.DISCOUNT_RSN_TAG_LABEL, discountReasonData, journalLocale);
        if (discount.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
        {
            accntDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.MARKDOWN_LABEL,
                    discountData, journalLocale);
            rsnDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.MARKDOWN_RSN_LABEL, discountReasonData, journalLocale);
        }
        else if (discount.getAssignmentBasis() == DiscountRuleConstantsIfc.ASSIGNMENT_EMPLOYEE)
        {
            accntDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.EMPLOYEE_DISCOUNT_LABEL, discountData, journalLocale);
            rsnDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.EMPLOYEE_ID1_LABEL, discountReasonData, journalLocale);
        }
        else if (damageDiscount)
        {
            rsnDescription = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE,
                    JournalConstantsIfc.DAMAGE_DISCOUNT_LABEL, null, journalLocale);
        }

        // Put the pieces together
        Object[] dataArgs = new Object[] { pluItem.getItemID(), price };
        strJournal.append(Util.EOL).append(Util.EOL);
        strJournal.append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_DESCID_PRICE, dataArgs,
                        journalLocale)).append(Util.EOL);
        strJournal.append(accntDescription).append(Util.EOL).append(rsnDescription);

        return strJournal.toString();

    }

    /**
     * Method toItemJournal.
     *
     * @param journalLocale locale received from the client
     * @return Journal formatted string for the item number
     */
    private String toItemJournal(Locale journalLocale)
    {
        StringBuilder strJournal = new StringBuilder();
        Object[] dataArgs = new Object[] { pluItem.getItemID() };
        strJournal.append(Util.EOL).append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LABEL, dataArgs,
                        journalLocale)).append(Util.EOL);
        return strJournal.toString();
    }

    /**
     * Returns journal string when returning an item.
     *
     * @param journalLocale locale received from the client
     * @return journal string when returning an item
     */
    public String toJournalRemoveString(Locale journalLocale)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(toJournalDeleteString(journalLocale)).append(Util.EOL).append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_DELETED_LABEL, null,
                        journalLocale));
        return sb.toString();
    }

    /**
     * Retrieves Pos Item identifier.
     *
     * @return Pos Item identifier
     */
    public String getPosItemID()
    {
        String posItemID = "";
        if (pluItem != null)
        {
            posItemID = pluItem.getPosItemID();
        }
        /*
         * POSLog expects POSItemID to have a value, setting posItemID to
         * getItemID() in case value is empty. Specifically, this is to address
         * unknown items sold within POS.
         */
        if ((posItemID == null) || (posItemID.equals("")))
        {
            posItemID = getItemID();
        }
        return posItemID;
    }

    /**
     * Restores the object from the contents of the xml tree based on the
     * current node property of the converter.
     *
     * @param converter is the conversion utility
     * @exception XMLConversionException if error occurs transalating from XML
     */
    public void translateFromElement(XMLConverterIfc converter) throws XMLConversionException
    {
        try
        {
            Element top = converter.getCurrentElement();
            Element[] properties = converter.getChildElements(top, XMLConverterIfc.TAG_PROPERTY);

            // Retrieve and store the values for each property
            for (int i = 0; i < properties.length; i++)
            {
                Element element = properties[i];
                String name = element.getAttribute("name");

                if ("itemQuantity".equals(name))
                {
                    itemQuantity = new BigDecimal(converter.getElementText(element));
                }
                else if ("quantityReturned".equals(name))
                {
                    quantityReturned = new BigDecimal(converter.getElementText(element));
                }
                else if ("registry".equals(name))
                {
                    registry = (RegistryIDIfc)converter.getPropertyObject(element);
                }
                else if ("itemPrice".equals(name))
                {
                    itemPrice = (ItemPriceIfc)converter.getPropertyObject(element);
                }
                else if ("pluItem".equals(name))
                {
                    pluItem = (PLUItemIfc)converter.getPropertyObject(element);
                }
                else if ("returnItem".equals(name))
                {
                    returnItem = (ReturnItemIfc)converter.getPropertyObject(element);
                }
                else if ("salesAssociate".equals(name))
                {
                    salesAssociate = (EmployeeIfc)converter.getPropertyObject(element);
                }
                else if ("lineNumber".equals(name))
                {
                    lineNumber = Integer.valueOf(converter.getElementText(element));
                }
                else
                {
                    // System.out.println(name);
                }
            }
        }
        catch (Exception e)
        {
            throw new XMLConversionException(e.toString());
        }
    }

    /**
     * Returns the item's type.
     *
     * @return The item's type.
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#getItemType()
     */
    public int getItemType()
    {
        return getPLUItem().getItemClassification().getItemType();
    }

    /**
     * @return Returns the itemSizeCode.
     */
    public String getItemSizeCode()
    {
        return itemSizeCode;
    }

    /**
     * @param itemSizeCode The itemSizeCode to set.
     */
    public void setItemSizeCode(String itemSizeCode)
    {
        this.itemSizeCode = itemSizeCode;
    }

    /**
     * Returns true if this item is a price adjustment item, false otherwise.
     *
     * @return boolean
     */
    public boolean isPriceAdjustmentLineItem()
    {
        return isPriceAdjustmentLineItem;
    }

    /**
     * Returns a boolean indicating whether or not this SaleReturnLineItem is a
     * part of a price adjustment
     *
     * @return true only if a this instance is not itself a price adjustment and
     *         the price adjustment reference number is greater than 0
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#isPartOfPriceAdjustment()
     */
    public boolean isPartOfPriceAdjustment()
    {
        boolean isPartOfPriceAdjustment = false;

        // The manually set status trumps the calculation for
        // price adjustment components.
        if (manuallySetPartOfPriceAdjustmentFlag)
        {
            isPartOfPriceAdjustment = this.isPartOfPriceAdjustment;
        }
        else
        {
            isPartOfPriceAdjustment = getPriceAdjustmentReference() > 0 && !isPriceAdjustmentLineItem();
        }

        return (isPartOfPriceAdjustment);
    }

    /**
     * Manually sets flag to indicate whether or not an item is part of a price
     * adjustment See the isPartOfPriceAdjustment() method for more details as
     * to how this status is evaluated. Normally this method is not used.
     * Instead isPartOfPriceAdjustment() uses other data elements to determine
     * the status of this item. This method is intended to be used when you need
     * to explicitly change the status of this item with regards to price
     * adjustments
     *
     * @param isPartOfPriceAdjustment flag indicating whether or not this item
     *            should be viewed as a part of a price adjustment
     */
    public void setIsPartOfPriceAdjustment(boolean isPartOfPriceAdjustment)
    {
        // Indicate that we are manually forcing the status of whether or not
        // this item is a price adjustment
        manuallySetPartOfPriceAdjustmentFlag = true;

        this.isPartOfPriceAdjustment = isPartOfPriceAdjustment;
    }

    /**
     * Sets the isPriceAdjustmentLineItem flag in order to indicate whether this
     * line item is price adjustable. This method is meant to be used only for
     * cloning and interested subclasses.
     *
     * @param isPriceAdjustmentLineItem whether this item is price adjustable
     */
    public void setIsPriceAdjustmentLineItem(boolean isPriceAdjustmentLineItem)
    {
        this.isPriceAdjustmentLineItem = isPriceAdjustmentLineItem;
    }

    /**
     * Returns registry-modified flag.
     *
     * @return registry-modified flag
     */
    public boolean getRegistryModifiedFlag()
    {
        return (registryModifiedFlag);
    }

    /**
     * Sets registry modified flag.
     *
     * @param value modified flag
     */
    public void setRegistryModifiedFlag(boolean value)
    {
        registryModifiedFlag = value;
    }

    /**
     * Returns journal string when removing an item.
     *
     * @param discountType the discount type
     * @param journalLocale locale received from the client
     * @return journal string when removing an item
     */
    public String toJournalDeleteString(int discountType, Locale journalLocale)
    {
        ItemPriceIfc ip = getItemPrice();
        CurrencyIfc itemPrice = ip.getExtendedSellingPrice();


        int signum = itemPrice.getDecimalValue().signum();
        String priceString = itemPrice.toGroupFormattedString();
        ItemDiscountStrategyIfc[] discounts = ip.getItemDiscounts();
        StringBuilder strResult = new StringBuilder();

        // Item number
        Object[] dataArgs = new Object[] { pluItem.getItemID() };
        strResult.append(Util.EOL).append(
                I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.ITEM_LABEL, dataArgs,
                        journalLocale)).append(Util.EOL);

        // Discounts
        if (discounts != null && discounts.length > 0 && discountType != 0)
        {
            journalRemoveDiscounts(ip, discounts, strResult, discountType);
        }
        else
        {
            BigDecimal quantity = getItemQuantityDecimal();
            quantity = quantity.setScale(2);
            String quantityString = quantity.toString();

            if (quantityString.startsWith("-"))
            {
                quantityString = quantityString.substring(1);
                if (signum == CurrencyIfc.NEGATIVE)
                {
                    priceString = itemPrice.negate().toGroupFormattedString();
                }
            }
            else
            {
                quantityString = "(" + quantityString + ")";
                if (signum > CurrencyIfc.NEGATIVE)
                {
                    priceString = itemPrice.negate().toGroupFormattedString();
                }
            }

            strResult.append(priceString);

            // Tax flag
            if (this.isReturnLineItem() && this.getTaxable())
            {
                strResult.append(" T");
            }

            // Item description
            strResult.append(Util.EOL).append(pluItem.getDescription(journalLocale));

            // Item Quantity and Unit Price
            dataArgs[0] = quantityString;
            strResult.append(Util.EOL).append(
                    I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.QUANTITY_LABEL, dataArgs,
                            journalLocale));

            strResult.append(ip.getSellingPrice().toGroupFormattedString());

            // Item tax
            if ((getItemTax() != null) && this.isReturnLineItem() && this.getTaxable())
            {
                dataArgs[0] = this.getItemTaxAmount().negate();
                strResult.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TAX_LABEL, dataArgs,
                                journalLocale));
            }

            // Item serial number
            if (itemSerial != null)
            {
                dataArgs[0] = itemSerial;
                strResult.append(Util.EOL).append(
                        I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.SERIAL_NUMBER_LABEL,
                                dataArgs, journalLocale)).append(Util.EOL);
            }
        }
        // pass back result

        return (strResult.toString());
    }

    /**
     * Journals discounts, if they exist. Due a the addition of Markdowns to
     * discount processing, this appoach to journaling items has been
     * invalidated. Initial journaling of items should alwasy call
     * toJournalString.
     *
     * @param ip ItemPriceIfc object
     * @param discounts array of discounts
     * @param strJournal journal string
     * @param discountType the discount type
     */
    protected void journalDiscounts(ItemPriceIfc ip, ItemDiscountStrategyIfc[] discounts, StringBuilder strJournal,
            int discountType)
    {
        int discountCount = 0;
        List<ItemDiscountStrategyIfc> orderedDiscounts = new ArrayList<ItemDiscountStrategyIfc>();

        // first get the best deal discount if any
        // Calculate the discount that coresponds to the best deal
        ItemDiscountStrategyIfc bd = ip.getBestDealDiscount();
        if (bd != null)
        {
            orderedDiscounts.add(bd);
        }
        ItemDiscountStrategyIfc[] pcdDiscounts = ip.getItemDiscountsByPercentage();
        if (pcdDiscounts != null)
        {
            for (int i = 0; i < pcdDiscounts.length; i++)
            {
                orderedDiscounts.add(pcdDiscounts[i]);
            }
        }
        ItemDiscountStrategyIfc[] amtDiscounts = ip.getItemDiscountsByAmount();
        if (amtDiscounts != null)
        {
            for (int i = 0; i < amtDiscounts.length; i++)
            {
                orderedDiscounts.add(amtDiscounts[i]);
            }
        }
        CurrencyIfc totalItemDiscount = DomainGateway.getBaseCurrencyInstance();
        for (int i = 0; i < orderedDiscounts.size(); i++)
        { // begin handle discounts
            // build discount line(s)
            ItemDiscountStrategyIfc d = orderedDiscounts.get(i);
            int method = d.getDiscountMethod();

            String reason = d.getReason().getCode();

            CurrencyIfc c = null;
            // skip transaction discount audit records
            if (!(d instanceof ItemTransactionDiscountAudit))
            {
                CurrencyIfc tempPrice = ip.getExtendedSellingPrice().subtract(totalItemDiscount);
                c = d.calculateItemDiscount(tempPrice);
                totalItemDiscount = totalItemDiscount.add(c);

                String discountDescription;
                if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT
                        || method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE)
                {
                    c = c.multiply(ip.getItemQuantityDecimal().abs());
                    discountDescription = "Amt.";
                }
                else if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                {
                    BigDecimal discountRate = d.getDiscountRate();
                    discountRate = discountRate.movePointRight(2);
                    discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
                    discountDescription = discountRate.toString() + "%";
                }
                else
                {
                    discountDescription = "Unknown";
                }

                if (discountType == DISCOUNT_BOTH
                        || (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT && method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                        || (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE && method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_FIXED_PRICE)
                        || (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE && method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE))
                {
                    if (discountCount > 0)
                    {
                        strJournal.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID()).append(
                                Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
                    }
                    if (!d.isAdvancedPricingRule())
                    {
                        discountCount++;
                    }

                    // c.setDefaultFormat("(#0.00);#0.00");
                    Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
                    String price = c.toGroupFormattedString();
                    // c.setDefaultFormat(CurrencyIfc.DEFAULT_FORMAT);
                    if (c.signum() > CurrencyIfc.NEGATIVE)
                    {
                        price = c.negate().toGroupFormattedString();
                        strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH + 1));
                    }
                    else
                    {
                        strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH));
                        discountDescription = discountDescription + " Deleted";
                    }
                    if (!d.isAdvancedPricingRule())
                    {
                        strJournal.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID()).append(
                                Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
                        String accntDescription = "  Discount: ";
                        String rsnDescription = "  Disc. Rsn.: ";
                        if (d.getAccountingMethod() == DiscountRuleConstantsIfc.ACCOUNTING_METHOD_MARKDOWN)
                        {
                            accntDescription = "  Markdown: ";
                            rsnDescription = "  Mrkd. Rsn.: ";
                        }
                        strJournal.append(price).append(Util.EOL);
                        strJournal.append(accntDescription);
                        strJournal.append(discountDescription).append(Util.EOL)
                        // need to expand reason
                                .append(rsnDescription).append(
                                        (d.getReason().getText(locale) == null) ? reason : d.getReason()
                                                .getText(locale));
                    }
                }
            }
        } // end handle discounts
    }

    /**
     * Journals removal of discounts, if they exist.
     *
     * @param ip ItemPriceIfc object
     * @param discounts array of discounts
     * @param strJournal journal string
     * @param discountType the discount type
     * @deprecated in 6.0: Due a the addition of Markdowns to discount
     *             processing, this appoach to journaling items has been
     *             invalidated. Initial journaling of items should alwasy call
     *             toJournalString. Manual Item Discounts/Markdowns should call
     *             journalDiscount() and journalRemoveDiscount.
     */
    public void journalRemoveDiscounts(ItemPriceIfc ip, ItemDiscountStrategyIfc[] discounts, StringBuilder strJournal,
            int discountType)
    { // begin journalDiscounts()
        String price;
        int numDiscounts = discounts.length;
        int discountCount = 0;
        for (int i = 0; i < numDiscounts; i++)
        { // begin handle discounts
            // build discount line(s)
            DiscountRuleIfc d = discounts[i];
            int method = d.getDiscountMethod();
            String reason = d.getReason().getCode();
            String reasonText = d.getReason().getText(LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL));

            CurrencyIfc c = null;
            // skip transaction discount audit records
            if (!(d instanceof ItemTransactionDiscountAudit))
            {
                // get data for journaling discount
                ItemDiscountStrategyIfc id = (ItemDiscountStrategyIfc)d;
                c = id.calculateItemDiscount(ip.getExtendedSellingPrice());

                String discountDescription;
                if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                {
                    c = c.multiply(ip.getItemQuantityDecimal().abs());
                    discountDescription = "Amt.";
                }
                else if (method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE)
                {
                    BigDecimal discountRate = d.getDiscountRate();
                    discountRate = discountRate.movePointRight(2);
                    discountRate = discountRate.setScale(0, BigDecimal.ROUND_HALF_UP);
                    discountDescription = discountRate.toString() + "%";
                }
                else
                {
                    discountDescription = "Unknown";
                }

                if (discountType == DISCOUNT_BOTH
                        || (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT && method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_AMOUNT)
                        || (discountType == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE && method == DiscountRuleConstantsIfc.DISCOUNT_METHOD_PERCENTAGE))
                {
                    if (discountCount > 0)
                    {
                        // Item number
                        strJournal.append(Util.EOL).append("ITEM: ").append(pluItem.getItemID()).append(
                                Util.SPACES.substring(pluItem.getItemID().length(), ITEM_NUMBER_LENGTH));
                    }
                    discountCount++;

                    price = c.toGroupFormattedString();
                    if (c.signum() > CurrencyIfc.NEGATIVE)
                    {
                        strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH));
                        discountDescription = discountDescription + " Deleted";
                    }
                    else
                    {
                        strJournal.append(Util.SPACES.substring(price.length(), ITEM_PRICE_LENGTH + 1));
                        discountDescription = discountDescription + " Deleted";
                    }

                    strJournal.append(price).append(Util.EOL).append("  Discount: ").append(discountDescription)
                            .append(Util.EOL)
                            // need to expand reason
                            .append("  Disc. Rsn.: ").append((reasonText == null) ? reason : reasonText);
                }
            }
        } // end handle discounts
    } // end journalRemoveDiscounts()

    /*
     * END DEPRECATED METHODS
     */

    /**
     * @return Returns the taxChanged.
     */
    public boolean isTaxChanged()
    {
        return taxChanged;
    }

    /**
     * @param taxChanged The taxChanged to set.
     */
    public void setTaxChanged(boolean taxChanged)
    {
        this.taxChanged = taxChanged;
    }

    /**
     * Returns the price adjutment line item reference
     *
     * @return Returns the priceAdjustmentReference.
     */
    public int getPriceAdjustmentReference()
    {
        return priceAdjustmentReference;
    }

    /**
     * Sets the price adjustment line item reference
     *
     * @param priceAdjustmentReference The priceAdjustmentReference to set.
     */
    public void setPriceAdjustmentReference(int priceAdjustmentReference)
    {
        this.priceAdjustmentReference = priceAdjustmentReference;
    }

    /**
     * Returns original line number. Not affiliated with the original line
     * number in return item.
     *
     * @return Returns the originalLineNumber or -1 if does not apply
     */
    public int getOriginalLineNumber()
    {
        return originalLineNumber;
    }

    /**
     * Sets original line number. Not affiliated with the original line number
     * in return item.
     *
     * @param originalLineNumber The originalLineNumber to set.
     */
    public void setOriginalLineNumber(int originalLineNumber)
    {
        this.originalLineNumber = originalLineNumber;
    }

    /**
     * Returns original transaction number. Not affiliated with the original
     * transaction number in return item.
     *
     * @return Returns the transactionSequenceNumber or -1 if does not apply
     */
    public long getOriginalTransactionSequenceNumber()
    {
        return transactionSequenceNumber;
    }

    /**
     * Sets original transaction number. Not affiliated with the original
     * transaction number in return item.
     *
     * @param transactionSequenceNumber The transactionSequenceNumber to set.
     */
    public void setOriginalTransactionSequenceNumber(long transactionSequenceNumber)
    {
        this.transactionSequenceNumber = transactionSequenceNumber;
    }

    /**
     * Sets send label count associated with this line item
     *
     * @param sendLabelCount send label count
     */
    public void setSendLabelCount(int sendLabelCount)
    {
        this.sendLabelCount = sendLabelCount;
    }

    /**
     * Gets send label count associated with this line item
     *
     * @return int send label count
     */
    public int getSendLabelCount()
    {
        return this.sendLabelCount;
    }

    /**
     * Get the identifier the uniquely identifies this item
     *
     * @return unique identifier for this tax line item
     */
    public int getLineItemTaxIdentifier()
    {
        return itemPrice.getLineItemTaxIdentifier();
    }

    /**
     * Retrieve the current active tax rules.
     *
     * @return The active tax rules
     */
    public RunTimeTaxRuleIfc[] getActiveTaxRules()
    {
        RunTimeTaxRuleIfc[] taxRules = null;
        // if it is a retrieved return item never been suspended
        if (isReturnLineItem() && getReturnItem() != null && getReturnItem().isItemTaxRetrieved())
        {
            taxRules = getRetrievedReturnTaxRules();
        }
        // if it is a line item retrieved from db, such as a voided item,
        // a retrieved return item from suspension, layway retrieval, or order
        // retrieval, or if the line item has tax calculated from an external 
        // source (such as CSC), preserve the tax amount, donot recalculate.
        else if (isFromTransaction() || hasExternalTax())
        {
            taxRules = getReverseTaxRules();
        }
        // Kit headers have no rules
        else if (isKitHeader())
        {
            taxRules = new RunTimeTaxRuleIfc[0];
        }
        else
        {
            taxRules = itemPrice.getActiveTaxRules();

            if (taxRules == null && pluItem != null)
            {
                taxRules = pluItem.getTaxRules();
            }
            // Use default tax rules if we don't find one, unless its a kit
            // header which is expected
            // to have no rules (the individual kit items have the rules)
            if (taxRules == null)
            {
                logger.info("Using Default tax rules, plu did not have any tax rules");
                taxRules = getDefaultTaxRules();
            }
        }

        return taxRules;
    }

    /**
     * Get the default tax rules, when none can be found in the DB
     *
     * @return list of tax rules
     * @see oracle.retail.stores.domain.lineitem.TaxLineItemInformationIfc#getDefaultTaxRules()
     */
    public TaxRuleIfc[] getDefaultTaxRules()
    {
        return itemPrice.getDefaultTaxRules();
    }

    /**
     * Tax rules applied on reverse transactions other than returns
     *
     * @return Array of revers item tax rules
     */
    protected ReverseItemTaxRuleIfc[] getReverseTaxRules()
    {
        if (reverseTaxRules == null)
        {
            List<ReverseItemTaxRuleIfc> rules = new ArrayList<ReverseItemTaxRuleIfc>();
            if (itemPrice != null && itemPrice.getItemTax() != null
                    && itemPrice.getItemTax().getTaxInformationContainer() != null)
            {
                TaxInformationIfc[] originalTaxes = itemPrice.getItemTax().getTaxInformationContainer()
                        .getTaxInformation();
                if (originalTaxes != null)
                {
                    for (int i = 0; i < originalTaxes.length; i++)
                    {
                        ReverseItemTaxRuleIfc taxRule = DomainGateway.getFactory().getReturnItemTaxRuleInstance();
                        taxRule.setOrder(i);
                        taxRule.setTaxRuleName(originalTaxes[i].getTaxRuleName());
                        taxRule.setTaxAuthorityName(originalTaxes[i].getTaxAuthorityName());
                        taxRule.setInclusiveTaxFlag(originalTaxes[i].getInclusiveTaxFlag());
                        taxRule.setUniqueID(String.valueOf(taxRule.hashCode()));
                        ReverseTaxCalculatorIfc calculator = DomainGateway.getFactory()
                                .getReverseTaxCalculatorInstance();
                        taxRule.setTaxCalculator(calculator);
                        calculator.setCalculationParameters(new TaxInformationIfc[] { originalTaxes[i] });
                        rules.add(taxRule);
                    }
                }
            }
            else
            {
                logger.error("Could not set up return item tax rule.  Either itemPrice, itemTax, or the tax information container is null.");
            }

            this.reverseTaxRules = rules.toArray(new ReverseItemTaxRuleIfc[rules.size()]);
        }
        return this.reverseTaxRules;

    }

    /**
     * Tax rules applied on returns
     *
     * @return Array of reverse itemtax rules
     */
    protected ReverseItemTaxRuleIfc[] getRetrievedReturnTaxRules()
    {
        if (reverseTaxRules == null || (reverseTaxRules.length > 0 && !reverseTaxRules[0].isReturn()))
        {
            List<ReverseItemTaxRuleIfc> rules = new ArrayList<ReverseItemTaxRuleIfc>();
            if (returnItem != null && returnItem.getItemTax() != null
                    && returnItem.getItemTax().getTaxInformationContainer() != null)
            {
                TaxInformationIfc[] originalTaxes = returnItem.getItemTax().getTaxInformationContainer()
                        .getTaxInformation();
                if (originalTaxes != null)
                {
                    for (int i = 0; i < originalTaxes.length; i++)
                    {
                        ReverseItemTaxRuleIfc taxRule = DomainGateway.getFactory().getReturnItemTaxRuleInstance();
                        taxRule.setOrder(i);
                        taxRule.setTaxRuleName(originalTaxes[i].getTaxRuleName());
                        taxRule.setTaxAuthorityName(originalTaxes[i].getTaxAuthorityName());
                        taxRule.setInclusiveTaxFlag(originalTaxes[i].getInclusiveTaxFlag());
                        // Every return item must be unique, otherwise they get
                        // merged into one taxRuleContainer and the wrong
                        // calculator
                        // is used for some items
                        taxRule.setUniqueID(String.valueOf(taxRule.hashCode()));
                        ReturnTaxCalculatorIfc calculator = DomainGateway.getFactory().getReturnTaxCalculatorInstance();
                        calculator.setCalculationParameters(new TaxInformationIfc[] { originalTaxes[i] });
                        if (returnItem != null)
                        {
                            calculator.setQuantityReturnable(returnItem.getQuantityReturnable());
                            calculator.setQuantityPurchased(returnItem.getQuantityPurchased());
                            calculator.setQuantityBeingReturned(returnItem.getItemQuantity());
                        }
                        taxRule.setTaxCalculator(calculator);
                        taxRule.setReturn(true);
                        rules.add(taxRule);
                    }
                }
            }
            else
            {
                logger.error("Could not set up return item tax rule.  Either itemPrice, itemTax, or the tax information container is null.");
            }

            this.reverseTaxRules = rules.toArray(new ReverseItemTaxRuleIfc[rules.size()]);
        }
        return reverseTaxRules;
    }

    /**
     * Can the transaction override the tax rules on this item. A transaction
     * override should not affect some items. For example if the line item is a
     * return that was retrieved then the tax should stay the same for that line
     * item.
     *
     * @return True if can override the items tax rules. False, otherwise.
     */
    public boolean canTransactionOverrideTaxRules()
    {
        boolean transactionCanOverride = false;

        // Tax exempt is on transaction level only, so this must return true or
        // the tax exempt rules wont take, they are never associated with an
        // item level
        if (getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT)
        {
            transactionCanOverride = true;
        }
        // If this is true, transaction level tax override was specifically set.
        // We must do this
        // Or else getActiveTaxRules on the item level will get the overridden
        // transaction tax and
        // treat it as a line item override.
        if (getTaxScope() == TaxConstantsIfc.TAX_SCOPE_TRANSACTION)
        {
            transactionCanOverride = true;
        }
        
        if (!isTaxOverrideEligible())
        {
            transactionCanOverride = false;
        }

        return transactionCanOverride;
    }
    
    /**
     * Can the transaction exempt the tax rules on this item.
     * A transaction exempt should not affect some items.  For
     * example if the line item is a return that was retrieved
     * then the tax should stay the same for that line item; or 
     * if the line item is an order pickup cancel item that is 
     * not repriced.
     *
     * @return True if can exempt the items tax rules.  False, otherwise.
     */
    public boolean canTransactionExemptTaxRules()
    {
        boolean transactionCanExempt = false;

        // Tax exempt is on transaction level only, so this must return true or
        // the tax exempt rules wont take, they are never associated with an
        // item level
        if (getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT)
        {
            transactionCanExempt = true;
        }
        
        if (!isTaxExemptionEligible())
        {
            transactionCanExempt = false;
        }
        
        return transactionCanExempt;
    }
    
    /**
     * returns a boolean flag indicating if tax override is eligible
     * @return true if tax override is eligible; false otherwise
     */
    public boolean isTaxOverrideEligible()
    {
        boolean isEligible = true;

        if (getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE)
        {
            isEligible = false;
        }
        else if (isReturnLineItem() && getReturnItem().isFromRetrievedTransaction())
        {
            isEligible = false;
        }
        else if (getItemSendFlag())
        {
            isEligible = false;
        }
        else if (isPickupCancelLineItem() && !isInStorePriceDuringPickup())
        {
            isEligible = false;
        }

        return isEligible;
    }
    
    /**
     * returns a boolean flag indicating if tax exemption is eligible
     * @return true if tax exemption is eligible; false otherwise
     */
    public boolean isTaxExemptionEligible()
    {
        boolean isEligible = true;

        if (getTaxMode() == TaxConstantsIfc.TAX_MODE_NON_TAXABLE)
        {
            isEligible = false;
        }
        else if (isReturnLineItem() && getReturnItem().isFromRetrievedTransaction())
        {
            isEligible = false;
        }
        else if (isPickupCancelLineItem() && !isInStorePriceDuringPickup())
        {
            isEligible = false;
        }

        return isEligible;
    }

    /**
     * Retrieve the tax information container that the tax calculation results
     * should be placed.
     *
     * @return The Tax Information Container
     */
    public TaxInformationContainerIfc getTaxInformationContainer()
    {
        TaxInformationContainerIfc taxInformationContainer = null;
        if (itemPrice != null && itemPrice.getItemTax() != null)
        {
            taxInformationContainer = itemPrice.getItemTax().getTaxInformationContainer();
        }

        return taxInformationContainer;
    }

    /**
     * Returns if item is a giftcard merch type
     *
     * @return True if PLU item is a gift item
     */
    public boolean isGiftItem()
    {
        if (getPLUItem().getProductGroupID().equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD))
            return true;

        return false;
    }

    /**
     * Checks the pluItem is gift certificate or gift card plu item. These items
     * will not treat as unit of sale.
     *
     * @param pluItem PLUItemIfc
     * @return isGiftPLUItem
     */
    protected boolean isGiftItem(PLUItemIfc pluItem)
    {
        boolean isGiftPLUItem = false;

        if (pluItem instanceof GiftCardPLUItemIfc || pluItem instanceof GiftCertificateItemIfc)
        {
            isGiftPLUItem = true;
        }
        return isGiftPLUItem;
    }

    /**
     * Retrieves indicator item is eligible for employee discounting.
     *
     * This simply returns the item's flag.  This does not consider other
     * factors such as external pricing, return item, etc.
     *
     * @return indicator item is eligible for employee discounting
     */
    public boolean isEmployeeDiscountEligible()
    {
        return getPLUItem().getEmployeeDiscountEligible();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#isDamageDiscountEligible()
     */
    public boolean isDamageDiscountEligible()
    {
        return getPLUItem().getDamageDiscountEligible();
    }
    
    //----------------------------------------------------------------------------
    /**
     * Retrieve indicator item is totalable in transaction totals
     * @return indicator item is totalable in transaction totals
     */
    //----------------------------------------------------------------------------
    public boolean isTotalable()
    {
        return true;
    }

    /**
     * Tell whether or not this item is returnable, assuming it is a related
     * item.
     *
     * @return true or false
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#isRelatedItemReturnable()
     * @since NEP67
     */
    public boolean isRelatedItemReturnable()
    {
        return this.relatedItemReturnable;
    }

    /**
     * Set this related item as being returnable or not
     *
     * @param value true or false
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#setRelatedItemReturnable(boolean)
     * @since NEP67
     */
    public void setRelatedItemReturnable(boolean value)
    {
        this.relatedItemReturnable = value;
    }

    /**
     * Set the sequence number this line item is related to. This should be -1
     * if this is not a related item.
     *
     * @param seqNum Sequence number.
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#setRelatedItemSequenceNumber(int)
     * @since NEP67
     */
    public void setRelatedItemSequenceNumber(int seqNum)
    {
        this.relatedItemSequenceNumber = seqNum;
    }

    /**
     * Get the sequence number this line item is related to. This should be -1
     * if this is not a related item.
     *
     * @return sequence number
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#getRelatedItemSequenceNumber()
     * @since NEP67
     */
    public int getRelatedItemSequenceNumber()
    {
        return this.relatedItemSequenceNumber;
    }

    /**
     * Flag that tracks whether or not this item is deleteable. This value is
     * persisted to the database, for suspend/retrieve transactions, but is not
     * used in the POSLog.
     *
     * @return true or false.
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#isRelatedItemDeleteable()
     * @since NEP67
     */
    public boolean isRelatedItemDeleteable()
    {
        return this.relatedItemDeleteable;
    }

    /**
     * Set whether or not this related item is deleteable. This value is
     * persisted to the database, for suspend/retrieve transactions, but is not
     * used in the POSLog.
     *
     * @param relatedItemDeleteable
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#setRelatedItemDeleteable(boolean)
     * @since NEP67
     */
    public void setRelatedItemDeleteable(boolean relatedItemDeleteable)
    {
        this.relatedItemDeleteable = relatedItemDeleteable;
    }

    /**
     * Adds a related item line item to the vector.
     *
     * @param lineItem
     */
    public void addRelatedItemLineItem(SaleReturnLineItemIfc lineItem)
    {
        if (relatedItemLineItems == null)
        {
            relatedItemLineItems = new ArrayList<SaleReturnLineItemIfc>();
        }
        relatedItemLineItems.add(lineItem);
    }

    /**
     * Returns the vector of related item line items.
     *
     * @return Array of sale return line items
     */
    public SaleReturnLineItemIfc[] getRelatedItemLineItems()
    {
        SaleReturnLineItemIfc[] relatedItems = null;
        if (relatedItemLineItems != null)
        {
            // create an array containing line items
            relatedItems = relatedItemLineItems.toArray(new SaleReturnLineItem[0]);
        }

        return relatedItems;
    }

    /**
     * Sets the related item line items.
     *
     * @param relatedItems
     */
    public void setRelatedItemLineItems(SaleReturnLineItemIfc[] relatedItems)
    {
        if (relatedItemLineItems == null)
        {
            relatedItemLineItems = new ArrayList<SaleReturnLineItemIfc>();
        }
        else
        {
            relatedItemLineItems.clear();
        }
        relatedItemLineItems.addAll(Arrays.asList(relatedItems));
    }

    /**
     * Retuns all the promotionLineItems for the Sale Return Line Item
     *
     * @return Returns the promotionLineItems.
     */
    public PromotionLineItemIfc[] getPromotionLineItems()
    {
        return itemPrice.getPromotionLineItems();
    }

    /**
     * Adds a Promotion Line Item to the Sale Return Line Item
     *
     * @param promotionLineItem
     */
    public void addPromotionLineItem(PromotionLineItemIfc promotionLineItem)
    {
        itemPrice.addPromotionLineItem(promotionLineItem);
    }

    /**
     * Sets flag if this Sale Return Line Item has had a pricing modification.
     *
     * @param hasPriceModification
     */
    public void setHasPriceModification(boolean hasPriceModification)
    {
        this.hasPriceModification = hasPriceModification;
    }

    /**
     * Returns flag if this Sale Return Line Item has had a pricing
     * modification.
     *
     * @return True if price modified
     */
    public boolean hasPriceModification()
    {
        return this.hasPriceModification;
    }

    /**
     * Returns the Item Level Receipt message if the item is not a duplicate.
     * returns "" if the item Message does not exist or if the item is a
     * duplicate
     * @return String
     * {@link #getItemReceiptMessage(Locale)}
     */
    public String getItemReceiptMessage()
    {
        if (isReturnLineItem())
        {
            return getItemReceiptMessage(getReceiptDescriptionLocale());
        }

        return getItemReceiptMessage(getReceiptDescriptionLocale());
    }

    /**
     * Returns the Item Level Receipt message if the item is not a duplicate for a given locale.
     * returns "" if the item Message does not exist or if the item is a
     * duplicate
     * @param locale Locale
     * @return String
     */
    public String getItemReceiptMessage(Locale locale)
    {
        if (isReturnLineItem())
        {
            return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.RETURN, ItemLevelMessageConstants.RECEIPT, locale);
        }

        return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.SALE, ItemLevelMessageConstants.RECEIPT, locale);
    }
    
    /**
     * Returns the Item Level Footer message if the item is not a duplicate.
     * returns "" if the item Message does not exist or if the item is a
     * duplicate
     *
     * @return String
     */
    public String getItemFooterMessage()
    {
        if (isReturnLineItem())
        {
            return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.RETURN, ItemLevelMessageConstants.FOOTER);
        }

        return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.SALE, ItemLevelMessageConstants.FOOTER);
    }
    
    /**
     * Returns the Item Level Footer message if the item is not a duplicate for a given locale
     * returns "" if the item Message does not exist or if the item is a
     * duplicate
     * @param locale Locale
     * @return String
     */
    public String getItemFooterMessage(Locale locale)
    {
        if (locale != null)
        {
            if (isReturnLineItem())
            {
                return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.RETURN,
                        ItemLevelMessageConstants.FOOTER, locale);
            }

            return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.SALE, ItemLevelMessageConstants.FOOTER, locale);
        }
        else
        {
            if (isReturnLineItem())
            {
                return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.RETURN,
                        ItemLevelMessageConstants.FOOTER, getReceiptDescriptionLocale());
            }

            return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.SALE, ItemLevelMessageConstants.FOOTER, getReceiptDescriptionLocale());
        }
    }
    
    
    /**
     * @return String
     */
    public String getReturnResponseItemId()
    {

        if (getReturnMessage().getReturnMessage() != null)
        {
            return getPLUItemID();
        }

        return "";
    }

    /**
     * Returns the Item Level Rebate message if the item is not a duplicate.
     * returns "" if the item Message does not exist or if the item is a
     * duplicate
     * @return String
     * {@link #getItemRebateMessage(Locale)}
     */
    public String getItemRebateMessage()
    {
        if (isReturnLineItem())
        {
            return getItemRebateMessage(getReceiptDescriptionLocale());
        }

        return getItemRebateMessage(getReceiptDescriptionLocale());
    }

    /**
     * Returns the Item Level Rebate message if the item is not a duplicate for a given locale.
     * returns "" if the item Message does not exist or if the item is a
     * duplicate
     * @param locale Locale
     * @return String
     */
    public String getItemRebateMessage(Locale locale)
    {
        if (isReturnLineItem())
        {
            return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.RETURN, ItemLevelMessageConstants.REBATE, locale);
        }

        return getPLUItem().getItemLevelMessage(ItemLevelMessageConstants.SALE, ItemLevelMessageConstants.REBATE, locale);
    }
    /**
     * Returns the Item Level Message Code ID for a given Transaction Type and
     * Message Type
     *
     * @return String
     */
    public String getItemMessageID(String transactionType, String messageType)
    {
        return getPLUItem().getItemLevelMessageCodeID(transactionType, messageType);
    }

    /**
     * @return ReturnMessage
     */
    public String getItemReturnResponseMessage()
    {
        if (!getReturnMessage().isDuplicate())
        {
            return (getReturnMessage().getReturnMessage());
        }

        return "";
    }

    /**
     * Sets flag if this Sale Return Line Item contain return item.
     *
     * @param hasReturnItem
     */
    public void setHasReturnItem(boolean hasReturnItem)
    {
        this.hasReturnItem = hasReturnItem;
    }

    /**
     * Returns flag if this Sale Return Line Item contain return item.
     *
     * @return True if return item
     */
    public boolean hasReturnItem()
    {
        return this.hasReturnItem;
    }

    /**
     * Sets flag if this Sale Return Line Item contain send item.
     *
     * @param hassendItem
     */
    public void setHasSendItem(boolean hassendItem)
    {
        this.hasSendItem = hassendItem;
    }

    /**
     * Returns flag if this Sale Return Line Item contain send item.
     *
     * @return True if send item
     */
    public boolean hasSendItem()
    {
        return this.hasSendItem;
    }

    /**
     * @return Returns the selectedForItemModification.
     */
    public boolean isSelectedForItemModification()
    {
        return selectedForItemModification;
    }

    /**
     * @param selectedForItemModification The selectedForItemModification to
     *            set.
     */
    public void setSelectedForItemModification(boolean selectedForItemModification)
    {
        this.selectedForItemModification = selectedForItemModification;
    }

    /**
     * Gets the IMEI Scanned Number
     * @return String
     */
    public String getItemIMEINumber()
    {
        return itemIMEINumber;
    }

    /**
     * Sets the IMEI Scanned Number
     * @param imeiNumber
     */
    public void setItemIMEINumber(String itemIMEINumber)
    {
        this.itemIMEINumber = itemIMEINumber;
    }

    /**
     * @return a flag indicating if this line item is from an external order
     */
    public boolean isFromExternalOrder()
    {
        return !StringUtils.isBlank(getExternalOrderItemID());
    }

    /**
     * @return external order item id
     */
    public String getExternalOrderItemID()
    {
        return externalOrderItemID;
    }

    /**
     * Sets the external order item id
     * @param externalOrderItemId the external order item id
     */
    public void setExternalOrderItemID(String externalOrderItemID)
    {
        this.externalOrderItemID = externalOrderItemID;
    }

    /**
     * @return the external order parent item id
     */
    public String getExternalOrderParentItemID()
    {
        return externalOrderParentItemID;
    }

    /**
     * Sets the external order parent item id
     * @param externalOrderParentItemId the external order parent item id
     */
    public void setExternalOrderParentItemID(String externalOrderParentItemID)
    {
        this.externalOrderParentItemID = externalOrderParentItemID;
    }

    /**
     * @return the flag indiciating if the pricing is set by an external system
     */
    public boolean hasExternalPricing()
    {
        return externalPricingFlag;
    }

    /**
     * Sets the flag indicating if the pricing is set by an external system
     * @param externalPricingFlag
     */
    public void setExternalPricingFlag(boolean externalPricingFlag)
    {
        this.externalPricingFlag = externalPricingFlag;
    }
    
    /**
     * @return the flag indiciating if the tax is set by an external system
     */
    public boolean hasExternalTax() 
    {
        return externalTaxFlag;
    }

    /**
     * Sets the flag indicating if the tax is set by an external system
     * @param externalTaxFlag
     */
    public void setExternalTaxFlag(boolean externalTaxFlag) 
    {
        this.externalTaxFlag = externalTaxFlag;
    }

    /**
     * @return Returns the receiptDescription.
     */
    public String getReceiptDescription()
    {
        return receiptDescription;
    }

    /**
     * @param receiptDescription The receiptDescription to set.
     */
    public void setReceiptDescription(String customerItemDescription)
    {
        this.receiptDescription = customerItemDescription;
    }

    /**
     * @return Returns the customerItemDescriptionLocale.
     */
    public Locale getReceiptDescriptionLocale()
    {
        return receiptDescriptionLocale;
    }

    /**
     * Sets the locale that will be used to print the receipt for the customer.
     * This method also applies the receipt locale onto any active promotion.
     *
     * @param receiptLocale The locale to set.
     * @see PromotionLineItemIfc#setReceiptLocale(Locale)
     */
    public void setReceiptDescriptionLocale(Locale receiptLocale)
    {
        this.receiptDescriptionLocale = receiptLocale;
        if (itemPrice.getAppliedPromotion() != null)
        {
            itemPrice.getAppliedPromotion().setReceiptLocale(receiptLocale);
        }
    }

    /**
     * This method gets the short description from the PLUItem then sets the
     * receipt locale.
     *
     * @param receiptLocale the locale used to print the receipt.
     * @see #setReceiptDescription(String)
     * @see #setReceiptDescriptionLocale(Locale)
     */
    public void setReceiptDescriptionFromPLUItem(Locale receiptLocale)
    {
        setReceiptDescription(pluItem.getShortDescription(receiptLocale));
        setReceiptDescriptionLocale(receiptLocale);
    }

    /**
     * Returns a flag that indicates if the external order
     * item needs to be updated in the external system
     * @return the externalOrderItemUpdateFlag
     */
    public boolean isExternalOrderItemUpdateSourceFlag()
    {
        return externalOrderItemUpdateSourceFlag;
    }

    /**
     * Sets the external order item update flag
     * @param externalOrderItemUpdateFlag the externalOrderItemUpdateFlag to set
     */
    public void setExternalOrderItemUpdateSourceFlag(boolean externalOrderItemUpdateFlag)
    {
        this.externalOrderItemUpdateSourceFlag = externalOrderItemUpdateFlag;
    }

     /**
     * Returns a flag that indicates if the line
     * item is a shipping charge line
     * @return the isShippingCharge flag
    */
    public boolean isShippingCharge()
    {
        return isShippingCharge;
    }

    /**
    *
    * @param setShippingCharge flag the setShippingCharge to set
    */
    public void setShippingCharge(boolean isShippingCharge)
    {
        this.isShippingCharge = isShippingCharge;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#isPluDataFromCrossChannelSource()
     */
    public boolean isPluDataFromCrossChannelSource()
    {
        return pluDataFromCrossChannelSource;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#setPluDataFromCrossChannelSource(boolean)
     */
    public void setPluDataFromCrossChannelSource(
            boolean pluDataFromCrossChannelSource)
    {
        this.pluDataFromCrossChannelSource = pluDataFromCrossChannelSource;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#getPreSplitTransactionSequenceNumber()
     */
    public int getPreSplitLineNumber()
    {
        return preSplitLineNumber;
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc#setPreSplitTransactionSequenceNumber(long)
     */
    public void setPreSplitLineNumber(
            int preSplitTransactionSequenceNumber)
    {
        this.preSplitLineNumber = preSplitTransactionSequenceNumber;
    }

    /**
     * Returns financial totals for the line item.
     *
     * @param isNotVoid boolean
     * @return totals FinancialTotalsIfc object for this line item
     */
    public void getShippingChargeItemSaleOrVoidFinancialTotals(FinancialTotalsIfc totals, boolean isNotVoid)
    {
        ItemTaxIfc itemTax = getItemTax();

        // Note: For a post void transaction, the shipping charges and shipping
        // charges
        // tax have already been negated in method setVoidTransactionTotals
        totals.addAmountShippingCharges(getExtendedDiscountedSellingPrice());
        totals.addAmountTaxShippingCharges(itemTax.getItemTaxAmount());
        totals.addAmountInclusiveTaxShippingCharges(itemTax.getItemInclusiveTaxAmount());
        int numberShippingCharges = isNotVoid ? 1 : -1;
        totals.addNumberShippingCharges(numberShippingCharges);

        // Save all the separate tax rules into totals
        TaxInformationIfc[] taxInformation = itemTax.getTaxInformationContainer().getTaxInformation();
        TaxTotalsContainerIfc container = DomainGateway.getFactory().getTaxTotalsContainerInstance();
        for (int i = 0; i < taxInformation.length; i++)
        {
            TaxTotalsIfc taxTotalsItem = new TaxTotals(taxInformation[i]);
            container.addTaxTotals(taxTotalsItem);
        }
        totals.addTaxes(container);
    }


    /**
     * Return true if the item is splitted from best deal
     * @return
     */
    public boolean isSelectedForItemSplit() {
        return selectedForItemSplit;
    }

    /**
     * Set true if the item is splitted from best deal
     */
    public void setSelectedForItemSplit(boolean selectedForItemSplit) {
        this.selectedForItemSplit = selectedForItemSplit;
    }

    /**
     * Returns true if item is on clearance , false otherwise
     * @return onClearance.
     */
    public boolean isOnClearance()
    {
        return onClearance;
    }

    /**
     * Sets the value of onClearance.
     *
     */
    public void setOnClearance(boolean onClearance)
    {
        this.onClearance = onClearance;
    }
    
    /**
     * @see oracle.retail.stores.domain.lineitem#getPrintedTranactionID()
     */
    public String getPrintedTranactionID()
    {
        String originalTransactionId = "";

        // Print transaction id only when nonRetrievedOriginalReceiptId is
        // null. This happens only in case of Non-Retrieved transactions
        if (isReturnLineItem() && !getReturnItem().isNonRetrievedReceiptedItem())
        {
            if (isOrderItem())
            {
                originalTransactionId = getOrderID();
            }
            else 
            {
                originalTransactionId = getReturnItem().getOriginalTransactionID().getTransactionIDString();
            }
        }
        return originalTransactionId;

    }
    
    //---------------------------------------------------------------------
    /**
     * @return a boolean flag indicating if this is an order pickup or cancel
     * line item
     */
    //---------------------------------------------------------------------
    public boolean isPickupCancelLineItem()
    {
        return false;
    }

    /**
     * returns true if line item price was set in-store during order pickup
     * 
     * @return the isInStorePriceDuringPickup
     */
    public boolean isInStorePriceDuringPickup()
    {
        return false;
    }
    
    /**
     * returns true if order line item price was cancelled during pickup
     * 
     * @return the isPriceCancelledDuringPickup
     */
    public boolean isPriceCancelledDuringPickup()
    {
        return false;
    }
    
    /**
     * @return deposit amount
     */
    public CurrencyIfc getDepositAmount()
    {
        return depositAmount;
    }

    /**
     * Set deposit amount
     * 
     * @param depositApplied
     */
    public void setDepositAmount(CurrencyIfc depositAmount)
    {
        this.depositAmount = depositAmount;
    }
}
