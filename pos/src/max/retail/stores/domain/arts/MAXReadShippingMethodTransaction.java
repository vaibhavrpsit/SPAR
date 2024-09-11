/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *
 *  Rev 1.0     1 Dec , 2016	        Ashish Yadav		Changes for SEND FES
 *
 ********************************************************************************/

package max.retail.stores.domain.arts;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.arts.ReadShippingMethodTransaction;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodSearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

public class MAXReadShippingMethodTransaction extends ReadShippingMethodTransaction{
	private static Logger logger = Logger.getLogger(max.retail.stores.domain.arts.MAXReadShippingMethodTransaction.class);
// Changes start for Rev 1.0 (Ashish : Send)
	public ShippingMethodIfc[] readShippingMethod(ShippingMethodSearchCriteriaIfc criteria) throws DataException
    {
		// Changes end for Rev 1.0 (Ashish : Send)
        if (logger.isDebugEnabled()) logger.debug(
                     "ReadShippingMethodTransaction.readShippingMethod");


        // set data actions and execute

        DataAction da = new DataAction();
        da.setDataOperationName("ReadShippingMethod");
     // Changes start for Rev 1.0 (Ashish : Send)
        da.setDataObject(criteria);
     // Changes end for Rev 1.0 (Ashish : Send)

        DataActionIfc[] dataActions = new DataActionIfc[1];
        dataActions[0] = da;

        setDataActions(dataActions);
        ShippingMethodIfc[] retrievedShippingMethods = (ShippingMethodIfc[]) getDataManager().execute(this);

        if (logger.isDebugEnabled()) logger.debug(
                    "ReadShippingMethodTransaction.readShippingMethod");

        return(retrievedShippingMethods);
    }
}
