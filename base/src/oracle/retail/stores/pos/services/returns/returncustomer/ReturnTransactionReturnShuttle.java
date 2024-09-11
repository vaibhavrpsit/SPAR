/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncustomer/ReturnTransactionReturnShuttle.java /main/13 2013/07/17 15:36:16 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     07/17/13 - Setting the originalTransactionID for cargo
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/03/22 22:39:47  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.4  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.3  2004/02/12 16:51:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:28  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:58   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:24   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:24:44   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncustomer;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the parent cargo (ReturnCustomerCargo) with
    information from the child cargo (ReturnTransactionCargo).
**/
//--------------------------------------------------------------------------
public class ReturnTransactionReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5559289953946219309L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returncustomer.ReturnTransactionReturnShuttle.class);

    /**
       Return Transaction cargo
    **/
    ReturnTransactionCargo rtCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from child service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // retrieve cargo from the parent service
        rtCargo = (ReturnTransactionCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by parent service.
       <P>
       @param  bus Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // retrieve cargo from the child
        ReturnCustomerCargo cargo = (ReturnCustomerCargo)bus.getCargo();
        // set the return transaction id in the options cargo

        if (rtCargo.getTransferCargo())
        {
            cargo.setTransferCargo(true);
            ReturnData retrunData = cargo.buildReturnData(rtCargo.getPLUItems(), 
                    rtCargo.getReturnSaleLineItems(), rtCargo.getReturnItems());
            retrunData = cargo.addReturnData(cargo.getReturnData(), retrunData);
            cargo.setReturnData(retrunData);
            cargo.setOriginalTransaction(rtCargo.getOriginalTransaction());
            cargo.setOriginalTransactionId(rtCargo.getOriginalTransactionId());
            cargo.setOriginalExternalOrderReturnTransactions(
                    rtCargo.getOriginalExternalOrderReturnTransactions());
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
        String strResult = new String("Class:  ReturnTransactionReturnShuttle (Revision " +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()
}
