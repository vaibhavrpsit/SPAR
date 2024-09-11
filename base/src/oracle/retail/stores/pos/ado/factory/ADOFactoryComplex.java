/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/factory/ADOFactoryComplex.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
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
package oracle.retail.stores.pos.ado.factory;

import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.foundation.tour.gate.Gateway;

/**
 * Container for the ADO factory classes.
 */

public class ADOFactoryComplex
{
    /** Constant for property group name */
    private static String APP_PROP_GROUP = "application";

    /** Constant for empty string */
    private static String EMPTY_STRING = "";

    /**
     * Constructor for ADOFactoryComplex Private to disallow creation of
     * instance
     */
    private ADOFactoryComplex()
    {
    }

    /**
     * Creates and returns an instance of the the TDO class for the specified
     * service name
     * 
     * @param svcName
     * @return reference to a TDO
     * @throws TDOException
     *             on error
     */
    public static ADOFactoryIfc getFactory(String adoId) throws ADOException
    {

        try
        {
            String className =
                Gateway.getProperty(APP_PROP_GROUP, adoId, EMPTY_STRING);
            if (className.length() == 0)
            {
                throw new ADOException(
                    "Failed to find factory class for " + adoId);
            }
            Class facClass = Class.forName(className);
            return (ADOFactoryIfc) facClass.newInstance();
        }
        catch (ADOException e)
        {
            throw e;
        }
        catch (ClassNotFoundException e)
        {
            throw new ADOException("Factory Class not found for " + adoId, e);
        }
        catch (InstantiationException e)
        {
            throw new ADOException(
                "Failed to Instantiate factory for " + adoId,
                e);
        }
        catch (IllegalAccessException e)
        {
            throw new ADOException(
                "IllegalAccessException creating factory for " + adoId,
                e);
        }
        catch (NullPointerException e)
        {
            throw new ADOException("Failed to find class for " + adoId, e);
        }
        catch (Throwable eth)
        {
            throw new ADOException(
                "Failed to create factory for " + adoId,
                eth);
        }
    }
}
