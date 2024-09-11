/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/common/SetSummaryRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:34 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:57 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:12 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:45  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:13:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:41:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Feb 2002 18:14:34   baa
 * more ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.0   Sep 24 2001 13:00:14   MPM
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.common;

//foundation imports
import oracle.retail.stores.domain.order.OrderSummaryEntryIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//------------------------------------------------------------------------------
/**
    Retrieves the selected order summary from the ui and sets the selected
    order summary in cargo for future use.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class SetSummaryRoad extends LaneActionAdapter
{
    /**
       class name constant
    **/
    public static final String LANENAME = "SetSummaryRoad";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //------------------------------------------------------------------------------
    /**
       Retrieves the selected order summary from the ui and sets the selected
       order summary in cargo for future use.
       <P>
       @param bus the bus arriving at this road
    **/
    //------------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {

        //retrieve the selected order from the model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ListBeanModel model = (ListBeanModel)ui.getModel(POSUIManagerIfc.ORDER_LIST);

        //set the cargo's selected summary with the value from the ui model
        ((OrderCargo)bus.getCargo()).setSelectedSummary((OrderSummaryEntryIfc)model.getSelectedValue());

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
        String strResult = new String("Class: SetSummaryRoad  (Revision " +
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
