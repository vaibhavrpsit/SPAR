/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaReadItemInfo.java /main/5 2014/07/07 10:43:49 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/01/14 - refactor throwing and catching exceptions so that
 *                         SQLExceptions get mapped
 *    jswan     12/13/13 - Upated JAVADOC.
 *    hyin      10/16/12 - offline work for PLU lookup and Advanced item
 *                         lookup.
 *    jswan     09/24/12 - Added to support request of Advanced Item Search
 *                         through JPA.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.item.ItemServiceIfc;

/**
 * This class retrieves PLU Item information from the database and provides
 * this data to the caller in the form of PLUItemIfc objects.
 * @since 14.0
 */
public class JpaReadItemInfo extends JpaDataOperation
{
    /* serialVersionUID */
    private static final long serialVersionUID = -2016699957522696146L;
    
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(JpaReadItemInfo.class);

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        //Get the service and criteria
        SearchCriteriaIfc criteria = (SearchCriteriaIfc)dataObject;
        ItemServiceIfc itemService = (ItemServiceIfc)service;
        
        // Call the service
        AdvItemSearchResults results = null;
        try
        {
            results = itemService.getItemsForAdvancedSearch(criteria);
        }
        catch (ServiceException exception)
        {
            if (exception.getCause() instanceof DataException)
            {
                throw (DataException)exception.getCause();
            }
            mapAndThrowDataException(exception);
        }
        
        // If no results have been found throw a NO_DATA DataException
        if (results == null || results.getReturnItems().size() == 0)
        {
            DataException de = new DataException(DataException.NO_DATA, "Read Item Info");
            if ((results != null) && (results.isUsingDerby()))
            {
                de = new DataException(DataException.NO_DATA, DataException.ERROR_CODE_EXTENDED_OFFLINE, "Read Item Info");
            }
                
            throw de;
        }
        
        // add the result to the transaction object
        return results;
    }
}
