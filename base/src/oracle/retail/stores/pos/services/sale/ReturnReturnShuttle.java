/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/ReturnReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    09/09/10 - added null check in original return transaction
 *                         comparison
 *    asinton   09/01/10 - Compare the lengths of the original return
 *                         transactions to determine if need to add the return
 *                         tender elements to this transaction
 *    asinton   08/31/10 - Prevent adding the ReturnTenderElements if this
 *                         retrieved transaction has been retrieved before.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   02/16/10 - Call setRefreshNeeded(true) in order to initiate the
 *                         update to the CPOI device in the Show Sale screen
 *                         with possible discounted items.
 *    abondala  01/03/10 - update header date
 *    blarsen   10/01/09 - XbranchMerge
 *                         blarsen_bug8841387-validate-trans-discount-on-return-fix
 *                         from rgbustores_13.1x_branch
 *    blarsen   09/22/09 - Do not copy original transaction's transaction-scope
 *                         discounts to the new transaction. This is
 *                         inappropriate according to FAs. This also causes
 *                         confusion and side-effects. (See bugdb 8841387/HPQC
 *                         3962 project 13_1)
 *    blarsen   09/03/09 - Removed code that initializes the new return
 *                         transaction's transaction-level discounts. Since the
 *                         transaction-level discounts are distributed to the
 *                         line items, including these is not required. When
 *                         the trans-lvl discounts are included, they are
 *                         considered in later discount validation checks. This
 *                         is inappropriate according to bryna.
 *    vikini    03/09/09 - Return Item with Trans Disc shows up as Item Disc in
 *                         POS report
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:45 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:52 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:54 PM  Robert Pearse   
 * $
 * Revision 1.13  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.12  2004/07/28 20:48:43  aschenk
 * @scr 6041 - customerid type information is now passed from the return shuttle.
 *
 * Revision 1.11  2004/04/19 20:42:20  tfritz
 * @scr 4340 - Added check to make transaction is not null.
 *
 * Revision 1.10  2004/03/17 16:00:15  epd
 * @scr 3561 Bug fixing and refactoring
 *
 * Revision 1.9  2004/03/15 17:19:44  epd
 * @scr 3561 Fixed saving of original txn line items for returns
 *
 * Revision 1.8  2004/03/03 17:26:50  baa
 * @scr 3561 add journaling of return items
 *
 * Revision 1.7  2004/02/19 19:29:36  epd
 * @scr 3561 Updates for Returns - Enter Size alternate flow
 *
 * Revision 1.6  2004/02/17 20:40:28  baa
 * @scr 3561 returns
 *
 * Revision 1.4  2004/02/16 13:38:31  baa
 * @scr  3561 returns enhancements
 * Revision 1.3 2004/02/12 16:48:17 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:22:50 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:11 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.2 05 Feb 2004 23:11:14 baa returns - multiple items
 * 
 * Rev 1.1 Nov 07 2003 12:37:56 baa use SaleCargoIfc Resolution for 3430: Sale Service Refactoring
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.pos.device.POSDeviceActions;
import oracle.retail.stores.pos.services.returns.returnoptions.ReturnOptionsCargo;

import org.apache.log4j.Logger;

/**
 * This shuttle updates the POS service with the information from the Return
 * service.
 * <p>
 * 
 */
public class ReturnReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    /**
     * serialVersionUID
     */
    static final long serialVersionUID = 4686166716844288887L;


    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.sale.ReturnReturnShuttle.class);

    /**
     * Returns cargo
     */
    protected ReturnOptionsCargo returnsCargo;

    /**
     * Copies information needed from child service.
     * <P>
     * 
     * @param bus
     *            Child Service Bus to copy cargo from.
     */
    public void load(BusIfc bus)
    {
        // retrieve cargo from the child(ReturnOptions Cargo)
        returnsCargo = (ReturnOptionsCargo) bus.getCargo();
    }

    /**
     * Stores information needed by parent service.
     * 
     * @param bus
     *            Parent Service Bus to copy cargo to.
     */
    public void unload(BusIfc bus)
    {

        if (returnsCargo.getTransferCargo() == true)
        {
            SaleReturnTransactionIfc transaction = returnsCargo.getTransaction();
            
            if (transaction != null)
            {    
                // retrieve cargo from the parent(POS Cargo)
                SaleCargoIfc cargo = (SaleCargoIfc) bus.getCargo();

                SaleReturnTransactionIfc[] returnTransactions = returnsCargo.getOriginalReturnTransactions();
                if (returnTransactions != null && returnsCargo.getTransferCargo())
                {
                    // If returnCargo's originalReturnTransactions is not equal in length to the saleCargo's
                    // originalReturnTransactions, then we add the returnTenderElements.  We don't add the
                    // returnTenderElements when they're equal because the refund tender options looks at these
                    // to calculate if the original transaction has only one tender.
                    if(cargo.getOriginalReturnTransactions() == null ||
                       returnTransactions.length != cargo.getOriginalReturnTransactions().length)
                    {
                        transaction.appendReturnTenderElements(returnsCargo.getOriginalTenders());
                    }
                    cargo.setOriginalReturnTransactions(returnTransactions);
                }
    
                updateReturnItems(bus, returnsCargo.isTransactionFound());
                //loop throw the
                cargo.setCustomerInfo(returnsCargo.getCustomerInfo());
                //link the customer to apply any preferred customer discounts
                transaction.linkCustomer(transaction.getCustomer());
                transaction.setCustomerInfo(returnsCargo.getCustomerInfo());
                // update the transaction object in the pos cargo
                cargo.setTransaction(transaction);

                 // setRefreshNeeded(true) in order to update CPOI display with possible
                 // linked customer discounts when we return to the show sale screen
                cargo.setRefreshNeeded(true);
            }
        }

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
     * @param bus
     * @param fromRetrievedTrans
     */
    protected void updateReturnItems(BusIfc bus, boolean fromRetrievedTrans)
    {
        // retrieve cargo from the parent(POS Cargo)
        ReturnItemIfc[] returnItems = returnsCargo.getReturnItems();
        SaleReturnLineItemIfc[] returnLineItems = returnsCargo.getReturnSaleLineItems();
        if (returnItems != null)
        {
            for (int i = 0; i < returnItems.length; ++i)
            {
                returnItems[i].setFromRetrievedTransaction(fromRetrievedTrans);
                addToLineDisplay(bus, returnLineItems[i]);
            }
        }
    }

    /**
     * Add item to line display
     * 
     * @param bus
     * @param item
     */
    public void addToLineDisplay(BusIfc bus, SaleReturnLineItemIfc item)
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

}
