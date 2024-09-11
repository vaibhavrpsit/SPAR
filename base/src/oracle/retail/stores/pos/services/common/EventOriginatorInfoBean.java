/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/EventOriginatorInfoBean.java /rgbustores_13.4x_generic_branch/2 2011/05/05 16:17:09 mszekely Exp $
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
 *  1    360Commerce 1.0         1/10/2008 7:31:18 AM   Manas Sahu      
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;

/**
 * Copyright: Copyright (c) 2006
 * <p>
 * Company: Oracle, Inc.
 * <p>
 * <p>
 * EventOriginatorInfoBean
 * </p>
 * Used for saving the event originator class name, anywhere in the application.
 */

public class EventOriginatorInfoBean
{
    /*
     * Event originator class name
     */
    public static String eventOriginator = null;
    
    /*
     * Name of the Event
     */
    public static AuditLogEventEnum eventName = null;
    
    /**
     * Getter method for the event originator class name
     * @return String Event originator classname
     */
    public static String getEventOriginator()
    {
        return eventOriginator;
    }
    
    /**
     * Setter method of the event originator class name
     * @param eventOriginator Event originator class name
     */
    public static void setEventOriginator(String eventOriginator)
    {
        EventOriginatorInfoBean.eventOriginator = eventOriginator;
    }
    
    /**
     * Getter method for the Event name
     * @return AuditLogEventEnum event enum
     */
    public static AuditLogEventEnum getEventName()
    {
        return eventName;
    }
    
    /**
     * Setter method of the event name
     * @param eventName Event enum
     */
    public static void setEventName(AuditLogEventEnum eventName)
    {
        EventOriginatorInfoBean.eventName = eventName;
    }
}
