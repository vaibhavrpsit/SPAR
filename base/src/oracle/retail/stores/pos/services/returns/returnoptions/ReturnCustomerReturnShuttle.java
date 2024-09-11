/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ReturnCustomerReturnShuttle.java /main/13 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:52 PM  Robert Pearse   
 *
 *   Revision 1.7  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.6  2004/08/06 18:25:53  dcobb
 *   @scr 6655 Letters being checked in shuttle classes.
 *   Added check for Letter or letterName != null.
 *
 *   Revision 1.5  2004/07/26 16:13:57  epd
 *   @scr 6247, 6248 Fixed Undo destination
 *
 *   Revision 1.4  2004/02/16 13:36:40  baa
 *   @scr  3561 returns enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   08 Nov 2003 01:42:44   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:06:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   27 Mar 2002 16:13:18   baa
 * add Entering/Exiting Custmer msg to journal
 * Resolution for POS SCR-648: Customer Find not journaling Entering and Exiting Customer during MBC
 *
 *    Rev 1.0   Mar 18 2002 11:46:26   msg
 * Initial revision.
 *
 *    Rev 1.1   12 Dec 2001 15:36:08   jbp
 * Shuttle back db error from customer and go to return item if there is a db error.
 * Resolution for POS SCR-129: Enhanced Return Searches all return to wrong screen offline
 *
 *    Rev 1.0   Sep 21 2001 11:25:24   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnData;
import oracle.retail.stores.pos.services.returns.returncustomer.ReturnCustomerCargo;
//--------------------------------------------------------------------------
/**
    This shuttle updates the Return Options service with the information
    from the CustomerIfc Return service.
**/
//--------------------------------------------------------------------------
public class ReturnCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8537210605883674522L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnoptions.ReturnCustomerReturnShuttle.class);

    /**
       Child cargo.
    **/
    protected ReturnCustomerCargo rcCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from child service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        rcCargo = (ReturnCustomerCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Stores information needed by parent service.
       <P>
       @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();

        if (rcCargo.getTransferCargo())
        {
            ReturnData returnData = cargo.addReturnData(cargo.getReturnData(), 
                    rcCargo.getReturnData());
            cargo.setReturnData(returnData);
            cargo.setOriginalTransaction(rcCargo.getOriginalTransaction());
            cargo.setOriginalTransactionId(rcCargo.getOriginalTransactionId());
            cargo.setCustomer(rcCargo.getCustomer());
            cargo.setOriginalExternalOrderReturnTransactions(
                    rcCargo.getOriginalExternalOrderReturnTransactions());
        }
        String transactionID = null;
        if (rcCargo.getTransaction() != null)
        {
            transactionID = rcCargo.getTransaction().getTransactionID();
        }
        // set the search criteria to null after the search is done
        LetterIfc letter = bus.getCurrentLetter();
        if (letter != null)
        {
            String letterName = letter.getName();
            if (letterName != null)
            {
                if (!letterName.equals(CommonLetterIfc.UNDO))
                {
                    cargo.setSearchCriteria(null);
                }
            }
        }
        cargo.setDataExceptionErrorCode(rcCargo.getDataExceptionErrorCode());
        // journal exit from customer package
        CustomerUtilities.journalCustomerExit(bus, rcCargo.getOperator().getEmployeeID(),
                                             transactionID);
        cargo.resetExternalOrderItemsSelectForReturn();
    }
}
