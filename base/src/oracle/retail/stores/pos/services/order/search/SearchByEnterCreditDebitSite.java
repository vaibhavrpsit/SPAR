/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/SearchByEnterCreditDebitSite.java /main/3 2012/08/01 14:02:03 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     08/01/12 - Update flow for order search by credit/debit card.
* yiqzhao     07/27/12 - modify order search flow and populate order cargo for
*                        searching
* yiqzhao     07/23/12 - modify order search flow for xchannel order and
*                        special order
* yiqzhao     07/20/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.search;

import java.util.ArrayList;

import oracle.retail.stores.domain.utility.CardData;
import oracle.retail.stores.domain.utility.CardDataIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReturnByCreditBeanModel;



public class SearchByEnterCreditDebitSite extends PosSiteActionAdapter
{
    /**credit 
       class name constant
    **/
    public static final String SITENAME = "SearchByEnterCreditDebitSite";
    /**
     * bank acct field
     */
    public static final String ITEM_NUMBER_FIELD   = "itemNumberField";
    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";

    //--------------------------------------------------------------------------
    /**
       Displays the Order Search Options screen and removes the customer name
       from the status area.
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnByCreditBeanModel model;

        //  If re-entering this service use previous data
        String currentLetter = bus.getCurrentLetter().getName();
        if (currentLetter.equals(CommonLetterIfc.RETRY))
        {
            model = (ReturnByCreditBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_CREDIT);
        }
        else
        {
            model = new ReturnByCreditBeanModel();

            // read the application properties and get list of date ranges.
            ArrayList<String> rawData = ReturnUtilities.getPropertyValues(ReturnUtilities.APPLICATION_PROPERTIES,
                                                                  ReturnUtilities.DATE_RANGE_LIST,
                                                                  ReturnUtilities.DEFAULT_DATE_RANGE);

            UtilityManagerIfc  utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            ArrayList<String> i18nData = ReturnUtilities.localalizeDateRangeList(utility, rawData);
            model.setDateRangeList(i18nData);
            model.setScannedFields(ITEM_NUMBER_FIELD);
        }

        // show the screen
        ui.showScreen(POSUIManagerIfc.RETURN_BY_CREDIT, model);        
    }
    
    //--------------------------------------------------------------------------
    /**
       Capture the Order Search
       <P>
       @param bus the bus departing at this site
    **/
    //--------------------------------------------------------------------------    
    public void depart(BusIfc bus)
    {
    	
        if ( !bus.getCurrentLetter().getName().equals(CommonLetterIfc.NEXT) )
        	return;
        
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnByCreditBeanModel model = (ReturnByCreditBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_CREDIT);


        // Retrieve data from model and cargo
        OrderCargo cargo = (OrderCargo) bus.getCargo();
        
        CardDataIfc cardData = new CardData();
        cardData.setLeadingCardNumber(model.getFirstCardDigits());
        cardData.setTrailingCardNumber(model.getLastCardDigits());
        cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_CREDIT_DEBIT_CARD);
        cargo.setCardData(cardData);
       
        cargo.setDateRange(false);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        int selection = model.getDateRangeIndex();
        EYSDate date[] = ReturnUtilities.calculateDateRange(selection, pm);
        if ( date != null && date.length == 2 )
        {
      	  	cargo.setStartDate(date[0]);
      	  	cargo.setEndDate(date[1]);
            cargo.setDateRange(true);
        }

        cargo.setItemNumber(model.getItemNumber());

        cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_CREDIT_DEBIT_CARD); 
    }
}