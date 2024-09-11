/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/UpdateShippingChargeSite.java /main/13 2012/06/29 11:53:42 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/29/12 - Add dialog for deleting ship item, disable change
 *                         price for ship item
 *    yiqzhao   04/30/12 - move getShippingChargeLineItem to
 *                         SaleReturnLineItemIfc
 *    yiqzhao   04/26/12 - handle shipping charge as sale return line item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         9/12/2007 1:31:58 PM   Brett J. Larsen CR
 *         28691 - POS client crashing when send item is deleted - index out
 *         of bounds - index is decremented twice - once inside arrive method
 *         and again in removeSendPackage
 *    4    360Commerce 1.3         5/1/2007 12:15:40 PM   Brett J. Larsen CR
 *         26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *         feature)
 *         
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/09 21:23:58  rsachdeva
 *   @scr 6791 Removed variables since these were not being used in the site
 *
 *   Revision 1.4  2004/06/22 17:28:10  lzhao
 *   @scr 4670: code review
 *
 *   Revision 1.3  2004/06/21 13:12:19  lzhao
 *   @scr 4670: remove deprecated method call
 *
 *   Revision 1.2  2004/06/03 13:29:21  lzhao
 *   @scr 4670: delete send item.
 *
 *   Revision 1.1  2004/06/02 19:06:51  lzhao
 *   @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale;


import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
    This site begins the Sale Package.
    <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class UpdateShippingChargeSite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
     * delete letter
     */
    protected static final String DELETE = "Delete";
    /**
     * change shipping method letter
     */
    protected static final String CHANGE_SHIPPING_METHOD = "ChangeShippingMethod";
    //----------------------------------------------------------------------
    /**
       Check if running as POS
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        SaleCargoIfc cargo = (SaleCargoIfc)bus.getCargo();

        SaleReturnTransactionIfc transaction = cargo.getTransaction();

        LineItemsModel beanModel   = (LineItemsModel)ui.getModel(POSUIManagerIfc.SELL_ITEM);
        int[]          allSelected = beanModel.getRowsToDelete();

        int sendIndex = transaction.getSendIndexFromSelectedItems(allSelected);

        if ( sendIndex > 0 )
        {
            // It is a send item.
            boolean isAllItemInSendDeleted = transaction.isAllItemInSendDeleted(allSelected, sendIndex);

            if ( isAllItemInSendDeleted )
            {
                // Remove shipping charge if there is no in the send
                transaction.removeSendPackage(sendIndex);
                
                //Get shipping charge line item which needs to be removed
                SaleReturnLineItemIfc shippingChargeLineIteme = transaction.getShippingChargeLineItem(sendIndex);
                
                transaction.removeLineItem(shippingChargeLineIteme.getLineNumber());
                //reindex all selected items if shippingChangeItem line number is smaller than their indics 
                for ( int i=0; i<allSelected.length; i++ )
                {
                	if ( allSelected[i]>shippingChargeLineIteme.getLineNumber() )
                		allSelected[i]--;
                }
                beanModel.setRowsToDelete(allSelected);

                SaleReturnLineItemIfc[] lineItems = (SaleReturnLineItemIfc[])transaction.getLineItems();
                for ( int i = 0; i < lineItems.length; i++ )
                {
                    //update send index for the sends have the index greater
                    //the one in removed send
                    if ( lineItems[i].getSendLabelCount() > sendIndex )
                    {
                        lineItems[i].setSendLabelCount(lineItems[i].getSendLabelCount()-1);
                    }
                }
                bus.mail(DELETE, BusIfc.CURRENT);
            }
            else
            {
                // still have some items in the send. Customer may want to change
                // shipping method.
                cargo.setSendIndex(sendIndex);
                bus.mail(CHANGE_SHIPPING_METHOD, BusIfc.CURRENT);
            }
         }
        else
        {
            bus.mail(DELETE, BusIfc.CURRENT);
        }
        
     }
}
