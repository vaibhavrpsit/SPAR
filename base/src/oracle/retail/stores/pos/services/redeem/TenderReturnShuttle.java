/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/TenderReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;


// foundation imports
import java.util.HashMap;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
    Copies the information needed by the Redeem service
    from the cargo of the Tender service.  This is an ADO service transferring
    to another ADO service.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -447553731274097348L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.postvoid.TenderReturnShuttle.class);
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       void transaction
    **/
    protected RetailTransactionADOIfc transaction;
    /**
     * tender attributes
     */
    protected HashMap tenderAttributes;

    //----------------------------------------------------------------------
    /**
       Load tender data into shuttle.

       @param  bus     Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        transaction = cargo.getCurrentTransactionADO();
    }

    //----------------------------------------------------------------------
    /**
       Unload tender data into calling service cargo.
       

       @param  bus     Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        RedeemCargo redeemCargo = (RedeemCargo) bus.getCargo();        
        redeemCargo.setCurrentTransactionADO(transaction);
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
        String strResult = new String("Class:  TenderReturnShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

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
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
