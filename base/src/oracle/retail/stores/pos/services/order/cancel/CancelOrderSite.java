/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/cancel/CancelOrderSite.java /main/14 2013/09/10 15:21:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/10/13 - Fix to set business customer name
 *    sgu       10/29/12 - check cancel order status
 *    sgu       10/24/12 - refactor order view and cancel flow
 *    sgu       05/11/12 - check order customer null pointer
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/12/2007 3:11:10 PM   Anda D. Cadar   call
 *         toFormattedString(locale)
 *    4    360Commerce 1.3         4/25/2007 8:52:21 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:46 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/22 00:06:34  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/07/15 01:24:03  jdeleau
 *   @scr 2495 Fill up the TotalBeanModel with the correct data
 *   for the special orders service.
 *
 *   Revision 1.3  2004/02/12 16:51:21  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:46  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 22 2002 11:48:20   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:13:20   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:42   msg
 * Initial revision.
 *
 *    Rev 1.5   Jan 30 2002 16:04:50   dfh
 * fix my fix...
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.4   Jan 30 2002 15:03:58   dfh
 * use setstatus of line item in case we need to void
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.3   27 Jan 2002 18:21:34   cir
 * Removed the cloned order
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.2   Jan 16 2002 17:27:58   dfh
 * replacing orderdetailspec with orderspec screens, some cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.1   14 Dec 2001 07:52:04   mpm
 * Handled change of getLineItems() to getOrderLineItems().
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *    Rev 1.0   Sep 24 2001 13:00:10   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.cancel;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the edit item status screen with order line items status set to cancelled,
    if the line item was not picked up.
    <P>
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class CancelOrderSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -3566080695643515856L;

    //revision number for source control
    public static final String revisionNumber = "$Revision: /main/14 $";

    //sitename constant
    public static final String SITENAME = "CancelOrderSite";

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
        Displays the edit item status screen with order line items status set to
        cancelled, if the line item was not picked up.
        <P>
            @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        //Initialize Variables
        OrderCargo              cargo           = (OrderCargo) bus.getCargo();

        //Clone the order
        OrderIfc                cancelOrder     = (OrderIfc)cargo.getOrder();
        POSUIManagerIfc         ui              = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel           model           = new ListBeanModel();
        PromptAndResponseModel  prModel         = new PromptAndResponseModel();
        StatusBeanModel         sbModel         = new StatusBeanModel();

        // set the split order items to list model.
        SplitOrderItemIfc[] splitOrderItems = cargo.getSplitOrderItems();
        model.setListModel(splitOrderItems);

        // save summaries if needed when user presses undo
        cargo.clearSelectedSummary(false);

        //PromptAndResponseModel Configured
        prModel.setArguments(cancelOrder.getOrderID());

        //StatusBeanModel Configured
        // Create the string from the bundle.
        CustomerIfc customer = cargo.getOrder().getCustomer();
        if (customer != null)
        {
            UtilityManagerIfc utility =
                (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
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

        //LineItemsModel Configured
        model.setPromptAndResponseModel(prModel);
        model.setStatusBeanModel(sbModel);

        //Display Screen
        ui.showScreen(POSUIManagerIfc.CANCEL_ORDER, model);
    } // end arrive
}                                                                               // end class CancelOrderSite
