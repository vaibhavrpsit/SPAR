/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/ValidateAdvancedSearchCriteriaAisle.java /rgbustores_13.4x_generic_branch/2 2011/08/01 15:33:43 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   08/01/11 - Added country to search criteria
 *    ohorne    07/09/10 - fix for setting of searchCriteria's ThisStoreOnly
 *                         flag
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/21/10 - show error dialog if advanced search criteria is
 *                         empty
 *    sgu       05/14/10 - repackage external order classes
 *    abondala  05/12/10 - External orders Search flow
 *    acadar    05/03/10 - initial checkin for external order search
 *    acadar    05/03/10 - external order search initial check in
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteriaIfc;
import oracle.retail.stores.pos.ui.beans.ExternalOrderSearchBeanModel;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


/**
 * This Aisle updates cargo with the search critria.
 *
 * @author abondala
 */
public class ValidateAdvancedSearchCriteriaAisle extends PosLaneActionAdapter
{

	private static final long serialVersionUID = 1775189824237785834L;

    /**
     * Constant for screen name
     */
    public static final String MoreInfoNeededError = "MoreInfoNeededError";
    
	public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ExternalOrderSearchBeanModel model = (ExternalOrderSearchBeanModel)
                                            ui.getModel(POSUIManagerIfc.EXT_ORDER_ADV_SEARCH);
        SearchOrderCargo cargo = (SearchOrderCargo)bus.getCargo();

        ExternalOrderSearchCriteriaIfc criteria = cargo.getSearchCriteria();

        criteria.setFirstName(model.getFirstName());
        criteria.setLastName(model.getLastName());
        criteria.setAccount(model.getAccount());
        criteria.setPhoneNumber(model.getTelephoneNumber());
        criteria.setOrderNumber(model.getOrderNumber());
        criteria.setThisStoreOnly(!model.getIncludeAllStores());
        criteria.setCountry(model.getCountry());

        cargo.setSearchCriteria(criteria);
        
        int emptyFieldsCount = 0;
        if(model.getFirstName().trim().equals(""))
        {
        	++emptyFieldsCount;
        }
        if(model.getLastName().trim().equals(""))
        {
        	++emptyFieldsCount;
        }
        if(model.getAccount().trim().equals(""))
        {
        	++emptyFieldsCount;
        }
        if(model.getTelephoneNumber().trim().equals(""))
        {
        	++emptyFieldsCount;
        }
        if(model.getOrderNumber().trim().equals(""))
        {
        	++emptyFieldsCount;
        }
        if (model.getCountry().trim().equals(""))
        {
            ++emptyFieldsCount;
        }

        if(emptyFieldsCount == 6)
        {
        	displayAdvancedSearchError(bus);
        }
        else
        {
        	bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }

    }

	/**
	 * This error is displayed when user doesn't 
	 * enter any one of the search criteria fields.
	 * @param bus
	 */
	public void displayAdvancedSearchError(BusIfc bus)
	{
		 DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID("MORE_INFO_NEEDED");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Retry");

        // display dialog
        POSUIManagerIfc  ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dialogModel);
	}

}
