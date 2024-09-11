/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSButtonUI.java /main/22 2013/09/18 10:36:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/18/13 - make disabled text color light gray if foreground is
 *                         black
 *    cgreene   04/30/13 - need to check for EYSToggleButtons, which are not
 *                         EYSButtons
 *    cgreene   12/14/12 - Button image and plaf loading updates
 *    mjwallac  10/22/12 - UI button changes
 *    cgreene   10/17/12 - tweak implementation of search field with icon
 *    cgreene   10/15/12 - implement buttons that can use images to paint
 *                         background
 *    vbongu    10/11/12 - Item inquiry with magnifying glass icon changes
 *    vbongu    10/10/12 - ui changes for search button
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   12/17/09 - add support for toggle buttons
 *    cgreene   03/11/09 - fix painting in update method to use method
 *                         isRoundedBorder in EYSBorderFactory and not do any
 *                         painting unless necessary
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:33 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *
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
 *    Rev 1.1   Sep 08 2003 17:30:58   DCobb
 * Migration to jvm 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:13:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:45:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   16 Apr 2002 16:42:56   baa
 * paint disable background on square btns
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.1   10 Apr 2002 13:59:42   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.SystemColor;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.pos.ui.beans.EYSButton;

/**
 * Implements a button ui that draws two lines of text on a button if the button
 * text contains a newline character ('\n'). This UI class expects it is applied
 * to an instance of {@link EYSButton}.
 * 
 * @version $Revision: /main/22 $
 */
public class EYSButtonUI extends BasicButtonUI
{
    /** revision number supplied by PVCS **/
    public static final String revisionNumber = "$Revision: /main/22 $";

    // static objects that all buttons can share
    // this eliminates the need to reinstantiate things
    static Rectangle viewRect = new Rectangle(0, 0, 0, 0);
    static Rectangle iconRect = new Rectangle(0, 0, 0, 0);
    static Rectangle textRect = new Rectangle(0, 0, 0, 0);

    // The top text line on a button
    static String topLine;

    // The bottom text line on a button
    static String botLine;

    // The middle text line on a button
    static String midLine;

    // The font metrics
    static FontMetrics fm;

    // Shared UI object
    private final static ButtonUI buttonUI = new EYSButtonUI();

    /**
     * Creates button UI object
     * 
     * @param c the button as a JComponent
     * @returns the button UI
     */
    public static ComponentUI createUI(JComponent c)
    {
        return buttonUI;
    }

    /**
     * Overrides the paint method to check for a newline character.
     * 
     * @param g the button's graphics object
     * @param c the button as a JComponent
     */
    @Override
    public void paint(Graphics g, JComponent c)
    {
        EYSButton b = (EYSButton)c;
        ButtonModel model = b.getModel();
        Dimension size = b.getSize();

        // check for newline character
        // and do a normal paint if it's not there
        if (!checkText(b.getText()))
        {
            super.paint(g, c);
        }
        else
        {
            b.setHorizontalAlignment(SwingConstants.TRAILING);
            // get the font metrics
            fm = g.getFontMetrics();

            // get the component insets
            Insets insets = c.getInsets();

            // set up the view rectangle
            viewRect.x = insets.left;
            viewRect.y = insets.top;
            viewRect.width = size.width - (insets.right + viewRect.x);
            viewRect.height = size.height - (insets.bottom + viewRect.y);

            // set the icon and text rectangle to 0 values
            textRect.x = textRect.y = textRect.width = textRect.height = 0;
            iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

            // make sure the graphics font matches the button's
            g.setFont(c.getFont());

            // assume bottom line is the largest string
            String temp = botLine;

            // check if top or middle line is longer than bottom line - midLine can be null
            if (topLine.length() > botLine.length())
            {
                temp = topLine;
            }
            
            // check if middle line not null and longer than the other two lines 

            if (midLine != null && midLine.length() > temp.length())
            {
                temp = midLine;
            }

            // layout the text and icon

            SwingUtilities.layoutCompoundLabel(c, fm, temp, b.getIcon(), SwingConstants.CENTER,
                    SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.TRAILING, 
                    viewRect, iconRect, textRect, b.getText() == null? 0 : b.getIconTextGap());
            repositionTextRect(b);
            if (midLine == null)
            {
                textRect.height = textRect.height * 2;
                
            }
            else
            {
                textRect.height = textRect.height * 3;
            }
            textRect.x += 12;
            iconRect.x += 8;

            if (textRect.height > viewRect.height)
            {
                resizeButton(b);
            }
            clearTextShiftOffset();

            // perform UI specific press action, e.g. Windows L&F shifts text
            if (model.isArmed() && model.isPressed())
            {
                paintButtonPressed(g, b);
            }

            // Paint the Icon
            if (b.getIcon() != null)
            {
                paintIcon(g, c, iconRect);
            }

            if (b.isFocusPainted() && b.getFocusTraversalKeysEnabled())
            {
                // paint UI specific focus
                paintFocus(g, b, viewRect, textRect, iconRect);
            }

            if (midLine !=  null)
            {
                paintThreeLineText(g, b);
            }
            else
            {
                paintTwoLineText(g, b);
            }
        }

        // Draw the arrow
        if (b.isMenu())
        {
            // paint an arrow a eighth of the size of the button
            int arrowSize = Math.min((size.height - 4) / 8, (size.width - 4) / 8);
            paintTriangle(g, arrowSize, (size.height - arrowSize) / 2,
                    arrowSize, SwingConstants.WEST, b.isEnabled());
        }
    }

    /**
     * Repaints button. If this UI has {@link #imageButtonUp} set, it will use
     * that to render the entire background and border. Else, if the border is
     * round, the border will be asked to render the background.
     * 
     * @param g the button's graphics object
     * @param c the button as a JComponent
     */
    @Override
    public void update(Graphics g, JComponent c)
    {
        AbstractButton b = (AbstractButton)c;
        boolean paintBackgroundImage = (b instanceof EYSButton) && ((EYSButton)b).isBackgroundImage();
        if (paintBackgroundImage)
        {
            paintButtonImage(g, (EYSButton)c);
            paint(g, c);
        }
        else
        {
            Border border = b.getBorder();
            // image-painted buttons ignore their background color
            if (!paintBackgroundImage && EYSBorderFactory.isRoundedBorder(border))
            {
                // get background color
                Color background = c.getBackground();
                // if the button is displaying menu, 
                boolean menuDisplayed = (b instanceof EYSButton) && ((EYSButton)b).isMenuDisplayed();
                if (menuDisplayed)
                {
                    background = getDisabledColor(background.darker());
                }
                ButtonModel model = b.getModel();
                if (model.isEnabled())
                {
                    ((EYSBorderFactory.RoundedBorder)border).paintBackground(g, c, background);
                }
                else
                {
                    ((EYSBorderFactory.RoundedBorder)border).paintDisabledBackground(g, c, background);
                }
                paint(g, c);
            }
            else
            {
                super.update(g, c);
            }
        }
    }

    /**
     * Adjusts the rendering hints for a graphics object so that area fills and
     * line drawing uses anti-aliasing, but text drawing does not.
     * 
     * @param g the button's graphics object
     */
    protected void setRendering(Graphics g)
    {
        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    /**
     * Paints button text in two lines
     * 
     * @param g the button's graphics object
     * @param b the button object
     */
    protected void paintTwoLineText(Graphics g, AbstractButton b)
    {
        ButtonModel model = b.getModel();

        // set the color based on enabled state
        if (model.isEnabled())
        {
            g.setColor(b.getForeground());
        }
        else
        {
            g.setColor(getDisabledColor(b.getForeground()));
        }

        // draw the lines
        // Swap top and bottom line so Fx key on bottom
        BasicGraphicsUtils.drawString(g, botLine, model.getMnemonic(), getStartPoint(botLine, b),
                textRect.y + fm.getAscent());
        BasicGraphicsUtils.drawString(g, topLine, model.getMnemonic(), getStartPoint(topLine, b),
                textRect.y + fm.getHeight() + fm.getAscent());
    }

    /**
     * Paints button text in three lines
     * 
     * @param g the button's graphics object
     * @param b the button object
     */
    protected void paintThreeLineText(Graphics g, AbstractButton b)
    {
        ButtonModel model = b.getModel();

        // set the color based on enabled state
        if (model.isEnabled())
        {
            g.setColor(b.getForeground());
        }
        else
        {
            g.setColor(getDisabledColor(b.getForeground()));
        }
        // draw the lines
        //print "top" line on bottom, shift others up so Fx key on bottom
        
        BasicGraphicsUtils.drawString(g, midLine, model.getMnemonic(), getStartPoint(midLine, b),
                textRect.y + (fm.getAscent()/2));

        BasicGraphicsUtils.drawString(g, botLine, model.getMnemonic(), getStartPoint(botLine, b),
                textRect.y + fm.getHeight() + fm.getAscent()/2);

        BasicGraphicsUtils.drawString(g, topLine, model.getMnemonic(), getStartPoint(topLine, b),
                textRect.y + 2*fm.getHeight() + fm.getAscent()/2);
    }
    
    
    /**
     * Checks for a carriage return on the text.
     * 
     * @param value the text to be check
     * @returns boolean result True if a carriage return was found false
     *          otherwise.
     */
    protected boolean checkText(String value)
    {
        int pos = value.indexOf('\n');
        int pos2 = value.indexOf('\n', pos+1);
        boolean result = false;

        if (pos != -1) 
        {
            topLine = value.substring(0, pos);
	        // if more than 1 carriage return found, save mid and bottom line
            if (pos2 != -1)
            {
                midLine  = value.substring(pos + 1, pos2);
                botLine = value.substring(pos2 + 1, value.length());                
            }
			// if only 1 carriage return found, save bottom, set midLine to null
            else
            {
                midLine = null;
                botLine = value.substring(pos + 1, value.length());
            }
            result = true;
        }
        return result;
    }

    /**
     * Resizes a button component.
     * 
     * @param b the button's object
     */
    protected void resizeButton(AbstractButton b)
    {
        int newHeight = 0;

        if (b.getHorizontalTextPosition() == SwingConstants.CENTER)
        {
            newHeight = iconRect.height + defaultTextIconGap + textRect.height;
        }
        else
        {
            newHeight = textRect.height;
        }
        // get the component insets
        Insets insets = b.getInsets();

        Dimension d = new Dimension(b.getWidth(), newHeight + insets.top + insets.bottom);

        b.setSize(d);
    }

    /**
     * Repositions the text within a button component.
     * 
     * @param b the button's object
     */
    protected void repositionTextRect(AbstractButton b)
    {
        if (b.getVerticalAlignment() == SwingConstants.CENTER)
        {
            textRect.y = textRect.y - (textRect.height / 2) - 1;
        }
        else if (b.getVerticalAlignment() == SwingConstants.BOTTOM)
        {
            textRect.y = textRect.y - textRect.height;
        }
    }

    /**
     * Retrieves x coordinate of text in button
     * 
     * @param text the text
     * @param b the button's object
     */
    protected int getStartPoint(String text, AbstractButton b)
    {
        int result = textRect.x;

        int width = fm.stringWidth(text);
        int dif = textRect.width - width;

        if (dif > 0)
        {
            if (b.getHorizontalAlignment() == SwingConstants.CENTER)
            {
                result = textRect.x + (dif / 2);
            }
            else if (b.getHorizontalAlignment() == SwingConstants.RIGHT)
            {
                result = textRect.x + dif;
            }
        }
        return result;
    }

    /**
     * This method is called if the images for this UI are set and will be used
     * to render the button's background and border. There is no effect if the
     * images are null.
     * 
     * @param g
     * @param b
     */
    protected void paintButtonImage(Graphics g, EYSButton b)
    {
        Image img = (!b.isEnabled())? b.getImageDisabled() : b.getImageUp();
        if (b.isMenuDisplayed())
        {
            img = b.getImageDown();
        }
        if (img != null)
        {
            g.drawImage(img, 0, 0, b.getWidth(), b.getHeight(), b);
        }
    }

    /**
     * Paints a darker background for the button object
     * 
     * @param g the button's graphics object
     * @param b the button's object
     */
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b)
    {
        if (b instanceof EYSButton && ((EYSButton)b).isBackgroundImage())
        {
            g.drawImage(((EYSButton)b).getImageDown(), 0, 0, b.getWidth(), b.getHeight(), b);
        }
        else
        {
            Color darkerBackground = getDisabledColor(b.getBackground());
            Border border = b.getBorder();

            if (border == null || !(border instanceof EYSBorderFactory.RoundedBorder))
            {
                Dimension size = b.getSize();
                g.setColor(darkerBackground);
                g.fillRect(0, 0, size.width, size.height);
            }
            else
            {
                ((EYSBorderFactory.RoundedBorder)border).paintBackground(g, b, darkerBackground);
            }
        }
    }

    /**
     * Return a color that is slightly darker than the starting color. Unless
     * the starting color is almost black, in which case, the color will be
     * {@link Color#lightGray}.
     *
     * @param startingColor
     * @return
     * @see Color#darker()
     */
    protected Color getDisabledColor(Color startingColor)
    {
        if (startingColor.getRed() < 20 && startingColor.getBlue() < 20 && startingColor.getGreen() < 20)
        {
            return Color.lightGray;
        }

        return startingColor.darker();
    }

    /**
     * Paints a box around the text of button with focus
     * 
     * @param g the button's graphics object
     * @param b the button's object
     * @param viewRect the objects rectangle area
     * @param textRect the objects text area
     * @param textRect the objects icon area
     */
    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
            Rectangle textRect, Rectangle iconRect)
    {
        Color org_color = g.getColor();
        Color focus_color = Color.lightGray;

        g.setColor(focus_color);

        // draw a box around the text of the button that has focus
        g.drawRect(viewRect.x + 5, textRect.y - 1, viewRect.width - 10, textRect.height + 1);

        g.setColor(org_color);

    }

    /**
     * Paints a triangle.
     *
     * @see BasicArrowButton#paintTriangle(Graphics, int, int, int, int, boolean)
     */
    protected void paintTriangle(Graphics g, int x, int y, int size, int direction, boolean isEnabled)
    {
        Color oldColor = g.getColor();
        int mid, i, j;

        j = 0;
        size = Math.max(size, 2);
        mid = (size / 2) - 1;

        g.translate(x, y);
        if (isEnabled)
            g.setColor(SystemColor.white);
        else
            g.setColor(SystemColor.controlShadow);

        switch (direction)
        {
        case SwingConstants.NORTH:
            for (i = 0; i < size; i++)
            {
                g.drawLine(mid - i, i, mid + i, i);
            }
            if (!isEnabled)
            {
                g.setColor(SystemColor.controlHighlight);
                g.drawLine(mid - i + 2, i, mid + i, i);
            }
            break;
        case SwingConstants.SOUTH:
            if (!isEnabled)
            {
                g.translate(1, 1);
                g.setColor(SystemColor.controlHighlight);
                for (i = size - 1; i >= 0; i--)
                {
                    g.drawLine(mid - i, j, mid + i, j);
                    j++;
                }
                g.translate(-1, -1);
                g.setColor(SystemColor.controlShadow);
            }

            j = 0;
            for (i = size - 1; i >= 0; i--)
            {
                g.drawLine(mid - i, j, mid + i, j);
                j++;
            }
            break;
        case SwingConstants.WEST:
            for (i = 0; i < size; i++)
            {
                g.drawLine(i, mid - i, i, mid + i);
            }
            if (!isEnabled)
            {
                g.setColor(SystemColor.controlHighlight);
                g.drawLine(i, mid - i + 2, i, mid + i);
            }
            break;
        case SwingConstants.EAST:
            if (!isEnabled)
            {
                g.translate(1, 1);
                g.setColor(SystemColor.controlHighlight);
                for (i = size - 1; i >= 0; i--)
                {
                    g.drawLine(j, mid - i, j, mid + i);
                    j++;
                }
                g.translate(-1, -1);
                g.setColor(SystemColor.controlShadow);
            }

            j = 0;
            for (i = size - 1; i >= 0; i--)
            {
                g.drawLine(j, mid - i, j, mid + i);
                j++;
            }
            break;
        }
        g.translate(-x, -y);
        g.setColor(oldColor);
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
