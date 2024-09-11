/* ===========================================================================
* Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/CompleteCreditCardInfoAisle.java /rgbustores_13.4x_generic_branch/1 2011/07/15 10:58:29 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;


//------------------------------------------------------------------------------
/**
    Gets the date and an item number associated with a search by Credit 
    Card Account Number token.     
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

@SuppressWarnings("serial")
public class CompleteCreditCardInfoAisle extends PosLaneActionAdapter
{
    //--------------------------------------------------------------------------
    /**
         Gets the date and an item number associated with a search by Credit 
         Card Account Number token.     
         @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.COMPLETE_RETURN_BY_CREDIT);

        // Retrieve data from model and cargo
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        int selection = model.getSelectionIndex(ReturnUtilities.DATE_RANGE_FIELD);
        SearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();
        if (searchCriteria == null)
        {
           searchCriteria = DomainGateway.getFactory().getSearchCriteriaInstance();   
        }
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        searchCriteria.setDateRange(ReturnUtilities.calculateDateRange(selection, pm));
        cargo.setSearchCriteria(searchCriteria);
        cargo.setSearchByTender(true);
        cargo.setHaveReceipt(false);                
        bus.mail(new Letter(CommonLetterIfc.VALIDATE), BusIfc.CURRENT);
    }
}
