/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/EnterSerialNumberSite.java /main/9 2012/08/30 14:45:05 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/29/12 - Modified to support the UIN Label as a Localized
 *                         Text object.
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    sgu       06/08/10 - fix item interactive screen prompts to include item
 *                         # and description
 *    sgu       06/03/10 - add item description to the screen to get item
 *                         serial #
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/24/09 - Added default UIN Label
 *    nkgautam  12/15/09 - New site to capture serial number for layaway
 *                         transactions
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import java.util.Locale;

import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site displays the ui to collect a serial number for a
 * serialized item.
 * @author nkgautam
 */
@SuppressWarnings("serial")
public class EnterSerialNumberSite extends PosSiteActionAdapter
{

    /**
     * Default UIN Label
     */
    protected String Default_UINLabel = "Serial Number";

    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    /**
     * Constant for cross channel enabled.
     */
    public static final String XCHANNEL_ENABLED = "XChannelEnabled";

    /**
     * Display the ui to collect a serial number for a serialized item.
     */
    public void arrive(BusIfc bus)
    {

        LayawayCargo cargo = (LayawayCargo)bus.getCargo();
        AbstractTransactionLineItemIfc[] lineItems = cargo.getSerializedItems();
        int counter = cargo.getSerializedItemsCounter();

        if (counter < lineItems.length)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[counter];
            cargo.setLineItem(lineItem);
            Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            String UINLabel = lineItem.getPLUItem().getItemClassification().getUINLabel(userLocale);
            if(UINLabel == null || UINLabel.equals(""))
            {
                UINLabel = Default_UINLabel;
            }
            // Get the UI manager
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            POSBaseBeanModel beanModel = new POSBaseBeanModel();
            PromptAndResponseModel parModel = new PromptAndResponseModel();
            NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
            localModel.setButtonEnabled(CommonActionsIfc.PICKUP, false);
            localModel.setButtonEnabled(CommonActionsIfc.DELIVERY, false);            
            boolean isXChannelEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
            if (isXChannelEnabled)
            {
                UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
                String label = utility.retrieveText(BundleConstantsIfc.COMMON, BundleConstantsIfc.COMMON_BUNDLE_NAME,
                        BundleConstantsIfc.SHIPPING_TAG, BundleConstantsIfc.SHIPPING_TAG);
                localModel.setButtonLabel(CommonActionsIfc.DELIVERY, label);
            }
            
            String[] args = new String[3];
            args[0] = lineItem.getItemID();
            args[1] = lineItem.getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            args[2] = UINLabel;
            parModel.setArguments(args);
            beanModel.setPromptAndResponseModel(parModel);
            beanModel.setLocalButtonBeanModel(localModel);

            // Set the serial number for the screen and display the screen
            ui.showScreen(POSUIManagerIfc.ITEM_SERIAL_INPUT, beanModel);
        }
        else
        {
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }

    }

    /**
     * Increments the Item counter for which serial number
     * has been captured
     */
    public void depart(BusIfc bus)
    {
        LayawayCargo cargo = (LayawayCargo)bus.getCargo();
        POSUIManagerIfc     ui   = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        AbstractTransactionLineItemIfc[] lineItems = cargo.getSerializedItems();
        int counter = cargo.getSerializedItemsCounter();
        if(counter < lineItems.length)
        {
            ((SaleReturnLineItemIfc)lineItems[counter]).setItemSerial(ui.getInput());
            cargo.setSerializedItemsCounter(counter + 1);
        }
    }

}
