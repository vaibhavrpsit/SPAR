/*===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/ConfirmGroupingSite.java /main/1 2013/08/27 14:46:50 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    08/24/13 - Xchannel Inventory lookup enhancement phase I
* abhinavs    08/24/13 - Initial Version
* abhinavs    08/24/13 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.xchannelcreatepickup.InventoryLookupStorage;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
* This class displays the screen for user input before performing xchannel inventory lookup
* @version $Revision: /main/1 $ 
*/
//--------------------------------------------------------------------------
public class ConfirmGroupingSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4684940484168895694L;
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    //----------------------------------------------------------------------
    /**
       This method receives the user input for bundling or unbundling SRLI
       for making WS invocation
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void arrive(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        Iterator<String> itemIdListIterator = cargo.getItemIdListIterator();

        if ( itemIdListIterator.hasNext() )
        {
            String itemId =  itemIdListIterator.next();

            cargo.setCurrentItemID(itemId);
            List<SaleReturnLineItemIfc> lineItems = getLineItems(itemId, cargo);
            if ( lineItems.size() == 1 )
            {
                List<SaleReturnLineItemIfc> listMap=(ArrayList)lineItems;
                List<InventoryLookupStorage> totalLineItemLists=new ArrayList<InventoryLookupStorage>();
                InventoryLookupStorage lineitemStorage=new InventoryLookupStorage();
                lineitemStorage.setItemBucket(listMap);
                lineitemStorage.setItemId(itemId);
                totalLineItemLists.add(lineitemStorage);
                if(cargo.getLineItemsBucket()!=null)
                {
                    cargo.getLineItemsBucket().add(lineitemStorage);
                }
                else
                {
                    cargo.setLineItemsBucket(totalLineItemLists);
                }
                bus.mail(CommonLetterIfc.CONTINUE);
            }
            else if (lineItems.size() > 1 )
            {

                POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

                String[] args = new String[2];
                args[0] = itemId;
                args[1] = ((SaleReturnLineItemIfc)lineItems.toArray()[0]).getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID("ConfirmItemGrouping");
                dialogModel.setType(DialogScreensIfc.CONFIRMATION);
                dialogModel.setArgs(args);

                // display dialog
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);               
            }

        }

        else
        {
            //Finish the iteration
            bus.mail(CommonLetterIfc.NEXT);
        }

    }
    
    /**
     * Get all the line items for the particular item
     * @param itemId
     * @param cargo
     * @return
     */
    protected List<SaleReturnLineItemIfc> getLineItems(String itemId, XChannelCreatePickupOrderCargo cargo)
    {
        List<SaleReturnLineItemIfc> lineItems = new ArrayList<SaleReturnLineItemIfc>();
        
        for (int i=0; i<cargo.getLineItems().length; i++)            
        {
            if (cargo.getLineItems()[i].getItemID().equals(itemId))
            {
                lineItems.add(cargo.getLineItems()[i]);
            }
        }
        return lineItems;
    }
}
