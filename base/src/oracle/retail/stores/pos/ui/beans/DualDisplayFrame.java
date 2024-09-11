/* ===========================================================================  
* Copyright (c) 2012, 20012, Oracle and/or its affiliates. 
* All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DualDisplayFrame.java /main/1 2013/01/14 18:50:22 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    01/02/13 - dual display initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.Properties;

import javax.swing.UIManager;

import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

/**
 * This is the main frame for dual display application
 * 
 * @author vbongu
 * @since 14.0
 */
public class DualDisplayFrame extends ApplicationFrame
{
    /** Serial version UID to prevent compile warnings */
    private static final long serialVersionUID = 6183703782076352170L;
    
    protected static final Logger logger = Logger.getLogger(DualDisplayFrame.class);

    /**
     * The UI prefix to use when configuring this frame with the
     * {@link UIFactory}.
     */
    public static final String UI_PREFIX = "DualDisplayFrame";

    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    /**
     * Constant for enabling dual display. Setting is found in
     * application.properties.
     */
    public static final String DUALDISPLAY_ENABLED = "DualDisplayEnabled";   
    
    public static final String FOCUSABLE_WINDOW_STATE = ".focusableWindow";
    

    /** enable dual display boolean */
    public static boolean enableDualDisplay = false;   
   
   
    /**
     * Constructor
     */
    public DualDisplayFrame()
    {
        UIFactory.getInstance().configureUIComponent(pane, UI_PREFIX);
    }

    /**
     * Initializes the frame. Gets the value for "DualDisplayEnabled"
     * from "application.properties"
     */
    @Override
    protected void initialize()
    {
        super.initialize();
        enableDualDisplay = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, DUALDISPLAY_ENABLED, false);

    }

    /**
     * Sets the size of this frame and also calls
     * {@link #setFocusableWindowState(boolean)} to false so that the window
     * never gets focus.
     */
    @Override
    public void configure(Properties props)
    {
        super.configure(props);
        String focusableWindow = UIManager.getString(UI_PREFIX + FOCUSABLE_WINDOW_STATE);
        if(StringUtils.isNotEmpty(focusableWindow))
        {
            setFocusableWindowState(UIUtilities.getBooleanValue(focusableWindow));
        }
        

    }

    /**
     * If dual display is enabled the {@link #getDualScreenBounds()} is called to
     * get the dual screen bounds and set it on the frame. 
     */
    @Override
    public void maximizeFrame()
    {
        if (enableDualDisplay)
        {
            Rectangle screenBounds = getDualScreenBounds();
            if (screenBounds != null)
            {
                setBounds(screenBounds.x, screenBounds.y,screenBounds.width, screenBounds.height);
            }
            else
            {
                logger.error("Couldnt find the second screen to display the frame");
            }
        }

    }
    
    /**
     * Gets the dual screen bounds to set on the frame. 
     * Uses {@link GraphicsEnvironment} api to  get 
     * configuration of the second screen. 
     * @return 
     */
    protected Rectangle getDualScreenBounds()
    {              
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        GraphicsDevice defaultDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle bounds = null;
        int i =0;                
        if (screenDevices != null)
        {
            int numberOfScreens = screenDevices.length;
            for (i=0; i <numberOfScreens; i++)
            {
                if (defaultDevice != screenDevices[i])
                {                                   
                    bounds = screenDevices[i].getDefaultConfiguration().getBounds();                   
                    break;
                }
            }
        }
        return bounds;
    }


}
