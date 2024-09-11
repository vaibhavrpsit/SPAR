/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/jpa/JpaReadMetaTagItemInfo.java /main/8 2014/07/07 10:43:49 cgreene Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* cgreene     07/01/14 - refactor throwing and catching exceptions so that
*                        SQLExceptions get mapped
* jswan       12/13/13 - Upated JAVADOC.
* hyin        03/06/13 - fix metatag search offline message.
* abondala    01/11/13 - support extending EJB3
* abondala    01/10/13 - support extending jpa
* hyin        10/12/12 - use single ItemSearchCriteria for metaTag service.
* hyin        10/02/12 - disable webservice call when it's offline.
* hyin        09/06/12 - renaming JpaReadItemInfo
* hyin        09/06/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.domain.arts.jpa;

import java.io.Serializable;

import oracle.retail.stores.common.item.AdvItemSearchResults;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.JpaDataOperation;
import oracle.retail.stores.storeservices.services.common.ServiceException;
import oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc;
import oracle.retail.stores.storeservices.services.item.ItemServiceIfc;

import org.apache.log4j.Logger;

public class JpaReadMetaTagItemInfo extends JpaDataOperation
{
    private static final long serialVersionUID = 7095875176044327984L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(JpaReadMetaTagItemInfo.class);

    private boolean offlineDBUsed = false;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.manager.data.JpaDataOperation#execute(oracle.retail.stores.storeservices.services.common.StoresDataServiceIfc, java.io.Serializable)
     */
    @Override
    protected Serializable execute(StoresDataServiceIfc service, Serializable dataObject) throws ServiceException, DataException
    {
        SearchCriteriaIfc cri = (SearchCriteriaIfc)dataObject;
        ItemServiceIfc itemService = (ItemServiceIfc)service;
    
        AdvItemSearchResults results = itemService.getItemsByMetaTagText(cri); 
        
        // If no results have been found throw a NO_DATA DataException
        if (results == null || results.getReturnItems().size() == 0)
        {
            DataException de = new DataException(DataException.NO_DATA, "Read MetaTag Item Info");
            if (isOfflineDBUsed())
            {
                de = new DataException(DataException.NO_DATA, DataException.ERROR_CODE_EXTENDED_OFFLINE, "Read MetaTag Item Info");
            }
                
            throw de;
        }
        
        return results;
    }

    public boolean isOfflineDBUsed()
    {
        return offlineDBUsed;
    }

    public void setOfflineDBUsed(boolean offlineDBUsed)
    {
        this.offlineDBUsed = offlineDBUsed;
    }

}
