/* ===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/RecommendedItemsBean.java /main/4 2014/07/10 14:02:15 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/08/14 - Added support for recommended items navigation
 *                         configuration.
 *    jswan     07/09/14 - Modified to prevent more than the maximum item
 *                         description length from showing on the buttons.
 *    jswan     06/27/14 - Fixed focus issue with the prompt and response
 *                         panel.
 *    jswan     06/16/14 - Added to support display of extended data
 *                         recommended items from the Sale Item screen.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import oracle.retail.stores.common.item.ExtendedItemData;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.stock.ItemImageIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.gui.utility.SwingWorker;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.log4j.Logger;

/**
 * Bean to represent recommended items on the sell item screen.
 * 
 * @since 14.1
 */
public class RecommendedItemsBean extends JPanel
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 7738231643589063869L;
    
    /** The logger to which log messages will be sent. */
    private static Logger logger = Logger.getLogger(RecommendedItemsBean.class);

    /**
     * Letter for initiating the Display Item.
     */
    public static final String DISPLAY_RECOMMENDED_ITEM_LETTER = "DisplayRecommendedItem";

    /** Defines the resource key for the forward image. */
    protected static final String BUTTON_ICON_FORWARD       = "buttonIconForward";
    /** Defines the resource key for the backward image. */
    protected static final String BUTTON_ICON_BACK          = "buttonIconBack";
    /** Defines the resource key for the expand image. */
    protected static final String BUTTON_ICON_EXPAND        = "buttonIconExpand";
    /** Defines the resource key for the collapse image. */
    protected static final String BUTTON_ICON_COLLAPSE      = "buttonIconCollapse";
    /** Defines the resource key for the not available image. */
    protected static final String BUTTON_ICON_NOT_AVAILABLE = "buttonIconNotAvailable";
    
    /** The prefix for the size resource keys. */
    protected static final String RECOMMENDED_ITEM_BEAN     = "RecommendedItemBean";
    /** Defines the resource key for the panel width. */
    protected static final String PANE_WIDTH                = ".pane.width";
    /** Defines the resource key for the panel height. */
    protected static final String PANE_HEIGHT               = ".pane.height";
    /** Defines the resource key for the button insets. */
    protected static final String INSET                     = ".inset";
    /** Defines the resource key for the average button width. */
    protected static final String AVERAGE_BUTTON_WIDTH      = ".average.button.width";
    /** Defines the resource key for the maximum button width. */
    protected static final String MAX_ITEM_BUTTONS          = ".max.item.buttons";
    /** Defines the resource key for the number of columns in the panel. */
    protected static final String NUM_GRIDLAYOUT_COLUMNS    = ".num.gridlayout.columns";
    /** Defines the resource key for the number of columns in the panel. */
    protected static final String RESIZING_BUTTON_WIDTH     = ".resizing.button.width";
    /** Defines the maximum number of characters allowed in the description. This
     * is pretty inexact because different characters have different widths in different fonts. */
    protected static final String ITEM_DESCRIPTION_WIDTH    = ".item.description.width";

    /** The prefix of the resources key for the resizing button text. */
    protected static final String RECOMMENDED_ITEMS_BEAN    = "RecommendedItemsBean";
    /** The key for the resizing button text. */
    protected static final String RECOMMENDED_ITEMS_TAG     = "RecommendedItems"; 
    /** The default resizing button text. */
    protected static final String RECOMMENDED_ITEMS_DEFAULT = "Recommended Items";

    /** The inset value. */
    protected int buttonInset           = 0;
    /** The panel width value. */
    protected int panelWidth            = 0;
    /** The panel height value. */
    protected int panelHeight           = 0;
    /** The maximum button width value. */
    protected int maxButtonWidth        = 0;
    /** The maximum button width value. */
    protected int averageButtonWidth    = 0;
    /** The maximum item buttons value. */
    protected int maxItemButtons        = 0;
    /** The number of grid layout columns. */
    protected int numGridlayoutColumns  = 0;
    /** The maximum button width value. */
    protected int resizingButtonWidth   = 0;
    /** The maximum description width. */
    protected int itemDescriptionWidth  = 0;

    /** The panel that holds the holds the recommended item buttons */
    protected JPanel itemsPanel         = null;
    
    /** Helps to determine which items to show. */
    protected int visibleStart          = 0;

    /** Indicates if the recommended items panel is expanded or collapsed. */ 
    protected boolean isExanded         = true;
    
    /** Indicates if the expand button has been initialized. */ 
    protected boolean isResizingButtonListenerAdded = false;
    
    /** Expanded size of the items panel */
    protected Dimension expandedPanelSize   = null;
    
    /** Expanded size of the items panel */
    protected Dimension collapsedPanelSize  = null;
    
    /** The image to apply the resizing button when it is expanded. */
    protected ImageIcon collapseImage       = null;
    
    /** The image to apply to the resizing button when it is collapsed. */
    protected ImageIcon expandImage         = null;
    
    /** The image to apply to an item button when no image is available. */
    protected ImageIcon notAvailableImage   = null;

    /** Button object that collapses and expands the recommended items panel. */
    protected JButton resizingButton        = null;

    /** Button object for sliding the recommended items to the right. */
    protected JButton backButton            = null;
    
    /** Button object for sliding the recommended items to the left. */
    protected JButton forwardButton         = null;

    /** List of {@link ExtendedItemData} items. */
    protected List<ExtendedItemData> extendedItemDataList   = null;

    /** Thread safe hash map that holds the map of already retrieve item images. */
    protected ConcurrentHashMap<String, ImageIcon> imageMap = null;
    
    /** Thread safe hash map that associates a button with its item ID. */
    protected ConcurrentHashMap<String, String> itemIDMap   = null;
    
    /** The item ID of the most recently selected recommended item. */
    protected String selectedRecommendedItemID              = null;

    /** Scroll direction obtained via Parameters */
    protected boolean naturalScrollDirection                = true;
    /**
     * The default public constructor.
     */
    public RecommendedItemsBean()
    {
        // Set up the the data members
        imageMap             = new ConcurrentHashMap<String, ImageIcon>();
        itemIDMap            = new ConcurrentHashMap<String, String>();
        buttonInset          = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + INSET));
        panelWidth           = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + PANE_WIDTH));
        panelHeight          = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + PANE_HEIGHT));
        averageButtonWidth   = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + AVERAGE_BUTTON_WIDTH));
        maxItemButtons       = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + MAX_ITEM_BUTTONS));
        numGridlayoutColumns = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + NUM_GRIDLAYOUT_COLUMNS));
        resizingButtonWidth  = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + RESIZING_BUTTON_WIDTH));
        itemDescriptionWidth = Integer.parseInt(UIManager.getString(RECOMMENDED_ITEM_BEAN + ITEM_DESCRIPTION_WIDTH));
        
        // Initialize the panel
        initialize();
    }

    /**
     * This method initializes the bean.
     */
    protected void initialize()
    {
        setFocusable(false);
        itemsPanel = new JPanel();
        expandedPanelSize = new Dimension(panelWidth, panelHeight);
        collapsedPanelSize = new Dimension(panelWidth, 0);
        itemsPanel.setSize(expandedPanelSize);
        itemsPanel.setPreferredSize(expandedPanelSize);
        itemsPanel.setMaximumSize(expandedPanelSize);
        itemsPanel.setMinimumSize(expandedPanelSize);
        itemsPanel.setBorder(null);
        // Initialize with 1 row and configurable number of columns.
        itemsPanel.setLayout(new GridLayout(1, numGridlayoutColumns));
        setLayout(new BorderLayout());
        add(itemsPanel, BorderLayout.CENTER);
        
        intializeResizingButtonPanel();
    }

    /**
     * Initializes the resizing button.
     */
    protected void intializeResizingButtonPanel()
    {
        // Initialize the JButton 
        UtilityManagerIfc utility = (UtilityManagerIfc)Gateway.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String buttonText = utility.retrieveText(RECOMMENDED_ITEMS_BEAN,
                BundleConstantsIfc.POS_BUNDLE_NAME, RECOMMENDED_ITEMS_TAG, RECOMMENDED_ITEMS_DEFAULT, locale);
        resizingButton      = new JButton();
        resizingButton.setText("<html><b>" + buttonText + "</b>");
        resizingButton.setBorder(null);
        resizingButton.setHorizontalAlignment(SwingConstants.LEFT);
        resizingButton.setFocusable(false);

        // Initialize the images
        collapseImage       = UIUtilities.getImageIcon(UIUtilities.ICON_SET_GRAY, BUTTON_ICON_COLLAPSE, resizingButton);
        collapseImage.setImage(collapseImage.getImage().
                getScaledInstance(resizingButtonWidth, resizingButtonWidth, Image.SCALE_SMOOTH));
        
        expandImage         = UIUtilities.getImageIcon(UIUtilities.ICON_SET_GRAY, BUTTON_ICON_EXPAND, resizingButton);
        expandImage.setImage(expandImage.getImage().
                getScaledInstance(resizingButtonWidth, resizingButtonWidth, Image.SCALE_SMOOTH));
        
        notAvailableImage   = UIUtilities.getImageIcon(UIUtilities.ICON_SET_GRAY, BUTTON_ICON_NOT_AVAILABLE, resizingButton);
        notAvailableImage.setImage(notAvailableImage.getImage().
                getScaledInstance(averageButtonWidth, averageButtonWidth, Image.SCALE_SMOOTH));
        
        // Set the collapse image on the button to start.
        resizingButton.setIcon(collapseImage);

        // Add the button to the bean.
        add(resizingButton, BorderLayout.NORTH);
    }

    /**
     * This method configures the navigation and recommended item buttons. 
     */
    protected void initButtons()
    {
        // This anonymous inner class is the action listener from all the buttons.
        ActionListener actionListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                // Determine which button was pressed.
                Object button = ae.getSource();
                if(button == backButton)
                {
                    // Move the recommended items
                    RecommendedItemsBean.this.setVisibleStart(true);
                    initButtons();
                }
                else if(button == forwardButton)
                {
                    // Move the recommended items
                    RecommendedItemsBean.this.setVisibleStart(false);
                    initButtons();
                }
                else if(button == resizingButton)
                {
                    if (isExanded)
                    {
                        itemsPanel.setSize(collapsedPanelSize);
                        itemsPanel.setPreferredSize(collapsedPanelSize);
                        itemsPanel.setMaximumSize(collapsedPanelSize);
                        itemsPanel.setMinimumSize(collapsedPanelSize);
                        resizingButton.setIcon(expandImage);
                        isExanded = false;
                    }
                    else
                    {
                        itemsPanel.setSize(expandedPanelSize);
                        itemsPanel.setPreferredSize(expandedPanelSize);
                        itemsPanel.setMaximumSize(expandedPanelSize);
                        itemsPanel.setMinimumSize(expandedPanelSize);
                        resizingButton.setIcon(collapseImage);
                        isExanded = true;
                    }
                    initButtons();
                }
                else
                {
                    // Get the associated item ID, set it on the bean, and mail a letter
                    // to the tour indication that a recommended item button has been pressed.
                    String buttonText = ((JButton)button).getText();
                    selectedRecommendedItemID = itemIDMap.get(buttonText);
                    UISubsystem.getInstance().mail(new Letter(DISPLAY_RECOMMENDED_ITEM_LETTER), true);
                }
            }
        };
        
        if (!isResizingButtonListenerAdded)
        {
            resizingButton.addActionListener(actionListener);
            isResizingButtonListenerAdded = true;
        }
        
        // Initialize the navigation buttons.
        if(forwardButton == null)
        {
            forwardButton = new JButton();
            ImageIcon image = UIUtilities.getImageIcon(UIUtilities.ICON_SET_GRAY, BUTTON_ICON_FORWARD, forwardButton);
            forwardButton.setIcon(image);
            image = UIUtilities.getImageIcon(UIUtilities.ICON_SET_WHITE, BUTTON_ICON_FORWARD, forwardButton);
            forwardButton.setDisabledIcon(image);
            intializeButton(actionListener, forwardButton);
        }
        if(backButton == null)
        {
            backButton = new JButton();
            ImageIcon image = UIUtilities.getImageIcon(UIUtilities.ICON_SET_GRAY, BUTTON_ICON_BACK, backButton);
            backButton.setIcon(image);
            image = UIUtilities.getImageIcon(UIUtilities.ICON_SET_WHITE, BUTTON_ICON_BACK, backButton);
            backButton.setDisabledIcon(image);
            intializeButton(actionListener, backButton);
        }
        
        // Remove all the buttons
        itemsPanel.removeAll();
        removeAll();
        
        // Add the left navigation button.
        itemsPanel.add(forwardButton);
        
        // Add the recommended item buttons.
        if(extendedItemDataList != null && extendedItemDataList.size() > 0)
        {
            // Add the configured number of buttons.
            JButton button = null;
            int count = extendedItemDataList.size();
            if (extendedItemDataList.size() > maxItemButtons)
            {
                count = maxItemButtons;
            }
            
            // Iterate through extendItemDataList, and starting at the visiableStart index,
            // add as many buttons from the list as possilbe
            final int listSize = extendedItemDataList.size();
            ExtendedItemData eid = null;
            String buttonText = null;
            String imageLocation = null;
            for(int i = 0; i < count && i < listSize; i++)
            {
                eid = extendedItemDataList.get((listSize + (visibleStart + i)) % listSize);
                
                buttonText = "<html><P ALIGN=Center>" + eid.getItemID() + ":" + "<br><P ALIGN=Center>" + 
                        getMaximumText(eid.getItemDescription()) + "</html>";
                itemIDMap.put(buttonText, eid.getItemID());
                button = new JButton(buttonText);
                intializeButton(actionListener, button);
                imageLocation = eid.getImageLocations().get(0);
                loadImage(imageLocation, button);
                itemsPanel.add(button);
            }
            forwardButton.setEnabled(true);
            backButton.setEnabled(true);
        }
        
        // Finally, add the back navigation button.
        itemsPanel.add(backButton);
        add(itemsPanel, BorderLayout.CENTER);
        add(resizingButton, BorderLayout.NORTH);

        // Refresh the naturalScrollDirection value from Parameters
        ParameterManagerIfc pm = (ParameterManagerIfc)Gateway.getDispatcher().getManager(ParameterManagerIfc.TYPE);
        try
        {
            this.naturalScrollDirection = pm.getBooleanValue("RecommendedItemsNaturalScrollDirection");
        }
        catch(ParameterException pe)
        {
            logger.warn("Unable to get RecommendedItemsNaturalScrollDirection parameter value, using 'true'.");
            this.naturalScrollDirection = true;
        }
        // Force the screen to redisplay.
        invalidate();
        revalidate();
    }

    /**
     * Moves the recommended items buttons either to the left or the right
     * depending upon the button selected and application property 
     * RecommendedItemsNaturalScrollDirection.
     * @param back true if the back (right) button was selected.
     */
    protected void setVisibleStart(boolean back)
    {
        final int size = extendedItemDataList.size();
        if((naturalScrollDirection && back) || (!naturalScrollDirection && !back))
        {
            this.visibleStart = (size + (visibleStart - 1)) % size;
        }
        else
        {
            this.visibleStart = (size + (visibleStart + 1)) % size;
        }
    }

    /**
     * Get maximum text.
     * @param text
     * @return shorten text, if necessary
     */
    protected String getMaximumText(String text)
    {
        if (text.length() < itemDescriptionWidth)
        {
            return text;
        }
        
        String newText = text.substring(0, itemDescriptionWidth - 3) + "...";
        return newText;
    }

    /**
     * Initialize the button settings for each navigation and
     * recommended item button.
     * @param actionListener
     * @param button
     */
    protected void intializeButton(ActionListener actionListener,
            JButton button)
    {
        button.setHorizontalAlignment(JButton.CENTER);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalAlignment(JButton.TOP);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setVerticalAlignment(JButton.CENTER);
        button.setBackground(itemsPanel.getBackground());
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setFocusable(false);
        if(button.getActionListeners().length == 0)
        {
            button.addActionListener(actionListener);
        }
    }

    /**
     * Load the specified item image into the model by using a {@link SwingWorker}.
     * The SwingWorker class creates a thread on which the image will be retrieved. 
     * @param imageLocation
     * @param button
     */
    protected void loadImage(final String imageLocation, final JButton button)
    {
        // If the image location is not available, set the not available image
        // on the button, and return.
        if (Util.isEmpty(imageLocation))
        {
            setImageNotAvailable(button);
            return;
        }
        
        // If the image has already been retrieved, set it on the button and return.
        if (imageMap.containsKey(button.getText()))
        {
            button.setIcon(imageMap.get(button.getText()));
            return;
        }
        
        // Display the loading animated GIF on the button.
        ImageIcon buzy = new ImageIcon(RecommendedItemsBean.class.getResource(ItemImageIfc.BUSY_LOADING_IMAGE));
        buzy.setImageObserver(itemsPanel);
        button.setIcon(buzy);
        
        // Execute this implementation of the SwingWoker class.
        new SwingWorker(button.getText())
        {
            @Override
            public Object construct()
            {
                // Get the image from the server called out by the URL, if the
                // retrieval succeeds set it on the button.  If not set the not available image
                // on the button.
                Image image = null;
                try
                {
                    URL url = new URL(imageLocation);
                    image = ImageIO.read(url);
                    
                    if (image != null)
                    {
                        image = image.getScaledInstance(averageButtonWidth, averageButtonWidth, Image.SCALE_FAST);
                        ImageIcon icon = new ImageIcon(image);
                        imageMap.put(button.getText(), icon);
                        button.setIcon(icon);
                    }
                    else
                    {
                        setImageNotAvailable(button);                    
                    }
                }
                catch (Exception ex)
                {
                    setImageNotAvailable(button);
                }
                finally
                {
                    itemsPanel.repaint();
                }
                return button;
            }
        };
    }

    /**
     * Set the not available image on the button.
     * @param button
     */
    protected void setImageNotAvailable(JButton button)
    {
        try
        {
            button.setIcon(notAvailableImage);
            imageMap.put(button.getText(), notAvailableImage);
        }
        catch(Exception e)
        {
            logger.error("Error applying the local generic image to a recommended item button when no actual image is available.", e);
        }
    }

    /**
     * Gets the <code>extendedItemDataList</code> value.
     * @return the extendedItemDataList
     */
    public List<ExtendedItemData> getExtendedItemDataList()
    {
        return extendedItemDataList;
    }

    /**
     * Sets the <code>extendedItemDataList</code> value.
     * @param extendedItemDataList the extendedItemDataList to set
     */
    public void setExtendedItemDataList(List<ExtendedItemData> extendedItemDataList)
    {
        this.extendedItemDataList = extendedItemDataList;
    }

    /**
     * The information displayed in this bean is associated with a particular set
     * of line items, and therefore with a transaction.  This method must be called
     * at the end of each transaction to clear these variables.
     */
    public void resetSessionVariables()
    {
        itemIDMap = new ConcurrentHashMap<String, String>();
        imageMap  = new ConcurrentHashMap<String, ImageIcon>();
        selectedRecommendedItemID = null;
        visibleStart = 0;
        itemsPanel.setSize(expandedPanelSize);
        itemsPanel.setPreferredSize(expandedPanelSize);
        itemsPanel.setMaximumSize(expandedPanelSize);
        itemsPanel.setMinimumSize(expandedPanelSize);
        resizingButton.setIcon(collapseImage);
        isExanded = true;
    }

    /**
     * Gets the <code>selectedRecommendedItemID</code> value.
     * @return the selectedRecommendedItemID
     */
    public String getSelectedRecommendedItemID()
    {
        return selectedRecommendedItemID;
    }
}
