/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LineItemRenderer.java /main/29 2013/03/18 14:59:18 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   03/18/13 - Add store id for pickup at show item screen.
 *    yiqzhao   08/06/12 - tax label layout -- right on Ext Price
 *    rgour     07/31/12 - Fixed the issues, related to WPTG
 *    sgu       06/27/12 - set item disposition code for ship to store item
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
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
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    acadar    02/25/09 - override the getDefaultLocale from JComponent
 *    ddbaker   01/06/09 - Removed duplicate additions of labels to renderer.
 *                         Labels are correctly added by
 *                         AbstractListRenderer.initLabels() only.
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    ddbaker   11/11/08 - Updated to layouts to meet business requirements.
 *    ddbaker   11/10/08 - Updated based on new requirements
 *    ddbaker   11/06/08 - Corrected formatting issue.
 *    ddbaker   11/06/08 - Update due to merges.
 *
 * ===========================================================================
 * $Log:
 *   8    360Commerce 1.7         3/20/2008 2:11:48 AM   Manikandan Chellapan
 *        CR#30934 Modified code to show truncated card number instead of
 *        encrypted card number.
 *   7    360Commerce 1.6         7/9/2007 3:07:52 PM    Anda D. Cadar   I18N
 *        changes for CR 27494: POS 1st initialization when Server is offline
 *   6    360Commerce 1.5         5/8/2007 11:32:27 AM   Anda D. Cadar
 *        currency changes for I18N
 *   5    360Commerce 1.4         4/25/2007 8:51:32 AM   Anda D. Cadar   I18N
 *        merge
 *   4    360Commerce 1.3         1/22/2006 11:45:27 AM  Ron W. Haight
 *        removed references to com.ibm.math.BigDecimal
 *   3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:23:06 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse
 *
 *  Revision 1.12  2004/07/21 14:29:46  rsachdeva
 *  @scr 3978 Re-entering Modify Item gift registry
 *
 *  Revision 1.11  2004/07/12 20:13:55  mweis
 *  @scr 6158 "Gift Card ID:" label not appearing correctly
 *
 *  Revision 1.10  2004/05/21 13:43:05  dfierling
 *  @scr 3987 - updated column widths and changed qty formatting
 *
 *  Revision 1.9  2004/04/22 20:09:10  mweis
 *  @scr 4507 Deal Item indicator - code review updates
 *
 *  Revision 1.8  2004/04/21 20:35:30  mweis
 *  @scr 4507 Deal Item indicator - initial submission
 *
 *  Revision 1.7  2004/04/16 13:51:34  mweis
 *  @scr 4410 Price Override indicator -- initial submission
 *
 *  Revision 1.6  2004/04/15 21:21:42  mweis
 *  @scr 4206 JavaDoc updates.
 *
 *  Revision 1.5  2004/04/09 16:56:00  cdb
 *  @scr 4302 Removed double semicolon warnings.
 *
 *  Revision 1.4  2004/04/08 20:33:02  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Apr 18 2003 12:46:18   bwf
 * Make sure using correct bundle name and id for tax status.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Apr 15 2003 13:51:24   RSachdeva
 * Labels Internationalization
 * Resolution for POS SCR-2114: Kit Label in ITEM_OPTIONS
 *
 *    Rev 1.3   Mar 07 2003 17:11:10   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 06 2002 17:25:26   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:58   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:50:48   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:07:16   dfh
 * cleanup, removed Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 *
 *    Rev 1.0   Mar 18 2002 11:56:00   msg
 * Initial revision.
 *
 *    Rev 1.8   13 Mar 2002 17:08:02   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.7   Feb 27 2002 21:25:54   mpm
 * Continuing work on internationalization
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.utility.PLUItemUtility;

//------------------------------------------------------------------------------
/**
 *  This is a basic renderer for a line item type of object.
 */
//------------------------------------------------------------------------------
public class LineItemRenderer extends AbstractListRenderer
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3087054759394560344L;

    /** the line weights */
    public static int[] LINE_WEIGHTS = {58,21,21}; // {33,14,16,14,18,8};   // (+1,0,0,0,+1,+1)

    /** the line weights */
    public static int[] LINE_WEIGHTS2 = {21,16,21,21,21,3}; // {33,14,16,14,18,8};   // (+1,0,0,0,+1,+1)

    /** the line cell widths */
    public static int[] LINE_WIDTHS = {3,1,1}; // {33,14,16,14,18,8};   // (+1,0,0,0,+1,+1)

    /** the line cell widths */
    public static int[] LINE_WIDTHS2 = {1,1,1,1,1,1}; // {33,14,16,14,18,8};   // (+1,0,0,0,+1,+1)

    /** The quantity format object    **/
    protected String quantityFormat = DomainGateway.getNumberFormat(getLocale()).toString();

    /** the description column */
    public static int DESCRIPTION = 0;
    /** the sale info column */
    public static int SALE_INFO   = 1;
    /** the stock number column */
    public static int STOCK_NUM   = 2;
    /** the quantity column */
    public static int QUANTITY    = 3;
    /** the price column */
    public static int PRICE       = 4;
    /** the discount column */
    public static int DISCOUNT    = 5;
    /** the ext_price column */
    public static int EXT_PRICE   = 6;
    /** the tax column */
    public static int TAX         = 7;    

    /** the maximum number of fields */
    public static int MAX_FIELDS  = 8;


    /**
      currencyifc display format
    **/
    public static final String CURRENCYIFC_DISPLAYFORMAT = "CurrencyIfc.DisplayFormat";
    /**
      sale line item renderer quantity format
    **/
    public static final String SALELINEITEM_RENDERER_QUANTITYFORMAT = "SaleLineItemRenderer.QuantityFormat";
    /**
      renderer Gift Card Label tag
    **/
    public static final String RENDERER_GIFT_CARD_LABEL = "Renderer.GiftCardLabel";
    /**
      renderer Sale Assoc Label tag
    **/
    public static final String RENDERER_SALE_ASSOC_LABEL = "Renderer.SalesAssociateLabel";
    /**
      renderer Gift Registry Label tag
    **/
    public static final String RENDERER_GIFT_REGISTRY_LABEL = "Renderer.GiftRegistryLabel";
    /**
      renderer Serial Label tag
    **/
    public static final String RENDERER_SERIAL_LABEL = "Renderer.SerialLabel";
    /**
      renderer Send Label tag
    **/
    public static final String RENDERER_SEND_LABEL = "Renderer.SendLabel";



    /**
    renderer Pckup Label tag
  **/

    public static final String RENDERER_PICKUP_LABEL = "Renderer.PickupLabel";
    public static final String RENDERER_PICKUP_AT_STORE_LABEL = "Renderer.PickupAtStoreLabel";
    public static final String RENDERER_DELIVERY_LABEL = "Renderer.DeliveryLabel";
    public static final String RENDERER_SHIPPING_LABEL = "Renderer.ShippingLabel";
    public static final String RENDERER_SHIPTOSTORE_LABEL = "Renderer.ShipToStoreLabel";
    public static final String RENDERER_SHIPTOADDR_LABEL = "Renderer.ShipToAddrLabel";

    /**
      renderer Gift Receipt Label tag
    **/
    public static final String RENDERER_GIFT_RECEIPT_LABEL = "Renderer.GiftReceiptLabel";
    /**
      renderer Kit Label tag
    **/
    public static final String RENDERER_KIT_LABEL = "Renderer.KitLabel";
    /**
      gift Card Label
    **/
    protected String giftCardLabel = "Gift Card ID:";
    /**
      gift Reg Label
    **/
    protected String giftRegLabel = "Gift Registry #";
    /**
      sales Assoc Label
    **/
    protected String salesAssocLabel = "Sales Assoc:";
    /**
      serial Label
    **/
    protected String serialLabel = "Serial #";
    /**
      send Label
    **/
    protected String sendLabel = "Send";

    protected String pickupLabel = "Pickup";
    protected String pickupStoreLabel = "Pickup at Store #";
    protected String deliveryLabel = "Delivery";
    protected String shippingLabel = "Shipping";
    protected String shipToStoreLabel = "Ship to Store #";
    protected String shipToAddrLabel = "{0} Address";

    /**
      gift Receipt Label
    **/
    protected String giftReceiptLabel = "Gift Receipt";
    /**
      kit Label
    **/
    protected String kitLabel = "Kit";
    /**
       Tax Mode Tag
     */
    protected String taxModeTag = "TaxModeChar.";

    /** The marker used to indicate if a price was overriden. */
    protected static final String OVERRIDE_MARKER = DomainUtil.retrieveOverrideMarker("panel");

    /** The marker used to indicate if an item is a deal item. */
    protected static final String DEAL_ITEM_MARKER = PLUItemUtility.retrieveDealItemMarker();

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public LineItemRenderer()
    {
        super();
        setName("LineItemRenderer");

        // set default in case lookup fails
        firstLineWeights = LINE_WEIGHTS;
        secondLineWeights = LINE_WEIGHTS2;
        firstLineWidths = LINE_WIDTHS;
        secondLineWidths = LINE_WIDTHS2;

        // look up the label weights
        setFirstLineWeights("lineItemRendererWeights");
        setSecondLineWeights("lineItemRendererWeights2");
        setFirstLineWidths("lineItemRendererWidths");
        setSecondLineWidths("lineItemRendererWidths2");

        fieldCount = MAX_FIELDS;
        lineBreak = SALE_INFO;
        secondLineBreak = TAX;
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        labels[DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[STOCK_NUM].setHorizontalAlignment(JLabel.LEFT);
        labels[PRICE].setHorizontalAlignment(JLabel.RIGHT);
        labels[DISCOUNT].setHorizontalAlignment(JLabel.RIGHT);
        labels[EXT_PRICE].setHorizontalAlignment(JLabel.RIGHT);
    }

    //--------------------------------------------------------------------------
    /**
     *  Extracts the data from a domain object and sets the visual
     *  components of the cell.
     *  @param value the data object
     */
    public void setData(Object value)
    {
        SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)value;

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

        String description = lineItem.getPLUItem().getDescription(getLocale());
        if (Util.isEmpty(description))
        {
            description = lineItem.getReceiptDescription();
        }
        labels[DESCRIPTION].setText(description);
        Locale defaultLocale = getDefaultLocale();
        labels[PRICE].setText(lineItem.getSellingPrice().toGroupFormattedString() + dealItemMarker + overrideMarker);
        labels[DISCOUNT].setText(getDiscountTotal(lineItem));
        labels[EXT_PRICE].setText(lineItem.getExtendedDiscountedSellingPrice().toGroupFormattedString());

        //The Kit Label is as per the User Interface Locale
        labels[STOCK_NUM].setText(lineItem.isKitHeader() ?
                                  lineItem.getItemID() + " " + kitLabel + " " :
                                  lineItem.getItemID());

        // set the optional fields
        setOptionalData(value);
    }

    //--------------------------------------------------------------------------
    /**
     *  Extracts and addition data that is optionally displayed.
     *  @param value the data object
     */
    public void setOptionalData(Object value)
    {
        SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)value;
        String taxStatus = lineItem.getTaxStatusDescriptor();

        if (lineItem.isUnitOfMeasureItem())
        {
            //since it is not having unit of measure as units, so to be displayed as decimal number
            labels[QUANTITY].setText(LocaleUtilities.formatDecimal(lineItem.getItemQuantityDecimal(),
                                                                   getLocale()));
        }
        else
        {
            //since it is having unit of measure as units, so to be displayed as whole number
            //items having unit of measure as units should not have fractional qtys.
            labels[QUANTITY].setText(LocaleUtilities.formatDecimalForWholeNumber(lineItem.getItemQuantityDecimal(),
                                                                                 getLocale()));
        }

        labels[TAX].setText(UIUtilities.retrieveCommonText(taxModeTag + taxStatus,taxStatus));
        labels[SALE_INFO].setText(getSaleInfoText(lineItem));
    }

    //--------------------------------------------------------------------------
    /**
     *  Extracts and formats the discount total data.
     *  @param lineItem the line item
     *  @return a formated currency display string
     */
    protected String getDiscountTotal(SaleReturnLineItemIfc lineItem)
    {
        String result = "";

        // check for any discounts
        CurrencyIfc discountTotal = null;
        if (lineItem.isKitHeader())
        {
            discountTotal = ((KitHeaderLineItemIfc)lineItem).getKitDiscountTotal();
        }
        else
        {
            discountTotal = lineItem.getItemDiscountTotal();
        }

        if (discountTotal.signum() != CurrencyIfc.ZERO)
        {
            result = discountTotal.toGroupFormattedString();
        }
        return result;
    }

    //--------------------------------------------------------------------------
    /**
     *  This returns the gift registry text if there is a gift registry for
     *  the sale line item.
     *  @param item the line item
     *  @return String The text for the gift registry for the sale
     *  line item.
     */
    protected String getGiftRegistryText(SaleReturnLineItemIfc item)
    {
        String theID = new String("");

        if (item.getRegistry() != null)
        {
            theID = item.getRegistry().getID().toString();

            if (!theID.equals(""))
            {
                theID = giftRegLabel + theID;
            }
        }
        return theID;
    }

    //---------------------------------------------------------------------
    /**
       Returns the description text that goes in the description field. <P>
       @return String The description text that goes into the description field.
       @param item
    */
    //---------------------------------------------------------------------
    protected String getSaleInfoText(SaleReturnLineItemIfc item)
    {
        //first do the SalesAssociate
        StringBuffer buf = new StringBuffer();
        //If the item is a gift card, then sale info field within the line item should
        //display "Gift Card ID: card #"
        if(item.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            GiftCardIfc giftCard = ((GiftCardPLUItemIfc)(item.getPLUItem())).getGiftCard();
            if(giftCard != null && giftCard.getEncipheredCardData() != null &&
              !(Util.isEmpty(giftCard.getEncipheredCardData().getEncryptedAcctNumber())))
            {
                buf.append(giftCardLabel + giftCard.getEncipheredCardData().getTruncatedAcctNumber());
            }
        }

        //Currently requirements changed to not show 'serialized' or sales associate.
        //However keeping this code if display criteria changes.
        //If an item has a serial number associated with it, then the sale info field
        //within the line item should display 'Serialized'
        /*else if(item.getItemSerial() != null)
        {
            buf.append("Serialized");
        }

        if (item.getSalesAssociate() != null)
        {
            if (item.getSalesAssociate().getName() != null && item.getSalesAssociateModifiedFlag())
            {
                salesAssociateLabel = "Sales Assoc:";
                buf.append(salesAssociateLabel).append(item.getSalesAssociate().getName().getFirstLastName());
            }
        }
        //putting gift registry also in commented part as per SCR 3978
        // now do the gift registry
        buf.append("  ").append(getGiftRegistryText(item));
        */
        return buf.toString();
    }

    //--------------------------------------------------------------------------
    /**
     *  Determines the tax status indicator.
     *  @param lineItem a line item
     *  @return a string value for the tax status
     *  @deprecated use SaleReturnLineItemIfc.getTaxStatusDescriptor() instead.
     */
    protected String getTaxStatus(SaleReturnLineItemIfc lineItem)
    {
        String result = "";

        // plan for index out of bounds
        try
        {
            int taxMode = lineItem.getTaxMode();

            // set taxable/taxable based on mode and taxable flag on item
            // This is a design flaw which will be corrected soon.
            if ((taxMode == TaxIfc.TAX_MODE_STANDARD ||
                 taxMode == TaxIfc.TAX_MODE_RETURN_RATE)
                && lineItem.getTaxable() == false)
            {
                result = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];
            }
            else
            {
                result = TaxIfc.TAX_MODE_CHAR[taxMode];
            }
        }
        // if out of bounds, set blank
        catch (ArrayIndexOutOfBoundsException e)
        {
            result = "";
        }
        return result;
    }

    //--------------------------------------------------------------------------
    /**
     *  creates the prototype cell to speed updates
     *  @return SaleReturnLineItem the prototype renderer
     */
    public Object createPrototype()
    {
        SaleReturnLineItemIfc cell =
            DomainGateway.getFactory().getSaleReturnLineItemInstance();

        PLUItemIfc plu = DomainGateway.getFactory().getPLUItemInstance();
        plu.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), "XXXXXXXXXXXXXXX");
        plu.setItemID("12345678901234");
        cell.setPLUItem(plu);

        CurrencyIfc testPrice = DomainGateway.getBaseCurrencyInstance("888888.88");

        ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();
        price.setSellingPrice(testPrice);
        price.setItemQuantity(new BigDecimal("888.88"));
        price.setItemDiscountTotal(testPrice);
        price.setExtendedSellingPrice(testPrice);
        cell.setItemPrice(price);

        EmployeeIfc emp = DomainGateway.getFactory().getEmployeeInstance();
        cell.setSalesAssociate(emp);

       return((Object)cell);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the format for printing out currency and quantities.
     */
    protected void setPropertyFields()
    {
        // Get the format string spec from the UI model properties.
        if (props != null)
        {
            quantityFormat =
              props.getProperty(SALELINEITEM_RENDERER_QUANTITYFORMAT,
                      DomainGateway.getNumberFormat(getLocale()).toString());
        }

        //Labels that need to be displayed in User Interface Locale
        giftCardLabel =
          UIUtilities.retrieveCommonText(RENDERER_GIFT_CARD_LABEL) +" ";

        salesAssocLabel =
          UIUtilities.retrieveCommonText(RENDERER_SALE_ASSOC_LABEL);

        giftRegLabel =
          UIUtilities.retrieveCommonText(RENDERER_GIFT_REGISTRY_LABEL);

        serialLabel =
          UIUtilities.retrieveCommonText(RENDERER_SERIAL_LABEL);

        sendLabel =
          UIUtilities.retrieveCommonText(RENDERER_SEND_LABEL);

        pickupLabel =
            UIUtilities.retrieveCommonText(RENDERER_PICKUP_LABEL);
        
        pickupStoreLabel =
                UIUtilities.retrieveCommonText(RENDERER_PICKUP_AT_STORE_LABEL);

        deliveryLabel=
            UIUtilities.retrieveCommonText(RENDERER_DELIVERY_LABEL);

        shippingLabel=
            UIUtilities.retrieveCommonText(RENDERER_SHIPPING_LABEL);

        shipToStoreLabel=
            UIUtilities.retrieveCommonText(RENDERER_SHIPTOSTORE_LABEL);

        shipToAddrLabel=
            UIUtilities.retrieveCommonText(RENDERER_SHIPTOADDR_LABEL);

        giftReceiptLabel =
          UIUtilities.retrieveCommonText(RENDERER_GIFT_RECEIPT_LABEL);

        kitLabel =
          UIUtilities.retrieveCommonText(RENDERER_KIT_LABEL);
    }

    //--------------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number. <P>
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    //--------------------------------------------------------------------------
    /**
     *  main entrypoint - starts the part when it is run as an application
     *  @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        SaleLineItemRenderer renderer = new SaleLineItemRenderer();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
