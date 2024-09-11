/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/cidscreens/CIDScreenManager.java /main/15 2014/01/09 16:23:22 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/09/14 - fix null dereferences
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   07/19/10 - add better logging of exception in this class
 *    asinton   07/01/10 - Removed old deprecated methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       02/18/09 - fix all callers of getGroupText and getText to use
 *                         best match locale
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:15 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse
 *
 *   Revision 1.6.2.3  2004/11/16 00:32:51  rzurga
 *   @scr 6552 SwipeAnytime - Cant invoke Select Paymnt screen on CPOI dev
 *   Hide current screen if an action is wanted with another one.
 *
 *   Revision 1.6.2.2  2004/11/09 17:31:16  rzurga
 *   @scr 6552 SwipeAnytime - Cant invoke Select Paymnt screen on CPOI dev
 *   Cleanup comments
 *
 *   Revision 1.6.2.1  2004/11/08 21:04:27  rzurga
 *   scr 6552 SwipeAnytime - Cant invoke Select Paymnt screen on CPOI dev
 *   Add function removeAction() so that the action is dequeued only after it
 *   has been processed. This way waitOnBusyCIDScreen() won't return
 *   in parallel with the last action being processed.
 *
 *   Revision 1.6  2004/07/24 16:43:51  rzurga
 *   @scr 6463 Items are showing on CPOI sell item from previous transaction
 *   Remove newly introduced automatic hiding of non-active CPOI screens
 *   Enable clearing of non-visible CPOI screens
 *   Improve appearance by clearing first, then setting fields and finally showing the CPOI screen
 *
 *   Revision 1.5  2004/07/20 23:31:41  rzurga
 *   @scr 3676 Ingenico does not update balance due if split tender is performed
 *
 *   Improve screen change
 *
 *   Revision 1.4  2004/03/25 20:25:15  jdeleau
 *   @scr 4090 Deleted items appearing on Ingenico, I18N, perf improvements.
 *   See the scr for more info.
 *
 *   Revision 1.3  2004/02/12 16:48:35  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:31:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Dec 11 2003 17:05:10   epd
 * Updates for Ingenico
 *
 *    Rev 1.0   Sep 03 2003 13:11:54   RSachdeva
 * Initial revision.
 * Resolution for POS SCR-3355: Add CIDScreen support
 * ===========================================================================
 */
package oracle.retail.stores.pos.device.cidscreens;

import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import java.util.Queue;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.thread.ThreadManager;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.ResourceBundleUtil;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.device.POSDeviceActionGroup;

/**
 * This class manages the various CIDScreenIfc implementations. It has an
 * actionQueue which all device requests are channeled through in a FIFO manner.
 * All device requests which are not generic are delegated to the particular
 * CIDScreenIfc for which that action may have a particular meaning via the
 * processAction() method. The actionQueue is processed on its separate thread,
 * and goes into a wait state when there are no actions pending.
 */
public class CIDScreenManager extends POSDeviceActionGroup implements Runnable
{
    private static final long serialVersionUID = 6882742071243024287L;

    /** The actionQueue which CIDActions are put on */
    protected ActionQueue actionQueue;
    /** The screen being managed */
    protected CIDScreenIfc visibleScreen = null;
    /** properties for I18N */
    protected Properties properties;

    /**
     * Constructor
     */
    public CIDScreenManager()
    {
        actionQueue = new ActionQueue();
        new Thread(this, "CIDScreenManager-" + ThreadManager.getNextThreadNumber()).start();
    }

    /**
     * Set the properties
     * 
     * @param props I18N properties object
     */
    public void setProperties(Properties props)
    {
        this.properties = props;
    }

    /**
     * Return the I18N properties, if they arent set it will set them with
     * values from the cidScreenText bundle
     * 
     * @return properties
     */
    public Properties getProperties()
    {
        if(this.properties == null)
        {
            Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEVICES);
            Locale bestMatchLocale = LocaleMap.getBestMatch(locale);
            Properties props = ResourceBundleUtil.getGroupText("IngenicoLineItem",
              BundleConstantsIfc.CID_SCREENS_BUNDLE_NAME, bestMatchLocale);
            setProperties(props);
        }
        return this.properties;
    }

    /**
     * This is called from DeviceActions.execute in foundation. This causes the
     * DeviceTechnician to wait if the device is busy (processing actions in its
     * queue).
     */
    public void waitIfBusy()
    {
        while(actionQueue.getSize() > 0);
    }

    /**
     * Get a Screen for the given screen name
     * 
     * @param screenName
     * @return CIDScreenIfc object corresponding to that screen
     */
    protected CIDScreenIfc getScreen(String screenName)
    {
        DeviceTechnicianIfc dt = null;
        CIDScreenIfc screen = null;
        try
        {
            dt = getDeviceTechnician();
        }
        catch (DeviceException e)
        {
            logger.error("Could not retrieve DeviceTechnician.", e);
        }

        if (dt != null)
        {
            try
            {
                screen = (CIDScreenIfc) dt.getDeviceActionGroup(screenName);
            }
            catch (DeviceException e)
            {
                logger.error("Could not get screen \"" + screenName + "\"", e);
            }
        }
        return screen;
    }

    /**
     * Run method executed from the constructor, its the separate command loop
     * that the device manager runs on.  It extracts items from the Queue and
     * sends the proper commands to either the device, or the CIDScreenIfc
     * implementing the processAction method
     * 
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        CIDAction action = null;

        while (true)
        {
            action = actionQueue.getAction();

            if (action != null)
            {
                CIDScreenIfc screen = getScreen(action.getScreenName());
                if (screen != null)
                {
                    screen.setCIDScreenManager(this);
    
                    switch(action.getCommand())
                    {
                        case CIDAction.RESET:
                        {
                            screen.reset();
                            if(screen.isVisible())
                                screen.refresh();
                            break;
                        }
                        case CIDAction.CLEAR: // Implemented by IngenicoBase
                        {
                            try
                            {
                                screen.clear();
                            }
                            catch (DeviceException de)
                            {
                                logger.error("Screen error occurred clearing new screen \"" + screen.getName() + "\"", de);
                            }
                            break;
                        }
                        case CIDAction.HIDE: // Implemented by IngenicoBase
                        {
                            if (screen.isVisible())
                            {
                                try
                                {
                                    screen.hide();
                                    visibleScreen = null;
                                }
                                catch (DeviceException de)
                                {
                                    logger.error("Screen error occurred hiding new screen \"" + screen.getName() + "\"", de);
                                }
                            }
                            break;
                        }
                        case CIDAction.SHOW:
                        {
                            if (visibleScreen != null && visibleScreen.isVisible() && visibleScreen != screen)
                            {
                                try
                                {
                                    visibleScreen.hide();
                                }
                                catch (DeviceException de)
                                {
                                    logger.error("Screen error occurred hiding visible screen \"" + visibleScreen.getName() + "\"", de);
                                }
                            }
                            try
                            {
                                screen.show();
                            }
                            catch (DeviceException de)
                            {
                                logger.error("Screen error occurred showing new screen \"" + screen.getName() + "\"", de);
                            }
                            visibleScreen = screen;
                            break;
                        }
                        case CIDAction.SET_VISIBLE:
                        {
                            // Allows renderer to draw without
                            // calling show() which will cause screen flashing
                            screen.setVisible(true);
                            visibleScreen = screen;
                        }
                        default:
                        {
                            if (screen != visibleScreen && visibleScreen != null)
                            {
                                // If we try to show something else than the currently
                                // visible screen let's hide the current one
                                try
                                {
                                    visibleScreen.hide();
                                }
                                catch (DeviceException de)
                                {
                                    logger.error("Screen error occurred hiding visible screen \"" + visibleScreen.getName() + "\"", de);
                                }
                                visibleScreen.setVisible(false);
                                visibleScreen = null;
                                // We will not by default show the screen, but allow the user to
                                // change the fields while the screen is not visible
                            }
                            screen.processAction(action);
                            break;
                        }
                    }
                }
            }

            // sleep just a little to avoid being greedy with CPU
            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                // ignore
            }
        }
    }

    /**
     * Retrieves a localized text string from properties
     * 
     * @param tag a property bundle tag for localized text
     * @param defaultText a plain text value to use as a default
     * @return a localized text string
     */
    protected String retrieveText(String tag, String defaultText)
    {
        String result = defaultText;

        if (getProperties() != null && tag != null)
        {
            result = getProperties().getProperty(tag, defaultText);
        }

        return result;
    }

    /**
     * Retrieves a localized text string from properties
     * 
     * @param tag a property bundle tag for localized text
     * @return a localized text string
     */
    protected String retrieveText(String tag)
    {
        return (retrieveText(tag, tag));
    }

    /**
     * Add an action to the actionQueue
     * 
     * @param action Action to add
     * @since 7.0
     */
    public void addAction(CIDAction action)
    {
        actionQueue.addAction(action);
    }

    // -------------------------------------------------------------------------
    /**
     * This class represents the list of actions processed by the manager in a
     * FIFO manner. The wait/notify java methods are used to make sure a
     * {@link #getAction()} request always returns an {@link CIDAction}. If one
     * is not available, the get request will block until one is available.
     */
    protected class ActionQueue
    {
        /**
         * FIFO list containing the actions.
         */
        protected Queue<CIDAction> actionQueue = new LinkedList<CIDAction>();

        /**
         * Add an action to the actionQueue, it notifys the getAction method,
         * and if there was a blocking request it unblocks
         * 
         * @param action Action to add
         */
        public synchronized void addAction(CIDAction action)
        {
            actionQueue.add(action);
            notifyAll();
        }

        /**
         * Get the next action to execute. if there is no action to execute, it
         * waits until one becomes available. Does not remove the element from
         * the queue.
         * 
         * @return Action to execute
         */
        protected synchronized CIDAction getAction()
        {
            if (actionQueue.isEmpty())
            {
                try
                {
                    wait();
                }
                catch (InterruptedException ie)
                {
                }
            }

            return actionQueue.remove();
        }

        /**
         * Get the number of pending actions
         *
         *  @return number of pending actions
         */
        protected int getSize()
        {
            return actionQueue.size();
        }
    } // ActionQueue
} // CIDScreenManager
