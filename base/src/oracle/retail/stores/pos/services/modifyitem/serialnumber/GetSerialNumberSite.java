/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/GetSerialNumberSite.java /main/23 2014/07/07 12:11:44 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  10/13/14 - Disabling pickup and delivery button
 *                         in case of item basket flow
 *    abhinavs  07/07/14 - CAE order pick up disable pickup/delivery buttons
 *                         for take with serialized items
 *    sgu       12/28/12 - check kit header at line item level
 *    jswan     08/29/12 - Modified to support the UIN Label as a Localized
 *                         Text object.
 *    jswan     05/14/12 - Modified to support Ship button feature.
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    sgu       06/08/10 - fix item interactive screen prompts to include item
 *                         # and description
 *    sgu       06/03/10 - add item description to the screen to get item
 *                         serial #
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   03/29/10 - add check for return line items in order to disable
 *                         buttons
 *    nkgautam  02/18/10 - pickup/delivery button needs to be disabled for
 *                         non-serialised items or when serial number is
 *                         already captured
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/24/09 - Added default UIN label
 *    nkgautam  12/21/09 - disable pickup and delivery button for kit item in a
 *                         transaction
 *    nkgautam  12/16/09 - Code review updates
 *    nkgautam  12/15/09 - Pickup/Delivery Additions and code added to fetch
 *                         UIN Label
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:51 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.3  2004/02/12 16:51:06  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:01:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:18:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:37:54   msg
 * Initial revision.
 *
 *    Rev 1.2   16 Jan 2002 13:01:34   baa
 * allow for adding serial item to non serialized items
 * Resolution for POS SCR-579: Unable to manually enter a serial number to an item
 *
 *    Rev 1.1   07 Dec 2001 12:51:58   pjf
 * Code review updates.
 * Resolution for POS SCR-8: Item Kits
 *
 *    Rev 1.0   14 Nov 2001 06:44:44   pjf
 * Initial revision.
 * Resolution for POS SCR-8: Item Kits
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

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
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

/**
 * This site displays the ui to collect a serial number for a serialized item.
 * The depart method associates the input from the ui with the line item's
 * serialNumber attribute.
 * 
 * @version $Revision: /main/23 $
 */
public class GetSerialNumberSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7095012270509393585L;

    /**
     * Default UIN Label
     */
    protected static final String Default_UINLabel = "Serial Number";

    /**
     * Constant for application property group
     */
    public static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    
    /**
     * Constant for cross channel enabled.
     */
    public static final String XCHANNEL_ENABLED = "XChannelEnabled";

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/23 $";

    /**
     * Display the ui to collect a serial number for a serialized item.
     *
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        SerializedItemCargo cargo = (SerializedItemCargo)bus.getCargo();

        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        String UINLabel = null;

        // Display the screen and get the data
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        PromptAndResponseModel parModel = new PromptAndResponseModel();
        SaleReturnLineItemIfc srli = cargo.getItem();
        String itemSerial = srli.getItemSerial();
        Locale userLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        UINLabel = srli.getPLUItem().getItemClassification().getUINLabel(userLocale);
        boolean isItemSerialised = srli.getPLUItem().isSerializedItem();
        if(UINLabel == null || UINLabel.equals(""))
        {
            UINLabel = Default_UINLabel;
        }
        parModel.setResponseText(itemSerial == null ? "" : itemSerial);
        String[] args = new String[3];
        args[0] = srli.getItemID();
        args[1] = srli.getItemDescription(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        args[2] = UINLabel;
        parModel.setArguments(args);
        beanModel.setPromptAndResponseModel(parModel);

        // determine if there are any return line items already in the transaction
        boolean hasReturnLineItems = false;
        if (cargo.getTransaction() instanceof SaleReturnTransactionIfc)
        {
            hasReturnLineItems = ((SaleReturnTransactionIfc)cargo.getTransaction()).hasReturnItems();
        }

        // turn the buttons on or off
        NavigationButtonBeanModel localModel = new NavigationButtonBeanModel();
        if (srli.isFromExternalOrder() || itemSerial != null || !isItemSerialised ||
                srli.isKitHeader() || hasReturnLineItems || cargo.isForReturn() || cargo.getTransaction().isOrderPickupOrCancel()
                || cargo.getTransaction().getIsItemBasketTransactionComplete())
        {
            localModel.setButtonEnabled(CommonActionsIfc.PICKUP, false);
            localModel.setButtonEnabled(CommonActionsIfc.DELIVERY, false);
        }
        else
        {
            localModel.setButtonEnabled(CommonActionsIfc.PICKUP, true);
            localModel.setButtonEnabled(CommonActionsIfc.DELIVERY, true);
        }
        boolean isXChannelEnabled = Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
        if (isXChannelEnabled)
        {
            UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            String label = utility.retrieveText(BundleConstantsIfc.COMMON, BundleConstantsIfc.COMMON_BUNDLE_NAME,
                    BundleConstantsIfc.SHIPPING_TAG, BundleConstantsIfc.SHIPPING_TAG);
            localModel.setButtonLabel(CommonActionsIfc.DELIVERY, label);
        }

        beanModel.setLocalButtonBeanModel(localModel);

        // show the screen
        ui.showScreen(POSUIManagerIfc.ITEM_SERIAL_INPUT, beanModel);
    }

}
