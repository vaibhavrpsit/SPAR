/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/DisplayOrderLocationSite.java /main/14 2011/12/05 12:16:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         11/27/2006 5:37:43 PM  Charles D. Baker
 *
 *   Revision 1.6.2.1  2004/10/15 18:50:30  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.8  2004/10/12 22:24:32  mweis
 *   @scr 7012 Honor the Order's channel when determining a default inventory location.
 *
 *   Revision 1.7  2004/10/11 21:35:14  mweis
 *   @scr 7012 Begin consolidating inventory location loading for Layaways and Orders.
 *
 *   Revision 1.6  2004/10/06 02:44:25  mweis
 *   @scr 7012 Special and Web Orders now have Inventory.
 *
 *   Revision 1.5  2004/09/27 18:27:40  mweis
 *   @scr 7012 Special Order restoration of "oder list" (and fixes for SCR 7243).
 *
 *   Revision 1.4  2004/06/29 22:03:32  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.1  2004/06/14 17:48:09  aachinfiev
 *   Inventory location/state related modifications
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
 *    Rev 1.0   Aug 29 2003 16:03:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:12:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 24 2001 13:00:12   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:28   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

// foundation imports
import java.util.Locale;
import java.util.Vector;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LocationBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the Order Location screen.

    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class DisplayOrderLocationSite extends PosSiteActionAdapter
{

    public static final String SITENAME = "DisplayOrderLocationSite";

    //--------------------------------------------------------------------------
    /**
       Visual presentation for the Order Location screen.
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        OrderCargo cargo = (OrderCargo) bus.getCargo();

        // Get managers.
        POSUIManagerIfc ui =
            (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        // Spike the model with the Order from the cargo.
        LocationBeanModel model = new LocationBeanModel(cargo.getOrder());
        model.setEditMode(false);

        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        String storeId = cargo.getOperator().getStoreID();
        CodeListIfc orderLocationsList =  utility.getReasonCodes(storeId, CodeConstantsIfc.CODE_LIST_ORDER_LOCATION_REASON_CODES);
        cargo.setOrderLocationsList(orderLocationsList);

        String selectedLocation = cargo.getOrder().getStatus().getLocation();
        if(selectedLocation != null)
        {
            if(selectedLocation.equals(CodeConstantsIfc.CODE_NOT_APPLICABLE))
            {
                model.setLocalizedNotAvailbleLocation(utility.retrieveCommonText(CodeConstantsIfc.CODE_NOT_APPLICABLE));
            }
        }

        model.setOrderLocationsList(orderLocationsList);

        String status = model.getStatus();
        if (status != null)
        {
            model.setStatus(utility.retrieveCommonText(status));  // i18n
        }

        // Inventory locations.  Invisible.
        String storeID = cargo.getRegister().getWorkstation().getStoreID();
        OrderUtilities util = new OrderUtilities();
        util.loadInventoryLocations(cargo.getOrder(), storeID, pm, model);

        ui.setModel(POSUIManagerIfc.ORDER_LOCATION, model);
        ui.showScreen(POSUIManagerIfc.ORDER_LOCATION);

        // when user hits Accept button, an "Accept" letter is sent from the UI.
    }

}
