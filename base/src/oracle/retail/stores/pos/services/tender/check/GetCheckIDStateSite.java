/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/GetCheckIDStateSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:49 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mkochumm  01/30/09 - set country index in order to format phone no. for
 *                         that country
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:47 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/04/13 21:07:36  bwf
 *   @scr 4263 Decomposition of check.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 07 2003 16:11:48   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
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
    
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetCheckIDStateSite extends PosSiteActionAdapter
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
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();
        
        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CheckEntryBeanModel model = (CheckEntryBeanModel) 
                                    ui.getModel(POSUIManagerIfc.ENTER_STATE);
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            
            cargo.getTenderAttributes().put(TenderConstants.ID_STATE, 
                                            model.getState());   
            cargo.getTenderAttributes().put(TenderConstants.ID_COUNTRY,
                                            model.getCountry());
            //set country index so that we know how to format phone no. for that country
            cargo.getTenderAttributes().put(TenderConstants.ID_COUNTRY_INDEX,
                    model.getCountryIndex());
        }
    }
    
    
}
