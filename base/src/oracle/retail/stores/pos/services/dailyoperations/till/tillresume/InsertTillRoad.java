/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillresume/InsertTillRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:21 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:23 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:07:13  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.3  2004/02/12 16:50:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:45:18  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:25:54   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:30:58   msg
 * Initial revision.
 * 
 *    Rev 1.2   03 Dec 2001 16:18:10   epd
 * Update drawer ID to use constant value as defined in DrawerIfc
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.1   12 Nov 2001 09:59:58   epd
 * Makes use of new Drawer object
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * 
 *    Rev 1.0   08 Nov 2001 10:28:28   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillresume;
// foundation imports
import oracle.retail.stores.domain.financial.AbstractStatusEntityIfc;
import oracle.retail.stores.domain.financial.DrawerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;

//------------------------------------------------------------------------------
/**
    Sets the register drawer status to Reserved.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class InsertTillRoad extends LaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2464478183796249936L;

    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       InsertTillRoad
    **/
    //--------------------------------------------------------------------------
    public static final String LANENAME = "InsertTillRoad";

    //--------------------------------------------------------------------------
    /**
       Sets the register drawer status to Reserved.
       <P>
       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        TillResumeCargo cargo = (TillResumeCargo) bus.getCargo();

        // sets the register drawer status to Reserved
        // Assume only one register
        cargo.getRegister()
             .getDrawer(DrawerIfc.DRAWER_PRIMARY)
             .setDrawerStatus(AbstractStatusEntityIfc.DRAWER_STATUS_OCCUPIED, cargo.getTillID());
    }

}
