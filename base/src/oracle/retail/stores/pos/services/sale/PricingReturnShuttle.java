/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/PricingReturnShuttle.java /main/14 2013/03/27 14:09:59 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  03/27/13 - Refund more amount than paid amount when price adj
 *                         and return are done simultaneous
 *    icole     04/19/12 - Forward port mukothan_bug-13013218, corrects problem
 *                         of a single item transaction capable of being price
 *                         adjusted and returned resulting in refund greater
 *                         than original amount.
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    npoola    07/20/10 - check to create a transaction for a valid price
 *                         adjustment
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:22 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:25 PM  Robert Pearse   
 *
 * Revision 1.14.2.1  2004/12/08 18:25:35  bwf
 * @scr 7811 Dont copy over item if undo or cancel.
 *
 * Revision 1.14  2004/06/30 00:41:58  jriggins
 * @scr 5466 Added logic for maintaining original SaleReturnTransactionIfc instances for transactions which contain returns. This is needed in order to update the line item data for the return components of price adjusted line items in the database.
 *
 * Revision 1.13  2004/06/10 23:06:35  jriggins
 * @scr 5018 Added logic to support replacing PriceAdjustmentLineItemIfc instances in the transaction which happens when shuttling to and from the pricing service
 *
 * Revision 1.12  2004/06/07 14:58:49  jriggins
 * @scr 5016 Added logic to persist previously entered transactions with price adjustments outside of the priceadjustment service so that a user cannot enter the same receipt multiple times in a transaction.
 *
 * Revision 1.11  2004/05/27 19:31:33  jdeleau
 * @scr 2775 Remove unused imports as a result of tax engine rework
 *
 * Revision 1.10  2004/05/27 17:12:48  mkp1
 * @scr 2775 Checking in first revision of new tax engine.
 *
 * Revision 1.9  2004/05/05 18:44:53  jriggins
 * @scr 4680 Moved Price Adjustment button from Sale to Pricing
 *
 * Revision 1.8  2004/04/20 13:05:35  tmorris
 * @scr 4332 -Sorted imports
 *
 * Revision 1.7  2004/04/14 15:17:10  pkillick
 * @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 * Revision 1.6  2004/04/08 22:14:55  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

/**
 * This shuttle carries the required contents from the Pricing service to the
 * POS service.
 * 
 * @version $Revision: /main/14 $
 */
public class PricingReturnShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = -3460316988579474577L;

    /** The logger to which log messages will be sent. * */
    protected static final Logger logger = Logger.getLogger(PricingReturnShuttle.class);

    /** revision number supplied by source-code-control system * */
    public static String revisionNumber = "$Revision: /main/14 $";

    /** class name constant * */
    public static final String SHUTTLENAME = "PricingReturnShuttle";

    protected PricingCargo pricingCargo = null;

    /**
     * Copies information from the cargo used in the Pricing service.
     * 
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        pricingCargo = (PricingCargo) bus.getCargo();
    }

    /**
     * Copies information to the cargo used in the POS service.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        SaleCargoIfc pc = (SaleCargoIfc) bus.getCargo();
        pc.setRefreshNeeded(true);

        LetterIfc ltr = bus.getCurrentLetter();
        String letter = null;
        if (ltr != null)
        {
            letter = ltr.getName();
        }

        // set boolean to true if undo or cancel so we dont have to check multiple times
        boolean undoOrCancel = false;
        if (letter != null)
        {
            if(letter.equalsIgnoreCase("Undo") || letter.equalsIgnoreCase("Cancel"))
            {
                undoOrCancel = true;
            }
        }
        
        // if the letter is undo or cancel then dont modify the transaction because it screws up advanced pricing rules.
        if (pc.getTransaction() != null && pricingCargo.getItems() != null && !undoOrCancel)
        {
            AbstractTransactionLineItemIfc[] lineItems =
                pricingCargo.getItems();
            SaleReturnLineItemIfc[] items =
                new SaleReturnLineItemIfc[lineItems.length];

            for (int i = 0; i < lineItems.length; i++)
            {
                pc.getTransaction().replaceLineItem(
                    lineItems[i],
                    lineItems[i].getLineNumber());
                pc.setItemModifiedIndex(lineItems[i].getLineNumber());

            } // End of item loop
            pc.setLineItems(items);
            if (pricingCargo.getEmployeeDiscountID() != null)
            {
                pc.getTransaction().setEmployeeDiscountID(
                    pricingCargo.getEmployeeDiscountID());
            }
        }
        else if (pricingCargo.getTransaction() != null && !undoOrCancel)
        {
            // if we got an employee discount id, put it in the transaction
            SaleReturnTransactionIfc tempTransaction =
                (SaleReturnTransactionIfc) pricingCargo.getTransaction();
            if (pricingCargo.getEmployeeDiscountID() != null)
            {
                tempTransaction.setEmployeeDiscountID(
                    pricingCargo.getEmployeeDiscountID());
            }
            pc.setTransaction(tempTransaction);
        }
        
        if (pc.getTransaction() == null && pricingCargo.getTransaction() != null)
        {
            pc.setTransaction((SaleReturnTransactionIfc) pricingCargo.getTransaction());
        }     
        
        pc.setOriginalPriceAdjustmentTransactions(pricingCargo.getOriginalPriceAdjustmentTransactions());
         
        // Add the original return transactions (which include price adjusted transactions) to list in SaleCargo
        SaleReturnTransactionIfc[] origReturnTrans = pricingCargo.getOriginalReturnTransactions();
        for (int i = 0; i < origReturnTrans.length; i++)
        {
            // this adds the transaction to the list if it not already on the list.  In this case don't want to replace.
            pc.addOriginalReturnTransaction(origReturnTrans[i]);
        }
        pricingCargo.resetOriginalReturnTransactions();
        pricingCargo.resetOriginalPriceAdjustmentTransactions();
    }
    
    /**
     * Retrieves the source-code-control system revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
