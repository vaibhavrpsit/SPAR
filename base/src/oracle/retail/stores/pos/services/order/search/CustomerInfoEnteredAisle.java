/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/CustomerInfoEnteredAisle.java /main/2 2013/05/29 18:20:51 abondala Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abondala    05/28/13 - add business name field to the order search screen
* yiqzhao     07/27/12 - modify order search flow and set customer info in
*                        cargo for order search.
* yiqzhao     07/27/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.search;

import java.util.ArrayList;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.OrderCustomerInfoBeanModel;

public class CustomerInfoEnteredAisle extends PosLaneActionAdapter 
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
       class name constant
    **/
    public static final String LANENAME = "CustomerInfoEnteredAisle";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/2 $";

    //----------------------------------------------------------------------
    /**
        This class gets the store criteria from the screen model, puts it in a
        StoreIfc model and uses it to retrieve the list of associated stores.
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
    	String phoneNumber = "";
        String firstName = "";
        String lastName = "";
        String businessName = "";
        
        POSUIManagerIfc ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        OrderCustomerInfoBeanModel model  = (OrderCustomerInfoBeanModel)ui.getModel();
        
        if ( model.getTelephoneNumber() != null )
        {
        	phoneNumber = model.getTelephoneNumber();
        }
        if ( model.getFirstName() != null )
        {
        	firstName = model.getFirstName();
        }
        if (model.getLastName() != null)
        {
        	lastName = model.getLastName();
        }
        if (model.getBusinessName() != null)
        {
            businessName = model.getBusinessName();
        }

        UtilityManagerIfc util = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        if (firstName.isEmpty() && lastName.isEmpty() &&  businessName.isEmpty() && phoneNumber.isEmpty())
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("InvalidCustomerInfo");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.ERROR);
            // Display the dialog.
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            OrderCargo cargo = (OrderCargo) bus.getCargo();

            CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
            customer.setFirstName(firstName);
            customer.setLastName(lastName);
            customer.setCompanyName(businessName);
            
            PhoneIfc phone =DomainGateway.getFactory().getPhoneInstance();
            phone.setPhoneNumber(model.getTelephoneNumber());
            ArrayList<PhoneIfc> phoneList = new ArrayList<PhoneIfc>();
            phoneList.add(phone);
            customer.setPhoneList(phoneList);
            cargo.setSelectedCustomer(customer);
            cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_CUSTOMER);
        	bus.mail(new Letter(CommonLetterIfc.OK), BusIfc.CURRENT);
        }
    }
}
