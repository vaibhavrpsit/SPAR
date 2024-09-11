/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processorder/LockOrderSite.java /main/11 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   09/19/11 - move ExternalOrderManager to domain
 *    cgreene   09/16/11 - repackage commext
 *    acadar    08/23/10 - show external order number in the EJ for retrieved
 *                         transaction
 *    acadar    07/29/10 - performance logging
 *    ohorne    07/07/10 - external order is not locked when in training mode
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    acadar    06/14/10 - rafactored to use string builder
 *    acadar    06/14/10 - journal external order id
 *    acadar    06/03/10 - set the unlock order to false if order could not be
 *                         locked
 *    acadar    05/28/10 - merged with tip
 *    acadar    05/26/10 - refactor shipping code
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    05/25/10 - additional fixes for the process order flow
 *    acadar    05/17/10 - added call to ExternalOrderMAnager; additional fixes
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processorder;


import oracle.retail.stores.commerceservices.logging.PerformanceLevel;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.utility.TransactionUtility;

/**
 * This site populates an external order search criteria object with the default search
 * criteria for external orders
 *
 * @author acadar
 */
public class LockOrderSite extends PosSiteActionAdapter
{
    /**
     * static private final  Logger  perfLogger = Logger.getLogger(PerformanceLevel.PERFORMANCE_CAT);
     */

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -1258020204539387447L;

    /**
     * Calls the ExternalOrder Manager API to lock the order
     */
    public void arrive(BusIfc bus)
    {
        ProcessOrderCargo cargo = (ProcessOrderCargo)bus.getCargo();
        String externalOrderId = cargo.getExternalOrder().getId();
        LetterIfc letter = new Letter(CommonLetterIfc.CONTINUE);
        cargo.setLockOrder(true);

        //if order does not have line items cancel
        if(!cargo.getExternalOrder().getItemIterator().hasNext())
        {
            //if order could not be locked exit the service
            logger.error("External Order does not have line items");
            cargo.setLockOrder(false);
            letter = new Letter(CommonLetterIfc.FAILURE);
        }
        else if (!cargo.getRegister().getWorkstation().isTrainingMode())
        {
            // calls the ExternalOrderManager to lock the order
            try
            {

                ExternalOrderManagerIfc orderManager = (ExternalOrderManagerIfc) bus.getManager(ExternalOrderManagerIfc.TYPE);
                //performance logging

                perfLogger.log(PerformanceLevel.PERF, "LockOrderSite: lock() starts for order id: " + externalOrderId);

                orderManager.lock(externalOrderId);

                perfLogger.log(PerformanceLevel.PERF, "LockOrderSite: lock() ends  for order id: " + externalOrderId);

                //journal the order number
                JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
                TransactionUtility.journalExternalOrder(journal, cargo.getExternalOrder().getNumber());
            }
            catch (ExternalOrderException e)
            {
                //if order could not be locked exit the service
                logger.error("External Order could not be locked", e);
                cargo.setLockOrder(false);
                letter = new Letter(CommonLetterIfc.FAILURE);

            }
        }
        bus.mail(letter, BusIfc.CURRENT);

    }



}
