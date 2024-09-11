/*===========================================================================
* Copyright (c) 2012, 2014, Oracle and/or its affiliates. All rights reserved.
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/DisplayShippingMethodsSite.java /main/16 2014/06/10 12:04:11 abhinavs Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
*    abhinavs  06/09/14 - CAE add available date during order create
*                         enhancement phase II
*    abhinavs  06/03/14 - CAE add available shipping date during order create enhancement
*    yiqzhao   09/20/13 - Disable Undo button for the shipping options except
*                         for the first one.
*    yiqzhao   09/19/13 - Handle multiple shipping options.
*    yiqzhao   09/18/13 - Avoid selected index from the previous screen
*                         overwrite the current selected index.
*    mkutiana  05/16/13 - retaining the values of the ShippingBeanModel upon
*                         error on the SelectShippingMethodSite
*    yiqzhao   05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
*                         the description to retail price
*                         modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
*    yiqzhao   03/13/13 - Add reason code for shipping charge override for
*                         cross channel and store send.
*    yiqzhao   10/22/12 - Reduce the number of roads.
*    yiqzhao   09/17/12 - fix the issue with multiple order delivery details
*                         for a given item group.
*    yiqzhao   07/16/12 - organize shipping response, handle message exceptions
*    yiqzhao   07/06/12 - merge with the previous transactions.
*    yiqzhao   07/06/12 - using ShippingOptionNotFound error dialog
*    yiqzhao   07/05/12 - Add ship item list on DisplayShippingMethod screen.
*    yiqzhao   07/02/12 - Read text from orderText bundle file and define
*                         screen names
*    yiqzhao   06/29/12 - handle mutiple shipping packages in one transaction
*                        while delete one or more shipping items
*    yiqzhao   06/29/12 - cleanup remove unnecessary code.
*    yiqzhao   06/28/12 - Update shipping flow
*    yiqzhao   06/11/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import java.util.ArrayList;
import java.util.Vector;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.order.OrderDeliveryDetailIfc;
import oracle.retail.stores.domain.shipping.ShippingItemIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.shipping.ShippingOptionIfc;
import oracle.retail.stores.domain.shipping.ShippingRequestIfc;
import oracle.retail.stores.domain.shipping.ShippingResponseIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.conduit.Dispatcher;
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

@SuppressWarnings("serial")
public class DisplayShippingMethodsSite extends PosSiteActionAdapter
{

    /**
     * class name constant
     **/
    public static final String SITENAME = "DisplayShippingMethodsSite";

    /**
     * revision number for this class
     **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    // --------------------------------------------------------------------------
    /**
     * <p>
     * 
     * @param bus the bus arriving at this site
     **/
    // --------------------------------------------------------------------------
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
                    // reason code not provide when changing the shipping price
                    // get previous bean model to set original price,
                    // instruction.
                    getPreviousBeanModel = true;
                }
                displayShippingMethods(option, shipCargo, ui, pm, utility, getPreviousBeanModel);
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
     * This method prepares ship to customer shipping methods based on the
     * shipping response from commerce anywhere shipping WS.
     * 
     * @param option
     * @param cargo
     * @param ui
     * @param pm
     * @param utility
     */
    protected void displayShippingMethods(ShippingOptionIfc option, XChannelShippingCargo cargo, POSUIManagerIfc ui,
            ParameterManagerIfc pm, UtilityManagerIfc utility, boolean getPreviousBeanModel)
    {
        OrderDeliveryDetailIfc deliveryDetail = cargo.getDeliveryDetail();

        XChannelShippingMethodBeanModel model = new XChannelShippingMethodBeanModel();
        XChannelShippingMethodBeanModel previousBeanModel = null;
        if (getPreviousBeanModel)
        {
            previousBeanModel = (XChannelShippingMethodBeanModel)cargo.getShippingBeanModel();
        }

        model.setFirstName(deliveryDetail.getFirstName());
        model.setLastName(deliveryDetail.getLastName());

        AddressIfc addr = deliveryDetail.getDeliveryAddress();
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

                ArrayList<ShippingMethodIfc> shippingMethods = (ArrayList<ShippingMethodIfc>)shippingOption
                        .getShippingMethods();
                model.setShipMethodsList(shippingMethods.toArray(new ShippingMethodIfc[shippingMethods.size()]));
                model.setEstimatedAvailableToShipDate(shippingOption.getEstimatedAvailableToShipDate());
            }
        }

        CodeListIfc reasonCodeList = getReasonCodes(cargo.getStore().getStoreID(),
                CodeConstantsIfc.CODE_LIST_SHIPPING_PRICE_OVERRIDE_REASON_CODES);
        model.inject(reasonCodeList, "", LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

        // Resetting the values in this model from what was entered by the user
        // before error
        // This is only for the first shipping option, since the shipping
        // options. rather than the first one, undo is not allowed.
        if (previousBeanModel != null)
        {
            model.setSelectedShipMethod(previousBeanModel.getSelectedShipMethodindex());
            model.setShippingCharge(previousBeanModel.getShippingCharge());
            model.setInstructions(previousBeanModel.getInstructions());
        }

        cargo.setShippingChargeReasonCodes(reasonCodeList);

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

        // Disable Undo and Cancel since the first shipping option have been
        // added into the transaction.
        if (cargo.getCurrentOptionIndex() > 0)
        {
            globalModel.setButtonEnabled(CommonLetterIfc.UNDO, false);
            globalModel.setButtonEnabled(CommonLetterIfc.CANCEL, false);
        }

        model.setGlobalButtonBeanModel(globalModel);
        model.setLocalButtonBeanModel(localModel);

        ui.showScreen(POSUIManagerIfc.XC_SHIPPING_METHOD, model);
    }

    /**
     * set item number and item description in the model
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
     * Convenience method to retrieve reason codes for the specified code list
     * type based upon the store ID of the current store status.
     * 
     * @param codeListType <code>String</code> containing the name of the code
     *            list.
     * @return code list unless {@link #getStoreStatus()} is null.
     */
    protected CodeListIfc getReasonCodes(String storeID, String codeListType)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)Dispatcher.getDispatcher().getManager(UtilityManagerIfc.TYPE);
        return utility.getReasonCodes(storeID, codeListType);
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
