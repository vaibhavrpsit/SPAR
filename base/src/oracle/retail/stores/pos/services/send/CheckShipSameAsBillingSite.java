/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/CheckShipSameAsBillingSite.java /main/12 2012/04/06 09:52:07 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         5/1/2007 12:15:40 PM   Brett J. Larsen CR
 *         26474 - Tax Engine Enhancements for Shipping Carge Tax (for VAT
 *         feature)
 *         
 *    3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/03 20:10:48  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.6  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.5  2004/06/21 13:13:55  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.4  2004/06/19 14:06:40  lzhao
 *   @scr 4670: Integrate with capture customer
 *
 *   Revision 1.3  2004/06/04 20:23:44  lzhao
 *   @scr 4670: add Change send functionality.
 *
 *   Revision 1.2  2004/06/02 19:06:51  lzhao
 *   @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 *   Revision 1.1  2004/05/26 16:37:47  lzhao
 *   @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;


import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
//--------------------------------------------------------------------------
/**
  .
   $Revision: /main/12 $
**/
//--------------------------------------------------------------------------
public class CheckShipSameAsBillingSite extends PosSiteActionAdapter
{
    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/12 $";
    /**
     info dialog ship address same as billing address resource id
     **/
    public static final String INFO_SAME_AS_BILLING = "SameAsBilling";

    //----------------------------------------------------------------------
    /**
        Ask if shipping address is same as billing address<P>
        @param  bus  Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        int[] buttons = new int[] { DialogScreensIfc.BUTTON_YES,
                                    DialogScreensIfc.BUTTON_NO };
        String[] letters = new String[] { CommonLetterIfc.YES, CommonLetterIfc.NO };

        UIUtilities.setDialogModel(ui,
                                   DialogScreensIfc.CONFIRMATION,
                                   INFO_SAME_AS_BILLING,
                                   null,
                                   buttons,
                                   letters);
    }

    //----------------------------------------------------------------------
    /**
     Receive user's selection, set customer to cargo and shipping address<P>
     @param  bus  Service Bus
     **/
    //----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo) bus.getCargo();
        SaleReturnTransaction transaction = (SaleReturnTransaction)cargo.getTransaction();
        if ( cargo.isItemUpdate() )
        {
            if ( !transaction.isSendCustomerLinked() )
            {
                cargo.setCustomer( transaction.getCaptureCustomer() );
            }
            else
            {
                cargo.setCustomer( transaction.getCustomer() );
            }

            if ( bus.getCurrentLetter().getName().equals(CommonLetterIfc.YES) )
            {
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

                ShippingMethodIfc shippingMethod = transaction.getSendPackages()[sendLabelIndex-1].getShippingMethod();
                transaction.updateSendPackageInfo(sendLabelIndex-1, shippingMethod, cargo.getCustomer());
            }
        }
    }
}
