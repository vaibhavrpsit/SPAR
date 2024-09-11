/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/JournalFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:42 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:28:48 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:57 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:10 PM  Robert Pearse   
 *
 * Revision 1.3.4.1  2004/11/12 14:28:53  kll
 * @scr 7337: JournalFactory extensibility initiative
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:11:12 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:31:20 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.foundation.tour.gate.Gateway;

/**
 *  
 */
public class JournalFactory implements JournalFactoryIfc
{
    
    /**
     * The cached journal instance
     */
    protected RegisterJournalIfc registerJournal;

    /**
     * Singleton instance
     */
    protected static JournalFactoryIfc instance;

    /**
     * private to enforce Singleton pattern
     */
    private JournalFactory()
    {
    }

    /**
     * Singleton factory method
     * 
     * @return
     */
    public static JournalFactoryIfc getInstance() throws ADOException
    {        
        final String APP_PROP_GROUP = "application";
        final String UTILITY_KEY = "ado.JournalFactory";
        final String DEFAULT = JournalFactory.class.getName();
        
        if (instance == null)
        {
            try
            {
                
                String className =
                    Gateway.getProperty(APP_PROP_GROUP, UTILITY_KEY, DEFAULT);
                
                if (className.length() == 0)
                {
                    throw new ADOException(
                                    "Failed to find factory class for " + UTILITY_KEY);
                }
                Class utilityClass = Class.forName(className);
                instance = (JournalFactoryIfc) utilityClass.newInstance();
                return instance;
            }
            catch (ADOException e)
            {
                throw e;
            }
            catch (ClassNotFoundException e)
            {
                throw new ADOException(
                                "Factory Class not found for " + UTILITY_KEY,
                                e);
            }
            catch (InstantiationException e)
            {
                throw new ADOException(
                                "Failed to Instantiate factory for " + UTILITY_KEY,
                                e);
            }
            catch (IllegalAccessException e)
            {
                throw new ADOException(
                                "IllegalAccessException creating factory for " + UTILITY_KEY,
                                e);
            }
            catch (NullPointerException e)
            {
                throw new ADOException(
                                "Failed to find class for " + UTILITY_KEY,
                                e);
            }
            catch (Throwable eth)
            {
                throw new ADOException(
                                "Failed to create factory for " + UTILITY_KEY,
                                eth);
            }
        }
        return instance;
    }

    /**
     * Journal factory method
     */
    public RegisterJournalIfc getRegisterJournal()
    {
        if (registerJournal == null)
        {
            registerJournal = new RegisterJournal();
        }
        return registerJournal;
    }
}
