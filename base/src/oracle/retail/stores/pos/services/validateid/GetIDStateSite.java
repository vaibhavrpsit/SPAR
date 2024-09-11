/* ===========================================================================
* Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/validateid/GetIDStateSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    1    360Commerce 1.0         12/13/2005 4:47:06 PM  Barry A. Pape   
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.validateid;

import oracle.retail.stores.domain.utility.CountryIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel;

//--------------------------------------------------------------------------
/**
    This class displays the ID State Origin screen.
    $Revision: /rgbustores_13.4x_generic_branch/1 $

**/
//--------------------------------------------------------------------------
public class GetIDStateSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by source-code control system
    */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
        This method displays the screen.
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {       
        CheckEntryBeanModel model = new CheckEntryBeanModel();
        
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        // get list of all available states
        CountryIfc[] countries= utility.getCountriesAndStates(pm);
        model.setCountries(countries);
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        ui.showScreen(POSUIManagerIfc.ENTER_STATE, model);  
    }
    
    //----------------------------------------------------------------------
    /**
        This method collect
        @param bus
        @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#depart(oracle.retail.stores.foundation.tour.ifc.BusIfc)
    **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        
        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CheckEntryBeanModel model = (CheckEntryBeanModel) 
                                    ui.getModel(POSUIManagerIfc.ENTER_STATE);
            ValidateIDCargoIfc cargo = (ValidateIDCargoIfc)bus.getCargo();
            
            cargo.setIDState(model.getState());
            cargo.setIDCountry(model.getCountry());
        }
    }
}
