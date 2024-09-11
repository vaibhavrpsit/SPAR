/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/SerialValidationResponse.java /main/4 2013/03/12 15:15:34 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  03/12/13 - UIN lookup code cleanup
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Response class of Serial Validation tour
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Serial Validation Response Object will hold the Response information
 * @author nkgautam
 */
public class SerialValidationResponse implements SerialValidationResponseIfc, Serializable
{

    private static final long serialVersionUID = -7516866703979218558L;
    
    /**
     * Hashmap to contain the item number and status
     */
    protected HashMap<String, String> responseMap;

    /**
     * Gets the Hashmap
     * @return
     */
    public HashMap<String, String> getResponseMap()
    {
        return responseMap;
    }

    /**
     * Sets the Response Hashmap.
     * @param responseMap
     */
    public void setResponseMap(HashMap<String, String> responseMap)
    {
        this.responseMap = responseMap;
    }

    /**
     * @return List of items in response
     */
    public ArrayList<String> getCompleteItemListFromResponse()
    {
        String itemId = null;
        ArrayList<String> itemList = new ArrayList<String>();
        Iterator<String> it = responseMap.keySet().iterator();
        while(it.hasNext())
        {
            itemId = (String)it.next();
            itemList.add(itemId);
        }
        return itemList;
    }

    /**
     *
     * @return list of sellable items in response
     */
    public ArrayList<String> getSellableItemListFromResponse()
    {
        String itemId = null;
        String itemStatus = null;
        ArrayList<String> sellableList = new ArrayList<String>();
        Iterator<String> it = responseMap.keySet().iterator();
        while(it.hasNext())
        {
            itemId = (String)it.next();
            itemStatus = responseMap.get(itemId);
            if(itemStatus.equalsIgnoreCase("Sellable"))
            {
                sellableList.add(itemId);
            }
        }
        return sellableList;
    }


}
