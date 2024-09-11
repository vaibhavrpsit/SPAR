/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	12/Aug/2013	 Animesh, Initial Draft:CR Supressing New Special order Receipt
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.services.order.print;

import max.retail.stores.domain.lineitem.MAXOrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.OrderLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.service.SessionBusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.receipt.OrderReceiptParameterBeanIfc;
import oracle.retail.stores.pos.receipt.PrintableDocumentException;
import oracle.retail.stores.pos.receipt.PrintableDocumentManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.order.common.OrderCargoIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//------------------------------------------------------------------------------
/**
    Prints the Order.

    @version $Revision: 9$
**/
//------------------------------------------------------------------------------
public class MAXPrintOrderSite extends PosSiteActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -4350668001956231672L;
	public static final String SITENAME = "PrintOrderSite";
    //--------------------------------------------------------------------------
    /**
       Print the Order and update the order and line item status if applicable.
       <p>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        Letter              letter  = null;
        OrderCargo          cargo   = (OrderCargo) bus.getCargo();
        POSUIManagerIfc     ui      = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        PrintableDocumentManagerIfc printableDocumentManager =
            (PrintableDocumentManagerIfc)bus.getManager(PrintableDocumentManagerIfc.TYPE);
        String[]            footer  = null;

        //Footer for order printing contains a description of the action
        //being taken on the order which is printed immediately after the bar code
        if (cargo.getServiceType() == OrderCargoIfc.SERVICE_TYPE_NOT_SET ||  //serviceName is null when coming from service alert
            cargo.getServiceType() == OrderCargoIfc.SERVICE_PRINT_TYPE)
        {
            footer = new String[] {"PRINT ORDER"};
        }
        else if(cargo.getServiceType() == OrderCargoIfc.SERVICE_VIEW_TYPE)
        {
            footer = new String[] {"VIEW ORDER"};
        }
        else if(cargo.getServiceType() == OrderCargoIfc.SERVICE_FILL_TYPE)
        {
            footer = new String[] {"FILL ORDER"};
        }
        else if(cargo.getServiceType() == OrderCargoIfc.SERVICE_CANCEL_TYPE)
        {
            footer = new String[] {"ORDER CANCELLATION"};
        }

        POSBaseBeanModel model = new POSBaseBeanModel();
		if(!(cargo.getServiceType() == OrderCargoIfc.SERVICE_FILL_TYPE)) //Rev 1.0 change
        ui.showScreen(POSUIManagerIfc.ORDER_PRINTING, model);
        ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.ONLINE);
        ui.customerNameChanged(" ");
        //set the EmployeeIfc for this Order to currently logged in operator
        cargo.getOrder().setSalesAssociate(cargo.getOperator());

        try
        {
            
            OrderReceiptParameterBeanIfc orderReceiptParameter =
                printableDocumentManager.getOrderReceiptParameterBeanInstance((SessionBusIfc)bus, cargo.getOrder());
          //Changes done for code merging(commenting below lines for error resolving)
            //orderReceiptParameter.setFooter(footer);
            if(!(cargo.getServiceType() == OrderCargoIfc.SERVICE_FILL_TYPE)) //Rev 1.0 change
            printableDocumentManager.printOrderReceipt((SessionBusIfc)bus, orderReceiptParameter);

            updateStatus(cargo.getOrder());
            letter = new Letter("Success");
        }
        catch (PrintableDocumentException e)
        {
             System.out.println(e.getMessage());
             logger.warn("Unable to print receipt. " + e.getMessage());

            if (e.getNestedException()!=null)
            {
                logger.warn("PrintableDocumentException.NestedException:  " + Util.throwableToString(e.getNestedException()));
            }
            ui.statusChanged(POSUIManagerIfc.PRINTER_STATUS, POSUIManagerIfc.OFFLINE);

            letter = new Letter("Error");
        }
        catch (ParameterException pe)
        {
            logger.error("ParameterException caught in " + SITENAME + ".arrive()", pe);
        }

        bus.mail(letter,BusIfc.CURRENT);
    }
    
    /**
     * This method is used to set the status of OrderLineItem and Order
     * @param order OrderIfc
     */
    protected void updateStatus(OrderIfc order)
    {
        AbstractTransactionLineItemIfc[] orderItems = order.getLineItems();
        int newItemCnt = 0; // count of new items
        for (int i = 0; i < orderItems.length; i++)
        {
            int itemStatus = ((SaleReturnLineItemIfc)orderItems[i]).getOrderItemStatus().getStatus().getStatus();
            
            // Set status for those items that have changed
          //Changes done for code merging(commenting below lines for error resolving)
            /*if (itemStatus == MAXOrderLineItemIfc.ORDER_ITEM_STATUS_NEW)
            {
                ((SaleReturnLineItemIfc)orderItems[i]).getOrderItemStatus().getStatus().changeStatus(MAXOrderLineItemIfc.ORDER_ITEM_STATUS_PENDING);
                newItemCnt++;
            }*/
        }
        if (newItemCnt == orderItems.length)
        {
            order.getStatus().getStatus().changeStatus(OrderConstantsIfc.ORDER_STATUS_PRINTED);
        }
    }
}

