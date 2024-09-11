/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/factory/DomainObjectFactory.java /main/63 2014/06/16 13:56:04 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       06/11/14 - combine multiple order transactions into one final
 *                         one
 *    asinton   08/08/13 - add HouseAccountPaymentRequest factory method.
 *    jswan     06/19/13 - Modified to perform the status update of an Order in
 *                         the context of a transaction.
 *    rgour     02/28/13 - added capped tax rule
 *    jswan     02/13/13 - Modified for Currency Rounding.
 *    yiqzhao   01/04/13 - Change after ADE merge.
 *    yiqzhao   01/04/13 - Refactoring ItemManager
 *    abondala  01/03/13 - refactored transformers
 *    vtemker   12/24/12 - CR 204 - Added method
 *                         getTLogTransactionEntryInstance
 *    jswan     12/06/12 - Modified to support JDBC opertions for order tax and
 *                         discount status.
 *    vtemker   11/30/12 - CR204 - Modified factory method to return SIM TLog/
 *                         RTLog batch generator based on type
 *    sgu       10/17/12 - prorate item tax for partial pickup or cancellation
 *    yiqzhao   09/20/12 - remove displayPriority and
 *                         relatedItemGroupAssociation columns from
 *                         relatedItemAssociation table. Remove
 *                         RelatedItemGroupContainer class.
 *    yiqzhao   09/20/12 - Remove DisplayPriority and
 *                         RelatedItemGroupAssociation columns from
 *                         RelationItemAssociation table. Remove
 *                         RelatedItemGroupContainer java object.
 *    jswan     09/17/12 - Added PLUItemAggregationTransformer to provide
 *                         simple interface for both Jpa PLU Operation and the
 *                         PLU Lookup through the web service.
 *    sgu       08/27/12 - read transaction discount audit from db
 *    abondala  08/21/12 - jpa for pricing group
 *    sgu       08/20/12 - added api to create item discount audit
 *    acadar    07/30/12 - refreshed to label
 *    jswan     07/20/12 - Modified to support JPA Entity to Domin
 *                         transformations.
 *    acadar    07/02/12 - merged to tip
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    jswan     06/29/12 - Removed references to tax classes that have been
 *                         depricated and are now deleted.
 *    acadar    06/26/12 - Cross Channel changes
 *    acadar    05/30/12 - merge to tip
 *    acadar    05/29/12 - changes for cross channel
 *    ohorne    05/29/12 - added getOrderSearchCriteriaInstance
 *    acadar    05/23/12 - CustomerManager refactoring
 *    ohorne    05/21/12 - removed ref to InvAvailDesc
 *    acadar    05/14/12 - refresh to tip
 *    acadar    05/08/12 - changes for customer
 *    jswan     05/01/12 - Modified to support the cross channel feature create
 *                         pickup order.
 *    acadar    04/30/12 - changes for XC
 *    sgu       04/27/12 - check in merge changes
 *    sgu       04/26/12 - check in merge changes
 *    ohorne    04/25/12 - ItemAvailability/StoreInventory now known as
 *                         AvailableToPromiseInventory
 *    ohorne    04/17/12 - xc:added getItemAvailability and
 *                         getStoreInventorySearchCriteria
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    yiqzhao   03/07/12 - add OrderShippingDetail domain object and modify the
 *                         related code
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *    sgu       10/04/11 - rework table tax using tax rules instead of
 *                         calculator
 *    cgreene   08/24/11 - create interfaces for customerinteraction objects
 *    sgu       07/13/11 - add api to get card token
 *    blarsen   07/12/11 - Add get instance for auth ReversalRequest
 *    cgreene   07/08/11 - refactor v2.1 POSLog to not insert items by default
 *    blarsen   06/30/11 - added getPaymentServiceStatusResponseInstance().
 *    jswan     06/29/11 - Add instantiation for Signature capture request and
 *                         response objects.
 *    blarsen   06/28/11 - Added getInstance() for KeyManagerResponse
 *    blarsen   06/28/11 - Added getInstance() method for
 *                         CustomerInteractionRequest.
 *    blarsen   06/14/11 - Removing IsSwipeAhead. This is being handled by the
 *                         CustomerInteraction request.
 *    blarsen   06/09/11 - Added gets for the IsSwipeAhead request and
 *                         response.
 *    cgreene   06/09/11 - added dao to persist and retrieve ICC card details
 *    cgreene   05/27/11 - move auth response objects into domain
 *    sgu       04/26/11 - merge with owen's code
 *    sgu       04/25/11 - check in all
 *    asinton   03/25/11 - Moved APF request and response objects to common
 *                         module.
 *    nkgautam  06/24/10 - merged to tip
 *    nkgautam  06/22/10 - bill pay changes
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    acadar    06/02/10 - refactoring
 *    acadar    06/02/10 - signature capture changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/21/10 - renamed from _externalorder to externalorder
 *    acadar    05/18/10 - renamed domain package
 *    acadar    05/17/10 - additional logic added for processing orders
 *    acadar    05/14/10 - initial version for external order processing
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/22/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    cgreene   03/19/09 - added getItemImageInstance method
 *    aphulamb  11/27/08 - fixed merge issue
 *    rkar      11/17/08 - View refresh to 081112.2142 label
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    cgreene   11/06/08 - add extra getCode(String) method
 *    rkar      11/04/08 - Added code for POS-RM integration
 *    akandru   10/31/08 - EJ Changes_I18n
 *    akandru   10/30/08 - EJ changes
 *    mdecama   10/23/08 - Updated getLocalizedCode()
 *    akandru   10/20/08 - EJ -- I18N
 *    akandru   10/20/08 -
 *    ddbaker   10/13/08 - Updated to use factory to create localized text
 *                         objects
 *    mchellap  09/30/08 - Updated copy right header
 *
 * ===========================================================================
 *     $Log:
 *      14   360Commerce 1.13        4/25/2007 10:00:58 AM  Anda D. Cadar   I18N
 *           merge
 *      13   360Commerce 1.12        2/6/2007 11:12:28 AM   Anil Bondalapati
 *           Merge from DomainObjectFactory.java, Revision 1.9.1.0
 *      12   360Commerce 1.11        12/11/2006 1:26:10 PM  Jack G. Swan
 *           Removed ExtractorObjectFactory.
 *      11   360Commerce 1.10        12/8/2006 5:01:15 PM   Brendan W. Farrell
 *           Read the tax history when creating pos log for openclosetill
 *           transactions.  Rewrite of some code was needed.
 *      10   360Commerce 1.9         11/9/2006 7:28:31 PM   Jack G. Swan
 *           Modifided for XML Data Replication and CTR.
 *      9    360Commerce 1.8         10/26/2006 4:06:14 PM  Gennady Ioffe
 *           Report Removal: removed 6.x Post-Processor
 *      8    360Commerce 1.7         9/29/2006 10:42:54 AM  Rohit Sachdeva
 *           21237: Password Policy Service Domain Updates
 *      7    360Commerce 1.6         8/29/2006 6:31:36 PM   Brett J. Larsen CR
 *           20917 - remove stock item return disposition codes (aka inventory
 *           reason codes)
 *
 *           part of inventory feature which is no longer supported
 *      6    360Commerce 1.5         4/27/2006 7:27:23 PM   Brett J. Larsen CR
 *           17307 - remove inventory functionality - stage 2
 *      5    360Commerce 1.4         1/25/2006 4:10:58 PM   Brett J. Larsen merge
 *            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *      4    360Commerce 1.3         12/13/2005 4:43:47 PM  Barry A. Pape
 *           Base-lining of 7.1_LA
 *      3    360Commerce 1.2         3/31/2005 4:27:51 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:21:08 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:10:42 PM  Robert Pearse
 *     $: DomainObjectFactory.java,v $
 *      5    .v710     1.2.2.0     9/21/2005 13:39:25     Brendan W. Farrell
 *           Initial Check in merge 67.
 *      4    .v700     1.2.3.0     11/7/2005 16:40:13     Jason L. DeLeau 4223:
 *           Internationalize DomainObjectFactory at the request of services.
 *      3    360Commerce1.2         3/31/2005 15:27:51     Robert Pearse
 *      2    360Commerce1.1         3/10/2005 10:21:08     Robert Pearse
 *      1    360Commerce1.0         2/11/2005 12:10:42     Robert Pearse
 *     $
 *     Revision 1.23  2004/08/23 16:15:46  cdb
 *     @scr 4204 Removed tab characters
 *
 *     Revision 1.22  2004/08/18 16:21:29  jriggins
 *     @scr 4985 Reworked CheckForPriceAdjustableItemsSite.carryDiscountsForward() removed need for PriceAdjustmentItemTransactionDiscount
 *
 *     Revision 1.21  2004/07/31 17:30:52  cdb
 *     @scr 5421 Removed unused import.
 *
 *     Revision 1.20  2004/07/30 22:50:01  jriggins
 *     @scr 4985 Reworked the CheckForPriceAdjustableItemsSite.carryDiscountsForward() method so that indivdual discounts are copied over and prorated as needed. This was needed for printing out the individual discounts on the receipt.
 *
 *     Revision 1.19  2004/07/26 14:39:55  jdeleau
 *     @scr 2775 Rename ReturnItemTaxRule to ReverseItemTaxRule add isReturn
 *     method, to the ReverseItemTaxRule interface
 *
 *     Revision 1.18  2004/07/19 21:53:44  jdeleau
 *     @scr 6329 Fix the way post-void taxes were being retrieved.
 *     Fix for tax overrides, fix for post void receipt printing, add new
 *     tax rules for reverse transaction types.
 *
 *     Revision 1.17  2004/07/09 18:37:40  aachinfiev
 *     @scr 6082 - Added getPurchaseOrderLineItemInstance
 *
 *     Revision 1.16  2004/06/29 21:59:01  aachinfiev
 *     Merge the changes for inventory & POS integration
 *
 *     Revision 1.15  2004/06/29 21:29:15  jdeleau
 *     @scr 5777 Improve on the way return taxes are calculated, to solve this defect.
 *     Returns and purchases were going into the same container, and return
 *     values were being cleared on subsequent calculations.
 *
 *     Revision 1.14  2004/06/17 21:35:50  khassen
 *     @scr 5684 - Feature enhancements for capture customer use case.
 *
 *     Revision 1.13  2004/06/15 00:44:31  jdeleau
 *     @scr 2775 Support register reports and financial totals with the new
 *     tax engine.
 *
 *     Revision 1.12  2004/06/14 13:50:33  mkp1
 *     @scr 2775 Changed returns that are retrieved not to recalculate tax
 *
 *     Revision 1.11  2004/06/03 11:48:36  mkp1
 *     @scr 2775 Added all transaction tax overrides including tax exempt
 *
 *     Revision 1.10  2004/06/02 13:33:47  mkp1
 *     @scr 2775 Implemented item tax overrides using new tax engine
 *
 *     Revision 1.9  2004/05/27 16:59:23  mkp1
 *     @scr 2775 Checking in first revision of new tax engine.
 *
 *     Revision 1.8  2004/04/03 00:18:46  jriggins
 *     @scr 3979 Added getPriceAdjustmentLineItemInstance() method
 *
 *     Revision 1.7  2004/03/16 18:27:09  cdb
 *     @scr 0 Removed tabs from all java source code.
 *
 *     Revision 1.6  2004/03/11 19:53:45  blj
 *     @scr 3871 - Updates and additions for Redeem Transactions.
 *
 *     Revision 1.5  2004/02/17 20:37:13  baa
 *     @scr 3561 returns
 *
 *     Revision 1.4  2004/02/17 16:18:57  rhafernik
 *     @scr 0 log4j conversion
 *
 *     Revision 1.3  2004/02/12 17:13:32  mcs
 *     Forcing head revision
 *
 *     Revision 1.2  2004/02/11 23:25:29  bwf
 *     @scr 0 Organize imports.
 *
 *     Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *     updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.5   Feb 09 2004 16:53:02   crain
 * Added gift certificate item instance
 * Resolution for 3814: Issue Gift Certificate
 *
 *    Rev 1.4   Dec 29 2003 15:32:26   baa
 * return enhancements
 *
 *    Rev 1.3   Oct 31 2003 10:30:30   nrao
 * Added Objects for InstantCredit and InstantCreditTransaction for Instant Credit Enrollment.
 *
 *    Rev 1.2   27 Oct 2003 04:23:30   mwright
 * Created default instance of Irish Monitor Mode
 *
 *    Rev 1.1   Oct 24 2003 15:11:40   blj
 * updated for money order tender
 *
 *    Rev 1.0   Aug 29 2003 15:35:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.32   20 Jul 2003 23:31:24   mwright
 * Change object factory call to remove parameters to GetCalendarLevelInstance()
 *
 *    Rev 1.31   Jul 17 2003 19:09:36   dlr
 * Adding Kintore ReportBean Persistence
 * Resolution for 2201: Adding Kintore Report Bean Persistence
 *
 *    Rev 1.30   14 Jul 2003 01:22:16   mwright
 * Added factory creation for AccumulatorReturnsGiftReceipt
 *
 *    Rev 1.29   09 Jul 2003 16:58:30   mpm
 * Added support for SecurityOverride.
 *
 *    Rev 1.28   Jul 08 2003 14:25:58   jgs
 * Modified to return DiscountTypeCodeEntryIfc rather than CodeEntryIfc on the getDiscountTypeCodeEntryInstance() method.
 * Resolution for 3054: When using the Buy n of X, get the highest price X at Z% off rule where the Deal Distribution Indicator = Target only, the discount is being applied to both sources and target items.
 *
 *    Rev 1.27   Jul 01 2003 13:59:26   jgs
 * Add POSLogGenerator to factory.
 * Resolution for 1157: Add task for Importing IX Retail Transactions.
 *
 *    Rev 1.26   30 Jun 2003 01:16:10   mwright
 * Added create for EmployeePosDepartment()
 *
 *    Rev 1.25   Jun 25 2003 17:22:42   jriggins
 * Kintore integration
 * Resolution for 1957: Integrate Kintore code
 *
 *    Rev 1.24   Jun 17 2003 15:03:04   jriggins
 * Kintore integration
 *
 *    Rev 1.23   Jun 12 2003 08:06:58   bwf
 * Remove MWright code to get a clean domain build.
 *
 *    Rev 1.22   11 Jun 2003 21:11:26   mwright
 * Fixed typo in previous hasty checkin
 *
 *    Rev 1.21   11 Jun 2003 20:55:20   mwright
 * Added default creation for time of day summary processors
 * for Workstation and Operator
 *
 *    Rev 1.20   Jun 10 2003 11:42:16   jgs
 * Backout hardtotals deprecations and compression change due to performance consideration.
 *
 *    Rev 1.19   09 Jun 2003 00:46:16   mwright
 * Added default creations for new buckets:
 *  Customer, Demographic, Operator, Workstation, PriceDerivation
 *
 *    Rev 1.18   May 29 2003 13:34:34   dal
 * Clean up code.
 *
 *    Rev 1.17   May 20 2003 07:18:06   jgs
 * Modified to return the Serialized version of HardTotalsIfc and HardTotalsBuilderIfc.
 * Resolution for 2573: Modify Hardtotals compress to remove dependency on code modifications.
 *
 *    Rev 1.16   May 18 2003 09:06:30   mpm
 * Merged 5.1 changes into 6.0
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.15   May 17 2003 07:57:44   mpm
 * Added TransactionPostProcessor to factory.
 * Resolution for Backoffice SCR-1957: Integrate Kintore code
 *
 *    Rev 1.14   May 14 2003 05:05:58   mpm
 * Merged Kintore changes.
 *
 *    Rev 1.13   Apr 22 2003 12:21:58   adc
 * Added RoleFunctionGroup
 * Resolution for 1935: Roles/Security updates
 *
 *    Rev 1.12   Apr 20 2003 18:47:22   mpm
 * Added TransactionKey
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.11   Jan 30 2003 16:01:12   adc
 * Changes for BackOffice 2.0
 * Resolution for 1846: Advanced Pricing Updates
 *
 *    Rev 1.10   Jan 22 2003 09:39:46   mpm
 * Preliminary merging of 5.1/5.5 code.
 * Resolution for Domain SCR-104: Merge 5.1/5.5 into 6.0
 *
 *    Rev 1.9   Sep 23 2002 11:57:58   kmorneau
 * added alternate method to instantiate a CardType object
 * Resolution for 1815: Credit Card Types Accepted
 *
 *    Rev 1.8   Sep 18 2002 15:06:42   kmorneau
 * added instantiation for RuleIfc and CardIfc objects
 * Resolution for 1815: Credit Card Types Accepted
 *
 *    Rev 1.7   Sep 18 2002 13:13:36   DCobb
 * Add Purchase Order tender type.
 * Resolution for POS SCR-1799: POS 5.5 Purchase Order Tender Package
 *
 *    Rev 1.6   Sep 03 2002 15:43:28   baa
 * Externalize domain constants
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.5   21 Aug 2002 11:38:28   adc
 * Added factory method for Supplier
 * Resolution for kbpos SCR-2230: Order line items details being duplicated on order save
 *
 *    Rev 1.4   12 Aug 2002 18:02:30   sfl
 * Addded new method for creating TaxRate instance.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.3   07 Aug 2002 17:14:38   sfl
 * Added new method for creating newly added tax related
 * domain object instances.
 * Resolution for POS SCR-1749: POS 5.5 Tax Package
 *
 *    Rev 1.2   Jul 18 2002 14:32:58   DCobb
 * Add Alteration item for POS 5.5 Alterations Package.
 * Resolution for POS SCR-1753: POS 5.5 Alterations Package
 *
 *    Rev 1.1   Jun 07 2002 17:46:06   cdb
 * Updated to save code disposition in Back Office.
 * Resolution for Backoffice SCR-1098: Add return/disposition code to Item Maintenance
 *
 *    Rev 1.0   Jun 03 2002 16:51:20   msg
 * Initial revision.
 *===========================================================================
 */
package oracle.retail.stores.domain.factory;

import java.util.Locale;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeListIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteria;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteriaIfc;
import oracle.retail.stores.commerceservices.security.EmployeeComplianceIfc;
import oracle.retail.stores.commerceservices.security.PasswordPolicyEvaluatorIfc;
import oracle.retail.stores.common.utility.LocalizedCode;
import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.common.utility.LocalizedTextIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.alert.AlertEntryIfc;
import oracle.retail.stores.domain.alert.AlertListIfc;
import oracle.retail.stores.domain.audit.AuditEntryIfc;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.customer.CustomerGroupIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.CustomerInfoIfc;
import oracle.retail.stores.domain.customer.IRSCustomer;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.customer.PricingGroupIfc;
import oracle.retail.stores.domain.customer.event.CustomerEventBabyIfc;
import oracle.retail.stores.domain.customer.event.CustomerEventSpecialIfc;
import oracle.retail.stores.domain.customer.event.CustomerEventWeddingIfc;
import oracle.retail.stores.domain.customer.event.MerchandisePreferenceIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleIfc;
import oracle.retail.stores.domain.discount.AdvancedPricingRuleSearchCriteriaIfc;
import oracle.retail.stores.domain.discount.BestDealGroupIfc;
import oracle.retail.stores.domain.discount.CustomerDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.DiscountCalculationIfc;
import oracle.retail.stores.domain.discount.DiscountListEntryIfc;
import oracle.retail.stores.domain.discount.DiscountListIfc;
import oracle.retail.stores.domain.discount.DiscountRuleIfc;
import oracle.retail.stores.domain.discount.ItemDiscountAuditIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.ItemDiscountByPercentageIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAggregator;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAggregatorIfc;
import oracle.retail.stores.domain.discount.ItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.PromotionLineItem;
import oracle.retail.stores.domain.discount.PromotionLineItemIfc;
import oracle.retail.stores.domain.discount.ReturnItemTransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.SuperGroupIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountAuditIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByAmountIfc;
import oracle.retail.stores.domain.discount.TransactionDiscountByPercentageIfc;
import oracle.retail.stores.domain.emessage.EMessageIfc;
import oracle.retail.stores.domain.employee.EmployeeClockEntryIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.employee.RoleFunctionGroupIfc;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.employee.RoleIfc;
import oracle.retail.stores.domain.event.ItemMaintenanceEventIfc;
import oracle.retail.stores.domain.event.ItemPriceMaintenanceEventIfc;
import oracle.retail.stores.domain.event.MaintenanceEventIfc;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.domain.event.PriceDerivationRuleMaintenanceEventIfc;
import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItem;
import oracle.retail.stores.domain.externalorder.ExternalOrderSaleItemIfc;
import oracle.retail.stores.domain.externalorder.ExternalOrderSendPackageItem;
import oracle.retail.stores.domain.externalorder.ExternalOrderSendPackageItemIfc;
import oracle.retail.stores.domain.externalorder.LegalDocument;
import oracle.retail.stores.domain.externalorder.LegalDocumentIfc;
import oracle.retail.stores.domain.financial.AssociateProductivityIfc;
import oracle.retail.stores.domain.financial.BillPayIfc;
import oracle.retail.stores.domain.financial.CurrencyRoundingCalculator;
import oracle.retail.stores.domain.financial.CurrencyRoundingRuleSearchCriteria;
import oracle.retail.stores.domain.financial.DepartmentActivityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.domain.financial.EmployeeActivityIfc;
import oracle.retail.stores.domain.financial.FinancialCountIfc;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.HardTotalsBuilderIfc;
import oracle.retail.stores.domain.financial.HardTotalsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.domain.financial.PaymentHistoryInfo;
import oracle.retail.stores.domain.financial.PaymentHistoryInfoIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.financial.ReconcilableCountIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.ReportingPeriodIfc;
import oracle.retail.stores.domain.financial.StoreSafeIfc;
import oracle.retail.stores.domain.financial.StoreStatusAndTotalsIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.financial.TaxTotalsContainerIfc;
import oracle.retail.stores.domain.financial.TaxTotalsIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.financial.TimeIntervalActivityIfc;
import oracle.retail.stores.domain.giftregistry.GiftRegistryIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventory;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventorySearchCriteria;
import oracle.retail.stores.domain.inventoryinquiry.promise.AvailableToPromiseInventorySearchCriteriaIfc;
import oracle.retail.stores.domain.inventoryinquiry.promise.StoreItemAvailableToPromiseInventory;
import oracle.retail.stores.domain.inventoryinquiry.promise.StoreItemAvailableToPromiseInventoryIfc;
import oracle.retail.stores.domain.ixretail.log.POSLogBatchGeneratorIfc;
import oracle.retail.stores.domain.ixretail.log.POSLogTransactionEntryIfc;
import oracle.retail.stores.domain.job.ActiveJobIfc;
import oracle.retail.stores.domain.job.NotificationRecipientsIfc;
import oracle.retail.stores.domain.job.ScheduledJobIfc;
import oracle.retail.stores.domain.job.message.JobControlEventMessageIfc;
import oracle.retail.stores.domain.job.schedule.CustomScheduleDocumentIfc;
import oracle.retail.stores.domain.job.schedule.DailyScheduleDocumentIfc;
import oracle.retail.stores.domain.job.schedule.MonthlyByDayScheduleDocumentIfc;
import oracle.retail.stores.domain.job.schedule.WeeklyScheduleDocumentIfc;
import oracle.retail.stores.domain.job.task.TaskInfoIfc;
import oracle.retail.stores.domain.lineitem.ItemContainerProxyIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ItemTaxIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatus;
import oracle.retail.stores.domain.lineitem.OrderItemDiscountStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatus;
import oracle.retail.stores.domain.lineitem.OrderItemTaxStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.manager.datareplication.DataReplicationBatchGeneratorIfc;
import oracle.retail.stores.domain.manager.datareplication.DataReplicationCustomerEntryIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeCallReferralRequest;
import oracle.retail.stores.domain.manager.payment.AuthorizeCallReferralRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditRequest;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponse;
import oracle.retail.stores.domain.manager.payment.AuthorizeInstantCreditResponseIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequest;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferRequestIfc;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponse;
import oracle.retail.stores.domain.manager.payment.AuthorizeTransferResponseIfc;
import oracle.retail.stores.domain.manager.payment.CardTokenRequest;
import oracle.retail.stores.domain.manager.payment.CardTokenRequestIfc;
import oracle.retail.stores.domain.manager.payment.CardTokenResponse;
import oracle.retail.stores.domain.manager.payment.CardTokenResponseIfc;
import oracle.retail.stores.domain.manager.payment.CustomerInteractionRequest;
import oracle.retail.stores.domain.manager.payment.CustomerInteractionRequestIfc;
import oracle.retail.stores.domain.manager.payment.CustomerInteractionRequestIfc.RequestSubType;
import oracle.retail.stores.domain.manager.payment.CustomerInteractionResponse;
import oracle.retail.stores.domain.manager.payment.CustomerInteractionResponseIfc;
import oracle.retail.stores.domain.manager.payment.HouseAccountPaymentRequest;
import oracle.retail.stores.domain.manager.payment.HouseAccountPaymentRequestIfc;
import oracle.retail.stores.domain.manager.payment.PaymentServiceResponseIfc.ResponseCode;
import oracle.retail.stores.domain.manager.payment.ReversalRequest;
import oracle.retail.stores.domain.manager.payment.ReversalRequestIfc;
import oracle.retail.stores.domain.manager.payment.SignatureCaptureRequest;
import oracle.retail.stores.domain.manager.payment.SignatureCaptureRequestIfc;
import oracle.retail.stores.domain.manager.payment.SignatureCaptureResponse;
import oracle.retail.stores.domain.manager.payment.SignatureCaptureResponseIfc;
import oracle.retail.stores.domain.manager.payment.StatusResponse;
import oracle.retail.stores.domain.manager.payment.StatusResponseIfc;
import oracle.retail.stores.domain.manager.payment.pincomm.KeyManagementResponse;
import oracle.retail.stores.domain.manager.payment.pincomm.KeyManagementResponseIfc;
import oracle.retail.stores.domain.manager.payment.pincomm.PinCommEncryptionUtility;
import oracle.retail.stores.domain.manager.payment.pincomm.PinCommEncryptionUtilityIfc;
import oracle.retail.stores.domain.manager.report.ReportBeanIfc;
import oracle.retail.stores.domain.manager.rtlog.RTLogExportBatchGenerator;
import oracle.retail.stores.domain.manager.rtlog.RTLogExportBatchGeneratorIfc;
import oracle.retail.stores.domain.manager.simtlog.SIMTLogExportBatchGenerator;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.order.OrderRecipientIfc;
import oracle.retail.stores.domain.order.OrderSearchCriteria;
import oracle.retail.stores.domain.order.OrderSearchCriteriaIfc;
import oracle.retail.stores.domain.order.OrderStatusIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.domain.purchasing.PurchaseOrderIfc;
import oracle.retail.stores.domain.purchasing.PurchaseOrderLineItemIfc;
import oracle.retail.stores.domain.purchasing.SupplierIfc;
import oracle.retail.stores.domain.registry.RegistryIDIfc;
import oracle.retail.stores.domain.returns.ReturnTenderDataContainer;
import oracle.retail.stores.domain.returns.ReturnTenderDataElement;
import oracle.retail.stores.domain.shipping.ShippingChargeIfc;
import oracle.retail.stores.domain.shipping.ShippingItemIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteria;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteriaIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationIfc;
import oracle.retail.stores.domain.stock.ItemColorIfc;
import oracle.retail.stores.domain.stock.ItemIfc;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.ItemInfoIfc;
import oracle.retail.stores.domain.stock.ItemInquirySearchCriteriaIfc;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.ItemSearchCriteriaIfc;
import oracle.retail.stores.domain.stock.ItemSizeIfc;
import oracle.retail.stores.domain.stock.ItemStyleIfc;
import oracle.retail.stores.domain.stock.ItemTypeIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.ManufacturerIfc;
import oracle.retail.stores.domain.stock.MerchandiseClassificationIfc;
import oracle.retail.stores.domain.stock.MerchandiseHierarchy;
import oracle.retail.stores.domain.stock.MerchandiseHierarchyIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.stock.ProductGroupIfc;
import oracle.retail.stores.domain.stock.ProductIfc;
import oracle.retail.stores.domain.stock.RelatedItem;
import oracle.retail.stores.domain.stock.RelatedItemGroup;
import oracle.retail.stores.domain.stock.RelatedItemGroupIfc;
import oracle.retail.stores.domain.stock.RelatedItemIfc;
import oracle.retail.stores.domain.stock.RelatedItemSummary;
import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
import oracle.retail.stores.domain.stock.StockItemIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureIfc;
import oracle.retail.stores.domain.stock.UnknownItemIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyGroupIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyLevelKeyIfc;
import oracle.retail.stores.domain.stock.classification.MerchandiseHierarchyTreeIfc;
import oracle.retail.stores.domain.store.DepartmentIfc;
import oracle.retail.stores.domain.store.DistrictIfc;
import oracle.retail.stores.domain.store.RegionIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.supply.SupplyCategoryIfc;
import oracle.retail.stores.domain.supply.SupplyItemIfc;
import oracle.retail.stores.domain.supply.SupplyItemSearchCriteriaIfc;
import oracle.retail.stores.domain.supply.SupplyOrderIfc;
import oracle.retail.stores.domain.supply.SupplyOrderLineItemIfc;
import oracle.retail.stores.domain.tax.CappedTaxRuleIfc;
import oracle.retail.stores.domain.tax.ExciseTaxRuleIfc;
import oracle.retail.stores.domain.tax.FixedAmountTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.ItemTransactionTaxAggregator;
import oracle.retail.stores.domain.tax.ItemTransactionTaxAggregatorIfc;
import oracle.retail.stores.domain.tax.OverrideItemTaxByAmountRuleIfc;
import oracle.retail.stores.domain.tax.OverrideItemTaxByRateRuleIfc;
import oracle.retail.stores.domain.tax.OverrideItemTaxRuleIfc;
import oracle.retail.stores.domain.tax.OverrideTransactionTaxByAmountRuleIfc;
import oracle.retail.stores.domain.tax.OverrideTransactionTaxByRateRuleIfc;
import oracle.retail.stores.domain.tax.ProratedTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.ReturnTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.ReverseItemTaxRuleIfc;
import oracle.retail.stores.domain.tax.ReverseTaxCalculatorIfc;
import oracle.retail.stores.domain.tax.TableTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxEngineIfc;
import oracle.retail.stores.domain.tax.TaxExemptTaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteria;
import oracle.retail.stores.domain.tax.TaxHistorySelectionCriteriaIfc;
import oracle.retail.stores.domain.tax.TaxInformationContainerIfc;
import oracle.retail.stores.domain.tax.TaxInformationIfc;
import oracle.retail.stores.domain.tax.TaxRateCalculatorIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRuleItemContainerIfc;
import oracle.retail.stores.domain.tax.TaxTableLineItemIfc;
import oracle.retail.stores.domain.tax.ValueAddedTaxRuleIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.tender.TenderChargeIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.domain.tender.TenderDebitIfc;
import oracle.retail.stores.domain.tender.TenderDescriptorIfc;
import oracle.retail.stores.domain.tender.TenderGiftCardIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLimitsIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.tender.TenderTypeMapIfc;
import oracle.retail.stores.domain.transaction.BankDepositTransactionIfc;
import oracle.retail.stores.domain.transaction.BillPayTransactionIfc;
import oracle.retail.stores.domain.transaction.InstantCreditTransactionIfc;
import oracle.retail.stores.domain.transaction.ItemSummaryIfc;
import oracle.retail.stores.domain.transaction.LayawayPaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.LayawayTransactionIfc;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderStatusChangeTransaction;
import oracle.retail.stores.domain.transaction.OrderStatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.PaymentTransactionIfc;
import oracle.retail.stores.domain.transaction.PurgeCriteriaIfc;
import oracle.retail.stores.domain.transaction.PurgeResultIfc;
import oracle.retail.stores.domain.transaction.PurgeTransactionEntryIfc;
import oracle.retail.stores.domain.transaction.RedeemTransactionIfc;
import oracle.retail.stores.domain.transaction.RegisterOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransaction;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.StoreOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TillAdjustmentTransactionIfc;
import oracle.retail.stores.domain.transaction.TillOpenCloseTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionKeyIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.domain.transaction.TransactionTypeMapIfc;
import oracle.retail.stores.domain.transaction.VoidTransactionIfc;
import oracle.retail.stores.domain.utility.AddressBookEntry;
import oracle.retail.stores.domain.utility.AddressBookEntryIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.domain.utility.CardIfc;
import oracle.retail.stores.domain.utility.CardTypeIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.CodeListSearchCriteria;
import oracle.retail.stores.domain.utility.CodeListSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.CodeSearchCriteria;
import oracle.retail.stores.domain.utility.CodeSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.Contact;
import oracle.retail.stores.domain.utility.ContactIfc;
import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.domain.utility.CurrencyRoundingCalculatorIfc;
import oracle.retail.stores.domain.utility.CurrencyRoundingRuleSearchCriteriaIfc;
import oracle.retail.stores.domain.utility.DiscountTypeCodeEntryIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EYSStatusIfc;
import oracle.retail.stores.domain.utility.EYSTime;
import oracle.retail.stores.domain.utility.EmailAddressIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.GiftCertificateDocument;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.domain.utility.HouseCardIfc;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.domain.utility.PersonIfc;
import oracle.retail.stores.domain.utility.PersonNameIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.domain.utility.RelatedItemTransactionInfo;
import oracle.retail.stores.domain.utility.RelatedItemTransactionInfoIfc;
import oracle.retail.stores.domain.utility.RuleIfc;
import oracle.retail.stores.domain.utility.SecurityOverrideIfc;
import oracle.retail.stores.domain.utility.StateIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.domain.utility.calendar.BusinessCalendarIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelFactoryIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarLevelKeyIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodIfc;
import oracle.retail.stores.domain.utility.calendar.CalendarPeriodKeyIfc;


/**
 * This is the base domain object factory. It simply returns instances of
 * classes. It is employed through the {@link DomainGateway#getFactory()}
 * method. For instance, to invoke an instance of ItemPrice, the developer would
 * code {@code DomainGateway.getFactory().getItemPriceInstance()}.
 *
 * @see oracle.retail.stores.domain.DomainGateway
 */
public class DomainObjectFactory extends I18NDomainObjectFactory
    implements DomainObjectFactoryIfc
{
    /**
     * factory identifier
     */
    protected String factoryID = "";

    /**
     * Constructs DomainObjectFactory object.
     */
    public DomainObjectFactory()
    {
    }

    /**
     * Constructor for a factory with a given default locale
     *
     * @param locale Default locale this object should use
     */
    public DomainObjectFactory(Locale locale)
    {
        super(locale);
    }

    /**
     * Creates clone of this object.
     * @return Object clone of this object
     * @see java.lang.Object#clone()
     */
    public Object clone()
    {
        DomainObjectFactory c = new DomainObjectFactory();

        setCloneAttributes(c);

        return c;
    }

    /**
     * Determine if two objects are identical.
     *
     * @param obj object to compare with
     * @return true if the objects are identical, false otherwise
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj)
    {
         // Compare the super class first
        boolean isEqual = super.equals(obj);
        // confirm object instanceof this object
        if (obj instanceof DomainObjectFactory && isEqual)
        {
            // downcast the input object
            DomainObjectFactory c = (DomainObjectFactory) obj;
            // compare all the attributes of DomainObjectFactory
            if (Util.isObjectEqual(getFactoryID(), c.getFactoryID()))
            {
                isEqual = true;
            }
            else
            {
                isEqual = false;
            }
        }
        else
        {
            isEqual = false;
        }
        return (isEqual);
    }

    /**
     * Returns instance of ActiveJobIfc class.
     *
     * @return ActiveJobIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getActiveJobInstance()
     */
    public ActiveJobIfc getActiveJobInstance()
    {
        return getActiveJobInstance(getLocale());
    }


    /**
     * Returns instance of AddressIfc class.
     *
     * @return AddressIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAddressInstance()
     */
    public AddressIfc getAddressInstance()
    {
        return getAddressInstance(getLocale());
    }


    /**
     * Returns instance of AddressBookEntryIfc class.
     *
     * @param locale Locale to get an object for
     * @return AddressBookEntryIfc instance
     */
    public AddressBookEntryIfc getAddressBookEntryInstance()
    {
        return new AddressBookEntry();
    }

    /**
     * Returns instance of AdvancedPricingRuleIfc class.
     *
     * @return AdvancedPricingRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAdvancedPricingRuleInstance()
     */
    public AdvancedPricingRuleIfc getAdvancedPricingRuleInstance()
    {
        return getAdvancedPricingRuleInstance(getLocale());
    }

    /**
     * Returns instance of AdvancedPricingRuleSearchCriteriaIfc class.
     *
     * @return AdvancedPricingRuleSearchCriteriaIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAdvancedPricingRuleSearchCriteriaInstance()
     */
    public AdvancedPricingRuleSearchCriteriaIfc getAdvancedPricingRuleSearchCriteriaInstance()
    {
        return getAdvancedPricingRuleSearchCriteriaInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAggregateCalendarLevelInstance()
     */
    public CalendarLevelIfc getAggregateCalendarLevelInstance()
    {
        return getAggregateCalendarLevelInstance(getLocale());
    }

    /**
     * Returns instance of AlertEntryIfc class.
     *
     * @return AlertEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAlertEntryInstance()
     */
    public AlertEntryIfc getAlertEntryInstance()
    {
        return getAlertEntryInstance(getLocale());
    }

    /**
     * Returns instance of AlertListIfc class.
     *
     * @return AlertListIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAlertListInstance()
     */
    public AlertListIfc getAlertListInstance()
    {
        return getAlertListInstance(getLocale());
    }

    /**
     * Returns instance of AlterationIfc class.
     *
     * @return AlertEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAlterationInstance()
     */
    public AlterationIfc getAlterationInstance()
    {
        return getAlterationInstance(getLocale());
    }

    /**
     * Returns instance of AlterationPLUItemIfc class.
     *
     * @return AlterationPLUItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAlterationPLUItemInstance()
     */
    public AlterationPLUItemIfc getAlterationPLUItemInstance()
    {
        return getAlterationPLUItemInstance(getLocale());
    }

    /**
     * Returns instance of AssociateProductivity class.
     *
     * @return AssociateProductivityIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAssociateProductivityInstance()
     */
    public AssociateProductivityIfc getAssociateProductivityInstance()
    {
        return getAssociateProductivityInstance(getLocale());
    }

    /**
     * Returns instance of AuditEntryIfc class.
     *
     * @return AuditEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuditEntryInstance()
     */
    public AuditEntryIfc getAuditEntryInstance()
    {
        return getAuditEntryInstance(getLocale());
    }

    /**
     * Returns instance of AuditEntryIfc class.
     *
     * @param Locale locale
     * @return AuditEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuditEntryInstance()
     */
	public AuditEntryIfc getAuditEntryInstance(Locale locale) {
		 return getAuditEntryInstance(locale);
	}

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuthorizeCallReferralRequestInstance()
     */
    @Override
    public AuthorizeCallReferralRequestIfc getAuthorizeCallReferralRequestInstance()
    {
        AuthorizeCallReferralRequest callReferralRequest = new AuthorizeCallReferralRequest();
        return callReferralRequest;
    }

    /**
     * Returns an instance of authorize instant credit request
     * @return
     */
    public AuthorizeInstantCreditRequestIfc getAuthorizeInstantCreditRequestInstance()
    {
        return new AuthorizeInstantCreditRequest();
    }

    /**
     * Returns an instance of authorize instant credit request
     * @return
     */
    public AuthorizeInstantCreditResponseIfc getAuthorizeInstantCreditResponseInstance()
    {
        return new AuthorizeInstantCreditResponse();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuthorizeTransferRequestInstance()
     */
    @Override
    public AuthorizeTransferRequestIfc getAuthorizeTransferRequestInstance()
    {
        return new AuthorizeTransferRequest();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getHouseAccountPaymentRequestInstance()
     */
    @Override
    public HouseAccountPaymentRequestIfc getHouseAccountPaymentRequestInstance()
    {
        return new HouseAccountPaymentRequest();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuthorizeTransferResponseInstance()
     */
    @Override
    public AuthorizeTransferResponseIfc getAuthorizeTransferResponseInstance()
    {
        return new AuthorizeTransferResponse();
    }
    /**
     * Returns an instance of an AvailableToPromiseInventoryIfc.
     * @return the AvailableToPromiseInventoryIfc representation
     */
    public AvailableToPromiseInventoryIfc getAvailableToPromiseInventoryInstance()
    {
        return new AvailableToPromiseInventory();
    }

    /**
     * Returns instance of AvailableToPromiseInventorySearchCriteriaIfc class.
     *
     * @return AvailableToPromiseInventorySearchCriteriaIfc instance
     */
    public AvailableToPromiseInventorySearchCriteriaIfc getAvailableToPromiseInventorySearchCriteriaInstance()
    {
        return new AvailableToPromiseInventorySearchCriteria();
    }

    /**
     * Returns instance of BankDepositTransactionIfc class.
     *
     * @return BankDepositTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getBankDepositTransactionInstance()
     */
    public BankDepositTransactionIfc getBankDepositTransactionInstance()
    {
        return getBankDepositTransactionInstance(getLocale());
    }

    /**
     * Returns instance of BestDealGroupIfc class.
     *
     * @return BestDealGroupIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getBestDealGroupInstance()
     */
    public BestDealGroupIfc getBestDealGroupInstance()
    {
        return getBestDealGroupInstance(getLocale());
    }

    /**
     * Returns an instance of BillPayIfc
     */
    public BillPayIfc getBillPayInstance()
    {
        return getBillPayInstance(getLocale());
    }

    /**
     * Returns an instance of BillPay Transaction
     */
    public BillPayTransactionIfc getBillPayTransactionInstance()
    {
        return getBillPayTransactionInstance(getLocale());
    }

    /**
     * Returns instance of BusinessCalendarIfc.
     *
     * @return BusinessCalendarIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getBusinessCalendarInstance()
     */
    public BusinessCalendarIfc getBusinessCalendarInstance()
    {
        return getBusinessCalendarInstance(getLocale());
    }

    /**
     * Returns instance of CalendarLevelFactoryIfc.
     *
     * @return CalendarLevelFactoryIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCalendarLevelFactoryInstance()
     */
    public CalendarLevelFactoryIfc getCalendarLevelFactoryInstance()
    {
        return getCalendarLevelFactoryInstance(getLocale());
    }

    /**
     * Returns instance of CalendarLevelKeyIfc.
     *
     * @return CalendarLevelKeyIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCalendarLevelKeyInstance()
     */
    public CalendarLevelKeyIfc getCalendarLevelKeyInstance()
    {
        return getCalendarLevelKeyInstance(getLocale());
    }

    /**
     * Returns instance of CalendarPeriodIfc
     *
     * @return CalendarPeriodIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCalendarPeriodInstance()
     */
    public CalendarPeriodIfc getCalendarPeriodInstance()
    {
        return getCalendarPeriodInstance(getLocale());
    }

    /**
     * Returns instance of CalendarPeriodKeyIfc
     *
     * @return CalendarPeriodKeyIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCalendarPeriodKeyInstance()
     */
    public CalendarPeriodKeyIfc getCalendarPeriodKeyInstance()
    {
        return getCalendarPeriodKeyInstance(getLocale());
    }

    /**
     * Returns an instance of the CaptureCustomerIfc class.
     *
     * @return CaptureCustomerIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCaptureCustomerInstance()
     */
    public CaptureCustomerIfc getCaptureCustomerInstance()
    {
        return getCaptureCustomerInstance(getLocale());
    }

    /**
     * Returns an instance of Card class
     *
     * @return CardIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCardInstance()
     */
    public CardIfc getCardInstance()
    {
        return getCardInstance(getLocale());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuthorizeTransferRequestInstance()
     */
    @Override
    public CardTokenRequestIfc getCardTokenRequestInstance()
    {
        return new CardTokenRequest();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuthorizeTransferRequestInstance()
     */
    @Override
    public CardTokenResponseIfc getCardTokenResponseInstance()
    {
        return new CardTokenResponse();
    }

    /**
     * Returns instance of CardTypeIfc class.
     *
     * @return CardTypeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCardTypeInstance()
     */
    public CardTypeIfc getCardTypeInstance()
    {
        return getCardTypeInstance(getLocale());
    }

    /**
     * Returns instance of CardTypeIfc class.
     * @param setDefaults set to true if default card types are desired,
     * false if a more custom approach is needed
     * @return CardTypeIfc instance
     */
    public CardTypeIfc getCardTypeInstance(boolean setDefaults)
    {
        return getCardTypeInstance(setDefaults, getLocale());
    }

    /**
     * Returns instance of CodeEntryIfc class.
     *
     * @return CodeEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCodeEntryInstance()
     */
    public CodeEntryIfc getCodeEntryInstance()
    {
        return getCodeEntryInstance(getLocale());
    }

    /**
     * Returns instance of CodeListIfc class.
     *
     * @return CodeListIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCodeListInstance()
     */
    public CodeListIfc getCodeListInstance()
    {
        return getCodeListInstance(getLocale());
    }

    /**
     * Returns instance of CodeListSearchCriteriaIfc class.
     *
     * @return CodeListSearchCriteriaIfc instance
     */
    public CodeListSearchCriteriaIfc getCodeListSearchCriteriaInstance()
    {
        return new CodeListSearchCriteria();
    }

    /**
     * Returns instance of CodeSearchCriteriaIfc class.
     *
     * @return CodeSearchCriteriaIfc instance
     */
    public CodeSearchCriteriaIfc getCodeSearchCriteriaInstance()
    {
        return new CodeSearchCriteria();
    }

    /**
     * Returns instance of CountryIfc class.
     *
     * @return CountryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCountryInstance()
     */
    public CountryIfc getCountryInstance()
    {
        return getCountryInstance(getLocale());
    }

    /**
     * Returns instance of ContactIfc class.
     *
     * @return ContactIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getContactInstance()
     */
    public ContactIfc getContactInstance()
    {
        return new Contact();
    }

    /**
     * Returns instance of CurrencyTypeIfc class.
     *
     * @return CurrencyTypeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCurrencyTypeInstance()
     */
    public CurrencyTypeIfc getCurrencyTypeInstance()
    {
        return getCurrencyTypeInstance(getLocale());
    }

    /**
     * Returns instance of CurrencyTypeListIfc class.
     *
     * @return CurrencyTypeListIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCurrencyTypeListInstance()
     */
    public CurrencyTypeListIfc getCurrencyTypeListInstance()
    {
        return getCurrencyTypeListInstance(getLocale());
    }

    /**
     * Returns instance of CustomerDiscountByPercentageIfc class.
     *
     * @return CustomerDiscountByPercentageIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerDiscountByPercentageInstance()
     */
    public CustomerDiscountByPercentageIfc getCustomerDiscountByPercentageInstance()
    {
        return getCustomerDiscountByPercentageInstance(getLocale());
    }

    /**
            Returns instance of CustomerEventBabyIfc class.
            @return CustomerEventBabyIfc instance
			@see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerEventBabyInstance()
    **/
    public CustomerEventBabyIfc getCustomerEventBabyInstance()
    {
        return getCustomerEventBabyInstance(getLocale());
    }

    /**
     * Returns instance of CustomerEventSpecialIfc class.
     *
     * @return CustomerEventSpecialIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerEventSpecialInstance()
     */
    public CustomerEventSpecialIfc getCustomerEventSpecialInstance()
    {
        return getCustomerEventSpecialInstance(getLocale());
    }

    /**
     * Returns instance of CustomerEventWeddingIfc class.
     *
     * @return CustomerEventWeddingIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerEventWeddingInstance()
     */
    public CustomerEventWeddingIfc getCustomerEventWeddingInstance()
    {
        return getCustomerEventWeddingInstance(getLocale());
    }

    /**
     * Returns instance of CustomerGroupIfc class.
     *
     * @return CustomerGroupIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerGroupInstance()
     */
    public CustomerGroupIfc getCustomerGroupInstance()
    {
        return getCustomerGroupInstance(getLocale());
    }

    /**
     * Returns instance of PricingGroupIfc class.
     *
     * @return PricingGroupIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPricingGroupInstance()
     */
    public PricingGroupIfc getPricingGroupInstance()
    {
        return getPricingGroupInstance(getLocale());
    }

    /**
     * Returns instance of CustomerInfoIfc class.
     *
     * @return CustomerInfoIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerInfoInstance()
     */
    public CustomerInfoIfc getCustomerInfoInstance()
    {
        return getCustomerInfoInstance(getLocale());
    }

    /**
     * Returns instance of CustomerIfc class.
     *
     * @return CustomerIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerInstance()
     */
    public CustomerIfc getCustomerInstance()
    {
        return getCustomerInstance(getLocale());
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerInteractionRequestInstance()
     */
    @Override
    public CustomerInteractionRequestIfc getCustomerInteractionRequestInstance()
    {
        return new CustomerInteractionRequest();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerInteractionRequestInstance(RequestSubType)
     */
    @Override
    public CustomerInteractionRequestIfc getCustomerInteractionRequestInstance(RequestSubType requestSubType)
    {
        return new CustomerInteractionRequest(requestSubType);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerInteractionResponseInstance()
     */
    @Override
    public CustomerInteractionResponseIfc getCustomerInteractionResponseInstance()
    {
        return new CustomerInteractionResponse();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerInteractionResponseInstance(ResponseCode)
     */
    @Override
    public CustomerInteractionResponseIfc getCustomerInteractionResponseInstance(ResponseCode responseCode)
    {
        return new CustomerInteractionResponse(responseCode);
    }

    /**
     * Returns instance of CustomScheduleDocumentIfc class.
     *
     * @return CustomScheduleDocumentIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomScheduleDocumentInstance()
     */
    public CustomScheduleDocumentIfc getCustomScheduleDocumentInstance()
    {
        return getCustomScheduleDocumentInstance(getLocale());
    }
    /**
     * Returns instance of DailyScheduleDocumentIfc class.
     *
     * @return DailyScheduleDocumentIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDailyScheduleDocumentInstance()
     */
    public DailyScheduleDocumentIfc getDailyScheduleDocumentInstance()
    {
        return getDailyScheduleDocumentInstance(getLocale());
    }

    /**
     * Returns instance of DataReplicationBatchGeneratorIfc class.
     *
     * @return DataReplicationBatchGeneratorIfc instance
     */
    public DataReplicationBatchGeneratorIfc getDataReplicationBatchGeneratorInstance()
    {
        return getDataReplicationBatchGeneratorInstance(getLocale());
    }

    /**
     * Returns instance of DataReplicationCustomerEntryIfc class.
     *
     * @return DataReplicationCustomerEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDataReplicationCustomerEntry()
     */
    public DataReplicationCustomerEntryIfc getDataReplicationCustomerEntry()
    {
        return getDataReplicationCustomerEntryInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDayCalendarLevelInstance()
     */
    public CalendarLevelIfc getDayCalendarLevelInstance()
    {
        return getDayCalendarLevelInstance(getLocale());
    }

    /**
     * Returns instance of DepartmentActivityIfc class.
     *
     * @return DepartmentActivityIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDepartmentActivityInstance()
     */
    public DepartmentActivityIfc getDepartmentActivityInstance()
    {
        return getDepartmentActivityInstance(getLocale());
    }

    /**
     * Returns instance of DepartmentIfc class.
     *
     * @return DepartmentIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDepartmentInstance()
     */
    public DepartmentIfc getDepartmentInstance()
    {
        return getDepartmentInstance(getLocale());
    }

    /**
     * Returns instance of DiscountCalculationIfc class.
     *
     * @return DiscountCalculationIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDiscountCalculationInstance()
     */
    public DiscountCalculationIfc getDiscountCalculationInstance()
    {
        return getDiscountCalculationInstance(getLocale());
    }

    /**
     * Returns instance of DiscountListEntryIfc class.
     *
     * @return DiscountListEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDiscountListEntryInstance()
     */
    public DiscountListEntryIfc getDiscountListEntryInstance()
    {
        return getDiscountListEntryInstance(getLocale());
    }

    /**
     * Returns instance of DiscountListIfc class.
     *
     * @return DiscountListIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDiscountListInstance()
     */
    public DiscountListIfc getDiscountListInstance()
    {
        return getDiscountListInstance(getLocale());
    }

    /**
     * Returns instance of DiscountRuleIfc class.
     *
     * @return DiscountRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDiscountRuleInstance()
     */
    public DiscountRuleIfc getDiscountRuleInstance()
    {
        return getDiscountRuleInstance(getLocale());
    }

    /**
     * Returns instance of CodeEntryIfc class.
     *
     * @return CodeEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDiscountTypeCodeEntryInstance()
     */
    public DiscountTypeCodeEntryIfc getDiscountTypeCodeEntryInstance()
    {
        return getDiscountTypeCodeEntryInstance(getLocale());
    }

    /**
     * Returns instance of DistrictIfc class.
     *
     * @return DistrictIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDistrictInstance()
     */
    public DistrictIfc getDistrictInstance()
    {
        return getDistrictInstance(getLocale());
    }

    /**
     * Returns instance of DrawerIfc class.
     *
     * @return DrawerIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getDrawerInstance()
     */
    public DrawerIfc getDrawerInstance()
    {
        return getDrawerInstance(getLocale());
    }

    /**
     * Returns instance of EmailAddressIfc class.
     *
     * @return EmailAddressIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEmailAddressInstance()
     */
    public EmailAddressIfc getEmailAddressInstance()
    {
        return getEmailAddressInstance(getLocale());
    }

    /**
     * Returns instance of EMessageIfc class.
     *
     * @return EMessageIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEMessageInstance()
     */
    public EMessageIfc getEMessageInstance()
    {
        return getEMessageInstance(getLocale());
    }

    /**
     * Returns instance of EmployeeActivityIfc class.
     *
     * @return EmployeeActivityIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEmployeeActivityInstance()
     */
    public EmployeeActivityIfc getEmployeeActivityInstance()
    {
        return getEmployeeActivityInstance(getLocale());
    }

    /**
     * Returns instance of EmployeeClockEntryIfc class.
     *
     * @return EmployeeClockEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEmployeeClockEntryInstance()
     */
    public EmployeeClockEntryIfc getEmployeeClockEntryInstance()
    {
        return getEmployeeClockEntryInstance(getLocale());
    }

    /**
     * Returns instance of Employee Compliance.
     *
     * @param locale Locale to get an object for.
     * @return EmployeeComplianceIfc instance
     */
    public EmployeeComplianceIfc getEmployeeCompliance()
    {
        return getEmployeeCompliance(getLocale());
    }

    /**
     * Returns instance of EmployeeIfc class.
     *
     * @return EmployeeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEmployeeInstance()
     */
    public EmployeeIfc getEmployeeInstance()
    {
        return getEmployeeInstance(getLocale());
    }

    /**
     * Return an instance of ExciseTaxRuleIfc
     *
     * @return ExciseTaxRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getExciseTaxRuleInstance()
     */
    public ExciseTaxRuleIfc getExciseTaxRuleInstance()
    {
        return getExciseTaxRuleInstance(getLocale());
    }

    /**
     * Return an instance of CappedTaxRuleIfc
     * 
     * @return CappedTaxRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCappedTaxRuleInstance()
     */
    public CappedTaxRuleIfc getCappedTaxRuleInstance()
    {
        return getCappedTaxRuleInstance(getLocale());
    }

    /**
     * Return an instance of TableTaxRuleIfc
     * 
     * @return TableTaxRuleIfc instance
     */
    public TableTaxRuleIfc getTableTaxRuleInstance()
    {
        return getTableTaxRuleInstance(getLocale());
    }

    /**
     * Returns an instance of External order item object that contains a plu reference
     * @return
     */
    public ExternalOrderSaleItemIfc getExternalOrderSaleItemInstance()
    {
        return new ExternalOrderSaleItem();
    }

    /**
     * Returns an instance of external order send package item instance
     * @return
     */
    public ExternalOrderSendPackageItemIfc getExternalOrderSendPackageItemInstance()
    {
    	return new ExternalOrderSendPackageItem();
    }

    /**
     * Returns an instance of external order send package item instance
     * @return
     */
    public ExternalOrderSearchCriteriaIfc getExternalOrderSearchCriteriaInstance()
    {
        return new ExternalOrderSearchCriteria();
    }

    /**
     * Returns instance of EYSDate class.
     *
     * @return EYSDate instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEYSDateInstance()
     */
    public EYSDate getEYSDateInstance()
    {
        return getEYSDateInstance(getLocale());
    }

    /**
     * Returns instance of EYSStatus class.
     *
     * @return EYSStatus instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEYSStatusInstance()
     */
    public EYSStatusIfc getEYSStatusInstance()
    {
        return getEYSStatusInstance(getLocale());
    }

    /**
     * Returns instance of EYSTime class.
     *
     * @return EYSTime instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getEYSTimeInstance()
     */
    public EYSTime getEYSTimeInstance()
    {
        return getEYSTimeInstance(getLocale());
    }

    /**
     * Retrieves factory identifier.
     *
     * @return factory identifier
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getFactoryID()
     */
    public String getFactoryID()
    {
        return factoryID;
    }

    /**
     * Returns instance of FinancialCountIfc class.
     *
     * @return FinancialCountIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getFinancialCountInstance()
     */
    public FinancialCountIfc getFinancialCountInstance()
    {
        return getFinancialCountInstance(getLocale());
    }

    /**
     * Returns instance of FinancialCountTenderItemIfc class.
     *
     * @return FinancialCountTenderItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getFinancialCountTenderItemInstance()
     */
    public FinancialCountTenderItemIfc getFinancialCountTenderItemInstance()
    {
        return getFinancialCountTenderItemInstance(getLocale());
    }

    /**
     * Returns instance of FinancialTotalsIfc class.
     *
     * @return FinancialTotalsIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getFinancialTotalsInstance()
     */
    public FinancialTotalsIfc getFinancialTotalsInstance()
    {
        return getFinancialTotalsInstance(getLocale());
    }

    /**
     * Returns an instance of FixedAmountTaxCalculatorIfc
     *
     * @return FixedAmountTaxCalculatorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getFixedAmountTaxCalculatorInstance()
     */
    public FixedAmountTaxCalculatorIfc getFixedAmountTaxCalculatorInstance()
    {
        return getFixedAmountTaxCalculatorInstance(getLocale());
    }

    /**
     * Returns instance of GiftCardIfc class.
     *
     * @return GiftCardIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getGiftCardInstance()
     */
    public GiftCardIfc getGiftCardInstance()
    {
        return getGiftCardInstance(getLocale());
    }

    /**
     * Returns instance of GiftCardPLUItemIfc class.
     *
     * @return GiftCardPLUItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getGiftCardPLUItemInstance()
     */
    public GiftCardPLUItemIfc getGiftCardPLUItemInstance()
    {
        return getGiftCardPLUItemInstance(getLocale());
    }

    /**
     * Returns instance of GiftCertificateItemIfc class.
     *
     * @return GiftCertificateItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getGiftCertificateItemInstance()
     */
    public GiftCertificateItemIfc getGiftCertificateItemInstance()
    {
        return getGiftCertificateItemInstance(getLocale());
    }

    /**
     * Returns instance of GiftRegistryIfc class.
     *
     * @return GiftRegistryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getGiftRegistryInstance()
     */
    public GiftRegistryIfc getGiftRegistryInstance()
    {
        return getGiftRegistryInstance(getLocale());
    }

    /**
     * Returns instance of GiftCertificateDocumentIfc class.
     *
     * @return GiftCertificateItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getGiftCertificateItemInstance()
     */
    public GiftCertificateDocumentIfc getGiftCertificateDocumentInstance()
    {
        return new GiftCertificateDocument();
    }

    /**
     * Returns instance of HardTotalsBuilderIfc class.
     *
     * @return HardTotalsBuilderIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getHardTotalsBuilderInstance()
     */
    public HardTotalsBuilderIfc getHardTotalsBuilderInstance()
    {
        return getHardTotalsBuilderInstance(getLocale());
    }

    /**
     * Returns instance of HardTotalsIfc class.
     *
     * @return HardTotalsIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getHardTotalsInstance()
     */
    public HardTotalsIfc getHardTotalsInstance()
    {
        return getHardTotalsInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getHourCalendarLevelInstance()
     */
    public CalendarLevelIfc getHourCalendarLevelInstance()
    {
        return getHourCalendarLevelInstance(getLocale());
    }

    /**
     * Returns instance of HouseCardIfc class.
     *
     * @return HouseCardIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getHouseCardInstance()
     */
    public HouseCardIfc getHouseCardInstance()
    {
        return getHouseCardInstance(getLocale());
    }

    /**
     * Returns instance of InstantCreditIfc class.
     *
     * @return instance of InstantCreditIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getInstantCreditInstance()
     */
    public InstantCreditIfc getInstantCreditInstance()
    {
        return getInstantCreditInstance(getLocale());
    }

    /**
     * Returns instance of InstantCreditTransactionIfc class.
     *
     * @return instance of InstantCreditTransactionIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getInstantCreditTransactionInstance()
     */
    public InstantCreditTransactionIfc getInstantCreditTransactionInstance()
    {
        return getInstantCreditTransactionInstance(getLocale());
    }

    /**
     * Returns an instance of the IRSCustomerIfc class.
     * @return IRSCustomerIfc instance
     */
    public IRSCustomerIfc getIRSCustomerInstance()
    {
        return (new IRSCustomer());
    }




    /**
     * Returns instance of ItemClassificationIfc class.
     *
     * @return ItemClassificationIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemClassificationInstance()
     */
    public ItemClassificationIfc getItemClassificationInstance()
    {
        return getItemClassificationInstance(getLocale());
    }

    /**
     * Returns instance of ItemColorIfc class.
     *
     * @return ItemColorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemColorInstance()
     */
    public ItemColorIfc getItemColorInstance()
    {
        return getItemColorInstance(getLocale());
    }

    /**
     * Returns instance of ItemContainerProxyIfc class.
     *
     * @return ItemContainerProxyIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemContainerProxyInstance()
     */
    public ItemContainerProxyIfc getItemContainerProxyInstance()
    {
        return getItemContainerProxyInstance(getLocale());
    }

    /**
     * Returns instance of ItemDiscountByAmountIfc class.
     *
     * @return ItemDiscountByAmountIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemDiscountByAmountInstance()
     */
    public ItemDiscountByAmountIfc getItemDiscountByAmountInstance()
    {
        return getItemDiscountByAmountInstance(getLocale());
    }

    /**
     * Returns instance of ItemDiscountByFixedPriceStrategy class.
     *
     * @return ItemDiscountByAmountIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemDiscountByFixedPriceStrategyInstance()
     */
    public ItemDiscountByAmountIfc getItemDiscountByFixedPriceStrategyInstance()
    {
        return getItemDiscountByFixedPriceStrategyInstance(getLocale());
    }

    /**
     * Returns instance of ItemDiscountByPercentageIfc class.
     *
     * @return ItemDiscountByPercentageIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemDiscountByPercentageInstance()
     */
    public ItemDiscountByPercentageIfc getItemDiscountByPercentageInstance()
    {
        return getItemDiscountByPercentageInstance(getLocale());
    }

    /**
     * Returns instance of ItemDiscountAuditIfc class.
     *
     * @return ItemDiscountAuditIfc instance
     */
    public ItemDiscountAuditIfc getItemDiscountAuditInstance()
    {
        return getItemDiscountAuditInstance(getLocale());
    }


    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemImageInstance()
     */
    public ItemImageIfc getItemImageInstance()
    {
        return getItemImageInstance(getLocale());
    }

    /**
     * Returns instance of ItemInfoIfc class.
     *
     * @return ItemInfoIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemInfoInstance()
     */
    public ItemInfoIfc getItemInfoInstance()
    {
        return getItemInfoInstance(getLocale());
    }


    /**
     * Returns instance of ItemInquirySearchCriteriaIfc class.
     *
     * @return ItemInquirySearchCriteriaIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemInquirySearchCriteriaInstance()
     */
    public ItemInquirySearchCriteriaIfc getItemInquirySearchCriteriaInstance()
    {
        return getItemInquirySearchCriteriaInstance(getLocale());
    }

    /**
     * Returns instance of ItemIfc class.
     *
     * @return ItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemInstance()
     */
    public ItemIfc getItemInstance()
    {
        return getItemInstance(getLocale());
    }

    /**
     * Returns instance of ItemKitIfc class.
     *
     * @return ItemKitIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemKitInstance()
     */
    public ItemKitIfc getItemKitInstance()
    {
        return getItemKitInstance(getLocale());
    }

    /**
     * Returns instance of ItemMaintenanceEventIfc class.
     *
     * @return ItemMaintenanceEventIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemMaintenanceEventInstance()
     */
    public ItemMaintenanceEventIfc getItemMaintenanceEventInstance()
    {
        return getItemMaintenanceEventInstance(getLocale());
    }

    /**
     * Returns instance of ItemPriceIfc class.
     *
     * @return ItemPriceIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemPriceInstance()
     */
    public ItemPriceIfc getItemPriceInstance()
    {
        return getItemPriceInstance(getLocale());
    }

    /**
     * Returns instance of ItemPriceMaintenanceEventIfc class.
     *
     * @return ItemPriceMaintenanceEventIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemPriceMaintenanceEventInstance()
     */
    public ItemPriceMaintenanceEventIfc getItemPriceMaintenanceEventInstance()
    {
        return getItemPriceMaintenanceEventInstance(getLocale());
    }

    /**
     * Returns instance of ItemSizeIfc class.
     *
     * @return ItemSizeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemSizeInstance()
     */
    public ItemSizeIfc getItemSizeInstance()
    {
        return getItemSizeInstance(getLocale());
    }

    /**
     * Returns instance of ItemStyleIfc class.
     *
     * @return ItemStyleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemStyleInstance()
     */
    public ItemStyleIfc getItemStyleInstance()
    {
        return getItemStyleInstance(getLocale());
    }

    /**
     * Returns instance of ItemSummaryIfc class.
     *
     * @return ItemSummaryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemSummaryInstance()
     */
    public ItemSummaryIfc getItemSummaryInstance()
    {
        return getItemSummaryInstance(getLocale());
    }

    /**
     * Returns instance of ItemTaxIfc class.
     *
     * @return ItemTaxIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemTaxInstance()
     */
    public ItemTaxIfc getItemTaxInstance()
    {
        return getItemTaxInstance(getLocale());
    }

    /**
     * Returns instance of ItemTransactionDiscountAuditIfc class.
     *
     * @return ItemTransactionDiscountAuditIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemTransactionDiscountAuditInstance()
     */
    public ItemTransactionDiscountAuditIfc getItemTransactionDiscountAuditInstance()
    {
        return getItemTransactionDiscountAuditInstance(getLocale());
    }

    /**
     * Returns instance of ItemTypeIfc class.
     *
     * @return ItemTypeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getItemColorInstance()
     */
    public ItemTypeIfc getItemTypeInstance()
    {
        return getItemTypeInstance(getLocale());
    }

    /**
     * Returns instance of JobControlEventMessageIfc class.
     *
     * @return JobControlEventMessageIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getJobControlEventMessageInstance()
     */
    public JobControlEventMessageIfc getJobControlEventMessageInstance()
    {
        return getJobControlEventMessageInstance(getLocale());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCustomerInteractionResponseInstance()
     */
    @Override
    public KeyManagementResponseIfc getKeyManagementResponseInstance()
    {
        return new KeyManagementResponse();
    }

    /**
     * Returns instance of KitComponentIfc class.
     *
     * @return KitComponentIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getKitComponentInstance()
     */
    public KitComponentIfc getKitComponentInstance()
    {
        return getKitComponentInstance(getLocale());
    }

    /**
     * Returns instance of KitComponentLineItemIfc.
     *
     * @return KitComponentLineItemIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getKitComponentLineItemInstance()
     */
    public KitComponentLineItemIfc getKitComponentLineItemInstance()
    {
        return getKitComponentLineItemInstance(getLocale());
    }

    /**
     * Returns instance of KitHeaderLineItemIfc.
     *
     * @return KitHeaderLineItemIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getKitHeaderLineItemInstance()
     */
    public KitHeaderLineItemIfc getKitHeaderLineItemInstance()
    {
        return getKitHeaderLineItemInstance(getLocale());
    }

    /**
     * Returns instance of LayawayIfc class.
     *
     * @return LayawayIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getLayawayInstance()
     */
    public LayawayIfc getLayawayInstance()
    {
        return getLayawayInstance(getLocale());
    }

    /**
     * Returns instance of LayawayPaymentTransactionIfc class.
     *
     * @return LayawayPaymentTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getLayawayPaymentTransactionInstance()
     */
    public LayawayPaymentTransactionIfc getLayawayPaymentTransactionInstance()
    {
        return getLayawayPaymentTransactionInstance(getLocale());
    }

    /**
     * Returns instance of LayawaySummaryEntryIfc class.
     *
     * @return LayawaySummaryEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getLayawaySummaryEntryInstance()
     */
    public LayawaySummaryEntryIfc getLayawaySummaryEntryInstance()
    {
        return getLayawaySummaryEntryInstance(getLocale());
    }

    /**
     * Returns instance of LayawayTransactionIfc class.
     *
     * @return LayawayTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getLayawayTransactionInstance()
     */
    public LayawayTransactionIfc getLayawayTransactionInstance()
    {
        return getLayawayTransactionInstance(getLocale());
    }

    /**
     * Returns an instance of the LegalDocument class
     * @return
     */
    public LegalDocumentIfc getLegalDocumentInstance()
    {
        return new LegalDocument();
    }

    /**
     * Returns a initialized instance of LocalizedCodeIfc class.
     *
     * @return LocalizedTextIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getLocalizedText()
     */
    public LocalizedCodeIfc getLocalizedCode()
    {
        return getLocalizedCode(null);
    }

    /**
     * Returns instance of LocalizedCodeIfc class with the code value set. If
     * <code>code</code> is null, the returned value is UNDEFINED.
     *
     * @param code the code to set onto the returned value.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getLocalizedCode()
     */
    public LocalizedCodeIfc getLocalizedCode(String code)
    {
        LocalizedCodeIfc localizedCode = new LocalizedCode();
        localizedCode.setText(getLocalizedText());
        if (code != null)
        {
            localizedCode.setCode(code);
        }
        return localizedCode;
    }

    /**
     * Returns instance of LocalizedTextIfc class.
     *
     * @return LocalizedTextIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getLocalizedText()
     */
    public LocalizedTextIfc getLocalizedText()
    {
        return getLocalizedText(getLocale());
    }

    /**
     * Returns instance of MaintenanceEventIfc class.
     *
     * @return MaintenanceEventIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMaintenanceEventInstance()
     */
    public MaintenanceEventIfc getMaintenanceEventInstance()
    {
        return getMaintenanceEventInstance(getLocale());
    }

    /**
     * Returns instance of ManufacturerIfc class.
     *
     * @return ManufacturerIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getManufacturerInstance()
     */
    public ManufacturerIfc getManufacturerInstance()
    {
        return getManufacturerInstance(getLocale());
    }

    /**
     * Returns instance of MerchandiseClassificationIfc class.
     *
     * @return MerchandiseClassificationIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMerchandiseClassificationInstance()
     */
    public MerchandiseClassificationIfc getMerchandiseClassificationInstance()
    {
        return getMerchandiseClassificationInstance(getLocale());
    }

    /**
     * Returns instance of MerchandiseHierarchyGroupIfc
     *
     * @return MerchandiseHierarchyGroupIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMerchandiseHierarchyGroupInstance()
     */
    public MerchandiseHierarchyGroupIfc getMerchandiseHierarchyGroupInstance()
    {
        return getMerchandiseHierarchyGroupInstance(getLocale());
    }

    /**
     * Returns instance of MerchandiseHierarchyIfc class.
     *
     * @return MerchandiseHierarchyIfc instance
     * @deprecated As of release 5.0.0, replaced by {@link #getMerchandiseClassificationInstance()}
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMerchandiseHierarchyInstance()
     */
    public MerchandiseHierarchyIfc getMerchandiseHierarchyInstance()
    {
        return new MerchandiseHierarchy();
    }

    /**
     * Returns instance of MerchandiseHierarchyLevelIfc.
     *
     * @return MerchandiseHierarchyLevelIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMerchandiseHierarchyLevelInstance()
     */
    public MerchandiseHierarchyLevelIfc getMerchandiseHierarchyLevelInstance()
    {
        return getMerchandiseHierarchyLevelInstance(getLocale());
    }

    /**
     * Returns instance of MerchandiseHierarchyLevelKeyIfc.
     *
     * @return MerchandiseHierarchyLevelKeyIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMerchandiseHierarchyLevelKeyInstance()
     */
    public MerchandiseHierarchyLevelKeyIfc getMerchandiseHierarchyLevelKeyInstance()
    {
        return getMerchandiseHierarchyLevelKeyInstance(getLocale());
    }

    /**
     * Returns instance of MerchandiseHierarchyTreeIfc.
     *
     * @return MerchandiseHierarchyTreeIfc instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMerchandiseHierarchyTreeInstance()
     */
    public MerchandiseHierarchyTreeIfc getMerchandiseHierarchyTreeInstance()
    {
        return getMerchandiseHierarchyTreeInstance(getLocale());
    }

    /**
     * Returns instance of MerchandisePreferenceIfc class.
     *
     * @return MerchandisePreferenceIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMerchandisePreferenceInstance()
     */
    public MerchandisePreferenceIfc getMerchandisePreferenceInstance()
    {
        return getMerchandisePreferenceInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMinuteCalendarLevelInstance()
     */
    public CalendarLevelIfc getMinuteCalendarLevelInstance()
    {
        return getMinuteCalendarLevelInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMonthDayCalendarLevelInstance()
     */
    public CalendarLevelIfc getMonthDayCalendarLevelInstance()
    {
        return getMonthDayCalendarLevelInstance(getLocale());
    }

    /**
     * Returns instance of MonthlyByDayScheduleDocumentIfc class.
     *
     * @return MonthlyByDayScheduleDocumentIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getMonthlyByDayScheduleDocumentInstance()
     */
    public MonthlyByDayScheduleDocumentIfc getMonthlyByDayScheduleDocumentInstance()
    {
        return getMonthlyByDayScheduleDocumentInstance(getLocale());
    }

    /**
     * Returns instance of NoSaleTransactionIfc class.
     *
     * @return NoSaleTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getNoSaleTransactionInstance()
     */
    public NoSaleTransactionIfc getNoSaleTransactionInstance()
    {
        return getNoSaleTransactionInstance(getLocale());
    }

    /**
     * Returns instance of NotificationRecipientsIfc class.
     *
     * @return NotificationRecipientsIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getNotificationRecipientsInstance()
     */
    public NotificationRecipientsIfc getNotificationRecipientsInstance()
    {
        return getNotificationRecipientsInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getNthWeekDayCalendarLevelInstance()
     */
    public CalendarLevelIfc getNthWeekDayCalendarLevelInstance()
    {
        return getNthWeekDayCalendarLevelInstance(getLocale());
    }



    /**
     * Returns instance of OrderIfc class.
     *
     * @return OrderIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderInstance()
     */
    public OrderIfc getOrderInstance()
    {
        return getOrderInstance(getLocale());
    }

    /**
     * Returns instance of OrderSearchCriteriaIfc
     * @return OrderSearchCriteriaIfc instance
     */
    public OrderSearchCriteriaIfc getOrderSearchCriteriaInstance()
    {
        return new OrderSearchCriteria();
    }

    /**
     * Returns instance of OrderItemStatusIfc class.
     *
     * @return OrderItemStatusIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderItemStatusInstance()
     */
    public OrderItemStatusIfc getOrderItemStatusInstance()
    {
        return getOrderItemStatusInstance(getLocale());
    }

    /**
     * Returns instance of OrderLineItemIfc class.
     *
     * @return OrderLineItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderLineItemInstance()
     */
    public OrderLineItemIfc getOrderLineItemInstance()
    {
        return getOrderLineItemInstance(getLocale());
    }

    /**
     * Returns instance of OrderRecipientIfc class.
     *
     * @return OrderRecipientIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderRecipientInstance()
     */
    public OrderRecipientIfc getOrderRecipientInstance()
    {
        return getOrderRecipientInstance(getLocale());
    }

    /**
     * Returns instance of OrderStatusIfc class.
     *
     * @return OrderStatusIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderStatusInstance()
     */
    public OrderStatusIfc getOrderStatusInstance()
    {
        return getOrderStatusInstance(getLocale());
    }

    /**
     * Returns instance of OrderSummaryEntryIfc class.
     *
     * @return OrderSummaryEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderSummaryEntryInstance()
     */
    public OrderSummaryEntryIfc getOrderSummaryEntryInstance()
    {
        return getOrderSummaryEntryInstance(getLocale());
    }

    /**
     * Returns instance of OrderDeliveryDetailIfc class.
     *
     * @return OrderTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderDeliveryDetailInstance()
     */
    public OrderDeliveryDetailIfc getOrderDeliveryDetailInstance()
    {
        return getOrderDeliveryDetailInstance(getLocale());
    }

    /**
     * Returns instance of OrderTransactionIfc class.
     *
     * @return OrderTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderTransactionInstance()
     */
    public OrderTransactionIfc getOrderTransactionInstance()
    {
        return getOrderTransactionInstance(getLocale());
    }

    /**
     * Returns an instance of OverrideItemTaxByAmountRuleIfc.
     *
     * @return OverrideItemTaxByAmountRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOverrideItemTaxByAmountRuleInstance()
     */
    public OverrideItemTaxByAmountRuleIfc getOverrideItemTaxByAmountRuleInstance()
    {
        return getOverrideItemTaxByAmountRuleInstance(getLocale());
    }

    /**
     * Return an instance of OverrideItemTaxByRateRuleIfc
     *
     * @return OverrideItemTaxByRateRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOverrideItemTaxByRateRuleInstance()
     */
    public OverrideItemTaxByRateRuleIfc getOverrideItemTaxByRateRuleInstance()
    {
        return getOverrideItemTaxByRateRuleInstance(getLocale());
    }

    /**
     * Return an instance of Toggle Item Tax Off rule
     *
     * @return OverrideItemTaxRuleIfc type that toggles tax Off
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOverrideItemTaxToggleOffRuleInstance()
     */
    public OverrideItemTaxRuleIfc getOverrideItemTaxToggleOffRuleInstance()
    {
        return getOverrideItemTaxToggleOffRuleInstance(getLocale());
    }

    /**
     * Returns an instance of OverrideTransactionTaxByAmountRuleIfc.
     *
     * @return OverrirdeTransactionTaxByAmountRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOverrideTransactionTaxByAmountRuleInstance()
     */
    public OverrideTransactionTaxByAmountRuleIfc getOverrideTransactionTaxByAmountRuleInstance()
    {
        return getOverrideTransactionTaxByAmountRuleInstance(getLocale());
    }

    /**
     * Returns an instance of OverrideTransactionTaxByRateRuleIfc
     *
     * @return OverrideTransactionTaxByRateRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOverrideTransactionTaxByRateRuleInstance()
     */
    public OverrideTransactionTaxByRateRuleIfc getOverrideTransactionTaxByRateRuleInstance()
    {
        return getOverrideTransactionTaxByRateRuleInstance(getLocale());
    }

    /**
     * Returns instance of Password Policy Evaluator.
     * @return PasswordPolicyEvaluatorIfc instance
     */
    public PasswordPolicyEvaluatorIfc getPasswordPolicyEvaluatorInstance()
    {
        return getPasswordPolicyEvaluatorInstance(getLocale());
    }

    /**
     * Returns instance of the PaymentHistoryInfoIfc implementation
     * @return PaymentHistoryInfoIfc
     * @since NEP67
     */
    public PaymentHistoryInfoIfc getPaymentHistoryInfoInstance()
    {
        return new PaymentHistoryInfo();
    }

    /**
     * Returns instance of PaymentIfc class.
     *
     * @return PaymentIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPaymentInstance()
     */
    public PaymentIfc getPaymentInstance()
    {
        return getPaymentInstance(getLocale());
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStatusResponseInstance()
     */
    @Override
    public StatusResponseIfc getPaymentServiceStatusResponseInstance()
    {
        return new StatusResponse();
    }

    /**
     * Returns instance of PaymentTransactionIfc class.
     *
     * @return PaymentTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPaymentTransactionInstance()
     */
    public PaymentTransactionIfc getPaymentTransactionInstance()
    {
        return getPaymentTransactionInstance(getLocale());
    }

    /**
     * Returns instance of PersonIfc class.
     *
     * @return PersonIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPersonInstance()
     */
    public PersonIfc getPersonInstance()
    {
        return getPersonInstance(getLocale());
    }

    /**
     * Returns instance of PersonNameIfc class.
     *
     * @return PersonNameIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPersonNameInstance()
     */
    public PersonNameIfc getPersonNameInstance()
    {
        return getPersonNameInstance(getLocale());
    }

    /**
     * Returns instance of PhoneIfc class.
     *
     * @return PhoneIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPhoneInstance()
     */
    public PhoneIfc getPhoneInstance()
    {
        return getPhoneInstance(getLocale());
    }

    /**
     * Returns instance of ISDITKEncryptionUtilityIfc class.
     * @return ISDITKEncryptionUtilityIfc
     */
    public PinCommEncryptionUtilityIfc getPinCommEncryptionUtilityInstance()
    {
        return new PinCommEncryptionUtility();
    }

    /**
     * Returns instance of PLUItemIfc class.
     *
     * @return PLUItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPLUItemInstance()
     */
    public PLUItemIfc getPLUItemInstance()
    {
        return getPLUItemInstance(getLocale());
    }

    /**
     * Returns instance of POSLogBatchGeneratorIfc class.
     *
     * @return POSLogBatchGeneratorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPOSLogBatchGeneratorInstance()
     */
    public POSLogBatchGeneratorIfc getPOSLogBatchGeneratorInstance()
    {
        return getPOSLogBatchGeneratorInstance(getLocale());
    }

    /**
     * Returns instance of POSLogTransactionEntryIfc class.
     *
     * @return POSLogTransactionEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPOSLogTransactionEntryInstance()
     */
    public POSLogTransactionEntryIfc getPOSLogTransactionEntryInstance()
    {
        return getPOSLogTransactionEntryInstance(getLocale());
    }
    
    /**
     * Returns instance of PriceAdjustmentLineItemIfc class.
     *
     * @return PriceAdjustmentLineItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPriceAdjustmentLineItemInstance()
     */
    public PriceAdjustmentLineItemIfc getPriceAdjustmentLineItemInstance()
    {
        return getPriceAdjustmentLineItemInstance(getLocale());
    }


    /**
     * Returns instance of PriceChangeIfc class.
     *
     * @return PriceChangeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPriceChangeInstance()
     */
    public PriceChangeIfc getPriceChangeInstance()
    {
        return getPriceChangeInstance(getLocale());
    }

    /**
     * Returns instance of PriceDerivationRuleMaintenanceEventIfc class.
     *
     * @return PriceDerivationRuleMaintenanceEventIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPriceDerivationRuleMaintenanceEventInstance()
     */
    public PriceDerivationRuleMaintenanceEventIfc getPriceDerivationRuleMaintenanceEventInstance()
    {
        return getPriceDerivationRuleMaintenanceEventInstance(getLocale());
    }

    /**
     * Returns instance of ProductGroupIfc class.
     *
     * @return ProductGroupIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getProductGroupInstance()
     */
    public ProductGroupIfc getProductGroupInstance()
    {
        return getProductGroupInstance(getLocale());
    }

    /**
     * Returns instance of ProductIfc class.
     *
     * @return ProductIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getProductInstance()
     * @deprecated 02JUL2007 Concept of Product has been replaced by Merchandise Hierarchy.
     */
    public ProductIfc getProductInstance()
    {
        return getProductInstance(getLocale());
    }

    /**
     * Returns instance of PromotionLineItemIfc Class
     * @return PromotionLineItemIfc
     */
    public PromotionLineItemIfc getPromotionLineItemInstance()
    {
        return new PromotionLineItem();
    }

    /**
     * Returns instance of PurchaseOrderIfc class.
     *
     * @return PurchaseOrderIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPurchaseOrderInstance()
     */
    public PurchaseOrderIfc getPurchaseOrderInstance()
    {
        return getPurchaseOrderInstance(getLocale());
    }

    /**
     * Returns instance of PurchaseOrderLineItemIfc class.
     *
     * @return PurchaseOrderLineItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPurchaseOrderLineItemInstance()
     */
    public PurchaseOrderLineItemIfc getPurchaseOrderLineItemInstance()
    {
        return getPurchaseOrderLineItemInstance(getLocale());
    }

    /**
     * Returns instance of PurgeCriteriaIfc class.
     *
     * @return PurgeCriteriaIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPurgeCriteriaInstance()
     */
    public PurgeCriteriaIfc getPurgeCriteriaInstance()
    {
        return getPurgeCriteriaInstance(getLocale());
    }

    /**
     * Returns instance of PurgeResultIfc class.
     *
     * @return PurgeResultIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPurgeResultInstance()
     */
    public PurgeResultIfc getPurgeResultInstance()
    {
        return getPurgeResultInstance(getLocale());
    }

    /**
     * Returns instance of PurgeTransactionEntryIfc class.
     *
     * @return PurgeTransactionEntryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getPurgeTransactionEntryInstance()
     */
    public PurgeTransactionEntryIfc getPurgeTransactionEntryInstance()
    {
        return getPurgeTransactionEntryInstance(getLocale());
    }

    /**
     * Returns instance of ReconcilableCountIfc class.
     *
     * @return ReconcilableCountIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReconcilableCountInstance()
     */
    public ReconcilableCountIfc getReconcilableCountInstance()
    {
        return getReconcilableCountInstance(getLocale());
    }

    /**
     * Returns instance of VoidTransactionIfc class.
     *
     * @return VoidTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRedeemTransactionInstance()
     */
    public RedeemTransactionIfc getRedeemTransactionInstance()
    {
        return getRedeemTransactionInstance(getLocale());
    }

    /**
     * Returns instance of RegionIfc class.
     *
     * @return RegionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRegionInstance()
     */
    public RegionIfc getRegionInstance()
    {
        return getRegionInstance(getLocale());
    }

    /**
     * Returns instance of RegisterIfc class.
     *
     * @return RegisterIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRegisterInstance()
     */
    public RegisterIfc getRegisterInstance()
    {
        return getRegisterInstance(getLocale());
    }

    /**
     * Returns instance of RegisterOpenCloseTransactionIfc class.
     *
     * @return RegisterOpenCloseTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRegisterOpenCloseTransactionInstance()
     */
    public RegisterOpenCloseTransactionIfc getRegisterOpenCloseTransactionInstance()
    {
        return getRegisterOpenCloseTransactionInstance(getLocale());
    }

    /**
     * Returns instance of RegistryIDIfc class.
     *
     * @return RegistryIDIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRegistryIDInstance()
     */
    public RegistryIDIfc getRegistryIDInstance()
    {
        return getRegistryIDInstance(getLocale());
    }

    /**
     * Return an implementation of the RelatedItemGroupIfc
     *
     * @return RelatedItemGroupIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRelatedItemGroupInstance()
     * @since NEP67
     */
    public RelatedItemGroupIfc getRelatedItemGroupInstance()
    {
        return new RelatedItemGroup();
    }

    /**
     * Return an implementation of the RelatedItemIfc
     *
     * @return RelatedItemIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRelatedItemInstance()
     * @since NEP67
     */
    public RelatedItemIfc getRelatedItemInstance()
    {
        return new RelatedItem();
    }

    /**
     * Return an implementation of the RelatedItemSummaryIfc
     *
     * @return RelatedItemSummaryIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRelatedItemSummaryInstance()
     * @since NEP67
     */
    public RelatedItemSummaryIfc getRelatedItemSummaryInstance()
    {
        return new RelatedItemSummary();
    }

    /**
     * Returns an implementation of the RelatedItemTransactionInfoIfc.
     *
     * @return RelatedItemTransactionInfoIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRelatedItemTransactionInfoInstance()
     */
    public RelatedItemTransactionInfoIfc getRelatedItemTransactionInfoInstance()
    {
        return new RelatedItemTransactionInfo();
    }

    /**
     * Returns instance of ReportBean (implementing ReportBeanIfc)
     *
     * @return ReportBean instance.
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReportBeanInstance()
     */
    public ReportBeanIfc getReportBeanInstance()
    {
        return getReportBeanInstance(getLocale());
    }
    /**
     * Returns instance of ReportingPeriodIfc class.
     *
     * @return ReportingPeriodIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReportingPeriodInstance()
     */
    public ReportingPeriodIfc getReportingPeriodInstance()
    {
        return getReportingPeriodInstance(getLocale());
    }

    /**
     * Returns instance of ReturnItemIfc class.
     *
     * @return ReturnItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReturnItemInstance()
     */
    public ReturnItemIfc getReturnItemInstance()
    {
        return getReturnItemInstance(getLocale());
    }

    /**
     * Return an instance of ReverseItemTaxRuleIfc
     *
     * @return ReverseItemTaxRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReturnItemTaxRuleInstance()
     */
    public ReverseItemTaxRuleIfc getReturnItemTaxRuleInstance()
    {
        return getReturnItemTaxRuleInstance(getLocale());
    }

    /**
     * Returns instance of ReturnItemTransactionDiscountAuditIfc class.
     *
     * @return ReturnItemTransactionDiscountAuditIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReturnItemTransactionDiscountAuditInstance()
     */
    public ReturnItemTransactionDiscountAuditIfc getReturnItemTransactionDiscountAuditInstance()
    {
        return getReturnItemTransactionDiscountAuditInstance(getLocale());
    }

    /**
     * Returns instance of ReturnResponseLineItemIfc class.
     *
     * @return ReturnResponseLineItemIfc instance
     */
    public ReturnResponseLineItemIfc getReturnResponseLineItemInstance()
    {
        return getReturnResponseLineItemInstance(getLocale());
    }

    /**
     * Return an instance of the calculator used in returns
     *
     * @return ReturnTaxCalculatorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReturnTaxCalculatorInstance()
     */
    public ReturnTaxCalculatorIfc getReturnTaxCalculatorInstance()
    {
        return getReturnTaxCalculatorInstance(getLocale());
    }

    /**
     * Returns instance of ReturnTenderDataContainer class.
     *
     * @return ReturnTenderDataContainer instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReturnTenderDataContainerInstance()
     */
    public ReturnTenderDataContainer getReturnTenderDataContainerInstance()
    {
        return getReturnTenderDataContainerInstance(getLocale());
    }

    /**
     * Returns instance of ReturnTenderDataElement class.
     *
     * @return ReturnTenderDataElement instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReturnTenderDataElementInstance()
     */
    public ReturnTenderDataElement getReturnTenderDataElementInstance()
    {
        return getReturnTenderDataElementInstance(getLocale());
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReversalRequestInstance()
     */
    @Override
    public ReversalRequestIfc getReversalRequestInstance()
    {
        ReversalRequestIfc reversalRequest = new ReversalRequest();
        return reversalRequest;
    }

    /**
     * Return an instance of the calculator used in reverse
     * transactions other than returns
     *
     * @return ReverseTaxCalculatorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getReverseTaxCalculatorInstance()
     */
    public ReverseTaxCalculatorIfc getReverseTaxCalculatorInstance()
    {
        return getReverseTaxCalculatorInstance(getLocale());
    }

    /**
     * Return an instance of the calculator used in proration
     * transactions other than returns
     *
     * @return ProratedTaxCalculatorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getProratedTaxCalculatorInstance()
     */
    public ProratedTaxCalculatorIfc getProratedTaxCalculatorInstance()
    {
        return getProratedTaxCalculatorInstance(getLocale());
    }

    /**
     * Returns instance of RoleFunctionGroupIfc class.
     *
     * @return RoleFunctionGroupIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRoleFunctionGroupInstance()
     */
    public RoleFunctionGroupIfc getRoleFunctionGroupInstance()
    {
        return getRoleFunctionGroupInstance(getLocale());
    }

    /**
     * Returns instance of RoleFunctionIfc class.
     *
     * @return RoleFunctionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRoleFunctionInstance()
     */
    public RoleFunctionIfc getRoleFunctionInstance()
    {
        return getRoleFunctionInstance(getLocale());
    }

    /**
     * Returns instance of RoleIfc class.
     *
     * @return RoleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRoleInstance()
     */
    public RoleIfc getRoleInstance()
    {
        return getRoleInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRootCalendarLevelInstance()
     */
    public CalendarLevelIfc getRootCalendarLevelInstance()
    {
        return getRootCalendarLevelInstance(getLocale());
    }

    /**
     * Returns an implementation of the RTLogExportBatchGeneratorIfc.
     * @return RTLogExportBatchGeneratorIfc
     */
    public RTLogExportBatchGeneratorIfc getRTLogBatchGeneratorInstance(int type)
    {
        if (type == POSLogTransactionEntryIfc.USE_SIMTLOG_ID)
        {
            return new SIMTLogExportBatchGenerator();
        }
        else
        {
            return new RTLogExportBatchGenerator();
        }
    }

    
    /**
     * Returns an instance of RuleBinRange class
     *
     * @return RuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRuleBinRangeInstance()
     */
    public RuleIfc getRuleBinRangeInstance()
    {
        return getRuleBinRangeInstance(getLocale());
    }

    /**
     * Returns an instance of RuleLength class
     *
     * @return RuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRuleLengthInstance()
     */
    public RuleIfc getRuleLengthInstance()
    {
        return getRuleLengthInstance(getLocale());
    }

    /**
     * Returns an instance of RuleMask class
     *
     * @return RuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getRuleMaskInstance()
     */
    public RuleIfc getRuleMaskInstance()
    {
        return getRuleMaskInstance(getLocale());
    }

    /**
     * Returns instance of SaleReturnLineItemIfc class.
     *
     * @return SaleReturnLineItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSaleReturnLineItemInstance()
     */
    public SaleReturnLineItemIfc getSaleReturnLineItemInstance()
    {
        return getSaleReturnLineItemInstance(getLocale());
    }

    /**
     * Returns instance of SaleReturnTransactionIfc class.
     *
     * @return SaleReturnTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSaleReturnTransactionInstance()
     */
    public SaleReturnTransactionIfc getSaleReturnTransactionInstance()
    {
        return getSaleReturnTransactionInstance(getLocale());
    }

    /**
     * Returns instance of ScheduledJobIfc class.
     *
     * @return ScheduledJobIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getScheduledJobInstance()
     */
    public ScheduledJobIfc getScheduledJobInstance()
    {
        return getScheduledJobInstance(getLocale());
    }

    /**
     * Returns instance of SearchCriteriaIfc class.
     *
     * @return SearchCriteriaIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSearchCriteriaInstance()
     */
    public SearchCriteriaIfc getSearchCriteriaInstance()
    {
        return getSearchCriteriaInstance(getLocale());
    }

    /**
     * Returns instance of SearchCriteriaIfc class.
     *
     * @return ItemSearchCriteriaIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSearchCriteriaInstance()
     */
    public ItemSearchCriteriaIfc getItemSearchCriteriaInstance()
    {
        return getItemSearchCriteriaInstance(getLocale());
    }
    
    /**
     * Returns instance of SecurityOverrideIfc class.
     *
     * @return instance of SecurityOVerrideIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSecurityOverrideInstance()
     */
    public SecurityOverrideIfc getSecurityOverrideInstance()
    {
        return getSecurityOverrideInstance(getLocale());
    }

    /**
     * Returns instance of SendPackageLineItemIfc class.
     *
     * @return SendPackageLineItemIfc instance
     */
    public SendPackageLineItemIfc getSendPackageLineItemInstance()
    {
        return getSendPackageLineItemInstance(getLocale());
    }

    /**
     * Returns instance of ShippingMethodIfc class.
     *
     * @return ShippingMethodIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getShippingMethodInstance()
     */
    public ShippingMethodIfc getShippingMethodInstance()
    {
        return getShippingMethodInstance(getLocale());
    }

    /**
     * Returns an instance of ShippingMethodSearchCriteria
     * @return ShippingMethodSearchCriteriaIfc
     */
    public ShippingMethodSearchCriteriaIfc getShippingMethodSearchCriteria()
    {
        return new ShippingMethodSearchCriteria();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuthorizeTransferRequestInstance()
     */
    @Override
    public SignatureCaptureRequestIfc getSignatureCaptureRequestInstance()
    {
        return new SignatureCaptureRequest();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getAuthorizeTransferRequestInstance()
     */
    @Override
    public SignatureCaptureResponseIfc getSignatureCaptureResponseInstance()
    {
        return new SignatureCaptureResponse();
    }

    /**
     * Returns instance of SourceCriteria class.
     *
     * @return DiscountListIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSourceCriteriaInstance()
     */
    public DiscountListIfc getSourceCriteriaInstance()
    {
        return getSourceCriteriaInstance(getLocale());
    }

    /**
     * Returns instance of StateIfc class.
     *
     * @return StateIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStateInstance()
     */
    public StateIfc getStateInstance()
    {
        return getStateInstance(getLocale());
    }

    /**
     * Returns instance of StockItemIfc class.
     *
     * @return StockItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStockItemInstance()
     */
    public StockItemIfc getStockItemInstance()
    {
        return getStockItemInstance(getLocale());
    }

    /**
     * Returns instance of StoreCreditIfc class.
     *
     * @return TenderDescriptorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStoreCreditInstance()
     */
    public StoreCreditIfc getStoreCreditInstance()
    {
        return getStoreCreditInstance(getLocale());
    }

    /**
     * Returns instance of StoreIfc class.
     *
     * @return StoreIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStoreInstance()
     */
    public StoreIfc getStoreInstance()
    {
        return getStoreInstance(getLocale());
    }



    /**
     * Returns instance of StoreItemAvailableToPromiseInventoryIfc class.
     *
     * @return StoreItemAvailableToPromiseInventoryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStoreItemAvailableToPromiseInventoryInstance()
     */
    public StoreItemAvailableToPromiseInventoryIfc getStoreItemAvailableToPromiseInventoryInstance()
    {
        return new StoreItemAvailableToPromiseInventory();
    }

    /**
     * Returns instance of StoreOpenCloseTransactionIfc class.
     *
     * @return StoreOpenCloseTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStoreOpenCloseTransactionInstance()
     */
    public StoreOpenCloseTransactionIfc getStoreOpenCloseTransactionInstance()
    {
        return getStoreOpenCloseTransactionInstance(getLocale());
    }

    /**
     * Returns instance of StoreSafeIfc class.
     *
     * @return StoreSafeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStoreSafeInstance()
     */
    public StoreSafeIfc getStoreSafeInstance()
    {
        return getStoreSafeInstance(getLocale());
    }

    /**
     * Returns instance of StoreStatusAndTotalsIfc class.
     *
     * @return StoreStatusAndTotalsIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStoreStatusAndTotalsInstance()
     */
    public StoreStatusAndTotalsIfc getStoreStatusAndTotalsInstance()
    {
        return getStoreStatusAndTotalsInstance(getLocale());
    }

    /**
     * Returns instance of StoreStatusIfc class.
     * @return StoreStatusIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStoreStatusInstance()
     */
    public StoreStatusIfc getStoreStatusInstance()
    {
        return getStoreStatusInstance(getLocale());
    }

    /**
     * Returns instance of SuperGroupIfc class.
     *
     * @return SuperGroupIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSuperGroupInstance()
     */
    public SuperGroupIfc getSuperGroupInstance()
    {
        return getSuperGroupInstance(getLocale());
    }

    /**
     * Returns instance of Supplier class.
     *
     * @return SupplierIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSupplierInstance()
     */
    public SupplierIfc getSupplierInstance()
    {
        return getSupplierInstance(getLocale());
    }

    /**
     * Returns instance of SupplyCategoryIfc class.
     *
     * @return SupplyCategoryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSupplyCategoryInstance()
     */
    public SupplyCategoryIfc getSupplyCategoryInstance()
    {
        return getSupplyCategoryInstance(getLocale());
    }

    /**
     * Returns instance of SupplyItemIfc class.
     *
     * @return SupplyItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSupplyItemInstance()
     */
    public SupplyItemIfc getSupplyItemInstance()
    {
        return getSupplyItemInstance(getLocale());
    }


    /**
     * Returns instance of SupplyItemSearchCriteriaIfc class.
     *
     * @return SupplyItemSearchCriteriaIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSupplyItemSearchCriteriaInstance()
     */
    public SupplyItemSearchCriteriaIfc getSupplyItemSearchCriteriaInstance()
    {
        return getSupplyItemSearchCriteriaInstance(getLocale());
    }

    /**
     * Returns instance of SupplyOrderIfc class.
     *
     * @return SupplyOrderIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSupplyOrderInstance()
     */
    public SupplyOrderIfc getSupplyOrderInstance()
    {
        return getSupplyOrderInstance(getLocale());
    }

    /**
     * Returns instance of SupplyOrderLineItemIfc class.
     *
     * @return SupplyOrderLineItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getSupplyOrderLineItemInstance()
     */
    public SupplyOrderLineItemIfc getSupplyOrderLineItemInstance()
    {
        return getSupplyOrderLineItemInstance(getLocale());
    }

    /**
     * Returns instance of TargetCriteria class.
     *
     * @return DiscountListIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTargetCriteriaInstance()
     */
    public DiscountListIfc getTargetCriteriaInstance()
    {
        return getTargetCriteriaInstance(getLocale());
    }

    /**
     * Returns instance of TaskInfoIfc class.
     *
     * @return TaskInfoIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaskInfoInstance()
     */
    public TaskInfoIfc getTaskInfoInstance()
    {
        return getTaskInfoInstance(getLocale());
    }

    /**
     * Return an instance of NewTaxRuleIfc which calculates tax by the line item
     *
     * @return NewTaxRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxByLineRuleInstance()
     */
    public TaxRuleIfc getTaxByLineRuleInstance()
    {
        return getTaxByLineRuleInstance(getLocale());
    }

    /**
     * Return an instance of TaxEngineIfc
     *
     * @return TaxEngineIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxEngineInstance()
     */
    public TaxEngineIfc getTaxEngineInstance()
    {
        return getTaxEngineInstance(getLocale());
    }

    /**
     * Returns an instance of TaxExemptRuleIfc
     *
     * @return TaxExemptRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxExemptTaxRuleInstance()
     */
    public TaxExemptTaxRuleIfc getTaxExemptTaxRuleInstance()
    {
        return getTaxExemptTaxRuleInstance(getLocale());
    }

    /**
     * Returns an implementation of the TaxHistorySelectionCriteriaIfc.
     * @return TaxHistorySelectionCriteriaIfc
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxHistorySelectionCriteriaInstance()
     **/
    public TaxHistorySelectionCriteriaIfc getTaxHistorySelectionCriteriaInstance()
    {
        return new TaxHistorySelectionCriteria();
    }

    /**
     * Returns and instance of TaxInformationContainerIfc.
     *
     * @return TaxInformationContainerIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxInformationContainerInstance()
     */
    public TaxInformationContainerIfc getTaxInformationContainerInstance()
    {
        return getTaxInformationContainerInstance(getLocale());
    }

    /**
     * Returns an instance of TaxInformation
     *
     * @return TaxInformation instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxInformationInstance()
     */
    public TaxInformationIfc getTaxInformationInstance()
    {
        return getTaxInformationInstance(getLocale());
    }

    /**
     * Return an instance of NewTaxRuleIfc which calculates
     * tax by the transaction and then prorates it down to
     * the line items.
     *
     * @return NewTaxRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxProrateRuleInstance()
     */
    public TaxRuleIfc getTaxProrateRuleInstance()
    {
        return getTaxProrateRuleInstance(getLocale());
    }

    /**
     * Returns an instance of TaxRateCalculatorIfc.
     *
     * @return TaxRateCalculatorInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxRateCalculatorInstance()
     */
    public TaxRateCalculatorIfc getTaxRateCalculatorInstance()
    {
        return getTaxRateCalculatorInstance(getLocale());
    }

    /**
     * Returns an instance of TaxRateCalculatorIfc.
     * @param inclusiveTaxFlag boolean flag indicating if inclusive or
     * 		exclusive tax rate calculator must be returned.
     * @return TaxRateCalculatorIfc instance
     */
    public TaxRateCalculatorIfc getTaxRateCalculatorInstance(boolean inclusiveTaxFlag)
    {
    	return getTaxRateCalculatorInstance(getLocale(), inclusiveTaxFlag);
    }

    /**
     * Returns an instance of TaxRuleItemContainerIfc
     *
     * @return TaxRuleItemContainerIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxRuleItemContainerInstance()
     */
    public TaxRuleItemContainerIfc getTaxRuleItemContainerInstance()
    {
        return getTaxRuleItemContainerInstance(getLocale());
    }

    /**
     * Return an instance of TaxTableLineItemIfc
     *
     * @return TaxTableLineItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxTableLineItemInstance()
     */
    public TaxTableLineItemIfc getTaxTableLineItemInstance()
    {
        return getTaxTableLineItemInstance(getLocale());
    }

    /**
     * Return an object of type taxTotalsContainer
     *
     * @return taxTotalsContainer
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxTotalsContainerInstance()
     */
    public TaxTotalsContainerIfc getTaxTotalsContainerInstance()
    {
        return getTaxTotalsContainerInstance(getLocale());
    }

    /**
     * Return an object of type TaxTotals
     *
     * @return tax totals instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTaxTotalsInstance()
     */
    public TaxTotalsIfc getTaxTotalsInstance()
    {
        return getTaxTotalsInstance(getLocale());
    }

    /**
     * Returns instance of TenderCashIfc class.
     *
     * @return TenderCashIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderCashInstance()
     */
    public TenderCashIfc getTenderCashInstance()
    {
        return getTenderCashInstance(getLocale());
    }

    /**
     * Returns instance of TenderChargeIfc class.
     *
     * @return TenderChargeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderChargeInstance()
     */
    public TenderChargeIfc getTenderChargeInstance()
    {
        return getTenderChargeInstance(getLocale());
    }

    /**
     * Returns instance of TenderCheckIfc class.
     *
     * @return TenderCheckIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderCheckInstance()
     */
    public TenderCheckIfc getTenderCheckInstance()
    {
        return getTenderCheckInstance(getLocale());
    }

    /**
     * Returns instance of TenderCouponIfc class.
     *
     * @return TenderCouponIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderCouponInstance()
     */
    public TenderCouponIfc getTenderCouponInstance()
    {
        return getTenderCouponInstance(getLocale());
    }

    /**
     * Returns instance of TenderDebitIfc class.
     *
     * @return TenderDebitIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderDebitInstance()
     */
    public TenderDebitIfc getTenderDebitInstance()
    {
        return getTenderDebitInstance(getLocale());
    }

    /**
     * Returns instance of TenderDescriptorIfc class.
     *
     * @return TenderDescriptorIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderDescriptorInstance()
     */
    public TenderDescriptorIfc getTenderDescriptorInstance()
    {
        return getTenderDescriptorInstance(getLocale());
    }

    /**
     * Returns instance of TenderGiftCardIfc class.
     *
     * @return TenderGiftCardIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderGiftCardInstance()
     */
    public TenderGiftCardIfc getTenderGiftCardInstance()
    {
        return getTenderGiftCardInstance(getLocale());
    }

    /**
     * Returns instance of TenderGiftCertificateIfc class.
     *
     * @return TenderGiftCertificateIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderGiftCertificateInstance()
     */
    public TenderGiftCertificateIfc getTenderGiftCertificateInstance()
    {
        return getTenderGiftCertificateInstance(getLocale());
    }

    /**
     * Returns instance of TenderLimitsIfc class.
     *
     * @return TenderLimitsIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderLimitsInstance()
     */
    public TenderLimitsIfc getTenderLimitsInstance()
    {
        return getTenderLimitsInstance(getLocale());
    }

    /**
     * Returns instance of TenderMailBankCheckIfc class.
     *
     * @return TenderMailBankCheckIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderMailBankCheckInstance()
     */
    public TenderMailBankCheckIfc getTenderMailBankCheckInstance()
    {
        return getTenderMailBankCheckInstance(getLocale());
    }

    /**
     * Returns instance of TenderMoneyOrderIfc class.
     *
     * @return TenderMoneyOrderIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderMoneyOrderInstance()
     */
    public TenderMoneyOrderIfc getTenderMoneyOrderInstance()
    {
        return getTenderMoneyOrderInstance(getLocale());
    }

    /**
     * Returns instance of TenderPurchaseOrderIfc class.
     *
     * @return TenderPurchaseOrderIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderPurchaseOrderInstance()
     */
    public TenderPurchaseOrderIfc getTenderPurchaseOrderInstance()
    {
        return getTenderPurchaseOrderInstance(getLocale());
    }

    /**
     * Returns instance of TenderStoreCreditIfc class.
     *
     * @return TenderStoreCreditIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderStoreCreditInstance()
     */
    public TenderStoreCreditIfc getTenderStoreCreditInstance()
    {
        return getTenderStoreCreditInstance(getLocale());
    }

    /**
     * Returns instance of TenderTravelersCheckIfc class.
     *
     * @return TenderTravelersCheckIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderTravelersCheckInstance()
     */
    public TenderTravelersCheckIfc getTenderTravelersCheckInstance()
    {
        return getTenderTravelersCheckInstance(getLocale());
    }

    /**
     * Returns instance of TenderTypeMapIfc class.
     *
     * @return TenderTypeMapIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTenderTypeMapInstance()
     */
    public TenderTypeMapIfc getTenderTypeMapInstance()
    {
        return getTenderTypeMapInstance(getLocale());
    }

    /**
     * Returns instance of TillAdjustmentTransactionIfc class.
     *
     * @return TillAdjustmentTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTillAdjustmentTransactionInstance()
     */
    public TillAdjustmentTransactionIfc getTillAdjustmentTransactionInstance()
    {
        return getTillAdjustmentTransactionInstance(getLocale());
    }

    /**
     * Returns instance of TillIfc class.
     *
     * @return TillIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTillInstance()
     */
    public TillIfc getTillInstance()
    {
        return getTillInstance(getLocale());
    }

    /**
     * Returns instance of TillOpenCloseTransactionIfc class.
     *
     * @return TillOpenCloseTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTillOpenCloseTransactionInstance()
     */
    public TillOpenCloseTransactionIfc getTillOpenCloseTransactionInstance()
    {
        return getTillOpenCloseTransactionInstance(getLocale());
    }

    /**
     * Returns instance of TimeIntervalActivityIfc class.
     *
     * @return TimeIntervalActivityIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTimeIntervalActivityInstance()
     */
    public TimeIntervalActivityIfc getTimeIntervalActivityInstance()
    {
        return getTimeIntervalActivityInstance(getLocale());
    }

    /**
     * Returns instance of TransactionDiscountByAmountIfc class.
     *
     * @return TransactionDiscountByAmountIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionDiscountByAmountInstance()
     */
    public TransactionDiscountByAmountIfc getTransactionDiscountByAmountInstance()
    {
        return getTransactionDiscountByAmountInstance(getLocale());
    }

    /**
     * Returns instance of TransactionDiscountByPercentageIfc class.
     *
     * @return TransactionDiscountByPercentageIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionDiscountByPercentageInstance()
     */
    public TransactionDiscountByPercentageIfc getTransactionDiscountByPercentageInstance()
    {
        return getTransactionDiscountByPercentageInstance(getLocale());
    }

    /**
     * Returns instance of TransactionDiscountAuditIfc class.
     *
     * @return TransactionDiscountAuditIfc instance
     */
    public TransactionDiscountAuditIfc getTransactionDiscountAuditInstance()
    {
        return getTransactionDiscountAuditInstance(getLocale());
    }

    /**
     * Returns instance of TransactionIDIfc class.
     *
     * @return TransactionIDIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionIDInstance()
     */
    public TransactionIDIfc getTransactionIDInstance()
    {
        return getTransactionIDInstance(getLocale());
    }

    /**
     * Returns instance of TransactionIfc class.
     *
     * @return TransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionInstance()
     */
    public TransactionIfc getTransactionInstance()
    {
        return getTransactionInstance(getLocale());
    }


    /**
     * Returns instance of TransactionKeyIfc class.
     *
     * @return TransactionKeyIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionKeyInstance()
     */
    public TransactionKeyIfc getTransactionKeyInstance()
    {
        return getTransactionKeyInstance(getLocale());
    }

    /**
     * Returns instance of TransactionSummaryIfc class.
     *
     * @return TransactionSummaryIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionSummaryInstance()
     */
    public TransactionSummaryIfc getTransactionSummaryInstance()
    {
        return getTransactionSummaryInstance(getLocale());
    }

    /**
     * Returns instance of TransactionTaxIfc class.
     *
     * @return TransactionTaxIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionTaxInstance()
     */
    public TransactionTaxIfc getTransactionTaxInstance()
    {
        return getTransactionTaxInstance(getLocale());
    }

    /**
     * Returns instance of TransactionTotalsIfc class.
     *
     * @return TransactionTotalsIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionTotalsInstance()
     */
    public TransactionTotalsIfc getTransactionTotalsInstance()
    {
        return getTransactionTotalsInstance(getLocale());
    }

    /**
     * Returns instance of TransactionTypeMapIfc class.
     *
     * @return TransactionTypeMapIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getTransactionTypeMapInstance()
     */
    public TransactionTypeMapIfc getTransactionTypeMapInstance()
    {
        return getTransactionTypeMapInstance(getLocale());
    }

    /**
     * Returns instance of UnitOfMeasureIfc class.
     *
     * @return UnitOfMeasureIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getUnitOfMeasureInstance()
     */
    public UnitOfMeasureIfc getUnitOfMeasureInstance()
    {
        return getUnitOfMeasureInstance(getLocale());
    }

    /**
     * Returns instance of UnknownItemIfc class.
     *
     * @return UnknownItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getUnknownItemInstance()
     */
    public UnknownItemIfc getUnknownItemInstance()
    {
        return getUnknownItemInstance(getLocale());
    }

    /**
     * Return an instance of ValueAddedTaxRuleIfc which calculates
     * the value added tax by line item
     * @return ValueAddedTaxRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getValueAddedTaxByLineRuleInstance()
     */
    public ValueAddedTaxRuleIfc getValueAddedTaxByLineRuleInstance()
    {
        return getValueAddedTaxByLineRuleInstance(getLocale());
    }

    /**
     * Return an instance of ValueAddedTaxRuleIfc which calculates
     * the value added tax by line item
     *
     * @return ValueAddedTaxRuleIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getValueAddedTaxProrateRuleInstance()
     */
    public ValueAddedTaxRuleIfc getValueAddedTaxProrateRuleInstance()
    {
        return getValueAddedTaxProrateRuleInstance(getLocale());
    }

    /**
     * Returns instance of VoidTransactionIfc class.
     *
     * @return VoidTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getVoidTransactionInstance()
     */
    public VoidTransactionIfc getVoidTransactionInstance()
    {
        return getVoidTransactionInstance(getLocale());
    }

    /**
     * Returns instance of StatusChangeTransactionIfc class.
     *
     * @return StatusChangeTransactionIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getStatusChangeTransactionInstance()
     */
    public StatusChangeTransactionIfc getStatusChangeTransactionInstance()
    {
        return new StatusChangeTransaction();
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getWeekDayCalendarLevelInstance()
     */
    public CalendarLevelIfc getWeekDayCalendarLevelInstance()
    {
        return getWeekDayCalendarLevelInstance(getLocale());
    }

    /**
     * Returns instance of WeeklyScheduleDocumentIfc class.
     *
     * @return WeeklyScheduleDocumentIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getWeeklyScheduleDocumentInstance()
     */
    public WeeklyScheduleDocumentIfc getWeeklyScheduleDocumentInstance()
    {
        return getWeeklyScheduleDocumentInstance(getLocale());
    }

    /**
     * Returns instance of WorkstationIfc class.
     *
     * @return WorkstationIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getWorkstationInstance()
     */
    public WorkstationIfc getWorkstationInstance()
    {
        return getWorkstationInstance(getLocale());
    }

    /**
     * Returns requested instance of CalendarLevelIfc
     *
     * @return CalendarLevelInstance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getYearDayCalendarLevelInstance()
     */
    public CalendarLevelIfc getYearDayCalendarLevelInstance()
    {
        return getYearDayCalendarLevelInstance(getLocale());
    }

    /**
     * Returns instance of ShippingCharge class.
     *
     * @return ShippingChargeIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderShippingDetailInstance()
     */
    public ShippingChargeIfc getShippingChargeInstance()
    {
        return getShippingChargeInstance(getLocale());
    }

    /**
     * Returns instance of ShippingCharge class.
     *
     * @return ShippingItemIfc instance
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#ShippingItemIfc()
     */
    public ShippingItemIfc getShippingItemInstance()
    {
        return getShippingItemInstance(getLocale());
    }

    /**
     * Returns instance of OrderItemTaxStatusIfc
     * @return OrderItemTaxStatusIfc instance
     */
    public OrderItemTaxStatusIfc getOrderItemTaxStatusInstance()
    {
        return new OrderItemTaxStatus();
    }

    /**
     * Returns instance of OrderItemDiscountStatusIfc
     * @return OrderItemDiscountStatusIfc instance
     */
    public OrderItemDiscountStatusIfc getOrderItemDiscountStatusInstance()
    {
        return new OrderItemDiscountStatus();
    }
 
    /**
     * Returns instance of ItemTransactionDiscountAggregatorIfc
     * @return itemTransactionDiscountAggregatorIfc instance
     */
    public ItemTransactionDiscountAggregatorIfc getItemTransactionDiscountAggregatorInstance()
    {
        return new ItemTransactionDiscountAggregator();
    }
    
    /**
     * Returns instance of ItemTransactionTaxAggregatorIfc
     * @return ItemTransactionTaxAggregatorIfc instance
     */
    public ItemTransactionTaxAggregatorIfc getItemTransactionTaxAggregatorInstance()
    {
        return new ItemTransactionTaxAggregator();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCurrencyRoundingCalculatorInstance()
     */
    public CurrencyRoundingCalculatorIfc getCurrencyRoundingCalculatorInstance()
    {
       return new CurrencyRoundingCalculator();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getCurrencyRoundingRuleSearchCriteriaInstance()
     */
    public CurrencyRoundingRuleSearchCriteriaIfc getCurrencyRoundingRuleSearchCriteriaInstance()
    {
       return new CurrencyRoundingRuleSearchCriteria();
    }

    /*
     * (non-Javadoc)
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#getOrderStatusChangeTransactionInstance()
     */
    public OrderStatusChangeTransactionIfc getOrderStatusChangeTransactionInstance()
    {
        return new OrderStatusChangeTransaction();
    }
    
    /**
     * Sets attributes in clone of this object.
     * @param newClass new instance of object
     */
    public void setCloneAttributes(DomainObjectFactory newClass)
    {
        super.setCloneAttributes(newClass);
        newClass.setFactoryID(getFactoryID());
    }

    /**
     * Sets factory identifier.
     *
     * @param value  factory identifier
     * @see oracle.retail.stores.domain.factory.DomainObjectFactoryIfc#setFactoryID(java.lang.String)
     */
    public void setFactoryID(String value)
    {
        factoryID = value;
    }

    /**
     * Returns default display string.
     *
     * @return String representation of object
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder strResult = Util.classToStringHeader("DomainObjectFactory", "", hashCode());
        strResult.append(Util.formatToStringEntry("factoryID", getFactoryID()));
        strResult.append(Util.formatToStringEntry("Locale", getLocale().toString()));
        return strResult.toString();
    }

}
