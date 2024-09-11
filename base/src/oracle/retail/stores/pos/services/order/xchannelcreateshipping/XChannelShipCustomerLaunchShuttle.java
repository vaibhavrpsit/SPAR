/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/XChannelShipCustomerLaunchShuttle.java /main/1 2012/06/21 12:42:41 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;

// foundation imports
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.main.CustomerMainCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the customer cargo with information from the item cargo.
    $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
public class XChannelShipCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    /**
       item cargo
    **/
    protected XChannelShippingCargo shipCargo = null;

    //----------------------------------------------------------------------
    /**
        Copies information needed from parent service to child service.
        @param  bus    parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent service
    	shipCargo = (XChannelShippingCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Stores information needed by child service. Copies the access employee
        and sales associate, sets the exit when offline flag for customer.
        @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the child - customer main/customer
        CustomerMainCargo cargo = (CustomerMainCargo)bus.getCargo();

        // set access
        cargo.setOperator(shipCargo.getOperator());

        // set register
        cargo.setRegister(shipCargo.getRegister());

        // reset the database error code to UNKNOWN
        cargo.setDataExceptionErrorCode(DataException.UNKNOWN);

        // if customer db is offline, get out
        cargo.setOfflineIndicator(CustomerCargo.OFFLINE_ADD);
        cargo.setLinkDoneSwitch(CustomerCargo.LINK);

        // if transaction in progress pass over id
        if (shipCargo.getTransaction() != null)
        {
            cargo.setTransactionID(shipCargo.getTransaction().getTransactionID());
        }
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  XChannelShipCustomerLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
