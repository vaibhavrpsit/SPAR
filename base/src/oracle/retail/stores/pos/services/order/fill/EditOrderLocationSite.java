/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/fill/EditOrderLocationSite.java /main/2 2013/09/10 15:21:38 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  09/10/13 - Fix to set business customer name
 *    sgu       01/04/13 - add new class
 *    sgu       01/03/13 - rename the class for xc only
 *    sgu       01/03/13 - add back order fill flow
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         11/27/2006 5:37:44 PM  Charles D. Baker
 *
 *   Revision 1.5.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.8  2004/10/12 22:24:32  mweis
 *   @scr 7012 Honor the Order's channel when determining a default inventory location.
 *
 *   Revision 1.7  2004/10/11 21:35:09  mweis
 *   @scr 7012 Begin consolidating inventory location loading for Layaways and Orders.
 *
 *   Revision 1.6  2004/10/11 20:20:20  mweis
 *   @scr 7012 Remove dead code.
 *
 *   Revision 1.5  2004/09/27 18:27:40  mweis
 *   @scr 7012 Special Order restoration of "oder list" (and fixes for SCR 7243).
 *
 *   Revision 1.4  2004/06/29 22:03:32  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.2  2004/06/20 20:21:23  aachinfiev
 *   Fixed removal of location when source = destination
 *
 *   Revision 1.3.2.1  2004/06/14 17:48:08  aachinfiev
 *   Inventory location/state related modifications
 *
 *   Revision 1.3  2004/02/12 16:51:23  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:49  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:03:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 26 2002 09:05:20   jriggins
 * Replaced concat of customer name in favor of formatting the text from the CustomerAddressSpec.CustomerName bundle in customerText.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:12:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:41:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Jan 25 2002 17:28:58   dfh
 * updates to prevent modifications to canceled, completed, voided orders
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 *
 *
 *    Rev 1.0   Sep 24 2001 13:01:10   MPM
 *
 * Initial revision.
 *
 *
 *    Rev 1.1   Sep 17 2001 13:10:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.fill;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LocationBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the Edit Order Location screen.
    <P>
    @version $Revision: /main/2 $
**/
//------------------------------------------------------------------------------

public class EditOrderLocationSite extends PosSiteActionAdapter
{

    /**
       site name constant
    **/
    public static final String SITENAME = "EditOrderLocationSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/2 $";

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
       Visual presentation for the Edit Order Location screen.
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
        OrderCargo      cargo = (OrderCargo) bus.getCargo();
        int             status = cargo.getOrder().getStatus().getStatus().getStatus();

        //get ui manager and display Edit Location Screen
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get parameter manager
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        // get utility manager
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        String storeId = cargo.getOperator().getStoreID();
        CodeListIfc orderLocationsList =  utility.getReasonCodes(storeId, CodeConstantsIfc.CODE_LIST_ORDER_LOCATION_REASON_CODES);
        cargo.setOrderLocationsList(orderLocationsList);

        // pass order from cargo to the bean model
        LocationBeanModel model = new LocationBeanModel(cargo.getOrder());

        // set boolean in the model so bean will know if display or edit mode is needed
        if (status != OrderConstantsIfc.ORDER_STATUS_CANCELED &&
            status != OrderConstantsIfc.ORDER_STATUS_COMPLETED &&
            status != OrderConstantsIfc.ORDER_STATUS_VOIDED)
        {
            model.setEditMode(true);
        }
        else
        {
            model.setEditMode(false);
        }

        String selectedLocation = cargo.getOrder().getStatus().getLocation();
        if(selectedLocation != null)
        {
            if(selectedLocation.equals(CodeConstantsIfc.CODE_NOT_APPLICABLE))
            {
                model.setLocalizedNotAvailbleLocation(utility.retrieveCommonText(CodeConstantsIfc.CODE_NOT_APPLICABLE));
            }
        }

        model.setOrderLocationsList(orderLocationsList);
        String statusCode = OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS[status];
        if (statusCode != null)
        {
            model.setStatus(utility.retrieveCommonText(statusCode));  // i18n
        }

        // Inventory locations.  Invisible on the screen.
        String storeID = cargo.getRegister().getWorkstation().getStoreID();
        OrderUtilities util = new OrderUtilities();
        util.loadInventoryLocations(cargo.getOrder(), storeID, pm, model);

        // Create the customer name string from the bundle.
        CustomerIfc customer = cargo.getOrder().getCustomer();
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

        ui.customerNameChanged(customerName);

        ui.showScreen(POSUIManagerIfc.EDIT_LOCATION, model);
    }
}
