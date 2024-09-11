/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/timer/DefaultEventMonitor.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:36 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:20:52 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:10:32 PM  Robert Pearse   
 *
 Revision 1.2  2004/04/08 20:33:03  cdb
 @scr 4206 Cleaned up class headers for logs and revisions.
 *
 Revision 1.1  2004/03/15 21:55:15  jdeleau
 @scr 4040 Automatic logoff after timeout
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.timer;

import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * A listener on the AWT event queue that peeks in the queue at specified
 * intervals. If it finds no events in the queue, it will trigger the interval
 * timer in the time monitor. It will also interrupt the monitor if any user
 * activity is detected.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class DefaultEventMonitor implements EventMonitorIfc, ActionListener, AWTEventListener
{
    /** source control revision number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /** time between peeks into the event queue */
    private int peekTime;
    /** the timer that checks the event queue */
    private Timer peekTimer = null;
    /** the controller to notify when events arrive */
    private TimedControllerIfc controller = null;

    /**
     * Default constructor. Sets monitor to null
     */
    public DefaultEventMonitor()
    {
        this(null);
    }

    /**
     * Constructor that attaches a controller to this monitor
     * 
     * @param controller
     *            the controller to attach
     */
    public DefaultEventMonitor(TimedControllerIfc controller)
    {
        this(controller, EventMonitorIfc.DEFAULT_PEEK_TIME);
    }

    /**
     * Constructor that attaches a controller and sets the peek time.
     * 
     * @param controller
     *            the monitor to attach
     * @param aTime
     *            the peek time
     */
    public DefaultEventMonitor(TimedControllerIfc controller, int aTime)
    {
        setController(controller);
        setPeekTime(aTime);
    }

    /////////////////////////////////////////////////////////////////////////
    // Implement ActionListener Interface
    /////////////////////////////////////////////////////////////////////////
    /**
     * Activates this trigger.
     * 
     * @see oracle.retail.stores.pos.ui.timer.EventMonitorIfc#activate()
     */
    public void activate()
    {
        if(peekTimer != null)
        {
            peekTimer.start();
        }
    }

    /**
     * Deactivates this trigger.
     * 
     * @see oracle.retail.stores.pos.ui.timer.EventMonitorIfc#deactivate()
     */
    public void deactivate()
    {
        if(peekTimer != null)
        {
            peekTimer.stop();
        }
        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
    }

    /**
     * @param aTime
     * @see oracle.retail.stores.pos.ui.timer.EventMonitorIfc#setPeekTime(int)
     */
    public void setPeekTime(int aTime)
    {
        peekTime = aTime;
        if(peekTime >= 0)
        {
            this.peekTimer = new Timer(peekTime, this);
        }
    }

    /**
     * @param controller
     * @see oracle.retail.stores.pos.ui.timer.EventMonitorIfc#setController(oracle.retail.stores.pos.ui.timer.TimedControllerIfc)
     */
    public void setController(TimedControllerIfc controller)
    {
        this.controller = controller;
    }

    /////////////////////////////////////////////////////////////////////////
    // Implement ActionListener Interface
    /////////////////////////////////////////////////////////////////////////
    /**
     * Processes an action event from the timer that is monitoring the event
     * queue. The swingTimer calls this when the timer expires.
     * 
     * @param e ActionEvent passed by the swing timer
     */
    public void actionPerformed(ActionEvent e)
    {
        // check to see if the event queue is empty
        if (Toolkit.getDefaultToolkit().getSystemEventQueue().peekEvent() == null)
        {
            // stop the peek timer
            peekTimer.stop();
            // if the controller is not null
            // notify it to start the next time interval
            if (controller != null)
            {
                controller.cycleNextAction();
                // attach the monitor as an event queue listener
                Toolkit.getDefaultToolkit().addAWTEventListener(
                  this, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // Implement AWTEventListener
    /////////////////////////////////////////////////////////////////////////
    /**
     * Processes an AWTEvent fired by the AWTEventQueue. This method will
     * remove the trigger as an event listener and send an interrupt message to
     * the controller.
     * 
     * @param e
     *            the AWTEvent
     */
    public void eventDispatched(AWTEvent e)
    {
        // Ignore certain mouse events that are not truly user input
        if (e.getID() != Event.MOUSE_ENTER && e.getID() != Event.MOUSE_EXIT)
        {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this);
            // if the controller exists, send an interrupt
            if (controller != null)
            {
                controller.fireInterrupt();
            }
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
    public String toString()
    {
        return "Class: " + this.getClass().getName() + "(Revision " + getRevisionNumber() + ") @" + hashCode();
    }
}
