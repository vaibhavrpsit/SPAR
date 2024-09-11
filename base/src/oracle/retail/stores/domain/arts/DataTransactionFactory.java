/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/DataTransactionFactory.java /main/10 2011/01/27 19:03:03 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/27/11 - refactor creation of data transactions to use spring
 *                         context
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         1/27/2006 10:03:32 AM  Ron W. Haight
 *         Compatibility updates for store server replatform
 *    3    360Commerce 1.2         3/31/2005 4:27:41 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:48 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:28 PM  Robert Pearse
 *
 *   Revision 1.3  2004/06/03 14:47:36  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.2  2004/04/20 18:07:51  epd
 *   @scr 4243 Synchronized create method to make threadsafe
 *
 *   Revision 1.1  2004/04/01 20:07:37  epd
 *   @scr 4243 Updates for new Database Transaction Factory
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.factory.FactoryException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataTransactionIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.utility.ReflectionUtility;

import org.apache.log4j.Logger;


/**
 *  This is a factory class for DataTranasctionIfc instances.
 */
public class DataTransactionFactory
{
    /** Log4J Logger */
    private static final Logger logger = Logger.getLogger(DataTransactionFactory.class);

    /** Constant for property group name */
    public static final String DOMAIN_PROP_GROUP = "domain";

    /** Key for factory class name in properties */
    public static final String FACTORY_CLASS_KEY = "DataTransactionFactory";

    /** Key for Helper class name in properties */
    public static final String HELPER_CLASS_KEY = "DataTransactionHelper";

    /** Default helper class */
    public static final String DEFAULT_HELPER_CLASS = "oracle.retail.stores.foundation.manager.data.DataTransactionHelper";

    /** Constant for empty string */
    private static final String EMPTY_STRING = "";

    /** Class name of DataTransactionHelper implementation */
    private static String helperClassName = null;

    /** Singleton instance to lazily create. */
    private static DataTransactionFactory instance;

    /**
     * Constructor for DataTransactionFactory
     * Private to disallow creation of instance
     */
    private DataTransactionFactory()
    {
    }

    /**
     * Lazily create and return the configured instance of this factory.
     *
     * @return
     */
    public static DataTransactionFactory getInstance()
    {
        if (instance == null)
        {
            String classname = Gateway.getProperty(DOMAIN_PROP_GROUP, FACTORY_CLASS_KEY, EMPTY_STRING);
            if (classname == null || classname.trim().equals(EMPTY_STRING) ||
                    classname.equals(DataTransactionFactory.class.getName()))
            {
                instance = new DataTransactionFactory();
            }
            else
            {
                try
                {
                    instance = (DataTransactionFactory)ReflectionUtility.createClass(classname);
                }
                catch (Exception e)
                {
                    logger.fatal("Could not create instance of DataTransactionFactory.", e);
                }
            }
        }
        return instance;
    }

    /**
     * Retrieve the singleton instance and call {@link #createDataTransaction(String)}.
     * Callers should use a constant from {@link DataTransactionKeys}.
     * 
     * @param dataTransactionName Spring context key, e.g. "persistence_AuditLogTransaction".
     * @return
     * @throws FactoryException
     */
    public static synchronized DataTransactionIfc create(String dataTransactionName) throws FactoryException
    {
        return getInstance().createDataTransaction(dataTransactionName);
    }

    /**
     * Creates and returns an instance of the the data transaction class for the
     * specified key to a desired data transaction type. This method uses Spring and the PersistenceContext.xml
     * to retrieve instances of {@link DataTransactionIfc}s.
     * Callers should use a constant from {@link DataTransactionKeys}.
     * 
     * @param dataTransactionKey Spring context key, e.g. "persistence_AuditLogTransaction".
     * @return reference to the desired Data Transaction
     * @throws FactoryException on error
     */
    public DataTransactionIfc createDataTransaction(String dataTransactionKey) throws FactoryException
    {
        DataTransactionIfc dt = null;
        try
        {
            dt = (DataTransactionIfc)BeanLocator.getPersistenceBean(dataTransactionKey);
        }
        catch (Throwable t)
        {
            throw new FactoryException("Unable to create DataTransaction for key \"" + dataTransactionKey + "\".", t);
        }
        if (dt instanceof DataTransaction)
        {
            DataTransaction tran = (DataTransaction)dt;
            tran.setHelperClassName(getHelperClassName());
        }
        return dt;
    }

    private static String getHelperClassName()
    {
        // Get the name of the helper class from the domain properties
        if (helperClassName == null)
        {
            helperClassName = Gateway.getProperty(DOMAIN_PROP_GROUP, HELPER_CLASS_KEY, DEFAULT_HELPER_CLASS);
        }
        return helperClassName;
    }
}
