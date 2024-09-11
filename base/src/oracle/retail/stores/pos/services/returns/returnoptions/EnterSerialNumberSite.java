/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/EnterSerialNumberSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
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

import java.util.ArrayList;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

/**
 * Displays screen for entering the Item serial number.
 */
@SuppressWarnings("serial")
public class EnterSerialNumberSite extends PosSiteActionAdapter
{

    /**
     * site name constant
     */
    public static final String SITENAME = "EnterSerialNumberSite";

    // --------------------------------------------------------------------------
    /**
     * Displays screen for for entering the serial number.
     *
     * @param bus the bus arriving at this site
     */
    // --------------------------------------------------------------------------
    @SuppressWarnings("unchecked")
	public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = new DataInputBeanModel();
        String currentLetter = bus.getCurrentLetter().getName();
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);

        String promptText = utility.retrieveText("PromptAndResponsePanelSpec", "returnsText", "ReturnByUINPrompt",
                "Enter serial number, select a date range, enter item number and press Next.");

        // If re-entering this service use previous data
        if (currentLetter.equals(CommonLetterIfc.RETRY))
        {
            model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_UIN);
            model.getPromptAndResponseModel().setPromptText(promptText);
        }
        else
        {
            // read the application properties and get list of date ranges.
            @SuppressWarnings("rawtypes")
			ArrayList rawData = ReturnUtilities.getPropertyValues(ReturnUtilities.APPLICATION_PROPERTIES,
                    ReturnUtilities.DATE_RANGE_LIST, ReturnUtilities.DEFAULT_DATE_RANGE);

            model = ReturnUtilities.setDateRangeList(utility, rawData);
        }

        ui.showScreen(POSUIManagerIfc.RETURN_BY_UIN, model);
    }

    /**
     * @param bus the bus undoing its actions
     */
    public void reset(BusIfc bus)
    {

        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        cargo.setSearchCriteria(null);
        cargo.setPLUItemID(null);
        cargo.setPLUItem(null);
        arrive(bus);
    }

}
