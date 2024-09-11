/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SaleLineItemRenderer.java /main/85 2014/06/20 15:58:38 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/20/14 - Not display item status if it is not pickup or
 *                         cancel.
 *    yiqzhao   05/07/14 - Add order status column in the first row.
 *    yiqzhao   05/02/14 - Display pickup image and text on show sale screen
 *                         for pickup items while picking up items from an
 *                         order
 *    yiqzhao   10/31/13 - Read serialLabel from Renderer.SerialLabel of common
 *                         properties.
 *    jswan     10/02/13 - Fixed issue with displaying the price for Kit
 *                         headers that have components with discounts.
 *    yiqzhao   10/01/13 - Make kit header not order item, but display the
 *                         order image and delivery store id on Sale Item
 *                         Screen since kit components are hidden on the
 *                         screen.
 *    yiqzhao   09/05/13 - Get correct kit item price when a quantity of one
 *                         kit component is greater than one.
 *    abhinavs  08/05/13 - Fix to display localized return item message.
 *    rabhawsa  06/28/13 - adding transaction level discount.
 *    subrdey   04/25/13 - Getting Item Level Message based on locale.
 *    rabhawsa  04/19/13 - GridBagConstraints.RELATIVE is used in method
 *                         layoutOptionalReturnLineItemLabels to avoid
 *                         overlapping messages.
 *    tksharma  04/18/13 - moved call to layoutItemLevelScreenText() before the
 *                         layoutOptionalReturnLineItemLabels() in
 *                         layoutOptions() method to fix overlap issues
 *    rgour     04/17/13 - Removing Cny from line item renderer
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    rgour     04/01/13 - CBR cleanup
 *    yiqzhao   03/18/13 - Add store id for pickup at show item screen.
 *    subrdey   03/08/13 - POS hangs when retrieving transaction for
 *                         PriceAdjustment - Fixed.
 *    subrdey   03/07/13 - Changed the Horizontal Alignment of Discount to
 *                         CENTER.
 *    cgreene   12/11/12 - allow sale renderer to show item's promotion name
 *    cgreene   11/27/12 - enhancement for displaying item images in sale
 *                         screen table
 *    cgreene   10/26/12 - Change item description to a urllabel
 *    yiqzhao   09/27/12 - add related item image for sell item screen.
 *    abhineek  09/10/12 - Fix for configuring item messages by plaf properties
 *    yiqzhao   09/04/12 - display store id for kit item
 *    yiqzhao   08/31/12 - display pickup and delivery icons without date/stoer
 *                         for kit header.
 *    yiqzhao   08/28/12 - shift discount column.
 *    yiqzhao   08/07/12 - rename super class
 *    yiqzhao   08/06/12 - tax label layout -- right on Ext Price
 *    yiqzhao   08/03/12 - Change tax indicator position for sell item screen
 *                         and transaction detail screen
 *    rgour     07/31/12 - Fixed the issues, related to WPTG
 *    sgu       07/03/12 - replace item disposition code to use delivery
 *                         instead of ship
 *    sgu       07/03/12 - merge with tip
 *    sgu       07/03/12 - check in after merge
 *    sgu       07/03/12 - added xc order ship delivery date, carrier code and
 *                         type code
 *    yiqzhao   07/03/12 - auto merged with the previous checkin
 *    yiqzhao   07/03/12 - refine shipping flow
 *    yiqzhao   07/02/12 - Remove unnecessary check for shipping charge line
 *                         item.
 *    yiqzhao   07/02/12 - Read text from orderText bundle file and define
 *                         screen names
 *    yiqzhao   06/29/12 - Add dialog for deleting ship item, disable change
 *                         price for ship item
 *    sgu       06/27/12 - set item disposition code for ship to store item
 *    yiqzhao   05/08/12 - leave a space between Send and index
 *    rsnayak   03/22/12 - cross border return changes
 *    hyin      11/10/11 - revert back using long description per requirement
 *                         team.
 *    hyin      11/07/11 - use item short description
 *    cgreene   11/03/10 - rename ItemLevelMessageConstants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    jswan     06/01/10 - Modified to support transaction retrieval
 *                         performance and data requirements improvements.
 *    jswan     05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    jswan     04/23/10 - Merges due to refresh to label.
 *    jswan     04/23/10 - Refactored CTR to include more data in the
 *                         SaleReturnLineItem class and table to reduce the
 *                         data required in and retveived from the CO database.
 *                         Modified this class to handle item description
 *                         issues associated with this change.
 *    acadar    04/12/10 - use default locale for display of currency
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency display
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    jswan     06/10/09 - Items which have been picked will not display the
 *                         logo.
 *    jswan     05/21/09 - Code Review
 *    jswan     05/21/09 - Modified to prevent the pickup/delivery images from
 *                         displaying for return items.
 *    cgreene   05/01/09 - string pooling performance aenhancements
 *    vikini    03/24/09 - Clear text from Optional Lines pertaining to
 *                         restocking fee
 *    nkgautam  03/06/09 - Associate name length changed for maximum thirty
 *                         multi-byte chinese characters to accomodate on sale
 *                         screen
 *    nkgautam  02/28/09 - Clipped associate name to 50 characters and added a
 *                         method makeSafeString to clip the name to desired
 *                         length
 *    mahising  02/26/09 - Rework for PDO functionality
 *    acadar    02/25/09 - override the getDefaultLocale from JComponent
 *    acadar    02/25/09 - use application default locale instead of jvm locale
 *    ddbaker   02/18/09 - Corrected alignment issue on Sale screen.
 *    mkochumm  02/12/09 - use default locale for dates
 *    ddbaker   02/10/09 - Updated with code review comments.
 *    ddbaker   02/10/09 - Made restocking fee span two lines. This lent itself
 *                         to cleaning up some redundant constants. Also
 *                         removed extraneous icon related code that was made
 *                         obsolete.
 *    ddbaker   02/09/09 - Merged to moving target.
 *    ddbaker   02/06/09 - Rearranged location of pickup and delivery
 *                         information for clearer display on sale screen. Also
 *                         ensure that optional and sale data don't interfere
 *                         with each other.
 *    ddbaker   01/06/09 - Removed duplicate additions of labels to renderer.
 *                         Labels are correctly added by
 *                         AbstractListRenderer.initLabels() only.
 *    aphulamb  01/05/09 - fixed QA issue
 *    aphulamb  01/02/09 - fix delivery issues
 *    vikini    12/23/08 - Made changes to show Return Message instead of Sale
 *                         Message on Return Item Screen
 *    aphulamb  12/17/08 - bug fixing of PDO
 *    aphulamb  12/10/08 - returns functionality changes for greying out
 *                         buttons
 *    aphulamb  11/27/08 - checking files after merging code for receipt
 *                         printing by Amrish
 *    aphulamb  11/24/08 - Checking files after code review by amrish
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    vikini    11/14/08 - Fixed the extra line break in each Line Item not
 *                         having ILRM Screen Message
 *    ddbaker   11/13/08 - Eliminate button clipping by adjusting font
 *    ddbaker   11/11/08 - Merge Changes
 *    ddbaker   11/10/08 - Updated based on new requirements
 *    ddbaker   11/06/08 - Update due to merges.
 *    vikini    11/10/08 - Incorporating Code Review findings
 *    vikini    11/08/08 - Line Break in Sale Item Screen for ILRM Messages
 *
 * ===========================================================================
 * $Log:
 *   13   360Commerce 1.12        3/25/2008 4:06:54 AM   Vikram Gopinath CD
 *        #29942, ported code from v12x. Display the sales associate employee
 *        id if the person information is not present.
 *   12   360Commerce 1.11        2/27/2008 3:19:23 PM   Alan N. Sinton  CR
 *        29989: Changed masked to truncated for UI renders of PAN.
 *   11   360Commerce 1.10        12/16/2007 5:57:17 PM  Alan N. Sinton  CR
 *        29598: Fixes for various areas broke from PABP changes.
 *   10   360Commerce 1.9         7/9/2007 3:07:53 PM    Anda D. Cadar   I18N
 *        changes for CR 27494: POS 1st initialization when Server is offline
 *   9    360Commerce 1.8         5/8/2007 11:32:29 AM   Anda D. Cadar
 *        currency changes for I18N
 *   8    360Commerce 1.7         4/25/2007 8:58:29 AM   Anda D. Cadar   I18N
 *        merge
 *   7    360Commerce 1.6         4/24/2007 11:05:23 AM  Ashok.Mondal    CR
 *        4381 :V7.2.2 merge to trunk.
 *   6    360Commerce 1.5         1/25/2006 4:11:44 PM   Brett J. Larsen merge
 *        7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *   5    360Commerce 1.4         1/22/2006 11:45:28 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   4    360Commerce 1.3         1/21/2006 9:56:27 PM   Kulbhushan Sharma Some
 *         code refactoring
 *   3    360Commerce 1.2         3/31/2005 4:29:48 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:24:58 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:14:00 PM  Robert Pearse
 *:
 *   6    .v700     1.2.1.2     1/4/2006 11:59:25      Deepanshu       CR 6160:
 *        Set quantity as blank for Alterations
 *   5    .v700     1.2.1.1     10/26/2005 11:51:24    Jason L. DeLeau 6087:
 *        Change the scope of layoutOptions from default to protected, for
 *        services extensibility purposes. (Forgot to add comment in previous
 *        version)
 *   4    .v700     1.2.1.0     10/26/2005 11:34:57    Jason L. DeLeau 60
 *   3    360Commerce1.2         3/31/2005 15:29:48     Robert Pearse
 *   2    360Commerce1.1         3/10/2005 10:24:58     Robert Pearse
 *   1    360Commerce1.0         2/11/2005 12:14:00     Robert Pearse
 *
 *  Revision 1.22  2004/07/15 23:00:08  jriggins
 *  @scr 6309 Capture the absolute value of discount values for presentation reasons.
 *
 *  Revision 1.21  2004/07/12 20:13:55  mweis
 *  @scr 6158 "Gift Card ID:" label not appearing correctly
 *
 *  Revision 1.20  2004/06/04 22:35:56  mweis
 *  @scr 4250 Return's restocking fee incorrectly calculated
 *
 *  Revision 1.19  2004/05/21 13:44:56  dfierling
 *  @scr 3987 - updated column widths
 *
 *  Revision 1.18  2004/05/05 14:05:49  rsachdeva
 *  @scr 4670 Send: Multiple Sends
 *
 *  Revision 1.17  2004/04/27 21:30:24  jriggins
 *  @scr 3979 Code review cleanup
 *
 *  Revision 1.16  2004/04/22 20:09:10  mweis
 *  @scr 4507 Deal Item indicator - code review updates
 *
 *  Revision 1.15  2004/04/21 20:35:30  mweis
 *  @scr 4507 Deal Item indicator - initial submission
 *
 *  Revision 1.14  2004/04/16 13:51:34  mweis
 *  @scr 4410 Price Override indicator -- initial submission
 *
 *  Revision 1.13  2004/04/15 15:43:18  jriggins
 *  @scr 3979 Added price adjustment checks using instanceof
 *
 *  Revision 1.12  2004/04/13 16:04:33  mweis
 *  @scr 4206 JavaDoc updates.
 *
 *  Revision 1.11  2004/04/09 16:56:00  cdb
 *  @scr 4302 Removed double semicolon warnings.
 *
 *  Revision 1.10  2004/04/08 20:33:02  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  Revision 1.9  2004/04/03 00:23:38  jriggins
 *  @scr 3979 Price Adjustment feature dev
 *
 *  Revision 1.8  2004/03/26 05:39:05  baa
 *  @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *  Revision 1.7  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.6  2004/03/12 21:44:26  rsachdeva
 *  @scr Sale Item Size
 *
 *  Revision 1.5  2004/03/02 16:23:00  rsachdeva
 *  @scr 3906 Whole Number Format
 *
 *  Revision 1.3  2004/03/01 15:39:19  rsachdeva
 *  @scr 3906 Unit of Measure
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.4   Dec 19 2003 15:21:56   lzhao
 * issue code review follow up
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.3   Dec 16 2003 11:41:24   lzhao
 * gift card issue hide/show item id.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.2   Dec 12 2003 14:20:30   lzhao
 * move gift card issue related task to giftcard/issue package.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 *
 *    Rev 1.1   Sep 23 2003 11:18:46   rsachdeva
 * Suspended Transaction being Retrieved should display Restocking Fee
 * Resolution for POS SCR-2753: Retrieve supended return trans, restocking fee not shows on Sell Item screen
 *
 *    Rev 1.0   Aug 29 2003 16:11:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.11   Apr 16 2003 13:00:46   pdd
 * Removed reference to DomainUtilities
 * Resolution for 2103: Remove uses of deprecated items in POS.
 *
 *    Rev 1.10   Apr 09 2003 14:03:16   baa
 * data base conversion / plaf cleanup
 * Resolution for POS SCR-1866: I18n Database  support
 *
 *    Rev 1.9   Mar 24 2003 10:08:18   baa
 * remove reference to foundation.util.EMPTY_STRING
 * Resolution for POS SCR-2101: Remove uses of  foundation constant  EMPTY_STRING
 *
 *    Rev 1.8   Mar 07 2003 17:11:10   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.7   Jan 15 2003 14:11:02   bwf
 * In sizeOptionalField, check if serial number label.  If it is then override to length of 136.
 *
 *    Rev 1.6   Oct 09 2002 16:20:06   jriggins
 * Pulling the tax mode from the bundle.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.5   Sep 25 2002 09:52:26   jriggins
 * Retrieving tax mode character from the bundle
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.4   Sep 18 2002 17:15:32   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.3   Sep 06 2002 17:25:36   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 23 2002 14:43:04   HDyer
 * Added optional sale line for restocking fee. Added getListCellRendererComponent method so foreground colors would be correct.
 * Resolution for 1774: Restocking Fee not displayed on Sell Item screen when applied
 *
 *    Rev 1.1   Aug 14 2002 18:18:32   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:57:24   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:34:38   dfh
 * removde Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 *
 *    Rev 1.0   Mar 18 2002 11:57:22   msg
 * Initial revision.
 *
 *    Rev 1.20   13 Mar 2002 17:08:08   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.19   28 Feb 2002 10:01:08   sfl
 * Expaneded the size of optionFields array to six.
 * Resolution for POS SCR-1421: Newly added a requirement to display Send in the Sell Item screen
 *
 *    Rev 1.18   Feb 27 2002 21:25:56   mpm
 * Continuing work on internationalization
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.constants.ItemLevelMessageConstants;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.discount.ItemDiscountStrategyIfc;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.ItemSizeConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.gui.URLLabel;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.DisplayItemInfoAction;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.utility.PLUItemUtility;

/**
 *  Renderer for SaleReturnLineItems.
 */
public class SaleLineItemRenderer extends LineItemRenderer
    implements CodeConstantsIfc, ItemLevelMessageConstants
{
    private static final long serialVersionUID = 3238897613749845147L;
    /** Switch this flag to true to cause the renderer to display colors to aid visual alignment. */
    private static boolean DEBUG_MODE = false; 

    /** Default width for first line layout. Sets {@link AbstractListRenderer#firstLineWidths} */
    public static int[] LINE_WIDTHS = { 1,4,1 };

    /** Default height for first line layout. Sets {@link AbstractListRenderer#firstLineHeights} */
    public static int[] LINE_HEIGHTS = { 2,1,1 };

    /** Default weight for first line layout. Sets {@link AbstractListRenderer#firstLineWeights} */
    public static int[] LINE_WEIGHTS = { 7,80,7 };

    /** Default weight for second line layout. Sets {@link AbstractListRenderer#secondLineWeights} */
    public static int[] LINE_WEIGHTS2 = { 22,8,22,20,19,3 };

    /** Default width for second line layout. Sets {@link AbstractListRenderer#secondLineWidths} */
    public static int[] LINE_WIDTHS2 = { 1,1,1,1,1,1 };

    /** the description column */
    public static int ICON = 0;

    /** the description column */
    public static int DESCRIPTION = 1;
    
    /** the status column */
    public static int STATUS      = 2;

    /** the stock column */
    public static int STOCK = 3;

    /** the quantity column */
    public static int QUANTITY = 4;

    /** the price column */
    public static int PRICE = 5;

    /** the discount column */
    public static int DISCOUNT = 6;

    /** the ext_price column */
    public static int EXT_PRICE = 7;

    /** the tax column */
    public static int TAX = 8;

    /** the maximum number of fields */
    public static int MAX_FIELDS = 9;

    /** number of optional labels that appear below description. */
    public static int MAX_OPTIONAL_FIELDS = 5;

    /** Default restocking fee label text */
    protected String restockingFeeLabel = "Restocking Fee";

    /** Price Adjustment purchase price label text */
    protected String priceAdjustmentPurchasePriceLabel = "PriceAdjustPurchasePriceLabel";

    /** Price Adjustment current price label text */
    protected String priceAdjustmentCurrentPriceLabel = "PriceAdjustCurrentPriceLabel";

    /** A third line of optional information for the line item, in the same
     * column layout as the first row of item labels. Used to display restocking
     * fee info and tax mode, etc. */
    protected JLabel[] optionalReturnLineItemLabels;
    /** An array of fields that populate vertically below all other rows.
     * @see #MAX_OPTIONAL_FIELDS */
    protected JLabel[] optionalFields;
    /** A counter for which {@link #optionalFields} is being set with text. */
    protected int optionSlot = 0;

    /** The marker used to indicate if a price was overriden. */
    protected static final String OVERRIDE_MARKER = DomainUtil.retrieveOverrideMarker("panel");

    /** The marker used to indicate if an item is a deal item. */
    protected static final String DEAL_ITEM_MARKER = PLUItemUtility.retrieveDealItemMarker();

    /** Property configured for quantity total incremented using non-merchandise quantity **/
    protected static final String QUANTITY_TOTAL_NONMERCHANDISE = "QuantityTotalNonMerchandise";

    /** Default true value **/
    protected static final String DEFAULT_TRUE_VALUE = "false";

    protected JLabel itemLevelScreenMessageLabel = null;

    /** Indicates that the Pickup/Delivery images should be displayed */
    protected boolean displayPickupDeliveryImage = true;

    private final int maxCharsBeforeLineBrk = 90;

    /** Defines the Maximum Length of Sales Associate Name field **/
    private final int MAX_ASSOCIATE_SALES_NAME_LENGTH = 30;

    /**
     * Default constructor.
     */
    public SaleLineItemRenderer()
    {
        super();
        setName("SaleLineItemRenderer");
        initialize();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.AbstractListRenderer#initialize()
     */
    @Override
    protected void initialize()
    {
        // set default in case lookup fails
        firstLineWeights = LINE_WEIGHTS;
        secondLineWeights = LINE_WEIGHTS2;
        firstLineWidths = LINE_WIDTHS;
        secondLineWidths = LINE_WIDTHS2;
        firstLineHeights = LINE_HEIGHTS;

        // look up the label weights
        setFirstLineWeights("saleItemRendererWeights");
        setSecondLineWeights("saleItemRendererWeights2");
        setFirstLineWidths("saleItemRendererWidths");
        setSecondLineWidths("saleItemRendererWidths2");
        setFirstLineHeights("saleItemRendererHeights");

        setMsgBackGroundClrProp("onScreenitemMessagesBackground");
        setMsgTextClrProp("onScreenitemMessagesText");
        setMsgFontProp("onScreenItemMessagesFont");
        
        fieldCount = MAX_FIELDS;
        lineBreak = STATUS; 
        secondLineBreak = TAX;

        super.initialize();
    }

    /**
     * Over ride to add new Label.
     * This is done to show the Item Message on the screen
     */
    @Override
    protected void initLabels()
    {
        super.initLabels();
        itemLevelScreenMessageLabel = new JLabel();
        itemLevelScreenMessageLabel.setBorder(null);
        optionalFields = new JLabel[MAX_OPTIONAL_FIELDS];
    }

    /**
     * Overridden to allow this renderer to use a {@link URLLabel} for the
     * description.
     */
    @Override
    protected JLabel createLabel(int index)
    {
        if (index == DESCRIPTION)
        {
            return uiFactory.createURLLabel("Description", "", null, LABEL_PREFIX, new DisplayItemInfoAction());
        }
        return super.createLabel(index);
    }

    /**
     * Initializes this renderer's components.
     */
    @Override
    protected void initOptions()
    {
        String prefix = UI_PREFIX + ".label";

        labels[DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[DESCRIPTION].setName("DESCRIPTION");
        labels[STATUS].setHorizontalAlignment(JLabel.LEFT);
        labels[STATUS].setName("STATUS");
        labels[STOCK].setHorizontalAlignment(JLabel.LEFT);
        labels[STOCK].setName("STOCK");
        labels[QUANTITY].setHorizontalAlignment(JLabel.CENTER);
        labels[QUANTITY].setName("QUANTITY");
        labels[PRICE].setHorizontalAlignment(JLabel.RIGHT);
        labels[PRICE].setName("PRICE");
        labels[DISCOUNT].setHorizontalAlignment(JLabel.CENTER);
        labels[DISCOUNT].setName("DISCOUNT");
        labels[EXT_PRICE].setHorizontalAlignment(JLabel.RIGHT);
        labels[EXT_PRICE].setName("EXT_PRICE");
        labels[TAX].setHorizontalAlignment(JLabel.RIGHT);
        labels[TAX].setName("TAX");

        // create the optional fields
        for (int i = 0; i < MAX_OPTIONAL_FIELDS; i++)
        {
            optionalFields[i] = uiFactory.createLabel("", "", null, prefix);
            optionalFields[i].setHorizontalAlignment(JLabel.LEFT);
        }

        // lay everything out
        layoutOptions();

        // make colorful for debug mode
        if (DEBUG_MODE)
        {
            for (int i = 0; i < labels.length; i++)
            {
                labels[i].setOpaque(true);
                if (i < optionalReturnLineItemLabels.length)
                {
                    optionalReturnLineItemLabels[i].setOpaque(true);
                }
                if (i < optionalFields.length)
                {
                    optionalFields[i].setOpaque(true);
                    optionalFields[i].setBackground(new Color(189, 84, 255));
                }
                switch (i)
                {
                case 0:
                    labels[i].setBackground(Color.black);
                    optionalReturnLineItemLabels[i].setBackground(Color.magenta);
                    break;
                case 1:
                    labels[i].setBackground(Color.red);
                    optionalReturnLineItemLabels[i].setBackground(Color.red);
                    break;
                case 2:
                    labels[i].setBackground(Color.yellow);
                    optionalReturnLineItemLabels[i].setBackground(Color.yellow);
                    break;
                case 3:
                    labels[i].setBackground(Color.green);
                    optionalReturnLineItemLabels[i].setBackground(Color.green);
                    break;
                case 4:
                    labels[i].setBackground(Color.gray);
                    optionalReturnLineItemLabels[i].setBackground(Color.gray);
                    break;
                case 5:
                    labels[i].setBackground(Color.orange);
                    optionalReturnLineItemLabels[i].setBackground(Color.orange);
                    break;
                case 6:
                    labels[i].setBackground(Color.blue);
                    optionalReturnLineItemLabels[i].setBackground(Color.blue);
                    break;
                case 7:
                    labels[i].setBackground(Color.pink);
                    break;
                case 8:
                    labels[i].setBackground(Color.magenta);
                    break;
                case 9:
                    labels[i].setBackground(Color.cyan);
                    break;
                }
            }
        }
        // end debug mode
    }

    /**
     * Initializes the layout and lays out the {@link #optionalFields} components.
     */
    protected void layoutOptions()
    {
        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        // add optional fields by column
        constraints.gridx = DESCRIPTION;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.0;
        for (int i = 0; i < MAX_OPTIONAL_FIELDS; i++)
        {
            add(optionalFields[i], constraints);
        }
        
        // add the item level screen text
        layoutItemLevelScreenText();

        // Set the layout of the optional sale line item labels
        layoutOptionalReturnLineItemLabels();
        
    }

    /**
     * Lays out the item level screen text component
     */
    protected void layoutItemLevelScreenText()
    {
        // add the optional text
        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");
        // Set position to start of the next line
        constraints.gridx = lineBreak;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.weightx = 0.0;

        add(itemLevelScreenMessageLabel, constraints);
    }

    /**
     *  Creates and initializes the layouts the
     *  {@link #optionalReturnLineItemLabels} components. The second row can
     *  begin on 
     */
    protected void layoutOptionalReturnLineItemLabels()
    {
        String prefix = UI_PREFIX + ".label";
        // Determine max number of fields per line based on the second line
        // Create labels for additional single line, copying alignment and
        // weights from the labels in the sale line item. Create only the
        // number for a single line, so using lineBreak variable for that.
        optionalReturnLineItemLabels = new JLabel[labels.length - lineBreak - 1];
        GridBagLayout layout = (GridBagLayout)getLayout();
        GridBagConstraints cons = null;

        for (int i = 0; i < optionalReturnLineItemLabels.length; i++)
        {
            optionalReturnLineItemLabels[i] = uiFactory.createLabel("", "", null, prefix);
            JLabel secondRowLabel = labels[i + lineBreak + 1];
            optionalReturnLineItemLabels[i].setHorizontalAlignment(secondRowLabel.getHorizontalAlignment());
            cons = layout.getConstraints(secondRowLabel);
            cons = (GridBagConstraints)cons.clone();
            cons.gridx = (i == 0)? 1 : -1;
            cons.gridy = GridBagConstraints.RELATIVE;
            cons.weightx = 0.0;
            add(optionalReturnLineItemLabels[i], cons);
        }
    }

    /**
     * Sets the optional data. See {@link #setOptionalFieldText(String)}.
     * 
     * @param item The line item that supplies the optional data.
     */
    public void setOptionalData(SaleReturnLineItemIfc item)
    {
        // clear the text from all optional fields
        for (int i = 0; i < MAX_OPTIONAL_FIELDS; i++)
        {
            optionalFields[i].setText("");
            optionalFields[i].setIcon(null);
        }

        // reset the index for using the fields.
        optionSlot = 0;

        setOptionalAdvancedPricingText(item);
        setOptionalTransactionDiscountText(item);
        setOptionalSaleAssociateText(item);
        setOptionalGiftReceiptText(item);
        if ( !item.isReturnLineItem() )
        {
            setOptionalDeliveryText(item);
        }
        setOptionalSendLabelText(item);
        setOptionalPriceAdjustmentText(item);
        setOptionalGiftCardText(item);
        setOptionalSerialNumberText(item);
        setOptionalGiftRegistryText(item);
        setReturnLineItemLabels(item);
    }

    /**
     * If the item has a promotion in effect, display its name.
     *
     * @param item
     */
    protected void setOptionalAdvancedPricingText(SaleReturnLineItemIfc item)
    {
        if (item.getAdvancedPricingDiscount() != null)
        {
            setOptionalFieldText(item.getAdvancedPricingDiscount().getName(getLocale()));
        }
    }
    
    /**
     * If Item has transaction level discount, display its name
     * 
     * @param item
     */
    protected void setOptionalTransactionDiscountText(SaleReturnLineItemIfc item)
    {
        ItemDiscountStrategyIfc[] transactionDiscounts = item.getTransactionDiscounts();
        for (ItemDiscountStrategyIfc transactionDiscount : transactionDiscounts)
        {
            setOptionalFieldText(transactionDiscount.getName(getLocale()));
        }

    }

    /**
     * Check if item is a return and there is a restocking fee to display.
     *
     * @param item
     */
    protected void setReturnLineItemLabels(SaleReturnLineItemIfc item)
    {
        if (item.isReturnLineItem())
        {
            CurrencyIfc restockingFee = item.getReturnItem().getRestockingFee();
            if (restockingFee == null)
            {
                //Always gets the restocking fee applied.
                restockingFee = item.getItemPrice().getRestockingFee();
            }
            if (restockingFee != null && restockingFee.signum() != CurrencyIfc.ZERO)
            {
                // Set label sizes based on original line labels
                for (int i = 0; i < optionalReturnLineItemLabels.length; i++)
                {
                    optionalReturnLineItemLabels[i].setMinimumSize(labels[i + lineBreak + 1].getMinimumSize());
                }

                // Multiply restockingFee by the quantity
                BigDecimal qty = item.getItemQuantityDecimal().abs(); // Force to be a positive number
                CurrencyIfc extendedRestockingFee = restockingFee.multiply(qty);

                // Restocking fee is not taxed. Get the no tax character
                String taxChar = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];

                // Add next line with restocking info with same column layout
                // as the first labels line
                optionalReturnLineItemLabels[0].setText(restockingFeeLabel);
                optionalReturnLineItemLabels[2].setText(restockingFee.toGroupFormattedString());
                optionalReturnLineItemLabels[4].setText(extendedRestockingFee.toGroupFormattedString());
                optionalReturnLineItemLabels[5].setText(UIUtilities.retrieveCommonText("TaxModeChar."
                        + taxChar, taxChar));
            }
            else
            {
                // Clear old values when the Re Stocking Fee is null
                // set size to Zero for the ones having zero size text
                for (int i = 0; i < optionalReturnLineItemLabels.length; i++)
                {
                    optionalReturnLineItemLabels[i].setText("");
                }
            }
        }
        else
        {
            // Clear the text from the optional sale line.
            for (int i = 0; i < optionalReturnLineItemLabels.length; i++)
            {
                optionalReturnLineItemLabels[i].setText("");
            }
        }
    }

    /**
     * @param item
     */
    protected void setOptionalDeliveryText(SaleReturnLineItemIfc item)
    {
        OrderItemStatusIfc orderItemStatus = item.getOrderItemStatus();
        OrderItemStatusIfc orderItemComponentStatus = null;
        if (displayPickupDeliveryImage)
        { 
            boolean isDeliveryItem = orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY;
            if ( !isDeliveryItem && item.isKitHeader() )
            {
                KitHeaderLineItemIfc kitLineItem = (KitHeaderLineItemIfc)item;
                if ( kitLineItem.getKitComponentLineItemArray().length>1 )
                {
                    orderItemComponentStatus = kitLineItem.getKitComponentLineItemArray()[0].getOrderItemStatus();
                    isDeliveryItem = orderItemComponentStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY;
                }
            }
            if (isDeliveryItem)
            {
                //for shipping items and shipping charge item
                String label = deliveryLabel;
                if ( orderItemStatus.isCrossChannelItem() ||
                     (orderItemComponentStatus != null && orderItemComponentStatus.isCrossChannelItem()))
                {
                    label = shippingLabel;
                }

                OrderDeliveryDetailIfc orderDeliveryDetail = orderItemStatus.getDeliveryDetails();
                if (orderDeliveryDetail.getDeliveryDate() != null)
                {
                    String logoDate = orderDeliveryDetail.getDeliveryDate().toFormattedString();
                    setOptionalFieldText(label.concat(" ").concat(logoDate), deliveryIcon);
                }
                else
                {
                    setOptionalFieldText(label, deliveryIcon);
                }

                if ( !item.isKitHeader() )
                {
                    int addressType = orderDeliveryDetail.getDeliveryAddress().getAddressType();
                    if (addressType == AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED)
                    {
                        addressType = AddressConstantsIfc.ADDRESS_TYPE_HOME;
                    }
                    String addressTypeLabel = UIUtilities.retrieveCommonText(AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[addressType]);
                    setOptionalFieldText(LocaleUtilities.formatComplexMessage(shipToAddrLabel, addressTypeLabel));
                }
            }
            if ( !isDeliveryItem )
            {
                //if the item is not delivery item, it maybe a pickup item or ship to store for pickup item
                boolean isPickupItem = orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP;
                KitHeaderLineItemIfc kitHeaderLineItem = null;
                if ( !isPickupItem && item.isKitHeader() )
                {
                    kitHeaderLineItem = (KitHeaderLineItemIfc)item;
                    if ( kitHeaderLineItem.getKitComponentLineItemArray().length>1 )
                    {
                        orderItemComponentStatus = kitHeaderLineItem.getKitComponentLineItemArray()[0].getOrderItemStatus();
                        isPickupItem = orderItemComponentStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP;
                    }
                }
                if (isPickupItem)
                {
                    if ( orderItemStatus.isShipToStoreForPickup() ||
                         (orderItemComponentStatus != null && orderItemComponentStatus.isShipToStoreForPickup()) )
                    {
                        if ( orderItemStatus.getPickupStoreID() != null )
                        {
                            setOptionalFieldText(shipToStoreLabel.concat(orderItemStatus.getPickupStoreID()), deliveryIcon);
                        }
                        else if (orderItemComponentStatus != null)
                        {
                            //item is kit header, use its component to get store id
                            setOptionalFieldText(shipToStoreLabel.concat(orderItemComponentStatus.getPickupStoreID()), deliveryIcon);
                        }
                        else
                        {
                            setOptionalFieldText(shipToStoreLabel, deliveryIcon);
                        }
                    }
                    else
                    {
                        if ( orderItemStatus.getPickupDate() != null )
                        {
                            //kit header pickupDate is null, the pickup dates are specified in its components
                            String logoDate = orderItemStatus.getPickupDate().toFormattedString();
                            setOptionalFieldText(pickupLabel.concat(" ").concat(logoDate), pickupIcon);
                        }
                        else
                        {
                            setOptionalFieldText(pickupLabel, pickupIcon);
                        }
                        if (orderItemComponentStatus != null)
                        {
                            //Is kitheader
                            if (isPickupFromMultipleStores(kitHeaderLineItem))
                            {
                                setOptionalFieldText(pickupStoreLabel + orderItemComponentStatus.getPickupStoreID() + " ...");
                            }
                            else
                            {
                                setOptionalFieldText(pickupStoreLabel + orderItemComponentStatus.getPickupStoreID());
                            }
                        }
                        else
                        {
                            setOptionalFieldText(pickupStoreLabel + orderItemStatus.getPickupStoreID());
                        }
                        
                    }
                }
            }
        }
    }
    

    /**
     * Check if picking up the kit components from different stores
     * @param kitHeaderLineItem
     * @return
     */
    protected boolean isPickupFromMultipleStores(KitHeaderLineItemIfc kitHeaderLineItem)
    {
        boolean isFromMultipleStores = false;
        Set<String> storeIDs = new HashSet<String>();
        if ( kitHeaderLineItem != null )
        {
            KitComponentLineItemIfc[] kitComponents = kitHeaderLineItem.getKitComponentLineItemArray();
            for ( int i=0; i<kitComponents.length-1; i++ )
            {
                storeIDs.add(kitComponents[i].getOrderItemStatus().getPickupStoreID());
            }
            if ( storeIDs.size() > 1)
            {
                isFromMultipleStores = true;
            }
        }
        return isFromMultipleStores;
    }
    
    /**
     * Add the optional label for a store send item or its shipping charge item.
     *
     * @param item
     */
    protected void setOptionalSendLabelText(SaleReturnLineItemIfc item)
    {
        String itemText;
        OrderItemStatusIfc orderItemStatus = item.getOrderItemStatus();
        if ( (item.getItemSendFlag() || item.isShippingCharge()) && !orderItemStatus.isCrossChannelItem())
        {
            itemText = new StringBuilder(sendLabel).append(" ").append(item.getSendLabelCount()).toString();
            setOptionalFieldText(itemText);
        }
    }

    /**
     * @param item
     */
    protected void setOptionalPriceAdjustmentText(SaleReturnLineItemIfc item)
    {
        if (item.isPartOfPriceAdjustment())
        {
            if (item.isReturnLineItem())
            {
                String purchasePriceText = UIUtilities.retrieveText("SellItemWorkPanelSpec", "posText",
                        priceAdjustmentPurchasePriceLabel);
                setOptionalFieldText(purchasePriceText);
            }
            else
            {
                String currentPriceText = UIUtilities.retrieveText("SellItemWorkPanelSpec", "posText",
                        priceAdjustmentCurrentPriceLabel);
                setOptionalFieldText(currentPriceText);
            }
        }
    }

    /**
     * If the item is a gift card, then sale info field within the line item
     * should display "Gift Card ID: card #".
     *
     * @param item
     */
    protected void setOptionalGiftCardText(SaleReturnLineItemIfc item)
    {
        if (item.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            GiftCardIfc giftCard = ((GiftCardPLUItemIfc)(item.getPLUItem())).getGiftCard();

            if (giftCard != null && giftCard.getEncipheredCardData() != null
                    && !Util.isEmpty(giftCard.getEncipheredCardData().getTruncatedAcctNumber()))
            {
                String text = giftCardLabel + giftCard.getEncipheredCardData().getTruncatedAcctNumber();
                setOptionalFieldText(text);
            }
        }
    }

    /**
     * If an item has a serial number associated with it, then the sale info
     * field within the line item should display 'Serialized'
     *
     * @param item
     */
    protected void setOptionalSerialNumberText(SaleReturnLineItemIfc item)
    {
        if (!Util.isEmpty(item.getItemSerial()))
        {
            String text = serialLabel + item.getItemSerial();
            setOptionalFieldText(text);
        }
    }

    /**
     * If the item is associated with a gift registry it  should display
     * "Gift Reg.#" and the number
     *
     * @param item
     */
    protected void setOptionalGiftRegistryText(SaleReturnLineItemIfc item)
    {
        if (item.getRegistry() != null)
        {
            String text = item.getRegistry().getID().toString();
    
            if (!Util.isEmpty(text))
            {
                text = giftRegLabel + text;
                setOptionalFieldText(text);
            }
        }
    }

    /**
     * @param item
     */
    protected void setOptionalSaleAssociateText(SaleReturnLineItemIfc item)
    {
        if (item.getSalesAssociate() != null)
        {
            if (item.getSalesAssociate().getEmployeeID() != null && item.getSalesAssociateModifiedFlag())
            {
                String text;
                if (item.getSalesAssociate().getPersonName() != null)
                {
                    text = salesAssocLabel +
                                makeSafeStringForDisplay(item.getSalesAssociate().getPersonName().getFirstLastName(),
                                    MAX_ASSOCIATE_SALES_NAME_LENGTH);
                }
                else
                {
                    text = salesAssocLabel + item.getSalesAssociate().getEmployeeID();
                }
                setOptionalFieldText(text);
            }
        }
    }

    /**
     * Check if Gift receipt and display "Gift Receipt".
     *
     * @param item
     */
    protected void setOptionalGiftReceiptText(SaleReturnLineItemIfc item)
    {
        if (item.isGiftReceiptItem())
        {
            setOptionalFieldText(giftReceiptLabel);
        }
    }

    /**
     * Sizes an optional field based on whether or not it contains text. An
     * empty field will have a width of 0.
     * 
     * @param label the field to be sized
     * @deprecated as of 14.0. No replacement.
     */
    protected void sizeOptionalField(JLabel label)
    {
        Dimension oldDim = label.getPreferredSize();
        int w = oldDim.width;
        Dimension newDim;
        int iconWidth = 0;

        if (label.getIcon() != null && label.getIcon().getIconWidth() > 0)
        {
            iconWidth = label.getIcon().getIconWidth();
        }
        if (label.getText().equals(""))
        {
            newDim = new Dimension(0, 0);
        }
        else
        {
            FontMetrics fm = label.getFontMetrics(label.getFont());
            w = SwingUtilities.computeStringWidth(fm, label.getText());
            newDim = new Dimension(w + 1 + iconWidth, lineHeight);
        }
        label.setPreferredSize(newDim);
    }

    /**
     * Sets the optional field text in the next available optional slot.
     * 
     * @param text The new text for the field.
     */
    protected void setOptionalFieldText(String text)
    {
        // setIcon() sets the "defult icon", so if there are any other icons in any other
        // line items, then this JLabel will have a default icon. So we null it out instead.
        setOptionalFieldText(text, null);
    }

    /**
     * Sets the optional field text in the next available optional slot.
     * 
     * @param text The new text for the field.
     */
    protected void setOptionalFieldText(String text, ImageIcon icon)
    {
        if (optionSlot < MAX_OPTIONAL_FIELDS)
        {
            optionalFields[optionSlot].setText(text);
            optionalFields[optionSlot].setIcon(icon);
            optionSlot++;
        }
    }

    /**
     * sets the visual components of the cell
     * 
     * @param value Object
     */
    @Override
    public void setData(Object value)
    {
        SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)value;
        setDescriptionData(lineItem);
        setStatusData(lineItem);
        setIconData(lineItem);
        setStockData(lineItem);
        setItemScreenMessageData(lineItem);
        setQuantityData(lineItem);        
        setPriceData(lineItem);
        setExtendedPriceData(lineItem);
        setTaxIndicatorData(lineItem);
        setDiscountData(lineItem);
        setOptionalData(lineItem);
    }

    /**
     * @param lineItem
     * @param pluItem
     */
    protected void setItemScreenMessageData(SaleReturnLineItemIfc lineItem)
    {
        PLUItemIfc pluItem = lineItem.getPLUItem();
        StringBuilder screenMessage;
        HashMap<String, String> map;
        Locale locale = null;
        // item level screen message
        if (lineItem.isReturnLineItem() || (lineItem.getReturnItem() != null))
        {
            screenMessage = new StringBuilder();
            locale = LocaleMap.getBestMatch(getLocale());
            map = addLineBreaks(pluItem.getItemLevelMessage(RETURN, SCREEN, locale), maxCharsBeforeLineBrk);
            screenMessage.append("<html><body>").append((String)map.get("MSG")).append("</body></html>");
            itemLevelScreenMessageLabel.setText(screenMessage.toString());
            itemLevelScreenMessageLabel.setBackground(UIManager.getColor(getMsgBackGroundClrProp()));
            itemLevelScreenMessageLabel.setForeground(UIManager.getColor(getMsgTextClrProp()));
            itemLevelScreenMessageLabel.setFont(UIManager.getFont(getMsgFontProp()));
        }
        else
        {
            screenMessage = new StringBuilder();
            locale = LocaleUtilities.getLocaleFromString(getLocale().getLanguage());
            map = addLineBreaks(pluItem.getItemLevelMessage(SALE, SCREEN, locale), maxCharsBeforeLineBrk);
            screenMessage.append("<html><body>").append((String)map.get("MSG")).append("</body></html>");
            itemLevelScreenMessageLabel.setText(screenMessage.toString());
            itemLevelScreenMessageLabel.setBackground(UIManager.getColor(getMsgBackGroundClrProp()));
            itemLevelScreenMessageLabel.setForeground(UIManager.getColor(getMsgTextClrProp()));
            itemLevelScreenMessageLabel.setFont(UIManager.getFont(getMsgFontProp()));
        }
    }

    /**
     * @param lineItem
     */
    protected void setDescriptionData(SaleReturnLineItemIfc lineItem)
    {
        String description = lineItem.getPLUItem().getDescription(getLocale());
        if (Util.isEmpty(description))
        {
            description = lineItem.getReceiptDescription();
        }
        labels[DESCRIPTION].setText(description);
    }
    
    /**
     * 
     * @param lineItem
     */
    protected void setStatusData(SaleReturnLineItemIfc lineItem)
    {
        // set status
        if (lineItem instanceof OrderLineItemIfc)
        {
            OrderLineItemIfc orderLineItem = (OrderLineItemIfc)lineItem;
            int status;
            if (orderLineItem.isPickupCancelLineItem())
            {
                status = orderLineItem.getItemStatus();
                String statusDesc = lineItem.getOrderItemStatus().getStatus().statusToString(status);
                labels[STATUS].setText(UIUtilities.retrieveCommonText(statusDesc,statusDesc));
            }
            else
            {
                labels[STATUS].setText("");
            }   

        }
        else
        {
            labels[STATUS].setText("");
        }
    }

    /**
     * @param pluItem
     */
    protected void setIconData(SaleReturnLineItemIfc lineItem)
    {
        PLUItemIfc pluItem = lineItem.getPLUItem();
        // set icon label if there is an icon
        if (pluItem.getItem() != null && pluItem.getItem().getItemImage() != null)
        {
            ItemImageIfc itemImage = pluItem.getItem().getItemImage();
            // shows no image
            if (itemImage.isEmptyImage() || itemImage.isImageError())
            {
                labels[ICON].setIcon(null);
            }
            // shows busy animated gif
            else if (itemImage.isLoadingImage())
            {
                labels[ICON].setIcon(ItemListRenderer.getLoadingImage(getParent()));
            }
            // not loading image, show actual image
            else
            {
                labels[ICON].setIcon(itemImage.getImage());
            }
        }
        else
        {
            labels[ICON].setIcon(null);
        }
    }

    /**
     * @param lineItem
     */
    protected void setStockData(SaleReturnLineItemIfc lineItem)
    {
        if (isEntryByItemID(lineItem))
        {
            String stockText = null;
            if (lineItem.isKitHeader())
            {
                stockText = lineItem.getItemID() + " " + kitLabel + " ";
            }
            else
            {
                String size = lineItem.getItemSizeCode();
                if (Util.isEmpty(size) || size.equalsIgnoreCase(ItemSizeConstantsIfc.ITEM_SIZE_IDENTIFIER_UNSPECIFIED))
                {
                    stockText = lineItem.getItemID();
                }
                else
                {
                    stockText = lineItem.getItemID() + " " + size + " ";
                }
            }
            labels[STOCK].setText(stockText);
        }
        else
        {
            labels[STOCK].setText(lineItem.isKitHeader() ? lineItem.getItemID() + " " + kitLabel + " " : "");
        }
        
        if (lineItem.getPLUItem().getRelatedItemContainer().isEmpty())
        {
            labels[STOCK].setIcon(null);
        }
        else
        {
            labels[STOCK].setIcon(relatedItemIcon);
            labels[STOCK].setHorizontalTextPosition(SwingConstants.LEFT);
        }
    }

    /**
     * @param lineItem
     */
    protected void setQuantityData(SaleReturnLineItemIfc lineItem)
    {
        String countQuantity = DomainGateway.getProperty(QUANTITY_TOTAL_NONMERCHANDISE, DEFAULT_TRUE_VALUE);
        Boolean incrementNonMerchandiseQuantity = new Boolean(countQuantity);

        if (lineItem.getPLUItem().getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE
                && !incrementNonMerchandiseQuantity.booleanValue())
        {
            labels[QUANTITY].setText("");
        }
        else if (lineItem.isUnitOfMeasureItem())
        {
            //since it is not having unit of measure as units, so to be displayed as decimal number
            labels[QUANTITY].setText(LocaleUtilities.formatDecimal(lineItem.getItemQuantityDecimal(), getLocale()));
        }
        else
        {
            //since it is having unit of measure as units, so to be displayed as whole number
            //items having unit of measure as units should not have fractional qtys.
            labels[QUANTITY].setText(LocaleUtilities.formatDecimalForWholeNumber(lineItem.getItemQuantityDecimal(),
                    getLocale()));
        }
    }

  

    /**
     * @param lineItem
     */
    protected void setPriceData(SaleReturnLineItemIfc lineItem)
    {
        // If we have a deal item, use the deal item marker.
        String dealItemMarker = "";
        if (lineItem.getItemPrice().getBestDealDiscount() != null)
        {
            dealItemMarker = DEAL_ITEM_MARKER;
        }
        // If we have a price override, use the override marker.
        String overrideMarker = "";
        if (lineItem.getItemPrice().isPriceOverride())
        {
            overrideMarker = OVERRIDE_MARKER;
        }
        if ( !lineItem.isKitHeader() )
        {
            labels[PRICE].setText(lineItem.getSellingPrice().toGroupFormattedString() + dealItemMarker
                + overrideMarker);
        }
        else
        {
            //The selling price is the unit price of the item. If the item is kit header, the price is the sum of item 
            //component unit prices. If the quantity of one of item components is greater than one, the price of the kit 
            //header will not be correct. In this case, extended discount price will be used to replace selling price. 
            labels[PRICE].setText(lineItem.getExtendedSellingPrice().toGroupFormattedString() + dealItemMarker
                    + overrideMarker);
        }
    }

    /**
     * @param lineItem
     */
    protected void setExtendedPriceData(SaleReturnLineItemIfc lineItem)
    {
        labels[EXT_PRICE].setText(lineItem.getExtendedDiscountedSellingPrice().toGroupFormattedString());
    }

    /**
     * @param lineItem
     */
    protected void setTaxIndicatorData(SaleReturnLineItemIfc lineItem)
    {
        String taxMode = lineItem.getTaxStatusDescriptor();
        labels[TAX].setText(UIUtilities.retrieveCommonText("TaxModeChar." + taxMode, taxMode));
    }

    /**
     * Check for any discounts.
     * 
     * @param lineItem
     */
    protected void setDiscountData(SaleReturnLineItemIfc lineItem)
    {
        CurrencyIfc discountTotal = null;

        if (lineItem.isKitHeader())
        {
            discountTotal = ((KitHeaderLineItemIfc)lineItem).getKitDiscountTotal();
        }
        else
        {
            discountTotal = lineItem.getItemDiscountTotal();
        }

        if (discountTotal.signum() == CurrencyIfc.ZERO)
        {
            labels[DISCOUNT].setText("");
        }
        else
        {
            labels[DISCOUNT].setText(discountTotal.abs().toGroupFormattedString());
        }
    }

    /**
     * creates the prototype cell to speed updates
     * 
     * @return SaleReturnLineItem the prototype renderer
     */
    public Object createPrototype()
    {
        SaleReturnLineItemIfc cell = DomainGateway.getFactory().getSaleReturnLineItemInstance();

        PLUItemIfc plu = DomainGateway.getFactory().getPLUItemInstance();
        plu.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), "XXXXXXXXXXXXXXX");
        plu.setItemID("12345678901234");
        cell.setPLUItem(plu);

        CurrencyIfc testPrice = DomainGateway.getBaseCurrencyInstance("888888.88");

        ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();

        price.setSellingPrice(testPrice);
        price.setItemDiscountTotal(testPrice);
        price.setExtendedSellingPrice(testPrice);

        price.setItemQuantity(new BigDecimal("888.88"));
        cell.setItemPrice(price);

        EmployeeIfc emp = DomainGateway.getFactory().getEmployeeInstance();
        cell.setSalesAssociate(emp);

        return cell;
    }

    /**
     * Sets the format for printing out currency and quantities.
     */
    @Override
    protected void setPropertyFields()
    {
        super.setPropertyFields();

        // Get the format string spec from the UI model properties.

        if (props != null)
        {
            /*   currencyFormat =
             props.getProperty("CurrencyIfc.DisplayFormat", CURRENCY_FORMAT);
             */
            quantityFormat = props.getProperty("SaleLineItemRenderer.QuantityFormat",
                    DomainGateway.getNumberFormat(getLocale()).toString());

            // ...just use the one our parent provided for us...
            //giftCardLabel = props.getProperty("GiftCardLabel", "Gift Card ID:");

            salesAssocLabel = props.getProperty("SalesAssociateLabel", "Sales Assoc:");
            giftRegLabel = props.getProperty("GiftRegistryLabel", "Gift Registry #");
            serialLabel = props.getProperty(RENDERER_SERIAL_LABEL, "Serial #");
            sendLabel = props.getProperty("SendLabel", "Send");
            kitLabel = props.getProperty("KitLabel", "Kit");
            giftReceiptLabel = props.getProperty("GiftReceiptLabel", "Gift Receipt");
            restockingFeeLabel = props.getProperty("RestockingFeeLabel", "Restocking Fee");
        }
    }

    /**
     * check the line item is price required gift card item
     * 
     * @param lineItem
     * @return boolean
     */
    protected boolean isEntryByItemID(SaleReturnLineItemIfc lineItem)
    {
        boolean retCode = true;

        if (lineItem.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            GiftCardPLUItemIfc item = (GiftCardPLUItemIfc)lineItem.getPLUItem();
            GiftCardIfc giftCard = item.getGiftCard();
            if (giftCard.getIssueEntryType() == GiftCardIfc.BY_DENOMINATION)
            {
                retCode = false;
            }
        }
        return retCode;
    }

    /**
     * This method returns a instance of java.awt.Component, which is configured to display
     *
     * the required value. The component.paint() method is called to render the cell.
     *
     * @param JList
     * @param Object
     * @param int
     * @param boolean
     * @param boolean
     * @return Component
     */
    public Component getListCellRendererComponent(JList jList, Object obj, int index, boolean isSelected,
            boolean isCellHasFocus)
    {
        Component returnCellComponent = this;

        // If this line item is a price adjustment, create a JPanel composed of the
        // return and sale components of the price adjustment item.
        if (obj instanceof PriceAdjustmentLineItemIfc)
        {
            PriceAdjustmentLineItemIfc priceAdjustmentLineItem = (PriceAdjustmentLineItemIfc)obj;

            SaleLineItemRenderer returnComponent = new SaleLineItemRenderer();
            returnComponent = (SaleLineItemRenderer)returnComponent.getListCellRendererComponent(jList,
                    priceAdjustmentLineItem.getPriceAdjustReturnItem(), index, isSelected, false);

            SaleLineItemRenderer saleComponent = new SaleLineItemRenderer();
            saleComponent = (SaleLineItemRenderer)saleComponent.getListCellRendererComponent(jList,
                    priceAdjustmentLineItem.getPriceAdjustSaleItem(), index, isSelected, false);

            JPanel priceAdjustmentCell = new JPanel(new BorderLayout());

            // Set the foreground and background colors and borders

            // if the item is selected, use the selected colors
            if (isSelected && jList.isEnabled())
            {
                priceAdjustmentCell.setBackground(jList.getSelectionBackground());
                priceAdjustmentCell.setForeground(jList.getSelectionForeground());
                priceAdjustmentCell.setOpaque(true);
            }
            // otherwise, set the background to the unselected colors
            else
            {
                priceAdjustmentCell.setBackground(jList.getBackground());
                priceAdjustmentCell.setForeground(jList.getForeground());
                priceAdjustmentCell.setOpaque(false);
            }
            // draw the border if the cell has focus
            if (isCellHasFocus)
            {
                priceAdjustmentCell.setBorder(UIManager.getBorder(FOCUS_BORDER));
            }
            else
            {
                priceAdjustmentCell.setBorder(UIManager.getBorder(NO_FOCUS_BORDER));
            }

            // Add the price adjustment components to the panel
            priceAdjustmentCell.add(returnComponent, BorderLayout.NORTH);
            priceAdjustmentCell.add(saleComponent, BorderLayout.SOUTH);

            returnCellComponent = priceAdjustmentCell;

        }
        else
        {
            // set the color of all label foregrounds making sure to call superclass
            // method as well
            super.getListCellRendererComponent(jList, obj, index, isSelected, isCellHasFocus);
            for (int i = 0; i < optionalFields.length; i++)
            {
                optionalFields[i].setForeground(getForeground());
            }
            for (int i = 0; i < optionalReturnLineItemLabels.length; i++)
            {
                optionalReturnLineItemLabels[i].setForeground(getForeground());
            }
        }

        return returnCellComponent;
    }

    /**
     * If the sales associate text string is too wide to fit within the
     * available space allocated in the work panel, specific number characters
     * and "..." will be displayed instead.
     * 
     * @param args associate name text string
     * @param displayLength Specified length of description string to be
     *            displayed in the screen
     * @return {@link String} Truncated description string suffixed with "..."
     */
    private String makeSafeStringForDisplay(String args, int displayLength)
    {
        String clipString = "...";
        args = args.trim();
        if (args.length() > displayLength)
        {
            StringBuilder buffer = new StringBuilder(args.substring(0, displayLength + 1));
            return buffer.append(clipString).toString();
        }
        return args;
    }

    // -------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        SaleLineItemRenderer renderer = new SaleLineItemRenderer();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
