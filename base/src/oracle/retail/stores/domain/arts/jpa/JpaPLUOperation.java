/* ===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaPLUOperation.java /main/10 2014/07/07 10:43:49 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   07/01/14 - refactor throwing and catching exceptions so that
 *                         SQLExceptions get mapped
 *    jswan     12/13/13 - Upated JAVADOC.
 *    yiqzhao   09/05/13 - Get correct kit item price when a quantity of one
 *                         kit component is greater than one.
 *    abondala  01/03/13 - refactored transformers
 *    hyin      10/16/12 - offline work for PLU lookup and Advanced item
 *                         lookup.
 *    jswan     10/01/12 - Modified to support more organized execption
 *                         handling.
 *    jswan     09/17/12 - Modified to use PLUItemAggregation and to process
 *                         Kit components more correctly.
 *    jswan     08/29/12 - Modified to support SearchCriteria.
 *    jswan     07/20/12 - Modified to support JPA Entity to Domin
 *                         transformations.
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.transform.entity.Item.PLUItemAggregationTransformerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.entities.item.PLUItemAggregationIfc;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.item.ItemServiceIfc;
import oracle.retail.stores.transform.TransformerIfc;

/**
 * This class retrieves PLU Item information from the database and provides
 * this data to the caller in the form of PLUItemIfc objects.
 * @since 14.0
 */
public class JpaPLUOperation extends JpaDataOperation
{
    /* serialVersionUID */
    private static final long serialVersionUID = -2016699957522696146L;
    
    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(JpaPLUOperation.class);

    private boolean offlineDBUsed = false;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        // Get the search criteria and the item service
        SearchCriteriaIfc criteria = (SearchCriteriaIfc)dataObject;
        ItemServiceIfc itemService = (ItemServiceIfc)service;
        
        // Retrieve the PLU item from the item service.  This try block should
        // only contain the this one method call; the exception strategy depends
        // on it.
        PLUItemAggregationIfc pluItemAggregation = null;
        try
        {
            pluItemAggregation = itemService.getPOSItem(criteria);
        }
        catch (ServiceException exception)
        {
            if (exception.getCause() instanceof DataException)
            {
                throw (DataException)exception.getCause();
            }
            mapAndThrowDataException(exception);
        }

        if (pluItemAggregation == null)
        {
            if (isOfflineDBUsed())
            {
                throw new DataException(DataException.NO_DATA, DataException.ERROR_CODE_EXTENDED_OFFLINE, "PLUItem lookup");
            }
            throw new DataException(DataException.NO_DATA, "PLUItem lookup");
        }
        
        // It is possible for the transformer code to throw runtime exceptions.
        // However, JPACommand class which calls this method, catches all classes
        // that extend Throwable; the Throwable is not a DataException, it logs
        // the error and wraps the Throwable in a DataException. 
        PLUItemIfc pluItem = transformPLUItemAggregation(pluItemAggregation, criteria, null);
        
        if (pluItemAggregation.getKitComponents() != null && pluItemAggregation.getKitComponents().size() > 0)
        {
            List<PLUItemAggregationIfc> components = pluItemAggregation.getKitComponents();
            KitComponentIfc[] kitComponents = new KitComponentIfc[components.size()];
            int i = 0;
            for (PLUItemAggregationIfc component: components)
            {
                PLUItemIfc kitComponent = transformPLUItemAggregation(component, criteria, pluItemAggregation);
                kitComponents[i++] = (KitComponentIfc)kitComponent;
            }
            
            ((ItemKitIfc)pluItem).addComponentItems(kitComponents);
        }

        // Add the PLUItem to the result
        PLUItemIfc[] pluItems = new PLUItemIfc[1];
        pluItems[0] = pluItem;
        return pluItems;
    }
    
    /**
     * This method uses the Spring loaded {@link PLUItemAggregationTransformerIfc} item aggregation transformer to convert 
     * the {@link PLUItemAggregationIfc) object into a oracle.retail.stores.domain.stock.PLUItemIfc domain object.  The 
     * implementation for the PLUItemAggregationTransformerIfc is defined in the TransformerContext.xml file; bean ID 
     * is 'transformer_PLUItemAggregationTransformer'.
     * @param pluItemAggregation the aggregation that hold the item and pricing data.
     * @param criteria the search criteria used to query the pluItemAggregation
     * @param kitHeader if the pluItemAggregation represents a kit component, this is the associated kit header
     * @return a PLUItemIfc domain object
     */
    protected PLUItemIfc transformPLUItemAggregation(PLUItemAggregationIfc pluItemAggregation, 
            SearchCriteriaIfc criteria, PLUItemAggregationIfc kitHeader)
    {
        PLUItemAggregationTransformerIfc  transformer = 
                (PLUItemAggregationTransformerIfc)BeanLocator.getTransformerBean(TransformerIfc.TRANSF_PLU_ITEM_AGGREGATION_TRANSFORMER);
        
        return transformer.transform(pluItemAggregation, criteria, kitHeader);
    }

    public boolean isOfflineDBUsed() {
        return offlineDBUsed;
    }

    public void setOfflineDBUsed(boolean offlineDBUsed) {
        this.offlineDBUsed = offlineDBUsed;
    }
    
    
}
