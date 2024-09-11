/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tilloptions/TillLoanReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:19 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:30 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:12 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:05 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:02  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:27:12   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:00   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:19:12   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:14:46   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tilloptions;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillloan.TillLoanCargo;

//------------------------------------------------------------------------------
/**
    Transfers data from TillLoan service to TillOptions service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillLoanReturnShuttle implements ShuttleIfc
{                                       // begin class TillLoanReturnShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2461562595115343716L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.till.tilloptions.TillLoanReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       shuttle name constant
    **/
    public static final String SHUTTLENAME = "TillLoanReturnShuttle";

    /**
       store financial status
    **/
    protected StoreStatusIfc storeStatus;

    /**
       register financial status
    **/
    protected RegisterIfc register;
    /**
       identifier of last reprintable transaction
    **/
    protected String lastReprintableTransactionID = "";

    //--------------------------------------------------------------------------
    /**
       Loads data from the TillLoan service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()

        // get store status, register from cargo
        TillLoanCargo cargo = (TillLoanCargo) bus.getCargo();
        register = cargo.getRegister();
        storeStatus = cargo.getStoreStatus();
        lastReprintableTransactionID = cargo.getLastReprintableTransactionID();

    }                                   // end load()

    //--------------------------------------------------------------------------
    /**
       Loads data to the TillOptions service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()

        // get store status, register from cargo
        TillOptionsCargo cargo = (TillOptionsCargo) bus.getCargo();
        cargo.setRegister(register);
        cargo.setStoreStatus(storeStatus);
        cargo.setLastReprintableTransactionID(lastReprintableTransactionID);

    }                                   // end unload()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}                                       // end class TillLoanReturnShuttle
