/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/img/ImgScrollBarUI.java /main/11 2014/01/13 13:13:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/10/14 - renamed JButton methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:22:04 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
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
 *    Rev 1.0   Apr 29 2002 14:45:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 14:00:10   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.img;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * Implements a bigger scrollbar ui that its easier to manipulate on a touch
 * screen
 * 
 * @version $Revision: /main/11 $
 */
public class ImgScrollBarUI extends BasicScrollBarUI
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /main/11 $";

    /**
     * Creates scrollbar UI object
     * 
     * @param c the scrollbar as a JComponent
     * @returns the scrollbar UI
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new ImgScrollBarUI();
    }

    /**
     * Install default behavior
     */
    protected void installDefaults()
    {
        super.installDefaults();
        scrollbar.setOpaque(false);
    }

    /**
     * Creates scroll up button
     * 
     * @param orientation the orientation of the button arrow
     */
    protected JButton createDecreaseButton(int orientation)
    {
        JButton button = UIFactory.getInstance().createJButton(null, null, "ScrollUpButton");

        return button;
    }

    /**
     * Creates scroll down button
     * 
     * @param orientation the orientation of the button arrow
     */
    protected JButton createIncreaseButton(int orientation)
    {
        JButton button = UIFactory.getInstance().createJButton(null, null, "ScrollDownButton");

        return button;
    }

    /**
     * Sets prefered size for the scroll bar
     * 
     * @param c the scrollbar as a JComponent
     */
    public Dimension getPreferredSize(JComponent c)
    {
        Dimension size = null;

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
        {
            size = new Dimension(32, 114);
        }
        else
        {
            size = new Dimension(114, 32);
        }
        return size;
    }

    /**
     * Paints movable thumb
     * 
     * @param g the scrollbar object
     * @param c the scrollbar as a JComponent
     * @param thumbBounds the rectangle boundaries
     */
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds)
    {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (thumbBounds.isEmpty() || !scrollbar.isEnabled())
        {
            return;
        }
        int w = thumbBounds.width;
        int h = thumbBounds.height;

        g2d.translate(thumbBounds.x, thumbBounds.y);

        g2d.setColor(UIManager.getColor("primary"));
        g2d.fillOval(0, 0, w, h);
        g2d.setColor(UIManager.getColor("white"));
        g2d.fillOval(3, 3, w - 6, h - 6);
        g2d.setColor(UIManager.getColor("secondary"));
        g2d.fillOval(5, 5, w - 10, h - 10);

        g2d.translate(-thumbBounds.x, -thumbBounds.y);
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
