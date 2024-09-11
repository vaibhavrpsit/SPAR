/* =============================================================================
* Copyright (c) 2003, 2014, Oracle and/or its affiliates. All rights reserved.
 * =============================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/CheckTransactionTypeSite.java /main/14 2014/05/09 13:15:49 yiqzhao Exp $
 * =============================================================================
 * NOTES
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/09/14 - Adding take with item while doing order item pickup.
 *    asinton   02/14/12 - XbranchMerge asinton_bug-13709171 from
 *                         rgbustores_13.4x_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    rkar      11/12/08 - Adds/changes for POS-RM integration
 *    rkar      11/07/08 - Additions/changes for POS-RM integration
 *
 * =============================================================================
     $Log:
      3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
      2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
      1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
     $
     Revision 1.3  2004/02/12 16:48:21  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:22:50  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
     updating to pvcs 360store-current

 * 
 *    Rev 1.2   08 Nov 2003 01:26:52   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.1   Nov 05 2003 14:36:12   sfl
 * Added instance of checking to identify special order/layaway transactions.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 * 
 *    Rev 1.0   Nov 04 2003 16:12:26   sfl
 * Initial revision.
 * Resolution for POS SCR-3430: Sale Service Refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;


// foundation imports
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.LayawayTransaction;
import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
 * This site checks the transaction types.
 */
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class CheckTransactionTypeSite extends PosSiteActionAdapter
{
    /**
        Revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /** constant letter for process serialized items for order */ 
    public static final String PROCESS_SERIALIZED_ITEMS_FOR_ORDER_LETTER = "ProcessSerializedItemsForOrder";
    
    //----------------------------------------------------------------------
    /**
        Check the transaction types and mail a proper letter
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        // Default the letter value to Sale
        String letter = "Sale";
        
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        
        SaleReturnTransactionIfc  transaction = cargo.getTransaction();
        
        if (transaction != null)
        {
            int transType = transaction.getTransactionType();
            
            if ((transType == TransactionIfc.TYPE_LAYAWAY_INITIATE) ||
                (transaction instanceof LayawayTransaction)) 
            {
                letter = "Layaway";
            }
            else if (transaction.isOrderPickupOrCancel() )
            {
                letter = "Sale";
            }
            else if (transType == TransactionIfc.TYPE_ORDER_INITIATE) 
            {
                letter = PROCESS_SERIALIZED_ITEMS_FOR_ORDER_LETTER;
            }
            else if ((transType == TransactionIfc.TYPE_SALE))
            {
            	if ( !hasReturnItem(transaction))
            	{
            		letter = "Sale";
            	}
            	else
            	{
            		letter = "Return";
            	}
            }
            else if ((transType == TransactionIfc.TYPE_RETURN))
            {
            	letter = "Return";
            }
            bus.mail(new Letter(letter), BusIfc.CURRENT);   
        }
    }
   
    /**
     * Check if there is one or more return items in a sale transaction, to decide
     * whether to call Returns Management.
     * @param transaction
     * @return true if the transaction includes items to return.
     */
    private boolean hasReturnItem(SaleReturnTransactionIfc transaction)
    {
        AbstractTransactionLineItemIfc[] saleReturnLineItems = transaction.getLineItems();        

        if ( saleReturnLineItems instanceof SaleReturnLineItemIfc[] )
        {
            for ( int i=0; i< saleReturnLineItems.length; i++ )
            {
                SaleReturnLineItemIfc saleReturnLineItem = (SaleReturnLineItemIfc)saleReturnLineItems[i];

                if ( saleReturnLineItem.getReturnItem() != null )
                {
                    return true;
                }
            }
        }
        return false;
    }
}

 
