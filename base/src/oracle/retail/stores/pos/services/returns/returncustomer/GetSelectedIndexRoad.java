/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncustomer/GetSelectedIndexRoad.java /main/11 2012/10/29 12:55:23 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Modified to support returns by order.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:54   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:28:28   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:24:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:36   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncustomer;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
    The system traverse this road to get the index of the transaction
    selected by the operator.
    <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class GetSelectedIndexRoad extends LaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";

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
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel model = (ListBeanModel)ui.getModel(POSUIManagerIfc.RETURN_LINKED_TRANS);

        int selected = model.getSelectedRow();

        // Update the cargo
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        cargo.setSelectedIndex(selected);
        cargo.setSelectedTransactionOrderID(cargo.getTransactionSummary()[selected].getInternalOrderID());
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
        String strResult = new String("Class:  GetSelectedIndexRoad (Revision " +
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
