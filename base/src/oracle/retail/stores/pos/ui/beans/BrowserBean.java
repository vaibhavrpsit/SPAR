/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BrowserBean.java /main/15 2012/10/29 16:37:48 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    10/29/12 - deprecating class
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:17 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:37 PM  Robert Pearse   
 *
 *   Revision 1.4.4.1  2004/10/18 19:34:37  jdeleau
 *   @scr 7291 Integrate ibV6 and remove Ib5 from installation procedure
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:16  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 07 2004 10:32:36   sfl
 * static void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) method
 * can be called just once per JVM.
 * Resolution for 3596: Selecting Web Store w/ Cookies Accepted = Yes hangs POS
 * 
 *    Rev 1.0   Aug 29 2003 16:09:38   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Jul 08 2003 18:38:34   baa
 * allow web order to have focus when tab key is pressed
 * Resolution for 2259: Web Store -No focus w/in website does not allow user to navigate w/out mouse
 * 
 *    Rev 1.2   Aug 14 2002 18:16:48   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   24 Jul 2002 11:25:02   jbp
 * removed deprecated dependencies
 * Resolution for POS SCR-1761: Icebrowser update
 *
 *    Rev 1.0   Apr 29 2002 14:54:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:53:34   msg
 * Initial revision.
 *
 *    Rev 1.8   11 Mar 2002 13:30:34   jbp
 * implement ice.storm.VeiwportCallback
 * Resolution for POS SCR-1351: New IceBrowser Jar
 *
 *    Rev 1.7   05 Mar 2002 20:01:44   jbp
 * enable buttons for WebStore.
 * Resolution for POS SCR-1351: New IceBrowser Jar
 *
 *    Rev 1.6   19 Feb 2002 12:26:16   jbp
 * changes for new IceBrowser jar
 * Resolution for POS SCR-1351: New IceBrowser Jar
 *
 *    Rev 1.5   Jan 25 2002 07:29:50   mpm
 * Corrected merge failure in new UI integration.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import oracle.retail.stores.pos.ui.behavior.BrowserButtonListener;
import oracle.retail.stores.pos.ui.behavior.IceBrowserHandler;
import oracle.retail.stores.pos.ui.behavior.NoBrowserHandler;

/**
*  This class is used to display an internet browser in the POS application.
*  @deprecated as of 14.0 Use {@link oracle.retail.stores.pos.ui.beans.BrowserFoundationDisplayBean} instead.
**/
public class BrowserBean extends OnlineOfficeBean implements ActionListener
{
    /**
    *   revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
    *   Constant for the BACK action.
    **/
    public static final String BACK    = "Back";
    /**
    *   Constant for the FORWARD action.
    **/
    public static final String FORWARD = "Forward";

    /**
    *  Listens for browser button events.
    **/
    public BrowserButtonListener browserButtonListener = null;


    /**
    *  Class Constructor
    **/
    public BrowserBean()
    {
        try
        {
            super.setHandler(new IceBrowserHandler(this));
        }
        catch(NoClassDefFoundError err)
        {
           super.setHandler(new NoBrowserHandler(this));
        }
    }

    /**
    *   Activates the bean when it is added to the screen.
    *   This is where a bean can "initialize" itself by registering necessary
    *   listeners, setting appropriate action states, etc.
    **/
    public void activate()
    {
        // check model to see if cookies are enabled.
        if (bbModel.isCookiesEnabled())
        {
            //enableCookies();
        }
        getHandler().activate();
    }

    /**
    *   Configures the buttons for local navigation of the browser.
    **/
    public void configureButtons()
    {
        getHandler().configureButtons();
    }

    /**
    *   Adds (actually sets) the yesno listener on the Yes/No button.
    *   @param listener the Browser Button Listener
    **/
    public void addBrowserButtonListener(BrowserButtonListener listener)
    {
        browserButtonListener = listener;
    }

    /**
    *   Removes (actually resets) the yes no listener on the Yes/No button.
    *   @param listener the Browser Button Listener
    **/
    public void removeBrowserButtonListener(BrowserButtonListener listener)
    {
        browserButtonListener = null;
    }
    
    /**
     * User performed an action on the Browser Bean, propogate action
     * on to the handler
     *  
     * @param ae Action Event
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent ae)
    {
        getHandler().actionPerformed(ae);
    }

    /**
    *  Overrides parant setVisible.
    *  @param visible true if the object should be visible
    **/
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        
        if (visible)
        {
            // set the initial focus on the task button bar
            browserButtonListener.enableButton(BACK, false);
            browserButtonListener.enableButton(FORWARD, false);
         }
        
    }
}
