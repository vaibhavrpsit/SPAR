/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/store/StoreFactory.java /rgbustores_13.4x_generic_branch/2 2011/09/26 09:48:08 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/26/11 - made key constants public
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:30:11 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:25:33 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:14:27 PM  Robert Pearse   
 *
 * Revision 1.5  2004/07/23 22:17:26  epd
 * @scr 5963 (ServicesImpact) Major update.  Lots of changes to fix RegisterADO singleton references and fix training mode
 *
 * Revision 1.4  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:12:22 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:32:54 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.store;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.factory.FactoryException;
import oracle.retail.stores.foundation.tour.gate.Gateway;

/**
 * Factory class used to generate store related entities such as store,
 * register, till, etc.
 */
public class StoreFactory
{
    /** Class logger */
    protected static final Logger logger = Logger.getLogger(StoreFactory.class);

    /** The singleton instance */
    protected static StoreFactory instance;

    /** property file key for Register ADO */
    public static final String REGISTER_KEY = "ado.register";
    /** property file key for Store ADO */
    public static final String STORE_KEY = "ado.store";
    
    /** protected constructor to prevent direct instantiation */
    protected StoreFactory()
    {
    }

    /**
     * Factory method to return singleton instance
     * 
     * @return the store factory
     */
    public static StoreFactory getInstance()
    {
        synchronized (StoreFactory.class)
        {
            if (instance == null)
            {
                instance = new StoreFactory();
            }
            return instance;
        }
    }
    
    /**
     * Create a new RegisterADO instance
     * NOTE:  If the setUseTrainingRegister method
     *        was set to true, the training register
     *        instance will be returned.
     * 
     * @return a RegisterADO instance;
     */
    public RegisterADO getRegisterADOInstance()
    {
        try
        {
            String className = Gateway.getProperty("application", REGISTER_KEY, "");
            if (className.length() == 0)
            {
                 throw new FactoryException("Failed to find class for " + REGISTER_KEY);
            }
            Class registerADOClass = Class.forName(className);
            return (RegisterADO)registerADOClass.newInstance();
        }
        catch (FactoryException e)
        {
            logger.error("Could not instantiate RegisterADO instance: " + REGISTER_KEY, e);
            throw e;
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Could not instantiate RegisterADO instance: " + REGISTER_KEY, e);
            throw new FactoryException("Class not found for " + REGISTER_KEY, e);
        }
        catch (InstantiationException e)
        {
            logger.error("Could not instantiate RegisterADO instance: " + REGISTER_KEY, e);
            throw new FactoryException("Failed to Instantiate helper for " + REGISTER_KEY, e);
        }
        catch (IllegalAccessException e)
        {
            logger.error("Could not instantiate RegisterADO instance: " + REGISTER_KEY, e);
            throw new FactoryException("IllegalAccessException creating helper for " + REGISTER_KEY, e);
        }
        catch (NullPointerException e)
        {
            logger.error("Could not instantiate RegisterADO instance: " + REGISTER_KEY, e);
            throw new FactoryException("Failed to find class for " + REGISTER_KEY, e);
        }
        catch (Throwable eth)
        {
            logger.error("Could not instantiate RegisterADO instance: " + REGISTER_KEY, eth);
            throw new FactoryException("Failed to create TDO for " + REGISTER_KEY, eth);
        }
    }

    /**
     * Create a new RegisterADO instance
     * 
     * @return a RegisterADO instance;
     */
    public StoreADO getStoreADOInstance()
    {
        try
        {
            String className = Gateway.getProperty("application", STORE_KEY, "");
            if (className.length() == 0)
            {
                 throw new FactoryException("Failed to find class for " + STORE_KEY);
            }
            Class storeADOClass = Class.forName(className);
            return (StoreADO)storeADOClass.newInstance();
        }
        catch (FactoryException e)
        {
            logger.error("Could not instantiate StoreADO instance: " + STORE_KEY, e);
            throw e;
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Could not instantiate StoreADO instance: " + STORE_KEY, e);
            throw new FactoryException("Class not found for " + STORE_KEY, e);
        }
        catch (InstantiationException e)
        {
            logger.error("Could not instantiate StoreADO instance: " + STORE_KEY, e);
            throw new FactoryException("Failed to Instantiate helper for " + STORE_KEY, e);
        }
        catch (IllegalAccessException e)
        {
            logger.error("Could not instantiate StoreADO instance: " + STORE_KEY, e);
            throw new FactoryException("IllegalAccessException creating helper for " + STORE_KEY, e);
        }
        catch (NullPointerException e)
        {
            logger.error("Could not instantiate StoreADO instance: " + STORE_KEY, e);
            throw new FactoryException("Failed to find class for " + STORE_KEY, e);
        }
        catch (Throwable eth)
        {
            logger.error("Could not instantiate StoreADO instance: " + STORE_KEY, eth);
            throw new FactoryException("Failed to create TDO for " + STORE_KEY, eth);
        }
    }

}
