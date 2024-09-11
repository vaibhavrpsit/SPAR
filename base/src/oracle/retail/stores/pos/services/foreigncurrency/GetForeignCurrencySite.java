/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/foreigncurrency/GetForeignCurrencySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
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
 *    5    360Commerce 1.4         6/21/2007 12:53:24 PM  Charles D. Baker CR
 *         27280 - Updated to remove dependency of country codes to exist in
 *         tourscript when tendering with alternate currencies.
 *    4    360Commerce 1.3         4/24/2007 1:16:09 PM   Charles D. Baker CR
 *         26556 - I18N Code Merge.
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/05/13 19:25:46  crain
 *   @scr 4222 Gift Receipt for gift certificate
 *
 *   Revision 1.5  2004/03/26 04:20:19  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.4  2004/03/25 14:20:06  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.3  2004/03/23 00:31:09  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.2  2004/03/19 07:16:09  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.1  2004/03/18 21:27:45  crain
 *   @scr 4105 Foreign Currency
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.foreigncurrency;

import java.util.Iterator;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;


//--------------------------------------------------------------------------
/**
    This class displays the screen to get the foreign currency.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetForeignCurrencySite extends PosSiteActionAdapter
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    //----------------------------------------------------------------------
    /**
        Arrive method displays screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        NavigationButtonBeanModel navigationModel = new NavigationButtonBeanModel();
        POSBaseBeanModel model = new POSBaseBeanModel();
        model.setLocalButtonBeanModel(navigationModel);
        
        // get list of countries
        CurrencyTypeIfc[] countries = DomainGateway.getAlternateCurrencyTypes();
        int f = 1;
        for (int i = 0; i < countries.length; i++)
        {
            f++;
            // Reset function key as maximum range of local navigation buttons
            //   is F1 through F8
            if (f == 9)
            {
                f = 2;
            }
            StringBuffer keyName = new StringBuffer("F");
            keyName.append(f);
            String currencyName = UIUtilities.retrieveCommonText(countries[i].getCurrencyCode());
            String actionName = getActionName(cargo, countries[i].getCountryCode());
            navigationModel.addButton(actionName,
                                      currencyName,
                                      currencyName,
                                      true,
                                      keyName.toString());
        }
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.FOREIGN_CURRENCY, model);
    }
    
    //---------------------------------------------------------------------
    /**
     Returns action name to use for a given country code
     
     @param cargo ForeignCurrencyCargo containing mapping fo country codes to letters 
     @param countryCode Country code used to reference a corresponding action name
     @return The action name corresponding to a given country code
     **/
    //---------------------------------------------------------------------
    protected String getActionName(TenderCargo cargo, String countryCode)
    {
        ForeignCurrencyCargo foreignCurrencyCargo = (ForeignCurrencyCargo)cargo;
        return foreignCurrencyCargo.getButtonAction(countryCode);
    }

}
