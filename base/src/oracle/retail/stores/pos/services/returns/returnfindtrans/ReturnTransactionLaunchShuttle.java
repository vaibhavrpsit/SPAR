/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnfindtrans/ReturnTransactionLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/16/10 - set haveReceipt in the ReturnTransactionCargo
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:56 PM  Robert Pearse   
 *
 *   Revision 1.8  2004/03/16 20:16:36  epd
 *   @scr 3561 fixed bug that sets gift receipt selected for retrieved return items
 *
 *   Revision 1.7  2004/03/04 20:55:36  epd
 *   @scr 3561 removed unused import
 *
 *   Revision 1.6  2004/03/04 20:50:28  baa
 *   @scr 3561 returns add support for units sold
 *
 *   Revision 1.5  2004/02/17 20:40:28  baa
 *   @scr 3561 returns
 *
 *   Revision 1.4  2004/02/13 22:46:22  baa
 *   @scr 3561 Returns - capture tender options on original trans.
 *
 *   Revision 1.3  2004/02/12 16:51:48  mcs
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
 *    Rev 1.1   Dec 19 2003 13:22:36   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.0   Aug 29 2003 16:06:00   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   17 Jan 2002 17:37:06   baa
 * update roles/security model
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.0   Sep 21 2001 11:24:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnfindtrans;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnLaunchShuttle;
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
