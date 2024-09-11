/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/timer/DefaultTimedAction.java /main/11 2013/02/21 12:05:40 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  02/19/13 - Close open dialogs after timeout
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:20:53 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:10:32 PM  Robert Pearse   
 *
 Revision 1.1  2004/03/15 21:55:15  jdeleau
 @scr 4040 Automatic logoff after timeout
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.timer;

import java.awt.event.ActionEvent;
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;

/**
 * This class will mail whatever letter is defined in the actionName variable
 * when the timer expires.
 * 
 * $Revision: /main/11 $
 */
public class DefaultTimedAction implements TimedActionIfc
{
    /** source control revision number */
    public static final String revisionNumber = "$Revision: /main/11 $";
    /** The logger to which log messages will be sent. */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.timer.DefaultTimedAction.class);
    /** the length of this interval */
    protected int interval = 0;
    /** the command that this action fires (in this case letter name) */
    protected String actionName;
    /** the controller that will fire this action */
    protected TimedControllerIfc controller = null;
    /**
     * protects against firing the userInput letter for every single user input
     * detected.
     */
    protected boolean timedOut = false;

    /**
     * Default constructor.
     */
    public DefaultTimedAction()
    {
    }

    /**
     * Constructor that sets the interval length and the name.
     * 
     * @param aInterval
     *            the interval length
     * @param anAction
     *            the action name
     */
    public DefaultTimedAction(int aInterval, String anAction)
    {
        setTimeInterval(aInterval);
        setTimedActionName(anAction);
    }

    //--------------------------------------------------------------------------
    /**
     * Implmentation of the ActionListener interface. Mails a letter and starts
     * the next interval when an action event is received.
     * 
     * @param e
     *            the action event fired by the end of the interval
     */
    public void actionPerformed(ActionEvent e)
    {
        // A negative value indicates infinity
        if (controller != null && getTimeInterval() >= 0)
        {
            try
            {
                // close any open dialogs and enable letters
                POSJFCUISubsystem.getInstance().closeDialog();
                POSJFCUISubsystem.getInstance().enableLetters();
            }
            catch (UIException e1)
            {
                logger.error(e1);
            }
            POSJFCUISubsystem.getInstance().mail(new Letter(actionName));
            timedOut = true;
            controller.cycleNextAction();
        }
    }

    /**
     * Causes the controller to reset its listeners when user activity is
     * detected. Subclasses should override this for specific behavior
     * 
     * @see oracle.retail.stores.pos.ui.timer.TimedActionIfc#doInterrupt()
     */
    public void doInterrupt()
    {
        if (controller != null)
        {
            controller.resetController();
        }
    }

    /**
     * Returns the length (in milliseconds) of a timeout before this action
     * will execute.
     * 
     * @return the interval length
     * @see oracle.retail.stores.pos.ui.timer.TimedActionIfc#getTimeInterval()
     */
    public int getTimeInterval()
    {
        return interval;
    }

    /**
     * Returns the name of this timed action.
     * 
     * @return the action name
     * @see oracle.retail.stores.pos.ui.timer.TimedActionIfc#getTimedActionName()
     */
    public String getTimedActionName()
    {
        return actionName;
    }

    /**
     * Sets the length of this action's time interval.
     * 
     * @param aValue
     *            the interval length (in milliseconds)
     * @see oracle.retail.stores.pos.ui.timer.TimedActionIfc#setTimeInterval(int)
     */
    public void setTimeInterval(int aValue)
    {
        interval = aValue;
    }

    /**
     * Sets the name of this timed action
     * 
     * @param aName
     *            the action name
     * @see oracle.retail.stores.pos.ui.timer.TimedActionIfc#setTimedActionName(java.lang.String)
     */
    public void setTimedActionName(String aName)
    {
        actionName = aName;
    }

    /**
     * Attaches a controller to this action
     * 
     * @param controller
     *            the controller to attach
     * @see oracle.retail.stores.pos.ui.timer.TimedActionIfc#setController(oracle.retail.stores.pos.ui.timer.TimedControllerIfc)
     */
    public void setController(TimedControllerIfc controller)
    {
        this.controller = controller;
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
