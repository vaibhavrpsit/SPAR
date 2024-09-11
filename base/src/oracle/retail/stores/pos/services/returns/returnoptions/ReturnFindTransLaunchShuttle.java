/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ReturnFindTransLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/11/10 - Pre code reveiw clean up.
 *    jswan     05/11/10 - Returns flow refactor: added transfer of
 *                         transactionID to unload method.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/03/15 15:16:52  baa
 *   @scr 3561 refactor/clean item size code, search by tender changes
 *
 *   Revision 1.5  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.4  2004/02/16 13:36:40  baa
 *   @scr  3561 returns enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Dec 17 2003 11:20:54   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.1   08 Nov 2003 01:42:48   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:06:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnLaunchShuttle;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This shuttle updates the Find Return Transaction service with the information
    from the Return Options service.
**/
//--------------------------------------------------------------------------
public class ReturnFindTransLaunchShuttle extends AbstractReturnLaunchShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = 7164898811131433022L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnoptions.ReturnFindTransLaunchShuttle.class);

    /**
       Parent cargo
    **/
    ReturnOptionsCargo roCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information from the parent service.
       <P>
       @param  bus    Parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        // Perform FinancialCargoShuttle load
        super.load(bus);

        // retrieve cargo from the parent
        roCargo = (ReturnOptionsCargo)bus.getCargo();

    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by child service.
       <P>
       @param  bus     Child Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        // Perform FinancialCargoShuttle unload
        super.unload(bus);

        // Set data in the child cargo
        ReturnFindTransCargo cargo = (ReturnFindTransCargo)bus.getCargo();
        cargo.setTransaction(roCargo.getTransaction());
        cargo.setSearchCriteria(roCargo.getSearchCriteria());
        if (roCargo.getTransaction() != null)
        {
            cargo.setCustomer(roCargo.getTransaction().getCustomer());
        }
        cargo.setOriginalReturnTransactions(roCargo.getOriginalReturnTransactions());
        cargo.setHaveReceipt(roCargo.haveReceipt());
        cargo.setGiftReceiptSelected(roCargo.isGiftReceiptSelected());
        cargo.setSearchByTender(roCargo.isSearchByTender());
        cargo.setOriginalTransactionId(roCargo.getOriginalTransactionId());
        cargo.setOriginalExternalOrderReturnTransactions(
                roCargo.getOriginalExternalOrderReturnTransactions());
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
        String strResult = new String("Class:  ReturnFindTransLaunchShuttle (Revision " +
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
