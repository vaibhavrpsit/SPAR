/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/UpdateSendActionRoad.java /main/13 2012/04/30 15:55:31 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/26/12 - handle shipping charge as sale return line item
 *    npoola    08/06/10 - removed the code to mail the Continue letter since
 *                         its ROAD
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         7/21/2006 4:14:14 PM   Brendan W. Farrell
 *         Merge from v7.x.  Use ifc so that it is extendable.
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:36 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:26 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/23 16:16:32  lzhao
 *   @scr 6413: update returns to ship to address directly.
 *
 *   Revision 1.1  2004/06/22 17:28:10  lzhao
 *   @scr 4670: code review
 *
 *   Revision 1.1  2004/06/14 23:35:26  lzhao
 *   @scr 4670: fix shipping charge calculation.
 *
 *   Revision 1.1  2004/06/04 20:23:44  lzhao
 *   @scr 4670: add Change send functionality.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import java.util.Vector;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;

/**
 * This site is called from clicking on Update button from 
 * SendAlreadyApplied Dialog.
 * $Revision: /main/13 $
 */
public class UpdateSendActionRoad extends PosLaneActionAdapter
{

    /**
     revision number of this class
     **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    
    public void traverse(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo)bus.getCargo();
        
        // Some of the items are send items. Update the send
        cargo.setItemUpdate(true);
        
        // Get Send index
        SaleReturnLineItemIfc[] items = cargo.getItems();
        int sendLabelIndex = 0;
        for ( int i = 0; i < items.length; i++ )
        {
            if( items[i].getItemSendFlag() )
            {
                sendLabelIndex = items[i].getSendLabelCount();
                break;
            }
        }
        
        // find the items which are not send items. Make these items belong
        // to the send.
        if ( sendLabelIndex > 0 )
        {
            for ( int i = 0; i < items.length; i++ )
            {
                if( !items[i].getItemSendFlag() )
                {
                    items[i].setSendLabelCount(sendLabelIndex);
                    items[i].setItemSendFlag(true);
                }
            }
        }
        
        // add the items are in the same send which are not selected into the cargo.
        // Because the shipping charge will be based on all the items in the send;
        Vector missedItems = new Vector();
        SaleReturnTransaction transaction = (SaleReturnTransaction)cargo.getTransaction();
        AbstractTransactionLineItemIfc[] allItems = transaction.getLineItems();
        for ( int i = 0; i < allItems.length; i++ )
        {
            SaleReturnLineItemIfc item = (SaleReturnLineItemIfc)allItems[i];
            //shipping charge item is not send item, exclude shipping charge item
            if ( (item.getSendLabelCount() == sendLabelIndex) && !item.isShippingCharge() )
            {
                boolean foundInSelection = false;
                for ( int j = 0; j < items.length; j++ )
                {
                    if ( item.getItemID() == items[j].getItemID() )
                    {
                        foundInSelection = true;
                        break;
                    }
                }
                if ( !foundInSelection )
                {
                    missedItems.add(item);
                }
            }                 
        }
        if ( missedItems.size() != 0 )
        {
            SaleReturnLineItemIfc[] sendItems = new SaleReturnLineItemIfc[missedItems.size()+items.length];
            System.arraycopy(items, 0, sendItems, 0, items.length);
            for ( int i = items.length, j=0; j < missedItems.size(); i++, j++ )
            {
                sendItems[i] = (SaleReturnLineItemIfc)missedItems.elementAt(j);
            }
            cargo.setItems(sendItems);
        }
                
        if ( !transaction.isSendCustomerLinked() )
        {    
            cargo.setCustomer( transaction.getCaptureCustomer() );
        }
        else
        {
            cargo.setCustomer( transaction.getCustomer() );
        }                
    }
}
