/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/RelatedItemListRenderer.java /main/17 2012/11/29 14:38:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    asinto 09/29/14 - Fix for the mock of item image error message
 *    cgreen 11/27/12 - enhancement for displaying item images in sale screen
 *                      table
 *    cgreen 08/24/11 - added null check
 *    acadar 06/10/10 - use default locale for currency display
 *    acadar 06/09/10 - XbranchMerge acadar_tech30 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 05/27/10 - convert to oracle packaging
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/17/09 - fix item image painting in lists
 *    cgreen 03/19/09 - refactoring changes
 *    ddbake 01/08/09 - Update to layout of item image screens to account for
 *                      I18N clipping issues.
 *    ddbake 10/23/08 - Final updates for localized item description support
 *    atirke 10/01/08 - modified for item images
 *    atirke 09/30/08 -
 *    atirke 09/29/08 - added logic to display related item images
 *
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         6/12/2007 8:48:24 PM   Anda D. Cadar   SCR
 *                      27207: Receipt changes -  proper alignment for amounts
 *  1    360Commerce 1.0         12/13/2005 4:47:07 PM  Barry A. Pape
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.RelatedItemSummaryIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * @author jdeleau
 * @version $Revision: /main/17 $
 */
public class RelatedItemListRenderer extends ItemListRenderer
{
    private static final long serialVersionUID = 8922218286783053074L;

    protected static Logger logger = Logger.getLogger(RelatedItemListRenderer.class);

    /**
     * Constructor
     *
     * @since NEP67
     */
    public RelatedItemListRenderer()
    {
        setName("RelatedItemListRenderer");
    }

    /**
     * Builds each line item to be displayed.
     * 
     * @see oracle.retail.stores.pos.ui.beans.AbstractListRenderer#setData(java.lang.Object)
     * @param value RelatedItemSummaryIfc object to display
     * @since NEP67
     */
    @Override
    public void setData(Object value)
    {
        RelatedItemSummaryIfc item = (RelatedItemSummaryIfc)value;
        if (item != null)
        {
            ItemImageIfc itemImage = item.getItemImage();

            // shows no image
            if (itemImage == null || itemImage.isEmptyImage())
            {
                labels[IMAGE].setIcon(null);
                labels[IMAGE].setText(null);
            }
            // displays error message
            else if (itemImage.isImageError())
            {
                labels[IMAGE].setIcon(null);
                String label = UIUtilities.retrieveText("ItemLocationSpec", BundleConstantsIfc.POS_BUNDLE_NAME, "ImageLabel", "Item Image");
                String message = UIUtilities.retrieveText("ItemLocationSpec", BundleConstantsIfc.POS_BUNDLE_NAME, "ErrorMessageLabel", "Could Not Display");
                StringBuilder formattedMessage = new StringBuilder("<HTML><BR><b>").append(label).append("</b><BR><b>").append(message).append("</b></HTML>");
                labels[IMAGE].setText(formattedMessage.toString());
            }
            // image is loading
            else if (itemImage.isLoadingImage())
            {
                labels[IMAGE].setIcon(ItemListRenderer.getLoadingImage(getParent()));
                labels[IMAGE].setText(null);
            }
            // related item images
            else if (itemImage.getImage() != null && !itemImage.isLoadingImage())
            {
                labels[IMAGE].setIcon(itemImage.getImage());
                labels[IMAGE].setText(null);
            }

            labels[ITEM_ID].setText(item.getItemID());
            labels[ITEM_DESC].setText(item.getDescription(getLocale()));
            labels[DEPARTMENT].setText(item.getDepartmentName());
            if (item.getPrice() != null)
            {
                labels[PRICE].setText(item.getPrice().toFormattedString());
            }
        }
    }

    /**
     * Unimplemented.
     *
     * @return null
     * @see oracle.retail.stores.pos.ui.beans.AbstractListRenderer#createPrototype()
     */
    @Override
    public Object createPrototype()
    {
        return null;
    }

}
