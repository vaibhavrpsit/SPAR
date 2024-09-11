/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSBorderFactory.java /rgbustores_13.4x_generic_branch/2 2011/05/23 11:58:38 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   05/06/11 - pos ui quick win
 *    cgreene   05/26/10 - convert to oracle packaging
 *    dwfung    02/04/10 - centered (vertical) required char 
 *    abondala  01/03/10 - update header date
 *    cgreene   12/17/09 - do instanceof against AbstractButtons
 *    cgreene   03/11/09 - made all ConerArcBorders static UI resources and
 *                         lazily inited. added method isRoundedBorder
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:33 AM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:13:22   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jul 05 2002 17:58:54   baa
 * code conversion and reduce number of color settings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:45:56   msg
 * Initial revision.
 * 
 *    Rev 1.2   10 Apr 2002 13:59:40   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.1   05 Apr 2002 14:25:20   baa
 * update method headers, add paintDisableBackground method to CornerBorder class
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.0   Mar 18 2002 11:58:42   msg
 * Initial revision.
 *
 *    Rev 1.5   13 Mar 2002 23:46:24   baa
 * fix painting problems
 * Resolution for POS SCR-1343: Split tender using Cash causes half of Tender Options to flash
 *
 *    Rev 1.0   Jan 19 2002 11:04:58   mpm
 * Initial revision.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ValidatingFieldIfc;

/**
 * Factory class that creates borders for the standard pos look and feel.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 * @since 13.1 Per {@link javax.swing.border.Border}, border should be shared
 *        and not recreated. Use this factory to accomplish that.
 */
public class EYSBorderFactory
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    /** the class name of this factory */
    public static final String FACTORY_NAME = "oracle.retail.stores.pos.ui.plaf.eys.EYSBorderFactory";

    /** The character that indicates to the user that a field is required */
    public static final String REQUIRED_CHAR = "*";

    /** The number of pixels to seperate the required character from the field */
    public static final int REQUIRED_INSET = 10;

    /** default font if no ui prop available */
    public static final Font DEFAULT_FONT = new Font("Dialog", Font.BOLD, 20);

    /** default color if no ui prop available */
    public static final Color DEFAULT_COLOR = new Color(175, 0, 0);

    public static final int BORDER_RADIUS = 10;

    /** specify what corners to round */
    public static final int ROUND_LEFT = 0;
    public static final int ROUND_RIGHT = 1;
    public static final int ROUND_TOP = 2;
    public static final int ROUND_BOTTOM = 3;
    public static final int ROUND_ALL = 4;
    public static final int ROUND_TOPLEFT=5;
    public static final int ROUND_TOPRIGHT=6;

    /** The type of paint technique to use */
    public static final int FILL = 0;
    public static final int DRAW = 1;

    /** static ui resource borders. */
    static RoundedBorder roundedBorder;
    static CornerArcBorder arcAllBorder;
    static CornerArcBorder arcBottomBorder;
    static CornerArcBorder arcLeftBorder;
    static CornerArcBorder arcRightBorder;
    static CornerArcBorder arcTopBorder;
    static CornerArcBorder headerLabelBorder;
    static CornerArcBorder arcAllBlackBorder;
    static CornerArcBorder arcBottomBlackBorder;
    static CornerArcBorder arcTopLeftBlackBorder;
    static CornerArcBorder arcTopRightBlackBorder;

    /**
     * Gets a border whose type is identified in the borderName parameter.
     * 
     * @param borderName the name of the border to create
     * @return a border object
     */
    public static Border getBorder(String borderName)
    {
        Border result = null;
        try
        {
            if (borderName.startsWith("emptyBorder"))
            {
                String parsed = UIUtilities.parseProperty(borderName);
                int size = Integer.parseInt(parsed);
                result = new BorderUIResource.EmptyBorderUIResource(size, size, size, size);
            }
            else
            {
                Class<? extends EYSBorderFactory> c = EYSBorderFactory.class;

                String methodName = "get" + borderName.substring(0, 1).toUpperCase() + borderName.substring(1);
                result = (Border)c.getMethod(methodName).invoke(null);
            }
        }
        catch (Exception e)
        {
            System.out.println("BORDER ERROR at " + borderName + ": " + e);
            result = new BorderUIResource.EmptyBorderUIResource(0, 0, 0, 0);
        }
        return result;
    }

    /**
     * Creates an email reply border.
     * 
     * @return an email reply border
     */
    public static Border getEmailReplyBorder()
    {
        Border insideEmailBorder = new BorderUIResource.CompoundBorderUIResource(BorderUIResource
                .getBlackLineBorderUIResource(), new BorderUIResource.EmptyBorderUIResource(2, 2, 2, 2));

        Border emailReplyBorder = new BorderUIResource.CompoundBorderUIResource(
                new BorderUIResource.EmptyBorderUIResource(5, 5, 5, 5), insideEmailBorder);

        return emailReplyBorder;
    }

    /**
     * Creates an empty border with the given width.
     * 
     * @param size the width of the border
     * @return an empty border
     */
    public static Border getEmptyBorder(int size)
    {
        return new BorderUIResource.EmptyBorderUIResource(size, size, size, size);
    }

    /**
     * Creates an etched border.
     * 
     * @return an etched border
     */
    public static Border getEtchedBorder()
    {
        return BorderUIResource.getEtchedBorderUIResource();
    }

    /**
     * Creates a focus border used in list renderers.
     * 
     * @return a focus border
     */
    public static Border getFocusBorder()
    {
        Color focusColor = UIManager.getColor("appBackground");

        if (focusColor == null || focusColor == Color.black)
        {
            return BorderUIResource.getBlackLineBorderUIResource();
        }
        return new BorderUIResource.LineBorderUIResource(focusColor);
    }

    /**
     * Creates an raised bevel border.
     * 
     * @return an raised bevel border
     */
    public static Border getRaisedBevelBorder()
    {
        return BorderUIResource.getRaisedBevelBorderUIResource();
    }

    /**
     * Creates a lowerer bevel border.
     * 
     * @return a lowerer bevel border
     */
    public static Border getLoweredBevelBorder()
    {
        return BorderUIResource.getLoweredBevelBorderUIResource();
    }

    /**
     * Creates a validating border that displays an asteric on required
     * components.
     * 
     * @return a validating border
     */
    public static Border getValidatingBorder()
    {
        Border validatingBorder = new BorderUIResource.CompoundBorderUIResource(new RequiredBorder(), BorderUIResource
                .getEtchedBorderUIResource());

        return validatingBorder;
    }

    /**
     * Creates an empty border for the prompt area.
     * 
     * @return a prompt area border
     */
    public static Border getPromptAreaBorder()
    {
        Border promptAreaBorder = new BorderUIResource.EmptyBorderUIResource(12, 20, 12, 12);
        return promptAreaBorder;
    }

    /**
     * Returns the rounded border ui resource. Lazily inits and returns the same
     * instance per Swing {@link BorderFactory} pattern.
     * 
     * @return a rounded border
     */
    public static Border getRoundedBorder()
    {
        if (roundedBorder == null)
        {
            roundedBorder = new RoundedBorder();
        }
        return roundedBorder;
    }

    /**
     * Creates a border where the top left and right corners are rounded.
     * 
     * @return a border rounded at the top
     */
    public static Border getArcTopBorder()
    {
        if (arcTopBorder == null)
        {
            arcTopBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_TOP);
        }
        return arcTopBorder;
    }

    /**
     * Creates a border where the bottom left and right corners are rounded.
     * 
     * @return a border rounded at the bottom
     */
    public static Border getArcBottomBorder()
    {
        if (arcBottomBorder == null)
        {
            arcBottomBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_BOTTOM);
        }
        return arcBottomBorder;
    }

    /**
     * Creates a border where the top and bottom left corners are rounded.
     * 
     * @return a border rounded on the left side
     */
    public static Border getArcLeftBorder()
    {
        if (arcLeftBorder == null)
        {
            arcLeftBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_LEFT);
        }
        return arcLeftBorder;
    }

    /**
     * Creates a border where the top and bottom right corners are rounded.
     * 
     * @return a border rounded on the right side
     */
    public static Border getArcRightBorder()
    {
        if (arcRightBorder == null)
        {
            arcRightBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_RIGHT);
        }
        return arcRightBorder;
    }

    /**
     * Creates a border where all corners are rounded.
     * 
     * @return a border rounded at all four corners
     */
    public static Border getArcAllBorder()
    {
        if (arcAllBorder == null)
        {
            arcAllBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_ALL);
        }
        return arcAllBorder;
    }

    /**
     * Creates a border for header labels. The default implementation creates a
     * border with four rounded edges.
     * 
     * @return a header label border
     */
    public static Border getHeaderLabelBorder()
    {
        if (headerLabelBorder == null)
        {
            headerLabelBorder = new CornerArcBorder(2, ROUND_ALL);
        }
        return headerLabelBorder;
    }
    
    public static Border getArcAllBlackBorder()
    {
        if (arcAllBlackBorder == null)
        {
          arcAllBlackBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_ALL, Color.BLACK);
        }
        return arcAllBlackBorder;
    }
    
    public static Border getArcBottomBlackBorder()
    {
        if (arcBottomBlackBorder == null)
        {
          arcBottomBlackBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_BOTTOM, Color.BLACK);
        }
        return arcBottomBlackBorder;
    }
    public static Border getArcTopLeftBlackBorder()
    {
        if (arcTopLeftBlackBorder == null)
        {
          arcTopLeftBlackBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_TOPLEFT, Color.BLACK);
        }
        return arcTopLeftBlackBorder;
    }

    public static Border getArcTopRightBlackBorder()
    {
        if (arcTopRightBlackBorder == null)
        {
          arcTopRightBlackBorder = new CornerArcBorder(BORDER_RADIUS, ROUND_TOPRIGHT, Color.BLACK);
        }
        return arcTopRightBlackBorder;
    }

    /**
     * Creates a border for data fields. The default implementation creates a
     * border with lowered bevel edges.
     * 
     * @return a field border
     */
    public static Border getFieldBorder()
    {
        return BorderUIResource.getLoweredBevelBorderUIResource();
    }

    /**
     * Creates a border for list fields. The default implementation creates a
     * border with lowered bevel edges.
     * 
     * @return a field border
     */
    public static Border getListBorder()
    {
        return BorderUIResource.getLoweredBevelBorderUIResource();
    }

    /**
     * Returns true if the specified border is one of the rounded border UI
     * resources kept by this factory or an instance of RoundedBorder.
     * 
     * @param b
     * @return
     */
    public static boolean isRoundedBorder(Border b)
    {
        return (b == getRoundedBorder() || b == getArcAllBorder()
                || b == getArcBottomBorder() || b == getArcLeftBorder()
                || b == getArcRightBorder() || b == getArcTopBorder()
                || b == getHeaderLabelBorder() || b instanceof RoundedBorder);
    }

    /**
     * A border that draws a required marker if a component is required.
     */
    public static class RequiredBorder extends EmptyBorder implements UIResource
    {
        private static final long serialVersionUID = 28751533623648650L;

        /**
         * Default constructor.
         */
        public RequiredBorder()
        {
            super(0, 0, 0, REQUIRED_INSET);
        }

        /**
         * Required by AbtractBorder class; draws the border.
         * 
         * @param c the component contained in the border
         * @param g the graphics object used to draw the border
         * @param x location in the x axis
         * @param y location in the y axis
         * @param w width of the field
         * @param h height of the field
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
        {
            // if the component is required, draw the required character
            Component comp = c;
            if (c instanceof JScrollPane
                    && ((JScrollPane)c).getViewport().getComponent(0) instanceof ValidatingFieldIfc)
            {
                comp = ((JScrollPane)c).getViewport().getComponent(0);
            }
            // if the component is required, draw the required character
            if (comp instanceof ValidatingFieldIfc && ((ValidatingFieldIfc)comp).isRequired())
            {
                // get the font and color from the ui
                Font font = UIManager.getFont("requiredFont");
                Color color = UIManager.getColor("requiredMark");

                // if the font or color is null, use the default
                if (font == null)
                {
                    font = DEFAULT_FONT;
                }
                if (color == null)
                {
                    color = DEFAULT_COLOR;
                }
                // set the font and color and draw the required character
                g.setFont(font);
                g.setColor(color);
                int xx = x + w - REQUIRED_INSET;
                int yy = y + h/2 + font.getSize();
                if (yy > h)
                {
                    yy = h;
                }
                g.drawString(REQUIRED_CHAR, xx, yy);
            }
        }
    } //RequiredBorder

    /**
     * A border that draws a rounded edge border.
     */
    public static class RoundedBorder extends AbstractBorder implements UIResource
    {
        private static final long serialVersionUID = 7605748398103490142L;

        /**
         * Default constructor.
         */
        public RoundedBorder()
        {
            super();
        }

        /**
         * Required by AbtractBorder class; draws the border.
         * 
         * @param c the component contained in the border
         * @param g the graphics object used to draw the border
         * @param x location in the x axis
         * @param y location in the y axis
         * @param w width of the component
         * @param h height of the component
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
        {
            setRendering(g);
            int startX = x;
            int startY = y;
            int endX = w - 3;
            int endY = h - 3;
            int jump = (endY / 2) + 1;

            Graphics2D g2 = (Graphics2D)g;
            Color baseColor = c.getBackground();
            Color darkColor = baseColor.darker().darker();

            // use dark color all around
            // g2.setColor(lightColor);

            g2.setColor(darkColor);

            g2.drawArc(startX, startY, endY, endY, 90, 180);
            g2.drawLine(startX + jump, startY, endX - jump, startY);

            g2.drawLine(startX + jump, endY, endX - jump, endY);
            g2.drawArc(endX - endY, startY, endY, endY, 90, -180);
        }

        /**
         * Paints a enable background that matches the shape of the border.
         * 
         * @param g the graphics object
         * @param c the component
         * @param color the background color
         */
        protected void paintBackground(Graphics g, Component c, Color color)
        {
            setRendering(g);
            Graphics2D g2 = (Graphics2D)g;
            if (c.isOpaque() || c instanceof AbstractButton)
            {
                g2.setColor(color);
                int h = c.getHeight() - 3;
                g2.fillRoundRect(1, 1, c.getWidth() - 3, h, h, h);
            }
        }

        /**
         * Paints a disable background that matches the shape of the border.
         * 
         * @param g the graphics object
         * @param c the component
         * @param color the background color
         */
        protected void paintDisabledBackground(Graphics g, Component c, Color color)
        {
            setRendering(g);

            if (c.isOpaque() || c instanceof AbstractButton)
            {
                // new color can be used for transparency
                Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 120);

                g.setColor(newColor);
                int h = c.getHeight() - 3;
                g.fillRoundRect(1, 1, c.getWidth() - 3, h, h, h);
            }
        }

        /**
         * Adjusts the rendering hints for a graphics object so that area fills
         * and line drawing uses antialiasing, but text drawing does not.
         * 
         * @param g the graphics object
         */
        protected void setRendering(Graphics g)
        {
            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
    } //RoundedBorder

    /**
     * A border that draws a rounded edge border with a specific radius and with
     * certain corners rounded.
     */
    public static class CornerArcBorder extends RoundedBorder implements UIResource
    {
        private static final long serialVersionUID = 1476901071875291795L;

        /**
         * the radius of the corner arc
         */
        protected int arcRadius;

        protected int location;
        
        protected Color borderColor;

        /**
         * Default constructor.
         */
        public CornerArcBorder()
        {
            this(20, ROUND_ALL);
        }

        /**
         * Constructor that sets the arc radius and style.
         * 
         * @param radius the arc radius
         * @param loc the corners to be rounded (TOP, BOTTOM, LEFT, RIGHT, ALL)
         */
        public CornerArcBorder(int radius, int loc)
        {
            super();
            arcRadius = radius;
            location = loc;
        }
        
        public CornerArcBorder(int radius, int loc, Color color)
        {
            super();
            arcRadius = radius;
            location = loc;
            borderColor = color;
            
        }

        /**
         * Required by AbtractBorder class; draws the border.
         * 
         * @param c the component contained in the border
         * @param g the graphics object used to draw the border
         * @param x location in the x axis
         * @param y location in the y axis
         * @param w width of the component
         * @param h height of the component
         */
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
        {
            setRendering(g);
            int endX = w - 1;
            int endY = h - 1;
            int diameter = arcRadius * 2;

            Graphics2D g2 = (Graphics2D)g;

            Color baseColor = c.getBackground();
            Color darkColor;
            
            if(borderColor != null)
            {
              darkColor = borderColor;
            }
            else
            {
               darkColor = baseColor.darker();
            }

            // draw the left and top lines
            // use same color
            g2.setColor(darkColor);
            int topStart = x;
            int topEnd = endX;
            int leftStart = y;
            int leftEnd = endY;
            int bottomStart = x;
            int bottomEnd = endX;
            int rightStart = y;
            int rightEnd = endY;

            if (location == ROUND_ALL || location == ROUND_TOP || location == ROUND_LEFT ||location == ROUND_TOPLEFT) 
            {
                g2.drawArc(x, y, diameter, diameter, 90, 90);
                topStart = topStart + arcRadius;
                leftStart = leftStart + arcRadius;
            }
            if (location == ROUND_ALL || location == ROUND_TOP || location == ROUND_RIGHT ||location == ROUND_TOPRIGHT)
            {
                topEnd = topEnd - arcRadius;
                rightStart = rightStart + arcRadius;
                g2.drawArc(endX - diameter, y, diameter, diameter, 90, -90);
            }
            g2.drawLine(topStart, y, topEnd, y);

            if (location == ROUND_ALL || location == ROUND_BOTTOM || location == ROUND_LEFT)
            {
                leftEnd = leftEnd - arcRadius;
                bottomStart = bottomStart + arcRadius;
                g2.setColor(darkColor);
                g2.drawArc(x, endY - diameter, diameter, diameter, 180, 90);
            }
            if(location != ROUND_TOPRIGHT)
            {
                g2.drawLine(x, leftStart, x, leftEnd);
            }

            if (location == ROUND_ALL || location == ROUND_BOTTOM || location == ROUND_RIGHT)
            {
                bottomEnd = bottomEnd - arcRadius;
                rightEnd = rightEnd - arcRadius;
                g2.drawArc(endX - diameter, endY - diameter, diameter, diameter, 0, -90);
            }
            if(location != ROUND_TOPRIGHT)
            {
                g2.drawLine(bottomStart, endY, bottomEnd, endY);
            }
            g2.drawLine(endX, rightStart, endX, rightEnd);
        }

        /**
         * Paints a enable background that matches the shape of the border.
         * 
         * @param g the graphics object
         * @param c the component
         * @param color the background color
         */
        protected void paintBackground(Graphics g, Component c, Color color)
        {
            setRendering(g);
            if (c.isOpaque() || c instanceof AbstractButton)
            {
                int x = 1;
                int y = 1;
                int width = c.getWidth() - 1;
                int height = c.getHeight() - 1;
                int topStart = 1;
                int topWidth = width;
                int bottomStart = 1;
                int bottomWidth = width;
                int radius = arcRadius - 1;
                int diameter = radius * 2;

                Graphics2D g2 = (Graphics2D)g;
                g2.setColor(color);
                if (location == ROUND_ALL || location == ROUND_TOP || location == ROUND_LEFT)
                {
                    g2.fillArc(x, y, diameter, diameter, 90, 90);
                    topStart = topStart + radius;
                }
                if (location == ROUND_ALL || location == ROUND_TOP || location == ROUND_RIGHT)
                {
                    topWidth = width - topStart - radius;
                    g2.fillArc(width - diameter, y, diameter, diameter, 90, -90);
                }
                if (location == ROUND_ALL || location == ROUND_BOTTOM || location == ROUND_LEFT)
                {
                    bottomStart = bottomStart + radius;
                    g2.fillArc(x, height - diameter, diameter, diameter, 180, 90);
                }
                if (location == ROUND_ALL || location == ROUND_BOTTOM || location == ROUND_RIGHT)
                {
                    bottomWidth = width - bottomStart - radius;
                    g2.fillArc(width - diameter, height - diameter, diameter, diameter, 0, -90);
                }
                g2.fillRect(topStart, y, topWidth, radius);
                g2.fillRect(x, y + radius, width, height - diameter - 1);
                g2.fillRect(bottomStart, height - radius, bottomWidth, radius);

            }

        }

        /**
         * Paints a disable background that matches the shape of the border.
         * 
         * @param g the graphics object
         * @param c the component
         * @param color the background color
         */
        protected void paintDisabledBackground(Graphics g, Component c, Color color)
        {
            // new color used for transparency
            Color newColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), 120);
            paintBackground(g, c, newColor);
        }
    } // CornerArcBorder

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
