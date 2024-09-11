package max.retail.stores.pos.ui.beans;
/* ===========================================================================

 * ===========================================================================
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import max.retail.stores.pos.ui.MAXDialogScreensIfc;
import max.retail.stores.pos.ui.MAXEdgeDialogScreensIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CycleRootPanel;
import oracle.retail.stores.pos.ui.beans.DialogBean;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.SignatureDialogBeanModel;
import oracle.retail.stores.pos.ui.beans.SignaturePanel;
import oracle.retail.stores.pos.ui.behavior.ScreenNameListener;

import org.apache.log4j.Logger;

public class MAXDialogBean extends DialogBean
{
    /** Serial Version UID */
    private static final long serialVersionUID = 8624230989769953784L;

    /** Debug and error logger. */
    private static final Logger logger = Logger.getLogger(DialogBean.class);

    /** Revision number */
    public static final String revisionNumber = "$Revision: /main/24 $";

    /** Static variables for buttons */
    protected static String OK_LETTER = "Ok";
    protected static String NO_LETTER = "No";
    protected static String YES_LETTER = "Yes";
    protected static String RETRY_LETTER = "Retry";
    protected static String CONTINUE_LETTER = "Continue";
    protected static String CANCEL_LETTER = "Cancel";
    protected static String NOW_LETTER = "Now";
    protected static String LATER_LETTER = "Later";
    protected static String ADD_LETTER = "Add";
    protected static String UPDATE_LETTER = "Update";
    protected static String PICKUP_LETTER = "Pickup";
    protected static String SHIP_LETTER = "Ship";
    protected static String DELIVER_LETTER = "Deliver";
    protected static String SEARCHWEBSTORE_LETTER = "Webstore";
    protected static String ONE_LETTER = "One";
    protected static String MULTIPLE_LETTER = "Multiple";
  //Changes starts for Rev 1.1 (Ashish : Edge)
    protected static String ENTER_LETTER = "Continue";
	protected static String CUST_INFORMATION_LETTER = "CustInfo";
	
	//Changes starts for Rev 1.1 (Ashish : Edge)
	
    protected static String PARTIAL_LETTER = "Partial";
    protected static String CONVERTED_LETTER = "Converted";
    protected static String TOTAL_LETTER = "Total"; 

    protected static String[] BUTTON_OK       = null; //{"Enter", OK_LETTER, "e"};
    protected static String[] BUTTON_YES      = null; //{"Yes", YES_LETTER, "y"};
    protected static String[] BUTTON_NO       = null; //{"No", NO_LETTER, "n"};
    protected static String[] BUTTON_CONTINUE = null; //{"Continue", CONTINUE_LETTER, "c"};
    protected static String[] BUTTON_RETRY    = null; //{"Retry", RETRY_LETTER, "r"};
    protected static String[] BUTTON_CANCEL   = null; //{"Cancel", CANCEL_LETTER, "a"};
    protected static String[] BUTTON_NOW      = null; //{"Now", NOW_LETTER, "b"};
    protected static String[] BUTTON_LATER    = null; //{"Later", LATER_LETTER, "l"};
    protected static String[] BUTTON_ADD      = null; //{"Add", ADD_LETTER, "a"};
    protected static String[] BUTTON_UPDATE   = null; //{"Update", UPDATE_LETTER, "u"};
    protected static String[] BUTTON_PICKUP   = null; //{"Pickup", PICKUP_LETTER, "p"};
    protected static String[] BUTTON_SHIP     = null; //{"Ship", SHIP_LETTER, "s"};
    protected static String[] BUTTON_DELIVER  = null; //{"Deliver", DELIVER_LETTER, "d"};
    protected static String[] BUTTON_SEARCH_WEBSTORE  = null; //{"SearchWebstore", SEARCHWEBSTORE_LETTER, "w"};
    protected static String[] BUTTON_SEARCH_SIM  = null; //{"StoreInventoryManagement", SEARCHSIM_LETTER, "i"};
    protected static String[] BUTTON_ONE      = null; //{"One", ONE_LETTER, "o"};
    protected static String[] BUTTON_MULTIPLE = null; //{"Multiple", MULTIPLE_LETTER, "m"};

    protected static String[] BUTTON_ENTER = null;
	protected static String[] BUTTON_CUST_INFORMATION = null;
	protected static String[] BUTTON_PARTIAL = null;
	protected static String[] BUTTON_CONVERTED = null;
	protected static String[] BUTTON_TOTAL = null;
	
    protected static String[][] BUTTONS = null;

    protected static int BTN_NAME = 0;
    protected static int BTN_ACTION = 1;
    protected static int BTN_MNEMONIC = 2;

    protected static KeyStroke ENTER_DOWN = KeyStroke.getKeyStroke("pressed ENTER");
    protected static KeyStroke ENTER_UP = KeyStroke.getKeyStroke("released ENTER");

    /** Static variables for the screen layout */
    protected static String ARG_MARKER       = "<ARG>";
    protected static String PROP_TITLE       = "title";
    protected static String PROP_DESCRIPTION = "description";
    protected static String PROP_LINE        = "line";

    /**
     * previous locale.
     * @deprecated as of 13.4. Use {@link DialogBeanModel#getLocale()} instead.
     */
    protected Locale previousLocale = null;
    /** Title label */
    protected JLabel titleLabel = null;

    /** Title wrapper */
    protected JPanel titleLabelWrap = null;

    /** Error message component */
    protected JTextPane messagePanel = null;

    /** Error message wrapper */
    protected JPanel messagePanelWrap = null;

    /** Button panel */
    protected CycleRootPanel buttonPanel = null;

    /** Signature capture panel */
    protected SignaturePanel signaturePanel = null;

    /** Holds the default button */
    protected JButton defaultButton = null;

    /** Dialog been model */
    protected DialogBeanModel beanModel = null;

    /** Indicate if the bean needs to be updated.
     * @deprecated as of release 6.0 obsolete flag*/
    protected boolean dirtyModel = true;

    /** the letter sent by the ok button */
    protected String okLetterName = null;

    /** Listens for a change in the screen name. */
    protected ScreenNameListener screenNameListener = null;

    /** Holds the screen name. */
    protected String screenName = null;

    protected String buttonPrefix = "DialogButton";
    protected String messageFont = "dialogFont";

    /**
     * Default constructor.
     * @return 
     */
    public void DialogBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("MAXDialogBean");
        UI_PREFIX = "MAXDialogBean";
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Activates screen.
     */
    @Override
    public void activate()
    {
        super.activate();
        updatePropertyFields();
    }

    /**
     * Initialize the components in this bean.
     */
    protected void initComponents()
    {
        createTitleLabel();
        createMessagePanel();
        createButtonPanel();
    }

    /**
     * Creates this bean's layout and lays out the components.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());

        // create the constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.gridy = 0;
//        gbc.ipady = 8;

        // add the title label
        add(titleLabelWrap, gbc);

        //add the TextPane
        gbc.ipady = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 1;
        gbc.weighty = 0.8;
        add(messagePanelWrap, gbc);

        // add the button panel
       // gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 4;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(buttonPanel, gbc);
    }

    /**
     * Creates the text pane that holds the dialog message.
     */
    protected void createMessagePanel()
    {
        String prefix = UI_PREFIX + ".message";

        messagePanel = new JTextPane(new DefaultStyledDocument());
        uiFactory.configureUIComponent(messagePanel, prefix);

        messagePanel.setMargin(UIManager.getInsets(prefix + ".margin"));
        messagePanel.setEditable(false);

        Style def =
            StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style s = messagePanel.addStyle("regular", def);

        Font dialogFont = messagePanel.getFont();

        if(dialogFont != null)
        {
            StyleConstants.setFontSize(s, dialogFont.getSize());
            StyleConstants.setFontFamily(s, dialogFont.getFamily());
            StyleConstants.setBold(s,dialogFont.isBold());
        }
        s = messagePanel.addStyle("centered",def);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);

        s = messagePanel.addStyle("justified",def);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_JUSTIFIED);

        messagePanelWrap = new JPanel(new BorderLayout());
        uiFactory.configureUIComponent(messagePanelWrap, prefix + "Wrap");
        messagePanelWrap.add(messagePanel, BorderLayout.CENTER);
    }

    /**
     * Creates the label that holds the dialog title.
     */
    protected void createTitleLabel()
    {
        titleLabelWrap = new JPanel(new BorderLayout());

        uiFactory.configureUIComponent(titleLabelWrap, UI_PREFIX + ".header");

        titleLabel = uiFactory.createLabel("DialogTitle", "DialogTitle", null, UI_PREFIX + ".header.label");

        titleLabelWrap.add(titleLabel, BorderLayout.CENTER);
    }

    /**
     * Creates the panel that holds the dialog buttons.
     */
    protected void createButtonPanel()
    {
        buttonPanel = new CycleRootPanel();
        uiFactory.configureUIComponent(buttonPanel, UI_PREFIX + ".buttonPanel");

        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setPreferredSize(new Dimension(300, 80));
    }

    /**
     * Creates each button.
     *
     * @param label the text to display on the button.
     * @param mnemonic the "hot key" characer.
     * @param focus indicates if the button should have focus when first displayed.
     * @Return JButton
     */
    protected JButton createButton(String[] data, boolean focus)
    {
        JButton button = uiFactory.createEYSButton(data[BTN_NAME], null, buttonPrefix, true);

        button.setActionCommand(data[BTN_ACTION]);
        button.setMnemonic(data[BTN_MNEMONIC].charAt(0));
        button.addActionListener(getDefaultBtnListener());

        KeyStroke mPress = KeyStroke.getKeyStroke("pressed " + data[BTN_MNEMONIC].toUpperCase());
        KeyStroke mRelease = KeyStroke.getKeyStroke("released " + data[BTN_MNEMONIC].toUpperCase());

        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(mPress, "pressed");
        button.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(mRelease, "released");

        button.getInputMap().put(ENTER_DOWN, "pressed");
        button.getInputMap().put(ENTER_UP, "released");
        button.setFocusTraversalKeysEnabled(true);
        button.setFocusable(true);

        if (focus)
        {
            defaultButton = button;
        }
        return (button);
    }

    /**
     * Sets the Model for this Bean
     *
     * @param model a DialogBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model instanceof DialogBeanModel)
        {
            if (beanModel != null && beanModel.getType() != ((DialogBeanModel)model).getType())
            {
                // if the buttons types have changed, remove them. Allows for reuse of current buttons.
                buttonPanel.removeAll();
            }
            beanModel = (DialogBeanModel)model;
            updateBean();
        }
    }

    /**
     * Updates the model if It's been changed
     */
    @Override
    protected void updateBean()
    {
        if (beanModel != null)
        {
            // set title.
            String title = beanModel.getTitle();
            String titleTag = beanModel.getTitleTag();

            if (Util.isEmpty(titleTag))
            {
                if (Util.isEmpty(title))
                {
                    title = getTitleText();
                }
            }
            else
            {
                title = retrieveText(titleTag,getTitleText());
            }
            titleLabel.setText(title);

            if (beanModel.getType() == DialogScreensIfc.ERROR ||
                    beanModel.getType() == DialogScreensIfc.ERROR_NO_BUTTONS)
            {
                setTitleColor(UIManager.getColor("attention"));
            }
            else
            {
                setTitleColor(UIManager.getColor("accent"));
            }

            // Retrieve button text
            initButtonsText();

            String[] letters = beanModel.getLetters();
            if (letters != null && letters.length > 0)
            {
                for (int i = 0; i < BUTTONS.length; i++)
                {
                	System.out.println("MAXDialogBean I value :"+i);
                    if (letters[i] != null && letters[i].length() > 0)
                    {
                        BUTTONS[i][BTN_ACTION] = letters[i];
                    }
                    else
                    {
                    	System.out.println("MAXDialogBean else I value :"+i);
                    	//Changes starts for Rev 1.1 (Ashish ; Edge)
                        BUTTONS[i][BTN_ACTION] = MAXEdgeDialogScreensIfc.DIALOG_BUTTON_LABELS[i];
                      //Changes ends for Rev 1.1 (Ashish ; Edge)
                    }
                }
            }
            screenName = beanModel.getDescription();
            if (screenName == null || screenName.equals(""))
            {
                screenName = getProperty(PROP_DESCRIPTION, "");
            }
            if (screenNameListener != null)
            {
                screenNameListener.setScreenName(screenName);
            }

            initTextPane();
            initButtons();
        }
    }

    /**
     * Sets up the buttons as they are required by the bean model.
     */
   protected void initButtonsText()
   {
        if (BUTTONS == null  || (beanModel.getLocale() != null && !beanModel.getLocale().equals(getLocale())))
        {
            if (beanModel.getLocale() != null)
            {
                setLocale(beanModel.getLocale());
            }
            BUTTON_YES = new String[] { retrieveText("YesBtn"  ,"Yes"),
                                        YES_LETTER, retrieveText("YesBtnMnemonic" ,"y")};

            BUTTON_NO = new String[] { retrieveText("NoBtn"  ,"No"),
                                       NO_LETTER, retrieveText("NoBtnMnemonic" ,"n")};

            BUTTON_OK = new String[] { retrieveText("OkBtn"  ,"Enter"),
                                       OK_LETTER, retrieveText("OkBtnMnemonic" ,"e")};

            BUTTON_CONTINUE = new String[] { retrieveText("ContinueBtn"  ,"Continue"),
                                             CONTINUE_LETTER, retrieveText("ContinueBtnMnemonic" ,"c")};

            BUTTON_CANCEL = new String[] { retrieveText("CancelBtn"  ,"Cancel"),
                                       CANCEL_LETTER, retrieveText("CancelBtnMnemonic" ,"a")};

            BUTTON_RETRY = new String[] { retrieveText("RetryBtn"  ,"Retry"),
                                       RETRY_LETTER, retrieveText("RetryBtnMnemonic" ,"r")};

            BUTTON_NOW = new String[] { retrieveText("NowBtn", "Now"),
                                        NOW_LETTER, retrieveText("NowBtnMnemonic", "b")};

            BUTTON_LATER = new String[] { retrieveText("LaterBtn", "Later"),
                                        LATER_LETTER, retrieveText("LaterBtnMnemonic", "l")};

            BUTTON_ADD = new String[] { retrieveText("AddBtn", "Add"),
                    ADD_LETTER, retrieveText("AddBtnMnemonic", "a")};

            BUTTON_UPDATE = new String[] { retrieveText("UpdateBtn", "Update"),
                    UPDATE_LETTER, retrieveText("UpdateBtnMnemonic", "u")};

            BUTTON_PICKUP = new String[] { retrieveText("PickupBtn", "Pickup"),
                    PICKUP_LETTER, retrieveText("PickupBtnMnemonic", "p")};

            BUTTON_SHIP = new String[] { retrieveText("ShipBtn", "Ship"),
                    SHIP_LETTER, retrieveText("ShipBtnMnemonic", "s")};

            BUTTON_DELIVER = new String[] { retrieveText("DeliverBtn", "Deliver"),
                    DELIVER_LETTER, retrieveText("DeliverBtnMnemonic", "d")};
            
            BUTTON_SEARCH_WEBSTORE = new String[] { retrieveText("SearchWebStoreBtn", "Webstore"),
                    SEARCHWEBSTORE_LETTER, retrieveText("SearchWebStoreBtnMnemonic", "w")};
            
            BUTTON_SEARCH_SIM = new String[] { retrieveText("SearchSIMBtn", "Serail Number"),
                    SEARCHWEBSTORE_LETTER, retrieveText("SearchSIMBtnMnemonic", "s")};
            
            BUTTON_ONE = new String[] { retrieveText("OneBtn", "One"), ONE_LETTER, retrieveText("OneBtnMnemonic", "o") };

            BUTTON_MULTIPLE = new String[] { retrieveText("MultipleBtn", "Multiple"), MULTIPLE_LETTER,
                    retrieveText("MultipleBtnMnemonic", "m") };
            //Changes starts for Rev 1.1 (Ashish : Edge)
            BUTTON_CUST_INFORMATION = new String[] { retrieveText("Customer Information", "CustInfo"), CUST_INFORMATION_LETTER,
                    retrieveText("CustInfoBtnMnemonic", "c") };
            BUTTON_ENTER = new String[] { retrieveText("Enter", "Enter"), ENTER_LETTER,
                    retrieveText("EnterBtnMnemonic", "e") };

            BUTTON_PARTIAL = new String[] { retrieveText("Partial", "Partial"), PARTIAL_LETTER,
                    retrieveText("PartialBtnMnemonic", "p") };
            BUTTON_CONVERTED = new String[] { retrieveText("Converted", "Converted"), CONVERTED_LETTER,
                    retrieveText("ConvertedBtnMnemonic", "c") };
            BUTTON_TOTAL = new String[] { retrieveText("Total", "Total"), TOTAL_LETTER,
                    retrieveText("TotalBtnMnemonic", "t") };
         
            
            BUTTONS = new  String[][] { BUTTON_OK, BUTTON_YES, BUTTON_NO,
                                       BUTTON_CONTINUE, BUTTON_RETRY, BUTTON_CANCEL,
                                       BUTTON_NOW, BUTTON_LATER, BUTTON_ADD, BUTTON_UPDATE,
                                       BUTTON_PICKUP, BUTTON_SHIP, BUTTON_DELIVER, 
                                       BUTTON_SEARCH_WEBSTORE, BUTTON_SEARCH_SIM, BUTTON_ONE, 
                                       BUTTON_MULTIPLE,BUTTON_CUST_INFORMATION,BUTTON_ENTER,
                                       BUTTON_PARTIAL,BUTTON_CONVERTED,BUTTON_TOTAL};
          //Changes starts for Rev 1.1 (Ashish : Edge)
        }

    }

    /**
     * Sets up the buttons as they are required by the bean model. If the type
     * of buttons to be used has changed since the last call of this method,
     * the button panel should already be empty.
     */
    protected void initButtons()
    {
        boolean buttonsAlreadyAdded = (buttonPanel.getComponentCount() > 0);

        if (signaturePanel != null)
        {
            remove(signaturePanel);
        }

        if (buttonsAlreadyAdded)
        {
            updateButtonTextAndLetter();
        }
        else
        {
            addButtons();
        }
        buttonPanel.repaint();

        if (signaturePanel != null)
        {
            signaturePanel.repaint();
        }
    }

    /**
     * Add the putton to the button panel.
     */
    protected void addButton(JButton button, int pos)
    {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = pos;
        Insets buttonInsets = UIManager.getInsets("dialogButtonInsets");

        if(buttonInsets != null)
        {
            gbc.insets = buttonInsets;
        }
        buttonPanel.add(button, gbc);
    }

    /**
     * Add signature panel to the dialog
     *
     * @param signature an array of points that represent a customer signature.
     */
    protected void addSignaturePanel(Point[] signature)
    {
        signaturePanel = new SignaturePanel(signature);
        signaturePanel.setName("SignaturePanel");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridy = 3;
        signaturePanel.setMinimumSize(signaturePanel.getPreferredSize());
        signaturePanel.setMaximumSize(signaturePanel.getPreferredSize());
        add(signaturePanel, gbc);
    }

    /**
     * Sets the color
     *
     * @param DefColor the default color object
     */
    protected void setTitleColor(Color DefColor)
    {
        if(beanModel.getBannerColor() != null)
        {
            titleLabelWrap.setBackground(beanModel.getBannerColor());
        }
        else
        {
            titleLabelWrap.setBackground(DefColor);
        }
    }

    /**
     * Initializes the TextPane from the model
     */
    protected void initTextPane()
    {
        StyledDocument doc = (StyledDocument) messagePanel.getDocument();

        try
        {
            //clear out the old text
            doc.remove(0,doc.getLength());

            //add the new text
            doc.insertString(0, getInternationalizedPropertyText(), messagePanel.getStyle("regular"));

            doc.setParagraphAttributes(0, doc.getLength(),
                messagePanel.getStyle("centered"), false);
        }
        catch(BadLocationException e)
        {
            logger.error("Exception initializing text pane in DialogBean", e);
        }
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        // if no bean model, can't update fields
        if (beanModel != null)
        {
            if (Util.isEmpty(beanModel.getTitleTag()))
            {
                titleLabel.setText(getTitleText());
            }
            else
            {
                titleLabel.setText(retrieveText(beanModel.getTitleTag(), titleLabel));
            }
            screenName = getProperty(PROP_DESCRIPTION, "");
            initTextPane();

        }
    }

    /**
     * Gets a property from the properties object
     *
     * @param Property the property key.
     * @param Default the default to return if the key is not found.
     * @return String value found.
     */
    protected String getProperty(String property, String defaultText)
    {
        String val = null;
        // if no bean model, no text
        if (beanModel != null)
        {
            if (props != null)
            {
                val = props.getProperty(beanModel.getResourceID() + "." + property);
            }
        }
        // if properties not found, use default
        if (val == null)
        {
            val = defaultText;
        }
        return (val);
    }

    /**
     * Returns text for title label.
     *
     * @return text for title label
     */
    protected String getTitleText()
    {
        return (getProperty(PROP_TITLE, ""));
    }

    /**
     * Return dialog text, where the variable runtime arguments are numberic,
     * such as {0}. The number corresponds to the argument index in the args
     * array passed to the model. The dialog needs to be treated as one giant
     * screen, like this:
     * <pre>
     *   #
     *   # Dialog Screens
     *   #
     *   TEST_MESSAGE.title=This is a Test Message
     *   TEST_MESSAGE.description=Test Message
     *   TEST_MESSAGE.line1=line 1 of test message
     *   TEST_MESSAGE.line2=line 2 one arg {0} !!!!!
     *   TEST_MESSAGE.line3=line 3 two args {1} and {2} !!!!!
     *   #TEST_MESSAGE.line4=line 4 should be blank !!!!
     *   TEST_MESSAGE.line5=line 5 of test message
     *   TEST_MESSAGE.line6=<{3}
     *   TEST_MESSAGE.line7=line 7 of test message
     *   TEST_MESSAGE.line8=line 8 of test message
     * </pre>
     *
     * @return String with args converted to text
     */
    protected String getInternationalizedPropertyText()
    {
        String result = "";
        String[] argStrs = beanModel.getArgs();
        String valueStr = getProperty(PROP_LINE, "");
        result = LocaleUtilities.formatComplexMessage(valueStr, argStrs);

        return result;

    }

    /**
     * Gets the default button listener. This method performs a "lazy create" on
     * the listener.
     *
     * @return java.awt.event.ActionListener
     */
    protected ActionListener getDefaultBtnListener()
    {
        return new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                UISubsystem ui = UISubsystem.getInstance();
                String letter = evt.getActionCommand();

                if (beanModel.getUiGeneratedError())
                {
                    showLastScreen(true);
                }
                else if(beanModel.getUiGeneratedCancel())
                {
                    if(letter.equals(NO_LETTER))
                    {
                            showLastScreen(false);
                    }
                    else
                    {
                        ui.mail(new ButtonPressedLetter(CANCEL_LETTER), true);
                    }
                }
                else
                {
                    ui.mail(new ButtonPressedLetter(letter), true);
                }
            }
        };
    }

    /**
     * Add the screen name listener to this bean.
     *
     * @param listener the ScreenNameListener.
     */
    public void addScreenNameListener(ScreenNameListener listener)
    {
        screenNameListener = listener;
        if (screenName != null)
        {
            screenNameListener.setScreenName(screenName);
        }
    }

    /**
     * Add the screen name listener to this bean.
     *
     * @param listener the ScreenNameListener.
     */
    public void removeScreenNameListener(ScreenNameListener listener)
    {
        screenNameListener = null;
        screenName = null;
    }

    /**
     * Override JPanel set Visible to request focus.
     *
     * @param aFlag indicates if the component should be visible or not.
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        if (aFlag)
        {
            setCurrentFocus(defaultButton);
        }
    }

    /**
     * Retrieves the previous screen from the bean model and instructs the ui to
     * display it.
     *
     * @param needsModel whether the old model needs to be set
     */
    protected void showLastScreen(boolean needsModel)
    {
        POSJFCUISubsystem ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        String spec = beanModel.getFormScreenSpecName();

        try
        {
            // if the ui needs a bean model, call show screen with model
            if(needsModel)
            {
                POSBaseBeanModel model = beanModel.getFormModel();
                ui.showScreen(spec, model);
            }
            else
            {
                ui.showScreen(spec);
            }
        }
        catch (UIException uie)
        {
            logger.error("Unable to show last screen: " + spec, uie);
        }
        catch (ConfigurationException ce)
        {
            logger.error("Invalid configuration for last screen: " + spec, ce);
        }
    }

    /**
     * Add buttons to the panel based up model type
     */
    private void addButtons()
    {
        buttonPanel.removeAll();
        switch(beanModel.getType())
        {
            case DialogScreensIfc.SIGNATURE:
                addSignaturePanel(((SignatureDialogBeanModel)beanModel).getSignature());

            case DialogScreensIfc.CONFIRMATION:

                addButton(createButton(BUTTON_YES, true), 0);
                addButton(createButton(BUTTON_NO, false), 1);
                break;

            case DialogScreensIfc.ERROR:
                addButton(createButton(BUTTON_OK, true), 0);
                break;

            case DialogScreensIfc.ACKNOWLEDGEMENT:
                addButton(createButton(BUTTON_OK, true), 0);
                break;

            case DialogScreensIfc.RETRY_CONTINUE_CANCEL:
                addButton(createButton(BUTTON_RETRY, true), 0);
                addButton(createButton(BUTTON_CONTINUE, false), 1);
                addButton(createButton(BUTTON_CANCEL, false), 2);
                break;

            case DialogScreensIfc.RETRY_CONTINUE:
                addButton(createButton(BUTTON_RETRY, true), 0);
                addButton(createButton(BUTTON_CONTINUE, false), 1);
                break;

            case DialogScreensIfc.RETRY_CANCEL:
                addButton(createButton(BUTTON_RETRY, true), 0);
                addButton(createButton(BUTTON_CANCEL, false), 1);
                break;

            case DialogScreensIfc.CONTINUE_CANCEL:
                addButton(createButton(BUTTON_CONTINUE, false), 0);
                addButton(createButton(BUTTON_CANCEL, true), 1);
                break;

            case DialogScreensIfc.RETRY:
                addButton(createButton(BUTTON_RETRY, true), 0);
                break;

            case DialogScreensIfc.NOW_LATER:
                addButton(createButton(BUTTON_NOW, true), 0);
                addButton(createButton(BUTTON_LATER, false), 1);
                break;

           case DialogScreensIfc.UPDATE_CANCEL:
               addButton(createButton(BUTTON_UPDATE, true), 0);
               addButton(createButton(BUTTON_CANCEL, false), 1);
               break;

           case DialogScreensIfc.YES_NO:
               addButton(createButton(BUTTON_YES, true), 0);
               addButton(createButton(BUTTON_NO, false), 1);
               break;

           case DialogScreensIfc.PICKUP_SHIP:
               addButton(createButton(BUTTON_PICKUP, true), 0);
               addButton(createButton(BUTTON_SHIP, false), 1);
               addButton(createButton(BUTTON_ADD, false), 2);
               break;

           case DialogScreensIfc.PICKUP_DELIVER:
               addButton(createButton(BUTTON_PICKUP, true), 0);
               addButton(createButton(BUTTON_DELIVER, false), 1);
               addButton(createButton(BUTTON_ADD, false), 2);
               break;
               
           case DialogScreensIfc.SEARCHWEBSTORE_CANCEL:
               addButton(createButton(BUTTON_SEARCH_WEBSTORE, true), 0);
               addButton(createButton(BUTTON_CANCEL, false), 1);
               break;
               
           case DialogScreensIfc.SEARCHWEB_SIM_CANCEL:
               addButton(createButton(BUTTON_SEARCH_WEBSTORE, true), 0);
               addButton(createButton(BUTTON_SEARCH_SIM, false), 1);
               addButton(createButton(BUTTON_CANCEL, false), 2);
               break;
               
           case DialogScreensIfc.ONE_OR_MULTIPLE:
               addButton(createButton(BUTTON_ONE, true), 0);
               addButton(createButton(BUTTON_MULTIPLE, false), 1);
               break;

           case DialogScreensIfc.ERROR_NO_BUTTONS:
           case DialogScreensIfc.NO_RESPONSE:
               break;
               //Changes starts for Rev 1.1 (Ashish ; Edge)
           case MAXEdgeDialogScreensIfc.EDGE_CUSTOMER_INFORMATION:
				addButton(createButton(BUTTON_CUST_INFORMATION, false), 0);
				addButton(createButton(BUTTON_ENTER, true), 1);
				break;
				//Changes ends for Rev 1.1 (Ashish ; Edge)
				
           case MAXDialogScreensIfc.SBI_POINT_INFORMATION:
        	   System.out.println("937 MaxDialogBeanjava");
				addButton(createButton(BUTTON_CONVERTED, true), 0);
				addButton(createButton(BUTTON_TOTAL, false), 1);
				addButton(createButton(BUTTON_PARTIAL, false), 2);
				break;
           default:
                logger.error("Invalid dialog ID beanModel.getType(): " + beanModel.getType());
                break;
        }
    }

    /**
     * Update the existing buttons with new text.
     */
    private void updateButtonTextAndLetter()
    {
        switch(beanModel.getType())
        {
            case DialogScreensIfc.SIGNATURE:
                addSignaturePanel(((SignatureDialogBeanModel)beanModel).getSignature());

            case DialogScreensIfc.CONFIRMATION:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_YES[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_YES[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_NO[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_NO[BTN_ACTION]);
                break;

            case DialogScreensIfc.ERROR:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_OK[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_OK[BTN_ACTION]);
                break;

            case DialogScreensIfc.ACKNOWLEDGEMENT:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_OK[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_OK[BTN_ACTION]);
                break;

            case DialogScreensIfc.RETRY_CONTINUE_CANCEL:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_RETRY[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_RETRY[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_CONTINUE[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_CONTINUE[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(2)).setText(BUTTON_CANCEL[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(2)).setActionCommand(BUTTON_CANCEL[BTN_ACTION]);
                break;

            case DialogScreensIfc.RETRY_CONTINUE:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_RETRY[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_RETRY[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_CONTINUE[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_CONTINUE[BTN_ACTION]);
                break;

            case DialogScreensIfc.RETRY_CANCEL:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_RETRY[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_RETRY[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_CANCEL[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_CANCEL[BTN_ACTION]);
                break;

            case DialogScreensIfc.CONTINUE_CANCEL:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_CONTINUE[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_CONTINUE[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_CANCEL[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_CANCEL[BTN_ACTION]);
                break;

            case DialogScreensIfc.RETRY:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_RETRY[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_RETRY[BTN_ACTION]);
                break;

            case DialogScreensIfc.NOW_LATER:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_NOW[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_NOW[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_LATER[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_LATER[BTN_ACTION]);
                break;

           case DialogScreensIfc.UPDATE_CANCEL:
               ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_UPDATE[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_UPDATE[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_CANCEL[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_CANCEL[BTN_ACTION]);
               break;

           case DialogScreensIfc.YES_NO:
               ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_YES[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_YES[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_NO[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_NO[BTN_ACTION]);
               break;

           case DialogScreensIfc.PICKUP_SHIP:
               ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_PICKUP[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_PICKUP[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_SHIP[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_SHIP[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(2)).setText(BUTTON_ADD[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(2)).setActionCommand(BUTTON_ADD[BTN_ACTION]);
               break;

           case DialogScreensIfc.PICKUP_DELIVER:
               ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_PICKUP[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_PICKUP[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_DELIVER[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_DELIVER[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(2)).setText(BUTTON_ADD[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(2)).setActionCommand(BUTTON_ADD[BTN_ACTION]);
               break;
               
           case DialogScreensIfc.SEARCHWEBSTORE_CANCEL:
               ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_SEARCH_WEBSTORE[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_SEARCH_WEBSTORE[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_CANCEL[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_CANCEL[BTN_ACTION]);
               break;
               
           case DialogScreensIfc.SEARCHWEB_SIM_CANCEL:
               ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_SEARCH_WEBSTORE[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_SEARCH_WEBSTORE[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_SEARCH_SIM[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_SEARCH_SIM[BTN_ACTION]);
               ((JButton)buttonPanel.getComponent(2)).setText(BUTTON_CANCEL[BTN_NAME]);
               ((JButton)buttonPanel.getComponent(2)).setActionCommand(BUTTON_CANCEL[BTN_ACTION]);
               break;
               
            case DialogScreensIfc.ONE_OR_MULTIPLE:
                ((JButton)buttonPanel.getComponent(0)).setText(BUTTON_ONE[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_ONE[BTN_ACTION]);
                ((JButton)buttonPanel.getComponent(1)).setText(BUTTON_MULTIPLE[BTN_NAME]);
                ((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_MULTIPLE[BTN_ACTION]);
                break;
              //Changes starts for Rev 1.1 (Ashish ; Edge)
            case MAXEdgeDialogScreensIfc.EDGE_CUSTOMER_INFORMATION:
    			((JButton)buttonPanel.getComponent(0)).setText(BUTTON_CUST_INFORMATION[BTN_NAME]);
    			((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_CUST_INFORMATION[BTN_ACTION]);
    			((JButton)buttonPanel.getComponent(1)).setText(BUTTON_ENTER[BTN_NAME]);
    			((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_ENTER[BTN_ACTION]);
    			break;
    			//Changes ends for Rev 1.1 (Ashish ; Edge)
            case MAXDialogScreensIfc.SBI_POINT_INFORMATION:
    			((JButton)buttonPanel.getComponent(0)).setText(BUTTON_CONVERTED[BTN_NAME]);
    			((JButton)buttonPanel.getComponent(0)).setActionCommand(BUTTON_CONVERTED[BTN_ACTION]);
    			((JButton)buttonPanel.getComponent(1)).setText(BUTTON_TOTAL[BTN_NAME]);
    			((JButton)buttonPanel.getComponent(1)).setActionCommand(BUTTON_TOTAL[BTN_ACTION]);
    			((JButton)buttonPanel.getComponent(2)).setText(BUTTON_PARTIAL[BTN_NAME]);
    			((JButton)buttonPanel.getComponent(2)).setActionCommand(BUTTON_PARTIAL[BTN_ACTION]);
    			break;
        }
    }

    /**
     * Returns default display string.
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: DialogBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
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

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        DialogBean bean = new DialogBean();
        DialogBeanModel model = null;
        bean.setProps(UIUtilities.loadProperties("posUI.properties"));

        // if args exist, see if signature panel desired
        if (args.length == 1)
        {
            if (args[0].equals("Signature"))
            {
                model = new SignatureDialogBeanModel();
                model.setResourceID("VerifySignature");
                model.setType(DialogScreensIfc.SIGNATURE);
            }
        }

        if (model == null)
        {
            model = new DialogBeanModel();
            model.setResourceID("CustDelConfirm");
            model.setType(DialogScreensIfc.CONFIRMATION);
        }

        bean.setModel(model);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
