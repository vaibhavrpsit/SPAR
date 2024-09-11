/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/EnterSerialNumberSite.java /main/6 2012/08/30 14:45:05 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     08/29/12 - Modified to support the UIN Label as a Localized
 *                         Text object.
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    jswan     09/14/10 - Modified to support verification that serial number
 *                         entered by operator are contained in the external
 *                         order.
 *    nkgautam  09/06/10 - serialisation return changes
 *    nkgautam  09/06/10 - initial version
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnItemCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

public class EnterSerialNumberSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -2014348964656529945L;

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
     * Prompts the Serial number in case of returns
     */
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ReturnItemCargoIfc cargo = (ReturnItemCargoIfc)bus.getCargo();
        
        SaleReturnLineItemIfc srli = DomainGateway.getFactory().getSaleReturnLineItemInstance();
        srli.setPLUItem(cargo.getPLUItem());
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        String UINLabel = cargo.getPLUItem().getItemClassification().getUINLabel(userLocale);
        if(UINLabel == null || UINLabel.equals(""))
        {
            UINLabel = Default_UINLabel;
        }
        
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
        args[0] = srli.getItemID();
        args[1] = srli.getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        args[2] = UINLabel;
        parModel.setArguments(args);
        beanModel.setPromptAndResponseModel(parModel);
        beanModel.setLocalButtonBeanModel(localModel);

        // Set the serial number for the screen and display the screen
        ui.showScreen(POSUIManagerIfc.ITEM_SERIAL_INPUT, beanModel);
        
    }
}
