/*===========================================================================
* Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/XChannelCreateShippingReturnShuttle.java /main/2 2013/03/05 14:03:17 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     02/28/13 - Handle orderLineItems for cargos.
* yiqzhao     10/11/12 - Enable shipping webstore items.
* yiqzhao     10/11/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.sale;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
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
        Logger.getLogger(oracle.retail.stores.pos.services.sale.XChannelCreateShippingReturnShuttle.class);

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /main/2 $";

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
        SaleCargo cargo = (SaleCargo) bus.getCargo();

        //set updated line items in order to display them correctly in show sale screen
        if (shippingCargo.getTransaction() != null)
        {
            cargo.setTransaction(shippingCargo.getTransaction());
        }
        if (shippingCargo.getCustomer()!=null)
        {
        	if ( cargo.getTransaction() != null )
        	{
        		cargo.getTransaction().setCustomer(shippingCargo.getCustomer());
        	}
        } 
        
        // Reset the line items
        AbstractTransactionLineItemIfc[] lineItems = cargo.getTransaction().
            getItemContainerProxy().getLineItems();
        for(int i = 0; i < lineItems.length; i++)
        {
            SaleReturnLineItemIfc srli = (SaleReturnLineItemIfc)lineItems[i];
            srli.setSelectedForItemModification(false);
        }
        
        int size = cargo.getOrderLineItems().size();
        for ( int i=0; i<size; i++ )
        {
            cargo.getOrderLineItems().remove(0);
        }
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
