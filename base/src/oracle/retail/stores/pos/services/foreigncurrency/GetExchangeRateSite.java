/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/foreigncurrency/GetExchangeRateSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:59 mszekely Exp $
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
 *    6    360Commerce 1.5         7/18/2007 2:50:17 PM   Alan N. Sinton  CR
 *         27788 Invoking DomainGateway.getCurrencyTypeList() in order to
 *         update currencies and their exchange rate information.
 *    5    360Commerce 1.4         6/21/2007 12:53:24 PM  Charles D. Baker CR
 *         27280 - Updated to remove dependency of country codes to exist in
 *         tourscript when tendering with alternate currencies.
 *    4    360Commerce 1.3         4/25/2007 8:52:47 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:10 PM  Robert Pearse   
 *
 *   Revision 1.12  2004/04/13 17:19:32  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.11  2004/04/09 19:26:01  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.10  2004/04/08 01:40:21  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.9  2004/04/07 21:34:55  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.8  2004/04/01 01:46:32  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.7  2004/03/31 19:53:37  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.6  2004/03/26 04:20:19  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.5  2004/03/25 14:20:06  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.4  2004/03/23 21:56:19  crain
 *   @scr 4082 Remove Enter Date flow
 *
 *   Revision 1.3  2004/03/22 15:51:03  crain
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

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This class displays the screen to enter the exchange rate/converted amount manually.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetExchangeRateSite extends PosSiteActionAdapter
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
        String letterName = bus.getCurrentLetter().getName();
        CurrencyIfc foreignCurrencyInstance = null;
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        NavigationButtonBeanModel navModel = ((POSBaseBeanModel)ui.getModel()).getLocalButtonBeanModel();
        if(navModel.checkLetter(letterName))
        {
            boolean isForeignAmount = cargo.getTenderAttributes().get(TenderConstants.ALTERNATE_AMOUNT) != null;

            // get the foreign currency instance
            String countryCode = getCountryCode(cargo, letterName);
            foreignCurrencyInstance = getForeignInstance(countryCode, 
                                      (String)cargo.getTenderAttributes().get(TenderConstants.AMOUNT),
                                      isForeignAmount);
            cargo.setCurrentAmount(foreignCurrencyInstance);
            
        }
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);     
    } 

    //---------------------------------------------------------------------
    /**
     Returns country code corresponding to letter sent by button press
     
     @param cargo ForeignCurrencyCargo containing mapping fo country codes to letters 
     @param letterName Letter used to reference a corresponding country code
     @return The country code corresponding to the button pressed
     **/
    //---------------------------------------------------------------------
    protected String getCountryCode(TenderCargo cargo, String letterName)
    {
        ForeignCurrencyCargo foreignCurrencyCargo = (ForeignCurrencyCargo)cargo;
        return foreignCurrencyCargo.getCountryCode(letterName);
    }

    
    //---------------------------------------------------------------------
    /**
     Attempts to create a CurrencyIfc instance
     @param letterName String 
     @param amount String
     @param isForeignAmount boolean
     @return  CurrencyIfc instance representing the proper amount
     **/
    //---------------------------------------------------------------------
    protected CurrencyIfc getForeignInstance(String letterName, String amount, boolean isForeignAmount)
    {
        CurrencyIfc currencyInstance = null;
        try
        {
            // reset the currencies to obtain the latest conversion rate
            DomainGateway.getCurrencyTypeList();

            currencyInstance = DomainGateway.getCurrencyInstance(letterName);
            
            if (isForeignAmount)
            {
                currencyInstance.setStringValue(amount);
                currencyInstance.setBaseAmount(DomainGateway.convertToBase(currencyInstance));
            }
            else
            {
                CurrencyIfc balance = DomainGateway.getBaseCurrencyInstance(amount);
                currencyInstance = DomainGateway.convertFromBase(balance, currencyInstance);
                currencyInstance.setBaseAmount(balance);
            }
        }
        catch (IllegalArgumentException arg)
        {
            logger.error("Unable to retrieve the foreign currency");
            logger.error(arg.getStackTrace());
        }
        return currencyInstance;
    }
}
