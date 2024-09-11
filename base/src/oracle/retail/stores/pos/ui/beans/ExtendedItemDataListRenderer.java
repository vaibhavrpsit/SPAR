/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ExtendedItemDataListRenderer.java /main/1 2014/06/03 17:06:10 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/07/14 - Improve performance of retrieving item images.
 *    asinton   09/29/14 - Fix for the mock of item image error message
 *    asinton   06/02/14 - Added support for extended customer data.
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.ui.beans;

import java.awt.Container;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Renderer for use with the {@link ExtendedItemDataListBean} to show item data.
 * @since 14.1
 *
 */
public class ExtendedItemDataListRenderer extends AbstractListRenderer
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -7092894217175930435L;

    public static int IMAGE      = 0;
    public static int ITEM_DESC  = 1;
    public static int SKIP_1     = 2;
    public static int ITEM_ID    = 3;
    public static int DEPARTMENT = 4;
    public static int PRICE      = 5;
    public static int MAX_FIELDS = 6;

    // setting the height of the rows to display
    public static int MAX_HEIGHT = 28;

    public static int[] ITEM_WEIGHTS  = { 10, 90 };
    public static int[] ITEM_WEIGHTS2 = { 10, 40, 30, 20 };

    /** the default weights that layout the first display line */
    public static int[] ITEM_WIDTHS = { 1, 3 };
    /** the default weights that layout the second display line */
    public static int[] ITEM_WIDTHS2 = { -1, 2, 1, 1 };

    /** the default weights that layout the first display line */
    public static int[] ITEM_HEIGHTS = { 2, 1 };
    /** the default weights that layout the second display line */
    public static int[] ITEM_HEIGHTS2 = { -1, 1, 1, 1 };

    /**
     * Constructor
     */
    public ExtendedItemDataListRenderer()
    {
        setName("ExtendedItemDataListRenderer");

        // set default in case lookup fails
        firstLineWeights = ITEM_WEIGHTS;
        secondLineWeights = ITEM_WEIGHTS2;
        firstLineWidths = ITEM_WIDTHS;
        secondLineWidths = ITEM_WIDTHS2;
        firstLineHeights = ITEM_HEIGHTS;
        secondLineHeights = ITEM_HEIGHTS2;

        setFirstLineWeights("labelWeights");
        setSecondLineWeights("labelWeights2");
        setFirstLineWidths("labelWidths");
        setSecondLineWidths("labelWidths2");
        setFirstLineHeights("labelHeights");
        setSecondLineHeights("labelHeights2");

        fieldCount = MAX_FIELDS;
        lineBreak  = ITEM_DESC;
        secondLineBreak = PRICE;
        lineHeight = MAX_HEIGHT;

        initialize();
    }

    /**
     * Initializes the optional components.
     */
    @Override
    protected void initOptions()
    {
        labels[IMAGE].setHorizontalAlignment(JLabel.CENTER);
        labels[ITEM_DESC].setHorizontalAlignment(JLabel.LEFT);
        labels[ITEM_ID].setHorizontalAlignment(JLabel.LEFT);
        labels[DEPARTMENT].setHorizontalAlignment(JLabel.LEFT);
        labels[PRICE].setHorizontalAlignment(JLabel.RIGHT);
    }

    /**
     * Builds each line item to be displayed.
     */
    @Override
    public void setData(Object value)
    {
        String itemID = "";
        String itemDesc = "";
        String department = "";
        String price = "";
        
        ItemImageIfc itemImage = null;
        if (value instanceof ExtendedItemDataBeanModel)
        {
            ExtendedItemDataBeanModel itemModel = (ExtendedItemDataBeanModel) value;
            ExtendedItemData item = itemModel.getItem();
            itemImage = itemModel.getItemImage();
            itemID = item.getItemID();
            itemDesc = item.getItemDescription();
            department = item.getDepartmentDescription();
            price = item.getPrice().toString();
        }

        // displays error message
        if (itemImage == null || itemImage.isImageError())
        {
            labels[IMAGE].setIcon(null);
            String label = UIUtilities.retrieveText("ItemLocationSpec", BundleConstantsIfc.POS_BUNDLE_NAME, "ImageLabel", "Item Image");
            String message = UIUtilities.retrieveText("ItemLocationSpec", BundleConstantsIfc.POS_BUNDLE_NAME, "ErrorMessageLabel", "Could Not Display");
            StringBuilder formattedMessage = new StringBuilder("<HTML><BR><b>").append(label).append("</b><BR><b>").append(message).append("</b></HTML>");
            labels[IMAGE].setText(formattedMessage.toString());
        }
        // shows no image
        else if (itemImage.isEmptyImage())
        {
            labels[IMAGE].setIcon(null);
            labels[IMAGE].setText(null);
        }

        // shows busy animated gif
        else if (itemImage.isLoadingImage())
        {
            labels[IMAGE].setIcon(getLoadingImage(getParent()));
            labels[IMAGE].setText(null);
        }

        // not loading image, show actual image
        else
        {
            labels[IMAGE].setIcon(itemImage.getImage());
            labels[IMAGE].setText(null);
        }

        labels[ITEM_ID].setText(itemID);
        labels[ITEM_DESC].setText(itemDesc);
        labels[DEPARTMENT].setText(department);
        labels[PRICE].setText(price);
    }

    /**
     * Create the loading animated gif with the parent of this renderer as its
     * observer. This renderer can't be the observer because its not permanently
     * in the container hierarchy.
     *
     * @return
     */
    protected static ImageIcon getLoadingImage(Container parent)
    {
        ImageIcon buzy = new ImageIcon(ItemListRenderer.class.getResource(ItemImageIfc.BUSY_LOADING_IMAGE));
        // About 10% of the time this method is called this renderer does not have a parent.
        // In this case, the gif image will not animate.
        if (parent != null)
        {
            buzy.setImageObserver(parent.getParent());
        }
        return buzy;
    }

    /**
     * creates the prototype cell to speed updates
     * @return TransactionSummaryIfc the prototype renderer
     */
    public Object createPrototype()
    {
        // Build objects that go into a transaction summary.
        PLUItemIfc item = DomainGateway.getFactory().getPLUItemInstance();
        item.setItemID("12345");
        item.setPrice(DomainGateway.getBaseCurrencyInstance());

        item.setDepartmentID("dept");

        item.getLocalizedDescriptions().initialize(LocaleMap.getSupportedLocales(), "test item");

        return(item);
    }

    /**
     *  Update the fields based on the properties
     */
    protected void setPropertyFields()  { }

    /**
     *  Set the properties to be used by this bean
     *  @param props the properties object
     */
    public void setProps(Properties props)
    {
        this.props = props;
    }

    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = "Class:  ItemListRenderer (Revision " + getRevisionNumber() + ")"
                + hashCode();
        // pass back result
        return (strResult);
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

}
