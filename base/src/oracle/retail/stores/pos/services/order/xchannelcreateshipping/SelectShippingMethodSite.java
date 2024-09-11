/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/SelectShippingMethodSite.java /main/5 2013/05/16 14:08:36 mkutiana Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* mkutiana    05/16/13 - retaining the values of the ShippingBeanModel upon
*                        error on the SelectShippingMethodSite
* yiqzhao     09/17/12 - fix the issue with multiple order delivery details for
*                        a given item group.
* sgu         07/03/12 - added xc order ship delivery date, carrier code and
*                        type code
* yiqzhao     06/29/12 - Remove updateLineItems
* yiqzhao     06/28/12 - Update shipping flow
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;



import java.util.List;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;

public class SelectShippingMethodSite extends PosSiteActionAdapter
{
    /**
        class name constant
    **/
    public static final String SITENAME = "SelectShippingMethodSite";
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/5 $";

    /**
     * offline shipping method prompt tag
     */
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT_TAG = "AlternateShippingMethodPromptTag";

    /**
     * offline shipping method prompt
     */
    protected static String ALTERNATE_SHIPPING_METHOD_PROMPT = "Enter the shipping method and shipping charge and press Done.";

    /**
     * Default Shipping Charge Service Item ID
     */
    public static final String DEFAULT_SHIPPING_CHARGE_ITEM_ID = "ShippingChargeItemID";

    //--------------------------------------------------------------------------
    /**
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui 	= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

    	XChannelShippingCargo shipCargo = (XChannelShippingCargo)bus.getCargo();

        ShippingMethodBeanModel model = (ShippingMethodBeanModel)ui.getModel();
        shipCargo.setShippingBeanModel(model);
        ShippingMethodIfc selectedMethodOfShipping = model.getSelectedShipMethod();

        OrderDeliveryDetailIfc deliveryDetail = shipCargo.getDeliveryDetail();
        if ( deliveryDetail == null )
        {
        	deliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();
        }
        else
        {
        	//Customer information has been entered on the delivery detail from ShippingInformationSite.
        	//Or there are more than one deliveries for selected items, create a new delivery detail
        	//and copy customer shipping info to the new one
        	deliveryDetail = (OrderDeliveryDetailIfc)deliveryDetail.clone();
        }
        shipCargo.setDeliveryID(shipCargo.getDeliveryID() + 1);
   	 	deliveryDetail.setDeliveryDetailID(shipCargo.getDeliveryID());

        deliveryDetail.setSpecialInstructions(model.getInstructions());
        deliveryDetail.setShippingCarrier(selectedMethodOfShipping.getShippingCarrierCode());
        deliveryDetail.setShippingType(selectedMethodOfShipping.getShippingTypeCode());
        deliveryDetail.setDeliveryDate(selectedMethodOfShipping.getEstimatedShippingDate());

        List<SaleReturnLineItemIfc> lines = shipCargo.getLineItemsForDelivery();
        for ( SaleReturnLineItemIfc line : lines )
        {
        	line.getOrderItemStatus().setDeliveryDetails(deliveryDetail);
        }
        shipCargo.removeAllLineItemsForDelivery();
        
        shipCargo.setDeliveryDetail(deliveryDetail);

        shipCargo.setCurrentOptionIndex(shipCargo.getCurrentOptionIndex()+1);

        String currentLetterName = bus.getCurrentLetter().getName();
        if (currentLetterName.equals(CommonLetterIfc.DONE) )
        	bus.mail(new Letter(CommonLetterIfc.DONE), BusIfc.CURRENT);
        else
        	bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }
}