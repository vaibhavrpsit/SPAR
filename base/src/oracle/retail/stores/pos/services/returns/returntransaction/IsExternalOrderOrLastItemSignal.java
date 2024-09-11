/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returntransaction/IsExternalOrderOrLastItemSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     07/14/10 - Added for undo processing of external orders.
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returntransaction;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;

//--------------------------------------------------------------------------
/**
    This signal returns true if the return is for a external order or if 
    this is the last item in the list of items to return.
**/
//--------------------------------------------------------------------------
public class IsExternalOrderOrLastItemSignal
extends IsThereNotAnotherItemSignal
implements TrafficLightIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2428936276198906828L;
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(IsExternalOrderOrLastItemSignal.class);

    //--------------------------------------------------------------------------
    /**
        This signal returns true if the return is for a external order or if 
        this is the last item in the list of items to return.
        <p>
        @param bus  the service bus
        @return true if a customer is available; false otherwise
    **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean retValue = false;
        ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();
        if (cargo.isExternalOrder())
        {
            retValue = true;
        }
        else
        if (super.roadClear(bus))
        {
            retValue = true;
        }
        
        return retValue;
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
