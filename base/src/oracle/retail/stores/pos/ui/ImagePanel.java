/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/ImagePanel.java /main/12 2012/10/17 11:51:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    09/14/12 - painting an image in a panel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui;

import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.JPanel;

import oracle.retail.stores.pos.ui.beans.ImageBeanIfc;

/**
 * A JPanel subclass that paints an image in its background.
 */
public class ImagePanel extends JPanel implements ImageBeanIfc
{
    private static final long serialVersionUID = 2722884375776636611L;

    /** the background image */
    protected Image backgroundImage;

    /**
     * Default constructor. Sets the layout to {@link BorderLayout} and the
     * panel to opaque.
     */
    public ImagePanel()
    {
        super();
        setLayout(new BorderLayout());
        setOpaque(true);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ImageBeanIfc#getBackgroundImage()
     */
    @Override
    public Image getBackgroundImage()
    {
        return backgroundImage;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ImageBeanIfc#setBackgroundImage(String)
     */
    @Override
    public void setBackgroundImage(String propValue)
    {
        setBackgroundImage(UIUtilities.getImage(propValue, this));
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ImageBeanIfc#setBackgroundImage(java.awt.Image)
     */
    @Override
    public void setBackgroundImage(Image imageName)
    {
        backgroundImage = imageName;
    }

}
