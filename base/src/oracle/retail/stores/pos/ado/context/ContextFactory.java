/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/context/ContextFactory.java /main/11 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:30 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:20:23 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:11 PM  Robert Pearse   
 *
 * Revision 1.3  2004/04/08 20:33:03  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:10:02 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:30:00 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.context;

import oracle.retail.stores.foundation.tour.service.TourContext;

/**
 * Singleton class to contain cached context. It is intended that this context
 * will be updated by Launch Shuttles with the bus from the new service.
 * 
 * @deprecated as of 13.4.1. This class does not support multiple running buses
 *             at the same time. Use {@link TourContext} instead.
 */
public class ContextFactory
{
    /**
     * Singleton instance
     */
    private static ContextFactory instance;

    /**
     * Cached context.
     */
    protected ADOContextIfc context;

    /**
     * Protected to enforce singleton pattern
     */
    protected ContextFactory()
    {
    }

    public static ContextFactory getInstance()
    {
        synchronized (ContextFactory.class)
        {
            if (instance == null)
            {
                instance = new ContextFactory();
            }
            return instance;
        }
    }

    /**
     * @return
     */
    public synchronized ADOContextIfc getContext()
    {
        if (context == null)
        {
            return new TourADOContext(TourContext.getInstance().getTourBus());
        }
        return context;
    }

    /**
     * @param context
     */
    public synchronized void setContext(ADOContextIfc context)
    {
        this.context = context;
    }

}
