/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncustomer/ReturnTransactionLaunchShuttle.java /main/16 2013/07/17 15:36:15 rgour Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rgour     07/17/13 - Setting the originalTransactionID for cargo
 *    jswan     07/16/10 - Code review changes.
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *    acadar    11/18/08 - forward port for BUG: 7578830
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.3  2004/02/12 16:51:47  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
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
 *    Rev 1.0   Apr 29 2002 15:06:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:45:34   msg
 * Initial revision.
 *
 *    Rev 1.1   17 Jan 2002 17:37:00   baa
 * update roles/security model
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.0   Sep 21 2001 11:24:46   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:32   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncustomer;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnLaunchShuttle;
import oracle.retail.stores.pos.services.returns.returntransaction.ReturnTransactionCargo;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This shuttle updates the child cargo (ReturnTransactionCargo) with
    information from the parent cargo (ReturnCustomerCargo).
    <p>
**/
//--------------------------------------------------------------------------
public class ReturnTransactionLaunchShuttle extends AbstractReturnLaunchShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = 2344486438058984125L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returncustomer.ReturnTransactionLaunchShuttle.class);

    /**
       Return CustomerIfc cargo
    **/
    ReturnCustomerCargo rcCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from the parent service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // Perform FinancialCargoShuttle load
        super.load(bus);

        // retrieve cargo from the parent service
        rcCargo = (ReturnCustomerCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by child service.
       <P>
       @param  bus   Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // Perform FinancialCargoShuttle unload
        super.unload(bus);

        // retrieve cargo from the child
        ReturnTransactionCargo cargo = (ReturnTransactionCargo)bus.getCargo();
        // set the return transaction id in the options cargo

        cargo.setOriginalTransaction(rcCargo.getOriginalTransaction());
        cargo.setOriginalTransactionId(rcCargo.getOriginalTransactionId());        
        cargo.setOriginalExternalOrderReturnTransactions(
                rcCargo.getOriginalExternalOrderReturnTransactions());
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
