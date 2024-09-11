/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DisplayTextBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:41 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.4  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/05 14:39:37  baa
 *   @scr 3561  Returns
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
    This class displays the journal display screen.
    It is used with the DisplayTextBeanModel class. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel
**/
//----------------------------------------------------------------------------
public class DisplayTextBean extends BaseBeanAdapter
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** the bean name **/
    protected String beanName = "DisplayTextBean";

    /** The bean model **/
    protected DisplayTextBeanModel beanModel = new DisplayTextBeanModel();

    /** The main panel **/
    protected JScrollPane displayTextPane = null;

    /** The area to display the text - transaction **/
    protected JTextArea displayTextArea = null;

    /** The scroll listener **/
    protected ScrollListener listener = new ScrollListener();

    //---------------------------------------------------------------------
    /**
       Default class Constructor and initializes its components.
     **/
    //---------------------------------------------------------------------
    public DisplayTextBean()
    {
        super();
        initialize();
    }
    //---------------------------------------------------------------------
    /**
       Initialize the class and its screen members.
     **/
    //---------------------------------------------------------------------
    protected void initialize()
    {
        // Intialize the panel
        setName("DisplayTextBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //---------------------------------------------------------------------
    /**
       Initializes the components.
     **/
    //---------------------------------------------------------------------
    protected void initComponents()
    {
        displayTextPane = new JScrollPane();
        uiFactory.configureUIComponent(displayTextPane, "DisplayTextPane");

        displayTextArea = createTextArea();
    }

    //---------------------------------------------------------------------
    /**
       Initializes the layout and lays out the components.
     **/
    //---------------------------------------------------------------------
    protected void initLayout()
    {
        setLayout(new BorderLayout(1,1));

        displayTextPane.setViewportView(displayTextArea);
        add(displayTextPane, BorderLayout.CENTER);
    }

    /**
     *  Creates the text area to be display
     * @return
     */
    protected JTextArea createTextArea()
    {
        JTextArea area = new JTextArea();
        uiFactory.configureUIComponent(area, "DisplayTextArea");
        area.setBorder(UIManager.getBorder("DisplayTextArea.border"));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEnabled(true);
        return area;
    }

    //---------------------------------------------------------------------
    /**
       Gets the POSBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean
   **/
    //---------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }
    //---------------------------------------------------------------------
    /**
       Activate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        updateBean();
        activateListeners();
    }
    //---------------------------------------------------------------------
    /**
       Deactivate this screen and listeners.
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        deactivateListeners();
    }
    //---------------------------------------------------------------------
    /**
       Activate listeners
       NOTE: These keys are handled internally, i.e., this
       bean will not register with anyone else.
     **/
    //---------------------------------------------------------------------
    protected void activateListeners()
    {
        KeyStroke k = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
        displayTextPane.registerKeyboardAction(listener, "DOWN", k, JComponent.WHEN_IN_FOCUSED_WINDOW);

        k = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
        displayTextPane.registerKeyboardAction(listener, "UP", k, JComponent.WHEN_IN_FOCUSED_WINDOW);

        k = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0);
        displayTextPane.registerKeyboardAction(listener, "PAGE_DOWN", k, JComponent.WHEN_IN_FOCUSED_WINDOW);

        k = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0);
        displayTextPane.registerKeyboardAction(listener, "PAGE_UP", k, JComponent.WHEN_IN_FOCUSED_WINDOW);

        k = KeyStroke.getKeyStroke(KeyEvent.VK_END, 0);
        displayTextPane.registerKeyboardAction(listener, "END", k, JComponent.WHEN_IN_FOCUSED_WINDOW);

        k = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0);
        displayTextPane.registerKeyboardAction(listener, "HOME", k, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        displayTextPane.addFocusListener(this);
    }
    //---------------------------------------------------------------------
    /**
       Deactivate key listeners
     **/
    //---------------------------------------------------------------------
    protected void deactivateListeners()
    {
        displayTextPane.resetKeyboardActions();
        displayTextPane.removeFocusListener(this);
    }
    //---------------------------------------------------------------------
    /**
       Listener class that handles the keystrokes for scrolling
     **/
    //---------------------------------------------------------------------
    protected class ScrollListener implements ActionListener
    {

        /**
         *  (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        public void actionPerformed(ActionEvent e)
        {
            JScrollBar bar = displayTextPane.getVerticalScrollBar();
            if (e.getActionCommand().equals("DOWN"))
            {
                bar.setValue(bar.getValue() +
                             bar.getUnitIncrement(-1));
            }
            else if (e.getActionCommand().equals("UP"))
            {
                bar.setValue(bar.getValue() -
                             bar.getUnitIncrement(1));
            }
            else if (e.getActionCommand().equals("PAGE_DOWN"))
            {
                bar.setValue(bar.getValue() +
                             bar.getBlockIncrement(-1));
            }
            else if (e.getActionCommand().equals("PAGE_UP"))
            {
                bar.setValue(bar.getValue() -
                             bar.getBlockIncrement(1));
            }
            else if (e.getActionCommand().equals("END"))
            {
                bar.setValue(bar.getMaximum());
            }
            else if (e.getActionCommand().equals("HOME"))
            {
                bar.setValue(bar.getMinimum());
            }
        }
    }
    //---------------------------------------------------------------------
    /**
       Set the text to be displayed
       @param value
     **/
    //---------------------------------------------------------------------
    public void setDisplayText(String value)
    {
        if (value == null)
            value = new String(" ");

        displayTextArea.setText(value);
    }
    //---------------------------------------------------------------------
    /**
       Sets the current model to the updated model..
       @param model UIModelIfc A DisplayTextBeanModel
       @see oracle.retail.stores.pos.ui.beans.DisplayTextBeanModel
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set DisplayTextBean model to null.");
        }
        else
        {
            if (model instanceof DisplayTextBeanModel)
            {
                beanModel = (DisplayTextBeanModel)model;
                updateBean();
            }
        }
    }
    //---------------------------------------------------------------------
    /**
       Update the bean with fresh data
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        setDisplayText(beanModel.getDisplayText());
        displayTextArea.setCaretPosition(0);
    }

    //---------------------------------------------------------------------
    /**
       Set the focus for the screen.
       @param aFlag
    **/
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        if (aFlag)
        {
            displayTextArea.setCaretPosition(0);
            setCurrentFocus(displayTextPane);
        }
    }
    //---------------------------------------------------------------------
    /**
       Returns default display string.
       <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: DisplayTextBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }
    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        DisplayTextBean bean = new DisplayTextBean();

        StringBuffer text = new StringBuffer("This is some text. ");
        text.append("And this is some more text. There is still more text. ");
        text.append("Even more text.");

        bean.setDisplayText(text.toString());

        UIUtilities.doBeanTest(bean);
    }

}
