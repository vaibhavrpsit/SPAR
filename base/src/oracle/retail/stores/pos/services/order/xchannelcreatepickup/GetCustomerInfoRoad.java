/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/GetCustomerInfoRoad.java /main/3 2013/08/27 14:46:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  08/24/13 - Xchannel Inventory lookup enhancement phase I
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

//foundation imports
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CaptureCustomerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

//--------------------------------------------------------------------------
/**
    This road gets the customer data from the model, creates a customer capture
    object to temporarily hold the data and adds the map in the cargo.
    <p>
    @version $Revision: /main/3 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class GetCustomerInfoRoad extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";

    //----------------------------------------------------------------------
    /**
        This method gets the customer data from the model, creates a customer capture
        object to temporarily hold the data and adds the map in the cargo.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DataInputBeanModel model = (DataInputBeanModel)ui.getModel(POSUIManagerIfc.XC_PICKUP_CUSTOMER);

        CaptureCustomerIfc customer = DomainGateway.getFactory().getCaptureCustomerInstance();
        customer.setFirstName((String)model.getValue("firstNameField"));
        customer.setLastName((String)model.getValue("lastNameField"));

        // parse phone number to remove formatting characters
        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.parseString((String)model.getValue("telephoneNumberField"));
        customer.setPhoneNumber(phone.getPhoneNumber());
        for (int i=0; i<cargo.getLineItemsBucket().get(cargo.getLineItemIndex()).getItemBucket().size();i++)
        {
            cargo.getCustomerForPickupByLineNum().put(cargo.getLineItemsBucket().get(cargo.lineItemIndex).getItemBucket().get(i).getLineNumber(), customer);
        }
    }
}
