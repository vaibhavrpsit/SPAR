/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/DisplayOrderDetailsSite.java /main/15 2013/09/10 15:21:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/10/13 - Fix to set business customer name
 *    sgu       10/24/12 - refactor order view and cancel flow
 *    sgu       05/11/12 - check customer null pointer and set status bean even
 *                         when customer does not exist
 *    sgu       05/11/12 - check order customer null pointer
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         7/12/2007 3:11:11 PM   Anda D. Cadar   call
 *         toFormattedString(locale)
 *    5    360Commerce 1.4         4/25/2007 8:52:20 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/22/2006 11:45:14 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:05 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse
 *
 *   Revision 1.4  2004/07/15 01:24:03  jdeleau
 *   @scr 2495 Fill up the TotalBeanModel with the correct data
 *   for the special orders service.
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:26   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 23 2002 16:52:02   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:12:46   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 16 2002 17:27:58   dfh
 * replacing orderdetailspec with orderspec screens, some cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.0   Sep 24 2001 13:01:04   MPM
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:10:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

// foundation imports
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.lineitem.SplitOrderItemIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the Order Details screen.

    @version $Revision: /main/15 $
**/
//------------------------------------------------------------------------------
public class DisplayOrderDetailsSite extends PosSiteActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 5352001426908462014L;

    public static final String SITENAME = "DisplayOrderDetailsSite";

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
        Display the Order Detail with Undo enabled.
        <p>
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        //Initialize Variables
        OrderCargo                  cargo   = (OrderCargo) bus.getCargo();
        POSUIManagerIfc             ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel               model   = new ListBeanModel();
        StatusBeanModel             sbModel = new StatusBeanModel();

        //recalculate the split order items if the order has been reset
        SplitOrderItemIfc[] splitOrderItems = cargo.getSplitOrderItems();
        if (splitOrderItems == null)
        {
            splitOrderItems = cargo.getOrder().getSplitLineItemsByStatus();
            cargo.setSplitOrderItems(splitOrderItems);
        }
        model.setListModel(splitOrderItems);

        //StatusBeanModel Configured
        // Create customer name from the bundle.
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
                        customer.getFirstLastName());
            String customerName =
                LocaleUtilities.formatComplexMessage(pattern, parms);
            sbModel.setCustomerName(customerName);
        }
        //OrderBeanModel Configured
        model.setStatusBeanModel(sbModel);

        //Display Screen
        ui.showScreen(POSUIManagerIfc.ORDER_DETAILS, model);
    }
}
