/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/specialorder/deposit/TenderReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:02 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:56 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:52:03  mcs
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
 *    Rev 1.3   Nov 04 2003 11:22:12   epd
 * Updates for repackaging
 * 
 *    Rev 1.2   Oct 23 2003 17:24:58   epd
 * Updated to use renamed ADO packages
 * 
 *    Rev 1.1   Oct 17 2003 13:05:48   epd
 * Updated for new ADO tender service
 * 
 *    Rev 1.0   Aug 29 2003 16:07:24   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:01:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:48:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Dec 04 2001 16:09:00   dfh
 * No change.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 04 2001 15:11:22   dfh
 * Initial revision.
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.specialorder.deposit;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.services.common.FinancialCargoShuttle;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.transaction.OrderTransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.specialorder.SpecialOrderCargo;

//--------------------------------------------------------------------------
/**
    This shuttle copies information from the tender cargo used
    in the Tender service to the cargo used in the POS service. <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class TenderReturnShuttle 
extends FinancialCargoShuttle
implements ShuttleIfc
{                                       // begin class TenderReturnShuttle()
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 808858133348676230L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.specialorder.deposit.TenderReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       tender cargo reference
    **/    
    protected TenderCargo cargo = null;
   
    /**
       Not opening the cash drawer, use this to make sure the user has time to
       press Enter before leaving.
    **/
   // protected boolean waitForNext = false;

    //----------------------------------------------------------------------
    /**
       Loads cargo from tender service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()
        cargo = (TenderCargo) bus.getCargo();
    }                                   // end load()

    //----------------------------------------------------------------------
    /**
       Loads cargo for special order payment service. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()
        SpecialOrderCargo Scargo = (SpecialOrderCargo) bus.getCargo();
        OrderTransactionIfc txnRDO = (OrderTransactionIfc)((ADO)cargo.getCurrentTransactionADO()).toLegacy();
        Scargo.setOrderTransaction(txnRDO);
        Scargo.setStoreStatus(cargo.getStoreStatus());
        Scargo.setRegister(cargo.getRegister());
    }                                   // end unload()

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
                                      ") @" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

}                                       // end class TenderReturnShuttle
