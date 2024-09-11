/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/border/SuperBevelBorder.java /main/1 2012/10/15 16:19:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/15/12 - implement buttons that can use images to paint
 *                         background
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.Arc2D;

import oracle.retail.stores.pos.ui.plaf.eys.EYSBorderFactory.RoundedBorder;

/**
 * A border that paints a thick 3D-looking bordered border with a heavy highlight
 * on the top border and dark highlights on the bottom and side borders. Even
 * though this class may call the colors highlights, they are more like shadows.
 * <p>
 * Note: The border should only be used on widgets that themselves paint their
 * backgrounds with rounded edges. This border works best at widget sizes of
 * about 180x70. It hasn't had thorough testing in other sizes.
 *
 * @author cgreene
 * @since 14.0
 */
public class SuperBevelBorder extends RoundedBorder
{
    private static final long serialVersionUID = 8566154144583531757L;

    /** The default width of this border. Equals 16. */
    public static final int DEFAULT_BORDER_WIDTH = 16;

    protected int borderWidth = DEFAULT_BORDER_WIDTH;
    protected Color highlightUpper;
    protected Color highlightLeft;
    protected Color highlightLower;
    protected Color highlightRight;

    /**
     * Creates a border border of {@link #DEFAULT_BORDER_WIDTH} size.
     */
    public SuperBevelBorder()
    {
        this(DEFAULT_BORDER_WIDTH);
    }

    /**
     * Creates a border border with the specified width and whose colors will be
     * derived from the background color of the component passed into the
     * paintBorder method.
     *
     * @param borderWidth the width of border for the border
     */
    public SuperBevelBorder(int borderWidth)
    {
        this.borderWidth = borderWidth;
    }

    /**
     * Creates a border border with the specified width and highlight colors.
     *
     * @param borderWidth the width of border for the border
     * @param highlightUpper the color to use for the border upper highlight
     * @param highlightLeft the color to use for the border left highlight
     * @param highlightRight the color to use for the border right highlight
     * @param highlightLower the color to use for the border lower highlight
     */
    public SuperBevelBorder(int borderWidth, Color highlightUpper, Color highlightLeft, Color highlightRight,
            Color highlightLower)
    {
        this(borderWidth);
        this.highlightUpper = highlightUpper;
        this.highlightLeft = highlightLeft;
        this.highlightRight = highlightRight;
        this.highlightLower = highlightLower;
    }

    /* (non-Javadoc)
     * @see javax.swing.border.AbstractBorder#paintBorder(java.awt.Component,
     * java.awt.Graphics, int, int, int, int)
     */
    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
    {
        // TODO code border to invert colors if painting a pressed button.

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color oldColor = g.getColor();
        g.translate(x, y);
        g.setClip(0, 0, width + 1, height + 1);
        int tweak = borderWidth / 3;

        // paint rectangular (parallelogram) parts
        // The points are in order NW, NE, SE, SW
        Polygon poly = new Polygon(new int[4], new int[4], 4);

        // paint top
        g.setColor(getHighlightUpper(c));
        poly.xpoints[0] = borderWidth - tweak;
        poly.xpoints[1] = width - borderWidth + tweak;
        poly.xpoints[2] = width - borderWidth;
        poly.xpoints[3] = borderWidth;
        poly.ypoints[0] = 0;
        poly.ypoints[1] = 0;
        poly.ypoints[2] = borderWidth;
        poly.ypoints[3] = borderWidth;
        g2.fill(poly);

        // paint left
        g.setColor(getHighlightLeft(c));
        poly.xpoints[0] = 0;
        poly.xpoints[1] = borderWidth;
        poly.xpoints[2] = borderWidth;
        poly.xpoints[3] = 0;
        poly.ypoints[0] = borderWidth - tweak;
        poly.ypoints[1] = borderWidth - 1;
        poly.ypoints[2] = height - borderWidth + 1;
        poly.ypoints[3] = height - borderWidth + tweak + 1;
        g2.fill(poly);

        // paint bottom
        g.setColor(getHighlightLower(c));
        poly.xpoints[0] = borderWidth;
        poly.xpoints[1] = width - borderWidth + 1;
        poly.xpoints[2] = width - borderWidth + tweak;
        poly.xpoints[3] = borderWidth - tweak;
        poly.ypoints[0] = height - borderWidth;
        poly.ypoints[1] = height - borderWidth;
        poly.ypoints[2] = height;
        poly.ypoints[3] = height;
        g2.fill(poly);

        // paint right
        g.setColor(getHighlightRight(c));
        poly.xpoints[0] = width - borderWidth;
        poly.xpoints[1] = width;
        poly.xpoints[2] = width;
        poly.xpoints[3] = width - borderWidth;
        poly.ypoints[0] = borderWidth;
        poly.ypoints[1] = borderWidth - tweak;
        poly.ypoints[2] = height - borderWidth + tweak;
        poly.ypoints[3] = height - borderWidth;
        g2.fill(poly);

        // paint rounded corners
        Arc2D.Float arc = new Arc2D.Float(Arc2D.PIE);

        // paint NW corner
        arc.x = -1;
        arc.y = -1;
        arc.width = borderWidth * 2 + 2;
        arc.height = borderWidth * 2 + 2;
        arc.start = 100f;
        arc.extent = 65f;
        GradientPaint paint = new GradientPaint(
                borderWidth - tweak,
                tweak,
                getHighlightUpper(),
                tweak,
                borderWidth - tweak,
                getHighlightLeft());
        g2.setPaint(paint);
        g2.fill(arc);

        // paint SW corner
        arc.x = -1;
        arc.y = height - (2 * borderWidth) - 1;
        arc.start = 200f;
        arc.width++;
        paint = new GradientPaint(
                tweak,
                height - borderWidth,
                getHighlightLeft(),
                borderWidth,
                height - tweak,
                getHighlightLower());
        g2.setPaint(paint);
        g2.fill(arc);

        // paint SE corner
        arc.x = width - (2 * borderWidth) - 2;
        arc.y = height - (2 * borderWidth) - 2;
        arc.start = 280f;
        arc.width++;
        arc.height++;
        paint = new GradientPaint(
                width - borderWidth + tweak,
                height - tweak,
                getHighlightLower(),
                width - tweak,
                height - borderWidth + tweak,
                getHighlightRight());
        g2.setPaint(paint);
        g2.fill(arc);

        // paint NE corner
        arc.x = width - (2 * borderWidth) - 1;
        arc.y = -1;
        arc.start = 10f;
        arc.width -= 2;
        arc.height--;
        paint = new GradientPaint(
                width - borderWidth + tweak,
                borderWidth - tweak,
                getHighlightRight(),
                width - borderWidth,
                tweak,
                getHighlightUpper());
        g2.setPaint(paint);
        g2.fill(arc);

        // paint a rounded border
        Color darkLineColor = darken(c.getBackground(), 0.60f);
        Color lighterlineColor = brighten(darkLineColor, 0.85f);
        paint = new GradientPaint(
                width / 2,
                0,
                lighterlineColor,
                width / 2,
                (int)(height * 0.75),
                darkLineColor);
        g2.setPaint(paint);
        g.drawRoundRect(0, 0, width, height, (int)(borderWidth * 1.5), (int)(borderWidth * 1.5));
        g.drawRoundRect(1, 1, width - 2, height - 2, (int)(borderWidth * 1.3), (int)(borderWidth * 1.3));

        // reset graphics
        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    /**
     * Returns the insets of the border.
     *
     * @param c the component for which this border insets value applies
     */
    @Override
    public Insets getBorderInsets(Component c)
    {
        return new Insets(borderWidth, borderWidth, borderWidth, borderWidth);
    }

    /**
     * Reinitialize the insets parameter with this Border's current Insets.
     *
     * @param c the component for which this border insets value applies
     * @param insets the object to be reinitialized
     */
    @Override
    public Insets getBorderInsets(Component c, Insets insets)
    {
        insets.left = insets.top = insets.right = insets.bottom = borderWidth;
        return insets;
    }

    /**
     * Returns the upper highlight color. If no color is yet been specified, the
     * color will be set to the component's background color at about 2%
     * lighter.
     *
     * @param c the component for which the highlight may be derived
     */
    public Color getHighlightUpper(Component c)
    {
        if (highlightUpper == null)
        {
            highlightUpper = brighten(c.getBackground(), 0.02f);
        }
        return highlightUpper;
    }

    /**
     * Returns the left highlight color. If no color is yet been specified, the
     * color will be set to the component's background color at about 20%
     * darker.
     *
     * @param c the component for which the highlight may be derived
     */
    public Color getHighlightLeft(Component c)
    {
        if (highlightLeft == null)
        {
            highlightLeft = darken(c.getBackground(), 0.20f);
        }
        return highlightLeft;
    }

    /**
     * Returns the lower highlight color. If no color is yet been specified, the
     * color will be set to the component's background color at about 41%
     * darker.
     *
     * @param c the component for which the highlight may be derived
     */
    public Color getHighlightLower(Component c)
    {
        if (highlightLower == null)
        {
            highlightLower = darken(c.getBackground(), 0.41f);
        }
        return highlightLower;
    }

    /**
     * Returns the left highlight color. If no color is yet been specified, the
     * color will be set to the component's background color at about 32%
     * darker.
     *
     * @param c the component for which the highlight may be derived
     */
    public Color getHighlightRight(Component c)
    {
        if (highlightRight == null)
        {
            highlightRight = darken(c.getBackground(), 0.32f);
        }
        return highlightRight;
    }

    /**
     * Returns the upper highlight color of the border border. Will return null
     * if no highlight color was specified at instantiation.
     */
    public Color getHighlightUpper()
    {
        return highlightUpper;
    }

    /**
     * Returns the left highlight color of the border border. Will return null if
     * no highlight color was specified at instantiation.
     */
    public Color getHighlightLeft()
    {
        return highlightLeft;
    }

    /**
     * Returns the lower highlight color of the border border. Will return null
     * if no highlight color was specified at instantiation.
     */
    public Color getHighlightLower()
    {
        return highlightLower;
    }

    /**
     * Returns the right highlight color of the border border. Will return null
     * if no highlight color was specified at instantiation.
     */
    public Color getHighlightRight()
    {
        return highlightRight;
    }

    /**
     * Returns the width of the border border.
     */
    public int getBorderWidth()
    {
        return borderWidth;
    }

    /**
     * Returns whether or not the border is opaque.
     */
    @Override
    public boolean isBorderOpaque()
    {
        return true;
    }

    /**
     * Creates a new <code>Color</code> that is a brighter version of this
     * <code>Color</code> by the specified scale.
     *
     * @param color the color to brighten. Must not be null.
     * @param factor between 0.0 and 1.0. E.g. 0.7d would be 70% brighter.
     * @return
     * @see java.awt.Color#brighter()
     */
    public static Color brighten(Color color, float factor)
    {
        assert (factor > 0.0 && factor < 1.0);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();

        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0)
        {
            return new Color(i, i, i);
        }
        if (r > 0 && r < i)
            r = i;
        if (g > 0 && g < i)
            g = i;
        if (b > 0 && b < i)
            b = i;

        return new Color(
                Math.min(Math.round(r * (1f + factor)), 255),
                Math.min(Math.round(g * (1f + factor)), 255),
                Math.min(Math.round(b * (1f + factor)), 255));
    }

    /**
     * Creates a new <code>Color</code> that is a darker version of this
     * <code>Color</code> by the specified scale.
     *
     * @param color the color to darken. Must not be null.
     * @param factor between 0.0 and 1.0. E.g. 0.7d would be 70% darker.
     * @return
     * @see java.awt.Color#darker()
     */
    public static Color darken(Color color, double factor)
    {
        assert (factor > 0.0 && factor < 1.0);
        return new Color(
                Math.max((int)(color.getRed()   / (1f + factor)), 0),
                Math.max((int)(color.getGreen() / (1f + factor)), 0),
                Math.max((int)(color.getBlue()  / (1f + factor)), 0));
    }
}