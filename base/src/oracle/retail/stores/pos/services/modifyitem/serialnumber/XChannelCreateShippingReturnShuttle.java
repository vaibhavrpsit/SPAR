/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/serialnumber/XChannelCreateShippingReturnShuttle.java /main/1 2013/01/02 11:55:34 sgu Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* sgu         12/27/12 - set presplit index and item selected flags
* sgu         12/26/12 - create shuttles for create xc shipping item
* sgu         12/26/12 - add new shuttle
* sgu         12/26/12 - add new shuttle
* sgu         12/26/12 - Creation
* ===========================================================================
*/
package oracle.retail.stores.pos.services.modifyitem.serialnumber;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.xchannelcreateshipping.XChannelShippingCargo;

public class XChannelCreateShippingReturnShuttle extends FinancialCargoShuttle
{
    /**
     * Serial ID
     */
    private static final long serialVersionUID = -3029725451741916192L;

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /main/1 $";

    /**
     * send cargo
     */
    protected XChannelShippingCargo shippingCargo = null;

    //---------------------------------------------------------------------
    /**
     * Load from child (Send) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of SendCargo class
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>Cargo loaded
     * </UL>
     *
     * @param bus
     *            bus interface
     */
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // begin load()

        // retrieve cargo
        shippingCargo = (XChannelShippingCargo) bus.getCargo();

    } // end load()

    //---------------------------------------------------------------------
    /**
     * Unload to parent (Send) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of SendCargo class
     * </UL>
     * <B>Post-Condition</B>
     * <UL>
     * <LI>Cargo unloaded
     * </UL>
     *
     * @param bus
     *            bus interface
     */
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // begin unload()

        // retrieve cargo
        SerializedItemCargo cargo = (SerializedItemCargo) bus.getCargo();

        //set updated line items in order to display them correctly in show sale screen
        if (shippingCargo.getTransaction() != null)
        {
            cargo.setTransaction(shippingCargo.getTransaction());
        }
        
        // set customer
        if (shippingCargo.getCustomer()!=null)
        {
            cargo.setCustomer(shippingCargo.getCustomer());
            if ( cargo.getTransaction() != null )
            {
                cargo.getTransaction().setCustomer(shippingCargo.getCustomer());
            }
        } 
        
        cargo.setPickupOrDeliveryExecuted(true);
    } // end unload()

    //---------------------------------------------------------------------
    /**
     * Method to default display string function.
     * <P>
     *
     * @return String representation of object
     */
    //---------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String("Class:  XChannelCreateShippingReturnShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
        // pass back result
        return (strResult);
    } // end toString()

    //---------------------------------------------------------------------
    /**
     * Retrieves the Team Connection revision number.
     * <P>
     *
     * @return String representation of revision number
     */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()
}