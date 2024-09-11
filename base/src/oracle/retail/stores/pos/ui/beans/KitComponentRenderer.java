/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/KitComponentRenderer.java /main/22 2013/09/06 15:15:25 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/05/14 - change use of deprecated methods.
 *    yiqzhao   09/06/13 - Display the value of item component quantity.
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    yiqzhao   08/31/12 - add pickup/delivery icons, date and store info in
 *                         kit components screen.
 *    yiqzhao   08/06/12 - tax label layout -- right on Ext Price
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ddbaker   11/10/08 - Updated based on new requirements
 *    ddbaker   11/06/08 - Update due to merges.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         5/8/2007 11:32:27 AM   Anda D. Cadar
 *         currency changes for I18N
 *    5    360Commerce 1.4         4/25/2007 8:51:32 AM   Anda D. Cadar   I18N
 *         merge
 *    4    360Commerce 1.3         1/22/2006 11:45:27 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:28:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:13 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.4  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Apr 18 2003 12:46:00   bwf
 * Make sure using correct bundle name and id for tax status.
 * Resolution for 1866: I18n Database  support
 * 
 *    Rev 1.3   Apr 16 2003 16:34:28   RSachdeva
 * Label Internationalization
 * Resolution for POS SCR-2159: Serial # in Component Options not internationalized
 * 
 *    Rev 1.2   Sep 06 2002 17:25:26   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 14 2002 18:17:56   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:50:38   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Mar 2002 17:34:26   dfh
 * removed Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 * 
 *    Rev 1.0   Mar 18 2002 11:55:54   msg
 * Initial revision.
 * 
 *    Rev 1.5   13 Mar 2002 17:07:56   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.4   Feb 05 2002 16:43:54   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.3   31 Jan 2002 15:56:44   pjf
 * added getSaleInfoText()
 * Resolution for POS SCR-859: Serialized kit items are not displaying serial #'s on the component screen
 *
 *    Rev 1.2   28 Jan 2002 12:43:18   pjf
 * Updated to call SaleReturnLineItem.getTaxStatusDescriptor() for display of tax information.
 * Resolution for POS SCR-838: Kit Header tax flag displayed incorrectly on sell item screen.
 *
 *    Rev 1.1   Jan 19 2002 10:30:50   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   30 Oct 2001 11:47:10   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridBagConstraints;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.UIUtilities;
import java.math.BigDecimal;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * This is the renderer for Kit Components
 * 
 * @version $Revision: /main/22 $
 */
public class KitComponentRenderer extends LineItemRenderer
{
    private static final long serialVersionUID = -2469657302386292678L;

    /** revision number supplied by version control    **/
    public static final String revisionNumber = "$Revision: /main/22 $";

    /** the property for the quantity format.    **/
    public static final String QUANTITY_FORMAT = "0.00;(0.00)";

    public static final int DESCRIPTION = 0;
    public static final int SALE_INFO   = 1;


    public static final int STOCK_NUM   = 2;
    public static final int QUANTITY    = 3;
    public static final int PRICE       = 4;
    public static final int DISCOUNT    = 5;
    public static final int EXT_PRICE   = 6;
    public static final int TAX         = 7;
    
    public static final int MAX_FIELDS  = 8;
    
    /** number of labels per line */
    public static int  NUMBER_OF_LABELS= 7;
    
    /**
       Tax Mode Tag     
     */
    protected String taxModeTag = "TaxModeChar.";
    
    protected JLabel[] optionalFields;

    // Optional information for the line item, in the same column layout as item
    // labels. To display additional pricing info etc.
    protected JLabel[] optionalSaleLineItemLabels;

    /** Code List for reason code lookup */
    //protected CodeListMapIfc map = null;

    protected int optionSlot = 0;
    
    protected int leftInset = 30;

    /**
     *  Default constructor.
     */
    public KitComponentRenderer()
    {
        super();
        setName("KitComponentRenderer");
    }
    
    /**
     *  Initializes this renderer's components.
     */
    protected void initOptions()
    {
        String prefix = UI_PREFIX + ".label";
        
    	super.initOptions();
    	
        optionalFields = new JLabel[NUMBER_OF_LABELS];
        // create the optional fields
        for (int i = 0; i < NUMBER_OF_LABELS; i++)
        {
            optionalFields[i] = uiFactory.createLabel("", "", null, prefix);
            optionalFields[i].setHorizontalAlignment(JLabel.LEFT);
        }
        
        layoutOptions();
    }

    /**
     *  Initializes the layout and lays out the components.
     */
    protected void layoutOptions()
    {
        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        // add optional fields by column
        constraints.gridwidth = 3;
        constraints.weightx = 0.0;

        // add optional fields by column
        constraints.gridx = 0;
        constraints.insets.left = leftInset;
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        add(optionalFields[0], constraints);
        add(optionalFields[2], constraints);
        add(optionalFields[4], constraints);
        add(optionalFields[1], constraints);
        add(optionalFields[3], constraints);

        // Set the layout of the optional sale line item labels
        //layoutOptionalSaleLineItemLabels();

        // add the item level screen text
        //layoutItemLevelScreenText();
    }

    /**
     *  Extracts and addition data that is optionally displayed.
     *  @param data Object
     */
    public void setOptionalData(Object value)
    {
        // clear the text from all optional fields
        for (int i = 0; i < NUMBER_OF_LABELS; i++)
        {
            optionalFields[i].setText("");
            sizeOptionalField(optionalFields[i]);
        }

        optionSlot = 0;
        
        SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)value;
        String taxStatus = lineItem.getTaxStatusDescriptor(); 

        labels[TAX].setText(UIUtilities.retrieveCommonText(taxModeTag + taxStatus,taxStatus));
        labels[SALE_INFO].setText(getSaleInfoText(lineItem));
        
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

        
        OrderItemStatusIfc orderItemStatus = lineItem.getOrderItemStatus();
        
        if (orderItemStatus.getStatus().getStatus() != OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)
        {
            boolean isDeliveryItem = orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY;
 
            boolean	isPickupItem = orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_PICKUP; 
            
            if (isDeliveryItem)
            {
                String label = deliveryLabel;
                if (orderItemStatus.isCrossChannelItem())
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

                int addressType = orderDeliveryDetail.getDeliveryAddress().getAddressType();
                if (addressType == AddressConstantsIfc.ADDRESS_TYPE_UNSPECIFIED)
                {
                    addressType = AddressConstantsIfc.ADDRESS_TYPE_HOME;
                }
                String addressTypeLabel = UIUtilities.retrieveCommonText(AddressConstantsIfc.ADDRESS_TYPE_DESCRIPTOR[addressType]);
                setOptionalFieldText(LocaleUtilities.formatComplexMessage(shipToAddrLabel, addressTypeLabel));
            }
            else if (isPickupItem)
            {
                if (orderItemStatus.isShipToStoreForPickup())
                {
                    setOptionalFieldText(shipToStoreLabel.concat(orderItemStatus.getPickupStoreID()), deliveryIcon);
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
                }
            }
        }
    }

    /**
     * Returns the description text that goes in the description field.
     * 
     * @return String The description text that goes into the description field.
     * @param item
     */
    protected String getSaleInfoText(SaleReturnLineItemIfc item)
    {
        //first do the SalesAssociate
        StringBuffer buf = new StringBuffer();
        //If the item is a gift card, then sale info field within the line item should
        //display "Gift Card ID: card #"
        if(item.getPLUItem() instanceof GiftCardPLUItemIfc)
        {
            GiftCardIfc giftCard = ((GiftCardPLUItemIfc)(item.getPLUItem())).getGiftCard();
            if(giftCard != null && giftCard.getCardNumber() != null &&
              !(giftCard.getCardNumber().equals("")))
            {
                buf.append(giftCardLabel + giftCard.getCardNumber());
            }
        }

        //If an item has a serial number associated with it, then the sale info field
        //within the line item should display 'Serialized'
        if(item.getItemSerial() != null)
        {
            buf.append(serialLabel).append(item.getItemSerial());
        }

        // now do the gift registry
        buf.append("  ").append(getGiftRegistryText(item));
        return buf.toString();
    }

    /**
     * Sizes an optional field based on whether or not it contains text. An
     * empty field will have a width of 0.
     * 
     * @param label the field to be sized
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
    protected void setOptionalFieldText(String text, ImageIcon icon)
    {
        if (optionSlot < NUMBER_OF_LABELS)
        {
            optionalFields[optionSlot].setText(text);
            optionalFields[optionSlot].setIcon(icon);
            sizeOptionalField(optionalFields[optionSlot]);
            optionSlot++;
        }
    }

    /**
     * Sets the optional field text in the next available optional slot.
     * 
     * @param text The new text for the field.
     */
    protected void setOptionalFieldText(String text)
    {
        if (optionSlot < NUMBER_OF_LABELS)
        {
            optionalFields[optionSlot].setText(text);
            // setIcon() sets the "defult icon", so if there are any other icons in any other
            // line items, then this JLabel will have a default icon. So we null it out instead.
            optionalFields[optionSlot].setIcon(null);
            sizeOptionalField(optionalFields[optionSlot]);
            optionSlot++;
        }
    }

    // ---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     * 
     * @return SaleReturnLineItem the prototype renderer
     */
    // ---------------------------------------------------------------------
    public Object createPrototype()
    {
        KitComponentLineItemIfc cell = DomainGateway.getFactory().getKitComponentLineItemInstance();
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

    /**
     * Sets the format for printing out currency and quantities.
     */
    protected void setPropertyFields()
    {
        //Labels that need to be displayed in User Interface Locale
        salesAssocLabel =
          UIUtilities.retrieveCommonText(RENDERER_SALE_ASSOC_LABEL);

        giftRegLabel =
          UIUtilities.retrieveCommonText(RENDERER_GIFT_REGISTRY_LABEL);

        serialLabel =
          UIUtilities.retrieveCommonText(RENDERER_SERIAL_LABEL);

        giftCardLabel =
          UIUtilities.retrieveCommonText(RENDERER_GIFT_CARD_LABEL);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args String[]
     */
    public static void main(String[] args)
    {
        Frame frame = new Frame();
        KitComponentRenderer aKitComponentRenderer;
        aKitComponentRenderer = new KitComponentRenderer();
        frame.add("Center", aKitComponentRenderer);
        frame.setSize(aKitComponentRenderer.getSize());
        frame.setVisible(true);
    }
}
