/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/EnterStoreSearchCriteriaSite.java /main/2 2012/07/02 14:31:53 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     07/02/12 - Read text from orderText bundle file and define screen
*                        names
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CountryModel;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;

//--------------------------------------------------------------------------
/**
    This site calls the UI manager to display the store search criteria data
    entry screen.
    @version $Revision: /main/2 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class EnterStoreSearchCriteriaSite extends  PosSiteActionAdapter
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
        uiManager.showScreen(POSUIManagerIfc.STORE_SEARCH_FOR_SHIP, model); 
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
    
    /*
     * Create the combo box model for the available countries
     */
    protected CountryModel getCountryModel(BusIfc bus)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);

        CountryModel model = new CountryModel();
        String storeState   = CustomerUtilities.getStoreState(pm);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);

        int countryIndx = utility.getCountryIndex(storeCountry, pm);
        model.setCountryIndex(countryIndx);
        model.setStateIndex(utility.getStateIndex(countryIndx,
                storeState.substring(3,storeState.length()), pm));
        model.setCountries(utility.getCountriesAndStates(pm));

        return model;
    }
}