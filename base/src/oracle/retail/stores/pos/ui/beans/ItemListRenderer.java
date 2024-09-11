/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemListRenderer.java /main/29 2014/01/09 16:23:22 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/29/14 - Fix for the mock of item image error message
 *    mjwallac  01/09/14 - fix null dereferences
 *    abhinavs  11/01/13 - Fix to sort meta tag search result on the basis of
 *                         department name
 *    vbongu    03/12/13 - Align fields with their headers
 *    cgreene   12/11/12 - allow sale renderer to show item's promotion name
 *    cgreene   11/27/12 - enhancement for displaying item images in sale
 *                         screen table
 *    hyin      09/05/12 - Show item image on item list screen.
 *    hyin      08/31/12 - meta tag search POS UI work.
 *    mkutiana  07/20/11 - Fix for randomly occuring NPE
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    cgreene   09/03/09 - XbranchMerge cgreene_bug8394467-timer from
 *                         rgbustores_13.1x_branch
 *    cgreene   09/03/09 - refactored busy image constant to ItemImageIfc
 *    cgreene   04/17/09 - fix item image painting in lists
 *    cgreene   03/19/09 - refactoring changes
 *    ddbaker   01/08/09 - Update to layout of item image screens to account
 *                         for I18N clipping issues.
 *    ddbaker   01/06/09 - Removed duplicate additions of labels to renderer.
 *                         Labels are correctly added by
 *                         AbstractListRenderer.initLabels() only.
 *    ddbaker   01/05/09 - Updates to support localization of Item Images
 *    ddbaker   12/31/08 - Work to repair SelectItem screen.
 *    atirkey   12/02/08 - Item Image CR
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         6/12/2007 8:48:21 PM   Anda D. Cadar   SCR
 *         27207: Receipt changes -  proper alignment for amounts
 *    4    360Commerce 1.3         5/8/2007 11:32:26 AM   Anda D. Cadar
 *         currency changes for I18N
 *    3    360Commerce 1.2         3/31/2005 4:28:32 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:28 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:38 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.1   Jan 26 2004 12:05:04   kll
 * attach department description to the item in question
 * Resolution for 3120: Item Inquiry is looking at the incorrect Column in the Tables for the Department
 *
 *    Rev 1.0   Aug 29 2003 16:10:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Sep 06 2002 17:25:24   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:17:52   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:53:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:55:42   msg
 * Initial revision.
 *
 *    Rev 1.2   Feb 05 2002 16:43:52   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.1   30 Jan 2002 17:07:06   baa
 * replace getPrice() method for  getSellingPrice()
 * Resolution for POS SCR-978: Kit Price doesn't display on the Item List screen
 *
 *    Rev 1.0   29 Jan 2002 07:30:58   baa
 * Initial revision.
 * Resolution for POS SCR-921: Item inquiry - the 'Item List' screen, arrow keys do not work
 *
 *    Rev 1.0   28 Jan 2002 10:49:20   baa
 * Initial revision.
 * Resolution for POS SCR-920: Inventory inquiry - the 'Item Inventory' screen is incorrect
 *
 *    Rev 1.1   Jan 19 2002 10:30:40   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   05 Nov 2001 17:43:56   baa
 * Initial revision.
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   19 Oct 2001 15:34:08   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Container;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import oracle.retail.stores.common.item.ItemSearchResult;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;

/**
 * This is the renderer for the SaleReturn Table. It displays
 * SaleReturnLineItems and makes them look like it is a table.
 * <P>
 * $Revision: /main/29 $
 */
public class ItemListRenderer extends AbstractListRenderer
{
    private static final long serialVersionUID = -9053211115630876736L;
    
    protected static final Logger logger = Logger.getLogger(ItemListRenderer.class); 

    /**
        revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/29 $";

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
    public ItemListRenderer()
    {
        setName("ItemListRenderer");

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
        if (value instanceof PLUItemIfc)
        {
            PLUItemIfc item = (PLUItemIfc) value;
            itemImage = item.getItemImage();
            
            itemID = item.getItemID();
            itemDesc = item.getDescription(getLocale());
            department = item.getDepartment().getDescription(getLocale());
            price = item.getSellingPrice().toFormattedString();
        }
        else if (value instanceof ItemSearchResult)
        {
            ItemSearchResult item = (ItemSearchResult) value;
            itemID = item.getItemID();
            itemDesc = item.getItemShortDescription();
            department = item.getDepartmentDescription();
            price = DomainGateway.getBaseCurrencyInstance(item.getPrice()).toFormattedString();

            // create a new item image from search results
            itemImage = DomainGateway.getFactory().getItemImageInstance();
            byte[] imageData = item.getImageBlob();
            itemImage.setImageBlob(imageData);
            String imageLoc = item.getImageLocation();
            itemImage.setImageLocation(imageLoc);
        }
        if (itemImage != null && itemImage.getImage() == null)
        {
            itemImage.scaleImage(ListBean.ICON_WIDTH, ListBean.ICON_WIDTH);
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

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        ItemListRenderer bean = new ItemListRenderer();
        bean.setData(bean.createPrototype());
        UIUtilities.doBeanTest(bean);
    }

}
