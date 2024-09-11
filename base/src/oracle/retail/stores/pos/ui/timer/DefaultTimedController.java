/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/timer/DefaultTimedController.java /rgbustores_13.4x_generic_branch/2 2011/07/08 12:43:58 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/08/11 - add debug logging and printlns
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:20:53 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:32 PM  Robert Pearse   
 *
 * Revision 1.1  2004/03/15 21:55:15  jdeleau
 * @scr 4040 Automatic logoff after timeout
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.timer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import javax.swing.Timer;

import org.apache.log4j.Logger;

/**
 * The default implementation of {@link TimedControllerIfc}.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class DefaultTimedController implements TimedControllerIfc
{
    /** source control revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";
    /** debug logger. */
    private static final Logger logger = Logger.getLogger(DefaultTimedController.class);
    /** list of timed actions controlled by this object */
    private ArrayList<TimedActionIfc> timedActions = new ArrayList<TimedActionIfc>();
    /** the timer that manages the intervals */
    private Timer intervalTimer = null;
    /** the event monitor that starts and interrupts the intervals */
    private EventMonitorIfc monitor = null;
    /** an iterator that tracks the current timed action */
    private ListIterator<TimedActionIfc> iterator = null;
    /** the current timed action */
    private TimedActionIfc currentAction = null;
    /** whether this controller is running */
    private boolean active = false;

    /**
     * Default constructor.
     */
    public DefaultTimedController()
    {
        this(TimedControllerIfc.DEFAULT_MONITOR);
    }

    /**
     * Constructor that takes the name of a monitor class.
     * 
     * @param aName the name of an EventMonitorIfc
     */
    public DefaultTimedController(String aName)
    {
        loadMonitor(aName);
        initialize();
    }

    /**
     * Adds an interval to this controller.
     * 
     * @param action the timed action
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#addTimedAction(oracle.retail.stores.pos.ui.timer.TimedActionIfc)
     */
    public void addTimedAction(TimedActionIfc action)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Adding timed action: " + action);
        }
        // if this controller is running, stop it first
        checkActive();
        // add the action
        timedActions.add(action);
    }

    /**
     * Clears the action list. If the monitor is running, calling this method
     * will stop it.
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#clearActions()
     */
    public void clearActions()
    {
        logger.debug("Clearing timed actions.");
        // if this controller is running, stop it first
        checkActive();
        // reinitialize the interval list
        timedActions = new ArrayList<TimedActionIfc>();
    }

    /**
     * Configures the interval timer to use the next action in the action list.
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#cycleNextAction()
     */
    public void cycleNextAction()
    {
        if (logger.isDebugEnabled()) System.out.println("DefaultTimedController.cycleNextAction()");
        // if an action is active, remove it as an action listener
        if (currentAction != null)
        {
            intervalTimer.removeActionListener(currentAction);
        }
        // set the new current interval, if one is available
        if (iterator != null && iterator.hasNext())
        {
            currentAction = iterator.next();
            // if the action is not null
            if (currentAction != null && currentAction.getTimeInterval() >= 0)
            {
                // link this controller to the action
                currentAction.setController(this);
                // reconfigure the interval timer
                intervalTimer.setInitialDelay(currentAction.getTimeInterval());
                intervalTimer.addActionListener(currentAction);
                intervalTimer.restart();
            }
        }
    }

    /**
     * Processes an interrupt message from the event monitor.
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#fireInterrupt()
     */
    public void fireInterrupt()
    {
        if (logger.isDebugEnabled()) System.out.println("DefaultTimedController.fireInterrupt()");
        // if an action is active, send an interrupt
        if (currentAction != null)
        {
            intervalTimer.stop();
            currentAction.doInterrupt();
        }
    }

    /**
     * Returns a timed action that matches the given name.
     * 
     * @param aName
     *            the name of the action
     * @return the found action, or null
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#getTimedAction(java.lang.String)
     */
    public TimedActionIfc getTimedAction(String aName)
    {
        // if this controller is running, stop it first
        checkActive();
        // result is null until one is found
        TimedActionIfc result = null;
        // iterate through the action
        for (Iterator<TimedActionIfc> i = timedActions.iterator(); i.hasNext();)
        {
            TimedActionIfc act = i.next();
            // if the name of the action matches our name,
            // set the result
            if (act.getTimedActionName().equals(aName))
            {
                result = act;
                break;
            }
        }
        return result;
    }

    /**
     * Initializes the controller.
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#initialize()
     */
    public void initialize()
    {
        if (logger.isDebugEnabled()) System.out.println("DefaultTimedController.initialize()");
        // initialize the interval timer
        intervalTimer = new Timer(0, null);
        intervalTimer.setRepeats(false);
    }

    /**
     * Determines if the controller is active.
     * 
     * @return true if active, false if inactive
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#isActive()
     */
    public boolean isActive()
    {
        return active;
    }

    /**
     * Convenience method that stops and then starts this controller.
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#resetController()
     */
    public void resetController()
    {
        stopController();
        startController();
    }

    /**
     * Starts the controller.
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#startController()
     */
    public void startController()
    {
        if (logger.isDebugEnabled()) System.out.println("DefaultTimedController.startController()");
        // reset the iterator
        iterator = timedActions.listIterator();
        // start the event monitor
        monitor.activate();
        // set the active flag
        active = true;
    }

    /**
     * Stops the monitor.
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedControllerIfc#stopController()
     */
    public void stopController()
    {
        if (logger.isDebugEnabled()) System.out.println("DefaultTimedController.stopController()");
        // set the current interval to null
        currentAction = null;
        // stop the interval timer
        intervalTimer.stop();
        // disable the event monitor
        monitor.deactivate();
        // iterate through the actions and remove them as listeners
        for (Iterator<TimedActionIfc> i = timedActions.iterator(); i.hasNext();)
        {
            TimedActionIfc act = i.next();
            intervalTimer.removeActionListener(act);
        }
        active = false;
    }

    /**
     * Convenience method that stops the controller if it is active.
     */
    protected void checkActive()
    {
        // if this controller is active, stop it
        if (active)
        {
            stopController();
        }
    }

    /**
     * Creates a EventMonitorIfc instance from a class name.
     * 
     * @param aName the class name
     */
    protected void loadMonitor(String aName)
    {
        try
        {
            // try to create a valid class instance from the string
            Class<?> aClass = Class.forName(aName);
            monitor = (EventMonitorIfc) aClass.newInstance();
            monitor.setController(this);
        }
        catch (Exception e)
        {
            // if it fails, we use one we know is there
            monitor = new DefaultEventMonitor(this);
        }
    }

    /**
     * Returns the source control revision number.
     * 
     * @return the revision number
     */
    public String getRevisionNumber()
    {
        return revisionNumber;
    }

    /**
     * Creates a string representation of this object.
     * 
     * @return string descriptor for this object
     */
    @Override
    public String toString()
    {
        return "Class: " + this.getClass().getName() + "(Revision " + getRevisionNumber() + ") @" + hashCode();
    }
}
