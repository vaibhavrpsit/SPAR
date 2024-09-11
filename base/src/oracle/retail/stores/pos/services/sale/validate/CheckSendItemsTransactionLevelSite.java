/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/validate/CheckSendItemsTransactionLevelSite.java /main/12 2012/04/17 13:33:53 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/08/10 15:26:21  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.validate;


import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    This site checks if the transaction has alteration item(s).
    @version $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CheckSendItemsTransactionLevelSite extends PosSiteActionAdapter
{
    /**
        Revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";
    /**
        Letter send items
    **/
    public static final String SEND_ITEMS = "SendItems";
    /**
        Letter continue
    **/
    public static final String CONTINUE = "Continue";
    
    //----------------------------------------------------------------------
    /**
        Check if send items are there for send level as transaction
        <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        //Default the letter value to Continue
        String letter = CONTINUE;
        
        boolean transactionLevelSend = false;
        
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();
        
        SaleReturnTransactionIfc  transaction = cargo.getTransaction();
        
        AbstractTransactionLineItemIfc[] lineItems = null;        
        
        if (transaction != null && transaction.isTransactionLevelSendAssigned())
        {
            lineItems = transaction.getSendItemBasedOnIndex(1);
            
            if (lineItems != null  && 
                    lineItems.length > 0)
            {
                letter = SEND_ITEMS;                                
            }                         
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT); 
    }
        
}
