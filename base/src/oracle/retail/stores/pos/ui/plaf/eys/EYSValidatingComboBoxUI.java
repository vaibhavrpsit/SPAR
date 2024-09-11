/* ===========================================================================  
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 *  ===========================================================================  
 *  $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSValidatingComboBoxUI.java /main/2 2012/11/26 09:21:02 jswan Exp $
 *  ===========================================================================  
 *  NOTES  
 *  <other useful comments, qualifications, etc.>  
 *   
 *     
 *  MODIFIED    (MM/DD/YY)  
 *     jswan     11/15/12 - Modified to support parameter controlled return
 *                          tenders.
 *     vbongu    10/01/12 - ui for ValidatingComboBox
 * 
 *  ===========================================================================  */

package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;

/**
 * Implements validating combo box ui
 */
public class EYSValidatingComboBoxUI extends EYSRequiredBoxUI
{
    /** Shadowed member since super.padding is not visible. */
    protected static Insets padding;

    /** The color we will paint disabled comboboxes. This is also the generic panel color. */ 
    protected static Color beanBackground;
    
    /** Border of the validating comboBox */
    protected Border orgBorder;

    /**
     * Creates validating combo box UI object
     * 
     * @param c the required combo box as a JComponent
     * @returns the required combo box UI
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new EYSValidatingComboBoxUI();
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicComboBoxUI#installDefaults()
     */
    @Override
    public void installDefaults()
    {
        super.installDefaults();
        if (padding == null)
        {
            padding = UIManager.getInsets("ComboBox.padding");
        }
        if (beanBackground == null)
        {
            beanBackground = UIManager.getColor("beanBackground");
        }
        orgBorder = UIManager.getBorder("ValidatingComboBox.border");
    }

    /*
     * (non-Javadoc)
     * @see javax.swing.plaf.basic.BasicComboBoxUI#createPopup()
     */
    @Override
    protected ComboPopup createPopup()
    {
        @SuppressWarnings("serial")
        BasicComboPopup popup = new BasicComboPopup(comboBox)
        {
            public void show()
            {
                Dimension popupSize = ((ValidatingComboBox)comboBox).getPopupSize();
                Dimension size = comboBox.getSize();
                // If the size (width) of the ComboBox is bigger than the
                // popupSize
                // make them the same.
                if (size.width > popupSize.width)
                {
                    popupSize.width = size.width;
                    popupSize.height = size.height;
                }
                popupSize.setSize(popupSize.width, getPopupHeightForRowCount(comboBox.getMaximumRowCount()));
                Rectangle popupBounds = computePopupBounds(0, comboBox.getBounds().height, popupSize.width,
                        popupSize.height);
                scroller.setMaximumSize(popupBounds.getSize());
                scroller.setPreferredSize(popupBounds.getSize());
                scroller.setMinimumSize(popupBounds.getSize());
                list.invalidate();

                int selectedIndex = comboBox.getSelectedIndex();
                if (selectedIndex == -1)
                {
                    list.clearSelection();
                }
                else
                {
                    list.setSelectedIndex(selectedIndex);
                }
                list.ensureIndexIsVisible(list.getSelectedIndex());
                setLightWeightPopupEnabled(comboBox.isLightWeightPopupEnabled());

                show(comboBox, popupBounds.x, popupBounds.y);
            }
        };
        popup.getAccessibleContext().setAccessibleParent(comboBox);
        return popup;
    }

    /**
     * Overrides the paint method to hide the arrowButton and the border
     * when not enabled and not editable.
     * 
     * @param g the graphics object
     * @param c the comboBox as a JComponent
     */
    @Override
    public void paint(Graphics g, JComponent c)
    {
        boolean showDisabled = false;
        if (c instanceof ValidatingComboBox)
        {
            showDisabled = ((ValidatingComboBox)c).isShowDisabled();
        }
        if (!showDisabled && !comboBox.isEnabled() && !comboBox.isEditable())
        {
            arrowButton.setVisible(false);
            comboBox.setBorder(null);
            Rectangle r = rectangleForCurrentValue();

            // paint the background and text
            paintCurrentValue(g, r, hasFocus);
        }
        else
        {
            arrowButton.setVisible(true);
            if (orgBorder != null)
            {
                comboBox.setBorder(orgBorder);
            }
            super.paint(g, c);
        }

    }

    /**
     * Overridden to paint the combox with the parent's background and normal
     * foreground when it is disabled. See {@link #beanBackground}.
     */
    @Override
    public void paintCurrentValue(Graphics g, Rectangle bounds, boolean hasFocus)
    {
        ListCellRenderer renderer = comboBox.getRenderer();
        Component c;

        if (hasFocus && !isPopupVisible(comboBox))
        {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, true, false);
        }
        else
        {
            c = renderer.getListCellRendererComponent(listBox, comboBox.getSelectedItem(), -1, false, false);
            c.setBackground(UIManager.getColor("ComboBox.background"));
        }
        c.setFont(comboBox.getFont());
        if (hasFocus && !isPopupVisible(comboBox))
        {
            c.setForeground(listBox.getSelectionForeground());
            c.setBackground(listBox.getSelectionBackground());
        }
        else
        {
            if (comboBox.isEnabled())
            {
                c.setForeground(comboBox.getForeground());
                c.setBackground(comboBox.getBackground());                
            }
            else
            {
                c.setForeground(comboBox.getForeground());
                if (beanBackground != null)
                {
                    c.setBackground(beanBackground);
                }
             }
        }

        // Fix for 4238829: should lay out the JPanel.
        boolean shouldValidate = false;
        if (c instanceof JPanel)
        {
            shouldValidate = true;
        }

        int x = bounds.x, y = bounds.y, w = bounds.width, h = bounds.height;
        if (padding != null)
        {
            x = bounds.x + padding.left;
            y = bounds.y + padding.top;
            w = bounds.width - (padding.left + padding.right);
            h = bounds.height - (padding.top + padding.bottom);
        }

        currentValuePane.paintComponent(g, c, comboBox, x, y, w, h, shouldValidate);
    }
}
