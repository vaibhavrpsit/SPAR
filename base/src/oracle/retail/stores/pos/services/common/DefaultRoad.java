/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/DefaultRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:52 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:32 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:54:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:36:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:56   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:14:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:06:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.foundation.tour.application.tourcam.ObjectRestoreException;
import oracle.retail.stores.foundation.tour.application.tourcam.SnapshotIfc;
import oracle.retail.stores.foundation.tour.application.tourcam.TourCamIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    Provides default restore of snapshot during backup
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class DefaultRoad extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Restores the snapshot, if appropriate
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void backup(BusIfc bus, SnapshotIfc snapShot)
    {

        // retrieve cargo
        CargoIfc cargo = (CargoIfc)bus.getCargo();
        // check to see if cargo implements the tourcam interface
        if (cargo instanceof TourCamIfc)
        {
            // cast to tour cam
            TourCamIfc tourCamCargo = (TourCamIfc)cargo;
            try
            {
                // restore the snap shot(cargo) on backup
                tourCamCargo.restoreSnapshot(snapShot);
            }
            catch (ObjectRestoreException e)
            {
                logger.error( "Can't restore snapshot.");
                logger.error( "" + e + "");
            }
        }

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  getClass().getName() (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
