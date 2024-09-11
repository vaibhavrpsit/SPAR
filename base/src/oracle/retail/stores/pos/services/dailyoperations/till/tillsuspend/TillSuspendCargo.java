/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/till/tillsuspend/TillSuspendCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:18 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:31 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:15 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:07 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:50:09  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:47:04  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:58:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:25:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:31:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:20:18   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:15:18   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.till.tillsuspend;

// Bedrock imports
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamSnapshot;
import oracle.retail.stores.pos.services.common.TillCargo;


//------------------------------------------------------------------------------
/**
    
     
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class TillSuspendCargo extends TillCargo
//implements CargoIfc, TourCamIfc
{

    //--------------------------------------------------------------------------
    /**
        Create a SnapshotIfc which can subsequently be used to restore
            the cargo to its current state. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The cargo is able to make a snapshot.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>A snapshot is returned which contains enough data to restore the 
            cargo to its current state.
        </UL>
        @return an object which stores the current state of the cargo.
        @see oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc
    */
    //--------------------------------------------------------------------------

    public SnapshotIfc makeSnapshot()
    {
        return new TourCamSnapshot(this);
    }

    
    //--------------------------------------------------------------------------
    /**
        Reset the cargo data using the snapshot passed in. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI>The snapshot represents the state of the cargo, possibly relative
        to the existing state of the cargo.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI>The cargo state has been restored with the contents of the snapshot.
        </UL>
        @param snapshot is the SnapshotIfc which contains the desired state 
            of the cargo.
        @exception ObjectRestoreException is thrown when the cargo cannot
            be restored with this snapshot 
    */
    //--------------------------------------------------------------------------

    public void restoreSnapshot(SnapshotIfc snapshot)
        throws ObjectRestoreException
    {
    }
}
