/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*
* Copyright (c) 2008, 2009, Oracle and/or its affiliates. All rights reserved. 
*
*  $Log:
*  
*  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.ui.beans;

// Java imports
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.math.BigDecimal;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.GiftCertificateItem;
import oracle.retail.stores.domain.stock.ItemClassificationConstantsIfc;
import oracle.retail.stores.domain.stock.ItemSizeConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.tax.TaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import max.retail.stores.domain.utility.MAXCodeListMapIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.utility.PLUItemUtility;
import max.retail.stores.domain.stock.MAXPLUItemIfc;
import max.retail.stores.domain.utility.MAXCodeConstantsIfc;
import max.retail.stores.domain.utility.MAXCodeListMapIfc;

//------------------------------------------------------------------------------
/**
 *  Renderer for SaleReturnLineItemsWithMRP.
 *  @version $Revision: /rgbustores_12.0.9in_branch/2 $
 */
//------------------------------------------------------------------------------
public class MAXSaleLineItemRendererWithMRP extends MAXLineItemRendererWithMRP
                                  implements MAXCodeConstantsIfc
{
    /** revision number supplied by version control    **/
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/2 $";

    /** the default weights that layout the first display line */
    public static int[] SALE_WEIGHTS = {35,10,20,20,12,20,8};

    /** the description column */
    public static int DESCRIPTION = 0;
    /** the quantity column */
    public static int QUANTITY    = 1;
    /** Maximum Retail Price Column */
    public static int MAXIMUM_RETAIL_PRICE  = 2;
    /** the price column */
    public static int PRICE       = 3;
    /** the discount column */
    public static int DISCOUNT    = 4;
    /** the ext_price column */
    public static int EXT_PRICE   = 5;
    /** the tax column */
    public static int TAX         = 6;
    /** the stock column */
    public static int STOCK       = 7;
    /** the maximum number of fields */
    public static int MAX_FIELDS  = 8;    
    
    public static String DECIMAL_FORMAT="DecimalFormat";
   

    /** Default restocking fee label text */
    protected String restockingFeeLabel = "Restocking Fee";
    
    /** Price Adjustment purchase price label text */
    protected String priceAdjustmentPurchasePriceLabel = "PriceAdjustPurchasePriceLabel";

    /** Price Adjustment current price label text */
    protected String priceAdjustmentCurrentPriceLabel = "PriceAdjustCurrentPriceLabel";
    
    protected JLabel[] optionalFields;

    // Optional information for the line item, in the same column layout as item
    // labels. To display additional pricing info etc.
    protected JLabel[] optionalSaleLineItemLabels;

    /** Code List for reason code lookup */
    protected MAXCodeListMapIfc map = null;

    protected int optionSlot = 0;

    /** number of labels per line */
    public static int NUMBER_OF_LABELS = 6;
    
    /** The marker used to indicate if a price was overriden. */
    protected static final String OVERRIDE_MARKER = DomainUtil.retrieveOverrideMarker("panel");
    
    /** The marker used to indicate if an item is a deal item. */
    protected static final String DEAL_ITEM_MARKER = PLUItemUtility.retrieveDealItemMarker();
    /** Property configured for quantity total incremented using non-merchandise quantity **/
	 protected static final String QUANTITY_TOTAL_NONMERCHANDISE = "QuantityTotalNonMerchandise";

	 /** Default true value **/
	 protected static final String DEFAULT_TRUE_VALUE = "false";
	 
	 Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public MAXSaleLineItemRendererWithMRP()
    {
        super();
        setName("MAXSaleLineItemRendererWithMRP");

        // set default in case lookup fails
        firstLineWeights = SALE_WEIGHTS;       
        fieldCount = MAX_FIELDS;
        lineBreak = TAX;
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        String prefix = UI_PREFIX + ".label";

        optionalFields = new JLabel[NUMBER_OF_LABELS];

        labels[DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[STOCK].setHorizontalAlignment(JLabel.LEFT);
        labels[PRICE].setHorizontalAlignment(JLabel.CENTER);
        labels[DISCOUNT].setHorizontalAlignment(JLabel.RIGHT);
        labels[EXT_PRICE].setHorizontalAlignment(JLabel.CENTER);
        labels[MAXIMUM_RETAIL_PRICE].setHorizontalAlignment(JLabel.CENTER);
        // create the optional fields
        for(int i=0; i<NUMBER_OF_LABELS; i++)
        {
            optionalFields[i] = uiFactory.createLabel("","", null, prefix);
            optionalFields[i].setHorizontalAlignment(JLabel.LEFT);
        }

        layoutOptions();
    }
    
    //--------------------------------------------------------------------------
    /**
     *  Initializes the layout and lays out the components.
     */
    protected void layoutOptions()
    {
        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        // add the second line
        constraints.gridy = 1;
        constraints.weightx = 0.0;
        add(labels[STOCK], constraints);

        // add optional fields by column
        constraints.gridx = 1;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(optionalFields[0], constraints);
        add(optionalFields[2], constraints);
        add(optionalFields[4], constraints);
        add(optionalFields[1], constraints);
        add(optionalFields[3], constraints);
        add(optionalFields[5], constraints);

        // Set the layout of the optional sale line item labels
        layoutOptionalSaleLineItemLabels();

    }

   //--------------------------------------------------------------------------
   /**
     *  Creates and initializes the layout of an optional sale line item
     */
    protected void layoutOptionalSaleLineItemLabels()
    {
        String prefix = UI_PREFIX + ".label";
        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        // Set position to start of the next line
        constraints.gridx = 0;
        constraints.gridy = 2;

        // Create labels for additional single line, copying alignment and
        // weights from the labels in the sale line item. Create only the
        // number for a single line, so using lineBreak variable for that.
        optionalSaleLineItemLabels = new JLabel[lineBreak+1];
        for(int i=0; i<lineBreak+1; i++)
        {
            optionalSaleLineItemLabels[i] = uiFactory.createLabel("","", null, prefix);
            optionalSaleLineItemLabels[i].setHorizontalAlignment(labels[i].getHorizontalAlignment());
            constraints.weightx = firstLineWeights[i] * .01;
            add(optionalSaleLineItemLabels[i], constraints);
            constraints.gridx = GridBagConstraints.RELATIVE; // All are relative to the 1st
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the optional data.
     *  @param item The line item that supplies the optional data.
     */

    //--------------------------------------------------------------------------
    /**
     *  Sizes an optional field based on whether or not it contains
     *  text. An empty field will have a width of 0.
     *  @param label the field to be sized
     */
    protected void sizeOptionalField(JLabel label)
    {
         Dimension oldDim = label.getPreferredSize();
        int w = oldDim.width;
        Dimension newDim;

        if(label.getText().equals(""))
        {
            newDim = new Dimension(0, 0);
        }
        else
        {
            FontMetrics fm = label.getFontMetrics(label.getFont());
            w = SwingUtilities.computeStringWidth(fm, label.getText());
            newDim = new Dimension(w + 1, lineHeight);
        }
        label.setPreferredSize(newDim);
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the map value.
     * @param aValue The new map value.
     */
    public void setMap(MAXCodeListMapIfc aValue)
    {
        map = aValue;
    }

    //--------------------------------------------------------------------------
    /**
     * Sets the optional field text in the next available optional slot.
     * @param text The new text for the field.
     */
    protected void setOptionalFieldText(String text)
    {
        if(optionSlot<NUMBER_OF_LABELS)
        {
            optionalFields[optionSlot].setText(text);
            sizeOptionalField(optionalFields[optionSlot]);
            optionSlot++;
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  sets the visual components of the cell
     *  @param value Object
     */
    //--------------------------------------------------------------------------
    public void setData(Object value)
    {
        SaleReturnLineItemIfc lineItem      = (SaleReturnLineItemIfc) value;

        labels[DESCRIPTION].setText(lineItem.getPLUItem().getDescription(locale));
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
                
        labels[PRICE].setText(lineItem.getSellingPrice()
                                      .toGroupFormattedString(getLocale()) + dealItemMarker + overrideMarker);
        labels[EXT_PRICE].setText(lineItem.getExtendedDiscountedSellingPrice()
                                          .toGroupFormattedString(getLocale()));
        String taxMode = lineItem.getTaxStatusDescriptor(); 
        if(!(lineItem.getPLUItem() instanceof GiftCertificateItem))
        labels[MAXIMUM_RETAIL_PRICE].setText(((MAXPLUItemIfc) lineItem.getPLUItem()).getMaximumRetailPrice().toGroupFormattedString(getLocale()));
        labels[TAX].setText(UIUtilities.retrieveCommonText("TaxModeChar." + taxMode, 
                                                           taxMode));
        // set the optional fields
        setOptionalData(lineItem);

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

        if (discountTotal.signum() == CurrencyIfc.ZERO)
        {
            labels[DISCOUNT].setText("");
        }
        else
        {
            labels[DISCOUNT].setText(discountTotal.abs().toGroupFormattedString(getLocale()));
        }
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
        plu.setDescription(locale,"XXXXXXXXXXXXXXX");
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

       return((Object)cell);
    }

    //--------------------------------------------------------------------------
    /**
     *  Sets the format for printing out currency and quantities.
     */
    protected void setPropertyFields()
    {
        super.setPropertyFields();
        
        // Get the format string spec from the UI model properties.
        
        if (props != null)
        {
        
            quantityFormat = DomainGateway.getProperty(DECIMAL_FORMAT);
                //props.getProperty("SaleLineItemRenderer.QuantityFormat", QUANTITY_FORMAT);
             
            // ...just use the one our parent provided for us...
            //giftCardLabel =
            //    props.getProperty("GiftCardLabel", "Gift Card ID:");

            salesAssocLabel =
                props.getProperty("SalesAssociateLabel", "Sales Assoc:");

            giftRegLabel =
                props.getProperty("GiftRegistryLabel", "GiftReg.#");

            serialLabel =
                props.getProperty("SerialLabel", "Serial #");

            sendLabel =
                props.getProperty("SendLabel", "Send");

            kitLabel =
                props.getProperty("KitLabel", "Kit");

            giftReceiptLabel =
                props.getProperty("GiftReceiptLabel", "Gift Receipt");

            restockingFeeLabel =
                props.getProperty("RestockingFeeLabel", "Restocking Fee");

        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Returns the Reason Codes from the DB.
     *  @param listKey  the list key
     *  @param code     the code
     *  @return The reason code
     */
    protected String getReasonCodeValue(String listKey, int code)
    {
        CodeListIfc list = null;
        String      desc = "";

        if (map != null)
        {
            list = map.get(listKey);

            if (list != null)
            {
                String str = Integer.toString(code);
                CodeEntryIfc clei = list.findListEntryByCode(str);
                if (clei != null)
                {
                    desc = clei.getText(locale);
                }
            }
        }
        return desc;
    }
    
    /**
     * check the line item is price required gift card item
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

	//------------------------------------------------------------------------------
	/**
	 * This method returns a instance of java.awt.Component, which is configured to display 
	 *
	 * the required value. The component.paint() method is called to render the cell. 
	 *
	 * @param JList 
	 *
	 * @param Object 
	 *
	 * @param int 
	 *
	 * @param boolean
	 *
	 * @param boolean
	 *
	 * @return Component
	 *
	 */
   public Component getListCellRendererComponent(JList jList,
                                                  Object obj,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean isCellHasFocus)
    {        
        Component returnCellComponent = this;
        
        // If this line item is a price adjustment, create a JPanel composed of the 
        // return and sale components of the price adjustment item.
        if (obj instanceof PriceAdjustmentLineItemIfc)
        {
            PriceAdjustmentLineItemIfc priceAdjustmentLineItem = 
                (PriceAdjustmentLineItemIfc)obj;
            
            MAXSaleLineItemRendererWithMRP returnComponent = new MAXSaleLineItemRendererWithMRP();
            returnComponent =
                (MAXSaleLineItemRendererWithMRP)returnComponent.getListCellRendererComponent(
                    jList,
                    priceAdjustmentLineItem.getPriceAdjustReturnItem(),
                    index,
                    isSelected,
                    false);

            MAXSaleLineItemRendererWithMRP saleComponent = new MAXSaleLineItemRendererWithMRP();
            saleComponent =
                (MAXSaleLineItemRendererWithMRP)saleComponent.getListCellRendererComponent(
                        jList,
                        priceAdjustmentLineItem.getPriceAdjustSaleItem(),
                        index,
                        isSelected,
                        false);
            
            
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
            if(isCellHasFocus)
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
            super.getListCellRendererComponent(jList, obj, index,
                                               isSelected, isCellHasFocus);
            for(int i=0; i<optionalFields.length; i++)
            {
                optionalFields[i].setForeground(getForeground());
            }
            for(int i=0; i<optionalSaleLineItemLabels.length; i++)
            {
                optionalSaleLineItemLabels[i].setForeground(getForeground());
            }
        }
        
        return returnCellComponent;
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
	
	
	public void setOptionalData(SaleReturnLineItemIfc item)
    {
    	// clear the text from all optional fields
        for(int i=0; i<NUMBER_OF_LABELS; i++)
        {
            optionalFields[i].setText("");
            sizeOptionalField(optionalFields[i]);
        }

        // Clear the text from the optional sale line.
        for(int i=0; i<lineBreak+1; i++)
        {
            optionalSaleLineItemLabels[i].setText("");
            sizeOptionalField(optionalSaleLineItemLabels[i]);
        }

        optionSlot = 0;
        String itemText = null;

        if (item.isPartOfPriceAdjustment())
        {            
            if (item.isReturnLineItem())
            {    
                String purchasePriceText = 
                    UIUtilities.retrieveText("SellItemWorkPanelSpec", "posText", priceAdjustmentPurchasePriceLabel);
                setOptionalFieldText(purchasePriceText);
            }
            else
            {
                String currentPriceText = 
                    UIUtilities.retrieveText("SellItemWorkPanelSpec", "posText", priceAdjustmentCurrentPriceLabel);
                setOptionalFieldText(currentPriceText);
            }
        }
        //If the item is a gift card, then sale info field within the line item should
        //display "Gift Card ID: card #"
        if(item.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            GiftCardIfc giftCard =
                ((GiftCardPLUItemIfc)(item.getPLUItem())).getGiftCard();

            if(giftCard != null && !Util.isEmpty(giftCard.getCardNumber()))
            {
                itemText = giftCardLabel + giftCard.getCardNumber();
                setOptionalFieldText(itemText);
            }
        }
        //If an item has a serial number associated with it, then the sale info field
        //within the line item should display 'Serialized'
        else if(!Util.isEmpty(item.getItemSerial()))
        {
            itemText = serialLabel + item.getItemSerial();
            setOptionalFieldText(itemText);
        }

        // if the item is associated with a gift registry it
        // should display "Gift Reg.#" and the number
        if (item.getRegistry() != null)
        {
            itemText = item.getRegistry().getID().toString();

            if (!itemText.equals(""))
            {
                itemText = giftRegLabel + itemText;
                setOptionalFieldText(itemText);
            }
        }
        if (item.getSalesAssociate() != null)
        {
            if (item.getSalesAssociate().getPersonName() != null &&
                item.getSalesAssociateModifiedFlag())
            {
                itemText =
                    salesAssocLabel + item.getSalesAssociate().getPersonName().getFirstLastName();
                setOptionalFieldText(itemText);
            }
        }
        //send item displayed with send label count
        //to show association with particular send
        if (item.getItemSendFlag())
        {
            itemText = sendLabel + item.getSendLabelCount();
            setOptionalFieldText(itemText);
        }
        if(item.getSendLabelCount()==-1)
        {
        	setOptionalFieldText("NotSend");
        }
        // Check if Gift receipt and display "Gift Receipt"
        if (item.isGiftReceiptItem())
        {
            itemText = giftReceiptLabel;
            setOptionalFieldText(itemText);
        }

        // Check if item is a return and there is a restocking fee to display
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
                for(int i=0; i<lineBreak+1; i++)
                {
                    optionalSaleLineItemLabels[i].setPreferredSize(labels[i].getPreferredSize());
                    optionalSaleLineItemLabels[i].setMinimumSize(labels[i].getMinimumSize());
                }
                
                // Multiply restockingFee by the quantity
                BigDecimal qty = item.getItemQuantityDecimal().abs();            // Force to be a positive number
                CurrencyIfc extendedRestockingFee = restockingFee.multiply(qty);

                // Restocking fee is not taxed. Get the no tax character
                String tax = TaxIfc.TAX_MODE_CHAR[TaxIfc.TAX_MODE_NON_TAXABLE];

                // Add next line with restocking info with same column layout
                // as the first labels line
                optionalSaleLineItemLabels[DESCRIPTION].setText(restockingFeeLabel);
                optionalSaleLineItemLabels[PRICE].setText(restockingFee.toGroupFormattedString(getLocale()));
                optionalSaleLineItemLabels[EXT_PRICE].setText(extendedRestockingFee.toGroupFormattedString(getLocale()));
                optionalSaleLineItemLabels[TAX].setText(UIUtilities.retrieveCommonText("TaxModeChar." + tax, tax));
            }
        }

    }

    //--------------------------------------------------------------------------
    /**
     *  main entrypoint - starts the part when it is run as an application
     *  @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        MAXSaleLineItemRendererWithMRP renderer = new MAXSaleLineItemRendererWithMRP();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
