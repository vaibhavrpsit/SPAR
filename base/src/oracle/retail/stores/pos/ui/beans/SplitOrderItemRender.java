/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SplitOrderItemRender.java /main/6 2013/04/16 13:32:48 vtemker Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* vtemker     04/16/13 - Moved constants in OrderLineItemIfc to
*                        OrderConstantsIfc in common project
* tksharma    12/10/12 - commons-lang update 3.1
* sgu         11/13/12 - enhance order print and view
* sgu         11/09/12 - optimize order lookup flow
* sgu         10/30/12 - add new file
* sgu         10/29/12 - check cancel order status
* sgu         10/26/12 - enhance split order item view
* sgu         10/04/12 - split order item for pickup
* sgu         10/04/12 - add suport to split order line for partial
*                        pickup/cancel
* sgu         10/02/12 - add new class
* sgu         10/02/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItem;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.commons.lang3.StringUtils;

public class SplitOrderItemRender extends AbstractListRenderer
{
    private static final long serialVersionUID = -7490584897040069836L;

    public static int[] ORDER_ITEM_WEIGHTS = {58,21};
    public static int[] ORDER_ITEM_WEIGHTS2 = {21,21,21,21};
    public static int[] ORDER_ITEM_WIDTHS = {5,1};
    public static int[] ORDER_ITEM_WIDTHS2 = {3,1,1,3};

    /** the item description column */
    public static int DESCRIPTION = 0;

    /** the pickup store ID column */
    public static int PICKUP_STORE_ID = 1;

    /** the stock column */
    public static int STOCK = 2;

    /** the quantity column */
    public static int QUANTITY    = 3;

    /** the status column */
    public static int STATUS = 4;

    /** the delivery flag column */
    public static int DELIVERY_FLAG = 5;

    public static int MAX_FIELDS    = 6;

    /**
       pickup Label
     **/
    protected String pickupLabel = "Pickup";

    /**
     * delivery label
     */
    protected String deliveryLabel = "Delivery";

    /**
       pickup name formatter
     **/
    protected String pickupNameFormatter = "{0}: {1} {2}";

    /**
       renderer Pckup Label tag
     **/
    protected static final String RENDERER_PICKUP_LABEL = "Renderer.PickupLabel";

    /**
     * render Delivery Label tag
     */
    protected static final String RENDERER_DELIVERY_LABEL = "Renderer.DeliveryLabel";

    /**
       renderer Pckup Label tag
    **/
    protected static final String RENDERER_SHIP_LABEL = "Renderer.ShipLabel";

    /**
       renderer Pckup Label tag
    **/
    protected static final String RENDERER_SALE_LABEL = "Renderer.SaleLabel";

    /**
       pickup fullname formatter tag
     **/
    protected static final String RENDERER_PICKUP_NAME_FORMATTER = "Renderer.PickupNameFormatter";

    //--------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public SplitOrderItemRender()
    {
        super();
        setName("SplitOrderItemRender");

        // set default in case lookup fails
        firstLineWeights = ORDER_ITEM_WEIGHTS;
        secondLineWeights = ORDER_ITEM_WEIGHTS2;
        firstLineWidths = ORDER_ITEM_WIDTHS;
        secondLineWidths = ORDER_ITEM_WIDTHS2;

        // look up the label weights
        setFirstLineWeights("orderItemRendererWeights");
        setSecondLineWeights("orderItemRendererWeights2");
        setFirstLineWidths("orderItemRendererWidths");
        setSecondLineWidths("orderItemRendererWidths2");

        fieldCount = MAX_FIELDS;
        lineBreak = PICKUP_STORE_ID;
        secondLineBreak = DELIVERY_FLAG;
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initializes the optional components.
     */
    //---------------------------------------------------------------------
    protected void initOptions()
    {
        labels[DESCRIPTION].setHorizontalAlignment(JLabel.LEFT);
        labels[PICKUP_STORE_ID].setHorizontalAlignment(JLabel.CENTER);
        labels[STOCK].setHorizontalAlignment(JLabel.LEFT);
        labels[QUANTITY].setHorizontalAlignment(JLabel.LEFT);
        labels[STATUS].setHorizontalAlignment(JLabel.CENTER);
        labels[DELIVERY_FLAG].setHorizontalAlignment(JLabel.CENTER);
    }

    //---------------------------------------------------------------------
    /**
       Sets the format for printing out currency and quantities.
    */
    //---------------------------------------------------------------------
    protected void setPropertyFields()
    {
        pickupLabel =
            UIUtilities.retrieveCommonText(RENDERER_PICKUP_LABEL);
        deliveryLabel =
            UIUtilities.retrieveCommonText(RENDERER_DELIVERY_LABEL);
        pickupNameFormatter =
            UIUtilities.retrieveCommonText(RENDERER_PICKUP_NAME_FORMATTER);
    }

    //---------------------------------------------------------------------
    /**
        Builds each  line item to be displayed.
      */
    //---------------------------------------------------------------------
    public void setData(Object value)
    {
        SplitOrderItemIfc orderItem = (SplitOrderItemIfc)value;
        SaleReturnLineItemIfc originalLineItem = orderItem.getOriginalOrderLineItem();
        OrderItemStatusIfc orderItemStatus = originalLineItem.getOrderItemStatus();

        String description = originalLineItem.getPLUItem().getDescription(getLocale());
        if (Util.isEmpty(description))
        {
            description = originalLineItem.getReceiptDescription();
        }

        labels[DESCRIPTION].setText(description);
        labels[QUANTITY].setText(LocaleUtilities.formatNumber(orderItem.getQuantity(),getLocale()));
        labels[STOCK].setText(originalLineItem.getItemID());

        // set status
        int status = orderItem.getStatus().getStatus();
        String statusDesc = orderItem.getStatus().statusToString(status);
        labels[STATUS].setText(UIUtilities.retrieveCommonText(statusDesc,statusDesc));

        // set pickup store id and person name
        labels[PICKUP_STORE_ID].setText("");
        String pickupStoreID = orderItemStatus.getPickupStoreID();
        if (!StringUtils.isBlank(pickupStoreID))
        {
            labels[PICKUP_STORE_ID].setText(pickupStoreID);
            boolean hasPickupName = !StringUtils.isBlank(orderItemStatus.getPickupFirstName()) ||
                                    !StringUtils.isBlank(orderItemStatus.getPickupLastName());
            if (hasPickupName)
            {
                String pickupName = LocaleUtilities.formatComplexMessage(pickupNameFormatter, new Object[]{
                        pickupLabel,
                        orderItemStatus.getPickupFirstName(),
                        orderItemStatus.getPickupLastName()});
                labels[STOCK].setText(labels[STOCK].getText().concat("  ").concat(pickupName));
            }
        }

        // set delivery flag
        labels[DELIVERY_FLAG].setText("");
        if (orderItemStatus.getItemDispositionCode() == OrderConstantsIfc.ORDER_ITEM_DISPOSITION_DELIVERY)
        {
            labels[DELIVERY_FLAG].setText(deliveryLabel);
        }
    }

    //---------------------------------------------------------------------
    /**
     * creates the prototype cell to speed updates
     */
    //---------------------------------------------------------------------
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
        SaleReturnLineItemIfc cell =
            DomainGateway.getFactory().getSaleReturnLineItemInstance();

        PLUItemIfc plu = DomainGateway.getFactory().getPLUItemInstance();
        plu.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        plu.setItemID("12345678901234");
        cell.setPLUItem(plu);

        CurrencyIfc testPrice = DomainGateway.getBaseCurrencyInstance("88888888.88");

        ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();
        price.setSellingPrice(testPrice);
        price.setItemDiscountTotal(testPrice);
        price.setExtendedSellingPrice(testPrice);

        price.setItemQuantity(888888);
        cell.setItemPrice(price);

        EmployeeIfc emp = DomainGateway.getFactory().getEmployeeInstance();
        cell.setSalesAssociate(emp);
        cell.getOrderItemStatus().getStatus().setStatus(1);

        SplitOrderItemIfc  orderItem = new SplitOrderItem(cell, BigDecimal.ONE,
                OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP);

        return(orderItem);
    }

}

