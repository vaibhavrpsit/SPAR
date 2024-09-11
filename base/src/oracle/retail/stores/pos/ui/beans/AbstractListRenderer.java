/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/AbstractListRenderer.java /main/29 2014/05/16 14:33:38 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/13/14 - add generics
 *    cgreene   12/11/12 - allow sale renderer to show item's promotion name
 *    cgreene   10/26/12 - Change item description to a urllabel
 *    yiqzhao   09/27/12 - add related item image for sell item screen.
 *    abhineek  09/10/12 - Fix for configuring item messages by plaf properties
 *    yiqzhao   08/31/12 - add pickup/delivery icons, date and store info in
 *                         kit components screen.
 *    rsnayak   12/12/11 - XbranchMerge rsnayak_bug-13483735 from
 *                         rgbustores_13.4x_generic_branch
 *    rsnayak   12/09/11 - Added Row highlight color
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   05/01/09 - string pooling performance aenhancements
 *    acadar    02/25/09 - override the getDefaultLocale from JComponent
 *    ddbaker   01/08/09 - Update to layout of item image screens to account
 *                         for I18N clipping issues.
 *    ddbaker   11/10/08 - Updated based on new requirements
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse
 *
 *  Revision 1.8  2004/09/14 19:39:59  mweis
 *  @scr 7012 Cleanup POS item inquiry renderers.
 *
 *  Revision 1.7  2004/07/14 19:48:00  crain
 *  @scr 6265 Regression: Column aligment or screen sizes changes between ptrunk-196 and 199
 *
 *  Revision 1.6  2004/07/09 23:12:43  bvanschyndel
 *  @scr 5268 Employee maximum character width too short.
 *
 *  Revision 1.5  2004/04/01 00:11:33  cdb
 *  @scr 4206 Corrected some header foul ups caused by Eclipse auto formatting.
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:16  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:09:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Feb 12 2003 18:52:34   DCobb
 * Added Log tag
 * Added Revision 1.4  2004/03/16 17:15:22  build
 * Added Forcing head revision
 * Added
 * Added Revision 1.3  2004/03/16 17:15:16  build
 * Added Forcing head revision
 * Added
 * Added Revision 1.2  2004/02/11 20:56:27  rhafernik
 * Added @scr 0 Log4J conversion and code cleanup
 * Added
 * Added Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 * Added updating to pvcs 360store-current
 * Added
 * Added
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.plaf.UIFactoryIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * This is the Abstract renderer that is used for lists in the POS application.
 * It lays out label objects in a {@link GridBagLayout}, using a set of provided
 * weights. Specific renderers need to extend from this class and implement the
 * abstract functions.
 * 
 * @version $Revision: /main/29 $
 */
public abstract class AbstractListRenderer<E> extends JPanel
                                           implements ListCellRenderer<E>
{
    private static final long serialVersionUID = 2968686357829523446L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/29 $";

    /** property prefixes for ui configuration */
    public static String UI_PREFIX = "List.renderer";
    public static String LABEL_PREFIX = UI_PREFIX + ".label";
    public static String FOCUS_BORDER = UI_PREFIX + ".focusBorder";
    public static String NO_FOCUS_BORDER = UI_PREFIX + ".noFocusBorder";

    /** base line width for initial measuring */
    public static int DEFAULT_WIDTH = 450;

    /** the labels that display the renderer data */
    protected JLabel[] labels = null;

    /** first line label weights */
    protected int[] firstLineWeights = null;

    /** second line label weights */
    protected int[] secondLineWeights = null;

    /** first line label weights */
    protected int[] firstLineWidths = null;

    /** second line label weights */
    protected int[] secondLineWidths = null;

    /** first line label heights */
    protected int[] firstLineHeights  = null;

    /** second line label heights */
    protected int[] secondLineHeights = null;

    /** the width of a display line */
    protected int lineWidth = DEFAULT_WIDTH;

    /** the height of a display line */
    protected int lineHeight = 0;

    /** the last label on the first display line */
    protected int lineBreak;

    /** the last label on the second display line */
    protected int secondLineBreak;

    /** the number of fields being displayed */
    protected int fieldCount = 0;

    /** The properties object which contains local specific text **/
    protected Properties props = null;

    /** the ui factory for ui components */
    protected UIFactory uiFactory = UIFactory.getInstance();

    /** Highlight Rows in List */
    protected Color altBGColor = null;
    
    /** Highlight Rows in List */
    protected Color altFGColor = null;
    
    /** Delivery image icon */
    protected ImageIcon pickupIcon = null;

    /** Pickup Image icon */
    protected ImageIcon deliveryIcon = null;
    
    /** Related Item Image icon */
    protected ImageIcon relatedItemIcon = null;

    /** Delivery icon name */
    private static final String DELEVERY_ICON_NAME = "DeliveryLogo";
    
    /** Pickup icon name */
    private static final String PICKUP_ICON_NAME = "PickupLogo";
    
    /** Related item icon name */
    private static final String RELATED_ITEM_ICON_NAME = "RelatedItemLogo";
    
    /** Message back ground color property */
    private String msgBackGroundClrProp;
    
    /** Message text color property **/
    private String msgTextClrProp;
    
    /** message font type property **/
    private String msgFontProp;    
    

    /** Pick up icon name */
    /**
     *    Default constructor.
     */
    public AbstractListRenderer()
    {
        altBGColor = UIManager.getColor("List.altRowBackground");
        altFGColor = UIManager.getColor("List.altRowForeground");
    }

    /**
     * Initializes this renderer.
     */
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        // set up the line width
        if (getWidth() != 0)
        {
            lineWidth = getWidth();
        }
        initLabels();
        initOptions();
        setPropertyFields();

        deliveryIcon = getImageIcon(DELEVERY_ICON_NAME);
        pickupIcon = getImageIcon(PICKUP_ICON_NAME);
        relatedItemIcon = getImageIcon(RELATED_ITEM_ICON_NAME);
    }

    /**
     * Initializes this renderer's components and formats the first line.
     */
    protected void initLabels()
    {
        removeAll();
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = uiFactory.getConstraints("Renderer");

        labels = new JLabel[fieldCount];

        int secondLineIndex = 0;
        int labelHeight = lineHeight;
        // create the labels
        for (int i = 0; i < fieldCount; i++)
        {
            // create label
            labels[i] = createLabel(i);
            // reset constraint
            constraints.gridwidth = 1;
            constraints.gridheight = 1;

            // set the line height if it hasn't already been set
            if (lineHeight == 0)
            {
                lineHeight = labels[i].getFont().getSize() + 4;
            }
            // if the label is on the first line, size it based on
            // weight and do the layout
            if (i <= lineBreak &&
                    (firstLineHeights == null || firstLineHeights[i] >= 0))
            {
                if (firstLineHeights != null)
                {
                    labelHeight = firstLineHeights[i] * lineHeight;
                }
                else
                {
                    labelHeight = lineHeight;
                }

                Dimension dim = UIUtilities.sizeFromWeight(lineWidth, firstLineWeights[i], labelHeight);
                if (dim != null)
                {
                    labels[i].setPreferredSize(dim);
                }

                constraints.gridy = 0;
                constraints.weightx = firstLineWeights[i] * .01;
                if (firstLineWidths != null)
                {
                    constraints.gridwidth = firstLineWidths[i];
                }
                if (firstLineHeights != null)
                {
                    constraints.gridheight = firstLineHeights[i];
                }
            }
            else
            {
                // count the number of labels being added to the second line.
                secondLineIndex = i - lineBreak - 1;
                if (i <= secondLineBreak
                        && (secondLineHeights == null || secondLineHeights[secondLineIndex] >= 0))
                {
                    Dimension dim = UIUtilities.sizeFromWeight(lineWidth, secondLineWeights[secondLineIndex], lineHeight);
                    if (dim != null)
                    {
                        labels[i].setPreferredSize(dim);
                    }

                    constraints.gridy = 1;
                    constraints.weightx = secondLineWeights[secondLineIndex] * .01;
                    if (secondLineWidths != null)
                    {
                        constraints.gridwidth = secondLineWidths[secondLineIndex];
                    }
                    if (secondLineHeights != null)
                    {
                        constraints.gridheight = secondLineHeights[secondLineIndex];
                    }
                }
            }

            // add the label
            add(labels[i], constraints);
        } // for fieldCount
    }

    /**
     * Add line breaks to the given message
     * @param message
     * @param length
     * @return HashMap contains lines count and the formatted message
     */
    protected HashMap<String, String> addLineBreaks(String message, int length)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        
        int linesCount = 1;
        String linebrkMessage = null;

        if(message != null && !message.equals(""))
        {
            StringBuilder sbMessage = new StringBuilder();
            if (message.length() > 0)
            {
                List<String> subStringList = new ArrayList<String>();

                while (message.length() > length)
                {
                    String sub = message.substring(0, length);
                    int indexOfSpace = sub.lastIndexOf(" ");
                    if(indexOfSpace == -1)
                    {
                        subStringList.add(message.substring(0, length));
                        message = message.substring(length, message.length());
                    }
                    else
                    {
                        subStringList.add(message.substring(0,indexOfSpace));
                        message = message.substring(indexOfSpace+1,message.length());
                    }
                }
                subStringList.add(message);

                for(int msgctr = 0 ; msgctr < subStringList.size(); msgctr ++ )
                {
                    sbMessage.append(subStringList.get(msgctr));
                    if(msgctr != subStringList.size())
                    {
                        sbMessage.append(" <br>");
                    }
                }
                linesCount = subStringList.size();
            }
            linebrkMessage = sbMessage.toString();
        }
        else
        {
            linebrkMessage = message;
        }
        
        map.put("MSG", linebrkMessage);
        map.put("COUNT", String.valueOf(linesCount));

        return map;
    } 
    
    /**
     * Instantiate a new label for the specified index.
     *
     * @return
     */
    protected JLabel createLabel(int index)
    {
        return uiFactory.createLabel("", "", null, LABEL_PREFIX);
    }

    /**
     * Initializes optional components.
     */
    protected void initOptions()
    {
    }

    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    @Override
    public Component getListCellRendererComponent(@SuppressWarnings("rawtypes") JList list,
                                                  Object value,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus)
    {
        // if the item is selected, use the selected colors
        if (isSelected && list.isEnabled())
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            setOpaque(true);
        }
        // otherwise, set the background to the unselected colors
        else
        {
            Color bgColor = list.getBackground();
            Color fgColor = list.getForeground();
            boolean isAltRow = (index % 2 == 1);

            setBackground(isAltRow ? altBGColor : bgColor);
            setForeground(isAltRow ? altFGColor : fgColor);
            setOpaque(isAltRow);
        }
        // set the color of the label foregrounds
        for(int i=0; i<labels.length; i++)
        {
            labels[i].setForeground(getForeground());
        }
        // draw the border if the cell has focus
        if(cellHasFocus)
        {
            setBorder(UIManager.getBorder(FOCUS_BORDER));
        }
        else
        {
            setBorder(UIManager.getBorder(NO_FOCUS_BORDER));
        }

        setData(value);
        return this;
    }

    /**
     * Set the weights that layout the first display line.
     * 
     * @param prefix the property prefix
     */
    public void setFirstLineWeights(String prefix)
    {
        // create the weight array from string property
        String weightString = UIManager.getString(prefix);
        int[] w = UIUtilities.getIntArrayFromString(weightString);

        // if the array is not null, set the first line weights
        if(w != null)
        {
            firstLineWeights = w;
        }
    }

    /**
     * Set the weights that layout the second display line.
     * 
     * @param prefix the property prefix
     */
    public void setSecondLineWeights(String prefix)
    {
        // create the weight array from string property
        String weightString = UIManager.getString(prefix);
        int[] w = UIUtilities.getIntArrayFromString(weightString);

        // if the array is not null, set the first line weights
        if(w != null)
        {
            secondLineWeights = w;
        }
    }

    /**
     * Set the weights that layout the first display line.
     * 
     * @param prefix the property prefix
     */
    public void setFirstLineWidths(String prefix)
    {
        // create the weight array from string property
        String weightString = UIManager.getString(prefix);
        int[] w = UIUtilities.getIntArrayFromString(weightString);

        // if the array is not null, set the first line weights
        if(w != null)
        {
            firstLineWidths = w;
        }
    }

    /**
     * Set the weights that layout the second display line.
     * 
     * @param prefix the property prefix
     */
    public void setSecondLineWidths(String prefix)
    {
        // create the weight array from string property
        String weightString = UIManager.getString(prefix);
        int[] w = UIUtilities.getIntArrayFromString(weightString);

        // if the array is not null, set the first line weights
        if(w != null)
        {
            secondLineWidths = w;
        }
    }

    /**
     * Set the heights that layout the first display line.
     * 
     * @param prefix the property prefix
     */
    public void setFirstLineHeights(String prefix)
    {
        // create the weight array from string property
        String heightString = UIManager.getString(prefix);
        int[] h = UIUtilities.getIntArrayFromString(heightString);

        // if the array is not null, set the first line weights
        if(h != null)
        {
            firstLineHeights = h;
        }
    }

    /**
     * Set the heights that layout the second display line.
     * 
     * @param prefix the property prefix
     */
    public void setSecondLineHeights(String prefix)
    {
        // create the weight array from string property
        String heightString = UIManager.getString(prefix);
        int[] h = UIUtilities.getIntArrayFromString(heightString);

        // if the array is not null, set the first line weights
        if(h != null)
        {
            secondLineHeights = h;
        }
    }

    /**
     * Set the properties to be used by this bean.
     * 
     * @param props the properties object
     */
    public void setProps(Properties props)
    {
        this.props = props;
        setPropertyFields();
    }

    /**
     * Retrieve an image for the specified name from the {@link UIFactoryIfc}
     * and wrap it in a {@link ImageIcon}.
     *
     * @param iconName
     * @return
     */
    protected ImageIcon getImageIcon(String iconName)
    {
        Image logoImage = uiFactory.getImage(iconName, this);
        return new ImageIcon(logoImage);
    }

    /**
     * Update the fields based on the properties. Subclasses must implement this
     * method for specific property values.
     */
    protected abstract void setPropertyFields();

    /**
     * Applies data to the visual components in the renderer. Subclasses must
     * implement this method for specific data objects.
     * 
     * @param data the data object to render
     */
    public abstract void setData(Object data);

    /**
     * Gets the locale for the user interface subsystem properties object.
     * 
     * @returns locale for the user interface subsystem
     */
    public Locale getLocale()
    {
        return (LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
    }

    /**
     * Gets the default application locale This is to override the JComponenent
     * getDefaultLocale() which returns the jvm's locale
     */
    public static Locale getDefaultLocale()
    {
        return (LocaleMap.getLocale(LocaleMap.DEFAULT));
    }

    /**
     * Gets the message background color property
     * @return msgBackGroundClrProp
     */
    public String getMsgBackGroundClrProp() 
    {
        return msgBackGroundClrProp;
    }

    /**
     * Set the message background color property
     * @param msgBackGroundClrProp
     */
    public void setMsgBackGroundClrProp(String msgBackGroundClrProp) 
    {
        this.msgBackGroundClrProp = msgBackGroundClrProp;
    }

    /**
     * Gets the message text color property
     * @return msgTextClrProp
     */
    public String getMsgTextClrProp() 
    {
        return msgTextClrProp;
    }

    /**
     * Sets the message text color property
     * @param msgTextClrProp
     */
    public void setMsgTextClrProp(String msgTextClrProp) 
    {
        this.msgTextClrProp = msgTextClrProp;
    }

    /**
     * Sets the message font property
     * @return msgFontProp
     */
    public String getMsgFontProp() 
    {
        return msgFontProp;
    }

    /**
     * Sets the message font property
     * @param msgFontProp
     */
    public void setMsgFontProp(String msgFontProp) 
    {
        this.msgFontProp = msgFontProp;
    }
    
    /**
     * Creates a prototype data object used to size the renderer. If the
     * renderer does not have a variable height (optional display lines),
     * assigning a prototype will make rendering more efficient. Objects
     * returned by this method should have all values set to their maximum
     * length.
     * 
     * @return a populated data object
     */
    public abstract Object createPrototype();

}
