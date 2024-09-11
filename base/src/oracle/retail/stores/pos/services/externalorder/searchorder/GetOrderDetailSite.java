/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/searchorder/GetOrderDetailSite.java /main/10 2012/05/15 13:15:37 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    05/08/12 - use DomainObjectFactory
 *    cgreene   09/19/11 - move ExternalOrderManager to domain
 *    cgreene   09/16/11 - repackage commext
 *    acadar    07/29/10 - performance logging
 *    ohorne    07/07/10 - external order is not rejected when in training mode
 *    abondala  06/24/10 - new error code for siebel multiple shippings
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  05/20/10 - Updated Search Flow
 *    abondala  05/20/10 - updated search flow
 *    abondala  05/19/10 - search flow update
 *    abondala  05/19/10 - Display list of external orders flow
 *    abondala  05/17/10 - Siebel search flow
 *    acadar    05/03/10 - initial checkin for external order search
 *    acadar    05/03/10 - external order search initial check in
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.searchorder;

import java.util.List;

import oracle.retail.stores.commerceservices.externalorder.ExternalOrderIfc;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteria;
import oracle.retail.stores.commerceservices.externalorder.ExternalOrderSearchCriteriaIfc;
import oracle.retail.stores.commerceservices.logging.PerformanceLevel;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * This site is responsible for getting the detailed information
 * for the selected order and then do all kinds of validation and
 * finally display the transaction on the Sell item screen.
 *
 * @author acadar
 */
public class GetOrderDetailSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = -1133661537184266992L;

    /**
     * Constant for screen name
     */
    public static final String UnknownExternalOrderError = "UnknownExternalOrderError";

    public void arrive(BusIfc bus)
    {

        SearchOrderCargo cargo  = (SearchOrderCargo) bus.getCargo();
        ExternalOrderIfc externalOrder = cargo.getExternalOrder();

        ExternalOrderSearchCriteriaIfc searchCriteria = DomainGateway.getFactory().getExternalOrderSearchCriteriaInstance();
        searchCriteria.setOrderId(externalOrder.getId());
        searchCriteria.setRequestDetail(true);

        ExternalOrderManagerIfc em = (ExternalOrderManagerIfc)bus.getManager(ExternalOrderManagerIfc.TYPE);

        try
        {
            //performance logging
            perfLogger.log(PerformanceLevel.PERF, "GetOrderDetailsSite: query() starts  for order id: " + externalOrder.getId());

            //call the external order manager API to perform the search
            List<ExternalOrderIfc> ordersList = em.query(searchCriteria);

            //performance logging
            perfLogger.log(PerformanceLevel.PERF, "GetOrderDetailsSite: query() ends  for order id: " + externalOrder.getId());
            if(ordersList.size() < 1)
            {
                displayErrorMessage(bus);
            }
            else
            {
                //There is only one order in the list for the selected order which has
                // detailed information.
                ExternalOrderIfc externalOrderDetail = ordersList.get(0);
                cargo.setExternalOrder(externalOrderDetail);
                bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
            }

        }
        catch (ExternalOrderException e)
        {
            logger.error(e);
            try
            {
                if(e.getErrorCode() != ExternalOrderException.CONNECTION_ERROR
                        && !cargo.getRegister().getWorkstation().isTrainingMode())
                {
                    perfLogger.log(PerformanceLevel.PERF, "GetOrderDetailsSite: reject() starts  for order id: " + externalOrder.getId());
                    em.reject(externalOrder.getId());
                    perfLogger.log(PerformanceLevel.PERF, "GetOrderDetailsSite: reject() ends  for order id: " + externalOrder.getId());
                }
            }
            catch (ExternalOrderException eoe)
            {
                 logger.error("The order in the external system, could not be rejected", eoe);
            }

            cargo.setExceptionErrorCode(e.getErrorCode());
            bus.mail(new Letter(CommonLetterIfc.ERROR), BusIfc.CURRENT);
        }

    }

    public void displayErrorMessage(BusIfc bus)
    {
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(UnknownExternalOrderError);
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.CANCEL);

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }

}
