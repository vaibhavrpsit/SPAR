/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/SelectStoreForShipSite.java /main/4 2014/06/10 12:04:11 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/09/14 - CAE add available date during order create enhancement
*                        phase II
* yiqzhao     07/02/12 - Read text from orderText bundle file and define screen
*                        names
* yiqzhao     06/29/12 - Use setStoreToShip in shipping cargo
* yiqzhao     06/22/12 - set setStoreToShip to the cargo
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StoreLineItemModel;

/**
 * This site calls the UI manager to list all the available store to ship the
 * order to.
 * 
 * @version $Revision: /main/4 $
 **/
@SuppressWarnings("serial")
public class SelectStoreForShipSite extends PosSiteActionAdapter
{
    // ----------------------------------------------------------------------
    /**
     * This method calls the UI manager to display list of ship to available stores  
     * for shipping.
     * 
     * @param bus Service Bus
     **/
    public void arrive(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        ListBeanModel model = getListModel(bus);

        // Set the item number and description on the prompt and response model
        PromptAndResponseModel responseModel = new PromptAndResponseModel();
        responseModel.setArguments(getArguments((SaleReturnLineItemIfc[])cargo.getTransaction().getLineItems()));
        // Set the prompt and response model list bean model
        model.setPromptAndResponseModel(responseModel);

        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.XC_SHIP_TO_STORE_SELECT, model);
    }

    public void depart(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        ListBeanModel model = (ListBeanModel)ui.getModel("XC_SHIP_TO_STORE_SELECT");
        StoreLineItemModel storeLineItemModel = (StoreLineItemModel)model.getSelectedValue();

        cargo.setStoreToShip(storeLineItemModel.getStore());
        // Creating order delivery details and setting to cargo
        // -------------------------------------------------------------------------------------
        OrderDeliveryDetailIfc deliveryDetail = cargo.getDeliveryDetail();
        if (deliveryDetail == null)
        {
            deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();
        }
        deliveryDetail.setDeliveryAddress(storeLineItemModel.getStore().getAddress());
        cargo.setDeliveryDetail(deliveryDetail);

        // -------------------------------------------------------------------------------------
        cargo.setShipToCustomer(false);
    }

    /**
     * Create the ListBeanModel from the list of store and available item
     * inventory
     * 
     * @param bus
     * @return
     */
    protected ListBeanModel getListModel(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo)bus.getCargo();

        StoreIfc[] storeList = cargo.getStoreGroup();
        // List<AvailableToPromiseInventoryIfc> iaList =
        // cargo.getItemAvailablityList();
        ListBeanModel model = new ListBeanModel();

        Vector<StoreLineItemModel> modelList = new Vector<StoreLineItemModel>();
        for (StoreIfc store : storeList)
        {
            StoreLineItemModel storeLineItemModel = getStoreLineItemModel(store);
            modelList.addElement(storeLineItemModel);
        }

        // Add the list of store available inventory to the ListBeanModel
        model.setListModel(modelList);

        return model;
    }

    /**
     * Convert the store and itemAvailablity objects into a
     * StoreInventoryLineItemModel
     * 
     * @param store
     * @param itemAvailablity
     * @param bus
     * @return StoreInventoryLineItemModel
     */
    private StoreLineItemModel getStoreLineItemModel(StoreIfc store)
    {
        StoreLineItemModel storeLineItemModel = new StoreLineItemModel();

        storeLineItemModel.setStoreID(store.getStoreID());
        storeLineItemModel.setStoreName(store.getLocationName(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
        storeLineItemModel.setAddress(store.getAddress());
        storeLineItemModel.setStorePhone(store.getPrimaryPhone());
        storeLineItemModel.setStore(store);

        return storeLineItemModel;
    }

    /**
     * get the argument for prompt and response
     */
    protected String[] getArguments(SaleReturnLineItemIfc[] lineItems)
    {
        String values[] = new String[2];
        SaleReturnLineItemIfc firstItem = null;
        boolean onlyOneItemSelected = true;
        for (SaleReturnLineItemIfc lineItem : lineItems)
        {
            if (lineItem.isSelectedForItemModification())
            {
                if (firstItem == null)
                {
                    firstItem = lineItem;
                }
                else
                {
                    onlyOneItemSelected = false;
                    break;
                }
            }
        }
        if (firstItem != null)
        {
            values[0] = firstItem.getItemID();
            if (onlyOneItemSelected)
            {
                values[1] = firstItem.getPLUItem().getShortDescription(
                        LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            }
            else
            {
                values[1] = firstItem.getPLUItem().getShortDescription(
                        LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE))
                        + ",...";
            }
        }
        return values;
    }

}
