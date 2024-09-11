/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/EnterSerialNumberSite.java /main/16 2012/08/30 14:45:05 jswan Exp $
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
 *    nkgautam  12/24/09 - added default UIN Label
 *    nkgautam  12/15/09 - Added depart method to keep track of items whose
 *                         serial number is already prompted and disabling the
 *                         pickup/delivery button on this screen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:05 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/22 00:06:35  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:11:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:40   msg
 * Initial revision.
 *
 *    Rev 1.0   29 Jan 2002 18:36:24   cir
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

// foundation imports
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
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    This site enables the UI to accept a serial number for a sale return
    line item.
    <p>
    @version $Revision: /main/16 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class EnterSerialNumberSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/16 $";

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

    //----------------------------------------------------------------------
    /**
       Enables the UI to accept a serial number for a sale return
       line item.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        PickupOrderCargo cargo = (PickupOrderCargo)bus.getCargo();
        AbstractTransactionLineItemIfc[] lineItems = cargo.getSerializedItems();
        int counter = cargo.getSerializedItemsCounter();

        String UINLabel = null;

        if (counter < lineItems.length)
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[counter];
            cargo.setLineItem(lineItem);

            Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            UINLabel = lineItem.getPLUItem().getItemClassification().getUINLabel(userLocale);
            if(UINLabel == null || UINLabel.equals(""))
            {
                UINLabel = Default_UINLabel;
            }
            // Get the UI manager
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            POSBaseBeanModel beanModel = new POSBaseBeanModel();
            PromptAndResponseModel parModel = new PromptAndResponseModel();
            parModel.setArguments(lineItem.getItemID());
            String[] args = new String[3];
            args[0] = lineItem.getItemID();
            args[1] = lineItem.getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            args[2] = UINLabel;
            parModel.setArguments(args);
            beanModel.setPromptAndResponseModel(parModel);

            beanModel.setPromptAndResponseModel(parModel);
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
    *
    */
   public void depart(BusIfc bus)
   {
     PickupOrderCargo cargo = (PickupOrderCargo)bus.getCargo();
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
