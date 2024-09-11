/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/endofday/CancelSuspendedTransactionsSite.java /main/15 2014/03/24 10:54:28 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    03/21/14 - ExternalOrderManager.cancel() now requires workstationId
 *    tksharma  12/10/12 - commons-lang update 3.1
 *    blarsen   08/28/12 - Merge project Echo (MPOS) into trunk.
 *    jswan     01/05/12 - Refactor the status change of suspended transaction
 *                         to occur in a transaction so that status change can
 *                         be sent to CO as part of DTM.
 *    cgreene   09/19/11 - move ExternalOrderManager to domain
 *    cgreene   09/16/11 - repackage commext
 *    acadar    10/27/10 - cleanup unused imports
 *    acadar    10/27/10 - changes for resetting the order status in Siebel
 *    acadar    10/27/10 - changes to reset external order status when
 *                         canceling suspended transactions
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:20 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:58 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:47 PM  Robert Pearse
 *
 *   Revision 1.7  2004/06/03 14:47:44  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.6  2004/04/20 13:13:09  tmorris
 *   @scr 4332 -Sorted imports
 *
 *   Revision 1.5  2004/04/14 15:17:10  pkillick
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:11  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:37  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:17  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:56:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:31:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:13:40   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:26:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:16:26   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.endofday;

import java.util.ArrayList;

import oracle.retail.stores.commerceservices.logging.PerformanceLevel;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderException;
import oracle.retail.stores.domain.manager.externalorder.ExternalOrderManagerIfc;
import oracle.retail.stores.domain.transaction.StatusChangeTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.domain.transaction.TransactionSummaryIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import org.apache.commons.lang3.StringUtils;

/**
 * Cancels all remaining suspended transactions for this store and business
 * date.
 * 
 * @version $Revision: /main/15 $
 */
@SuppressWarnings("serial")
public class CancelSuspendedTransactionsSite extends PosSiteActionAdapter
{
    /**
     * revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";
    /**
     * site name constant
     */
    public static final String SITENAME = "CancelSuspendedTransactionsSite";

    /**
     * Cancel all remaining suspended transactions for this store and business
     * date.
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // letter to be mailed
        String letterName = CommonLetterIfc.FAILURE;

        // get cargo
        EndOfDayCargo cargo = (EndOfDayCargo)bus.getCargo();

        // Read the summary from persistent storage
        TransactionSummaryIfc[] summaries = cargo.getSuspendedTransactionList();

        try
        {
            TransactionUtilityManagerIfc utility =
                (TransactionUtilityManagerIfc) bus.getManager(TransactionUtilityManagerIfc.TYPE);
            RegisterIfc register = cargo.getRegister();
            
            StatusChangeTransactionIfc transaction = DomainGateway.getFactory().
                getStatusChangeTransactionInstance();
            utility.initializeTransaction(transaction, -1, null);
            ArrayList<TransactionSummaryIfc> summaryList = new ArrayList<TransactionSummaryIfc>();
            for (TransactionSummaryIfc summary: summaries)
            {
                TransactionSummaryIfc clone = (TransactionSummaryIfc)summary.clone();
                clone.setTransactionStatus(TransactionConstantsIfc.STATUS_SUSPENDED_CANCELED);
                summaryList.add(clone);
                if (!summary.isTrainingMode())
                {
                    register.addNumberCancelledTransactions(1);
                    register.addAmountCancelledTransactions(summary.getTransactionTotal());
                }
            }
            transaction.setTransactionSummaries(summaryList);
            utility.saveTransaction(transaction);

            // if we got to here, we've got a problem
            letterName = CommonLetterIfc.SUCCESS;
        }
        catch (DataException e)
        {
            // if exception is not no-data, it's an error
            if (e.getErrorCode() != DataException.NO_DATA)
            {
                cargo.setDataExceptionErrorCode(e.getErrorCode());
                letterName = CommonLetterIfc.DB_ERROR;
            }
        }


        for (int i = 0; i < summaries.length; i++)
        {
            // retrieve the transaction details and get the external order id

            TransactionSummaryIfc summary = summaries[i];
            String externalOrderId = summary.getExternalOrderID();
            try
            {
                if (StringUtils.isNotBlank(externalOrderId))
                {
                    ExternalOrderManagerIfc externalOrderManager = (ExternalOrderManagerIfc)bus
                            .getManager(ExternalOrderManagerIfc.TYPE);

                    String orderId = summary.getExternalOrderID();
                    // performance logging
                    perfLogger.log(PerformanceLevel.PERF,
                            "CancelSuspendedTransactionsSite: cancel() starts  for order id: " + orderId);

                    // cancel order
                    RegisterIfc register = cargo.getRegister();
                    externalOrderManager.cancel(orderId, register.getWorkstation().getWorkstationID());

                    // performance logging
                    perfLogger.log(PerformanceLevel.PERF,
                            "CancelSuspendedTransactionsSite: cancel() ends  for order id: " + orderId);

                }
            }
            catch (ExternalOrderException eoe)
            {

                logger.error("External Order was not unlocked, need to manually unlock the order", eoe);
            }

        }

        // mail letter, if necessary
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}