/* ===========================================================================
* Copyright (c) 2009, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSToggleButtonUI.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:01 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   12/17/09 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import javax.swing.text.View;

/**
 * A subclass of {@link EYSButtonUI} that attempts to mimic the capabilities
 * of {@link BasicToggleButtonUI}.
 *
 * @author cgreene
 * @since 13.2
 * @revision $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EYSToggleButtonUI extends EYSButtonUI
{


    // Shared UI objects
    private final static ButtonUI buttonUI = new EYSToggleButtonUI();

    /**
     *  Creates button UI object
     *  @param c the button as a JComponent
     *  @returns the button UI
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
        AbstractButton b = (AbstractButton)c;
        ButtonModel model = b.getModel();
        Dimension size = b.getSize();

        Insets i = c.getInsets();

        viewRect = new Rectangle(size);

        viewRect.x += i.left;
        viewRect.y += i.top;
        viewRect.width -= (i.right + viewRect.x);
        viewRect.height -= (i.bottom + viewRect.y);

        iconRect = new Rectangle();
        textRect = new Rectangle();

        Font f = c.getFont();
        g.setFont(f);
     
        // check for newline character
        // and do a normal paint if it's not there
        if(!checkText(b.getText()))
        {
            // layout the text and icon
            String text = SwingUtilities.layoutCompoundLabel(c, fm,
                    b.getText(), b.getIcon(),
                    b.getVerticalAlignment(), b.getHorizontalAlignment(),
                    b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                    viewRect, iconRect, textRect, b.getText() == null ? 0 : b.getIconTextGap());

            g.setColor(b.getBackground());

            if (model.isArmed() && model.isPressed() || model.isSelected())
            {
                paintButtonPressed(g, b);
            }

            // Paint the Icon
            if (b.getIcon() != null)
            {
                paintIcon(g, b, iconRect);
            }

            // Draw the Text
            if (text != null && !text.equals(""))
            {
                View v = (View)c.getClientProperty(BasicHTML.propertyKey);
                if (v != null)
                {
                    v.paint(g, textRect);
                }
                else
                {
                    paintText(g, b, textRect, text);
                }
            }

            // draw the dashed focus line.
            if (b.isFocusPainted() && b.hasFocus())
            {
                paintFocus(g, b, viewRect, textRect, iconRect);
            }
        }
        else
        {
            // get the font metrics
            fm = g.getFontMetrics();

            // get the component insets
            Insets insets = c.getInsets();

            // set up the view rectangle
            viewRect.x = insets.left;
            viewRect.y = insets.top;
            viewRect.width = b.getWidth() - (insets.right + viewRect.x);
            viewRect.height = b.getHeight() - (insets.bottom + viewRect.y);

            // set the icon and text rectangle to 0 values
            textRect.x = textRect.y = textRect.width = textRect.height = 0;
            iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

            // make sure the graphics font matches the button's
            g.setFont(c.getFont());

            // assume bottom line is the largest string
            String temp = botLine;

            // check for longer top line
            if(topLine.length() > botLine.length())
            {
                temp = topLine;
            }

            // layout the text and icon
            SwingUtilities.layoutCompoundLabel(c, fm, temp, b.getIcon(),
                          b.getVerticalAlignment(), b.getHorizontalAlignment(),
                          b.getVerticalTextPosition(), b.getHorizontalTextPosition(),
                          viewRect, iconRect, textRect,
                          b.getText() == null ? 0 : defaultTextIconGap);
            repositionTextRect(b);
            textRect.height = textRect.height * 2;

            if(textRect.height > viewRect.height)
            {
                resizeButton(b);
            }
            clearTextShiftOffset();

            // perform UI specific press action, e.g. Windows L&F shifts text
            if (model.isArmed() && model.isPressed() || model.isSelected())
            {
                paintButtonPressed(g,b);
            }

            // Paint the Icon
            if(b.getIcon() != null)
            {
                paintIcon(g,c,iconRect);
            }


            if (b.isFocusPainted() && b.getFocusTraversalKeysEnabled())
            {
                // paint UI specific focus
                paintFocus(g,b,viewRect,textRect,iconRect);
            }

            paintTwoLineText(g, b);
        }
    }
}
