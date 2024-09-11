/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/PriceAdjustmentReturnShuttle.java /main/15 2012/09/12 11:57:20 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mahising  02/18/09 - fixed price adjustment issue
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:24:19 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:13:22 PM  Robert Pearse
 * $
 * Revision 1.6  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.5  2004/06/30 00:41:58  jriggins
 * @scr 5466 Added logic for maintaining original SaleReturnTransactionIfc instances for transactions which contain returns. This is needed in order to update the line item data for the return components of price adjusted line items in the database.
 *
 * Revision 1.4  2004/06/24 20:38:00  jriggins
 * @scr 4984 Modified the journalling output
 *
 * Revision 1.3  2004/06/10 23:06:36  jriggins
 * @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service
 *
 * Revision 1.2  2004/06/07 14:58:49  jriggins
 * @scr 5016 Added logic to persist previously entered transactions with price adjustments outside of the priceadjustment service so that a user cannot enter the same receipt multiple times in a transaction.
 *
 * Revision 1.1  2004/05/05 18:44:53  jriggins
 * @scr 4680 Moved Price Adjustment button from Sale to Pricing
 *
 * Revision 1.5  2004/04/27 21:26:56  jriggins
 * @scr 3979 Code review cleanup
 *
 * Revision 1.4  2004/04/19 03:28:42  jriggins
 * @scr 3979 Added journaling logic
 *
 * Revision 1.3  2004/03/30 00:04:59  jriggins
 * @scr 3979 Price Adjustment feature dev
 *
 * Revision 1.2  2004/03/17 16:00:15  epd
 * @scr 3561 Bug fixing and refactoring
 *
 * Revision 1.1  2004/03/05 16:34:26  jriggins
 * @scr 3979 Price Adjustment additions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing;

import java.util.Iterator;
import java.util.Map;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIDIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.priceadjustment.PriceAdjustmentCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle updates the POS service with the information from the
 * priceadjustment service.
 *
 */
public class PriceAdjustmentReturnShuttle implements ShuttleIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -8832909442986379064L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(PriceAdjustmentReturnShuttle.class);

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * Returns cargo
     */
    protected PriceAdjustmentCargo priceAdjCargo;

    /**
     * Copies information needed from child service.
     * 
     * @param bus Child Service Bus to copy cargo from.
     */
    @Override
    public void load(BusIfc bus)
    {
        // retrieve cargo from the child(ReturnOptions Cargo)
        priceAdjCargo = (PriceAdjustmentCargo) bus.getCargo();
    }

    /**
     * Stores information needed by parent service.
     * 
     * @param bus Parent Service Bus to copy cargo to.
     */
    @Override
    public void unload(BusIfc bus)
    {

        if (priceAdjCargo.getTransferCargo() == true)
        {
            // retrieve cargo from the parent(POS Cargo)
            PricingCargo cargo = (PricingCargo) bus.getCargo();

            TransactionIDIfc origTransID;
            SaleReturnTransactionIfc originalTransaction = priceAdjCargo.getOriginalTransaction();
            if (originalTransaction != null)
            {
                cargo.addOriginalPriceAdjustmentTransaction(originalTransaction);
                origTransID = DomainGateway.getFactory().getTransactionIDInstance();
                origTransID.setTransactionID(originalTransaction.getTransactionID());
            }
            else
            {
                logger.warn("Original transaction not set: required for price adjustments.");
            }

            cargo.setCustomerInfo(priceAdjCargo.getCustomerInfo());

            SaleReturnTransactionIfc transaction = priceAdjCargo.getTransaction();
            if (transaction != null)
            {
                transaction.appendReturnTenderElements(priceAdjCargo.getOriginalTenders());

                // upate the transaction object in the pos cargo
                cargo.setTransaction(transaction);
            }

            // If available, add the original transaction used in the price adjustment to the list of
            // return transactions so that the return items will be updated in the database correctly
            Map<String,SaleReturnTransactionIfc> originalPriceAdjTrans = priceAdjCargo.getOriginalPriceAdjustmentTransactions();
            if (originalPriceAdjTrans != null)
            {
                Iterator<SaleReturnTransactionIfc> transactions = originalPriceAdjTrans.values().iterator();
                while(transactions.hasNext())
                {
                    cargo.addOriginalReturnTransaction(transactions.next());
                }

            }

            // Journal each price adjustment line item.
            PriceAdjustmentLineItemIfc priceAdjLineItems[] = priceAdjCargo.getPriceAdjustmentLineItems();
            if (priceAdjLineItems != null)
            {
                journalLineItem(bus, priceAdjLineItems);
            }
        }

        priceAdjCargo.resetOriginalPriceAdjustmentTransactions();

        //clear the line display device
        try
        {
            POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
            pda.clearText();
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage() + "");
        }
    }

    /**
     * Journals price adjustment line items
     *
     * @param bus
     *            The bus
     * @param item
     *            price adjustment line item to journal
     */
    public void journalLineItem(BusIfc bus, PriceAdjustmentLineItemIfc[] items)
    {
        for (int i = 0; i < items.length; i++)
        {
            PricingCargo cargo = (PricingCargo) bus.getCargo();
            SaleReturnTransactionIfc transaction = (SaleReturnTransactionIfc)cargo.getTransaction();

            JournalManagerIfc journal = (JournalManagerIfc) bus.getManager(JournalManagerIfc.TYPE);
            if (journal != null)
            {
                StringBuffer sb = new StringBuffer();
                sb.append(items[i].toJournalString(transaction,LocaleMap.getLocale(LocaleConstantsIfc.JOURNAL)));

                journal.journal(cargo.getOperator().getLoginID(), transaction.getTransactionID(), sb.toString());
            }
            else
            {
                logger.error(bus.getServiceName() + " No JournalManager found");
            }

        }
    }

    /**
     * Adds item to the line display
     *
     * @param bus
     *            The Bus
     * @param item
     *            price adjustment line item to add to line display
     */
    public void addToLineDisplay(BusIfc bus, PriceAdjustmentLineItemIfc item)
    {
        //Show item on Line Display device
        POSDeviceActions pda = new POSDeviceActions((SessionBusIfc) bus);
        try
        {
            pda.lineDisplayItem(item);
        }
        catch (DeviceException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage());
        }

    }

    /**
     * Returns a string representation of this object.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class:  PriceAdjustmentReturnShuttle (Revision " + getRevisionNumber() + ")"
                + hashCode());
        // pass back result
        return (strResult);
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
