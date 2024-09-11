/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/CompleteCreditInfoSite.java /rgbustores_13.4x_generic_branch/1 2011/07/15 10:58:29 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import java.util.ArrayList;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

/**
 * Displays the UI to request the date and an item number associated 
 * with a search by Credit Card Account Number token.     
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class CompleteCreditInfoSite extends PosSiteActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5906262303382672384L;

    /**
     * Displays the UI to request the date and an item number associated 
     * with a search by Credit Card Account Number token.     
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = new DataInputBeanModel();
        
        UtilityManagerIfc  utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        // read the application properties and get list of date ranges.
        ArrayList<String> rawData = ReturnUtilities.getPropertyValues(ReturnUtilities.APPLICATION_PROPERTIES,
                                                              ReturnUtilities.DATE_RANGE_LIST,
                                                              ReturnUtilities.DEFAULT_DATE_RANGE);
        model = ReturnUtilities.setDateRangeList(utility, rawData);
        model.setScannedFields("itemNumberField");
        ui.showScreen(POSUIManagerIfc.COMPLETE_RETURN_BY_CREDIT, model);        
    }
}
