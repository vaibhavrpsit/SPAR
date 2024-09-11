/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSRequiredFieldUI.java /main/13 2012/10/17 11:51:51 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    10/11/12 - Item inquiry with magnifying glass icon changes
 *    vbongu    10/10/12 - search field changes
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:35 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *  $
 *  Revision 1.3  2004/02/12 16:52:14  mcs
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
 *    Rev 1.0   Aug 29 2003 16:13:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:46:20   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 13:59:58   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

import oracle.retail.stores.common.utility.Util;

/**
 * Implements a required box around a text field ui
 * 
 * @version $Revision: /main/13 $
 */
public class EYSRequiredFieldUI extends BasicTextFieldUI
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /main/13 $";

    /** Border of the textfield */
    protected Border orgBorder;

    /**
     * Creates required text field UI object
     * 
     * @param c the text field as a JComponent
     * @returns the text field box UI
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new EYSRequiredFieldUI();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicTextFieldUI#installDefaults()
     */
    @Override
    public void installDefaults()
    {
        super.installDefaults();
        orgBorder = UIManager.getBorder("ValidatingField.border");
    }

    /**
     * Overridden to make the text component non-opaque if it is neither
     * editable nor enabled.
     * 
     * @param g the graphics context
     */
    @Override
    protected void paintSafely(Graphics g)
    {
        JTextComponent c = getComponent();
        if (!c.isEnabled() && !c.isEditable())
        {
            c.setOpaque(false);
            c.setBorder(null);
        }
        else
        {
            c.setOpaque(true);
            if (orgBorder != null)
            {
                c.setBorder(orgBorder);
            }
        }

        super.paintSafely(g);
    }

    /**
     * Repaints the required text field background and sets the border if null
     * 
     * @param g the text field graphic object
     */
    @Override
    protected void paintBackground(Graphics g)
    {
        JComponent c = getComponent();

        g.setColor(c.getBackground());

        if (c.getWidth() > 0)
        {
            g.fillRect(0, 0, c.getWidth() - EYSBorderFactory.REQUIRED_INSET, c.getHeight());
            g.setColor(c.getParent().getBackground());
            g.fillRect(c.getWidth() - EYSBorderFactory.REQUIRED_INSET, 0, EYSBorderFactory.REQUIRED_INSET,
                    c.getHeight());
        }
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
