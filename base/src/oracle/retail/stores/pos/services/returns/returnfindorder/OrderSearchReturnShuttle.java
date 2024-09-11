/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindorder/OrderSearchReturnShuttle.java /main/3 2014/04/25 09:55:47 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    sgu       04/24/14 - update logic to get returnable quantity
 *    sgu       03/13/13 - rename to order summary
 *    sgu       03/07/13 - handle multiple return orders
 *    jswan     10/25/12 - Added to support returns by order.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindorder;

import oracle.retail.stores.domain.transaction.OrderTransaction;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This shuttle updates the current service with the information
    from the Find Transaction by ID service.
    <p>
    @version $Revision: /main/3 $
**/
//--------------------------------------------------------------------------
public class OrderSearchReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5161531796358057026L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(OrderSearchReturnShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/3 $";
    /**
       Child cargo.
    **/
    protected OrderCargo orderCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from child service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        orderCargo = (OrderCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by parent service.
       <P>
       @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        cargo.setOrderSummaries(orderCargo.getOrderSummaries());
        if (orderCargo.getOrder() != null && orderCargo.getOrder().getOriginalTransaction() != null)
        {
            // TODO get partial order items (i.e. only some items have been picked up)
            OrderTransaction originalTransaction = 
                (OrderTransaction)orderCargo.getOrder().getOriginalTransaction().clone();
            originalTransaction.setTransactionType(TransactionIfc.TYPE_ORDER_PARTIAL);
            cargo.setTransactionFound(true);
            cargo.setOriginalTransaction(originalTransaction);
            cargo.setHaveReceipt(true);
            cargo.setOriginalTransactionId(originalTransaction.getTransactionIdentifier());
            cargo.moveTransactionToOriginal(originalTransaction);
        }
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ReturnFindTransReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
