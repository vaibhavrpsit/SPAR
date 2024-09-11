/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/ReturnFindTransReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:54 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     07/16/10 - Modifications to support the escape/undo
 *                         functionality on the ReturnItemInformation screen in
 *                         the retrieved transaction context.
 *    jswan     07/14/10 - Modifications to support pressing the escape key in
 *                         the EnterItemInformation screen during retrieved
 *                         transaction screen for external order integration.
 *    jswan     06/30/10 - Checkin for first promotion of External Order
 *                         integration.
 *    jswan     06/17/10 - Checkin external order integration files for
 *                         refresh.
 *    jswan     06/01/10 - Checked in for refresh to latest lable.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     05/14/10 - ExternalOrder mods checkin for refresh to tip.
 *    abondala  01/03/10 - update header date
 *    vikini    02/04/09 - Setting Geocode for Obtaining Tax Rules
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:53 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/02/17 20:40:28  baa
 *   @scr 3561 returns
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
 *    Rev 1.1   08 Nov 2003 01:42:50   baa
 * cleanup -sale refactoring
 * 
 *    Rev 1.0   Aug 29 2003 16:06:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:28   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:25:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the current service with the information
    from the Find Transaction by ID service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReturnFindTransReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5161531796358057026L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.returns.returnoptions.ReturnFindTransReturnShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       Child cargo.
    **/
    protected ReturnFindTransCargo rftCargo = null;

    //----------------------------------------------------------------------
    /**
       Copies information needed from child service.
       <P>
       @param  bus    Child Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {

        rftCargo = (ReturnFindTransCargo)bus.getCargo();

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

        if (rftCargo.getTransferCargo())
        {
            cargo.setReturnData(cargo.addReturnData(cargo.getReturnData(), rftCargo.getReturnData()));
            cargo.setOriginalTransaction(rftCargo.getOriginalTransaction());
            cargo.setOriginalTransactionId(rftCargo.getOriginalTransactionId());
            cargo.setHaveReceipt(rftCargo.haveReceipt());
            cargo.setGiftReceiptSelected(rftCargo.isGiftReceiptSelected());
            
            //Have to set the GeoCode for obtaining tax rules
            if(cargo.getStoreStatus() != null &&
                    cargo.getStoreStatus().getStore() != null )
            {
                cargo.setGeoCode(cargo.getStoreStatus().getStore().getGeoCode());
                cargo.setStoreID(cargo.getStoreStatus().getStore().getStoreID());
            }
        }

        cargo.resetExternalOrderItemsSelectForReturn();
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
        String strResult = new String("Class:  ReturnFindTransReturnShuttle (Revision " +
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
