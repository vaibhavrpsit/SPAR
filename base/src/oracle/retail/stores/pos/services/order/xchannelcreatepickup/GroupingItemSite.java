/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/GroupingItemSite.java /main/2 2014/04/22 16:23:15 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    04/22/14 - Upgrade Common-collections to 4.0
* abhinavs    08/24/13 - Xchannel Inventory lookup enhancement phase I
* abhinavs    08/24/13 - Initial Version
* abhinavs    08/24/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import java.util.HashSet;
import java.util.Set;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import org.apache.commons.collections4.map.MultiValueMap;

//--------------------------------------------------------------------------
/**
* This class creates the data structures for xchannel Inventory lookup WS
* @version $Revision: /main/2 $ 
*/
//--------------------------------------------------------------------------
public class GroupingItemSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3909763793730031948L;
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/2 $";

    //----------------------------------------------------------------------
    /**
       This method updates the cargo with multivalue itemMap.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void arrive(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        
        MultiValueMap lineItemMap = new MultiValueMap();
        Set itemIdList = new HashSet<String>();
        
        lineItemMap.clear();
        
        synchronized (lineItemMap)
        {
            for(SaleReturnLineItemIfc lineItem : cargo.getLineItems())
            {
                 for(int i=0;i<lineItem.getItemQuantity().intValue();i++)
                 {
                    lineItemMap.put(lineItem.getItemID(), lineItem);
                    itemIdList.add(lineItem.getItemID());
                 }
            }  
        }
        
        cargo.setLineItemMap(lineItemMap);
        cargo.setItemIdListIterator(itemIdList.iterator());
        bus.mail(CommonLetterIfc.CONTINUE);
    }


}
