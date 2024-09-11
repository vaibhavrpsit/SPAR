/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PromptAreaBean.java /rgbustores_13.4x_generic_branch/3 2011/08/11 14:55:05 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   08/11/11 - UI tweaks for global button size and prompt area
 *                         alignment
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * This class displays the prompt area.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class PromptAreaBean extends JPanel
{
    private static final long serialVersionUID = -3619988133665866714L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/3 $";

    public String UI_PREFIX = "PromptArea";

    protected JTextArea promptArea = null;

    /**
     * Default constructor.
     */
    public PromptAreaBean()
    {
        super();
        initialize();
    }

    /**
     * Initializes this bean.
     */
    protected void initialize()
    {
        UIFactory.getInstance().configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    /**
     * Initializes the components for this bean.
     */
    protected void initComponents()
    {
        promptArea = new JTextArea();
        configureTextArea(promptArea);
    }

    /**
     * Lays out this bean's components.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints contraints = new GridBagConstraints();
        contraints.anchor = GridBagConstraints.WEST;
        contraints.fill = GridBagConstraints.HORIZONTAL;
        contraints.weightx = 1.0;
        add(promptArea, contraints);
    }

    /**
     * Convenience method to set the text in the prompt area.
     * 
     * @param text the new prompt text
     */
    public void setText(String text)
    {
        promptArea.setText(text);
        promptArea.revalidate();
    }

    /**
     * Configures a text area object.
     * 
     * @param textArea the text area
     */
    protected void configureTextArea(JTextArea textArea)
    {
        UIFactory.getInstance().configureUIComponent(textArea, UI_PREFIX + ".textArea");

        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setAlignmentY(0.5f);
    }

    /**
     * Configures a text area object.
     * 
     * @param textArea the text area
     * @deprecated as of 13.4. Do not call unless you've changed the layout manager.
     */
    protected void expandTextArea(String dimensionKey)
    {
        Dimension d = UIManager.getDimension(dimensionKey);
        promptArea.setPreferredSize(d);
    }
}
