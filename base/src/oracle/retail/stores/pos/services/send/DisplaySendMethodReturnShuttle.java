/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/DisplaySendMethodReturnShuttle.java /main/12 2012/04/30 15:55:31 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/30/12 - remove commented lines
 *    yiqzhao   04/26/12 - handle shipping charge as sale return line item
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 * $
 * Revision 1.3  2004/09/23 00:07:10  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.2  2004/06/21 13:13:55  lzhao
 * @scr 4670: cleanup
 *
 * Revision 1.1  2004/06/16 13:42:07  lzhao
 * @scr 4670: refactoring Send for 7.0.
 *
 * Revision 1.3  2004/06/02 19:06:51  lzhao
 * @scr 4670: add ability to delete send items, modify shipping and display shipping method.
 *
 * Revision 1.2  2004/05/28 20:12:09  lzhao
 * @scr 4670: remove unnecessary code.
 *
 * Revision 1.1  2004/05/26 16:37:47  lzhao
 * @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send;

import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.modifyitem.ItemCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;


//------------------------------------------------------------------------------
/**
 * Return shuttle class for DisplaySendMethod service.
 * <P>
 * 
 * @version $Revision: /main/12 $
 */
//------------------------------------------------------------------------------
public class DisplaySendMethodReturnShuttle implements ShuttleIfc
{ // begin class DisplaySendMethodReturnShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4783754192579837249L;

    /**
     * revision number supplied by Team Connection
     */
    public static String revisionNumber = "$Revision: /main/12 $";
    /**
     * send cargo
     */
    protected SendCargo sendCargo = null;
    
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
        sendCargo = (SendCargo) bus.getCargo();

    } // end load()

    //---------------------------------------------------------------------
    /**
     * Unload to parent (Item) cargo class.
     * <P>
     * <B>Pre-Condition</B>
     * <UL>
     * <LI>Cargo in bus is instance of ItemCargo class
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
            new String("Class:  DisplaySendMethodReturnShuttle (Revision " + getRevisionNumber() + ")" + hashCode());
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

} // end class DisplaySendMethodReturnShuttle
