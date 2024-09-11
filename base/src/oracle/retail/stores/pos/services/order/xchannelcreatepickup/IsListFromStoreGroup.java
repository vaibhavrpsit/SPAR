/* ===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreatepickup/IsListFromStoreGroup.java /main/1 2012/05/02 14:07:50 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.order.xchannelcreatepickup;

// foundation imports
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal determines if the list of stores for the available to promise inventory
    comes from the store group.
    <P>
    $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class IsListFromStoreGroup implements TrafficLightIfc
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    /**
        Signal name for toString
    **/
    public static final String SIGNALNAME = "IsListFromStoreGroup";

    //----------------------------------------------------------------------
    /**
        This method determines if if the list of stores for the available to 
        promise inventory comes from the store group.
        <p>
        @param bus the bus trying to proceed
        @return true if Customer add find ; false otherwise
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        XChannelCreatePickupOrderCargo cargo = (XChannelCreatePickupOrderCargo)bus.getCargo();
        return cargo.isListFromStoreGroup(); 

    }

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

    //----------------------------------------------------------------------
    /**
        Returns a string representation of the object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        String strResult = new String("Class:  " + SIGNALNAME + " (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        return(strResult);
    }                                   // end toString()
}
