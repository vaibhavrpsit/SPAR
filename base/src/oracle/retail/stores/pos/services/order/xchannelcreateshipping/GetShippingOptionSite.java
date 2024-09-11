/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/GetShippingOptionSite.java /main/10 2014/06/10 12:04:10 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/09/14 - CAE add available date during order create enhancement
*                        phase II
* yiqzhao     05/21/14 - Update payload by adding shipType, shipLocation and
*                        estimated ship date.
* yiqzhao     09/17/12 - fix the issue with multiple order delivery details for
*                        a given item group.
* yiqzhao     08/31/12 - add kit components and remove kit header from
*                        lineItems in cargo.
* yiqzhao     07/16/12 - organize shipping response, handle message exceptions
* yiqzhao     07/06/12 - merge with the previous transactions.
* yiqzhao     07/06/12 - add ShippingOptionNotFound error dialog
* yiqzhao     07/05/12 - Add ship item list on DisplayShippingMethod screen.
* yiqzhao     07/03/12 - refine shipping flow
* yiqzhao     06/29/12 - Use lineItem.isSelectedForItemModification to replace
*                        cargo.getLineItems
* yiqzhao     06/28/12 - Update shipping flow
* yiqzhao     06/18/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.ArrayList;
import java.util.Arrays;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.LocaleRequestor;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.manager.shipping.OrderShippingManagerIfc;
import oracle.retail.stores.domain.shipping.ShippingItemIfc;
import oracle.retail.stores.domain.shipping.ShippingRequest;
import oracle.retail.stores.domain.shipping.ShippingRequestIfc;
import oracle.retail.stores.domain.shipping.ShippingResponseIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site makes Shipping WS call to get the shipping option response. Based
 * on ship to customer or store response vary and so do the UI
 * 
 * @version $Revision: /main/10 $
 **/
@SuppressWarnings("serial")
public class GetShippingOptionSite extends PosSiteActionAdapter
{
    /**
     * class name constant
     **/
    public static final String SITENAME = "GetShippingOptionSite";

    /**
     * revision number for this class
     **/
    public static final String revisionNumber = "$Revision: /main/10 $";

    // --------------------------------------------------------------------------
    /**
     * <p>
     * 
     * @param bus the bus arriving at this site
     **/
    // --------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        XChannelShippingCargo shipCargo = (XChannelShippingCargo)bus.getCargo();

        ShippingRequest request = new ShippingRequest();
        if (shipCargo.isShipToCustomer())
        {
            request.setShipType(ShippingRequestIfc.TYPE_SHIPPING_CUSTOMER);
            request.setStoreID("");
            request.setDestination(shipCargo.getDeliveryDetail().getDeliveryAddress());
        }
        else
        {
            request.setShipType(ShippingRequestIfc.TYPE_SHIPPING_STORE);
            request.setStoreID(shipCargo.getStoreToShip().getStoreID());
            request.setDestination(shipCargo.getStoreToShip().getAddress());
        }

        request.setShippingItems(getShippingItems(shipCargo.getTransaction()));
        request.setShippingLocaleRequetor(getLocalRequestor(utility));

        shipCargo.setRequest(request);

        ShippingResponseIfc response = shipCargo.getResponse();
        if (response == null)
        {
            OrderShippingManagerIfc manager = (OrderShippingManagerIfc)bus.getManager(OrderShippingManagerIfc.TYPE);
            try
            {
                // the manager will access the technician and calling shipping
                // web server to get shipping options
                response = manager.getShippingMethods(request);
                shipCargo.setResponse(response);
                if (shipCargo.isShipToCustomer())
                {
                    bus.mail(CommonLetterIfc.CUSTOMER, BusIfc.CURRENT);
                }
                else
                {
                    bus.mail(CommonLetterIfc.SHIP_TO_STORE, BusIfc.CURRENT);
                }
            }
            catch (DataException e)
            {

                String message = utility.retrieveText("Order", BundleConstantsIfc.ORDER_BUNDLE_NAME, e.getMessage(),
                        "Return shipping option syntax error");
                if (!shipCargo.isShipToCustomer())
                    shipCargo.setDeliveryDetail(null);
                errorDialog(bus, message);
            }
        }
    }

    /**
     * @param lineItems
     * @return
     */
    protected ArrayList<ShippingItemIfc> getShippingItems(SaleReturnTransactionIfc transaction)
    {
        SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])transaction.getLineItems();
        ArrayList<ShippingItemIfc> shippingItems = new ArrayList<ShippingItemIfc>();
        int shippingItemCount = 0;
        for (SaleReturnLineItemIfc lineItem : lineItems)
        {
            if (lineItem.isSelectedForItemModification())
            {
                if (lineItem.isKitHeader())
                {
                    KitHeaderLineItemIfc kitLineItem = (KitHeaderLineItemIfc)lineItem;
                    for (int j = 0; j < kitLineItem.getKitComponentLineItemArray().length; j++)
                    {
                        lineItem = kitLineItem.getKitComponentLineItemArray()[j];
                        ShippingItemIfc shippingItem = getShippingItem(lineItem, ++shippingItemCount);
                        shippingItems.add(shippingItem);
                    }
                }
                else
                {
                    ShippingItemIfc shippingItem = getShippingItem(lineItem, ++shippingItemCount);
                    shippingItems.add(shippingItem);
                }
            }
        }
        return shippingItems;
    }

    /**
     * Create a shipping item
     * 
     * @param lineItem
     * @param seqNo
     * @return
     */
    protected ShippingItemIfc getShippingItem(SaleReturnLineItemIfc lineItem, int seqNo)
    {
        ShippingItemIfc shippingItem = DomainGateway.getFactory().getShippingItemInstance();
        shippingItem.setSaleReturnLineItem(lineItem);
        shippingItem.setItemSeqNo(seqNo);
        shippingItem.setItemID(lineItem.getItemID());
        shippingItem.setQuantity(lineItem.getItemQuantityDecimal());
        shippingItem.setItemSizeCode(lineItem.getItemSizeCode());
        shippingItem.setItemTotalPrice(lineItem.getExtendedSellingPrice());
        shippingItem.setItemTotalExtendedDiscountedPrice(lineItem.getExtendedDiscountedSellingPrice());
        shippingItem.setDescriptions(lineItem.getLocalizedItemDescriptions());
        return shippingItem;
    }

    protected LocaleRequestor getLocalRequestor(UtilityManagerIfc utility)
    {
        LocaleRequestor localeReq = utility.getRequestLocales();
        localeReq.setSortByLocale(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        return localeReq;
    }

    /**
     * Display error dialog
     * 
     * @param bus Service bus.
     */
    protected void errorDialog(BusIfc bus, String errorMessage)
    {
        String[] args = new String[1];
        Arrays.fill(args, "");
        args[0] = errorMessage;

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("ShippingOptionInCorrect");
        model.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        model.setArgs(args);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}
