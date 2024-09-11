/* ===========================================================================
* Copyright (c) 2004, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/pickup/SaveCanceledTransactionSite.java /main/15 2013/11/06 16:25:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  11/06/13 - Adding entry to original EJ in order to indicate the
 *                         txn was canceled
 *    vtemker   04/16/13 - Moved constants in OrderLineItemIfc to
 *                         OrderConstantsIfc in common project
 *    cgreene   03/16/12 - split transaction-methods out of utilitymanager
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         12/14/2007 8:59:59 AM  Alan N. Sinton  CR
 *         29761: Removed non-PABP compliant methods and modified card RuleIfc
 *          to take an instance of EncipheredCardData.
 *    7    360Commerce 1.6         8/13/2007 3:01:32 PM   Charles D. Baker CR
 *         27803 - Remove unused domain property.
 *    6    360Commerce 1.5         7/10/2007 4:51:51 PM   Charles D. Baker CR
 *         27506 - Updated to remove old fix for truncating extra decimal
 *         places that are used for accuracy. Truncating is no longer
 *         required.
 *    5    360Commerce 1.4         5/21/2007 9:16:22 AM   Anda D. Cadar   EJ
 *         changes
 *    4    360Commerce 1.3         4/25/2007 8:52:19 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:29:49 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:02 PM  Robert Pearse
 *
 *   Revision 1.6  2004/07/15 22:31:03  cdb
 *   @scr 1673 Removed all deprecation warnings from log and manager packages.
 *   Moved LogArchiveStrategies to deprecation tree.
 *
 *   Revision 1.5  2004/07/06 20:35:11  lzhao
 *   @scr 6020: Fix ClassCastException.
 *
 *   Revision 1.4  2004/03/03 23:15:07  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:51:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Jan 19 2004 14:08:36   DCobb
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.pickup;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.financial.FinancialTotalsIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.manager.ifc.TransactionUtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;

/**
 * This site cancels the current transaction.
 *
 *@version $Revision: /main/15 $
 */
public class SaveCanceledTransactionSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 3934286027507240642L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Saves the current canceled transaction.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // retrieve cargo, transaction
        PickupOrderCargo    pickupOrderCargo       = (PickupOrderCargo)bus.getCargo();

        TransactionUtilityManagerIfc utility = (TransactionUtilityManagerIfc)bus.getManager(TransactionUtilityManagerIfc.TYPE);

        TenderableTransactionIfc transaction = pickupOrderCargo.getTenderableTransaction();
        OrderIfc order = pickupOrderCargo.getOrder();

        if (transaction != null)
        {
            // The sum of picked up item totals
            CurrencyIfc pickedTotals = DomainGateway.getBaseCurrencyInstance("0.00");

            RegisterIfc register = pickupOrderCargo.getRegister();
            register.addNumberCancelledTransactions(1);

            AbstractTransactionLineItemIfc[] allItems = order.getLineItems();

            for (int i=0; i<allItems.length; i++)
            {
                 int status = ((SaleReturnLineItemIfc)allItems[i]).getOrderItemStatus().getStatus().getStatus();

                 if (status > OrderConstantsIfc.ORDER_ITEM_STATUS_PICKED_UP)
                 {
                     pickedTotals = pickedTotals.add(allItems[i].getLineItemAmount().subtract(((SaleReturnLineItemIfc)allItems[i]).getOrderItemStatus().getDepositAmount()));
                 }
            }

            // write a transaction to the database
            try
            {
               // Create a totals object that contains only the cancel
               // transaction numbers.  Set the total on clones of the register
               // and till.  These will be used to accumulate the data
               // in the database.
               TillIfc    till = (TillIfc)register.getCurrentTill().clone();
               RegisterIfc reg = (RegisterIfc)register.clone();
               FinancialTotalsIfc accTotals =
               DomainGateway.getFactory().getFinancialTotalsInstance();
               accTotals.setNumberCancelledTransactions(1);
               accTotals.setAmountCancelledTransactions(pickedTotals);
               till.setTotals(accTotals);
               reg.setTotals(accTotals);
               
               // journal
               JournalManagerIfc journal;
               journal = (JournalManagerIfc)
                 bus.getManager(JournalManagerIfc.TYPE);

               if (journal != null)
               {
                   /*
                    * Write an entry to the journal to indicate that
                    * the transaction was canceled.
                    */
                   String transactionID = transaction.getTransactionID();
                   String pickedTotalString = pickedTotals.toGroupFormattedString();

                   StringBuffer taxCancel = new StringBuffer();

                   String transactionCanceled=I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TRANSACTION_CANCELED,null);

                   Object dataObject[]={pickedTotalString};

                   String totalCanceled = I18NHelper.getString(I18NConstantsIfc.EJOURNAL_TYPE, JournalConstantsIfc.TOTAL_CANCELED,dataObject);


                   taxCancel.append("\n")
                   .append(transactionCanceled)
                   .append("\n")
                   .append("\n")
                   .append(totalCanceled)
                   .append("\n");

                   journal.journal(pickupOrderCargo.getOperator().getLoginID(),
                           transactionID, taxCancel.toString());
                   if (logger.isInfoEnabled()) logger.info(
                           "Transaction " + transactionID + " Canceled");
               }
               else
               {
                   logger.error( "No JournalManager found");
               }
               utility.saveTransaction
                      (transaction,
                       null,
                       till,
                       reg);
            }
            catch (DataException de)
            {
                // an error message is already logged
            }

            // write the hard totals
            try
            {
                utility.writeHardTotals();
            }
            catch (Exception e)
            {
                logger.error(
                             "Unable to save hard totals: " + e + "");
            }

           
        }

        //clear line display device
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn(
                        "Unable to use Line Display: " + e.getMessage() + "");
        }

        /*
         * Send a Continue Letter
         */
        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);

    }
}

