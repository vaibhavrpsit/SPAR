/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DialogPromptAndResponseBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   09/17/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * A prompt and response bean specifically for the popup dialog.
 * 
 * @author cgreene
 * @since 13.1
 */
public class DialogPromptAndResponseBean extends PromptAndResponseBean
{
    private static final long serialVersionUID = -7521073866831167036L;

    protected WindowFocusListener windowListener = new WindowFocusListener();

    /**
     * Overridden to ensure the focus is placed into the {@link #activeResponseField}.
     * 
     * @see oracle.retail.stores.pos.ui.beans.PromptAndResponseBean#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                activeResponseField.requestFocusInWindow();
            }
        });
    }


    /**
     * Overridden to configure the response field as "ValidatingField".
     *
     * @see oracle.retail.stores.pos.ui.beans.PromptAndResponseBean#setActiveResponseField(javax.swing.JTextField)
     * @see UIFactory#configureUIComponent(javax.swing.JComponent, String)
     */
    @Override
    public void setActiveResponseField(JTextField rspField)
    {
        super.setActiveResponseField(rspField);
        uiFactory.configureUIComponent(activeResponseField, "ValidatingField");
    }

    /**
     * Overridden to move the response field to the grid below the prompt.
     *
     * @returns gbc the gridbag constraints
     */
    @Override
    public GridBagConstraints getResponseFieldConstraints()
    {
        if (responseContraints == null)
        {
            UIFactory factory = UIFactory.getInstance();
            responseContraints = new GridBagConstraints();

            responseContraints.anchor = GridBagConstraints.SOUTH;
            responseContraints.fill = GridBagConstraints.HORIZONTAL;
            responseContraints.insets = factory.getInsets("responseField");

            responseContraints.gridx = 0;
            responseContraints.gridy = 1;

            responseContraints.weightx = 1.0;
            responseContraints.weighty = 0.0;

            responseContraints.gridwidth = 1;
            responseContraints.gridheight = 1;
        }
        return responseContraints;
    }

    /**
     * Overridden to ensure a window listener is added to the dialog that will
     * provide focus in the field upon the first display.
     * 
     * @see javax.swing.JComponent#addNotify()
     */
    @Override
    public void addNotify()
    {
        super.addNotify();
        Container parent = getParent();
        if (parent != null)
        {
            Window w = SwingUtilities.getWindowAncestor(parent);
            WindowListener[] listeners = w.getWindowListeners();
            for (int i = listeners.length - 1; i >= 0; i--)
            {
                if (listeners[i] instanceof WindowFocusListener)
                {
                    w.removeWindowListener(listeners[i]);
                }
            }
            w.addWindowListener(windowListener);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * This internal class ensures that the active field gets the focus upon
     * the first display of the dialog. The {@link #activate()} method above
     * did not work in that case, and this does not work but the first time.
     */
    protected class WindowFocusListener extends WindowAdapter
    {
        /* (non-Javadoc)
         * @see java.awt.event.WindowAdapter#windowOpened(java.awt.event.WindowEvent)
         */
        public void windowOpened(WindowEvent e)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    activeResponseField.requestFocusInWindow();
                }
            });
        }
    }
}
