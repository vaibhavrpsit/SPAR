/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/address/DisplaySendMethodLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:49 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:40 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/09/01 13:53:31  rsachdeva
 *   @scr 6791 Transaction Level Send Javadoc
 *
 *   Revision 1.5  2004/08/27 14:41:48  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.4  2004/08/09 19:27:05  rsachdeva
 *   @scr 6791 Send Level In Progress
 *
 *   Revision 1.3  2004/06/21 13:16:07  lzhao
 *   @scr 4670: cleanup
 *
 *   Revision 1.2  2004/06/19 14:06:14  lzhao
 *   @scr 4670: integrate with capture customer
 *
 *   Revision 1.1  2004/06/16 13:42:07  lzhao
 *   @scr 4670: refactoring Send for 7.0.
 *
 *   Revision 1.3  2004/06/11 19:10:35  lzhao
 *   @scr 4670: add customer present feature
 *
 *   Revision 1.2  2004/06/04 20:23:44  lzhao
 *   @scr 4670: add Change send functionality.
 *
 *   Revision 1.1  2004/05/26 16:37:47  lzhao
 *   @scr 4670: add capture customer and bill addr. same as shipping for send
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.send.address;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//------------------------------------------------------------------------------
/**
    Launch shuttle class for DisplaySendMethod service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DisplaySendMethodLaunchShuttle implements ShuttleIfc
{                                       // begin class DisplaySendMethodLaunchShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5777535014937152741L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.send.address.DisplaySendMethodLaunchShuttle.class);


    /**
       revision number supplied by Team Connection
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Cargo from the Item service.
    **/
    protected SendCargo sendCargo = null;
    /**
       send level in progress
    **/
    protected boolean sendLevelInProgress = false;

    //---------------------------------------------------------------------
    /**
       Load parent (Send) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of SendCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo loaded
       </UL>
       @param bus service bus interface
    **/
    //---------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        // log entry
        // retrieve cargo
        sendCargo = (SendCargo) bus.getCargo();
        sendLevelInProgress = sendCargo.isTransactionLevelSendInProgress();

        // log exit
    }                                   // end load()

    //---------------------------------------------------------------------
    /**
       Unload to child (Send) cargo class. <P>
       <B>Pre-Condition</B>
       <UL>
       <LI>Cargo in bus is instance of SnedCargo class
       </UL>
       <B>Post-Condition</B>
       <UL>
       <LI>Cargo unloaded
       </UL>
       @param bus  service bus interface
    **/
    //---------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        // retrieve cargo
        SendCargo cargo = (SendCargo) bus.getCargo();
        cargo.setTransactionLevelSendInProgress(sendLevelInProgress);        
        cargo.setTransaction(sendCargo.getTransaction());
        cargo.setLineItems(sendCargo.getLineItems());
        cargo.setShipToInfo(sendCargo.getShipToInfo());
        cargo.setShippingMethod(sendCargo.getShippingMethod());
        cargo.setItemUpdate(sendCargo.isItemUpdate());
        cargo.setSendIndex(sendCargo.getSendIndex());
        cargo.setPartialShippingCharges(sendCargo.getPartialShippingCharges());
        cargo.setParameter(sendCargo.getParameter());
        cargo.setOperator(sendCargo.getOperator());
        cargo.setStoreStatus(sendCargo.getStoreStatus());
        cargo.setCustomer(sendCargo.getCustomer());
    }                                   // end unload()

    //---------------------------------------------------------------------
    /**
       Method to default display string function. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  DisplaySendMethodLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" +
                                      hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class DisplaySendMethodLaunchShuttle

