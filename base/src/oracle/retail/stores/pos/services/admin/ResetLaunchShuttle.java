/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/ResetLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:29:40 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:24:44 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:13:44 PM  Robert Pearse   
 *
 *Revision 1.4  2004/04/09 16:56:00  cdb
 *@scr 4302 Removed double semicolon warnings.
 *
 *Revision 1.3  2004/02/12 16:48:47  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:38:15  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:52:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:36:42   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:06:42   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:18:24   msg
 * Initial revision.
 * 
 *    Rev 1.2   19 Nov 2001 15:09:56   pdd
 * Changed to extend UserAccessCargoLaunchShuttle.
 * Resolution for POS SCR-309: Convert to new Security Override design.
 * 
 *    Rev 1.1   16 Nov 2001 10:36:06   epd
 * Deprecated/removed unused fields and methods
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   Sep 21 2001 11:10:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:05:16   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

// Foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.admin.resethardtotals.ResetHardTotalsCargo;
import oracle.retail.stores.pos.services.admin.security.common.UserAccessCargoLaunchShuttle;

//------------------------------------------------------------------------------
/**
    This shuttle will transfer the register from the AdminCargo to the Shuttle
    and then from the shuttle to the ResetCargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class ResetLaunchShuttle extends UserAccessCargoLaunchShuttle
{
    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.ResetLaunchShuttle.class);
    // Class name
    public static final String SHUTTLENAME = "ResetLaunchShuttle";
    /**
       Store ID
       @deprecated
    **/
    protected String storeID = "";
    /**
       Register ID
       @deprecated
    **/
    protected String registerID = "";
    /**
       Cashier ID
       @deprecated
    **/
    protected String cashierID = "";

    //--------------------------------------------------------------------------
    /**
       Load register from admin cargo

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        super.load(bus);
    }

    //--------------------------------------------------------------------------
    /**
       Unload register from reset hard totals cargo

       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        super.unload(bus);
        ResetHardTotalsCargo cargo = (ResetHardTotalsCargo) bus.getCargo();
        cargo.setManualReset(true);
    }
}
