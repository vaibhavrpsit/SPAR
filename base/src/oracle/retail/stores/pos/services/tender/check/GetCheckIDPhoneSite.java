/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/GetCheckIDPhoneSite.java /main/13 2013/06/10 10:59:48 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/10/13 - Fix to correctly set ctry index and preventing AIOOB
 *                         exception
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
 *    Rev 1.0   Nov 07 2003 16:11:46   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
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
    
    $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class GetCheckIDPhoneSite extends PosSiteActionAdapter
{
    /**
        revision number supplied by source-code control system
    */
    public static final String revisionNumber = "$Revision: /main/13 $";
    
    //----------------------------------------------------------------------
    /**
        The arrive method displays the screen.
        @param bus BusIfc
     **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        CheckEntryBeanModel model = new CheckEntryBeanModel();
        
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        CountryIfc[] countries= utility.getCountriesAndStates(pm);
        model.setCountries(countries);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);
        
        //set country index so that we know how to display format phone no. for that country
        Integer countryIndex=utility.getCountryIndex(storeCountry, pm);
        if (countryIndex != null)
        {
            model.setCountryIndex(countryIndex.intValue());
        }
    
        
        ui.showScreen(POSUIManagerIfc.ENTER_PHONE, model);
    }       
    
    //----------------------------------------------------------------------
    /**
        The depart method captures the user input.
        @param bus BusIfc
     **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();
        
        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            CheckEntryBeanModel model = (CheckEntryBeanModel) 
                                                ui.getModel(POSUIManagerIfc.ENTER_PHONE);
            TenderCargo cargo = (TenderCargo)bus.getCargo();

            cargo.getTenderAttributes().put(TenderConstants.PHONE_NUMBER, model.getPhoneNumber());
           
        }        
    }
    
}
