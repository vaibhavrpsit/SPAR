/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/register/registerclose/TillCloseReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:29 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:04 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:51  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:46:34  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:57:08   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:30:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:15:04   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:27:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:17:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:11:58   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.register.registerclose;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.dailyoperations.till.tillclose.TillCloseCargo;

//------------------------------------------------------------------------------
/**
    Transfers data from TillClose service to TillOptions service. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class TillCloseReturnShuttle implements ShuttleIfc
{                                       // begin class TillCloseReturnShuttle
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2611306503480923999L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.dailyoperations.register.registerclose.TillCloseReturnShuttle.class);

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       shuttle name constant
    **/
    public static final String SHUTTLENAME = "TillCloseReturnShuttle";

    /**
       store financial status
    **/
    //protected StoreStatusIfc storeStatus;

    /**
       register financial status
    **/
    protected RegisterIfc tillCloseRegister;

    //--------------------------------------------------------------------------
    /**
       Loads data from the TillClose service. <P>
       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {                                   // begin load()

        // get store status, register from cargo
        TillCloseCargo cargo = (TillCloseCargo) bus.getCargo();
        tillCloseRegister = cargo.getRegister();
        //storeStatus = cargo.getStoreStatus();

    }                                   // end load()

    //--------------------------------------------------------------------------
    /**
       Loads data to the TillOptions service. <P>
       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {                                   // begin unload()

        // get store status, set register from cargo
        RegisterCloseCargo cargo = (RegisterCloseCargo) bus.getCargo();

        //cargo.setStoreStatus(storeStatus);

        cargo.setTillCloseRegister(tillCloseRegister); // set till close register clone

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

}                                       // end class TillCloseReturnShuttle
