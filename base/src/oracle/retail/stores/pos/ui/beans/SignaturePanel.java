/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SignaturePanel.java /rgbustores_13.4x_generic_branch/3 2011/08/11 18:53:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/11/11 - passing in the width and height of the panel to
 *                         minimize the degrading scaling effect. (The less you
 *                         scale, the better the image looks.)
 *    jswan     06/22/11 - Modified to support signature capture in APF.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:21 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:15 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:12:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 14:52:38   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:53:50   msg
 * Initial revision.
 *
 *    Rev 1.2   Jan 25 2002 07:12:24   mpm
 * Made preliminary fixes for signature dialog bean.  This is not a complete fix.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.image.ImageUtilityIfc;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

import org.apache.log4j.Logger;

//----------------------------------------------------------------------------
/**
 * Panel to display a signature
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $;
 */
//----------------------------------------------------------------------------
public class SignaturePanel extends JPanel
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1833664145597990204L;

    /** Logger */
    protected static Logger logger = Logger.getLogger(SignaturePanel.class);

    /** points used to draw the signature */
    protected Point[] signature = null;

    /** width in pixels of signature panel */
    protected static int maxX = 400;

    /** height in pixels of signature panel */
    protected static int maxY = 125;

    /** BufferedImage for the signature data.
        This is lazily created in the paint method. */
    protected BufferedImage bufferedImage = null;

    //---------------------------------------------------------------------
    /**
       Constructor.
       @param signature array of points to draw the signature
    */
    //---------------------------------------------------------------------
    public SignaturePanel(Point [] signature)
    {
        super();
        UIFactory.getInstance().configureUIComponent(this, "DialogBean");
        this.signature = signature;
        setPreferredSize(new Dimension(maxX, maxY));
        repaint();
    }

    //---------------------------------------------------------------------
    /**
       Override of paintComponent method from JComponent.
       If the UI delegate is non-null, call its paint method.
    */
    //---------------------------------------------------------------------
    public void paintComponent(Graphics graphics)
    {
        if (signature != null && bufferedImage == null)
        {
            // Use the ImageUtility to convert the points to a BufferedImage
            try
            {
                ImageUtilityIfc imageUtility = (ImageUtilityIfc)BeanLocator.getServiceBean(ImageUtilityIfc.SERVICE_IMAGE);
                bufferedImage = imageUtility.convertPoints2BufferedImage(getWidth(), getHeight(), signature);
            }
            catch (Exception e)
            {
                logger.warn("Error occurred while converting Pincom signature TIFF image to a Point array.", e);
            }
        }
        if(bufferedImage != null)
        {
            // PaintComoponent on the super class
            super.paintComponent(graphics);

            // Scale the image for display
            final int imageWidth = bufferedImage.getWidth();
            final int imageHeight = bufferedImage.getHeight();
            if(imageWidth != 0 && imageHeight != 0)
            {
                // Subtracting a few pixels from the panel width and height
                // provides a little border for the signature.
                int displayWidth  = getWidth() - 20;
                int displayHeight = getHeight() - 15;

                // Calculate the difference in size between the panel and signature's
                // width and height.
                double imageToPanelWidthRatio = imageWidth / (double)displayWidth;
                double imageToPanelHeightRatio = imageHeight / (double)displayHeight;

                // Use which ever ratio, i.e. size difference is greater to calculate
                // the actual area of the panel to be used.
                Dimension size = null;
                if(imageToPanelWidthRatio > imageToPanelHeightRatio)
                {
                    size = new Dimension((int)(imageWidth / imageToPanelWidthRatio),
                            (int)(imageHeight / imageToPanelWidthRatio));
                }
                else
                {
                    size = new Dimension((int)(imageWidth / imageToPanelHeightRatio),
                            (int)(imageHeight / imageToPanelHeightRatio));
                }

                // Scale the image to the available viewing area in the panel.
                Image useImage = bufferedImage.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);

                // Draw the image on the panel.  The calculations with the width and height place the image in
                // the center of the panel.
                graphics.drawImage(useImage, (getWidth() - size.width)/2, (getHeight() - size.height)/2, null);
            }
        }
    }
}