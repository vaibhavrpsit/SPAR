/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/PricingLaunchShuttle.java /main/11 2011/02/16 09:13:32 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:25 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/07/14 21:32:08  jriggins
 *   @scr 6268 Filter out price adjustment related items from the modify item and pricing services so that they will not be displayed
 *
 *   Revision 1.5  2004/06/07 14:58:49  jriggins
 *   @scr 5016 Added logic to persist previously entered transactions with price adjustments outside of the priceadjustment service so that a user cannot enter the same receipt multiple times in a transaction.
 *
 *   Revision 1.4  2004/02/24 00:50:40  cdb
 *   @scr 3588 Provided for Transaction Discounts to remove
 *   previously existing discounts if they Only One Discount is allowed.
 *
 *   Revision 1.3  2004/02/12 16:48:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Dec 30 2003 20:28:30   cdb
 * Just because no line items exist doesn't mean a transaction hasn't been generated - transaction discounts.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 * 
 *    Rev 1.2   Nov 13 2003 13:17:28   baa
 * fix null pointer 
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.1   Nov 07 2003 12:37:40   baa
 * use SaleCargoIfc
 * Resolution for 3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 05 2003 14:14:34   baa
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.PricingCargo;

/**
 * This shuttle carries the required contents from the POS service to the
 * Pricing service.
 * 
 * @version $Revision: /main/11 $
 */
public class PricingLaunchShuttle extends FinancialCargoShuttle
{
    private static final long serialVersionUID = 8308932217956066966L;

    /** The logger to which log messages will be sent. **/
    protected static final Logger logger = Logger.getLogger(PricingLaunchShuttle.class);

    /** revision number supplied by source-code-control system **/
    public static String revisionNumber = "$Revision: /main/11 $";

    /** class name constant **/
    public static final String SHUTTLENAME = "PricingLaunchShuttle";

    /** Pos Cargo **/
    protected SaleCargoIfc saleCargo = null;

    /**
     * Copies information from the cargo used in the POS service.
     * 
     * @param bus the bus being loaded
     */
    @Override
    public void load(BusIfc bus)
    {
        super.load(bus);
        saleCargo = (SaleCargoIfc)bus.getCargo();
    }

    /**
     * Copies information to the cargo used in the Pricing service.
     * 
     * @param bus the bus being unloaded
     */
    @Override
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        PricingCargo pricingCargo = (PricingCargo)bus.getCargo();
        pricingCargo.setOriginalPriceAdjustmentTransactions(saleCargo.getOriginalPriceAdjustmentTransactions());
               
        SaleReturnTransactionIfc transaction = saleCargo.getTransaction();
        pricingCargo.setTransaction(transaction);
        
        if (transaction != null)
        {                        
            // Get the selected lines items from the sale cargo
            ArrayList<SaleReturnLineItemIfc> itemList = new ArrayList<SaleReturnLineItemIfc>();
            SaleReturnLineItemIfc[] cargoItems = saleCargo.getLineItems();
            if (cargoItems != null)
            {
                for (int i = 0; i < cargoItems.length; i++)
                {
                    if ( !(cargoItems[i].isPriceAdjustmentLineItem() || cargoItems[i].isPartOfPriceAdjustment()) )
                    {
                        itemList.add(cargoItems[i]);
                    }
                }
            }
                        
            SaleReturnLineItemIfc[] items = itemList.toArray(new SaleReturnLineItemIfc[itemList.size()]);     
        
            if (items != null)
            {
                int[] indices = new int[items.length];
    
                if (items.length > 0)
                {
                    pricingCargo.setItems(items);
                    for (int j = 0; j < items.length; j++)
                    {
                         indices[j] = items[j].getLineNumber();
                    }
                    pricingCargo.setIndices(indices);
                }
            }
    
            pricingCargo.setEmployeeDiscountID(transaction.getEmployeeDiscountID());
        }
        
    }
}
