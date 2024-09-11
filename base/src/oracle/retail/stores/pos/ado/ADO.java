/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/ADO.java /main/13 2012/11/26 09:21:02 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/15/12 - Modified to support parameter controlled return
 *                         tenders.
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.manager.ManagerIfc;
import oracle.retail.stores.foundation.tour.service.TourContext;
import oracle.retail.stores.pos.ado.context.ADOContextIfc;
import oracle.retail.stores.pos.ado.context.ContextFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactory;
import oracle.retail.stores.pos.ado.journal.JournalFactoryIfc;
import oracle.retail.stores.pos.ado.utility.TenderUtility;
import oracle.retail.stores.pos.ado.utility.TenderUtilityIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;

import org.apache.log4j.Logger;

/**
 * Provides some basic functionality that is common to all ADO objects.
 */
public abstract class ADO implements ADOIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -3186211529044819664L;

    /** The ADO logger */
    protected static final Logger logger = Logger.getLogger(ADO.class);
    /**
	 * Locale for journal information
	 */
    protected static Locale journalLocale = LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL);
    /**
     * Handle to the ParameterManagerIfc.  This must transient due to the fact that 
     * that an ADO must be Serializable and the manager class is not.  Changed to private
     * so that extending classes would have access it through the getter; the getter refreshes
     * the variable if it is null. 
     */
    private transient ParameterManagerIfc parameterManager;

    /**
     * Default constructor for ADO
     *
     */
    public ADO()
    {
        super();
    }

    /**
     * Returns the context for this ADO
     *
     * @return
     * @deprecated as of 13.4.1. Use {@link TourContext} instead.
     */
    protected ADOContextIfc getContext()
    {
        return ContextFactory.getInstance().getContext();
    }

    /**
     * Returns the desired Manager
     *
     * @param managerType
     *            The desired Manager TYPE
     * @return the requested Manager
     */
    protected ManagerIfc getManager(String managerType)
    {
        return getContext().getManager(managerType);
    }

    /**
     * Returns the retrieved property value Note: To retrieve a parameter from
     * application.properties, pass in the String "application" as the value
     * for the propertyGroup.
     *
     * @param propertyGroup
     *            The name of the property group containing the desired
     *            property
     * @param propertyName
     *            The keyname to the desired property
     * @param defaultValue
     *            A default value in the case there is a problem retrieving the
     *            property
     * @return The retrieved property value.
     */
    protected String getProperty(
        String propertyGroup,
        String propertyName,
        String defaultValue)
    {
        return Gateway.getProperty(propertyGroup, propertyName, defaultValue);
    }

    /**
     * Returns the appropriate UtilityIfc implementation
     *
     * @return
     */
    protected UtilityIfc getUtility()
    {
        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            logger.error(e);
            throw new RuntimeException("Configuration problem: could not create instance of UtilityIfc");
        }
        return util;
    }

    //----------------------------------------------------------------------
    /**
        Returns the appropriate TenderUtilityIfc implementation
        @return
    **/
    //----------------------------------------------------------------------
    protected TenderUtilityIfc getTenderUtility()
    {
        TenderUtilityIfc util = null;
        try
        {
            util = TenderUtility.createInstance();
        }
        catch (ADOException e)
        {
            logger.error(e);
            throw new RuntimeException("Configuration problem: could not create instance of TenderUtilityIfc");
        }
        return util;
    }

    //----------------------------------------------------------------------
    /**
        Returns the appropriate TenderUtilityIfc implementation
        @return
    **/
    //----------------------------------------------------------------------
    protected JournalFactoryIfc getJournalFactory()
    {
        JournalFactoryIfc journalFactory = null;
        try
        {
            journalFactory = JournalFactory.getInstance();
        }
        catch (ADOException e)
        {
            logger.error(e);
            throw new RuntimeException("Configuration problem: could not create instance of JournalFactoryIfc");
        }
        return journalFactory;
    }

    /**
     * Set the parameter manager.
     * @param pm
     */
    public void setParameterManager(ParameterManagerIfc pm)
    {
        parameterManager = pm;
    }
    
    /**
     * Get the parameter manager; initialize the object if it is null.
     * @return
     */
    public ParameterManagerIfc getParameterManager()
    {
        if (parameterManager == null)
        {
            parameterManager = (ParameterManagerIfc)Dispatcher.getDispatcher().getManager(ParameterManagerIfc.TYPE);
        }
        return parameterManager;
    }
}
