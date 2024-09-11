/*===========================================================================
* Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/DisplayStoreShippingOptionsSite.java /main/1 2014/06/10 12:04:11 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* abhinavs    06/09/14 - CAE add available date during order create enhancement
*                        phase II
* abhinavs    06/09/14 - Initial Version
* abhinavs    06/09/14 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.ArrayList;
import java.util.Vector;

import oracle.retail.stores.domain.shipping.ShippingItemIfc;
import oracle.retail.stores.domain.shipping.ShippingOptionIfc;
import oracle.retail.stores.domain.shipping.ShippingRequestIfc;
import oracle.retail.stores.domain.shipping.ShippingResponseIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.XChannelShippingMethodBeanModel;

/**
 * This site is traveled to display store shipping options.
 * @since 14.1
 * @author abhinavs
 */
@SuppressWarnings("serial")
public class DisplayStoreShippingOptionsSite extends PosSiteActionAdapter
{

    /**
     * class name constant
     **/
    public static final String SITENAME = "DisplayStoreShippingOptionsSite";

    /**
     * 
     * @param bus the bus arriving at this site
     **/
    public void arrive(BusIfc bus)
    {
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);

        XChannelShippingCargo shipCargo = (XChannelShippingCargo)bus.getCargo();

        ShippingResponseIfc response = shipCargo.getResponse();

        if (response != null && response.getShippingOptions().size() > shipCargo.getCurrentOptionIndex())
        {
            ShippingOptionIfc option = shipCargo.getResponse().getShippingOptions()
                    .get(shipCargo.getCurrentOptionIndex());
            if (option != null)
            {
                boolean getPreviousBeanModel = false;
                if (bus.getCurrentLetter().getName().equals(CommonLetterIfc.NOT_FOUND))
                {
                    getPreviousBeanModel = true;
                }
                displayShippingOptions(option, shipCargo, ui, pm, utility, getPreviousBeanModel);
            }
            else
            {
                errorDialog(bus);
            }
        }
        else
        {
            errorDialog(bus);
        }
    }

    /**
     * This method prepares store shipping options based on the shipping
     * response from commerce anywhere shipping WS.
     * 
     * @param option
     * @param cargo
     * @param ui
     * @param pm
     * @param utility
     * @param getPreviousBeanModel
     */
    protected void displayShippingOptions(ShippingOptionIfc option, XChannelShippingCargo cargo, POSUIManagerIfc ui,
            ParameterManagerIfc pm, UtilityManagerIfc utility, boolean getPreviousBeanModel)
    {
        XChannelShippingMethodBeanModel model = new XChannelShippingMethodBeanModel();

        AddressIfc addr = cargo.getDeliveryDetail().getDeliveryAddress();
        if (addr != null)
        {
            Vector<String> lines = addr.getLines();
            int sizeOfLines = lines.size();
            if (sizeOfLines >= 1)
            {
                model.setAddressLine1(lines.get(0));
            }
            if (sizeOfLines >= 2)
            {
                model.setAddressLine2(lines.get(1));
            }
            if (sizeOfLines >= 3)
            {
                model.setAddressLine3(lines.get(2));
            }
            model.setCity(addr.getCity());

            int countryIndex = utility.getCountryIndex(addr.getCountry(), pm);
            model.setCountryIndex(countryIndex);
            model.setStateIndex(utility.getStateIndex(countryIndex, addr.getState(), pm));
            model.setCountries(utility.getCountriesAndStates(pm));

            model.setPostalCode(addr.getPostalCode());
        }
        model.setStoreID(cargo.getStoreToShip().getStoreID());
        model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));

        ShippingResponseIfc response = cargo.getResponse();
        if (response != null)
        {
            ArrayList<ShippingOptionIfc> shippingOptions = response.getShippingOptions();
            ShippingOptionIfc shippingOption = shippingOptions.get(cargo.getCurrentOptionIndex());
            if (shippingOption != null)
            {
                ArrayList<Integer> itemReferences = shippingOption.getItemSequenceNoRefs();
                setItemIDandDescription(cargo, itemReferences, model);
                model.setEstimatedAvailableToShipDate(shippingOption.getEstimatedAvailableToShipDate());
            }
        }

        PromptAndResponseModel prompt = new PromptAndResponseModel();
        model.setPromptAndResponseModel(prompt);

        NavigationButtonBeanModel globalModel = new NavigationButtonBeanModel();
        NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();;

        // check if it is the last option
        if (cargo.getCurrentOptionIndex() + 1 == cargo.getShippingOptionList().size())
        {
            localModel.setButtonEnabled(CommonLetterIfc.DONE, true);
            globalModel.setButtonEnabled(CommonLetterIfc.NEXT, false);
        }
        else
        {
            localModel.setButtonEnabled(CommonLetterIfc.DONE, false);
            globalModel.setButtonEnabled(CommonLetterIfc.NEXT, true);
        }

        // Disable Undo and Cancel since either all shipping options can be canceled or none
        if (cargo.getCurrentOptionIndex() > 0)
        {
            globalModel.setButtonEnabled(CommonLetterIfc.UNDO, false);
            globalModel.setButtonEnabled(CommonLetterIfc.CANCEL, false);
        }

        model.setGlobalButtonBeanModel(globalModel);
        model.setLocalButtonBeanModel(localModel);

        ui.showScreen(POSUIManagerIfc.XC_STORE_SHIPPING_METHOD, model);
    }

    /**
     * Set item number and item description in the model
     * 
     * @param cargo
     * @param itemReferences
     * @param model
     */
    protected void setItemIDandDescription(XChannelShippingCargo cargo, ArrayList<Integer> itemReferences,
            XChannelShippingMethodBeanModel model)
    {
        ArrayList<String> itemIDs = new ArrayList<String>();
        ArrayList<String> itemDescriptions = new ArrayList<String>();
        for (Integer ref : itemReferences)
        {
            ShippingItemIfc shipItem = getShipItem(cargo, ref.intValue());
            if (shipItem != null)
            {
                itemIDs.add(shipItem.getItemID());
                itemDescriptions.add(shipItem.getDescriptions().getText());
            }
        }
        model.setItemNumbers(itemIDs);
        model.setItemDescriptions(itemDescriptions);
    }

    /**
     * get shipping line item
     * 
     * @param cargo
     * @param index
     * @return
     */
    protected ShippingItemIfc getShipItem(XChannelShippingCargo cargo, int index)
    {
        ShippingRequestIfc request = cargo.getRequest();
        ArrayList<ShippingItemIfc> shippingItems = (ArrayList<ShippingItemIfc>)request.getShippingItems();
        for (ShippingItemIfc shipItem : shippingItems)
        {
            if (shipItem.getItemSeqNo() == index)
            {
                cargo.addLineItemForDelivery(shipItem.getSaleReturnLineItem());
                return shipItem;
            }
        }
        return null;
    }

    /**
     * Display error dialog
     * 
     * @param bus Service bus.
     */
    protected void errorDialog(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("ShippingOptionNotFound");
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.UNDO);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
}