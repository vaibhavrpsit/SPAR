/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ImageGridBean.java /main/9 2013/05/01 16:04:18 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 05/01/13 - set font from plaf proeprties
 *    cgreen 02/04/13 - Corrected image scaling to maintain image ratio
 *    arabal 01/31/13 - Resize the Scansheet images only if they are larger in
 *                      size
 *    cgreen 09/04/12 - Code cleanup, method name cleanup and refactor to allow
 *                      for single-clicks and ESC back to previous category
 *    cgreen 08/29/12 - Add a colon to Page label and refactor code
 *    asinto 02/28/12 - XbranchMerge asinton_bug-13732985 from
 *                      rgbustores_13.4x_generic_branch
 *    asinto 02/27/12 - Fixed handling of mouse and focus events, switched to
 *                      single click instead of double click for scan sheet
 *                      actions.
 *    vtemke 10/03/11 - Fixed image resize issue
 *    jkoppo 04/01/11 - Removed text 'Empty' for empty items
 *    jkoppo 03/09/11 - I18N changes.
 *    jkoppo 03/04/11 - Modified to support return to original page, images
 *                      from url and other improvements.
 *    jkoppo 03/02/11 - New ui bean for scan sheet screen
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import oracle.retail.stores.domain.stock.ScanSheetComponent;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.behavior.EnableButtonListener;
import oracle.retail.stores.pos.ui.behavior.GlobalButtonListener;
import oracle.retail.stores.pos.ui.behavior.LocalButtonListener;

/**
 * A UI bean that displays the Scan Sheet of items in a grid.
 */
public class ImageGridBean extends CycleRootPanel implements ItemListener
{
    /**
     * A custom {@link ImageIcon} that can scale the components image depending
     * on the size of the widget using the image.
     */
    public static class ScalingImageIcon extends ImageIcon
    {
        private static final long serialVersionUID = 5192800383637703184L;

        public ScalingImageIcon(byte[] image)
        {
            super(image);
        }

        public ScalingImageIcon(Image image)
        {
            super(image);
        }

        /* (non-Javadoc)
         * @see javax.swing.ImageIcon#paintIcon(java.awt.Component, java.awt.Graphics, int, int)
         */
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y)
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            int textHeight = g2.getFontMetrics().getHeight();

            int maxWidth = c.getWidth() - 10;
            int maxHeight = c.getHeight() - 15 - textHeight;

            int imageHeight = getImage().getHeight(null);
            int imageWidth = getImage().getWidth(null);

            int imageDiv = Math.min(imageHeight, imageWidth);
            if (imageHeight > imageWidth && maxHeight < maxWidth
                    || imageHeight < imageWidth && maxHeight > maxWidth)
            {
                imageDiv = Math.max(imageHeight, imageWidth);
            }

            int maxDiv = Math.min(maxHeight, maxWidth);

            double ratio = (double)maxDiv / (double)imageDiv;

            int scaledWidth = (int)(imageWidth * ratio);
            int scaledHeight = (int)(imageHeight * ratio);

            int imageX = (c.getWidth() - scaledWidth) / 2;
            int imageY = textHeight + 10;
            g2.drawImage(getImage(), imageX, imageY, scaledWidth, scaledHeight, c);
            g2.dispose();
        }
    }

    /**
     * A {@link JLabel} class that draws its text at the middle of the label's
     * area near the top and renders the text anti-aliased.
     */
    public static class HeaderTextLabel extends JLabel
    {
        private static final long serialVersionUID = 1350888305300597898L;
        private String customText;

        public HeaderTextLabel(String text)
        {
            super();
            setCustomText(text);
            Font font = UIManager.getFont("scanSheetFont");
            if (font != null)
            {
                setFont(font);
            }
        }

        public void setCustomText(String text)
        {
            this.customText = text;
        }

        /* (non-Javadoc)
         * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
         */
        @Override
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            smoothenFonts(g);
            if (getIcon() != null)
            {
                ((Graphics2D) g).drawString(customText, ((getWidth() / 2) - (customText.length() * 3)), 20);
            }
            else
            {
                ((Graphics2D) g).drawString(customText, ((getWidth() / 2) - (customText.length() * 3)), getHeight() / 2);
            }
        }

        protected void smoothenFonts(Graphics g)
        {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        }
    };

    /**
     * A mouse listener to send the letter when the user clicks.
     * 
     * @ see UISubsystem#mail(oracle.retail.stores.foundation.tour.ifc.LetterIfc)
     */
    protected class ImageGridMouseListener extends MouseAdapter
    {
        private final ScanSheetComponent scanSheet;
        private final JLabel lblItem;
        
        public ImageGridMouseListener(ScanSheetComponent scanSheet, JLabel lblItem)
        {
            this.scanSheet = scanSheet;
            this.lblItem = lblItem;
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseClicked(MouseEvent e)
        {
            if (scanSheet.isCategory())
            {
                model.setSelectedItemID(scanSheet.getCategoryID());
                model.setCategorySelected(true);
            }
            else
            {
                model.setSelectedItemID(scanSheet.getItemID());
                model.setCategorySelected(false);
            }
            UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.ADD), false);
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseEntered(MouseEvent e)
        {
            lblItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        /* (non-Javadoc)
         * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
         */
        @Override
        public void mouseExited(MouseEvent e)
        {
            lblItem.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * A focus listener to change the border of the item selected when focused
     * and to enable the "AddItem" button.
     * 
     * @see EnableButtonListener#enableButton(String, boolean)
     */
    protected class ImageGridFocusListener implements FocusListener
    {
        private final JLabel lblItem;

        public ImageGridFocusListener(JLabel lblItem)
        {
            this.lblItem = lblItem;
        }

        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
         */
        @Override
        public void focusGained(FocusEvent e)
        {
            lblItem.setBorder(new LineBorder(Color.BLUE, 2));
            if (globalButtonListener != null)
            {
                globalButtonListener.enableButton(CommonLetterIfc.ADD, true);
            }
        }

        /* (non-Javadoc)
         * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
         */
        @Override
        public void focusLost(FocusEvent e)
        {
            lblItem.setBorder(new LineBorder(Color.BLACK, 1));
            if (globalButtonListener != null)
            {
                globalButtonListener.enableButton(CommonLetterIfc.ADD, false);
            }
        }
    }

    /** serialVersionUID */
    private static final long serialVersionUID = -7772199341953908442L;

    protected JPanel pnlScanSheet;
    protected JPanel pnlHeader;
    protected JLabel lblPage;
    protected JComboBox<String> cboPage;
    protected JLabel lblCategory;
    protected ImageGridBeanModel model;
    protected EnableButtonListener globalButtonListener;
    protected EnableButtonListener localButtonListener;

    /**
     * Configures the bean.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new BorderLayout());
        pnlScanSheet = new JPanel(new CardLayout());
        lblPage = uiFactory.createLabel("pageLabel", "Page:", null, UI_LABEL);
        cboPage = uiFactory.createValidatingComboBox("cboPage");
        cboPage.setRequestFocusEnabled(true);
        cboPage.requestFocusInWindow();
        cboPage.addItemListener(this);
        cboPage.setEditable(false);
        cboPage.setMaximumSize(new Dimension(80, 25));
        cboPage.setPreferredSize(new Dimension(80, 25));
        lblCategory = new JLabel();
        Font f = lblCategory.getFont();
        lblCategory.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        lblCategory.setForeground(Color.BLUE);
        pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.X_AXIS));
        pnlHeader.add(lblCategory);
        pnlHeader.add(Box.createHorizontalGlue());
        pnlHeader.add(lblPage);
        pnlHeader.add(cboPage);
        pnlHeader.add(Box.createHorizontalGlue());
        add(pnlHeader, BorderLayout.PAGE_START);
        add(pnlScanSheet, BorderLayout.CENTER);
    }

    private void sortScItems()
    {
        if (model != null)
        {
            Collections.sort(model.getScanSheet().getScItemList());
        }
        for (String s : model.getScanSheet().getCategoryMap().keySet())
        {
            Collections.sort(model.getScanSheet().getCategoryMap().get(s));
        }
    }

    public JPanel prepareImagePane(int pageNo)
    {
        // Calculate start item index in the scan sheet item arraylist that
        // should be displayed in this page
        int startIndex = model.maxNumberOfItems * (pageNo - 1);
        // Calculate end item index in the scan sheet item arraylist that
        // should be displayed in this page
        int endIndex = (pageNo * model.maxNumberOfItems) - 1;
        ArrayList<ScanSheetComponent> scItems = model.getScanSheet().getScItemList();
        if (endIndex > (scItems.size() - 1))
        {
            endIndex = scItems.size() - 1;
        }
        JPanel page = null;
        // Calculate/ decide grid size
        int gridSize;
        int noOfItems = (endIndex - startIndex) + 1;
        if (noOfItems >= model.maxNumberOfItems)
        {
            gridSize = (int) Math.sqrt(model.maxNumberOfItems);
        }
        else
        {
            gridSize = (int) Math.ceil(Math.sqrt(noOfItems));
        }
        page = new JPanel(new GridLayout(gridSize, gridSize));
        for (int i = startIndex; i <= endIndex; i++)
        {
            ScanSheetComponent ssc = scItems.get(i);
            HeaderTextLabel jlbl = new HeaderTextLabel(ssc.getDesc());
            prepareGridItem(jlbl, true, ssc.isCategory() ? Color.BLUE : Color.BLACK);
            byte[] image = ssc.getImage();
            if (image == null || image.length == 0)
            {
                try
                {
                    BufferedImage bufImage = ImageIO.read(new URL(ssc.getImageLocation()));
                    jlbl.setIcon(new ScalingImageIcon(bufImage));
                }
                catch (Exception e)
                {
                    logger.error("Unable to read the image from the specified location - " + e.getMessage());
                }
            }
            else
            {
                jlbl.setIcon(new ScalingImageIcon(image));
            }
            jlbl.addFocusListener(new ImageGridFocusListener(jlbl));
            jlbl.addMouseListener(new ImageGridMouseListener(ssc, jlbl));
            page.add(jlbl);
        }
        for (int j = noOfItems; j < (gridSize * gridSize); j++)
        {
            HeaderTextLabel jlbl = new HeaderTextLabel("");
            prepareGridItem(jlbl, false, Color.RED);
            page.add(jlbl);
        }
        return page;
    }

    public void prepareGridItem(JLabel jlbl, boolean focus, Color color)
    {
        jlbl.setForeground(color);
        jlbl.setHorizontalAlignment(JLabel.CENTER);
        jlbl.setFocusable(focus);
        Font f = jlbl.getFont();
        jlbl.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        jlbl.setBorder(new LineBorder(Color.BLACK, 1));
        jlbl.setRequestFocusEnabled(true);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @Override
    public void itemStateChanged(ItemEvent e)
    {
        if (e.getStateChange() == ItemEvent.SELECTED)
        {
            CardLayout cl = (CardLayout) (pnlScanSheet.getLayout());
            cl.show(pnlScanSheet, (String) e.getItem());
            int pageNo = Integer.parseInt((String) e.getItem());
            model.setCurrentPageNumber(pageNo);
            if (localButtonListener != null)
            {
                if (pageNo == 1)
                {
                    if (model.getNumberOfPages() > 1)
                    {
                        localButtonListener.enableButton("FirstPage", false);
                        localButtonListener.enableButton("NextPage", true);
                        localButtonListener.enableButton("LastPage", true);
                        localButtonListener.enableButton("PreviousPage", false);
                    }
                    else
                    {
                        localButtonListener.enableButton("FirstPage", false);
                        localButtonListener.enableButton("NextPage", false);
                        localButtonListener.enableButton("LastPage", false);
                        localButtonListener.enableButton("PreviousPage", false);
                    }
                }
                // If in last page
                else if (model.getNumberOfPages() != 1 && pageNo == model.getNumberOfPages())
                {
                    localButtonListener.enableButton("FirstPage", true);
                    localButtonListener.enableButton("LastPage", false);
                    localButtonListener.enableButton("NextPage", false);
                    localButtonListener.enableButton("PreviousPage", true);
                }
                // If in between first and last page
                else
                {
                    localButtonListener.enableButton("FirstPage", true);
                    localButtonListener.enableButton("LastPage", true);
                    localButtonListener.enableButton("NextPage", true);
                    localButtonListener.enableButton("PreviousPage", true);
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateBean()
     */
    @Override
    protected void updateBean()
    {
        if (beanModel instanceof ImageGridBeanModel)
        {
            model = (ImageGridBeanModel) beanModel;
            sortScItems();
            pnlScanSheet.removeAll();
            ArrayList<JPanel> pages = new ArrayList<JPanel>();
            for (int i = 1; i <= model.getNumberOfPages(); i++)
            {
                pages.add(prepareImagePane(i));
            }
            String comboBoxItems[] = new String[pages.size()];
            for (int i = 0; i < pages.size(); i++)
            {
                String pageName = String.valueOf(i + 1);
                pnlScanSheet.add(pages.get(i), pageName);
                comboBoxItems[i] = pageName;
            }
            cboPage.setModel(new DefaultComboBoxModel<String>(comboBoxItems));
            cboPage.setSelectedIndex(model.getCurrentPageNumber() - 1);
            lblCategory.setText(model.getCategoryDescription());
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            setCurrentFocus(cboPage);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateModel()
     */
    @Override
    public void updateModel()
    {
    }

    /**
     * Adds (actually sets) the enable button listener on the bean.
     * 
     * @param listener
     */
    public void addGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = listener;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
    }

    public void addLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = listener;
        if (localButtonListener != null)
        {
            if (model.getCategoryID() != null)
            {
                localButtonListener.enableButton("Return", true);
            }
            else
            {
                localButtonListener.enableButton("Return", false);
            }
        }
    }

    public void removeLocalButtonListener(LocalButtonListener listener)
    {
        localButtonListener = null;
    }

    /**
     * Removes the enable button listener from the bean.
     * 
     * @param listener
     */
    public void removeGlobalButtonListener(GlobalButtonListener listener)
    {
        globalButtonListener = null;
    }
}
