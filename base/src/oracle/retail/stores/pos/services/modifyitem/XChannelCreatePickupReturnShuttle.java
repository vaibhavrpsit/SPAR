/* ===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/XChannelCreatePickupReturnShuttle.java /main/2 2013/06/04 16:02:41 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/04/13 - Fix to update item attributes on undo action of
 *                         xchannel pickup
 *    jswan     05/01/12 - Added to support the cross channel feature create
 *                         pickup order.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifyitem;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.xchannelcreatepickup.XChannelCreatePickupOrderCargo;

/**
 *  This class shuttles data back to the Modify Item tour from the 
 *  x channel create pickup order tour. 
 */
public class XChannelCreatePickupReturnShuttle extends FinancialCargoShuttle
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 4468433671907736129L;
    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/2 $";
    /**
        pickup delivery cargo
    **/
    protected XChannelCreatePickupOrderCargo createOrderCargo = null;

    //----------------------------------------------------------------------
    /**
        load pickup delivery order cargo.
        <P>
        @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        createOrderCargo = (XChannelCreatePickupOrderCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Copies the pickup delivery order info to the cargo for the Modify Item service.
        <P>
        @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        ItemCargo cargo = (ItemCargo) bus.getCargo();
        if (createOrderCargo.getOrderTransaction() != null)
        {
            cargo.setTransaction(createOrderCargo.getTransaction());
        }
        cargo.setPickupOrDeliveryExecuted(createOrderCargo.isXchannelCreatePickupExecuted());
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
        String strResult = new String("Class:  InquiryOptionsReturnShuttle (Revision " +
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
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()
}
