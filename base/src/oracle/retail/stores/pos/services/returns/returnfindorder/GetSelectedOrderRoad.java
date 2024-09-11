/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindorder/GetSelectedOrderRoad.java /main/1 2013/03/19 11:55:20 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindorder;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

/**
 * This aisle is traversed when the user presses the accept key from 
 * the RETURN_LINKED_TRANS screen.
 * 
 * @author sgu
 *
 */
public class GetSelectedOrderRoad extends LaneActionAdapter
{
    /**
     * 
     */
    private static final long serialVersionUID = 2058961523165649630L;

    /**
    revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /main/1 $";

 //----------------------------------------------------------------------
 /**
    Gets the selected transacation index from the ui.
    <P>
    @param  bus     Service Bus
 **/
 //----------------------------------------------------------------------
 public void traverse(BusIfc bus)
 {

     // Get the index of the selected item
     POSUIManagerIfc ui;
     ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
     ListBeanModel model = (ListBeanModel)ui.getModel(POSUIManagerIfc.RETURN_LINKED_TRANS);

     int selected = model.getSelectedRow();

     // Update the cargo
     ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
     String orderId = cargo.getOrderSummaryFromCollection(selected).getOrderID();
     cargo.setSelectedTransactionOrderID(orderId);
 }

 //----------------------------------------------------------------------
 /**
    Returns a string representation of the object.
    <P>
    @return String representation of object
 **/
 //----------------------------------------------------------------------
 public String toString()
 {                                   // begin toString()
     // result string
     String strResult = new String("Class:  GetSelectedTransactionRoad (Revision " +
                                   getRevisionNumber() +
                                   ")" + hashCode());
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
