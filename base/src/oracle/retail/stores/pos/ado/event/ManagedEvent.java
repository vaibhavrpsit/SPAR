/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/event/ManagedEvent.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:00 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:23:26 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:32 PM  Robert Pearse   
 *
 * Revision 1.3  2004/04/08 20:33:03  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:10:34 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:30:28 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.event;

/**
 * A simple class that maintains the type and source of the event as well as an
 * event payload that may be used by the event listeners for processing.
 */
public class ManagedEvent
{
    /**
     * The object that invoked the event
     */
    protected Object source;

    /**
     * The event type
     */
    protected EventTypeEnum type;

    /**
     * Any information that needs to be passed
     */
    protected Object payload;

    /**
     * Constructs this event with the source of the event and the event type.
     * 
     * @param source
     *            The source of the event.
     * @param type
     *            The event type.
     */
    public ManagedEvent(Object source, EventTypeEnum type)
    {
        this.source = source;
        this.type = type;
    }

    /**
     * Constructs this event with the source of the event, event type, and the
     * payload.
     * 
     * @param source
     *            The source of the event
     * @param type
     *            The event type
     * @param payload
     *            Information that needs to be passed to listeners.
     */
    public ManagedEvent(Object source, EventTypeEnum type, Object payload)
    {
        this(source, type);
        this.payload = payload;
    }

    /**
     * Return the source
     * 
     * @return
     */
    public Object getSource()
    {
        return source;
    }

    /**
     * Return the type
     * 
     * @return
     */
    public EventTypeEnum getType()
    {
        return type;
    }

    /**
     * Return the payload
     * 
     * @return
     */
    public Object getPayload()
    {
        return payload;
    }

}
