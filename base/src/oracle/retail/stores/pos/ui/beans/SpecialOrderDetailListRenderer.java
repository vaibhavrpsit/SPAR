/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SpecialOrderDetailListRenderer.java /main/17 2013/04/16 13:32:47 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *    3    360Commerce 1.2         3/31/2005 4:30:07 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:25 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:20 PM  Robert Pearse
 *
 *   Revision 1.2  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:22   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Sep 24 2002 14:10:20   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:18:44   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// domain imports
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *  Renders a SpecialOrder line item for display.
 */
//------------------------------------------------------------------------------
public class SpecialOrderDetailListRenderer extends AbstractListRenderer
{
    /**
     *
     */
    private static final long serialVersionUID = -8177438112908229126L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /main/17 $";

    public static final int DESCRIPTION = 0;
    public static final int STATUS      = 1;

    public static final int ITEM_NUM    = 2;
    public static final int QUANTITY    = 3;
    public static final int PRICE       = 4;
    public static final int DISCOUNT    = 5;
    public static final int EXT_PRICE   = 6;

    public static final int MAX_FIELDS  = 7;

    public static final int[] DEFAULT_WEIGHTS = {79,21};
    public static final int[] DEFAULT_WEIGHTS2 = {21,16,21,21,21};//{40, 10, 15, 15, 15, 5};
    public static final int[] DEFAULT_WIDTHS = {4,1};
    public static final int[] DEFAULT_WIDTHS2 = {1,1,1,1,1};

//------------------------------------------------------------------------------
/**
 *  Default constructor.
 */
    public SpecialOrderDetailListRenderer()
    {
        super();
        initialize();
    }

//------------------------------------------------------------------------------
/**
 *  Initializes the renderer.
 */
    protected void initialize()
    {
        setName("SpecialOrderDetailListRenderer");

        // set default in case lookup fails
        firstLineWeights = DEFAULT_WEIGHTS;
        secondLineWeights = DEFAULT_WEIGHTS2;
        firstLineWidths = DEFAULT_WIDTHS;
        secondLineWidths = DEFAULT_WIDTHS2;
        // look up the label weights
        setFirstLineWeights("specialOrderDetailRendererWeights");
        setSecondLineWeights("specialOrderDetailRendererWeights2");
        setFirstLineWidths("specialOrderDetailRendererWidths");
        setSecondLineWidths("specialOrderDetailRendererWidths2");

        fieldCount = MAX_FIELDS;
        lineBreak = STATUS;
        secondLineBreak = EXT_PRICE;

        super.initialize();
    }

//---------------------------------------------------------------------
/**
 *  Sets the content of the visual components based on the data
 *  in the specific object to be displayed. This function is called
 *  by <code>getListCellRendererComponent</code>
 *  @param data the data object to be rendered in the list cell
 */
    public void setData(Object data)
    {
        OrderLineItemIfc item = (OrderLineItemIfc)data;


        labels[DESCRIPTION].setText(item.getPLUItem().getDescription(getLocale()));

        labels[QUANTITY].setText(LocaleUtilities.formatNumber(item.getQuantityOrderedDecimal(),
            getLocale()));

        labels[PRICE].setText(
            item.getItemPrice().getSellingPrice().toGroupFormattedString());

        labels[DISCOUNT].setText(
            (item.getSellingPrice().multiply(item.getItemQuantityDecimal())).
               subtract(item.getExtendedDiscountedSellingPrice()).toGroupFormattedString());

        labels[EXT_PRICE].setText(
            item.getItemPrice().getExtendedDiscountedSellingPrice().toGroupFormattedString());

        labels[STATUS].setText(
            UIUtilities.retrieveCommonText(OrderConstantsIfc.ORDER_ITEM_STATUS_DESCRIPTORS[item.getItemStatus()]));

        labels[ITEM_NUM].setText(item.getPLUItemID());
    }

//---------------------------------------------------------------------
/**
 *  Makes rendering more efficient by generating a display
 *  object with its data set to the maximum values.
 *  @return a renderable object
 */
//---------------------------------------------------------------------
    public Object createPrototype()
    {
        return null;
    }

    //---------------------------------------------------------------------
    /**
       Sets the format for printing out currency and quantities.
    */
    //---------------------------------------------------------------------
    protected void setPropertyFields()
    {
        // Get the format string spec from the UI model properties.

        if (props != null)
        {
        }
    }
}
