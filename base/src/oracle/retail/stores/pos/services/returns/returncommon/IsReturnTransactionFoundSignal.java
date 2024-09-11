/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/IsReturnTransactionFoundSignal.java /main/1 2013/03/19 11:55:19 sgu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;

/**
 * This is a signal indicating if a return transaction has been found
 * based on the transaction or order search criteria
 * 
 * @author sgu
 *
 */
public class IsReturnTransactionFoundSignal implements TrafficLightIfc
{
    /** serialVersionUID */
    private static final long serialVersionUID = -8715397657448915887L;
    
    /** 
    The logger to which log messages will be sent.
     **/
    protected static Logger logger = Logger.getLogger(IsReturnTransactionFoundSignal.class);

    //--------------------------------------------------------------------------
    /**
   Determines whether it is safe for the bus to proceed
   <p>
   @param bus  the service bus
   @return true if a transaction is found; false otherwise
     **/
    //--------------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        return(cargo.isTransactionFound());
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
