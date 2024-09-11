/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	Jyoti Rawal		09/04/2013		Initial Draft: Changes for Employee Discount
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package max.retail.stores.pos.services.sale;

// Java import
import org.apache.log4j.Logger;

import max.retail.stores.pos.services.pricing.MAXPricingCargo;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;

//------------------------------------------------------------------------------
/**
 * This shuttle carries the required contents from the Pricing service to the
 * POS service.
 * <P>
 * 
 * @version $Revision: 5$
 */
//------------------------------------------------------------------------------
public class MAXPricingReturnShuttle extends FinancialCargoShuttle
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -8728559732498225682L;

	/** The logger to which log messages will be sent. * */
    protected static Logger logger =
        Logger.getLogger(
        		max.retail.stores.pos.services.sale.MAXPricingReturnShuttle.class);

    /** revision number supplied by source-code-control system * */
    public static String revisionNumber = "$Revision: 5$";

    /** class name constant * */
    public static final String SHUTTLENAME = "MAXPricingReturnShuttle";

    protected MAXPricingCargo pricingCargo = null;

    //--------------------------------------------------------------------------
    /**
     * Copies information from the cargo used in the Pricing service.
     * <P>
     * 
     * @param bus
     *            the bus being loaded
     */
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
        pricingCargo = (MAXPricingCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
     * Copies information to the cargo used in the POS service.
     * <P>
     * 
     * @param bus
     *            the bus being unloaded
     */
    //--------------------------------------------------------------------------
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
            SaleReturnLineItemIfc[] lineItems =
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
        
        pc.setOriginalPriceAdjustmentTransactions(pricingCargo.getOriginalPriceAdjustmentTransactions());
        
        // Add the original return transactions (which include price adjusted transactions) to list in SaleCargo
        SaleReturnTransactionIfc[] origReturnTrans = pricingCargo.getOriginalReturnTransactions();
        for (int i = 0; i < origReturnTrans.length; i++)
        {
            pc.addOriginalReturnTransaction(origReturnTrans[i]);
        }
        
        pricingCargo.resetOriginalReturnTransactions();
        pricingCargo.resetOriginalPriceAdjustmentTransactions();
    }

    
    //---------------------------------------------------------------------
    /**
     * Retrieves the source-code-control system revision number.
     * <P>
     * 
     * @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }
}
