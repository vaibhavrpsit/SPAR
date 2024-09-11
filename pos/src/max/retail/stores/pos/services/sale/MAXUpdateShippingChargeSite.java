/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2016-2017 Max Hypermarket.    All Rights Reserved. 
 * 
 * Rev 1.0   May 04, 2017	        Kritica Agarwal  GST Changes
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.services.sale;


import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.sale.SaleCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;

//--------------------------------------------------------------------------
/**
    This site begins the Sale Package.
    <p>
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class MAXUpdateShippingChargeSite extends PosSiteActionAdapter
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
                //Change for Rev 1.0 : Starts
                //Get shipping charge line item which needs to be removed
               /* SaleReturnLineItemIfc shippingChargeLineIteme = transaction.getShippingChargeLineItem(sendIndex);
                
                transaction.removeLineItem(shippingChargeLineIteme.getLineNumber());
                //reindex all selected items if shippingChangeItem line number is smaller than their indics 
                for ( int i=0; i<allSelected.length; i++ )
                {
                	if ( allSelected[i]>shippingChargeLineIteme.getLineNumber() )
                		allSelected[i]--;
                }
                beanModel.setRowsToDelete(allSelected);
*/
              //Change for Rev 1.0 : Ends
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
