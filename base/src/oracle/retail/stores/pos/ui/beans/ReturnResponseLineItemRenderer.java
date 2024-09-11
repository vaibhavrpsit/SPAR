/* =============================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReturnResponseLineItemRenderer.java /main/10 2013/11/05 17:41:09 cgreene Exp $
 * =============================================================================
 * NOTES
 * Created by Lucy Zhao (Oracle Consulting) for POS-RM integration.
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/05/13 - refactor to pass ItemPrice to returnResponseLineItem
 *                         for displaying on Return Response screen.
 *    cgreene   11/27/12 - enhancement for displaying item images in sale
 *                         screen table
 *    abondala  03/12/12 - return response codes localization
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/12/10 - use default locale for display of currency
 *    acadar    04/09/10 - optimize calls to LocaleMAp
 *    acadar    04/06/10 - use default locale for currency display
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    mdecama   12/05/08 - Updates to the RETURN_RESPONSE Screen
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.math.BigDecimal;
import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.ReturnResponseLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemSizeConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;

/**
 * Renderer for SaleReturnLineItems.
 *
 * @version $Revision: /main/10 $
 */
public class ReturnResponseLineItemRenderer extends LineItemRenderer
                                  implements CodeConstantsIfc
{
    private static final long serialVersionUID = 3238897613749845147L;

    /** the default weights that layout the first display line */
    public static int[] RETURN_RESPONSE_WEIGHTS = {35,35,15,15};

    /** the default widths that layout the first display line */
    public static int[] RETURN_RESPONSE_LINE_WIDTHS = {1,1,1,1};

    /** the return response column */
    public static int APPROVE_DENY_CODE        = 0;
    /** the description column */
    public static int ITEM_DESCRIPTION         = 1;
    /** the quantity column */
    public static int QUANTITY                 = 2;
    /** the ext_price column */
    public static int EXT_PRICE                = 3;
    /** the tax column */
    public static int RESPONSE_DESCRIPTION     = 4;
    /** the stock column */
    public static int STOCK                    = 5;
    /** the maximum number of fields */
    public static int MAX_FIELDS               = 6;  // the first 4 at line one, the last two at line two. //7

    /** Property configured for quantity total incremented using non-merchandise quantity **/
    protected static final String QUANTITY_TOTAL_NONMERCHANDISE = "QuantityTotalNonMerchandise";

    /** Default true value **/
    protected static final String DEFAULT_TRUE_VALUE = "false";

    /**
     * Default constructor.
     */
    public ReturnResponseLineItemRenderer()
    {
        setName("ReturnResponseLineItemRenderer");
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.AbstractListRenderer#initialize()
     */
    @Override
    protected void initialize()
    {
        // set defaults in case lookup fails
        firstLineWeights = RETURN_RESPONSE_WEIGHTS;
        firstLineWidths = RETURN_RESPONSE_LINE_WIDTHS;

        // look up the label weights
        setFirstLineWeights("returnResponseItemRendererWeights");
        setFirstLineWidths("returnResponseItemRendererWidths");

        fieldCount = MAX_FIELDS;  //6
        lineBreak = EXT_PRICE;  //3

        super.initialize();
    }

    /**
     * Initializes this renderer's components.
     */
    @Override
    protected void initOptions()
    {
        labels[APPROVE_DENY_CODE].setHorizontalAlignment(JLabel.LEFT);
        labels[ITEM_DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[RESPONSE_DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[STOCK].setHorizontalAlignment(JLabel.LEFT);
        labels[EXT_PRICE].setHorizontalAlignment(JLabel.RIGHT);

        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        // add the second line
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        add(labels[RESPONSE_DESCRIPTION], constraints);
        add(labels[STOCK], constraints);

        // add optional fields by column
        constraints.gridx = 1;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(labels[STOCK], constraints);
    }

    /**
     * sets the visual components of the cell
     * 
     * @param value Object
     */
    @Override
    public void setData(Object value)
    {
        ReturnResponseLineItemIfc lineItem = (ReturnResponseLineItemIfc) value;

        UtilityManagerIfc utility =
            (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);

        labels[APPROVE_DENY_CODE].setText(utility.retrieveText(BundleConstantsIfc.COMMON,
                BundleConstantsIfc.RETURNS_BUNDLE_NAME, lineItem.getApproveDenyCode(), lineItem.getApproveDenyCode()));;

        if ( lineItem.isManagerOverride() )
        {
            labels[RESPONSE_DESCRIPTION].setText(utility.retrieveText(BundleConstantsIfc.COMMON,
                    BundleConstantsIfc.RETURNS_BUNDLE_NAME, "ManagerOverrideApproved", "Manager Override Approved"));
        }
        else
            labels[RESPONSE_DESCRIPTION].setText(lineItem.getResponseDescription());

        labels[ITEM_DESCRIPTION].setText(lineItem.getPLUItem().getDescription(getLocale()));
        if ( isEntryByItemID(lineItem) )
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
                    stockText = lineItem.getItemID()+ " "+ size+" ";
                }
            }
            labels[STOCK].setText(stockText);
        }
        else
        {
            labels[STOCK].setText(lineItem.isKitHeader() ?
                                  lineItem.getItemID() + " " + kitLabel + " " : "");
        }

        String countQuantity = DomainGateway.getProperty(QUANTITY_TOTAL_NONMERCHANDISE,
                DEFAULT_TRUE_VALUE);
        Boolean incrementNonMerchandiseQuantity = new Boolean(countQuantity);

        if (lineItem.getPLUItem().getItemClassification().getItemType() == ItemClassificationConstantsIfc.TYPE_SERVICE
                && !incrementNonMerchandiseQuantity.booleanValue())
        {
            labels[QUANTITY].setText("");
        }
        else if (lineItem.isUnitOfMeasureItem())
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

        labels[EXT_PRICE].setText(lineItem.getExtendedDiscountedSellingPrice().toGroupFormattedString());
    }

    /**
     *  creates the prototype cell to speed updates
     *  @return ReturnResponseLineItem the prototype renderer
     */
    public Object createPrototype()
    {
        ReturnResponseLineItemIfc cell =
            DomainGateway.getFactory().getReturnResponseLineItemInstance();

        cell.setApproveDenyCode("Authorization");
        cell.setResponseDescription("Authorization");
        cell.setManagerOverride(false);

        PLUItemIfc plu = DomainGateway.getFactory().getPLUItemInstance();
        plu.setDescription(LocaleMap.getLocale("en_US"), "XXXXXXXXXXXXXXX");
        plu.setItemID("12345678901234");
        cell.setPLUItem(plu);

        CurrencyIfc testPrice = DomainGateway.getBaseCurrencyInstance("888888.88");

        ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();

        price.setExtendedSellingPrice(testPrice);

        price.setItemQuantity(new BigDecimal("888.88"));

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
            quantityFormat =
                props.getProperty("SaleLineItemRenderer.QuantityFormat", DomainGateway.getDecimalFormat(getLocale()).toPattern());
        }
    }


    /**
     * check the line item is price required gift card item.
     *
     * @param lineItem
     * @return boolean
     */
    protected boolean isEntryByItemID(SaleReturnLineItemIfc lineItem)
    {
        boolean retCode = true;

        if ( lineItem.getPLUItem() instanceof GiftCardPLUItemIfc )
        {
             GiftCardPLUItemIfc item = (GiftCardPLUItemIfc)lineItem.getPLUItem();
             GiftCardIfc giftCard = item.getGiftCard();
             if ( giftCard.getIssueEntryType() == GiftCardIfc.BY_DENOMINATION )
             {
                 retCode = false;
             }
         }
         return retCode;
    }
}
