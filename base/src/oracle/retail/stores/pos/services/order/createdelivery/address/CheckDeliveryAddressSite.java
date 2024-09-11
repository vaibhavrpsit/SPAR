/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/createdelivery/address/CheckDeliveryAddressSite.java /main/17 2012/09/12 11:57:22 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/29/12 - Merge from project Echo (MPOS) into Trunk.
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    jswan     04/13/12 - Moved to the "order" package during the cross
 *                         channel project to provide better organization for
 *                         the order create process.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/30/09 - print special instructions line as two lines and
 *                         spell out whole label. hide label when there are not
 *                         special instructions
 *    cgreene   03/14/09 - use data from model in screen for order details
 *                         instead of customer in transaction
 *    mahising  03/04/09 - Fixed special order issue for business customer
 *    mahising  02/27/09 - clean up code after code review by jack for PDO
 *    mahising  02/26/09 - Rework for PDO functionality
 *    mkochumm  01/23/09 - set country
 *    mahising  01/13/09 - fix QA issue
 *    aphulamb  01/02/09 - fix delivery issues
 *    aphulamb  12/23/08 - Mock padding fix and PDO flow related changes for
 *                         buttons enable/disable
 *    aphulamb  11/22/08 - Checking files after code review by Naga
 *    aphulamb  11/18/08 - Pickup Delivery Order
 *    aphulamb  11/13/08 - Check in all the files for Pickup Delivery Order
 *                         functionality
 *    aphulamb  11/13/08 - Check delivery address
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.createdelivery.address;

import java.util.Iterator;
import java.util.zip.DataFormatException;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.KitComponentLineItemIfc;
import oracle.retail.stores.domain.lineitem.KitHeaderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.createpickup.PickupDeliveryOrderCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;

public class CheckDeliveryAddressSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 858712268570494285L;

    /**
     * invalid postal code resource id
     */
    public static final String INVALID_POSTAL_CODE = "InvalidPostalCode";

    /**
     * invalid country resource id
     */
    public static final String INVALID_COUNTRY = "InvalidCountry";

    /**
     * invalid state resource id
     */
    public static final String INVALID_STATE = "InvalidState";

    /**
     * set to true if all lineitem selected are already delivery status.
     */
    boolean isAllItemAlreadyDelivery = false;

    /**
     * validate the delivery address screen and set the delivery address detail
     * into OrderDeliveryDetail Object
     *
     * @param bus the bus arriving at this site
     */
    @SuppressWarnings({ "unchecked", "deprecation" })
    @Override
    public void arrive(BusIfc bus)
    {
        PickupDeliveryOrderCargo pickupDeliveryOrderCargo = (PickupDeliveryOrderCargo)bus.getCargo();
        OrderDeliveryDetailIfc orderDeliveryDetail = DomainGateway.getFactory().getOrderDeliveryDetailInstance();

        // get the user interface manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel)ui.getModel();
        if (Util.isEmpty(model.getCountryNames()[model.getCountryIndex()]))
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(INVALID_COUNTRY);
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else if (Util.isEmpty(model.getStateNames()[model.getStateIndex()]))
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID(INVALID_STATE);
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
            // set and display the model
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        { // set the delivery address detail into OrderDeliveryDetail Object
            try
            {
                AddressIfc address = DomainGateway.getFactory().getAddressInstance();
                String postalString = address.validatePostalCode(model.getPostalCode(), model.getCountry());
                model.setPostalCode(postalString);

                if (model.isBusinessCustomer())
                {
                    orderDeliveryDetail.setBusinessName(model.getOrgName());
                }
                else
                {
                    orderDeliveryDetail.setFirstName(model.getFirstName());
                    orderDeliveryDetail.setLastName(model.getLastName());

                }
                orderDeliveryDetail.getContactPhone().setPhoneType(model.getTelephoneIntType());
                orderDeliveryDetail.getDeliveryAddress().setCity(model.getCity());
                orderDeliveryDetail.getDeliveryAddress().setCountry(model.getCountry());
                orderDeliveryDetail.getDeliveryAddress().setState(model.getState());
                orderDeliveryDetail.getDeliveryAddress().setPostalCode(model.getPostalCode());
                orderDeliveryDetail.getContactPhone().setPhoneNumber(
                        model.getTelephoneNumber(model.getTelephoneIntType()));
                orderDeliveryDetail.getContactPhone().setCountry(model.getCountry());
                orderDeliveryDetail.setSpecialInstructions(model.getInstructions());
                address.addAddressLine(model.getAddressLine1());
                address.addAddressLine(model.getAddressLine2());
                orderDeliveryDetail.getDeliveryAddress().setLines(address.getLines());
                SaleReturnLineItemIfc[] lineItems = pickupDeliveryOrderCargo.getLineItems();
                KitHeaderLineItemIfc parentKitItem = null;
                KitComponentLineItemIfc childKit = null;
                for (int i = 0; i < lineItems.length; i++)
                {
                    lineItems[i].getOrderItemStatus().setDeliveryDetails(orderDeliveryDetail);
                    if (lineItems[i].isKitHeader())
                    {
                        parentKitItem = (KitHeaderLineItemIfc)lineItems[i];
                        Iterator<KitComponentLineItemIfc> childKitItemIter = parentKitItem.getKitComponentLineItems();
                        while (childKitItemIter.hasNext())
                        {
                            childKit = childKitItemIter.next();
                            childKit.getOrderItemStatus().setDeliveryDetails(orderDeliveryDetail);
                        }
                    }
                }
            }
            catch (DataFormatException e)
            {
                // Using "generic dialog bean".
                DialogBeanModel dialogModel = new DialogBeanModel();
                dialogModel.setResourceID(INVALID_POSTAL_CODE);
                dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
                dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
                // set and display the model
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
            }
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
    }
}
