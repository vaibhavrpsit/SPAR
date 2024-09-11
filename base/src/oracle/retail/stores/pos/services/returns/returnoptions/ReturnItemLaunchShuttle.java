/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ReturnItemLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/07/10 - Code review changes and fixes for Cancel button in
 *                         External Order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     12/14/09 - Modifications for 'Min return price for X days'
 *                         feature.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/27/2008 7:37:28 PM   Anil Rathore
 *         Updated to display ITEM_NOT_FOUND dialog. Changes reviewed by Dan.
 *    4    360Commerce 1.3         3/10/2008 3:51:48 PM   Sandy Gu
 *         Specify store id for non receipted return item query.
 *    3    360Commerce 1.2         3/31/2005 4:29:45 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:51 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse   
 *
 *   Revision 1.13  2004/07/22 23:08:56  blj
 *   @scr 6258 - changed the flow so that if UNDO is pressed, we dont lookup the item again we use the information previously entered.
 *
 *   Revision 1.12  2004/06/07 19:59:00  mkp1
 *   @scr 2775 Put correct header on files
 *
 *   Revision 1.11  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *   Revision 1.10  2004/03/10 14:16:46  baa
 *   @scr 0 fix javadoc warnings
 *
 *   Revision 1.9  2004/03/02 18:49:54  baa
 *   @scr 3561 Returns add size info to journal and receipt
 *
 *   Revision 1.8  2004/02/27 22:43:50  baa
 *   @scr 3561 returns add trans not found flow
 *
 *   Revision 1.7  2004/02/23 13:54:52  baa
 *   @scr 3561 Return Enhancements to support item size
 *
 *   Revision 1.6  2004/02/19 15:37:31  baa
 *   @scr 3561 returns
 *
 *   Revision 1.5  2004/02/18 20:36:20  baa
 *   @scr 3561 Returns changes to support size
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
 *    Rev 1.3   Dec 19 2003 13:22:56   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.2   Dec 17 2003 11:20:56   baa
 * return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 * 
 *    Rev 1.1   08 Nov 2003 01:42:54   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:06:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:28   msg
 * Initial revision.
 * 
 *    Rev 1.2   17 Jan 2002 17:37:32   baa
 * update roles/security model
 * Resolution for POS SCR-714: Roles/Security 5.0 Updates
 *
 *    Rev 1.1   Nov 27 2001 18:06:22   blj
 * Updated to allow return by gift receipt
 * Resolution for POS SCR-237: Gift Receipt Feature
 *
 *    Rev 1.0   Sep 21 2001 11:25:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:12:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.returns.returncommon.AbstractReturnLaunchShuttle;
import oracle.retail.stores.pos.services.returns.returnitem.ReturnItemCargo;

import org.apache.log4j.Logger;

//--------------------------------------------------------------------------
/**
    This shuttle sets up the Return Item service.
**/
//--------------------------------------------------------------------------
public class ReturnItemLaunchShuttle extends AbstractReturnLaunchShuttle
{
    /** serialVersionUID */
    private static final long serialVersionUID = -801252096344442463L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnoptions.ReturnItemLaunchShuttle.class);

    /**
       Parent Cargo
    **/
    protected ReturnOptionsCargo roCargo;

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
        roCargo = (ReturnOptionsCargo)bus.getCargo();

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

        // retrieve cargo from the child(ItemReturn Cargo)
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        cargo.setOriginalTransaction(roCargo.getOriginalTransaction());
        cargo.setOriginalTransactionId(roCargo.getOriginalTransactionId());
        cargo.setHaveReceipt(roCargo.haveReceipt());
        cargo.setGiftReceiptSelected(roCargo.isGiftReceiptSelected());
        cargo.setAccessFunctionID(RoleFunctionIfc.OVERRIDE_RESTOCKING_FEE);
        cargo.setPLUItemID(roCargo.getPLUItemID());
        cargo.setItemScanned(roCargo.isItemScanned());
        cargo.setResourceID("RestockingFeeSecurityError");
        cargo.setSearchCriteria(roCargo.getSearchCriteria());
        cargo.setTransactionFound(roCargo.isTransactionFound());
        cargo.setGeoCode(roCargo.getGeoCode());
        cargo.setStoreID(roCargo.getStoreID());
        cargo.setMaxPLUItemIDLength(roCargo.getMaxPLUItemIDLength());
        cargo.setTransaction(roCargo.getTransaction());
        // If there is returndata already in the case of an undo, update cargo
        cargo.setReturnData(roCargo.getReturnData());
        if (roCargo.getReturnData() != null)
        {
            cargo.setReturnSaleLineItems(roCargo.getReturnData().getSaleReturnLineItems());
            cargo.setReturnItems(roCargo.getReturnData().getReturnItems());
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
        String strResult = new String("Class:  ReturnItemLaunchShuttle (Revision " +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
    }                                   // end toString()
}
