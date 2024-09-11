/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindorder/ReturnTransactionLaunchShuttle.java /main/1 2012/10/29 12:55:21 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/25/12 - Added to support returns by order.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindorder;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnLaunchShuttle;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;

//--------------------------------------------------------------------------
/**
    This shuttle sets up the Return Transaction service.
**/
//--------------------------------------------------------------------------
public class ReturnTransactionLaunchShuttle extends AbstractReturnLaunchShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = 8462974428417281986L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnTransactionLaunchShuttle.class);

    /**
       Parent cargo
    **/
    ReturnFindTransCargo rftCargo = null;

    //----------------------------------------------------------------------
    /**
       Store data from parent service in the shuttle
       <P>
       @param  bus     Parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // Perform FinancialCargoShuttle load
        super.load(bus);

        // retrieve cargo from the parent
        rftCargo = (ReturnFindTransCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Transfer parent data to child cargo.
       <P>
       @param  bus     Child Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // Perform FinancialCargoShuttle unload
        super.unload(bus);

        ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();
        cargo.setSearchCriteria(rftCargo.getSearchCriteria());
        cargo.setTransaction(rftCargo.getTransaction());
        cargo.setOriginalTransaction(rftCargo.getOriginalTransaction());
        cargo.setOriginalTransactionId(rftCargo.getOriginalTransactionId());
        cargo.setGiftReceiptSelected(rftCargo.isGiftReceiptSelected());
        cargo.setHaveReceipt(rftCargo.haveReceipt());
        cargo.setOriginalExternalOrderReturnTransactions(
                rftCargo.getOriginalExternalOrderReturnTransactions());
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
        String strResult = new String("Class:  ReturnTransactionLaunchShuttle (Revision " +
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
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
