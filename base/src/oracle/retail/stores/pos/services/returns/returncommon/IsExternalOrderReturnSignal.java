/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/IsExternalOrderReturnSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/13/10 - Added signal for External Order return.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This determines whether a customer is available.
**/
//--------------------------------------------------------------------------
public class IsExternalOrderReturnSignal implements TrafficLightIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2428936276198906828L;
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(IsExternalOrderReturnSignal.class);

    //--------------------------------------------------------------------------
    /**
       Determines whether it is safe for the bus to proceed
       <p>
       @param bus  the service bus
       @return true if a customer is available; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        ReturnExternalOrderItemsCargoIfc cargo = (ReturnExternalOrderItemsCargoIfc)bus.getCargo();
        return(cargo.isExternalOrder());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  " + getClass().getName() + " (Revision " +
                                      ")" + hashCode());
        return(strResult);
    }
}
