/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
  Copyright (c) 2012-2013 MAXHyperMarket, Inc.    All Rights Reserved.
  Rev 1.0	1/May/2013	  	Tanmaya, Home Delivery Special Order
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package max.retail.stores.pos.services.order;

import max.retail.stores.pos.services.order.common.MAXOrderCargo;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//------------------------------------------------------------------------------
/**
    Sets the order service name to View and view order flag to false.
    <P>
    @version $Revision: 3$
**/
//------------------------------------------------------------------------------

public class MAXAlterEnteredRoad extends LaneActionAdapter
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 4544850398311292922L;

	/**
       class name constant
    **/
    public static final String LANENAME = "MAXAlterEnteredRoad";

    /**
       revision number for this class
    **/
    public static final String revisionNumber = "$Revision: 3$";

    /**
        Service View bundle tag
    **/
    public static final String SERVICE_VIEW_TAG = "ServiceView";
    
    //------------------------------------------------------------------------------
    /**
       Sets the order service name to View and view order flag to false.
       <P>
       @param bus the bus arriving at this site
    **/
    //------------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {
        MAXOrderCargo cargo = (MAXOrderCargo) bus.getCargo();
        cargo.setServiceType(5);
        //cargo.setViewOrder(false);
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
        String strResult = new String("Class: ViewEnteredRoad  (Revision " +
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
