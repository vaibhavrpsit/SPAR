/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcReadTransaction.java /main/230 2014/07/24 15:23:29 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.> 
 *
 * MODIFIED (MM/DD/YY)
 *    yiqzha 11/12/14 - Add a column price entry required flag(FL_ENTR_PRC_RQ)
 *    yiqzha 11/05/14 - When retrieving a suspended transaction, 
 *                      FIELD_PICKUP_ORDER_SHIP_TO_STORE_FLAG and
 *                      FIELD_FULFILLMENT_ORDER_ID in ALIAS_TRANSACTION_ORDER_LINE_ITEM are 
 *                      the two columns before reading original transaction id columns. 
 *                      It is necessary to index them. 
 *    abhina 10/17/14 - Fixing wrongly used reason code type 
 *                      for capturing customer ID type.
 *    crain  10/01/14 - Fix to prevent user to return items (non retrieved
 *                      receipted return) from training mode in the normal
 *                      mode and vice versa.
 *    sgu    07/23/14 - add tax authority name
 *    yiqzha 07/15/14 - Read original line item number for order line item.
 *    sgu    07/08/14 - aggregate totablable line items
 *    yiqzha 07/01/14 - Add two columns for tr_ltm_sls_rtn table.
 *    yiqzha 06/20/14 - Persist order line item deposit amount and balance due.
 *    cgreen 05/14/14 - XbranchMerge cgreene_bug18687702-actualfix from
 *                      rgbustores_14.0x_generic_branch
 *    rabhaw 03/12/14 - removed use of deprecated method as it is not required 
 *                      to set selling price, it is calcuated on the fly.
 *    cgreen 03/11/14 - add support for returning ASA ordered items
 *    ohorne 03/07/14 - Suspended Orders are not saved to OR_* tables
 *    sgu    03/06/14 - add logic to retrieve transaction discount and tax for
 *                      CSC order
 *    mchell 02/26/14 - Persist external serial number creation allowed flag
 *    jswan  02/26/14 - Fixed an issue with retrieving transactions with a Mall
 *                      Certificate tender for returns and voids.
 *    cgreen 01/28/14 - Add support for saving type of external order
 *    cgreen 01/24/14 - add column to indicate transaction is created from an
 *                      ATG order
 *    mjwall 12/19/13 - fix POS null dereferences (part 1)
 *    arabal 12/16/13 - released the InputStream handle
 *    vinees 11/19/13 - changed other item Classifications required
 *    vinees 11/18/13 - Changing old referece of item Classification to current
 *                      one
 *    rabhaw 11/16/13 - Fixed wrong serial entry time update in
 *                      itemclassification
 *    icole  11/15/13 - Add support for check approval sequence number. This
 *                      code was lost along the way in a merge.
 *    yiqzha 10/24/13 - Persist external validation serialized item flag in
 *                      sale return line item table.
 *    abanan 10/09/13 - added method getItemLevelMessages to attach item related message
 *                      to transactionLineItem. 
 *    abonda 10/04/13 - set clearance flag during return process
 *    mchell 09/12/13 - Fixed duplicate tax entry in pos log for tax exempt
 *                      transaction. Setting tax rules to plu item to prevent
 *                      tax engine using default tax rules.
 *    abonda 09/04/13 - initialize collections
 *    yiqzha 08/06/13 - Using pricing group id when retrieving suspended
 *                      transaction.
 *    abhine 07/26/13 - this is to fix broken mixed order retrieval in xc env
 *    jswan  06/25/13 - Modified to fix totals issues with post voiding a
 *                      return funded with store credit.
 *    abhine 06/20/13 - fix for kit item not getting selected in mpos ui order
 *                      pick up screen
 *    subrde 06/17/13 - Update the taxChanged attribute of the line item to
 *                      true if tax modifier information is available.
 *    vtemke 06/11/13 - Fixed Unique constraint SQL errors when retrieving
 *                      suspended Delivery orders
 *    mkutia 06/10/13 - Order and Layaway transaction Cash Rounding
 *    jswan  06/05/13 - Modified to read the cash change rounding adjustment
 *                      for the void receiept.
 *    mkutia 05/23/13 - Retrieving orderLineItemStatus from DB
 *    rgour  05/15/13 - adding methods to retrieve employee record without
 *                      roles or fingerprints
 *    jswan  05/07/13 - Modified to support sending voided order returns to the
 *                      cross channel order repository.
 *    vtemke 04/16/13 - Moved constants in OrderLineItemIfc to
 *                      OrderConstantsIfc in common project
 *    rgour  04/01/13 - CBR cleanup
 *    jswan  03/27/13 - Fixed issue with reading/exporting to POSLog Store
 *                      Credit Tenders.
 *    icole  03/06/13 - Print Trace Number on Debit receipt if exists, else print
 *                      System Audit Trace Number if exists per ACI's
 *                      requirements.
 *    icole  02/28/13 - Forward Port Print trace number on receipt for gift
 *                      cards, required by ACI.
 *    sgu    02/22/13 - prorate transaction discounts for suspended
 *                      transactions
 *    mchell 02/12/13 - Rollback plu tax read
 *    mchell 01/30/13 - Set plu item tax rules
 *    sgu    01/14/13 - process pickup or cancel for store order items
 *    mkutia 01/10/13 - Persisting the Sale restrictive age field for the
 *                      SaleReturn Line items in the transacation
 *    yiqzha 01/10/13 - Add business name for store credit and store credit
 *                      tender line tables.
 *    sgu    01/07/13 - add quantity pending
 *    sgu    12/27/12 - add serialized item flag to sale return line item table
 *    sgu    12/14/12 - add order id column and rename order item reference
 *                      column
 *    jswan  12/13/12 - Modified to prorate discount and tax for returns of
 *                      order line items.
 *    sgu    12/12/12 - prorate tax for order pickup, cancel, and return
 *    jswan  12/10/12 - Fixed null pointer exception.
 *    jswan  12/06/12 - Modified to support JDBC opertions for order tax and
 *                      discount status.
 *    sgu    11/26/12 - set desposition code for special order item
 *    sgu    11/26/12 - read pickup store ID
 *    sgu    11/26/12 - set pickup store id for store order items
 *    yiqzha 11/21/12 - Remove clone() operation.
 *    yiqzha 11/19/12 - Fix the issue for an item with multiple related items
 *                      for return.
 *    yiqzha 11/19/12 - Fix mutiple related items for return.
 *    jswan  11/15/12 - Modified to support parameter controlled return
 *                      tenders.
 *    sgu    11/09/12 - add check for xc
 *    sgu    11/07/12 - added captured order line item
 *    sgu    10/16/12 - rename FIELD_ITEM_QUANTITY_PICKED to
 *                      FIELD_ITEM_QUANTITY_PICKED_UP
 *    sgu    10/16/12 - clean up order item quantities
 *    sgu    10/15/12 - added ordered amount at order ine item level
 *    arabal 10/03/12 - Forward Port : Fix is to update the POS log correctly
 *                      when transaction overrides of tax are followed by item
 *                      overrides of tax
 *    sgu    09/19/12 - accumulate completed and cancelled amount
 *    sgu    09/19/12 - add completed and cancelled amount at order line item
 *                      level
 *    jswan  09/13/12 - Modified to support deprecation of JdbcPLUOperation.
 *    sgu    08/31/12 - convert payment to tender in xc order
 *    sgu    08/27/12 - persist a TransactionDiscountAuditIfc discount rule
 *    sgu    08/27/12 - read transaction discount audit from db
 *    sgu    08/21/12 - rename column names in retail price modifer
 *    sgu    08/20/12 - fix defects in xc order discount rule reading
 *    sgu    08/17/12 - refactor discount audit
 *    sgu    08/16/12 - add ItemDiscountAudit discount rule
 *    sgu    07/03/12 - added xc order ship delivery date, carrier code and
 *                      type code
 *    jswan  06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    sgu    06/26/12 - change order delivery id column name
 *    sgu    06/15/12 - add payment support to xc order creation
 *    cgreen 05/16/12 - arrange order of businessDay column to end of primary
 *                      key to improve performance since most receipt lookups
 *                      are done without the businessDay
 *    yiqzha 05/18/12 - add the changes from ADE transaction merge
 *    yiqzha 05/17/12 - remove SHP_RDS_SLS_RTN_TX, RU_SHP_TX_ATHY tables and
 *                      three shipping tax related column from SHP_RDS_SLS_RTN
 *    sgu    05/16/12 - rename column LN_ITM_REF to AI_ORD_LN_ITM based on
 *                      Luis' review comments
 *    sgu    05/15/12 - added order line sequence number
 *    sgu    05/15/12 - remove column LN_ITM_REF from order line item tables
 *    sgu    05/11/12 - add more comments
 *    sgu    05/09/12 - check in after merge with latest build
 *    sgu    05/09/12 - initialize resultset index
 *    sgu    05/09/12 - separate minimum deposit amount into xchannel part and
 *                      store order part
 *    sgu    05/08/12 - prorate store order and xchannel deposit amount
 *                      separatly
 *    sgu    05/07/12 - rename crossChannel to XChannel
 *    sgu    05/07/12 - read/write order status table
 *    yiqzha 05/03/12 - save and read shipping charge flag for
 *                      SaleReturnLineItem
 *    sgu    04/26/12 - check in merge changes
 *    sgu    04/25/12 - fixed indentation
 *    sgu    04/24/12 - add order line reference
 *    sgu    04/24/12 - read/save order delivery details
 *    sgu    04/19/12 - save order status change date
 *    sgu    04/18/12 - enhance order item tables to support xc
 *    abonda 04/19/12 - moving the dao files which are direcly under
 *                      persistence to dao folder to seperate from jpa files
 *    yiqzha 04/16/12 - refactor store send from transaction totals
 *    sgu    04/09/12 - check in after merge
 *    yiqzha 04/03/12 - refactor store send for cross channel
 *    sgu    03/29/12 - move order recipient record be transactional data.
 *    rsnaya 03/22/12 - cross border return changes
 *    jswan  03/21/12 - Modified to support centralized gift certificate and
 *                      store credit.
 *    asinto 03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                      List<AddressIfc>) and remove old deprecated methods and
 *                      references to them
 *    rabhaw 03/21/12 - added for new column for item condition
 *    asinto 03/19/12 - do not read customer data, let client retrieve cusotmer
 *                      via CustomerManager.
 *    asinto 03/15/12 - do not read the customer, instead have the client read
 *                      customer using CustomerManager.
 *    yiqzha 03/07/12 - add OrderShippingDetail domain object and modify the
 *                      related code
 *    jswan  03/06/12 - Refactor the status change of suspended transaction to
 *                      occur in a transaction so that status change can be
 *                      sent to CO as part of DTM.
 *    mkutia 02/09/12 - XbranchMerge
 *                      mkutiana_bug13694785-handle_suspretrvd_suspcanc_trans
 *                      from rgbustores_13.4x_generic_branch
 *    mkutia 02/09/12 - Handle SuspendedRetrieved and SuspendedCancelled Trans
 *                      Status types during POSlog creation
 *    asinto 02/06/12 - XbranchMerge asinton_bug-13641991 from
 *                      rgbustores_13.4x_generic_branch
 *    asinto 02/06/12 - fix plu item lookup for retrieval of suspended
 *                      transactions
 *    mjwall 01/31/12 - XbranchMerge mjwallac_forward_port_bug_13603967 from
 *                      rgbustores_13.4x_generic_branch
 *    mjwall 01/27/12 - XbranchMerge mjwallac_forward_port_bug_13599097 from
 *                      rgbustores_13.4x_generic_branch
 *    mjwall 01/27/12 - Forward port: SQL Exception when trying to save a
 *                      resumed order transaction that had been linked to a
 *                      customer, but customer was deleted before resuming.
 *    mjwall 01/27/12 - Prevent returns from non-complete transactions
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    asinto 10/28/11 - removed posItemId from reasonCodeMap key as line item
 *                      sequence number is sufficent and posItemId is different
 *                      from itemId.
 *    tkshar 10/12/11 - Linked the related item to the line item
 *    rsnaya 10/10/11 - Pos log Item id fix
 *    asinto 09/22/11 - when reading a suspended transaction, capture the
 *                      giftcard's requested amount from the line item's
 *                      extended selling price.
 *    cgreen 09/15/11 - removed deprecated methods and changed static methods
 *                      to non-static
 *    jswan  09/12/11 - Modifications for reversals of Gift Cards when escaping
 *                      from the Tender Tour.
 *    cgreen 09/12/11 - revert aba number encryption, which is not sensitive
 *    cgreen 09/02/11 - refactored method names around enciphered objects
 *    cgreen 08/22/11 - removed deprecated methods
 *    tkshar 08/19/11 - Made column names consistent for Encryption CR
 *    mkutia 08/17/11 - Removed deprecated Customer.ID_HSH_ACNT from DB and all
 *                      using classes
 *    blarse 08/02/11 - Renamed token to accountNumberToken to be consistent.
 *    vtemke 07/28/11 - Modified readCustomer() method to fetch based on
 *                      isLayawayTransacction flag
 *    rrkohl 07/27/11 - removing check number encryption/masking
 *    mkutia 07/25/11 - HPQC 438 - DiscountEmployeeID is set on the
 *                      retrieved/recreated transaction object
 *    rrkohl 07/19/11 - encryption CR
 *    cgreen 07/18/11 - remove hashed number column from gift card tables
 *    blarse 07/15/11 - Fix misspelled word: retrival
 *    cgreen 07/15/11 - removed encrypted expiration date from datamodel
 *    blarse 07/14/11 - Persisting new authorization journal key
 *    rrkohl 07/13/11 - IRS customer related changes for encryption CR
 *    cgreen 07/12/11 - set tenderType onto cardData
 *    cgreen 07/07/11 - convert entryMethod to an enum
 *    rrkohl 07/05/11 - POS Log (Encryption CR)
 *    asinto 06/29/11 - Refactored to use EntryMethod and AuthorizationMethod
 *                      enums.
 *    cgreen 06/29/11 - add token column and remove encrypted/hashed account
 *                      number column in credit-debit tender table.
 *    blarse 06/16/11 - Reading payment service token.
 *    cgreen 06/09/11 - added dao to persist and retrieve ICC card details
 *    sgu    02/16/11 - read manufacturer item upc
 *    sgu    02/15/11 - check in all
 *    sgu    02/03/11 - check in all
 *    nkgaut 12/10/10 - added missing promotion id while reading
 *                      discountLineItems
 *    cgreen 12/01/10 - implement saving applied promotion names into
 *                      tr_ltm_prm table
 *    jswan  11/12/10 - Removed lookup for default tax rules.
 *    nkgaut 09/29/10 - added first name and last name for store credit
 *    jkoppo 09/29/10 - BUG#872, Kit components are coming as individual
 *                      lineitems in POSLog.
 *    asinto 09/22/10 - Adding Credit Card Accountability Responsibility and
 *                      Disclosure Act of 2009 changes.
 *    rsnaya 09/14/10 - BPE post void transaction retrieval fix
 *    blarse 09/07/10 - updated instantiatePLUItem() to support unknown items.
 *    jswan  09/03/10 - Modified to read item level messages for returns
 *                      processing.
 *    acadar 08/31/10 - merged with tip
 *    acadar 08/31/10 - changes for external orders to not filter by action
 *                      codes
 *    jswan  08/31/10 - Checkin merge from refresh to tip.
 *    jswan  08/31/10 - Fixed issues setting the isCollected flag on the
 *                      TenderLineItem.
 *    npoola 08/31/10 - Set discountable flag to the Item price while reading
 *                      the item price
 *    acadar 08/30/10 - do not filter external order items based on action code
 *    abhayg 08/27/10 - Fix for Missing customer info at re-printed layaway
 *                      receipt
 *    npoola 08/26/10 - merged the code to tip
 *    npoola 08/26/10 - send info on the receipt is not required for Returns.
 *    abonda 08/26/10 - updated related to retrieve a suspended return
 *                      transaction
 *    jswan  08/25/10 - Fixed issues returning a transaction with a transaction
 *                      discount and non discountable items. Also refactored
 *                      the creation of PLUItems to remove extraneous data
 *                      element from the SaleReturnLineItem table.
 *    mchell 08/24/10 - Billpay datamodel changes
 *    nkgaut 08/24/10 - fixed transaction tax info absent in the receipt and EJ
 *    jkoppo 08/20/10 - Item level messages are not displayed in return
 *                      receipts.
 *    nkgaut 08/20/10 - fixed wrong total tender amount issue for reprint
 *                      receipts for over tendered transactions
 *    jswan  08/18/10 - Made multiple fixes for suspend/retrieve, and price
 *                      adjustment functionality which was broken by the
 *                      modifications to CTR.
 *    jswan  08/13/10 - Checkin for label server change.
 *    acadar 08/05/10 - modified based on code review comments
 *    acadar 08/05/10 - read the plu tax group id and use it for
 *                      suspend/retrieve
 *    jkoppo 08/05/10 - Fix for Bug#9955620 - Null pointer exception when
 *                      searching for special order.
 *    nkgaut 07/19/10 - readAllTransactionData method is updated not to query
 *                      redeem tables in case of cancelled transaction.
 *    nkgaut 07/06/10 - bill pay report changes
 *    nkgaut 06/23/10 - bill pay changes
 *    sgu    06/11/10 - fix tab
 *    sgu    06/11/10 - use -1 as the external order item id to be filled
 *    abonda 06/07/10 - rename externalOrderDesc to externalOrderNumber
 *    asinto 05/28/10 - KSN bytes need to be captured from CPOI device and
 *                      formatted in the ISD request message for debit
 *    jswan  06/01/10 - Modified to support transaction retrieval performance
 *                      and data requirements improvements.
 *    jswan  05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    sgu    05/25/10 - clean up jdbc read or save external order info
 *    sgu    05/25/10 - add jdbc read or save for external order info
 *    cgreen 05/11/10 - convert Base64 from axis
 *    asinto 05/06/10 - Added Prepaid Remaining Balance to receipt and ejournal
 *    cgreen 05/05/10 - remove deprecated log amanger and technician
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abhayg 02/25/10 - For pos log issue
 *    mkutia 02/22/10 - Cancelled Order - POSLog not created issue
 *    cgreen 02/18/10 - set shortDesc on unknown items to same as longDesc
 *    nkgaut 02/15/10 - added code to retrieve transaction gift receipt flag
 *    cgreen 02/01/10 - removed and updated deprecated methods in Register
 *                      class
 *    abonda 01/03/10 - update header date
 *    cgreen 12/22/09 - added code to select applied promotion by prcgroupid
 *    asinto 12/09/09 - Changes per code review.
 *    asinto 12/03/09 - Changes to support credit card authorizations on
 *                      returns and voids.
 *    jswan  11/20/09 - Fixed integer/string issues associated with reading
 *                      reason codes.
 *    djenni 07/17/09 - removing extra call to customer locale. not locale is
 *                      read out with the customer.
 *    acadar 06/22/09 - set the default country code
 *    jswan  06/09/09 - Checkin result of merge.
 *    jswan  06/09/09 - Fix I18N/area code issues with saving and testing the
 *                      check tender table.
 *    acadar 06/08/09 - merge to the tip
 *    acadar 06/05/09 - save tender display description when doing a bank
 *                      deposit
 *    acadar 06/05/09 - refactor the way tender media line items are read from
 *                      the database
 *    cgreen 06/04/09 - set phones of mail bank check. this was previously only
 *                      in EYSPrintableDocument
 *    mchell 05/15/09 - Fixed gift certificate POSLog issue
 *    asinto 05/15/09 - Set the ReturnItemIfc's have receipt attribute when the
 *                      original receipt ID is present upon reading the item
 *                      from the database.
 *    asinto 05/06/09 - Added Gift Certificate subtype to the select statement.
 *    cgreen 05/05/09 - set redeem foreign amount to null if same tpye as base
 *    cgreen 05/04/09 - fix setting of foriegn currency type for retrieving
 *                      redeem
 *    jswan  04/24/09 - Code review changes.
 *    jswan  04/24/09 - Modified to ensure that orders created in training mode
 *                      can only retrieve in training mode, and non-training
 *                      mode orders can only be retrieved in non-training mode.
 *    cgreen 04/15/09 - use correct code type for retrieving store credit id
 *                      types
 *    vikini 04/15/09 - Fixing TenderDescriptor data
 *    mpbarn 04/14/09 - Merge
 *    acadar 04/14/09 - added reason code for employee discount
 *    acadar 04/13/09 - make layaway location required; refactor the way we
 *                      handle layaway reason codes
 *    mpbarn 04/10/09 - Merge
 *    mpbarn 04/07/09 - In readTransactionExecuteAndParse, use
 *                      getBooleanFromString() to read reentry mode from result
 *                      set.
 *    mpbarn 04/03/09 - Added code to add the reentry flag to the transaction
 *                      object.
 *    mpbarn 04/03/09 - Set the reentry flag in the transaction object.
 *    jswan  04/02/09 - Removed address type qualifier from store address
 *                      lookup.
 *    mpbarn 04/01/09 - Read the reentry flag and store in domain.
 *    cgreen 03/30/09 - print special instructions line as two lines and spell
 *                      out whole label. hide label when there are not special
 *                      instructions
 *    djenni 03/28/09 - Enter a single line comment: creating
 *                      isSalesAssociateModifiedAtLineItem(), which is similar
 *                      to getSalesAssociateModified(), and using it at receipt
 *                      to determine whether to print the SalesAssociate at the
 *                      line item. Jack warned against modifying the existing
 *                      method as it is used for something else.
 *    jswan  03/26/09 - Added code to read the tax exempt reason code.
 *    cgreen 03/20/09 - use employeediscount codes when discount is empdisc
 *    mchell 03/18/09 - Using CaptureCustomerIDTypes to retreive store credit
 *                      PersonalIDType reason code
 *    cgreen 03/10/09 - switch from getTenderType by code to get by description
 *                      which TillLoan saves in db
 *    mahisi 03/06/09 - fixed issue for suspended transaction
 *    acadar 03/05/09 - use code instead of name, fix the result set problem
 *    sswamy 02/28/09 - Removed getRequestLocales() method.
 *    sswamy 02/27/09 - Added new getBestMatchingRequestLocales() method, to be
 *                      used for intializing the Locale Descriptons for Unknown
 *                      Item.
 *    mahisi 02/27/09 - clean up code after code review by jack for PDO
 *    mahisi 02/26/09 - Rework for PDO functionality
 *    cgreen 02/25/09 - move detemination of discounts codeListType outside of
 *                      switch so that any discount type can gets its proper
 *                      codes
 *    jswan  02/22/09 - Depricated selectOrderLineItems() per code review.
 *    jswan  02/21/09 - Added order detail to the order line item; saving
 *                      delivery items fails otherwise.
 *    jswan  02/21/09 - Set pickup/delivery flags and delivery date in
 *                      OrderItemStatus object.
 *    jswan  02/20/09 - Added read of Order Delivery Detail and Order Recipient
 *                      Detail tables; added read of new OrderLineItem colums;
 *                      general clean up including generics, deprications and
 *                      removal of unsed columns.
 *    ranojh 02/20/09 - Merged
 *    ranojh 02/18/09 - Fixed import and export logic for TenderDescriptor in
 *                      till Pickup POSLog
 *    aratho 02/17/09 - Updated to send IRS CUSTOMER data thru DTMLog.
 *    jswan  02/13/09 - Fix problem with totaling void of returns.
 *    mkochu 01/23/09 - set country
 *    cgreen 01/08/09 - removed deprecated prepareDataForCanadianReceipt()
 *                      method
 *    mchell 12/23/08 - Fixed DB2 result set closed exception
 *    vikini 12/10/08 - Adding ItemMessage Info into Item Object
 *    mahisi 12/04/08 - JUnit fix and SQL fix
 *    aphula 11/27/08 - fixed merge issue
 *    aphula 11/27/08 - checking files after merging code for receipt printing
 *                      by Amrish
 *    mahisi 11/25/08 - updated due to merge
 *    rkar   11/17/08 - View refresh to 081112.2142 label
 *    mchell 11/14/08 - Duefiles moodule
 *    mchell 11/13/08 - Merge changes
 *    mchell 11/13/08 - Inventory Reservation Module
 *    rkar   11/12/08 - Adds/changes for POS-RM integration
 *    sgu    11/10/08 - refresh to latest build
 *    sgu    11/06/08 - more reason code CTR configuration
 *    abonda 11/06/08 - updated files related to reason codes
 *    abonda 11/05/08 - updated files related to reason codes
 *    abonda 11/05/08 - updated files related to reason code
 *    abonda 11/05/08 - updated files related to the reason codes CheckIDTypes
 *                      and MailBankCheckIDTypes
 *    ranojh 11/05/08 - Fixed Tax Exempt Reason Code for Customer
 *    rkar   11/04/08 - Added code for POS-RM integration
 *    abonda 11/04/08 - updated files related to check reason codes.
 *    acadar 11/04/08 - merge to tip
 *    abonda 11/04/08 - updated file related to customer id types reason code.
 *    acadar 11/03/08 - updated as per the code review comments
 *    acadar 11/03/08 - updates
 *    acadar 11/03/08 - transaction tax reason codes updates
 *    acadar 11/03/08 - localization of transaction tax reason codes
 *    acadar 11/03/08 - localization of reason codes for discounts and merging
 *                      to tip
 *    abonda 11/03/08 - updated files related to the Patriotic customer ID
 *                      types reason code
 *    acadar 11/02/08 - updated as per code review
 *    acadar 11/02/08 - changes to read the localized reason codes for customer
 *                      groups and store coupons
 *    acadar 10/31/08 - fixes for retrieving the reason codes for advanced
 *                      pricing rules
 *    acadar 10/31/08 - added check for null LocalizedCodeIfc
 *    acadar 10/31/08 - fixes to distinguish between manual item discounts and
 *                      markdowns
 *    acadar 10/31/08 - fixes to reason code localization
 *    acadar 10/31/08 - minor fixes for manual discounts localization
 *    ohorne 10/29/08 - Localization of Till-related Reason Codes
 *    ranojh 10/31/08 - Refreshed View and Merged changes with Reason Codes
 *    acadar 10/30/08 - use localized reason codes for item and transaction
 *                      discounts
 *    ranojh 10/30/08 - Fixed Return/UOM and Department Reason Codes
 *    ranojh 10/29/08 - Fixed ReturnItem
 *    ranojh 10/29/08 - Changes for Return, UOM and Department Reason Codes
 *    ddbake 10/29/08 - Merge Update
 *    acadar 10/29/08 - merged to tip
 *    ddbake 10/28/08 - Updates due to merge
 *    mdecam 10/28/08 - I18N - Refactoring the retrieval of reason codes for
 *                      NoSale, TransactionSuspend and IDTypes.
 *    mdecam 10/28/08 - I18N - Reason Codes for Customer Types.
 *    mdecam 10/27/08 - I18N - Localizing Reason Codes for Customer Capture
 *    mdecam 10/27/08 - I18N - Refactoring Reason Codes for
 *                      CaptureCustomerIDTypes
 *    acadar 10/27/08 - fix broken unittests
 *    acadar 10/27/08 - merges to the tip
 *    acadar 10/27/08 - use localized price override reason codes
 *    mdecam 10/24/08 - I18N updates for Suspend Transaction Reason Codes.
 *    acadar 10/24/08 - use the correct reason code list
 *    acadar 10/24/08 - fix for getLocalizedReasonCode
 *    acadar 10/24/08 - I18N changes for post void reason codes
 *    ohorne 10/23/08 - I18N StoreInfo-related changes
 *    acadar 10/23/08 - merged with tip
 *    acadar 10/22/08 - merged to the tip
 *    mdecam 10/21/08 - I18N - Localizing No Sale ReasonCode
 *    ranojh 10/21/08 - Code Review changes
 *    ranojh 10/21/08 - Changes for POS for UnitOfMeasure I18N
 *    ranojh 10/17/08 - Changes for UnitOfMeasure and Item Size/Color and Style
 *    mipare 10/17/08 - Deptartment list changes for localized text
 *    ranojh 10/16/08 - Implementation for UnitOfMeasure I18N Changes
 *    mipare 10/16/08 - dept list changes
 *    acadar 10/15/08 - merge
 *    acadar 10/14/08 - unit test fixes
 *    acadar 10/13/08 - updates for reading localized information
 *    sgu    10/14/08 - set up locale requestor to get original transaction for
 *                      post void
 *    sgu    10/14/08 - update review comments
 *    sgu    10/13/08 - update POS unit tests
 *    sgu    10/13/08 - update
 *    sgu    10/11/08 - update JdbcReadTransaction to take locale requestor
 *    ohorne 10/08/08 - deprecated methods per I18N Database Technical
 *                      Specification
 *
 * ===========================================================================

     $Log:
      51   360Commerce 1.50        11/12/2007 6:37:51 PM  Anil Bondalapati
           updated related to PABP.
      50   360Commerce 1.49        11/12/2007 4:28:28 PM  Alan N. Sinton  CR
           29598 - Changes for PABP.
      49   360Commerce 1.48        11/12/2007 2:14:22 PM  Tony Zgarba
           Deprecated all existing encryption APIs and migrated the code to
           the new encryption API.
      48   360Commerce 1.47        9/12/2007 1:30:42 PM   Brett J. Larsen CR
           28691 - adding an order-by to the query - this is not causing a
           crash - however, if a database decides to return the records in an
           order other than send-label-count, there will be a problem
      47   360Commerce 1.46        8/27/2007 2:48:04 PM   Ashok.Mondal    CR
           28588 :Read the currencyID value in correct sequence to avoid wrong
            data for tender with foreign gift certificate.
      46   360Commerce 1.45        8/24/2007 10:20:48 AM  Anda D. Cadar   CR
           28495: Make sure that the currency ID is read after the foreign
           amount creation
      45   360Commerce 1.44        8/21/2007 11:09:15 AM  Alan N. Sinton  CR
           28175 Don't set the tax percentage on the item's tax information.
      44   360Commerce 1.43        8/13/2007 3:03:19 PM   Charles D. Baker CR
           27803 - Remove unused domain property.
      43   360Commerce 1.42        8/8/2007 11:20:59 AM   Sandy Gu        Read
           department tax rules for jdbcReadTransaction
      42   360Commerce 1.41        8/3/2007 9:57:52 AM    Michael P. Barnett In
            readCreditDebitTenderLineItem(), check for null expiration date.
      41   360Commerce 1.40        8/1/2007 11:22:58 AM   Ranjan Ojha     Fix
           of UniqueID of TaxInformation in TaxModifier.
      40   360Commerce 1.39        7/19/2007 1:52:23 PM   Charles D. Baker CR
           27803 - Removed references to unused domain gateway property.
      39   360Commerce 1.38        6/26/2007 11:13:58 AM  Ashok.Mondal    I18N
           changes to export and import POSLog.
      38   360Commerce 1.37        6/11/2007 2:08:04 PM   Anda D. Cadar   SCR
           27106: Use the DateTimeServiceifc to convert the credit card
           expiration date
      37   360Commerce 1.36        6/1/2007 3:16:05 PM    Christian Greene
           Backing out PLU to pre-v1.0.0.414 version code
      36   360Commerce 1.35        5/31/2007 6:23:15 AM   Christian Greene
           Changed selling price lookup to rely on price change tables.
           Removed selling and permanenet price columns from posidentity and
           retailstoreitem tables. Moved some pricing DIMP beans to item
           module so ItemImport DAO can persist price.
      35   360Commerce 1.34        5/18/2007 12:18:50 PM  Maisa De Camargo
           Retrieving the promotionLineItems.
      34   360Commerce 1.33        5/18/2007 5:48:34 AM   Prakash Shanmugam
           i18n date changes done.
      33   360Commerce 1.32        5/17/2007 3:47:17 PM   Owen D. Horne
           CR#25377 - Merged fix from v8.0.1 (no comment in v8x check-in)
      32   360Commerce 1.31        5/15/2007 5:53:46 PM   Maisa De Camargo
           Added PromotionId, PromotionComponentId and
           PromotionComponentDetailId
      31   360Commerce 1.30        5/15/2007 4:59:01 PM   Sandy Gu        set
           fromTransaction flag to true
      30   360Commerce 1.29        5/14/2007 4:17:45 PM   Brett J. Larsen CR
           26477 - changing how tax amounts are assigned - this info shold be
           saved in the sendPackage.taxItem

      29   360Commerce 1.28        5/12/2007 2:49:25 PM   Brett J. Larsen CR
           26477 - persist shipping charge tax

           added method to read the shipping tax records

           modified readTransactionShippings to call the new read-shipping-tax
            method

           integrated changes made to ShippingMethodIFC (methods renamed)

      28   360Commerce 1.27        5/8/2007 2:30:39 PM    Brett J. Larsen CR
           26477 - adding support for new columns/fields added for the VAT
           project

           table: tax shipping records (shp_rds_sls_rtn)
           columns: tax group id, tax amount & included tax amount
      27   360Commerce 1.26        5/7/2007 2:21:04 PM    Sandy Gu
           enhance shipping method retrieval and internal tax engine to handle
           tax rules
      26   360Commerce 1.25        5/3/2007 11:57:43 PM   Sandy Gu
           Enhance transaction persistence layer to store inclusive tax
      25   360Commerce 1.24        5/1/2007 12:16:12 PM   Brett J. Larsen CR
           26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
           feature)
      24   360Commerce 1.23        4/26/2007 1:03:32 PM   Ashok.Mondal    CR
           16572 :V7.2.2 merge to trunk.
      23   360Commerce 1.22        4/25/2007 10:01:11 AM  Anda D. Cadar   I18N
           merge
      22   360Commerce 1.21        12/8/2006 5:01:15 PM   Brendan W. Farrell
           Read the tax history when creating pos log for openclosetill
           transactions.  Rewrite of some code was needed.
      21   360Commerce 1.20        11/9/2006 7:28:30 PM   Jack G. Swan
           Modifided for XML Data Replication and CTR.
      20   360Commerce 1.19        8/10/2006 11:17:01 AM  Brendan W. Farrell
           16500 -Merge fix from v7.x.  Maintain sales associate to be used in
            reporting.
      19   360Commerce 1.18        8/9/2006 6:15:34 PM    Brett J. Larsen CR
           19562 - incorrect previous-order-status for special orders in
           poslog

           CR 4262 - incorrect old-status in ejournal

           v7x->360commerce merge
      18   360Commerce 1.17        7/25/2006 7:53:03 PM   Charles D. Baker
           Updated to handle DB update breaking single address line column
           into two. Corrected other special handling as mail bank check
           shares Customer object with Capture Cutomer.
      17   360Commerce 1.16        7/19/2006 12:51:40 PM  Brendan W. Farrell
           Create wrapper around encryption manager and service so that this
           can be used in either store server environment.
      16   360Commerce 1.15        7/14/2006 1:23:50 PM   Brett J. Larsen CR
           18490 - UDM - percent values in exported poslog were incorrect (ex.
            0.082500000003897927340408308242398 - should be 8.25)
           - TR_LTM_TX.PE_TX changed from DECIMAL(5,2) to (8,5)
           - previous code change for this was incorrect - this change handles
            it correctly
      15   360Commerce 1.14        6/14/2006 1:43:00 PM   Brendan W. Farrell
           UDM
      14   360Commerce 1.13        6/12/2006 3:32:10 PM   Brendan W. Farrell
           Fix for UDM.
      13   360Commerce 1.12        6/7/2006 7:08:08 PM    Brett J. Larsen CR
           18490 - UDM - updating tax percentage and override tax percentage
           to support the new decimal(8, 5) db type
      12   360Commerce 1.11        4/27/2006 7:26:57 PM   Brett J. Larsen CR
           17307 - remove inventory functionality - stage 2
      11   360Commerce 1.10        4/5/2006 6:00:22 AM    Akhilashwar K. Gupta
           CR-3861: As per BA decision, reverted back the changes done earlier
            to fix the CR i.e. addition of following 4 fields in Store Credit
           and related code:
           - RetailStoreID
           - WorkstationID
           - TransactionSequenceNumber
           - BusinessDayDate
      10   360Commerce 1.9         4/3/2006 10:33:33 PM   Dinesh Gautam   CR
           10745: This file was changed for CR 10745. But fix for CR 172
           already fixed CR 10745, hence reverting back the change done.
      9    360Commerce 1.8         3/15/2006 11:49:05 PM  Akhilashwar K. Gupta
           CR-3861: Modified selectTenderLineItems() and
           readStoreCreditTenderLineItem() methods. Overridden
           readStoreCredit() method.
      8    360Commerce 1.7         3/3/2006 5:40:09 AM    Dinesh Gautam
           CR10745 - Post void with store coupon
      7    360Commerce 1.6         2/17/2006 1:13:10 PM   Brett J. Larsen CR
           10622 - RelatedItemTransactionLineItemSeqNumb has wrong value in
           POSLog - the 7.0.3->7.1.1 merge got the column order in the query
           out of sync with the rs.getXXX calls
      6    360Commerce 1.5         1/25/2006 4:11:19 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      5    360Commerce 1.4         1/22/2006 11:41:21 AM  Ron W. Haight
           Removed references to com.ibm.math.BigDecimal
      4    360Commerce 1.3         12/13/2005 4:43:44 PM  Barry A. Pape
           Base-lining of 7.1_LA
      3    360Commerce 1.2         3/31/2005 4:28:42 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:22:46 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:00 PM  Robert Pearse
     $:


      12   .v7x      1.10.1.0    4/21/2006 3:50:07 AM   Nageshwar Mishra CR
           16500: Modified the readTransactionsAddColumns()for getting the
           Sales Associate value from the TR_TRN table.

      14   .v7x      1.10.1.2    6/30/2006 7:12:48 AM   Dinesh Gautam   CR
           4262: Defect fixed, Old Status modified for Special Order Pickup
           items.


      11   .v710     1.2.2.0     9/21/2005 13:39:46     Brendan W. Farrell
           Initial Check in merge 67.
      10   .v700     1.2.3.6     1/4/2006 16:12:12      Rohit Sachdeva
           4123:Customer Physically Present
      9    .v700     1.2.3.5     11/30/2005 17:24:04    Deepanshu       CR
           6261: Added Postvoid transaction reason code
      8    .v700     1.2.3.4     11/14/2005 17:11:00    Deepanshu       CR
           6144: Retrieve the Retrieved Transaction Flag and set the
           fromRetrivedTransaction flag of ReturnItem class if transaction was
           suspended.
      7    .v700     1.2.3.3     10/27/2005 15:31:14    Jason L. DeLeau 175:
           Make sure a transaction with a store coupon can be post voided.
      6    .v700     1.2.3.2     10/27/2005 14:37:10    Jason L. DeLeau 172:
           Make sure a transaction with a store coupon can be returned.
      5    .v700     1.2.3.1     10/25/2005 10:42:32    Deepanshu       CR
           6116: Set the tender amount in TenderStoreCredit.
      4    .v700     1.2.3.0     10/24/2005 13:30:01    Deepanshu       CR
           6132: The fix from Gap is merged as this was a proper code that will
           reduce chances of defect later.
      3    360Commerce1.2         3/31/2005 15:28:42     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:22:46     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:00     Robert Pearse
     $
     Revision 1.88.2.7  2005/01/20 21:03:14  kmcbride
     @scr 7939

     Revision 1.88.2.6  2005/01/20 19:27:13  kmcbride
     @scr 7939: Adding ability for poslog to retrieve store coupon line items while being as confident as possible that this does not regress other transaction lookups.

     Revision 1.88.2.5  2004/12/16 23:30:11  cdb
     @scr 7842 Updated to set redeem ID in redeem transaction for gift certificate redeem.

     Revision 1.88.2.4  2004/12/09 23:20:27  mwright
     No functional change, added comments to clarify dodgy code that drops through a case statement in selectTillAdjustmentTransaction()

     Revision 1.88.2.3  2004/12/08 00:05:37  mwright
     Added code to read transaction re-entry flag

     Revision 1.88.2.2  2004/11/11 22:26:34  mwright
     Merge in SOME changes from top of tree - not sure why v1.89 was not merged in, so I'm leaving it out while merging in the change in v1.90

     Revision 1.88.2.1  2004/11/03 21:12:56  jdeleau
     @scr 7354 Make sure uniqueID doesn't exceed space allocated
     in the database of 35 characters.

     Revision 1.88  2004/09/30 18:08:45  cdb
     @scr 7248 Cleaned up inventory location and state in LayawayTransaction object.

     Revision 1.86  2004/08/31 19:09:14  blj
     @scr 6855 - cleanup gift card credit code and fix defects found by PBY

     Revision 1.85  2004/08/23 16:15:45  cdb
     @scr 4204 Removed tab characters

     Revision 1.84  2004/08/19 21:34:59  mweis
     @scr 6841 readAllTransactionData wasn't reading every transaction's data, specifically for canceled transactions.

     Revision 1.83  2004/08/19 15:13:32  rsachdeva
     @scr 6791 Item Level Send  to Transaction Level Send Update Flow

     Revision 1.82  2004/08/17 14:25:38  jdeleau
     @scr 6844 Make PE_TX save as a percentage instead of a rate.

     Revision 1.81  2004/08/16 21:14:50  lzhao
     @scr 6654: remove the relationship for training mode sequence number from real training mode sequence number.

     Revision 1.80  2004/08/10 22:09:20  bwf
     @scr 6519 Fix infinite loop during credit transaction retrieve for return.

     Revision 1.79  2004/08/10 14:23:44  rsachdeva
     @scr 6791 Transaction Level Send Flag

     Revision 1.78  2004/08/10 07:17:11  mwright
     Merge (3) with top of tree


     Revision 1.77  2004/08/09 18:22:20  dcobb
     @scr 6793 "convert" mispelled in  converStringToPointArray method name

     Revision 1.76  2004/08/06 22:22:36  kll
     @scr 6644: db2 syntax

     Revision 1.75  2004/08/04 15:31:12  kll
     @scr 6644: db2 syntax support

     Revision 1.74  2004/08/02 21:23:43  mweis
     @scr 6519 Don't blow chunks if the image data stream has an apostrophe at the end.

     Revision 1.73  2004/07/26 14:36:59  lzhao
     @scr 2681: add gift card original/current balance and auth method.

     Revision 1.72  2004/07/24 18:03:12  jdeleau
     @scr 6429 Make Tax Exempt items appear as such in reprint receipt

     Revision 1.71  2004/07/22 17:37:19  jdeleau
     @scr 6408 Make sure reprint receipt is exactly the same as the original.  To
     do this the right data needed to be pulled from the database, and
     the uniqueID on the taxRule needed to be properly set.

     Revision 1.70  2004/07/22 04:56:31  khassen
     @scr 6296/6297/6298 - Updating pay in, pay out, payroll pay out:
     Adding database fields, print and reprint receipt functionality to reflect
     persistence of additional data in transaction.

     Revision 1.69  2004/07/19 21:53:44  jdeleau
     @scr 6329 Fix the way post-void taxes were being retrieved.
     Fix for tax overrides, fix for post void receipt printing, add new
     tax rules for reverse transaction types.

     Revision 1.68  2004/07/08 23:34:31  jdeleau
     @scr 6086 payroll till pay out on post void was crashing the system.  In fact it
     was not implemented at all.  Now its implemented just as normal till pay out.

     Revision 1.67  2004/07/08 18:15:55  cdb
     @scr 6038 Updated so that inability to find PLUItem associated with a
     given sale return line item will throw data not found data exception. Cleaned
     up some associated errors.

     Revision 1.66.2.2  2004/08/09 12:51:33  mwright
     Added code to read send check reason code
     Strip quotes from signature capture string
     Contains commented out workaround for percent divide by 100 bug

     Revision 1.66.2.1  2004/07/29 00:49:02  mwright
     Read capture customer data in readAllTransactionData()
     Added send package count in selectSaleReturnTransaction()
     Set timestamp and operator for till suspend/resume in selectTillOpenCloseTransaction()
     Contains commented backout of send label changes (look for "backout Kintore change")
     Added customer email and order description in readOrderInfo()


     Revision 1.66  2004/07/02 21:08:26  aachinfiev
     @scr 2020 - Fixed reprinting recipt for a void transaction in training mode

     Revision 1.65  2004/07/02 19:11:27  jdeleau
     @scr 5982 Support Tax Holiday

     Revision 1.64  2004/07/02 15:08:28  mweis
     @scr 5854 NPE when going after PLUItem

     Revision 1.63  2004/07/01 16:21:30  jdeleau
     @scr 5952 Ensure that the suspended transaction that is retrieved has identical
     data fields when its tendered, as the suspended transaction, if no changes were
     made after the transaction was retrieved.

     Revision 1.62  2004/06/29 21:58:58  aachinfiev
     Merge the changes for inventory & POS integration

     Revision 1.61  2004/06/28 14:13:21  jdeleau
     @scr 5818 Suspended transactions now have all types of
     overrides preserved when suspended.

     Revision 1.60  2004/06/25 14:55:00  khassen
     @scr 5803 - Added qualifiers for capture customer query.

     Revision 1.59  2004/06/21 18:34:21  jdeleau
     @scr 4906 Itemized taxes for returns, printing on the receipt.

     Revision 1.58  2004/06/19 17:43:21  khassen
     @scr 5684 - Feature enhancements for capture customer use case.  Added selectCaptureCustomer() method.

     Revision 1.57  2004/06/19 14:00:53  lzhao
     @scr 4670: add column for the flag for send customer: linking or capture

     Revision 1.56  2004/06/15 16:05:33  jdeleau
     @scr 2775 Add database entry for uniqueID so returns w/
     receipt will work, make some fixes to FinancialTotals storage of tax.

     Revision 1.55  2004/06/14 23:25:09  lzhao
     @scr 4670: Fix the duplicated business name in reprint receipt.

     Revision 1.54  2004/06/11 18:59:52  lzhao
     @scr 4670: Change the way to getCalculatedShippingCharge

     Revision 1.53  2004/06/11 12:37:54  mkp1
     @scr 2775 More Tax - Returns

     Revision 1.52  2004/06/07 23:00:30  lzhao
     @scr 4670: add more column for shippingRecords table.

     Revision 1.51  2004/06/07 18:19:31  jdeleau
     @scr 2775 Add tax Service, Multiple Geo Codes screens

     Revision 1.50  2004/06/04 18:57:49  crain
     @scr 5388 Voiding a gift card redeem does not increase the "out" in tender summary in register reports

     Revision 1.49  2004/06/03 16:22:41  jdeleau
     @scr 2775 Initial Drop of send item tax support.

     Revision 1.48  2004/06/02 19:54:30  jdeleau
     @scr 2275 Fix sql error in reading items in.

     Revision 1.47  2004/06/02 19:01:53  lzhao
     @scr 4670: add shippingRecords table.

     Revision 1.46  2004/06/01 22:35:53  jdeleau
     @scr 2775 Retrieve PLUItem by GeoCode when reading in the line
     items for a transaction.

     Revision 1.45  2004/05/28 19:10:20  jdeleau
     @scr 2775 Change tax structure to use TaxInformationIfc

     Revision 1.44  2004/05/28 17:08:49  jdeleau
     @scr 2775 drop tabs

     Revision 1.43  2004/05/26 19:26:03  lzhao
     @scr 4670: add send label count for send.

     Revision 1.42  2004/05/14 21:32:56  blj
     @scr 4476 - fix  post void for store credit issue/redeem/tender

     Revision 1.41  2004/05/14 00:00:42  cdb
     @scr 5103 Updated to retrieve assignment basis of employee discounts for
     transactions. Removed debugging print statements.

     Revision 1.40  2004/05/07 22:01:08  crain
     @scr 4553 Redeem Gift Certificate

     Revision 1.39  2004/05/04 15:34:51  blj
     @scr 4603 - added new column to gift card table.

     Revision 1.38  2004/05/04 03:36:57  crain
     @scr 4553 Redeem Gift Certificate

     Revision 1.37  2004/04/29 19:45:50  crain
     @scr 4553 Redeem Gift Certificate

     Revision 1.36  2004/04/26 22:17:25  crain
     @scr 4553 Redeem Gift Certificate

     Revision 1.35  2004/04/21 15:01:49  blj
     @scr 4476 - updated for store credit rework for foreign currency.

     Revision 1.34  2004/04/20 12:49:25  jriggins
     @scr 3979 Added UpdatePriceAdjustedItemsDataTransaction and associated operations

     Revision 1.33  2004/04/16 22:30:34  jriggins
     @scr 3979 Added price adjustment columns

     Revision 1.32  2004/04/15 15:25:28  jriggins
     @scr 3979 Added price adjustment line item columns

     Revision 1.31  2004/04/14 20:07:38  lzhao
     @scr 3872 Redeem, change gift card request type from String to in.

     Revision 1.30  2004/04/09 19:08:15  bjosserand
     @scr 4093 Transaction Reentry

     Revision 1.29  2004/04/09 16:55:47  cdb
     @scr 4302 Removed double semicolon warnings.

     Revision 1.28  2004/04/08 22:04:15  bjosserand
     @scr 4093 Transaction Reentry

     Revision 1.27  2004/04/08 20:01:23  rsachdeva
     @scr 3906 reprint receipt empty/not found sales associate

     Revision 1.26  2004/04/08 01:40:22  crain
     @scr 4105 Foreign Currency

     Revision 1.25  2004/04/07 20:58:05  lzhao
     @scr 3872: remove unused lines.

     Revision 1.24  2004/04/01 21:54:44  crain
     @scr 4105 Foreign Currency

     Revision 1.23  2004/04/01 15:58:18  blj
     @scr 3872 Added training mode, toggled the redeem button based
     on transaction==null and fixed post void problems.

     Revision 1.22  2004/03/31 16:11:27  lzhao
     @scr 3872: add the part of transaction for gift card redeem.

     Revision 1.21  2004/03/24 17:45:33  blj
     @scr 0 removed unused imports

     Revision 1.20  2004/03/24 17:06:37  blj
     @scr 3871-3872 - Added the ability to reprint redeem transaction receipts and added a void receipt.

     Revision 1.19  2004/03/18 22:04:47  epd
     @scr 3561 Fixed busted SQL and result set processing

     Revision 1.18  2004/03/16 18:27:07  cdb
     @scr 0 Removed tabs from all java source code.

     Revision 1.17  2004/03/12 19:18:52  blj
     @scr 0 - removing FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER again

     Revision 1.16  2004/03/11 19:53:45  blj
     @scr 3871 - Updates and additions for Redeem Transactions.

     Revision 1.15  2004/03/02 22:44:28  cdb
     @scr 3588 Added ability to save Employee ID associated
     with a discount at the transaction level.

     Revision 1.14  2004/03/01 18:02:59  nrao
     Added new fields to the sql read statement for first name, last name and id type.

     Revision 1.13  2004/02/26 20:55:56  epd
     @scr 3561 Fixed a couple returns related db issues

     Revision 1.12  2004/02/24 19:40:01  nrao
     Removed erroneous field Field_Retail_Transaction_Line_Item_Sequence_Number (ai_ln_itm) from table
     TABLE_RETURN_TENDER_DATA (tr_rtn_tnd) in method readReturnTenders().

     Revision 1.11  2004/02/24 15:15:32  baa
     @scr 3561 returns enter item

     Revision 1.10  2004/02/18 21:09:07  epd
     @scr 3561
     Makes use of new Item Size code attribute

     Revision 1.9  2004/02/17 20:37:12  baa
     @scr 3561 returns

     Revision 1.7  2004/02/17 18:20:28  dcobb
     @scr 3381 Feature Enhancement:  Till Pickup and Loan
     Remove commented out code.

     Revision 1.6  2004/02/17 17:57:38  bwf
     @scr 0 Organize imports.

     Revision 1.5  2004/02/17 16:18:47  rhafernik
     @scr 0 log4j conversion

     Revision 1.4  2004/02/13 23:07:39  dcobb
     @scr 3381 Feature Enhancement:  Till Pickup and Loan
     Add to/from register to database.

     Revision 1.3  2004/02/12 17:13:17  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 23:25:26  bwf
     @scr 0 Organize imports.

     Revision 1.1.1.1  2004/02/11 01:04:27  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.18   Feb 10 2004 14:33:12   bwf
 * Refactor Echeck.
 *
 *    Rev 1.17   Feb 09 2004 17:12:34   crain
 * Added gift certificate item
 * Resolution for 3814: Issue Gift Certificate
 *
 *    Rev 1.16   Feb 05 2004 13:00:00   lzhao
 * add fields current balance and requestType to db gift card table.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.15   Jan 26 2004 17:22:00   cdb
 * Added support for Employee and Damage item discounts.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.14   Dec 31 2003 10:54:44   nrao
 * Added methods to read rows from the new Instant Credit table.
 *
 *    Rev 1.13   Dec 17 2003 15:56:32   nrao
 * Added PO amount and Agency name to Purchase Order Tender Line Item table.
 *
 *    Rev 1.12   Dec 05 2003 17:38:10   crain
 * Added foreign gift certificate
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.11   Nov 26 2003 16:57:54   crain
 * Added issuing store number field
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.10   Nov 20 2003 11:06:48   bwf
 * Update for new check info.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.9   Nov 19 2003 15:06:32   kll
 * intepret a StringBuffer from retrieving an inputstream
 *
 *    Rev 1.8   Nov 06 2003 17:06:40   epd
 * added ID information for Credit
 *
 *    Rev 1.7   Oct 30 2003 14:05:20   lzhao
 * remove readTransactionByIDOnly().
 *
 *    Rev 1.6   Oct 28 2003 17:24:52   lzhao
 * add transaction and operation for gift card reload
 *
 *    Rev 1.5   Oct 26 2003 09:05:36   blj
 * updated for money order tender.
 *
 *    Rev 1.4   Oct 02 2003 10:45:26   baa
 * fix for rss defect 1406
 * Resolution for 3402: RSS  PRF 1406 Tender Information on Void transactions does not looks correct
 *
 *    Rev 1.2   Sep 17 2003 17:39:28   ixb1
 * Modified readTransactionsAddColumns() - added table specific column names.
 * Resolution for 3367: MySQL Migration
 *
 *    Rev 1.1   Sep 03 2003 16:21:38   mrm
 * DB2 support
 * Resolution for POS SCR-3357: Add support needed by RSS
 *
 *    Rev 1.0   Aug 29 2003 15:32:16   CSchellenger
 * Initial revision.
 *
 *    Rev 1.38   Aug 20 2003 16:24:16   sfl
 * Make sure the void transaction get the amountTender and balanceDue values from the original transaction being voided.
 * Resolution for POS SCR-3337: Voided Gift Registry transaction has different 'Change Due' on dup. receipt
 *
 *    Rev 1.37   09 Jul 2003 19:15:38   mpm
 * Added support for price override authorization persistence.
 *
 *    Rev 1.36   Jul 04 2003 09:42:26   jgs
 * Modified to include read/write of id_ws_vd column.
 * Resolution for 2574: transactions not uniquely identifiable enough for voiding
 *
 *    Rev 1.35   Jul 01 2003 13:29:20   jgs
 * Added columns to look up which have been added in the 6.0 release.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.34   Jun 20 2003 16:05:28   bwf
 * If check allow alt currency.
 * Resolution for 2682: Voided Canadian Check trans, the amount of Cana. check are not correct on till summary report
 *
 *    Rev 1.33   Jun 19 2003 14:02:00   sfl
 * Read the store address information when retrieve transaction
 * from the database.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.32   Jun 17 2003 11:46:58   sfl
 * Improved the code.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.31   Jun 16 2003 13:53:22   sfl
 * Enhancement on reading back return line item's tax data for each tax jurisdiction from the database.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.30   Jun 12 2003 13:33:42   sfl
 * Read extra data from SaleReturnTaxLineItem table.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.29   May 30 2003 17:08:58   RSachdeva
 * Reprint new business customer's layaway trans, get "Device Offline" message
 * Resolution for POS SCR-2530: Reprint new business customer's layaway trans, get "Device Offline" message
 *
 *    Rev 1.28   May 15 2003 14:52:18   mpm
 * Test check-in.
 *
 *    Rev 1.27   May 10 2003 16:05:44   mpm
 * Added support for post-processing-status-code.
 *
 *    Rev 1.26   Apr 24 2003 17:26:32   sfl
 * Minor enhancement
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.25   Apr 24 2003 17:12:34   sfl
 * Implemented recording each tax jurisdiction's tax amount to support tax auditing.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.24   Apr 11 2003 12:55:50   baa
 * remove deprecation for get/setName methods in EmployeeIfc
 * Resolution for POS SCR-2155: Deprecation warnings - EmployeeIfc
 *
 *    Rev 1.23   Mar 25 2003 16:45:22   HDyer
 * Update tr_rtl table to add state and country columns per code review.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.22   Mar 20 2003 09:28:58   jgs
 * Changes due to code review.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.21   Mar 07 2003 10:39:20   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.20   Mar 05 2003 18:04:32   DCobb
 * Save and restore alteration information.
 * Resolution for POS SCR-1808: Alterations instructions not saved and not printed when trans. suspended
 *
 *    Rev 1.19   Feb 28 2003 16:57:26   sfl
 * Let one data base connection to serve two queries in
 * selectSaleReturnLineItems method
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.18   Feb 24 2003 10:44:28   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.17   Feb 21 2003 18:24:04   sfl
 * Split the standard line item query from the line item tax query so that even if line items have no tax, they can still be retrieved with transaction.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.16   Feb 15 2003 17:25:52   mpm
 * Merged 5.1 changes.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.15   Feb 14 2003 09:12:30   RSachdeva
 * Database Internationalization
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.14   Jan 31 2003 17:01:30   sfl
 * To support receipt reprint, the item tax amount needs to
 * keep longer precision when it is read from database.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.13   Jan 31 2003 13:34:32   sfl
 * When read item's modified retail price amount from the
 * Retail Price Modifier table, need to read it with longer
 * precision format so that the items total discount amount
 * can be calculated correctly without extra penny being generated.
 * Resolution for POS SCR-1749: POS 6.0 Tax Package
 *
 *    Rev 1.12   Jan 22 2003 14:15:20   DCobb
 * Check PLUItem for AlterationPLUItem.
 * Resolution for POS SCR-1807: Retrieve transactions with alterations does not allow alt. modification
 *
 *    Rev 1.11   Jan 20 2003 11:49:02   jgs
 * Added code to read/write columns for allow repeating sources, deal distribution, and percent off lowest priced Item.
 * Resolution for 103: New Advanced Pricing Features
 *
 *    Rev 1.10   Dec 23 2002 12:35:48   HDyer
 * Added personal ID fields.
 * Resolution for POS SCR-1854: Return Prompt for ID feature for POS 6.0
 *
 *    Rev 1.9   Nov 04 2002 11:25:50   DCobb
 * Added Mall Gift Certificate.
 * Resolution for POS SCR-1821: POS 6.0 Mall Gift Certificates
 *
 *    Rev 1.8   Nov 01 2002 09:19:40   DCobb
 * Corrected PO Tender.
 * Resolution for POS SCR-1799: POS 5.5 Purchase Order Tender Package
 *
 *    Rev 1.7   04 Oct 2002 13:56:58   sfl
 * Make sure to keep the item level tax data in longer precision format when read it from the SaleReturnTaxModifier table.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.6   03 Oct 2002 17:29:36   sfl
 * Retrieve previously calculated line item tax data to support
 * return items.
 *
 *    Rev 1.5   Sep 18 2002 13:13:22   DCobb
 * Add Purchase Order tender type.
 * Resolution for POS SCR-1799: POS 5.5 Purchase Order Tender Package
 *
 *    Rev 1.4   16 Jun 2002 09:02:44   vpn-mpm
 * Pulled out call for advanced pricing rule to facilitate extensibility.
 *
 *    Rev 1.3   16 Jun 2002 08:16:34   vpn-mpm
 * Merged in 5.1 changes.
 *
 *    Rev 1.2   05 Jun 2002 17:11:50   jbp
 * changes for pricing updates
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.1   Jun 04 2002 09:27:58   epd
 * Fixed use of Data Transaction within Data Operation
 * Resolution for Domain SCR-80: Fix Use of Data Transaction within Data Operation
 * $
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.domain.arts;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyType;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderConstantsIfc;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.data.JdbcUtilities;
import oracle.retail.stores.common.sql.SQLParameterValue;
import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.common.utility.ImageUtils;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.common.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedText;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.discount.DiscountRuleConstantsIfc;
import oracle.retail.stores.domain.discount.DiscountTargetIfc;
import oracle.retail.stores.domain.discount.ItemDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ItemDiscountAuditStrategyIfc;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAggregatorIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.factory.DomainObjectFactoryIfc;
import oracle.retail.stores.domain.financial.Bill;
import oracle.retail.stores.domain.financial.BillIfc;
import oracle.retail.stores.domain.financial.BillPayIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItem;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.order.OrderRecipientIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataElement;
import oracle.retail.stores.domain.returns.ReturnTenderDataElementIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemKitConstantsIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.MessageDTO;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupConstantsIfc;
import oracle.retail.stores.domain.stock.ProductGroupIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxConstantsIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.domain.tender.IntegratedChipCardDetailsIfc;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderDescriptor;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemConstantsIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.BankDepositTransactionIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.Transaction;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.DiscountUtility;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.domain.utility.Phone;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.domain.utility.SecurityOverrideIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.domain.utility.TenderUtility;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JdbcDataConnection;
import oracle.retail.stores.foundation.manager.device.EncipheredCardDataIfc;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataConnectionIfc;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.persistence.dao.tender.IntegratedChipCardDetailsDAOIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;
import oracle.retail.stores.persistence.utility.DBConstantsIfc;

/**
 * This operation reads a POS transaction from a database. It contains the
 * methods that read the transaction tables in the database.
 *
 */
public class JdbcReadTransaction extends JdbcReadlocalizedDescription
    implements ARTSDatabaseIfc, DiscountRuleConstantsIfc
{

    private static final long serialVersionUID = -2951364327898990804L;

    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(JdbcReadTransaction.class);

    /**
     * The performance logger
     */
    protected static final Logger perf = Logger.getLogger("PERF." + JdbcReadTransaction.class.getName());

    /**
     * The tax group id for default tax rule
     */
    protected static final int DEFAULT_TAX_RULE_GROUP_ID = -1;

    /**
     * tax mode constant -- used to determine if a retrived SaleReturnLineItem
     * should have mode of TAX_MODE_NON_TAXABLE.
     */
    public final static int TAX_MODE_NOT_SET = -1;

    protected static final String EXTERNAL_ORDER_ITEM_ID_TO_BE_FILLED_IN = "-1";

    /**
     * The column index of the order item system record fields
     */
    protected static final int IDX_ORDER_ITM_SYSTEM_RECORD_FIELD_START = 33;

    /**
     * The column index of the order system record fields
     */
    protected static final int IDX_ORDER_SYSTEM_RECORD_FIELD_START = 23;
    
    /**
     * Serialization capture time : Sale
     */
    protected static final String SALE_SERIALIZED_CAPTURE_TIME = "Sale";

    /**
     * Serialization Capture time : StoreReceiving
     */
    protected static final String STORE_RECEIVING_SERIALIZED_CAPTURE_TIME = "StoreReceiving";

    /**
     * Class constructor.
     */
    public JdbcReadTransaction()
    {
        setName("JdbcReadTransaction");
    }

    /**
     * Executes the SQL statements against the database.
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
            logger.debug("JdbcReadTransaction.execute");

        JdbcDataConnection connection = (JdbcDataConnection)dataConnection;
        TransactionIfc searchTransaction = (TransactionIfc)action.getDataObject();
        LocaleRequestor localeRequestor = getLocaleRequestor(searchTransaction);
        // Remove locale and locale requestor from transaction to enforce
        // explicitly
        // specifying the desired locale. In the future, use a
        // TransactionSearchCriteria
        // to specify a transaction and locale requestor explicitly.
        searchTransaction.setLocaleRequestor(null);

        // Send back the correct transaction (or lack thereof)
        TransactionIfc transaction = selectTransaction(connection, searchTransaction, localeRequestor);

        // if void transaction, handle original transaction
        if (transaction instanceof VoidTransactionIfc)
        {
            setOriginalTransaction(connection, (VoidTransactionIfc)transaction, localeRequestor);
        }

        dataTransaction.setResult(transaction);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.execute");
    }

    /**
     * Reads all transactions between the specified reporting periods.
     *
     * @param dataConnection a connection to the database
     * @param storeID The retail store ID
     * @param periods The reporting periods that begin and end the time period
     *            wanted.
     * @param localeRequestor the requested locales
     * @return The list of transactions.
     * @throws DataException thrown when an error occurs executing the SQL
     *             against the DataConnection, or when processing the ResultSet
     */
    public Vector<TransactionIfc> readTransactions(JdbcDataConnection dataConnection, String storeID,
            ReportingPeriodIfc[] periods, LocaleRequestor localeRequestor) throws DataException
    {
        logger.debug("JdbcReadTransaction.readTransactions()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);
        sql.addTable(TABLE_BUSINESS_DAY, ALIAS_BUSINESS_DAY);
        sql.addTable(TABLE_REPORTING_PERIOD, ALIAS_REPORTING_PERIOD);

        // add columns
        readTransactionsAddColumns(sql);

        // add qualifiers
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + getStoreID(storeID));

        // Join Transaction and Business Day
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = " + ALIAS_BUSINESS_DAY + "."
                + FIELD_BUSINESS_DAY_DATE);
        // Join Business Day and Reporting Period
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_YEAR + " = " + ALIAS_REPORTING_PERIOD + "."
                + FIELD_FISCAL_YEAR);
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_WEEK_NUMBER + " = " + ALIAS_REPORTING_PERIOD + "."
                + FIELD_FISCAL_WEEK_NUMBER);
        sql.addQualifier(ALIAS_BUSINESS_DAY + "." + FIELD_FISCAL_DAY_NUMBER + " = " + ALIAS_REPORTING_PERIOD + "."
                + FIELD_FISCAL_DAY_NUMBER);
        // No training mode transactions
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_TRAINING_FLAG + " = " + String.valueOf(0));
        // Only completed transaction (not canceled or suspended)
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_STATUS_CODE + " = "
                + String.valueOf(TransactionIfc.STATUS_COMPLETED));

        if (periods.length > 0)
        {
            String fy = periods[0].getFiscalYear();
            StringBuffer fyList = new StringBuffer("'" + fy + "'");
            StringBuffer idList = new StringBuffer();
            for (int i = 0; i < periods.length; ++i)
            {
                if (!fy.equals(periods[i].getFiscalYear()))
                {
                    fy = periods[i].getFiscalYear();
                    fyList.append(", '" + fy + "'");
                }

                if (i > 0)
                {
                    idList.append(", ");
                }

                idList.append(periods[i].getReportingPeriodID());
            }

            int type = periods[0].getReportingPeriodType();
            sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_TYPE_CODE + " = '"
                    + ReportingPeriodIfc.REPORTING_PERIOD_CODES[type] + "'");
            sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_REPORTING_PERIOD_ID + " in (" + idList + ")");
            sql.addQualifier(ALIAS_REPORTING_PERIOD + "." + FIELD_FISCAL_YEAR + " in (" + fyList + ")");
        }

        Vector<TransactionIfc> transVector = null;
        try
        {
            // build sub-select to exclude post voided transaction
            ARTSExcludePostVoidSQL.buildSQL(sql);
            transVector = readTransactionsExecuteAndParse(dataConnection, sql, false, localeRequestor);
            readStoreLocations(dataConnection, transVector, localeRequestor);
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTransactions()");

        return (transVector);
    }

    /**
     * Reads all transactions for a specified transaction ID for an optionally
     * specified date.
     *
     * @param dataConnection a connection to the database
     * @param transaction transaction information to search for
     * @param localeRequestor The request locales
     * @return The list of transactions.
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public TransactionIfc[] readTransactionsByID(JdbcDataConnection dataConnection, TransactionIfc transaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTransactionsByID()");
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

        // add columns
        readTransactionsAddColumns(sql);

        // add qualifiers for the transaction ID
        sql.addQualifier(ALIAS_TRANSACTION, FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addQualifier(ALIAS_TRANSACTION, FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER,
                getTransactionSequenceNumber(transaction));

        // add qualifiers if status is completed or voided
        if(transaction.getTransactionStatus()==TransactionIfc.STATUS_COMPLETED ||
        transaction.getTransactionStatus()==TransactionIfc.STATUS_VOIDED)
        {
            sql.addQualifier("(" + FIELD_TRANSACTION_STATUS_CODE + " = " + TransactionIfc.STATUS_COMPLETED +
            " OR " + FIELD_TRANSACTION_STATUS_CODE + " = " + TransactionIfc.STATUS_VOIDED + ")");
        }

        // match training mode
        sql.addQualifier(ALIAS_TRANSACTION, FIELD_TRANSACTION_TRAINING_FLAG, getTrainingMode(transaction));

        // see if businessDate is specified
        if (transaction.getBusinessDay() != null)
        {
            sql.addQualifier(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        }

        sql.addOrdering(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE);

        // set up transaction array
        TransactionIfc[] transactions = null;

        Vector<TransactionIfc> transVector = new Vector<TransactionIfc>(2);
        transVector = readTransactionsExecuteAndParse(dataConnection, sql, false, localeRequestor);

        transactions = new TransactionIfc[transVector.size()];
        transVector.copyInto(transactions);

        readStoreLocations(dataConnection, transVector, localeRequestor);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTransactionsByID()");

        return (transactions);
    }

    /**
     * Reads all transactions for a specified transaction ID for an optionally
     * specified date.
     *
     * @param dataConnection a connection to the database
     * @param transaction transaction information to search for
     * @param localeRequestor The request locales
     * @return The list of transactions.
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     * @since 14.1
     *                
     */
    public TransactionIfc[] readTrainingTransactionsByID(JdbcDataConnection dataConnection, TransactionIfc transaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTrainingTransactionsByID()");
        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

        // add columns
        readTransactionsAddColumns(sql);

        // add qualifiers for the transaction ID
        sql.addQualifier(ALIAS_TRANSACTION, FIELD_TRANSACTION_SEQUENCE_NUMBER,
                getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_TRANSACTION, FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(ALIAS_TRANSACTION, FIELD_RETAIL_STORE_ID, getStoreID(transaction));

        // add qualifiers if status is completed or voided
        if(transaction.getTransactionStatus()==TransactionIfc.STATUS_COMPLETED ||
        transaction.getTransactionStatus()==TransactionIfc.STATUS_VOIDED)
        {
            sql.addQualifier("(" + FIELD_TRANSACTION_STATUS_CODE + " = " + TransactionIfc.STATUS_COMPLETED +
            " OR " + FIELD_TRANSACTION_STATUS_CODE + " = " + TransactionIfc.STATUS_VOIDED + ")");
        }

       // see if businessDate is specified
        if (transaction.getBusinessDay() != null)
        {
            sql.addQualifier(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        }

        sql.addOrdering(ALIAS_TRANSACTION, FIELD_BUSINESS_DAY_DATE);

        // set up transaction array
        TransactionIfc[] transactions = null;

        Vector<TransactionIfc> transVector = new Vector<TransactionIfc>();
        transVector = readTransactionsExecuteAndParse(dataConnection, sql, false, localeRequestor);

        transactions = new TransactionIfc[transVector.size()];
        transVector.copyInto(transactions);

        readStoreLocations(dataConnection, transVector, localeRequestor);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTrainingTransactionsByID()");

        return (transactions);
    }

    /**
     * Reads a transaction for batch processing. This query recovers all
     * training-mode and voided transactions.
     *
     * @param dataConnection a connection to the database
     * @param searchTransaction transaction information to search for
     * @param localeRequestor The request locales
     * @return transaction matching specified ID and business date
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public TransactionIfc readTransactionForBatch(JdbcDataConnection dataConnection, TransactionIfc searchTransaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTransactionForBatch()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

        // add columns
        readTransactionsAddColumns(sql);

        // add qualifiers for the transaction ID
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + getStoreID(searchTransaction));
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + getWorkstationID(searchTransaction));
        sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(searchTransaction));

        // see if businessDate is specified
        if (searchTransaction.getBusinessDay() != null)
        {
            sql.addQualifier(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = "
                    + getBusinessDayString(searchTransaction));
        }

        // read transaction and retrieve for return
        // KLM: Updating this call to retrieve store coupon line items. This was
        // done for the sake of POSLog that was failing when trying to output a
        // transaction that contained store coupon line items.
        //
        Vector<TransactionIfc> transVector = readTransactionsExecuteAndParse(dataConnection, sql, true, localeRequestor);
        TransactionIfc transaction = transVector.firstElement();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTransactionForBatch()");

        return (transaction);
    }

    /**
     * Adds columns for readTransactions SQL. This is done to match result set
     * used in readTransactionsExecuteAndParse().
     *
     * @param sql select statement object
     */
    public void readTransactionsAddColumns(SQLSelectStatement sql)
    {
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_TRAINING_FLAG);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_END_DATE_TIMESTAMP);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_BEGIN_DATE_TIMESTAMP);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_TYPE_CODE);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_OPERATOR_ID);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_WORKSTATION_ID);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_STATUS_CODE);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TENDER_REPOSITORY_ID);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_CUSTOMER_INFO_TYPE);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_CUSTOMER_INFO);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_POST_PROCESSING_STATUS_CODE);

        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_REENTRY_FLAG);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_AUDIT_LOG_EMPLOYEE_ID);
        sql.addColumn(ALIAS_TRANSACTION + "." + FIELD_TRANSACTION_SALES_ASSOCIATE_MODIFIED);
      
    }

    /**
     * Executes specified SQL to read and parse transactions. Columns in select
     * must be built using readTransactionsAddColumns() or result set will not
     * be parseable.
     *
     * @param dataConnection JDBC data connection
     * @param sql select statement object
     * @param localeRequestor The request locales
     * @param retrieveStoreCoupons designates whether or not to retrieve store
     *            coupon line items
     * @return Vector of transactions (TransactionIfc)
     * @throws DataException if there are any errors reading the data or parsing
     *             the result set.
     */
    public Vector<TransactionIfc> readTransactionsExecuteAndParse(JdbcDataConnection dataConnection,
            SQLSelectStatement sql, boolean retrieveStoreCoupons, LocaleRequestor localeRequestor) throws DataException
    {
        Vector<TransactionIfc> transVector = new Vector<TransactionIfc>(2);
        Vector<TransactionIfc> completedTransVector = new Vector<TransactionIfc>(2);
        try
        {
            // execute sql and get result set
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            // loop through result set
            while (rs.next())
            {
                // parse the data from the database
                int index = 0;
                boolean trainingFlag = getBooleanFromString(rs, ++index);
                Timestamp endTimestamp = rs.getTimestamp(++index);
                Timestamp beginTimestamp = rs.getTimestamp(++index);
                int transType = rs.getInt(++index);
                int sequenceNumber = rs.getInt(++index);
                String operatorID = getSafeString(rs, ++index);
                String storeID = getSafeString(rs, ++index);
                String workstationID = getSafeString(rs, ++index);
                EYSDate businessDate = getEYSDateFromString(rs, ++index);
                int status = rs.getInt(++index);
                String tillID = getSafeString(rs, ++index);
                String customerInfoType = getSafeString(rs, ++index);
                String customerInfoData = getSafeString(rs, ++index);
                int postProcessingStatus = rs.getInt(++index);
                boolean reentryMode = getBooleanFromString(rs, ++index);
               
                // Instantiate Store
                StoreIfc store = instantiateStore();
                store.setStoreID(storeID);

                // Instantiate Workstation
                WorkstationIfc workstation = instantiateWorkstation();
                workstation.setWorkstationID(workstationID);
                workstation.setStore(store);

                // Instantiate Employee
                // Note: operator is a reserved word
                EmployeeIfc operatingEmployee = instantiateEmployee();
                operatingEmployee.setEmployeeID(operatorID);

                // Instantiate Transaction
                TransactionIfc transaction = createTransaction(transType);

                transaction.setTimestampBegin(timestampToEYSDate(beginTimestamp));
                transaction.setTimestampEnd(timestampToEYSDate(endTimestamp));
                transaction.setTransactionSequenceNumber(sequenceNumber);
                transaction.setBusinessDay(businessDate);
                transaction.setTransactionStatus(status);
                transaction.setTillID(tillID);
                transaction.setTrainingMode(trainingFlag);

                transaction.setWorkstation(workstation);
                transaction.setCashier(operatingEmployee);
                transaction.setPostProcessingStatus(postProcessingStatus);
                transaction.setReentryMode(reentryMode);

                // set customer info, as needed
                if (!Util.isEmpty(customerInfoType))
                {
                    CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
                    if (customerInfo == null)
                    {
                        customerInfo = DomainGateway.getFactory().getCustomerInfoInstance();
                    }
                    int infoType = Integer.parseInt(customerInfoType);
                    // if skipped, set variable accordingly
                    if (Util.isObjectEqual(customerInfoData, CustomerInfoIfc.SKIPPED))
                    {
                        customerInfo.setSkipped(true);
                    }
                    // disregard if type is none
                    if (infoType != CustomerInfoIfc.CUSTOMER_INFO_TYPE_NONE)
                    {
                        customerInfo.setCustomerInfoType(infoType);
                        customerInfo.setCustomerInfo(infoType, customerInfoData);
                        transaction.setCustomerInfo(customerInfo);
                    }
                }

                if (transaction instanceof Transaction)
                {
                    ((Transaction)transaction).buildTransactionID();
                }

                // add the transaction to the vector
                transVector.addElement(transaction);
            }

            // close result set
            rs.close();

            TransactionIfc trans = null;
            // walk through vector and retrieve additional data
            Enumeration<TransactionIfc> e = transVector.elements();
            while (e.hasMoreElements())
            {
                trans = e.nextElement();

                // Get the employee information
                trans.setCashier(getEmployee(dataConnection, trans.getCashier().getEmployeeID()));

                // read all additional data
                try
                {
                    readAllTransactionData(dataConnection, trans, localeRequestor, retrieveStoreCoupons);
                    completedTransVector.add(trans);
                }
                catch (DataException de)
                {
                    if (e.hasMoreElements() || completedTransVector.size() > 0)
                    {
                        logger
                                .error("Encounterd an exception attempting to read multiple transactions; continuing on.");
                    }
                    else
                    {
                        throw de;
                    }
                }
            }
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "transaction table", e);
        }

        if (completedTransVector.isEmpty())
        {
            logger.warn("No transactions found");
            throw new DataException(DataException.NO_DATA, "No transactions found");
        }

        return (completedTransVector);
    }

    /**
     * Selects from the transaction table.
     *
     * @param dataConnection a connection to the database
     * @param inputTransaction the transaction coming from business logic
     * @param localeRequestor The request locales
     * @return the transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public TransactionIfc selectTransaction(JdbcDataConnection dataConnection, TransactionIfc inputTransaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        return selectTransaction(dataConnection, inputTransaction, (String)null, localeRequestor);
    }

    /**
     * Selects from the transaction table.
     *
     * @param dataConnection a connection to the database
     * @param inputTransaction the transaction coming from business logic
     * @param orderID order identifier
     * @param localeRequestor The request locales
     * @return the transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public TransactionIfc selectTransaction(JdbcDataConnection dataConnection, TransactionIfc inputTransaction,
            String orderID, LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTransaction()");
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_TRANSACTION);

        /*
         * Add Columns
         */
        sql.addColumn(FIELD_TRANSACTION_TRAINING_FLAG);
        sql.addColumn(FIELD_TRANSACTION_END_DATE_TIMESTAMP);
        sql.addColumn(FIELD_TRANSACTION_BEGIN_DATE_TIMESTAMP);
        sql.addColumn(FIELD_TRANSACTION_TYPE_CODE);
        sql.addColumn(FIELD_OPERATOR_ID);
        sql.addColumn(FIELD_TRANSACTION_STATUS_CODE);
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID);
        sql.addColumn(FIELD_TRANSACTION_POST_PROCESSING_STATUS_CODE);
        sql.addColumn(FIELD_TRANSACTION_SALES_ASSOCIATE_MODIFIED);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(inputTransaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(inputTransaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(inputTransaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(inputTransaction));
        sql.addQualifier(FIELD_TRANSACTION_TRAINING_FLAG + " = " + getTrainingMode(inputTransaction));

        // if a status code was passed in, use it
        if (inputTransaction.getTransactionStatus() != TransactionIfc.STATUS_UNKNOWN)
        {
            sql.addQualifier(FIELD_TRANSACTION_STATUS_CODE + " = " + getStatusCode(inputTransaction));
        }

        TransactionIfc transaction = null;
        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                logger.warn("JdbcReadTransaction: transaction not found!");
                throw new DataException(DataException.NO_DATA, "transaction not found");
            }

            /*
             * Grab the fields selected from the database
             */
            int index = 0;
            boolean trainingFlag = getBooleanFromString(rs, ++index);
            Timestamp endTimestamp = rs.getTimestamp(++index);
            Timestamp beginTimestamp = rs.getTimestamp(++index);
            int transType = rs.getInt(++index);
            String operatorID = getSafeString(rs, ++index);
            int statusCode = rs.getInt(++index);
            String tillID = rs.getString(++index);
            int postProcessingStatusCode = rs.getInt(++index);
            boolean saleAssociateModifiedFlag = getBooleanFromString(rs, ++index);

            transaction = createTransaction(transType);

            transaction.setTrainingMode(trainingFlag);
            transaction.setTimestampBegin(timestampToEYSDate(beginTimestamp));
            transaction.setTimestampEnd(timestampToEYSDate(endTimestamp));
            transaction.setWorkstation(inputTransaction.getWorkstation());
            transaction.setTransactionSequenceNumber(inputTransaction.getTransactionSequenceNumber());
            transaction.setBusinessDay(inputTransaction.getBusinessDay());
            transaction.setTillID(tillID);
            transaction.setTransactionStatus(statusCode);
            transaction.setPostProcessingStatus(postProcessingStatusCode);

            if (transaction instanceof SaleReturnTransactionIfc)
            {
                ((SaleReturnTransactionIfc)transaction).setSalesAssociateModifiedFlag(saleAssociateModifiedFlag);
            }

            if (transaction instanceof Transaction)
            {
                ((Transaction)transaction).buildTransactionID();
            }

            rs.close();

            if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
                    || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
                    || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
                    || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                ((OrderTransactionIfc)transaction).setOrderID(orderID);
            }

            // Store the cashier information in the transaction
            transaction.setCashier(getEmployee(dataConnection, operatorID));
            // Store the capture customer information in the transaction, if it
            // exists.
            transaction.setCaptureCustomer(selectCaptureCustomer(dataConnection, transaction, localeRequestor));

            readAllTransactionData(dataConnection, transaction, localeRequestor, false);

        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "transaction table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTransaction()");

        return (transaction);
    }

    /**
     * This method reads from the capture customer table in the db.
     *
     * @param dc the JDBC data connection.
     * @param transaction the transaction.
     * @param localeRequestor The request locales
     * @return a new capture customer object, if one exists.
     * @throws DataException
     */
    protected CaptureCustomerIfc selectCaptureCustomer(JdbcDataConnection dc, TransactionIfc transaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.setTable(TABLE_CAPTURE_CUSTOMER);

        sql.addQualifier(FIELD_RETAIL_STORE_ID,
                inQuotes(transaction.getWorkstation().getStore().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getWorkstation().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                Integer.parseInt(String.valueOf(transaction.getTransactionSequenceNumber())));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(transaction.getBusinessDay()));

        sql.addColumn(FIELD_CAPTURE_CUSTOMER_FIRST_NAME);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_LAST_NAME);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_ADDRESS_LINE_1);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_ADDRESS_LINE_2);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_CITY);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_COUNTRY);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_STATE);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_POSTAL);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_POSTAL_EXT);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE_TYPE);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_PHONE);
        sql.addColumn(FIELD_CAPTURE_CUSTOMER_IDTYPE);

        try
        {
            dc.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dc.getResult();

            if (!rs.next())
            {
                return null;
            }

            int index = 0;
            String firstName = getSafeString(rs, ++index);
            String lastName = getSafeString(rs, ++index);
            String addressLine1 = getSafeString(rs, ++index);
            String addressLine2 = getSafeString(rs, ++index);
            String city = getSafeString(rs, ++index);
            String country = getSafeString(rs, ++index);
            String state = getSafeString(rs, ++index);
            String postalCode = getSafeString(rs, ++index);
            String postalCodeExt = getSafeString(rs, ++index);
            String phoneType = getSafeString(rs, ++index);
            String phone = getSafeString(rs, ++index);
            String idType = getSafeString(rs, ++index);

            CaptureCustomerIfc customer = DomainGateway.getFactory().getCaptureCustomerInstance();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setAddressLine(1, addressLine1);
            customer.setAddressLine(2, addressLine2);
            customer.setCity(city);
            customer.setCountry(country);
            customer.setState(state);
            customer.setPostalCode(postalCode);
            customer.setPostalCodeExt(postalCodeExt);
            customer.setPhoneType(Phone.getPhoneTypeCode(phoneType));
            customer.setPhoneNumber(phone);

            customer.setPersonalIDType(getInitializedLocalizedReasonCode(dc, transaction.getTransactionIdentifier().getStoreID(),
                    idType, CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES, localeRequestor));

            return customer;
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dc.logSQLException(se, "capture customer table");
            throw new DataException(DataException.SQL_ERROR, "capture customer table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "capture customer table", e);
        }
    }

    /**
     * Creates the proper transaction object based on <code>transType</code>.
     *
     * @param transType the type of transaction
     * @return a new transaction
     */
    protected TransactionIfc createTransaction(int transType)
    {
        TransactionIfc transaction = null;

        switch (transType)
        {
            case TransactionIfc.TYPE_SALE:
            case TransactionIfc.TYPE_RETURN:
            case TransactionIfc.TYPE_EXCHANGE:
                transaction = instantiateSaleReturnTransaction();
                break;

            case TransactionIfc.TYPE_VOID:
                transaction = instantiateVoidTransaction();
                break;

            case TransactionIfc.TYPE_NO_SALE:
                transaction = instantiateNoSaleTransaction();
                break;

            case TransactionIfc.TYPE_HOUSE_PAYMENT:
                transaction = instantiatePaymentTransaction();
                break;
            case TransactionIfc.TYPE_INSTANT_CREDIT_ENROLLMENT:
                transaction = instantiateInstantCreditTransaction();
                break;
            case TransactionIfc.TYPE_LOAN_TILL:
            case TransactionIfc.TYPE_PICKUP_TILL:
            case TransactionIfc.TYPE_PAYIN_TILL:
            case TransactionIfc.TYPE_PAYOUT_TILL:
            case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                transaction = instantiateTillAdjustmentTransaction();
                break;
            case TransactionIfc.TYPE_OPEN_STORE:
            case TransactionIfc.TYPE_CLOSE_STORE:
                transaction = DomainGateway.getFactory().getStoreOpenCloseTransactionInstance();
                break;
            case TransactionIfc.TYPE_OPEN_REGISTER:
            case TransactionIfc.TYPE_CLOSE_REGISTER:
                transaction = DomainGateway.getFactory().getRegisterOpenCloseTransactionInstance();
                break;
            case TransactionIfc.TYPE_OPEN_TILL:
            case TransactionIfc.TYPE_CLOSE_TILL:
            case TransactionIfc.TYPE_SUSPEND_TILL:
            case TransactionIfc.TYPE_RESUME_TILL:
                transaction = DomainGateway.getFactory().getTillOpenCloseTransactionInstance();
                break;
            case TransactionIfc.TYPE_LAYAWAY_INITIATE:
            case TransactionIfc.TYPE_LAYAWAY_COMPLETE:
            case TransactionIfc.TYPE_LAYAWAY_DELETE:
                transaction = instantiateLayawayTransaction();
                break;
            case TransactionIfc.TYPE_ORDER_CANCEL:
            case TransactionIfc.TYPE_ORDER_INITIATE:
            case TransactionIfc.TYPE_ORDER_PARTIAL:
            case TransactionIfc.TYPE_ORDER_COMPLETE:
                transaction = DomainGateway.getFactory().getOrderTransactionInstance();
                break;
            case TransactionIfc.TYPE_LAYAWAY_PAYMENT:
                transaction = instantiateLayawayPaymentTransaction();
                break;
            case TransactionIfc.TYPE_BANK_DEPOSIT_STORE:
                transaction = DomainGateway.getFactory().getBankDepositTransactionInstance();
                break;
            case TransactionIfc.TYPE_REDEEM:
                transaction = DomainGateway.getFactory().getRedeemTransactionInstance();
                break;
            case TransactionIfc.TYPE_BILL_PAY:
                transaction = DomainGateway.getFactory().getBillPayTransactionInstance();
                break;
            case TransactionIfc.TYPE_STATUS_CHANGE:
                transaction = DomainGateway.getFactory().getStatusChangeTransactionInstance();
                break;
            case TransactionIfc.TYPE_UNKNOWN:
                transaction = instantiateTransaction();
                break;
            default:
                logger.error("JdbcReadTransaction: Unknown Transaction type: " + Integer.toString(transType) + "");
                logger.warn("Instantiating Transaction");
                transaction = instantiateTransaction();
                break;
        }

        transaction.setTransactionType(transType);

        return (transaction);
    }

    /**
     * Gets the rest of the data associated with transaction.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @param retrieveStoreCoupons designates whether or not to retrieve store
     *            coupon line items
     * @param localeRequestor The request locales
     * @return the transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public TransactionIfc readAllTransactionData(JdbcDataConnection dataConnection, TransactionIfc transaction,
            LocaleRequestor localeRequestor, boolean retrieveStoreCoupons) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readAllTransactionData()");
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering readAllTransactionData(JdbcDataConnection, TransactionIfc, boolean)");
        }
        // add capture customer information:
        transaction.setCaptureCustomer(selectCaptureCustomer(dataConnection, transaction, localeRequestor));

        if (transaction instanceof SaleReturnTransactionIfc)
        {

            SaleReturnTransactionIfc retailTransaction = (SaleReturnTransactionIfc)transaction;
            // do not read the transaction details if a canceled layaway
            // transaction
            if (transaction instanceof LayawayTransactionIfc ||
                    transaction instanceof OrderTransactionIfc)
            {
                if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
                {
                    selectSaleReturnTransaction(dataConnection, retailTransaction, localeRequestor,
                            retrieveStoreCoupons);
                }
            }
            else
            {
                selectSaleReturnTransaction(dataConnection, retailTransaction, localeRequestor, retrieveStoreCoupons);
            }

            if (transaction instanceof LayawayTransactionIfc)
            {
                // read layaway for transaction
                LayawayTransactionIfc layawayTransaction = (LayawayTransactionIfc)transaction;
                // no payment required if it's a suspended transaction or
                // canceled transaction
                if (transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED
                        && transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED
                        && transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_CANCELED
                        && transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_RETRIEVED)
                {
                    selectPaymentForLayawayTransaction(dataConnection, layawayTransaction);
                    // set tender transaction totals for payment
                    layawayTransaction.getTransactionTotals().updateTransactionTotalsForPayment(
                            layawayTransaction.getPayment().getPaymentAmount());
                }
                // if layaway initiate, we can use the transaction ID to find
                // the layaway; otherwise, we use the payment record. Since a
                // suspended layaway initiate transaction will not have a payment
                // transaction, we need to use the transaction to find the layaway.
                if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
                {
                    if (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE)
                    {
                        selectLayawayForTransaction(dataConnection, layawayTransaction, localeRequestor);
                    }
                    else
                    {
                        selectLayawayForPayment(dataConnection, layawayTransaction, localeRequestor);
                    }
                    // get payment history info for layaway
                    LayawayIfc layaway = layawayTransaction.getLayaway();
                    layaway = selectLayawayPaymentHistoryInfo(dataConnection, layaway);
                    layawayTransaction.setLayaway(layaway);

                    layawayTransaction.getLayaway().setCustomer(layawayTransaction.getCustomer());
                }
            }
            else if (transaction instanceof OrderTransactionIfc &&
                    transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
            {
                // read in payment for order transaction if not suspended
                OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
                // no payment required if it's a suspended transaction
                if (transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED
                        && transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_CANCELED
                        && transaction.getTransactionStatus() != TransactionIfc.STATUS_SUSPENDED_RETRIEVED)
                {
                    selectPaymentForOrderTransaction(dataConnection, orderTransaction);
                    readOrderInfo(dataConnection, orderTransaction);
                }
                // get payment history info for order
                orderTransaction = selectOrderPaymentHistoryInfo(dataConnection, orderTransaction);
            }

            // Make sure we update all the necessary information
            retailTransaction.getTransactionTotals().updateTransactionTotals(retailTransaction.getLineItems(),
                    retailTransaction.getTransactionDiscounts(), retailTransaction.getTransactionTax());
        }
        else if (transaction instanceof VoidTransactionIfc)
        {
            VoidTransactionIfc voidTransaction = (VoidTransactionIfc)transaction;
            selectVoidTransaction(dataConnection, voidTransaction, localeRequestor);
            // retrieve the reason code text
        }
        else if (transaction instanceof PaymentTransactionIfc)
        {
            // if transaction is canceled , there is no payment being saved in
            // the database
            if (transaction.getTransactionStatus() != Transaction.STATUS_CANCELED)
            {

                PaymentTransactionIfc retailTransaction = (PaymentTransactionIfc)transaction;
                selectPaymentTransaction(dataConnection, retailTransaction, localeRequestor);

                /*
                 * Make sure we update transaction totals for payment
                 * transaction
                 */
                retailTransaction.getTransactionTotals().updateTransactionTotalsForPayment(
                        retailTransaction.getPaymentAmount());

                if (transaction instanceof LayawayPaymentTransactionIfc)
                {
                    selectLayawayForPaymentTransaction(dataConnection, transaction, localeRequestor);

                    // read layaway for transaction
                    LayawayPaymentTransactionIfc layawayTransaction = (LayawayPaymentTransactionIfc)transaction;
                    // get payment history info for layaway
                    LayawayIfc layaway = layawayTransaction.getLayaway();
                    layaway = selectLayawayPaymentHistoryInfo(dataConnection, layaway);
                    layawayTransaction.setLayaway(layaway);
                    layawayTransaction.getLayaway().setCustomer(layawayTransaction.getCustomer());
                }
            }
        }

        else if (transaction instanceof BillPayTransactionIfc)
        {
            selectBillPayTransaction(dataConnection, (BillPayTransactionIfc) transaction);
        }
        else if (transaction instanceof InstantCreditTransactionIfc)
        {
            InstantCreditTransactionIfc icTrans = (InstantCreditTransactionIfc)transaction;
            selectInstantCreditTransaction(dataConnection, icTrans);
        }
        else if (transaction instanceof TillAdjustmentTransactionIfc)
        {
            TillAdjustmentTransactionIfc tillAdjustmentTransaction = (TillAdjustmentTransactionIfc)transaction;
            selectTillAdjustmentTransaction(dataConnection, tillAdjustmentTransaction);
        }
        else if (transaction instanceof NoSaleTransactionIfc)
        {
            selectNoSaleTransaction(dataConnection, (NoSaleTransactionIfc)transaction, localeRequestor);
        }
        else if (transaction instanceof StoreOpenCloseTransactionIfc)
        {
            selectStoreOpenCloseTransaction(dataConnection, (StoreOpenCloseTransactionIfc)transaction);
        }
        else if (transaction instanceof RegisterOpenCloseTransactionIfc)
        {
            selectRegisterOpenCloseTransaction(dataConnection, (RegisterOpenCloseTransactionIfc)transaction);
        }
        else if (transaction instanceof TillOpenCloseTransactionIfc)
        {
            selectTillOpenCloseTransaction(dataConnection, (TillOpenCloseTransactionIfc)transaction);
        }
        else if (transaction instanceof BankDepositTransactionIfc)
        {
            selectBankDepositTransaction(dataConnection, (BankDepositTransactionIfc)transaction);
        }
        else if (transaction instanceof RedeemTransactionIfc)
        {
            if(transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
            {
                selectRedeemTransaction(dataConnection, (RedeemTransactionIfc)transaction);
            }
        }
        else if (transaction instanceof StatusChangeTransactionIfc)
        {
            selectStatusChangeTransaction(dataConnection, (StatusChangeTransactionIfc)transaction);
        }
        else
        {
            /*
             * The listed transaction types below require no additional
             * information from the database
             */
            int transactionType = transaction.getTransactionType();
            if (transactionType != TransactionIfc.TYPE_NO_SALE && transactionType != TransactionIfc.TYPE_OPEN_TILL
                    && transactionType != TransactionIfc.TYPE_CLOSE_TILL
                    && transactionType != TransactionIfc.TYPE_SUSPEND_TILL
                    && transactionType != TransactionIfc.TYPE_RESUME_TILL)
            {
                logger.error("JdbcReadTransaction: Unsupported transaction type " + Integer.toString(transactionType)
                        + " (" + transaction.getClass().getName() + ")");
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readAllTransactionData()");
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting readAllTransactionData(JdbcDataConnection, TransactionIfc, boolean)");
        }
        return (transaction);
    }

    /**
     * Selects from the retail transaction table.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @param retrieveStoreCoupons designates whether or not to retrieve store
     *            coupon line items
     * @param localeRequestor The request locales
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectSaleReturnTransaction(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction,
            LocaleRequestor localeRequestor, boolean retrieveStoreCoupons) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnTransaction()");
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_RETAIL_TRANSACTION, ALIAS_RETAIL_TRANSACTION);
        sql.addTable(TABLE_ADDRESS, ALIAS_ADDRESS);
        sql.addTable(TABLE_RETAIL_STORE, ALIAS_RETAIL_STORE);

        /*
         * Add Columns
         */
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CUSTOMER_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_IRS_CUSTOMER_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_GIFT_REGISTRY_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_SUSPENDED_TRANSACTION_REASON_CODE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_EMPLOYEE_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_SEND_PACKAGE_COUNT);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_SEND_CUSTOMER_TYPE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_SEND_CUSTOMER_PHYSICALLY_PRESENT);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_TRANSACTION_LEVEL_SEND);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_ORDER_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_ENCRYPTED_PERSONAL_ID_NUMBER);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_MASKED_PERSONAL_ID_NUMBER);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_PERSONAL_ID_REQUIRED_TYPE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_PERSONAL_ID_STATE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_PERSONAL_ID_COUNTRY);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_AGE_RESTRICTED_DOB);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_EXTERNAL_ORDER_ID);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_EXTERNAL_ORDER_NUMBER);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_EXTERNAL_ORDER_TYPE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_CONTRACT_SIGNATURE_REQUIRED_FLAG);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_CITY);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_STATE);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_COUNTRY);
        sql.addColumn(ALIAS_ADDRESS + "." + FIELD_CONTACT_POSTAL_CODE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION + "." + FIELD_TRANSACTION_RETURN_TICKET);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION+ "." + FIELD_TRANSACTION_LEVEL_GIFT_RECEIPT_FLAG); 
        sql.addColumn(ALIAS_RETAIL_TRANSACTION+ "." + FIELD_TRANSACTION_CURRENCY);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION+ "." + FIELD_TRANSACTION_COUNTRY);        

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + getBusinessDayString(transaction));
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION + "." + FIELD_RETAIL_STORE_ID + " = " + ALIAS_RETAIL_STORE + "."
                + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(ALIAS_RETAIL_STORE + "." + FIELD_PARTY_ID + " = " + ALIAS_ADDRESS + "." + FIELD_PARTY_ID);

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                logger.warn("retail transaction not found!");
                throw new DataException(DataException.NO_DATA, "transaction not found");
            }

            int index = 0;
            String customerId = getSafeString(rs, ++index);
            String irsCustomerId = getSafeString(rs, ++index);
            String giftRegistryID = getSafeString(rs, ++index);
            String suspendReasonCode = getSafeString(rs, ++index);
            String salesAssociateID = getSafeString(rs, ++index);
            int sendPackagesCount = rs.getInt(++index);
            String sendCustomerType = getSafeString(rs, ++index);
            boolean sendCustomerPhysicallyPresent = getBooleanFromString(rs, ++index);
            boolean transactionLevelSend = getBooleanFromString(rs, ++index);
            String orderID = getSafeString(rs, ++index);
            String encryptedPersonalIDNumber = getSafeString(rs, ++index);
            String maskedPersonalIDNumber = getSafeString(rs, ++index);
            String personalIDType = getSafeString(rs, ++index);
            String personalIDState = getSafeString(rs, ++index);
            String personalIDCountry = getSafeString(rs, ++index);
            EYSDate ageRestrictedDOB = getEYSDateFromString(rs, ++index);
            String externalOrderID = getSafeString(rs, ++index);
            String externalOrderNumber = getSafeString(rs, ++index);
            int externalOrderType = getExternalOrderType(getSafeString(rs, ++index));
            boolean requireServiceContractFlag = getBooleanFromString(rs, ++index);
            String storeCity = getSafeString(rs, ++index);
            String storeState = getSafeString(rs, ++index);
            String storeCountry = getSafeString(rs, ++index);
            String storePostalCode = getSafeString(rs, ++index);
            String returnTicket = getSafeString(rs, ++index);
            boolean transactionGiftReceiptAssigned = getBooleanFromString(rs, ++index);            
            String transactionCurrencyType = getSafeString(rs, ++index);            
            String transactionCountryCode = getSafeString(rs, ++index);
            rs.close();
            transaction.setCustomerId(customerId);
            if (irsCustomerId != null && irsCustomerId.length() > 0)
            {
                IRSCustomerIfc irsCustomer = readIRSCustomer(dataConnection, irsCustomerId);

                // Read Localized personald ID Code
                irsCustomer.setLocalizedPersonalIDCode(getInitializedLocalizedReasonCode(dataConnection, transaction
                        .getTransactionIdentifier().getStoreID(), irsCustomer.getLocalizedPersonalIDCode().getCode(),
                        CodeConstantsIfc.CODE_LIST_PAT_CUSTOMER_ID_TYPES, localeRequestor));

                transaction.setIRSCustomer(irsCustomer);
            }

            // If there is a default gift registry associated with the
            // transaction, instantiate the GiftRegistry
            if (!(Util.isEmpty(giftRegistryID)))
            {
                RegistryIDIfc registry = instantiateGiftRegistry();
                registry.setID(giftRegistryID);
                transaction.setDefaultRegistry(registry);
            }

            // Read Localized Reason Code
            transaction.setSuspendReason(getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                    .getStoreID(), suspendReasonCode,
                    CodeConstantsIfc.CODE_LIST_TRANSACTION_SUSPEND_REASON_CODES, localeRequestor));

            try
            {
                transaction.setSalesAssociate(getEmployeeHeader(dataConnection, salesAssociateID));                
            }
            catch (DataException checkEmployeeNotFound)
            {
                // Since empty/not found Sales Associate id could exist in
                // transaction and the
                // sales associate id here is retrieved from particular
                // transaction saved,
                // transaction is set with employee object using the sales
                // associate id
                // retrieved. For error codes other than for not found, data
                // exception is thrown
                if (checkEmployeeNotFound.getErrorCode() == DataException.NO_DATA)
                {
                    PersonNameIfc name = DomainGateway.getFactory().getPersonNameInstance();
                    EmployeeIfc employee = DomainGateway.getFactory().getEmployeeInstance();
                    employee.setEmployeeID(salesAssociateID);
                    name.setFirstName(salesAssociateID);
                    employee.setPersonName(name);
                    transaction.setSalesAssociate(employee);
                }
                else
                {
                    throw checkEmployeeNotFound;
                }
            }

            /*
             * Transaction Tax MUST BE FIRST! When we add the line items or send
             * items, the default tax information has to be setup.
             */
            TransactionTaxIfc transactionTax = selectTaxLineItem(dataConnection, transaction,
                    getLocaleRequestor(transaction));

            if (transactionTax.getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
            {
                selectTaxExemptionModifier(dataConnection, transaction, transactionTax);
            }
            transaction.setTransactionTax(transactionTax);

            // Set the shipping information.
            if (sendPackagesCount > 0)
            {
                transaction.setSendPackageCount(sendPackagesCount);
                readTransactionShippings(dataConnection, transaction, localeRequestor);
            }

            if (sendCustomerType.equals("0"))
            {
                transaction.setSendCustomerLinked(true);
            }
            else
            {
                transaction.setSendCustomerLinked(false);
            }
            transaction.setCustomerPhysicallyPresent(sendCustomerPhysicallyPresent);
            transaction.setTransactionLevelSendAssigned(transactionLevelSend);
            // Set the store address information
            transaction.getWorkstation().getStore().getAddress().setCity(storeCity);
            transaction.getWorkstation().getStore().getAddress().setState(storeState);
            transaction.getWorkstation().getStore().getAddress().setCountry(storeCountry);
            transaction.getWorkstation().getStore().getAddress().setPostalCode(storePostalCode);

            transaction.setReturnTicket(returnTicket);
            transaction.setTransactionGiftReceiptAssigned(transactionGiftReceiptAssigned);

            // Set the personal ID information
            if (!(Util.isEmpty(maskedPersonalIDNumber)))
            {
                CustomerInfoIfc customerInfo = transaction.getCustomerInfo();
                if (customerInfo == null)
                {
                    customerInfo = DomainGateway.getFactory().getCustomerInfoInstance();
                }

                // Read Localized Reason Code
                if (!Util.isEmpty(personalIDType))
                {
                    customerInfo.setLocalizedPersonalIDType(getInitializedLocalizedReasonCode(dataConnection, transaction
                            .getTransactionIdentifier().getStoreID(), personalIDType,
                            CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES, localeRequestor));
                }

                EncipheredDataIfc personalIDNumber = FoundationObjectFactory.getFactory()
                        .createEncipheredDataInstance(encryptedPersonalIDNumber, maskedPersonalIDNumber);
                customerInfo.setPersonalID(personalIDNumber);
                customerInfo.setPersonalIDState(personalIDState);
                customerInfo.setPersonalIDCountry(personalIDCountry);
                transaction.setCustomerInfo(customerInfo);
            }

            // Set the age restricted DOB
            transaction.setAgeRestrictedDOB(ageRestrictedDOB);

            // Set external order info
            transaction.setExternalOrderID(externalOrderID);
            transaction.setExternalOrderNumber(externalOrderNumber);
            transaction.setExternalOrderType(externalOrderType);
            transaction.setRequireServiceContractFlag(requireServiceContractFlag);            
            CurrencyTypeIfc currencyType = getCurrencyType(transactionCurrencyType);
            transaction.setCurrencyType(currencyType);
            transaction.setTransactionCountryCode(transactionCountryCode);

            // Read sale return line items
            SaleReturnLineItemIfc[] lineItems = selectSaleReturnLineItems(dataConnection, transaction, localeRequestor,
                    retrieveStoreCoupons);

            if (transaction instanceof OrderTransaction &&
                    transaction.getTransactionStatus() != TransactionConstantsIfc.STATUS_SUSPENDED)// logic added to
            // eliminate kitItem.
            {
                ArrayList<SaleReturnLineItemIfc> arrayOfLineItem = new ArrayList<SaleReturnLineItemIfc>();
                for (int i = 0; i < lineItems.length; i++)
                {
                    if (!(lineItems[i].getPLUItem().isKitHeader()))
                    {
                        arrayOfLineItem.add(lineItems[i]);
                    }

                }
                SaleReturnLineItemIfc[] pdoLineItems = new SaleReturnLineItemIfc[arrayOfLineItem.size()];
                arrayOfLineItem.toArray(pdoLineItems);
                lineItems = pdoLineItems;
            }
            SaleReturnLineItemIfc[] deletedLineItems = selectDeletedSaleReturnLineItems(dataConnection, transaction,
                    localeRequestor);

            // Set line items without updating transaction totals. The totals will be updated
            // when transaction discounts are added. Transaction totals must not be updated here
            // since it will erase the item level transaction discount information which is needed
            // to aggregate into transaction discount rules.
            transaction.getItemContainerProxy().setLineItems(lineItems);
            if (deletedLineItems != null)
            {
                if (deletedLineItems.length > 0)
                {
                    for (int i = 0; i < deletedLineItems.length; i++)
                    {
                        transaction.addDeletedLineItems(deletedLineItems[i]);
                    }
                }
            }
            
            // Read transaction discounts
            TransactionDiscountStrategyIfc[] transactionDiscounts;
            transactionDiscounts = selectDiscountLineItems(dataConnection, transaction, localeRequestor);
 
            // A return line item can also contain order item information, so look for order
            // item info for all SaleReturnTransactions.
            selectOrderLineItemsByRef(dataConnection, transaction);
            selectOrderLineItemDiscountStatusByRef(dataConnection, transaction);
            selectOrderLineItemTaxStatusByRef(dataConnection, transaction);
            
            // Aggregate Transaction Discounts must happen after selectSaleReturnLineItems since in some cases 
            // the transaction discounts are aggregated from item level discounts. It also must be called after
            // selectOrderLineItemsByRef since information such as if the line item is a pickup or cancel item 
            // is initialized after this call.
            transactionDiscounts = aggregateTransactionDiscounts(transaction, transactionDiscounts);
            transaction.addTransactionDiscounts(transactionDiscounts);

            // if the transaction is an order transaction
            if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
                    || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
                    || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
                    || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
            {
                OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
                orderTransaction.setOrderID(orderID);
                orderTransaction.getOrderStatus().setTrainingModeFlag(transaction.isTrainingMode());
                selectOrderStatusForTransaction(dataConnection, orderTransaction);
                selectDeliveryDetails(dataConnection, transaction);
                selectRecipientDetail(dataConnection, transaction);
            }

            // Read tender line items
            TenderLineItemIfc[] tenderLineItems = selectTenderLineItems(dataConnection, transaction);
            transaction.setTenderLineItems(tenderLineItems);

            // Read cash change rounding adjustment
            selectRoundingTenderChangeLineItem(dataConnection, transaction);
            
            // Read tenders for return items in the trans
            if (transaction.hasReturnItems())
            {
                ReturnTenderDataElementIfc[] returnTenders = readReturnTenders(dataConnection, transaction);
                transaction.appendReturnTenderElements(returnTenders);
            }
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "retail transaction table");
            throw new DataException(DataException.SQL_ERROR, "retail transaction table", se);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "retail transaction table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnTransaction()");
    }

    /**
     * returns the CurrencyType.
     * @param String transactionCurrencyType
     * @
     * @return CurrencyTypeIfc
     */
    private CurrencyTypeIfc getCurrencyType(String transactionCurrencyType)
    {
        CurrencyTypeIfc currencyType = null;
        try
        {
            currencyType = DomainGateway.findCurrencyType(transactionCurrencyType);
        }
        catch (IllegalArgumentException ex)
        {
            currencyType = new CurrencyType();
            ((CurrencyType)currencyType).setCurrencyCode(transactionCurrencyType);
        }
        return currencyType;
    }

    /**
     * Selects from the SaleReturnLineItemAddress table.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @param customer the customer
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectSaleReturnLineItemAddress(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, CustomerIfc customer) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItemAddress()");
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_SHIPPING_RECORDS);

        /*
         * Add Columns
         */
        sql.addColumn("DISTINCT " + FIELD_SHIPPING_RECORDS_FIRST_NAME);
        sql.addColumn(FIELD_SHIPPING_RECORDS_LAST_NAME);
        sql.addColumn(FIELD_SHIPPING_RECORDS_LINE1);
        sql.addColumn(FIELD_SHIPPING_RECORDS_LINE2);
        sql.addColumn(FIELD_SHIPPING_RECORDS_CITY);
        sql.addColumn(FIELD_SHIPPING_RECORDS_STATE);
        sql.addColumn(FIELD_SHIPPING_RECORDS_POSTAL_CODE);
        sql.addColumn(FIELD_SHIPPING_RECORDS_ZIP_EXT);
        sql.addColumn(FIELD_SHIPPING_RECORDS_COUNTRY);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                logger.warn("retail transaction not found!");
                throw new DataException(DataException.NO_DATA, "transaction not found");
            }

            int index = 0;

            String firstName = getSafeString(rs, ++index);
            String lastName = getSafeString(rs, ++index);
            String addressLine1 = getSafeString(rs, ++index);
            String addressLine2 = getSafeString(rs, ++index);
            String city = getSafeString(rs, ++index);
            String state = getSafeString(rs, ++index);
            String zip = getSafeString(rs, ++index);
            String zipExt = getSafeString(rs, ++index);
            String country = getSafeString(rs, ++index);

            rs.close();

            AddressIfc addr = DomainGateway.getFactory().getAddressInstance();
            Vector<String> lines = new Vector<String>(3);
            List<AddressIfc> addresses = new ArrayList<AddressIfc>();
            lines.addElement(addressLine1);
            lines.addElement(addressLine2);
            addr.setLines(lines);
            addr.setCity(city);
            addr.setState(state);
            addr.setPostalCode(zip);
            addr.setPostalCodeExtension(zipExt);
            addr.setCountry(country);
            addresses.add(addr);

            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setAddressList(addresses);
            transaction.setCustomer(customer);

        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "SaleReturnLineItemAddress table");
            throw new DataException(DataException.SQL_ERROR, "SaleReturnLineItemAddress table", se);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "SaleReturnLineItemAddress table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItemAddress()");
    }

    /**
     * Selects from the payment transaction table.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @param localeRequestor The request locales
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectPaymentTransaction(JdbcDataConnection dataConnection, PaymentTransactionIfc transaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPaymentTransaction()");
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_RETAIL_TRANSACTION);

        /*
         * Add Columns
         */
        sql.addColumn(FIELD_CUSTOMER_ID);
        sql.addColumn(FIELD_EMPLOYEE_ID);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                logger.warn("retail transaction not found!");
                throw new DataException(DataException.NO_DATA, "transaction not found");
            }

            int index = 0;
            String customerId = getSafeString(rs, ++index);
            // String salesAssociateID = getSafeString(rs, ++index);

            rs.close();

            transaction.setCustomerId(customerId);

            /*
             * Transaction Tax MUST BE FIRST! When we add the line items, the
             * default tax information has to be setup.
             */
            // Read Transaction Tax
            TransactionTaxIfc tax = DomainGateway.getFactory().getTransactionTaxInstance();
            tax.setDefaultRate(0.0);

            tax.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
            transaction.setTransactionTax(tax);

            // Read tender line items
            TenderLineItemIfc[] tenderLineItems = selectTenderLineItems(dataConnection, transaction);
            transaction.setTenderLineItems(tenderLineItems);

            // Read cash change rounding adjustment
            selectRoundingTenderChangeLineItem(dataConnection, transaction);
            
            // read payment data from payment table
            JdbcReadPayment readPaymentOp = new JdbcReadPayment();
            PaymentIfc inputPayment = transaction.getPayment();
            PaymentIfc payment = readPaymentOp.readPayment(dataConnection, inputPayment);
            transaction.setPayment(payment);

        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "retail transaction table");
            throw new DataException(DataException.SQL_ERROR, "retail transaction table", se);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "retail transaction table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPaymentTransaction()");
    }

    /**
     * Selects a payment from from the payment line item table based on order
     * transaction data.
     *
     * @param dataConnection a connection to the database
     * @param transaction the order transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectPaymentForOrderTransaction(JdbcDataConnection dataConnection, OrderTransactionIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPaymentForOrderTransaction()");
        try
        {
            // use transaction data for setting up input for jdbc call
            JdbcReadPayment readPaymentOp = new JdbcReadPayment();
            PaymentIfc inputPayment = DomainGateway.getFactory().getPaymentInstance();
            inputPayment.setTransactionID(transaction.getTransactionIdentifier());
            inputPayment.setBusinessDate(transaction.getBusinessDay());
            PaymentIfc payment = readPaymentOp.readPayment(dataConnection, inputPayment);
            transaction.setPayment(payment);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "retail transaction table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPaymentForOrderTransaction()");
    }

    /**
     * Selects a payment from from the payment line item table based on layaway
     * transaction data.
     *
     * @param dataConnection a connection to the database
     * @param transaction the layaway transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectPaymentForLayawayTransaction(JdbcDataConnection dataConnection,
            LayawayTransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPaymentForLayawayTransaction()");
        try
        {
            // use transaction data for setting up input for jdbc call
            JdbcReadPayment readPaymentOp = new JdbcReadPayment();
            PaymentIfc inputPayment = DomainGateway.getFactory().getPaymentInstance();
            inputPayment.setTransactionID(transaction.getTransactionIdentifier());
            inputPayment.setBusinessDate(transaction.getBusinessDay());
            PaymentIfc payment = readPaymentOp.readPayment(dataConnection, inputPayment);
            transaction.setPayment(payment);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "retail transaction table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPaymentForLayawayTransaction()");
    }

    /**
     * Selects an instant credit from from the instant credit table
     *
     * @param dataConnection a connection to the database
     * @param transaction the instantCredit transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectInstantCreditTransaction(JdbcDataConnection dataConnection,
            InstantCreditTransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectInstantCreditTransaction()");
        try
        {
            // use transaction data for setting up input for jdbc call
            JdbcReadInstantCredit readIC = new JdbcReadInstantCredit();

            InstantCreditIfc ic = readIC.readInstantCredit(dataConnection, transaction);
            transaction.setInstantCredit(ic);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "instant credit table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectInstantCreditTransaction()");
    }

    /**
     * Selects layaway from the layaway table.
     *
     * @param dataConnection a connection to the database
     * @param layawayTransaction layaway transaction
     * @param localeRequestor
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
    **/
    protected void selectLayawayForTransaction(JdbcDataConnection dataConnection,
            LayawayTransactionIfc layawayTransaction, LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectLayawayForTransaction()");
        JdbcReadLayawayForTransaction op = new JdbcReadLayawayForTransaction();
        try
        {

            LayawayIfc layaway = op.readLayawayForTransaction(dataConnection, layawayTransaction, localeRequestor);
            layawayTransaction.setLayaway(layaway);
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "layaway table", e);
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectLayawayForTransaction()");
    }

    /**
     * Select Layaway Payment History Info
     *
     * @param dataConnection data connection
     * @param layaway reference
     * @return layaway reference updated with payment history info
     * @throws DataException data exceptio
     */
    protected LayawayIfc selectLayawayPaymentHistoryInfo(JdbcDataConnection dataConnection, LayawayIfc layaway)
            throws DataException
    {
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering selectLayawayPaymentHistoryInfo");
        }
        if (layaway != null && !Util.isEmpty(layaway.getLayawayID()))
        {
            JdbcReadLayawayPaymentHistoryInfo dbReadLayawayPaymentHistoryInfoOperation = new JdbcReadLayawayPaymentHistoryInfo();
            try
            {
                layaway = dbReadLayawayPaymentHistoryInfoOperation.readLayawayPaymentHistoryInfo(dataConnection,
                        layaway);
            }
            catch (DataException de)
            {
                logger.error("" + de + "");
                throw de;
            }
            catch (Exception e)
            {
                logger.error("" + Util.throwableToString(e) + "");
                throw new DataException(DataException.UNKNOWN, "layaway payment history info table", e);
            }
        }
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting selectLayawayPaymentHistoryInfo");
        }
        return layaway;
    }


    /**
     * Selects layaway from the layaway table using the layaway ID in the
     * payment data.
     *
     * @param dataConnection a connection to the database
     * @param transaction layaway transaction
     * @param localeRequestor
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
     protected void selectLayawayForPayment(JdbcDataConnection dataConnection, LayawayTransactionIfc transaction, LocaleRequestor localeRequestor)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectLayawayForPayment()");

        JdbcReadLayaway op = new JdbcReadLayaway();

        try
        {
            LayawayIfc inputLayaway = DomainGateway.getFactory().getLayawayInstance();
            inputLayaway.setLayawayID(transaction.getPayment().getReferenceNumber());
            inputLayaway.setTrainingMode(transaction.isTrainingMode());
            LayawayIfc layaway = op.readLayaway(dataConnection, inputLayaway, localeRequestor);
            transaction.setLayaway(layaway);

        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "layaway table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectLayawayForPayment()");
    }

    /**
     * Selects layaway from the layaway table based on payment transaction.
     *
     * @param dataConnection a connection to the database
     * @param transaction layaway transaction
     * @param LocaleRequestor
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
    **/
    protected void selectLayawayForPaymentTransaction(JdbcDataConnection dataConnection, TransactionIfc transaction, LocaleRequestor localeRequestor)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectLayawayForPaymentTransaction()");

        LayawayPaymentTransactionIfc layawayPaymentTransaction = (LayawayPaymentTransactionIfc)transaction;
        LayawayIfc inputLayaway = DomainGateway.getFactory().getLayawayInstance();
        inputLayaway.setLayawayID(layawayPaymentTransaction.getAccountNum());
        inputLayaway.setTrainingMode(layawayPaymentTransaction.isTrainingMode());
        JdbcReadLayaway op = new JdbcReadLayaway();

        try
        {
            LayawayIfc layaway = op.readLayaway(dataConnection, inputLayaway, localeRequestor);
            layawayPaymentTransaction.setLayaway(layaway);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "layaway table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectLayawayForPaymentTransaction()");
    }

    /**
     * Select order status for given transaction.
     *
     * @param connection data connection
     * @param orderTransaction order transaction
     * @exception DataException thrown when error occurs
     */
    public void selectOrderStatusForTransaction(JdbcDataConnection connection, OrderTransactionIfc orderTransaction)
            throws DataException
    {
        OrderStatusIfc orderStatus = DomainGateway.getFactory().getOrderStatusInstance();
        orderTransaction.setOrderStatus(orderStatus);

        //build SQL statement
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(ARTSDatabaseIfc.TABLE_ORDER_STATUS, ARTSDatabaseIfc.ALIAS_ORDER_STATUS);

        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_XC_ORDER_FLAG);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_STATUS);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_STATUS_PREVIOUS);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_STATUS_CHANGE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_BEGIN);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_CREATE_TIMESTAMP);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_TOTAL);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_DEPOSIT_AMOUNT);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_INITIATION_CHANNEL);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_MINIMUM_DEPOSIT_AMOUNT);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_BALANCE_DUE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_WORKSTATION_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_LOCATION);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_TYPE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER_STATUS+"."+ARTSDatabaseIfc.FIELD_ORDER_SALE_AMOUNT);

        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_STATUS);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_STATUS_PREVIOUS);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_STATUS_CHANGE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_BEGIN);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_CREATE_TIMESTAMP);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_TOTAL);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_DEPOSIT_AMOUNT);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_INITIATION_CHANNEL);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_MINIMUM_DEPOSIT_AMOUNT);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_BALANCE_DUE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_STORE_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_WORKSTATION_ID);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_RECORDING_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_LOCATION);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_TYPE);
        sql.addColumn(ARTSDatabaseIfc.ALIAS_ORDER+"."+ARTSDatabaseIfc.FIELD_ORDER_SALE_AMOUNT);

        sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_ORDER+" "+ALIAS_ORDER
                +" ON " +ALIAS_ORDER+"."+FIELD_ORDER_ID + " = " + ALIAS_ORDER_STATUS+"."+FIELD_ORDER_ID);

        sql.addQualifier(ALIAS_ORDER_STATUS+"."+FIELD_RETAIL_STORE_ID + " = " + getStoreID(orderTransaction));
        sql.addQualifier(ALIAS_ORDER_STATUS+"."+FIELD_WORKSTATION_ID + " = " + getWorkstationID(orderTransaction));
        sql.addQualifier(ALIAS_ORDER_STATUS+"."+FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(orderTransaction));
        sql.addQualifier(ALIAS_ORDER_STATUS+"."+FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(orderTransaction));

        int recordsFound = 0;
        try
        {
            //execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet) connection.getResult();

            EYSStatusIfc status = null;
            TransactionIDIfc originalTransactionID = null;
            TransactionIDIfc recordingTransactionID = null;
            CurrencyIfc orderTotal = null, orderDepositAmount = null, orderBalanceDue = null,
                 orderMinimumDepositAmount = null, orderSaleAmount = null;

            //loop through result set
            while (rs.next())
            {                           // begin loop through result set
                recordsFound++;

                // instantatiate objects
                status = DomainGateway.getFactory().getEYSStatusInstance();
                originalTransactionID =
                    DomainGateway.getFactory().getTransactionIDInstance();
                orderStatus.setInitialTransactionID(originalTransactionID);
                recordingTransactionID =
                    DomainGateway.getFactory().getTransactionIDInstance();
                orderStatus.setRecordingTransactionID(recordingTransactionID);

                // read internal order status from the order table OR_ORD, and
                // read xchannel and suspended order status from the transactional level order
                // status table TR_OR_ORD.
                int index = 0;

                boolean isXChannelOrder = getBooleanFromString(rs, ++index);
                if (!isXChannelOrder && !orderTransaction.isSuspended())
                {
                    index = IDX_ORDER_SYSTEM_RECORD_FIELD_START;
                }

                orderStatus.setOrderID(getSafeString(rs, ++index));
                status.setStatus(rs.getInt(++index));
                status.setPreviousStatus(rs.getInt(++index));
                status.setLastStatusChange(getEYSDateFromString(rs, ++index));
                orderStatus.setTimestampBegin(getEYSDateFromString(rs, ++index));
                Timestamp createTime = rs.getTimestamp(++index);
                if (createTime != null)
                {
                    orderStatus.setTimestampCreated(new EYSDate(createTime));
                }
                orderTotal = getCurrencyFromDecimal(rs, ++index);
                orderDepositAmount = getCurrencyFromDecimal(rs, ++index);
                orderStatus.setInitiatingChannel(rs.getInt(++index));
                orderMinimumDepositAmount = getCurrencyFromDecimal(rs, ++index);
                orderBalanceDue = getCurrencyFromDecimal(rs, ++index);
                originalTransactionID.setStoreID(getSafeString(rs, ++index));
                originalTransactionID.setWorkstationID(getSafeString(rs, ++index));
                orderStatus.setInitialTransactionBusinessDate(getEYSDateFromString(rs, ++index));
                originalTransactionID.setSequenceNumber(rs.getLong(++index));
                recordingTransactionID.setStoreID(getSafeString(rs, ++index));
                recordingTransactionID.setWorkstationID(getSafeString(rs, ++index));
                orderStatus.setRecordingTransactionBusinessDate(getEYSDateFromString(rs, ++index));
                recordingTransactionID.setSequenceNumber(rs.getLong(++index));
                orderStatus.initializeStatus();
                orderStatus.setLocation(getSafeString(rs, ++index));
                orderStatus.setOrderType(rs.getInt(++index));
                orderSaleAmount = getCurrencyFromDecimal(rs, ++index);

                if (isXChannelOrder)
                {
                    orderStatus.setXChannelStatus(status);
                    orderStatus.setXChannelTotal(orderTotal);
                    orderStatus.setXChannelDepositAmount(orderDepositAmount);
                    orderStatus.setXChannelBalanceDue(orderBalanceDue);
                    orderStatus.setXChannelMinimumDepositAmount(orderMinimumDepositAmount);
                }
                else
                {
                    orderStatus.setStoreOrderStatus(status);
                    orderStatus.setStoreOrderTotal(orderTotal);
                    orderStatus.setStoreOrderDepositAmount(orderDepositAmount);
                    orderStatus.setStoreOrderBalanceDue(orderBalanceDue);
                    orderStatus.setStoreOrderMinimumDepositAmount(orderMinimumDepositAmount);
                    orderStatus.setSaleAmount(orderSaleAmount);  // sale amount only applies to store order.
                }
            } // end loop through result set
//          close result set
            rs.close();
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "order table");
            throw new DataException(DataException.SQL_ERROR, "order table", se);
        }

        if (recordsFound == 0)
        {
            logger.warn( "No order found");
            throw new DataException(DataException.NO_DATA, "No order found");
        }

    }

    /**
     * Selects from the till adjustment transaction tables.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectTillAdjustmentTransaction(JdbcDataConnection dataConnection,
            TillAdjustmentTransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTillAdjustmentTransaction()");

        // no further processing if transaction was cancelled
        if (transaction.getTransactionStatus() != TransactionIfc.STATUS_CANCELED)
        {
            SQLSelectStatement sql = new SQLSelectStatement();
            TenderDescriptor td = new TenderDescriptor();

            sql.addTable(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION);
            sql.addColumn(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_TENDER_TYPE_CODE);
            sql.addColumn(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION,
                    FIELD_FINANCIAL_ACCOUNTING_TRANSACTION_TENDER_MEDIA_COUNT_TYPE);

            sql.addQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_RETAIL_STORE_ID,
                    getStoreID(transaction));
            sql.addQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_WORKSTATION_ID,
                    getWorkstationID(transaction));
            sql.addQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER,
                    getTransactionSequenceNumber(transaction));
            sql.addQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE,
                    getBusinessDayString(transaction));

            switch (transaction.getTransactionType())
            {
                case TransactionIfc.TYPE_PAYIN_TILL:
                case TransactionIfc.TYPE_PAYOUT_TILL:
                case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                    sql.addTable(TABLE_FUNDS_RECEIPT_TRANSACTION);
                    sql.addColumn(FIELD_FUND_RECEIPT_MONETARY_AMOUNT);
                    sql.addColumn(FIELD_DISBURSEMENT_RECEIPT_REASON_CODE);
                    sql.addColumn(FIELD_TILL_PAYMENT_PAYEE_NAME);
                    sql.addColumn(FIELD_CURRENCY_ID); // I18N
                    sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_1);
                    sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_2);
                    sql.addColumn(FIELD_TILL_PAYMENT_ADDRESS_LINE_3);
                    sql.addColumn(FIELD_TILL_PAYMENT_COMMENTS);
                    sql.addColumn(FIELD_TILL_PAYMENT_APPROVAL_CODE);
                    sql.addColumn(FIELD_TILL_PAYMENT_EMPLOYEE_ID);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_RETAIL_STORE_ID,
                            TABLE_FUNDS_RECEIPT_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_WORKSTATION_ID,
                            TABLE_FUNDS_RECEIPT_TRANSACTION + "." + FIELD_WORKSTATION_ID);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE,
                            TABLE_FUNDS_RECEIPT_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "."
                            + FIELD_TRANSACTION_SEQUENCE_NUMBER, TABLE_FUNDS_RECEIPT_TRANSACTION + "."
                            + FIELD_TRANSACTION_SEQUENCE_NUMBER);
                    break;

                // NB: The number and order of columns in till loan and pickup
                // are important - see the deliberate drop-through in the switch
                case TransactionIfc.TYPE_LOAN_TILL:
                    sql.addTable(TABLE_TENDER_LOAN_TRANSACTION);

                    sql.addColumn(FIELD_TENDER_LOAN_AMOUNT);
                    sql.addColumn(FIELD_TENDER_LOAN_REASON_CODE);
                    sql.addColumn(FIELD_TENDER_LOAN_FROM_REGISTER_ID);
                    sql.addColumn(FIELD_CURRENCY_ID); // I18N

                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_RETAIL_STORE_ID,
                            TABLE_TENDER_LOAN_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_WORKSTATION_ID,
                            TABLE_TENDER_LOAN_TRANSACTION + "." + FIELD_WORKSTATION_ID);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE,
                            TABLE_TENDER_LOAN_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "."
                            + FIELD_TRANSACTION_SEQUENCE_NUMBER, TABLE_TENDER_LOAN_TRANSACTION + "."
                            + FIELD_TRANSACTION_SEQUENCE_NUMBER);
                    break;
                case TransactionIfc.TYPE_PICKUP_TILL:
                    sql.addTable(TABLE_TENDER_PICKUP_TRANSACTION);
                    sql.addColumn(FIELD_TENDER_PICKUP_TENDER_COUNTRY_CODE);
                    sql.addColumn(FIELD_TENDER_PICKUP_EXPECTED_AMOUNT);
                    sql.addColumn(FIELD_TENDER_PICKUP_EXPECTED_COUNT);

                    sql.addColumn(FIELD_TENDER_PICKUP_AMOUNT); // matches first
                                                               // column in loan
                    sql.addColumn(FIELD_TENDER_PICKUP_REASON_CODE);
                    sql.addColumn(FIELD_TENDER_PICKUP_TO_REGISTER_ID);
                    sql.addColumn(FIELD_CURRENCY_ID); // I18N

                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_RETAIL_STORE_ID,
                            TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_RETAIL_STORE_ID);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_WORKSTATION_ID,
                            TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_WORKSTATION_ID);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE,
                            TABLE_TENDER_PICKUP_TRANSACTION + "." + FIELD_BUSINESS_DAY_DATE);
                    sql.addJoinQualifier(TABLE_FINANCIAL_ACCOUNTING_TRANSACTION + "."
                            + FIELD_TRANSACTION_SEQUENCE_NUMBER, TABLE_TENDER_PICKUP_TRANSACTION + "."
                            + FIELD_TRANSACTION_SEQUENCE_NUMBER);
                    break;
                default:
                    break;
            }

            try
            {
                dataConnection.execute(sql.getSQLString());

                ResultSet rs = (ResultSet)dataConnection.getResult();

                if (!rs.next())
                {
                    logger.warn("till adjustment transaction not found!");
                    throw new DataException(DataException.NO_DATA, "transaction not found");
                }

                int index = 0;
                TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
                // set the tender type, count type from the financial accounting transaction table
                // TillLoan uses "Cash" for TillAdjustmentTransactionIfc#getTenderType
                int tenderCode = tenderTypeMap.getTypeFromCode((getSafeString(rs, ++index)).toUpperCase());
                td.setTenderType(tenderCode);
                // transaction.setTenderType(getSafeString(rs, ++index));
                transaction.setCountType(rs.getInt(++index));

                // 1 database record per till adjustment
                transaction.setAdjustmentCount(1);

                // get the data from other till adjustment tables depending on
                // transaction type
                String countryCode = DomainGateway.getBaseCurrencyType().getCountryCode();
                String reasonCode = null;
                String approvalCode = null;
                int currencyID;

                int transactionType = transaction.getTransactionType();
                switch (transaction.getTransactionType())
                {
                    case TransactionIfc.TYPE_PAYIN_TILL:
                    case TransactionIfc.TYPE_PAYOUT_TILL:
                    case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                        transaction.setAdjustmentAmount(getCurrencyFromDecimal(rs, ++index));
                        reasonCode = getSafeString(rs, ++index);
                        transaction.setPayeeName(getSafeString(rs, ++index));
                        currencyID = rs.getInt(++index);
                        transaction.setCurrencyID(currencyID); // I18N
                        td.setCurrencyID(currencyID);
                        transaction.setAddressLine(getSafeString(rs, ++index), 0);
                        transaction.setAddressLine(getSafeString(rs, ++index), 1);
                        transaction.setAddressLine(getSafeString(rs, ++index), 2);
                        transaction.setComments(getSafeString(rs, ++index));
                        approvalCode = getSafeString(rs, ++index);
                        transaction.setEmployeeID(getSafeString(rs, ++index));
                        break;
                    case TransactionIfc.TYPE_PICKUP_TILL:
                        countryCode = getSafeString(rs, ++index);
                        transaction.setExpectedAmount(getCurrencyFromDecimal(rs, ++index, countryCode));
                        transaction.setExpectedCount(rs.getInt(++index));
                        // td.setCurrencyID(currencyID);
                        td.setCountryCode(countryCode);


                        // deliberate drop through:

                    case TransactionIfc.TYPE_LOAN_TILL:
                        if (countryCode.length() > 0)
                        {
                            transaction.setAdjustmentAmount(getCurrencyFromDecimal(rs, ++index, countryCode));
                        }
                        else
                        {
                            transaction.setAdjustmentAmount(getCurrencyFromDecimal(rs, ++index));
                        }
                        reasonCode = getSafeString(rs, ++index);

                        // Set the to/from registers
                        String register = getSafeString(rs, ++index);
                        currencyID = rs.getInt(++index);
                        td.setCurrencyID(currencyID);
                        //loans can only be done for base currency, so use default value for the country code
                        td.setCountryCode(countryCode);
                        transaction.setTender(td);
                        transaction.setCurrencyID(currencyID); // I18N
                        if (!Util.isEmpty(register))
                        {
                            if (transactionType == TransactionIfc.TYPE_LOAN_TILL)
                            {
                                transaction.setFromRegister(register);
                                transaction.setToRegister(transaction.getWorkstation().getWorkstationID());
                            }
                            else
                            // transactionType ==
                            // TransactionIfc.TYPE_PICKUP_TILL
                            {
                                transaction.setToRegister(register);
                                transaction.setFromRegister(transaction.getWorkstation().getWorkstationID());
                            }
                        }

                        // handle detail counts, if necessary
                        if (transaction.getCountType() != FinancialCountIfc.COUNT_TYPE_NONE
                                || tenderTypeMap.getDescriptor(transaction.getTender().getTenderType()).equals(
                                        tenderTypeMap.getDescriptor(TenderLineItemIfc.TENDER_TYPE_CHECK)))
                        {
                            FinancialCountIfc fCount = selectTenderMediaLineItems(dataConnection, transaction);
                            transaction.setTenderCount(fCount);
                        }

                        break;
                    default:
                        break;
                }

                // lookup and set codelists for ReasonCode and ApprovalCode
                setTillAdjustmentReasonCodes(dataConnection, transaction, reasonCode, approvalCode);

                rs.close();
            }
            catch (DataException de)
            {
                logger.error("" + de + "");
                throw de;
            }
            catch (SQLException se)
            {
                dataConnection.logSQLException(se, "financial accounting transaction table");
                throw new DataException(DataException.SQL_ERROR, "financial accounting transaction table", se);
            }
            catch (Exception e)
            {
                logger.error("" + Util.throwableToString(e) + "");
                throw new DataException(DataException.UNKNOWN, "financial accounting transaction table", e);
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTillAdjustmentTransaction()");
    }

    /**
     * Sets the Reason and Approval Code Lists, if applicable, on the
     * transaction.
     *
     * @param dataConnection the connection
     * @param transaction the transaction
     * @param reasonCode the reason code
     * @param approvalCode the approval code
     */
    private void setTillAdjustmentReasonCodes(JdbcDataConnection dataConnection,
            TillAdjustmentTransactionIfc transaction, String reasonCode, String approvalCode)
    {
        // a null code is handled as undefined
        if (reasonCode == null)
        {
            reasonCode = CodeConstantsIfc.CODE_UNDEFINED;
        }

        if (approvalCode == null)
        {
            approvalCode = CodeConstantsIfc.CODE_UNDEFINED;
        }

        switch (transaction.getTransactionType())
        {
            case TransactionIfc.TYPE_PAYIN_TILL:
                transaction.setReason(getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                        .getStoreID(), reasonCode, CodeConstantsIfc.CODE_LIST_TILL_PAY_IN_REASON_CODES, transaction
                        .getLocaleRequestor()));
                break;
            case TransactionIfc.TYPE_PAYOUT_TILL:
                transaction.setReason(getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                        .getStoreID(), reasonCode, CodeConstantsIfc.CODE_LIST_TILL_PAY_OUT_REASON_CODES, transaction
                        .getLocaleRequestor()));
                transaction.setApproval(getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                        .getStoreID(), approvalCode, CodeConstantsIfc.CODE_LIST_TILL_PAY_OUT_APPROVAL_CODES,
                        transaction.getLocaleRequestor()));

                break;
            case TransactionIfc.TYPE_PAYROLL_PAYOUT_TILL:
                transaction.setReason(getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                        .getStoreID(), reasonCode, CodeConstantsIfc.CODE_LIST_TILL_PAYROLL_PAY_OUT_REASON_CODES,
                        transaction.getLocaleRequestor()));
                transaction.setApproval(getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                        .getStoreID(), approvalCode, CodeConstantsIfc.CODE_LIST_TILL_PAYROLL_PAY_OUT_APPROVAL_CODES,
                        transaction.getLocaleRequestor()));
                break;

            case TransactionIfc.TYPE_PICKUP_TILL:
            case TransactionIfc.TYPE_LOAN_TILL:
            default:
                // nothing to do
                break;

        }
    }

    /**
     * Selects tender media line items.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @return financial count object
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected FinancialCountIfc selectTenderMediaLineItems(JdbcDataConnection dataConnection, TransactionIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTenderMediaLineItems()");

        // build SQL statement
        SQLSelectStatement sql = buildSelectTenderMediaLineItemsSQL(transaction);

        FinancialCountIfc fCount = null;
        // execute SQL and parse
        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            fCount = parseSelectTenderMediaLineItemsResultSet(rs);
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "tender media line items table");
            throw new DataException(DataException.SQL_ERROR, "tender media line items table", se);
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "tender media line items table", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTenderMediaLineItems()");

        return (fCount);
    }

    /**
     * Returns an SQL select statement object for retrieving tender media line
     * items for the given transaction.
     *
     * @param transaction the base transaction
     * @return SQL Select statement
     */
    protected SQLSelectStatement buildSelectTenderMediaLineItemsSQL(TransactionIfc transaction)
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // set table
        sql.setTable(TABLE_TENDER_MEDIA_LINE_ITEM);
        // add columns
        addSelectTenderMediaLineItemsColumns(sql);
        // add qualifiers
        addSelectTenderMediaLineItemsQualifiers(sql, transaction);

        return (sql);
    }

    /**
     * Adds the columns for selecting tender media line items to the specified
     * SQL statement.
     *
     * @param sql SQLSelectStatement object
     */
    protected void addSelectTenderMediaLineItemsColumns(SQLSelectStatement sql)
    {
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_PICKUP_TENDER_COUNTRY_CODE); // CD_CNY_TND
        sql.addColumn(FIELD_TENDER_MEDIA_AMOUNT_IN);
        sql.addColumn(FIELD_TENDER_MEDIA_AMOUNT_OUT);
        sql.addColumn(FIELD_TENDER_MEDIA_UNITS_IN);
        sql.addColumn(FIELD_TENDER_MEDIA_UNITS_OUT);
        sql.addColumn(FIELD_TENDER_MEDIA_SUMMARY_DESCRIPTION);
        sql.addColumn(FIELD_TENDER_MEDIA_SUMMARY_FLAG);
        sql.addColumn(FIELD_TENDER_MEDIA_DENOMINATIONS_FLAG);

    }

    /**
     * Adds the qualifiers for selecting tender media line items to the
     * specified SQL statement.
     *
     * @param sql SQLSelectStatement object
     * @param transaction transaction object
     */
    protected void addSelectTenderMediaLineItemsQualifiers(SQLSelectStatement sql, TransactionIfc transaction)
    {
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(transaction.getTransactionIdentifier().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transaction.getTransactionIdentifier()
                .getSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(transaction.getBusinessDay()));
    }

    /**
     * Parses the result set created by the selection of tender media line items
     * for the given transaction and adds the items to the transaction object.
     *
     * @param rs ResultSet
     * @param transaction the base transaction
     * @exception SQLException thrown if error occurs parsing result set
     */
    protected void parseSelectTenderMediaLineItemsResultSet(ResultSet rs, TillAdjustmentTransactionIfc transaction)
            throws SQLException
    {

        FinancialCountIfc fCount = parseSelectTenderMediaLineItemsResultSet(rs);
        transaction.setTenderCount(fCount);

    }

    /**
     * Returns a financial count object parsed from the result set created by
     * the selection of tender media line items.
     *
     * @param rs ResultSet
     * @return Financial count object
     * @exception SQLException thrown if error occurs parsing result set
     */
    protected FinancialCountIfc parseSelectTenderMediaLineItemsResultSet(ResultSet rs) throws SQLException
    {
        int recordsRead = 0;
        // index to entries in result set row
        int index = 0;

        // temporary values
        String tenderCode = null;
        CurrencyIfc amountIn = null;
        CurrencyIfc amountOut = null;
        int unitsIn = 0;
        int unitsOut = 0;
        String summaryDescription = null;
        boolean summaryFlag = false;
        String countryCode = null;
        String tenderDescription = null;

        FinancialCountIfc fCount = null;
        FinancialCountIfc summaryCount = null;
        TenderDescriptorIfc td = null;
        int detailCount = 0;
        int currencyId = 0;
        TenderTypeMapIfc tenderTypeMap = DomainGateway.getFactory().getTenderTypeMapInstance();
        while (rs.next())
        {
            index = 0;
            // grab values from result set
            tenderCode = getSafeString(rs, ++index);
            countryCode = getSafeString(rs, ++index);
            if (countryCode != null)
            {
                amountIn = getCurrencyFromDecimal(rs, ++index, countryCode);
                amountOut = getCurrencyFromDecimal(rs, ++index, countryCode);
            }
            else
            {
                amountIn = getCurrencyFromDecimal(rs, ++index);
                amountOut = getCurrencyFromDecimal(rs, ++index);
            }
            currencyId = amountOut.getType().getCurrencyId();
            unitsIn = rs.getInt(++index);
            unitsOut = rs.getInt(++index);
            summaryDescription = getSafeString(rs, ++index);
            summaryFlag = getBooleanFromString(rs, ++index);
            // add tender line items
            if (recordsRead == 0)
            {
                fCount = DomainGateway.getFactory().getFinancialCountInstance();
            }
            // we don't need to add summary entries; they'll be added
            // automatically
            td = DomainGateway.getFactory().getTenderDescriptorInstance();
            td.setCountryCode(countryCode);
            td.setCurrencyID(currencyId);
            td.setTenderType(tenderTypeMap.getTypeFromCode(tenderCode));
            tenderDescription = tenderTypeMap.getDescriptor(td.getTenderType());
            if (summaryFlag == false && fCount != null)
            {
                fCount.addTenderItem(td, unitsIn, unitsOut,
                        amountIn, amountOut, tenderDescription, summaryDescription, false,false);
                detailCount++;
            }
            else
            {
                if (summaryCount == null)
                {
                    summaryCount = DomainGateway.getFactory().getFinancialCountInstance();
                }

                summaryCount.addTenderItem(td, unitsIn, unitsOut,
                        amountIn, amountOut, tenderDescription, summaryDescription, false,true);
            }

            recordsRead++;
        }

        // if no detail records, return summary count
        if (detailCount == 0)
        {
            fCount = summaryCount;
        }

        rs.close();

        if (recordsRead == 0)
        {
            logger.warn("No tender media line items found.");
        }

        return (fCount);
    }

    /**
     * Selects from the redeem transaction table.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectRedeemTransaction(JdbcDataConnection dataConnection, RedeemTransactionIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("Begin JdbcReadTransaction.selectRedeemTransaction()");

        String reasonCode = null;
        Integer tenderTypeCodeInt = null;
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_REDEEM_TRANSACTION);

        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_REDEEM_ID);
        sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER); // encrypted gift card number, null if not gift card
        sql.addColumn(FIELD_ISSUING_STORE_NUMBER);
        sql.addColumn(FIELD_REDEEM_AMOUNT);
        sql.addColumn(FIELD_REDEEM_FOREIGN_AMOUNT);
        sql.addColumn(FIELD_REDEEM_FACE_VALUE_AMOUNT);
        sql.addColumn(FIELD_STORE_CREDIT_STATUS);
        sql.addColumn(FIELD_CUSTOMER_FIRST_NAME);
        sql.addColumn(FIELD_CUSTOMER_LAST_NAME);
        sql.addColumn(FIELD_CUSTOMER_ID_TYPE);
        sql.addColumn(FIELD_CURRENCY_ID);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER);
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String tenderTypeCode = getSafeString(rs, ++index);
                String redeemId = getSafeString(rs, ++index);
                String encryptedGiftCard = getSafeString(rs, ++index);
                String issuingStoreId = getSafeString(rs, ++index);
                CurrencyIfc redeemAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc foreignAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc faceValueAmount = getCurrencyFromDecimal(rs, ++index);
                String storeCreditStatus = getSafeString(rs, ++index);
                String firstName = getSafeString(rs, ++index);
                String lastName = getSafeString(rs, ++index);
                reasonCode = getSafeString(rs, ++index);
                int currencyID = rs.getInt(++index);
                if (foreignAmount != null)
                {
                    CurrencyTypeIfc foreignType = getCurrencyType(currencyID);
                    if (foreignType.equals(redeemAmount.getType()))
                    {
                        // since the types are the same, there really was no foreign amount
                        foreignAmount = null;
                    }
                    else
                    {
                        foreignAmount.setType(foreignType);
                    }
                }
                String traceNumber = getSafeString(rs, ++index);
                DomainObjectFactoryIfc factory = DomainGateway.getFactory();
                tenderTypeCodeInt = new Integer(tenderTypeCode);
                switch (tenderTypeCodeInt.intValue())
                {
                    case TenderLineItemIfc.TENDER_TYPE_GIFT_CARD:
                        TenderGiftCardIfc giftCard = factory.getTenderGiftCardInstance();
                        GiftCardIfc gCard = factory.getGiftCardInstance();

                        EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory()
                                .createEncipheredCardDataInstance(encryptedGiftCard, redeemId, null);

                        gCard.setEncipheredCardData(cardData);
                        gCard.setRequestType(GiftCardIfc.GIFT_CARD_REDEEM);
                        giftCard.setEncipheredCardData(cardData);
                        giftCard.setAmountTender(redeemAmount);
                        giftCard.setGiftCard(gCard);
                        gCard.setTraceNumber(traceNumber);
                        transaction.addRedeemTender(giftCard);
                        break;
                    case TenderLineItemIfc.TENDER_TYPE_GIFT_CERTIFICATE:
                        TenderGiftCertificateIfc giftCert = factory.getTenderGiftCertificateInstance();
                        giftCert.setGiftCertificateNumber(redeemId);
                        giftCert.setAmountTender(redeemAmount);
                        giftCert.setAlternateCurrencyTendered(foreignAmount);
                        giftCert.setFaceValueAmount(faceValueAmount);
                        giftCert.setStoreNumber(issuingStoreId);
                        transaction.addRedeemTender(giftCert);
                        transaction.setRedeemID(redeemId);
                        break;
                    case TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT:
                        TenderStoreCreditIfc storeCredit = factory.getTenderStoreCreditInstance();
                        storeCredit.setStoreCreditID(redeemId);
                        storeCredit.setAmount(redeemAmount);
                        storeCredit.setAmountTender(redeemAmount);
                        storeCredit.setAlternateCurrencyTendered(foreignAmount);
                        storeCredit.setState(storeCreditStatus);
                        storeCredit.setFirstName(firstName);
                        storeCredit.setLastName(lastName);
                        transaction.addRedeemTender(storeCredit);
                        transaction.setRedeemID(redeemId);
                        break;
                }
                TenderLineItemIfc[] tenderLineItems = selectTenderLineItems(dataConnection, transaction);
                transaction.setTenderLineItems(tenderLineItems);

                // Read cash change rounding adjustment
                selectRoundingTenderChangeLineItem(dataConnection, transaction);
                
                transaction.setCurrencyID(currencyID);
            }
            rs.close();

        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "selectRedeemTransaction");
            throw new DataException(DataException.SQL_ERROR, "selectRedeemTransaction", se);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }
        catch (NumberFormatException ne)
        {
            throw new DataException(DataException.DATA_FORMAT, "selectRedeemTransaction", ne);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectRedeemTransaction", e);
        }

        // Read Localized Reason Code
        if (tenderTypeCodeInt != null && tenderTypeCodeInt.intValue() == TenderLineItemIfc.TENDER_TYPE_STORE_CREDIT)
        {
            TenderStoreCreditIfc storeCredit = (TenderStoreCreditIfc)transaction.getRedeemTender();
            storeCredit.setPersonalIDType(getInitializedLocalizedReasonCode(dataConnection, getStoreID(transaction), reasonCode,
                    CodeConstantsIfc.CODE_LIST_CAPTURE_CUSTOMER_ID_TYPES, getLocaleRequestor(transaction)));
        }

        if (logger.isDebugEnabled())
            logger.debug("End JdbcReadTransaction.selectRedeemTransaction()");
    }

    /**
     * Reads all the status change rows for this transaction.
     * @param dataConnection
     * @param transaction
     * @throws DataException
     */
    protected void selectStatusChangeTransaction(JdbcDataConnection dataConnection,
            StatusChangeTransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("Begin JdbcReadTransaction.selectStatusChangeTransaction()");

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_STATUS_CHANGE_TRANSACTION);

        sql.addColumn(FIELD_CHANGED_WORKSTATION_ID);
        sql.addColumn(FIELD_CHANGED_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_CHANGED_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_TRANSACTION_STATUS_CODE);
        sql.addColumn(FIELD_ORDER_ID);
        sql.addColumn(FIELD_LAYAWAY_ID);
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        String storeID = transaction.getFormattedStoreID();

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String workstationID = getSafeString(rs, ++index);
                int sequenceNumber = rs.getInt(++index);
                EYSDate businessDate = getEYSDateFromString(rs, ++index);
                int statusCode = rs.getInt(++index);
                String orderID = getSafeString(rs, ++index);
                String layawayID = getSafeString(rs, ++index);

                TransactionSummaryIfc summary = DomainGateway.getFactory().getTransactionSummaryInstance();
                TransactionID transID = new TransactionID();
                transID.setTransactionID(storeID, workstationID, sequenceNumber);
                transID.setBusinessDate(businessDate);
                summary.setTransactionID(transID);
                summary.setInternalOrderID(orderID);
                summary.setLayawayID(layawayID);
                summary.setTransactionStatus(statusCode);
                transaction.addTransactionSummary(summary);
            }
            rs.close();
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "selectStatusChangeTransaction");
            throw new DataException(DataException.SQL_ERROR, "selectStatusChangeTransaction", se);
        }
        catch (DataException de)
        {
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectStatusChangeTransaction", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("End JdbcReadTransaction.selectRedeemTransaction()");
    }

    /**
     * Selects from the post void transaction table.
     *
     * @param dataConnection a connection to the database
     * @param transaction the base transaction
     * @param localeRequestor The request locales
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectVoidTransaction(JdbcDataConnection dataConnection, VoidTransactionIfc transaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectVoidTransaction()");
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_POST_VOID_TRANSACTION);
        /*
         * Add Columns
         */
        sql.addColumn(FIELD_VOIDED);
        sql.addColumn(FIELD_VOIDED_WORKSTATION_ID);
        sql.addColumn(FIELD_VOIDED_REASON_CODE);
        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                logger.warn("void transaction not found!");
                throw new DataException(DataException.NO_DATA, "transaction not found");
            }

            int index = 0;
            int sequenceNumber = rs.getInt(++index);
            String workstationID = getSafeString(rs, ++index);
            String reasonCode = getSafeString(rs, ++index);
            if (workstationID.length() == 0)
            {
                workstationID = transaction.getWorkstation().getWorkstationID();
            }
            transaction.setOriginalRetailStoreID(transaction.getWorkstation().getStoreID());
            transaction.setOriginalWorkstationID(workstationID);
            transaction.setOriginalTransactionSequenceNumber(sequenceNumber);
            transaction.setOriginalBusinessDay(transaction.getBusinessDay());

            rs.close();

            if (Util.isEmpty(reasonCode))
            {
                reasonCode = CodeConstantsIfc.CODE_UNDEFINED;
            }
            transaction.setReason(getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                    .getStoreID(), reasonCode, CodeConstantsIfc.CODE_LIST_POST_VOID_REASON_CODES, localeRequestor));

            // Read tender line items
            TenderLineItemIfc[] tenderLineItems = selectTenderLineItems(dataConnection, transaction);
            transaction.setTenderLineItems(tenderLineItems);

            // Read cash change rounding adjustment
            selectRoundingTenderChangeLineItem(dataConnection, transaction);
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "void transaction table");
            throw new DataException(DataException.SQL_ERROR, "void transaction table", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "void transaction table", e);
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectVoidTransaction()");
    }

    /**
     * Gets the localized reason code for a transaction
     *
     * @param connection
     * @param storeId
     * @param reasonCode
     * @param codeListType
     * @param locale
     * @return LocalizedCodeIfc
     */
    protected LocalizedCodeIfc getLocalizedReasonCode(JdbcDataConnection connection, String storeId, String reasonCode,
            String codeListType, LocaleRequestor locale)
    {
        LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();

        if (!reasonCode.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {

            // Read Localized Reason Code
            CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
            criteria.setStoreID(storeId);
            criteria.setListID(codeListType);
            criteria.setLocaleRequestor(locale);
            criteria.setCode(reasonCode);
            localizedReasonCode = getLocalizedReasonCode(connection, criteria);

        }
        else
        {
            localizedReasonCode.setCode(reasonCode);
        }

        return localizedReasonCode;
    }

    /**
     * Gets the localized reason code for a transaction; if the none is found
     * The method creates a localized code object that contains only the reason code.
     * @param connection
     * @param storeId
     * @param reasonCode
     * @param codeListType
     * @param locale
     * @return LocalizedCodeIfc
     */
    protected LocalizedCodeIfc getInitializedLocalizedReasonCode(JdbcDataConnection connection, String storeId, String reasonCode,
            String codeListType, LocaleRequestor locale)
    {
        LocalizedCodeIfc localizedReasonCode = getLocalizedReasonCode(connection, storeId, reasonCode, codeListType, locale);

        if (localizedReasonCode == null)
        {
            localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
            localizedReasonCode.setCode(reasonCode);
        }

        return localizedReasonCode;
    }

    /**
     * Gets the localized reason code for a transaction
     *
     * @param connection
     * @param storeId
     * @param reasonCode
     * @param codeListType
     * @param locale
     * @return LocalizedCodeIfc
     */
    protected LocalizedCodeIfc getLocalizedReasonCode(JdbcDataConnection connection, String storeId, String reasonCode,
            String codeListType, LocaleRequestor locale, String discountId)
    {
        LocalizedCodeIfc localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();

        if (!reasonCode.equals(CodeConstantsIfc.CODE_UNDEFINED))
        {

            // Read Localized Reason Code
            CodeSearchCriteriaIfc criteria = DomainGateway.getFactory().getCodeSearchCriteriaInstance();
            criteria.setStoreID(storeId);
            criteria.setListID(codeListType);
            criteria.setLocaleRequestor(locale);
            criteria.setCode(reasonCode);
            criteria.setRuleID(discountId);
            localizedReasonCode = getLocalizedReasonCode(connection, criteria);

        }
        else
        {
            localizedReasonCode.setCode(reasonCode);
        }
        return localizedReasonCode;
    }

    /**
     * Gets the localized reason code for a transaction
     *
     * @param connection
     * @param storeId
     * @param reasonCode
     * @param codeListType
     * @param locale
     * @return LocalizedCodeIfc
     */
    protected LocalizedCodeIfc getInitializedLocalizedReasonCode(JdbcDataConnection connection, String storeId, String reasonCode,
            String codeListType, LocaleRequestor locale, String discountId)
    {
        LocalizedCodeIfc localizedReasonCode = getLocalizedReasonCode(connection, storeId, reasonCode, codeListType, locale, discountId);

        if (localizedReasonCode == null)
        {
            localizedReasonCode = DomainGateway.getFactory().getLocalizedCode();
            localizedReasonCode.setCode(reasonCode);
        }

        return localizedReasonCode;
    }

    /**
     * Reads the transaction tax line items.
     *
     * @param dataConnection a connection to the database
     * @param transaction the sale return transaction
     * @return TransactionTax
     * @throws DataException when there is an error reading from the DB or
     *             processing the result set
     */
    protected TransactionTaxIfc selectTaxLineItem(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, LocaleRequestor localeRequestor) throws DataException
    {

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTaxLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_TAX_LINE_ITEM);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_TAX_AMOUNT);
        sql.addColumn(FIELD_TAX_INC_AMOUNT);
        sql.addColumn(FIELD_TAX_TYPE_CODE);
        sql.addColumn(FIELD_TAX_PERCENT);
        sql.addColumn(FIELD_TAX_OVERRIDE_PERCENT);
        sql.addColumn(FIELD_TAX_OVERRIDE_AMOUNT);
        sql.addColumn(FIELD_TAX_REASON_CODE);
        // sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        /*
         * Add Ordering
         */
        sql.addOrdering(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);

        TransactionTaxIfc transactionTax = DomainGateway.getFactory().getTransactionTaxInstance();
        TaxInformationIfc taxInformation = DomainGateway.getFactory().getTaxInformationInstance();
        TaxInformationIfc inclusiveTaxInformation = DomainGateway.getFactory().getTaxInformationInstance();
        inclusiveTaxInformation.setInclusiveTaxFlag(true);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                taxInformation.setTaxAmount(getCurrencyFromDecimal(rs, ++index));
                inclusiveTaxInformation.setTaxAmount(getCurrencyFromDecimal(rs, ++index));
                transactionTax.setTaxMode(rs.getInt(++index));
                transactionTax.setDefaultRate(getBigDecimal(rs, ++index, 5).movePointLeft(2).doubleValue());
                transactionTax.setOverrideRate(getBigDecimal(rs, ++index, 5).movePointLeft(2).doubleValue());
                transactionTax.setOverrideAmount(getCurrencyFromDecimal(rs, ++index));
                String reasonCodeString = getSafeString(rs, ++index);
                LocalizedCodeIfc reason = DomainGateway.getFactory().getLocalizedCode();
                String codeListType = "";
                taxInformation.setTaxMode(transactionTax.getTaxMode());
                inclusiveTaxInformation.setTaxMode(transactionTax.getTaxMode());
                if (transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_AMOUNT)
                {
                    taxInformation.setTaxAmount(transactionTax.getOverrideAmount());
                    // inclusive tax cannot be overridden
                    codeListType = CodeConstantsIfc.CODE_LIST_TRANSACTION_TAX_AMOUNT_OVERRIDE_REASON_CODES;
                }

                if (transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_OVERRIDE_RATE)
                {
                    taxInformation.setTaxPercentage(new BigDecimal(transactionTax.getOverrideRate() * 100));
                    // inclusive tax cannot be overridden
                    codeListType = CodeConstantsIfc.CODE_LIST_TRANSACTION_TAX_RATE_OVERRIDE_REASON_CODES;
                }

                if(transactionTax.getTaxMode() == TaxConstantsIfc.TAX_MODE_EXEMPT)
                {
                    codeListType = CodeConstantsIfc.CODE_LIST_TAX_EXEMPT_REASON_CODES;
                }

                reason = getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier().getStoreID(),
                        reasonCodeString, codeListType, localeRequestor);
                transactionTax.setReason(reason);
            }
            rs.close();

            transaction.getTransactionTotals().getTaxInformationContainer().addTaxInformation(taxInformation);
            transaction.getTransactionTotals().getTaxInformationContainer().addTaxInformation(inclusiveTaxInformation);

        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectTaxLineItem", exc);
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTaxLineItem()");

        return transactionTax;
    }

    /**
     * Reads the transaction tax line items.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @param transactionTax the transaction level tax information
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectTaxExemptionModifier(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TransactionTaxIfc transactionTax) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTaxExemptionModifier()");
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_TAX_EXEMPTION_MODIFIER);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_ENCRYPTED_TAX_MODIFIER_TAX_EXEMPTION_CERTIFICATE_NUMBER);
        sql.addColumn(FIELD_MASKED_TAX_MODIFIER_TAX_EXEMPTION_CERTIFICATE_NUMBER);
        sql.addColumn(FIELD_TAX_MODIFIER_TAX_EXEMPTION_REASON_CODE);
        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                String certificateID = getSafeString(rs, ++index);
                String maskedCertificateID = getSafeString(rs, ++index);
                EncipheredDataIfc customerTaxCertificate = FoundationObjectFactory.getFactory()
                        .createEncipheredDataInstance(certificateID, maskedCertificateID);
                transactionTax.setTaxExemptCertificateID(certificateID);
                transactionTax.setTaxExemptCertificate(customerTaxCertificate);
            }

            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectTaxExemptionModifier", exc);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTaxExemptionModifier()");
    }

    /**
     * Reads the transaction discount line items.
     *
     * @param dataConnection the connection to the data source
     * @param transaction the retail transaction
     * @return Array of TransactionDiscountStrategies
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected TransactionDiscountStrategyIfc[] selectDiscountLineItems(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectDiscountLineItems()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_DISCOUNT_LINE_ITEM);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_DISCOUNT_REASON_CODE);
        sql.addColumn(FIELD_DISCOUNT_TYPE_CODE);
        sql.addColumn(FIELD_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_DISCOUNT_PERCENT);
        sql.addColumn(FIELD_DISCOUNT_ENABLED);
        sql.addColumn(FIELD_DISCOUNT_ASSIGNMENT_BASIS);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_INCLUDED_IN_BEST_DEAL_FLAG);
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_DISCOUNT_RULE_ID);
        sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID);
        sql.addColumn(FIELD_DISCOUNT_REFERENCE_ID_TYPE_CODE);
        sql.addColumn(FIELD_PROMOTION_ID);
        /*
         * Add Qualifier(s)
         */
        // For the specific transaction only
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        /*
         * Add Ordering
         */
        sql.addOrdering(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);

        Vector<TransactionDiscountStrategyIfc> discountLineItems = new Vector<TransactionDiscountStrategyIfc>(4);
        int lineItemSize = transaction.getLineItemsSize();
        boolean isSuspendedTxn = transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED;

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            while (rs.next())
            {
                int index = 0;
                String reasonCodeString = getSafeString(rs, ++index);
                int discountMethod = DiscountUtility.getDiscountMethod(getSafeString(rs, ++index));
                CurrencyIfc discountAmount = getCurrencyFromDecimal(rs, ++index);
                BigDecimal discountPercent = getPercentage(rs, ++index);
                index = index + 1;
                int assignmentBasis = DiscountUtility.getAssignmentBasis(getSafeString(rs, ++index));
                String discountEmployeeID = getSafeString(rs, ++index);
                boolean includedInBestDealFlag = getBooleanFromString(rs, ++index);
                index = index + 1;
                String ruleID = getSafeString(rs, ++index);
                String referenceID = rs.getString(++index);
                String referenceIDCodeStr = getSafeString(rs, ++index);
                int promotionID = rs.getInt(++index);

                TransactionDiscountStrategyIfc lineItem = null;
                if (isSuspendedTxn && !transaction.isWebManagedOrder())
                {
                    // For a suspended transaction that is being retrieved also not a web manager order 
                    // (CSC order), we will re-prorate its transactional discounts to its line items 
                    // since a suspended transaction is still subject to more changes. For a web managed
                    // order, its transaction discount cannot be reprorated and it has to preserve the
                    // amount calculated by an external source (CSC).
                    if (assignmentBasis == ASSIGNMENT_CUSTOMER)
                    {
                        lineItem = DomainGateway.getFactory().getCustomerDiscountByPercentageInstance();
                    }
                    else if (discountMethod == DISCOUNT_METHOD_PERCENTAGE)
                    {
                        lineItem = DomainGateway.getFactory().getTransactionDiscountByPercentageInstance();
                    }
                    else
                    {
                        lineItem = DomainGateway.getFactory().getTransactionDiscountByAmountInstance();
                    }
                    lineItem.setDiscountMethod(discountMethod);
                }
                else
                {
                    // A transaction discount audit does not recalucate its item discount by proration.
                    // Instead, it uses the item discount amount recorded in TR_MDFR_SLS_RTN_PRC table.
                    lineItem = DomainGateway.getFactory().getTransactionDiscountAuditInstance();
                    ((TransactionDiscountAuditIfc)lineItem).initialize(lineItemSize, null);
                    // For a transaction discount audit, the discount method is always DISCOUNT_METHOD_AMOUNT;
                    // Its original discount method records if it is applied by rate or amount initially.
                    ((TransactionDiscountAuditIfc)lineItem).setOriginalDiscountMethod(discountMethod);
                }

                lineItem.setDiscountRate(discountPercent);
                lineItem.setDiscountAmount(discountAmount);
                lineItem.setIncludedInBestDeal(includedInBestDealFlag);
                lineItem.setAssignmentBasis(assignmentBasis);
                lineItem.setDiscountEmployee(discountEmployeeID);
                setDiscountEmployeeIDOnTransaction(transaction, discountEmployeeID);

                String codeListType = DiscountUtility.getTransactionDiscountReasonCodeList(assignmentBasis, discountMethod);
                LocalizedCodeIfc reason = getInitializedLocalizedReasonCode(dataConnection,
                        transaction.getTransactionIdentifier().getStoreID(), reasonCodeString,
                        codeListType, localeRequestor);
                lineItem.setReason(reason);
                lineItem.setLocalizedNames(reason.getText());
                lineItem.setReferenceID(referenceID);
                lineItem.setPromotionId(promotionID);

                // set rule ID for all discount lineitems. CR30190 14FEB08 CMG
                // see {@link
                // ItemContainerProxyIfc#areAllStoreCouponsApplied()}.
                lineItem.setRuleID(ruleID);

                if (referenceIDCodeStr == null)
                {
                    lineItem.setReferenceIDCode(0);
                }
                else
                {
                    for (int i = 0; i < DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE.length; i++)
                    {
                        if (referenceIDCodeStr.equalsIgnoreCase(DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[i]))
                        {
                            lineItem.setReferenceIDCode(i);
                        }
                    }

                }

                discountLineItems.addElement(lineItem);
            }

            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectDiscountLineItems", exc);
        }

        // put vector into array
        TransactionDiscountStrategyIfc[] discounts = null;
        int numDiscounts = discountLineItems.size();
        if (numDiscounts > 0)
        {
            discounts = new TransactionDiscountStrategyIfc[numDiscounts];
            discountLineItems.copyInto(discounts);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectDiscountLineItems()");

        return (discounts);
    }
    
    /**
     * Aggregate transaction discount from line item discount
     * @param transaction the transaction
     * @param discountLineItems the list of transaction discounts
     * @return the aggregated transaction discounts
     */
    protected TransactionDiscountStrategyIfc[] aggregateTransactionDiscounts(SaleReturnTransactionIfc transaction, 
            TransactionDiscountStrategyIfc[] discounts)
    {
        TransactionDiscountAuditIfc[] discountAudits = null;
        if ((discounts != null) && discounts.length > 0)
        {
            // Only aggregate transactional discounts from line items if the transaction
            // is not suspended; otherwise, prorate the transactional discount down to
            // it line items. For an external web managed order (CSC order), always aggregate
            // up from line item to preserve the discount amount set by an external source (CSC).
            boolean isSuspendedTxn = transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED;
            if (!isSuspendedTxn || transaction.isWebManagedOrder())
            {
                discountAudits = Arrays.copyOf(discounts, discounts.length, TransactionDiscountAuditIfc[].class);
                // Aggregate item discount rules into transaction discount rules.
                ItemTransactionDiscountAggregatorIfc aggregator = DomainGateway.getFactory().
                        getItemTransactionDiscountAggregatorInstance();
                discounts = aggregator.aggregate((SaleReturnLineItemIfc[])transaction.getTotalableLineItems(),  /* only aggregate totable line items */
                        discountAudits);
            }
        }
        
        return discounts;
    }

    /**
     * Put the employeeDiscountID on the SaleReturnTransaction object only if it does not already exist there
     * @param transaction
     * @param discountEmployeeID
     */
    private void setDiscountEmployeeIDOnTransaction(TransactionIfc transaction, String discountEmployeeID)
    {
        if (!Util.isEmpty(discountEmployeeID) && transaction instanceof SaleReturnTransactionIfc && ((SaleReturnTransactionIfc)transaction).getEmployeeDiscountID() == null)
        {
            ((SaleReturnTransactionIfc)transaction).setEmployeeDiscountID(discountEmployeeID);
        }
    }

    /**
     * Get the tax information for an individual line item.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @param lineItemSequenceNumber
     * @return The Tax Information
     * @throws DataException
     */
    protected TaxInformationIfc[] selectSaleReturnLineItemTaxInformation(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItemTaxInformation()");

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_SALE_RETURN_TAX_LINE_ITEM, ALIAS_SALE_RETURN_TAX_LINE_ITEM);

        sql.setDistinctFlag(true);
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_SALE_RETURN_TAX_AMOUNT); // MO_TX_RTN_SLS
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_AUTHORITY_ID); // ID_ATHY_TX
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_GROUP_ID); // ID_GP_TX
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_TYPE); // TY_TX
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_HOLIDAY); // FL_TX_HDY
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_AUTHORITY_NAME); // NM_ATHY_TX
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_RULE_NAME); // NM_RU_TX
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_PERCENTAGE); // PE_TX
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_SALE_RETURN_TAX_AMOUNT); // MO_TX_RTN_SLS
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAXABLE_SALE_RETURN_AMOUNT); // MO_TXBL_RTN_SLS
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_UNIQUE_ID); // ID_UNQ
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_TAX_MODE); // TX_MOD
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM, FIELD_FLG_TAX_INCLUSIVE); // FL_TX_INC

        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + "=" + getStoreID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                + " = " + lineItemSequenceNumber);
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + getBusinessDayString(transaction));

        ArrayList<TaxInformationIfc> taxInfoList = new ArrayList<TaxInformationIfc>();

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                TaxInformationIfc taxInformation = DomainGateway.getFactory().getTaxInformationInstance();
                int index = 0;
                taxInformation.setTaxAmount(getCurrencyFromDecimal(rs, ++index));
                taxInformation.setTaxAuthorityID(rs.getInt(++index));
                taxInformation.setTaxGroupID(rs.getInt(++index));
                taxInformation.setTaxTypeCode(rs.getInt(++index));
                taxInformation.setTaxHoliday(getBooleanFromString(rs, ++index));
                taxInformation.setTaxAuthorityName(getSafeString(rs, ++index));
                taxInformation.setTaxRuleName(getSafeString(rs, ++index));

                BigDecimal perc = getBigDecimal(rs, ++index, TAX_PERCENTAGE_SCALE);
                taxInformation.setTaxPercentage(perc);

                taxInformation.setTaxAmount(getCurrencyFromDecimal(rs, ++index));
                taxInformation.setTaxableAmount(getCurrencyFromDecimal(rs, ++index));
                taxInformation.setUniqueID(getSafeString(rs, ++index));
                taxInformation.setTaxMode(rs.getInt(++index));
                taxInformation.setInclusiveTaxFlag(getBooleanFromString(rs, ++index));
                taxInfoList.add(taxInformation);
            }
            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectSaleReturnLineItemTaxByTaxAuthority", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectSaleReturnLineItemTaxByTaxAuthority", e);
        }

        TaxInformationIfc[] results = new TaxInformationIfc[taxInfoList.size()];
        for (int i = 0; i < results.length; i++)
        {
            results[i] = taxInfoList.get(i);
        }
        return results;
    }

    /**
     * Reads the sale return line item exclusive and inclusive tax amount from
     * SaleReturnLineItemTax table.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @param lineItemSequenceNumber the line item sequence number
     * @return exclusive and inclusive tax amount for this line item, across all
     *         jurisdictions
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected CurrencyIfc[] selectSaleReturnLineItemTaxAmount(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, int lineItemSequenceNumber) throws DataException
    {
        CurrencyIfc[] taxAmount = { DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO),
                DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO) };
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItemTaxAmount()");

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_SALE_RETURN_TAX_LINE_ITEM, ALIAS_SALE_RETURN_TAX_LINE_ITEM);

        sql.addColumn("DISTINCT " + ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_ITEM_TAX_AMOUNT_TOTAL);
        sql.addColumn(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_ITEM_TAX_INC_AMOUNT_TOTAL);

        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + "=" + getStoreID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                + " = " + lineItemSequenceNumber);
        sql.addQualifier(ALIAS_SALE_RETURN_TAX_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                taxAmount[0] = getCurrencyFromDecimal(rs, ++index);
                taxAmount[1] = getCurrencyFromDecimal(rs, ++index);
            }
        }
        catch (SQLException se)
        {
            throw new DataException(DataException.SQL_ERROR, "selectSaleReturnLineItemTaxAmount", se);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "selectSaleReturnLineItemTaxAmount", e);
        }
        return (taxAmount);
    }

    /**
     * Reads the sale return line items.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @param retrieveStoreCoupons designates whether or not to retrieve store
     *            coupon line items
     * @param localeRequestor the requested locales
     * @return Array of SaleReturn Line items
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected SaleReturnLineItemIfc[] selectSaleReturnLineItems(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, LocaleRequestor localeRequestor, boolean retrieveStoreCoupons)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItems()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_SALE_RETURN_LINE_ITEM, ALIAS_SALE_RETURN_LINE_ITEM);
        sql.addTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM, ALIAS_RETAIL_TRANSACTION_LINE_ITEM);
        /*
         * Add Column(s)
         */
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_GIFT_REGISTRY_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ITEM_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_QUANTITY);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIAL_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ORIGINAL_TRANSACTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_BUSINESS_DAY_DATE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_REASON_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE);  
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE);          
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_KIT_SET_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_COLLECTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_KIT_HEADER_REFERENCE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SHIPPING_CHARGE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_LABEL_COUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_GIFT_RECEIPT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID_ENTRY_METHOD_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SIZE_CODE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICEADJ_REFERENCE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETURN_RELATED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_REMOVE_RELATED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_RETRIEVED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIALIZED_ITEM_VALIDATION_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_DISCOUNT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_DAMAGE_DISCOUNT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + NON_RETRIEVED_ORIGINAL_RECEIPT_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_AGE_RESTRICTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_CLEARANCE_INDICATOR);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG);

        /*
         * Add Qualifier(s)
         */
        // For the specific transaction only
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + getBusinessDayString(transaction));
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + "="
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                + " = " + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);

        // order by line item sequence number
        sql.addOrdering(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                        + " ASC");

        Vector<SaleReturnLineItemIfc> saleReturnLineItems = new Vector<SaleReturnLineItemIfc>(2);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            TransactionTaxIfc transactionTax = transaction.getTransactionTax();

            HashMap<Integer, String> reasonCodeMap = new HashMap<Integer, String>(1);
            HashMap<Integer, String> itemConditionCodeMap = new HashMap<Integer, String>(1);
            while (rs.next())
            {
                int index = 0;
                String giftRegistryID = getSafeString(rs, ++index);
                String posItemID = getSafeString(rs, ++index);
                String itemID = getSafeString(rs, ++index);
                BigDecimal quantity = getBigDecimal(rs, ++index);
                CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
                /* CurrencyIfc itemTaxAmount = getLongerCurrencyFromDecimal(rs, */++index;
                /* CurrencyIfc itemIncTaxAmount = getLongerCurrencyFromDecimal(rs, */++index;
                int sequenceNumber = rs.getInt(++index);
                String serialNumber = getSafeString(rs, ++index);
                BigDecimal quantityReturned = getBigDecimal(rs, ++index);
                String originalTransactionID = getSafeString(rs, ++index);
                EYSDate originalTransactionBusinessDay = getEYSDateFromString(rs, ++index);
                int originalTransactionLineNumber = rs.getInt(++index);
                String originalStoreID = getSafeString(rs, ++index);
                boolean returnFlag = getBooleanFromString(rs, ++index);
                String returnReasonCode = getSafeString(rs, ++index);
                String returnItemConditionCode = getSafeString(rs, ++index);
                if (Util.isBlank(returnItemConditionCode))
                {
                    returnItemConditionCode = CodeConstantsIfc.CODE_UNDEFINED;
                }
                CurrencyIfc restockingFee = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc balanceDue = getCurrencyFromDecimal(rs, ++index);
                boolean pickupCancelledPrice = getBooleanFromString(rs, ++index);
                boolean pickupInStorePrice = getBooleanFromString(rs, ++index);
                int kitCode = rs.getInt(++index);
                String itemKitID = getSafeString(rs, ++index);
                int kitReference = rs.getInt(++index);
                String sendFlag = getSafeString(rs, ++index);
                String shippingChargeFlag = getSafeString(rs, ++index);
                int sendLabelCount = rs.getInt(++index);
                String giftReceiptStr = getSafeString(rs, ++index);
                String orderID = rs.getString(++index);
                int orderLineReference = rs.getInt(++index);
                String entryMethodCode = getSafeString(rs, ++index);
                String sizeCode = getSafeString(rs, ++index);
                String itemVoidFlag = getSafeString(rs, ++index);
                boolean isPriceAdjLineItem = rs.getBoolean(++index);
                int priceAdjReferenceID = rs.getInt(++index);
                boolean returnRelatedItemFlag = rs.getBoolean(++index);
                int relatedSeqNumber = rs.getInt(++index);
                boolean deleteRelatedItemFlag = rs.getBoolean(++index);
                boolean retrievedFlag = rs.getBoolean(++index);
                boolean saleAsscModifiedFlag = getBooleanFromString(rs, ++index);
                CurrencyIfc beforeOverride = getCurrencyFromDecimal(rs, ++index);
                String receiptDescription = getSafeString(rs, ++index);
                Locale receiptDescriptionLocale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));
                boolean restockingFeeFlag = rs.getBoolean(++index);
                boolean serializedItemFlag = rs.getBoolean(++index);
                boolean externalValidationSerializedItemFlag = rs.getBoolean(++index);
                boolean isPOSAllowedToCreateUIN =  rs.getBoolean(++index);
                String productGroupID = getSafeString(rs, ++index);
                boolean sizeRequiredFlag = rs.getBoolean(++index);
                String unitOfMeasureCode =  getSafeString(rs, ++index);
                String posDepartmentID =  getSafeString(rs, ++index);
                int itemTypeID = rs.getInt(++index);
                boolean returnEligible = !(rs.getBoolean(++index));
                boolean employeeDiscountEligible = (rs.getBoolean(++index));
                int taxGroupId = rs.getInt(++index);
                boolean taxable = (rs.getBoolean(++index));
                boolean discountable = (rs.getBoolean(++index));
                boolean damageDiscountable = (rs.getBoolean(++index));
                String merchandiseHierarchyGroupID = getSafeString(rs, ++index);
                String manufacturerItemUPC = getSafeString(rs, ++index);
                String nonRetrievedOriginalReceiptId = getSafeString(rs, ++index);
                int restrictiveAge = rs.getInt(++index);
                boolean clearanceIndicator = (rs.getBoolean(++index));
                boolean priceEntryRequired = (rs.getBoolean(++index));

                CurrencyIfc lineItemTaxAmount = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);
                CurrencyIfc lineItemIncTaxAmount = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);

                // create and initialize item price object
                ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();
                ItemTaxIfc itemTax = DomainGateway.getFactory().getItemTaxInstance();
                price.setExtendedSellingPrice(amount);
                price.setDiscountEligible(discountable);
                price.setExtendedRestockingFee(restockingFee);

                if (quantity.signum() != 0)
                {
                    amount = amount.divide(new BigDecimal(quantity.toString()));
                    if (restockingFee != null)
                    {
                        restockingFee = restockingFee.divide(new BigDecimal(quantity.toString()));
                    }
                }

                price.setSellingPrice(amount);
                price.setPermanentSellingPrice(beforeOverride);
                price.setRestockingFee(restockingFee);

                // Obtain the previously calculated and saved line item tax
                itemTax = price.getItemTax();
                itemTax.setDefaultRate(transactionTax.getDefaultRate());
                itemTax.setDefaultTaxRules(transactionTax.getDefaultTaxRules());
                itemTax.setItemTaxAmount(lineItemTaxAmount);
                itemTax.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
                // The tax mode is unknown at this point; It can be set from
                // SaleReturnTaxLineItem Table or the TaxModifier Table; however,
                // if neither is available it must be explicitly set
                // depending on item taxability.
                itemTax.setTaxMode(TAX_MODE_NOT_SET);
                price.setItemTax(itemTax);

                // price.setItemTaxAmount(itemTaxAmount);
                price.setItemTaxAmount(lineItemTaxAmount);
                price.setItemInclusiveTaxAmount(lineItemIncTaxAmount);

                // price.setTaxGroupId(taxGroupID);
                price.setItemQuantity(quantity);
 

                SaleReturnLineItemIfc lineItem;
                // create and initialize appropriate line item object
                if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
                {

                    // code added for item realted to PDO
                    if (transaction.getTransactionType() == TransactionIfc.TYPE_RETURN ||
                            transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED)
                    {
                        switch (kitCode)
                        {
                            case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
                                lineItem = DomainGateway.getFactory().getKitHeaderLineItemInstance();
                                break;
                            case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
                                lineItem = DomainGateway.getFactory().getKitComponentLineItemInstance();
                                ((KitComponentLineItemIfc)lineItem).setItemKitID(itemKitID);
                                break;
                            default:
                                lineItem = DomainGateway.getFactory().getOrderLineItemInstance();
                                break;
                        }
                    }
                    else
                    {
                        lineItem = DomainGateway.getFactory().getOrderLineItemInstance();
                    }

                }
                else
                {
                    switch (kitCode)
                    {
                        case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
                            lineItem = DomainGateway.getFactory().getKitHeaderLineItemInstance();
                            break;
                        case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
                            lineItem = DomainGateway.getFactory().getKitComponentLineItemInstance();
                            ((KitComponentLineItemIfc)lineItem).setItemKitID(itemKitID);
                            break;
                        default:
                            lineItem = DomainGateway.getFactory().getSaleReturnLineItemInstance();
                            break;
                    }
                }

                lineItem.setPLUItemID(itemID);
                lineItem.setItemPrice(price);
                lineItem.setItemTaxAmount(lineItemTaxAmount);
                lineItem.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
                lineItem.modifyItemQuantity(quantity);
                lineItem.setLineNumber(sequenceNumber);

                // set the order id & line item reference number
                lineItem.setOrderID(orderID);
                lineItem.setOrderLineReference(orderLineReference);

                lineItem.setReceiptDescription(receiptDescription);
                lineItem.setReceiptDescriptionLocale(receiptDescriptionLocale);
                lineItem.getItemPrice().setEmployeeDiscountEligible(employeeDiscountEligible);
                
                lineItem.setDepositAmount(depositAmount);
                
                if (lineItem instanceof OrderLineItemIfc)
                {
                    ((OrderLineItemIfc)lineItem).setItemBalanceDue(balanceDue);
                    ((OrderLineItemIfc)lineItem).setPriceCancelledDuringPickup(pickupCancelledPrice);
                    ((OrderLineItemIfc)lineItem).setInStorePriceDuringPickup(pickupInStorePrice);
                }

                // set the KitHeaderReference
                lineItem.setKitHeaderReference(kitReference);

                if (serialNumber != null && serialNumber.length() > 0)
                {
                    lineItem.setItemSerial(serialNumber);
                }
                lineItem.setQuantityReturned(quantityReturned);
                /*
                 * Should probably be a relationship fetched from the gift
                 * registry table
                 */
                if (giftRegistryID.length() > 0)
                {
                    RegistryIDIfc registry = instantiateGiftRegistry();
                    registry.setID(giftRegistryID);
                    lineItem.modifyItemRegistry(registry, true);
                }

                // Return Item Original Transaction information is available
                if (returnFlag)
                {
                    ReturnItemIfc ri = DomainGateway.getFactory().getReturnItemInstance();

                    if (originalTransactionID != null && originalTransactionID.length() > 0)
                    {
                        // Create the transaction id.
                        TransactionIDIfc id = DomainGateway.getFactory().getTransactionIDInstance();
                        id.setTransactionID(originalTransactionID);
                        ri.setOriginalTransactionID(id);
                        ri.setHaveReceipt(true);
                    }
                    ri.setNonRetrievedOriginalReceiptId(nonRetrievedOriginalReceiptId);

                    if (originalTransactionBusinessDay != null)
                    {
                        ri.setOriginalTransactionBusinessDate(originalTransactionBusinessDay);
                    }
                    ri.setOriginalLineNumber(originalTransactionLineNumber);

                    // DB2 does not support nested result sets, so the localized
                    // reason codes needs to be retrieved after closing the current
                    // result set. Add the reason code to a map for later retrieval.
                    reasonCodeMap.put(sequenceNumber, returnReasonCode);
                    itemConditionCodeMap.put(sequenceNumber, returnItemConditionCode);

                    if (originalStoreID.equals(transaction.getWorkstation().getStoreID()))
                    {
                        ri.setStore(transaction.getWorkstation().getStore());
                    }
                    else
                    {
                        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
                        store.setStoreID(originalStoreID);
                        ri.setStore(store);
                    }
                    if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED ||
                            transaction.getTransactionStatus() == TransactionIfc.STATUS_COMPLETED)
                    {
                        // we preserve this value only if we are fetching a
                        // transaction out
                        // of the suspended state.
                        ri.setFromRetrievedTransaction(retrievedFlag);
                    }
                    lineItem.setReturnItem(ri);
                }

                /* Sales Associate defaults to the cashier. If it's different,
                 * that will be accounted for by the selectCommissionModifier
                 * below.
                 */
                if (transaction.getSalesAssociate() != null)
                {
                    lineItem.setSalesAssociate(transaction.getSalesAssociate());
                }
                else
                {
                    lineItem.setSalesAssociate(transaction.getCashier());
                }

                // Send Flag
                if (transaction.getTransactionType() != TransactionIfc.TYPE_RETURN)
                {
                    if (sendFlag.equals("0"))
                    {
                        lineItem.setItemSendFlag(false);
                    }
                    else if (sendFlag.equals("1"))
                    {
                        lineItem.setItemSendFlag(true);
                    }
                    lineItem.setSendLabelCount(sendLabelCount);
                    /*
                     * Shipping Charge Flag
                     */
                    if (shippingChargeFlag.equals("0"))
                    {
                        lineItem.setShippingCharge(false);
                    }
                    else if (shippingChargeFlag.equals("1"))
                    {
                        lineItem.setShippingCharge(true);
                    }
                }

                // Gift Receipt Flag
                if (giftReceiptStr.equals("1"))
                {
                    lineItem.setGiftReceiptItem(true);
                }

                // Price Adjustment Flags
                lineItem.setPriceAdjustmentReference(priceAdjReferenceID);
                lineItem.setIsPriceAdjustmentLineItem(isPriceAdjLineItem);

                // set entry method (parse string from database to entry code
                EntryMethod entryMethod = EntryMethod.Manual;
                if (!Util.isEmpty(entryMethodCode))
                {
                    for (EntryMethod code : EntryMethod.values())
                    {
                        if (entryMethod.equals(code.getIxRetailCode()) || entryMethod.equals(String.valueOf(code.getLegacyCode())))
                        {
                            entryMethod = code;
                            break;
                        }
                    }
                }

                lineItem.setEntryMethod(entryMethod);
                lineItem.setItemSizeCode(sizeCode);

                if (!itemVoidFlag.equals("1"))
                {
                    saleReturnLineItems.addElement(lineItem);
                }

                lineItem.setRelatedItemReturnable(returnRelatedItemFlag);
                lineItem.setRelatedItemSequenceNumber(relatedSeqNumber);
                lineItem.setRelatedItemDeleteable(deleteRelatedItemFlag);
                lineItem.setSalesAssociateModifiedFlag(saleAsscModifiedFlag);
                // Generate a pluItem from the data in the Sale Return Line Item table
                PLUItemIfc pluItem = instantiatePLUItem(productGroupID, kitCode, transaction.isTrainingMode());
               
                pluItem.setItemID(itemID);
                pluItem.setPosItemID(posItemID);
                pluItem.setItemSizeRequired(sizeRequiredFlag);
                pluItem.setDepartmentID(posDepartmentID);
                pluItem.setTaxable(taxable);
                pluItem.setTaxGroupID(taxGroupId);
                pluItem.setManufacturerItemUPC(manufacturerItemUPC);
                getItemLevelMessages(dataConnection, pluItem);
                // Set kitid, if the line item is a KitComponentLineItem
                if (lineItem.isKitComponent())
                {
                    ((KitComponentIfc) pluItem).setItemKitID(((KitComponentLineItemIfc) lineItem).getItemKitID());
                }
                ItemClassificationIfc itemClassification = DomainGateway.getFactory().getItemClassificationInstance();
                itemClassification.setRestockingFeeFlag(restockingFeeFlag);
                itemClassification.setSerializedItem(serializedItemFlag);
                if ( externalValidationSerializedItemFlag )
                {
                    itemClassification.setSerialEntryTime(STORE_RECEIVING_SERIALIZED_CAPTURE_TIME);
                }
                else
                {
                    itemClassification.setSerialEntryTime(SALE_SERIALIZED_CAPTURE_TIME);
                }

                
                // If external validation is true and the serial number is not available in the external system (e.g SIM) 
                // then the following flag will allow POS to go through the sale and request SIM to add the missing serial number.
                itemClassification.setExternalSystemCreateUIN(isPOSAllowedToCreateUIN);
               
                ProductGroupIfc pg = DomainGateway.getFactory().getProductGroupInstance();
                pg.setGroupID(productGroupID);
                itemClassification.setGroup(pg);
                itemClassification.setItemType(itemTypeID);
                itemClassification.setReturnEligible(returnEligible);
                itemClassification.setEmployeeDiscountAllowedFlag(employeeDiscountEligible);
                itemClassification.setDiscountEligible(discountable);
                itemClassification.setDamageDiscountEligible(damageDiscountable);
                itemClassification.setPriceEntryRequired(priceEntryRequired);
                
                itemClassification.setMerchandiseHierarchyGroup(merchandiseHierarchyGroupID);
                pluItem.setItemClassification(itemClassification);
                lineItem.setOnClearance(clearanceIndicator);
                UnitOfMeasureIfc pluUOM = DomainGateway.getFactory().getUnitOfMeasureInstance();
                pluUOM.setUnitID(unitOfMeasureCode);
                pluItem.setUnitOfMeasure(pluUOM);
                pluItem.setRestrictiveAge(restrictiveAge);
                selectOptionalI18NPLUData(dataConnection, pluItem, localeRequestor, lineItem);
                lineItem.setPLUItem(pluItem);
            }

            // Set the localized reason code for return line items
            for (int lineItemCounter = 0; lineItemCounter < saleReturnLineItems.size(); lineItemCounter++)
            {
                if (saleReturnLineItems.get(lineItemCounter).isReturnLineItem())
                {
                    int sequenceNumber = saleReturnLineItems.get(lineItemCounter).getLineNumber();

                    // Get the reason code from the map
                    String reasonCode = reasonCodeMap.get(sequenceNumber);

                    // Retrieve localized reason code
                    saleReturnLineItems.get(lineItemCounter).getReturnItem().setReason(
                            getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier().getStoreID(),
                                    reasonCode, CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES, localeRequestor));

                    //Get the item condition code from the map
                    String itemConditionCode = itemConditionCodeMap.get(sequenceNumber);

                    //retreve localized item condition code
                    saleReturnLineItems.get(lineItemCounter).getReturnItem().setItemCondition(
                            getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier().getStoreID(),
                                    itemConditionCode, CodeConstantsIfc.CODE_LIST_RETURN_ITEM_CONDITION_CODES, localeRequestor));
                }
            }

            //To relate related items with the lineitems
            for (int k = 0; k < saleReturnLineItems.size(); k++) {
                if (saleReturnLineItems.get(k).getRelatedItemSequenceNumber() != -1) {
                    for (int l = 0; l < saleReturnLineItems.size(); l++) {
                        if (saleReturnLineItems.get(l)
                                .getRelatedItemSequenceNumber() == -1
                                && (saleReturnLineItems.get(k)
                                        .getRelatedItemSequenceNumber() == saleReturnLineItems
                                        .get(l).getLineNumber())) {
                            {
                                //line item k is line item l's related item
                                saleReturnLineItems
                                        .get(l).addRelatedItemLineItem((SaleReturnLineItemIfc)saleReturnLineItems.get(k));
                            }
                        }
                    }
                }
            }

            // Do separate query to find the possible line item tax amount from
            // tr_ltm_sls_rtn_tx table.
            for (int i = 0; i < saleReturnLineItems.size(); i++)
            {
                SaleReturnLineItemIfc srli = saleReturnLineItems.elementAt(i);
                int lineItemSequenceNumber = srli.getLineNumber();
                TaxInformationIfc[] taxInfoArray = selectSaleReturnLineItemTaxInformation(dataConnection, transaction,
                        lineItemSequenceNumber);
                TaxInformationContainerIfc container = DomainGateway.getFactory().getTaxInformationContainerInstance();
                for (int j = 0; j < taxInfoArray.length; j++)
                {
                    container.addTaxInformation(taxInfoArray[j]);
                    srli.getItemPrice().getItemTax().setTaxMode(taxInfoArray[j].getTaxMode());
                    if (srli.getReturnItem() != null)
                    {
                        srli.getReturnItem().setTaxRate(
                                taxInfoArray[j].getTaxPercentage().movePointLeft(2).doubleValue());
                    }
                }
                srli.getItemPrice().getItemTax().setTaxInformationContainer(container);
                CurrencyIfc[] taxAmount = selectSaleReturnLineItemTaxAmount(dataConnection, transaction,
                        lineItemSequenceNumber);
                srli.setItemTaxAmount(taxAmount[0]); // the first element is add on item tax
                srli.setItemInclusiveTaxAmount(taxAmount[1]); // the second element is inclusive item tax
                if (transaction.getTransactionStatus() == TransactionIfc.STATUS_SUSPENDED)
                {
                    if ((srli.getReturnItem() != null) && srli.getReturnItem().isFromRetrievedTransaction())
                    {
                        // set the flag indicating that the line item is read
                        // from database for any retrieved return
                        // line item in a suspended transaction. This imples
                        // that no more price modification can be
                        // applied to the line item.
                        srli.setFromTransaction(true);
                    }
                    else
                    {
                        // set the flag indicating that the line item may
                        // subject to more price modification for
                        // any line item in a suspended transactin except a
                        // retrieved return item.
                        srli.setFromTransaction(false);
                    }
                }
                else
                {
                    // set the flag indicating that the line item is read from
                    // database for any non-suspended
                    // transaction line item
                    srli.setFromTransaction(true);
                }
            }

            // Grab auxiliary elements
            List<ItemDiscountStrategyIfc> itemDiscounts = null;
            for (SaleReturnLineItemIfc lineItem: saleReturnLineItems)
            {
                PLUItemIfc pluItem = lineItem.getPLUItem();

                // For tax exempt transactions add the tax rules to the plu
                // item. The tax rules are used by the tax engine while updating
                // the transaction totals.
                if (TaxConstantsIfc.TAX_MODE_EXEMPT == transaction.getTransactionTax().getTaxMode())
                {
                    pluItem = selectPLUItemTaxRules(dataConnection, transaction, pluItem);
                }

                if (lineItem.isKitComponent() && pluItem instanceof KitComponentIfc)
                {
                    ((KitComponentIfc)pluItem).setKitComponent(true);
                    pluItem.setItemID(lineItem.getPLUItemID());
                    ((KitComponentIfc)pluItem).setItemKitID(((KitComponentLineItemIfc)lineItem).getItemKitID());
                }

                // if gift card, find gift card number
                if (pluItem instanceof GiftCardPLUItemIfc)
                {
                    selectGiftCard(dataConnection, transaction, lineItem.getLineNumber(), (GiftCardPLUItemIfc)pluItem, lineItem);
                }

                // if alterations item, set line item alteration item flag,
                // alteration item price and alteration
                if (pluItem instanceof AlterationPLUItemIfc)
                {
                    lineItem.setAlterationItemFlag(true);
                    AlterationPLUItemIfc altItem = (AlterationPLUItemIfc)pluItem;
                    altItem.setPrice(lineItem.getSellingPrice());
                    selectAlteration(dataConnection, transaction, lineItem.getLineNumber(), altItem);
                }

                lineItem.setPLUItem(pluItem);
                lineItem.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());

                if (lineItem.getReturnItem() != null)
                {
                    lineItem.getReturnItem().setPLUItem(pluItem);
                    lineItem.getReturnItem().setPrice(pluItem.getPrice());
                }

                // See if there is a commission modifier
                int sequenceNumber = lineItem.getLineNumber();

                try
                {
                    String employeeID = selectCommissionModifier(dataConnection, transaction, sequenceNumber);
                    String transactionLevelSalesAssociateEmployeeID = lineItem.getSalesAssociate().getEmployeeID();
                    lineItem.setSalesAssociate(getEmployee(dataConnection, employeeID));
                    if(!transactionLevelSalesAssociateEmployeeID.equals(employeeID))
                    {
                        lineItem.setSalesAssociateModifiedAtLineItem(true);
                    }
                }
                catch (DataException de)
                {
                    // ignore
                }

                // Add item discounts for each line item
                itemDiscounts = selectRetailPriceModifiers(dataConnection, transaction, lineItem, localeRequestor);
                itemDiscounts.addAll(selectSaleReturnPriceModifiers(dataConnection, transaction, lineItem, localeRequestor));
                lineItem.getItemPrice().setItemDiscounts(itemDiscounts.toArray(new ItemDiscountStrategyIfc[0]));

                // Add item promotions for each line item
                PromotionLineItemIfc[] promotionLineItems = selectPromotionLineItems(dataConnection, transaction, lineItem);
                lineItem.getItemPrice().setPromotionLineItems(promotionLineItems);

                // See if there is an item tax entry
                ItemTaxIfc tax = selectSaleReturnTaxModifier(dataConnection, transaction, lineItem, localeRequestor);

                if (tax != null)
                {
                    if (lineItem.getItemTaxMethod() == ItemTaxIfc.ITEM_TAX_EXTERNAL_RATE)
                    {
                        tax.setItemTaxAmount(lineItem.getItemPrice().getItemTaxAmount());
                        tax.setItemInclusiveTaxAmount(lineItem.getItemPrice().getItemInclusiveTaxAmount());
                    }

                    lineItem.getItemPrice().setItemTax(tax);

                    if (lineItem.getReturnItem() != null)
                    {
                        lineItem.getReturnItem().setTaxRate(tax.getDefaultRate());
                    }
                }

                // When the sale return line item record was read, the tax mode was unknown.
                // The mode can be set from vales in either SaleReturnTaxLineItem
                // Table or the TaxModifier Table; however, if neither is available,
                // this code set it explicitly based on the on item taxability.
                if (lineItem.getItemPrice().getItemTax().getTaxMode() == TAX_MODE_NOT_SET)
                {
                    if (pluItem.getTaxable())
                    {
                        lineItem.getItemPrice().getItemTax().setTaxMode(TaxIfc.TAX_MODE_STANDARD);
                    }
                    else
                    {
                        lineItem.getItemPrice().getItemTax().setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
                    }
                }

                // add external order line item information
                selectExternalOrderLineItem(dataConnection, transaction, lineItem);

                lineItem.getItemPrice().calculateItemTotal();
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing sale return line items", exc);
        }

        associateKitComponents(saleReturnLineItems);

        int numItems = saleReturnLineItems.size();
        SaleReturnLineItemIfc[] lineItems = new SaleReturnLineItemIfc[numItems];
        saleReturnLineItems.copyInto(lineItems);
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItems");

        return (lineItems);
    }

    /**
     * Reads the sale return line items.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @param localeRequestor the requested locales
     * @return Array of Sale Return line items
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected SaleReturnLineItemIfc[] selectSaleReturnLineItems(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItems()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_SALE_RETURN_LINE_ITEM, ALIAS_SALE_RETURN_LINE_ITEM);
        sql.addTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM, ALIAS_RETAIL_TRANSACTION_LINE_ITEM);

        /*
         * Add Column(s)
         */
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_GIFT_REGISTRY_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ITEM_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_QUANTITY);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIAL_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ORIGINAL_TRANSACTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_BUSINESS_DAY_DATE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_REASON_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE);  
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE);          
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_KIT_SET_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_COLLECTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_KIT_HEADER_REFERENCE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SHIPPING_CHARGE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_LABEL_COUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_GIFT_RECEIPT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID_ENTRY_METHOD_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SIZE_CODE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICEADJ_REFERENCE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETURN_RELATED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_REMOVE_RELATED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIALIZED_ITEM_VALIDATION_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_DISCOUNT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_DAMAGE_DISCOUNT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + NON_RETRIEVED_ORIGINAL_RECEIPT_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_AGE_RESTRICTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_CLEARANCE_INDICATOR);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG);

        /*
         * Add Qualifier(s)
         */
        // For the specific transaction only
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + getBusinessDayString(transaction));
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + "="
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                + " = " + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);

        // order by line item sequence number
        sql
                .addOrdering(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                        + " ASC");

        Vector<SaleReturnLineItemIfc> saleReturnLineItems = new Vector<SaleReturnLineItemIfc>(2);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            TransactionTaxIfc transactionTax = transaction.getTransactionTax();

            HashMap<Integer, String> reasonCodeMap = new HashMap<Integer, String>();
            HashMap<Integer, String> itemConditionCodeMap = new HashMap<Integer, String>();
            while (rs.next())
            {
                int index = 0;
                String giftRegistryID = getSafeString(rs, ++index);
                String posItemID = getSafeString(rs, ++index);
                String itemID = getSafeString(rs, ++index);
                BigDecimal quantity = getBigDecimal(rs, ++index);
                CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
                /* CurrencyIfc itemTaxAmount = getLongerCurrencyFromDecimal(rs, */++index;
                /*
                 * CurrencyIfc itemIncTaxAmount =
                 * getLongerCurrencyFromDecimal(rs,
                 */++index;
                int sequenceNumber = rs.getInt(++index);
                String serialNumber = getSafeString(rs, ++index);
                BigDecimal quantityReturned = getBigDecimal(rs, ++index);
                String originalTransactionID = getSafeString(rs, ++index);
                EYSDate originalTransactionBusinessDay = getEYSDateFromString(rs, ++index);
                int originalTransactionLineNumber = rs.getInt(++index);
                String originalStoreID = getSafeString(rs, ++index);
                boolean returnFlag = getBooleanFromString(rs, ++index);
                String returnReasonCode = getSafeString(rs, ++index);
                String returnItemConditionCode = getSafeString(rs, ++index);
                if (Util.isBlank(returnItemConditionCode))
                {
                    returnItemConditionCode = CodeConstantsIfc.CODE_UNDEFINED;
                }
                CurrencyIfc restockingFee = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc balanceDue = getCurrencyFromDecimal(rs, ++index); 
                boolean pickupCancelledPrice = getBooleanFromString(rs, ++index);
                boolean pickupInStorePrice = getBooleanFromString(rs, ++index);
                int kitCode = rs.getInt(++index);
                String itemKitID = getSafeString(rs, ++index);
                int kitReference = rs.getInt(++index);
                String sendFlag = getSafeString(rs, ++index);
                String shippingChargeFlag = getSafeString(rs, ++index);
                int sendLabelCount = rs.getInt(++index);
                String giftReceiptStr = getSafeString(rs, ++index);
                String orderID = rs.getString(++index);
                int orderLineReference = rs.getInt(++index);
                /*String entryMethod =*/ getSafeString(rs, ++index);
                String sizeCode = getSafeString(rs, ++index);
                String itemVoidFlag = getSafeString(rs, ++index);
                boolean isPriceAdjLineItem = rs.getBoolean(++index);
                int priceAdjReferenceID = rs.getInt(++index);
                boolean returnRelatedItemFlag = rs.getBoolean(++index);
                int relatedSeqNumber = rs.getInt(++index);
                boolean deleteRelatedItemFlag = rs.getBoolean(++index);
                boolean saleAsscModifiedFlag = getBooleanFromString(rs, ++index);
                CurrencyIfc beforeOverride = getCurrencyFromDecimal(rs, ++index);
                String receiptDescription = getSafeString(rs, ++index);
                Locale receiptDescriptionLocale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));
                boolean restockingFeeFlag = rs.getBoolean(++index);
                boolean serializedItemFlag = rs.getBoolean(++index);
                boolean externalValidationSerializedItemFlag = rs.getBoolean(++index);
                boolean isPOSAllowedtoCreateUIN = rs.getBoolean(++index);
                String productGroupID = getSafeString(rs, ++index);
                boolean sizeRequiredFlag = rs.getBoolean(++index);
                String unitOfMeasureCode =  getSafeString(rs, ++index);
                String posDepartmentID =  getSafeString(rs, ++index);
                int itemTypeID = rs.getInt(++index);
                boolean returnEligible = !(rs.getBoolean(++index));
                boolean employeeDiscountEligible = (rs.getBoolean(++index));
                int taxGroupId = rs.getInt(++index);
                boolean taxable = (rs.getBoolean(++index));
                boolean discountable = (rs.getBoolean(++index));
                boolean damageDiscountable = (rs.getBoolean(++index));
                String merchandiseHierarchyGroupID = getSafeString(rs, ++index);
                String manufacturerItemUPC = getSafeString(rs, ++index);
                String nonRetrievedOriginalReceiptId = getSafeString(rs, ++index);
                int restrictiveAge = rs.getInt(++index);
                boolean clearanceIndicator = (rs.getBoolean(++index));
                boolean priceEntryRequired = (rs.getBoolean(++index));

                CurrencyIfc lineItemTaxAmount = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);
                CurrencyIfc lineItemIncTaxAmount = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);

                // create and initialize item price object
                ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();
                ItemTaxIfc itemTax = DomainGateway.getFactory().getItemTaxInstance();
                price.setExtendedSellingPrice(amount);
                price.setDiscountEligible(discountable);
                price.setExtendedRestockingFee(restockingFee);

                if (quantity.signum() != 0)
                {
                    amount = amount.divide(new BigDecimal(quantity.toString()));
                    if (restockingFee != null)
                    {
                        restockingFee = restockingFee.divide(new BigDecimal(quantity.toString()));
                    }
                }

                price.setSellingPrice(amount);
                price.setPermanentSellingPrice(beforeOverride);
                price.setRestockingFee(restockingFee);

                // Obtain the previously calculated and saved line item tax
                itemTax = price.getItemTax();
                itemTax.setDefaultRate(transactionTax.getDefaultRate());
                itemTax.setDefaultTaxRules(transactionTax.getDefaultTaxRules());
                itemTax.setItemTaxAmount(lineItemTaxAmount);
                itemTax.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
                // The tax mode is unknown at this point; It can be set from
                // SaleReturnTaxLineItem Table or the TaxModifier Table;
                // however,
                // if neither is available it must be explicitly set
                // depending on item taxability.
                itemTax.setTaxMode(TAX_MODE_NOT_SET);
                price.setItemTax(itemTax);

                // price.setItemTaxAmount(itemTaxAmount);
                price.setItemTaxAmount(lineItemTaxAmount);
                price.setItemTaxAmount(lineItemIncTaxAmount);

                // price.setTaxGroupId(taxGroupID);
                price.setItemQuantity(quantity);

                SaleReturnLineItemIfc lineItem;
                // create and initialize appropriate line item object
                if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
                {
                    lineItem = DomainGateway.getFactory().getOrderLineItemInstance();
                }
                else
                {
                    switch (kitCode)
                    {
                        case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
                            lineItem = DomainGateway.getFactory().getKitHeaderLineItemInstance();
                            break;
                        case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
                            lineItem = DomainGateway.getFactory().getKitComponentLineItemInstance();
                            ((KitComponentLineItemIfc)lineItem).setItemKitID(itemKitID);
                            break;
                        default:
                            lineItem = DomainGateway.getFactory().getSaleReturnLineItemInstance();
                            break;
                    }
                }

                lineItem.setPLUItemID(itemID);
                lineItem.setItemPrice(price);
                lineItem.setItemTaxAmount(lineItemTaxAmount);
                lineItem.setItemInclusiveTaxAmount(lineItemIncTaxAmount);
                lineItem.modifyItemQuantity(quantity);
                lineItem.setLineNumber(sequenceNumber);
                lineItem.setItemSizeCode(sizeCode);

                // set the order id & line item reference number
                lineItem.setOrderID(orderID);
                lineItem.setOrderLineReference(orderLineReference);

                lineItem.setReceiptDescription(receiptDescription);
                lineItem.setReceiptDescriptionLocale(receiptDescriptionLocale);
                lineItem.getItemPrice().setEmployeeDiscountEligible(employeeDiscountEligible);
                
                lineItem.setDepositAmount(depositAmount);
                
                if (lineItem instanceof OrderLineItemIfc)
                {
                    ((OrderLineItemIfc)lineItem).setItemBalanceDue(balanceDue);
                    ((OrderLineItemIfc)lineItem).setPriceCancelledDuringPickup(pickupCancelledPrice);
                    ((OrderLineItemIfc)lineItem).setInStorePriceDuringPickup(pickupInStorePrice);
                }

                // set the KitHeaderReference
                lineItem.setKitHeaderReference(kitReference);

                if (serialNumber != null && serialNumber.length() > 0)
                {
                    lineItem.setItemSerial(serialNumber);
                }
                lineItem.setQuantityReturned(quantityReturned);
                /*
                 * Should probably be a relationship fetched from the gift
                 * registry table
                 */
                if (giftRegistryID.length() > 0)
                {
                    RegistryIDIfc registry = instantiateGiftRegistry();
                    registry.setID(giftRegistryID);
                    lineItem.modifyItemRegistry(registry, true);
                }

                // Return Item Original Transaction information is available
                if (returnFlag)
                {
                    ReturnItemIfc ri = DomainGateway.getFactory().getReturnItemInstance();

                    if (originalTransactionID != null && originalTransactionID.length() > 0)
                    {
                        // Create the transaction id.
                        TransactionIDIfc id = DomainGateway.getFactory().getTransactionIDInstance();
                        id.setTransactionID(originalTransactionID);
                        ri.setOriginalTransactionID(id);
                    }
                    ri.setNonRetrievedOriginalReceiptId(nonRetrievedOriginalReceiptId);

                    if (originalTransactionBusinessDay != null)
                    {
                        ri.setOriginalTransactionBusinessDate(originalTransactionBusinessDay);
                    }
                    ri.setOriginalLineNumber(originalTransactionLineNumber);

                    // DB2 does not support nested result sets, so the localized
                    // reason codes needs to be retrieved after closing the current
                    // result set.  Add the reason code to a map for later retrieval.
                    reasonCodeMap.put(sequenceNumber, returnReasonCode);
                    itemConditionCodeMap.put(sequenceNumber, returnItemConditionCode);

                    if (originalStoreID.equals(transaction.getWorkstation().getStoreID()))
                    {
                        ri.setStore(transaction.getWorkstation().getStore());
                    }
                    else
                    {
                        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
                        store.setStoreID(originalStoreID);
                        ri.setStore(store);
                    }
                    lineItem.setReturnItem(ri);
                }
                /*
                 * Sales Associate defaults to the cashier. If it's different,
                 * that will be accounted for by the selectCommissionModifier
                 * below.
                 */
                if (transaction.getSalesAssociate() != null)
                {
                    lineItem.setSalesAssociate(transaction.getSalesAssociate());
                }
                else
                {
                    lineItem.setSalesAssociate(transaction.getCashier());
                }
                /*
                 * Send Flag
                 */
                if (transaction.getTransactionType() != TransactionIfc.TYPE_RETURN)
                {
                    if (sendFlag.equals("0"))
                    {
                        lineItem.setItemSendFlag(false);
                    }
                    else if (sendFlag.equals("1"))
                    {
                        lineItem.setItemSendFlag(true);
                    }
                    lineItem.setSendLabelCount(sendLabelCount);
                    /*
                     * Shipping Charge Flag
                     */
                    if (shippingChargeFlag.equals("0"))
                    {
                        lineItem.setShippingCharge(false);
                    }
                    else if (shippingChargeFlag.equals("1"))
                    {
                        lineItem.setShippingCharge(true);
                    }
                }

                /**
                 * Gift Receipt Flag
                 */
                if (giftReceiptStr.equals("1"))
                {
                    lineItem.setGiftReceiptItem(true);
                }

                /**
                 * Price Adjustment Flags
                 */
                lineItem.setPriceAdjustmentReference(priceAdjReferenceID);
                lineItem.setIsPriceAdjustmentLineItem(isPriceAdjLineItem);

                if (!itemVoidFlag.equals(DBConstantsIfc.TRUE))
                {
                    saleReturnLineItems.addElement(lineItem);
                }

                lineItem.setRelatedItemReturnable(returnRelatedItemFlag);
                lineItem.setRelatedItemSequenceNumber(relatedSeqNumber);
                lineItem.setRelatedItemDeleteable(deleteRelatedItemFlag);
                lineItem.setSalesAssociateModifiedFlag(saleAsscModifiedFlag);

                // Generate a pluItem from the data in the Sale Return Line Item table
                PLUItemIfc pluItem = instantiatePLUItem(productGroupID, kitCode, transaction.isTrainingMode());
                pluItem.setItemID(itemID);
                pluItem.setPosItemID(posItemID);
                pluItem.setItemSizeRequired(sizeRequiredFlag);
                pluItem.setDepartmentID(posDepartmentID);
                pluItem.setTaxable(taxable);
                pluItem.setTaxGroupID(taxGroupId);
                pluItem.setManufacturerItemUPC(manufacturerItemUPC);
                ItemClassificationIfc itemClassification = DomainGateway.getFactory().getItemClassificationInstance();
                itemClassification.setRestockingFeeFlag(restockingFeeFlag);
                itemClassification.setSerializedItem(serializedItemFlag);
                if ( externalValidationSerializedItemFlag )
                {
                    itemClassification.setSerialEntryTime(STORE_RECEIVING_SERIALIZED_CAPTURE_TIME);
                }
                else
                {
                    itemClassification.setSerialEntryTime(SALE_SERIALIZED_CAPTURE_TIME);
                }
                itemClassification.setExternalSystemCreateUIN(isPOSAllowedtoCreateUIN);
                ProductGroupIfc pg = DomainGateway.getFactory().getProductGroupInstance();
                pg.setGroupID(productGroupID);
                itemClassification.setGroup(pg);
                itemClassification.setItemType(itemTypeID);
                itemClassification.setReturnEligible(returnEligible);
                itemClassification.setEmployeeDiscountAllowedFlag(employeeDiscountEligible);
                itemClassification.setDiscountEligible(discountable);
                itemClassification.setDamageDiscountEligible(damageDiscountable);
                itemClassification.setMerchandiseHierarchyGroup(merchandiseHierarchyGroupID);
                itemClassification.setPriceEntryRequired(priceEntryRequired);
                pluItem.setItemClassification(itemClassification);
                lineItem.setOnClearance(clearanceIndicator);
                pluItem.setSellingPrice(lineItem.getItemPrice().getPermanentSellingPrice());
                UnitOfMeasureIfc pluUOM = DomainGateway.getFactory().getUnitOfMeasureInstance();
                pluUOM.setUnitID(unitOfMeasureCode);
                pluItem.setUnitOfMeasure(pluUOM);
                pluItem.setRestrictiveAge(restrictiveAge);
                selectOptionalI18NPLUData(dataConnection, pluItem, localeRequestor, lineItem);
                lineItem.setPLUItem(pluItem);
            }
            rs.close();

            // Set the localized reason code for return line items
            for (int lineItemCounter = 0; lineItemCounter < saleReturnLineItems.size(); lineItemCounter++)
            {
                if (saleReturnLineItems.get(lineItemCounter).isReturnLineItem())
                {
                    int sequenceNumber = saleReturnLineItems.get(lineItemCounter).getLineNumber();

                    // Get the reason code from the map
                    String reasonCode = reasonCodeMap.get(sequenceNumber);

                    // Retrieve localized reason code
                    saleReturnLineItems.get(lineItemCounter).getReturnItem().setReason(
                            getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier().getStoreID(),
                                    reasonCode, CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES, localeRequestor));

                  //Get the item condition code from the map
                    String itemConditionCode = itemConditionCodeMap.get(sequenceNumber);

                    //retreve localized item condition code
                    saleReturnLineItems.get(lineItemCounter).getReturnItem().setItemCondition(
                            getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier().getStoreID(),
                                    itemConditionCode, CodeConstantsIfc.CODE_LIST_RETURN_ITEM_CONDITION_CODES, localeRequestor));

                }
            }

            // Do separate query to find the possible line item tax amount from
            // tr_ltm_sls_rtn_tx table.
            for (int i = 0; i < saleReturnLineItems.size(); i++)
            {
                SaleReturnLineItemIfc srli = saleReturnLineItems.elementAt(i);
                int lineItemSequenceNumber = srli.getLineNumber();
                TaxInformationIfc[] taxInfoArray = selectSaleReturnLineItemTaxInformation(dataConnection, transaction,
                        lineItemSequenceNumber);
                TaxInformationContainerIfc container = DomainGateway.getFactory().getTaxInformationContainerInstance();
                for (int j = 0; j < taxInfoArray.length; j++)
                {
                    container.addTaxInformation(taxInfoArray[j]);
                    srli.getItemPrice().getItemTax().setTaxMode(taxInfoArray[j].getTaxMode());
                }
                srli.getItemPrice().getItemTax().setTaxInformationContainer(container);
                CurrencyIfc[] taxAmount = selectSaleReturnLineItemTaxAmount(dataConnection, transaction,
                        lineItemSequenceNumber);
                srli.setItemTaxAmount(taxAmount[0]); // the first element is add
                                                     // on item tax
                srli.setItemInclusiveTaxAmount(taxAmount[1]); // the second
                                                              // element is
                                                              // inclusive item
                                                              // tax
                srli.setFromTransaction(true);
            }

            /*
             * Grab auxilliary elements
             */
            List<ItemDiscountStrategyIfc> itemDiscounts = null;
            for (SaleReturnLineItemIfc lineItem: saleReturnLineItems)
            {
                /*
                 * Grab the PLUItem
                 */
                PLUItemIfc pluItem = lineItem.getPLUItem();;

                if (lineItem.isKitComponent() && pluItem.isKitComponent())
                {
                    pluItem.setItemID(lineItem.getPLUItemID());
                    ((KitComponentIfc)pluItem).setItemKitID(((KitComponentLineItemIfc)lineItem).getItemKitID());
                }

                // if gift card, find gift card number
                if (pluItem instanceof GiftCardPLUItemIfc)
                {
                    selectGiftCard(dataConnection, transaction, lineItem.getLineNumber(), (GiftCardPLUItemIfc)pluItem, lineItem);
                }

                // if alterations item, set line item alteration item flag,
                // alteration item price
                // and alteration
                if (pluItem instanceof AlterationPLUItemIfc)
                {
                    lineItem.setAlterationItemFlag(true);
                    AlterationPLUItemIfc altItem = (AlterationPLUItemIfc)pluItem;
                    altItem.setPrice(lineItem.getSellingPrice());
                    selectAlteration(dataConnection, transaction, lineItem.getLineNumber(), altItem);
                }

                lineItem.setPLUItem(pluItem);
                lineItem.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());

                if (lineItem.getReturnItem() != null)
                {
                    lineItem.getReturnItem().setPLUItem(pluItem);
                    lineItem.getReturnItem().setPrice(pluItem.getPrice());
                }

                /*
                 * See if there is a commission modifier
                 */
                int sequenceNumber = lineItem.getLineNumber();

                try
                {
                    String employeeID = selectCommissionModifier(dataConnection, transaction, sequenceNumber);
                    // Detect if the sales associate is another person than the
                    // cashier
                    if (!employeeID.equals(transaction.getCashier().getEmployeeID()))
                    {
                        ((AbstractTransactionLineItem)lineItem).setSalesAssociateModifiedFlag(true);
                    }
                    lineItem.setSalesAssociate(getEmployee(dataConnection, employeeID));

                    if(!employeeID.equals(transaction.getSalesAssociate()))
                    {
                        lineItem.setSalesAssociateModifiedAtLineItem(true);
                    }
                }
                catch (DataException de)
                {
                    // ignore
                }
                /*
                 * Add item discounts for each line item
                 */
                itemDiscounts = selectRetailPriceModifiers(dataConnection, transaction, lineItem, localeRequestor);
                itemDiscounts.addAll(selectSaleReturnPriceModifiers(dataConnection, transaction, lineItem, localeRequestor));
                lineItem.getItemPrice().setItemDiscounts(itemDiscounts.toArray(new ItemDiscountStrategyIfc[0]));

                /*
                 * See if there is an item tax entry
                 */
                ItemTaxIfc tax = selectSaleReturnTaxModifier(dataConnection, transaction, lineItem, localeRequestor);

                if (tax != null)
                {
                    if (lineItem.getItemTaxMethod() == ItemTaxIfc.ITEM_TAX_EXTERNAL_RATE)
                    {
                        tax.setItemTaxAmount(lineItem.getItemPrice().getItemTaxAmount());
                        tax.setItemInclusiveTaxAmount(lineItem.getItemPrice().getItemInclusiveTaxAmount());
                    }

                    lineItem.getItemPrice().setItemTax(tax);

                    if (lineItem.getReturnItem() != null)
                    {
                        lineItem.getReturnItem().setTaxRate(tax.getDefaultRate());
                    }
                }
                // When the sale return line item record was read, the tax mode
                // was unknown.
                // The mode can be set from vales in either
                // SaleReturnTaxLineItem
                // Table or the TaxModifier Table; however, if neither is
                // available,
                // this code set it explicitly based on the on item taxability.
                if (lineItem.getItemPrice().getItemTax().getTaxMode() == TAX_MODE_NOT_SET)
                {
                    if (pluItem.getTaxable())
                    {
                        lineItem.getItemPrice().getItemTax().setTaxMode(TaxIfc.TAX_MODE_STANDARD);
                    }
                    else
                    {
                        lineItem.getItemPrice().getItemTax().setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
                    }
                }

                // add external order line item information
                selectExternalOrderLineItem(dataConnection, transaction, lineItem);

                lineItem.getItemPrice().calculateItemTotal();
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing sale return line items", exc);
        }

        associateKitComponents(saleReturnLineItems);

        int numItems = saleReturnLineItems.size();
        SaleReturnLineItemIfc[] lineItems = new SaleReturnLineItemIfc[numItems];
        saleReturnLineItems.copyInto(lineItems);
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnLineItems");

        return (lineItems);
    }

    /**
     * Selects gift card for specified line item.
     *
     * @param dataConnection database connection
     * @param transaction transaction reference
     * @param sequenceNumber retail transaction line item sequence number
     * @param giftCardItem gift card PLU item
     * @exception DataException is thrown if error occurs
     * @deprecated As of 13.4, please use {@link JdbcReadTransaction#selectGiftCard(JdbcDataConnection, SaleReturnTransactionIfc, int, GiftCardPLUItemIfc, SaleReturnLineItemIfc)} instead.
     */
    protected void selectGiftCard(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction,
            int sequenceNumber, GiftCardPLUItemIfc giftCardItem) throws DataException
    {
        this.selectGiftCard(dataConnection, transaction, sequenceNumber, giftCardItem, null);
    }

    /**
     * Selects gift card for specified line item.
     *
     * @param dataConnection database connection
     * @param transaction transaction reference
     * @param sequenceNumber retail transaction line item sequence number
     * @param giftCardItem gift card PLU item
     * @param lineItem sale Return Line Item
     * @exception DataException is thrown if error occurs
     */
    protected void selectGiftCard(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction,
            int sequenceNumber, GiftCardPLUItemIfc giftCardItem, SaleReturnLineItemIfc lineItem) throws DataException
    {
        // build SQL statement to select gift card
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_GIFT_CARD);
        sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER);
        sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER);
        sql.addColumn(FIELD_CURRENCY_ID); // I18N
        sql.addColumn(FIELD_GIFT_CARD_ACTIVATION_ADJUDICATION_CODE);
        sql.addColumn(FIELD_GIFT_CARD_ENTRY_METHOD);
        sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE);
        sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE);
        sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_JOURNAL_KEY);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER);

        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(sequenceNumber));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String cardNumber = getSafeString(rs, ++index);
                String maskedCardNumber = getSafeString(rs, ++index);
                EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(
                        cardNumber, maskedCardNumber, null);
                giftCardItem.getGiftCard().setEncipheredCardData(cardData);
                giftCardItem.getGiftCard().setCurrencyID(rs.getInt(++index)); // I18N
                giftCardItem.getGiftCard().setApprovalCode(getSafeString(rs, ++index));
                giftCardItem.getGiftCard().setEntryMethod(EntryMethod.valueOf(getSafeString(rs, ++index)));
                giftCardItem.getGiftCard().setRequestType(rs.getInt(++index));
                CurrencyIfc currentBalance = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
                giftCardItem.getGiftCard().setCurrentBalance(currentBalance);
                CurrencyIfc initialBalance = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
                giftCardItem.getGiftCard().setInitialBalance(initialBalance);
                giftCardItem.getGiftCard().setSettlementData(getSafeString(rs, ++index));
                Timestamp authDate = rs.getTimestamp(++index);
                if (authDate != null)
                {
                    giftCardItem.getGiftCard().setAuthorizedDateTime(new EYSDate(authDate));
                }
                giftCardItem.getGiftCard().setJournalKey(getSafeString(rs, ++index));
                giftCardItem.getGiftCard().setTraceNumber(getSafeString(rs, ++index));

                // if this read is of a suspended transaction, then set the giftCard.requestedAmount
                // to the extendended selling price of the line item.  This ensures that the gift card
                // can be activated for the desired amount.
                if(TransactionConstantsIfc.STATUS_SUSPENDED == transaction.getTransactionStatus() && lineItem != null)
                {
                    giftCardItem.getGiftCard().setReqestedAmount(lineItem.getExtendedSellingPrice());
                }
            }
            rs.close();
        }
        catch (DataException de)
        {
            // ignore no-data data exception
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Error reading gift card: " + e.toString());
        }
    }

    /**
     * Selects alteration for specified line item.
     *
     * @param dataConnection database connection
     * @param transaction transaction reference
     * @param sequenceNumber retail transaction line item sequence number
     * @param alterationItem alteration PLU item
     * @exception DataException is thrown if error occurs
     */
    protected void selectAlteration(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction,
            int sequenceNumber, AlterationPLUItemIfc alterationItem) throws DataException
    {
        // build SQL statement to select alteration
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_ALTERATION_LINE_ITEM);
        sql.addColumn(FIELD_ALTERATION_TYPE);
        sql.addColumn(FIELD_ITEM_DESCRIPTION);
        sql.addColumn(FIELD_ITEM_ID);
        sql.addColumn(FIELD_VALUE1);
        sql.addColumn(FIELD_VALUE2);
        sql.addColumn(FIELD_VALUE3);
        sql.addColumn(FIELD_VALUE4);
        sql.addColumn(FIELD_VALUE5);
        sql.addColumn(FIELD_VALUE6);
        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, Integer.toString(sequenceNumber));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                alterationItem.getAlteration().setAlterationType(Integer.parseInt(getSafeString(rs, ++index)));
                alterationItem.getAlteration().setItemDescription(getSafeString(rs, ++index));
                alterationItem.getAlteration().setItemNumber(getSafeString(rs, ++index));
                alterationItem.getAlteration().setValue1(getSafeString(rs, ++index));
                alterationItem.getAlteration().setValue2(getSafeString(rs, ++index));
                alterationItem.getAlteration().setValue3(getSafeString(rs, ++index));
                alterationItem.getAlteration().setValue4(getSafeString(rs, ++index));
                alterationItem.getAlteration().setValue5(getSafeString(rs, ++index));
                alterationItem.getAlteration().setValue6(getSafeString(rs, ++index));
            }
            rs.close();
        }
        catch (DataException de)
        {
            // ignore no-data data exception
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "Error reading alteration line item: " + e.toString());
        }
    }

    /**
     * Reads the deleted sale return line items.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @param sqlLocale Locale being used for SQL
     * @return Array of SaleReturn line items
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected SaleReturnLineItemIfc[] selectDeletedSaleReturnLineItems(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectDeletedSaleReturnLineItems()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_SALE_RETURN_LINE_ITEM, ALIAS_SALE_RETURN_LINE_ITEM);
        sql.addTable(TABLE_RETAIL_TRANSACTION_LINE_ITEM, ALIAS_RETAIL_TRANSACTION_LINE_ITEM);
        /*
         * Add Column(s)
         */
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_GIFT_REGISTRY_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ITEM_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_QUANTITY);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_EXTENDED_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_VAT_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_TAX_INC_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIAL_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_RETURN_QUANTITY);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_POS_ORIGINAL_TRANSACTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_BUSINESS_DAY_DATE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORIGINAL_RETAIL_STORE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_REASON_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_MERCHANDISE_RETURN_ITEM_CONDITION_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_RESTOCKING_FEE_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_DEPOSIT_AMOUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_BALANCE_DUE);  
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_PICKUP_CANCEL_PRICE);  
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_RETURN_LINE_ITEM_PICKUP_INSTORE_PRICE);  
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_KIT_SET_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_COLLECTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_KIT_HEADER_REFERENCE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SHIPPING_CHARGE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SEND_LABEL_COUNT);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_GIFT_RECEIPT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_ID_ENTRY_METHOD_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SIZE_CODE);
        sql.addColumn(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_VOID_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICEADJ_LINE_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICEADJ_REFERENCE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETURN_RELATED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RELATED_ITEM_TRANSACTION_LINE_ITEM_SEQ_NUMBER);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_REMOVE_RELATED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALES_ASSC_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_PREMANENT_RETAIL_PRICE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RECEIPT_DESCRIPTION_LOCALE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RESTOCKING_FEE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIALIZED_ITEM_VALIDATION_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_EXTERNAL_VALIDATION_SERIALIZED_ITEM_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SERIALIZED_ITEM_EXTERNAL_SYSTEM_CREATE_UIN);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_LEVEL_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SIZE_REQUIRED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_UNIT_OF_MEASURE_CODE);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_POS_DEPARTMENT_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SALE_ITEM_TYPE_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_RETURN_PROHIBITED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_EMPLOYEE_DISCOUNT_ALOWED_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_TAXABLE_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_DISCOUNT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_DAMAGE_DISCOUNT_FLAG);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MERCHANDISE_HIERARCHY_GROUP_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_MANUFACTURER_ITEM_UPC);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + NON_RETRIEVED_ORIGINAL_RECEIPT_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_SALE_AGE_RESTRICTION_ID);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_CLEARANCE_INDICATOR);
        sql.addColumn(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_ITEM_PRICE_ENTRY_REQUIRED_FLAG);

        /*
         * Add Qualifier(s)
         */
        // For the specific transaction only
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + getBusinessDayString(transaction));
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID + "="
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_WORKSTATION_ID + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                + " = " + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addQualifier(ALIAS_RETAIL_TRANSACTION_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);

        // order by line item sequence number
        sql
                .addOrdering(ALIAS_SALE_RETURN_LINE_ITEM + "." + FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER
                        + " ASC");

        Vector<SaleReturnLineItemIfc> saleReturnLineItems = new Vector<SaleReturnLineItemIfc>(2);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            HashMap<Integer, String> reasonCodeMap = new HashMap<Integer, String>(1);
            HashMap<Integer, String> itemConditionCodeMap = new HashMap<Integer, String>(1);

            while (rs.next())
            {
                int index = 0;
                String giftRegistryID = getSafeString(rs, ++index);
                String posItemID = getSafeString(rs, ++index);
                String itemID = getSafeString(rs, ++index);
                BigDecimal quantity = getBigDecimal(rs, ++index);
                CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemTaxAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc itemIncTaxAmount = getCurrencyFromDecimal(rs, ++index);
                int sequenceNumber = rs.getInt(++index);
                String serialNumber = getSafeString(rs, ++index);
                BigDecimal quantityReturned = getBigDecimal(rs, ++index);
                String originalTransactionID = getSafeString(rs, ++index);
                EYSDate originalTransactionBusinessDay = getEYSDateFromString(rs, ++index);
                int originalTransactionLineNumber = rs.getInt(++index);
                String originalStoreID = getSafeString(rs, ++index);
                boolean returnFlag = getBooleanFromString(rs, ++index);
                String returnReasonCode = getSafeString(rs, ++index);
                String returnItemConditionCode = getSafeString(rs, ++index);
                if (Util.isBlank(returnItemConditionCode))
                {
                    returnItemConditionCode = CodeConstantsIfc.CODE_UNDEFINED;
                }
                CurrencyIfc restockingFee = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc depositAmount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc balanceDue = getCurrencyFromDecimal(rs, ++index); 
                boolean pickupCancelledPrice = getBooleanFromString(rs, ++index);
                boolean pickupInStorePrice = getBooleanFromString(rs, ++index);
                int kitCode = rs.getInt(++index);
                String itemKitID = getSafeString(rs, ++index);
                int kitReference = rs.getInt(++index);
                String sendFlag = getSafeString(rs, ++index);
                String shippingChargeFlag = getSafeString(rs, ++index);
                int sendLabelCount = rs.getInt(++index);
                String giftReceiptStr = getSafeString(rs, ++index);
                String orderID = rs.getString(++index);
                int orderLineReference = rs.getInt(++index);
                String entryMethod = getSafeString(rs, ++index);
                String sizeCode = getSafeString(rs, ++index);
                String itemVoidFlag = getSafeString(rs, ++index);
                boolean isPriceAdjLineItem = rs.getBoolean(++index);
                int priceAdjReferenceID = rs.getInt(++index);
                boolean returnRelatedItemFlag = rs.getBoolean(++index);
                int relatedSeqNumber = rs.getInt(++index);
                boolean deleteRelatedItemFlag = rs.getBoolean(++index);
                boolean saleAsscModifiedFlag = getBooleanFromString(rs, ++index);
                CurrencyIfc beforeOverride = getCurrencyFromDecimal(rs, ++index);
                String receiptDescription = getSafeString(rs, ++index);
                Locale receiptDescriptionLocale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));
                boolean restockingFeeFlag = rs.getBoolean(++index);
                boolean serializedItemFlag = rs.getBoolean(++index);
                boolean externalValidationSerializedItemFlag = rs.getBoolean(++index);
                boolean isPOSAllowedToCreateUIN = rs.getBoolean(++index);
                String productGroupID = getSafeString(rs, ++index);
                boolean sizeRequiredFlag = rs.getBoolean(++index);
                String unitOfMeasureCode =  getSafeString(rs, ++index);
                String posDepartmentID =  getSafeString(rs, ++index);
                int itemTypeID = rs.getInt(++index);
                boolean returnEligible = !(rs.getBoolean(++index));
                boolean employeeDiscountEligible = (rs.getBoolean(++index));
                int taxGroupId = rs.getInt(++index);
                boolean taxable = (rs.getBoolean(++index));
                boolean discountable = (rs.getBoolean(++index));
                boolean damageDiscountable = (rs.getBoolean(++index));
                String merchandiseHierarchyGroupID = getSafeString(rs, ++index);
                String manufacturerItemUPC = getSafeString(rs, ++index);
                String nonRetrievedOriginalReceiptId = getSafeString(rs, ++index);
                int restrictiveAge = rs.getInt(++index);
                boolean clearanceIndicator = (rs.getBoolean(++index));
                boolean priceEntryRequired = (rs.getBoolean(++index));

                // create and initialize item price object
                ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();
                price.setExtendedSellingPrice(amount);
                price.setDiscountEligible(discountable);
                price.setExtendedRestockingFee(restockingFee);
                // The tax mode is unknown at this point; It can be set from
                // SaleReturnTaxLineItem Table or the TaxModifier Table;
                // however,
                // if neither is available it must be explicitly set
                // depending on item taxability.
                price.getItemTax().setTaxMode(TAX_MODE_NOT_SET);

                if (quantity.signum() != 0)
                {
                    amount = amount.divide(new BigDecimal(quantity.toString()));
                    if (restockingFee != null)
                    {
                        restockingFee = restockingFee.divide(new BigDecimal(quantity.toString()));
                    }
                }

                price.setSellingPrice(amount);
                price.setPermanentSellingPrice(beforeOverride);
                price.setRestockingFee(restockingFee);
                price.setItemTaxAmount(itemTaxAmount);
                price.setItemInclusiveTaxAmount(itemIncTaxAmount);
                // price.setTaxGroupId(taxGroupID);
                price.setItemQuantity(quantity);

                SaleReturnLineItemIfc lineItem;
                // create and initialize appropriate line item object
                if (transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_CANCEL
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_COMPLETE
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_PARTIAL
                        || transaction.getTransactionType() == TransactionIfc.TYPE_ORDER_INITIATE)
                {
                    lineItem = DomainGateway.getFactory().getOrderLineItemInstance();
                }
                else
                {
                    switch (kitCode)
                    {
                        case ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER:
                            lineItem = DomainGateway.getFactory().getKitHeaderLineItemInstance();
                            break;
                        case ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT:
                            lineItem = DomainGateway.getFactory().getKitComponentLineItemInstance();
                            ((KitComponentLineItemIfc)lineItem).setItemKitID(itemKitID);
                            break;
                        default:
                            lineItem = DomainGateway.getFactory().getSaleReturnLineItemInstance();
                            break;
                    }
                }

                lineItem.setPLUItemID(itemID);
                lineItem.setItemPrice(price);
                lineItem.modifyItemQuantity(quantity);
                lineItem.setLineNumber(sequenceNumber);

                // set the order id & line item reference number
                lineItem.setOrderID(orderID);
                lineItem.setOrderLineReference(orderLineReference);

                lineItem.setReceiptDescription(receiptDescription);
                lineItem.setReceiptDescriptionLocale(receiptDescriptionLocale);
                lineItem.getItemPrice().setEmployeeDiscountEligible(employeeDiscountEligible);

                // set the KitHeaderReference
                lineItem.setKitHeaderReference(kitReference);
                
                lineItem.setDepositAmount(depositAmount);
                
                if (lineItem instanceof OrderLineItemIfc)
                {
                    ((OrderLineItemIfc)lineItem).setItemBalanceDue(balanceDue);
                    ((OrderLineItemIfc)lineItem).setPriceCancelledDuringPickup(pickupCancelledPrice);
                    ((OrderLineItemIfc)lineItem).setInStorePriceDuringPickup(pickupInStorePrice);
                }
                
                if (serialNumber != null && serialNumber.length() > 0)
                {
                    lineItem.setItemSerial(serialNumber);
                }
                lineItem.setQuantityReturned(quantityReturned);
                /*
                 * Should probably be a relationship fetched from the gift
                 * registry table
                 */
                if (giftRegistryID.length() > 0)
                {
                    RegistryIDIfc registry = instantiateGiftRegistry();
                    registry.setID(giftRegistryID);
                    lineItem.modifyItemRegistry(registry, true);
                }

                // Return Item Original Transaction information is available
                if (returnFlag)
                {
                    ReturnItemIfc ri = DomainGateway.getFactory().getReturnItemInstance();

                    if (originalTransactionID != null && originalTransactionID.length() > 0)
                    {
                        // Create the transaction id.
                        TransactionIDIfc id = DomainGateway.getFactory().getTransactionIDInstance();
                        id.setTransactionID(originalTransactionID);
                        ri.setOriginalTransactionID(id);
                    }
                    ri.setNonRetrievedOriginalReceiptId(nonRetrievedOriginalReceiptId);

                    if (originalTransactionBusinessDay != null)
                    {
                        ri.setOriginalTransactionBusinessDate(originalTransactionBusinessDay);
                    }
                    ri.setOriginalLineNumber(originalTransactionLineNumber);

                    // DB2 does not support nested result sets, so the localized
                    // reason codes needs to be retrieved after closing the current
                    // result set.  Add the reason code to a map for later retrieval.
                    reasonCodeMap.put(sequenceNumber, returnReasonCode);
                    itemConditionCodeMap.put(sequenceNumber, returnItemConditionCode);

                    if (originalStoreID.equals(transaction.getWorkstation().getStoreID()))
                    {
                        ri.setStore(transaction.getWorkstation().getStore());
                    }
                    else
                    {
                        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
                        store.setStoreID(originalStoreID);
                        ri.setStore(store);
                    }

                    // ri.setTaxRate(lineItem.getItemTax().getDefaultRate()); -
                    // external tax mgr
                    // this field should be unnecessary, but it isn't
                    // if really want sales assoc. tied with a return item, then
                    // execute this code.

                    lineItem.setReturnItem(ri);
                }
                /*
                 * Sales Associate defaults to the cashier. If it's different,
                 * that will be accounted for by the selectCommissionModifier
                 * below.
                 */
                if (transaction.getSalesAssociate() != null)
                {
                    lineItem.setSalesAssociate(transaction.getSalesAssociate());
                }
                else
                {
                    lineItem.setSalesAssociate(transaction.getCashier());
                }
                /*
                 * Send Flag
                 */
                if (transaction.getTransactionType() != TransactionIfc.TYPE_RETURN)
                {
                    if (sendFlag.equals("0"))
                    {
                        lineItem.setItemSendFlag(false);
                    }
                    else if (sendFlag.equals("1"))
                    {
                        lineItem.setItemSendFlag(true);
                    }
                    lineItem.setSendLabelCount(sendLabelCount);
                    /*
                     * Shipping Charge Flag
                     */
                    if (shippingChargeFlag.equals("0"))
                    {
                        lineItem.setShippingCharge(false);
                    }
                    else if (sendFlag.equals("1"))
                    {
                        lineItem.setShippingCharge(true);
                    }
                }

                /**
                 * Gift Receipt Flag
                 */
                if (giftReceiptStr.equals("1"))
                {
                    lineItem.setGiftReceiptItem(true);
                }

                /**
                 * Price Adjustment Flags
                 */
                lineItem.setPriceAdjustmentReference(priceAdjReferenceID);
                lineItem.setIsPriceAdjustmentLineItem(isPriceAdjLineItem);

                lineItem.setItemSizeCode(sizeCode);

                if (itemVoidFlag.equals(DBConstantsIfc.TRUE))
                {
                    saleReturnLineItems.addElement(lineItem);
                }

                lineItem.setRelatedItemReturnable(returnRelatedItemFlag);
                lineItem.setRelatedItemSequenceNumber(relatedSeqNumber);
                lineItem.setRelatedItemDeleteable(deleteRelatedItemFlag);
                lineItem.setSalesAssociateModifiedFlag(saleAsscModifiedFlag);

                // Generate a pluItem from the data in the Sale Return Line Item table
                PLUItemIfc pluItem = instantiatePLUItem(productGroupID, kitCode, transaction.isTrainingMode());
                pluItem.setItemID(itemID);
                pluItem.setPosItemID(posItemID);
                pluItem.setItemSizeRequired(sizeRequiredFlag);
                pluItem.setDepartmentID(posDepartmentID);
                pluItem.setTaxable(taxable);
                pluItem.setTaxGroupID(taxGroupId);
                pluItem.setManufacturerItemUPC(manufacturerItemUPC);
                ItemClassificationIfc itemClassification = DomainGateway.getFactory().getItemClassificationInstance();
                itemClassification.setRestockingFeeFlag(restockingFeeFlag);
                itemClassification.setSerializedItem(serializedItemFlag);
                if ( externalValidationSerializedItemFlag )
                {
                    itemClassification.setSerialEntryTime(STORE_RECEIVING_SERIALIZED_CAPTURE_TIME);
                }
                else
                {
                    itemClassification.setSerialEntryTime(SALE_SERIALIZED_CAPTURE_TIME);
                }
                itemClassification.setExternalSystemCreateUIN(isPOSAllowedToCreateUIN);
                itemClassification.setPriceEntryRequired(priceEntryRequired);
                ProductGroupIfc pg = DomainGateway.getFactory().getProductGroupInstance();
                pg.setGroupID(productGroupID);
                itemClassification.setGroup(pg);
                itemClassification.setItemType(itemTypeID);
                itemClassification.setReturnEligible(returnEligible);
                itemClassification.setEmployeeDiscountAllowedFlag(employeeDiscountEligible);
                itemClassification.setDiscountEligible(discountable);
                itemClassification.setDamageDiscountEligible(damageDiscountable);
                itemClassification.setMerchandiseHierarchyGroup(merchandiseHierarchyGroupID);
                pluItem.setItemClassification(itemClassification);
                lineItem.setOnClearance(clearanceIndicator);
                pluItem.setSellingPrice(lineItem.getItemPrice().getPermanentSellingPrice());
                UnitOfMeasureIfc pluUOM = DomainGateway.getFactory().getUnitOfMeasureInstance();
                pluUOM.setUnitID(unitOfMeasureCode);
                pluItem.setUnitOfMeasure(pluUOM);
                pluItem.setRestrictiveAge(restrictiveAge);
                selectOptionalI18NPLUData(dataConnection, pluItem, localeRequestor, lineItem);
                lineItem.setPLUItem(pluItem);
            }
            rs.close();

            // Set the localized reason code for return line items
            for (SaleReturnLineItemIfc saleReturnLineItem: saleReturnLineItems)
            {
                if (saleReturnLineItem.isReturnLineItem())
                {
                    int sequenceNumber = saleReturnLineItem.getLineNumber();

                    // Get the reason code from the map
                    String reasonCode = reasonCodeMap.get(sequenceNumber);

                    // Retrieve localized reason code
                    saleReturnLineItem.getReturnItem().setReason(
                            getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier().getStoreID(),
                                    reasonCode, CodeConstantsIfc.CODE_LIST_RETURN_REASON_CODES, localeRequestor));

                  //Get the item condition code from the map
                    String itemConditionCode = itemConditionCodeMap.get(sequenceNumber);

                    //retreve localized item condition code
                    saleReturnLineItem.getReturnItem().setItemCondition(
                            getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier().getStoreID(),
                                    itemConditionCode, CodeConstantsIfc.CODE_LIST_RETURN_ITEM_CONDITION_CODES, localeRequestor));
                }
            }

            /*
             * Grab auxilliary elements
             */
            List<ItemDiscountStrategyIfc> itemDiscounts = null;

            for (SaleReturnLineItemIfc lineItem: saleReturnLineItems)
            {
                PLUItemIfc pluItem = lineItem.getPLUItem();;

                if (lineItem.isKitComponent() && pluItem.isKitComponent())
                {
                    pluItem.setItemID(lineItem.getPLUItemID());
                    ((KitComponentIfc)pluItem).setItemKitID(((KitComponentLineItemIfc)lineItem).getItemKitID());
                }

                // if gift card, find gift card number
                if (pluItem instanceof GiftCardPLUItemIfc)
                {
                    selectGiftCard(dataConnection, transaction, lineItem.getLineNumber(), (GiftCardPLUItemIfc)pluItem, lineItem);
                }

                // if alterations item, set line item alteration item flag,
                // alteration item price,
                // and alteration
                if (pluItem instanceof AlterationPLUItemIfc)
                {
                    lineItem.setAlterationItemFlag(true);
                    AlterationPLUItemIfc altItem = (AlterationPLUItemIfc)pluItem;
                    altItem.setPrice(lineItem.getSellingPrice());
                    selectAlteration(dataConnection, transaction, lineItem.getLineNumber(), altItem);
                }

                lineItem.setPLUItem(pluItem);
                lineItem.getItemTax().setTaxGroupId(pluItem.getTaxGroupID());

                if (lineItem.getReturnItem() != null)
                {
                    lineItem.getReturnItem().setPLUItem(pluItem);
                    lineItem.getReturnItem().setPrice(pluItem.getPrice());
                }
                /*
                 * See if there is a commission modifier
                 */
                int sequenceNumber = lineItem.getLineNumber();

                try
                {
                    String employeeID = selectCommissionModifier(dataConnection, transaction, sequenceNumber);
                    // Detect if the sales associate is another person than the
                    // cashier
                    if (!employeeID.equals(transaction.getCashier().getEmployeeID()))
                    {
                        ((AbstractTransactionLineItem)lineItem).setSalesAssociateModifiedFlag(true);
                    }
                    lineItem.setSalesAssociate(getEmployee(dataConnection, employeeID));
                    if(!employeeID.equals(transaction.getSalesAssociate().getEmployeeID()))
                    {
                        lineItem.setSalesAssociateModifiedAtLineItem(true);
                    }
                }
                catch (DataException de)
                {
                    // ignore
                }
                /*
                 * Add item discounts for each line item
                 */
                itemDiscounts = selectRetailPriceModifiers(dataConnection, transaction, lineItem, localeRequestor);
                itemDiscounts.addAll(selectSaleReturnPriceModifiers(dataConnection, transaction, lineItem, localeRequestor));
                lineItem.getItemPrice().setItemDiscounts(itemDiscounts.toArray(new ItemDiscountStrategyIfc[0]));

                /*
                 * See if there is an item tax entry
                 */
                ItemTaxIfc tax = selectSaleReturnTaxModifier(dataConnection, transaction, lineItem, localeRequestor);

                if (tax != null)
                {
                    if (lineItem.getItemTaxMethod() == ItemTaxIfc.ITEM_TAX_EXTERNAL_RATE)
                    {
                        tax.setItemTaxAmount(lineItem.getItemPrice().getItemTaxAmount());
                        tax.setItemInclusiveTaxAmount(lineItem.getItemPrice().getItemInclusiveTaxAmount());
                    }
                    lineItem.getItemPrice().setItemTax(tax);

                    if (lineItem.getReturnItem() != null)
                    {
                        lineItem.getReturnItem().setTaxRate(tax.getDefaultRate());
                    }
                }

                // When the sale return line item record was read, the tax mode
                // was unknown.
                // The mode can be set from vales in either
                // SaleReturnTaxLineItem
                // Table or the TaxModifier Table; however, if neither is
                // available,
                // this code set it explicitly based on the on item taxability.
                if (lineItem.getItemPrice().getItemTax().getTaxMode() == TAX_MODE_NOT_SET)
                {
                    if (pluItem.getTaxable())
                    {
                        lineItem.getItemPrice().getItemTax().setTaxMode(TaxIfc.TAX_MODE_STANDARD);
                    }
                    else
                    {
                        lineItem.getItemPrice().getItemTax().setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
                    }
                }

                // add external order line item information
                selectExternalOrderLineItem(dataConnection, transaction, lineItem);

                lineItem.getItemPrice().calculateItemTotal();
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing sale return line items", exc);
        }

        associateKitComponents(saleReturnLineItems);

        int numItems = saleReturnLineItems.size();
        SaleReturnLineItemIfc[] lineItems = new SaleReturnLineItemIfc[numItems];
        saleReturnLineItems.copyInto(lineItems);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectDeletedSaleReturnLineItems");

        return (lineItems);
    }

    /**
     * Reads the order delivery details, if any in the transaction.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectDeliveryDetails(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectDeliveryDetails()");

        OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
        SQLSelectStatement sql = new SQLSelectStatement();
        boolean isCrossChannelOrder = orderTransaction.containsXChannelOrderLineItem();

        // Table
        String tableName = TABLE_DELIVERY_ORDER_RECORD;
        String deliveryDetailIDColumnName = FIELD_DELIVERY_ORDER_ID;

        if (isCrossChannelOrder || transaction.isSuspended())
        {
            tableName = TABLE_TRANSACTION_ORDER_DELIVERY_DETAIL;
            deliveryDetailIDColumnName = FIELD_TRANSACTION_DELIVERY_ORDER_ID;
        }
        sql.setTable(tableName);

        // Fields
        sql.addColumn(deliveryDetailIDColumnName);
        sql.addColumn(FIELD_DELIVERY_ORDER_FIRST_NAME);
        sql.addColumn(FIELD_DELIVERY_ORDER_LASTNAME);
        sql.addColumn(FIELD_DELIVERY_ORDER_BUSINESS_NAME);
        sql.addColumn(FIELD_DELIVERY_ORDER_ADDRESS_LINE1);
        sql.addColumn(FIELD_DELIVERY_ORDER_ADDRESS_LINE2);
        sql.addColumn(FIELD_DELIVERY_ORDER_CITY);
        sql.addColumn(FIELD_DELIVERY_ORDER_COUNTRY);
        sql.addColumn(FIELD_DELIVERY_ORDER_STATE);
        sql.addColumn(FIELD_DELIVERY_ORDER_POSTAL_CODE);
        sql.addColumn(FIELD_DELIVERY_ORDER_PHONE_TYPE);
        sql.addColumn(FIELD_DELIVERY_ORDER_CONTACT_PHONE_NUMBER);
        sql.addColumn(FIELD_DELIVERY_ORDER_SPECIAL_INSTRUCTIONS);
        sql.addColumn(FIELD_DELIVERY_ORDER_DELIVERY_DATE);

        if (isCrossChannelOrder)
        {
            sql.addColumn(FIELD_DELIVERY_ORDER_SHIPPING_CARRIER);
            sql.addColumn(FIELD_DELIVERY_ORDER_SHIPPING_TYPE);
        }
        if (isCrossChannelOrder || transaction.isSuspended())
        {
            sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(orderTransaction));
            sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(orderTransaction));
            sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(orderTransaction));
            sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(orderTransaction));
        }
        else
        {
            sql.addQualifier(FIELD_ORDER_ID, inQuotes(orderTransaction.getOrderID()));
        }

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;

                // initialize order delivery detail object
                OrderDeliveryDetailIfc deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();

                deliveryDetail.setDeliveryDetailID(rs.getInt(++index));
                deliveryDetail.setFirstName(getSafeString(rs, ++index));
                deliveryDetail.setLastName(getSafeString(rs, ++index));
                deliveryDetail.setBusinessName(getSafeString(rs, ++index));
                deliveryDetail.getDeliveryAddress().addAddressLine(getSafeString(rs, ++index));
                deliveryDetail.getDeliveryAddress().addAddressLine(getSafeString(rs, ++index));
                deliveryDetail.getDeliveryAddress().setCity(getSafeString(rs, ++index));
                deliveryDetail.getDeliveryAddress().setCountry(getSafeString(rs, ++index));
                deliveryDetail.getDeliveryAddress().setState(getSafeString(rs, ++index));
                deliveryDetail.getDeliveryAddress().setPostalCode(getSafeString(rs, ++index));
                deliveryDetail.getContactPhone().setPhoneType(rs.getInt(++index));
                deliveryDetail.getContactPhone().setPhoneNumber(getSafeString(rs, ++index));
                deliveryDetail.setSpecialInstructions(getSafeString(rs, ++index));
                deliveryDetail.setDeliveryDate(getEYSDateFromString(rs, ++index));
                if (isCrossChannelOrder)
                {
                    deliveryDetail.setShippingCarrier(getSafeString(rs, ++index));
                    deliveryDetail.setShippingType(getSafeString(rs, ++index));
                }
                orderTransaction.addDeliveryDetail(deliveryDetail);
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order delivery details", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order delivery details", e);
        }

        setDeliveryDetailsOnOrderItemStatus(orderTransaction.getLineItems(), orderTransaction.getDeliveryDetails());

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectDeliveryDetails");
    }

    /**
     * This method iterates through both lists to match up the delivery details
     * with the line item and sets the delivery date on the line item.
     *
     * @param lineItems
     * @param deliveryDetails
     */
    @SuppressWarnings("unchecked")
    protected void setDeliveryDetailsOnOrderItemStatus(AbstractTransactionLineItemIfc[] lineItems,
            Collection<OrderDeliveryDetailIfc> deliveryDetails)
    {
        for (int i = 0; i < lineItems.length; i++)
        {
            if (lineItems[i] instanceof KitHeaderLineItemIfc && lineItems[i].isKitHeader())
            {
                KitHeaderLineItemIfc parentKitItem = null;
                KitComponentLineItemIfc childKit = null;
                parentKitItem = (KitHeaderLineItemIfc)lineItems[i];
                for (OrderDeliveryDetailIfc deliveryDetail : deliveryDetails)
                {
                    if (parentKitItem.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID() == deliveryDetail
                            .getDeliveryDetailID())
                    {
                        parentKitItem.getOrderItemStatus().setDeliveryDetails(deliveryDetail);
                        Iterator<KitComponentLineItemIfc> childKitItemIter = parentKitItem.getKitComponentLineItems();
                        while (childKitItemIter.hasNext())
                        {
                            childKit = childKitItemIter.next();
                            childKit.getOrderItemStatus().setDeliveryDetails(deliveryDetail);
                        }
                    }
                }

            }
            else if (lineItems[i] instanceof OrderLineItemIfc)
            {
                OrderLineItemIfc orderLineItem = (OrderLineItemIfc)lineItems[i];
                for (OrderDeliveryDetailIfc deliveryDetail : deliveryDetails)
                {
                    if (orderLineItem.getOrderItemStatus().getDeliveryDetails().getDeliveryDetailID() == deliveryDetail
                            .getDeliveryDetailID())
                    {
                        orderLineItem.getOrderItemStatus().setDeliveryDetails(deliveryDetail);
                    }
                }
            }
        }
    }

    /**
     * Reads the order receipient details, if any in the transaction.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectRecipientDetail(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectRecipientDetail()");

        OrderTransactionIfc orderTransaction = (OrderTransactionIfc)transaction;
        SQLSelectStatement sql = new SQLSelectStatement();

        // Table
        sql.setTable(TABLE_ORDER_RECIPIENT_DETAIL);

        // Fields
        sql.addColumn(FIELD_ORDER_RECIPIENT_ORDER_STATUS);
        sql.addColumn(FIELD_ORDER_RECIPIENT_ACTUAL_PICKUP_DATE);
        sql.addColumn(FIELD_ORDER_RECIPIENT_CUSTOMER_SIGNATURE);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                OrderRecipientIfc orderRecipientDetail = DomainGateway.getFactory().getOrderRecipientInstance();
                orderRecipientDetail.setOrderStatus(rs.getInt(++index));
                orderRecipientDetail.setActualPickupDate(getEYSDateFromString(rs, ++index));

                // render a StringBuffer from the Blob
                // Blob content = rs.getBlob(++index);
                try (InputStream is = rs.getBinaryStream(++index))
                {
                    StringBuffer imageData = new StringBuffer();
                    if (is != null)
                    {
                        imageData = getStringBufferFromStream(is);
                    }
                    if (imageData.toString() != null && !imageData.toString().equals("null"))
                    {
                        Point[] points = ImageUtils.getInstance().convertXYStringToPointArray(imageData.toString());
                        orderRecipientDetail.setCustomerSignature(points);
                    }
                }
                // set recipent info to the order transaction
                orderTransaction.setOrderRecipient(orderRecipientDetail);
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order recipient detail", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order recipient detail", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectRecipientDetail");
    }

    /**
     * Reads the order line item data and applies it to the line items in the
     * transaction.
     *
     * @param dataConnection a connection to the database
     * @param transaction the retail transaction
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void selectOrderLineItemsByRef(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemsByRef()");

        AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();

        for (int i = 0; i < lineItems.length; i++)
        {
            if (lineItems[i] instanceof SaleReturnLineItemIfc && ((SaleReturnLineItemIfc)lineItems[i]).getOrderID() != null)
            {
                selectOrderLineItemStatus(dataConnection, transaction, (SaleReturnLineItemIfc)lineItems[i]);
            }
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemsByRef");
    }

    /**
     * Reads the order line item data and applies it to the line item in the
     * transaction.
     * @param dataConnection a connection to the database
     * @param transaction the order transaction
     * @param saleReturnLineItem the sale return line item
     * @throws DataException
     */
    protected void selectOrderLineItemStatus(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction,
            SaleReturnLineItemIfc saleReturnLineItem) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderItemStatus()");

        SQLSelectStatement sql = new SQLSelectStatement();

        // set table
        sql.addTable(TABLE_ORDER_LINE_ITEM_STATUS, ALIAS_TRANSACTION_ORDER_LINE_ITEM);

        // get columns from TR_LTM_SLS_RTN_ORD for a cross channel order item. ORPOS is not the
        // system of record for cross channel order items. This is just a snapshot of the order item status.
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_XC_ORDER_ITEM_FLAG);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_LINE_ITEM_CAPTURED_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_LINE_ITEM_STATUS); 
        
        
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_ORDERED);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_PICKED_UP);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_SHIPPED);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_NEW);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_PENDING);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_AVAILABLE);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_CANCELLED);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_QUANTITY_RETURNED);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_STATUS);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ITEM_STATUS_PREVIOUS);       
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_STATUS_CHANGE);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_TOTAL_AMOUNT);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_COMPLETED_AMOUNT);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_CANCELLED_AMOUNT);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_RETURNED_AMOUNT);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_DEPOSIT_AMOUNT);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_LINE_ITEM_DISPOSITION_CODE);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_DELIVERY_ORDER_ID);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_PICKUP_ORDER_STORE);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_PICKUP_ORDER_DATE);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_PICKUP_ORDER_FNAME);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_PICKUP_ORDER_LNAME);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_PICKUP_ORDER_PHONE);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_PICKUP_ORDER_SHIP_TO_STORE_FLAG);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_FULFILLMENT_ORDER_ID);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_ORIGINAL_STORE_ID);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORIGINAL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);

        // get columns from OR_LTM for an internal order item. ORPOS is the system of record for
        // internal ORPOS order items.
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_ORDERED);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_PICKED_UP);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_SHIPPED);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_NEW);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_PENDING);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_AVAILABLE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_CANCELLED);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_QUANTITY_RETURNED);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_STATUS);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ITEM_STATUS_PREVIOUS);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_STATUS_CHANGE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_TOTAL_AMOUNT);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_COMPLETED_AMOUNT);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_CANCELLED_AMOUNT);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_RETURNED_AMOUNT);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_DEPOSIT_AMOUNT);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_LINE_ITEM_DISPOSITION_CODE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_DELIVERY_ORDER_ID);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_PICKUP_ORDER_STORE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_PICKUP_ORDER_DATE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_PICKUP_ORDER_FNAME);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_PICKUP_ORDER_LNAME);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_PICKUP_ORDER_PHONE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_STORE_ID);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_TRANSACTION_BUSINESS_DATE);
        sql.addColumn(ALIAS_ORDER_ITEM+"."+FIELD_ORIGINAL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);

        sql.addOuterJoinQualifier(" LEFT OUTER JOIN "+TABLE_ORDER_ITEM+" "+ALIAS_ORDER_ITEM
        		+" ON " +ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ID + " = " + ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_ID+
        		" AND " +ALIAS_ORDER_ITEM+"."+FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER + " = " + ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER+
        		" AND " +ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_STORE_ID + " = " + ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_ORIGINAL_STORE_ID+
        		" AND " +ALIAS_ORDER_ITEM+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_ORDER_ORIGINAL_WORKSTATION_ID+
        		" AND " +inQuotes(0)+ " = " + ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_XC_ORDER_ITEM_FLAG);

        sql.addQualifier(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_TRANSACTION_ORDER_LINE_ITEM+"."+FIELD_LINE_ITEM_SEQUENCE_NUMBER + " = " + saleReturnLineItem.getLineNumber());

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                // initialize order item status
                OrderItemStatusIfc orderItemStatus = DomainGateway.getFactory().getOrderItemStatusInstance();
                int index = 0;
                orderItemStatus.setCrossChannelItem(getBooleanFromString(rs, ++index));
                orderItemStatus.setExternalOrderType(transaction.getExternalOrderType());
                saleReturnLineItem.setCapturedOrderLineReference(rs.getInt(++index));
                
                //This is also called during returns and in this case the item may not be a orderlineitem
                if(saleReturnLineItem instanceof OrderLineItemIfc)
                {
                    ((OrderLineItemIfc)saleReturnLineItem).setItemStatus(rs.getInt(++index));
                }
                else
                {
                    //increment and ignore this value since not an orderline item.
                    rs.getInt(++index);
                }

                if (!orderItemStatus.isCrossChannelItem() && !transaction.isSuspended())
                {
                    // For an internal order item, retrieve columns from OR_LTM
                    // table which starts at this index
                    index = IDX_ORDER_ITM_SYSTEM_RECORD_FIELD_START;
                }

                orderItemStatus.setQuantityOrdered(getBigDecimal(rs, ++index));
                orderItemStatus.setQuantityPickedUp(getBigDecimal(rs, ++index));
                orderItemStatus.setQuantityShipped(getBigDecimal(rs, ++index));
                orderItemStatus.setQuantityNew(getBigDecimal(rs, ++index));
                orderItemStatus.setQuantityPending(getBigDecimal(rs, ++index));
                orderItemStatus.setQuantityPicked(getBigDecimal(rs, ++index));
                orderItemStatus.setQuantityCancelled(getBigDecimal(rs, ++index));
                orderItemStatus.setQuantityReturned(getBigDecimal(rs, ++index));
                orderItemStatus.getStatus().setStatus(rs.getInt(++index));
                orderItemStatus.getStatus().saveStatus();
                orderItemStatus.getStatus().setPreviousStatus(rs.getInt(++index));                
                orderItemStatus.getStatus().setLastStatusChange(getEYSDateFromString(rs, ++index));
                orderItemStatus.setOrderedAmount(getCurrencyFromDecimal(rs, ++index));
                orderItemStatus.setCompletedAmount(getCurrencyFromDecimal(rs, ++index));
                orderItemStatus.setCancelledAmount(getCurrencyFromDecimal(rs, ++index));
                orderItemStatus.setReturnedAmount(getCurrencyFromDecimal(rs, ++index));
                orderItemStatus.setDepositAmount(getCurrencyFromDecimal(rs, ++index));
                // Set the disposition code for the line item
                int dispositionCode = rs.getInt(++index);
                int deliveryDetailID = rs.getInt(++index);
                String pickupStoreID = getSafeString(rs, ++index);
                EYSDate pickupDate = getEYSDateFromString(rs, ++index);
                String pickupFName = getSafeString(rs, ++index);
                String pickupLName = getSafeString(rs, ++index);
                PhoneIfc pickupContact = DomainGateway.getFactory().getPhoneInstance();
                pickupContact.setPhoneNumber(getSafeString(rs, ++index));
                boolean shipToStoreForPickup = false;
                String fulfillmentOrderId = null;
                if (orderItemStatus.isCrossChannelItem() || transaction.isSuspended())
                {
                    shipToStoreForPickup = getBooleanFromString(rs, ++index);
                    fulfillmentOrderId = getSafeString(rs, ++index);
                }

                // get order item's originating transaction id. For a xc order created outside
                // of ORPOS, the transaction id is blank.
                TransactionIDIfc originalTransactionID =
                        DomainGateway.getFactory().getTransactionIDInstance();
                originalTransactionID.setStoreID(getSafeString(rs, ++index));
                originalTransactionID.setWorkstationID(getSafeString(rs, ++index));
                originalTransactionID.setSequenceNumber(rs.getLong(++index));
                orderItemStatus.setOriginalTransactionId(originalTransactionID);
                orderItemStatus.setOriginalBusinessDate(getEYSDateFromString(rs, ++index));
                orderItemStatus.setOriginalLineNumber(rs.getInt(++index));
                
                orderItemStatus.setItemDispositionCode(dispositionCode);
                orderItemStatus.setPickupStoreID(pickupStoreID); // a store delivery item also has a pickup store ID

                if (dispositionCode == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY)
                {
                    orderItemStatus.getDeliveryDetails().setDeliveryDetailID(deliveryDetailID);
                }
                if (dispositionCode == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP)
                {
                    orderItemStatus.setPickupDate(pickupDate);
                    orderItemStatus.setPickupFirstName(pickupFName);
                    orderItemStatus.setPickupLastName(pickupLName);
                    orderItemStatus.setPickupContact(pickupContact);
                    orderItemStatus.setShipToStoreForPickup(shipToStoreForPickup);
                }

                orderItemStatus.setFulfillmentOrderID(fulfillmentOrderId);
                saleReturnLineItem.setOrderItemStatus(orderItemStatus);
                
                // we are not saving the original order item status. Explicitly set it to null.
                // This is very important since if it is null, an order line item will not try to
                // prorate tax but instead read from the tax line items saved in the transaction.
                saleReturnLineItem.setOriginalOrderItemStatus(null); 
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order line items", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order line items", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderItemStatus");
     }

    /**
     * There three tables that are associated with discounts on orders.  If the
     * the order is an in-store order, the running totals of amount cancelled, amount
     * completed and amount returned will be read from the Order Line Item Retail Price Modifier
     * table.  This table is part of Order group of tables.
     *
     * Discounts on items are read from different tables depending on scope of the discount.
     * A snap shot of the discount totals are stored in the either the Sale Return Order
     * Retail Price Modifier table (transaction scope) or the Order Retail Price Modifier (item
     * scope) table.  These tables are part of the transaction group.
     *
     * @param dataConnection
     * @param transaction
     * @throws DataException
     */
    protected void selectOrderLineItemDiscountStatusByRef(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemDiscountStatusByRef()");

        // Get and iterate through the list of line items.
        AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();

        for (AbstractTransactionLineItemIfc lineItem: lineItems)
        {
            // If the line item is a sale return line item..
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc slri = (SaleReturnLineItemIfc)lineItem;
                if (slri.getOrderID() != null)
                {
                    // If the order is part of a cross channel order or is suspended ...
                    if (slri.getOrderItemStatus().isCrossChannelItem() || transaction.isSuspended())
                    {
                        // Just get the data from the tables in the transaction group
                        selectSaleReturnOrderItemDiscountStatusByTransType(dataConnection, transaction, slri);
                        selectSaleReturnOrderItemDiscountStatusByItemType(dataConnection, transaction, slri);
                    }
                    else
                    {
                        // Get the data from the table in the order group
                        selectOrderLineItemDiscountStatus(dataConnection, transaction, slri);
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemDiscountStatusByRef");

    }

    /**
     * Get the discount data from the Sale Return Order Price Modifier table.  This table contians
     * the item portion of discounts with a transaction scope.  There may or may not be records in
     * this table associated with the sale return line item.
     *
     * This table belongs to the transaction group of tables.
     *
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @throws DataException
     */
    protected void selectSaleReturnOrderItemDiscountStatusByTransType(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnOrderItemDiscountStatusByTransType()");

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_SALE_RETURN_ORDER_PRICE_MODIFIER);

        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT);

        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getItemSequenceNumber(lineItem));

        try
        {
            ArrayList<OrderItemDiscountStatusIfc> statusList = new ArrayList<OrderItemDiscountStatusIfc>();
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            while(rs.next())
            {
                OrderItemDiscountStatusIfc oids = DomainGateway.getFactory().getOrderItemDiscountStatusInstance();
                int index = 0;

                oids.setTotalAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setCompletedAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setCancelledAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setReturnedAmount(getCurrencyFromDecimal(rs, ++index));

                statusList.add(oids);
            }
            rs.close();
            for (OrderItemDiscountStatusIfc oids: statusList)
            {
                lineItem.getOrderItemStatus().addDiscountStatus(oids);
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order line item discount status", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order line item discount status", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnOrderItemDiscountStatusByTransType");
    }

    /**
     * Get the discount data from the Retail Order Price Modifier table.  This table contians
     * the item discounts with an item scope.  There may or may not be records in
     * this table associated with the sale return line item.
     *
     * This table belongs to the transaction group of tables.
     *
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @throws DataException
     */
    protected void selectSaleReturnOrderItemDiscountStatusByItemType(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnOrderItemDiscountStatusByItemType()");

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_RETAIL_ORDER_PRICE_MODIFIER);

        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT);

        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getItemSequenceNumber(lineItem));

        try
        {
            ArrayList<OrderItemDiscountStatusIfc> statusList = new ArrayList<OrderItemDiscountStatusIfc>();
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            while(rs.next())
            {
                OrderItemDiscountStatusIfc oids = DomainGateway.getFactory().getOrderItemDiscountStatusInstance();
                int index = 0;

                oids.setTotalAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setCompletedAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setCancelledAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setReturnedAmount(getCurrencyFromDecimal(rs, ++index));

                statusList.add(oids);
            }
            rs.close();
            for (OrderItemDiscountStatusIfc oids: statusList)
            {
                lineItem.getOrderItemStatus().addDiscountStatus(oids);
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order line item discount status", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order line item discount status", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnOrderItemDiscountStatusByItemType");
    }

    /**
     * Get the order discount amount information from the Order Line Item Retail Price Modifier
     * table.
     *
     * This is table is part of the Order group of tables.
     *
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @throws DataException
     */
    protected void selectOrderLineItemDiscountStatus(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemDiscountStatusByTransType()");

        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER);

        sql.addColumn(FIELD_TOTAL_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_COMPLETED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_CANCELLED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_RETURNED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER);

        sql.addQualifier(FIELD_ORDER_ID, inQuotes(lineItem.getOrderID()));
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + getStoreID(itemStatus.getOriginalTransactionId()) );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + getWorkstationID(itemStatus.getOriginalTransactionId()) );
        
        try
        {
            ArrayList<OrderItemDiscountStatusIfc> statusList = new ArrayList<OrderItemDiscountStatusIfc>();
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            while(rs.next())
            {
                OrderItemDiscountStatusIfc oids = DomainGateway.getFactory().getOrderItemDiscountStatusInstance();
                int index = 0;

                oids.setTotalAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setCompletedAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setCancelledAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setReturnedAmount(getCurrencyFromDecimal(rs, ++index));
                oids.setLineNumber(rs.getInt(++index));

                statusList.add(oids);
            }
            rs.close();
            for (OrderItemDiscountStatusIfc oids: statusList)
            {
                lineItem.getOrderItemStatus().addDiscountStatus(oids);
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order line item discount status", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order line item discount status", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemDiscountStatusByTransType");

    }

    /**
     * This method reads the order tax information from two tables from either
     * the Sale Return Order Line Item Tax table (Cross Channel or Suspended
     * items) or the Order Line Item Tax table (in-store) items.
     *
     * @param dataConnection
     * @param transaction
     * @throws DataException
     */
    protected void selectOrderLineItemTaxStatusByRef(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemTaxStatusByRef()");

        AbstractTransactionLineItemIfc[] lineItems = transaction.getLineItems();

        for (AbstractTransactionLineItemIfc lineItem: lineItems)
        {
            if (lineItem instanceof SaleReturnLineItemIfc)
            {
                SaleReturnLineItemIfc slri = (SaleReturnLineItemIfc)lineItem;
                if (slri.getOrderID() != null)
                {
                    if (slri.getOrderItemStatus().isCrossChannelItem() || transaction.isSuspended())
                    {
                        selectSaleReturnOrderItemTaxStatus(dataConnection, transaction, slri);
                    }
                    else
                    {
                        selectOrderLineItemTaxStatus(dataConnection, transaction, slri);
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemTaxStatusByRef");

    }

    /**
     * This method reads the order tax information from the
     * Sale Return Order Line Item Tax table (Cross Channel order items).
     *
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @throws DataException
     */
    protected void selectSaleReturnOrderItemTaxStatus(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnOrderItemTaxStatus()");

        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_SALE_RETURN_ORDER_TAX_LINE_ITEM);

        sql.addColumn(FIELD_TAX_AUTHORITY_ID);
        sql.addColumn(FIELD_TAX_GROUP_ID);
        sql.addColumn(FIELD_TAX_TYPE);
        sql.addColumn(FIELD_TOTAL_TAX_AMOUNT);
        sql.addColumn(FIELD_COMPLETED_TAX_AMOUNT);
        sql.addColumn(FIELD_CANCELLED_TAX_AMOUNT);
        sql.addColumn(FIELD_RETURNED_TAX_AMOUNT);

        sql.addQualifier(FIELD_RETAIL_STORE_ID, getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID, getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER, getItemSequenceNumber(lineItem));

        try
        {
            ArrayList<OrderItemTaxStatusIfc> statusList = new ArrayList<OrderItemTaxStatusIfc>();
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            while(rs.next())
            {
                OrderItemTaxStatusIfc oits = DomainGateway.getFactory().getOrderItemTaxStatusInstance();
                int index = 0;

                oits.setAuthorityID(rs.getInt(++index));
                oits.setTaxGroupID(rs.getInt(++index));
                oits.setTypeCode(rs.getInt(++index));
                oits.setTotalAmount(getCurrencyFromDecimal(rs, ++index));
                oits.setCompletedAmount(getCurrencyFromDecimal(rs, ++index));
                oits.setCancelledAmount(getCurrencyFromDecimal(rs, ++index));
                oits.setReturnedAmount(getCurrencyFromDecimal(rs, ++index));

                statusList.add(oits);
            }
            rs.close();
            for (OrderItemTaxStatusIfc oits: statusList)
            {
                lineItem.getOrderItemStatus().addTaxStatus(oits);
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order line item tax status", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order line item tax status", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnOrderItemTaxStatus");
    }

    /**
     * This method reads the order tax information from the Order
     * Line Item Tax table (in-store order items).
     *
     * @param dataConnection
     * @param orderTransaction
     * @param lineItem
     * @throws DataException
     */
    protected void selectOrderLineItemTaxStatus(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction,  SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemTaxStatus()");

        OrderItemStatusIfc itemStatus = lineItem.getOrderItemStatus();
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_ORDER_LINE_ITEM_TAX);

        sql.addColumn(FIELD_TAX_AUTHORITY_ID);
        sql.addColumn(FIELD_TAX_GROUP_ID);
        sql.addColumn(FIELD_TAX_TYPE);
        sql.addColumn(FIELD_TOTAL_TAX_AMOUNT);
        sql.addColumn(FIELD_COMPLETED_TAX_AMOUNT);
        sql.addColumn(FIELD_CANCELLED_TAX_AMOUNT);
        sql.addColumn(FIELD_RETURNED_TAX_AMOUNT);

        sql.addQualifier(FIELD_ORDER_ID, inQuotes(lineItem.getOrderID()));
        sql.addQualifier(FIELD_ORDER_LINE_ITEM_SEQUENCE_NUMBER, lineItem.getOrderLineReference());
        sql.addQualifier(FIELD_ORDER_ORIGINAL_STORE_ID + " = " + getStoreID(itemStatus.getOriginalTransactionId()) );
        sql.addQualifier(FIELD_ORDER_ORIGINAL_WORKSTATION_ID + " = " + getWorkstationID(itemStatus.getOriginalTransactionId()) );

        try
        {
            ArrayList<OrderItemTaxStatusIfc> statusList = new ArrayList<OrderItemTaxStatusIfc>();
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            while(rs.next())
            {
                OrderItemTaxStatusIfc oits = DomainGateway.getFactory().getOrderItemTaxStatusInstance();
                int index = 0;

                oits.setAuthorityID(rs.getInt(++index));
                oits.setTaxGroupID(rs.getInt(++index));
                oits.setTypeCode(rs.getInt(++index));
                oits.setTotalAmount(getCurrencyFromDecimal(rs, ++index));
                oits.setCompletedAmount(getCurrencyFromDecimal(rs, ++index));
                oits.setCancelledAmount(getCurrencyFromDecimal(rs, ++index));
                oits.setReturnedAmount(getCurrencyFromDecimal(rs, ++index));

                statusList.add(oits);
            }
            rs.close();
            for (OrderItemTaxStatusIfc oits: statusList)
            {
                lineItem.getOrderItemStatus().addTaxStatus(oits);
            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "error processing order line item tax status", exc);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "error processing order line item tax status", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectOrderLineItemTaxStatus");
    }

    /**
     * Associate any KitComponentLineItems in the retrieved collection with
     * their parent KitHeaderLineItem.
     *
     * @param lineItems a collection of sale return line items retrieved from
     *            the database
     */
    protected void associateKitComponents(Vector<SaleReturnLineItemIfc> lineItems)
    {
        SaleReturnLineItemIfc temp;
        for (Iterator<SaleReturnLineItemIfc> e = lineItems.iterator(); e.hasNext();)
        {
            temp = e.next();
            if (temp.isKitHeader())
            {
                KitHeaderLineItemIfc header = (KitHeaderLineItemIfc)temp;
                header.associateKitComponentLineItems(lineItems.iterator());
            }
        }
    }

    /**
     * This method instantiates the type of plu required by data read from the sale return line item table
     * @param lineItem
     * @param productGroupID
     * @param isTrainingMode
     * @return
     */
    protected PLUItemIfc instantiatePLUItem(String productGroupID, int kitCode, boolean isTrainingMode)
    {
        PLUItemIfc pluItem = null;
        if (productGroupID.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_UNKNOWN_ITEM))
        {
            pluItem = DomainGateway.getFactory().getUnknownItemInstance();
        }
        else
        if (productGroupID.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CARD))
        {
            pluItem = DomainGateway.getFactory().getGiftCardPLUItemInstance();
            ((GiftCardPLUItemIfc)pluItem).setGiftCard(DomainGateway.getFactory().getGiftCardInstance());
        }
        else
        if (productGroupID.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_ALTERATION))
        {
            pluItem = DomainGateway.getFactory().getAlterationPLUItemInstance();
            ((AlterationPLUItemIfc)pluItem).setAlteration(DomainGateway.getFactory().getAlterationInstance());
        }
        else
        if (productGroupID.equals(ProductGroupConstantsIfc.PRODUCT_GROUP_GIFT_CERTIFICATE))
        {
            pluItem = DomainGateway.getFactory().getGiftCertificateItemInstance();
            try
            {
                ((GiftCertificateItemIfc)pluItem).setNumber(pluItem.getItemID());
                ((GiftCertificateItemIfc)pluItem).setTrainingMode(isTrainingMode);
            }
            catch (Exception e)
            {
                logger.warn("Error setting certificate number on the PLU Item.", e);
            }
        }

        // If the PLUItem object is still null, check to see if it is part of kit; if so,
        // create the appropriate PLUItem object.
        if (pluItem == null)
        {
            if (kitCode == ItemKitConstantsIfc.ITEM_KIT_CODE_HEADER)
            {
                pluItem = DomainGateway.getFactory().getItemKitInstance();
            }

            if (kitCode == ItemKitConstantsIfc.ITEM_KIT_CODE_COMPONENT)
            {
                pluItem = DomainGateway.getFactory().getKitComponentInstance();
            }
        }

        // If the PLUItem object is still null, create a generic PLUItem object.
        if (pluItem == null)
        {
            pluItem = DomainGateway.getFactory().getPLUItemInstance();
        }

        return pluItem;
    }

    /**
     * Read optional I18N Item data
     * @param connection
     * @param pluItem
     * @param localeRequestor
     * @param lineItem
     */
    protected void selectOptionalI18NPLUData(JdbcDataConnection connection, PLUItemIfc pluItem,
            LocaleRequestor localeRequestor, SaleReturnLineItemIfc lineItem)
    {
        try
        {
            readLocalizedItemDescriptions(connection, pluItem, localeRequestor);
        }
        catch (DataException de)
        {
            if (!(de.getErrorCode() == DataException.NO_DATA))
            {
                logger.warn("DataException occured attempting read I18N item description.", de);
            }
        }

        // Apply the sale return line item description to the short description and to the
        // long discription if one was not found in the database.
        pluItem.setShortDescription(lineItem.getReceiptDescriptionLocale(), lineItem.getReceiptDescription());
        if (Util.isEmpty(pluItem.getDescription(lineItem.getReceiptDescriptionLocale())))
        {
            pluItem.setDescription(lineItem.getReceiptDescriptionLocale(), lineItem.getReceiptDescription());
        }
    }

    /**
     * Selects an item from the Unknown Item table.
     *
     * @param dataConnection a connection to the database
     * @param transaction the original transaction
     * @param lineItem The line item to get an unknown item for
     * @param localeRequestor the requested locales
     * @return an unknown item
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    public UnknownItemIfc selectUnknownItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            AbstractTransactionLineItemIfc lineItem, LocaleRequestor localeRequestor) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectUnknownItem()");
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_UNKNOWN_ITEM, ALIAS_UNKNOWN_ITEM);
        sql.addTable(TABLE_UNIT_OF_MEASURE, ALIAS_UNIT_OF_MEASURE);
        sql.addTable(TABLE_UNIT_OF_MEASURE_I8, ALIAS_UNIT_OF_MEASURE_I8);

        /*
         * Add Columns
         */
        sql.addColumn(ALIAS_UNKNOWN_ITEM + "." + FIELD_POS_ITEM_ID);
        sql.addColumn(ALIAS_UNKNOWN_ITEM + "." + FIELD_UNKNOWNITEM_CURRENT_SALE_UNIT_POS_RETAIL_PRICE_AMOUNT);
        // Unknown item description is not locale specific
        sql.addColumn(ALIAS_UNKNOWN_ITEM + "." + FIELD_ITEM_DESCRIPTION);
        sql.addColumn(ALIAS_UNKNOWN_ITEM + "." + FIELD_TAX_GROUP_ID);
        sql.addColumn(ALIAS_UNKNOWN_ITEM + "." + FIELD_ITEM_TAX_EXEMPT_CODE);
        sql.addColumn(ALIAS_UNKNOWN_ITEM + "." + FIELD_POS_DEPARTMENT_ID);
        sql.addColumn(ALIAS_UNKNOWN_ITEM + "." + FIELD_UNIT_OF_MEASURE_CODE);
        sql.addColumn(ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_ENGLISH_METRIC_FLAG);
        sql.addColumn(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_UNIT_OF_MEASURE_NAME);
        sql.addColumn(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_UNIT_OF_MEASURE_DESCRIPTION);
        sql.addColumn(ALIAS_UNIT_OF_MEASURE + "." + FIELD_DEFAULT_UNIT_OF_MEASURE_FLAG);
        sql.addColumn(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_LOCALE);
        sql.addColumn(FIELD_UNKNOWN_ITEM_TYPE);
        /*
         * Add Qualifier(s)
         */
        // Join Unknown Item and Unit of Measure tables
        sql.addQualifier(ALIAS_UNKNOWN_ITEM + "." + FIELD_UNIT_OF_MEASURE_CODE + " = " + ALIAS_UNIT_OF_MEASURE + "."
                + FIELD_UNIT_OF_MEASURE_CODE);
        // For the specific transaction line item
        sql.addQualifier(ALIAS_UNKNOWN_ITEM + "." + FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(ALIAS_UNKNOWN_ITEM + "." + FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(ALIAS_UNKNOWN_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = "
                + getTransactionSequenceNumber(transaction));
        sql.addQualifier(ALIAS_UNKNOWN_ITEM + "." + FIELD_BUSINESS_DAY_DATE + " = "
                + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + getItemSequenceNumber(lineItem));
        sql.addQualifier(ALIAS_UNIT_OF_MEASURE + "." + FIELD_UNIT_OF_MEASURE_CODE + " = " + ALIAS_UNIT_OF_MEASURE_I8
                + "." + FIELD_UNIT_OF_MEASURE_CODE);
        // add qualifier for locale
        sql.addQualifier(ALIAS_UNIT_OF_MEASURE_I8 + "." + FIELD_LOCALE + " "
                + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        UnknownItemIfc item = null;
        Locale locale = null;


        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();
            UnitOfMeasureIfc pluUOM = DomainGateway.getFactory().getUnitOfMeasureInstance();

            while (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String posItemID = getSafeString(rs, ++index);
                CurrencyIfc retailPrice = getCurrencyFromDecimal(rs, ++index);
                String itemDescription = getSafeString(rs, ++index);
                int taxGroupID = rs.getInt(++index);
                boolean taxable = getBooleanFromString(rs, ++index);
                String deptID = getSafeString(rs, ++index);
                String uomCode = getSafeString(rs, ++index);
                boolean isMetric = getBooleanFromString(rs, ++index);
                String uomName = getSafeString(rs, ++index);
                /* String uomDescription = getSafeString(rs, */++index;
                boolean isDefaultUOM = getBooleanFromString(rs, ++index);
                locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, ++index));
                String unknownItemType = getSafeString(rs, ++index);

                if (GIFT_CERTIFICATE.equals(unknownItemType))
                {
                    item = DomainGateway.getFactory().getGiftCertificateItemInstance();
                }
                else
                {
                    item = DomainGateway.getFactory().getUnknownItemInstance();
                }

                // Initialize Unknown Item
                item.setItemID(posItemID);
                // Unknown item description is not locale specific, but may be
                // referenced as if it was.
                Locale[] locales = getBestMatchingRequestLocales(localeRequestor);
                item.getLocalizedDescriptions().initialize(locales, itemDescription);
                item.getShortLocalizedDescriptions().initialize(locales, itemDescription);
                item.setTaxGroupID(taxGroupID);
                item.setPrice(retailPrice);
                item.setTaxable(taxable);
                item.setDepartmentID(deptID);

                // Determine if we need to make the unit of measure object for
                // this item
                // if the uom is not the default value, then make the uom
                // reference
                if (!isDefaultUOM) // not the default, make uom reference object
                {
                    pluUOM.setName(locale, uomName);
                    pluUOM.setUnitID(uomCode);
                    pluUOM.setMetric(isMetric);
                    item.setUnitOfMeasure(pluUOM);
                }

                // read unknow item department
                item.setDepartment(selectDepartmentByDeptID(dataConnection, deptID));

                // read department tax rules for the unknown item
                JdbcReadNewTaxRules taxReader = new JdbcReadNewTaxRules();
                NewTaxRuleSearchCriteria searchCriteria = new NewTaxRuleSearchCriteria(transaction.getWorkstation()
                        .getStore().getStoreID(), item.getDepartment().getTaxGroupID(),
                        NewTaxRuleSearchCriteria.SEARCH_BY_STORE);
                TaxRulesVO taxRulesVO = taxReader.readTaxRules(dataConnection, searchCriteria);
                if (item.getTaxable())
                {
                    TaxRuleIfc[] taxRules = taxRulesVO.getTaxRules(item.getDepartment().getTaxGroupID());
                    item.setTaxRules(taxRules);
                }
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "UnknownItem lookup");
            throw new DataException(DataException.SQL_ERROR, "UnknownItem lookup", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "UnknownItem lookup", e);
        }

        if (item == null)
        {
            throw new DataException(DataException.NO_DATA,
                    "No Unknown Item was found processing the result set in JdbcReadTransaction.selectUnknownItem().");
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectUnknownItem()");

        return (item);
    }

    /**
     * Returns a department by its ID.
     *
     * @param dataConnection connection to the db
     * @param departmentName The ID of the department to return
     * @return department
     * @exception DataException upon error
     */
    public DepartmentIfc selectDepartmentByDeptID(JdbcDataConnection dataConnection, String departmentID)
        throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        /*
         * Add the desired tables
         */
        sql.addTable(TABLE_POS_DEPARTMENT, ALIAS_POS_DEPARTMENT);

        /*
         * Add desired columns
         */
        sql.addColumn(FIELD_POS_DEPARTMENT_ID);
        sql.addColumn(FIELD_TAX_GROUP_ID);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(new SQLParameterValue(FIELD_POS_DEPARTMENT_ID, departmentID));
        DepartmentIfc dept = DomainGateway.getFactory().getDepartmentInstance();
        try
        {
            dataConnection.execute(sql.getSQLString(), sql.getParameterValues());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                /*
                 * Grab the fields selected from the database
                 */
                int index = 0;
                String deptID = getSafeString(rs, ++index);
                int taxGroupID = rs.getInt(++index);

                /*
                 * Fill in the department object
                 */
                dept.setDepartmentID(deptID);
                dept.setTaxGroupID(taxGroupID);
            }
            else
            {
                throw new DataException(NO_DATA, "Department Not Found");
            }

            rs.close();
        }
        catch (SQLException se)
        {
            throw new DataException(SQL_ERROR, "selectDepartmentByID", se);
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(UNKNOWN, "selectDepartmentByID", e);
        }
        return(dept);
    }

    /**
     * Reads from the commission modifier table.
     *
     * @param dataConnection the connection to the data source
     * @param transaction the transaction
     * @param lineItemSequenceNumber an integer that is a foriegn key for all
     *            these records
     * @return the employee ID
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected String selectCommissionModifier(JdbcDataConnection dataConnection, TransactionIfc transaction,
            int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectCommissionModifier()");
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_COMMISSION_MODIFIER);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_EMPLOYEE_ID);
        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItemSequenceNumber);
        sql.addQualifier(FIELD_COMMISSION_MODIFIER_SEQUENCE_NUMBER + " = 0");
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        String employeeID = null;

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                employeeID = rs.getString(++index);
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectCommissionModifier", exc);
        }

        if (employeeID == null)
        {
            throw new DataException(DataException.NO_DATA, "No commission modifiers found");
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectCommissionModifier()");

        return (employeeID);
    }

    /**
     * Reads from the retail price modifier table.
     *
     * @param dataConnection the connection to the data source
     * @param transaction the transaction coming from business logic
     * @param lineItem the sale/return line item
     * @param localeRequestor the requested locales
     * @return Array of discount strategies
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    /**
     * @param dataConnection
     * @param transaction
     * @param lineItem
     * @param localeRequestor
     * @return
     * @throws DataException
     */
    protected List<ItemDiscountStrategyIfc> selectRetailPriceModifiers(JdbcDataConnection dataConnection,
            TransactionIfc transaction, SaleReturnLineItemIfc lineItem, LocaleRequestor localeRequestor)
            throws DataException
    {

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectRetailPriceModifiers()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_RETAIL_PRICE_MODIFIER);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_RETAIL_PRICE_EXTENDED_DISCOUNT_AMOUNT);
        sql.addColumn(FIELD_RETAIL_PRICE_USE_EXTENDED_DISCOUNT_FLAG);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_SCOPE_CODE);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DAMAGE_DISCOUNT);
        sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL);
        sql.addColumn(FIELD_ADVANCED_PRICING_RULE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_TYPE_CODE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_STOCK_LEDGER_ACCOUNTING_DISPOSITION_CODE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_EMPLOYEE_ID);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_OVERRIDE_ENTRY_METHOD_CODE);
        sql.addColumn(FIELD_PROMOTION_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID);
        sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_REFERENCE);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItem.getLineNumber());
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        /*
         * Add Ordering
         */
        sql.addOrdering(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER + " ASC");

        Vector<ItemDiscountStrategyIfc> itemDiscounts = new Vector<ItemDiscountStrategyIfc>(2);
        String reasonCodeString = "";
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                CurrencyIfc extendedDiscountAmount = getCurrencyFromDecimal(rs, ++index);
                boolean useExtendedDiscountFlag = getBooleanFromString(rs, ++index);
                int ruleID = rs.getInt(++index);
                reasonCodeString = getSafeString(rs, ++index);
                BigDecimal percent = getBigDecimal(rs, ++index);
                // CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc amount = getLongerCurrencyFromDecimal(rs, ++index);
                index = index + 1;
                int scopeCode = rs.getInt(++index);
                int methodCode = rs.getInt(++index);
                int assignmentBasis = rs.getInt(++index);
                String discountEmployeeID = getSafeString(rs, ++index);
                boolean isDamageDiscount = getBooleanFromString(rs, ++index);
                boolean isIncludedInBestDealFlag = getBooleanFromString(rs, ++index);
                boolean isAdvancedPricingRuleFlag = getBooleanFromString(rs, ++index);
                String referenceID = rs.getString(++index);
                String referenceIDCodeStr = getSafeString(rs, ++index);
                int typeCode = rs.getInt(++index);
                int accountingCode = rs.getInt(++index);
                String overrideEmployeeID = getSafeString(rs, ++index);
                int overrideEntryMethod = rs.getInt(++index);
                int promotionId = rs.getInt(++index);
                int promotionComponentId = rs.getInt(++index);
                int promotionComponentDetailId = rs.getInt(++index);
                int orderItemDiscountLineReference = rs.getInt(++index);

                LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();

                // Determine type
                if (ruleID == 0) // price override
                {
                    localizedCode = getInitializedLocalizedReasonCode(dataConnection, transaction.getTransactionIdentifier()
                            .getStoreID(), reasonCodeString, CodeConstantsIfc.CODE_LIST_PRICE_OVERRIDE_REASON_CODES,
                            localeRequestor);
                    lineItem.modifyItemPrice(amount, localizedCode);
                    if (!Util.isEmpty(overrideEmployeeID))
                    {
                        SecurityOverrideIfc override = DomainGateway.getFactory().getSecurityOverrideInstance();
                        override.setAuthorizingEmployee(overrideEmployeeID);
                        override.setEntryMethod(EntryMethod.getEntryMethod(overrideEntryMethod));
                        lineItem.getItemPrice().setPriceOverrideAuthorization(override);
                    }
                }
                else
                // item discount
                {
                    // If this flag is ture, use the extended discount amount to reconstruct the
                    // discount line item; no recalculation of the discount is performed.
                    ItemDiscountStrategyIfc itemDiscount = null;
                    if (useExtendedDiscountFlag)
                    {
                        if (scopeCode == DiscountRuleConstantsIfc.DISCOUNT_SCOPE_TRANSACTION) // return item transaction discount audit
                        {
                            itemDiscount = DomainGateway.getFactory().getReturnItemTransactionDiscountAuditInstance();
                        }
                        else // item discount audit
                        {
                            itemDiscount = DomainGateway.getFactory().getItemDiscountAuditInstance();
                            // For an item discount audit, unit discount amount is for display
                            // purpose only (on receipt and screen). The unit amount is always positive.
                            if (amount != null)
                            {
                                ((ItemDiscountAuditIfc)itemDiscount).setUnitDiscountAmount(amount.abs());
                            }
                        }
                        itemDiscount.setDiscountAmount(extendedDiscountAmount);
                        ((ItemDiscountAuditStrategyIfc)itemDiscount).setOriginalDiscountMethod(methodCode);
                        // For an item discount audit, rate is for display purpose only (on receipt and screen).
                        if (methodCode == DISCOUNT_METHOD_PERCENTAGE)
                        {
                            itemDiscount.setDiscountRate(percent.movePointLeft(2));
                        }
                    }
                    else
                    {
                        switch (methodCode)
                        {
                        case DISCOUNT_METHOD_PERCENTAGE:
                        {
                            itemDiscount = DomainGateway.getFactory().getItemDiscountByPercentageInstance();
                            itemDiscount.setDiscountRate(percent.movePointLeft(2));

                            break;
                        }
                        case DISCOUNT_METHOD_AMOUNT:
                        {
                            itemDiscount = DomainGateway.getFactory().getItemDiscountByAmountInstance();
                            itemDiscount.setDiscountAmount(amount);
                            break;
                        }
                        case DISCOUNT_METHOD_FIXED_PRICE:
                        {
                            itemDiscount = DomainGateway.getFactory().getItemDiscountByFixedPriceStrategyInstance();
                            itemDiscount.setDiscountAmount(amount);

                            break;
                        }
                        }// end switch methodCode
                        if (itemDiscount != null)
                        {
                            itemDiscount.setDiscountMethod(methodCode);
                        }
                    }

                    // ReferenceID and TypeCode
                    if (itemDiscount != null)
                    {
                        String ruleIDString = Integer.toString(ruleID);
                        itemDiscount.setRuleID(ruleIDString);

                        itemDiscount.setAssignmentBasis(assignmentBasis);
                        itemDiscount.setDiscountEmployee(discountEmployeeID);
                        setDiscountEmployeeIDOnTransaction(transaction, discountEmployeeID);
                        itemDiscount.setDamageDiscount(isDamageDiscount);
                        itemDiscount.setTypeCode(typeCode);
                        itemDiscount.setAccountingMethod(accountingCode);

                        itemDiscount.setReferenceID(referenceID);
                        if (referenceIDCodeStr == null)
                        {
                            itemDiscount.setReferenceIDCode(0);
                        }
                        else
                        {
                            for (int i = 0; i < DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE.length; i++)
                            {
                                if (referenceIDCodeStr
                                        .equalsIgnoreCase(DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[i]))
                                {
                                    itemDiscount.setReferenceIDCode(i);
                                }
                            }
                        }
                        itemDiscount.setAdvancedPricingRule(isAdvancedPricingRuleFlag);
                        if (isAdvancedPricingRuleFlag)
                        {
                            ((DiscountTargetIfc)lineItem).applyAdvancedPricingDiscount(itemDiscount);
                        }

                        itemDiscount.setIncludedInBestDeal(isIncludedInBestDealFlag);

                        String codeListType = DiscountUtility.getDiscountReasonCodeList(itemDiscount);
                        localizedCode = getLocalizedReasonCode(dataConnection, transaction
                                .getTransactionIdentifier().getStoreID(), reasonCodeString, codeListType,
                                localeRequestor, ruleIDString);

                        // discount names and reason code names are the
                        // same, so set it here .
                        if (localizedCode != null)
                        {
                            itemDiscount.setLocalizedNames(localizedCode.getText());
                        }
                        else
                        {
                            localizedCode = DomainGateway.getFactory().getLocalizedCode();
                            localizedCode.setCode(reasonCodeString);
                        }
                        itemDiscount.setReason(localizedCode);

                        // Set Temporary Price Change Promotion IDs
                        itemDiscount.setPromotionId(promotionId);
                        itemDiscount.setPromotionComponentId(promotionComponentId);
                        itemDiscount.setPromotionComponentDetailId(promotionComponentDetailId);
                        itemDiscount.setOrderItemDiscountLineReference(orderItemDiscountLineReference);

                        itemDiscounts.addElement(itemDiscount);
                    }
                    else
                    // itemDiscount == null
                    {
                        logger.error("Unknown type of itemDiscount:  reasonCode=" + reasonCodeString
                                + " percent=" + percent + " amount=" + amount + "");
                    }
                }
            }// end while (rs.next())
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectRetailPriceModifiers", exc);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectRetailPriceModifiers()");

        return itemDiscounts;
    }

    protected List<ItemDiscountStrategyIfc> selectSaleReturnPriceModifiers(JdbcDataConnection dataConnection,
            TransactionIfc transaction, SaleReturnLineItemIfc lineItem, LocaleRequestor localeRequestor)
            throws DataException
    {

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnPriceModifiers()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_SALE_RETURN_PRICE_MODIFIER);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DERIVATION_RULE_ID);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_REASON_CODE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_PERCENT);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_METHOD_CODE);
        sql.addColumn(FIELD_PRICE_DERIVATION_RULE_ASSIGNMENT_BASIS_CODE);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_EMPLOYEE_ID);
        sql.addColumn(FIELD_PCD_INCLUDED_IN_BEST_DEAL);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_DISCOUNT_REFERENCE_ID_TYPE_CODE);
        sql.addColumn(FIELD_PROMOTION_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID);
        sql.addColumn(FIELD_ORDER_LINE_ITEM_RETAIL_PRICE_MODIFIER_REFERENCE);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItem.getLineNumber());
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        /*
         * Add Ordering
         */
        sql.addOrdering(FIELD_RETAIL_PRICE_MODIFIER_SEQUENCE_NUMBER + " ASC");

        Vector<ItemDiscountStrategyIfc> itemDiscounts = new Vector<ItemDiscountStrategyIfc>(2);
        String reasonCodeString = "";
        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                int ruleID = rs.getInt(++index);
                reasonCodeString = getSafeString(rs, ++index);
                BigDecimal percent = getBigDecimal(rs, ++index);
                // CurrencyIfc amount = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc amount = getLongerCurrencyFromDecimal(rs, ++index);
                int discountMethod = rs.getInt(++index);
                int assignmentBasis = rs.getInt(++index);
                String discountEmployeeID = getSafeString(rs, ++index);
                boolean isIncludedInBestDealFlag = getBooleanFromString(rs, ++index);
                String referenceID = rs.getString(++index);
                String referenceIDCodeStr = getSafeString(rs, ++index);
                int promotionId = rs.getInt(++index);
                int promotionComponentId = rs.getInt(++index);
                int promotionComponentDetailId = rs.getInt(++index);
                int orderItemDiscountLineReference = rs.getInt(++index);

                // Determine type of discount
                ItemTransactionDiscountAuditIfc itemDiscount = DomainGateway.getFactory().getItemTransactionDiscountAuditInstance();
                itemDiscount.setDiscountAmount(amount);
                if (percent != null)
                {
                    itemDiscount.setDiscountRate(percent.movePointLeft(2));
                }
                itemDiscount.setIncludedInBestDeal(isIncludedInBestDealFlag);

                // ReferenceID and TypeCode
                String ruleIDString = Integer.toString(ruleID);
                itemDiscount.setRuleID(ruleIDString);

                String codeListType = DiscountUtility.getTransactionDiscountReasonCodeList(assignmentBasis, discountMethod);
                LocalizedCodeIfc localizedCode = getLocalizedReasonCode(dataConnection, transaction
                        .getTransactionIdentifier().getStoreID(), reasonCodeString, codeListType,
                        localeRequestor, ruleIDString);

                // discount names and reason code names are the
                // same, so set it here .
                if (localizedCode != null)
                {
                    itemDiscount.setLocalizedNames(localizedCode.getText());
                }
                else
                {
                    localizedCode = DomainGateway.getFactory().getLocalizedCode();
                    localizedCode.setCode(reasonCodeString);
                }
                itemDiscount.setReason(localizedCode);

                itemDiscount.setOriginalDiscountMethod(discountMethod);
                itemDiscount.setAssignmentBasis(assignmentBasis);
                itemDiscount.setDiscountEmployee(discountEmployeeID);
                setDiscountEmployeeIDOnTransaction(transaction, discountEmployeeID);
                itemDiscount.setReferenceID(referenceID);

                if (referenceIDCodeStr == null)
                {
                    itemDiscount.setReferenceIDCode(0);
                }
                else
                {
                    for (int i = 0; i < DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE.length; i++)
                    {
                        if (referenceIDCodeStr
                                .equalsIgnoreCase(DiscountRuleConstantsIfc.REFERENCE_ID_TYPE_CODE[i]))
                        {
                            itemDiscount.setReferenceIDCode(i);
                        }
                    }
                }

                // Set Temporary Price Change Promotion IDs
                itemDiscount.setPromotionId(promotionId);
                itemDiscount.setPromotionComponentId(promotionComponentId);
                itemDiscount.setPromotionComponentDetailId(promotionComponentDetailId);
                itemDiscount.setOrderItemDiscountLineReference(orderItemDiscountLineReference);

                itemDiscounts.addElement(itemDiscount);
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectSaleReturnPriceModifiers", exc);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectRetailPriceModifiers()");

        return itemDiscounts;
    }

    /**
     * Reads from the sale return tax modifier table.
     *
     * @param dataConnection the connection to the data source
     * @param transaction the retail transaction
     * @param lineItem the sale/return line item
     * @return sale return tax modifier
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected ItemTaxIfc selectSaleReturnTaxModifier(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc lineItem, LocaleRequestor localeRequestor)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnTaxModifier()");
        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_SALE_RETURN_TAX_MODIFIER);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_SALE_RETURN_TAX_AMOUNT);
        sql.addColumn(FIELD_SALE_RETURN_TAX_EXEMPTION_REASON_CODE);
        sql.addColumn(FIELD_TAX_TYPE_CODE);
        sql.addColumn(FIELD_TAX_PERCENT);
        sql.addColumn(FIELD_TAX_OVERRIDE_PERCENT);
        sql.addColumn(FIELD_TAX_OVERRIDE_AMOUNT);
        sql.addColumn(FIELD_TAX_SCOPE_ID);
        sql.addColumn(FIELD_TAX_GROUP_ID);
        sql.addColumn(FIELD_TAX_MODIFIER_SEQUENCE_NUMBER);

        // sql.addColumn(FIELD_TAX_METHOD_ID); - external tax mgr
        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItem.getLineNumber());
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        /*
         * Add Ordering
         */
        sql.addOrdering(FIELD_TAX_MODIFIER_SEQUENCE_NUMBER + " ASC");

        ItemTaxIfc itemTax = null;

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            TaxInformationContainerIfc taxInformationContainer = lineItem.getTaxInformationContainer();
            if (taxInformationContainer == null)
            {
                taxInformationContainer = DomainGateway.getFactory().getTaxInformationContainerInstance();
            }
            if (rs.next())
            {
                int index = 0;
                // The tax amount is now saved in longer precision to
                // resolve the rounding problem. So, use the newly
                // introduced getLongerCurrencyFromDeciaml during read
                // so that it will keep the item level precision.
                CurrencyIfc amount = getLongerCurrencyFromDecimal(rs, ++index);
                String reasonCodeString = getSafeString(rs, ++index);
                int taxMode = rs.getInt(++index);
                BigDecimal defaultPercent = getBigDecimal(rs, ++index, TAX_PERCENTAGE_SCALE);
                BigDecimal overridePercent = getBigDecimal(rs, ++index, TAX_PERCENTAGE_SCALE);
                CurrencyIfc overrideAmount = getCurrencyFromDecimal(rs, ++index);
                int taxScope = rs.getInt(++index);
                int taxGroupID = rs.getInt(++index);
                // int taxMethod = rs.getInt(++index); - external tax mgr

                itemTax = DomainGateway.getFactory().getItemTaxInstance();
                itemTax.setDefaultRate(defaultPercent.movePointLeft(2).doubleValue());
                itemTax.setOverrideRate(overridePercent.movePointLeft(2).doubleValue());
                itemTax.setOverrideAmount(overrideAmount);
                itemTax.setTaxScope(taxScope);
                boolean taxable = false;
                if (lineItem.getPLUItem() != null)
                {
                    taxable = lineItem.getPLUItem().getTaxable();
                }
                itemTax.setTaxable(taxable);
                itemTax.setTaxMode(taxMode);
                itemTax.setItemTaxAmount(amount); // external tax mgr
                itemTax.setTaxGroupId(taxGroupID);
                taxInformationContainer.setTaxScope(taxScope);
                if (taxInformationContainer.getTaxInformation() != null)
                {
                    TaxInformationIfc[] taxInformation = taxInformationContainer.getTaxInformation();
                    for (int i = 0; i < taxInformation.length; i++)
                    {
                        taxInformation[i].setTaxAmount(amount);
                        taxInformation[i].setTaxMode(taxMode);
                        taxInformation[i].setTaxGroupID(taxGroupID);
                    }
                }
                else
                {
                    TaxInformationIfc taxInformation = DomainGateway.getFactory().getTaxInformationInstance();
                    taxInformation.setTaxAmount(amount);
                    taxInformation.setTaxMode(taxMode);
                    taxInformation.setTaxGroupID(taxGroupID);
                    // Transaction Read In
                    // taxInformation.setUniqueID("TRI"+taxGroupID);
                    taxInformation.setTaxPercentage(overridePercent);
                    taxInformationContainer.addTaxInformation(taxInformation);
                }
                itemTax.setTaxInformationContainer(taxInformationContainer);
                // set default tax rules into item tax
                itemTax.setDefaultTaxRules(transaction.getTransactionTax().getDefaultTaxRules());
                String codeListType = "";
                if (itemTax.getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_AMOUNT)
                {
                    codeListType = CodeConstantsIfc.CODE_LIST_ITEM_TAX_AMOUNT_OVERRIDE_REASON_CODES;
                }
                else if (itemTax.getTaxMode() == TaxIfc.TAX_MODE_OVERRIDE_RATE)
                {
                    codeListType = CodeConstantsIfc.CODE_LIST_ITEM_TAX_RATE_OVERRIDE_REASON_CODES;
                }
                else
                {
                    codeListType = CodeConstantsIfc.CODE_LIST_ON_OFF_REASON_CODES;
                }

                LocalizedCodeIfc localizedCode = getInitializedLocalizedReasonCode(dataConnection, transaction
                        .getTransactionIdentifier().getStoreID(), reasonCodeString, codeListType, localeRequestor);
                itemTax.setReason(localizedCode);
                lineItem.setTaxChanged(true);
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectSaleReturnTaxModifier", exc);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectSaleReturnTaxModifier()");

        return (itemTax);
    }

    /**
     * Reads from the tender line items table.
     *
     * @param dataConnection the connection to the data source
     * @param transaction the retail transaction
     * @return Array of tender line items
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected TenderLineItemIfc[] selectTenderLineItems(JdbcDataConnection dataConnection, TransactionIfc transaction)
            throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectTenderLineItems()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_CURRENCY_ID); // I18N
        sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT);
        // alternate tender support
        sql.addColumn(FIELD_TENDER_LOCAL_CURRENCY_DESCRIPTION);
        sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_DESCRIPTION);
        sql.addColumn(FIELD_EXCHANGE_RATE_TO_BUY_AMOUNT);
        sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_COUNTRY_CODE);
        sql.addColumn(FIELD_TENDER_FOREIGN_CURRENCY_AMOUNT_TENDERED);
        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        // Ordering
        sql.addOrdering(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " ASC");

        Vector<TenderLineItemIfc> tenderLineItems = new Vector<TenderLineItemIfc>(2);

        try
        {
            dataConnection.execute(sql.getSQLString());
            TenderLineItemIfc tenderLineItem = null;
            int firstSequenceNumber = -1;
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                int sequenceNumber = rs.getInt(++index);
                String tenderType = getSafeString(rs, ++index);
                int currencyID = rs.getInt(++index);
                // alternate currency support
                CurrencyIfc tenderAmount = getCurrencyFromDecimal(rs, ++index);
                String localCurrencyDescription = getSafeString(rs, ++index);
                String altCurrencyDescription = getSafeString(rs, ++index);
                BigDecimal exchangeRate = getBigDecimal(rs, ++index, 4);
                CurrencyIfc altAmount = null;

                if (altCurrencyDescription.length() > 0)
                {
                    String altCountryCode = getSafeString(rs, ++index);
                    String altValue = getSafeString(rs, ++index);
                    altAmount = getAltCurrencyFromCountryCode(altValue, altCountryCode);
                    if (logger.isInfoEnabled())
                        logger.info("Local currency: "
                                + new Object[] { localCurrencyDescription + " Alternate currency: "
                                        + altCurrencyDescription + " Exchange Rate: " + exchangeRate + "Country Code: "
                                        + altCountryCode + " Val: " + altAmount.getStringValue() } + "");
                }

                tenderLineItem = TenderUtility.instantiateTenderLineItem(tenderType);
                tenderLineItem.setAmountTender(tenderAmount);
                tenderLineItem.setCurrencyID(currencyID); // I18N

                /*If change is issued for overtendered transaction,
                then a record in the TenderLineItem table with a negative amount is logged
                Negative amount is also logged for return transactions, hence excluding return transactions
                and setting property collected to false for other transactions. */
                if(transaction.getTransactionType()!= TransactionConstantsIfc.TYPE_RETURN
                    && (tenderAmount.signum() == CurrencyIfc.NEGATIVE && tenderLineItem instanceof TenderCashIfc))
                {
                    tenderLineItem.setCollected(false);
                }

                if (tenderLineItem instanceof TenderAlternateCurrencyIfc)
                {
                    if (altAmount != null)
                    {
                        ((TenderAlternateCurrencyIfc)tenderLineItem).setAlternateCurrencyTendered(altAmount);
                    }
                }

                if (firstSequenceNumber < 0)
                {
                    /*
                     * Save the value of the first sequence number because we
                     * need to know it to match up the tender specific records
                     * if there are multiples
                     */
                    firstSequenceNumber = sequenceNumber;
                }

                /*
                 * Add the line item to the vector of retrieved line items
                 */
                tenderLineItems.addElement(tenderLineItem);
            }
            rs.close();
            // For each tender line item, retrieve other information that
            // is applicable to the type of tender
            Enumeration<TenderLineItemIfc> lineItems = tenderLineItems.elements();

            int index = 0;
            while (lineItems.hasMoreElements())
            {
                tenderLineItem = lineItems.nextElement();
                int sequenceNumber = firstSequenceNumber + index;

                // Do Gift Card before Charge because it extends it.
                if (tenderLineItem instanceof TenderGiftCardIfc)
                {
                    readGiftCardTenderLineItem(dataConnection, transaction, (TenderGiftCardIfc)tenderLineItem,
                            sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderChargeIfc)
                {
                    readCreditDebitTenderLineItem(dataConnection, transaction, (TenderChargeIfc)tenderLineItem,
                            sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderCheckIfc)
                {
                    readCheckTenderLineItem(dataConnection, transaction, (TenderCheckIfc)tenderLineItem, sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderGiftCertificateIfc)
                {
                    readGiftCertificateTenderLineItem(dataConnection, transaction,
                            (TenderGiftCertificateIfc)tenderLineItem, sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderMailBankCheckIfc)
                {
                    readSendCheckTenderLineItem(dataConnection, transaction, (TenderMailBankCheckIfc)tenderLineItem,
                            sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderTravelersCheckIfc)
                {
                    readTravelersCheckTenderLineItem(dataConnection, transaction,
                            (TenderTravelersCheckIfc)tenderLineItem, sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderCouponIfc)
                {
                    readCouponTenderLineItem(dataConnection, transaction, (TenderCouponIfc)tenderLineItem,
                            sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderStoreCreditIfc)
                {
                    readStoreCreditTenderLineItem(dataConnection, transaction, (TenderStoreCreditIfc)tenderLineItem,
                            sequenceNumber);
                }
                else if (tenderLineItem instanceof TenderPurchaseOrderIfc)
                {
                    readPurchaseOrderTenderLineItem(dataConnection, transaction,
                            (TenderPurchaseOrderIfc)tenderLineItem, sequenceNumber);
                }
                else if (!(tenderLineItem instanceof TenderCashIfc))
                {
                    logger.error("don't know how to read " + "" + tenderLineItem.getClass().getName() + "");
                }
                index++;

            }
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "readTenderLineItems", exc);
        }

        int numItems = tenderLineItems.size();
        TenderLineItemIfc[] lineItems = new TenderLineItemIfc[numItems];
        tenderLineItems.copyInto(lineItems);

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTenderLineItems()");

        return (lineItems);
    }

    protected void selectRoundingTenderChangeLineItem(JdbcDataConnection dataConnection,
            TenderableTransactionIfc transaction) throws DataException
    {
        logger.debug("JdbcReadTransaction.selectRoundingTenderChangeLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_TENDER_CHANGE_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_TENDER_CHANGE_LINE_ITEM_AMOUNT);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_TENDER_TYPE_CODE, inQuotes(TenderLineItemConstantsIfc.CASH_ROUNDING_NAME));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                CurrencyIfc tenderAmount = getCurrencyFromDecimal(rs, 1);
                if (transaction instanceof OrderTransactionIfc)
                {
                    ((OrderTransactionIfc)transaction).getTenderTransactionTotals().setCashChangeRoundingAdjustment(tenderAmount);
                }
                else if (transaction instanceof LayawayTransactionIfc)
                {
                    ((LayawayTransactionIfc)transaction).getTenderTransactionTotals().setCashChangeRoundingAdjustment(tenderAmount);
                }
                else
                {
                    ((TenderableTransactionIfc)transaction).getTransactionTotals().setCashChangeRoundingAdjustment(tenderAmount);
                }
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "readTenderLineItems", exc);
        }

        logger.debug("JdbcReadTransaction.selectRoundingTenderChangeLineItem()");
    }

    /**
     * Reads from the tender line items table.
     *
     * @param dataConnection the connection to the data source
     * @param transaction the retail transaction
     * @return array of return tender data elements
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected ReturnTenderDataElementIfc[] readReturnTenders(JdbcDataConnection dataConnection,
            TransactionIfc transaction) throws DataException
    {
        logger.debug("JdbcReadTransaction.selectTenderLineItems()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_RETURN_TENDER_DATA);
        // Fields
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TENDER_MEDIA_ISSUER_ID);
        sql.addColumn(FIELD_TENDER_LINE_ITEM_AMOUNT);
        // alternate tender support
        sql.addColumn(FIELD_TENDER_ACCOUNT_NUMBER);
        sql.addColumn(FIELD_EXPIRATION_DATE);
        sql.addColumn(FIELD_AUTHORIZATION_RESPONSE);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

        ArrayList<ReturnTenderDataElementIfc> tenderLineItems = new ArrayList<ReturnTenderDataElementIfc>();

        try
        {
            dataConnection.execute(sql.getSQLString());
            ReturnTenderDataElementIfc returnTender = null;
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                int tenderType = rs.getInt(++index);
                String cardType = getSafeString(rs, ++index);
                // alternate currency support
                CurrencyIfc tenderAmount = getCurrencyFromDecimal(rs, ++index);
                String accountNumber = getSafeString(rs, ++index);
                EYSDate date = getEYSDateFromString(rs, ++index);
                String approvalCode = getSafeString(rs, ++index);

                /*
                 * Add the line item to the vector of retrieved line items
                 */
                returnTender = DomainGateway.getFactory().getReturnTenderDataElementInstance();
                returnTender.setTenderAmount(tenderAmount);
                returnTender.setTenderType(tenderType);
                returnTender.setCardType(cardType);
                returnTender.setAccountNumber(accountNumber);
                returnTender.setApprovalCode(approvalCode);
                returnTender.setExpirationDate(date);

                tenderLineItems.add(returnTender);
            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "readTenderLineItems", exc);
        }

        logger.debug("JdbcReadTransaction.readReturnTenders()");

        ReturnTenderDataElement[] result = new ReturnTenderDataElement[tenderLineItems.size()];
        return tenderLineItems.toArray(result);
    }

    /**
     * Reads localized information for the customer discounts
     *
     * @param dataConnection
     * @param customerID
     * @param locale
     * @return Customer object
     * @throws DataException
     * @throws SQLException
     * @deprecated As of 14.0, use {@link oracle.retail.stores.domain.manager.customer.CustomerManagerIfc#getCustomerByID(String, LocaleRequestor)} from the client instead.
     */
    protected CustomerIfc readCustomer(JdbcDataConnection dataConnection, String customerID, boolean isLayawayTransaction, LocaleRequestor locale)
            throws DataException, SQLException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readCustomer()");

        ARTSCustomer customer;
        // Read all the customer information
        JdbcReadCustomer customerOp = new JdbcReadCustomer();
        if(isLayawayTransaction){
            CustomerIfc customerSearchCriteria = DomainGateway.getFactory().getCustomerInstance();
            customerSearchCriteria.setCustomerLinkedWithLayaway(true);
            customerSearchCriteria.setCustomerID(customerID);
            customer = customerOp.selectCustomer(dataConnection, customerSearchCriteria);
        }else{
            customer = customerOp.selectCustomer(dataConnection, customerID);
        }


        if (customer.getPosCustomer() != null)
        {
            customer.getPosCustomer().setLocaleRequestor(locale);
            customerOp.selectContactInfo(dataConnection, customer);
            customerOp.selectAddressInfo(dataConnection, customer);
            customerOp.selectEmailInfo(dataConnection, customer);
            customerOp.selectPhoneInfo(dataConnection, customer);
            customerOp.selectGroupInfo(dataConnection, customer);
            customerOp.selectBusinessInfo(dataConnection, customer, locale);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readCustomer()");

        return (customer.getPosCustomer());
    }

    //---------------------------------------------------------------------
    /**
        Read the customer information from the database. <P>
        @param  dataConnection
        @param  customerID
        @return Customer being requested
        @exception  DataException thrown when an error occurs executing the
        against the DataConnection, or when processing the ResultSet
        @exception SQLException if an SQL error occurs
     * @deprecated As of 14.0, use {@link oracle.retail.stores.domain.manager.customer.CustomerManagerIfc#getCustomerByID(String, LocaleRequestor)} from the client instead.
    **/
    //---------------------------------------------------------------------
    protected CustomerIfc readCustomer(JdbcDataConnection dataConnection, String customerID, LocaleRequestor locale)
                                       throws DataException, SQLException
    {
        return readCustomer(dataConnection, customerID, false, locale);
    }

    /**
     * Instantiate a GiftCardIfc object
     *
     * @return GiftCardIfc
     */
    protected GiftCardIfc instantiateGiftCard()
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.instantiateGiftCard()");

        GiftCardIfc giftCard = DomainGateway.getFactory().getGiftCardInstance();

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.instantiateGiftCard()");

        return giftCard;
    }

    /**
     * Reads data from the tender credit debit table.
     *
     * @param dataConnection the connection to the database
     * @param transaction the retail transaction
     * @param lineItem tender line item
     * @param lineItemSequenceNumber the sequence number for the line item
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     * @throws IOException 
     */
    protected void readCreditDebitTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderChargeIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readCreditDebitTenderLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        String reasonCode = null;
        // Table
        sql.setTable(TABLE_CREDIT_DEBIT_CARD_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_TENDER_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_CARD_ACCOUNT_MASKED);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ACCOUNT_NUMBER_TOKEN);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CREDIT_DEBIT_CARD_ADJUDICATION_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TENDER_MEDIA_ISSUER_ID);

        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CUSTOMER_SIGNATURE_IMAGE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_TYPE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_COUNTRY);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_STATE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DEBIT_CREDIT_ID_EXPIRATION_DATE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_MESSAGE_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TIME);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_RETRIEVAL_REFERENCE_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_RESPONSE_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_ACCOUNT_DATA_SOURCE_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_PAYMENT_SERVICE_INDICATOR);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_TRANSACTION_IDENTIFICATION_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_VALIDATION_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_AUTHORIZATION_SOURCE_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ORIGINAL_HOST_TRANSACTION_REFERENCE_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER);
        sql.addColumn(FIELD_TENDER_REMAINING_PREPAID_BALANCE_AMOUNT);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_KSN_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_AFTER_PROMOTION_ACCOUNT_APR);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_AFTER_PROMOTION_ACCOUNT_APR_TYPE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DURING_PROMOTION_APR);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DURING_PROMOTION_APR_TYPE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_PROMOTION_DESCRIPTION);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_PROMOTION_DURATION);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_JOURNAL_KEY);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String tenderType = getSafeString(rs, ++index);
                String maskAccountNumber = getSafeString(rs, ++index);
                String cardToken = getSafeString(rs, ++index);
                String authorizationCode = rs.getString(++index);
                String entryMethod = rs.getString(++index);
                String issuerID = rs.getString(++index);

                /*
                 * Don't do any special processing on these, they are converted
                 * bitmaps
                 */
                StringBuffer imageData = new StringBuffer();

                // render a StringBuffer from the Blob
                // Blob content = rs.getBlob(++index);
                try (InputStream is = rs.getBinaryStream(++index))
                {
                    if (is != null)
                    {
                        imageData = getStringBufferFromStream(is);
                    }
                }
                
                String authorizationMethodCode = getSafeString(rs, ++index);
                reasonCode = getSafeString(rs, ++index);
                String idCountry = getSafeString(rs, ++index);
                String idState = getSafeString(rs, ++index);
                EYSDate idExpirationDate = timestampToEYSDate(rs, ++index);
                String authorizationSettlememntData = getSafeString(rs, ++index);
                Timestamp authorzationDateTime = rs.getTimestamp(++index);
                String messageSequence = rs.getString(++index);
                String authorzationDate = rs.getString(++index);
                String authorzationTime = rs.getString(++index);
                String retrievalReferenceNumber = rs.getString(++index);
                String authRespCode=rs.getString(++index);
                String accountDataSource = rs.getString(++index);
                String paymentServiceIndicator = rs.getString(++index);
                String transactionIdentificationNumber = rs.getString(++index);
                String validationCode = rs.getString(++index);
                String authorizationSource = rs.getString(++index);
                String hostReference = rs.getString(++index);
                String traceNumber = rs.getString(++index);
                BigDecimal prepaidBalanceAsBigDecimal = rs.getBigDecimal(++index);
                String additionalSecurityInfo = rs.getString(++index);
                String accountAPR = rs.getString(++index);
                String accountAPRType = rs.getString(++index);
                String promotionAPR = rs.getString(++index);
                String promotionAPRType = rs.getString(++index);
                String promotionDescription = rs.getString(++index);
                String promotionDuration = rs.getString(++index);
                String journalKey = rs.getString(++index);

                // convert the prepaid  balance to currency if it exists
                CurrencyIfc prepaidBalance = null;
                if(prepaidBalanceAsBigDecimal != null)
                {
                    prepaidBalance = DomainGateway.getCurrencyInstance(
                                                    DomainGateway.getBaseCurrencyType().getCountryCode(),
                                                    prepaidBalanceAsBigDecimal.toString());
                }

                EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(
                        null, maskAccountNumber, null);
                cardData.setCardName(issuerID);
                cardData.setCardType(tenderType);
                cardData.setCardNumberValid(true);
                lineItem.setEncipheredCardData(cardData);
                lineItem.setAccountNumberToken(cardToken);
                lineItem.setAuthorizationCode(authorizationCode);
                lineItem.setEntryMethod(EntryMethod.valueOf(entryMethod));
                lineItem.setCardType(issuerID);

                lineItem.setAuthorizationMethod(authorizationMethodCode);
                lineItem.setIDCountry(idCountry);
                lineItem.setIDState(idState);
                lineItem.setIDExpirationDate(idExpirationDate);
                lineItem.setSettlementData(authorizationSettlememntData);
                if (authorzationDateTime != null)
                {
                    lineItem.setAuthorizedDateTime(new EYSDate(authorzationDateTime));
                }

                if (imageData.toString() != null && !imageData.toString().equals("null"))
                {
                    Point[] points = ImageUtils.getInstance().convertXYStringToPointArray(imageData.toString());
                    lineItem.setSignatureData(points);
                }
                lineItem.setReferenceCode(messageSequence);
                lineItem.setAuthorizationDate(authorzationDate);
                lineItem.setAuthorizationTime(authorzationTime);
                lineItem.setRetrievalReferenceNumber(retrievalReferenceNumber);
                lineItem.setAuthResponseCode(authRespCode);
                lineItem.setAccountDataSource(accountDataSource);
                lineItem.setPaymentServiceIndicator(paymentServiceIndicator);
                lineItem.setTransactionIdentificationNumber(transactionIdentificationNumber);
                lineItem.setValidationCode(validationCode);
                lineItem.setAuthorizationSource(authorizationSource);
                lineItem.setHostReference(hostReference);
                lineItem.setTraceNumber(traceNumber);
                lineItem.setAuditTraceNumber(traceNumber);
                lineItem.setPrepaidRemainingBalance(prepaidBalance);
                lineItem.setAdditionalSecurityInfo(additionalSecurityInfo);
                lineItem.setAccountAPR(accountAPR);
                lineItem.setAccountAPRType(accountAPRType);
                lineItem.setPromotionAPR(promotionAPR);
                lineItem.setPromotionAPRType(promotionAPRType);
                lineItem.setPromotionDescription(promotionDescription);
                lineItem.setPromotionDuration(promotionDuration);
                lineItem.setJournalKey(journalKey);

                // read possible ICC details
                IntegratedChipCardDetailsDAOIfc dao = (IntegratedChipCardDetailsDAOIfc)BeanLocator.getPersistenceBean(IntegratedChipCardDetailsDAOIfc.DAO_BEAN_KEY);
                SimpleDateFormat format = new SimpleDateFormat(JdbcUtilities.YYYYMMDD_DATE_FORMAT_STRING);
                String businessDate = format.format(transaction.getBusinessDay().dateValue());
                IntegratedChipCardDetailsIfc iccDetails = dao.getICCDetails(transaction.getWorkstation().getStore().getStoreID(),
                        transaction.getWorkstation().getWorkstationID(), businessDate,
                        transaction.getTransactionSequenceNumber(), lineItemSequenceNumber);
                lineItem.setICCDetails(iccDetails);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readCreditDebitTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readCreditDebitTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readCreditDebitTenderLineItem", se);
        }
        catch (Exception e)
        {
            logger.error("An exception occurred reading CreditDebitTenderLineItem: ", e);
            throw new DataException(DataException.UNKNOWN, "readCreditDebitTenderLineItem", e);
        }

        // Read Localized Reason Code
        lineItem.setPersonalIDType(getInitializedLocalizedReasonCode(dataConnection, getStoreID(transaction), reasonCode,
                CodeConstantsIfc.CODE_LIST_CHECK_ID_TYPES, getLocaleRequestor(transaction)));

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readCreditDebitTenderLineItem()");
    }

    /**
     * Reads data from the tender check table.
     *
     * @param dataConnection the connection to the database
     * @param transaction the retail transaction
     * @param lineItem tender line item
     * @param lineItemSequenceNumber the sequence number for the line item
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void readCheckTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderCheckIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readCheckTenderLineItem()");

        String reasonCode = null;
        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_CHECK_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_TENDER_CHECK_AUTHORIZATION_PERSONAL_ID_REQUIRED_TYPE_CODE);
        sql.addColumn(FIELD_TENDER_CHECK_AUTHORIZATION_ENCRYPTED_PERSONAL_ID_NUMBER);
        sql.addColumn(FIELD_TENDER_CHECK_AUTHORIZATION_MASKED_PERSONAL_ID_NUMBER);
        sql.addColumn(FIELD_TENDER_CHECK_AUTHORIZATION_SEQ_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CHECK_BIRTH_DATE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CHECK_ADJUDICATION_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CHECK_BANK_ID);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_ENCRYPTED_CHECK_ACCOUNT_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_MASKED_CHECK_ACCOUNT_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CHECK_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_METHOD_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CHECK_DATA_SCANNED_OR_KEYED_CODE);
        sql.addColumn(FIELD_TENDER_CHECK_AUTHORIZATION_PERSONAL_ID_ISSUER);

        sql.addColumn(FIELD_CUSTOMER_PHONE_NUMBER);
        sql.addColumn(FIELD_TENDER_ENCRYPTED_CHECK_MICR_NUMBER);
        sql.addColumn(FIELD_TENDER_MASKED_CHECK_MICR_NUMBER);
        sql.addColumn(FIELD_TENDER_CHECK_MICR_COUNTRY_CODE);
        sql.addColumn(FIELD_TENDER_CHECK_STATE_CODE);
        sql.addColumn(FIELD_TENDER_CHECK_ID_SWIPED);
        sql.addColumn(FIELD_TENDER_CHECK_ID_TRACK2);
        sql.addColumn(FIELD_TENDER_CHECK_ID_TRACK1);
        sql.addColumn(FIELD_CONVERSION_FLAG);
        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                reasonCode = rs.getString(++index);
                String encryptedIdNumber = rs.getString(++index);
                String maskedIdNumber = rs.getString(++index);
                String authSeqNum = rs.getString(++index);                
                EYSDate dob = getEYSDateFromString(rs, ++index);
                String authorizationCode = rs.getString(++index);
                String bankID = getSafeString(rs, ++index);
                String encryptedAccountNumber = getSafeString(rs, ++index);
                String maskedAccountNumber = getSafeString(rs, ++index);
                String checkNumber = getSafeString(rs, ++index);
                String authorizationMethodCode = getSafeString(rs, ++index);
                String entryMethod = getSafeString(rs, ++index);
                String issuer = getSafeString(rs, ++index);
                String phoneNumber = getSafeString(rs, ++index);
                String encryptedMicrNumber = getSafeString(rs, ++index);
                String maskedMicrNumber = getSafeString(rs, ++index);
                int micrCountryCode = rs.getInt(++index);
                String stateCode = getSafeString(rs, ++index);
                boolean idSwiped = getBooleanFromString(rs, ++index);
                String idTrack2Data = rs.getString(++index);
                String idTrack1Data = rs.getString(++index);
                String conversionFlag = rs.getString(++index);

                lineItem.setPersonalID(FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                        encryptedIdNumber, maskedIdNumber));
                lineItem.setDateOfBirth(dob);
                lineItem.setAuthorizationCode(authorizationCode);
                lineItem.setABANumber(bankID);
                lineItem.setAccountNumberEncipheredData(FoundationObjectFactory.getFactory()
                        .createEncipheredDataInstance(encryptedAccountNumber, maskedAccountNumber));
                lineItem.setCheckNumber(checkNumber);
                lineItem.setAuthorizationMethod(authorizationMethodCode);
                lineItem.setAuthorizationSequenceNumber(authSeqNum);                
                lineItem.setEntryMethod(EntryMethod.valueOf(entryMethod));
                lineItem.setIDIssuer(issuer);
                lineItem.setPhoneNumber(phoneNumber);

                lineItem.setMICREncipheredData(FoundationObjectFactory.getFactory().createEncipheredDataInstance(
                        encryptedMicrNumber, maskedMicrNumber));
                lineItem.setMICRCountryCode(micrCountryCode);
                lineItem.setStateCode(stateCode);
                lineItem.setIDSwiped(idSwiped);

                if (idTrack2Data != null)
                {
                    lineItem.setIDTrack2Data(idTrack2Data.getBytes());
                }
                if (idTrack1Data != null)
                {
                    lineItem.setIDTrack1Data(idTrack1Data.getBytes());
                }

                lineItem.setConversionFlag(conversionFlag);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readCheckTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readCheckTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readCheckTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readCheckTenderLineItem", e);
        }

        // Read Localized Reason Code
        lineItem.setPersonalIDType(getInitializedLocalizedReasonCode(dataConnection, getStoreID(transaction), reasonCode,
                CodeConstantsIfc.CODE_LIST_CHECK_ID_TYPES, getLocaleRequestor(transaction)));

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readCheckTenderLineItem()");
    }

    /**
     * Reads data from the tender gift card table.
     *
     * @param dataConnection the connection to the database
     * @param transaction the retail transaction
     * @param lineItem tender line item
     * @param lineItemSequenceNumber the sequence number for the line item
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void readGiftCardTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderGiftCardIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readGiftCardTenderLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_GIFT_CARD_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_GIFT_CARD_SERIAL_NUMBER);
        sql.addColumn(FIELD_MASKED_GIFT_CARD_SERIAL_NUMBER);
        sql.addColumn(FIELD_CURRENCY_ID); // I18N
        sql.addColumn(FIELD_GIFT_CARD_ADJUDICATION_CODE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_CARD_NUMBER_SWIPED_OR_KEYED_CODE);
        sql.addColumn(FIELD_AUTHORIZATION_METHOD_CODE);
        sql.addColumn(FIELD_GIFT_CARD_INITIAL_BALANCE);
        sql.addColumn(FIELD_GIFT_CARD_CURRENT_BALANCE);
        sql.addColumn(FIELD_GIFT_CARD_CREDIT_FLAG);
        sql.addColumn(FIELD_GIFT_CARD_REQUEST_TYPE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_SETTLEMENT_DATA);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE_TIME);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_JOURNAL_KEY);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_MESSAGE_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_DATE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TIME);
        sql.addColumn(FIELD_GIFT_CARD_ACCOUNT_TYPE);
        sql.addColumn(FIELD_TENDER_AUTHORIZATION_TRANSACTION_TRACE_NUMBER);
        // sql.addColumn(FIELD_ISSUING_STORE_NUMBER,
        // getIssuingStoreNumber(lineItem));
        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String serialNumber = getSafeString(rs, ++index);
                String maskedGiftCardNumber = getSafeString(rs, ++index);
                EncipheredCardDataIfc cardData = FoundationObjectFactory.getFactory().createEncipheredCardDataInstance(
                        serialNumber, maskedGiftCardNumber, null);
                lineItem.setEncipheredCardData(cardData);
                int currencyID = rs.getInt(++index); // I18N
                lineItem.setAuthorizationCode(getSafeString(rs, ++index));
                lineItem.setEntryMethod(EntryMethod.valueOf(getSafeString(rs, ++index)));
                lineItem.setAuthorizationMethod(getSafeString(rs, ++index));
                CurrencyIfc initialBalance = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
                CurrencyIfc Balance = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
                lineItem.setGiftCardCredit(getBooleanFromString(rs, ++index));
                lineItem.setRequestCode(rs.getString(++index));
                lineItem.setSettlementData(getSafeString(rs, ++index));
                Timestamp authDate = rs.getTimestamp(++index);
                if (authDate != null)
                {
                    lineItem.setAuthorizedDateTime(new EYSDate(authDate));
                }
                lineItem.setJournalKey(getSafeString(rs, ++index));
                lineItem.setReferenceCode(getSafeString(rs, ++index));
                lineItem.setAuthorizationDate(getSafeString(rs, ++index));
                lineItem.setAuthorizationTime(getSafeString(rs, ++index));
                lineItem.setAccountType(getSafeString(rs, ++index));

                GiftCardIfc giftCard = instantiateGiftCard();
                giftCard.setInitialBalance(initialBalance);
                giftCard.setCurrentBalance(Balance);
                giftCard.getEncipheredCardData().setEncryptedAcctNumber(serialNumber);
                giftCard.setCurrencyID(currencyID); // I18N
                giftCard.setTraceNumber(getSafeString(rs, ++index));
                lineItem.setGiftCard(giftCard);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readGiftCardTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readGiftCardTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readGiftCardTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readGiftCardTenderLineItem", e);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readGiftCardTenderLineItem()");
    }

    /**
     * Reads data from the tender gift certificate table.
     *
     * @param dataConnection the connection to the database
     * @param transaction the retail transaction
     * @param lineItem tender line item
     * @param lineItemSequenceNumber the sequence number for the line item
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void readGiftCertificateTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderGiftCertificateIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readGiftCertificateTenderLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_GIFT_CERTIFICATE_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER);
        sql.addColumn(FIELD_ISSUING_STORE_NUMBER);
        sql.addColumn(FIELD_CURRENCY_ID); // I18N
        sql.addColumn(FIELD_GIFT_CERTIFICATE_FACE_VALUE_AMOUNT);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_FOREIGN_FACE_VALUE_AMOUNT);
        sql.addColumn(FIELD_GIFT_CERTIFICATE_SUBTENDER_TYPE);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String serialNumber = getSafeString(rs, ++index);
                String storeNumber = getSafeString(rs, ++index);
                int currencyID = rs.getInt(++index); // I18N
                CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
                String foreignAmountString = rs.getString(++index);

                if (foreignAmountString != null)
                {
                    CurrencyTypeIfc currencyType = getCurrencyType(currencyID);
                    if (currencyType != null)
                    {
                        CurrencyIfc foreignAmount = getCurrencyFromDecimal(rs, index, currencyType.getCountryCode());
                        ((TenderAlternateCurrencyIfc)lineItem).setAlternateCurrencyTendered(foreignAmount);
                        lineItem.setCertificateType(TenderLineItemIfc.CERTIFICATE_TYPE_FOREIGN);
                    }
                    else
                    {
                        throw new DataException(DataException.UNKNOWN, "currency type of id " + currencyID
                                + " not found");
                    }
                }
                String subTenderType = rs.getString(++index);

                lineItem.setFaceValueAmount(amount);
                lineItem.setGiftCertificateNumber(serialNumber);
                lineItem.setStoreNumber(storeNumber);
                lineItem.setCurrencyID(currencyID); // I18N
                lineItem.setCertificateType(subTenderType);
                readGiftCertificateDocument(dataConnection, lineItem, transaction);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readGiftCertificateTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readGiftCertificateTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readGiftCertificateTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readGiftCertificateTenderLineItem", e);
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readGiftCertificateTenderLineItem()");
    }

    /**
     * Get the document associated with the gift cetificate line item.
     * @param dataConnection
     * @param lineItem
     * @throws DataException
     */
    protected void readGiftCertificateDocument(JdbcDataConnection dataConnection,
            TenderGiftCertificateIfc lineItem, TransactionIfc transaction) throws DataException
    {
        if ((lineItem.isMallCertificateAsCheck() || 
                lineItem.isMallCertificateAsPurchaseOrder()) && 
                lineItem.getNumber().length == 0)
        {
            // In this case no document row is available in the database.
            // Build a dummy object.
            GiftCertificateDocumentIfc document = DomainGateway.getFactory().
                    getGiftCertificateDocumentInstance();
            document.setIssuingStoreID(transaction.getTransactionIdentifier().getStoreID());
            document.setIssuingWorkstationID(transaction.getTransactionIdentifier().getWorkstationID());
            document.setIssuingTransactionSeqNumber(lineItem.getLineNumber());
            document.setIssuingBusinessDate(transaction.getTransactionIdentifier().getBusinessDate());
            document.setIssuingLineItemNumber(lineItem.getLineNumber());
            document.setAmount(lineItem.getAmountTender());
            document.setStatus(TenderCertificateIfc.REDEEMED);
            document.setPreviousStatus(TenderCertificateIfc.ISSUED);
            document.setTrainingMode(transaction.isTrainingMode());
            document.setIssueDate(DomainGateway.getFactory().getEYSDateInstance());
            document.setRedeemDate(DomainGateway.getFactory().getEYSDateInstance());
            lineItem.setDocument(document);
        }
        else
        {
            JdbcLookupCertificateIssued dataOperation = new JdbcLookupCertificateIssued();
            dataOperation.lookupCertificate(dataConnection, lineItem);
        }
    }

    /**
     * Get the document associated with the gift cetificate line item.
     * @param dataConnection
     * @param lineItem
     * @throws DataException
     * @deprecated in 14.0.1; use readGiftCertificateDocument(JdbcDataConnection dataConnection, TenderGiftCertificateIfc lineItem, TransactionIfc transaction)
     */
    protected void readGiftCertificateDocument(JdbcDataConnection dataConnection,
            TenderGiftCertificateIfc lineItem) throws DataException
    {
        JdbcLookupCertificateIssued dataOperation = new JdbcLookupCertificateIssued();
        dataOperation.lookupCertificate(dataConnection, lineItem);
    }

    /**
     * Reads data from the send check tender table.
     *
     * @param dataConnection the connection to the database
     * @param transaction the retail transaction
     * @param lineItem tender line item
     * @param lineItemSequenceNumber the sequence number for the line item
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void readSendCheckTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderMailBankCheckIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readSendCheckTenderLineItem()");

        String reasonCode = null;
        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_SEND_CHECK_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_ADDRESS_LINE_1);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_ADDRESS_LINE_2);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_CITY);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_POSTAL_CODE);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_STATE);
        sql.addColumn(FIELD_SEND_CHECK_REASON_CODE);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_NAME_PREFIX);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_FIRST_NAME);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_MIDDLE_NAME);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_LAST_NAME);
        sql.addColumn(FIELD_SEND_CHECK_PAYABLE_TO_NAME_SUFFIX);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String addressLine1 = getSafeString(rs, ++index);
                String addressLine2 = getSafeString(rs, ++index);
                String city = getSafeString(rs, ++index);
                String postalCode = getSafeString(rs, ++index);
                String state = getSafeString(rs, ++index);
                reasonCode = getSafeString(rs, ++index);
                String prefix = getSafeString(rs, ++index);
                String firstName = getSafeString(rs, ++index);
                String middleName = getSafeString(rs, ++index);
                String lastName = getSafeString(rs, ++index);
                String suffix = getSafeString(rs, ++index);

                PersonNameIfc personName = instantiatePersonName();
                personName.setSalutation(prefix);
                personName.setFirstName(firstName);
                personName.setMiddleName(middleName);
                personName.setLastName(lastName);
                personName.setNameSuffix(suffix);
                lineItem.setPayeeName(personName);

                AddressIfc address = instantiateAddress();
                address.setCity(city);
                address.setState(state);
                address.setPostalCode(postalCode);
                /*
                 * Build the address lines vector. Split entries by newline
                 * characters.
                 */
                Vector<String> lines = new Vector<String>(2);
                lines.addElement(addressLine1);
                lines.addElement(addressLine2);
                address.setLines(lines);
                address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED);
                List<AddressIfc> addresses = new ArrayList<AddressIfc>();
                addresses.add(address);
                lineItem.setAddressList(addresses);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readSendCheckTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readSendCheckTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readSendCheckTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readSendCheckTenderLineItem", e);
        }

        // Read Localized Reason Code
        lineItem.setPersonalIDType(getInitializedLocalizedReasonCode(dataConnection, getStoreID(transaction), reasonCode,
                CodeConstantsIfc.CODE_LIST_CHECK_ID_TYPES, getLocaleRequestor(transaction)));

        // apply capture customer's phones since the mail bank check doesn't store them
        if (transaction.getCaptureCustomer() != null)
        {
            lineItem.setPhoneList(transaction.getCaptureCustomer().getPhoneList());
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readSendCheckTenderLineItem()");
    }

    /**
     * Reads data from the travelers check tender table.
     *
     * @param dataConnection a connection to the database
     * @param transaction
     * @param lineItem
     * @param lineItemSequenceNumber sequence number identifying the line item
     *            for a given store/register/till/business date.
     * @exception DataException upon error
     */
    protected void readTravelersCheckTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderTravelersCheckIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTravelersCheckTenderLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_TRAVELERS_CHECK_TENDER_LINE_ITEM);
        // Columns
        sql.addColumn(FIELD_TRAVELERS_CHECK_COUNT);
        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                short count = rs.getShort(++index);
                lineItem.setNumberChecks(count);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readTravelersCheckTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readTravelersCheckTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readTravelersCheckTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readTravelersCheckTenderLineItem", e);
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readTravelersCheckTenderLineItem()");
    }

    /**
     * Reads data from the coupon tender line item table.
     *
     * @param dataConnection the connection to the database
     * @param transaction the retail transaction
     * @param lineItem tender line item
     * @param lineItemSequenceNumber the sequence number for the line item
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void readCouponTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderCouponIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readCouponTenderLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(TABLE_COUPON_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_COUPON_SCAN_CODE);
        sql.addColumn(FIELD_COUPON_TYPE);
        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));
        sql.addColumn(FIELD_COUPON_KEY_ENTERED_FLAG);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String couponNumber = getSafeString(rs, ++index);
                String couponTypeCode = getSafeString(rs, ++index);
                boolean keyedEntryFlag = getBooleanFromString(rs, ++index);
                lineItem.setCouponNumber(couponNumber);
                // Traverse the Coupon Type Code array to find a match for the
                // coupon type string returned from the database. Set the coupon
                // type
                // in the domain to the appropriate int value.
                int i = -1; // -1 indicates coupon type is UNDEFINED
                String[] codes = TenderCouponIfc.COUPON_TYPE_CODE;

                for (i = 0; i < codes.length; i++)
                {
                    if (couponTypeCode.equals(codes[i]))
                    {
                        break;
                    }
                }
                lineItem.setCouponType(i);
                // set entry method
                if (keyedEntryFlag)
                {
                    lineItem.setEntryMethod(EntryMethod.Manual);
                }
                else
                {
                    lineItem.setEntryMethod(EntryMethod.Automatic);
                }
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readCouponTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readCouponTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readCouponTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readCouponTenderLineItem", e);
        }
    }

    /**
     * Reads data from the purchase order tender table.
     *
     * @param dataConnection a connection to the database
     * @param transaction
     * @param lineItem
     * @param lineItemSequenceNumber sequence number for the line item being
     *            read
     * @exception DataException upon error
     */
    protected void readPurchaseOrderTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderPurchaseOrderIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readPurchaseOrderTenderLineItem()");

        SQLSelectStatement sql = new SQLSelectStatement();
        // Table
        sql.setTable(ARTSDatabaseIfc.TABLE_PURCHASE_ORDER_TENDER_LINE_ITEM);
        // Columns
        sql.addColumn(ARTSDatabaseIfc.PURCHASE_ORDER_NUMBER);
        sql.addColumn(ARTSDatabaseIfc.PURCHASE_ORDER_AMOUNT);
        sql.addColumn(ARTSDatabaseIfc.PURCHASE_ORDER_AGENCY_NAME);
        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String poNumber = getSafeString(rs, ++index);
                CurrencyIfc poAmount = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
                String poAgencyName = getSafeString(rs, ++index);
                lineItem.setPurchaseOrderNumber(poNumber);
                lineItem.setFaceValueAmount(poAmount);
                lineItem.setAgencyName(poAgencyName);
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readPurchaseOrderTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readPurchaseOrderTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readPurchaseOrderTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readPurchaseOrderTenderLineItem", e);
        }
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.readPurchaseOrderTenderLineItem()");
    }

    /**
     * Reads data from the store credit tender line item table.
     *
     * @param dataConnection the connection to the database
     * @param transaction the retail transaction
     * @param lineItem tender line item
     * @param lineItemSequenceNumber the sequence number for the line item
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void readStoreCreditTenderLineItem(JdbcDataConnection dataConnection, TransactionIfc transaction,
            TenderStoreCreditIfc lineItem, int lineItemSequenceNumber) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        String reasonCode = null;
        // Table
        sql.setTable(TABLE_STORE_CREDIT_TENDER_LINE_ITEM);
        // Fields
        sql.addColumn(FIELD_STORE_CREDIT_ID);
        sql.addColumn(FIELD_STORE_CREDIT_BALANCE);
        sql.addColumn(FIELD_STORE_CREDIT_TENDER_STATE);
        sql.addColumn(FIELD_STORE_CREDIT_FIRST_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_LAST_NAME);
        sql.addColumn(FIELD_STORE_CREDIT_ID_TYPE);
        sql.addColumn(FIELD_CURRENCY_ID); // I18N
        sql.addColumn(FIELD_STORE_CREDIT_FOREIGN_FACE_VALUE_AMOUNT);

        // Qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = "
                + String.valueOf(lineItemSequenceNumber));

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs.next())
            {
                int index = 0;
                String storeCreditID = getSafeString(rs, ++index);
                CurrencyIfc storeCreditAmount = DomainGateway.getBaseCurrencyInstance(getSafeString(rs, ++index));
                String storeCreditState = getSafeString(rs, ++index);
                String storeCreditFirstName = getSafeString(rs, ++index);
                String storeCreditLastName = getSafeString(rs, ++index);
                reasonCode = getSafeString(rs, ++index);
                int currencyID = rs.getInt(++index); // I18N
                String foreignAmountString = getSafeString(rs, ++index);

                if (foreignAmountString != null)
                {// CR 28495
                    CurrencyTypeIfc currencyType = getCurrencyType(currencyID);
                    if (currencyType != null)
                    {
                        CurrencyIfc foreignAmount = getCurrencyFromDecimal(rs, index, currencyType.getCountryCode());
                        if (foreignAmount.getDecimalValue().compareTo(BigDecimal.ZERO) != 0)
                        {
                            ((TenderAlternateCurrencyIfc)lineItem).setAlternateCurrencyTendered(foreignAmount);
                            lineItem.setCertificateType(TenderLineItemIfc.CERTIFICATE_TYPE_FOREIGN);
                        }
                    }
                    else
                    {
                        throw new DataException(DataException.UNKNOWN, "currency type of id " + currencyID
                                + " not found");
                    }
                }
                lineItem.setStoreCreditID(storeCreditID);
                lineItem.setAmount(storeCreditAmount);
                lineItem.setState(storeCreditState);
                lineItem.setFirstName(storeCreditFirstName);
                lineItem.setLastName(storeCreditLastName);
                // lineItem.setIdType(storeCreditIdType);
                lineItem.setCurrencyID(currencyID); // I18N
                readStoreCredit(dataConnection, lineItem);
                if (lineItem.getState().equals(TenderStoreCreditIfc.ISSUE))
                {
                    lineItem.setCollected(false);
                }
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "readStoreCreditTenderLineItem");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.warn(de.toString());
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "readStoreCreditTenderLineItem");
            throw new DataException(DataException.SQL_ERROR, "readStoreCreditTenderLineItem", se);
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreCreditTenderLineItem", e);
        }

        // Read Localized Reason Code
        lineItem.setPersonalIDType(getInitializedLocalizedReasonCode(dataConnection, getStoreID(transaction), reasonCode,
                CodeConstantsIfc.CODE_LIST_MAIL_BANK_CHECK_ID_TYPES, getLocaleRequestor(transaction)));
    }

    /**
     * Read the Store Credit information from the database.
     *
     * @param dataConnection
     * @param storeCreditID
     * @return store credit information
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     * @deprecated in 14.0 use 
     */
    protected StoreCreditIfc readStoreCredit(JdbcDataConnection dataConnection, String storeCreditID, String storeID)
            throws DataException
    {
        StoreCreditIfc storeCredit = DomainGateway.getFactory().getStoreCreditInstance();
        storeCredit.setStoreCreditID(storeCreditID);
        JdbcReadStoreCredit jrsc = new JdbcReadStoreCredit();
        try
        {
            StoreCreditIfc readStoreCredit = jrsc.readStoreCredit(dataConnection, storeCredit, storeID);
            if (readStoreCredit != null)
            {
                storeCredit = readStoreCredit;
            }
        }
        catch (DataException de)
        {
            logger.error(de.toString());
            throw de;
        }
        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readStoreCredit", e);
        }

        return storeCredit;
    }

    /**
     * Read the Store Credit information from the database.
     *
     * @param dataConnection
     * @param tenderLineItem
     * @return store credit information
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected void readStoreCredit(JdbcDataConnection dataConnection, TenderStoreCreditIfc tenderLineItem)
            throws DataException
    {
        JdbcLookupStoreCredit jrsc = new JdbcLookupStoreCredit();
        jrsc.lookupStoreCredit(dataConnection, tenderLineItem);
    }

    /**
     * Instantiates a TransactionIfc object.
     *
     * @return new TransactionIfc object
     */
    static protected TransactionIfc instantiateTransaction()
    {
        return (DomainGateway.getFactory().getTransactionInstance());
    }

    /**
     * Instantiates a SaleReturnTransaction object.
     *
     * @return new SaleReturnTransaction object
     */
    static protected SaleReturnTransactionIfc instantiateSaleReturnTransaction()
    {
        return (DomainGateway.getFactory().getSaleReturnTransactionInstance());
    }

    /**
     * Instantiates a VoidTransaction object.
     *
     * @return new VoidTransaction object
     */
    static protected VoidTransactionIfc instantiateVoidTransaction()
    {
        return (DomainGateway.getFactory().getVoidTransactionInstance());
    }

    /**
     * Instantiates a VoidTransaction object.
     *
     * @return new VoidTransaction object
     */
    static protected RedeemTransactionIfc instantiateRedeemTransaction()
    {
        return (DomainGateway.getFactory().getRedeemTransactionInstance());
    }

    /**
     * Instantiates a NoSaleTransaction object.
     *
     * @return new NoSaleTransaction object
     */
    static protected NoSaleTransactionIfc instantiateNoSaleTransaction()
    {
        return (DomainGateway.getFactory().getNoSaleTransactionInstance());
    }

    /**
     * Instantiates a PaymentTransaction object.
     *
     * @return new PaymentTransaction object
     */
    static protected PaymentTransactionIfc instantiatePaymentTransaction()
    {
        return (DomainGateway.getFactory().getPaymentTransactionInstance());
    }

    //--------------------------------------------------------------------------
    /**
     * Instantiates an InstantCreditTransaction object.
     *
     * @return new InstantCreditTransaction object
     */
    //--------------------------------------------------------------------------
    static protected InstantCreditTransactionIfc instantiateInstantCreditTransaction()
    {
        return (DomainGateway.getFactory().getInstantCreditTransactionInstance());
    }

    /**
     * Instantiates a LayawayTransaction object.
     *
     * @return new LayawayTransaction object
     */
    static protected LayawayTransactionIfc instantiateLayawayTransaction()
    {
        return (DomainGateway.getFactory().getLayawayTransactionInstance());
    }

    /**
     * Instantiates a LayawayPaymentTransaction object.
     *
     * @return new LayawayPaymentTransaction object
     */
    static protected LayawayPaymentTransactionIfc instantiateLayawayPaymentTransaction()
    {
        return (DomainGateway.getFactory().getLayawayPaymentTransactionInstance());
    }

    /**
     * Instantiates a TillAdjustmentTransaction object.
     *
     * @return new TillAdjustmentTransaction object
     */
    static protected TillAdjustmentTransactionIfc instantiateTillAdjustmentTransaction()
    {
        return (DomainGateway.getFactory().getTillAdjustmentTransactionInstance());
    }

    /**
     * Instantiates an Employee object.
     *
     * @return new Employee object
     */
    static protected EmployeeIfc instantiateEmployee()
    {
        return (DomainGateway.getFactory().getEmployeeInstance());
    }

    /**
     * Instantiates a Store object.
     *
     * @return new Store object
     */
    static protected StoreIfc instantiateStore()
    {
        return (DomainGateway.getFactory().getStoreInstance());
    }

    /**
     * Instantiates a Workstation object.
     *
     * @return new Workstation object
     */
    static protected WorkstationIfc instantiateWorkstation()
    {
        return (DomainGateway.getFactory().getWorkstationInstance());
    }

    /**
     * Instantiates PersonNameIfc class.
     *
     * @return PersonNameIfc class
     */
    static protected PersonNameIfc instantiatePersonName()
    {
        return (DomainGateway.getFactory().getPersonNameInstance());
    }

    /**
     * Instantiates AddressIfc class.
     *
     * @return AddressIfc class
     */
    static protected AddressIfc instantiateAddress()
    {
        return (DomainGateway.getFactory().getAddressInstance());
    }

    /**
     * Instantiates GiftRegistry class.
     *
     * @return GiftRegistry class
     */
    static protected RegistryIDIfc instantiateGiftRegistry()
    {
        return (DomainGateway.getFactory().getRegistryIDInstance());
    }

    /**
     * @param trans The transaction to get the workstationID from
     * @return workstationID for the transaction
     */
    static protected String getWorkstationID(TransactionIfc trans)
    {
        return (getWorkstationID(trans.getWorkstation().getWorkstationID()));
    }

    /**
     * Returns SQL-formatted workstation identifier from transaction ID object.
     *
     * @param transactionID object
     * @return SQL-formatted workstation identifier
     */
    static protected String getWorkstationID(TransactionIDIfc transactionID)
    {
        return (getWorkstationID(transactionID.getWorkstationID()));
    }

    /**
     * Returns SQL-formatted workstation identifier from string.
     *
     * @param input string
     * @return SQL-formatted workstation identifier
     */
    static protected String getWorkstationID(String input)
    {
        StringBuffer sb = new StringBuffer("'");
        sb.append(input);
        sb.append("'");
        return (sb.toString());
    }

    /**
     * Returns the store ID for the transaction
     *
     * @param trans The transaction
     * @return the store ID
     */
    static protected String getStoreID(TransactionIfc trans)
    {
        return ("'" + trans.getWorkstation().getStore().getStoreID() + "'");
    }

    /**
     * Returns the SQL-formatted store ID from the transaction ID object.
     *
     * @param transactionID transaction ID object
     * @return the sql-formatted store ID
     */
    static protected String getStoreID(TransactionIDIfc transactionID)
    {
        return (getStoreID(transactionID.getStoreID()));
    }

    /**
     * Returns the store ID
     *
     * @param storeID The store ID
     * @return the store ID
     */
    static protected String getStoreID(String storeID)
    {
        return ("'" + storeID + "'");
    }

    /**
     * @param trans The transaction to get the business day of
     * @return the BusinessDay of the transaction, as a String usable by SQL.
     */
    static protected String getBusinessDayString(TransactionIfc trans)
    {
        return (dateToSQLDateString(trans.getBusinessDay()));
    }

    /**
     * Returns string value of transaction sequence number from transaction ID
     * object.
     *
     * @param transactionID transaction ID object
     * @return string value of transaction sequence number
     */
    static protected String getTransactionSequenceNumber(TransactionIDIfc transactionID)
    {
        return (String.valueOf(transactionID.getSequenceNumber()));
    }

    /**
     * Returns the transaction sequence number
     *
     * @param transaction a pos transaction
     * @return The transaction sequence number
     */
    static protected String getTransactionSequenceNumber(TransactionIfc transaction)
    {
        return (String.valueOf(transaction.getTransactionSequenceNumber()));
    }

    /**
     * Returns the line item sequence number
     *
     * @param lineItem a line item
     * @return The line item sequence number
     */
    static protected String getItemSequenceNumber(AbstractTransactionLineItemIfc lineItem)
    {
        return (String.valueOf(lineItem.getLineNumber()));
    }

    /**
     * Returns the training mode
     *
     * @param transaction a pos transaction
     * @return The training mode
     */
    static protected String getTrainingMode(TransactionIfc transaction)
    {
        String rc = "'0'";

        if (transaction.isTrainingMode())
        {
            rc = "'1'";
        }

        return (rc);
    }

    /**
     * Returns the status code.
     *
     * @param transaction a pos transaction
     * @return The status code
     */
    static protected String getStatusCode(TransactionIfc transaction)
    {
        return (Integer.toString(transaction.getTransactionStatus()));
    }

    /**
     * Returns the item ID
     *
     * @param itemID the Item ID
     * @return the item ID
     */
    static protected String getPOSItemID(String itemID)
    {
        return ("'" + itemID + "'");
    }

    /**
     * Sets original transaction on a void transaction.
     *
     * @param connection connection to database
     * @param voidTransaction void transaction read from database
     * @param localeRequestor the requested locales
     * @exception DataException thrown if error occurs
     */
    protected void setOriginalTransaction(JdbcDataConnection connection, VoidTransactionIfc voidTransaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        // set up values to read original transaction
        TransactionIfc searchTransaction = DomainGateway.getFactory().getTransactionInstance();
        // build transaction ID, set business day
        TransactionIDIfc transactionID = DomainGateway.getFactory().getTransactionIDInstance();
        transactionID.setTransactionID(voidTransaction.getOriginalRetailStoreID(), voidTransaction
                .getOriginalWorkstationID(), voidTransaction.getOriginalTransactionSequenceNumber());
        searchTransaction.initialize(transactionID);
        searchTransaction.setBusinessDay(voidTransaction.getOriginalBusinessDay());
        searchTransaction.setTrainingMode(voidTransaction.isTrainingMode());
        TransactionIfc originalTransaction = selectTransaction(connection, searchTransaction, localeRequestor);
        voidTransaction.setOriginalTransaction((TenderableTransactionIfc)originalTransaction);
    }

    /**
     * Selects no-sale transaction data.
     *
     * @param connection connection to database
     * @param transaction NoSaleTransaction object
     * @param localeRequestor the requested locales
     * @exception DataException thrown if error occurs
     */
    protected void selectNoSaleTransaction(JdbcDataConnection connection, NoSaleTransactionIfc transaction,
            LocaleRequestor localeRequestor) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();
        String reasonCode = null;

        // set table
        sql.setTable(TABLE_NO_SALE_TRANSACTION);
        // set column
        sql.addColumn(FIELD_NO_SALE_REASON_CODE);
        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(transaction.getTransactionIdentifier().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transaction.getTransactionIdentifier()
                .getSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, dateToSQLDateString(transaction.getBusinessDay()));

        try
        {
            connection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)connection.getResult();

            if (rs.next())
            {
                reasonCode = getSafeString(rs, 1);
            }
            else
            {
                logger.error("No sale transaction was not found.");
                throw new DataException(DataException.NO_DATA, "No sale transaction was not found.");
            }
            rs.close();
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "no sale transaction transaction table");
            throw new DataException(DataException.SQL_ERROR, "no sale transaction transaction table", se);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "no sale transaction transaction table", e);
        }

        // Read Localized Reason Code
        transaction.setLocalizedReasonCode(getInitializedLocalizedReasonCode(connection, transaction.getTransactionIdentifier()
                .getStoreID(), reasonCode, CodeConstantsIfc.CODE_LIST_NO_SALE_REASON_CODES, localeRequestor));
    }

    /**
     * Selects store open-close transaction data.
     *
     * @param connection connection to database
     * @param transaction StoreOpenCloseTransaction object
     * @exception DataException thrown if error occurs
     */
    protected void selectStoreOpenCloseTransaction(JdbcDataConnection connection,
            StoreOpenCloseTransactionIfc transaction) throws DataException
    {
        // build sql statement to read store open-close transaction table
        SQLSelectStatement sql = new SQLSelectStatement();
        // set table
        sql.addTable(TABLE_STORE_OPEN_CLOSE_TRANSACTION);
        // set columns
        sql.addColumn(ARTSDatabaseIfc.FIELD_OPERATOR_ID);
        sql.addColumn(ARTSDatabaseIfc.FIELD_STORE_STATUS_CODE);
        sql.addColumn(ARTSDatabaseIfc.FIELD_TRANSACTION_TIMESTAMP);
        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(transaction.getTransactionIdentifier().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transaction.getTransactionSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();

            if (rs.next())
            {
                int index = 0;
                // get data
                String operatorID = getSafeString(rs, ++index);
                int statusCode = rs.getInt(++index);
                EYSDate timestamp = timestampToEYSDate(rs, ++index);

                // look up employee
                EmployeeIfc emp = getEmployee(connection, operatorID);

                // instantiate store status, if need be
                if (transaction.getStoreStatus() == null)
                {
                    transaction.setStoreStatus(DomainGateway.getFactory().getStoreStatusInstance());
                }
                // set status code
                transaction.getStoreStatus().setStatus(statusCode);

                // set values based on transaction (open vs. close)
                if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_STORE)
                {
                    transaction.getStoreStatus().setOpenTime(timestamp);
                    transaction.getStoreStatus().setSignOnOperator(emp);
                }
                else
                {
                    transaction.getStoreStatus().setCloseTime(timestamp);
                    transaction.getStoreStatus().setSignOffOperator(emp);
                }
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "Store open-close transaction not found.");
            }
            rs.close();

            // set other attributes
            transaction.getStoreStatus().setStore(transaction.getWorkstation().getStore());
            transaction.getStoreStatus().setBusinessDate(transaction.getBusinessDay());

            // get tender media line items for safe
            FinancialCountIfc fCount = selectTenderMediaLineItems(connection, transaction);
            // read financial totals only at store close
            if (transaction.getTransactionType() == TransactionIfc.TYPE_CLOSE_STORE)
            {
                transaction.setEndingSafeCount(fCount);
                FinancialTotalsIfc totals = selectStoreFinancialTotals(connection, transaction
                        .getTransactionIdentifier().getStoreID(), transaction.getBusinessDay());
                transaction.setEndOfDayTotals(totals);
            }
            else
            {
                transaction.setStartingSafeCount(fCount);
            }
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "Error reading store open-close transaction", e);
        }

    }

    /**
     * Selects bank deposit transaction data.
     *
     * @param connection connection to database
     * @param transaction BankDepositTransaction object
     * @exception DataException thrown if error occurs
     */
    protected void selectBankDepositTransaction(JdbcDataConnection connection, BankDepositTransactionIfc transaction)
            throws DataException
    {
        try
        {
            // get tender media line items for safe
            FinancialCountIfc fCount = selectTenderMediaLineItems(connection, transaction);
            transaction.setDepositCount(fCount);
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "Error reading bank deposit transaction", e);
        }

    }

    /**
     * Selects register open-close transaction data.
     *
     * @param connection connection to database
     * @param transaction RegisterOpenCloseTransaction object
     * @exception DataException thrown if error occurs
     */
    protected void selectRegisterOpenCloseTransaction(JdbcDataConnection connection,
            RegisterOpenCloseTransactionIfc transaction) throws DataException
    {
        // build sql statement to read register open-close transaction table
        SQLSelectStatement sql = new SQLSelectStatement();
        // set table
        sql.addTable(TABLE_WORKSTATION_OPEN_CLOSE_TRANSACTION);
        // set columns
        sql.addColumn(FIELD_OPERATOR_ID);
        sql.addColumn(FIELD_WORKSTATION_TERMINAL_STATUS_CODE);
        sql.addColumn(FIELD_TRANSACTION_TIMESTAMP);
        sql.addColumn(FIELD_RECORDED_WORKSTATION_ID);
        sql.addColumn(FIELD_WORKSTATION_TRAINING_MODE_FLAG);
        sql.addColumn(FIELD_WORKSTATION_ACCOUNTABILITY);
        sql.addColumn(FIELD_WORKSTATION_TILL_FLOAT_AMOUNT);
        sql.addColumn(FIELD_WORKSTATION_COUNT_TILL_AT_RECONCILE);
        sql.addColumn(FIELD_WORKSTATION_COUNT_FLOAT_AT_OPEN);
        sql.addColumn(FIELD_WORKSTATION_COUNT_FLOAT_AT_RECONCILE);
        sql.addColumn(FIELD_WORKSTATION_COUNT_CASH_LOAN);
        sql.addColumn(FIELD_WORKSTATION_COUNT_CASH_PICKUP);
        sql.addColumn(FIELD_WORKSTATION_COUNT_CHECK_PICKUP);
        sql.addColumn(FIELD_UNIQUE_IDENTIFIER_EXTENSION);
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_WORKSTATION_TILL_RECONCILE);
        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(transaction.getTransactionIdentifier().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transaction.getTransactionSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();
            RegisterIfc register = null;

            if (rs.next())
            {
                int index = 0;
                // instantiate register, if need be
                if (transaction.getRegister() == null)
                {
                    transaction.setRegister(DomainGateway.getFactory().getRegisterInstance());
                }
                register = transaction.getRegister();
                register.setBusinessDate(transaction.getBusinessDay());
                // instantiate workstation, if need be
                if (transaction.getRegister().getWorkstation() == null)
                {
                    WorkstationIfc workstation = DomainGateway.getFactory().getWorkstationInstance();
                    StoreIfc store = DomainGateway.getFactory().getStoreInstance();
                    store.setStoreID(transaction.getTransactionIdentifier().getStoreID());
                    workstation.setStore(store);
                    workstation.setWorkstationID(transaction.getTransactionIdentifier().getWorkstationID());
                    register.setWorkstation(workstation);
                }
                // get data
                String operatorID = getSafeString(rs, ++index);
                int statusCode = rs.getInt(++index);
                EYSDate timestamp = timestampToEYSDate(rs, ++index);
                register.getWorkstation().setWorkstationID(getSafeString(rs, ++index));
                register.getWorkstation().setTrainingMode(rs.getBoolean(++index));
                register.setAccountability(Integer.parseInt(getSafeString(rs, ++index)));
                register.setTillFloatAmount(getCurrencyFromDecimal(rs, ++index));
                register.setTillCountTillAtReconcile(Integer.parseInt(getSafeString(rs, ++index)));
                register.setTillCountFloatAtOpen(Integer.parseInt(getSafeString(rs, ++index)));
                register.setTillCountFloatAtReconcile(Integer.parseInt(getSafeString(rs, ++index)));
                register.setTillCountCashLoan(Integer.parseInt(getSafeString(rs, ++index)));
                register.setTillCountCashPickup(Integer.parseInt(getSafeString(rs, ++index)));
                register.setTillCountCheckPickup(Integer.parseInt(getSafeString(rs, ++index)));
                register.setCurrentUniqueID(getSafeString(rs, ++index));
                register.setLastTransactionSequenceNumber(rs.getInt(++index));
                register.setTillReconcile(getBooleanFromString(rs, ++index));

                // look up employee
                EmployeeIfc emp = getEmployee(connection, operatorID);

                // set status code
                transaction.getRegister().setStatus(statusCode);

                // set values based on transaction (open vs. close)
                if (transaction.getTransactionType() == TransactionIfc.TYPE_OPEN_REGISTER)
                {
                    transaction.getRegister().setOpenTime(timestamp);
                    transaction.getRegister().setSignOnOperator(emp);
                }
                else
                {
                    transaction.getRegister().setCloseTime(timestamp);
                    transaction.getRegister().setSignOffOperator(emp);
                }
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "Register open-close transaction not found.");
            }
            rs.close();

            JdbcReadRegisterTotals readRegisterDataOp = new JdbcReadRegisterTotals();
            Vector<RegisterIfc> registerVector = readRegisterDataOp.selectWorkstationHistory(connection, register);
            if (registerVector != null && registerVector.size() > 0)
            {
                RegisterIfc useRegister = registerVector.firstElement();
                register.setTotals(useRegister.getTotals());
                register = selectRegisterDrawers(connection, register);
                transaction.setRegister(register);
            }

        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "Error reading register open-close transaction", e);
        }
    }

    /**
     * Selects till open-close transaction data.
     *
     * @param connection connection to database
     * @param transaction TillOpenCloseTransaction object
     * @exception DataException thrown if error occurs
     */
    protected void selectTillOpenCloseTransaction(JdbcDataConnection connection, TillOpenCloseTransactionIfc transaction)
            throws DataException
    {
        String storeID = transaction.getTransactionIdentifier().getStoreID();
        EYSDate businessDate = transaction.getBusinessDay();
        // build sql statement to read register open-close transaction table
        SQLSelectStatement sql = new SQLSelectStatement();
        // set table
        sql.addTable(TABLE_TILL_OPEN_CLOSE_TRANSACTION);
        // set columns
        sql.addColumn(FIELD_OPERATOR_ID);
        sql.addColumn(FIELD_TILL_STATUS_CODE);
        sql.addColumn(FIELD_TRANSACTION_TIMESTAMP);
        sql.addColumn(FIELD_TENDER_REPOSITORY_ID);
        // set qualifiers
        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(storeID));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transaction.getTransactionSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));

        try
        {
            // execute sql and get result set
            connection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)connection.getResult();
            TillIfc till = null;

            if (rs.next())
            {
                int index = 0;
                // instantiate till, if need be
                if (transaction.getTill() == null)
                {
                    transaction.setTill(DomainGateway.getFactory().getTillInstance());
                }
                till = transaction.getTill();
                till.setBusinessDate(transaction.getBusinessDay());
                // get data
                String operatorID = getSafeString(rs, ++index);
                int statusCode = rs.getInt(++index);
                EYSDate timestamp = timestampToEYSDate(rs, ++index);
                till.setTillID(getSafeString(rs, ++index));

                // look up employee
                EmployeeIfc emp = getEmployee(connection, operatorID);

                // set status code
                transaction.getTill().setStatus(statusCode);

                // set values based on transaction (open vs. close)
                switch (transaction.getTransactionType())
                {
                    case TransactionIfc.TYPE_OPEN_TILL:
                        transaction.getTill().setOpenTime(timestamp);
                        transaction.getTill().setSignOnOperator(emp);
                        break;

                    case TransactionIfc.TYPE_CLOSE_TILL:
                        transaction.getTill().setCloseTime(timestamp);
                        transaction.getTill().setSignOffOperator(emp);
                        break;

                    case TransactionIfc.TYPE_SUSPEND_TILL:
                        transaction.getTill().setOpenTime(timestamp);
                        transaction.getTill().setSignOnOperator(emp);
                        break;

                    case TransactionIfc.TYPE_RESUME_TILL:
                        transaction.getTill().setOpenTime(timestamp);
                        transaction.getTill().setSignOnOperator(emp);
                        break;
                }
            }
            else
            {
                throw new DataException(DataException.NO_DATA, "Till open-close transaction not found.");
            }
            rs.close();
        }
        catch (DataException de)
        {
            throw de;
        }
        catch (Exception e)
        {
            logger.error(Util.throwableToString(e));
            throw new DataException(DataException.UNKNOWN, "Error reading till open-close transaction", e);
        }

        TillIfc useTill = transaction.getTill();
        JdbcReadTillStatus readTillDataOp = new JdbcReadTillStatus();
        Vector<TillIfc> tillVector = readTillDataOp.selectTillHistory(connection, storeID, useTill.getTillID(), businessDate);

        // bwf needs to add something here for taxhistory.

        // assume first item is correct item
        if (tillVector.size() > 0)
        {
            TillIfc totalsTill = tillVector.firstElement();
            FinancialTotalsIfc totals = totalsTill.getTotals();
            useTill.setTotals(totals);
            // Set the open time here, since it is a key field when saving open
            // / close till transactions
            useTill.setOpenTime(totalsTill.getOpenTime());
            if (totals.getStartingFloatCount() != null)
            {
                transaction.setStartingFloatCount(totals.getStartingFloatCount().getEntered());
            }
            if (totals.getEndingFloatCount() != null)
            {
                transaction.setEndingFloatCount(totals.getEndingFloatCount());
            }
            if (totals.getCombinedCount() != null)
            {
                transaction.setEndingCombinedEnteredCount(totals.getCombinedCount().getEntered());
            }
        }

    }

    /**
     * Selects store status for store ID and business date.
     *
     * @param connection Jdbc data connection
     * @param storeID store identifier
     * @param businessDate business date
     * @return store status
     * @exception DataException thrown if error occurs
     */
    protected StoreStatusIfc selectStoreStatus(JdbcDataConnection connection, String storeID, EYSDate businessDate)
            throws DataException
    {
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);
        StoreStatusIfc storeStatus = DomainGateway.getFactory().getStoreStatusInstance();
        storeStatus.setStore(store);
        storeStatus.setBusinessDate(businessDate);
        // read store status and set on transaction
        JdbcReadStoreStatus storeStatusDataOp = new JdbcReadStoreStatus();
        storeStatus = storeStatusDataOp.readStoreStatus(connection, storeStatus);

        // read store safe tenders
        TenderDescriptorIfc[] tenderDescList = storeStatusDataOp.readSafeTenders(connection);
        for (int i = 0; i < tenderDescList.length; i++)
        {
            storeStatus.addSafeTenderDesc(tenderDescList[i]);
        }

        return (storeStatus);
    }

    /**
     * Selects store safe for store ID and business date.
     *
     * @param connection Jdbc data connection
     * @param storeID store identifier
     * @param businessDate business date
     * @return store status
     * @exception DataException thrown if error occurs
     */
    protected StoreSafeIfc selectStoreSafe(JdbcDataConnection connection, String storeID, EYSDate businessDate)
            throws DataException
    {
        // set up key for reading store safe
        StoreSafeIfc storeSafe = DomainGateway.getFactory().getStoreSafeInstance();
        storeSafe.setStoreID(storeID);
        storeSafe.setBusinessDay(businessDate);
        // read store safe and set on transaction
        JdbcReadStoreSafeTotals storeSafeDataOp = new JdbcReadStoreSafeTotals();
        storeSafe = storeSafeDataOp.readStoreSafeTenderHistory(connection, storeSafe);
        return (storeSafe);
    }

    /**
     * Selects financial totals for store.
     *
     * @param connection Jdbc data connection
     * @param storeID store identifier
     * @param businessDate business date
     * @return financial totals
     * @exception DataException thrown if error occurs
     */
    protected FinancialTotalsIfc selectStoreFinancialTotals(JdbcDataConnection connection, String storeID,
            EYSDate businessDate) throws DataException
    {
        JdbcReadStoreTotals storeTotalsDataOp = new JdbcReadStoreTotals();
        StoreIfc store = DomainGateway.getFactory().getStoreInstance();
        store.setStoreID(storeID);
        FinancialTotalsIfc totals = storeTotalsDataOp.readStoreTotals(connection, store, businessDate);
        return (totals);
    }

    /**
     * Selects drawer(s) for given register.
     *
     * @param connection JDBC data connection
     * @param register register on which search should be performed
     * @return register with drawer(s) added
     * @exception DataException thrown if error occurs
     */
    protected RegisterIfc selectRegisterDrawers(JdbcDataConnection connection, RegisterIfc register)
            throws DataException
    {
        JdbcReadDrawerStatus readDrawerDataOp = new JdbcReadDrawerStatus();
        readDrawerDataOp.readDrawerStatus(connection, register);

        return (register);

    }

    /**
     * Selects till for transaction.
     *
     * @param connection Jdbc data connection
     * @param storeID store identifier
     * @param tillID till identifier
     * @param businessDate business date
     * @return The till requested
     * @exception DataException thrown if error occurs
     */
    protected TillIfc selectTill(JdbcDataConnection connection, String storeID, String tillID, EYSDate businessDate)
            throws DataException
    {
        JdbcReadTillStatus readTillDataOp = new JdbcReadTillStatus();
        TillIfc till = readTillDataOp.selectTill(connection, storeID, tillID);
        till.setBusinessDate(businessDate);
        Vector<TillIfc> tillVector = readTillDataOp.selectTillHistory(connection, storeID, tillID, businessDate);
        // assume first item is correct item
        if (tillVector.size() > 0)
        {
            TillIfc totalsTill = tillVector.firstElement();
            till.setTotals(totalsTill.getTotals());
        }

        return (till);
    }

    /**
     * Read shipping information and link to the transaction.
     *
     * @param dataConnection
     * @param transaction
     * @param localeRequestor the requested locales
     * @throws DataException
     */
    protected void readTransactionShippings(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction,
            LocaleRequestor localeRequestor) throws DataException

    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_SHIPPING_RECORDS);

        sql.addColumn(FIELD_SHIPPING_RECORDS_FIRST_NAME);
        sql.addColumn(FIELD_SHIPPING_RECORDS_LAST_NAME);
        sql.addColumn(FIELD_SHIPPING_RECORDS_BUSINESS_NAME);
        sql.addColumn(FIELD_SHIPPING_RECORDS_BUSINESS_CUSTOMER);
        sql.addColumn(FIELD_SHIPPING_RECORDS_LINE1);
        sql.addColumn(FIELD_SHIPPING_RECORDS_LINE2);
        sql.addColumn(FIELD_SHIPPING_RECORDS_CITY);
        sql.addColumn(FIELD_SHIPPING_RECORDS_STATE);
        sql.addColumn(FIELD_SHIPPING_RECORDS_POSTAL_CODE);
        sql.addColumn(FIELD_SHIPPING_RECORDS_ZIP_EXT);
        sql.addColumn(FIELD_SHIPPING_RECORDS_COUNTRY);
        sql.addColumn(FIELD_PHONE_TYPE);
        sql.addColumn(FIELD_CONTACT_LOCAL_TELEPHONE_NUMBER);
        sql.addColumn(FIELD_CONTACT_EXTENSION);
        sql.addColumn(FIELD_SHIPPING_METHOD_ID);
        sql.addColumn(FIELD_SHIPPING_CARRIER);
        sql.addColumn(FIELD_SHIPPING_TYPE);
        sql.addColumn(FIELD_SHIPPING_BASE_CHARGE);
        sql.addColumn(FIELD_SHIPPING_CHARGE_RATE_BY_WEIGHT);
        sql.addColumn(FIELD_FLAT_RATE);
        sql.addColumn(FIELD_SPECIAL_INSTRUCTION);
        sql.addColumn(FIELD_SHIPPING_CHARGE);

        sql.addColumn(FIELD_EXTERNAL_SHIPPING_FLAG);

        sql.addQualifier(FIELD_RETAIL_STORE_ID, inQuotes(transaction.getWorkstation().getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID, inQuotes(transaction.getTransactionIdentifier().getWorkstationID()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER, Long.toString(transaction.getTransactionSequenceNumber()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE, getBusinessDayString(transaction));

        // NOTE: the order is important
        // later in this method the code which retrieves the shipping taxes must
        // assume shipping
        // records are in send-label-count order since the send-label-count is
        // not included in the
        // objects from this query
        sql.addOrdering(FIELD_SEND_LABEL_COUNT + " ASC");

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            CurrencyIfc totalShippingCharge = DomainGateway.getBaseCurrencyInstance(BigDecimal.ZERO);

            while (rs.next())
            {
                int index = 0;
                String firstName = getSafeString(rs, ++index);
                String lastName = getSafeString(rs, ++index);
                String businessName = getSafeString(rs, ++index);
                String isBusinessCustomer = getSafeString(rs, ++index);
                String addr1 = getSafeString(rs, ++index);
                String addr2 = getSafeString(rs, ++index);
                String city = getSafeString(rs, ++index);
                String state = getSafeString(rs, ++index);
                String postalCode = getSafeString(rs, ++index);
                String extension = getSafeString(rs, ++index);
                String country = getSafeString(rs, ++index);
                String phoneType = getSafeString(rs, ++index);
                String phoneNumber = getSafeString(rs, ++index);
                String phoneExtension = getSafeString(rs, ++index);
                String shippingMethodID = getSafeString(rs, ++index);
                /* String shippingCarrier = */ getSafeString(rs, ++index);
                /* String shippingType = */ getSafeString(rs, ++index);
                CurrencyIfc baseShippingCharge = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc weightBasedShippingCharge = getCurrencyFromDecimal(rs, ++index);
                CurrencyIfc flatRate = getCurrencyFromDecimal(rs, ++index);
                String specialInstruction = getSafeString(rs, ++index);
                CurrencyIfc calculatedShippingCharge = getCurrencyFromDecimal(rs, ++index);

                boolean externalShippingFlag = getBooleanFromString(rs, ++index);

                ShippingMethodIfc shippingMethod = DomainGateway.getFactory().getShippingMethodInstance();
                shippingMethod.setShippingMethodID((new Integer(shippingMethodID)).intValue());
                shippingMethod.setBaseShippingCharge(baseShippingCharge);
                shippingMethod.setShippingChargeRateByWeight(weightBasedShippingCharge);
                shippingMethod.setFlatRate(flatRate);
                shippingMethod.setCalculatedShippingCharge(calculatedShippingCharge);
                shippingMethod.setShippingInstructions(specialInstruction);


                // read localized shipping types and carriers
                try
                {
                    readI8ShippingMethod(dataConnection, shippingMethod, localeRequestor);
                }
                catch (DataException de)
                {
                    if (de.getErrorCode() == DataException.NO_DATA)
                    {
                        logger.info(
                                "Localized descriptions are not available for shipping method: " + shippingMethod);
                    }
                    else
                    {
                        throw de;
                    }
                }

                CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();

                if (isBusinessCustomer.equals("1"))
                { // it is a business customer
                    customer.setBusinessCustomer(true);
                }
                else
                {
                    customer.setBusinessCustomer(false);
                }

                // still possible user typed in busineses name on shipping to
                // address screen
                customer.setFirstName(firstName);
                customer.setLastName(lastName);
                customer.setCustomerName(businessName);

                AddressIfc address = DomainGateway.getFactory().getAddressInstance();
                Vector<String> addressLines = new Vector<String>(2);
                addressLines.addElement(addr1);
                addressLines.addElement(addr2);
                address.setLines(addressLines);
                address.setCity(city);
                address.setCountry(country);
                address.setPostalCode(postalCode);
                address.setPostalCodeExtension(extension);
                address.setState(state);
                address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
                PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
                phone.setPhoneNumber(phoneNumber);
                phone.setCountry(country);
                phone.setPhoneType(Integer.valueOf(phoneType).intValue());
                phone.setExtension(phoneExtension);
                customer.addPhone(phone);
                customer.addAddress(address);

                totalShippingCharge = totalShippingCharge.add(calculatedShippingCharge);
                SendPackageLineItemIfc sendPackage = transaction.addSendPackageInfo(shippingMethod, customer);//, taxAmount, inclusiveTaxAmount);
                sendPackage.setExternalSendFlag(externalShippingFlag);
            }

            rs.close();

            // loop over send packages and retrieve each send package's tax
            // information from shp_rds_sls_rtn_tx
            SendPackageLineItemIfc[] sendPackageLineItems = transaction.getSendPackages();
            for (int i = 0; i < sendPackageLineItems.length; i++)
            {
                SendPackageLineItemIfc spli = sendPackageLineItems[i];
                // assume send packages are in send-label-count order
                spli.setFromTransaction(true);

            }
        }
        catch (SQLException se)
        {
            logger.error(Util.throwableToString(se));
            throw new DataException(DataException.UNKNOWN, "Error reading store location: {0}", se);
        }
        catch (DataException de)
        {
            // no data is Ok
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }

    }

    /**
     * Read a shipping method types and carriers
     *
     * @param connection the db connection
     * @param shippingMethod the shipping method
     * @param localeRequestor the locales of the shipping method
     * @throws DataException
     */
    protected void readI8ShippingMethod(JdbcDataConnection connection, ShippingMethodIfc shippingMethod,
            LocaleRequestor localeRequestor) throws DataException
    {
        Statement stmt = null;
        try
        {
            stmt =  connection.getConnection().createStatement();
            SQLSelectStatement sql = new SQLSelectStatement();

            // Table to select from
            sql.addTable(TABLE_SHIPPING_METHOD_I8);

            // add column
            sql.addColumn(FIELD_LOCALE);
            sql.addColumn(FIELD_SHIPPING_TYPE);
            sql.addColumn(FIELD_SHIPPING_CARRIER);

            // add identifier qualifier
            sql.addQualifier(FIELD_SHIPPING_METHOD_ID, shippingMethod.getShippingMethodID());

            // add qualifier for locale
            sql.addQualifier(FIELD_LOCALE + " "
                    + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));


            // execute sql
            String sqlString = sql.getSQLString();
            stmt.execute(sqlString);
            ResultSet rs = stmt.getResultSet();

            Locale locale = null;
            // parse result set
            while (rs.next())
            {
                locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, 1));
                shippingMethod.setShippingType(locale, getSafeString(rs, 2));
                shippingMethod.setShippingCarrier(locale, getSafeString(rs, 3));
            }

        }
        catch (SQLException se)
        {
            connection.logSQLException(se, "readI8ShippingMethod");
            throw new DataException(DataException.SQL_ERROR, "readI8ShippingMethod", se);
        }

        catch (Exception e)
        {
            throw new DataException(DataException.UNKNOWN, "readI8ShippingMethod", e);
        }
        finally
        {
            if (stmt != null)
            {
                try { stmt.close(); } catch (SQLException ignore){}
            }
        }

    }


    /**
     * Reads store locations for a vector of transactions. For each transaction,
     * the workstation's store object is set with the store location, if it can
     * be found.
     *
     * @param connection jdbc data connection
     * @param transactionVector vector of transactions
     * @param localeRequestor the requested locales
     * @throws DataException if there is an error reading store locations
     */
    protected void readStoreLocations(JdbcDataConnection connection, Vector<TransactionIfc> transactionVector,
            LocaleRequestor localeRequestor) throws DataException
    {
        // use hash map to store entries read from store table
        LocalizedTextIfc locationNames;
        TransactionIfc transaction = null;
        Iterator<TransactionIfc> iter = transactionVector.iterator();
        while (iter.hasNext())
        {
            transaction = iter.next();
            locationNames = readStoreLocation(connection, transaction.getWorkstation().getStoreID(), localeRequestor);
            transaction.getWorkstation().getStore().setLocalizedLocationNames(locationNames);
        }

    }

    /**
     * Reads store location for the store ID in a given object.
     *
     * @param connection jdbc data connection
     * @param storeID store identifier
     * @param localeRequestor the requested locales
     * @return location name
     * @exception DataException thrown if non-not-found error occurs
     */
    protected LocalizedTextIfc readStoreLocation(JdbcDataConnection connection, String storeID,
            LocaleRequestor localeRequestor) throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        // add table
        sql.addTable(TABLE_RETAIL_STORE_I8);

        // add columns
        sql.addColumn(FIELD_RETAIL_STORE_LOCATION_NAME);
        sql.addColumn(FIELD_LOCALE);

        // add qualifer
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + inQuotes(storeID));
        sql.addQualifier(FIELD_LOCALE + buildINClauseString(LocaleMap.getBestMatch("", localeRequestor.getLocales())));

        LocalizedTextIfc storeLocations = new LocalizedText();
        ResultSet rs = null;
        try
        {
            connection.execute(sql.getSQLString());
            rs = (ResultSet)connection.getResult();

            // loop through result set
            if (rs.next())
            {
                String locationName = getSafeString(rs, 1);
                Locale locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, 2));
                storeLocations.putText(locale, locationName);

                while (rs.next())
                {
                    locationName = getSafeString(rs, 1);
                    locale = LocaleUtilities.getLocaleFromString(getSafeString(rs, 2));
                    storeLocations.putText(locale, locationName);
                }

                if (localeRequestor.getDefaultLocale() != null)
                {
                    storeLocations.setDefaultLocale(LocaleMap.getBestMatch(localeRequestor.getDefaultLocale()));
                }
            }
        }
        catch (SQLException se)
        {
            logger.error(Util.throwableToString(se));
            throw new DataException(DataException.UNKNOWN, "Error reading store location: {0}", se);
        }
        catch (DataException de)
        {
            // no data is Ok
            if (de.getErrorCode() != DataException.NO_DATA)
            {
                throw de;
            }
        }
        finally
        {
            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (SQLException se)
                {
                    throw new DataException(DataException.SQL_ERROR, "readStoreLocation", se);
                }
            }
        }

        return storeLocations;
    }

    /**
     * Read values from the Order table and inserts it into the order
     * transaction.
     *
     * @param dataConnection The data connection
     * @param transaction The object of OrderTransactionIfc
     * @throws DataException upon error
     */
    protected void readOrderInfo(JdbcDataConnection dataConnection, OrderTransactionIfc transaction)
            throws DataException
    {
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.addTable(TABLE_ORDER_STATUS);

        sql.addColumn(FIELD_EMAIL_ADDRESS);
        sql.addColumn(FIELD_ORDER_DESCRIPTION);
        sql.addColumn(FIELD_ORDER_TYPE_CODE);

        TransactionIDIfc transactionID = transaction.getTransactionIdentifier();
        sql.addQualifier(FIELD_RETAIL_STORE_ID,
                inQuotes(transactionID.getStoreID()));
        sql.addQualifier(FIELD_WORKSTATION_ID,
                inQuotes(transactionID.getWorkstationID()));
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE,
                dateToSQLDateString(transactionID.getBusinessDate()));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER,
                Long.toString(transactionID.getSequenceNumber()));
        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs != null)
            {
                if (rs.next())
                {
                    int index = 0;

                    String customerEmailAddr = getSafeString(rs, ++index);
                    String orderDescription = getSafeString(rs, ++index);
                    int orderType = rs.getInt(++index);

                    transaction.setOrderCustomerEmailAddress(customerEmailAddr);
                    transaction.setOrderDescription(orderDescription);
                    transaction.setOrderType(orderType);
                }
            }
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "order table");
            throw new DataException(DataException.SQL_ERROR, "order table", se);
        }
    }

    /**
     * Select Order Payment History Info
     *
     * @param dataConnection data connection
     * @param inputOrderTransaction reference
     * @return order transaction reference updated with payment history info
     * @throws DataException data exception
     */
    protected OrderTransactionIfc selectOrderPaymentHistoryInfo(JdbcDataConnection dataConnection,
            OrderTransactionIfc inputOrderTransaction) throws DataException
    {
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering selectOrderPaymentHistoryInfo");
        }
        OrderTransactionIfc updatedOrderTransaction = null;
        if (inputOrderTransaction != null && !Util.isEmpty(inputOrderTransaction.getOrderID()))
        {
            JdbcReadOrderPaymentHistoryInfo dbReadOrderPaymentHistoryInfoOperation = new JdbcReadOrderPaymentHistoryInfo();
            try
            {
                updatedOrderTransaction = dbReadOrderPaymentHistoryInfoOperation.readOrderPaymentHistoryInfo(
                        dataConnection, inputOrderTransaction);
            }
            catch (DataException de)
            {
                logger.error("" + de + "");
                throw de;
            }
            catch (Exception e)
            {
                logger.error("" + Util.throwableToString(e) + "");
                throw new DataException(DataException.UNKNOWN, "order payment history info table", e);
            }
        }
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting selectOrderPaymentHistoryInfo");
        }
        return updatedOrderTransaction;
    }

    /**
     * Get latest business day from transaction table.
     *
     * @param dataConnection jdbc data connection
     * @return String latest business day
     * @throws DataException upon error
     */
    public String getLatestBusinessDay(JdbcDataConnection dataConnection) throws DataException
    {
        String latestBSN = "";

        if (logger.isDebugEnabled())
        {
            logger.debug("JdbcReadTransaction.getLastestBSN()");
        }

        SQLSelectStatement sql = new SQLSelectStatement();

        // add tables
        sql.addTable(TABLE_TRANSACTION, ALIAS_TRANSACTION);

        // add max
        sql.addMaxFunction(FIELD_BUSINESS_DAY_DATE);

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (rs != null)
            {
                if (rs.next())
                {
                    latestBSN = getSafeString(rs, 1);
                }
            }
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "transaction table");
            throw new DataException(DataException.SQL_ERROR, "transaction table", se);
        }

        return latestBSN;
    }

    /**
     * Retrieve a stringbuffer from an inputstream
     *
     * @param is - the retrieved inputstream
     * @return StringBuffer represented by the InputStream
     * @throws Exception if there are any errors
     */
    protected StringBuffer getStringBufferFromStream(InputStream is) throws Exception
    {
        StringBuffer sf = new StringBuffer();
        int ch = is.read();
        while ((ch != -1) && Character.isWhitespace((char)ch))
            ch = is.read();

        while ((ch != -1) && !Character.isWhitespace((char)ch))
        {
            sf.append((char)ch);
            ch = is.read();
        }

        return sf;
    }

    /**
     * Convert the string saved in the database to the appropriate matching
     * integer.
     *
     * @param readableOrderType
     * @return the matching internal integer for either ATG or SIEBEL.
     * @see ExternalOrderConstantsIfc
     */
    protected int getExternalOrderType(String readableOrderType)
    {
        if (ExternalOrderConstantsIfc.ORDER_TYPE_DESCRIPTORS[ExternalOrderConstantsIfc.TYPE_SIEBEL].equals(readableOrderType))
        {
            return ExternalOrderConstantsIfc.TYPE_SIEBEL;
        }
        else if (ExternalOrderConstantsIfc.ORDER_TYPE_DESCRIPTORS[ExternalOrderConstantsIfc.TYPE_ATG].equals(readableOrderType))
        {
            return ExternalOrderConstantsIfc.TYPE_ATG;
        }
        return ExternalOrderConstantsIfc.TYPE_UNKNOWN;
    }

    /**
     * Read the customer information from the database.
     *
     * @param dataConnection data connection reference
     * @param irsCustomerID irs customer id
     * @return IRSCustomerIfc being requested
     * @exception DataException thrown when an error occurs executing the
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected IRSCustomerIfc readIRSCustomer(JdbcDataConnection dataConnection, String irsCustomerID)
            throws DataException
    {
        if (perf.isDebugEnabled())
        {
            perf.debug("Entering readIRSCustomer");
        }
        IRSCustomerIfc retrievedIrsCustomer = DomainGateway.getFactory().getIRSCustomerInstance();
        retrievedIrsCustomer.setCustomerID(irsCustomerID);
        ARTSCustomer artsCustomer = new ARTSCustomer(retrievedIrsCustomer);

        artsCustomer = retrieveIRSCustomerDetails(dataConnection, artsCustomer);

        artsCustomer = retrieveIRSCustomerAddress(dataConnection, artsCustomer);

        retrievedIrsCustomer = (IRSCustomerIfc)artsCustomer.getPosCustomer();
        if (perf.isDebugEnabled())
        {
            perf.debug("Exiting readIRSCustomer");
        }
        return retrievedIrsCustomer;
    }

    /**
     * @param dataConnection data connection reference
     * @param irsArtsCustomer irs arts customer reference
     * @param localeRequestor
     * @return ARTSCustomer arts customer
     * @throws DataException data exception
     */
    protected ARTSCustomer retrieveIRSCustomerDetails(JdbcDataConnection dataConnection, ARTSCustomer irsArtsCustomer)
            throws DataException
    {
        IRSCustomerIfc irsCustomer = (IRSCustomerIfc)irsArtsCustomer.getPosCustomer();
        SQLSelectStatement sql = new SQLSelectStatement();

        String verifyingIdType = null;

        sql.setTable(TABLE_IRS_CUSTOMER);
        sql.addColumn(FIELD_PARTY_ID);
        sql.addColumn(FIELD_IRS_CUSTOMER_FIRST_NAME);
        sql.addColumn(FIELD_IRS_CUSTOMER_LAST_NAME);
        sql.addColumn(FIELD_IRS_CUSTOMER_MIDDLE_INITIAL);
        sql.addColumn(FIELD_IRS_CUSTOMER_DATE_OF_BIRTH);
        sql.addColumn(FIELD_IRS_CUSTOMER_ENCRYPTED_TAXPAYER_ID_NUMBER);
        sql.addColumn(FIELD_IRS_CUSTOMER_MASKED_TAXPAYER_ID_NUMBER);
        sql.addColumn(FIELD_IRS_CUSTOMER_OCCUPATION);
        sql.addColumn(FIELD_IRS_CUSTOMER_VERIFYING_ID_TYPE);
        sql.addColumn(FIELD_IRS_CUSTOMER_ENCRYPTED_VERIFYING_ID_NUMBER);
        sql.addColumn(FIELD_IRS_CUSTOMER_MASKED_VERIFYING_ID_NUMBER);
        sql.addColumn(FIELD_IRS_CUSTOMER_VERIFYING_ID_ISSUING_STATE);
        sql.addColumn(FIELD_IRS_CUSTOMER_VERIFYING_ID_ISSUING_COUNTRY);
        sql.addColumn(FIELD_IRS_CUSTOMER_DATE_CASH_RECEIVED);

        sql.addQualifier(FIELD_IRS_CUSTOMER_ID, "'" + irsCustomer.getCustomerID() + "'");

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                logger.warn("irs customer not found!");
                throw new DataException(DataException.NO_DATA, "irs customer not found");
            }

            int index = 0;
            int partyID = rs.getInt(++index);
            String firstName = getSafeString(rs, ++index);
            String lastName = getSafeString(rs, ++index);
            String middleName = getSafeString(rs, ++index);
            EYSDate birthDate = getEYSDateFromString(rs, ++index);
            String encryptedTaxPayerIdNumber = getSafeString(rs, ++index);
            String maskedTaxPayerIdNumber = getSafeString(rs, ++index);
            EncipheredDataIfc taxPayerID = FoundationObjectFactory.getFactory()
                    .createEncipheredDataInstance(encryptedTaxPayerIdNumber, maskedTaxPayerIdNumber);
            String occupation = getSafeString(rs, ++index);
            verifyingIdType = getSafeString(rs, ++index);
            String encryptedVerifyingIdNumber = getSafeString(rs, ++index);
            String maskedVerifyingIdNumber = getSafeString(rs, ++index);
            EncipheredDataIfc verifyingIdNumber = FoundationObjectFactory.getFactory()
                    .createEncipheredDataInstance(encryptedVerifyingIdNumber, maskedVerifyingIdNumber);
            String verifyingIdIssuingState = getSafeString(rs, ++index);
            String verifyingIdIssuingCountry = getSafeString(rs, ++index);
            EYSDate dateCashreceived = getEYSDateFromString(rs, ++index);

            irsArtsCustomer.setPartyId(partyID);
            irsCustomer.setFirstName(firstName);
            irsCustomer.setLastName(lastName);
            irsCustomer.setMiddleName(middleName);
            irsCustomer.setBirthdate(birthDate);
            irsCustomer.setEncipheredTaxID(taxPayerID);
            irsCustomer.setOccupation(occupation);
            irsCustomer.getLocalizedPersonalIDCode().setCode(verifyingIdType);
            irsCustomer.setVerifyingID(verifyingIdNumber);
            irsCustomer.setVerifyingIdIssuingState(verifyingIdIssuingState);
            irsCustomer.setVerifyingIdIssuingCountry(verifyingIdIssuingCountry);
            irsCustomer.setDateCashReceived(dateCashreceived);
            irsArtsCustomer.setPosCustomer(irsCustomer);
            rs.close();
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "irs customer table");
            throw new DataException(DataException.SQL_ERROR, "irs customer table", se);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "irs customer table", e);
        }

        return irsArtsCustomer;
    }

    /**
     * @param dataConnection data connection reference
     * @param irsArtsCustomer irs arts customer reference
     * @return ARTSCustomer arts customer
     * @throws DataException data exception
     */
    protected ARTSCustomer retrieveIRSCustomerAddress(JdbcDataConnection dataConnection, ARTSCustomer irsArtsCustomer)
            throws DataException
    {
        IRSCustomerIfc irsCustomer = (IRSCustomerIfc)irsArtsCustomer.getPosCustomer();
        AddressIfc irsCustomerAddress = DomainGateway.getFactory().getAddressInstance();
        SQLSelectStatement sql = new SQLSelectStatement();

        sql.setTable(TABLE_ADDRESS);

        // Fields
        sql.addColumn(FIELD_ADDRESS_TYPE_CODE);
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_1);
        sql.addColumn(FIELD_CONTACT_ADDRESS_LINE_2);
        sql.addColumn(FIELD_CONTACT_CITY);
        sql.addColumn(FIELD_CONTACT_STATE);
        sql.addColumn(FIELD_CONTACT_POSTAL_CODE);
        sql.addColumn(FIELD_CONTACT_COUNTRY);

        sql.addQualifier(FIELD_PARTY_ID, irsArtsCustomer.getPartyId());

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            if (!rs.next())
            {
                logger.warn("irs customer not found!");
                throw new DataException(DataException.NO_DATA, "irs customer not found");
            }

            int index = 0;
            String addressTypeCode = getSafeString(rs, ++index);
            String addressLine1 = getSafeString(rs, ++index);
            String addressLine2 = getSafeString(rs, ++index);
            String city = getSafeString(rs, ++index);
            String state = getSafeString(rs, ++index);
            String postalCode = getSafeString(rs, ++index);
            String country = getSafeString(rs, ++index);

            irsCustomerAddress.setAddressType(Integer.parseInt(addressTypeCode));
            irsCustomerAddress.addAddressLine(addressLine1);
            if (addressLine2 != null)
            {
                irsCustomerAddress.addAddressLine(addressLine2);
            }
            irsCustomerAddress.setCity(city);
            irsCustomerAddress.setState(state);
            irsCustomerAddress.setPostalCode(postalCode);
            irsCustomerAddress.setCountry(country);
            irsCustomer.addAddress(irsCustomerAddress);
            irsArtsCustomer.setPosCustomer(irsCustomer);
            rs.close();
        }
        catch (DataException de)
        {
            logger.error("" + de + "");
            throw de;
        }
        catch (SQLException se)
        {
            dataConnection.logSQLException(se, "address table for irs customer");
            throw new DataException(DataException.SQL_ERROR, "address table for irs customer", se);
        }
        catch (Exception e)
        {
            logger.error("" + Util.throwableToString(e) + "");
            throw new DataException(DataException.UNKNOWN, "address table for irs customer", e);
        }
        return irsArtsCustomer;
    }

    /**
     * Reads from the retail price modifier table.
     *
     * @param dataConnection the connection to the data source
     * @param transaction the transaction coming from business logic
     * @param lineItem the sale/return line item
     * @return Array of discount strategies
     * @exception DataException thrown when an error occurs executing the SQL
     *                against the DataConnection, or when processing the
     *                ResultSet
     */
    protected PromotionLineItemIfc[] selectPromotionLineItems(JdbcDataConnection dataConnection,
            TransactionIfc transaction, SaleReturnLineItemIfc lineItem) throws DataException
    {
        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPromotionLineItems()");

        SQLSelectStatement sql = new SQLSelectStatement();
        /*
         * Add Table(s)
         */
        sql.addTable(TABLE_PROMOTION_LINE_ITEM);
        /*
         * Add Column(s)
         */
        sql.addColumn(FIELD_RETAIL_STORE_ID);
        sql.addColumn(FIELD_WORKSTATION_ID);
        sql.addColumn(FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_BUSINESS_DAY_DATE);
        sql.addColumn(FIELD_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_PROMOTION_LINE_ITEM_SEQUENCE_NUMBER);
        sql.addColumn(FIELD_PROMOTION_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_ID);
        sql.addColumn(FIELD_PROMOTION_COMPONENT_DETAIL_ID);
        sql.addColumn(FIELD_RETAIL_PRICE_MODIFIER_AMOUNT);
        sql.addColumn(FIELD_CUSTOMER_PRICING_GROUP_ID);
        sql.addColumn(FIELD_PROMOTION_RECEIPT_LOCALE);
        sql.addColumn(FIELD_PROMOTION_RECEIPT_NAME);

        /*
         * Add Qualifier(s)
         */
        sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
        sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
        sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItem.getLineNumber());
        sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));
        /*
         * Add Ordering
         */
        sql.addOrdering(FIELD_PROMOTION_LINE_ITEM_SEQUENCE_NUMBER + " ASC");

        Vector<PromotionLineItemIfc> promotionLineItems = new Vector<PromotionLineItemIfc>(2);

        try
        {
            dataConnection.execute(sql.getSQLString());
            ResultSet rs = (ResultSet)dataConnection.getResult();

            while (rs.next())
            {
                int index = 0;
                PromotionLineItemIfc promotionLineItem = DomainGateway.getFactory().getPromotionLineItemInstance();
                promotionLineItem.setStoreId(getSafeString(rs, ++index));
                promotionLineItem.setWorkstationId(getSafeString(rs, ++index));
                promotionLineItem.setTransactionSequenceNumber(rs.getInt(++index));
                promotionLineItem.setBusinessDate(getEYSDateFromString(rs, ++index));
                promotionLineItem.setTransactionLineItemSequenceNumber(rs.getInt(++index));
                promotionLineItem.setPromotionLineItemSequenceNumber(rs.getInt(++index));
                promotionLineItem.setPromotionId(rs.getInt(++index));
                promotionLineItem.setPromotionComponentId(rs.getInt(++index));
                promotionLineItem.setPromotionComponentDetailId(rs.getInt(++index));
                promotionLineItem.setDiscountAmount(getCurrencyFromDecimal(rs, ++index));
                promotionLineItem.setPricingGroupID(rs.getInt(++index));
                String locale = getSafeString(rs, ++index);
                if (locale != null)
                {
                    promotionLineItem.setReceiptLocale(new Locale(locale));
                }
                promotionLineItem.setPromotionName(getSafeString(rs, ++index));

                promotionLineItems.add(promotionLineItem);

            }
            rs.close();
        }
        catch (SQLException exc)
        {
            dataConnection.logSQLException(exc, "Processing result set.");
            throw new DataException(DataException.SQL_ERROR, "selectPromotionLineItems", exc);
        }

        PromotionLineItemIfc[] promotionLineItemsArray = null;
        if (promotionLineItems.size() > 0)
        {
            promotionLineItemsArray = new PromotionLineItemIfc[promotionLineItems.size()];
            promotionLineItems.copyInto(promotionLineItemsArray);
        }

        if (logger.isDebugEnabled())
            logger.debug("JdbcReadTransaction.selectPromotionLineItems()");

        return (promotionLineItemsArray);
    }

    /**
     * Determines locale requestor for given transaction.
     *
     * @param searchTransaction The transaction that may contain a locale
     *            requestor
     * @return a locale requestor
     */
    public LocaleRequestor getLocaleRequestor(TransactionIfc searchTransaction)
    {
        LocaleRequestor localeRequestor = null;
        if (searchTransaction.getLocaleRequestor() != null)
        {
            localeRequestor = searchTransaction.getLocaleRequestor();
        }
        else
        {
            localeRequestor = new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT));
        }
        return localeRequestor;
    }

    /**
     * Determines locale requestor for given locale.
     *
     * @param sqlLocale The locale used to create a locale requestor
     * @return a locale requestor
     * @deprecated as of 14.0; no remaining users.
     */
    public static LocaleRequestor getLocaleRequestor(Locale sqlLocale)
    {
        LocaleRequestor localeRequestor = null;
        if (sqlLocale != null)
        {
            localeRequestor = new LocaleRequestor(sqlLocale);
        }
        else
        {
            localeRequestor = new LocaleRequestor(LocaleMap.getLocale(LocaleMap.DEFAULT));
        }
        return localeRequestor;
    }


    /**
     * Determines the array of Best Matching locales for the locales contained
     * in a LocaleRequestor.
     *
     * @param localeRequestor The transaction that may contain a locale
     *            requestor
     * @return Array of locales
     */
    public static Locale[] getBestMatchingRequestLocales(LocaleRequestor localeRequestor)
    {
        Locale[] returnLocales = new Locale[0];
        if (localeRequestor != null)
        {
            Set<Locale> locales = localeRequestor.getLocales();
            if (locales != null)
            {
                returnLocales = new Locale[locales.size()];
                int counter = 0;
                for (Iterator<Locale> i = locales.iterator(); i.hasNext();)
                {
                    returnLocales[counter++] = LocaleMap.getBestMatch(i.next());
                }
            }
        }

        return returnLocales;
    }


    /**
     * Retrieves a localized reason code.
     *
     * @param dataConnection
     * @param criteria
     * @return Localized Code object
     */
    protected LocalizedCodeIfc getLocalizedReasonCode(JdbcDataConnection dataConnection, CodeSearchCriteriaIfc criteria)
    {
        LocalizedCodeIfc localizedCode = null;

        String codeListType = criteria.getListID();
        try
        {
            if (codeListType.equals(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_PERCENTAGE)
                    || codeListType.equals(CodeConstantsIfc.CODE_LIST_ITEM_DISCOUNT_BY_AMOUNT)
                    || codeListType.equals(CodeConstantsIfc.CODE_LIST_PREFERRED_CUSTOMER_DISCOUNT)
                    || codeListType.equals(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_PERCENTAGE)
                    || codeListType.equals(CodeConstantsIfc.CODE_LIST_TRANSACTION_DISCOUNT_BY_AMOUNT))
            {
                localizedCode = new JdbcReadCodeListDiscount().readCode(dataConnection, criteria);
            }
            else if (codeListType.equals(CodeConstantsIfc.CODE_LIST_ADVANCED_PRICING_REASON_CODES))
            {
                localizedCode = new JdbcReadCodeListDiscount().readAdvancedPricingRuleCode(dataConnection, criteria);
            }
            else
            {
                localizedCode = new JdbcReadCodeList().readCode(dataConnection, criteria);
            }
        }
        catch (DataException e)
        {
            if (e.getErrorCode() == DataException.NO_DATA)
            {
                logger.info(
                        "Localized descriptions are not available for reason code: " + criteria.getCode());
            }
            else
            {
                logger.warn(
                        "An error occured retrieving the localized descriptions for reason code: " + criteria.getCode(), e);
            }
            localizedCode = DomainGateway.getFactory().getLocalizedCode();
            localizedCode.setCode(criteria.getCode());
        }
        return localizedCode;
    }

    /**
     * Select external order line item info into the sale return line item
     * @param dataConnection the data connection
     * @param transaction the retail transaction
     * @param lineItem the sale return line item
     * @throws DataException
     */
    protected void selectExternalOrderLineItem(JdbcDataConnection dataConnection,
            SaleReturnTransactionIfc transaction, SaleReturnLineItemIfc lineItem)
            throws DataException
    {
          if (logger.isDebugEnabled())
              logger.debug("JdbcReadTransaction.selectExternalOrderLineItem()");

          SQLSelectStatement sql = new SQLSelectStatement();
          /*
           * Add Table(s)
           */
          sql.addTable(TABLE_EXTERNAL_ORDER_LINE_ITEM);
          /*
           * Add Column(s)
           */
          sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_ID);
          sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_PARENT_ID);
          sql.addColumn(FIELD_EXTERNAL_PRICING_FLAG);
          sql.addColumn(FIELD_EXTERNAL_TAX_FLAG);
          sql.addColumn(FIELD_EXTERNAL_ORDER_ITEM_UPDATE_SOURCE);

          /*
           * Add Qualifier(s)
           */
          sql.addQualifier(FIELD_RETAIL_STORE_ID + " = " + getStoreID(transaction));
          sql.addQualifier(FIELD_WORKSTATION_ID + " = " + getWorkstationID(transaction));
          sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(transaction));
          sql.addQualifier(FIELD_RETAIL_TRANSACTION_LINE_ITEM_SEQUENCE_NUMBER + " = " + lineItem.getLineNumber());
          sql.addQualifier(FIELD_BUSINESS_DAY_DATE + " = " + getBusinessDayString(transaction));

          try
          {
              String externalOrderItemID = "";
              String externalOrderParentItemID = "";
              boolean externalPricingFlag = false;
              boolean externalTaxFlag = false;
              boolean externalOrderItemUpdateFlag = true;

              dataConnection.execute(sql.getSQLString());
              ResultSet rs = (ResultSet)dataConnection.getResult();

              if (rs.next())
              {
                  int index = 0;
                  externalOrderItemID = getSafeString(rs, ++index);
                  if (externalOrderItemID.equals(EXTERNAL_ORDER_ITEM_ID_TO_BE_FILLED_IN))
                  {
                      externalOrderItemID = "";
                  }
                  externalOrderParentItemID = getSafeString(rs, ++index);
                  externalPricingFlag = getBooleanFromString(rs, ++index);
                  externalTaxFlag = getBooleanFromString(rs, ++index);
                  externalOrderItemUpdateFlag = getBooleanFromString(rs, ++index);
              }

              lineItem.setExternalOrderItemID(externalOrderItemID);
              lineItem.setExternalOrderParentItemID(externalOrderParentItemID);
              lineItem.setExternalPricingFlag(externalPricingFlag);
              lineItem.setExternalTaxFlag(externalTaxFlag);
              lineItem.setExternalOrderItemUpdateSourceFlag(externalOrderItemUpdateFlag);
          }
          catch (SQLException exc)
          {
              dataConnection.logSQLException(exc, "Processing result set.");
              throw new DataException(DataException.SQL_ERROR, "selectExternalOrderLineItem", exc);
          }
    }

    /**
     * Selects the bill pay transaction from the bill pay transaction tables
     * @param dataConnection
     * @param billPayTransaction
     * @throws DataException
     */
    protected void selectBillPayTransaction(JdbcDataConnection dataConnection, BillPayTransactionIfc billPayTransaction)
    throws DataException
    {

        ArrayList<BillIfc> billsList = new ArrayList<BillIfc>();
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_BILL_PAY);

        sql.addColumn(FIELD_BILLPAY_ACCOUNT_NUMBER);
        sql.addColumn(FIELD_BILLPAY_CUSTOMER_NAME);
        sql.addColumn(FIELD_BILLPAY_PAYMENT_DATE);

        sql.addQualifier(FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(billPayTransaction));

        BillPayIfc billPayInfo = DomainGateway.getFactory().getBillPayInstance();
        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            while(rs.next())
            {
                int index = 0;

                String accountNumber = getSafeString(rs, ++index);
                String customerName = getSafeString(rs, ++index);
                EYSDate billPaymentDate = getEYSDateFromString(rs, ++index);

                billPayInfo.setAccountNumber(accountNumber);
                billPayInfo.setFirstLastName(customerName);
                billPayInfo.setPaymentDate(billPaymentDate);

            }

            rs.close();

            billsList = selectBillPayTransactionLineItems(dataConnection, billPayTransaction);
            billPayInfo.setBillsList(billsList);
            billPayTransaction.setBillPayInfo(billPayInfo);

            TenderLineItemIfc[] tenderLineItems = selectTenderLineItems(dataConnection, billPayTransaction);
            billPayTransaction.setTenderLineItems(tenderLineItems);

            TransactionTaxIfc tax = DomainGateway.getFactory().getTransactionTaxInstance();
            tax.setDefaultRate(0.0);
            tax.setTaxMode(TaxIfc.TAX_MODE_NON_TAXABLE);
            billPayTransaction.setTransactionTax(tax);

        }
        catch(DataException e)
        {
            logger.error("" + e + "");
            throw e;
        }
        catch(SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(DataException.SQL_ERROR);
        }
        finally
        {

        }

    }

    /**
     * Selects the bill pay transaction from the bill pay transaction tables
     * @param dataConnection
     * @param billPayTransaction
     * @throws DataException
     */
    protected ArrayList<BillIfc> selectBillPayTransactionLineItems(JdbcDataConnection dataConnection, BillPayTransactionIfc billPayTransaction)
    throws DataException
    {

        ArrayList<BillIfc> billsList = new ArrayList<BillIfc>();
        SQLSelectStatement sql = new SQLSelectStatement();
        sql.addTable(TABLE_BILL_PAY, ALIAS_BILL_PAY);
        sql.addTable(TABLE_BILL_PAY_LINE_ITEM, ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM);

        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_BILLPAY_ACCOUNT_NUMBER);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_CHILD_ACCOUNT_NUMBER);
        sql.addColumn(ALIAS_BILL_PAY + "." + FIELD_BILLPAY_CUSTOMER_NAME);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_CHILD_CUSTOMER_NAME);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_BILL_NUMBER);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_PAYMENT_COLLECTED);
        sql.addColumn(ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_BILL_DUE_DATE);


        sql.addJoinQualifier(ALIAS_BILL_PAY + "." + FIELD_BILLPAY_ACCOUNT_NUMBER,
                ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BILLPAY_ACCOUNT_NUMBER);
        sql.addJoinQualifier(ALIAS_BILL_PAY + "." + FIELD_RETAIL_STORE_ID,
                ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_RETAIL_STORE_ID);
        sql.addJoinQualifier(ALIAS_BILL_PAY + "." + FIELD_WORKSTATION_ID,
                ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_WORKSTATION_ID);
        sql.addJoinQualifier(ALIAS_BILL_PAY + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER,
                ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER);
        sql.addJoinQualifier(ALIAS_BILL_PAY + "." + FIELD_BUSINESS_DAY_DATE,
                ALIAS_BILL_PAY_TRANSACTION_LINE_ITEM + "." + FIELD_BUSINESS_DAY_DATE);

        sql.addQualifier(ALIAS_BILL_PAY + "." + FIELD_RETAIL_STORE_ID, getStoreID(billPayTransaction));
        sql.addQualifier(ALIAS_BILL_PAY + "." + FIELD_WORKSTATION_ID, getWorkstationID(billPayTransaction));
        sql.addQualifier(ALIAS_BILL_PAY + "." + FIELD_TRANSACTION_SEQUENCE_NUMBER + " = " + getTransactionSequenceNumber(billPayTransaction));
        sql.addQualifier(ALIAS_BILL_PAY + "." + FIELD_BUSINESS_DAY_DATE, getBusinessDayString(billPayTransaction));

        try
        {
            dataConnection.execute(sql.getSQLString());

            ResultSet rs = (ResultSet)dataConnection.getResult();

            while(rs.next())
            {
                BillIfc billDetails = new Bill();
                int index = 0;
                String accountNumber = getSafeString(rs, ++index);
                String childAccountNumber = getSafeString(rs, ++index);
                String customerName  = getSafeString(rs, ++index);
                String childCustomerName  = getSafeString(rs, ++index);
                String billNumber = getSafeString(rs, ++index);
                String  billAmountPaid = getSafeString(rs, ++index);
                EYSDate dueDate = getEYSDateFromString(rs, ++index);
                CurrencyIfc billAmount = DomainGateway.getBaseCurrencyInstance(billAmountPaid);

                if(Util.isEmpty(childAccountNumber))
                {
                    billDetails.setAccountNumber(accountNumber);
                    billDetails.setCustomerName(customerName);
                }
                else
                {
                    billDetails.setAccountNumber(childAccountNumber);
                    billDetails.setCustomerName(childCustomerName);
                }
                billDetails.setBillNumber(billNumber);
                billDetails.setBillAmountPaid(billAmount);
                billDetails.setDueDate(dueDate);
                billsList.add(billDetails);
             }

        }
        catch(DataException e)
        {
            logger.error("" + e + "");
            throw e;
        }
        catch(SQLException se)
        {
            logger.error("" + se + "");
            throw new DataException(DataException.SQL_ERROR);
        }

        return billsList;
    }    
    
    
    /**
     *  Selects the tax rules for the plu item
     * 
     * @param JdbcDataConnection The jdbc data connection.
     * @param SaleReturnTransactionIfc The transaction object
     * @param PLUItemIfc The plu item
     * @throws DataException
     */
    protected PLUItemIfc selectPLUItemTaxRules(JdbcDataConnection dataConnection, SaleReturnTransactionIfc transaction,
            PLUItemIfc pluItem)
    {
        try
        {
            JdbcReadNewTaxRules taxReader = new JdbcReadNewTaxRules();
            String storeId = transaction.getWorkstation().getStore().getStoreID();
            ArrayList<Integer> taxGroupIds = new ArrayList<Integer>();
            taxGroupIds.add(new Integer(pluItem.getTaxGroupID()));
            // build search criteria for tax rule query
            NewTaxRuleSearchCriteria searchCriteria = new NewTaxRuleSearchCriteria(storeId, taxGroupIds,
                    NewTaxRuleSearchCriteria.SEARCH_BY_STORE);
            TaxRulesVO taxRulesVO = taxReader.readTaxRules(dataConnection, searchCriteria);
            TaxRuleIfc[] taxRules = (TaxRuleIfc[]) taxRulesVO.getTaxRules(pluItem.getTaxGroupID());
            // in case there is no tax rules for the plu item, use the
            // department tax rules
            if ((taxRules == null) || (taxRules.length == 0))
            {
                taxRules = (TaxRuleIfc[]) taxRulesVO.getTaxRules(pluItem.getDepartment().getTaxGroupID());
            }
            pluItem.setTaxRules(taxRules);

        }
        catch (DataException e)
        {
            // Do nothing, Default tax rules will be used.
        }
        return pluItem;
    }

    /**
     * Method which gets the ILRM Message for a Given Item. The Catch block
     * simply prints the exception caused during execution as the requirement is
     * to just print the error not propogate it.
     * 
     * @param dataConnection
     * @param item
     * @throws DataException
     */
    public void getItemLevelMessages(JdbcDataConnection dataConnection, PLUItemIfc item)
    {
        if (item != null)
        {
            SQLSelectStatement sql = new SQLSelectStatement();
            MessageDTO mdto = null;
            List<MessageDTO> messageList = new ArrayList<MessageDTO>();
            Map<String, List<MessageDTO>> messagesMap = new HashMap<String, List<MessageDTO>>(1);
            ResultSet rs = null;
            // add tables
            sql.addTable(TABLE_ITEM_MESSAGE_ASSOCIATION);
            sql.addTable(TABLE_ASSET_MESSAGES);
            sql.addTable(TABLE_ASSET_MESSAGES_I18N);

            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TYPE);
            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_CODE_ID);
            sql.addColumn(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TRANSACTION_TYPE);
            sql.addColumn(TABLE_ASSET_MESSAGES_I18N, FIELD_LOCALE);
            sql.addColumn(TABLE_ASSET_MESSAGES_I18N, FIELD_MESSAGE_DESCRIPTION);

            // add qualifiers
            sql.addQualifier(new SQLParameterValue(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_ITEM_ID, item.getItemID()));
            sql.addJoinQualifier(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES,
                    FIELD_MESSAGE_CODE_ID);
            sql.addJoinQualifier(TABLE_ASSET_MESSAGES, FIELD_MESSAGE_CODE_ID, TABLE_ASSET_MESSAGES_I18N,
                    FIELD_MESSAGE_CODE_ID);

            sql.addOrdering(TABLE_ITEM_MESSAGE_ASSOCIATION, FIELD_MESSAGE_TRANSACTION_TYPE);

            try
            {
                String str = sql.getSQLString();
                String transactionType = null;
                String messageType = null;
                logger.debug(str);
                // execute the query and get the result set
                dataConnection.execute(str, sql.getParameterValues());
                rs = (ResultSet)dataConnection.getResult();
                while (rs.next())
                {
                    if (transactionType != null
                            && !transactionType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE)))
                    {
                        messageList.add(mdto);
                        messagesMap.put(transactionType, messageList);
                        messageList = null;
                        messageType = null;
                        messageList = new ArrayList<MessageDTO>();
                    }

                    if (messageType != null && messageType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TYPE)) && mdto != null)
                    {
                        mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rs.getString(FIELD_LOCALE)),
                                rs.getString(FIELD_MESSAGE_DESCRIPTION));
                        continue;
                    }
                    else if (messageType != null && !messageType.equalsIgnoreCase(rs.getString(FIELD_MESSAGE_TYPE)))
                    {
                        messageList.add(mdto);
                    }

                    messageType = rs.getString(FIELD_MESSAGE_TYPE);

                    mdto = new MessageDTO();
                    mdto.setDefaultItemMessage(rs.getString(FIELD_MESSAGE_DESCRIPTION));
                    mdto.setItemMessageCodeID(rs.getString(FIELD_MESSAGE_CODE_ID));
                    mdto.setItemMessageTransactionType(rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE));
                    mdto.setItemMessageType(messageType);
                    mdto.addLocalizedItemMessage(LocaleUtilities.getLocaleFromString(rs.getString(FIELD_LOCALE)),
                            rs.getString(FIELD_MESSAGE_DESCRIPTION));

                    logger.info(mdto.toString());
                    transactionType = rs.getString(FIELD_MESSAGE_TRANSACTION_TYPE);
                }
                messageList.add(mdto);
                messagesMap.put(transactionType, messageList);
                item.setAllItemLevelMessages(messagesMap);
            }
            catch (DataException de)
            {
                logger.error("A Data Exception occurred while executing sql -" + sql + " " + de.toString());
            }
            catch (SQLException se)
            {
                logger.error("SqlError in retrieving data from table " + ARTSDatabaseIfc.TABLE_ITEM_MESSAGE_ASSOCIATION
                        + se);
            }
            catch (Exception e)
            {
                logger.error("Unexpected exception in Retrieving item associated message " + e);
            }
            finally
            {
                if (rs != null)
                {
                    try
                    {
                        rs.close();
                    }
                    catch (SQLException se)
                    {
                        logger.error(se);
                    }
                }
            }
        }
    }

}
