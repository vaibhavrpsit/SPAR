/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/EnterStoreSearchCriteriaSite.java /main/1 2012/05/02 14:07:49 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     04/29/12 - Added to support cross channel create pickup order
 *                         feature.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CountryModel;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;

//--------------------------------------------------------------------------
/**
    This site calls the UI manager to display the store search criteria data
    entry screen.
    @version $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class EnterStoreSearchCriteriaSite extends EnterPickupCustomerInfoSite
{
    //----------------------------------------------------------------------
    /**
        This method calls the UI manager to display the store search criteria data
        entry screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        DataInputBeanModel model = getModel(bus);
        POSUIManagerIfc uiManager = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        uiManager.showScreen(POSUIManagerIfc.STORE_SEARCH_FOR_ORDER, model);
    }

    protected DataInputBeanModel getModel(BusIfc bus)
    {
        DataInputBeanModel model = new DataInputBeanModel();
        // Initialize the model
        model.setValue("firstNameField", "");
        model.setValue("storeNameField", "");
        model.setValue("storeNumberField", "");
        model.setValue("cityField", "");
        model.setValue("postalCodeField", "");

        CountryModel cModel = getCountryModel(bus);
        model.setValue("countryField", cModel);
        ValidatingComboBoxModel vcbm = new ValidatingComboBoxModel(cModel.getStateNames());
        vcbm.setSelectedItem(cModel.getStateInfo().getStateName());
        model.setValue("stateField", vcbm);
        
        return model;
    }   
}