/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/ItemSerialNumberRequiredSite.java /main/3 2012/08/30 14:45:05 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/29/12 - Modified to support the UIN Label as a Localized
 *                         Text object.
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    asinton   02/13/12 - prompt for serial numbers when entering tender if
 *                         items are missing this data
 *
 * ===========================================================================
 */

package oracle.retail.stores.pos.services.sale.validate;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.services.sale.SaleCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * Prompts the operator for the serial number of the item at index
 * SaleCargoIfc.getSerializedItemIndex().
 * @author asinton
 * @since 13.4.1
 */
@SuppressWarnings("serial")
public class ItemSerialNumberRequiredSite extends PosSiteActionAdapter
{
    /**
     * Default UIN Label
     */
    protected static final String DEFAULT_UINLABEL = "Serial Number";

    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    /**
     * Constant for cross channel enabled.
     */
    public static final String XCHANNEL_ENABLED = "XChannelEnabled";

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SaleCargo cargo = (SaleCargo)bus.getCargo();
        SaleReturnTransactionIfc transaction = cargo.getTransaction();
        int index = cargo.getSerializedItemIndex();
        SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)transaction.getLineItems()[index];
        
        // Display the screen and get the data
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        String itemSerial = item.getItemSerial();
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String uinLabel = item.getPLUItem().getItemClassification().getUINLabel(userLocale);
        if(uinLabel == null || uinLabel.equals(""))
        {
            uinLabel = DEFAULT_UINLABEL;
        }
        if(itemSerial == null)
        {
            parModel.setResponseText("");
        }
        else
        {
            parModel.setResponseText(itemSerial);
        }
        String[] args = new String[3];
        args[0] = item.getItemID();
        args[1] = item.getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        args[2] = uinLabel;
        parModel.setArguments(args);
        beanModel.setPromptAndResponseModel(parModel);
        // turn off local buttons
        NavigationButtonBeanModel localButtons = new NavigationButtonBeanModel();
        localButtons.setButtonEnabled(CommonActionsIfc.PICKUP, false);
        localButtons.setButtonEnabled(CommonActionsIfc.DELIVERY, false);
        boolean isXChannelEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
        if (isXChannelEnabled)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String label = utility.retrieveText(BundleConstantsIfc.COMMON, BundleConstantsIfc.COMMON_BUNDLE_NAME,
                    BundleConstantsIfc.SHIPPING_TAG, BundleConstantsIfc.SHIPPING_TAG);
            localButtons.setButtonLabel(CommonActionsIfc.DELIVERY, label);
        }

        beanModel.setLocalButtonBeanModel(localButtons);
        // turn off cancel button
        NavigationButtonBeanModel globalButtons = new NavigationButtonBeanModel();
        globalButtons.setButtonEnabled(CommonActionsIfc.CANCEL, false);
        // turn on undo button
        globalButtons.setButtonEnabled(CommonActionsIfc.UNDO, true);
        beanModel.setGlobalButtonBeanModel(globalButtons);

        // show the screen
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.ITEM_SERIAL_INPUT, beanModel);
    }

}
