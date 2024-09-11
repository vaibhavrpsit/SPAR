/* ===========================================================================
* Copyright (c) 2010, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/AbstractReturnLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    jswan     05/13/10 - Add abstract launch shuttle.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This shuttle updates the child cargo (ReturnTransactionCargo) with
    information from the parent cargo (ReturnCustomerCargo).
**/
//--------------------------------------------------------------------------
public abstract class AbstractReturnLaunchShuttle extends FinancialCargoShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8547062860427485210L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returncustomer.ReturnTransactionLaunchShuttle.class);

    /**
       External Order Items Cargo from the load method.
    **/
    ReturnExternalOrderItemsCargoIfc lCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from the parent service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // Perform FinancialCargoShuttle load
        super.load(bus);

        // retrieve cargo from the parent service
        lCargo = (ReturnExternalOrderItemsCargoIfc)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by child service.
       <P>
       @param  bus   Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // Perform FinancialCargoShuttle unload
        super.unload(bus);

        // retrieve cargo from the child
        ReturnExternalOrderItemsCargoIfc uCargo = (ReturnExternalOrderItemsCargoIfc)bus.getCargo();
        // set the return transaction id in the options cargo

        if (lCargo.isExternalOrder())
        {
            uCargo.setExternalOrder(true);
            uCargo.setExternalOrderItemReturnStatusElements(lCargo.getExternalOrderItemReturnStatusElements());
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
        String strResult = new String("Class:  ReturnTransactionLaunchShuttle (Revision " +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()

}
