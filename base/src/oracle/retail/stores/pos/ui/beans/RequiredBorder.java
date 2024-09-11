/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/RequiredBorder.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:52 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.UIResource;

//------------------------------------------------------------------------------
/**
    This class draws the boarder around required fields.

    @version $KW=@(#); $Ver=pos_4.5.0:2; $EKW;
    @deprecated as of release 5.0.0
**/
//------------------------------------------------------------------------------
public class RequiredBorder extends AbstractBorder
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6165187535251104798L;

    /** The character that indicates to the user that a field is required.  */
    protected static final String REQUIRED_CHAR = "*";

    /** The number of pixels to seperate the required character from the field.  */
    protected static final int INSET_OFFSET = 10;

    /** Border font  */
    protected static final Font DEFAULT_FONT = new Font("Dialog", Font.BOLD, 20);

    /** Border color  */
    public static final Color DEFAULT_COLOR = new Color(175, 0, 0);

    /** Indicates if the field is required  */
    protected boolean isRequired = false;

    //-------------------------------------------------------------------------
    /**
        Required by AbtractBorder class; draws the border.
        @param c the component contained in the border
        @param g the graphics object used to draw the border
        @param x location in the x axis
        @param y location in the y axis
        @param w width of the field
        @param h hight of the filed
    **/
    //-------------------------------------------------------------------------
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h)
    {
        Font font = g.getFont();
        Color color = g.getColor();


    /*if(c.isOpaque())
     {
       g.setColor(UIManager.getColor("beanBackground"));
           g.fillRect( x + w - INSET_OFFSET, y, INSET_OFFSET, h) ;
         } */


        Font newFont = UIManager.getFont("requiredFont");
        Color newColor = UIManager.getColor("requiredMark");

        if(newFont == null)
        {
                newFont = DEFAULT_FONT;
        }
        if(newColor == null)
        {
                newColor = DEFAULT_COLOR;
        }
        if(isRequired)
        {
            g.setFont(newFont);
            g.setColor(newColor);
            g.drawString(REQUIRED_CHAR, x + w - INSET_OFFSET , y + h);

        }
        g.setFont(font);
        g.setColor(color);

    }

    //-------------------------------------------------------------------------
    /**
        Gets the opaque property.
    **/
    //-------------------------------------------------------------------------
    public boolean isBorderOpaque()
    {
        return false;
    }

    //-------------------------------------------------------------------------
    /**
        Gets insets based on the component
        @param c the contained component
    **/
    //-------------------------------------------------------------------------
    public Insets getBorderInsets(Component c)
    {
        return new Insets(0, 0, 0, INSET_OFFSET);
    }

    //-------------------------------------------------------------------------
    /**
        Gets insets based on the component and provided Insets object
        @param c the contained component
        @param i the insets object
    **/
    //-------------------------------------------------------------------------
    public Insets getBorderInsets(Component c, Insets i)
    {
        i.right = INSET_OFFSET;
        i.left = i.bottom = i.top = 0;
        return i;
    }

    //-------------------------------------------------------------------------
    /**
        Sets the required property.
        @param required true if required
    **/
    //-------------------------------------------------------------------------
    public void setRequired(boolean required)
    {
        isRequired = required;
    }

    //-------------------------------------------------------------------------
    /**
        Test main for displaying the boarder.
        @param args the commond line.
    **/
    //-------------------------------------------------------------------------
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Border Test");
        RequiredBorder border = new RequiredBorder();
        border.setRequired(true);

        frame.getContentPane().setLayout(new GridBagLayout());

        JTextField textField1 = new JTextField(16);
        textField1.setBorder(border);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        frame.getContentPane().add(textField1, gbc);

        frame.setSize(200, 200);
        frame.show();
    }

    public static class RequiredBorderUIResource extends RequiredBorder
                                                 implements UIResource
    {
    }
}
