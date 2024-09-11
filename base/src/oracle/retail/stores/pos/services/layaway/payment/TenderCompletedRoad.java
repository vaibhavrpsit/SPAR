/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/TenderCompletedRoad.java /main/15 2012/09/12 11:57:10 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/30/12 - get journalmanager from bus
 *    jswan     10/07/11 - Modified to prevent a Layway Initiate transaction
 *                         from being marked as complete.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    acadar    04/05/10 - use default locale for currency and date/time
 *                         display
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *      5    360Commerce 1.4         6/5/2008 4:02:29 AM    Manikandan
 *           Chellapan CR#31879 Fixed layaway discount problem with instant
 *           credit tender
 *      4    360Commerce 1.3         5/21/2007 9:16:20 AM   Anda D. Cadar   EJ
 *           changes
 *      3    360Commerce 1.2         3/31/2005 4:30:23 PM   Robert Pearse
 *      2    360Commerce 1.1         3/10/2005 10:25:55 AM  Robert Pearse
 *      1    360Commerce 1.0         2/11/2005 12:14:49 PM  Robert Pearse
 *     $
 *     Revision 1.3  2004/02/12 16:50:53  mcs
 *     Forcing head revision
 *
 *    Revision 1.2  2004/02/11 21:51:22  rhafernik
 *     @scr 0 Log4J conversion and code cleanup
 *
 *     Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *     updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:52   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:20:18   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:35:36   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:42   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:42   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.financial.PaymentIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.OfflinePaymentBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This class updates the status of the layaway, adds the payment, and sets the
 * cargo.
 * 
 * @version $Revision: /main/15 $
 */
@SuppressWarnings("serial")
public class TenderCompletedRoad extends PosLaneActionAdapter
{
    /**
     * lane name constant
     */
    public static final String LANENAME = "TenderCompletedRoad";
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Performs the traversal functionality for the aisle. In this case, The
     * status of the layaway is checked and updated accordingly.
     * 
     * @param bus the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        LayawayCargo layawayCargo = (LayawayCargo)bus.getCargo();
        POSUIManagerIfc ui =
                        (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Gets Layaway and Payment after Tender
        LayawayIfc layaway = layawayCargo.getLayaway();
        PaymentIfc payment = layawayCargo.getPayment();
        TenderableTransactionIfc transaction = layawayCargo.getTransaction();

        // Changes status accordingly...
        if (transaction.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_INITIATE &&
                layaway.getBalanceDue().equals(payment.getPaymentAmount())) // BalanceDue == PaymentAmount
        {
            layaway.changeStatus(LayawayConstantsIfc.STATUS_COMPLETED);
        }
        else
        if (layaway.getStatus() == LayawayConstantsIfc.STATUS_UNDEFINED)
        {
            layaway.changeStatus(LayawayConstantsIfc.STATUS_NEW);
            // CR31789 : Instant credit enrollment discount is applied only after layaway
            // creation, So reset the layaway total and balance amounts if instant credit
            // discount is applied for a layaway creation.
            TransactionTotalsIfc transactionTotals = transaction.getTransactionTotals();
            CurrencyIfc finalTotal = transactionTotals.getGrandTotal();
            layaway.setTotal(finalTotal);
            layaway.setBalanceDue(finalTotal);
        }
        else
        {
            layaway.changeStatus(LayawayConstantsIfc.STATUS_ACTIVE);
        }

        payment.setTransactionID(transaction.getTransactionIdentifier());
        layaway.addPaymentAmount(payment.getPaymentAmount());

        // sets payment balance due after payment has been accounted for
        payment.setBalanceDue( layaway.getBalanceDue() );

        //  Journal entry for payment
        JournalManagerIfc journal = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

        //  Sets Total amount paid to current payment and allows
        //  the database to accumulate this total.  This is done
        //  for offline payments when the application does not have
        //  the previous amount paid.  Layaway Delete handles this after printing.
        if ( transaction.getTransactionType() != TransactionIfc.TYPE_LAYAWAY_DELETE )
        {
            layaway.setTotalAmountPaid( payment.getPaymentAmount() );
        }

        //  Journal balance due on a layaway if initial layaway
        if (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_INITIATE)
        {
            String balanceString = layaway.getBalanceDue().toGroupFormattedString();
            StringBuffer sb = new StringBuffer();
            Object[] dataArgs = new Object[2];
    		dataArgs[0] = balanceString;
            sb.append(Util.EOL)
            .append(I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.BALANCE_DUE_LABEL, dataArgs));

            if (journal != null)
            {
                journal.journal(transaction.getCashier().getLoginID(),
                                transaction.getTransactionID(),
                                sb.toString());
            }
            else
            {
                logger.error( "No JournalManager found");
            }
        }
        // Resets the beanModel for offline flow
        POSBaseBeanModel model = ( POSBaseBeanModel )ui.getModel(POSUIManagerIfc.LAYAWAY_OFFLINE);
        if ( model != null )
        {
            if ( model instanceof OfflinePaymentBeanModel)
            {
                ( (OfflinePaymentBeanModel)model ).resetModel();
            }
        } 
    }
}