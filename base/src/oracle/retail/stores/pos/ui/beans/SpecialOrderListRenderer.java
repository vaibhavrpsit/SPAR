/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SpecialOrderListRenderer.java /main/19 2013/01/30 15:17:11 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       01/25/13 - format customer name
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    acadar    02/25/09 - override the getDefaultLocale from JComponent
 *    acadar    02/25/09 - use application default locale instead of jvm locale
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         6/12/2007 8:48:25 PM   Anda D. Cadar   SCR
 *         27207: Receipt changes -  proper alignment for amounts
 *    3    360Commerce 1.2         3/31/2005 4:30:08 PM   Robert Pearse
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
 *    Rev 1.0   Aug 29 2003 16:12:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Sep 24 2002 14:10:20   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:18:46   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:26   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:51:04   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:52:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 19 2002 10:32:04   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Dec 10 2001 19:24:46   cir
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Locale;

import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;
import oracle.retail.stores.pos.ui.UIUtilities;
//------------------------------------------------------------------------------
/**
 *  Renders a Order object for display.
 */
//------------------------------------------------------------------------------
public class SpecialOrderListRenderer extends AbstractListRenderer
{
    /**
     * serial version UID
     */
    private static final long serialVersionUID = 3331769195156414256L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /main/19 $";

    public static final int ORDER_NUM = 0;
    public static final int CUSTOMER  = 1;
    public static final int ORDERED   = 2;
    public static final int UPDATED   = 3;
    public static final int STATUS    = 4;
    public static final int TOTAL     = 5;

    public static final int MAX_FIELDS = 6;

    public static final int[] DEFAULT_WEIGHTS = {29, 24, 12, 12, 12, 11};

//------------------------------------------------------------------------------
/**
 *  Default constructor.
 */
    public SpecialOrderListRenderer()
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
        // look up the label weights
        setFirstLineWeights("specialOrderRendererWeights");

        fieldCount = MAX_FIELDS;
        lineBreak = TOTAL;
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
        OrderSummaryEntryIfc order = (OrderSummaryEntryIfc)data;

        labels[ORDER_NUM].setText(order.getOrderID());

        String customerName = OrderUtilities.formatCustomerName(order);
        labels[CUSTOMER].setText(customerName);

        Locale defaultLocale = getDefaultLocale();
        if(order.getTimestampCreated() != null)
        {
            labels[ORDERED].setText(order.getTimestampCreated().toFormattedString(defaultLocale));
        }
        else
        {
            labels[ORDERED].setText("");
        }
        if(order.getTimestampModified() != null)
        {
            labels[UPDATED].setText(order.getTimestampModified().toFormattedString(defaultLocale));
        }
        else
        {
            labels[UPDATED].setText("");
        }

        labels[STATUS].setText(UIUtilities.retrieveCommonText(OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS[order.getOrderStatus()]));

        if(order.getOrderTotal() != null)
        {
            labels[TOTAL].setText(order.getOrderTotal().toFormattedString());
        }
        else
        {
            labels[TOTAL].setText("");
        }
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
