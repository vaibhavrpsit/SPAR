/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ImageAppFrame.java /main/16 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/05/12 - updated from deprecated methods
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:04 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
 * $
 * Revision 1.6  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.5  2004/03/26 21:18:19  cdb
 * @scr 4204 Removing Tabs.
 *
 * Revision 1.4  2004/03/21 16:34:28  bjosserand
 * @scr 4093 Transaction Reentry
 * Revision 1.3 2004/03/16 17:15:17 build Forcing head revision
 * 
 * Revision 1.2 2004/02/11 20:56:27 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:22 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.0 Aug 29 2003 16:10:44 CSchellenger Initial revision.
 * 
 * Rev 1.2 05 Jun 2002 22:02:42 baa support for opendrawerfortrainingmode parameter Resolution for POS SCR-1645:
 * Training Mode Enhancements
 * 
 * Rev 1.1 14 May 2002 18:29:56 baa training mode enhancements Resolution for POS SCR-1645: Training Mode Enhancements
 * 
 * Rev 1.0 Apr 29 2002 14:52:46 msg Initial revision.
 * 
 * Rev 1.1 10 Apr 2002 13:59:32 baa make code compliant with coding guidelines Resolution for POS SCR-1590: PLAF code
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Container;
import java.util.Locale;
import java.util.Properties;

import javax.swing.JRootPane;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ui.ImagePanel;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

//------------------------------------------------------------------------------
/**
 * This is an application frame that displays a background image behind the
 * screen beans and components.
 * 
 * @version $Revision: /main/16 $
 */
public class ImageAppFrame extends ApplicationFrame
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 7928462588704558033L;

    /** revision number supplied by PVCS * */
    public static String revisionNumber = "$Revision: /main/16 $";

    /** the default window title if no property is specified */
    public static String DEFAULT_TITLE = "360Store - Point of Sale";

    /** the default background image if no property is specified */
    public static String DEFAULT_IMAGE = "watermark_800x600_lantana";

    /** the name of the background image */
    public static String backgroundImageName = null;

    private static String backgroundImageTrainingModeOnName = null;

    private static String backgroundImageTrainingModeOffName = null;

    private static String backgroundImageTransReentryModeName = null;

    protected boolean trainingMode = false;

    protected boolean transReentryMode = false;

    /**
     * Overrides JFrame to use an ImagePanel.
     */
    protected JRootPane createRootPane()
    {
        JRootPane pane = new JRootPane();
        pane.setContentPane(new ImagePanel());
        pane.setOpaque(true);
        return pane;
    }

    /**
     * Sets a properties object on the frame. Overridden to set the background image.
     * 
     * @param props the properties object
     */
    public void configure(Properties props)
    {
        super.configure(props);

        // get the Image properties
        Locale lcl = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String image = UIFactory.getInstance().getUIProperties(lcl).getProperty("backgroundImage", DEFAULT_IMAGE) + ".gif";
        backgroundImageTrainingModeOnName =
            UIFactory.getInstance().getUIProperties(lcl).getProperty("trainingModeImage", DEFAULT_IMAGE) + ".gif";
        backgroundImageTransReentryModeName =
            UIFactory.getInstance().getUIProperties(lcl).getProperty("transReentryModeImage", DEFAULT_IMAGE) + ".gif";

        // if the image name is different, set the new background image
        if (image != null && !image.equals(backgroundImageName))
        {
            Container pane = getRootPane().getContentPane();

            // if the pane supports images, set the image
            if (pane instanceof ImageBeanIfc)
            {
                ((ImageBeanIfc) pane).setBackgroundImage(image);
                backgroundImageName = image;
            }
        }

        if (image != null)
        {
            backgroundImageTrainingModeOffName = image;
        }
        else
        {
            backgroundImageTrainingModeOffName = DEFAULT_IMAGE + ".gif";
        }
    }

    protected void switchTrainingModeImage()
    {
        if (backgroundImageTrainingModeOnName != null)
        {
            String image = backgroundImageTrainingModeOffName;
            if (trainingMode)
            {
                image = backgroundImageTrainingModeOnName;
            }

            if (image != null)
            {
                Container pane = getRootPane().getContentPane();

                // if the pane supports images, set the image
                if (pane instanceof ImageBeanIfc)
                {
                    ((ImageBeanIfc) pane).setBackgroundImage(image);
                    repaint();
                }
            }
        }
    }

    /**
     * Static main() test method.
     * 
     * @param command
     *            line args
     */
    public static void main(String[] args)
    {
        ImageAppFrame af = new ImageAppFrame();

        Properties testProps = new Properties();
        testProps.setProperty("width", "640");
        testProps.setProperty("height", "480");
        testProps.setProperty("title", "Test App Frame");
        testProps.setProperty("backgroundImage", "watermark.gif");

        af.configure(testProps);
        af.setVisible(true);
    }

    /**
     * @return Returns the trainingMode.
     */
    public boolean isTrainingMode()
    {
        return trainingMode;
    }

    /**
     * @param trainingMode
     *            The trainingMode to set.
     */
    public void setTrainingMode(boolean trainingMode)
    {
        this.trainingMode = trainingMode;
    }

    /**
     * @return Returns the transReentryMode.
     */
    public boolean isTransReentryMode()
    {
        return transReentryMode;
    }

    /**
     * @param transReentryMode
     *            The transReentryMode to set.
     */
    public void setTransReentryMode(boolean transReentryMode)
    {
        this.transReentryMode = transReentryMode;
    }
}