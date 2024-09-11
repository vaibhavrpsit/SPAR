/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LogoBeanSmall.java /main/17 2012/10/30 16:49:44 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/30/12 - Logo click browser cleanup
 *    vbongu    10/29/12 - logo is the link to embedded browser
 *    cgreene   11/17/11 - fix LogoBean so that its subclass can have its image
 *                         in a labal and not painted.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Properties;

import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.tour.application.RestAreaLetter;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Bean for displaying the small logo in the bottom right corner. 
 * Logo is a link to the embedded browser
 * 
 * @version $Revision: /main/17 $
 */

public class LogoBeanSmall extends LogoBean implements MouseListener
{
    private static final long serialVersionUID = 7764529585308024013L;
    /** revision number supplied by Team Connection */
    
    /**
     * Flag that indicates whether or not to honor the mouse click.
     * @since 14.1
     */
    protected boolean enableBrowserLanuch = false;
    
    public static final String revisionNumber = "$Revision: /main/17 $";

    /**
     * Default Constructor for the LogoBeanSmall. If there is no image name
     * given, default to properties or cornerstone logo or null.
     */
    public LogoBeanSmall()
    {
        this("logo_small.gif");
    }

    /**
     * Default Constructor for the LogoBeanSmall. Creates a panel from an image.
     * 
     * @param imgSrc The filename of the image.
     */
    public LogoBeanSmall(String inputImageName)
    {
        super(inputImageName, false);
        LOG_ENTRY = "LogoBeanSmall";
        UI_PREFIX = "LogoBeanSmall";
        addMouseListener(this);
    }

    /**
     * Set the properties object on the class.
     * 
     * @param props a properties object.
     */
    public void setProps(Properties props)
    {
        if (props != null)
        {
            imageSource = props.getProperty("logoSmallImg");
        }
        if (imageSource != null)
        {
            configure();
        }
    }

    /**
     * Check if mouse clicks are acceptable.
     * 
     * @return
     * @since 14.1
     */
    public boolean isEnableBrowserLaunch() {
        return enableBrowserLanuch;
    }

    /**
     * Sets whether or not to honor mouse clicks to invoke the browser.
     * Invoking the browser at some sites can result in errors.  
     * 
     * @param acceptInput
     * 
     * @since 14.1
     */
    public void setEnableBrowserLaunch(boolean acceptInput)
    {
        this.enableBrowserLanuch = acceptInput;
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent e)
    {
        if (isEnableBrowserLaunch())
        {
            UISubsystem.getInstance().mail(new RestAreaLetter("LaunchBrowserRestArea"));
        }
        else
        {
            logger.debug("Mouse click on logo was ignored due to enableBrowserLaunch setting.");
        }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent e)
    {
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent e)
    {
        setCursor(Cursor.getDefaultCursor());

    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e)
    {
    }

    /*
     * (non-Javadoc)
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent e)
    {
    }

}
