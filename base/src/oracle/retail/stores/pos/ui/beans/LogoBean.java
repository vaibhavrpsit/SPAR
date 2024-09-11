/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LogoBean.java /main/16 2013/01/14 18:50:29 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    01/10/13 - retriev image from bean property in LogoBeanSpec
 *    cgreene   11/17/11 - fix LogoBean so that its subclass can have its image
 *                         in a labal and not painted.
 *    cgreene   10/28/11 - paint full version of gif image so that resizing the
 *                         window doesn't clip the gif.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/05/10 - clean up some logging
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    cgreene   06/18/09 - use simple reference checking for performance when
 *                         testing of whether to add listener
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:28:55 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:14 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:25 PM  Robert Pearse
 *
 *   Revision 1.5  2004/05/25 13:33:32  jeffp
 *   @scr 3876 - removed line where it set background to white when configured
 *
 *   Revision 1.4  2004/04/09 16:56:00  cdb
 *   @scr 4302 Removed double semicolon warnings.
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
 *    Rev 1.1   Sep 16 2003 17:52:42   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import oracle.retail.stores.pos.ui.OnlineStatusContainer;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * Bean for displaying a logo. This is contained typically by the LogoScreen.
 * Default behavior should allow for an active logo panel without an image name.
 * If the image name is null, then don't display anything.
 *
 * @version $Revision: /main/16 $
 */
public class LogoBean extends BaseBeanAdapter implements WindowFocusListener
{
    private static final long serialVersionUID = -7858034785636467644L;

    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:15; $EKW;";

    /** The logger to which log messages will be sent */
    protected static final Logger logger = Logger.getLogger(LogoBean.class);

    /** The name of the bean */
    protected static String LOG_ENTRY = "LogoBean";

    /** A copy of the image source name */
    protected String imageSource = null;
    
    /** Bean property name for getting the image */
    protected String logoImage = null;

    /** Whether to paint the image directly or use a JLabel. */
    protected boolean paintImage;

    /** The logo image */
    protected Image image = null;

    /**
     * Default Constructor for the LogoBean. If there is no image name given,
     * default to properties or cornerstone logo or null.
     */
    public LogoBean()
    {
        this("logo.gif", true);
    }

    /**
     * Default Constructor for the LogoBean. Creates a panel from an image.
     *
     * @param imgSrc The filename of the image.
     */
    public LogoBean(String inputImageName)
    {
        this(inputImageName, true);
    }

    /**
     * Default Constructor for the LogoBean. Creates a panel from an image.
     *
     * @param imgSrc The filename of the image.
     * @param paintImge if true, then paint the image. If false, use a JLabel.
     */
    public LogoBean(String inputImageName, boolean paintImage)
    {
        imageSource = inputImageName;
        UI_PREFIX = "LogoBean";
        this.paintImage = paintImage;
    }

    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure()
    {
        setLayout(new BorderLayout());
        uiFactory.configureUIComponent(this, UI_PREFIX);

        if (logger.isInfoEnabled())
        {
            logger.info("Retrieving image from ResourceManager: " + imageSource);
        }

        // get the image
        image = UIUtilities.getImage(imageSource, this);

        if (logger.isInfoEnabled())
        {
            logger.info("Creating logo with image: " + image);
        }

        if (!paintImage && image != null)
        {
            ImageIcon icon = new ImageIcon(image);
            JLabel logo = new JLabel(icon);

            add(logo, BorderLayout.CENTER);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible)
        {
            StatusBeanModel statusBeanModel = beanModel.getStatusBeanModel();

            if (statusBeanModel != null)
            {
                OnlineStatusContainer onlineStatusContainer = statusBeanModel.getStatusContainer();

                if (onlineStatusContainer != null)
                {
                    Hashtable<Integer,Boolean> hashtable = onlineStatusContainer.getStatusHash();
                    Boolean trainingMode = hashtable.get(new Integer(POSUIManagerIfc.TRAINING_MODE_STATUS));
                    if (trainingMode != null)
                    {
                        setApplicationBackground(trainingMode.booleanValue());
                    }
                }
            }
            currentWindow = SwingUtilities.getWindowAncestor(this);
            // Focus listener is required so that the window is properly focused and
            // function key scan be used. However, dont keep adding these.
            WindowFocusListener[] listeners = currentWindow.getWindowFocusListeners();
            boolean addListener = true;
            for (int i = 0; i < listeners.length; i++)
            {
                if (listeners[i] == this)
                {
                    addListener = false;
                    break;
                }
            }
            if (addListener)
            {
                currentWindow.addWindowFocusListener(this);
            }
        }
    }
    
    /**
     * Get the bean property image name
     * @return logoImg
     */
    public String getLogoImage()
    {
        return logoImage;
    }

    /**
     * Set the image name from "LogoBeanSpec" bean property
     * named as "logoImage"
     * @param logoImage
     */
    public void setLogoImage(String logoImage)
    {
        this.logoImage = logoImage;
    }


    /**
     * Updates the bean with the image given in
     * beanproperty of "LogoBeanSpec"
     */
    @Override
    public void updateBean()
    {
        String prevImagesource = imageSource;
        imageSource = getLogoImage();
        if (!StringUtils.isEmpty(imageSource))
        {
            if(!StringUtils.equals(prevImagesource, imageSource))
            {
                configure();
            }
        }        
    }

    /**
     * Invoked when the Window is set to be the focused Window, which means that
     * the Window, or one of its subcomponents, will receive keyboard events.
     *
     * @param e window event
     */
    public void windowGainedFocus(WindowEvent e)
    {
        if (getComponentCount() > 0)
        {
            getComponent(0).requestFocusInWindow();
        }
        else
        {
            requestFocusInWindow();
        }
    }

    /**
     * Overridden to paint the logo the full size of the panel, scaling as the
     * screen size is adjusted.
     *
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (image != null)
        {
            // paint the image the size of the work panel
            g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
