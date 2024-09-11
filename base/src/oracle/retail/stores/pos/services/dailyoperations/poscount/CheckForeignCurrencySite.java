/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/poscount/CheckForeignCurrencySite.java /main/11 2013/01/29 15:22:39 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  01/16/13 - Foreign Currency screen will be disabled when
 *                         foreign currency to count parameter is set to no
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:52:33 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/06/07 18:29:38  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Add foreign currency counts.
 *
 *   Revision 1.2  2004/05/26 21:32:25  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Testing with no alternate currencies configured.
 *
 *   Revision 1.1  2004/05/20 22:48:39  dcobb
 *   @scr 4204 Feature Enhancement: Till Options
 *   Added Foreign Currency To Count dialog.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.poscount;

import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialCountTenderItemIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;


//------------------------------------------------------------------------------
/**
     Checks to see if foreign currency has been collected into the till. 
     @version $Revision: /main/11 $
**/
//------------------------------------------------------------------------------

public class CheckForeignCurrencySite extends PosSiteActionAdapter
{
    /** revision number */
    public static String revisionNumber = "$Revision: /main/11 $";
    /** Foreign Currency to Count dialog spec name */
    public static final String FOREIGN_CURRENCY_TO_COUNT = "ForeignCurrencyToCount";

    //--------------------------------------------------------------------------
    /**
        Check to see if foreign currency has been collected into the till. 
        Mails a Success letter if the type count is not TILL. Mails Yes letter 
        if foreign currecncy has been collected; mails No letter otherwise.
        @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {                                   // begin arrive()
        // Get the cargo
        PosCountCargo cargo = (PosCountCargo)bus.getCargo();
        String letterName = null;
        
        CurrencyTypeIfc[] altCurrencies = DomainGateway.getAlternateCurrencyTypes();
        // get the local navigation button bean model
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        // Initialize list of tenders to count
        String[] countTenders = null;
        try
        {
            countTenders = pm.getStringValues("TendersToCountAtTillReconcile");
        }
        catch (ParameterException pe)
        {
            logger.warn(pe);
        }
        
        // If the count is for float, loan or pick, exit.
        if ((cargo.getCountType() != PosCountCargo.TILL) || (getAlternateCurrenciesToCount() == 0)
                || !checkForeignCurrencyToCount(countTenders, altCurrencies))
        {
            letterName = CommonLetterIfc.SUCCESS;
        }
        else // Count is for Till; perform the check for foregin currency.
        {
            if (foreignCurrencyCollected(cargo))
            {    
                letterName = CommonLetterIfc.YES;
            }
            else
            {
                // Display Foreign Currency To Count dialog
                DialogBeanModel model = new DialogBeanModel();
                model.setResourceID(FOREIGN_CURRENCY_TO_COUNT);
                // Yes, No buttons
                model.setButtonLetter(DialogScreensIfc.BUTTON_NO, CommonLetterIfc.SUCCESS);
                model.setType(DialogScreensIfc.CONFIRMATION);
                
                POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
            }
        }
        if (letterName != null)
        {    
            bus.mail(new Letter(letterName), BusIfc.CURRENT);
        }
    }                                   // end arrive()

    //----------------------------------------------------------------------
    /**
        Determines the number of configured alternate currencies.
        <P>
        @return int  The number of configured alternate currencies 
     **/
    //----------------------------------------------------------------------
    protected int getAlternateCurrenciesToCount()
    {
        int numberAltCurrencies = 0;
        CurrencyTypeIfc[]   altCurrencies = DomainGateway.getAlternateCurrencyTypes();
        if (altCurrencies != null)
        {
            numberAltCurrencies = altCurrencies.length;
        }
        return numberAltCurrencies;
    }


    //----------------------------------------------------------------------
    /**
       Check the till for any foreign currency collected. Returns true if 
       a foreign currency has been collected; false otherwise.
       <P>
       @param cargo  The cargo
       @return boolean true if foreign currency has been collected
    **/
    //----------------------------------------------------------------------
    public boolean foreignCurrencyCollected(PosCountCargo cargo)
    {                                   // begin foreignCurrencyCollected()
        boolean returnValue = false;
        
        FinancialCountTenderItemIfc[] count = cargo.getForeignCurrencyFinancialCountTenderTotals();
        if ((count != null) && (count.length > 0))
        {
            returnValue = true;
        }
                    
        return returnValue;
    }                                   // end foreignCurrencyCollected()
    
    /**
     * @param currencies
     * @param altCurrencies
     * @return
     */
    protected boolean checkForeignCurrencyToCount(String[] currencies, CurrencyTypeIfc[] altCurrencies)
    {
        if (currencies == null)
        {
            return false;
        }
        for (CurrencyTypeIfc altCurrency : altCurrencies)
        {
            for (String currency : currencies)
            {
                if (currency.toUpperCase().startsWith(altCurrency.getCurrencyCode().toUpperCase())
                        && currency.toUpperCase().endsWith("CASH"))
                {
                    return true;
                }
            }
        }
        return false;
    }

}
