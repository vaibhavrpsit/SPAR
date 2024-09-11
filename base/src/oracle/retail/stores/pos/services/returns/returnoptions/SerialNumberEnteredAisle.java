/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/SerialNumberEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mchellap  12/10/09 - Serialisation return without receipt changes
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

// foundation imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

public class SerialNumberEnteredAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4831657275328007672L;

    /**
     * Unique Identification Number field
     */
    public static final String UIN_FIELD = "uinNumberField";

    /**
     * Gets the Serial number from UI
     *
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();

        DataInputBeanModel model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_UIN);

        // Setup date range
        int selection = model.getSelectionIndex(ReturnUtilities.DATE_RANGE_FIELD);

        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();
        if (searchCriteria == null)
        {
            searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();
        }
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        searchCriteria.setDateRange(ReturnUtilities.calculateDateRange(selection, pm));

        // Get UIN from UI
        String serialNumber = model.getValueAsString(UIN_FIELD);
        searchCriteria.setItemSerialNumber(serialNumber);

        // set the search criteria in the cargo
        cargo.setSearchCriteria(searchCriteria);

        Letter letter = new Letter(CommonLetterIfc.VALIDATE);

        bus.mail(letter, BusIfc.CURRENT);

    }
}
