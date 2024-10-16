/* ===========================================================================
* Copyright (c) 2010, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/CancelOrderSite.java /main/11 2014/03/24 10:54:28 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    03/21/14 - ExternalOrderManager.cancel() now requires workstationId
 *    cgreene   09/19/11 - move ExternalOrderManager to domain
 *    cgreene   09/16/11 - repackage commext
 *    acadar    07/29/10 - fix dialog message description
 *    acadar    07/29/10 - performance logging
 *    ohorne    07/08/10 - external order is not canceled when in training mode
 *    acadar    06/18/10 - clear external order info from transaction
 *    acadar    06/18/10 - poslog changes for external order
 *    acadar    06/17/10 - updates for poslog and for new ExternalOrdermanager
 *                         API
 *    sgu       06/16/10 - add cancel and reject order apis
 *    acadar    06/03/10 - changes for signature capture
 *    acadar    05/28/10 - merged with tip
 *    acadar    05/27/10 - added code for displaying a different error message;
 *                         additional fixes
 *    acadar    05/26/10 - refactor shipping code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/25/10 - additional fixes for the process order flow
 *    acadar    05/21/10 - additional changes for process order flow
 *    acadar    05/17/10 - added call to ExternalOrderMAnager; additional fixes
 *    acadar    05/17/10 - pluged in the ExternalOrderManager
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;

import oracle.retail.stores.commerceservices.logging.PerformanceLevel;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;



/**
 * This site displays an error message and returns to the calling use case
 *
 * @author acadar
 */
public class CancelOrderSite extends PosSiteActionAdapter
{

    /**
     *  Serial version UID
     */
    private static final long serialVersionUID = -9002448666220988126L;
    /**
     * Constant for screen name
     */
    public static final String DataValidationErrorWithExternalOrder = "DataValidationErrorWithExternalOrder";
    /**
     * Displays an error message
     */
    public void arrive(BusIfc bus)
    {
//      Using "generic dialog bean".
        String resourceId = DataValidationErrorWithExternalOrder;
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(resourceId);
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);


    }

    /**
     * Departs
     */
    public void depart(BusIfc bus)
    {
        // send message to the external order system to unlock the order
        ProcessOrderCargo  cargo = (ProcessOrderCargo)bus.getCargo();
        String orderId = cargo.getExternalOrder().getId();
        if (cargo.isLockOrder() && !cargo.getRegister().getWorkstation().isTrainingMode())
        {
            try
            {
                ExternalOrderManagerIfc externalOrderManager = (ExternalOrderManagerIfc)bus.getManager(ExternalOrderManagerIfc.TYPE);
                //performance logging
                perfLogger.log(PerformanceLevel.PERF, "CancelOrderSite: cancel() starts for order id: " + orderId);

                externalOrderManager.cancel(orderId, cargo.getRegister().getWorkstation().getWorkstationID());

                perfLogger.log(PerformanceLevel.PERF, "CancelOrderSite: cancel() ends  for order id: " + orderId);
            }
            catch (ExternalOrderException eoe)
            {
                logger.error("The order in the external system, could not be unlocked", eoe);
            }
        }
        //remove any external order reference from transaction
        cargo.cleanUpTransaction();


    }



}
