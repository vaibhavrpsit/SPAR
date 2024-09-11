/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/EditItemStatusSite.java /main/25 2013/08/08 08:55:47 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  08/06/13 - Fix to display correct business customer name
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    rgour     04/01/13 - CBR cleanup
 *    sgu       02/11/13 - disallow pickup/fill for cross border order
 *    sgu       01/11/13 - disable order item actions buttons
 *    sgu       01/03/13 - add fill and pending
 *    sgu       01/03/13 - rename the class for xc only
 *    sgu       11/26/12 - determine if an order item can be picked up from
 *                         this store
 *    sgu       10/30/12 - refactor sites to check order status for pickup and
 *                         cancel
 *    sgu       10/29/12 - disable pickup and cancel buttons when not
 *                         applicable
 *    sgu       10/25/12 - add filled status for order and order item
 *    sgu       10/04/12 - split order item for pickup
 *    sgu       10/04/12 - add suport to split order line for partial
 *                         pickup/cancel
 *    sgu       05/22/12 - remove unneeded order status from blueprints
 *    sgu       05/11/12 - check customer null pointer and set status bean even
 *                         when customer does not exist
 *    sgu       05/11/12 - check order customer null pointer
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/12/2007 3:11:11 PM   Anda D. Cadar   call
 *         toFormattedString(locale)
 *    4    360Commerce 1.3         4/25/2007 8:52:19 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/15 01:24:03  jdeleau
 *   @scr 2495 Fill up the TotalBeanModel with the correct data
 *   for the special orders service.
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
 *    Rev 1.0   Aug 29 2003 16:03:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 26 2002 11:49:56   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:11:52   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:40   msg
 * Initial revision.
 *
 *    Rev 1.8   Mar 11 2002 16:32:46   dfh
 * enable pickup and canceled buttons, leave filled button disabled
 * Resolution for POS SCR-1546: In Order Options, Pickup status button should be disabled in Fill.
 *
 *    Rev 1.7   Mar 10 2002 18:00:36   mpm
 * Externalized text in dialog messages.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.6   Feb 04 2002 09:12:24   dfh
 * disable Filled button on Edit Item Status screen
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.5   Feb 03 2002 21:11:44   dfh
 * disable Filled button if pickup service
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.4   Jan 25 2002 17:28:58   dfh
 * updates to prevent modifications to canceled, completed, voided orders
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   17 Jan 2002 18:05:56   cir
 * Clone line items
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   15 Jan 2002 18:43:28   cir
 * Use SaleReturnLineItem
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   14 Dec 2001 07:52:08   mpm
 * Handled change of getLineItems() to getOrderLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:01:18   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:40   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.OrderItemStatusIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItem;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.stock.UnitOfMeasureConstantsIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.EditOrderItemStatusListEntry;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the "Edit Item Status" screen.
    <P>
    @version $Revision: /main/25 $
**/
//------------------------------------------------------------------------------

public class EditItemStatusSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 2217498315406525919L;
    /** class name constant */
    public static final String SITENAME = "EditItemStatusSite";
    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /main/25 $";

    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";
    private static final String XCHANNEL_ENABLED = "XChannelEnabled";

    /**
        Customer name bundle tag
    **/
    protected static final String CUSTOMER_NAME_TAG = "CustomerName";
    /**
        Customer name default text
    **/
    protected static final String CUSTOMER_NAME_TEXT = "{0} {1}";

    //--------------------------------------------------------------------------
    /**
        Visual presentation for the Edit Item Status screen.
        <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        //Initialize Variables
        PickupOrderCargo    cargo          = (PickupOrderCargo)bus.getCargo();
        POSUIManagerIfc     ui          = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        UtilityManagerIfc utility =
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ListBeanModel       lineModel   = new ListBeanModel();
        StatusBeanModel     sbModel     = new StatusBeanModel();
        OrderIfc            order       = (OrderIfc)cargo.getOrder();

        NavigationButtonBeanModel  localModel = new NavigationButtonBeanModel();

        //StatusBeanModel Configure
        // Create the customer name string from the bundle.
        CustomerIfc customer = cargo.getOrder().getCustomer();
        if (customer != null)
        {
            Object parms[] = { customer.getFirstName(), customer.getLastName() };
            if(customer.isBusinessCustomer())
            {
                parms[0]=customer.getLastName();
                parms[1]="";
            }
            String pattern =
                utility.retrieveText("CustomerAddressSpec",
                        BundleConstantsIfc.CUSTOMER_BUNDLE_NAME,
                        CUSTOMER_NAME_TAG,
                        CUSTOMER_NAME_TEXT);
         
            String customerName =
                LocaleUtilities.formatComplexMessage(pattern, parms);
            sbModel.setCustomerName(customerName);
        }

        //LineItemsModel Configure
        lineModel.setStatusBeanModel(sbModel);

        //recalculate the split order items if the order has been reset
        SplitOrderItemIfc[] splitOrderItems = cargo.getSplitOrderItems();
        if (splitOrderItems == null)
        {
            splitOrderItems = splitLineItemsForPickup(order, cargo.getStoreStatus().getStore().getStoreID());
            cargo.setSplitOrderItems(splitOrderItems);
        }
        lineModel.setListModel(splitOrderItems);
        lineModel.setLocalButtonBeanModel(localModel);

        //Display Screen and disable all local navigation buttons
        localModel.setButtonEnabled(CommonActionsIfc.PICK_UP, false);
        localModel.setButtonEnabled(CommonActionsIfc.CANCELED, false);
        if (isXChannelEnabled())
        {
            ui.showScreen(POSUIManagerIfc.XC_EDIT_ITEM_STATUS, lineModel);
        }
        else
        {
            localModel.setButtonEnabled(CommonActionsIfc.FILLED, false);
            localModel.setButtonEnabled(CommonActionsIfc.PENDING, false);
            ui.showScreen(POSUIManagerIfc.EDIT_ITEM_STATUS, lineModel);
        }
    }

    //----------------------------------------------------------------------------
    /**
     * Split line items for pickup.
     * @param lineItems order line items
     * @param storeID the store ID
     * @return an array of split line items
     */
    //----------------------------------------------------------------------------
    protected SplitOrderItemIfc[] splitLineItemsForPickup(OrderIfc order, String storeID)
    {
        // Determine if this is a cross currency order pickup which is not supported in ORPOS
        String storeBaseCurrency = DomainGateway.getBaseCurrencyType().getCurrencyCode();
        OrderTransactionIfc orderTransaction = (OrderTransactionIfc)order.getOriginalTransaction();
        boolean isCrossCurrencyPickup =false ;
        if(orderTransaction.getCurrencyType() !=null)
        {
         isCrossCurrencyPickup = !storeBaseCurrency.equalsIgnoreCase(orderTransaction.getCurrencyType().getCurrencyCode());
        }
        SplitOrderItemIfc[] lineItems = order.getSplitLineItemsByStatus();
        List<SplitOrderItemIfc> orderItemResults = new ArrayList<SplitOrderItemIfc>();
        for (SplitOrderItemIfc lineItem : lineItems)
        {
            SaleReturnLineItemIfc originalOrderLineItem = lineItem.getOriginalOrderLineItem();
            OrderItemStatusIfc originalOrderItemStatus = originalOrderLineItem.getOrderItemStatus();
            boolean isXChannelItem = originalOrderItemStatus.isCrossChannelItem();
            boolean isItemFilled = lineItem.getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_FILLED;
            boolean isItemPickedup = lineItem.getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP;
            boolean isItemCancelled = lineItem.getStatus().getStatus() == OrderConstantsIfc.ORDER_ITEM_STATUS_CANCELED;
            boolean pickupFromThisStore = storeID.equalsIgnoreCase(originalOrderItemStatus.getPickupStoreID());

            // A xchannel item has to be filled before pickup
            // A in store order item does not have to be filled before pickup
            // Cross currency order pickup (for cross channel order item only) is not supported
            if ((isXChannelItem && isItemFilled && pickupFromThisStore && !isCrossCurrencyPickup) ||
                (!isXChannelItem && !isItemPickedup && !isItemCancelled && pickupFromThisStore))
            {
                boolean isUnitItem = UnitOfMeasureConstantsIfc.UNIT_OF_MEASURE_TYPE_UNITS.equals(
                        originalOrderLineItem.getPLUItem().getUnitOfMeasure().getUnitID());
                if (isUnitItem)
                {
                    // split the order line item to an array of unit items
                    List<SplitOrderItemIfc> unitLineItems = getSplitUnitLineItems(lineItem);
                    orderItemResults.addAll(unitLineItems);
                }
                else
                {
                    orderItemResults.add(new EditOrderItemStatusListEntry(lineItem,
                            EditOrderItemStatusListEntry.EditActionCode.PICKUP, true));
                }
            }
            else
            {
                orderItemResults.add(new EditOrderItemStatusListEntry(lineItem,
                        EditOrderItemStatusListEntry.EditActionCode.PICKUP, false));
            }
        }

        return orderItemResults.toArray(new SplitOrderItemIfc[0]);
    }

    //----------------------------------------------------------------------------
    /**
     * split the order line item into an array of unit line items
     * @param an order line item
     * @return an array of unit line items
     */
    //----------------------------------------------------------------------------
    protected List<SplitOrderItemIfc> getSplitUnitLineItems(SplitOrderItemIfc lineItem)
    {
        int qty = lineItem.getQuantity().intValue();
        List<SplitOrderItemIfc> splitItems = new ArrayList<SplitOrderItemIfc>();
        for (int i=0; i<qty; i++)
        {
            SplitOrderItemIfc splitItem = new SplitOrderItem(lineItem.getOriginalOrderLineItem(),
                    BigDecimal.ONE, lineItem.getStatus().getStatus());
            splitItems.add(new EditOrderItemStatusListEntry(splitItem,
                    EditOrderItemStatusListEntry.EditActionCode.PICKUP, true));
        }

        return splitItems;
    }

    /**
     * @return a flag indicating if cross channel is enabled.
     */
    protected boolean isXChannelEnabled()
    {
        return Gateway.getBooleanProperty(APPLICATION_PROPERTY_GROUP_NAME, XCHANNEL_ENABLED, false);
    }

} // EditItemStatusSite
