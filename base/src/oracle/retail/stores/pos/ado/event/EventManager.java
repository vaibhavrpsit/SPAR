/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/event/EventManager.java /main/12 2014/01/13 13:09:39 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  01/13/14 - remove double-checked locking
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:57 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/08/02 23:07:48  mweis
 *   @scr 4206 JavaDoc updates.
 *
 *   Revision 1.2  2004/02/12 16:47:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Feb 04 2004 14:18:02   rhafernik
 * Logger cleanup
 * 
 *    Rev 1.0   Nov 04 2003 11:10:34   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 17 2003 12:30:28   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class acts as a Mediator between entities that require
 * interaction of some kind.  It serves to decouple those
 * entities but provides a mechanism of communication.
 *
 */
public class EventManager
{
    /** Singleton instance */
    private static EventManager instance = null;

    /** listener map */    
    private HashMap listenerMap = new HashMap(0);
    
    /**
    * Returns the singleton instance of the EventManager.
    * @return The singleton instance of the EventManager.
    **/
    public synchronized static EventManager getInstance()
    {
        if (instance == null)
        {
            instance = new EventManager();
        }

        return instance;
    }
    
    /**
    * Protected default constructor. Prevents the EventManager being directly
    * constructed.
    **/
    protected EventManager()
    {}
    
    /**
     * Register a listener with this EventManager 
     * @param eventType The type of event the listener is interested in.
     * @param listener The listener.
     */
    public void register(EventTypeEnum eventType, EventManagerListenerIfc listener)
    {
        this.getListenerList(eventType).add(listener);
    }
    
    /**
     * Get a list of listeners that are registered to listen for a 
     * particular event.
     * @param eventType The event type that we are interested in
     * @return The list of listeners.
     */
    protected ArrayList getListenerList(EventTypeEnum eventType)
    {
        ArrayList list = (ArrayList)listenerMap.get(eventType);
        if (list == null)
        {
            list = new ArrayList();
            listenerMap.put(eventType, list);
        }
        return list;
    }
    
    /**
     * Determines whether anything is listening for a particular event type
     * @param eventType The event type.
     * @return A flag indicating whether anyone is listening for the eventType.
     */
    protected boolean exists(EventTypeEnum eventType)
    {
        return (listenerMap.get(eventType) != null);
    }
    
    /**
     * Removes a listener from the EventManager for a specific event type
     * @param eventType The event type from which we want to deregister.
     * @param listener The listener to remove.
     */
    public void remove(EventTypeEnum eventType, EventManagerListenerIfc listener)
    {
        // Make sure a ListenerList exists in the map, if one does not exist
        // this unregister is considered redundant
        if (this.exists(eventType))
        {
            // removeListener will return false if the listener was not
            // registered in the first place, if this is so the unregister
            // is considered redundant
            this.getListenerList(eventType).remove(listener);
        }
    }
    
    /**
     * The message notification mechanism.  This method creates a 
     * ManagedEvent and sends it off to each listener.
     * @param source
     * @param eventType
     */
    public void notify(Object source, EventTypeEnum eventType)
    {
        // Make sure a list exists in the map, if one does
        // not exist this notification is considered redundant
        if (this.exists(eventType))
        {
            ArrayList list = this.getListenerList(eventType);
            // If there are any listeners in the list notify them
            if (list.size() > 0)
            {
                ManagedEvent me = new ManagedEvent(source, eventType);
                Iterator it = list.iterator();
                while (it.hasNext())
                {
                    EventManagerListenerIfc eml = (EventManagerListenerIfc)it.next();
                    eml.eventNotification(me);
                }
            }
        }
    }
}
