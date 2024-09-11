/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/PerformSearchOrdersSite.java /rgbustores_13.4x_generic_branch/3 2011/09/19 12:16:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/19/11 - move ExternalOrderManager to domain
 *    cgreene   09/16/11 - repackage commext
 *    acadar    07/29/10 - performance logging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/20/10 - updated search flow
 *    abondala  05/19/10 - search flow update
 *    sgu       05/14/10 - repackage external order classes
 *    abondala  05/12/10 - updated
 *    abondala  05/12/10 - updated
 *    abondala  05/12/10 - Search external orders flow
 *    acadar    05/03/10 - added logic for searching for external orders by the
 *                         default search criteria
 *    acadar    05/03/10 - initial checkin for external order search
 *    acadar    05/03/10 - external order search initial check in
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import java.util.List;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteriaIfc;
import oracle.retail.stores.commerceservices.logging.PerformanceLevel;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.log4j.Logger;

/**
 * This is the site that performs an external order search.
 *
 * @author acadar
 */
@SuppressWarnings("serial")
public class PerformSearchOrdersSite extends PosSiteActionAdapter
{
    /**
     * The logger to which log messages will be sent.
     */
    private static final Logger logger = Logger.getLogger(PerformSearchOrdersSite.class);

    /**
     * Calls the External Order Manager API to retrieve all the active orders
     * based on a pre - populated search criteria. Also displays a screen that
     * shows that the search is in progress.
     */
    @Override
    public void arrive(BusIfc bus)
    {

        String letterName = CommonLetterIfc.CONTINUE;

        // get the cargo from the bus
        SearchOrderCargo cargo = (SearchOrderCargo)bus.getCargo();

        //Display search in progress UI
        displaySearchInProgressUI(bus);

        //get the search criteria from the cargo
        ExternalOrderSearchCriteriaIfc searchCriteria = cargo.getSearchCriteria();

        try
        {
            //call the external order manager API to perform the search
            ExternalOrderManagerIfc em = (ExternalOrderManagerIfc)bus.getManager(ExternalOrderManagerIfc.TYPE);
            //performance logging
            perfLogger.log(PerformanceLevel.PERF, "PerformSearchOrdersSite: query() start");
            //search for orders
            List<ExternalOrderIfc> ordersList = em.query(searchCriteria);

            //performance logging ends
            perfLogger.log(PerformanceLevel.PERF, "PerformSearchOrdersSite: query() end");

            cargo.setExternalOrdersList(ordersList);

        }
        catch (ExternalOrderException e)
        {

            logger.error(e);

            cargo.setExceptionErrorCode(e.getErrorCode());
            letterName = CommonLetterIfc.ERROR;

        }

        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    /**
     * Displays the Search in Progress screen
     * @param bus
     */
    protected void displaySearchInProgressUI(BusIfc bus)
    {
        //get manager for ui and put up "search in progress..." screen
       POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

       POSBaseBeanModel baseModel = new POSBaseBeanModel();
       ui.showScreen(POSUIManagerIfc.SEARCH_IN_PROGRESS, baseModel);
    }

}
