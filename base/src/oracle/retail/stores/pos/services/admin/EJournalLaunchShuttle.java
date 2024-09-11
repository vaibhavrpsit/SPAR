/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/admin/EJournalLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:07 mszekely Exp $
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
 * 3    360Commerce 1.2         3/31/2005 4:27:54 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:13 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:46 PM  Robert Pearse   
 *
 *Revision 1.5  2004/09/23 00:07:14  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
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
 *    Rev 1.1   Dec 24 2003 06:31:04   rrn
 * Added businessDate.
 * Resolution for 3646: EJournal - default search date should be business date not system date
 * 
 *    Rev 1.0   Dec 17 2003 09:16:32   rrn
 * Initial revision.
 * Resolution for 3611: EJournal to database
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.admin;

// Foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.admin.ejournal.EJournalCargo;

//------------------------------------------------------------------------------
/**
    This shuttle will transfer the registerID from the AdminCargo to the Shuttle
    and then from the shuttle to the EJournalCargo.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class EJournalLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -4274046550753842473L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.admin.EJournalLaunchShuttle.class);
    // Class name
    public static final String SHUTTLENAME = "EJournalLaunchShuttle";
    // source cargo
    protected AdminCargo aCargo = null;

    //--------------------------------------------------------------------------
    /**
       Load the shuttle.

       @param bus the bus being loaded
    **/
    //--------------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        aCargo = (AdminCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
       Unload the shuttle.

       @param bus the bus being unloaded
    **/
    //--------------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        EJournalCargo cargo = (EJournalCargo) bus.getCargo();
        cargo.setRegisterID(aCargo.getRegister().getWorkstation().getWorkstationID());
        cargo.setOperator(aCargo.getOperator());
        cargo.setBusinessDate(aCargo.getStoreStatus().getBusinessDate());
    }
}
