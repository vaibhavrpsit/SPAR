/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/NavigationButtonBean.java /main/42 2014/05/20 12:14:37 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED (MM/DD/YY)
*    cgreen 05/20/14 - refactor list model sorting
*    abonda 09/04/13 - initialize collections
*    jswan  06/27/13 - Modified to fix issues with ordering and location of the
*                      visible buttons on a popup menu.
*    cgreen 06/04/13 - implement manager override as dialogs
*    cgreen 04/30/13 - correct button sorting so they can also be arranged
*                      going upwards
*    cgreen 01/25/13 - set the menu as displayed when a child knows its parent
*                      button
*    cgreen 01/25/13 - check for possible null menu parent
*    cgreen 01/24/13 - set button name to resource id
*    cgreen 12/14/12 - Button image and plaf loading updates
*    cgreen 12/05/12 - correctly over-sized buttons when shrinking UI
*    mjwall 10/22/12 - Button ui changes
*    cgreen 09/20/12 - Popupmenu implmentation round 2
*    cgreen 09/10/12 - Popup menu implementation
*    cgreen 08/11/11 - UI tweaks for global button size and prompt area
*                      alignment
*    npoola 10/11/10 - added the map to maintain the state of the button based
*                      on the *uiconfig.xml
*    cgreen 07/14/10 - fix key handling by forwarding from frame to rootpane
*    cgreen 07/02/10 - switch to stringbuilder
*    cgreen 01/05/10 - implement parameter enabled bean spec
*    abonda 01/03/10 - update header date
*    cgreen 12/07/09 - add maxButtons and ability to parse ctrl keyword for
*                      buttons
*    cgreen 09/17/09 - refactored methods in order to subclass with
*                      dialogbutton bean
*    cgreen 04/15/09 - only clear buttons if this is a LocalNavigationPanelSpec
*    cgreen 04/15/09 - clear and disable buttons when this bean is only cached
*                      once and shared with cases where model may be null
*    nkgaut 11/14/08 - Reverted change of Max Horizontal buttons and added
*                      setter method of MAX_HORIZONTAL variable to accomodate
*                      six buttons for browser.
*    nkgaut 09/29/08 - Changed MAX_HORIZONTAL from 5 to 6 to accomodate six
*                      buttons for browserfoundation
* ===========================================================================
     $Log:
      4    360Commerce 1.3         1/25/2006 4:11:32 PM   Brett J. Larsen merge
            7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
      3    360Commerce 1.2         3/31/2005 4:29:08 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:40 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:44 PM  Robert Pearse
     $:
      4    .v700     1.2.1.0     11/15/2005 16:05:47    Jason L. DeLeau 4214:
           Make orientation configurable
      3    360Commerce1.2         3/31/2005 15:29:08     Robert Pearse
      2    360Commerce1.1         3/10/2005 10:23:40     Robert Pearse
      1    360Commerce1.0         2/11/2005 12:12:44     Robert Pearse
     $
     Revision 1.3  2004/03/16 17:15:18  build
     Forcing head revision

     Revision 1.2  2004/02/11 20:56:27  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Dec 10 2003 15:09:18   nrao
 * Modified NavigationButtonBean so it allows 3 or more screens of buttons when required.
 *
 *    Rev 1.0   Aug 29 2003 16:11:14   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   22 Jul 2003 22:18:32   baa
 * create EYSButton instead of JButton
 *
 *    Rev 1.2   Aug 15 2002 17:55:52   baa
 * apply foundation  updates to UISubsystem
 *
 * Resolution for POS SCR-1769: 5.2 UI defects resulting from change to java 1.4
 *
 *    Rev 1.1   Aug 14 2002 18:18:02   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:34   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:18   msg
 * Initial revision.
 *
 *    Rev 1.7   Feb 23 2002 15:04:18   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.6   11 Feb 2002 16:10:52   pdd
 * Fixed changedUpdate() bug.
 * Resolution for POS SCR-1017: Mod Effect for ChecksAccepted incorrect
 *
 *    Rev 1.5   11 Feb 2002 09:56:24   pdd
 * Added deactivate to reset savedStates.
 * Resolution for POS SCR-1017: Mod Effect for ChecksAccepted incorrect
 *
 *    Rev 1.4   06 Feb 2002 20:47:36   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.3   01 Feb 2002 14:29:18   KAC
 * Added code from Dave Teagle for addButtonListener,
 * removeButtonListener.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.foundation.manager.gui.ButtonBarBeanIfc;
import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.foundation.manager.gui.UIBeanIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ui.jfc.DefaultMailAction;
import oracle.retail.stores.foundation.manager.ui.jfc.MenuCancelledLetter;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ButtonListener;

import org.apache.log4j.Logger;

/**
 * This class represents a collection of push buttons and associated key stroke
 * equivalents.
 *
 * @version $Revision: /main/42 $
 */
public class NavigationButtonBean extends BaseBeanAdapter
    implements SwingConstants, DocumentListener, ButtonBarBeanIfc
{
    private static final long serialVersionUID = -2719605005950105529L;

    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(NavigationButtonBean.class);

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/42 $";

    /** Property prefixes for configuring this widget. */
    public static final String PREFIX_BUTTONBAR = "ButtonBar";
    public static final String PREFIX_VERTICALBUTTON = "VerticalButton";
    public static final String PREFIX_HORIZONTALBUTTON = "HorizontalButton";

    /** Initial values for the number of buttons that will fit per orientation. */
    public static final int MAX_HORIZONTAL        = 6;
    public static final int MAX_VERTICAL          = 8;

    public static final String ACTION_NAME_DELIMITER = ",";
    public static final String ACTION_COMMAND_MORE = "More";

    /** @deprecated as of 14.1. Use {@link UIUtilities#SUFFIX_ICON_SET} instead. */
    public static final String SUFFIX_ICON_SET = ".iconSet";
    /** @deprecated as of 14.1. Use {@link UIUtilities#SUFFIX_ICON_SET} instead. */
    public static final String ICON_SET_WHITE = "white";
    /** @deprecated as of 14.1. Use {@link UIUtilities#SUFFIX_ICON_SET} instead. */
    public static final String ICON_SET_GRAY = "gray";
    public static final String BUTTON_ICON_MORE = "buttonIconMore";

    /** the action used to trigger the buttons to switch to the next bar. */
    private final MoreAction moreAction = new MoreAction();

    protected POSBaseBeanModel          baseModel   = null;
    protected NavigationButtonBeanModel buttonModel = null;

    /** Orientation of the button bar defaults to VERTICAL */
    protected int orientation = VERTICAL;
    protected String propertyPrefix = PREFIX_BUTTONBAR;
    protected String buttonPrefix;

    /** actions for the buttons */
    protected UIAction[][] actions;
    protected boolean[][] savedStates;

    /** buttons for the task bar */
    protected EYSButton[][] buttons = new EYSButton[0][];
    /** Spacers for blank areas when showing a menu. */
    protected JLabel[] spacers;

    /** Size of the button index */
    protected int buttonIndexSize;

    /** current active button index */
    protected int currentButtonIndex;

    /** maximum number of buttons that can fit on the bean at once. */
    protected int maxButtons = MAX_VERTICAL;

    /** constraints */
    protected GridBagConstraints constraints;

    /** Button States */
    protected HashMap<String, Boolean> buttonStates = new HashMap<String, Boolean>(1);

    /** Popup menu member. The child menu bean being displayed. */
    protected NavigationButtonBean menuBean;
    /** Popup menu member. The last button pressed. Its identified as the
     * "parent" of this menu. */
    protected EYSButton menuParent;
    /** Popup menu member. Switches a flag on the button when pressed. */
    protected MenuActionListener menuActionListener = new MenuActionListener();

    /**
     * Default constructor.
     */
    public NavigationButtonBean()
    {
        super(new GridBagLayout());
        buttonPrefix = PREFIX_VERTICALBUTTON;
        // set the panel's display aspects
        uiFactory.configureUIComponent(this, propertyPrefix);
    }

    /**
     * Creates an empty NavigationButtonBean.
     *
     * @param actions two dimensional list of buttons
     */
    public NavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

    /**
     * Initializes the button bar
     *
     * @param actions the actions to associate with the bar
     */
    protected void initialize(UIAction[][] actions)
    {
        // Remove all previous buttons
        removeAll();

        // set up the constraints object used to layout buttons
        constraints = uiFactory.getConstraints(propertyPrefix);
        this.actions = actions;
        buttons = new EYSButton[actions.length][actions[0].length];
        spacers = new JLabel[actions[0].length];

        // create buttons and add them
        setUIActions(this.actions);

        //set the 1st button bar to visible
        setButtonsVisible(0, true);
    }

    /**
     * Sets the model for the current settings of this bean.
     *
     * @param model the model for the current values of this bean
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("NavigationButtonBean model must not be set to null");
        }
        if (getName() == null) // this has no name and is being refreshed, hide popup
        {
            hidePopupMenu();
        }
        if (model instanceof POSBaseBeanModel)
        {
            baseModel = (POSBaseBeanModel)model;
        }
        if (baseModel.getLocalButtonBeanModel() != null)
        {
            buttonModel = baseModel.getLocalButtonBeanModel();

            if (buttonModel.getNewButtons() != null)
            {
                configureButtons(buttonModel.getNewButtons());
            }
            if (buttonModel.getModifyButtons() != null)
            {
                if (actions == null)
                {
                    configureButtons(buttonModel.getModifyButtons());
                }
                else
                {
                    modifyButtons(buttonModel.getModifyButtons());
                }
            }
        }
    }

    /**
     * Calls super then {@link #resetTaskButtonBar()}.
     *
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        resetTaskButtonBar();
    }

    /**
     * Calls super method then sets the saved states to null.
     *
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        savedStates = null;
        hidePopupMenu();
        if (isReusable())
        {
            setButtonsDisabled();
        }
    }

    /**
     *
     */
    protected void hidePopupMenu()
    {
        if (getComponentPopupMenu() != null)
        {
            getComponentPopupMenu().setVisible(false);
            setComponentPopupMenu(null);
        }
        if (menuBean != null)
        {
            menuBean.deactivate();
            menuBean = null;
        }
    }

    /**
     * Popup the <code>childBean</code> as a {@link NavigationButtonBean}.
     *
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#popupMenu(java.lang.String, oracle.retail.stores.foundation.manager.gui.UIBeanIfc, oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void popupMenu(String menuName, UIBeanIfc childBean, UIModelIfc model)
    {
        if (menuName.equals(getName()))
        {
            // do nothing because the menu is already showing.
            return;
        }

        if (menuBean == null)
        {
            // create popup
            SlidingPopupMenu popupMenu = new SlidingPopupMenu();
            popupMenu.addPopupMenuListener(new SlidingMenuListener());
            // initialize the child nav button bean
            menuBean = (NavigationButtonBean)childBean;
            menuBean.setName(menuName);
            menuBean.setModel(model);
            if (menuParent != null)
            {
                // if we know the button that spawn this menu, paint it differently
                menuParent.setMenuDisplayed(true);
                // and move the buttons down level with the parent.
                menuBean.arrangeButtonsForMenu(menuParent, getButtonIndex(menuParent));
            }
            popupMenu.add(menuBean);

            // activate the child
            menuBean.activate();
            setComponentPopupMenu(popupMenu);
            popupMenu.show(this);
        }
        else
        {
            menuBean.popupMenu(menuName, childBean, model);
        }
    }

    public boolean isShowingMenu()
    {
        return (menuBean != null && menuBean.menuBean != null);
    }

    /**
     * Get orientation
     *
     * @return orientation {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}
     */
    public int getOrientation()
    {
        return orientation;
    }


    /**
     * Set orientation. Resets the {@link #buttonPrefix} and {@link #maxButtons}
     * to their defaults.
     *
     * @param newOrientation {@link SwingConstants#HORIZONTAL} or {@link SwingConstants#VERTICAL}
     */
    public void setOrientation(int newOrientation)
    {
        orientation = newOrientation;
        switch (orientation)
        {
            case HORIZONTAL:
                buttonPrefix = PREFIX_HORIZONTALBUTTON;
                maxButtons = MAX_HORIZONTAL;
                break;
            default:
                buttonPrefix = PREFIX_VERTICALBUTTON;
                maxButtons = MAX_VERTICAL;
        }
    }

    /**
     * Set the orientation of the bean from the XML. If
     * <BEANPROPERTY propName="orientation" propValue="Horizontal"/>
     * then its horizontal, otherwise vertical.
     *
     * @param orientation
     * @since 7.0.3
     */
    public void setOrientation(String orientation)
    {
        if(orientation.equalsIgnoreCase("Horizontal"))
        {
            setOrientation(HORIZONTAL);
        }
        else
        {
            setOrientation(VERTICAL);
        }
    }

    /**
     * @return the maxButtons
     */
    public int getMaxButtons()
    {
        return maxButtons;
    }

    /**
     * @param maxButtons the maxButtons to set
     */
    public void setMaxButtons(int maxButtons)
    {
        this.maxButtons = maxButtons;
    }

    /**
     * Adds a button listener. This adds the button listener as an action
     * listener to each of the buttons.
     *
     * @param listener the button listener
     */
    public void addButtonListener(ButtonListener listener)
    {
        if (buttons != null)
        {
            for (int i = 0; i < buttons.length; i++)
            {
                for (int j = 0; j < buttons[i].length; j++)
                {
                    buttons[i][j].addActionListener(listener);
                    buttons[i][j].setActionCommand(actions[i][j].getActionName());
                }
            }
        }
    }

    /**
     * Removes a button listener. This removes the button listener as an action
     * listener from each of the buttons.
     *
     * @param listener the button listener
     */
    public void removeButtonListener(ButtonListener listener)
    {
        if (buttons != null)
        {
            for (int i = 0; i < buttons.length; i++)
            {
                for (int j = 0; j < buttons[i].length; j++)
                {
                    buttons[i][j].removeActionListener(listener);
                }
            }
        }
    }

    /**
     * Modifies buttons using info from the the bean model. Only the button
     * label end enable state may be changed.
     */
    public void modifyButtons(ButtonSpec[] buttonSpecs)
    {
        for (int i = buttonSpecs.length - 1; i >= 0; i--)
        {
            try
            {
                UIAction action = getUIAction(buttonSpecs[i].getActionName());
                action.setEnabled(buttonSpecs[i].isEnabled());
                if (buttonSpecs[i].getLabel() != null)
                {
                    action.setButtonName(buttonSpecs[i].getLabel());
                }
                if (buttonSpecs[i].getKeyName() != null)
                {
                    action.setKeyName(buttonSpecs[i].getKeyName());
                }
            }
            catch (ActionNotFoundException e)
            {
                logger.warn("Failed to Find Action: " + buttonSpecs[i].getActionName());
            }
        }
    }

    /**
     * This method is called by the POSBeanSpec; the ButtonSpec array comes from
     * the <BUTTON> tag in the beans.xml file
     *
     * @param buttonSpecs each element of this array contains the information to
     *            create a button.
     */
    public void configureButtons(ButtonSpec[] buttonSpecs)
    {
        int barCount    = 1;
        int buttonCount = maxButtons;
        if(orientation == VERTICAL)
        {
            if (buttonSpecs.length > maxButtons)
            {
                // Calculate the number of button bars needed.
                // If there are more than MAX_VERTICAL buttons, then one button
                // per bar will be dedicated to the "More" key;  Therefore, the
                // number of buttons than can be used for other functions is
                // MAX_VERTICAL - 1.
                int funcButtons = maxButtons - 1;
                barCount = buttonSpecs.length / funcButtons;

                // The integer division above may have remainder; if so
                // then we need another button bar for remainder number of
                // buttons.
                int rem  = buttonSpecs.length % funcButtons;
                if (rem > 0)
                {
                    barCount++;
                }
            }
        }
        UIAction[][] actions = createActions(buttonSpecs, barCount, buttonCount);
        initialize(actions);
    }

    /**
     * Creates actions for the button bar.
     *
     * @param buttonSpecs Element[][] Parsed XML
     * @param indexSize int Number of bars
     * @param buttonSize int Number of buttons
     * @return UIAction[][] actions
     */
    public UIAction[][] createActions(ButtonSpec[] buttonSpecs,
                                      int indexSize, int buttonSize)
    {
        UIAction[][] actions = new UIAction[indexSize][buttonSize];
        ActionListener listener = null;
        buttonIndexSize = indexSize;
        int buttonIndex = 0;

        // Get which icon set to use via the plaf. Use white (vertical) set if undefined
        String iconSet = UIManager.getString(buttonPrefix + UIUtilities.SUFFIX_ICON_SET);
        if (iconSet == null)
        {
            iconSet = UIUtilities.ICON_SET_WHITE;
        }

        // loop through the bars
        for(int i = 0; i < indexSize; i++)
        {
            //loop through the buttons
            for(int j = 0; j < buttonSize; j++)
            {
                String actionName    = null;
                boolean enabled      = false;
                String buttonName    = null;
                String labelTag      = null;
                String iconName      = null;
                String keyName       = null;
                String parameterName = null;
                String listenerName  = null;
                ImageIcon icon       = null;
                boolean menu         = false;

                // Account for the "More key".
                if (indexSize > 1 && j == (buttonSize - 1))
                {
                    actionName  = ACTION_COMMAND_MORE;
                    enabled     = true;
                    buttonName  = "More >>>";
                    labelTag    = ACTION_COMMAND_MORE;
                    keyName     = "F9";
                    icon = UIUtilities.getImageIcon(iconSet, BUTTON_ICON_MORE, this);
                }
                else
                {
                    // If there is a valid button spec left
                    if (buttonIndex < buttonSpecs.length)
                    {
                        // Get the data
                        ButtonSpec buttonSpec   = buttonSpecs[buttonIndex];
                        actionName              = buttonSpec.getActionName();
                        enabled                 = buttonSpec.isEnabled();
                        buttonName              = buttonSpec.getLabel();
                        labelTag                = buttonSpec.getLabelTag();
                        iconName                = buttonSpec.getIconName();
                        keyName                 = buttonSpec.getKeyName();
                        parameterName           = buttonSpec.getParameterName();
                        listenerName            = buttonSpec.getActionListenerName();
                        menu                    = buttonSpec.isMenu();
                        buttonIndex++;

                        // Check for icon
                        if (buttonSpec.isIcon())
                        {
                            icon = UIUtilities.getImageIcon(iconSet, iconName, this);
                        }
                    }
                    else
                    {
                        // otherwise build a dummy action.
                        actionName = "NullButton";
                        enabled    = false;
                        keyName    = "";
                    }
                }

                // Create the key event
                int keyEvent = 0;
                if (keyName != null && keyName.length() > 0)
                {
                    StringBuilder keyNameStr = new StringBuilder("VK_");
                    keyNameStr.append(keyName);
                    keyEvent = KeyTable.getKeyEvent(keyNameStr.toString());
                }

                listener = createButtonListener(actionName, listenerName);
                buttonName = retrieveText(labelTag, buttonName);

                actions[i][j] = new UIAction(actionName,
                                             keyName,
                                             buttonName,
                                             labelTag,
                                             icon,
                                             enabled,
                                             keyEvent,
                                             listener);
                listener = null;
                actions[i][j].setButtonNumber((i*buttonSize)+j);
                actions[i][j].setParameterName(parameterName);
                actions[i][j].setMenu(menu);
            } // end looping of buttons
        } // end looping of bars

        return actions;
    }

    /**
     * @param iconSet
     * @param iconKey
     * @return
     * @deprecated as of 14.1. Use {@link UIUtilities#getImageIcon(String, String, Component)} instead.
     */
    protected ImageIcon getButtonIcon(String iconSet, String iconKey)
    {
        return UIUtilities.getImageIcon(iconSet, iconKey, this);
    }

    /**
     * Create the button action listener. Defaults to returning a
     * {@link DefaultMailAction} if <code>listenerName</code> is null. If <code>
     * actionName</code> is null, then a {@link MoreAction} is returned.
     *
     * @param actionName an {@link ActionEvent} command name. May be null.
     * @param listenerName a class name of a specific {@link ActionListener}. May be null.
     * @return
     */
    protected ActionListener createButtonListener(String actionName, String listenerName)
    {
        ActionListener listener = null;
        if (ACTION_COMMAND_MORE.equals(actionName))
        {
            listener = moreAction;
        }
        else if (listenerName != null)
        {
            try
            {
                Class<?> actClass = Class.forName(listenerName);
                listener = (ActionListener)actClass.newInstance();
            }
            catch(Exception e)
            {
                logger.error("Failed to Create Default Action:" + e);
            }
        }
        return listener;
    }

    /**
     *  Extracts component text from the properties object.
     */
    @Override
    protected void updatePropertyFields()
    {
        int indexSize = actions.length;
        int buttonSize = actions[0].length;
        String resourceKey = null;
        String defaultText = null;
        String resourcedText = null;
        //loop through the bars
        for (int i = 0; i < indexSize; i++)
        {
            // loop through the buttons
            for (int j = 0; j < buttonSize; j++)
            {
                if (!Util.isEmpty(actions[i][j].getButtonNameTag()))
                {
                    resourceKey = actions[i][j].getButtonNameTag();
                    defaultText = actions[i][j].getButtonName();
                    resourcedText = retrieveText(resourceKey, defaultText);
                    actions[i][j].setButtonName(resourcedText);
                }
            }
        }
    }

    /**
     * Associates a JButton with an Action via a PropertyChangeListener. This
     * triplet is stored in a hashtable with the button name as the key. The
     * action is added to the appropriate place in the array of UIActions. If
     * there is already a UIAction with the same name as a, it is replaced, in
     * location. If not, a is simply added to the array.
     *
     * @param barIndex the index of the current bar
     * @param actionIndex the index of the current action
     * @param action the Action to associate
     */
    protected void addUIAction(int barIndex, int actionIndex, UIAction action)
    {
        String label = (String)action.getValue(UIAction.SHORT_DESCRIPTION);
        Icon icon = (Icon)action.getValue(UIAction.SMALL_ICON);

        // create the button
        EYSButton button = uiFactory.createEYSButton(label, icon, buttonPrefix, false);
        // set the name to the resource id
        button.setName(action.getButtonNameTag());
        button.setActionCommand(action.getActionName());

        // this prevents the button from getting focus, a problem with
        // the clear action on the sale screen
        button.setRequestFocusEnabled(false);

        button.setEnabled(action.isEnabled());
        addActionListeners(button, action);

        // set whether it is a menu
        button.setMenu(action.isMenu());

        // all new buttons are created with visible set to false
        // the first button bar is made visible in the constructor
        button.setVisible(false);

        String keyName = (String)action.getValue(UIAction.KEYSTROKE);

        if (!Util.isEmpty(keyName))
        {
            StringBuilder keyNameBuilder = new StringBuilder();
            keyName = keyName.toUpperCase();

            if(keyName.equals("ESC"))
            {
                keyName = "ESCAPE";
            }
            else if (keyName.startsWith("CTRL"))
            {
                keyName = keyName.replace('+', ' ');
                keyName = keyName.replace("CTRL", "");
                keyNameBuilder.append("control ");
            }

            keyNameBuilder.append("released ");
            keyNameBuilder.append(keyName);

            KeyStroke keyStroke = KeyStroke.getKeyStroke(keyNameBuilder.toString());

            button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, action.getActionName());
            button.getActionMap().put(action.getActionName(), action);
        }

        // set the listener
        PropertyChangeListener listener = new ActionChangedListener(button);
        action.addPropertyChangeListener(listener);

        addButton(button, actionIndex);

        // add the button to the button arrays
        buttons[barIndex][actionIndex] = button;
    }

    /**
     * Adds the specified action and any other actions to the the button. This
     * method also adds the {@link #menuActionListener}.
     *
     * @param button
     * @param action
     */
    protected void addActionListeners(EYSButton button, UIAction action)
    {
        button.addActionListener(action);
        button.addActionListener(menuActionListener);
    }

    /**
     * Add the button to the bar at the specified index. Calls
     * {@link #adjustConstraints(int)} first.
     *
     * @param button
     * @param index
     */
    protected void addButton(EYSButton button, int index)
    {
        adjustConstraints(index);
        add(button, constraints);
    }

    /**
     * Adjusts the constraints object before adding a button.
     *
     * @param the index of the button being added
     */
    protected void adjustConstraints(int index)
    {
        if (orientation == VERTICAL)
        {
            constraints.gridx = 0;
            constraints.gridy = index;
        }
        else
        {
            constraints.gridx = index;
            constraints.gridy = 0;
            constraints.fill = GridBagConstraints.BOTH;
        }
        Insets i = UIManager.getInsets(buttonPrefix + "Insets");

        if (i != null)
        {
            constraints.insets = i;
        }
    }

    /**
     * Set the next button bar to be active. Increments {@link #currentButtonIndex}
     * by one and sets all those buttons as visible.
     */
    public void activateNextButtonBar()
    {
        // set visible false for current bar
        setButtonsVisible(currentButtonIndex, false);

        // update currentButtonIndex
        if (currentButtonIndex == buttonIndexSize - 1)
        {
            currentButtonIndex = 0;
        }
        else
        {
            currentButtonIndex++;
        }

        // set visible true for the updated index
        setButtonsVisible(currentButtonIndex, true);
    }

    /**
     * Set the button bar index back to zero and sets all the first bar's
     * buttons to visible.
     */
    public void resetTaskButtonBar()
    {
        if (currentButtonIndex != 0)
        {
            // set visible false for current bar
            setButtonsVisible(currentButtonIndex, false);

            // update currentButtonIndex
            currentButtonIndex = 0;

            // set visible true for the updated index
            setButtonsVisible(currentButtonIndex, true);
        }

        // check each action if it has been turn off by a parameter
        for (int i = buttons[currentButtonIndex].length - 1; i >= 0; i--)
        {
            String parameterName = actions[currentButtonIndex][i].getParameterName();
            if (parameterName != null)
            {
                boolean visible = UIUtilities.isParameterDisabled(parameterName);
                buttons[currentButtonIndex][i].setVisible(visible);
            }
        }
    }

    /**
     * Get background color
     *
     * @return Color background
     */
    public Color getBarBackground()
    {
        return getBackground();
    }

    /**
     * Get foreground color
     *
     * @return Color foreground
     */
    public Color getBarForeground()
    {
        return getForeground();
    }

    /**
     * Set background color
     *
     * @param c Color background color
     */
    public void setBarBackground(Color c)
    {
        super.setBackground(c);

        for (int i = buttons.length - 1; i >= 0; i--)
        {
            for (int j = buttons[i].length - 1; j >= 0; j--)
            {
                buttons[i][j].setBackground(c);
            }
        }
    }

    /**
     * Set foreground color
     *
     * @param c Color Foreground color
     */
    public void setBarForeground(Color c)
    {
        super.setForeground(c);

        for (int i = buttons.length - 1; i >= 0; i--)
        {
            for (int j = buttons[i].length - 1; j >= 0; j--)
            {
                buttons[i][j].setForeground(c);
            }
        }
    }

    /**
     * Hides all the buttons that do nothing (by using JLabels) and preferably,
     * we want the enabled buttons to start at the same index as {@link #menuParentIndex}
     *
     * @param menuParent the parent button that spawned this menu
     * @param menuParentIndex the index of the button that spawned this menu
     */
    protected void arrangeButtonsForMenu(EYSButton menuParent, int menuParentIndex)
    {
        // if we're on the first page of buttons, we want hide non-buttons
        Component[] components = getComponents();
        int componentCount = components.length;
        int startingPosition = -1; // the first real button that can be pressed

        // if there is only one page of buttons
        if (componentCount == this.buttons[currentButtonIndex].length)
        {
            GridBagLayout layout = (GridBagLayout)getLayout();
            // count the non-buttons and replace them with spacers
            int buttonCount = 0;
            for (int i = componentCount - 1; i >= 0; i--)
            {
                if (components[i] instanceof EYSButton)
                {
                    if (!components[i].isEnabled() && ((EYSButton)components[i]).getText().isEmpty())
                    {
                        GridBagConstraints cons = layout.getConstraints(components[i]);
                        if (spacers[i] == null)
                        {
                            spacers[i] = new JLabel();
                            if (menuParent != null)
                            {
                                spacers[i].setPreferredSize(menuParent.getPreferredSize());
                            }
                            else
                            {
                                spacers[i].setPreferredSize(components[i].getPreferredSize());
                            }
                        }
                        remove(components[i]);
                        add(spacers[i], cons, i);
                    }
                    else
                    {
                        buttonCount++;
                        startingPosition = i;
                    }
                }
            }

            // if submenu is shorter than normal menu and does not start at the parent's index
            // then bubble the real buttons down but not past the bottom of the bean.
            if (buttonCount < maxButtons && menuParentIndex != startingPosition)
            {
                bubbleButtons(menuParentIndex, startingPosition, componentCount, buttonCount);
            }

            // when the screen is resized, the spacers tend to get smaller,
            // allowing the submenu buttons to look expanded. This will force
            // them to look correctly sized.
            if (menuParent != null)
            {
                for (Component button : components)
                {
                    button.setMinimumSize(menuParent.getSize());
                }
            }
        }
    }

    /**
     * Bubble sorts the buttons to match the <code>menuParentIndex</code>.
     *
     * @param menuParentIndex
     * @param startingIndex
     * @param componentCount
     * @param buttonCount
     */
    private void bubbleButtons(int menuParentIndex, int startingIndex, int componentCount, int buttonCount)
    {
        // Given the number of visible buttons, calculate the highest index value the
        // top button can have.
        int maxButtonIndex = maxButtons - buttonCount;

        // Ideally the top button should be placed next to parent button.
        int topButtonIndex = menuParentIndex;

        // However, if the parent button is too low on the screen to
        // accommadate length of the popup menu, set the top button index
        // to maximum value it can have.
        if (maxButtonIndex < menuParentIndex)
        {
            topButtonIndex = maxButtonIndex;
        }

        // If current index of the top button is greater than the required
        // location...
        if (startingIndex > topButtonIndex)
        {
            // Slide the entire menu up the required number of slots.  This is
            // accomplished by moving the top (transparent) button, to the bottom,
            // once for each position change.
            for(int i = startingIndex - topButtonIndex; i > 0; --i)
            {
                moveTopButtonToBottomPosition();
            }
        }

        // If current index of the top button is less than the required
        // location...
        if (startingIndex < topButtonIndex)
        {
            // Slide the entire menu down the required number of slots.  This is
            // accomplished by moving the bottom (transparent) button, to the top,
            // once for each position change.
            for(int i = topButtonIndex - startingIndex; i > 0; --i)
            {
                moveBottomButtonToTopPosition();
            }
        }
    }

    /**
     * Move the top button to lowest postion. This has the effect of
     * moving all the visible buttons up one postion in the button bar.
     */
    private void moveTopButtonToBottomPosition()
    {
        int higher = 0;
        int lower  = 1;
        for(int i = maxButtons; i > 1; i--)
        {
            swapPosition(higher, lower);
            higher++;
            lower++;
        }
    }

    /**
     * Move the bottom button to highest postion. This has the effect of
     * moving all the visible buttons down one postion in the button bar.
     */
    private void moveBottomButtonToTopPosition()
    {
        int higher = maxButtons - 1;
        int lower  = higher - 1;
        for(int i = maxButtons; i > 1; i--)
        {
            swapPosition(lower, higher);
            higher--;
            lower--;
        }
    }

    /**
     * @param index1
     * @param index2
     */
    private void swapPosition(int index1, int index2)
    {
        GridBagLayout layout = (GridBagLayout)getLayout();
        // get the higher widget that is moving down
        Component widget1 = getComponent(index1);
        GridBagConstraints constraint1 = layout.getConstraints(widget1);
        // get the lower widget that is moving up
        Component widget2 = getComponent(index2);
        GridBagConstraints constraint2 = layout.getConstraints(widget2);
        // re-arrange their position
        add(widget1, constraint2, index2);
        layout.setConstraints(widget2, constraint1);
    }

    /**
     * Return the index in which the specified button appears in the current
     * array of buttons.
     *
     * @param button
     * @return
     */
    protected int getButtonIndex(EYSButton button)
    {
        if (button != null)
        {
            Object[] buttons = (currentButtonIndex == 0)? getComponents() : this.buttons[currentButtonIndex];
            for (int i = buttons.length - 1; i >= 0; i--)
            {
                if (button == buttons[i])
                {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Set the bar of buttons at the specified index to visible.
     *
     * @param barIndex
     * @param visible
     */
    protected void setButtonsVisible(int barIndex, boolean visible)
    {
        for (int i = buttons[barIndex].length - 1; i >= 0; i--)
        {
            buttons[barIndex][i].setVisible(visible);
        }
    }

    /**
     * Returns true if this bean is reusable for different {@link #buttonModel}.
     * This default implementation returns true if the {@link #getBeanSpecName()}
     * returns "LocalNavigationPanelSpec".
     *
     * @return
     */
    protected boolean isReusable()
    {
        return "LocalNavigationPanelSpec".equals(getBeanSpecName());
    }

    /**
     * Set all the current buttons to enabled=false and text="".
     */
    protected void setButtonsDisabled()
    {
        if (buttons != null)
        {
            for (int i = buttons[currentButtonIndex].length - 1; i >= 0; i--)
            {
                buttons[currentButtonIndex][i].setEnabled(false);
                buttons[currentButtonIndex][i].setText("");
            }
        }
    }

    /**
     * Creates a button bar from UIActions. The arrays are looped through in
     * ascending order to ensure subclasses can add buttons correctly.
     *
     * @param actions UIAction[][] Actions to create button from
     */
    protected void setUIActions(UIAction[][] actions)
    {
        UIAction a = null;

        for (int i = 0; i < actions.length; i++)
        {
            for (int j = 0; j < actions[i].length; j++)
            {
                a = actions[i][j];
                addUIAction(i, j, a);
            }
        }
    }

    /**
     * Set the properties for a button
     *
     * @param actionName String Name of the action
     * @param action UIAction Action to set
     * @exception ActionNotFoundException if the action is not in the button bar
     */
    public void setUIAction(String actionName, UIAction action)
        throws ActionNotFoundException
    {
        for (int i = actions.length - 1; i >= 0; i--)
        {
            for (int j = actions[i].length - 1; j >= 0; j--)
            {
                if (actions[i][j].getButtonName().equals(actionName))
                {
                    actions[i][j] = action;
                    return ;
                }
            }
        }
        throw new ActionNotFoundException("UIAction with name \"" + actionName + "\" not found");
    }

    /**
     * Retrieves one of the actions in the button bar by name.
     *
     * @param actionName the name of the action to retrieve
     * @return the desired action
     * @exception ActionNotFoundException if the action is not in the button bar
     */
    public UIAction getUIAction(String actionName)
        throws ActionNotFoundException
    {
        // loop through the bars
        for (int i = actions.length - 1; i >= 0; i--)
        {
            // loop through the actions
            for (int j = actions[i].length - 1; j >= 0; j--)
            {
                UIAction action = actions[i][j];

                // return the action if found
                if (action.getActionName().equals(actionName))
                {
                    return action;
                }
            }
        }
        throw new ActionNotFoundException(actionName + " UIAction not found");
    }

    /**
     * Sets the buttons with the action names listed in the parameter to
     * disabled. This can be called from the BeanSpec by setting a BEANPROPERTY
     * in the XML.
     *
     * @param actionNames a comma delimited list of action names.
     */
    public void setButtonStates(String actions)
    {
        String[] actionsAndStates =
            UIUtilities.parseDelimitedList(actions, ACTION_NAME_DELIMITER);

        applyEnableProperty(actionsAndStates);
    }

    /**
     * This method set the buttons to the requested state. Each element in the
     * array should look line this, "Next[true]" or "Cancel[false]".
     *
     * @param actionNames an array of action names.
     */
    protected void applyEnableProperty(String[] actions)
    {
        // Clear the button states from map
        buttonStates.clear();

        for(int i = actions.length - 1; i >= 0; i--)
        {
            try
            {
                // Get the star and end of the action and the enable value
                int startName  = 0;
                int endName    = actions[i].indexOf("[");
                int startBool  = endName + 1;
                int endBool    = actions[i].indexOf("]");

                // Get the name, the string value and boolean enable value
                String name    = actions[i].substring(startName, endName);
                String bool    = actions[i].substring(startBool, endBool);


                // Get the action and set the enable value
                UIAction act   = getUIAction(name);
                act.setEnabled(UIUtilities.getBooleanValue(bool));
                buttonStates.put(name, UIUtilities.getBooleanValue(bool));
            }
            catch (ActionNotFoundException e)
            {
                logger.warn("Failed to Find Action: " + actions[i]);
            }
        }
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    /* (non-Javadoc)
     * @see java.awt.Component#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder strResult = new StringBuilder(Util.getSimpleClassName(getClass()));
        strResult.append("@");
        strResult.append(hashCode());
        strResult.append("[beanSpecName=");
        strResult.append(getBeanSpecName());
        strResult.append("]");
        return strResult.toString();
    }

    /**
     * Disables the buttons if the response field is empty. Enables the buttons
     * when the response field is not empty.
     *
     * @param evt the document event
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent evt)
    {
        int len = evt.getDocument().getLength();

        // Enable buttons
        if (len > 0)
        {
            if (savedStates != null)
            {
                for (int i = actions.length - 1; i >= 0; i--)
                {
                    for (int j = actions[i].length - 1; j >= 0; j--)
                    {
                        if (savedStates[i][j])
                        {
                            actions[i][j].setEnabled(true);
                        }
                    }
                }
            }
        }
        else // Disable buttons
        {
            savedStates = new boolean[actions.length][];

            for (int i = actions.length - 1; i >= 0; i--)
            {
                savedStates[i] = new boolean[actions[i].length];

                for (int j = actions[i].length - 1; j >= 0; j--)
                {
                    savedStates[i][j] = actions[i][j].isEnabled();

                    if (!actions[i][j].getActionName().equals(ACTION_COMMAND_MORE))
                    {
                        actions[i][j].setEnabled(false);
                    }
                }
            }
        }
    }

    /**
     * Calls {@link #changedUpdate(DocumentEvent)}
     *
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent evt)
    {
        changedUpdate(evt);
    }

    /**
     * Calls {@link #changedUpdate(DocumentEvent)}
     *
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent evt)
    {
        changedUpdate(evt);
    }

    // -------------------------------------------------------------------------
    /**
     * Updates a button when properties are changed. Properties supported are
     * <ul>
     * <li>"enabled"
     * <li>{@link UIAction#KEYSTROKE}
     * <li>{@link UIAction#SHORT_DESCRIPTION}
     * <li>{@link UIAction#SMALL_ICON}
     * </ul>
     */
    protected class ActionChangedListener implements PropertyChangeListener
    {
        AbstractButton button;
        KeyStroke stroke        = null;
        boolean strokeNotSet    = true;

        /**
         * Constructor
         * @param b
         */
        ActionChangedListener(AbstractButton b)
        {
            this.button = b;
        }

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent e)
        {
            String propertyName = e.getPropertyName();
            if (propertyName.equals("enabled"))
            {
                Boolean enabledState = (Boolean)e.getNewValue();
                button.setEnabled(enabledState);
            }
            else if (propertyName.equals(UIAction.KEYSTROKE))
            {
                String oldKey = (String) e.getOldValue();
                String newKey = (String) e.getNewValue();

                if(oldKey != null)
                {
                    KeyStroke oldPress =
                        KeyStroke.getKeyStroke("pressed " + oldKey.toUpperCase());

                    KeyStroke oldRel =
                        KeyStroke.getKeyStroke("released " + oldKey.toUpperCase());

                    button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(oldPress);
                    button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(oldRel);
                }
                if(newKey != null)
                {
                    KeyStroke newPress =
                        KeyStroke.getKeyStroke("pressed " + newKey.toUpperCase());

                    KeyStroke newRel =
                        KeyStroke.getKeyStroke("released " + newKey.toUpperCase());

                    button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(newPress, "pressed");
                    button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(newRel, "released");
                }
            }
            else if (propertyName.equals(UIAction.SHORT_DESCRIPTION))
            {
                String text = (String) e.getNewValue();
                button.setText(text);
            }
            else if (propertyName.equals(UIAction.SMALL_ICON))
            {
                Icon icon = (Icon) e.getNewValue();
                button.setIcon(icon);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     * An action that calls {@link NavigationButtonBean#activateNextButtonBar()}.
     */
    public class MoreAction extends AbstractAction implements Action
    {
        private static final long serialVersionUID = 4762814008093386181L;

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent evt)
        {
            activateNextButtonBar();
        }
    }

    //---------------------------------------------------------------------
    /**
     * An action that keeps track of which button trigger a menu.
     */
    public class MenuActionListener implements ActionListener
    {
        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent evt)
        {
            if (evt.getSource() instanceof EYSButton)
            {
                EYSButton button = (EYSButton)evt.getSource();
                for (int i = 0; i < getComponentCount(); i++)
                {
                    // use reference equals check. We want the exact button object.
                    if (button == getComponent(i))
                    {
                        menuParent = button;
                        break;
                    }
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    protected class SlidingMenuListener implements PopupMenuListener
    {
        /* (non-Javadoc)
         * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent)
         */
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e)
        {
            if (buttons != null)
            {
                for (int i = buttons[currentButtonIndex].length - 1; i >= 0; i--)
                {
                    boolean wasEnabled = buttons[currentButtonIndex][i].isEnabled();
                    buttons[currentButtonIndex][i].putClientProperty("wasEnabled", wasEnabled);
                    buttons[currentButtonIndex][i].setEnabled(false);
                }
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.event.PopupMenuListener#popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent)
         */
        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
        {
            if (menuParent != null)
            {
                menuParent.setMenuDisplayed(false);
            }
            NavigationButtonBean.this.menuBean = null;
            if (buttons != null)
            {
                for (int i = buttons[currentButtonIndex].length - 1; i >= 0; i--)
                {
                    Boolean wasEnabled = (Boolean)buttons[currentButtonIndex][i].getClientProperty("wasEnabled");
                    buttons[currentButtonIndex][i].setEnabled(wasEnabled);
                }
            }
        }

        /* (non-Javadoc)
         * @see javax.swing.event.PopupMenuListener#popupMenuCanceled(javax.swing.event.PopupMenuEvent)
         */
        @Override
        public void popupMenuCanceled(PopupMenuEvent e)
        {
            if (menuParent != null)
            {
                menuParent.setMenuDisplayed(false);
            }
            NavigationButtonBean.this.menuBean = null;
            // consume the event that cause the menu to hide, to prevent extra letters
            AWTEvent awte  = EventQueue.getCurrentEvent();
            if (awte instanceof KeyEvent)
            {
                POSFocusManager focusMgr = (POSFocusManager)KeyboardFocusManager.getCurrentKeyboardFocusManager();
                focusMgr.setIgnoreNextKeyEvent(((KeyEvent)awte).getKeyChar());
            }
            UISubsystem.getInstance().mail(new MenuCancelledLetter(), true);
        }
    }

    /**
     * The main function acts as a test driver.
     *
     * @param args - command line parameters
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        ActionListener l=new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                System.out.println(evt.toString());
            }
        };
        // Make a 4 button bar
        UIAction[][] action4 = new UIAction[1][];
        action4[0]=new UIAction[4];
        for (int i = 0; i < 4; i++)
        {
            action4[0][i] =
                new UIAction("name"+i, "F"+i, "Label "+i, "tag"+i, null, i%2 == 0,
                                         KeyEvent.VK_0+i, l);
        }
        action4[0][1].setEnabled(true);
        action4[0][3].setEnabled(true);

        // Use the button bar of the default constructor
        NavigationButtonBean bbar1 = new NavigationButtonBean(action4);

        UIUtilities.doBeanTest(bbar1);
    }
}
