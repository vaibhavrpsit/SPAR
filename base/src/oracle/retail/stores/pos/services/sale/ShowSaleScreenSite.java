/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ShowSaleScreenSite.java /main/80 2014/07/10 14:02:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    jswan  07/09/14 - Modified to prevent webstore recommended items
 *                      displaying when the current register is not set up for
 *                      ICE.
 *    yiqzha 06/26/14 - Enable Pricing button when there is no transaction.
 *    yiqzha 06/25/14 - The status of Pricing button will be set by
 *                      PricingItemListAdapter and SaleBean. The pricing button
 *                      status will be reset after deleting an item.
 *    jswan  06/20/14 - Modified to get the recommended items for all sale
 *                      return line items in the transaction.
 *    jswan  06/20/14 - Modified to support display of a Recommended Item.
 *    yiqzha 06/02/14 - Add Amount Paid for order pick or order cancel in show
 *                      sale screen.
 *    abonda 05/30/14 - notifications UI related changes
 *    abonda 05/14/14 - notifications requirement
 *    cgreen 05/15/14 - refactor and code cleanup
 *    yiqzha 05/14/14 - For ASA order retrival, enable Item button, disable
 *                      buttons in modify item, except for Gift Receipt and
 *                      Sale Associate.
 *    tkshar 02/26/14 - fixed disabling tender button when all items are
 *                      deleted from the sale screen.
 *    cgreen 02/06/14 - add trafficlight that displays dialog when an action is
 *                      attempted on non-editable web-managed transaction.
 *    cgreen 11/18/13 - corrected method name spelling
 *    mkutia 11/01/13 - handle timeout for Dual display screen
 *    mkutia 10/08/13 - correctly display customer name on status bean
 *    rgour  06/17/13 - setting captured customer value for shipping method
 *                      screen if no customer is linked to the transaction
 *    abhina 05/15/13 - Fix to enable or disable pricing sub menus based on the
 *                      line items
 *    abonda 05/08/13 - display customer info in the status bar.
 *    arabal 03/19/13 - added steps to update the transation level Sales
 *                      Associate in the status region
 *    mkutia 01/15/13 - timeout at Sale Screen reworked for extracted
 *                      cancelSale tour
 *    vbongu 01/02/13 - show sale screen on dual display
 *    rgour  12/27/12 - changing the prompt message to Enter a number for sale
 *                      screen
 *    rgour  12/10/12 - Enhancement in suspended transaction phase
 *    rgour  10/29/12 - Enhancements in Suspended Transactions
 *    icole  04/26/12 - Clean up line display and payment CPOI code.
 *    icole  04/25/12 - Send item and totals in one request for performance
 *                      improvement in displaying items on CPOI. Separated
 *                      processing of tenders from items.
 *    yiqzha 04/16/12 - refactor store send from transaction totals
 *    yiqzha 04/03/12 - refactor store send for cross channel
 *    icole  03/28/12 - Moved display of item into setCPOIDisplay and don't
 *                      display the single item if a refresh is required.
 *    icole  03/28/12 - Forward port mukothan_bug_13112591, moved display of
 *                      line item from AddItemSite to ShowSaleScreenSite.
 *    icole  03/06/12 - Refactor to remove CPOIPaymentUtility and attempt to
 *                      have more generic code, rather than heavily Pincomm.
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    mchell 09/29/11 - Fixed manager override issue for transaction
 *                      cancellation
 *    tkshar 09/16/11 - Disabled Redeem button on ReEntry ON
 *    icole  08/15/11 - Move code for ensuring the held swipe is cleared on the
 *                      CPOI to after the UI is intialized as there was a
 *                      timing window when Enter could be pressed during
 *                      Layaway without the UI ready. HPQC 545 BugId 12810060
 *    blarse 07/28/11 - Changed house-account-button to use HouseCardsAccepted
 *                      parameter instead of CardTypes parameter (which was
 *                      deleted).
 *    cgreen 07/08/11 - do not disable timer when leaving screen
 *    icole  06/16/11 - Changes for CurrencyIfc, Sardine refresh items list,
 *                      other simulted changes.
 *    blarse 06/15/11 - Integrated change to CustomerIneractionRequest.
 *                      registerID was renamed to workstationID.
 *    blarse 06/14/11 - Adding storeID to scrolling receipt calls.
 *                      clearSwipeAheadData() was moved to PaymentManager.
 *    icole  06/14/11 - Restore CurrencyIfc
 *    icole  06/09/11 - Correct merge
 *    icole  06/09/11 - APF
 *    cgreen 06/07/11 - update to first pass of removing pospal project
 *    sgu    06/02/11 - enable/disable house account/instant credit button
 *    jkoppo 03/02/11 - Added logic to enable/disable scan sheet button.
 *    npoola 12/20/10 - action button texts are moved to CommonActionsIfc
 *    blarse 12/10/10 - Customer name being displayed even though no customer
 *                      is linked. This happens after displaying customer
 *                      history and canceling. Change code to clear customer
 *                      hame if no customer is linked.
 *    jkoppo 11/26/10 - sales associate linked with the transaction is
 *                      incorrect on status region.
 *    cgreen 10/25/10 - do not call setModel right after showScreen
 *    nkgaut 09/20/10 - refractored code to use a single class for checking
 *                      cash in drawer
 *    sgu    07/15/10 - fix the the sale item screen name
 *    dwfung 07/14/10 - Re-enable Gift Card/Cert button that could be disabled for
 *                      Special Order Transactions.
 *    abonda 06/21/10 - Disable item level editing for an external order line
 *                      item
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/28/10 - updating deprecated names
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    acadar 04/09/10 - optimize calls to LocaleMAp
 *    acadar 04/08/10 - merge to tip
 *    acadar 04/06/10 - use default locale when displaying currency
 *    acadar 04/05/10 - use default locale for currency and date/time display
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    asinto 02/22/10 - Changed item quantity for CPOI display.
 *    dwfung 02/02/10 - hide Gift Cert/Card button for Special Order Transaction.
 *    cgreen 01/14/10 - hide the OSK instead of show it
 *    abonda 01/03/10 - update header date
 *    nkgaut 12/17/09 - Added extra condition check for IMEI Field length
 *                      change on prompt and response panel
 *    cgreen 12/16/09 - show keyboard when arriving at sell or tender screen
 *    nkgaut 12/16/09 - code review changes
 *    nkgaut 12/15/09 - Added condition to accomodate 15 characters field
 *                      length on prompt and response panel for IMEI
 *    cgreen 08/06/09 - XbranchMerge cgreene_bug-8737695 from
 *                      rgbustores_13.1x_branch
 *    cgreen 08/05/09 - add null check to beanModel from ui
 *    vapart 04/08/09 - Added description for the changes.
 *    vapart 04/08/09 - Added code to set the cashiername and
 *                      salesassociate.This is needed when the preferred
 *                      langauge is different from the default locale.
 *    asinto 04/03/09 - Localizing the amounts that appear in the CPOI device.
 *    djenni 03/28/09 - Enter a single line comment: creating
 *                      isSalesAssociateModifiedAtLineItem(), which is similar
 *                      to getSalesAssociateModified(), and using it at receipt
 *                      to determine whether to print the SalesAssociate at the
 *                      line item. Jack warned against modifying the existing
 *                      method as it is used for something else.
 *    cgreen 03/20/09 - keep kit components off receipts by implementing new
 *                      method getLineItemsExceptExclusions
 *    mahisi 03/04/09 - Fixed send button disable issue when line item mark as
 *                      pick-up or deliver vice versa
 *    asinto 02/24/09 - Honor thy customer's preferred language.
 *    cgreen 02/16/09 - XbranchMerge cgreene_bug7462232-profiling from
 *                      rgbustores_13.0.1_branch
 *    mipare 01/06/09 - Forward port 7314478, TIMEOUT INACTIVE WITH TRANS
 *                      CONFIG PARAMETER IS NOT WORKING CORRECTLY
 *    asinto 12/18/08 - Changes to support i18n text where images were shown
 *                      before in Sardine.
 *    aphula 12/10/08 - returns functionality changes for greying out buttons
 *    aphula 11/25/08 - Checking files after code review by Amrish
 *    ranojh 10/31/08 - Ensure all base currency/alt currency and database
 *                      locales are deprecated
 *    ranojh 10/28/08 - Removed usage of database locale and base currency
 *                      locales
 *    asinto 10/24/08 - Updates for POS integration.
 *    ranojh 10/09/08 - Changes for User Selection for Employee and Customer
 *    nkgaut 09/18/08 - Added check for cash drawer UNDER warning message

 * ===========================================================================
 * $Log:
 *  17   360Commerce 1.16        5/28/2008 6:07:28 AM   Anil Rathore    Updated
 *        to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *  16   360Commerce 1.15        5/22/2008 5:38:11 PM   subramanyaprasad gv For
 *        CR 31731: Code reviewed by Manikandan Chellapan.
 *  15   360Commerce 1.14        3/25/2008 2:32:00 PM   Vikram Gopinath CR
 *       #29942, ported changes from v12x. Set the line item's sales associate
 *        modified flag depending on whether the transaction's sales associate
 *        was overridden.
 *  14   360Commerce 1.13        3/6/2008 5:10:40 PM    Chengegowda Venkatesh
 *       For CR 30275
 *  13   360Commerce 1.12        2/23/2008 3:39:38 AM   Deepti Sharma   disable
 *        House Account in transaction Reentry mode
 *  12   360Commerce 1.11        1/10/2008 7:06:25 PM   Manas Sahu      Event
 *       originator changes
 *  11   360Commerce 1.10        1/7/2008 7:31:13 PM    Chengegowda Venkatesh
 *       Audit log changes
 *  10   360Commerce 1.9         6/13/2007 7:17:54 AM   Anda D. Cadar   SCR
 *       27207: Receipt changes -  proper alignment for amounts
 *  9    360Commerce 1.8         6/5/2007 1:08:54 AM    Alan N. Sinton  CR
 *       26483 - Changes per review comments.
 *  8    360Commerce 1.7         5/1/2007 10:45:40 PM   Brett J. Larsen CR
 *       26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *       feature)
 *
 *  7    360Commerce 1.6         5/1/2007 2:15:51 AM    Alan N. Sinton  Merge
 *       from v12.0_temp.
 *  6    360Commerce 1.5         4/25/2007 7:22:46 PM   Anda D. Cadar   I18N
 *       merge
 *
 *  5    360Commerce 1.4         2/10/2006 10:36:44 PM  Deepanshu       CR
 *       6092: Sales Assoc sould be last 4 digits of Sales Assoc ID and not of
 *        Cashier ID on the recipt
 *  4    360Commerce 1.3         1/26/2006 3:41:47 AM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  3    360Commerce 1.2         4/1/2005 3:00:03 AM    Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 9:55:19 PM   Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 11:44:14 PM  Robert Pearse
 * $:
 *  4    .v700     1.2.1.0     11/11/2005 09:30:57    Devi Sreekumari CR 4801 -
 *       Modified code to highlight the last item added to the transaction
 *  3    360Commerce1.2         3/31/2005 15:30:03     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:25:19     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:14:14     Robert Pearse
 * $
 * Revision 1.45.2.1  2004/11/16 00:30:04  rzurga
 * @scr 6552 SwipeAnytime - Cant invoke Select Paymnt screen on CPOI dev
 * Always show ItemScreen - for the case we get back from tender selection by cancel on CPOI
 *
 * Revision 1.45  2004/09/10 15:29:15  rsachdeva
 * @scr 6791 Removed variables since these were not being used in the site
 *
 * Revision 1.44  2004/08/20 21:27:44  rsachdeva
 * @scr 6791 Transaction Level Send
 *
 * Revision 1.43  2004/08/10 20:57:55  rsachdeva
 * @scr 6791 Transaction Level Send
 *
 * Revision 1.42  2004/08/09 18:27:33  kmcbride
 * @scr 6803: Removing un-necessary cloning of line items
 *
 * Revision 1.41  2004/07/31 19:08:15  epd
 * @scr 6381 first item now highlighted but not selected
 *
 * Revision 1.40  2004/07/28 21:16:36  rzurga
 * @scr 6544 Wrong prompt appears in Sale items screen on CPOI device
 * Remove wrong prompt
 *
 * Revision 1.39  2004/07/27 22:29:28  jdeleau
 * @scr 6485 Make sure the undo button on the sell item screen does
 * not force the operator to re-enter the users zip of phone.
 *
 * Revision 1.38  2004/07/23 16:27:17  lzhao
 * @scr 5307: disable customer button when it is tax exempt.
 *
 * Revision 1.37  2004/07/21 15:36:35  jriggins
 * @scr 6147 Added null check for customer info
 *
 * Revision 1.36  2004/07/20 23:05:30  mweis
 * @scr 6147 "Cancel" from Returns should not remove customer's name as a linked customer.
 *
 * Revision 1.35  2004/07/19 21:40:36  cdb
 * @scr 6179 Externalized CID display Swipe Anytime message.
 *
 * Revision 1.34  2004/07/15 16:27:07  jdeleau
 * @scr 6281 Make sure that receipts print out with the correct
 * Locale.
 *
 * Revision 1.33  2004/07/15 15:43:47  rsachdeva
 * @scr 6270 Line Display and CPOI
 *
 * Revision 1.32  2004/07/14 15:40:19  jdeleau
 * @scr 5025 Persist the item selection on the sale screen across services, such that
 * when it returns to the sale screen the same items are selected, if possible.
 *
 * Revision 1.31  2004/07/08 13:59:47  rsachdeva
 * @scr 6055 CPOI Discount Field
 *
 * Revision 1.30  2004/07/07 14:38:28  rsachdeva
 * @scr 5962 CPOI Split Line Items
 *
 * Revision 1.29  2004/07/06 15:49:31  rsachdeva
 * @scr 5963 CPOI Line Items Update
 *
 * Revision 1.28  2004/07/02 00:00:52  rzurga
 * @scr 5107 Customer Point of Interaction- Several elements mising from CPOI screen
 *
 * Added quantitySale to TransactionTotals that accounts for the sale items only.
 *
 * Revision 1.27  2004/07/01 22:22:17  rzurga
 * @scr 5107 Customer Point of Interaction- Elements missing from CPOI screen
 *
 * Units sold and discount added to the bottom of the CPOI screen along with the taxes and total.
 *
 * Revision 1.26  2004/06/04 13:04:39  tmorris
 * @scr 5308 -House Account button was not disabling properly.
 *
 * Revision 1.25  2004/05/27 17:12:48  mkp1
 * @scr 2775 Checking in first revision of new tax engine.
 *
 * Revision 1.24  2004/05/24 20:54:18  rsachdeva
 * @scr 4670 Send: Multiple Sends Postal Code and Destination State
 *
 * Revision 1.23  2004/05/19 00:09:07  rzurga
 * @scr 5178 Fixed little CPOI bugs
 *
 * Revision 1.22  2004/05/06 02:58:59  tfritz
 * @scr 1872 Adding an unknown item after 10 or more items no longer sets the focus to the first item entered.
 *
 * Revision 1.21  2004/05/05 18:44:53  jriggins
 * @scr 4680 Moved Price Adjustment button from Sale to Pricing
 *
 * Revision 1.20  2004/05/05 16:43:32  rsachdeva
 * @scr 4670 Send: Multiple Sends Shipping Charge
 *
 * Revision 1.19  2004/04/27 21:27:30  jriggins
 * @scr 3979 Code review cleanup
 *
 * Revision 1.18  2004/04/22 07:27:24  jriggins
 * @scr 3979 Switched '+' to '|' in order to do a bitwise ORing of the type values
 *
 * Revision 1.17  2004/04/16 22:32:38  jriggins
 * @scr 3979 Added check for enable price adjustment parameter
 *
 * Revision 1.16  2004/04/15 15:41:50  jriggins
 * @scr 3979 Now make use of new filtering functionality of the SaleReturnTransaction
 *
 * Revision 1.15  2004/04/14 15:17:10  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.14  2004/04/06 19:55:03  mweis
 * @scr 4305  Sale:  indicators when till balance is negative
 *
 * Revision 1.13  2004/04/05 16:16:08  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.12  2004/04/05 15:47:54  jdeleau
 * @scr 4090 Code review comments incorporated into the codebase
 *
 * Revision 1.11  2004/04/03 00:24:45  jriggins
 * @scr 3979 Added price adjustment items to ui list
 *
 * Revision 1.10  2004/04/01 16:04:10  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.9  2004/04/01 15:58:17  blj
 * @scr 3872 Added training mode, toggled the redeem button based
 * on transaction==null and fixed post void problems.
 *
 * Revision 1.8  2004/03/25 20:25:15  jdeleau
 * @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 * See the scr for more info.
 *
 * Revision 1.7  2004/03/23 22:08:45  bjosserand
 * @scr 4093 Transaction Reentry
 *
 * Revision 1.6  2004/03/16 18:30:42  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/03/15 21:55:15  jdeleau
 * @scr 4040 Automatic logoff after timeout
 *
 * Revision 1.4  2004/03/14 21:15:28  tfritz
 * @scr 3884 - New Training Mode Functionality
 *
 * Revision 1.3  2004/02/12 16:48:17  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:22:50  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 * Revision 1.1.1.1 2004/02/11 01:04:11
 * cschellenger updating to pvcs 360store-current
 *
 *
 *
 * Rev 1.3 28 Jan 2004 20:33:38 baa set focus index
 *
 * Rev 1.2 08 Nov 2003 01:16:44 baa cleanup -sale refactoring
 *
 * Rev 1.1 Nov 07 2003 12:38:06 baa use SaleCargoIfc Resolution for 3430: Sale
 * Service Refactoring
 *
 * Rev 1.0 Nov 05 2003 14:14:48 baa Initial revision.
 * ===============================================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.commerceservices.audit.AuditLoggerConstants;
import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.UserEvent;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.comparators.Comparators;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.ifc.PaymentManagerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.dualdisplay.DualDisplayManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.TagConstantsIfc;
import oracle.retail.stores.pos.services.common.TimedCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.DualDisplayFrame;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
import oracle.retail.stores.pos.ui.beans.TotalsBeanModel;
import oracle.retail.stores.pos.ui.timer.DefaultTimerModel;

import org.apache.log4j.Logger;

/**
 * This site displays the SELL_ITEM screen.
 */
public class ShowSaleScreenSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3890372067179784543L;

    /**
     * Logger for debugging this site.
     */
    protected static final Logger logger = Logger.getLogger(ShowSaleScreenSite.class);

    /**
     * Sp Ord Item Screen Name tag
     */
    public static final String SP_ORD_ITEM_SCREEN_NAME_TAG = "SpOrdItemScreenName";

    /**
     * PDO Ord Item Screen Name tag
     */
    public static final String PDO_ORD_ITEM_SCREEN_NAME_TAG = "PDOOrdItemScreenName";

    /**
     * Sp Ord Item Screen Name default text
     */
    public static final String SP_ORD_ITEM_SCREEN_NAME_TEXT = "Sp. Ord. Item";

    /**
     * PDO Ord Item Screen Name default text
     */
    public static final String PDO_ORD_ITEM_SCREEN_NAME_TEXT = "PickupDeliveryOrd.Item";

    /**
     * Layaway Item Screen Name tag
     */
    public static final String LAYAWAY_ITEM_SCREEN_NAME_TAG = "LayawayItemScreenName";

    /**
     * Layaway Item Screen Name default text
     */
    public static final String LAYAWAY_ITEM_SCREEN_NAME_TEXT = "Layaway Item";

    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

    /**
     * This timestamp is recorded when this site leaves. When this site arrives
     * again, the current timestamp is compared against this one for a duration.
     * This will effectively denote how much time it takes to scan an item.
     */
    private long debugRoundtripTimestamp;

    /** Enable dual display */
    protected boolean dualDisplayEnabled = false;

    /**
     * Displays the SELL_ITEM screen.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // grab the transaction (if it exists)
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();        

        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);          
        dualDisplayEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, DualDisplayFrame.DUALDISPLAY_ENABLED, false);

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        boolean imeiEnabled = utility.getIMEIProperty();
        boolean serializationEnabled = utility.getSerialisationProperty();
        String imeiResponseFieldLength = utility.getIMEIFieldLengthProperty();

        // Setup bean models information for the UI to display
        LineItemsModel beanModel = new LineItemsModel();
        NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
        beanModel.setLocalButtonBeanModel(localModel);
        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
        beanModel.setGlobalButtonBeanModel(globalModel);
        StatusBeanModel statusModel = new StatusBeanModel();
        beanModel.setStatusBeanModel(statusModel);

        // allow the status model to dynamically compute a negative till balance.
        statusModel.setRegister(cargo.getRegister());
        TotalsBeanModel totalsModel = new TotalsBeanModel();
        beanModel.setTotalsBeanModel(totalsModel);

        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        
        // Check for Cash drawer UNDER Warning
        if (cargo.isCashDrawerUnderWarning())
        {
            statusModel.setCashDrawerWarningRequired(true);
            cargo.setCashDrawerUnderWarning(false);
        }

        // Reset locale to default values
        if (transaction == null || transaction.getCustomer() == null)
        {
            Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            UIUtilities.setUILocaleForCustomer(defaultLocale);
        }

        //For the Condition in which the user prefered lanaguage is different from the default locale.
        //the statusBean was getting cleared in the AssignmentSpec.getBean(BeanSpec beanSpec, String assignmentID, Locale lcl) method.
        //So the Statusbar didnt have the cashierName and SalesAssociate Name.
        //Added this code to set the cashier name and Sales Associate Name.
        statusModel.setCashierName(cargo.getOperator().getPersonName().getFirstLastName());
        if (transaction == null || transaction.getSalesAssociate() == null)
        {
            try
            {
                boolean defaultToCashier = pm.getBooleanValue(ParameterConstantsIfc.DAILYOPERATIONS_DefaultToCashier);
                if (defaultToCashier)
                {
                    boolean identifySaleAssoc = pm.getBooleanValue(ParameterConstantsIfc.DAILYOPERATIONS_IdentifySalesAssociateEveryTransaction);
                    if (identifySaleAssoc)
                    {
                        statusModel.setSalesAssociateName(cargo.getEmployee().getPersonName().getFirstLastName());
                    }
                    else
                    {
                        statusModel.setSalesAssociateName(cargo.getOperator().getPersonName().getFirstLastName());
                    }
                }
            }
            catch (ParameterException e)
            {
                logger.error(Util.throwableToString(e));
            }
        }
        else
        {
            // Set Sales Associate
            statusModel.setSalesAssociateName(cargo.getEmployee().getPersonName().getFirstLastName());
        }

        // Set the undo, cancel and tender buttons enabled state based on
        // transaction from cargo.
        if (transaction == null)
        {
            updateScreenForNoTransaction(bus, beanModel);
        }
        else
        {
            updateScreenForTransaction(bus, beanModel, transaction);
        }

        // Set the training and customer buttons enabled state based on flags
        // from the cargo. Training Mode is a single toggle on/off button.
        boolean trainingModeOn = cargo.getRegister().getWorkstation().isTrainingMode();
        // If training mode is turned on, then put Training Mode indication in
        // status panel. Otherwise, return status to online/offline status.
        statusModel.setStatus(POSUIManagerIfc.TRAINING_MODE_STATUS, trainingModeOn);
        boolean reentryModeOn = cargo.getRegister().getWorkstation().isTransReentryMode();
        localModel.setButtonEnabled(CommonActionsIfc.REDEEM, !reentryModeOn);
        // House Account button should be disabled in transaction reentry mode
        if (reentryModeOn)
        {
            localModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, false);
        }
        localModel.setButtonEnabled(CommonActionsIfc.ITEM_INQUIRY, true); // in case it was turned by web-orders
        localModel.setButtonEnabled(CommonActionsIfc.CUSTOMER, true);
        boolean enableScanSheet = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, "enableScanSheet", false);
        localModel.setButtonEnabled(CommonActionsIfc.SCANSHEET, enableScanSheet);
        
        PromptAndResponseModel promptModel = new PromptAndResponseModel();
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

        String responseText = utility.retrieveText("SalePromptAndResponsePanelSpec",
                BundleConstantsIfc.POS_BUNDLE_NAME, "EnterItemNumberPrompt", "Enter a number.", locale);
        promptModel.setPromptText(responseText);
        beanModel.setPromptAndResponseModel(promptModel);

        // Don't call showScreen() if SELL_ITEM is the current screen
        // because showScreen() will result in resetting the scanner session.
        try
        {
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            if (ui.getActiveScreenID() == POSUIManagerIfc.SELL_ITEM)
            {
                ui.setModel(POSUIManagerIfc.SELL_ITEM, beanModel);
            }
            else
            {
                // If both property true, change the prompt reponse field length
                if (imeiEnabled && serializationEnabled)
                {
                  
                  promptModel.setMaxLength(imeiResponseFieldLength);
                  beanModel.setPromptAndResponseModel(promptModel);
                }
                ui.showScreen(POSUIManagerIfc.SELL_ITEM, beanModel);
                try
                {
                    LineItemsModel tempBeanModel = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
                    cargo.setMaxPLUItemIDLength(Integer.valueOf(tempBeanModel.getPromptAndResponseModel()
                            .getMaxLength()));
                }
                catch (Exception e)
                {
                    logger.warn("Unable to get the maximum PLU item ID length", e);
                }
            }
        }
        catch (UIException uie)
        {
            logger.warn("Unable to get the active screen ID");
        }

        // line display part
        setLineDisplay(bus, cargo, transaction, utility, beanModel.getLineItems());

        // cpoi part
        setCPOIDisplay(bus, cargo, transaction, beanModel.getLineItems());   
        
        // set debug timestamp and print to console
        if (debugRoundtripTimestamp != 0 && logger.isDebugEnabled())
        {
            String message = "Roundtrip to ShowSaleScreen is " + (System.currentTimeMillis() - debugRoundtripTimestamp) + "ms";
            logger.debug(message);
            System.out.println(message);
        }
        // hide the on screen keyboard if the transaction just started.
        bus.mail("HideOnScreenKeyboard");
        
        if (dualDisplayEnabled)
        {
          //If dualdisplay is enabled, show sale screen on second display 
            DualDisplayManagerIfc dualDisplayManager = (DualDisplayManagerIfc)bus.getManager(DualDisplayManagerIfc.TYPE);            
            dualDisplayManager.showSaleScreen(DualDisplayManagerIfc.DUALDISPLAY_SELLITEM, beanModel);             
        }
    }

    /**
     * Transaction in cargo is null. Update menus and screen accordingly.
     *
     * @param cargo
     * @param pm
     * @param localModel
     * @param globalModel
     */
    protected void updateScreenForNoTransaction(BusIfc bus, LineItemsModel beanModel)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);          
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();        
        NavigationButtonBeanModel localModel = beanModel.getLocalButtonBeanModel();
        NavigationButtonBeanModel globalModel = beanModel.getGlobalButtonBeanModel();

        // initialize lineitem list on cargo
        globalModel.setButtonEnabled(CommonActionsIfc.UNDO, true);
        localModel.setButtonEnabled(CommonActionsIfc.NOSALE, true);
        globalModel.setButtonEnabled(CommonActionsIfc.CANCEL, false);
        localModel.setButtonEnabled(CommonActionsIfc.TENDER, false);
        localModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, true);
        localModel.setButtonEnabled(CommonActionsIfc.TILLFUNCTIONS, true);
        localModel.setButtonEnabled(CommonActionsIfc.RETURN, true);
        localModel.setButtonEnabled(CommonActionsIfc.REPRINT_RECEIPT, true);
        localModel.setButtonEnabled(CommonActionsIfc.GIFT_CARD_CERT, true);
        localModel.setButtonEnabled(CommonActionsIfc.PRICING,  true);

        // House Account button should be disabled if house account is not a supported card type
        try
        {
            boolean houseAccountAccepted = pm.getBooleanValue(ParameterConstantsIfc.TENDER_HouseCardsAccepted);
            localModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, houseAccountAccepted);
        }
        catch (ParameterException pe)
        {
            logger.error("Unable to get parameter values for CreditCardTypes" + Util.throwableToString(pe));
        }

        cargo.setLineItems(null);
        
        // Display the prompt message 
        boolean retrieveValueForSuspendedTransactionOnsaleScreen = retrieveValueForSuspendedTransactionOnSaleScreen(bus);
        cargo.setRetrieveSuspendedTransactionOnSaleScreen(retrieveValueForSuspendedTransactionOnsaleScreen);
    }
    
    /**
     * Transaction is not null. Update menus and screen accordingly.
     *
     * @param localModel 
     * @param beanModel 
     * @param transaction
     */
    @SuppressWarnings("unchecked")
    protected void updateScreenForTransaction(BusIfc bus, LineItemsModel beanModel, SaleReturnTransactionIfc transaction)
    {
        NavigationButtonBeanModel localModel = beanModel.getLocalButtonBeanModel();
        NavigationButtonBeanModel globalModel = beanModel.getGlobalButtonBeanModel();
        TotalsBeanModel totalsModel = beanModel.getTotalsBeanModel();
        StatusBeanModel statusModel = beanModel.getStatusBeanModel();

        ArrayList<AbstractTransactionLineItemIfc> itemList = new ArrayList<AbstractTransactionLineItemIfc>(transaction.getLineItemsSize());

        // Reset the transaction status to "In Progress" on returning of 'No'
        // from cancel transaction prompt
        if (bus.getCurrentLetter() != null && CommonLetterIfc.FAILURE.equals(bus.getCurrentLetter().getName())
                && transaction.getTransactionStatus() == TransactionConstantsIfc.STATUS_CANCELED
                && transaction.getPreviousTransactionStatus() == TransactionConstantsIfc.STATUS_IN_PROGRESS)
        {
            transaction.setTransactionStatus(TransactionConstantsIfc.STATUS_IN_PROGRESS);
        }

        // Disable Redeem button when transaction != null
        localModel.setButtonEnabled(CommonActionsIfc.REDEEM, false);
        itemList.addAll(Arrays.asList(transaction.getLineItemsExceptExclusions()));

        // sort the line items list by line number
        Collections.sort(itemList, Comparators.lineNumberAscending);

        beanModel.setTimerModel(new DefaultTimerModel(bus, transaction != null));

        if (transaction != null && transaction.getSalesAssociate() != null)
        {
            if (transaction.getSalesAssociateModifiedFlag())
            {
                for (int i = itemList.size() - 1; i >= 0; i--)
                {
                    itemList.get(i).setSalesAssociateModifiedFlag(true);

                    if (!Util.isObjectEqual(itemList.get(i).getSalesAssociate(), transaction.getSalesAssociate()))
                    {
                        itemList.get(i).setSalesAssociateModifiedAtLineItem(true);
                    }
                }
            }

        }

        if (isLineItemContainReturnableItem(itemList))
        {
            for (AbstractTransactionLineItemIfc item : itemList)
            {
                SaleReturnLineItemIfc saleReturnItem = (SaleReturnLineItemIfc)item;
                saleReturnItem.setHasReturnItem(true);
            }
        }
        if (isLineItemContainSendItem(itemList))
        {
            for (AbstractTransactionLineItemIfc item : itemList)
            {
                SaleReturnLineItemIfc saleReturnItem = (SaleReturnLineItemIfc)item;
                saleReturnItem.setHasSendItem(true);
            }
        }

        // enable Tender button
        if (itemList.size() > 0)
        {
            beanModel.setLineItems(itemList.toArray(new AbstractTransactionLineItemIfc[itemList.size()]));
            if (transaction.isTransactionLevelSendAssigned())
            {
                // enabled only when there is at least one send item
                // in transaction level send (as per reqs.)
                localModel.setButtonEnabled(CommonActionsIfc.TENDER, transaction.hasSendItems());
            }
            else
            {
                localModel.setButtonEnabled(CommonActionsIfc.TENDER, true);
            }
            beanModel.setSelectedRow(itemList.size() - 1);

            boolean retrieveExtendedData = Gateway.getBooleanProperty(
                    Gateway.APPLICATION_PROPERTIES_GROUP, "RetrieveExtendedData", false);

            if (retrieveExtendedData)
            {
                int maxRecommendedItemsListSize = Integer.parseInt(Gateway.getProperty(Gateway.APPLICATION_PROPERTIES_GROUP, "MaxRecommendedItemsListSize", "100"));
                beanModel.setRecommendedItems(transaction.getRecommendedItems(maxRecommendedItemsListSize));
            }
        }
        else
        {
            localModel.setButtonEnabled(CommonActionsIfc.TENDER, false);
            beanModel.setSelectedRow(-1);
        }
        
        // Check for Shipping Method in Transaction level send
        // Discard all shipping method info if required (as per reqs.)
        if (transaction.isTransactionLevelSendAssigned())
        {
            ShippingMethodIfc sendShippingMethod = transaction.getSendPackages()[0].getShippingMethod();
            CustomerIfc sendCustomer = transaction.getSendPackages()[0].getCustomer();
            ShippingMethodIfc shippingMethod = DomainGateway.getFactory().getShippingMethodInstance();
            if (sendShippingMethod != null && !sendShippingMethod.equals(shippingMethod))
            {
                transaction.updateSendPackageInfo(0, shippingMethod, sendCustomer);
                // Must do this to force tax recalculation
                transaction.updateTransactionTotals();
            }
        }

        // If there is a transaction, send the transaction totals that
        // can be displayed to the UI.

        // Before display taxTotals, need to convert the longer precision
        // calculated total tax amount back to shorter precision tax total
        // amount for UI display.
        transaction.getTransactionTotals().setTaxTotal(transaction.getTransactionTotals().getTaxTotalUI());

        // Now, display on the UI.
        totalsModel.setTotals(transaction.getTransactionTotals(), calculateAmountPaid(transaction));

        OrderTransaction orderTransaction = null;
        if (transaction instanceof OrderTransaction)
        {
            orderTransaction = (OrderTransaction)transaction;
            if (orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_SPECIAL)
            {
                localModel.setButtonEnabled(CommonActionsIfc.GIFT_CARD_CERT, false);
            }
        }

        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        // Set screen name to Layaway Item if transaction is a layaway
        // disable no sale, customer, and return buttons
        int transType = transaction.getTransactionType();
        if (transType == TransactionIfc.TYPE_LAYAWAY_INITIATE || transType == TransactionIfc.TYPE_ORDER_INITIATE)
        {
            localModel.setButtonEnabled(CommonActionsIfc.CUSTOMER, false);
            localModel.setButtonEnabled(CommonActionsIfc.RETURN, false);

            if (transType == TransactionIfc.TYPE_ORDER_INITIATE
                    && orderTransaction.getOrderType() != OrderConstantsIfc.ORDER_TYPE_ON_HAND)
            {
                String spOrdItem = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                        BundleConstantsIfc.POS_BUNDLE_NAME, SP_ORD_ITEM_SCREEN_NAME_TAG,
                        SP_ORD_ITEM_SCREEN_NAME_TEXT, LocaleConstantsIfc.USER_INTERFACE);
                statusModel.setScreenName(spOrdItem);
            }
            else if (transType == TransactionIfc.TYPE_ORDER_INITIATE
                    && orderTransaction.getOrderType() == OrderConstantsIfc.ORDER_TYPE_ON_HAND)
            {
                String PDOOrdItem = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                        BundleConstantsIfc.POS_BUNDLE_NAME, PDO_ORD_ITEM_SCREEN_NAME_TAG,
                        PDO_ORD_ITEM_SCREEN_NAME_TEXT, LocaleConstantsIfc.USER_INTERFACE);
                statusModel.setScreenName(PDOOrdItem);
            }
            else
            {
                String layawayItem = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                        BundleConstantsIfc.POS_BUNDLE_NAME, LAYAWAY_ITEM_SCREEN_NAME_TAG,
                        LAYAWAY_ITEM_SCREEN_NAME_TEXT, LocaleConstantsIfc.USER_INTERFACE);
                statusModel.setScreenName(layawayItem);
            }
        }
        else if (transaction.hasExternalOrder() ||  transaction.isOrderPickupOrCancel())
        {
            localModel.setButtonEnabled(CommonActionsIfc.RETURN, false);
        }
        else
        {
            localModel.setButtonEnabled(CommonActionsIfc.RETURN, true);
        }

        if (transaction.getTransactionTax().getTaxMode() == TaxIfc.TAX_MODE_EXEMPT)
        {
            localModel.setButtonEnabled(CommonActionsIfc.CUSTOMER, false);
        }

        // Show linked customer, if applicable.
        // If no customer, clear the customer field.  Customer history, for one, can leave wrong name in field.
        CustomerIfc customer = (transaction.getCustomer() != null) ? transaction.getCustomer() : (CustomerIfc)transaction.getCaptureCustomer();
        String customerName = getDisplayCustomerName(customer, utility);
        statusModel.setCustomerName(customerName);

        disableButtonsWhileTransactionInProgress(globalModel, localModel);
        if (transaction.isWebManagedOrder())
        {
            disableButtonsForWebManagedOrder(globalModel, localModel);
        }
    }

    /**
     * @since 14.0.1
     */
    protected void disableButtonsWhileTransactionInProgress(NavigationButtonBeanModel globalModel, NavigationButtonBeanModel localModel)
    {
        localModel.setButtonEnabled(CommonActionsIfc.TRAINING_ON_OFF, false);
        localModel.setButtonEnabled(CommonActionsIfc.NOSALE, false);
        globalModel.setButtonEnabled(CommonActionsIfc.UNDO, false);
        globalModel.setButtonEnabled(CommonActionsIfc.CANCEL, true);
        localModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, false);
        localModel.setButtonEnabled(CommonActionsIfc.TILLFUNCTIONS, false);
        localModel.setButtonEnabled(CommonActionsIfc.REPRINT_RECEIPT, false);
    }

    /**
     * Disable all options that are not allowed when having resumed a transaction
     * that was begun on the web channel (i.e. ASA).
     * 
     * @param localModel
     * @since 14.0.1
     */
    protected void disableButtonsForWebManagedOrder(NavigationButtonBeanModel globalModel, NavigationButtonBeanModel localModel)
    {
        localModel.setButtonEnabled(CommonActionsIfc.RETURN, false);
        localModel.setButtonEnabled(CommonActionsIfc.ITEM_INQUIRY, false);
        localModel.setButtonEnabled(CommonActionsIfc.ITEM, false);
        localModel.setButtonEnabled(CommonActionsIfc.CUSTOMER, false);
        localModel.setButtonEnabled(CommonActionsIfc.PRICING, false);
        localModel.setButtonEnabled(CommonActionsIfc.NOSALE, false);
        localModel.setButtonEnabled(CommonActionsIfc.HOUSEACCOUNT, false);
        localModel.setButtonEnabled(CommonActionsIfc.REPRINT_RECEIPT, false);
        localModel.setButtonEnabled(CommonActionsIfc.TILLFUNCTIONS, false);
        localModel.setButtonEnabled(CommonActionsIfc.GIFT_CARD_CERT, false);
        localModel.setButtonEnabled(CommonActionsIfc.REDEEM, false);
        localModel.setButtonEnabled(CommonActionsIfc.SCANSHEET, false);
    }
    
    /**
     * Returns the customer name
     * @param customer
     * @param utility
     */
    protected String getDisplayCustomerName(CustomerIfc customer, UtilityManagerIfc utility)
    {
        /** Customer name bundle tag **/
        final String CUSTOMER_NAME_TAG = "CustomerName";
        
        /** Customer name default text **/
        final String CUSTOMER_NAME_TEXT = "{0} {1}";
        
        String displayCustomerName;
        
        if (customer != null && utility != null)
        {
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            if(customer.isBusinessCustomer())
            {
                parms[0]=customer.getLastName();
                parms[1]="";
            }
            String pattern =
                utility.retrieveText("CustomerAddressSpec",
                        BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                        CUSTOMER_NAME_TAG,
                        CUSTOMER_NAME_TEXT);
            displayCustomerName = LocaleUtilities.formatComplexMessage(pattern, parms);
        }
        else
        {
            displayCustomerName = "";
        }
        
        return displayCustomerName;
    }

    private boolean retrieveValueForSuspendedTransactionOnSaleScreen(BusIfc bus)
    {
        boolean isSupported = false;
        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
            boolean value = pm.getBooleanValue(ParameterConstantsIfc.SALE_ResumeSuspendedTransactionOnSaleScreen);

            if (value)
            {
                isSupported = true;
            }
        }
        catch (ParameterException e)
        {
            logger.error("Could not determine retrieve Suspended Transaction On Sale Screen setting.", e);
        }

        return isSupported;
    }

    /**
     * Method to set the line display. Should be refactored into an aisle.
     *
     * @param bus
     * @param cargo
     * @param pda
     * @param transaction
     * @param utility
     */
    protected void setLineDisplay(BusIfc bus, SaleCargoIfc cargo, 
            SaleReturnTransactionIfc transaction,
            UtilityManagerIfc utility, AbstractTransactionLineItemIfc[] lineItems)
    {
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc)bus);
        try
        {
            if (transaction != null && lineItems.length > 0)
            {
                pda.lineDisplayItem((SaleReturnLineItemIfc)lineItems[lineItems.length - 1]);
                // update running tax and total on line display
                String taxTag = utility.retrieveLineDisplayText("TaxAbbreviatedText", TagConstantsIfc.SHORT_TAX_TAG);
                String totalTag = utility.retrieveLineDisplayText("TotalAbbreviatedText",
                        TagConstantsIfc.SHORT_TOTAL_TAG);
                StringBuffer displayLine2 = new StringBuffer(taxTag).append(
                        Util.formatTextData(makeShorter(transaction.getTransactionTotals().getTaxTotalUI()), 7, true))
                        .append(totalTag).append(
                                Util.formatTextData(makeShorter(transaction.getTransactionTotals().getBalanceDue()), 9,
                                        true));
                pda.displayTextAt(1, 0, displayLine2.toString());
            }
            else
            {
                pda.clearText();
            }
        }
        catch (DeviceException de)
        {
            logger.warn("Unable to use Line Display: " + de.getMessage() + "");
        }
    }

    /**
     * Method to set the display for the CPOI device. Should be refactored into an aisle.
     *
     * @param cargo
     * @param pda
     * @param transaction
     * @param itemList
     */
    protected void setCPOIDisplay(BusIfc bus, SaleCargoIfc cargo,
            SaleReturnTransactionIfc transaction, AbstractTransactionLineItemIfc[] lineItems)
    {
        WorkstationIfc workstation = cargo.getRegister().getWorkstation();

        if (transaction != null && lineItems.length > 0)
        {
            PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
            if (cargo.isRefreshNeeded())
            {
                // refresh the items list and totals on CPOI
                paymentManager.refreshItems(workstation, transaction.getLineItemsVector(), transaction);
                cargo.setRefreshNeeded(false);
            }
            else
            {
                // show the item and totals on CPOI
                paymentManager.addItem(workstation, (SaleReturnLineItemIfc)lineItems[lineItems.length - 1], transaction);
            }
        }
        else
        {
            PaymentManagerIfc paymentManager = (PaymentManagerIfc)bus.getManager(PaymentManagerIfc.TYPE);
            paymentManager.clearSwipeAheadData(workstation);
            paymentManager.showLogo(workstation);
        }
    }


    /**
     * Updates the item index in the cargo to the currently selected item.
     *
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        // set debug timestamp
        debugRoundtripTimestamp = System.currentTimeMillis();

        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UIModelIfc model = ui.getModel(POSUIManagerIfc.SELL_ITEM);
        if (model instanceof LineItemsModel)
        {
            LineItemsModel beanModel = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
            if (beanModel != null)
            {
                int rows[] = beanModel.getSelectedRows();
                cargo.setIndices(rows);
            }
        }
        cargo.setItemSerial(null);

        if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.UNDO))
        {
            cargo.setCanSkipCustomerPrompt(true);
        }
        cargo.setSalesAssociate(cargo.getEmployee());

        LetterIfc letter = bus.getCurrentLetter();
        
        if (CommonLetterIfc.TIMEOUT.equals(letter.getName()) && cargo instanceof TimedCargoIfc)
        {
            ((TimedCargoIfc)cargo).setTimeout(true);
            
            showDualDisplayMainScreen(bus);
        }
        
        if (letter instanceof ButtonPressedLetter)
        {
            String letterName = letter.getName();
            if (letterName != null && letterName.equals(CommonLetterIfc.UNDO))
            {                
                showDualDisplayMainScreen(bus);                
                // Audit Logging UserEvent for user logout
                AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();

                UserEvent ev = (UserEvent)AuditLoggingUtils.createLogEvent(UserEvent.class, AuditLogEventEnum.LOG_OUT);
                ev.setStoreId(cargo.getOperator().getStoreID());
                RegisterIfc ri = cargo.getRegister();
                if (ri != null)
                {
                    WorkstationIfc wi = ri.getWorkstation();
                    if (wi != null)
                    {
                        ev.setRegisterNumber(wi.getWorkstationID());
                    }
                }
                ev.setUserId(cargo.getOperator().getLoginID());
                ev.setStatus(AuditLoggerConstants.SUCCESS);
                ev.setEventOriginator("ShowSaleScreenSite.depart");
                auditService.logStatusSuccess(ev);
            }
        }
    }

    /**
     * Show the Dual Display main screen if enabled
     * @param bus
     */
    protected void showDualDisplayMainScreen(BusIfc bus)
    {
        if (dualDisplayEnabled)
        {
            //Show default screen on the dual display
            DualDisplayManagerIfc dualDisplayManager = (DualDisplayManagerIfc)bus.getManager(DualDisplayManagerIfc.TYPE);
            dualDisplayManager.showMainScreen();
        }
    }

    /**
     * Take away the extra digits starting from the 3rd one after decimal point
     *
     * @param longAmount
     *            CurrencyIfc object
     * @return shortString String
     */
    public String makeShorter(CurrencyIfc longAmount)
    {
        String shortString = "";

        Locale locale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        shortString = CurrencyServiceLocator.getCurrencyService().formatCurrency(longAmount, locale);

        return (shortString);
    }

    /**
     * Method to get the return line item .
     *
     * @param item AbstractTransactionLineItemIfc array
     * @return boolean result
     */
    protected boolean isLineItemContainReturnableItem(List<AbstractTransactionLineItemIfc> items)
    {
        for (AbstractTransactionLineItemIfc item : items)
        {
            SaleReturnLineItemIfc saleReturnItem = (SaleReturnLineItemIfc)item;
            if (saleReturnItem.isReturnLineItem())
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Method to get the send line item .
     *
     * @param item AbstractTransactionLineItemIfc array
     * @return boolean result
     */
    protected boolean isLineItemContainSendItem(List<AbstractTransactionLineItemIfc> items)
    {
        for (AbstractTransactionLineItemIfc item : items)
        {
            SaleReturnLineItemIfc saleReturnItem = (SaleReturnLineItemIfc)item;
            if (saleReturnItem.getItemSendFlag())
            {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Calculate amount which has been paid when doing order initiate. 
     * For special order, the amount paid is the deposit;
     * @param transaction
     * @return amount paid
     */
    protected CurrencyIfc calculateAmountPaid(SaleReturnTransactionIfc transaction)
    {
        CurrencyIfc amountPaid = DomainGateway.getBaseCurrencyInstance();
        if (transaction instanceof OrderTransaction)
        {
            OrderTransactionIfc orderTxn = (OrderTransaction)transaction;
            if (orderTxn.getOrderType()== OrderConstantsIfc.ORDER_TYPE_SPECIAL || 
                orderTxn.getOrderType()== OrderConstantsIfc.ORDER_TYPE_ON_HAND) 
            {
                //Payment will be not null only at the time for order picking up or canncelling.
                if (orderTxn.getPayment() != null)
                {
                    //payment amount from Payment is the amount going to pay.
                    amountPaid = transaction.getTransactionTotals().getGrandTotal().subtract(orderTxn.getPayment().getPaymentAmount());
                }
            }
        }
        return amountPaid;
    }    
}
