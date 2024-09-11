/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/img/ImgUIFactory.java /main/11 2014/01/13 13:13:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/10/14 - rename JButton methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:04 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:23 PM  Robert Pearse   
 *
 *  Revision 1.3  2004/02/12 16:52:16  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:30   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   23 Jul 2003 00:52:08   baa
 * add EYSbutton to factory
 * 
 *    Rev 1.0   Apr 29 2002 14:45:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 14:00:14   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.img;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.UIManager;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.EYSButton;
import oracle.retail.stores.pos.ui.plaf.eys.EYSUIFactory;

/**
 * Extends EYSUIFactory for Image LAF specific ui features.
 * 
 * @version $Revision: /main/11 $
 */
public class ImgUIFactory extends EYSUIFactory
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /main/11 $";

    /**
     * Default constructor.
     */
    public ImgUIFactory()
    {
        super();
    }

    /**
     * Creates and configures a JLabel.
     * 
     * @param text the label text
     * @return a configured label
     */
    public JLabel makePromptLabel(String text)
    {
        JLabel promptLabel = new JLabel(text);
        promptLabel.setFont((Font)UIManager.get("PromptLabel.font"));
        promptLabel.setForeground((Color)UIManager.get("PromptLabel.foreground"));

        return promptLabel;
    }

    /**
     * Overrides UIFactory.createButton to set the button size based on default
     * dimensions.
     * 
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public JButton createJButton(String label, Icon icon, String prefix)
    {
        JButton b = super.createJButton(label, icon, prefix);

        b.setIcon(UIManager.getIcon(prefix + ".icon"));
        b.setDisabledIcon(UIManager.getIcon(prefix + ".disabledIcon"));
        b.setPressedIcon(UIManager.getIcon(prefix + ".pressedIcon"));

        // size the button based on the icon
        UIUtilities.sizeToIcon(b, b.getIcon());

        return b;
    }

    /**
     * Overrides UIFactory.createButton to set the button size based on default
     * dimensions.
     * 
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public EYSButton createEYSButton(String label, Icon icon, String prefix, boolean traversable)
    {
        EYSButton b = super.createEYSButton(label, icon, prefix, traversable);

        b.setIcon(UIManager.getIcon(prefix + ".icon"));
        b.setDisabledIcon(UIManager.getIcon(prefix + ".disabledIcon"));
        b.setPressedIcon(UIManager.getIcon(prefix + ".pressedIcon"));

        // size the button based on the icon
        UIUtilities.sizeToIcon(b, b.getIcon());

        return b;
    }

    /**
     * Overrides UIFactory.getConstraints to provide bean-specific constraints
     * objects for ButtonBars, dataEntry beans, and Renderers.
     * 
     * @param prefix the name of the constraint
     */
    public GridBagConstraints getConstraints(String prefix)
    {
        GridBagConstraints constraints = new GridBagConstraints();

        if (prefix.equals("ButtonBar"))
        {
            constraints.fill = GridBagConstraints.BOTH;
        }
        else if (prefix.equals("DataEntryBean"))
        {
            constraints.gridx = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
        }
        else if (prefix.equals("Renderer"))
        {
            constraints.gridy = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.WEST;
            constraints.insets = getInsets("emptyInsets");
        }
        return constraints;
    }

    /**
     * Retrieves the PVCS revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
