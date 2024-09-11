/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/search/SearchByEnterInfoForSwipeCardSite.java /main/3 2012/08/01 14:02:03 yiqzhao Exp $
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

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.domain.utility.DomainUtil;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderSearchCargoIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.ReturnByCreditBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;



public class SearchByEnterInfoForSwipeCardSite extends PosSiteActionAdapter
{
    /**
       class name constant
    **/
    public static final String SITENAME = "SearchByEnterInfoForSwipeCardSite";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";
    
    /**
     * datefield constant
     */
    public static final String DATE_RANGE_FIELD = "dateRangeField";
    
    /**
     * bank acct field
     */
    public static final String ITEM_NUMBER_FIELD   = "itemNumberField";

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
        POSUIManagerIfc ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        UtilityManagerIfc  utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        // read the application properties and get list of date ranges.
        ArrayList<String> rawData = ReturnUtilities.getPropertyValues(ReturnUtilities.APPLICATION_PROPERTIES,
                                                              ReturnUtilities.DATE_RANGE_LIST,
                                                              ReturnUtilities.DEFAULT_DATE_RANGE);
        DataInputBeanModel model = ReturnUtilities.setDateRangeList(utility, rawData);
        model.setScannedFields(ITEM_NUMBER_FIELD);
        ui.showScreen(POSUIManagerIfc.ORDER_SEARCH_BY_SWIPE_CARD_INFO, model);  
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
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.ORDER_SEARCH_BY_SWIPE_CARD_INFO);

        // Retrieve data from model and cargo
        OrderCargo cargo = (OrderCargo) bus.getCargo();
        cargo.setDateRange(false);
        
        // get date range
        int selection = model.getSelectionIndex(ReturnUtilities.DATE_RANGE_FIELD);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        EYSDate date[] = ReturnUtilities.calculateDateRange(selection, pm);
        if ( date != null && date.length == 2 )
        {
      	  	cargo.setStartDate(date[0]);
      	  	cargo.setEndDate(date[1]);
            cargo.setDateRange(true);
        }
        cargo.setItemNumber(model.getValueAsString(ITEM_NUMBER_FIELD));

        cargo.setSearchMethod(OrderSearchCargoIfc.SEARCH_BY_CREDIT_DEBIT_CARD);
    }
}