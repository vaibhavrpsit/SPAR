/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/img/ImgButtonUI.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    Rev 1.1   Sep 08 2003 17:31:00   DCobb
 * Migration to jvm 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:13:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 15 2003 13:51:20   baa
 * modify img plaf classes to extend the eys look and feel
 * Resolution for 3169: Image plaf borders not correctly displayed as borders are not rounded
 * 
 *    Rev 1.0   Apr 29 2002 14:45:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 14:00:04   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.img;
// java imports
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import oracle.retail.stores.foundation.utility.Util;

//------------------------------------------------------------------------------
/**
 *      Implements a button ui that draws two lines of text on a button image
 *      if the button text contains a newline character ('\n').
 *      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//------------------------------------------------------------------------------
public class ImgButtonUI extends BasicButtonUI
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public final static char SEPARATOR = '\n';

    /// Shared UI object
    private final static ButtonUI buttonUI = new ImgButtonUI();

    // The top text line on a button
    private  String topLine = null;

    // The bottom text line on a button
    private  String botLine = null;

    //--------------------------------------------------------------------------
    /**
     *  Creates button UI object
     *  @param c the button as a JComponent
     *  @returns the button UI
     */
    //--------------------------------------------------------------------------
    public static ComponentUI createUI(JComponent c)
    {
        return buttonUI;
    }

    //--------------------------------------------------------------------------
    /**
     *    Overrides the paint method to display icon and paint text on image.
     *    @param g the button's graphics object
     *    @param c the button as a JComponent
     */
    //--------------------------------------------------------------------------
    public void paint(Graphics g, JComponent c)
    {
        AbstractButton b = (AbstractButton)c;

        if(b.getIcon() != null)
        {
            paintIcon(g, c, new Rectangle(0, 0, 0, 0));
            if(b.getText() != null)
            {
              paintIconText(g, c);
            }
    }
        else
        {
            super.paint(g,c);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *    Paint  text on image.
     *    @param g the button's graphics object
     *    @param c the button as a JComponent
     */
    //--------------------------------------------------------------------------
    protected void paintIconText(Graphics g, JComponent c)
    {
        AbstractButton b = (AbstractButton)c;
        ButtonModel model = ((AbstractButton)c).getModel();
        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        String key = null;
        String text = b.getText();
        String name = text;

        int pos = text.indexOf(SEPARATOR);

           if(pos != -1)
           {
              key = text.substring(0, pos);
           name = text.substring(pos+1, text.length());
    }
        if(b.isEnabled())
        {
            g2.setColor(b.getForeground());
        }
        else
        {
            g2.setColor(Color.lightGray);
        }

        //  Use font metrics and the button to calculate
        //  the horizontal and vertical positions of the
        //  button text
        g2.setFont(b.getFont());
        FontMetrics fm = g2.getFontMetrics();

        int textHeight = fm.getHeight();
        int textWidth = fm.stringWidth(name);
        int horizontalPos = (b.getIcon().getIconWidth()- textWidth)/2;

        //  Position the text vertically in the center if
        //  there is no key, otherwise, shift
        int verticalPos = (b.getIcon().getIconHeight()/2);
        if(key != null)
           {
            verticalPos = verticalPos - textHeight/4;
        }

        if (b.isFocusPainted() && b.getFocusTraversalKeysEnabled())
        {
           // paint UI specific focus
           paintFocus(g,horizontalPos,verticalPos-textHeight+5,textWidth,textHeight);
        }

        //  Draw text
        BasicGraphicsUtils.drawString(g2, name, model.getMnemonic(), horizontalPos, verticalPos);

        //  If a function key is available then calculate the
        //  horizontal and vertical positions of the key text
        if(key != null)
           {
            g2.setFont(UIManager.getFont("buttonKeyFont"));
            fm = g2.getFontMetrics();
            textHeight = fm.getHeight();
               textWidth = fm.stringWidth(key);
            horizontalPos = (b.getIcon().getIconWidth()- textWidth)/2;
            verticalPos = (b.getIcon().getIconHeight()/2);
        verticalPos = verticalPos + fm.getHeight() - fm.getHeight()/4;

            BasicGraphicsUtils.drawString(g2, key, model.getMnemonic(), horizontalPos, verticalPos);
    }
    }


    //--------------------------------------------------------------------------
    /**
     * Checks for a carriage return on the text.
     * @param value the text to be check
     * @returns boolean result  True if a carriage return was found false otherwise.
     */
    //--------------------------------------------------------------------------
    protected boolean checkText(String theText)
    {
        int pos = theText.indexOf('\n');
        boolean result = false;

        if(pos != -1)
        {
            topLine = theText.substring(0, pos);
            botLine = theText.substring(pos+1, theText.length());
            result = true;
        }
        return result;
    }

    //--------------------------------------------------------------------------
    /**
     *  Paints a box around the text of button with focus
     *  @param g the button's graphics object
     *  @param x the horizontal coordinate
     *  @param y the vertical coordinate
     *  @param width the width of the focus rectangle
     *  @param height the height of the rectangle
     */
    //--------------------------------------------------------------------------
    protected void paintFocus(Graphics g, int x, int y, int width, int height)
    {
        Color org_color = g.getColor();
        Color focus_color = Color.lightGray;

        g.setColor(focus_color);
        g.drawRect(x, y, width, height);
        g.setColor(org_color);
    }

    //--------------------------------------------------------------------------
    /**
     *    Retrieves the PVCS revision number.
     *    @return String representation of revision number
     */
    //--------------------------------------------------------------------------
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }
}
