/* ===========================================================================
* Copyright (c) 2009, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OnScreenKeyboardBean.java /main/12 2014/01/13 13:13:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/10/14 - added main method for testing
 *    cgreene   04/30/13 - get preferred sizes from UI factory
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   01/21/10 - add mnemonic matching feature to trigger highlighted
 *                         button
 *    cgreene   01/14/10 - move space button one space to the right
 *    cgreene   01/14/10 - fix enter and tab key not working when SHIFTed
 *    cgreene   01/14/10 - added key pressed-released events for TAB key
 *    abondala  01/03/10 - update header date
 *    cgreene   12/16/09 - add Keys button
 *    cgreene   12/16/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import oracle.retail.stores.foundation.manager.gui.UIDialog;
import oracle.retail.stores.gui.layout.FloodConstraints;
import oracle.retail.stores.gui.layout.FloodLayout;
import oracle.retail.stores.gui.utility.RotatedTextIcon;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * A bean that shows various keys in three choose-able panels. Pressing these
 * keys sends {@link KeyEvent}s to the main window.
 * 
 * @author cgreene
 * @since 13.2
 */
public class OnScreenKeyboardBean extends BaseBeanAdapter implements ActionListener
{
    private static final long serialVersionUID = 8063360972973028895L;

    /** Keys used to configure component layout with uifactory. */
    public static final String OSK_UI_PREFIX = "OnScreenKeyboard";
    public static final String OSK_UI_BUTTON = "OnScreenKeyboard.button";
    public static final String OSK_UI_ROTATEDBUTTON = "OnScreenKeyboard.rotatedButton";
    /** The default bean name. */
    public static final String BEAN_NAME = "OnScreenKeyboardBean";
    /** Key used in client property of button to indicate the button text */
    public static final String LABEL = "label";
    /** Key used in client property of button to indicate upper case text */
    public static final String UPPER_CASE = "upperCase";
    /** Key used in client property of button to indicate the key char */
    public static final String KEY_CHAR = "keyChar";

    // toggle
    protected JPanel panelToggle;
    protected JButton btnAlpha;
    protected JButton btnNumbers;
    protected JButton btnSymbols;

    // numpad
    protected JPanel panelNumpad;
    protected JButton btn0;
    protected JButton btn1;
    protected JButton btn2;
    protected JButton btn3;
    protected JButton btn4;
    protected JButton btn5;
    protected JButton btn6;
    protected JButton btn7;
    protected JButton btn8;
    protected JButton btn9;
    protected JButton btnBackSpace_numpad;
    protected JButton btnEnter_numpad;

    // symbols
    protected JPanel panelSymbols;
    protected JButton btnParenthesisOpen;
    protected JButton btnParanthesisClose;
    protected JButton btnUnderscore;
    protected JButton btnAsterisk;
    protected JButton btnAmpersand;
    protected JButton btnCircumflex;
    protected JButton btnPercent;
    protected JButton btnNumberSign;
    protected JButton btnDollar;
    protected JButton btnPlus;
    protected JButton btnQuestion;
    protected JButton btnExclamationMark;
    protected JButton btnBackslash;
    protected JButton btnBar;
    protected JButton btnDoubleQuote;
    protected JButton btnComma;
    protected JButton btnEquals;
    protected JButton btnMinus;
    protected JButton btnAt_symbols;
    protected JButton btnSpace_symbols;
    protected JButton btnPeriod;
    protected JButton btnQuote;
    protected JButton btnSlash;
    protected JButton btnBraceOpen;
    protected JButton btnBraceClose;
    protected JButton btnBracketOpen;
    protected JButton btnBracketClose;
    protected JButton btnColon;
    protected JButton btnSemicolon;
    protected JButton btnBackSpace_symbols;
    protected JButton btnEnter_symbols;

    // alpha
    protected JPanel panelAlpha;
    protected JButton btnA;
    protected JButton btnAt;
    protected JButton btnB;
    protected JButton btnBackSpace;
    protected JButton btnC;
    protected JToggleButton btnCapsLock;
    protected JButton btnD;
    protected JButton btnDotCom;
    protected JButton btnE;
    protected JButton btnEnter;
    protected JButton btnF;
    protected JButton btnG;
    protected JButton btnH;
    protected JButton btnI;
    protected JButton btnJ;
    protected JButton btnK;
    protected JButton btnL;
    protected JButton btnM;
    protected JButton btnN;
    protected JButton btnO;
    protected JButton btnP;
    protected JButton btnQ;
    protected JButton btnR;
    protected JButton btnS;
    protected JToggleButton btnShift;
    protected JButton btnSpace;
    protected JButton btnT;
    protected JButton btnTab;
    protected JButton btnU;
    protected JButton btnV;
    protected JButton btnW;
    protected JButton btnX;
    protected JButton btnY;
    protected JButton btnZ;

    /** Reference to parent window so that we can pass events to it. */
    private Window window;

    /**
     * Default constructor.
     */
    public OnScreenKeyboardBean()
    {
        UI_PREFIX = OSK_UI_PREFIX;
    }

    /**
     * Implemented to convert and dispatch the generated {@link KeyEvent}s.
     * This try to dispatch to the parent of the owning window unless it is
     * <code>null</code> in which case, it will just send to the owning window.
     * 
     * @see #getWindow()
     * @see java.awt.Container#getParent()
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        // determine if its a back or enter button.
        AbstractButton btn = (AbstractButton)e.getSource();
        if (btn == btnCapsLock || btn == btnShift)
        {
            switchCase(panelAlpha.getComponents());
        }
        else
        {
            // get main window of dialog
            Window window = getWindow();
            if (window.getParent() instanceof Window)
            {
                window = (Window)window.getParent();
            }

            // get key char
            String keyCharString = (String)btn.getClientProperty(KEY_CHAR);
            if (btnShift.isSelected() || btnCapsLock.isSelected())
            {
                String upperCaseChar = (String)btn.getClientProperty(KEY_CHAR + "." + UPPER_CASE);
                if (upperCaseChar != null)
                {
                    keyCharString = upperCaseChar;
                }
            }

            // send each char in the string as an event.
            for (int i = 0; i < keyCharString.length(); i++)
            {
                // convert to key event and send
                window.dispatchEvent(convertEvent(e, keyCharString.charAt(i)));
            }

            // turn off shift button for any button press
            if (btnShift.isSelected())
            {
                btnShift.doClick();
            }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure()
    {
        // Intialize the panel
        setName(BEAN_NAME);
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());

        buildPanelAlpha();
        buildPanelNumpad();
        buildPanelSymbols();
        buildPanelToggle();

        add(panelToggle, BorderLayout.EAST);
        add(panelNumpad, BorderLayout.CENTER);
    }

    /**
     * Lazily initialize and return the reference to the immediate window in
     * which this bean is displayed.
     * 
     * @return
     */
    protected Window getWindow()
    {
        if (window == null)
        {
            // get a reference to our window
            window = SwingUtilities.getWindowAncestor(this);
        }
        return window;
    }

    /**
     * Convert the given action event into a key-typed event for sending to
     * the parent window.
     * 
     * @param action the action event to convert
     * @return
     */
    protected KeyEvent convertEvent(ActionEvent action, char keyChar)
    {
        // get modifiers
        int modifiers = 0;
        if (btnShift.isSelected() || btnCapsLock.isSelected())
        {
            modifiers = Event.SHIFT_MASK;
        }

        // get widget
        Window window = getWindow();
        if (window.getParent() instanceof Window)
        {
            window = (Window)window.getParent();
        }
        Component focusedWidget = window.getMostRecentFocusOwner();

        // special case if Enter; send key pressed/release
        AbstractButton btn = (AbstractButton)action.getSource();
        if (btn == btnEnter || btn == btnEnter_numpad || btn == btnEnter_symbols)
        {
            window.dispatchEvent(new KeyEvent(focusedWidget,
                    KeyEvent.KEY_PRESSED,
                    action.getWhen(),
                    0, // no modifiers
                    KeyEvent.VK_ENTER,
                    keyChar));
            return new KeyEvent(focusedWidget,
                    KeyEvent.KEY_RELEASED,
                    action.getWhen(),
                    0, // no modifiers
                    KeyEvent.VK_ENTER,
                    keyChar);
        }

        // special case if tab; send key pressed/release
        if (btn == btnTab)
        {
            window.dispatchEvent(new KeyEvent(focusedWidget,
                    KeyEvent.KEY_PRESSED,
                    action.getWhen(),
                    0, // no modifiers
                    KeyEvent.VK_TAB,
                    keyChar));
            return new KeyEvent(focusedWidget,
                    KeyEvent.KEY_RELEASED,
                    action.getWhen(),
                    0, // no modifiers
                    KeyEvent.VK_TAB,
                    keyChar);
        }

        // if widget is button and key matches mnemonic, press it
        if (focusedWidget instanceof AbstractButton)
        {
            AbstractButton button = (AbstractButton)focusedWidget;
            int mnem = button.getMnemonic();
            if (mnem == keyChar || mnem == keyChar - 32)
            {
                button.doClick();
            }
        }

        // create event
        KeyEvent e = new KeyEvent(focusedWidget,
                KeyEvent.KEY_TYPED,
                action.getWhen(),
                modifiers,
                KeyEvent.VK_UNDEFINED,
                keyChar);
        return e;
    }

    /**
     * Make the component related to the specified button visible in the center
     * of this bean.
     * 
     * @param source either <code>btnAlpha</code>, <code>panelNumpad</code> or <code>panelSymbols</code>
     */
    protected void switchView(JButton source)
    {
        // get the component to switch out
        Component removingPanel = getComponent(1);

        // get reference to owning window
        Window dialog = getWindow();

        // calculate how much to move the window after switch
        Point loc = dialog.getLocation();//OnScreen();
        loc.x += removingPanel.getWidth();
        loc.y += removingPanel.getHeight();

        // determine the panel being inserted
        JPanel newPanel = panelNumpad;
        if (source == btnAlpha)
        {
            newPanel = panelAlpha;
        }
        else if (source == btnSymbols)
        {
            newPanel = panelSymbols;
        }

        // remove the "removingPanel"
        remove(1);
        removingPanel.setVisible(false);
        add(newPanel, BorderLayout.CENTER);
        newPanel.setVisible(true);

        // reset our location
        dialog.pack();
        loc.x -= newPanel.getWidth();
        loc.y -= newPanel.getHeight();
        dialog.setLocation(loc);
    }

    /**
     * 
     */
    protected void buildPanelAlpha()
    {
        panelAlpha = new JPanel(new FloodLayout(3, 12, 1, 1));
        panelAlpha.setName("panelAlpha");
        panelAlpha.setPreferredSize(uiFactory.getDimension(UI_PREFIX + ".panelAlpha"));

        // row 1
        {
            btnTab = uiFactory.createButton("Tab", "Tab", null, OSK_UI_BUTTON);
            panelAlpha.add(btnTab);
        }
        {
            btnQ = uiFactory.createButton("Q", "q", null, OSK_UI_BUTTON);
            panelAlpha.add(btnQ);
        }
        {
            btnW = uiFactory.createButton("W", "w", null, OSK_UI_BUTTON);
            panelAlpha.add(btnW);
        }
        {
            btnE = uiFactory.createButton("E", "e", null, OSK_UI_BUTTON);
            panelAlpha.add(btnE);
        }
        {
            btnR = uiFactory.createButton("R", "r", null, OSK_UI_BUTTON);
            panelAlpha.add(btnR);
        }
        {
            btnT = uiFactory.createButton("T", "t", null, OSK_UI_BUTTON);
            panelAlpha.add(btnT);
        }
        {
            btnY = uiFactory.createButton("Y", "y", null, OSK_UI_BUTTON);
            panelAlpha.add(btnY);
        }
        {
            btnU = uiFactory.createButton("U", "u", null, OSK_UI_BUTTON);
            panelAlpha.add(btnU);
        }
        {
            btnI = uiFactory.createButton("I", "i", null, OSK_UI_BUTTON);
            panelAlpha.add(btnI);
        }
        {
            btnO = uiFactory.createButton("O", "o", null, OSK_UI_BUTTON);
            panelAlpha.add(btnO);
        }
        {
            btnP = uiFactory.createButton("P", "p", null, OSK_UI_BUTTON);
            panelAlpha.add(btnP);
        }
        {
            btnBackSpace = uiFactory.createButton("BackSpace", "Back", null, OSK_UI_BUTTON);
            panelAlpha.add(btnBackSpace);
        }
        
        // row 2
        {
            btnCapsLock = uiFactory.createToggleButton("CapsLock", "Caps", null, OSK_UI_BUTTON);
            panelAlpha.add(btnCapsLock);
        }
        {
            btnA = uiFactory.createButton("A", "a", null, OSK_UI_BUTTON);
            panelAlpha.add(btnA);
        }
        {
            btnS = uiFactory.createButton("S", "s", null, OSK_UI_BUTTON);
            panelAlpha.add(btnS);
        }
        {
            btnD = uiFactory.createButton("D", "d", null, OSK_UI_BUTTON);
            panelAlpha.add(btnD);
        }
        {
            btnF = uiFactory.createButton("F", "f", null, OSK_UI_BUTTON);
            panelAlpha.add(btnF);
        }
        {
            btnG = uiFactory.createButton("G", "g", null, OSK_UI_BUTTON);
            panelAlpha.add(btnG);
        }
        {
            btnH = uiFactory.createButton("H", "h", null, OSK_UI_BUTTON);
            panelAlpha.add(btnH);
        }
        {
            btnJ = uiFactory.createButton("J", "j", null, OSK_UI_BUTTON);
            panelAlpha.add(btnJ);
        }
        {
            btnK = uiFactory.createButton("K", "k", null, OSK_UI_BUTTON);
            panelAlpha.add(btnK);
        }
        {
            btnL = uiFactory.createButton("L", "l", null, OSK_UI_BUTTON);
            panelAlpha.add(btnL);
        }
        {
            btnDotCom = uiFactory.createButton("DotCom", ".com", null, OSK_UI_BUTTON);
            panelAlpha.add(btnDotCom);
        }
        {
            btnEnter = uiFactory.createButton("Enter", "Enter", null, OSK_UI_BUTTON);
            panelAlpha.add(btnEnter, new FloodConstraints(0, 1));
        }
        
        // row 3
        {
            btnShift = uiFactory.createToggleButton("Shift", "Shift", null, OSK_UI_BUTTON);
            panelAlpha.add(btnShift);
        }
        {
            btnZ = uiFactory.createButton("Z", "z", null, OSK_UI_BUTTON);
            panelAlpha.add(btnZ);
        }
        {
            btnX = uiFactory.createButton("X", "x", null, OSK_UI_BUTTON);
            panelAlpha.add(btnX);
        }
        {
            btnC = uiFactory.createButton("C", "c", null, OSK_UI_BUTTON);
            panelAlpha.add(btnC);
        }
        {
            btnV = uiFactory.createButton("V", "v", null, OSK_UI_BUTTON);
            panelAlpha.add(btnV);
        }
        {
            btnSpace = uiFactory.createButton("Space", "Space", null, OSK_UI_BUTTON);
            panelAlpha.add(btnSpace, new FloodConstraints(1, 0));
        }
        {
            btnB = uiFactory.createButton("B", "b", null, OSK_UI_BUTTON);
            panelAlpha.add(btnB);
        }
        {
            btnN = uiFactory.createButton("N", "n", null, OSK_UI_BUTTON);
            panelAlpha.add(btnN);
        }
        {
            btnM = uiFactory.createButton("M", "m", null, OSK_UI_BUTTON);
            panelAlpha.add(btnM);
        }
        {
            btnAt = uiFactory.createButton("At", "@", null, OSK_UI_BUTTON);
            panelAlpha.add(btnAt);
        }
    }

    /**
     * 
     */
    protected void buildPanelNumpad()
    {
        panelNumpad = new JPanel(new FloodLayout(4, 3, 1, 1));
        panelNumpad.setName("panelNumpad");
        panelNumpad.setPreferredSize(uiFactory.getDimension(UI_PREFIX + ".panelNumpad"));

        {
            btn7 = uiFactory.createButton("7", "7", null, OSK_UI_BUTTON);
            panelNumpad.add(btn7);
        }
        {
            btn8 = uiFactory.createButton("8", "8", null, OSK_UI_BUTTON);
            panelNumpad.add(btn8);
        }
        {
            btn9 = uiFactory.createButton("9", "9", null, OSK_UI_BUTTON);
            panelNumpad.add(btn9);
        }
        {
            btn4 = uiFactory.createButton("4", "4", null, OSK_UI_BUTTON);
            panelNumpad.add(btn4);
        }
        {
            btn5 = uiFactory.createButton("5", "5", null, OSK_UI_BUTTON);
            panelNumpad.add(btn5);
        }
        {
            btn6 = uiFactory.createButton("6", "6", null, OSK_UI_BUTTON);
            panelNumpad.add(btn6);
        }
        {
            btn1 = uiFactory.createButton("1", "1", null, OSK_UI_BUTTON);
            panelNumpad.add(btn1);
        }
        {
            btn2 = uiFactory.createButton("2", "2", null, OSK_UI_BUTTON);
            panelNumpad.add(btn2);
        }
        {
            btn3 = uiFactory.createButton("3", "3", null, OSK_UI_BUTTON);
            panelNumpad.add(btn3);
        }
        {
            btnBackSpace_numpad = uiFactory.createButton("BackSpace_numpad", "Back", null, OSK_UI_BUTTON);
            panelNumpad.add(btnBackSpace_numpad);
        }
        {
            btn0 = uiFactory.createButton("0", "0", null, OSK_UI_BUTTON);
            panelNumpad.add(btn0);
        }
        {
            btnEnter_numpad = uiFactory.createButton("Enter_numpad", "Enter", null, OSK_UI_BUTTON);
            panelNumpad.add(btnEnter_numpad);
        }
    }

    /**
     * 
     */
    protected void buildPanelSymbols()
    {
        panelSymbols = new JPanel(new FloodLayout(3, 11, 1, 1));
        panelSymbols.setName("panelSymbols");
        panelSymbols.setPreferredSize(uiFactory.getDimension(UI_PREFIX + ".panelSymbols"));

        // row 1
        {
            btnExclamationMark = uiFactory.createButton("ExclamationMark", "!", null, OSK_UI_BUTTON);//1
            panelSymbols.add(btnExclamationMark);
        }
        {
            btnAt_symbols = uiFactory.createButton("At_symbols", "@", null, OSK_UI_BUTTON);//2
            panelSymbols.add(btnAt_symbols);
        }
        {
            btnNumberSign = uiFactory.createButton("NumberSign", "#", null, OSK_UI_BUTTON);//3
            panelSymbols.add(btnNumberSign);
        }
        {
            btnDollar = uiFactory.createButton("Dollar", "$", null, OSK_UI_BUTTON);//4
            panelSymbols.add(btnDollar);
        }
        {
            btnPercent = uiFactory.createButton("Percent", "%", null, OSK_UI_BUTTON);//5
            panelSymbols.add(btnPercent);
        }
        {
            btnCircumflex = uiFactory.createButton("Circumflex", "^", null, OSK_UI_BUTTON);//6
            panelSymbols.add(btnCircumflex);
        }
        {
            btnAmpersand = uiFactory.createButton("Ampersand", "&", null, OSK_UI_BUTTON);//7
            panelSymbols.add(btnAmpersand);
        }
        {
            btnAsterisk = uiFactory.createButton("Asterisk", "*", null, OSK_UI_BUTTON);//8
            panelSymbols.add(btnAsterisk);
        }
        {
            btnParenthesisOpen = uiFactory.createButton("ParenthesisOpen", "(", null, OSK_UI_BUTTON);//9
            panelSymbols.add(btnParenthesisOpen);
        }
        {
            btnParanthesisClose = uiFactory.createButton("ParenthesisClose", ")", null, OSK_UI_BUTTON);//10
            panelSymbols.add(btnParanthesisClose);
        }
        {
            btnBackSpace_symbols = uiFactory.createButton("BackSpace_symbols", "Back", null, OSK_UI_BUTTON);//11
            panelSymbols.add(btnBackSpace_symbols);
        }

        // row 2
        {
            btnBraceOpen = uiFactory.createButton("BraceOpen", "{", null, OSK_UI_BUTTON);
            panelSymbols.add(btnBraceOpen);//1
        }
        {
            btnBraceClose = uiFactory.createButton("BraceClose", "}", null, OSK_UI_BUTTON);
            panelSymbols.add(btnBraceClose);//2
        }
        {
            btnBracketOpen = uiFactory.createButton("BracketOpen", "[", null, OSK_UI_BUTTON);//3
            panelSymbols.add(btnBracketOpen);
        }
        {
            btnBracketClose = uiFactory.createButton("BracketClose", "]", null, OSK_UI_BUTTON);//4
            panelSymbols.add(btnBracketClose);
        }
        {
            btnColon = uiFactory.createButton("Colon", ":", null, OSK_UI_BUTTON);//5
            panelSymbols.add(btnColon);
        }
        {
            btnSemicolon = uiFactory.createButton("Semicolon", ";", null, OSK_UI_BUTTON);//6
            panelSymbols.add(btnSemicolon);
        }
        {
            btnBar = uiFactory.createButton("Bar", "|", null, OSK_UI_BUTTON);
            panelSymbols.add(btnBar);
        }
        {
            btnEquals = uiFactory.createButton("Equals", "=", null, OSK_UI_BUTTON);//8
            panelSymbols.add(btnEquals);
        }
        {
            btnPlus = uiFactory.createButton("Plus", "+", null, OSK_UI_BUTTON);//9
            panelSymbols.add(btnPlus);
        }
        {
            btnMinus = uiFactory.createButton("Minus", "-", null, OSK_UI_BUTTON);//10
            panelSymbols.add(btnMinus);
        }
        {
            btnEnter_symbols = uiFactory.createButton("Enter_symbols", "Enter", null, OSK_UI_BUTTON);//11
            panelSymbols.add(btnEnter_symbols, new FloodConstraints(0, 1));
        }

        // row 3"-
        {
            btnComma = uiFactory.createButton("Comma", ",", null, OSK_UI_BUTTON);//1
            panelSymbols.add(btnComma);
        }
        {
            btnPeriod = uiFactory.createButton("Period", ".", null, OSK_UI_BUTTON);//2
            panelSymbols.add(btnPeriod);
        }
        {
            btnQuestion = uiFactory.createButton("Question", "?", null, OSK_UI_BUTTON);//3
            panelSymbols.add(btnQuestion);
        }
        {
            btnUnderscore = uiFactory.createButton("Underscore", "_", null, OSK_UI_BUTTON);
            panelSymbols.add(btnUnderscore);
        }
        {
            btnSpace_symbols = uiFactory.createButton("Space_symbols", "Space", null, OSK_UI_BUTTON);//5,6
            panelSymbols.add(btnSpace_symbols, new FloodConstraints(1, 0));
        }
        {
            btnSlash = uiFactory.createButton("Slash", "/", null, OSK_UI_BUTTON);//7
            panelSymbols.add(btnSlash);
        }
        {
            btnBackslash = uiFactory.createButton("BackSlash", "\\", null, OSK_UI_BUTTON);//8
            panelSymbols.add(btnBackslash);
        }
        {
            btnQuote = uiFactory.createButton("Quote", "'", null, OSK_UI_BUTTON);//9
            panelSymbols.add(btnQuote);
        }
        {
            btnDoubleQuote = uiFactory.createButton("DoubleQuote", "\"", null, OSK_UI_BUTTON);//10
            panelSymbols.add(btnDoubleQuote);
        }
    }

    /**
     * 
     */
    protected void buildPanelToggle()
    {
        panelToggle = new JPanel();
        panelToggle.setName("panelToggle");
        panelToggle.setPreferredSize(uiFactory.getDimension(UI_PREFIX + ".panelToggle"));
        GridBagLayout panelToggleLayout = new GridBagLayout();
        panelToggle.setFocusable(false);
        panelToggleLayout.rowWeights = new double[] {0.1, 0.1, 0.1};
        panelToggleLayout.columnWeights = new double[] {0.1};

        ActionListener toggleAction = new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                switchView((JButton)evt.getSource());
            }
        };

        panelToggle.setLayout(panelToggleLayout);
        {
            btnNumbers = uiFactory.createButton("Numpad", "", null, OSK_UI_ROTATEDBUTTON);
            panelToggle.add(btnNumbers, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, 
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
                    new Insets(1, 1, 1, 1), 0, 0));
            btnNumbers.setIcon(new RotatedTextIcon(btnNumbers.getFont(), "123"));
            btnNumbers.setMargin(uiFactory.getInsets("rotatedTextMargin"));
            btnNumbers.addActionListener(toggleAction);
        }
        {
            btnAlpha = uiFactory.createButton("Alpha", "", null, OSK_UI_ROTATEDBUTTON);
            panelToggle.add(btnAlpha, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, 
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
                    new Insets(1, 1, 1, 1), 0, 0));
            btnAlpha.setIcon(new RotatedTextIcon(btnAlpha.getFont(), "ABC"));
            btnAlpha.setMargin(uiFactory.getInsets("rotatedTextMargin"));
            btnAlpha.addActionListener(toggleAction);
        }
        {
            btnSymbols = uiFactory.createButton("Symbols", "", null, OSK_UI_ROTATEDBUTTON);
            panelToggle.add(btnSymbols, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, 
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH, 
                    new Insets(1, 1, 1, 1), 0, 0));
            btnSymbols.setIcon(new RotatedTextIcon(btnSymbols.getFont(), "Symbols"));
            btnSymbols.setMargin(uiFactory.getInsets("rotatedTextMargin"));
            btnSymbols.addActionListener(toggleAction);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updatePropertyFields()
     */
    @Override
    protected void updatePropertyFields()
    {     
        super.updatePropertyFields();
        configureComponents(panelNumpad.getComponents(), false);
        configureComponents(panelAlpha.getComponents(), true);
        configureComponents(panelSymbols.getComponents(), false);
        btnNumbers.setIcon(new RotatedTextIcon(btnNumbers.getFont(), retrieveText(btnNumbers.getName() + "." + LABEL, "123")));
        btnAlpha.setIcon(new RotatedTextIcon(btnAlpha.getFont(), retrieveText(btnAlpha.getName() + "." + LABEL, "ABC")));
        btnSymbols.setIcon(new RotatedTextIcon(btnSymbols.getFont(), retrieveText(btnSymbols.getName() + "." + LABEL, "Symbols")));
    }

    /**
     * Internal method to set all buttons to the same margin and action listener.
     * 
     * @param comps
     */
    protected void configureComponents(Component[] comps, boolean includeUppercase)
    {
        for (int i = comps.length - 1; i >= 0; i--)
        {
            if (comps[i] instanceof AbstractButton)
            {
                AbstractButton btn = (AbstractButton)comps[i];
                btn.setActionCommand(btn.getText());
                btn.addActionListener(this);
                btn.setFocusable(false);
                btn.setMargin(uiFactory.getInsets("emptyInsets"));
                // get text for button
                String key = btn.getName() + "." + LABEL;
                String defaultText = btn.getText();
                String text = retrieveText(key, defaultText);
                btn.setText(text);
                btn.putClientProperty(LABEL, text);
                // get key char for button
                key = btn.getName() + "." + KEY_CHAR;
                defaultText = btn.getText();
                String keyChar = retrieveText(key, defaultText);
                btn.putClientProperty(KEY_CHAR, keyChar);
                // get uppercase text
                if (includeUppercase)
                {
                    key = btn.getName() + "." + LABEL + "." + UPPER_CASE;
                    text = retrieveTextFallbackToDefault(key , null);
                    if (text == null)
                    {
                        continue;
                    }
                    btn.putClientProperty(LABEL + "." + UPPER_CASE, text);
                    // get upper case key char
                    key = btn.getName() + "." + KEY_CHAR + "." + UPPER_CASE;
                    defaultText = btn.getText().toUpperCase();
                    keyChar = retrieveText(key, defaultText);
                    btn.putClientProperty(KEY_CHAR + "." + UPPER_CASE, keyChar);
                }
            }
        }
    }

    /**
     * Internal method to set all buttons to the same margin and action listener.
     * This switches all 26 English letter-based buttons.
     * 
     * @param comps
     */
    protected void switchCase(Component[] comps)
    {
        for (int i = comps.length - 1; i >= 0; i--)
        {
            if (comps[i] instanceof AbstractButton)
            {
                AbstractButton btn = (AbstractButton)comps[i];
                String textKey = LABEL;
                if (btnCapsLock.isSelected() || btnShift.isSelected())
                {
                    textKey = textKey + "." + UPPER_CASE;
                }
                String text = (String)btn.getClientProperty(textKey);
                if (text == null)
                {
                    continue;
                }
                btn.setText(text);
            }
        }
    }

    /**
     * Test look of this bean method.
     *
     * @param args
     */
    public static void main (String[] args)
    {
        UIUtilities.setUpTest();

        POSFocusManager pfm = new POSFocusManager();
        java.awt.DefaultKeyboardFocusManager.setCurrentKeyboardFocusManager(pfm);

        OnScreenKeyboardBean bean = new OnScreenKeyboardBean();
        bean.configure();
        UIDialog dialog = new UIDialog(new javax.swing.JFrame(), false);
        dialog.add(bean);
        dialog.setSize(625, 140);
        dialog.setVisible(true);
    }
}
