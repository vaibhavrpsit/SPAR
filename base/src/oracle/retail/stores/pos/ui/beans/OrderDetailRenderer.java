/* ===========================================================================
 * Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderDetailRenderer.java /main/18 2012/08/01 14:21:08 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/05/14 - change use of deprecated methods.
 *    rgour     07/31/12 - Fixed the issues, related to WPTG
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   5    360Commerce 1.4         5/8/2007 11:32:28 AM   Anda D. Cadar
 *        currency changes for I18N
 *   4    360Commerce 1.3         4/25/2007 8:51:32 AM   Anda D. Cadar   I18N
 *        merge
 *   3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:23:51 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:12:52 PM  Robert Pearse   
 *
 *  Revision 1.4  2004/04/08 20:33:02  cdb
 *  @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *  Revision 1.3  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:11:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Sep 06 2002 17:25:26   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 14 2002 18:18:06   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:54:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Mar 2002 17:34:32   dfh
 * removde Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Frame;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.ui.UIUtilities;


/**
 * This is the renderer for the OrderLineItem Table. It displays OrderLineItems
 * in the same manner as the SaleLineItemRenderer.
 * 
 * @version $Revision: /main/18 $
 */
public class OrderDetailRenderer extends LineItemRenderer
{
    private static final long serialVersionUID = 1558608627730081064L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/18 $";

    // quantity format string
    private static final String QUANTITY_FORMAT = "#;(#)";

    public static final int DESCRIPTION = 0;
    public static final int QUANTITY    = 1;
    public static final int PRICE       = 2;
    public static final int DISCOUNT    = 3;
    public static final int EXT_PRICE   = 4;
    public static final int STATUS      = 5;
    public static final int STOCK_NUM   = 6;
    public static final int SALE_INFO   = 7;
    
    public static final int MAX_FIELDS  = 8;

    /**
     * Default constructor.
     */
    public OrderDetailRenderer()
    {
        setName("KitComponentRenderer");
    }

    /**
     * Extracts and addition data that is optionally displayed.
     * 
     * @param data Object
     */
    public void setOptionalData(Object value)
    {
        OrderLineItemIfc lineItem = (OrderLineItemIfc)value;
        String status = lineItem.statusToString(lineItem.getItemStatus());
        labels[STATUS].setText(UIUtilities.retrieveCommonText(status, status));
        labels[SALE_INFO].setText(getSaleInfoText(lineItem));
    }

    /**
     * creates the prototype cell to speed updates
     * 
     * @return OrderLineItemIfc the prototype renderer
     */
    public Object createPrototype()
    {
        OrderLineItemIfc cell = DomainGateway.getFactory().getOrderLineItemInstance();

        PLUItemIfc plu = DomainGateway.getFactory().getPLUItemInstance();
        plu.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(),
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        plu.setItemID("12345678901234");
        cell.setPLUItem(plu);

        CurrencyIfc testPrice = DomainGateway.getBaseCurrencyInstance("88888888.88");
        ItemPriceIfc price = DomainGateway.getFactory().getItemPriceInstance();
        price.setSellingPrice(testPrice);
        price.setItemQuantity(888888);
        price.setItemDiscountTotal(testPrice);
        price.setExtendedSellingPrice(testPrice);
        cell.setItemPrice(price);

        EmployeeIfc emp = DomainGateway.getFactory().getEmployeeInstance();
        cell.setSalesAssociate(emp);

        return cell;
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return Util.parseRevisionNumber(revisionNumber);
    }

    /**
     * Sets the format for printing out currency and quantities.
     */
    protected void setPropertyFields()
    {
        // Get the format string spec from the UI model properties.

        if (props != null)
        {
            quantityFormat = props.getProperty("OrderDetailRenderer.QuantityFormat", QUANTITY_FORMAT);
            salesAssocLabel = props.getProperty("SaleLineItemRenderer.SalesAssociateLabel", "Sales Assoc:");
            giftRegLabel = props.getProperty("SaleLineItemRenderer.GiftRegistryLabel", "Gift Registry #");
        }
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args String[]
     */
    public static void main(String[] args)
    {
        Frame frame = new Frame();
        SaleLineItemRenderer aSaleLineItemRenderer;
        aSaleLineItemRenderer = new SaleLineItemRenderer();
        frame.add("Center", aSaleLineItemRenderer);
        frame.setSize(aSaleLineItemRenderer.getSize());
        frame.setVisible(true);
    }
}
