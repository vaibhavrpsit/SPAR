/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DialogNavigationButtonBean.java /main/12 2013/12/04 10:52:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   11/27/13 - override addNotify so that we are more likely to
 *                         have access to the rootPane
 *    cgreene   11/19/13 - trigger single button if user escapes from dialog
 *    cgreene   11/18/13 - massaged rootpane's input map to only fire button on
 *                         key release so that release event doesn't get sent
 *                         to main appContainer.
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    cgreene   07/02/10 - override activate in order to set defaul tbutton
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   12/07/09 - set maxButtons value
 *    cgreene   09/17/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import oracle.retail.stores.foundation.manager.gui.ButtonSpec;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.behavior.CancelActionListener;
import oracle.retail.stores.pos.ui.behavior.ConfirmActionListener;

/**
 * A bean intended to display buttons in a horizontal panel in a popup dialog.
 * 
 * @author cgreene
 * @since 13.1
 */
public class DialogNavigationButtonBean extends NavigationButtonBean
{
    private static final long serialVersionUID = 8186003753860984985L;

    protected CancelActionListener cancelActionListener = new CancelActionListener();
    protected ConfirmActionListener confirmActionListener = new ConfirmActionListener();

    /**
     * Constructor. Sets orientation to {@link SwingConstants#HORIZONTAL}, the
     * buttonPrefix to "HorizontalButton" and a center-justified
     * {@link FlowLayout}.
     */
    public DialogNavigationButtonBean()
    {
        setOrientation(HORIZONTAL);
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 5));
    }

    /**
     * Constructor. Calls this() then {@link #initialize(UIAction[][])}.
     * 
     * @param actions
     */
    public DialogNavigationButtonBean(UIAction[][] actions)
    {
        this();
        initialize(actions);
    }

     /**
      * Overridden to call {@link @installDefaultActions()} when this component
      * is added to the rootpane.
      *
      * @see javax.swing.JComponent#addNotify()
      */
    @Override
    public void addNotify()
    {
        super.addNotify();
        if (logger.isDebugEnabled())
        {
            logger.debug("addNotify called. Parent is " + getParent());
        }

        installDefaultActions();
    }

    /**
     * Overridden to always set the {@link #maxButtons} to the exact length
     * of the incoming buttonSpecs array.
     *
     * @see oracle.retail.stores.pos.ui.beans.NavigationButtonBean#configureButtons(oracle.retail.stores.foundation.manager.gui.ButtonSpec[])
     */
    @Override
    public void configureButtons(ButtonSpec[] buttonSpecs)
    {
        maxButtons = buttonSpecs.length;
        super.configureButtons(buttonSpecs);
    }

    /**
     * Overridden in order to set custom letters onto the dialog buttons.
     *
     * @see oracle.retail.stores.pos.ui.beans.NavigationButtonBean#setModel(oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        super.setModel(model);
        if (model instanceof DialogBeanModel)
        {
            // tweak the button letters
            DialogBeanModel dialogModel = (DialogBeanModel)model;
            for (int i = 0; i < buttons[0].length; i++)
            {
                buttons[0][i].putClientProperty("letter", dialogModel.getLetters()[i]);
            }
        }
        else
        {
            for (int i = 0; i < buttons[0].length; i++)
            {
                buttons[0][i].putClientProperty("letter", null);
            }
        }
    }

    /**
     * Calls {@link JRootPane#setDefaultButton(javax.swing.JButton)}
     * on the first button in this panel. If this panel only has one button, any
     * press of the {@link KeyEvent#VK_ESCAPE} key will just trigger
     * {@link UISubsystem#closeDialog()}, else if any of the added buttons have
     * "Cancel" as its actionCommand, then that button will be triggered instead
     * when Escape is pressed.
     * 
     * @since 14.0
     */
    @SuppressWarnings("serial")
    protected void installDefaultActions()
    {
        logger.debug("installing default actions...");

        // get the dialog's rootpane.
        JRootPane rootPane = SwingUtilities.getRootPane(this);
        if (rootPane == null)
        {
            logger.warn("Could not find rootPane of dialog in order to set defaultButton.");
            return;
        }

        // set the first button as the default button.
        for (Component comp : getComponents())
        {
            if (comp instanceof JButton)
            {
                installDefaultButton(rootPane, (JButton)comp);
                break;
            }
        }

        // install cancel operation
        InputMap iMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true), "escape");
  
        ActionMap aMap = rootPane.getActionMap();
        aMap.put("escape", new AbstractAction()
        {
            public void actionPerformed(ActionEvent e)
            {
                handleEscapeAction(e);
            }
        });
    }

    /**
     * Set this button as the default button of this root pane. Extra effort is
     * made to only trigger this button when it is release.
     *
     * @param rootPane
     * @param button
     */
    protected void installDefaultButton(JRootPane rootPane, JButton button)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Setting default button as " + button);
        }
        rootPane.setDefaultButton(button);
        button.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "none");
        // remove the binding for pressed
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("ENTER"), "none");
        // retarget the binding for released
        rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke("released ENTER"), "press");
        rootPane.getInputMap(JComponent.WHEN_FOCUSED)
            .put(KeyStroke.getKeyStroke("released ENTER"), "press");
    }

    /**
     * Handle the escape key pressed and triggered by the root pane's action
     * map.
     *
     * @param e
     */
    protected void handleEscapeAction(ActionEvent e)
    {
        // click the cancel button
        if (getComponentCount() > 1)
        {
            for (int i = 0; i < getComponentCount(); i++)
            {
                if (getComponent(i) instanceof JButton)
                {
                    JButton button = (JButton)getComponent(i);
                    if (CommonActionsIfc.CANCEL.equals(button.getActionCommand()) ||
                        CommonActionsIfc.FAILURE.equals(button.getActionCommand()) ||
                            button.getActionMap().get("Failure") != null)
                    {
                        button.doClick();
                        return;
                    }
                }
            }
        }
        else if (getComponentCount() == 1 && getComponent(0) instanceof JButton)
        {
            // if the dialog is just a notification, click that one button so
            // that any desired letter is mailed.
            JButton button = (JButton)getComponent(0);
            if (CommonActionsIfc.OK.equals(button.getActionCommand()))
            {
                button.doClick();
                return;
            }
        }

        // no cancel button was found. Just close the dialog.
        try
        {
            UISubsystem.getInstance().closeDialog();
        }
        catch (UIException e1)
        {
            logger.error("Unable to close dialog when pressing escape.", e1);
        }
    }

    /**
     * Overridden to simply add the buttons to the panel at the specified index.
     * Also adds actionlisteners for dismissing the dialog as needed.
     *
     * @see oracle.retail.stores.pos.ui.beans.NavigationButtonBean#addButton(oracle.retail.stores.pos.ui.beans.EYSButton, int)
     */
    @Override
    protected void addActionListeners(EYSButton button, UIAction action)
    {
        if (CommonActionsIfc.CANCEL.equals(button.getActionCommand()) ||
                CommonActionsIfc.FAILURE.equals(button.getActionCommand()))
        {
            button.addActionListener(cancelActionListener);
        }
        else
        {
            button.addActionListener(confirmActionListener);
        }
    }
}
