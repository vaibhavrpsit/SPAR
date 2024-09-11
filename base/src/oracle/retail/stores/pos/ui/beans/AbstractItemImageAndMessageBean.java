/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AbstractItemImageAndMessageBean.java /main/1 2014/06/12 09:33:50 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* asinton     09/29/14 - Fix for the mock of item image error message
* abhinavs    06/11/14 - Miscellaneous Item search related cleanup
* abhinavs    06/11/14 - Initial Version
* abhinavs    06/11/14 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.UIManager;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.gui.utility.SwingWorker;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This abstract class provides image loading and item level message formatting to the item inquiry
 * UI beans that extend it.  
 * The class declares the abstract methods {@link #initComponents(int, String[])}, 
 * {@link #initLayout()},  {@link #configure()} and {@link #updateBean()}
 * for which extender must provide implementations.
 * 
 * @Since 14.1
 * @author abhinavs
 */
public abstract class AbstractItemImageAndMessageBean extends ValidatingBean implements ItemBeanConstantsIfc
{
    /**
     * serial version ID
     */
    private static final long serialVersionUID = 5715211440927720707L;

    /** array of labels */
    protected JLabel[] labels = null;

    /** array of display fields */
    protected JLabel[] fields = null;

    /** the bean model */
    protected ItemInfoBeanModel beanModel = null;

    public static String YES = null;

    public static String NO = null;

    /**
     * Lengths used for max lengths of the text fields. Item ID, Description,
     * Price.
     * 
     * @see ConstrainedTextField#setMaxLength(int)
     */
    protected int[] maxFieldLengths = { 14, 32, 9 };

    /**
     * Initialize the display components.
     */
    protected abstract void initComponents(int maxFields, String[] label);
    
    /**
     * Layout the components.
     */
    public abstract void initLayout();
    
    /**
     * Update bean
     */
    protected abstract void updateBean();
    
    /**
     * Adds line breaks to the given string so the tokens don't exceed the
     * specified length.
     * 
     * @param itemLevelMessage
     * @param length
     * @return
     */
    protected String addLineBreaks(String itemLevelMessage, int length)
    {
        String linebrkItemMessage = null;

        if (itemLevelMessage != null && !itemLevelMessage.equals(""))
        {
            StringBuffer lineBrkItemMessage = new StringBuffer();
            if (itemLevelMessage.length() > 0)
            {
                List<String> subStringList = new ArrayList<String>();

                while (itemLevelMessage.length() > length)
                {
                    String sub = itemLevelMessage.substring(0, length);
                    int indexOfSpace = sub.lastIndexOf(" ");
                    if (indexOfSpace == -1)
                    {
                        subStringList.add(itemLevelMessage.substring(0, length));
                        itemLevelMessage = itemLevelMessage.substring(length, itemLevelMessage.length());
                    }
                    else
                    {
                        subStringList.add(itemLevelMessage.substring(0, indexOfSpace));
                        itemLevelMessage = itemLevelMessage.substring(indexOfSpace + 1, itemLevelMessage.length());
                    }
                }
                subStringList.add(itemLevelMessage);

                for (int msgctr = 0; msgctr < subStringList.size(); msgctr++)
                {
                    lineBrkItemMessage.append(subStringList.get(msgctr));
                    if (msgctr != subStringList.size())
                    {
                        lineBrkItemMessage.append(" <br>");
                    }
                }
            }
            linebrkItemMessage = lineBrkItemMessage.toString();
        }
        else
        {
            linebrkItemMessage = itemLevelMessage;
        }

        return linebrkItemMessage;
    }

    /**
     * This method configures the child class. Any implementation
     * should override this method and sets the corresponding bean name.
     */
    public abstract void configure();

    /**
     * Display the image info into the UI.
     * 
     * @param beanModel
     */
    protected void displayImage(ItemInfoBeanModel beanModel)
    {
        if (beanModel.isImageError())
        {
            String label = UIUtilities.retrieveText("ItemLocationSpec", BundleConstantsIfc.POS_BUNDLE_NAME, "ImageLabel", "Item Image");
            String message = UIUtilities.retrieveText("ItemLocationSpec", BundleConstantsIfc.POS_BUNDLE_NAME, "ErrorMessageLabel", "Could Not Display");
            StringBuilder formattedMessage = new StringBuilder("<HTML><BR><b>").append(label).append("</b><BR><b>").append(message).append("</b></HTML>");
            setImageLabelContents(formattedMessage.toString(), null);
        }
        else if (beanModel.getImage() != null)
        {
            setImageLabelContents(null, beanModel.getImage());
        }
        else
        {
            setImageLabelContents(null, null);
        }
    }
    
    /**
     * Return the maximum width and height for displaying the item image. See UI
     * plaf properties "Dimension.itemInfoImage".
     * 
     * @return
     */
    protected Dimension getImageMaximumSize()
    {
        Dimension maxSize = UIManager.getDimension("itemInfoImage");
        return (maxSize != null) ? maxSize : new Dimension(IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);
    }

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.pos.ui.beans.ValidatingBean#getPOSBaseBeanModel()
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return (beanModel);
    }
    
    /**
     * Load the specified item image into the model by using a
     * {@link SwingWorker}
     * 
     * @param beanModel
     */
    protected void loadImage(final ItemInfoBeanModel beanModel)
    {
        if (beanModel.getImage() != null)
        {
            logger.debug("ImageIcon is already loaded for " + beanModel);
            displayImage(beanModel);
            return;
        }

        beanModel.setLoadingImage(true);
        // constructing the worker starts it
        new SwingWorker(beanModel.toString())
        {
            @Override
            public Object construct()
            {
                Image image = null;
                String location = beanModel.getImageLocation();
                Dimension imageSize = getImageMaximumSize();

                try
                {
                    // set blob
                    if (beanModel.isBlobImage())
                    {
                        image = Toolkit.getDefaultToolkit().createImage(beanModel.getImageBlob());
                    }
                    // set url
                    else if (!Util.isEmpty(location))
                    {
                        URL url = new URL(location);
                        // using ImageIO lets us catch exceptions, but Toolkit
                        // would have cached image for us
                        image = ImageIO.read(url);// Toolkit.getDefaultToolkit().getImage(url);
                    }
                    // if neither of blob, file or url the set null
                    else
                    {
                        beanModel.setEmptyImage(true);
                    }

                    if (image != null)
                    {
                        // resize image
                        if (image.getHeight(AbstractItemImageAndMessageBean.this) != imageSize.height)
                        {
                            image = image.getScaledInstance(imageSize.width, imageSize.width, Image.SCALE_FAST);
                        }
                        beanModel.setImage(new ImageIcon(image));
                    }
                }
                catch (Exception ex)
                {
                    logger.warn("Unable to load item image for " + beanModel.getItemNumber(), ex);
                    beanModel.setImageError(true);
                }
                finally
                {
                    // since we have the image now
                    beanModel.setLoadingImage(false);

                    displayImage(beanModel);

                    repaint();
                }
                return beanModel;
            }
        };

    }

    /**
     * If the item description text string is too wide to fit within the
     * available space allocated in the work panel, specific number characters
     * and "..." will be displayed instead.
     * 
     * @param args Item description text string
     * @param displayLength Specified length of description string to be
     *            displayed in the screen
     * @return {@link String} Truncated description string suffixed with "..."
     */
    protected String makeSafeStringForDisplay(String args, int displayLength)
    {
        String clipString = "...";
        args = args.trim();
        if (args.length() > displayLength)
        {
            StringBuffer buffer = new StringBuffer(args.substring(0, displayLength));
            return buffer.append(clipString).toString();
        }
        return args;
    }


    /**
     * Replaces '%' to '*' from storing data to cargo
     * 
     * @param string oldtext
     */
    protected String replaceStar(String oldtext)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        String uiWildcard = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                UI_WILDCARD_TAG, UI_WILDCARD);
        String dbWildcard = utility.retrieveText("Common", BundleConstantsIfc.INQUIRY_OPTIONS_BUNDLE_NAME,
                DB_WILDCARD_TAG, DB_WILDCARD);

        return Util.replaceStar(oldtext, uiWildcard.charAt(0), dbWildcard.charAt(0));
    }

    /**
     * Sets the information to be shown by this bean.
     * 
     * @param model the model to be shown. The runtime type should be
     *            ItemInfoBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set ItemInfoBean " + "model to null");
        }
        if (model instanceof ItemInfoBeanModel)
        {
            beanModel = (ItemInfoBeanModel)model;
            updateBean();
        }
    }
    
    /**
     * Put the specified contents into the correct position of the image label.
     * 
     * @param text
     * @param icon
     */
    protected void setImageLabelContents(String text, ImageIcon icon)
    {
        if (beanModel.isItemSizeRequired() && beanModel.isUsePlanogramID())
        {
            fields[IMAGE].setText(text);
            fields[IMAGE].setIcon(icon);
        }
        else if (beanModel.isItemSizeRequired() || beanModel.isUsePlanogramID())
        {
            fields[IMAGE - 1].setText(text);
            fields[IMAGE - 1].setIcon(icon);
        }
        else
        {
            fields[IMAGE - 2].setText(text);
            fields[IMAGE - 2].setIcon(icon);
        }
    }

    /**
     * Updates property-based fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        if (beanModel != null)
        {
            if (beanModel.isItemSizeRequired())
            {
                if (beanModel.isUsePlanogramID())
                {
                    for (int i = 0; i < labelText.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTags[i], labelText[i]));
                    }
                }
                else
                {
                    for (int i = 0; i < labelTextSizeNoPlanogram.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTagsSizeNoPlanogram[i], labelTextSizeNoPlanogram[i]));
                    }
                }
            }
            else
            {
                if (beanModel.isUsePlanogramID())
                {
                    for (int i = 0; i < labelTextNoSizePlanogram.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTagsNoSizePlanogram[i], labelTextNoSizePlanogram[i]));
                    }
                }
                else
                {
                    for (int i = 0; i < labelTextNoSizeNoPlanogram.length; i++)
                    {
                        labels[i].setText(retrieveText(labelTagsNoSizeNoPlanogram[i], labelTextNoSizeNoPlanogram[i]));
                    }
                }
            }
        }

        YES = retrieveText("YesItemLabel", "Yes");
        NO = retrieveText("NoItemLabel", "No");
    }
}