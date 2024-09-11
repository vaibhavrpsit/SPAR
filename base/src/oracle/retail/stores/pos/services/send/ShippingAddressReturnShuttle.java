/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/ShippingAddressReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:04 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:17 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:10  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/05/05 16:17:51  rsachdeva
 *   @scr 4670 Send: Multiple Sends
 *
 *   Revision 1.2  2004/05/04 20:48:20  rsachdeva
 *   @scr 4670 Send: Pre-Tender Multiple Sends
 *
 *   Revision 1.1  2004/05/04 20:44:39  rsachdeva
 *   @scr 4670 Send: Pre-Tender Multiple Sends
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;
//--------------------------------------------------------------------------
/**
    This shuttle copies information from the cargo used
    in the shippingAddress service to the cargo used 
    in the modify item send service. <p>
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ShippingAddressReturnShuttle implements ShuttleIfc
{                                       // begin class ShippingAddressReturnShuttle()
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -995613087414835708L;

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       send cargo
    **/
    protected SendCargo sendCargo = null;
    //----------------------------------------------------------------------
    /**
       Loads cargo from shipping address service. <P>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        sendCargo = (SendCargo) bus.getCargo();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads data into modify item send service. <P>
       @param  bus  Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        ItemCargo cargo = (ItemCargo) bus.getCargo();
        cargo.setTransaction(sendCargo.getTransaction()); 
        cargo.setItems(sendCargo.getLineItems());
    }                                   // end unload()

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
        String strResult = new String("Class:  ShippingAddressReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }

}                                       // end class ShippingAddressReturnShuttle
