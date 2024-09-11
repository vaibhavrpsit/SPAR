/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/tdo/TDOFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.tdo;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.foundation.tour.gate.Gateway;

/**
 * Factory class to create TDO instances based on a provided tdoId.
 * The tdoid is declared in the application.properties file
 */
public final class TDOFactory
{
    /** Constant for property group name */
    private static String APP_PROP_GROUP = "application";
    
    /** Constant for empty string */
    private static String EMPTY_STRING = "";
    
    /**
     * Constructor for TDOFactoryComplex 
     * Private to disallow creation of instance
     */
    private TDOFactory()
    {
    }

    /**
     * Creates and returns an instance of the the TDO class for the
     * specified service name
     * @param svcName
     * @return reference to a TDO
     * @throws TDOException on error
     */
    public static TDOIfc create(String tdoId) throws TDOException
    {
        
        try
        {
            String className = Gateway.getProperty(APP_PROP_GROUP, tdoId, EMPTY_STRING);
            if (className.length() == 0)
            {
                 throw new TDOException("Failed to find class for " + tdoId);
            }
            Class tdoClass = Class.forName(className);
            return (TDOIfc)tdoClass.newInstance();
        }
        catch (TDOException e)
        {
             throw e;
        }
        catch (ClassNotFoundException e)
        {
            throw new TDOException("Class not found for " + tdoId, e);
        }
        catch (InstantiationException e)
        {
            throw new TDOException("Failed to Instantiate helper for " + tdoId, e);
        }
        catch (IllegalAccessException e)
        {
            throw new TDOException("IllegalAccessException creating helper for " + tdoId, e);
        }
        catch (NullPointerException e)
        {
            throw new TDOException("Failed to find class for " + tdoId, e);
        }
        catch (Throwable eth)
        {
            throw new TDOException("Failed to create TDO for " + tdoId, eth);
        }
    }

    /**
     * Creates and returns an instance of the the TDO class for the
     * specified bean id
     * @param beanId this is Spring Bean
     * @return reference to a TDO
     */
    public static TDOIfc createBean(String beanId)
    {
    	TDOIfc bean = (TDOIfc)BeanLocator.getApplicationBean(beanId);
    	return bean;
    }
}
