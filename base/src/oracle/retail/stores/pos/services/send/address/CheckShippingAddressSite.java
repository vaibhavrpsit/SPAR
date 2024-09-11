/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/address/CheckShippingAddressSite.java /main/13 2012/10/22 15:36:21 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   10/18/12 - Set postal code to ShipToCustomer object.
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/16/2006 6:05:20 AM   Akhilashwar K. Gupta
 *         CR-3995: Updated "copyModelDataToCustomer()" method to set the
 *         company name in customer info.
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/03 21:55:48  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.4  2004/06/25 22:51:06  rsachdeva
 *   @scr 5859 This has been re-fixed as the code from Deprecated ShippingAddressEntered was moved to a Wrong Location in the new site created
 *
 *   Revision 1.3  2004/06/21 13:16:07  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.2  2004/06/16 21:44:15  lzhao
 *   @scr 4670: add dialog, update phone, state, country
 *
 *   Revision 1.1  2004/06/16 13:42:07  lzhao
 *   @scr 4670: refactoring Send for 7.0.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.address;


import java.util.zip.DataFormatException;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;

//------------------------------------------------------------------------------
/**
    Check country, state and post code in shipping address is valid or not.
    <P>
    $Revision: /main/13 $
**/
//------------------------------------------------------------------------------
@SuppressWarnings("serial")
public class CheckShippingAddressSite extends PosSiteActionAdapter
{
    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    /**
     invalid postal code resource id
     **/
    public static final String INVALID_POSTAL_CODE = "InvalidPostalCode";
    /**
     invalid country resource id
     **/
    public static final String INVALID_COUNTRY = "InvalidCountry";
    /**
     invalid state resource id
     **/
    public static final String INVALID_STATE = "InvalidState";
    //--------------------------------------------------------------------------
    /**
        Displays the layaway customer screen.
        <P>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        SendCargo cargo = (SendCargo) bus.getCargo();

        // get the user interface manager
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ShippingMethodBeanModel model =
            (ShippingMethodBeanModel) ui.getModel(POSUIManagerIfc.SHIPPING_ADDRESS);
        
        if ( Util.isEmpty(model.getCountryNames()[model.getCountryIndex()]) )
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(INVALID_COUNTRY);
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(
                    DialogScreensIfc.BUTTON_OK,
                    CommonLetterIfc.RETRY);
            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else if ( Util.isEmpty( model.getStateNames()[model.getStateIndex()]) )
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(INVALID_STATE);
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(
                    DialogScreensIfc.BUTTON_OK,
                    CommonLetterIfc.RETRY);
            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {    
            try
            {
                AddressIfc address =
                        DomainGateway.getFactory().getAddressInstance();
                String postalString =
                        address.validatePostalCode(
                                model.getPostalCode(),
                                model.getCountry());
                model.setPostalCode(postalString);         
            
                // Get the customerdata from the UI and
                // move data from model to the customer object
                //This is for Shipping to Info
                //The Send flow goes back to SellItem Screen
                CustomerIfc shipToCustomer = DomainGateway.getFactory().getCustomerInstance();
                cargo.setShipToInfo(copyModelDataToCustomer(shipToCustomer, model));
                    
                bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
            }
            catch (DataFormatException e)
            {
                // Using "generic dialog bean".
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID(INVALID_POSTAL_CODE);
                dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                dialogModel.setButtonLetter(
                        DialogScreensIfc.BUTTON_OK,
                        CommonLetterIfc.RETRY);
                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Gets the data from the ShippingMethodBeanModel Updates the customer
     * information with the model data 
     * @param customer ship to customer reference   
     * @param model  Shipping Bean Model
     * @return CustomerIfc reference for customer
     */
    //--------------------------------------------------------------------------
    public CustomerIfc copyModelDataToCustomer(CustomerIfc customer,
                                               ShippingMethodBeanModel model)
    {
        customer.setFirstName(model.getFirstName());
        customer.setLastName(model.getLastName());
        customer.setCompanyName(model.getOrgName());
        customer.setBusinessCustomer(false);
        customer = CustomerUtilities.updateAddressAndPhone(customer, model);
        return (customer);
    }
    
}
