/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/AdvancedCriteriaSearchOrderSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/12/10 - Search external orders flow
 *    acadar    05/03/10 - initial checkin for external order search
 *    acadar    05/03/10 - external order search initial check in
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import oracle.retail.stores.pos.ui.beans.ExternalOrderSearchBeanModel;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * This site displays the ui to collect search criteria for 
 * searching external orders.
 * @author acadar
 */
public class AdvancedCriteriaSearchOrderSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -5393495959775315122L;

    /**
     * Display the ui to collect a search criteria for searching external orders.
     */
    public void arrive(BusIfc bus)
    {
        ExternalOrderSearchBeanModel model = new ExternalOrderSearchBeanModel();
        
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        //set the countries for formatting the telephone number.
        String storeCountry = CustomerUtilities.getStoreCountry(pm);
        int countryIndx = utility.getCountryIndex(storeCountry, pm);
        model.setCountryIndex(countryIndx);
        model.setCountries(utility.getCountriesAndStates(pm));
        
        // show the screen
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.EXT_ORDER_ADV_SEARCH, model);
    }

}
