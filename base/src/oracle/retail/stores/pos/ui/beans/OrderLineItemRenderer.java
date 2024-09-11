/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderLineItemRenderer.java /main/22 2012/11/29 14:38:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/27/12 - enhancement for displaying item images in sale
 *                         screen table
 *    sgu       10/16/12 - add function to determine if an order line item is a
 *                         pickup or cancel line item
 *    sgu       10/15/12 - refactor order pickup flow to support partial pickup
 *    sgu       08/20/12 - shift order line columns
 *    rgour     07/31/12 - Fixed the issues, related to WPTG
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    jswan     06/01/10 - Modified to support transaction retrieval
 *                         performance and data requirements improvements.
 *    jswan     05/28/10 - XbranchMerge jswan_hpqc-techissues-73 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ddbaker   11/10/08 - Updated based on new requirements
 *    ddbaker   11/06/08 - Update due to merges.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         7/9/2007 3:07:53 PM    Anda D. Cadar   I18N
 *         changes for CR 27494: POS 1st initialization when Server is offline
 *    5    360Commerce 1.4         5/8/2007 11:32:28 AM   Anda D. Cadar
 *         currency changes for I18N
 *    4    360Commerce 1.3         4/25/2007 8:51:31 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:52 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:53 PM  Robert Pearse
 *
 *   Revision 1.4  2004/04/08 20:33:02  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Mar 07 2003 17:11:10   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 06 2002 17:25:28   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:18:06   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:54:48   msg
 * Initial revision.
 *
 *    Rev 1.1   27 Mar 2002 17:34:36   dfh
 * removde Dollar Sign from currency format
 * Resolution for POS SCR-365:  appears on several screens, not to specification
 * Resolution for POS SCR-1445: Dollar signs are showing up on the receipt
 *
 *    Rev 1.0   Mar 18 2002 11:56:34   msg
 * Initial revision.
 *
 *    Rev 1.7   13 Mar 2002 17:08:04   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 *
 *    Rev 1.6   Jan 25 2002 08:48:28   dfh
 * fix due to updates to salereturnlineitemifc
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.5   Jan 20 2002 18:25:22   mpm
 * Cleaned up rendering (not completely) on order items.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   Jan 19 2002 10:31:14   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.2   15 Jan 2002 18:55:12   cir
 * Use SaleReturnLineItem
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   05 Nov 2001 17:37:58   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 24 2001 11:19:24   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:16:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.lineitem.ItemPriceIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This is the renderer for the OrderLineItem Table. It displays OrderLineItems
 * in the same manner as the SaleLineItemRenderer.
 * 
 * @version $Revision: /main/22 $
 */
public class OrderLineItemRenderer extends LineItemRenderer
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2431967046929734786L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/22 $";

    public static int[] ORDER_LINE_ITEM_WEIGHTS = {58,21,21};
    public static int[] ORDER_LINE_ITEM_WEIGHTS2 = {21,16,21,21,21}; //{40,12,12,12,12,13};
    public static int[] ORDER_LINE_ITEM_WIDTHS = {3,1,1};
    public static int[] ORDER_LINE_ITEM_WIDTHS2 = {1,1,1,1,1};

    public static int DESCRIPTION = 0;
    public static int INFO        = 1;
    public static int STATUS      = 2;

    public static int STOCK       = 3;
    public static int QUANTITY    = 4;
    public static int PRICE       = 5;
    public static int DISCOUNT    = 6;
    public static int EXT_PRICE   = 7;

    public static int MAX_FIELDS  = 8;


    /**
     *  Default Constructor.
     */
    public OrderLineItemRenderer()
    {
        super();
        setName("OrderLineItemRenderer");
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.AbstractListRenderer#initialize()
     */
    @Override
    protected void initialize()
    {
        // set default in case lookup fails
        firstLineWeights = ORDER_LINE_ITEM_WEIGHTS;
        secondLineWeights = ORDER_LINE_ITEM_WEIGHTS2;
        firstLineWidths = ORDER_LINE_ITEM_WIDTHS;
        secondLineWidths = ORDER_LINE_ITEM_WIDTHS2;
        lineBreak = STATUS;

        // look up the label weights
        setFirstLineWeights("orderItemRendererWeights");
        setSecondLineWeights("orderItemRendererWeights2");
        setFirstLineWidths("orderItemRendererWidths");
        setSecondLineWidths("orderItemRendererWidths2");

        super.initialize();
    }

    /**
     * creates the prototype cell to speed updates
     * @return OrderLineItemIfc the prototype renderer
     */
    public Object createPrototype()
    {
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

       return cell;
    }

    /**
     * Extracts the data from a domain object and sets the visual components of
     * the cell.
     * 
     * @param data Object
     */
    @Override
    public void setData(Object value)
    {
        OrderLineItemIfc lineItem = (OrderLineItemIfc)value;

        String description = lineItem.getPLUItem().getDescription(getLocale());
        if (Util.isEmpty(description))
        {
            description = lineItem.getReceiptDescription();
        }
        labels[DESCRIPTION].setText(description);
        labels[PRICE].setText(lineItem.getSellingPrice().toGroupFormattedString());
        labels[DISCOUNT].setText(getDiscountTotal(lineItem));
        labels[EXT_PRICE].setText(lineItem.getExtendedDiscountedSellingPrice().toGroupFormattedString());
        labels[QUANTITY].setText(LocaleUtilities.formatNumber(lineItem.getItemQuantityDecimal(),getLocale()));
        labels[STOCK].setText(lineItem.getItemID());

        // set status
        int status;
        if (lineItem.isPickupCancelLineItem())
        {
            status = lineItem.getItemStatus();
        }
        else
        {
            status = lineItem.getOrderItemStatus().getStatus().getStatus();
        }
        String statusDesc = lineItem.getOrderItemStatus().getStatus().statusToString(status);
        labels[STATUS].setText(UIUtilities.retrieveCommonText(statusDesc,statusDesc));
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
            quantityFormat = props.getProperty("OrderDetailRenderer.QuantityFormat",
                    DomainGateway.getNumberFormat(getLocale()).toString());

            salesAssocLabel = props.getProperty("SaleLineItemRenderer.SalesAssociateLabel", "Sales Assoc:");

            giftRegLabel = props.getProperty("SaleLineItemRenderer.GiftRegistryLabel", "Gift Registry #");
        }
    }

    // ---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        OrderLineItemRenderer renderer = new OrderLineItemRenderer();
        renderer.setData(renderer.createPrototype());

        UIUtilities.doBeanTest(renderer);
    }
}
