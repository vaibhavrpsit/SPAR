/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifyitem/XChannelCreateShippingReturnShuttle.java /main/3 2012/09/17 16:12:51 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     09/17/12 - set linked customer into transaction.
* sgu         06/27/12 - set item disposition code for ship to store item
* sgu         06/27/12 - modify ship to store for xc
* yiqzhao     06/05/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.modifyitem;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.order.xchannelcreateshipping.XChannelShippingCargo;

public class XChannelCreateShippingReturnShuttle extends FinancialCargoShuttle implements ShuttleIfc
{                                       // begin class XChannelCreateShippingReturnShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.modifyitem.XChannelCreateShippingReturnShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /main/3 $";

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
        ItemCargo cargo = (ItemCargo) bus.getCargo();

        //set updated line items in order to display them correctly in show sale screen
        if (shippingCargo.getTransaction() != null)
        {
            cargo.setTransaction(shippingCargo.getTransaction());
        }
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

}                                       // end class XChannelCreateShippingReturnShuttle
