/*===========================================================================
* Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/GroupingInventorySite.java /main/2 2014/04/22 16:23:15 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    04/22/14 - Upgrade Common-collections to 4.0
* abhinavs    08/26/13 - Xchannel Inventory lookup enhancement phase I
* abhinavs    08/26/13 - Initial Version
* abhinavs    08/26/13 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.map.MultiValueMap;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.xchannelcreatepickup.InventoryLookupStorage;

//--------------------------------------------------------------------------
/**
* This class contains the data structures for SRLI when bundling of Inventory lookup WS is opted
* @version $Revision: /main/2 $ 
*/
//--------------------------------------------------------------------------
public class GroupingInventorySite extends PosSiteActionAdapter
{

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -3110600338029193224L;
  //--------------------------------------------------------------------------
    /**
        This class loads the data structures for items which are
        bundled together
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void arrive(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        //Get the key i.e. the itemId set in the cargo.
        String key=cargo.getCurrentItemID();
        MultiValueMap lineItemMap = cargo.getLineItemMap();
        List<SaleReturnLineItemIfc> listMap=new ArrayList<>();
        listMap=(ArrayList)lineItemMap.get(key);
        InventoryLookupStorage lineitemStorage=new InventoryLookupStorage();
        lineitemStorage.setItemBucket(listMap);
        lineitemStorage.setItemId(listMap.get(0).getItemID());
        
        if(cargo.getLineItemsBucket()!= null)
        {
            cargo.getLineItemsBucket().add(lineitemStorage);
        }
        else
        {
            List<InventoryLookupStorage> totalLineItemLists=new ArrayList<InventoryLookupStorage>();
            totalLineItemLists.add(lineitemStorage);
            cargo.setLineItemsBucket(totalLineItemLists);
        }

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
    }
}
